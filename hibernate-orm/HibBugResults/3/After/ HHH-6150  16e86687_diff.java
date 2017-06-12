diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
new file mode 100644
index 0000000000..78999cd7a2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -0,0 +1,517 @@
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
+package org.hibernate.cfg;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface AvailableSettings {
+	/**
+	 * Names a {@literal JNDI} namespace into which the {@link org.hibernate.SessionFactory} should be bound.
+	 */
+	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
+
+	/**
+	 * Names the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} to use for obtaining
+	 * JDBC connections.  Can either reference an instance of
+	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} or a {@link Class} or {@link String}
+	 * reference to the {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider} implementation
+	 * class.
+	 */
+	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
+
+	/**
+	 * Names the {@literal JDBC} driver class
+	 */
+	public static final String DRIVER ="hibernate.connection.driver_class";
+
+	/**
+	 * Names the {@literal JDBC} connection url.
+	 */
+	public static final String URL ="hibernate.connection.url";
+
+	/**
+	 * Names the connection user.  This might mean one of 2 things in out-of-the-box Hibernate
+	 * {@link org.hibernate.service.jdbc.connections.spi.ConnectionProvider}: <ul>
+	 *     <li>The username used to pass along to creating the JDBC connection</li>
+	 *     <li>The username used to obtain a JDBC connection from a data source</li>
+	 * </ul>
+	 */
+	public static final String USER ="hibernate.connection.username";
+
+	/**
+	 * Names the connection password.  See usage discussion on {@link #USER}
+	 */
+	public static final String PASS ="hibernate.connection.password";
+
+	/**
+	 * Names the {@literal JDBC} transaction isolation level
+	 */
+	public static final String ISOLATION ="hibernate.connection.isolation";
+
+	/**
+	 * Names the {@literal JDBC} autocommit mode
+	 */
+	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
+
+	/**
+	 * Maximum number of inactive connections for the built-in Hibernate connection pool.
+	 */
+	public static final String POOL_SIZE ="hibernate.connection.pool_size";
+
+	/**
+	 * Names a {@link javax.sql.DataSource}.  Can either reference a {@link javax.sql.DataSource} instance or
+	 * a {@literal JNDI} name under which to locate the {@link javax.sql.DataSource}.
+	 */
+	public static final String DATASOURCE ="hibernate.connection.datasource";
+
+	/**
+	 * Names a prefix used to define arbitrary JDBC connection properties.  These properties are passed along to
+	 * the {@literal JDBC} provider when creating a connection.
+	 */
+	public static final String CONNECTION_PREFIX = "hibernate.connection";
+
+	/**
+	 * Names the {@literal JNDI} {@link javax.naming.InitialContext} class.
+	 *
+	 * @see javax.naming.Context#INITIAL_CONTEXT_FACTORY
+	 */
+	public static final String JNDI_CLASS ="hibernate.jndi.class";
+
+	/**
+	 * Names the {@literal JNDI} provider/connection url
+	 *
+	 * @see javax.naming.Context#PROVIDER_URL
+	 */
+	public static final String JNDI_URL ="hibernate.jndi.url";
+
+	/**
+	 * Names a prefix used to define arbitrary {@literal JNDI} {@link javax.naming.InitialContext} properties.  These
+	 * properties are passed along to {@link javax.naming.InitialContext#InitialContext(java.util.Hashtable)}
+	 */
+	public static final String JNDI_PREFIX = "hibernate.jndi";
+
+	/**
+	 * Names the Hibernate {@literal SQL} {@link org.hibernate.dialect.Dialect} class
+	 */
+	public static final String DIALECT ="hibernate.dialect";
+
+	/**
+	 * Names any additional {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} implementations to
+	 * register with the standard {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}.
+	 */
+	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
+
+
+	/**
+	 * A default database schema (owner) name to use for unqualified tablenames
+	 */
+	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
+	/**
+	 * A default database catalog name to use for unqualified tablenames
+	 */
+	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
+
+	/**
+	 * Enable logging of generated SQL to the console
+	 */
+	public static final String SHOW_SQL ="hibernate.show_sql";
+	/**
+	 * Enable formatting of SQL logged to the console
+	 */
+	public static final String FORMAT_SQL ="hibernate.format_sql";
+	/**
+	 * Add comments to the generated SQL
+	 */
+	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
+	/**
+	 * Maximum depth of outer join fetching
+	 */
+	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
+	/**
+	 * The default batch size for batch fetching
+	 */
+	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
+	/**
+	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
+	 */
+	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
+	/**
+	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
+	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
+	 */
+	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
+	/**
+	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
+	 * method. In general, performance will be better if this property is set to true and the underlying
+	 * JDBC driver supports getGeneratedKeys().
+	 */
+	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
+	/**
+	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
+	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
+	 */
+	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
+	/**
+	 * Maximum JDBC batch size. A nonzero value enables batch updates.
+	 */
+	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
+	/**
+	 * Select a custom batcher.
+	 */
+	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
+	/**
+	 * Should versioned data be included in batching?
+	 */
+	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
+	/**
+	 * An XSLT resource used to generate "custom" XML
+	 */
+	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
+
+	/**
+	 * Maximum size of C3P0 connection pool
+	 */
+	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
+	/**
+	 * Minimum size of C3P0 connection pool
+	 */
+	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
+
+	/**
+	 * Maximum idle time for C3P0 connection pool
+	 */
+	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
+	/**
+	 * Maximum size of C3P0 statement cache
+	 */
+	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
+	/**
+	 * Number of connections acquired when pool is exhausted
+	 */
+	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
+	/**
+	 * Idle time before a C3P0 pooled connection is validated
+	 */
+	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
+
+	/**
+	 * Proxool/Hibernate property prefix
+	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
+	 */
+	public static final String PROXOOL_PREFIX = "hibernate.proxool";
+	/**
+	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
+	 */
+	public static final String PROXOOL_XML = "hibernate.proxool.xml";
+	/**
+	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
+	 */
+	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
+	/**
+	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
+	 */
+	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
+	/**
+	 * Proxool property with the Proxool pool alias to use
+	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
+	 * <tt>PROXOOL_XML</tt>)
+	 */
+	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
+
+	/**
+	 * Enable automatic session close at end of transaction
+	 */
+	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
+	/**
+	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
+	 */
+	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
+	/**
+	 * Specifies how Hibernate should release JDBC connections.
+	 */
+	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
+	/**
+	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
+	 */
+	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
+
+	/**
+	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
+	 * creating {@link org.hibernate.Transaction} instances
+	 */
+	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
+
+	/**
+	 * Names the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation to use for integrating
+	 * with {@literal JTA} systems.  Can reference either a {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
+	 * instance or the name of the {@link org.hibernate.service.jta.platform.spi.JtaPlatform} implementation class
+	 * @since 4.0
+	 */
+	public static final String JTA_PLATFORM = "hibernate.transaction.jta.platform";
+
+	/**
+	 * Names the {@link org.hibernate.transaction.TransactionManagerLookup} implementation to use for obtaining
+	 * reference to the {@literal JTA} {@link javax.transaction.TransactionManager}
+	 *
+	 * @deprecated See {@link #JTA_PLATFORM}
+	 */
+	@Deprecated
+	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
+
+	/**
+	 * JNDI name of JTA <tt>UserTransaction</tt> object
+	 *
+	 * @deprecated See {@link #JTA_PLATFORM}
+	 */
+	@Deprecated
+	public static final String USER_TRANSACTION = "jta.UserTransaction";
+
+	/**
+	 * The <tt>CacheProvider</tt> implementation class
+	 *
+	 * @deprecated See {@link #CACHE_REGION_FACTORY}
+	 */
+	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
+
+	/**
+	 * The {@link org.hibernate.cache.RegionFactory} implementation class
+	 */
+	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
+
+	/**
+	 * The <tt>CacheProvider</tt> implementation class
+	 */
+	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
+	/**
+	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
+	 */
+	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
+	/**
+	 * Enable the query cache (disabled by default)
+	 */
+	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
+	/**
+	 * The <tt>QueryCacheFactory</tt> implementation class.
+	 */
+	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
+	/**
+	 * Enable the second-level cache (enabled by default)
+	 */
+	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
+	/**
+	 * Optimize the cache for minimal puts instead of minimal gets
+	 */
+	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
+	/**
+	 * The <tt>CacheProvider</tt> region name prefix
+	 */
+	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
+	/**
+	 * Enable use of structured second-level cache entries
+	 */
+	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
+
+	/**
+	 * Enable statistics collection
+	 */
+	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
+
+	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
+
+	/**
+	 * Use bytecode libraries optimized property access
+	 */
+	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
+
+	/**
+	 * The classname of the HQL query parser factory
+	 */
+	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
+
+	/**
+	 * A comma-separated list of token substitutions to use when translating a Hibernate
+	 * query to SQL
+	 */
+	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
+
+	/**
+	 * Should named queries be checked during startup (the default is enabled).
+	 * <p/>
+	 * Mainly intended for test environments.
+	 */
+	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
+
+	/**
+	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
+	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
+	 */
+	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
+
+	/**
+	 * Comma-separated names of the optional files containing SQL DML statements executed
+	 * during the SessionFactory creation.
+	 * File order matters, the statements of a give file are executed before the statements of the
+	 * following files.
+	 *
+	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
+	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
+	 *
+	 * The default value is <tt>/import.sql</tt>
+	 */
+	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
+
+	/**
+	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
+	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
+	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
+	 */
+	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
+
+	/**
+	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
+	 * broken JDBC drivers
+	 */
+	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
+
+	/**
+	 * Enable ordering of update statements by primary key value
+	 */
+	public static final String ORDER_UPDATES = "hibernate.order_updates";
+
+	/**
+	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
+	 */
+	public static final String ORDER_INSERTS = "hibernate.order_inserts";
+
+	/**
+	 * The EntityMode in which set the Session opened from the SessionFactory.
+	 */
+    public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
+
+    /**
+     * The jacc context id of the deployment
+     */
+    public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
+
+	/**
+	 * Should all database identifiers be quoted.
+	 */
+	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
+
+	/**
+	 * Enable nullability checking.
+	 * Raises an exception if a property marked as not-null is null.
+	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
+	 * true otherwise.
+	 */
+	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
+
+
+	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
+
+	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
+
+	/**
+	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
+	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
+	 */
+	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
+
+	/**
+	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
+	 */
+	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
+
+	/**
+	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
+	 */
+	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
+
+	/**
+	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
+	 */
+	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
+
+	/**
+	 * Strategy for multi-tenancy.
+
+	 * @see org.hibernate.MultiTenancyStrategy
+	 * @since 4.0
+	 */
+	public static final String MULTI_TENANT = "hibernate.multiTenancy";
+
+	/**
+	 * Names the {@link ClassLoader} used to load user application classes.
+	 * @since 4.0
+	 */
+	public static final String APP_CLASSLOADER = "hibernate.classLoader.application";
+
+	/**
+	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
+	 * @since 4.0
+	 */
+	public static final String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
+
+	/**
+	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
+	 * the {@link ClassLoader} that loaded this class.
+	 * @since 4.0
+	 */
+	public static final String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
+
+	/**
+	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
+	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
+	 * @since 4.0
+	 */
+	public static final String ENVIRONMENT_CLASSLOADER = "hibernate.classLoader.environment";
+
+
+	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
+
+	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
+
+
+	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
+	public static final String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
+	public static final String JMX_AGENT_ID = "hibernate.jmx.agentId";
+	public static final String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
+	public static final String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
+	public static final String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
+
+	/**
+	 * A configuration value key used to indicate that it is safe to cache
+	 * {@link javax.transaction.TransactionManager} references.
+	 * @since 4.0
+	 */
+	public static final String JTA_CACHE_TM = "hibernate.jta.cacheTransactionManager";
+
+	/**
+	 * A configuration value key used to indicate that it is safe to cache
+	 * {@link javax.transaction.UserTransaction} references.
+	 * @since 4.0
+	 */
+	public static final String JTA_CACHE_UT = "hibernate.jta.cacheUserTransaction";
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index d9e7e1ce1f..fab6abc548 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,743 +1,361 @@
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
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Version;
 import org.hibernate.bytecode.spi.BytecodeProvider;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
-import org.jboss.logging.Logger;
-
 
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
  * <tt>Configuration.buildSessionFactory()</tt><br>
  * <br>
  * <table>
  * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
  * <tr>
  *   <td><tt>hibernate.dialect</tt></td>
  *   <td>classname of <tt>org.hibernate.dialect.Dialect</tt> subclass</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.cache.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.cache.CacheProvider</tt>
  *   subclass (if not specified EHCache is used)</td>
  * </tr>
  * <tr>
  *   <td><tt>hibernate.connection.provider_class</tt></td>
  *   <td>classname of <tt>org.hibernate.service.jdbc.connections.spi.ConnectionProvider</tt>
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
-public final class Environment {
-	/**
-	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
-	 */
-	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
-	/**
-	 * JDBC driver class
-	 */
-	public static final String DRIVER ="hibernate.connection.driver_class";
-	/**
-	 * JDBC transaction isolation level
-	 */
-	public static final String ISOLATION ="hibernate.connection.isolation";
-	/**
-	 * JDBC URL
-	 */
-	public static final String URL ="hibernate.connection.url";
-	/**
-	 * JDBC user
-	 */
-	public static final String USER ="hibernate.connection.username";
-	/**
-	 * JDBC password
-	 */
-	public static final String PASS ="hibernate.connection.password";
-	/**
-	 * JDBC autocommit mode
-	 */
-	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
-	/**
-	 * Maximum number of inactive connections for Hibernate's connection pool
-	 */
-	public static final String POOL_SIZE ="hibernate.connection.pool_size";
-	/**
-	 * <tt>java.sql.Datasource</tt> JNDI name
-	 */
-	public static final String DATASOURCE ="hibernate.connection.datasource";
-	/**
-	 * prefix for arbitrary JDBC connection properties
-	 */
-	public static final String CONNECTION_PREFIX = "hibernate.connection";
-
-	/**
-	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
-	 */
-	public static final String JNDI_CLASS ="hibernate.jndi.class";
-	/**
-	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
-	 */
-	public static final String JNDI_URL ="hibernate.jndi.url";
-	/**
-	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
-	 */
-	public static final String JNDI_PREFIX = "hibernate.jndi";
-	/**
-	 * JNDI name to bind to <tt>SessionFactory</tt>
-	 */
-	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
-
-	/**
-	 * Hibernate SQL {@link org.hibernate.dialect.Dialect} class
-	 */
-	public static final String DIALECT ="hibernate.dialect";
-
-	/**
-	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} classes to register with the
-	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}
-	 */
-	public static final String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
-
-	/**
-	 * A default database schema (owner) name to use for unqualified tablenames
-	 */
-	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
-	/**
-	 * A default database catalog name to use for unqualified tablenames
-	 */
-	public static final String DEFAULT_CATALOG = "hibernate.default_catalog";
-
-	/**
-	 * Enable logging of generated SQL to the console
-	 */
-	public static final String SHOW_SQL ="hibernate.show_sql";
-	/**
-	 * Enable formatting of SQL logged to the console
-	 */
-	public static final String FORMAT_SQL ="hibernate.format_sql";
-	/**
-	 * Add comments to the generated SQL
-	 */
-	public static final String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
-	/**
-	 * Maximum depth of outer join fetching
-	 */
-	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
-	/**
-	 * The default batch size for batch fetching
-	 */
-	public static final String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
-	/**
-	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
-	 */
-	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
-	/**
-	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
-	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
-	 */
-	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
-	/**
-	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
-	 * method. In general, performance will be better if this property is set to true and the underlying
-	 * JDBC driver supports getGeneratedKeys().
-	 */
-	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
-	/**
-	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
-	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
-	 */
-	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
-	/**
-	 * Maximum JDBC batch size. A nonzero value enables batch updates.
-	 */
-	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
-	/**
-	 * Select a custom batcher.
-	 */
-	public static final String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
-	/**
-	 * Should versioned data be included in batching?
-	 */
-	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
-	/**
-	 * An XSLT resource used to generate "custom" XML
-	 */
-	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
-
-	/**
-	 * Maximum size of C3P0 connection pool
-	 */
-	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
-	/**
-	 * Minimum size of C3P0 connection pool
-	 */
-	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
-
-	/**
-	 * Maximum idle time for C3P0 connection pool
-	 */
-	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
-	/**
-	 * Maximum size of C3P0 statement cache
-	 */
-	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
-	/**
-	 * Number of connections acquired when pool is exhausted
-	 */
-	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
-	/**
-	 * Idle time before a C3P0 pooled connection is validated
-	 */
-	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
-
-	/**
-	 * Proxool/Hibernate property prefix
-	 */
-	public static final String PROXOOL_PREFIX = "hibernate.proxool";
-	/**
-	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
-	 */
-	public static final String PROXOOL_XML = "hibernate.proxool.xml";
-	/**
-	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
-	 */
-	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
-	/**
-	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
-	 */
-	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
-	/**
-	 * Proxool property with the Proxool pool alias to use
-	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
-	 * <tt>PROXOOL_XML</tt>)
-	 */
-	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
-
-	/**
-	 * Enable automatic session close at end of transaction
-	 */
-	public static final String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
-	/**
-	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
-	 */
-	public static final String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
-	/**
-	 * Specifies how Hibernate should release JDBC connections.
-	 */
-	public static final String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
-	/**
-	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
-	 */
-	public static final String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
-	/**
-	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionContext} to use for
-	 * creating {@link org.hibernate.Transaction} instances
-	 */
-	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
-	/**
-	 * <tt>TransactionManagerLookup</tt> implementor to use for obtaining the <tt>TransactionManager</tt>
-	 */
-	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
-	/**
-	 * JNDI name of JTA <tt>UserTransaction</tt> object
-	 */
-	public static final String USER_TRANSACTION = "jta.UserTransaction";
-
-	/**
-	 * The <tt>CacheProvider</tt> implementation class
-	 */
-	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
-
-	/**
-	 * The {@link org.hibernate.cache.RegionFactory} implementation class
-	 */
-	public static final String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
-
-	/**
-	 * The <tt>CacheProvider</tt> implementation class
-	 */
-	public static final String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
-	/**
-	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
-	 */
-	public static final String CACHE_NAMESPACE = "hibernate.cache.jndi";
-	/**
-	 * Enable the query cache (disabled by default)
-	 */
-	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
-	/**
-	 * The <tt>QueryCacheFactory</tt> implementation class.
-	 */
-	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
-	/**
-	 * Enable the second-level cache (enabled by default)
-	 */
-	public static final String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
-	/**
-	 * Optimize the cache for minimal puts instead of minimal gets
-	 */
-	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
-	/**
-	 * The <tt>CacheProvider</tt> region name prefix
-	 */
-	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
-	/**
-	 * Enable use of structured second-level cache entries
-	 */
-	public static final String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
-
-	/**
-	 * Enable statistics collection
-	 */
-	public static final String GENERATE_STATISTICS = "hibernate.generate_statistics";
-
-	public static final String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
-
-	/**
-	 * Use bytecode libraries optimized property access
-	 */
-	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
-
-	/**
-	 * The classname of the HQL query parser factory
-	 */
-	public static final String QUERY_TRANSLATOR = "hibernate.query.factory_class";
-
-	/**
-	 * A comma-separated list of token substitutions to use when translating a Hibernate
-	 * query to SQL
-	 */
-	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
-
-	/**
-	 * Should named queries be checked during startup (the default is enabled).
-	 * <p/>
-	 * Mainly intended for test environments.
-	 */
-	public static final String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
-
-	/**
-	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
-	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
-	 */
-	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
-
-	/**
-	 * Comma-separated names of the optional files containing SQL DML statements executed
-	 * during the SessionFactory creation.
-	 * File order matters, the statements of a give file are executed before the statements of the
-	 * following files.
-	 *
-	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
-	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
-	 *
-	 * The default value is <tt>/import.sql</tt>
-	 */
-	public static final String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
-
-	/**
-	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
-	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
-	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
-	 */
-	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
-
-	/**
-	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
-	 * broken JDBC drivers
-	 */
-	public static final String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
-
-	/**
-	 * Enable ordering of update statements by primary key value
-	 */
-	public static final String ORDER_UPDATES = "hibernate.order_updates";
-
-	/**
-	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
-	 */
-	public static final String ORDER_INSERTS = "hibernate.order_inserts";
-
-	/**
-	 * The EntityMode in which set the Session opened from the SessionFactory.
-	 */
-    public static final String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
-
-    /**
-     * The jacc context id of the deployment
-     */
-    public static final String JACC_CONTEXTID = "hibernate.jacc_context_id";
-
-	/**
-	 * Should all database identifiers be quoted.
-	 */
-	public static final String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
-
-	/**
-	 * Enable nullability checking.
-	 * Raises an exception if a property marked as not-null is null.
-	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
-	 * true otherwise.
-	 */
-	public static final String CHECK_NULLABILITY = "hibernate.check_nullability";
-
-
-	public static final String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
-
-	public static final String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
-
-	/**
-	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
-	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
-	 */
-	public static final String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
-
-	/**
-	 * The maximum number of strong references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 128.
-	 */
-	public static final String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
-
-	/**
-	 * The maximum number of soft references maintained by {@link org.hibernate.internal.util.collections.SoftLimitMRUCache}. Default is 2048.
-	 */
-	public static final String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
-
-	/**
-	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
-	 */
-	public static final String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
-
-	/**
-	 * Strategy for multi-tenancy.
-	 * @see org.hibernate.MultiTenancyStrategy
-	 */
-	public static final String MULTI_TENANT = "hibernate.multiTenancy";
+public final class Environment implements AvailableSettings {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Environment.class.getName());
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
+
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Environment.class.getName());
-
 	/**
 	 * Issues warnings to the user when any obsolete or renamed property names are used.
 	 *
 	 * @param props The specified properties.
 	 */
 	public static void verifyProperties(Properties props) {
 		Iterator iter = props.keySet().iterator();
 		Map propertiesToAdd = new HashMap();
 		while ( iter.hasNext() ) {
 			final Object propertyName = iter.next();
 			Object newPropertyName = OBSOLETE_PROPERTIES.get( propertyName );
             if (newPropertyName != null) LOG.unsupportedProperty(propertyName, newPropertyName);
 			newPropertyName = RENAMED_PROPERTIES.get( propertyName );
 			if ( newPropertyName != null ) {
                 LOG.renamedProperty(propertyName, newPropertyName);
 				if ( ! props.containsKey( newPropertyName ) ) {
 					propertiesToAdd.put( newPropertyName, props.get( propertyName ) );
 				}
 			}
 		}
 		props.putAll(propertiesToAdd);
 	}
 
 	static {
 
         LOG.version(Version.getVersionString());
 
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_NONE), "NONE" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_UNCOMMITTED), "READ_UNCOMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_COMMITTED), "READ_COMMITTED" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_REPEATABLE_READ), "REPEATABLE_READ" );
 		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_SERIALIZABLE), "SERIALIZABLE" );
 
 		GLOBAL_PROPERTIES = new Properties();
 		//Set USE_REFLECTION_OPTIMIZER to false to fix HHH-227
 		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.FALSE.toString() );
 
 		try {
 			InputStream stream = ConfigHelper.getResourceAsStream( "/hibernate.properties" );
 			try {
 				GLOBAL_PROPERTIES.load(stream);
                 LOG.propertiesLoaded(ConfigurationHelper.maskOut(GLOBAL_PROPERTIES, PASS));
 			}
 			catch (Exception e) {
                 LOG.unableToLoadProperties();
 			}
 			finally {
 				try{
 					stream.close();
 				}
 				catch (IOException ioe){
                     LOG.unableToCloseStreamError(ioe);
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
         if (ENABLE_BINARY_STREAMS) {
 			LOG.usingStreams();
 		}
 
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
         if (ENABLE_REFLECTION_OPTIMIZER) {
 			LOG.usingReflectionOptimizer();
 		}
 
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
         if (JVM_HAS_TIMESTAMP_BUG) {
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
 		return (String) ISOLATION_LEVELS.get( new Integer(isolation) );
 	}
 
 	public static BytecodeProvider buildBytecodeProvider(Properties properties) {
 		String provider = ConfigurationHelper.getString( BYTECODE_PROVIDER, properties, "javassist" );
         LOG.bytecodeProvider(provider);
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 37786ccaf5..0453f8dd93 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,365 +1,410 @@
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
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
-import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.ServiceRegistry;
-
-import org.jboss.logging.Logger;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	protected SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
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
         LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
         LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.debugf( "Connection release mode: %s", releaseModeName );
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
         if ( defaultSchema != null ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
         if (defaultCatalog != null) {
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
         LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
         LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
         LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
-		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
+		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
         Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
         LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
         LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
-			settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
+			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
-		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
+		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
         if (prefix != null) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
         LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
         LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
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
         LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
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
 //            LOG.debugf("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
-	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
+	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
         LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
-			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
+			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
+					.classForName( queryCacheFactoryClassName )
+					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
-	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
+	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null && cachingEnabled ) {
 			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
 			if ( providerClassName != null ) {
 				// legacy behavior, apply the bridge...
 				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
 			}
 		}
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
-				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
+				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
+						.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
-				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
+				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
+						.classForName( regionFactoryClassName )
+						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
-	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
+	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
         LOG.debugf( "Query translator: %s", className );
 		try {
-			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
+			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
+					.classForName( className )
+					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
+
+	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
+		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
+		String regionFactoryClassName = ConfigurationHelper.getString(
+				Environment.CACHE_REGION_FACTORY, properties, null
+		);
+		if ( regionFactoryClassName == null && cachingEnabled ) {
+			String providerClassName = ConfigurationHelper.getString( Environment.CACHE_PROVIDER, properties, null );
+			if ( providerClassName != null ) {
+				// legacy behavior, apply the bridge...
+				regionFactoryClassName = RegionFactoryCacheProviderBridge.class.getName();
+			}
+		}
+		if ( regionFactoryClassName == null ) {
+			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
+		}
+        LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
+		try {
+			try {
+				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
+						.getConstructor( Properties.class )
+						.newInstance( properties );
+			}
+			catch ( NoSuchMethodException e ) {
+				// no constructor accepting Properties found, try no arg constructor
+                LOG.debugf(
+						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
+						regionFactoryClassName
+				);
+				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
+						.newInstance();
+			}
+		}
+		catch ( Exception e ) {
+			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
index 5bc8e0285a..22372dcb94 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
@@ -1,198 +1,198 @@
 //$Id: HibernateService.java 6100 2005-03-17 10:48:03Z turin42 $
 package org.hibernate.jmx;
 
 import javax.naming.InitialContext;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ExternalSessionFactoryConfig;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.jndi.JndiHelper;
 import org.hibernate.service.ServiceRegistryBuilder;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 
 
 /**
  * Implementation of <tt>HibernateServiceMBean</tt>. Creates a
  * <tt>SessionFactory</tt> and binds it to the specified JNDI name.<br>
  * <br>
  * All mapping documents are loaded as resources by the MBean.
  * @see HibernateServiceMBean
  * @see org.hibernate.SessionFactory
  * @author John Urberg, Gavin King
  */
 public class HibernateService extends ExternalSessionFactoryConfig implements HibernateServiceMBean {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HibernateService.class.getName());
 
 	private String boundName;
 	private Properties properties = new Properties();
 
 	@Override
 	public void start() throws HibernateException {
 		boundName = getJndiName();
 		try {
 			buildSessionFactory();
 		}
 		catch (HibernateException he) {
             LOG.unableToBuildSessionFactoryUsingMBeanClasspath(he.getMessage());
             LOG.debug("Error was", he);
 			new SessionFactoryStub(this);
 		}
 	}
 
 	@Override
 	public void stop() {
         LOG.stoppingService();
 		try {
 			InitialContext context = JndiHelper.getInitialContext( buildProperties() );
 			( (SessionFactory) context.lookup(boundName) ).close();
 			//context.unbind(boundName);
 		}
 		catch (Exception e) {
             LOG.unableToStopHibernateService(e);
 		}
 	}
 
 	SessionFactory buildSessionFactory() throws HibernateException {
         LOG.startingServiceAtJndiName( boundName );
         LOG.serviceProperties( properties );
         return buildConfiguration().buildSessionFactory(
 				new ServiceRegistryBuilder( properties ).buildServiceRegistry()
 		);
 	}
 
 	@Override
 	protected Map getExtraProperties() {
 		return properties;
 	}
 
 	@Override
 	public String getTransactionStrategy() {
 		return getProperty(Environment.TRANSACTION_STRATEGY);
 	}
 
 	@Override
 	public void setTransactionStrategy(String txnStrategy) {
 		setProperty(Environment.TRANSACTION_STRATEGY, txnStrategy);
 	}
 
 	@Override
 	public String getUserTransactionName() {
 		return getProperty(Environment.USER_TRANSACTION);
 	}
 
 	@Override
 	public void setUserTransactionName(String utName) {
 		setProperty(Environment.USER_TRANSACTION, utName);
 	}
 
 	@Override
 	public String getJtaPlatformName() {
-		return getProperty( JtaPlatformInitiator.JTA_PLATFORM );
+		return getProperty( AvailableSettings.JTA_PLATFORM );
 	}
 
 	@Override
 	public void setJtaPlatformName(String name) {
-		setProperty( JtaPlatformInitiator.JTA_PLATFORM, name );
+		setProperty( AvailableSettings.JTA_PLATFORM, name );
 	}
 
 	@Override
 	public String getPropertyList() {
 		return buildProperties().toString();
 	}
 
 	@Override
 	public String getProperty(String property) {
 		return properties.getProperty(property);
 	}
 
 	@Override
 	public void setProperty(String property, String value) {
 		properties.setProperty(property, value);
 	}
 
 	@Override
 	public void dropSchema() {
 		new SchemaExport( buildConfiguration() ).drop(false, true);
 	}
 
 	@Override
 	public void createSchema() {
 		new SchemaExport( buildConfiguration() ).create(false, true);
 	}
 
 	public String getName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public String getDatasource() {
 		return getProperty(Environment.DATASOURCE);
 	}
 
 	@Override
 	public void setDatasource(String datasource) {
 		setProperty(Environment.DATASOURCE, datasource);
 	}
 
 	@Override
 	public String getJndiName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public void setJndiName(String jndiName) {
 		setProperty(Environment.SESSION_FACTORY_NAME, jndiName);
 	}
 
 	@Override
 	public String getUserName() {
 		return getProperty(Environment.USER);
 	}
 
 	@Override
 	public void setUserName(String userName) {
 		setProperty(Environment.USER, userName);
 	}
 
 	@Override
 	public String getPassword() {
 		return getProperty(Environment.PASS);
 	}
 
 	@Override
 	public void setPassword(String password) {
 		setProperty(Environment.PASS, password);
 	}
 
 	@Override
 	public void setFlushBeforeCompletionEnabled(String enabled) {
 		setProperty(Environment.FLUSH_BEFORE_COMPLETION, enabled);
 	}
 
 	@Override
 	public String getFlushBeforeCompletionEnabled() {
 		return getProperty(Environment.FLUSH_BEFORE_COMPLETION);
 	}
 
 	@Override
 	public void setAutoCloseSessionEnabled(String enabled) {
 		setProperty(Environment.AUTO_CLOSE_SESSION, enabled);
 	}
 
 	@Override
 	public String getAutoCloseSessionEnabled() {
 		return getProperty(Environment.AUTO_CLOSE_SESSION);
 	}
 
 	public Properties getProperties() {
 		return buildProperties();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
index 0bf0195e02..5361a63e5c 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
@@ -1,192 +1,188 @@
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
 package org.hibernate.service.classloading.internal;
 
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * Standard implementation of the service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceImpl implements ClassLoaderService {
-	public static final String APP_CL = "hibernate.classLoader.application";
-	public static final String RESOURCES_CL = "hibernate.classLoader.resources";
-	public static final String HIB_CL = "hibernate.classLoader.hibernate";
-	public static final String ENV_CL = "hibernate.classLoader.environment";
-
 	private final LinkedHashSet<ClassLoader> classLoadingClassLoaders;
 	private final ClassLoader resourcesClassLoader;
 
 	public ClassLoaderServiceImpl(Map configVales) {
 		this( determineClassLoaders( configVales ) );
 	}
 
 	private ClassLoaderServiceImpl(ClassLoader... classLoaders) {
 		this( classLoaders[0], classLoaders[1], classLoaders[2], classLoaders[3] );
 	}
 
 	private static ClassLoader[] determineClassLoaders(Map configVales) {
-		ClassLoader applicationClassLoader = (ClassLoader) configVales.get( APP_CL );
-		ClassLoader resourcesClassLoader = (ClassLoader) configVales.get( RESOURCES_CL );
-		ClassLoader hibernateClassLoader = (ClassLoader) configVales.get( HIB_CL );
-		ClassLoader environmentClassLoader = (ClassLoader) configVales.get( ENV_CL );
+		ClassLoader applicationClassLoader = (ClassLoader) configVales.get( AvailableSettings.APP_CLASSLOADER );
+		ClassLoader resourcesClassLoader = (ClassLoader) configVales.get( AvailableSettings.RESOURCES_CLASSLOADER );
+		ClassLoader hibernateClassLoader = (ClassLoader) configVales.get( AvailableSettings.HIBERNATE_CLASSLOADER );
+		ClassLoader environmentClassLoader = (ClassLoader) configVales.get( AvailableSettings.ENVIRONMENT_CLASSLOADER );
 
 		if ( hibernateClassLoader == null ) {
 			hibernateClassLoader = ClassLoaderServiceImpl.class.getClassLoader();
 		}
 
 		if ( environmentClassLoader == null || applicationClassLoader == null ) {
 			ClassLoader sysClassLoader = locateSystemClassLoader();
 			ClassLoader tccl = locateTCCL();
 			if ( environmentClassLoader == null ) {
 				environmentClassLoader = sysClassLoader != null ? sysClassLoader : hibernateClassLoader;
 			}
 			if ( applicationClassLoader == null ) {
 				applicationClassLoader = tccl != null ? tccl : hibernateClassLoader;
 			}
 		}
 
 		if ( resourcesClassLoader == null ) {
 			resourcesClassLoader = applicationClassLoader;
 		}
 
 		return new ClassLoader[] {
 			applicationClassLoader,
 			resourcesClassLoader,
 			hibernateClassLoader,
 			environmentClassLoader
 		};
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
 
 	public ClassLoaderServiceImpl(ClassLoader classLoader) {
 		this( classLoader, classLoader, classLoader, classLoader );
 	}
 
 	public ClassLoaderServiceImpl(
 			ClassLoader applicationClassLoader,
 			ClassLoader resourcesClassLoader,
 			ClassLoader hibernateClassLoader,
 			ClassLoader environmentClassLoader) {
 		this.classLoadingClassLoaders = new LinkedHashSet<ClassLoader>();
 		classLoadingClassLoaders.add( applicationClassLoader );
 		classLoadingClassLoaders.add( hibernateClassLoader );
 		classLoadingClassLoaders.add( environmentClassLoader );
 
 		this.resourcesClassLoader = resourcesClassLoader;
 	}
 
 	@Override
 	public Class classForName(String className) {
 		for ( ClassLoader classLoader : classLoadingClassLoaders ) {
 			try {
 				return classLoader.loadClass( className );
 			}
 			catch ( Exception ignore) {
 			}
 		}
 		throw new ClassLoadingException( "Unable to load class [" + className + "]" );
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
 			return resourcesClassLoader.getResource( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public InputStream locateResourceStream(String name) {
 		// first we try name as a URL
 		try {
 			return new URL( name ).openStream();
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			return resourcesClassLoader.getResourceAsStream( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public List<URL> locateResources(String name) {
 		ArrayList<URL> urls = new ArrayList<URL>();
 		try {
 			Enumeration<URL> urlEnumeration = resourcesClassLoader.getResources( name );
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
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
index abd25bce8c..27b4c8588f 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
@@ -1,75 +1,76 @@
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
 package org.hibernate.service.classloading.spi;
 
 import java.io.InputStream;
 import java.net.URL;
 import java.util.List;
 
 import org.hibernate.service.Service;
 
 /**
  * A service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public interface ClassLoaderService extends Service {
+
 	/**
 	 * Locate a class by name
 	 *
 	 * @param className The name of the class to locate
 	 *
 	 * @return The class reference
 	 *
 	 * @throws ClassLoadingException Indicates the class could not be found
 	 */
 	public Class classForName(String className);
 
 	/**
 	 * Locate a resource by name (classpath lookup)
 	 *
 	 * @param name The resource name.
 	 *
 	 * @return The located URL; may return {@code null} to indicate the resource was not found
 	 */
 	public URL locateResource(String name);
 
 	/**
 	 * Locate a resource by name (classpath lookup) and gets its stream
 	 *
 	 * @param name The resource name.
 	 *
 	 * @return The stream of the located resource; may return {@code null} to indicate the resource was not found
 	 */
 	public InputStream locateResourceStream(String name);
 
 	/**
 	 * Locate a series of resource by name (classpath lookup)
 	 *
 	 * @param name The resource name.
 	 *
 	 * @return The list of URL matching; may return {@code null} to indicate the resource was not found
 	 */
 	public List<URL> locateResources(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
index 620fe1f104..b378769605 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
@@ -1,285 +1,284 @@
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
 package org.hibernate.service.jdbc.connections.internal;
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Instantiates and configures an appropriate {@link ConnectionProvider}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class ConnectionProviderInitiator implements BasicServiceInitiator<ConnectionProvider> {
 	public static final ConnectionProviderInitiator INSTANCE = new ConnectionProviderInitiator();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        ConnectionProviderInitiator.class.getName());
-	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 	public static final String C3P0_PROVIDER_CLASS_NAME =
 			"org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider";
 
-	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 	public static final String PROXOOL_PROVIDER_CLASS_NAME =
 			"org.hibernate.service.jdbc.connections.internal.ProxoolConnectionProvider";
 
 	public static final String INJECTION_DATA = "hibernate.connection_provider.injection_data";
 
 	// mapping from legacy connection provider name to actual
 	// connection provider that will be used
 	private static final Map<String,String> LEGACY_CONNECTION_PROVIDER_MAPPING;
 
 	static {
 		LEGACY_CONNECTION_PROVIDER_MAPPING = new HashMap<String,String>( 5 );
 
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.DatasourceConnectionProvider",
 				DatasourceConnectionProviderImpl.class.getName()
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.DriverManagerConnectionProvider",
 				DriverManagerConnectionProviderImpl.class.getName()
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.UserSuppliedConnectionProvider",
 				UserSuppliedConnectionProviderImpl.class.getName()
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.C3P0ConnectionProvider",
 				C3P0_PROVIDER_CLASS_NAME
 		);
 		LEGACY_CONNECTION_PROVIDER_MAPPING.put(
 				"org.hibernate.connection.ProxoolConnectionProvider",
 				PROXOOL_PROVIDER_CLASS_NAME
 		);
 	}
 
 	@Override
 	public Class<ConnectionProvider> getServiceInitiated() {
 		return ConnectionProvider.class;
 	}
 
 	@Override
 	public ConnectionProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		if ( MultiTenancyStrategy.determineMultiTenancyStrategy( configurationValues ) != MultiTenancyStrategy.NONE ) {
 			// nothing to do, but given the separate hierarchies have to handle this here.
 		}
 
 		final ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 
 		ConnectionProvider connectionProvider = null;
 		String providerClassName = getConfiguredConnectionProviderName( configurationValues );
 		if ( providerClassName != null ) {
 			connectionProvider = instantiateExplicitConnectionProvider( providerClassName, classLoaderService );
 		}
 		else if ( configurationValues.get( Environment.DATASOURCE ) != null ) {
 			connectionProvider = new DatasourceConnectionProviderImpl();
 		}
 
 		if ( connectionProvider == null ) {
 			if ( c3p0ConfigDefined( configurationValues ) && c3p0ProviderPresent( classLoaderService ) ) {
 				connectionProvider = instantiateExplicitConnectionProvider( C3P0_PROVIDER_CLASS_NAME,
 						classLoaderService
 				);
 			}
 		}
 
 		if ( connectionProvider == null ) {
 			if ( proxoolConfigDefined( configurationValues ) && proxoolProviderPresent( classLoaderService ) ) {
 				connectionProvider = instantiateExplicitConnectionProvider( PROXOOL_PROVIDER_CLASS_NAME,
 						classLoaderService
 				);
 			}
 		}
 
 		if ( connectionProvider == null ) {
 			if ( configurationValues.get( Environment.URL ) != null ) {
 				connectionProvider = new DriverManagerConnectionProviderImpl();
 			}
 		}
 
 		if ( connectionProvider == null ) {
             LOG.noAppropriateConnectionProvider();
 			connectionProvider = new UserSuppliedConnectionProviderImpl();
 		}
 
 
 		final Map injectionData = (Map) configurationValues.get( INJECTION_DATA );
 		if ( injectionData != null && injectionData.size() > 0 ) {
 			final ConnectionProvider theConnectionProvider = connectionProvider;
 			new BeanInfoHelper( connectionProvider.getClass() ).applyToBeanInfo(
 					connectionProvider,
 					new BeanInfoHelper.BeanInfoDelegate() {
 						public void processBeanInfo(BeanInfo beanInfo) throws Exception {
 							PropertyDescriptor[] descritors = beanInfo.getPropertyDescriptors();
 							for ( int i = 0, size = descritors.length; i < size; i++ ) {
 								String propertyName = descritors[i].getName();
 								if ( injectionData.containsKey( propertyName ) ) {
 									Method method = descritors[i].getWriteMethod();
 									method.invoke(
 											theConnectionProvider,
 											injectionData.get( propertyName )
 									);
 								}
 							}
 						}
 					}
 			);
 		}
 
 		return connectionProvider;
 	}
 
 	private String getConfiguredConnectionProviderName( Map configurationValues ) {
 		String providerClassName = ( String ) configurationValues.get( Environment.CONNECTION_PROVIDER );
 		if ( LEGACY_CONNECTION_PROVIDER_MAPPING.containsKey( providerClassName ) ) {
 			String actualProviderClassName = LEGACY_CONNECTION_PROVIDER_MAPPING.get( providerClassName );
             LOG.providerClassDeprecated(providerClassName, actualProviderClassName);
 			providerClassName = actualProviderClassName;
 		}
 		return providerClassName;
 	}
 
 	private ConnectionProvider instantiateExplicitConnectionProvider(
 			String providerClassName,
 			ClassLoaderService classLoaderService) {
 		try {
             LOG.instantiatingExplicitConnectionProvider( providerClassName );
 			return (ConnectionProvider) classLoaderService.classForName( providerClassName ).newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate connection provider [" + providerClassName + "]", e );
 		}
 	}
 
 	private boolean c3p0ProviderPresent(ClassLoaderService classLoaderService) {
 		try {
 			classLoaderService.classForName( C3P0_PROVIDER_CLASS_NAME );
 		}
 		catch ( Exception e ) {
             LOG.c3p0ProviderClassNotFound(C3P0_PROVIDER_CLASS_NAME);
 			return false;
 		}
 		return true;
 	}
 
 	private static boolean c3p0ConfigDefined(Map configValues) {
 		for ( Object key : configValues.keySet() ) {
 			if ( String.class.isInstance( key )
-					&& ( (String) key ).startsWith( C3P0_CONFIG_PREFIX ) ) {
+					&& ( (String) key ).startsWith( AvailableSettings.C3P0_CONFIG_PREFIX ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean proxoolProviderPresent(ClassLoaderService classLoaderService) {
 		try {
 			classLoaderService.classForName( PROXOOL_PROVIDER_CLASS_NAME );
 		}
 		catch ( Exception e ) {
             LOG.proxoolProviderClassNotFound(PROXOOL_PROVIDER_CLASS_NAME);
 			return false;
 		}
 		return true;
 	}
 
 	private static boolean proxoolConfigDefined(Map configValues) {
 		for ( Object key : configValues.keySet() ) {
 			if ( String.class.isInstance( key )
-					&& ( (String) key ).startsWith( PROXOOL_CONFIG_PREFIX ) ) {
+					&& ( (String) key ).startsWith( AvailableSettings.PROXOOL_CONFIG_PREFIX ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Build the connection properties capable of being passed to the {@link java.sql.DriverManager#getConnection}
 	 * forms taking {@link Properties} argument.  We seek out all keys in the passed map which start with
 	 * {@code hibernate.connection.}, using them to create a new {@link Properties} instance.  The keys in this
 	 * new {@link Properties} have the {@code hibernate.connection.} prefix trimmed.
 	 *
 	 * @param properties The map from which to build the connection specific properties.
 	 *
 	 * @return The connection properties.
 	 */
 	public static Properties getConnectionProperties(Map<?,?> properties) {
 		Properties result = new Properties();
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( ! ( String.class.isInstance( entry.getKey() ) ) || ! String.class.isInstance( entry.getValue() ) ) {
 				continue;
 			}
 			final String key = (String) entry.getKey();
 			final String value = (String) entry.getValue();
 			if ( key.startsWith( Environment.CONNECTION_PREFIX ) ) {
 				if ( SPECIAL_PROPERTIES.contains( key ) ) {
 					if ( Environment.USER.equals( key ) ) {
 						result.setProperty( "user", value );
 					}
 				}
 				else {
 					result.setProperty(
 							key.substring( Environment.CONNECTION_PREFIX.length() + 1 ),
 							value
 					);
 				}
 			}
 		}
 		return result;
 	}
 
 	private static final Set<String> SPECIAL_PROPERTIES;
 
 	static {
 		SPECIAL_PROPERTIES = new HashSet<String>();
 		SPECIAL_PROPERTIES.add( Environment.DATASOURCE );
 		SPECIAL_PROPERTIES.add( Environment.URL );
 		SPECIAL_PROPERTIES.add( Environment.CONNECTION_PROVIDER );
 		SPECIAL_PROPERTIES.add( Environment.POOL_SIZE );
 		SPECIAL_PROPERTIES.add( Environment.ISOLATION );
 		SPECIAL_PROPERTIES.add( Environment.DRIVER );
 		SPECIAL_PROPERTIES.add( Environment.USER );
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/AbstractDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/AbstractDialectResolver.java
index 6b20d99dc2..4eab076eed 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/AbstractDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/AbstractDialectResolver.java
@@ -1,78 +1,79 @@
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
 package org.hibernate.service.jdbc.dialect.internal;
+
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
 
-import org.hibernate.internal.CoreMessageLogger;
+import org.jboss.logging.Logger;
+
 import org.hibernate.JDBCException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.resolver.BasicSQLExceptionConverter;
 import org.hibernate.exception.JDBCConnectionException;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
 
-import org.jboss.logging.Logger;
-
 /**
  * A templated resolver impl which delegates to the {@link #resolveDialectInternal} method
  * and handles any thrown {@link SQLException SQL errors}.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractDialectResolver implements DialectResolver {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractDialectResolver.class.getName());
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Here we template the resolution, delegating to {@link #resolveDialectInternal} and handling
 	 * {@link java.sql.SQLException}s properly.
 	 */
 	public final Dialect resolveDialect(DatabaseMetaData metaData) {
 		try {
 			return resolveDialectInternal( metaData );
 		}
 		catch ( SQLException sqlException ) {
 			JDBCException jdbcException = BasicSQLExceptionConverter.INSTANCE.convert( sqlException );
             if (jdbcException instanceof JDBCConnectionException) throw jdbcException;
             LOG.warnf("%s : %s", BasicSQLExceptionConverter.MSG, sqlException.getMessage());
             return null;
 		}
 		catch ( Throwable t ) {
             LOG.unableToExecuteResolver(this, t.getMessage());
 			return null;
 		}
 	}
 
 	/**
 	 * Perform the actual resolution without caring about handling {@link SQLException}s.
 	 *
 	 * @param metaData The database metadata
 	 * @return The resolved dialect, or null if we could not resolve.
 	 * @throws SQLException Indicates problems accessing the metadata.
 	 */
 	protected abstract Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java
index b2c0396f63..0de006258e 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/BasicDialectResolver.java
@@ -1,77 +1,79 @@
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
 package org.hibernate.service.jdbc.dialect.internal;
+
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
+
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 
 /**
  * Intended as support for custom resolvers.
  *
  * @author Steve Ebersole
  */
 public class BasicDialectResolver extends AbstractDialectResolver {
 	// TODO: should this disappear???
 
 	public static final int VERSION_INSENSITIVE_VERSION = -9999;
 
 	private final String matchingName;
 	private final int matchingVersion;
 	private final Class dialectClass;
 
 	public BasicDialectResolver(String matchingName, Class dialectClass) {
 		this( matchingName, VERSION_INSENSITIVE_VERSION, dialectClass );
 	}
 
 	public BasicDialectResolver(String matchingName, int matchingVersion, Class dialectClass) {
 		this.matchingName = matchingName;
 		this.matchingVersion = matchingVersion;
 		this.dialectClass = dialectClass;
 	}
 
 	protected final Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 		final String databaseName = metaData.getDatabaseProductName();
 		final int databaseMajorVersion = metaData.getDatabaseMajorVersion();
 
 		if ( matchingName.equalsIgnoreCase( databaseName )
 				&& ( matchingVersion == VERSION_INSENSITIVE_VERSION || matchingVersion == databaseMajorVersion ) ) {
 			try {
 				return ( Dialect ) dialectClass.newInstance();
 			}
 			catch ( HibernateException e ) {
 				// conceivable that the dialect ctor could throw HibernateExceptions, so don't re-wrap
 				throw e;
 			}
 			catch ( Throwable t ) {
 				throw new HibernateException(
 						"Could not instantiate specified Dialect class [" + dialectClass.getName() + "]",
 						t
 				);
 			}
 		}
 
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryImpl.java
index 969f115e43..7c045603c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryImpl.java
@@ -1,121 +1,121 @@
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
 package org.hibernate.service.jdbc.dialect.internal;
+
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
 import java.util.Map;
+
 import org.hibernate.HibernateException;
-import org.hibernate.cfg.Environment;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
 import org.hibernate.service.spi.InjectService;
 
 /**
  * Standard implementation of the {@link DialectFactory} service.
  *
  * @author Steve Ebersole
  */
 public class DialectFactoryImpl implements DialectFactory {
 	private ClassLoaderService classLoaderService;
 
 	@InjectService
 	public void setClassLoaderService(ClassLoaderService classLoaderService) {
 		this.classLoaderService = classLoaderService;
 	}
 
 	private DialectResolver dialectResolver;
 
 	@InjectService
 	public void setDialectResolver(DialectResolver dialectResolver) {
 		this.dialectResolver = dialectResolver;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Dialect buildDialect(Map configValues, Connection connection) throws HibernateException {
-		final String dialectName = (String) configValues.get( Environment.DIALECT );
+		final String dialectName = (String) configValues.get( AvailableSettings.DIALECT );
 		if ( dialectName != null ) {
 			return constructDialect( dialectName );
 		}
 		else {
 			return determineDialect( connection );
 		}
 	}
 
 	private Dialect constructDialect(String dialectName) {
 		try {
 			return ( Dialect ) classLoaderService.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassLoadingException e ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName, e );
 		}
 		catch ( HibernateException e ) {
 			throw e;
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate dialect class", e );
 		}
 	}
 
 	/**
 	 * Determine the appropriate Dialect to use given the connection.
 	 *
 	 * @param connection The configured connection.
 	 * @return The appropriate dialect instance.
 	 *
 	 * @throws HibernateException No connection given or no resolver could make
 	 * the determination from the given connection.
 	 */
 	private Dialect determineDialect(Connection connection) {
 		if ( connection == null ) {
 			throw new HibernateException( "Connection cannot be null when 'hibernate.dialect' not set" );
 		}
 
 		try {
 			final DatabaseMetaData databaseMetaData = connection.getMetaData();
 			final Dialect dialect = dialectResolver.resolveDialect( databaseMetaData );
 
 			if ( dialect == null ) {
 				throw new HibernateException(
 						"Unable to determine Dialect to use [name=" + databaseMetaData.getDatabaseProductName() +
 								", majorVersion=" + databaseMetaData.getDatabaseMajorVersion() +
 								"]; user must register resolver or explicitly set 'hibernate.dialect'"
 				);
 			}
 
 			return dialect;
 		}
 		catch ( SQLException sqlException ) {
 			throw new HibernateException(
 					"Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use",
 					sqlException
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
index ef2ded0cf8..3405a7bda3 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
@@ -1,49 +1,80 @@
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
 package org.hibernate.service.jdbc.dialect.internal;
 
+import java.util.ArrayList;
+import java.util.List;
 import java.util.Map;
 
+import org.hibernate.HibernateException;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link DialectResolver} service
  *
  * @author Steve Ebersole
  */
 public class DialectResolverInitiator implements BasicServiceInitiator<DialectResolver> {
 	public static final DialectResolverInitiator INSTANCE = new DialectResolverInitiator();
 
 	@Override
 	public Class<DialectResolver> getServiceInitiated() {
 		return DialectResolver.class;
 	}
 
 	@Override
 	public DialectResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-		return new StandardDialectResolver();
+		return new DialectResolverSet( determineResolvers( configurationValues, registry ) );
+	}
+
+	private List<DialectResolver> determineResolvers(Map configurationValues, ServiceRegistryImplementor registry) {
+		final List<DialectResolver> resolvers = new ArrayList<DialectResolver>();
+
+		final String resolverImplNames = (String) configurationValues.get( AvailableSettings.DIALECT_RESOLVERS );
+
+		if ( StringHelper.isNotEmpty( resolverImplNames ) ) {
+			final ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
+			for ( String resolverImplName : StringHelper.split( ", \n\r\f\t", resolverImplNames ) ) {
+				try {
+					resolvers.add( (DialectResolver) classLoaderService.classForName( resolverImplName ).newInstance() );
+				}
+				catch (HibernateException e) {
+					throw e;
+				}
+				catch (Exception e) {
+					throw new ServiceException( "Unable to instantiate named dialect resolver [" + resolverImplName + "]", e );
+				}
+			}
+		}
+
+		resolvers.add( new StandardDialectResolver() );
+		return resolvers;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverSet.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverSet.java
index 73fe821cac..8577d8cc1d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverSet.java
@@ -1,98 +1,99 @@
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
 package org.hibernate.service.jdbc.dialect.internal;
+
 import java.sql.DatabaseMetaData;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 
-import org.hibernate.internal.CoreMessageLogger;
+import org.jboss.logging.Logger;
+
 import org.hibernate.dialect.Dialect;
 import org.hibernate.exception.JDBCConnectionException;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
 
-import org.jboss.logging.Logger;
-
 /**
  * A {@link DialectResolver} implementation which coordinates resolution by delegating to sub-resolvers.
  *
  * @author Tomoto Shimizu Washio
  * @author Steve Ebersole
  */
 public class DialectResolverSet implements DialectResolver {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DialectResolverSet.class.getName());
 
 	private List<DialectResolver> resolvers;
 
 	public DialectResolverSet() {
 		this( new ArrayList<DialectResolver>() );
 	}
 
 	public DialectResolverSet(List<DialectResolver> resolvers) {
 		this.resolvers = resolvers;
 	}
 
 	public DialectResolverSet(DialectResolver... resolvers) {
 		this( Arrays.asList( resolvers ) );
 	}
 
 	public Dialect resolveDialect(DatabaseMetaData metaData) throws JDBCConnectionException {
 		for ( DialectResolver resolver : resolvers ) {
 			try {
 				Dialect dialect = resolver.resolveDialect( metaData );
 				if ( dialect != null ) {
 					return dialect;
 				}
 			}
 			catch ( JDBCConnectionException e ) {
 				throw e;
 			}
 			catch ( Exception e ) {
                 LOG.exceptionInSubResolver(e.getMessage());
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Add a resolver at the end of the underlying resolver list.  The resolver added by this method is at lower
 	 * priority than any other existing resolvers.
 	 *
 	 * @param resolver The resolver to add.
 	 */
 	public void addResolver(DialectResolver resolver) {
 		resolvers.add( resolver );
 	}
 
 	/**
 	 * Add a resolver at the beginning of the underlying resolver list.  The resolver added by this method is at higher
 	 * priority than any other existing resolvers.
 	 *
 	 * @param resolver The resolver to add.
 	 */
 	public void addResolverAtFirst(DialectResolver resolver) {
 		resolvers.add( 0, resolver );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/StandardDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/StandardDialectResolver.java
index 8a0b7d6e36..e7ec567665 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/StandardDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/StandardDialectResolver.java
@@ -1,160 +1,161 @@
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
 package org.hibernate.service.jdbc.dialect.internal;
+
 import java.sql.DatabaseMetaData;
 import java.sql.SQLException;
 
-import org.hibernate.internal.CoreMessageLogger;
+import org.jboss.logging.Logger;
+
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.DerbyDialect;
 import org.hibernate.dialect.DerbyTenFiveDialect;
 import org.hibernate.dialect.DerbyTenSixDialect;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.InformixDialect;
 import org.hibernate.dialect.Ingres10Dialect;
 import org.hibernate.dialect.Ingres9Dialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle10gDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.Oracle9iDialect;
 import org.hibernate.dialect.PostgreSQLDialect;
 import org.hibernate.dialect.SQLServer2005Dialect;
 import org.hibernate.dialect.SQLServer2008Dialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.dialect.SybaseAnywhereDialect;
-
-import org.jboss.logging.Logger;
+import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * The standard Hibernate Dialect resolver.
  *
  * @author Steve Ebersole
  */
 public class StandardDialectResolver extends AbstractDialectResolver {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        StandardDialectResolver.class.getName());
 
 	@Override
     protected Dialect resolveDialectInternal(DatabaseMetaData metaData) throws SQLException {
 		String databaseName = metaData.getDatabaseProductName();
 		int databaseMajorVersion = metaData.getDatabaseMajorVersion();
 
 		if ( "HSQL Database Engine".equals( databaseName ) ) {
 			return new HSQLDialect();
 		}
 
 		if ( "H2".equals( databaseName ) ) {
 			return new H2Dialect();
 		}
 
 		if ( "MySQL".equals( databaseName ) ) {
 			return new MySQLDialect();
 		}
 
 		if ( "PostgreSQL".equals( databaseName ) ) {
 			return new PostgreSQLDialect();
 		}
 
 		if ( "Apache Derby".equals( databaseName ) ) {
 			final int databaseMinorVersion = metaData.getDatabaseMinorVersion();
 			if ( databaseMajorVersion > 10 || ( databaseMajorVersion == 10 && databaseMinorVersion >= 6 ) ) {
 				return new DerbyTenSixDialect();
 			}
 			else if ( databaseMajorVersion == 10 && databaseMinorVersion == 5 ) {
 				return new DerbyTenFiveDialect();
 			}
 			else {
 				return new DerbyDialect();
 			}
 		}
 
 		if ( "ingres".equalsIgnoreCase( databaseName ) ) {
             switch( databaseMajorVersion ) {
                 case 9:
                     int databaseMinorVersion = metaData.getDatabaseMinorVersion();
                     if (databaseMinorVersion > 2) {
                         return new Ingres9Dialect();
                     }
                     return new IngresDialect();
                 case 10:
                     return new Ingres10Dialect();
                 default:
                     LOG.unknownIngresVersion(databaseMajorVersion);
             }
 			return new IngresDialect();
 		}
 
 		if ( databaseName.startsWith( "Microsoft SQL Server" ) ) {
 			switch ( databaseMajorVersion ) {
                 case 8:
                     return new SQLServerDialect();
                 case 9:
                     return new SQLServer2005Dialect();
                 case 10:
                     return new SQLServer2008Dialect();
                 default:
                     LOG.unknownSqlServerVersion(databaseMajorVersion);
 			}
 			return new SQLServerDialect();
 		}
 
 		if ( "Sybase SQL Server".equals( databaseName ) || "Adaptive Server Enterprise".equals( databaseName ) ) {
 			return new SybaseASE15Dialect();
 		}
 
 		if ( databaseName.startsWith( "Adaptive Server Anywhere" ) ) {
 			return new SybaseAnywhereDialect();
 		}
 
 		if ( "Informix Dynamic Server".equals( databaseName ) ) {
 			return new InformixDialect();
 		}
 
 		if ( databaseName.startsWith( "DB2/" ) ) {
 			return new DB2Dialect();
 		}
 
 		if ( "Oracle".equals( databaseName ) ) {
 			switch ( databaseMajorVersion ) {
 				case 11:
 					return new Oracle10gDialect();
 				case 10:
 					return new Oracle10gDialect();
 				case 9:
 					return new Oracle9iDialect();
 				case 8:
 					return new Oracle8iDialect();
 				default:
                     LOG.unknownOracleVersion(databaseMajorVersion);
 			}
 		}
 
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
index d83b39b236..f01fea5129 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
@@ -1,219 +1,210 @@
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
 package org.hibernate.service.jmx.internal;
 import java.lang.management.ManagementFactory;
 import java.util.ArrayList;
 import java.util.Map;
 import javax.management.MBeanServer;
 import javax.management.MBeanServerFactory;
 import javax.management.MalformedObjectNameException;
 import javax.management.ObjectName;
 import org.hibernate.HibernateException;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.Service;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.Manageable;
 import org.hibernate.service.spi.Stoppable;
 
 import org.jboss.logging.Logger;
 
 /**
  * Standard implementation of JMX services
  *
  * @author Steve Ebersole
  */
 public class JmxServiceImpl implements JmxService, Stoppable {
-
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JmxServiceImpl.class.getName());
 
-	public static final String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
-	public static final String JMX_AGENT_ID = "hibernate.jmx.agentId";
-	public static final String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
-	public static final String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
+	public static final String OBJ_NAME_TEMPLATE = "%s:sessionFactory=%s,serviceRole=%s,serviceType=%s";
 
 	private final boolean usePlatformServer;
 	private final String agentId;
 	private final String defaultDomain;
 	private final String sessionFactoryName;
 
 	public JmxServiceImpl(Map configValues) {
-		usePlatformServer = ConfigurationHelper.getBoolean( JMX_PLATFORM_SERVER, configValues );
-		agentId = (String) configValues.get( JMX_AGENT_ID );
-		defaultDomain = (String) configValues.get( JMX_DOMAIN_NAME );
+		usePlatformServer = ConfigurationHelper.getBoolean( AvailableSettings.JMX_PLATFORM_SERVER, configValues );
+		agentId = (String) configValues.get( AvailableSettings.JMX_AGENT_ID );
+		defaultDomain = (String) configValues.get( AvailableSettings.JMX_DOMAIN_NAME );
 		sessionFactoryName = ConfigurationHelper.getString(
-				JMX_SF_NAME,
+				AvailableSettings.JMX_SF_NAME,
 				configValues,
 				ConfigurationHelper.getString( Environment.SESSION_FACTORY_NAME, configValues )
 		);
 	}
 
 	private boolean startedServer;
 	private ArrayList<ObjectName> registeredMBeans;
 
 	@Override
 	public void stop() {
 		try {
 			// if we either started the JMX server or we registered some MBeans we at least need to look up
-			// MBean server and do *some* work on shutdwon.
+			// MBean server and do *some* work on shutdown.
 			if ( startedServer || registeredMBeans != null ) {
 				MBeanServer mBeanServer = findServer();
 				if ( mBeanServer == null ) {
                     LOG.unableToLocateMBeanServer();
 					return;
 				}
 
 				// release any MBeans we registered
 				if ( registeredMBeans != null ) {
 					for ( ObjectName objectName : registeredMBeans ) {
 						try {
                             LOG.trace("Unregistering registered MBean [ON=" + objectName + "]");
 							mBeanServer.unregisterMBean( objectName );
 						}
 						catch ( Exception e ) {
                             LOG.debugf("Unable to unregsiter registered MBean [ON=%s] : %s", objectName, e.toString());
 						}
 					}
 				}
 
 				// stop the MBean server if we started it
 				if ( startedServer ) {
                     LOG.trace("Attempting to release created MBeanServer");
 					try {
 						MBeanServerFactory.releaseMBeanServer( mBeanServer );
 					}
 					catch ( Exception e ) {
                         LOG.unableToReleaseCreatedMBeanServer(e.toString());
 					}
 				}
 			}
 		}
 		finally {
 			startedServer = false;
 			if ( registeredMBeans != null ) {
 				registeredMBeans.clear();
 				registeredMBeans = null;
 			}
 		}
 	}
 
-	public static final String DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
-	public static final String OBJ_NAME_TEMPLATE = "%s:sessionFactory=%s,serviceRole=%s,serviceType=%s";
 
 	// todo : should serviceRole come first in ObjectName template?  depends on the groupings we want in the UI.
 	// 		as-is mbeans from each sessionFactory are grouped primarily.
 
 	@Override
 	public void registerService(Manageable service, Class<? extends Service> serviceRole) {
 		final String domain = service.getManagementDomain() == null
-				? DEFAULT_OBJ_NAME_DOMAIN
+				? AvailableSettings.JMX_DEFAULT_OBJ_NAME_DOMAIN
 				: service.getManagementDomain();
 		final String serviceType = service.getManagementServiceType() == null
 				? service.getClass().getName()
 				: service.getManagementServiceType();
 		try {
 			final ObjectName objectName = new ObjectName(
 					String.format(
 							OBJ_NAME_TEMPLATE,
 							domain,
 							sessionFactoryName,
 							serviceRole.getName(),
 							serviceType
 					)
 			);
 			registerMBean( objectName, service.getManagementBean() );
 		}
-		catch ( HibernateException e ) {
-			throw e;
-		}
 		catch ( MalformedObjectNameException e ) {
 			throw new HibernateException( "Unable to generate service IbjectName", e );
 		}
 	}
 
 	@Override
 	public void registerMBean(ObjectName objectName, Object mBean) {
 		MBeanServer mBeanServer = findServer();
 		if ( mBeanServer == null ) {
 			if ( startedServer ) {
 				throw new HibernateException( "Could not locate previously started MBeanServer" );
 			}
 			mBeanServer = startMBeanServer();
 			startedServer = true;
 		}
 
 		try {
 			mBeanServer.registerMBean( mBean, objectName );
 			if ( registeredMBeans == null ) {
 				registeredMBeans = new ArrayList<ObjectName>();
 			}
 			registeredMBeans.add( objectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Unable to register MBean [ON=" + objectName + "]", e );
 		}
 	}
 
 	/**
 	 * Locate the MBean server to use based on user input from startup.
 	 *
 	 * @return The MBean server to use.
 	 */
 	private MBeanServer findServer() {
 		if ( usePlatformServer ) {
 			// they specified to use the platform (vm) server
 			return ManagementFactory.getPlatformMBeanServer();
 		}
 
 		// otherwise lookup all servers by (optional) agentId.
 		// IMPL NOTE : the findMBeanServer call treats a null agentId to mean match all...
 		ArrayList<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer( agentId );
 
 		if ( defaultDomain == null ) {
 			// they did not specify a domain by which to locate a particular MBeanServer to use, so chose the first
 			return mbeanServers.get( 0 );
 		}
 
 		for ( MBeanServer mbeanServer : mbeanServers ) {
 			// they did specify a domain, so attempt to locate an MBEanServer with a matching default domain, returning it
 			// if we find it.
 			if ( defaultDomain.equals( mbeanServer.getDefaultDomain() ) ) {
 				return mbeanServer;
 			}
 		}
 
 		return null;
 	}
 
 	private MBeanServer startMBeanServer() {
 		try {
-			MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer( defaultDomain );
-			return mbeanServer;
+			return MBeanServerFactory.createMBeanServer( defaultDomain );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Unable to start MBeanServer", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
index 5cf071b5d5..c394caec0b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
@@ -1,53 +1,53 @@
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
 package org.hibernate.service.jmx.internal;
 
 import java.util.Map;
 
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JmxService} service
  *
  * @author Steve Ebersole
  */
 public class JmxServiceInitiator implements BasicServiceInitiator<JmxService> {
-	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final JmxServiceInitiator INSTANCE = new JmxServiceInitiator();
 
 	@Override
 	public Class<JmxService> getServiceInitiated() {
 		return JmxService.class;
 	}
 
 	@Override
 	public JmxService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-		return ConfigurationHelper.getBoolean( JMX_ENABLED, configurationValues, false )
+		return ConfigurationHelper.getBoolean( AvailableSettings.JMX_ENABLED, configurationValues, false )
 				? new JmxServiceImpl( configurationValues )
 				: DisabledJmxServiceImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
index 97e6e34d0a..184b95886b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
@@ -1,134 +1,135 @@
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
 package org.hibernate.service.jta.platform.internal;
 
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 import javax.transaction.UserTransaction;
 import java.util.Map;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractJtaPlatform
 		implements JtaPlatform, Configurable, ServiceRegistryAwareService, TransactionManagerAccess {
 	private boolean cacheTransactionManager;
 	private boolean cacheUserTransaction;
 	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	protected ServiceRegistry serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected JndiService jndiService() {
 		return serviceRegistry().getService( JndiService.class );
 	}
 
 	protected abstract TransactionManager locateTransactionManager();
 	protected abstract UserTransaction locateUserTransaction();
 
 	public void configure(Map configValues) {
-		cacheTransactionManager = ConfigurationHelper.getBoolean( CACHE_TM, configValues, true );
-		cacheUserTransaction = ConfigurationHelper.getBoolean( CACHE_UT, configValues, false );
+		cacheTransactionManager = ConfigurationHelper.getBoolean( AvailableSettings.JTA_CACHE_TM, configValues, true );
+		cacheUserTransaction = ConfigurationHelper.getBoolean( AvailableSettings.JTA_CACHE_UT, configValues, false );
 	}
 
 	protected boolean canCacheTransactionManager() {
 		return cacheTransactionManager;
 	}
 
 	protected boolean canCacheUserTransaction() {
 		return cacheUserTransaction;
 	}
 
 	private TransactionManager transactionManager;
 
 	@Override
 	public TransactionManager retrieveTransactionManager() {
 		if ( canCacheTransactionManager() ) {
 			if ( transactionManager == null ) {
 				transactionManager = locateTransactionManager();
 			}
 			return transactionManager;
 		}
 		else {
 			return locateTransactionManager();
 		}
 	}
 
 	@Override
 	public TransactionManager getTransactionManager() {
 		return retrieveTransactionManager();
 	}
 
 	private UserTransaction userTransaction;
 
 	@Override
 	public UserTransaction retrieveUserTransaction() {
 		if ( canCacheUserTransaction() ) {
 			if ( userTransaction == null ) {
 				userTransaction = locateUserTransaction();
 			}
 			return userTransaction;
 		}
 		return locateUserTransaction();
 	}
 
 	@Override
 	public Object getTransactionIdentifier(Transaction transaction) {
 		// generally we use the transaction itself.
 		return transaction;
 	}
 
 	protected abstract JtaSynchronizationStrategy getSynchronizationStrategy();
 
 	@Override
 	public void registerSynchronization(Synchronization synchronization) {
 		getSynchronizationStrategy().registerSynchronization( synchronization );
 	}
 
 	@Override
 	public boolean canRegisterSynchronization() {
 		return getSynchronizationStrategy().canRegisterSynchronization();
 	}
 
 	@Override
 	public int getCurrentStatus() throws SystemException {
 		return retrieveTransactionManager().getStatus();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
index 896e6dd191..815f9064f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
@@ -1,186 +1,186 @@
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
 package org.hibernate.service.jta.platform.internal;
 
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.jndi.JndiHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.jta.platform.spi.JtaPlatformException;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.transaction.TransactionManagerLookup;
 
 /**
  * Standard initiator for the standard {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
  *
  * @author Steve Ebersole
  */
 public class JtaPlatformInitiator implements BasicServiceInitiator<JtaPlatform> {
 	public static final JtaPlatformInitiator INSTANCE = new JtaPlatformInitiator();
-	public static final String JTA_PLATFORM = "hibernate.jta.platform";
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JtaPlatformInitiator.class.getName());
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JtaPlatformInitiator.class.getName());
 
 	@Override
 	public Class<JtaPlatform> getServiceInitiated() {
 		return JtaPlatform.class;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public JtaPlatform initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		final Object platform = getConfiguredPlatform( configurationValues, registry );
 		if ( platform == null ) {
 			return new NoJtaPlatform();
 		}
 
 		if ( JtaPlatform.class.isInstance( platform ) ) {
 			return (JtaPlatform) platform;
 		}
 
 		final Class<JtaPlatform> jtaPlatformImplClass;
 
 		if ( Class.class.isInstance( platform ) ) {
 			jtaPlatformImplClass = (Class<JtaPlatform>) platform;
 		}
 		else {
 			final String platformImplName = platform.toString();
 			final ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 			try {
 				jtaPlatformImplClass = classLoaderService.classForName( platformImplName );
 			}
 			catch ( Exception e ) {
 				throw new HibernateException( "Unable to locate specified JtaPlatform class [" + platformImplName + "]", e );
 			}
 		}
 
 		try {
 			return jtaPlatformImplClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Unable to create specified JtaPlatform class [" + jtaPlatformImplClass.getName() + "]", e );
 		}
 	}
 
 	private Object getConfiguredPlatform(Map configVales, ServiceRegistryImplementor registry) {
-		Object platform = configVales.get( JTA_PLATFORM );
+		Object platform = configVales.get( AvailableSettings.JTA_PLATFORM );
 		if ( platform == null ) {
 			final String transactionManagerLookupImplName = (String) configVales.get( Environment.TRANSACTION_MANAGER_STRATEGY );
 			if ( transactionManagerLookupImplName != null ) {
                 LOG.deprecatedTransactionManagerStrategy(TransactionManagerLookup.class.getName(),
                                                          Environment.TRANSACTION_MANAGER_STRATEGY,
                                                          JtaPlatform.class.getName(),
-                                                         JTA_PLATFORM);
+                                                         AvailableSettings.JTA_PLATFORM);
 				platform = mapLegacyClasses( transactionManagerLookupImplName, configVales, registry );
                 LOG.debugf("Mapped %s -> %s", transactionManagerLookupImplName, platform);
 			}
 		}
 		return platform;
 	}
 
 	private JtaPlatform mapLegacyClasses(String tmlImplName, Map configVales, ServiceRegistryImplementor registry) {
 		if ( tmlImplName == null ) {
 			return null;
 		}
 
-        LOG.legacyTransactionManagerStrategy(JtaPlatform.class.getName(), JTA_PLATFORM);
+        LOG.legacyTransactionManagerStrategy(JtaPlatform.class.getName(), AvailableSettings.JTA_PLATFORM);
 
 		if ( "org.hibernate.transaction.BESTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new BorlandEnterpriseServerJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.BTMTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new BitronixJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.JBossTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JBossAppServerPlatform();
 		}
 
 		if ( "org.hibernate.transaction.JBossTSStandaloneTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JBossStandAloneJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.JOnASTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JOnASJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.JOTMTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JOTMJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.JRun4TransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JRun4JtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.OC4JTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new OC4JJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.OrionTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new OrionJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.ResinTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new ResinJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.SunONETransactionManagerLookup".equals( tmlImplName ) ) {
 			return new SunOneJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.WeblogicTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new WeblogicJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.WebSphereTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new WebSphereJtaPlatform();
 		}
 
 		if ( "org.hibernate.transaction.WebSphereExtendedJTATransactionLookup".equals( tmlImplName ) ) {
 			return new WebSphereExtendedJtaPlatform();
 		}
 
 		try {
 			TransactionManagerLookup lookup = (TransactionManagerLookup) registry.getService( ClassLoaderService.class )
 					.classForName( tmlImplName )
 					.newInstance();
 			return new TransactionManagerLookupBridge( lookup, JndiHelper.extractJndiProperties( configVales ) );
 		}
 		catch ( Exception e ) {
 			throw new JtaPlatformException(
 					"Unable to build " + TransactionManagerLookupBridge.class.getName() + " from specified " +
 							TransactionManagerLookup.class.getName() + " implementation: " +
 							tmlImplName
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
index 32e303fbcb..86c56bc843 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
@@ -1,99 +1,89 @@
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
 package org.hibernate.service.jta.platform.spi;
 
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 import javax.transaction.UserTransaction;
 
 import org.hibernate.service.Service;
 
 /**
  * Defines how we interact with various JTA services on the given platform/environment.
  *
  * @author Steve Ebersole
  */
 public interface JtaPlatform extends Service {
-	/**
-	 * A configuration value key used to indicate that it is safe to cache
-	 * {@link TransactionManager} references.
-	 */
-	public static final String CACHE_TM = "hibernate.jta.cacheTransactionManager";
-	/**
-	 * A configuration value key used to indicate that it is safe to cache
-	 * {@link UserTransaction} references.
-	 */
-	public static final String CACHE_UT = "hibernate.jta.cacheUserTransaction";
 
 	/**
 	 * Locate the {@link TransactionManager}
 	 *
 	 * @return The {@link TransactionManager}
 	 */
 	public TransactionManager retrieveTransactionManager();
 
 	/**
 	 * Locate the {@link UserTransaction}
 	 *
 	 * @return The {@link UserTransaction}
 	 */
 	public UserTransaction retrieveUserTransaction();
 
 	/**
 	 * Determine an identifier for the given transaction appropriate for use in caching/lookup usages.
 	 * <p/>
 	 * Generally speaking the transaction itself will be returned here.  This method was added specifically
 	 * for use in WebSphere and other unfriendly JEE containers (although WebSphere is still the only known
 	 * such brain-dead, sales-driven impl).
 	 *
 	 * @param transaction The transaction to be identified.
 	 * @return An appropriate identifier
 	 */
 	public Object getTransactionIdentifier(Transaction transaction);
 
 	/**
 	 * Can we currently register a {@link Synchronization}?
 	 *
 	 * @return True if registering a {@link Synchronization} is currently allowed; false otherwise.
 	 */
 	public boolean canRegisterSynchronization();
 
 	/**
 	 * Register a JTA {@link Synchronization} in the means defined by the platform.
 	 *
 	 * @param synchronization The synchronization to register
 	 */
 	public void registerSynchronization(Synchronization synchronization);
 
 	/**
 	 * Obtain the current transaction status using whatever means is preferred for this platform
 	 *
 	 * @return The current status.
 	 *
 	 * @throws SystemException Indicates a problem access the underlying status
 	 */
 	public int getCurrentStatus() throws SystemException;
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/EnversTestingJtaBootstrap.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/EnversTestingJtaBootstrap.java
index d738b3db43..da94f8631b 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/EnversTestingJtaBootstrap.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/EnversTestingJtaBootstrap.java
@@ -1,70 +1,69 @@
 package org.hibernate.envers.test;
 
 import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
 import com.arjuna.ats.internal.arjuna.objectstore.VolatileStore;
 import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
 import org.enhydra.jdbc.standard.StandardXADataSource;
 import org.hibernate.cfg.Environment;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
 import org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 import javax.transaction.Status;
 import javax.transaction.TransactionManager;
 import java.sql.SQLException;
 import java.util.Map;
 
 /**
  * Copied from {@link org.hibernate.testing.jta.TestingJtaBootstrap}, as Envers tests use a different URL for
  * testing databases.
  * @author Adam Warski (adam at warski dot org)
  */
 public class EnversTestingJtaBootstrap {
 	public static TransactionManager updateConfigAndCreateTM(Map configValues) {
         BeanPopulator
 				.getDefaultInstance(ObjectStoreEnvironmentBean.class)
 				.setObjectStoreType( VolatileStore.class.getName() );
 
 		BeanPopulator
 				.getNamedInstance( ObjectStoreEnvironmentBean.class, "communicationStore" )
 				.setObjectStoreType( VolatileStore.class.getName() );
 
 		BeanPopulator
 				.getNamedInstance( ObjectStoreEnvironmentBean.class, "stateStore" )
 				.setObjectStoreType( VolatileStore.class.getName() );
 
 		TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
 
 		StandardXADataSource dataSource = new StandardXADataSource();
 		dataSource.setTransactionManager( transactionManager );
 		try {
 			dataSource.setDriverName( configValues.get(Environment.DRIVER).toString() );
 		}
 		catch (SQLException e) {
 			throw new RuntimeException( "Unable to set DataSource JDBC driver name", e );
 		}
 		dataSource.setUrl(configValues.get(Environment.URL).toString());
 		dataSource.setUser(configValues.get(Environment.USER).toString());
 
         configValues.remove(Environment.URL);
         configValues.remove(Environment.USER);
         configValues.remove(Environment.DRIVER);
 
-		configValues.put( JtaPlatformInitiator.JTA_PLATFORM, new JBossStandAloneJtaPlatform() );
+		configValues.put( org.hibernate.cfg.AvailableSettings.JTA_PLATFORM, new JBossStandAloneJtaPlatform() );
 		configValues.put( Environment.CONNECTION_PROVIDER, DatasourceConnectionProviderImpl.class.getName() );
 		configValues.put( Environment.DATASOURCE, dataSource );
 
         configValues.put(AvailableSettings.TRANSACTION_TYPE, "JTA");
 
         return transactionManager;
 	}
 
     public static void tryCommit(TransactionManager tm) throws Exception {
         if (tm.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
             tm.rollback();
         } else {
             tm.commit();
         }
     }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
index d3e0f8ee04..aeaa155ea5 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
@@ -1,123 +1,123 @@
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
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.TransactionalDataRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Base class for tests of EntityRegion and CollectionRegion implementations.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractEntityCollectionRegionTestCase extends AbstractRegionImplTestCase {
 	@Test
 	public void testSupportedAccessTypes() throws Exception {
 		supportedAccessTypeTest();
 	}
 
 	private void supportedAccessTypeTest() throws Exception {
 		Configuration cfg = CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, true, false );
 		String entityCfg = "entity";
 		cfg.setProperty( InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, entityCfg );
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 				ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		supportedAccessTypeTest( regionFactory, cfg.getProperties() );
 	}
 
 	/**
 	 * Creates a Region using the given factory, and then ensure that it handles calls to
 	 * buildAccessStrategy as expected when all the various {@link AccessType}s are passed as
 	 * arguments.
 	 */
 	protected abstract void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties);
 
 	@Test
 	public void testIsTransactionAware() throws Exception {
 		Configuration cfg = CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, true, false );
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 				ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		TransactionalDataRegion region = (TransactionalDataRegion) createRegion(
 				regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription()
 		);
 		assertTrue( "Region is transaction-aware", region.isTransactionAware() );
 		CacheTestUtil.stopRegionFactory( regionFactory, getCacheTestSupport() );
 		cfg = CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, true, false );
 		// Make it non-transactional
-		cfg.getProperties().remove( JtaPlatformInitiator.JTA_PLATFORM );
+		cfg.getProperties().remove( AvailableSettings.JTA_PLATFORM );
 		regionFactory = CacheTestUtil.startRegionFactory(
 				ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		region = (TransactionalDataRegion) createRegion(
 				regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription()
 		);
 		assertFalse( "Region is not transaction-aware", region.isTransactionAware() );
 		CacheTestUtil.stopRegionFactory( regionFactory, getCacheTestSupport() );
 	}
 
 	@Test
 	public void testGetCacheDataDescription() throws Exception {
 		Configuration cfg = CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, true, false );
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 				ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		TransactionalDataRegion region = (TransactionalDataRegion) createRegion(
 				regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription()
 		);
 		CacheDataDescription cdd = region.getCacheDataDescription();
 		assertNotNull( cdd );
 		CacheDataDescription expected = getCacheDataDescription();
 		assertEquals( expected.isMutable(), cdd.isMutable() );
 		assertEquals( expected.isVersioned(), cdd.isVersioned() );
 		assertEquals( expected.getVersionComparator(), cdd.getVersionComparator() );
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
index 37a8dfba94..fa75e9c3a4 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
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
 package org.hibernate.test.cache.infinispan.functional;
 
 import javax.transaction.Status;
 import javax.transaction.TransactionManager;
 
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.tm.JtaPlatformImpl;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class SingleNodeTestCase extends BaseCoreFunctionalTestCase {
 	private static final Log log = LogFactory.getLog( SingleNodeTestCase.class );
 	private TransactionManager tm;
 
 	@Before
 	public void prepare() {
 		tm = getTransactionManager();
 	}
 
 	protected TransactionManager getTransactionManager() {
 		try {
 			Class<? extends JtaPlatform> jtaPlatformClass = getJtaPlatform();
 			if ( jtaPlatformClass == null ) {
 				return null;
 			}
 			else {
 				return jtaPlatformClass.newInstance().retrieveTransactionManager();
 			}
 		}
 		catch (Exception e) {
 			log.error( "Error", e );
 			throw new RuntimeException( e );
 		}
 	}
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"cache/infinispan/functional/Item.hbm.xml",
 				"cache/infinispan/functional/Customer.hbm.xml",
 				"cache/infinispan/functional/Contact.hbm.xml"
 		};
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return "transactional";
 	}
 
 	protected Class<? extends RegionFactory> getCacheRegionFactory() {
 		return InfinispanRegionFactory.class;
 	}
 
 	protected Class<? extends TransactionFactory> getTransactionFactoryClass() {
 		return CMTTransactionFactory.class;
 	}
 
 	protected Class<? extends ConnectionProvider> getConnectionProviderClass() {
 		return org.hibernate.test.cache.infinispan.tm.XaConnectionProvider.class;
 	}
 
 	protected Class<? extends JtaPlatform> getJtaPlatform() {
 		return JtaPlatformImpl.class;
 	}
 
 	protected boolean getUseQueryCache() {
 		return true;
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, String.valueOf( getUseQueryCache() ) );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName() );
 
 		if ( getJtaPlatform() != null ) {
-			cfg.getProperties().put( JtaPlatformInitiator.JTA_PLATFORM, getJtaPlatform() );
+			cfg.getProperties().put( AvailableSettings.JTA_PLATFORM, getJtaPlatform() );
 		}
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName() );
 		cfg.setProperty( Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName() );
 	}
 
 	protected void beginTx() throws Exception {
 		tm.begin();
 	}
 
 	protected void setRollbackOnlyTx() throws Exception {
 		tm.setRollbackOnly();
 	}
 
 	protected void setRollbackOnlyTx(Exception e) throws Exception {
 		log.error( "Error", e );
 		tm.setRollbackOnly();
 		throw e;
 	}
 
 	protected void setRollbackOnlyTxExpected(Exception e) throws Exception {
 		log.debug( "Expected behaivour", e );
 		tm.setRollbackOnly();
 	}
 
 	protected void commitOrRollbackTx() throws Exception {
 		if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 			tm.commit();
 		}
 		else {
 			tm.rollback();
 		}
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
index 3cbf2000a1..5ccfdbd725 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
@@ -1,421 +1,421 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.functional.bulk;
 
 import javax.transaction.Status;
 import javax.transaction.TransactionManager;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.FlushMode;
 import org.hibernate.Session;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.functional.Contact;
 import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.hibernate.test.cache.infinispan.tm.JtaPlatformImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 
 /**
  * BulkOperationsTestCase.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class BulkOperationsTestCase extends BaseCoreFunctionalTestCase {
 	private TransactionManager tm;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"cache/infinispan/functional/Contact.hbm.xml",
 				"cache/infinispan/functional/Customer.hbm.xml"
 		};
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return "transactional";
 	}
 
 	protected Class<? extends RegionFactory> getCacheRegionFactory() {
 		return InfinispanRegionFactory.class;
 	}
 
 	protected Class<? extends TransactionFactory> getTransactionFactoryClass() {
 		return CMTTransactionFactory.class;
 	}
 
 	protected Class<? extends ConnectionProvider> getConnectionProviderClass() {
 		return org.hibernate.test.cache.infinispan.tm.XaConnectionProvider.class;
 	}
 
 	protected JtaPlatform getJtaPlatform() {
 		return new JtaPlatformImpl();
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "false" );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName() );
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName() );
-		cfg.getProperties().put( JtaPlatformInitiator.JTA_PLATFORM, getJtaPlatform() );
+		cfg.getProperties().put( AvailableSettings.JTA_PLATFORM, getJtaPlatform() );
 		cfg.setProperty( Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName() );
 	}
 
 	@Test
 	public void testBulkOperations() throws Throwable {
 		boolean cleanedUp = false;
 		try {
 			tm = getJtaPlatform().retrieveTransactionManager();
 
 			createContacts();
 
 			List<Integer> rhContacts = getContactsByCustomer( "Red Hat" );
 			assertNotNull( "Red Hat contacts exist", rhContacts );
 			assertEquals( "Created expected number of Red Hat contacts", 10, rhContacts.size() );
 
 			SecondLevelCacheStatistics contactSlcs = sessionFactory()
 					.getStatistics()
 					.getSecondLevelCacheStatistics( Contact.class.getName() );
 			assertEquals( 20, contactSlcs.getElementCountInMemory() );
 
 			assertEquals( "Deleted all Red Hat contacts", 10, deleteContacts() );
 			assertEquals( 0, contactSlcs.getElementCountInMemory() );
 
 			List<Integer> jbContacts = getContactsByCustomer( "JBoss" );
 			assertNotNull( "JBoss contacts exist", jbContacts );
 			assertEquals( "JBoss contacts remain", 10, jbContacts.size() );
 
 			for ( Integer id : rhContacts ) {
 				assertNull( "Red Hat contact " + id + " cannot be retrieved", getContact( id ) );
 			}
 			rhContacts = getContactsByCustomer( "Red Hat" );
 			if ( rhContacts != null ) {
 				assertEquals( "No Red Hat contacts remain", 0, rhContacts.size() );
 			}
 
 			updateContacts( "Kabir", "Updated" );
 			assertEquals( 0, contactSlcs.getElementCountInMemory() );
 			for ( Integer id : jbContacts ) {
 				Contact contact = getContact( id );
 				assertNotNull( "JBoss contact " + id + " exists", contact );
 				String expected = ("Kabir".equals( contact.getName() )) ? "Updated" : "2222";
 				assertEquals( "JBoss contact " + id + " has correct TLF", expected, contact.getTlf() );
 			}
 
 			List<Integer> updated = getContactsByTLF( "Updated" );
 			assertNotNull( "Got updated contacts", updated );
 			assertEquals( "Updated contacts", 5, updated.size() );
 
 			updateContactsWithOneManual( "Kabir", "UpdatedAgain" );
 			assertEquals( contactSlcs.getElementCountInMemory(), 0 );
 			for ( Integer id : jbContacts ) {
 				Contact contact = getContact( id );
 				assertNotNull( "JBoss contact " + id + " exists", contact );
 				String expected = ("Kabir".equals( contact.getName() )) ? "UpdatedAgain" : "2222";
 				assertEquals( "JBoss contact " + id + " has correct TLF", expected, contact.getTlf() );
 			}
 
 			updated = getContactsByTLF( "UpdatedAgain" );
 			assertNotNull( "Got updated contacts", updated );
 			assertEquals( "Updated contacts", 5, updated.size() );
 		}
 		catch (Throwable t) {
 			cleanedUp = true;
 			cleanup( true );
 			throw t;
 		}
 		finally {
 			// cleanup the db so we can run this test multiple times w/o restarting the cluster
 			if ( !cleanedUp ) {
 				cleanup( false );
 			}
 		}
 	}
 
 	public void createContacts() throws Exception {
 		tm.begin();
 		try {
 			for ( int i = 0; i < 10; i++ ) {
 				createCustomer( i );
 			}
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				tm.rollback();
 			}
 		}
 	}
 
 	public int deleteContacts() throws Exception {
 		String deleteHQL = "delete Contact where customer in ";
 		deleteHQL += " (select customer FROM Customer as customer ";
 		deleteHQL += " where customer.name = :cName)";
 
 		tm.begin();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			int rowsAffected = session.createQuery( deleteHQL ).setFlushMode( FlushMode.AUTO )
 					.setParameter( "cName", "Red Hat" ).executeUpdate();
 			tm.commit();
 			return rowsAffected;
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				try {
 					tm.rollback();
 				}
 				catch (Exception ee) {
 					// ignored
 				}
 			}
 		}
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public List<Integer> getContactsByCustomer(String customerName) throws Exception {
 		String selectHQL = "select contact.id from Contact contact";
 		selectHQL += " where contact.customer.name = :cName";
 
 		tm.begin();
 		try {
 
 			Session session = sessionFactory().getCurrentSession();
 			return session.createQuery( selectHQL )
 					.setFlushMode( FlushMode.AUTO )
 					.setParameter( "cName", customerName )
 					.list();
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				tm.rollback();
 			}
 		}
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public List<Integer> getContactsByTLF(String tlf) throws Exception {
 		String selectHQL = "select contact.id from Contact contact";
 		selectHQL += " where contact.tlf = :cTLF";
 
 		tm.begin();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			return session.createQuery( selectHQL )
 					.setFlushMode( FlushMode.AUTO )
 					.setParameter( "cTLF", tlf )
 					.list();
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				tm.rollback();
 			}
 		}
 	}
 
 	public int updateContacts(String name, String newTLF) throws Exception {
 		String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
 		tm.begin();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			return session.createQuery( updateHQL )
 					.setFlushMode( FlushMode.AUTO )
 					.setParameter( "cNewTLF", newTLF )
 					.setParameter( "cName", name )
 					.executeUpdate();
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				tm.rollback();
 			}
 		}
 	}
 
 	public int updateContactsWithOneManual(String name, String newTLF) throws Exception {
 		String queryHQL = "from Contact c where c.name = :cName";
 		String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
 		tm.begin();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			@SuppressWarnings("unchecked")
 			List<Contact> list = session.createQuery( queryHQL ).setParameter( "cName", name ).list();
 			list.get( 0 ).setTlf( newTLF );
 			return session.createQuery( updateHQL )
 					.setFlushMode( FlushMode.AUTO )
 					.setParameter( "cNewTLF", newTLF )
 					.setParameter( "cName", name )
 					.executeUpdate();
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				tm.rollback();
 			}
 		}
 	}
 
 	public Contact getContact(Integer id) throws Exception {
 		tm.begin();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			return (Contact) session.get( Contact.class, id );
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				tm.rollback();
 			}
 		}
 	}
 
 	public void cleanup(boolean ignore) throws Exception {
 		String deleteContactHQL = "delete from Contact";
 		String deleteCustomerHQL = "delete from Customer";
 		tm.begin();
 		try {
 			Session session = sessionFactory().getCurrentSession();
 			session.createQuery( deleteContactHQL ).setFlushMode( FlushMode.AUTO ).executeUpdate();
 			session.createQuery( deleteCustomerHQL ).setFlushMode( FlushMode.AUTO ).executeUpdate();
 		}
 		catch (Exception e) {
 			tm.setRollbackOnly();
 			throw e;
 		}
 		finally {
 			if ( tm.getStatus() == Status.STATUS_ACTIVE ) {
 				tm.commit();
 			}
 			else {
 				if ( !ignore ) {
 					try {
 						tm.rollback();
 					}
 					catch (Exception ee) {
 						// ignored
 					}
 				}
 			}
 		}
 	}
 
 	private Customer createCustomer(int id) throws Exception {
 		System.out.println( "CREATE CUSTOMER " + id );
 		try {
 			Customer customer = new Customer();
 			customer.setName( (id % 2 == 0) ? "JBoss" : "Red Hat" );
 			Set<Contact> contacts = new HashSet<Contact>();
 
 			Contact kabir = new Contact();
 			kabir.setCustomer( customer );
 			kabir.setName( "Kabir" );
 			kabir.setTlf( "1111" );
 			contacts.add( kabir );
 
 			Contact bill = new Contact();
 			bill.setCustomer( customer );
 			bill.setName( "Bill" );
 			bill.setTlf( "2222" );
 			contacts.add( bill );
 
 			customer.setContacts( contacts );
 
 			Session s = openSession();
 			s.getTransaction().begin();
 			s.persist( customer );
 			s.getTransaction().commit();
 			s.close();
 
 			return customer;
 		}
 		finally {
 			System.out.println( "CREATE CUSTOMER " + id + " -  END" );
 		}
 	}
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
index ad0b535feb..43b60c4b23 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
@@ -1,191 +1,191 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class DualNodeTestCase extends BaseCoreFunctionalTestCase {
 	private static final Log log = LogFactory.getLog( DualNodeTestCase.class );
 
 	public static final String NODE_ID_PROP = "hibernate.test.cluster.node.id";
 	public static final String NODE_ID_FIELD = "nodeId";
 	public static final String LOCAL = "local";
 	public static final String REMOTE = "remote";
 
 	private SecondNodeEnvironment secondNodeEnvironment;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"cache/infinispan/functional/Contact.hbm.xml", "cache/infinispan/functional/Customer.hbm.xml"
 		};
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return "transactional";
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		standardConfigure( cfg );
 		cfg.setProperty( NODE_ID_PROP, LOCAL );
 		cfg.setProperty( NODE_ID_FIELD, LOCAL );
 	}
 
 	@Override
 	protected void cleanupTest() throws Exception {
 		cleanupTransactionManagement();
 	}
 
 	protected void cleanupTransactionManagement() {
 		DualNodeJtaTransactionManagerImpl.cleanupTransactions();
 		DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
 	}
 
 	@Before
 	public void prepare() throws Exception {
 		secondNodeEnvironment = new SecondNodeEnvironment();
 	}
 
 	@After
 	public void unPrepare() {
 		if ( secondNodeEnvironment != null ) {
 			secondNodeEnvironment.shutDown();
 		}
 	}
 
 	protected SecondNodeEnvironment secondNodeEnvironment() {
 		return secondNodeEnvironment;
 	}
 
 	protected Class getCacheRegionFactory() {
 		return ClusterAwareRegionFactory.class;
 	}
 
 	protected Class getConnectionProviderClass() {
 		return DualNodeConnectionProviderImpl.class;
 	}
 
 	protected Class getJtaPlatformClass() {
 		return DualNodeJtaPlatformImpl.class;
 	}
 
 	protected Class getTransactionFactoryClass() {
 		return CMTTransactionFactory.class;
 	}
 
 	protected void sleep(long ms) {
 		try {
 			Thread.sleep( ms );
 		}
 		catch (InterruptedException e) {
 			log.warn( "Interrupted during sleep", e );
 		}
 	}
 
 	protected boolean getUseQueryCache() {
 		return true;
 	}
 
 	protected void configureSecondNode(Configuration cfg) {
 
 	}
 
 	protected void standardConfigure(Configuration cfg) {
 		super.configure( cfg );
 
 		cfg.setProperty( Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName() );
-		cfg.setProperty( JtaPlatformInitiator.JTA_PLATFORM, getJtaPlatformClass().getName() );
+		cfg.setProperty( AvailableSettings.JTA_PLATFORM, getJtaPlatformClass().getName() );
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName() );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName() );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, String.valueOf( getUseQueryCache() ) );
 	}
 
 	public class SecondNodeEnvironment {
 		private Configuration configuration;
 		private BasicServiceRegistryImpl serviceRegistry;
 		private SessionFactoryImplementor sessionFactory;
 
 		public SecondNodeEnvironment() {
 			configuration = constructConfiguration();
 			standardConfigure( configuration );
 			configuration.setProperty( NODE_ID_PROP, REMOTE );
 			configuration.setProperty( NODE_ID_FIELD, REMOTE );
 			configureSecondNode( configuration );
 			addMappings(configuration);
 			configuration.buildMappings();
 			applyCacheSettings( configuration );
 			afterConfigurationBuilt( configuration );
 			serviceRegistry = buildServiceRegistry( configuration );
 			sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		}
 
 		public Configuration getConfiguration() {
 			return configuration;
 		}
 
 		public BasicServiceRegistryImpl getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		public SessionFactoryImplementor getSessionFactory() {
 			return sessionFactory;
 		}
 
 		public void shutDown() {
 			if ( sessionFactory != null ) {
 				try {
 					sessionFactory.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 			if ( serviceRegistry != null ) {
 				try {
 					serviceRegistry.destroy();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
index 4739d9e27d..d48e3433c5 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
@@ -1,147 +1,147 @@
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
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 import junit.framework.Test;
 import junit.framework.TestCase;
 import junit.framework.TestSuite;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 /**
  * Utilities for cache testing.
  * 
  * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
  */
 public class CacheTestUtil {
 
    public static Configuration buildConfiguration(String regionPrefix, Class regionFactory, boolean use2ndLevel, boolean useQueries) {
       Configuration cfg = new Configuration();
       cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
       cfg.setProperty(Environment.USE_STRUCTURED_CACHE, "true");
-      cfg.setProperty( JtaPlatformInitiator.JTA_PLATFORM, BatchModeJtaPlatform.class.getName() );
+      cfg.setProperty( AvailableSettings.JTA_PLATFORM, BatchModeJtaPlatform.class.getName() );
 
       cfg.setProperty(Environment.CACHE_REGION_FACTORY, regionFactory.getName());
       cfg.setProperty(Environment.CACHE_REGION_PREFIX, regionPrefix);
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, String.valueOf(use2ndLevel));
       cfg.setProperty(Environment.USE_QUERY_CACHE, String.valueOf(useQueries));
 
       return cfg;
    }
 
    public static Configuration buildLocalOnlyConfiguration(String regionPrefix, boolean use2ndLevel, boolean useQueries) {
       Configuration cfg = buildConfiguration(regionPrefix, InfinispanRegionFactory.class, use2ndLevel, useQueries);
       cfg.setProperty(InfinispanRegionFactory.INFINISPAN_CONFIG_RESOURCE_PROP,
                InfinispanRegionFactory.DEF_INFINISPAN_CONFIG_RESOURCE);
       return cfg;
    }
    
    public static Configuration buildCustomQueryCacheConfiguration(String regionPrefix, String queryCacheName) {
       Configuration cfg = buildConfiguration(regionPrefix, InfinispanRegionFactory.class, true, true);
       cfg.setProperty(InfinispanRegionFactory.QUERY_CACHE_RESOURCE_PROP, queryCacheName);
       return cfg;
    }
 
    public static InfinispanRegionFactory startRegionFactory(
 		   ServiceRegistry serviceRegistry,
 		   Configuration cfg) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
 
       Settings settings = cfg.buildSettings( serviceRegistry );
       Properties properties = cfg.getProperties();
 
       String factoryType = cfg.getProperty(Environment.CACHE_REGION_FACTORY);
       Class factoryClass = Thread.currentThread().getContextClassLoader().loadClass(factoryType);
       InfinispanRegionFactory regionFactory = (InfinispanRegionFactory) factoryClass.newInstance();
 
       regionFactory.start(settings, properties);
 
       return regionFactory;
    }
 
    public static InfinispanRegionFactory startRegionFactory(
 		   ServiceRegistry serviceRegistry,
 		   Configuration cfg,
 		   CacheTestSupport testSupport) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
       InfinispanRegionFactory factory = startRegionFactory( serviceRegistry, cfg );
       testSupport.registerFactory(factory);
       return factory;
    }
 
    public static void stopRegionFactory(InfinispanRegionFactory factory, CacheTestSupport testSupport) {
       factory.stop();
       testSupport.unregisterFactory(factory);
    }
 
    /**
     * Supports easy creation of a TestSuite where a subclass' "FailureExpected" version of a base
     * test is included in the suite, while the base test is excluded. E.g. test class FooTestCase
     * includes method testBar(), while test class SubFooTestCase extends FooTestCase includes method
     * testBarFailureExcluded(). Passing SubFooTestCase.class to this method will return a suite that
     * does not include testBar().
     * 
     * FIXME Move this to UnitTestCase
     */
    public static TestSuite createFailureExpectedSuite(Class testClass) {
 
       TestSuite allTests = new TestSuite(testClass);
       Set failureExpected = new HashSet();
       Enumeration tests = allTests.tests();
       while (tests.hasMoreElements()) {
          Test t = (Test) tests.nextElement();
          if (t instanceof TestCase) {
             String name = ((TestCase) t).getName();
             if (name.endsWith("FailureExpected"))
                failureExpected.add(name);
          }
       }
 
       TestSuite result = new TestSuite();
       tests = allTests.tests();
       while (tests.hasMoreElements()) {
          Test t = (Test) tests.nextElement();
          if (t instanceof TestCase) {
             String name = ((TestCase) t).getName();
             if (!failureExpected.contains(name + "FailureExpected")) {
                result.addTest(t);
             }
          }
       }
 
       return result;
    }
 
    /**
     * Prevent instantiation.
     */
    private CacheTestUtil() {
    }
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/jta/TestingJtaBootstrap.java b/hibernate-testing/src/main/java/org/hibernate/testing/jta/TestingJtaBootstrap.java
index 44098739b6..bf6f69fa90 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/jta/TestingJtaBootstrap.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/jta/TestingJtaBootstrap.java
@@ -1,109 +1,108 @@
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
 package org.hibernate.testing.jta;
 
 import javax.sql.DataSource;
 import javax.transaction.TransactionManager;
 import javax.transaction.UserTransaction;
 import java.sql.SQLException;
 import java.util.Map;
 import java.util.Properties;
 
 import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
-import com.arjuna.ats.arjuna.common.arjPropertyManager;
 import com.arjuna.ats.internal.arjuna.objectstore.VolatileStore;
 import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
 import org.enhydra.jdbc.standard.StandardXADataSource;
 
+import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Environment;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
 import org.hibernate.service.jta.platform.internal.JBossStandAloneJtaPlatform;
-import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 /**
  * Manages the {@link TransactionManager}, {@link UserTransaction} and {@link DataSource} instances used for testing.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnusedDeclaration", "unchecked"})
 public class TestingJtaBootstrap {
 	public static final TestingJtaBootstrap INSTANCE = new TestingJtaBootstrap();
 
 	private TransactionManager transactionManager;
 	private UserTransaction userTransaction;
 	private DataSource dataSource;
 
 	private TestingJtaBootstrap() {
 		BeanPopulator
 				.getDefaultInstance( ObjectStoreEnvironmentBean.class )
 				.setObjectStoreType( VolatileStore.class.getName() );
 
 		BeanPopulator
 				.getNamedInstance( ObjectStoreEnvironmentBean.class, "communicationStore" )
 				.setObjectStoreType( VolatileStore.class.getName() );
 
 		BeanPopulator
 				.getNamedInstance( ObjectStoreEnvironmentBean.class, "stateStore" )
 				.setObjectStoreType( VolatileStore.class.getName() );
 
 		this.transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
 		this.userTransaction = com.arjuna.ats.jta.UserTransaction.userTransaction();
 
 		Properties environmentProperties = Environment.getProperties();
 		StandardXADataSource dataSource = new StandardXADataSource();
 		dataSource.setTransactionManager( com.arjuna.ats.jta.TransactionManager.transactionManager() );
 		try {
 			dataSource.setDriverName( environmentProperties.getProperty( Environment.DRIVER ) );
 		}
 		catch (SQLException e) {
 			throw new RuntimeException( "Unable to set DataSource JDBC driver name", e );
 		}
 		dataSource.setUrl( environmentProperties.getProperty( Environment.URL ) );
 		dataSource.setUser( environmentProperties.getProperty( Environment.USER ) );
 		dataSource.setPassword( environmentProperties.getProperty( Environment.PASS ) );
 		final String isolationString = environmentProperties.getProperty( Environment.ISOLATION );
 		if ( isolationString != null ) {
 			dataSource.setTransactionIsolation( Integer.valueOf( isolationString ) );
 		}
 		this.dataSource = dataSource;
 	}
 
 	public TransactionManager getTransactionManager() {
 		return transactionManager;
 	}
 
 	public UserTransaction getUserTransaction() {
 		return userTransaction;
 	}
 
 	public DataSource getDataSource() {
 		return dataSource;
 	}
 
 	public static void prepare(Map configValues) {
-		configValues.put( JtaPlatformInitiator.JTA_PLATFORM, new JBossStandAloneJtaPlatform() );
+		configValues.put( AvailableSettings.JTA_PLATFORM, new JBossStandAloneJtaPlatform() );
 		configValues.put( Environment.CONNECTION_PROVIDER, DatasourceConnectionProviderImpl.class.getName() );
 		configValues.put( Environment.DATASOURCE, INSTANCE.getDataSource() );
 	}
 }
