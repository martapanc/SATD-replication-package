diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 88af1d7786..3d89015f0f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,642 +1,641 @@
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
 	 * Names the {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver} implementation to use.
 	 * @since 4.3
 	 */
 	public static final String JTA_PLATFORM_RESOLVER = "hibernate.transaction.jta.platform_resolver";
 
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
 
-    /**
-     * The jacc context id of the deployment
-     */
-    public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
-
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
 	 * Used to define a {@link java.util.Collection} of the {@link ClassLoader} instances Hibernate should use for
 	 * class-loading and resource-lookups.
 	 *
 	 * @since 5.0
 	 */
 	public static final String CLASSLOADERS = "hibernate.classLoaders";
 
 	/**
 	 * Names the {@link ClassLoader} used to load user application classes.
 	 * @since 4.0
 	 *
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	public static final String APP_CLASSLOADER = "hibernate.classLoader.application";
 
 	/**
 	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
 	 * @since 4.0
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	public static final String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
 
 	/**
 	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
 	 * the {@link ClassLoader} that loaded this class.
 	 * @since 4.0
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	public static final String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
 
 	/**
 	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
 	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
 	 * @since 4.0
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
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
 
 	/**
 	 * Enable nationalized character support on all string / clob based attribute ( string, char, clob, text etc ).
 	 *
 	 * Default is <clode>false</clode>.
 	 */
 	public static final String USE_NATIONALIZED_CHARACTER_DATA = "hibernate.use_nationalized_character_data";
 	
 	/**
 	 * A transaction can be rolled back by another thread ("tracking by thread")
 	 * -- not the original application. Examples of this include a JTA
 	 * transaction timeout handled by a background reaper thread.  The ability
 	 * to handle this situation requires checking the Thread ID every time
 	 * Session is called.  This can certainly have performance considerations.
 	 * 
 	 * Default is <code>true</code> (enabled).
 	 */
 	public static final String JTA_TRACK_BY_THREAD = "hibernate.jta.track_by_thread";
+
+	public static final String JACC_CONTEXT_ID = "hibernate.jacc_context_id";
+	public static final String JACC_PREFIX = "hibernate.jacc";
+	public static final String JACC_ENABLED = "hibernate.jacc.enabled";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 701d769946..c62ef69eff 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1137 +1,1138 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.MetadataProvider;
 import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.internal.util.xml.ErrorLogger;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.Origin;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XMLHelper;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.internal.util.xml.XmlDocumentImpl;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.secure.internal.JACCConfiguration;
+import org.hibernate.secure.spi.GrantedPermission;
+import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 /**
  * An instance of <tt>Configuration</tt> allows the application
  * to specify properties and mapping documents to be used when
  * creating a <tt>SessionFactory</tt>. Usually an application will create
  * a single <tt>Configuration</tt>, build a single instance of
  * <tt>SessionFactory</tt> and then instantiate <tt>Session</tt>s in
  * threads servicing client requests. The <tt>Configuration</tt> is meant
  * only as an initialization-time object. <tt>SessionFactory</tt>s are
  * immutable and do not retain any association back to the
  * <tt>Configuration</tt>.<br>
  * <br>
  * A new <tt>Configuration</tt> will use the properties specified in
  * <tt>hibernate.properties</tt> by default.
  * <p/>
  * NOTE : This will be replaced by use of {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder} and
  * {@link org.hibernate.metamodel.MetadataSources} instead after the 4.0 release at which point this class will become
  * deprecated and scheduled for removal in 5.0.  See
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6183">HHH-6183</a>,
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-2578">HHH-2578</a> and
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6586">HHH-6586</a> for details
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class Configuration implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
 
 	public static final String ARTEFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
 
 	/**
 	 * Class name of the class needed to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_CLASS = "org.hibernate.search.event.EventListenerRegister";
 
 	/**
 	 * Method to call to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_METHOD = "enableHibernateSearch";
 
 	protected MetadataSourceQueue metadataSourceQueue;
 	private transient ReflectionManager reflectionManager;
 
 	protected Map<String, PersistentClass> classes;
 	protected Map<String, String> imports;
 	protected Map<String, Collection> collections;
 	protected Map<String, Table> tables;
 	protected List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects;
 
 	protected Map<String, NamedQueryDefinition> namedQueries;
 	protected Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	protected Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 
 	protected Map<String, TypeDef> typeDefs;
 	protected Map<String, FilterDefinition> filterDefinitions;
 	protected Map<String, FetchProfile> fetchProfiles;
 
 	protected Map tableNameBinding;
 	protected Map columnNameBindingPerTable;
 
 	protected List<SecondPass> secondPasses;
 	protected List<Mappings.PropertyReference> propertyReferences;
 	protected Map<ExtendsQueueEntry, ?> extendsQueue;
 
 	protected Map<String, SQLFunction> sqlFunctions;
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private EntityTuplizerFactory entityTuplizerFactory;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 
 	private Interceptor interceptor;
 	private Properties properties;
 	private EntityResolver entityResolver;
 	private EntityNotFoundDelegate entityNotFoundDelegate;
 
 	protected transient XMLHelper xmlHelper;
 	protected NamingStrategy namingStrategy;
 	private SessionFactoryObserver sessionFactoryObserver;
 
 	protected final SettingsFactory settingsFactory;
 
 	private transient Mapping mapping = buildMapping();
 
 	private MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private Map<Class<?>, org.hibernate.mapping.MappedSuperclass> mappedSuperClasses;
 
 	private Map<String, IdGenerator> namedGenerators;
 	private Map<String, Map<String, Join>> joins;
 	private Map<String, AnnotatedClassType> classTypes;
 	private Set<String> defaultNamedQueryNames;
 	private Set<String> defaultNamedNativeQueryNames;
 	private Set<String> defaultSqlResultSetMappingNames;
 	private Set<String> defaultNamedGenerators;
 	private Map<String, Properties> generatorTables;
 	private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
 	private Map<Table, List<JPAIndexHolder>> jpaIndexHoldersByTable;
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private List<CacheHolder> caches;
 	private boolean inSecondPass = false;
 	private boolean isDefaultProcessed = false;
 	private boolean isValidatorNotPresentLogged;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 	private boolean specjProprietarySyntaxEnabled;
 
 	private ConcurrentHashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 
 	protected Configuration(SettingsFactory settingsFactory) {
 		this.settingsFactory = settingsFactory;
 		reset();
 	}
 
 	public Configuration() {
 		this( new SettingsFactory() );
 	}
 
 	protected void reset() {
 		metadataSourceQueue = new MetadataSourceQueue();
 		createReflectionManager();
 
 		classes = new HashMap<String,PersistentClass>();
 		imports = new HashMap<String,String>();
 		collections = new HashMap<String,Collection>();
 		tables = new TreeMap<String,Table>();
 
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		namedSqlQueries = new HashMap<String,NamedSQLQueryDefinition>();
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 
 		typeDefs = new HashMap<String,TypeDef>();
 		filterDefinitions = new HashMap<String, FilterDefinition>();
 		fetchProfiles = new HashMap<String, FetchProfile>();
 		auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 		tableNameBinding = new HashMap();
 		columnNameBindingPerTable = new HashMap();
 
 		secondPasses = new ArrayList<SecondPass>();
 		propertyReferences = new ArrayList<Mappings.PropertyReference>();
 		extendsQueue = new HashMap<ExtendsQueueEntry, String>();
 
 		xmlHelper = new XMLHelper();
 		interceptor = EmptyInterceptor.INSTANCE;
 		properties = Environment.getProperties();
 		entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
 
 		sqlFunctions = new HashMap<String, SQLFunction>();
 
 		entityTuplizerFactory = new EntityTuplizerFactory();
 //		componentTuplizerFactory = new ComponentTuplizerFactory();
 
 		identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 		mappedSuperClasses = new HashMap<Class<?>, MappedSuperclass>();
 
 		metadataSourcePrecedence = Collections.emptyList();
 
 		namedGenerators = new HashMap<String, IdGenerator>();
 		joins = new HashMap<String, Map<String, Join>>();
 		classTypes = new HashMap<String, AnnotatedClassType>();
 		generatorTables = new HashMap<String, Properties>();
 		defaultNamedQueryNames = new HashSet<String>();
 		defaultNamedNativeQueryNames = new HashSet<String>();
 		defaultSqlResultSetMappingNames = new HashSet<String>();
 		defaultNamedGenerators = new HashSet<String>();
 		uniqueConstraintHoldersByTable = new HashMap<Table, List<UniqueConstraintHolder>>();
 		jpaIndexHoldersByTable = new HashMap<Table,List<JPAIndexHolder>>(  );
 		mappedByResolver = new HashMap<String, String>();
 		propertyRefResolver = new HashMap<String, String>();
 		caches = new ArrayList<CacheHolder>();
 		namingStrategy = EJB3NamingStrategy.INSTANCE;
 		setEntityResolver( new EJB3DTDEntityResolver() );
 		anyMetaDefs = new HashMap<String, AnyMetaDef>();
 		propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
 		specjProprietarySyntaxEnabled = System.getProperty( "hibernate.enable_specj_proprietary_syntax" ) != null;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 	public ReflectionManager getReflectionManager() {
 		return reflectionManager;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
 	/**
 	 * Iterate the entity mappings
 	 *
 	 * @return Iterator of the entity mappings currently contained in the configuration.
 	 */
 	public Iterator<PersistentClass> getClassMappings() {
 		return classes.values().iterator();
 	}
 
 	/**
 	 * Iterate the collection mappings
 	 *
 	 * @return Iterator of the collection mappings currently contained in the configuration.
 	 */
 	public Iterator getCollectionMappings() {
 		return collections.values().iterator();
 	}
 
 	/**
 	 * Iterate the table mappings
 	 *
 	 * @return Iterator of the table mappings currently contained in the configuration.
 	 */
 	public Iterator<Table> getTableMappings() {
 		return tables.values().iterator();
 	}
 
 	/**
 	 * Iterate the mapped super class mappings
 	 * EXPERIMENTAL Consider this API as PRIVATE
 	 *
 	 * @return iterator over the MappedSuperclass mapping currently contained in the configuration.
 	 */
 	public Iterator<MappedSuperclass> getMappedSuperclassMappings() {
 		return mappedSuperClasses.values().iterator();
 	}
 
 	/**
 	 * Get the mapping for a particular entity
 	 *
 	 * @param entityName An entity name.
 	 * @return the entity mapping information
 	 */
 	public PersistentClass getClassMapping(String entityName) {
 		return classes.get( entityName );
 	}
 
 	/**
 	 * Get the mapping for a particular collection role
 	 *
 	 * @param role a collection role
 	 * @return The collection mapping information
 	 */
 	public Collection getCollectionMapping(String role) {
 		return collections.get( role );
 	}
 
 	/**
 	 * Set a custom entity resolver. This entity resolver must be
 	 * set before addXXX(misc) call.
 	 * Default value is {@link org.hibernate.internal.util.xml.DTDEntityResolver}
 	 *
 	 * @param entityResolver entity resolver to use
 	 */
 	public void setEntityResolver(EntityResolver entityResolver) {
 		this.entityResolver = entityResolver;
 	}
 
 	public EntityResolver getEntityResolver() {
 		return entityResolver;
 	}
 
 	/**
 	 * Retrieve the user-supplied delegate to handle non-existent entity
 	 * scenarios.  May be null.
 	 *
 	 * @return The user-supplied delegate
 	 */
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	/**
 	 * Specify a user-supplied delegate to be used to handle scenarios where an entity could not be
 	 * located by specified id.  This is mainly intended for EJB3 implementations to be able to
 	 * control how proxy initialization errors should be handled...
 	 *
 	 * @param entityNotFoundDelegate The delegate to use
 	 */
 	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates inability to locate or parse
 	 * the specified mapping file.
 	 * @see #addFile(java.io.File)
 	 */
 	public Configuration addFile(String xmlFile) throws MappingException {
 		return addFile( new File( xmlFile ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.  Historically this could
 	 * have indicated a problem parsing the XML document, but that is now delayed until after {@link #buildMappings}
 	 */
 	public Configuration addFile(final File xmlFile) throws MappingException {
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		final String name =  xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 		add( inputSource, "file", name );
 		return this;
 	}
 
 	private XmlDocument add(InputSource inputSource, String originType, String originName) {
 		return add( inputSource, new OriginImpl( originType, originName ) );
 	}
 
 	private XmlDocument add(InputSource inputSource, Origin origin) {
 		XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument( entityResolver, inputSource, origin );
 		add( metadataXml );
 		return metadataXml;
 	}
 
 	public void add(XmlDocument metadataXml) {
 		if ( inSecondPass || !isOrmXml( metadataXml ) ) {
 			metadataSourceQueue.add( metadataXml );
 		}
 		else {
 			final MetadataProvider metadataProvider = ( (MetadataProviderInjector) reflectionManager ).getMetadataProvider();
 			JPAMetadataProvider jpaMetadataProvider = ( JPAMetadataProvider ) metadataProvider;
 			List<String> classNames = jpaMetadataProvider.getXMLContext().addDocument( metadataXml.getDocumentTree() );
 			for ( String className : classNames ) {
 				try {
 					metadataSourceQueue.add( reflectionManager.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to load class defined in XML: " + className, e );
 				}
 			}
 		}
 	}
 
 	private static boolean isOrmXml(XmlDocument xmlDocument) {
 		return "entity-mappings".equals( xmlDocument.getDocumentTree().getRootElement().getName() );
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation
 	 * of the DOM structure of a particular mapping.  It is saved from a previous
 	 * call as a file with the name <tt>xmlFile + ".bin"</tt> where xmlFile is
 	 * the name of the original mapping file.
 	 * </p>
 	 * If a cached <tt>xmlFile + ".bin"</tt> exists and is newer than
 	 * <tt>xmlFile</tt> the <tt>".bin"</tt> file will be read directly. Otherwise
 	 * xmlFile is read and then serialized to <tt>xmlFile + ".bin"</tt> for use
 	 * the next time.
 	 *
 	 * @param xmlFile The cacheable mapping file to be added.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 */
 	public Configuration addCacheableFile(File xmlFile) throws MappingException {
 		File cachedFile = determineCachedDomFile( xmlFile );
 
 		try {
 			return addCacheableFileStrictly( xmlFile );
 		}
 		catch ( SerializationException e ) {
 			LOG.unableToDeserializeCache( cachedFile.getPath(), e );
 		}
 		catch ( FileNotFoundException e ) {
 			LOG.cachedFileNotFound( cachedFile.getPath(), e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
 			LOG.debugf( "Writing cache file for: %s to: %s", xmlFile, cachedFile );
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
 		}
 		catch ( Exception e ) {
 			LOG.unableToWriteCachedFile( cachedFile.getPath(), e.getMessage() );
 		}
 
 		return this;
 	}
 
 	private File determineCachedDomFile(File xmlFile) {
 		return new File( xmlFile.getAbsolutePath() + ".bin" );
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param xmlFile The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public Configuration addCacheableFileStrictly(File xmlFile) throws SerializationException, FileNotFoundException {
 		final File cachedFile = determineCachedDomFile( xmlFile );
 
 		final boolean useCachedFile = xmlFile.exists()
 				&& cachedFile.exists()
 				&& xmlFile.lastModified() < cachedFile.lastModified();
 
 		if ( ! useCachedFile ) {
 			throw new FileNotFoundException( "Cached file could not be found or could not be used" );
 		}
 
 		LOG.readingCachedMappings( cachedFile );
 		Document document = ( Document ) SerializationHelper.deserialize( new FileInputStream( cachedFile ) );
 		add( new XmlDocumentImpl( document, "file", xmlFile.getAbsolutePath() ) );
 		return this;
 	}
 
 	/**
 	 * Add a cacheable mapping file.
 	 *
 	 * @param xmlFile The name of the file to be added.  This must be in a form
 	 * useable to simply construct a {@link java.io.File} instance.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public Configuration addCacheableFile(String xmlFile) throws MappingException {
 		return addCacheableFile( new File( xmlFile ) );
 	}
 
 
 	/**
 	 * Read mappings from a <tt>String</tt>
 	 *
 	 * @param xml an XML string
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates problems parsing the
 	 * given XML string
 	 */
 	public Configuration addXML(String xml) throws MappingException {
 		LOG.debugf( "Mapping XML:\n%s", xml );
 		final InputSource inputSource = new InputSource( new StringReader( xml ) );
 		add( inputSource, "string", "XML String" );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a <tt>URL</tt>
 	 *
 	 * @param url The url for the mapping document to be read.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the URL or processing
 	 * the mapping document.
 	 */
 	public Configuration addURL(URL url) throws MappingException {
 		final String urlExternalForm = url.toExternalForm();
 
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		try {
 			add( url.openStream(), "URL", urlExternalForm );
 		}
 		catch ( IOException e ) {
 			throw new InvalidMappingException( "Unable to open url stream [" + urlExternalForm + "]", "URL", urlExternalForm, e );
 		}
 		return this;
 	}
 
 	private XmlDocument add(InputStream inputStream, final String type, final String name) {
 		final InputSource inputSource = new InputSource( inputStream );
 		try {
 			return add( inputSource, type, name );
 		}
 		finally {
 			try {
 				inputStream.close();
 			}
 			catch ( IOException ignore ) {
 				LOG.trace( "Was unable to close input stream");
 			}
 		}
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 */
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		LOG.debugf( "Mapping Document:\n%s", doc );
 
 		final Document document = xmlHelper.createDOMReader().read( doc );
 		add( new XmlDocumentImpl( document, "unknown", null ) );
 
 		return this;
 	}
 
 	/**
 	 * Read mappings from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the stream, or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		add( xmlInputStream, "input stream", null );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resource (i.e. classpath lookup).
 	 *
 	 * @param resourceName The resource name
 	 * @param classLoader The class loader to use.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		InputStream resourceInputStream = classLoader.getResourceAsStream( resourceName );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup)
 	 * trying different class loaders.
 	 *
 	 * @param resourceName The resource name
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		ClassLoader contextClassLoader = ClassLoaderHelper.getContextClassLoader();
 		InputStream resourceInputStream = null;
 		if ( contextClassLoader != null ) {
 			resourceInputStream = contextClassLoader.getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			resourceInputStream = Environment.class.getClassLoader().getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class
 	 * named <tt>foo.bar.Foo</tt> is mapped by a file <tt>foo/bar/Foo.hbm.xml</tt>
 	 * which can be resolved as a classpath resource.
 	 *
 	 * @param persistentClass The mapped class
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addClass(Class persistentClass) throws MappingException {
 		String mappingResourceName = persistentClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		LOG.readingMappingsFromResource( mappingResourceName );
 		return addResource( mappingResourceName, persistentClass.getClassLoader() );
 	}
 
 	/**
 	 * Read metadata from the annotations associated with this class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Configuration addAnnotatedClass(Class annotatedClass) {
 		XClass xClass = reflectionManager.toXClass( annotatedClass );
 		metadataSourceQueue.add( xClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
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
 	public Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
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
 
@@ -1201,2019 +1202,2038 @@ public class Configuration implements Serializable {
 					if (constraintString != null) script.add( constraintString );
 				}
 
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
 		
 		// TEMPORARY
 		// Ensure the correct ClassLoader is used in commons-annotations.
 		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 		Thread.currentThread().setContextClassLoader( ClassLoaderHelper.getContextClassLoader() );
 
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
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? StringHelper.randomFixedLengthHex("UK_")
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 		
 		for(Table table : jpaIndexHoldersByTable.keySet()){
 			final List<JPAIndexHolder> jpaIndexHolders = jpaIndexHoldersByTable.get( table );
 			int uniqueIndexPerTable = 0;
 			for ( JPAIndexHolder holder : jpaIndexHolders ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "idx_"+table.getName()+"_" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns(), holder.getOrdering(), holder.isUnique() );
 			}
 		}
 		
 		Thread.currentThread().setContextClassLoader( tccl );
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
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames){
 		buildUniqueKeyFromColumnNames( table, keyName, columnNames, null, true );
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames, String[] orderings, boolean unique) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			String column = columnNames[index];
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( column, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( column ) );
 			}
 		}
 		if ( unique ) {
 			UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					uk.addColumn( column, order );
 					unbound.remove( column );
 				}
 			}
 		}
 		else {
 			Index index = table.getOrCreateIndex( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					index.addColumn( column, order );
 					unbound.remove( column );
 				}
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
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append("'").append( column.getName() ).append( "', " );
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
 				.build();
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
 
+	private JaccPermissionDeclarations jaccPermissionDeclarations;
+
 	private void parseSecurity(Element secNode) {
-		String contextId = secNode.attributeValue( "context" );
-		setProperty( Environment.JACC_CONTEXTID, contextId );
-		LOG.jaccContextId( contextId );
-		JACCConfiguration jcfg = new JACCConfiguration( contextId );
+		final String nodeContextId = secNode.attributeValue( "context" );
+
+		final String explicitContextId = getProperty( AvailableSettings.JACC_CONTEXT_ID );
+		if ( explicitContextId == null ) {
+			setProperty( AvailableSettings.JACC_CONTEXT_ID, nodeContextId );
+			LOG.jaccContextId( nodeContextId );
+		}
+		else {
+			// if they dont match, throw an error
+			if ( ! nodeContextId.equals( explicitContextId ) ) {
+				throw new HibernateException( "Non-matching JACC context ids" );
+			}
+		}
+		jaccPermissionDeclarations = new JaccPermissionDeclarations( nodeContextId );
+
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
-			Element grantElement = (Element) grantElements.next();
-			String elementName = grantElement.getName();
+			final Element grantElement = (Element) grantElements.next();
+			final String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
-				jcfg.addPermission(
-						grantElement.attributeValue( "role" ),
-						grantElement.attributeValue( "entity-name" ),
-						grantElement.attributeValue( "actions" )
-					);
+				jaccPermissionDeclarations.addPermissionDeclaration(
+						new GrantedPermission(
+								grantElement.attributeValue( "role" ),
+								grantElement.attributeValue( "entity-name" ),
+								grantElement.attributeValue( "actions" )
+						)
+				);
 			}
 		}
 	}
 
+	public JaccPermissionDeclarations getJaccPermissionDeclarations() {
+		return jaccPermissionDeclarations;
+	}
+
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
 			}
 		}
 
 		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			Object old = sqlResultSetMappings.put( sqlResultSetMapping.getName(), sqlResultSetMapping );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "resultSet",  sqlResultSetMapping.getName() );
 			}
 		}
 
 		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 			final String name = definition.getName();
 			if ( !defaultSqlResultSetMappingNames.contains( name ) && getResultSetMapping( name ) != null ) {
 				removeResultSetMapping( name );
 			}
 			applyResultSetMapping( definition );
 			defaultSqlResultSetMappingNames.add( name );
 		}
 
 		protected void removeResultSetMapping(String name) {
 			sqlResultSetMappings.remove( name );
 		}
 
 		public TypeDef getTypeDef(String typeName) {
 			return typeDefs.get( typeName );
 		}
 
 		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
 			TypeDef def = new TypeDef( typeClass, paramMap );
 			typeDefs.put( typeName, def );
 			LOG.debugf( "Added %s with class %s", typeName, typeClass );
 		}
 
 		public Map getFilterDefinitions() {
 			return filterDefinitions;
 		}
 
 		public FilterDefinition getFilterDefinition(String name) {
 			return filterDefinitions.get( name );
 		}
 
 		public void addFilterDefinition(FilterDefinition definition) {
 			filterDefinitions.put( definition.getFilterName(), definition );
 		}
 
 		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
 			FetchProfile profile = fetchProfiles.get( name );
 			if ( profile == null ) {
 				profile = new FetchProfile( name, source );
 				fetchProfiles.put( name, profile );
 			}
 			return profile;
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
 			return iterateAuxiliaryDatabaseObjects();
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
 			return auxiliaryDatabaseObjects.iterator();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
 			return iterateAuxiliaryDatabaseObjectsInReverse();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse() {
 			return auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 		}
 
 		public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 			auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 		}
 
 		/**
 		 * Internal struct used to help track physical table names to logical table names.
 		 */
 		private class TableDescription implements Serializable {
 			final String logicalName;
 			final Table denormalizedSupertable;
 
 			TableDescription(String logicalName, Table denormalizedSupertable) {
 				this.logicalName = logicalName;
 				this.denormalizedSupertable = denormalizedSupertable;
 			}
 		}
 
 		public String getLogicalTableName(Table table) throws MappingException {
 			return getLogicalTableName( table.getQuotedSchema(), table.getQuotedCatalog(), table.getQuotedName() );
 		}
 
 		private String getLogicalTableName(String schema, String catalog, String physicalName) throws MappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription descriptor = (TableDescription) tableNameBinding.get( key );
 			if (descriptor == null) {
 				throw new MappingException( "Unable to find physical table: " + physicalName);
 			}
 			return descriptor.logicalName;
 		}
 
 		public void addTableBinding(
 				String schema,
 				String catalog,
 				String logicalName,
 				String physicalName,
 				Table denormalizedSuperTable) throws DuplicateMappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription tableDescription = new TableDescription( logicalName, denormalizedSuperTable );
 			TableDescription oldDescriptor = ( TableDescription ) tableNameBinding.put( key, tableDescription );
 			if ( oldDescriptor != null && ! oldDescriptor.logicalName.equals( logicalName ) ) {
 				//TODO possibly relax that
 				throw new DuplicateMappingException(
 						"Same physical table name [" + physicalName + "] references several logical table names: [" +
 								oldDescriptor.logicalName + "], [" + logicalName + ']',
 						"table",
 						physicalName
 				);
 			}
 		}
 
 		private String buildTableNameKey(String schema, String catalog, String finalName) {
 			StringBuilder keyBuilder = new StringBuilder();
 			if (schema != null) keyBuilder.append( schema );
 			keyBuilder.append( ".");
 			if (catalog != null) keyBuilder.append( catalog );
 			keyBuilder.append( ".");
 			keyBuilder.append( finalName );
 			return keyBuilder.toString();
 		}
 
 		/**
 		 * Internal struct used to maintain xref between physical and logical column
 		 * names for a table.  Mainly this is used to ensure that the defined
 		 * {@link NamingStrategy} is not creating duplicate column names.
 		 */
 		private class TableColumnNameBinding implements Serializable {
 			private final String tableName;
 			private Map/*<String, String>*/ logicalToPhysical = new HashMap();
 			private Map/*<String, String>*/ physicalToLogical = new HashMap();
 
 			private TableColumnNameBinding(String tableName) {
 				this.tableName = tableName;
 			}
 
 			public void addBinding(String logicalName, Column physicalColumn) {
 				bindLogicalToPhysical( logicalName, physicalColumn );
 				bindPhysicalToLogical( logicalName, physicalColumn );
 			}
 
 			private void bindLogicalToPhysical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String logicalKey = logicalName.toLowerCase();
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingPhysicalName = ( String ) logicalToPhysical.put( logicalKey, physicalName );
 				if ( existingPhysicalName != null ) {
 					boolean areSamePhysicalColumn = physicalColumn.isQuoted()
 							? existingPhysicalName.equals( physicalName )
 							: existingPhysicalName.equalsIgnoreCase( physicalName );
 					if ( ! areSamePhysicalColumn ) {
 						throw new DuplicateMappingException(
 								" Table [" + tableName + "] contains logical column name [" + logicalName
 										+ "] referenced by multiple physical column names: [" + existingPhysicalName
 										+ "], [" + physicalName + "]",
 								"column-binding",
 								tableName + "." + logicalName
 						);
 					}
 				}
 			}
 
 			private void bindPhysicalToLogical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingLogicalName = ( String ) physicalToLogical.put( physicalName, logicalName );
 				if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 					throw new DuplicateMappingException(
 							" Table [" + tableName + "] contains phyical column name [" + physicalName
 									+ "] represented by different logical column names: [" + existingLogicalName
 									+ "], [" + logicalName + "]",
 							"column-binding",
 							tableName + "." + physicalName
 					);
 				}
 			}
 		}
 
 		public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException {
 			TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( table );
 			if ( binding == null ) {
 				binding = new TableColumnNameBinding( table.getName() );
 				columnNameBindingPerTable.put( table, binding );
 			}
 			binding.addBinding( logicalName, physicalColumn );
 		}
 
 		public String getPhysicalColumnName(String logicalName, Table table) throws MappingException {
 			logicalName = logicalName.toLowerCase();
 			String finalName = null;
 			Table currentTable = table;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					finalName = ( String ) binding.logicalToPhysical.get( logicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getQuotedCatalog(), currentTable.getQuotedName()
 				);
 				TableDescription description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			} while ( finalName == null && currentTable != null );
 
 			if ( finalName == null ) {
 				throw new MappingException(
 						"Unable to find column with logical name " + logicalName + " in table " + table.getName()
 				);
 			}
 			return finalName;
 		}
 
 		public String getLogicalColumnName(String physicalName, Table table) throws MappingException {
 			String logical = null;
 			Table currentTable = table;
 			TableDescription description = null;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					logical = ( String ) binding.physicalToLogical.get( physicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getQuotedCatalog(), currentTable.getQuotedName()
 				);
 				description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			}
 			while ( logical == null && currentTable != null && description != null );
 			if ( logical == null ) {
 				throw new MappingException(
 						"Unable to find logical column name from physical name "
 								+ physicalName + " in table " + table.getName()
 				);
 			}
 			return logical;
 		}
 
 		public void addSecondPass(SecondPass sp) {
 			addSecondPass( sp, false );
 		}
 
 		public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue) {
 			if ( onTopOfTheQueue ) {
 				secondPasses.add( 0, sp );
 			}
 			else {
 				secondPasses.add( sp );
 			}
 		}
 
 		@Override
 		public AttributeConverterDefinition locateAttributeConverter(Class converterClass) {
 			if ( attributeConverterDefinitionsByClass == null ) {
 				return null;
 			}
 			return attributeConverterDefinitionsByClass.get( converterClass );
 		}
 
 		@Override
 		public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
 			if ( attributeConverterDefinitionsByClass == null ) {
 				return Collections.emptyList();
 			}
 			return attributeConverterDefinitionsByClass.values();
 		}
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 			return identifierGeneratorFactory;
 		}
 
 		public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 			mappedSuperClasses.put( type, mappedSuperclass );
 		}
 
 		public MappedSuperclass getMappedSuperclass(Class type) {
 			return mappedSuperClasses.get( type );
 		}
 
 		public ObjectNameNormalizer getObjectNameNormalizer() {
 			return normalizer;
 		}
 
 		public Properties getConfigurationProperties() {
 			return properties;
 		}
 
 		public void addDefaultGenerator(IdGenerator generator) {
 			this.addGenerator( generator );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 
 		public boolean isInSecondPass() {
 			return inSecondPass;
 		}
 
 		public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 		}
 
 		public boolean isSpecjProprietarySyntaxEnabled() {
 			return specjProprietarySyntaxEnabled;
 		}
 
 		public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( mapsIdValue, property );
 		}
 
 		public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 			}
 			map.put( property.getPropertyName(), property );
 		}
 
 		private Boolean useNewGeneratorMappings;
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
 		}
 
 		private Boolean useNationalizedCharacterData;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean useNationalizedCharacterData() {
 			if ( useNationalizedCharacterData == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NATIONALIZED_CHARACTER_DATA );
 				useNationalizedCharacterData = Boolean.valueOf( booleanName );
 			}
 			return useNationalizedCharacterData.booleanValue();
 		}
 
 		private Boolean forceDiscriminatorInSelectsByDefault;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean forceDiscriminatorInSelectsByDefault() {
diff --git a/hibernate-core/src/main/java/org/hibernate/event/spi/AbstractPreDatabaseOperationEvent.java b/hibernate-core/src/main/java/org/hibernate/event/spi/AbstractPreDatabaseOperationEvent.java
index c39afd5dde..685e72c13b 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/spi/AbstractPreDatabaseOperationEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/spi/AbstractPreDatabaseOperationEvent.java
@@ -1,100 +1,115 @@
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
 package org.hibernate.event.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.secure.spi.PermissionCheckEntityInformation;
 
 /**
  * Represents an operation we are about to perform against the database.
  *
  * @author Steve Ebersole
  */
-public abstract class AbstractPreDatabaseOperationEvent extends AbstractEvent {
+public abstract class AbstractPreDatabaseOperationEvent
+		extends AbstractEvent
+		implements PermissionCheckEntityInformation {
+
 	private final Object entity;
 	private final Serializable id;
 	private final EntityPersister persister;
 
 	/**
 	 * Constructs an event containing the pertinent information.
 	 *
 	 * @param source The session from which the event originated.
 	 * @param entity The entity to be invloved in the database operation.
 	 * @param id The entity id to be invloved in the database operation.
 	 * @param persister The entity's persister.
 	 */
 	public AbstractPreDatabaseOperationEvent(
 			EventSource source,
 			Object entity,
 			Serializable id,
 			EntityPersister persister) {
 		super( source );
 		this.entity = entity;
 		this.id = id;
 		this.persister = persister;
 	}
 
 	/**
 	 * Retrieves the entity involved in the database operation.
 	 *
 	 * @return The entity.
 	 */
+	@Override
 	public Object getEntity() {
 		return entity;
 	}
 
 	/**
 	 * The id to be used in the database operation.
 	 *
 	 * @return The id.
 	 */
 	public Serializable getId() {
 		return id;
 	}
 
 	/**
 	 * The persister for the {@link #getEntity entity}.
 	 *
 	 * @return The entity persister.
 	 */
 	public EntityPersister getPersister() {
 		return persister;
 	}
 
 	/**
 	 * Getter for property 'source'.  This is the session from which the event
 	 * originated.
 	 * <p/>
 	 * Some of the pre-* events had previous exposed the event source using
 	 * getSource() because they had not originally extended from
 	 * {@link AbstractEvent}.
 	 *
 	 * @return Value for property 'source'.
 	 * @deprecated Use {@link #getSession} instead
 	 */
 	public EventSource getSource() {
 		return getSession();
 	}
+
+	@Override
+	public String getEntityName() {
+		return persister.getEntityName();
+	}
+
+	@Override
+	public Serializable getIdentifier() {
+		return id;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/spi/PreDeleteEvent.java b/hibernate-core/src/main/java/org/hibernate/event/spi/PreDeleteEvent.java
index 9d4ded751a..7fae5576b9 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/spi/PreDeleteEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/spi/PreDeleteEvent.java
@@ -1,70 +1,75 @@
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
 package org.hibernate.event.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.secure.spi.PermissionCheckEntityInformation;
+
 
 /**
  * Represents a <tt>pre-delete</tt> event, which occurs just prior to
  * performing the deletion of an entity from the database.
  * 
  * @author Gavin King
  * @author Steve Ebersole
  */
-public class PreDeleteEvent extends AbstractPreDatabaseOperationEvent {
+public class PreDeleteEvent
+		extends AbstractPreDatabaseOperationEvent
+		implements PermissionCheckEntityInformation{
+
 	private Object[] deletedState;
 
 	/**
 	 *
 	 * Constructs an event containing the pertinent information.
 	 *
 	 * @param entity The entity to be deleted.
 	 * @param id The id to use in the deletion.
 	 * @param deletedState The entity's state at deletion time.
 	 * @param persister The entity's persister.
 	 * @param source The session from which the event originated.
 	 */
 	public PreDeleteEvent(
 			Object entity,
 			Serializable id,
 			Object[] deletedState,
 			EntityPersister persister,
 			EventSource source) {
 	    super( source, entity, id, persister );
 		this.deletedState = deletedState;
 	}
 
 	/**
 	 * Getter for property 'deletedState'.  This is the entity state at the
 	 * time of deletion (useful for optomistic locking and such).
 	 *
 	 * @return Value for property 'deletedState'.
 	 */
 	public Object[] getDeletedState() {
 		return deletedState;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/spi/PreLoadEvent.java b/hibernate-core/src/main/java/org/hibernate/event/spi/PreLoadEvent.java
index 7d825fee87..e7b2a2e283 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/spi/PreLoadEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/spi/PreLoadEvent.java
@@ -1,82 +1,92 @@
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
 package org.hibernate.event.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.secure.spi.PermissionCheckEntityInformation;
 
 /**
- * Called before injecting property values into a newly 
- * loaded entity instance.
+ * Called before injecting property values into a newly loaded entity instance.
  *
  * @author Gavin King
  */
-public class PreLoadEvent extends AbstractEvent {
+public class PreLoadEvent extends AbstractEvent implements PermissionCheckEntityInformation {
 	private Object entity;
 	private Object[] state;
 	private Serializable id;
 	private EntityPersister persister;
 
 	public PreLoadEvent(EventSource session) {
 		super(session);
 	}
 
+	@Override
 	public Object getEntity() {
 		return entity;
 	}
 	
 	public Serializable getId() {
 		return id;
 	}
 	
 	public EntityPersister getPersister() {
 		return persister;
 	}
 	
 	public Object[] getState() {
 		return state;
 	}
 
 	public PreLoadEvent setEntity(Object entity) {
 		this.entity = entity;
 		return this;
 	}
 	
 	public PreLoadEvent setId(Serializable id) {
 		this.id = id;
 		return this;
 	}
 	
 	public PreLoadEvent setPersister(EntityPersister persister) {
 		this.persister = persister;
 		return this;
 	}
 	
 	public PreLoadEvent setState(Object[] state) {
 		this.state = state;
 		return this;
 	}
-	
+
+	@Override
+	public String getEntityName() {
+		return persister.getEntityName();
+	}
+
+	@Override
+	public Serializable getIdentifier() {
+		return id;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
index b4cd7872d7..f8829a04e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
@@ -1,67 +1,69 @@
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
 package org.hibernate.integrator.internal;
 
 import java.util.LinkedHashSet;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.secure.spi.JaccIntegrator;
 
 /**
  * @author Steve Ebersole
  */
 public class IntegratorServiceImpl implements IntegratorService {
 	private static final Logger LOG = Logger.getLogger( IntegratorServiceImpl.class.getName() );
 
 	private final LinkedHashSet<Integrator> integrators = new LinkedHashSet<Integrator>();
 
 	public IntegratorServiceImpl(LinkedHashSet<Integrator> providedIntegrators, ClassLoaderService classLoaderService) {
 		// register standard integrators.  Envers and JPA, for example, need to be handled by discovery because in
 		// separate project/jars.
 		addIntegrator( new BeanValidationIntegrator() );
+		addIntegrator( new JaccIntegrator() );
 
 		// register provided integrators
 		for ( Integrator integrator : providedIntegrators ) {
 			addIntegrator( integrator );
 		}
 
 		for ( Integrator integrator : classLoaderService.loadJavaServices( Integrator.class ) ) {
 			addIntegrator( integrator );
 		}
 	}
 
 	private void addIntegrator(Integrator integrator) {
 		LOG.debugf( "Adding Integrator [%s].", integrator.getClass().getName() );
 		integrators.add( integrator );
 	}
 
 	@Override
 	public Iterable<Integrator> getIntegrators() {
 		return integrators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/AbstractJaccSecurableEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/AbstractJaccSecurableEventListener.java
new file mode 100644
index 0000000000..38173b73c3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/AbstractJaccSecurableEventListener.java
@@ -0,0 +1,53 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.internal;
+
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
+import org.hibernate.secure.spi.JaccService;
+import org.hibernate.secure.spi.PermissibleAction;
+import org.hibernate.secure.spi.PermissionCheckEntityInformation;
+
+/**
+ * Base class for JACC-securable event listeners
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractJaccSecurableEventListener implements JaccSecurityListener {
+	private JaccService jaccService;
+
+	protected void performSecurityCheck(AbstractPreDatabaseOperationEvent event, PermissibleAction action) {
+		performSecurityCheck( event.getSession(), event, action );
+	}
+
+	protected void performSecurityCheck(
+			SessionImplementor session,
+			PermissionCheckEntityInformation entityInformation,
+			PermissibleAction action) {
+		if ( jaccService == null ) {
+			jaccService = session.getFactory().getServiceRegistry().getService( JaccService.class );
+		}
+		jaccService.checkPermission( entityInformation, action );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/DisabledJaccServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/DisabledJaccServiceImpl.java
new file mode 100644
index 0000000000..29aaf8b61d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/DisabledJaccServiceImpl.java
@@ -0,0 +1,48 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.internal;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.secure.spi.GrantedPermission;
+import org.hibernate.secure.spi.JaccService;
+import org.hibernate.secure.spi.PermissibleAction;
+import org.hibernate.secure.spi.PermissionCheckEntityInformation;
+
+/**
+ * @author Steve Ebersole
+ */
+public class DisabledJaccServiceImpl implements JaccService {
+	private static final Logger log = Logger.getLogger( DisabledJaccServiceImpl.class );
+
+	@Override
+	public void addPermission(GrantedPermission permissionDeclaration) {
+		log.debug( "Ignoring call to addPermission on disabled JACC service" );
+	}
+
+	@Override
+	public void checkPermission(PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
+		log.debug( "Ignoring call to checkPermission on disabled JACC service" );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/HibernatePermission.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/HibernatePermission.java
deleted file mode 100755
index 071754d679..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/HibernatePermission.java
+++ /dev/null
@@ -1,70 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.secure.internal;
-
-import java.security.Permission;
-
-/**
- * @author Gavin King
- */
-public class HibernatePermission extends Permission {
-	public static final String INSERT = "insert";
-	public static final String UPDATE = "update";
-	public static final String DELETE = "delete";
-	public static final String READ = "read";
-	public static final String ANY = "*";
-	
-	private final String actions;
-
-	public HibernatePermission(String entityName, String actions) {
-		super(entityName);
-		this.actions = actions;
-	}
-
-	public boolean implies(Permission permission) {
-		//TODO!
-		return ( "*".equals( getName() ) || getName().equals( permission.getName() ) ) &&
-			( "*".equals(actions) || actions.indexOf( permission.getActions() ) >= 0 );
-	}
-
-	public boolean equals(Object obj) {
-		if ( !(obj instanceof HibernatePermission) ) return false;
-		HibernatePermission permission = (HibernatePermission) obj;
-		return permission.getName().equals( getName() ) && 
-			permission.getActions().equals(actions);
-	}
-
-	public int hashCode() {
-		return getName().hashCode() * 37 + actions.hashCode();
-	}
-
-	public String getActions() {
-		return actions;
-	}
-	
-	public String toString() {
-		return "HibernatePermission(" + getName() + ':' + actions + ')';
-	}
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCConfiguration.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCConfiguration.java
deleted file mode 100755
index a6e2a31a83..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCConfiguration.java
+++ /dev/null
@@ -1,88 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.secure.internal;
-
-import java.util.StringTokenizer;
-import javax.security.jacc.EJBMethodPermission;
-import javax.security.jacc.PolicyConfiguration;
-import javax.security.jacc.PolicyConfigurationFactory;
-import javax.security.jacc.PolicyContextException;
-
-import org.jboss.logging.Logger;
-
-import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
-
-/**
- * Adds Hibernate permissions to roles via JACC
- *
- * @author Gavin King
- */
-public class JACCConfiguration {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JACCConfiguration.class.getName());
-
-	private final PolicyConfiguration policyConfiguration;
-
-	public JACCConfiguration(String contextId) throws HibernateException {
-		try {
-			policyConfiguration = PolicyConfigurationFactory
-					.getPolicyConfigurationFactory()
-					.getPolicyConfiguration( contextId, false );
-		}
-		catch (ClassNotFoundException cnfe) {
-			throw new HibernateException( "JACC provider class not found", cnfe );
-		}
-		catch (PolicyContextException pce) {
-			throw new HibernateException( "policy context exception occurred", pce );
-		}
-	}
-
-	public void addPermission(String role, String entityName, String action) {
-
-		if ( action.equals( "*" ) ) {
-			action = "insert,read,update,delete";
-		}
-
-		StringTokenizer tok = new StringTokenizer( action, "," );
-
-		while ( tok.hasMoreTokens() ) {
-			String methodName = tok.nextToken().trim();
-			EJBMethodPermission permission = new EJBMethodPermission(
-					entityName,
-					methodName,
-					null, // interfaces
-					null // arguments
-				);
-
-            LOG.debugf("Adding permission to role %s: %s", role, permission);
-			try {
-				policyConfiguration.addToRole( role, permission );
-			}
-			catch (PolicyContextException pce) {
-				throw new HibernateException( "policy context exception occurred", pce );
-			}
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPermissions.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPermissions.java
deleted file mode 100644
index 31eb980f47..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPermissions.java
+++ /dev/null
@@ -1,144 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.secure.internal;
-
-import java.lang.reflect.UndeclaredThrowableException;
-import java.security.AccessController;
-import java.security.CodeSource;
-import java.security.Policy;
-import java.security.Principal;
-import java.security.PrivilegedAction;
-import java.security.PrivilegedActionException;
-import java.security.PrivilegedExceptionAction;
-import java.security.ProtectionDomain;
-import java.util.Set;
-import javax.security.auth.Subject;
-import javax.security.jacc.EJBMethodPermission;
-import javax.security.jacc.PolicyContext;
-import javax.security.jacc.PolicyContextException;
-
-/**
- * Copied from JBoss org.jboss.ejb3.security.JaccHelper and org.jboss.ejb3.security.SecurityActions
- *
- * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
- */
-public class JACCPermissions {
-
-	public static void checkPermission(Class clazz, String contextID, EJBMethodPermission methodPerm)
-			throws SecurityException {
-		CodeSource ejbCS = clazz.getProtectionDomain().getCodeSource();
-
-		try {
-			setContextID( contextID );
-
-			Policy policy = Policy.getPolicy();
-			// Get the caller
-			Subject caller = getContextSubject();
-
-			Principal[] principals = null;
-			if ( caller != null ) {
-				// Get the caller principals
-				Set principalsSet = caller.getPrincipals();
-				principals = new Principal[ principalsSet.size() ];
-				principalsSet.toArray( principals );
-			}
-
-			ProtectionDomain pd = new ProtectionDomain( ejbCS, null, null, principals );
-			if ( policy.implies( pd, methodPerm ) == false ) {
-				String msg = "Denied: " + methodPerm + ", caller=" + caller;
-				SecurityException e = new SecurityException( msg );
-				throw e;
-			}
-		}
-		catch (PolicyContextException e) {
-			throw new RuntimeException( e );
-		}
-	}
-
-	interface PolicyContextActions {
-		/**
-		 * The JACC PolicyContext key for the current Subject
-		 */
-		static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
-		PolicyContextActions PRIVILEGED = new PolicyContextActions() {
-			private final PrivilegedExceptionAction exAction = new PrivilegedExceptionAction() {
-				public Object run() throws Exception {
-					return PolicyContext.getContext( SUBJECT_CONTEXT_KEY );
-				}
-			};
-
-			public Subject getContextSubject() throws PolicyContextException {
-				try {
-					return (Subject) AccessController.doPrivileged( exAction );
-				}
-				catch (PrivilegedActionException e) {
-					Exception ex = e.getException();
-					if ( ex instanceof PolicyContextException ) {
-						throw (PolicyContextException) ex;
-					}
-					else {
-						throw new UndeclaredThrowableException( ex );
-					}
-				}
-			}
-		};
-
-		PolicyContextActions NON_PRIVILEGED = new PolicyContextActions() {
-			public Subject getContextSubject() throws PolicyContextException {
-				return (Subject) PolicyContext.getContext( SUBJECT_CONTEXT_KEY );
-			}
-		};
-
-		Subject getContextSubject() throws PolicyContextException;
-	}
-
-	static Subject getContextSubject() throws PolicyContextException {
-		if ( System.getSecurityManager() == null ) {
-			return PolicyContextActions.NON_PRIVILEGED.getContextSubject();
-		}
-		else {
-			return PolicyContextActions.PRIVILEGED.getContextSubject();
-		}
-	}
-
-	private static class SetContextID implements PrivilegedAction {
-		String contextID;
-
-		SetContextID(String contextID) {
-			this.contextID = contextID;
-		}
-
-		public Object run() {
-			String previousID = PolicyContext.getContextID();
-			PolicyContext.setContextID( contextID );
-			return previousID;
-		}
-	}
-
-	static String setContextID(String contextID) {
-		PrivilegedAction action = new SetContextID( contextID );
-		String previousID = (String) AccessController.doPrivileged( action );
-		return previousID;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreDeleteEventListener.java
similarity index 72%
rename from hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreDeleteEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreDeleteEventListener.java
index 955842f8d9..5ad75293cd 100755
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreDeleteEventListener.java
@@ -1,54 +1,45 @@
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
 package org.hibernate.secure.internal;
 
-import javax.security.jacc.EJBMethodPermission;
-
 import org.hibernate.event.spi.PreDeleteEvent;
 import org.hibernate.event.spi.PreDeleteEventListener;
+import org.hibernate.secure.spi.PermissibleAction;
 
 /**
  * Check security before any deletion
  *
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
+ * @author Steve Ebersole
  */
-public class JACCPreDeleteEventListener implements PreDeleteEventListener, JACCSecurityListener {
-	private final String contextId;
-
-	public JACCPreDeleteEventListener(String contextId) {
-		this.contextId = contextId;
+public class JaccPreDeleteEventListener extends AbstractJaccSecurableEventListener implements PreDeleteEventListener {
+	public JaccPreDeleteEventListener() {
 	}
 
 	public boolean onPreDelete(PreDeleteEvent event) {
-		final EJBMethodPermission deletePermission = new EJBMethodPermission(
-				event.getPersister().getEntityName(),
-				HibernatePermission.DELETE,
-				null,
-				null
-		);
-		JACCPermissions.checkPermission( event.getEntity().getClass(), contextId, deletePermission );
+		performSecurityCheck( event, PermissibleAction.DELETE );
 		return false;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreInsertEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreInsertEventListener.java
similarity index 72%
rename from hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreInsertEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreInsertEventListener.java
index b05a73cfd7..6dd0babb2f 100755
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreInsertEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreInsertEventListener.java
@@ -1,53 +1,44 @@
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
 package org.hibernate.secure.internal;
 
-import javax.security.jacc.EJBMethodPermission;
-
 import org.hibernate.event.spi.PreInsertEvent;
 import org.hibernate.event.spi.PreInsertEventListener;
+import org.hibernate.secure.spi.PermissibleAction;
 
 /**
  * Check security before an insertion
  *
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
+ * @author Steve Ebersole
  */
-public class JACCPreInsertEventListener implements PreInsertEventListener, JACCSecurityListener {
-	private final String contextId;
-
-	public JACCPreInsertEventListener(String contextId) {
-		this.contextId = contextId;
+public class JaccPreInsertEventListener extends AbstractJaccSecurableEventListener implements PreInsertEventListener {
+	public JaccPreInsertEventListener() {
 	}
 
 	public boolean onPreInsert(PreInsertEvent event) {
-		final EJBMethodPermission insertPermission = new EJBMethodPermission(
-				event.getPersister().getEntityName(),
-				HibernatePermission.INSERT,
-				null,
-				null
-		);
-		JACCPermissions.checkPermission( event.getEntity().getClass(), contextId, insertPermission );
+		performSecurityCheck( event, PermissibleAction.INSERT );
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreLoadEventListener.java
similarity index 72%
rename from hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreLoadEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreLoadEventListener.java
index 51423314a6..492ce52680 100755
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreLoadEventListener.java
@@ -1,52 +1,43 @@
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
 package org.hibernate.secure.internal;
 
-import javax.security.jacc.EJBMethodPermission;
-
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
+import org.hibernate.secure.spi.PermissibleAction;
 
 /**
  * Check security before any load
  *
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
+ * @author Steve Ebersole
  */
-public class JACCPreLoadEventListener implements PreLoadEventListener, JACCSecurityListener {
-	private final String contextId;
-
-	public JACCPreLoadEventListener(String contextId) {
-		this.contextId = contextId;
+public class JaccPreLoadEventListener extends AbstractJaccSecurableEventListener implements PreLoadEventListener {
+	public JaccPreLoadEventListener() {
 	}
 
 	public void onPreLoad(PreLoadEvent event) {
-		final EJBMethodPermission loadPermission = new EJBMethodPermission(
-				event.getPersister().getEntityName(),
-				HibernatePermission.READ,
-				null,
-				null
-		);
-		JACCPermissions.checkPermission( event.getEntity().getClass(), contextId, loadPermission );
+		performSecurityCheck( event.getSession(), event, PermissibleAction.READ );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreUpdateEventListener.java
similarity index 72%
rename from hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreUpdateEventListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreUpdateEventListener.java
index f7e9d73303..4f481266ed 100755
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCPreUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccPreUpdateEventListener.java
@@ -1,53 +1,44 @@
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
 package org.hibernate.secure.internal;
 
-import javax.security.jacc.EJBMethodPermission;
-
 import org.hibernate.event.spi.PreUpdateEvent;
 import org.hibernate.event.spi.PreUpdateEventListener;
+import org.hibernate.secure.spi.PermissibleAction;
 
 /**
  * Check security before any update
  *
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
+ * @author Steve Ebersole
  */
-public class JACCPreUpdateEventListener implements PreUpdateEventListener, JACCSecurityListener {
-	private final String contextId;
-
-	public JACCPreUpdateEventListener(String contextId) {
-		this.contextId = contextId;
+public class JaccPreUpdateEventListener extends AbstractJaccSecurableEventListener implements PreUpdateEventListener {
+	public JaccPreUpdateEventListener() {
 	}
 
 	public boolean onPreUpdate(PreUpdateEvent event) {
-		final EJBMethodPermission updatePermission = new EJBMethodPermission(
-				event.getPersister().getEntityName(),
-				HibernatePermission.UPDATE,
-				null,
-				null
-		);
-		JACCPermissions.checkPermission( event.getEntity().getClass(), contextId, updatePermission );
+		performSecurityCheck( event, PermissibleAction.UPDATE );
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCSecurityListener.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccSecurityListener.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/secure/internal/JACCSecurityListener.java
rename to hibernate-core/src/main/java/org/hibernate/secure/internal/JaccSecurityListener.java
index 90b9871790..2dfe030f12 100644
--- a/hibernate-core/src/main/java/org/hibernate/secure/internal/JACCSecurityListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/JaccSecurityListener.java
@@ -1,32 +1,33 @@
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
 package org.hibernate.secure.internal;
 
 /**
- * Marker interface for JACC event listeners
+ * Marker interface for JACC event listeners.  Used in event listener duplication strategy checks; see
+ * {@link org.hibernate.secure.spi.JaccIntegrator} for details.
  * 
  * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
  */
-public interface JACCSecurityListener {
+public interface JaccSecurityListener {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/internal/StandardJaccServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/secure/internal/StandardJaccServiceImpl.java
new file mode 100644
index 0000000000..25e173d581
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/internal/StandardJaccServiceImpl.java
@@ -0,0 +1,221 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.internal;
+
+import javax.security.auth.Subject;
+import javax.security.jacc.EJBMethodPermission;
+import javax.security.jacc.PolicyConfiguration;
+import javax.security.jacc.PolicyConfigurationFactory;
+import javax.security.jacc.PolicyContext;
+import javax.security.jacc.PolicyContextException;
+import java.security.AccessController;
+import java.security.CodeSource;
+import java.security.Policy;
+import java.security.Principal;
+import java.security.PrivilegedAction;
+import java.security.ProtectionDomain;
+import java.util.Map;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.secure.spi.GrantedPermission;
+import org.hibernate.secure.spi.IntegrationException;
+import org.hibernate.secure.spi.JaccService;
+import org.hibernate.secure.spi.PermissibleAction;
+import org.hibernate.secure.spi.PermissionCheckEntityInformation;
+import org.hibernate.service.spi.Configurable;
+
+/**
+ * @author Steve Ebersole
+ */
+public class StandardJaccServiceImpl implements JaccService, Configurable {
+	private static final Logger log = Logger.getLogger( StandardJaccServiceImpl.class );
+
+	private String contextId;
+	private PolicyConfiguration policyConfiguration;
+
+	@Override
+	public void configure(Map configurationValues) {
+		this.contextId = (String) configurationValues.get( AvailableSettings.JACC_CONTEXT_ID );
+	}
+
+	@Override
+	public void addPermission(GrantedPermission permissionDeclaration) {
+		// todo : do we need to wrap these PolicyConfiguration calls in privileged actions like we do during permission checks?
+
+		if ( policyConfiguration == null ) {
+			policyConfiguration = locatePolicyConfiguration( contextId );
+		}
+
+		for ( String grantedAction : permissionDeclaration.getPermissibleAction().getImpliedActions() ) {
+			final EJBMethodPermission permission = new EJBMethodPermission(
+					permissionDeclaration.getEntityName(),
+					grantedAction,
+					null, // interfaces
+					null // arguments
+			);
+
+			log.debugf( "Adding permission [%s] to role [%s]", grantedAction, permissionDeclaration.getRole() );
+			try {
+				policyConfiguration.addToRole( permissionDeclaration.getRole(), permission );
+			}
+			catch (PolicyContextException pce) {
+				throw new HibernateException( "policy context exception occurred", pce );
+			}
+		}
+	}
+
+	private PolicyConfiguration locatePolicyConfiguration(String contextId) {
+		try {
+			return PolicyConfigurationFactory
+					.getPolicyConfigurationFactory()
+					.getPolicyConfiguration( contextId, false );
+		}
+		catch (Exception e) {
+			throw new IntegrationException( "Unable to access JACC PolicyConfiguration" );
+		}
+	}
+
+	@Override
+	public void checkPermission(PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
+		if ( action == PermissibleAction.ANY ) {
+			throw new HibernateException( "ANY action (*) is not legal for permission check, only for configuration" );
+		}
+
+		final String originalContextId = AccessController.doPrivileged( new ContextIdSetAction( contextId ) );
+		try {
+			doPermissionCheckInContext( entityInformation, action );
+		}
+		finally {
+			AccessController.doPrivileged( new ContextIdSetAction( originalContextId ) );
+		}
+	}
+
+	private static class ContextIdSetAction implements PrivilegedAction<String> {
+		private final String contextId;
+
+		private ContextIdSetAction(String contextId) {
+			this.contextId = contextId;
+		}
+
+		@Override
+		public String run() {
+			String previousID = PolicyContext.getContextID();
+			PolicyContext.setContextID( contextId );
+			return previousID;
+		}
+	}
+
+	private void doPermissionCheckInContext(PermissionCheckEntityInformation entityInformation, PermissibleAction action) {
+		final Policy policy = Policy.getPolicy();
+		final Principal[] principals = getCallerPrincipals();
+
+		final CodeSource codeSource = entityInformation.getEntity().getClass().getProtectionDomain().getCodeSource();
+		final ProtectionDomain pd = new ProtectionDomain( codeSource, null, null, principals );
+
+		// the action is known as 'method name' in JACC
+		final EJBMethodPermission jaccPermission = new EJBMethodPermission(
+				entityInformation.getEntityName(),
+				action.getImpliedActions()[0],
+				null,
+				null
+		);
+
+		if ( ! policy.implies( pd, jaccPermission) ) {
+			throw new SecurityException(
+					String.format(
+							"JACC denied permission to [%s.%s] for [%s]",
+							entityInformation.getEntityName(),
+							action.getImpliedActions()[0],
+							join( principals )
+					)
+			);
+		}
+	}
+
+	private String join(Principal[] principals) {
+		String separator = "";
+		final StringBuilder buffer = new StringBuilder();
+		for ( Principal principal : principals ) {
+			buffer.append( separator ).append( principal.getName() );
+			separator = ", ";
+		}
+		return buffer.toString();
+	}
+
+	protected Principal[] getCallerPrincipals() {
+		final Subject caller = getContextSubjectAccess().getContextSubject();
+		if ( caller == null ) {
+			return new Principal[0];
+		}
+
+		final Set<Principal> principalsSet = caller.getPrincipals();
+		return principalsSet.toArray( new Principal[ principalsSet.size()] );
+	}
+
+	private ContextSubjectAccess getContextSubjectAccess() {
+		return ( System.getSecurityManager() == null )
+				? NonPrivilegedContextSubjectAccess.INSTANCE
+				: PrivilegedContextSubjectAccess.INSTANCE;
+	}
+
+	protected static interface ContextSubjectAccess {
+		public static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
+
+		public Subject getContextSubject();
+	}
+
+	protected static class PrivilegedContextSubjectAccess implements ContextSubjectAccess {
+		public static final PrivilegedContextSubjectAccess INSTANCE = new PrivilegedContextSubjectAccess();
+
+		private final PrivilegedAction<Subject> privilegedAction = new PrivilegedAction<Subject>() {
+			public Subject run() {
+				return NonPrivilegedContextSubjectAccess.INSTANCE.getContextSubject();
+			}
+		};
+
+		@Override
+		public Subject getContextSubject() {
+			return AccessController.doPrivileged( privilegedAction );
+		}
+	}
+
+	protected static class NonPrivilegedContextSubjectAccess implements ContextSubjectAccess {
+		public static final NonPrivilegedContextSubjectAccess INSTANCE = new NonPrivilegedContextSubjectAccess();
+
+		@Override
+		public Subject getContextSubject() {
+			try {
+				return (Subject) PolicyContext.getContext( SUBJECT_CONTEXT_KEY );
+			}
+			catch (PolicyContextException e) {
+				throw new HibernateException( "Unable to access JACC PolicyContext in order to locate calling Subject", e );
+			}
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/package-info.java b/hibernate-core/src/main/java/org/hibernate/secure/package-info.java
new file mode 100644
index 0000000000..31a9b0b7ff
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/package-info.java
@@ -0,0 +1,5 @@
+package org.hibernate.secure;
+
+/**
+ * Package defining support for declarative security of CRUD operations via JACC.
+ */
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/package.html b/hibernate-core/src/main/java/org/hibernate/secure/package.html
deleted file mode 100755
index 24eae8cfdf..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/secure/package.html
+++ /dev/null
@@ -1,33 +0,0 @@
-<!--
-  ~ Hibernate, Relational Persistence for Idiomatic Java
-  ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
-  ~ indicated by the @author tags or express copyright attribution
-  ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
-  ~
-  ~ This copyrighted material is made available to anyone wishing to use, modify,
-  ~ copy, or redistribute it subject to the terms and conditions of the GNU
-  ~ Lesser General Public License, as published by the Free Software Foundation.
-  ~
-  ~ This program is distributed in the hope that it will be useful,
-  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
-  ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
-  ~ for more details.
-  ~
-  ~ You should have received a copy of the GNU Lesser General Public License
-  ~ along with this distribution; if not, write to:
-  ~ Free Software Foundation, Inc.
-  ~ 51 Franklin Street, Fifth Floor
-  ~ Boston, MA  02110-1301  USA
-  ~
-  -->
-
-<html>
-<head></head>
-<body>
-<p>
-	Declarative security for CRUD operations on entities.
-</p>
-</body>
-</html>
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/GrantedPermission.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/GrantedPermission.java
new file mode 100644
index 0000000000..6d4ca397cc
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/GrantedPermission.java
@@ -0,0 +1,53 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+/**
+ * Describes a Hibernate (persistence) permission.
+ *
+ * @author Steve Ebersole
+ */
+public class GrantedPermission {
+	private final String role;
+	private final String entityName;
+	private final PermissibleAction action;
+
+	public GrantedPermission(String role, String entityName, String action) {
+		this.role = role;
+		this.entityName = entityName;
+		this.action = PermissibleAction.interpret( action );
+	}
+
+	public String getRole() {
+		return role;
+	}
+
+	public String getEntityName() {
+		return entityName;
+	}
+
+	public PermissibleAction getPermissibleAction() {
+		return action;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/IntegrationException.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/IntegrationException.java
new file mode 100644
index 0000000000..78762bc960
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/IntegrationException.java
@@ -0,0 +1,39 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+import org.hibernate.HibernateException;
+
+/**
+ * @author Steve Ebersole
+ */
+public class IntegrationException extends HibernateException {
+	public IntegrationException(String message) {
+		super( message );
+	}
+
+	public IntegrationException(String message, Throwable root) {
+		super( message, root );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccIntegrator.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccIntegrator.java
new file mode 100644
index 0000000000..44095920d4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccIntegrator.java
@@ -0,0 +1,132 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+import java.util.Map;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.event.service.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.event.spi.EventType;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.integrator.spi.ServiceContributingIntegrator;
+import org.hibernate.metamodel.source.MetadataImplementor;
+import org.hibernate.secure.internal.DisabledJaccServiceImpl;
+import org.hibernate.secure.internal.JaccPreDeleteEventListener;
+import org.hibernate.secure.internal.JaccPreInsertEventListener;
+import org.hibernate.secure.internal.JaccPreLoadEventListener;
+import org.hibernate.secure.internal.JaccPreUpdateEventListener;
+import org.hibernate.secure.internal.JaccSecurityListener;
+import org.hibernate.secure.internal.StandardJaccServiceImpl;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
+/**
+ * Integrator for setting up JACC integration
+ *
+ * @author Steve Ebersole
+ */
+public class JaccIntegrator implements ServiceContributingIntegrator {
+	private static final Logger log = Logger.getLogger( JaccIntegrator.class );
+
+	private static final DuplicationStrategy DUPLICATION_STRATEGY = new DuplicationStrategy() {
+		@Override
+		public boolean areMatch(Object listener, Object original) {
+			return listener.getClass().equals( original.getClass() ) &&
+					JaccSecurityListener.class.isInstance( original );
+		}
+
+		@Override
+		public Action getAction() {
+			return Action.KEEP_ORIGINAL;
+		}
+	};
+
+	@Override
+	public void prepareServices(StandardServiceRegistryBuilder serviceRegistryBuilder) {
+		boolean isSecurityEnabled = serviceRegistryBuilder.getSettings().containsKey( AvailableSettings.JACC_ENABLED );
+		final JaccService jaccService = isSecurityEnabled ? new StandardJaccServiceImpl() : new DisabledJaccServiceImpl();
+		serviceRegistryBuilder.addService( JaccService.class, jaccService );
+	}
+
+	@Override
+	public void integrate(
+			Configuration configuration,
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		doIntegration( configuration.getProperties(), configuration.getJaccPermissionDeclarations(), serviceRegistry );
+	}
+
+	private void doIntegration(
+			Map properties,
+			JaccPermissionDeclarations permissionDeclarations,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		boolean isSecurityEnabled = properties.containsKey( AvailableSettings.JACC_ENABLED );
+		if ( ! isSecurityEnabled ) {
+			log.debug( "Skipping JACC integration as it was not enabled" );
+			return;
+		}
+
+		final String contextId = (String) properties.get( AvailableSettings.JACC_CONTEXT_ID );
+		if ( contextId == null ) {
+			throw new IntegrationException( "JACC context id must be specified" );
+		}
+
+		final JaccService jaccService = serviceRegistry.getService( JaccService.class );
+		if ( jaccService == null ) {
+			throw new IntegrationException( "JaccService was not set up" );
+		}
+
+		if ( permissionDeclarations != null ) {
+			for ( GrantedPermission declaration : permissionDeclarations.getPermissionDeclarations() ) {
+				jaccService.addPermission( declaration );
+			}
+		}
+
+		final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		eventListenerRegistry.addDuplicationStrategy( DUPLICATION_STRATEGY );
+
+		eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JaccPreDeleteEventListener() );
+		eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JaccPreInsertEventListener() );
+		eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JaccPreUpdateEventListener() );
+		eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JaccPreLoadEventListener() );
+	}
+
+	@Override
+	public void integrate(
+			MetadataImplementor metadata,
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		doIntegration( sessionFactory.getProperties(), null, serviceRegistry );
+	}
+
+	@Override
+	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
+		// nothing to do
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccPermissionDeclarations.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccPermissionDeclarations.java
new file mode 100644
index 0000000000..571cba36a3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccPermissionDeclarations.java
@@ -0,0 +1,50 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+import java.util.ArrayList;
+import java.util.List;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JaccPermissionDeclarations {
+	private final String contextId;
+	private List<GrantedPermission> permissionDeclarations;
+
+	public JaccPermissionDeclarations(String contextId) {
+		this.contextId = contextId;
+	}
+
+	public void addPermissionDeclaration(GrantedPermission permissionDeclaration) {
+		if ( permissionDeclarations == null ) {
+			permissionDeclarations = new ArrayList<GrantedPermission>();
+		}
+		permissionDeclarations.add( permissionDeclaration );
+	}
+
+	public Iterable<GrantedPermission> getPermissionDeclarations() {
+		return permissionDeclarations;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccService.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccService.java
new file mode 100644
index 0000000000..ffd15dbd01
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/JaccService.java
@@ -0,0 +1,36 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+import org.hibernate.service.Service;
+
+/**
+ * Service describing Hibernate integration with JACC for security service.
+ *
+ * @author Steve Ebersole
+ */
+public interface JaccService extends Service {
+	public void addPermission(GrantedPermission permissionDeclaration);
+	public void checkPermission(PermissionCheckEntityInformation entityInformation, PermissibleAction action);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/PermissibleAction.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/PermissibleAction.java
new file mode 100644
index 0000000000..473ed503b5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/PermissibleAction.java
@@ -0,0 +1,80 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public enum PermissibleAction {
+	INSERT( "insert" ),
+	UPDATE( "update" ),
+	DELETE( "delete" ),
+	READ( "read" ),
+	ANY( "*" ) {
+		@Override
+		public String[] getImpliedActions() {
+			return new String[] { INSERT.externalName, UPDATE.externalName, DELETE.externalName, READ.externalName };
+		}
+	};
+
+	private final String externalName;
+	private final String[] impliedActions;
+
+	private PermissibleAction(String externalName) {
+		this.externalName = externalName;
+		this.impliedActions = buildImpliedActions( externalName );
+	}
+
+	private String[] buildImpliedActions(String externalName) {
+		return new String[] { externalName };
+	}
+
+	public String getExternalName() {
+		return externalName;
+	}
+
+	public String[] getImpliedActions() {
+		return impliedActions;
+	}
+
+	public static PermissibleAction interpret(String action) {
+		if ( INSERT.externalName.equals( action ) ) {
+			return INSERT;
+		}
+		else if ( UPDATE.externalName.equals( action ) ) {
+			return UPDATE;
+		}
+		else if ( DELETE.externalName.equals( action ) ) {
+			return DELETE;
+		}
+		else if ( READ.externalName.equals( action ) ) {
+			return READ;
+		}
+		else if ( ANY.externalName.equals( action ) ) {
+			return ANY;
+		}
+
+		throw new IllegalArgumentException( "Unrecognized action : " + action );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/secure/spi/PermissionCheckEntityInformation.java b/hibernate-core/src/main/java/org/hibernate/secure/spi/PermissionCheckEntityInformation.java
new file mode 100644
index 0000000000..e1b544b65e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/secure/spi/PermissionCheckEntityInformation.java
@@ -0,0 +1,35 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.secure.spi;
+
+import java.io.Serializable;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface PermissionCheckEntityInformation {
+	public Object getEntity();
+	public String getEntityName();
+	public Serializable getIdentifier();
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
index a291438e39..66051b1ea3 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
@@ -1,517 +1,517 @@
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
 package org.hibernate.jpa;
 
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Defines the available HEM settings, both JPA-defined as well as Hibernate-specific
  * <p/>
  * NOTE : Does *not* include {@link org.hibernate.cfg.Environment} values.
  *
  * @author Steve Ebersole
  */
 public interface AvailableSettings {
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// JPA defined settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * THe name of the {@link javax.persistence.spi.PersistenceProvider} implementor
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.4
 	 */
 	public static final String PROVIDER = "javax.persistence.provider";
 
 	/**
 	 * The type of transactions supported by the entity managers.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.2
 	 */
 	public static final String TRANSACTION_TYPE = "javax.persistence.transactionType";
 
 	/**
 	 * The JNDI name of a JTA {@link javax.sql.DataSource}.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.5
 	 */
 	public static final String JTA_DATASOURCE = "javax.persistence.jtaDataSource";
 
 	/**
 	 * The JNDI name of a non-JTA {@link javax.sql.DataSource}.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.5
 	 */
 	public static final String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";
 
 	/**
 	 * The name of a JDBC driver to use to connect to the database.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_URL}, {@link #JDBC_USER} and {@link #JDBC_PASSWORD}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See section 8.2.1.9
 	 */
 	public static final String JDBC_DRIVER = "javax.persistence.jdbc.driver";
 
 	/**
 	 * The JDBC connection url to use to connect to the database.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_USER} and {@link #JDBC_PASSWORD}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See section 8.2.1.9
 	 */
 	public static final String JDBC_URL = "javax.persistence.jdbc.url";
 
 	/**
 	 * The JDBC connection user name.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_URL} and {@link #JDBC_PASSWORD}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See section 8.2.1.9
 	 */
 	public static final String JDBC_USER = "javax.persistence.jdbc.user";
 
 	/**
 	 * The JDBC connection password.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_URL} and {@link #JDBC_USER}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	public static final String JDBC_PASSWORD = "javax.persistence.jdbc.password";
 
 	/**
 	 * Used to indicate whether second-level (what JPA terms shared cache) caching is
 	 * enabled as per the rules defined in JPA 2 section 3.1.7.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.7
 	 * @see javax.persistence.SharedCacheMode
 	 */
 	public static final String SHARED_CACHE_MODE = "javax.persistence.sharedCache.mode";
 
 	/**
 	 * NOTE : Not a valid EMF property...
 	 * <p/>
 	 * Used to indicate if the provider should attempt to retrieve requested data
 	 * in the shared cache.
 	 *
 	 * @see javax.persistence.CacheRetrieveMode
 	 */
 	public static final String SHARED_CACHE_RETRIEVE_MODE ="javax.persistence.cache.retrieveMode";
 
 	/**
 	 * NOTE : Not a valid EMF property...
 	 * <p/>
 	 * Used to indicate if the provider should attempt to store data loaded from the database
 	 * in the shared cache.
 	 *
 	 * @see javax.persistence.CacheStoreMode
 	 */
 	public static final String SHARED_CACHE_STORE_MODE ="javax.persistence.cache.storeMode";
 
 	/**
 	 * Used to indicate what form of automatic validation is in effect as per rules defined
 	 * in JPA 2 section 3.6.1.1
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.8
 	 * @see javax.persistence.ValidationMode
 	 */
 	public static final String VALIDATION_MODE = "javax.persistence.validation.mode";
 
 	/**
 	 * Used to pass along any discovered validator factory.
 	 */
 	public static final String VALIDATION_FACTORY = "javax.persistence.validation.factory";
 
 	/**
 	 * Used to request (hint) a pessimistic lock scope.
 	 * <p/>
 	 * See JPA 2 sections 8.2.1.9 and 3.4.4.3
 	 */
 	public static final String LOCK_SCOPE = "javax.persistence.lock.scope";
 
 	/**
 	 * Used to request (hint) a pessimistic lock timeout (in milliseconds).
 	 * <p/>
 	 * See JPA 2 sections 8.2.1.9 and 3.4.4.3
 	 */
 	public static final String LOCK_TIMEOUT = "javax.persistence.lock.timeout";
 
 	/**
 	 * Used to coordinate with bean validators
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	public static final String PERSIST_VALIDATION_GROUP = "javax.persistence.validation.group.pre-persist";
 
 	/**
 	 * Used to coordinate with bean validators
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	public static final String UPDATE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-update";
 
 	/**
 	 * Used to coordinate with bean validators
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	public static final String REMOVE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-remove";
 
 	/**
 	 * Used to pass along the CDI BeanManager, if any, to be used.
 	 */
 	public static final String CDI_BEAN_MANAGER = "javax.persistence.bean.manager";
 
 	/**
 	 * Specifies whether schema generation commands for schema creation are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SchemaGenSource} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #SCHEMA_GEN_CREATE_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SchemaGenSource
 	 */
 	public static final String SCHEMA_GEN_CREATE_SOURCE = "javax.persistence.schema-generation.create-source ";
 
 	/**
 	 * Specifies whether schema generation commands for schema dropping are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SchemaGenSource} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #SCHEMA_GEN_DROP_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SchemaGenSource
 	 */
 	public static final String SCHEMA_GEN_DROP_SOURCE = "javax.persistence.schema-generation.drop-source ";
 
 	/**
 	 * Specifies the CREATE script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 *
 	 * @see #SCHEMA_GEN_CREATE_SOURCE
 	 * @see #SCHEMA_GEN_DROP_SCRIPT_SOURCE
 	 */
 	public static final String SCHEMA_GEN_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source ";
 
 	/**
 	 * Specifies the DROP script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 *
 	 * @see #SCHEMA_GEN_DROP_SOURCE
 	 * @see #SCHEMA_GEN_CREATE_SCRIPT_SOURCE
 	 */
 	public static final String SCHEMA_GEN_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source ";
 
 	/**
 	 * Specifies the type of schema generation action to be taken by the persistence provider in regards to sending
 	 * commands directly to the database via JDBC.  See {@link SchemaGenAction} for the set of possible values.
 	 * <p/>
 	 * If no value is specified, the default is "none".
 	 *
 	 * @see SchemaGenAction
 	 */
 	public static final String SCHEMA_GEN_DATABASE_ACTION = "javax.persistence.schema-generation.database.action";
 
 	/**
 	 * Specifies the type of schema generation action to be taken by the persistence provider in regards to writing
 	 * commands to DDL script files.  See {@link SchemaGenAction} for the set of possible values.
 	 * <p/>
 	 * If no value is specified, the default is "none".
 	 *
 	 * @see SchemaGenAction
 	 */
 	public static final String SCHEMA_GEN_SCRIPTS_ACTION = "javax.persistence.schema-generation.scripts.action";
 
 	/**
 	 * For cases where the {@value #SCHEMA_GEN_SCRIPTS_ACTION} value indicates that schema creation commands should
 	 * be written to DDL script file, {@value #SCHEMA_GEN_SCRIPTS_CREATE_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #SCHEMA_GEN_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	public static final String SCHEMA_GEN_SCRIPTS_CREATE_TARGET = "javax.persistence.schema-generation.scripts.create-target";
 
 	/**
 	 * For cases where the {@value #SCHEMA_GEN_SCRIPTS_ACTION} value indicates that schema drop commands should
 	 * be written to DDL script file, {@value #SCHEMA_GEN_SCRIPTS_DROP_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #SCHEMA_GEN_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	public static final String SCHEMA_GEN_SCRIPTS_DROP_TARGET = "javax.persistence.schema-generation.scripts.drop-target";
 
 	/**
 	 * Specifies whether the persistence provider is to create the database schema(s) in addition to creating
 	 * database objects (tables, sequences, constraints, etc).  The value of this boolean property should be set
 	 * to {@code true} if the persistence provider is to create schemas in the database or to generate DDL that
 	 * contains “CREATE SCHEMA” commands.  If this property is not supplied (or is explicitly {@code false}), the
 	 * provider should not attempt to create database schemas.
 	 */
 	public static final String SCHEMA_GEN_CREATE_SCHEMAS = "javax.persistence.create-database-schemas";
 
 	/**
 	 * Allows passing the specific {@link java.sql.Connection} instance to be used for performing schema generation
 	 * where the target is "database".
 	 * <p/>
 	 * May also be used to determine the values for {@value #SCHEMA_GEN_DB_NAME},
 	 * {@value #SCHEMA_GEN_DB_MAJOR_VERSION} and {@value #SCHEMA_GEN_DB_MINOR_VERSION}.
 	 */
 	public static final String SCHEMA_GEN_CONNECTION = "javax.persistence.schema-generation-connection";
 
 	/**
 	 * Specifies the name of the database provider in cases where a Connection to the underlying database is
 	 * not available (aka, mainly in generating scripts).  In such cases, a value for
 	 * {@value #SCHEMA_GEN_DB_NAME} *must* be specified.
 	 * <p/>
 	 * The value of this setting is expected to match the value returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseProductName()} for the target database.
 	 * <p/>
 	 * Additionally specifying {@value #SCHEMA_GEN_DB_MAJOR_VERSION} and/or {@value #SCHEMA_GEN_DB_MINOR_VERSION}
 	 * may be required to understand exactly how to generate the required schema commands.
 	 *
 	 * @see #SCHEMA_GEN_DB_MAJOR_VERSION
 	 * @see #SCHEMA_GEN_DB_MINOR_VERSION
 	 */
 	@SuppressWarnings("JavaDoc")
 	public static final String SCHEMA_GEN_DB_NAME = "javax.persistence.database-product-name";
 
 	/**
 	 * Specifies the major version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where {@value #SCHEMA_GEN_DB_NAME} does not provide enough distinction.
 
 	 * @see #SCHEMA_GEN_DB_NAME
 	 * @see #SCHEMA_GEN_DB_MINOR_VERSION
 	 */
 	public static final String SCHEMA_GEN_DB_MAJOR_VERSION = "javax.persistence.database-major-version";
 
 	/**
 	 * Specifies the minor version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where te combination of {@value #SCHEMA_GEN_DB_NAME} and {@value #SCHEMA_GEN_DB_MAJOR_VERSION} does not provide
 	 * enough distinction.
 	 *
 	 * @see #SCHEMA_GEN_DB_NAME
 	 * @see #SCHEMA_GEN_DB_MAJOR_VERSION
 	 */
 	public static final String SCHEMA_GEN_DB_MINOR_VERSION = "javax.persistence.database-minor-version";
 
 	/**
 	 * Specifies a {@link java.io.Reader} configured for reading of the SQL load script or a string designating the
 	 * file {@link java.net.URL} for the SQL load script.
 	 * <p/>
 	 * A "SQL load script" is a script that performs some database initialization (INSERT, etc).
 	 */
 	public static final String SCHEMA_GEN_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Hibernate specific settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Query hint (aka {@link javax.persistence.Query#setHint}) for applying
 	 * an alias specific lock mode (aka {@link org.hibernate.Query#setLockMode}).
 	 * <p/>
 	 * Either {@link org.hibernate.LockMode} or {@link javax.persistence.LockModeType}
 	 * are accepted.  Also the String names of either are accepted as well.  <tt>null</tt>
 	 * is additionally accepted as meaning {@link org.hibernate.LockMode#NONE}.
 	 * <p/>
 	 * Usage is to concatenate this setting name and the alias name together, separated
 	 * by a dot.  For example<code>Query.setHint( "org.hibernate.lockMode.a", someLockMode )</code>
 	 * would apply <code>someLockMode</code> to the alias <code>"a"</code>.
 	 */
 	//Use the org.hibernate prefix. instead of hibernate. as it is a query hint se QueryHints
 	public static final String ALIAS_SPECIFIC_LOCK_MODE = "org.hibernate.lockMode";
 
 	/**
 	 * JAR autodetection artifacts class, hbm
 	 */
 	public static final String AUTODETECTION = "hibernate.archive.autodetection";
 
 	/**
 	 * cfg.xml configuration file used
 	 */
 	public static final String CFG_FILE = "hibernate.ejb.cfgfile";
 
 	/**
 	 * Caching configuration should follow the following pattern
 	 * hibernate.ejb.classcache.<fully.qualified.Classname> usage[, region]
 	 * where usage is the cache strategy used and region the cache region name
 	 */
 	public static final String CLASS_CACHE_PREFIX = "hibernate.ejb.classcache";
 
 	/**
 	 * Caching configuration should follow the following pattern
 	 * hibernate.ejb.collectioncache.<fully.qualified.Classname>.<role> usage[, region]
 	 * where usage is the cache strategy used and region the cache region name
 	 */
 	public static final String COLLECTION_CACHE_PREFIX = "hibernate.ejb.collectioncache";
 
 	/**
 	 * Interceptor class name, the class has to have a no-arg constructor
 	 * the interceptor instance is shared amongst all EntityManager of a given EntityManagerFactory
 	 */
 	public static final String INTERCEPTOR = "hibernate.ejb.interceptor";
 
 	/**
 	 * Interceptor class name, the class has to have a no-arg constructor
 	 */
 	public static final String SESSION_INTERCEPTOR = "hibernate.ejb.interceptor.session_scoped";
 
 	/**
 	 * SessionFactoryObserver class name, the class must have a no-arg constructor
 	 */
 	public static final String SESSION_FACTORY_OBSERVER = "hibernate.ejb.session_factory_observer";
 
 	/**
 	 * Naming strategy class name, the class has to have a no-arg constructor
 	 */
 	public static final String NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
 
 	/**
 	 * IdentifierGeneratorStrategyProvider class name, the class must have a no-arg constructor
 	 * @deprecated if possible wait of Hibernate 4.1 and theService registry (MutableIdentifierGeneratorStrategy service)
 	 */
 	public static final String IDENTIFIER_GENERATOR_STRATEGY_PROVIDER = "hibernate.ejb.identifier_generator_strategy_provider";
 
 	/**
 	 * Event configuration should follow the following pattern
 	 * hibernate.ejb.event.[eventType] f.q.c.n.EventListener1, f.q.c.n.EventListener12 ...
 	 */
 	public static final String EVENT_LISTENER_PREFIX = "hibernate.ejb.event";
 
 	/**
 	 * Enable the class file enhancement
 	 */
 	public static final String USE_CLASS_ENHANCER = "hibernate.ejb.use_class_enhancer";
 
 	/**
 	 * Whether or not discard persistent context on entityManager.close()
 	 * The EJB3 compliant and default choice is false
 	 */
 	public static final String DISCARD_PC_ON_CLOSE = "hibernate.ejb.discard_pc_on_close";
 
 	/**
 	 * Consider this as experimental
 	 * It is not recommended to set up this property, the configuration is stored
 	 * in the JNDI in a serialized form
 	 */
 	public static final String CONFIGURATION_JNDI_NAME = "hibernate.ejb.configuration_jndi_name";
 
 	/**
 	 * Used to determine flush mode.
 	 */
 	//Use the org.hibernate prefix. instead of hibernate. as it is a query hint se QueryHints
 	public static final String FLUSH_MODE = "org.hibernate.flushMode";
 
 	/**
 	 * Pass an implementation of {@link org.hibernate.ejb.packaging.Scanner}:
 	 *  - preferably an actual instance
 	 *  - or a class name with a no-arg constructor 
 	 */
 	public static final String SCANNER = "hibernate.ejb.resource_scanner";
 
 	/**
 	 * List of classes names
 	 * Internal use only
 	 */
 	public static final String CLASS_NAMES = "hibernate.ejb.classes";
 
 	/**
 	 * List of annotated packages
 	 * Internal use only
 	 */
 	public static final String PACKAGE_NAMES = "hibernate.ejb.packages";
 
 	/**
 	 * EntityManagerFactory name
 	 */
 	public static final String ENTITY_MANAGER_FACTORY_NAME = "hibernate.ejb.entitymanager_factory_name";
 
 	/**
 	 * @deprecated use {@link #JPA_METAMODEL_POPULATION} instead.
 	 */
 	@Deprecated
 	public static final String JPA_METAMODEL_GENERATION = "hibernate.ejb.metamodel.generation";
 
 	/**
 	 * Setting that controls whether we seek out JPA "static metamodel" classes and populate them.  Accepts
 	 * 3 values:<ul>
 	 *     <li>
 	 *         <b>enabled</b> - Do the population
 	 *     </li>
 	 *     <li>
 	 *         <b>disabled</b> - Do not do the population
 	 *     </li>
 	 *     <li>
 	 *         <b>ignoreUnsupported</b> - Do the population, but ignore any non-JPA features that would otherwise
 	 *         result in the population failing.
 	 *     </li>
 	 * </ul>
 	 *
 	 */
 	public static final String JPA_METAMODEL_POPULATION = "hibernate.ejb.metamodel.population";
 
 
 	/**
 	 * List of classes names
 	 * Internal use only
 	 */
 	public static final String XML_FILE_NAMES = "hibernate.ejb.xml_files";
 	public static final String HBXML_FILES = "hibernate.hbmxml.files";
 	public static final String LOADED_CLASSES = "hibernate.ejb.loaded.classes";
-	public static final String JACC_CONTEXT_ID = "hibernate.jacc.ctx.id";
-	public static final String JACC_PREFIX = "hibernate.jacc";
-	public static final String JACC_ENABLED = "hibernate.jacc.enabled";
+	public static final String JACC_CONTEXT_ID = org.hibernate.cfg.AvailableSettings.JACC_CONTEXT_ID;
+	public static final String JACC_PREFIX = org.hibernate.cfg.AvailableSettings.JACC_PREFIX;
+	public static final String JACC_ENABLED = org.hibernate.cfg.AvailableSettings.JACC_ENABLED;
 	public static final String PERSISTENCE_UNIT_NAME = "hibernate.ejb.persistenceUnitName";
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index dd47f695b0..eba7b99013 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,1252 +1,1248 @@
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
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
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
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.jboss.jandex.IndexView;
 import org.jboss.jandex.Indexer;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
 import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.boot.scan.internal.StandardScanOptions;
 import org.hibernate.jpa.boot.scan.internal.StandardScanner;
 import org.hibernate.jpa.boot.spi.ClassDescriptor;
 import org.hibernate.jpa.boot.spi.InputStreamAccess;
 import org.hibernate.jpa.boot.spi.MappingFileDescriptor;
 import org.hibernate.jpa.boot.spi.NamedInputStream;
 import org.hibernate.jpa.boot.spi.PackageDescriptor;
 import org.hibernate.jpa.boot.scan.spi.ScanOptions;
 import org.hibernate.jpa.boot.scan.spi.ScanResult;
 import org.hibernate.jpa.boot.scan.spi.Scanner;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.secure.internal.JACCConfiguration;
+import org.hibernate.secure.spi.GrantedPermission;
+import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.ConfigLoader;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
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
 
-	private final List<JaccDefinition> jaccDefinitions = new ArrayList<JaccDefinition>();
+	private final List<GrantedPermission> grantedJaccPermissions = new ArrayList<GrantedPermission>();
 	private final List<CacheRegionDefinition> cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 	// todo : would much prefer this as a local variable...
 	private final List<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping> cfgXmlNamedMappings = new ArrayList<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping>();
 	private Interceptor sessionFactoryInterceptor;
 	private NamingStrategy namingStrategy;
 	private SessionFactoryObserver suppliedSessionFactoryObserver;
 
 	private MetadataSources metadataSources;
 	private Configuration hibernateConfiguration;
 
 	private static EntityNotFoundDelegate jpaEntityNotFoundDelegate = new JpaEntityNotFoundDelegate();
 	
 	private ClassLoader providedClassLoader;
 
 	private static class JpaEntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException( "Unable to find " + entityName  + " with id " + id );
 		}
 	}
 
 	public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
 		this( persistenceUnit, integrationSettings, null );
 	}
 
 	public EntityManagerFactoryBuilderImpl(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			ClassLoader providedClassLoader ) {
 		
 		LogHelper.logPersistenceUnitInformation( persistenceUnit );
 
 		this.persistenceUnit = persistenceUnit;
 
 		if ( integrationSettings == null ) {
 			integrationSettings = Collections.emptyMap();
 		}
 		
 		this.providedClassLoader = providedClassLoader;
 
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
 		final ScanResult scanResult = scan( bootstrapServiceRegistry );
 		final DeploymentResources deploymentResources = buildDeploymentResources( scanResult, bootstrapServiceRegistry );
 		//		2) building a Jandex index
 		final IndexView jandexIndex = locateOrBuildJandexIndex( deploymentResources );
 		//		3) building "metadata sources" to keep for later to use in building the SessionFactory
 		metadataSources = prepareMetadataSources( jandexIndex, deploymentResources, bootstrapServiceRegistry );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		withValidatorFactory( configurationValues.get( AvailableSettings.VALIDATION_FACTORY ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// push back class transformation to the environment; for the time being this only has any effect in EE
 		// container situations, calling back into PersistenceUnitInfo#addClassTransformer
 		final boolean useClassTransformer = "true".equals( configurationValues.remove( AvailableSettings.USE_CLASS_ENHANCER ) );
 		if ( useClassTransformer ) {
 			persistenceUnit.pushClassTransformer( metadataSources.collectMappingClassNames() );
 		}
 	}
 
 	private static interface DeploymentResources {
 		public Iterable<ClassDescriptor> getClassDescriptors();
 		public Iterable<PackageDescriptor> getPackageDescriptors();
 		public Iterable<MappingFileDescriptor> getMappingFileDescriptors();
 	}
 
 	private DeploymentResources buildDeploymentResources(
 			ScanResult scanResult,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 
 		// mapping files ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final ArrayList<MappingFileDescriptor> mappingFileDescriptors = new ArrayList<MappingFileDescriptor>();
 
 		final Set<String> nonLocatedMappingFileNames = new HashSet<String>();
 		final List<String> explicitMappingFileNames = persistenceUnit.getMappingFileNames();
 		if ( explicitMappingFileNames != null ) {
 			nonLocatedMappingFileNames.addAll( explicitMappingFileNames );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : scanResult.getLocatedMappingFiles() ) {
 			mappingFileDescriptors.add( mappingFileDescriptor );
 			nonLocatedMappingFileNames.remove( mappingFileDescriptor.getName() );
 		}
 
 		for ( String name : nonLocatedMappingFileNames ) {
 			MappingFileDescriptor descriptor = buildMappingFileDescriptor( name, bootstrapServiceRegistry );
 			mappingFileDescriptors.add( descriptor );
 		}
 
 
 		// classes and packages ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final HashMap<String, ClassDescriptor> classDescriptorMap = new HashMap<String, ClassDescriptor>();
 		final HashMap<String, PackageDescriptor> packageDescriptorMap = new HashMap<String, PackageDescriptor>();
 
 		for ( ClassDescriptor classDescriptor : scanResult.getLocatedClasses() ) {
 			classDescriptorMap.put( classDescriptor.getName(), classDescriptor );
 		}
 
 		for ( PackageDescriptor packageDescriptor : scanResult.getLocatedPackages() ) {
 			packageDescriptorMap.put( packageDescriptor.getName(), packageDescriptor );
 		}
 
 		final List<String> explicitClassNames = persistenceUnit.getManagedClassNames();
 		if ( explicitClassNames != null ) {
 			for ( String explicitClassName : explicitClassNames ) {
 				// IMPL NOTE : explicitClassNames can contain class or package names!!!
 				if ( classDescriptorMap.containsKey( explicitClassName ) ) {
 					continue;
 				}
 				if ( packageDescriptorMap.containsKey( explicitClassName ) ) {
 					continue;
 				}
 
 				// try it as a class name first...
 				final String classFileName = explicitClassName.replace( '.', '/' ) + ".class";
 				final URL classFileUrl = bootstrapServiceRegistry.getService( ClassLoaderService.class )
 						.locateResource( classFileName );
 				if ( classFileUrl != null ) {
 					classDescriptorMap.put(
 							explicitClassName,
 							new ClassDescriptorImpl( explicitClassName, new UrlInputStreamAccess( classFileUrl ) )
 					);
 					continue;
 				}
 
 				// otherwise, try it as a package name
 				final String packageInfoFileName = explicitClassName.replace( '.', '/' ) + "/package-info.class";
 				final URL packageInfoFileUrl = bootstrapServiceRegistry.getService( ClassLoaderService.class )
 						.locateResource( packageInfoFileName );
 				if ( packageInfoFileUrl != null ) {
 					packageDescriptorMap.put(
 							explicitClassName,
 							new PackageDescriptorImpl( explicitClassName, new UrlInputStreamAccess( packageInfoFileUrl ) )
 					);
 					continue;
 				}
 
 				LOG.debugf(
 						"Unable to resolve class [%s] named in persistence unit [%s]",
 						explicitClassName,
 						persistenceUnit.getName()
 				);
 			}
 		}
 
 		return new DeploymentResources() {
 			@Override
 			public Iterable<ClassDescriptor> getClassDescriptors() {
 				return classDescriptorMap.values();
 			}
 
 			@Override
 			public Iterable<PackageDescriptor> getPackageDescriptors() {
 				return packageDescriptorMap.values();
 			}
 
 			@Override
 			public Iterable<MappingFileDescriptor> getMappingFileDescriptors() {
 				return mappingFileDescriptors;
 			}
 		};
 	}
 
 	private MappingFileDescriptor buildMappingFileDescriptor(
 			String name,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final URL url = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResource( name );
 		if ( url == null ) {
 			throw persistenceException( "Unable to resolve named mapping-file [" + name + "]" );
 		}
 
 		return new MappingFileDescriptorImpl( name, new UrlInputStreamAccess( url ) );
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
 			DeploymentResources deploymentResources,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// todo : this needs to tie into the metamodel branch...
 		MetadataSources metadataSources = new MetadataSources();
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			final String className = classDescriptor.getName();
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
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			metadataSources.packageNames.add( packageDescriptor.getName() );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : deploymentResources.getMappingFileDescriptors() ) {
 			metadataSources.namedMappingFileInputStreams.add( mappingFileDescriptor.getStreamAccess().asNamedInputStream() );
 		}
 
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
 
 	private IndexView locateOrBuildJandexIndex(DeploymentResources deploymentResources) {
 		// for now create a whole new Index to work with, eventually we need to:
 		//		1) accept an Index as an incoming config value
 		//		2) pass that Index along to the metamodel code...
 		IndexView jandexIndex = (IndexView) configurationValues.get( JANDEX_INDEX );
 		if ( jandexIndex == null ) {
 			jandexIndex = buildJandexIndex( deploymentResources );
 		}
 		return jandexIndex;
 	}
 
 	private IndexView buildJandexIndex(DeploymentResources deploymentResources) {
 		Indexer indexer = new Indexer();
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			indexStream( indexer, classDescriptor.getStreamAccess() );
 		}
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			indexStream( indexer, packageDescriptor.getStreamAccess() );
 		}
 
 		// for now we just skip entities defined in (1) orm.xml files and (2) hbm.xml files.  this part really needs
 		// metamodel branch...
 
 		// for now, we also need to wrap this in a CompositeIndex until Jandex is updated to use a common interface
 		// between the 2...
 		return indexer.complete();
 	}
 
 	private void indexStream(Indexer indexer, InputStreamAccess streamAccess) {
 		try {
 			InputStream stream = streamAccess.accessInputStream();
 			try {
 				indexer.index( stream );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw persistenceException( "Unable to index from stream " + streamAccess.getStreamName(), e );
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
 
 		// TODO: If providedClassLoader is present (OSGi, etc.) *and*
 		// an APP_CLASSLOADER is provided, should throw an exception or
 		// warn?
 		ClassLoader classLoader;
 		ClassLoader appClassLoader = (ClassLoader) integrationSettings.get( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		if ( providedClassLoader != null ) {
 			classLoader = providedClassLoader;
 		}
 		else if ( appClassLoader != null ) {
 			classLoader = appClassLoader;
 			integrationSettings.remove( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		}
 		else {
 			classLoader = persistenceUnit.getClassLoader();
 		}
 		bootstrapServiceRegistryBuilder.with( classLoader );
 
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
-			final String contextId = configurationElement.getSecurity().getContext();
 			for ( JaxbHibernateConfiguration.JaxbSecurity.JaxbGrant grant : configurationElement.getSecurity().getGrant() ) {
-				jaccDefinitions.add(
-						new JaccDefinition(
-								contextId,
+				grantedJaccPermissions.add(
+						new GrantedPermission(
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
 
-			final JaccDefinition def = new JaccDefinition( jaccContextId, role, clazz, (String) value );
-
-			jaccDefinitions.add( def );
-
+			grantedJaccPermissions.add( new GrantedPermission( role, clazz, (String) value ) );
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
 		final Scanner scanner = locateOrBuildScanner( bootstrapServiceRegistry );
 		final ScanOptions scanOptions = determineScanOptions();
 
 		return scanner.scan( persistenceUnit, scanOptions );
 	}
 
 	private ScanOptions determineScanOptions() {
 		return new StandardScanOptions(
 				(String) configurationValues.get( AvailableSettings.AUTODETECTION ),
 				persistenceUnit.isExcludeUnlistedClasses()
 		);
 	}
 
 	@SuppressWarnings("unchecked")
 	private Scanner locateOrBuildScanner(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Object value = configurationValues.remove( AvailableSettings.SCANNER );
 		if ( value == null ) {
 			return new StandardScanner();
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
 
 	@Override
 	public void generateSchema() {
 		processProperties();
 
 		final ServiceRegistry serviceRegistry = buildServiceRegistry();
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// IMPL NOTE : TCCL handling here is temporary.
 		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
 		// 		in turn which relies on TCCL being set.
 
 		( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work() {
 					@Override
 					public Object perform() {
 						final Configuration hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
 						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
 						return null;
 					}
 				}
 		);
 
 		// release this builder
 		cancel();
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
 		return serviceRegistryBuilder.build();
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
 
-		if ( jaccDefinitions != null ) {
-			for ( JaccDefinition jaccDefinition : jaccDefinitions ) {
-				JACCConfiguration jaccCfg = new JACCConfiguration( jaccDefinition.contextId );
-				jaccCfg.addPermission( jaccDefinition.role, jaccDefinition.clazz, jaccDefinition.actions );
+		if ( grantedJaccPermissions != null ) {
+			final JaccService jaccService = serviceRegistry.getService( JaccService.class );
+			for ( GrantedPermission grantedPermission : grantedJaccPermissions ) {
+				jaccService.addPermission( grantedPermission );
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
 			catch ( InvalidMappingException e ) {
 				// try our best to give the file name
 				if ( StringHelper.isNotEmpty( namedInputStream.getName() ) ) {
 					throw new InvalidMappingException(
 							"Error while parsing file: " + namedInputStream.getName(),
 							e.getType(),
 							e.getPath(),
 							e
 					);
 				}
 				else {
 					throw e;
 				}
 			}
 			catch (MappingException me) {
 				// try our best to give the file name
 				if ( StringHelper.isNotEmpty( namedInputStream.getName() ) ) {
 					throw new MappingException("Error while parsing file: " + namedInputStream.getName(), me );
 				}
 				else {
 					throw me;
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/spi/JpaIntegrator.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/spi/JpaIntegrator.java
index d7a397c236..20f56e0b75 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/spi/JpaIntegrator.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/event/spi/JpaIntegrator.java
@@ -1,362 +1,319 @@
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
 package org.hibernate.jpa.event.spi;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.DuplicationStrategy;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.event.internal.core.HibernateEntityManagerEventListener;
 import org.hibernate.jpa.event.internal.core.JpaAutoFlushEventListener;
 import org.hibernate.jpa.event.internal.core.JpaDeleteEventListener;
 import org.hibernate.jpa.event.internal.core.JpaFlushEntityEventListener;
 import org.hibernate.jpa.event.internal.core.JpaFlushEventListener;
 import org.hibernate.jpa.event.internal.core.JpaMergeEventListener;
 import org.hibernate.jpa.event.internal.core.JpaPersistEventListener;
 import org.hibernate.jpa.event.internal.core.JpaPersistOnFlushEventListener;
 import org.hibernate.jpa.event.internal.core.JpaPostDeleteEventListener;
 import org.hibernate.jpa.event.internal.core.JpaPostInsertEventListener;
 import org.hibernate.jpa.event.internal.core.JpaPostLoadEventListener;
 import org.hibernate.jpa.event.internal.core.JpaPostUpdateEventListener;
 import org.hibernate.jpa.event.internal.core.JpaSaveEventListener;
 import org.hibernate.jpa.event.internal.core.JpaSaveOrUpdateEventListener;
 import org.hibernate.jpa.event.internal.jpa.CallbackProcessor;
 import org.hibernate.jpa.event.internal.jpa.CallbackProcessorImpl;
 import org.hibernate.jpa.event.internal.jpa.CallbackRegistryConsumer;
 import org.hibernate.jpa.event.internal.jpa.CallbackRegistryImpl;
 import org.hibernate.jpa.event.internal.jpa.LegacyCallbackProcessor;
 import org.hibernate.jpa.event.spi.jpa.ListenerFactory;
 import org.hibernate.jpa.event.internal.jpa.StandardListenerFactory;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.secure.internal.JACCPreDeleteEventListener;
-import org.hibernate.secure.internal.JACCPreInsertEventListener;
-import org.hibernate.secure.internal.JACCPreLoadEventListener;
-import org.hibernate.secure.internal.JACCPreUpdateEventListener;
-import org.hibernate.secure.internal.JACCSecurityListener;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Hibernate EntityManager specific Integrator, performing JPA setup.
  *
  * @author Steve Ebersole
  */
 public class JpaIntegrator implements Integrator {
 	private ListenerFactory jpaListenerFactory;
 	private CallbackProcessor callbackProcessor;
 	private CallbackRegistryImpl callbackRegistry;
 
 	private static final DuplicationStrategy JPA_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					HibernateEntityManagerEventListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
-	private static final DuplicationStrategy JACC_DUPLICATION_STRATEGY = new DuplicationStrategy() {
-		@Override
-		public boolean areMatch(Object listener, Object original) {
-			return listener.getClass().equals( original.getClass() ) &&
-					JACCSecurityListener.class.isInstance( original );
-		}
-
-		@Override
-		public Action getAction() {
-			return Action.KEEP_ORIGINAL;
-		}
-	};
-
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		// first, register the JPA-specific persist cascade style
 		CascadeStyles.registerCascadeStyle(
 				"persist",
 				new CascadeStyles.BaseCascadeStyle() {
 					@Override
 					public boolean doCascade(CascadingAction action) {
 						return action == JpaPersistEventListener.PERSIST_SKIPLAZY
 								|| action == CascadingActions.PERSIST_ON_FLUSH;
 					}
 
 					@Override
 					public String toString() {
 						return "STYLE_PERSIST_SKIPLAZY";
 					}
 				}
 		);
 
 
 		// then prepare listeners
 		final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 
-		boolean isSecurityEnabled = configuration.getProperties().containsKey( AvailableSettings.JACC_ENABLED );
-
 		eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
-		eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
 
 		// op listeners
 		eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, JpaAutoFlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.DELETE, new JpaDeleteEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new JpaFlushEntityEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH, JpaFlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.MERGE, new JpaMergeEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST, new JpaPersistEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new JpaPersistOnFlushEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE, new JpaSaveEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new JpaSaveOrUpdateEventListener() );
 
-		// pre op listeners
-		if ( isSecurityEnabled ) {
-			final String jaccContextId = configuration.getProperty( Environment.JACC_CONTEXTID );
-			eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener(jaccContextId) );
-			eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener(jaccContextId) );
-			eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener(jaccContextId) );
-			eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener(jaccContextId) );
-		}
-
 		// post op listeners
 		eventListenerRegistry.prependListeners( EventType.POST_DELETE, new JpaPostDeleteEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_INSERT, new JpaPostInsertEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_LOAD, new JpaPostLoadEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new JpaPostUpdateEventListener() );
 
 		for ( Map.Entry<?,?> entry : configuration.getProperties().entrySet() ) {
 			if ( ! String.class.isInstance( entry.getKey() ) ) {
 				continue;
 			}
 			final String propertyName = (String) entry.getKey();
 			if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
 				continue;
 			}
 			final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
 			final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
 				eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
 			}
 		}
 
 		// handle JPA "entity listener classes"...
 
 		this.callbackRegistry = new CallbackRegistryImpl();
 		final Object beanManagerRef = configuration.getProperties().get( AvailableSettings.CDI_BEAN_MANAGER );
 		this.jpaListenerFactory = beanManagerRef == null
 				? new StandardListenerFactory()
 				: buildBeanManagerListenerFactory( beanManagerRef );
 		this.callbackProcessor = new LegacyCallbackProcessor( jpaListenerFactory, configuration.getReflectionManager() );
 
 		Iterator classes = configuration.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass clazz = (PersistentClass) classes.next();
 			if ( clazz.getClassName() == null ) {
 				// we can have non java class persisted by hibernate
 				continue;
 			}
 			callbackProcessor.processCallbacksForEntity( clazz.getClassName(), callbackRegistry );
 		}
 
 		for ( EventType eventType : EventType.values() ) {
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			for ( Object listener : eventListenerGroup.listeners() ) {
 				if ( CallbackRegistryConsumer.class.isInstance( listener ) ) {
 					( (CallbackRegistryConsumer) listener ).injectCallbackRegistry( callbackRegistry );
 				}
 			}
 		}
 	}
 
 	private static final String CDI_LISTENER_FACTORY_CLASS = "org.hibernate.jpa.event.internal.jpa.BeanManagerListenerFactory";
 
 	private ListenerFactory buildBeanManagerListenerFactory(Object beanManagerRef) {
 		try {
 			// specifically using our classloader here...
 			final Class beanManagerListenerFactoryClass = getClass().getClassLoader()
 					.loadClass( CDI_LISTENER_FACTORY_CLASS );
 			final Method beanManagerListenerFactoryBuilderMethod = beanManagerListenerFactoryClass.getMethod(
 					"fromBeanManagerReference",
 					Object.class
 			);
 
 			try {
 				return (ListenerFactory) beanManagerListenerFactoryBuilderMethod.invoke( null, beanManagerRef );
 			}
 			catch (InvocationTargetException e) {
 				throw e.getTargetException();
 			}
 		}
 		catch (ClassNotFoundException e) {
 			throw new HibernateException( "Could not locate BeanManagerListenerFactory class to handle CDI extensions", e );
 		}
 		catch (HibernateException e) {
 			throw e;
 		}
 		catch (Throwable e) {
 			throw new HibernateException( "Could not access BeanManagerListenerFactory class to handle CDI extensions", e );
 		}
 	}
 
 	@Override
 	public void integrate(
 			MetadataImplementor metadata,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry ) {
 		// first, register the JPA-specific persist cascade style
 		CascadeStyles.registerCascadeStyle(
 				"persist",
 				new CascadeStyles.BaseCascadeStyle() {
 					@Override
 					public boolean doCascade(CascadingAction action) {
 						return action == JpaPersistEventListener.PERSIST_SKIPLAZY
 								|| action == CascadingActions.PERSIST_ON_FLUSH;
 					}
 
 					@Override
 					public String toString() {
 						return "STYLE_PERSIST_SKIPLAZY";
 					}
 				}
 		);
 
 		// then prepare listeners
         final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 
-        boolean isSecurityEnabled = sessionFactory.getProperties().containsKey( AvailableSettings.JACC_ENABLED );
-
         eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
-        eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
 
         // op listeners
         eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, JpaAutoFlushEventListener.INSTANCE );
         eventListenerRegistry.setListeners( EventType.DELETE, new JpaDeleteEventListener() );
         eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new JpaFlushEntityEventListener() );
         eventListenerRegistry.setListeners( EventType.FLUSH, JpaFlushEventListener.INSTANCE );
         eventListenerRegistry.setListeners( EventType.MERGE, new JpaMergeEventListener() );
         eventListenerRegistry.setListeners( EventType.PERSIST, new JpaPersistEventListener() );
         eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new JpaPersistOnFlushEventListener() );
         eventListenerRegistry.setListeners( EventType.SAVE, new JpaSaveEventListener() );
         eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new JpaSaveOrUpdateEventListener() );
 
-        // pre op listeners
-        if ( isSecurityEnabled ) {
-            final String jaccContextId = sessionFactory.getProperties().getProperty( Environment.JACC_CONTEXTID );
-            eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener(jaccContextId) );
-            eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener(jaccContextId) );
-            eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener(jaccContextId) );
-            eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener(jaccContextId) );
-        }
-
         // post op listeners
         eventListenerRegistry.prependListeners( EventType.POST_DELETE, new JpaPostDeleteEventListener() );
         eventListenerRegistry.prependListeners( EventType.POST_INSERT, new JpaPostInsertEventListener() );
         eventListenerRegistry.prependListeners( EventType.POST_LOAD, new JpaPostLoadEventListener() );
         eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new JpaPostUpdateEventListener() );
 
         for ( Map.Entry<?,?> entry : sessionFactory.getProperties().entrySet() ) {
             if ( ! String.class.isInstance( entry.getKey() ) ) {
                 continue;
             }
             final String propertyName = (String) entry.getKey();
             if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
                 continue;
             }
             final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
             final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
             final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
             for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
                 eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
             }
         }
 
 		// handle JPA "entity listener classes"...
 
 		this.callbackRegistry = new CallbackRegistryImpl();
 		final Object beanManagerRef = sessionFactory.getProperties().get( AvailableSettings.CDI_BEAN_MANAGER );
 		this.jpaListenerFactory = beanManagerRef == null
 				? new StandardListenerFactory()
 				: buildBeanManagerListenerFactory( beanManagerRef );
 		this.callbackProcessor = new CallbackProcessorImpl( jpaListenerFactory, metadata, serviceRegistry );
 
         for ( EntityBinding binding : metadata.getEntityBindings() ) {
 			callbackProcessor.processCallbacksForEntity( binding, callbackRegistry );
         }
 
         for ( EventType eventType : EventType.values() ) {
             final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
             for ( Object listener : eventListenerGroup.listeners() ) {
                 if ( CallbackRegistryConsumer.class.isInstance( listener ) ) {
                     ( (CallbackRegistryConsumer) listener ).injectCallbackRegistry( callbackRegistry );
                 }
             }
         }
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 		if ( callbackRegistry != null ) {
 			callbackRegistry.release();
 		}
 		if ( callbackProcessor != null ) {
 			callbackProcessor.release();
 		}
 		if ( jpaListenerFactory != null ) {
 			jpaListenerFactory.release();
 		}
 	}
 
 	private Object instantiate(String listenerImpl, ServiceRegistryImplementor serviceRegistry) {
 		try {
 			return serviceRegistry.getService( ClassLoaderService.class ).classForName( listenerImpl ).newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Could not instantiate requested listener [" + listenerImpl + "]", e );
         }
     }
 }
