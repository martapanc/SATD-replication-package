diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 9d99ef86a5..f4f1def079 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,532 +1,543 @@
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
-	 * Names a {@literal JNDI} namespace into which the {@link org.hibernate.SessionFactory} should be bound.
+	 * Defines a name for the {@link org.hibernate.SessionFactory}.  Useful both to<ul>
+	 *     <li>allow serialization and deserialization to work across different jvms</li>
+	 *     <li>optionally allow the SessionFactory to be bound into JNDI</li>
+	 * </ul>
+	 *
+	 * @see #SESSION_FACTORY_NAME_IS_JNDI
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
+	 * Does the value defined by {@link #SESSION_FACTORY_NAME} represent a {@literal JNDI} namespace into which
+	 * the {@link org.hibernate.SessionFactory} should be bound?
+	 */
+	public static final String SESSION_FACTORY_NAME_IS_JNDI = "hibernate.session_factory_name_is_jndi";
+
+	/**
 	 * Names the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} to use for obtaining
 	 * JDBC connections.  Can either reference an instance of
 	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} or a {@link Class} or {@link String}
 	 * reference to the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} implementation
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
 	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider}: <ul>
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
 	 * Names any additional {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} implementations to
 	 * register with the standard {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}.
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
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 
 	/**
 	 * Names the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use for integrating
 	 * with {@literal JTA} systems.  Can reference either a {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
 	 * instance or the name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation class
 	 * @since 4.0
 	 */
 	public static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
 
 	/**
 	 * Names the {@link org.hibernate.transaction.TransactionManagerLookup} implementation to use for obtaining
 	 * reference to the {@literal JTA} {@link javax.transaction.TransactionManager}
 	 *
 	 * @deprecated See {@link #JTA_PLATFORM}
 	 */
 	@Deprecated
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 *
 	 * @deprecated See {@link #JTA_PLATFORM}
 	 */
 	@Deprecated
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
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
 	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
 	 */
 	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	public static final String MULTI_TENANT = "hibernate.multiTenancy";
 
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 73b6047e98..0a155ae00a 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,452 +1,461 @@
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
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Settings that affect the behaviour of Hibernate at runtime.
  *
  * @author Gavin King
  */
 public final class Settings {
 
 	private Integer maximumFetchDepth;
 	private Map querySubstitutions;
 	private int jdbcBatchSize;
 	private int defaultBatchFetchSize;
 	private boolean scrollableResultSetsEnabled;
 	private boolean getGeneratedKeysEnabled;
 	private String defaultSchemaName;
 	private String defaultCatalogName;
 	private Integer jdbcFetchSize;
 	private String sessionFactoryName;
+	private boolean sessionFactoryNameAlsoJndiName;
 	private boolean autoCreateSchema;
 	private boolean autoDropSchema;
 	private boolean autoUpdateSchema;
 	private boolean autoValidateSchema;
 	private boolean queryCacheEnabled;
 	private boolean structuredCacheEntriesEnabled;
 	private boolean secondLevelCacheEnabled;
 	private String cacheRegionPrefix;
 	private boolean minimalPutsEnabled;
 	private boolean commentsEnabled;
 	private boolean statisticsEnabled;
 	private boolean jdbcBatchVersionedData;
 	private boolean identifierRollbackEnabled;
 	private boolean flushBeforeCompletionEnabled;
 	private boolean autoCloseSessionEnabled;
 	private ConnectionReleaseMode connectionReleaseMode;
 	private RegionFactory regionFactory;
 	private QueryCacheFactory queryCacheFactory;
 	private QueryTranslatorFactory queryTranslatorFactory;
 	private boolean wrapResultSetsEnabled;
 	private boolean orderUpdatesEnabled;
 	private boolean orderInsertsEnabled;
 	private EntityMode defaultEntityMode;
 	private boolean dataDefinitionImplicitCommit;
 	private boolean dataDefinitionInTransactionSupported;
 	private boolean strictJPAQLCompliance;
 	private boolean namedQueryStartupCheckingEnabled;
 	private EntityTuplizerFactory entityTuplizerFactory;
 	private boolean checkNullability;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 //	private BytecodeProvider bytecodeProvider;
 	private String importFiles;
 	private MultiTenancyStrategy multiTenancyStrategy;
 
 	private JtaPlatform jtaPlatform;
 
 	/**
 	 * Package protected constructor
 	 */
 	Settings() {
 	}
 
 	// public getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getImportFiles() {
 		return importFiles;
 	}
 
 	public void setImportFiles(String importFiles) {
 		this.importFiles = importFiles;
 	}
 
 	public String getDefaultSchemaName() {
 		return defaultSchemaName;
 	}
 
 	public String getDefaultCatalogName() {
 		return defaultCatalogName;
 	}
 
 	public int getJdbcBatchSize() {
 		return jdbcBatchSize;
 	}
 
 	public int getDefaultBatchFetchSize() {
 		return defaultBatchFetchSize;
 	}
 
 	public Map getQuerySubstitutions() {
 		return querySubstitutions;
 	}
 
 	public boolean isIdentifierRollbackEnabled() {
 		return identifierRollbackEnabled;
 	}
 
 	public boolean isScrollableResultSetsEnabled() {
 		return scrollableResultSetsEnabled;
 	}
 
 	public boolean isGetGeneratedKeysEnabled() {
 		return getGeneratedKeysEnabled;
 	}
 
 	public boolean isMinimalPutsEnabled() {
 		return minimalPutsEnabled;
 	}
 
 	public Integer getJdbcFetchSize() {
 		return jdbcFetchSize;
 	}
 
 	public String getSessionFactoryName() {
 		return sessionFactoryName;
 	}
 
+	public boolean isSessionFactoryNameAlsoJndiName() {
+		return sessionFactoryNameAlsoJndiName;
+	}
+
 	public boolean isAutoCreateSchema() {
 		return autoCreateSchema;
 	}
 
 	public boolean isAutoDropSchema() {
 		return autoDropSchema;
 	}
 
 	public boolean isAutoUpdateSchema() {
 		return autoUpdateSchema;
 	}
 
 	public Integer getMaximumFetchDepth() {
 		return maximumFetchDepth;
 	}
 
 	public RegionFactory getRegionFactory() {
 		return regionFactory;
 	}
 
 	public boolean isQueryCacheEnabled() {
 		return queryCacheEnabled;
 	}
 
 	public boolean isCommentsEnabled() {
 		return commentsEnabled;
 	}
 
 	public boolean isSecondLevelCacheEnabled() {
 		return secondLevelCacheEnabled;
 	}
 
 	public String getCacheRegionPrefix() {
 		return cacheRegionPrefix;
 	}
 
 	public QueryCacheFactory getQueryCacheFactory() {
 		return queryCacheFactory;
 	}
 
 	public boolean isStatisticsEnabled() {
 		return statisticsEnabled;
 	}
 
 	public boolean isJdbcBatchVersionedData() {
 		return jdbcBatchVersionedData;
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	public QueryTranslatorFactory getQueryTranslatorFactory() {
 		return queryTranslatorFactory;
 	}
 
 	public boolean isWrapResultSetsEnabled() {
 		return wrapResultSetsEnabled;
 	}
 
 	public boolean isOrderUpdatesEnabled() {
 		return orderUpdatesEnabled;
 	}
 
 	public boolean isOrderInsertsEnabled() {
 		return orderInsertsEnabled;
 	}
 
 	public boolean isStructuredCacheEntriesEnabled() {
 		return structuredCacheEntriesEnabled;
 	}
 
 	public EntityMode getDefaultEntityMode() {
 		return defaultEntityMode;
 	}
 
 	public boolean isAutoValidateSchema() {
 		return autoValidateSchema;
 	}
 
 	public boolean isDataDefinitionImplicitCommit() {
 		return dataDefinitionImplicitCommit;
 	}
 
 	public boolean isDataDefinitionInTransactionSupported() {
 		return dataDefinitionInTransactionSupported;
 	}
 
 	public boolean isStrictJPAQLCompliance() {
 		return strictJPAQLCompliance;
 	}
 
 	public boolean isNamedQueryStartupCheckingEnabled() {
 		return namedQueryStartupCheckingEnabled;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
 	// package protected setters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	void setDefaultSchemaName(String string) {
 		defaultSchemaName = string;
 	}
 
 	void setDefaultCatalogName(String string) {
 		defaultCatalogName = string;
 	}
 
 	void setJdbcBatchSize(int i) {
 		jdbcBatchSize = i;
 	}
 
 	void setDefaultBatchFetchSize(int i) {
 		defaultBatchFetchSize = i;
 	}
 
 	void setQuerySubstitutions(Map map) {
 		querySubstitutions = map;
 	}
 
 	void setIdentifierRollbackEnabled(boolean b) {
 		identifierRollbackEnabled = b;
 	}
 
 	void setMinimalPutsEnabled(boolean b) {
 		minimalPutsEnabled = b;
 	}
 
 	void setScrollableResultSetsEnabled(boolean b) {
 		scrollableResultSetsEnabled = b;
 	}
 
 	void setGetGeneratedKeysEnabled(boolean b) {
 		getGeneratedKeysEnabled = b;
 	}
 
 	void setJdbcFetchSize(Integer integer) {
 		jdbcFetchSize = integer;
 	}
 
 	void setSessionFactoryName(String string) {
 		sessionFactoryName = string;
 	}
 
+	void setSessionFactoryNameAlsoJndiName(boolean sessionFactoryNameAlsoJndiName) {
+		this.sessionFactoryNameAlsoJndiName = sessionFactoryNameAlsoJndiName;
+	}
+
 	void setAutoCreateSchema(boolean b) {
 		autoCreateSchema = b;
 	}
 
 	void setAutoDropSchema(boolean b) {
 		autoDropSchema = b;
 	}
 
 	void setAutoUpdateSchema(boolean b) {
 		autoUpdateSchema = b;
 	}
 
 	void setMaximumFetchDepth(Integer i) {
 		maximumFetchDepth = i;
 	}
 
 	void setRegionFactory(RegionFactory regionFactory) {
 		this.regionFactory = regionFactory;
 	}
 
 	void setQueryCacheEnabled(boolean b) {
 		queryCacheEnabled = b;
 	}
 
 	void setCommentsEnabled(boolean commentsEnabled) {
 		this.commentsEnabled = commentsEnabled;
 	}
 
 	void setSecondLevelCacheEnabled(boolean secondLevelCacheEnabled) {
 		this.secondLevelCacheEnabled = secondLevelCacheEnabled;
 	}
 
 	void setCacheRegionPrefix(String cacheRegionPrefix) {
 		this.cacheRegionPrefix = cacheRegionPrefix;
 	}
 
 	void setQueryCacheFactory(QueryCacheFactory queryCacheFactory) {
 		this.queryCacheFactory = queryCacheFactory;
 	}
 
 	void setStatisticsEnabled(boolean statisticsEnabled) {
 		this.statisticsEnabled = statisticsEnabled;
 	}
 
 	void setJdbcBatchVersionedData(boolean jdbcBatchVersionedData) {
 		this.jdbcBatchVersionedData = jdbcBatchVersionedData;
 	}
 
 	void setFlushBeforeCompletionEnabled(boolean flushBeforeCompletionEnabled) {
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 	}
 
 	void setAutoCloseSessionEnabled(boolean autoCloseSessionEnabled) {
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 	}
 
 	void setConnectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 		this.connectionReleaseMode = connectionReleaseMode;
 	}
 
 	void setQueryTranslatorFactory(QueryTranslatorFactory queryTranslatorFactory) {
 		this.queryTranslatorFactory = queryTranslatorFactory;
 	}
 
 	void setWrapResultSetsEnabled(boolean wrapResultSetsEnabled) {
 		this.wrapResultSetsEnabled = wrapResultSetsEnabled;
 	}
 
 	void setOrderUpdatesEnabled(boolean orderUpdatesEnabled) {
 		this.orderUpdatesEnabled = orderUpdatesEnabled;
 	}
 
 	void setOrderInsertsEnabled(boolean orderInsertsEnabled) {
 		this.orderInsertsEnabled = orderInsertsEnabled;
 	}
 
 	void setStructuredCacheEntriesEnabled(boolean structuredCacheEntriesEnabled) {
 		this.structuredCacheEntriesEnabled = structuredCacheEntriesEnabled;
 	}
 
 	void setDefaultEntityMode(EntityMode defaultEntityMode) {
 		this.defaultEntityMode = defaultEntityMode;
 	}
 
 	void setAutoValidateSchema(boolean autoValidateSchema) {
 		this.autoValidateSchema = autoValidateSchema;
 	}
 
 	void setDataDefinitionImplicitCommit(boolean dataDefinitionImplicitCommit) {
 		this.dataDefinitionImplicitCommit = dataDefinitionImplicitCommit;
 	}
 
 	void setDataDefinitionInTransactionSupported(boolean dataDefinitionInTransactionSupported) {
 		this.dataDefinitionInTransactionSupported = dataDefinitionInTransactionSupported;
 	}
 
 	void setStrictJPAQLCompliance(boolean strictJPAQLCompliance) {
 		this.strictJPAQLCompliance = strictJPAQLCompliance;
 	}
 
 	void setNamedQueryStartupCheckingEnabled(boolean namedQueryStartupCheckingEnabled) {
 		this.namedQueryStartupCheckingEnabled = namedQueryStartupCheckingEnabled;
 	}
 
 	void setEntityTuplizerFactory(EntityTuplizerFactory entityTuplizerFactory) {
 		this.entityTuplizerFactory = entityTuplizerFactory;
 	}
 
 	public boolean isCheckNullability() {
 		return checkNullability;
 	}
 
 	public void setCheckNullability(boolean checkNullability) {
 		this.checkNullability = checkNullability;
 	}
 
 	//	void setComponentTuplizerFactory(ComponentTuplizerFactory componentTuplizerFactory) {
 //		this.componentTuplizerFactory = componentTuplizerFactory;
 //	}
 
 	//	public BytecodeProvider getBytecodeProvider() {
 //		return bytecodeProvider;
 //	}
 //
 //	void setBytecodeProvider(BytecodeProvider bytecodeProvider) {
 //		this.bytecodeProvider = bytecodeProvider;
 //	}
 
 
 	public JtaPlatform getJtaPlatform() {
 		return jtaPlatform;
 	}
 
 	void setJtaPlatform(JtaPlatform jtaPlatform) {
 		this.jtaPlatform = jtaPlatform;
 	}
 
 	public MultiTenancyStrategy getMultiTenancyStrategy() {
 		return multiTenancyStrategy;
 	}
 
 	void setMultiTenancyStrategy(MultiTenancyStrategy multiTenancyStrategy) {
 		this.multiTenancyStrategy = multiTenancyStrategy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index a98d453da6..97c197f799 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,450 +1,453 @@
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
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
 	private static final long serialVersionUID = -1194386144994524825L;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final boolean debugEnabled =  LOG.isDebugEnabled();
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
-		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
-		settings.setSessionFactoryName(sessionFactoryName);
+		String sessionFactoryName = props.getProperty( Environment.SESSION_FACTORY_NAME );
+		settings.setSessionFactoryName( sessionFactoryName );
+		settings.setSessionFactoryNameAlsoJndiName(
+				ConfigurationHelper.getBoolean( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, props, true )
+		);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
 		if ( debugEnabled ) {
 			LOG.debugf( "Connection release mode: %s", releaseModeName );
 		}
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
 				LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
 		if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		}
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
 		if ( "validate".equals(autoSchemaExport) ) {
 			settings.setAutoValidateSchema( true );
 		}
 		if ( "update".equals(autoSchemaExport) ) {
 			settings.setAutoUpdateSchema( true );
 		}
 		if ( "create".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema( true );
 		}
 		if ( "create-drop".equals( autoSchemaExport ) ) {
 			settings.setAutoCreateSchema( true );
 			settings.setAutoDropSchema( true );
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		}
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debug("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
 		);
 		LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( queryCacheFactoryClassName )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
 	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
 						Environment.CACHE_REGION_FACTORY, properties, null
 				)
 		);
 		if ( regionFactoryClassName == null || !cachingEnabled) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
 		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
 				LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
 						Environment.CACHE_REGION_FACTORY, properties, null
 				)
 		);
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
 		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
 				LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
 		);
 		LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( className )
 					.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 035a892d4a..f0193426b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1788 +1,1819 @@
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
-import java.io.ObjectStreamException;
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
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
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
 import org.hibernate.dialect.function.SQLFunction;
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
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
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
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
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
 
-	private final transient Map entityPersisters;
+	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
-	private final transient Map collectionPersisters;
-	private final transient Map collectionMetadata;
+	private final transient Map<String,CollectionPersister> collectionPersisters;
+	private final transient Map<String,CollectionMetadata> collectionMetadata;
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
 	private final transient ConcurrentMap<String,QueryCache> queryCaches;
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
 
-	@SuppressWarnings( {"unchecked"} )
+	@SuppressWarnings( {"unchecked", "ThrowableResultOfMethodCallIgnored"})
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
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
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
 					if ( LOG.isTraceEnabled() ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntityName() );
 					}
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
-		collectionPersisters = new HashMap();
+		collectionPersisters = new HashMap<String,CollectionPersister>();
+		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracev("Building cache for collection data [{0}]", model.getRole() );
 				}
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
-			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
+			collectionPersisters.put( model.getRole(), persister );
+			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
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
-		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
+		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
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
-		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
+		SessionFactoryRegistry.INSTANCE.addSessionFactory(
+				uuid,
+				name,
+				settings.isSessionFactoryNameAlsoJndiName(),
+				this,
+				serviceRegistry.getService( JndiService.class )
+		);
 
 		LOG.debug( "Instantiated session factory" );
 
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
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties, this);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
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
-			Map errors = checkNamedQueries();
-			if ( !errors.isEmpty() ) {
-				Set keys = errors.keySet();
-				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
-				for ( Iterator iterator = keys.iterator() ; iterator.hasNext() ; ) {
-					String queryName = ( String ) iterator.next();
-					HibernateException e = ( HibernateException ) errors.get( queryName );
-					failingQueries.append( queryName );
-					if ( iterator.hasNext() ) failingQueries.append( ", " );
-					LOG.namedQueryError( queryName, e );
+			final Map<String,HibernateException> errors = checkNamedQueries();
+			if ( ! errors.isEmpty() ) {
+				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
+				String sep = "";
+				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
+					LOG.namedQueryError( entry.getKey(), entry.getValue() );
+					failingQueries.append( entry.getKey() ).append( sep );
+					sep = ", ";
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
-			Iterator fetches = mappingProfile.getFetches().iterator();
-			while ( fetches.hasNext() ) {
-				final org.hibernate.mapping.FetchProfile.Fetch mappingFetch =
-						( org.hibernate.mapping.FetchProfile.Fetch ) fetches.next();
+			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
-				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
+				final EntityPersister owner = entityName == null
+						? null
+						: entityPersisters.get( entityName );
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
-				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
+				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
+	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
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
 				metadata.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 
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
 
-		entityPersisters = new HashMap();
+		entityPersisters = new HashMap<String,EntityPersister>();
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
-		collectionPersisters = new HashMap();
+		collectionPersisters = new HashMap<String,CollectionPersister>();
+		Map<String, CollectionMetadata> tmpCollectionMetadata = new HashMap<String, CollectionMetadata>();
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
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry
 					.getService( PersisterFactory.class )
 					.createCollectionPersister( metadata, model, accessStrategy, this );
-			collectionPersisters.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
+			collectionPersisters.put( model.getAttribute().getRole(), persister );
+			tmpCollectionMetadata.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
-				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
+				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
-					roles = new HashSet();
+					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
-				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
+				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
-					roles = new HashSet();
+					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
-		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
-		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
-		while ( itr.hasNext() ) {
-			final Map.Entry entry = ( Map.Entry ) itr.next();
-			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
+		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
+		for ( Map.Entry<String, Set<String>> entry : tmpEntityToCollectionRoleMap.entrySet() ) {
+			entry.setValue( Collections.unmodifiableSet( entry.getValue() ) );
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
-		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
+		SessionFactoryRegistry.INSTANCE.addSessionFactory(
+				uuid, 
+				name,
+				settings.isSessionFactoryNameAlsoJndiName(),
+				this,
+				serviceRegistry.getService( JndiService.class )
+		);
 
 		LOG.debug("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
-		/*
-		if ( settings.isAutoUpdateSchema() ) {
-			new SchemaUpdate( metadata ).execute( false, true );
-		}
-		if ( settings.isAutoValidateSchema() ) {
-			new SchemaValidator( metadata ).validate();
-		}
-		*/
+
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache( settings, properties, this );
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache( null, updateTimestampsCache, settings, properties );
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
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
-			Map errors = checkNamedQueries();
+			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
-				Set keys = errors.keySet();
-				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
-				for ( Iterator<String> iterator = keys.iterator() ; iterator.hasNext() ; ) {
-					String queryName = iterator.next();
-					HibernateException e = ( HibernateException ) errors.get( queryName );
-					failingQueries.append( queryName );
-                    if ( iterator.hasNext() ) failingQueries.append( ", " );
-					LOG.namedQueryError( queryName, e );
+				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
+				String sep = "";
+				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
+					LOG.namedQueryError( entry.getKey(), entry.getValue() );
+					failingQueries.append( entry.getKey() ).append( sep );
+					sep = ", ";
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
-				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
+				final EntityPersister owner = entityName == null ? null : entityPersisters.get( entityName );
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
 
-	private Map checkNamedQueries() throws HibernateException {
-		Map errors = new HashMap();
+	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
+	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
+		Map<String,HibernateException> errors = new HashMap<String,HibernateException>();
 
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
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, CollectionHelper.EMPTY_MAP );
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
-		EntityPersister result = (EntityPersister) entityPersisters.get(entityName);
-		if (result==null) {
+		EntityPersister result = entityPersisters.get(entityName);
+		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
-		CollectionPersister result = (CollectionPersister) collectionPersisters.get(role);
-		if (result==null) {
+		CollectionPersister result = collectionPersisters.get(role);
+		if ( result == null ) {
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
 
-	private Object readResolve() throws ObjectStreamException {
-		LOG.trace( "Resolving serialized SessionFactory" );
-		// look for the instance by uuid
-		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
-		if ( result == null ) {
-			// in case we were deserialized in a different JVM, look for an instance with the same name
-			// (alternatively we could do an actual JNDI lookup here....)
-			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
-			if ( result == null ) {
-				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
-			}
-			LOG.debug( "Resolved SessionFactory by name" );
-		}
-		else {
-			LOG.debug( "Resolved SessionFactory by UUID" );
-		}
-		return result;
-	}
-
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueries.get(queryName);
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedSqlQueries.get(queryName);
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return sqlResultSetMappings.get(resultSetName);
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
-	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
-		LOG.trace( "Deserializing" );
-		in.defaultReadObject();
-		LOG.debugf( "Deserialized: %s", uuid );
-	}
-
-	private void writeObject(ObjectOutputStream out) throws IOException {
-		LOG.debugf( "Serializing: %s", uuid );
-		out.defaultWriteObject();
-		LOG.trace( "Serialized" );
-	}
-
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
-		return (CollectionMetadata) collectionMetadata.get(roleName);
+		return collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
-		return classMetadata.get(entityName);
+		return classMetadata.get( entityName );
 	}
 
 	/**
-     * @param className
+	 * Given the name of an entity class, determine all the class and interface names by which it can be
+	 * referenced in an HQL query.
+	 *
+     * @param className The name of the entity class
+	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
-		ArrayList results = new ArrayList();
-		Iterator iter = entityPersisters.values().iterator();
-		while ( iter.hasNext() ) {
-			//test this entity to see if we must query it
-			EntityPersister testPersister = (EntityPersister) iter.next();
-			if ( testPersister instanceof Queryable ) {
-				Queryable testQueryable = (Queryable) testPersister;
-				String testClassName = testQueryable.getEntityName();
-				boolean isMappedClass = className.equals(testClassName);
-				if ( testQueryable.isExplicitPolymorphism() ) {
-					if ( isMappedClass ) {
-						return new String[] {className}; //NOTE EARLY EXIT
-					}
+		ArrayList<String> results = new ArrayList<String>();
+		for ( EntityPersister checkPersister : entityPersisters.values() ) {
+			if ( ! Queryable.class.isInstance( checkPersister ) ) {
+				continue;
+			}
+			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
+			final String checkQueryableEntityName = checkQueryable.getEntityName();
+			final boolean isMappedClass = className.equals( checkQueryableEntityName );
+			if ( checkQueryable.isExplicitPolymorphism() ) {
+				if ( isMappedClass ) {
+					return new String[] { className }; //NOTE EARLY EXIT
+				}
+			}
+			else {
+				if ( isMappedClass ) {
+					results.add( checkQueryableEntityName );
 				}
 				else {
-					if (isMappedClass) {
-						results.add(testClassName);
-					}
-					else {
-						final Class mappedClass = testQueryable.getMappedClass();
-						if ( mappedClass!=null && clazz.isAssignableFrom( mappedClass ) ) {
-							final boolean assignableSuperclass;
-							if ( testQueryable.isInherited() ) {
-								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass();
-								assignableSuperclass = clazz.isAssignableFrom(mappedSuperclass);
-							}
-							else {
-								assignableSuperclass = false;
-							}
-							if ( !assignableSuperclass ) {
-								results.add( testClassName );
-							}
+					final Class mappedClass = checkQueryable.getMappedClass();
+					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
+						final boolean assignableSuperclass;
+						if ( checkQueryable.isInherited() ) {
+							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
+							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
+						}
+						else {
+							assignableSuperclass = false;
+						}
+						if ( !assignableSuperclass ) {
+							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
-		return (String[]) results.toArray( new String[ results.size() ] );
+		return results.toArray( new String[ results.size() ] );
 	}
 
 	public String getImportedClassName(String className) {
 		String result = imports.get(className);
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
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
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
-				uuid, name, serviceRegistry.getService( JndiService.class )
+				uuid,
+				name,
+				settings.isSessionFactoryNameAlsoJndiName(),
+				serviceRegistry.getService( JndiService.class )
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
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s",
 							MessageHelper.infoString( p, identifier, SessionFactoryImpl.this ) );
 				}
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
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s", p.getEntityName() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictEntityRegions() {
-			Iterator entityNames = entityPersisters.keySet().iterator();
-			while ( entityNames.hasNext() ) {
-				evictEntityRegion( ( String ) entityNames.next() );
+			for ( String s : entityPersisters.keySet() ) {
+				evictEntityRegion( s );
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
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s",
 							MessageHelper.collectionInfoString( p, ownerIdentifier, SessionFactoryImpl.this ) );
 				}
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
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting second-level cache: %s", p.getRole() );
 				}
 				p.getCacheAccessStrategy().evictAll();
 			}
 		}
 
 		public void evictCollectionRegions() {
-			Iterator collectionRoles = collectionPersisters.keySet().iterator();
-			while ( collectionRoles.hasNext() ) {
-				evictCollectionRegion( ( String ) collectionRoles.next() );
+			for ( String s : collectionPersisters.keySet() ) {
+				evictCollectionRegion( s );
 			}
 		}
 
 		public boolean containsQuery(String regionName) {
 			return queryCaches.containsKey( regionName );
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
 			synchronized ( allCacheRegions ) {
 				currentQueryCache = queryCaches.get( regionName );
 				if ( currentQueryCache == null ) {
 					currentQueryCache = settings.getQueryCacheFactory()
 							.getQueryCache( regionName, updateTimestampsCache, settings, properties );
 					queryCaches.put( regionName, currentQueryCache );
 					allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 				} else {
 					return currentQueryCache;
 				}
 			}
 		}
 		return currentQueryCache;
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
+	@SuppressWarnings( {"unchecked"})
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
-		return ( FetchProfile ) fetchProfiles.get( name );
+		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
-	/**
-	 * Custom serialization hook used during Session serialization.
-	 *
-	 * @param oos The stream to which to write the factory
-	 * @throws IOException Indicates problems writing out the serial data stream
-	 */
-	void serialize(ObjectOutputStream oos) throws IOException {
-		oos.writeUTF( uuid );
-		oos.writeBoolean( name != null );
-		if ( name != null ) {
-			oos.writeUTF( name );
-		}
-	}
-
-	/**
-	 * Custom deserialization hook used during Session deserialization.
-	 *
-	 * @param ois The stream from which to "read" the factory
-	 * @return The deserialized factory
-	 * @throws IOException indicates problems reading back serial data stream
-	 * @throws ClassNotFoundException indicates problems reading back serial data stream
-	 */
-	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
-		final String uuid = ois.readUTF();
-		boolean isNamed = ois.readBoolean();
-		final String name = isNamed ? ois.readUTF() : null;
-		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
-		if ( result == null ) {
-			LOG.tracev( "Could not locate session factory by uuid [{0}] during session deserialization; trying name", uuid );
-			if ( isNamed ) {
-				result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
-			}
-			if ( result == null ) {
-				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
-			}
-		}
-		return ( SessionFactoryImpl ) result;
-	}
-
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
 					connection,
 					sessionFactory,
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
+
+
+	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
+	 *
+	 * @param out The stream into which the object is being serialized.
+	 *
+	 * @throws IOException Can be thrown by the stream
+	 */
+	private void writeObject(ObjectOutputStream out) throws IOException {
+		LOG.debugf( "Serializing: %s", uuid );
+		out.defaultWriteObject();
+		LOG.trace( "Serialized" );
+	}
+
+	/**
+	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
+	 *
+	 * @param in The stream from which the object is being deserialized.
+	 *
+	 * @throws IOException Can be thrown by the stream
+	 * @throws ClassNotFoundException Again, can be thrown by the stream
+	 */
+	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
+		LOG.trace( "Deserializing" );
+		in.defaultReadObject();
+		LOG.debugf( "Deserialized: %s", uuid );
+	}
+
+	/**
+	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
+	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
+	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
+	 *
+	 * @return The resolved factory to use.
+	 *
+	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
+	 */
+	private Object readResolve() throws InvalidObjectException {
+		LOG.trace( "Resolving serialized SessionFactory" );
+		return locateSessionFactoryOnDeserialization( uuid, name );
+	}
+
+	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
+		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
+		if ( uuidResult != null ) {
+			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
+			return uuidResult;
+		}
+
+		// in case we were deserialized in a different JVM, look for an instance with the same name
+		// (provided we were given a name)
+		if ( name != null ) {
+			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
+			if ( namedResult != null ) {
+				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
+				return namedResult;
+			}
+		}
+
+		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
+	}
+
+	/**
+	 * Custom serialization hook used during Session serialization.
+	 *
+	 * @param oos The stream to which to write the factory
+	 * @throws IOException Indicates problems writing out the serial data stream
+	 */
+	void serialize(ObjectOutputStream oos) throws IOException {
+		oos.writeUTF( uuid );
+		oos.writeBoolean( name != null );
+		if ( name != null ) {
+			oos.writeUTF( name );
+		}
+	}
+
+	/**
+	 * Custom deserialization hook used during Session deserialization.
+	 *
+	 * @param ois The stream from which to "read" the factory
+	 * @return The deserialized factory
+	 * @throws IOException indicates problems reading back serial data stream
+	 * @throws ClassNotFoundException indicates problems reading back serial data stream
+	 */
+	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
+		LOG.trace( "Deserializing SessionFactory from Session" );
+		final String uuid = ois.readUTF();
+		boolean isNamed = ois.readBoolean();
+		final String name = isNamed ? ois.readUTF() : null;
+		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java
index 7d3f115457..41dcfdeb71 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryRegistry.java
@@ -1,182 +1,198 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.service.jndi.JndiException;
 import org.hibernate.service.jndi.JndiNameException;
 import org.hibernate.service.jndi.spi.JndiService;
 
 /**
  * A registry of all {@link SessionFactory} instances for the same classloader as this class.
  *
  * This registry is used for serialization/deserialization as well as JNDI binding.
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryRegistry {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			SessionFactoryRegistry.class.getName()
 	);
 
 	public static final SessionFactoryRegistry INSTANCE = new SessionFactoryRegistry();
 
 	private final ConcurrentHashMap<String, SessionFactory> sessionFactoryMap = new ConcurrentHashMap<String, SessionFactory>();
 	private final ConcurrentHashMap<String,String> nameUuidXref = new ConcurrentHashMap<String, String>();
 
 	public SessionFactoryRegistry() {
 		LOG.debugf( "Initializing SessionFactoryRegistry : %s", this );
 	}
 
-	public void addSessionFactory(String uuid, String name, SessionFactory instance, JndiService jndiService) {
+	public void addSessionFactory(
+			String uuid,
+			String name,
+			boolean isNameAlsoJndiName,
+			SessionFactory instance,
+			JndiService jndiService) {
+		if ( uuid == null ) {
+			throw new IllegalArgumentException( "SessionFactory UUID cannot be null" );
+		}
+
         LOG.debugf( "Registering SessionFactory: %s (%s)", uuid, name == null ? "<unnamed>" : name );
 		sessionFactoryMap.put( uuid, instance );
+		if ( name != null ) {
+			nameUuidXref.put( name, uuid );
+		}
 
-		if ( name == null ) {
-			LOG.debug( "Not binding factory to JNDI, no JNDI name configured" );
+		if ( name == null || ! isNameAlsoJndiName ) {
+			LOG.debug( "Not binding SessionFactory to JNDI, no JNDI name configured" );
 			return;
 		}
 
-		nameUuidXref.put( name, uuid );
-
-		LOG.debugf( "SessionFactory name : %s, attempting to bind to JNDI", name );
+		LOG.debugf( "Attempting to bind SessionFactory [%s] to JNDI", name );
 
 		try {
 			jndiService.bind( name, instance );
 			LOG.factoryBoundToJndiName( name );
 			try {
 				jndiService.addListener( name, LISTENER );
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
 
-	public void removeSessionFactory(String uuid, String name, JndiService jndiService) {
+	public void removeSessionFactory(
+			String uuid,
+			String name,
+			boolean isNameAlsoJndiName,
+			JndiService jndiService) {
 		if ( name != null ) {
-			try {
-				LOG.tracef( "Unbinding SessionFactory from JNDI : %s", name );
-				jndiService.unbind( name );
-				LOG.factoryUnboundFromJndiName( name );
-			}
-			catch ( JndiNameException e ) {
-				LOG.invalidJndiName( name, e );
-			}
-			catch ( JndiException e ) {
-				LOG.unableToUnbindFactoryFromJndi( e );
-			}
-
 			nameUuidXref.remove( name );
+
+			if ( isNameAlsoJndiName ) {
+				try {
+					LOG.tracef( "Unbinding SessionFactory from JNDI : %s", name );
+					jndiService.unbind( name );
+					LOG.factoryUnboundFromJndiName( name );
+				}
+				catch ( JndiNameException e ) {
+					LOG.invalidJndiName( name, e );
+				}
+				catch ( JndiException e ) {
+					LOG.unableToUnbindFactoryFromJndi( e );
+				}
+			}
 		}
 
 		sessionFactoryMap.remove( uuid );
 	}
 
 	public SessionFactory getNamedSessionFactory(String name) {
         LOG.debugf( "Lookup: name=%s", name );
 		final String uuid = nameUuidXref.get( name );
 		return getSessionFactory( uuid );
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
 
 	/**
 	 * Implementation of {@literal JNDI} {@link javax.naming.event.NamespaceChangeListener} contract to listener for context events
 	 * and react accordingly if necessary
 	 */
 	private final NamespaceChangeListener LISTENER = new NamespaceChangeListener() {
 		@Override
 		public void objectAdded(NamingEvent evt) {
             LOG.debugf("A factory was successfully bound to name: %s", evt.getNewBinding().getName());
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
             LOG.namingExceptionAccessingFactory(evt.getException());
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
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
index c0738d9b6c..837aa39be0 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
@@ -1,245 +1,258 @@
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
 package org.hibernate.jmx;
 
 import java.io.InvalidObjectException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryRegistry;
+import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.service.jndi.internal.JndiServiceImpl;
 import org.hibernate.stat.Statistics;
 
 /**
  * A flyweight for <tt>SessionFactory</tt>. If the MBean itself does not
  * have classpath to the persistent classes, then a stub will be registered
  * with JNDI and the actual <tt>SessionFactoryImpl</tt> built upon first
  * access.
  *
  * @author Gavin King
  *
  * @deprecated See <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6190">HHH-6190</a> for details
  */
 @Deprecated
+@SuppressWarnings( {"deprecation"})
 public class SessionFactoryStub implements SessionFactory {
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryStub.class.getName());
 
 	private transient SessionFactory impl;
 	private transient HibernateService service;
 	private String uuid;
 	private String name;
 
 	SessionFactoryStub(HibernateService service) {
 		this.service = service;
 		this.name = service.getJndiName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
-		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, new JndiServiceImpl( service.getProperties() )  );
+		SessionFactoryRegistry.INSTANCE.addSessionFactory(
+				uuid,
+				name,
+				ConfigurationHelper.getBoolean(
+						AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI,
+						service.getProperties(),
+						true
+				),
+				this,
+				new JndiServiceImpl( service.getProperties() )
+		);
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return impl.getSessionFactoryOptions();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return getImpl().withOptions();
 	}
 
 	public Session openSession() throws HibernateException {
 		return getImpl().openSession();
 	}
 
 	public Session getCurrentSession() {
 		return getImpl().getCurrentSession();
 	}
 
 	private synchronized SessionFactory getImpl() {
 		if (impl==null) impl = service.buildSessionFactory();
 		return impl;
 	}
 
 	//readResolveObject
 	private Object readResolve() throws ObjectStreamException {
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid ) ;
 		if ( result == null ) {
             // in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
 			LOG.debug("Resolved stub SessionFactory by name");
 		}
 		else {
 			LOG.debug("Resolved stub SessionFactory by UUID");
 		}
 		return result;
 	}
 
 	/**
 	 * @see javax.naming.Referenceable#getReference()
 	 */
 	@Override
 	public Reference getReference() throws NamingException {
 		return new Reference(
 				SessionFactoryStub.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getImpl().getClassMetadata(persistentClass);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName)
 	throws HibernateException {
 		return getImpl().getClassMetadata(entityName);
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return getImpl().getCollectionMetadata(roleName);
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return getImpl().getAllClassMetadata();
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return getImpl().getAllCollectionMetadata();
 	}
 
 	public void close() throws HibernateException {
 	}
 
 	public boolean isClosed() {
 		return false;
 	}
 
 	public Cache getCache() {
 		return getImpl().getCache();
 	}
 
 	public void evict(Class persistentClass, Serializable id)
 		throws HibernateException {
 		getImpl().evict(persistentClass, id);
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getImpl().evict(persistentClass);
 	}
 
 	public void evictEntity(String entityName, Serializable id)
 	throws HibernateException {
 		getImpl().evictEntity(entityName, id);
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getImpl().evictEntity(entityName);
 	}
 
 	public void evictCollection(String roleName, Serializable id)
 		throws HibernateException {
 		getImpl().evictCollection(roleName, id);
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getImpl().evictCollection(roleName);
 	}
 
 	public void evictQueries() throws HibernateException {
 		getImpl().evictQueries();
 	}
 
 	public void evictQueries(String cacheRegion) throws HibernateException {
 		getImpl().evictQueries(cacheRegion);
 	}
 
 	public Statistics getStatistics() {
 		return getImpl().getStatistics();
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return getImpl().withStatelessOptions();
 	}
 
 	public StatelessSession openStatelessSession() {
 		return getImpl().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection conn) {
 		return getImpl().openStatelessSession(conn);
 	}
 
 	public Set getDefinedFilterNames() {
 		return getImpl().getDefinedFilterNames();
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		return getImpl().getFilterDefinition( filterName );
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return getImpl().containsFetchProfileDefinition( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return getImpl().getTypeHelper();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/serialization/SessionFactorySerializationTest.java b/hibernate-core/src/test/java/org/hibernate/serialization/SessionFactorySerializationTest.java
new file mode 100644
index 0000000000..6c4fdeffda
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/serialization/SessionFactorySerializationTest.java
@@ -0,0 +1,103 @@
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
+package org.hibernate.serialization;
+
+
+import javax.naming.NamingException;
+import javax.naming.Reference;
+import javax.naming.StringRefAddr;
+
+import java.io.InvalidObjectException;
+
+import org.hibernate.SessionFactory;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.id.UUIDGenerator;
+import org.hibernate.internal.SessionFactoryRegistry;
+import org.hibernate.internal.util.SerializationHelper;
+import org.hibernate.type.SerializationException;
+
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertSame;
+import static org.junit.Assert.fail;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SessionFactorySerializationTest extends BaseUnitTestCase {
+	public static final String NAME = "mySF";
+
+	@Test
+	public void testNamedSessionFactorySerialization() throws Exception {
+		Configuration cfg = new Configuration()
+				.setProperty( AvailableSettings.SESSION_FACTORY_NAME, NAME )
+				.setProperty( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, "false" ); // default is true
+		SessionFactory factory = cfg.buildSessionFactory();
+
+		// we need to do some tricking here so that Hibernate thinks the deserialization happens in a
+		// different VM
+		Reference reference = factory.getReference();
+		StringRefAddr refAddr = (StringRefAddr) reference.get( "uuid" );
+		String uuid = (String) refAddr.getContent();
+		// deregister under this uuid...
+		SessionFactoryRegistry.INSTANCE.removeSessionFactory( uuid, NAME, false, null );
+		// and then register under a different uuid...
+		SessionFactoryRegistry.INSTANCE.addSessionFactory( "some-other-uuid", NAME, false, factory, null );
+
+		SessionFactory factory2 = (SessionFactory) SerializationHelper.clone( factory );
+		assertSame( factory, factory2 );
+		factory.close();
+	}
+
+	@Test
+	public void testUnNamedSessionFactorySerialization() throws Exception {
+		// IMPL NOTE : this test is a control to testNamedSessionFactorySerialization
+		// 		here, the test should fail based just on attempted uuid resolution
+		Configuration cfg = new Configuration()
+				.setProperty( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, "false" ); // default is true
+		SessionFactory factory = cfg.buildSessionFactory();
+
+		// we need to do some tricking here so that Hibernate thinks the deserialization happens in a
+		// different VM
+		Reference reference = factory.getReference();
+		StringRefAddr refAddr = (StringRefAddr) reference.get( "uuid" );
+		String uuid = (String) refAddr.getContent();
+		// deregister under this uuid...
+		SessionFactoryRegistry.INSTANCE.removeSessionFactory( uuid, NAME, false, null );
+		// and then register under a different uuid...
+		SessionFactoryRegistry.INSTANCE.addSessionFactory( "some-other-uuid", NAME, false, factory, null );
+
+		try {
+			SerializationHelper.clone( factory );
+			fail( "Expecting an error" );
+		}
+		catch ( SerializationException expected ) {
+
+		}
+		factory.close();
+	}
+}
