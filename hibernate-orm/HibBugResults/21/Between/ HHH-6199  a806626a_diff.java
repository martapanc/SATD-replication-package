diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index 27e21e5188..d502a96ba0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,531 +1,531 @@
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
 	 * Names a {@literal JNDI} namespace into which the {@link org.hibernate.SessionFactory} should be bound.
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
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
 	 * The <tt>CacheProvider</tt> implementation class
 	 *
 	 * @deprecated See {@link #CACHE_REGION_FACTORY}
 	 */
 	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
 
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
-	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
+	 * The {@link org.hibernate.exception.spi.SQLExceptionConverter} to use for converting SQLExceptions
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
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
index edaac0cdfd..74601a3004 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
@@ -1,694 +1,694 @@
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.dialect;
-import java.sql.CallableStatement;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.sql.Types;
-import org.hibernate.LockMode;
-import org.hibernate.MappingException;
-import org.hibernate.cfg.Environment;
-import org.hibernate.dialect.function.ConditionalParenthesisFunction;
-import org.hibernate.dialect.function.ConvertFunction;
-import org.hibernate.dialect.function.NoArgSQLFunction;
-import org.hibernate.dialect.function.NvlFunction;
-import org.hibernate.dialect.function.SQLFunctionTemplate;
-import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
-import org.hibernate.dialect.function.StandardSQLFunction;
-import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.dialect.lock.LockingStrategy;
-import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
-import org.hibernate.dialect.lock.OptimisticLockingStrategy;
-import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
-import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
-import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
-import org.hibernate.dialect.lock.SelectLockingStrategy;
-import org.hibernate.dialect.lock.UpdateLockingStrategy;
-import org.hibernate.exception.CacheSQLStateConverter;
-import org.hibernate.exception.SQLExceptionConverter;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
-import org.hibernate.id.IdentityGenerator;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.persister.entity.Lockable;
-import org.hibernate.sql.CacheJoinFragment;
-import org.hibernate.sql.JoinFragment;
-import org.hibernate.type.StandardBasicTypes;
-
-/**
- * Cach&eacute; 2007.1 dialect. This class is required in order to use Hibernate with Intersystems Cach� SQL.<br>
- * <br>
- * Compatible with Cach� 2007.1.
- * <br>
- * <head>
- * <title>Cach&eacute; and Hibernate</title>
- * </head>
- * <body>
- * <h1>Cach&eacute; and Hibernate</h1>
- * <h2>PREREQUISITES</h2>
- * These setup instructions assume that both Cach&eacute; and Hibernate are installed and operational.
- * <br>
- * <h2>HIBERNATE DIRECTORIES AND FILES</h2>
- * JBoss distributes the InterSystems Cache' dialect for Hibernate 3.2.1
- * For earlier versions of Hibernate please contact
- * <a href="http://www.intersystems.com/support/cache-support.html">InterSystems Worldwide Response Center</A> (WRC)
- * for the appropriate source files.
- * <br>
- * <h2>CACH&Eacute; DOCUMENTATION</h2>
- * Documentation for Cach&eacute; is available online when Cach&eacute; is running.
- * It can also be obtained from the
- * <a href="http://www.intersystems.com/cache/downloads/documentation.html">InterSystems</A> website.
- * The book, "Object-oriented Application Development Using the Cach&eacute; Post-relational Database:
- * is also available from Springer-Verlag.
- * <br>
- * <h2>HIBERNATE DOCUMENTATION</h2>
- * Hibernate comes with extensive electronic documentation.
- * In addition, several books on Hibernate are available from
- * <a href="http://www.manning.com">Manning Publications Co</a>.
- * Three available titles are "Hibernate Quickly", "Hibernate in Action", and "Java Persistence with Hibernate".
- * <br>
- * <h2>TO SET UP HIBERNATE FOR USE WITH CACH&Eacute;</h2>
- * The following steps assume that the directory where Cach&eacute; was installed is C:\CacheSys.
- * This is the default installation directory for  Cach&eacute;.
- * The default installation directory for Hibernate is assumed to be C:\Hibernate.
- * <p/>
- * If either product is installed in a different location, the pathnames that follow should be modified appropriately.
- * <p/>
- * Cach&eacute; version 2007.1 and above is recommended for use with
- * Hibernate.  The next step depends on the location of your
- * CacheDB.jar depending on your version of Cach&eacute;.
- * <ol>
- * <li>Copy C:\CacheSys\dev\java\lib\JDK15\CacheDB.jar to C:\Hibernate\lib\CacheDB.jar.</li>
- * <p/>
- * <li>Insert the following files into your Java classpath:
- * <p/>
- * <ul>
- * <li>All jar files in the directory C:\Hibernate\lib</li>
- * <li>The directory (or directories) where hibernate.properties and/or hibernate.cfg.xml are kept.</li>
- * </ul>
- * </li>
- * <p/>
- * <li>In the file, hibernate.properties (or hibernate.cfg.xml),
- * specify the Cach&eacute; dialect and the Cach&eacute; version URL settings.</li>
- * </ol>
- * <p/>
- * For example, in Hibernate 3.2, typical entries in hibernate.properties would have the following
- * "name=value" pairs:
- * <p/>
- * <table cols=3 border cellpadding=5 cellspacing=0>
- * <tr>
- * <th>Property Name</th>
- * <th>Property Value</th>
- * </tr>
- * <tr>
- * <td>hibernate.dialect</td>
- * <td>org.hibernate.dialect.Cache71Dialect</td>
- * </tr>
- * <tr>
- * <td>hibernate.connection.driver_class</td>
- * <td>com.intersys.jdbc.CacheDriver</td>
- * </tr>
- * <tr>
- * <td>hibernate.connection.username</td>
- * <td>(see note 1)</td>
- * </tr>
- * <tr>
- * <td>hibernate.connection.password</td>
- * <td>(see note 1)</td>
- * </tr>
- * <tr>
- * <td>hibernate.connection.url</td>
- * <td>jdbc:Cache://127.0.0.1:1972/USER</td>
- * </tr>
- * </table>
- * <p/>
- * <dl>
- * <dt><b>Note 1</b></dt>
- * <dd>Please contact your administrator for the userid and password you should use when attempting access via JDBC.
- * By default, these are chosen to be "_SYSTEM" and "SYS" respectively as noted in the SQL standard.</dd>
- * </dl>
- * <br>
- * <h2>CACH&Eacute; VERSION URL</h2>
- * This is the standard URL for the JDBC driver.
- * For a JDBC driver on the machine hosting Cach&eacute;, use the IP "loopback" address, 127.0.0.1.
- * For 1972, the default port, specify the super server port of your Cach&eacute; instance.
- * For USER, substitute the NAMESPACE which contains your Cach&eacute; database data.
- * <br>
- * <h2>CACH&Eacute; DIALECTS</h2>
- * Choices for Dialect are:
- * <br>
- * <p/>
- * <ol>
- * <li>org.hibernate.dialect.Cache71Dialect (requires Cach&eacute;
- * 2007.1 or above)</li>
- * <p/>
- * </ol>
- * <br>
- * <h2>SUPPORT FOR IDENTITY COLUMNS</h2>
- * Cach&eacute; 2007.1 or later supports identity columns.  For
- * Hibernate to use identity columns, specify "native" as the
- * generator.
- * <br>
- * <h2>SEQUENCE DIALECTS SUPPORT SEQUENCES</h2>
- * <p/>
- * To use Hibernate sequence support with Cach&eacute; in a namespace, you must FIRST load the following file into that namespace:
- * <pre>
- *     etc\CacheSequences.xml
- * </pre>
- * For example, at the COS terminal prompt in the namespace, run the
- * following command:
- * <p>
- * d LoadFile^%apiOBJ("c:\hibernate\etc\CacheSequences.xml","ck")
- * <p>
- * In your Hibernate mapping you can specify sequence use.
- * <p>
- * For example, the following shows the use of a sequence generator in a Hibernate mapping:
- * <pre>
- *     &lt;id name="id" column="uid" type="long" unsaved-value="null"&gt;
- *         &lt;generator class="sequence"/&gt;
- *     &lt;/id&gt;
- * </pre>
- * <br>
- * <p/>
- * Some versions of Hibernate under some circumstances call
- * getSelectSequenceNextValString() in the dialect.  If this happens
- * you will receive the error message: new MappingException( "Dialect
- * does not support sequences" ).
- * <br>
- * <h2>HIBERNATE FILES ASSOCIATED WITH CACH&Eacute; DIALECT</h2>
- * The following files are associated with Cach&eacute; dialect:
- * <p/>
- * <ol>
- * <li>src\org\hibernate\dialect\Cache71Dialect.java</li>
- * <li>src\org\hibernate\dialect\function\ConditionalParenthesisFunction.java</li>
- * <li>src\org\hibernate\dialect\function\ConvertFunction.java</li>
- * <li>src\org\hibernate\exception\CacheSQLStateConverter.java</li>
- * <li>src\org\hibernate\sql\CacheJoinFragment.java</li>
- * </ol>
- * Cache71Dialect ships with Hibernate 3.2.  All other dialects are distributed by InterSystems and subclass Cache71Dialect.
- *
- * @author Jonathan Levinson
- */
-
-public class Cache71Dialect extends Dialect {
-
-	/**
-	 * Creates new <code>Cach�71Dialect</code> instance. Sets up the JDBC /
-	 * Cach� type mappings.
-	 */
-	public Cache71Dialect() {
-		super();
-		commonRegistration();
-		register71Functions();
-	}
-
-	protected final void commonRegistration() {
-		// Note: For object <-> SQL datatype mappings see:
-		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
-		//
-		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
-		// changed 08-11-2005, jsl
-		registerColumnType( Types.BINARY, "varbinary($1)" );
-		registerColumnType( Types.BIGINT, "BigInt" );
-		registerColumnType( Types.BIT, "bit" );
-		registerColumnType( Types.CHAR, "char(1)" );
-		registerColumnType( Types.DATE, "date" );
-		registerColumnType( Types.DECIMAL, "decimal" );
-		registerColumnType( Types.DOUBLE, "double" );
-		registerColumnType( Types.FLOAT, "float" );
-		registerColumnType( Types.INTEGER, "integer" );
-		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );	// binary %Stream
-		registerColumnType( Types.LONGVARCHAR, "longvarchar" );		// character %Stream
-		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
-		registerColumnType( Types.REAL, "real" );
-		registerColumnType( Types.SMALLINT, "smallint" );
-		registerColumnType( Types.TIMESTAMP, "timestamp" );
-		registerColumnType( Types.TIME, "time" );
-		registerColumnType( Types.TINYINT, "tinyint" );
-		// TBD should this be varbinary($1)?
-		//		registerColumnType(Types.VARBINARY,     "binary($1)");
-		registerColumnType( Types.VARBINARY, "longvarbinary" );
-		registerColumnType( Types.VARCHAR, "varchar($l)" );
-		registerColumnType( Types.BLOB, "longvarbinary" );
-		registerColumnType( Types.CLOB, "longvarchar" );
-
-		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
-		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
-		//getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
-
-		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
-
-		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
-		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
-		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
-		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
-		// hibernate impelemnts cast in Dialect.java
-		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
-		registerFunction( "char", new StandardJDBCEscapeFunction( "char", StandardBasicTypes.CHARACTER ) );
-		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
-		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
-		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
-		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
-		registerFunction( "convert", new ConvertFunction() );
-		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", StandardBasicTypes.DATE ) );
-		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
-		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
-		registerFunction(
-				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP )
-		);
-		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", StandardBasicTypes.TIME ) );
-		registerFunction( "database", new StandardJDBCEscapeFunction( "database", StandardBasicTypes.STRING ) );
-		registerFunction( "dateadd", new VarArgsSQLFunction( StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")" ) );
-		registerFunction( "datediff", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datediff(", ",", ")" ) );
-		registerFunction( "datename", new VarArgsSQLFunction( StandardBasicTypes.STRING, "datename(", ",", ")" ) );
-		registerFunction( "datepart", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datepart(", ",", ")" ) );
-		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
-		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", StandardBasicTypes.STRING ) );
-		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
-		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
-		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
-		// is it necessary to register %exact since it can only appear in a where clause?
-		registerFunction( "%exact", new StandardSQLFunction( "%exact", StandardBasicTypes.STRING ) );
-		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "%external", new StandardSQLFunction( "%external", StandardBasicTypes.STRING ) );
-		registerFunction( "$extract", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$extract(", ",", ")" ) );
-		registerFunction( "$find", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$find(", ",", ")" ) );
-		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
-		registerFunction( "getdate", new StandardSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
-		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", StandardBasicTypes.INTEGER ) );
-		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
-		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
-		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
-		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", StandardBasicTypes.INTEGER ) );
-		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", StandardBasicTypes.STRING ) );
-		registerFunction( "left", new StandardJDBCEscapeFunction( "left", StandardBasicTypes.STRING ) );
-		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
-		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
-		// aggregate functions shouldn't be registered, right?
-		//registerFunction( "list", new StandardSQLFunction("list",StandardBasicTypes.STRING) );
-		// stopped on $list
-		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
-		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
-		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
-		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
-		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", StandardBasicTypes.INTEGER ) );
-		registerFunction( "locate", new StandardSQLFunction( "$FIND", StandardBasicTypes.INTEGER ) );
-		registerFunction( "log", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
-		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
-		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", StandardBasicTypes.INTEGER ) );
-		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "month", new StandardJDBCEscapeFunction( "month", StandardBasicTypes.INTEGER ) );
-		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.STRING ) );
-		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.TIMESTAMP ) );
-		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
-		registerFunction( "nvl", new NvlFunction() );
-		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
-		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
-		registerFunction( "%pattern", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%pattern", "" ) );
-		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "$piece", new VarArgsSQLFunction( StandardBasicTypes.STRING, "$piece(", ",", ")" ) );
-		registerFunction( "position", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "position(", " in ", ")" ) );
-		registerFunction( "power", new VarArgsSQLFunction( StandardBasicTypes.STRING, "power(", ",", ")" ) );
-		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", StandardBasicTypes.INTEGER ) );
-		registerFunction( "repeat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "repeat(", ",", ")" ) );
-		registerFunction( "replicate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "replicate(", ",", ")" ) );
-		registerFunction( "right", new StandardJDBCEscapeFunction( "right", StandardBasicTypes.STRING ) );
-		registerFunction( "round", new VarArgsSQLFunction( StandardBasicTypes.FLOAT, "round(", ",", ")" ) );
-		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
-		registerFunction( "second", new StandardJDBCEscapeFunction( "second", StandardBasicTypes.INTEGER ) );
-		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
-		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
-		registerFunction( "%sqlstring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlstring(", ",", ")" ) );
-		registerFunction( "%sqlupper", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlupper(", ",", ")" ) );
-		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "%startswith", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%startswith", "" ) );
-		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
-		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as char varying)" ) );
-		registerFunction( "string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "string(", ",", ")" ) );
-		// note that %string is deprecated
-		registerFunction( "%string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%string(", ",", ")" ) );
-		registerFunction( "substr", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substr(", ",", ")" ) );
-		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
-		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
-		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", StandardBasicTypes.DOUBLE ) );
-		registerFunction( "tochar", new VarArgsSQLFunction( StandardBasicTypes.STRING, "tochar(", ",", ")" ) );
-		registerFunction( "to_char", new VarArgsSQLFunction( StandardBasicTypes.STRING, "to_char(", ",", ")" ) );
-		registerFunction( "todate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
-		registerFunction( "to_date", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
-		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
-		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
-		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
-		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
-		//registerFunction( "trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 from ?3)") );
-		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", StandardBasicTypes.STRING ) );
-		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", StandardBasicTypes.STRING ) );
-		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
-		// %upper is deprecated
-		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
-		registerFunction( "user", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.STRING ) );
-		registerFunction( "week", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.INTEGER ) );
-		registerFunction( "xmlconcat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlconcat(", ",", ")" ) );
-		registerFunction( "xmlelement", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlelement(", ",", ")" ) );
-		// xmlforest requires a new kind of function constructor
-		registerFunction( "year", new StandardJDBCEscapeFunction( "year", StandardBasicTypes.INTEGER ) );
-	}
-
-	protected final void register71Functions() {
-		this.registerFunction( "str", new VarArgsSQLFunction( StandardBasicTypes.STRING, "str(", ",", ")" ) );
-	}
-
-	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean hasAlterTable() {
-		// Does this dialect support the ALTER TABLE syntax?
-		return true;
-	}
-
-	public boolean qualifyIndexName() {
-		// Do we need to qualify index names with the schema name?
-		return false;
-	}
-
-	public boolean supportsUnique() {
-		// Does this dialect support the UNIQUE column syntax?
-		return true;
-	}
-
-	/**
-	 * The syntax used to add a foreign key constraint to a table.
-	 *
-	 * @return String
-	 */
-	public String getAddForeignKeyConstraintString(
-			String constraintName,
-			String[] foreignKey,
-			String referencedTable,
-			String[] primaryKey,
-			boolean referencesPrimaryKey) {
-		// The syntax used to add a foreign key constraint to a table.
-		return new StringBuffer( 300 )
-				.append( " ADD CONSTRAINT " )
-				.append( constraintName )
-				.append( " FOREIGN KEY " )
-				.append( constraintName )
-				.append( " (" )
-				.append( StringHelper.join( ", ", foreignKey ) )	// identifier-commalist
-				.append( ") REFERENCES " )
-				.append( referencedTable )
-				.append( " (" )
-				.append( StringHelper.join( ", ", primaryKey ) ) // identifier-commalist
-				.append( ") " )
-				.toString();
-	}
-
-	public boolean supportsCheck() {
-		// Does this dialect support check constraints?
-		return false;
-	}
-
-	public String getAddColumnString() {
-		// The syntax used to add a column to a table
-		return " add column";
-	}
-
-	public String getCascadeConstraintsString() {
-		// Completely optional cascading drop clause.
-		return "";
-	}
-
-	public boolean dropConstraints() {
-		// Do we need to drop constraints before dropping tables in this dialect?
-		return true;
-	}
-
-	public boolean supportsCascadeDelete() {
-		return true;
-	}
-
-	public boolean hasSelfReferentialForeignKeyBug() {
-		return true;
-	}
-
-	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean supportsTemporaryTables() {
-		return true;
-	}
-
-	public String generateTemporaryTableName(String baseTableName) {
-		String name = super.generateTemporaryTableName( baseTableName );
-		return name.length() > 25 ? name.substring( 1, 25 ) : name;
-	}
-
-	public String getCreateTemporaryTableString() {
-		return "create global temporary table";
-	}
-
-	public Boolean performTemporaryTableDDLInIsolation() {
-		return Boolean.FALSE;
-	}
-
-	public String getCreateTemporaryTablePostfix() {
-		return "";
-	}
-
-	public boolean dropTemporaryTableAfterUse() {
-		return true;
-	}
-
-	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean supportsIdentityColumns() {
-		return true;
-	}
-
-	public Class getNativeIdentifierGeneratorClass() {
-		return IdentityGenerator.class;
-	}
-
-	public boolean hasDataTypeInIdentityColumn() {
-		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
-		// data type
-		return true;
-	}
-
-	public String getIdentityColumnString() throws MappingException {
-		// The keyword used to specify an identity column, if identity column key generation is supported.
-		return "identity";
-	}
-
-	public String getIdentitySelectString() {
-		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
-	}
-
-	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean supportsSequences() {
-		return false;
-	}
-
-// It really does support sequences, but InterSystems elects to suggest usage of IDENTITY instead :/
-// Anyway, below are the actual support overrides for users wanting to use this combo...
-//
-//	public String getSequenceNextValString(String sequenceName) {
-//		return "select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
-//	}
-//
-//	public String getSelectSequenceNextValString(String sequenceName) {
-//		return "(select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "'))";
-//	}
-//
-//	public String getCreateSequenceString(String sequenceName) {
-//		return "insert into InterSystems.Sequences(Name) values (ucase('" + sequenceName + "'))";
-//	}
-//
-//	public String getDropSequenceString(String sequenceName) {
-//		return "delete from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
-//	}
-//
-//	public String getQuerySequencesString() {
-//		return "select name from InterSystems.Sequences";
-//	}
-
-	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean supportsForUpdate() {
-		// Does this dialect support the FOR UPDATE syntax?
-		return false;
-	}
-
-	public boolean supportsForUpdateOf() {
-		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
-		return false;
-	}
-
-	public boolean supportsForUpdateNowait() {
-		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
-		return false;
-	}
-
-	public boolean supportsOuterJoinForUpdate() {
-		return false;
-	}
-
-	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
-		// InterSystems Cache' does not current support "SELECT ... FOR UPDATE" syntax...
-		// Set your transaction mode to READ_COMMITTED before using
-		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
-			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
-		}
-		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
-			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
-		}
-		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
-			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
-		}
-		else if ( lockMode==LockMode.OPTIMISTIC) {
-			return new OptimisticLockingStrategy( lockable, lockMode);
-		}
-		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
-			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
-		}
-		else if ( lockMode.greaterThan( LockMode.READ ) ) {
-			return new UpdateLockingStrategy( lockable, lockMode );
-		}
-		else {
-			return new SelectLockingStrategy( lockable, lockMode );
-		}
-	}
-
-	// LIMIT support (ala TOP) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean supportsLimit() {
-		return true;
-	}
-
-	public boolean supportsLimitOffset() {
-		return false;
-	}
-
-	public boolean supportsVariableLimit() {
-		return true;
-	}
-
-	public boolean bindLimitParametersFirst() {
-		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
-		return true;
-	}
-
-	public boolean useMaxForLimit() {
-		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
-		return true;
-	}
-
-	public String getLimitString(String sql, boolean hasOffset) {
-		if ( hasOffset ) {
-			throw new UnsupportedOperationException( "query result offset is not supported" );
-		}
-
-		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
-		// but this extension is not supported through Hibernate anyway.
-		int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
-
-		return new StringBuffer( sql.length() + 8 )
-				.append( sql )
-				.insert( insertionPoint, " TOP ? " )
-				.toString();
-	}
-
-	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
-		return col;
-	}
-
-	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
-		ps.execute();
-		return ( ResultSet ) ps.getObject( 1 );
-	}
-
-	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public String getLowercaseFunction() {
-		// The name of the SQL function that transforms a string to lowercase
-		return "lower";
-	}
-
-	public String getNullColumnString() {
-		// The keyword used to specify a nullable column.
-		return " null";
-	}
-
-	public JoinFragment createOuterJoinFragment() {
-		// Create an OuterJoinGenerator for this dialect.
-		return new CacheJoinFragment();
-	}
-
-	public String getNoColumnsInsertString() {
-		// The keyword used to insert a row without specifying
-		// any column values
-		return " default values";
-	}
-
-	public SQLExceptionConverter buildSQLExceptionConverter() {
-		return new CacheSQLStateConverter( EXTRACTER );
-	}
-
-	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
-		/**
-		 * Extract the name of the violated constraint from the given SQLException.
-		 *
-		 * @param sqle The exception that was the result of the constraint violation.
-		 * @return The extracted constraint name.
-		 */
-		public String extractConstraintName(SQLException sqle) {
-			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
-		}
-	};
-
-
-	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	public boolean supportsEmptyInList() {
-		return false;
-	}
-
-	public boolean areStringComparisonsCaseInsensitive() {
-		return true;
-	}
-
-	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
-		return false;
-	}
-}
+import java.sql.CallableStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import org.hibernate.LockMode;
+import org.hibernate.MappingException;
+import org.hibernate.cfg.Environment;
+import org.hibernate.dialect.function.ConditionalParenthesisFunction;
+import org.hibernate.dialect.function.ConvertFunction;
+import org.hibernate.dialect.function.NoArgSQLFunction;
+import org.hibernate.dialect.function.NvlFunction;
+import org.hibernate.dialect.function.SQLFunctionTemplate;
+import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
+import org.hibernate.dialect.function.StandardSQLFunction;
+import org.hibernate.dialect.function.VarArgsSQLFunction;
+import org.hibernate.dialect.lock.LockingStrategy;
+import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
+import org.hibernate.dialect.lock.OptimisticLockingStrategy;
+import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
+import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
+import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
+import org.hibernate.dialect.lock.SelectLockingStrategy;
+import org.hibernate.dialect.lock.UpdateLockingStrategy;
+import org.hibernate.exception.internal.CacheSQLStateConverter;
+import org.hibernate.exception.spi.SQLExceptionConverter;
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.id.IdentityGenerator;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.persister.entity.Lockable;
+import org.hibernate.sql.CacheJoinFragment;
+import org.hibernate.sql.JoinFragment;
+import org.hibernate.type.StandardBasicTypes;
+
+/**
+ * Cach&eacute; 2007.1 dialect. This class is required in order to use Hibernate with Intersystems Cach� SQL.<br>
+ * <br>
+ * Compatible with Cach� 2007.1.
+ * <br>
+ * <head>
+ * <title>Cach&eacute; and Hibernate</title>
+ * </head>
+ * <body>
+ * <h1>Cach&eacute; and Hibernate</h1>
+ * <h2>PREREQUISITES</h2>
+ * These setup instructions assume that both Cach&eacute; and Hibernate are installed and operational.
+ * <br>
+ * <h2>HIBERNATE DIRECTORIES AND FILES</h2>
+ * JBoss distributes the InterSystems Cache' dialect for Hibernate 3.2.1
+ * For earlier versions of Hibernate please contact
+ * <a href="http://www.intersystems.com/support/cache-support.html">InterSystems Worldwide Response Center</A> (WRC)
+ * for the appropriate source files.
+ * <br>
+ * <h2>CACH&Eacute; DOCUMENTATION</h2>
+ * Documentation for Cach&eacute; is available online when Cach&eacute; is running.
+ * It can also be obtained from the
+ * <a href="http://www.intersystems.com/cache/downloads/documentation.html">InterSystems</A> website.
+ * The book, "Object-oriented Application Development Using the Cach&eacute; Post-relational Database:
+ * is also available from Springer-Verlag.
+ * <br>
+ * <h2>HIBERNATE DOCUMENTATION</h2>
+ * Hibernate comes with extensive electronic documentation.
+ * In addition, several books on Hibernate are available from
+ * <a href="http://www.manning.com">Manning Publications Co</a>.
+ * Three available titles are "Hibernate Quickly", "Hibernate in Action", and "Java Persistence with Hibernate".
+ * <br>
+ * <h2>TO SET UP HIBERNATE FOR USE WITH CACH&Eacute;</h2>
+ * The following steps assume that the directory where Cach&eacute; was installed is C:\CacheSys.
+ * This is the default installation directory for  Cach&eacute;.
+ * The default installation directory for Hibernate is assumed to be C:\Hibernate.
+ * <p/>
+ * If either product is installed in a different location, the pathnames that follow should be modified appropriately.
+ * <p/>
+ * Cach&eacute; version 2007.1 and above is recommended for use with
+ * Hibernate.  The next step depends on the location of your
+ * CacheDB.jar depending on your version of Cach&eacute;.
+ * <ol>
+ * <li>Copy C:\CacheSys\dev\java\lib\JDK15\CacheDB.jar to C:\Hibernate\lib\CacheDB.jar.</li>
+ * <p/>
+ * <li>Insert the following files into your Java classpath:
+ * <p/>
+ * <ul>
+ * <li>All jar files in the directory C:\Hibernate\lib</li>
+ * <li>The directory (or directories) where hibernate.properties and/or hibernate.cfg.xml are kept.</li>
+ * </ul>
+ * </li>
+ * <p/>
+ * <li>In the file, hibernate.properties (or hibernate.cfg.xml),
+ * specify the Cach&eacute; dialect and the Cach&eacute; version URL settings.</li>
+ * </ol>
+ * <p/>
+ * For example, in Hibernate 3.2, typical entries in hibernate.properties would have the following
+ * "name=value" pairs:
+ * <p/>
+ * <table cols=3 border cellpadding=5 cellspacing=0>
+ * <tr>
+ * <th>Property Name</th>
+ * <th>Property Value</th>
+ * </tr>
+ * <tr>
+ * <td>hibernate.dialect</td>
+ * <td>org.hibernate.dialect.Cache71Dialect</td>
+ * </tr>
+ * <tr>
+ * <td>hibernate.connection.driver_class</td>
+ * <td>com.intersys.jdbc.CacheDriver</td>
+ * </tr>
+ * <tr>
+ * <td>hibernate.connection.username</td>
+ * <td>(see note 1)</td>
+ * </tr>
+ * <tr>
+ * <td>hibernate.connection.password</td>
+ * <td>(see note 1)</td>
+ * </tr>
+ * <tr>
+ * <td>hibernate.connection.url</td>
+ * <td>jdbc:Cache://127.0.0.1:1972/USER</td>
+ * </tr>
+ * </table>
+ * <p/>
+ * <dl>
+ * <dt><b>Note 1</b></dt>
+ * <dd>Please contact your administrator for the userid and password you should use when attempting access via JDBC.
+ * By default, these are chosen to be "_SYSTEM" and "SYS" respectively as noted in the SQL standard.</dd>
+ * </dl>
+ * <br>
+ * <h2>CACH&Eacute; VERSION URL</h2>
+ * This is the standard URL for the JDBC driver.
+ * For a JDBC driver on the machine hosting Cach&eacute;, use the IP "loopback" address, 127.0.0.1.
+ * For 1972, the default port, specify the super server port of your Cach&eacute; instance.
+ * For USER, substitute the NAMESPACE which contains your Cach&eacute; database data.
+ * <br>
+ * <h2>CACH&Eacute; DIALECTS</h2>
+ * Choices for Dialect are:
+ * <br>
+ * <p/>
+ * <ol>
+ * <li>org.hibernate.dialect.Cache71Dialect (requires Cach&eacute;
+ * 2007.1 or above)</li>
+ * <p/>
+ * </ol>
+ * <br>
+ * <h2>SUPPORT FOR IDENTITY COLUMNS</h2>
+ * Cach&eacute; 2007.1 or later supports identity columns.  For
+ * Hibernate to use identity columns, specify "native" as the
+ * generator.
+ * <br>
+ * <h2>SEQUENCE DIALECTS SUPPORT SEQUENCES</h2>
+ * <p/>
+ * To use Hibernate sequence support with Cach&eacute; in a namespace, you must FIRST load the following file into that namespace:
+ * <pre>
+ *     etc\CacheSequences.xml
+ * </pre>
+ * For example, at the COS terminal prompt in the namespace, run the
+ * following command:
+ * <p>
+ * d LoadFile^%apiOBJ("c:\hibernate\etc\CacheSequences.xml","ck")
+ * <p>
+ * In your Hibernate mapping you can specify sequence use.
+ * <p>
+ * For example, the following shows the use of a sequence generator in a Hibernate mapping:
+ * <pre>
+ *     &lt;id name="id" column="uid" type="long" unsaved-value="null"&gt;
+ *         &lt;generator class="sequence"/&gt;
+ *     &lt;/id&gt;
+ * </pre>
+ * <br>
+ * <p/>
+ * Some versions of Hibernate under some circumstances call
+ * getSelectSequenceNextValString() in the dialect.  If this happens
+ * you will receive the error message: new MappingException( "Dialect
+ * does not support sequences" ).
+ * <br>
+ * <h2>HIBERNATE FILES ASSOCIATED WITH CACH&Eacute; DIALECT</h2>
+ * The following files are associated with Cach&eacute; dialect:
+ * <p/>
+ * <ol>
+ * <li>src\org\hibernate\dialect\Cache71Dialect.java</li>
+ * <li>src\org\hibernate\dialect\function\ConditionalParenthesisFunction.java</li>
+ * <li>src\org\hibernate\dialect\function\ConvertFunction.java</li>
+ * <li>src\org\hibernate\exception\CacheSQLStateConverter.java</li>
+ * <li>src\org\hibernate\sql\CacheJoinFragment.java</li>
+ * </ol>
+ * Cache71Dialect ships with Hibernate 3.2.  All other dialects are distributed by InterSystems and subclass Cache71Dialect.
+ *
+ * @author Jonathan Levinson
+ */
+
+public class Cache71Dialect extends Dialect {
+
+	/**
+	 * Creates new <code>Cach�71Dialect</code> instance. Sets up the JDBC /
+	 * Cach� type mappings.
+	 */
+	public Cache71Dialect() {
+		super();
+		commonRegistration();
+		register71Functions();
+	}
+
+	protected final void commonRegistration() {
+		// Note: For object <-> SQL datatype mappings see:
+		//	 Configuration Manager | Advanced | SQL | System DDL Datatype Mappings
+		//
+		//	TBD	registerColumnType(Types.BINARY,        "binary($1)");
+		// changed 08-11-2005, jsl
+		registerColumnType( Types.BINARY, "varbinary($1)" );
+		registerColumnType( Types.BIGINT, "BigInt" );
+		registerColumnType( Types.BIT, "bit" );
+		registerColumnType( Types.CHAR, "char(1)" );
+		registerColumnType( Types.DATE, "date" );
+		registerColumnType( Types.DECIMAL, "decimal" );
+		registerColumnType( Types.DOUBLE, "double" );
+		registerColumnType( Types.FLOAT, "float" );
+		registerColumnType( Types.INTEGER, "integer" );
+		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );	// binary %Stream
+		registerColumnType( Types.LONGVARCHAR, "longvarchar" );		// character %Stream
+		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
+		registerColumnType( Types.REAL, "real" );
+		registerColumnType( Types.SMALLINT, "smallint" );
+		registerColumnType( Types.TIMESTAMP, "timestamp" );
+		registerColumnType( Types.TIME, "time" );
+		registerColumnType( Types.TINYINT, "tinyint" );
+		// TBD should this be varbinary($1)?
+		//		registerColumnType(Types.VARBINARY,     "binary($1)");
+		registerColumnType( Types.VARBINARY, "longvarbinary" );
+		registerColumnType( Types.VARCHAR, "varchar($l)" );
+		registerColumnType( Types.BLOB, "longvarbinary" );
+		registerColumnType( Types.CLOB, "longvarchar" );
+
+		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "false" );
+		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
+		//getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
+
+		getDefaultProperties().setProperty( Environment.USE_SQL_COMMENTS, "false" );
+
+		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
+		registerFunction( "acos", new StandardJDBCEscapeFunction( "acos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "%alphaup", new StandardSQLFunction( "%alphaup", StandardBasicTypes.STRING ) );
+		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
+		registerFunction( "asin", new StandardJDBCEscapeFunction( "asin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "atan", new StandardJDBCEscapeFunction( "atan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "($length(?1)*8)" ) );
+		// hibernate impelemnts cast in Dialect.java
+		registerFunction( "ceiling", new StandardSQLFunction( "ceiling", StandardBasicTypes.INTEGER ) );
+		registerFunction( "char", new StandardJDBCEscapeFunction( "char", StandardBasicTypes.CHARACTER ) );
+		registerFunction( "character_length", new StandardSQLFunction( "character_length", StandardBasicTypes.INTEGER ) );
+		registerFunction( "char_length", new StandardSQLFunction( "char_length", StandardBasicTypes.INTEGER ) );
+		registerFunction( "cos", new StandardJDBCEscapeFunction( "cos", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "cot", new StandardJDBCEscapeFunction( "cot", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(", ",", ")" ) );
+		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
+		registerFunction( "convert", new ConvertFunction() );
+		registerFunction( "curdate", new StandardJDBCEscapeFunction( "curdate", StandardBasicTypes.DATE ) );
+		registerFunction( "current_date", new NoArgSQLFunction( "current_date", StandardBasicTypes.DATE, false ) );
+		registerFunction( "current_time", new NoArgSQLFunction( "current_time", StandardBasicTypes.TIME, false ) );
+		registerFunction(
+				"current_timestamp", new ConditionalParenthesisFunction( "current_timestamp", StandardBasicTypes.TIMESTAMP )
+		);
+		registerFunction( "curtime", new StandardJDBCEscapeFunction( "curtime", StandardBasicTypes.TIME ) );
+		registerFunction( "database", new StandardJDBCEscapeFunction( "database", StandardBasicTypes.STRING ) );
+		registerFunction( "dateadd", new VarArgsSQLFunction( StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")" ) );
+		registerFunction( "datediff", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datediff(", ",", ")" ) );
+		registerFunction( "datename", new VarArgsSQLFunction( StandardBasicTypes.STRING, "datename(", ",", ")" ) );
+		registerFunction( "datepart", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "datepart(", ",", ")" ) );
+		registerFunction( "day", new StandardSQLFunction( "day", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayname", new StandardJDBCEscapeFunction( "dayname", StandardBasicTypes.STRING ) );
+		registerFunction( "dayofmonth", new StandardJDBCEscapeFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofweek", new StandardJDBCEscapeFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
+		registerFunction( "dayofyear", new StandardJDBCEscapeFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
+		// is it necessary to register %exact since it can only appear in a where clause?
+		registerFunction( "%exact", new StandardSQLFunction( "%exact", StandardBasicTypes.STRING ) );
+		registerFunction( "exp", new StandardJDBCEscapeFunction( "exp", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "%external", new StandardSQLFunction( "%external", StandardBasicTypes.STRING ) );
+		registerFunction( "$extract", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$extract(", ",", ")" ) );
+		registerFunction( "$find", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "$find(", ",", ")" ) );
+		registerFunction( "floor", new StandardSQLFunction( "floor", StandardBasicTypes.INTEGER ) );
+		registerFunction( "getdate", new StandardSQLFunction( "getdate", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "hour", new StandardJDBCEscapeFunction( "hour", StandardBasicTypes.INTEGER ) );
+		registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(", ",", ")" ) );
+		registerFunction( "%internal", new StandardSQLFunction( "%internal" ) );
+		registerFunction( "isnull", new VarArgsSQLFunction( "isnull(", ",", ")" ) );
+		registerFunction( "isnumeric", new StandardSQLFunction( "isnumeric", StandardBasicTypes.INTEGER ) );
+		registerFunction( "lcase", new StandardJDBCEscapeFunction( "lcase", StandardBasicTypes.STRING ) );
+		registerFunction( "left", new StandardJDBCEscapeFunction( "left", StandardBasicTypes.STRING ) );
+		registerFunction( "len", new StandardSQLFunction( "len", StandardBasicTypes.INTEGER ) );
+		registerFunction( "$length", new VarArgsSQLFunction( "$length(", ",", ")" ) );
+		// aggregate functions shouldn't be registered, right?
+		//registerFunction( "list", new StandardSQLFunction("list",StandardBasicTypes.STRING) );
+		// stopped on $list
+		registerFunction( "$list", new VarArgsSQLFunction( "$list(", ",", ")" ) );
+		registerFunction( "$listdata", new VarArgsSQLFunction( "$listdata(", ",", ")" ) );
+		registerFunction( "$listfind", new VarArgsSQLFunction( "$listfind(", ",", ")" ) );
+		registerFunction( "$listget", new VarArgsSQLFunction( "$listget(", ",", ")" ) );
+		registerFunction( "$listlength", new StandardSQLFunction( "$listlength", StandardBasicTypes.INTEGER ) );
+		registerFunction( "locate", new StandardSQLFunction( "$FIND", StandardBasicTypes.INTEGER ) );
+		registerFunction( "log", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "log10", new StandardJDBCEscapeFunction( "log", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "lower", new StandardSQLFunction( "lower" ) );
+		registerFunction( "ltrim", new StandardSQLFunction( "ltrim" ) );
+		registerFunction( "minute", new StandardJDBCEscapeFunction( "minute", StandardBasicTypes.INTEGER ) );
+		registerFunction( "mod", new StandardJDBCEscapeFunction( "mod", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "month", new StandardJDBCEscapeFunction( "month", StandardBasicTypes.INTEGER ) );
+		registerFunction( "monthname", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.STRING ) );
+		registerFunction( "now", new StandardJDBCEscapeFunction( "monthname", StandardBasicTypes.TIMESTAMP ) );
+		registerFunction( "nullif", new VarArgsSQLFunction( "nullif(", ",", ")" ) );
+		registerFunction( "nvl", new NvlFunction() );
+		registerFunction( "%odbcin", new StandardSQLFunction( "%odbcin" ) );
+		registerFunction( "%odbcout", new StandardSQLFunction( "%odbcin" ) );
+		registerFunction( "%pattern", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%pattern", "" ) );
+		registerFunction( "pi", new StandardJDBCEscapeFunction( "pi", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "$piece", new VarArgsSQLFunction( StandardBasicTypes.STRING, "$piece(", ",", ")" ) );
+		registerFunction( "position", new VarArgsSQLFunction( StandardBasicTypes.INTEGER, "position(", " in ", ")" ) );
+		registerFunction( "power", new VarArgsSQLFunction( StandardBasicTypes.STRING, "power(", ",", ")" ) );
+		registerFunction( "quarter", new StandardJDBCEscapeFunction( "quarter", StandardBasicTypes.INTEGER ) );
+		registerFunction( "repeat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "repeat(", ",", ")" ) );
+		registerFunction( "replicate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "replicate(", ",", ")" ) );
+		registerFunction( "right", new StandardJDBCEscapeFunction( "right", StandardBasicTypes.STRING ) );
+		registerFunction( "round", new VarArgsSQLFunction( StandardBasicTypes.FLOAT, "round(", ",", ")" ) );
+		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
+		registerFunction( "second", new StandardJDBCEscapeFunction( "second", StandardBasicTypes.INTEGER ) );
+		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
+		registerFunction( "sin", new StandardJDBCEscapeFunction( "sin", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "space", new StandardSQLFunction( "space", StandardBasicTypes.STRING ) );
+		registerFunction( "%sqlstring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlstring(", ",", ")" ) );
+		registerFunction( "%sqlupper", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%sqlupper(", ",", ")" ) );
+		registerFunction( "sqrt", new StandardJDBCEscapeFunction( "SQRT", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "%startswith", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "%startswith", "" ) );
+		// below is for Cache' that don't have str in 2007.1 there is str and we register str directly
+		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as char varying)" ) );
+		registerFunction( "string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "string(", ",", ")" ) );
+		// note that %string is deprecated
+		registerFunction( "%string", new VarArgsSQLFunction( StandardBasicTypes.STRING, "%string(", ",", ")" ) );
+		registerFunction( "substr", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substr(", ",", ")" ) );
+		registerFunction( "substring", new VarArgsSQLFunction( StandardBasicTypes.STRING, "substring(", ",", ")" ) );
+		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.TIMESTAMP, false ) );
+		registerFunction( "tan", new StandardJDBCEscapeFunction( "tan", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "timestampadd", new StandardJDBCEscapeFunction( "timestampadd", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "timestampdiff", new StandardJDBCEscapeFunction( "timestampdiff", StandardBasicTypes.DOUBLE ) );
+		registerFunction( "tochar", new VarArgsSQLFunction( StandardBasicTypes.STRING, "tochar(", ",", ")" ) );
+		registerFunction( "to_char", new VarArgsSQLFunction( StandardBasicTypes.STRING, "to_char(", ",", ")" ) );
+		registerFunction( "todate", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
+		registerFunction( "to_date", new VarArgsSQLFunction( StandardBasicTypes.STRING, "todate(", ",", ")" ) );
+		registerFunction( "tonumber", new StandardSQLFunction( "tonumber" ) );
+		registerFunction( "to_number", new StandardSQLFunction( "tonumber" ) );
+		// TRIM(end_keyword string-expression-1 FROM string-expression-2)
+		// use Hibernate implementation "From" is one of the parameters they pass in position ?3
+		//registerFunction( "trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 from ?3)") );
+		registerFunction( "truncate", new StandardJDBCEscapeFunction( "truncate", StandardBasicTypes.STRING ) );
+		registerFunction( "ucase", new StandardJDBCEscapeFunction( "ucase", StandardBasicTypes.STRING ) );
+		registerFunction( "upper", new StandardSQLFunction( "upper" ) );
+		// %upper is deprecated
+		registerFunction( "%upper", new StandardSQLFunction( "%upper" ) );
+		registerFunction( "user", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.STRING ) );
+		registerFunction( "week", new StandardJDBCEscapeFunction( "user", StandardBasicTypes.INTEGER ) );
+		registerFunction( "xmlconcat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlconcat(", ",", ")" ) );
+		registerFunction( "xmlelement", new VarArgsSQLFunction( StandardBasicTypes.STRING, "xmlelement(", ",", ")" ) );
+		// xmlforest requires a new kind of function constructor
+		registerFunction( "year", new StandardJDBCEscapeFunction( "year", StandardBasicTypes.INTEGER ) );
+	}
+
+	protected final void register71Functions() {
+		this.registerFunction( "str", new VarArgsSQLFunction( StandardBasicTypes.STRING, "str(", ",", ")" ) );
+	}
+
+	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean hasAlterTable() {
+		// Does this dialect support the ALTER TABLE syntax?
+		return true;
+	}
+
+	public boolean qualifyIndexName() {
+		// Do we need to qualify index names with the schema name?
+		return false;
+	}
+
+	public boolean supportsUnique() {
+		// Does this dialect support the UNIQUE column syntax?
+		return true;
+	}
+
+	/**
+	 * The syntax used to add a foreign key constraint to a table.
+	 *
+	 * @return String
+	 */
+	public String getAddForeignKeyConstraintString(
+			String constraintName,
+			String[] foreignKey,
+			String referencedTable,
+			String[] primaryKey,
+			boolean referencesPrimaryKey) {
+		// The syntax used to add a foreign key constraint to a table.
+		return new StringBuffer( 300 )
+				.append( " ADD CONSTRAINT " )
+				.append( constraintName )
+				.append( " FOREIGN KEY " )
+				.append( constraintName )
+				.append( " (" )
+				.append( StringHelper.join( ", ", foreignKey ) )	// identifier-commalist
+				.append( ") REFERENCES " )
+				.append( referencedTable )
+				.append( " (" )
+				.append( StringHelper.join( ", ", primaryKey ) ) // identifier-commalist
+				.append( ") " )
+				.toString();
+	}
+
+	public boolean supportsCheck() {
+		// Does this dialect support check constraints?
+		return false;
+	}
+
+	public String getAddColumnString() {
+		// The syntax used to add a column to a table
+		return " add column";
+	}
+
+	public String getCascadeConstraintsString() {
+		// Completely optional cascading drop clause.
+		return "";
+	}
+
+	public boolean dropConstraints() {
+		// Do we need to drop constraints before dropping tables in this dialect?
+		return true;
+	}
+
+	public boolean supportsCascadeDelete() {
+		return true;
+	}
+
+	public boolean hasSelfReferentialForeignKeyBug() {
+		return true;
+	}
+
+	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean supportsTemporaryTables() {
+		return true;
+	}
+
+	public String generateTemporaryTableName(String baseTableName) {
+		String name = super.generateTemporaryTableName( baseTableName );
+		return name.length() > 25 ? name.substring( 1, 25 ) : name;
+	}
+
+	public String getCreateTemporaryTableString() {
+		return "create global temporary table";
+	}
+
+	public Boolean performTemporaryTableDDLInIsolation() {
+		return Boolean.FALSE;
+	}
+
+	public String getCreateTemporaryTablePostfix() {
+		return "";
+	}
+
+	public boolean dropTemporaryTableAfterUse() {
+		return true;
+	}
+
+	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean supportsIdentityColumns() {
+		return true;
+	}
+
+	public Class getNativeIdentifierGeneratorClass() {
+		return IdentityGenerator.class;
+	}
+
+	public boolean hasDataTypeInIdentityColumn() {
+		// Whether this dialect has an Identity clause added to the data type or a completely seperate identity
+		// data type
+		return true;
+	}
+
+	public String getIdentityColumnString() throws MappingException {
+		// The keyword used to specify an identity column, if identity column key generation is supported.
+		return "identity";
+	}
+
+	public String getIdentitySelectString() {
+		return "SELECT LAST_IDENTITY() FROM %TSQL_sys.snf";
+	}
+
+	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean supportsSequences() {
+		return false;
+	}
+
+// It really does support sequences, but InterSystems elects to suggest usage of IDENTITY instead :/
+// Anyway, below are the actual support overrides for users wanting to use this combo...
+//
+//	public String getSequenceNextValString(String sequenceName) {
+//		return "select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
+//	}
+//
+//	public String getSelectSequenceNextValString(String sequenceName) {
+//		return "(select InterSystems.Sequences_GetNext('" + sequenceName + "') from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "'))";
+//	}
+//
+//	public String getCreateSequenceString(String sequenceName) {
+//		return "insert into InterSystems.Sequences(Name) values (ucase('" + sequenceName + "'))";
+//	}
+//
+//	public String getDropSequenceString(String sequenceName) {
+//		return "delete from InterSystems.Sequences where ucase(name)=ucase('" + sequenceName + "')";
+//	}
+//
+//	public String getQuerySequencesString() {
+//		return "select name from InterSystems.Sequences";
+//	}
+
+	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean supportsForUpdate() {
+		// Does this dialect support the FOR UPDATE syntax?
+		return false;
+	}
+
+	public boolean supportsForUpdateOf() {
+		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
+		return false;
+	}
+
+	public boolean supportsForUpdateNowait() {
+		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
+		return false;
+	}
+
+	public boolean supportsOuterJoinForUpdate() {
+		return false;
+	}
+
+	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
+		// InterSystems Cache' does not current support "SELECT ... FOR UPDATE" syntax...
+		// Set your transaction mode to READ_COMMITTED before using
+		if ( lockMode==LockMode.PESSIMISTIC_FORCE_INCREMENT) {
+			return new PessimisticForceIncrementLockingStrategy( lockable, lockMode);
+		}
+		else if ( lockMode==LockMode.PESSIMISTIC_WRITE) {
+			return new PessimisticWriteUpdateLockingStrategy( lockable, lockMode);
+		}
+		else if ( lockMode==LockMode.PESSIMISTIC_READ) {
+			return new PessimisticReadUpdateLockingStrategy( lockable, lockMode);
+		}
+		else if ( lockMode==LockMode.OPTIMISTIC) {
+			return new OptimisticLockingStrategy( lockable, lockMode);
+		}
+		else if ( lockMode==LockMode.OPTIMISTIC_FORCE_INCREMENT) {
+			return new OptimisticForceIncrementLockingStrategy( lockable, lockMode);
+		}
+		else if ( lockMode.greaterThan( LockMode.READ ) ) {
+			return new UpdateLockingStrategy( lockable, lockMode );
+		}
+		else {
+			return new SelectLockingStrategy( lockable, lockMode );
+		}
+	}
+
+	// LIMIT support (ala TOP) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean supportsLimit() {
+		return true;
+	}
+
+	public boolean supportsLimitOffset() {
+		return false;
+	}
+
+	public boolean supportsVariableLimit() {
+		return true;
+	}
+
+	public boolean bindLimitParametersFirst() {
+		// Does the LIMIT clause come at the start of the SELECT statement, rather than at the end?
+		return true;
+	}
+
+	public boolean useMaxForLimit() {
+		// Does the LIMIT clause take a "maximum" row number instead of a total number of returned rows?
+		return true;
+	}
+
+	public String getLimitString(String sql, boolean hasOffset) {
+		if ( hasOffset ) {
+			throw new UnsupportedOperationException( "query result offset is not supported" );
+		}
+
+		// This does not support the Cache SQL 'DISTINCT BY (comma-list)' extensions,
+		// but this extension is not supported through Hibernate anyway.
+		int insertionPoint = sql.startsWith( "select distinct" ) ? 15 : 6;
+
+		return new StringBuffer( sql.length() + 8 )
+				.append( sql )
+				.insert( insertionPoint, " TOP ? " )
+				.toString();
+	}
+
+	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
+		return col;
+	}
+
+	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
+		ps.execute();
+		return ( ResultSet ) ps.getObject( 1 );
+	}
+
+	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public String getLowercaseFunction() {
+		// The name of the SQL function that transforms a string to lowercase
+		return "lower";
+	}
+
+	public String getNullColumnString() {
+		// The keyword used to specify a nullable column.
+		return " null";
+	}
+
+	public JoinFragment createOuterJoinFragment() {
+		// Create an OuterJoinGenerator for this dialect.
+		return new CacheJoinFragment();
+	}
+
+	public String getNoColumnsInsertString() {
+		// The keyword used to insert a row without specifying
+		// any column values
+		return " default values";
+	}
+
+	public SQLExceptionConverter buildSQLExceptionConverter() {
+		return new CacheSQLStateConverter( EXTRACTER );
+	}
+
+	public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
+		/**
+		 * Extract the name of the violated constraint from the given SQLException.
+		 *
+		 * @param sqle The exception that was the result of the constraint violation.
+		 * @return The extracted constraint name.
+		 */
+		public String extractConstraintName(SQLException sqle) {
+			return extractUsingTemplate( "constraint (", ") violated", sqle.getMessage() );
+		}
+	};
+
+
+	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public boolean supportsEmptyInList() {
+		return false;
+	}
+
+	public boolean areStringComparisonsCaseInsensitive() {
+		return true;
+	}
+
+	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
+		return false;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 4ec5710f01..54a0a172f1 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,1062 +1,1062 @@
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
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
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
 import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.exception.SQLExceptionConverter;
-import org.hibernate.exception.SQLStateConverter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.SQLExceptionConverter;
+import org.hibernate.exception.internal.SQLStateConverter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.jboss.logging.Logger;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.
  * Subclasses implement Hibernate compatibility with different systems.<br>
  * <br>
  * Subclasses should provide a public default constructor that <tt>register()</tt>
  * a set of type mappings and default Hibernate properties.<br>
  * <br>
  * Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 public abstract class Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dialect.class.getName());
 
 	public static final String DEFAULT_BATCH_SIZE = "15";
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
         LOG.usingDialect(this);
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
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		String dialectName = Environment.getProperties().getProperty( Environment.DIALECT );
 		return instantiateDialect( dialectName );
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
 		String dialectName = props.getProperty( Environment.DIALECT );
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
 			return ( Dialect ) ReflectHelper.classForName( dialectName ).newInstance();
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
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		String result = typeNames.get( code );
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
 	public String getTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					"No type mapping for java.sql.Types code: " +
 					code +
 					", length: " +
 					length
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
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, int capacity, String name) {
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
 	 * If <code>sqlTypeDescriptor</code> is a "standard basic" SQL type
 	 * descriptor, then this method uses {@link #getSqlTypeDescriptorOverride}
 	 * to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override, then this method
 	 * simply returns <code>sqlTypeDescriptor</code>
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then <code>sqlTypeDescriptor</code> is returned.
 	 * @throws IllegalArgumentException if <code>sqlTypeDescriptor</code> is null.
 	 *
 	 * @see {@link #getSqlTypeDescriptorOverride}
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
 	 * Returns the {@link SqlTypeDescriptor} that should override the
 	 * "standard basic" SQL type descriptor for values of the specified
 	 * column type, or null, if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} that should override the
 	 * "standard basic" SQL type descriptor, or null, if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				descriptor = useInputStreamToInsertBlob() ? BlobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
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
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setBinaryStream( 1L );  // the BLOB just read during the load phase of merge
 					InputStream detachedStream = original.getBinaryStream();      // the BLOB from the detached state
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
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the CLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the CLOB from the detached state
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
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the NCLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the NCLOB from the detached state
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
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code) throws HibernateException {
 		String result = hibernateTypeNames.get( code );
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
 		String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					"No Hibernate type mapping for java.sql.Types code: " +
 					code +
 					", length: " +
 					length
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
 	protected void registerHibernateType(int code, int capacity, String name) {
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
 		sqlFunctions.put( name, function );
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
 		sqlKeywords.add(word);
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
 			return TableHiLoGenerator.class;
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
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 */
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 */
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 */
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 */
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 */
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
 	 */
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 */
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
 	 */
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
 	 */
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed {@link #convertToFirstRowValue}
 	 * calls prior to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 *
 	 * @return The corresponding db/dialect specific offset.
 	 *
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 */
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
index 048280e334..ba281ce6cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/H2Dialect.java
@@ -1,357 +1,358 @@
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
+
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 /**
  * A dialect compatible with the H2 database.
  *
  * @author Thomas Mueller
  */
 public class H2Dialect extends Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, H2Dialect.class.getName());
 
 	private final String querySequenceString;
 
 	public H2Dialect() {
 		super();
 
 		String querySequenceString = "select sequence_name from information_schema.sequences";
 		try {
 			// HHH-2300
 			final Class h2ConstantsClass = ReflectHelper.classForName( "org.h2.engine.Constants" );
 			final int majorVersion = ( Integer ) h2ConstantsClass.getDeclaredField( "VERSION_MAJOR" ).get( null );
 			final int minorVersion = ( Integer ) h2ConstantsClass.getDeclaredField( "VERSION_MINOR" ).get( null );
 			final int buildId = ( Integer ) h2ConstantsClass.getDeclaredField( "BUILD_ID" ).get( null );
 			if ( buildId < 32 ) {
 				querySequenceString = "select name from information_schema.sequences";
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
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		getDefaultProperties().setProperty( Environment.NON_CONTEXTUAL_LOB_CREATION, "true" );  // http://code.google.com/p/h2database/issues/detail?id=235
 	}
 
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getIdentityColumnString() {
 		return "generated by default as identity"; // not null is implicit
 	}
 
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	public String getIdentityInsertString() {
 		return "null";
 	}
 
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		return new StringBuffer( sql.length() + 20 )
 				.append( sql )
 				.append( hasOffset ? " limit ? offset ?" : " limit ?" )
 				.toString();
 	}
 
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	public String getQuerySequencesString() {
 		return querySequenceString;
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
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
 				int idx = message.indexOf( "violation: " );
 				if ( idx > 0 ) {
 					constraintName = message.substring( idx + "violation: ".length() );
 				}
 			}
 			return constraintName;
 		}
 	};
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create cached local temporary table if not exists";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		// actually 2 different options are specified here:
 		//		1) [on commit drop] - says to drop the table on transaction commit
 		//		2) [transactional] - says to not perform an implicit commit of any current transaction
 		return "on commit drop transactional";
 	}
 
 	@Override
 	public Boolean performTemporaryTableDDLInIsolation() {
 		// explicitly create the table using the same connection and transaction
 		return Boolean.FALSE;
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp()";
 	}
 
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	@Override
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		// see http://groups.google.com/group/h2-database/browse_thread/thread/562d8a49e2dabe99?hl=en
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
index ffaf04c383..8e1ab6031e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/HSQLDialect.java
@@ -1,666 +1,666 @@
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
 
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
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
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
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
  * Enhancements to version 3.5.0 GA to provide basic support for both HSQLDB 1.8.x and 2.0
  * Should work with Hibernate 3.2 and later
  *
  * @author Christoph Sturm
  * @author Phillip Baird
  * @author Fred Toussi
  */
 public class HSQLDialect extends Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HSQLDialect.class.getName());
 
 	/**
 	 * version is 18 for 1.8 or 20 for 2.0
 	 */
 	private int hsqldbVersion = 18;
 
 
 	public HSQLDialect() {
 		super();
 
 		try {
 			Class props = ReflectHelper.classForName( "org.hsqldb.persist.HsqlDatabaseProperties" );
 			String versionString = (String) props.getDeclaredField( "THIS_VERSION" ).get( null );
 
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
 			registerColumnType( Types.BLOB, "blob" );
 			registerColumnType( Types.CLOB, "clob" );
 		}
 
 		registerFunction( "avg", new AvgWithArgumentCastFunction( "double" ) );
 
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
 		registerFunction( "rawtohex", new StandardSQLFunction( "rawtohex" ) );
 		registerFunction( "hextoraw", new StandardSQLFunction( "hextoraw" ) );
 		registerFunction( "str", new SQLFunctionTemplate( StandardBasicTypes.STRING, "cast(?1 as varchar(24))" ) );
 		registerFunction( "user", new NoArgSQLFunction( "user", StandardBasicTypes.STRING ) );
 		registerFunction( "database", new NoArgSQLFunction( "database", StandardBasicTypes.STRING ) );
 
 		registerFunction( "sysdate", new NoArgSQLFunction( "sysdate", StandardBasicTypes.DATE, false ) );
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
 		registerFunction( "second", new StandardSQLFunction( "second", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 
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
 		registerFunction( "roundmagic", new StandardSQLFunction( "roundmagic" ) );
 
 		registerFunction( "ceiling", new StandardSQLFunction( "ceiling" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 
 		// Multi-param dialect functions...
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER ) );
 
 		// function templates
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 	}
 
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getIdentityColumnString() {
 		return "generated by default as identity (start with 1)"; //not null is implicit
 	}
 
 	public String getIdentitySelectString() {
 		return "call identity()";
 	}
 
 	public String getIdentityInsertString() {
 		return hsqldbVersion < 20 ? "null" : "default";
 	}
 
 	public boolean supportsLockTimeouts() {
 		return false;
 	}
 
 	public String getForUpdateString() {
 		return "";
 	}
 
 	public boolean supportsUnique() {
 		return false;
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		if ( hsqldbVersion < 20 ) {
 			return new StringBuffer( sql.length() + 10 )
 					.append( sql )
 					.insert(
 							sql.toLowerCase().indexOf( "select" ) + 6,
 							hasOffset ? " limit ? ?" : " top ?"
 					)
 					.toString();
 		}
 		else {
 			return new StringBuffer( sql.length() + 20 )
 					.append( sql )
 					.append( hasOffset ? " offset ? limit ?" : " limit ?" )
 					.toString();
 		}
 	}
 
 	public boolean bindLimitParametersFirst() {
 		return hsqldbVersion < 20;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return true;
 	}
 
 	public boolean supportsColumnCheck() {
 		return hsqldbVersion >= 20;
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	protected String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	protected String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "next value for " + sequenceName;
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "call next value for " + sequenceName;
 	}
 
 	public String getQuerySequencesString() {
 		// this assumes schema support, which is present in 1.8.0 and later...
 		return "select sequence_name from information_schema.system_sequences";
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return hsqldbVersion < 20 ? EXTRACTER_18 : EXTRACTER_20;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER_18 = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
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
 	private static ViolatedConstraintNameExtracter EXTRACTER_20 = new TemplatedViolatedConstraintNameExtracter() {
 
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
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
 
 	public String getSelectClauseNullString(int sqlType) {
 		String literal;
 		switch ( sqlType ) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				literal = "cast(null as varchar(100))";
 				break;
 			case Types.DATE:
 				literal = "cast(null as date)";
 				break;
 			case Types.TIMESTAMP:
 				literal = "cast(null as timestamp)";
 				break;
 			case Types.TIME:
 				literal = "cast(null as time)";
 				break;
 			default:
 				literal = "cast(null as int)";
 		}
 		return literal;
 	}
 
     public boolean supportsUnionAll() {
         return true;
     }
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Hibernate uses this information for temporary tables that it uses for its own operations
 	// therefore the appropriate strategy is taken with different versions of HSQLDB
 
 	// All versions of HSQLDB support GLOBAL TEMPORARY tables where the table
 	// definition is shared by all users but data is private to the session
 	// HSQLDB 2.0 also supports session-based LOCAL TEMPORARY tables where
 	// the definition and data is private to the session and table declaration
 	// can happen in the middle of a transaction
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	/**
 	 * With HSQLDB 2.0, the table name is qualified with MODULE to assist the drop
 	 * statement (in-case there is a global name beginning with HT_)
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 *
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		if ( hsqldbVersion < 20 ) {
 			return "HT_" + baseTableName;
 		}
 		else {
 			return "MODULE.HT_" + baseTableName;
 		}
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		if ( hsqldbVersion < 20 ) {
 			return "create global temporary table";
 		}
 		else {
 			return "declare local temporary table";
 		}
 	}
 
 	/**
 	 * No fragment is needed if data is not needed beyond commit, otherwise
 	 * should add "on commit preserve rows"
 	 *
 	 * @return Any required postfix.
 	 */
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	/**
 	 * Command used to drop a temporary table.
 	 *
 	 * @return The command used to drop a temporary table.
 	 */
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	/**
 	 * Different behavior for GLOBAL TEMPORARY (1.8) and LOCAL TEMPORARY (2.0)
 	 * <p/>
 	 * Possible return values and their meanings:<ul>
 	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
 	 * in isolation.</li>
 	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
 	 * temporary table DDL in isolation.</li>
 	 * <li><i>null</i> - defer to the JDBC driver response in regards to
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
 	 * </ul>
 	 *
 	 * @return see the result matrix above.
 	 */
 	public Boolean performTemporaryTableDDLInIsolation() {
 		if ( hsqldbVersion < 20 ) {
 			return Boolean.TRUE;
 		}
 		else {
 			return Boolean.FALSE;
 		}
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * todo - clarify usage by Hibernate
 	 * Version 1.8 GLOBAL TEMPORARY table definitions persist beyond the end
 	 * of the session (data is cleared). If there are not too many such tables,
 	 * perhaps we can avoid dropping them and reuse the table next time?
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * HSQLDB 1.8.x requires CALL CURRENT_TIMESTAMP but this should not
 	 * be treated as a callable statement. It is equivalent to
 	 * "select current_timestamp from dual" in some databases.
 	 * HSQLDB 2.0 also supports VALUES CURRENT_TIMESTAMP
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 *         is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		return "call current_timestamp";
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
 
 	/**
 	 * For HSQLDB 2.0, this is a copy of the base class implementation.
 	 * For HSQLDB 1.8, only READ_UNCOMMITTED is supported.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 *
 	 * @return The appropriate locking strategy.
 	 *
 	 * @since 3.2
 	 */
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
 
 	public static class ReadUncommittedLockingStrategy extends SelectLockingStrategy {
 		public ReadUncommittedLockingStrategy(Lockable lockable, LockMode lockMode) {
 			super( lockable, lockMode );
 		}
 
 		public void lock(Serializable id, Object version, Object object, int timeout, SessionImplementor session)
 				throws StaleObjectStateException, JDBCException {
             if (getLockMode().greaterThan(LockMode.READ)) LOG.hsqldbSupportsOnlyReadCommittedIsolation();
 			super.lock( id, version, object, timeout, session );
 		}
 	}
 
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	/**
 	 * todo - needs usage clarification
 	 *
 	 * If the SELECT statement is always part of a UNION, then the type of
 	 * parameter is resolved by v. 2.0, but not v. 1.8 (assuming the other
 	 * SELECT in the UNION has a column reference in the same position and
 	 * can be type-resolved).
 	 *
 	 * On the other hand if the SELECT statement is isolated, all versions of
 	 * HSQLDB require casting for "select ? from .." to work.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 *
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return hsqldbVersion >= 20;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return hsqldbVersion >= 20;
 	}
 
 
 	public boolean supportsLobValueChangePropogation() {
 		return false;
 	}
 
     public String toBooleanValueString(boolean bool) {
         return String.valueOf( bool );
     }
 
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index b664bab1c7..567c4399bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,242 +1,242 @@
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
 import org.hibernate.MappingException;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
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
 
 	/**
 	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
 	 * Informix type mappings.
 	 */
 	public InformixDialect() {
 		super();
 
 		registerColumnType(Types.BIGINT, "int8");
 		registerColumnType(Types.BINARY, "byte");
 		registerColumnType(Types.BIT, "smallint"); // Informix doesn't have a bit type
 		registerColumnType(Types.CHAR, "char($l)");
 		registerColumnType(Types.DATE, "date");
 		registerColumnType(Types.DECIMAL, "decimal");
         registerColumnType(Types.DOUBLE, "float");
         registerColumnType(Types.FLOAT, "smallfloat");
 		registerColumnType(Types.INTEGER, "integer");
 		registerColumnType(Types.LONGVARBINARY, "blob"); // or BYTE
 		registerColumnType(Types.LONGVARCHAR, "clob"); // or TEXT?
 		registerColumnType(Types.NUMERIC, "decimal"); // or MONEY
 		registerColumnType(Types.REAL, "smallfloat");
 		registerColumnType(Types.SMALLINT, "smallint");
 		registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
 		registerColumnType(Types.TIME, "datetime hour to second");
 		registerColumnType(Types.TINYINT, "smallint");
 		registerColumnType(Types.VARBINARY, "byte");
 		registerColumnType(Types.VARCHAR, "varchar($l)");
 		registerColumnType(Types.VARCHAR, 255, "varchar($l)");
 		registerColumnType(Types.VARCHAR, 32739, "lvarchar($l)");
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 	}
 
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getIdentitySelectString(String table, String column, int type) 
 	throws MappingException {
 		return type==Types.BIGINT ?
 			"select dbinfo('serial8') from informix.systables where tabid=1" :
 			"select dbinfo('sqlca.sqlerrd1') from informix.systables where tabid=1";
 	}
 
 	public String getIdentityColumnString(int type) throws MappingException {
 		return type==Types.BIGINT ?
 			"serial8 not null" :
 			"serial not null";
 	}
 
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 * Informix constraint name must be at the end.
 	 * @return String
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		StringBuffer result = new StringBuffer( 30 )
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
 	 * The syntax used to add a primary key constraint to a table.
 	 * Informix constraint name must be at the end.
 	 * @return String
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint primary key constraint " + constraintName + " ";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from informix.systables where tabid=1";
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	public String getQuerySequencesString() {
 		return "select tabname from informix.systables where tabtype='Q'";
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuffer( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( querySelect.toLowerCase().indexOf( "select" ) + 6, " first " + limit )
 				.toString();
 	}
 
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
         return EXTRACTER;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			String constraintName = null;
 			
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == -268 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == -691 ) {
 				constraintName = extractUsingTemplate( "Missing key in referenced table for referential constraint (", ").", sqle.getMessage() );
 			}
 			else if ( errorCode == -692 ) {
 				constraintName = extractUsingTemplate( "Key value for constraint (", ") is still being referenced.", sqle.getMessage() );
 			}
 			
 			if (constraintName != null) {
 				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
 				int i = constraintName.indexOf('.');
 				if (i != -1) {
 					constraintName = constraintName.substring(i + 1);
 				}
 			}
 
 			return constraintName;
 		}
 
 	};
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select distinct current timestamp from informix.systables";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index 53432d3591..e549ed576c 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,496 +1,496 @@
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
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for Oracle 8i.
  *
  * @author Steve Ebersole
  */
 public class Oracle8iDialect extends Dialect {
 
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
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "date" );
 	}
 
 	protected void registerLargeObjectTypeMappings() {
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
 
 
 	// features which change between 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Support for the oracle proprietary join syntax...
 	 *
 	 * @return The orqacle join fragment
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	/**
 	 * Map case support to the Oracle DECODE function.  Oracle did not
 	 * add support for CASE until 9i.
 	 *
 	 * @return The oracle CASE -> DECODE fragment
 	 */
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase().endsWith(" for update") ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
 		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
 		if (hasOffset) {
 			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
 		}
 		else {
 			pagingSelect.append("select * from ( ");
 		}
 		pagingSelect.append(sql);
 		if (hasOffset) {
 			pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
 		}
 		else {
 			pagingSelect.append(" ) where rownum <= ?");
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
 
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
 
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
 
 	// features which remain constant across 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~
 
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName; //starts with 1, implicitly
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	public String getQuerySequencesString() {
 		return    " select sequence_name from all_sequences"
 				+ "  union"
 				+ " select synonym_name"
 				+ "   from all_synonyms us, all_sequences asq"
 				+ "  where asq.sequence_name = us.table_name"
 				+ "    and asq.sequence_owner = us.table_owner";
 	}
 
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
         return EXTRACTER;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
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
 
 	public static final String ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.OracleTypes";
 	public static final String DEPRECATED_ORACLE_TYPES_CLASS_NAME = "oracle.jdbc.driver.OracleTypes";
 
 	public static final int INIT_ORACLETYPES_CURSOR_VALUE = -99;
 
 	// not final-static to avoid possible classcast exceptions if using different oracle drivers.
 	private int oracleCursorTypeSqlType = INIT_ORACLETYPES_CURSOR_VALUE;
 
 	public int getOracleCursorTypeSqlType() {
 		if ( oracleCursorTypeSqlType == INIT_ORACLETYPES_CURSOR_VALUE ) {
 			// todo : is there really any reason to kkeep trying if this fails once?
 			oracleCursorTypeSqlType = extractOracleCursorTypeValue();
 		}
 		return oracleCursorTypeSqlType;
 	}
 
 	protected int extractOracleCursorTypeValue() {
 		Class oracleTypesClass;
 		try {
 			oracleTypesClass = ReflectHelper.classForName( ORACLE_TYPES_CLASS_NAME );
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			try {
 				oracleTypesClass = ReflectHelper.classForName( DEPRECATED_ORACLE_TYPES_CLASS_NAME );
 			}
 			catch ( ClassNotFoundException e ) {
 				throw new HibernateException( "Unable to locate OracleTypes class", e );
 			}
 		}
 
 		try {
 			return oracleTypesClass.getField( "CURSOR" ).getInt( null );
 		}
 		catch ( Exception se ) {
 			throw new HibernateException( "Unable to access OracleTypes.CURSOR value", se );
 		}
 	}
 
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter( col, getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return ( ResultSet ) ps.getObject( 1 );
 	}
 
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String generateTemporaryTableName(String baseTableName) {
 		String name = super.generateTemporaryTableName(baseTableName);
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
index 8c6f6bc04d..ae37fc1fa4 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9Dialect.java
@@ -1,371 +1,371 @@
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
 import org.hibernate.HibernateException;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.StandardBasicTypes;
 import org.jboss.logging.Logger;
 
 /**
  * An SQL dialect for Oracle 9 (uses ANSI-style syntax where possible).
  *
  * @deprecated Use either Oracle9iDialect or Oracle10gDialect instead
  * @author Gavin King, David Channon
  */
 @Deprecated
 public class Oracle9Dialect extends Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Oracle9Dialect.class.getName());
 
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
 		getDefaultProperties().setProperty(Environment.USE_GET_GENERATED_KEYS, "false");
 		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
 		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
 
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
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
 
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName; //starts with 1, implicitly
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase().endsWith(" for update") ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
 		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
 		if (hasOffset) {
 			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
 		}
 		else {
 			pagingSelect.append("select * from ( ");
 		}
 		pagingSelect.append(sql);
 		if (hasOffset) {
 			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
 		}
 		else {
 			pagingSelect.append(" ) where rownum <= ?");
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	public String getQuerySequencesString() {
 		return "select sequence_name from user_sequences";
 	}
 
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
         return EXTRACTER;
 	}
 
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
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
 
 	// not final-static to avoid possible classcast exceptions if using different oracle drivers.
 	int oracletypes_cursor_value = 0;
 	public int registerResultSetOutParameter(java.sql.CallableStatement statement,int col) throws SQLException {
 		if(oracletypes_cursor_value==0) {
 			try {
 				Class types = ReflectHelper.classForName("oracle.jdbc.driver.OracleTypes");
 				oracletypes_cursor_value = types.getField("CURSOR").getInt(types.newInstance());
 			} catch (Exception se) {
 				throw new HibernateException("Problem while trying to load or access OracleTypes.CURSOR value",se);
 			}
 		}
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter(col, oracletypes_cursor_value);
 		col++;
 		return col;
 	}
 
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return ( ResultSet ) ps.getObject( 1 );
 	}
 
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String generateTemporaryTableName(String baseTableName) {
 		String name = super.generateTemporaryTableName(baseTableName);
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
index d96aecd78f..99c8bb9fbe 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/PostgreSQLDialect.java
@@ -1,428 +1,428 @@
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
 
 import org.hibernate.LockOptions;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.PositionSubstringFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
-import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.internal.util.JdbcExceptionHelper;
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
 public class PostgreSQLDialect extends Dialect {
 
 	public PostgreSQLDialect() {
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
 		registerFunction( "md5", new StandardSQLFunction("md5") );
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
 
 		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
 		getDefaultProperties().setProperty( Environment.NON_CONTEXTUAL_LOB_CREATION, "true" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
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
 
 	public String getAddColumnString() {
 		return "add column";
 	}
 
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName );
 	}
 
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return "nextval ('" + sequenceName + "')";
 	}
 
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName; //starts with 1, implicitly
 	}
 
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	public String getCascadeConstraintsString() {
 		return " cascade";
 	}
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	public String getQuerySequencesString() {
 		return "select relname from pg_class where relkind='S'";
 	}
 
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		return new StringBuffer( sql.length()+20 )
 				.append( sql )
 				.append( hasOffset ? " limit ? offset ?" : " limit ?" )
 				.toString();
 	}
 
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	public boolean supportsIdentityColumns() {
 		return true;
 	}
 
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	public String getIdentitySelectString(String table, String column, int type) {
 		return new StringBuffer().append("select currval('")
 			.append(table)
 			.append('_')
 			.append(column)
 			.append("_seq')")
 			.toString();
 	}
 
 	public String getIdentityColumnString(int type) {
 		return type==Types.BIGINT ?
 			"bigserial not null" :
 			"serial not null";
 	}
 
 	public boolean hasDataTypeInIdentityColumn() {
 		return false;
 	}
 
 	public String getNoColumnsInsertString() {
 		return "default values";
 	}
 
 	public Class getNativeIdentifierGeneratorClass() {
 		return SequenceGenerator.class;
 	}
 
 	public boolean supportsOuterJoinForUpdate() {
 		return false;
 	}
 	
 	public boolean useInputStreamToInsertBlob() {
 		return false;
 	}
 
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	/**
 	 * Workaround for postgres bug #1453
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		String typeName = getTypeName(sqlType, 1, 1, 0);
 		//trim off the length/precision/scale
 		int loc = typeName.indexOf('(');
 		if (loc>-1) {
 			typeName = typeName.substring(0, loc);
 		}
 		return "null::" + typeName;
 	}
 
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	public String getCreateTemporaryTableString() {
 		return "create temporary table";
 	}
 
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit drop";
 	}
 
 	/*public boolean dropTemporaryTableAfterUse() {
 		//we have to, because postgres sets current tx
 		//to rollback only after a failed create table
 		return true;
 	}*/
 
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select now()";
 	}
 
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}
 
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "true" : "false";
 	}
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	/**
 	 * Constraint-name extractor for Postgres constraint violation exceptions.
 	 * Orginally contributed by Denny Bartelt.
 	 */
 	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			try {
 				int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( sqle )).intValue();
 				switch (sqlState) {
 					// CHECK VIOLATION
 					case 23514: return extractUsingTemplate("violates check constraint \"","\"", sqle.getMessage());
 					// UNIQUE VIOLATION
 					case 23505: return extractUsingTemplate("violates unique constraint \"","\"", sqle.getMessage());
 					// FOREIGN KEY VIOLATION
 					case 23503: return extractUsingTemplate("violates foreign key constraint \"","\"", sqle.getMessage());
 					// NOT NULL VIOLATION
 					case 23502: return extractUsingTemplate("null value in column \"","\" violates not-null constraint", sqle.getMessage());
 					// TODO: RESTRICT VIOLATION
 					case 23001: return null;
 					// ALL OTHER
 					default: return null;
 				}
 			} catch (NumberFormatException nfe) {
 				return null;
 			}
 		}
 	};
 	
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		// Register the type of the out param - PostgreSQL uses Types.OTHER
 		statement.registerOutParameter(col, Types.OTHER);
 		col++;
 		return col;
 	}
 
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		ResultSet rs = (ResultSet) ps.getObject(1);
 		return rs;
 	}
 
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	//only necessary for postgre < 7.4
 	//http://anoncvs.postgresql.org/cvsweb.cgi/pgsql/doc/src/sgml/ref/create_sequence.sgml
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
 		return getCreateSequenceString( sequenceName ) + " start " + initialValue + " increment " + incrementSize;
 	}
 	
 	// Overridden informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 // seems to not really...
 //	public boolean supportsRowValueConstructorSyntax() {
 //		return true;
 //	}
 
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
 
 	// locking support
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT )
 			return " for update nowait";
 		else
 			return " for update";
 	}
 
 	public String getReadLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT )
 			return " for share nowait";
 		else
 			return " for share";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
index 5cfada1f50..ce92b29ff7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/resolver/BasicSQLExceptionConverter.java
@@ -1,66 +1,66 @@
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
 package org.hibernate.dialect.resolver;
 import java.sql.SQLException;
 
+import org.hibernate.exception.internal.SQLStateConverter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
-import org.hibernate.exception.SQLStateConverter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
 
 import org.jboss.logging.Logger;
 
 /**
  * A helper to centralize conversion of {@link java.sql.SQLException}s to {@link org.hibernate.JDBCException}s.
  *
  * @author Steve Ebersole
  */
 public class BasicSQLExceptionConverter {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        BasicSQLExceptionConverter.class.getName());
 	public static final BasicSQLExceptionConverter INSTANCE = new BasicSQLExceptionConverter();
     public static final String MSG = LOG.unableToQueryDatabaseMetadata();
 
 	private static final SQLStateConverter CONVERTER = new SQLStateConverter( new ConstraintNameExtracter() );
 
 	/**
 	 * Perform a conversion.
 	 *
 	 * @param sqlException The exception to convert.
 	 * @return The converted exception.
 	 */
 	public JDBCException convert(SQLException sqlException) {
 		return CONVERTER.convert( sqlException, MSG, null );
 	}
 
 	private static class ConstraintNameExtracter implements ViolatedConstraintNameExtracter {
 		/**
 		 * {@inheritDoc}
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			return "???";
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
index 4b5e1f7b09..d8e1570ddf 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
@@ -1,294 +1,294 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 
+import org.hibernate.exception.internal.SQLStateConverter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
-import org.hibernate.exception.SQLExceptionConverter;
-import org.hibernate.exception.SQLStateConverter;
-import org.hibernate.exception.ViolatedConstraintNameExtracter;
+import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.internal.util.StringHelper;
 import org.jboss.logging.Logger;
 import org.jboss.logging.Logger.Level;
 
 /**
  * Helper for handling SQLExceptions in various manners.
  *
  * @author Steve Ebersole
  */
 public class SqlExceptionHelper {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SqlExceptionHelper.class.getName());
 
 	public static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";
 	public static final String DEFAULT_WARNING_MSG = "SQL Warning";
 
 	public static final SQLExceptionConverter DEFAULT_CONVERTER = new SQLStateConverter(
 			new ViolatedConstraintNameExtracter() {
 				public String extractConstraintName(SQLException e) {
 					return null;
 				}
 			}
 	);
 
 	private SQLExceptionConverter sqlExceptionConverter;
 
 	/**
 	 * Create an exception helper with a default exception converter.
 	 */
 	public SqlExceptionHelper() {
 		sqlExceptionConverter = DEFAULT_CONVERTER;
 	}
 
 	/**
 	 * Create an exception helper with a specific exception converter.
 	 *
 	 * @param sqlExceptionConverter The exception converter to use.
 	 */
 	public SqlExceptionHelper(SQLExceptionConverter sqlExceptionConverter) {
 		this.sqlExceptionConverter = sqlExceptionConverter;
 	}
 
 	/**
 	 * Access the current exception converter being used internally.
 	 *
 	 * @return The current exception converter.
 	 */
 	public SQLExceptionConverter getSqlExceptionConverter() {
 		return sqlExceptionConverter;
 	}
 
 	/**
 	 * Inject the exception converter to use.
 	 * <p/>
 	 * NOTE : <tt>null</tt> is allowed and signifies to use the default.
 	 *
 	 * @param sqlExceptionConverter The converter to use.
 	 */
 	public void setSqlExceptionConverter(SQLExceptionConverter sqlExceptionConverter) {
 		this.sqlExceptionConverter = ( sqlExceptionConverter == null ? DEFAULT_CONVERTER : sqlExceptionConverter );
 	}
 
     // SQLException ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
     /**
      * Convert an SQLException using the current converter, doing some logging first.
      *
      * @param sqlException The exception to convert
      * @param message An error message.
      * @return The converted exception
      */
     public JDBCException convert( SQLException sqlException,
                                   String message ) {
         return convert(sqlException, message, "n/a");
     }
 
     /**
      * Convert an SQLException using the current converter, doing some logging first.
      *
      * @param sqlException The exception to convert
      * @param message An error message.
      * @param sql The SQL being executed when the exception occurred
      * @return The converted exception
      */
     public JDBCException convert( SQLException sqlException,
                                   String message,
                                   String sql ) {
         logExceptions(sqlException, message + " [" + sql + "]");
         return sqlExceptionConverter.convert(sqlException, message, sql);
     }
 
     /**
      * Log the given (and any nested) exception.
      *
      * @param sqlException The exception to log
      * @param message The message text to use as a preamble.
      */
     public void logExceptions( SQLException sqlException,
                                String message ) {
         if (LOG.isEnabled(Level.ERROR)) {
             if (LOG.isDebugEnabled()) {
                 message = StringHelper.isNotEmpty(message) ? message : DEFAULT_EXCEPTION_MSG;
                 LOG.debug(message, sqlException);
             }
             while (sqlException != null) {
                 StringBuffer buf = new StringBuffer(30).append("SQL Error: ").append(sqlException.getErrorCode()).append(", SQLState: ").append(sqlException.getSQLState());
                 LOG.warn(buf.toString());
                 LOG.error(sqlException.getMessage());
                 sqlException = sqlException.getNextException();
             }
         }
     }
 
     // SQLWarning ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
     /**
      * Contract for handling {@link SQLWarning warnings}
      */
     public static interface WarningHandler {
         /**
          * Should processing be done? Allows short-circuiting if not.
          *
          * @return True to process warnings, false otherwise.
          */
         public boolean doProcess();
 
         /**
          * Prepare for processing of a {@link SQLWarning warning} stack.
          * <p/>
          * Note that the warning here is also the first passed to {@link #handleWarning}
          *
          * @param warning The first warning in the stack.
          */
         public void prepare( SQLWarning warning );
 
         /**
          * Handle an individual warning in the stack.
          *
          * @param warning The warning to handle.
          */
         public void handleWarning( SQLWarning warning );
     }
 
     /**
      * Basic support for {@link WarningHandler} implementations which log
      */
     public static abstract class WarningHandlerLoggingSupport implements WarningHandler {
         public final void handleWarning( SQLWarning warning ) {
             StringBuffer buf = new StringBuffer(30).append("SQL Warning Code: ").append(warning.getErrorCode()).append(", SQLState: ").append(warning.getSQLState());
             logWarning(buf.toString(), warning.getMessage());
         }
 
         /**
          * Delegate to log common details of a {@link SQLWarning warning}
          *
          * @param description A description of the warning
          * @param message The warning message
          */
         protected abstract void logWarning( String description,
                                             String message );
     }
 
     public static class StandardWarningHandler extends WarningHandlerLoggingSupport {
         private final String introMessage;
 
         public StandardWarningHandler( String introMessage ) {
             this.introMessage = introMessage;
         }
 
         public boolean doProcess() {
             return LOG.isEnabled(Level.WARN);
         }
 
         public void prepare( SQLWarning warning ) {
             LOG.debug(introMessage, warning);
         }
 
         @Override
         protected void logWarning( String description,
                                    String message ) {
             LOG.warn(description);
             LOG.warn(message);
         }
     }
 
     public static StandardWarningHandler STANDARD_WARNING_HANDLER = new StandardWarningHandler(DEFAULT_WARNING_MSG);
 
     public void walkWarnings( SQLWarning warning,
                               WarningHandler handler ) {
         if (warning == null || handler.doProcess()) {
             return;
         }
         handler.prepare(warning);
         while (warning != null) {
             handler.handleWarning(warning);
             warning = warning.getNextWarning();
         }
     }
 
     /**
      * Standard (legacy) behavior for logging warnings associated with a JDBC {@link Connection} and clearing them.
      * <p/>
      * Calls {@link #handleAndClearWarnings(Connection, WarningHandler)} using {@link #STANDARD_WARNING_HANDLER}
      *
      * @param connection The JDBC connection potentially containing warnings
      */
     public void logAndClearWarnings( Connection connection ) {
         handleAndClearWarnings(connection, STANDARD_WARNING_HANDLER);
     }
 
     /**
      * General purpose handling of warnings associated with a JDBC {@link Connection}.
      *
      * @param connection The JDBC connection potentially containing warnings
      * @param handler The handler for each individual warning in the stack.
      * @see #walkWarnings
      */
     @SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"} )
     public void handleAndClearWarnings( Connection connection,
                                         WarningHandler handler ) {
         try {
             walkWarnings(connection.getWarnings(), handler);
         } catch (SQLException sqle) {
             // workaround for WebLogic
             LOG.debug("could not log warnings", sqle);
         }
         try {
             // Sybase fail if we don't do that, sigh...
             connection.clearWarnings();
         } catch (SQLException sqle) {
             LOG.debug("could not clear warnings", sqle);
         }
     }
 
     /**
      * General purpose handling of warnings associated with a JDBC {@link Statement}.
      *
      * @param statement The JDBC statement potentially containing warnings
      * @param handler The handler for each individual warning in the stack.
      * @see #walkWarnings
      */
     @SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"} )
     public void handleAndClearWarnings( Statement statement,
                                         WarningHandler handler ) {
         try {
             walkWarnings(statement.getWarnings(), handler);
         } catch (SQLException sqlException) {
             // workaround for WebLogic
             LOG.debug("could not log warnings", sqlException);
         }
         try {
             // Sybase fail if we don't do that, sigh...
             statement.clearWarnings();
         } catch (SQLException sqle) {
             LOG.debug("could not clear warnings", sqle);
         }
     }
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index ae92327f0e..f4c1bdef0c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,241 +1,241 @@
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
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
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
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
-import org.hibernate.exception.SQLExceptionConverter;
+import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
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
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	public Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	public EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@link #getJdbcServices().getDialect()}.{@link JdbcServices#getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	public Interceptor getInterceptor();
 
 	public QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	public Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	public String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the connection provider
 	 */
 	public ConnectionProvider getConnectionProvider();
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	public String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	public String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	public QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	public QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	public UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 
 	public NamedQueryDefinition getNamedQuery(String queryName);
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getSecondLevelCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	public Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionConverter getSQLExceptionConverter();
 	   // TODO: deprecate???
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 */
     public SqlExceptionHelper getSQLExceptionHelper();
 
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	public FetchProfile getFetchProfile(String name);
 
 	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLStateConverter.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLStateConverter.java
index 74607bba4d..c58025d381 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/CacheSQLStateConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/internal/CacheSQLStateConverter.java
@@ -1,116 +1,123 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
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
- *
- */
-package org.hibernate.exception;
-
-import java.sql.SQLException;
-import java.util.HashSet;
-import java.util.Set;
-import org.hibernate.JDBCException;
-import org.hibernate.internal.util.JdbcExceptionHelper;
-
-/**
- * A SQLExceptionConverter implementation specific to Cach&eacute; SQL,
- * accounting for its custom integrity constraint violation error codes.
- *
- * @author Jonathan Levinson
- */
-public class CacheSQLStateConverter implements SQLExceptionConverter {
-
-	private ViolatedConstraintNameExtracter extracter;
-
-	private static final Set SQL_GRAMMAR_CATEGORIES = new HashSet();
-	private static final Set DATA_CATEGORIES = new HashSet();
-	private static final Set INTEGRITY_VIOLATION_CATEGORIES = new HashSet();
-	private static final Set CONNECTION_CATEGORIES = new HashSet();
-
-	static {
-		SQL_GRAMMAR_CATEGORIES.add( "07" );
-		SQL_GRAMMAR_CATEGORIES.add( "37" );
-		SQL_GRAMMAR_CATEGORIES.add( "42" );
-		SQL_GRAMMAR_CATEGORIES.add( "65" );
-		SQL_GRAMMAR_CATEGORIES.add( "S0" );
-		SQL_GRAMMAR_CATEGORIES.add( "20" );
-
-		DATA_CATEGORIES.add( "22" );
-		DATA_CATEGORIES.add( "21" );
-		DATA_CATEGORIES.add( "02" );
-
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 119 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 120 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 121 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 122 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 123 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 124 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 125 ) );
-		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 127 ) );
-
-		CONNECTION_CATEGORIES.add( "08" );
-	}
-
-	public CacheSQLStateConverter(ViolatedConstraintNameExtracter extracter) {
-		this.extracter = extracter;
-	}
-
-	/**
-	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
-	 *
-	 * @param sqlException The SQLException to be converted.
-	 * @param message	  An optional error message.
-	 * @param sql		  Optionally, the sql being performed when the exception occurred.
-	 * @return The resulting JDBCException.
-	 */
-	public JDBCException convert(SQLException sqlException, String message, String sql) {
-		String sqlStateClassCode = JdbcExceptionHelper.extractSqlStateClassCode( sqlException );
-		Integer errorCode = new Integer( JdbcExceptionHelper.extractErrorCode( sqlException ) );
-		if ( sqlStateClassCode != null ) {
-			if ( SQL_GRAMMAR_CATEGORIES.contains( sqlStateClassCode ) ) {
-				return new SQLGrammarException( message, sqlException, sql );
-			}
-			else if ( INTEGRITY_VIOLATION_CATEGORIES.contains( errorCode ) ) {
-				String constraintName = extracter.extractConstraintName( sqlException );
-				return new ConstraintViolationException( message, sqlException, sql, constraintName );
-			}
-			else if ( CONNECTION_CATEGORIES.contains( sqlStateClassCode ) ) {
-				return new JDBCConnectionException( message, sqlException, sql );
-			}
-			else if ( DATA_CATEGORIES.contains( sqlStateClassCode ) ) {
-				return new DataException( message, sqlException, sql );
-			}
-		}
-		return handledNonSpecificException( sqlException, message, sql );
-	}
-
-	/**
-	 * Handle an exception not converted to a specific type based on the SQLState.
-	 *
-	 * @param sqlException The exception to be handled.
-	 * @param message	  An optional message
-	 * @param sql		  Optionally, the sql being performed when the exception occurred.
-	 * @return The converted exception; should <b>never</b> be null.
-	 */
-	protected JDBCException handledNonSpecificException(SQLException sqlException, String message, String sql) {
-		return new GenericJDBCException( message, sqlException, sql );
-	}
-}
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.exception.internal;
+
+import java.sql.SQLException;
+import java.util.HashSet;
+import java.util.Set;
+
+import org.hibernate.JDBCException;
+import org.hibernate.exception.ConstraintViolationException;
+import org.hibernate.exception.DataException;
+import org.hibernate.exception.GenericJDBCException;
+import org.hibernate.exception.JDBCConnectionException;
+import org.hibernate.exception.SQLGrammarException;
+import org.hibernate.exception.spi.SQLExceptionConverter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
+import org.hibernate.internal.util.JdbcExceptionHelper;
+
+/**
+ * A SQLExceptionConverter implementation specific to Cach&eacute; SQL,
+ * accounting for its custom integrity constraint violation error codes.
+ *
+ * @author Jonathan Levinson
+ */
+public class CacheSQLStateConverter implements SQLExceptionConverter {
+
+	private ViolatedConstraintNameExtracter extracter;
+
+	private static final Set SQL_GRAMMAR_CATEGORIES = new HashSet();
+	private static final Set DATA_CATEGORIES = new HashSet();
+	private static final Set INTEGRITY_VIOLATION_CATEGORIES = new HashSet();
+	private static final Set CONNECTION_CATEGORIES = new HashSet();
+
+	static {
+		SQL_GRAMMAR_CATEGORIES.add( "07" );
+		SQL_GRAMMAR_CATEGORIES.add( "37" );
+		SQL_GRAMMAR_CATEGORIES.add( "42" );
+		SQL_GRAMMAR_CATEGORIES.add( "65" );
+		SQL_GRAMMAR_CATEGORIES.add( "S0" );
+		SQL_GRAMMAR_CATEGORIES.add( "20" );
+
+		DATA_CATEGORIES.add( "22" );
+		DATA_CATEGORIES.add( "21" );
+		DATA_CATEGORIES.add( "02" );
+
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 119 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 120 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 121 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 122 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 123 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 124 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 125 ) );
+		INTEGRITY_VIOLATION_CATEGORIES.add( new Integer( 127 ) );
+
+		CONNECTION_CATEGORIES.add( "08" );
+	}
+
+	public CacheSQLStateConverter(ViolatedConstraintNameExtracter extracter) {
+		this.extracter = extracter;
+	}
+
+	/**
+	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
+	 *
+	 * @param sqlException The SQLException to be converted.
+	 * @param message	  An optional error message.
+	 * @param sql		  Optionally, the sql being performed when the exception occurred.
+	 * @return The resulting JDBCException.
+	 */
+	public JDBCException convert(SQLException sqlException, String message, String sql) {
+		String sqlStateClassCode = JdbcExceptionHelper.extractSqlStateClassCode( sqlException );
+		Integer errorCode = new Integer( JdbcExceptionHelper.extractErrorCode( sqlException ) );
+		if ( sqlStateClassCode != null ) {
+			if ( SQL_GRAMMAR_CATEGORIES.contains( sqlStateClassCode ) ) {
+				return new SQLGrammarException( message, sqlException, sql );
+			}
+			else if ( INTEGRITY_VIOLATION_CATEGORIES.contains( errorCode ) ) {
+				String constraintName = extracter.extractConstraintName( sqlException );
+				return new ConstraintViolationException( message, sqlException, sql, constraintName );
+			}
+			else if ( CONNECTION_CATEGORIES.contains( sqlStateClassCode ) ) {
+				return new JDBCConnectionException( message, sqlException, sql );
+			}
+			else if ( DATA_CATEGORIES.contains( sqlStateClassCode ) ) {
+				return new DataException( message, sqlException, sql );
+			}
+		}
+		return handledNonSpecificException( sqlException, message, sql );
+	}
+
+	/**
+	 * Handle an exception not converted to a specific type based on the SQLState.
+	 *
+	 * @param sqlException The exception to be handled.
+	 * @param message	  An optional message
+	 * @param sql		  Optionally, the sql being performed when the exception occurred.
+	 * @return The converted exception; should <b>never</b> be null.
+	 */
+	protected JDBCException handledNonSpecificException(SQLException sqlException, String message, String sql) {
+		return new GenericJDBCException( message, sqlException, sql );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/internal/SQLStateConverter.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/internal/SQLStateConverter.java
index b8f57f6a41..2eda211532 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/SQLStateConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/internal/SQLStateConverter.java
@@ -1,142 +1,150 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
-package org.hibernate.exception;
+package org.hibernate.exception.internal;
 
 import java.sql.SQLException;
 import java.util.HashSet;
 import java.util.Set;
+
 import org.hibernate.JDBCException;
 import org.hibernate.PessimisticLockException;
 import org.hibernate.QueryTimeoutException;
+import org.hibernate.exception.ConstraintViolationException;
+import org.hibernate.exception.DataException;
+import org.hibernate.exception.GenericJDBCException;
+import org.hibernate.exception.JDBCConnectionException;
+import org.hibernate.exception.LockAcquisitionException;
+import org.hibernate.exception.SQLGrammarException;
+import org.hibernate.exception.spi.SQLExceptionConverter;
+import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 
 /**
  * A SQLExceptionConverter implementation which performs converion based on
  * the underlying SQLState. Interpretation of a SQL error based on SQLState
  * is not nearly as accurate as using the ErrorCode (which is, however, vendor-
  * specific).  Use of a ErrorCode-based converter should be preferred approach
  * for converting/interpreting SQLExceptions.
  *
  * @author Steve Ebersole
  */
 public class SQLStateConverter implements SQLExceptionConverter {
 
 	private ViolatedConstraintNameExtracter extracter;
 
 	private static final Set SQL_GRAMMAR_CATEGORIES = new HashSet();
 	private static final Set DATA_CATEGORIES = new HashSet();
 	private static final Set INTEGRITY_VIOLATION_CATEGORIES = new HashSet();
 	private static final Set CONNECTION_CATEGORIES = new HashSet();
 
 	static {
 		SQL_GRAMMAR_CATEGORIES.add( "07" );
 		SQL_GRAMMAR_CATEGORIES.add( "37" );
 		SQL_GRAMMAR_CATEGORIES.add( "42" );
 		SQL_GRAMMAR_CATEGORIES.add( "65" );
 		SQL_GRAMMAR_CATEGORIES.add( "S0" );
 		SQL_GRAMMAR_CATEGORIES.add( "20" );
 		
 		DATA_CATEGORIES.add("22");
 		DATA_CATEGORIES.add("21");
 		DATA_CATEGORIES.add("02");
 
 		INTEGRITY_VIOLATION_CATEGORIES.add( "23" );
 		INTEGRITY_VIOLATION_CATEGORIES.add( "27" );
 		INTEGRITY_VIOLATION_CATEGORIES.add( "44" );
 
 		CONNECTION_CATEGORIES.add( "08" );
 	}
 
 	public SQLStateConverter(ViolatedConstraintNameExtracter extracter) {
 		this.extracter = extracter;
 	}
 
 	/**
 	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
 	 *
 	 * @param sqlException The SQLException to be converted.
 	 * @param message      An optional error message.
 	 * @param sql          Optionally, the sql being performed when the exception occurred.
 	 * @return The resulting JDBCException.
 	 */
 	public JDBCException convert(SQLException sqlException, String message, String sql) {
 		String sqlState = JdbcExceptionHelper.extractSqlState( sqlException );
 
 		if ( sqlState != null ) {
 			String sqlStateClassCode = JdbcExceptionHelper.determineSqlStateClassCode( sqlState );
 
 			if ( sqlStateClassCode != null ) {
 				if ( SQL_GRAMMAR_CATEGORIES.contains( sqlStateClassCode ) ) {
 					return new SQLGrammarException( message, sqlException, sql );
 				}
 				else if ( INTEGRITY_VIOLATION_CATEGORIES.contains( sqlStateClassCode ) ) {
 					String constraintName = extracter.extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 				else if ( CONNECTION_CATEGORIES.contains( sqlStateClassCode ) ) {
 					return new JDBCConnectionException( message, sqlException, sql );
 				}
 				else if ( DATA_CATEGORIES.contains( sqlStateClassCode ) ) {
 					return new DataException( message, sqlException, sql );
 				}
 			}
 
 			if ( "40001".equals( sqlState ) ) {
 				return new LockAcquisitionException( message, sqlException, sql );
 			}
 
 			if ( "61000".equals( sqlState ) ) {
 				// oracle sql-state code for deadlock
 				return new LockAcquisitionException( message, sqlException, sql );
 			}
 
 			if ( "40XL1".equals( sqlState ) || "40XL2".equals( sqlState )) {
 				// Derby "A lock could not be obtained within the time requested."
 				return new PessimisticLockException( message, sqlException, sql );
 			}
 
 			// MySQL Query execution was interrupted
 			if ( "70100".equals( sqlState ) ||
 				// Oracle user requested cancel of current operation
 				  "72000".equals( sqlState ) ) {
 				throw new QueryTimeoutException(  message, sqlException, sql );
 			}
 		}
 
 		return handledNonSpecificException( sqlException, message, sql );
 	}
 
 	/**
 	 * Handle an exception not converted to a specific type based on the SQLState.
 	 *
 	 * @param sqlException The exception to be handled.
 	 * @param message      An optional message
 	 * @param sql          Optionally, the sql being performed when the exception occurred.
 	 * @return The converted exception; should <b>never</b> be null.
 	 */
 	protected JDBCException handledNonSpecificException(SQLException sqlException, String message, String sql) {
 		return new GenericJDBCException( message, sqlException, sql );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/Configurable.java b/hibernate-core/src/main/java/org/hibernate/exception/spi/Configurable.java
similarity index 83%
rename from hibernate-core/src/main/java/org/hibernate/exception/Configurable.java
rename to hibernate-core/src/main/java/org/hibernate/exception/spi/Configurable.java
index 70306305a0..6b86231ab2 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/Configurable.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/spi/Configurable.java
@@ -1,45 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
-package org.hibernate.exception;
+package org.hibernate.exception.spi;
+
 import java.util.Properties;
+
 import org.hibernate.HibernateException;
 
 /**
  * The Configurable interface defines the contract for SQLExceptionConverter impls that
  * want to be configured prior to usage given the currently defined Hibernate properties.
  *
  * @author Steve Ebersole
  */
 public interface Configurable {
-	// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.
-
 	/**
 	 * Configure the component, using the given settings and properties.
 	 *
 	 * @param properties All defined startup properties.
 	 * @throws HibernateException Indicates a configuration exception.
 	 */
 	public void configure(Properties properties) throws HibernateException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/exception/spi/SQLExceptionConverter.java
similarity index 82%
rename from hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/spi/SQLExceptionConverter.java
index 143395f6ba..2da3dcea39 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/spi/SQLExceptionConverter.java
@@ -1,55 +1,55 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
-package org.hibernate.exception;
+package org.hibernate.exception.spi;
 
 import java.io.Serializable;
 import java.sql.SQLException;
+
 import org.hibernate.JDBCException;
 
 /**
  * Defines a contract for implementations that know how to convert a SQLException
  * into Hibernate's JDBCException hierarchy.  Inspired by Spring's
  * SQLExceptionTranslator.
  * <p/>
  * Implementations <b>must</b> have a constructor which takes a
  * {@link ViolatedConstraintNameExtracter} parameter.
  * <p/>
- * Implementations may implement {@link Configurable} if they need to perform
+ * Implementations may implement {@link org.hibernate.exception.spi.Configurable} if they need to perform
  * configuration steps prior to first use.
  *
  * @author Steve Ebersole
  * @see SQLExceptionConverterFactory
  */
 public interface SQLExceptionConverter extends Serializable {
 	/**
 	 * Convert the given SQLException into the Hibernate {@link JDBCException} hierarchy.
 	 *
 	 * @param sqlException The SQLException to be converted.
 	 * @param message      An optional error message.
 	 * @return The resulting JDBCException.
-	 * @see ConstraintViolationException, JDBCConnectionException, SQLGrammarException, LockAcquisitionException
+	 * @see org.hibernate.exception.ConstraintViolationException , JDBCConnectionException, SQLGrammarException, LockAcquisitionException
 	 */
 	public JDBCException convert(SQLException sqlException, String message, String sql);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java b/hibernate-core/src/main/java/org/hibernate/exception/spi/SQLExceptionConverterFactory.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
rename to hibernate-core/src/main/java/org/hibernate/exception/spi/SQLExceptionConverterFactory.java
index 40e4b58265..ba967a548d 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/SQLExceptionConverterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/spi/SQLExceptionConverterFactory.java
@@ -1,138 +1,139 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
-package org.hibernate.exception;
+package org.hibernate.exception.spi;
 
 import java.lang.reflect.Constructor;
 import java.sql.SQLException;
 import java.util.Properties;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
+import org.hibernate.exception.GenericJDBCException;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 
-import org.jboss.logging.Logger;
-
 /**
  * A factory for building SQLExceptionConverter instances.
  *
  * @author Steve Ebersole
  */
 public class SQLExceptionConverterFactory {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        SQLExceptionConverterFactory.class.getName());
 
 	private SQLExceptionConverterFactory() {
 		// Private constructor - stops checkstyle from complaining.
 	}
 
 	/**
 	 * Build a SQLExceptionConverter instance.
 	 * <p/>
 	 * First, looks for a {@link Environment#SQL_EXCEPTION_CONVERTER} property to see
 	 * if the configuration specified the class of a specific converter to use.  If this
 	 * property is set, attempt to construct an instance of that class.  If not set, or
 	 * if construction fails, the converter specific to the dialect will be used.
 	 *
 	 * @param dialect    The defined dialect.
 	 * @param properties The configuration properties.
 	 * @return An appropriate SQLExceptionConverter instance.
 	 * @throws HibernateException There was an error building the SQLExceptionConverter.
 	 */
 	public static SQLExceptionConverter buildSQLExceptionConverter(Dialect dialect, Properties properties) throws HibernateException {
 		SQLExceptionConverter converter = null;
 
 		String converterClassName = ( String ) properties.get( Environment.SQL_EXCEPTION_CONVERTER );
 		if ( StringHelper.isNotEmpty( converterClassName ) ) {
 			converter = constructConverter( converterClassName, dialect.getViolatedConstraintNameExtracter() );
 		}
 
 		if ( converter == null ) {
             LOG.trace("Using dialect defined converter");
 			converter = dialect.buildSQLExceptionConverter();
 		}
 
 		if ( converter instanceof Configurable ) {
 			try {
-				( ( Configurable ) converter ).configure( properties );
+				( (Configurable) converter ).configure( properties );
 			}
 			catch ( HibernateException e ) {
                 LOG.unableToConfigureSqlExceptionConverter(e);
 				throw e;
 			}
 		}
 
 		return converter;
 	}
 
 	/**
 	 * Builds a minimal converter.  The instance returned here just always converts to
-	 * {@link GenericJDBCException}.
+	 * {@link org.hibernate.exception.GenericJDBCException}.
 	 *
 	 * @return The minimal converter.
 	 */
 	public static SQLExceptionConverter buildMinimalSQLExceptionConverter() {
 		return new SQLExceptionConverter() {
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				return new GenericJDBCException( message, sqlException, sql );
 			}
 		};
 	}
 
 	private static SQLExceptionConverter constructConverter(String converterClassName, ViolatedConstraintNameExtracter violatedConstraintNameExtracter) {
 		try {
             LOG.trace("Attempting to construct instance of specified SQLExceptionConverter [" + converterClassName + "]");
 			Class converterClass = ReflectHelper.classForName( converterClassName );
 
 			// First, try to find a matching constructor accepting a ViolatedConstraintNameExtracter param...
 			Constructor[] ctors = converterClass.getDeclaredConstructors();
 			for ( int i = 0; i < ctors.length; i++ ) {
 				if ( ctors[i].getParameterTypes() != null && ctors[i].getParameterTypes().length == 1 ) {
 					if ( ViolatedConstraintNameExtracter.class.isAssignableFrom( ctors[i].getParameterTypes()[0] ) ) {
 						try {
 							return ( SQLExceptionConverter )
 									ctors[i].newInstance( new Object[]{violatedConstraintNameExtracter} );
 						}
 						catch ( Throwable t ) {
 							// eat it and try next
 						}
 					}
 				}
 			}
 
 			// Otherwise, try to use the no-arg constructor
 			return ( SQLExceptionConverter ) converterClass.newInstance();
 
 		}
 		catch ( Throwable t ) {
             LOG.unableToConstructSqlExceptionConverter(t);
 		}
 
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java b/hibernate-core/src/main/java/org/hibernate/exception/spi/TemplatedViolatedConstraintNameExtracter.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/spi/TemplatedViolatedConstraintNameExtracter.java
index 31609eb162..303d2be611 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/TemplatedViolatedConstraintNameExtracter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/spi/TemplatedViolatedConstraintNameExtracter.java
@@ -1,59 +1,57 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
-package org.hibernate.exception;
-
+package org.hibernate.exception.spi;
 
 /**
  * Knows how to extract a violated constraint name from an error message based on the
  * fact that the constraint name is templated within the message.
  *
  * @author Steve Ebersole
  */
 public abstract class TemplatedViolatedConstraintNameExtracter implements ViolatedConstraintNameExtracter {
 
 	/**
 	 * Extracts the constraint name based on a template (i.e., <i>templateStart</i><b>constraintName</b><i>templateEnd</i>).
 	 *
 	 * @param templateStart The pattern denoting the start of the constraint name within the message.
 	 * @param templateEnd   The pattern denoting the end of the constraint name within the message.
 	 * @param message       The templated error message containing the constraint name.
 	 * @return The found constraint name, or null.
 	 */
 	protected String extractUsingTemplate(String templateStart, String templateEnd, String message) {
 		int templateStartPosition = message.indexOf( templateStart );
 		if ( templateStartPosition < 0 ) {
 			return null;
 		}
 
 		int start = templateStartPosition + templateStart.length();
 		int end = message.indexOf( templateEnd, start );
 		if ( end < 0 ) {
 			end = message.length();
 		}
 
 		return message.substring( start, end );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java b/hibernate-core/src/main/java/org/hibernate/exception/spi/ViolatedConstraintNameExtracter.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java
rename to hibernate-core/src/main/java/org/hibernate/exception/spi/ViolatedConstraintNameExtracter.java
index 6e733fcd71..c6e9060b61 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/ViolatedConstraintNameExtracter.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/spi/ViolatedConstraintNameExtracter.java
@@ -1,42 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
-package org.hibernate.exception;
+package org.hibernate.exception.spi;
+
 import java.sql.SQLException;
 
 /**
  * Defines a contract for implementations that can extract the name of a violated
  * constraint from a SQLException that is the result of that constraint violation.
  *
  * @author Steve Ebersole
  */
 public interface ViolatedConstraintNameExtracter {
 	/**
 	 * Extract the name of the violated constraint from the given SQLException.
 	 *
 	 * @param sqle The exception that was the result of the constraint violation.
 	 * @return The extracted constraint name.
 	 */
 	public String extractConstraintName(SQLException sqle);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 6bcf97a6e4..1baa86829b 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1102 +1,1102 @@
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
-import org.hibernate.exception.SQLExceptionConverter;
+import org.hibernate.exception.spi.SQLExceptionConverter;
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
  * @see org.hibernate.hql.QueryTranslator
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 3f804204d0..1e109d5f98 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1062 +1,1062 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
+import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
-import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractCollectionPersister.class.getName());
 
     // TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	//SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final String sqlWhereString;
 	private final String sqlOrderByStringTemplate;
 	private final String sqlWhereStringTemplate;
 	private final boolean hasOrder;
 	protected final boolean hasWhere;
 	private final int baseIndex;
 
 	private final String nodeName;
 	private final String elementNodeName;
 	private final String indexNodeName;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 
 	//types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	//columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	//private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	private final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	//extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	private final boolean hasManyToManyOrder;
 	private final String manyToManyOrderByTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	public AbstractCollectionPersister(
 			final Collection collection,
 			final CollectionRegionAccessStrategy cacheAccessStrategy,
 			final Configuration cfg,
 			final SessionFactoryImplementor factory) throws MappingException, CacheException {
 
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collection.isMap() ?
 					( CacheEntryStructure ) new StructuredMapCacheEntry() :
 					( CacheEntryStructure ) new StructuredCollectionCacheEntry();
 		}
 		else {
 			cacheEntryStructure = new UnstructuredCacheEntry();
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister(entityName);
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		//isSet = collection.isSet();
 		//isSorted = collection.isSorted();
 		isPrimitiveArray = collection.isPrimitiveArray();
 		isArray = collection.isArray();
 		subselectLoadable = collection.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 			);
 
 		int spacesSize = 1 + collection.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collection.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collection.getWhere() ) ? "( " + collection.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate(sqlWhereString, dialect, factory.getSqlFunctionRegistry()) :
 				null;
 
 		hasOrphanDelete = collection.hasOrphanDelete();
 
 		int batch = collection.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collection.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collection.getKey().getType();
 		iter = collection.getKey().getColumnIterator();
 		int keySpan = collection.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName(dialect);
 			keyColumnAliases[k] = col.getAlias(dialect,collection.getOwner().getRootTable());
 			k++;
 		}
 
 		//unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		//ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister(entityName);
 			if ( elemNode==null ) {
 				elemNode = cfg.getClassMapping(entityName).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 		elementNodeName = elemNode;
 
 		int elementSpan = collection.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collection.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias(dialect);
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName(dialect);
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr(dialect);
 				elementColumnReaderTemplates[j] = col.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 
 		//workaround, for backward compatibility of sets with no
 		//not-null columns, assume all columns are used in the
 		//row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if (hasIndex) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collection;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias(dialect);
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate(dialect, factory.getSqlFunctionRegistry());
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName(dialect);
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 
 			indexNodeName = indexedCollection.getIndexNodeName();
 
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 			indexNodeName = null;
 		}
 
 		hasIdentifier = collection.isIdentified();
 		if (hasIdentifier) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = ( Column ) iter.next();
 			identifierColumnName = col.getQuotedName(dialect);
 			identifierColumnAlias = col.getAlias(dialect);
 			//unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					cfg.getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 			);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			//unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		//GENERATE THE SQL:
 
 		//sqlSelectString = sqlSelectString();
 		//sqlSelectRowString = sqlSelectRowString();
 
 		if ( collection.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collection.getCustomSQLInsert();
 			insertCallable = collection.isCustomInsertCallable();
 			insertCheckStyle = collection.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLInsert(), insertCallable )
 		            : collection.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collection.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collection.getCustomSQLUpdate();
 			updateCallable = collection.isCustomUpdateCallable();
 			updateCheckStyle = collection.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLUpdate(), insertCallable )
 		            : collection.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collection.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collection.getCustomSQLDelete();
 			deleteCallable = collection.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collection.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collection.getCustomSQLDeleteAll();
 			deleteAllCallable = collection.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString(  collection.isIndexed() && !collection.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collection.isLazy();
 		isExtraLazy = collection.isExtraLazy();
 
 		isInverse = collection.isInverse();
 
 		if ( collection.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collection ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; //elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping(
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory
 				);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping(
 					elementColumnNames,
 					elementType
 				);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { //not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping(
 						elementColumnNames,
 						elementType
 					);
 			}
 		}
 
 		hasOrder = collection.getOrderBy() != null;
 		if ( hasOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
 					collection.getOrderBy(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			sqlOrderByStringTemplate = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
 					collection.getManyToManyOrdering(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTemplate = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("Static SQL for collection: %s", getRole());
             if (getSQLInsertRowString() != null) LOG.debugf(" Row insert: %s", getSQLInsertRowString());
             if (getSQLUpdateRowString() != null) LOG.debugf(" Row update: %s", getSQLUpdateRowString());
             if (getSQLDeleteRowString() != null) LOG.debugf(" Row delete: %s", getSQLDeleteRowString());
             if (getSQLDeleteString() != null) LOG.debugf(" One-shot delete: %s", getSQLDeleteString());
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			//if there is a user-specified loader, return that
 			//TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
 		if (subselect == null) {
 			return null;
 		}
 		else {
 
 			// Take care of any entities that might have
 			// been evicted!
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}
 
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass() { //needed by arrays
 		return elementClass;
 	}
 
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() - baseIndex );
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 	throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role );  //an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsSettable, session);
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue(indexColumnIsSettable);
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() + baseIndex );
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (elementIsPureFormula) {
 			throw new AssertionFailure("cannot use a formula-based element in the where condition");
 		}
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsInPrimaryKey, session);
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (indexContainsFormula) {
 			throw new AssertionFailure("cannot use a formula-based index in the where condition");
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		} else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 			"max(" + getIndexColumnNames()[0] + ") + 1": //lists, arrays
 			"count(" + getElementColumnNames()[0] + ")"; //sets, maps, bags
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn(selectValue)
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i=0; i<elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i=0; i<indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify(alias, indexColumnNames, indexFormulaTemplates);
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify(alias, elementColumnNames, elementFormulaTemplates);
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for (int i=0; i<span; i++) {
 			if ( columnNames[i]==null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; //TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
index 312b81bd23..787cb8ac40 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/DatabaseMetadata.java
@@ -1,197 +1,197 @@
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
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
-import org.hibernate.exception.SQLExceptionConverter;
+import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Table;
 
 import org.jboss.logging.Logger;
 
 /**
  * JDBC database metadata
  * @author Christoph Sturm, Teodor Danciu
  */
 public class DatabaseMetadata {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DatabaseMetaData.class.getName());
 
 	private final Map tables = new HashMap();
 	private final Set sequences = new HashSet();
 	private final boolean extras;
 
 	private DatabaseMetaData meta;
 	private SQLExceptionConverter sqlExceptionConverter;
 
 	public DatabaseMetadata(Connection connection, Dialect dialect) throws SQLException {
 		this(connection, dialect, true);
 	}
 
 	public DatabaseMetadata(Connection connection, Dialect dialect, boolean extras) throws SQLException {
 		sqlExceptionConverter = dialect.buildSQLExceptionConverter();
 		meta = connection.getMetaData();
 		this.extras = extras;
 		initSequences(connection, dialect);
 	}
 
 	private static final String[] TYPES = {"TABLE", "VIEW"};
 
 	public TableMetadata getTableMetadata(String name, String schema, String catalog, boolean isQuoted) throws HibernateException {
 
 		Object identifier = identifier(catalog, schema, name);
 		TableMetadata table = (TableMetadata) tables.get(identifier);
 		if (table!=null) {
 			return table;
 		}
 		else {
 
 			try {
 				ResultSet rs = null;
 				try {
 					if ( (isQuoted && meta.storesMixedCaseQuotedIdentifiers())) {
 						rs = meta.getTables(catalog, schema, name, TYPES);
 					} else if ( (isQuoted && meta.storesUpperCaseQuotedIdentifiers())
 						|| (!isQuoted && meta.storesUpperCaseIdentifiers() )) {
 						rs = meta.getTables(
 								StringHelper.toUpperCase(catalog),
 								StringHelper.toUpperCase(schema),
 								StringHelper.toUpperCase(name),
 								TYPES
 							);
 					}
 					else if ( (isQuoted && meta.storesLowerCaseQuotedIdentifiers())
 							|| (!isQuoted && meta.storesLowerCaseIdentifiers() )) {
 						rs = meta.getTables( 
 								StringHelper.toLowerCase( catalog ),
 								StringHelper.toLowerCase(schema), 
 								StringHelper.toLowerCase(name), 
 								TYPES 
 							);
 					}
 					else {
 						rs = meta.getTables(catalog, schema, name, TYPES);
 					}
 
 					while ( rs.next() ) {
 						String tableName = rs.getString("TABLE_NAME");
 						if ( name.equalsIgnoreCase(tableName) ) {
 							table = new TableMetadata(rs, meta, extras);
 							tables.put(identifier, table);
 							return table;
 						}
 					}
 
                     LOG.tableNotFound(name);
 					return null;
 
 				}
 				finally {
 					if (rs!=null) rs.close();
 				}
 			}
 			catch (SQLException sqlException) {
 				throw new SqlExceptionHelper( sqlExceptionConverter )
 						.convert( sqlException, "could not get table metadata: " + name );
 			}
 		}
 
 	}
 
 	private Object identifier(String catalog, String schema, String name) {
 		return Table.qualify(catalog,schema,name);
 	}
 
 	private void initSequences(Connection connection, Dialect dialect) throws SQLException {
 		if ( dialect.supportsSequences() ) {
 			String sql = dialect.getQuerySequencesString();
 			if (sql!=null) {
 
 				Statement statement = null;
 				ResultSet rs = null;
 				try {
 					statement = connection.createStatement();
 					rs = statement.executeQuery(sql);
 
 					while ( rs.next() ) {
 						sequences.add( rs.getString(1).toLowerCase().trim() );
 					}
 				}
 				finally {
 					if (rs!=null) rs.close();
 					if (statement!=null) statement.close();
 				}
 
 			}
 		}
 	}
 
 	public boolean isSequence(Object key) {
 		if (key instanceof String){
 			String[] strings = StringHelper.split(".", (String) key);
 			return sequences.contains( strings[strings.length-1].toLowerCase());
 		}
 		return false;
 	}
 
  	public boolean isTable(Object key) throws HibernateException {
  		if(key instanceof String) {
 			Table tbl = new Table((String)key);
 			if ( getTableMetadata( tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted() ) != null ) {
  				return true;
  			} else {
  				String[] strings = StringHelper.split(".", (String) key);
  				if(strings.length==3) {
 					tbl = new Table(strings[2]);
 					tbl.setCatalog(strings[0]);
 					tbl.setSchema(strings[1]);
 					return getTableMetadata( tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted() ) != null;
  				} else if (strings.length==2) {
 					tbl = new Table(strings[1]);
 					tbl.setSchema(strings[0]);
 					return getTableMetadata( tbl.getName(), tbl.getSchema(), tbl.getCatalog(), tbl.isQuoted() ) != null;
  				}
  			}
  		}
  		return false;
  	}
 
 	@Override
     public String toString() {
 		return "DatabaseMetadata" + tables.keySet().toString() + sequences.toString();
 	}
 }
