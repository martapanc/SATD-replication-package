diff --git a/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
index d711e5c1bd..ca53d8010d 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/SessionFactoryBuilder.java
@@ -1,668 +1,669 @@
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
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
-import org.hibernate.hql.spi.QueryTranslatorFactory;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * The contract for building a {@link org.hibernate.SessionFactory} given a number of options.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  *
  * @since 5.0
  */
 public interface SessionFactoryBuilder {
 	/**
 	 * Apply a Bean Validation ValidatorFactory to the SessionFactory being built.
 	 *
 	 * NOTE : De-typed to avoid hard dependency on Bean Validation jar at runtime.
 	 *
 	 * @param validatorFactory The Bean Validation ValidatorFactory to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory);
 
 	/**
 	 * Apply a CDI BeanManager to the SessionFactory being built.
 	 *
 	 * NOTE : De-typed to avoid hard dependency on CDI jar at runtime.
 	 *
 	 * @param beanManager The CDI BeanManager to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder applyBeanManager(Object beanManager);
 
 	/**
 	 * Applies a SessionFactory name.
 	 *
 	 * @param sessionFactoryName The name to use for the SessionFactory being built
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#SESSION_FACTORY_NAME
 	 */
 	public SessionFactoryBuilder applyName(String sessionFactoryName);
 
 	/**
 	 * Applies a SessionFactory name.
 	 *
 	 * @param isJndiName {@code true} indicates that the name specified in
 	 * {@link #applyName} will be used for binding the SessionFactory into JNDI.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#SESSION_FACTORY_NAME_IS_JNDI
 	 */
 	public SessionFactoryBuilder applyNameAsJndiName(boolean isJndiName);
 
 	/**
 	 * Applies whether Sessions should be automatically closed at the end of the transaction.
 	 *
 	 * @param enabled {@code true} indicates they should be auto-closed; {@code false} indicates not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#AUTO_CLOSE_SESSION
 	 */
 	public SessionFactoryBuilder applyAutoClosing(boolean enabled);
 
 	/**
 	 * Applies whether Sessions should be automatically flushed at the end of the transaction.
 	 *
 	 * @param enabled {@code true} indicates they should be auto-flushed; {@code false} indicates not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#FLUSH_BEFORE_COMPLETION
 	 */
 	public SessionFactoryBuilder applyAutoFlushing(boolean enabled);
 
 	/**
 	 * Applies whether statistics gathering is enabled.
 	 *
 	 * @param enabled {@code true} indicates that statistics gathering should be enabled; {@code false} indicates not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#GENERATE_STATISTICS
 	 */
 	public SessionFactoryBuilder applyStatisticsSupport(boolean enabled);
 
 	/**
 	 * Names an interceptor to be applied to the SessionFactory, which in turn means it will be used by all
 	 * Sessions unless one is explicitly specified in {@link org.hibernate.SessionBuilder#interceptor}
 	 *
 	 * @param interceptor The interceptor
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#INTERCEPTOR
 	 */
 	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor);
 
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
 	 * Specifies a custom entity dirtiness strategy to be applied to the SessionFactory.  See the contract
 	 * of {@link org.hibernate.CustomEntityDirtinessStrategy} for details.
 	 *
 	 * @param strategy The custom strategy to be used.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#CUSTOM_ENTITY_DIRTINESS_STRATEGY
 	 */
 	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy);
 
 	/**
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
 	 * Should generated identifiers be "unset" on entities during a rollback?
 	 *
 	 * @param enabled {@code true} indicates identifiers should be unset; {@code false} indicates not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_IDENTIFIER_ROLLBACK
 	 */
 	public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean enabled);
 
 	/**
 	 * Applies the given entity mode as the default for the SessionFactory.
 	 *
 	 * @param entityMode The default entity mode to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_ENTITY_MODE
 	 *
 	 * @deprecated Different entity modes per entity is soon to be removed as a feature.
 	 */
 	@Deprecated
 	public SessionFactoryBuilder applyDefaultEntityMode(EntityMode entityMode);
 
 	/**
 	 * Should attributes using columns marked as not-null be checked (by Hibernate) for nullness?
 	 *
 	 * @param enabled {@code true} indicates that Hibernate should perform nullness checking; {@code false} indicates
 	 * it should not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#CHECK_NULLABILITY
 	 */
 	public SessionFactoryBuilder applyNullabilityChecking(boolean enabled);
 
 	/**
 	 * Should the application be allowed to initialize uninitialized lazy state outside the bounds of a transaction?
 	 *
 	 * @param enabled {@code true} indicates initialization outside the transaction should be allowed; {@code false}
 	 * indicates it should not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#ENABLE_LAZY_LOAD_NO_TRANS
 	 */
 	public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean enabled);
 
 	/**
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
 	 * How should updates and deletes that span multiple tables be handled?
 	 *
 	 * @param strategy The strategy for handling multi-table updates and deletes.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#HQL_BULK_ID_STRATEGY
 	 */
 	public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy);
 
+	public SessionFactoryBuilder applyTempTableDdlTransactionHandling(TempTableDdlTransactionHandling handling);
+
 	/**
 	 * What style of batching should be used?
 	 *
 	 * @param style The style to use
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#BATCH_FETCH_STYLE
 	 */
 	public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle style);
 
 	/**
 	 * Allows specifying a default batch-fetch size for all entities and collections
 	 * which do not otherwise specify a batch-fetch size.
 	 *
 	 * @param size The size to use for batch fetching for entities/collections which
 	 * do not specify an explicit batch fetch size.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_BATCH_FETCH_SIZE
 	 */
 	public SessionFactoryBuilder applyDefaultBatchFetchSize(int size);
 
 	/**
 	 * Apply a limit to the depth Hibernate will use for outer joins.  Note that this is different than an
 	 * overall limit on the number of joins...
 	 *
 	 * @param depth The depth for limiting joins.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#MAX_FETCH_DEPTH
 	 */
 	public SessionFactoryBuilder applyMaximumFetchDepth(int depth);
 
 	/**
 	 * Apply a null precedence (NULLS FIRST, NULLS LAST) to be applied order-by clauses rendered into
 	 * SQL queries.
 	 *
 	 * @param nullPrecedence The default null precedence to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#DEFAULT_NULL_ORDERING
 	 */
 	public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence nullPrecedence);
 
 	/**
 	 * Apply whether ordering of inserts should be enabled.  This allows more efficient SQL
 	 * generation via the use of batching for the inserts; the cost is that the determination of the
 	 * ordering is far more inefficient than not ordering.
 	 *
 	 * @param enabled {@code true} indicates that ordering should be enabled; {@code false} indicates not
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#ORDER_INSERTS
 	 */
 	public SessionFactoryBuilder applyOrderingOfInserts(boolean enabled);
 
 	/**
 	 * Apply whether ordering of updates should be enabled.  This allows more efficient SQL
 	 * generation via the use of batching for the updates; the cost is that the determination of the
 	 * ordering is far more inefficient than not ordering.
 	 *
 	 * @param enabled {@code true} indicates that ordering should be enabled; {@code false} indicates not
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#ORDER_UPDATES
 	 */
 	public SessionFactoryBuilder applyOrderingOfUpdates(boolean enabled);
 
 	/**
 	 * Apply the form of multi-tenancy used by the application
 	 *
 	 * @param strategy The form of multi-tenancy in use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#MULTI_TENANT
 	 */
 	public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy strategy);
 
 	/**
 	 * Specifies a strategy for resolving the notion of a "current" tenant-identifier when using multi-tenancy
 	 * together with current sessions
 	 *
 	 * @param resolver The resolution strategy to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#MULTI_TENANT_IDENTIFIER_RESOLVER
 	 */
 	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver);
 
 	/**
 	 * Should we track JTA transactions to attempt to detect timeouts?
 	 *
 	 * @param enabled {@code true} indicates we should track by thread; {@code false} indicates not
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#JTA_TRACK_BY_THREAD
 	 *
 	 * @deprecated This should be replaced by new TransactionCoordinator work...
 	 */
 	@Deprecated
 	public SessionFactoryBuilder applyJtaTrackingByThread(boolean enabled);
 
 	/**
 	 * Apply query substitutions to use in HQL queries.  Note, this is a legacy feature and almost always
 	 * never needed anymore...
 	 *
 	 * @param substitutions The substitution map
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#QUERY_SUBSTITUTIONS
 	 *
 	 * @deprecated This is a legacy feature and should never be needed anymore...
 	 */
 	@Deprecated
 	public SessionFactoryBuilder applyQuerySubstitutions(Map substitutions);
 
 	/**
 	 * Should we strictly adhere to JPA Query Language (JPQL) syntax, or more broadly support
 	 * all of Hibernate's superset (HQL)?
 	 * <p/>
 	 * Setting this to {@code true} may cause valid HQL to throw an exception because it violates
 	 * the JPQL subset.
 	 *
 	 * @param enabled {@code true} indicates that we should strictly adhere to the JPQL subset; {@code false}
 	 * indicates we should accept the broader HQL syntax.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#JPAQL_STRICT_COMPLIANCE
 	 */
 	public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean enabled);
 
 	/**
 	 * Should named queries be checked on startup?
 	 *
 	 * @param enabled {@code true} indicates that they should; {@code false} indicates they should not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#QUERY_STARTUP_CHECKING
 	 */
 	public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean enabled);
 
 	/**
 	 * Should second level caching support be enabled?
 	 *
 	 * @param enabled {@code true} indicates we should enable the use of second level caching; {@code false}
 	 * indicates we should disable the use of second level caching.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_SECOND_LEVEL_CACHE
 	 */
 	public SessionFactoryBuilder applySecondLevelCacheSupport(boolean enabled);
 
 	/**
 	 * Should second level query caching support be enabled?
 	 *
 	 * @param enabled {@code true} indicates we should enable the use of second level query
 	 * caching; {@code false} indicates we should disable the use of second level query caching.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_QUERY_CACHE
 	 */
 	public SessionFactoryBuilder applyQueryCacheSupport(boolean enabled);
 
 	/**
 	 * Specifies a QueryCacheFactory to use for building query cache handlers.
 	 *
 	 * @param factory The QueryCacheFactory to use
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#QUERY_CACHE_FACTORY
 	 */
 	public SessionFactoryBuilder applyQueryCacheFactory(QueryCacheFactory factory);
 
 	/**
 	 * Apply a prefix to prepended to all cache region names for this SessionFactory.
 	 *
 	 * @param prefix The prefix.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#CACHE_REGION_PREFIX
 	 */
 	public SessionFactoryBuilder applyCacheRegionPrefix(String prefix);
 
 	/**
 	 * By default, Hibernate will always just push data into the cache without first checking
 	 * if that data already exists.  For some caches (mainly distributed caches) this can have a
 	 * major adverse performance impact.  For these caches, it is best to enable this "minimal puts"
 	 * feature.
 	 * <p/>
 	 * Cache integrations also report whether "minimal puts" should be enabled by default.  So its is
 	 * very rare that users need to set this, generally speaking.
 	 *
 	 * @param enabled {@code true} indicates Hibernate should first check whether data exists and only
 	 * push to the cache if it does not already exist. {@code false} indicates to perform the default
 	 * behavior.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_MINIMAL_PUTS
 	 * @see org.hibernate.cache.spi.RegionFactory#isMinimalPutsEnabledByDefault()
 	 */
 	public SessionFactoryBuilder applyMinimalPutsForCaching(boolean enabled);
 
 	/**
 	 * By default, Hibernate stores data in the cache in its own optimized format.  However,
 	 * that format is impossible to "read" if browsing the cache.  The use of "structured" cache
 	 * entries allows the cached data to be read.
 	 *
 	 * @param enabled {@code true} indicates that structured cache entries (human readable) should be used;
 	 * {@code false} indicates that the native entry structure should be used.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_STRUCTURED_CACHE
 	 */
 	public SessionFactoryBuilder applyStructuredCacheEntries(boolean enabled);
 
 	/**
 	 * Generally, Hibernate will extract the information from an entity and put that
 	 * extracted information into the second-level cache.  This is by far the safest way to
 	 * second-level cache persistent data.  However, there are some cases where it is safe
 	 * to cache the entity instance directly.  This setting controls whether that is used
 	 * in those cases.
 	 *
 	 * @param enabled {@code true} indicates that applicable entities will be stored into the
 	 * second-level cache directly by reference; false indicates that all entities will be stored
 	 * via the extraction approach.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_DIRECT_REFERENCE_CACHE_ENTRIES
 	 */
 	public SessionFactoryBuilder applyDirectReferenceCaching(boolean enabled);
 
 	/**
 	 * When using bi-directional many-to-one associations and caching the one-to-many side
 	 * it is expected that both sides of the association are managed (actually that is true of
 	 * all bi-directional associations).  However, in this case, if the user forgets to manage the
 	 * one-to-many side stale data can be left in the second-level cache.
 	 * <p/>
 	 * Warning: enabling this will have a performance impact.  Hence why it is disabled by default
 	 * (for good citizens) and is an opt-in setting.
 	 *
 	 * @param enabled {@code true} indicates that these collection caches should be evicted automatically.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#AUTO_EVICT_COLLECTION_CACHE
 	 */
 	public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean enabled);
 
 	/**
 	 * Specifies the maximum number of statements to batch together in a JDBC batch for
 	 * insert, update and delete operations.  A non-zero number enables batching, but really
 	 * only a number greater than zero will have any effect.  If used, a number great than 5
 	 * is suggested.
 	 *
 	 * @param size The batch size to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#STATEMENT_BATCH_SIZE
 	 */
 	public SessionFactoryBuilder applyJdbcBatchSize(int size);
 
 	/**
 	 * This setting controls whether versioned entities will be included in JDBC batching.  The reason
 	 * being that some JDBC drivers have a problems returning "accurate" update counts from batch statements.
 	 * This is setting is {@code false} by default.
 	 *
 	 * @param enabled The batch size to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#BATCH_VERSIONED_DATA
 	 */
 	public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean enabled);
 
 	/**
 	 * Should scrollable results be supported in queries?  We ask the JDBC driver whether it
 	 * supports scrollable result sets as the default for this setting, but some drivers do not
 	 * accurately report this via DatabaseMetaData.  Also, needed if user is supplying connections
 	 * (and so no Connection is available when we bootstrap).
 	 *
 	 * @param enabled {@code true} to enable this support, {@code false} to disable it
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_SCROLLABLE_RESULTSET
 	 */
 	public SessionFactoryBuilder applyScrollableResultsSupport(boolean enabled);
 
 	/**
 	 * Hibernate currently accesses results from the JDBC ResultSet by name.  This is known
 	 * to be VERY slow on some drivers, especially older Oracle drivers.  This setting
 	 * allows Hibernate to wrap the ResultSet of the JDBC driver to manage the name->position
 	 * resolution itself.
 	 *
 	 * @param enabled {@code true} indicates Hibernate should wrap result sets; {@code false} indicates
 	 * it should not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#WRAP_RESULT_SETS
 	 */
 	public SessionFactoryBuilder applyResultSetsWrapping(boolean enabled);
 
 	/**
 	 * Should JDBC {@link java.sql.PreparedStatement#getGeneratedKeys()} feature be used for
 	 * retrieval of *insert-generated* ids?
 	 *
 	 * @param enabled {@code true} indicates we should use JDBC getGeneratedKeys support; {@code false}
 	 * indicates we should not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_GET_GENERATED_KEYS
 	 */
 	public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean enabled);
 
 	/**
 	 * Apply a fetch size to the JDBC driver for fetching results.
 	 *
 	 * @param size The fetch saize to be passed to the driver.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_GET_GENERATED_KEYS
 	 * @see java.sql.Statement#setFetchSize(int)
 	 */
 	public SessionFactoryBuilder applyJdbcFetchSize(int size);
 
 	/**
 	 * Apply a ConnectionReleaseMode.
 	 *
 	 * @param connectionReleaseMode The ConnectionReleaseMode to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#RELEASE_CONNECTIONS
 	 */
 	public SessionFactoryBuilder applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode);
 
 	/**
 	 * Should Hibernate apply comments to SQL it generates?
 	 *
 	 * @param enabled {@code true} indicates comments should be applied; {@code false} indicates not.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_SQL_COMMENTS
 	 */
 	public SessionFactoryBuilder applySqlComments(boolean enabled);
 
 	/**
 	 * Apply a SQLFunction to the underlying {@link org.hibernate.dialect.function.SQLFunctionRegistry}.
 	 * <p/>
 	 * TODO : Ultimately I would like this to move to {@link org.hibernate.boot.MetadataBuilder} in conjunction with allowing mappings to reference SQLFunctions.
 	 * today mappings can only name SQL functions directly, not through the SQLFunctionRegistry indirection
 	 *
 	 * @param registrationName The name to register it under.
 	 * @param sqlFunction The SQLFunction impl
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionFactoryBuilder applySqlFunction(String registrationName, SQLFunction sqlFunction);
 
 	/**
 	 * Allows unwrapping this builder as another, more specific type.
 	 *
 	 * @param type
 	 * @param <T>
 	 *
 	 * @return The unwrapped builder.
 	 */
 	public <T extends SessionFactoryBuilder> T unwrap(Class<T> type);
 
 	/**
 	 * After all options have been set, build the SessionFactory.
 	 *
 	 * @return The built SessionFactory.
 	 */
 	public SessionFactory build();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/TempTableDdlTransactionHandling.java b/hibernate-core/src/main/java/org/hibernate/boot/TempTableDdlTransactionHandling.java
new file mode 100644
index 0000000000..ac1454fb2f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/boot/TempTableDdlTransactionHandling.java
@@ -0,0 +1,47 @@
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
+/**
+ * Enum describing how creation and dropping of temporary tables should be done in terms of
+ * transaction handling.
+ *
+ * @author Steve Ebersole
+ */
+public enum TempTableDdlTransactionHandling {
+	/**
+	 * No handling of transactions is needed
+	 */
+	NONE,
+	/**
+	 * Execution of the DDL must be isolated from any ongoing transaction
+	 */
+	ISOLATE,
+	/**
+	 * As with {@link #ISOLATE} the execution of the DDL must be isolated from any ongoing transaction.
+	 * However, here the "isolation" will also be transacted.  Some databases require that the DDL
+	 * happen within a transaction.  This value covers such cases.
+	 */
+	ISOLATE_AND_TRANSACT
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
index edab8785b4..2f5db38c89 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryBuilderImpl.java
@@ -1,1202 +1,1206 @@
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
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.SchemaAutoTooling;
 import org.hibernate.boot.SessionFactoryBuilder;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.config.internal.ConfigurationServiceImpl;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.AvailableSettings.AUTO_CLOSE_SESSION;
 import static org.hibernate.cfg.AvailableSettings.AUTO_EVICT_COLLECTION_CACHE;
 import static org.hibernate.cfg.AvailableSettings.AUTO_SESSION_EVENTS_LISTENER;
 import static org.hibernate.cfg.AvailableSettings.BATCH_FETCH_STYLE;
 import static org.hibernate.cfg.AvailableSettings.BATCH_VERSIONED_DATA;
 import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_PREFIX;
 import static org.hibernate.cfg.AvailableSettings.CHECK_NULLABILITY;
 import static org.hibernate.cfg.AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY;
 import static org.hibernate.cfg.AvailableSettings.DEFAULT_BATCH_FETCH_SIZE;
 import static org.hibernate.cfg.AvailableSettings.DEFAULT_ENTITY_MODE;
 import static org.hibernate.cfg.AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS;
 import static org.hibernate.cfg.AvailableSettings.FLUSH_BEFORE_COMPLETION;
 import static org.hibernate.cfg.AvailableSettings.GENERATE_STATISTICS;
 import static org.hibernate.cfg.AvailableSettings.HQL_BULK_ID_STRATEGY;
 import static org.hibernate.cfg.AvailableSettings.INTERCEPTOR;
 import static org.hibernate.cfg.AvailableSettings.JPAQL_STRICT_COMPLIANCE;
 import static org.hibernate.cfg.AvailableSettings.JTA_TRACK_BY_THREAD;
 import static org.hibernate.cfg.AvailableSettings.LOG_SESSION_METRICS;
 import static org.hibernate.cfg.AvailableSettings.MAX_FETCH_DEPTH;
 import static org.hibernate.cfg.AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
 import static org.hibernate.cfg.AvailableSettings.ORDER_INSERTS;
 import static org.hibernate.cfg.AvailableSettings.ORDER_UPDATES;
 import static org.hibernate.cfg.AvailableSettings.QUERY_CACHE_FACTORY;
 import static org.hibernate.cfg.AvailableSettings.QUERY_STARTUP_CHECKING;
 import static org.hibernate.cfg.AvailableSettings.QUERY_SUBSTITUTIONS;
 import static org.hibernate.cfg.AvailableSettings.RELEASE_CONNECTIONS;
 import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME;
 import static org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI;
 import static org.hibernate.cfg.AvailableSettings.STATEMENT_BATCH_SIZE;
 import static org.hibernate.cfg.AvailableSettings.STATEMENT_FETCH_SIZE;
 import static org.hibernate.cfg.AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES;
 import static org.hibernate.cfg.AvailableSettings.USE_GET_GENERATED_KEYS;
 import static org.hibernate.cfg.AvailableSettings.USE_IDENTIFIER_ROLLBACK;
 import static org.hibernate.cfg.AvailableSettings.USE_MINIMAL_PUTS;
 import static org.hibernate.cfg.AvailableSettings.USE_QUERY_CACHE;
 import static org.hibernate.cfg.AvailableSettings.USE_SCROLLABLE_RESULTSET;
 import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;
 import static org.hibernate.cfg.AvailableSettings.USE_SQL_COMMENTS;
 import static org.hibernate.cfg.AvailableSettings.USE_STRUCTURED_CACHE;
 import static org.hibernate.cfg.AvailableSettings.WRAP_RESULT_SETS;
 import static org.hibernate.engine.config.spi.StandardConverters.BOOLEAN;
 
 /**
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class SessionFactoryBuilderImpl implements SessionFactoryBuilderImplementor, SessionFactoryOptionsState {
 	private static final Logger log = Logger.getLogger( SessionFactoryBuilderImpl.class );
 
 	private final MetadataImplementor metadata;
 	private final SessionFactoryOptionsStateStandardImpl options;
 
 	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 		this.options = new SessionFactoryOptionsStateStandardImpl( metadata.getMetadataBuildingOptions().getServiceRegistry() );
 
 		if ( metadata.getSqlFunctionMap() != null ) {
 			for ( Map.Entry<String, SQLFunction> sqlFunctionEntry : metadata.getSqlFunctionMap().entrySet() ) {
 				applySqlFunction( sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue() );
 			}
 		}
 	}
 
 	@Override
 	public SessionFactoryBuilder applyValidatorFactory(Object validatorFactory) {
 		this.options.validatorFactoryReference = validatorFactory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyBeanManager(Object beanManager) {
 		this.options.beanManagerReference = beanManager;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyName(String sessionFactoryName) {
 		this.options.sessionFactoryName = sessionFactoryName;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyNameAsJndiName(boolean isJndiName) {
 		this.options.sessionFactoryNameAlsoJndiName = isJndiName;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyAutoClosing(boolean enabled) {
 		this.options.autoCloseSessionEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyAutoFlushing(boolean enabled) {
 		this.options.flushBeforeCompletionEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyStatisticsSupport(boolean enabled) {
 		this.options.statisticsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder addSessionFactoryObservers(SessionFactoryObserver... observers) {
 		this.options.sessionFactoryObserverList.addAll( Arrays.asList( observers ) );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyInterceptor(Interceptor interceptor) {
 		this.options.interceptor = interceptor;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyCustomEntityDirtinessStrategy(CustomEntityDirtinessStrategy strategy) {
 		this.options.customEntityDirtinessStrategy = strategy;
 		return this;
 	}
 
 
 	@Override
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
 	public SessionFactoryBuilder applyIdentifierRollbackSupport(boolean enabled) {
 		this.options.identifierRollbackEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDefaultEntityMode(EntityMode entityMode) {
 		this.options.defaultEntityMode = entityMode;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyNullabilityChecking(boolean enabled) {
 		this.options.checkNullability = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyLazyInitializationOutsideTransaction(boolean enabled) {
 		this.options.initializeLazyStateOutsideTransactions = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
 		this.options.entityTuplizerFactory = entityTuplizerFactory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyEntityTuplizer(
 			EntityMode entityMode,
 			Class<? extends EntityTuplizer> tuplizerClass) {
 		this.options.entityTuplizerFactory.registerDefaultTuplizerClass( entityMode, tuplizerClass );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMultiTableBulkIdStrategy(MultiTableBulkIdStrategy strategy) {
 		this.options.multiTableBulkIdStrategy = strategy;
 		return this;
 	}
 
 	@Override
+	public SessionFactoryBuilder applyTempTableDdlTransactionHandling(TempTableDdlTransactionHandling handling) {
+		this.options.tempTableDdlTransactionHandling = handling;
+		return this;
+	}
+
+	@Override
 	public SessionFactoryBuilder applyBatchFetchStyle(BatchFetchStyle style) {
 		this.options.batchFetchStyle = style;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDefaultBatchFetchSize(int size) {
 		this.options.defaultBatchFetchSize = size;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMaximumFetchDepth(int depth) {
 		this.options.maximumFetchDepth = depth;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDefaultNullPrecedence(NullPrecedence nullPrecedence) {
 		this.options.defaultNullPrecedence = nullPrecedence;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyOrderingOfInserts(boolean enabled) {
 		this.options.orderInsertsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyOrderingOfUpdates(boolean enabled) {
 		this.options.orderUpdatesEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMultiTenancyStrategy(MultiTenancyStrategy strategy) {
 		this.options.multiTenancyStrategy = strategy;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver resolver) {
 		this.options.currentTenantIdentifierResolver = resolver;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJtaTrackingByThread(boolean enabled) {
 		this.options.jtaTrackByThread = enabled;
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public SessionFactoryBuilder applyQuerySubstitutions(Map substitutions) {
 		this.options.querySubstitutions.putAll( substitutions );
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyStrictJpaQueryLanguageCompliance(boolean enabled) {
 		this.options.strictJpaQueryLanguageCompliance = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyNamedQueryCheckingOnStartup(boolean enabled) {
 		this.options.namedQueryStartupCheckingEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applySecondLevelCacheSupport(boolean enabled) {
 		this.options.secondLevelCacheEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyQueryCacheSupport(boolean enabled) {
 		this.options.queryCacheEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyQueryCacheFactory(QueryCacheFactory factory) {
 		this.options.queryCacheFactory = factory;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyCacheRegionPrefix(String prefix) {
 		this.options.cacheRegionPrefix = prefix;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyMinimalPutsForCaching(boolean enabled) {
 		this.options.minimalPutsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyStructuredCacheEntries(boolean enabled) {
 		this.options.structuredCacheEntriesEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyDirectReferenceCaching(boolean enabled) {
 		this.options.directReferenceCacheEntriesEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyAutomaticEvictionOfCollectionCaches(boolean enabled) {
 		this.options.autoEvictCollectionCache = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJdbcBatchSize(int size) {
 		this.options.jdbcBatchSize = size;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJdbcBatchingForVersionedEntities(boolean enabled) {
 		this.options.jdbcBatchVersionedData = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyScrollableResultsSupport(boolean enabled) {
 		this.options.scrollableResultSetsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyResultSetsWrapping(boolean enabled) {
 		this.options.wrapResultSetsEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyGetGeneratedKeysSupport(boolean enabled) {
 		this.options.getGeneratedKeysEnabled = enabled;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyJdbcFetchSize(int size) {
 		this.options.jdbcFetchSize = size;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applyConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 		this.options.connectionReleaseMode = connectionReleaseMode;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder applySqlComments(boolean enabled) {
 		this.options.commentsEnabled = enabled;
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
 	@SuppressWarnings("unchecked")
 	public <T extends SessionFactoryBuilder> T unwrap(Class<T> type) {
 		return (T) this;
 	}
 
 	@Override
 	public SessionFactory build() {
 		metadata.validate();
 		return new SessionFactoryImpl( metadata, buildSessionFactoryOptions() );
 	}
 
 	@Override
 	public SessionFactoryOptions buildSessionFactoryOptions() {
 		return new SessionFactoryOptionsImpl( this );
 	}
 
 
 
 	// SessionFactoryOptionsState impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static class SessionFactoryOptionsStateStandardImpl implements SessionFactoryOptionsState {
 		private final StandardServiceRegistry serviceRegistry;
 
 		// integration
 		private Object beanManagerReference;
 		private Object validatorFactoryReference;
 
 		// SessionFactory behavior
 		private String sessionFactoryName;
 		private boolean sessionFactoryNameAlsoJndiName;
 
 		// Session behavior
 		private boolean flushBeforeCompletionEnabled;
 		private boolean autoCloseSessionEnabled;
 
 		// Statistics/Interceptor/observers
 		private boolean statisticsEnabled;
 		private Interceptor interceptor;
 		private List<SessionFactoryObserver> sessionFactoryObserverList = new ArrayList<SessionFactoryObserver>();
 		private BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder;	// not exposed on builder atm
 
 		// persistence behavior
 		private CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 		private List<EntityNameResolver> entityNameResolvers = new ArrayList<EntityNameResolver>();
 		private EntityNotFoundDelegate entityNotFoundDelegate;
 		private boolean identifierRollbackEnabled;
 		private EntityMode defaultEntityMode;
 		private EntityTuplizerFactory entityTuplizerFactory = new EntityTuplizerFactory();
 		private boolean checkNullability;
 		private boolean initializeLazyStateOutsideTransactions;
 		private MultiTableBulkIdStrategy multiTableBulkIdStrategy;
+		private TempTableDdlTransactionHandling tempTableDdlTransactionHandling;
 		private BatchFetchStyle batchFetchStyle;
 		private int defaultBatchFetchSize;
 		private Integer maximumFetchDepth;
 		private NullPrecedence defaultNullPrecedence;
 		private boolean orderUpdatesEnabled;
 		private boolean orderInsertsEnabled;
 
 		// multi-tenancy
 		private MultiTenancyStrategy multiTenancyStrategy;
 		private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 		// JTA timeout detection
 		private boolean jtaTrackByThread;
 
 		// Queries
 		private Map querySubstitutions;
 		private boolean strictJpaQueryLanguageCompliance;
 		private boolean namedQueryStartupCheckingEnabled;
 
 		// Caching
 		private boolean secondLevelCacheEnabled;
 		private boolean queryCacheEnabled;
 		private QueryCacheFactory queryCacheFactory;
 		private String cacheRegionPrefix;
 		private boolean minimalPutsEnabled;
 		private boolean structuredCacheEntriesEnabled;
 		private boolean directReferenceCacheEntriesEnabled;
 		private boolean autoEvictCollectionCache;
 
 		// Schema tooling
 		private SchemaAutoTooling schemaAutoTooling;
 
 		// JDBC Handling
-		private boolean dataDefinitionImplicitCommit;			// not exposed on builder atm
-		private boolean dataDefinitionInTransactionSupported;	// not exposed on builder atm
 		private boolean getGeneratedKeysEnabled;
 		private int jdbcBatchSize;
 		private boolean jdbcBatchVersionedData;
 		private Integer jdbcFetchSize;
 		private boolean scrollableResultSetsEnabled;
 		private boolean commentsEnabled;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean wrapResultSetsEnabled;
 
 		private Map<String, SQLFunction> sqlFunctions;
 
 		public SessionFactoryOptionsStateStandardImpl(StandardServiceRegistry serviceRegistry) {
 			this.serviceRegistry = serviceRegistry;
 
 			final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 			ConfigurationService cfgService = serviceRegistry.getService( ConfigurationService.class );
 			final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 			final Map configurationSettings = new HashMap();
 			//noinspection unchecked
 			configurationSettings.putAll( cfgService.getSettings() );
 			//noinspection unchecked
 			configurationSettings.putAll( jdbcServices.getJdbcEnvironment().getDialect().getDefaultProperties() );
 			cfgService = new ConfigurationServiceImpl( configurationSettings );
 			( (ConfigurationServiceImpl) cfgService ).injectServices( (ServiceRegistryImplementor) serviceRegistry );
 
 			this.beanManagerReference = configurationSettings.get( "javax.persistence.bean.manager" );
 			this.validatorFactoryReference = configurationSettings.get( "javax.persistence.validation.factory" );
 
 			this.sessionFactoryName = (String) configurationSettings.get( SESSION_FACTORY_NAME );
 			this.sessionFactoryNameAlsoJndiName = cfgService.getSetting(
 					SESSION_FACTORY_NAME_IS_JNDI,
 					BOOLEAN,
 					true
 			);
 
 			this.flushBeforeCompletionEnabled = cfgService.getSetting( FLUSH_BEFORE_COMPLETION, BOOLEAN, false );
 			this.autoCloseSessionEnabled = cfgService.getSetting( AUTO_CLOSE_SESSION, BOOLEAN, false );
 
 			this.statisticsEnabled = cfgService.getSetting( GENERATE_STATISTICS, BOOLEAN, false );
 			this.interceptor = strategySelector.resolveDefaultableStrategy(
 					Interceptor.class,
 					configurationSettings.get( INTERCEPTOR ),
 					EmptyInterceptor.INSTANCE
 			);
 			// todo : expose this from builder?
 			final String autoSessionEventsListenerName = (String) configurationSettings.get(
 					AUTO_SESSION_EVENTS_LISTENER
 			);
 			final Class<? extends SessionEventListener> autoSessionEventsListener = autoSessionEventsListenerName == null
 					? null
 					: strategySelector.selectStrategyImplementor( SessionEventListener.class, autoSessionEventsListenerName );
 
 			final boolean logSessionMetrics = cfgService.getSetting( LOG_SESSION_METRICS, BOOLEAN, statisticsEnabled );
 			this.baselineSessionEventsListenerBuilder = new BaselineSessionEventsListenerBuilder( logSessionMetrics, autoSessionEventsListener );
 
 			this.customEntityDirtinessStrategy = strategySelector.resolveDefaultableStrategy(
 					CustomEntityDirtinessStrategy.class,
 					configurationSettings.get( CUSTOM_ENTITY_DIRTINESS_STRATEGY ),
 					DefaultCustomEntityDirtinessStrategy.INSTANCE
 			);
 
 			this.entityNotFoundDelegate = StandardEntityNotFoundDelegate.INSTANCE;
 			this.identifierRollbackEnabled = cfgService.getSetting( USE_IDENTIFIER_ROLLBACK, BOOLEAN, false );
 			this.defaultEntityMode = EntityMode.parse( (String) configurationSettings.get( DEFAULT_ENTITY_MODE ) );
 			this.checkNullability = cfgService.getSetting( CHECK_NULLABILITY, BOOLEAN, true );
 			this.initializeLazyStateOutsideTransactions = cfgService.getSetting( ENABLE_LAZY_LOAD_NO_TRANS, BOOLEAN, false );
 
 			this.multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configurationSettings );
 			this.currentTenantIdentifierResolver = strategySelector.resolveStrategy(
 					CurrentTenantIdentifierResolver.class,
 					configurationSettings.get( MULTI_TENANT_IDENTIFIER_RESOLVER )
 			);
 
 			this.multiTableBulkIdStrategy = strategySelector.resolveDefaultableStrategy(
 					MultiTableBulkIdStrategy.class,
 					configurationSettings.get( HQL_BULK_ID_STRATEGY ),
 					jdbcServices.getJdbcEnvironment().getDialect().getDefaultMultiTableBulkIdStrategy()
 			);
 
 			this.batchFetchStyle = BatchFetchStyle.interpret( configurationSettings.get( BATCH_FETCH_STYLE ) );
 			this.defaultBatchFetchSize = ConfigurationHelper.getInt( DEFAULT_BATCH_FETCH_SIZE, configurationSettings, -1 );
 			this.maximumFetchDepth = ConfigurationHelper.getInteger( MAX_FETCH_DEPTH, configurationSettings );
 			final String defaultNullPrecedence = ConfigurationHelper.getString(
 					AvailableSettings.DEFAULT_NULL_ORDERING, configurationSettings, "none", "first", "last"
 			);
 			this.defaultNullPrecedence = NullPrecedence.parse( defaultNullPrecedence );
 			this.orderUpdatesEnabled = ConfigurationHelper.getBoolean( ORDER_UPDATES, configurationSettings );
 			this.orderInsertsEnabled = ConfigurationHelper.getBoolean( ORDER_INSERTS, configurationSettings );
 
 			this.jtaTrackByThread = cfgService.getSetting( JTA_TRACK_BY_THREAD, BOOLEAN, true );
 
 			this.querySubstitutions = ConfigurationHelper.toMap( QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", configurationSettings );
 			this.strictJpaQueryLanguageCompliance = cfgService.getSetting( JPAQL_STRICT_COMPLIANCE, BOOLEAN, false );
 			this.namedQueryStartupCheckingEnabled = cfgService.getSetting( QUERY_STARTUP_CHECKING, BOOLEAN, true );
 
 			this.secondLevelCacheEnabled = cfgService.getSetting( USE_SECOND_LEVEL_CACHE, BOOLEAN, true );
 			this.queryCacheEnabled = cfgService.getSetting( USE_QUERY_CACHE, BOOLEAN, false );
 			this.queryCacheFactory = strategySelector.resolveDefaultableStrategy(
 					QueryCacheFactory.class,
 					configurationSettings.get( QUERY_CACHE_FACTORY ),
 					StandardQueryCacheFactory.INSTANCE
 			);
 			this.cacheRegionPrefix = ConfigurationHelper.extractPropertyValue(
 					CACHE_REGION_PREFIX,
 					configurationSettings
 			);
 			this.minimalPutsEnabled = cfgService.getSetting(
 					USE_MINIMAL_PUTS,
 					BOOLEAN,
 					serviceRegistry.getService( RegionFactory.class ).isMinimalPutsEnabledByDefault()
 			);
 			this.structuredCacheEntriesEnabled = cfgService.getSetting( USE_STRUCTURED_CACHE, BOOLEAN, false );
 			this.directReferenceCacheEntriesEnabled = cfgService.getSetting( USE_DIRECT_REFERENCE_CACHE_ENTRIES,BOOLEAN, false );
 			this.autoEvictCollectionCache = cfgService.getSetting( AUTO_EVICT_COLLECTION_CACHE, BOOLEAN, false );
 
 			try {
 				this.schemaAutoTooling = SchemaAutoTooling.interpret( (String) configurationSettings.get( AvailableSettings.HBM2DDL_AUTO ) );
 			}
 			catch (Exception e) {
 				log.warn( e.getMessage() + "  Ignoring" );
 			}
 
 
 			final ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
-			this.dataDefinitionImplicitCommit = meta.doesDataDefinitionCauseTransactionCommit();
-			this.dataDefinitionInTransactionSupported = meta.supportsDataDefinitionInTransaction();
+
+			this.tempTableDdlTransactionHandling = TempTableDdlTransactionHandling.NONE;
+			if ( meta.doesDataDefinitionCauseTransactionCommit() ) {
+				if ( meta.supportsDataDefinitionInTransaction() ) {
+					this.tempTableDdlTransactionHandling = TempTableDdlTransactionHandling.ISOLATE_AND_TRANSACT;
+				}
+				else {
+					this.tempTableDdlTransactionHandling = TempTableDdlTransactionHandling.ISOLATE;
+				}
+			}
 
 			this.jdbcBatchSize = ConfigurationHelper.getInt( STATEMENT_BATCH_SIZE, configurationSettings, 0 );
 			if ( !meta.supportsBatchUpdates() ) {
 				this.jdbcBatchSize = 0;
 			}
 
 			this.jdbcBatchVersionedData = ConfigurationHelper.getBoolean( BATCH_VERSIONED_DATA, configurationSettings, false );
 			this.scrollableResultSetsEnabled = ConfigurationHelper.getBoolean(
 					USE_SCROLLABLE_RESULTSET,
 					configurationSettings,
 					meta.supportsScrollableResults()
 			);
 			this.wrapResultSetsEnabled = ConfigurationHelper.getBoolean(
 					WRAP_RESULT_SETS,
 					configurationSettings,
 					false
 			);
 			this.getGeneratedKeysEnabled = ConfigurationHelper.getBoolean(
 					USE_GET_GENERATED_KEYS,
 					configurationSettings,
 					meta.supportsGetGeneratedKeys()
 			);
 			this.jdbcFetchSize = ConfigurationHelper.getInteger( STATEMENT_FETCH_SIZE, configurationSettings );
 
 			final String releaseModeName = ConfigurationHelper.getString( RELEASE_CONNECTIONS, configurationSettings, "auto" );
 			if ( "auto".equals( releaseModeName ) ) {
 				this.connectionReleaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 			}
 			else {
 				connectionReleaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			}
 
 			this.commentsEnabled = ConfigurationHelper.getBoolean( USE_SQL_COMMENTS, configurationSettings );
 		}
 
 		@Override
 		public StandardServiceRegistry getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		@Override
 		public Object getBeanManagerReference() {
 			return beanManagerReference;
 		}
 
 		@Override
 		public Object getValidatorFactoryReference() {
 			return validatorFactoryReference;
 		}
 
 		@Override
 		public String getSessionFactoryName() {
 			return sessionFactoryName;
 		}
 
 		@Override
 		public boolean isSessionFactoryNameAlsoJndiName() {
 			return sessionFactoryNameAlsoJndiName;
 		}
 
 		@Override
 		public boolean isFlushBeforeCompletionEnabled() {
 			return flushBeforeCompletionEnabled;
 		}
 
 		@Override
 		public boolean isAutoCloseSessionEnabled() {
 			return autoCloseSessionEnabled;
 		}
 
 		@Override
 		public boolean isStatisticsEnabled() {
 			return statisticsEnabled;
 		}
 
 		@Override
 		public Interceptor getInterceptor() {
 			return interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		}
 
 		@Override
 		public SessionFactoryObserver[] getSessionFactoryObservers() {
 			return sessionFactoryObserverList.toArray( new SessionFactoryObserver[ sessionFactoryObserverList.size() ] );
 		}
 
 		@Override
 		public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
 			return baselineSessionEventsListenerBuilder;
 		}
 
 		@Override
 		public boolean isIdentifierRollbackEnabled() {
 			return identifierRollbackEnabled;
 		}
 
 		@Override
 		public EntityMode getDefaultEntityMode() {
 			return defaultEntityMode;
 		}
 
 		@Override
 		public EntityTuplizerFactory getEntityTuplizerFactory() {
 			return entityTuplizerFactory;
 		}
 
 		@Override
 		public boolean isCheckNullability() {
 			return checkNullability;
 		}
 
 		@Override
 		public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
 			return initializeLazyStateOutsideTransactions;
 		}
 
 		@Override
 		public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
 			return multiTableBulkIdStrategy;
 		}
 
 		@Override
+		public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling() {
+			return tempTableDdlTransactionHandling;
+		}
+
+		@Override
 		public BatchFetchStyle getBatchFetchStyle() {
 			return batchFetchStyle;
 		}
 
 		@Override
 		public int getDefaultBatchFetchSize() {
 			return defaultBatchFetchSize;
 		}
 
 		@Override
 		public Integer getMaximumFetchDepth() {
 			return maximumFetchDepth;
 		}
 
 		@Override
 		public NullPrecedence getDefaultNullPrecedence() {
 			return defaultNullPrecedence;
 		}
 
 		@Override
 		public boolean isOrderUpdatesEnabled() {
 			return orderUpdatesEnabled;
 		}
 
 		@Override
 		public boolean isOrderInsertsEnabled() {
 			return orderInsertsEnabled;
 		}
 
 		@Override
 		public MultiTenancyStrategy getMultiTenancyStrategy() {
 			return multiTenancyStrategy;
 		}
 
 		@Override
 		public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 			return currentTenantIdentifierResolver;
 		}
 
 		@Override
 		public boolean isJtaTrackByThread() {
 			return jtaTrackByThread;
 		}
 
 		@Override
 		public Map getQuerySubstitutions() {
 			return querySubstitutions;
 		}
 
 		@Override
 		public boolean isStrictJpaQueryLanguageCompliance() {
 			return strictJpaQueryLanguageCompliance;
 		}
 
 		@Override
 		public boolean isNamedQueryStartupCheckingEnabled() {
 			return namedQueryStartupCheckingEnabled;
 		}
 
 		@Override
 		public boolean isSecondLevelCacheEnabled() {
 			return secondLevelCacheEnabled;
 		}
 
 		@Override
 		public boolean isQueryCacheEnabled() {
 			return queryCacheEnabled;
 		}
 
 		@Override
 		public QueryCacheFactory getQueryCacheFactory() {
 			return queryCacheFactory;
 		}
 
 		@Override
 		public String getCacheRegionPrefix() {
 			return cacheRegionPrefix;
 		}
 
 		@Override
 		public boolean isMinimalPutsEnabled() {
 			return minimalPutsEnabled;
 		}
 
 		@Override
 		public boolean isStructuredCacheEntriesEnabled() {
 			return structuredCacheEntriesEnabled;
 		}
 
 		@Override
 		public boolean isDirectReferenceCacheEntriesEnabled() {
 			return directReferenceCacheEntriesEnabled;
 		}
 
 		@Override
 		public boolean isAutoEvictCollectionCache() {
 			return autoEvictCollectionCache;
 		}
 
 		@Override
 		public SchemaAutoTooling getSchemaAutoTooling() {
 			return schemaAutoTooling;
 		}
 
 		@Override
-		public boolean isDataDefinitionImplicitCommit() {
-			return dataDefinitionImplicitCommit;
-		}
-
-		@Override
-		public boolean isDataDefinitionInTransactionSupported() {
-			return dataDefinitionInTransactionSupported;
-		}
-
-		@Override
 		public int getJdbcBatchSize() {
 			return jdbcBatchSize;
 		}
 
 		@Override
 		public boolean isJdbcBatchVersionedData() {
 			return jdbcBatchVersionedData;
 		}
 
 		@Override
 		public boolean isScrollableResultSetsEnabled() {
 			return scrollableResultSetsEnabled;
 		}
 
 		@Override
 		public boolean isWrapResultSetsEnabled() {
 			return wrapResultSetsEnabled;
 		}
 
 		@Override
 		public boolean isGetGeneratedKeysEnabled() {
 			return getGeneratedKeysEnabled;
 		}
 
 		@Override
 		public Integer getJdbcFetchSize() {
 			return jdbcFetchSize;
 		}
 
 		@Override
 		public ConnectionReleaseMode getConnectionReleaseMode() {
 			return connectionReleaseMode;
 		}
 
 		@Override
 		public boolean isCommentsEnabled() {
 			return commentsEnabled;
 		}
 
 		@Override
 		public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 			return customEntityDirtinessStrategy;
 		}
 
 		@Override
 		public EntityNameResolver[] getEntityNameResolvers() {
 			return entityNameResolvers.toArray( new EntityNameResolver[ entityNameResolvers.size() ] );
 		}
 
 		@Override
 		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 			return entityNotFoundDelegate;
 		}
 
 		@Override
 		public Map<String, SQLFunction> getCustomSqlFunctionMap() {
 			return sqlFunctions == null ? Collections.<String, SQLFunction>emptyMap() : sqlFunctions;
 		}
 
 	}
 
 	@Override
 	public StandardServiceRegistry getServiceRegistry() {
 		return options.getServiceRegistry();
 	}
 
 	@Override
 	public Object getBeanManagerReference() {
 		return options.getBeanManagerReference();
 	}
 
 	@Override
 	public Object getValidatorFactoryReference() {
 		return options.getValidatorFactoryReference();
 	}
 
 	@Override
 	public String getSessionFactoryName() {
 		return options.getSessionFactoryName();
 	}
 
 	@Override
 	public boolean isSessionFactoryNameAlsoJndiName() {
 		return options.isSessionFactoryNameAlsoJndiName();
 	}
 
 	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return options.isFlushBeforeCompletionEnabled();
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return options.isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public boolean isStatisticsEnabled() {
 		return options.isStatisticsEnabled();
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return options.getInterceptor();
 	}
 
 	@Override
 	public SessionFactoryObserver[] getSessionFactoryObservers() {
 		return options.getSessionFactoryObservers();
 	}
 
 	@Override
 	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
 		return options.getBaselineSessionEventsListenerBuilder();
 	}
 
 	@Override
 	public boolean isIdentifierRollbackEnabled() {
 		return options.isIdentifierRollbackEnabled();
 	}
 
 	@Override
 	public EntityMode getDefaultEntityMode() {
 		return options.getDefaultEntityMode();
 	}
 
 	@Override
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return options.getEntityTuplizerFactory();
 	}
 
 	@Override
 	public boolean isCheckNullability() {
 		return options.isCheckNullability();
 	}
 
 	@Override
 	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
 		return options.isInitializeLazyStateOutsideTransactionsEnabled();
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
 		return options.getMultiTableBulkIdStrategy();
 	}
 
 	@Override
+	public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling() {
+		return options.getTempTableDdlTransactionHandling();
+	}
+
+	@Override
 	public BatchFetchStyle getBatchFetchStyle() {
 		return options.getBatchFetchStyle();
 	}
 
 	@Override
 	public int getDefaultBatchFetchSize() {
 		return options.getDefaultBatchFetchSize();
 	}
 
 	@Override
 	public Integer getMaximumFetchDepth() {
 		return options.getMaximumFetchDepth();
 	}
 
 	@Override
 	public NullPrecedence getDefaultNullPrecedence() {
 		return options.getDefaultNullPrecedence();
 	}
 
 	@Override
 	public boolean isOrderUpdatesEnabled() {
 		return options.isOrderUpdatesEnabled();
 	}
 
 	@Override
 	public boolean isOrderInsertsEnabled() {
 		return options.isOrderInsertsEnabled();
 	}
 
 	@Override
 	public MultiTenancyStrategy getMultiTenancyStrategy() {
 		return options.getMultiTenancyStrategy();
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return options.getCurrentTenantIdentifierResolver();
 	}
 
 	@Override
 	public boolean isJtaTrackByThread() {
 		return options.isJtaTrackByThread();
 	}
 
 	@Override
 	public Map getQuerySubstitutions() {
 		return options.getQuerySubstitutions();
 	}
 
 	@Override
 	public boolean isStrictJpaQueryLanguageCompliance() {
 		return options.isStrictJpaQueryLanguageCompliance();
 	}
 
 	@Override
 	public boolean isNamedQueryStartupCheckingEnabled() {
 		return options.isNamedQueryStartupCheckingEnabled();
 	}
 
 	@Override
 	public boolean isSecondLevelCacheEnabled() {
 		return options.isSecondLevelCacheEnabled();
 	}
 
 	@Override
 	public boolean isQueryCacheEnabled() {
 		return options.isQueryCacheEnabled();
 	}
 
 	@Override
 	public QueryCacheFactory getQueryCacheFactory() {
 		return options.getQueryCacheFactory();
 	}
 
 	@Override
 	public String getCacheRegionPrefix() {
 		return options.getCacheRegionPrefix();
 	}
 
 	@Override
 	public boolean isMinimalPutsEnabled() {
 		return options.isMinimalPutsEnabled();
 	}
 
 	@Override
 	public boolean isStructuredCacheEntriesEnabled() {
 		return options.isStructuredCacheEntriesEnabled();
 	}
 
 	@Override
 	public boolean isDirectReferenceCacheEntriesEnabled() {
 		return options.isDirectReferenceCacheEntriesEnabled();
 	}
 
 	@Override
 	public boolean isAutoEvictCollectionCache() {
 		return options.isAutoEvictCollectionCache();
 	}
 
 	@Override
 	public SchemaAutoTooling getSchemaAutoTooling() {
 		return options.getSchemaAutoTooling();
 	}
 
 	@Override
-	public boolean isDataDefinitionImplicitCommit() {
-		return options.isDataDefinitionImplicitCommit();
-	}
-
-	@Override
-	public boolean isDataDefinitionInTransactionSupported() {
-		return options.isDataDefinitionInTransactionSupported();
-	}
-
-	@Override
 	public int getJdbcBatchSize() {
 		return options.getJdbcBatchSize();
 	}
 
 	@Override
 	public boolean isJdbcBatchVersionedData() {
 		return options.isJdbcBatchVersionedData();
 	}
 
 	@Override
 	public boolean isScrollableResultSetsEnabled() {
 		return options.isScrollableResultSetsEnabled();
 	}
 
 	@Override
 	public boolean isWrapResultSetsEnabled() {
 		return options.isWrapResultSetsEnabled();
 	}
 
 	@Override
 	public boolean isGetGeneratedKeysEnabled() {
 		return options.isGetGeneratedKeysEnabled();
 	}
 
 	@Override
 	public Integer getJdbcFetchSize() {
 		return options.getJdbcFetchSize();
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return options.getConnectionReleaseMode();
 	}
 
 	@Override
 	public boolean isCommentsEnabled() {
 		return options.isCommentsEnabled();
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return options.getCustomEntityDirtinessStrategy();
 	}
 
 	@Override
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return options.getEntityNameResolvers();
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return options.getEntityNotFoundDelegate();
 	}
 
 	@Override
 	public Map<String, SQLFunction> getCustomSqlFunctionMap() {
 		return options.getCustomSqlFunctionMap();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsImpl.java
index 006e3768e5..fd1c96a033 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsImpl.java
@@ -1,461 +1,455 @@
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
 package org.hibernate.boot.internal;
 
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.SchemaAutoTooling;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Standard implementation of SessionFactoryOptions
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryOptionsImpl implements SessionFactoryOptions {
 	private final StandardServiceRegistry serviceRegistry;
 
 	// integration
 	private final Object beanManagerReference;
 	private final Object validatorFactoryReference;
 
 	// SessionFactory behavior
 	private final String sessionFactoryName;
 	private final boolean sessionFactoryNameAlsoJndiName;
 
 	// Session behavior
 	private final boolean flushBeforeCompletionEnabled;
 	private final boolean autoCloseSessionEnabled;
 
 	// Statistics/Interceptor/observers
 	private final boolean statisticsEnabled;
 	private final Interceptor interceptor;
 	private final SessionFactoryObserver[] sessionFactoryObserverList;
 	private final BaselineSessionEventsListenerBuilder baselineSessionEventsListenerBuilder;	// not exposed on builder atm
 
 	// persistence behavior
 	private final CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 	private final EntityNameResolver[] entityNameResolvers;
 	private final EntityNotFoundDelegate entityNotFoundDelegate;
 	private final boolean identifierRollbackEnabled;
 	private final EntityMode defaultEntityMode;
 	private final EntityTuplizerFactory entityTuplizerFactory;
 	private boolean checkNullability;
 	private final boolean initializeLazyStateOutsideTransactions;
 	private final MultiTableBulkIdStrategy multiTableBulkIdStrategy;
+	private final TempTableDdlTransactionHandling tempTableDdlTransactionHandling;
 	private final BatchFetchStyle batchFetchStyle;
 	private final int defaultBatchFetchSize;
 	private final Integer maximumFetchDepth;
 	private final NullPrecedence defaultNullPrecedence;
 	private final boolean orderUpdatesEnabled;
 	private final boolean orderInsertsEnabled;
 
 	// multi-tenancy
 	private final MultiTenancyStrategy multiTenancyStrategy;
 	private final CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 	// JTA timeout detection
 	private final boolean jtaTrackByThread;
 
 	// Queries
 	private final Map querySubstitutions;
 	private final boolean strictJpaQueryLanguageCompliance;
 	private final boolean namedQueryStartupCheckingEnabled;
 
 	// Caching
 	private final boolean secondLevelCacheEnabled;
 	private final boolean queryCacheEnabled;
 	private final QueryCacheFactory queryCacheFactory;
 	private final String cacheRegionPrefix;
 	private final boolean minimalPutsEnabled;
 	private final boolean structuredCacheEntriesEnabled;
 	private final boolean directReferenceCacheEntriesEnabled;
 	private final boolean autoEvictCollectionCache;
 
 	// Schema tooling
 	private final SchemaAutoTooling schemaAutoTooling;
 
 	// JDBC Handling
-	private final boolean dataDefinitionImplicitCommit;			// not exposed on builder atm
-	private final boolean dataDefinitionInTransactionSupported;	// not exposed on builder atm
 	private final boolean getGeneratedKeysEnabled;
 	private final int jdbcBatchSize;
 	private final boolean jdbcBatchVersionedData;
 	private final Integer jdbcFetchSize;
 	private final boolean scrollableResultSetsEnabled;
 	private final boolean commentsEnabled;
 	private final ConnectionReleaseMode connectionReleaseMode;
 	private final boolean wrapResultSetsEnabled;
 
 	private final Map<String, SQLFunction> sqlFunctions;
 
 
 	public SessionFactoryOptionsImpl(SessionFactoryOptionsState state) {
 		this.serviceRegistry = state.getServiceRegistry();
 
 		this.beanManagerReference = state.getBeanManagerReference();
 		this.validatorFactoryReference = state.getValidatorFactoryReference();
 
 		this.sessionFactoryName = state.getSessionFactoryName();
 		this.sessionFactoryNameAlsoJndiName = state.isSessionFactoryNameAlsoJndiName();
 
 		this.flushBeforeCompletionEnabled = state.isFlushBeforeCompletionEnabled();
 		this.autoCloseSessionEnabled = state.isAutoCloseSessionEnabled();
 
 		this.statisticsEnabled = state.isStatisticsEnabled();
 		this.interceptor = state.getInterceptor();
 		this.sessionFactoryObserverList = state.getSessionFactoryObservers();
 		this.baselineSessionEventsListenerBuilder = state.getBaselineSessionEventsListenerBuilder();
 
 		this.customEntityDirtinessStrategy = state.getCustomEntityDirtinessStrategy();
 		this.entityNameResolvers = state.getEntityNameResolvers();
 		this.entityNotFoundDelegate = state.getEntityNotFoundDelegate();
 		this.identifierRollbackEnabled = state.isIdentifierRollbackEnabled();
 		this.defaultEntityMode = state.getDefaultEntityMode();
 		this.entityTuplizerFactory = state.getEntityTuplizerFactory();
 		this.checkNullability = state.isCheckNullability();
 		this.initializeLazyStateOutsideTransactions = state.isInitializeLazyStateOutsideTransactionsEnabled();
 		this.multiTableBulkIdStrategy = state.getMultiTableBulkIdStrategy();
+		this.tempTableDdlTransactionHandling = state.getTempTableDdlTransactionHandling();
 		this.batchFetchStyle = state.getBatchFetchStyle();
 		this.defaultBatchFetchSize = state.getDefaultBatchFetchSize();
 		this.maximumFetchDepth = state.getMaximumFetchDepth();
 		this.defaultNullPrecedence = state.getDefaultNullPrecedence();
 		this.orderUpdatesEnabled = state.isOrderUpdatesEnabled();
 		this.orderInsertsEnabled = state.isOrderInsertsEnabled();
 
 		this.multiTenancyStrategy = state.getMultiTenancyStrategy();
 		this.currentTenantIdentifierResolver = state.getCurrentTenantIdentifierResolver();
 
 		this.jtaTrackByThread = state.isJtaTrackByThread();
 
 		this.querySubstitutions = state.getQuerySubstitutions();
 		this.strictJpaQueryLanguageCompliance = state.isStrictJpaQueryLanguageCompliance();
 		this.namedQueryStartupCheckingEnabled = state.isNamedQueryStartupCheckingEnabled();
 
 		this.secondLevelCacheEnabled = state.isSecondLevelCacheEnabled();
 		this.queryCacheEnabled = state.isQueryCacheEnabled();
 		this.queryCacheFactory = state.getQueryCacheFactory();
 		this.cacheRegionPrefix = state.getCacheRegionPrefix();
 		this.minimalPutsEnabled = state.isMinimalPutsEnabled();
 		this.structuredCacheEntriesEnabled = state.isStructuredCacheEntriesEnabled();
 		this.directReferenceCacheEntriesEnabled = state.isDirectReferenceCacheEntriesEnabled();
 		this.autoEvictCollectionCache = state.isAutoEvictCollectionCache();
 
 		this.schemaAutoTooling = state.getSchemaAutoTooling();
 
 		this.connectionReleaseMode = state.getConnectionReleaseMode();
-		this.dataDefinitionImplicitCommit = state.isDataDefinitionImplicitCommit();
-		this.dataDefinitionInTransactionSupported = state.isDataDefinitionInTransactionSupported();
 		this.getGeneratedKeysEnabled = state.isGetGeneratedKeysEnabled();
 		this.jdbcBatchSize = state.getJdbcBatchSize();
 		this.jdbcBatchVersionedData = state.isJdbcBatchVersionedData();
 		this.jdbcFetchSize = state.getJdbcFetchSize();
 		this.scrollableResultSetsEnabled = state.isScrollableResultSetsEnabled();
 		this.wrapResultSetsEnabled = state.isWrapResultSetsEnabled();
 		this.commentsEnabled = state.isCommentsEnabled();
 
 		this.sqlFunctions = state.getCustomSqlFunctionMap();
 	}
 
 	@Override
 	public StandardServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public Object getBeanManagerReference() {
 		return beanManagerReference;
 	}
 
 	@Override
 	public Object getValidatorFactoryReference() {
 		return validatorFactoryReference;
 	}
 
 	@Override
 	public String getSessionFactoryName() {
 		return sessionFactoryName;
 	}
 
 	@Override
 	public boolean isSessionFactoryNameAlsoJndiName() {
 		return sessionFactoryNameAlsoJndiName;
 	}
 
 	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	@Override
 	public boolean isStatisticsEnabled() {
 		return statisticsEnabled;
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	@Override
 	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
 		return baselineSessionEventsListenerBuilder;
 	}
 
 	@Override
 	public SessionFactoryObserver[] getSessionFactoryObservers() {
 		return sessionFactoryObserverList;
 	}
 
 	@Override
 	public boolean isIdentifierRollbackEnabled() {
 		return identifierRollbackEnabled;
 	}
 
 	@Override
 	public EntityMode getDefaultEntityMode() {
 		return defaultEntityMode;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 	@Override
 	public boolean isCheckNullability() {
 		return checkNullability;
 	}
 
 	@Override
 	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
 		return initializeLazyStateOutsideTransactions;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
 		return multiTableBulkIdStrategy;
 	}
 
 	@Override
+	public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling() {
+		return tempTableDdlTransactionHandling;
+	}
+
+	@Override
 	public BatchFetchStyle getBatchFetchStyle() {
 		return batchFetchStyle;
 	}
 
 	@Override
 	public int getDefaultBatchFetchSize() {
 		return defaultBatchFetchSize;
 	}
 
 	@Override
 	public Integer getMaximumFetchDepth() {
 		return maximumFetchDepth;
 	}
 
 	@Override
 	public NullPrecedence getDefaultNullPrecedence() {
 		return defaultNullPrecedence;
 	}
 
 	@Override
 	public boolean isOrderUpdatesEnabled() {
 		return orderUpdatesEnabled;
 	}
 
 	@Override
 	public boolean isOrderInsertsEnabled() {
 		return orderInsertsEnabled;
 	}
 
 	@Override
 	public MultiTenancyStrategy getMultiTenancyStrategy() {
 		return multiTenancyStrategy;
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 	@Override
 	public boolean isJtaTrackByThread() {
 		return jtaTrackByThread;
 	}
 
 	@Override
 	public Map getQuerySubstitutions() {
 		return querySubstitutions;
 	}
 
 	@Override
 	public boolean isStrictJpaQueryLanguageCompliance() {
 		return strictJpaQueryLanguageCompliance;
 	}
 
 	@Override
 	public boolean isNamedQueryStartupCheckingEnabled() {
 		return namedQueryStartupCheckingEnabled;
 	}
 
 	@Override
 	public boolean isSecondLevelCacheEnabled() {
 		return secondLevelCacheEnabled;
 	}
 
 	@Override
 	public boolean isQueryCacheEnabled() {
 		return queryCacheEnabled;
 	}
 
 	@Override
 	public QueryCacheFactory getQueryCacheFactory() {
 		return queryCacheFactory;
 	}
 
 	@Override
 	public String getCacheRegionPrefix() {
 		return cacheRegionPrefix;
 	}
 
 	@Override
 	public boolean isMinimalPutsEnabled() {
 		return minimalPutsEnabled;
 	}
 
 	@Override
 	public boolean isStructuredCacheEntriesEnabled() {
 		return structuredCacheEntriesEnabled;
 	}
 
 	@Override
 	public boolean isDirectReferenceCacheEntriesEnabled() {
 		return directReferenceCacheEntriesEnabled;
 	}
 
 	public boolean isAutoEvictCollectionCache() {
 		return autoEvictCollectionCache;
 	}
 
 	@Override
 	public SchemaAutoTooling getSchemaAutoTooling() {
 		return schemaAutoTooling;
 	}
 
 	@Override
-	public boolean isDataDefinitionImplicitCommit() {
-		return dataDefinitionImplicitCommit;
-	}
-
-	@Override
-	public boolean isDataDefinitionInTransactionSupported() {
-		return dataDefinitionInTransactionSupported;
-	}
-
-	@Override
 	public int getJdbcBatchSize() {
 		return jdbcBatchSize;
 	}
 
 	@Override
 	public boolean isJdbcBatchVersionedData() {
 		return jdbcBatchVersionedData;
 	}
 
 	@Override
 	public boolean isScrollableResultSetsEnabled() {
 		return scrollableResultSetsEnabled;
 	}
 
 	@Override
 	public boolean isWrapResultSetsEnabled() {
 		return wrapResultSetsEnabled;
 	}
 
 	@Override
 	public boolean isGetGeneratedKeysEnabled() {
 		return getGeneratedKeysEnabled;
 	}
 
 	@Override
 	public Integer getJdbcFetchSize() {
 		return jdbcFetchSize;
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public boolean isCommentsEnabled() {
 		return commentsEnabled;
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return customEntityDirtinessStrategy;
 	}
 
 
 	@Override
 	public EntityNameResolver[] getEntityNameResolvers() {
 		return entityNameResolvers;
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
 	public void setCheckNullability(boolean enabled) {
 		this.checkNullability = enabled;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsState.java b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsState.java
index 7e4280a174..bce4250a88 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsState.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/internal/SessionFactoryOptionsState.java
@@ -1,156 +1,155 @@
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
 package org.hibernate.boot.internal;
 
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.SchemaAutoTooling;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Sort of a mutable SessionFactoryOptions used during SessionFactoryBuilder calls.
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryOptionsState {
 	public StandardServiceRegistry getServiceRegistry();
 
 	public Object getBeanManagerReference();
 
 	public Object getValidatorFactoryReference();
 
 	public String getSessionFactoryName();
 
 	public boolean isSessionFactoryNameAlsoJndiName();
 
 	public boolean isFlushBeforeCompletionEnabled();
 
 	public boolean isAutoCloseSessionEnabled();
 
 	public boolean isStatisticsEnabled();
 
 	public Interceptor getInterceptor();
 
 	public SessionFactoryObserver[] getSessionFactoryObservers();
 
 	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder();
 
 	public boolean isIdentifierRollbackEnabled();
 
 	public EntityMode getDefaultEntityMode();
 
 	public EntityTuplizerFactory getEntityTuplizerFactory();
 
 	public boolean isCheckNullability();
 
 	public boolean isInitializeLazyStateOutsideTransactionsEnabled();
 
 	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy();
 
+	public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling();
+
 	public BatchFetchStyle getBatchFetchStyle();
 
 	public int getDefaultBatchFetchSize();
 
 	public Integer getMaximumFetchDepth();
 
 	public NullPrecedence getDefaultNullPrecedence();
 
 	public boolean isOrderUpdatesEnabled();
 
 	public boolean isOrderInsertsEnabled();
 
 	public MultiTenancyStrategy getMultiTenancyStrategy();
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
 	public boolean isJtaTrackByThread();
 
 	public Map getQuerySubstitutions();
 
 	public boolean isStrictJpaQueryLanguageCompliance();
 
 	public boolean isNamedQueryStartupCheckingEnabled();
 
 	public boolean isSecondLevelCacheEnabled();
 
 	public boolean isQueryCacheEnabled();
 
 	public QueryCacheFactory getQueryCacheFactory();
 
 	public String getCacheRegionPrefix();
 
 	public boolean isMinimalPutsEnabled();
 
 	public boolean isStructuredCacheEntriesEnabled();
 
 	public boolean isDirectReferenceCacheEntriesEnabled();
 
 	public boolean isAutoEvictCollectionCache();
 
 	public SchemaAutoTooling getSchemaAutoTooling();
 
-	public boolean isDataDefinitionImplicitCommit();
-
-	public boolean isDataDefinitionInTransactionSupported();
-
 	public int getJdbcBatchSize();
 
 	public boolean isJdbcBatchVersionedData();
 
 	public boolean isScrollableResultSetsEnabled();
 
 	public boolean isWrapResultSetsEnabled();
 
 	public boolean isGetGeneratedKeysEnabled();
 
 	public Integer getJdbcFetchSize();
 
 	public ConnectionReleaseMode getConnectionReleaseMode();
 
 	public boolean isCommentsEnabled();
 
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	public EntityNameResolver[] getEntityNameResolvers();
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public Map<String, SQLFunction> getCustomSqlFunctionMap();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java
index 5285dfe8c2..df301da11c 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/selector/internal/StrategySelectorBuilder.java
@@ -1,410 +1,404 @@
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
 package org.hibernate.boot.registry.selector.internal;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.SimpleStrategyRegistrationImpl;
 import org.hibernate.boot.registry.selector.StrategyRegistration;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.boot.registry.selector.spi.StrategySelectionException;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.dialect.CUBRIDDialect;
 import org.hibernate.dialect.Cache71Dialect;
 import org.hibernate.dialect.DB2390Dialect;
 import org.hibernate.dialect.DB2400Dialect;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.DerbyTenFiveDialect;
 import org.hibernate.dialect.DerbyTenSevenDialect;
 import org.hibernate.dialect.DerbyTenSixDialect;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.FirebirdDialect;
 import org.hibernate.dialect.FrontBaseDialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InformixDialect;
 import org.hibernate.dialect.Ingres10Dialect;
 import org.hibernate.dialect.Ingres9Dialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.InterbaseDialect;
 import org.hibernate.dialect.JDataStoreDialect;
 import org.hibernate.dialect.MckoiDialect;
 import org.hibernate.dialect.MimerSQLDialect;
 import org.hibernate.dialect.MySQL5Dialect;
 import org.hibernate.dialect.MySQL5InnoDBDialect;
 import org.hibernate.dialect.Oracle10gDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.Oracle9iDialect;
 import org.hibernate.dialect.PointbaseDialect;
 import org.hibernate.dialect.PostgreSQL81Dialect;
 import org.hibernate.dialect.PostgreSQL82Dialect;
 import org.hibernate.dialect.PostgreSQL9Dialect;
 import org.hibernate.dialect.PostgresPlusDialect;
 import org.hibernate.dialect.ProgressDialect;
 import org.hibernate.dialect.SAPDBDialect;
 import org.hibernate.dialect.SQLServer2005Dialect;
 import org.hibernate.dialect.SQLServer2008Dialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.Sybase11Dialect;
 import org.hibernate.dialect.SybaseASE157Dialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
 import org.hibernate.dialect.TeradataDialect;
 import org.hibernate.dialect.TimesTenDialect;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.jta.platform.internal.BitronixJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.BorlandEnterpriseServerJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JBossAppServerJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JBossStandAloneJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JOTMJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JOnASJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.JRun4JtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.OC4JJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.OrionJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.ResinJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.SunOneJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.WebSphereExtendedJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.WebSphereJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.internal.WeblogicJtaPlatform;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.event.internal.EntityCopyAllowedLoggedObserver;
 import org.hibernate.event.internal.EntityCopyAllowedObserver;
 import org.hibernate.event.internal.EntityCopyNotAllowedObserver;
 import org.hibernate.event.spi.EntityCopyObserver;
-import org.hibernate.hql.spi.GlobalTemporaryTableBulkIdStrategy;
-import org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
-import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
-import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
 
 import org.jboss.logging.Logger;
 
 /**
  * Builder for StrategySelector instances.
  *
  * @author Steve Ebersole
  */
 public class StrategySelectorBuilder {
 	private static final Logger log = Logger.getLogger( StrategySelectorBuilder.class );
 
 	private final List<StrategyRegistration> explicitStrategyRegistrations = new ArrayList<StrategyRegistration>();
 
 	/**
 	 * Adds an explicit (as opposed to discovered) strategy registration.
 	 *
 	 * @param strategy The strategy
 	 * @param implementation The strategy implementation
 	 * @param name The registered name
 	 * @param <T> The type of the strategy.  Used to make sure that the strategy and implementation are type
 	 * compatible.
 	 */
 	@SuppressWarnings("unchecked")
 	public <T> void addExplicitStrategyRegistration(Class<T> strategy, Class<? extends T> implementation, String name) {
 		addExplicitStrategyRegistration( new SimpleStrategyRegistrationImpl<T>( strategy, implementation, name ) );
 	}
 
 	/**
 	 * Adds an explicit (as opposed to discovered) strategy registration.
 	 *
 	 * @param strategyRegistration The strategy implementation registration.
 	 * @param <T> The type of the strategy.  Used to make sure that the strategy and implementation are type
 	 * compatible.
 	 */
 	public <T> void addExplicitStrategyRegistration(StrategyRegistration<T> strategyRegistration) {
 		if ( !strategyRegistration.getStrategyRole().isInterface() ) {
 			// not good form...
 			log.debug( "Registering non-interface strategy : " + strategyRegistration.getStrategyRole().getName()  );
 		}
 
 		if ( ! strategyRegistration.getStrategyRole().isAssignableFrom( strategyRegistration.getStrategyImplementation() ) ) {
 			throw new StrategySelectionException(
 					"Implementation class [" + strategyRegistration.getStrategyImplementation().getName()
 							+ "] does not implement strategy interface ["
 							+ strategyRegistration.getStrategyRole().getName() + "]"
 			);
 		}
 		explicitStrategyRegistrations.add( strategyRegistration );
 	}
 
 	/**
 	 * Builds the selector.
 	 *
 	 * @param classLoaderService The class loading service used to (attempt to) resolve any un-registered
 	 * strategy implementations.
 	 *
 	 * @return The selector.
 	 */
 	public StrategySelector buildSelector(ClassLoaderService classLoaderService) {
 		final StrategySelectorImpl strategySelector = new StrategySelectorImpl( classLoaderService );
 
 		// build the baseline...
 		addDialects( strategySelector );
 		addJtaPlatforms( strategySelector );
 		addTransactionFactories( strategySelector );
 		addMultiTableBulkIdStrategies( strategySelector );
 		addEntityCopyObserverStrategies( strategySelector );
 
 		// apply auto-discovered registrations
 		for ( StrategyRegistrationProvider provider : classLoaderService.loadJavaServices( StrategyRegistrationProvider.class ) ) {
 			for ( StrategyRegistration discoveredStrategyRegistration : provider.getStrategyRegistrations() ) {
 				applyFromStrategyRegistration( strategySelector, discoveredStrategyRegistration );
 			}
 		}
 
 		// apply customizations
 		for ( StrategyRegistration explicitStrategyRegistration : explicitStrategyRegistrations ) {
 			applyFromStrategyRegistration( strategySelector, explicitStrategyRegistration );
 		}
 
 		return strategySelector;
 	}
 
 	@SuppressWarnings("unchecked")
 	private <T> void applyFromStrategyRegistration(StrategySelectorImpl strategySelector, StrategyRegistration<T> strategyRegistration) {
 		for ( String name : strategyRegistration.getSelectorNames() ) {
 			strategySelector.registerStrategyImplementor(
 					strategyRegistration.getStrategyRole(),
 					name,
 					strategyRegistration.getStrategyImplementation()
 			);
 		}
 	}
 
 	private void addDialects(StrategySelectorImpl strategySelector) {
 		addDialect( strategySelector, Cache71Dialect.class );
 		addDialect( strategySelector, CUBRIDDialect.class );
 		addDialect( strategySelector, DB2Dialect.class );
 		addDialect( strategySelector, DB2390Dialect.class );
 		addDialect( strategySelector, DB2400Dialect.class );
 		addDialect( strategySelector, DerbyTenFiveDialect.class );
 		addDialect( strategySelector, DerbyTenSixDialect.class );
 		addDialect( strategySelector, DerbyTenSevenDialect.class );
 		addDialect( strategySelector, FirebirdDialect.class );
 		addDialect( strategySelector, FrontBaseDialect.class );
 		addDialect( strategySelector, H2Dialect.class );
 		addDialect( strategySelector, HSQLDialect.class );
 		addDialect( strategySelector, InformixDialect.class );
 		addDialect( strategySelector, IngresDialect.class );
 		addDialect( strategySelector, Ingres9Dialect.class );
 		addDialect( strategySelector, Ingres10Dialect.class );
 		addDialect( strategySelector, InterbaseDialect.class );
 		addDialect( strategySelector, JDataStoreDialect.class );
 		addDialect( strategySelector, MckoiDialect.class );
 		addDialect( strategySelector, MimerSQLDialect.class );
 		addDialect( strategySelector, MySQL5Dialect.class );
 		addDialect( strategySelector, MySQL5InnoDBDialect.class );
 		addDialect( strategySelector, MySQL5Dialect.class );
 		addDialect( strategySelector, MySQL5InnoDBDialect.class );
 		addDialect( strategySelector, Oracle8iDialect.class );
 		addDialect( strategySelector, Oracle9iDialect.class );
 		addDialect( strategySelector, Oracle10gDialect.class );
 		addDialect( strategySelector, PointbaseDialect.class );
 		addDialect( strategySelector, PostgresPlusDialect.class );
 		addDialect( strategySelector, PostgreSQL81Dialect.class );
 		addDialect( strategySelector, PostgreSQL82Dialect.class );
 		addDialect( strategySelector, PostgreSQL9Dialect.class );
 		addDialect( strategySelector, ProgressDialect.class );
 		addDialect( strategySelector, SAPDBDialect.class );
 		addDialect( strategySelector, SQLServerDialect.class );
 		addDialect( strategySelector, SQLServer2005Dialect.class );
 		addDialect( strategySelector, SQLServer2008Dialect.class );
 		addDialect( strategySelector, Sybase11Dialect.class );
 		addDialect( strategySelector, SybaseAnywhereDialect.class );
 		addDialect( strategySelector, SybaseASE15Dialect.class );
 		addDialect( strategySelector, SybaseASE157Dialect.class );
 		addDialect( strategySelector, TeradataDialect.class );
 		addDialect( strategySelector, TimesTenDialect.class );
 	}
 
 	private void addDialect(StrategySelectorImpl strategySelector, Class<? extends Dialect> dialectClass) {
 		String simpleName = dialectClass.getSimpleName();
 		if ( simpleName.endsWith( "Dialect" ) ) {
 			simpleName = simpleName.substring( 0, simpleName.length() - "Dialect".length() );
 		}
 		strategySelector.registerStrategyImplementor( Dialect.class, simpleName, dialectClass );
 	}
 
 	private void addJtaPlatforms(StrategySelectorImpl strategySelector) {
 		addJtaPlatforms(
 				strategySelector,
 				BorlandEnterpriseServerJtaPlatform.class,
 				"Borland",
 				"org.hibernate.service.jta.platform.internal.BorlandEnterpriseServerJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				BitronixJtaPlatform.class,
 				"Bitronix",
 				"org.hibernate.service.jta.platform.internal.BitronixJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JBossAppServerJtaPlatform.class,
 				"JBossAS",
 				"org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JBossStandAloneJtaPlatform.class,
 				"JBossTS",
 				"org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JOnASJtaPlatform.class,
 				"JOnAS",
 				"org.hibernate.service.jta.platform.internal.JOnASJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JOTMJtaPlatform.class,
 				"JOTM",
 				"org.hibernate.service.jta.platform.internal.JOTMJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				JRun4JtaPlatform.class,
 				"JRun4",
 				"org.hibernate.service.jta.platform.internal.JRun4JtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				OC4JJtaPlatform.class,
 				"OC4J",
 				"org.hibernate.service.jta.platform.internal.OC4JJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				OrionJtaPlatform.class,
 				"Orion",
 				"org.hibernate.service.jta.platform.internal.OrionJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				ResinJtaPlatform.class,
 				"Resin",
 				"org.hibernate.service.jta.platform.internal.ResinJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				SunOneJtaPlatform.class,
 				"SunOne",
 				"org.hibernate.service.jta.platform.internal.SunOneJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				WeblogicJtaPlatform.class,
 				"Weblogic",
 				"org.hibernate.service.jta.platform.internal.WeblogicJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				WebSphereJtaPlatform.class,
 				"WebSphere",
 				"org.hibernate.service.jta.platform.internal.WebSphereJtaPlatform"
 		);
 
 		addJtaPlatforms(
 				strategySelector,
 				WebSphereExtendedJtaPlatform.class,
 				"WebSphereExtended",
 				"org.hibernate.service.jta.platform.internal.WebSphereExtendedJtaPlatform"
 		);
 	}
 
 	private void addJtaPlatforms(StrategySelectorImpl strategySelector, Class<? extends JtaPlatform> impl, String... names) {
 		for ( String name : names ) {
 			strategySelector.registerStrategyImplementor( JtaPlatform.class, name, impl );
 		}
 	}
 
 	private void addTransactionFactories(StrategySelectorImpl strategySelector) {
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, JdbcTransactionFactory.SHORT_NAME, JdbcTransactionFactory.class );
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, "org.hibernate.transaction.JDBCTransactionFactory", JdbcTransactionFactory.class );
 
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, JtaTransactionFactory.SHORT_NAME, JtaTransactionFactory.class );
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, "org.hibernate.transaction.JTATransactionFactory", JtaTransactionFactory.class );
 
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, CMTTransactionFactory.SHORT_NAME, CMTTransactionFactory.class );
 		strategySelector.registerStrategyImplementor( TransactionFactory.class, "org.hibernate.transaction.CMTTransactionFactory", CMTTransactionFactory.class );
 	}
 
 	private void addMultiTableBulkIdStrategies(StrategySelectorImpl strategySelector) {
 		strategySelector.registerStrategyImplementor(
 				MultiTableBulkIdStrategy.class,
 				PersistentTableBulkIdStrategy.SHORT_NAME,
 				PersistentTableBulkIdStrategy.class
 		);
 		strategySelector.registerStrategyImplementor(
 				MultiTableBulkIdStrategy.class,
 				GlobalTemporaryTableBulkIdStrategy.SHORT_NAME,
 				GlobalTemporaryTableBulkIdStrategy.class
 		);
 		strategySelector.registerStrategyImplementor(
 				MultiTableBulkIdStrategy.class,
 				LocalTemporaryTableBulkIdStrategy.SHORT_NAME,
 				LocalTemporaryTableBulkIdStrategy.class
 		);
-		strategySelector.registerStrategyImplementor(
-				MultiTableBulkIdStrategy.class,
-				TemporaryTableBulkIdStrategy.SHORT_NAME,
-				TemporaryTableBulkIdStrategy.class
-		);
 	}
 
 	private void addEntityCopyObserverStrategies(StrategySelectorImpl strategySelector) {
 		strategySelector.registerStrategyImplementor(
 				EntityCopyObserver.class,
 				EntityCopyNotAllowedObserver.SHORT_NAME,
 				EntityCopyNotAllowedObserver.class
 		);
 		strategySelector.registerStrategyImplementor(
 				EntityCopyObserver.class,
 				EntityCopyAllowedObserver.SHORT_NAME,
 				EntityCopyAllowedObserver.class
 		);
 		strategySelector.registerStrategyImplementor(
 				EntityCopyObserver.class,
 				EntityCopyAllowedLoggedObserver.SHORT_NAME,
 				EntityCopyAllowedLoggedObserver.class
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java b/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java
index defb08c5d5..11d741e597 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/spi/SessionFactoryOptions.java
@@ -1,187 +1,186 @@
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
 
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Interceptor;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.SchemaAutoTooling;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cfg.BaselineSessionEventsListenerBuilder;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.function.SQLFunction;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Aggregator of special options used to build the SessionFactory.
  *
  * @since 5.0
  */
 public interface SessionFactoryOptions {
 	/**
 	 * The service registry to use in building the factory.
 	 *
 	 * @return The service registry to use.
 	 */
 	public StandardServiceRegistry getServiceRegistry();
 
 	public Object getBeanManagerReference();
 
 	public Object getValidatorFactoryReference();
 
 	/**
 	 * The name to be used for the SessionFactory.  This is use both in:<ul>
 	 *     <li>in-VM serialization</li>
 	 *     <li>JNDI binding, depending on {@link #isSessionFactoryNameAlsoJndiName}</li>
 	 * </ul>
 	 *
 	 * @return The SessionFactory name
 	 */
 	public String getSessionFactoryName();
 
 	/**
 	 * Is the {@link #getSessionFactoryName SesssionFactory name} also a JNDI name, indicating we
 	 * should bind it into JNDI?
 	 *
 	 * @return {@code true} if the SessionFactory name is also a JNDI name; {@code false} otherwise.
 	 */
 	public boolean isSessionFactoryNameAlsoJndiName();
 
 	public boolean isFlushBeforeCompletionEnabled();
 
 	public boolean isAutoCloseSessionEnabled();
 
 	public boolean isStatisticsEnabled();
 
 	/**
 	 * Get the interceptor to use by default for all sessions opened from this factory.
 	 *
 	 * @return The interceptor to use factory wide.  May be {@code null}
 	 */
 	public Interceptor getInterceptor();
 
 	public SessionFactoryObserver[] getSessionFactoryObservers();
 
 	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder();
 
 	public boolean isIdentifierRollbackEnabled();
 
 	public EntityMode getDefaultEntityMode();
 
 	public EntityTuplizerFactory getEntityTuplizerFactory();
 
 	public boolean isCheckNullability();
 
 	public boolean isInitializeLazyStateOutsideTransactionsEnabled();
 
 	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy();
 
+	public TempTableDdlTransactionHandling getTempTableDdlTransactionHandling();
+
 	public BatchFetchStyle getBatchFetchStyle();
 
 	public int getDefaultBatchFetchSize();
 
 	public Integer getMaximumFetchDepth();
 
 	public NullPrecedence getDefaultNullPrecedence();
 
 	public boolean isOrderUpdatesEnabled();
 
 	public boolean isOrderInsertsEnabled();
 
 	public MultiTenancyStrategy getMultiTenancyStrategy();
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
 	public boolean isJtaTrackByThread();
 
 	public Map getQuerySubstitutions();
 
 	public boolean isStrictJpaQueryLanguageCompliance();
 
 	public boolean isNamedQueryStartupCheckingEnabled();
 
 	public boolean isSecondLevelCacheEnabled();
 
 	public boolean isQueryCacheEnabled();
 
 	public QueryCacheFactory getQueryCacheFactory();
 
 	public String getCacheRegionPrefix();
 
 	public boolean isMinimalPutsEnabled();
 
 	public boolean isStructuredCacheEntriesEnabled();
 
 	public boolean isDirectReferenceCacheEntriesEnabled();
 
 	public boolean isAutoEvictCollectionCache();
 
 	public SchemaAutoTooling getSchemaAutoTooling();
 
-	public boolean isDataDefinitionImplicitCommit();
-
-	public boolean isDataDefinitionInTransactionSupported();
-
 	public int getJdbcBatchSize();
 
 	public boolean isJdbcBatchVersionedData();
 
 	public boolean isScrollableResultSetsEnabled();
 
 	public boolean isWrapResultSetsEnabled();
 
 	public boolean isGetGeneratedKeysEnabled();
 
 	public Integer getJdbcFetchSize();
 
 	public ConnectionReleaseMode getConnectionReleaseMode();
 
 	public boolean isCommentsEnabled();
 
 
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 	public EntityNameResolver[] getEntityNameResolvers();
 
 	/**
 	 * Get the delegate for handling entity-not-found exception conditions.
 	 *
 	 * @return The specific EntityNotFoundDelegate to use,  May be {@code null}
 	 */
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public Map<String, SQLFunction> getCustomSqlFunctionMap();
 
 	void setCheckNullability(boolean enabled);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 81715168df..b194245c84 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,340 +1,332 @@
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
 import org.hibernate.boot.Metadata;
 import org.hibernate.boot.SchemaAutoTooling;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 import org.jboss.logging.Logger;
 
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
  * @author Steve Ebersole
  *
  * @deprecated Use {@link org.hibernate.boot.spi.SessionFactoryOptions} instead.
  */
 @Deprecated
 public final class Settings {
 	private static final Logger LOG = Logger.getLogger( Settings.class );
 
 	private final SessionFactoryOptions sessionFactoryOptions;
 	private final String defaultCatalogName;
 	private final String defaultSchemaName;
 
 	public Settings(SessionFactoryOptions sessionFactoryOptions) {
 		this( sessionFactoryOptions, null, null );
 	}
 
 	public Settings(SessionFactoryOptions sessionFactoryOptions, Metadata metadata) {
 		this(
 				sessionFactoryOptions,
 				extractName( metadata.getDatabase().getDefaultSchema().getName().getCatalog() ),
 				extractName( metadata.getDatabase().getDefaultSchema().getName().getSchema() )
 		);
 	}
 
 	private static String extractName(Identifier identifier) {
 		return identifier == null ? null : identifier.render();
 	}
 
 	public Settings(SessionFactoryOptions sessionFactoryOptions, String defaultCatalogName, String defaultSchemaName) {
 		this.sessionFactoryOptions = sessionFactoryOptions;
 		this.defaultCatalogName = defaultCatalogName;
 		this.defaultSchemaName = defaultSchemaName;
 
 		final boolean debugEnabled =  LOG.isDebugEnabled();
 
 		if ( debugEnabled ) {
 			LOG.debugf( "SessionFactory name : %s", sessionFactoryOptions.getSessionFactoryName() );
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled( sessionFactoryOptions.isFlushBeforeCompletionEnabled() ) );
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled( sessionFactoryOptions.isAutoCloseSessionEnabled() ) );
 
 			LOG.debugf( "Statistics: %s", enabledDisabled( sessionFactoryOptions.isStatisticsEnabled() ) );
 
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled( sessionFactoryOptions.isIdentifierRollbackEnabled() ) );
 			LOG.debugf( "Default entity-mode: %s", sessionFactoryOptions.getDefaultEntityMode() );
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled( sessionFactoryOptions.isCheckNullability() ) );
 			LOG.debugf( "Allow initialization of lazy state outside session : %s", enabledDisabled( sessionFactoryOptions.isInitializeLazyStateOutsideTransactionsEnabled() ) );
 
 			LOG.debugf( "Using BatchFetchStyle : " + sessionFactoryOptions.getBatchFetchStyle().name() );
 			LOG.debugf( "Default batch fetch size: %s", sessionFactoryOptions.getDefaultBatchFetchSize() );
 			LOG.debugf( "Maximum outer join fetch depth: %s", sessionFactoryOptions.getMaximumFetchDepth() );
 			LOG.debugf( "Default null ordering: %s", sessionFactoryOptions.getDefaultNullPrecedence() );
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled( sessionFactoryOptions.isOrderUpdatesEnabled() ) );
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled( sessionFactoryOptions.isOrderInsertsEnabled() ) );
 
 			LOG.debugf( "multi-tenancy strategy : %s", sessionFactoryOptions.getMultiTenancyStrategy() );
 
 			LOG.debugf( "JTA Track by Thread: %s", enabledDisabled( sessionFactoryOptions.isJtaTrackByThread() ) );
 
 			LOG.debugf( "Query language substitutions: %s", sessionFactoryOptions.getQuerySubstitutions() );
 			LOG.debugf( "JPA query language strict compliance: %s", enabledDisabled( sessionFactoryOptions.isStrictJpaQueryLanguageCompliance() ) );
 			LOG.debugf( "Named query checking : %s", enabledDisabled( sessionFactoryOptions.isNamedQueryStartupCheckingEnabled() ) );
 
 			LOG.debugf( "Second-level cache: %s", enabledDisabled( sessionFactoryOptions.isSecondLevelCacheEnabled() ) );
 			LOG.debugf( "Second-level query cache: %s", enabledDisabled( sessionFactoryOptions.isQueryCacheEnabled() ) );
 			LOG.debugf( "Second-level query cache factory: %s", sessionFactoryOptions.getQueryCacheFactory() );
 			LOG.debugf( "Second-level cache region prefix: %s", sessionFactoryOptions.getCacheRegionPrefix() );
 			LOG.debugf( "Optimize second-level cache for minimal puts: %s", enabledDisabled( sessionFactoryOptions.isMinimalPutsEnabled() ) );
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled( sessionFactoryOptions.isStructuredCacheEntriesEnabled() ) );
 			LOG.debugf( "Second-level cache direct-reference entries: %s", enabledDisabled( sessionFactoryOptions.isDirectReferenceCacheEntriesEnabled() ) );
 			LOG.debugf( "Automatic eviction of collection cache: %s", enabledDisabled( sessionFactoryOptions.isAutoEvictCollectionCache() ) );
 
 			LOG.debugf( "JDBC batch size: %s", sessionFactoryOptions.getJdbcBatchSize() );
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled( sessionFactoryOptions.isJdbcBatchVersionedData() ) );
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled( sessionFactoryOptions.isScrollableResultSetsEnabled() ) );
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled( sessionFactoryOptions.isWrapResultSetsEnabled() ) );
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled( sessionFactoryOptions.isGetGeneratedKeysEnabled() ) );
 			LOG.debugf( "JDBC result set fetch size: %s", sessionFactoryOptions.getJdbcFetchSize() );
 			LOG.debugf( "Connection release mode: %s", sessionFactoryOptions.getConnectionReleaseMode() );
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled( sessionFactoryOptions.isCommentsEnabled() ) );
 		}
 	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	public String getDefaultSchemaName() {
 		return defaultSchemaName;
 	}
 
 	public String getDefaultCatalogName() {
 		return defaultCatalogName;
 	}
 
 	public String getSessionFactoryName() {
 		return sessionFactoryOptions.getSessionFactoryName();
 	}
 
 	public boolean isSessionFactoryNameAlsoJndiName() {
 		return sessionFactoryOptions.isSessionFactoryNameAlsoJndiName();
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return sessionFactoryOptions.isFlushBeforeCompletionEnabled();
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return sessionFactoryOptions.isAutoCloseSessionEnabled();
 	}
 
 	public boolean isStatisticsEnabled() {
 		return sessionFactoryOptions.isStatisticsEnabled();
 	}
 
 	public BaselineSessionEventsListenerBuilder getBaselineSessionEventsListenerBuilder() {
 		return sessionFactoryOptions.getBaselineSessionEventsListenerBuilder();
 	}
 
 	public boolean isIdentifierRollbackEnabled() {
 		return sessionFactoryOptions.isIdentifierRollbackEnabled();
 	}
 
 	public EntityMode getDefaultEntityMode() {
 		return sessionFactoryOptions.getDefaultEntityMode();
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return sessionFactoryOptions.getEntityTuplizerFactory();
 	}
 
 	public boolean isCheckNullability() {
 		return sessionFactoryOptions.isCheckNullability();
 	}
 
 	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
 		return sessionFactoryOptions.isInitializeLazyStateOutsideTransactionsEnabled();
 	}
 
 	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
 		return sessionFactoryOptions.getMultiTableBulkIdStrategy();
 	}
 
 	public BatchFetchStyle getBatchFetchStyle() {
 		return sessionFactoryOptions.getBatchFetchStyle();
 	}
 
 	public int getDefaultBatchFetchSize() {
 		return sessionFactoryOptions.getDefaultBatchFetchSize();
 	}
 
 	public Integer getMaximumFetchDepth() {
 		return sessionFactoryOptions.getMaximumFetchDepth();
 	}
 
 	public NullPrecedence getDefaultNullPrecedence() {
 		return sessionFactoryOptions.getDefaultNullPrecedence();
 	}
 
 	public boolean isOrderUpdatesEnabled() {
 		return sessionFactoryOptions.isOrderUpdatesEnabled();
 	}
 
 	public boolean isOrderInsertsEnabled() {
 		return sessionFactoryOptions.isOrderInsertsEnabled();
 	}
 
 	public MultiTenancyStrategy getMultiTenancyStrategy() {
 		return sessionFactoryOptions.getMultiTenancyStrategy();
 	}
 	public boolean isJtaTrackByThread() {
 		return sessionFactoryOptions.isJtaTrackByThread();
 	}
 
 	public Map getQuerySubstitutions() {
 		return sessionFactoryOptions.getQuerySubstitutions();
 	}
 
 	public boolean isStrictJPAQLCompliance() {
 		return sessionFactoryOptions.isStrictJpaQueryLanguageCompliance();
 	}
 
 	public boolean isNamedQueryStartupCheckingEnabled() {
 		return sessionFactoryOptions.isNamedQueryStartupCheckingEnabled();
 	}
 
 	public boolean isSecondLevelCacheEnabled() {
 		return sessionFactoryOptions.isSecondLevelCacheEnabled();
 	}
 
 	public boolean isQueryCacheEnabled() {
 		return sessionFactoryOptions.isQueryCacheEnabled();
 	}
 
 	public QueryCacheFactory getQueryCacheFactory() {
 		return sessionFactoryOptions.getQueryCacheFactory();
 	}
 
 	public String getCacheRegionPrefix() {
 		return sessionFactoryOptions.getCacheRegionPrefix();
 	}
 
 	public boolean isMinimalPutsEnabled() {
 		return sessionFactoryOptions.isMinimalPutsEnabled();
 	}
 
 	public boolean isStructuredCacheEntriesEnabled() {
 		return sessionFactoryOptions.isStructuredCacheEntriesEnabled();
 	}
 
 	public boolean isDirectReferenceCacheEntriesEnabled() {
 		return sessionFactoryOptions.isDirectReferenceCacheEntriesEnabled();
 	}
 
 	public boolean isAutoEvictCollectionCache() {
 		return sessionFactoryOptions.isAutoEvictCollectionCache();
 	}
 
 	public boolean isAutoCreateSchema() {
 		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE
 				|| sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_DROP;
 	}
 
 	public boolean isAutoDropSchema() {
 		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.CREATE_DROP;
 	}
 
 	public boolean isAutoUpdateSchema() {
 		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.UPDATE;
 	}
 
 	public boolean isAutoValidateSchema() {
 		return sessionFactoryOptions.getSchemaAutoTooling() == SchemaAutoTooling.VALIDATE;
 	}
 
-	public boolean isDataDefinitionImplicitCommit() {
-		return sessionFactoryOptions.isDataDefinitionImplicitCommit();
-	}
-
-	public boolean isDataDefinitionInTransactionSupported() {
-		return sessionFactoryOptions.isDataDefinitionInTransactionSupported();
-	}
-
 	public int getJdbcBatchSize() {
 		return sessionFactoryOptions.getJdbcBatchSize();
 	}
 
 	public boolean isJdbcBatchVersionedData() {
 		return sessionFactoryOptions.isJdbcBatchVersionedData();
 	}
 
 	public Integer getJdbcFetchSize() {
 		return sessionFactoryOptions.getJdbcFetchSize();
 	}
 
 	public boolean isScrollableResultSetsEnabled() {
 		return sessionFactoryOptions.isScrollableResultSetsEnabled();
 	}
 
 	public boolean isWrapResultSetsEnabled() {
 		return sessionFactoryOptions.isWrapResultSetsEnabled();
 	}
 
 	public boolean isGetGeneratedKeysEnabled() {
 		return sessionFactoryOptions.isGetGeneratedKeysEnabled();
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return sessionFactoryOptions.getConnectionReleaseMode();
 	}
 
 	public boolean isCommentsEnabled() {
 		return sessionFactoryOptions.isCommentsEnabled();
 	}
 
 	public RegionFactory getRegionFactory() {
 		return sessionFactoryOptions.getServiceRegistry().getService( RegionFactory.class );
 	}
 
 	public JtaPlatform getJtaPlatform() {
 		return sessionFactoryOptions.getServiceRegistry().getService( JtaPlatform.class );
 	}
 
 	public QueryTranslatorFactory getQueryTranslatorFactory() {
 		return sessionFactoryOptions.getServiceRegistry().getService( QueryTranslatorFactory.class );
 	}
 
 	public void setCheckNullability(boolean enabled) {
 		// ugh, used by org.hibernate.cfg.beanvalidation.TypeSafeActivator as part of the BV integrator
 		sessionFactoryOptions.setCheckNullability( enabled );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java
index 43b16b586c..24c185c8b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractHANADialect.java
@@ -1,701 +1,709 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect;
 
 import java.io.FilterReader;
 import java.io.Reader;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.ScrollMode;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AnsiTrimFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.jdbc.CharacterStream;
 import org.hibernate.engine.jdbc.ClobImplementer;
 import org.hibernate.engine.jdbc.NClobImplementer;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.SQLGrammarException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
+import org.hibernate.hql.spi.id.IdTableSupport;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An abstract base class for HANA dialects. <br/>
  * <a href="http://help.sap.com/hana/html/sqlmain.html">SAP HANA Reference</a> <br/>
  *
  * NOTE: This dialect is currently configured to create foreign keys with
  * <code>on update cascade</code>.
  *
  * @author Andrew Clemons <andrew.clemons@sap.com>
  */
 public abstract class AbstractHANADialect extends Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return new StringBuilder( sql.length() + 20 ).append( sql )
 					.append( hasOffset ? " limit ? offset ?" : " limit ?" ).toString();
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 	};
 
 	private static class CloseSuppressingReader extends FilterReader {
 		protected CloseSuppressingReader(final Reader in) {
 			super( in );
 		}
 
 		@Override
 		public void close() {
 			// do not close
 		}
 	}
 
 	// the ClobTypeDescriptor and NClobTypeDescriptor for HANA are slightly
 	// changed from the standard ones. The HANA JDBC driver currently closes any
 	// stream passed in via
 	// PreparedStatement.setCharacterStream(int,Reader,long)
 	// after the stream has been processed. this causes problems later if we are
 	// using non-contexual lob creation and HANA then closes our StringReader.
 	// see test case LobLocatorTest
 
 	private static final ClobTypeDescriptor HANA_CLOB_STREAM_BINDING = new ClobTypeDescriptor() {
 		/** serial version uid. */
 		private static final long serialVersionUID = -379042275442752102L;
 
 		@Override
 		public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(final PreparedStatement st, final X value, final int index,
 						final WrapperOptions options) throws SQLException {
 					final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class,
 							options );
 
 					if ( value instanceof ClobImplementer ) {
 						st.setCharacterStream( index, new CloseSuppressingReader( characterStream.asReader() ),
 								characterStream.getLength() );
 					}
 					else {
 						st.setCharacterStream( index, characterStream.asReader(), characterStream.getLength() );
 					}
 
 				}
 			};
 		}
 	};
 
 	private static final NClobTypeDescriptor HANA_NCLOB_STREAM_BINDING = new NClobTypeDescriptor() {
 		/** serial version uid. */
 		private static final long serialVersionUID = 5651116091681647859L;
 
 		@Override
 		public <X> BasicBinder<X> getNClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(final PreparedStatement st, final X value, final int index,
 						final WrapperOptions options) throws SQLException {
 					final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class,
 							options );
 
 					if ( value instanceof NClobImplementer ) {
 						st.setCharacterStream( index, new CloseSuppressingReader( characterStream.asReader() ),
 								characterStream.getLength() );
 					}
 					else {
 						st.setCharacterStream( index, characterStream.asReader(), characterStream.getLength() );
 					}
 
 				}
 			};
 		}
 	};
 
 	public AbstractHANADialect() {
 		super();
 
 		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 
 		// varbinary max length 5000
 		registerColumnType( Types.BINARY, 5000, "varbinary($l)" );
 		registerColumnType( Types.VARBINARY, 5000, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, 5000, "varbinary($l)" );
 
 		// for longer values, map to blob
 		registerColumnType( Types.BINARY, "blob" );
 		registerColumnType( Types.VARBINARY, "blob" );
 		registerColumnType( Types.LONGVARBINARY, "blob" );
 
 		registerColumnType( Types.CHAR, "varchar(1)" );
 		registerColumnType( Types.VARCHAR, 5000, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, 5000, "varchar($l)" );
 		registerColumnType( Types.NVARCHAR, 5000, "nvarchar($l)" );
 
 		// for longer values map to clob/nclob
 		registerColumnType( Types.LONGVARCHAR, "clob" );
 		registerColumnType( Types.VARCHAR, "clob" );
 		registerColumnType( Types.NVARCHAR, "nclob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.BOOLEAN, "tinyint" );
 
 		// map bit/tinyint to smallint since tinyint is unsigned on HANA
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 
 		registerHibernateType( Types.NCLOB, StandardBasicTypes.NCLOB.getName() );
 		registerHibernateType( Types.NVARCHAR, StandardBasicTypes.STRING.getName() );
 
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.DATE ) );
 		registerFunction( "to_seconddate", new StandardSQLFunction( "to_seconddate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "to_time", new StandardSQLFunction( "to_time", StandardBasicTypes.TIME ) );
 		registerFunction( "to_timestamp", new StandardSQLFunction( "to_timestamp", StandardBasicTypes.TIMESTAMP ) );
 
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP,
 				false ) );
 		registerFunction( "current_utcdate", new NoArgSQLFunction( "current_utcdate", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_utctime", new NoArgSQLFunction( "current_utctime", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_utctimestamp", new NoArgSQLFunction( "current_utctimestamp",
 				StandardBasicTypes.TIMESTAMP, false ) );
 
 		registerFunction( "add_days", new StandardSQLFunction( "add_days" ) );
 		registerFunction( "add_months", new StandardSQLFunction( "add_months" ) );
 		registerFunction( "add_seconds", new StandardSQLFunction( "add_seconds" ) );
 		registerFunction( "add_years", new StandardSQLFunction( "add_years" ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "days_between", new StandardSQLFunction( "days_between", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "isoweek", new StandardSQLFunction( "isoweek", StandardBasicTypes.STRING ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "localtoutc", new StandardSQLFunction( "localtoutc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "next_day", new StandardSQLFunction( "next_day", StandardBasicTypes.DATE ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP, true ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.STRING ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "seconds_between", new StandardSQLFunction( "seconds_between", StandardBasicTypes.LONG ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "utctolocal", new StandardSQLFunction( "utctolocal", StandardBasicTypes.TIMESTAMP ) );
 
 		registerFunction( "to_bigint", new StandardSQLFunction( "to_bigint", StandardBasicTypes.LONG ) );
 		registerFunction( "to_binary", new StandardSQLFunction( "to_binary", StandardBasicTypes.BINARY ) );
 		registerFunction( "to_decimal", new StandardSQLFunction( "to_decimal", StandardBasicTypes.BIG_DECIMAL ) );
 		registerFunction( "to_double", new StandardSQLFunction( "to_double", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "to_int", new StandardSQLFunction( "to_int", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_integer", new StandardSQLFunction( "to_integer", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_real", new StandardSQLFunction( "to_real", StandardBasicTypes.FLOAT ) );
 		registerFunction( "to_smalldecimal",
 				new StandardSQLFunction( "to_smalldecimal", StandardBasicTypes.BIG_DECIMAL ) );
 		registerFunction( "to_smallint", new StandardSQLFunction( "to_smallint", StandardBasicTypes.SHORT ) );
 		registerFunction( "to_tinyint", new StandardSQLFunction( "to_tinyint", StandardBasicTypes.BYTE ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bin2hex", new StandardSQLFunction( "bin2hex", StandardBasicTypes.STRING ) );
 		registerFunction( "bitand", new StandardSQLFunction( "bitand", StandardBasicTypes.LONG ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "greatest", new StandardSQLFunction( "greatest" ) );
 		registerFunction( "hex2bin", new StandardSQLFunction( "hex2bin", StandardBasicTypes.BINARY ) );
 		registerFunction( "least", new StandardSQLFunction( "least" ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power" ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "uminus", new StandardSQLFunction( "uminus" ) );
 
 		registerFunction( "to_alphanum", new StandardSQLFunction( "to_alphanum", StandardBasicTypes.STRING ) );
 		registerFunction( "to_nvarchar", new StandardSQLFunction( "to_nvarchar", StandardBasicTypes.STRING ) );
 		registerFunction( "to_varchar", new StandardSQLFunction( "to_varchar", StandardBasicTypes.STRING ) );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.LONG ) );
 		registerFunction( "locate", new StandardSQLFunction( "locate", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "nchar", new StandardSQLFunction( "nchar", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "substr_after", new StandardSQLFunction( "substr_after", StandardBasicTypes.STRING ) );
 		registerFunction( "substr_before", new StandardSQLFunction( "substr_before", StandardBasicTypes.STRING ) );
 		registerFunction( "substring", new StandardSQLFunction( "substring", StandardBasicTypes.STRING ) );
 		registerFunction( "trim", new AnsiTrimFunction() );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "unicode", new StandardSQLFunction( "unicode", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "length(to_binary(?1))*8" ) );
 
 		registerFunction( "to_blob", new StandardSQLFunction( "to_blob", StandardBasicTypes.BLOB ) );
 		registerFunction( "to_clob", new StandardSQLFunction( "to_clob", StandardBasicTypes.CLOB ) );
 		registerFunction( "to_nclob", new StandardSQLFunction( "to_nclob", StandardBasicTypes.NCLOB ) );
 
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "current_connection", new NoArgSQLFunction( "current_connection", StandardBasicTypes.INTEGER,
 				false ) );
 		registerFunction( "current_schema", new NoArgSQLFunction( "current_schema", StandardBasicTypes.STRING, false ) );
 		registerFunction( "current_user", new NoArgSQLFunction( "current_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "grouping_id", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "(", ",", ")" ) );
 		registerFunction( "ifnull", new StandardSQLFunction( "ifnull" ) );
 		registerFunction( "map", new StandardSQLFunction( "map" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "session_context", new StandardSQLFunction( "session_context" ) );
 		registerFunction( "session_user", new NoArgSQLFunction( "session_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "sysuuid", new NoArgSQLFunction( "sysuuid", StandardBasicTypes.STRING, false ) );
 
 		registerHanaKeywords();
 
 		// createBlob() and createClob() are not supported by the HANA JDBC driver
 		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(final SQLException sqlException, final String message, final String sql) {
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 				if ( errorCode == 131 ) {
 					// 131 - Transaction rolled back by lock wait timeout
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( errorCode == 146 ) {
 					// 146 - Resource busy and acquire with NOWAIT specified
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( errorCode == 132 ) {
 					// 132 - Transaction rolled back due to unavailable resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				if ( errorCode == 133 ) {
 					// 133 - Transaction rolled back by detected deadlock
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				// 259 - Invalid table name
 				// 260 - Invalid column name
 				// 261 - Invalid index name
 				// 262 - Invalid query name
 				// 263 - Invalid alias name
 				if ( errorCode == 257 || ( errorCode >= 259 && errorCode <= 263 ) ) {
 					throw new SQLGrammarException( message, sqlException, sql );
 				}
 
 				// 257 - Cannot insert NULL or update to NULL
 				// 301 - Unique constraint violated
 				// 461 - foreign key constraint violation
 				// 462 - failed on update or delete by foreign key constraint violation
 				if ( errorCode == 287 || errorCode == 301 || errorCode == 461 || errorCode == 462 ) {
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName(
 							sqlException );
 
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add (";
 	}
 
 	@Override
 	public String getAddColumnSuffixString() {
 		return ")";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
 	@Override
 	public String getCreateSequenceString(final String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create global temporary table";
+					}
+				},
+				AfterUseAction.CLEAN
+		);
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select current_timestamp from dummy";
 	}
 
 	@Override
 	public String getDropSequenceString(final String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getForUpdateString(final String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateString(final String aliases, final LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		final Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode> entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan( lockMode ) ) {
 				lockMode = lm;
 			}
 		}
 
 		// not sure why this is sometimes empty
 		if ( aliases == null || "".equals( aliases ) ) {
 			return getForUpdateString( lockMode );
 		}
 
 		return getForUpdateString( lockMode ) + " of " + aliases;
 	}
 
 	@Override
 	public String getLimitString(final String sql, final boolean hasOffset) {
 		return new StringBuilder( sql.length() + 20 ).append( sql )
 				.append( hasOffset ? " limit ? offset ?" : " limit ?" ).toString();
 	}
 
 	@Override
 	public String getNotExpression(final String expression) {
 		return "not (" + expression + ")";
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from sys.sequences";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(final String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getSequenceNextValString(final String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dummy";
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(final int sqlCode) {
 		switch ( sqlCode ) {
 		case Types.BOOLEAN:
 			return BitTypeDescriptor.INSTANCE;
 		case Types.CLOB:
 			return HANA_CLOB_STREAM_BINDING;
 		case Types.NCLOB:
 			return HANA_NCLOB_STREAM_BINDING;
 		case Types.TINYINT:
 			// tinyint is unsigned on HANA
 			return SmallIntTypeDescriptor.INSTANCE;
 		default:
 			return super.getSqlTypeDescriptorOverride( sqlCode );
 		}
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	protected void registerHanaKeywords() {
 		registerKeyword( "all" );
 		registerKeyword( "alter" );
 		registerKeyword( "as" );
 		registerKeyword( "before" );
 		registerKeyword( "begin" );
 		registerKeyword( "both" );
 		registerKeyword( "case" );
 		registerKeyword( "char" );
 		registerKeyword( "condition" );
 		registerKeyword( "connect" );
 		registerKeyword( "cross" );
 		registerKeyword( "cube" );
 		registerKeyword( "current_connection" );
 		registerKeyword( "current_date" );
 		registerKeyword( "current_schema" );
 		registerKeyword( "current_time" );
 		registerKeyword( "current_timestamp" );
 		registerKeyword( "current_user" );
 		registerKeyword( "current_utcdate" );
 		registerKeyword( "current_utctime" );
 		registerKeyword( "current_utctimestamp" );
 		registerKeyword( "currval" );
 		registerKeyword( "cursor" );
 		registerKeyword( "declare" );
 		registerKeyword( "distinct" );
 		registerKeyword( "else" );
 		registerKeyword( "elseif" );
 		registerKeyword( "elsif" );
 		registerKeyword( "end" );
 		registerKeyword( "except" );
 		registerKeyword( "exception" );
 		registerKeyword( "exec" );
 		registerKeyword( "for" );
 		registerKeyword( "from" );
 		registerKeyword( "full" );
 		registerKeyword( "group" );
 		registerKeyword( "having" );
 		registerKeyword( "if" );
 		registerKeyword( "in" );
 		registerKeyword( "inner" );
 		registerKeyword( "inout" );
 		registerKeyword( "intersect" );
 		registerKeyword( "into" );
 		registerKeyword( "is" );
 		registerKeyword( "join" );
 		registerKeyword( "leading" );
 		registerKeyword( "left" );
 		registerKeyword( "limit" );
 		registerKeyword( "loop" );
 		registerKeyword( "minus" );
 		registerKeyword( "natural" );
 		registerKeyword( "nextval" );
 		registerKeyword( "null" );
 		registerKeyword( "on" );
 		registerKeyword( "order" );
 		registerKeyword( "out" );
 		registerKeyword( "prior" );
 		registerKeyword( "return" );
 		registerKeyword( "returns" );
 		registerKeyword( "reverse" );
 		registerKeyword( "right" );
 		registerKeyword( "rollup" );
 		registerKeyword( "rowid" );
 		registerKeyword( "select" );
 		registerKeyword( "set" );
 		registerKeyword( "sql" );
 		registerKeyword( "start" );
 		registerKeyword( "sysdate" );
 		registerKeyword( "systime" );
 		registerKeyword( "systimestamp" );
 		registerKeyword( "sysuuid" );
 		registerKeyword( "top" );
 		registerKeyword( "trailing" );
 		registerKeyword( "union" );
 		registerKeyword( "using" );
 		registerKeyword( "utcdate" );
 		registerKeyword( "utctime" );
 		registerKeyword( "utctimestamp" );
 		registerKeyword( "values" );
 		registerKeyword( "when" );
 		registerKeyword( "where" );
 		registerKeyword( "while" );
 		registerKeyword( "with" );
 	}
 
 	@Override
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		// HANA does not support circular constraints
 		return false;
 	}
 
 	@Override
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.FORWARD_ONLY;
 	}
 
 	/**
 	 * HANA currently does not support check constraints.
 	 */
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		// http://scn.sap.com/thread/3221812
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return true;
 	}
 
 	@Override
 	public int getMaxAliasLength() {
 		return 128;
 	}
 
 	/**
 	 * The default behaviour for 'on update restrict' on HANA is currently
 	 * to not allow any updates to any column of a row if the row has a 
 	 * foreign key. Make the default for foreign keys have 'on update cascade'
 	 * to work around the issue.
 	 */
 	@Override
 	public String getAddForeignKeyConstraintString(final String constraintName, final String[] foreignKey,
 			final String referencedTable, final String[] primaryKey, final boolean referencesPrimaryKey) {
 		return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey) + " on update cascade";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
index b30013b626..9886cb90bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
@@ -1,309 +1,313 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CharIndexFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An abstract base class for Sybase and MS SQL Server dialects.
  *
  * @author Gavin King
  */
 abstract class AbstractTransactSQLDialect extends Dialect {
 	public AbstractTransactSQLDialect() {
 		super();
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.BIGINT, "numeric(19,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "datetime" );
 		registerColumnType( Types.TIME, "datetime" );
 		registerColumnType( Types.TIMESTAMP, "datetime" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "image" );
 		registerColumnType( Types.CLOB, "text" );
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.LONG ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "str", new StandardSQLFunction( "str", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "getdate", StandardBasicTypes.DATE ) );
 
 		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "getutcdate", new NoArgSQLFunction( "getutcdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "datename", new StandardSQLFunction( "datename", StandardBasicTypes.STRING ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "square", new StandardSQLFunction( "square" ) );
 		registerFunction( "rand", new StandardSQLFunction( "rand", StandardBasicTypes.FLOAT ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		registerFunction( "isnull", new StandardSQLFunction( "isnull" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 
 		registerFunction( "length", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "ltrim(rtrim(?1))" ) );
 		registerFunction( "locate", new CharIndexFunction() );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return "";
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select @@identity";
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		//starts with 1, implicitly
 		return "identity not null";
 	}
 
 	@Override
 	public boolean supportsInsertSelectIdentity() {
 		return true;
 	}
 
 	@Override
 	public String appendIdentitySelectToInsert(String insertSQL) {
 		return insertSQL + "\nselect @@identity";
 	}
 
 	@Override
 	public String appendLockHint(LockOptions lockOptions, String tableName) {
 		return lockOptions.getLockMode().greaterThan( LockMode.READ ) ? tableName + " holdlock" : tableName;
 	}
 
 	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
 		// TODO:  merge additional lockoptions support in Dialect.applyLocksToSql
 		final Iterator itr = aliasedLockOptions.getAliasLockIterator();
 		final StringBuilder buffer = new StringBuilder( sql );
 		int correction = 0;
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final LockMode lockMode = (LockMode) entry.getValue();
 			if ( lockMode.greaterThan( LockMode.READ ) ) {
 				final String alias = (String) entry.getKey();
 				int start = -1;
 				int end = -1;
 				if ( sql.endsWith( " " + alias ) ) {
 					start = ( sql.length() - alias.length() ) + correction;
 					end = start + alias.length();
 				}
 				else {
 					int position = sql.indexOf( " " + alias + " " );
 					if ( position <= -1 ) {
 						position = sql.indexOf( " " + alias + "," );
 					}
 					if ( position > -1 ) {
 						start = position + correction + 1;
 						end = start + alias.length();
 					}
 				}
 
 				if ( start > -1 ) {
 					final String lockHint = appendLockHint( lockMode, alias );
 					buffer.replace( start, end, lockHint );
 					correction += ( lockHint.length() - alias.length() );
 				}
 			}
 		}
 		return buffer.toString();
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// sql server just returns automatically
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		// This assumes you will want to ignore any update counts
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
 
 		// You may still have other ResultSets or update counts left to process here
 		// but you can't do it now or the ResultSet you just got will be closed
 		return ps.getResultSet();
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select getdate()";
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		return "#" + baseTableName;
-	}
-
-	@Override
-	public boolean dropTemporaryTableAfterUse() {
-		// sql-server, at least needed this dropped after use; strange!
-		return true;
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new LocalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						return "#" + baseName;
+					}
+				},
+				// sql-server, at least needed this dropped after use; strange!
+				AfterUseAction.DROP,
+				TempTableDdlTransactionHandling.NONE
+		);
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select newid()";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsTuplesInSubqueries() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index 1cfb478853..c5ff5bceac 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,722 +1,711 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.ConditionalParenthesisFunction;
 import org.hibernate.dialect.function.ConvertFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.TopLimitHandler;
 import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.CacheJoinFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Cach&eacute; 2007.1 dialect.
  *
  * This class is required in order to use Hibernate with Intersystems Cach&eacute; SQL.  Compatible with
  * Cach&eacute; 2007.1.
  *
  * <h2>PREREQUISITES</h2>
  * These setup instructions assume that both Cach&eacute; and Hibernate are installed and operational.
  * <br>
  * <h2>HIBERNATE DIRECTORIES AND FILES</h2>
  * JBoss distributes the InterSystems Cache' dialect for Hibernate 3.2.1
  * For earlier versions of Hibernate please contact
  * <a href="http://www.intersystems.com/support/cache-support.html">InterSystems Worldwide Response Center</A> (WRC)
  * for the appropriate source files.
  * <br>
  * <h2>CACH&Eacute; DOCUMENTATION</h2>
  * Documentation for Cach&eacute; is available online when Cach&eacute; is running.
  * It can also be obtained from the
  * <a href="http://www.intersystems.com/cache/downloads/documentation.html">InterSystems</A> website.
  * The book, "Object-oriented Application Development Using the Cach&eacute; Post-relational Database:
  * is also available from Springer-Verlag.
  * <br>
  * <h2>HIBERNATE DOCUMENTATION</h2>
  * Hibernate comes with extensive electronic documentation.
  * In addition, several books on Hibernate are available from
  * <a href="http://www.manning.com">Manning Publications Co</a>.
  * Three available titles are "Hibernate Quickly", "Hibernate in Action", and "Java Persistence with Hibernate".
  * <br>
  * <h2>TO SET UP HIBERNATE FOR USE WITH CACH&Eacute;</h2>
  * The following steps assume that the directory where Cach&eacute; was installed is C:\CacheSys.
  * This is the default installation directory for  Cach&eacute;.
  * The default installation directory for Hibernate is assumed to be C:\Hibernate.
  * <p/>
  * If either product is installed in a different location, the pathnames that follow should be modified appropriately.
  * <p/>
  * Cach&eacute; version 2007.1 and above is recommended for use with
  * Hibernate.  The next step depends on the location of your
  * CacheDB.jar depending on your version of Cach&eacute;.
  * <ol>
  * <li>Copy C:\CacheSys\dev\java\lib\JDK15\CacheDB.jar to C:\Hibernate\lib\CacheDB.jar.</li>
  * <p/>
  * <li>Insert the following files into your Java classpath:
  * <p/>
  * <ul>
  * <li>All jar files in the directory C:\Hibernate\lib</li>
  * <li>The directory (or directories) where hibernate.properties and/or hibernate.cfg.xml are kept.</li>
  * </ul>
  * </li>
  * <p/>
  * <li>In the file, hibernate.properties (or hibernate.cfg.xml),
  * specify the Cach&eacute; dialect and the Cach&eacute; version URL settings.</li>
  * </ol>
  * <p/>
  * For example, in Hibernate 3.2, typical entries in hibernate.properties would have the following
  * "name=value" pairs:
  * <p/>
  * <table cols=3 border cellpadding=5 cellspacing=0>
  * <tr>
  * <th>Property Name</th>
  * <th>Property Value</th>
  * </tr>
  * <tr>
  * <td>hibernate.dialect</td>
  * <td>org.hibernate.dialect.Cache71Dialect</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.driver_class</td>
  * <td>com.intersys.jdbc.CacheDriver</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.username</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.password</td>
  * <td>(see note 1)</td>
  * </tr>
  * <tr>
  * <td>hibernate.connection.url</td>
  * <td>jdbc:Cache://127.0.0.1:1972/USER</td>
  * </tr>
  * </table>
  * <p/>
  * <b>NOTE:</b> Please contact your administrator for the userid and password you should use when
  *         attempting access via JDBC.  By default, these are chosen to be "_SYSTEM" and "SYS" respectively
  *         as noted in the SQL standard.
  * <br>
  * <h2>CACH&Eacute; VERSION URL</h2>
  * This is the standard URL for the JDBC driver.
  * For a JDBC driver on the machine hosting Cach&eacute;, use the IP "loopback" address, 127.0.0.1.
  * For 1972, the default port, specify the super server port of your Cach&eacute; instance.
  * For USER, substitute the NAMESPACE which contains your Cach&eacute; database data.
  * <br>
  * <h2>CACH&Eacute; DIALECTS</h2>
  * Choices for Dialect are:
  * <br>
  * <p/>
  * <ol>
  * <li>org.hibernate.dialect.Cache71Dialect (requires Cach&eacute;
  * 2007.1 or above)</li>
  * <p/>
  * </ol>
  * <br>
  * <h2>SUPPORT FOR IDENTITY COLUMNS</h2>
  * Cach&eacute; 2007.1 or later supports identity columns.  For
  * Hibernate to use identity columns, specify "native" as the
  * generator.
  * <br>
  * <h2>SEQUENCE DIALECTS SUPPORT SEQUENCES</h2>
  * <p/>
  * To use Hibernate sequence support with Cach&eacute; in a namespace, you must FIRST load the following file into that namespace:
  * <pre>
  *     etc\CacheSequences.xml
  * </pre>
  * For example, at the COS terminal prompt in the namespace, run the
  * following command:
  * <p>
  * d LoadFile^%apiOBJ("c:\hibernate\etc\CacheSequences.xml","ck")
  * <p>
  * In your Hibernate mapping you can specify sequence use.
  * <p>
  * For example, the following shows the use of a sequence generator in a Hibernate mapping:
  * <pre>
  *     &lt;id name="id" column="uid" type="long" unsaved-value="null"&gt;
  *         &lt;generator class="sequence"/&gt;
  *     &lt;/id&gt;
  * </pre>
  * <br>
  * <p/>
  * Some versions of Hibernate under some circumstances call
  * getSelectSequenceNextValString() in the dialect.  If this happens
  * you will receive the error message: new MappingException( "Dialect
  * does not support sequences" ).
  * <br>
  * <h2>HIBERNATE FILES ASSOCIATED WITH CACH&Eacute; DIALECT</h2>
  * The following files are associated with Cach&eacute; dialect:
  * <p/>
  * <ol>
  * <li>src\org\hibernate\dialect\Cache71Dialect.java</li>
  * <li>src\org\hibernate\dialect\function\ConditionalParenthesisFunction.java</li>
  * <li>src\org\hibernate\dialect\function\ConvertFunction.java</li>
  * <li>src\org\hibernate\exception\CacheSQLStateConverter.java</li>
  * <li>src\org\hibernate\sql\CacheJoinFragment.java</li>
  * </ol>
  * Cache71Dialect ships with Hibernate 3.2.  All other dialects are distributed by InterSystems and subclass Cache71Dialect.
  *
  * @author Jonathan Levinson
  */
 
 public class Cache71Dialect extends Dialect {
 
 	private final TopLimitHandler limitHandler;
 
 	/**
 	 * Creates new <code>Cache71Dialect</code> instance. Sets up the JDBC /
 	 * Cach&eacute; type mappings.
 	 */
 	public Cache71Dialect() {
 		super();
 		commonRegistration();
 		register71Functions();
 		this.limitHandler = new TopLimitHandler(true, true);
 	}
 
 	protected final void commonRegistration() {
 		// Note: For object <-> SQL datatype mappings see:
 		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
 		//
 		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
 		// changed 08-11-2005, jsl
 		registerColumnType( Types.BINARY, "varbinary($1)" );
 		registerColumnType( Types.BIGINT, "BigInt" );
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.VARBINARY, "longvarbinary" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.BLOB, "longvarbinary" );
 		registerColumnType( Types.CLOB, "longvarchar" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardJDBCEscapeFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 		registerFunction( "convert", new ConvertFunction() );
 		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP )
 		);
 		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "database", new StandardJDBCEscapeFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "dateadd", new VarArgsSQLFunction( StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")" ) );
 		registerFunction( "datediff", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datediff(", ",", ")" ) );
 		registerFunction( "datename", new VarArgsSQLFunction( StandardBasicTypes.STRING, "datename(", ",", ")" ) );
 		registerFunction( "datepart", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datepart(", ",", ")" ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		// is it necessary to register %exact since it can only appear in a where clause?
 		registerFunction( "%exact", new StandardSQLFunction( "%exact", StandardBasicTypes.STRING ) );
 		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%external", new StandardSQLFunction( "%external", StandardBasicTypes.STRING ) );
 		registerFunction( "$extract", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$extract(", ",", ")" ) );
 		registerFunction( "$find", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$find(", ",", ")" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "getdate", new StandardSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
 		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
 		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
 		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardJDBCEscapeFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
 		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
 		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
 		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
 		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
 		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
 		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", StandardBasicTypes.INTEGER ) );
 		registerFunction( "locate", new StandardSQLFunction( "$FIND", StandardBasicTypes.INTEGER ) );
 		registerFunction( "log", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "month", new StandardJDBCEscapeFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
 		registerFunction( "nvl", new NvlFunction() );
 		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
 		registerFunction( "%pattern", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%pattern", "" ) );
 		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "$piece", new VarArgsSQLFunction( StandardBasicTypes.STRING, "$piece(", ",", ")" ) );
 		registerFunction( "position", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "position(", " in ", ")" ) );
 		registerFunction( "power", new VarArgsSQLFunction( StandardBasicTypes.STRING, "power(", ",", ")" ) );
 		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "repeat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "repeat(", ",", ")" ) );
 		registerFunction( "replicate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "replicate(", ",", ")" ) );
 		registerFunction( "right", new StandardJDBCEscapeFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "round", new VarArgsSQLFunction( StandardBasicTypes.FLOAT, "round(", ",", ")" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "second", new StandardJDBCEscapeFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "%sqlstring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlstring(", ",", ")" ) );
 		registerFunction( "%sqlupper", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlupper(", ",", ")" ) );
 		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "%startswith", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%startswith", "" ) );
 		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as char varying)" ) );
 		registerFunction( "string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "string(", ",", ")" ) );
 		// note that %string is deprecated
 		registerFunction( "%string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%string(", ",", ")" ) );
 		registerFunction( "substr", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substr(", ",", ")" ) );
 		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tochar", new VarArgsSQLFunction( StandardBasicTypes.STRING, "tochar(", ",", ")" ) );
 		registerFunction( "to_char", new VarArgsSQLFunction( StandardBasicTypes.STRING, "to_char(", ",", ")" ) );
 		registerFunction( "todate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "to_date", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
 		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
 		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
 		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
 		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
 		//registerFunction( "trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 from ?3)") );
 		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		// %upper is deprecated
 		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
 		registerFunction( "user", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.INTEGER ) );
 		registerFunction( "xmlconcat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlconcat(", ",", ")" ) );
 		registerFunction( "xmlelement", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlelement(", ",", ")" ) );
 		// xmlforest requires a new kind of function constructor
 		registerFunction( "year", new StandardJDBCEscapeFunction( "year", StandardBasicTypes.INTEGER ) );
 	}
 
 	protected final void register71Functions() {
 		this.registerFunction( "str", new VarArgsSQLFunction( StandardBasicTypes.STRING, "str(", ",", ")" ) );
 	}
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean hasAlterTable() {
 		// Does this dialect support the ALTER TABLE syntax?
 		return true;
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		// Do we need to qualify index names with the schema name?
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("StringBufferReplaceableByString")
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		// The syntax used to add a foreign key constraint to a table.
 		return new StringBuilder( 300 )
 				.append( " ADD CONSTRAINT " )
 				.append( constraintName )
 				.append( " FOREIGN KEY " )
 				.append( constraintName )
 				.append( " (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") REFERENCES " )
 				.append( referencedTable )
 				.append( " (" )
 				.append( StringHelper.join( ", ", primaryKey ) )
 				.append( ") " )
 				.toString();
 	}
 
 	/**
 	 * Does this dialect support check constraints?
 	 *
 	 * @return {@code false} (Cache does not support check constraints)
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean supportsCheck() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		// The syntax used to add a column to a table
 		return " add column";
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		// Completely optional cascading drop clause.
 		return "";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		// Do we need to drop constraints before dropping tables in this dialect?
 		return true;
 	}
 
 	@Override
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	@Override
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return true;
 	}
 
-
-	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		final String name = super.generateTemporaryTableName( baseTableName );
-		return name.length() > 25 ? name.substring( 1, 25 ) : name;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
-	}
-
-	@Override
-	public Boolean performTemporaryTableDDLInIsolation() {
-		return Boolean.FALSE;
-	}
-
 	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "";
-	}
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						final String name = super.generateIdTableName( baseName );
+						return name.length() > 25 ? name.substring( 1, 25 ) : name;
+					}
 
-	@Override
-	public boolean dropTemporaryTableAfterUse() {
-		return true;
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create global temporary table";
+					}
+				},
+				AfterUseAction.DROP
+		);
 	}
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return IdentityGenerator.class;
 	}
 
 	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
 		// data type
 		return true;
 	}
 
 	@Override
 	public String getIdentityColumnString() throws MappingException {
 		// The keyword used to specify an identity column, if identity column key generation is supported.
 		return "identity";
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
 	}
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsSequences() {
 		return false;
 	}
 
 // It really does support sequences, but InterSystems elects to suggest usage of IDENTITY instead :/
 // Anyway, below are the actual support overrides for users wanting to use this combo...
 //
 //	public String getSequenceNextValString(String sequenceName) {
 //		return "select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getSelectSequenceNextValString(String sequenceName) {
 //		return "(select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getCreateSequenceString(String sequenceName) {
 //		return "insert into InterSystems.Sequences(Name) values (ucase('" + sequenceName + "'))";
 //	}
 //
 //	public String getDropSequenceString(String sequenceName) {
 //		return "delete from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
 //	}
 //
 //	public String getQuerySequencesString() {
 //		return "select name from InterSystems.Sequences";
 //	}
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// InterSystems Cache' does not current support "SELECT ... FOR UPDATE" syntax...
 		// Set your transaction mode to READ_COMMITTED before using
 		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC) {
 			return new OptimisticLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// LIMIT support (ala TOP) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return limitHandler;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean bindLimitParametersFirst() {
 		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hasOffset ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 
 		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
 		// but this extension is not supported through Hibernate anyway.
 		final int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
 
 		return new StringBuilder( sql.length() + 8 )
 				.append( sql )
 				.insert( insertionPoint, " TOP ? " )
 				.toString();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getLowercaseFunction() {
 		// The name of the SQL function that transforms a string to lowercase
 		return "lower";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		// The keyword used to specify a nullable column.
 		return " null";
 	}
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		// Create an OuterJoinGenerator for this dialect.
 		return new CacheJoinFragment();
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		// The keyword used to insert a row without specifying
 		// any column values
 		return " default values";
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new CacheSQLExceptionConversionDelegate( this );
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * The Cache ViolatedConstraintNameExtracter.
 	 */
 	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 		}
 	};
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
index ee9cf32cd3..f4a27ef300 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/DB2Dialect.java
@@ -1,579 +1,586 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.JDBCException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.unique.DB2UniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An SQL dialect for DB2.
  *
  * @author Gavin King
  */
 public class DB2Dialect extends Dialect {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( DB2Dialect.class );
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			if (LimitHelper.hasFirstRow( selection )) {
 				//nest the main query in an outer select
 				return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
 						+ sql + " fetch first ? rows only ) as inner2_ ) as inner1_ where rownumber_ > "
 						+ "? order by rownumber_";
 			}
 			return sql + " fetch first ? rows only";
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean supportsVariableLimit() {
 			return false;
 		}
 	};
 
 
 	private final UniqueDelegate uniqueDelegate;
 
 	/**
 	 * Constructs a DB2Dialect
 	 */
 	public DB2Dialect() {
 		super();
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "varchar($l) for bit data" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.BLOB, "blob($l)" );
 		registerColumnType( Types.CLOB, "clob($l)" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARBINARY, "long varchar for bit data" );
 		registerColumnType( Types.BINARY, "varchar($l) for bit data" );
 		registerColumnType( Types.BINARY, 254, "char($l) for bit data" );
 		registerColumnType( Types.BOOLEAN, "smallint" );
 
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "absval", new StandardSQLFunction( "absval" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "float", new StandardSQLFunction( "float", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction( "stddev", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "variance", new StandardSQLFunction( "variance", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "julian_day", new StandardSQLFunction( "julian_day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 		registerFunction(
 				"midnight_seconds",
 				new StandardSQLFunction( "midnight_seconds", StandardBasicTypes.INTEGER )
 		);
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek_iso", new StandardSQLFunction( "dayofweek_iso", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "days", new StandardSQLFunction( "days", StandardBasicTypes.LONG ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction(
 				"current_timestamp",
 				new NoArgSQLFunction( "current timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "timestamp_iso", new StandardSQLFunction( "timestamp_iso", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week_iso", new StandardSQLFunction( "week_iso", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "double", new StandardSQLFunction( "double", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "varchar", new StandardSQLFunction( "varchar", StandardBasicTypes.STRING ) );
 		registerFunction( "real", new StandardSQLFunction( "real", StandardBasicTypes.FLOAT ) );
 		registerFunction( "bigint", new StandardSQLFunction( "bigint", StandardBasicTypes.LONG ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "integer", new StandardSQLFunction( "integer", StandardBasicTypes.INTEGER ) );
 		registerFunction( "smallint", new StandardSQLFunction( "smallint", StandardBasicTypes.SHORT ) );
 
 		registerFunction( "digits", new StandardSQLFunction( "digits", StandardBasicTypes.STRING ) );
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "posstr", new StandardSQLFunction( "posstr", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "length(?1)*8" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "rtrim(char(?1))" ) );
 
 		registerKeyword( "current" );
 		registerKeyword( "date" );
 		registerKeyword( "time" );
 		registerKeyword( "timestamp" );
 		registerKeyword( "fetch" );
 		registerKeyword( "first" );
 		registerKeyword( "rows" );
 		registerKeyword( "only" );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 		
 		uniqueDelegate = new DB2UniqueDelegate( this );
 	}
 
 	@Override
 	public String getLowercaseFunction() {
 		return "lcase";
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "values identity_val_local()";
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		return "generated by default as identity";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return "default";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "values nextval for " + sequenceName;
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select seqname from sysibm.syssequences";
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getLimitString(String sql, int offset, int limit) {
 		if ( offset == 0 ) {
 			return sql + " fetch first " + limit + " rows only";
 		}
 		//nest the main query in an outer select
 		return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( "
 				+ sql + " fetch first " + limit + " rows only ) as inner2_ ) as inner1_ where rownumber_ > "
 				+ offset + " order by rownumber_";
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 *
 	 * DB2 does have a one-based offset, however this was actually already handled in the limit string building
 	 * (the '?+1' bit).  To not mess up inheritors, I'll leave that part alone and not touch the offset here.
 	 */
 	@Override
 	@SuppressWarnings("deprecation")
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public String getForUpdateString() {
 		return " for read only with rs use and keep update locks";
 	}
 
 	@Override
 	@SuppressWarnings("deprecation")
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		//as far as I know, DB2 doesn't support this
 		return false;
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "'x'";
 				break;
 			case Types.DATE:
 				literal = "'2000-1-1'";
 				break;
 			case Types.TIMESTAMP:
 				literal = "'2000-1-1 00:00:00'";
 				break;
 			case Types.TIME:
 				literal = "'00:00:00'";
 				break;
 			default:
 				literal = "0";
 		}
 		return "nullif(" + literal + ',' + literal + ')';
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		// This assumes you will want to ignore any update counts 
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
 
 		return ps.getResultSet();
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						return "session." + super.generateIdTableName( baseName );
+					}
 
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "declare global temporary table";
-	}
+					@Override
+					public String getCreateIdTableCommand() {
+						return "declare global temporary table";
+					}
 
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "not logged";
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		return "session." + super.generateTemporaryTableName( baseTableName );
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "not logged";
+					}
+				},
+				AfterUseAction.CLEAN
+		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "values current timestamp";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * NOTE : DB2 is know to support parameters in the <tt>SELECT</tt> clause, but only in casted form
 	 * (see {@link #requiresCastingOfParametersInSelectClause()}).
 	 */
 	@Override
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * DB2 in fact does require that parameters appearing in the select clause be wrapped in cast() calls
 	 * to tell the DB parser the type of the select value.
 	 */
 	@Override
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return false;
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		//DB2 v9.1 doesn't support 'cross join' syntax
 		return ", ";
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? SmallIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 				if( -952 == errorCode && "57014".equals( sqlState )){
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				return null;
 			}
 		};
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	/**
 	 * Handle DB2 "support" for null precedence...
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nullPrecedence Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 *
 	 * @return
 	 */
 	@Override
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nullPrecedence) {
 		if ( nullPrecedence == null || nullPrecedence == NullPrecedence.NONE ) {
 			return super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE );
 		}
 
 		// DB2 FTW!  A null precedence was explicitly requested, but DB2 "support" for null precedence
 		// is a joke.  Basically it supports combos that align with what it does anyway.  Here is the
 		// support matrix:
 		//		* ASC + NULLS FIRST -> case statement
 		//		* ASC + NULLS LAST -> just drop the NULLS LAST from sql fragment
 		//		* DESC + NULLS FIRST -> just drop the NULLS FIRST from sql fragment
 		//		* DESC + NULLS LAST -> case statement
 
 		if ( ( nullPrecedence == NullPrecedence.FIRST  && "desc".equalsIgnoreCase( order ) )
 				|| ( nullPrecedence == NullPrecedence.LAST && "asc".equalsIgnoreCase( order ) ) ) {
 			// we have one of:
 			//		* ASC + NULLS LAST
 			//		* DESC + NULLS FIRST
 			// so just drop the null precedence.  *NOTE: we could pass along the null precedence here,
 			// but only DB2 9.7 or greater understand it; dropping it is more portable across DB2 versions
 			return super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE );
 		}
 
 		return String.format(
 				Locale.ENGLISH,
 				"case when %s is null then %s else %s end, %s %s",
 				expression,
 				nullPrecedence == NullPrecedence.FIRST ? "0" : "1",
 				nullPrecedence == NullPrecedence.FIRST ? "1" : "0",
 				expression,
 				order
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index cddb071ce6..0227172982 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,2827 +1,2731 @@
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
 package org.hibernate.dialect;
 
+import java.io.InputStream;
+import java.io.OutputStream;
+import java.sql.Blob;
+import java.sql.CallableStatement;
+import java.sql.Clob;
+import java.sql.NClob;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import java.util.HashMap;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Locale;
+import java.util.Map;
+import java.util.Properties;
+import java.util.Set;
+
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.ScrollMode;
 import org.hibernate.boot.model.TypeContributions;
 import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
 import org.hibernate.boot.model.relational.Sequence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
 import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
 import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
-import org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
-import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Table;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.tool.schema.internal.StandardAuxiliaryDatabaseObjectExporter;
 import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
 import org.hibernate.tool.schema.internal.StandardIndexExporter;
 import org.hibernate.tool.schema.internal.StandardSequenceExporter;
 import org.hibernate.tool.schema.internal.StandardTableExporter;
 import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;
-import org.hibernate.tool.schema.internal.TemporaryTableExporter;
 import org.hibernate.tool.schema.spi.Exporter;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
-import org.jboss.logging.Logger;
 
-import java.io.InputStream;
-import java.io.OutputStream;
-import java.sql.Blob;
-import java.sql.CallableStatement;
-import java.sql.Clob;
-import java.sql.NClob;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.HashMap;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Locale;
-import java.util.Map;
-import java.util.Properties;
-import java.util.Set;
+import org.jboss.logging.Logger;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
  * with different systems.  Subclasses should provide a public default constructor that register a set of type
  * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 @SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			Dialect.class.getName()
 	);
 
 	/**
 	 * Defines a default batch size constant
 	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
 
 	/**
 	 * Defines a "no batching" batch size constant
 	 */
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 
 	/**
 	 * Characters used as closing for quoting SQL identifiers
 	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 	private final UniqueDelegate uniqueDelegate;
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
 		// register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
 
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
 		final String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
 			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Allows the Dialect to contribute additional types
 	 *
 	 * @param typeContributions Callback to contribute the types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
 		// by default, nothing to do
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		final String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
 		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
 	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the BLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setBinaryStream( 1L );
 					// the BLOB from the detached state
 					final InputStream detachedStream = original.getBinaryStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the CLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the CLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the NCLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the NCLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		final String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format(
 							"No Hibernate type mapping for type [code=%s, length=%s]",
 							code,
 							length
 					)
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase(Locale.ROOT), function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add( word );
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return SequenceStyleGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
 	}
 
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
 	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		if ( getQuerySequencesString() == null ) {
 			return SequenceInformationExtractorNoOpImpl.INSTANCE;
 		}
 		else {
 			return SequenceInformationExtractorLegacyImpl.INSTANCE;
 		}
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns the delegate managing LIMIT clause.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	public LimitHandler getLimitHandler() {
 		return new LegacyLimitHandler( this );
 	}
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		switch ( lockMode ) {
 			case PESSIMISTIC_FORCE_INCREMENT:
 				return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 			case PESSIMISTIC_WRITE:
 				return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
 			case PESSIMISTIC_READ:
 				return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
 			case OPTIMISTIC:
 				return new OptimisticLockingStrategy( lockable, lockMode );
 			case OPTIMISTIC_FORCE_INCREMENT:
 				return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 			default:
 				return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
 		final LockMode lockMode = lockOptions.getLockMode();
 		return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
 	@SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
 		switch ( lockMode ) {
 			case UPGRADE:
 				return getForUpdateString();
 			case PESSIMISTIC_READ:
 				return getReadLockString( timeout );
 			case PESSIMISTIC_WRITE:
 				return getWriteLockString( timeout );
 			case UPGRADE_NOWAIT:
 			case FORCE:
 			case PESSIMISTIC_FORCE_INCREMENT:
 				return getForUpdateNowaitString();
 			case UPGRADE_SKIPLOCKED:
 				return getForUpdateSkipLockedString();
 			default:
 				return "";
 		}
 	}
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings({"unchecked", "UnusedParameters"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		final Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan( lockMode ) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE SKIP LOCKED</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString() {
 		// by default we report no support for SKIP_LOCKED lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list SKIP LOCKED</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE colunm_list SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 * @deprecated use {@code appendLockHint(LockOptions,String)} instead
 	 */
 	@Deprecated
 	public String appendLockHint(LockMode mode, String tableName) {
 		return appendLockHint( new LockOptions( mode ), tableName );
 	}
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param lockOptions The lock options to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockOptions lockOptions, String tableName){
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
-
-	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
-		// mimic the old behavior for now...
-		return supportsTemporaryTables()
-				? LocalTemporaryTableBulkIdStrategy.INSTANCE
-				: new PersistentTableBulkIdStrategy();
+		return new PersistentTableBulkIdStrategy();
 	}
 
-	/**
-	 * Does this dialect support temporary tables?
-	 *
-	 * @return True if temp tables are supported; false otherwise.
-	 */
-	public boolean supportsTemporaryTables() {
-		return false;
-	}
-
-	/**
-	 * Generate a temporary table name given the base table.
-	 *
-	 * @param baseTableName The table name from which to base the temp table name.
-	 * @return The generated temp table name.
-	 */
-	public String generateTemporaryTableName(String baseTableName) {
-		return "HT_" + baseTableName;
-	}
-
-	/**
-	 * Command used to create a temporary table.
-	 *
-	 * @return The command used to create a temporary table.
-	 */
-	public String getCreateTemporaryTableString() {
-		return "create table";
-	}
-
-	/**
-	 * Get any fragments needing to be postfixed to the command for
-	 * temporary table creation.
-	 *
-	 * @return Any required postfix.
-	 */
-	public String getCreateTemporaryTablePostfix() {
-		return "";
-	}
-
-	/**
-	 * Command used to drop a temporary table.
-	 *
-	 * @return The command used to drop a temporary table.
-	 */
-	public String getDropTemporaryTableString() {
-		return "drop table";
-	}
-
-	/**
-	 * Does the dialect require that temporary table DDL statements occur in
-	 * isolation from other statements?  This would be the case if the creation
-	 * would cause any current transaction to get committed implicitly.
-	 * <p/>
-	 * JDBC defines a standard way to query for this information via the
-	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
-	 * method.  However, that does not distinguish between temporary table
-	 * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
-	 * transaction commit via its driver, even though that is not the case for
-	 * temporary table DDL.
-	 * <p/>
-	 * Possible return values and their meanings:<ul>
-	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
-	 * in isolation.</li>
-	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
-	 * temporary table DDL in isolation.</li>
-	 * <li><i>null</i> - defer to the JDBC driver response in regards to
-	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
-	 * </ul>
-	 *
-	 * @return see the result matrix above.
-	 */
-	public Boolean performTemporaryTableDDLInIsolation() {
-		return null;
-	}
-
-	/**
-	 * Do we need to drop the temporary table after use?
-	 *
-	 * @return True if the table should be dropped.
-	 */
-	public boolean dropTemporaryTableAfterUse() {
-		return true;
-	}
-
-
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by position*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by name*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet}.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 *
 	 * @return The SQLExceptionConversionDelegate for this dialect
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * Does this dialect support case insensitive LIKE restrictions?
 	 *
 	 * @return {@code true} if the underlying database supports case insensitive like comparison,
 	 * {@code false} otherwise.  The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 * <p/>
 	 * The maximum here should account for the fact that Hibernate often needs to append "uniqueing" information
 	 * to the end of generated aliases.  That "uniqueing" information will be added to the end of a identifier
 	 * generated to the length specified here; so be sure to leave some room (generally speaking 5 positions will
 	 * suffice).
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private StandardTableExporter tableExporter = new StandardTableExporter( this );
 	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter( this );
 	private StandardIndexExporter indexExporter = new StandardIndexExporter( this );
 	private StandardForeignKeyExporter foreignKeyExporter = new StandardForeignKeyExporter( this );
 	private StandardUniqueKeyExporter uniqueKeyExporter = new StandardUniqueKeyExporter( this );
 	private StandardAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter = new StandardAuxiliaryDatabaseObjectExporter( this );
-	private TemporaryTableExporter temporaryTableExporter = new TemporaryTableExporter( this );
 
 	public Exporter<Table> getTableExporter() {
 		return tableExporter;
 	}
 
-	public Exporter<Table> getTemporaryTableExporter() {
-		return temporaryTableExporter;
-	}
-
 	public Exporter<Sequence> getSequenceExporter() {
 		return sequenceExporter;
 	}
 
 	public Exporter<Index> getIndexExporter() {
 		return indexExporter;
 	}
 
 	public Exporter<ForeignKey> getForeignKeyExporter() {
 		return foreignKeyExporter;
 	}
 
 	public Exporter<Constraint> getUniqueKeyExporter() {
 		return uniqueKeyExporter;
 	}
 
 	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
 		return auxiliaryObjectExporter;
 	}
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation command
 	 */
 	public String getCreateSchemaCommand(String schemaName) {
 		return "create schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop command
 	 */
 	public String getDropSchemaCommand(String schemaName) {
 		return "drop schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to retrieve the current schema name.  Works in conjunction
 	 * with {@link #getSchemaNameResolver()}, unless the return from there does not need this
 	 * information.  E.g., a custom impl might make use of the Java 1.7 addition of
 	 * the {@link java.sql.Connection#getSchema()} method
 	 *
 	 * @return The current schema retrieval SQL
 	 */
 	public String getCurrentSchemaCommand() {
 		return null;
 	}
 
 	/**
 	 * Get the strategy for determining the schema name of a Connection
 	 *
 	 * @return The schema name resolver strategy
 	 */
 	public SchemaNameResolver getSchemaNameResolver() {
 		return DefaultSchemaNameResolver.INSTANCE;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * The syntax for the suffix used to add a column to a table (optional).
 	 *
 	 * @return The suffix "add column" fragment.
 	 */
 	public String getAddColumnSuffixString() {
 		return "";
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
 	/**
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied before the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied after the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied before the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied after the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase(Locale.ROOT) );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support `count(distinct a,b)`?
 	 *
 	 * @return True if the database supports counting distinct tuples; false otherwise.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 
 	/**
 	 * If {@link #supportsTupleDistinctCounts()} is true, does the Dialect require the tuple to be wrapped with parens?
 	 *
 	 * @return boolean
 	 */
 	public boolean requiresParensForTupleDistinctCounts() {
 		return false;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 
 	/**
 	 * Negate an expression
 	 *
 	 * @param expression The expression to negate
 	 *
 	 * @return The negated expression
 	 */
 	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
 	 * Get the UniqueDelegate supported by this dialect
 	 *
 	 * @return The UniqueDelegate
 	 */
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support adding Unique constraints via create and alter table ?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a unique constraint to a table.
 	 *
 	 * @param constraintName The name of the unique constraint.
 	 * @return The "add unique" fragment
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " unique ";
 	}
 
 	/**
 	 * Is the combination of not-null and unique supported?
 	 *
 	 * @return deprecated
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 
 	/**
 	 * Apply a hint to the query.  The entire query is provided, allowing the Dialect full control over the placement
 	 * and syntax of the hint.  By default, ignore the hint and simply return the query.
 	 *
 	 * @param query The query to which to apply the hint.
 	 * @param hints The  hints to apply
 	 * @return The modified SQL
 	 */
 	public String getQueryHintString(String query, List<String> hints) {
 		return query;
 	}
 
 	/**
 	 * Certain dialects support a subset of ScrollModes.  Provide a default to be used by Criteria and Query.
 	 *
 	 * @return ScrollMode
 	 */
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.SCROLL_INSENSITIVE;
 	}
 
 	/**
 	 * Does this dialect support tuples in subqueries?  Ex:
 	 * delete from Table1 where (col1, col2) in (select col1, col2 from Table2)
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsTuplesInSubqueries() {
 		return true;
 	}
 
 	public CallableStatementSupport getCallableStatementSupport() {
 		// most databases do not support returning cursors (ref_cursor)...
 		return StandardCallableStatementSupport.NO_REF_CURSOR_INSTANCE;
 	}
 
 	/**
 	 * By default interpret this based on DatabaseMetaData.
 	 *
 	 * @return
 	 */
 	public NameQualifierSupport getNameQualifierSupport() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
index 0794b26b0e..6c6904336d 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
@@ -1,476 +1,473 @@
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
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
 import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
 import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
 /**
  * A dialect compatible with the H2 database.
  *
  * @author Thomas Mueller
  */
 public class H2Dialect extends Dialect {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			H2Dialect.class.getName()
 	);
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 	};
 
 	private final String querySequenceString;
 	private final SequenceInformationExtractor sequenceInformationExtractor;
 
 	/**
 	 * Constructs a H2Dialect
 	 */
 	public H2Dialect() {
 		super();
 
 		String querySequenceString = "select sequence_name from information_schema.sequences";
 		SequenceInformationExtractor sequenceInformationExtractor = SequenceInformationExtractorH2DatabaseImpl.INSTANCE;
 		try {
 			// HHH-2300
 			final Class h2ConstantsClass = ReflectHelper.classForName( "org.h2.engine.Constants" );
 			final int majorVersion = (Integer) h2ConstantsClass.getDeclaredField( "VERSION_MAJOR" ).get( null );
 			final int minorVersion = (Integer) h2ConstantsClass.getDeclaredField( "VERSION_MINOR" ).get( null );
 			final int buildId = (Integer) h2ConstantsClass.getDeclaredField( "BUILD_ID" ).get( null );
 			if ( buildId < 32 ) {
 				querySequenceString = "select name from information_schema.sequences";
 				sequenceInformationExtractor = SequenceInformationExtractorLegacyImpl.INSTANCE;
 			}
 			if ( ! ( majorVersion > 1 || minorVersion > 2 || buildId >= 139 ) ) {
 				LOG.unsupportedMultiTableBulkHqlJpaql( majorVersion, minorVersion, buildId );
 			}
 		}
 		catch ( Exception e ) {
 			// probably H2 not in the classpath, though in certain app server environments it might just mean we are
 			// not using the correct classloader
 			LOG.undeterminedH2Version();
 		}
 
 		this.querySequenceString = querySequenceString;
 		this.sequenceInformationExtractor = sequenceInformationExtractor;
 
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BINARY, "binary" );
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
 		registerColumnType( Types.NUMERIC, "decimal($p,$s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARBINARY, "binary($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		// Aggregations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		// select topic, syntax from information_schema.help
 		// where section like 'Function%' order by section, topic
 		//
 		// see also ->  http://www.h2database.com/html/functions.html
 
 		// Numeric Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bitand", new StandardSQLFunction( "bitand", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bitor", new StandardSQLFunction( "bitor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bitxor", new StandardSQLFunction( "bitxor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "compress", new StandardSQLFunction( "compress", StandardBasicTypes.BINARY ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "decrypt", new StandardSQLFunction( "decrypt", StandardBasicTypes.BINARY ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "encrypt", new StandardSQLFunction( "encrypt", StandardBasicTypes.BINARY ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "expand", new StandardSQLFunction( "compress", StandardBasicTypes.BINARY ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.BINARY ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "round", new StandardSQLFunction( "round", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "truncate", new StandardSQLFunction( "truncate", StandardBasicTypes.DOUBLE ) );
 
 		// String Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "difference", new StandardSQLFunction( "difference", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw", StandardBasicTypes.STRING ) );
 		registerFunction( "insert", new StandardSQLFunction( "lower", StandardBasicTypes.STRING ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.INTEGER ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex", StandardBasicTypes.STRING ) );
 		registerFunction( "repeat", new StandardSQLFunction( "repeat", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "stringencode", new StandardSQLFunction( "stringencode", StandardBasicTypes.STRING ) );
 		registerFunction( "stringdecode", new StandardSQLFunction( "stringdecode", StandardBasicTypes.STRING ) );
 		registerFunction( "stringtoutf8", new StandardSQLFunction( "stringtoutf8", StandardBasicTypes.BINARY ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase", StandardBasicTypes.STRING ) );
 		registerFunction( "utf8tostring", new StandardSQLFunction( "utf8tostring", StandardBasicTypes.STRING ) );
 
 		// Time and Date Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "curtimestamp", new NoArgSQLFunction( "curtimestamp", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 
 		// System Functions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 
 		getDefaultProperties().setProperty( AvailableSettings.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		// http://code.google.com/p/h2database/issues/detail?id=235
 		getDefaultProperties().setProperty( AvailableSettings.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		// not null is implicit
 		return "generated by default as identity";
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return "null";
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence if exists " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return querySequenceString;
 	}
 
 	@Override
 	public SequenceInformationExtractor getSequenceInformationExtractor() {
 		return sequenceInformationExtractor;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 			// 23000: Check constraint violation: {0}
 			// 23001: Unique index or primary key violation: {0}
 			if ( sqle.getSQLState().startsWith( "23" ) ) {
 				final String message = sqle.getMessage();
 				final int idx = message.indexOf( "violation: " );
 				if ( idx > 0 ) {
 					constraintName = message.substring( idx + "violation: ".length() );
 				}
 			}
 			return constraintName;
 		}
 	};
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		SQLExceptionConversionDelegate delegate = super.buildSQLExceptionConversionDelegate();
 		if (delegate == null) {
 			delegate = new SQLExceptionConversionDelegate() {
 				@Override
 				public JDBCException convert(SQLException sqlException, String message, String sql) {
 					final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 					if (40001 == errorCode) {
 						// DEADLOCK DETECTED
 						return new LockAcquisitionException(message, sqlException, sql);
 					}
 
 					if (50200 == errorCode) {
 						// LOCK NOT AVAILABLE
 						return new PessimisticLockException(message, sqlException, sql);
 					}
 
 					if ( 90006 == errorCode ) {
 						// NULL not allowed for column [90006-145]
 						final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 						return new ConstraintViolationException( message, sqlException, sql, constraintName );
 					}
 
 					return null;
 				}
 			};
 		}
 		return delegate;
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create cached local temporary table if not exists";
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		// actually 2 different options are specified here:
-		//		1) [on commit drop] - says to drop the table on transaction commit
-		//		2) [transactional] - says to not perform an implicit commit of any current transaction
-		return "on commit drop transactional";
-	}
-
-	@Override
-	public Boolean performTemporaryTableDDLInIsolation() {
-		// explicitly create the table using the same connection and transaction
-		return Boolean.FALSE;
-	}
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new LocalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create cached local temporary table if not exists";
+					}
 
-	@Override
-	public boolean dropTemporaryTableAfterUse() {
-		return false;
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						// actually 2 different options are specified here:
+						//		1) [on commit drop] - says to drop the table on transaction commit
+						//		2) [transactional] - says to not perform an implicit commit of any current transaction
+						return "on commit drop transactional";					}
+				},
+				AfterUseAction.CLEAN,
+				TempTableDdlTransactionHandling.NONE
+		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp()";
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean requiresParensForTupleDistinctCounts() {
 		return true;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// see http://groups.google.com/group/h2-database/browse_thread/thread/562d8a49e2dabe99?hl=en
 		return true;
 	}
 	
 	@Override
 	public boolean supportsTuplesInSubqueries() {
 		return false;
 	}
 	
 	@Override
 	public boolean dropConstraints() {
 		// We don't need to drop constraints before dropping tables, that just leads to error
 		// messages about missing tables when we don't have a schema in the database
 		return false;
 	}	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
index 693fd1591b..257ac8ca50 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
@@ -1,706 +1,693 @@
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
 package org.hibernate.dialect;
 
 import java.io.Serializable;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.StaleObjectStateException;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
 /**
  * An SQL dialect compatible with HSQLDB (HyperSQL).
  * <p/>
  * Note this version supports HSQLDB version 1.8 and higher, only.
  * <p/>
  * Enhancements to version 3.5.0 GA to provide basic support for both HSQLDB 1.8.x and 2.x
  * Does not works with Hibernate 3.2 - 3.4 without alteration.
  *
  * @author Christoph Sturm
  * @author Phillip Baird
  * @author Fred Toussi
  */
 @SuppressWarnings("deprecation")
 public class HSQLDialect extends Dialect {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			HSQLDialect.class.getName()
 	);
 
 	private final class HSQLLimitHandler extends AbstractLimitHandler {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			if (hsqldbVersion < 20) {
 				return new StringBuilder( sql.length() + 10 )
 						.append( sql )
 						.insert(
 								sql.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6,
 								hasOffset ? " limit ? ?" : " top ?"
 						)
 						.toString();
 			}
 			else {
 				return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
 			}
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersFirst() {
 			return hsqldbVersion < 20;
 		}
 	}
 
 	/**
 	 * version is 18 for 1.8 or 20 for 2.0
 	 */
 	private int hsqldbVersion = 18;
 	private final LimitHandler limitHandler;
 
 
 	/**
 	 * Constructs a HSQLDialect
 	 */
 	public HSQLDialect() {
 		super();
 
 		try {
 			final Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
 			final String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );
 
 			hsqldbVersion = Integer.parseInt( versionString.substring( 0, 1 ) ) * 10;
 			hsqldbVersion += Integer.parseInt( versionString.substring( 2, 3 ) );
 		}
 		catch ( Throwable e ) {
 			// must be a very old version
 		}
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 
 		registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
 		registerColumnType( Types.DOUBLE, "double" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
 		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARBINARY, "varbinary($l)" );
 
 		if ( hsqldbVersion < 20 ) {
 			registerColumnType( Types.NUMERIC, "numeric" );
 		}
 		else {
 			registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		}
 
 		//HSQL has no Blob/Clob support .... but just put these here for now!
 		if ( hsqldbVersion < 20 ) {
 			registerColumnType( Types.BLOB, "longvarbinary" );
 			registerColumnType( Types.CLOB, "longvarchar" );
 		}
 		else {
 			registerColumnType( Types.BLOB, "blob($l)" );
 			registerColumnType( Types.CLOB, "clob($l)" );
 		}
 
 		// aggregate functions
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
 		// string functions
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "char", new StandardSQLFunction( "char", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(256))" ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex" ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw" ) );
 
 		// system functions
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 
 		// datetime functions
 		if ( hsqldbVersion < 20 ) {
 			registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		}
 		else {
 			registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		}
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false )
 		);
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "cast(second(?1) as int)" ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 
 		// numeric functions
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new StandardSQLFunction( "rand", StandardBasicTypes.FLOAT ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic" ) );
 		registerFunction( "truncate", new StandardSQLFunction( "truncate" ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		// special functions
 		// from v. 2.2.0 ROWNUM() is supported in all modes as the equivalent of Oracle ROWNUM
 		if ( hsqldbVersion > 21 ) {
 			registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.INTEGER ) );
 		}
 
 		// function templates
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		limitHandler = new HSQLLimitHandler();
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		//not null is implicit
 		return "generated by default as identity (start with 1)";
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return hsqldbVersion < 20 ? "null" : "default";
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		if ( hsqldbVersion >= 20 ) {
 			return " for update";
 		}
 		else {
 			return "";
 		}
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return limitHandler;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hsqldbVersion < 20 ) {
 			return new StringBuilder( sql.length() + 10 )
 					.append( sql )
 					.insert(
 							sql.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6,
 							hasOffset ? " limit ? ?" : " top ?"
 					)
 					.toString();
 		}
 		else {
 			return sql + (hasOffset ? " offset ? limit ?" : " limit ?");
 		}
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return hsqldbVersion < 20;
 	}
 
 	@Override
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return hsqldbVersion >= 20;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	/**
 	 * HSQL will start with 0, by default.  In order for Hibernate to know that this not transient,
 	 * manually start with 1.
 	 */
 	@Override
 	protected String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName + " start with 1";
 	}
 	
 	/**
 	 * Because of the overridden {@link #getCreateSequenceString(String)}, we must also override
 	 * {@link #getCreateSequenceString(String, int, int)} to prevent 2 instances of "start with".
 	 */
 	@Override
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return "create sequence " + sequenceName + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	@Override
 	protected String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		// this assumes schema support, which is present in 1.8.0 and later...
 		return "select sequence_name from information_schema.system_sequences";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return hsqldbVersion < 20 ? EXTRACTER_18 : EXTRACTER_20;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -8 ) {
 				constraintName = extractUsingTemplate(
 						"Integrity constraint violation ", " table:", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -9 ) {
 				constraintName = extractUsingTemplate(
 						"Violation of unique index: ", " in statement [", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -104 ) {
 				constraintName = extractUsingTemplate(
 						"Unique constraint violation: ", " in statement [", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -177 ) {
 				constraintName = extractUsingTemplate(
 						"Integrity constraint violation - no parent ", " table:",
 						sqle.getMessage()
 				);
 			}
 			return constraintName;
 		}
 
 	};
 
 	/**
 	 * HSQLDB 2.0 messages have changed
 	 * messages may be localized - therefore use the common, non-locale element " table: "
 	 */
 	private static final ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -8 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -9 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -104 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -177 ) {
 				constraintName = extractUsingTemplate(
 						"; ", " table: ", sqle.getMessage()
 				);
 			}
 			return constraintName;
 		}
 	};
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
 			case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "cast(null as varchar(100))";
 				break;
 			case Types.LONGVARBINARY:
 			case Types.VARBINARY:
 			case Types.BINARY:
 				literal = "cast(null as varbinary(100))";
 				break;
 			case Types.CLOB:
 				literal = "cast(null as clob)";
 				break;
 			case Types.BLOB:
 				literal = "cast(null as blob)";
 				break;
 			case Types.DATE:
 				literal = "cast(null as date)";
 				break;
 			case Types.TIMESTAMP:
 				literal = "cast(null as timestamp)";
 				break;
 			case Types.BOOLEAN:
 				literal = "cast(null as boolean)";
 				break;
 			case Types.BIT:
 				literal = "cast(null as bit)";
 				break;
 			case Types.TIME:
 				literal = "cast(null as time)";
 				break;
 			default:
 				literal = "cast(null as int)";
 		}
 		return literal;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
-	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	// Hibernate uses this information for temporary tables that it uses for its own operations
-	// therefore the appropriate strategy is taken with different versions of HSQLDB
-
-	// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
-	// definition is shared by all users but data is private to the session
-	// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
-	// the definition and data is private to the session and table declaration
-	// can happen in the middle of a transaction
-
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		// Hibernate uses this information for temporary tables that it uses for its own operations
+		// therefore the appropriate strategy is taken with different versions of HSQLDB
 
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		if ( hsqldbVersion < 20 ) {
-			return "HT_" + baseTableName;
-		}
-		else {
-			// With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
-			// statement (in-case there is a global name beginning with HT_)
-			return "MODULE.HT_" + baseTableName;
-		}
-	}
+		// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
+		// definition is shared by all users but data is private to the session
+		// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
+		// the definition and data is private to the session and table declaration
+		// can happen in the middle of a transaction
 
-	@Override
-	public String getCreateTemporaryTableString() {
 		if ( hsqldbVersion < 20 ) {
-			return "create global temporary table";
+			return new GlobalTemporaryTableBulkIdStrategy(
+					new IdTableSupportStandardImpl() {
+						@Override
+						public String generateIdTableName(String baseName) {
+							return "HT_" + baseName;
+						}
+
+						@Override
+						public String getCreateIdTableCommand() {
+							return "create global temporary table";
+						}
+					},
+					// Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
+					// of the session (by default, data is cleared at commit).
+					AfterUseAction.CLEAN
+			);
 		}
 		else {
-			return "declare local temporary table";
+			return new LocalTemporaryTableBulkIdStrategy(
+					new IdTableSupportStandardImpl() {
+						@Override
+						public String generateIdTableName(String baseName) {
+							// With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
+							// statement (in-case there is a global name beginning with HT_)
+							return "MODULE.HT_" + baseName;
+						}
+
+						@Override
+						public String getCreateIdTableCommand() {
+							return "declare local temporary table";
+						}
+					},
+					AfterUseAction.DROP,
+					TempTableDdlTransactionHandling.NONE
+			);
 		}
 	}
 
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "";
-	}
-
-	@Override
-	public String getDropTemporaryTableString() {
-		return "drop table";
-	}
-
-	@Override
-	public Boolean performTemporaryTableDDLInIsolation() {
-		// Different behavior for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
-		if ( hsqldbVersion < 20 ) {
-			return Boolean.TRUE;
-		}
-		else {
-			return Boolean.FALSE;
-		}
-	}
-
-	@Override
-	public boolean dropTemporaryTableAfterUse() {
-		// Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
-		// of the session (by default, data is cleared at commit).<p>
-		//
-		// Version 2.x LOCAL TEMPORARY table definitions do not persist beyond
-		// the end of the session (by default, data is cleared at commit).
-		return true;
-	}
-
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * HSQLDB 1.8.x requires CALL CURRENT_TIMESTAMP but this should not
 	 * be treated as a callable statement. It is equivalent to
 	 * "select current_timestamp from dual" in some databases.
 	 * HSQLDB 2.0 also supports VALUES CURRENT_TIMESTAMP
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 	/**
 	 * For HSQLDB 2.0, this is a copy of the base class implementation.
 	 * For HSQLDB 1.8, only READ_UNCOMMITTED is supported.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 
 		if ( hsqldbVersion < 20 ) {
 			return new ReadUncommittedLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	private static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
 		public ReadUncommittedLockingStrategy(Lockable lockable, LockMode lockMode) {
 			super( lockable, lockMode );
 		}
 
 		public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
 				throws StaleObjectStateException, JDBCException {
 			if ( getLockMode().greaterThan( LockMode.READ ) ) {
 				LOG.hsqldbSupportsOnlyReadCommittedIsolation();
 			}
 			super.lock( id, version, object, timeout, session );
 		}
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return hsqldbVersion >= 20;
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return hsqldbVersion >= 20;
 	}
 
 	@Override
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return hsqldbVersion >= 20;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return String.valueOf( bool );
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index 614d7dc8c2..12bcae35a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,308 +1,317 @@
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
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.MappingException;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.InformixUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Informix dialect.<br>
  * <br>
  * Seems to work with Informix Dynamic Server Version 7.31.UD3,  Informix JDBC driver version 2.21JC3.
  *
  * @author Steve Molitor
  */
 public class InformixDialect extends Dialect {
 	
 	private final UniqueDelegate uniqueDelegate;
 
 	/**
 	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
 	 * Informix type mappings.
 	 */
 	public InformixDialect() {
 		super();
 
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.BINARY, "byte" );
 		// Informix doesn't have a bit type
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.FLOAT, "smallfloat" );
 		registerColumnType( Types.INTEGER, "integer" );
 		// or BYTE
 		registerColumnType( Types.LONGVARBINARY, "blob" );
 		// or TEXT?
 		registerColumnType( Types.LONGVARCHAR, "clob" );
 		// or MONEY
 		registerColumnType( Types.NUMERIC, "decimal" );
 		registerColumnType( Types.REAL, "smallfloat" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "datetime year to fraction(5)" );
 		registerColumnType( Types.TIME, "datetime hour to second" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.VARBINARY, "byte" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 32739, "lvarchar($l)" );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		
 		uniqueDelegate = new InformixUniqueDelegate( this );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString(String table, String column, int type)
 			throws MappingException {
 		return type == Types.BIGINT
 				? "select dbinfo('serial8') from informix.systables where tabid=1"
 				: "select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
 	}
 
 	@Override
 	public String getIdentityColumnString(int type) throws MappingException {
 		return type == Types.BIGINT ?
 				"serial8 not null" :
 				"serial not null";
 	}
 
 	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder result = new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			result.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		result.append( " constraint " ).append( constraintName );
 
 		return result.toString();
 	}
 
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint primary key constraint " + constraintName + " ";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from informix.systables where tabid=1";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select tabname from informix.systables where tabtype='Q'";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( querySelect.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -268 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == -691 ) {
 				constraintName = extractUsingTemplate(
 						"Missing key in referenced table for referential constraint (",
 						").",
 						sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -692 ) {
 				constraintName = extractUsingTemplate(
 						"Key value for constraint (",
 						") is still being referenced.",
 						sqle.getMessage()
 				);
 			}
 
 			if ( constraintName != null ) {
 				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
 				final int i = constraintName.indexOf( '.' );
 				if ( i != -1 ) {
 					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 
 			return constraintName;
 		}
 
 	};
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select distinct current timestamp from informix.systables";
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create temp table";
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "with no log";
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new LocalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create temp table";
+					}
+
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "with no log";
+					}
+				},
+				AfterUseAction.CLEAN,
+				null
+		);
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
index 905d9b171a..e5db5930c7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/IngresDialect.java
@@ -1,317 +1,325 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for Ingres 9.2.
  * <p/>
  * Known limitations: <ul>
  *     <li>
  *         Only supports simple constants or columns on the left side of an IN,
  *         making {@code (1,2,3) in (...)} or {@code (subselect) in (...)} non-supported.
  *     </li>
  *     <li>
  *         Supports only 39 digits in decimal.
  *     </li>
  *     <li>
  *         Explicitly set USE_GET_GENERATED_KEYS property to false.
  *     </li>
  *     <li>
  *         Perform string casts to varchar; removes space padding.
  *     </li>
  * </ul>
  * 
  * @author Ian Booth
  * @author Bruce Lunsford
  * @author Max Rydahl Andersen
  * @author Raymond Fan
  */
 @SuppressWarnings("deprecation")
 public class IngresDialect extends Dialect {
 	/**
 	 * Constructs a IngresDialect
 	 */
 	public IngresDialect() {
 		super();
 		registerColumnType( Types.BIT, "tinyint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.REAL, "real" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.NUMERIC, "decimal($p, $s)" );
 		registerColumnType( Types.DECIMAL, "decimal($p, $s)" );
 		registerColumnType( Types.BINARY, 32000, "byte($l)" );
 		registerColumnType( Types.BINARY, "long byte" );
 		registerColumnType( Types.VARBINARY, 32000, "varbyte($l)" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.LONGVARBINARY, "long byte" );
 		registerColumnType( Types.CHAR, 32000, "char($l)" );
 		registerColumnType( Types.VARCHAR, 32000, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, "long varchar" );
 		registerColumnType( Types.LONGVARCHAR, "long varchar" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time with time zone" );
 		registerColumnType( Types.TIMESTAMP, "timestamp with time zone" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "bit_add", new StandardSQLFunction( "bit_add" ) );
 		registerFunction( "bit_and", new StandardSQLFunction( "bit_and" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(hex(?1))*4" ) );
 		registerFunction( "bit_not", new StandardSQLFunction( "bit_not" ) );
 		registerFunction( "bit_or", new StandardSQLFunction( "bit_or" ) );
 		registerFunction( "bit_xor", new StandardSQLFunction( "bit_xor" ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "charextract", new StandardSQLFunction( "charextract", StandardBasicTypes.STRING ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "+", ")" ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "current_user", new NoArgSQLFunction( "current_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "date('now')", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dba", new NoArgSQLFunction( "dba", StandardBasicTypes.STRING, true ) );
 		registerFunction( "dow", new StandardSQLFunction( "dow", StandardBasicTypes.STRING ) );
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "date_part('?1', ?3)" ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "gmt_timestamp", new StandardSQLFunction( "gmt_timestamp", StandardBasicTypes.STRING ) );
 		registerFunction( "hash", new StandardSQLFunction( "hash", StandardBasicTypes.INTEGER ) );
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "initial_user", new NoArgSQLFunction( "initial_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "intextract", new StandardSQLFunction( "intextract", StandardBasicTypes.INTEGER ) );
 		registerFunction( "left", new StandardSQLFunction( "left", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.LONG, "locate(?1, ?2)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.LONG ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "lowercase", new StandardSQLFunction( "lowercase" ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "pad", new StandardSQLFunction( "pad", StandardBasicTypes.STRING ) );
 		registerFunction( "position", new StandardSQLFunction( "position", StandardBasicTypes.LONG ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.LONG, true ) );
 		registerFunction( "randomf", new NoArgSQLFunction( "randomf", StandardBasicTypes.DOUBLE, true ) );
 		registerFunction( "right", new StandardSQLFunction( "right", StandardBasicTypes.STRING ) );
 		registerFunction( "session_user", new NoArgSQLFunction( "session_user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "size", new NoArgSQLFunction( "size", StandardBasicTypes.LONG, true ) );
 		registerFunction( "squeeze", new StandardSQLFunction( "squeeze" ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 FROM ?2 FOR ?3)" ) );
 		registerFunction( "system_user", new NoArgSQLFunction( "system_user", StandardBasicTypes.STRING, false ) );
 		//registerFunction( "trim", new StandardSQLFunction( "trim", StandardBasicTypes.STRING ) );
 		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "uppercase", new StandardSQLFunction( "uppercase" ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 		registerFunction( "usercode", new NoArgSQLFunction( "usercode", StandardBasicTypes.STRING, true ) );
 		registerFunction( "username", new NoArgSQLFunction( "username", StandardBasicTypes.STRING, true ) );
 		registerFunction( "uuid_create", new StandardSQLFunction( "uuid_create", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_compare", new StandardSQLFunction( "uuid_compare", StandardBasicTypes.INTEGER ) );
 		registerFunction( "uuid_from_char", new StandardSQLFunction( "uuid_from_char", StandardBasicTypes.BYTE ) );
 		registerFunction( "uuid_to_char", new StandardSQLFunction( "uuid_to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		// Casting to char of numeric values introduces space padding up to the
 		// maximum width of a value for that return type.  Casting to varchar
 		// does not introduce space padding.
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
 		// Ingres driver supports getGeneratedKeys but only in the following
 		// form:
 		// The Ingres DBMS returns only a single table key or a single object
 		// key per insert statement. Ingres does not return table and object
 		// keys for INSERT AS SELECT statements. Depending on the keys that are
 		// produced by the statement executed, auto-generated key parameters in
 		// execute(), executeUpdate(), and prepareStatement() methods are
 		// ignored and getGeneratedKeys() returns a result-set containing no
 		// rows, a single row with one column, or a single row with two columns.
 		// Ingres JDBC Driver returns table and object keys as BINARY values.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 		// There is no support for a native boolean type that accepts values
 		// of true, false or unknown. Using the tinyint type requires
 		// substitions of true and false.
 		getDefaultProperties().setProperty( Environment.QUERY_SUBSTITUTIONS, "true=1,false=0" );
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select uuid_to_char(uuid_create())";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return " with null";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select nextval for " + sequenceName;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select seq_name from iisequence";
 	}
 
 	@Override
 	public String getLowercaseFunction() {
 		return "lowercase";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 16 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						return "session." + super.generateIdTableName( baseName );
+					}
+
+					@Override
+					public String getCreateIdTableCommand() {
+						return "declare global temporary table";
+					}
+
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "on commit preserve rows with norecovery";
+					}
+				},
+				AfterUseAction.CLEAN
+		);
 	}
 
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "declare global temporary table";
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "on commit preserve rows with norecovery";
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		return "session." + super.generateTemporaryTableName( baseTableName );
-	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "date(now)";
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsSubselectAsInPredicateLHS() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
index 2ed59b0e11..2798700906 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/MySQLDialect.java
@@ -1,485 +1,488 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.NullPrecedence;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
+import org.hibernate.hql.spi.id.IdTableSupport;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect for MySQL (prior to 5.x).
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class MySQLDialect extends Dialect {
 
 	private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (hasOffset ? " limit ?, ?" : " limit ?");
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 	};
 
 	/**
 	 * Constructs a MySQLDialect
 	 */
 	public MySQLDialect() {
 		super();
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.BOOLEAN, "bit" ); // HHH-6935
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "datetime" );
 		registerColumnType( Types.VARBINARY, "longblob" );
 		registerColumnType( Types.VARBINARY, 16777215, "mediumblob" );
 		registerColumnType( Types.VARBINARY, 65535, "blob" );
 		registerColumnType( Types.VARBINARY, 255, "tinyblob" );
 		registerColumnType( Types.BINARY, "binary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "longblob" );
 		registerColumnType( Types.LONGVARBINARY, 16777215, "mediumblob" );
 		registerColumnType( Types.NUMERIC, "decimal($p,$s)" );
 		registerColumnType( Types.BLOB, "longblob" );
 //		registerColumnType( Types.BLOB, 16777215, "mediumblob" );
 //		registerColumnType( Types.BLOB, 65535, "blob" );
 		registerColumnType( Types.CLOB, "longtext" );
 //		registerColumnType( Types.CLOB, 16777215, "mediumtext" );
 //		registerColumnType( Types.CLOB, 65535, "text" );
 		registerVarcharTypes();
 
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bin", new StandardSQLFunction( "bin", StandardBasicTypes.STRING ) );
 		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.LONG ) );
 		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.LONG ) );
 		registerFunction( "lcase", new StandardSQLFunction( "lcase" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "ord", new StandardSQLFunction( "ord", StandardBasicTypes.INTEGER ) );
 		registerFunction( "quote", new StandardSQLFunction( "quote" ) );
 		registerFunction( "reverse", new StandardSQLFunction( "reverse" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
 		registerFunction( "ucase", new StandardSQLFunction( "ucase" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "unhex", new StandardSQLFunction( "unhex", StandardBasicTypes.STRING ) );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cot", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "crc32", new StandardSQLFunction( "crc32", StandardBasicTypes.LONG ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log2", new StandardSQLFunction( "log2", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log10", new StandardSQLFunction( "log10", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "rand", new NoArgSQLFunction( "rand", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction("std", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil", StandardBasicTypes.INTEGER ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 
 		registerFunction( "datediff", new StandardSQLFunction( "datediff", StandardBasicTypes.INTEGER ) );
 		registerFunction( "timediff", new StandardSQLFunction( "timediff", StandardBasicTypes.TIME ) );
 		registerFunction( "date_format", new StandardSQLFunction( "date_format", StandardBasicTypes.STRING ) );
 
 		registerFunction( "curdate", new NoArgSQLFunction( "curdate", StandardBasicTypes.DATE ) );
 		registerFunction( "curtime", new NoArgSQLFunction( "curtime", StandardBasicTypes.TIME ) );
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "from_days", new StandardSQLFunction( "from_days", StandardBasicTypes.DATE ) );
 		registerFunction( "from_unixtime", new StandardSQLFunction( "from_unixtime", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "hour", new StandardSQLFunction( "hour", StandardBasicTypes.INTEGER ) );
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "localtime", new NoArgSQLFunction( "localtime", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "localtimestamp", new NoArgSQLFunction( "localtimestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "microseconds", new StandardSQLFunction( "microseconds", StandardBasicTypes.INTEGER ) );
 		registerFunction( "minute", new StandardSQLFunction( "minute", StandardBasicTypes.INTEGER ) );
 		registerFunction( "month", new StandardSQLFunction( "month", StandardBasicTypes.INTEGER ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "now", new NoArgSQLFunction( "now", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "quarter", new StandardSQLFunction( "quarter", StandardBasicTypes.INTEGER ) );
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "sec_to_time", new StandardSQLFunction( "sec_to_time", StandardBasicTypes.TIME ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "time_to_sec", new StandardSQLFunction( "time_to_sec", StandardBasicTypes.INTEGER ) );
 		registerFunction( "to_days", new StandardSQLFunction( "to_days", StandardBasicTypes.LONG ) );
 		registerFunction( "unix_timestamp", new StandardSQLFunction( "unix_timestamp", StandardBasicTypes.LONG ) );
 		registerFunction( "utc_date", new NoArgSQLFunction( "utc_date", StandardBasicTypes.STRING ) );
 		registerFunction( "utc_time", new NoArgSQLFunction( "utc_time", StandardBasicTypes.STRING ) );
 		registerFunction( "utc_timestamp", new NoArgSQLFunction( "utc_timestamp", StandardBasicTypes.STRING ) );
 		registerFunction( "week", new StandardSQLFunction( "week", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekday", new StandardSQLFunction( "weekday", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekofyear", new StandardSQLFunction( "weekofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "year", new StandardSQLFunction( "year", StandardBasicTypes.INTEGER ) );
 		registerFunction( "yearweek", new StandardSQLFunction( "yearweek", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "hex", new StandardSQLFunction( "hex", StandardBasicTypes.STRING ) );
 		registerFunction( "oct", new StandardSQLFunction( "oct", StandardBasicTypes.STRING ) );
 
 		registerFunction( "octet_length", new StandardSQLFunction( "octet_length", StandardBasicTypes.LONG ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.LONG ) );
 
 		registerFunction( "bit_count", new StandardSQLFunction( "bit_count", StandardBasicTypes.LONG ) );
 		registerFunction( "encrypt", new StandardSQLFunction( "encrypt", StandardBasicTypes.STRING ) );
 		registerFunction( "md5", new StandardSQLFunction( "md5", StandardBasicTypes.STRING ) );
 		registerFunction( "sha1", new StandardSQLFunction( "sha1", StandardBasicTypes.STRING ) );
 		registerFunction( "sha", new StandardSQLFunction( "sha", StandardBasicTypes.STRING ) );
 
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 
 		getDefaultProperties().setProperty( Environment.MAX_FETCH_DEPTH, "2" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
 	protected void registerVarcharTypes() {
 		registerColumnType( Types.VARCHAR, "longtext" );
 //		registerColumnType( Types.VARCHAR, 16777215, "mediumtext" );
 //		registerColumnType( Types.VARCHAR, 65535, "text" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "longtext" );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getIdentitySelectString() {
 		return "select last_insert_id()";
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		//starts with 1, implicitly
 		return "not null auto_increment";
 	}
 
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final String cols = StringHelper.join( ", ", foreignKey );
 		final String referencedCols = StringHelper.join( ", ", primaryKey );
 		return String.format(
 				" add constraint %s foreign key (%s) references %s (%s)",
 				constraintName,
 				cols,
 				referencedTable,
 				referencedCols
 		);
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getDropForeignKeyString() {
 		return " drop foreign key ";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ?, ?" : " limit ?");
 	}
 
 	@Override
 	public char closeQuote() {
 		return '`';
 	}
 
 	@Override
 	public char openQuote() {
 		return '`';
 	}
 
 	@Override
 	public boolean supportsIfExistsBeforeTableName() {
 		return true;
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select uuid()";
 	}
 
 	@Override
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
 	@Override
 	public String getTableComment(String comment) {
 		return " comment='" + comment + "'";
 	}
 
 	@Override
 	public String getColumnComment(String comment) {
 		return " comment '" + comment + "'";
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create temporary table if not exists";
-	}
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new LocalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create temporary table if not exists";
+					}
 
-	@Override
-	public String getDropTemporaryTableString() {
-		return "drop temporary table";
-	}
-
-	@Override
-	public Boolean performTemporaryTableDDLInIsolation() {
-		// because we [drop *temporary* table...] we do not
-		// have to doAfterTransactionCompletion these in isolation.
-		return Boolean.FALSE;
+					@Override
+					public String getDropIdTableCommand() {
+						return "drop temporary table";
+					}
+				},
+				AfterUseAction.DROP,
+				TempTableDdlTransactionHandling.NONE
+		);
 	}
 
 	@Override
 	public String getCastTypeName(int code) {
 		switch ( code ) {
 			case Types.INTEGER:
 			case Types.BIGINT:
 			case Types.SMALLINT:
 				return "signed";
 			case Types.FLOAT:
 			case Types.NUMERIC:
 			case Types.REAL:
 				return "decimal";
 			case Types.VARCHAR:
 				return "char";
 			case Types.VARBINARY:
 				return "binary";
 			default:
 				return super.getCastTypeName( code );
 		}
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		boolean isResultSet = ps.execute();
 		while ( !isResultSet && ps.getUpdateCount() != -1 ) {
 			isResultSet = ps.getMoreResults();
 		}
 		return ps.getResultSet();
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 
 	@Override
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder();
 		if ( nulls != NullPrecedence.NONE ) {
 			// Workaround for NULLS FIRST / LAST support.
 			orderByElement.append( "case when " ).append( expression ).append( " is null then " );
 			if ( nulls == NullPrecedence.FIRST ) {
 				orderByElement.append( "0 else 1" );
 			}
 			else {
 				orderByElement.append( "1 else 0" );
 			}
 			orderByElement.append( " end, " );
 		}
 		// Nulls precedence has already been handled so passing NONE value.
 		orderByElement.append( super.renderOrderByElement( expression, collation, order, NullPrecedence.NONE ) );
 		return orderByElement.toString();
 	}
 
 	// locking support
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		return " for update";
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		return " lock in share mode";
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		// note: at least my local MySQL 5.1 install shows this not working...
 		return false;
 	}
 
 	@Override
 	public boolean supportsSubqueryOnMutatingTable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		// yes, we do handle "lock timeout" conditions in the exception conversion delegate,
 		// but that's a hardcoded lock timeout period across the whole entire MySQL database.
 		// MySQL does not support specifying lock timeouts as part of the SQL statement, which is really
 		// what this meta method is asking.
 		return false;
 	}
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
 				if ( "41000".equals( sqlState ) ) {
 					return new LockTimeoutException( message, sqlException, sql );
 				}
 
 				if ( "40001".equals( sqlState ) ) {
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public String getNotExpression(String expression) {
 		return "not (" + expression + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index c57be684dd..1484b04aa1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,691 +1,686 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.List;
 import java.util.Locale;
 
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
-import org.hibernate.hql.spi.GlobalTemporaryTableBulkIdStrategy;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.procedure.internal.StandardCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Oracle 8i.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings("deprecation")
 public class Oracle8iDialect extends Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			sql = sql.trim();
 			boolean isForUpdate = false;
 			if (sql.toLowerCase(Locale.ROOT
                         ).endsWith( " for update" )) {
 				sql = sql.substring( 0, sql.length() - 11 );
 				isForUpdate = true;
 			}
 
 			final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 			if (hasOffset) {
 				pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 			}
 			else {
 				pagingSelect.append( "select * from ( " );
 			}
 			pagingSelect.append( sql );
 			if (hasOffset) {
 				pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 			}
 			else {
 				pagingSelect.append( " ) where rownum <= ?" );
 			}
 
 			if (isForUpdate) {
 				pagingSelect.append( " for update" );
 			}
 
 			return pagingSelect.toString();
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 
 		@Override
 		public boolean useMaxForLimit() {
 			return true;
 		}
 	};
 
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
 	/**
 	 * Constructs a Oracle8iDialect
 	 */
 	public Oracle8iDialect() {
 		super();
 		registerCharacterTypeMappings();
 		registerNumericTypeMappings();
 		registerDateTimeTypeMappings();
 		registerLargeObjectTypeMappings();
 		registerReverseHibernateTypeMappings();
 		registerFunctions();
 		registerDefaultProperties();
 	}
 
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	protected void registerNumericTypeMappings() {
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 
 		registerColumnType( Types.BOOLEAN, "number(1,0)" );
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "date" );
 	}
 
 	protected void registerLargeObjectTypeMappings() {
 		registerColumnType( Types.BINARY, 2000, "raw($l)" );
 		registerColumnType( Types.BINARY, "long raw" );
 
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.LONGVARCHAR, "long" );
 		registerColumnType( Types.LONGVARBINARY, "long raw" );
 	}
 
 	protected void registerReverseHibernateTypeMappings() {
 	}
 
 	protected void registerFunctions() {
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "bitand", new StandardSQLFunction("bitand") );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
 		registerFunction( "soundex", new StandardSQLFunction("soundex") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP) );
 
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 
 		registerFunction( "last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
 		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
 		registerFunction( "systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 
 		registerFunction( "rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false) );
 		registerFunction( "rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", "") );
 		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER) );
 		registerFunction( "instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER) );
 		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
 		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
 		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING) );
 		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER) );
 		registerFunction( "mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 		registerFunction( "nvl2", new StandardSQLFunction("nvl2") );
 		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE) );
 		registerFunction( "months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT) );
 		registerFunction( "next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE) );
 
 		registerFunction( "str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 	}
 
 	protected void registerDefaultProperties() {
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 
 	// features which change between 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	/**
 	 * Map case support to the Oracle DECODE function.  Oracle did not
 	 * add support for CASE until 9i.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase(Locale.ROOT).endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
 		if (hasOffset) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if (hasOffset) {
 			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	/**
 	 * Allows access to the basic {@link Dialect#getSelectClauseNullString}
 	 * implementation...
 	 *
 	 * @param sqlType The {@link java.sql.Types} mapping type code
 	 * @return The appropriate select cluse fragment
 	 */
 	public String getBasicSelectClauseNullString(int sqlType) {
 		return super.getSelectClauseNullString( sqlType );
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		switch(sqlType) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				return "to_char(null)";
 			case Types.DATE:
 			case Types.TIMESTAMP:
 			case Types.TIME:
 				return "to_date(null)";
 			default:
 				return "to_number(null)";
 		}
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
 
 	// features which remain constant across 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		//starts with 1, implicitly
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return    " select sequence_name from all_sequences"
 				+ "  union"
 				+ " select synonym_name"
 				+ "   from all_synonyms us, all_sequences asq"
 				+ "  where asq.sequence_name = us.table_name"
 				+ "    and asq.sequence_owner = us.table_owner";
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "(", ")", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
 
 	};
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				// interpreting Oracle exceptions is much much more precise based on their specific vendor codes.
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 
 				// lock timeouts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( errorCode == 30006 ) {
 					// ORA-30006: resource busy; acquire with WAIT timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( errorCode == 54 ) {
 					// ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( 4021 == errorCode ) {
 					// ORA-04021 timeout occurred while waiting to lock object
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 
 
 				// deadlocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 60 == errorCode ) {
 					// ORA-00060: deadlock detected while waiting for resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 				else if ( 4020 == errorCode ) {
 					// ORA-04020 deadlock detected while trying to lock object
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 
 				// query cancelled ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1013 == errorCode ) {
 					// ORA-01013: user requested cancel of current operation
 					throw new QueryTimeoutException(  message, sqlException, sql );
 				}
 
 
 				// data integrity violation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1407 == errorCode ) {
 					// ORA-01407: cannot update column to NULL
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
-		return new GlobalTemporaryTableBulkIdStrategy();
-	}
-
-	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		final String name = super.generateTemporaryTableName( baseTableName );
-		return name.length() > 30 ? name.substring( 0, 30 ) : name;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "on commit delete rows";
-	}
-
-	@Override
-	public boolean dropTemporaryTableAfterUse() {
-		return false;
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						final String name = super.generateIdTableName( baseName );
+						return name.length() > 30 ? name.substring( 0, 30 ) : name;
+					}
+
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create global temporary table";
+					}
+
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "on commit delete rows";
+					}
+				},
+				AfterUseAction.CLEAN
+		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 	
 	@Override
 	public boolean forceLobAsLastValue() {
 		return true;
 	}
 
 	@Override
 	public boolean useFollowOnLocking() {
 		return true;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
 	
 	@Override
 	public String getQueryHintString(String sql, List<String> hints) {
 		final String hint = StringHelper.join( ", ", hints.iterator() );
 		
 		if ( StringHelper.isEmpty( hint ) ) {
 			return sql;
 		}
 
 		final int pos = sql.indexOf( "select" );
 		if ( pos > -1 ) {
 			final StringBuilder buffer = new StringBuilder( sql.length() + hint.length() + 8 );
 			if ( pos > 0 ) {
 				buffer.append( sql.substring( 0, pos ) );
 			}
 			buffer.append( "select /*+ " ).append( hint ).append( " */" )
 					.append( sql.substring( pos + "select".length() ) );
 			sql = buffer.toString();
 		}
 
 		return sql;
 	}
 	
 	@Override
 	public int getMaxAliasLength() {
 		// Oracle's max identifier length is 30, but Hibernate needs to add "uniqueing info" so we account for that,
 		return 20;
 	}
 
 	@Override
 	public CallableStatementSupport getCallableStatementSupport() {
 		// Oracle supports returning cursors
 		return StandardCallableStatementSupport.REF_CURSOR_INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
index 51c83a1246..8a8218a897 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
@@ -1,421 +1,416 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
-import org.hibernate.hql.spi.GlobalTemporaryTableBulkIdStrategy;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.jboss.logging.Logger;
 
 /**
  * An SQL dialect for Oracle 9 (uses ANSI-style syntax where possible).
  *
  * @author Gavin King
  * @author David Channon
  *
  * @deprecated Use either Oracle9iDialect or Oracle10gDialect instead
  */
 @SuppressWarnings("deprecation")
 @Deprecated
 public class Oracle9Dialect extends Dialect {
 
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			Oracle9Dialect.class.getName()
 	);
 
 	/**
 	 * Constructs a Oracle9Dialect
 	 */
 	public Oracle9Dialect() {
 		super();
 		LOG.deprecatedOracle9Dialect();
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 		registerColumnType( Types.CHAR, "char(1 char)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l char)" );
 		registerColumnType( Types.VARCHAR, "long" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "stddev", new StandardSQLFunction( "stddev", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "variance", new StandardSQLFunction( "variance", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		registerFunction( "chr", new StandardSQLFunction( "chr", StandardBasicTypes.CHARACTER ) );
 		registerFunction( "initcap", new StandardSQLFunction( "initcap" ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
 
 		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
 		registerFunction( "current_time", new NoArgSQLFunction( "current_timestamp", StandardBasicTypes.TIME, false ) );
 		registerFunction(
 				"current_timestamp", new NoArgSQLFunction(
 				"current_timestamp",
 				StandardBasicTypes.TIMESTAMP,
 				false
 		)
 		);
 
 		registerFunction( "last_day", new StandardSQLFunction( "last_day", StandardBasicTypes.DATE ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
 		registerFunction( "systimestamp", new NoArgSQLFunction( "systimestamp", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "uid", new NoArgSQLFunction( "uid", StandardBasicTypes.INTEGER, false ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING, false ) );
 
 		registerFunction( "rowid", new NoArgSQLFunction( "rowid", StandardBasicTypes.LONG, false ) );
 		registerFunction( "rownum", new NoArgSQLFunction( "rownum", StandardBasicTypes.LONG, false ) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
 		registerFunction( "instr", new StandardSQLFunction( "instr", StandardBasicTypes.INTEGER ) );
 		registerFunction( "instrb", new StandardSQLFunction( "instrb", StandardBasicTypes.INTEGER ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "substrb", new StandardSQLFunction( "substrb", StandardBasicTypes.STRING ) );
 		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.FLOAT ) );
 		registerFunction( "log", new StandardSQLFunction( "log", StandardBasicTypes.INTEGER ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 		registerFunction( "nvl2", new StandardSQLFunction( "nvl2" ) );
 		registerFunction( "power", new StandardSQLFunction( "power", StandardBasicTypes.FLOAT ) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction( "add_months", StandardBasicTypes.DATE ) );
 		registerFunction( "months_between", new StandardSQLFunction( "months_between", StandardBasicTypes.FLOAT ) );
 		registerFunction( "next_day", new StandardSQLFunction( "next_day", StandardBasicTypes.DATE ) );
 
 		registerFunction( "str", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		//starts with 1, implicitly
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase(Locale.ROOT).endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length() - 11 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 		if ( hasOffset ) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if ( hasOffset ) {
 			pagingSelect.append( " ) row_ where rownum <= ?) where rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from user_sequences";
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
 	};
 
 	@Override
 	public int registerResultSetOutParameter(java.sql.CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
-		return new GlobalTemporaryTableBulkIdStrategy();
-	}
-
-	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		final String name = super.generateTemporaryTableName( baseTableName );
-		return name.length() > 30 ? name.substring( 1, 30 ) : name;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "on commit delete rows";
-	}
-
-	@Override
-	public boolean dropTemporaryTableAfterUse() {
-		return false;
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						final String name = super.generateIdTableName( baseName );
+						return name.length() > 30 ? name.substring( 0, 30 ) : name;
+					}
+
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create global temporary table";
+					}
+
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "on commit delete rows";
+					}
+				},
+				AfterUseAction.CLEAN
+		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 
 	@Override
 	public String getNotExpression(String expression) {
 		return "not (" + expression + ")";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
index 8bba7e8d40..f4c8e9c43a 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQL81Dialect.java
@@ -1,572 +1,581 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.JDBCException;
 import org.hibernate.LockOptions;
 import org.hibernate.PessimisticLockException;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.PositionSubstringFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.pagination.AbstractLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.procedure.internal.PostgresCallableStatementSupport;
 import org.hibernate.procedure.spi.CallableStatementSupport;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * An SQL dialect for Postgres
  * <p/>
  * For discussion of BLOB support in Postgres, as of 8.4, have a peek at
  * <a href="http://jdbc.postgresql.org/documentation/84/binary-data.html">http://jdbc.postgresql.org/documentation/84/binary-data.html</a>.
  * For the effects in regards to Hibernate see <a href="http://in.relation.to/15492.lace">http://in.relation.to/15492.lace</a>
  *
  * @author Gavin King
  */
 @SuppressWarnings("deprecation")
 public class PostgreSQL81Dialect extends Dialect {
 
 	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
 		@Override
 		public String processSql(String sql, RowSelection selection) {
 			final boolean hasOffset = LimitHelper.hasFirstRow( selection );
 			return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 		}
 
 		@Override
 		public boolean supportsLimit() {
 			return true;
 		}
 
 		@Override
 		public boolean bindLimitParametersInReverseOrder() {
 			return true;
 		}
 	};
 
 	/**
 	 * Constructs a PostgreSQL81Dialect
 	 */
 	public PostgreSQL81Dialect() {
 		super();
 		registerColumnType( Types.BIT, "bool" );
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.SMALLINT, "int2" );
 		registerColumnType( Types.TINYINT, "int2" );
 		registerColumnType( Types.INTEGER, "int4" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float4" );
 		registerColumnType( Types.DOUBLE, "float8" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "bytea" );
 		registerColumnType( Types.BINARY, "bytea" );
 		registerColumnType( Types.LONGVARCHAR, "text" );
 		registerColumnType( Types.LONGVARBINARY, "bytea" );
 		registerColumnType( Types.CLOB, "text" );
 		registerColumnType( Types.BLOB, "oid" );
 		registerColumnType( Types.NUMERIC, "numeric($p, $s)" );
 		registerColumnType( Types.OTHER, "uuid" );
 
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cbrt", new StandardSQLFunction("cbrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE) );
 		registerFunction( "degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 		registerFunction( "rand", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "to_ascii", new StandardSQLFunction("to_ascii") );
 		registerFunction( "quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING) );
 		registerFunction( "quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING) );
 		registerFunction( "md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING) );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 		registerFunction( "char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG) );
 		registerFunction( "bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG) );
 		registerFunction( "octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG) );
 
 		registerFunction( "age", new StandardSQLFunction("age") );
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "date_trunc", new StandardSQLFunction( "date_trunc", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false) );
 		registerFunction( "localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING) );
 
 		registerFunction( "current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false) );
 		registerFunction( "session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 		registerFunction( "current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true) );
 		registerFunction( "current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true) );
 		
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE) );
 		registerFunction( "to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP) );
 		registerFunction( "to_number", new StandardSQLFunction("to_number", StandardBasicTypes.BIG_DECIMAL) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(","||",")" ) );
 
 		registerFunction( "locate", new PositionSubstringFunction() );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)") );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		getDefaultProperties().setProperty( Environment.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	@Override
 	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				// Force BLOB binding.  Otherwise, byte[] fields annotated
 				// with @Lob will attempt to use
 				// BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING.  Since the
 				// dialect uses oid for Blobs, byte arrays cannot be used.
 				descriptor = BlobTypeDescriptor.BLOB_BINDING;
 				break;
 			}
 			case Types.CLOB: {
 				descriptor = ClobTypeDescriptor.CLOB_BINDING;
 				break;
 			}
 			default: {
 				descriptor = super.getSqlTypeDescriptorOverride( sqlCode );
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName );
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "nextval ('" + sequenceName + "')";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		//starts with 1, implicitly
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select relname from pg_class where relkind='S'";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return LIMIT_HANDLER;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 	
 	@Override
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		/*
 		 * Parent's implementation for (aliases, lockOptions) ignores aliases.
 		 */
 		return getForUpdateString(aliases);
 	}
 
 	@Override
 	public String getIdentitySelectString(String table, String column, int type) {
 		return "select currval('" + table + '_' + column + "_seq')";
 	}
 
 	@Override
 	public String getIdentityColumnString(int type) {
 		return type==Types.BIGINT ?
 			"bigserial not null" :
 			"serial not null";
 	}
 
 	@Override
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	@Override
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	@Override
 	public String getCaseInsensitiveLike(){
 		return "ilike";
 	}
 
 	@Override
 	public boolean supportsCaseInsensitiveLike() {
 		return true;
 	}
 
 	@Override
 	public Class getNativeIdentifierGeneratorClass() {
 		return SequenceGenerator.class;
 	}
 
 	@Override
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 
 	@Override
 	public boolean useInputStreamToInsertBlob() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	/**
 	 * Workaround for postgres bug #1453
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		String typeName = getTypeName( sqlType, 1, 1, 0 );
 		//trim off the length/precision/scale
 		final int loc = typeName.indexOf( '(' );
 		if ( loc > -1 ) {
 			typeName = typeName.substring( 0, loc );
 		}
 		return "null::" + typeName;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create temporary table";
-	}
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new LocalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create temporary table";
+					}
 
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "on commit drop";
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "on commit drop";
+					}
+				},
+				AfterUseAction.CLEAN,
+				null
+		);
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	@Override
 	public boolean requiresParensForTupleDistinctCounts() {
 		return true;
 	}
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "true" : "false";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * Constraint-name extractor for Postgres constraint violation exceptions.
 	 * Orginally contributed by Denny Bartelt.
 	 */
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			try {
 				final int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle ) );
 				switch (sqlState) {
 					// CHECK VIOLATION
 					case 23514: return extractUsingTemplate( "violates check constraint \"","\"", sqle.getMessage() );
 					// UNIQUE VIOLATION
 					case 23505: return extractUsingTemplate( "violates unique constraint \"","\"", sqle.getMessage() );
 					// FOREIGN KEY VIOLATION
 					case 23503: return extractUsingTemplate( "violates foreign key constraint \"","\"", sqle.getMessage() );
 					// NOT NULL VIOLATION
 					case 23502: return extractUsingTemplate( "null value in column \"","\" violates not-null constraint", sqle.getMessage() );
 					// TODO: RESTRICT VIOLATION
 					case 23001: return null;
 					// ALL OTHER
 					default: return null;
 				}
 			}
 			catch (NumberFormatException nfe) {
 				return null;
 			}
 		}
 	};
 	
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				final String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
 				if ( "40P01".equals( sqlState ) ) {
 					// DEADLOCK DETECTED
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 				if ( "55P03".equals( sqlState ) ) {
 					// LOCK NOT AVAILABLE
 					return new PessimisticLockException( message, sqlException, sql );
 				}
 
 				// returning null allows other delegates to operate
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// Register the type of the out param - PostgreSQL uses Types.OTHER
 		statement.registerOutParameter( col++, Types.OTHER );
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	/**
 	 * only necessary for postgre < 7.4  See http://anoncvs.postgresql.org/cvsweb.cgi/pgsql/doc/src/sgml/ref/create_sequence.sgml
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
 		return getCreateSequenceString( sequenceName ) + " start " + initialValue + " increment " + incrementSize;
 	}
 	
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for update nowait";
 		}
 		else {
 			return " for update";
 		}
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for share nowait";
 		}
 		else {
 			return " for share";
 		}
 	}
 
 	@Override
 	public boolean supportsRowValueConstructorSyntax() {
 		return true;
 	}
 	
 	@Override
 	public String getForUpdateNowaitString() {
 		return getForUpdateString() + " nowait ";
 	}
 	
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases ) + " nowait ";
 	}
 
 	@Override
 	public CallableStatementSupport getCallableStatementSupport() {
 		return PostgresCallableStatementSupport.INSTANCE;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		if ( position != 1 ) {
 			throw new UnsupportedOperationException( "PostgreSQL only supports REF_CURSOR parameters as the first parameter" );
 		}
 		return (ResultSet) statement.getObject( 1 );
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException( "PostgreSQL only supports accessing REF_CURSOR parameters by name" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
index 2c6448498b..faa67d3a60 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
@@ -1,237 +1,245 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect compatible with SAP DB.
  *
  * @author Brad Clow
  */
 public class SAPDBDialect extends Dialect {
 	/**
 	 * Constructs a SAPDBDialect
 	 */
 	public SAPDBDialect() {
 		super();
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.BIGINT, "fixed(19,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "fixed(3,0)" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.NUMERIC, "fixed($p,$s)" );
 		registerColumnType( Types.CLOB, "long varchar" );
 		registerColumnType( Types.BLOB, "long byte" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power" ) );
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "greatest", new StandardSQLFunction( "greatest" ) );
 		registerFunction( "least", new StandardSQLFunction( "least" ) );
 
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "second(?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "minute(?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hour(?1)" ) );
 		registerFunction( "day", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "day(?1)" ) );
 		registerFunction( "month", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "month(?1)" ) );
 		registerFunction( "year", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "year(?1)" ) );
 
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1(?3)" ) );
 
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekofyear", new StandardSQLFunction( "weekofyear", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "initcap", new StandardSQLFunction( "initcap", StandardBasicTypes.STRING ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "lfill", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "rfill", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "index", new StandardSQLFunction( "index", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "value", new StandardSQLFunction( "value" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new StandardSQLFunction( "index", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "value" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 )
 				.append( " foreign key " )
 				.append( constraintName )
 				.append( " (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " primary key ";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return " null";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from domain.sequences";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "ignore rollback";
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new LocalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						return "temp." + super.generateIdTableName( baseName );
+					}
+
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "ignore rollback";
+					}
+				},
+				AfterUseAction.DROP,
+				null
+		);
 	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		return "temp." + super.generateTemporaryTableName( baseTableName );
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
index e713cb8b58..54918fdcfc 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Teradata14Dialect.java
@@ -1,223 +1,218 @@
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
 package org.hibernate.dialect;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Map;
 /**
  * A dialect for the Teradata database
  *
  */
 public class Teradata14Dialect extends TeradataDialect {
 	/**
 	 * Constructor
 	 */
 	public Teradata14Dialect() {
 		super();
 		//registerColumnType data types
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.BINARY, "VARBYTE(100)" );
 		registerColumnType( Types.LONGVARBINARY, "VARBYTE(32000)" );
 		registerColumnType( Types.LONGVARCHAR, "VARCHAR(32000)" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE,DEFAULT_BATCH_SIZE );
 
 		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIME, "current_time" ) );
 		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.DATE, "current_date" ) );
 	}
 
 	@Override
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "Add";
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * <tt>java.sql.Types</tt> typecode.
 	 *
 	 * @param code <tt>java.sql.Types</tt> typecode
 	 * @param length the length or precision of the column
 	 * @param precision the precision of the column
 	 * @param scale the scale of the column
 	 *
 	 * @return the database type name
 	 *
 	 * @throws HibernateException
 	 */
 	 public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		/*
 		 * We might want a special case for 19,2. This is very common for money types
 		 * and here it is converted to 18,1
 		 */
 		float f = precision > 0 ? ( float ) scale / ( float ) precision : 0;
 		int p = ( precision > 38 ? 38 : precision );
 		int s = ( precision > 38 ? ( int ) ( 38.0 * f ) : ( scale > 38 ? 38 : scale ) );
 		return super.getTypeName( code, length, p, s );
 	}
 
 	@Override
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	@Override
 	public String getIdentityColumnString() {
 		return "generated by default as identity not null";
 	}
 
 	@Override
 	public String getIdentityInsertString() {
 		return "null";
 	}
 
 	@Override
-	public String getDropTemporaryTableString() {
-		return "drop temporary table";
-	}
-
-	@Override
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 
 	@Override
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return false;
 	}
 
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		statement.registerOutParameter(col, Types.REF);
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement cs) throws SQLException {
 		boolean isResultSet = cs.execute();
 		while (!isResultSet && cs.getUpdateCount() != -1) {
 			isResultSet = cs.getMoreResults();
 		}
 		return cs.getResultSet();
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			int errorCode = sqle.getErrorCode();
 			if (errorCode == 27003) {
 				constraintName = extractUsingTemplate("Unique constraint (", ") violated.", sqle.getMessage());
 			} else if (errorCode == 2700) {
 				constraintName = extractUsingTemplate("Referential constraint", "violation:", sqle.getMessage());
 			} else if (errorCode == 5317) {
 				constraintName = extractUsingTemplate("Check constraint (", ") violated.", sqle.getMessage());
 			}
 
 			if (constraintName != null) {
 				int i = constraintName.indexOf('.');
 				if (i != -1) {
 					constraintName = constraintName.substring(i + 1);
 				}
 			}
 			return constraintName;
 		}
 	};
 
 	@Override
 	public String getWriteLockString(int timeout) {
 		String sMsg = " Locking row for write ";
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return sMsg + " nowait ";
 		}
 		return sMsg;
 	}
 
 	@Override
 	public String getReadLockString(int timeout) {
 		String sMsg = " Locking row for read  ";
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return sMsg + " nowait ";
 		}
 		return sMsg;
 	}
 
 	@Override
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString() + " " + sql;
 	}
 
 	@Override
 	public boolean useFollowOnLocking() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
index 942057aa64..f9f4b80867 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TeradataDialect.java
@@ -1,269 +1,279 @@
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
 package org.hibernate.dialect;
 import java.sql.Types;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.hql.spi.id.IdTableSupport;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for the Teradata database created by MCR as part of the
  * dialect certification process.
  *
  * @author Jay Nance
  */
-public class TeradataDialect extends Dialect {
+public class TeradataDialect extends Dialect implements IdTableSupport {
 	
 	private static final int PARAM_LIST_SIZE_LIMIT = 1024;
 
 	/**
 	 * Constructor
 	 */
 	public TeradataDialect() {
 		super();
 		//registerColumnType data types
 		registerColumnType( Types.NUMERIC, "NUMERIC($p,$s)" );
 		registerColumnType( Types.DOUBLE, "DOUBLE PRECISION" );
 		registerColumnType( Types.BIGINT, "NUMERIC(18,0)" );
 		registerColumnType( Types.BIT, "BYTEINT" );
 		registerColumnType( Types.TINYINT, "BYTEINT" );
 		registerColumnType( Types.VARBINARY, "VARBYTE($l)" );
 		registerColumnType( Types.BINARY, "BYTEINT" );
 		registerColumnType( Types.LONGVARCHAR, "LONG VARCHAR" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.DECIMAL, "DECIMAL" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.BOOLEAN, "BYTEINT" );  // hibernate seems to ignore this type...
 		registerColumnType( Types.BLOB, "BLOB" );
 		registerColumnType( Types.CLOB, "CLOB" );
 
 		registerFunction( "year", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "extract(year from ?1)" ) );
 		registerFunction( "length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "character_length(?1)" ) );
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1 from ?2 for ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.STRING, "position(?1 in ?2)" ) );
 		registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.STRING, "?1 mod ?2" ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(255))" ) );
 
 		// bit_length feels a bit broken to me. We have to cast to char in order to
 		// pass when a numeric value is supplied. But of course the answers given will
 		// be wildly different for these two datatypes. 1234.5678 will be 9 bytes as
 		// a char string but will be 8 or 16 bytes as a true numeric.
 		// Jay Nance 2006-09-22
 		registerFunction(
 				"bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "octet_length(cast(?1 as char))*4" )
 		);
 
 		// The preference here would be
 		//   SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_timestamp(?1)", false)
 		// but this appears not to work.
 		// Jay Nance 2006-09-22
 		registerFunction( "current_timestamp", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_timestamp" ) );
 		registerFunction( "current_time", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_time" ) );
 		registerFunction( "current_date", new SQLFunctionTemplate( StandardBasicTypes.TIMESTAMP, "current_date" ) );
 		// IBID for current_time and current_date
 
 		registerKeyword( "password" );
 		registerKeyword( "type" );
 		registerKeyword( "title" );
 		registerKeyword( "year" );
 		registerKeyword( "month" );
 		registerKeyword( "summary" );
 		registerKeyword( "alias" );
 		registerKeyword( "value" );
 		registerKeyword( "first" );
 		registerKeyword( "role" );
 		registerKeyword( "account" );
 		registerKeyword( "class" );
 
 		// Tell hibernate to use getBytes instead of getBinaryStream
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
 		// No batch statements
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
 	}
 
 	/**
 	 * Does this dialect support the <tt>FOR UPDATE</tt> syntax?
 	 *
 	 * @return empty string ... Teradata does not support <tt>FOR UPDATE<tt> syntax
 	 */
 	public String getForUpdateString() {
 		return "";
 	}
 
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	public String getAddColumnString() {
 		return "Add Column";
 	}
 
-	public boolean supportsTemporaryTables() {
-		return true;
+	@Override
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy( this, AfterUseAction.CLEAN );
 	}
 
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
+	@Override
+	public String generateIdTableName(String baseName) {
+		return IdTableSupportStandardImpl.INSTANCE.generateIdTableName( baseName );
 	}
 
-	public String getCreateTemporaryTablePostfix() {
-		return " on commit preserve rows";
+	@Override
+	public String getCreateIdTableCommand() {
+		return "create global temporary table";
 	}
 
-	public Boolean performTemporaryTableDDLInIsolation() {
-		return Boolean.TRUE;
+	@Override
+	public String getCreateIdTableStatementOptions() {
+		return " on commit preserve rows";
 	}
 
-	public boolean dropTemporaryTableAfterUse() {
-		return false;
+	@Override
+	public String getDropIdTableCommand() {
+		return "drop table";
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * <tt>java.sql.Types</tt> typecode.
 	 *
 	 * @param code <tt>java.sql.Types</tt> typecode
 	 * @param length the length or precision of the column
 	 * @param precision the precision of the column
 	 * @param scale the scale of the column
 	 *
 	 * @return the database type name
 	 *
 	 * @throws HibernateException
 	 */
 	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		/*
 		 * We might want a special case for 19,2. This is very common for money types
 		 * and here it is converted to 18,1
 		 */
 		float f = precision > 0 ? ( float ) scale / ( float ) precision : 0;
 		int p = ( precision > 18 ? 18 : precision );
 		int s = ( precision > 18 ? ( int ) ( 18.0 * f ) : ( scale > 18 ? 18 : scale ) );
 
 		return super.getTypeName( code, length, p, s );
 	}
 
 	public boolean supportsCascadeDelete() {
 		return false;
 	}
 
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return false;
 	}
 
 	public boolean areStringComparisonsCaseInsensitive() {
 		return true;
 	}
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	public String getSelectClauseNullString(int sqlType) {
 		String v = "null";
 
 		switch ( sqlType ) {
 			case Types.BIT:
 			case Types.TINYINT:
 			case Types.SMALLINT:
 			case Types.INTEGER:
 			case Types.BIGINT:
 			case Types.FLOAT:
 			case Types.REAL:
 			case Types.DOUBLE:
 			case Types.NUMERIC:
 			case Types.DECIMAL:
 				v = "cast(null as decimal)";
 				break;
 			case Types.CHAR:
 			case Types.VARCHAR:
 			case Types.LONGVARCHAR:
 				v = "cast(null as varchar(255))";
 				break;
 			case Types.DATE:
 			case Types.TIME:
 			case Types.TIMESTAMP:
 				v = "cast(null as timestamp)";
 				break;
 			case Types.BINARY:
 			case Types.VARBINARY:
 			case Types.LONGVARBINARY:
 			case Types.NULL:
 			case Types.OTHER:
 			case Types.JAVA_OBJECT:
 			case Types.DISTINCT:
 			case Types.STRUCT:
 			case Types.ARRAY:
 			case Types.BLOB:
 			case Types.CLOB:
 			case Types.REF:
 			case Types.DATALINK:
 			case Types.BOOLEAN:
 				break;
 		}
 		return v;
 	}
 
 	public String getCreateMultisetTableString() {
 		return "create multiset table ";
 	}
 
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return true;
 	}
 
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return true;
 	}
 
 	public boolean supportsBindAsCallableArgument() {
 		return false;
 	}
 
 	/* (non-Javadoc)
 		 * @see org.hibernate.dialect.Dialect#getInExpressionCountLimit()
 		 */
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
index 44f1ae4ea2..808305385f 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/TimesTenDialect.java
@@ -1,277 +1,285 @@
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
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.LockMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.lock.UpdateLockingStrategy;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A SQL dialect for TimesTen 5.1.
  * <p/>
  * Known limitations:
  * joined-subclass support because of no CASE support in TimesTen
  * No support for subqueries that includes aggregation
  * - size() in HQL not supported
  * - user queries that does subqueries with aggregation
  * No CLOB/BLOB support
  * No cascade delete support.
  * No Calendar support
  * No support for updating primary keys.
  *
  * @author Sherry Listgarten and Max Andersen
  */
 @SuppressWarnings("deprecation")
 public class TimesTenDialect extends Dialect {
 	/**
 	 * Constructs a TimesTenDialect
 	 */
 	public TimesTenDialect() {
 		super();
 		registerColumnType( Types.BIT, "TINYINT" );
 		registerColumnType( Types.BIGINT, "BIGINT" );
 		registerColumnType( Types.SMALLINT, "SMALLINT" );
 		registerColumnType( Types.TINYINT, "TINYINT" );
 		registerColumnType( Types.INTEGER, "INTEGER" );
 		registerColumnType( Types.CHAR, "CHAR(1)" );
 		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
 		registerColumnType( Types.FLOAT, "FLOAT" );
 		registerColumnType( Types.DOUBLE, "DOUBLE" );
 		registerColumnType( Types.DATE, "DATE" );
 		registerColumnType( Types.TIME, "TIME" );
 		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
 		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
 		registerColumnType( Types.NUMERIC, "DECIMAL($p, $s)" );
 		// TimesTen has no BLOB/CLOB support, but these types may be suitable 
 		// for some applications. The length is limited to 4 million bytes.
 		registerColumnType( Types.BLOB, "VARBINARY(4000000)" );
 		registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
 
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim" ) );
 		registerFunction( "concat", new StandardSQLFunction( "concat", StandardBasicTypes.STRING ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod" ) );
 		registerFunction( "to_char", new StandardSQLFunction( "to_char", StandardBasicTypes.STRING ) );
 		registerFunction( "to_date", new StandardSQLFunction( "to_date", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "getdate", new NoArgSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP, false ) );
 		registerFunction( "nvl", new StandardSQLFunction( "nvl" ) );
 
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	@Override
 	public boolean qualifyIndexName() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select first 1 " + sequenceName + ".nextval from sys.tables";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select NAME from sys.sequences";
 	}
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	@Override
 	public String getForUpdateString() {
 		return "";
 	}
 
 	@Override
 	public boolean supportsColumnCheck() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTableCheck() {
 		return false;
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select first 1 sysdate from sys.tables";
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	@Override
-	public String generateTemporaryTableName(String baseTableName) {
-		final String name = super.generateTemporaryTableName( baseTableName );
-		return name.length() > 30 ? name.substring( 1, 30 ) : name;
-	}
-
-	@Override
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
-	}
-
-	@Override
-	public String getCreateTemporaryTablePostfix() {
-		return "on commit delete rows";
+	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
+		return new GlobalTemporaryTableBulkIdStrategy(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String generateIdTableName(String baseName) {
+						final String name = super.generateIdTableName( baseName );
+						return name.length() > 30 ? name.substring( 1, 30 ) : name;
+					}
+
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create global temporary table";
+					}
+
+					@Override
+					public String getCreateIdTableStatementOptions() {
+						return "on commit delete rows";
+					}
+				},
+				AfterUseAction.CLEAN
+		);
 	}
 
 	@Override
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		// TimesTen has no known variation of a "SELECT ... FOR UPDATE" syntax...
 		if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT ) {
 			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_WRITE ) {
 			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
 			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC ) {
 			return new OptimisticLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT ) {
 			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 		}
 		else if ( lockMode.greaterThan( LockMode.READ ) ) {
 			return new UpdateLockingStrategy( lockable, lockMode );
 		}
 		else {
 			return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
index 9fb74e5e66..a895992743 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
@@ -1,63 +1,63 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.hql.internal.ast.exec;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 
 /**
  * Implementation of MultiTableDeleteExecutor.
  *
  * @author Steve Ebersole
  */
 public class MultiTableDeleteExecutor implements StatementExecutor {
 	private final MultiTableBulkIdStrategy.DeleteHandler deleteHandler;
 
 	public MultiTableDeleteExecutor(HqlSqlWalker walker) {
 		final MultiTableBulkIdStrategy strategy = walker.getSessionFactoryHelper().getFactory().getSettings()
 				.getMultiTableBulkIdStrategy();
 		this.deleteHandler = strategy.buildDeleteHandler( walker.getSessionFactoryHelper().getFactory(), walker );
 	}
 
 	public String[] getSqlStatements() {
 		return deleteHandler.getSqlStatements();
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, deleteHandler.getTargetedQueryable() );
 		if ( session.isEventSource() ) {
 			( (EventSource) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 
 		return deleteHandler.execute( session, parameters );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java
index b78afe8f3f..47991563d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java
@@ -1,67 +1,67 @@
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
 package org.hibernate.hql.internal.ast.exec;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 
 /**
  * Implementation of MultiTableUpdateExecutor.
  *
  * @author Steve Ebersole
  */
 public class MultiTableUpdateExecutor implements StatementExecutor {
 	private final MultiTableBulkIdStrategy.UpdateHandler updateHandler;
 
 	public MultiTableUpdateExecutor(HqlSqlWalker walker) {
 		MultiTableBulkIdStrategy strategy = walker.getSessionFactoryHelper()
 				.getFactory()
 				.getSettings()
 				.getMultiTableBulkIdStrategy();
 		this.updateHandler = strategy.buildUpdateHandler( walker.getSessionFactoryHelper().getFactory(), walker );
 	}
 
 	public String[] getSqlStatements() {
 		return updateHandler.getSqlStatements();
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
 		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, updateHandler.getTargetedQueryable() );
 
 		if ( session.isEventSource() ) {
 			( (EventSource) session ).getActionQueue().addAction( action );
 		}
 		else {
 			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
 
 		return updateHandler.execute( session, parameters );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/GlobalTemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/GlobalTemporaryTableBulkIdStrategy.java
deleted file mode 100644
index 04f2390384..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/GlobalTemporaryTableBulkIdStrategy.java
+++ /dev/null
@@ -1,165 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.hql.spi;
-
-import java.sql.PreparedStatement;
-import java.util.ArrayList;
-import java.util.List;
-
-import org.hibernate.boot.spi.MetadataImplementor;
-import org.hibernate.engine.config.spi.ConfigurationService;
-import org.hibernate.engine.config.spi.StandardConverters;
-import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Table;
-import org.hibernate.persister.entity.Queryable;
-
-/**
- * Strategy based on ANSI SQL's definition of a "global temporary table".
- *
- * @author Steve Ebersole
- */
-public class GlobalTemporaryTableBulkIdStrategy implements MultiTableBulkIdStrategy {
-	public static final String CLEAN_UP_ID_TABLES = "hibernate.hql.bulk_id_strategy.global_temporary.clean_up";
-
-	public static final String SHORT_NAME = "global_temporary";
-
-	private boolean cleanUpTables;
-	private List<String> tableCleanUpDdl;
-
-	@Override
-	public void prepare(
-			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess,
-			MetadataImplementor metadata) {
-		final ConfigurationService configService = metadata.getMetadataBuildingOptions()
-				.getServiceRegistry()
-				.getService( ConfigurationService.class );
-		this.cleanUpTables = configService.getSetting(
-				CLEAN_UP_ID_TABLES,
-				StandardConverters.BOOLEAN,
-				false
-		);
-
-		final List<Table> idTableDefinitions = new ArrayList<Table>();
-
-		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
-			if ( !MultiTableBulkIdHelper.INSTANCE.needsIdTable( entityBinding ) ) {
-				continue;
-			}
-			final Table idTableDefinition = generateIdTableDefinition( entityBinding, metadata );
-			idTableDefinitions.add( idTableDefinition );
-
-			if ( cleanUpTables ) {
-				if ( tableCleanUpDdl == null ) {
-					tableCleanUpDdl = new ArrayList<String>();
-				}
-				tableCleanUpDdl.add( idTableDefinition.sqlDropString( jdbcServices.getDialect(), null, null  ) );
-			}
-		}
-
-		// we export them all at once to better reuse JDBC resources
-		exportTableDefinitions( idTableDefinitions, jdbcServices, connectionAccess, metadata );
-	}
-
-	protected Table generateIdTableDefinition(PersistentClass entityMapping, MetadataImplementor metadata) {
-		return MultiTableBulkIdHelper.INSTANCE.generateIdTableDefinition(
-				entityMapping,
-				null,
-				null,
-				false
-		);
-	}
-
-	protected void exportTableDefinitions(
-			List<Table> idTableDefinitions,
-			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess,
-			MetadataImplementor metadata) {
-		MultiTableBulkIdHelper.INSTANCE.exportTableDefinitions(
-				idTableDefinitions,
-				jdbcServices,
-				connectionAccess,
-				metadata
-		);
-	}
-
-	@Override
-	public void release(
-			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess) {
-		if ( ! cleanUpTables ) {
-			return;
-		}
-
-		MultiTableBulkIdHelper.INSTANCE.cleanupTableDefinitions( jdbcServices, connectionAccess, tableCleanUpDdl );
-	}
-
-	@Override
-	public UpdateHandler buildUpdateHandler(
-			SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedUpdateHandlerImpl( factory, walker ) {
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				// clean up our id-table rows
-				cleanUpRows( determineIdTableName( persister ), session );
-			}
-		};
-	}
-
-	private void cleanUpRows(String tableName, SessionImplementor session) {
-		final String sql = "delete from " + tableName;
-		PreparedStatement ps = null;
-		try {
-			ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
-			session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
-		}
-		finally {
-			if ( ps != null ) {
-				try {
-					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
-				}
-				catch( Throwable ignore ) {
-					// ignore
-				}
-			}
-		}
-	}
-
-	@Override
-	public DeleteHandler buildDeleteHandler(
-			SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedDeleteHandlerImpl( factory, walker ) {
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				// clean up our id-table rows
-				cleanUpRows( determineIdTableName( persister ), session );
-			}
-		};
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
deleted file mode 100644
index 29f24cd4e9..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
+++ /dev/null
@@ -1,259 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.hql.spi;
-
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import java.sql.Types;
-import java.util.ArrayList;
-import java.util.List;
-
-import org.hibernate.HibernateException;
-import org.hibernate.JDBCException;
-import org.hibernate.boot.spi.MetadataImplementor;
-import org.hibernate.cfg.AvailableSettings;
-import org.hibernate.engine.config.spi.ConfigurationService;
-import org.hibernate.engine.config.spi.StandardConverters;
-import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.internal.AbstractSessionImpl;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Table;
-import org.hibernate.persister.entity.Queryable;
-import org.hibernate.sql.SelectValues;
-import org.hibernate.type.UUIDCharType;
-
-import org.jboss.logging.Logger;
-
-/**
- * This is a strategy that mimics temporary tables for databases which do not support
- * temporary tables.  It follows a pattern similar to the ANSI SQL definition of global
- * temporary table using a "session id" column to segment rows from the various sessions.
- *
- * @author Steve Ebersole
- */
-public class PersistentTableBulkIdStrategy implements MultiTableBulkIdStrategy {
-	private static final CoreMessageLogger log = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			PersistentTableBulkIdStrategy.class.getName()
-	);
-
-	public static final String SHORT_NAME = "persistent";
-
-	public static final String CLEAN_UP_ID_TABLES = "hibernate.hql.bulk_id_strategy.persistent.clean_up";
-	public static final String SCHEMA = "hibernate.hql.bulk_id_strategy.persistent.schema";
-	public static final String CATALOG = "hibernate.hql.bulk_id_strategy.persistent.catalog";
-
-	private String catalog;
-	private String schema;
-	private boolean cleanUpTables;
-	private List<String> tableCleanUpDdl;
-
-	/**
-	 * Creates the tables for all the entities that might need it
-	 *
-	 * @param jdbcServices The JdbcService object
-	 * @param connectionAccess Access to the JDBC Connection
-	 * @param metadata Access to the O/RM mapping information
-	 */
-	@Override
-	public void prepare(
-			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess,
-			MetadataImplementor metadata) {
-		final ConfigurationService configService = metadata.getMetadataBuildingOptions()
-				.getServiceRegistry()
-				.getService( ConfigurationService.class );
-		this.catalog = configService.getSetting(
-				CATALOG,
-				StandardConverters.STRING,
-				configService.getSetting( AvailableSettings.DEFAULT_CATALOG, StandardConverters.STRING )
-		);
-		this.schema = configService.getSetting(
-				SCHEMA,
-				StandardConverters.STRING,
-				configService.getSetting( AvailableSettings.DEFAULT_SCHEMA, StandardConverters.STRING )
-		);
-		this.cleanUpTables = configService.getSetting(
-				CLEAN_UP_ID_TABLES,
-				StandardConverters.BOOLEAN,
-				false
-		);
-
-		final List<Table> idTableDefinitions = new ArrayList<Table>();
-
-		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
-			if ( !MultiTableBulkIdHelper.INSTANCE.needsIdTable( entityBinding ) ) {
-				continue;
-			}
-			final Table idTableDefinition = generateIdTableDefinition( entityBinding, metadata );
-			idTableDefinitions.add( idTableDefinition );
-
-			if ( cleanUpTables ) {
-				if ( tableCleanUpDdl == null ) {
-					tableCleanUpDdl = new ArrayList<String>();
-				}
-				tableCleanUpDdl.add( idTableDefinition.sqlDropString( jdbcServices.getDialect(), null, null  ) );
-			}
-		}
-
-		// we export them all at once to better reuse JDBC resources
-		exportTableDefinitions( idTableDefinitions, jdbcServices, connectionAccess, metadata );
-	}
-
-	protected Table generateIdTableDefinition(PersistentClass entityMapping, MetadataImplementor metadata) {
-		return MultiTableBulkIdHelper.INSTANCE.generateIdTableDefinition(
-				entityMapping,
-				catalog,
-				schema,
-				true
-		);
-	}
-
-	protected void exportTableDefinitions(
-			List<Table> idTableDefinitions,
-			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess,
-			MetadataImplementor metadata) {
-		MultiTableBulkIdHelper.INSTANCE.exportTableDefinitions(
-				idTableDefinitions,
-				jdbcServices,
-				connectionAccess,
-				metadata
-		);
-	}
-
-	@Override
-	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
-		if ( ! cleanUpTables ) {
-			return;
-		}
-
-		MultiTableBulkIdHelper.INSTANCE.cleanupTableDefinitions( jdbcServices, connectionAccess, tableCleanUpDdl );
-	}
-
-	@Override
-	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedUpdateHandlerImpl( factory, walker, catalog, schema ) {
-			@Override
-			protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
-				selectClause.addParameter( Types.CHAR, 36 );
-			}
-
-			@Override
-			protected String generateIdSubselect(Queryable persister) {
-				return super.generateIdSubselect( persister ) + " where hib_sess_id=?";
-			}
-
-			@Override
-			protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
-				bindSessionIdentifier( ps, session, pos );
-				return 1;
-			}
-
-			@Override
-			protected void handleAddedParametersOnUpdate(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
-				bindSessionIdentifier( ps, session, position );
-			}
-
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				// clean up our id-table rows
-				cleanUpRows( determineIdTableName( persister ), session );
-			}
-		};
-	}
-
-	private void bindSessionIdentifier(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
-		if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
-			throw new HibernateException( "Only available on SessionImpl instances" );
-		}
-		UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), position, session );
-	}
-
-	private void cleanUpRows(String tableName, SessionImplementor session) {
-		final String sql = "delete from " + tableName + " where hib_sess_id=?";
-		try {
-			PreparedStatement ps = null;
-			try {
-				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
-				bindSessionIdentifier( ps, session, 1 );
-				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
-			}
-			finally {
-				if ( ps != null ) {
-					try {
-						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
-					}
-					catch( Throwable ignore ) {
-						// ignore
-					}
-				}
-			}
-		}
-		catch (SQLException e) {
-			throw convert( session.getFactory(), e, "Unable to clean up id table [" + tableName + "]", sql );
-		}
-	}
-
-	protected JDBCException convert(SessionFactoryImplementor factory, SQLException e, String message, String sql) {
-		throw factory.getSQLExceptionHelper().convert( e, message, sql );
-	}
-
-	@Override
-	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedDeleteHandlerImpl( factory, walker, catalog, schema ) {
-			@Override
-			protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
-				selectClause.addParameter( Types.CHAR, 36 );
-			}
-
-			@Override
-			protected String generateIdSubselect(Queryable persister) {
-				return super.generateIdSubselect( persister ) + " where hib_sess_id=?";
-			}
-
-			@Override
-			protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
-				bindSessionIdentifier( ps, session, pos );
-				return 1;
-			}
-
-			@Override
-			protected void handleAddedParametersOnDelete(PreparedStatement ps, SessionImplementor session) throws SQLException {
-				bindSessionIdentifier( ps, session, 1 );
-			}
-
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				// clean up our id-table rows
-				cleanUpRows( determineIdTableName( persister ), session );
-			}
-		};
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
deleted file mode 100644
index 758b034646..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
+++ /dev/null
@@ -1,64 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.hql.spi;
-
-import org.hibernate.boot.spi.MetadataImplementor;
-import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.internal.log.DeprecationLogger;
-
-/**
- * @author Steve Ebersole
- *
- * @deprecated Use the more specific {@link org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy} instead.
- */
-@Deprecated
-public class TemporaryTableBulkIdStrategy extends LocalTemporaryTableBulkIdStrategy {
-	public static final TemporaryTableBulkIdStrategy INSTANCE = new TemporaryTableBulkIdStrategy();
-
-	public static final String SHORT_NAME = "temporary";
-
-	@Override
-	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata) {
-		DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfTemporaryTableBulkIdStrategy();
-		super.prepare( jdbcServices, connectionAccess, metadata );
-	}
-
-	@Override
-	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
-		super.release( jdbcServices, connectionAccess );
-	}
-
-	@Override
-	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return super.buildUpdateHandler( factory, walker );
-	}
-
-	@Override
-	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return super.buildDeleteHandler( factory, walker );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/AbstractMultiTableBulkIdStrategyImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/AbstractMultiTableBulkIdStrategyImpl.java
new file mode 100644
index 0000000000..ad06120d7c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/AbstractMultiTableBulkIdStrategyImpl.java
@@ -0,0 +1,213 @@
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
+package org.hibernate.hql.spi.id;
+
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.QueryException;
+import org.hibernate.boot.model.relational.QualifiedTableName;
+import org.hibernate.boot.spi.MetadataBuildingOptions;
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.Queryable;
+
+/**
+ * Convenience base class for MultiTableBulkIdStrategy implementations.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractMultiTableBulkIdStrategyImpl<TT extends IdTableInfo, CT extends AbstractMultiTableBulkIdStrategyImpl.PreparationContext>
+		implements MultiTableBulkIdStrategy {
+
+	private final IdTableSupport idTableSupport;
+	private Map<String,TT> idTableInfoMap = new HashMap<String, TT>();
+
+	public AbstractMultiTableBulkIdStrategyImpl(IdTableSupport idTableSupport) {
+		this.idTableSupport = idTableSupport;
+	}
+
+	public IdTableSupport getIdTableSupport() {
+		return idTableSupport;
+	}
+
+	@Override
+	public final void prepare(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata,
+			SessionFactoryOptions sessionFactoryOptions) {
+		// build/get Table representation of the bulk-id tables - subclasses need hooks
+		// for each:
+		// 		handle DDL
+		// 		build insert-select
+		//		build id-subselect
+
+		final CT context =  buildPreparationContext();
+
+		initialize( metadata.getMetadataBuildingOptions(), sessionFactoryOptions );
+
+		final JdbcEnvironment jdbcEnvironment = jdbcServices.getJdbcEnvironment();
+
+		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
+			if ( !IdTableHelper.INSTANCE.needsIdTable( entityBinding ) ) {
+				continue;
+			}
+
+			final String idTableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
+					determineIdTableName( jdbcEnvironment, entityBinding ),
+					jdbcEnvironment.getDialect()
+			);
+			final Table idTable = new Table();
+			idTable.setName( idTableName );
+			idTable.setComment( "Used to hold id values for the " + entityBinding.getEntityName() + " entity" );
+
+			Iterator itr = entityBinding.getTable().getPrimaryKey().getColumnIterator();
+			while( itr.hasNext() ) {
+				Column column = (Column) itr.next();
+				idTable.addColumn( column.clone()  );
+			}
+			augmentIdTableDefinition( idTable );
+
+			final TT idTableInfo = buildIdTableInfo( entityBinding, idTable, jdbcServices, metadata, context );
+			idTableInfoMap.put( entityBinding.getEntityName(), idTableInfo );
+		}
+
+		finishPreparation( jdbcServices, connectionAccess, metadata, context );
+	}
+
+	protected CT buildPreparationContext() {
+		return null;
+	}
+
+
+	/**
+	 * Configure ourselves.  By default, nothing to do; here totally for subclass hook-in
+	 *
+	 * @param buildingOptions Access to user-defined Metadata building options
+	 * @param sessionFactoryOptions
+	 */
+	protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
+		// by default nothing to do
+	}
+
+	protected QualifiedTableName determineIdTableName(JdbcEnvironment jdbcEnvironment, PersistentClass entityBinding) {
+		final String entityPrimaryTableName = entityBinding.getTable().getName();
+		final String idTableName = getIdTableSupport().generateIdTableName( entityPrimaryTableName );
+
+		// by default no explicit catalog/schema
+		return new QualifiedTableName(
+				null,
+				null,
+				jdbcEnvironment.getIdentifierHelper().toIdentifier( idTableName )
+		);
+
+	}
+
+	protected void augmentIdTableDefinition(Table idTable) {
+		// by default nothing to do
+	}
+
+	protected abstract TT buildIdTableInfo(
+			PersistentClass entityBinding,
+			Table idTable,
+			JdbcServices jdbcServices,
+			MetadataImplementor metadata,
+			CT context);
+
+
+	protected String buildIdTableCreateStatement(Table idTable, JdbcServices jdbcServices, MetadataImplementor metadata) {
+		final JdbcEnvironment jdbcEnvironment = jdbcServices.getJdbcEnvironment();
+		final Dialect dialect = jdbcEnvironment.getDialect();
+
+		StringBuilder buffer = new StringBuilder( getIdTableSupport().getCreateIdTableCommand() )
+				.append( ' ' )
+				.append( jdbcEnvironment.getQualifiedObjectNameFormatter().format( idTable.getQualifiedTableName(), dialect ) )
+				.append( " (" );
+
+		Iterator itr = idTable.getColumnIterator();
+		while ( itr.hasNext() ) {
+			final Column column = (Column) itr.next();
+			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
+			buffer.append( column.getSqlType( dialect, metadata ) );
+			if ( column.isNullable() ) {
+				buffer.append( dialect.getNullColumnString() );
+			}
+			else {
+				buffer.append( " not null" );
+			}
+			if ( itr.hasNext() ) {
+				buffer.append( ", " );
+			}
+		}
+
+		buffer.append( ") " );
+		if ( getIdTableSupport().getCreateIdTableStatementOptions() != null ) {
+			buffer.append( getIdTableSupport().getCreateIdTableStatementOptions() );
+		}
+
+		return buffer.toString();
+	}
+
+	protected String buildIdTableDropStatement(Table idTable, JdbcServices jdbcServices) {
+		final JdbcEnvironment jdbcEnvironment = jdbcServices.getJdbcEnvironment();
+		final Dialect dialect = jdbcEnvironment.getDialect();
+
+		return getIdTableSupport().getDropIdTableCommand() + " "
+				+ jdbcEnvironment.getQualifiedObjectNameFormatter().format( idTable.getQualifiedTableName(), dialect );
+	}
+
+	protected void finishPreparation(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata,
+			CT context) {
+	}
+
+	protected TT getIdTableInfo(Queryable targetedPersister) {
+		return getIdTableInfo( targetedPersister.getEntityName() );
+	}
+
+	protected TT getIdTableInfo(String entityName) {
+		TT tableInfo = idTableInfoMap.get( entityName );
+		if ( tableInfo == null ) {
+			throw new QueryException( "Entity does not have an id table for multi-table handling : " + entityName );
+		}
+		return tableInfo;
+	}
+
+	public static interface PreparationContext {
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/AbstractTableBasedBulkIdHandler.java
similarity index 66%
rename from hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/id/AbstractTableBasedBulkIdHandler.java
index 4e6e9ff936..c28da9ceaa 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/AbstractTableBasedBulkIdHandler.java
@@ -1,183 +1,213 @@
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
-package org.hibernate.hql.spi;
+package org.hibernate.hql.spi.id;
 
 import java.sql.SQLException;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.mapping.Table;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.InsertSelect;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectValues;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 
 /**
+ * Convenience base class for {@link MultiTableBulkIdStrategy.UpdateHandler}
+ * and {@link MultiTableBulkIdStrategy.DeleteHandler} implementations through
+ * {@link TableBasedUpdateHandlerImpl} and {@link TableBasedDeleteHandlerImpl} respectively.
+ * <p/>
+ * Mainly supports common activities like:<ul>
+ *     <li>processing the original {@code WHERE} clause (if one) - {@link #processWhereClause}</li>
+ *     <li>generating the proper {@code SELECT} clause for the id-table insert - {@link #generateIdInsertSelect}</li>
+ *     <li>generating the sub-select from the id-table - {@link #generateIdSubselect}</li>
+ * </ul>
+ *
  * @author Steve Ebersole
  */
 public abstract class AbstractTableBasedBulkIdHandler {
 	private final SessionFactoryImplementor sessionFactory;
 	private final HqlSqlWalker walker;
 
-	private final String catalog;
-	private final String schema;
-
 	public AbstractTableBasedBulkIdHandler(
 			SessionFactoryImplementor sessionFactory,
-			HqlSqlWalker walker,
-			String catalog,
-			String schema) {
+			HqlSqlWalker walker) {
 		this.sessionFactory = sessionFactory;
 		this.walker = walker;
-		this.catalog = catalog;
-		this.schema = schema;
 	}
 
 	protected SessionFactoryImplementor factory() {
 		return sessionFactory;
 	}
 
 	protected HqlSqlWalker walker() {
 		return walker;
 	}
 
+	public abstract Queryable getTargetedQueryable();
+
 	protected JDBCException convert(SQLException e, String message, String sql) {
 		throw factory().getSQLExceptionHelper().convert( e, message, sql );
 	}
 
 	protected static class ProcessedWhereClause {
 		public static final ProcessedWhereClause NO_WHERE_CLAUSE = new ProcessedWhereClause();
 
 		private final String userWhereClauseFragment;
 		private final List<ParameterSpecification> idSelectParameterSpecifications;
 
 		private ProcessedWhereClause() {
 			this( "", Collections.<ParameterSpecification>emptyList() );
 		}
 
 		public ProcessedWhereClause(String userWhereClauseFragment, List<ParameterSpecification> idSelectParameterSpecifications) {
 			this.userWhereClauseFragment = userWhereClauseFragment;
 			this.idSelectParameterSpecifications = idSelectParameterSpecifications;
 		}
 
 		public String getUserWhereClauseFragment() {
 			return userWhereClauseFragment;
 		}
 
 		public List<ParameterSpecification> getIdSelectParameterSpecifications() {
 			return idSelectParameterSpecifications;
 		}
 	}
 
+	/**
+	 * Interprets the {@code WHERE} clause from the user-defined update/delete  query
+	 *
+	 * @param whereClause The user-defined {@code WHERE} clause
+	 *
+	 * @return The bulk-id-ready {@code WHERE} clause representation
+	 */
 	@SuppressWarnings("unchecked")
 	protected ProcessedWhereClause processWhereClause(AST whereClause) {
 		if ( whereClause.getNumberOfChildren() != 0 ) {
 			// If a where clause was specified in the update/delete query, use it to limit the
-			// returned ids here...
+			// ids that will be returned and inserted into the id table...
 			try {
 				SqlGenerator sqlGenerator = new SqlGenerator( sessionFactory );
 				sqlGenerator.whereClause( whereClause );
 				String userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
 				List<ParameterSpecification> idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
 
 				return new ProcessedWhereClause( userWhereClause, idSelectParameterSpecifications );
 			}
 			catch ( RecognitionException e ) {
 				throw new HibernateException( "Unable to generate id select for DML operation", e );
 			}
 		}
 		else {
 			return ProcessedWhereClause.NO_WHERE_CLAUSE;
 		}
 	}
 
-	protected String generateIdInsertSelect(Queryable persister, String tableAlias, ProcessedWhereClause whereClause) {
+	/**
+	 * Generate the {@code INSERT}-{@code SELECT} statement for holding matching ids.  This is the
+	 * {@code INSERT} used to populate the bulk-id table with ids matching the restrictions defined in the
+	 * original {@code WHERE} clause
+	 *
+	 * @param tableAlias The table alias to use for the entity
+	 * @param whereClause The processed representation for the user-defined {@code WHERE} clause.
+	 *
+	 * @return The {@code INSERT}-{@code SELECT} for populating the bulk-id table.
+	 */
+	protected String generateIdInsertSelect(
+			String tableAlias,
+			IdTableInfo idTableInfo,
+			ProcessedWhereClause whereClause) {
+
 		Select select = new Select( sessionFactory.getDialect() );
-		SelectValues selectClause = new SelectValues( sessionFactory.getDialect() )
-				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
+		SelectValues selectClause = new SelectValues( sessionFactory.getDialect() ).addColumns(
+				tableAlias,
+				getTargetedQueryable().getIdentifierColumnNames(),
+				getTargetedQueryable().getIdentifierColumnNames()
+		);
 		addAnyExtraIdSelectValues( selectClause );
 		select.setSelectClause( selectClause.render() );
 
-		String rootTableName = persister.getTableName();
-		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
-		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
+		String rootTableName = getTargetedQueryable().getTableName();
+		String fromJoinFragment = getTargetedQueryable().fromJoinFragment( tableAlias, true, false );
+		String whereJoinFragment = getTargetedQueryable().whereJoinFragment( tableAlias, true, false );
 
 		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
 
 		if ( whereJoinFragment == null ) {
 			whereJoinFragment = "";
 		}
 		else {
 			whereJoinFragment = whereJoinFragment.trim();
 			if ( whereJoinFragment.startsWith( "and" ) ) {
 				whereJoinFragment = whereJoinFragment.substring( 4 );
 			}
 		}
 
 		if ( whereClause.getUserWhereClauseFragment().length() > 0 ) {
 			if ( whereJoinFragment.length() > 0 ) {
 				whereJoinFragment += " and ";
 			}
 		}
 		select.setWhereClause( whereJoinFragment + whereClause.getUserWhereClauseFragment() );
 
 		InsertSelect insert = new InsertSelect( sessionFactory.getDialect() );
 		if ( sessionFactory.getSettings().isCommentsEnabled() ) {
-			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
+			insert.setComment( "insert-select for " + getTargetedQueryable().getEntityName() + " ids" );
 		}
-		insert.setTableName( determineIdTableName( persister ) );
+		insert.setTableName( idTableInfo.getQualifiedIdTableName() );
 		insert.setSelect( select );
 		return insert.toStatementString();
 	}
 
+	/**
+	 * Used from {@link #generateIdInsertSelect} to allow subclasses to define any extra
+	 * values to be selected (and therefore stored into the bulk-id table).  Used to store
+	 * session identifier, e.g.
+	 *
+	 * @param selectClause The SelectValues that defines the select clause of the insert statement.
+	 */
 	protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
 	}
 
-	protected String determineIdTableName(Queryable persister) {
-		// todo : use the identifier/name qualifier service once we pull that over to master
-		return Table.qualify( catalog, schema, persister.getTemporaryIdTableName() );
-	}
-
-	protected String generateIdSubselect(Queryable persister) {
+	protected String generateIdSubselect(Queryable persister, IdTableInfo idTableInfo) {
 		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
-				" from " + determineIdTableName( persister );
+				" from " + idTableInfo.getQualifiedIdTableName();
 	}
 
 	protected void prepareForUse(Queryable persister, SessionImplementor session) {
 	}
 
 	protected void releaseFromUse(Queryable persister, SessionImplementor session) {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdHelper.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableHelper.java
similarity index 67%
rename from hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdHelper.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableHelper.java
index e8fd8fdb19..3f7f3d7e5d 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableHelper.java
@@ -1,207 +1,171 @@
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
-package org.hibernate.hql.spi;
+package org.hibernate.hql.spi.id;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.Iterator;
 import java.util.List;
 
-import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.mapping.Column;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.Subclass;
-import org.hibernate.mapping.Table;
 import org.hibernate.mapping.UnionSubclass;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
-public class MultiTableBulkIdHelper {
-	private static final Logger log = Logger.getLogger( MultiTableBulkIdHelper.class );
+public class IdTableHelper {
+	private static final Logger log = Logger.getLogger( IdTableHelper.class );
 
 	/**
 	 * Singleton access
 	 */
-	public static final MultiTableBulkIdHelper INSTANCE = new MultiTableBulkIdHelper();
+	public static final IdTableHelper INSTANCE = new IdTableHelper();
 
-	private MultiTableBulkIdHelper() {
+	private IdTableHelper() {
 	}
 
 	public boolean needsIdTable(PersistentClass entityBinding) {
 		// need id table if the entity has secondary tables (joins)
 		if ( entityBinding.getJoinClosureSpan() > 0 ) {
 			return true;
 		}
 
 		// need an id table if the entity is part of either a JOINED or UNION inheritance
 		// hierarchy.  We do not allow inheritance strategy mixing, so work on that assumption
 		// here...
 		final RootClass rootEntityBinding = entityBinding.getRootClass();
 		final Iterator itr = rootEntityBinding.getSubclassIterator();
 		if ( itr.hasNext() ) {
 			final Subclass subclassEntityBinding = (Subclass) itr.next();
 			if ( subclassEntityBinding instanceof JoinedSubclass || subclassEntityBinding instanceof UnionSubclass ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 
-
-	public Table generateIdTableDefinition(
-			PersistentClass entityMapping,
-			String catalog,
-			String schema,
-			boolean generateSessionIdColumn) {
-		Table idTable = new Table( entityMapping.getTemporaryIdTableName() );
-		idTable.setComment( "Used to hold id values for the " + entityMapping.getEntityName() + " class" );
-
-		if ( catalog != null ) {
-			idTable.setCatalog( catalog );
-		}
-		if ( schema != null ) {
-			idTable.setSchema( schema );
-		}
-
-		Iterator itr = entityMapping.getTable().getPrimaryKey().getColumnIterator();
-		while( itr.hasNext() ) {
-			Column column = (Column) itr.next();
-			idTable.addColumn( column.clone()  );
-		}
-
-		if ( generateSessionIdColumn ) {
-			Column sessionIdColumn = new Column( "hib_sess_id" );
-			sessionIdColumn.setSqlType( "CHAR(36)" );
-			sessionIdColumn.setComment( "Used to hold the Hibernate Session identifier" );
-			idTable.addColumn( sessionIdColumn );
-		}
-
-		return idTable;
-	}
-
-	protected void exportTableDefinitions(
-			List<Table> idTableDefinitions,
+	public void executeIdTableCreationStatements(
+			List<String> creationStatements,
 			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess,
-			MetadataImplementor metadata) {
+			JdbcConnectionAccess connectionAccess) {
 		try {
 			Connection connection;
 			try {
 				connection = connectionAccess.obtainConnection();
 			}
 			catch (UnsupportedOperationException e) {
 				// assume this comes from org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl
 				log.debug( "Unable to obtain JDBC connection; assuming ID tables already exist or wont be needed" );
 				return;
 			}
 
 			try {
 				// TODO: session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement();
 				Statement statement = connection.createStatement();
-				for ( Table idTableDefinition : idTableDefinitions ) {
+				for ( String creationStatement : creationStatements ) {
 					try {
-						final String sql = idTableDefinition.sqlCreateString( jdbcServices.getDialect(), metadata, null, null );
-						jdbcServices.getSqlStatementLogger().logStatement( sql );
+						jdbcServices.getSqlStatementLogger().logStatement( creationStatement );
 						// TODO: ResultSetExtractor
-						statement.execute( sql );
+						statement.execute( creationStatement );
 					}
 					catch (SQLException e) {
-						log.debugf( "Error attempting to export id-table [%s] : %s", idTableDefinition.getName(), e.getMessage() );
+						log.debugf( "Error attempting to export id-table [%s] : %s", creationStatement, e.getMessage() );
 					}
 				}
 
 				// TODO
 //				session.getTransactionCoordinator().getJdbcCoordinator().release( statement );
 				statement.close();
 			}
 			catch (SQLException e) {
 				log.error( "Unable to use JDBC Connection to create Statement", e );
 			}
 			finally {
 				try {
 					connectionAccess.releaseConnection( connection );
 				}
 				catch (SQLException ignore) {
 				}
 			}
 		}
 		catch (SQLException e) {
 			log.error( "Unable obtain JDBC Connection", e );
 		}
 	}
 
-	public void cleanupTableDefinitions(
+	public void executeIdTableDropStatements(
+			String[] dropStatements,
 			JdbcServices jdbcServices,
-			JdbcConnectionAccess connectionAccess,
-			List<String> tableCleanUpDdl) {
-		if ( tableCleanUpDdl == null ) {
+			JdbcConnectionAccess connectionAccess) {
+		if ( dropStatements == null ) {
 			return;
 		}
 
 		try {
 			Connection connection = connectionAccess.obtainConnection();
 
 			try {
 				// TODO: session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().createStatement();
 				Statement statement = connection.createStatement();
 
-				for ( String cleanupDdl : tableCleanUpDdl ) {
+				for ( String dropStatement : dropStatements ) {
 					try {
-						jdbcServices.getSqlStatementLogger().logStatement( cleanupDdl );
-						statement.execute( cleanupDdl );
+						jdbcServices.getSqlStatementLogger().logStatement( dropStatement );
+						statement.execute( dropStatement );
 					}
 					catch (SQLException e) {
 						log.debugf( "Error attempting to cleanup id-table : [%s]", e.getMessage() );
 					}
 				}
 
 				// TODO
 //				session.getTransactionCoordinator().getJdbcCoordinator().release( statement );
 				statement.close();
 			}
 			catch (SQLException e) {
 				log.error( "Unable to use JDBC Connection to create Statement", e );
 			}
 			finally {
 				try {
 					connectionAccess.releaseConnection( connection );
 				}
 				catch (SQLException ignore) {
 				}
 			}
 		}
 		catch (SQLException e) {
 			log.error( "Unable obtain JDBC Connection", e );
 		}
 	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableInfo.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableInfo.java
new file mode 100644
index 0000000000..05b0ec7ada
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableInfo.java
@@ -0,0 +1,31 @@
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
+package org.hibernate.hql.spi.id;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface IdTableInfo {
+	String getQualifiedIdTableName();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableSupport.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableSupport.java
new file mode 100644
index 0000000000..4ebd8bc3b8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableSupport.java
@@ -0,0 +1,36 @@
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
+package org.hibernate.hql.spi.id;
+
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface IdTableSupport {
+	public String generateIdTableName(String baseName);
+	public String getCreateIdTableCommand();
+	public String getCreateIdTableStatementOptions();
+	public String getDropIdTableCommand();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableSupportStandardImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableSupportStandardImpl.java
new file mode 100644
index 0000000000..de1ad2f197
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/IdTableSupportStandardImpl.java
@@ -0,0 +1,54 @@
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
+package org.hibernate.hql.spi.id;
+
+/**
+* @author Steve Ebersole
+*/
+public class IdTableSupportStandardImpl implements IdTableSupport {
+	/**
+	 * Singleton access
+	 */
+	public static final IdTableSupportStandardImpl INSTANCE = new IdTableSupportStandardImpl();
+
+	@Override
+	public String generateIdTableName(String baseName) {
+		return "HT_" + baseName;
+	}
+
+	@Override
+	public String getCreateIdTableCommand() {
+		return "create table";
+	}
+
+	@Override
+	public String getCreateIdTableStatementOptions() {
+		return null;
+	}
+
+	@Override
+	public String getDropIdTableCommand() {
+		return "drop table";
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/MultiTableBulkIdStrategy.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/id/MultiTableBulkIdStrategy.java
index 78e9bc9974..c074184065 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/MultiTableBulkIdStrategy.java
@@ -1,100 +1,105 @@
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
-package org.hibernate.hql.spi;
+package org.hibernate.hql.spi.id;
 
 import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.persister.entity.Queryable;
 
 /**
  * Generalized strategy contract for handling multi-table bulk HQL operations.
  *
  * @author Steve Ebersole
  */
 public interface MultiTableBulkIdStrategy {
 	/**
 	 * Prepare the strategy.  Called as the SessionFactory is being built.  Intended patterns here include:<ul>
 	 *     <li>Adding tables to the passed Mappings, to be picked by by "schema management tools"</li>
 	 *     <li>Manually creating the tables immediately through the passed JDBC Connection access</li>
 	 * </ul>
-	 *
-	 * @param jdbcServices The JdbcService object
+	 *  @param jdbcServices The JdbcService object
 	 * @param connectionAccess Access to the JDBC Connection
 	 * @param metadata Access to the O/RM mapping information
+	 * @param sessionFactoryOptions
 	 */
-	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata);
+	public void prepare(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata,
+			SessionFactoryOptions sessionFactoryOptions);
 
 	/**
 	 * Release the strategy.   Called as the SessionFactory is being shut down.
 	 *
 	 * @param jdbcServices The JdbcService object
 	 * @param connectionAccess Access to the JDBC Connection
 	 */
 	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess);
 
 	/**
 	 * Handler for dealing with multi-table HQL bulk update statements.
 	 */
 	public static interface UpdateHandler {
 		public Queryable getTargetedQueryable();
 		public String[] getSqlStatements();
 
 		public int execute(SessionImplementor session, QueryParameters queryParameters);
 	}
 
 	/**
 	 * Build a handler capable of handling the bulk update indicated by the given walker.
 	 *
 	 * @param factory The SessionFactory
 	 * @param walker The AST walker, representing the update query
 	 *
 	 * @return The handler
 	 */
 	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
 
 	/**
 	 * Handler for dealing with multi-table HQL bulk delete statements.
 	 */
 	public static interface DeleteHandler {
 		public Queryable getTargetedQueryable();
 		public String[] getSqlStatements();
 
 		public int execute(SessionImplementor session, QueryParameters queryParameters);
 	}
 
 	/**
 	 * Build a handler capable of handling the bulk delete indicated by the given walker.
 	 *
 	 * @param factory The SessionFactory
 	 * @param walker The AST walker, representing the delete query
 	 *
 	 * @return The handler
 	 */
 	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/TableBasedDeleteHandlerImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/id/TableBasedDeleteHandlerImpl.java
index 8e890fb492..a0c12ed817 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/TableBasedDeleteHandlerImpl.java
@@ -1,192 +1,187 @@
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
-package org.hibernate.hql.spi;
+package org.hibernate.hql.spi.id;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.tree.DeleteStatement;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.AbstractCollectionPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.Delete;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
 * @author Steve Ebersole
 */
 public class TableBasedDeleteHandlerImpl
 		extends AbstractTableBasedBulkIdHandler
 		implements MultiTableBulkIdStrategy.DeleteHandler {
 	private static final Logger log = Logger.getLogger( TableBasedDeleteHandlerImpl.class );
 
 	private final Queryable targetedPersister;
 
 	private final String idInsertSelect;
 	private final List<ParameterSpecification> idSelectParameterSpecifications;
 	private final List<String> deletes;
 
-	public TableBasedDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		this( factory, walker, null, null );
-	}
-
 	public TableBasedDeleteHandlerImpl(
 			SessionFactoryImplementor factory,
 			HqlSqlWalker walker,
-			String catalog,
-			String schema) {
-		super( factory, walker, catalog, schema );
+			IdTableInfo idTableInfo) {
+		super( factory, walker );
 
 		DeleteStatement deleteStatement = ( DeleteStatement ) walker.getAST();
 		FromElement fromElement = deleteStatement.getFromClause().getFromElement();
 
 		this.targetedPersister = fromElement.getQueryable();
 		final String bulkTargetAlias = fromElement.getTableAlias();
 
 		final ProcessedWhereClause processedWhereClause = processWhereClause( deleteStatement.getWhereClause() );
 		this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
-		this.idInsertSelect = generateIdInsertSelect( targetedPersister, bulkTargetAlias, processedWhereClause );
+		this.idInsertSelect = generateIdInsertSelect( bulkTargetAlias, idTableInfo, processedWhereClause );
 		log.tracev( "Generated ID-INSERT-SELECT SQL (multi-table delete) : {0}", idInsertSelect );
 		
-		final String idSubselect = generateIdSubselect( targetedPersister );
+		final String idSubselect = generateIdSubselect( targetedPersister, idTableInfo );
 		deletes = new ArrayList<String>();
 		
 		// If many-to-many, delete the FK row in the collection table.
 		// This partially overlaps with DeleteExecutor, but it instead uses the temp table in the idSubselect.
 		for ( Type type : targetedPersister.getPropertyTypes() ) {
 			if ( type.isCollectionType() ) {
 				CollectionType cType = (CollectionType) type;
 				AbstractCollectionPersister cPersister = (AbstractCollectionPersister)factory.getCollectionPersister( cType.getRole() );
 				if ( cPersister.isManyToMany() ) {
 					deletes.add( generateDelete( cPersister.getTableName(),
 							cPersister.getKeyColumnNames(), idSubselect, "bulk delete - m2m join table cleanup"));
 				}
 			}
 		}
 
 		String[] tableNames = targetedPersister.getConstraintOrderedTableNameClosure();
 		String[][] columnNames = targetedPersister.getContraintOrderedTableKeyColumnClosure();
 		for ( int i = 0; i < tableNames.length; i++ ) {
 			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
 			//      the difficulty is the ordering of the tables here vs the cascade attributes on the persisters ->
 			//          the table info gotten here should really be self-contained (i.e., a class representation
 			//          defining all the needed attributes), then we could then get an array of those
 			deletes.add( generateDelete( tableNames[i], columnNames[i], idSubselect, "bulk delete"));
 		}
 	}
 	
 	private String generateDelete(String tableName, String[] columnNames, String idSubselect, String comment) {
 		final Delete delete = new Delete()
 				.setTableName( tableName )
 				.setWhere( "(" + StringHelper.join( ", ", columnNames ) + ") IN (" + idSubselect + ")" );
 		if ( factory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( comment );
 		}
 		return delete.toStatementString();
 	}
 
 	@Override
 	public Queryable getTargetedQueryable() {
 		return targetedPersister;
 	}
 
 	@Override
 	public String[] getSqlStatements() {
 		return deletes.toArray( new String[deletes.size()] );
 	}
 
 	@Override
 	public int execute(SessionImplementor session, QueryParameters queryParameters) {
 		prepareForUse( targetedPersister, session );
 		try {
 			PreparedStatement ps = null;
 			int resultCount = 0;
 			try {
 				try {
 					ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( idInsertSelect, false );
 					int pos = 1;
 					pos += handlePrependedParametersOnIdSelection( ps, session, pos );
 					for ( ParameterSpecification parameterSpecification : idSelectParameterSpecifications ) {
 						pos += parameterSpecification.bind( ps, queryParameters, session, pos );
 					}
 					resultCount = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
 				}
 				finally {
 					if ( ps != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 					}
 				}
 			}
 			catch( SQLException e ) {
 				throw convert( e, "could not insert/select ids for bulk delete", idInsertSelect );
 			}
 
 			// Start performing the deletes
 			for ( String delete : deletes ) {
 				try {
 					try {
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( delete, false );
 						handleAddedParametersOnDelete( ps, session );
 						session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
 					}
 					finally {
 						if ( ps != null ) {
 							session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 						}
 					}
 				}
 				catch (SQLException e) {
 					throw convert( e, "error performing bulk delete", delete );
 				}
 			}
 
 			return resultCount;
 
 		}
 		finally {
 			releaseFromUse( targetedPersister, session );
 		}
 	}
 
 	protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
 		return 0;
 	}
 
 	protected void handleAddedParametersOnDelete(PreparedStatement ps, SessionImplementor session) throws SQLException {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedUpdateHandlerImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/TableBasedUpdateHandlerImpl.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedUpdateHandlerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/id/TableBasedUpdateHandlerImpl.java
index cc34035020..6f858d27c7 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedUpdateHandlerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/TableBasedUpdateHandlerImpl.java
@@ -1,197 +1,192 @@
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
-package org.hibernate.hql.spi;
+package org.hibernate.hql.spi.id;
 
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.ArrayList;
+import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.UpdateStatement;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.Update;
 
 import org.jboss.logging.Logger;
 
 /**
 * @author Steve Ebersole
 */
 public class TableBasedUpdateHandlerImpl
 		extends AbstractTableBasedBulkIdHandler
 		implements MultiTableBulkIdStrategy.UpdateHandler {
 
 	private static final Logger log = Logger.getLogger( TableBasedUpdateHandlerImpl.class );
 
 	private final Queryable targetedPersister;
 
 	private final String idInsertSelect;
 	private final List<ParameterSpecification> idSelectParameterSpecifications;
 
 	private final String[] updates;
 	private final ParameterSpecification[][] assignmentParameterSpecifications;
 
 	@SuppressWarnings("unchecked")
-	public TableBasedUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		this( factory, walker, null, null );
-	}
-
 	public TableBasedUpdateHandlerImpl(
 			SessionFactoryImplementor factory,
 			HqlSqlWalker walker,
-			String catalog,
-			String schema) {
-		super( factory, walker, catalog, schema );
+			IdTableInfo idTableInfo) {
+		super( factory, walker );
 
 		UpdateStatement updateStatement = ( UpdateStatement ) walker.getAST();
 		FromElement fromElement = updateStatement.getFromClause().getFromElement();
 
 		this.targetedPersister = fromElement.getQueryable();
+
 		final String bulkTargetAlias = fromElement.getTableAlias();
 
 		final ProcessedWhereClause processedWhereClause = processWhereClause( updateStatement.getWhereClause() );
 		this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
-		this.idInsertSelect = generateIdInsertSelect( targetedPersister, bulkTargetAlias, processedWhereClause );
+		this.idInsertSelect = generateIdInsertSelect( bulkTargetAlias, idTableInfo, processedWhereClause );
 		log.tracev( "Generated ID-INSERT-SELECT SQL (multi-table update) : {0}", idInsertSelect );
 
 		String[] tableNames = targetedPersister.getConstraintOrderedTableNameClosure();
 		String[][] columnNames = targetedPersister.getContraintOrderedTableKeyColumnClosure();
-		String idSubselect = generateIdSubselect( targetedPersister );
+		String idSubselect = generateIdSubselect( targetedPersister, idTableInfo );
 
 		updates = new String[tableNames.length];
 		assignmentParameterSpecifications = new ParameterSpecification[tableNames.length][];
 		for ( int tableIndex = 0; tableIndex < tableNames.length; tableIndex++ ) {
 			boolean affected = false;
 			final List<ParameterSpecification> parameterList = new ArrayList<ParameterSpecification>();
 			final Update update = new Update( factory().getDialect() )
 					.setTableName( tableNames[tableIndex] )
 					.setWhere( "(" + StringHelper.join( ", ", columnNames[tableIndex] ) + ") IN (" + idSubselect + ")" );
 			if ( factory().getSettings().isCommentsEnabled() ) {
 				update.setComment( "bulk update" );
 			}
 			final List<AssignmentSpecification> assignmentSpecifications = walker.getAssignmentSpecifications();
 			for ( AssignmentSpecification assignmentSpecification : assignmentSpecifications ) {
 				if ( assignmentSpecification.affectsTable( tableNames[tableIndex] ) ) {
 					affected = true;
 					update.appendAssignmentFragment( assignmentSpecification.getSqlAssignmentFragment() );
 					if ( assignmentSpecification.getParameters() != null ) {
-						for ( int paramIndex = 0; paramIndex < assignmentSpecification.getParameters().length; paramIndex++ ) {
-							parameterList.add( assignmentSpecification.getParameters()[paramIndex] );
-						}
+						Collections.addAll( parameterList, assignmentSpecification.getParameters() );
 					}
 				}
 			}
 			if ( affected ) {
 				updates[tableIndex] = update.toStatementString();
 				assignmentParameterSpecifications[tableIndex] = parameterList.toArray( new ParameterSpecification[parameterList.size()] );
 			}
 		}
 	}
 
 	@Override
 	public Queryable getTargetedQueryable() {
 		return targetedPersister;
 	}
 
 	@Override
 	public String[] getSqlStatements() {
 		return updates;
 	}
 
 	@Override
 	public int execute(SessionImplementor session, QueryParameters queryParameters) {
 		prepareForUse( targetedPersister, session );
 		try {
 			// First, save off the pertinent ids, as the return value
 			PreparedStatement ps = null;
 			int resultCount = 0;
 			try {
 				try {
 					ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( idInsertSelect, false );
 					int sum = 1;
 					sum += handlePrependedParametersOnIdSelection( ps, session, sum );
 					for ( ParameterSpecification parameterSpecification : idSelectParameterSpecifications ) {
 						sum += parameterSpecification.bind( ps, queryParameters, session, sum );
 					}
 					resultCount = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
 				}
 				finally {
 					if ( ps != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 					}
 				}
 			}
 			catch( SQLException e ) {
 				throw convert( e, "could not insert/select ids for bulk update", idInsertSelect );
 			}
 
 			// Start performing the updates
 			for ( int i = 0; i < updates.length; i++ ) {
 				if ( updates[i] == null ) {
 					continue;
 				}
 				try {
 					try {
 						ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( updates[i], false );
 						if ( assignmentParameterSpecifications[i] != null ) {
 							int position = 1; // jdbc params are 1-based
 							for ( int x = 0; x < assignmentParameterSpecifications[i].length; x++ ) {
 								position += assignmentParameterSpecifications[i][x].bind( ps, queryParameters, session, position );
 							}
 							handleAddedParametersOnUpdate( ps, session, position );
 						}
 						session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
 					}
 					finally {
 						if ( ps != null ) {
 							session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 						}
 					}
 				}
 				catch( SQLException e ) {
 					throw convert( e, "error performing bulk update", updates[i] );
 				}
 			}
 
 			return resultCount;
 		}
 		finally {
 			releaseFromUse( targetedPersister, session );
 		}
 	}
 
 	protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
 		return 0;
 	}
 
 	protected void handleAddedParametersOnUpdate(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/GlobalTemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/GlobalTemporaryTableBulkIdStrategy.java
new file mode 100644
index 0000000000..c11f8e2ef1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/GlobalTemporaryTableBulkIdStrategy.java
@@ -0,0 +1,225 @@
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
+package org.hibernate.hql.spi.id.global;
+
+import java.sql.PreparedStatement;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.spi.MetadataBuildingOptions;
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
+import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.engine.config.spi.StandardConverters;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.internal.ast.tree.DeleteStatement;
+import org.hibernate.hql.internal.ast.tree.FromElement;
+import org.hibernate.hql.internal.ast.tree.UpdateStatement;
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
+import org.hibernate.hql.spi.id.IdTableHelper;
+import org.hibernate.hql.spi.id.IdTableInfo;
+import org.hibernate.hql.spi.id.IdTableSupport;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.TableBasedDeleteHandlerImpl;
+import org.hibernate.hql.spi.id.TableBasedUpdateHandlerImpl;
+import org.hibernate.hql.spi.id.local.AfterUseAction;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.Queryable;
+
+/**
+ * Strategy based on ANSI SQL's definition of a "global temporary table".
+ *
+ * @author Steve Ebersole
+ */
+public class GlobalTemporaryTableBulkIdStrategy
+		extends AbstractMultiTableBulkIdStrategyImpl<IdTableInfoImpl,PreparationContextImpl>
+		implements MultiTableBulkIdStrategy {
+	public static final String DROP_ID_TABLES = "hibernate.hql.bulk_id_strategy.global_temporary.drop_tables";
+
+	public static final String SHORT_NAME = "global_temporary";
+
+	private final AfterUseAction afterUseAction;
+
+	private boolean dropIdTables;
+	private String[] dropTableStatements;
+
+	public GlobalTemporaryTableBulkIdStrategy() {
+		this( AfterUseAction.CLEAN );
+	}
+
+	public GlobalTemporaryTableBulkIdStrategy(AfterUseAction afterUseAction) {
+		this(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create global temporary table";
+					}
+
+					@Override
+					public String getDropIdTableCommand() {
+						return super.getDropIdTableCommand();
+					}
+				},
+				afterUseAction
+		);
+	}
+
+	public GlobalTemporaryTableBulkIdStrategy(IdTableSupport idTableSupport, AfterUseAction afterUseAction) {
+		super( idTableSupport );
+		this.afterUseAction = afterUseAction;
+		if ( afterUseAction == AfterUseAction.DROP ) {
+			throw new IllegalArgumentException( "DROP not supported as a after-use action for global temp table strategy" );
+		}
+	}
+
+	@Override
+	protected PreparationContextImpl buildPreparationContext() {
+		return new PreparationContextImpl();
+	}
+
+	@Override
+	protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
+		final StandardServiceRegistry serviceRegistry = buildingOptions.getServiceRegistry();
+		final ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
+		this.dropIdTables = configService.getSetting(
+				DROP_ID_TABLES,
+				StandardConverters.BOOLEAN,
+				false
+		);
+	}
+
+	@Override
+	protected IdTableInfoImpl buildIdTableInfo(
+			PersistentClass entityBinding,
+			Table idTable,
+			JdbcServices jdbcServices,
+			MetadataImplementor metadata,
+			PreparationContextImpl context) {
+		context.creationStatements.add( buildIdTableCreateStatement( idTable, jdbcServices, metadata ) );
+		if ( dropIdTables ) {
+			context.dropStatements.add( buildIdTableDropStatement( idTable, jdbcServices ) );
+		}
+
+		final String renderedName = jdbcServices.getJdbcEnvironment().getQualifiedObjectNameFormatter().format(
+				idTable.getQualifiedTableName(),
+				jdbcServices.getJdbcEnvironment().getDialect()
+		);
+
+		return new IdTableInfoImpl( renderedName );
+	}
+
+	@Override
+	protected void finishPreparation(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata,
+			PreparationContextImpl context) {
+		IdTableHelper.INSTANCE.executeIdTableCreationStatements(
+				context.creationStatements,
+				jdbcServices,
+				connectionAccess
+		);
+
+		this.dropTableStatements = dropIdTables
+				? context.dropStatements.toArray( new String[ context.dropStatements.size() ] )
+				: null;
+	}
+
+	@Override
+	public void release(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess) {
+		if ( ! dropIdTables ) {
+			return;
+		}
+
+		IdTableHelper.INSTANCE.executeIdTableDropStatements( dropTableStatements, jdbcServices, connectionAccess );
+	}
+
+	@Override
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		final UpdateStatement updateStatement = (UpdateStatement) walker.getAST();
+
+		final FromElement fromElement = updateStatement.getFromClause().getFromElement();
+		final Queryable targetedPersister = fromElement.getQueryable();
+
+		return new TableBasedUpdateHandlerImpl( factory, walker, getIdTableInfo( targetedPersister ) ) {
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				if ( afterUseAction == AfterUseAction.NONE ) {
+					return;
+				}
+
+				// clean up our id-table rows
+				cleanUpRows( getIdTableInfo( persister ).getQualifiedIdTableName(), session );
+			}
+		};
+	}
+
+	private void cleanUpRows(String tableName, SessionImplementor session) {
+		final String sql = "delete from " + tableName;
+		PreparedStatement ps = null;
+		try {
+			ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
+			session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
+		}
+		finally {
+			if ( ps != null ) {
+				try {
+					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
+				}
+				catch( Throwable ignore ) {
+					// ignore
+				}
+			}
+		}
+	}
+
+	@Override
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		final DeleteStatement updateStatement = (DeleteStatement) walker.getAST();
+
+		final FromElement fromElement = updateStatement.getFromClause().getFromElement();
+		final Queryable targetedPersister = fromElement.getQueryable();
+
+		return new TableBasedDeleteHandlerImpl( factory, walker, getIdTableInfo( targetedPersister ) ) {
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				if ( afterUseAction == AfterUseAction.NONE ) {
+					return;
+				}
+
+				// clean up our id-table rows
+				cleanUpRows( getIdTableInfo( persister ).getQualifiedIdTableName(), session );
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/IdTableInfoImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/IdTableInfoImpl.java
new file mode 100644
index 0000000000..1bc708a31e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/IdTableInfoImpl.java
@@ -0,0 +1,44 @@
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
+package org.hibernate.hql.spi.id.global;
+
+import org.hibernate.hql.spi.id.IdTableInfo;
+
+/**
+ * IdTableInfo implementation specific to PersistentTableBulkIdStrategy
+ *
+ * @author Steve Ebersole
+ */
+class IdTableInfoImpl implements IdTableInfo {
+	private final String idTableName;
+
+	public IdTableInfoImpl(String idTableName) {
+		this.idTableName = idTableName;
+	}
+
+	@Override
+	public String getQualifiedIdTableName() {
+		return idTableName;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/PreparationContextImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/PreparationContextImpl.java
new file mode 100644
index 0000000000..41155ab77a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/global/PreparationContextImpl.java
@@ -0,0 +1,40 @@
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
+package org.hibernate.hql.spi.id.global;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
+
+/**
+ * PreparationContext implementation for GlobalTemporaryTableBulkIdStrategy.  Used to collect
+ * create and drop statements
+ *
+ * @author Steve Ebersole
+ */
+class PreparationContextImpl implements AbstractMultiTableBulkIdStrategyImpl.PreparationContext {
+	List<String> creationStatements = new ArrayList<String>();
+	List<String> dropStatements = new ArrayList<String>();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/AfterUseAction.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/AfterUseAction.java
new file mode 100644
index 0000000000..366b00d377
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/AfterUseAction.java
@@ -0,0 +1,33 @@
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
+package org.hibernate.hql.spi.id.local;
+
+/**
+* @author Steve Ebersole
+*/
+public enum AfterUseAction {
+	CLEAN,
+	DROP,
+	NONE
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/LocalTemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/Helper.java
similarity index 51%
rename from hibernate-core/src/main/java/org/hibernate/hql/spi/LocalTemporaryTableBulkIdStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/Helper.java
index 1c8b39e930..0c2e505745 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/LocalTemporaryTableBulkIdStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/Helper.java
@@ -1,263 +1,222 @@
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
-package org.hibernate.hql.spi;
+package org.hibernate.hql.spi.id.local;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 
-import org.hibernate.boot.spi.MetadataImplementor;
-import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.boot.TempTableDdlTransactionHandling;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.jdbc.AbstractWork;
-import org.hibernate.persister.entity.Queryable;
-
-import org.jboss.logging.Logger;
 
 /**
- * Strategy based on ANSI SQL's definition of a "local temporary table" (local to each db session).
- *
  * @author Steve Ebersole
  */
-public class LocalTemporaryTableBulkIdStrategy implements MultiTableBulkIdStrategy {
-	public static final LocalTemporaryTableBulkIdStrategy INSTANCE = new LocalTemporaryTableBulkIdStrategy();
-
-	public static final String SHORT_NAME = "temporary";
-
-	private static final CoreMessageLogger log = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			LocalTemporaryTableBulkIdStrategy.class.getName()
-	);
-
-	@Override
-	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, MetadataImplementor metadata) {
-		// nothing to do
-	}
-
-	@Override
-	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
-		// nothing to do
-	}
-
-	@Override
-	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedUpdateHandlerImpl( factory, walker ) {
-			@Override
-			protected void prepareForUse(Queryable persister, SessionImplementor session) {
-				createTempTable( persister, session );
-			}
+public class Helper {
+	/**
+	 * Singleton access
+	 */
+	public static final Helper INSTANCE = new Helper();
 
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				releaseTempTable( persister, session );
-			}
-		};
-	}
-
-	@Override
-	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
-		return new TableBasedDeleteHandlerImpl( factory, walker ) {
-			@Override
-			protected void prepareForUse(Queryable persister, SessionImplementor session) {
-				createTempTable( persister, session );
-			}
+	private static final CoreMessageLogger log = CoreLogging.messageLogger( Helper.class );
 
-			@Override
-			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
-				releaseTempTable( persister, session );
-			}
-		};
+	private Helper() {
 	}
 
 
-	protected void createTempTable(Queryable persister, SessionImplementor session) {
+	public void createTempTable(
+			IdTableInfoImpl idTableInfo,
+			TempTableDdlTransactionHandling ddlTransactionHandling,
+			SessionImplementor session) {
 		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
 		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
-		TemporaryTableCreationWork work = new TemporaryTableCreationWork( persister );
-		if ( shouldIsolateTemporaryTableDDL( session ) ) {
-			session.getTransactionCoordinator()
-					.getTransaction()
-					.createIsolationDelegate()
-					.delegateWork( work, shouldTransactIsolatedTemporaryTableDDL( session ) );
-		}
-		else {
+		TemporaryTableCreationWork work = new TemporaryTableCreationWork( idTableInfo, session.getFactory() );
+
+		if ( ddlTransactionHandling == TempTableDdlTransactionHandling.NONE ) {
 			final Connection connection = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getLogicalConnection()
 					.getConnection();
+
 			work.execute( connection );
+
 			session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.afterStatementExecution();
 		}
-	}
-
-	protected void releaseTempTable(Queryable persister, SessionImplementor session) {
-		if ( session.getFactory().getDialect().dropTemporaryTableAfterUse() ) {
-			TemporaryTableDropWork work = new TemporaryTableDropWork( persister, session );
-			if ( shouldIsolateTemporaryTableDDL( session ) ) {
-				session.getTransactionCoordinator()
-						.getTransaction()
-						.createIsolationDelegate()
-						.delegateWork( work, shouldTransactIsolatedTemporaryTableDDL( session ) );
-			}
-			else {
-				final Connection connection = session.getTransactionCoordinator()
-						.getJdbcCoordinator()
-						.getLogicalConnection()
-						.getConnection();
-				work.execute( connection );
-				session.getTransactionCoordinator()
-						.getJdbcCoordinator()
-						.afterStatementExecution();
-			}
-		}
 		else {
-			// at the very least cleanup the data :)
-			PreparedStatement ps = null;
-			try {
-				final String sql = "delete from " + persister.getTemporaryIdTableName();
-				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
-				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
-			}
-			catch( Throwable t ) {
-				log.unableToCleanupTemporaryIdTable(t);
-			}
-			finally {
-				if ( ps != null ) {
-					try {
-						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
-					}
-					catch( Throwable ignore ) {
-						// ignore
-					}
-				}
-			}
-		}
-	}
-
-	protected boolean shouldIsolateTemporaryTableDDL(SessionImplementor session) {
-		Boolean dialectVote = session.getFactory().getDialect().performTemporaryTableDDLInIsolation();
-		if ( dialectVote != null ) {
-			return dialectVote;
+			session.getTransactionCoordinator()
+					.getTransaction()
+					.createIsolationDelegate()
+					.delegateWork( work, ddlTransactionHandling == TempTableDdlTransactionHandling.ISOLATE_AND_TRANSACT );
 		}
-		return session.getFactory().getSettings().isDataDefinitionImplicitCommit();
-	}
-
-	protected boolean shouldTransactIsolatedTemporaryTableDDL(SessionImplementor session) {
-		// is there ever a time when it makes sense to do this?
-//		return session.getFactory().getSettings().isDataDefinitionInTransactionSupported();
-		return false;
 	}
 
 	private static class TemporaryTableCreationWork extends AbstractWork {
-		private final Queryable persister;
+		private final IdTableInfoImpl idTableInfo;
+		private final SessionFactoryImplementor factory;
 
-		private TemporaryTableCreationWork(Queryable persister) {
-			this.persister = persister;
+		private TemporaryTableCreationWork(IdTableInfoImpl idTableInfo, SessionFactoryImplementor factory) {
+			this.idTableInfo = idTableInfo;
+			this.factory = factory;
 		}
 
 		@Override
 		public void execute(Connection connection) {
 			try {
 				Statement statement = connection.createStatement();
 				try {
-					statement.executeUpdate( persister.getTemporaryIdTableDDL() );
-					persister.getFactory()
-							.getServiceRegistry()
+					statement.executeUpdate( idTableInfo.getIdTableCreationStatement() );
+					factory.getServiceRegistry()
 							.getService( JdbcServices.class )
 							.getSqlExceptionHelper()
-							.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
+							.handleAndClearWarnings( statement, WARNING_HANDLER );
 				}
 				finally {
 					try {
 						statement.close();
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 			catch( Exception e ) {
 				log.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
 			}
 		}
 	}
 
-	private static SqlExceptionHelper.WarningHandler CREATION_WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
+	private static SqlExceptionHelper.WarningHandler WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
 		public boolean doProcess() {
 			return log.isDebugEnabled();
 		}
 
 		public void prepare(SQLWarning warning) {
 			log.warningsCreatingTempTable( warning );
 		}
 
 		@Override
 		protected void logWarning(String description, String message) {
 			log.debug( description );
 			log.debug( message );
 		}
 	};
 
+	protected void releaseTempTable(
+			IdTableInfoImpl idTableInfo,
+			AfterUseAction afterUseAction,
+			TempTableDdlTransactionHandling ddlTransactionHandling,
+			SessionImplementor session) {
+		if ( afterUseAction == AfterUseAction.NONE ) {
+			return;
+		}
+
+		if ( afterUseAction == AfterUseAction.DROP ) {
+			TemporaryTableDropWork work = new TemporaryTableDropWork( idTableInfo, session.getFactory() );
+			if ( ddlTransactionHandling == TempTableDdlTransactionHandling.NONE ) {
+				final Connection connection = session.getTransactionCoordinator()
+						.getJdbcCoordinator()
+						.getLogicalConnection()
+						.getConnection();
+
+				work.execute( connection );
+
+				session.getTransactionCoordinator()
+						.getJdbcCoordinator()
+						.afterStatementExecution();
+			}
+			else {
+				session.getTransactionCoordinator()
+						.getTransaction()
+						.createIsolationDelegate()
+						.delegateWork( work, ddlTransactionHandling == TempTableDdlTransactionHandling.ISOLATE_AND_TRANSACT );
+			}
+		}
+
+		if ( afterUseAction == AfterUseAction.CLEAN ) {
+			PreparedStatement ps = null;
+			try {
+				final String sql = "delete from " + idTableInfo.getQualifiedIdTableName();
+				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
+				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
+			}
+			catch( Throwable t ) {
+				log.unableToCleanupTemporaryIdTable(t);
+			}
+			finally {
+				if ( ps != null ) {
+					try {
+						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
+					}
+					catch( Throwable ignore ) {
+						// ignore
+					}
+				}
+			}
+		}
+	}
+
 	private static class TemporaryTableDropWork extends AbstractWork {
-		private final Queryable persister;
-		private final SessionImplementor session;
+		private final IdTableInfoImpl idTableInfo;
+		private final SessionFactoryImplementor factory;
 
-		private TemporaryTableDropWork(Queryable persister, SessionImplementor session) {
-			this.persister = persister;
-			this.session = session;
+		private TemporaryTableDropWork(IdTableInfoImpl idTableInfo, SessionFactoryImplementor factory) {
+			this.idTableInfo = idTableInfo;
+			this.factory = factory;
 		}
 
 		@Override
 		public void execute(Connection connection) {
-			final String command = session.getFactory().getDialect().getDropTemporaryTableString()
-					+ ' ' + persister.getTemporaryIdTableName();
 			try {
 				Statement statement = connection.createStatement();
 				try {
-					statement.executeUpdate( command );
+					statement.executeUpdate( idTableInfo.getIdTableDropStatement() );
+					factory.getServiceRegistry()
+							.getService( JdbcServices.class )
+							.getSqlExceptionHelper()
+							.handleAndClearWarnings( statement, WARNING_HANDLER );
 				}
 				finally {
 					try {
 						statement.close();
 					}
 					catch( Throwable ignore ) {
 						// ignore
 					}
 				}
 			}
 			catch( Exception e ) {
 				log.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
 			}
 		}
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/IdTableInfoImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/IdTableInfoImpl.java
new file mode 100644
index 0000000000..7d88a77679
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/IdTableInfoImpl.java
@@ -0,0 +1,58 @@
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
+package org.hibernate.hql.spi.id.local;
+
+import org.hibernate.hql.spi.id.IdTableInfo;
+
+/**
+ * @author Steve Ebersole
+ */
+public class IdTableInfoImpl implements IdTableInfo {
+	private final String idTableName;
+
+	private final String creationStatement;
+	private final String dropStatement;
+
+	public IdTableInfoImpl(
+			String idTableName,
+			String creationStatement,
+			String dropStatement) {
+		this.idTableName = idTableName;
+		this.creationStatement = creationStatement;
+		this.dropStatement = dropStatement;
+	}
+
+	@Override
+	public String getQualifiedIdTableName() {
+		return idTableName;
+	}
+
+	public String getIdTableCreationStatement() {
+		return creationStatement;
+	}
+
+	public String getIdTableDropStatement() {
+		return dropStatement;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/LocalTemporaryTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/LocalTemporaryTableBulkIdStrategy.java
new file mode 100644
index 0000000000..62a63f8c78
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/local/LocalTemporaryTableBulkIdStrategy.java
@@ -0,0 +1,176 @@
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
+package org.hibernate.hql.spi.id.local;
+
+import org.hibernate.boot.TempTableDdlTransactionHandling;
+import org.hibernate.boot.spi.MetadataBuildingOptions;
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.internal.ast.tree.DeleteStatement;
+import org.hibernate.hql.internal.ast.tree.FromElement;
+import org.hibernate.hql.internal.ast.tree.UpdateStatement;
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl.PreparationContext;
+import org.hibernate.hql.spi.id.IdTableSupport;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.id.TableBasedDeleteHandlerImpl;
+import org.hibernate.hql.spi.id.TableBasedUpdateHandlerImpl;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.Queryable;
+
+/**
+ * Strategy based on ANSI SQL's definition of a "local temporary table" (local to each db session).
+ *
+ * @author Steve Ebersole
+ */
+public class LocalTemporaryTableBulkIdStrategy
+		extends AbstractMultiTableBulkIdStrategyImpl<IdTableInfoImpl, PreparationContext>
+		implements MultiTableBulkIdStrategy {
+
+	public static final String SHORT_NAME = "local_temporary";
+
+	private final AfterUseAction afterUseAction;
+	private TempTableDdlTransactionHandling ddlTransactionHandling;
+
+	public LocalTemporaryTableBulkIdStrategy() {
+		this(
+				new IdTableSupportStandardImpl() {
+					@Override
+					public String getCreateIdTableCommand() {
+						return "create local temporary table";
+					}
+				},
+				AfterUseAction.DROP,
+				null
+		);
+	}
+
+	public LocalTemporaryTableBulkIdStrategy(
+			IdTableSupport idTableSupport,
+			AfterUseAction afterUseAction,
+			TempTableDdlTransactionHandling ddlTransactionHandling) {
+		super( idTableSupport );
+		this.afterUseAction = afterUseAction;
+		this.ddlTransactionHandling = ddlTransactionHandling;
+	}
+
+	@Override
+	protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
+		if ( ddlTransactionHandling == null ) {
+			ddlTransactionHandling = sessionFactoryOptions.getTempTableDdlTransactionHandling();
+		}
+	}
+
+	@Override
+	protected IdTableInfoImpl buildIdTableInfo(
+			PersistentClass entityBinding,
+			Table idTable,
+			JdbcServices jdbcServices,
+			MetadataImplementor metadata,
+			PreparationContext context) {
+		return new IdTableInfoImpl(
+				jdbcServices.getJdbcEnvironment().getQualifiedObjectNameFormatter().format(
+						idTable.getQualifiedTableName(),
+						jdbcServices.getJdbcEnvironment().getDialect()
+				),
+				buildIdTableCreateStatement( idTable, jdbcServices, metadata ),
+				buildIdTableDropStatement( idTable, jdbcServices )
+		);
+	}
+
+	@Override
+	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
+		// nothing to do
+	}
+
+	@Override
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		final UpdateStatement updateStatement = (UpdateStatement) walker.getAST();
+
+		final FromElement fromElement = updateStatement.getFromClause().getFromElement();
+		final Queryable targetedPersister = fromElement.getQueryable();
+
+		final IdTableInfoImpl tableInfo = getIdTableInfo( targetedPersister );
+
+		return new TableBasedUpdateHandlerImpl( factory, walker, tableInfo ) {
+			@Override
+			protected void prepareForUse(Queryable persister, SessionImplementor session) {
+				Helper.INSTANCE.createTempTable(
+						tableInfo,
+						ddlTransactionHandling,
+						session
+				);
+			}
+
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				Helper.INSTANCE.releaseTempTable(
+						tableInfo,
+						afterUseAction,
+						ddlTransactionHandling,
+						session
+				);
+			}
+		};
+	}
+
+	@Override
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		final DeleteStatement updateStatement = (DeleteStatement) walker.getAST();
+
+		final FromElement fromElement = updateStatement.getFromClause().getFromElement();
+		final Queryable targetedPersister = fromElement.getQueryable();
+
+		final IdTableInfoImpl tableInfo = getIdTableInfo( targetedPersister );
+
+		return new TableBasedDeleteHandlerImpl( factory, walker, tableInfo ) {
+			@Override
+			protected void prepareForUse(Queryable persister, SessionImplementor session) {
+				Helper.INSTANCE.createTempTable(
+						tableInfo,
+						ddlTransactionHandling,
+						session
+				);
+			}
+
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				Helper.INSTANCE.releaseTempTable(
+						tableInfo,
+						afterUseAction,
+						ddlTransactionHandling,
+						session
+				);
+			}
+		};
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/package-info.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/package-info.java
new file mode 100644
index 0000000000..675cc421ba
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/package-info.java
@@ -0,0 +1,28 @@
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
+
+/**
+ * Support for multi-table update and delete statements via id-tables.
+ */
+package org.hibernate.hql.spi.id;
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/DeleteHandlerImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/DeleteHandlerImpl.java
new file mode 100644
index 0000000000..9f8944d919
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/DeleteHandlerImpl.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.hql.spi.id.persistent;
+
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.spi.id.IdTableInfo;
+import org.hibernate.hql.spi.id.TableBasedDeleteHandlerImpl;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.sql.SelectValues;
+
+import static org.hibernate.hql.spi.id.persistent.Helper.SESSION_ID_COLUMN_NAME;
+
+/**
+* @author Steve Ebersole
+*/
+public class DeleteHandlerImpl extends TableBasedDeleteHandlerImpl {
+	private final IdTableInfo idTableInfo;
+
+	public DeleteHandlerImpl(
+			SessionFactoryImplementor factory,
+			HqlSqlWalker walker,
+			IdTableInfo idTableInfo) {
+		super( factory, walker, idTableInfo );
+		this.idTableInfo = idTableInfo;
+	}
+
+	@Override
+	protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
+		selectClause.addParameter( Types.CHAR, 36 );
+	}
+
+	@Override
+	protected String generateIdSubselect(Queryable persister, IdTableInfo idTableInfo) {
+		return super.generateIdSubselect( persister, idTableInfo ) + " where " + SESSION_ID_COLUMN_NAME + "=?";
+	}
+
+	@Override
+	protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
+		Helper.INSTANCE.bindSessionIdentifier( ps, session, pos );
+		return 1;
+	}
+
+	@Override
+	protected void handleAddedParametersOnDelete(PreparedStatement ps, SessionImplementor session) throws SQLException {
+		Helper.INSTANCE.bindSessionIdentifier( ps, session, 1 );
+	}
+
+	@Override
+	protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+		// clean up our id-table rows
+		Helper.INSTANCE.cleanUpRows( idTableInfo.getQualifiedIdTableName(), session );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/Helper.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/Helper.java
new file mode 100644
index 0000000000..7e830b11e6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/Helper.java
@@ -0,0 +1,86 @@
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
+package org.hibernate.hql.spi.id.persistent;
+
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+
+import org.hibernate.HibernateException;
+import org.hibernate.JDBCException;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.AbstractSessionImpl;
+import org.hibernate.type.UUIDCharType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class Helper {
+	/**
+	 * Singleton access
+	 */
+	public static final Helper INSTANCE = new Helper();
+
+	public static final String SESSION_ID_COLUMN_NAME = "hib_sess_id";
+
+	private Helper() {
+	}
+
+
+	public void bindSessionIdentifier(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
+		if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
+			throw new HibernateException( "Only available on SessionImpl instances" );
+		}
+		UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), position, session );
+	}
+
+	public void cleanUpRows(String tableName, SessionImplementor session) {
+		final String sql = "delete from " + tableName + " where " + SESSION_ID_COLUMN_NAME + "=?";
+		try {
+			PreparedStatement ps = null;
+			try {
+				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
+				bindSessionIdentifier( ps, session, 1 );
+				session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( ps );
+			}
+			finally {
+				if ( ps != null ) {
+					try {
+						session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
+					}
+					catch( Throwable ignore ) {
+						// ignore
+					}
+				}
+			}
+		}
+		catch (SQLException e) {
+			throw convert( session.getFactory(), e, "Unable to clean up id table [" + tableName + "]", sql );
+		}
+	}
+
+	public JDBCException convert(SessionFactoryImplementor factory, SQLException e, String message, String sql) {
+		throw factory.getSQLExceptionHelper().convert( e, message, sql );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/IdTableInfoImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/IdTableInfoImpl.java
new file mode 100644
index 0000000000..fba0be9497
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/IdTableInfoImpl.java
@@ -0,0 +1,44 @@
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
+package org.hibernate.hql.spi.id.persistent;
+
+import org.hibernate.hql.spi.id.IdTableInfo;
+
+/**
+ * IdTableInfo implementation specific to PersistentTableBulkIdStrategy
+ *
+ * @author Steve Ebersole
+ */
+class IdTableInfoImpl implements IdTableInfo {
+	private final String idTableName;
+
+	public IdTableInfoImpl(String idTableName) {
+		this.idTableName = idTableName;
+	}
+
+	@Override
+	public String getQualifiedIdTableName() {
+		return idTableName;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/PersistentTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/PersistentTableBulkIdStrategy.java
new file mode 100644
index 0000000000..d69e97593a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/PersistentTableBulkIdStrategy.java
@@ -0,0 +1,208 @@
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
+package org.hibernate.hql.spi.id.persistent;
+
+import org.hibernate.boot.model.naming.Identifier;
+import org.hibernate.boot.model.relational.QualifiedTableName;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.spi.MetadataBuildingOptions;
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.boot.spi.SessionFactoryOptions;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.engine.config.spi.StandardConverters;
+import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.internal.ast.tree.DeleteStatement;
+import org.hibernate.hql.internal.ast.tree.FromElement;
+import org.hibernate.hql.internal.ast.tree.UpdateStatement;
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
+import org.hibernate.hql.spi.id.IdTableHelper;
+import org.hibernate.hql.spi.id.IdTableSupport;
+import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
+import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.Queryable;
+
+/**
+ * This is a strategy that mimics temporary tables for databases which do not support
+ * temporary tables.  It follows a pattern similar to the ANSI SQL definition of global
+ * temporary table using a "session id" column to segment rows from the various sessions.
+ *
+ * @author Steve Ebersole
+ */
+public class PersistentTableBulkIdStrategy
+		extends AbstractMultiTableBulkIdStrategyImpl<IdTableInfoImpl,PreparationContextImpl>
+		implements MultiTableBulkIdStrategy {
+
+	public static final String SHORT_NAME = "persistent";
+
+	public static final String DROP_ID_TABLES = "hibernate.hql.bulk_id_strategy.persistent.drop_tables";
+	public static final String SCHEMA = "hibernate.hql.bulk_id_strategy.persistent.schema";
+	public static final String CATALOG = "hibernate.hql.bulk_id_strategy.persistent.catalog";
+
+	private Identifier catalog;
+	private Identifier schema;
+
+	private boolean dropIdTables;
+	private String[] dropTableStatements;
+
+	public PersistentTableBulkIdStrategy() {
+		this( IdTableSupportStandardImpl.INSTANCE );
+	}
+
+	public PersistentTableBulkIdStrategy(IdTableSupport idTableSupport) {
+		super( idTableSupport );
+	}
+
+	@Override
+	protected PreparationContextImpl buildPreparationContext() {
+		return new PreparationContextImpl();
+	}
+
+	@Override
+	protected void initialize(MetadataBuildingOptions buildingOptions, SessionFactoryOptions sessionFactoryOptions) {
+		final StandardServiceRegistry serviceRegistry = buildingOptions.getServiceRegistry();
+		final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService( JdbcEnvironment.class );
+		final ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
+
+		final String catalogName = configService.getSetting(
+				CATALOG,
+				StandardConverters.STRING,
+				configService.getSetting( AvailableSettings.DEFAULT_CATALOG, StandardConverters.STRING )
+		);
+		final String schemaName = configService.getSetting(
+				SCHEMA,
+				StandardConverters.STRING,
+				configService.getSetting( AvailableSettings.DEFAULT_SCHEMA, StandardConverters.STRING )
+		);
+
+		this.catalog = jdbcEnvironment.getIdentifierHelper().toIdentifier( catalogName );
+		this.schema = jdbcEnvironment.getIdentifierHelper().toIdentifier( schemaName );
+
+		this.dropIdTables = configService.getSetting(
+				DROP_ID_TABLES,
+				StandardConverters.BOOLEAN,
+				false
+		);
+	}
+
+	@Override
+	protected QualifiedTableName determineIdTableName(
+			JdbcEnvironment jdbcEnvironment,
+			PersistentClass entityBinding) {
+		return new QualifiedTableName(
+				catalog,
+				schema,
+				super.determineIdTableName( jdbcEnvironment, entityBinding ).getTableName()
+		);
+	}
+
+	@Override
+	protected void augmentIdTableDefinition(Table idTable) {
+		Column sessionIdColumn = new Column( Helper.SESSION_ID_COLUMN_NAME );
+		sessionIdColumn.setSqlType( "CHAR(36)" );
+		sessionIdColumn.setComment( "Used to hold the Hibernate Session identifier" );
+		idTable.addColumn( sessionIdColumn );
+	}
+
+	@Override
+	protected IdTableInfoImpl buildIdTableInfo(
+			PersistentClass entityBinding,
+			Table idTable,
+			JdbcServices jdbcServices,
+			MetadataImplementor metadata,
+			PreparationContextImpl context) {
+		final String renderedName = jdbcServices.getJdbcEnvironment().getQualifiedObjectNameFormatter().format(
+				idTable.getQualifiedTableName(),
+				jdbcServices.getJdbcEnvironment().getDialect()
+		);
+
+		context.creationStatements.add( buildIdTableCreateStatement( idTable, jdbcServices, metadata ) );
+		if ( dropIdTables ) {
+			context.dropStatements.add( buildIdTableDropStatement( idTable, jdbcServices ) );
+		}
+
+		return new IdTableInfoImpl( renderedName );
+	}
+
+	@Override
+	protected void finishPreparation(
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess connectionAccess,
+			MetadataImplementor metadata,
+			PreparationContextImpl context) {
+		IdTableHelper.INSTANCE.executeIdTableCreationStatements(
+				context.creationStatements,
+				jdbcServices,
+				connectionAccess
+		);
+
+		this.dropTableStatements = dropIdTables
+				? context.dropStatements.toArray( new String[ context.dropStatements.size() ] )
+				: null;
+	}
+
+	@Override
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		final UpdateStatement updateStatement = (UpdateStatement) walker.getAST();
+
+		final FromElement fromElement = updateStatement.getFromClause().getFromElement();
+		final Queryable targetedPersister = fromElement.getQueryable();
+
+		return new UpdateHandlerImpl(
+				factory,
+				walker,
+				getIdTableInfo( targetedPersister )
+		);
+	}
+
+	@Override
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		final DeleteStatement updateStatement = (DeleteStatement) walker.getAST();
+
+		final FromElement fromElement = updateStatement.getFromClause().getFromElement();
+		final Queryable targetedPersister = fromElement.getQueryable();
+
+		return new DeleteHandlerImpl(
+				factory,
+				walker,
+				getIdTableInfo( targetedPersister )
+		);
+	}
+
+	@Override
+	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess) {
+		if ( ! dropIdTables ) {
+			return;
+		}
+
+		IdTableHelper.INSTANCE.executeIdTableDropStatements( dropTableStatements, jdbcServices, connectionAccess );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/PreparationContextImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/PreparationContextImpl.java
new file mode 100644
index 0000000000..7bbd303a5e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/PreparationContextImpl.java
@@ -0,0 +1,39 @@
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
+package org.hibernate.hql.spi.id.persistent;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
+
+/**
+ * PreparationContext implementation for PersistentTableBulkIdStrategy
+ *
+ * @author Steve Ebersole
+ */
+class PreparationContextImpl implements AbstractMultiTableBulkIdStrategyImpl.PreparationContext {
+	List<String> creationStatements = new ArrayList<String>();
+	List<String> dropStatements = new ArrayList<String>();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/UpdateHandlerImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/UpdateHandlerImpl.java
new file mode 100644
index 0000000000..cd94853a31
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/UpdateHandlerImpl.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.hql.spi.id.persistent;
+
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.sql.Types;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.spi.id.IdTableInfo;
+import org.hibernate.hql.spi.id.TableBasedUpdateHandlerImpl;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.sql.SelectValues;
+
+import static org.hibernate.hql.spi.id.persistent.Helper.SESSION_ID_COLUMN_NAME;
+
+/**
+* @author Steve Ebersole
+*/
+public class UpdateHandlerImpl extends TableBasedUpdateHandlerImpl {
+	private final IdTableInfo idTableInfo;
+
+	public UpdateHandlerImpl(
+			SessionFactoryImplementor factory,
+			HqlSqlWalker walker,
+			IdTableInfo idTableInfo) {
+		super( factory, walker, idTableInfo );
+		this.idTableInfo = idTableInfo;
+	}
+
+	@Override
+	protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
+		selectClause.addParameter( Types.CHAR, 36 );
+	}
+
+	@Override
+	protected String generateIdSubselect(Queryable persister, IdTableInfo idTableInfo) {
+		return super.generateIdSubselect( persister, idTableInfo ) + " where " + SESSION_ID_COLUMN_NAME + "=?";
+	}
+
+	@Override
+	protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
+		Helper.INSTANCE.bindSessionIdentifier( ps, session, pos );
+		return 1;
+	}
+
+	@Override
+	protected void handleAddedParametersOnUpdate(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
+		Helper.INSTANCE.bindSessionIdentifier( ps, session, position );
+	}
+
+	@Override
+	protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+		// clean up our id-table rows
+		Helper.INSTANCE.cleanUpRows( idTableInfo.getQualifiedIdTableName(), session );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/package-info.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/package-info.java
new file mode 100644
index 0000000000..367faa98f5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/id/persistent/package-info.java
@@ -0,0 +1,30 @@
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
+
+/**
+ * Package contains an implementation of MultiTableBulkIdStrategy based on the use
+ * of a persistent (ANSI SQL term) table to hold id values.  It also holds a "session identifier"
+ * column which Hibernate manages in order to isolate rows from different sessions.
+ */
+package org.hibernate.hql.spi.id.persistent;
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 087ac1ac64..dbe332e2a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1448 +1,1448 @@
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
 		for ( final PersistentClass model : metadata.getEntityBindings() ) {
-			model.prepareTemporaryTables( metadata, getDialect() );
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
-				metadata
+				metadata,
+				sessionFactoryOptions
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
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 
 			listeners = settings.getBaselineSessionEventsListenerBuilder().buildBaselineList();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
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
 					getTransactionCompletionProcesses(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
index cd5e92ace6..83fab7ab63 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/log/DeprecationLogger.java
@@ -1,163 +1,163 @@
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
 package org.hibernate.internal.log;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.annotations.LogMessage;
 import org.jboss.logging.annotations.Message;
 import org.jboss.logging.annotations.MessageLogger;
 import org.jboss.logging.annotations.ValidIdRange;
 
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * Class to consolidate logging about usage of deprecated features.
  *
  * @author Steve Ebersole
  */
 @MessageLogger( projectCode = "HHH" )
 @ValidIdRange( min = 90000001, max = 90001000 )
 public interface DeprecationLogger {
 	public static final DeprecationLogger DEPRECATION_LOGGER = Logger.getMessageLogger(
 			DeprecationLogger.class,
 			"org.hibernate.orm.deprecation"
 	);
 
 	/**
 	 * Log about usage of deprecated Scanner setting
 	 */
 	@LogMessage( level = INFO )
 	@Message(
 			value = "Found usage of deprecated setting for specifying Scanner [hibernate.ejb.resource_scanner]; " +
 					"use [hibernate.archive.scanner] instead",
 			id = 90000001
 	)
 	public void logDeprecatedScannerSetting();
 
 	/**
 	 * Log message indicating the use of multiple EntityModes for a single entity.
 	 */
 	@LogMessage( level = WARN )
 	@Message(
 			value = "Support for an entity defining multiple entity-modes is deprecated",
 			id = 90000002
 	)
 	public void logDeprecationOfMultipleEntityModeSupport();
 
 	/**
 	 * Log message indicating the use of DOM4J EntityMode.
 	 */
 	@LogMessage( level = WARN )
 	@Message(
 			value = "Use of DOM4J entity-mode is considered deprecated",
 			id = 90000003
 	)
 	public void logDeprecationOfDomEntityModeSupport();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been " +
 					"removed, embed-xml attributes are no longer supported and should be removed from mappings.",
 			id = 90000004
 	)
 	public void logDeprecationOfEmbedXmlSupport();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Defining an entity [%s] with no physical id attribute is no longer supported; please map the " +
 					"identifier to a physical entity attribute",
 			id = 90000005
 	)
 	public void logDeprecationOfNonNamedIdAttribute(String entityName);
 
 	/**
 	 * Log a warning about an attempt to specify no-longer-supported NamingStrategy
 	 *
 	 * @param setting - The old setting that indicates the NamingStrategy to use
 	 * @param implicitInstead - The new setting that indicates the ImplicitNamingStrategy to use
 	 * @param physicalInstead - The new setting that indicates the PhysicalNamingStrategy to use
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via setting [%s]; NamingStrategy " +
 					"has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy; use [%s] or [%s], respectively, instead.",
 			id = 90000006
 	)
 	void logDeprecatedNamingStrategySetting(String setting, String implicitInstead, String physicalInstead);
 
 	/**
 	 * Log a warning about an attempt to specify unsupported NamingStrategy
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via command-line argument [--naming]. " +
 					"NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy; use [--implicit-naming] or [--physical-naming], respectively, instead.",
 			id = 90000007
 	)
 	void logDeprecatedNamingStrategyArgument();
 
 	/**
 	 * Log a warning about an attempt to specify unsupported NamingStrategy
 	 */
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Attempted to specify unsupported NamingStrategy via Ant task argument. " +
 					"NamingStrategy has been removed in favor of the split ImplicitNamingStrategy and " +
 					"PhysicalNamingStrategy.",
 			id = 90000008
 	)
 	void logDeprecatedNamingStrategyAntArgument();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "The outer-join attribute on <many-to-many> has been deprecated. " +
 					"Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, " +
 					"<bag>, <idbag>, or <list>, which will only initialize entities (not as " +
 					"a proxy) as needed.",
 			id = 90000009
 	)
 	void deprecatedManyToManyOuterJoin();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "The fetch attribute on <many-to-many> has been deprecated. " +
 					"Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, " +
 					"<bag>, <idbag>, or <list>, which will only initialize entities (not as " +
 					"a proxy) as needed.",
 			id = 90000010
 	)
 	void deprecatedManyToManyFetch();
 
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "org.hibernate.hql.spi.TemporaryTableBulkIdStrategy (temporary) has been deprecated in favor of the" +
-					" more specific org.hibernate.hql.spi.LocalTemporaryTableBulkIdStrategy (local_temporary).",
+					" more specific org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy (local_temporary).",
 			id = 90000011
 	)
 	void logDeprecationOfTemporaryTableBulkIdStrategy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
index 9bf86c1fd4..24fa192726 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
@@ -1,900 +1,875 @@
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
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.sql.Alias;
 
 /**
  * Mapping for an entity.
  *
  * @author Gavin King
  */
 public abstract class PersistentClass implements AttributeContainer, Serializable, Filterable, MetaAttributable {
 
 	private static final Alias PK_ALIAS = new Alias(15, "PK");
 
 	public static final String NULL_DISCRIMINATOR_MAPPING = "null";
 	public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";
 
 	private String entityName;
 
 	private String className;
 	private transient Class mappedClass;
 	
 	private String proxyInterfaceName;
 	private transient Class proxyInterface;
 	
 	private String nodeName;
 	private String jpaEntityName;
 
 	private String discriminatorValue;
 	private boolean lazy;
 	private ArrayList properties = new ArrayList();
 	private ArrayList declaredProperties = new ArrayList();
 	private final ArrayList subclasses = new ArrayList();
 	private final ArrayList subclassProperties = new ArrayList();
 	private final ArrayList subclassTables = new ArrayList();
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private int batchSize=-1;
 	private boolean selectBeforeUpdate;
 	private java.util.Map metaAttributes;
 	private ArrayList joins = new ArrayList();
 	private final ArrayList subclassJoins = new ArrayList();
 	private final java.util.List filters = new ArrayList();
 	protected final java.util.Set synchronizedTables = new HashSet();
 	private String loaderName;
 	private Boolean isAbstract;
 	private boolean hasSubselectLoadableCollections;
 	private Component identifierMapper;
 
 	// Custom SQL
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 
-	private String temporaryIdTableName;
-	private String temporaryIdTableDDL;
-
 	private java.util.Map tuplizerImpls;
 
 	private MappedSuperclass superMappedSuperclass;
 	private Component declaredIdentifierMapper;
 	private OptimisticLockStyle optimisticLockStyle;
 
 	public String getClassName() {
 		return className;
 	}
 
 	public void setClassName(String className) {
 		this.className = className==null ? null : className.intern();
 		this.mappedClass = null;
 	}
 
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 		this.proxyInterface = null;
 	}
 
 	public Class getMappedClass() throws MappingException {
 		if (className==null) return null;
 		try {
 			if(mappedClass == null) {
 				mappedClass = ReflectHelper.classForName(className);
 			}
 			return mappedClass;
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("entity class not found: " + className, cnfe);
 		}
 	}
 
 	public Class getProxyInterface() {
 		if (proxyInterfaceName==null) return null;
 		try {
 			if(proxyInterface == null) {
 				proxyInterface = ReflectHelper.classForName( proxyInterfaceName );
 			}
 			return proxyInterface;
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("proxy class not found: " + proxyInterfaceName, cnfe);
 		}
 	}
 	public boolean useDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	abstract int nextSubclassId();
 	public abstract int getSubclassId();
 	
 	public boolean useDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 
 	public String getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public void addSubclass(Subclass subclass) throws MappingException {
 		// inheritance cycle detection (paranoid check)
 		PersistentClass superclass = getSuperclass();
 		while (superclass!=null) {
 			if( subclass.getEntityName().equals( superclass.getEntityName() ) ) {
 				throw new MappingException(
 					"Circular inheritance mapping detected: " +
 					subclass.getEntityName() +
 					" will have it self as superclass when extending " +
 					getEntityName()
 				);
 			}
 			superclass = superclass.getSuperclass();
 		}
 		subclasses.add(subclass);
 	}
 
 	public boolean hasSubclasses() {
 		return subclasses.size() > 0;
 	}
 
 	public int getSubclassSpan() {
 		int n = subclasses.size();
 		Iterator iter = subclasses.iterator();
 		while ( iter.hasNext() ) {
 			n += ( (Subclass) iter.next() ).getSubclassSpan();
 		}
 		return n;
 	}
 	/**
 	 * Iterate over subclasses in a special 'order', most derived subclasses
 	 * first.
 	 */
 	public Iterator getSubclassIterator() {
 		Iterator[] iters = new Iterator[ subclasses.size() + 1 ];
 		Iterator iter = subclasses.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Subclass) iter.next() ).getSubclassIterator();
 		}
 		iters[i] = subclasses.iterator();
 		return new JoinedIterator(iters);
 	}
 
 	public Iterator getSubclassClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( new SingletonIterator(this) );
 		Iterator iter = getSubclassIterator();
 		while ( iter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass)  iter.next();
 			iters.add( clazz.getSubclassClosureIterator() );
 		}
 		return new JoinedIterator(iters);
 	}
 	
 	public Table getIdentityTable() {
 		return getRootTable();
 	}
 	
 	public Iterator getDirectSubclasses() {
 		return subclasses.iterator();
 	}
 
 	@Override
 	public void addProperty(Property p) {
 		properties.add(p);
 		declaredProperties.add(p);
 		p.setPersistentClass(this);
 	}
 
 	public abstract Table getTable();
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public abstract boolean isMutable();
 	public abstract boolean hasIdentifierProperty();
 	public abstract Property getIdentifierProperty();
 	public abstract Property getDeclaredIdentifierProperty();
 	public abstract KeyValue getIdentifier();
 	public abstract Property getVersion();
 	public abstract Property getDeclaredVersion();
 	public abstract Value getDiscriminator();
 	public abstract boolean isInherited();
 	public abstract boolean isPolymorphic();
 	public abstract boolean isVersioned();
 	public abstract String getNaturalIdCacheRegionName();
 	public abstract String getCacheConcurrencyStrategy();
 	public abstract PersistentClass getSuperclass();
 	public abstract boolean isExplicitPolymorphism();
 	public abstract boolean isDiscriminatorInsertable();
 
 	public abstract Iterator getPropertyClosureIterator();
 	public abstract Iterator getTableClosureIterator();
 	public abstract Iterator getKeyClosureIterator();
 
 	protected void addSubclassProperty(Property prop) {
 		subclassProperties.add(prop);
 	}
 	protected void addSubclassJoin(Join join) {
 		subclassJoins.add(join);
 	}
 	protected void addSubclassTable(Table subclassTable) {
 		subclassTables.add(subclassTable);
 	}
 	public Iterator getSubclassPropertyClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( getPropertyClosureIterator() );
 		iters.add( subclassProperties.iterator() );
 		for ( int i=0; i<subclassJoins.size(); i++ ) {
 			Join join = (Join) subclassJoins.get(i);
 			iters.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator(iters);
 	}
 	public Iterator getSubclassJoinClosureIterator() {
 		return new JoinedIterator( getJoinClosureIterator(), subclassJoins.iterator() );
 	}
 	public Iterator getSubclassTableClosureIterator() {
 		return new JoinedIterator( getTableClosureIterator(), subclassTables.iterator() );
 	}
 
 	public boolean isClassOrSuperclassJoin(Join join) {
 		return joins.contains(join);
 	}
 
 	public boolean isClassOrSuperclassTable(Table closureTable) {
 		return getTable()==closureTable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public abstract boolean hasEmbeddedIdentifier();
 	public abstract Class getEntityPersisterClass();
 	public abstract void setEntityPersisterClass(Class classPersisterClass);
 	public abstract Table getRootTable();
 	public abstract RootClass getRootClass();
 	public abstract KeyValue getKey();
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public void setEntityName(String entityName) {
 		this.entityName = entityName==null ? null : entityName.intern();
 	}
 
 	public void createPrimaryKey() {
 		//Primary key constraint
 		PrimaryKey pk = new PrimaryKey();
 		Table table = getTable();
 		pk.setTable(table);
 		pk.setName( PK_ALIAS.toAliasString( table.getName() ) );
 		table.setPrimaryKey(pk);
 
 		pk.addColumns( getKey().getColumnIterator() );
 	}
 
 	public abstract String getWhere();
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public boolean hasSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	/**
 	 * Build an iterator of properties which are "referenceable".
 	 *
 	 * @see #getReferencedProperty for a discussion of "referenceable"
 	 * @return The property iterator.
 	 */
 	public Iterator getReferenceablePropertyIterator() {
 		return getPropertyClosureIterator();
 	}
 
 	/**
 	 * Given a property path, locate the appropriate referenceable property reference.
 	 * <p/>
 	 * A referenceable property is a property  which can be a target of a foreign-key
 	 * mapping (an identifier or explcitly named in a property-ref).
 	 *
 	 * @param propertyPath The property path to resolve into a property reference.
 	 * @return The property reference (never null).
 	 * @throws MappingException If the property could not be found.
 	 */
 	public Property getReferencedProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getReferenceablePropertyIterator() );
 		}
 		catch ( MappingException e ) {
 			throw new MappingException(
 					"property-ref [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	public Property getRecursiveProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getPropertyIterator() );
 		}
 		catch ( MappingException e ) {
 			throw new MappingException(
 					"property [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	private Property getRecursiveProperty(String propertyPath, Iterator iter) throws MappingException {
 		Property property = null;
 		StringTokenizer st = new StringTokenizer( propertyPath, ".", false );
 		try {
 			while ( st.hasMoreElements() ) {
 				final String element = ( String ) st.nextElement();
 				if ( property == null ) {
 					Property identifierProperty = getIdentifierProperty();
 					if ( identifierProperty != null && identifierProperty.getName().equals( element ) ) {
 						// we have a mapped identifier property and the root of
 						// the incoming property path matched that identifier
 						// property
 						property = identifierProperty;
 					}
 					else if ( identifierProperty == null && getIdentifierMapper() != null ) {
 						// we have an embedded composite identifier
 						try {
 							identifierProperty = getProperty( element, getIdentifierMapper().getPropertyIterator() );
 							if ( identifierProperty != null ) {
 								// the root of the incoming property path matched one
 								// of the embedded composite identifier properties
 								property = identifierProperty;
 							}
 						}
 						catch( MappingException ignore ) {
 							// ignore it...
 						}
 					}
 
 					if ( property == null ) {
 						property = getProperty( element, iter );
 					}
 				}
 				else {
 					//flat recursive algorithm
 					property = ( ( Component ) property.getValue() ).getProperty( element );
 				}
 			}
 		}
 		catch ( MappingException e ) {
 			throw new MappingException( "property [" + propertyPath + "] not found on entity [" + getEntityName() + "]" );
 		}
 
 		return property;
 	}
 
 	private Property getProperty(String propertyName, Iterator iterator) throws MappingException {
 		if(iterator.hasNext()) {
 			String root = StringHelper.root(propertyName);
 			while ( iterator.hasNext() ) {
 				Property prop = (Property) iterator.next();
 				if ( prop.getName().equals( root ) ) {
 					return prop;
 				}
 			}
 		}
 		throw new MappingException( "property [" + propertyName + "] not found on entity [" + getEntityName() + "]" );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyClosureIterator();
 		Property identifierProperty = getIdentifierProperty();
 		if ( identifierProperty != null
 				&& identifierProperty.getName().equals( StringHelper.root(propertyName) )
 				) {
 			return identifierProperty;
 		}
 		else {
 			return getProperty( propertyName, iter );
 		}
 	}
 
 	@Deprecated
 	public int getOptimisticLockMode() {
 		return getOptimisticLockStyle().getOldCode();
 	}
 
 	@Deprecated
 	public void setOptimisticLockMode(int optimisticLockMode) {
 		setOptimisticLockStyle( OptimisticLockStyle.interpretOldCode( optimisticLockMode ) );
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
 		this.optimisticLockStyle = optimisticLockStyle;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !prop.isValid(mapping) ) {
 				throw new MappingException(
 						"property mapping has wrong number of columns: " +
 						StringHelper.qualify( getEntityName(), prop.getName() ) +
 						" type: " +
 						prop.getType().getName()
 					);
 			}
 		}
 		checkPropertyDuplication();
 		checkColumnDuplication();
 	}
 	
 	private void checkPropertyDuplication() throws MappingException {
 		HashSet names = new HashSet();
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !names.add( prop.getName() ) ) {
 				throw new MappingException( "Duplicate property mapping of " + prop.getName() + " found in " + getEntityName());
 			}
 		}
 	}
 
 	public boolean isDiscriminatorValueNotNull() {
 		return NOT_NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
 	}
 	public boolean isDiscriminatorValueNull() {
 		return NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
 	}
 
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 
 	public MetaAttribute getMetaAttribute(String name) {
 		return metaAttributes == null
 				? null
 				: (MetaAttribute) metaAttributes.get( name );
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getEntityName() + ')';
 	}
 	
 	public Iterator getJoinIterator() {
 		return joins.iterator();
 	}
 
 	public Iterator getJoinClosureIterator() {
 		return joins.iterator();
 	}
 
 	public void addJoin(Join join) {
 		joins.add(join);
 		join.setPersistentClass(this);
 	}
 
 	public int getJoinClosureSpan() {
 		return joins.size();
 	}
 
 	public int getPropertyClosureSpan() {
 		int span = properties.size();
 		for ( int i=0; i<joins.size(); i++ ) {
 			Join join = (Join) joins.get(i);
 			span += join.getPropertySpan();
 		}
 		return span;
 	}
 
 	public int getJoinNumber(Property prop) {
 		int result=1;
 		Iterator iter = getSubclassJoinClosureIterator();
 		while ( iter.hasNext() ) {
 			Join join = (Join) iter.next();
 			if ( join.containsProperty(prop) ) return result;
 			result++;
 		}
 		return 0;
 	}
 
 	/**
 	 * Build an iterator over the properties defined on this class.  The returned
 	 * iterator only accounts for "normal" properties (i.e. non-identifier
 	 * properties).
 	 * <p/>
 	 * Differs from {@link #getUnjoinedPropertyIterator} in that the iterator
 	 * we return here will include properties defined as part of a join.
 	 *
 	 * @return An iterator over the "normal" properties.
 	 */
 	public Iterator getPropertyIterator() {
 		ArrayList iterators = new ArrayList();
 		iterators.add( properties.iterator() );
 		for ( int i = 0; i < joins.size(); i++ ) {
 			Join join = ( Join ) joins.get( i );
 			iterators.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	/**
 	 * Build an iterator over the properties defined on this class <b>which
 	 * are not defined as part of a join</b>.  As with {@link #getPropertyIterator},
 	 * the returned iterator only accounts for non-identifier properties.
 	 *
 	 * @return An iterator over the non-joined "normal" properties.
 	 */
 	public Iterator getUnjoinedPropertyIterator() {
 		return properties.iterator();
 	}
 
 	public void setCustomSQLInsert(String customSQLInsert, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLInsert = customSQLInsert;
 		this.customInsertCallable = callable;
 		this.insertCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public boolean isCustomInsertCallable() {
 		return customInsertCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLUpdate = customSQLUpdate;
 		this.customUpdateCallable = callable;
 		this.updateCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public boolean isCustomUpdateCallable() {
 		return customUpdateCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	public void setCustomSQLDelete(String customSQLDelete, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDelete = customSQLDelete;
 		this.customDeleteCallable = callable;
 		this.deleteCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public boolean isCustomDeleteCallable() {
 		return customDeleteCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	public void addFilter(String name, String condition, boolean autoAliasInjection, java.util.Map<String,String> aliasTableMap, java.util.Map<String,String> aliasEntityMap) {
 		filters.add(new FilterConfiguration(name, condition, autoAliasInjection, aliasTableMap, aliasEntityMap,  this));
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public boolean isForceDiscriminator() {
 		return false;
 	}
 
 	public abstract boolean isJoinedSubclass();
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String loaderName) {
 		this.loaderName = loaderName==null ? null : loaderName.intern();
 	}
 
 	public abstract java.util.Set getSynchronizedTables();
 	
 	public void addSynchronizedTable(String table) {
 		synchronizedTables.add(table);
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public void setAbstract(Boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	protected void checkColumnDuplication(Set distinctColumns, Iterator columns) 
 	throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable columnOrFormula = (Selectable) columns.next();
 			if ( !columnOrFormula.isFormula() ) {
 				Column col = (Column) columnOrFormula;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException( 
 							"Repeated column in mapping for entity: " +
 							getEntityName() +
 							" column: " +
 							col.getName() + 
 							" (should be mapped with insert=\"false\" update=\"false\")"
 						);
 				}
 			}
 		}
 	}
 	
 	protected void checkPropertyColumnDuplication(Set distinctColumns, Iterator properties) 
 	throws MappingException {
 		while ( properties.hasNext() ) {
 			Property prop = (Property) properties.next();
 			if ( prop.getValue() instanceof Component ) { //TODO: remove use of instanceof!
 				Component component = (Component) prop.getValue();
 				checkPropertyColumnDuplication( distinctColumns, component.getPropertyIterator() );
 			}
 			else {
 				if ( prop.isUpdateable() || prop.isInsertable() ) {
 					checkColumnDuplication( distinctColumns, prop.getColumnIterator() );
 				}
 			}
 		}
 	}
 	
 	protected Iterator getNonDuplicatedPropertyIterator() {
 		return getUnjoinedPropertyIterator();
 	}
 	
 	protected Iterator getDiscriminatorColumnIterator() {
 		return EmptyIterator.INSTANCE;
 	}
 	
 	protected void checkColumnDuplication() {
 		HashSet cols = new HashSet();
 		if (getIdentifierMapper() == null ) {
 			//an identifier mapper => getKey will be included in the getNonDuplicatedPropertyIterator()
 			//and checked later, so it needs to be excluded
 			checkColumnDuplication( cols, getKey().getColumnIterator() );
 		}
 		checkColumnDuplication( cols, getDiscriminatorColumnIterator() );
 		checkPropertyColumnDuplication( cols, getNonDuplicatedPropertyIterator() );
 		Iterator iter = getJoinIterator();
 		while ( iter.hasNext() ) {
 			cols.clear();
 			Join join = (Join) iter.next();
 			checkColumnDuplication( cols, join.getKey().getColumnIterator() );
 			checkPropertyColumnDuplication( cols, join.getPropertyIterator() );
 		}
 	}
 	
 	public abstract Object accept(PersistentClassVisitor mv);
 	
 	public String getNodeName() {
 		return nodeName;
 	}
 	
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 	
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 	
 	public boolean hasPojoRepresentation() {
 		return getClassName()!=null;
 	}
 
 	public boolean hasDom4jRepresentation() {
 		return getNodeName()!=null;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 	
 	public void setSubselectLoadableCollections(boolean hasSubselectCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectCollections;
 	}
 
-	public void prepareTemporaryTables(Mapping mapping, Dialect dialect) {
-		temporaryIdTableName = dialect.generateTemporaryTableName( getTable().getName() );
-		if ( dialect.supportsTemporaryTables() ) {
-			Table table = new Table();
-			table.setName( temporaryIdTableName );
-			Iterator itr = getTable().getPrimaryKey().getColumnIterator();
-			while( itr.hasNext() ) {
-				Column column = (Column) itr.next();
-				table.addColumn( column.clone()  );
-			}
-			temporaryIdTableDDL = table.sqlTemporaryTableCreateString( dialect, mapping );
-		}
-	}
-
-	public String getTemporaryIdTableName() {
-		return temporaryIdTableName;
-	}
-
-	public String getTemporaryIdTableDDL() {
-		return temporaryIdTableDDL;
-	}
-
 	public Component getIdentifierMapper() {
 		return identifierMapper;
 	}
 
 	public Component getDeclaredIdentifierMapper() {
 		return declaredIdentifierMapper;
 	}
 
 	public void setDeclaredIdentifierMapper(Component declaredIdentifierMapper) {
 		this.declaredIdentifierMapper = declaredIdentifierMapper;
 	}
 
 	public boolean hasIdentifierMapper() {
 		return identifierMapper != null;
 	}
 
 	public void setIdentifierMapper(Component handle) {
 		this.identifierMapper = handle;
 	}
 
 	public void addTuplizer(EntityMode entityMode, String implClassName) {
 		if ( tuplizerImpls == null ) {
 			tuplizerImpls = new HashMap();
 		}
 		tuplizerImpls.put( entityMode, implClassName );
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		if ( tuplizerImpls == null ) return null;
 		return ( String ) tuplizerImpls.get( mode );
 	}
 
 	public java.util.Map getTuplizerMap() {
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return java.util.Collections.unmodifiableMap( tuplizerImpls );
 	}
 
 	public boolean hasNaturalId() {
 		Iterator props = getRootClass().getPropertyIterator();
 		while ( props.hasNext() ) {
 			if ( ( (Property) props.next() ).isNaturalIdentifier() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public abstract boolean isLazyPropertiesCacheable();
 
 	// The following methods are added to support @MappedSuperclass in the metamodel
 	public Iterator getDeclaredPropertyIterator() {
 		ArrayList iterators = new ArrayList();
 		iterators.add( declaredProperties.iterator() );
 		for ( int i = 0; i < joins.size(); i++ ) {
 			Join join = ( Join ) joins.get( i );
 			iterators.add( join.getDeclaredPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	public void addMappedsuperclassProperty(Property p) {
 		properties.add(p);
 		p.setPersistentClass(this);
 	}
 
 	public MappedSuperclass getSuperMappedSuperclass() {
 		return superMappedSuperclass;
 	}
 
 	public void setSuperMappedSuperclass(MappedSuperclass superMappedSuperclass) {
 		this.superMappedSuperclass = superMappedSuperclass;
 	}
 
 	// End of @Mappedsuperclass support
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 93d3b4a1f9..39805afd88 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,897 +1,871 @@
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
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.Exportable;
 import org.hibernate.boot.model.relational.QualifiedTableName;
 import org.hibernate.boot.model.relational.Schema;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tool.schema.extract.spi.ColumnInformation;
 import org.hibernate.tool.schema.extract.spi.TableInformation;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 @SuppressWarnings("unchecked")
 public class Table implements RelationalModel, Serializable, Exportable {
 
 	private Identifier catalog;
 	private Identifier schema;
 	private Identifier name;
 
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<ForeignKeyKey, ForeignKey> foreignKeys = new LinkedHashMap<ForeignKeyKey, ForeignKey>();
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private List<String> checkConstraints = new ArrayList<String>();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables;
 	private String comment;
 
 	public Table() {
 	}
 
 	public Table(String name) {
 		setName( name );
 	}
 
 	public Table(
 			Schema schema,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = schema.getPhysicalName().getCatalog();
 		this.schema = schema.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(
 			Identifier catalog,
 			Identifier schema,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = catalog;
 		this.schema = schema;
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Schema schema, Identifier physicalTableName, String subselect, boolean isAbstract) {
 		this.catalog = schema.getPhysicalName().getCatalog();
 		this.schema = schema.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Schema schema, String subselect, boolean isAbstract) {
 		this.catalog = schema.getPhysicalName().getCatalog();
 		this.schema = schema.getPhysicalName().getSchema();
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public void setName(String name) {
 		this.name = Identifier.toIdentifier( name );
 	}
 
 	public String getName() {
 		return name == null ? null : name.getText();
 	}
 
 	public Identifier getNameIdentifier() {
 		return name;
 	}
 
 	public String getQuotedName() {
 		return name == null ? null : name.toString();
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return name == null ? null : name.render( dialect );
 	}
 
 	public QualifiedTableName getQualifiedTableName() {
 		return name == null ? null : new QualifiedTableName( catalog, schema, name );
 	}
 
 	public boolean isQuoted() {
 		return name.isQuoted();
 	}
 
 	public void setQuoted(boolean quoted) {
 		if ( quoted == name.isQuoted() ) {
 			return;
 		}
 		this.name = new Identifier( name.getText(), quoted );
 	}
 
 	public void setSchema(String schema) {
 		this.schema = Identifier.toIdentifier( schema );
 	}
 
 	public String getSchema() {
 		return schema == null ? null : schema.getText();
 	}
 
 	public String getQuotedSchema() {
 		return schema == null ? null : schema.toString();
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schema == null ? null : schema.render( dialect );
 	}
 
 	public boolean isSchemaQuoted() {
 		return schema != null && schema.isQuoted();
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = Identifier.toIdentifier( catalog );
 	}
 
 	public String getCatalog() {
 		return catalog == null ? null : catalog.getText();
 	}
 
 	public String getQuotedCatalog() {
 		return catalog == null ? null : catalog.render();
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalog == null ? null : catalog.render( dialect );
 	}
 
 	public boolean isCatalogQuoted() {
 		return catalog != null && catalog.isQuoted();
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(Identifier name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		return (Column) columns.get( name.getCanonicalName() );
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Map<ForeignKeyKey, ForeignKey> getForeignKeys() {
 		return Collections.unmodifiableMap( foreignKeys );
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
 		result = prime * result + ((name == null) ? 0 : name.hashCode());
 		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return Identifier.areEqual( name, table.name )
 				&& Identifier.areEqual( schema, table.schema )
 				&& Identifier.areEqual( catalog, table.catalog );
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase(Locale.ROOT)
 						.startsWith( columnInfo.getTypeName().toLowerCase(Locale.ROOT) )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase(Locale.ROOT) +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(
 			Dialect dialect,
 			Mapping p,
 			TableInformation tableInfo,
 			String defaultCatalog,
 			String defaultSchema) throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
 		while ( iter.hasNext() ) {
 			final Column column = (Column) iter.next();
 			final ColumnInformation columnInfo = tableInfo.getColumn( Identifier.toIdentifier( column.getName() ) );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					String keyName = Constraint.generateName( "UK_", this, column );
 					UniqueKey uk = getOrCreateUniqueKey( keyName );
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.getColumnDefinitionUniquenessFragment( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				alter.append( dialect.getAddColumnSuffixString() );
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
-	public String sqlTemporaryTableCreateString(Dialect dialect, Mapping mapping) throws HibernateException {
-		StringBuilder buffer = new StringBuilder( dialect.getCreateTemporaryTableString() )
-				.append( ' ' )
-				.append( name )
-				.append( " (" );
-		Iterator itr = getColumnIterator();
-		while ( itr.hasNext() ) {
-			final Column column = (Column) itr.next();
-			buffer.append( column.getQuotedName( dialect ) ).append( ' ' );
-			buffer.append( column.getSqlType( dialect, mapping ) );
-			if ( column.isNullable() ) {
-				buffer.append( dialect.getNullColumnString() );
-			}
-			else {
-				buffer.append( " not null" );
-			}
-			if ( itr.hasNext() ) {
-				buffer.append( ", " );
-			}
-		}
-		buffer.append( ") " );
-		buffer.append( dialect.getCreateTemporaryTablePostfix() );
-		return buffer.toString();
-	}
-
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				String keyName = Constraint.generateName( "UK_", this, col );
 				UniqueKey uk = getOrCreateUniqueKey( keyName );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( String checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = Constraint.generateName( "UK_", this, keyColumns );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
 	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
 		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
 	}
 
 	public ForeignKey createForeignKey(
 			String keyName,
 			List keyColumns,
 			String referencedEntityName,
 			List referencedColumns) {
 		final ForeignKeyKey key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			fk.setTable( this );
 			fk.setReferencedEntityName( referencedEntityName );
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 
 			// NOTE : if the name is null, we will generate an implicit name during second pass processing
 			// after we know the referenced table name (which might not be resolved yet).
 			fk.setName( keyName );
 
 			foreignKeys.put( key, fk );
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() ).append( "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() ).append( "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator<String> getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				comments.add( "comment on table " + tableName + " is '" + comment + "'" );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					comments.add( "comment on column " + tableName + '.' + column.getQuotedName( dialect ) + " is '" + columnComment + "'" );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return Table.qualify(
 				render( catalog ),
 				render( schema ),
 				name.render()
 		);
 	}
 
 	private String render(Identifier identifier) {
 		return identifier == null ? null : identifier.render();
 	}
 
 
 	public static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk.columns.equals( columns ) &&
 					fkk.referencedClassName.equals( referencedClassName ) && fkk.referencedColumns
 					.equals( referencedColumns );
 		}
 
 		@Override
 		public String toString() {
 			return "ForeignKeyKey{" +
 					"columns=" + StringHelper.join( ",", columns ) +
 					", referencedClassName='" + referencedClassName + '\'' +
 					", referencedColumns=" + StringHelper.join( ",", referencedColumns ) +
 					'}';
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 58ab70d7ea..c98f5e2f83 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1797 +1,1790 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
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
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.ValueGeneration;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 import org.jboss.logging.Logger;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 		SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractEntityPersister.class.getName() );
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryHelper cacheEntryHelper;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityEntryFactory entityEntryFactory;
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
 	
 	private final List<Integer> lobProperties = new ArrayList<Integer>();
 
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
 
 	private final Set<String> affectingFetchProfileNames = new HashSet<String>();
 
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
 
-	private final String temporaryIdTableName;
-	private final String temporaryIdTableDDL;
-
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	private final boolean useReferenceCacheEntries;
 
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
 
 	protected abstract String filterFragment(String alias, Set<String> treatAsDeclarations);
 
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
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 
 		if( entityMetamodel.isMutable() ) {
 			this.entityEntryFactory = MutableEntityEntryFactory.INSTANCE;
 		}
 		else {
 			this.entityEntryFactory = ImmutableEntityEntryFactory.INSTANCE;
 		}
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
 			
 			if (prop.isLob() && getFactory().getDialect().forceLobAsLastValue() ) {
 				lobProperties.add( i );
 			}
 
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
 			propertyDefinedOnSubclass[j++] = (Boolean) iter.next();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
-		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
-		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
-
-
 		// Check if we can use Reference Cached entities in 2lc
 		// todo : should really validate that the cache access type is read-only
 		boolean refCacheEntries = true;
 		if ( ! factory.getSettings().isDirectReferenceCacheEntriesEnabled() ) {
 			refCacheEntries = false;
 		}
 
 		// for now, limit this to just entities that:
 		// 		1) are immutable
 		if ( entityMetamodel.isMutable() ) {
 			refCacheEntries =  false;
 		}
 
 		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
 		for ( Type type : getSubclassPropertyTypeClosure() ) {
 			if ( type.isAssociationType() ) {
 				refCacheEntries =  false;
 			}
 		}
 
 		useReferenceCacheEntries = refCacheEntries;
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 
 	}
 
 	protected CacheEntryHelper buildCacheEntryHelper() {
 		if ( cacheAccessStrategy == null ) {
 			// the entity defined no caching...
 			return NoopCacheEntryHelper.INSTANCE;
 		}
 
 		if ( canUseReferenceCacheEntries() ) {
 			entityMetamodel.setLazy( false );
 			// todo : do we also need to unset proxy factory?
 			return new ReferenceCacheEntryHelper( this );
 		}
 
 		return factory.getSettings().isStructuredCacheEntriesEnabled()
 				? new StructuredCacheEntryHelper( this )
 				: new StandardCacheEntryHelper( this );
 	}
 
 	public boolean canUseReferenceCacheEntries() {
 		return useReferenceCacheEntries;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
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
 			LOG.tracev( "Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString( this, id, getFactory() ), fieldName );
 		}
 
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
 			final Object ce = CacheHelper.fromSharedCache( session, cacheKey, getCacheAccessStrategy() );
 			if ( ce != null ) {
 				final CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
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
 
 		if ( !hasLazyProperties() ) throw new AssertionFailure( "no lazy properties" );
 
 		LOG.trace( "Initializing lazy properties from datastore" );
 
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
 						rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
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
 
 		LOG.trace( "Initializing lazy properties from second-level cache" );
 
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
 
 		LOG.trace( "Done initializing lazy properties" );
 
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current persistent state for: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
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
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) throws HibernateException {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"resolving unique key [%s] to identifier for entity [%s]",
 					key,
 					getEntityName()
 			);
 		}
 
 		int propertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		if ( propertyIndex < 0 ) {
 			throw new HibernateException(
 					"Could not determine Type for property [" + uniquePropertyName + "] on entity [" + getEntityName() + "]"
 			);
 		}
 		Type propertyType = getSubclassPropertyType( propertyIndex );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve unique property [%s] to identifier for entity [%s]",
 							uniquePropertyName,
 							getEntityName()
 					),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	protected String generateIdByUniqueKeySelectString(String uniquePropertyName) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "resolve id by unique property [" + getEntityName() + "." + uniquePropertyName + "]" );
 		}
 
 		final String rooAlias = getRootAlias();
 
 		select.setFromClause( fromTableFragment( rooAlias ) + fromJoinFragment( rooAlias, true, false ) );
 
 		SelectFragment selectFragment = new SelectFragment();
 		selectFragment.addColumns( rooAlias, getIdentifierColumnNames(), getIdentifierAliases() );
 		select.setSelectClause( selectFragment );
 
 		StringBuilder whereClauseBuffer = new StringBuilder();
 		final int uniquePropertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		final String uniquePropertyTableAlias = generateTableAlias(
 				rooAlias,
 				getSubclassPropertyTableNumber( uniquePropertyIndex )
 		);
 		String sep = "";
 		for ( String columnTemplate : getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex] ) {
 			if ( columnTemplate == null ) {
 				continue;
 			}
 			final String columnReference = StringHelper.replace( columnTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace( formulaTemplate, Template.TEMPLATE, uniquePropertyTableAlias );
 			whereClauseBuffer.append( sep ).append( formulaReference ).append( "=?" );
 			sep = " and ";
 		}
 		whereClauseBuffer.append( whereJoinFragment( rooAlias, true, false ) );
 
 		select.setWhereClause( whereClauseBuffer.toString() );
 
 		return select.setOuterJoins( "", "" ).toStatementString();
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
 		return generateGeneratedValuesSelectString( GenerationTiming.INSERT );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.ALWAYS );
 	}
 
 	private String generateGeneratedValuesSelectString(final GenerationTiming generationTimingToMatch) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment(
 				getRootAlias(),
 				new InclusionChecker() {
 					@Override
 					public boolean includeProperty(int propertyNumber) {
 						final InDatabaseValueGenerationStrategy generationStrategy
 								= entityMetamodel.getInDatabaseValueGenerationStrategies()[propertyNumber];
 						return generationStrategy != null
 								&& timingsMatch( generationStrategy.getGenerationTiming(), generationTimingToMatch );
 					}
 				}
 		);
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
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
 
 		String whereClause = new StringBuilder()
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
 				int rows = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
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
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 		lockers.put( LockMode.UPGRADE_SKIPLOCKED, generateLocker( LockMode.UPGRADE_SKIPLOCKED ) );
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
 
 	public static String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
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
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
@@ -3412,1706 +3405,1698 @@ public abstract class AbstractEntityPersister
 		return join;
 	}
 
 	protected JoinType determineSubclassTableJoinType(
 			int subclassTableNumber,
 			boolean canInnerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 
 		if ( isClassOrSuperclassTable( subclassTableNumber ) ) {
 			final boolean shouldInnerJoin = canInnerJoin
 					&& !isInverseTable( subclassTableNumber )
 					&& !isNullableTable( subclassTableNumber );
 			// the table is either this persister's driving table or (one of) its super class persister's driving
 			// tables which can be inner joined as long as the `shouldInnerJoin` condition resolves to true
 			return shouldInnerJoin ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN;
 		}
 
 		// otherwise we have a subclass table and need to look a little deeper...
 
 		// IMPL NOTE : By default includeSubclasses indicates that all subclasses should be joined and that each
 		// subclass ought to be joined by outer-join.  However, TREAT-AS always requires that an inner-join be used
 		// so we give TREAT-AS higher precedence...
 
 		if ( isSubclassTableIndicatedByTreatAsDeclarations( subclassTableNumber, treatAsDeclarations ) ) {
 			return JoinType.INNER_JOIN;
 		}
 
 		if ( includeSubclasses
 				&& !isSubclassTableSequentialSelect( subclassTableNumber )
 				&& !isSubclassTableLazy( subclassTableNumber ) ) {
 			return JoinType.LEFT_OUTER_JOIN;
 		}
 
 		return JoinType.NONE;
 	}
 
 	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
 			int subclassTableNumber,
 			Set<String> treatAsDeclarations) {
 		return false;
 	}
 
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int i = 1; i < tableNumbers.length; i++ ) {
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j )
 							? JoinType.LEFT_OUTER_JOIN
 							: JoinType.INNER_JOIN
 			);
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	/**
 	 * Post-construct is a callback for AbstractEntityPersister subclasses to call after they are all done with their
 	 * constructor processing.  It allows AbstractEntityPersister to extend its construction after all subclass-specific
 	 * details have been handled.
 	 *
 	 * @param mapping The mapping
 	 *
 	 * @throws MappingException Indicates a problem accessing the Mapping
 	 */
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths( mapping );
 
 		//doLateInit();
 		prepareEntityIdentifierDefinition();
 	}
 
 	private void doLateInit() {
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 	}
 
 	public final void postInstantiate() throws MappingException {
 		doLateInit();
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 		doPostInstantiate();
 	}
 
 	protected void doPostInstantiate() {
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.UPGRADE_SKIPLOCKED,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_SKIPLOCKED )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingActions.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingActions.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEntityGraph(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().getFetchGraph() != null || session.getLoadQueryInfluencers()
 				.getLoadGraph() != null;
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		for ( String s : session.getLoadQueryInfluencers().getEnabledFetchProfileNames() ) {
 			if ( affectingFetchProfileNames.contains( s ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( isAffectedByEntityGraph( session ) ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryHelper.getCacheEntryStructure();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 		return cacheEntryHelper.buildCacheEntry( entity, state, version, session );
 	}
 
 	public boolean hasNaturalIdCache() {
 		return naturalIdRegionAccessStrategy != null;
 	}
 	
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( ! hasNaturalIdentifier() ) {
 			return;
 		}
 
 		if ( getEntityMetamodel().hasImmutableNaturalId() ) {
 			// we assume there were no changes to natural id during detachment for now, that is validated later
 			// during flush.
 			return;
 		}
 
 		final NaturalIdHelper naturalIdHelper = session.getPersistenceContext().getNaturalIdHelper();
 		final Serializable id = getIdentifier( entity, session );
 
 		// for reattachment of mutable natural-ids, we absolutely positively have to grab the snapshot from the
 		// database, because we have no other way to know if the state changed while detached.
 		final Object[] naturalIdSnapshot;
 		final Object[] entitySnapshot = session.getPersistenceContext().getDatabaseSnapshot( id, this );
 		if ( entitySnapshot == StatefulPersistenceContext.NO_ROW ) {
 			naturalIdSnapshot = null;
 		}
 		else {
 			naturalIdSnapshot = naturalIdHelper.extractNaturalIdValues( entitySnapshot, this );
 		}
 
 		naturalIdHelper.removeSharedNaturalIdCrossReference( this, id, naturalIdSnapshot );
 		naturalIdHelper.manageLocalNaturalIdCrossReference(
 				this,
 				id,
 				naturalIdHelper.extractNaturalIdValues( entity, this ),
 				naturalIdSnapshot,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			final Object ce = CacheHelper.fromSharedCache( session, ck, getCacheAccessStrategy() );
 			if ( ce != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && getEntityMetamodel().isVersionGenerated();
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return null;
 	}
 
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return null;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
-	public String getTemporaryIdTableName() {
-		return temporaryIdTableName;
-	}
-
-	public String getTemporaryIdTableDDL() {
-		return temporaryIdTableDDL;
-	}
-
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, GenerationTiming.INSERT );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, GenerationTiming.ALWAYS );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 			GenerationTiming matchTiming) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					int propertyIndex = -1;
 					for ( NonIdentifierAttribute attribute : entityMetamodel.getProperties() ) {
 						propertyIndex++;
 						final ValueGeneration valueGeneration = attribute.getValueGenerationStrategy();
 						if ( isReadRequired( valueGeneration, matchTiming ) ) {
 							final Object hydratedState = attribute.getType().hydrate(
 									rs, getPropertyAliases(
 									"",
 									propertyIndex
 							), session, entity
 							);
 							state[propertyIndex] = attribute.getType().resolve( hydratedState, session, entity );
 							setPropertyValue( entity, propertyIndex, state[propertyIndex] );
 						}
 					}
 //					for ( int i = 0; i < getPropertySpan(); i++ ) {
 //						if ( includeds[i] != ValueInclusion.NONE ) {
 //							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 //							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 //							setPropertyValue( entity, i, state[i] );
 //						}
 //					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	/**
 	 * Whether the given value generation strategy requires to read the value from the database or not.
 	 */
 	private boolean isReadRequired(ValueGeneration valueGeneration, GenerationTiming matchTiming) {
 		return valueGeneration != null &&
 				valueGeneration.getValueGenerator() == null &&
 				timingsMatch( valueGeneration.getGenerationTiming(), matchTiming );
 	}
 
 	private boolean timingsMatch(GenerationTiming timing, GenerationTiming matchTiming) {
 		return
 				(matchTiming == GenerationTiming.INSERT && timing.includesInsert()) ||
 						(matchTiming == GenerationTiming.ALWAYS && timing.includesUpdate());
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 			);
 		}
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		final boolean[] valueNullness = determineValueNullness( naturalIdValues );
 		final String sqlEntityIdByNaturalIdString = determinePkByNaturalIdQuery( valueNullness );
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Object naturalIdValue = naturalIdValues[loop++];
 					if ( naturalIdValue != null ) {
 						final Type type = getPropertyTypes()[idPosition];
 						type.nullSafeSet( ps, naturalIdValue, positions, session );
 						positions += type.getColumnSpan( session.getFactory() );
 					}
 				}
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					final Object hydratedId = getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 					return (Serializable) getIdentifierType().resolve( hydratedId, session, null );
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, ps );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( ps );
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private boolean[] determineValueNullness(Object[] naturalIdValues) {
 		boolean[] nullness = new boolean[ naturalIdValues.length ];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( ! hasNaturalIdentifier() ) {
 			throw new HibernateException( "Attempt to build natural-id -> PK resolution query for entity that does not define natural id" );
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && ! ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
 		return generateEntityIdByNaturalIdSql( valueNullness );
 	}
 
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable;
 	}
 
 	private boolean determineNaturalIdNullability() {
 		boolean[] nullability = getPropertyNullability();
 		for ( int position : getNaturalIdentifierProperties() ) {
 			// if any individual property is nullable, return false
 			if ( nullability[position] ) {
 				return false;
 			}
 		}
 		// return true if we found no individually nullable properties
 		return true;
 	}
 
 	private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
 		EntityPersister rootPersister = getFactory().getEntityPersister( getRootEntityName() );
 		if ( rootPersister != this ) {
 			if ( rootPersister instanceof AbstractEntityPersister ) {
 				return ( (AbstractEntityPersister) rootPersister ).generateEntityIdByNaturalIdSql( valueNullness );
 			}
 		}
 
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		int valuesIndex = -1;
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			valuesIndex++;
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			if ( valueNullness != null && valueNullness[valuesIndex] ) {
 				whereClause.append( StringHelper.join( " is null and ", aliasedPropertyColumns ) ).append( " is null" );
 			}
 			else {
 				whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 			}
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 	
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 	
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return entityMetamodel.getInstrumentationMetadata();
 	}
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
 		return this.entityEntryFactory;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public static interface CacheEntryHelper {
 		public CacheEntryStructure getCacheEntryStructure();
 
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
 
 
 	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityIdentifierDefinition entityIdentifierDefinition;
 	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
 	private Iterable<AttributeDefinition> attributeDefinitions;
 
 	@Override
 	public void generateEntityDefinition() {
 		prepareEntityIdentifierDefinition();
 		collectAttributeDefinitions();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		return entityIdentifierDefinition;
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return attributeDefinitions;
 	}
 
 
 	private void prepareEntityIdentifierDefinition() {
 		if ( entityIdentifierDefinition != null ) {
 			return;
 		}
 		final Type idType = getIdentifierType();
 
 		if ( !idType.isComponentType() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildSimpleEncapsulatedIdentifierDefinition( this );
 			return;
 		}
 
 		final CompositeType cidType = (CompositeType) idType;
 		if ( !cidType.isEmbedded() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildEncapsulatedCompositeIdentifierDefinition( this );
 			return;
 		}
 
 		entityIdentifierDefinition =
 				EntityIdentifierDefinitionHelper.buildNonEncapsulatedCompositeIdentifierDefinition( this );
 	}
 
 	private void collectAttributeDefinitions(
 			Map<String,AttributeDefinition> attributeDefinitionsByName,
 			EntityMetamodel metamodel) {
 		for ( int i = 0; i < metamodel.getPropertySpan(); i++ ) {
 			final AttributeDefinition attributeDefinition = metamodel.getProperties()[i];
 			// Don't replace an attribute definition if it is already in attributeDefinitionsByName
 			// because the new value will be from a subclass.
 			final AttributeDefinition oldAttributeDefinition = attributeDefinitionsByName.get(
 					attributeDefinition.getName()
 			);
 			if ( oldAttributeDefinition != null ) {
 				if ( LOG.isTraceEnabled() ) {
 						LOG.tracef(
 								"Ignoring subclass attribute definition [%s.%s] because it is defined in a superclass ",
 								entityMetamodel.getName(),
 								attributeDefinition.getName()
 						);
 				}
 			}
 			else {
 				attributeDefinitionsByName.put( attributeDefinition.getName(), attributeDefinition );
 			}
 		}
 
 		// see if there are any subclass persisters...
 		final Set<String> subClassEntityNames = metamodel.getSubclassEntityNames();
 		if ( subClassEntityNames == null ) {
 			return;
 		}
 
 		// see if we can find the persisters...
 		for ( String subClassEntityName : subClassEntityNames ) {
 			if ( metamodel.getName().equals( subClassEntityName ) ) {
 				// skip it
 				continue;
 			}
 			try {
 				final EntityPersister subClassEntityPersister = factory.getEntityPersister( subClassEntityName );
 				collectAttributeDefinitions( attributeDefinitionsByName, subClassEntityPersister.getEntityMetamodel() );
 			}
 			catch (MappingException e) {
 				throw new IllegalStateException(
 						String.format(
 								"Could not locate subclass EntityPersister [%s] while processing EntityPersister [%s]",
 								subClassEntityName,
 								metamodel.getName()
 						),
 						e
 				);
 			}
 		}
 	}
 
 	private void collectAttributeDefinitions() {
 		// todo : I think this works purely based on luck atm
 		// 		specifically in terms of the sub/super class entity persister(s) being available.  Bit of chicken-egg
 		// 		problem there:
 		//			* If I do this during postConstruct (as it is now), it works as long as the
 		//			super entity persister is already registered, but I don't think that is necessarily true.
 		//			* If I do this during postInstantiate then lots of stuff in postConstruct breaks if we want
 		//			to try and drive SQL generation on these (which we do ultimately).  A possible solution there
 		//			would be to delay all SQL generation until postInstantiate
 
 		Map<String,AttributeDefinition> attributeDefinitionsByName = new LinkedHashMap<String,AttributeDefinition>();
 		collectAttributeDefinitions( attributeDefinitionsByName, getEntityMetamodel() );
 
 
 //		EntityMetamodel currentEntityMetamodel = this.getEntityMetamodel();
 //		while ( currentEntityMetamodel != null ) {
 //			for ( int i = 0; i < currentEntityMetamodel.getPropertySpan(); i++ ) {
 //				attributeDefinitions.add( currentEntityMetamodel.getProperties()[i] );
 //			}
 //			// see if there is a super class EntityMetamodel
 //			final String superEntityName = currentEntityMetamodel.getSuperclass();
 //			if ( superEntityName != null ) {
 //				currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //			}
 //			else {
 //				currentEntityMetamodel = null;
 //			}
 //		}
 
 		this.attributeDefinitions = Collections.unmodifiableList(
 				new ArrayList<AttributeDefinition>( attributeDefinitionsByName.values() )
 		);
 //		// todo : leverage the attribute definitions housed on EntityMetamodel
 //		// 		for that to work, we'd have to be able to walk our super entity persister(s)
 //		this.attributeDefinitions = new Iterable<AttributeDefinition>() {
 //			@Override
 //			public Iterator<AttributeDefinition> iterator() {
 //				return new Iterator<AttributeDefinition>() {
 ////					private final int numberOfAttributes = countSubclassProperties();
 ////					private final int numberOfAttributes = entityMetamodel.getPropertySpan();
 //
 //					EntityMetamodel currentEntityMetamodel = entityMetamodel;
 //					int numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //
 //					private int currentAttributeNumber;
 //
 //					@Override
 //					public boolean hasNext() {
 //						return currentEntityMetamodel != null
 //								&& currentAttributeNumber < numberOfAttributesInCurrentEntityMetamodel;
 //					}
 //
 //					@Override
 //					public AttributeDefinition next() {
 //						final int attributeNumber = currentAttributeNumber;
 //						currentAttributeNumber++;
 //						final AttributeDefinition next = currentEntityMetamodel.getProperties()[ attributeNumber ];
 //
 //						if ( currentAttributeNumber >= numberOfAttributesInCurrentEntityMetamodel ) {
 //							// see if there is a super class EntityMetamodel
 //							final String superEntityName = currentEntityMetamodel.getSuperclass();
 //							if ( superEntityName != null ) {
 //								currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //								if ( currentEntityMetamodel != null ) {
 //									numberOfAttributesInCurrentEntityMetamodel = currentEntityMetamodel.getPropertySpan();
 //									currentAttributeNumber = 0;
 //								}
 //							}
 //						}
 //
 //						return next;
 //					}
 //
 //					@Override
 //					public void remove() {
 //						throw new UnsupportedOperationException( "Remove operation not supported here" );
 //					}
 //				};
 //			}
 //		};
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
index bf92493f04..968c9883ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/Queryable.java
@@ -1,187 +1,171 @@
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
 import org.hibernate.sql.SelectFragment;
 
 /**
  * Extends the generic <tt>EntityPersister</tt> contract to add
  * operations required by the Hibernate Query Language
  *
  * @author Gavin King
  */
 public interface Queryable extends Loadable, PropertyMapping, Joinable {
 
 	/**
 	 * Is this an abstract class?
 	 */
 	public boolean isAbstract();
 	/**
 	 * Is this class explicit polymorphism only?
 	 */
 	public boolean isExplicitPolymorphism();
 	/**
 	 * Get the class that this class is mapped as a subclass of -
 	 * not necessarily the direct superclass
 	 */
 	public String getMappedSuperclass();
 	/**
 	 * Get the discriminator value for this particular concrete subclass,
 	 * as a string that may be embedded in a select statement
 	 */
 	public String getDiscriminatorSQLValue();
 
 	/**
 	 * Given a query alias and an identifying suffix, render the identifier select fragment.
 	 */
 	public String identifierSelectFragment(String name, String suffix);
 	/**
 	 * Given a query alias and an identifying suffix, render the property select fragment.
 	 */
 	public String propertySelectFragment(String alias, String suffix, boolean allProperties);
 
 	public SelectFragment propertySelectFragmentFragment(String alias, String suffix, boolean allProperties);
 	/**
 	 * Get the names of columns used to persist the identifier
 	 */
 	public String[] getIdentifierColumnNames();
 
 	/**
 	 * Is the inheritance hierarchy described by this persister contained across
 	 * multiple tables?
 	 *
 	 * @return True if the inheritance hierarchy is spread across multiple tables; false otherwise.
 	 */
 	public boolean isMultiTable();
 
 	/**
 	 * Get the names of all tables used in the hierarchy (up and down) ordered such
 	 * that deletes in the given order would not cause constraint violations.
 	 *
 	 * @return The ordered array of table names.
 	 */
 	public String[] getConstraintOrderedTableNameClosure();
 
 	/**
 	 * For each table specified in {@link #getConstraintOrderedTableNameClosure()}, get
 	 * the columns that define the key between the various hierarchy classes.
 	 * <p/>
 	 * The first dimension here corresponds to the table indexes returned in
 	 * {@link #getConstraintOrderedTableNameClosure()}.
 	 * <p/>
 	 * The second dimension should have the same length across all the elements in
 	 * the first dimension.  If not, that would be a problem ;)
 	 *
 	 */
 	public String[][] getContraintOrderedTableKeyColumnClosure();
 
 	/**
-	 * Get the name of the temporary table to be used to (potentially) store id values
-	 * when performing bulk update/deletes.
-	 *
-	 * @return The appropriate temporary table name.
-	 */
-	public String getTemporaryIdTableName();
-
-	/**
-	 * Get the appropriate DDL command for generating the temporary table to
-	 * be used to (potentially) store id values when performing bulk update/deletes.
-	 *
-	 * @return The appropriate temporary table creation command.
-	 */
-	public String getTemporaryIdTableDDL();
-
-	/**
 	 * Given a property name, determine the number of the table which contains the column
 	 * to which this property is mapped.
 	 * <p/>
 	 * Note that this is <b>not</b> relative to the results from {@link #getConstraintOrderedTableNameClosure()}.
 	 * It is relative to the subclass table name closure maintained internal to the persister (yick!).
 	 * It is also relative to the indexing used to resolve {@link #getSubclassTableName}...
 	 *
 	 * @param propertyPath The name of the property.
 	 * @return The number of the table to which the property is mapped.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath);
 
 	/**
 	 * Determine whether the given property is declared by our
 	 * mapped class, our super class, or one of our subclasses...
 	 * <p/>
 	 * Note: the method is called 'subclass property...' simply
 	 * for consistency sake (e.g. {@link #getSubclassPropertyTableNumber}
 	 *
 	 * @param propertyPath The property name.
 	 * @return The property declarer
 	 */
 	public Declarer getSubclassPropertyDeclarer(String propertyPath);
 
 	/**
 	 * Get the name of the table with the given index from the internal
 	 * array.
 	 *
 	 * @param number The index into the internal array.
 	 */
 	public String getSubclassTableName(int number);
 
 	/**
 	 * Is the version property included in insert statements?
 	 */
 	public boolean isVersionPropertyInsertable();
 
 	/**
 	 * The alias used for any filter conditions (mapped where-fragments or
 	 * enabled-filters).
 	 * </p>
 	 * This may or may not be different from the root alias depending upon the
 	 * inheritance mapping strategy.
 	 *
 	 * @param rootAlias The root alias
 	 * @return The alias used for "filter conditions" within the where clause.
 	 */
 	public String generateFilterConditionAlias(String rootAlias);
 
 	/**
 	 * Retrieve the information needed to properly deal with this entity's discriminator
 	 * in a query.
 	 *
 	 * @return The entity discriminator metadata
 	 */
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata();
 
 	String[][] getSubclassPropertyFormulaTemplateClosure();
 
 	public static class Declarer {
 		public static final Declarer CLASS = new Declarer( "class" );
 		public static final Declarer SUBCLASS = new Declarer( "subclass" );
 		public static final Declarer SUPERCLASS = new Declarer( "superclass" );
 		private final String name;
 		public Declarer(String name) {
 			this.name = name;
 		}
 		public String toString() {
 			return name;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/TemporaryTableExporter.java b/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/TemporaryTableExporter.java
deleted file mode 100644
index b618b4d98d..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/TemporaryTableExporter.java
+++ /dev/null
@@ -1,90 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2015, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.tool.schema.internal;
-
-import java.util.List;
-
-import org.hibernate.boot.Metadata;
-import org.hibernate.boot.model.naming.Identifier;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.mapping.Column;
-import org.hibernate.mapping.Table;
-
-/**
- * @author Strong Liu <stliu@hibernate.org>
- */
-public class TemporaryTableExporter extends StandardTableExporter {
-	public TemporaryTableExporter(Dialect dialect) {
-		super(dialect);
-	}
-
-	@Override
-	public String[] getSqlCreateStrings(Table exportable, Metadata metadata) {
-		if ( dialect.supportsTemporaryTables() ) {
-			final String temporaryTableName = generateTableName( dialect, exportable );
-			Table temporaryTable = new Table(
-					Identifier.toIdentifier( exportable.getQuotedCatalog() ),
-					Identifier.toIdentifier( exportable.getQuotedSchema() ),
-					Identifier.toIdentifier( temporaryTableName ),
-					false
-			);
-			//noinspection unchecked
-			for ( Column column : ( (List<Column>) exportable.getPrimaryKey().getColumns() ) ) {
-				final Column clone = column.clone();
-				temporaryTable.addColumn( clone );
-			}
-			return super.getSqlCreateStrings( temporaryTable, metadata );
-		}
-		return null;
-	}
-
-	public static String generateTableName(final Dialect dialect, final Table primaryTable) {
-		return dialect.generateTemporaryTableName( primaryTable.getName() );
-	}
-
-	@Override
-	protected String tableCreateString(boolean hasPrimaryKey) {
-		return dialect.getCreateTemporaryTableString();
-	}
-
-	@Override
-	protected void applyTableCheck(Table table, StringBuilder buf) {
-		// N/A
-	}
-
-	@Override
-	protected void applyComments(Table table, List<String> sqlStrings) {
-		// N/A
-	}
-
-	@Override
-	protected void applyTableTypeString(StringBuilder buf) {
-		buf.append( " " ).append( dialect.getCreateTemporaryTablePostfix() );
-	}
-
-	@Override
-	public String[] getSqlDropStrings(Table exportable, Metadata metadata) {
-		return null;
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/dialect/Oracle8iDialectTestCase.java b/hibernate-core/src/test/java/org/hibernate/dialect/Oracle8iDialectTestCase.java
index f71efd9842..b38b2cd4a4 100644
--- a/hibernate-core/src/test/java/org/hibernate/dialect/Oracle8iDialectTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/dialect/Oracle8iDialectTestCase.java
@@ -1,53 +1,57 @@
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
 package org.hibernate.dialect;
 
+import org.hibernate.hql.spi.id.AbstractMultiTableBulkIdStrategyImpl;
+
 import org.junit.Test;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 public class Oracle8iDialectTestCase extends BaseUnitTestCase {
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-9290")
 	public void testTemporaryTableNameTruncation() throws Exception {
-		String temporaryTableName = new Oracle8iDialect().generateTemporaryTableName(
+		final AbstractMultiTableBulkIdStrategyImpl strategy = (AbstractMultiTableBulkIdStrategyImpl) new Oracle8iDialect().getDefaultMultiTableBulkIdStrategy();
+
+		String temporaryTableName = strategy.getIdTableSupport().generateIdTableName(
 				"TABLE_NAME_THAT_EXCEEDS_30_CHARACTERS"
 		);
 
 		assertEquals(
 				"Temporary table names should be truncated to 30 characters",
 				30,
 				temporaryTableName.length()
 		);
 		assertEquals(
 				"Temporary table names should start with HT_",
 				"HT_TABLE_NAME_THAT_EXCEEDS_30_",
 				temporaryTableName
 		);
 	}
 }
\ No newline at end of file
diff --git a/working-5.0-migration-guide.md b/working-5.0-migration-guide.md
index b47bc92e1d..f38ba40e8e 100644
--- a/working-5.0-migration-guide.md
+++ b/working-5.0-migration-guide.md
@@ -1,79 +1,80 @@
 Working list of changes for 5.0
 ===================================
 
 * Switch from Configuration to ServiceRegistry+Metadata for SessionFactory building
 * `org.hibernate.hql.spi.MultiTableBulkIdStrategy#prepare` contract has been changed to account for Metadata
 * (proposed) `org.hibernate.persister.spi.PersisterFactory` contract, specifically building CollectionPersisters)
   has been changed to account for Metadata
 * extract `org.hibernate.engine.jdbc.env.spi.JdbcEnvironment` from `JdbcServices`; create
   `org.hibernate.engine.jdbc.env` package and moved a few contracts there.
 * Introduction of `org.hibernate.boot.model.relational.ExportableProducer` which will effect any
  `org.hibernate.id.PersistentIdentifierGenerator` implementations
 * Change to signature of `org.hibernate.id.Configurable` to accept `JdbcEnvironment` rather than just `Dialect`
 * Removed deprecated `org.hibernate.id.TableGenerator` id-generator
 * Removed deprecated `org.hibernate.id.TableHiLoGenerator` (hilo) id-generator
 * Deprecated `org.hibernate.id.SequenceGenerator` and its subclasses
 * cfg.xml files are again fully parsed and integrated (events, security, etc)
 * Removed the deprecated `org.hibernate.cfg.AnnotationConfiguration`
 * `Integrator` contract
 * `Configuration` is  no longer `Serializable`
 * `org.hibernate.dialect.Dialect.getQuerySequencesString` expected to retrieve catalog, schema, and increment values as well
 * properties loaded from cfg.xml through EMF did not previously prefix names with "hibernate." this is now made consistent.
 * removed AuditConfiguration in preference for new `org.hibernate.envers.boot.internal.EnversService`
 * changed AuditStrategy method parameters from (removed) AuditConfiguration to (new) EnversService
 * Built-in `org.hibernate.type.descriptor.sql.SqlTypeDescriptor` implementations no longer auto-register themselves
     with `org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry`.  Applications using custom SqlTypeDescriptor
     implementations extending the built-in ones and relying on that behavior should be updated to call
     `SqlTypeDescriptorRegistry#addDescriptor` themselves.
-
+* Moving `org.hibernate.hql.spi.MultiTableBulkIdStrategy` and friends to new `org.hibernate.hql.spi.id` package
+    and sub-packages
 
 TODOs
 =====
 * Still need to go back and make all "persistent id generators" to properly implement ExportableProducer
 * Add a setting to "consistently apply" naming strategies.  E.g. use the "join column" methods from hbm.xml binding.
 * Along with this ^^ consistency setting, split the implicit naming strategy for join columns into multiple methods - one for each usage:
    * many-to-one
    * one-to-one
    * etc
 
 
 Blog items
 ==========
 * New bootstrapping API - better determinism, better integration
 * Java 8 Support (though still compatible with Java 6).
 * Ability to handle additional Java types for id attributes marked as `GenerationType#AUTO`.  Built-in support
     for Number and UUID.  Expandable via new `org.hibernate.boot.model.IdGeneratorStrategyInterpreter` extension
 * Expanded support for AttributeConverters.
     * fully supported for non-`@Enumerated` enum values
     * applicable in conjunction with `@Nationalized` support
     * called to handle null values
     * settable in hbm.xml by using `type="converter:fully.qualified.AttributeConverterName"`
     * integrated with hibernate-envers
     * collection values, map keys
 * scanning support for non-JPA usage
 * naming strategy
 
 
 Proposals for discussion
 ========================
 * Currently there is a "post-binding" hook to allow validation of the bound model (PersistentClass,
 Property, Value, etc).  However, the top-level entry points are currently the only possible place
 (per contract) to throw exceptions when a validation fails".  I'd like to instead consider a pattern
 where each level is asked to validate itself.  Given the current model, however, this is unfortunately
 not a win-win situation.  `org.hibernate.boot.model.source.internal.hbm.ModelBinder#createManyToOneAttribute`
 illustrates one such use case where this would be worthwhile, and also can illustrate how pushing the
 validation (and exception throwing down) can be less than stellar given the current model.  In the process
 of binding a many-to-one, we need to validate that any many-to-one that defines "delete-orphan" cascading
 is a "logical one-to-one".  There are 2 ways a many-to-one can be marked as a "logical one-to-one"; first
 is at the `<many-to-one/>` level; the other is through a singular `<column/>` that is marked as unique.
 Occasionally the binding of the column(s) of a many-to-one need to be delayed until a second pass, which
 means that sometimes we cannot perform this check immediately from the `#createManyToOneAttribute` method.
 What would be ideal would be to check this after all binding is complete.  In current code, this could be
 either an additional SecondPass or done in a `ManyToOne#isValid` override of `SimpleValue#isValid`.  The
 `ManyToOne#isValid` approach illustrates the conundrum... In the `ManyToOne#isValid` call we know the real
 reason the validation failed (non-unique many-to-one marked for orphan delete) but not the property name/path.
 Simply returning false from `ManyToOne#isValid` would instead lead to a misleading exception message, which
 would at least have the proper context to know the property name/path.
 * Should `org.hibernate.boot.MetadataBuilder` be folded into `org.hibernate.boot.MetadataSources`?
 * Consider an additional "naming strategy contract" specifically for logical naming.  This would be non-pluggable, and
 would be the thing that generates the names we use to cross-reference and locate tables, columns, etc.
\ No newline at end of file
