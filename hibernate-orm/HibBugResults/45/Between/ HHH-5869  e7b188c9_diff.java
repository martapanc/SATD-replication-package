diff --git a/hibernate-core/src/main/java/org/hibernate/annotations/Nationalized.java b/hibernate-core/src/main/java/org/hibernate/annotations/Nationalized.java
new file mode 100644
index 0000000000..84c468d522
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/annotations/Nationalized.java
@@ -0,0 +1,42 @@
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
+package org.hibernate.annotations;
+
+import java.lang.annotation.Retention;
+import java.lang.annotation.Target;
+
+import static java.lang.annotation.ElementType.FIELD;
+import static java.lang.annotation.ElementType.METHOD;
+import static java.lang.annotation.RetentionPolicy.RUNTIME;
+
+/**
+ * Marks a character data type (String, Character, character, Clob) as being a nationalized variant
+ * (NVARCHAR, NCHAR, NCLOB, etc).
+ *
+ * @author Steve Ebersole
+ */
+@Target( { METHOD, FIELD } )
+@Retention( RUNTIME )
+public @interface Nationalized {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index ce5744a0a6..74e605d027 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,595 +1,597 @@
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
 	 * Names the {@link ClassLoader} used to load user application classes.
 	 * @since 4.0
 	 */
 	public static final String APP_CLASSLOADER = "hibernate.classLoader.application";
 
 	/**
 	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
 	 * @since 4.0
 	 */
 	public static final String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
 
 	/**
 	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
 	 * the {@link ClassLoader} that loaded this class.
 	 * @since 4.0
 	 */
 	public static final String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
 
 	/**
 	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
 	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
 	 * @since 4.0
 	 */
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
+
+	public static final String USE_NATIONALIZED_CHARACTER_DATA = "hibernate.use_nationalized_character_data";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 2a4dadc46b..1671f0d0f9 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -2147,1468 +2147,1481 @@ public class Configuration implements Serializable {
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
 
+		private Boolean useNationalizedCharacterData;
+
+		@Override
+		@SuppressWarnings( {"UnnecessaryUnboxing"})
+		public boolean useNationalizedCharacterData() {
+			if ( useNationalizedCharacterData == null ) {
+				final String booleanName = getConfigurationProperties()
+						.getProperty( AvailableSettings.USE_NATIONALIZED_CHARACTER_DATA );
+				useNationalizedCharacterData = Boolean.valueOf( booleanName );
+			}
+			return useNationalizedCharacterData.booleanValue();
+		}
+
 		private Boolean forceDiscriminatorInSelectsByDefault;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean forceDiscriminatorInSelectsByDefault() {
 			if ( forceDiscriminatorInSelectsByDefault == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT );
 				forceDiscriminatorInSelectsByDefault = Boolean.valueOf( booleanName );
 			}
 			return forceDiscriminatorInSelectsByDefault.booleanValue();
 		}
 
 		public IdGenerator getGenerator(String name) {
 			return getGenerator( name, null );
 		}
 
 		public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
 			if ( localGenerators != null ) {
 				IdGenerator result = localGenerators.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return namedGenerators.get( name );
 		}
 
 		public void addGenerator(IdGenerator generator) {
 			if ( !defaultNamedGenerators.contains( generator.getName() ) ) {
 				IdGenerator old = namedGenerators.put( generator.getName(), generator );
 				if ( old != null ) {
 					LOG.duplicateGeneratorName( old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				LOG.duplicateGeneratorTable( name );
 			}
 		}
 
 		public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
 			if ( localGeneratorTables != null ) {
 				Properties result = localGeneratorTables.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return generatorTables.get( name );
 		}
 
 		public Map<String, Join> getJoins(String entityName) {
 			return joins.get( entityName );
 		}
 
 		public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
 			Object old = Configuration.this.joins.put( persistentClass.getEntityName(), joins );
 			if ( old != null ) {
 				LOG.duplicateJoins( persistentClass.getEntityName() );
 			}
 		}
 
 		public AnnotatedClassType getClassType(XClass clazz) {
 			AnnotatedClassType type = classTypes.get( clazz.getName() );
 			if ( type == null ) {
 				return addClassType( clazz );
 			}
 			else {
 				return type;
 			}
 		}
 
 		//FIXME should be private but is part of the ExtendedMapping contract
 
 		public AnnotatedClassType addClassType(XClass clazz) {
 			AnnotatedClassType type;
 			if ( clazz.isAnnotationPresent( Entity.class ) ) {
 				type = AnnotatedClassType.ENTITY;
 			}
 			else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE;
 			}
 			else if ( clazz.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
 			}
 			else {
 				type = AnnotatedClassType.NONE;
 			}
 			classTypes.put( clazz.getName(), type );
 			return type;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Map<Table, List<String[]>> getTableUniqueConstraints() {
 			final Map<Table, List<String[]>> deprecatedStructure = new HashMap<Table, List<String[]>>(
 					CollectionHelper.determineProperSizing( getUniqueConstraintHoldersByTable() ),
 					CollectionHelper.LOAD_FACTOR
 			);
 			for ( Map.Entry<Table, List<UniqueConstraintHolder>> entry : getUniqueConstraintHoldersByTable().entrySet() ) {
 				List<String[]> columnsPerConstraint = new ArrayList<String[]>(
 						CollectionHelper.determineProperSizing( entry.getValue().size() )
 				);
 				deprecatedStructure.put( entry.getKey(), columnsPerConstraint );
 				for ( UniqueConstraintHolder holder : entry.getValue() ) {
 					columnsPerConstraint.add( holder.getColumns() );
 				}
 			}
 			return deprecatedStructure;
 		}
 
 		public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable() {
 			return uniqueConstraintHoldersByTable;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		public void addUniqueConstraints(Table table, List uniqueConstraints) {
 			List<UniqueConstraintHolder> constraintHolders = new ArrayList<UniqueConstraintHolder>(
 					CollectionHelper.determineProperSizing( uniqueConstraints.size() )
 			);
 
 			int keyNameBase = determineCurrentNumberOfUniqueConstraintHolders( table );
 			for ( String[] columns : ( List<String[]> ) uniqueConstraints ) {
 				final String keyName = "key" + keyNameBase++;
 				constraintHolders.add(
 						new UniqueConstraintHolder().setName( keyName ).setColumns( columns )
 				);
 			}
 			addUniqueConstraintHolders( table, constraintHolders );
 		}
 
 		private int determineCurrentNumberOfUniqueConstraintHolders(Table table) {
 			List currentHolders = getUniqueConstraintHoldersByTable().get( table );
 			return currentHolders == null
 					? 0
 					: currentHolders.size();
 		}
 
 		public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
 			List<UniqueConstraintHolder> holderList = getUniqueConstraintHoldersByTable().get( table );
 			if ( holderList == null ) {
 				holderList = new ArrayList<UniqueConstraintHolder>();
 				getUniqueConstraintHoldersByTable().put( table, holderList );
 			}
 			holderList.addAll( uniqueConstraintHolders );
 		}
 
 		public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
 			mappedByResolver.put( entityName + "." + propertyName, inversePropertyName );
 		}
 
 		public String getFromMappedBy(String entityName, String propertyName) {
 			return mappedByResolver.get( entityName + "." + propertyName );
 		}
 
 		public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
 			propertyRefResolver.put( entityName + "." + propertyName, propertyRef );
 		}
 
 		public String getPropertyReferencedAssociation(String entityName, String propertyName) {
 			return propertyRefResolver.get( entityName + "." + propertyName );
 		}
 
 		public ReflectionManager getReflectionManager() {
 			return reflectionManager;
 		}
 
 		public Map getClasses() {
 			return classes;
 		}
 
 		public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException {
 			if ( anyMetaDefs.containsKey( defAnn.name() ) ) {
 				throw new AnnotationException( "Two @AnyMetaDef with the same name defined: " + defAnn.name() );
 			}
 			anyMetaDefs.put( defAnn.name(), defAnn );
 		}
 
 		public AnyMetaDef getAnyMetaDef(String name) {
 			return anyMetaDefs.get( name );
 		}
 	}
 
 	final ObjectNameNormalizer normalizer = new ObjectNameNormalizerImpl();
 
 	final class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
 		public boolean isUseQuotedIdentifiersGlobally() {
 			//Do not cache this value as we lazily set it in Hibernate Annotation (AnnotationConfiguration)
 			//TODO use a dedicated protected useQuotedIdentifier flag in Configuration (overriden by AnnotationConfiguration)
 			String setting = (String) properties.get( Environment.GLOBALLY_QUOTED_IDENTIFIERS );
 			return setting != null && Boolean.valueOf( setting ).booleanValue();
 		}
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 	}
 
 	protected class MetadataSourceQueue implements Serializable {
 		private LinkedHashMap<XmlDocument, Set<String>> hbmMetadataToEntityNamesMap
 				= new LinkedHashMap<XmlDocument, Set<String>>();
 		private Map<String, XmlDocument> hbmMetadataByEntityNameXRef = new HashMap<String, XmlDocument>();
 
 		//XClass are not serializable by default
 		private transient List<XClass> annotatedClasses = new ArrayList<XClass>();
 		//only used during the secondPhaseCompile pass, hence does not need to be serialized
 		private transient Map<String, XClass> annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			ois.defaultReadObject();
 			annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 			//build back annotatedClasses
 			@SuppressWarnings( "unchecked" )
 			List<Class> serializableAnnotatedClasses = (List<Class>) ois.readObject();
 			annotatedClasses = new ArrayList<XClass>( serializableAnnotatedClasses.size() );
 			for ( Class clazz : serializableAnnotatedClasses ) {
 				annotatedClasses.add( reflectionManager.toXClass( clazz ) );
 			}
 		}
 
 		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 			out.defaultWriteObject();
 			List<Class> serializableAnnotatedClasses = new ArrayList<Class>( annotatedClasses.size() );
 			for ( XClass xClass : annotatedClasses ) {
 				serializableAnnotatedClasses.add( reflectionManager.toClass( xClass ) );
 			}
 			out.writeObject( serializableAnnotatedClasses );
 		}
 
 		public void add(XmlDocument metadataXml) {
 			final Document document = metadataXml.getDocumentTree();
 			final Element hmNode = document.getRootElement();
 			Attribute packNode = hmNode.attribute( "package" );
 			String defaultPackage = packNode != null ? packNode.getValue() : "";
 			Set<String> entityNames = new HashSet<String>();
 			findClassNames( defaultPackage, hmNode, entityNames );
 			for ( String entity : entityNames ) {
 				hbmMetadataByEntityNameXRef.put( entity, metadataXml );
 			}
 			this.hbmMetadataToEntityNamesMap.put( metadataXml, entityNames );
 		}
 
 		private void findClassNames(String defaultPackage, Element startNode, Set<String> names) {
 			// if we have some extends we need to check if those classes possibly could be inside the
 			// same hbm.xml file...
 			Iterator[] classes = new Iterator[4];
 			classes[0] = startNode.elementIterator( "class" );
 			classes[1] = startNode.elementIterator( "subclass" );
 			classes[2] = startNode.elementIterator( "joined-subclass" );
 			classes[3] = startNode.elementIterator( "union-subclass" );
 
 			Iterator classIterator = new JoinedIterator( classes );
 			while ( classIterator.hasNext() ) {
 				Element element = ( Element ) classIterator.next();
 				String entityName = element.attributeValue( "entity-name" );
 				if ( entityName == null ) {
 					entityName = getClassName( element.attribute( "name" ), defaultPackage );
 				}
 				names.add( entityName );
 				findClassNames( defaultPackage, element, names );
 			}
 		}
 
 		private String getClassName(Attribute name, String defaultPackage) {
 			if ( name == null ) {
 				return null;
 			}
 			String unqualifiedName = name.getValue();
 			if ( unqualifiedName == null ) {
 				return null;
 			}
 			if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 				return defaultPackage + '.' + unqualifiedName;
 			}
 			return unqualifiedName;
 		}
 
 		public void add(XClass annotatedClass) {
 			annotatedClasses.add( annotatedClass );
 		}
 
 		protected void syncAnnotatedClasses() {
 			final Iterator<XClass> itr = annotatedClasses.iterator();
 			while ( itr.hasNext() ) {
 				final XClass annotatedClass = itr.next();
 				if ( annotatedClass.isAnnotationPresent( Entity.class ) ) {
 					annotatedClassesByEntityNameMap.put( annotatedClass.getName(), annotatedClass );
 					continue;
 				}
 
 				if ( !annotatedClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 					itr.remove();
 				}
 			}
 		}
 
 		protected void processMetadata(List<MetadataSourceType> order) {
 			syncAnnotatedClasses();
 
 			for ( MetadataSourceType type : order ) {
 				if ( MetadataSourceType.HBM.equals( type ) ) {
 					processHbmXmlQueue();
 				}
 				else if ( MetadataSourceType.CLASS.equals( type ) ) {
 					processAnnotatedClassesQueue();
 				}
 			}
 		}
 
 		private void processHbmXmlQueue() {
 			LOG.debug( "Processing hbm.xml files" );
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), Collections.EMPTY_MAP, entityNames );
 			}
 			catch ( MappingException me ) {
 				throw new InvalidMappingException(
 						metadataXml.getOrigin().getType(),
 						metadataXml.getOrigin().getName(),
 						me
 				);
 			}
 
 			for ( String entityName : entityNames ) {
 				if ( annotatedClassesByEntityNameMap.containsKey( entityName ) ) {
 					annotatedClasses.remove( annotatedClassesByEntityNameMap.get( entityName ) );
 					annotatedClassesByEntityNameMap.remove( entityName );
 				}
 			}
 		}
 
 		private void processAnnotatedClassesQueue() {
 			LOG.debug( "Process annotated classes" );
 			//bind classes in the correct order calculating some inheritance state
 			List<XClass> orderedClasses = orderAndFillHierarchy( annotatedClasses );
 			Mappings mappings = createMappings();
 			Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(
 					orderedClasses, mappings
 			);
 
 
 			for ( XClass clazz : orderedClasses ) {
 				AnnotationBinder.bindClass( clazz, inheritanceStatePerClass, mappings );
 
 				final String entityName = clazz.getName();
 				if ( hbmMetadataByEntityNameXRef.containsKey( entityName ) ) {
 					hbmMetadataToEntityNamesMap.remove( hbmMetadataByEntityNameXRef.get( entityName ) );
 					hbmMetadataByEntityNameXRef.remove( entityName );
 				}
 			}
 			annotatedClasses.clear();
 			annotatedClassesByEntityNameMap.clear();
 		}
 
 		private List<XClass> orderAndFillHierarchy(List<XClass> original) {
 			List<XClass> copy = new ArrayList<XClass>( original );
 			insertMappedSuperclasses( original, copy );
 
 			// order the hierarchy
 			List<XClass> workingCopy = new ArrayList<XClass>( copy );
 			List<XClass> newList = new ArrayList<XClass>( copy.size() );
 			while ( workingCopy.size() > 0 ) {
 				XClass clazz = workingCopy.get( 0 );
 				orderHierarchy( workingCopy, newList, copy, clazz );
 			}
 			return newList;
 		}
 
 		private void insertMappedSuperclasses(List<XClass> original, List<XClass> copy) {
 			for ( XClass clazz : original ) {
 				XClass superClass = clazz.getSuperclass();
 				while ( superClass != null
 						&& !reflectionManager.equals( superClass, Object.class )
 						&& !copy.contains( superClass ) ) {
 					if ( superClass.isAnnotationPresent( Entity.class )
 							|| superClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 						copy.add( superClass );
 					}
 					superClass = superClass.getSuperclass();
 				}
 			}
 		}
 
 		private void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
 			if ( clazz == null || reflectionManager.equals( clazz, Object.class ) ) {
 				return;
 			}
 			//process superclass first
 			orderHierarchy( copy, newList, original, clazz.getSuperclass() );
 			if ( original.contains( clazz ) ) {
 				if ( !newList.contains( clazz ) ) {
 					newList.add( clazz );
 				}
 				copy.remove( clazz );
 			}
 		}
 
 		public boolean isEmpty() {
 			return hbmMetadataToEntityNamesMap.isEmpty() && annotatedClasses.isEmpty();
 		}
 
 	}
 
 
 	public static final MetadataSourceType[] DEFAULT_ARTEFACT_PROCESSING_ORDER = new MetadataSourceType[] {
 			MetadataSourceType.HBM,
 			MetadataSourceType.CLASS
 	};
 
 	private List<MetadataSourceType> metadataSourcePrecedence;
 
 	private List<MetadataSourceType> determineMetadataSourcePrecedence() {
 		if ( metadataSourcePrecedence.isEmpty()
 				&& StringHelper.isNotEmpty( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) ) ) {
 			metadataSourcePrecedence = parsePrecedence( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) );
 		}
 		if ( metadataSourcePrecedence.isEmpty() ) {
 			metadataSourcePrecedence = Arrays.asList( DEFAULT_ARTEFACT_PROCESSING_ORDER );
 		}
 		metadataSourcePrecedence = Collections.unmodifiableList( metadataSourcePrecedence );
 
 		return metadataSourcePrecedence;
 	}
 
 	public void setPrecedence(String precedence) {
 		this.metadataSourcePrecedence = parsePrecedence( precedence );
 	}
 
 	private List<MetadataSourceType> parsePrecedence(String s) {
 		if ( StringHelper.isEmpty( s ) ) {
 			return Collections.emptyList();
 		}
 		StringTokenizer precedences = new StringTokenizer( s, ",; ", false );
 		List<MetadataSourceType> tmpPrecedences = new ArrayList<MetadataSourceType>();
 		while ( precedences.hasMoreElements() ) {
 			tmpPrecedences.add( MetadataSourceType.parsePrecedence( ( String ) precedences.nextElement() ) );
 		}
 		return tmpPrecedences;
 	}
 
 	private static class CacheHolder {
 		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.isClass = isClass;
 			this.cacheLazy = cacheLazy;
 		}
 
 		public String role;
 		public String usage;
 		public String region;
 		public boolean isClass;
 		public boolean cacheLazy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
index 92a9cf9136..2aa183c06d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
@@ -1,777 +1,785 @@
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
 
 import javax.persistence.AttributeConverter;
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.type.TypeResolver;
 
 /**
  * A collection of mappings from classes and collections to relational database tables.  Represents a single
  * <tt>&lt;hibernate-mapping&gt;</tt> element.
  * <p/>
  * todo : the statement about this representing a single mapping element is simply not true if it was ever the case.
  * this contract actually represents 3 scopes of information: <ol>
  * <li><i>bounded</i> state : this is information which is indeed scoped by a single mapping</li>
  * <li><i>unbounded</i> state : this is information which is Configuration wide (think of metadata repository)</li>
  * <li><i>transient</i> state : state which changed at its own pace (naming strategy)</li>
  * </ol>
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Mappings {
 	/**
 	 * Retrieve the type resolver in effect.
 	 *
 	 * @return The type resolver.
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get the current naming strategy.
 	 *
 	 * @return The current naming strategy.
 	 */
 	public NamingStrategy getNamingStrategy();
 
 	/**
 	 * Set the current naming strategy.
 	 *
 	 * @param namingStrategy The naming strategy to use.
 	 */
 	public void setNamingStrategy(NamingStrategy namingStrategy);
 
 	/**
 	 * Returns the currently bound default schema name.
 	 *
 	 * @return The currently bound schema name
 	 */
 	public String getSchemaName();
 
 	/**
 	 * Sets the currently bound default schema name.
 	 *
 	 * @param schemaName The schema name to bind as the current default.
 	 */
 	public void setSchemaName(String schemaName);
 
 	/**
 	 * Returns the currently bound default catalog name.
 	 *
 	 * @return The currently bound catalog name, or null if none.
 	 */
 	public String getCatalogName();
 
     /**
      * Sets the currently bound default catalog name.
 	 *
      * @param catalogName The catalog name to use as the current default.
      */
     public void setCatalogName(String catalogName);
 
 	/**
 	 * Get the currently bound default package name.
 	 *
 	 * @return The currently bound default package name
 	 */
 	public String getDefaultPackage();
 
 	/**
 	 * Set the current default package name.
 	 *
 	 * @param defaultPackage The package name to set as the current default.
 	 */
 	public void setDefaultPackage(String defaultPackage);
 
 	/**
 	 * Determine whether auto importing of entity names is currently enabled.
 	 *
 	 * @return True if currently enabled; false otherwise.
 	 */
 	public boolean isAutoImport();
 
 	/**
 	 * Set whether to enable auto importing of entity names.
 	 *
 	 * @param autoImport True to enable; false to diasable.
 	 * @see #addImport
 	 */
 	public void setAutoImport(boolean autoImport);
 
 	/**
 	 * Determine whether default laziness is currently enabled.
 	 *
 	 * @return True if enabled, false otherwise.
 	 */
 	public boolean isDefaultLazy();
 
 	/**
 	 * Set whether to enable default laziness.
 	 *
 	 * @param defaultLazy True to enable, false to disable.
 	 */
 	public void setDefaultLazy(boolean defaultLazy);
 
 	/**
 	 * Get the current default cascade style.
 	 *
 	 * @return The current default cascade style.
 	 */
 	public String getDefaultCascade();
 
 	/**
 	 * Sets the current default cascade style.
 	 * .
 	 * @param defaultCascade The cascade style to set as the current default.
 	 */
 	public void setDefaultCascade(String defaultCascade);
 
 	/**
 	 * Get the current default property access style.
 	 *
 	 * @return The current default property access style.
 	 */
 	public String getDefaultAccess();
 
 	/**
 	 * Sets the current default property access style.
 	 *
 	 * @param defaultAccess The access style to use as the current default.
 	 */
 	public void setDefaultAccess(String defaultAccess);
 
 
 	/**
 	 * Retrieves an iterator over the entity metadata present in this repository.
 	 *
 	 * @return Iterator over class metadata.
 	 */
 	public Iterator<PersistentClass> iterateClasses();
 
 	/**
 	 * Retrieves the entity mapping metadata for the given entity name.
 	 *
 	 * @param entityName The entity name for which to retrieve the metadata.
 	 * @return The entity mapping metadata, or null if none found matching given entity name.
 	 */
 	public PersistentClass getClass(String entityName);
 
 	/**
 	 * Retrieves the entity mapping metadata for the given entity name, potentially accounting
 	 * for imports.
 	 *
 	 * @param entityName The entity name for which to retrieve the metadata.
 	 * @return The entity mapping metadata, or null if none found matching given entity name.
 	 */
 	public PersistentClass locatePersistentClassByEntityName(String entityName);
 
 	/**
 	 * Add entity mapping metadata.
 	 *
 	 * @param persistentClass The entity metadata
 	 * @throws DuplicateMappingException Indicates there4 was already an extry
 	 * corresponding to the given entity name.
 	 */
 	public void addClass(PersistentClass persistentClass) throws DuplicateMappingException;
 
 	/**
 	 * Adds an import (HQL entity rename) to the repository.
 	 *
 	 * @param entityName The entity name being renamed.
 	 * @param rename The rename
 	 * @throws DuplicateMappingException If rename already is mapped to another
 	 * entity name in this repository.
 	 */
 	public void addImport(String entityName, String rename) throws DuplicateMappingException;
 
 	/**
 	 * Retrieves the collection mapping metadata for the given collection role.
 	 *
 	 * @param role The collection role for which to retrieve the metadata.
 	 * @return The collection mapping metadata, or null if no matching collection role found.
 	 */
 	public Collection getCollection(String role);
 
 	/**
 	 * Returns an iterator over collection metadata.
 	 *
 	 * @return Iterator over collection metadata.
 	 */
 	public Iterator<Collection> iterateCollections();
 
 	/**
 	 * Add collection mapping metadata to this repository.
 	 *
 	 * @param collection The collection metadata
 	 * @throws DuplicateMappingException Indicates there was already an entry
 	 * corresponding to the given collection role
 	 */
 	public void addCollection(Collection collection) throws DuplicateMappingException;
 
 	/**
 	 * Returns the named table metadata.
 	 *
 	 * @param schema The named schema in which the table belongs (or null).
 	 * @param catalog The named catalog in which the table belongs (or null).
 	 * @param name The table name
 	 * @return The table metadata, or null.
 	 */
 	public Table getTable(String schema, String catalog, String name);
 
 	/**
 	 * Returns an iterator over table metadata.
 	 *
 	 * @return Iterator over table metadata.
 	 */
 	public Iterator<Table> iterateTables();
 
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
 	 * @return The created table metadata, or the existing reference.
 	 */
 	public Table addTable(String schema, String catalog, String name, String subselect, boolean isAbstract);
 
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
 	 * @return The created table metadata.
 	 * @throws DuplicateMappingException If such a table mapping already exists.
 	 */
 	public Table addDenormalizedTable(String schema, String catalog, String name, boolean isAbstract, String subselect, Table includedTable)
 			throws DuplicateMappingException;
 
 	/**
 	 * Get named query metadata by name.
 	 *
 	 * @param name The named query name
 	 * @return The query metadata, or null.
 	 */
 	public NamedQueryDefinition getQuery(String name);
 
 	/**
 	 * Adds metadata for a named query to this repository.
 	 *
 	 * @param name The name
 	 * @param query The metadata
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Get named SQL query metadata.
 	 *
 	 * @param name The named SQL query name.
 	 * @return The meatdata, or null if none found.
 	 */
 	public NamedSQLQueryDefinition getSQLQuery(String name);
 
 	/**
 	 * Adds metadata for a named SQL query to this repository.
 	 *
 	 * @param name The name
 	 * @param query The metadata
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException;
 
 	/**
 	 * Get the metadata for a named SQL result set mapping.
 	 *
 	 * @param name The mapping name.
 	 * @return The SQL result set mapping metadat, or null if none found.
 	 */
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Adds the metadata for a named SQL result set mapping to this repository.
 	 *
 	 * @param sqlResultSetMapping The metadata
 	 * @throws DuplicateMappingException If metadata for another SQL result mapping was
 	 * already found under the given name.
 	 */
 	public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException;
 
 	/**
 	 * Retrieve a type definition by name.
 	 *
 	 * @param typeName The name of the type definition to retrieve.
 	 * @return The type definition, or null if none found.
 	 */
 	public TypeDef getTypeDef(String typeName);
 
 	/**
 	 * Adds a type definition to this metadata repository.
 	 *
 	 * @param typeName The type name.
 	 * @param typeClass The class implementing the {@link org.hibernate.type.Type} contract.
 	 * @param paramMap Map of parameters to be used to configure the type after instantiation.
 	 */
 	public void addTypeDef(String typeName, String typeClass, Properties paramMap);
 
 	/**
 	 * Retrieves the copmplete map of filter definitions.
 	 *
 	 * @return The filter definition map.
 	 */
 	public Map getFilterDefinitions();
 
 	/**
 	 * Retrieves a filter definition by name.
 	 *
 	 * @param name The name of the filter definition to retrieve.
 	 * @return The filter definition, or null.
 	 */
 	public FilterDefinition getFilterDefinition(String name);
 
 	/**
 	 * Adds a filter definition to this repository.
 	 *
 	 * @param definition The filter definition to add.
 	 */
 	public void addFilterDefinition(FilterDefinition definition);
 
 	/**
 	 * Retrieves a fetch profile by either finding one currently in this repository matching the given name
 	 * or by creating one (and adding it).
 	 *
 	 * @param name The name of the profile.
 	 * @param source The source from which this profile is named.
 	 * @return The fetch profile metadata.
 	 */
 	public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source);
 
 	/**
 	 * @deprecated To fix misspelling; use {@link #iterateAuxiliaryDatabaseObjects} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects();
 
 	/**
 	 * Retrieves an iterator over the metadata pertaining to all auxiliary database objects int this repository.
 	 *
 	 * @return Iterator over the auxiliary database object metadata.
 	 */
 	public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects();
 
 	/**
 	 * @deprecated To fix misspelling; use {@link #iterateAuxiliaryDatabaseObjectsInReverse} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse();
 
 	/**
 	 * Same as {@link #iterateAuxiliaryDatabaseObjects()} except that here the iterator is reversed.
 	 *
 	 * @return The reversed iterator.
 	 */
 	public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse();
 
 	/**
 	 * Add metadata pertaining to an auxiliary database object to this repository.
 	 *
 	 * @param auxiliaryDatabaseObject The metadata.
 	 */
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
 	/**
 	 * Get the logical table name mapped for the given physical table.
 	 *
 	 * @param table The table for which to determine the logical name.
 	 * @return The logical name.
 	 * @throws MappingException Indicates that no logical name was bound for the given physical table.
 	 */
 	public String getLogicalTableName(Table table) throws MappingException;
 
 	/**
 	 * Adds a table binding to this repository.
 	 *
 	 * @param schema The schema in which the table belongs (may be null).
 	 * @param catalog The catalog in which the table belongs (may be null).
 	 * @param logicalName The logical table name.
 	 * @param physicalName The physical table name.
 	 * @param denormalizedSuperTable ???
 	 * @throws DuplicateMappingException Indicates physical table was already bound to another logical name.
 	 */
 	public void addTableBinding(
 			String schema,
 			String catalog,
 			String logicalName,
 			String physicalName,
 			Table denormalizedSuperTable) throws DuplicateMappingException;
 
 	/**
 	 * Binds the given 'physicalColumn' to the give 'logicalName' within the given 'table'.
 	 *
 	 * @param logicalName The logical column name binding.
 	 * @param physicalColumn The physical column metadata.
 	 * @param table The table metadata.
 	 * @throws DuplicateMappingException Indicates a duplicate binding for either the physical column name
 	 * or the logical column name.
 	 */
 	public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException;
 
 	/**
 	 * Find the physical column name for the given logical column name within the given table.
 	 *
 	 * @param logicalName The logical name binding.
 	 * @param table The table metatdata.
 	 * @return The physical column name.
 	 * @throws MappingException Indicates that no such binding was found.
 	 */
 	public String getPhysicalColumnName(String logicalName, Table table) throws MappingException;
 
 	/**
 	 * Find the logical column name against whcih the given physical column name was bound within the given table.
 	 *
 	 * @param physicalName The physical column name
 	 * @param table The table metadata.
 	 * @return The logical column name.
 	 * @throws MappingException Indicates that no such binding was found.
 	 */
 	public String getLogicalColumnName(String physicalName, Table table) throws MappingException;
 
 	/**
 	 * Adds a second-pass to the end of the current queue.
 	 *
 	 * @param sp The second pass to add.
 	 */
 	public void addSecondPass(SecondPass sp);
 
 	/**
 	 * Adds a second pass.
 	 * @param sp The second pass to add.
 	 * @param onTopOfTheQueue True to add to the beginning of the queue; false to add to the end.
 	 */
 	public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue);
 
 	/**
 	 * Locate the AttributeConverterDefinition corresponding to the given AttributeConverter Class.
 	 *
 	 * @param attributeConverterClass The AttributeConverter Class for which to get the definition
 	 *
 	 * @return The corresponding AttributeConverter definition; will return {@code null} if no corresponding
 	 * definition found.
 	 */
 	public AttributeConverterDefinition locateAttributeConverter(Class attributeConverterClass);
 
 	/**
 	 * All all AttributeConverter definitions
 	 *
 	 * @return The collection of all AttributeConverter definitions.
 	 */
 	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
 
 	/**
 	 * Represents a property-ref mapping.
 	 * <p/>
 	 * TODO : currently needs to be exposed because Configuration needs access to it for second-pass processing
 	 */
 	public static final class PropertyReference implements Serializable {
 		public final String referencedClass;
 		public final String propertyName;
 		public final boolean unique;
 
 		public PropertyReference(String referencedClass, String propertyName, boolean unique) {
 			this.referencedClass = referencedClass;
 			this.propertyName = propertyName;
 			this.unique = unique;
 		}
 	}
 
 	/**
 	 * Adds a property reference binding to this repository.
 	 *
 	 * @param referencedClass The referenced entity name.
 	 * @param propertyName The referenced property name.
 	 */
 	public void addPropertyReference(String referencedClass, String propertyName);
 
 	/**
 	 * Adds a property reference binding to this repository where said proeprty reference is marked as unique.
 	 *
 	 * @param referencedClass The referenced entity name.
 	 * @param propertyName The referenced property name.
 	 */
 	public void addUniquePropertyReference(String referencedClass, String propertyName);
 
 	/**
 	 * Adds an entry to the extends queue queue.
 	 *
 	 * @param entry The entry to add.
 	 */
 	public void addToExtendsQueue(ExtendsQueueEntry entry);
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this mapping.
 	 *
 	 * @return The IdentifierGeneratorFactory
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory();
 
 	/**
 	 * add a new MappedSuperclass
 	 * This should not be called if the MappedSuperclass already exists
 	 * (it would be erased)
 	 * @param type type corresponding to the Mappedsuperclass
 	 * @param mappedSuperclass MappedSuperclass
 	 */
 	public void addMappedSuperclass(Class type, org.hibernate.mapping.MappedSuperclass mappedSuperclass);
 
 	/**
 	 * Get a MappedSuperclass or null if not mapped
 	 *
 	 * @param type class corresponding to the MappedSuperclass
 	 * @return the MappedSuperclass
 	 */
 	org.hibernate.mapping.MappedSuperclass getMappedSuperclass(Class type);
 
 	/**
 	 * Retrieve the database identifier normalizer for this context.
 	 *
 	 * @return The normalizer.
 	 */
 	public ObjectNameNormalizer getObjectNameNormalizer();
 
 	/**
 	 * Retrieve the configuration properties currently in effect.
 	 *
 	 * @return The configuration properties
 	 */
 	public Properties getConfigurationProperties();
 
 
 
 
 
 
 
 
 
 	/**
 	 * Adds a default id generator.
 	 *
 	 * @param generator The id generator
 	 */
 	public void addDefaultGenerator(IdGenerator generator);
 
 	/**
 	 * Retrieve the id-generator by name.
 	 *
 	 * @param name The generator name.
 	 *
 	 * @return The generator, or null.
 	 */
 	public IdGenerator getGenerator(String name);
 
 	/**
 	 * Try to find the generator from the localGenerators
 	 * and then from the global generator list
 	 *
 	 * @param name generator name
 	 * @param localGenerators local generators
 	 *
 	 * @return the appropriate idgenerator or null if not found
 	 */
 	public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators);
 
 	/**
 	 * Add a generator.
 	 *
 	 * @param generator The generator to add.
 	 */
 	public void addGenerator(IdGenerator generator);
 
 	/**
 	 * Add a generator table properties.
 	 *
 	 * @param name The generator name
 	 * @param params The generator table properties.
 	 */
 	public void addGeneratorTable(String name, Properties params);
 
 	/**
 	 * Retrieve the properties related to a generator table.
 	 *
 	 * @param name generator name
 	 * @param localGeneratorTables local generator tables
 	 *
 	 * @return The properties, or null.
 	 */
 	public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables);
 
 	/**
 	 * Retrieve join metadata for a particular persistent entity.
 	 *
 	 * @param entityName The entity name
 	 *
 	 * @return The join metadata
 	 */
 	public Map<String, Join> getJoins(String entityName);
 
 	/**
 	 * Add join metadata for a persistent entity.
 	 *
 	 * @param persistentClass The persistent entity metadata.
 	 * @param joins The join metadata to add.
 	 *
 	 * @throws MappingException
 	 */
 	public void addJoins(PersistentClass persistentClass, Map<String, Join> joins);
 
 	/**
 	 * Get and maintain a cache of class type.
 	 *
 	 * @param clazz The XClass mapping
 	 *
 	 * @return The class type.
 	 */
 	public AnnotatedClassType getClassType(XClass clazz);
 
 	/**
 	 * FIXME should be private but will this break things?
 	 * Add a class type.
 	 *
 	 * @param clazz The XClass mapping.
 	 *
 	 * @return The class type.
 	 */
 	public AnnotatedClassType addClassType(XClass clazz);
 
 	/**
 	 * @deprecated Use {@link #getUniqueConstraintHoldersByTable} instead
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public Map<Table, List<String[]>> getTableUniqueConstraints();
 
 	public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable();
 
 	/**
 	 * @deprecated Use {@link #addUniqueConstraintHolders} instead
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public void addUniqueConstraints(Table table, List uniqueConstraints);
 
 	public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders);
 
 	public void addMappedBy(String entityName, String propertyName, String inversePropertyName);
 
 	public String getFromMappedBy(String entityName, String propertyName);
 
 	public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef);
 
 	public String getPropertyReferencedAssociation(String entityName, String propertyName);
 
 	public ReflectionManager getReflectionManager();
 
 	public void addDefaultQuery(String name, NamedQueryDefinition query);
 
 	public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query);
 
 	public void addDefaultResultSetMapping(ResultSetMappingDefinition definition);
 
 	public Map getClasses();
 
 	public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException;
 
 	public AnyMetaDef getAnyMetaDef(String name);
 
 	public boolean isInSecondPass();
 
 	/**
 	 * Return the property annotated with @MapsId("propertyName") if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName);
 
 	public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property);
 
 	public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue);
 
 	public boolean isSpecjProprietarySyntaxEnabled();
 
 	/**
 	 * Should we use the new generator strategy mappings.  This is controlled by the
 	 * {@link AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS} setting.
 	 *
 	 * @return True if the new generators should be used, false otherwise.
 	 */
 	public boolean useNewGeneratorMappings();
 
 	/**
+	 * Should we use nationalized variants of character data by default?  This is controlled by the
+	 * {@link AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA} setting.
+	 *
+	 * @return {@code true} if nationalized character data should be used by default; {@code false} otherwise.
+	 */
+	public boolean useNationalizedCharacterData();
+
+	/**
 	 * Return the property annotated with @ToOne and @Id if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData property);
 
 	public boolean forceDiscriminatorInSelectsByDefault();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 86469b3f45..fca9a1849b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,639 +1,676 @@
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
 package org.hibernate.cfg.annotations;
 
 import java.io.Serializable;
 import java.lang.reflect.TypeVariable;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Properties;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Convert;
 import javax.persistence.Converts;
 import javax.persistence.Enumerated;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
+import org.hibernate.annotations.Nationalized;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.annotations.common.util.ReflectHelper;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
+import org.hibernate.type.CharacterArrayNClobType;
+import org.hibernate.type.CharacterNCharType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
+import org.hibernate.type.PrimitiveCharacterArrayNClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.StringNVarcharType;
 import org.hibernate.type.WrappedMaterializedBlobType;
 import org.hibernate.usertype.DynamicParameterizedType;
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
 	private String defaultType = "";
 	private Properties typeParameters = new Properties();
 	private Mappings mappings;
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
 	private XProperty xproperty;
 	private AccessType accessType;
 
 	private AttributeConverterDefinition attributeConverterDefinition;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 
 		if ( defaultType.length() == 0 ) {
 			defaultType = returnedClassName;
 		}
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		this.columns = columns;
 	}
 
 
 	public void setPersistentClassName(String persistentClassName) {
 		this.persistentClassName = persistentClassName;
 	}
 
 	//TODO execute it lazily to be order safe
 
 	public void setType(XProperty property, XClass returnedClass, String declaringClassName) {
 		if ( returnedClass == null ) {
 			// we cannot guess anything
 			return;
 		}
 		XClass returnedClassOrElement = returnedClass;
                 boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
 		this.xproperty = property;
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
 
+		final boolean isNationalized = property.isAnnotationPresent( Nationalized.class )
+				|| mappings.useNationalizedCharacterData();
+
 		Type annType = property.getAnnotation( Type.class );
 		if ( annType != null ) {
 			setExplicitType( annType );
 			type = explicitType;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
 				isDate = false;
 			}
 			else {
 				throw new AnnotationException(
 						"@Temporal should only be set on a java.util.Date or java.util.Calendar property: "
 								+ StringHelper.qualify( persistentClassName, propertyName )
 				);
 			}
 			final TemporalType temporalType = getTemporalType( property );
 			switch ( temporalType ) {
 				case DATE:
 					type = isDate ? "date" : "calendar_date";
 					break;
 				case TIME:
 					type = "time";
 					if ( !isDate ) {
 						throw new NotYetImplementedException(
 								"Calendar cannot persist TIME only"
 										+ StringHelper.qualify( persistentClassName, propertyName )
 						);
 					}
 					break;
 				case TIMESTAMP:
 					type = isDate ? "timestamp" : "calendar";
 					break;
 				default:
 					throw new AssertionFailure( "Unknown temporal type: " + temporalType );
 			}
 			explicitType = type;
 		}
 		else if ( property.isAnnotationPresent( Lob.class ) ) {
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
-				type = "clob";
+				type = isNationalized
+						? StandardBasicTypes.NCLOB.getName()
+						: StandardBasicTypes.CLOB.getName();
+			}
+			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.NClob.class ) ) {
+				type = StandardBasicTypes.NCLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
-				type = StandardBasicTypes.MATERIALIZED_CLOB.getName();
+				type = isNationalized
+						? StandardBasicTypes.MATERIALIZED_NCLOB.getName()
+						: StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
-				type = CharacterArrayClobType.class.getName();
+				type = isNationalized
+						? CharacterArrayNClobType.class.getName()
+						: CharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
-				type = PrimitiveCharacterArrayClobType.class.getName();
+				type = isNationalized
+						? PrimitiveCharacterArrayNClobType.class.getName()
+						: PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
 				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( mappings.getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
 			explicitType = type;
 		}
 		else if ( ( !key && property.isAnnotationPresent( Enumerated.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyEnumerated.class ) ) ) {
 			final Class attributeJavaType = mappings.getReflectionManager().toClass( returnedClassOrElement );
 			if ( !Enum.class.isAssignableFrom( attributeJavaType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"Attribute [%s.%s] was annotated as enumerated, but its java type is not an enum [%s]",
 								declaringClassName,
 								xproperty.getName(),
 								attributeJavaType.getName()
 						)
 				);
 			}
 			type = EnumType.class.getName();
 			explicitType = type;
 		}
+		else if ( isNationalized ) {
+			if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
+				// nvarchar
+				type = StringNVarcharType.INSTANCE.getName();
+				explicitType = type;
+			}
+			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) ) {
+				if ( isArray ) {
+					// nvarchar
+					type = StringNVarcharType.INSTANCE.getName();
+				}
+				else {
+					// nchar
+					type = CharacterNCharType.INSTANCE.getName();
+				}
+				explicitType = type;
+			}
+		}
 
 		// implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
 
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
 			}
 		}
 
 		defaultType = BinderHelper.isEmptyAnnotationValue( type ) ? returnedClassName : type;
 		this.typeParameters = typeParameters;
 
 		applyAttributeConverter( property );
 	}
 
 	private void applyAttributeConverter(XProperty property) {
 		final boolean canBeConverted = ! property.isAnnotationPresent( Id.class )
 				&& ! isVersion
 				&& ! isAssociation()
 				&& ! property.isAnnotationPresent( Temporal.class )
 				&& ! property.isAnnotationPresent( Enumerated.class );
 
 		if ( canBeConverted ) {
 			// @Convert annotations take precedence
 			final Convert convertAnnotation = locateConvertAnnotation( property );
 			if ( convertAnnotation != null ) {
 				if ( ! convertAnnotation.disableConversion() ) {
 					attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
 				}
 			}
 			else {
 				attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
 			}
 		}
 	}
 
 	private AttributeConverterDefinition locateAutoApplyAttributeConverter(XProperty property) {
 		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
 		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
 			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
 				return attributeConverterDefinition;
 			}
 		}
 		return null;
 	}
 
 	private boolean isAssociation() {
 		// todo : this information is only known to caller(s), need to pass that information in somehow.
 		// or, is this enough?
 		return referencedEntityName != null;
 	}
 
 	@SuppressWarnings("unchecked")
 	private Convert locateConvertAnnotation(XProperty property) {
 		// first look locally on the property for @Convert
 		Convert localConvertAnnotation = property.getAnnotation( Convert.class );
 		if ( localConvertAnnotation != null ) {
 			return localConvertAnnotation;
 		}
 
 		if ( persistentClassName == null ) {
 			LOG.debug( "Persistent Class name not known during attempt to locate @Convert annotations" );
 			return null;
 		}
 
 		final XClass owner;
 		try {
 			final Class ownerClass = ReflectHelper.classForName( persistentClassName );
 			owner = mappings.getReflectionManager().classForName( persistentClassName, ownerClass  );
 		}
 		catch (ClassNotFoundException e) {
 			throw new AnnotationException( "Unable to resolve Class reference during attempt to locate @Convert annotations" );
 		}
 
 		return lookForEntityDefinedConvertAnnotation( property, owner );
 	}
 
 	private Convert lookForEntityDefinedConvertAnnotation(XProperty property, XClass owner) {
 		if ( owner == null ) {
 			// we have hit the root of the entity hierarchy
 			return null;
 		}
 
 		{
 			Convert convertAnnotation = owner.getAnnotation( Convert.class );
 			if ( convertAnnotation != null && isMatch( convertAnnotation, property ) ) {
 				return convertAnnotation;
 			}
 		}
 
 		{
 			Converts convertsAnnotation = owner.getAnnotation( Converts.class );
 			if ( convertsAnnotation != null ) {
 				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
 					if ( isMatch( convertAnnotation, property ) ) {
 						return convertAnnotation;
 					}
 				}
 			}
 		}
 
 		// finally, look on superclass
 		return lookForEntityDefinedConvertAnnotation( property, owner.getSuperclass() );
 	}
 
 	@SuppressWarnings("unchecked")
 	private boolean isMatch(Convert convertAnnotation, XProperty property) {
 		return property.getName().equals( convertAnnotation.attributeName() )
 				&& isTypeMatch( convertAnnotation.converter(), property );
 	}
 
 	private boolean isTypeMatch(Class<? extends AttributeConverter> attributeConverterClass, XProperty property) {
 		return areTypeMatch(
 				extractEntityAttributeType( attributeConverterClass ),
 				mappings.getReflectionManager().toClass( property.getType() )
 		);
 	}
 
 	private Class extractEntityAttributeType(Class<? extends AttributeConverter> attributeConverterClass) {
 		// this is duplicated in SimpleValue...
 		final TypeVariable[] attributeConverterTypeInformation = attributeConverterClass.getTypeParameters();
 		if ( attributeConverterTypeInformation == null || attributeConverterTypeInformation.length < 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] did not retain parameterized type information"
 			);
 		}
 
 		if ( attributeConverterTypeInformation.length > 2 ) {
 			LOG.debug(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] specified more than 2 parameterized types"
 			);
 		}
 		final Class entityAttributeJavaType = extractType( attributeConverterTypeInformation[0] );
 		if ( entityAttributeJavaType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'entity attribute' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 		return entityAttributeJavaType;
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	private boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType == null ) {
 			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
 		}
 		if ( propertyType == null ) {
 			throw new AnnotationException( "Property defined java type cannot be null" );
 		}
 
 		return converterDefinedType.equals( propertyType )
 				|| arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
 	}
 
 	private boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType.isPrimitive() ) {
 			return getWrapperEquivalent( converterDefinedType ).equals( propertyType );
 		}
 		else if ( propertyType.isPrimitive() ) {
 			return getWrapperEquivalent( propertyType ).equals( converterDefinedType );
 		}
 		return false;
 	}
 
 	private static Class getWrapperEquivalent(Class primitive) {
 		if ( ! primitive.isPrimitive() ) {
 			throw new AssertionFailure( "Passed type for which to locate wrapper equivalent was not a primitive" );
 		}
 
 		if ( boolean.class.equals( primitive ) ) {
 			return Boolean.class;
 		}
 		else if ( char.class.equals( primitive ) ) {
 			return Character.class;
 		}
 		else if ( byte.class.equals( primitive ) ) {
 			return Byte.class;
 		}
 		else if ( short.class.equals( primitive ) ) {
 			return Short.class;
 		}
 		else if ( int.class.equals( primitive ) ) {
 			return Integer.class;
 		}
 		else if ( long.class.equals( primitive ) ) {
 			return Long.class;
 		}
 		else if ( float.class.equals( primitive ) ) {
 			return Float.class;
 		}
 		else if ( double.class.equals( primitive ) ) {
 			return Double.class;
 		}
 
 		throw new AssertionFailure( "Unexpected primitive type (VOID most likely) passed to getWrapperEquivalent" );
 	}
 	
 	private TemporalType getTemporalType(XProperty property) {
 		if ( key ) {
 			MapKeyTemporal ann = property.getAnnotation( MapKeyTemporal.class );
 			return ann.value();
 		}
 		else {
 			Temporal ann = property.getAnnotation( Temporal.class );
 			return ann.value();
 		}
 	}
 
 	public void setExplicitType(String explicitType) {
 		this.explicitType = explicitType;
 	}
 
 	//FIXME raise an assertion failure  if setResolvedTypeMapping(String) and setResolvedTypeMapping(Type) are use at the same time
 
 	public void setExplicitType(Type typeAnn) {
 		if ( typeAnn != null ) {
 			explicitType = typeAnn.type();
 			typeParameters.clear();
 			for ( Parameter param : typeAnn.parameters() ) {
 				typeParameters.setProperty( param.name(), param.value() );
 			}
 		}
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	private void validate() {
 		//TODO check necessary params
 		Ejb3Column.checkPropertyConsistency( columns, propertyName );
 	}
 
 	public SimpleValue make() {
 
 		validate();
 		LOG.debugf( "building SimpleValue for %s", propertyName );
 		if ( table == null ) {
 			table = columns[0].getTable();
 		}
 		simpleValue = new SimpleValue( mappings, table );
 
 		linkWithValue();
 
 		boolean isInSecondPass = mappings.isInSecondPass();
 		SetSimpleValueTypeSecondPass secondPass = new SetSimpleValueTypeSecondPass( this );
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			mappings.addSecondPass( secondPass );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !mappings.isInSecondPass() && referencedEntityName != null ) {
 			mappings.addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, ( Ejb3JoinColumn[] ) columns, simpleValue
 					)
 			);
 		}
 		else {
 			for ( Ejb3Column column : columns ) {
 				column.linkWithValue( simpleValue );
 			}
 		}
 	}
 
 	public void fillSimpleValue() {
 		LOG.debugf( "Setting SimpleValue typeName for %s", propertyName );
                 
 		if ( attributeConverterDefinition != null ) {
 			if ( ! BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];" +
 										"remove @Type or specify @Convert(disableConversion = true)",
 								persistentClassName,
 								propertyName
 						)
 				);
 			}
 			simpleValue.setJpaAttributeConverterDefinition( attributeConverterDefinition );
 		}
 		else {
 			String type;
 			org.hibernate.mapping.TypeDef typeDef;
 
 			if ( !BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				type = explicitType;
 				typeDef = mappings.getTypeDef( type );
 			}
 			else {
 				// try implicit type
 				org.hibernate.mapping.TypeDef implicitTypeDef = mappings.getTypeDef( returnedClassName );
 				if ( implicitTypeDef != null ) {
 					typeDef = implicitTypeDef;
 					type = returnedClassName;
 				}
 				else {
 					typeDef = mappings.getTypeDef( defaultType );
 					type = defaultType;
 				}
 			}
 
 			if ( typeDef != null ) {
 				type = typeDef.getTypeClass();
 				simpleValue.setTypeParameters( typeDef.getParameters() );
 			}
 			if ( typeParameters != null && typeParameters.size() != 0 ) {
 				//explicit type params takes precedence over type def params
 				simpleValue.setTypeParameters( typeParameters );
 			}
 			simpleValue.setTypeName( type );
 		}
 
 		if ( persistentClassName != null || attributeConverterDefinition != null ) {
 			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
 		
 		if ( simpleValue.getTypeName() != null && simpleValue.getTypeName().length() > 0
 				&& simpleValue.getMappings().getTypeResolver().basic( simpleValue.getTypeName() ) == null ) {
 			try {
 				Class typeClass = ReflectHelper.classForName( simpleValue.getTypeName() );
 
 				if ( typeClass != null && DynamicParameterizedType.class.isAssignableFrom( typeClass ) ) {
 					Properties parameters = simpleValue.getTypeParameters();
 					if ( parameters == null ) {
 						parameters = new Properties();
 					}
 					parameters.put( DynamicParameterizedType.IS_DYNAMIC, Boolean.toString( true ) );
 					parameters.put( DynamicParameterizedType.RETURNED_CLASS, returnedClassName );
 					parameters.put( DynamicParameterizedType.IS_PRIMARY_KEY, Boolean.toString( key ) );
 
 					parameters.put( DynamicParameterizedType.ENTITY, persistentClassName );
 					parameters.put( DynamicParameterizedType.XPROPERTY, xproperty );
 					parameters.put( DynamicParameterizedType.PROPERTY, xproperty.getName() );
 					parameters.put( DynamicParameterizedType.ACCESS_TYPE, accessType.getType() );
 					simpleValue.setTypeParameters( parameters );
 				}
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), cnfe );
 			}
 		}
 
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 
 	public AccessType getAccessType() {
 		return accessType;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java
index 2150d73c62..03c9394e46 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/AbstractLobCreator.java
@@ -1,48 +1,54 @@
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
 package org.hibernate.engine.jdbc;
 import java.sql.Blob;
 import java.sql.Clob;
+import java.sql.NClob;
 
 /**
  * Convenient base class for proxy-based LobCreator for handling wrapping.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractLobCreator implements LobCreator {
 	@Override
 	public Blob wrap(Blob blob) {
 		return SerializableBlobProxy.generateProxy( blob );
 	}
 
 	@Override
 	public Clob wrap(Clob clob) {
-		if ( SerializableNClobProxy.isNClob( clob ) ) {
-			return SerializableNClobProxy.generateProxy( clob );
+		if ( NClob.class.isInstance( clob ) ) {
+			return wrap( (NClob) clob );
 		}
 		else {
 			return SerializableClobProxy.generateProxy( clob );
 		}
 	}
+
+	@Override
+	public NClob wrap(NClob nclob) {
+		return SerializableNClobProxy.generateProxy( nclob );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java
index b1f6d39628..e96fe27232 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/LobCreator.java
@@ -1,106 +1,114 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.engine.jdbc;
 import java.io.InputStream;
 import java.io.Reader;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.NClob;
 
 /**
  * Contract for creating various LOB references.
  * 
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public interface LobCreator {
 	/**
 	 * Wrap the given blob in a serializable wrapper.
 	 *
 	 * @param blob The blob to be wrapped.
 	 * @return The wrapped blob which will be castable to {@link Blob} as well as {@link WrappedBlob}.
 	 */
 	public Blob wrap(Blob blob);
 
 	/**
 	 * Wrap the given clob in a serializable wrapper.
 	 *
 	 * @param clob The clob to be wrapped.
 	 * @return The wrapped clob which will be castable to {@link Clob} as well as {@link WrappedClob}.
 	 */
 	public Clob wrap(Clob clob);
 
 	/**
+	 * Wrap the given nclob in a serializable wrapper.
+	 *
+	 * @param nclob The nclob to be wrapped.
+	 * @return The wrapped nclob which will be castable to {@link NClob} as well as {@link WrappedNClob}.
+	 */
+	public NClob wrap(NClob nclob);
+
+	/**
 	 * Create a BLOB reference encapsulating the given byte array.
 	 *
 	 * @param bytes The byte array to wrap as a blob.
 	 * @return The created blob, castable to {@link Blob} as well as {@link BlobImplementer}
 	 */
 	public Blob createBlob(byte[] bytes);
 
 	/**
 	 * Create a BLOB reference encapsulating the given binary stream.
 	 *
 	 * @param stream The binary stream to wrap as a blob.
 	 * @param length The length of the stream.
 	 * @return The created blob, castable to {@link Blob} as well as {@link BlobImplementer}
 	 */
 	public Blob createBlob(InputStream stream, long length);
 
 	/**
 	 * Create a CLOB reference encapsulating the given String data.
 	 *
 	 * @param string The String to wrap as a clob.
 	 * @return The created clob, castable to {@link Clob} as well as {@link ClobImplementer}
 	 */
 	public Clob createClob(String string);
 
 	/**
 	 * Create a CLOB reference encapsulating the given character data.
 	 *
 	 * @param reader The character data reader.
 	 * @param length The length of the reader data.
 	 * @return The created clob, castable to {@link Clob} as well as {@link ClobImplementer}
 	 */
 	public Clob createClob(Reader reader, long length);
 
 	/**
 	 * Create a NCLOB reference encapsulating the given String data.
 	 *
 	 * @param string The String to wrap as a NCLOB.
 	 * @return The created NCLOB, castable as {@link Clob} as well as {@link NClobImplementer}.  In JDK 1.6
 	 * environments, also castable to java.sql.NClob
 	 */
 	public NClob createNClob(String string);
 
 	/**
 	 * Create a NCLOB reference encapsulating the given character data.
 	 *
 	 * @param reader The character data reader.
 	 * @param length The length of the reader data.
 	 * @return The created NCLOB, castable as {@link Clob} as well as {@link NClobImplementer}.  In JDK 1.6
 	 * environments, also castable to java.sql.NClob
 	 */
 	public NClob createNClob(Reader reader, long length);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java
index 2314f92d3a..cde389cb01 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableNClobProxy.java
@@ -1,92 +1,76 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.engine.jdbc;
 
 import java.lang.reflect.Proxy;
 import java.sql.Clob;
+import java.sql.NClob;
 
 /**
  * Manages aspects of proxying java.sql.NClobs to add serializability.
  *
  * @author Steve Ebersole
  */
 public class SerializableNClobProxy extends SerializableClobProxy {
-	private static final Class NCLOB_CLASS = loadNClobClassIfAvailable();
-
-	private static Class loadNClobClassIfAvailable() {
-		try {
-			return getProxyClassLoader().loadClass( "java.sql.NClob" );
-		}
-		catch ( ClassNotFoundException e ) {
-			return null;
-		}
-	}
-
-	private static final Class[] PROXY_INTERFACES = new Class[] { determineNClobInterface(), WrappedClob.class };
-
-	private static Class determineNClobInterface() {
-		// java.sql.NClob is a simple marker interface extending java.sql.Clob.  So if java.sql.NClob is not available
-		// on the classloader, just use java.sql.Clob
-		return NCLOB_CLASS == null ? Clob.class : NCLOB_CLASS;
-	}
+	private static final Class[] PROXY_INTERFACES = new Class[] { NClob.class, WrappedNClob.class };
 
 	public static boolean isNClob(Clob clob) {
-		return NCLOB_CLASS != null && NCLOB_CLASS.isInstance( clob );
+		return NClob.class.isInstance( clob );
 	}
 
 	/**
 	 * Builds a serializable {@link java.sql.Clob} wrapper around the given {@link java.sql.Clob}.
 	 *
 	 * @param clob The {@link java.sql.Clob} to be wrapped.
 	 *
 	 * @see #generateProxy(java.sql.Clob)
 	 */
 	protected SerializableNClobProxy(Clob clob) {
 		super( clob );
 	}
 
 	/**
-	 * Generates a SerializableClobProxy proxy wrapping the provided Clob object.
+	 * Generates a SerializableNClobProxy proxy wrapping the provided NClob object.
 	 *
-	 * @param clob The Clob to wrap.
+	 * @param nclob The NClob to wrap.
 	 * @return The generated proxy.
 	 */
-	public static Clob generateProxy(Clob clob) {
-		return ( Clob ) Proxy.newProxyInstance(
+	public static NClob generateProxy(NClob nclob) {
+		return ( NClob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
-				new SerializableNClobProxy( clob )
+				new SerializableNClobProxy( nclob )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	public static ClassLoader getProxyClassLoader() {
 		return SerializableClobProxy.getProxyClassLoader();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedNClob.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedNClob.java
new file mode 100644
index 0000000000..41c801e160
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/WrappedNClob.java
@@ -0,0 +1,37 @@
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
+package org.hibernate.engine.jdbc;
+
+import java.sql.NClob;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface WrappedNClob extends WrappedClob {
+	@Override
+	@Deprecated
+	public NClob getWrappedClob();
+
+	public NClob getWrappedNClob();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
index ee22a4ab3b..512f19764f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BasicTypeRegistry.java
@@ -1,164 +1,169 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 /**
  * A registry of {@link BasicType} instances
  *
  * @author Steve Ebersole
  */
 public class BasicTypeRegistry implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicTypeRegistry.class.getName());
 
 	// TODO : analyze these sizing params; unfortunately this seems to be the only way to give a "concurrencyLevel"
 	private Map<String,BasicType> registry = new ConcurrentHashMap<String, BasicType>( 100, .75f, 1 );
 	private boolean locked = false;
 
 	public BasicTypeRegistry() {
 		register( BooleanType.INSTANCE );
 		register( NumericBooleanType.INSTANCE );
 		register( TrueFalseType.INSTANCE );
 		register( YesNoType.INSTANCE );
 
 		register( ByteType.INSTANCE );
 		register( CharacterType.INSTANCE );
 		register( ShortType.INSTANCE );
 		register( IntegerType.INSTANCE );
 		register( LongType.INSTANCE );
 		register( FloatType.INSTANCE );
 		register( DoubleType.INSTANCE );
 		register( BigDecimalType.INSTANCE );
 		register( BigIntegerType.INSTANCE );
 
 		register( StringType.INSTANCE );
+		register( StringNVarcharType.INSTANCE );
+		register( CharacterNCharType.INSTANCE );
 		register( UrlType.INSTANCE );
 
 		register( DateType.INSTANCE );
 		register( TimeType.INSTANCE );
 		register( TimestampType.INSTANCE );
 		register( DbTimestampType.INSTANCE );
 		register( CalendarType.INSTANCE );
 		register( CalendarDateType.INSTANCE );
 
 		register( LocaleType.INSTANCE );
 		register( CurrencyType.INSTANCE );
 		register( TimeZoneType.INSTANCE );
 		register( ClassType.INSTANCE );
 		register( UUIDBinaryType.INSTANCE );
 		register( UUIDCharType.INSTANCE );
 		register( PostgresUUIDType.INSTANCE );
 
 		register( BinaryType.INSTANCE );
 		register( WrapperBinaryType.INSTANCE );
 		register( ImageType.INSTANCE );
 		register( CharArrayType.INSTANCE );
 		register( CharacterArrayType.INSTANCE );
 		register( TextType.INSTANCE );
+		register( NTextType.INSTANCE );
 		register( BlobType.INSTANCE );
 		register( MaterializedBlobType.INSTANCE );
 		register( ClobType.INSTANCE );
+		register( NClobType.INSTANCE );
 		register( MaterializedClobType.INSTANCE );
+		register( MaterializedNClobType.INSTANCE );
 		register( SerializableType.INSTANCE );
 
 		register( ObjectType.INSTANCE );
 
 		//noinspection unchecked
 		register( new AdaptedImmutableType( DateType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( TimeType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( TimestampType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( DbTimestampType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( CalendarType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( CalendarDateType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( BinaryType.INSTANCE ) );
 		//noinspection unchecked
 		register( new AdaptedImmutableType( SerializableType.INSTANCE ) );
 	}
 
 	/**
 	 * Constructor version used during shallow copy
 	 *
 	 * @param registeredTypes The type map to copy over
 	 */
 	@SuppressWarnings({ "UnusedDeclaration" })
 	private BasicTypeRegistry(Map<String, BasicType> registeredTypes) {
 		registry.putAll( registeredTypes );
 		locked = true;
 	}
 
 	public void register(BasicType type) {
 		if ( locked ) {
 			throw new HibernateException( "Can not alter TypeRegistry at this time" );
 		}
 
 		if ( type == null ) {
 			throw new HibernateException( "Type to register cannot be null" );
 		}
 
 		if ( type.getRegistrationKeys() == null || type.getRegistrationKeys().length == 0 ) {
 			LOG.typeDefinedNoRegistrationKeys( type );
 		}
 
 		for ( String key : type.getRegistrationKeys() ) {
 			// be safe...
             if (key == null) continue;
             LOG.debugf("Adding type registration %s -> %s", key, type);
 			final Type old = registry.put( key, type );
             if (old != null && old != type) LOG.typeRegistrationOverridesPrevious(key, old);
 		}
 	}
 
 	public void register(UserType type, String[] keys) {
 		register( new CustomType( type, keys ) );
 	}
 
 	public void register(CompositeUserType type, String[] keys) {
 		register( new CompositeCustomType( type, keys ) );
 	}
 
 	public BasicType getRegisteredType(String key) {
 		return registry.get( key );
 	}
 
 	public BasicTypeRegistry shallowCopy() {
 		return new BasicTypeRegistry( this.registry );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CharacterArrayNClobType.java b/hibernate-core/src/main/java/org/hibernate/type/CharacterArrayNClobType.java
new file mode 100644
index 0000000000..43d4cd8ed5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/CharacterArrayNClobType.java
@@ -0,0 +1,49 @@
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
+package org.hibernate.type;
+
+import org.hibernate.type.descriptor.java.CharacterArrayTypeDescriptor;
+import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
+
+/**
+ * A type that maps between {@link java.sql.Types#NCLOB NCLOB} and {@link Character Character[]}
+ * <p/>
+ * Essentially a {@link org.hibernate.type.MaterializedNClobType} but represented as a Character[] in Java rather than String.
+ *
+ * @author Emmanuel Bernard
+ * @author Steve Ebersole
+ */
+public class CharacterArrayNClobType extends AbstractSingleColumnStandardBasicType<Character[]> {
+	public static final CharacterArrayNClobType INSTANCE = new CharacterArrayNClobType();
+
+	public CharacterArrayNClobType() {
+		super( NClobTypeDescriptor.DEFAULT, CharacterArrayTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() {
+		// todo name these annotation types for addition to the registry
+		return null;
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CharacterNCharType.java b/hibernate-core/src/main/java/org/hibernate/type/CharacterNCharType.java
new file mode 100644
index 0000000000..3f0bf31374
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/CharacterNCharType.java
@@ -0,0 +1,68 @@
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
+package org.hibernate.type;
+
+import java.io.Serializable;
+
+import org.hibernate.dialect.Dialect;
+import org.hibernate.type.descriptor.java.CharacterTypeDescriptor;
+import org.hibernate.type.descriptor.sql.NCharTypeDescriptor;
+
+/**
+ * A type that maps between {@link java.sql.Types#NCHAR NCHAR(1)} and {@link Character}
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public class CharacterNCharType
+		extends AbstractSingleColumnStandardBasicType<Character>
+		implements PrimitiveType<Character>, DiscriminatorType<Character> {
+
+	public static final CharacterNCharType INSTANCE = new CharacterNCharType();
+
+	public CharacterNCharType() {
+		super( NCharTypeDescriptor.INSTANCE, CharacterTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() {
+		return "ncharacter";
+	}
+
+	public Serializable getDefaultValue() {
+		throw new UnsupportedOperationException( "not a valid id type" );
+	}
+
+	public Class getPrimitiveClass() {
+		return char.class;
+	}
+
+	public String objectToSQLString(Character value, Dialect dialect) {
+		return '\'' + toString( value ) + '\'';
+	}
+
+	public Character stringToObject(String xml) {
+		return fromString( xml );
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/MaterializedNClobType.java b/hibernate-core/src/main/java/org/hibernate/type/MaterializedNClobType.java
new file mode 100644
index 0000000000..4a6de4ecd0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/MaterializedNClobType.java
@@ -0,0 +1,46 @@
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
+package org.hibernate.type;
+
+import org.hibernate.type.descriptor.java.StringTypeDescriptor;
+import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
+
+/**
+ * A type that maps between {@link java.sql.Types#CLOB CLOB} and {@link String}
+ *
+ * @author Gavin King
+ * @author Gail Badner
+ * @author Steve Ebersole
+ */
+public class MaterializedNClobType extends AbstractSingleColumnStandardBasicType<String> {
+	public static final MaterializedNClobType INSTANCE = new MaterializedNClobType();
+
+	public MaterializedNClobType() {
+		super( NClobTypeDescriptor.DEFAULT, StringTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() {
+		return "materialized_nclob";
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/NClobType.java b/hibernate-core/src/main/java/org/hibernate/type/NClobType.java
new file mode 100644
index 0000000000..979895950c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/NClobType.java
@@ -0,0 +1,58 @@
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
+package org.hibernate.type;
+
+import java.sql.NClob;
+
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.type.descriptor.java.NClobTypeDescriptor;
+
+/**
+ * A type that maps between {@link java.sql.Types#CLOB CLOB} and {@link java.sql.Clob}
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public class NClobType extends AbstractSingleColumnStandardBasicType<NClob> {
+	public static final NClobType INSTANCE = new NClobType();
+
+	public NClobType() {
+		super( org.hibernate.type.descriptor.sql.NClobTypeDescriptor.DEFAULT, NClobTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() {
+		return "nclob";
+	}
+
+	@Override
+	protected boolean registerUnderJavaType() {
+		return true;
+	}
+
+	@Override
+	protected NClob getReplacement(NClob original, NClob target, SessionImplementor session) {
+		return session.getFactory().getDialect().getLobMergeStrategy().mergeNClob( original, target, session );
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/NTextType.java b/hibernate-core/src/main/java/org/hibernate/type/NTextType.java
new file mode 100644
index 0000000000..402876ff3d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/NTextType.java
@@ -0,0 +1,47 @@
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
+package org.hibernate.type;
+
+import org.hibernate.type.descriptor.java.StringTypeDescriptor;
+import org.hibernate.type.descriptor.sql.LongNVarcharTypeDescriptor;
+
+/**
+ * A type that maps between {@link java.sql.Types#LONGNVARCHAR LONGNVARCHAR} and {@link String}
+ *
+ * @author Gavin King,
+ * @author Bertrand Renuart
+ * @author Steve Ebersole
+ */
+public class NTextType extends AbstractSingleColumnStandardBasicType<String> {
+	public static final NTextType INSTANCE = new NTextType();
+
+	public NTextType() {
+		super( LongNVarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() { 
+		return "ntext";
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayNClobType.java b/hibernate-core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayNClobType.java
new file mode 100644
index 0000000000..031ea2c7f3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/PrimitiveCharacterArrayNClobType.java
@@ -0,0 +1,45 @@
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
+package org.hibernate.type;
+
+import org.hibernate.type.descriptor.java.PrimitiveCharacterArrayTypeDescriptor;
+import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
+
+/**
+ * Map a char[] to a NClob
+ *
+ * @author Emmanuel Bernard
+ */
+public class PrimitiveCharacterArrayNClobType extends AbstractSingleColumnStandardBasicType<char[]> {
+	public static final CharacterArrayClobType INSTANCE = new CharacterArrayClobType();
+
+	public PrimitiveCharacterArrayNClobType() {
+		super( NClobTypeDescriptor.DEFAULT, PrimitiveCharacterArrayTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() {
+		// todo name these annotation types for addition to the registry
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java b/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
index 4b5927efe2..e853fba1a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/StandardBasicTypes.java
@@ -1,322 +1,348 @@
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
 package org.hibernate.type;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Centralizes access to the standard set of basic {@link Type types}.
  * <p/>
  * Type mappings can be adjusted per {@link org.hibernate.SessionFactory}.  These adjusted mappings can be accessed
  * from the {@link org.hibernate.TypeHelper} instance obtained via {@link org.hibernate.SessionFactory#getTypeHelper()}
  *
  * @see BasicTypeRegistry
  * @see org.hibernate.TypeHelper
  * @see org.hibernate.SessionFactory#getTypeHelper()
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class StandardBasicTypes {
 
 	private static final Set<SqlTypeDescriptor> sqlTypeDescriptors = new HashSet<SqlTypeDescriptor>();
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#BIT BIT}.
 	 *
 	 * @see BooleanType
 	 */
 	public static final BooleanType BOOLEAN = BooleanType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#INTEGER INTEGER}.
 	 *
 	 * @see NumericBooleanType
 	 */
 	public static final NumericBooleanType NUMERIC_BOOLEAN = NumericBooleanType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#CHAR CHAR(1)} (using 'T'/'F').
 	 *
 	 * @see TrueFalseType
 	 */
 	public static final TrueFalseType TRUE_FALSE = TrueFalseType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Boolean} to JDBC {@link java.sql.Types#CHAR CHAR(1)} (using 'Y'/'N').
 	 *
 	 * @see YesNoType
 	 */
 	public static final YesNoType YES_NO = YesNoType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Byte} to JDBC {@link java.sql.Types#TINYINT TINYINT}.
 	 */
 	public static final ByteType BYTE = ByteType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Short} to JDBC {@link java.sql.Types#SMALLINT SMALLINT}.
 	 *
 	 * @see ShortType
 	 */
 	public static final ShortType SHORT = ShortType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Integer} to JDBC {@link java.sql.Types#INTEGER INTEGER}.
 	 *
 	 * @see IntegerType
 	 */
 	public static final IntegerType INTEGER = IntegerType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Long} to JDBC {@link java.sql.Types#BIGINT BIGINT}.
 	 *
 	 * @see LongType
 	 */
 	public static final LongType LONG = LongType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Float} to JDBC {@link java.sql.Types#FLOAT FLOAT}.
 	 *
 	 * @see FloatType
 	 */
 	public static final FloatType FLOAT = FloatType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Double} to JDBC {@link java.sql.Types#DOUBLE DOUBLE}.
 	 *
 	 * @see DoubleType
 	 */
 	public static final DoubleType DOUBLE = DoubleType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.math.BigInteger} to JDBC {@link java.sql.Types#NUMERIC NUMERIC}.
 	 *
 	 * @see BigIntegerType
 	 */
 	public static final BigIntegerType BIG_INTEGER = BigIntegerType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.math.BigDecimal} to JDBC {@link java.sql.Types#NUMERIC NUMERIC}.
 	 *
 	 * @see BigDecimalType
 	 */
 	public static final BigDecimalType BIG_DECIMAL = BigDecimalType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Character} to JDBC {@link java.sql.Types#CHAR CHAR(1)}.
 	 *
 	 * @see CharacterType
 	 */
 	public static final CharacterType CHARACTER = CharacterType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see StringType
 	 */
 	public static final StringType STRING = StringType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.net.URL} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see UrlType
 	 */
 	public static final UrlType URL = UrlType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Date} ({@link java.sql.Time}) to JDBC
 	 * {@link java.sql.Types#TIME TIME}.
 	 *
 	 * @see TimeType
 	 */
 	public static final TimeType TIME = TimeType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Date} ({@link java.sql.Date}) to JDBC
 	 * {@link java.sql.Types#DATE DATE}.
 	 *
 	 * @see TimeType
 	 */
 	public static final DateType DATE = DateType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Date} ({@link java.sql.Timestamp}) to JDBC
 	 * {@link java.sql.Types#TIMESTAMP TIMESTAMP}.
 	 *
 	 * @see TimeType
 	 */
 	public static final TimestampType TIMESTAMP = TimestampType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Calendar} to JDBC
 	 * {@link java.sql.Types#TIMESTAMP TIMESTAMP}.
 	 *
 	 * @see CalendarType
 	 */
 	public static final CalendarType CALENDAR = CalendarType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Calendar} to JDBC
 	 * {@link java.sql.Types#DATE DATE}.
 	 *
 	 * @see CalendarDateType
 	 */
 	public static final CalendarDateType CALENDAR_DATE = CalendarDateType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Class} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see ClassType
 	 */
 	public static final ClassType CLASS = ClassType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Locale} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see LocaleType
 	 */
 	public static final LocaleType LOCALE = LocaleType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.Currency} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see CurrencyType
 	 */
 	public static final CurrencyType CURRENCY = CurrencyType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.TimeZone} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see TimeZoneType
 	 */
 	public static final TimeZoneType TIMEZONE = TimeZoneType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.UUID} to JDBC {@link java.sql.Types#BINARY BINARY}.
 	 *
 	 * @see UUIDBinaryType
 	 */
 	public static final UUIDBinaryType UUID_BINARY = UUIDBinaryType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.util.UUID} to JDBC {@link java.sql.Types#CHAR CHAR}.
 	 *
 	 * @see UUIDCharType
 	 */
 	public static final UUIDCharType UUID_CHAR = UUIDCharType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#VARBINARY VARBINARY}.
 	 *
 	 * @see BinaryType
 	 */
 	public static final BinaryType BINARY = BinaryType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Byte Byte[]} to JDBC {@link java.sql.Types#VARBINARY VARBINARY}.
 	 *
 	 * @see WrapperBinaryType
 	 */
 	public static final WrapperBinaryType WRAPPER_BINARY = WrapperBinaryType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#LONGVARBINARY LONGVARBINARY}.
 	 *
 	 * @see ImageType
 	 * @see #MATERIALIZED_BLOB
 	 */
 	public static final ImageType IMAGE = ImageType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link java.sql.Blob} to JDBC {@link java.sql.Types#BLOB BLOB}.
 	 *
 	 * @see BlobType
 	 * @see #MATERIALIZED_BLOB
 	 */
 	public static final BlobType BLOB = BlobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code byte[]} to JDBC {@link java.sql.Types#BLOB BLOB}.
 	 *
 	 * @see MaterializedBlobType
 	 * @see #MATERIALIZED_BLOB
 	 * @see #IMAGE
 	 */
 	public static final MaterializedBlobType MATERIALIZED_BLOB = MaterializedBlobType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@code char[]} to JDBC {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see CharArrayType
 	 */
 	public static final CharArrayType CHAR_ARRAY = CharArrayType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link Character Character[]} to JDBC
 	 * {@link java.sql.Types#VARCHAR VARCHAR}.
 	 *
 	 * @see CharacterArrayType
 	 */
 	public static final CharacterArrayType CHARACTER_ARRAY = CharacterArrayType.INSTANCE;
 
 	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#LONGVARCHAR LONGVARCHAR}.
 	 * <p/>
 	 * Similar to a {@link #MATERIALIZED_CLOB}
 	 *
 	 * @see TextType
 	 */
 	public static final TextType TEXT = TextType.INSTANCE;
 
 	/**
+	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#LONGNVARCHAR LONGNVARCHAR}.
+	 * <p/>
+	 * Similar to a {@link #MATERIALIZED_NCLOB}
+	 *
+	 * @see NTextType
+	 */
+	public static final NTextType NTEXT = NTextType.INSTANCE;
+
+	/**
 	 * The standard Hibernate type for mapping {@link java.sql.Clob} to JDBC {@link java.sql.Types#CLOB CLOB}.
 	 *
 	 * @see ClobType
 	 * @see #MATERIALIZED_CLOB
 	 */
 	public static final ClobType CLOB = ClobType.INSTANCE;
 
 	/**
+	 * The standard Hibernate type for mapping {@link java.sql.NClob} to JDBC {@link java.sql.Types#NCLOB NCLOB}.
+	 *
+	 * @see NClobType
+	 * @see #MATERIALIZED_NCLOB
+	 */
+	public static final NClobType NCLOB = NClobType.INSTANCE;
+
+	/**
 	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#CLOB CLOB}.
 	 *
 	 * @see MaterializedClobType
 	 * @see #MATERIALIZED_CLOB
 	 * @see #TEXT
 	 */
 	public static final MaterializedClobType MATERIALIZED_CLOB = MaterializedClobType.INSTANCE;
 
 	/**
+	 * The standard Hibernate type for mapping {@link String} to JDBC {@link java.sql.Types#NCLOB NCLOB}.
+	 *
+	 * @see MaterializedNClobType
+	 * @see #MATERIALIZED_CLOB
+	 * @see #NTEXT
+	 */
+	public static final MaterializedNClobType MATERIALIZED_NCLOB = MaterializedNClobType.INSTANCE;
+
+	/**
 	 * The standard Hibernate type for mapping {@link java.io.Serializable} to JDBC {@link java.sql.Types#VARBINARY VARBINARY}.
 	 * <p/>
 	 * See especially the discussion wrt {@link ClassLoader} determination on {@link SerializableType}
 	 *
 	 * @see SerializableType
 	 */
 	public static final SerializableType SERIALIZABLE = SerializableType.INSTANCE;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/StringNVarcharType.java b/hibernate-core/src/main/java/org/hibernate/type/StringNVarcharType.java
new file mode 100644
index 0000000000..c97b52be54
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/StringNVarcharType.java
@@ -0,0 +1,66 @@
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
+package org.hibernate.type;
+
+import org.hibernate.dialect.Dialect;
+import org.hibernate.type.descriptor.java.StringTypeDescriptor;
+import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;
+
+/**
+ * A type that maps between {@link java.sql.Types#VARCHAR VARCHAR} and {@link String}
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public class StringNVarcharType
+		extends AbstractSingleColumnStandardBasicType<String>
+		implements DiscriminatorType<String> {
+
+	public static final StringNVarcharType INSTANCE = new StringNVarcharType();
+
+	public StringNVarcharType() {
+		super( NVarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE );
+	}
+
+	public String getName() {
+		return "nstring";
+	}
+
+	@Override
+	protected boolean registerUnderJavaType() {
+		return false;
+	}
+
+	public String objectToSQLString(String value, Dialect dialect) throws Exception {
+		return '\'' + value + '\'';
+	}
+
+	public String stringToObject(String xml) throws Exception {
+		return xml;
+	}
+
+	public String toString(String value) {
+		return value;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java
new file mode 100644
index 0000000000..f1a9fcd283
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/NClobTypeDescriptor.java
@@ -0,0 +1,134 @@
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
+package org.hibernate.type.descriptor.java;
+
+import java.io.Serializable;
+import java.sql.NClob;
+import java.util.Comparator;
+
+import org.hibernate.engine.jdbc.CharacterStream;
+import org.hibernate.engine.jdbc.NClobImplementer;
+import org.hibernate.engine.jdbc.NClobProxy;
+import org.hibernate.engine.jdbc.WrappedNClob;
+import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
+import org.hibernate.type.descriptor.WrapperOptions;
+
+/**
+ * Descriptor for {@link java.sql.NClob} handling.
+ * <p/>
+ * Note, {@link java.sql.NClob nclobs} really are mutable (their internal state can in fact be mutated).  We simply
+ * treat them as immutable because we cannot properly check them for changes nor deep copy them.
+ *
+ * @author Steve Ebersole
+ */
+public class NClobTypeDescriptor extends AbstractTypeDescriptor<NClob> {
+	public static final NClobTypeDescriptor INSTANCE = new NClobTypeDescriptor();
+
+	public static class NClobMutabilityPlan implements MutabilityPlan<NClob> {
+		public static final NClobMutabilityPlan INSTANCE = new NClobMutabilityPlan();
+
+		public boolean isMutable() {
+			return false;
+		}
+
+		public NClob deepCopy(NClob value) {
+			return value;
+		}
+
+		public Serializable disassemble(NClob value) {
+			throw new UnsupportedOperationException( "Clobs are not cacheable" );
+		}
+
+		public NClob assemble(Serializable cached) {
+			throw new UnsupportedOperationException( "Clobs are not cacheable" );
+		}
+	}
+
+	public NClobTypeDescriptor() {
+		super( NClob.class, NClobMutabilityPlan.INSTANCE );
+	}
+
+	public String toString(NClob value) {
+		return DataHelper.extractString( value );
+	}
+
+	public NClob fromString(String string) {
+		return NClobProxy.generateProxy( string );
+	}
+
+	@Override
+	@SuppressWarnings({ "unchecked" })
+	public Comparator<NClob> getComparator() {
+		return IncomparableComparator.INSTANCE;
+	}
+
+	@Override
+	public int extractHashCode(NClob value) {
+		return System.identityHashCode( value );
+	}
+
+	@Override
+	public boolean areEqual(NClob one, NClob another) {
+		return one == another;
+	}
+
+	@SuppressWarnings({ "unchecked" })
+	public <X> X unwrap(final NClob value, Class<X> type, WrapperOptions options) {
+		if ( ! ( NClob.class.isAssignableFrom( type ) || CharacterStream.class.isAssignableFrom( type ) ) ) {
+			throw unknownUnwrap( type );
+		}
+
+		if ( value == null ) {
+			return null;
+		}
+
+		if ( CharacterStream.class.isAssignableFrom( type ) ) {
+			if ( NClobImplementer.class.isInstance( value ) ) {
+				// if the incoming Clob is a wrapper, just pass along its CharacterStream
+				return (X) ( (NClobImplementer) value ).getUnderlyingStream();
+			}
+			else {
+				// otherwise we need to build one...
+				return (X) new CharacterStreamImpl( DataHelper.extractString( value ) );
+			}
+		}
+
+		final NClob clob =  WrappedNClob.class.isInstance( value )
+				? ( (WrappedNClob) value ).getWrappedNClob()
+				: value;
+		return (X) clob;
+	}
+
+	public <X> NClob wrap(X value, WrapperOptions options) {
+		if ( value == null ) {
+			return null;
+		}
+
+		if ( ! NClob.class.isAssignableFrom( value.getClass() ) ) {
+			throw unknownWrap( value.getClass() );
+		}
+
+		return options.getLobCreator().wrap( (NClob) value );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/nationalized/SimpleNationalizedTest.java b/hibernate-core/src/test/java/org/hibernate/test/nationalized/SimpleNationalizedTest.java
new file mode 100644
index 0000000000..c56c40dd64
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/nationalized/SimpleNationalizedTest.java
@@ -0,0 +1,90 @@
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
+package org.hibernate.test.nationalized;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Lob;
+
+import java.sql.NClob;
+
+import org.hibernate.annotations.Nationalized;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Property;
+import org.hibernate.type.MaterializedNClobType;
+import org.hibernate.type.NClobType;
+import org.hibernate.type.StandardBasicTypes;
+import org.hibernate.type.StringNVarcharType;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertSame;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SimpleNationalizedTest extends BaseUnitTestCase {
+
+	@Entity( name="NationalizedEntity")
+	public static class NationalizedEntity {
+		@Id
+		private Integer id;
+		@Nationalized
+		private String nvarcharAtt;
+		@Lob
+		@Nationalized
+		private String materializedNclobAtt;
+		@Lob
+		@Nationalized
+		private NClob nclobAtt;
+	}
+
+	@Test
+	public void simpleNationalizedTest() {
+		Configuration cfg = new Configuration();
+		cfg.addAnnotatedClass( NationalizedEntity.class );
+		cfg.buildMappings();
+		PersistentClass pc = cfg.getClassMapping( NationalizedEntity.class.getName() );
+		assertNotNull( pc );
+
+		{
+			Property prop = pc.getProperty( "nvarcharAtt" );
+			assertSame( StringNVarcharType.INSTANCE, prop.getType() );
+		}
+
+		{
+			Property prop = pc.getProperty( "materializedNclobAtt" );
+			assertSame( MaterializedNClobType.INSTANCE, prop.getType() );
+		}
+
+		{
+			Property prop = pc.getProperty( "nclobAtt" );
+			assertSame( NClobType.INSTANCE, prop.getType() );
+		}
+	}
+}
