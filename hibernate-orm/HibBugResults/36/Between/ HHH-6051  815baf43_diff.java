diff --git a/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java b/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
index a3c7ea42d5..d946835a6e 100644
--- a/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
+++ b/hibernate-c3p0/src/main/java/org/hibernate/service/jdbc/connections/internal/C3P0ConnectionProvider.java
@@ -1,249 +1,249 @@
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
 package org.hibernate.service.jdbc.connections.internal;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.Properties;
 import javax.sql.DataSource;
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
+import org.hibernate.service.UnknownUnwrapTypeException;
 import org.jboss.logging.Logger;
 import com.mchange.v2.c3p0.DataSources;
 
 /**
  * A connection provider that uses a C3P0 connection pool. Hibernate will use this by
  * default if the <tt>hibernate.c3p0.*</tt> properties are set.
  *
  * @author various people
  * @see ConnectionProvider
  */
 public class C3P0ConnectionProvider implements ConnectionProvider {
 
     private static final C3P0Logger LOG = Logger.getMessageLogger(C3P0Logger.class, C3P0ConnectionProvider.class.getName());
 
 	//swaldman 2006-08-28: define c3p0-style configuration parameters for properties with
 	//                     hibernate-specific overrides to detect and warn about conflicting
 	//                     declarations
 	private final static String C3P0_STYLE_MIN_POOL_SIZE = "c3p0.minPoolSize";
 	private final static String C3P0_STYLE_MAX_POOL_SIZE = "c3p0.maxPoolSize";
 	private final static String C3P0_STYLE_MAX_IDLE_TIME = "c3p0.maxIdleTime";
 	private final static String C3P0_STYLE_MAX_STATEMENTS = "c3p0.maxStatements";
 	private final static String C3P0_STYLE_ACQUIRE_INCREMENT = "c3p0.acquireIncrement";
 	private final static String C3P0_STYLE_IDLE_CONNECTION_TEST_PERIOD = "c3p0.idleConnectionTestPeriod";
     // private final static String C3P0_STYLE_TEST_CONNECTION_ON_CHECKOUT = "c3p0.testConnectionOnCheckout";
 
 	//swaldman 2006-08-28: define c3p0-style configuration parameters for initialPoolSize, which
 	//                     hibernate sensibly lets default to minPoolSize, but we'll let users
 	//                     override it with the c3p0-style property if they want.
 	private final static String C3P0_STYLE_INITIAL_POOL_SIZE = "c3p0.initialPoolSize";
 
 	private DataSource ds;
 	private Integer isolation;
 	private boolean autocommit;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection getConnection() throws SQLException {
 		final Connection c = ds.getConnection();
 		if ( isolation != null ) {
 			c.setTransactionIsolation( isolation.intValue() );
 		}
 		if ( c.getAutoCommit() != autocommit ) {
 			c.setAutoCommit( autocommit );
 		}
 		return c;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void closeConnection(Connection conn) throws SQLException {
 		conn.close();
 	}
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				C3P0ConnectionProvider.class.isAssignableFrom( unwrapType ) ||
 				DataSource.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				C3P0ConnectionProvider.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else if ( DataSource.class.isAssignableFrom( unwrapType ) ) {
 			return (T) ds;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void configure(Properties props) throws HibernateException {
 		String jdbcDriverClass = props.getProperty( Environment.DRIVER );
 		String jdbcUrl = props.getProperty( Environment.URL );
 		Properties connectionProps = ConnectionProviderInitiator.getConnectionProperties( props );
 
         LOG.c3p0UsingDriver(jdbcDriverClass, jdbcUrl);
         LOG.connectionProperties(ConfigurationHelper.maskOut(connectionProps, "password"));
 
 		autocommit = ConfigurationHelper.getBoolean( Environment.AUTOCOMMIT, props );
         LOG.autoCommitMode(autocommit);
 
         if (jdbcDriverClass == null) LOG.jdbcDriverNotSpecified(Environment.DRIVER);
 		else {
 			try {
 				Class.forName( jdbcDriverClass );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				try {
 					ReflectHelper.classForName( jdbcDriverClass );
 				}
 				catch ( ClassNotFoundException e ) {
                     String msg = LOG.jdbcDriverNotFound(jdbcDriverClass);
                     LOG.error(msg, e);
 					throw new HibernateException( msg, e );
 				}
 			}
 		}
 
 		try {
 
 			//swaldman 2004-02-07: modify to allow null values to signify fall through to c3p0 PoolConfig defaults
 			Integer minPoolSize = ConfigurationHelper.getInteger( Environment.C3P0_MIN_SIZE, props );
 			Integer maxPoolSize = ConfigurationHelper.getInteger( Environment.C3P0_MAX_SIZE, props );
 			Integer maxIdleTime = ConfigurationHelper.getInteger( Environment.C3P0_TIMEOUT, props );
 			Integer maxStatements = ConfigurationHelper.getInteger( Environment.C3P0_MAX_STATEMENTS, props );
 			Integer acquireIncrement = ConfigurationHelper.getInteger( Environment.C3P0_ACQUIRE_INCREMENT, props );
 			Integer idleTestPeriod = ConfigurationHelper.getInteger( Environment.C3P0_IDLE_TEST_PERIOD, props );
 
 			Properties c3props = new Properties();
 
 			// turn hibernate.c3p0.* into c3p0.*, so c3p0
 			// gets a chance to see all hibernate.c3p0.*
 			for ( Iterator ii = props.keySet().iterator(); ii.hasNext(); ) {
 				String key = ( String ) ii.next();
 				if ( key.startsWith( "hibernate.c3p0." ) ) {
 					String newKey = key.substring( 10 );
 					if ( props.containsKey( newKey ) ) {
 						warnPropertyConflict( key, newKey );
 					}
 					c3props.put( newKey, props.get( key ) );
 				}
 			}
 
 			setOverwriteProperty( Environment.C3P0_MIN_SIZE, C3P0_STYLE_MIN_POOL_SIZE, props, c3props, minPoolSize );
 			setOverwriteProperty( Environment.C3P0_MAX_SIZE, C3P0_STYLE_MAX_POOL_SIZE, props, c3props, maxPoolSize );
 			setOverwriteProperty( Environment.C3P0_TIMEOUT, C3P0_STYLE_MAX_IDLE_TIME, props, c3props, maxIdleTime );
 			setOverwriteProperty(
 					Environment.C3P0_MAX_STATEMENTS, C3P0_STYLE_MAX_STATEMENTS, props, c3props, maxStatements
 			);
 			setOverwriteProperty(
 					Environment.C3P0_ACQUIRE_INCREMENT, C3P0_STYLE_ACQUIRE_INCREMENT, props, c3props, acquireIncrement
 			);
 			setOverwriteProperty(
 					Environment.C3P0_IDLE_TEST_PERIOD, C3P0_STYLE_IDLE_CONNECTION_TEST_PERIOD, props, c3props, idleTestPeriod
 			);
 
 			// revert to traditional hibernate behavior of setting initialPoolSize to minPoolSize
 			// unless otherwise specified with a c3p0.*-style parameter.
 			Integer initialPoolSize = ConfigurationHelper.getInteger( C3P0_STYLE_INITIAL_POOL_SIZE, props );
 			if ( initialPoolSize == null && minPoolSize != null ) {
 				c3props.put( C3P0_STYLE_INITIAL_POOL_SIZE, String.valueOf( minPoolSize ).trim() );
 			}
 
 			/*DataSource unpooled = DataSources.unpooledDataSource(
 				jdbcUrl, props.getProperty(Environment.USER), props.getProperty(Environment.PASS)
 			);*/
 			DataSource unpooled = DataSources.unpooledDataSource( jdbcUrl, connectionProps );
 
 			Properties allProps = ( Properties ) props.clone();
 			allProps.putAll( c3props );
 
 			ds = DataSources.pooledDataSource( unpooled, allProps );
 		}
 		catch ( Exception e ) {
             LOG.error(LOG.unableToInstantiateC3p0ConnectionPool(), e);
             throw new HibernateException(LOG.unableToInstantiateC3p0ConnectionPool(), e);
 		}
 
 		String i = props.getProperty( Environment.ISOLATION );
         if (i == null) isolation = null;
 		else {
 			isolation = new Integer( i );
             LOG.jdbcIsolationLevel(Environment.isolationLevelToString(isolation.intValue()));
 		}
 
 	}
 
     /**
 	 *
 	 */
 	public void close() {
 		try {
 			DataSources.destroy( ds );
 		}
 		catch ( SQLException sqle ) {
             LOG.unableToDestroyC3p0ConnectionPool(sqle);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean supportsAggressiveRelease() {
 		return false;
 	}
 
 	private void setOverwriteProperty(String hibernateStyleKey, String c3p0StyleKey, Properties hibp, Properties c3p, Integer value) {
 		if ( value != null ) {
 			c3p.put( c3p0StyleKey, String.valueOf( value ).trim() );
 			if ( hibp.getProperty( c3p0StyleKey ) != null ) {
 				warnPropertyConflict( hibernateStyleKey, c3p0StyleKey );
 			}
 			String longC3p0StyleKey = "hibernate." + c3p0StyleKey;
 			if ( hibp.getProperty( longC3p0StyleKey ) != null ) {
 				warnPropertyConflict( hibernateStyleKey, longC3p0StyleKey );
 			}
 		}
 	}
 
 	private void warnPropertyConflict(String hibernateStyle, String c3p0Style) {
         LOG.bothHibernateAndC3p0StylesSet(hibernateStyle, c3p0Style, hibernateStyle, c3p0Style);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 9f1b1b2afa..d89accc9e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1163 +1,1163 @@
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
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
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
 import java.util.ResourceBundle;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
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
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.cfg.beanvalidation.BeanValidationActivator;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.event.AutoFlushEventListener;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.DirtyCheckEventListener;
 import org.hibernate.event.EventListeners;
 import org.hibernate.event.EvictEventListener;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.InitializeCollectionEventListener;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.LockEventListener;
 import org.hibernate.event.MergeEventListener;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PostCollectionUpdateEventListener;
 import org.hibernate.event.PostDeleteEventListener;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.event.PostUpdateEventListener;
 import org.hibernate.event.PreCollectionRecreateEventListener;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionUpdateEventListener;
 import org.hibernate.event.PreDeleteEventListener;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.event.PreLoadEventListener;
 import org.hibernate.event.PreUpdateEventListener;
 import org.hibernate.event.RefreshEventListener;
 import org.hibernate.event.ReplicateEventListener;
 import org.hibernate.event.SaveOrUpdateEventListener;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.impl.SessionFactoryImpl;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
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
 import org.hibernate.secure.JACCConfiguration;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistry;
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
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
 public class Configuration implements Serializable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Configuration.class.getName());
 
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
 
 	private EventListeners eventListeners;
 
 	protected final SettingsFactory settingsFactory;
 
 	private transient Mapping mapping = buildMapping();
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory;
 
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
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private List<CacheHolder> caches;
 	private boolean inSecondPass = false;
 	private boolean isDefaultProcessed = false;
 	private boolean isValidatorNotPresentLogged;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private boolean specjProprietarySyntaxEnabled;
 
 
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
 		eventListeners = new EventListeners();
 
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
         LOG.readingMappingsFromFile(xmlFile.getPath());
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
             LOG.unableToDeserializeCache(cachedFile.getPath(), e);
 		}
 		catch ( FileNotFoundException e ) {
             LOG.cachedFileNotFound(cachedFile.getPath(), e);
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
         LOG.readingMappingsFromFile(xmlFile.getPath());
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
             LOG.debugf("Writing cache file for: %s to: %s", xmlFile, cachedFile);
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
         } catch (Exception e) {
             LOG.unableToWriteCachedFile(cachedFile.getPath(), e.getMessage());
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
 
         LOG.readingCachedMappings(cachedFile);
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
         LOG.debugf("Mapping XML:\n%s", xml);
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
 
         LOG.debugf("Reading mapping document from URL : %s", urlExternalForm);
 
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
                 LOG.trace("Was unable to close input stream");
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
         LOG.debugf("Mapping Document:\n%s", doc);
 
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
         LOG.readingMappingsFromResource(resourceName);
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
         LOG.readingMappingsFromResource(resourceName);
 		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
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
         LOG.readingMappingsFromResource(mappingResourceName);
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
         LOG.mappingPackage(packageName);
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
             LOG.unableToParseMetadata(packageName);
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
         LOG.searchingForMappingDocuments(jar.getName());
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
                     LOG.foundMappingDocument(ze.getName());
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
                 LOG.unableToCloseJar(ioe.getMessage());
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
 	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
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
 
 				if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 					Iterator subIter = table.getUniqueKeyIterator();
 					while ( subIter.hasNext() ) {
 						UniqueKey uk = (UniqueKey) subIter.next();
 						String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 						if (constraintString != null) script.add( constraintString );
 					}
 				}
 
 
 				Iterator subIter = table.getIndexIterator();
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
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 0a222063e4..fe355cc039 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,325 +1,325 @@
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
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
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
         LOG.cacheRegionFactory(regionFactoryClassName);
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
         LOG.queryTranslator(className);
 		try {
 			return (QueryTranslatorFactory) ReflectHelper.classForName(className).newInstance();
 		}
 		catch (Exception cnfe) {
 			throw new HibernateException("could not instantiate QueryTranslatorFactory: " + className, cnfe);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
index d3cea25517..d06b800ec4 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
@@ -1,258 +1,261 @@
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
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
+import org.hibernate.SessionFactoryObserver;
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
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.ServiceRegistry;
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
 
 	public ServiceRegistry getServiceRegistry();
+
+	public void addObserver(SessionFactoryObserver observer);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java
index 65dbc8676c..588337f749 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java
@@ -1,71 +1,71 @@
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
 package org.hibernate.engine.jdbc.batch.internal;
 
 import java.util.Map;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceException;
 
 /**
  * Initiator for the {@link BatchBuilder} service
  *
  * @author Steve Ebersole
  */
 public class BatchBuilderInitiator implements BasicServiceInitiator<BatchBuilder> {
 	public static final BatchBuilderInitiator INSTANCE = new BatchBuilderInitiator();
 	public static final String BUILDER = "hibernate.jdbc.batch.builder";
 
 	@Override
 	public Class<BatchBuilder> getServiceInitiated() {
 		return BatchBuilder.class;
 	}
 
 	@Override
 	public BatchBuilder initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		final Object builder = configurationValues.get( BUILDER );
 		if ( builder == null ) {
 			return new BatchBuilderImpl(
 					ConfigurationHelper.getInt( Environment.STATEMENT_BATCH_SIZE, configurationValues, 1 )
 			);
 		}
 
 		if ( BatchBuilder.class.isInstance( builder ) ) {
 			return (BatchBuilder) builder;
 		}
 
 		final String builderClassName = builder.toString();
 		try {
 			return (BatchBuilder) registry.getService( ClassLoaderService.class ).classForName( builderClassName ).newInstance();
 		}
 		catch (Exception e) {
 			throw new ServiceException( "Could not build explicit BatchBuilder [" + builderClassName + "]", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/BatchBuilder.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/BatchBuilder.java
index 4715c13faa..8af7370ee1 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/BatchBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/spi/BatchBuilder.java
@@ -1,45 +1,45 @@
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
 package org.hibernate.engine.jdbc.batch.spi;
 
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.service.spi.Manageable;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * A builder for {@link Batch} instances
  *
  * @author Steve Ebersole
  */
 public interface BatchBuilder extends Service, Manageable {
 	/**
 	 * Build a batch.
 	 *
 	 * @param key Value to uniquely identify a batch
 	 * @param jdbcCoordinator The JDBC coordinator with which to coordinate efforts
 	 *
 	 * @return The built batch
 	 */
 	public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java
index 65aa06a6b5..8c824c2099 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java
@@ -1,51 +1,51 @@
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
 
 import java.util.Map;
 
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JdbcServices} service
  *
  * @todo : should this maybe be a SessionFactory service?
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesInitiator implements BasicServiceInitiator<JdbcServices> {
 	public static final JdbcServicesInitiator INSTANCE = new JdbcServicesInitiator();
 
 	@Override
 	public Class<JdbcServices> getServiceInitiated() {
 		return JdbcServices.class;
 	}
 
 	@Override
 	public JdbcServices initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new JdbcServicesImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
index ba6f0b1de3..a09ecb1773 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
@@ -1,93 +1,93 @@
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
 import java.sql.ResultSet;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Contract for services around JDBC operations.  These represent shared resources, aka not varied by session/use.
  *
  * @author Steve Ebersole
  */
 public interface JdbcServices extends Service {
 	/**
 	 * Obtain service for providing JDBC connections.
 	 *
 	 * @return The connection provider.
 	 */
 	public ConnectionProvider getConnectionProvider();
 
 	/**
 	 * Obtain the dialect of the database to which {@link Connection connections} from
 	 * {@link #getConnectionProvider()} point.
 	 *
 	 * @return The database dialect.
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Obtain service for logging SQL statements.
 	 *
 	 * @return The SQL statement logger.
 	 */
 	public SqlStatementLogger getSqlStatementLogger();
 
 	/**
 	 * Obtain service for dealing with exceptions.
 	 *
 	 * @return The exception helper service.
 	 */
 	public SqlExceptionHelper getSqlExceptionHelper();
 
 	/**
 	 * Obtain information about supported behavior reported by the JDBC driver.
 	 * <p/>
 	 * Yuck, yuck, yuck!  Much prefer this to be part of a "basic settings" type object.
 	 * 
 	 * @return The extracted database metadata, oddly enough :)
 	 */
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport();
 
 	/**
 	 * Create an instance of a {@link LobCreator} appropriate for the current environment, mainly meant to account for
 	 * variance between JDBC 4 (<= JDK 1.6) and JDBC3 (>= JDK 1.5).
 	 *
 	 * @param lobCreationContext The context in which the LOB is being created
 	 * @return The LOB creator.
 	 */
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext);
 
 	/**
 	 * Obtain service for wrapping a {@link ResultSet} in a "column name cache" wrapper.
 	 * @return The ResultSet wrapper.
 	 */
 	public ResultSetWrapper getResultSetWrapper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionFactoryInitiator.java
index f1a32c06cd..b1c4813107 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionFactoryInitiator.java
@@ -1,100 +1,100 @@
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
 package org.hibernate.engine.transaction.internal;
 
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard instantiator for the standard {@link TransactionFactory} service.
  *
  * @author Steve Ebersole
  */
 public class TransactionFactoryInitiator<T extends TransactionImplementor> implements BasicServiceInitiator<TransactionFactory> {
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        TransactionFactoryInitiator.class.getName());
 
 	public static final TransactionFactoryInitiator INSTANCE = new TransactionFactoryInitiator();
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public Class<TransactionFactory> getServiceInitiated() {
 		return TransactionFactory.class;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public TransactionFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		final Object strategy = configurationValues.get( Environment.TRANSACTION_STRATEGY );
 		if ( TransactionFactory.class.isInstance( strategy ) ) {
 			return (TransactionFactory) strategy;
 		}
 
 		if ( strategy == null ) {
             LOG.usingDefaultTransactionStrategy();
 			return new JdbcTransactionFactory();
 		}
 
 		final String strategyClassName = mapLegacyNames( strategy.toString() );
         LOG.transactionStrategy(strategyClassName);
 
 		ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 		try {
 			return (TransactionFactory) classLoaderService.classForName( strategyClassName ).newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Unable to instantiate specified TransactionFactory class [" + strategyClassName + "]", e );
 		}
 	}
 
 	private String mapLegacyNames(String name) {
 		if ( "org.hibernate.transaction.JDBCTransactionFactory".equals( name ) ) {
 			return JdbcTransactionFactory.class.getName();
 		}
 
 		if ( "org.hibernate.transaction.JTATransactionFactory".equals( name ) ) {
 			return JtaTransactionFactory.class.getName();
 		}
 
 		if ( "org.hibernate.transaction.CMTTransactionFactory".equals( name ) ) {
 			return CMTTransactionFactory.class.getName();
 		}
 
 		return name;
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionFactory.java
index ae5d34aab2..56276f3888 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/spi/TransactionFactory.java
@@ -1,83 +1,83 @@
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
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Contract for transaction creation, as well as providing metadata and contextual information about that creation.
  *
  * @author Steve Ebersole
  */
 public interface TransactionFactory<T extends TransactionImplementor> extends Service {
 	/**
 	 * Construct a transaction instance compatible with this strategy.
 	 *
 	 * @param coordinator The coordinator for this transaction
 	 *
 	 * @return The appropriate transaction instance.
 	 *
 	 * @throws org.hibernate.HibernateException Indicates a problem constructing the transaction.
 	 */
 	public T createTransaction(TransactionCoordinator coordinator);
 
 	/**
 	 * Can the transactions created from this strategy act as the driver?  In other words can the user actually manage
 	 * transactions with this strategy?
 	 *
 	 * @return {@literal true} if the transaction strategy represented by this factory can act as the driver callback;
 	 * {@literal false} otherwise.
 	 */
 	public boolean canBeDriver();
 
 	/**
 	 * Should we attempt to register JTA transaction {@link javax.transaction.Synchronization synchronizations}.
 	 * <p/>
 	 * In other words, is this strategy JTA-based?
 	 *
 	 * @return {@literal true} if the transaction strategy represented by this factory is compatible with registering
 	 * {@link javax.transaction.Synchronization synchronizations}; {@literal false} otherwise.
 	 */
 	public boolean compatibleWithJtaSynchronization();
 
 	/**
 	 * Can the underlying transaction represented by the passed Hibernate {@link TransactionImplementor} be joined?
 	 *
 	 * @param transactionCoordinator The transaction coordinator
 	 * @param transaction The current Hibernate transaction
 	 *
 	 * @return {@literal true} is the transaction can be joined; {@literal false} otherwise.
 	 */
 	public boolean isJoinableJtaTransaction(TransactionCoordinator transactionCoordinator, T transaction);
 
 	/**
 	 * Get the default connection release mode.
 	 *
 	 * @return The default release mode associated with this strategy
 	 */
 	public ConnectionReleaseMode getDefaultReleaseMode();
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 90e538ff49..478b376a55 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1316 +1,1324 @@
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
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
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
-	private final transient ServiceRegistry serviceRegistry;
+	private final transient ServiceRegistryImplementor serviceRegistry;
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
-	private final transient SessionFactoryObserver observer;
+	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
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
-		this.serviceRegistry = serviceRegistry;
+		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
+				this,
+				cfg
+		);
 		this.settings = settings;
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
         this.eventListeners = listeners;
-		this.observer = observer != null ? observer : new SessionFactoryObserver() {
-			public void sessionFactoryCreated(SessionFactory factory) {
-			}
-			public void sessionFactoryClosed(SessionFactory factory) {
-			}
-		};
+		if ( observer != null ) {
+			this.observer.addObserver( observer );
+		}
 
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
 
+	@Override
+	public void addObserver(SessionFactoryObserver observer) {
+		this.observer.addObserver( observer );
+	}
+
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
+		serviceRegistry.destroy();
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
 	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java
new file mode 100644
index 0000000000..c7ecb0e48b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java
@@ -0,0 +1,66 @@
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
+package org.hibernate.impl;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.SessionFactory;
+import org.hibernate.SessionFactoryObserver;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SessionFactoryObserverChain implements SessionFactoryObserver {
+	private List<SessionFactoryObserver> observers;
+
+	public void addObserver(SessionFactoryObserver observer) {
+		if ( observers == null ) {
+			observers = new ArrayList<SessionFactoryObserver>();
+		}
+		observers.add( observer );
+	}
+
+	@Override
+	public void sessionFactoryCreated(SessionFactory factory) {
+		if ( observers == null ) {
+			return;
+		}
+
+		for ( SessionFactoryObserver observer : observers ) {
+			observer.sessionFactoryCreated( factory );
+		}
+	}
+
+	@Override
+	public void sessionFactoryClosed(SessionFactory factory) {
+		if ( observers == null ) {
+			return;
+		}
+
+		for ( SessionFactoryObserver observer : observers ) {
+			observer.sessionFactoryClosed( factory );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
index 145e801df4..6e96517276 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
@@ -1,72 +1,72 @@
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
 package org.hibernate.impl;
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class TransactionEnvironmentImpl implements TransactionEnvironment {
 	private final SessionFactoryImpl sessionFactory;
 
 	public TransactionEnvironmentImpl(SessionFactoryImpl sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	protected ServiceRegistry serviceRegistry() {
 		return sessionFactory.getServiceRegistry();
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry().getService( JdbcServices.class );
 	}
 
 	@Override
 	public JtaPlatform getJtaPlatform() {
 		return serviceRegistry().getService( JtaPlatform.class );
 	}
 
 	@Override
 	public TransactionFactory getTransactionFactory() {
 		return serviceRegistry().getService( TransactionFactory.class );
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return sessionFactory.getStatisticsImplementor();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java
index dd710d3bcd..2344ab8052 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java
@@ -1,69 +1,69 @@
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
 package org.hibernate.persister.internal;
 
 import java.util.Map;
 
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceException;
 
 /**
  * @author Steve Ebersole
  */
 public class PersisterClassResolverInitiator implements BasicServiceInitiator<PersisterClassResolver> {
 	public static final PersisterClassResolverInitiator INSTANCE = new PersisterClassResolverInitiator();
 	public static final String IMPL_NAME = "hibernate.persister.resolver";
 
 	@Override
 	public Class<PersisterClassResolver> getServiceInitiated() {
 		return PersisterClassResolver.class;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public PersisterClassResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		final Object customImpl = configurationValues.get( IMPL_NAME );
 		if ( customImpl == null ) {
 			return new StandardPersisterClassResolver();
 		}
 
 		if ( PersisterClassResolver.class.isInstance( customImpl ) ) {
 			return (PersisterClassResolver) customImpl;
 		}
 
 		final Class<? extends PersisterClassResolver> customImplClass = Class.class.isInstance( customImpl )
 				? (Class<? extends PersisterClassResolver>) customImpl
 				: registry.getService( ClassLoaderService.class ).classForName( customImpl.toString() );
 
 		try {
 			return customImplClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new ServiceException( "Could not initialize custom PersisterClassResolver impl [" + customImplClass.getName() + "]", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index c56523a9b9..4f34548b2c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,183 +1,183 @@
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
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 
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
 
 	private ServiceRegistry serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistry serviceRegistry) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java
index 68cc54643f..420769808f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java
@@ -1,69 +1,69 @@
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
 package org.hibernate.persister.internal;
 
 import java.util.Map;
 
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceException;
 
 /**
  * @author Steve Ebersole
  */
 public class PersisterFactoryInitiator implements BasicServiceInitiator<PersisterFactory> {
 	public static final PersisterFactoryInitiator INSTANCE = new PersisterFactoryInitiator();
 
 	public static final String IMPL_NAME = "hibernate.persister.factory";
 
 	@Override
 	public Class<PersisterFactory> getServiceInitiated() {
 		return PersisterFactory.class;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public PersisterFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		final Object customImpl = configurationValues.get( IMPL_NAME );
 		if ( customImpl == null ) {
 			return new PersisterFactoryImpl();
 		}
 
 		if ( PersisterFactory.class.isInstance( customImpl ) ) {
 			return (PersisterFactory) customImpl;
 		}
 
 		final Class<? extends PersisterFactory> customImplClass = Class.class.isInstance( customImpl )
 				? ( Class<? extends PersisterFactory> ) customImpl
 				: registry.getService( ClassLoaderService.class ).classForName( customImpl.toString() );
 		try {
 			return customImplClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new ServiceException( "Could not initialize custom PersisterFactory impl [" + customImplClass.getName() + "]", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
index 7bfd7e7aa9..9ec5515f84 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
@@ -1,62 +1,62 @@
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
 package org.hibernate.persister.spi;
 
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Provides persister classes based on the entity or collection role.
  * The persister class is chosen according to the following rules in decreasing priority:
  *  - the persister class defined explicitly via annotation or XML
  *  - the persister class returned by the PersisterClassResolver implementation (if not null)
  *  - the default provider as chosen by Hibernate Core (best choice most of the time)
  *
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  * @author Steve Ebersole
  */
 public interface PersisterClassResolver extends Service {
 	/**
 	 * Returns the entity persister class for a given entityName or null
 	 * if the entity persister class should be the default.
 	 *
 	 * @param metadata The entity metadata
 	 *
 	 * @return The entity persister class to use
 	 */
 	Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata);
 
 	/**
 	 * Returns the collection persister class for a given collection role or null
 	 * if the collection persister class should be the default.
 	 *
 	 * @param metadata The collection metadata
 	 *
 	 * @return The collection persister class to use
 	 */
 	Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index e71e41d349..72e37635a4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
@@ -1,89 +1,89 @@
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
 package org.hibernate.persister.spi;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Contract for creating persister instances (both {@link EntityPersister} and {@link CollectionPersister} varieties).
  *
  * @author Steve Ebersole
  */
 public interface PersisterFactory extends Service {
 
 	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
 	// Should it not be enough with associated class ? or why does EntityPersister's not get access to configuration ?
 	//
 	// The only reason I could see that Configuration gets passed to collection persisters
 	// is so that they can look up the dom4j node name of the entity element in case
 	// no explicit node name was applied at the collection element level.  Are you kidding me?
 	// Trivial to fix then.  Just store and expose the node name on the entity persister
 	// (which the collection persister looks up anyway via other means...).
 
 	/**
 	 * Create an entity persister instance.
 	 *
 	 * @param model The O/R mapping metamodel definition for the entity
 	 * @param cacheAccessStrategy The caching strategy for this entity
 	 * @param factory The session factory
 	 * @param cfg The overall mapping
 	 *
 	 * @return An appropriate entity persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public EntityPersister createEntityPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException;
 
 	/**
 	 * Create a collection persister instance.
 	 *
 	 * @param cfg The configuration
 	 * @param model The O/R mapping metamodel definition for the collection
 	 * @param cacheAccessStrategy The caching strategy for this collection
 	 * @param factory The session factory
 	 *
 	 * @return An appropriate collection persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceBinding.java b/hibernate-core/src/main/java/org/hibernate/service/BasicServiceRegistry.java
similarity index 77%
rename from hibernate-core/src/main/java/org/hibernate/service/internal/ServiceBinding.java
rename to hibernate-core/src/main/java/org/hibernate/service/BasicServiceRegistry.java
index 0289c4fec2..56d66cda9d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/BasicServiceRegistry.java
@@ -1,48 +1,33 @@
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
-package org.hibernate.service.internal;
+package org.hibernate.service;
+
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * @author Steve Ebersole
  */
-final class ServiceBinding<R> {
-	private final R proxy;
-	private R target;
-
-	ServiceBinding(R proxy) {
-		this.proxy = proxy;
-	}
-
-	public R getProxy() {
-		return proxy;
-	}
-
-	public R getTarget() {
-		return target;
-	}
-
-	public void setTarget(R target) {
-		this.target = target;
-	}
+public interface BasicServiceRegistry extends ServiceRegistry {
+	public void registerServiceInitiator(BasicServiceInitiator initiator);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/Service.java b/hibernate-core/src/main/java/org/hibernate/service/Service.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/Service.java
rename to hibernate-core/src/main/java/org/hibernate/service/Service.java
index 796e1afd83..6fa598ea0d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/Service.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/Service.java
@@ -1,36 +1,36 @@
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
-package org.hibernate.service.spi;
+package org.hibernate.service;
 
 import java.io.Serializable;
 
 /**
  * Marker interface for services.
  * <p/>
  * NOTE : All services must be {@link Serializable}!
  *
  * @author Steve Ebersole
  */
 public interface Service extends Serializable {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java
index 6672ebf719..3e8170ae8c 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistry.java
@@ -1,62 +1,62 @@
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
-package org.hibernate.service.spi;
+package org.hibernate.service;
 
 /**
  * The registry of {@link Service services}.
  *
  * @author Steve Ebersole
  */
 public interface ServiceRegistry {
 	/**
 	 * Retrieve this registry's parent registry.
 	 * 
 	 * @return The parent registry.  May be null.
 	 */
 	public ServiceRegistry getParentServiceRegistry();
 
 	/**
-	 * Retrieve a service by role.  If service is not found, but a {@link BasicServiceInitiator} is registered for
+	 * Retrieve a service by role.  If service is not found, but a {@link org.hibernate.service.spi.BasicServiceInitiator} is registered for
 	 * this service role, the service will be initialized and returned.
 	 * <p/>
 	 * NOTE: We cannot return {@code <R extends Service<T>>} here because the service might come from the parent...
 	 * 
 	 * @param serviceRole The service role
 	 * @param <R> The service role type
 	 *
 	 * @return The requested service.
 	 *
 	 * @throws UnknownServiceException Indicates the service was not known.
 	 */
 	public <R extends Service> R getService(Class<R> serviceRole);
 
 	/**
 	 * Register a service into the registry.
 	 *
 	 * @param serviceRole The service role.
 	 * @param service The service to register
 	 * @param <R> The service role type
 	 */
 	public <R extends Service> void registerService(Class<R> serviceRole, R service);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
similarity index 92%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
rename to hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 512279b38d..247e5199ea 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,69 +1,73 @@
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
-package org.hibernate.service.spi;
+package org.hibernate.service;
 
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
+import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
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
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
+		serviceInitiators.add( SessionFactoryServiceRegistryFactoryInitiator.INSTANCE );
+
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java b/hibernate-core/src/main/java/org/hibernate/service/UnknownServiceException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java
rename to hibernate-core/src/main/java/org/hibernate/service/UnknownServiceException.java
index b7f5180fe3..0b6badf4c7 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/UnknownServiceException.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/UnknownServiceException.java
@@ -1,43 +1,43 @@
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
-package org.hibernate.service.spi;
+package org.hibernate.service;
 import org.hibernate.HibernateException;
 
 /**
  * Indicates that an unkown service was requested from the registry.
  *
  * @author Steve Ebersole
  */
 public class UnknownServiceException extends HibernateException {
 	public final Class serviceRole;
 
 	public UnknownServiceException(Class serviceRole) {
 		super( "Unknown service requested [" + serviceRole.getName() + "]" );
 		this.serviceRole = serviceRole;
 	}
 
 	public Class getServiceRole() {
 		return serviceRole;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/UnknownUnwrapTypeException.java b/hibernate-core/src/main/java/org/hibernate/service/UnknownUnwrapTypeException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/UnknownUnwrapTypeException.java
rename to hibernate-core/src/main/java/org/hibernate/service/UnknownUnwrapTypeException.java
index 5e2406dcab..684e5b7fdf 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/UnknownUnwrapTypeException.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/UnknownUnwrapTypeException.java
@@ -1,40 +1,40 @@
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
-package org.hibernate.service.spi;
+package org.hibernate.service;
 
 import org.hibernate.HibernateException;
 
 /**
  * @author Steve Ebersole
  */
 public class UnknownUnwrapTypeException extends HibernateException {
 	public UnknownUnwrapTypeException(Class unwrapType) {
 		super( "Cannot unwrap to requested type [" + unwrapType.getName() + "]" );
 	}
 
 	public UnknownUnwrapTypeException(Class unwrapType, Throwable root) {
 		this( unwrapType );
 		super.initCause( root );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
index ebdb52b4aa..861f677cf1 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
@@ -1,49 +1,49 @@
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
 
 import java.util.Map;
 
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link ClassLoaderService} service.
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceInitiator implements BasicServiceInitiator<ClassLoaderService> {
 	public static final ClassLoaderServiceInitiator INSTANCE = new ClassLoaderServiceInitiator();
 
 	@Override
 	public Class<ClassLoaderService> getServiceInitiated() {
 		return ClassLoaderService.class;
 	}
 
 	@Override
 	public ClassLoaderService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new ClassLoaderServiceImpl( configurationValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
index b10175e9a0..abd25bce8c 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
@@ -1,75 +1,75 @@
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
 
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * A service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public interface ClassLoaderService extends Service {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
index a2c92818ab..58c91f69d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
@@ -1,160 +1,245 @@
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
 
+import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.service.Service;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.proxy.javassist.ServiceProxyFactoryFactoryImpl;
-import org.hibernate.service.spi.Service;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.jmx.spi.JmxService;
+import org.hibernate.service.spi.InjectService;
+import org.hibernate.service.spi.Manageable;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.Startable;
 import org.hibernate.service.spi.Stoppable;
-import org.hibernate.service.spi.UnknownServiceException;
+import org.hibernate.service.UnknownServiceException;
 import org.hibernate.service.spi.proxy.ServiceProxyFactory;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractServiceRegistryImpl implements ServiceRegistryImplementor {
 	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, AbstractServiceRegistryImpl.class.getName() );
 
 	private final ServiceRegistryImplementor parent;
 
 	// for now just hard-code the javassist factory
 	private ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactoryFactoryImpl().makeServiceProxyFactory( this );
 
 	private ConcurrentHashMap<Class,ServiceBinding> serviceBindingMap;
 	// IMPL NOTE : the list used for ordered destruction.  Cannot used map above because we need to
 	// iterate it in reverse order which is only available through ListIterator
 	private List<Service> serviceList = new ArrayList<Service>();
 
 	protected AbstractServiceRegistryImpl() {
 		this( null );
 	}
 
 	protected AbstractServiceRegistryImpl(ServiceRegistryImplementor parent) {
 		this.parent = parent;
 		// assume 20 services for initial sizing
 		this.serviceBindingMap = CollectionHelper.concurrentMap( 20 );
 		this.serviceList = CollectionHelper.arrayList( 20 );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public ServiceRegistry getParentServiceRegistry() {
 		return parent;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
 		return locateServiceBinding( serviceRole, true );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole, boolean checkParent) {
 		ServiceBinding<R> serviceBinding = serviceBindingMap.get( serviceRole );
 		if ( serviceBinding == null && checkParent && parent != null ) {
 			// look in parent
 			serviceBinding = parent.locateServiceBinding( serviceRole );
 		}
 		return serviceBinding;
 	}
 
 	@Override
 	public <R extends Service> R getService(Class<R> serviceRole) {
 		return locateOrCreateServiceBinding( serviceRole, true ).getProxy();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected <R extends Service> ServiceBinding<R> locateOrCreateServiceBinding(Class<R> serviceRole, boolean checkParent) {
 		ServiceBinding<R> serviceBinding = locateServiceBinding( serviceRole, checkParent );
 		if ( serviceBinding == null ) {
 			R proxy = serviceProxyFactory.makeProxy( serviceRole );
 			serviceBinding = new ServiceBinding<R>( proxy );
 			serviceBindingMap.put( serviceRole, serviceBinding );
 		}
 		return serviceBinding;
 	}
 
 	@Override
 	public <R extends Service> void registerService(Class<R> serviceRole, R service) {
 		ServiceBinding<R> serviceBinding = locateOrCreateServiceBinding( serviceRole, false );
 		R priorServiceInstance = serviceBinding.getTarget();
 		serviceBinding.setTarget( service );
 		if ( priorServiceInstance != null ) {
 			serviceList.remove( priorServiceInstance );
 		}
 		serviceList.add( service );
 	}
 
-	protected abstract <R extends Service> R initializeService(Class<R> serviceRole);
+	private <R extends Service> R initializeService(Class<R> serviceRole) {
+        LOG.trace("Initializing service [role=" + serviceRole.getName() + "]");
+
+		// PHASE 1 : create service
+		R service = createService( serviceRole );
+		if ( service == null ) {
+			return null;
+		}
+
+		// PHASE 2 : configure service (***potentially recursive***)
+		configureService( service );
+
+		// PHASE 3 : Start service
+		startService( service, serviceRole );
+
+		return service;
+	}
+
+	protected abstract <T extends Service> T createService(Class<T> serviceRole);
+	protected abstract <T extends Service> void configureService(T service);
+
+	protected <T extends Service> void applyInjections(T service) {
+		try {
+			for ( Method method : service.getClass().getMethods() ) {
+				InjectService injectService = method.getAnnotation( InjectService.class );
+				if ( injectService == null ) {
+					continue;
+				}
+
+				applyInjection( service, method, injectService );
+			}
+		}
+		catch (NullPointerException e) {
+            LOG.error("NPE injecting service deps : " + service.getClass().getName());
+		}
+	}
+
+	@SuppressWarnings({ "unchecked" })
+	private <T extends Service> void applyInjection(T service, Method injectionMethod, InjectService injectService) {
+		if ( injectionMethod.getParameterTypes() == null || injectionMethod.getParameterTypes().length != 1 ) {
+			throw new ServiceDependencyException(
+					"Encountered @InjectService on method with unexpected number of parameters"
+			);
+		}
+
+		Class dependentServiceRole = injectService.serviceRole();
+		if ( dependentServiceRole == null || dependentServiceRole.equals( Void.class ) ) {
+			dependentServiceRole = injectionMethod.getParameterTypes()[0];
+		}
+
+		// todo : because of the use of proxies, this is no longer returning null here...
+
+		final Service dependantService = getService( dependentServiceRole );
+		if ( dependantService == null ) {
+			if ( injectService.required() ) {
+				throw new ServiceDependencyException(
+						"Dependency [" + dependentServiceRole + "] declared by service [" + service + "] not found"
+				);
+			}
+		}
+		else {
+			try {
+				injectionMethod.invoke( service, dependantService );
+			}
+			catch ( Exception e ) {
+				throw new ServiceDependencyException( "Cannot inject dependency service", e );
+			}
+		}
+	}
+
+	@SuppressWarnings({ "unchecked" })
+	protected <T extends Service> void startService(T service, Class serviceRole) {
+		if ( Startable.class.isInstance( service ) ) {
+			( (Startable) service ).start();
+		}
+
+		if ( Manageable.class.isInstance( service ) ) {
+			getService( JmxService.class ).registerService( (Manageable) service, serviceRole );
+		}
+	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <R extends Service> R getServiceInternal(Class<R> serviceRole) {
 		// this call comes from the binding proxy, we most definitely do not want to look up into the parent
 		// in this case!
 		ServiceBinding<R> serviceBinding = locateServiceBinding( serviceRole, false );
 		if ( serviceBinding == null ) {
 			throw new HibernateException( "Only proxies should invoke #getServiceInternal" );
 		}
 		R service = serviceBinding.getTarget();
 		if ( service == null ) {
 			service = initializeService( serviceRole );
 			serviceBinding.setTarget( service );
 		}
 		if ( service == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 		return service;
 	}
 
 	public void destroy() {
 		ListIterator<Service> serviceIterator = serviceList.listIterator( serviceList.size() );
 		while ( serviceIterator.hasPrevious() ) {
 			final Service service = serviceIterator.previous();
 			if ( Stoppable.class.isInstance( service ) ) {
 				try {
 					( (Stoppable) service ).stop();
 				}
 				catch ( Exception e ) {
                     LOG.unableToStopService(service.getClass(), e.toString());
 				}
 			}
 		}
 		serviceList.clear();
 		serviceList = null;
 		serviceBindingMap.clear();
 		serviceBindingMap = null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
index cb4661a578..5623092564 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
@@ -1,208 +1,130 @@
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
 package org.hibernate.service.internal;
 
 import java.lang.reflect.Method;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateLogger;
-import org.hibernate.service.jmx.spi.JmxService;
+import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.Service;
+import org.hibernate.service.StandardServiceInitiators;
+import org.hibernate.service.UnknownServiceException;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.InjectService;
-import org.hibernate.service.spi.Manageable;
-import org.hibernate.service.spi.Service;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
-import org.hibernate.service.spi.StandardServiceInitiators;
-import org.hibernate.service.spi.Startable;
-import org.hibernate.service.spi.UnknownServiceException;
 
 /**
  * Standard Hibernate implementation of the service registry.
  *
  * @author Steve Ebersole
  */
-public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl {
+public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl implements BasicServiceRegistry {
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicServiceRegistryImpl.class.getName());
 
 	private final Map<Class,BasicServiceInitiator> serviceInitiatorMap;
 	private final Map configurationValues;
 
 	public BasicServiceRegistryImpl(Map configurationValues) {
 		this( StandardServiceInitiators.LIST, configurationValues );
 	}
 
 	public BasicServiceRegistryImpl(List<BasicServiceInitiator> serviceInitiators, Map configurationValues) {
 		super();
 		this.serviceInitiatorMap = toMap( serviceInitiators );
 		this.configurationValues = configurationValues;
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
 
+	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void registerServiceInitiator(BasicServiceInitiator initiator) {
 		ServiceBinding serviceBinding = locateServiceBinding( initiator.getServiceInitiated(), false );
 		if ( serviceBinding != null ) {
 			serviceBinding.setTarget( null );
 		}
 		final Object previous = serviceInitiatorMap.put( initiator.getServiceInitiated(), initiator );
 		if ( previous != null ) {
 			LOG.debugf( "Over-wrote existing service initiator [role=%s]", initiator.getServiceInitiated().getName() );
 		}
 	}
 
 	@Override
-	protected <R extends Service> R initializeService(Class<R> serviceRole) {
-        LOG.trace("Initializing service [role=" + serviceRole.getName() + "]");
-
-		// PHASE 1 : create service
-		R service = createService( serviceRole );
-		if ( service == null ) {
-			return null;
-		}
-
-		// PHASE 2 : configure service (***potentially recursive***)
-		configureService( service );
-
-		// PHASE 3 : Start service
-		startService( service, serviceRole );
-
-		return service;
-	}
-
 	@SuppressWarnings({ "unchecked" })
-	private <T extends Service> T createService(Class<T> serviceRole) {
+	protected <T extends Service> T createService(Class<T> serviceRole) {
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
 
-	private <T extends Service> void configureService(T service) {
+	@Override
+	protected <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
 		if ( Configurable.class.isInstance( service ) ) {
 			( (Configurable) service ).configure( configurationValues );
 		}
 
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
 			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
 	}
-
-	private <T extends Service> void applyInjections(T service) {
-		try {
-			for ( Method method : service.getClass().getMethods() ) {
-				InjectService injectService = method.getAnnotation( InjectService.class );
-				if ( injectService == null ) {
-					continue;
-				}
-
-				applyInjection( service, method, injectService );
-			}
-		}
-		catch (NullPointerException e) {
-            LOG.error("NPE injecting service deps : " + service.getClass().getName());
-		}
-	}
-
-	@SuppressWarnings({ "unchecked" })
-	private <T extends Service> void applyInjection(T service, Method injectionMethod, InjectService injectService) {
-		if ( injectionMethod.getParameterTypes() == null || injectionMethod.getParameterTypes().length != 1 ) {
-			throw new ServiceDependencyException(
-					"Encountered @InjectService on method with unexpected number of parameters"
-			);
-		}
-
-		Class dependentServiceRole = injectService.serviceRole();
-		if ( dependentServiceRole == null || dependentServiceRole.equals( Void.class ) ) {
-			dependentServiceRole = injectionMethod.getParameterTypes()[0];
-		}
-
-		// todo : because of the use of proxies, this is no longer returning null here...
-
-		final Service dependantService = getService( dependentServiceRole );
-		if ( dependantService == null ) {
-			if ( injectService.required() ) {
-				throw new ServiceDependencyException(
-						"Dependency [" + dependentServiceRole + "] declared by service [" + service + "] not found"
-				);
-			}
-		}
-		else {
-			try {
-				injectionMethod.invoke( service, dependantService );
-			}
-			catch ( Exception e ) {
-				throw new ServiceDependencyException( "Cannot inject dependency service", e );
-			}
-		}
-	}
-
-	@SuppressWarnings({ "unchecked" })
-	private <T extends Service> void startService(T service, Class serviceRole) {
-		if ( Startable.class.isInstance( service ) ) {
-			( (Startable) service ).start();
-		}
-
-		if ( Manageable.class.isInstance( service ) ) {
-			getService( JmxService.class ).registerService( (Manageable) service, serviceRole );
-		}
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java
index a8bae0dffa..4d32804f12 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceProxy.java
@@ -1,41 +1,41 @@
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
 
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Marker interface for a service proxy which allows mixed-in ability to unproxy.
  *
  * @author Steve Ebersole
  */
 public interface ServiceProxy extends Service {
 	/**
 	 * Get the target service instance represented by this proxy.
 	 *
 	 * @param <T>
 	 * @return
 	 */
 	public <T extends Service> T getTargetInstance();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
new file mode 100644
index 0000000000..446a2a71ae
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
@@ -0,0 +1,51 @@
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
+package org.hibernate.service.internal;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.service.Service;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
+
+/**
+ * Acts as a {@link Service} in the {@link BasicServiceRegistryImpl} whose function is as a factory for
+ * {@link SessionFactoryServiceRegistryImpl} implementations.
+ *
+ * @author Steve Ebersole
+ */
+public class SessionFactoryServiceRegistryFactoryImpl implements SessionFactoryServiceRegistryFactory {
+	private final ServiceRegistryImplementor theBasicServiceRegistry;
+
+	public SessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
+		this.theBasicServiceRegistry = theBasicServiceRegistry;
+	}
+
+	@Override
+	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
+			SessionFactoryImplementor sessionFactory,
+			Configuration configuration) {
+		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, configuration );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryInitiator.java
new file mode 100644
index 0000000000..5cabc73b87
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryInitiator.java
@@ -0,0 +1,47 @@
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
+package org.hibernate.service.internal;
+
+import java.util.Map;
+
+import org.hibernate.service.spi.BasicServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SessionFactoryServiceRegistryFactoryInitiator implements BasicServiceInitiator<SessionFactoryServiceRegistryFactory> {
+	public static final SessionFactoryServiceRegistryFactoryInitiator INSTANCE = new SessionFactoryServiceRegistryFactoryInitiator();
+
+	@Override
+	public Class<SessionFactoryServiceRegistryFactory> getServiceInitiated() {
+		return SessionFactoryServiceRegistryFactory.class;
+	}
+
+	@Override
+	public SessionFactoryServiceRegistryFactoryImpl initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		return new SessionFactoryServiceRegistryFactoryImpl( registry );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
new file mode 100644
index 0000000000..50c3a99286
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
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
+package org.hibernate.service.internal;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.service.Service;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
+/**
+ * @author Steve Ebersole
+ */
+public class SessionFactoryServiceRegistryImpl
+		extends AbstractServiceRegistryImpl
+		implements SessionFactoryServiceRegistry {
+
+	private final SessionFactoryImplementor sessionFactory;
+	private Configuration configuration;
+
+	// for now we need to hold on to the Configuration... :(
+
+	public SessionFactoryServiceRegistryImpl(
+			ServiceRegistryImplementor parent,
+			SessionFactoryImplementor sessionFactory,
+			Configuration configuration) {
+		super( parent );
+		this.sessionFactory = sessionFactory;
+		this.configuration = configuration;
+	}
+
+	@Override
+	protected <T extends Service> T createService(Class<T> serviceRole) {
+		return null; // todo : implement method body
+	}
+
+	@Override
+	protected <T extends Service> void configureService(T service) {
+		// todo : implement method body
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/proxy/javassist/ServiceProxyFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/proxy/javassist/ServiceProxyFactoryImpl.java
index 9d823a9a44..9be3b2daa5 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/proxy/javassist/ServiceProxyFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/proxy/javassist/ServiceProxyFactoryImpl.java
@@ -1,124 +1,124 @@
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
 package org.hibernate.service.internal.proxy.javassist;
 
 import javassist.util.proxy.MethodFilter;
 import javassist.util.proxy.MethodHandler;
 import javassist.util.proxy.ProxyFactory;
 import javassist.util.proxy.ProxyObject;
 
 import org.hibernate.service.internal.ServiceProxy;
 import org.hibernate.service.internal.ServiceProxyGenerationException;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 import org.hibernate.service.spi.proxy.ServiceProxyFactory;
 import org.hibernate.service.spi.proxy.ServiceProxyTargetSource;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
 /**
  * Javassist-based implementation of a {@link ServiceProxyFactory}
  *
  * @author Steve Ebersole
  */
 public class ServiceProxyFactoryImpl implements ServiceProxyFactory {
 	private final ServiceProxyTargetSource serviceRegistry;
 
 	public ServiceProxyFactoryImpl(ServiceProxyTargetSource serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	private static final MethodFilter FINALIZE_FILTER = new MethodFilter() {
 		public boolean isHandled(Method m) {
 			// skip finalize methods
 			return !( m.getParameterTypes().length == 0 && m.getName().equals( "finalize" ) );
 		}
 	};
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T extends Service> T makeProxy(Class<T> serviceRole) {
 		try {
 			ProxyFactory factory = new ProxyFactory();
 			factory.setFilter( FINALIZE_FILTER );
 
 			Class[] interfaces = new Class[2];
 			interfaces[0] = serviceRole;
 			interfaces[1] = ServiceProxy.class;
 			factory.setInterfaces( interfaces );
 
 			Class proxyClass = factory.createClass();
 			ProxyObject proxyObject = (ProxyObject) proxyClass.newInstance();
 			proxyObject.setHandler( new ServiceProxyMethodInterceptor<T>( (T)proxyObject, serviceRole, serviceRegistry ) );
 			return (T) proxyObject;
 		}
 		catch (Exception e) {
 			throw new ServiceProxyGenerationException( "Unable to make service proxy", e );
 		}
 	}
 
 	private static class ServiceProxyMethodInterceptor<T extends Service> implements MethodHandler {
 		private final T proxy;
 		private final Class<T> serviceRole;
 		private final ServiceProxyTargetSource serviceRegistry;
 
 		private ServiceProxyMethodInterceptor(T proxy, Class<T> serviceRole, ServiceProxyTargetSource serviceRegistry) {
 			this.proxy = proxy;
 			this.serviceRole = serviceRole;
 			this.serviceRegistry = serviceRegistry;
 		}
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryBoxing"} )
 		public Object invoke(
 				Object object,
 		        Method method,
 		        Method method1,
 		        Object[] args) throws Exception {
 			String name = method.getName();
 			if ( "toString".equals( name ) ) {
 				return serviceRole.getName() + "_$$_Proxy@" + System.identityHashCode( object );
 			}
 			else if ( "equals".equals( name ) ) {
 				return proxy == object ? Boolean.TRUE : Boolean.FALSE;
 			}
 			else if ( "hashCode".equals( name ) ) {
 				return Integer.valueOf( System.identityHashCode( object ) );
 			}
 			else if ( "getTargetInstance".equals( name ) && ServiceProxy.class.equals( method.getDeclaringClass() ) ) {
 				return serviceRegistry.getServiceInternal( serviceRole );
 			}
 			else {
 				try {
 					T target = serviceRegistry.getServiceInternal( serviceRole );
 					return method.invoke( target, args );
 				}
 				catch (InvocationTargetException e) {
 					throw (Exception) e.getTargetException();
 				}
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
index 0fd0a267d3..a7cb84e174 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
@@ -1,280 +1,280 @@
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
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DatasourceConnectionProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DatasourceConnectionProviderImpl.java
index 14c59ab4ec..00dcc8c01d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DatasourceConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DatasourceConnectionProviderImpl.java
@@ -1,156 +1,156 @@
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
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Map;
 import javax.sql.DataSource;
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
+import org.hibernate.service.UnknownUnwrapTypeException;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.InjectService;
 import org.hibernate.service.spi.Stoppable;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
 
 /**
  * A {@link ConnectionProvider} that manages connections from an underlying {@link DataSource}.
  * <p/>
  * The {@link DataSource} to use may be specified by either:<ul>
  * <li>injection via {@link #setDataSource}</li>
  * <li>decaring the {@link DataSource} instance using the {@link Environment#DATASOURCE} config property</li>
  * <li>decaring the JNDI name under which the {@link DataSource} can be found via {@link Environment#DATASOURCE} config property</li>
  * </ul>
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class DatasourceConnectionProviderImpl implements ConnectionProvider, Configurable, Stoppable {
 
 	private DataSource dataSource;
 	private String user;
 	private String pass;
 	private boolean useCredentials;
 	private JndiService jndiService;
 
 	private boolean available;
 
 	public DataSource getDataSource() {
 		return dataSource;
 	}
 
 	public void setDataSource(DataSource dataSource) {
 		this.dataSource = dataSource;
 	}
 
 	@InjectService( required = false )
 	public void setJndiService(JndiService jndiService) {
 		this.jndiService = jndiService;
 	}
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				DatasourceConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ||
 				DataSource.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				DatasourceConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else if ( DataSource.class.isAssignableFrom( unwrapType ) ) {
 			return (T) getDataSource();
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void configure(Map configValues) {
 		if ( this.dataSource == null ) {
 			final Object dataSource = configValues.get( Environment.DATASOURCE );
 			if ( DataSource.class.isInstance( dataSource ) ) {
 				this.dataSource = (DataSource) dataSource;
 			}
 			else {
 				final String dataSourceJndiName = (String) dataSource;
 				if ( dataSourceJndiName == null ) {
 					throw new HibernateException(
 							"DataSource to use was not injected nor specified by [" + Environment.DATASOURCE
 									+ "] configuration property"
 					);
 				}
 				if ( jndiService == null ) {
 					throw new HibernateException( "Unable to locate JndiService to lookup Datasource" );
 				}
 				this.dataSource = (DataSource) jndiService.locate( dataSourceJndiName );
 			}
 		}
 		if ( this.dataSource == null ) {
 			throw new HibernateException( "Unable to determine appropriate DataSource to use" );
 		}
 
 		user = (String) configValues.get( Environment.USER );
 		pass = (String) configValues.get( Environment.PASS );
 		useCredentials = user != null || pass != null;
 		available = true;
 	}
 
 	public void stop() {
 		available = false;
 		dataSource = null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection getConnection() throws SQLException {
 		if ( !available ) {
 			throw new HibernateException( "Provider is closed!" );
 		}
 		return useCredentials ? dataSource.getConnection( user, pass ) : dataSource.getConnection();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void closeConnection(Connection connection) throws SQLException {
 		connection.close();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean supportsAggressiveRelease() {
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
index aaf414fbfb..73af9e393a 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
@@ -1,214 +1,214 @@
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
 
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.Stoppable;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
+import org.hibernate.service.UnknownUnwrapTypeException;
 import org.jboss.logging.Logger;
 
 /**
  * A connection provider that uses the {@link java.sql.DriverManager} directly to open connections and provides
  * a very rudimentary connection pool.
  * <p/>
  * IMPL NOTE : not intended for production use!
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"UnnecessaryUnboxing"})
 public class DriverManagerConnectionProviderImpl implements ConnectionProvider, Configurable, Stoppable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DriverManagerConnectionProviderImpl.class.getName());
 
 	private String url;
 	private Properties connectionProps;
 	private Integer isolation;
 	private int poolSize;
 	private boolean autocommit;
 
 	private final ArrayList<Connection> pool = new ArrayList<Connection>();
 	private int checkedOut = 0;
 
 	private boolean stopped;
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				DriverManagerConnectionProviderImpl.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				DriverManagerConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	public void configure(Map configurationValues) {
         LOG.usingHibernateBuiltInConnectionPool();
 
 		String driverClassName = (String) configurationValues.get( Environment.DRIVER );
         if (driverClassName == null) LOG.jdbcDriverNotSpecified(Environment.DRIVER);
 		else {
 			try {
 				// trying via forName() first to be as close to DriverManager's semantics
 				Class.forName( driverClassName );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				try {
 					ReflectHelper.classForName( driverClassName );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new HibernateException( "Specified JDBC Driver " + driverClassName + " class not found", e );
 				}
 			}
 		}
 
 		poolSize = ConfigurationHelper.getInt( Environment.POOL_SIZE, configurationValues, 20 ); // default pool size 20
         LOG.hibernateConnectionPoolSize(poolSize);
 
 		autocommit = ConfigurationHelper.getBoolean( Environment.AUTOCOMMIT, configurationValues );
         LOG.autoCommitMode(autocommit);
 
 		isolation = ConfigurationHelper.getInteger( Environment.ISOLATION, configurationValues );
         if (isolation != null) LOG.jdbcIsolationLevel(Environment.isolationLevelToString(isolation.intValue()));
 
 		url = (String) configurationValues.get( Environment.URL );
 		if ( url == null ) {
             String msg = LOG.jdbcUrlNotSpecified(Environment.URL);
             LOG.error(msg);
 			throw new HibernateException( msg );
 		}
 
 		connectionProps = ConnectionProviderInitiator.getConnectionProperties( configurationValues );
 
         LOG.usingDriver(driverClassName, url);
 		// if debug level is enabled, then log the password, otherwise mask it
         if (LOG.isDebugEnabled()) LOG.connectionProperties(connectionProps);
         else LOG.connectionProperties(ConfigurationHelper.maskOut(connectionProps, "password"));
 	}
 
 	public void stop() {
         LOG.cleaningUpConnectionPool(url);
 
 		for ( Connection connection : pool ) {
 			try {
 				connection.close();
 			}
 			catch (SQLException sqle) {
                 LOG.unableToClosePooledConnection(sqle);
 			}
 		}
 		pool.clear();
 		stopped = true;
 	}
 
 	public Connection getConnection() throws SQLException {
         LOG.trace("Total checked-out connections: " + checkedOut);
 
 		// essentially, if we have available connections in the pool, use one...
 		synchronized (pool) {
 			if ( !pool.isEmpty() ) {
 				int last = pool.size() - 1;
                 if (LOG.isTraceEnabled()) {
                     LOG.trace("Using pooled JDBC connection, pool size: " + last);
 					checkedOut++;
 				}
 				Connection pooled = pool.remove(last);
 				if ( isolation != null ) {
 					pooled.setTransactionIsolation( isolation.intValue() );
 				}
 				if ( pooled.getAutoCommit() != autocommit ) {
 					pooled.setAutoCommit( autocommit );
 				}
 				return pooled;
 			}
 		}
 
 		// otherwise we open a new connection...
 
         LOG.debugf("Opening new JDBC connection");
 		Connection conn = DriverManager.getConnection( url, connectionProps );
 		if ( isolation != null ) {
 			conn.setTransactionIsolation( isolation.intValue() );
 		}
 		if ( conn.getAutoCommit() != autocommit ) {
 			conn.setAutoCommit(autocommit);
 		}
 
         LOG.debugf("Created connection to: %s, Isolation Level: %s", url, conn.getTransactionIsolation());
 
 		checkedOut++;
 
 		return conn;
 	}
 
 	public void closeConnection(Connection conn) throws SQLException {
 		checkedOut--;
 
 		// add to the pool if the max size is not yet reached.
 		synchronized (pool) {
 			int currentSize = pool.size();
 			if ( currentSize < poolSize ) {
                 LOG.trace("Returning connection to pool, pool size: " + (currentSize + 1));
 				pool.add(conn);
 				return;
 			}
 		}
 
         LOG.debugf("Closing JDBC connection");
 		conn.close();
 	}
 
 	@Override
     protected void finalize() throws Throwable {
 		if ( !stopped ) {
 			stop();
 		}
 		super.finalize();
 	}
 
 	public boolean supportsAggressiveRelease() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/UserSuppliedConnectionProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/UserSuppliedConnectionProviderImpl.java
index 03c459748f..ff3fbb3f0b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/UserSuppliedConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/UserSuppliedConnectionProviderImpl.java
@@ -1,77 +1,77 @@
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
 import java.sql.Connection;
 import java.sql.SQLException;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
+import org.hibernate.service.UnknownUnwrapTypeException;
 
 /**
  * An implementation of the {@link ConnectionProvider} interface that simply throws an exception when a connection
  * is requested, the assumption being that the application is responsible for handing the connection to use to
  * the session
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class UserSuppliedConnectionProviderImpl implements ConnectionProvider {
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				UserSuppliedConnectionProviderImpl.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				UserSuppliedConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Connection getConnection() throws SQLException {
 		throw new UnsupportedOperationException( "The application must supply JDBC connections" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void closeConnection(Connection conn) throws SQLException {
 		throw new UnsupportedOperationException( "The application must supply JDBC connections" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean supportsAggressiveRelease() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java
index 8a67c92d2e..3603152023 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java
@@ -1,79 +1,79 @@
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
 package org.hibernate.service.jdbc.connections.spi;
 import java.sql.Connection;
 import java.sql.SQLException;
 import org.hibernate.HibernateException;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 import org.hibernate.service.spi.Wrapped;
 
 /**
  * A contract for obtaining JDBC connections.
  * <p/>
  * Implementors might also implement connection pooling.
  * <p/>
  * Implementors should provide a public default constructor.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface ConnectionProvider extends Service, Wrapped {
 	/**
 	 * Obtains a connection for Hibernate use according to the underlying strategy of this provider.
 	 *
 	 * @return The obtained JDBC connection
 	 *
 	 * @throws SQLException Indicates a problem opening a connection
 	 * @throws HibernateException Indicates a problem otherwise obtaining a connection.
 	 */
 	public Connection getConnection() throws SQLException;
 
 	/**
 	 * Release a connection from Hibernate use.
 	 *
 	 * @param conn The JDBC connection to release
 	 *
 	 * @throws SQLException Indicates a problem closing the connection
 	 * @throws HibernateException Indicates a problem otherwise releasing a connection.
 	 */
 	public void closeConnection(Connection conn) throws SQLException;
 
 	/**
 	 * Does this connection provider support aggressive release of JDBC
 	 * connections and re-acquisition of those connections (if need be) later?
 	 * <p/>
 	 * This is used in conjunction with {@link org.hibernate.cfg.Environment#RELEASE_CONNECTIONS}
 	 * to aggressively release JDBC connections.  However, the configured ConnectionProvider
 	 * must support re-acquisition of the same underlying connection for that semantic to work.
 	 * <p/>
 	 * Typically, this is only true in managed environments where a container
 	 * tracks connections by transaction or thread.
 	 *
 	 * Note that JTA semantic depends on the fact that the underlying connection provider does
 	 * support aggressive release.
 	 *
 	 * @return {@code true} if aggressive releasing is supported; {@code false} otherwise.
 	 */
 	public boolean supportsAggressiveRelease();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
index d6f77a0103..8b07b31280 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
@@ -1,49 +1,49 @@
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
 
 import java.util.Map;
 
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link DialectFactory} service
  *
  * @author Steve Ebersole
  */
 public class DialectFactoryInitiator implements BasicServiceInitiator<DialectFactory> {
 	public static final DialectFactoryInitiator INSTANCE = new DialectFactoryInitiator();
 
 	@Override
 	public Class<DialectFactory> getServiceInitiated() {
 		return DialectFactory.class;
 	}
 
 	@Override
 	public DialectFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new DialectFactoryImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
index 21ee769af0..ef2ded0cf8 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
@@ -1,49 +1,49 @@
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
 
 import java.util.Map;
 
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
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
 		return new StandardDialectResolver();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java
index f6e048c5d5..33e9c1dcad 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java
@@ -1,56 +1,56 @@
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
 package org.hibernate.service.jdbc.dialect.spi;
 
 import java.sql.Connection;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * A factory for generating Dialect instances.
  *
  * @author Steve Ebersole
  */
 public interface DialectFactory extends Service {
 	/**
 	 * Builds an appropriate Dialect instance.
 	 * <p/>
 	 * If a dialect is explicitly named in the incoming properties, it should used. Otherwise, it is
 	 * determined by dialect resolvers based on the passed connection.
 	 * <p/>
 	 * An exception is thrown if a dialect was not explicitly set and no resolver could make
 	 * the determination from the given connection.
 	 *
 	 * @param configValues The configuration properties.
 	 * @param connection The configured connection.
 	 *
 	 * @return The appropriate dialect instance.
 	 *
 	 * @throws HibernateException No dialect specified and no resolver could make the determination.
 	 */
 	public Dialect buildDialect(Map configValues, Connection connection) throws HibernateException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectResolver.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectResolver.java
index 32bb10ea08..1adfae2ecf 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectResolver.java
@@ -1,52 +1,52 @@
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
 package org.hibernate.service.jdbc.dialect.spi;
 
 import java.sql.DatabaseMetaData;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.exception.JDBCConnectionException;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Contract for determining the {@link Dialect} to use based on a JDBC {@link java.sql.Connection}.
  *
  * @author Tomoto Shimizu Washio
  * @author Steve Ebersole
  */
 public interface DialectResolver extends Service {
 	/**
 	 * Determine the {@link Dialect} to use based on the given JDBC {@link DatabaseMetaData}.  Implementations are
 	 * expected to return the {@link Dialect} instance to use, or null if the {@link DatabaseMetaData} does not match
 	 * the criteria handled by this impl.
 	 * 
 	 * @param metaData The JDBC metadata.
 	 *
 	 * @return The dialect to use, or null.
 	 *
 	 * @throws JDBCConnectionException Indicates a 'non transient connection problem', which indicates that
 	 * we should stop resolution attempts.
 	 */
 	public Dialect resolveDialect(DatabaseMetaData metaData) throws JDBCConnectionException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
index 40de850b54..2c1c0d2cc3 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceImpl.java
@@ -1,218 +1,218 @@
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
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.Service;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.Manageable;
-import org.hibernate.service.spi.Service;
 import org.hibernate.service.spi.Stoppable;
 import org.jboss.logging.Logger;
 
 /**
  * Standard implementation of JMX services
  *
  * @author Steve Ebersole
  */
 public class JmxServiceImpl implements JmxService, Stoppable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JmxServiceImpl.class.getName());
 
 	public static final String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	public static final String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	public static final String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	public static final String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 
 	private final boolean usePlatformServer;
 	private final String agentId;
 	private final String defaultDomain;
 	private final String sessionFactoryName;
 
 	public JmxServiceImpl(Map configValues) {
 		usePlatformServer = ConfigurationHelper.getBoolean( JMX_PLATFORM_SERVER, configValues );
 		agentId = (String) configValues.get( JMX_AGENT_ID );
 		defaultDomain = (String) configValues.get( JMX_DOMAIN_NAME );
 		sessionFactoryName = ConfigurationHelper.getString(
 				JMX_SF_NAME,
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
 			// MBean server and do *some* work on shutdwon.
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
 
 	public static final String DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 	public static final String OBJ_NAME_TEMPLATE = "%s:sessionFactory=%s,serviceRole=%s,serviceType=%s";
 
 	// todo : should serviceRole come first in ObjectName template?  depends on the groupings we want in the UI.
 	// 		as-is mbeans from each sessionFactory are grouped primarily.
 
 	@Override
 	public void registerService(Manageable service, Class<? extends Service> serviceRole) {
 		final String domain = service.getManagementDomain() == null
 				? DEFAULT_OBJ_NAME_DOMAIN
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
 		catch ( HibernateException e ) {
 			throw e;
 		}
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
 			MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer( defaultDomain );
 			return mbeanServer;
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Unable to start MBeanServer", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
index 237761663d..5cf071b5d5 100644
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
 
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JmxService} service
  *
  * @author Steve Ebersole
  */
 public class JmxServiceInitiator implements BasicServiceInitiator<JmxService> {
 	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final JmxServiceInitiator INSTANCE = new JmxServiceInitiator();
 
 	@Override
 	public Class<JmxService> getServiceInitiated() {
 		return JmxService.class;
 	}
 
 	@Override
 	public JmxService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return ConfigurationHelper.getBoolean( JMX_ENABLED, configurationValues, false )
 				? new JmxServiceImpl( configurationValues )
 				: DisabledJmxServiceImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
index 6fcc1386b2..629cf8faec 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
@@ -1,52 +1,52 @@
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
 package org.hibernate.service.jmx.spi;
 
 import javax.management.ObjectName;
 
+import org.hibernate.service.Service;
 import org.hibernate.service.spi.Manageable;
-import org.hibernate.service.spi.Service;
 
 /**
  * Service providing simplified access to JMX related features needed by Hibernate.
  *
  * @author Steve Ebersole
  */
 public interface JmxService extends Service {
 	/**
 	 * Handles registration of a manageable service.
 	 *
 	 * @param service The manageable service
 	 * @param serviceRole The service's role.
 	 */
 	public void registerService(Manageable service, Class<? extends Service> serviceRole);
 
 	/**
 	 * Registers the given {@code mBean} under the given {@code objectName}
 	 *
 	 * @param objectName The name under which to register the MBean
 	 * @param mBean The MBean to register
 	 */
 	public void registerMBean(ObjectName objectName, Object mBean);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
index ece7f2e05b..fafeb0e640 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
@@ -1,49 +1,49 @@
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
 package org.hibernate.service.jndi.internal;
 
 import java.util.Map;
 
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JndiService} service
  *
  * @author Steve Ebersole
  */
 public class JndiServiceInitiator implements BasicServiceInitiator<JndiService> {
 	public static final JndiServiceInitiator INSTANCE = new JndiServiceInitiator();
 
 	@Override
 	public Class<JndiService> getServiceInitiated() {
 		return JndiService.class;
 	}
 
 	@Override
 	public JndiService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new JndiServiceImpl( configurationValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
index 7d16f1d4ff..55b8a8b417 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
@@ -1,50 +1,50 @@
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
 package org.hibernate.service.jndi.spi;
 
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Service providing simplified access to JNDI related features needed by Hibernate.
  *
  * @author Steve Ebersole
  */
 public interface JndiService extends Service {
 	/**
 	 * Locate an object in JNDI by name
 	 *
 	 * @param jndiName The JNDI name of the object to locate
 	 *
 	 * @return The object found (may be null).
 	 */
 	public Object locate(String jndiName);
 
 	/**
 	 * Binds a value into JNDI by name.
 	 *
 	 * @param jndiName The name under whcih to bind the object
 	 * @param value The value to bind
 	 */
 	public void bind(String jndiName, Object value);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
index 8a6f696b17..b3200900ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/AbstractJtaPlatform.java
@@ -1,133 +1,133 @@
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
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 
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
 	private ServiceRegistry serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistry serviceRegistry) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
index a6320d272c..02622c1cd2 100644
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
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.jndi.JndiHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
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
 	public static final String JTA_PLATFORM = "hibernate.jta.platform";
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JtaPlatformInitiator.class.getName());
 
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
 		Object platform = configVales.get( JTA_PLATFORM );
 		if ( platform == null ) {
 			final String transactionManagerLookupImplName = (String) configVales.get( Environment.TRANSACTION_MANAGER_STRATEGY );
 			if ( transactionManagerLookupImplName != null ) {
                 LOG.deprecatedTransactionManagerStrategy(TransactionManagerLookup.class.getName(),
                                                          Environment.TRANSACTION_MANAGER_STRATEGY,
                                                          JtaPlatform.class.getName(),
                                                          JTA_PLATFORM);
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
 
         LOG.legacyTransactionManagerStrategy(JtaPlatform.class.getName(), JTA_PLATFORM);
 
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
index 4f81875f18..32e303fbcb 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
@@ -1,99 +1,99 @@
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
 
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Defines how we interact with various JTA services on the given platform/environment.
  *
  * @author Steve Ebersole
  */
 public interface JtaPlatform extends Service {
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link TransactionManager} references.
 	 */
 	public static final String CACHE_TM = "hibernate.jta.cacheTransactionManager";
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link UserTransaction} references.
 	 */
 	public static final String CACHE_UT = "hibernate.jta.cacheUserTransaction";
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
index 706bb0e0b3..1b9ed5fe15 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
@@ -1,52 +1,52 @@
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
 package org.hibernate.service.spi;
 
 import java.util.Map;
 
-import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.Service;
 
 /**
  * Responsible for initiating services.
  *
  * @author Steve Ebersole
  */
 public interface BasicServiceInitiator<R extends Service> {
 	/**
 	 * Obtains the service role initiated by this initiator.  Should be unique within a registry
 	 *
 	 * @return The service role.
 	 */
 	public Class<R> getServiceInitiated();
 
 	/**
 	 * Initiates the managed service.
 	 *
 	 * @param configurationValues The configuration values in effect
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(Map configurationValues, ServiceRegistryImplementor registry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java b/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
index f730ad93f0..7fe42b8820 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/Manageable.java
@@ -1,55 +1,55 @@
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
 
 
 /**
- * Optional {@link Service} contract for services which can be managed in JMX
+ * Optional {@link org.hibernate.service.Service} contract for services which can be managed in JMX
  *
  * @author Steve Ebersole
  */
 public interface Manageable {
 	/**
 	 * Get the domain name to be used in registering the management bean.  May be {@code null} to indicate Hibernate's
 	 * default domain ({@code org.hibernate.core}) should be used.
 	 *
 	 * @return The management domain.
 	 */
 	public String getManagementDomain();
 
 	/**
 	 * Allows the service to specify a special 'serviceType' portion of the object name.  {@code null} indicates
 	 * we should use the default scheme, which is to use the name of the service impl class for this purpose.
 	 *
 	 * @return The custom 'serviceType' name.
 	 */
 	public String getManagementServiceType();
 
 	/**
 	 * The the management bean (MBean) for this service.
 	 *
 	 * @return The management bean.
 	 */
 	public Object getManagementBean();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
index 67763b52a1..62941d4a61 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
@@ -1,38 +1,40 @@
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
 
+import org.hibernate.service.ServiceRegistry;
+
 /**
- * Allows services to be injected with the {@link ServiceRegistry} during configuration phase.
+ * Allows services to be injected with the {@link org.hibernate.service.ServiceRegistry} during configuration phase.
  *
  * @author Steve Ebersole
  */
 public interface ServiceRegistryAwareService {
 	/**
 	 * Callback to inject the registry.
 	 *
 	 * @param serviceRegistry The registry
 	 */
 	public void injectServices(ServiceRegistry serviceRegistry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImplementor.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java
similarity index 74%
rename from hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImplementor.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java
index 27b5cbdf8b..70179376d5 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryImplementor.java
@@ -1,35 +1,58 @@
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
-package org.hibernate.service.internal;
+package org.hibernate.service.spi;
 
-import org.hibernate.service.spi.Service;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.Service;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.proxy.ServiceProxyTargetSource;
 
 /**
  * @author Steve Ebersole
  */
 public interface ServiceRegistryImplementor extends ServiceRegistry, ServiceProxyTargetSource {
 	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole);
+
+	public void destroy();
+
+	public final class ServiceBinding<R> {
+		private final R proxy;
+		private R target;
+
+		public ServiceBinding(R proxy) {
+			this.proxy = proxy;
+		}
+
+		public R getProxy() {
+			return proxy;
+		}
+
+		public R getTarget() {
+			return target;
+		}
+
+		public void setTarget(R target) {
+			this.target = target;
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java
new file mode 100644
index 0000000000..78e2fbb00f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistry.java
@@ -0,0 +1,34 @@
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
+package org.hibernate.service.spi;
+
+/**
+ * Acts as a {@link org.hibernate.service.Service} in the {@link org.hibernate.service.internal.BasicServiceRegistryImpl} whose function is as a factory for
+ * {@link org.hibernate.service.internal.SessionFactoryServiceRegistryImpl} implementations.
+ *
+ * @author Steve Ebersole
+ */
+public interface SessionFactoryServiceRegistry extends ServiceRegistryImplementor {
+	// todo : add regsitration of service initiator
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
new file mode 100644
index 0000000000..8d5106ee5d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
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
+package org.hibernate.service.spi;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.service.Service;
+import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
+
+/**
+ * Contract for builder of {@link SessionFactoryServiceRegistry} instances.  Defined as a service to
+ * "sit inside" the {@link org.hibernate.service.BasicServiceRegistry}.
+ *
+ * @author Steve Ebersole
+ */
+public interface SessionFactoryServiceRegistryFactory extends Service {
+	/**
+	 * Create the registry.
+	 *
+	 * @todo : fully expect this signature to change!
+	 *
+	 * @param sessionFactory The (in flux) session factory.  Generally this is useful for grabbing a reference for later
+	 * 		use.  However, care should be taken when invoking on the session factory until after it has been fully
+	 * 		initialized.
+	 * @param configuration The configuration object.
+	 *
+	 * @return The registry
+	 */
+	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
+			SessionFactoryImplementor sessionFactory,
+			Configuration configuration);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyFactory.java
index cc90536ca7..9d81709a1e 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyFactory.java
@@ -1,43 +1,43 @@
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
 package org.hibernate.service.spi.proxy;
 
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 
 /**
  * Contract for creating proxy instances for {@link Service} instances.
  *
  * @author Steve Ebersole
  */
 public interface ServiceProxyFactory {
 	/**
 	 * Create a proxy for the given service role.
 	 *
 	 * @param serviceRole The service role for which to create a proxy.
 	 * @param <T> The type of the service
 	 *
 	 * @return The service proxy
 	 */
 	public <T extends Service> T makeProxy(Class<T> serviceRole);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyTargetSource.java b/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyTargetSource.java
index e7b50c0b7f..45419fa2f6 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyTargetSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyTargetSource.java
@@ -1,46 +1,46 @@
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
 package org.hibernate.service.spi.proxy;
 
-import org.hibernate.service.spi.Service;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.Service;
+import org.hibernate.service.ServiceRegistry;
 
 /**
  * Additional contract for service proxies.  This allows the proxies access to their actual service instances.
  *
  * @author Steve Ebersole
  */
 public interface ServiceProxyTargetSource extends ServiceRegistry {
 	/**
 	 * Retrieve a service by role.  Unlike {@link ServiceRegistry#getService}, this version will never return a proxy.
 	 *
 	 * @param serviceRole The service role
 	 * @param <R> The service role type
 	 *
 	 * @return The requested service.
 	 *
-	 * @throws org.hibernate.service.spi.UnknownServiceException Indicates the service was not known.
+	 * @throws org.hibernate.service.UnknownServiceException Indicates the service was not known.
 	 */
 	public <R extends Service> R getServiceInternal(Class<R> serviceRole);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
index f951841469..3bb2bced11 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
@@ -1,720 +1,720 @@
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
 package org.hibernate.stat.internal;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.atomic.AtomicLong;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateLogger;
 import org.hibernate.cache.Region;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.service.spi.Service;
+import org.hibernate.service.Service;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.stat.EntityStatistics;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * Implementation of {@link org.hibernate.stat.Statistics} based on the {@link java.util.concurrent} package.
  *
  * @author Alex Snaps
  */
 @SuppressWarnings({ "unchecked" })
 public class ConcurrentStatisticsImpl implements StatisticsImplementor, Service {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ConcurrentStatisticsImpl.class.getName());
 
 	private SessionFactoryImplementor sessionFactory;
 
 	private volatile boolean isStatisticsEnabled;
 	private volatile long startTime;
 	private AtomicLong sessionOpenCount = new AtomicLong();
 	private AtomicLong sessionCloseCount = new AtomicLong();
 	private AtomicLong flushCount = new AtomicLong();
 	private AtomicLong connectCount = new AtomicLong();
 
 	private AtomicLong prepareStatementCount = new AtomicLong();
 	private AtomicLong closeStatementCount = new AtomicLong();
 
 	private AtomicLong entityLoadCount = new AtomicLong();
 	private AtomicLong entityUpdateCount = new AtomicLong();
 	private AtomicLong entityInsertCount = new AtomicLong();
 	private AtomicLong entityDeleteCount = new AtomicLong();
 	private AtomicLong entityFetchCount = new AtomicLong();
 	private AtomicLong collectionLoadCount = new AtomicLong();
 	private AtomicLong collectionUpdateCount = new AtomicLong();
 	private AtomicLong collectionRemoveCount = new AtomicLong();
 	private AtomicLong collectionRecreateCount = new AtomicLong();
 	private AtomicLong collectionFetchCount = new AtomicLong();
 
 	private AtomicLong secondLevelCacheHitCount = new AtomicLong();
 	private AtomicLong secondLevelCacheMissCount = new AtomicLong();
 	private AtomicLong secondLevelCachePutCount = new AtomicLong();
 
 	private AtomicLong queryExecutionCount = new AtomicLong();
 	private AtomicLong queryExecutionMaxTime = new AtomicLong();
 	private volatile String queryExecutionMaxTimeQueryString;
 	private AtomicLong queryCacheHitCount = new AtomicLong();
 	private AtomicLong queryCacheMissCount = new AtomicLong();
 	private AtomicLong queryCachePutCount = new AtomicLong();
 
 	private AtomicLong committedTransactionCount = new AtomicLong();
 	private AtomicLong transactionCount = new AtomicLong();
 
 	private AtomicLong optimisticFailureCount = new AtomicLong();
 
 	/**
 	 * second level cache statistics per region
 	 */
 	private final ConcurrentMap secondLevelCacheStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per name
 	 */
 	private final ConcurrentMap entityStatistics = new ConcurrentHashMap();
 	/**
 	 * collection statistics per name
 	 */
 	private final ConcurrentMap collectionStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per query string (HQL or SQL)
 	 */
 	private final ConcurrentMap queryStatistics = new ConcurrentHashMap();
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ConcurrentStatisticsImpl() {
 		clear();
 	}
 
 	public ConcurrentStatisticsImpl(SessionFactoryImplementor sessionFactory) {
 		clear();
 		this.sessionFactory = sessionFactory;
 	}
 
 	/**
 	 * reset all statistics
 	 */
 	public void clear() {
 		secondLevelCacheHitCount.set( 0 );
 		secondLevelCacheMissCount.set( 0 );
 		secondLevelCachePutCount.set( 0 );
 
 		sessionCloseCount.set( 0 );
 		sessionOpenCount.set( 0 );
 		flushCount.set( 0 );
 		connectCount.set( 0 );
 
 		prepareStatementCount.set( 0 );
 		closeStatementCount.set( 0 );
 
 		entityDeleteCount.set( 0 );
 		entityInsertCount.set( 0 );
 		entityUpdateCount.set( 0 );
 		entityLoadCount.set( 0 );
 		entityFetchCount.set( 0 );
 
 		collectionRemoveCount.set( 0 );
 		collectionUpdateCount.set( 0 );
 		collectionRecreateCount.set( 0 );
 		collectionLoadCount.set( 0 );
 		collectionFetchCount.set( 0 );
 
 		queryExecutionCount.set( 0 );
 		queryCacheHitCount.set( 0 );
 		queryExecutionMaxTime.set( 0 );
 		queryExecutionMaxTimeQueryString = null;
 		queryCacheMissCount.set( 0 );
 		queryCachePutCount.set( 0 );
 
 		transactionCount.set( 0 );
 		committedTransactionCount.set( 0 );
 
 		optimisticFailureCount.set( 0 );
 
 		secondLevelCacheStatistics.clear();
 		entityStatistics.clear();
 		collectionStatistics.clear();
 		queryStatistics.clear();
 
 		startTime = System.currentTimeMillis();
 	}
 
 	public void openSession() {
 		sessionOpenCount.getAndIncrement();
 	}
 
 	public void closeSession() {
 		sessionCloseCount.getAndIncrement();
 	}
 
 	public void flush() {
 		flushCount.getAndIncrement();
 	}
 
 	public void connect() {
 		connectCount.getAndIncrement();
 	}
 
 	public void loadEntity(String entityName) {
 		entityLoadCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementLoadCount();
 	}
 
 	public void fetchEntity(String entityName) {
 		entityFetchCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementFetchCount();
 	}
 
 	/**
 	 * find entity statistics per name
 	 *
 	 * @param entityName entity name
 	 *
 	 * @return EntityStatistics object
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) entityStatistics.get( entityName );
 		if ( es == null ) {
 			es = new ConcurrentEntityStatisticsImpl( entityName );
 			ConcurrentEntityStatisticsImpl previous;
 			if ( ( previous = (ConcurrentEntityStatisticsImpl) entityStatistics.putIfAbsent(
 					entityName, es
 			) ) != null ) {
 				es = previous;
 			}
 		}
 		return es;
 	}
 
 	public void updateEntity(String entityName) {
 		entityUpdateCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementUpdateCount();
 	}
 
 	public void insertEntity(String entityName) {
 		entityInsertCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementInsertCount();
 	}
 
 	public void deleteEntity(String entityName) {
 		entityDeleteCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementDeleteCount();
 	}
 
 	/**
 	 * Get collection statistics per role
 	 *
 	 * @param role collection role
 	 *
 	 * @return CollectionStatistics
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		ConcurrentCollectionStatisticsImpl cs = (ConcurrentCollectionStatisticsImpl) collectionStatistics.get( role );
 		if ( cs == null ) {
 			cs = new ConcurrentCollectionStatisticsImpl( role );
 			ConcurrentCollectionStatisticsImpl previous;
 			if ( ( previous = (ConcurrentCollectionStatisticsImpl) collectionStatistics.putIfAbsent(
 					role, cs
 			) ) != null ) {
 				cs = previous;
 			}
 		}
 		return cs;
 	}
 
 	public void loadCollection(String role) {
 		collectionLoadCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementLoadCount();
 	}
 
 	public void fetchCollection(String role) {
 		collectionFetchCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementFetchCount();
 	}
 
 	public void updateCollection(String role) {
 		collectionUpdateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementUpdateCount();
 	}
 
 	public void recreateCollection(String role) {
 		collectionRecreateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRecreateCount();
 	}
 
 	public void removeCollection(String role) {
 		collectionRemoveCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRemoveCount();
 	}
 
 	/**
 	 * Second level cache statistics per region
 	 *
 	 * @param regionName region name
 	 *
 	 * @return SecondLevelCacheStatistics
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		ConcurrentSecondLevelCacheStatisticsImpl slcs
 				= (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.get( regionName );
 		if ( slcs == null ) {
 			if ( sessionFactory == null ) {
 				return null;
 			}
 			Region region = sessionFactory.getSecondLevelCacheRegion( regionName );
 			if ( region == null ) {
 				return null;
 			}
 			slcs = new ConcurrentSecondLevelCacheStatisticsImpl( region );
 			ConcurrentSecondLevelCacheStatisticsImpl previous;
 			if ( ( previous = (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.putIfAbsent(
 					regionName, slcs
 			) ) != null ) {
 				slcs = previous;
 			}
 		}
 		return slcs;
 	}
 
 	public void secondLevelCachePut(String regionName) {
 		secondLevelCachePutCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementPutCount();
 	}
 
 	public void secondLevelCacheHit(String regionName) {
 		secondLevelCacheHitCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementHitCount();
 	}
 
 	public void secondLevelCacheMiss(String regionName) {
 		secondLevelCacheMissCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementMissCount();
 	}
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public void queryExecuted(String hql, int rows, long time) {
         LOG.hql(hql, Long.valueOf(time), Long.valueOf(rows));
 		queryExecutionCount.getAndIncrement();
 		boolean isLongestQuery = false;
 		for ( long old = queryExecutionMaxTime.get();
 			  ( isLongestQuery = time > old ) && ( !queryExecutionMaxTime.compareAndSet( old, time ) );
 			  old = queryExecutionMaxTime.get() ) {
 			// nothing to do here given the odd loop structure...
 		}
 		if ( isLongestQuery ) {
 			queryExecutionMaxTimeQueryString = hql;
 		}
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.executed( rows, time );
 		}
 	}
 
 	public void queryCacheHit(String hql, String regionName) {
 		queryCacheHitCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheHitCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementHitCount();
 	}
 
 	public void queryCacheMiss(String hql, String regionName) {
 		queryCacheMissCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheMissCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementMissCount();
 	}
 
 	public void queryCachePut(String hql, String regionName) {
 		queryCachePutCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCachePutCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementPutCount();
 	}
 
 	/**
 	 * Query statistics from query string (HQL or SQL)
 	 *
 	 * @param queryString query string
 	 *
 	 * @return QueryStatistics
 	 */
 	public QueryStatistics getQueryStatistics(String queryString) {
 		ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) queryStatistics.get( queryString );
 		if ( qs == null ) {
 			qs = new ConcurrentQueryStatisticsImpl( queryString );
 			ConcurrentQueryStatisticsImpl previous;
 			if ( ( previous = (ConcurrentQueryStatisticsImpl) queryStatistics.putIfAbsent(
 					queryString, qs
 			) ) != null ) {
 				qs = previous;
 			}
 		}
 		return qs;
 	}
 
 	/**
 	 * @return entity deletion count
 	 */
 	public long getEntityDeleteCount() {
 		return entityDeleteCount.get();
 	}
 
 	/**
 	 * @return entity insertion count
 	 */
 	public long getEntityInsertCount() {
 		return entityInsertCount.get();
 	}
 
 	/**
 	 * @return entity load (from DB)
 	 */
 	public long getEntityLoadCount() {
 		return entityLoadCount.get();
 	}
 
 	/**
 	 * @return entity fetch (from DB)
 	 */
 	public long getEntityFetchCount() {
 		return entityFetchCount.get();
 	}
 
 	/**
 	 * @return entity update
 	 */
 	public long getEntityUpdateCount() {
 		return entityUpdateCount.get();
 	}
 
 	public long getQueryExecutionCount() {
 		return queryExecutionCount.get();
 	}
 
 	public long getQueryCacheHitCount() {
 		return queryCacheHitCount.get();
 	}
 
 	public long getQueryCacheMissCount() {
 		return queryCacheMissCount.get();
 	}
 
 	public long getQueryCachePutCount() {
 		return queryCachePutCount.get();
 	}
 
 	/**
 	 * @return flush
 	 */
 	public long getFlushCount() {
 		return flushCount.get();
 	}
 
 	/**
 	 * @return session connect
 	 */
 	public long getConnectCount() {
 		return connectCount.get();
 	}
 
 	/**
 	 * @return second level cache hit
 	 */
 	public long getSecondLevelCacheHitCount() {
 		return secondLevelCacheHitCount.get();
 	}
 
 	/**
 	 * @return second level cache miss
 	 */
 	public long getSecondLevelCacheMissCount() {
 		return secondLevelCacheMissCount.get();
 	}
 
 	/**
 	 * @return second level cache put
 	 */
 	public long getSecondLevelCachePutCount() {
 		return secondLevelCachePutCount.get();
 	}
 
 	/**
 	 * @return session closing
 	 */
 	public long getSessionCloseCount() {
 		return sessionCloseCount.get();
 	}
 
 	/**
 	 * @return session opening
 	 */
 	public long getSessionOpenCount() {
 		return sessionOpenCount.get();
 	}
 
 	/**
 	 * @return collection loading (from DB)
 	 */
 	public long getCollectionLoadCount() {
 		return collectionLoadCount.get();
 	}
 
 	/**
 	 * @return collection fetching (from DB)
 	 */
 	public long getCollectionFetchCount() {
 		return collectionFetchCount.get();
 	}
 
 	/**
 	 * @return collection update
 	 */
 	public long getCollectionUpdateCount() {
 		return collectionUpdateCount.get();
 	}
 
 	/**
 	 * @return collection removal
 	 *         FIXME: even if isInverse="true"?
 	 */
 	public long getCollectionRemoveCount() {
 		return collectionRemoveCount.get();
 	}
 
 	/**
 	 * @return collection recreation
 	 */
 	public long getCollectionRecreateCount() {
 		return collectionRecreateCount.get();
 	}
 
 	/**
 	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
 	 */
 	public long getStartTime() {
 		return startTime;
 	}
 
 	/**
 	 * log in info level the main statistics
 	 */
 	public void logSummary() {
         LOG.loggingStatistics();
         LOG.startTime(startTime);
         LOG.sessionsOpened(sessionOpenCount.get());
         LOG.sessionsClosed(sessionCloseCount.get());
         LOG.transactions(transactionCount.get());
         LOG.successfulTransactions(committedTransactionCount.get());
         LOG.optimisticLockFailures(optimisticFailureCount.get());
         LOG.flushes(flushCount.get());
         LOG.connectionsObtained(connectCount.get());
         LOG.statementsPrepared(prepareStatementCount.get());
         LOG.statementsClosed(closeStatementCount.get());
         LOG.secondLevelCachePuts(secondLevelCachePutCount.get());
         LOG.secondLevelCacheHits(secondLevelCacheHitCount.get());
         LOG.secondLevelCacheMisses(secondLevelCacheMissCount.get());
         LOG.entitiesLoaded(entityLoadCount.get());
         LOG.entitiesUpdated(entityUpdateCount.get());
         LOG.entitiesInserted(entityInsertCount.get());
         LOG.entitiesDeleted(entityDeleteCount.get());
         LOG.entitiesFetched(entityFetchCount.get());
         LOG.collectionsLoaded(collectionLoadCount.get());
         LOG.collectionsUpdated(collectionUpdateCount.get());
         LOG.collectionsRemoved(collectionRemoveCount.get());
         LOG.collectionsRecreated(collectionRecreateCount.get());
         LOG.collectionsFetched(collectionFetchCount.get());
         LOG.queriesExecuted(queryExecutionCount.get());
         LOG.queryCachePuts(queryCachePutCount.get());
         LOG.queryCacheHits(queryCacheHitCount.get());
         LOG.queryCacheMisses(queryCacheMissCount.get());
         LOG.maxQueryTime(queryExecutionMaxTime.get());
 	}
 
 	/**
 	 * Are statistics logged
 	 */
 	public boolean isStatisticsEnabled() {
 		return isStatisticsEnabled;
 	}
 
 	/**
 	 * Enable statistics logs (this is a dynamic parameter)
 	 */
 	public void setStatisticsEnabled(boolean b) {
 		isStatisticsEnabled = b;
 	}
 
 	/**
 	 * @return Returns the max query execution time,
 	 *         for all queries
 	 */
 	public long getQueryExecutionMaxTime() {
 		return queryExecutionMaxTime.get();
 	}
 
 	/**
 	 * Get all executed query strings
 	 */
 	public String[] getQueries() {
 		return ArrayHelper.toStringArray( queryStatistics.keySet() );
 	}
 
 	/**
 	 * Get the names of all entities
 	 */
 	public String[] getEntityNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( entityStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllClassMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get the names of all collection roles
 	 */
 	public String[] getCollectionRoleNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( collectionStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllCollectionMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get all second-level cache region names
 	 */
 	public String[] getSecondLevelCacheRegionNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( secondLevelCacheStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllSecondLevelCacheRegions().keySet() );
 		}
 	}
 
 	public void endTransaction(boolean success) {
 		transactionCount.getAndIncrement();
 		if ( success ) {
 			committedTransactionCount.getAndIncrement();
 		}
 	}
 
 	public long getSuccessfulTransactionCount() {
 		return committedTransactionCount.get();
 	}
 
 	public long getTransactionCount() {
 		return transactionCount.get();
 	}
 
 	public void closeStatement() {
 		closeStatementCount.getAndIncrement();
 	}
 
 	public void prepareStatement() {
 		prepareStatementCount.getAndIncrement();
 	}
 
 	public long getCloseStatementCount() {
 		return closeStatementCount.get();
 	}
 
 	public long getPrepareStatementCount() {
 		return prepareStatementCount.get();
 	}
 
 	public void optimisticFailure(String entityName) {
 		optimisticFailureCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementOptimisticFailureCount();
 	}
 
 	public long getOptimisticFailureCount() {
 		return optimisticFailureCount.get();
 	}
 
 	@Override
     public String toString() {
 		return new StringBuilder()
 				.append( "Statistics[" )
 				.append( "start time=" ).append( startTime )
 				.append( ",sessions opened=" ).append( sessionOpenCount )
 				.append( ",sessions closed=" ).append( sessionCloseCount )
 				.append( ",transactions=" ).append( transactionCount )
 				.append( ",successful transactions=" ).append( committedTransactionCount )
 				.append( ",optimistic lock failures=" ).append( optimisticFailureCount )
 				.append( ",flushes=" ).append( flushCount )
 				.append( ",connections obtained=" ).append( connectCount )
 				.append( ",statements prepared=" ).append( prepareStatementCount )
 				.append( ",statements closed=" ).append( closeStatementCount )
 				.append( ",second level cache puts=" ).append( secondLevelCachePutCount )
 				.append( ",second level cache hits=" ).append( secondLevelCacheHitCount )
 				.append( ",second level cache misses=" ).append( secondLevelCacheMissCount )
 				.append( ",entities loaded=" ).append( entityLoadCount )
 				.append( ",entities updated=" ).append( entityUpdateCount )
 				.append( ",entities inserted=" ).append( entityInsertCount )
 				.append( ",entities deleted=" ).append( entityDeleteCount )
 				.append( ",entities fetched=" ).append( entityFetchCount )
 				.append( ",collections loaded=" ).append( collectionLoadCount )
 				.append( ",collections updated=" ).append( collectionUpdateCount )
 				.append( ",collections removed=" ).append( collectionRemoveCount )
 				.append( ",collections recreated=" ).append( collectionRecreateCount )
 				.append( ",collections fetched=" ).append( collectionFetchCount )
 				.append( ",queries executed to database=" ).append( queryExecutionCount )
 				.append( ",query cache puts=" ).append( queryCachePutCount )
 				.append( ",query cache hits=" ).append( queryCacheHitCount )
 				.append( ",query cache misses=" ).append( queryCacheMissCount )
 				.append( ",max query time=" ).append( queryExecutionMaxTime )
 				.append( ']' )
 				.toString();
 	}
 
 	public String getQueryExecutionMaxTimeQueryString() {
 		return queryExecutionMaxTimeQueryString;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
index f4e9b1e7d0..5cac2329da 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
@@ -1,174 +1,174 @@
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
 package org.hibernate.id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class SequenceHiLoGeneratorNoIncrementTest extends BaseUnitTestCase {
 	private static final String TEST_SEQUENCE = "test_sequence";
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private SequenceHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( SequenceGenerator.SEQUENCE, TEST_SEQUENCE );
 		properties.setProperty( SequenceHiLoGenerator.MAX_LO, "0" ); // JPA allocationSize of 1
 		properties.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					@Override
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					@Override
 					protected NamingStrategy getNamingStrategy() {
 						return cfg.getNamingStrategy();
 					}
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new SequenceHiLoGenerator();
 		generator.configure( Hibernate.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		SessionImpl session = (SessionImpl) sessionFactory.openSession();
 		session.beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// historically the hilo generators skipped the initial block of values;
 		// 		so the first generated id value is maxlo + 1, here be 4
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		// which should also perform the first read on the sequence which should set it to its "start with" value (1)
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 3L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 4L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 5L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
index ab072fb57b..a0e9885080 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
@@ -1,176 +1,176 @@
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
 package org.hibernate.id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class SequenceHiLoGeneratorTest extends BaseUnitTestCase {
 	private static final String TEST_SEQUENCE = "test_sequence";
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private SequenceHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( SequenceGenerator.SEQUENCE, TEST_SEQUENCE );
 		properties.setProperty( SequenceHiLoGenerator.MAX_LO, "3" );
 		properties.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					@Override
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					@Override
 					protected NamingStrategy getNamingStrategy() {
 						return cfg.getNamingStrategy();
 					}
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new SequenceHiLoGenerator();
 		generator.configure( Hibernate.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		SessionImpl session = (SessionImpl) sessionFactory.openSession();
 		session.beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// historically the hilo generators skipped the initial block of values;
 		// 		so the first generated id value is maxlo + 1, here be 4
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		// which should also perform the first read on the sequence which should set it to its "start with" value (1)
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 6L, generatedValue.longValue() );
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 7L, generatedValue.longValue() );
 		// unlike the newer strategies, the db value will not get update here.  It gets updated on the next invocation
 		// 	after a clock over
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 8L, generatedValue.longValue() );
 		// this should force an increment in the sequence value
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
index b4ca8ad832..e45b00821c 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
@@ -1,180 +1,180 @@
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
 package org.hibernate.id;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.impl.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class TableHiLoGeneratorTest extends BaseUnitTestCase {
 	private static final String GEN_TABLE = "generator_table";
 	private static final String GEN_COLUMN = TableHiLoGenerator.DEFAULT_COLUMN_NAME;
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private TableHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( TableGenerator.TABLE, GEN_TABLE );
 		properties.setProperty( TableGenerator.COLUMN, GEN_COLUMN );
 		properties.setProperty( TableHiLoGenerator.MAX_LO, "3" );
 		properties.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					@Override
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					@Override
 					protected NamingStrategy getNamingStrategy() {
 						return cfg.getNamingStrategy();
 					}
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new TableHiLoGenerator();
 		generator.configure( Hibernate.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[1],
 						null
 				)
 		);
 
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		SessionImpl session = (SessionImpl) sessionFactory.openSession();
 		session.beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 2L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 2L, extractInDatabaseValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractInDatabaseValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select " + GEN_COLUMN + " from " + GEN_TABLE );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java
index 72c0126c50..50665f8b53 100644
--- a/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/subclassProxyInterface/SubclassProxyInterfaceTest.java
@@ -1,48 +1,49 @@
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
 package org.hibernate.subclassProxyInterface;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class SubclassProxyInterfaceTest extends BaseUnitTestCase {
 	@Test
 	public void testSubclassProxyInterfaces() {
         final Configuration cfg = new Configuration()
 				.setProperty( Environment.DIALECT, H2Dialect.class.getName() )
 				.addClass( Person.class );
 		ServiceRegistry serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		cfg.buildSessionFactory( serviceRegistry ).close();
 		ServiceRegistryBuilder.destroy( serviceRegistry );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/ConfigurationTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/ConfigurationTest.java
index 357d5f9bc9..a49fdaa4a2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/ConfigurationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/ConfigurationTest.java
@@ -1,155 +1,156 @@
 //$Id$
 package org.hibernate.test.annotations;
 import org.hibernate.HibernateException;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * @author Emmanuel Bernard
  */
 public class ConfigurationTest extends junit.framework.TestCase {
 	private ServiceRegistry serviceRegistry;
 
 	protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	protected void tearDown() {
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	public void testDeclarativeMix() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.configure( "org/hibernate/test/annotations/hibernate.cfg.xml" );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		SessionFactory sf = cfg.buildSessionFactory( serviceRegistry );
 		assertNotNull( sf );
 		Session s = sf.openSession();
 		Transaction tx = s.beginTransaction();
 		Query q = s.createQuery( "from Boat" );
 		assertEquals( 0, q.list().size() );
 		q = s.createQuery( "from Plane" );
 		assertEquals( 0, q.list().size() );
 		tx.commit();
 		s.close();
 		sf.close();
 	}
 
 	public void testIgnoringHbm() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.configure( "org/hibernate/test/annotations/hibernate.cfg.xml" );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AnnotationConfiguration.ARTEFACT_PROCESSING_ORDER, "class" );
 		SessionFactory sf = cfg.buildSessionFactory( serviceRegistry );
 		assertNotNull( sf );
 		Session s = sf.openSession();
 		Transaction tx = s.beginTransaction();
 		Query q;
 		try {
 			s.createQuery( "from Boat" ).list();
 			fail( "Boat should not be mapped" );
 		}
 		catch (HibernateException e) {
 			//all good
 		}
 		q = s.createQuery( "from Plane" );
 		assertEquals( 0, q.list().size() );
 		tx.commit();
 		s.close();
 		sf.close();
 	}
 
 	public void testPrecedenceHbm() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.configure( "org/hibernate/test/annotations/hibernate.cfg.xml" );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAnnotatedClass( Boat.class );
 		SessionFactory sf = cfg.buildSessionFactory( serviceRegistry );
 		assertNotNull( sf );
 		Session s = sf.openSession();
 		s.getTransaction().begin();
 		Boat boat = new Boat();
 		boat.setSize( 12 );
 		boat.setWeight( 34 );
 		s.persist( boat );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boat = (Boat) s.get( Boat.class, boat.getId() );
 		assertTrue( "Annotation has precedence", 34 != boat.getWeight() );
 		s.delete( boat );
 		//s.getTransaction().commit();
 		tx.commit();
 		s.close();
 		sf.close();
 	}
 
 	public void testPrecedenceAnnotation() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.configure( "org/hibernate/test/annotations/hibernate.cfg.xml" );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.setProperty( AnnotationConfiguration.ARTEFACT_PROCESSING_ORDER, "class, hbm" );
 		cfg.addAnnotatedClass( Boat.class );
 		SessionFactory sf = cfg.buildSessionFactory( serviceRegistry );
 		assertNotNull( sf );
 		Session s = sf.openSession();
 		s.getTransaction().begin();
 		Boat boat = new Boat();
 		boat.setSize( 12 );
 		boat.setWeight( 34 );
 		s.persist( boat );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boat = (Boat) s.get( Boat.class, boat.getId() );
 		assertTrue( "Annotation has precedence", 34 == boat.getWeight() );
 		s.delete( boat );
 		tx.commit();
 		s.close();
 		sf.close();
 	}
 
 	public void testHbmWithSubclassExtends() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.configure( "org/hibernate/test/annotations/hibernate.cfg.xml" );
 		cfg.addClass( Ferry.class );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		SessionFactory sf = cfg.buildSessionFactory( serviceRegistry );
 		assertNotNull( sf );
 		Session s = sf.openSession();
 		Transaction tx = s.beginTransaction();
 		Query q = s.createQuery( "from Ferry" );
 		assertEquals( 0, q.list().size() );
 		q = s.createQuery( "from Plane" );
 		assertEquals( 0, q.list().size() );
 		tx.commit();
 		s.close();
 		sf.close();
 	}
 
 	public void testAnnReferencesHbm() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.configure( "org/hibernate/test/annotations/hibernate.cfg.xml" );
 		cfg.addAnnotatedClass( Port.class );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		SessionFactory sf = cfg.buildSessionFactory( serviceRegistry );
 		assertNotNull( sf );
 		Session s = sf.openSession();
 		Transaction tx = s.beginTransaction();
 		Query q = s.createQuery( "from Boat" );
 		assertEquals( 0, q.list().size() );
 		q = s.createQuery( "from Port" );
 		assertEquals( 0, q.list().size() );
 		tx.commit();
 		s.close();
 		sf.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/SafeMappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/SafeMappingTest.java
index 179dd9c4ab..db1d0c8b68 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/SafeMappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/SafeMappingTest.java
@@ -1,32 +1,32 @@
 //$Id$
 package org.hibernate.test.annotations;
 import org.hibernate.AnnotationException;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SafeMappingTest extends junit.framework.TestCase {
 	public void testDeclarativeMix() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.addAnnotatedClass( IncorrectEntity.class );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		ServiceRegistry serviceRegistry = null;
 		try {
 			serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 			cfg.buildSessionFactory( serviceRegistry );
 			fail( "Entity wo id should fail" );
 		}
 		catch (AnnotationException e) {
 			//success
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/SecuredBindingTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/SecuredBindingTest.java
index e4921f0b6d..af454c2c00 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/SecuredBindingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/SecuredBindingTest.java
@@ -1,55 +1,56 @@
 //$Id$
 package org.hibernate.test.annotations;
 
 import java.util.Properties;
 import junit.framework.TestCase;
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SecuredBindingTest extends TestCase {
 
 	public SecuredBindingTest(String x) {
 		super( x );
 	}
 
 	public void testConfigurationMethods() throws Exception {
 		AnnotationConfiguration ac = new AnnotationConfiguration();
 		Properties p = new Properties();
 		p.put( Environment.DIALECT, "org.hibernate.dialect.HSQLDialect" );
 		p.put( "hibernate.connection.driver_class", "org.hsqldb.jdbcDrive" );
 		p.put( "hibernate.connection.url", "jdbc:hsqldb:." );
 		p.put( "hibernate.connection.username", "sa" );
 		p.put( "hibernate.connection.password", "" );
 		p.put( "hibernate.show_sql", "true" );
 		ac.setProperties( p );
 		ac.addAnnotatedClass( Plane.class );
 		SessionFactory sf;
 		ServiceRegistry serviceRegistry = null;
 		try {
 			serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( p );
 			sf = ac.buildSessionFactory( serviceRegistry );
 			try {
 				sf.close();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "Driver property overriding should work" );
 		}
 		catch (HibernateException he) {
 			//success
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
index bdfe9207e0..5b90b5e026 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/jpa/AccessMappingTest.java
@@ -1,218 +1,219 @@
 //$Id$
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
  */
 package org.hibernate.test.annotations.access.jpa;
 import junit.framework.TestCase;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.property.BasicPropertyAccessor;
 import org.hibernate.property.DirectPropertyAccessor;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.PojoEntityTuplizer;
 
 
 /**
  * Tests verifying the correct behaviour for the usage of {@code @javax.persistence.Access}.
  *
  * @author Hardy Ferentschik
  */
 public class AccessMappingTest extends TestCase {
 
 	private ServiceRegistry serviceRegistry;
 
 	protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	protected void tearDown() {
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	public void testInconsistentAnnotationPlacement() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.addAnnotatedClass( Course1.class );
 		cfg.addAnnotatedClass( Student.class );
 		try {
 			cfg.buildSessionFactory( serviceRegistry );
 			fail( "@Id and @OneToMany are not placed consistently in test entities. SessionFactory creation should fail." );
 		}
 		catch ( MappingException e ) {
 			// success
 		}
 	}
 
 	public void testFieldAnnotationPlacement() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		Class<?> classUnderTest = Course6.class;
 		cfg.addAnnotatedClass( classUnderTest );
 		cfg.addAnnotatedClass( Student.class );
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Field access should be used.",
 				tuplizer.getIdentifierGetter() instanceof DirectPropertyAccessor.DirectGetter
 		);
 	}
 
 	public void testPropertyAnnotationPlacement() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		Class<?> classUnderTest = Course7.class;
 		cfg.addAnnotatedClass( classUnderTest );
 		cfg.addAnnotatedClass( Student.class );
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Property access should be used.",
 				tuplizer.getIdentifierGetter() instanceof BasicPropertyAccessor.BasicGetter
 		);
 	}
 
 	public void testExplicitPropertyAccessAnnotationsOnProperty() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		Class<?> classUnderTest = Course2.class;
 		cfg.addAnnotatedClass( classUnderTest );
 		cfg.addAnnotatedClass( Student.class );
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Property access should be used.",
 				tuplizer.getIdentifierGetter() instanceof BasicPropertyAccessor.BasicGetter
 		);
 	}
 
 	public void testExplicitPropertyAccessAnnotationsOnField() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.addAnnotatedClass( Course4.class );
 		cfg.addAnnotatedClass( Student.class );
 		try {
 			cfg.buildSessionFactory( serviceRegistry );
 			fail( "@Id and @OneToMany are not placed consistently in test entities. SessionFactory creation should fail." );
 		}
 		catch ( MappingException e ) {
 			// success
 		}
 	}
 
 	public void testExplicitPropertyAccessAnnotationsWithHibernateStyleOverride() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		Class<?> classUnderTest = Course3.class;
 		cfg.addAnnotatedClass( classUnderTest );
 		cfg.addAnnotatedClass( Student.class );
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Field access should be used.",
 				tuplizer.getIdentifierGetter() instanceof DirectPropertyAccessor.DirectGetter
 		);
 
 		assertTrue(
 				"Property access should be used.",
 				tuplizer.getGetter( 0 ) instanceof BasicPropertyAccessor.BasicGetter
 		);
 	}
 
 	public void testExplicitPropertyAccessAnnotationsWithJpaStyleOverride() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		Class<?> classUnderTest = Course5.class;
 		cfg.addAnnotatedClass( classUnderTest );
 		cfg.addAnnotatedClass( Student.class );
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Field access should be used.",
 				tuplizer.getIdentifierGetter() instanceof DirectPropertyAccessor.DirectGetter
 		);
 
 		assertTrue(
 				"Property access should be used.",
 				tuplizer.getGetter( 0 ) instanceof BasicPropertyAccessor.BasicGetter
 		);
 	}
 
 	public void testDefaultFieldAccessIsInherited() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		Class<?> classUnderTest = User.class;
 		cfg.addAnnotatedClass( classUnderTest );
 		cfg.addAnnotatedClass( Person.class );
 		cfg.addAnnotatedClass( Being.class );
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Field access should be used since the default access mode gets inherited",
 				tuplizer.getIdentifierGetter() instanceof DirectPropertyAccessor.DirectGetter
 		);
 	}
 
 	public void testDefaultPropertyAccessIsInherited() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.addAnnotatedClass( Horse.class );
 		cfg.addAnnotatedClass( Animal.class );
 
 		SessionFactoryImplementor factory = ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 		EntityMetamodel metaModel = factory.getEntityPersister( Animal.class.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Property access should be used since explicity configured via @Access",
 				tuplizer.getIdentifierGetter() instanceof BasicPropertyAccessor.BasicGetter
 		);
 
 		metaModel = factory.getEntityPersister( Horse.class.getName() )
 				.getEntityMetamodel();
 		tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		assertTrue(
 				"Property access should be used since the default access mode gets inherited",
 				tuplizer.getGetter( 0 ) instanceof BasicPropertyAccessor.BasicGetter
 		);
 	}
 
 	/**
 	 * HHH-5004
 	 */
 	public void testAccessOnClassAndId() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.addAnnotatedClass( Course8.class );
 		cfg.addAnnotatedClass( Student.class );
 		cfg.buildSessionFactory( serviceRegistry );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
index 48c3b1b215..4e072098ef 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
@@ -1,210 +1,210 @@
 //$Id$
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
  */
 package org.hibernate.test.annotations.access.xml;
 
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import javax.persistence.AccessType;
 import junit.framework.TestCase;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.property.BasicPropertyAccessor;
 import org.hibernate.property.DirectPropertyAccessor;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.PojoEntityTuplizer;
 
 
 /**
  * Test verifying that it is possible to configure the access type via xml configuration.
  *
  * @author Hardy Ferentschik
  */
 public class XmlAccessTest extends TestCase {
 
 	private ServiceRegistry serviceRegistry;
 
 	@Override
     protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@Override
     protected void tearDown() {
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	public void testAccessOnBasicXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using basic
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	public void testAccessOnPersistenceUnitDefaultsXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using persitence unit defaults
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist2.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	public void testAccessOnEntityMappingsXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using default in entity-mappings
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist3.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	public void testAccessOnEntityXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using entity level config
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist4.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	public void testAccessOnMappedSuperClassXmlElement() throws Exception {
 		Class<?> classUnderTest = Waiter.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		classes.add( Crew.class );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Crew.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 	}
 
 	public void testAccessOnAssociationXmlElement() throws Exception {
 		Class<?> classUnderTest = RentalCar.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		classes.add( Driver.class );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/RentalCar.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	public void testAccessOnEmbeddedXmlElement() throws Exception {
 		Class<?> classUnderTest = Cook.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		classes.add( Knive.class );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Cook.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	public void testAccessOnElementCollectionXmlElement() throws Exception {
 		Class<?> classUnderTest = Boy.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Boy.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	private SessionFactoryImplementor buildSessionFactory(List<Class<?>> classesUnderTest, List<String> configFiles) {
 		assert classesUnderTest != null;
 		assert configFiles != null;
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		for ( Class<?> clazz : classesUnderTest ) {
 			cfg.addAnnotatedClass( clazz );
 		}
 		for ( String configFile : configFiles ) {
 			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( configFile );
 			cfg.addInputStream( is );
 		}
 		return ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	// uses the first getter of the tupelizer for the assertions
 
 	private void assertAccessType(SessionFactoryImplementor factory, Class<?> classUnderTest, AccessType accessType) {
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		if ( AccessType.FIELD.equals( accessType ) ) {
 			assertTrue(
 					"Field access was expected.",
 					tuplizer.getGetter( 0 ) instanceof DirectPropertyAccessor.DirectGetter
 			);
 		}
 		else {
 			assertTrue(
 					"Property access was expected.",
 					tuplizer.getGetter( 0 ) instanceof BasicPropertyAccessor.BasicGetter
 			);
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
index 387d445e9b..58b7587514 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/backquotes/BackquoteTest.java
@@ -1,75 +1,75 @@
 //$Id$
 package org.hibernate.test.annotations.backquotes;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import junit.framework.TestCase;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * Testcase for ANN-718 - @JoinTable / @JoinColumn fail when using backquotes in PK field name.
  *
  * @author Hardy Ferentschik
  *
  */
 public class BackquoteTest extends TestCase {
 
 	private ServiceRegistry serviceRegistry;
 
 	@Override
     protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@Override
     protected void tearDown() {
         if (serviceRegistry != null) ServiceRegistryBuilder.destroy(serviceRegistry);
 	}
 
 	public void testBackquotes() {
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.addAnnotatedClass(Bug.class);
 			config.addAnnotatedClass(Category.class);
 			config.buildSessionFactory( serviceRegistry );
 		}
 		catch( Exception e ) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             LOG.debug(writer.toString());
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
 	public void testInvalidReferenceToQuotedTableName() {
     	try {
     		AnnotationConfiguration config = new AnnotationConfiguration();
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
             LOG.debug(writer.toString());
         	fail(e.getMessage());
         }
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/duplicatedgenerator/DuplicateTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/duplicatedgenerator/DuplicateTest.java
index c963dc9549..d3a7e515a0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/duplicatedgenerator/DuplicateTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/duplicatedgenerator/DuplicateTest.java
@@ -1,37 +1,38 @@
 //$Id$
 package org.hibernate.test.annotations.duplicatedgenerator;
 import junit.framework.TestCase;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * @author Emmanuel Bernard
  */
 public class DuplicateTest extends TestCase {
 	public void testDuplicateEntityName() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		ServiceRegistry serviceRegistry = null;
 		try {
 			cfg.addAnnotatedClass( Flight.class );
 			cfg.addAnnotatedClass( org.hibernate.test.annotations.Flight.class );
 			cfg.addResource( "org/hibernate/test/annotations/orm.xml" );
 			cfg.addResource( "org/hibernate/test/annotations/duplicatedgenerator/orm.xml" );
 			serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 			cfg.buildSessionFactory( serviceRegistry );
 			fail( "Should not be able to map the same entity name twice" );
 		}
 		catch (AnnotationException ae) {
 			//success
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
index 1659e92650..1b62f1604a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/fetchprofile/FetchProfileTest.java
@@ -1,175 +1,176 @@
 // $Id$
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
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
 
 package org.hibernate.test.annotations.fetchprofile;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import java.io.InputStream;
 import junit.framework.TestCase;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * Test case for HHH-4812
  *
  * @author Hardy Ferentschik
  */
 public class FetchProfileTest extends TestCase {
 
 	private ServiceRegistry serviceRegistry;
 
 	@Override
     protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@Override
     protected void tearDown() {
         if (serviceRegistry != null) ServiceRegistryBuilder.destroy(serviceRegistry);
 	}
 
 	public void testFetchProfileConfigured() {
 		AnnotationConfiguration config = new AnnotationConfiguration();
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
 
 	public void testWrongAssociationName() {
 		AnnotationConfiguration config = new AnnotationConfiguration();
 		config.addAnnotatedClass( Customer2.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             LOG.trace("success");
 		}
 	}
 
 	public void testWrongClass() {
 		AnnotationConfiguration config = new AnnotationConfiguration();
 		config.addAnnotatedClass( Customer3.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             LOG.trace("success");
 		}
 	}
 
 	public void testUnsupportedFetchMode() {
 		AnnotationConfiguration config = new AnnotationConfiguration();
 		config.addAnnotatedClass( Customer4.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             LOG.trace("success");
 		}
 	}
 
 	public void testXmlOverride() {
 		AnnotationConfiguration config = new AnnotationConfiguration();
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
 		config = new AnnotationConfiguration();
 		config.addAnnotatedClass( Customer5.class );
 		config.addAnnotatedClass( Order.class );
 		config.addAnnotatedClass( Country.class );
 		try {
 			config.buildSessionFactory( serviceRegistry );
 			fail();
 		}
 		catch ( MappingException e ) {
             LOG.trace("success");
 		}
 	}
 
 	public void testPackageConfiguredFetchProfile() {
 		AnnotationConfiguration config = new AnnotationConfiguration();
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/fkcircularity/FkCircularityTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/fkcircularity/FkCircularityTest.java
index 8bbfaac1ca..a9a3307c03 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/fkcircularity/FkCircularityTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/fkcircularity/FkCircularityTest.java
@@ -1,77 +1,78 @@
 // $Id$
 package org.hibernate.test.annotations.fkcircularity;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import junit.framework.TestCase;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.SQLServerDialect;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * Test case for ANN-722 and ANN-730.
  *
  * @author Hardy Ferentschik
  */
 public class FkCircularityTest extends TestCase {
 
 	private ServiceRegistry serviceRegistry;
 
 	@Override
     protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@Override
     protected void tearDown() {
         if (serviceRegistry != null) ServiceRegistryBuilder.destroy(serviceRegistry);
 	}
 
 	public void testJoinedSublcassesInPK() {
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.addAnnotatedClass(A.class);
 			config.addAnnotatedClass(B.class);
 			config.addAnnotatedClass(C.class);
 			config.addAnnotatedClass(D.class);
 			config.buildSessionFactory( serviceRegistry );
 			String[] schema = config
 					.generateSchemaCreationScript(new SQLServerDialect());
 			for (String s : schema) {
                 LOG.debug(s);
 			}
             LOG.debug("success");
 		} catch (Exception e) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             LOG.debug(writer.toString());
 			fail(e.getMessage());
 		}
 	}
 
 	public void testDeepJoinedSuclassesHierachy() {
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.addAnnotatedClass(ClassA.class);
 			config.addAnnotatedClass(ClassB.class);
 			config.addAnnotatedClass(ClassC.class);
 			config.addAnnotatedClass(ClassD.class);
 			config.buildSessionFactory( serviceRegistry );
 			String[] schema = config
 					.generateSchemaCreationScript(new HSQLDialect());
 			for (String s : schema) {
                 LOG.debug(s);
 			}
             LOG.debug("success");
 		} catch (Exception e) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             LOG.debug(writer.toString());
 			fail(e.getMessage());
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
index 890801bbbc..65f41277fe 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/namingstrategy/NamingStrategyTest.java
@@ -1,95 +1,96 @@
 // $Id$
 package org.hibernate.test.annotations.namingstrategy;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import java.io.PrintWriter;
 import java.io.StringWriter;
 import java.util.Iterator;
 import junit.framework.TestCase;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.mapping.Table;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
+
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * Test harness for ANN-716.
  *
  * @author Hardy Ferentschik
  */
 public class NamingStrategyTest extends TestCase {
 
 	private ServiceRegistry serviceRegistry;
 
 	@Override
     protected void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@Override
     protected void tearDown() {
         if (serviceRegistry != null) ServiceRegistryBuilder.destroy(serviceRegistry);
 	}
 
 	public void testWithCustomNamingStrategy() throws Exception {
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.setNamingStrategy(new DummyNamingStrategy());
 			config.addAnnotatedClass(Address.class);
 			config.addAnnotatedClass(Person.class);
 			config.buildSessionFactory( serviceRegistry );
 		}
 		catch( Exception e ) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             LOG.debug(writer.toString());
 			fail(e.getMessage());
 		}
 	}
 
 	public void testWithEJB3NamingStrategy() throws Exception {
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.setNamingStrategy(EJB3NamingStrategy.INSTANCE);
 			config.addAnnotatedClass(A.class);
 			config.addAnnotatedClass(AddressEntry.class);
 			config.buildSessionFactory( serviceRegistry );
 			Mappings mappings = config.createMappings();
 			boolean foundIt = false;
 
 			for ( Iterator iter = mappings.iterateTables(); iter.hasNext();  ) {
 				Table table = (Table) iter.next();
                 LOG.info("testWithEJB3NamingStrategy table = " + table.getName());
 				if ( table.getName().equalsIgnoreCase("A_ADDRESS")) {
 					foundIt = true;
 				}
 				// make sure we use A_ADDRESS instead of AEC_address
 				assertFalse("got table name mapped to: AEC_address (should be A_ADDRESS) which violates JPA-2 spec section 11.1.8 ([OWNING_ENTITY_NAME]_[COLLECTION_ATTRIBUTE_NAME])",table.getName().equalsIgnoreCase("AEC_address"));
 			}
 			assertTrue("table not mapped to A_ADDRESS which violates JPA-2 spec section 11.1.8",foundIt);
 		}
 		catch( Exception e ) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             LOG.debug(writer.toString());
 			fail(e.getMessage());
 		}
 	}
 
 	public void testWithoutCustomNamingStrategy() throws Exception {
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.addAnnotatedClass(Address.class);
 			config.addAnnotatedClass(Person.class);
 			config.buildSessionFactory( serviceRegistry );
 		}
 		catch( Exception e ) {
 			StringWriter writer = new StringWriter();
 			e.printStackTrace(new PrintWriter(writer));
             LOG.debug(writer.toString());
 			fail(e.getMessage());
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/OneToOneErrorTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/OneToOneErrorTest.java
index 4c8204ded0..522b8e617d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/OneToOneErrorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/OneToOneErrorTest.java
@@ -1,34 +1,34 @@
 //$Id$
 package org.hibernate.test.annotations.onetoone;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * @author Emmanuel Bernard
  */
 public class OneToOneErrorTest extends junit.framework.TestCase {
 	public void testWrongOneToOne() throws Exception {
 		AnnotationConfiguration cfg = new AnnotationConfiguration();
 		cfg.addAnnotatedClass( Show.class )
 				.addAnnotatedClass( ShowDescription.class );
 		cfg.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		ServiceRegistry serviceRegistry = null;
 		try {
 			serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 			cfg.buildSessionFactory( serviceRegistry );
 			fail( "Wrong mappedBy does not fail property" );
 		}
 		catch (AnnotationException e) {
 			//success
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/primarykey/NullablePrimaryKeyTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/primarykey/NullablePrimaryKeyTest.java
index a562ad605f..6517a7b5d3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/primarykey/NullablePrimaryKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetoone/primarykey/NullablePrimaryKeyTest.java
@@ -1,46 +1,46 @@
 //$Id: A320.java 14736 2008-06-04 14:23:42Z hardy.ferentschik $
 package org.hibernate.test.annotations.onetoone.primarykey;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import junit.framework.TestCase;
 import org.hibernate.cfg.AnnotationConfiguration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.SQLServerDialect;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 
 /**
  * Test harness for ANN-742.
  *
  * @author Hardy Ferentschik
  *
  */
 public class NullablePrimaryKeyTest extends TestCase {
 
 	public void testGeneratedSql() {
 
 		ServiceRegistry serviceRegistry = null;
 		try {
 			AnnotationConfiguration config = new AnnotationConfiguration();
 			config.addAnnotatedClass(Address.class);
 			config.addAnnotatedClass(Person.class);
 			serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 			config.buildSessionFactory( serviceRegistry );
 			String[] schema = config
 					.generateSchemaCreationScript(new SQLServerDialect());
 			for (String s : schema) {
                 LOG.debug(s);
 			}
 			String expectedMappingTableSql = "create table personAddress (address_id numeric(19,0) null, " +
 					"person_id numeric(19,0) not null, primary key (person_id))";
 			assertEquals("Wrong SQL", expectedMappingTableSql, schema[2]);
 		} catch (Exception e) {
 			fail(e.getMessage());
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/ConfigurationSerializationTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/ConfigurationSerializationTest.java
index 414f58269a..ff980410d0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/ConfigurationSerializationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/ConfigurationSerializationTest.java
@@ -1,134 +1,134 @@
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
 package org.hibernate.test.cfg;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.internal.util.SerializationHelper;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * Copied over mostly from ConfigurationPerformanceTest
  *
  * @author Steve Ebersole
  * @author Max Andersen
  */
 public class ConfigurationSerializationTest extends BaseUnitTestCase {
 	private static final String[] FILES = new String[] {
 			"legacy/ABC.hbm.xml",
 			"legacy/ABCExtends.hbm.xml",
 			"legacy/Baz.hbm.xml",
 			"legacy/Blobber.hbm.xml",
 			"legacy/Broken.hbm.xml",
 			"legacy/Category.hbm.xml",
 			"legacy/Circular.hbm.xml",
 			"legacy/Commento.hbm.xml",
 			"legacy/ComponentNotNullMaster.hbm.xml",
 			"legacy/Componentizable.hbm.xml",
 			"legacy/Container.hbm.xml",
 			"legacy/Custom.hbm.xml",
 			"legacy/CustomSQL.hbm.xml",
 			"legacy/Eye.hbm.xml",
 			"legacy/Fee.hbm.xml",
 			"legacy/Fo.hbm.xml",
 			"legacy/FooBar.hbm.xml",
 			"legacy/Fum.hbm.xml",
 			"legacy/Fumm.hbm.xml",
 			"legacy/Glarch.hbm.xml",
 			"legacy/Holder.hbm.xml",
 			"legacy/IJ2.hbm.xml",
 			"legacy/Immutable.hbm.xml",
 			"legacy/Location.hbm.xml",
 			"legacy/Many.hbm.xml",
 			"legacy/Map.hbm.xml",
 			"legacy/Marelo.hbm.xml",
 			"legacy/MasterDetail.hbm.xml",
 			"legacy/Middle.hbm.xml",
 			"legacy/Multi.hbm.xml",
 			"legacy/MultiExtends.hbm.xml",
 			"legacy/Nameable.hbm.xml",
 			"legacy/One.hbm.xml",
 			"legacy/ParentChild.hbm.xml",
 			"legacy/Qux.hbm.xml",
 			"legacy/Simple.hbm.xml",
 			"legacy/SingleSeveral.hbm.xml",
 			"legacy/Stuff.hbm.xml",
 			"legacy/UpDown.hbm.xml",
 			"legacy/Vetoer.hbm.xml",
 			"legacy/WZ.hbm.xml",
 			"cfg/orm-serializable.xml"
 	};
 
 	@Test
 	public void testConfigurationSerializability() {
 		Configuration cfg = new Configuration();
 		for ( String file : FILES ) {
 			cfg.addResource( "org/hibernate/test/" + file );
 		}
 
 		cfg.addAnnotatedClass( Serial.class );
 
 		byte[] bytes = SerializationHelper.serialize( cfg );
 		cfg = ( Configuration ) SerializationHelper.deserialize( bytes );
 
 		SessionFactory factory = null;
 		ServiceRegistry serviceRegistry = null;
 		try {
 			serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 			// try to build SF
 			factory = cfg.buildSessionFactory( serviceRegistry );
 		}
 		finally {
 			if ( factory != null ) {
 				factory.close();
 			}
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 			}
 		}
 	}
 
 	@Entity
 	public static class Serial {
 		private String id;
 		private String value;
 
 		@Id
 		public String getId() {
 			return id;
 		}
 
 		public void setId(String id) {
 			this.id = id;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
index a8154b1b47..8597d932c8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
@@ -1,83 +1,83 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.cfg.persister;
 
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.persister.spi.PersisterClassResolver;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends BaseUnitTestCase {
 	@Test
 	public void testPersisterClassProvider() throws Exception {
 
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		ServiceRegistry serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		//no exception as the GoofyPersisterClassProvider is not set
 		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		sessionFactory.close();
 
 		serviceRegistry.registerService( PersisterClassResolver.class, new GoofyPersisterClassProvider() );
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The entity persister should be overridden",
 					GoofyPersisterClassProvider.NoopEntityPersister.class,
 					( (GoofyException) e.getCause() ).getValue()
 			);
 		}
 
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Portal.class );
 		cfg.addAnnotatedClass( Window.class );
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The collection persister should be overridden but not the entity persister",
 					GoofyPersisterClassProvider.NoopCollectionPersister.class,
 					( (GoofyException) e.getCause() ).getValue() );
 		}
 
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
index ebc840dd5f..21497aa6ab 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/TransactionEnvironmentImpl.java
@@ -1,71 +1,71 @@
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
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
-import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class TransactionEnvironmentImpl implements TransactionEnvironment {
 	private final ServiceRegistry serviceRegistry;
 	private final ConcurrentStatisticsImpl statistics = new ConcurrentStatisticsImpl();
 
 	public TransactionEnvironmentImpl(ServiceRegistry serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		throw new NotYetImplementedException( "Not available in this context" );
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry.getService( JdbcServices.class );
 	}
 
 	@Override
 	public JtaPlatform getJtaPlatform() {
 		return serviceRegistry.getService( JtaPlatform.class );
 	}
 
 	@Override
 	public TransactionFactory getTransactionFactory() {
 		return serviceRegistry.getService( TransactionFactory.class );
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return statistics;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java
index 1ee2efe875..7665886fa3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/instrument/cases/AbstractExecutable.java
@@ -1,49 +1,49 @@
 package org.hibernate.test.instrument.cases;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
-
-/**
- * @author Steve Ebersole
- */
-public abstract class AbstractExecutable implements Executable {
-
-	private ServiceRegistry serviceRegistry;
-	private SessionFactory factory;
-
-	public final void prepare() {
-		Configuration cfg = new Configuration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
-		String[] resources = getResources();
-		for ( String resource : resources ) {
-			cfg.addResource( resource );
-		}
-		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
-		factory = cfg.buildSessionFactory( serviceRegistry );
-	}
-
-	public final void complete() {
-		try {
-			cleanup();
-		}
-		finally {
-			factory.close();
-			if ( serviceRegistry != null ) {
-				ServiceRegistryBuilder.destroy( serviceRegistry );
-			}
-		}
-	}
-
-	protected SessionFactory getFactory() {
-		return factory;
-	}
-
-	protected void cleanup() {
-	}
-
-	protected String[] getResources() {
-		return new String[] { "org/hibernate/test/instrument/domain/Documents.hbm.xml" };
-	}
-}
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractExecutable implements Executable {
+
+	private ServiceRegistry serviceRegistry;
+	private SessionFactory factory;
+
+	public final void prepare() {
+		Configuration cfg = new Configuration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
+		String[] resources = getResources();
+		for ( String resource : resources ) {
+			cfg.addResource( resource );
+		}
+		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
+		factory = cfg.buildSessionFactory( serviceRegistry );
+	}
+
+	public final void complete() {
+		try {
+			cleanup();
+		}
+		finally {
+			factory.close();
+			if ( serviceRegistry != null ) {
+				ServiceRegistryBuilder.destroy( serviceRegistry );
+			}
+		}
+	}
+
+	protected SessionFactory getFactory() {
+		return factory;
+	}
+
+	protected void cleanup() {
+	}
+
+	protected String[] getResources() {
+		return new String[] { "org/hibernate/test/instrument/domain/Documents.hbm.xml" };
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
index 78fd6f5585..26cd2739d5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
@@ -1,220 +1,220 @@
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
+import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.StandardServiceInitiators;
 
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
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/MigrationTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/MigrationTest.java
index 966beecec3..a5e94a44b6 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/MigrationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/MigrationTest.java
@@ -1,89 +1,89 @@
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
 package org.hibernate.test.schemaupdate;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 
 import org.hibernate.cfg.Configuration;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 
 /**
  * @author Max Rydahl Andersen
  */
 public class MigrationTest extends BaseUnitTestCase {
 	private ServiceRegistry serviceRegistry;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
 	}
 
 	@After
 	public void tearDown() {
 		ServiceRegistryBuilder.destroy( serviceRegistry );
 		serviceRegistry = null;
 	}
 
 	protected JdbcServices getJdbcServices() {
 		return serviceRegistry.getService( JdbcServices.class );
 	}
 
 	@Test
 	public void testSimpleColumnAddition() {
 		String resource1 = "org/hibernate/test/schemaupdate/1_Version.hbm.xml";
 		String resource2 = "org/hibernate/test/schemaupdate/2_Version.hbm.xml";
 
 		Configuration v1cfg = new Configuration();
 		v1cfg.addResource( resource1 );
 		new SchemaExport( v1cfg ).execute( false, true, true, false );
 
 		SchemaUpdate v1schemaUpdate = new SchemaUpdate( getJdbcServices(), v1cfg );
 		v1schemaUpdate.execute( true, true );
 
 		assertEquals( 0, v1schemaUpdate.getExceptions().size() );
 
 		Configuration v2cfg = new Configuration();
 		v2cfg.addResource( resource2 );
 
 		SchemaUpdate v2schemaUpdate = new SchemaUpdate( getJdbcServices(), v2cfg );
 		v2schemaUpdate.execute( true, true );
 		assertEquals( 0, v2schemaUpdate.getExceptions().size() );
 		
 		new SchemaExport( getJdbcServices(), v2cfg ).drop( false, true );
 
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
index f4b1adc387..7b47132d88 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
@@ -1,142 +1,142 @@
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
 package org.hibernate.test.transaction.jdbc;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
+import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.StandardServiceInitiators;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 /**
  * @author Steve Ebersole
  */
 public class TestExpectedUsage extends BaseUnitTestCase {
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
 
 	@Test
 	public void testBasicUsage() {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		};
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
index 90694dc042..0f0413535d 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
@@ -1,179 +1,179 @@
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
 package org.hibernate.test.transaction.jta;
 
 import javax.transaction.TransactionManager;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
+import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.internal.ServiceProxy;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
-import org.hibernate.service.spi.StandardServiceInitiators;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Testing transaction facade handling when the transaction is being driven by something other than the facade.
  *
  * @author Steve Ebersole
  */
 public class ManagedDrivingTest extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		TestingJtaBootstrap.prepare( configValues );
 //		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 
 		serviceRegistry = new BasicServiceRegistryImpl( StandardServiceInitiators.LIST, configValues );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_STATEMENT;
 			}
 		};
 
 		final TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		final JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		final LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 		TransactionManager transactionManager = instance.retrieveTransactionManager();
 
 		// start the cmt
 		transactionManager.begin();
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 		assertFalse( txn.isInitiator() );
 		connection = logicalConnection.getShareableConnectionProxy();
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// since txn is not a driver, nothing should have changed...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 0, transactionObserver.getBeforeCompletions() );
 			assertEquals( 0, transactionObserver.getAfterCompletions() );
 
 			transactionManager.commit();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 1, transactionObserver.getBeforeCompletions() );
 			assertEquals( 1, transactionObserver.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index 0a14e81301..80f4182b08 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1104 +1,1104 @@
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
 package org.hibernate.ejb;
 
 import javax.naming.BinaryRefAddr;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.Referenceable;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitInfo;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectOutput;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.annotations.reflection.XMLContext;
 import org.hibernate.ejb.connection.InjectedDataSourceConnectionProvider;
 import org.hibernate.ejb.instrument.InterceptFieldClassFileTransformer;
 import org.hibernate.ejb.packaging.JarVisitorFactory;
 import org.hibernate.ejb.packaging.NamedInputStream;
 import org.hibernate.ejb.packaging.NativeScanner;
 import org.hibernate.ejb.packaging.PersistenceMetadata;
 import org.hibernate.ejb.packaging.PersistenceXmlLoader;
 import org.hibernate.ejb.packaging.Scanner;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LogHelper;
 import org.hibernate.ejb.util.NamingHelper;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.event.EventListeners;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.JACCConfiguration;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
-import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Allow a fine tuned configuration of an EJB 3.0 EntityManagerFactory
  *
  * A Ejb3Configuration object is only guaranteed to create one EntityManagerFactory.
  * Multiple usage of #buildEntityManagerFactory() is not guaranteed.
  *
  * After #buildEntityManagerFactory() has been called, you no longer can change the configuration
  * state (no class adding, no property change etc)
  *
  * When serialized / deserialized or retrieved from the JNDI, you no longer can change the
  * configuration state (no class adding, no property change etc)
  *
  * Putting the configuration in the JNDI is an expensive operation that requires a partial
  * serialization
  *
  * @author Emmanuel Bernard
  */
 public class Ejb3Configuration implements Serializable, Referenceable {
 
     private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
                                                                            Ejb3Configuration.class.getName());
 	private static final String IMPLEMENTATION_NAME = HibernatePersistence.class.getName();
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 	private static final String PARSED_MAPPING_DOMS = "hibernate.internal.mapping_doms";
 
 	private static EntityNotFoundDelegate ejb3EntityNotFoundDelegate = new Ejb3EntityNotFoundDelegate();
 	private static Configuration DEFAULT_CONFIGURATION = new Configuration();
 
 	private static class Ejb3EntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);
 		}
 	}
 
 	private String persistenceUnitName;
 	private String cfgXmlResource;
 
 	private Configuration cfg;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient EventListenerConfigurator listenerConfigurator;
 	private PersistenceUnitTransactionType transactionType;
 	private boolean discardOnClose;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient ClassLoader overridenClassLoader;
 	private boolean isConfigurationProcessed = false;
 
 
 	public Ejb3Configuration() {
 		cfg = new Configuration();
 		cfg.setEntityNotFoundDelegate( ejb3EntityNotFoundDelegate );
 		listenerConfigurator = new EventListenerConfigurator( this );
 	}
 
 	/**
 	 * Used to inject a datasource object as the connection provider.
 	 * If used, be sure to <b>not override</b> the hibernate.connection.provider_class
 	 * property
 	 */
 	@SuppressWarnings({ "JavaDoc", "unchecked" })
 	public void setDataSource(DataSource ds) {
 		if ( ds != null ) {
 			cfg.getProperties().put( Environment.DATASOURCE, ds );
 			this.setProperty( Environment.CONNECTION_PROVIDER, DatasourceConnectionProviderImpl.class.getName() );
 		}
 	}
 
 	/**
 	 * create a factory from a parsed persistence.xml
 	 * Especially the scanning of classes and additional jars is done already at this point.
 	 * <p/>
 	 * NOTE: public only for unit testing purposes; not a public API!
 	 *
 	 * @param metadata The information parsed from the persistence.xml
 	 * @param overridesIn Any explicitly passed config settings
 	 *
 	 * @return this
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceMetadata metadata, Map overridesIn) {
         LOG.debugf("Creating Factory: %s", metadata.getName());
 
 		Map overrides = new HashMap();
 		if ( overridesIn != null ) {
 			overrides.putAll( overridesIn );
 		}
 
 		Map workingVars = new HashMap();
 		workingVars.put( AvailableSettings.PERSISTENCE_UNIT_NAME, metadata.getName() );
 		this.persistenceUnitName = metadata.getName();
 
 		if ( StringHelper.isNotEmpty( metadata.getJtaDatasource() ) ) {
 			this.setProperty( Environment.DATASOURCE, metadata.getJtaDatasource() );
 		}
 		else if ( StringHelper.isNotEmpty( metadata.getNonJtaDatasource() ) ) {
 			this.setProperty( Environment.DATASOURCE, metadata.getNonJtaDatasource() );
 		}
 		else {
 			final String driver = (String) metadata.getProps().get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				this.setProperty( Environment.DRIVER, driver );
 			}
 			final String url = (String) metadata.getProps().get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				this.setProperty( Environment.URL, url );
 			}
 			final String user = (String) metadata.getProps().get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				this.setProperty( Environment.USER, user );
 			}
 			final String pass = (String) metadata.getProps().get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				this.setProperty( Environment.PASS, pass );
 			}
 		}
 		defineTransactionType( metadata.getTransactionType(), workingVars );
 		if ( metadata.getClasses().size() > 0 ) {
 			workingVars.put( AvailableSettings.CLASS_NAMES, metadata.getClasses() );
 		}
 		if ( metadata.getPackages().size() > 0 ) {
 			workingVars.put( AvailableSettings.PACKAGE_NAMES, metadata.getPackages() );
 		}
 		if ( metadata.getMappingFiles().size() > 0 ) {
 			workingVars.put( AvailableSettings.XML_FILE_NAMES, metadata.getMappingFiles() );
 		}
 		if ( metadata.getHbmfiles().size() > 0 ) {
 			workingVars.put( AvailableSettings.HBXML_FILES, metadata.getHbmfiles() );
 		}
 
 		Properties props = new Properties();
 		props.putAll( metadata.getProps() );
 
 		// validation factory
 		final Object validationFactory = overrides.get( AvailableSettings.VALIDATION_FACTORY );
 		if ( validationFactory != null ) {
 			props.put( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 		}
 		overrides.remove( AvailableSettings.VALIDATION_FACTORY );
 
 		// validation-mode (overrides has precedence)
 		{
 			final Object integrationValue = overrides.get( AvailableSettings.VALIDATION_MODE );
 			if ( integrationValue != null ) {
 				props.put( AvailableSettings.VALIDATION_MODE, integrationValue.toString() );
 			}
 			else if ( metadata.getValidationMode() != null ) {
 				props.put( AvailableSettings.VALIDATION_MODE, metadata.getValidationMode() );
 			}
 			overrides.remove( AvailableSettings.VALIDATION_MODE );
 		}
 
 		// shared-cache-mode (overrides has precedence)
 		{
 			final Object integrationValue = overrides.get( AvailableSettings.SHARED_CACHE_MODE );
 			if ( integrationValue != null ) {
 				props.put( AvailableSettings.SHARED_CACHE_MODE, integrationValue.toString() );
 			}
 			else if ( metadata.getSharedCacheMode() != null ) {
 				props.put( AvailableSettings.SHARED_CACHE_MODE, metadata.getSharedCacheMode() );
 			}
 			overrides.remove( AvailableSettings.SHARED_CACHE_MODE );
 		}
 
 		for ( Map.Entry entry : (Set<Map.Entry>) overrides.entrySet() ) {
 			Object value = entry.getValue();
 			props.put( entry.getKey(), value == null ? "" :  value ); //alter null, not allowed in properties
 		}
 
 		configure( props, workingVars );
 		return this;
 	}
 
 	/**
 	 * Build the configuration from an entity manager name and given the
 	 * appropriate extra properties. Those properties override the one get through
 	 * the persistence.xml file.
 	 * If the persistence unit name is not found or does not match the Persistence Provider, null is returned
 	 *
 	 * This method is used in a non managed environment
 	 *
 	 * @param persistenceUnitName persistence unit name
 	 * @param integration properties passed to the persistence provider
 	 *
 	 * @return configured Ejb3Configuration or null if no persistence unit match
 	 *
 	 * @see HibernatePersistence#createEntityManagerFactory(String, java.util.Map)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(String persistenceUnitName, Map integration) {
 		try {
             LOG.debugf("Look up for persistence unit: %s", persistenceUnitName);
 			integration = integration == null ?
 					CollectionHelper.EMPTY_MAP :
 					Collections.unmodifiableMap( integration );
 			Enumeration<URL> xmls = Thread.currentThread()
 					.getContextClassLoader()
 					.getResources( "META-INF/persistence.xml" );
             if (!xmls.hasMoreElements()) LOG.unableToFindPersistenceXmlInClasspath();
 			while ( xmls.hasMoreElements() ) {
 				URL url = xmls.nextElement();
                 LOG.trace("Analyzing persistence.xml: " + url);
 				List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader.deploy(
 						url,
 						integration,
 						cfg.getEntityResolver(),
 						PersistenceUnitTransactionType.RESOURCE_LOCAL );
 				for ( PersistenceMetadata metadata : metadataFiles ) {
                     LOG.trace(metadata);
 
 					if ( metadata.getProvider() == null || IMPLEMENTATION_NAME.equalsIgnoreCase(
 							metadata.getProvider()
 					) ) {
 						//correct provider
 
 						//lazy load the scanner to avoid unnecessary IOExceptions
 						Scanner scanner = null;
 						URL jarURL = null;
 						if ( metadata.getName() == null ) {
 							scanner = buildScanner( metadata.getProps(), integration );
 							jarURL = JarVisitorFactory.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
 							metadata.setName( scanner.getUnqualifiedJarName(jarURL) );
 						}
 						if ( persistenceUnitName == null && xmls.hasMoreElements() ) {
 							throw new PersistenceException( "No name provided and several persistence units found" );
 						}
 						else if ( persistenceUnitName == null || metadata.getName().equals( persistenceUnitName ) ) {
 							if (scanner == null) {
 								scanner = buildScanner( metadata.getProps(), integration );
 								jarURL = JarVisitorFactory.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
 							}
 							//scan main JAR
 							ScanningContext mainJarScanCtx = new ScanningContext()
 									.scanner( scanner )
 									.url( jarURL )
 									.explicitMappingFiles( metadata.getMappingFiles() )
 									.searchOrm( true );
 							setDetectedArtifactsOnScanningContext( mainJarScanCtx, metadata.getProps(), integration,
 																				metadata.getExcludeUnlistedClasses() );
 							addMetadataFromScan( mainJarScanCtx, metadata );
 
 							ScanningContext otherJarScanCtx = new ScanningContext()
 									.scanner( scanner )
 									.explicitMappingFiles( metadata.getMappingFiles() )
 									.searchOrm( true );
 							setDetectedArtifactsOnScanningContext( otherJarScanCtx, metadata.getProps(), integration,
 																				false );
 							for ( String jarFile : metadata.getJarFiles() ) {
 								otherJarScanCtx.url( JarVisitorFactory.getURLFromPath( jarFile ) );
 								addMetadataFromScan( otherJarScanCtx, metadata );
 							}
 							return configure( metadata, integration );
 						}
 					}
 				}
 			}
 			return null;
 		}
 		catch (Exception e) {
 			if ( e instanceof PersistenceException) {
 				throw (PersistenceException) e;
 			}
 			else {
 				throw new PersistenceException( getExceptionHeader() + "Unable to configure EntityManagerFactory", e );
 			}
 		}
 	}
 
 	private Scanner buildScanner(Properties properties, Map<?,?> integration) {
 		//read the String or Instance from the integration map first and use the properties as a backup.
 		Object scanner = integration.get( AvailableSettings.SCANNER );
 		if (scanner == null) {
 			scanner = properties.getProperty( AvailableSettings.SCANNER );
 		}
 		if (scanner != null) {
 			Class<?> scannerClass;
 			if ( scanner instanceof String ) {
 				try {
 					scannerClass = ReflectHelper.classForName( (String) scanner, this.getClass() );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new PersistenceException(  "Cannot find scanner class. " + AvailableSettings.SCANNER + "=" + scanner, e );
 				}
 			}
 			else if (scanner instanceof Class) {
 				scannerClass = (Class<? extends Scanner>) scanner;
 			}
 			else if (scanner instanceof Scanner) {
 				return (Scanner) scanner;
 			}
 			else {
 				throw new PersistenceException(  "Scanner class configuration error: unknown type on the property. " + AvailableSettings.SCANNER );
 			}
 			try {
 				return (Scanner) scannerClass.newInstance();
 			}
 			catch ( InstantiationException e ) {
 				throw new PersistenceException(  "Unable to load Scanner class: " + scannerClass, e );
 			}
 			catch ( IllegalAccessException e ) {
 				throw new PersistenceException(  "Unable to load Scanner class: " + scannerClass, e );
 			}
 		}
 		else {
 			return new NativeScanner();
 		}
 	}
 
 	private static class ScanningContext {
 		//boolean excludeUnlistedClasses;
 		private Scanner scanner;
 		private URL url;
 		private List<String> explicitMappingFiles;
 		private boolean detectClasses;
 		private boolean detectHbmFiles;
 		private boolean searchOrm;
 
 		public ScanningContext scanner(Scanner scanner) {
 			this.scanner = scanner;
 			return this;
 		}
 
 		public ScanningContext url(URL url) {
 			this.url = url;
 			return this;
 		}
 
 		public ScanningContext explicitMappingFiles(List<String> explicitMappingFiles) {
 			this.explicitMappingFiles = explicitMappingFiles;
 			return this;
 		}
 
 		public ScanningContext detectClasses(boolean detectClasses) {
 			this.detectClasses = detectClasses;
 			return this;
 		}
 
 		public ScanningContext detectHbmFiles(boolean detectHbmFiles) {
 			this.detectHbmFiles = detectHbmFiles;
 			return this;
 		}
 
 		public ScanningContext searchOrm(boolean searchOrm) {
 			this.searchOrm = searchOrm;
 			return this;
 		}
 	}
 
 	private static void addMetadataFromScan(ScanningContext scanningContext, PersistenceMetadata metadata) throws IOException {
 		List<String> classes = metadata.getClasses();
 		List<String> packages = metadata.getPackages();
 		List<NamedInputStream> hbmFiles = metadata.getHbmfiles();
 		List<String> mappingFiles = metadata.getMappingFiles();
 		addScannedEntries( scanningContext, classes, packages, hbmFiles, mappingFiles );
 	}
 
 	private static void addScannedEntries(ScanningContext scanningContext, List<String> classes, List<String> packages, List<NamedInputStream> hbmFiles, List<String> mappingFiles) throws IOException {
 		Scanner scanner = scanningContext.scanner;
 		if (scanningContext.detectClasses) {
 			Set<Class<? extends Annotation>> annotationsToExclude = new HashSet<Class<? extends Annotation>>(3);
 			annotationsToExclude.add( Entity.class );
 			annotationsToExclude.add( MappedSuperclass.class );
 			annotationsToExclude.add( Embeddable.class );
 			Set<Class<?>> matchingClasses = scanner.getClassesInJar( scanningContext.url, annotationsToExclude );
 			for (Class<?> clazz : matchingClasses) {
 				classes.add( clazz.getName() );
 			}
 
 			Set<Package> matchingPackages = scanner.getPackagesInJar( scanningContext.url, new HashSet<Class<? extends Annotation>>(0) );
 			for (Package pkg : matchingPackages) {
 				packages.add( pkg.getName() );
 			}
 		}
 		Set<String> patterns = new HashSet<String>();
 		if (scanningContext.searchOrm) {
 			patterns.add( META_INF_ORM_XML );
 		}
 		if (scanningContext.detectHbmFiles) {
 			patterns.add( "**/*.hbm.xml" );
 		}
 		if ( mappingFiles != null) patterns.addAll( mappingFiles );
 		if (patterns.size() !=0) {
 			Set<NamedInputStream> files = scanner.getFilesInJar( scanningContext.url, patterns );
 			for (NamedInputStream file : files) {
 				hbmFiles.add( file );
 				if (mappingFiles != null) mappingFiles.remove( file.getName() );
 			}
 		}
 	}
 
 	/**
 	 * Process configuration from a PersistenceUnitInfo object; typically called by the container
 	 * via {@link javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory}.
 	 * In Hibernate EM, this correlates to {@link HibernatePersistence#createContainerEntityManagerFactory}
 	 *
 	 * @param info The persistence unit info passed in by the container (usually from processing a persistence.xml).
 	 * @param integration The map of integration properties from the container to configure the provider.
 	 *
 	 * @return this
 	 *
 	 * @see HibernatePersistence#createContainerEntityManagerFactory
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceUnitInfo info, Map integration) {
         if (LOG.isDebugEnabled()) LOG.debugf("Processing %s", LogHelper.logPersistenceUnitInfo(info));
         else LOG.processingPersistenceUnitInfoName(info.getPersistenceUnitName());
 
 		// Spec says the passed map may be null, so handle that to make further processing easier...
 		integration = integration != null ? Collections.unmodifiableMap( integration ) : CollectionHelper.EMPTY_MAP;
 
 		// See if we (Hibernate) are the persistence provider
 		String provider = (String) integration.get( AvailableSettings.PROVIDER );
 		if ( provider == null ) {
 			provider = info.getPersistenceProviderClassName();
 		}
 		if ( provider != null && ! provider.trim().startsWith( IMPLEMENTATION_NAME ) ) {
             LOG.requiredDifferentProvider(provider);
 			return null;
 		}
 
 		// set the classloader, passed in by the container in info, to set as the TCCL so that
 		// Hibernate uses it to properly resolve class references.
 		if ( info.getClassLoader() == null ) {
 			throw new IllegalStateException(
 					"[PersistenceUnit: " + info.getPersistenceUnitName() == null ? "" : info.getPersistenceUnitName()
 							+ "] " + "PersistenceUnitInfo.getClassLoader() id null" );
 		}
 		Thread thread = Thread.currentThread();
 		ClassLoader contextClassLoader = thread.getContextClassLoader();
 		boolean sameClassLoader = info.getClassLoader().equals( contextClassLoader );
 		if ( ! sameClassLoader ) {
 			overridenClassLoader = info.getClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		else {
 			overridenClassLoader = null;
 		}
 
 		// Best I can tell, 'workingVars' is some form of additional configuration contract.
 		// But it does not correlate 1-1 to EMF/SF settings.  It really is like a set of de-typed
 		// additional configuration info.  I think it makes better sense to define this as an actual
 		// contract if that was in fact the intent; the code here is pretty confusing.
 		try {
 			Map workingVars = new HashMap();
 			workingVars.put( AvailableSettings.PERSISTENCE_UNIT_NAME, info.getPersistenceUnitName() );
 			this.persistenceUnitName = info.getPersistenceUnitName();
 			List<String> entities = new ArrayList<String>( 50 );
 			if ( info.getManagedClassNames() != null ) entities.addAll( info.getManagedClassNames() );
 			List<NamedInputStream> hbmFiles = new ArrayList<NamedInputStream>();
 			List<String> packages = new ArrayList<String>();
 			List<String> xmlFiles = new ArrayList<String>( 50 );
 			List<XmlDocument> xmlDocuments = new ArrayList<XmlDocument>( 50 );
 			if ( info.getMappingFileNames() != null ) {
 				xmlFiles.addAll( info.getMappingFileNames() );
 			}
 			//Should always be true if the container is not dump
 			boolean searchForORMFiles = ! xmlFiles.contains( META_INF_ORM_XML );
 
 			ScanningContext context = new ScanningContext();
 			final Properties copyOfProperties = (Properties) info.getProperties().clone();
 			ConfigurationHelper.overrideProperties( copyOfProperties, integration );
 			context.scanner( buildScanner( copyOfProperties, integration ) )
 					.searchOrm( searchForORMFiles )
 					.explicitMappingFiles( null ); //URLs provided by the container already
 
 			//context for other JARs
 			setDetectedArtifactsOnScanningContext(context, info.getProperties(), null, false );
 			for ( URL jar : info.getJarFileUrls() ) {
 				context.url(jar);
 				scanForClasses( context, packages, entities, hbmFiles );
 			}
 
 			//main jar
 			context.url( info.getPersistenceUnitRootUrl() );
 			setDetectedArtifactsOnScanningContext( context, info.getProperties(), null, info.excludeUnlistedClasses() );
 			scanForClasses( context, packages, entities, hbmFiles );
 
 			Properties properties = info.getProperties() != null ? info.getProperties() : new Properties();
 			ConfigurationHelper.overrideProperties( properties, integration );
 
 			//FIXME entities is used to enhance classes and to collect annotated entities this should not be mixed
 			//fill up entities with the on found in xml files
 			addXMLEntities( xmlFiles, info, entities, xmlDocuments );
 
 			//FIXME send the appropriate entites.
 			if ( "true".equalsIgnoreCase( properties.getProperty( AvailableSettings.USE_CLASS_ENHANCER ) ) ) {
 				info.addTransformer( new InterceptFieldClassFileTransformer( entities ) );
 			}
 
 			workingVars.put( AvailableSettings.CLASS_NAMES, entities );
 			workingVars.put( AvailableSettings.PACKAGE_NAMES, packages );
 			workingVars.put( AvailableSettings.XML_FILE_NAMES, xmlFiles );
 			workingVars.put( PARSED_MAPPING_DOMS, xmlDocuments );
 
 			if ( hbmFiles.size() > 0 ) {
 				workingVars.put( AvailableSettings.HBXML_FILES, hbmFiles );
 			}
 
 			// validation factory
 			final Object validationFactory = integration.get( AvailableSettings.VALIDATION_FACTORY );
 			if ( validationFactory != null ) {
 				properties.put( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 			}
 
 			// validation-mode (integration has precedence)
 			{
 				final Object integrationValue = integration.get( AvailableSettings.VALIDATION_MODE );
 				if ( integrationValue != null ) {
 					properties.put( AvailableSettings.VALIDATION_MODE, integrationValue.toString() );
 				}
 				else if ( info.getValidationMode() != null ) {
 					properties.put( AvailableSettings.VALIDATION_MODE, info.getValidationMode().name() );
 				}
 			}
 
 			// shared-cache-mode (integration has precedence)
 			{
 				final Object integrationValue = integration.get( AvailableSettings.SHARED_CACHE_MODE );
 				if ( integrationValue != null ) {
 					properties.put( AvailableSettings.SHARED_CACHE_MODE, integrationValue.toString() );
 				}
 				else if ( info.getSharedCacheMode() != null ) {
 					properties.put( AvailableSettings.SHARED_CACHE_MODE, info.getSharedCacheMode().name() );
 				}
 			}
 
 			//datasources
 			Boolean isJTA = null;
 			boolean overridenDatasource = false;
 			if ( integration.containsKey( AvailableSettings.JTA_DATASOURCE ) ) {
 				String dataSource = (String) integration.get( AvailableSettings.JTA_DATASOURCE );
 				overridenDatasource = true;
 				properties.setProperty( Environment.DATASOURCE, dataSource );
 				isJTA = Boolean.TRUE;
 			}
 			if ( integration.containsKey( AvailableSettings.NON_JTA_DATASOURCE ) ) {
 				String dataSource = (String) integration.get( AvailableSettings.NON_JTA_DATASOURCE );
 				overridenDatasource = true;
 				properties.setProperty( Environment.DATASOURCE, dataSource );
 				if (isJTA == null) isJTA = Boolean.FALSE;
 			}
 
 			if ( ! overridenDatasource && ( info.getJtaDataSource() != null || info.getNonJtaDataSource() != null ) ) {
 				isJTA = info.getJtaDataSource() != null ? Boolean.TRUE : Boolean.FALSE;
 				this.setDataSource(
 						isJTA ? info.getJtaDataSource() : info.getNonJtaDataSource()
 				);
 				this.setProperty(
 						Environment.CONNECTION_PROVIDER, InjectedDataSourceConnectionProvider.class.getName()
 				);
 			}
 			/*
 			 * If explicit type => use it
 			 * If a JTA DS is used => JTA transaction,
 			 * if a non JTA DS is used => RESOURCe_LOCAL
 			 * if none, set to JavaEE default => JTA transaction
 			 */
 			PersistenceUnitTransactionType transactionType = info.getTransactionType();
 			if (transactionType == null) {
 				if (isJTA == Boolean.TRUE) {
 					transactionType = PersistenceUnitTransactionType.JTA;
 				}
 				else if ( isJTA == Boolean.FALSE ) {
 					transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 				}
 				else {
 					transactionType = PersistenceUnitTransactionType.JTA;
 				}
 			}
 			defineTransactionType( transactionType, workingVars );
 			configure( properties, workingVars );
 		}
 		finally {
 			//After EMF, set the CCL back
 			if ( ! sameClassLoader ) {
 				thread.setContextClassLoader( contextClassLoader );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Processes {@code xmlFiles} argument and populates:<ul>
 	 * <li>the {@code entities} list with encountered classnames</li>
 	 * <li>the {@code xmlDocuments} list with parsed/validated {@link XmlDocument} corrolary to each xml file</li>
 	 * </ul>
 	 *
 	 * @param xmlFiles The XML resource names; these will be resolved by classpath lookup and parsed/validated.
 	 * @param info The PUI
 	 * @param entities (output) The names of all encountered "mapped" classes
 	 * @param xmlDocuments (output) The list of {@link XmlDocument} instances of each entry in {@code xmlFiles}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	private void addXMLEntities(
 			List<String> xmlFiles,
 			PersistenceUnitInfo info,
 			List<String> entities,
 			List<XmlDocument> xmlDocuments) {
 		//TODO handle inputstream related hbm files
 		ClassLoader classLoaderToUse = info.getNewTempClassLoader();
 		if ( classLoaderToUse == null ) {
             LOG.persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 			return;
 		}
 		for ( final String xmlFile : xmlFiles ) {
 			final InputStream fileInputStream = classLoaderToUse.getResourceAsStream( xmlFile );
 			if ( fileInputStream == null ) {
                 LOG.unableToResolveMappingFile(xmlFile);
 				continue;
 			}
 			final InputSource inputSource = new InputSource( fileInputStream );
 
 			XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument(
 					cfg.getEntityResolver(),
 					inputSource,
 					new OriginImpl( "persistence-unit-info", xmlFile )
 			);
 			xmlDocuments.add( metadataXml );
 			try {
 				final Element rootElement = metadataXml.getDocumentTree().getRootElement();
 				if ( rootElement != null && "entity-mappings".equals( rootElement.getName() ) ) {
 					Element element = rootElement.element( "package" );
 					String defaultPackage = element != null ? element.getTextTrim() : null;
 					List<Element> elements = rootElement.elements( "entity" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 					elements = rootElement.elements( "mapped-superclass" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 					elements = rootElement.elements( "embeddable" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 				}
 				else if ( rootElement != null && "hibernate-mappings".equals( rootElement.getName() ) ) {
 					//FIXME include hbm xml entities to enhance them but entities is also used to collect annotated entities
 				}
 			}
 			finally {
 				try {
 					fileInputStream.close();
 				}
 				catch (IOException ioe) {
                     LOG.unableToCloseInputStream(ioe);
 				}
 			}
 		}
 		xmlFiles.clear();
 	}
 
 	private void defineTransactionType(Object overridenTxType, Map workingVars) {
 		if ( overridenTxType == null ) {
 //			if ( transactionType == null ) {
 //				transactionType = PersistenceUnitTransactionType.JTA; //this is the default value
 //			}
 			//nothing to override
 		}
 		else if ( overridenTxType instanceof String ) {
 			transactionType = PersistenceXmlLoader.getTransactionType( (String) overridenTxType );
 		}
 		else if ( overridenTxType instanceof PersistenceUnitTransactionType ) {
 			transactionType = (PersistenceUnitTransactionType) overridenTxType;
 		}
 		else {
 			throw new PersistenceException( getExceptionHeader() +
 					AvailableSettings.TRANSACTION_TYPE + " of the wrong class type"
 							+ ": " + overridenTxType.getClass()
 			);
 		}
 
 	}
 
 	public Ejb3Configuration setProperty(String key, String value) {
 		cfg.setProperty( key, value );
 		return this;
 	}
 
 	/**
 	 * Set ScanningContext detectClasses and detectHbmFiles according to context
 	 */
 	private void setDetectedArtifactsOnScanningContext(ScanningContext context,
 													   Properties properties,
 													   Map overridenProperties,
 													   boolean excludeIfNotOverriden) {
 
 		boolean detectClasses = false;
 		boolean detectHbm = false;
 		String detectSetting = overridenProperties != null ?
 				(String) overridenProperties.get( AvailableSettings.AUTODETECTION ) :
 				null;
 		detectSetting = detectSetting == null ?
 				properties.getProperty( AvailableSettings.AUTODETECTION) :
 				detectSetting;
 		if ( detectSetting == null && excludeIfNotOverriden) {
 			//not overriden through HibernatePersistence.AUTODETECTION so we comply with the spec excludeUnlistedClasses
 			context.detectClasses( false ).detectHbmFiles( false );
 			return;
 		}
 
 		if ( detectSetting == null){
 			detectSetting = "class,hbm";
 		}
 		StringTokenizer st = new StringTokenizer( detectSetting, ", ", false );
 		while ( st.hasMoreElements() ) {
 			String element = (String) st.nextElement();
 			if ( "class".equalsIgnoreCase( element ) ) detectClasses = true;
 			if ( "hbm".equalsIgnoreCase( element ) ) detectHbm = true;
 		}
         LOG.debugf("Detect class: %s; detect hbm: %s", detectClasses, detectHbm);
 		context.detectClasses( detectClasses ).detectHbmFiles( detectHbm );
 	}
 
 	private void scanForClasses(ScanningContext scanningContext, List<String> packages, List<String> entities, List<NamedInputStream> hbmFiles) {
 		if (scanningContext.url == null) {
             LOG.containerProvidingNullPersistenceUnitRootUrl();
 			return;
 		}
 		try {
 			addScannedEntries( scanningContext, entities, packages, hbmFiles, null );
 		}
 		catch (RuntimeException e) {
 			throw new RuntimeException( "error trying to scan <jar-file>: " + scanningContext.url.toString(), e );
 		}
 		catch( IOException e ) {
 			throw new RuntimeException( "Error while reading " + scanningContext.url.toString(), e );
 		}
 	}
 
 	/**
 	 * create a factory from a list of properties and
 	 * HibernatePersistence.CLASS_NAMES -> Collection<String> (use to list the classes from config files
 	 * HibernatePersistence.PACKAGE_NAMES -> Collection<String> (use to list the mappings from config files
 	 * HibernatePersistence.HBXML_FILES -> Collection<InputStream> (input streams of hbm files)
 	 * HibernatePersistence.LOADED_CLASSES -> Collection<Class> (list of loaded classes)
 	 * <p/>
 	 * <b>Used by JBoss AS only</b>
 	 * @deprecated use the Java Persistence API
 	 */
 	// This is used directly by JBoss so don't remove until further notice.  bill@jboss.org
 	@Deprecated
     public EntityManagerFactory createEntityManagerFactory(Map workingVars) {
 		configure( workingVars );
 		return buildEntityManagerFactory();
 	}
 
 	/**
 	 * Process configuration and build an EntityManagerFactory <b>when</b> the configuration is ready
 	 * @deprecated
 	 */
 	@Deprecated
     public EntityManagerFactory createEntityManagerFactory() {
 		configure( cfg.getProperties(), new HashMap() );
 		return buildEntityManagerFactory();
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory() {
 		return buildEntityManagerFactory( new BasicServiceRegistryImpl( cfg.getProperties() ) );
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory(ServiceRegistry serviceRegistry) {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			configure( (Properties)null, null );
 			NamingHelper.bind(this);
 			return new EntityManagerFactoryImpl(
 					transactionType,
 					discardOnClose,
 					getSessionInterceptorClass( cfg.getProperties() ),
 					cfg,
 					serviceRegistry
 			);
 		}
 		catch (HibernateException e) {
 			throw new PersistenceException( getExceptionHeader() + "Unable to build EntityManagerFactory", e );
 		}
 		finally {
 			if (thread != null) {
 				thread.setContextClassLoader( contextClassLoader );
 			}
 		}
 	}
 
 	private Class getSessionInterceptorClass(Properties properties) {
 		String sessionInterceptorClassname = (String) properties.get( AvailableSettings.SESSION_INTERCEPTOR );
 		if ( StringHelper.isNotEmpty( sessionInterceptorClassname ) ) {
 			try {
 				Class interceptorClass = ReflectHelper.classForName(
 						sessionInterceptorClassname, Ejb3Configuration.class
 				);
 				interceptorClass.newInstance();
 				return interceptorClass;
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to load "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to instanciate "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to instanciate "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
         }
         return null;
 	}
 
 	public Reference getReference() throws NamingException {
         LOG.debugf("Returning a Reference to the Ejb3Configuration");
 		ByteArrayOutputStream stream = new ByteArrayOutputStream();
 		ObjectOutput out = null;
 		byte[] serialized;
 		try {
 			out = new ObjectOutputStream( stream );
 			out.writeObject( this );
 			out.close();
 			serialized = stream.toByteArray();
 			stream.close();
 		}
 		catch (IOException e) {
 			NamingException namingException = new NamingException( "Unable to serialize Ejb3Configuration" );
 			namingException.setRootCause( e );
 			throw namingException;
 		}
 
 		return new Reference(
 				Ejb3Configuration.class.getName(),
 				new BinaryRefAddr("object", serialized ),
 				Ejb3ConfigurationObjectFactory.class.getName(),
 				null
 		);
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Ejb3Configuration configure(Map configValues) {
 		Properties props = new Properties();
 		if ( configValues != null ) {
 			props.putAll( configValues );
 			//remove huge non String elements for a clean props
 			props.remove( AvailableSettings.CLASS_NAMES );
 			props.remove( AvailableSettings.PACKAGE_NAMES );
 			props.remove( AvailableSettings.HBXML_FILES );
 			props.remove( AvailableSettings.LOADED_CLASSES );
 		}
 		return configure( props, configValues );
 	}
 
 	/**
 	 * Configures this configuration object from 2 distinctly different sources.
 	 *
 	 * @param properties These are the properties that came from the user, either via
 	 * a persistence.xml or explicitly passed in to one of our
 	 * {@link javax.persistence.spi.PersistenceProvider}/{@link HibernatePersistence} contracts.
 	 * @param workingVars Is collection of settings which need to be handled similarly
 	 * between the 2 main bootstrap methods, but where the values are determine very differently
 	 * by each bootstrap method.  todo eventually make this a contract (class/interface)
 	 *
 	 * @return The configured configuration
 	 *
 	 * @see HibernatePersistence
 	 */
 	private Ejb3Configuration configure(Properties properties, Map workingVars) {
 		//TODO check for people calling more than once this method (except buildEMF)
 		if (isConfigurationProcessed) return this;
 		isConfigurationProcessed = true;
 		Properties preparedProperties = prepareProperties( properties, workingVars );
 		if ( workingVars == null ) workingVars = CollectionHelper.EMPTY_MAP;
 
 		if ( preparedProperties.containsKey( AvailableSettings.CFG_FILE ) ) {
 			String cfgFileName = preparedProperties.getProperty( AvailableSettings.CFG_FILE );
 			cfg.configure( cfgFileName );
 		}
 
 		cfg.addProperties( preparedProperties ); //persistence.xml has priority over hibernate.cfg.xml
 
 		addClassesToSessionFactory( workingVars );
 
 		//processes specific properties
 		List<String> jaccKeys = new ArrayList<String>();
 
 
 		Interceptor defaultInterceptor = DEFAULT_CONFIGURATION.getInterceptor();
 		NamingStrategy defaultNamingStrategy = DEFAULT_CONFIGURATION.getNamingStrategy();
 
 		Iterator propertyIt = preparedProperties.keySet().iterator();
 		while ( propertyIt.hasNext() ) {
 			Object uncastObject = propertyIt.next();
 			//had to be safe
 			if ( uncastObject != null && uncastObject instanceof String ) {
 				String propertyKey = (String) uncastObject;
 				if ( propertyKey.startsWith( AvailableSettings.CLASS_CACHE_PREFIX ) ) {
 					setCacheStrategy( propertyKey, preparedProperties, true, workingVars );
 				}
 				else if ( propertyKey.startsWith( AvailableSettings.COLLECTION_CACHE_PREFIX ) ) {
 					setCacheStrategy( propertyKey, preparedProperties, false, workingVars );
 				}
 				else if ( propertyKey.startsWith( AvailableSettings.JACC_PREFIX )
 						&& ! ( propertyKey.equals( AvailableSettings.JACC_CONTEXT_ID )
 						|| propertyKey.equals( AvailableSettings.JACC_ENABLED ) ) ) {
 					jaccKeys.add( propertyKey );
 				}
 			}
 		}
 		final Interceptor interceptor = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				defaultInterceptor,
 				cfg.getInterceptor(),
 				AvailableSettings.INTERCEPTOR,
 				"interceptor",
 				Interceptor.class
 		);
 		if ( interceptor != null ) {
 			cfg.setInterceptor( interceptor );
 		}
 		final NamingStrategy namingStrategy = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				defaultNamingStrategy,
 				cfg.getNamingStrategy(),
 				AvailableSettings.NAMING_STRATEGY,
 				"naming strategy",
 				NamingStrategy.class
 		);
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
 
 		final SessionFactoryObserver observer = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				null,
 				cfg.getSessionFactoryObserver(),
 				AvailableSettings.SESSION_FACTORY_OBSERVER,
 				"SessionFactory observer",
 				SessionFactoryObserver.class
 		);
 		if ( observer != null ) {
 			cfg.setSessionFactoryObserver( observer );
 		}
 
 		if ( jaccKeys.size() > 0 ) {
 			addSecurity( jaccKeys, preparedProperties, workingVars );
 		}
 
 		//initialize listeners
 		listenerConfigurator.setProperties( preparedProperties );
 		listenerConfigurator.configure();
 
 		//some spec compliance checking
 		//TODO centralize that?
         if (!"true".equalsIgnoreCase(cfg.getProperty(Environment.AUTOCOMMIT))) LOG.jdbcAutoCommitFalseBreaksEjb3Spec(Environment.AUTOCOMMIT);
         discardOnClose = preparedProperties.getProperty(AvailableSettings.DISCARD_PC_ON_CLOSE).equals("true");
 		return this;
 	}
 
 	private <T> T instantiateCustomClassFromConfiguration(
 			Properties preparedProperties,
 			T defaultObject,
 			T cfgObject,
 			String propertyName,
 			String classDescription,
 			Class<T> objectClass) {
 		if ( preparedProperties.containsKey( propertyName )
 				&& ( cfgObject == null || cfgObject.equals( defaultObject ) ) ) {
 			//cfg.setXxx has precedence over configuration file
 			String className = preparedProperties.getProperty( propertyName );
 			try {
 				Class<T> clazz = classForName( className );
 				return clazz.newInstance();
 				//cfg.setInterceptor( (Interceptor) instance.newInstance() );
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to find " + classDescription + " class: " + className, e
 				);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to access " + classDescription + " class: " + className, e
 				);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to instantiate " + classDescription + " class: " + className, e
 				);
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
index 070524c50b..8231afe8b8 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
@@ -1,220 +1,220 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
  */
 package org.hibernate.ejb;
 
 import javax.persistence.Cache;
 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceUnitUtil;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.LoadState;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
 import org.hibernate.ejb.metamodel.MetamodelImpl;
 import org.hibernate.ejb.util.PersistenceUtilHelper;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 
 /**
  * Actual Hiberate implementation of {@link javax.persistence.EntityManagerFactory}.
  *
  * @author Gavin King
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryImpl implements HibernateEntityManagerFactory {
 	private final SessionFactory sessionFactory;
 	private final PersistenceUnitTransactionType transactionType;
 	private final boolean discardOnClose;
 	private final Class sessionInterceptorClass;
 	private final CriteriaBuilderImpl criteriaBuilder;
 	private final Metamodel metamodel;
 	private final HibernatePersistenceUnitUtil util;
 	private final Map<String,Object> properties;
 
 	private final PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
 
 	@SuppressWarnings( "unchecked" )
 	public EntityManagerFactoryImpl(
 			PersistenceUnitTransactionType transactionType,
 			boolean discardOnClose,
 			Class sessionInterceptorClass,
 			Configuration cfg,
 			ServiceRegistry serviceRegistry) {
 		this.sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		this.transactionType = transactionType;
 		this.discardOnClose = discardOnClose;
 		this.sessionInterceptorClass = sessionInterceptorClass;
 		final Iterator<PersistentClass> classes = cfg.getClassMappings();
 		//a safe guard till we are confident that metamodel is wll tested
 		if ( !"disabled".equalsIgnoreCase( cfg.getProperty( "hibernate.ejb.metamodel.generation" ) ) ) {
 			this.metamodel = MetamodelImpl.buildMetamodel( classes, ( SessionFactoryImplementor ) sessionFactory );
 		}
 		else {
 			this.metamodel = null;
 		}
 		this.criteriaBuilder = new CriteriaBuilderImpl( this );
 		this.util = new HibernatePersistenceUnitUtil( this );
 
 		HashMap<String,Object> props = new HashMap<String, Object>();
 		addAll( props, ( (SessionFactoryImplementor) sessionFactory ).getProperties() );
 		addAll( props, cfg.getProperties() );
 		this.properties = Collections.unmodifiableMap( props );
 	}
 
 	private static void addAll(HashMap<String, Object> propertyMap, Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( String.class.isInstance( entry.getKey() ) ) {
 				propertyMap.put( (String)entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	public EntityManager createEntityManager() {
 		return createEntityManager( null );
 	}
 
 	public EntityManager createEntityManager(Map map) {
 		//TODO support discardOnClose, persistencecontexttype?, interceptor,
 		return new EntityManagerImpl(
 				this, PersistenceContextType.EXTENDED, transactionType,
 				discardOnClose, sessionInterceptorClass, map
 		);
 	}
 
 	public CriteriaBuilder getCriteriaBuilder() {
 		return criteriaBuilder;
 	}
 
 	public Metamodel getMetamodel() {
 		return metamodel;
 	}
 
 	public void close() {
 		sessionFactory.close();
 	}
 
 	public Map<String, Object> getProperties() {
 		return properties;
 	}
 
 	public Cache getCache() {
 		// TODO : cache the cache reference?
 		if ( ! isOpen() ) {
 			throw new IllegalStateException("EntityManagerFactory is closed");
 		}
 		return new JPACache( sessionFactory );
 	}
 
 	public PersistenceUnitUtil getPersistenceUnitUtil() {
 		if ( ! isOpen() ) {
 			throw new IllegalStateException("EntityManagerFactory is closed");
 		}
 		return util;
 	}
 
 	public boolean isOpen() {
 		return ! sessionFactory.isClosed();
 	}
 
 	public SessionFactory getSessionFactory() {
 		return sessionFactory;
 	}
 
 	private static class JPACache implements Cache {
 		private SessionFactory sessionFactory;
 
 		private JPACache(SessionFactory sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		public boolean contains(Class entityClass, Object identifier) {
 			return sessionFactory.getCache().containsEntity( entityClass, ( Serializable ) identifier );
 		}
 
 		public void evict(Class entityClass, Object identifier) {
 			sessionFactory.getCache().evictEntity( entityClass, ( Serializable ) identifier );
 		}
 
 		public void evict(Class entityClass) {
 			sessionFactory.getCache().evictEntityRegion( entityClass );
 		}
 
 		public void evictAll() {
 			sessionFactory.getCache().evictEntityRegions();
 // TODO : if we want to allow an optional clearing of all cache data, the additional calls would be:
 //			sessionFactory.getCache().evictCollectionRegions();
 //			sessionFactory.getCache().evictQueryRegions();
 		}
 	}
 
 	private static class HibernatePersistenceUnitUtil implements PersistenceUnitUtil, Serializable {
 		private final HibernateEntityManagerFactory emf;
 		private transient PersistenceUtilHelper.MetadataCache cache;
 
 		private HibernatePersistenceUnitUtil(EntityManagerFactoryImpl emf) {
 			this.emf = emf;
 			this.cache = emf.cache;
 		}
 
 		public boolean isLoaded(Object entity, String attributeName) {
 			LoadState state = PersistenceUtilHelper.isLoadedWithoutReference( entity, attributeName, cache );
 			if (state == LoadState.LOADED) {
 				return true;
 			}
 			else if (state == LoadState.NOT_LOADED ) {
 				return false;
 			}
 			else {
 				return PersistenceUtilHelper.isLoadedWithReference( entity, attributeName, cache ) != LoadState.NOT_LOADED;
 			}
 		}
 
 		public boolean isLoaded(Object entity) {
 			return PersistenceUtilHelper.isLoaded( entity ) != LoadState.NOT_LOADED;
 		}
 
 		public Object getIdentifier(Object entity) {
 			final Class entityClass = Hibernate.getClass( entity );
 			final ClassMetadata classMetadata = emf.getSessionFactory().getClassMetadata( entityClass );
 			if (classMetadata == null) {
 				throw new IllegalArgumentException( entityClass + " is not an entity" );
 			}
 			//TODO does that work for @IdClass?
 			return classMetadata.getIdentifier( entity, EntityMode.POJO );
 		}
 	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractOneSessionTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractOneSessionTest.java
index f098a9c3d1..8c5e97aa5c 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractOneSessionTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractOneSessionTest.java
@@ -1,111 +1,110 @@
 package org.hibernate.envers.test;
 
 import java.io.File;
 import java.net.URISyntaxException;
 import java.net.URL;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
 import org.hibernate.envers.AuditReader;
 import org.hibernate.envers.AuditReaderFactory;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.testng.annotations.AfterClass;
 import org.testng.annotations.BeforeClass;
 import org.testng.annotations.BeforeMethod;
 import org.testng.annotations.Optional;
 import org.testng.annotations.Parameters;
 
 /**
  * Base class for testing envers with Session when the same session and
  * auditReader must be used for the hole test.
  *
  * @author Hern&aacute;n Chanfreau
  *
  */
 public abstract class AbstractOneSessionTest  {
 
 
 	protected Configuration config;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactory sessionFactory;
 	private Session session ;
 	private AuditReader auditReader;
 
 
 	@BeforeClass
     @Parameters("auditStrategy")
     public void init(@Optional String auditStrategy) throws URISyntaxException {
         config = new Configuration();
         URL url = Thread.currentThread().getContextClassLoader().getResource(getHibernateConfigurationFileName());
         config.configure(new File(url.toURI()));
 
         if (auditStrategy != null && !"".equals(auditStrategy)) {
             config.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
         }
 
         this.initMappings();
 
         serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( config.getProperties() );
 		sessionFactory = config.buildSessionFactory( serviceRegistry );
     }
 
 	protected abstract void initMappings() throws MappingException, URISyntaxException ;
 
 	protected String getHibernateConfigurationFileName(){
 		return "hibernate.test.session-cfg.xml";
 	}
 
 
 	private SessionFactory getSessionFactory(){
 		return sessionFactory;
     }
 
 	@AfterClass
 	public void closeSessionFactory() {
 		try {
 	   		sessionFactory.close();
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 				serviceRegistry = null;
 			}
 		}
 	}
 
 	/**
 	 * Creates a new session and auditReader only if there is nothing created
 	 * before
 	 */
 	@BeforeMethod
 	public void initializeSession() {
 		if (getSession() == null) {
 		      session = getSessionFactory().openSession();
 		      auditReader = AuditReaderFactory.get(session);
 		}
 	}
 
 	/**
 	 * Creates a new session and auditReader.
 	 */
 	public void forceNewSession() {
 	      session = getSessionFactory().openSession();
 	      auditReader = AuditReaderFactory.get(session);
 	}
 
 	protected Session getSession() {
 		return session;
 	}
 
 
 
 	protected AuditReader getAuditReader() {
 		return auditReader;
 	}
 
 
 
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractSessionTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractSessionTest.java
index 0e60545efb..0cb650dd56 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractSessionTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractSessionTest.java
@@ -1,96 +1,95 @@
 package org.hibernate.envers.test;
 
 import java.io.File;
 import java.net.URISyntaxException;
 import java.net.URL;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.Environment;
 import org.hibernate.envers.AuditReader;
 import org.hibernate.envers.AuditReaderFactory;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.testng.annotations.AfterClass;
 import org.testng.annotations.BeforeClass;
 import org.testng.annotations.BeforeMethod;
 import org.testng.annotations.Optional;
 import org.testng.annotations.Parameters;
 
 /**
  * Base class for testing envers with Session.
  *
  * @author Hern&aacute;n Chanfreau
  *
  */
 public abstract class AbstractSessionTest {
 
 	protected Configuration config;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactory sessionFactory;
 	private Session session ;
 	private AuditReader auditReader;
 
 
 	@BeforeClass
     @Parameters("auditStrategy")
     public void init(@Optional String auditStrategy) throws URISyntaxException {
         config = new Configuration();
         URL url = Thread.currentThread().getContextClassLoader().getResource(getHibernateConfigurationFileName());
         config.configure(new File(url.toURI()));
 
         if (auditStrategy != null && !"".equals(auditStrategy)) {
             config.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
         }
 
         this.initMappings();
 
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( config.getProperties() );
 		sessionFactory = config.buildSessionFactory( serviceRegistry );
     }
 
 	protected abstract void initMappings() throws MappingException, URISyntaxException ;
 
 	protected String getHibernateConfigurationFileName(){
 		return "hibernate.test.session-cfg.xml";
 	}
 
 
 	private SessionFactory getSessionFactory(){
 		return sessionFactory;
     }
 
 
     @BeforeMethod
     public void newSessionFactory() {
       session = getSessionFactory().openSession();
       auditReader = AuditReaderFactory.get(session);
     }
 
 	@AfterClass
 	public void closeSessionFactory() {
 		try {
 	   		sessionFactory.close();
 		}
 		finally {
 			if ( serviceRegistry != null ) {
 				ServiceRegistryBuilder.destroy( serviceRegistry );
 				serviceRegistry = null;
 			}
 		}
 	}
 
 
 	protected Session getSession() {
 		return session;
 	}
 
 
 
 	protected AuditReader getAuditReader() {
 		return auditReader;
 	}
 
 }
 
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
index 60a6564eaf..182c1913b0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
@@ -1,116 +1,116 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.Stoppable;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
+import org.hibernate.service.UnknownUnwrapTypeException;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 
 /**
  * A {@link ConnectionProvider} implementation adding JTA-style transactionality around the returned
  * connections using the {@link DualNodeJtaTransactionManagerImpl}.
  * 
  * @author Brian Stansberry
  */
 public class DualNodeConnectionProviderImpl implements ConnectionProvider, Configurable {
    private static ConnectionProvider actualConnectionProvider = ConnectionProviderBuilder.buildConnectionProvider();
    private String nodeId;
    private boolean isTransactional;
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return DualNodeConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ||
 				ConnectionProvider.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( DualNodeConnectionProviderImpl.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else if ( ConnectionProvider.class.isAssignableFrom( unwrapType ) ) {
 			return (T) actualConnectionProvider;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
    public static ConnectionProvider getActualConnectionProvider() {
       return actualConnectionProvider;
    }
 
    public void setNodeId(String nodeId) throws HibernateException {
       if (nodeId == null) {
          throw new HibernateException( "nodeId not configured" );
 	  }
 	  this.nodeId = nodeId;
    }
 
    public Connection getConnection() throws SQLException {
       DualNodeJtaTransactionImpl currentTransaction = DualNodeJtaTransactionManagerImpl
                .getInstance(nodeId).getCurrentTransaction();
       if (currentTransaction == null) {
          isTransactional = false;
          return actualConnectionProvider.getConnection();
       } else {
          isTransactional = true;
          Connection connection = currentTransaction.getEnlistedConnection();
          if (connection == null) {
             connection = actualConnectionProvider.getConnection();
             currentTransaction.enlistConnection(connection);
          }
          return connection;
       }
    }
 
    public void closeConnection(Connection conn) throws SQLException {
       if (!isTransactional) {
          conn.close();
       }
    }
 
    public void close() throws HibernateException {
 	   if ( actualConnectionProvider instanceof Stoppable ) {
 		   ( ( Stoppable ) actualConnectionProvider ).stop();
 	   }
    }
 
    public boolean supportsAggressiveRelease() {
       return true;
    }
 
 	@Override
 	public void configure(Map configurationValues) {
 		nodeId = (String) configurationValues.get( "nodeId" );
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java
index a3eb3d42ad..a479df3be1 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/JBossStandaloneJtaExampleTest.java
@@ -1,305 +1,305 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
  */
 package org.hibernate.test.cache.infinispan.tm;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Iterator;
 import java.util.Properties;
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.naming.Name;
 import javax.naming.NameNotFoundException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import javax.transaction.Status;
 import javax.transaction.TransactionManager;
 import javax.transaction.UserTransaction;
 import junit.framework.TestCase;
 import org.enhydra.jdbc.standard.StandardXADataSource;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.stat.Statistics;
 import org.hibernate.test.cache.infinispan.functional.Item;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.infinispan.transaction.lookup.JBossStandaloneJTAManagerLookup;
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 import org.jboss.util.naming.NonSerializableFactory;
 import org.jnp.interfaces.NamingContext;
 import org.jnp.server.Main;
 import org.jnp.server.NamingServer;
 
 /**
  * This is an example test based on http://community.jboss.org/docs/DOC-14617 that shows how to interact with
  * Hibernate configured with Infinispan second level cache provider using JTA transactions.
  *
  * In this test, an XADataSource wrapper is in use where we have associated our transaction manager to it so that
  * commits/rollbacks are propagated to the database as well.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class JBossStandaloneJtaExampleTest extends TestCase {
    private static final Log log = LogFactory.getLog(JBossStandaloneJtaExampleTest.class);
    private static final JBossStandaloneJTAManagerLookup lookup = new JBossStandaloneJTAManagerLookup();
    Context ctx;
    Main jndiServer;
    private ServiceRegistry serviceRegistry;
 
    @Override
    protected void setUp() throws Exception {
       super.setUp();
 	  serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
       jndiServer = startJndiServer();
       ctx = createJndiContext();
       bindTransactionManager();
       bindUserTransaction();
       bindDataSource();
    }
 
    @Override
    protected void tearDown() throws Exception {
       try {
          super.tearDown();
          ctx.close();
          jndiServer.stop();
 	  }
 	  finally {
 		  if ( serviceRegistry != null ) {
 			  ServiceRegistryBuilder.destroy( serviceRegistry );
 		  }
 	  }
    }
 
    public void testPersistAndLoadUnderJta() throws Exception {
       Item item;
       SessionFactory sessionFactory = buildSessionFactory();
       try {
          UserTransaction ut = (UserTransaction) ctx.lookup("UserTransaction");
          ut.begin();
          try {
             Session session = sessionFactory.openSession();
             session.getTransaction().begin();
             item = new Item("anItem", "An item owned by someone");
             session.persist(item);
             session.getTransaction().commit();
             session.close();
          } catch(Exception e) {
             ut.setRollbackOnly();
             throw e;
          } finally {
             if (ut.getStatus() == Status.STATUS_ACTIVE)
                ut.commit();
             else
                ut.rollback();
          }
 
          ut = (UserTransaction) ctx.lookup("UserTransaction");
          ut.begin();
          try {
             Session session = sessionFactory.openSession();
             session.getTransaction().begin();
             Item found = (Item) session.load(Item.class, item.getId());
             Statistics stats = session.getSessionFactory().getStatistics();
             log.info(stats.toString());
             assertEquals(item.getDescription(), found.getDescription());
             assertEquals(0, stats.getSecondLevelCacheMissCount());
             assertEquals(1, stats.getSecondLevelCacheHitCount());
             session.delete(found);
             session.getTransaction().commit();
             session.close();
          } catch(Exception e) {
             ut.setRollbackOnly();
             throw e;
          } finally {
             if (ut.getStatus() == Status.STATUS_ACTIVE)
                ut.commit();
             else
                ut.rollback();
          }
 
          ut = (UserTransaction) ctx.lookup("UserTransaction");
          ut.begin();
          try {
             Session session = sessionFactory.openSession();
             session.getTransaction().begin();
             assertNull(session.get(Item.class, item.getId()));
             session.getTransaction().commit();
             session.close();
          } catch(Exception e) {
             ut.setRollbackOnly();
             throw e;
          } finally {
             if (ut.getStatus() == Status.STATUS_ACTIVE)
                ut.commit();
             else
                ut.rollback();
          }
       } finally {
          if (sessionFactory != null)
             sessionFactory.close();
       }
 
    }
 
    public static class ExtendedXADataSource extends StandardXADataSource { // XAPOOL
       @Override
       public Connection getConnection() throws SQLException {
 
          if (getTransactionManager() == null) { // although already set before, it results null again after retrieving the datasource by jndi
             TransactionManager tm;  // this is because the TransactionManager information is not serialized.
             try {
                tm = lookup.getTransactionManager();
             } catch (Exception e) {
                throw new SQLException(e);
             }
             setTransactionManager(tm);  //  resets the TransactionManager on the datasource retrieved by jndi,
             //  this makes the datasource JTA-aware
          }
 
          // According to Enhydra documentation, here we must return the connection of our XAConnection
          // see http://cvs.forge.objectweb.org/cgi-bin/viewcvs.cgi/xapool/xapool/examples/xapooldatasource/DatabaseHelper.java?sortby=rev
          return super.getXAConnection().getConnection();
       }
 
       @Override
       public <T> T unwrap(Class<T> iface) throws SQLException {
          return null;  // JDK6 stuff
       }
 
       @Override
       public boolean isWrapperFor(Class<?> iface) throws SQLException {
          return false;  // JDK6 stuff
       }
    }
 
    private Main startJndiServer() throws Exception {
       // Create an in-memory jndi
       NamingServer namingServer = new NamingServer();
       NamingContext.setLocal(namingServer);
       Main namingMain = new Main();
       namingMain.setInstallGlobalService(true);
       namingMain.setPort(-1);
       namingMain.start();
       return namingMain;
    }
 
    private Context createJndiContext() throws Exception {
       Properties props = new Properties();
       props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
       props.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
       return new InitialContext(props);
    }
 
    private void bindTransactionManager() throws Exception {
       // as JBossTransactionManagerLookup extends JNDITransactionManagerLookup we must also register the TransactionManager
       bind("java:/TransactionManager", lookup.getTransactionManager(), lookup.getTransactionManager().getClass(), ctx);
    }
 
    private void bindUserTransaction() throws Exception {
       // also the UserTransaction must be registered on jndi: org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory#getUserTransaction() requires this
       bind("UserTransaction", lookup.getUserTransaction(), lookup.getUserTransaction().getClass(), ctx);
    }
 
    private void bindDataSource() throws Exception {
       ExtendedXADataSource xads = new ExtendedXADataSource();
       xads.setDriverName("org.h2.Driver");
       xads.setUrl("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE");
       ctx.bind("java:/MyDatasource", xads);
    }
 
    /**
     * Helper method that binds the a non serializable object to the JNDI tree.
     *
     * @param jndiName  Name under which the object must be bound
     * @param who       Object to bind in JNDI
     * @param classType Class type under which should appear the bound object
     * @param ctx       Naming context under which we bind the object
     * @throws Exception Thrown if a naming exception occurs during binding
     */
    private void bind(String jndiName, Object who, Class classType, Context ctx) throws Exception {
       // Ah ! This service isn't serializable, so we use a helper class
       NonSerializableFactory.bind(jndiName, who);
       Name n = ctx.getNameParser("").parse(jndiName);
       while (n.size() > 1) {
          String ctxName = n.get(0);
          try {
             ctx = (Context) ctx.lookup(ctxName);
          } catch (NameNotFoundException e) {
             System.out.println("Creating subcontext:" + ctxName);
             ctx = ctx.createSubcontext(ctxName);
          }
          n = n.getSuffix(1);
       }
 
       // The helper class NonSerializableFactory uses address type nns, we go on to
       // use the helper class to bind the service object in JNDI
       StringRefAddr addr = new StringRefAddr("nns", jndiName);
       Reference ref = new Reference(classType.getName(), addr, NonSerializableFactory.class.getName(), null);
       ctx.rebind(n.get(0), ref);
    }
 
    private void unbind(String jndiName, Context ctx) throws Exception {
       NonSerializableFactory.unbind(jndiName);
       ctx.unbind(jndiName);
    }
 
    private SessionFactory buildSessionFactory() {
       // Extra options located in src/test/resources/hibernate.properties
       Configuration cfg = new Configuration();
       cfg.setProperty(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect");
       cfg.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
       cfg.setProperty(Environment.DATASOURCE, "java:/MyDatasource");
       cfg.setProperty(Environment.JNDI_CLASS, "org.jnp.interfaces.NamingContextFactory");
       cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, "org.hibernate.transaction.JBossTransactionManagerLookup");
       cfg.setProperty(Environment.TRANSACTION_STRATEGY, "org.hibernate.transaction.JTATransactionFactory");
       cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "jta");
       cfg.setProperty(Environment.RELEASE_CONNECTIONS, "auto");
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
       cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
       cfg.setProperty(Environment.CACHE_REGION_FACTORY, "org.hibernate.cache.infinispan.InfinispanRegionFactory");
       String[] mappings = new String[]{"org/hibernate/test/cache/infinispan/functional/Item.hbm.xml"};
       for (String mapping : mappings) {
          cfg.addResource(mapping, Thread.currentThread().getContextClassLoader());
       }
       cfg.buildMappings();
       Iterator iter = cfg.getClassMappings();
       while (iter.hasNext()) {
          PersistentClass clazz = (PersistentClass) iter.next();
          cfg.setCacheConcurrencyStrategy(clazz.getEntityName(), "transactional");
       }
       iter = cfg.getCollectionMappings();
       while (iter.hasNext()) {
          Collection coll = (Collection) iter.next();
          cfg.setCollectionCacheConcurrencyStrategy(coll.getRole(), "transactional");
       }
       return cfg.buildSessionFactory( serviceRegistry );
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java
index bc5b867503..0e4e008ad9 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaConnectionProvider.java
@@ -1,104 +1,104 @@
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
 package org.hibernate.test.cache.infinispan.tm;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 import org.hibernate.HibernateException;
+import org.hibernate.service.UnknownUnwrapTypeException;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Stoppable;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 
 /**
  * XaConnectionProvider.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class XaConnectionProvider implements ConnectionProvider {
 	private static ConnectionProvider actualConnectionProvider = ConnectionProviderBuilder.buildConnectionProvider();
 	private boolean isTransactional;
 
 	public static ConnectionProvider getActualConnectionProvider() {
 		return actualConnectionProvider;
 	}
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return XaConnectionProvider.class.isAssignableFrom( unwrapType ) ||
 				ConnectionProvider.class.equals( unwrapType ) ||
 				actualConnectionProvider.getClass().isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( XaConnectionProvider.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else if ( ConnectionProvider.class.isAssignableFrom( unwrapType ) ||
 				actualConnectionProvider.getClass().isAssignableFrom( unwrapType ) ) {
 			return (T) getActualConnectionProvider();
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	public void configure(Properties props) throws HibernateException {
 	}
 
 	public Connection getConnection() throws SQLException {
 		XaTransactionImpl currentTransaction = XaTransactionManagerImpl.getInstance().getCurrentTransaction();
 		if ( currentTransaction == null ) {
 			isTransactional = false;
 			return actualConnectionProvider.getConnection();
 		}
 		else {
 			isTransactional = true;
 			Connection connection = currentTransaction.getEnlistedConnection();
 			if ( connection == null ) {
 				connection = actualConnectionProvider.getConnection();
 				currentTransaction.enlistConnection( connection );
 			}
 			return connection;
 		}
 	}
 
 	public void closeConnection(Connection conn) throws SQLException {
 		if ( !isTransactional ) {
 			conn.close();
 		}
 	}
 
 	public void close() throws HibernateException {
 		if ( actualConnectionProvider instanceof Stoppable ) {
 			((Stoppable) actualConnectionProvider).stop();
 		}
 	}
 
 	public boolean supportsAggressiveRelease() {
 		return true;
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
index 9d8800356e..4739d9e27d 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/CacheTestUtil.java
@@ -1,148 +1,147 @@
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
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
 
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
       cfg.setProperty( JtaPlatformInitiator.JTA_PLATFORM, BatchModeJtaPlatform.class.getName() );
 
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
diff --git a/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java b/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
index de27989fcf..e084a6db29 100644
--- a/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
+++ b/hibernate-proxool/src/main/java/org/hibernate/service/jdbc/connections/internal/ProxoolConnectionProvider.java
@@ -1,240 +1,240 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
  */
 package org.hibernate.service.jdbc.connections.internal;
 
 import java.sql.Connection;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.util.Properties;
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.UnknownUnwrapTypeException;
+import org.hibernate.service.UnknownUnwrapTypeException;
 import org.jboss.logging.Logger;
 import org.logicalcobwebs.proxool.ProxoolException;
 import org.logicalcobwebs.proxool.ProxoolFacade;
 import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
 import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
 
 /**
  * A connection provider that uses a Proxool connection pool. Hibernate will use this by
  * default if the <tt>hibernate.proxool.*</tt> properties are set.
  * @see ConnectionProvider
  */
 public class ProxoolConnectionProvider implements ConnectionProvider {
 
     public static final ProxoolLogger LOG = Logger.getMessageLogger(ProxoolLogger.class, ProxoolConnectionProvider.class.getName());
 
 	private static final String PROXOOL_JDBC_STEM = "proxool.";
 
 	private String proxoolAlias;
 
 	// TRUE if the pool is borrowed from the outside, FALSE if we used to create it
 	private boolean existingPool;
 
 	// Not null if the Isolation level has been specified in the configuration file.
 	// Otherwise, it is left to the Driver's default value.
 	private Integer isolation;
 
 	private boolean autocommit;
 
 	/**
 	 * Grab a connection
 	 * @return a JDBC connection
 	 * @throws SQLException
 	 */
 	public Connection getConnection() throws SQLException {
 	    // get a connection from the pool (thru DriverManager, cfr. Proxool doc)
 		Connection c = DriverManager.getConnection(proxoolAlias);
 
 		// set the Transaction Isolation if defined
 		if (isolation!=null) c.setTransactionIsolation( isolation.intValue() );
 
 		// toggle autoCommit to false if set
 		if ( c.getAutoCommit()!=autocommit ) c.setAutoCommit(autocommit);
 
 		// return the connection
 		return c;
 	}
 
 	@Override
 	public boolean isUnwrappableAs(Class unwrapType) {
 		return ConnectionProvider.class.equals( unwrapType ) ||
 				ProxoolConnectionProvider.class.isAssignableFrom( unwrapType );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> T unwrap(Class<T> unwrapType) {
 		if ( ConnectionProvider.class.equals( unwrapType ) ||
 				ProxoolConnectionProvider.class.isAssignableFrom( unwrapType ) ) {
 			return (T) this;
 		}
 		else {
 			throw new UnknownUnwrapTypeException( unwrapType );
 		}
 	}
 
 	/**
 	 * Dispose of a used connection.
 	 * @param conn a JDBC connection
 	 * @throws SQLException
 	 */
 	public void closeConnection(Connection conn) throws SQLException {
 		conn.close();
 	}
 
 	/**
 	 * Initialize the connection provider from given properties.
 	 * @param props <tt>SessionFactory</tt> properties
 	 */
 	public void configure(Properties props) throws HibernateException {
 
 		// Get the configurator files (if available)
 		String jaxpFile = props.getProperty(Environment.PROXOOL_XML);
 		String propFile = props.getProperty(Environment.PROXOOL_PROPERTIES);
 		String externalConfig = props.getProperty(Environment.PROXOOL_EXISTING_POOL);
 
 		// Default the Proxool alias setting
 		proxoolAlias = props.getProperty(Environment.PROXOOL_POOL_ALIAS);
 
 		// Configured outside of Hibernate (i.e. Servlet container, or Java Bean Container
 		// already has Proxool pools running, and this provider is to just borrow one of these
 		if ( "true".equals(externalConfig) ) {
 
 			// Validate that an alias name was provided to determine which pool to use
 			if ( !StringHelper.isNotEmpty( proxoolAlias ) ) {
                 String msg = LOG.unableToConfigureProxoolProviderToUseExistingInMemoryPool(Environment.PROXOOL_POOL_ALIAS);
                 LOG.error(msg);
 				throw new HibernateException( msg );
 			}
 			// Append the stem to the proxool pool alias
 			proxoolAlias = PROXOOL_JDBC_STEM + proxoolAlias;
 
 			// Set the existing pool flag to true
 			existingPool = true;
 
             LOG.configuringProxoolProviderUsingExistingPool(proxoolAlias);
 
 			// Configured using the JAXP Configurator
 		}
 		else if ( StringHelper.isNotEmpty( jaxpFile ) ) {
 
             LOG.configuringProxoolProviderUsingJaxpConfigurator(jaxpFile);
 
 			// Validate that an alias name was provided to determine which pool to use
 			if ( !StringHelper.isNotEmpty( proxoolAlias ) ) {
                 String msg = LOG.unableToConfigureProxoolProviderToUseJaxp(Environment.PROXOOL_POOL_ALIAS);
                 LOG.error(msg);
 				throw new HibernateException( msg );
 			}
 
 			try {
 				JAXPConfigurator.configure( ConfigHelper.getConfigStreamReader( jaxpFile ), false );
 			}
 			catch ( ProxoolException e ) {
                 String msg = LOG.unableToLoadJaxpConfiguratorFile(jaxpFile);
                 LOG.error(msg, e);
 				throw new HibernateException( msg, e );
 			}
 
 			// Append the stem to the proxool pool alias
 			proxoolAlias = PROXOOL_JDBC_STEM + proxoolAlias;
             LOG.configuringProxoolProviderToUsePoolAlias(proxoolAlias);
 
 			// Configured using the Properties File Configurator
 		}
 		else if ( StringHelper.isNotEmpty( propFile ) ) {
 
             LOG.configuringProxoolProviderUsingPropertiesFile(propFile);
 
 			// Validate that an alias name was provided to determine which pool to use
 			if ( !StringHelper.isNotEmpty( proxoolAlias ) ) {
                 String msg = LOG.unableToConfigureProxoolProviderToUsePropertiesFile(Environment.PROXOOL_POOL_ALIAS);
                 LOG.error(msg);
 				throw new HibernateException( msg );
 			}
 
 			try {
 				PropertyConfigurator.configure( ConfigHelper.getConfigProperties( propFile ) );
 			}
 			catch ( ProxoolException e ) {
                 String msg = LOG.unableToLoadPropertyConfiguratorFile(propFile);
                 LOG.error(msg, e);
 				throw new HibernateException( msg, e );
 			}
 
 			// Append the stem to the proxool pool alias
 			proxoolAlias = PROXOOL_JDBC_STEM + proxoolAlias;
             LOG.configuringProxoolProviderToUsePoolAlias(proxoolAlias);
 		}
 
 		// Remember Isolation level
 		isolation = ConfigurationHelper.getInteger(Environment.ISOLATION, props);
         if (isolation != null) LOG.jdbcIsolationLevel(Environment.isolationLevelToString(isolation.intValue()));
 
 		autocommit = ConfigurationHelper.getBoolean(Environment.AUTOCOMMIT, props);
         LOG.autoCommmitMode(autocommit);
 	}
 
 	/**
 	 * Release all resources held by this provider. JavaDoc requires a second sentence.
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		// If the provider was leeching off an existing pool don't close it
 		if (existingPool) {
 			return;
 		}
 
 		// We have created the pool ourselves, so shut it down
 		try {
 			if ( ProxoolFacade.getAliases().length == 1 ) {
 				ProxoolFacade.shutdown( 0 );
 			}
 			else {
 				ProxoolFacade.removeConnectionPool(proxoolAlias.substring(PROXOOL_JDBC_STEM.length()));
 			}
 		}
 		catch (Exception e) {
 			// If you're closing down the ConnectionProvider chances are an
 			// is not a real big deal, just warn
             String msg = LOG.exceptionClosingProxoolPool();
             LOG.warn(msg, e);
             throw new HibernateException(msg, e);
 		}
 	}
 
 	/**
 	 * @see ConnectionProvider#supportsAggressiveRelease()
 	 */
 	public boolean supportsAggressiveRelease() {
 		return false;
 	}
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
index cb96628a0a..f110dcbfad 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
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
 package org.hibernate.testing;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistry;
 
 import java.util.Map;
 import java.util.Properties;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
 	public static BasicServiceRegistryImpl buildServiceRegistry() {
 		return buildServiceRegistry( Environment.getProperties() );
 	}
 
 	public static BasicServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
 		Properties properties = new Properties();
 		properties.putAll( serviceRegistryConfig );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		return new BasicServiceRegistryImpl( properties );
 	}
 
 	public static void destroy(ServiceRegistry serviceRegistry) {
 		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
