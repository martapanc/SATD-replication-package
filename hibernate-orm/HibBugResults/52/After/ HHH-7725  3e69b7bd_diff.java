diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
index 43d9b5acdb..f8917f4653 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/BulkOperationCleanupAction.java
@@ -1,264 +1,264 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 
 /**
  * An {@link org.hibernate.engine.spi.ActionQueue} {@link org.hibernate.action.spi.Executable} for ensuring
  * shared cache cleanup in relation to performed bulk HQL queries.
  * <p/>
  * NOTE: currently this executes for <tt>INSERT</tt> queries as well as
  * <tt>UPDATE</tt> and <tt>DELETE</tt> queries.  For <tt>INSERT</tt> it is
  * really not needed as we'd have no invalid entity/collection data to
  * cleanup (we'd still nee to invalidate the appropriate update-timestamps
  * regions) as a result of this query.
  *
  * @author Steve Ebersole
  */
 public class BulkOperationCleanupAction implements Executable, Serializable {
 	private final Serializable[] affectedTableSpaces;
 
 	private final Set<EntityCleanup> entityCleanups = new HashSet<EntityCleanup>();
 	private final Set<CollectionCleanup> collectionCleanups = new HashSet<CollectionCleanup>();
 	private final Set<NaturalIdCleanup> naturalIdCleanups = new HashSet<NaturalIdCleanup>();
 
 	/**
 	 * Constructs an action to cleanup "affected cache regions" based on the
 	 * affected entity persisters.  The affected regions are defined as the
 	 * region (if any) of the entity persisters themselves, plus the
 	 * collection regions for any collection in which those entity
 	 * persisters participate as elements/keys/etc.
 	 *
 	 * @param session The session to which this request is tied.
 	 * @param affectedQueryables The affected entity persisters.
 	 */
-	public BulkOperationCleanupAction(SessionImplementor session, Queryable[] affectedQueryables) {
+	public BulkOperationCleanupAction(SessionImplementor session, Queryable... affectedQueryables) {
 		SessionFactoryImplementor factory = session.getFactory();
 		LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
 		for ( Queryable persister : affectedQueryables ) {
 			spacesList.addAll( Arrays.asList( (String[]) persister.getQuerySpaces() ) );
 
 			if ( persister.hasCache() ) {
 				entityCleanups.add( new EntityCleanup( persister.getCacheAccessStrategy() ) );
 			}
 			if ( persister.hasNaturalIdentifier() && persister.hasNaturalIdCache() ) {
 				naturalIdCleanups.add( new NaturalIdCleanup( persister.getNaturalIdCacheAccessStrategy() ) );
 			}
 
 			Set<String> roles = factory.getCollectionRolesByEntityParticipant( persister.getEntityName() );
 			if ( roles != null ) {
 				for ( String role : roles ) {
 					CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 					if ( collectionPersister.hasCache() ) {
 						collectionCleanups.add( new CollectionCleanup( collectionPersister.getCacheAccessStrategy() ) );
 					}
 				}
 			}
 		}
 
 		this.affectedTableSpaces = spacesList.toArray( new String[ spacesList.size() ] );
 	}
 
 	/**
 	 * Constructs an action to cleanup "affected cache regions" based on a
 	 * set of affected table spaces.  This differs from {@link #BulkOperationCleanupAction(SessionImplementor, Queryable[])}
 	 * in that here we have the affected <strong>table names</strong>.  From those
 	 * we deduce the entity persisters which are affected based on the defined
 	 * {@link EntityPersister#getQuerySpaces() table spaces}; and from there, we
 	 * determine the affected collection regions based on any collections
 	 * in which those entity persisters participate as elements/keys/etc.
 	 *
 	 * @param session The session to which this request is tied.
 	 * @param tableSpaces The table spaces.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public BulkOperationCleanupAction(SessionImplementor session, Set tableSpaces) {
 		LinkedHashSet<String> spacesList = new LinkedHashSet<String>();
 		spacesList.addAll( tableSpaces );
 
 		SessionFactoryImplementor factory = session.getFactory();
 		for ( String entityName : factory.getAllClassMetadata().keySet() ) {
 			final EntityPersister persister = factory.getEntityPersister( entityName );
 			final String[] entitySpaces = (String[]) persister.getQuerySpaces();
 			if ( affectedEntity( tableSpaces, entitySpaces ) ) {
 				spacesList.addAll( Arrays.asList( entitySpaces ) );
 
 				if ( persister.hasCache() ) {
 					entityCleanups.add( new EntityCleanup( persister.getCacheAccessStrategy() ) );
 				}
 				if ( persister.hasNaturalIdentifier() && persister.hasNaturalIdCache() ) {
 					naturalIdCleanups.add( new NaturalIdCleanup( persister.getNaturalIdCacheAccessStrategy() ) );
 				}
 
 				Set<String> roles = session.getFactory().getCollectionRolesByEntityParticipant( persister.getEntityName() );
 				if ( roles != null ) {
 					for ( String role : roles ) {
 						CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 						if ( collectionPersister.hasCache() ) {
 							collectionCleanups.add(
 									new CollectionCleanup( collectionPersister.getCacheAccessStrategy() )
 							);
 						}
 					}
 				}
 			}
 		}
 
 		this.affectedTableSpaces = spacesList.toArray( new String[ spacesList.size() ] );
 	}
 
 
 	/**
 	 * Check to determine whether the table spaces reported by an entity
 	 * persister match against the defined affected table spaces.
 	 *
 	 * @param affectedTableSpaces The table spaces reported to be affected by
 	 * the query.
 	 * @param checkTableSpaces The table spaces (from the entity persister)
 	 * to check against the affected table spaces.
 	 *
 	 * @return True if there are affected table spaces and any of the incoming
 	 * check table spaces occur in that set.
 	 */
 	private boolean affectedEntity(Set affectedTableSpaces, Serializable[] checkTableSpaces) {
 		if ( affectedTableSpaces == null || affectedTableSpaces.isEmpty() ) {
 			return true;
 		}
 
 		for ( Serializable checkTableSpace : checkTableSpaces ) {
 			if ( affectedTableSpaces.contains( checkTableSpace ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public Serializable[] getPropertySpaces() {
 		return affectedTableSpaces;
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return new AfterTransactionCompletionProcess() {
 			@Override
 			public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 				for ( EntityCleanup cleanup : entityCleanups ) {
 					cleanup.release();
 				}
 				entityCleanups.clear();
 
 				for ( NaturalIdCleanup cleanup : naturalIdCleanups ) {
 					cleanup.release();
 
 				}
 				entityCleanups.clear();
 
 				for ( CollectionCleanup cleanup : collectionCleanups ) {
 					cleanup.release();
 				}
 				collectionCleanups.clear();
 			}
 		};
 	}
 
 	@Override
 	public void beforeExecutions() throws HibernateException {
 		// nothing to do
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		// nothing to do		
 	}
 
 	private static class EntityCleanup {
 		private final EntityRegionAccessStrategy cacheAccess;
 		private final SoftLock cacheLock;
 
 		private EntityCleanup(EntityRegionAccessStrategy cacheAccess) {
 			this.cacheAccess = cacheAccess;
 			this.cacheLock = cacheAccess.lockRegion();
 			cacheAccess.removeAll();
 		}
 
 		private void release() {
 			cacheAccess.unlockRegion( cacheLock );
 		}
 	}
 
 	private static class CollectionCleanup {
 		private final CollectionRegionAccessStrategy cacheAccess;
 		private final SoftLock cacheLock;
 
 		private CollectionCleanup(CollectionRegionAccessStrategy cacheAccess) {
 			this.cacheAccess = cacheAccess;
 			this.cacheLock = cacheAccess.lockRegion();
 			cacheAccess.removeAll();
 		}
 
 		private void release() {
 			cacheAccess.unlockRegion( cacheLock );
 		}
 	}
 
 	private class NaturalIdCleanup {
 		private final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy;
 		private final SoftLock cacheLock;
 
 		public NaturalIdCleanup(NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy) {
 			this.naturalIdCacheAccessStrategy = naturalIdCacheAccessStrategy;
 			this.cacheLock = naturalIdCacheAccessStrategy.lockRegion();
 			naturalIdCacheAccessStrategy.removeAll();
 		}
 
 		private void release() {
 			naturalIdCacheAccessStrategy.unlockRegion( cacheLock );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index ec58aa3973..657619f4b4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,580 +1,582 @@
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
+
+	public static final String HQL_BULK_ID_STRATEGY = "hibernate.hql.bulk_id_strategy";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
index 0f007f713c..b11b15e788 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Settings.java
@@ -1,471 +1,483 @@
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
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
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
 	private boolean sessionFactoryNameAlsoJndiName;
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
 	private boolean initializeLazyStateOutsideTransactions;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 //	private BytecodeProvider bytecodeProvider;
 	private String importFiles;
 	private MultiTenancyStrategy multiTenancyStrategy;
 
 	private JtaPlatform jtaPlatform;
 
+	private MultiTableBulkIdStrategy multiTableBulkIdStrategy;
+
+
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
 
 	public boolean isSessionFactoryNameAlsoJndiName() {
 		return sessionFactoryNameAlsoJndiName;
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
 
 	void setSessionFactoryNameAlsoJndiName(boolean sessionFactoryNameAlsoJndiName) {
 		this.sessionFactoryNameAlsoJndiName = sessionFactoryNameAlsoJndiName;
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
 
 	public MultiTenancyStrategy getMultiTenancyStrategy() {
 		return multiTenancyStrategy;
 	}
 
 	void setMultiTenancyStrategy(MultiTenancyStrategy multiTenancyStrategy) {
 		this.multiTenancyStrategy = multiTenancyStrategy;
 	}
 
 	public boolean isInitializeLazyStateOutsideTransactionsEnabled() {
 		return initializeLazyStateOutsideTransactions;
 	}
 
 	void setInitializeLazyStateOutsideTransactions(boolean initializeLazyStateOutsideTransactions) {
 		this.initializeLazyStateOutsideTransactions = initializeLazyStateOutsideTransactions;
 	}
+
+	public MultiTableBulkIdStrategy getMultiTableBulkIdStrategy() {
+		return multiTableBulkIdStrategy;
+	}
+
+	void setMultiTableBulkIdStrategy(MultiTableBulkIdStrategy multiTableBulkIdStrategy) {
+		this.multiTableBulkIdStrategy = multiTableBulkIdStrategy;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index b578c18036..71bf2a1f84 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,470 +1,486 @@
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
+import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
+import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
+import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
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
 
 		String sessionFactoryName = props.getProperty( AvailableSettings.SESSION_FACTORY_NAME );
 		settings.setSessionFactoryName( sessionFactoryName );
 		settings.setSessionFactoryNameAlsoJndiName(
 				ConfigurationHelper.getBoolean( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, props, true )
 		);
 
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
 
+		MultiTableBulkIdStrategy multiTableBulkIdStrategy = serviceRegistry.getService( StrategySelector.class )
+				.resolveStrategy(
+						MultiTableBulkIdStrategy.class,
+						properties.getProperty( AvailableSettings.HQL_BULK_ID_STRATEGY )
+				);
+		if ( multiTableBulkIdStrategy == null ) {
+			multiTableBulkIdStrategy = jdbcServices.getDialect().supportsTemporaryTables()
+					? TemporaryTableBulkIdStrategy.INSTANCE
+					: new PersistentTableBulkIdStrategy();
+		}
+		settings.setMultiTableBulkIdStrategy( multiTableBulkIdStrategy );
+
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(AvailableSettings.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(AvailableSettings.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(AvailableSettings.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(AvailableSettings.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(AvailableSettings.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(AvailableSettings.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(AvailableSettings.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		}
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 		String releaseModeName = ConfigurationHelper.getString( AvailableSettings.RELEASE_CONNECTIONS, properties, "auto" );
 		if ( debugEnabled ) {
 			LOG.debugf( "Connection release mode: %s", releaseModeName );
 		}
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 				// we need to make sure the underlying JDBC connection access supports aggressive release...
 				boolean supportsAgrressiveRelease = multiTenancyStrategy.requiresMultiTenantConnectionProvider()
 						? serviceRegistry.getService( MultiTenantConnectionProvider.class ).supportsAggressiveRelease()
 						: serviceRegistry.getService( ConnectionProvider.class ).supportsAggressiveRelease();
 				if ( ! supportsAgrressiveRelease ) {
 					LOG.unsupportedAfterStatement();
 					releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 				}
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( AvailableSettings.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( AvailableSettings.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( AvailableSettings.MAX_FETCH_DEPTH, properties );
 		if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( AvailableSettings.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( AvailableSettings.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(AvailableSettings.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap( AvailableSettings.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( AvailableSettings.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(AvailableSettings.USE_QUERY_CACHE, properties);
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
 				AvailableSettings.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( AvailableSettings.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( AvailableSettings.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( AvailableSettings.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( AvailableSettings.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( AvailableSettings.HBM2DDL_AUTO );
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
 		settings.setImportFiles( properties.getProperty( AvailableSettings.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( AvailableSettings.DEFAULT_ENTITY_MODE ) );
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( AvailableSettings.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(AvailableSettings.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
 //		String provider = properties.getProperty( AvailableSettings.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		boolean initializeLazyStateOutsideTransactionsEnabled = ConfigurationHelper.getBoolean(
 				AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS,
 				properties,
 				false
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Allow initialization of lazy state outside session : : %s", enabledDisabled( initializeLazyStateOutsideTransactionsEnabled ) );
 		}
 		settings.setInitializeLazyStateOutsideTransactions( initializeLazyStateOutsideTransactionsEnabled );
 
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
 				AvailableSettings.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
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
 						AvailableSettings.CACHE_REGION_FACTORY, properties, null
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
 						AvailableSettings.CACHE_REGION_FACTORY, properties, null
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
 				AvailableSettings.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/BasicExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/BasicExecutor.java
index 04b7feb2e0..2c2aeaf164 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/BasicExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/BasicExecutor.java
@@ -1,116 +1,115 @@
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
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.List;
 
 import antlr.RecognitionException;
 
 import org.hibernate.HibernateException;
+import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.QuerySyntaxException;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 
 /**
  * Implementation of BasicExecutor.
  *
  * @author Steve Ebersole
  */
-public class BasicExecutor extends AbstractStatementExecutor {
-
+public class BasicExecutor implements StatementExecutor {
+	private final SessionFactoryImplementor factory;
 	private final Queryable persister;
 	private final String sql;
 	private final List parameterSpecifications;
 
 	public BasicExecutor(HqlSqlWalker walker, Queryable persister) {
-        super(walker, null);
+		this.factory = walker.getSessionFactoryHelper().getFactory();
 		this.persister = persister;
 		try {
-			SqlGenerator gen = new SqlGenerator( getFactory() );
+			SqlGenerator gen = new SqlGenerator( factory );
 			gen.statement( walker.getAST() );
 			sql = gen.getSQL();
 			gen.getParseErrorHandler().throwQueryException();
 			parameterSpecifications = gen.getCollectedParameters();
 		}
 		catch ( RecognitionException e ) {
 			throw QuerySyntaxException.convert( e );
 		}
 	}
 
 	public String[] getSqlStatements() {
 		return new String[] { sql };
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
-
-		coordinateSharedCacheCleanup( session );
+		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, persister );
+		if ( session.isEventSource() ) {
+			( (EventSource) session ).getActionQueue().addAction( action );
+		}
+		else {
+			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
+		}
 
 		PreparedStatement st = null;
 		RowSelection selection = parameters.getRowSelection();
 
 		try {
 			try {
 				st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
 				Iterator parameterSpecifications = this.parameterSpecifications.iterator();
 				int pos = 1;
 				while ( parameterSpecifications.hasNext() ) {
 					final ParameterSpecification paramSpec = ( ParameterSpecification ) parameterSpecifications.next();
 					pos += paramSpec.bind( st, parameters, session, pos );
 				}
 				if ( selection != null ) {
 					if ( selection.getTimeout() != null ) {
 						st.setQueryTimeout( selection.getTimeout().intValue() );
 					}
 				}
 
 				return st.executeUpdate();
 			}
 			finally {
 				if ( st != null ) {
 					st.close();
 				}
 			}
 		}
 		catch( SQLException sqle ) {
-			throw getFactory().getSQLExceptionHelper().convert(
-			        sqle,
-			        "could not execute update query",
-			        sql
-				);
+			throw factory.getSQLExceptionHelper().convert( sqle, "could not execute update query", sql );
 		}
 	}
-
-	@Override
-    protected Queryable[] getAffectedQueryables() {
-		return new Queryable[] { persister };
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
index 5c2cb36ed6..b0b5a75e22 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
@@ -1,166 +1,65 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.hql.internal.ast.exec;
 
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import java.util.Iterator;
-
-import org.jboss.logging.Logger;
-
 import org.hibernate.HibernateException;
+import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.hql.internal.ast.tree.DeleteStatement;
-import org.hibernate.hql.internal.ast.tree.FromElement;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.param.ParameterSpecification;
-import org.hibernate.persister.entity.Queryable;
-import org.hibernate.sql.Delete;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 
 /**
  * Implementation of MultiTableDeleteExecutor.
  *
  * @author Steve Ebersole
  */
-public class MultiTableDeleteExecutor extends AbstractStatementExecutor {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       MultiTableDeleteExecutor.class.getName());
-
-	private final Queryable persister;
-	private final String idInsertSelect;
-	private final String[] deletes;
+public class MultiTableDeleteExecutor implements StatementExecutor {
+	private final MultiTableBulkIdStrategy.DeleteHandler deleteHandler;
 
 	public MultiTableDeleteExecutor(HqlSqlWalker walker) {
-        super(walker, null);
-
-		if ( !walker.getSessionFactoryHelper().getFactory().getDialect().supportsTemporaryTables() ) {
-			throw new HibernateException( "cannot doAfterTransactionCompletion multi-table deletes using dialect not supporting temp tables" );
-		}
-
-		DeleteStatement deleteStatement = ( DeleteStatement ) walker.getAST();
-		FromElement fromElement = deleteStatement.getFromClause().getFromElement();
-		String bulkTargetAlias = fromElement.getTableAlias();
-		this.persister = fromElement.getQueryable();
-
-		this.idInsertSelect = generateIdInsertSelect( persister, bulkTargetAlias, deleteStatement.getWhereClause() );
-		LOG.tracev( "Generated ID-INSERT-SELECT SQL (multi-table delete) : {0}", idInsertSelect );
-
-		String[] tableNames = persister.getConstraintOrderedTableNameClosure();
-		String[][] columnNames = persister.getContraintOrderedTableKeyColumnClosure();
-		String idSubselect = generateIdSubselect( persister );
-
-		deletes = new String[tableNames.length];
-		for ( int i = tableNames.length - 1; i >= 0; i-- ) {
-			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
-			//      the difficulty is the ordering of the tables here vs the cascade attributes on the persisters ->
-			//          the table info gotten here should really be self-contained (i.e., a class representation
-			//          defining all the needed attributes), then we could then get an array of those
-			final Delete delete = new Delete()
-					.setTableName( tableNames[i] )
-					.setWhere( "(" + StringHelper.join( ", ", columnNames[i] ) + ") IN (" + idSubselect + ")" );
-			if ( getFactory().getSettings().isCommentsEnabled() ) {
-				delete.setComment( "bulk delete" );
-			}
-
-			deletes[i] = delete.toStatementString();
-		}
+		MultiTableBulkIdStrategy strategy = walker.getSessionFactoryHelper()
+				.getFactory()
+				.getSettings()
+				.getMultiTableBulkIdStrategy();
+		 this.deleteHandler = strategy.buildDeleteHandler( walker.getSessionFactoryHelper().getFactory(), walker );
 	}
 
 	public String[] getSqlStatements() {
-		return deletes;
+		return deleteHandler.getSqlStatements();
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
-		coordinateSharedCacheCleanup( session );
-
-		createTemporaryTableIfNecessary( persister, session );
-
-		try {
-			// First, save off the pertinent ids, saving the number of pertinent ids for return
-			PreparedStatement ps = null;
-			int resultCount = 0;
-			try {
-				try {
-					ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( idInsertSelect, false );
-					Iterator paramSpecifications = getIdSelectParameterSpecifications().iterator();
-					int pos = 1;
-					while ( paramSpecifications.hasNext() ) {
-						final ParameterSpecification paramSpec = ( ParameterSpecification ) paramSpecifications.next();
-						pos += paramSpec.bind( ps, parameters, session, pos );
-					}
-					resultCount = ps.executeUpdate();
-				}
-				finally {
-					if ( ps != null ) {
-						ps.close();
-					}
-				}
-			}
-			catch( SQLException e ) {
-				throw getFactory().getSQLExceptionHelper().convert(
-				        e,
-				        "could not insert/select ids for bulk delete",
-				        idInsertSelect
-					);
-			}
-
-			// Start performing the deletes
-			for ( int i = 0; i < deletes.length; i++ ) {
-				try {
-					try {
-						ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( deletes[i], false );
-						ps.executeUpdate();
-					}
-					finally {
-						if ( ps != null ) {
-							ps.close();
-						}
-					}
-				}
-				catch( SQLException e ) {
-					throw getFactory().getSQLExceptionHelper().convert(
-					        e,
-					        "error performing bulk delete",
-					        deletes[i]
-						);
-				}
-			}
-
-			return resultCount;
+		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, deleteHandler.getTargetedQueryable() );
+		if ( session.isEventSource() ) {
+			( (EventSource) session ).getActionQueue().addAction( action );
 		}
-		finally {
-			dropTemporaryTableIfNecessary( persister, session );
+		else {
+			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
-	}
 
-	@Override
-    protected Queryable[] getAffectedQueryables() {
-		return new Queryable[] { persister };
+		return deleteHandler.execute( session, parameters );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java
index b5168bb3cb..b78afe8f3f 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableUpdateExecutor.java
@@ -1,201 +1,67 @@
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
 
-import java.sql.PreparedStatement;
-import java.sql.SQLException;
-import java.util.ArrayList;
-import java.util.Iterator;
-import java.util.List;
-
-import org.jboss.logging.Logger;
-
 import org.hibernate.HibernateException;
+import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
-import org.hibernate.hql.internal.ast.tree.FromElement;
-import org.hibernate.hql.internal.ast.tree.UpdateStatement;
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.param.ParameterSpecification;
-import org.hibernate.persister.entity.Queryable;
-import org.hibernate.sql.Update;
+import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 
 /**
  * Implementation of MultiTableUpdateExecutor.
  *
  * @author Steve Ebersole
  */
-public class MultiTableUpdateExecutor extends AbstractStatementExecutor {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       MultiTableUpdateExecutor.class.getName());
-
-	private final Queryable persister;
-	private final String idInsertSelect;
-	private final String[] updates;
-	private final ParameterSpecification[][] hqlParameters;
+public class MultiTableUpdateExecutor implements StatementExecutor {
+	private final MultiTableBulkIdStrategy.UpdateHandler updateHandler;
 
 	public MultiTableUpdateExecutor(HqlSqlWalker walker) {
-        super(walker, null);
-
-		if ( !walker.getSessionFactoryHelper().getFactory().getDialect().supportsTemporaryTables() ) {
-			throw new HibernateException( "cannot doAfterTransactionCompletion multi-table updates using dialect not supporting temp tables" );
-		}
-
-		UpdateStatement updateStatement = ( UpdateStatement ) walker.getAST();
-		FromElement fromElement = updateStatement.getFromClause().getFromElement();
-		String bulkTargetAlias = fromElement.getTableAlias();
-		this.persister = fromElement.getQueryable();
-
-		this.idInsertSelect = generateIdInsertSelect( persister, bulkTargetAlias, updateStatement.getWhereClause() );
-		LOG.tracev( "Generated ID-INSERT-SELECT SQL (multi-table update) : {0}", idInsertSelect );
-
-		String[] tableNames = persister.getConstraintOrderedTableNameClosure();
-		String[][] columnNames = persister.getContraintOrderedTableKeyColumnClosure();
-
-		String idSubselect = generateIdSubselect( persister );
-		List assignmentSpecifications = walker.getAssignmentSpecifications();
-
-		updates = new String[tableNames.length];
-		hqlParameters = new ParameterSpecification[tableNames.length][];
-		for ( int tableIndex = 0; tableIndex < tableNames.length; tableIndex++ ) {
-			boolean affected = false;
-			List parameterList = new ArrayList();
-			Update update = new Update( getFactory().getDialect() )
-					.setTableName( tableNames[tableIndex] )
-					.setWhere( "(" + StringHelper.join( ", ", columnNames[tableIndex] ) + ") IN (" + idSubselect + ")" );
-			if ( getFactory().getSettings().isCommentsEnabled() ) {
-				update.setComment( "bulk update" );
-			}
-			final Iterator itr = assignmentSpecifications.iterator();
-			while ( itr.hasNext() ) {
-				final AssignmentSpecification specification = ( AssignmentSpecification ) itr.next();
-				if ( specification.affectsTable( tableNames[tableIndex] ) ) {
-					affected = true;
-					update.appendAssignmentFragment( specification.getSqlAssignmentFragment() );
-					if ( specification.getParameters() != null ) {
-						for ( int paramIndex = 0; paramIndex < specification.getParameters().length; paramIndex++ ) {
-							parameterList.add( specification.getParameters()[paramIndex] );
-						}
-					}
-				}
-			}
-			if ( affected ) {
-				updates[tableIndex] = update.toStatementString();
-				hqlParameters[tableIndex] = ( ParameterSpecification[] ) parameterList.toArray( new ParameterSpecification[0] );
-			}
-		}
-	}
-
-	public Queryable getAffectedQueryable() {
-		return persister;
+		MultiTableBulkIdStrategy strategy = walker.getSessionFactoryHelper()
+				.getFactory()
+				.getSettings()
+				.getMultiTableBulkIdStrategy();
+		this.updateHandler = strategy.buildUpdateHandler( walker.getSessionFactoryHelper().getFactory(), walker );
 	}
 
 	public String[] getSqlStatements() {
-		return updates;
+		return updateHandler.getSqlStatements();
 	}
 
 	public int execute(QueryParameters parameters, SessionImplementor session) throws HibernateException {
-		coordinateSharedCacheCleanup( session );
+		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, updateHandler.getTargetedQueryable() );
 
-		createTemporaryTableIfNecessary( persister, session );
-
-		try {
-			// First, save off the pertinent ids, as the return value
-			PreparedStatement ps = null;
-			int resultCount = 0;
-			try {
-				try {
-					ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( idInsertSelect, false );
-//					int parameterStart = getWalker().getNumberOfParametersInSetClause();
-//					List allParams = getIdSelectParameterSpecifications();
-//					Iterator whereParams = allParams.subList( parameterStart, allParams.size() ).iterator();
-					Iterator whereParams = getIdSelectParameterSpecifications().iterator();
-					int sum = 1; // jdbc params are 1-based
-					while ( whereParams.hasNext() ) {
-						sum += ( ( ParameterSpecification ) whereParams.next() ).bind( ps, parameters, session, sum );
-					}
-					resultCount = ps.executeUpdate();
-				}
-				finally {
-					if ( ps != null ) {
-						ps.close();
-					}
-				}
-			}
-			catch( SQLException e ) {
-				throw getFactory().getSQLExceptionHelper().convert(
-				        e,
-				        "could not insert/select ids for bulk update",
-				        idInsertSelect
-					);
-			}
-
-			// Start performing the updates
-			for ( int i = 0; i < updates.length; i++ ) {
-				if ( updates[i] == null ) {
-					continue;
-				}
-				try {
-					try {
-						ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( updates[i], false );
-						if ( hqlParameters[i] != null ) {
-							int position = 1; // jdbc params are 1-based
-							for ( int x = 0; x < hqlParameters[i].length; x++ ) {
-								position += hqlParameters[i][x].bind( ps, parameters, session, position );
-							}
-						}
-						ps.executeUpdate();
-					}
-					finally {
-						if ( ps != null ) {
-							ps.close();
-						}
-					}
-				}
-				catch( SQLException e ) {
-					throw getFactory().getSQLExceptionHelper().convert(
-					        e,
-					        "error performing bulk update",
-					        updates[i]
-						);
-				}
-			}
-
-			return resultCount;
+		if ( session.isEventSource() ) {
+			( (EventSource) session ).getActionQueue().addAction( action );
 		}
-		finally {
-			dropTemporaryTableIfNecessary( persister, session );
+		else {
+			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
 		}
-	}
 
-	@Override
-    protected Queryable[] getAffectedQueryables() {
-		return new Queryable[] { persister };
+		return updateHandler.execute( session, parameters );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
new file mode 100644
index 0000000000..1bad685122
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
@@ -0,0 +1,173 @@
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
+package org.hibernate.hql.spi;
+
+import java.sql.SQLException;
+import java.util.Collections;
+import java.util.List;
+
+import antlr.RecognitionException;
+import antlr.collections.AST;
+
+import org.hibernate.HibernateException;
+import org.hibernate.JDBCException;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.internal.ast.SqlGenerator;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.param.ParameterSpecification;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.sql.InsertSelect;
+import org.hibernate.sql.Select;
+import org.hibernate.sql.SelectFragment;
+
+/**
+ * @author Steve Ebersole
+ */
+public class AbstractTableBasedBulkIdHandler {
+	private final SessionFactoryImplementor sessionFactory;
+	private final HqlSqlWalker walker;
+
+	public AbstractTableBasedBulkIdHandler(SessionFactoryImplementor sessionFactory, HqlSqlWalker walker) {
+		this.sessionFactory = sessionFactory;
+		this.walker = walker;
+	}
+
+	protected SessionFactoryImplementor factory() {
+		return sessionFactory;
+	}
+
+	protected HqlSqlWalker walker() {
+		return walker;
+	}
+
+	protected JDBCException convert(SQLException e, String message, String sql) {
+		throw factory().getSQLExceptionHelper().convert( e, message, sql );
+	}
+
+	protected static class ProcessedWhereClause {
+		public static final ProcessedWhereClause NO_WHERE_CLAUSE = new ProcessedWhereClause();
+
+		private final String userWhereClauseFragment;
+		private final List<ParameterSpecification> idSelectParameterSpecifications;
+
+		private ProcessedWhereClause() {
+			this( "", Collections.<ParameterSpecification>emptyList() );
+		}
+
+		public ProcessedWhereClause(String userWhereClauseFragment, List<ParameterSpecification> idSelectParameterSpecifications) {
+			this.userWhereClauseFragment = userWhereClauseFragment;
+			this.idSelectParameterSpecifications = idSelectParameterSpecifications;
+		}
+
+		public String getUserWhereClauseFragment() {
+			return userWhereClauseFragment;
+		}
+
+		public List<ParameterSpecification> getIdSelectParameterSpecifications() {
+			return idSelectParameterSpecifications;
+		}
+	}
+
+	@SuppressWarnings("unchecked")
+	protected ProcessedWhereClause processWhereClause(AST whereClause) {
+		if ( whereClause.getNumberOfChildren() != 0 ) {
+			// If a where clause was specified in the update/delete query, use it to limit the
+			// returned ids here...
+			try {
+				SqlGenerator sqlGenerator = new SqlGenerator( sessionFactory );
+				sqlGenerator.whereClause( whereClause );
+				String userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
+				List<ParameterSpecification> idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
+
+				return new ProcessedWhereClause( userWhereClause, idSelectParameterSpecifications );
+			}
+			catch ( RecognitionException e ) {
+				throw new HibernateException( "Unable to generate id select for DML operation", e );
+			}
+		}
+		else {
+			return ProcessedWhereClause.NO_WHERE_CLAUSE;
+		}
+	}
+
+	protected String generateIdInsertSelect(Queryable persister, String tableAlias, ProcessedWhereClause whereClause) {
+		Select select = new Select( sessionFactory.getDialect() );
+		SelectFragment selectFragment = new SelectFragment()
+				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
+		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) + extraIdSelectValues() );
+
+		String rootTableName = persister.getTableName();
+		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
+		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
+
+		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
+
+		if ( whereJoinFragment == null ) {
+			whereJoinFragment = "";
+		}
+		else {
+			whereJoinFragment = whereJoinFragment.trim();
+			if ( whereJoinFragment.startsWith( "and" ) ) {
+				whereJoinFragment = whereJoinFragment.substring( 4 );
+			}
+		}
+
+		if ( whereClause.userWhereClauseFragment.length() > 0 ) {
+			if ( whereJoinFragment.length() > 0 ) {
+				whereJoinFragment += " and ";
+			}
+		}
+
+		select.setWhereClause( whereJoinFragment + whereClause.userWhereClauseFragment );
+
+		InsertSelect insert = new InsertSelect( sessionFactory.getDialect() );
+		if ( sessionFactory.getSettings().isCommentsEnabled() ) {
+			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
+		}
+		insert.setTableName( determineIdTableName( persister ) );
+		insert.setSelect( select );
+		return insert.toStatementString();
+	}
+
+	protected String extraIdSelectValues() {
+		return "";
+	}
+
+	protected String determineIdTableName(Queryable persister) {
+		return persister.getTemporaryIdTableName();
+	}
+
+	protected String generateIdSubselect(Queryable persister) {
+		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
+				" from " + determineIdTableName( persister );
+	}
+
+	protected void prepareForUse(Queryable persister, SessionImplementor session) {
+	}
+
+	protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
new file mode 100644
index 0000000000..e98ff99ac5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
@@ -0,0 +1,105 @@
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
+package org.hibernate.hql.spi;
+
+import java.util.Map;
+
+import org.hibernate.cfg.Mappings;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.persister.entity.Queryable;
+
+/**
+ * Generalized strategy contract for handling multi-table bulk HQL operations.
+ *
+ * @author Steve Ebersole
+ */
+public interface MultiTableBulkIdStrategy {
+	/**
+	 * Prepare the strategy.  Called as the SessionFactory is being built.  Intended patterns here include:<ul>
+	 *     <li>Adding tables to the passed Mappings, to be picked by by "schema management tools"</li>
+	 *     <li>Manually creating the tables immediately through the passed JDBC Connection access</li>
+	 * </ul>
+	 *
+	 * @param dialect The dialect
+	 * @param connectionAccess Access to the JDBC Connection
+	 * @param mappings The Hibernate Mappings object, for access to O/RM mapping information
+	 * @param mapping The Hibernate Mapping contract, mainly for use in DDL generation
+	 * @param settings Configuration settings
+	 */
+	public void prepare(Dialect dialect, JdbcConnectionAccess connectionAccess, Mappings mappings, Mapping mapping, Map settings);
+
+	/**
+	 * Release the strategy.   Called as the SessionFactory is being shut down.
+	 *
+	 * @param dialect The dialect
+	 * @param connectionAccess Access to the JDBC Connection
+	 */
+	public void release(Dialect dialect, JdbcConnectionAccess connectionAccess);
+
+	/**
+	 * Handler for dealing with multi-table HQL bulk update statements.
+	 */
+	public static interface UpdateHandler {
+		public Queryable getTargetedQueryable();
+		public String[] getSqlStatements();
+
+		public int execute(SessionImplementor session, QueryParameters queryParameters);
+	}
+
+	/**
+	 * Build a handler capable of handling the bulk update indicated by the given walker.
+	 *
+	 * @param factory The SessionFactory
+	 * @param walker The AST walker, representing the update query
+	 *
+	 * @return The handler
+	 */
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
+
+	/**
+	 * Handler for dealing with multi-table HQL bulk delete statements.
+	 */
+	public static interface DeleteHandler {
+		public Queryable getTargetedQueryable();
+		public String[] getSqlStatements();
+
+		public int execute(SessionImplementor session, QueryParameters queryParameters);
+	}
+
+	/**
+	 * Build a handler capable of handling the bulk delete indicated by the given walker.
+	 *
+	 * @param factory The SessionFactory
+	 * @param walker The AST walker, representing the delete query
+	 *
+	 * @return The handler
+	 */
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
new file mode 100644
index 0000000000..1655f13340
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/PersistentTableBulkIdStrategy.java
@@ -0,0 +1,241 @@
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
+package org.hibernate.hql.spi;
+
+import java.sql.Connection;
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.sql.Statement;
+import java.util.ArrayList;
+import java.util.Iterator;
+import java.util.List;
+import java.util.Map;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.cfg.Mappings;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.internal.AbstractSessionImpl;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.mapping.Column;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.Table;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.type.UUIDCharType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class PersistentTableBulkIdStrategy implements MultiTableBulkIdStrategy {
+	private static final Logger log = Logger.getLogger( PersistentTableBulkIdStrategy.class );
+
+	public static final String CLEAN_UP_ID_TABLES = "hibernate.hql.bulk_id_strategy.persistent.clean_up";
+
+	private boolean cleanUpTables;
+	private List<String> tableCleanUpDdl;
+
+	@Override
+	public void prepare(
+			Dialect dialect,
+			JdbcConnectionAccess connectionAccess,
+			Mappings mappings,
+			Mapping mapping,
+			Map settings) {
+		cleanUpTables = ConfigurationHelper.getBoolean( CLEAN_UP_ID_TABLES, settings, false );
+		final Iterator<PersistentClass> entityMappings = mappings.iterateClasses();
+		final List<Table> idTableDefinitions = new ArrayList<Table>();
+		while ( entityMappings.hasNext() ) {
+			final PersistentClass entityMapping = entityMappings.next();
+			final Table idTableDefinition = generateIdTableDefinition( entityMapping );
+			idTableDefinitions.add( idTableDefinition );
+		}
+		exportTableDefinitions( idTableDefinitions, dialect, connectionAccess, mappings, mapping );
+	}
+
+	protected Table generateIdTableDefinition(PersistentClass entityMapping) {
+		Table idTable = new Table( entityMapping.getTemporaryIdTableName() );
+		Iterator itr = entityMapping.getIdentityTable().getPrimaryKey().getColumnIterator();
+		while( itr.hasNext() ) {
+			Column column = (Column) itr.next();
+			idTable.addColumn( column.clone()  );
+		}
+		Column sessionIdColumn = new Column( "hib_sess_id" );
+		sessionIdColumn.setSqlType( "CHAR(36)" );
+		sessionIdColumn.setComment( "Used to hold the Hibernate Session identifier" );
+		idTable.addColumn( sessionIdColumn );
+
+		idTable.setComment( "Used to hold id values for the " + entityMapping.getEntityName() + " class" );
+		return idTable;
+	}
+
+	protected void exportTableDefinitions(
+			List<Table> idTableDefinitions,
+			Dialect dialect,
+			JdbcConnectionAccess connectionAccess,
+			Mappings mappings,
+			Mapping mapping) {
+		try {
+			Connection connection = connectionAccess.obtainConnection();
+
+			try {
+				Statement statement = connection.createStatement();
+
+				for ( Table idTableDefinition : idTableDefinitions ) {
+					if ( cleanUpTables ) {
+						if ( tableCleanUpDdl == null ) {
+							tableCleanUpDdl = new ArrayList<String>();
+						}
+						tableCleanUpDdl.add( idTableDefinition.sqlDropString( dialect, null, null  ) );
+					}
+					try {
+						statement.execute( idTableDefinition.sqlCreateString( dialect, mapping, null, null ) );
+					}
+					catch (SQLException e) {
+						log.debugf( "Error attempting to export id-table [%s] : %s", idTableDefinition.getName(), e.getMessage() );
+					}
+				}
+			}
+			catch (SQLException e) {
+				log.error( "Unable to use JDBC Connection to create Statement", e );
+			}
+			finally {
+				try {
+					connectionAccess.releaseConnection( connection );
+				}
+				catch (SQLException ignore) {
+				}
+			}
+		}
+		catch (SQLException e) {
+			log.error( "Unable obtain JDBC Connection", e );
+		}
+	}
+
+	@Override
+	public void release(Dialect dialect, JdbcConnectionAccess connectionAccess) {
+		if ( ! cleanUpTables ) {
+			return;
+		}
+
+		try {
+			Connection connection = connectionAccess.obtainConnection();
+
+			try {
+				Statement statement = connection.createStatement();
+
+				for ( String cleanupDdl : tableCleanUpDdl ) {
+					try {
+						statement.execute( cleanupDdl );
+					}
+					catch (SQLException e) {
+						log.debugf( "Error attempting to cleanup id-table : [%s]", e.getMessage() );
+					}
+				}
+			}
+			catch (SQLException e) {
+				log.error( "Unable to use JDBC Connection to create Statement", e );
+			}
+			finally {
+				try {
+					connectionAccess.releaseConnection( connection );
+				}
+				catch (SQLException ignore) {
+				}
+			}
+		}
+		catch (SQLException e) {
+			log.error( "Unable obtain JDBC Connection", e );
+		}
+	}
+
+	@Override
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedUpdateHandlerImpl( factory, walker ) {
+			@Override
+			protected String extraIdSelectValues() {
+				return "cast(? as char)";
+			}
+
+			@Override
+			protected String generateIdSubselect(Queryable persister) {
+				return super.generateIdSubselect( persister ) + " where hib_sess_id=?";
+			}
+
+			@Override
+			protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
+				if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
+					throw new HibernateException( "Only available on SessionImpl instances" );
+				}
+				UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), pos, session );
+				return 1;
+			}
+
+			@Override
+			protected void handleAddedParametersOnUpdate(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
+				if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
+					throw new HibernateException( "Only available on SessionImpl instances" );
+				}
+				UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), position, session );
+			}
+		};
+	}
+
+	@Override
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedDeleteHandlerImpl( factory, walker ) {
+			@Override
+			protected String extraIdSelectValues() {
+				return "cast(? as char)";
+			}
+
+			@Override
+			protected String generateIdSubselect(Queryable persister) {
+				return super.generateIdSubselect( persister ) + " where hib_sess_id=?";
+			}
+
+			@Override
+			protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
+				if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
+					throw new HibernateException( "Only available on SessionImpl instances" );
+				}
+				UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), pos, session );
+				return 1;
+			}
+
+			@Override
+			protected void handleAddedParametersOnDelete(PreparedStatement ps, SessionImplementor session) throws SQLException {
+				if ( ! AbstractSessionImpl.class.isInstance( session ) ) {
+					throw new HibernateException( "Only available on SessionImpl instances" );
+				}
+				UUIDCharType.INSTANCE.set( ps, ( (AbstractSessionImpl) session ).getSessionIdentifier(), 1, session );
+			}
+		};
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java
new file mode 100644
index 0000000000..27f510a0d1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java
@@ -0,0 +1,164 @@
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
+package org.hibernate.hql.spi;
+
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.internal.ast.tree.DeleteStatement;
+import org.hibernate.hql.internal.ast.tree.FromElement;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.param.ParameterSpecification;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.sql.Delete;
+
+/**
+* @author Steve Ebersole
+*/
+class TableBasedDeleteHandlerImpl
+		extends AbstractTableBasedBulkIdHandler
+		implements MultiTableBulkIdStrategy.DeleteHandler {
+	private static final Logger log = Logger.getLogger( TableBasedDeleteHandlerImpl.class );
+
+	private final Queryable targetedPersister;
+
+	private final String idInsertSelect;
+	private final List<ParameterSpecification> idSelectParameterSpecifications;
+	private final String[] deletes;
+
+	TableBasedDeleteHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		super( factory, walker );
+
+		DeleteStatement deleteStatement = ( DeleteStatement ) walker.getAST();
+		FromElement fromElement = deleteStatement.getFromClause().getFromElement();
+
+		this.targetedPersister = fromElement.getQueryable();
+		final String bulkTargetAlias = fromElement.getTableAlias();
+
+		final ProcessedWhereClause processedWhereClause = processWhereClause( deleteStatement.getWhereClause() );
+		this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
+		this.idInsertSelect = generateIdInsertSelect( targetedPersister, bulkTargetAlias, processedWhereClause );
+		log.tracev( "Generated ID-INSERT-SELECT SQL (multi-table delete) : {0}", idInsertSelect );
+
+		String[] tableNames = targetedPersister.getConstraintOrderedTableNameClosure();
+		String[][] columnNames = targetedPersister.getContraintOrderedTableKeyColumnClosure();
+		String idSubselect = generateIdSubselect( targetedPersister );
+
+		deletes = new String[tableNames.length];
+		for ( int i = tableNames.length - 1; i >= 0; i-- ) {
+			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
+			//      the difficulty is the ordering of the tables here vs the cascade attributes on the persisters ->
+			//          the table info gotten here should really be self-contained (i.e., a class representation
+			//          defining all the needed attributes), then we could then get an array of those
+			final Delete delete = new Delete()
+					.setTableName( tableNames[i] )
+					.setWhere( "(" + StringHelper.join( ", ", columnNames[i] ) + ") IN (" + idSubselect + ")" );
+			if ( factory().getSettings().isCommentsEnabled() ) {
+				delete.setComment( "bulk delete" );
+			}
+
+			deletes[i] = delete.toStatementString();
+		}
+	}
+
+	@Override
+	public Queryable getTargetedQueryable() {
+		return targetedPersister;
+	}
+
+	@Override
+	public String[] getSqlStatements() {
+		return deletes;
+	}
+
+	@Override
+	public int execute(SessionImplementor session, QueryParameters queryParameters) {
+		prepareForUse( targetedPersister, session );
+		try {
+			PreparedStatement ps = null;
+			int resultCount = 0;
+			try {
+				try {
+					ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( idInsertSelect, false );
+					int pos = 1;
+					pos += handlePrependedParametersOnIdSelection( ps, session, pos );
+					for ( ParameterSpecification parameterSpecification : idSelectParameterSpecifications ) {
+						pos += parameterSpecification.bind( ps, queryParameters, session, pos );
+					}
+					resultCount = ps.executeUpdate();
+				}
+				finally {
+					if ( ps != null ) {
+						ps.close();
+					}
+				}
+			}
+			catch( SQLException e ) {
+				throw convert( e, "could not insert/select ids for bulk delete", idInsertSelect );
+			}
+
+			// Start performing the deletes
+			for ( String delete : deletes ) {
+				try {
+					try {
+						ps = session.getTransactionCoordinator()
+								.getJdbcCoordinator()
+								.getStatementPreparer()
+								.prepareStatement( delete, false );
+						handleAddedParametersOnDelete( ps, session );
+						ps.executeUpdate();
+					}
+					finally {
+						if ( ps != null ) {
+							ps.close();
+						}
+					}
+				}
+				catch (SQLException e) {
+					throw convert( e, "error performing bulk delete", delete );
+				}
+			}
+
+			return resultCount;
+
+		}
+		finally {
+			releaseFromUse( targetedPersister, session );
+		}
+	}
+
+	protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
+		return 0;
+	}
+
+	protected void handleAddedParametersOnDelete(PreparedStatement ps, SessionImplementor session) throws SQLException {
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedUpdateHandlerImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedUpdateHandlerImpl.java
new file mode 100644
index 0000000000..fd5cb8b4ae
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedUpdateHandlerImpl.java
@@ -0,0 +1,190 @@
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
+package org.hibernate.hql.spi;
+
+import java.sql.PreparedStatement;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.hql.internal.ast.HqlSqlWalker;
+import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
+import org.hibernate.hql.internal.ast.tree.FromElement;
+import org.hibernate.hql.internal.ast.tree.UpdateStatement;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.param.ParameterSpecification;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.sql.Update;
+
+/**
+* @author Steve Ebersole
+*/
+public class TableBasedUpdateHandlerImpl
+		extends AbstractTableBasedBulkIdHandler
+		implements MultiTableBulkIdStrategy.UpdateHandler {
+
+	private static final Logger log = Logger.getLogger( TableBasedUpdateHandlerImpl.class );
+
+	private final Queryable targetedPersister;
+
+	private final String idInsertSelect;
+	private final List<ParameterSpecification> idSelectParameterSpecifications;
+
+	private final String[] updates;
+	private final ParameterSpecification[][] assignmentParameterSpecifications;
+
+	@SuppressWarnings("unchecked")
+	TableBasedUpdateHandlerImpl(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		super( factory, walker );
+
+		UpdateStatement updateStatement = ( UpdateStatement ) walker.getAST();
+		FromElement fromElement = updateStatement.getFromClause().getFromElement();
+
+		this.targetedPersister = fromElement.getQueryable();
+		final String bulkTargetAlias = fromElement.getTableAlias();
+
+		final ProcessedWhereClause processedWhereClause = processWhereClause( updateStatement.getWhereClause() );
+		this.idSelectParameterSpecifications = processedWhereClause.getIdSelectParameterSpecifications();
+		this.idInsertSelect = generateIdInsertSelect( targetedPersister, bulkTargetAlias, processedWhereClause );
+		log.tracev( "Generated ID-INSERT-SELECT SQL (multi-table update) : {0}", idInsertSelect );
+
+		String[] tableNames = targetedPersister.getConstraintOrderedTableNameClosure();
+		String[][] columnNames = targetedPersister.getContraintOrderedTableKeyColumnClosure();
+		String idSubselect = generateIdSubselect( targetedPersister );
+
+		updates = new String[tableNames.length];
+		assignmentParameterSpecifications = new ParameterSpecification[tableNames.length][];
+		for ( int tableIndex = 0; tableIndex < tableNames.length; tableIndex++ ) {
+			boolean affected = false;
+			final List<ParameterSpecification> parameterList = new ArrayList<ParameterSpecification>();
+			final Update update = new Update( factory().getDialect() )
+					.setTableName( tableNames[tableIndex] )
+					.setWhere( "(" + StringHelper.join( ", ", columnNames[tableIndex] ) + ") IN (" + idSubselect + ")" );
+			if ( factory().getSettings().isCommentsEnabled() ) {
+				update.setComment( "bulk update" );
+			}
+			final List<AssignmentSpecification> assignmentSpecifications = walker.getAssignmentSpecifications();
+			for ( AssignmentSpecification assignmentSpecification : assignmentSpecifications ) {
+				if ( assignmentSpecification.affectsTable( tableNames[tableIndex] ) ) {
+					affected = true;
+					update.appendAssignmentFragment( assignmentSpecification.getSqlAssignmentFragment() );
+					if ( assignmentSpecification.getParameters() != null ) {
+						for ( int paramIndex = 0; paramIndex < assignmentSpecification.getParameters().length; paramIndex++ ) {
+							parameterList.add( assignmentSpecification.getParameters()[paramIndex] );
+						}
+					}
+				}
+			}
+			if ( affected ) {
+				updates[tableIndex] = update.toStatementString();
+				assignmentParameterSpecifications[tableIndex] = parameterList.toArray( new ParameterSpecification[parameterList.size()] );
+			}
+		}
+	}
+
+	@Override
+	public Queryable getTargetedQueryable() {
+		return targetedPersister;
+	}
+
+	@Override
+	public String[] getSqlStatements() {
+		return updates;
+	}
+
+	@Override
+	public int execute(SessionImplementor session, QueryParameters queryParameters) {
+		prepareForUse( targetedPersister, session );
+		try {
+			// First, save off the pertinent ids, as the return value
+			PreparedStatement ps = null;
+			int resultCount = 0;
+			try {
+				try {
+					ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( idInsertSelect, false );
+					int sum = 1;
+					sum += handlePrependedParametersOnIdSelection( ps, session, sum );
+					for ( ParameterSpecification parameterSpecification : idSelectParameterSpecifications ) {
+						sum += parameterSpecification.bind( ps, queryParameters, session, sum );
+					}
+					resultCount = ps.executeUpdate();
+				}
+				finally {
+					if ( ps != null ) {
+						ps.close();
+					}
+				}
+			}
+			catch( SQLException e ) {
+				throw convert( e, "could not insert/select ids for bulk update", idInsertSelect );
+			}
+
+			// Start performing the updates
+			for ( int i = 0; i < updates.length; i++ ) {
+				if ( updates[i] == null ) {
+					continue;
+				}
+				try {
+					try {
+						ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( updates[i], false );
+						if ( assignmentParameterSpecifications[i] != null ) {
+							int position = 1; // jdbc params are 1-based
+							for ( int x = 0; x < assignmentParameterSpecifications[i].length; x++ ) {
+								position += assignmentParameterSpecifications[i][x].bind( ps, queryParameters, session, position );
+							}
+							handleAddedParametersOnUpdate( ps, session, position );
+						}
+						ps.executeUpdate();
+					}
+					finally {
+						if ( ps != null ) {
+							ps.close();
+						}
+					}
+				}
+				catch( SQLException e ) {
+					throw convert( e, "error performing bulk update", updates[i] );
+				}
+			}
+
+			return resultCount;
+		}
+		finally {
+			releaseFromUse( targetedPersister, session );
+		}
+	}
+
+	protected int handlePrependedParametersOnIdSelection(PreparedStatement ps, SessionImplementor session, int pos) throws SQLException {
+		return 0;
+	}
+
+	protected void handleAddedParametersOnUpdate(PreparedStatement ps, SessionImplementor session, int position) throws SQLException {
+		//To change body of created methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
similarity index 50%
rename from hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java
rename to hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
index d745bac252..c60e164a77 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/AbstractStatementExecutor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/TemporaryTableBulkIdStrategy.java
@@ -1,310 +1,259 @@
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
-package org.hibernate.hql.internal.ast.exec;
+package org.hibernate.hql.spi;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLWarning;
 import java.sql.Statement;
-import java.util.Collections;
-import java.util.List;
 
-import antlr.RecognitionException;
-import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
-import org.hibernate.HibernateException;
-import org.hibernate.action.internal.BulkOperationCleanupAction;
+import org.hibernate.cfg.Mappings;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
+import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
-import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.internal.util.StringHelper;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.persister.entity.Queryable;
-import org.hibernate.sql.InsertSelect;
-import org.hibernate.sql.Select;
-import org.hibernate.sql.SelectFragment;
 
 /**
- * Implementation of AbstractStatementExecutor.
- *
  * @author Steve Ebersole
  */
-public abstract class AbstractStatementExecutor implements StatementExecutor {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       AbstractStatementExecutor.class.getName());
+public class TemporaryTableBulkIdStrategy implements MultiTableBulkIdStrategy {
+	public static final TemporaryTableBulkIdStrategy INSTANCE = new TemporaryTableBulkIdStrategy();
 
-	private final HqlSqlWalker walker;
-	private List idSelectParameterSpecifications = Collections.EMPTY_LIST;
+	private static final CoreMessageLogger log = Logger.getMessageLogger(
+			CoreMessageLogger.class,
+			TemporaryTableBulkIdStrategy.class.getName()
+	);
 
-    public AbstractStatementExecutor( HqlSqlWalker walker,
-                                      CoreMessageLogger log ) {
-		this.walker = walker;
+	@Override
+	public void prepare(Dialect dialect, JdbcConnectionAccess connectionAccess, Mappings mappings, Mapping mapping) {
+		// nothing to do
 	}
 
-	protected HqlSqlWalker getWalker() {
-		return walker;
+	@Override
+	public void release(Dialect dialect, JdbcConnectionAccess connectionAccess) {
+		// nothing to do
 	}
 
-	protected SessionFactoryImplementor getFactory() {
-		return walker.getSessionFactoryHelper().getFactory();
-	}
+	@Override
+	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedUpdateHandlerImpl( factory, walker ) {
+			@Override
+			protected void prepareForUse(Queryable persister, SessionImplementor session) {
+				createTempTable( persister, session );
+			}
 
-	protected List getIdSelectParameterSpecifications() {
-		return idSelectParameterSpecifications;
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				releaseTempTable( persister, session );
+			}
+		};
 	}
 
-	protected abstract Queryable[] getAffectedQueryables();
-
-	protected String generateIdInsertSelect(Queryable persister, String tableAlias, AST whereClause) {
-		Select select = new Select( getFactory().getDialect() );
-		SelectFragment selectFragment = new SelectFragment()
-				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
-		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
+	@Override
+	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker) {
+		return new TableBasedDeleteHandlerImpl( factory, walker ) {
+			@Override
+			protected void prepareForUse(Queryable persister, SessionImplementor session) {
+				createTempTable( persister, session );
+			}
 
-		String rootTableName = persister.getTableName();
-		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
-		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
+			@Override
+			protected void releaseFromUse(Queryable persister, SessionImplementor session) {
+				releaseTempTable( persister, session );
+			}
+		};
+	}
 
-		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
 
-		if ( whereJoinFragment == null ) {
-			whereJoinFragment = "";
+	protected void createTempTable(Queryable persister, SessionImplementor session) {
+		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
+		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
+		TemporaryTableCreationWork work = new TemporaryTableCreationWork( persister );
+		if ( shouldIsolateTemporaryTableDDL( session ) ) {
+			session.getTransactionCoordinator()
+					.getTransaction()
+					.createIsolationDelegate()
+					.delegateWork( work, session.getFactory().getSettings().isDataDefinitionInTransactionSupported() );
 		}
 		else {
-			whereJoinFragment = whereJoinFragment.trim();
-			if ( whereJoinFragment.startsWith( "and" ) ) {
-				whereJoinFragment = whereJoinFragment.substring( 4 );
-			}
+			final Connection connection = session.getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getLogicalConnection()
+					.getShareableConnectionProxy();
+			work.execute( connection );
+			session.getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getLogicalConnection()
+					.afterStatementExecution();
 		}
+	}
 
-		String userWhereClause = "";
-		if ( whereClause.getNumberOfChildren() != 0 ) {
-			// If a where clause was specified in the update/delete query, use it to limit the
-			// returned ids here...
+	protected void releaseTempTable(Queryable persister, SessionImplementor session) {
+		if ( session.getFactory().getDialect().dropTemporaryTableAfterUse() ) {
+			TemporaryTableDropWork work = new TemporaryTableDropWork( persister, session );
+			if ( shouldIsolateTemporaryTableDDL( session ) ) {
+				session.getTransactionCoordinator()
+						.getTransaction()
+						.createIsolationDelegate()
+						.delegateWork( work, session.getFactory().getSettings().isDataDefinitionInTransactionSupported() );
+			}
+			else {
+				final Connection connection = session.getTransactionCoordinator()
+						.getJdbcCoordinator()
+						.getLogicalConnection()
+						.getShareableConnectionProxy();
+				work.execute( connection );
+				session.getTransactionCoordinator()
+						.getJdbcCoordinator()
+						.getLogicalConnection()
+						.afterStatementExecution();
+			}
+		}
+		else {
+			// at the very least cleanup the data :)
+			PreparedStatement ps = null;
 			try {
-				SqlGenerator sqlGenerator = new SqlGenerator( getFactory() );
-				sqlGenerator.whereClause( whereClause );
-				userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
-				idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
+				final String sql = "delete from " + persister.getTemporaryIdTableName();
+				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
+				ps.executeUpdate();
 			}
-			catch ( RecognitionException e ) {
-				throw new HibernateException( "Unable to generate id select for DML operation", e );
+			catch( Throwable t ) {
+				log.unableToCleanupTemporaryIdTable(t);
 			}
-			if ( whereJoinFragment.length() > 0 ) {
-				whereJoinFragment += " and ";
+			finally {
+				if ( ps != null ) {
+					try {
+						ps.close();
+					}
+					catch( Throwable ignore ) {
+						// ignore
+					}
+				}
 			}
 		}
-
-		select.setWhereClause( whereJoinFragment + userWhereClause );
-
-		InsertSelect insert = new InsertSelect( getFactory().getDialect() );
-		if ( getFactory().getSettings().isCommentsEnabled() ) {
-			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
-		}
-		insert.setTableName( persister.getTemporaryIdTableName() );
-		insert.setSelect( select );
-		return insert.toStatementString();
 	}
 
-	protected String generateIdSubselect(Queryable persister) {
-		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
-			        " from " + persister.getTemporaryIdTableName();
+	@SuppressWarnings({ "UnnecessaryUnboxing" })
+	protected boolean shouldIsolateTemporaryTableDDL(SessionImplementor session) {
+		Boolean dialectVote = session.getFactory().getDialect().performTemporaryTableDDLInIsolation();
+		if ( dialectVote != null ) {
+			return dialectVote.booleanValue();
+		}
+		return session.getFactory().getSettings().isDataDefinitionImplicitCommit();
 	}
 
 	private static class TemporaryTableCreationWork extends AbstractWork {
 		private final Queryable persister;
 
 		private TemporaryTableCreationWork(Queryable persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public void execute(Connection connection) {
 			try {
 				Statement statement = connection.createStatement();
 				try {
 					statement.executeUpdate( persister.getTemporaryIdTableDDL() );
 					persister.getFactory()
 							.getServiceRegistry()
 							.getService( JdbcServices.class )
 							.getSqlExceptionHelper()
 							.handleAndClearWarnings( statement, CREATION_WARNING_HANDLER );
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
-				LOG.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
+				log.debug( "unable to create temporary id table [" + e.getMessage() + "]" );
 			}
 		}
 	}
-	protected void createTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
-		// Don't really know all the codes required to adequately decipher returned jdbc exceptions here.
-		// simply allow the failure to be eaten and the subsequent insert-selects/deletes should fail
-		TemporaryTableCreationWork work = new TemporaryTableCreationWork( persister );
-		if ( shouldIsolateTemporaryTableDDL() ) {
-			session.getTransactionCoordinator()
-					.getTransaction()
-					.createIsolationDelegate()
-					.delegateWork( work, getFactory().getSettings().isDataDefinitionInTransactionSupported() );
-		}
-		else {
-			final Connection connection = session.getTransactionCoordinator()
-					.getJdbcCoordinator()
-					.getLogicalConnection()
-					.getShareableConnectionProxy();
-			work.execute( connection );
-			session.getTransactionCoordinator()
-					.getJdbcCoordinator()
-					.getLogicalConnection()
-					.afterStatementExecution();
-		}
-	}
 
 	private static SqlExceptionHelper.WarningHandler CREATION_WARNING_HANDLER = new SqlExceptionHelper.WarningHandlerLoggingSupport() {
 		public boolean doProcess() {
-			return LOG.isDebugEnabled();
+			return log.isDebugEnabled();
 		}
 
 		public void prepare(SQLWarning warning) {
-			LOG.warningsCreatingTempTable( warning );
+			log.warningsCreatingTempTable( warning );
 		}
 
 		@Override
 		protected void logWarning(String description, String message) {
-			LOG.debug( description );
-			LOG.debug( message );
+			log.debug( description );
+			log.debug( message );
 		}
 	};
 
 	private static class TemporaryTableDropWork extends AbstractWork {
 		private final Queryable persister;
 		private final SessionImplementor session;
 
 		private TemporaryTableDropWork(Queryable persister, SessionImplementor session) {
 			this.persister = persister;
 			this.session = session;
 		}
 
 		@Override
 		public void execute(Connection connection) {
 			final String command = session.getFactory().getDialect().getDropTemporaryTableString()
 					+ ' ' + persister.getTemporaryIdTableName();
 			try {
 				Statement statement = connection.createStatement();
 				try {
 					statement = connection.createStatement();
 					statement.executeUpdate( command );
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
-				LOG.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
+				log.warn( "unable to drop temporary id table after use [" + e.getMessage() + "]" );
 			}
 		}
 	}
 
-	protected void dropTemporaryTableIfNecessary(final Queryable persister, final SessionImplementor session) {
-		if ( getFactory().getDialect().dropTemporaryTableAfterUse() ) {
-			TemporaryTableDropWork work = new TemporaryTableDropWork( persister, session );
-			if ( shouldIsolateTemporaryTableDDL() ) {
-				session.getTransactionCoordinator()
-						.getTransaction()
-						.createIsolationDelegate()
-						.delegateWork( work, getFactory().getSettings().isDataDefinitionInTransactionSupported() );
-			}
-			else {
-				final Connection connection = session.getTransactionCoordinator()
-						.getJdbcCoordinator()
-						.getLogicalConnection()
-						.getShareableConnectionProxy();
-				work.execute( connection );
-				session.getTransactionCoordinator()
-						.getJdbcCoordinator()
-						.getLogicalConnection()
-						.afterStatementExecution();
-			}
-		}
-		else {
-			// at the very least cleanup the data :)
-			PreparedStatement ps = null;
-			try {
-				final String sql = "delete from " + persister.getTemporaryIdTableName();
-				ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( sql, false );
-				ps.executeUpdate();
-			}
-			catch( Throwable t ) {
-                LOG.unableToCleanupTemporaryIdTable(t);
-			}
-			finally {
-				if ( ps != null ) {
-					try {
-						ps.close();
-					}
-					catch( Throwable ignore ) {
-						// ignore
-					}
-				}
-			}
-		}
-	}
-
-	protected void coordinateSharedCacheCleanup(SessionImplementor session) {
-		BulkOperationCleanupAction action = new BulkOperationCleanupAction( session, getAffectedQueryables() );
-
-		if ( session.isEventSource() ) {
-			( ( EventSource ) session ).getActionQueue().addAction( action );
-		}
-		else {
-			action.getAfterTransactionCompletionProcess().doAfterTransactionCompletion( true, session );
-		}
-	}
-
-	@SuppressWarnings({ "UnnecessaryUnboxing" })
-	protected boolean shouldIsolateTemporaryTableDDL() {
-		Boolean dialectVote = getFactory().getDialect().performTemporaryTableDDLInIsolation();
-        if (dialectVote != null) return dialectVote.booleanValue();
-        return getFactory().getSettings().isDataDefinitionImplicitCommit();
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index 8b51b21036..d0e75b1dc1 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,371 +1,382 @@
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
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.List;
+import java.util.UUID;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
 import org.hibernate.StoredProcedureCall;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
+import org.hibernate.id.uuid.StandardRandomStrategy;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract,
 													 SessionImplementor, TransactionContext {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private boolean closed = false;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory, String tenantIdentifier) {
 		this.factory = factory;
 		this.tenantIdentifier = tenantIdentifier;
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			if ( tenantIdentifier != null ) {
 				throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 			}
 		}
 		else {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "SessionFactory configured for multi-tenancy, but no tenant identifier specified" );
 			}
 		}
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
 
 	@Override
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
 
 	@Override
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
 			if ( nqd.getLockOptions() != null ) {
 				query.setLockOptions( nqd.getLockOptions() );
 			}
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() );
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 					parameterMetadata
 			);
 			query.setComment( "named native SQL query " + queryName );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	@Override
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
 
 	@SuppressWarnings("UnnecessaryUnboxing")
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		query.setReadOnly( nqd.isReadOnly() );
 
 		if ( nqd.getTimeout() != null ) {
 			query.setTimeout( nqd.getTimeout().intValue() );
 		}
 		if ( nqd.getFetchSize() != null ) {
 			query.setFetchSize( nqd.getFetchSize().intValue() );
 		}
 		if ( nqd.getCacheMode() != null ) {
 			query.setCacheMode( nqd.getCacheMode() );
 		}
 		if ( nqd.getComment() != null ) {
 			query.setComment( nqd.getComment() );
 		}
 		if ( nqd.getFirstResult() != null ) {
 			query.setFirstResult( nqd.getFirstResult() );
 		}
 		if ( nqd.getMaxResults() != null ) {
 			query.setMaxResults( nqd.getMaxResults() );
 		}
 		if ( nqd.getFlushMode() != null ) {
 			query.setFlushMode( nqd.getFlushMode() );
 		}
 	}
 
 	@Override
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
 
 	@Override
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
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public StoredProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName );
 //		call.setComment( "Dynamic stored procedure call" );
 		return call;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public StoredProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName, resultClasses );
 //		call.setComment( "Dynamic stored procedure call" );
 		return call;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public StoredProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName, resultSetMappings );
 //		call.setComment( "Dynamic stored procedure call" );
 		return call;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister, getTenantIdentifier() );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return new CacheKey( id, type, entityOrRoleName, getTenantIdentifier(), getFactory() );
 	}
 
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
+	private UUID sessionIdentifier;
+
+	public UUID getSessionIdentifier() {
+		if ( sessionIdentifier == null ) {
+			sessionIdentifier = StandardRandomStrategy.INSTANCE.generateUUID( this );
+		}
+		return sessionIdentifier;
+	}
+
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			return connectionProvider.getConnection( tenantIdentifier );
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			connectionProvider.releaseConnection( tenantIdentifier, connection );
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index a4bad4c387..419d2a0c58 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1781 +1,1822 @@
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
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
+import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
+import org.hibernate.MultiTenancyStrategy;
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
+import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
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
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
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
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
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
 			
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
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
 			CollectionPersister persister = serviceRegistry.getService( PersisterFactory.class ).createCollectionPersister(
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
 
+		settings.getMultiTableBulkIdStrategy().prepare(
+				dialect,
+				buildLocalConnectionAccess(),
+				cfg.createMappings(),
+				cfg.buildMapping(),
+				properties
+		);
+
+
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
 					failingQueries.append( entry.getKey() ).append( sep );
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
 
+	private JdbcConnectionAccess buildLocalConnectionAccess() {
+		return new JdbcConnectionAccess() {
+			@Override
+			public Connection obtainConnection() throws SQLException {
+				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
+						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
+						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
+			}
+
+			@Override
+			public void releaseConnection(Connection connection) throws SQLException {
+				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
+					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
+				}
+				else {
+					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
+				}
+			}
+
+			@Override
+			public boolean supportsAggressiveRelease() {
+				return false;
+			}
+		};
+	}
+
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
 				metadata.getServiceRegistry()
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
 
 	public String getImportedClassName(String className) {
 		String result = imports.get(className);
 		if (result==null) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				return className;
 			}
 			catch (ClassLoadingException cnfe) {
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
 
+		settings.getMultiTableBulkIdStrategy().release( dialect, buildLocalConnectionAccess() );
+
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
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return customEntityDirtinessStrategy;
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
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
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
index 44dfc49d54..569b08e30d 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Column.java
@@ -1,367 +1,368 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.Template;
 
 /**
  * A column of a relational database table
  * @author Gavin King
  */
 public class Column implements Selectable, Serializable, Cloneable {
 
 	public static final int DEFAULT_LENGTH = 255;
 	public static final int DEFAULT_PRECISION = 19;
 	public static final int DEFAULT_SCALE = 2;
 
 	private int length=DEFAULT_LENGTH;
 	private int precision=DEFAULT_PRECISION;
 	private int scale=DEFAULT_SCALE;
 	private Value value;
 	private int typeIndex = 0;
 	private String name;
 	private boolean nullable=true;
 	private boolean unique=false;
 	private String sqlType;
 	private Integer sqlTypeCode;
 	private boolean quoted=false;
 	int uniqueInteger;
 	private String checkConstraint;
 	private String comment;
 	private String defaultValue;
 	private String customWrite;
 	private String customRead;
 
 	public Column() {
 	}
 
 	public Column(String columnName) {
 		setName(columnName);
 	}
 
 	public int getLength() {
 		return length;
 	}
 	public void setLength(int length) {
 		this.length = length;
 	}
 	public Value getValue() {
 		return value;
 	}
 	public void setValue(Value value) {
 		this.value= value;
 	}
 	public String getName() {
 		return name;
 	}
 	public void setName(String name) {
 		if (
 			name.charAt(0)=='`' ||
 			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
 		) {
 			quoted=true;
 			this.name=name.substring( 1, name.length()-1 );
 		}
 		else {
 			this.name = name;
 		}
 	}
 
 	/** returns quoted name as it would be in the mapping file. */
 	public String getQuotedName() {
 		return quoted ?
 				"`" + name + "`" :
 				name;
 	}
 
 	public String getQuotedName(Dialect d) {
 		return quoted ?
 			d.openQuote() + name + d.closeQuote() :
 			name;
 	}
 	
 	/**
 	 * For any column name, generate an alias that is unique
 	 * to that column name, and also 10 characters or less
 	 * in length.
 	 */
 	public String getAlias(Dialect dialect) {
 		String alias = name;
 		String unique = Integer.toString(uniqueInteger) + '_';
 		int lastLetter = StringHelper.lastIndexOfLetter(name);
 		if ( lastLetter == -1 ) {
 			alias = "column";
 		}
 		else if ( lastLetter < name.length()-1 ) {
 			alias = name.substring(0, lastLetter+1);
 		}
 		if ( alias.length() > dialect.getMaxAliasLength() ) {
 			alias = alias.substring( 0, dialect.getMaxAliasLength() - unique.length() );
 		}
 		boolean useRawName = name.equals(alias) && 
 			!quoted && 
 			!name.toLowerCase().equals("rowid");
 		if ( useRawName ) {
 			return alias;
 		}
 		else {
 			return alias + unique;
 		}
 	}
 	
 	/**
 	 * Generate a column alias that is unique across multiple tables
 	 */
 	public String getAlias(Dialect dialect, Table table) {
 		return getAlias(dialect) + table.getUniqueInteger() + '_';
 	}
 
 	public boolean isNullable() {
 		return nullable;
 	}
 
 	public void setNullable(boolean nullable) {
 		this.nullable=nullable;
 	}
 
 	public int getTypeIndex() {
 		return typeIndex;
 	}
 	public void setTypeIndex(int typeIndex) {
 		this.typeIndex = typeIndex;
 	}
 
 	public boolean isUnique() {
 		return unique;
 	}
 
 	//used also for generation of FK names!
 	public int hashCode() {
 		return isQuoted() ?
 			name.hashCode() :
 			name.toLowerCase().hashCode();
 	}
 
 	public boolean equals(Object object) {
 		return object instanceof Column && equals( (Column) object );
 	}
 
 	public boolean equals(Column column) {
 		if (null == column) return false;
 		if (this == column) return true;
 
 		return isQuoted() ? 
 			name.equals(column.name) :
 			name.equalsIgnoreCase(column.name);
 	}
 
     public int getSqlTypeCode(Mapping mapping) throws MappingException {
         org.hibernate.type.Type type = getValue().getType();
         try {
             int sqlTypeCode = type.sqlTypes( mapping )[getTypeIndex()];
             if ( getSqlTypeCode() != null && getSqlTypeCode() != sqlTypeCode ) {
                 throw new MappingException( "SQLType code's does not match. mapped as " + sqlTypeCode + " but is " + getSqlTypeCode() );
             }
             return sqlTypeCode;
         }
         catch ( Exception e ) {
             throw new MappingException(
                     "Could not determine type for column " +
                             name +
                             " of type " +
                             type.getClass().getName() +
                             ": " +
                             e.getClass().getName(),
                     e
             );
         }
     }
 
     /**
      * Returns the underlying columns sqltypecode.
      * If null, it is because the sqltype code is unknown.
      *
      * Use #getSqlTypeCode(Mapping) to retreive the sqltypecode used
      * for the columns associated Value/Type.
      *
      * @return sqlTypeCode if it is set, otherwise null.
      */
     public Integer getSqlTypeCode() {
         return sqlTypeCode;
     }
 
     public void setSqlTypeCode(Integer typeCode) {
         sqlTypeCode=typeCode;
     }
 
     public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
         if ( sqlType == null ) {
             sqlType = dialect.getTypeName( getSqlTypeCode( mapping ), getLength(), getPrecision(), getScale() );
         }
         return sqlType;
     }
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public void setSqlType(String sqlType) {
 		this.sqlType = sqlType;
 	}
 
 	public void setUnique(boolean unique) {
 		this.unique = unique;
 	}
 
 	public boolean isQuoted() {
 		return quoted;
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getName() + ')';
 	}
 
 	public String getCheckConstraint() {
 		return checkConstraint;
 	}
 
 	public void setCheckConstraint(String checkConstraint) {
 		this.checkConstraint = checkConstraint;
 	}
 
 	public boolean hasCheckConstraint() {
 		return checkConstraint!=null;
 	}
 
 	public String getTemplate(Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		return hasCustomRead()
 				? Template.renderWhereStringTemplate( customRead, dialect, functionRegistry )
 				: Template.TEMPLATE + '.' + getQuotedName( dialect );
 	}
 
 	public boolean hasCustomRead() {
 		return ( customRead != null && customRead.length() > 0 );
 	}
 
 	public String getReadExpr(Dialect dialect) {
 		return hasCustomRead() ? customRead : getQuotedName( dialect );
 	}
 	
 	public String getWriteExpr() {
 		return ( customWrite != null && customWrite.length() > 0 ) ? customWrite : "?";
 	}
 	
 	public boolean isFormula() {
 		return false;
 	}
 
 	public String getText(Dialect d) {
 		return getQuotedName(d);
 	}
 	public String getText() {
 		return getName();
 	}
 	
 	public int getPrecision() {
 		return precision;
 	}
 	public void setPrecision(int scale) {
 		this.precision = scale;
 	}
 
 	public int getScale() {
 		return scale;
 	}
 	public void setScale(int scale) {
 		this.scale = scale;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public String getDefaultValue() {
 		return defaultValue;
 	}
 
 	public void setDefaultValue(String defaultValue) {
 		this.defaultValue = defaultValue;
 	}
 
 	public String getCustomWrite() {
 		return customWrite;
 	}
 
 	public void setCustomWrite(String customWrite) {
 		this.customWrite = customWrite;
 	}
 
 	public String getCustomRead() {
 		return customRead;
 	}
 
 	public void setCustomRead(String customRead) {
 		this.customRead = customRead;
 	}
 
 	public String getCanonicalName() {
 		return quoted ? name : name.toLowerCase();
 	}
 
 	/**
 	 * Shallow copy, the value is not copied
 	 */
-	protected Object clone() {
+	@Override
+	public Column clone() {
 		Column copy = new Column();
 		copy.setLength( length );
 		copy.setScale( scale );
 		copy.setValue( value );
 		copy.setTypeIndex( typeIndex );
 		copy.setName( getQuotedName() );
 		copy.setNullable( nullable );
 		copy.setPrecision( precision );
 		copy.setUnique( unique );
 		copy.setSqlType( sqlType );
 		copy.setSqlTypeCode( sqlTypeCode );
 		copy.uniqueInteger = uniqueInteger; //usually useless
 		copy.setCheckConstraint( checkConstraint );
 		copy.setComment( comment );
 		copy.setDefaultValue( defaultValue );
 		copy.setCustomRead( customRead );
 		copy.setCustomWrite( customWrite );
 		return copy;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
index 851f53e724..e8a299dd48 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
@@ -1,869 +1,869 @@
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
 public abstract class PersistentClass implements Serializable, Filterable, MetaAttributable {
 
 	private static final Alias PK_ALIAS = new Alias(15, "PK");
 
 	public static final String NULL_DISCRIMINATOR_MAPPING = "null";
 	public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";
 
 	private String entityName;
 
 	private String className;
 	private String proxyInterfaceName;
 	
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
 
 	private String temporaryIdTableName;
 	private String temporaryIdTableDDL;
 
 	private java.util.Map tuplizerImpls;
 
 	protected int optimisticLockMode;
 	private MappedSuperclass superMappedSuperclass;
 	private Component declaredIdentifierMapper;
 
 	public String getClassName() {
 		return className;
 	}
 
 	public void setClassName(String className) {
 		this.className = className==null ? null : className.intern();
 	}
 
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public Class getMappedClass() throws MappingException {
 		if (className==null) return null;
 		try {
 			return ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("entity class not found: " + className, cnfe);
 		}
 	}
 
 	public Class getProxyInterface() {
 		if (proxyInterfaceName==null) return null;
 		try {
 			return ReflectHelper.classForName( proxyInterfaceName );
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
 		while ( iterator.hasNext() ) {
 			Property prop = (Property) iterator.next();
 			if ( prop.getName().equals( StringHelper.root(propertyName) ) ) {
 				return prop;
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
 
 	abstract public int getOptimisticLockMode();
 
 	public void setOptimisticLockMode(int optimisticLockMode) {
 		this.optimisticLockMode = optimisticLockMode;
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
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(name);
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
 
 	public void prepareTemporaryTables(Mapping mapping, Dialect dialect) {
+		temporaryIdTableName = dialect.generateTemporaryTableName( getTable().getName() );
 		if ( dialect.supportsTemporaryTables() ) {
-			temporaryIdTableName = dialect.generateTemporaryTableName( getTable().getName() );
 			Table table = new Table();
 			table.setName( temporaryIdTableName );
 			Iterator itr = getTable().getPrimaryKey().getColumnIterator();
 			while( itr.hasNext() ) {
 				Column column = (Column) itr.next();
-				table.addColumn( (Column) column.clone()  );
+				table.addColumn( column.clone()  );
 			}
 			temporaryIdTableDDL = table.sqlTemporaryTableCreateString( dialect, mapping );
 		}
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
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
\ No newline at end of file
