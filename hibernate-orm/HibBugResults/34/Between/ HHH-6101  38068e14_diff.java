diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index 62e17c22cb..d054905513 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,811 +1,744 @@
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
 import java.sql.Statement;
 import java.sql.Timestamp;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Version;
 import org.hibernate.bytecode.spi.BytecodeProvider;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 import org.jboss.logging.Logger;
 
 
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
 public final class Environment {
 	/**
 	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
 	 */
 	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 	/**
 	 * JDBC driver class
 	 */
 	public static final String DRIVER ="hibernate.connection.driver_class";
 	/**
 	 * JDBC transaction isolation level
 	 */
 	public static final String ISOLATION ="hibernate.connection.isolation";
 	/**
 	 * JDBC URL
 	 */
 	public static final String URL ="hibernate.connection.url";
 	/**
 	 * JDBC user
 	 */
 	public static final String USER ="hibernate.connection.username";
 	/**
 	 * JDBC password
 	 */
 	public static final String PASS ="hibernate.connection.password";
 	/**
 	 * JDBC autocommit mode
 	 */
 	public static final String AUTOCOMMIT ="hibernate.connection.autocommit";
 	/**
 	 * Maximum number of inactive connections for Hibernate's connection pool
 	 */
 	public static final String POOL_SIZE ="hibernate.connection.pool_size";
 	/**
 	 * <tt>java.sql.Datasource</tt> JNDI name
 	 */
 	public static final String DATASOURCE ="hibernate.connection.datasource";
 	/**
 	 * prefix for arbitrary JDBC connection properties
 	 */
 	public static final String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
 	 */
 	public static final String JNDI_CLASS ="hibernate.jndi.class";
 	/**
 	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
 	 */
 	public static final String JNDI_URL ="hibernate.jndi.url";
 	/**
 	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
 	 */
 	public static final String JNDI_PREFIX = "hibernate.jndi";
 	/**
 	 * JNDI name to bind to <tt>SessionFactory</tt>
 	 */
 	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Hibernate SQL {@link org.hibernate.dialect.Dialect} class
 	 */
 	public static final String DIALECT ="hibernate.dialect";
 
 	/**
 	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectResolver} classes to register with the
 	 * {@link org.hibernate.service.jdbc.dialect.spi.DialectFactory}
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
 	 * <tt>TransactionManagerLookup</tt> implementor to use for obtaining the <tt>TransactionManager</tt>
 	 */
 	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
 	/**
 	 * JNDI name of JTA <tt>UserTransaction</tt> object
 	 */
 	public static final String USER_TRANSACTION = "jta.UserTransaction";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
 
 	/**
 	 * The {@link org.hibernate.cache.RegionFactory} implementation class
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
 	 * The {@link org.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
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
 	 */
 	public static final String MULTI_TENANT = "hibernate.multiTenancy";
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
-	private static final boolean JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
-	private static final boolean JVM_HAS_JDK14_TIMESTAMP;
-	private static final boolean JVM_SUPPORTS_GET_GENERATED_KEYS;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Environment.class.getName());
 
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
                 LOG.unableToloadProperties();
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
-		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
-
-        if (ENABLE_BINARY_STREAMS) LOG.usingStreams();
-        if (ENABLE_REFLECTION_OPTIMIZER) LOG.usingReflectionOptimizer();
-		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
-
-		boolean getGeneratedKeysSupport;
-		try {
-			Statement.class.getMethod("getGeneratedKeys", (Class[])null);
-			getGeneratedKeysSupport = true;
+        if (ENABLE_BINARY_STREAMS) {
+			LOG.usingStreams();
 		}
-		catch (NoSuchMethodException nsme) {
-			getGeneratedKeysSupport = false;
-		}
-		JVM_SUPPORTS_GET_GENERATED_KEYS = getGeneratedKeysSupport;
-        if (!JVM_SUPPORTS_GET_GENERATED_KEYS) LOG.generatedKeysNotSupported();
 
-		boolean linkedHashSupport;
-		try {
-			Class.forName("java.util.LinkedHashSet");
-			linkedHashSupport = true;
-		}
-		catch (ClassNotFoundException cnfe) {
-			linkedHashSupport = false;
+		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
+        if (ENABLE_REFLECTION_OPTIMIZER) {
+			LOG.usingReflectionOptimizer();
 		}
-		JVM_SUPPORTS_LINKED_HASH_COLLECTIONS = linkedHashSupport;
-        if (!JVM_SUPPORTS_LINKED_HASH_COLLECTIONS) LOG.linkedMapsAndSetsNotSupported();
+
+		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
-        if (JVM_HAS_TIMESTAMP_BUG) LOG.usingTimestampWorkaround();
-
-		Timestamp t = new Timestamp(0);
-		t.setNanos(5 * 1000000);
-		JVM_HAS_JDK14_TIMESTAMP = t.getTime() == 5;
-        if (JVM_HAS_JDK14_TIMESTAMP) LOG.usingJdk14TimestampHandling();
-        else LOG.usingPreJdk14TimestampHandling();
+        if (JVM_HAS_TIMESTAMP_BUG) {
+			LOG.usingTimestampWorkaround();
+		}
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
-	 * Does this JVM handle {@link java.sql.Timestamp} in the JDK 1.4 compliant way wrt to nano rolling>
-	 *
-	 * @return True if the JDK 1.4 (JDBC3) specification for {@link java.sql.Timestamp} nano rolling is adhered to.
-	 *
-	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
-	 */
-	@Deprecated
-    public static boolean jvmHasJDK14Timestamp() {
-		return JVM_HAS_JDK14_TIMESTAMP;
-	}
-
-	/**
-	 * Does this JVM support {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap}?
-	 * <p/>
-	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
-	 *
-	 * @return True if {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap} are available.
-	 *
-	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
-	 * @see java.util.LinkedHashSet
-	 * @see java.util.LinkedHashMap
-	 */
-	@Deprecated
-    public static boolean jvmSupportsLinkedHashCollections() {
-		return JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
-	}
-
-	/**
-	 * Does this JDK/JVM define the JDBC {@link Statement} interface with a 'getGeneratedKeys' method?
-	 * <p/>
-	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
-	 *
-	 * @return True if generated keys can be retrieved via Statement; false otherwise.
-	 *
-	 * @see Statement
-	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
-	 */
-	@Deprecated
-    public static boolean jvmSupportsGetGeneratedKeys() {
-		return JVM_SUPPORTS_GET_GENERATED_KEYS;
-	}
-
-	/**
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 1ad46426c9..135175e19f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -364,2004 +364,2001 @@ public final class HbmBinder {
 		entity.setExplicitPolymorphism( ( polyNode != null )
 			&& polyNode.getValue().equals( "explicit" ) );
 
 		// ROW ID
 		Attribute rowidNode = node.attribute( "rowid" );
 		if ( rowidNode != null ) table.setRowId( rowidNode.getValue() );
 
 		Iterator subnodes = node.elementIterator();
 		while ( subnodes.hasNext() ) {
 
 			Element subnode = (Element) subnodes.next();
 			String name = subnode.getName();
 
 			if ( "id".equals( name ) ) {
 				// ID
 				bindSimpleId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "composite-id".equals( name ) ) {
 				// COMPOSITE-ID
 				bindCompositeId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "version".equals( name ) || "timestamp".equals( name ) ) {
 				// VERSION / TIMESTAMP
 				bindVersioningProperty( table, subnode, mappings, name, entity, inheritedMetas );
 			}
 			else if ( "discriminator".equals( name ) ) {
 				// DISCRIMINATOR
 				bindDiscriminatorProperty( table, entity, subnode, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				entity.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				entity.setCacheRegionName( subnode.attributeValue( "region" ) );
 				entity.setLazyPropertiesCacheable( !"non-lazy".equals( subnode.attributeValue( "include" ) ) );
 			}
 
 		}
 
 		// Primary key constraint
 		entity.createPrimaryKey();
 
 		createClassProperties( node, entity, mappings, inheritedMetas );
 	}
 
 	private static void bindSimpleId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 
 		SimpleValue id = new SimpleValue( mappings, entity.getTable() );
 		entity.setIdentifier( id );
 
 		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
 		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		// if ( !id.isTypeSpecified() ) {
 		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
 		// );
 		// }
 		// }
 		// else {
 		// bindSimpleValue( idNode, id, false, propertyName, mappings );
 		// PojoRepresentation pojo = entity.getPojoRepresentation();
 		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
 		//
 		// Property prop = new Property();
 		// prop.setValue( id );
 		// bindProperty( idNode, prop, mappings, inheritedMetas );
 		// entity.setIdentifierProperty( prop );
 		// }
 
 		if ( propertyName == null ) {
 			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		}
 		else {
 			bindSimpleValue( idNode, id, false, propertyName, mappings );
 		}
 
 		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 			if ( !id.isTypeSpecified() ) {
 				throw new MappingException( "must specify an identifier type: "
 					+ entity.getEntityName() );
 			}
 		}
 		else {
 			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 		}
 
 		if ( propertyName != null ) {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 		Component id = new Component( mappings, entity );
 		entity.setIdentifier( id );
 		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 		if ( propertyName == null ) {
 			entity.setEmbeddedIdentifier( id.isEmbedded() );
 			if ( id.isEmbedded() ) {
 				// todo : what is the implication of this?
 				id.setDynamic( !entity.hasPojoRepresentation() );
 				/*
 				 * Property prop = new Property(); prop.setName("id");
 				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 				 * entity.setIdentifierProperty(prop);
 				 */
 			}
 		}
 		else {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 		}
 
 		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private static void bindVersioningProperty(Table table, Element subnode, Mappings mappings,
 			String name, RootClass entity, java.util.Map inheritedMetas) {
 
 		String propertyName = subnode.attributeValue( "name" );
 		SimpleValue val = new SimpleValue( mappings, table );
 		bindSimpleValue( subnode, val, false, propertyName, mappings );
 		if ( !val.isTypeSpecified() ) {
 			// this is either a <version/> tag with no type attribute,
 			// or a <timestamp/> tag
 			if ( "version".equals( name ) ) {
 				val.setTypeName( "integer" );
 			}
 			else {
 				if ( "db".equals( subnode.attributeValue( "source" ) ) ) {
 					val.setTypeName( "dbtimestamp" );
 				}
 				else {
 					val.setTypeName( "timestamp" );
 				}
 			}
 		}
 		Property prop = new Property();
 		prop.setValue( val );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure...
 		if ( prop.getGeneration() == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		makeVersion( subnode, val );
 		entity.setVersion( prop );
 		entity.addProperty( prop );
 	}
 
 	private static void bindDiscriminatorProperty(Table table, RootClass entity, Element subnode,
 			Mappings mappings) {
 		SimpleValue discrim = new SimpleValue( mappings, table );
 		entity.setDiscriminator( discrim );
 		bindSimpleValue(
 				subnode,
 				discrim,
 				false,
 				RootClass.DEFAULT_DISCRIMINATOR_COLUMN_NAME,
 				mappings
 			);
 		if ( !discrim.isTypeSpecified() ) {
 			discrim.setTypeName( "string" );
 			// ( (Column) discrim.getColumnIterator().next() ).setType(type);
 		}
 		entity.setPolymorphic( true );
 		if ( "true".equals( subnode.attributeValue( "force" ) ) )
 			entity.setForceDiscriminator( true );
 		if ( "false".equals( subnode.attributeValue( "insert" ) ) )
 			entity.setDiscriminatorInsertable( false );
 	}
 
 	public static void bindClass(Element node, PersistentClass persistentClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		// transfer an explicitly defined entity name
 		// handle the lazy attribute
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean lazy = lazyNode == null ?
 				mappings.isDefaultLazy() :
 				"true".equals( lazyNode.getValue() );
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		persistentClass.setLazy( lazy );
 
 		String entityName = node.attributeValue( "entity-name" );
 		if ( entityName == null ) entityName = getClassName( node.attribute("name"), mappings );
 		if ( entityName==null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 		persistentClass.setEntityName( entityName );
 
 		bindPojoRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindDom4jRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindMapRepresentation( node, persistentClass, mappings, inheritedMetas );
 
 		Iterator itr = node.elementIterator( "fetch-profile" );
 		while ( itr.hasNext() ) {
 			final Element profileElement = ( Element ) itr.next();
 			parseFetchProfile( profileElement, mappings, entityName );
 		}
 
 		bindPersistentClassCommonValues( node, persistentClass, mappings, inheritedMetas );
 	}
 
 	private static void bindPojoRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map metaTags) {
 
 		String className = getClassName( node.attribute( "name" ), mappings );
 		String proxyName = getClassName( node.attribute( "proxy" ), mappings );
 
 		entity.setClassName( className );
 
 		if ( proxyName != null ) {
 			entity.setProxyInterfaceName( proxyName );
 			entity.setLazy( true );
 		}
 		else if ( entity.isLazy() ) {
 			entity.setProxyInterfaceName( className );
 		}
 
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.POJO );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.POJO, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	private static void bindDom4jRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = StringHelper.unqualify( entity.getEntityName() );
 		entity.setNodeName(nodeName);
 
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	private static void bindMapRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.MAP );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.MAP, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 * @return The tuplizer element, or null.
 	 */
 	private static Element locateTuplizerDefinition(Element container, EntityMode entityMode) {
 		Iterator itr = container.elements( "tuplizer" ).iterator();
 		while( itr.hasNext() ) {
 			final Element tuplizerElem = ( Element ) itr.next();
 			if ( entityMode.toString().equals( tuplizerElem.attributeValue( "entity-mode") ) ) {
 				return tuplizerElem;
 			}
 		}
 		return null;
 	}
 
 	private static void bindPersistentClassCommonValues(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		// DISCRIMINATOR
 		Attribute discriminatorNode = node.attribute( "discriminator-value" );
 		entity.setDiscriminatorValue( ( discriminatorNode == null )
 			? entity.getEntityName()
 			: discriminatorNode.getValue() );
 
 		// DYNAMIC UPDATE
 		Attribute dynamicNode = node.attribute( "dynamic-update" );
 		entity.setDynamicUpdate(
 				dynamicNode != null && "true".equals( dynamicNode.getValue() )
 		);
 
 		// DYNAMIC INSERT
 		Attribute insertNode = node.attribute( "dynamic-insert" );
 		entity.setDynamicInsert(
 				insertNode != null && "true".equals( insertNode.getValue() )
 		);
 
 		// IMPORT
 		mappings.addImport( entity.getEntityName(), entity.getEntityName() );
 		if ( mappings.isAutoImport() && entity.getEntityName().indexOf( '.' ) > 0 ) {
 			mappings.addImport(
 					entity.getEntityName(),
 					StringHelper.unqualify( entity.getEntityName() )
 				);
 		}
 
 		// BATCH SIZE
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) entity.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 
 		// SELECT BEFORE UPDATE
 		Attribute sbuNode = node.attribute( "select-before-update" );
 		if ( sbuNode != null ) entity.setSelectBeforeUpdate( "true".equals( sbuNode.getValue() ) );
 
 		// OPTIMISTIC LOCK MODE
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		entity.setOptimisticLockMode( getOptimisticLockMode( olNode ) );
 
 		entity.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				entity.setEntityPersisterClass( ReflectHelper.classForName(
 						persisterNode
 								.getValue()
 				) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, entity );
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			entity.addSynchronizedTable( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Attribute abstractNode = node.attribute( "abstract" );
 		Boolean isAbstract = abstractNode == null
 				? null
 		        : "true".equals( abstractNode.getValue() )
 						? Boolean.TRUE
 	                    : "false".equals( abstractNode.getValue() )
 								? Boolean.FALSE
 	                            : null;
 		entity.setAbstract( isAbstract );
 	}
 
 	private static void handleCustomSQL(Element node, PersistentClass model)
 			throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "loader" );
 		if ( element != null ) {
 			model.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Join model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Collection model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete-all" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDeleteAll( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static boolean isCallable(Element e) throws MappingException {
 		return isCallable( e, true );
 	}
 
 	private static boolean isCallable(Element element, boolean supportsCallable)
 			throws MappingException {
 		Attribute attrib = element.attribute( "callable" );
 		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
 			if ( !supportsCallable ) {
 				throw new MappingException( "callable attribute not supported yet!" );
 			}
 			return true;
 		}
 		return false;
 	}
 
 	private static ExecuteUpdateResultCheckStyle getResultCheckStyle(Element element, boolean callable) throws MappingException {
 		Attribute attr = element.attribute( "check" );
 		if ( attr == null ) {
 			// use COUNT as the default.  This mimics the old behavior, although
 			// NONE might be a better option moving forward in the case of callable
 			return ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		return ExecuteUpdateResultCheckStyle.parse( attr.getValue() );
 	}
 
 	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, unionSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( unionSubclass.getEntityPersisterClass() == null ) {
 			unionSubclass.getRootClass().setEntityPersisterClass(
 				UnionSubclassEntityPersister.class );
 		}
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table denormalizedSuperTable = unionSubclass.getSuperclass().getTable();
 		Table mytable = mappings.addDenormalizedTable(
 				schema,
 				catalog,
 				getClassTableName(unionSubclass, node, schema, catalog, denormalizedSuperTable, mappings ),
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract().booleanValue(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
         LOG.mappingUnionSubclass(unionSubclass.getEntityName(), unionSubclass.getTable().getName());
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( subclass.getEntityPersisterClass() == null ) {
 			subclass.getRootClass()
 					.setEntityPersisterClass( SingleTableEntityPersister.class );
 		}
 
         LOG.mappingSubclass(subclass.getEntityName(), subclass.getTable().getName());
 
 		// properties
 		createClassProperties( node, subclass, mappings, inheritedMetas );
 	}
 
 	private static String getClassTableName(
 			PersistentClass model, Element node, String schema, String catalog, Table denormalizedSuperTable,
 			Mappings mappings
 	) {
 		Attribute tableNameNode = node.attribute( "table" );
 		String logicalTableName;
 		String physicalTableName;
 		if ( tableNameNode == null ) {
 			logicalTableName = StringHelper.unqualify( model.getEntityName() );
 			physicalTableName = mappings.getNamingStrategy().classToTableName( model.getEntityName() );
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
 			physicalTableName = mappings.getNamingStrategy().tableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, joinedSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from
 																	// <joined-subclass>
 
 		// joined subclasses
 		if ( joinedSubclass.getEntityPersisterClass() == null ) {
 			joinedSubclass.getRootClass()
 				.setEntityPersisterClass( JoinedSubclassEntityPersister.class );
 		}
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table mytable = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( joinedSubclass, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 				false
 			);
 		joinedSubclass.setTable( mytable );
 		bindComment(mytable, node);
 
         LOG.mappingJoinedSubclass(joinedSubclass.getEntityName(), joinedSubclass.getTable().getName());
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, mytable, joinedSubclass.getIdentifier() );
 		joinedSubclass.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, joinedSubclass.getEntityName(), mappings );
 
 		// model.getKey().setType( new Type( model.getIdentifier() ) );
 		joinedSubclass.createPrimaryKey();
 		joinedSubclass.createForeignKey();
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) mytable.addCheckConstraint( chNode.getValue() );
 
 		// properties
 		createClassProperties( node, joinedSubclass, mappings, inheritedMetas );
 
 	}
 
 	private static void bindJoin(Element node, Join join, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		PersistentClass persistentClass = join.getPersistentClass();
 		String path = persistentClass.getEntityName();
 
 		// TABLENAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 		Table primaryTable = persistentClass.getTable();
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( persistentClass, node, schema, catalog, primaryTable, mappings ),
 				getSubselect( node ),
 				false
 			);
 		join.setTable( table );
 		bindComment(table, node);
 
 		Attribute fetchNode = node.attribute( "fetch" );
 		if ( fetchNode != null ) {
 			join.setSequentialSelect( "select".equals( fetchNode.getValue() ) );
 		}
 
 		Attribute invNode = node.attribute( "inverse" );
 		if ( invNode != null ) {
 			join.setInverse( "true".equals( invNode.getValue() ) );
 		}
 
 		Attribute nullNode = node.attribute( "optional" );
 		if ( nullNode != null ) {
 			join.setOptional( "true".equals( nullNode.getValue() ) );
 		}
 
 
         LOG.mappingClassJoin(persistentClass.getEntityName(), join.getTable().getName());
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, table, persistentClass.getIdentifier() );
 		join.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, persistentClass.getEntityName(), mappings );
 
 		// join.getKey().setType( new Type( lazz.getIdentifier() ) );
 		join.createPrimaryKey();
 		join.createForeignKey();
 
 		// PROPERTIES
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			Value value = null;
 			if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, true, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, true, propertyName, mappings );
 			}
 			else if ( "component".equals( name ) || "dynamic-component".equals( name ) ) {
 				String subpath = StringHelper.qualify( path, propertyName );
 				value = new Component( mappings, join );
 				bindComponent(
 						subnode,
 						(Component) value,
 						join.getPersistentClass().getClassName(),
 						propertyName,
 						subpath,
 						true,
 						false,
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 
 			if ( value != null ) {
 				Property prop = createProperty( value, propertyName, persistentClass
 					.getEntityName(), subnode, mappings, inheritedMetas );
 				prop.setOptional( join.isOptional() );
 				join.addProperty( prop );
 			}
 
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, join );
 
 	}
 
 	public static void bindColumns(final Element node, final SimpleValue simpleValue,
 			final boolean isNullable, final boolean autoColumn, final String propertyPath,
 			final Mappings mappings) throws MappingException {
 
 		Table table = simpleValue.getTable();
 
 		// COLUMN(S)
 		Attribute columnAttribute = node.attribute( "column" );
 		if ( columnAttribute == null ) {
 			Iterator itr = node.elementIterator();
 			int count = 0;
 			while ( itr.hasNext() ) {
 				Element columnElement = (Element) itr.next();
 				if ( columnElement.getName().equals( "column" ) ) {
 					Column column = new Column();
 					column.setValue( simpleValue );
 					column.setTypeIndex( count++ );
 					bindColumn( columnElement, column, isNullable );
 					final String columnName = columnElement.attributeValue( "name" );
 					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 							columnName, propertyPath
 					);
 					column.setName( mappings.getNamingStrategy().columnName(
 						columnName ) );
 					if ( table != null ) {
 						table.addColumn( column ); // table=null -> an association
 						                           // - fill it in later
 						//TODO fill in the mappings for table == null
 						mappings.addColumnBinding( logicalColumnName, column, table );
 					}
 
 
 					simpleValue.addColumn( column );
 					// column index
 					bindIndex( columnElement.attribute( "index" ), table, column, mappings );
 					bindIndex( node.attribute( "index" ), table, column, mappings );
 					//column unique-key
 					bindUniqueKey( columnElement.attribute( "unique-key" ), table, column, mappings );
 					bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 				}
 				else if ( columnElement.getName().equals( "formula" ) ) {
 					Formula formula = new Formula();
 					formula.setFormula( columnElement.getText() );
 					simpleValue.addFormula( formula );
 				}
 			}
 
 			// todo : another GoodThing would be to go back after all parsing and see if all the columns
 			// (and no formulas) are contained in a defined unique key that only contains these columns.
 			// That too would mark this as a logical one-to-one
 			final Attribute uniqueAttribute = node.attribute( "unique" );
 			if ( uniqueAttribute != null
 					&& "true".equals( uniqueAttribute.getValue() )
 					&& ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 		}
 		else {
 			if ( node.elementIterator( "column" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <column> subelement" );
 			}
 			if ( node.elementIterator( "formula" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <formula> subelement" );
 			}
 
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			if ( column.isUnique() && ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 			final String columnName = columnAttribute.getValue();
 			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 					columnName, propertyPath
 			);
 			column.setName( mappings.getNamingStrategy().columnName( columnName ) );
 			if ( table != null ) {
 				table.addColumn( column ); // table=null -> an association - fill
 				                           // it in later
 				//TODO fill in the mappings for table == null
 				mappings.addColumnBinding( logicalColumnName, column, table );
 			}
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 		if ( autoColumn && simpleValue.getColumnSpan() == 0 ) {
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			column.setName( mappings.getNamingStrategy().propertyToColumnName( propertyPath ) );
 			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
 			 * Right now HbmBinder does not support the
 			 */
 			simpleValue.getTable().addColumn( column );
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 	}
 
 	private static void bindIndex(Attribute indexAttribute, Table table, Column column, Mappings mappings) {
 		if ( indexAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( indexAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateIndex( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	private static void bindUniqueKey(Attribute uniqueKeyAttribute, Table table, Column column, Mappings mappings) {
 		if ( uniqueKeyAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( uniqueKeyAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateUniqueKey( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	// automatically makes a column with the default name if none is specifed by XML
 	public static void bindSimpleValue(Element node, SimpleValue simpleValue, boolean isNullable,
 			String path, Mappings mappings) throws MappingException {
 		bindSimpleValueType( node, simpleValue, mappings );
 
 		bindColumnsOrFormula( node, simpleValue, path, isNullable, mappings );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) simpleValue.setForeignKeyName( fkNode.getValue() );
 	}
 
 	private static void bindSimpleValueType(Element node, SimpleValue simpleValue, Mappings mappings)
 			throws MappingException {
 		String typeName = null;
 
 		Properties parameters = new Properties();
 
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode != null ) typeName = typeNode.getValue();
 
 		Element typeChild = node.element( "type" );
 		if ( typeName == null && typeChild != null ) {
 			typeName = typeChild.attribute( "name" ).getValue();
 			Iterator typeParameters = typeChild.elementIterator( "param" );
 
 			while ( typeParameters.hasNext() ) {
 				Element paramElement = (Element) typeParameters.next();
 				parameters.setProperty(
 						paramElement.attributeValue( "name" ),
 						paramElement.getTextTrim()
 					);
 			}
 		}
 
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
 		}
 
 		if ( !parameters.isEmpty() ) simpleValue.setTypeParameters( parameters );
 
 		if ( typeName != null ) simpleValue.setTypeName( typeName );
 	}
 
 	public static void bindProperty(
 			Element node,
 	        Property property,
 	        Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		String propName = node.attributeValue( "name" );
 		property.setName( propName );
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = propName;
 		property.setNodeName( nodeName );
 
 		// TODO:
 		//Type type = model.getValue().getType();
 		//if (type==null) throw new MappingException(
 		//"Could not determine a property type for: " + model.getName() );
 
 		Attribute accessNode = node.attribute( "access" );
 		if ( accessNode != null ) {
 			property.setPropertyAccessorName( accessNode.getValue() );
 		}
 		else if ( node.getName().equals( "properties" ) ) {
 			property.setPropertyAccessorName( "embedded" );
 		}
 		else {
 			property.setPropertyAccessorName( mappings.getDefaultAccess() );
 		}
 
 		Attribute cascadeNode = node.attribute( "cascade" );
 		property.setCascade( cascadeNode == null ? mappings.getDefaultCascade() : cascadeNode
 			.getValue() );
 
 		Attribute updateNode = node.attribute( "update" );
 		property.setUpdateable( updateNode == null || "true".equals( updateNode.getValue() ) );
 
 		Attribute insertNode = node.attribute( "insert" );
 		property.setInsertable( insertNode == null || "true".equals( insertNode.getValue() ) );
 
 		Attribute lockNode = node.attribute( "optimistic-lock" );
 		property.setOptimisticLocked( lockNode == null || "true".equals( lockNode.getValue() ) );
 
 		Attribute generatedNode = node.attribute( "generated" );
         String generationName = generatedNode == null ? null : generatedNode.getValue();
         PropertyGeneration generation = PropertyGeneration.parse( generationName );
 		property.setGeneration( generation );
 
         if ( generation == PropertyGeneration.ALWAYS || generation == PropertyGeneration.INSERT ) {
 	        // generated properties can *never* be insertable...
 	        if ( property.isInsertable() ) {
 		        if ( insertNode == null ) {
 			        // insertable simply because that is the user did not specify
 			        // anything; just override it
 					property.setInsertable( false );
 		        }
 		        else {
 			        // the user specifically supplied insert="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both insert=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
 
 	        // properties generated on update can never be updateable...
 	        if ( property.isUpdateable() && generation == PropertyGeneration.ALWAYS ) {
 		        if ( updateNode == null ) {
 			        // updateable only because the user did not specify
 			        // anything; just override it
 			        property.setUpdateable( false );
 		        }
 		        else {
 			        // the user specifically supplied update="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
         }
 
 		boolean isLazyable = "property".equals( node.getName() ) ||
 				"component".equals( node.getName() ) ||
 				"many-to-one".equals( node.getName() ) ||
 				"one-to-one".equals( node.getName() ) ||
 				"any".equals( node.getName() );
 		if ( isLazyable ) {
 			Attribute lazyNode = node.attribute( "lazy" );
 			property.setLazy( lazyNode != null && "true".equals( lazyNode.getValue() ) );
 		}
 
         if (LOG.isDebugEnabled()) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
             LOG.debugf(msg);
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuffer columns = new StringBuffer();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollection(Element node, Collection collection, String className,
 			String path, Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		// ROLENAME
 		collection.setRole(path);
 
 		Attribute inverseNode = node.attribute( "inverse" );
 		if ( inverseNode != null ) {
 			collection.setInverse( "true".equals( inverseNode.getValue() ) );
 		}
 
 		Attribute mutableNode = node.attribute( "mutable" );
 		if ( mutableNode != null ) {
 			collection.setMutable( !"false".equals( mutableNode.getValue() ) );
 		}
 
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );
 
 		Attribute orderNode = node.attribute( "order-by" );
 		if ( orderNode != null ) {
-			if ( Environment.jvmSupportsLinkedHashCollections() || ( collection instanceof Bag ) ) {
-				collection.setOrderBy( orderNode.getValue() );
-			}
- else LOG.attributeIgnored();
+			collection.setOrderBy( orderNode.getValue() );
 		}
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) {
 			collection.setWhere( whereNode.getValue() );
 		}
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) {
 			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		collection.setNodeName( nodeName );
 		String embed = node.attributeValue( "embed-xml" );
 		collection.setEmbedded( embed==null || "true".equals(embed) );
 
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				collection.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find collection persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		Attribute typeNode = node.attribute( "collection-type" );
 		if ( typeNode != null ) {
 			String typeName = typeNode.getValue();
 			TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 			else {
 				collection.setTypeName( typeName );
 			}
 		}
 
 		// FETCH STRATEGY
 
 		initOuterJoinFetchSetting( node, collection );
 
 		if ( "subselect".equals( node.attributeValue("fetch") ) ) {
 			collection.setSubselectLoadable(true);
 			collection.getOwner().setSubselectLoadableCollections(true);
 		}
 
 		initLaziness( node, collection, mappings, "true", mappings.isDefaultLazy() );
 		//TODO: suck this into initLaziness!
 		if ( "extra".equals( node.attributeValue("lazy") ) ) {
 			collection.setLazy(true);
 			collection.setExtraLazy(true);
 		}
 
 		Element oneToManyNode = node.element( "one-to-many" );
 		if ( oneToManyNode != null ) {
 			OneToMany oneToMany = new OneToMany( mappings, collection.getOwner() );
 			collection.setElement( oneToMany );
 			bindOneToMany( oneToManyNode, oneToMany, mappings );
 			// we have to set up the table later!! yuck
 		}
 		else {
 			// TABLE
 			Attribute tableNode = node.attribute( "table" );
 			String tableName;
 			if ( tableNode != null ) {
 				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				tableName = mappings.getNamingStrategy().collectionTableName(
 						collection.getOwner().getEntityName(),
 						logicalOwnerTableName ,
 						null,
 						null,
 						path
 				);
 				if ( ownerTable.isQuoted() ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 			Attribute schemaNode = node.attribute( "schema" );
 			String schema = schemaNode == null ?
 					mappings.getSchemaName() : schemaNode.getValue();
 
 			Attribute catalogNode = node.attribute( "catalog" );
 			String catalog = catalogNode == null ?
 					mappings.getCatalogName() : catalogNode.getValue();
 
 			Table table = mappings.addTable(
 					schema,
 					catalog,
 					tableName,
 					getSubselect( node ),
 					false
 				);
 			collection.setCollectionTable( table );
 			bindComment(table, node);
 
             LOG.mappingCollection(collection.getRole(), collection.getCollectionTable().getName());
 		}
 
 		// SORT
 		Attribute sortedAtt = node.attribute( "sort" );
 		// unsorted, natural, comparator.class.name
 		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
 			collection.setSorted( false );
 		}
 		else {
 			collection.setSorted( true );
 			String comparatorClassName = sortedAtt.getValue();
 			if ( !comparatorClassName.equals( "natural" ) ) {
 				collection.setComparatorClassName(comparatorClassName);
 			}
 		}
 
 		// ORPHAN DELETE (used for programmer error detection)
 		Attribute cascadeAtt = node.attribute( "cascade" );
 		if ( cascadeAtt != null && cascadeAtt.getValue().indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, collection );
 		// set up second pass
 		if ( collection instanceof List ) {
 			mappings.addSecondPass( new ListSecondPass( node, mappings, (List) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof Map ) {
 			mappings.addSecondPass( new MapSecondPass( node, mappings, (Map) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof IdentifierCollection ) {
 			mappings.addSecondPass( new IdentifierCollectionSecondPass(
 					node,
 					mappings,
 					collection,
 					inheritedMetas
 				) );
 		}
 		else {
 			mappings.addSecondPass( new CollectionSecondPass( node, mappings, collection, inheritedMetas ) );
 		}
 
 		Iterator iter = node.elementIterator( "filter" );
 		while ( iter.hasNext() ) {
 			final Element filter = (Element) iter.next();
 			parseFilter( filter, collection, mappings );
 		}
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			collection.getSynchronizedTables().add(
 				( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Element element = node.element( "loader" );
 		if ( element != null ) {
 			collection.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 
 		collection.setReferencedPropertyName( node.element( "key" ).attributeValue( "property-ref" ) );
 	}
 
 	private static void initLaziness(
 			Element node,
 			Fetchable fetchable,
 			Mappings mappings,
 			String proxyVal,
 			boolean defaultLazy
 	) {
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean isLazyTrue = lazyNode == null ?
 				defaultLazy && fetchable.isLazy() : //fetch="join" overrides default laziness
 				lazyNode.getValue().equals(proxyVal); //fetch="join" overrides default laziness
 		fetchable.setLazy( isLazyTrue );
 	}
 
 	private static void initLaziness(
 			Element node,
 			ToOne fetchable,
 			Mappings mappings,
 			boolean defaultLazy
 	) {
 		if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
 			fetchable.setUnwrapProxy(true);
 			fetchable.setLazy(true);
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness(node, fetchable, mappings, "proxy", defaultLazy);
 		}
 	}
 
 	private static void bindColumnsOrFormula(Element node, SimpleValue simpleValue, String path,
 			boolean isNullable, Mappings mappings) {
 		Attribute formulaNode = node.attribute( "formula" );
 		if ( formulaNode != null ) {
 			Formula f = new Formula();
 			f.setFormula( formulaNode.getText() );
 			simpleValue.addFormula( f );
 		}
 		else {
 			bindColumns( node, simpleValue, isNullable, true, path, mappings );
 		}
 	}
 
 	private static void bindComment(Table table, Element node) {
 		Element comment = node.element("comment");
 		if (comment!=null) table.setComment( comment.getTextTrim() );
 	}
 
 	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
 			boolean isNullable, Mappings mappings) throws MappingException {
 
 		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
 		initOuterJoinFetchSetting( node, manyToOne );
 		initLaziness( node, manyToOne, mappings, true );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) {
 			manyToOne.setReferencedPropertyName( ukName.getValue() );
 		}
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
 			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
 				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
 			}
 		}
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( !manyToOne.isLogicalOneToOne() ) {
 				throw new MappingException(
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		oneToOne.setEmbedded( "true".equals( node.attributeValue( "embed-xml" ) ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
 			);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName("_identifierMapper");
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
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
 				Property property = createProperty( value, propertyName, persistentClass
 					.getClassName(), subnode, mappings, inheritedMetas );
 				if ( !mutable ) property.setUpdateable(false);
 				if ( naturalId ) property.setNaturalIdentifier(true);
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java
index 9d3b568790..a0475a12dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SetBinder.java
@@ -1,61 +1,59 @@
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
-import org.hibernate.internal.CoreMessageLogger;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.annotations.OrderBy;
-import org.hibernate.cfg.Environment;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 
-import org.jboss.logging.Logger;
-
 /**
  * Bind a set.
  *
  * @author Matthew Inger
  */
 public class SetBinder extends CollectionBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SetBinder.class.getName());
 
 	public SetBinder() {
 	}
 
 	public SetBinder(boolean sorted) {
 		super( sorted );
 	}
 
 	@Override
     protected Collection createCollection(PersistentClass persistentClass) {
 		return new org.hibernate.mapping.Set( getMappings(), persistentClass );
 	}
 
 	@Override
     public void setSqlOrderBy(OrderBy orderByAnn) {
-		// *annotation* binder, jdk 1.5, ... am i missing something?
 		if ( orderByAnn != null ) {
-            if (Environment.jvmSupportsLinkedHashCollections()) super.setSqlOrderBy(orderByAnn);
-            else LOG.orderByAttributeIgnored();
+            super.setSqlOrderBy( orderByAnn );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index 77e761bb35..d7cd094a6d 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -1,1873 +1,1849 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 
 import static org.jboss.logging.Logger.Level.ERROR;
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.util.Hashtable;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import javax.naming.InvalidNameException;
 import javax.naming.NameNotFoundException;
 import javax.naming.NamingException;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.loading.CollectionLoadContext;
 import org.hibernate.engine.loading.EntityLoadContext;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.service.jdbc.dialect.internal.AbstractDialectResolver;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.jboss.logging.BasicLogger;
 import org.jboss.logging.Cause;
 import org.jboss.logging.LogMessage;
 import org.jboss.logging.Message;
 import org.jboss.logging.MessageLogger;
 
 /**
  * The jboss-logging {@link MessageLogger} for the hibernate-core module.  It reserves message ids ranging from
  * 00001 to 10000 inclusively.
  * <p/>
  * New messages must be added after the last message defined to ensure message codes are unique.
  */
 @MessageLogger( projectCode = "HHH" )
 public interface CoreMessageLogger extends BasicLogger {
 
     @LogMessage( level = INFO )
     @Message( value = "Adding secondary table to entity %s -> %s", id = 1 )
     void addingSecondaryTableToEntity( String entity,
                                        String table );
 
     @LogMessage( level = WARN )
     @Message( value = "Already session bound on call to bind(); make sure you clean up your sessions!", id = 2 )
     void alreadySessionBound();
 
     @LogMessage( level = WARN )
     @Message( value = "Placing @Access(AccessType.%s) on a field does not have any effect.", id = 3 )
     void annotationHasNoEffect( AccessType type );
 
     @LogMessage( level = WARN )
     @Message( value = "Attempt to map column [%s] to no target column after explicit target column(s) named for FK [name=%s]", id = 4 )
     void attemptToMapColumnToNoTargetColumn( String loggableString,
                                              String name );
 
-    @LogMessage( level = WARN )
-    @Message( value = "Attribute \"order-by\" ignored in JDK1.3 or less", id = 5 )
-    void attributeIgnored();
-
     @LogMessage( level = INFO )
     @Message( value = "Autocommit mode: %s", id = 6 )
     void autoCommitMode( boolean autocommit );
 
     @LogMessage( level = INFO )
     @Message( value = "Automatic flush during beforeCompletion(): %s", id = 7 )
     void autoFlush( String enabledDisabled );
 
     @LogMessage( level = WARN )
     @Message( value = "JTASessionContext being used with JDBCTransactionFactory; auto-flush will not operate correctly with getCurrentSession()", id = 8 )
     void autoFlushWillNotWork();
 
     @LogMessage( level = INFO )
     @Message( value = "Automatic session close at end of transaction: %s", id = 9 )
     void autoSessionClose( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "On release of batch it still contained JDBC statements", id = 10 )
     void batchContainedStatementsOnRelease();
 
     @LogMessage( level = INFO )
     @Message( value = "Batcher factory: %s", id = 11 )
     void batcherFactory( String batcherClass );
 
     @LogMessage( level = INFO )
     @Message( value = "Bind entity %s on table %s", id = 12 )
     void bindEntityOnTable( String entity,
                             String table );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding Any Meta definition: %s", id = 13 )
     void bindingAnyMetaDefinition( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding entity from annotated class: %s", id = 14 )
     void bindingEntityFromClass( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding filter definition: %s", id = 15 )
     void bindingFilterDefinition( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding named native query: %s => %s", id = 16 )
     void bindingNamedNativeQuery( String name,
                                   String query );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding named query: %s => %s", id = 17 )
     void bindingNamedQuery( String name,
                             String query );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding result set mapping: %s", id = 18 )
     void bindingResultSetMapping( String mapping );
 
     @LogMessage( level = INFO )
     @Message( value = "Binding type definition: %s", id = 19 )
     void bindingTypeDefinition( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Building session factory", id = 20 )
     void buildingSessionFactory();
 
     @LogMessage( level = INFO )
     @Message( value = "Bytecode provider name : %s", id = 21 )
     void bytecodeProvider( String provider );
 
     @LogMessage( level = WARN )
     @Message( value = "c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.", id = 22 )
     void c3p0ProviderClassNotFound( String c3p0ProviderClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "I/O reported cached file could not be found : %s : %s", id = 23 )
     void cachedFileNotFound( String path,
                              FileNotFoundException error );
 
     @LogMessage( level = INFO )
     @Message( value = "Cache provider: %s", id = 24 )
     void cacheProvider( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Cache region factory : %s", id = 25 )
     void cacheRegionFactory( String regionFactoryClassName );
 
     @LogMessage( level = INFO )
     @Message( value = "Cache region prefix: %s", id = 26 )
     void cacheRegionPrefix( String prefix );
 
     @LogMessage( level = WARN )
     @Message( value = "Calling joinTransaction() on a non JTA EntityManager", id = 27 )
     void callingJoinTransactionOnNonJtaEntityManager();
 
     @Message( value = "CGLIB Enhancement failed: %s", id = 28 )
     String cglibEnhancementFailed( String entityName );
 
     @LogMessage( level = INFO )
     @Message( value = "Check Nullability in Core (should be disabled when Bean Validation is on): %s", id = 29 )
     void checkNullability( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Cleaning up connection pool [%s]", id = 30 )
     void cleaningUpConnectionPool( String url );
 
     @LogMessage( level = INFO )
     @Message( value = "Closing", id = 31 )
     void closing();
 
     @LogMessage( level = INFO )
     @Message( value = "Collections fetched (minimize this): %s", id = 32 )
     void collectionsFetched( long collectionFetchCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections loaded: %s", id = 33 )
     void collectionsLoaded( long collectionLoadCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections recreated: %s", id = 34 )
     void collectionsRecreated( long collectionRecreateCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections removed: %s", id = 35 )
     void collectionsRemoved( long collectionRemoveCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Collections updated: %s", id = 36 )
     void collectionsUpdated( long collectionUpdateCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Columns: %s", id = 37 )
     void columns( Set keySet );
 
     @LogMessage( level = WARN )
     @Message( value = "Composite-id class does not override equals(): %s", id = 38 )
     void compositeIdClassDoesNotOverrideEquals( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Composite-id class does not override hashCode(): %s", id = 39 )
     void compositeIdClassDoesNotOverrideHashCode( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuration resource: %s", id = 40 )
     void configurationResource( String resource );
 
     @LogMessage( level = INFO )
     @Message( value = "Configured SessionFactory: %s", id = 41 )
     void configuredSessionFactory( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from file: %s", id = 42 )
     void configuringFromFile( String file );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from resource: %s", id = 43 )
     void configuringFromResource( String resource );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from URL: %s", id = 44 )
     void configuringFromUrl( URL url );
 
     @LogMessage( level = INFO )
     @Message( value = "Configuring from XML document", id = 45 )
     void configuringFromXmlDocument();
 
     @LogMessage( level = INFO )
     @Message( value = "Connection properties: %s", id = 46 )
     void connectionProperties( Properties connectionProps );
 
     @LogMessage( level = INFO )
     @Message( value = "Connection release mode: %s", id = 47 )
     void connectionReleaseMode( String releaseModeName );
 
     @LogMessage( level = INFO )
     @Message( value = "Connections obtained: %s", id = 48 )
     void connectionsObtained( long connectCount );
 
     @LogMessage( level = INFO )
     @Message( value = "%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.", id = 49 )
     void constructorWithPropertiesNotFound( String regionFactoryClassName );
 
     @LogMessage( level = ERROR )
     @Message( value = "Container is providing a null PersistenceUnitRootUrl: discovery impossible", id = 50 )
     void containerProvidingNullPersistenceUnitRootUrl();
 
     @LogMessage( level = WARN )
     @Message( value = "Ignoring bag join fetch [%s] due to prior collection join fetch", id = 51 )
     void containsJoinFetchedCollection( String role );
 
     @Message( value = "Could not close connection", id = 52 )
     Object couldNotCloseConnection();
 
     @LogMessage( level = INFO )
     @Message( value = "Creating subcontext: %s", id = 53 )
     void creatingSubcontextInfo( String intermediateContextName );
 
     @LogMessage( level = INFO )
     @Message( value = "Database ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s", id = 54 )
     void database( String databaseProductName,
                    String databaseProductVersion,
                    int databaseMajorVersion,
                    int databaseMinorVersion );
 
     @LogMessage( level = INFO )
     @Message( value = "Default batch fetch size: %s", id = 55 )
     void defaultBatchFetchSize( int batchFetchSize );
 
     @LogMessage( level = INFO )
     @Message( value = "Default catalog: %s", id = 56 )
     void defaultCatalog( String defaultCatalog );
 
     @LogMessage( level = INFO )
     @Message( value = "Default entity-mode: %s", id = 57 )
     void defaultEntityMode( EntityMode defaultEntityMode );
 
     @LogMessage( level = INFO )
     @Message( value = "Default schema: %s", id = 58 )
     void defaultSchema( String defaultSchema );
 
     @LogMessage( level = WARN )
     @Message( value = "Defining %s=true ignored in HEM", id = 59 )
     void definingFlushBeforeCompletionIgnoredInHem( String flushBeforeCompletion );
 
     @LogMessage( level = INFO )
     @Message( value = "Deleted entity synthetic identifier rollback: %s", id = 60 )
     void deletedEntitySyntheticIdentifierRollback( String enabledDisabled );
 
     @LogMessage( level = WARN )
     @Message( value = "Per HHH-5451 support for cglib as a bytecode provider has been deprecated.", id = 61 )
     void deprecated();
 
     @LogMessage( level = WARN )
     @Message( value = "@ForceDiscriminator is deprecated use @DiscriminatorOptions instead.", id = 62 )
     void deprecatedForceDescriminatorAnnotation();
 
     @LogMessage( level = WARN )
     @Message( value = "The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead", id = 63 )
     void deprecatedOracle9Dialect();
 
     @LogMessage( level = WARN )
     @Message( value = "The OracleDialect dialect has been deprecated; use Oracle8iDialect instead", id = 64 )
     void deprecatedOracleDialect();
 
     @LogMessage( level = WARN )
     @Message( value = "DEPRECATED : use {} instead with custom {} implementation", id = 65 )
     void deprecatedUuidGenerator( String name,
                                   String name2 );
 
     @LogMessage( level = WARN )
     @Message( value = "Dialect resolver class not found: %s", id = 66 )
     void dialectResolverNotFound( String resolverName );
 
     @LogMessage( level = INFO )
     @Message( value = "Disallowing insert statement comment for select-identity due to Oracle driver bug", id = 67 )
     void disallowingInsertStatementComment();
 
     @LogMessage( level = INFO )
     @Message( value = "Driver ->\n" + "       name : %s\n" + "    version : %s\n" + "      major : %s\n" + "      minor : %s", id = 68 )
     void driver( String driverProductName,
                  String driverProductVersion,
                  int driverMajorVersion,
                  int driverMinorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "Duplicate generator name %s", id = 69 )
     void duplicateGeneratorName( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Duplicate generator table: %s", id = 70 )
     void duplicateGeneratorTable( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Duplicate import: %s -> %s", id = 71 )
     void duplicateImport( String entityName,
                           String rename );
 
     @LogMessage( level = WARN )
     @Message( value = "Duplicate joins for class: %s", id = 72 )
     void duplicateJoins( String entityName );
 
     @LogMessage( level = INFO )
     @Message( value = "entity-listener duplication, first event definition will be used: %s", id = 73 )
     void duplicateListener( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Found more than one <persistence-unit-metadata>, subsequent ignored", id = 74 )
     void duplicateMetadata();
 
     @LogMessage( level = INFO )
     @Message( value = "Echoing all SQL to stdout", id = 75 )
     void echoingSql();
 
     @LogMessage( level = INFO )
     @Message( value = "Entities deleted: %s", id = 76 )
     void entitiesDeleted( long entityDeleteCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities fetched (minimize this): %s", id = 77 )
     void entitiesFetched( long entityFetchCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities inserted: %s", id = 78 )
     void entitiesInserted( long entityInsertCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities loaded: %s", id = 79 )
     void entitiesLoaded( long entityLoadCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Entities updated: %s", id = 80 )
     void entitiesUpdated( long entityUpdateCount );
 
     @LogMessage( level = WARN )
     @Message( value = "@org.hibernate.annotations.Entity used on a non root entity: ignored for %s", id = 81 )
     void entityAnnotationOnNonRoot( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Entity Manager closed by someone else (%s must not be used)", id = 82 )
     void entityManagerClosedBySomeoneElse( String autoCloseSession );
 
     @LogMessage( level = INFO )
     @Message( value = "Hibernate EntityManager %s", id = 83 )
     void entityManagerVersion( String versionString );
 
     @LogMessage( level = WARN )
     @Message( value = "Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names", id = 84 )
     void entityMappedAsNonAbstract( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "%s %s found", id = 85 )
     void exceptionHeaderFound( String exceptionHeader,
                                String metaInfOrmXml );
 
     @LogMessage( level = INFO )
     @Message( value = "%s No %s found", id = 86 )
     void exceptionHeaderNotFound( String exceptionHeader,
                                   String metaInfOrmXml );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception in interceptor afterTransactionCompletion()", id = 87 )
     void exceptionInAfterTransactionCompletionInterceptor( @Cause Throwable e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception in interceptor beforeTransactionCompletion()", id = 88 )
     void exceptionInBeforeTransactionCompletionInterceptor( @Cause Throwable e );
 
     @LogMessage( level = INFO )
     @Message( value = "Sub-resolver threw unexpected exception, continuing to next : %s", id = 89 )
     void exceptionInSubResolver( String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Executing import script: %s", id = 90 )
     void executingImportScript( String name );
 
     @LogMessage( level = ERROR )
     @Message( value = "Expected type: %s, actual value: %s", id = 91 )
     void expectedType( String name,
                        String string );
 
     @LogMessage( level = WARN )
     @Message( value = "An item was expired by the cache while it was locked (increase your cache timeout): %s", id = 92 )
     void expired( Object key );
 
     @LogMessage( level = INFO )
     @Message( value = "Exporting generated schema to database", id = 93 )
     void exportingGeneratedSchemaToDatabase();
 
     @LogMessage( level = INFO )
     @Message( value = "Bound factory to JNDI name: %s", id = 94 )
     void factoryBoundToJndiName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Factory name: %s", id = 95 )
     void factoryName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "A factory was renamed from name: %s", id = 96 )
     void factoryRenamedFromName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Unbound factory from JNDI name: %s", id = 97 )
     void factoryUnboundFromJndiName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "A factory was unbound from name: %s", id = 98 )
     void factoryUnboundFromName( String name );
 
     @LogMessage( level = ERROR )
     @Message( value = "an assertion failure occured" + " (this may indicate a bug in Hibernate, but is more likely due"
                       + " to unsafe use of the session): %s", id = 99 )
     void failed( Throwable throwable );
 
     @LogMessage( level = WARN )
     @Message( value = "Fail-safe cleanup (collections) : %s", id = 100 )
     void failSafeCollectionsCleanup( CollectionLoadContext collectionLoadContext );
 
     @LogMessage( level = WARN )
     @Message( value = "Fail-safe cleanup (entities) : %s", id = 101 )
     void failSafeEntitiesCleanup( EntityLoadContext entityLoadContext );
 
     @LogMessage( level = INFO )
     @Message( value = "Fetching database metadata", id = 102 )
     void fetchingDatabaseMetadata();
 
     @LogMessage( level = WARN )
     @Message( value = "@Filter not allowed on subclasses (ignored): %s", id = 103 )
     void filterAnnotationOnSubclass( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "firstResult/maxResults specified with collection fetch; applying in memory!", id = 104 )
     void firstOrMaxResultsSpecifiedWithCollectionFetch();
 
     @LogMessage( level = INFO )
     @Message( value = "Flushes: %s", id = 105 )
     void flushes( long flushCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Forcing container resource cleanup on transaction completion", id = 106 )
     void forcingContainerResourceCleanup();
 
     @LogMessage( level = INFO )
     @Message( value = "Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences", id = 107 )
     void forcingTableUse();
 
     @LogMessage( level = INFO )
     @Message( value = "Foreign keys: %s", id = 108 )
     void foreignKeys( Set keySet );
 
     @LogMessage( level = INFO )
     @Message( value = "Found mapping document in jar: %s", id = 109 )
     void foundMappingDocument( String name );
 
     @LogMessage( level = INFO )
-    @Message( value = "JVM does not support Statement.getGeneratedKeys()", id = 110 )
-    void generatedKeysNotSupported();
-
-    @LogMessage( level = INFO )
     @Message( value = "Generate SQL with comments: %s", id = 111 )
     void generateSqlWithComments( String enabledDisabled );
 
     @LogMessage( level = ERROR )
     @Message( value = "Getters of lazy classes cannot be final: %s.%s", id = 112 )
     void gettersOfLazyClassesCannotBeFinal( String entityName,
                                             String name );
 
     @LogMessage( level = WARN )
     @Message( value = "GUID identifier generated: %s", id = 113 )
     void guidGenerated( String result );
 
     @LogMessage( level = INFO )
     @Message( value = "Handling transient entity in delete processing", id = 114 )
     void handlingTransientEntity();
 
     @LogMessage( level = INFO )
     @Message( value = "Hibernate connection pool size: %s", id = 115 )
     void hibernateConnectionPoolSize( int poolSize );
 
     @LogMessage( level = WARN )
     @Message( value = "Config specified explicit optimizer of [%s], but [%s=%s; honoring optimizer setting", id = 116 )
     void honoringOptimizerSetting( String none,
                                    String incrementParam,
                                    int incrementSize );
 
     @LogMessage( level = INFO )
     @Message( value = "HQL: %s, time: %sms, rows: %s", id = 117 )
     void hql( String hql,
               Long valueOf,
               Long valueOf2 );
 
     @LogMessage( level = WARN )
     @Message( value = "HSQLDB supports only READ_UNCOMMITTED isolation", id = 118 )
     void hsqldbSupportsOnlyReadCommittedIsolation();
 
     @LogMessage( level = WARN )
     @Message( value = "On EntityLoadContext#clear, hydratingEntities contained [%s] entries", id = 119 )
     void hydratingEntitiesCount( int size );
 
     @LogMessage( level = WARN )
     @Message( value = "Ignoring unique constraints specified on table generator [%s]", id = 120 )
     void ignoringTableGeneratorConstraints( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Ignoring unrecognized query hint [%s]", id = 121 )
     void ignoringUnrecognizedQueryHint( String hintName );
 
     @LogMessage( level = ERROR )
     @Message( value = "IllegalArgumentException in class: %s, getter method of property: %s", id = 122 )
     void illegalPropertyGetterArgument( String name,
                                         String propertyName );
 
     @LogMessage( level = ERROR )
     @Message( value = "IllegalArgumentException in class: %s, setter method of property: %s", id = 123 )
     void illegalPropertySetterArgument( String name,
                                         String propertyName );
 
     @LogMessage( level = WARN )
     @Message( value = "@Immutable used on a non root entity: ignored for %s", id = 124 )
     void immutableAnnotationOnNonRoot( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Mapping metadata cache was not completely processed", id = 125 )
     void incompleteMappingMetadataCacheProcessing();
 
     @LogMessage( level = INFO )
     @Message( value = "Indexes: %s", id = 126 )
     void indexes( Set keySet );
 
     @LogMessage( level = WARN )
     @Message( value = "InitialContext did not implement EventContext", id = 127 )
     void initialContextDidNotImplementEventContext();
 
     @LogMessage( level = WARN )
     @Message( value = "InitialContext did not implement EventContext", id = 128 )
     void initialContextDoesNotImplementEventContext();
 
     @LogMessage( level = INFO )
     @Message( value = "Instantiated TransactionManagerLookup", id = 129 )
     void instantiatedTransactionManagerLookup();
 
     @LogMessage( level = INFO )
     @Message( value = "Instantiating explicit connection provider: %s", id = 130 )
     void instantiatingExplicitConnectinProvider( String providerClassName );
 
     @LogMessage( level = INFO )
     @Message( value = "Instantiating TransactionManagerLookup: %s", id = 131 )
     void instantiatingTransactionManagerLookup( String tmLookupClass );
 
     @LogMessage( level = ERROR )
     @Message( value = "Array element type error\n%s", id = 132 )
     void invalidArrayElementType( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s", id = 133 )
     void invalidDescriminatorAnnotation( String className );
 
     @LogMessage( level = ERROR )
     @Message( value = "Application attempted to edit read only item: %s", id = 134 )
     void invalidEditOfReadOnlyItem( Object key );
 
     @LogMessage( level = ERROR )
     @Message( value = "Invalid JNDI name: %s", id = 135 )
     void invalidJndiName( String name,
                           @Cause InvalidNameException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Inapropriate use of @OnDelete on entity, annotation ignored: %s", id = 136 )
     void invalidOnDeleteAnnotation( String entityName );
 
     @LogMessage( level = WARN )
     @Message( value = "Root entity should not hold an PrimaryKeyJoinColum(s), will be ignored", id = 137 )
     void invalidPrimaryKeyJoinColumnAnnotation();
 
     @LogMessage( level = WARN )
     @Message( value = "Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s", id = 138 )
     void invalidSubStrategy( String className );
 
     @LogMessage( level = WARN )
     @Message( value = "Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s", id = 139 )
     void invalidTableAnnotation( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "JACC contextID: %s", id = 140 )
     void jaccContextId( String contextId );
 
     @LogMessage( level = INFO )
     @Message( value = "java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]", id = 141 )
     void JavaSqlTypesMappedSameCodeMultipleTimes( int code,
                                                   String old,
                                                   String name );
 
     @Message( value = "Javassist Enhancement failed: %s", id = 142 )
     String javassistEnhancementFailed( String entityName );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC3 getGeneratedKeys(): %s", id = 143 )
     void jdbc3GeneratedKeys( String enabledDisabled );
 
     @LogMessage( level = WARN )
     @Message( value = "%s = false breaks the EJB3 specification", id = 144 )
     void jdbcAutoCommitFalseBreaksEjb3Spec( String autocommit );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC batch size: %s", id = 145 )
     void jdbcBatchSize( int batchSize );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC batch updates for versioned data: %s", id = 146 )
     void jdbcBatchUpdates( String enabledDisabled );
 
     @Message( value = "JDBC begin failed", id = 147 )
     String jdbcBeginFailed();
 
     @LogMessage( level = WARN )
     @Message( value = "No JDBC Driver class was specified by property %s", id = 148 )
     void jdbcDriverNotSpecified( String driver );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC isolation level: %s", id = 149 )
     void jdbcIsolationLevel( String isolationLevelToString );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC result set fetch size: %s", id = 150 )
     void jdbcResultSetFetchSize( Integer statementFetchSize );
 
     @Message( value = "JDBC rollback failed", id = 151 )
     String jdbcRollbackFailed();
 
     @Message( value = "JDBC URL was not specified by property %s", id = 152 )
     String jdbcUrlNotSpecified( String url );
 
     @LogMessage( level = INFO )
     @Message( value = "JDBC version : %s.%s", id = 153 )
     void jdbcVersion( int jdbcMajorVersion,
                       int jdbcMinorVersion );
 
     @LogMessage( level = INFO )
     @Message( value = "JNDI InitialContext properties:%s", id = 154 )
     void jndiInitialContextProperties( Hashtable hash );
 
     @LogMessage( level = ERROR )
     @Message( value = "JNDI name %s does not handle a session factory reference", id = 155 )
     void jndiNameDoesNotHandleSessionFactoryReference( String sfJNDIName,
                                                        @Cause ClassCastException e );
 
     @LogMessage( level = INFO )
     @Message( value = "JPA-QL strict compliance: %s", id = 156 )
     void jpaQlStrictCompliance( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Lazy property fetching available for: %s", id = 157 )
     void lazyPropertyFetchingAvailable( String name );
 
-    @LogMessage( level = INFO )
-    @Message( value = "JVM does not support LinkedHashMap, LinkedHashSet - ordered maps and sets disabled", id = 158 )
-    void linkedMapsAndSetsNotSupported();
-
     @LogMessage( level = WARN )
     @Message( value = "In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts", id = 159 )
     void loadingCollectionKeyNotFound( CollectionKey collectionKey );
 
     @LogMessage( level = WARN )
     @Message( value = "On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries", id = 160 )
     void localLoadingCollectionKeysCount( int size );
 
     @LogMessage( level = INFO )
     @Message( value = "Logging statistics....", id = 161 )
     void loggingStatistics();
 
     @LogMessage( level = INFO )
     @Message( value = "*** Logical connection closed ***", id = 162 )
     void logicalConnectionClosed();
 
     @LogMessage( level = INFO )
     @Message( value = "Logical connection releasing its physical connection", id = 163 )
     void logicalConnectionReleasingPhysicalConnection();
 
     @LogMessage( level = WARN )
     @Message( value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 164 )
     void managerLookupClassShouldBeSet();
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping class: %s -> %s", id = 165 )
     void mappingClass( String entityName,
                        String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping class join: %s -> %s", id = 166 )
     void mappingClassJoin( String entityName,
                            String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping collection: %s -> %s", id = 167 )
     void mappingCollection( String name1,
                             String name2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping joined-subclass: %s -> %s", id = 168 )
     void mappingJoinedSubclass( String entityName,
                                 String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping Package %s", id = 169 )
     void mappingPackage( String packageName );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping subclass: %s -> %s", id = 170 )
     void mappingSubclass( String entityName,
                           String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Mapping union-subclass: %s -> %s", id = 171 )
     void mappingUnionSubclass( String entityName,
                                String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Maximum outer join fetch depth: %s", id = 172 )
     void maxOuterJoinFetchDepth( Integer maxFetchDepth );
 
     @LogMessage( level = INFO )
     @Message( value = "Max query time: %sms", id = 173 )
     void maxQueryTime( long queryExecutionMaxTime );
 
     @LogMessage( level = WARN )
     @Message( value = "Function template anticipated %s arguments, but %s arguments encountered", id = 174 )
     void missingArguments( int anticipatedNumberOfArguments,
                            int numberOfArguments );
 
     @LogMessage( level = WARN )
     @Message( value = "Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s", id = 175 )
     void missingEntityAnnotation( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Named query checking : %s", id = 176 )
     void namedQueryChecking( String enabledDisabled );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error in named query: %s", id = 177 )
     void namedQueryError( String queryName,
                           @Cause HibernateException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Naming exception occurred accessing factory: %s", id = 178 )
     void namingExceptionAccessingFactory( NamingException exception );
 
     @LogMessage( level = WARN )
     @Message( value = "Narrowing proxy to %s - this operation breaks ==", id = 179 )
     void narrowingProxy( Class concreteProxyClass );
 
     @LogMessage( level = WARN )
     @Message( value = "FirstResult/maxResults specified on polymorphic query; applying in memory!", id = 180 )
     void needsLimit();
 
     @LogMessage( level = WARN )
     @Message( value = "No appropriate connection provider encountered, assuming application will be supplying connections", id = 181 )
     void noAppropriateConnectionProvider();
 
     @LogMessage( level = INFO )
     @Message( value = "No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)", id = 182 )
     void noDefaultConstructor( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "no persistent classes found for query class: %s", id = 183 )
     void noPersistentClassesFound( String query );
 
     @LogMessage( level = ERROR )
     @Message( value = "No session factory with JNDI name %s", id = 184 )
     void noSessionFactoryWithJndiName( String sfJNDIName,
                                        @Cause NameNotFoundException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Not binding factory to JNDI, no JNDI name configured", id = 185 )
     void notBindingFactoryToJndi();
 
     @LogMessage( level = INFO )
     @Message( value = "Obtaining TransactionManager", id = 186 )
     void obtainingTransactionManager();
 
     @LogMessage( level = INFO )
     @Message( value = "Optimistic lock failures: %s", id = 187 )
     void optimisticLockFailures( long optimisticFailureCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Optimize cache for minimal puts: %s", id = 188 )
     void optimizeCacheForMinimalInputs( String enabledDisabled );
 
     @LogMessage( level = WARN )
     @Message( value = "@OrderBy not allowed for an indexed collection, annotation ignored.", id = 189 )
     void orderByAnnotationIndexedCollection();
 
-    @LogMessage( level = WARN )
-    @Message( value = "Attribute \"order-by\" ignored in JDK1.3 or less", id = 190 )
-    void orderByAttributeIgnored();
-
     @LogMessage( level = INFO )
     @Message( value = "Order SQL inserts for batching: %s", id = 191 )
     void orderSqlInsertsForBatching( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Order SQL updates by primary key: %s", id = 192 )
     void orderSqlUpdatesByPrimaryKey( String enabledDisabled );
 
     @LogMessage( level = WARN )
     @Message( value = "Overriding %s is dangerous, this might break the EJB3 specification implementation", id = 193 )
     void overridingTransactionStrategyDangerous( String transactionStrategy );
 
     @LogMessage( level = WARN )
     @Message( value = "Package not found or wo package-info.java: %s", id = 194 )
     void packageNotFound( String packageName );
 
     @LogMessage( level = WARN )
     @Message( value = "Parameter position [%s] occurred as both JPA and Hibernate positional parameter", id = 195 )
     void parameterPositionOccurredAsBothJpaAndHibernatePositionalParameter( Integer position );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error parsing XML (%s) : %s", id = 196 )
     void parsingXmlError( int lineNumber,
                           String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error parsing XML: %s(%s) %s", id = 197 )
     void parsingXmlErrorForFile( String file,
                                  int lineNumber,
                                  String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Warning parsing XML (%s) : %s", id = 198 )
     void parsingXmlWarning( int lineNumber,
                             String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Warning parsing XML: %s(%s) %s", id = 199 )
     void parsingXmlWarningForFile( String file,
                                    int lineNumber,
                                    String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Persistence provider caller does not implement the EJB3 spec correctly."
                       + "PersistenceUnitInfo.getNewTempClassLoader() is null.", id = 200 )
     void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 
     @LogMessage( level = INFO )
     @Message( value = "Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended", id = 201 )
     void pooledOptimizerReportedInitialValue( IntegralDataTypeHolder value );
 
     @LogMessage( level = ERROR )
     @Message( value = "PreparedStatement was already in the batch, [%s].", id = 202 )
     void preparedStatementAlreadyInBatch( String sql );
 
     @LogMessage( level = WARN )
     @Message( value = "processEqualityExpression() : No expression to process!", id = 203 )
     void processEqualityExpression();
 
     @LogMessage( level = INFO )
     @Message( value = "Processing PersistenceUnitInfo [\n\tname: %s\n\t...]", id = 204 )
     void processingPersistenceUnitInfoName( String persistenceUnitName );
 
     @LogMessage( level = INFO )
     @Message( value = "Loaded properties from resource hibernate.properties: %s", id = 205 )
     void propertiesLoaded( Properties maskOut );
 
     @LogMessage( level = INFO )
     @Message( value = "hibernate.properties not found", id = 206 )
     void propertiesNotFound();
 
     @LogMessage( level = WARN )
     @Message( value = "Property %s not found in class but described in <mapping-file/> (possible typo error)", id = 207 )
     void propertyNotFound( String property );
 
     @LogMessage( level = WARN )
     @Message( value = "%s has been deprecated in favor of %s; that provider will be used instead.", id = 208 )
     void providerClassDeprecated( String providerClassName,
                                   String actualProviderClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.", id = 209 )
     void proxoolProviderClassNotFound( String proxoolProviderClassName );
 
     @LogMessage( level = INFO )
     @Message( value = "Queries executed to database: %s", id = 210 )
     void queriesExecuted( long queryExecutionCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache: %s", id = 211 )
     void queryCache( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache factory: %s", id = 212 )
     void queryCacheFactory( String queryCacheFactoryClassName );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache hits: %s", id = 213 )
     void queryCacheHits( long queryCacheHitCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache misses: %s", id = 214 )
     void queryCacheMisses( long queryCacheMissCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Query cache puts: %s", id = 215 )
     void queryCachePuts( long queryCachePutCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Query language substitutions: %s", id = 216 )
     void queryLanguageSubstitutions( Map querySubstitutions );
 
     @LogMessage( level = INFO )
     @Message( value = "Query translator: %s", id = 217 )
     void queryTranslator( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "RDMSOS2200Dialect version: 1.0", id = 218 )
     void rdmsOs2200Dialect();
 
     @LogMessage( level = INFO )
     @Message( value = "Reading mappings from cache file: %s", id = 219 )
     void readingCachedMappings( File cachedFile );
 
     @LogMessage( level = INFO )
     @Message( value = "Reading mappings from file: %s", id = 220 )
     void readingMappingsFromFile( String path );
 
     @LogMessage( level = INFO )
     @Message( value = "Reading mappings from resource: %s", id = 221 )
     void readingMappingsFromResource( String resourceName );
 
     @LogMessage( level = WARN )
     @Message( value = "read-only cache configured for mutable collection [%s]", id = 222 )
     void readOnlyCacheConfiguredForMutableCollection( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!", id = 223 )
     void recognizedObsoleteHibernateNamespace( String oldHibernateNamespace,
                                                String hibernateNamespace );
 
     @LogMessage( level = WARN )
     @Message( value = "Reconnecting the same connection that is already connected; should this connection have been disconnected?", id = 224 )
     void reconnectingConnectedConnection();
 
     @LogMessage( level = WARN )
     @Message( value = "Property [%s] has been renamed to [%s]; update your properties appropriately", id = 225 )
     void renamedProperty( Object propertyName,
                           Object newPropertyName );
 
     @LogMessage( level = INFO )
     @Message( value = "Required a different provider: %s", id = 226 )
     void requiredDifferentProvider( String provider );
 
     @LogMessage( level = INFO )
     @Message( value = "Running hbm2ddl schema export", id = 227 )
     void runningHbm2ddlSchemaExport();
 
     @LogMessage( level = INFO )
     @Message( value = "Running hbm2ddl schema update", id = 228 )
     void runningHbm2ddlSchemaUpdate();
 
     @LogMessage( level = INFO )
     @Message( value = "Running schema validator", id = 229 )
     void runningSchemaValidator();
 
     @LogMessage( level = INFO )
     @Message( value = "Schema export complete", id = 230 )
     void schemaExportComplete();
 
     @LogMessage( level = ERROR )
     @Message( value = "Schema export unsuccessful", id = 231 )
     void schemaExportUnsuccessful( @Cause Exception e );
 
     @LogMessage( level = INFO )
     @Message( value = "Schema update complete", id = 232 )
     void schemaUpdateComplete();
 
     @LogMessage( level = WARN )
     @Message( value = "Scoping types to session factory %s after already scoped %s", id = 233 )
     void scopingTypesToSessionFactoryAfterAlreadyScoped( SessionFactoryImplementor factory,
                                                          SessionFactoryImplementor factory2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Scrollable result sets: %s", id = 234 )
     void scrollabelResultSets( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Searching for mapping documents in jar: %s", id = 235 )
     void searchingForMappingDocuments( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Second-level cache: %s", id = 236 )
     void secondLevelCache( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Second level cache hits: %s", id = 237 )
     void secondLevelCacheHits( long secondLevelCacheHitCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Second level cache misses: %s", id = 238 )
     void secondLevelCacheMisses( long secondLevelCacheMissCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Second level cache puts: %s", id = 239 )
     void secondLevelCachePuts( long secondLevelCachePutCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Service properties: %s", id = 240 )
     void serviceProperties( Properties properties );
 
     @LogMessage( level = INFO )
     @Message( value = "Sessions closed: %s", id = 241 )
     void sessionsClosed( long sessionCloseCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Sessions opened: %s", id = 242 )
     void sessionsOpened( long sessionOpenCount );
 
     @LogMessage( level = ERROR )
     @Message( value = "Setters of lazy classes cannot be final: %s.%s", id = 243 )
     void settersOfLazyClassesCannotBeFinal( String entityName,
                                             String name );
 
     @LogMessage( level = WARN )
     @Message( value = "@Sort not allowed for an indexed collection, annotation ignored.", id = 244 )
     void sortAnnotationIndexedCollection();
 
     @LogMessage( level = WARN )
     @Message( value = "Manipulation query [%s] resulted in [%s] split queries", id = 245 )
     void splitQueries( String sourceQuery,
                        int length );
 
     @LogMessage( level = ERROR )
     @Message( value = "SQLException escaped proxy", id = 246 )
     void sqlExceptionEscapedProxy( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "SQL Error: %s, SQLState: %s", id = 247 )
     void sqlWarning( int errorCode,
                      String sqlState );
 
     @LogMessage( level = INFO )
     @Message( value = "Starting query cache at region: %s", id = 248 )
     void startingQueryCache( String region );
 
     @LogMessage( level = INFO )
     @Message( value = "Starting service at JNDI name: %s", id = 249 )
     void startingServiceAtJndiName( String boundName );
 
     @LogMessage( level = INFO )
     @Message( value = "Starting update timestamps cache at region: %s", id = 250 )
     void startingUpdateTimestampsCache( String region );
 
     @LogMessage( level = INFO )
     @Message( value = "Start time: %s", id = 251 )
     void startTime( long startTime );
 
     @LogMessage( level = INFO )
     @Message( value = "Statements closed: %s", id = 252 )
     void statementsClosed( long closeStatementCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Statements prepared: %s", id = 253 )
     void statementsPrepared( long prepareStatementCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Statistics: %s", id = 254 )
     void statistics( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "Stopping service", id = 255 )
     void stoppingService();
 
     @LogMessage( level = INFO )
     @Message( value = "Structured second-level cache entries: %s", id = 256 )
     void structuredSecondLevelCacheEntries( String enabledDisabled );
 
     @LogMessage( level = INFO )
     @Message( value = "sub-resolver threw unexpected exception, continuing to next : %s", id = 257 )
     void subResolverException( String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Successful transactions: %s", id = 258 )
     void successfulTransactions( long committedTransactionCount );
 
     @LogMessage( level = INFO )
     @Message( value = "Synchronization [%s] was already registered", id = 259 )
     void synchronizationAlreadyRegistered( Synchronization synchronization );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception calling user Synchronization [%s] : %s", id = 260 )
     void synchronizationFailed( Synchronization synchronization,
                                 Throwable t );
 
     @LogMessage( level = INFO )
     @Message( value = "Table found: %s", id = 261 )
     void tableFound( String string );
 
     @LogMessage( level = INFO )
     @Message( value = "Table not found: %s", id = 262 )
     void tableNotFound( String name );
 
     @Message( value = "TransactionFactory class not found: %s", id = 263 )
     String transactionFactoryClassNotFound( String strategyClassName );
 
     @LogMessage( level = INFO )
     @Message( value = "No TransactionManagerLookup configured (in JTA environment, use of read-write or transactional second-level cache is not recommended)", id = 264 )
     void transactionManagerLookupNotConfigured();
 
     @LogMessage( level = WARN )
     @Message( value = "Transaction not available on beforeCompletion: assuming valid", id = 265 )
     void transactionNotAvailableOnBeforeCompletion();
 
     @LogMessage( level = INFO )
     @Message( value = "Transactions: %s", id = 266 )
     void transactions( long transactionCount );
 
     @LogMessage( level = WARN )
     @Message( value = "Transaction started on non-root session", id = 267 )
     void transactionStartedOnNonRootSession();
 
     @LogMessage( level = INFO )
     @Message( value = "Transaction strategy: %s", id = 268 )
     void transactionStrategy( String strategyClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "Type [%s] defined no registration keys; ignoring", id = 269 )
     void typeDefinedNoRegistrationKeys( BasicType type );
 
     @LogMessage( level = INFO )
     @Message( value = "Type registration [%s] overrides previous : %s", id = 270 )
     void typeRegistrationOverridesPrevious( String key,
                                             Type old );
 
     @LogMessage( level = WARN )
     @Message( value = "Naming exception occurred accessing Ejb3Configuration", id = 271 )
     void unableToAccessEjb3Configuration( @Cause NamingException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error while accessing session factory with JNDI name %s", id = 272 )
     void unableToAccessSessionFactory( String sfJNDIName,
                                        @Cause NamingException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Error accessing type info result set : %s", id = 273 )
     void unableToAccessTypeInfoResultSet( String string );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to apply constraints on DDL for %s", id = 274 )
     void unableToApplyConstraints( String className,
                                    @Cause Exception e );
 
     @Message( value = "JTA transaction begin failed", id = 275 )
     String unableToBeginJtaTransaction();
 
     @LogMessage( level = WARN )
     @Message( value = "Could not bind Ejb3Configuration to JNDI", id = 276 )
     void unableToBindEjb3ConfigurationToJndi( @Cause NamingException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not bind factory to JNDI", id = 277 )
     void unableToBindFactoryToJndi( @Cause NamingException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not bind value '%s' to parameter: %s; %s", id = 278 )
     void unableToBindValueToParameter( String nullSafeToString,
                                        int index,
                                        String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to build enhancement metamodel for %s", id = 279 )
     void unableToBuildEnhancementMetamodel( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s", id = 280 )
     void unableToBuildSessionFactoryUsingMBeanClasspath( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to clean up callable statement", id = 281 )
     void unableToCleanUpCallableStatement( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to clean up prepared statement", id = 282 )
     void unableToCleanUpPreparedStatement( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to cleanup temporary id table after use [%s]", id = 283 )
     void unableToCleanupTemporaryIdTable( Throwable t );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error closing connection", id = 284 )
     void unableToCloseConnection( @Cause Exception e );
 
     @LogMessage( level = INFO )
     @Message( value = "Error closing InitialContext [%s]", id = 285 )
     void unableToCloseInitialContext( String string );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error closing input files: %s", id = 286 )
     void unableToCloseInputFiles( String name,
                                   @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not close input stream", id = 287 )
     void unableToCloseInputStream( @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not close input stream for %s", id = 288 )
     void unableToCloseInputStreamForResource( String resourceName,
                                               @Cause IOException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to close iterator", id = 289 )
     void unableToCloseIterator( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close jar: %s", id = 290 )
     void unableToCloseJar( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error closing output file: %s", id = 291 )
     void unableToCloseOutputFile( String outputFile,
                                   @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "IOException occurred closing output stream", id = 292 )
     void unableToCloseOutputStream( @Cause IOException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Problem closing pooled connection", id = 293 )
     void unableToClosePooledConnection( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close session", id = 294 )
     void unableToCloseSession( @Cause HibernateException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close session during rollback", id = 295 )
     void unableToCloseSessionDuringRollback( @Cause Exception e );
 
     @LogMessage( level = WARN )
     @Message( value = "IOException occurred closing stream", id = 296 )
     void unableToCloseStream( @Cause IOException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not close stream on hibernate.properties: %s", id = 297 )
     void unableToCloseStreamError( IOException error );
 
     @Message( value = "JTA commit failed", id = 298 )
     String unableToCommitJta();
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not complete schema update", id = 299 )
     void unableToCompleteSchemaUpdate( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not complete schema validation", id = 300 )
     void unableToCompleteSchemaValidation( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to configure SQLExceptionConverter : %s", id = 301 )
     void unableToConfigureSqlExceptionConverter( HibernateException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to construct current session context [%s]", id = 302 )
     void unableToConstructCurrentSessionContext( String impl,
                                                  @Cause Throwable e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to construct instance of specified SQLExceptionConverter : %s", id = 303 )
     void unableToConstructSqlExceptionConverter( Throwable t );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not copy system properties, system properties will be ignored", id = 304 )
     void unableToCopySystemProperties();
 
     @LogMessage( level = WARN )
     @Message( value = "Could not create proxy factory for:%s", id = 305 )
     void unableToCreateProxyFactory( String entityName,
                                      @Cause HibernateException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Error creating schema ", id = 306 )
     void unableToCreateSchema( @Cause Exception e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not deserialize cache file: %s : %s", id = 307 )
     void unableToDeserializeCache( String path,
                                    SerializationException error );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to destroy cache: %s", id = 308 )
     void unableToDestroyCache( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to destroy query cache: %s: %s", id = 309 )
     void unableToDestroyQueryCache( String region,
                                     String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to destroy update timestamps cache: %s: %s", id = 310 )
     void unableToDestroyUpdateTimestampsCache( String region,
                                                String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to determine lock mode value : %s -> %s", id = 311 )
     void unableToDetermineLockModeValue( String hintName,
                                          Object value );
 
     @Message( value = "Could not determine transaction status", id = 312 )
     String unableToDetermineTransactionStatus();
 
     @Message( value = "Could not determine transaction status after commit", id = 313 )
     String unableToDetermineTransactionStatusAfterCommit();
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to drop temporary id table after use [%s]", id = 314 )
     void unableToDropTemporaryIdTable( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Exception executing batch [%s]", id = 315 )
     void unableToExecuteBatch( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Error executing resolver [%s] : %s", id = 316 )
     void unableToExecuteResolver( AbstractDialectResolver abstractDialectResolver,
                                   String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to find %s on the classpath. Hibernate Search is not enabled.", id = 317 )
     void unableToFindListenerClass( String className );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318 )
     void unableToFindPersistenceXmlInClasspath();
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not get database metadata", id = 319 )
     void unableToGetDatabaseMetadata( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to instantiate configured schema name resolver [%s] %s", id = 320 )
     void unableToInstantiateConfiguredSchemaNameResolver( String resolverClassName,
                                                           String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not instantiate dialect resolver class : %s", id = 321 )
     void unableToInstantiateDialectResolver( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to instantiate specified optimizer [%s], falling back to noop", id = 322 )
     void unableToInstantiateOptimizer( String type );
 
     @Message( value = "Failed to instantiate TransactionFactory", id = 323 )
     String unableToInstantiateTransactionFactory();
 
     @Message( value = "Failed to instantiate TransactionManagerLookup '%s'", id = 324 )
     String unableToInstantiateTransactionManagerLookup( String tmLookupClass );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to instantiate UUID generation strategy class : %s", id = 325 )
     void unableToInstantiateUuidGenerationStrategy( Exception ignore );
 
     @LogMessage( level = WARN )
     @Message( value = "Cannot join transaction: do not override %s", id = 326 )
     void unableToJoinTransaction( String transactionStrategy );
 
     @LogMessage( level = INFO )
     @Message( value = "Error performing load command : %s", id = 327 )
     void unableToLoadCommand( HibernateException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to load/access derby driver class sysinfo to check versions : %s", id = 328 )
     void unableToLoadDerbyDriver( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Problem loading properties from hibernate.properties", id = 329 )
     void unableToloadProperties();
 
     @Message( value = "Unable to locate config file: %s", id = 330 )
     String unableToLocateConfigFile( String path );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to locate configured schema name resolver class [%s] %s", id = 331 )
     void unableToLocateConfiguredSchemaNameResolver( String resolverClassName,
                                                      String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to locate MBeanServer on JMX service shutdown", id = 332 )
     void unableToLocateMBeanServer();
 
     @LogMessage( level = INFO )
     @Message( value = "Could not locate 'java.sql.NClob' class; assuming JDBC 3", id = 333 )
     void unableToLocateNClobClass();
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to locate requested UUID generation strategy class : %s", id = 334 )
     void unableToLocateUuidGenerationStrategy( String strategyClassName );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to log SQLWarnings : %s", id = 335 )
     void unableToLogSqlWarnings( SQLException sqle );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not log warnings", id = 336 )
     void unableToLogWarnings( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to mark for rollback on PersistenceException: ", id = 337 )
     void unableToMarkForRollbackOnPersistenceException( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to mark for rollback on TransientObjectException: ", id = 338 )
     void unableToMarkForRollbackOnTransientObjectException( @Cause Exception e );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection metadata: %s", id = 339 )
     void unableToObjectConnectionMetadata( SQLException error );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection to query metadata: %s", id = 340 )
     void unableToObjectConnectionToQueryMetadata( SQLException error );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection metadata : %s", id = 341 )
     void unableToObtainConnectionMetadata( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not obtain connection to query metadata : %s", id = 342 )
     void unableToObtainConnectionToQueryMetadata( String message );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not obtain initial context", id = 343 )
     void unableToObtainInitialContext( @Cause NamingException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not parse the package-level metadata [%s]", id = 344 )
     void unableToParseMetadata( String packageName );
 
     @Message( value = "JDBC commit failed", id = 345 )
     String unableToPerformJdbcCommit();
 
     @LogMessage( level = ERROR )
     @Message( value = "Error during managed flush [%s]", id = 346 )
     void unableToPerformManagedFlush( String message );
 
     @Message( value = "Unable to query java.sql.DatabaseMetaData", id = 347 )
     String unableToQueryDatabaseMetadata();
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to read class: %s", id = 348 )
     void unableToReadClass( String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not read column value from result set: %s; %s", id = 349 )
     void unableToReadColumnValueFromResultSet( String name,
                                                String message );
 
     @Message( value = "Could not read a hi value - you need to populate the table: %s", id = 350 )
     String unableToReadHiValue( String tableName );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not read or init a hi value", id = 351 )
     void unableToReadOrInitHiValue( @Cause SQLException e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to release batch statement...", id = 352 )
     void unableToReleaseBatchStatement();
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not release a cache lock : %s", id = 353 )
     void unableToReleaseCacheLock( CacheException ce );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to release initial context: %s", id = 354 )
     void unableToReleaseContext( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to release created MBeanServer : %s", id = 355 )
     void unableToReleaseCreatedMBeanServer( String string );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to release isolated connection [%s]", id = 356 )
     void unableToReleaseIsolatedConnection( Throwable ignore );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to release type info result set", id = 357 )
     void unableToReleaseTypeInfoResultSet();
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to erase previously added bag join fetch", id = 358 )
     void unableToRemoveBagJoinFetch();
 
     @LogMessage( level = INFO )
     @Message( value = "Could not resolve aggregate function {}; using standard definition", id = 359 )
     void unableToResolveAggregateFunction( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to resolve mapping file [%s]", id = 360 )
     void unableToResolveMappingFile( String xmlFile );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to retreive cache from JNDI [%s]: %s", id = 361 )
     void unableToRetrieveCache( String namespace,
                                 String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to retrieve type info result set : %s", id = 362 )
     void unableToRetrieveTypeInfoResultSet( String string );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to rollback connection on exception [%s]", id = 363 )
     void unableToRollbackConnection( Exception ignore );
 
     @LogMessage( level = INFO )
     @Message( value = "Unable to rollback isolated transaction on error [%s] : [%s]", id = 364 )
     void unableToRollbackIsolatedTransaction( Exception e,
                                               Exception ignore );
 
     @Message( value = "JTA rollback failed", id = 365 )
     String unableToRollbackJta();
 
     @LogMessage( level = ERROR )
     @Message( value = "Error running schema update", id = 366 )
     void unableToRunSchemaUpdate( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not set transaction to rollback only", id = 367 )
     void unableToSetTransactionToRollbackOnly( @Cause SystemException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Exception while stopping service", id = 368 )
     void unableToStopHibernateService( @Cause Exception e );
 
     @LogMessage( level = INFO )
     @Message( value = "Error stopping service [%s] : %s", id = 369 )
     void unableToStopService( Class class1,
                               String string );
 
     @LogMessage( level = WARN )
     @Message( value = "Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]", id = 370 )
     void unableToSwitchToMethodUsingColumnIndex( Method method );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not synchronize database state with session: %s", id = 371 )
     void unableToSynchronizeDatabaseStateWithSession( HibernateException he );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not toggle autocommit", id = 372 )
     void unableToToggleAutoCommit( @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to transform class: %s", id = 373 )
     void unableToTransformClass( String message );
 
     @LogMessage( level = WARN )
     @Message( value = "Could not unbind factory from JNDI", id = 374 )
     void unableToUnbindFactoryFromJndi( @Cause NamingException e );
 
     @Message( value = "Could not update hi value in: %s", id = 375 )
     Object unableToUpdateHiValue( String tableName );
 
     @LogMessage( level = ERROR )
     @Message( value = "Could not updateQuery hi value in: %s", id = 376 )
     void unableToUpdateQueryHiValue( String tableName,
                                      @Cause SQLException e );
 
     @LogMessage( level = INFO )
     @Message( value = "Error wrapping result set", id = 377 )
     void unableToWrapResultSet( @Cause SQLException e );
 
     @LogMessage( level = WARN )
     @Message( value = "I/O reported error writing cached file : %s: %s", id = 378 )
     void unableToWriteCachedFile( String path,
                                   String message );
 
     @LogMessage( level = INFO )
     @Message( value = "Unbinding factory from JNDI name: %s", id = 379 )
     void unbindingFactoryFromJndiName( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Unexpected literal token type [%s] passed for numeric processing", id = 380 )
     void unexpectedLiteralTokenType( int type );
 
     @LogMessage( level = WARN )
     @Message( value = "JDBC driver did not return the expected number of row counts", id = 381 )
     void unexpectedRowCounts();
 
     @LogMessage( level = WARN )
     @Message( value = "unrecognized bytecode provider [%s], using javassist by default", id = 382 )
     void unknownBytecodeProvider( String providerName );
 
     @LogMessage( level = WARN )
     @Message( value = "Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id = 383 )
     void unknownIngresVersion( int databaseMajorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "Unknown Oracle major version [%s]", id = 384 )
     void unknownOracleVersion( int databaseMajorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "Unknown Microsoft SQL Server major version [%s] using SQL Server 2000 dialect", id = 385 )
     void unknownSqlServerVersion( int databaseMajorVersion );
 
     @LogMessage( level = WARN )
     @Message( value = "ResultSet had no statement associated with it, but was not yet registered", id = 386 )
     void unregisteredResultSetWithoutStatement();
 
     @LogMessage( level = WARN )
     @Message( value = "ResultSet's statement was not registered", id = 387 )
     void unregisteredStatement();
 
     @LogMessage( level = ERROR )
     @Message( value = "Unsuccessful: %s", id = 388 )
     void unsuccessful( String sql );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unsuccessful: %s", id = 389 )
     void unsuccessfulCreate( String string );
 
     @LogMessage( level = WARN )
     @Message( value = "Overriding release mode as connection provider does not support 'after_statement'", id = 390 )
     void unsupportedAfterStatement();
 
     @LogMessage( level = WARN )
     @Message( value = "Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id = 391 )
     void unsupportedIngresVersion();
 
     @LogMessage( level = WARN )
     @Message( value = "Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id = 392 )
     void unsupportedInitialValue( String propertyName );
 
     @LogMessage( level = WARN )
     @Message( value = "The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly", id = 393 )
     void unsupportedMultiTableBulkHqlJpaql( int majorVersion,
                                             int minorVersion,
                                             int buildId );
 
     @LogMessage( level = WARN )
     @Message( value = "Oracle 11g is not yet fully supported; using Oracle 10g dialect", id = 394 )
     void unsupportedOracleVersion();
 
     @LogMessage( level = WARN )
     @Message( value = "Usage of obsolete property: %s no longer supported, use: %s", id = 395 )
     void unsupportedProperty( Object propertyName,
                               Object newPropertyName );
 
     @LogMessage( level = INFO )
     @Message( value = "Updating schema", id = 396 )
     void updatingSchema();
 
     @LogMessage( level = INFO )
     @Message( value = "Using ASTQueryTranslatorFactory", id = 397 )
     void usingAstQueryTranslatorFactory();
 
     @LogMessage( level = INFO )
     @Message( value = "Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id = 398 )
     void usingDefaultIdGeneratorSegmentValue( String tableName,
                                               String segmentColumnName,
                                               String defaultToUse );
 
     @LogMessage( level = INFO )
     @Message( value = "Using default transaction strategy (direct JDBC transactions)", id = 399 )
     void usingDefaultTransactionStrategy();
 
     @LogMessage( level = INFO )
     @Message( value = "Using dialect: %s", id = 400 )
     void usingDialect( Dialect dialect );
 
     @LogMessage( level = INFO )
     @Message( value = "using driver [%s] at URL [%s]", id = 401 )
     void usingDriver( String driverClassName,
                       String url );
 
     @LogMessage( level = INFO )
     @Message( value = "Using Hibernate built-in connection pool (not for production use!)", id = 402 )
     void usingHibernateBuiltInConnectionPool();
 
-    @LogMessage( level = INFO )
-    @Message( value = "Using JDK 1.4 java.sql.Timestamp handling", id = 403 )
-    void usingJdk14TimestampHandling();
-
     @LogMessage( level = ERROR )
     @Message( value = "Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id = 404 )
     void usingOldDtd();
 
     @LogMessage( level = INFO )
-    @Message( value = "Using pre JDK 1.4 java.sql.Timestamp handling", id = 405 )
-    void usingPreJdk14TimestampHandling();
-
-    @LogMessage( level = INFO )
     @Message( value = "Using bytecode reflection optimizer", id = 406 )
     void usingReflectionOptimizer();
 
     @LogMessage( level = INFO )
     @Message( value = "Using java.io streams to persist binary types", id = 407 )
     void usingStreams();
 
     @LogMessage( level = INFO )
     @Message( value = "Using workaround for JVM bug in java.sql.Timestamp", id = 408 )
     void usingTimestampWorkaround();
 
     @LogMessage( level = WARN )
     @Message( value = "Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead", id = 409 )
     void usingUuidHexGenerator( String name,
                                 String name2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Hibernate Validator not found: ignoring", id = 410 )
     void validatorNotFound();
 
     @LogMessage( level = WARN )
     @Message( value = "Value mapping mismatch as part of FK [table=%s, name=%s] while adding source column [%s]", id = 411 )
     void valueMappingMismatch( String loggableString,
                                String name,
                                String loggableString2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Hibernate %s", id = 412 )
     void version( String versionString );
 
     @LogMessage( level = WARN )
     @Message( value = "Warnings creating temp table : %s", id = 413 )
     void warningsCreatingTempTable( SQLWarning warning );
 
     @LogMessage( level = INFO )
     @Message( value = "Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.", id = 414 )
     void willNotRegisterListeners();
 
     @LogMessage( level = INFO )
     @Message( value = "Wrap result sets: %s", id = 415 )
     void wrapResultSets( String enabledDisabled );
 
     @LogMessage( level = WARN )
     @Message( value = "Write locks via update not supported for non-versioned entities [%s]", id = 416 )
     void writeLocksNotSupported( String entityName );
 
     @LogMessage( level = INFO )
     @Message( value = "Writing generated schema to file: %s", id = 417 )
     void writingGeneratedSchemaToFile( String outputFile );
 
     @LogMessage( level = INFO )
     @Message( value = "Adding override for %s: %s", id = 418 )
     void addingOverrideFor( String name,
                             String name2 );
 
     @LogMessage( level = WARN )
     @Message( value = "Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s", id = 419 )
     void resolvedSqlTypeDescriptorForDifferentSqlCode( String name,
                                                        String valueOf,
                                                        String name2,
                                                        String valueOf2 );
 
     @LogMessage( level = WARN )
     @Message( value = "Closing un-released batch", id = 420 )
     void closingUnreleasedBatch();
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as %s is true", id = 421 )
     void disablingContextualLOBCreation( String nonContextualLobCreation );
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as connection was null", id = 422 )
     void disablingContextualLOBCreationSinceConnectionNull();
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4", id = 423 )
     void disablingContextualLOBCreationSinceOldJdbcVersion( int jdbcMajorVersion );
 
     @LogMessage( level = INFO )
     @Message( value = "Disabling contextual LOB creation as createClob() method threw error : %s", id = 424 )
     void disablingContextualLOBCreationSinceCreateClobFailed( Throwable t );
 
     @LogMessage( level = INFO )
     @Message( value = "Could not close session; swallowing exception as transaction completed", id = 425 )
     void unableToCloseSessionButSwallowingError( HibernateException e );
 
     @LogMessage( level = WARN )
     @Message( value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 426 )
     void setManagerLookupClass();
 
     @LogMessage( level = WARN )
     @Message( value = "Using deprecated %s strategy [%s], use newer %s strategy instead [%s]", id = 427 )
     void deprecatedTransactionManagerStrategy( String name,
                                                String transactionManagerStrategy,
                                                String name2,
                                                String jtaPlatform );
 
     @LogMessage( level = INFO )
     @Message( value = "Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting", id = 428 )
     void legacyTransactionManagerStrategy( String name,
                                            String jtaPlatform );
 
     @LogMessage( level = WARN )
     @Message( value = "Setting entity-identifier value binding where one already existed : %s.", id = 429 )
 	void entityIdentifierValueBindingExists(String name);
 
     @LogMessage( level = WARN )
     @Message( value = "The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead", id = 430 )
     void deprecatedDerbyDialect();
 
 	@LogMessage( level = WARN )
 	@Message( value = "Unable to determine H2 database version, certain features may not work", id = 431 )
 	void undeterminedH2Version();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
index 6e8bac2fb1..bcfe2be3ba 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/JdbcTimestampTypeDescriptor.java
@@ -1,182 +1,177 @@
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
 package org.hibernate.type.descriptor.java;
 import java.sql.Timestamp;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class JdbcTimestampTypeDescriptor extends AbstractTypeDescriptor<Date> {
 	public static final JdbcTimestampTypeDescriptor INSTANCE = new JdbcTimestampTypeDescriptor();
 	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
 
 	public static class TimestampMutabilityPlan extends MutableMutabilityPlan<Date> {
 		public static final TimestampMutabilityPlan INSTANCE = new TimestampMutabilityPlan();
 
 		public Date deepCopyNotNull(Date value) {
 			if ( value instanceof Timestamp ) {
 				Timestamp orig = (Timestamp) value;
 				Timestamp ts = new Timestamp( orig.getTime() );
 				ts.setNanos( orig.getNanos() );
 				return ts;
 			}
 			else {
 				Date orig = value;
 				return new Date( orig.getTime() );
 			}
 		}
 	}
 
 	public JdbcTimestampTypeDescriptor() {
 		super( Date.class, TimestampMutabilityPlan.INSTANCE );
 	}
 
 	public String toString(Date value) {
 		return new SimpleDateFormat( TIMESTAMP_FORMAT ).format( value );
 	}
 
 	public Date fromString(String string) {
 		try {
 			return new Timestamp( new SimpleDateFormat( TIMESTAMP_FORMAT ).parse( string ).getTime() );
 		}
 		catch ( ParseException pe) {
 			throw new HibernateException( "could not parse timestamp string" + string, pe );
 		}
 	}
 
 	@Override
 	public boolean areEqual(Date one, Date another) {
 		if ( one == another ) {
 			return true;
 		}
 		if ( one == null || another == null) {
 			return false;
 		}
 
 		long t1 = one.getTime();
 		long t2 = another.getTime();
 
 		boolean oneIsTimestamp = Timestamp.class.isInstance( one );
 		boolean anotherIsTimestamp = Timestamp.class.isInstance( another );
 
 		int n1 = oneIsTimestamp ? ( (Timestamp) one ).getNanos() : 0;
 		int n2 = anotherIsTimestamp ? ( (Timestamp) another ).getNanos() : 0;
 
-		if ( !Environment.jvmHasJDK14Timestamp() ) {
-			t1 += n1 / 1000000;
-			t2 += n2 / 1000000;
-		}
-
 		if ( t1 != t2 ) {
 			return false;
 		}
 
 		if ( oneIsTimestamp && anotherIsTimestamp ) {
 			// both are Timestamps
 			int nn1 = n1 % 1000000;
 			int nn2 = n2 % 1000000;
 			return nn1 == nn2;
 		}
 		else {
 			// at least one is a plain old Date
 			return true;
 		}
 	}
 
 	@Override
 	public int extractHashCode(Date value) {
 		return Long.valueOf( value.getTime() / 1000 ).hashCode();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public <X> X unwrap(Date value, Class<X> type, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Timestamp.class.isAssignableFrom( type ) ) {
 			final Timestamp rtn = Timestamp.class.isInstance( value )
 					? ( Timestamp ) value
 					: new Timestamp( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Date.class.isAssignableFrom( type ) ) {
 			final java.sql.Date rtn = java.sql.Date.class.isInstance( value )
 					? ( java.sql.Date ) value
 					: new java.sql.Date( value.getTime() );
 			return (X) rtn;
 		}
 		if ( java.sql.Time.class.isAssignableFrom( type ) ) {
 			final java.sql.Time rtn = java.sql.Time.class.isInstance( value )
 					? ( java.sql.Time ) value
 					: new java.sql.Time( value.getTime() );
 			return (X) rtn;
 		}
 		if ( Date.class.isAssignableFrom( type ) ) {
 			return (X) value;
 		}
 		if ( Calendar.class.isAssignableFrom( type ) ) {
 			final GregorianCalendar cal = new GregorianCalendar();
 			cal.setTimeInMillis( value.getTime() );
 			return (X) cal;
 		}
 		if ( Long.class.isAssignableFrom( type ) ) {
 			return (X) Long.valueOf( value.getTime() );
 		}
 		throw unknownUnwrap( type );
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	public <X> Date wrap(X value, WrapperOptions options) {
 		if ( value == null ) {
 			return null;
 		}
 		if ( Timestamp.class.isInstance( value ) ) {
 			return (Timestamp) value;
 		}
 
 		if ( Long.class.isInstance( value ) ) {
 			return new Timestamp( ( (Long) value ).longValue() );
 		}
 
 		if ( Calendar.class.isInstance( value ) ) {
 			return new Timestamp( ( (Calendar) value ).getTimeInMillis() );
 		}
 
 		if ( Date.class.isInstance( value ) ) {
 			return (Date) value;
 		}
 
 		throw unknownWrap( value.getClass() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
index 636f251228..3c4e4fd0a6 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
@@ -1,91 +1,91 @@
 //$Id$
 package org.hibernate.test.annotations.backquotes;
 
 import java.io.PrintWriter;
 import java.io.StringWriter;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.service.ServiceRegistry;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Testcase for ANN-718 - @JoinTable / @JoinColumn fail when using backquotes in PK field name.
  *
  * @author Hardy Ferentschik
  *
  */
 public class BackquoteTest extends BaseUnitTestCase {
 	private static final Logger log = Logger.getLogger( BackquoteTest.class );
 
 	private ServiceRegistry serviceRegistry;
 
 	@Before
-    protected void setUp() {
+    public void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@After
-    protected void tearDown() {
+    public void tearDown() {
         if (serviceRegistry != null) ServiceRegistryBuilder.destroy(serviceRegistry);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "ANN-718" )
 	public void testBackquotes() {
 		try {
 			Configuration config = new Configuration();
 			config.addAnnotatedClass(Bug.class);
 			config.addAnnotatedClass(Category.class);
 			config.buildSessionFactory( serviceRegistry );
 		}
 		catch( Exception e ) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             log.debug(writer.toString());
 			fail(e.getMessage());
 		}
 	}
 
 	/**
 	 *  HHH-4647 : Problems with @JoinColumn referencedColumnName and quoted column and table names
 	 *
 	 *  An invalid referencedColumnName to an entity having a quoted table name results in an
 	 *  infinite loop in o.h.c.Configuration$MappingsImpl#getPhysicalColumnName().
 	 *  The same issue exists with getLogicalColumnName()
 	 */
 	@Test
 	@TestForIssue( jiraKey = "HHH-4647" )
 	public void testInvalidReferenceToQuotedTableName() {
     	try {
     		Configuration config = new Configuration();
     		config.addAnnotatedClass(Printer.class);
     		config.addAnnotatedClass(PrinterCable.class);
     		config.buildSessionFactory( serviceRegistry );
     		fail("expected MappingException to be thrown");
     	}
     	//we WANT MappingException to be thrown
         catch( MappingException e ) {
         	assertTrue("MappingException was thrown", true);
         }
         catch(Exception e) {
         	StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             log.debug(writer.toString());
         	fail(e.getMessage());
         }
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
index 9a7b8fb558..a5c5b002cc 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
@@ -1,185 +1,192 @@
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
 package org.hibernate.test.annotations.fetchprofile;
 
 import java.io.InputStream;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.service.ServiceRegistry;
 
 import org.junit.After;
 import org.junit.Before;
+import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Test case for HHH-4812
  *
  * @author Hardy Ferentschik
  */
 @TestForIssue( jiraKey = "HHH-4812" )
 public class FetchProfileTest extends BaseUnitTestCase {
 	private static final Logger log = Logger.getLogger( FetchProfileTest.class );
 
 	private ServiceRegistry serviceRegistry;
 
 	@Before
-    protected void setUp() {
+    public void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@After
-    protected void tearDown() {
+    public void tearDown() {
         if (serviceRegistry != null) ServiceRegistryBuilder.destroy(serviceRegistry);
 	}
 
+	@Test
 	public void testFetchProfileConfigured() {
 		Configuration config = new Configuration();
 		config.addAnnotatedClass( Customer.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( SupportTickets.class );
 		config.addAnnotatedClass( Country.class );
 		SessionFactoryImplementor sessionImpl = ( SessionFactoryImplementor ) config.buildSessionFactory(
 				serviceRegistry
 		);
 
 		assertTrue(
 				"fetch profile not parsed properly",
 				sessionImpl.containsFetchProfileDefinition( "customer-with-orders" )
 		);
 		assertFalse(
 				"package info should not be parsed",
 				sessionImpl.containsFetchProfileDefinition( "package-profile-1" )
 		);
 	}
 
+	@Test
 	public void testWrongAssociationName() {
 		Configuration config = new Configuration();
 		config.addAnnotatedClass( Customer2.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             log.trace("success");
 		}
 	}
 
+	@Test
 	public void testWrongClass() {
 		Configuration config = new Configuration();
 		config.addAnnotatedClass( Customer3.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             log.trace("success");
 		}
 	}
 
+	@Test
 	public void testUnsupportedFetchMode() {
 		Configuration config = new Configuration();
 		config.addAnnotatedClass( Customer4.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             log.trace("success");
 		}
 	}
 
+	@Test
 	public void testXmlOverride() {
 		Configuration config = new Configuration();
 		config.addAnnotatedClass( Customer5.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 		InputStream is = Thread.currentThread()
 				.getContextClassLoader()
 				.getResourceAsStream( "org/hibernate/test/annotations/fetchprofile/mappings.hbm.xml" );
 		config.addInputStream( is );
 		SessionFactoryImplementor sessionImpl = ( SessionFactoryImplementor ) config.buildSessionFactory(
 				serviceRegistry
 		);
 
 		assertTrue(
 				"fetch profile not parsed properly",
 				sessionImpl.containsFetchProfileDefinition( "orders-profile" )
 		);
 
 		// now the same with no xml
 		config = new Configuration();
 		config.addAnnotatedClass( Customer5.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             log.trace("success");
 		}
 	}
 
+	@Test
 	public void testPackageConfiguredFetchProfile() {
 		Configuration config = new Configuration();
 		config.addAnnotatedClass( Customer.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( SupportTickets.class );
 		config.addAnnotatedClass( Country.class );
 		config.addPackage( Customer.class.getPackage().getName() );
 		SessionFactoryImplementor sessionImpl = ( SessionFactoryImplementor ) config.buildSessionFactory(
 				serviceRegistry
 		);
 
 		assertTrue(
 				"fetch profile not parsed properly",
 				sessionImpl.containsFetchProfileDefinition( "package-profile-1" )
 		);
 		assertTrue(
 				"fetch profile not parsed properly",
 				sessionImpl.containsFetchProfileDefinition( "package-profile-2" )
 		);
 	}
 }
\ No newline at end of file
