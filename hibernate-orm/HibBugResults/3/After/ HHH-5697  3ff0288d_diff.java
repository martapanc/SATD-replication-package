diff --git a/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java b/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
new file mode 100644
index 0000000000..999b584057
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/MultiTenancyStrategy.java
@@ -0,0 +1,81 @@
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
+package org.hibernate;
+
+import java.util.Map;
+
+import org.hibernate.cfg.Environment;
+
+/**
+ * Describes the methods for multi-tenancy understood by Hibernate.
+ *
+ * @author Steve Ebersole
+ */
+public enum MultiTenancyStrategy {
+	/**
+	 * Multi-tenancy implemented by use of discriminator columns.
+	 */
+	DISCRIMINATOR,
+	/**
+	 * Multi-tenancy implemented as separate schemas.
+	 */
+	SCHEMA,
+	/**
+	 * Multi-tenancy implemented as separate databases.
+	 */
+	DATABASE,
+	/**
+	 * No multi-tenancy
+	 */
+	NONE;
+
+	public static MultiTenancyStrategy determineMultiTenancyStrategy(Map properties) {
+		final Object strategy = properties.get( Environment.MULTI_TENANT );
+		if ( strategy == null ) {
+			return MultiTenancyStrategy.NONE;
+		}
+
+		if ( MultiTenancyStrategy.class.isInstance( strategy ) ) {
+			return (MultiTenancyStrategy) strategy;
+		}
+
+		final String strategyName = strategy.toString();
+		if ( MultiTenancyStrategy.DISCRIMINATOR.name().equals( strategyName ) ) {
+			return MultiTenancyStrategy.DISCRIMINATOR;
+		}
+		else if ( MultiTenancyStrategy.SCHEMA.name().equals( strategyName ) ) {
+			return MultiTenancyStrategy.SCHEMA;
+		}
+		else if ( MultiTenancyStrategy.DATABASE.name().equals( strategyName ) ) {
+			return MultiTenancyStrategy.DATABASE;
+		}
+		else if ( MultiTenancyStrategy.NONE.name().equals( strategyName ) ) {
+			return MultiTenancyStrategy.NONE;
+		}
+		else {
+			// todo log?
+			return MultiTenancyStrategy.NONE;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java b/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
index e25ecb1d33..e318f3a219 100644
--- a/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
@@ -1,118 +1,136 @@
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
 package org.hibernate;
 
 import java.io.Serializable;
 
 /**
  * Contract methods shared between {@link Session} and {@link StatelessSession}
  * 
  * @author Steve Ebersole
  */
 public interface SharedSessionContract extends Serializable {
 	/**
+	 * Obtain the tenant identifier associated with this session.
+	 *
+	 * @return The tenant identifier associated with this session, or {@code null}
+	 */
+	public String getTenantIdentifier();
+
+	/**
+	 * Should be set only once for the session.  Would rather this be supplied to opening the session, as
+	 * being discussed for HHH-2860
+	 *
+	 * @param identifier The tenant identifier.
+	 *
+	 * @deprecated HHH-2860
+	 */
+	@Deprecated
+	public void setTenantIdentifier(String identifier);
+
+	/**
 	 * Begin a unit of work and return the associated {@link Transaction} object.  If a new underlying transaction is
 	 * required, begin the transaction.  Otherwise continue the new work in the context of the existing underlying
 	 * transaction.
 	 *
 	 * @return a Transaction instance
 	 *
 	 * @see #getTransaction
 	 */
 	public Transaction beginTransaction();
 
 	/**
 	 * Get the {@link Transaction} instance associated with this session.  The concrete type of the returned
 	 * {@link Transaction} object is determined by the {@code hibernate.transaction_factory} property.
 	 *
 	 * @return a Transaction instance
 	 */
 	public Transaction getTransaction();
 
 	/**
 	 * Create a {@link Query} instance for the named query string defined in the metadata.
 	 *
 	 * @param queryName the name of a query defined externally
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query getNamedQuery(String queryName);
 
 	/**
 	 * Create a {@link Query} instance for the given HQL query string.
 	 *
 	 * @param queryString The HQL query
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query createQuery(String queryString);
 
 	/**
 	 * Create a {@link SQLQuery} instance for the given SQL query string.
 	 *
 	 * @param queryString The SQL query
 	 * 
 	 * @return The query instance for manipulation and execution
 	 */
 	public SQLQuery createSQLQuery(String queryString);
 
 	/**
 	 * Create {@link Criteria} instance for the given class (entity or subclasses/implementors)
 	 *
 	 * @param persistentClass The class, which is an entity, or has entity subclasses/implementors
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(Class persistentClass);
 
 	/**
 	 * Create {@link Criteria} instance for the given class (entity or subclasses/implementors), using a specific
 	 * alias.
 	 *
 	 * @param persistentClass The class, which is an entity, or has entity subclasses/implementors
 	 * @param alias The alias to use
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(Class persistentClass, String alias);
 
 	/**
 	 * Create {@link Criteria} instance for the given entity name.
 	 *
 	 * @param entityName The entity name
 
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(String entityName);
 
 	/**
 	 * Create {@link Criteria} instance for the given entity name, using a specific alias.
 	 *
 	 * @param entityName The entity name
 	 * @param alias The alias to use
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(String entityName, String alias);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
index cc3c8d36bc..a1b03f455b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Environment.java
@@ -1,805 +1,810 @@
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
 import org.hibernate.HibernateLogger;
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
 
+	/**
+	 * Strategy for multi-tenancy.
+	 * @see org.hibernate.MultiTenancyStrategy
+	 */
+	public static final String MULTI_TENANT = "hibernate.multiTenancy";
 
 	private static final BytecodeProvider BYTECODE_PROVIDER_INSTANCE;
 	private static final boolean ENABLE_BINARY_STREAMS;
 	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
 	private static final boolean JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	private static final boolean JVM_HAS_TIMESTAMP_BUG;
 	private static final boolean JVM_HAS_JDK14_TIMESTAMP;
 	private static final boolean JVM_SUPPORTS_GET_GENERATED_KEYS;
 
 	private static final Properties GLOBAL_PROPERTIES;
 	private static final HashMap ISOLATION_LEVELS = new HashMap();
 	private static final Map OBSOLETE_PROPERTIES = new HashMap();
 	private static final Map RENAMED_PROPERTIES = new HashMap();
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Environment.class.getName());
 
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
 		ENABLE_REFLECTION_OPTIMIZER = ConfigurationHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
 
         if (ENABLE_BINARY_STREAMS) LOG.usingStreams();
         if (ENABLE_REFLECTION_OPTIMIZER) LOG.usingReflectionOptimizer();
 		BYTECODE_PROVIDER_INSTANCE = buildBytecodeProvider( GLOBAL_PROPERTIES );
 
 		boolean getGeneratedKeysSupport;
 		try {
 			Statement.class.getMethod("getGeneratedKeys", (Class[])null);
 			getGeneratedKeysSupport = true;
 		}
 		catch (NoSuchMethodException nsme) {
 			getGeneratedKeysSupport = false;
 		}
 		JVM_SUPPORTS_GET_GENERATED_KEYS = getGeneratedKeysSupport;
         if (!JVM_SUPPORTS_GET_GENERATED_KEYS) LOG.generatedKeysNotSupported();
 
 		boolean linkedHashSupport;
 		try {
 			Class.forName("java.util.LinkedHashSet");
 			linkedHashSupport = true;
 		}
 		catch (ClassNotFoundException cnfe) {
 			linkedHashSupport = false;
 		}
 		JVM_SUPPORTS_LINKED_HASH_COLLECTIONS = linkedHashSupport;
         if (!JVM_SUPPORTS_LINKED_HASH_COLLECTIONS) LOG.linkedMapsAndSetsNotSupported();
 
 		long x = 123456789;
 		JVM_HAS_TIMESTAMP_BUG = new Timestamp(x).getTime() != x;
         if (JVM_HAS_TIMESTAMP_BUG) LOG.usingTimestampWorkaround();
 
 		Timestamp t = new Timestamp(0);
 		t.setNanos(5 * 1000000);
 		JVM_HAS_JDK14_TIMESTAMP = t.getTime() == 5;
         if (JVM_HAS_JDK14_TIMESTAMP) LOG.usingJdk14TimestampHandling();
         else LOG.usingPreJdk14TimestampHandling();
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
 	 * Does this JVM handle {@link java.sql.Timestamp} in the JDK 1.4 compliant way wrt to nano rolling>
 	 *
 	 * @return True if the JDK 1.4 (JDBC3) specification for {@link java.sql.Timestamp} nano rolling is adhered to.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	@Deprecated
     public static boolean jvmHasJDK14Timestamp() {
 		return JVM_HAS_JDK14_TIMESTAMP;
 	}
 
 	/**
 	 * Does this JVM support {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap}?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if {@link java.util.LinkedHashSet} and {@link java.util.LinkedHashMap} are available.
 	 *
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 * @see java.util.LinkedHashSet
 	 * @see java.util.LinkedHashMap
 	 */
 	@Deprecated
     public static boolean jvmSupportsLinkedHashCollections() {
 		return JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
 	}
 
 	/**
 	 * Does this JDK/JVM define the JDBC {@link Statement} interface with a 'getGeneratedKeys' method?
 	 * <p/>
 	 * Note, this is true for JDK 1.4 and above; hence the deprecation.
 	 *
 	 * @return True if generated keys can be retrieved via Statement; false otherwise.
 	 *
 	 * @see Statement
 	 * @deprecated Starting with 3.3 Hibernate requires JDK 1.4 or higher
 	 */
 	@Deprecated
     public static boolean jvmSupportsGetGeneratedKeys() {
 		return JVM_SUPPORTS_GET_GENERATED_KEYS;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 5b9c416c25..3628b4d4ae 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,441 +1,451 @@
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
+import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
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
+	private MultiTenancyStrategy multiTenancyStrategy;
 
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
+
+	public MultiTenancyStrategy getMultiTenancyStrategy() {
+		return multiTenancyStrategy;
+	}
+
+	void setMultiTenancyStrategy(MultiTenancyStrategy multiTenancyStrategy) {
+		this.multiTenancyStrategy = multiTenancyStrategy;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index fe355cc039..efcfec4ccf 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,325 +1,329 @@
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
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
+import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.QueryCacheFactory;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.impl.NoCachingRegionFactory;
 import org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.QueryTranslatorFactory;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.ServiceRegistry;
 import org.jboss.logging.Logger;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SettingsFactory.class.getName());
 
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
         LOG.autoFlush(enabledDisabled(flushBeforeCompletion));
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.autoSessionClose(enabledDisabled(autoCloseSession));
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) batchSize = 0;
 		if (batchSize>0) LOG.jdbcBatchSize(batchSize);
 		settings.setJdbcBatchSize(batchSize);
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if (batchSize > 0) LOG.jdbcBatchUpdates(enabledDisabled(jdbcBatchVersionedData));
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(Environment.USE_SCROLLABLE_RESULTSET, properties, meta.supportsScrollableResults());
         LOG.scrollabelResultSets(enabledDisabled(useScrollableResultSets));
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.wrapResultSets(enabledDisabled(wrapResultSets));
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.jdbc3GeneratedKeys(enabledDisabled(useGetGeneratedKeys));
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) LOG.jdbcResultSetFetchSize(statementFetchSize);
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.connectionReleaseMode(releaseModeName);
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
 
 		String defaultSchema = properties.getProperty(Environment.DEFAULT_SCHEMA);
 		String defaultCatalog = properties.getProperty(Environment.DEFAULT_CATALOG);
         if (defaultSchema != null) LOG.defaultSchema(defaultSchema);
         if (defaultCatalog != null) LOG.defaultCatalog(defaultCatalog);
 		settings.setDefaultSchemaName(defaultSchema);
 		settings.setDefaultCatalogName(defaultCatalog);
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger(Environment.MAX_FETCH_DEPTH, properties);
         if (maxFetchDepth != null) LOG.maxOuterJoinFetchDepth(maxFetchDepth);
 		settings.setMaximumFetchDepth(maxFetchDepth);
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
         LOG.defaultBatchFetchSize(batchFetchSize);
 		settings.setDefaultBatchFetchSize(batchFetchSize);
 
 		boolean comments = ConfigurationHelper.getBoolean(Environment.USE_SQL_COMMENTS, properties);
         LOG.generateSqlWithComments(enabledDisabled(comments));
 		settings.setCommentsEnabled(comments);
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean(Environment.ORDER_UPDATES, properties);
         LOG.orderSqlUpdatesByPrimaryKey(enabledDisabled(orderUpdates));
 		settings.setOrderUpdatesEnabled(orderUpdates);
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.orderSqlInsertsForBatching(enabledDisabled(orderInserts));
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory(properties) );
 
         Map querySubstitutions = ConfigurationHelper.toMap(Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties);
         LOG.queryLanguageSubstitutions(querySubstitutions);
 		settings.setQuerySubstitutions(querySubstitutions);
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
         LOG.jpaQlStrictCompliance(enabledDisabled(jpaqlCompliance));
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean(Environment.USE_SECOND_LEVEL_CACHE, properties, true);
         LOG.secondLevelCache(enabledDisabled(useSecondLevelCache));
 		settings.setSecondLevelCacheEnabled(useSecondLevelCache);
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.queryCache(enabledDisabled(useQueryCache));
 		settings.setQueryCacheEnabled(useQueryCache);
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ) ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.optimizeCacheForMinimalInputs(enabledDisabled(useMinimalPuts));
 		settings.setMinimalPutsEnabled(useMinimalPuts);
 
 		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
 		if ( StringHelper.isEmpty(prefix) ) prefix=null;
         if (prefix != null) LOG.cacheRegionPrefix(prefix);
 		settings.setCacheRegionPrefix(prefix);
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean(Environment.USE_STRUCTURED_CACHE, properties, false);
         LOG.structuredSecondLevelCacheEntries(enabledDisabled(useStructuredCacheEntries));
 		settings.setStructuredCacheEntriesEnabled(useStructuredCacheEntries);
 
 		if (useQueryCache) settings.setQueryCacheFactory( createQueryCacheFactory(properties) );
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean(Environment.GENERATE_STATISTICS, properties);
 		LOG.statistics( enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled(useStatistics);
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean(Environment.USE_IDENTIFIER_ROLLBACK, properties);
         LOG.deletedEntitySyntheticIdentifierRollback(enabledDisabled(useIdentifierRollback));
 		settings.setIdentifierRollbackEnabled(useIdentifierRollback);
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty(Environment.HBM2DDL_AUTO);
 		if ( "validate".equals(autoSchemaExport) ) settings.setAutoValidateSchema(true);
 		if ( "update".equals(autoSchemaExport) ) settings.setAutoUpdateSchema(true);
 		if ( "create".equals(autoSchemaExport) ) settings.setAutoCreateSchema(true);
 		if ( "create-drop".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema(true);
 			settings.setAutoDropSchema(true);
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
         LOG.defaultEntityMode(defaultEntityMode);
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.namedQueryChecking(enabledDisabled(namedQueryChecking));
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.checkNullability(enabledDisabled(checkNullability));
 		settings.setCheckNullability(checkNullability);
 
+		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
+		LOG.debug( "multi-tenancy strategy : " + multiTenancyStrategy );
+		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
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
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, "org.hibernate.cache.StandardQueryCacheFactory"
 		);
         LOG.queryCacheFactory(queryCacheFactoryClassName);
 		try {
 			return (QueryCacheFactory) ReflectHelper.classForName(queryCacheFactoryClassName).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, cnfe);
 		}
 	}
 
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
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
-        LOG.cacheRegionFactory(regionFactoryClassName);
+        LOG.cacheRegionFactory( regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException nsme ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.constructorWithPropertiesNotFound(regionFactoryClassName);
 				return (RegionFactory) ReflectHelper.classForName( regionFactoryClassName ).newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.ast.ASTQueryTranslatorFactory"
 		);
-        LOG.queryTranslator(className);
+        LOG.queryTranslator( className );
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
index d06b800ec4..fa751170e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
@@ -1,261 +1,263 @@
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
 package org.hibernate.engine;
 
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
+import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.impl.SessionFactoryImpl
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
 	 * Open a session conforming to the given parameters.  Used mainly by
 	 * {@link org.hibernate.context.JTASessionContext} for current session processing.
 	 *
 	 * @param connection The external jdbc connection to use, if one (i.e., optional).
 	 * @param flushBeforeCompletionEnabled Should the session be auto-flushed
 	 * prior to transaction completion?
 	 * @param autoCloseSessionEnabled Should the session be auto-closed after
 	 * transaction completion?
 	 * @param connectionReleaseMode The release mode for managed jdbc connections.
 	 * @return An appropriate session.
 	 * @throws HibernateException
 	 */
 	public Session openSession(
 			final Connection connection,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode) throws HibernateException;
 
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
 
-	public ServiceRegistry getServiceRegistry();
+	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index abcf983b75..19758ee96d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,366 +1,371 @@
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
 package org.hibernate.engine;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreationContext;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.event.EventListeners;
 import org.hibernate.impl.CriteriaImpl;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
-
 /**
- * Defines the internal contract between the <tt>Session</tt> and other parts of
- * Hibernate such as implementors of <tt>Type</tt> or <tt>EntityPersister</tt>.
+ * Defines the internal contract between {@link org.hibernate.Session} / {@link org.hibernate.StatelessSession} and
+ * other parts of Hibernate such as {@link Type}, {@link EntityPersister} and
+ * {@link org.hibernate.persister.collection.CollectionPersister} implementors
  *
- * @see org.hibernate.Session the interface to the application
- * @see org.hibernate.impl.SessionImpl the actual implementation
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public interface SessionImplementor extends Serializable, LobCreationContext {
+	/**
+	 * Provides access to JDBC connections
+	 *
+	 * @return The contract for accessing JDBC connections.
+	 */
+	public JdbcConnectionAccess getJdbcConnectionAccess();
 
 	/**
 	 * Retrieves the interceptor currently in use by this event source.
 	 *
 	 * @return The interceptor.
 	 */
 	public Interceptor getInterceptor();
 
 	/**
 	 * Enable/disable automatic cache clearing from after transaction
 	 * completion (for EJB3)
 	 */
 	public void setAutoClear(boolean enabled);
 
 	/**
 	 * Disable automatic transaction joining.  The really only has any effect for CMT transactions.  The default
 	 * Hibernate behavior is to auto join any active JTA transaction (register {@link javax.transaction.Synchronization}).
 	 * JPA however defines an explicit join transaction operation.
 	 *
 	 * See javax.persistence.EntityManager#joinTransaction
 	 */
 	public void disableTransactionAutoJoin();
 
 	/**
 	 * Does this <tt>Session</tt> have an active Hibernate transaction
 	 * or is there a JTA transaction in progress?
 	 */
 	public boolean isTransactionInProgress();
 
 	/**
 	 * Initialize the collection (if not already initialized)
 	 */
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException;
 
 	/**
 	 * Load an instance without checking if it was deleted.
 	 *
 	 * When <tt>nullable</tt> is disabled this method may create a new proxy or
 	 * return an existing proxy; if it does not exist, throw an exception.
 	 *
 	 * When <tt>nullable</tt> is enabled, the method does not create new proxies
 	 * (but might return an existing proxy); if it does not exist, return
 	 * <tt>null</tt>.
 	 *
 	 * When <tt>eager</tt> is enabled, the object is eagerly fetched
 	 */
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable)
 	throws HibernateException;
 
 	/**
 	 * Load an instance immediately. This method is only called when lazily initializing a proxy.
 	 * Do not return the proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * System time before the start of the transaction
 	 */
 	public long getTimestamp();
 	/**
 	 * Get the creating <tt>SessionFactoryImplementor</tt>
 	 */
 	public SessionFactoryImplementor getFactory();
 
 	/**
 	 * Execute a <tt>find()</tt> query
 	 */
 	public List list(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute an <tt>iterate()</tt> query
 	 */
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a <tt>scroll()</tt> query
 	 */
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a criteria query
 	 */
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode);
 	/**
 	 * Execute a criteria query
 	 */
 	public List list(CriteriaImpl criteria);
 
 	/**
 	 * Execute a filter
 	 */
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Iterate a filter
 	 */
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Get the <tt>EntityPersister</tt> for any instance
 	 * @param entityName optional entity name
 	 * @param object the entity instance
 	 */
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Get the entity instance associated with the given <tt>Key</tt>,
 	 * calling the Interceptor if necessary
 	 */
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException;
 
 	/**
 	 * Return the identifier of the persistent object, or null if
 	 * not associated with the session
 	 */
 	public Serializable getContextEntityIdentifier(Object object);
 
 	/**
 	 * The best guess entity name for an entity not in an association
 	 */
 	public String bestGuessEntityName(Object object);
 
 	/**
 	 * The guessed entity name for an entity not in an association
 	 */
 	public String guessEntityName(Object entity) throws HibernateException;
 
 	/**
 	 * Instantiate the entity class, initializing with the given identifier
 	 */
 	public Object instantiate(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a fully built list.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The result list.
 	 * @throws HibernateException
 	 */
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a scrollable result.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The resulting scrollable result.
 	 * @throws HibernateException
 	 */
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Retreive the currently set value for a filter parameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter parameter value.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Object getFilterParameterValue(String filterParameterName);
 
 	/**
 	 * Retreive the type for a given filter parrameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter param type
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Type getFilterParameterType(String filterParameterName);
 
 	/**
 	 * Return the currently enabled filters.  The filter map is keyed by filter
 	 * name, with values corresponding to the {@link org.hibernate.impl.FilterImpl}
 	 * instance.
 	 * @return The currently enabled filters.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Map getEnabledFilters();
 
 	public int getDontFlushFromFind();
 
 	/**
 	 * Retrieves the configured event listeners from this event source.
 	 *
 	 * @return The configured event listeners.
 	 */
 	public EventListeners getListeners();
 
 	//TODO: temporary
 
 	/**
 	 * Get the persistence context for this session
 	 */
 	public PersistenceContext getPersistenceContext();
 
 	/**
 	 * Execute a HQL update or delete query
 	 */
 	int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a native SQL update or delete query
 	 */
 	int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException;
 
 
 	/**
 	 * Return changes to this session that have not been flushed yet.
 	 *
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException;
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException;
 
 	// copied from Session:
 
 	public EntityMode getEntityMode();
 	public CacheMode getCacheMode();
 	public void setCacheMode(CacheMode cm);
 	public boolean isOpen();
 	public boolean isConnected();
 	public FlushMode getFlushMode();
 	public void setFlushMode(FlushMode fm);
 	public Connection connection();
 	public void flush();
 
 	/**
 	 * Get a Query instance for a named query or named native SQL query
 	 */
 	public Query getNamedQuery(String name);
 	/**
 	 * Get a Query instance for a named native SQL query
 	 */
 	public Query getNamedSQLQuery(String name);
 
 	public boolean isEventSource();
 
 	public void afterScrollOperation();
 
 	/**
 	 * Get the <i>internal</i> fetch profile currently associated with this session.
 	 *
 	 * @return The current internal fetch profile, or null if none currently associated.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public String getFetchProfile();
 
 	/**
 	 * Set the current <i>internal</i> fetch profile for this session.
 	 *
 	 * @param name The internal fetch profile name to use
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public void setFetchProfile(String name);
 
 	/**
 	 * Retrieve access to the session's transaction coordinator.
 	 *
 	 * @return The transaction coordinator.
 	 */
 	public TransactionCoordinator getTransactionCoordinator();
 
 	/**
 	 * Determine whether the session is closed.  Provided separately from
 	 * {@link #isOpen()} as this method does not attempt any JTA synchronization
 	 * registration, where as {@link #isOpen()} does; which makes this one
 	 * nicer to use for most internal purposes.
 	 *
 	 * @return True if the session is closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Get the load query influencers associated with this session.
 	 *
 	 * @return the load query influencers associated with this session;
 	 * should never be null.
 	 */
 	public LoadQueryInfluencers getLoadQueryInfluencers();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
index cc5125ac17..347ccb5875 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcCoordinatorImpl.java
@@ -1,240 +1,241 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.StatementPreparer;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.jdbc.WorkExecutor;
 
 import org.jboss.logging.Logger;
 
 /**
  * Standard Hibernate implementation of {@link JdbcCoordinator}
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  */
 public class JdbcCoordinatorImpl implements JdbcCoordinator {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JdbcCoordinatorImpl.class.getName());
 
 	private transient TransactionCoordinatorImpl transactionCoordinator;
 
 	private final transient LogicalConnectionImpl logicalConnection;
 
 	private transient Batch currentBatch;
 
 	public JdbcCoordinatorImpl(
 			Connection userSuppliedConnection,
 			TransactionCoordinatorImpl transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 		this.logicalConnection = new LogicalConnectionImpl(
 				userSuppliedConnection,
 				transactionCoordinator.getTransactionContext().getConnectionReleaseMode(),
-				transactionCoordinator.getTransactionContext().getTransactionEnvironment().getJdbcServices()
+				transactionCoordinator.getTransactionContext().getTransactionEnvironment().getJdbcServices(),
+				transactionCoordinator.getTransactionContext().getJdbcConnectionAccess()
 		);
 	}
 
 	private JdbcCoordinatorImpl(LogicalConnectionImpl logicalConnection) {
 		this.logicalConnection = logicalConnection;
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public LogicalConnectionImplementor getLogicalConnection() {
 		return logicalConnection;
 	}
 
 	protected TransactionEnvironment transactionEnvironment() {
 		return getTransactionCoordinator().getTransactionContext().getTransactionEnvironment();
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return transactionEnvironment().getSessionFactory();
 	}
 
 	protected BatchBuilder batchBuilder() {
 		return sessionFactory().getServiceRegistry().getService( BatchBuilder.class );
 	}
 
 	private SqlExceptionHelper sqlExceptionHelper() {
 		return transactionEnvironment().getJdbcServices().getSqlExceptionHelper();
 	}
 
 
 	private int flushDepth = 0;
 
 	@Override
 	public void flushBeginning() {
 		if ( flushDepth == 0 ) {
 			logicalConnection.disableReleases();
 		}
 		flushDepth++;
 	}
 
 	@Override
 	public void flushEnding() {
 		flushDepth--;
 		if ( flushDepth < 0 ) {
 			throw new HibernateException( "Mismatched flush handling" );
 		}
 		if ( flushDepth == 0 ) {
 			logicalConnection.enableReleases();
 		}
 	}
 
 	@Override
 	public Connection close() {
 		if ( currentBatch != null ) {
             LOG.closingUnreleasedBatch();
 			currentBatch.release();
 		}
 		return logicalConnection.close();
 	}
 
 	@Override
 	public Batch getBatch(BatchKey key) {
 		if ( currentBatch != null ) {
 			if ( currentBatch.getKey().equals( key ) ) {
 				return currentBatch;
 			}
 			else {
 				currentBatch.execute();
 				currentBatch.release();
 			}
 		}
 		currentBatch = batchBuilder().buildBatch( key, this );
 		return currentBatch;
 	}
 
 	@Override
 	public void abortBatch() {
 		if ( currentBatch != null ) {
 			currentBatch.release();
 		}
 	}
 
 	private transient StatementPreparer statementPreparer;
 
 	@Override
 	public StatementPreparer getStatementPreparer() {
 		if ( statementPreparer == null ) {
 			statementPreparer = new StatementPreparerImpl( this );
 		}
 		return statementPreparer;
 	}
 
 	@Override
 	public void setTransactionTimeOut(int timeOut) {
 		getStatementPreparer().setTransactionTimeOut( timeOut );
 	}
 
 	/**
 	 * To be called after local transaction completion.  Used to conditionally
 	 * release the JDBC connection aggressively if the configured release mode
 	 * indicates.
 	 */
 	public void afterTransaction() {
 		logicalConnection.afterTransaction();
 		if ( statementPreparer != null ) {
 			statementPreparer.unsetTransactionTimeOut();
 		}
 	}
 
 	@Override
 	public <T> T coordinateWork(WorkExecutorVisitable<T> work) {
 		Connection connection = getLogicalConnection().getDistinctConnectionProxy();
 		try {
 			T result = work.accept( new WorkExecutor<T>(), connection );
 			getLogicalConnection().afterStatementExecution();
 			return result;
 		}
 		catch ( SQLException e ) {
 			throw sqlExceptionHelper().convert( e, "error executing work" );
 		}
 		finally {
 			try {
 				if ( ! connection.isClosed() ) {
 					connection.close();
 				}
 			}
 			catch (SQLException e) {
                 LOG.debug("Error closing connection proxy", e);
 			}
 		}
 	}
 
 	public void executeBatch() {
 		if ( currentBatch != null ) {
 			currentBatch.execute();
 			currentBatch.release(); // needed?
 		}
 	}
 
 	@Override
 	public void cancelLastQuery() {
 		logicalConnection.getResourceRegistry().cancelLastQuery();
 	}
 
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		if ( ! logicalConnection.isReadyForSerialization() ) {
 			throw new HibernateException( "Cannot serialize Session while connected" );
 		}
 		logicalConnection.serialize( oos );
 	}
 
 	public static JdbcCoordinatorImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws IOException, ClassNotFoundException {
 		return new JdbcCoordinatorImpl( LogicalConnectionImpl.deserialize( ois, transactionContext ) );
  	}
 
 	public void afterDeserialize(TransactionCoordinatorImpl transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index 183293c6f9..400f575011 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,359 +1,425 @@
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
 package org.hibernate.engine.jdbc.internal;
+
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
+import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateLogger;
+import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.Configurable;
-import org.hibernate.service.spi.InjectService;
-import org.jboss.logging.Logger;
+import org.hibernate.service.spi.ServiceRegistryAwareService;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
-public class JdbcServicesImpl implements JdbcServices, Configurable {
-
+public class JdbcServicesImpl implements JdbcServices, ServiceRegistryAwareService, Configurable {
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JdbcServicesImpl.class.getName());
 
-	private ConnectionProvider connectionProvider;
-
-	@InjectService
-	public void setConnectionProvider(ConnectionProvider connectionProvider) {
-		this.connectionProvider = connectionProvider;
-	}
-
-	private DialectFactory dialectFactory;
-
-	@InjectService
-	public void setDialectFactory(DialectFactory dialectFactory) {
-		this.dialectFactory = dialectFactory;
-	}
+	private ServiceRegistryImplementor serviceRegistry;
 
 	private Dialect dialect;
+	private ConnectionProvider connectionProvider;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
 	private LobCreatorBuilder lobCreatorBuilder;
 
+	@Override
+	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
+		this.serviceRegistry = serviceRegistry;
+	}
+
+	@Override
 	public void configure(Map configValues) {
+		final JdbcConnectionAccess jdbcConnectionAccess = buildJdbcConnectionAccess( configValues );
+		final DialectFactory dialectFactory = serviceRegistry.getService( DialectFactory.class );
+
 		Dialect dialect = null;
 		LobCreatorBuilder lobCreatorBuilder = null;
 
 		boolean metaSupportsScrollable = false;
 		boolean metaSupportsGetGeneratedKeys = false;
 		boolean metaSupportsBatchUpdates = false;
 		boolean metaReportsDDLCausesTxnCommit = false;
 		boolean metaReportsDDLInTxnSupported = true;
 		String extraKeywordsString = "";
 		int sqlStateType = -1;
 		boolean lobLocatorUpdateCopy = false;
 		String catalogName = null;
 		String schemaName = null;
 		LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 
 		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
 		// The need for it is intended to be alleviated with future development, thus it is
 		// not defined as an Environment constant...
 		//
 		// it is used to control whether we should consult the JDBC metadata to determine
 		// certain Settings default values; it is useful to *not* do this when the database
 		// may not be available (mainly in tools usage).
 		boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", configValues, true );
 		if ( useJdbcMetadata ) {
 			try {
-				Connection conn = connectionProvider.getConnection();
+				Connection connection = jdbcConnectionAccess.obtainConnection();
 				try {
-					DatabaseMetaData meta = conn.getMetaData();
+					DatabaseMetaData meta = connection.getMetaData();
                     LOG.database(meta.getDatabaseProductName(),
                                  meta.getDatabaseProductVersion(),
                                  meta.getDatabaseMajorVersion(),
                                  meta.getDatabaseMinorVersion());
                     LOG.driver(meta.getDriverName(),
                                meta.getDriverVersion(),
                                meta.getDriverMajorVersion(),
                                meta.getDriverMinorVersion());
                     LOG.jdbcVersion(meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion());
 
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfoExtracter.extractTypeInfo( meta ) );
 
-					dialect = dialectFactory.buildDialect( configValues, conn );
+					dialect = dialectFactory.buildDialect( configValues, connection );
 
-					catalogName = conn.getCatalog();
+					catalogName = connection.getCatalog();
 					SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
-						schemaName = schemaNameResolver.resolveSchemaName( conn );
+						schemaName = schemaNameResolver.resolveSchemaName( connection );
 					}
-					lobCreatorBuilder = new LobCreatorBuilder( configValues, conn );
+					lobCreatorBuilder = new LobCreatorBuilder( configValues, connection );
 				}
 				catch ( SQLException sqle ) {
                     LOG.unableToObtainConnectionMetadata(sqle.getMessage());
 				}
 				finally {
-					if ( conn != null ) {
-						connectionProvider.closeConnection( conn );
+					if ( connection != null ) {
+						jdbcConnectionAccess.releaseConnection( connection );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
                 LOG.unableToObtainConnectionToQueryMetadata(sqle.getMessage());
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 			catch ( UnsupportedOperationException uoe ) {
 				// user supplied JDBC connections
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 		}
 		else {
 			dialect = dialectFactory.buildDialect( configValues, null );
 		}
 
 		final boolean showSQL = ConfigurationHelper.getBoolean( Environment.SHOW_SQL, configValues, false );
 		final boolean formatSQL = ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, configValues, false );
 
 		this.dialect = dialect;
 		this.lobCreatorBuilder = (
 				lobCreatorBuilder == null ?
 						new LobCreatorBuilder( configValues, null ) :
 						lobCreatorBuilder
 		);
 
 		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
 		this.sqlExceptionHelper = new SqlExceptionHelper( dialect.buildSQLExceptionConverter() );
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
 				metaSupportsScrollable,
 				metaSupportsGetGeneratedKeys,
 				metaSupportsBatchUpdates,
 				metaReportsDDLInTxnSupported,
 				metaReportsDDLCausesTxnCommit,
 				parseKeywords( extraKeywordsString ),
 				parseSQLStateType( sqlStateType ),
 				lobLocatorUpdateCopy,
 				schemaName,
 				catalogName,
 				typeInfoSet
 		);
 	}
 
+	private JdbcConnectionAccess buildJdbcConnectionAccess(Map configValues) {
+		final MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configValues );
+
+		if ( MultiTenancyStrategy.NONE == multiTenancyStrategy ) {
+			connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
+			return new ConnectionProviderJdbcConnectionAccess( connectionProvider );
+		}
+		else {
+			connectionProvider = null;
+			final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService( MultiTenantConnectionProvider.class );
+			return new MultiTenantConnectionProviderJdbcConnectionAccess( multiTenantConnectionProvider );
+		}
+	}
+
+	private static class ConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
+		private final ConnectionProvider connectionProvider;
+
+		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
+			this.connectionProvider = connectionProvider;
+		}
+
+		@Override
+		public Connection obtainConnection() throws SQLException {
+			return connectionProvider.getConnection();
+		}
+
+		@Override
+		public void releaseConnection(Connection connection) throws SQLException {
+			connection.close();
+		}
+	}
+
+	private static class MultiTenantConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
+		private final MultiTenantConnectionProvider connectionProvider;
+
+		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
+			this.connectionProvider = connectionProvider;
+		}
+
+		@Override
+		public Connection obtainConnection() throws SQLException {
+			return connectionProvider.getAnyConnection();
+		}
+
+		@Override
+		public void releaseConnection(Connection connection) throws SQLException {
+			connection.close();
+		}
+	}
+
+
 	// todo : add to Environment
 	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
 
 	private SchemaNameResolver determineExplicitSchemaNameResolver(Map configValues) {
 		Object setting = configValues.get( SCHEMA_NAME_RESOLVER );
 		if ( SchemaNameResolver.class.isInstance( setting ) ) {
 			return (SchemaNameResolver) setting;
 		}
 
 		String resolverClassName = (String) setting;
 		if ( resolverClassName != null ) {
 			try {
 				Class resolverClass = ReflectHelper.classForName( resolverClassName, getClass() );
 				return (SchemaNameResolver) ReflectHelper.getDefaultConstructor( resolverClass ).newInstance();
 			}
 			catch ( ClassNotFoundException e ) {
                 LOG.unableToLocateConfiguredSchemaNameResolver(resolverClassName, e.toString());
 			}
 			catch ( InvocationTargetException e ) {
                 LOG.unableToInstantiateConfiguredSchemaNameResolver(resolverClassName, e.getTargetException().toString());
 			}
 			catch ( Exception e ) {
                 LOG.unableToInstantiateConfiguredSchemaNameResolver(resolverClassName, e.toString());
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		Set<String> keywordSet = new HashSet<String>();
-		for ( String keyword : extraKeywordsString.split( "," ) ) {
-			keywordSet.add( keyword );
-		}
+		keywordSet.addAll( Arrays.asList( extraKeywordsString.split( "," ) ) );
 		return keywordSet;
 	}
 
 	private ExtractedDatabaseMetaData.SQLStateType parseSQLStateType(int sqlStateType) {
 		switch ( sqlStateType ) {
 			case DatabaseMetaData.sqlStateSQL99 : {
 				return ExtractedDatabaseMetaData.SQLStateType.SQL99;
 			}
 			case DatabaseMetaData.sqlStateXOpen : {
 				return ExtractedDatabaseMetaData.SQLStateType.XOpen;
 			}
 			default : {
 				return ExtractedDatabaseMetaData.SQLStateType.UNKOWN;
 			}
 		}
 	}
 
 	private static class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
 		private final boolean supportsScrollableResults;
 		private final boolean supportsGetGeneratedKeys;
 		private final boolean supportsBatchUpdates;
 		private final boolean supportsDataDefinitionInTransaction;
 		private final boolean doesDataDefinitionCauseTransactionCommit;
 		private final Set<String> extraKeywords;
 		private final SQLStateType sqlStateType;
 		private final boolean lobLocatorUpdateCopy;
 		private final String connectionSchemaName;
 		private final String connectionCatalogName;
 		private final LinkedHashSet<TypeInfo> typeInfoSet;
 
 		private ExtractedDatabaseMetaDataImpl(
 				boolean supportsScrollableResults,
 				boolean supportsGetGeneratedKeys,
 				boolean supportsBatchUpdates,
 				boolean supportsDataDefinitionInTransaction,
 				boolean doesDataDefinitionCauseTransactionCommit,
 				Set<String> extraKeywords,
 				SQLStateType sqlStateType,
 				boolean lobLocatorUpdateCopy,
 				String connectionSchemaName,
 				String connectionCatalogName,
 				LinkedHashSet<TypeInfo> typeInfoSet) {
 			this.supportsScrollableResults = supportsScrollableResults;
 			this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
 			this.supportsBatchUpdates = supportsBatchUpdates;
 			this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
 			this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
 			this.extraKeywords = extraKeywords;
 			this.sqlStateType = sqlStateType;
 			this.lobLocatorUpdateCopy = lobLocatorUpdateCopy;
 			this.connectionSchemaName = connectionSchemaName;
 			this.connectionCatalogName = connectionCatalogName;
 			this.typeInfoSet = typeInfoSet;
 		}
 
+		@Override
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
+		@Override
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
+		@Override
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
+		@Override
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
+		@Override
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
+		@Override
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
+		@Override
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
+		@Override
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
+		@Override
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
+		@Override
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
+		@Override
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
+	@Override
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
+	@Override
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
+	@Override
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
+	@Override
 	public Dialect getDialect() {
 		return dialect;
 	}
 
+	@Override
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
 		return lobCreatorBuilder.buildLobCreator( lobCreationContext );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public ResultSetWrapper getResultSetWrapper() {
 		return ResultSetWrapperImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
index c6ed725dcf..8220ef8d09 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/LogicalConnectionImpl.java
@@ -1,432 +1,445 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcResourceRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.jdbc.spi.NonDurableConnectionObserver;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Standard Hibernate {@link org.hibernate.engine.jdbc.spi.LogicalConnection} implementation
  * <p/>
  * IMPL NOTE : Custom serialization handling!
  *
  * @author Steve Ebersole
  */
 public class LogicalConnectionImpl implements LogicalConnectionImplementor {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, LogicalConnectionImpl.class.getName());
 
 	private transient Connection physicalConnection;
 	private transient Connection shareableConnectionProxy;
 
 	private final transient ConnectionReleaseMode connectionReleaseMode;
 	private final transient JdbcServices jdbcServices;
+	private final transient JdbcConnectionAccess jdbcConnectionAccess;
 	private final transient JdbcResourceRegistry jdbcResourceRegistry;
 	private final transient List<ConnectionObserver> observers;
 
 	private boolean releasesEnabled = true;
 
 	private final boolean isUserSuppliedConnection;
 
 	private boolean isClosed;
 
 	public LogicalConnectionImpl(
 			Connection userSuppliedConnection,
 			ConnectionReleaseMode connectionReleaseMode,
-			JdbcServices jdbcServices) {
+			JdbcServices jdbcServices,
+			JdbcConnectionAccess jdbcConnectionAccess) {
 		this(
 				connectionReleaseMode,
 				jdbcServices,
+				jdbcConnectionAccess,
 				(userSuppliedConnection != null),
 				false,
 				new ArrayList<ConnectionObserver>()
 		);
 		this.physicalConnection = userSuppliedConnection;
 	}
 
 	private LogicalConnectionImpl(
 			ConnectionReleaseMode connectionReleaseMode,
 			JdbcServices jdbcServices,
+			JdbcConnectionAccess jdbcConnectionAccess,
 			boolean isUserSuppliedConnection,
 			boolean isClosed,
 			List<ConnectionObserver> observers) {
 		this.connectionReleaseMode = determineConnectionReleaseMode(
 				jdbcServices, isUserSuppliedConnection, connectionReleaseMode
 		);
 		this.jdbcServices = jdbcServices;
+		this.jdbcConnectionAccess = jdbcConnectionAccess;
 		this.jdbcResourceRegistry = new JdbcResourceRegistryImpl( getJdbcServices().getSqlExceptionHelper() );
 		this.observers = observers;
 
 		this.isUserSuppliedConnection = isUserSuppliedConnection;
 		this.isClosed = isClosed;
 	}
 
 	private static ConnectionReleaseMode determineConnectionReleaseMode(
 			JdbcServices jdbcServices,
 			boolean isUserSuppliedConnection,
 			ConnectionReleaseMode connectionReleaseMode) {
 		if ( isUserSuppliedConnection ) {
 			return ConnectionReleaseMode.ON_CLOSE;
 		}
 		else if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 				! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
             LOG.debugf("Connection provider reports to not support aggressive release; overriding");
 			return ConnectionReleaseMode.AFTER_TRANSACTION;
 		}
 		else {
 			return connectionReleaseMode;
 		}
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	@Override
 	public JdbcResourceRegistry getResourceRegistry() {
 		return jdbcResourceRegistry;
 	}
 
 	@Override
 	public void addObserver(ConnectionObserver observer) {
 		observers.add( observer );
 	}
 
 	@Override
 	public void removeObserver(ConnectionObserver connectionObserver) {
 		observers.remove( connectionObserver );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed;
 	}
 
 	@Override
 	public boolean isPhysicallyConnected() {
 		return physicalConnection != null;
 	}
 
 	@Override
 	public Connection getConnection() throws HibernateException {
 		if ( isClosed ) {
 			throw new HibernateException( "Logical connection is closed" );
 		}
 		if ( physicalConnection == null ) {
 			if ( isUserSuppliedConnection ) {
 				// should never happen
 				throw new HibernateException( "User-supplied connection was null" );
 			}
 			obtainConnection();
 		}
 		return physicalConnection;
 	}
 
 	@Override
 	public Connection getShareableConnectionProxy() {
 		if ( shareableConnectionProxy == null ) {
 			shareableConnectionProxy = buildConnectionProxy();
 		}
 		return shareableConnectionProxy;
 	}
 
 	private Connection buildConnectionProxy() {
 		return ProxyBuilder.buildConnection( this );
 	}
 
 	@Override
 	public Connection getDistinctConnectionProxy() {
 		return buildConnectionProxy();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection close() {
 		LOG.trace( "Closing logical connection" );
 		Connection c = isUserSuppliedConnection ? physicalConnection : null;
 		try {
 			releaseProxies();
 			jdbcResourceRegistry.close();
 			if ( !isUserSuppliedConnection && physicalConnection != null ) {
 				releaseConnection();
 			}
 			return c;
 		}
 		finally {
 			// no matter what
 			physicalConnection = null;
 			isClosed = true;
             LOG.trace("Logical connection closed");
 			for ( ConnectionObserver observer : observers ) {
 				observer.logicalConnectionClosed();
 			}
 			observers.clear();
 		}
 	}
 
 	private void releaseProxies() {
 		if ( shareableConnectionProxy != null ) {
 			try {
 				shareableConnectionProxy.close();
 			}
 			catch (SQLException e) {
 				LOG.debug( "Error releasing shared connection proxy", e );
 			}
 		}
 		shareableConnectionProxy = null;
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public void afterStatementExecution() {
         LOG.trace("Starting after statement execution processing [" + connectionReleaseMode + "]");
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 			if ( ! releasesEnabled ) {
                 LOG.debugf("Skipping aggressive release due to manual disabling");
 				return;
 			}
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
                 LOG.debugf("Skipping aggressive release due to registered resources");
 				return;
 			}
 			releaseConnection();
 		}
 	}
 
 	@Override
 	public void afterTransaction() {
 		if ( connectionReleaseMode == ConnectionReleaseMode.AFTER_STATEMENT ||
 				connectionReleaseMode == ConnectionReleaseMode.AFTER_TRANSACTION ) {
 			if ( jdbcResourceRegistry.hasRegisteredResources() ) {
                 LOG.forcingContainerResourceCleanup();
 				jdbcResourceRegistry.releaseResources();
 			}
 			aggressiveRelease();
 		}
 	}
 
 	@Override
 	public void disableReleases() {
         LOG.trace("Disabling releases");
 		releasesEnabled = false;
 	}
 
 	@Override
 	public void enableReleases() {
         LOG.trace("(Re)enabling releases");
 		releasesEnabled = true;
 		afterStatementExecution();
 	}
 
 	/**
 	 * Force aggressive release of the underlying connection.
 	 */
 	public void aggressiveRelease() {
         if (isUserSuppliedConnection) LOG.debugf("Cannot aggressively release user-supplied connection; skipping");
 		else {
             LOG.debugf("Aggressively releasing JDBC connection");
 			if ( physicalConnection != null ) {
 				releaseConnection();
 			}
 		}
 	}
 
 
 	/**
 	 * Physically opens a JDBC Connection.
 	 *
 	 * @throws org.hibernate.JDBCException Indicates problem opening a connection
 	 */
 	private void obtainConnection() throws JDBCException {
         LOG.debugf("Obtaining JDBC connection");
 		try {
-			physicalConnection = getJdbcServices().getConnectionProvider().getConnection();
+			physicalConnection = jdbcConnectionAccess.obtainConnection();
 			for ( ConnectionObserver observer : observers ) {
 				observer.physicalConnectionObtained( physicalConnection );
 			}
             LOG.debugf("Obtained JDBC connection");
 		}
 		catch ( SQLException sqle) {
 			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "Could not open connection" );
 		}
 	}
 
 	/**
 	 * Physically closes the JDBC Connection.
 	 *
 	 * @throws JDBCException Indicates problem closing a connection
 	 */
 	private void releaseConnection() throws JDBCException {
         LOG.debugf("Releasing JDBC connection");
-		if ( physicalConnection == null ) return;
+		if ( physicalConnection == null ) {
+			return;
+		}
 		try {
-            if (!physicalConnection.isClosed()) getJdbcServices().getSqlExceptionHelper().logAndClearWarnings(physicalConnection);
-            if (!isUserSuppliedConnection) getJdbcServices().getConnectionProvider().closeConnection(physicalConnection);
+            if ( ! physicalConnection.isClosed() ) {
+				getJdbcServices().getSqlExceptionHelper().logAndClearWarnings( physicalConnection );
+			}
+            if ( ! isUserSuppliedConnection ) {
+				jdbcConnectionAccess.releaseConnection( physicalConnection );
+			}
 		}
-		catch (SQLException sqle) {
-			throw getJdbcServices().getSqlExceptionHelper().convert( sqle, "Could not close connection" );
+		catch (SQLException e) {
+			throw getJdbcServices().getSqlExceptionHelper().convert( e, "Could not close connection" );
 		}
 		finally {
 			physicalConnection = null;
 		}
         LOG.debugf("Released JDBC connection");
 		for ( ConnectionObserver observer : observers ) {
 			observer.physicalConnectionReleased();
 		}
 		releaseNonDurableObservers();
 	}
 
 	private void releaseNonDurableObservers() {
 		Iterator observers = this.observers.iterator();
 		while ( observers.hasNext() ) {
 			if ( NonDurableConnectionObserver.class.isInstance( observers.next() ) ) {
 				observers.remove();
 			}
 		}
 	}
 
 	@Override
 	public Connection manualDisconnect() {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually disconnect because logical connection is already closed" );
 		}
 		releaseProxies();
 		Connection c = physicalConnection;
 		jdbcResourceRegistry.releaseResources();
 		releaseConnection();
 		return c;
 	}
 
 	@Override
 	public void manualReconnect(Connection suppliedConnection) {
 		if ( isClosed ) {
 			throw new IllegalStateException( "cannot manually reconnect because logical connection is already closed" );
 		}
 		if ( !isUserSuppliedConnection ) {
 			throw new IllegalStateException( "cannot manually reconnect unless Connection was originally supplied" );
 		}
 		else {
 			if ( suppliedConnection == null ) {
 				throw new IllegalArgumentException( "cannot reconnect a null user-supplied connection" );
 			}
 			else if ( suppliedConnection == physicalConnection ) {
                 LOG.debug("reconnecting the same connection that is already connected; should this connection have been disconnected?");
 			}
 			else if ( physicalConnection != null ) {
 				throw new IllegalArgumentException(
 						"cannot reconnect to a new user-supplied connection because currently connected; must disconnect before reconnecting."
 				);
 			}
 			physicalConnection = suppliedConnection;
             LOG.debugf("Reconnected JDBC connection");
 		}
 	}
 
 	@Override
 	public boolean isAutoCommit() {
 		if ( !isOpen() || ! isPhysicallyConnected() ) {
 			return true;
 		}
 
 		try {
 			return getConnection().getAutoCommit();
 		}
 		catch (SQLException e) {
 			throw jdbcServices.getSqlExceptionHelper().convert( e, "could not inspect JDBC autocommit mode" );
 		}
 	}
 
 	@Override
 	public void notifyObserversStatementPrepared() {
 		for ( ConnectionObserver observer : observers ) {
 			observer.statementPrepared();
 		}
 	}
 
 	@Override
 	public boolean isReadyForSerialization() {
 		return isUserSuppliedConnection
 				? ! isPhysicallyConnected()
 				: ! getResourceRegistry().hasRegisteredResources();
 	}
 
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeBoolean( isUserSuppliedConnection );
 		oos.writeBoolean( isClosed );
 		List<ConnectionObserver> durableConnectionObservers = new ArrayList<ConnectionObserver>();
 		for ( ConnectionObserver observer : observers ) {
 			if ( ! NonDurableConnectionObserver.class.isInstance( observer ) ) {
 				durableConnectionObservers.add( observer );
 			}
 		}
 		oos.writeInt( durableConnectionObservers.size() );
 		for ( ConnectionObserver observer : durableConnectionObservers ) {
 			oos.writeObject( observer );
 		}
 	}
 
 	public static LogicalConnectionImpl deserialize(
 			ObjectInputStream ois,
 			TransactionContext transactionContext) throws IOException, ClassNotFoundException {
 		boolean isUserSuppliedConnection = ois.readBoolean();
 		boolean isClosed = ois.readBoolean();
 		int observerCount = ois.readInt();
 		List<ConnectionObserver> observers = CollectionHelper.arrayList( observerCount );
 		for ( int i = 0; i < observerCount; i++ ) {
 			observers.add( (ConnectionObserver) ois.readObject() );
 		}
 		return new LogicalConnectionImpl(
 				transactionContext.getConnectionReleaseMode(),
 				transactionContext.getTransactionEnvironment().getJdbcServices(),
+				transactionContext.getJdbcConnectionAccess(),
 				isUserSuppliedConnection,
 				isClosed,
 				observers
 		);
  	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
deleted file mode 100644
index b76fcf467c..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ConnectionManager.java
+++ /dev/null
@@ -1,210 +0,0 @@
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
-package org.hibernate.engine.jdbc.spi;
-import java.io.Serializable;
-import java.sql.CallableStatement;
-import java.sql.Connection;
-import java.sql.PreparedStatement;
-import org.hibernate.HibernateException;
-import org.hibernate.ScrollMode;
-import org.hibernate.jdbc.Expectation;
-
-/**
- * Encapsulates JDBC Connection management SPI.
- * <p/>
- * The lifecycle is intended to span a logical series of interactions with the
- * database.  Internally, this means the the lifecycle of the Session.
- *
- * @author Gail Badner
- */
-public interface ConnectionManager extends Serializable {
-
-	/**
-	 * Retrieves the connection currently managed by this ConnectionManager.
-	 * <p/>
-	 * Note, that we may need to obtain a connection to return here if a
-	 * connection has either not yet been obtained (non-UserSuppliedConnectionProvider)
-	 * or has previously been aggressively released (if supported in this environment).
-	 *
-	 * @return The current Connection.
-	 *
-	 * @throws HibernateException Indicates a connection is currently not
-	 * available (we are currently manually disconnected).
-	 */
-	Connection getConnection();
-
-	// TODO: should this be removd from the SPI?
-	boolean hasBorrowedConnection();
-
-	// TODO: should this be removd from the SPI?
-	void releaseBorrowedConnection();
-
-	/**
-	 * Is this ConnectionManager instance "logically" connected.  Meaning
-	 * do we either have a cached connection available or do we have the
-	 * ability to obtain a connection on demand.
-	 *
-	 * @return True if logically connected; false otherwise.
-	 */
-	boolean isCurrentlyConnected();
-
-	/**
-	 * To be called after execution of each JDBC statement.  Used to
-	 * conditionally release the JDBC connection aggressively if
-	 * the configured release mode indicates.
-	 */
-	void afterStatement();
-
-	/**
-	 * Sets the transaction timeout.
-	 * @param seconds - number of seconds until the the transaction times out.
-	 */
-	void setTransactionTimeout(int seconds);
-
-	/**
-	 * To be called after Session completion.  Used to release the JDBC
-	 * connection.
-	 *
-	 * @return The connection mantained here at time of close.  Null if
-	 * there was no connection cached internally.
-	 */
-	Connection close();
-
-	/**
-	 * Manually disconnect the underlying JDBC Connection.  The assumption here
-	 * is that the manager will be reconnected at a later point in time.
-	 *
-	 * @return The connection mantained here at time of disconnect.  Null if
-	 * there was no connection cached internally.
-	 */
-	Connection manualDisconnect();
-
-	/**
-	 * Manually reconnect the underlying JDBC Connection.  Should be called at
-	 * some point after manualDisconnect().
-	 * <p/>
-	 * This form is used for ConnectionProvider-supplied connections.
-	 */
-	void manualReconnect();
-
-	/**
-	 * Manually reconnect the underlying JDBC Connection.  Should be called at
-	 * some point after manualDisconnect().
-	 * <p/>
-	 * This form is used for user-supplied connections.
-	 */
-	void manualReconnect(Connection suppliedConnection);
-
-	/**
-	 * Callback to let us know that a flush is beginning.  We use this fact
-	 * to temporarily circumvent aggressive connection releasing until after
-	 * the flush cycle is complete {@link #flushEnding()}
-	 */
-	void flushBeginning();
-
-	/**
-	 * Callback to let us know that a flush is ending.  We use this fact to
-	 * stop circumventing aggressive releasing connections.
-	 */
-	void flushEnding();
-
-	/**
-	 * Get a non-batchable prepared statement to use for inserting / deleting / updating,
-	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, int)}).
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
-	 * released when the session is closed or disconnected.
-	 */
-	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys);
-
-	/**
-	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
-	 * using JDBC3 getGeneratedKeys ({@link java.sql.Connection#prepareStatement(String, String[])}).
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
-	 * released when the session is closed or disconnected.
-	 */
-	public PreparedStatement prepareStatement(String sql, String[] columnNames);
-
-	/**
-	 * Get a non-batchable prepared statement to use for selecting. Does not
-	 * result in execution of the current batch.
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
-	 * it will be released when the session is closed or disconnected.
-	 */
-	public PreparedStatement prepareSelectStatement(String sql);
-
-	/**
-	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
-	 * released when the session is closed or disconnected.
-	 */
-	public PreparedStatement prepareStatement(String sql, boolean isCallable);
-
-	/**
-	 * Get a non-batchable callable statement to use for inserting / deleting / updating.
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
-	 * released when the session is closed or disconnected.
-	 */
-	public CallableStatement prepareCallableStatement(String sql);
-
-	/**
-	 * Get a batchable prepared statement to use for inserting / deleting / updating
-	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
-	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
-	 * statement explicitly.
-	 * @see org.hibernate.engine.jdbc.batch.spi.Batch#addToBatch
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()}, it will be
-	 * released when the session is closed or disconnected.
-	 */
-	public PreparedStatement prepareBatchStatement(Object key, String sql, boolean isCallable);
-
-	/**
-	 * Get a prepared statement for use in loading / querying. Does not
-	 * result in execution of the current batch.
-	 * <p/>
-	 * If not explicitly closed via {@link java.sql.PreparedStatement#close()},
-	 * it will be released when the session is closed or disconnected.
-	 */
-	public PreparedStatement prepareQueryStatement(
-			String sql,
-			boolean isScrollable,
-			ScrollMode scrollMode,
-			boolean isCallable);
-	
-	/**
-	 * Cancel the current query statement
-	 */
-	public void cancelLastQuery();
-
-	public void abortBatch();
-
-	public void addToBatch(Object batchKey, String sql, Expectation expectation);
-
-	public void executeBatch();
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java
new file mode 100644
index 0000000000..03b8438610
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcConnectionAccess.java
@@ -0,0 +1,39 @@
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
+package org.hibernate.engine.jdbc.spi;
+
+import java.io.Serializable;
+import java.sql.Connection;
+import java.sql.SQLException;
+
+/**
+ * Provides centralized access to JDBC connections.  Centralized to hide the complexity of accounting for contextual
+ * (multi-tenant) versus non-contextual access.
+ *
+ * @author Steve Ebersole
+ */
+public interface JdbcConnectionAccess extends Serializable {
+	public Connection obtainConnection() throws SQLException;
+	public void releaseConnection(Connection connection) throws SQLException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionContext.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionContext.java
index 85be89fd6e..a172afd071 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionContext.java
@@ -1,113 +1,116 @@
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
 package org.hibernate.engine.transaction.spi;
 
 import org.hibernate.ConnectionReleaseMode;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 
 import java.io.Serializable;
 
 /**
  * Access to services needed in the context of processing transaction requests.
  * <p/>
  * The context is roughly speaking equivalent to the Hibernate session, as opposed to the {@link TransactionEnvironment}
  * which is roughly equivalent to the Hibernate session factory
  * 
  * @author Steve Ebersole
  */
 public interface TransactionContext extends Serializable {
 	/**
 	 * Obtain the {@link TransactionEnvironment} associated with this context.
 	 *
 	 * @return The transaction environment.
 	 */
 	public TransactionEnvironment getTransactionEnvironment();
 
 	/**
 	 * Get the mode for releasing JDBC connection in effect for ths context.
 	 *
 	 * @return The connection release mode.
 	 */
 	public ConnectionReleaseMode getConnectionReleaseMode();
 
 	/**
 	 * Should transactions be auto joined?  Generally this is only a concern for CMT transactions.  The default
 	 * should be to auto join.  JPA defines an explicit operation for joining a CMT transaction.
 	 *
 	 * @return Should we automatically join transactions
 	 */
 	public boolean shouldAutoJoinTransaction();
 
 	/**
 	 * Should session automatically be closed after transaction completion in this context?
 	 *
 	 * @return {@literal true}/{@literal false} appropriately.
 	 */
 	public boolean isAutoCloseSessionEnabled();
 
 	/**
 	 * Is this context already closed?
 	 *
 	 * @return {@literal true}/{@literal false} appropriately.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Should flushes only happen manually for this context?
 	 *
 	 * @return {@literal true}/{@literal false} appropriately.
 	 */
 	public boolean isFlushModeNever();
 
 	/**
 	 * Should before transaction completion processing perform a flush when initiated from JTA synchronization for this
 	 * context?
 	 *
 	 * @return {@literal true}/{@literal false} appropriately.
 	 */
 	public boolean isFlushBeforeCompletionEnabled();
 
 	/**
 	 * Perform a managed flush.
 	 */
 	public void managedFlush();
 
 	/**
 	 * Should JTA synchronization processing perform a automatic close (call to {@link #managedClose} for this
 	 * context?
 	 * 
 	 * @return {@literal true}/{@literal false} appropriately.
 	 */
 	public boolean shouldAutoClose();
 
 	/**
 	 * Perform a managed close.
 	 */
 	public void managedClose();
 
 	public void afterTransactionBegin(TransactionImplementor hibernateTransaction);
 
 	public void beforeTransactionCompletion(TransactionImplementor hibernateTransaction);
 
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful);
+
+	public JdbcConnectionAccess getJdbcConnectionAccess();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
index ad2ff42fbb..6375181110 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
@@ -1,206 +1,284 @@
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
 package org.hibernate.impl;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.List;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
+import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
+import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract, SessionImplementor, TransactionContext {
 
 	protected transient SessionFactoryImpl factory;
+	private String tenantIdentifier;
 	private boolean closed = false;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory) {
 		this.factory = factory;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return factory.getTransactionEnvironment();
 	}
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getTransactionCoordinator().getJdbcCoordinator().coordinateWork(
 				new WorkExecutorVisitable<T>() {
 					@Override
 					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 						try {
 							return callback.executeOnConnection( connection );
 						}
 						catch (SQLException e) {
 							throw getFactory().getSQLExceptionHelper().convert(
 									e,
 									"Error creating contextual LOB : " + e.getMessage()
 							);
 						}
 					}
 				}
 		);
 	}
 
 	public boolean isClosed() {
 		return closed;
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( closed ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			String queryString = nqd.getQueryString();
 			query = new QueryImpl(
 					queryString,
 			        nqd.getFlushMode(),
 			        this,
 			        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 			);
 			query.setComment( "named HQL query " + queryName );
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 			        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 			);
 			query.setComment( "named native SQL query " + queryName );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	public Query getNamedSQLQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 		if ( nsqlqd==null ) {
 			throw new MappingException( "Named SQL query not known: " + queryName );
 		}
 		Query query = new SQLQueryImpl(
 				nsqlqd,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 		);
 		query.setComment( "named native SQL query " + queryName );
 		initQuery( query, nsqlqd );
 		return query;
 	}
 
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		if ( nqd.getTimeout()!=null ) query.setTimeout( nqd.getTimeout().intValue() );
 		if ( nqd.getFetchSize()!=null ) query.setFetchSize( nqd.getFetchSize().intValue() );
 		if ( nqd.getCacheMode() != null ) query.setCacheMode( nqd.getCacheMode() );
 		query.setReadOnly( nqd.isReadOnly() );
 		if ( nqd.getComment() != null ) query.setComment( nqd.getComment() );
 	}
 
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		QueryImpl query = new QueryImpl(
 				queryString,
 		        this,
 		        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
+	@Override
+	public String getTenantIdentifier() {
+		return tenantIdentifier;
+	}
+
+	@Override
+	public void setTenantIdentifier(String identifier) {
+		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
+			throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
+		}
+		this.tenantIdentifier = identifier;
+	}
+
+	private transient JdbcConnectionAccess jdbcConnectionAccess;
+
+	@Override
+	public JdbcConnectionAccess getJdbcConnectionAccess() {
+		if ( jdbcConnectionAccess == null ) {
+			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
+				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
+						factory.getServiceRegistry().getService( ConnectionProvider.class )
+				);
+			}
+			else {
+				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
+						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
+				);
+			}
+		}
+		return jdbcConnectionAccess;
+	}
+
+	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
+		private final ConnectionProvider connectionProvider;
+
+		private NonContextualJdbcConnectionAccess(ConnectionProvider connectionProvider) {
+			this.connectionProvider = connectionProvider;
+		}
+
+		@Override
+		public Connection obtainConnection() throws SQLException {
+			return connectionProvider.getConnection();
+		}
+
+		@Override
+		public void releaseConnection(Connection connection) throws SQLException {
+			connectionProvider.closeConnection( connection );
+		}
+	}
+
+	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
+		private final MultiTenantConnectionProvider connectionProvider;
+
+		private ContextualJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
+			this.connectionProvider = connectionProvider;
+		}
+
+		@Override
+		public Connection obtainConnection() throws SQLException {
+			if ( tenantIdentifier == null ) {
+				throw new HibernateException( "Tenant identifier required!" );
+			}
+			return connectionProvider.getConnection( tenantIdentifier );
+		}
+
+		@Override
+		public void releaseConnection(Connection connection) throws SQLException {
+			if ( tenantIdentifier == null ) {
+				throw new HibernateException( "Tenant identifier required!" );
+			}
+			connectionProvider.releaseConnection( tenantIdentifier, connection );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 478b376a55..5a360bd3af 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1324 +1,1327 @@
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
 package org.hibernate.impl;
 
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
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
+import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.CurrentSessionContext;
 import org.hibernate.context.JTASessionContext;
 import org.hibernate.context.ManagedSessionContext;
 import org.hibernate.context.ThreadLocalSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.event.EventListeners;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
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
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
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
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactory, SessionFactoryImplementor {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionFactoryImpl.class.getName());
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
 	private final transient ServiceRegistryImplementor serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient Statistics statistics;
 	private final transient EventListeners eventListeners;
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
 	        EventListeners listeners,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.buildingSessionFactory();
 
 		this.statistics = buildStatistics( settings, serviceRegistry );
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 		this.interceptor = cfg.getInterceptor();
+
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
 		this.settings = settings;
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
         this.eventListeners = listeners;
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
 				final AccessType accessType = AccessType.parse( model.getCacheConcurrencyStrategy() );
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
 			final AccessType accessType = AccessType.parse( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
                 LOG.trace("Building cache for collection data [" + model.getRole() + "]");
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
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
 		SessionFactoryObjectFactory.addInstance(uuid, name, this, properties);
 
         LOG.debugf("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( getJdbcServices(), cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( getJdbcServices(), cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( getJdbcServices(), cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( getJdbcServices(), cfg );
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
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	private Statistics buildStatistics(Settings settings, ServiceRegistry serviceRegistry) {
 		Statistics statistics = new ConcurrentStatisticsImpl( this );
 		statistics.setStatisticsEnabled( settings.isStatisticsEnabled() );
 		LOG.debugf("Statistics initialized [enabled=%s]", settings.isStatisticsEnabled());
 		return statistics;
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
 
 	public StatelessSession openStatelessSession() {
 		return new StatelessSessionImpl( null, this );
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return new StatelessSessionImpl( connection, this );
 	}
 
 	private SessionImpl openSession(
 		Connection connection,
 	    boolean autoClose,
 	    long timestamp,
 	    Interceptor sessionLocalInterceptor
 	) {
 		return new SessionImpl(
 		        connection,
 		        this,
 		        autoClose,
 		        timestamp,
 		        sessionLocalInterceptor == null ? interceptor : sessionLocalInterceptor,
 		        settings.getDefaultEntityMode(),
 		        settings.isFlushBeforeCompletionEnabled(),
 		        settings.isAutoCloseSessionEnabled(),
 		        settings.getConnectionReleaseMode()
 			);
 	}
 
 	public Session openSession(Connection connection, Interceptor sessionLocalInterceptor) {
 		return openSession(connection, false, Long.MIN_VALUE, sessionLocalInterceptor);
 	}
 
 	public Session openSession(Interceptor sessionLocalInterceptor) throws HibernateException {
 		// note that this timestamp is not correct if the connection provider
 		// returns an older JDBC connection that was associated with a
 		// transaction that was already begun before openSession() was called
 		// (don't know any possible solution to this!)
 		long timestamp = settings.getRegionFactory().nextTimestamp();
 		return openSession( null, true, timestamp, sessionLocalInterceptor );
 	}
 
 	public Session openSession(Connection connection) {
 		return openSession(connection, interceptor); //prevents this session from adding things to cache
 	}
 
 	public Session openSession() throws HibernateException {
 		return openSession(interceptor);
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return new SessionImpl(
 				null,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        false,
 		        false,
 		        ConnectionReleaseMode.AFTER_STATEMENT
 			);
 	}
 
 	public Session openSession(
 			final Connection connection,
 	        final boolean flushBeforeCompletionEnabled,
 	        final boolean autoCloseSessionEnabled,
 	        final ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
 		return new SessionImpl(
 				connection,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        flushBeforeCompletionEnabled,
 		        autoCloseSessionEnabled,
 		        connectionReleaseMode
 			);
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
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
 
 	// from javax.naming.Referenceable
 	public Reference getReference() throws NamingException {
         LOG.debugf("Returning a Reference to the SessionFactory");
 		return new Reference(
 			SessionFactoryImpl.class.getName(),
 		    new StringRefAddr("uuid", uuid),
 		    SessionFactoryObjectFactory.class.getName(),
 		    null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
         LOG.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryObjectFactory.getInstance(uuid);
 		if (result==null) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryObjectFactory.getNamedInstance(name);
             if (result == null) throw new InvalidObjectException("Could not find a SessionFactory named: " + name);
             LOG.debugf("Resolved SessionFactory by name");
         } else LOG.debugf("Resolved SessionFactory by UID");
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
         LOG.trace("Deserializing");
 		in.defaultReadObject();
         LOG.debugf("Deserialized: %s", uuid);
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
 		return getEntityPersister(className).getPropertyType(propertyName);
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
 
 		SessionFactoryObjectFactory.removeInstance(uuid, name, properties);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
 		eventListeners.destroyListeners();
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
 					EntityMode.POJO,
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
 					EntityMode.POJO,
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
 			currentQueryCache = settings.getQueryCacheFactory().getQueryCache( regionName, updateTimestampsCache, settings, properties );
 			queryCaches.put( regionName, currentQueryCache );
 			allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 		}
 
 		return currentQueryCache;
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	public Map getAllSecondLevelCacheRegions() {
 		return new HashMap( allCacheRegions );
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return statistics;
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return (StatisticsImplementor) statistics;
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = ( FilterDefinition ) filters.get( filterName );
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
 		return (IdentifierGenerator) identifierGenerators.get(rootEntityName);
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
                 LOG.unableToConstructCurrentSessionContext(impl, t);
 				return null;
 			}
 		}
 	}
 
 	public EventListeners getEventListeners() {
 		return eventListeners;
 	}
 
 	@Override
-	public ServiceRegistry getServiceRegistry() {
+	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
+	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return ( FetchProfile ) fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
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
 		String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		String name = null;
 		if ( isNamed ) {
 			name = ois.readUTF();
 		}
 		Object result = SessionFactoryObjectFactory.getInstance( uuid );
 		if ( result == null ) {
             LOG.trace("Could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name");
 			if ( isNamed ) {
 				result = SessionFactoryObjectFactory.getNamedInstance( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index 4f34548b2c..7bf9741bad 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,183 +1,184 @@
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
 package org.hibernate.persister.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 /**
  * The standard Hibernate {@link PersisterFactory} implementation
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class PersisterFactoryImpl implements PersisterFactory, ServiceRegistryAwareService {
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			PersistentClass.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			Collection.class,
 			CollectionRegionAccessStrategy.class,
 			Configuration.class,
 			SessionFactoryImplementor.class
 	};
 
-	private ServiceRegistry serviceRegistry;
+	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
-	public void injectServices(ServiceRegistry serviceRegistry) {
+	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(
 			PersistentClass metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	private static EntityPersister create(
 			Class<? extends EntityPersister> persisterClass,
 			PersistentClass metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException {
 		try {
 			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( ENTITY_PERSISTER_CONSTRUCTOR_ARGS );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, factory, cfg );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection metadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = metadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( metadata );
 		}
 
 		return create( persisterClass, cfg, metadata, cacheAccessStrategy, factory );
 	}
 
 	private static CollectionPersister create(
 			Class<? extends CollectionPersister> persisterClass,
 			Configuration cfg,
 			Collection metadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		try {
 			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( COLLECTION_PERSISTER_CONSTRUCTOR_ARGS );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, cfg, factory );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 247e5199ea..44b2f91f3b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,73 +1,75 @@
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
 
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
+import org.hibernate.service.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 import java.util.ArrayList;
 import java.util.List;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardServiceInitiators {
 	public static List<BasicServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<BasicServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<BasicServiceInitiator> serviceInitiators = new ArrayList<BasicServiceInitiator>();
 
 		serviceInitiators.add( ClassLoaderServiceInitiator.INSTANCE );
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( PersisterClassResolverInitiator.INSTANCE );
 		serviceInitiators.add( PersisterFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
+		serviceInitiators.add( MultiTenantConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( SessionFactoryServiceRegistryFactoryInitiator.INSTANCE );
 
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
index bb02d014e0..f3b7b03f2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
@@ -1,136 +1,136 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
 
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateLogger;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.Service;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.UnknownServiceException;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 
 /**
  * Standard Hibernate implementation of the service registry.
  *
  * @author Steve Ebersole
  */
 public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl implements BasicServiceRegistry {
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicServiceRegistryImpl.class.getName());
 
 	private final Map<Class,BasicServiceInitiator> serviceInitiatorMap;
 	private final Map configurationValues;
 
 	public BasicServiceRegistryImpl(Map configurationValues) {
 		this( StandardServiceInitiators.LIST, configurationValues );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public BasicServiceRegistryImpl(List<BasicServiceInitiator> serviceInitiators, Map configurationValues) {
 		super();
 		this.serviceInitiatorMap = toMap( serviceInitiators );
 		this.configurationValues = configurationValues;
 		for ( BasicServiceInitiator initiator : serviceInitiatorMap.values() ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator.getServiceInitiated() );
 		}
 	}
 
 	/**
 	 * We convert the incoming list of initiators to a map for 2 reasons:<ul>
 	 * <li>to make it easier to look up the initiator we need for a given service role</li>
 	 * <li>to make sure there is only one initiator for a given service role (last wins)</li>
 	 * </ul>
 	 *
 	 * @param serviceInitiators The list of individual initiators
 	 *
 	 * @return The map of initiators keyed by the service rle they initiate.
 	 */
 	private static Map<Class, BasicServiceInitiator> toMap(List<BasicServiceInitiator> serviceInitiators) {
 		final Map<Class, BasicServiceInitiator> result = new HashMap<Class, BasicServiceInitiator>();
 		for ( BasicServiceInitiator initiator : serviceInitiators ) {
 			result.put( initiator.getServiceInitiated(), initiator );
 		}
 		return result;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void registerServiceInitiator(BasicServiceInitiator initiator) {
 		ServiceBinding serviceBinding = locateServiceBinding( initiator.getServiceInitiated(), false );
 		if ( serviceBinding != null ) {
 			serviceBinding.setTarget( null );
 		}
 		else {
 			createServiceBinding( initiator.getServiceInitiated() );
 		}
 		final Object previous = serviceInitiatorMap.put( initiator.getServiceInitiated(), initiator );
 		if ( previous != null ) {
 			LOG.debugf( "Over-wrote existing service initiator [role=%s]", initiator.getServiceInitiated().getName() );
 		}
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	protected <T extends Service> T createService(Class<T> serviceRole) {
 		BasicServiceInitiator<T> initiator = serviceInitiatorMap.get( serviceRole );
 		if ( initiator == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 		try {
 			T service = initiator.initiateService( configurationValues, this );
 			// IMPL NOTE : the register call here is important to avoid potential stack overflow issues
 			//		from recursive calls through #configureService
 			registerService( serviceRole, service );
 			return service;
 		}
 		catch ( ServiceException e ) {
 			throw e;
 		}
 		catch ( Exception e ) {
 			throw new ServiceException( "Unable to create requested service [" + serviceRole.getName() + "]", e );
 		}
 	}
 
 	@Override
 	protected <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
-		if ( Configurable.class.isInstance( service ) ) {
-			( (Configurable) service ).configure( configurationValues );
-		}
-
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
 			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
+
+		if ( Configurable.class.isInstance( service ) ) {
+			( (Configurable) service ).configure( configurationValues );
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
index a7cb84e174..dc4b21010b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
@@ -1,280 +1,285 @@
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
 import org.hibernate.HibernateLogger;
+import org.hibernate.MultiTenancyStrategy;
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        ConnectionProviderInitiator.class.getName());
 	public static final String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 	public static final String C3P0_PROVIDER_CLASS_NAME =
 			"org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider";
 
 	public static final String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
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
+		if ( MultiTenancyStrategy.determineMultiTenancyStrategy( configurationValues ) != MultiTenancyStrategy.NONE ) {
+			// nothing to do, but given the separate hierarchies have to handle this here.
+		}
+
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
             LOG.instantiatingExplicitConnectinProvider(providerClassName);
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
 					&& ( (String) key ).startsWith( C3P0_CONFIG_PREFIX ) ) {
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
 					&& ( (String) key ).startsWith( PROXOOL_CONFIG_PREFIX ) ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/MultiTenantConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/MultiTenantConnectionProviderInitiator.java
new file mode 100644
index 0000000000..044565cc2d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/MultiTenantConnectionProviderInitiator.java
@@ -0,0 +1,53 @@
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
+package org.hibernate.service.jdbc.connections.internal;
+
+import java.util.Map;
+
+import org.hibernate.MultiTenancyStrategy;
+import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
+import org.hibernate.service.spi.BasicServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class MultiTenantConnectionProviderInitiator implements BasicServiceInitiator<MultiTenantConnectionProvider> {
+	public static final MultiTenantConnectionProviderInitiator INSTANCE = new MultiTenantConnectionProviderInitiator();
+
+	@Override
+	public Class<MultiTenantConnectionProvider> getServiceInitiated() {
+		return MultiTenantConnectionProvider.class;
+	}
+
+	@Override
+	public MultiTenantConnectionProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		if ( MultiTenancyStrategy.determineMultiTenancyStrategy(  configurationValues ) == MultiTenancyStrategy.NONE ) {
+			// nothing to do, but given the separate hierarchies have to handle this here.
+		}
+
+		// for now...
+		return null;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/AbstractMultiTenantConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/AbstractMultiTenantConnectionProvider.java
new file mode 100644
index 0000000000..b2f8e614b9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/AbstractMultiTenantConnectionProvider.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.service.jdbc.connections.spi;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+
+import org.hibernate.service.UnknownUnwrapTypeException;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractMultiTenantConnectionProvider implements MultiTenantConnectionProvider {
+	protected abstract ConnectionProvider getAnyConnectionProvider();
+	protected abstract ConnectionProvider selectConnectionProvider(String tenantIdentifier);
+
+	@Override
+	public Connection getAnyConnection() throws SQLException {
+		return getAnyConnectionProvider().getConnection();
+	}
+
+	@Override
+	public void releaseAnyConnection(Connection connection) throws SQLException {
+		getAnyConnectionProvider().closeConnection( connection );
+	}
+
+	@Override
+	public Connection getConnection(String tenantIdentifier) throws SQLException {
+		return selectConnectionProvider( tenantIdentifier ).getConnection();
+	}
+
+	@Override
+	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
+		selectConnectionProvider( tenantIdentifier ).getConnection();
+	}
+
+	@Override
+	public boolean supportsAggressiveRelease() {
+		return getAnyConnectionProvider().supportsAggressiveRelease();
+	}
+
+	@Override
+	public boolean isUnwrappableAs(Class unwrapType) {
+		return ConnectionProvider.class.equals( unwrapType ) ||
+				MultiTenantConnectionProvider.class.equals( unwrapType ) ||
+				AbstractMultiTenantConnectionProvider.class.isAssignableFrom( unwrapType );
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public <T> T unwrap(Class<T> unwrapType) {
+		if ( isUnwrappableAs( unwrapType ) ) {
+			return (T) this;
+		}
+		else {
+			throw new UnknownUnwrapTypeException( unwrapType );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/MultiTenantConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/MultiTenantConnectionProvider.java
new file mode 100644
index 0000000000..29ce194997
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/MultiTenantConnectionProvider.java
@@ -0,0 +1,95 @@
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
+package org.hibernate.service.jdbc.connections.spi;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+
+import org.hibernate.service.Service;
+import org.hibernate.service.spi.Wrapped;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface MultiTenantConnectionProvider extends Service, Wrapped {
+	/**
+	 * Allows access to the database metadata of the underlying database(s) in situations where we do not have a
+	 * tenant id (like startup processing, for example).
+	 *
+	 * @return The database metadata.
+	 *
+	 * @throws SQLException Indicates a problem opening a connection
+	 */
+	public Connection getAnyConnection() throws SQLException;
+
+	/**
+	 * Release a connection obtained from {@link #getAnyConnection}
+	 *
+	 * @param connection The JDBC connection to release
+	 *
+	 * @throws SQLException Indicates a problem closing the connection
+	 */
+	public void releaseAnyConnection(Connection connection) throws SQLException;
+
+	/**
+	 * Obtains a connection for Hibernate use according to the underlying strategy of this provider.
+	 *
+	 * @param tenantIdentifier The identifier of the tenant for which to get a connection
+	 *
+	 * @return The obtained JDBC connection
+	 *
+	 * @throws SQLException Indicates a problem opening a connection
+	 * @throws org.hibernate.HibernateException Indicates a problem otherwise obtaining a connection.
+	 */
+	public Connection getConnection(String tenantIdentifier) throws SQLException;
+
+	/**
+	 * Release a connection from Hibernate use.
+	 *
+	 * @param connection The JDBC connection to release
+	 * @param tenantIdentifier The identifier of the tenant.
+	 *
+	 * @throws SQLException Indicates a problem closing the connection
+	 * @throws org.hibernate.HibernateException Indicates a problem otherwise releasing a connection.
+	 */
+	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException;
+
+	/**
+	 * Does this connection provider support aggressive release of JDBC
+	 * connections and re-acquisition of those connections (if need be) later?
+	 * <p/>
+	 * This is used in conjunction with {@link org.hibernate.cfg.Environment#RELEASE_CONNECTIONS}
+	 * to aggressively release JDBC connections.  However, the configured ConnectionProvider
+	 * must support re-acquisition of the same underlying connection for that semantic to work.
+	 * <p/>
+	 * Typically, this is only true in managed environments where a container
+	 * tracks connections by transaction or thread.
+	 *
+	 * Note that JTA semantic depends on the fact that the underlying connection provider does
+	 * support aggressive release.
+	 *
+	 * @return {@code true} if aggressive releasing is supported; {@code false} otherwise.
+	 */
+	public boolean supportsAggressiveRelease();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
index b3200900ca..97e6e34d0a 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
@@ -1,133 +1,134 @@
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
 
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 
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
-	private ServiceRegistry serviceRegistry;
+	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
-	public void injectServices(ServiceRegistry serviceRegistry) {
+	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
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
 		cacheTransactionManager = ConfigurationHelper.getBoolean( CACHE_TM, configValues, true );
 		cacheUserTransaction = ConfigurationHelper.getBoolean( CACHE_UT, configValues, false );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
index 62941d4a61..633ee4d862 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
@@ -1,40 +1,38 @@
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
 package org.hibernate.service.spi;
 
-import org.hibernate.service.ServiceRegistry;
-
 /**
  * Allows services to be injected with the {@link org.hibernate.service.ServiceRegistry} during configuration phase.
  *
  * @author Steve Ebersole
  */
 public interface ServiceRegistryAwareService {
 	/**
 	 * Callback to inject the registry.
 	 *
 	 * @param serviceRegistry The registry
 	 */
-	public void injectServices(ServiceRegistry serviceRegistry);
+	public void injectServices(ServiceRegistryImplementor serviceRegistry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java
index 141aefffb2..d1b1c6d5e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/ConnectionHelper.java
@@ -1,59 +1,59 @@
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
 
 /**
  * Contract for delegates responsible for managing connection used by the
  * hbm2ddl tools.
  *
  * @author Steve Ebersole
  */
-interface ConnectionHelper {
+public interface ConnectionHelper {
 	/**
 	 * Prepare the helper for use.
 	 *
 	 * @param needsAutoCommit Should connection be forced to auto-commit
 	 * if not already.
 	 * @throws SQLException
 	 */
 	public void prepare(boolean needsAutoCommit) throws SQLException;
 
 	/**
 	 * Get a reference to the connection we are using.
 	 *
 	 * @return The JDBC connection.
 	 * @throws SQLException
 	 */
 	public Connection getConnection() throws SQLException;
 
 	/**
 	 * Release any resources held by this helper.
 	 *
 	 * @throws SQLException
 	 */
 	public void release() throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index eba32ea470..438ce25ea1 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,547 +1,558 @@
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
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileWriter;
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
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.jboss.logging.Logger;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  */
 public class SchemaExport {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SchemaExport.class.getName());
 
 	private ConnectionHelper connectionHelper;
 	private String[] dropSQL;
 	private String[] createSQL;
 	private String outputFile = null;
 	private String importFiles;
 	private Dialect dialect;
 	private String delimiter;
 	private final List exceptions = new ArrayList();
 	private boolean haltOnError = false;
 	private Formatter formatter;
 	private SqlStatementLogger sqlStatementLogger;
 	private static final String DEFAULT_IMPORT_FILE = "/import.sql";
 
 	/**
 	 * Create a schema exporter for the given Configuration
 	 *
 	 * @param cfg The configuration from which to build a schema export.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration and given settings
 	 *
 	 * @param cfg The configuration from which to build a schema export.
 	 * @param jdbcServices The jdbc services
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(JdbcServices jdbcServices, Configuration cfg) throws HibernateException {
 		dialect = jdbcServices.getDialect();
 		connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 		dropSQL = cfg.generateDropSchemaScript( dialect );
 		createSQL = cfg.generateSchemaCreationScript( dialect );
 		sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES, cfg.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, with the given
 	 * database connection properties.
 	 *
 	 * @param cfg The configuration from which to build a schema export.
 	 * @param properties The properties from which to configure connectivity etc.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 *
 	 * @deprecated properties may be specified via the Configuration object
 	 */
 	@Deprecated
     public SchemaExport(Configuration cfg, Properties properties) throws HibernateException {
 		dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 		dropSQL = cfg.generateDropSchemaScript( dialect );
 		createSQL = cfg.generateSchemaCreationScript( dialect );
 
 		formatter = ( ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, props ) ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 
 		importFiles = ConfigurationHelper.getString( Environment.HBM2DDL_IMPORT_FILES, props, DEFAULT_IMPORT_FILE );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
 	 *
 	 * @param cfg The configuration to use.
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration cfg, Connection connection) throws HibernateException {
 		this.connectionHelper = new SuppliedConnectionHelper( connection );
 		dialect = Dialect.getDialect( cfg.getProperties() );
 		dropSQL = cfg.generateDropSchemaScript( dialect );
 		createSQL = cfg.generateSchemaCreationScript( dialect );
 		formatter = ( ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, cfg.getProperties() ) ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		importFiles = ConfigurationHelper.getString( Environment.HBM2DDL_IMPORT_FILES, cfg.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 	}
 
+	public SchemaExport(
+			ConnectionHelper connectionHelper,
+			String[] dropSql,
+			String[] createSql) {
+		this.connectionHelper = connectionHelper;
+		this.dropSQL = dropSql;
+		this.createSQL = createSql;
+		this.importFiles = "";
+		this.formatter = FormatStyle.DDL.getFormatter();
+	}
+
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
 	 * An import file, containing raw SQL statements to be executed.
 	 *
 	 * @param filename The import file name.
 	 * @return this
 	 * @deprecated use {@link org.hibernate.cfg.Environment#HBM2DDL_IMPORT_FILES}
 	 */
 	@Deprecated
     public SchemaExport setImportFile(String filename) {
 		importFiles = filename;
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
 	 * Run the schema creation script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void create(boolean script, boolean export) {
 		execute( script, export, false, false );
 	}
 
 	/**
 	 * Run the drop schema script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void drop(boolean script, boolean export) {
 		execute( script, export, true, false );
 	}
 
 	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
 
         LOG.runningHbm2ddlSchemaExport();
 
 		Connection connection = null;
 		Writer outputFileWriter = null;
 		List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
 		Statement statement = null;
 
 		exceptions.clear();
 
 		try {
 
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
 
 			if ( outputFile != null ) {
                 LOG.writingGeneratedSchemaToFile(outputFile);
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			if ( export ) {
                 LOG.exportingGeneratedSchemaToDatabase();
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				statement = connection.createStatement();
 			}
 
 			if ( !justCreate ) {
 				drop( script, export, outputFileWriter, statement );
 			}
 
 			if ( !justDrop ) {
 				create( script, export, outputFileWriter, statement );
 				if ( export && importFileReaders.size() > 0 ) {
 					for (NamedReader reader : importFileReaders) {
 						importScript( reader, statement );
 					}
 				}
 			}
 
             LOG.schemaExportComplete();
 
 		}
 
 		catch ( Exception e ) {
 			exceptions.add( e );
             LOG.schemaExportUnsuccessful(e);
 		}
 
 		finally {
 
 			try {
 				if ( statement != null ) {
 					statement.close();
 				}
 				if ( connection != null ) {
 					connectionHelper.release();
 				}
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
                 LOG.unableToCloseConnection(e);
 			}
 
 			try {
 				if ( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch ( IOException ioe ) {
 				exceptions.add( ioe );
                 LOG.unableToCloseOutputFile(outputFile, ioe);
 			}
             for (NamedReader reader : importFileReaders) {
                 try {
                     reader.getReader().close();
                 } catch (IOException ioe) {
                     exceptions.add(ioe);
                     LOG.unableToCloseInputFiles(reader.getName(), ioe);
 				}
             }
 		}
 	}
 
 	private class NamedReader {
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
 
 	private void importScript(NamedReader importFileReader, Statement statement)
 			throws IOException {
         LOG.executingImportScript(importFileReader.getName());
 		BufferedReader reader = new BufferedReader( importFileReader.getReader() );
 		long lineNo = 0;
 		for ( String sql = reader.readLine(); sql != null; sql = reader.readLine() ) {
 			try {
 				lineNo++;
 				String trimmedSql = sql.trim();
 				if ( trimmedSql.length() == 0 ||
 				     trimmedSql.startsWith( "--" ) ||
 				     trimmedSql.startsWith( "//" ) ||
 				     trimmedSql.startsWith( "/*" ) ) {
 					continue;
 				}
                 if (trimmedSql.endsWith(";")) trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
                 LOG.debugf(trimmedSql);
                 statement.execute(trimmedSql);
 			}
 			catch ( SQLException e ) {
 				throw new JDBCException( "Error during import script execution at line " + lineNo, e );
 			}
 		}
 	}
 
 	private void create(boolean script, boolean export, Writer fileOutput, Statement statement)
 			throws IOException {
 		for ( int j = 0; j < createSQL.length; j++ ) {
 			try {
 				execute( script, export, fileOutput, statement, createSQL[j] );
 			}
 			catch ( SQLException e ) {
 				if ( haltOnError ) {
 					throw new JDBCException( "Error during DDL export", e );
 				}
 				exceptions.add( e );
                 LOG.unsuccessfulCreate(createSQL[j]);
                 LOG.error(e.getMessage());
 			}
 		}
 	}
 
 	private void drop(boolean script, boolean export, Writer fileOutput, Statement statement)
 			throws IOException {
 		for ( int i = 0; i < dropSQL.length; i++ ) {
 			try {
 				execute( script, export, fileOutput, statement, dropSQL[i] );
 			}
 			catch ( SQLException e ) {
 				exceptions.add( e );
                 LOG.debugf("Unsuccessful: %s", dropSQL[i]);
                 LOG.debugf(e.getMessage());
 			}
 		}
 	}
 
 	private void execute(boolean script, boolean export, Writer fileOutput, Statement statement, final String sql)
 			throws IOException, SQLException {
 		final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
 
 		String formatted = formatter.format( sql );
         if (delimiter != null) formatted += delimiter;
         if (script) System.out.println(formatted);
         LOG.debugf(formatted);
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
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		return new BasicServiceRegistryImpl( properties );
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
 				cfg.setProperty( Environment.HBM2DDL_IMPORT_FILES, importFile );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				SchemaExport se = new SchemaExport( serviceRegistry.getService( JdbcServices.class ), cfg )
 						.setHaltOnError( halt )
 						.setOutputFile( outFile )
 						.setDelimiter( delim );
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java
new file mode 100644
index 0000000000..d9042a7631
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/JdbcConnectionAccessImpl.java
@@ -0,0 +1,61 @@
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
+package org.hibernate.test.common;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
+import org.hibernate.engine.transaction.spi.TransactionEnvironment;
+import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JdbcConnectionAccessImpl implements JdbcConnectionAccess {
+	private final ConnectionProvider connectionProvider;
+
+	public JdbcConnectionAccessImpl(TransactionEnvironment transactionEnvironment) {
+		this( transactionEnvironment.getSessionFactory().getServiceRegistry() );
+	}
+
+	public JdbcConnectionAccessImpl(ConnectionProvider connectionProvider) {
+		this.connectionProvider = connectionProvider;
+	}
+
+	public JdbcConnectionAccessImpl(ServiceRegistry serviceRegistry) {
+		this( serviceRegistry.getService( ConnectionProvider.class ) );
+	}
+
+	@Override
+	public Connection obtainConnection() throws SQLException {
+		return connectionProvider.getConnection();
+	}
+
+	@Override
+	public void releaseConnection(Connection connection) throws SQLException {
+		connectionProvider.closeConnection( connection );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionContextImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionContextImpl.java
index 549294c789..16d1a95218 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionContextImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionContextImpl.java
@@ -1,100 +1,117 @@
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
 package org.hibernate.test.common;
 
 import org.hibernate.ConnectionReleaseMode;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
+import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public class TransactionContextImpl implements TransactionContext {
 	private final TransactionEnvironment transactionEnvironment;
+	private final JdbcConnectionAccess jdbcConnectionAccess;
 
-	public TransactionContextImpl(TransactionEnvironment transactionEnvironment) {
+	public TransactionContextImpl(TransactionEnvironment transactionEnvironment, JdbcConnectionAccess jdbcConnectionAccess) {
 		this.transactionEnvironment = transactionEnvironment;
+		this.jdbcConnectionAccess = jdbcConnectionAccess;
+	}
+
+	public TransactionContextImpl(TransactionEnvironment transactionEnvironment, ServiceRegistry serviceRegistry) {
+		this( transactionEnvironment, new JdbcConnectionAccessImpl( serviceRegistry ) );
+	}
+
+	public TransactionContextImpl(TransactionEnvironment transactionEnvironment) {
+		this( transactionEnvironment, new JdbcConnectionAccessImpl( transactionEnvironment.getJdbcServices().getConnectionProvider() ) );
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return transactionEnvironment;
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return transactionEnvironment.getTransactionFactory().getDefaultReleaseMode();
 	}
 
 	@Override
+	public JdbcConnectionAccess getJdbcConnectionAccess() {
+		return jdbcConnectionAccess;
+	}
+
+	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return false;
 	}
 
 	@Override
 	public boolean isClosed() {
 		return false;
 	}
 
 	@Override
 	public boolean isFlushModeNever() {
 		return false;
 	}
 
 	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return true;
 	}
 
 	@Override
 	public void managedFlush() {
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		return false;
 	}
 
 	@Override
 	public void managedClose() {
 	}
 
 	@Override
 	public void afterTransactionBegin(TransactionImplementor hibernateTransaction) {
 	}
 
 	@Override
 	public void beforeTransactionCompletion(TransactionImplementor hibernateTransaction) {
 	}
 
 	@Override
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful) {
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
index 91b0b2bad9..0546485242 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/AggressiveReleaseTest.java
@@ -1,246 +1,262 @@
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
 package org.hibernate.test.jdbc.proxies;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.BasicTestingJdbcServiceImpl;
+import org.hibernate.test.common.JdbcConnectionAccessImpl;
 import org.hibernate.test.common.JournalingConnectionObserver;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Steve Ebersole
  */
 public class AggressiveReleaseTest extends BaseUnitTestCase {
 	private BasicTestingJdbcServiceImpl services = new BasicTestingJdbcServiceImpl();
 
 	@Before
 	public void setUp() throws SQLException {
 		services.prepare( true );
 
 		Connection connection = null;
 		Statement stmnt = null;
 		try {
 			connection = services.getConnectionProvider().getConnection();
 			stmnt = connection.createStatement();
 			stmnt.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			stmnt.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		}
 		finally {
 			if ( stmnt != null ) {
 				try {
 					stmnt.close();
 				}
 				catch ( SQLException ignore ) {
 				}
 			}
 			if ( connection != null ) {
 				try {
 					connection.close();
 				}
 				catch ( SQLException ignore ) {
 				}
 			}
 		}
 	}
 
 	@After
 	public void tearDown() throws SQLException {
 		Connection connection = null;
 		Statement stmnt = null;
 		try {
 			connection = services.getConnectionProvider().getConnection();
 			stmnt = connection.createStatement();
 			stmnt.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		}
 		finally {
 			if ( stmnt != null ) {
 				try {
 					stmnt.close();
 				}
 				catch ( SQLException ignore ) {
 				}
 			}
 			if ( connection != null ) {
 				try {
 					connection.close();
 				}
 				catch ( SQLException ignore ) {
 				}
 			}
 		}
 
 		services.release();
 	}
 
 	@Test
 	public void testBasicRelease() {
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_STATEMENT,
+				services ,
+				new JdbcConnectionAccessImpl( services.getConnectionProvider() )
+		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		JournalingConnectionObserver observer = new JournalingConnectionObserver();
 		logicalConnection.addObserver( observer );
 
 		try {
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 0, observer.getPhysicalConnectionReleasedCount() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 	}
 
 	@Test
 	public void testReleaseCircumventedByHeldResources() {
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_STATEMENT,
+				services,
+				new JdbcConnectionAccessImpl( services.getConnectionProvider() )
+		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		JournalingConnectionObserver observer = new JournalingConnectionObserver();
 		logicalConnection.addObserver( observer );
 
 		try {
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 0, observer.getPhysicalConnectionReleasedCount() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 
 			// open a result set and hold it open...
 			ps = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 
 			// open a second result set
 			PreparedStatement ps2 = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps2.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 			// and close it...
 			ps2.close();
 			// the release should be circumvented...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 
 			// let the close of the logical connection below release all resources (hopefully)...
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 		assertEquals( 2, observer.getPhysicalConnectionReleasedCount() );
 	}
 
 	@Test
 	public void testReleaseCircumventedManually() {
-		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl( null, ConnectionReleaseMode.AFTER_STATEMENT, services );
+		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
+				null,
+				ConnectionReleaseMode.AFTER_STATEMENT,
+				services,
+				new JdbcConnectionAccessImpl( services.getConnectionProvider() ) 
+		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		JournalingConnectionObserver observer = new JournalingConnectionObserver();
 		logicalConnection.addObserver( observer );
 
 		try {
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 0, observer.getPhysicalConnectionReleasedCount() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 1, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 
 			// disable releases...
 			logicalConnection.disableReleases();
 
 			// open a result set...
 			ps = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 			// and close it...
 			ps.close();
 			// the release should be circumvented...
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 			assertEquals( 1, observer.getPhysicalConnectionReleasedCount() );
 
 			// let the close of the logical connection below release all resources (hopefully)...
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertEquals( 2, observer.getPhysicalConnectionObtainedCount() );
 		assertEquals( 2, observer.getPhysicalConnectionReleasedCount() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
index b41e42c62b..b7edfc9fe5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BasicConnectionProxyTest.java
@@ -1,150 +1,154 @@
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
 package org.hibernate.test.jdbc.proxies;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.jdbc.internal.LogicalConnectionImpl;
 import org.hibernate.engine.jdbc.internal.proxy.ProxyBuilder;
 import org.hibernate.test.common.BasicTestingJdbcServiceImpl;
+import org.hibernate.test.common.JdbcConnectionAccessImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicConnectionProxyTest extends BaseUnitTestCase {
 	private BasicTestingJdbcServiceImpl services = new BasicTestingJdbcServiceImpl();
 
 	@Before
 	public void setUp() {
 		services.prepare( false );
 	}
 
 	@After
 	public void tearDown() {
 		services.release();
 	}
 
 	@Test
 	public void testDatabaseMetaDataHandling() throws Throwable {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_TRANSACTION,
-				services
+				services,
+				new JdbcConnectionAccessImpl( services.getConnectionProvider() )
 		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		try {
 			DatabaseMetaData metaData = proxiedConnection.getMetaData();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ResultSet rs1 = metaData.getCatalogs();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			rs1.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			metaData.getCatalogs();
 			metaData.getSchemas();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		}
 		catch ( SQLException e ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		}
 	}
 
 	@Test
 	public void testExceptionHandling() {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_TRANSACTION,
-				services
+				services,
+				new JdbcConnectionAccessImpl( services.getConnectionProvider() )
 		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 		try {
 			proxiedConnection.prepareStatement( "select count(*) from NON_EXISTENT" ).executeQuery();
 		}
 		catch ( SQLException e ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		catch ( JDBCException ok ) {
 			// expected outcome
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 	@Test
 	public void testBasicJdbcUsage() throws JDBCException {
 		LogicalConnectionImpl logicalConnection = new LogicalConnectionImpl(
 				null,
 				ConnectionReleaseMode.AFTER_TRANSACTION,
-				services
+				services,
+				new JdbcConnectionAccessImpl( services.getConnectionProvider() )
 		);
 		Connection proxiedConnection = ProxyBuilder.buildConnection( logicalConnection );
 
 		try {
 			Statement statement = proxiedConnection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 			PreparedStatement ps = proxiedConnection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 
 			ps = proxiedConnection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		}
 		catch ( SQLException e ) {
 			fail( "incorrect exception type : sqlexception" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
index 26cd2739d5..2dad3fb5f1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
@@ -1,220 +1,222 @@
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
 package org.hibernate.test.jdbc.proxies;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.Statement;
 
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
 import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
 import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingBatchObserver;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class BatchingTest extends BaseUnitTestCase implements BatchKey {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
 		serviceRegistry = new BasicServiceRegistryImpl(
 				StandardServiceInitiators.LIST,
 				ConnectionProviderBuilder.getConnectionProviderProperties()
 		);
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
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
-		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
+		final TransactionContext transactionContext = new TransactionContextImpl(
+				new TransactionEnvironmentImpl( serviceRegistry )
+		);
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		Statement statement = connection.createStatement();
 		statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		statement.close();
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
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
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		logicalConnection.close();
 	}
 
 	@Test
 	public void testBatchingUsage() throws Exception {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		Statement statement = connection.createStatement();
 		statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		statement.close();
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 
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
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
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
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		logicalConnection.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/Customer.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/Customer.java
new file mode 100644
index 0000000000..cc1e06425e
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/Customer.java
@@ -0,0 +1,62 @@
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
+package org.hibernate.test.multitenancy.schema;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class Customer {
+	private Long id;
+	private String name;
+
+	public Customer() {
+	}
+
+	public Customer(String name) {
+		this.name = name;
+	}
+
+	@Id
+	@GeneratedValue
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
new file mode 100644
index 0000000000..05b8e38da3
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
@@ -0,0 +1,206 @@
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
+package org.hibernate.test.multitenancy.schema;
+
+import java.sql.Connection;
+import java.sql.SQLException;
+import java.util.Map;
+
+import org.hibernate.HibernateException;
+import org.hibernate.MultiTenancyStrategy;
+import org.hibernate.Session;
+import org.hibernate.SessionFactory;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
+import org.hibernate.service.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
+import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.tool.hbm2ddl.ConnectionHelper;
+import org.hibernate.tool.hbm2ddl.SchemaExport;
+
+import org.junit.After;
+import org.junit.Assert;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.testing.env.ConnectionProviderBuilder;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SchemaBasedMultiTenancyTest extends BaseUnitTestCase {
+	private DriverManagerConnectionProviderImpl acmeProvider;
+	private DriverManagerConnectionProviderImpl jbossProvider;
+
+	private ServiceRegistryImplementor serviceRegistry;
+
+	private SessionFactory sessionFactory;
+
+	@Before
+	public void setUp() {
+		acmeProvider = ConnectionProviderBuilder.buildConnectionProvider( "acme" );
+		jbossProvider = ConnectionProviderBuilder.buildConnectionProvider( "jboss" );
+		AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = new AbstractMultiTenantConnectionProvider() {
+			@Override
+			protected ConnectionProvider getAnyConnectionProvider() {
+				return acmeProvider;
+			}
+
+			@Override
+			protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
+				if ( "acme".equals( tenantIdentifier ) ) {
+					return acmeProvider;
+				}
+				else if ( "jboss".equals( tenantIdentifier ) ) {
+					return jbossProvider;
+				}
+				throw new HibernateException( "Unknown tenant identifier" );
+			}
+		};
+
+		Configuration cfg = new Configuration();
+		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE );
+		cfg.addAnnotatedClass( Customer.class );
+
+		cfg.buildMappings();
+
+		// do the acme export
+		new SchemaExport(
+				new ConnectionHelper() {
+					private Connection connection;
+					@Override
+					public void prepare(boolean needsAutoCommit) throws SQLException {
+						connection = acmeProvider.getConnection();
+					}
+
+					@Override
+					public Connection getConnection() throws SQLException {
+						return connection;
+					}
+
+					@Override
+					public void release() throws SQLException {
+						acmeProvider.closeConnection( connection );
+					}
+				},
+				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
+				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
+		).execute( 		// so stupid...
+				false, 	// do not script the export (write it to file)
+				true, 	// do run it against the database
+				false, 	// do not *just* perform the drop
+				false	// do not *just* perform the create
+		);
+
+		// do the jboss export
+		new SchemaExport(
+				new ConnectionHelper() {
+					private Connection connection;
+					@Override
+					public void prepare(boolean needsAutoCommit) throws SQLException {
+						connection = jbossProvider.getConnection();
+					}
+
+					@Override
+					public Connection getConnection() throws SQLException {
+						return connection;
+					}
+
+					@Override
+					public void release() throws SQLException {
+						jbossProvider.closeConnection( connection );
+					}
+				},
+				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
+				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
+		).execute( 		// so stupid...
+				false, 	// do not script the export (write it to file)
+				true, 	// do run it against the database
+				false, 	// do not *just* perform the drop
+				false	// do not *just* perform the create
+		);
+
+		serviceRegistry = new BasicServiceRegistryImpl( cfg.getProperties() );
+		serviceRegistry.registerService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider );
+
+		sessionFactory = cfg.buildSessionFactory( serviceRegistry );
+	}
+
+	@After
+	public void tearDown() {
+		if ( sessionFactory != null ) {
+			sessionFactory.close();
+		}
+		if ( serviceRegistry != null ) {
+			serviceRegistry.destroy();
+		}
+		if ( jbossProvider != null ) {
+			jbossProvider.stop();
+		}
+		if ( acmeProvider != null ) {
+			acmeProvider.stop();
+		}
+	}
+
+	private Session openSession() {
+		return sessionFactory.openSession();
+	}
+
+	@Test
+	public void testBasicExpectedBehavior() {
+		Session session = openSession();
+		session.setTenantIdentifier( "jboss" );
+		session.beginTransaction();
+		Customer steve = new Customer( "steve" );
+		session.save( steve );
+		session.getTransaction().commit();
+		session.close();
+
+		session = openSession();
+		try {
+			session.setTenantIdentifier( "acme" );
+			session.beginTransaction();
+			Customer check = (Customer) session.get( Customer.class, steve.getId() );
+			Assert.assertNull( "tenancy not properly isolated", check );
+		}
+		finally {
+			session.getTransaction().commit();
+			session.close();
+		}
+
+		session = openSession();
+		session.setTenantIdentifier( "jboss" );
+		session.beginTransaction();
+		session.delete( steve );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+}
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/env/ConnectionProviderBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/env/ConnectionProviderBuilder.java
index 410653273c..443a13a346 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/env/ConnectionProviderBuilder.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/env/ConnectionProviderBuilder.java
@@ -1,71 +1,82 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.testing.env;
 
 import java.util.Properties;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
-import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * Defines the JDBC connection information (currently H2) used by Hibernate for unit (not functional!) tests
  *
  * @author Steve Ebersole
  */
 public class ConnectionProviderBuilder {
 	public static final String DRIVER = "org.h2.Driver";
-	public static final String URL = "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE";
+	public static final String URL = "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1;MVCC=TRUE";
 	public static final String USER = "sa";
 	public static final String PASS = "";
 
-	public static Properties getConnectionProviderProperties() {
+	public static Properties getConnectionProviderProperties(String dbName) {
 		Properties props = new Properties( null );
 		props.put( Environment.DRIVER, DRIVER );
-		props.put( Environment.URL, URL );
+		props.put( Environment.URL, String.format( URL, dbName ) );
 		props.put( Environment.USER, USER );
+		props.put( Environment.PASS, PASS );
 		return props;
 	}
 
-	public static ConnectionProvider buildConnectionProvider() {
+	public static Properties getConnectionProviderProperties() {
+		return getConnectionProviderProperties( "db1" );
+	}
+
+	public static DriverManagerConnectionProviderImpl buildConnectionProvider() {
 		return buildConnectionProvider( false );
 	}
 
-	public static ConnectionProvider buildConnectionProvider(final boolean allowAggressiveRelease) {
-		final Properties props = getConnectionProviderProperties();
+	public static DriverManagerConnectionProviderImpl buildConnectionProvider(String dbName) {
+		return buildConnectionProvider( getConnectionProviderProperties( dbName ), false );
+	}
+
+	public static DriverManagerConnectionProviderImpl buildConnectionProvider(final boolean allowAggressiveRelease) {
+		return buildConnectionProvider( getConnectionProviderProperties( "db1" ), allowAggressiveRelease );
+	}
+
+	private static DriverManagerConnectionProviderImpl buildConnectionProvider(Properties props, final boolean allowAggressiveRelease) {
 		DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl() {
 			public boolean supportsAggressiveRelease() {
 				return allowAggressiveRelease;
 			}
 		};
 		connectionProvider.configure( props );
 		return connectionProvider;
 	}
 
 	public static Dialect getCorrespondingDialect() {
 		return new H2Dialect();
 	}
 }
