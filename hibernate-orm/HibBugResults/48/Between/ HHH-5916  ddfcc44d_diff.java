diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
index 0195d31f3e..07cb7e0fab 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
@@ -1,276 +1,248 @@
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
 import java.io.InputStream;
 import java.net.URL;
-import java.util.ArrayList;
-import java.util.HashMap;
-import java.util.List;
-import java.util.Map;
 import java.util.Properties;
-import javax.persistence.Embeddable;
-import javax.persistence.Entity;
-import javax.persistence.MappedSuperclass;
-import javax.persistence.MapsId;
 
 import org.dom4j.Document;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
-import org.hibernate.AnnotationException;
-import org.hibernate.DuplicateMappingException;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
-import org.hibernate.annotations.AnyMetaDef;
-import org.hibernate.annotations.common.reflection.ReflectionManager;
-import org.hibernate.annotations.common.reflection.XClass;
-import org.hibernate.engine.NamedQueryDefinition;
-import org.hibernate.engine.NamedSQLQueryDefinition;
-import org.hibernate.engine.ResultSetMappingDefinition;
-import org.hibernate.mapping.IdGenerator;
-import org.hibernate.mapping.Join;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.mapping.Table;
-import org.hibernate.persister.PersisterClassProvider;
-import org.hibernate.util.CollectionHelper;
 
 /**
  * Similar to the {@link Configuration} object but handles EJB3 and Hibernate
  * specific annotations as a metadata facility.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  *
  * @deprecated All functionality has been moved to {@link Configuration}
  */
 @Deprecated
 public class AnnotationConfiguration extends Configuration {
 	private Logger log = LoggerFactory.getLogger( AnnotationConfiguration.class );
 
 	public AnnotationConfiguration() {
 		super();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	public AnnotationConfiguration addAnnotatedClass(Class annotatedClass) throws MappingException {
 		return (AnnotationConfiguration) super.addAnnotatedClass( annotatedClass );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@Override
 	public AnnotationConfiguration addPackage(String packageName) throws MappingException {
 		return (AnnotationConfiguration) super.addPackage( packageName );
 	}
 
 	public ExtendedMappings createExtendedMappings() {
 		return new ExtendedMappingsImpl();
 	}
 
 	@Override
 	public AnnotationConfiguration addFile(String xmlFile) throws MappingException {
 		super.addFile( xmlFile );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addFile(File xmlFile) throws MappingException {
 		super.addFile( xmlFile );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addCacheableFile(File xmlFile) throws MappingException {
 		super.addCacheableFile( xmlFile );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addCacheableFile(String xmlFile) throws MappingException {
 		super.addCacheableFile( xmlFile );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addXML(String xml) throws MappingException {
 		super.addXML( xml );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addURL(URL url) throws MappingException {
 		super.addURL( url );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		super.addResource( resourceName, classLoader );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		super.addDocument( doc );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addResource(String resourceName) throws MappingException {
 		super.addResource( resourceName );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addClass(Class persistentClass) throws MappingException {
 		super.addClass( persistentClass );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addJar(File jar) throws MappingException {
 		super.addJar( jar );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addDirectory(File dir) throws MappingException {
 		super.addDirectory( dir );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setInterceptor(Interceptor interceptor) {
 		super.setInterceptor( interceptor );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setProperties(Properties properties) {
 		super.setProperties( properties );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration addProperties(Properties extraProperties) {
 		super.addProperties( extraProperties );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration mergeProperties(Properties properties) {
 		super.mergeProperties( properties );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setProperty(String propertyName, String value) {
 		super.setProperty( propertyName, value );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration configure() throws HibernateException {
 		super.configure();
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration configure(String resource) throws HibernateException {
 		super.configure( resource );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration configure(URL url) throws HibernateException {
 		super.configure( url );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration configure(File configFile) throws HibernateException {
 		super.configure( configFile );
 		return this;
 	}
 
 	@Override
 	protected AnnotationConfiguration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		super.doConfigure( stream, resourceName );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration configure(org.w3c.dom.Document document) throws HibernateException {
 		super.configure( document );
 		return this;
 	}
 
 	@Override
 	protected AnnotationConfiguration doConfigure(Document doc) throws HibernateException {
 		super.doConfigure( doc );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setCacheConcurrencyStrategy(String clazz, String concurrencyStrategy) {
 		super.setCacheConcurrencyStrategy( clazz, concurrencyStrategy );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setCacheConcurrencyStrategy(String clazz, String concurrencyStrategy, String region) {
 		super.setCacheConcurrencyStrategy( clazz, concurrencyStrategy, region );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy)
 			throws MappingException {
 		super.setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy );
 		return this;
 	}
 
 	@Override
 	public AnnotationConfiguration setNamingStrategy(NamingStrategy namingStrategy) {
 		super.setNamingStrategy( namingStrategy );
 		return this;
 	}
 
-	@Override
-	public AnnotationConfiguration setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
-		super.setPersisterClassProvider( persisterClassProvider );
-		return this;
-	}
-
 	@Deprecated
 	protected class ExtendedMappingsImpl extends MappingsImpl implements ExtendedMappings {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index ba64ce4900..e239c6b870 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,4124 +1,4092 @@
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
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.AnnotationException;
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
-import org.hibernate.engine.jdbc.spi.JdbcServices;
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
 import org.hibernate.internal.util.config.ConfigurationHelper;
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
-import org.hibernate.persister.PersisterClassProvider;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.JACCConfiguration;
 import org.hibernate.service.spi.ServiceRegistry;
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
 import org.hibernate.util.ArrayHelper;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.ConfigHelper;
 import org.hibernate.util.JoinedIterator;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.SerializationHelper;
 import org.hibernate.util.StringHelper;
 import org.hibernate.util.XMLHelper;
 import org.hibernate.util.xml.MappingReader;
 import org.hibernate.util.xml.Origin;
 import org.hibernate.util.xml.OriginImpl;
 import org.hibernate.util.xml.XmlDocument;
 import org.hibernate.util.xml.XmlDocumentImpl;
 
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
 	private static Logger log = LoggerFactory.getLogger( Configuration.class );
 
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
-	private PersisterClassProvider persisterClassProvider;
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
-		persisterClassProvider = null;
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
 	 * Default value is {@link org.hibernate.util.DTDEntityResolver}
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
 		log.info( "Reading mappings from file: " + xmlFile.getPath() );
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
 			log.warn( "Could not deserialize cache file: " + cachedFile.getPath() + " : " + e );
 		}
 		catch ( FileNotFoundException e ) {
 			log.warn( "I/O reported cached file could not be found : " + cachedFile.getPath() + " : " + e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
 		log.info( "Reading mappings from file: " + xmlFile );
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
 			log.debug( "Writing cache file for: " + xmlFile + " to: " + cachedFile );
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
 		}
 		catch ( SerializationException e ) {
 			log.warn( "Could not write cached file: " + cachedFile, e );
 		}
 		catch ( FileNotFoundException e ) {
 			log.warn( "I/O reported error writing cached file : " + cachedFile.getPath(), e );
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
 
 		log.info( "Reading mappings from cache file: " + cachedFile );
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
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Mapping XML:\n" + xml );
 		}
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Reading mapping document from URL : {}", urlExternalForm );
 		}
 
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
 				log.trace( "Was unable to close input stream" );
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
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Mapping document:\n" + doc );
 		}
 
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
 		log.info( "Reading mappings from resource: " + resourceName );
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
 		log.info( "Reading mappings from resource : " + resourceName );
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
 		log.info( "Reading mappings from resource: " + mappingResourceName );
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
 		log.info( "Mapping package {}", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
 			log.error( "Could not parse the package-level metadata [" + packageName + "]" );
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
 		log.info( "Searching for mapping documents in jar: " + jar.getName() );
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
 					log.info( "Found mapping document in jar: " + ze.getName() );
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
 				log.error("could not close jar", ioe);
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
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted()
 
 					);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							defaultCatalog,
 							defaultSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
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
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						table.getSchema(),
 						table.getCatalog(),
 						table.isQuoted()
 					);
 
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
 												defaultCatalog,
 												defaultSchema
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
 									defaultCatalog,
 									defaultSchema
 							)
 					);
 				}
 
 //broken, 'cos we don't generate these with names in SchemaExport
 //				subIter = table.getUniqueKeyIterator();
 //				while ( subIter.hasNext() ) {
 //					UniqueKey uk = (UniqueKey) subIter.next();
 //					if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
 //						script.add( uk.sqlCreateString(dialect, mapping) );
 //					}
 //				}
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
 		log.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
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
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 
 		applyConstraintsToDDL();
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
 		log.debug( "processing fk mappings (*ToOne and JoinedSubclass)" );
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
 				String dependentTable = classMapping.getTable().getQuotedName();
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
 			String dependentTable = sp.getValue().getTable().getQuotedName();
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
 			Iterator<FkSecondPass> it = endOfQueueFkSecondPasses.listIterator();
 			while ( it.hasNext() ) {
 				final FkSecondPass pass = it.next();
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch ( RecoverableException e ) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = ( RuntimeException ) e.getCause();
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
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		UniqueKey uc;
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uc = table.getOrCreateUniqueKey( keyName );
 				uc.addColumn( table.getColumn( column ) );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void applyConstraintsToDDL() {
 		boolean applyOnDdl = getProperties().getProperty(
 				"hibernate.validator.apply_to_ddl",
 				"true"
 		)
 				.equalsIgnoreCase( "true" );
 
 		if ( !applyOnDdl ) {
 			return; // nothing to do in this case
 		}
 		applyHibernateValidatorLegacyConstraintsOnDDL();
 		applyBeanValidationConstraintsOnDDL();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void applyHibernateValidatorLegacyConstraintsOnDDL() {
 		//TODO search for the method only once and cache it?
 		Constructor validatorCtr = null;
 		Method applyMethod = null;
 		try {
 			Class classValidator = ReflectHelper.classForName(
 					"org.hibernate.validator.ClassValidator", this.getClass()
 			);
 			Class messageInterpolator = ReflectHelper.classForName(
 					"org.hibernate.validator.MessageInterpolator", this.getClass()
 			);
 			validatorCtr = classValidator.getDeclaredConstructor(
 					Class.class, ResourceBundle.class, messageInterpolator, Map.class, ReflectionManager.class
 			);
 			applyMethod = classValidator.getMethod( "apply", PersistentClass.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			if ( !isValidatorNotPresentLogged ) {
 				log.info( "Hibernate Validator not found: ignoring" );
 			}
 			isValidatorNotPresentLogged = true;
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new AnnotationException( e );
 		}
 		if ( applyMethod != null ) {
 			for ( PersistentClass persistentClazz : classes.values() ) {
 				//integrate the validate framework
 				String className = persistentClazz.getClassName();
 				if ( StringHelper.isNotEmpty( className ) ) {
 					try {
 						Object validator = validatorCtr.newInstance(
 								ReflectHelper.classForName( className ), null, null, null, reflectionManager
 						);
 						applyMethod.invoke( validator, persistentClazz );
 					}
 					catch ( Exception e ) {
 						log.warn( "Unable to apply constraints on DDL for " + className, e );
 					}
 				}
 			}
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void applyBeanValidationConstraintsOnDDL() {
 		BeanValidationActivator.applyDDL( classes.values(), getProperties() );
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
 		log.debug( "processing extends queue" );
 		processExtendsQueue();
 
 		log.debug( "processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
 		log.debug( "processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
 		log.debug( "processing association property references" );
 
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
 
 		log.debug( "processing foreign key constraints" );
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
 		log.debug( "processing extends queue" );
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
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
 
 	protected void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {
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
 				if ( log.isDebugEnabled() ) {
 					log.debug( "resolving reference to class: " + referencedEntityName );
 				}
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
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		log.debug( "Preparing to build session factory with filters : " + filterDefinitions );
 
 		secondPassCompile();
 		if ( ! metadataSourceQueue.isEmpty() ) {
 			log.warn( "mapping metadata cache was not completely processed" );
 		}
 
 		enableLegacyHibernateValidator();
 		enableBeanValidation();
 		enableHibernateSearch();
 
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
 				getInitializedEventListeners(),
 				sessionFactoryObserver
 			);
 	}
 
 	private static final String LEGACY_VALIDATOR_EVENT_LISTENER = "org.hibernate.validator.event.ValidateEventListener";
 
 	private void enableLegacyHibernateValidator() {
 		//add validator events if the jar is available
 		boolean enableValidatorListeners = !"false".equalsIgnoreCase(
 				getProperty(
 						"hibernate.validator.autoregister_listeners"
 				)
 		);
 		Class validateEventListenerClass = null;
 		try {
 			validateEventListenerClass = ReflectHelper.classForName( LEGACY_VALIDATOR_EVENT_LISTENER, Configuration.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			//validator is not present
 			log.debug( "Legacy Validator not present in classpath, ignoring event listener registration" );
 		}
 		if ( enableValidatorListeners && validateEventListenerClass != null ) {
 			//TODO so much duplication
 			Object validateEventListener;
 			try {
 				validateEventListener = validateEventListenerClass.newInstance();
 			}
 			catch ( Exception e ) {
 				throw new AnnotationException( "Unable to load Validator event listener", e );
 			}
 			{
 				boolean present = false;
 				PreInsertEventListener[] listeners = getEventListeners().getPreInsertEventListeners();
 				if ( listeners != null ) {
 					for ( Object eventListener : listeners ) {
 						//not isAssignableFrom since the user could subclass
 						present = present || validateEventListenerClass == eventListener.getClass();
 					}
 					if ( !present ) {
 						int length = listeners.length + 1;
 						PreInsertEventListener[] newListeners = new PreInsertEventListener[length];
 						System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
 						newListeners[length - 1] = ( PreInsertEventListener ) validateEventListener;
 						getEventListeners().setPreInsertEventListeners( newListeners );
 					}
 				}
 				else {
 					getEventListeners().setPreInsertEventListeners(
 							new PreInsertEventListener[] { ( PreInsertEventListener ) validateEventListener }
 					);
 				}
 			}
 
 			//update event listener
 			{
 				boolean present = false;
 				PreUpdateEventListener[] listeners = getEventListeners().getPreUpdateEventListeners();
 				if ( listeners != null ) {
 					for ( Object eventListener : listeners ) {
 						//not isAssignableFrom since the user could subclass
 						present = present || validateEventListenerClass == eventListener.getClass();
 					}
 					if ( !present ) {
 						int length = listeners.length + 1;
 						PreUpdateEventListener[] newListeners = new PreUpdateEventListener[length];
 						System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
 						newListeners[length - 1] = ( PreUpdateEventListener ) validateEventListener;
 						getEventListeners().setPreUpdateEventListeners( newListeners );
 					}
 				}
 				else {
 					getEventListeners().setPreUpdateEventListeners(
 							new PreUpdateEventListener[] { ( PreUpdateEventListener ) validateEventListener }
 					);
 				}
 			}
 		}
 	}
 
 	private void enableBeanValidation() {
 		BeanValidationActivator.activateBeanValidation( getEventListeners(), getProperties() );
 	}
 
 	private static final String SEARCH_EVENT_LISTENER_REGISTERER_CLASS = "org.hibernate.cfg.search.HibernateSearchEventListenerRegister";
 
 	/**
 	 * Tries to automatically register Hibernate Search event listeners by locating the
 	 * appropriate bootstrap class and calling the <code>enableHibernateSearch</code> method.
 	 */
 	private void enableHibernateSearch() {
 		// load the bootstrap class
 		Class searchStartupClass;
 		try {
 			searchStartupClass = ReflectHelper.classForName( SEARCH_STARTUP_CLASS, getClass() );
 		}
 		catch ( ClassNotFoundException e ) {
 			// TODO remove this together with SearchConfiguration after 3.1.0 release of Search
 			// try loading deprecated HibernateSearchEventListenerRegister
 			try {
 				searchStartupClass = ReflectHelper.classForName( SEARCH_EVENT_LISTENER_REGISTERER_CLASS, getClass() );
 			}
 			catch ( ClassNotFoundException cnfe ) {
 				log.debug( "Search not present in classpath, ignoring event listener registration." );
 				return;
 			}
 		}
 
 		// call the method for registering the listeners
 		try {
 			Object searchStartupInstance = searchStartupClass.newInstance();
 			Method enableSearchMethod = searchStartupClass.getDeclaredMethod(
 					SEARCH_STARTUP_METHOD,
 					EventListeners.class,
 					Properties.class
 			);
 			enableSearchMethod.invoke( searchStartupInstance, getEventListeners(), getProperties() );
 		}
 		catch ( InstantiationException e ) {
 			log.debug( "Unable to instantiate {}, ignoring event listener registration.", SEARCH_STARTUP_CLASS );
 		}
 		catch ( IllegalAccessException e ) {
 			log.debug( "Unable to instantiate {}, ignoring event listener registration.", SEARCH_STARTUP_CLASS );
 		}
 		catch ( NoSuchMethodException e ) {
 			log.debug( "Method enableHibernateSearch() not found in {}.", SEARCH_STARTUP_CLASS );
 		}
 		catch ( InvocationTargetException e ) {
 			log.debug( "Unable to execute {}, ignoring event listener registration.", SEARCH_STARTUP_METHOD );
 		}
 	}
 
 	private EventListeners getInitializedEventListeners() {
 		EventListeners result = (EventListeners) eventListeners.shallowCopy();
 		result.initializeListeners( this );
 		return result;
 	}
 
 	/**
 	 * Rterieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
-	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory() built}
+	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
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
 	 * @return The value curently associated with that property name; may be null.
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
 			log.debug( name + "=" + value );
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
 		log.info( "configuring from resource: " + resource );
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
 		log.info( "Configuration resource: " + resource );
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
 		log.info( "configuring from url: " + url.toString() );
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
 		log.info( "configuring from file: " + configFile.getName() );
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
 			List errors = new ArrayList();
 			Document document = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errors.size() != 0 ) {
 				throw new MappingException( "invalid configuration", (Throwable) errors.get( 0 ) );
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
 				log.warn( "could not close input stream for: " + resourceName, ioe );
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
 		log.info( "configuring from XML document" );
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
 
 		log.info( "Configured SessionFactory: " + name );
 		log.debug( "properties: " + properties );
 
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
 			else if ( "listener".equals( subelementName ) ) {
 				parseListener( subelement );
 			}
 			else if ( "event".equals( subelementName ) ) {
 				parseEvent( subelement );
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
 			log.debug( "session-factory config [{}] named resource [{}] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			log.debug( "session-factory config [{}] named file [{}] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			log.debug( "session-factory config [{}] named jar file [{}] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			log.debug( "session-factory config [{}] named package [{}] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			log.debug( "session-factory config [{}] named class [{}] for mapping", name, className );
 
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
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
       setProperty(Environment.JACC_CONTEXTID, contextId);
 		log.info( "JACC contextID: " + contextId );
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
 
 	private void parseEvent(Element element) {
 		String type = element.attributeValue( "type" );
 		List listeners = element.elements();
 		String[] listenerClasses = new String[ listeners.size() ];
 		for ( int i = 0; i < listeners.size() ; i++ ) {
 			listenerClasses[i] = ( (Element) listeners.get( i ) ).attributeValue( "class" );
 		}
 		log.debug( "Event listeners: " + type + "=" + StringHelper.toString( listenerClasses ) );
 		setListeners( type, listenerClasses );
 	}
 
 	private void parseListener(Element element) {
 		String type = element.attributeValue( "type" );
 		if ( type == null ) {
 			throw new MappingException( "No type specified for listener" );
 		}
 		String impl = element.attributeValue( "class" );
 		log.debug( "Event listener: " + type + "=" + impl );
 		setListeners( type, new String[]{impl} );
 	}
 
 	public void setListener(String type, String listener) {
 		String[] listeners = null;
 		if ( listener != null ) {
 			listeners = (String[]) Array.newInstance( String.class, 1 );
 			listeners[0] = listener;
 		}
 		setListeners( type, listeners );
 	}
 
 	public void setListeners(String type, String[] listenerClasses) {
 		Object[] listeners = null;
 		if ( listenerClasses != null ) {
 			listeners = (Object[]) Array.newInstance( eventListeners.getListenerClassFor(type), listenerClasses.length );
 			for ( int i = 0; i < listeners.length ; i++ ) {
 				try {
 					listeners[i] = ReflectHelper.classForName( listenerClasses[i] ).newInstance();
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"Unable to instantiate specified event (" + type + ") listener class: " + listenerClasses[i],
 							e
 						);
 				}
 			}
 		}
 		setListeners( type, listeners );
 	}
 
 	public void setListener(String type, Object listener) {
 		Object[] listeners = null;
 		if ( listener != null ) {
 			listeners = (Object[]) Array.newInstance( eventListeners.getListenerClassFor(type), 1 );
 			listeners[0] = listener;
 		}
 		setListeners( type, listeners );
 	}
 
 	public void setListeners(String type, Object[] listeners) {
 		if ( "auto-flush".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setAutoFlushEventListeners( new AutoFlushEventListener[]{} );
 			}
 			else {
 				eventListeners.setAutoFlushEventListeners( (AutoFlushEventListener[]) listeners );
 			}
 		}
 		else if ( "merge".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setMergeEventListeners( new MergeEventListener[]{} );
 			}
 			else {
 				eventListeners.setMergeEventListeners( (MergeEventListener[]) listeners );
 			}
 		}
 		else if ( "create".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPersistEventListeners( new PersistEventListener[]{} );
 			}
 			else {
 				eventListeners.setPersistEventListeners( (PersistEventListener[]) listeners );
 			}
 		}
 		else if ( "create-onflush".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPersistOnFlushEventListeners( new PersistEventListener[]{} );
 			}
 			else {
 				eventListeners.setPersistOnFlushEventListeners( (PersistEventListener[]) listeners );
 			}
 		}
 		else if ( "delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setDeleteEventListeners( new DeleteEventListener[]{} );
 			}
 			else {
 				eventListeners.setDeleteEventListeners( (DeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "dirty-check".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setDirtyCheckEventListeners( new DirtyCheckEventListener[]{} );
 			}
 			else {
 				eventListeners.setDirtyCheckEventListeners( (DirtyCheckEventListener[]) listeners );
 			}
 		}
 		else if ( "evict".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setEvictEventListeners( new EvictEventListener[]{} );
 			}
 			else {
 				eventListeners.setEvictEventListeners( (EvictEventListener[]) listeners );
 			}
 		}
 		else if ( "flush".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setFlushEventListeners( new FlushEventListener[]{} );
 			}
 			else {
 				eventListeners.setFlushEventListeners( (FlushEventListener[]) listeners );
 			}
 		}
 		else if ( "flush-entity".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setFlushEntityEventListeners( new FlushEntityEventListener[]{} );
 			}
 			else {
 				eventListeners.setFlushEntityEventListeners( (FlushEntityEventListener[]) listeners );
 			}
 		}
 		else if ( "load".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setLoadEventListeners( new LoadEventListener[]{} );
 			}
 			else {
 				eventListeners.setLoadEventListeners( (LoadEventListener[]) listeners );
 			}
 		}
 		else if ( "load-collection".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setInitializeCollectionEventListeners(
 						new InitializeCollectionEventListener[]{}
 					);
 			}
 			else {
 				eventListeners.setInitializeCollectionEventListeners(
 						(InitializeCollectionEventListener[]) listeners
 					);
 			}
 		}
 		else if ( "lock".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setLockEventListeners( new LockEventListener[]{} );
 			}
 			else {
 				eventListeners.setLockEventListeners( (LockEventListener[]) listeners );
 			}
 		}
 		else if ( "refresh".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setRefreshEventListeners( new RefreshEventListener[]{} );
 			}
 			else {
 				eventListeners.setRefreshEventListeners( (RefreshEventListener[]) listeners );
 			}
 		}
 		else if ( "replicate".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setReplicateEventListeners( new ReplicateEventListener[]{} );
 			}
 			else {
 				eventListeners.setReplicateEventListeners( (ReplicateEventListener[]) listeners );
 			}
 		}
 		else if ( "save-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setSaveOrUpdateEventListeners( new SaveOrUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setSaveOrUpdateEventListeners( (SaveOrUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "save".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setSaveEventListeners( new SaveOrUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setSaveEventListeners( (SaveOrUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setUpdateEventListeners( new SaveOrUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setUpdateEventListeners( (SaveOrUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-load".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreLoadEventListeners( new PreLoadEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreLoadEventListeners( (PreLoadEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreUpdateEventListeners( new PreUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreUpdateEventListeners( (PreUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreDeleteEventListeners( new PreDeleteEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreDeleteEventListeners( (PreDeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-insert".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreInsertEventListeners( new PreInsertEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreInsertEventListeners( (PreInsertEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-collection-recreate".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreCollectionRecreateEventListeners( new PreCollectionRecreateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreCollectionRecreateEventListeners( (PreCollectionRecreateEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-collection-remove".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreCollectionRemoveEventListeners( new PreCollectionRemoveEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreCollectionRemoveEventListeners( ( PreCollectionRemoveEventListener[]) listeners );
 			}
 		}
 		else if ( "pre-collection-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPreCollectionUpdateEventListeners( new PreCollectionUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPreCollectionUpdateEventListeners( ( PreCollectionUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-load".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostLoadEventListeners( new PostLoadEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostLoadEventListeners( (PostLoadEventListener[]) listeners );
 			}
 		}
 		else if ( "post-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostUpdateEventListeners( new PostUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostUpdateEventListeners( (PostUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostDeleteEventListeners( new PostDeleteEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostDeleteEventListeners( (PostDeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "post-insert".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostInsertEventListeners( new PostInsertEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostInsertEventListeners( (PostInsertEventListener[]) listeners );
 			}
 		}
 		else if ( "post-commit-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCommitUpdateEventListeners(
 						new PostUpdateEventListener[]{}
 					);
 			}
 			else {
 				eventListeners.setPostCommitUpdateEventListeners( (PostUpdateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-commit-delete".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCommitDeleteEventListeners(
 						new PostDeleteEventListener[]{}
 					);
 			}
 			else {
 				eventListeners.setPostCommitDeleteEventListeners( (PostDeleteEventListener[]) listeners );
 			}
 		}
 		else if ( "post-commit-insert".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCommitInsertEventListeners(
 						new PostInsertEventListener[]{}
 				);
 			}
 			else {
 				eventListeners.setPostCommitInsertEventListeners( (PostInsertEventListener[]) listeners );
 			}
 		}
 		else if ( "post-collection-recreate".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCollectionRecreateEventListeners( new PostCollectionRecreateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostCollectionRecreateEventListeners( (PostCollectionRecreateEventListener[]) listeners );
 			}
 		}
 		else if ( "post-collection-remove".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCollectionRemoveEventListeners( new PostCollectionRemoveEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostCollectionRemoveEventListeners( ( PostCollectionRemoveEventListener[]) listeners );
 			}
 		}
 		else if ( "post-collection-update".equals( type ) ) {
 			if ( listeners == null ) {
 				eventListeners.setPostCollectionUpdateEventListeners( new PostCollectionUpdateEventListener[]{} );
 			}
 			else {
 				eventListeners.setPostCollectionUpdateEventListeners( ( PostCollectionUpdateEventListener[]) listeners );
 			}
 		}
 		else {
 			throw new MappingException("Unrecognized listener type [" + type + "]");
 		}
 	}
 
 	public EventListeners getEventListeners() {
 		return eventListeners;
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
 	 *
 	 * @return this for method chaining
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
 
-	public PersisterClassProvider getPersisterClassProvider() {
-		return persisterClassProvider;
-	}
-
-	/**
-	 * Defines a custom persister class provider.
-	 *
-	 * The persister class is chosen according to the following rules in decreasing priority:
-	 *  - the persister class defined explicitly via annotation or XML
-	 *  - the persister class returned by the PersisterClassProvider implementation (if not null)
-	 *  - the default provider as chosen by Hibernate Core (best choice most of the time)
-	 *
-	 *
-	 * @param persisterClassProvider implementation
-	 */
-	public Configuration setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
-		this.persisterClassProvider = persisterClassProvider;
-		return this;
-	}
-
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
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
 		sqlFunctions.put( functionName, function );
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
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
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
 
-		public PersisterClassProvider getPersisterClassProvider() {
-			return persisterClassProvider;
-		}
-
-		public void setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
-			Configuration.this.persisterClassProvider = persisterClassProvider;
-		}
-
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
 				if ( existing.equals( entityName ) ) {
 					log.info( "duplicate import: {} -> {}", entityName, rename );
 				}
 				else {
 					throw new DuplicateMappingException(
 							"duplicate import: " + rename + " refers to both " + entityName +
 									" and " + existing + " (try using auto-import=\"false\")",
 							"import",
 							rename
 					);
 				}
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
 			log.debug( "Added " + typeName + " with class " + typeClass );
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
 			return getLogicalTableName( table.getQuotedSchema(), table.getCatalog(), table.getQuotedName() );
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
 			StringBuffer keyBuilder = new StringBuffer();
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
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
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
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
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
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
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
 
 
 		private Boolean useNewGeneratorMappings;
 
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
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties().getProperty( USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
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
 					log.warn( "duplicate generator name {}", old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				log.warn( "duplicate generator table: {}", name );
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
 				log.warn( "duplicate joins for class: {}", persistentClass.getEntityName() );
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
 			log.debug( "Processing hbm.xml files" );
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), CollectionHelper.EMPTY_MAP, entityNames );
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
 			log.debug( "Process annotated classes" );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 473fc0b47b..e93dd0e60c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1,2442 +1,2412 @@
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
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.Element;
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.Versioning;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Array;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Fetchable;
 import org.hibernate.mapping.Filterable;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierBag;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexBackref;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.Map;
 import org.hibernate.mapping.MetaAttribute;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.PrimitiveArray;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Set;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UnionSubclass;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.mapping.Value;
 import org.hibernate.mapping.FetchProfile;
-import org.hibernate.persister.PersisterClassProvider;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
 import org.hibernate.persister.entity.SingleTableEntityPersister;
 import org.hibernate.persister.entity.UnionSubclassEntityPersister;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 import org.hibernate.util.JoinedIterator;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 import org.hibernate.util.xml.XmlDocument;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Walks an XML mapping document and produces the Hibernate configuration-time metamodel (the
  * classes in the <tt>mapping</tt> package)
  *
  * @author Gavin King
  */
 public final class HbmBinder {
 
 	private static final Logger log = LoggerFactory.getLogger( HbmBinder.class );
 
 	/**
 	 * Private constructor to disallow instantiation.
 	 */
 	private HbmBinder() {
 	}
 
 	/**
 	 * The main contract into the hbm.xml-based binder. Performs necessary binding operations
 	 * represented by the given DOM.
 	 *
 	 * @param metadataXml The DOM to be parsed and bound.
 	 * @param mappings Current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @param entityNames Any state
 	 *
 	 * @throws MappingException
 	 */
 	public static void bindRoot(
 			XmlDocument metadataXml,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			java.util.Set<String> entityNames) throws MappingException {
 
 		final Document doc = metadataXml.getDocumentTree();
 		final Element hibernateMappingElement = doc.getRootElement();
 
 		java.util.List<String> names = HbmBinder.getExtendsNeeded( metadataXml, mappings );
 		if ( !names.isEmpty() ) {
 			// classes mentioned in extends not available - so put it in queue
 			Attribute packageAttribute = hibernateMappingElement.attribute( "package" );
 			String packageName = packageAttribute == null ? null : packageAttribute.getValue();
 			for ( String name : names ) {
 				mappings.addToExtendsQueue( new ExtendsQueueEntry( name, packageName, metadataXml, entityNames ) );
 			}
 			return;
 		}
 
 		// get meta's from <hibernate-mapping>
 		inheritedMetas = getMetas( hibernateMappingElement, inheritedMetas, true );
 		extractRootAttributes( hibernateMappingElement, mappings );
 
 		Iterator rootChildren = hibernateMappingElement.elementIterator();
 		while ( rootChildren.hasNext() ) {
 			final Element element = (Element) rootChildren.next();
 			final String elementName = element.getName();
 
 			if ( "filter-def".equals( elementName ) ) {
 				parseFilterDef( element, mappings );
 			}
 			else if ( "fetch-profile".equals( elementName ) ) {
 				parseFetchProfile( element, mappings, null );
 			}
 			else if ( "identifier-generator".equals( elementName ) ) {
 				parseIdentifierGeneratorRegistration( element, mappings );
 			}
 			else if ( "typedef".equals( elementName ) ) {
 				bindTypeDef( element, mappings );
 			}
 			else if ( "class".equals( elementName ) ) {
 				RootClass rootclass = new RootClass();
 				bindRootClass( element, rootclass, mappings, inheritedMetas );
 				mappings.addClass( rootclass );
 			}
 			else if ( "subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "query".equals( elementName ) ) {
 				bindNamedQuery( element, null, mappings );
 			}
 			else if ( "sql-query".equals( elementName ) ) {
 				bindNamedSQLQuery( element, null, mappings );
 			}
 			else if ( "resultset".equals( elementName ) ) {
 				bindResultSetMappingDefinition( element, null, mappings );
 			}
 			else if ( "import".equals( elementName ) ) {
 				bindImport( element, mappings );
 			}
 			else if ( "database-object".equals( elementName ) ) {
 				bindAuxiliaryDatabaseObject( element, mappings );
 			}
 		}
 	}
 
 	private static void parseIdentifierGeneratorRegistration(Element element, Mappings mappings) {
 		String strategy = element.attributeValue( "name" );
 		if ( StringHelper.isEmpty( strategy ) ) {
 			throw new MappingException( "'name' attribute expected for identifier-generator elements" );
 		}
 		String generatorClassName = element.attributeValue( "class" );
 		if ( StringHelper.isEmpty( generatorClassName ) ) {
 			throw new MappingException( "'class' attribute expected for identifier-generator [identifier-generator@name=" + strategy + "]" );
 		}
 
 		try {
 			Class generatorClass = ReflectHelper.classForName( generatorClassName );
 			mappings.getIdentifierGeneratorFactory().register( strategy, generatorClass );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new MappingException( "Unable to locate identifier-generator class [name=" + strategy + ", class=" + generatorClassName + "]" );
 		}
 
 	}
 
 	private static void bindImport(Element importNode, Mappings mappings) {
 		String className = getClassName( importNode.attribute( "class" ), mappings );
 		Attribute renameNode = importNode.attribute( "rename" );
 		String rename = ( renameNode == null ) ?
 						StringHelper.unqualify( className ) :
 						renameNode.getValue();
 		log.debug( "Import: " + rename + " -> " + className );
 		mappings.addImport( className, rename );
 	}
 
 	private static void bindTypeDef(Element typedefNode, Mappings mappings) {
 		String typeClass = typedefNode.attributeValue( "class" );
 		String typeName = typedefNode.attributeValue( "name" );
 		Iterator paramIter = typedefNode.elementIterator( "param" );
 		Properties parameters = new Properties();
 		while ( paramIter.hasNext() ) {
 			Element param = (Element) paramIter.next();
 			parameters.setProperty( param.attributeValue( "name" ), param.getTextTrim() );
 		}
 		mappings.addTypeDef( typeName, typeClass, parameters );
 	}
 
 	private static void bindAuxiliaryDatabaseObject(Element auxDbObjectNode, Mappings mappings) {
 		AuxiliaryDatabaseObject auxDbObject = null;
 		Element definitionNode = auxDbObjectNode.element( "definition" );
 		if ( definitionNode != null ) {
 			try {
 				auxDbObject = ( AuxiliaryDatabaseObject ) ReflectHelper
 						.classForName( definitionNode.attributeValue( "class" ) )
 						.newInstance();
 			}
 			catch( ClassNotFoundException e ) {
 				throw new MappingException(
 						"could not locate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 			catch( Throwable t ) {
 				throw new MappingException(
 						"could not instantiate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 		}
 		else {
 			auxDbObject = new SimpleAuxiliaryDatabaseObject(
 					auxDbObjectNode.elementTextTrim( "create" ),
 					auxDbObjectNode.elementTextTrim( "drop" )
 				);
 		}
 
 		Iterator dialectScopings = auxDbObjectNode.elementIterator( "dialect-scope" );
 		while ( dialectScopings.hasNext() ) {
 			Element dialectScoping = ( Element ) dialectScopings.next();
 			auxDbObject.addDialectScope( dialectScoping.attributeValue( "name" ) );
 		}
 
 		mappings.addAuxiliaryDatabaseObject( auxDbObject );
 	}
 
 	private static void extractRootAttributes(Element hmNode, Mappings mappings) {
 		Attribute schemaNode = hmNode.attribute( "schema" );
 		mappings.setSchemaName( ( schemaNode == null ) ? null : schemaNode.getValue() );
 
 		Attribute catalogNode = hmNode.attribute( "catalog" );
 		mappings.setCatalogName( ( catalogNode == null ) ? null : catalogNode.getValue() );
 
 		Attribute dcNode = hmNode.attribute( "default-cascade" );
 		mappings.setDefaultCascade( ( dcNode == null ) ? "none" : dcNode.getValue() );
 
 		Attribute daNode = hmNode.attribute( "default-access" );
 		mappings.setDefaultAccess( ( daNode == null ) ? "property" : daNode.getValue() );
 
 		Attribute dlNode = hmNode.attribute( "default-lazy" );
 		mappings.setDefaultLazy( dlNode == null || dlNode.getValue().equals( "true" ) );
 
 		Attribute aiNode = hmNode.attribute( "auto-import" );
 		mappings.setAutoImport( ( aiNode == null ) || "true".equals( aiNode.getValue() ) );
 
 		Attribute packNode = hmNode.attribute( "package" );
 		if ( packNode != null ) mappings.setDefaultPackage( packNode.getValue() );
 	}
 
 	/**
 	 * Responsible for perfoming the bind operation related to an &lt;class/&gt; mapping element.
 	 *
 	 * @param node The DOM Element for the &lt;class/&gt; element.
 	 * @param rootClass The mapping instance to which to bind the information.
 	 * @param mappings The current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @throws MappingException
 	 */
 	public static void bindRootClass(Element node, RootClass rootClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		bindClass( node, rootClass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <class>
 		bindRootPersistentClassCommonValues( node, inheritedMetas, mappings, rootClass );
 	}
 
 	private static void bindRootPersistentClassCommonValues(Element node,
 			java.util.Map inheritedMetas, Mappings mappings, RootClass entity)
 			throws MappingException {
 
 		// DB-OBJECTNAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( entity, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 		        entity.isAbstract() != null && entity.isAbstract().booleanValue()
 			);
 		entity.setTable( table );
 		bindComment(table, node);
 
 		log.info(
 				"Mapping class: " + entity.getEntityName() +
 				" -> " + entity.getTable().getName()
 			);
 
 		// MUTABLE
 		Attribute mutableNode = node.attribute( "mutable" );
 		entity.setMutable( ( mutableNode == null ) || mutableNode.getValue().equals( "true" ) );
 
 		// WHERE
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) entity.setWhere( whereNode.getValue() );
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) table.addCheckConstraint( chNode.getValue() );
 
 		// POLYMORPHISM
 		Attribute polyNode = node.attribute( "polymorphism" );
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
-		//persister node in XML has priority over
-		//persisterClassProvider
-		//if all fail, the default Hibernate persisters kick in
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				entity.setEntityPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
-		else {
-			final PersisterClassProvider persisterClassProvider = mappings.getPersisterClassProvider();
-			if ( persisterClassProvider != null ) {
-				final Class<? extends EntityPersister> persister = persisterClassProvider.getEntityPersisterClass(
-						entity.getEntityName()
-				);
-				if ( persister != null ) {
-					entity.setEntityPersisterClass( persister );
-				}
-			}
-		}
 
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
 
 		log.info(
 				"Mapping union-subclass: " + unionSubclass.getEntityName() +
 				" -> " + unionSubclass.getTable().getName()
 			);
 
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
 
 		log.info(
 				"Mapping subclass: " + subclass.getEntityName() +
 				" -> " + subclass.getTable().getName()
 			);
 
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
 
 		log.info(
 				"Mapping joined-subclass: " + joinedSubclass.getEntityName() +
 				" -> " + joinedSubclass.getTable().getName()
 			);
 
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
 
 		log.info(
 				"Mapping class join: " + persistentClass.getEntityName() +
 				" -> " + join.getTable().getName()
 			);
 
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
 
 		if ( log.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
 			log.debug( msg );
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
 			if ( Environment.jvmSupportsLinkedHashCollections() || ( collection instanceof Bag ) ) {
 				collection.setOrderBy( orderNode.getValue() );
 			}
 			else {
 				log.warn( "Attribute \"order-by\" ignored in JDK1.3 or less" );
 			}
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
-		//persister node in XML has priority over
-		//persisterClassProvider
-		//if all fail, the default Hibernate persisters kick in
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
-		else {
-			final PersisterClassProvider persisterClassProvider = mappings.getPersisterClassProvider();
-			if ( persisterClassProvider != null ) {
-				final Class<? extends CollectionPersister> persister =
-						persisterClassProvider.getCollectionPersisterClass( collection.getRole() );
-				if ( persister != null ) {
-					collection.setCollectionPersisterClass( persister );
-				}
-			}
-		}
 
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
 
 			log.info(
 					"Mapping collection: " + collection.getRole() +
 					" -> " + collection.getCollectionTable().getName()
 				);
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
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
index 7886b3f777..3e3bb128dd 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
@@ -1,766 +1,755 @@
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
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 import java.util.Map;
 import java.util.ListIterator;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.FetchProfile;
-import org.hibernate.persister.PersisterClassProvider;
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
-	 * Get the current persister class provider implementation
-	 */
-	public PersisterClassProvider getPersisterClassProvider();
-
-	/**
-	 * Set the current persister class provider implementation
-	 */
-	public void setPersisterClassProvider(PersisterClassProvider persisterClassProvider);
-
-	/**
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
 	public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory();
 
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
 	@SuppressWarnings({ "JavaDoc" })
 	public Map<Table, List<String[]>> getTableUniqueConstraints();
 
 	public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable();
 
 	/**
 	 * @deprecated Use {@link #addUniqueConstraintHolders} instead
 	 */
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
 	 * {@link Configuration#USE_NEW_ID_GENERATOR_MAPPINGS} setting.
 	 *
 	 * @return True if the new generators should be used, false otherwise.
 	 */
 	public boolean useNewGeneratorMappings();
 
 	/**
 	 * Return the property annotated with @ToOne and @Id if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData property);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 2922fc1203..e5cd290d93 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -1,1420 +1,1407 @@
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
 
 import java.util.ArrayList;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.OneToMany;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.CollectionOfElements;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.FilterJoinTables;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.LazyCollection;
 import org.hibernate.annotations.LazyCollectionOption;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.SortType;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyInferredData;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Table;
-import org.hibernate.persister.PersisterClassProvider;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.util.StringHelper;
 
 /**
  * Base class for binding different types of collections to Hibernate configuration objects.
  *
  * @author inger
  * @author Emmanuel Bernard
  */
 @SuppressWarnings({"unchecked", "serial"})
 public abstract class CollectionBinder {
 	private Logger log = LoggerFactory.getLogger( CollectionBinder.class );
 
 	protected Collection collection;
 	protected String propertyName;
 	PropertyHolder propertyHolder;
 	int batchSize;
 	private String mappedBy;
 	private XClass collectionType;
 	private XClass targetEntity;
 	private Mappings mappings;
 	private Ejb3JoinColumn[] inverseJoinColumns;
 	private String cascadeStrategy;
 	String cacheConcurrencyStrategy;
 	String cacheRegionName;
 	private boolean oneToMany;
 	protected IndexColumn indexColumn;
 	private String orderBy;
 	protected String hqlOrderBy;
 	private boolean isSorted;
 	private Class comparator;
 	private boolean hasToBeSorted;
 	protected boolean cascadeDeleteEnabled;
 	protected String mapKeyPropertyName;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private Ejb3JoinColumn[] fkJoinColumns;
 	private boolean isExplicitAssociationTable;
 	private Ejb3Column[] elementColumns;
 	private boolean isEmbedded;
 	private XProperty property;
 	private boolean ignoreNotFound;
 	private TableBinder tableBinder;
 	private Ejb3Column[] mapKeyColumns;
 	private Ejb3JoinColumn[] mapKeyManyToManyColumns;
 	protected HashMap<String, IdGenerator> localGenerators;
 	protected Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private AccessType accessType;
 	private boolean hibernateExtensionMapping;
 
 	protected Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public void setIsHibernateExtensionMapping(boolean hibernateExtensionMapping) {
 		this.hibernateExtensionMapping = hibernateExtensionMapping;
 	}
 
 	protected boolean isHibernateExtensionMapping() {
 		return hibernateExtensionMapping;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setCascadeStrategy(String cascadeStrategy) {
 		this.cascadeStrategy = cascadeStrategy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
 		this.inverseJoinColumns = inverseJoinColumns;
 	}
 
 	public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
 		this.joinColumns = joinColumns;
 	}
 
 	private Ejb3JoinColumn[] joinColumns;
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	public void setBatchSize(BatchSize batchSize) {
 		this.batchSize = batchSize == null ? -1 : batchSize.size();
 	}
 
 	public void setEjb3OrderBy(javax.persistence.OrderBy orderByAnn) {
 		if ( orderByAnn != null ) {
 			hqlOrderBy = orderByAnn.value();
 		}
 	}
 
 	public void setSqlOrderBy(OrderBy orderByAnn) {
 		if ( orderByAnn != null ) {
 			if ( !BinderHelper.isEmptyAnnotationValue( orderByAnn.clause() ) ) {
 				orderBy = orderByAnn.clause();
 			}
 		}
 	}
 
 	public void setSort(Sort sortAnn) {
 		if ( sortAnn != null ) {
 			isSorted = !SortType.UNSORTED.equals( sortAnn.type() );
 			if ( isSorted && SortType.COMPARATOR.equals( sortAnn.type() ) ) {
 				comparator = sortAnn.comparator();
 			}
 		}
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping) {
 		CollectionBinder result;
 		if ( property.isArray() ) {
 			if ( property.getElementClass().isPrimitive() ) {
 				result = new PrimitiveArrayBinder();
 			}
 			else {
 				result = new ArrayBinder();
 			}
 		}
 		else if ( property.isCollection() ) {
 			//TODO consider using an XClass
 			Class returnedClass = property.getCollectionClass();
 			if ( java.util.Set.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder();
 			}
 			else if ( java.util.SortedSet.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( true );
 			}
 			else if ( java.util.Map.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder();
 			}
 			else if ( java.util.SortedMap.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( true );
 			}
 			else if ( java.util.Collection.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else if ( java.util.List.class.equals( returnedClass ) ) {
 				if ( isIndexed ) {
 					if ( property.isAnnotationPresent( CollectionId.class ) ) {
 						throw new AnnotationException(
 								"List do not support @CollectionId and @OrderColumn (or @IndexColumn) at the same time: "
 								+ StringHelper.qualify( entityName, property.getName() ) );
 					}
 					result = new ListBinder();
 				}
 				else if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else {
 				throw new AnnotationException(
 						returnedClass.getName() + " collection not yet supported: "
 								+ StringHelper.qualify( entityName, property.getName() )
 				);
 			}
 		}
 		else {
 			throw new AnnotationException(
 					"Illegal attempt to map a non collection as a @OneToMany, @ManyToMany or @CollectionOfElements: "
 							+ StringHelper.qualify( entityName, property.getName() )
 			);
 		}
 		result.setIsHibernateExtensionMapping( isHibernateExtensionMapping );
 		return result;
 	}
 
 	protected CollectionBinder() {
 	}
 
 	protected CollectionBinder(boolean sorted) {
 		this.hasToBeSorted = sorted;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	public void setTableBinder(TableBinder tableBinder) {
 		this.tableBinder = tableBinder;
 	}
 
 	public void setCollectionType(XClass collectionType) {
 		this.collectionType = collectionType;
 	}
 
 	public void setTargetEntity(XClass targetEntity) {
 		this.targetEntity = targetEntity;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	protected abstract Collection createCollection(PersistentClass persistentClass);
 
 	public Collection getCollection() {
 		return collection;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	public void bind() {
 		this.collection = createCollection( propertyHolder.getPersistentClass() );
 		log.debug( "Collection role: {}", StringHelper.qualify( propertyHolder.getPath(), propertyName ) );
 		collection.setRole( StringHelper.qualify( propertyHolder.getPath(), propertyName ) );
 		collection.setNodeName( propertyName );
 
 		if ( (property.isAnnotationPresent( org.hibernate.annotations.MapKey.class )
 				|| property.isAnnotationPresent( MapKeyColumn.class ) )
 			&& mapKeyPropertyName != null ) {
 			throw new AnnotationException(
 					"Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey "
 							+ "on the same collection: " + StringHelper.qualify(
 							propertyHolder.getPath(), propertyName
 					)
 			);
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 		if ( orderBy != null && hqlOrderBy != null ) {
 			throw new AnnotationException(
 					"Cannot use sql order by clause in conjunction of EJB3 order by clause: " + safeCollectionRole()
 			);
 		}
 
 		collection.setMutable( !property.isAnnotationPresent( Immutable.class ) );
 		OptimisticLock lockAnn = property.getAnnotation( OptimisticLock.class );
 		if ( lockAnn != null ) collection.setOptimisticLocked( !lockAnn.excluded() );
 
-		//@Persister has priority over PersisterClassProvider
-		//if all fail, left null and Hibernate defaults kick in
 		Persister persisterAnn = property.getAnnotation( Persister.class );
 		if ( persisterAnn != null ) {
 			collection.setCollectionPersisterClass( persisterAnn.impl() );
 		}
-		else {
-			final PersisterClassProvider persisterClassProvider = mappings.getPersisterClassProvider();
-			if (persisterClassProvider != null) {
-				final Class<? extends CollectionPersister> persister =
-						persisterClassProvider.getCollectionPersisterClass( collection.getRole() );
-				if (persister != null) {
-					collection.setCollectionPersisterClass( persister );
-				}
-			}
-		}
 
 		// set ordering
 		if ( orderBy != null ) collection.setOrderBy( orderBy );
 		if ( isSorted ) {
 			collection.setSorted( true );
 			if ( comparator != null ) {
 				try {
 					collection.setComparator( (Comparator) comparator.newInstance() );
 				}
 				catch (ClassCastException e) {
 					throw new AnnotationException(
 							"Comparator not implementing java.util.Comparator class: "
 									+ comparator.getName() + "(" + safeCollectionRole() + ")"
 					);
 				}
 				catch (Exception e) {
 					throw new AnnotationException(
 							"Could not instantiate comparator class: "
 									+ comparator.getName() + "(" + safeCollectionRole() + ")"
 					);
 				}
 			}
 		}
 		else {
 			if ( hasToBeSorted ) {
 				throw new AnnotationException(
 						"A sorted collection has to define @Sort: "
 								+ safeCollectionRole()
 				);
 			}
 		}
 
 		//set cache
 		if ( StringHelper.isNotEmpty( cacheConcurrencyStrategy ) ) {
 			collection.setCacheConcurrencyStrategy( cacheConcurrencyStrategy );
 			collection.setCacheRegionName( cacheRegionName );
 		}
 
 		//SQL overriding
 		SQLInsert sqlInsert = property.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = property.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = property.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = property.getAnnotation( SQLDeleteAll.class );
 		Loader loader = property.getAnnotation( Loader.class );
 		if ( sqlInsert != null ) {
 			collection.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
 		//work on association
 		boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue( mappedBy );
 
 		if (isMappedBy
 				&& (property.isAnnotationPresent( JoinColumn.class )
 					|| property.isAnnotationPresent( JoinColumns.class )
 					|| propertyHolder.getJoinTable( property ) != null ) ) {
 			String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
 			message += StringHelper.qualify( propertyHolder.getPath(), propertyName );
 			throw new AnnotationException( message );
 		}
 
 		collection.setInverse( isMappedBy );
 
 		//many to many may need some second pass informations
 		if ( !oneToMany && isMappedBy ) {
 			mappings.addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns, mapKeyManyToManyColumns, isEmbedded,
 				property, collectionType,
 				ignoreNotFound, oneToMany,
 				tableBinder, mappings
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( CollectionOfElements.class ) //legacy hibernate
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 
 		mappings.addCollection( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 		binder.setAccessType( accessType );
 		binder.setProperty( property );
 		binder.setInsertable( insertable );
 		binder.setUpdatable( updatable );
 		Property prop = binder.makeProperty();
 		//we don't care about the join stuffs because the column is on the association table.
 		if (! declaringClassSet) throw new AssertionFailure( "DeclaringClass is not set in CollectionBinder while binding" );
 		propertyHolder.addProperty( prop, declaringClass );
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
 		CollectionOfElements collectionOfElements = property.getAnnotation( CollectionOfElements.class ); //legacy hibernate
 		ElementCollection elementCollection = property.getAnnotation( ElementCollection.class ); //jpa 2
 		ManyToAny manyToAny = property.getAnnotation( ManyToAny.class );
 		FetchType fetchType;
 		if ( oneToMany != null ) {
 			fetchType = oneToMany.fetch();
 		}
 		else if ( manyToMany != null ) {
 			fetchType = manyToMany.fetch();
 		}
 		else if ( elementCollection != null ) {
 			fetchType = elementCollection.fetch();
 		}
 		else if ( collectionOfElements != null ) {
 			fetchType = collectionOfElements.fetch();
 		}
 		else if ( manyToAny != null ) {
 			fetchType = FetchType.LAZY;
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements"
 			);
 		}
 		if ( lazy != null ) {
 			collection.setLazy( !( lazy.value() == LazyCollectionOption.FALSE ) );
 			collection.setExtraLazy( lazy.value() == LazyCollectionOption.EXTRA );
 		}
 		else {
 			collection.setLazy( fetchType == FetchType.LAZY );
 			collection.setExtraLazy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				collection.setFetchMode( FetchMode.JOIN );
 				collection.setLazy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 				collection.setSubselectLoadable( true );
 				collection.getOwner().setSubselectLoadableCollections( true );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			collection.setFetchMode( AnnotationBinder.getFetchMode( fetchType ) );
 		}
 	}
 
 	private XClass getCollectionType() {
 		if ( AnnotationBinder.isDefault( targetEntity, mappings ) ) {
 			if ( collectionType != null ) {
 				return collectionType;
 			}
 			else {
 				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
 						+ safeCollectionRole();
 				throw new AnnotationException( errorMsg );
 			}
 		}
 		else {
 			return targetEntity;
 		}
 	}
 
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final Mappings mappings) {
 		return new CollectionSecondPass( mappings, collection ) {
 			public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, mappings
 				);
 			}
 		};
 	}
 
 	/**
 	 * return true if it's a Fk, false if it's an association table
 	 */
 	protected boolean bindStarToManySecondPass(
 			Map persistentClasses,
 			XClass collType,
 			Ejb3JoinColumn[] fkJoinColumns,
 			Ejb3JoinColumn[] keyColumns,
 			Ejb3JoinColumn[] inverseColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XProperty property,
 			boolean unique,
 			TableBinder associationTableBinder,
 			boolean ignoreNotFound,
 			Mappings mappings) {
 		PersistentClass persistentClass = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean reversePropertyInJoin = false;
 		if ( persistentClass != null && StringHelper.isNotEmpty( this.mappedBy ) ) {
 			try {
 				reversePropertyInJoin = 0 != persistentClass.getJoinNumber(
 						persistentClass.getRecursiveProperty( this.mappedBy )
 				);
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( this.mappedBy )
 						.append( " in " )
 						.append( collection.getOwnerEntityName() )
 						.append( "." )
 						.append( property.getName() );
 				throw new AnnotationException( error.toString() );
 			}
 		}
 		if ( persistentClass != null
 				&& !reversePropertyInJoin
 				&& oneToMany
 				&& !this.isExplicitAssociationTable
 				&& ( joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue( this.mappedBy ) //implicit @JoinColumn
 				|| !fkJoinColumns[0].isImplicit() ) //this is an explicit @JoinColumn
 				) {
 			//this is a Foreign key
 			bindOneToManySecondPass(
 					getCollection(),
 					persistentClasses,
 					fkJoinColumns,
 					collType,
 					cascadeDeleteEnabled,
 					ignoreNotFound, hqlOrderBy,
 					mappings,
 					inheritanceStatePerClass
 			);
 			return true;
 		}
 		else {
 			//this is an association table
 			bindManyToManySecondPass(
 					this.collection,
 					persistentClasses,
 					keyColumns,
 					inverseColumns,
 					elementColumns,
 					isEmbedded, collType,
 					ignoreNotFound, unique,
 					cascadeDeleteEnabled,
 					associationTableBinder, property, propertyHolder, hqlOrderBy, mappings
 			);
 			return false;
 		}
 	}
 
 	protected void bindOneToManySecondPass(
 			Collection collection,
 			Map persistentClasses,
 			Ejb3JoinColumn[] fkJoinColumns,
 			XClass collectionType,
 			boolean cascadeDeleteEnabled,
 			boolean ignoreNotFound,
 			String hqlOrderBy,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		log.debug("Binding a OneToMany: {}.{} through a foreign key", propertyHolder.getEntityName(), propertyName);
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( mappings, collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		String orderBy = buildOrderByClauseFromHql( hqlOrderBy, associatedClass, collection.getRole() );
 		if ( orderBy != null ) collection.setOrderBy( orderBy );
 		if ( mappings == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = mappings.getJoins( assocClass );
 		if ( associatedClass == null ) {
 			throw new MappingException(
 					"Association references unmapped class: " + assocClass
 			);
 		}
 		oneToMany.setAssociatedClass( associatedClass );
 		for (Ejb3JoinColumn column : fkJoinColumns) {
 			column.setPersistentClass( associatedClass, joins, inheritanceStatePerClass );
 			column.setJoins( joins );
 			collection.setCollectionTable( column.getTable() );
 		}
 		log.info(
 				"Mapping collection: " + collection.getRole() + " -> " + collection.getCollectionTable().getName()
 		);
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 
 	private void bindFilters(boolean hasAssociationTable) {
 		Filter simpleFilter = property.getAnnotation( Filter.class );
 		//set filtering
 		//test incompatible choices
 		//if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
 		if ( simpleFilter != null ) {
 			if ( hasAssociationTable ) {
 				collection.addManyToManyFilter( simpleFilter.name(), getCondition( simpleFilter ) );
 			}
 			else {
 				collection.addFilter( simpleFilter.name(), getCondition( simpleFilter ) );
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition( filter ) );
 				}
 				else {
 					collection.addFilter( filter.name(), getCondition( filter ) );
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter( simpleFilterJoinTable.name(), getCondition( simpleFilterJoinTable ) );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @FilterJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		FilterJoinTables filterJoinTables = property.getAnnotation( FilterJoinTables.class );
 		if ( filterJoinTables != null ) {
 			for (FilterJoinTable filter : filterJoinTables.value()) {
 				if ( hasAssociationTable ) {
 					collection.addFilter( filter.name(), getCondition( filter ) );
 				}
 				else {
 					throw new AnnotationException(
 							"Illegal use of @FilterJoinTable on an association without join table:"
 									+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 					);
 				}
 			}
 		}
 
 		Where where = property.getAnnotation( Where.class );
 		String whereClause = where == null ? null : where.clause();
 		if ( StringHelper.isNotEmpty( whereClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setManyToManyWhere( whereClause );
 			}
 			else {
 				collection.setWhere( whereClause );
 			}
 		}
 
 		WhereJoinTable whereJoinTable = property.getAnnotation( WhereJoinTable.class );
 		String whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
 		if ( StringHelper.isNotEmpty( whereJoinTableClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setWhere( whereJoinTableClause );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @WhereJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 //		This cannot happen in annotations since the second fetch is hardcoded to join
 //		if ( ( ! collection.getManyToManyFilterMap().isEmpty() || collection.getManyToManyWhere() != null ) &&
 //		        collection.getFetchMode() == FetchMode.JOIN &&
 //		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 //			throw new MappingException(
 //			        "association with join table  defining filter or where without join fetching " +
 //			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 //				);
 //		}
 	}
 
 	private String getCondition(FilterJoinTable filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(Filter filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(String cond, String name) {
 		if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 			cond = mappings.getFilterDefinition( name ).getDefaultFilterCondition();
 			if ( StringHelper.isEmpty( cond ) ) {
 				throw new AnnotationException(
 						"no filter condition found for filter " + name + " in "
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		return cond;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegionName = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ? null : cacheAnn.region();
 			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
 		}
 		else {
 			cacheConcurrencyStrategy = null;
 			cacheRegionName = null;
 		}
 	}
 
 	public void setOneToMany(boolean oneToMany) {
 		this.oneToMany = oneToMany;
 	}
 
 	public void setIndexColumn(IndexColumn indexColumn) {
 		this.indexColumn = indexColumn;
 	}
 
 	public void setMapKey(MapKey key) {
 		if ( key != null ) {
 			mapKeyPropertyName = key.name();
 		}
 	}
 
 	private static String buildOrderByClauseFromHql(String hqlOrderBy, PersistentClass associatedClass, String role) {
 		String orderByString = null;
 		if ( hqlOrderBy != null ) {
 			List<String> properties = new ArrayList<String>();
 			List<String> ordering = new ArrayList<String>();
 			StringBuilder orderByBuffer = new StringBuilder();
 			if ( hqlOrderBy.length() == 0 ) {
 				//order by id
 				Iterator it = associatedClass.getIdentifier().getColumnIterator();
 				while ( it.hasNext() ) {
 					Selectable col = (Selectable) it.next();
 					orderByBuffer.append( col.getText() ).append( " asc" ).append( ", " );
 				}
 			}
 			else {
 				StringTokenizer st = new StringTokenizer( hqlOrderBy, " ,", false );
 				String currentOrdering = null;
 				//FIXME make this code decent
 				while ( st.hasMoreTokens() ) {
 					String token = st.nextToken();
 					if ( isNonPropertyToken( token ) ) {
 						if ( currentOrdering != null ) {
 							throw new AnnotationException(
 									"Error while parsing HQL orderBy clause: " + hqlOrderBy
 											+ " (" + role + ")"
 							);
 						}
 						currentOrdering = token;
 					}
 					else {
 						//Add ordering of the previous
 						if ( currentOrdering == null ) {
 							//default ordering
 							ordering.add( "asc" );
 						}
 						else {
 							ordering.add( currentOrdering );
 							currentOrdering = null;
 						}
 						properties.add( token );
 					}
 				}
 				ordering.remove( 0 ); //first one is the algorithm starter
 				// add last one ordering
 				if ( currentOrdering == null ) {
 					//default ordering
 					ordering.add( "asc" );
 				}
 				else {
 					ordering.add( currentOrdering );
 					currentOrdering = null;
 				}
 				int index = 0;
 
 				for (String property : properties) {
 					Property p = BinderHelper.findPropertyByName( associatedClass, property );
 					if ( p == null ) {
 						throw new AnnotationException(
 								"property from @OrderBy clause not found: "
 										+ associatedClass.getEntityName() + "." + property
 						);
 					}
 					PersistentClass pc = p.getPersistentClass();
 					String table;
 					if ( pc == null ) {
 						//we are touching a @IdClass property, the pc is not set
 						//this means pc == associatedClass
 						//TODO check whether @ManyToOne @JoinTable in @IdClass used for @OrderBy works: doh!
 						table = "";
 					}
 					
 					else if (pc == associatedClass
 							|| (associatedClass instanceof SingleTableSubclass && pc
 									.getMappedClass().isAssignableFrom(
 											associatedClass.getMappedClass()))) {
 						table = "";
 					} else {
 						table = pc.getTable().getQuotedName() + ".";
 					}
 					
 					Iterator propertyColumns = p.getColumnIterator();
 					while ( propertyColumns.hasNext() ) {
 						Selectable column = (Selectable) propertyColumns.next();
 						orderByBuffer.append( table )
 								.append( column.getText() )
 								.append( " " )
 								.append( ordering.get( index ) )
 								.append( ", " );
 					}
 					index++;
 				}
 			}
 			orderByString = orderByBuffer.substring( 0, orderByBuffer.length() - 2 );
 		}
 		return orderByString;
 	}
 
 	private static String buildOrderByClauseFromHql(String hqlOrderBy, Component component, String role) {
 		String orderByString = null;
 		if ( hqlOrderBy != null ) {
 			List<String> properties = new ArrayList<String>();
 			List<String> ordering = new ArrayList<String>();
 			StringBuilder orderByBuffer = new StringBuilder();
 			if ( hqlOrderBy.length() == 0 ) {
 				//TODO : Check that. Maybe order by key for maps
 			}
 			else {
 				StringTokenizer st = new StringTokenizer( hqlOrderBy, " ,", false );
 				String currentOrdering = null;
 				//FIXME make this code decent
 				while ( st.hasMoreTokens() ) {
 					String token = st.nextToken();
 					if ( isNonPropertyToken( token ) ) {
 						if ( currentOrdering != null ) {
 							throw new AnnotationException(
 									"Error while parsing HQL orderBy clause: " + hqlOrderBy
 											+ " (" + role + ")"
 							);
 						}
 						currentOrdering = token;
 					}
 					else {
 						//Add ordering of the previous
 						if ( currentOrdering == null ) {
 							//default ordering
 							ordering.add( "asc" );
 						}
 						else {
 							ordering.add( currentOrdering );
 							currentOrdering = null;
 						}
 						properties.add( token );
 					}
 				}
 				ordering.remove( 0 ); //first one is the algorithm starter
 				// add last one ordering
 				if ( currentOrdering == null ) {
 					//default ordering
 					ordering.add( "asc" );
 				}
 				else {
 					ordering.add( currentOrdering );
 					currentOrdering = null;
 				}
 				int index = 0;
 
 				for (String property : properties) {
 					Property p = BinderHelper.findPropertyByName( component, property );
 					if ( p == null ) {
 						throw new AnnotationException(
 								"property from @OrderBy clause not found: "
 										+ role + "." + property
 						);
 					}
 
 					Iterator propertyColumns = p.getColumnIterator();
 					while ( propertyColumns.hasNext() ) {
 						Selectable column = (Selectable) propertyColumns.next();
 						orderByBuffer.append( column.getText() )
 								.append( " " )
 								.append( ordering.get( index ) )
 								.append( ", " );
 					}
 					index++;
 				}
 
 				if ( orderByBuffer.length() >= 2 ) {
 					orderByString = orderByBuffer.substring( 0, orderByBuffer.length() - 2 );
 				}
 			}
 		}
 		return orderByString;
 	}
 
 	private static boolean isNonPropertyToken(String token) {
 		if ( " ".equals( token ) ) return true;
 		if ( ",".equals( token ) ) return true;
 		if ( token.equalsIgnoreCase( "desc" ) ) return true;
 		if ( token.equalsIgnoreCase( "asc" ) ) return true;
 		return false;
 	}
 
 	private static SimpleValue buildCollectionKey(
 			Collection collValue, Ejb3JoinColumn[] joinColumns, boolean cascadeDeleteEnabled,
 			XProperty property, Mappings mappings
 	) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = mappings.getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				mappings.addPropertyReference( collValue.getOwnerEntityName(), propRef );
 			}
 		}
 		String propRef = collValue.getReferencedPropertyName();
 		if ( propRef == null ) {
 			keyVal = collValue.getOwner().getIdentifier();
 		}
 		else {
 			keyVal = (KeyValue) collValue.getOwner()
 					.getRecursiveProperty( propRef )
 					.getValue();
 		}
 		DependantValue key = new DependantValue( mappings, collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 		ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 		String fkName = fk != null ? fk.name() : "";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) key.setForeignKeyName( fkName );
 		return key;
 	}
 
 	protected void bindManyToManySecondPass(
 			Collection collValue,
 			Map persistentClasses,
 			Ejb3JoinColumn[] joinColumns,
 			Ejb3JoinColumn[] inverseJoinColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XClass collType,
 			boolean ignoreNotFound, boolean unique,
 			boolean cascadeDeleteEnabled,
 			TableBinder associationTableBinder,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			String hqlOrderBy,
 			Mappings mappings) throws MappingException {
 
 		PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
 		if ( log.isDebugEnabled() ) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 			if ( isCollectionOfEntities && unique ) {
 				log.debug( "Binding a OneToMany: {} through an association table", path );
 			}
 			else if ( isCollectionOfEntities ) {
 				log.debug( "Binding as ManyToMany: {}", path );
 			}
 			else if ( anyAnn != null ) {
 				log.debug( "Binding a ManyToAny: {}", path );
 			}
 			else {
 				log.debug( "Binding a collection of element: {}", path );
 			}
 		}
 		//check for user error
 		if ( !isCollectionOfEntities ) {
 			if ( property.isAnnotationPresent( ManyToMany.class ) || property.isAnnotationPresent( OneToMany.class ) ) {
 				String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 				throw new AnnotationException(
 						"Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]"
 				);
 			}
 			else if ( anyAnn != null ) {
 				if ( parentPropertyHolder.getJoinTable( property ) == null ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"@JoinTable is mandatory when @ManyToAny is used: " + path
 					);
 				}
 			}
 			else {
 				JoinTable joinTableAnn = parentPropertyHolder.getJoinTable( property );
 				if ( joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0 ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path + "[" + collType + "]"
 					);
 				}
 			}
 		}
 
 		boolean mappedBy = !BinderHelper.isEmptyAnnotationValue( joinColumns[0].getMappedBy() );
 		if ( mappedBy ) {
 			if ( !isCollectionOfEntities ) {
 				StringBuilder error = new StringBuilder( 80 )
 						.append(
 								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
 						)
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Property otherSideProperty;
 			try {
 				otherSideProperty = collectionEntity.getRecursiveProperty( joinColumns[0].getMappedBy() );
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( joinColumns[0].getMappedBy() )
 						.append( " in " )
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Table table;
 			if ( otherSideProperty.getValue() instanceof Collection ) {
 				//this is a collection on the other side
 				table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
 			}
 			else {
 				//This is a ToOne with a @JoinTable or a regular property
 				table = otherSideProperty.getValue().getTable();
 			}
 			collValue.setCollectionTable( table );
 			String entityName = collectionEntity.getEntityName();
 			for (Ejb3JoinColumn column : joinColumns) {
 				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
 				column.setManyToManyOwnerSideEntityName( entityName );
 			}
 		}
 		else {
 			//TODO: only for implicit columns?
 			//FIXME NamingStrategy
 			for (Ejb3JoinColumn column : joinColumns) {
 				String mappedByProperty = mappings.getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(), mappings.getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getEntityName(),
 						mappings.getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? mappings.getLogicalTableName( collectionEntity.getTable() ) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, mappings );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element =
 					new ManyToOne( mappings,  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA-2 spec, default to primary key if no column is specified by @OrderBy. 
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 			ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 			String fkName = fk != null ? fk.inverseName() : "";
 			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) element.setForeignKeyName( fkName );
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", mappings.getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue( anyAnn.metaDef(), inverseJoinColumns, anyAnn.metaColumn(),
 					inferredData, cascadeDeleteEnabled, Nullability.NO_CONSTRAINT,
 					propertyHolder, new EntityBinder(), true, mappings );
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			PropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 			}
 			else {
 				elementClass = collType;
 				classType = mappings.getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property, parentPropertyHolder, mappings
 				);
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				if ( isEmbedded || attributeOverride ) {
 					classType = AnnotatedClassType.EMBEDDABLE;
 				}
 			}
 
 			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 				EntityBinder entityBinder = new EntityBinder();
 				PersistentClass owner = collValue.getOwner();
 				boolean isPropertyAnnotated;
 				//FIXME support @Access for collection of elements
 				//String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				PropertyData inferredData;
 				if ( isMap() ) {
 					//"value" is the JPA 2 prefix for map values (used to be "element")
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "value", elementClass );
 					}
 				}
 				else {
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						//"collection&&element" is not a valid property name => placeholder
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "collection&&element", elementClass );
 					}
 				}
 				//TODO be smart with isNullable
 				Component component = AnnotationBinder.fillComponent(
 						holder, inferredData, isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD, true,
 						entityBinder, false, false,
 						true, mappings, inheritanceStatePerClass
 				);
 
 				collValue.setElement( component );
 
 				if ( StringHelper.isNotEmpty( hqlOrderBy ) ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					String orderBy = buildOrderByClauseFromHql( hqlOrderBy, component, path );
 					if ( orderBy != null ) {
 						collValue.setOrderBy( orderBy );
 					}
 				}
 			}
 			else {
 				SimpleValueBinder elementBinder = new SimpleValueBinder();
 				elementBinder.setMappings( mappings );
 				elementBinder.setReturnedClassName( collType.getName() );
 				if ( elementColumns == null || elementColumns.length == 0 ) {
 					elementColumns = new Ejb3Column[1];
 					Ejb3Column column = new Ejb3Column();
 					column.setImplicit( false );
 					//not following the spec but more clean
 					column.setNullable( true );
 					column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 					column.setLogicalColumnName( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 					//TODO create an EMPTY_JOINS collection
 					column.setJoins( new HashMap<String, Join>() );
 					column.setMappings( mappings );
 					column.bind();
 					elementColumns[0] = column;
 				}
 				//override the table
 				for (Ejb3Column column : elementColumns) {
 					column.setTable( collValue.getCollectionTable() );
 				}
 				elementBinder.setColumns( elementColumns );
 				elementBinder.setType( property, elementClass );
 				collValue.setElement( elementBinder.make() );
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index e0c988cf9a..fe0daf3e4d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,944 +1,934 @@
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
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.Access;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.SecondaryTable;
 import javax.persistence.SecondaryTables;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Subselect;
 import org.hibernate.annotations.Synchronize;
 import org.hibernate.annotations.Tables;
 import org.hibernate.annotations.Tuplizer;
 import org.hibernate.annotations.Tuplizers;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Versioning;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TableOwner;
 import org.hibernate.mapping.Value;
-import org.hibernate.persister.PersisterClassProvider;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
 	private String name;
 	private XClass annotatedClass;
 	private PersistentClass persistentClass;
 	private Mappings mappings;
 	private Logger log = LoggerFactory.getLogger( EntityBinder.class );
 	private String discriminatorValue = "";
 	private Boolean forceDiscriminator;
 	private Boolean insertableDiscriminator;
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private boolean explicitHibernateEntityAnnotation;
 	private OptimisticLockType optimisticLockType;
 	private PolymorphismType polymorphismType;
 	private boolean selectBeforeUpdate;
 	private int batchSize;
 	private boolean lazy;
 	private XClass proxyClass;
 	private String where;
 	private java.util.Map<String, Join> secondaryTables = new HashMap<String, Join>();
 	private java.util.Map<String, Object> secondaryTableJoins = new HashMap<String, Object>();
 	private String cacheConcurrentStrategy;
 	private String cacheRegion;
 	private java.util.Map<String, String> filters = new HashMap<String, String>();
 	private InheritanceState inheritanceState;
 	private boolean ignoreIdAnnotations;
 	private boolean cacheLazyProperty;
 	private AccessType propertyAccessType = AccessType.DEFAULT;
 	private boolean wrapIdsInEmbeddedComponents;
 	private String subselect;
 	
 	
 	public boolean wrapIdsInEmbeddedComponents() {
 		return wrapIdsInEmbeddedComponents;
 	}
 
 	/**
 	 * Use as a fake one for Collection of elements
 	 */
 	public EntityBinder() {
 	}
 
 	public EntityBinder(
 			Entity ejb3Ann,
 			org.hibernate.annotations.Entity hibAnn,
 			XClass annotatedClass,
 			PersistentClass persistentClass,
 			Mappings mappings) {
 		this.mappings = mappings;
 		this.persistentClass = persistentClass;
 		this.annotatedClass = annotatedClass;
 		bindEjb3Annotation( ejb3Ann );
 		bindHibernateAnnotation( hibAnn );
 	}
 
 	private void bindHibernateAnnotation(org.hibernate.annotations.Entity hibAnn) {
 		if ( hibAnn != null ) {
 			dynamicInsert = hibAnn.dynamicInsert();
 			dynamicUpdate = hibAnn.dynamicUpdate();
 			optimisticLockType = hibAnn.optimisticLock();
 			selectBeforeUpdate = hibAnn.selectBeforeUpdate();
 			polymorphismType = hibAnn.polymorphism();
 			explicitHibernateEntityAnnotation = true;
 			//persister handled in bind
 		}
 		else {
 			//default values when the annotation is not there
 			dynamicInsert = false;
 			dynamicUpdate = false;
 			optimisticLockType = OptimisticLockType.VERSION;
 			polymorphismType = PolymorphismType.IMPLICIT;
 			selectBeforeUpdate = false;
 		}
 	}
 
 	private void bindEjb3Annotation(Entity ejb3Ann) {
 		if ( ejb3Ann == null ) throw new AssertionFailure( "@Entity should always be not null" );
 		if ( BinderHelper.isEmptyAnnotationValue( ejb3Ann.name() ) ) {
 			name = StringHelper.unqualify( annotatedClass.getName() );
 		}
 		else {
 			name = ejb3Ann.name();
 		}
 	}
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public void setForceDiscriminator(boolean forceDiscriminator) {
 		this.forceDiscriminator = forceDiscriminator;
 	}
 
 	public void setInsertableDiscriminator(boolean insertableDiscriminator) {
 		this.insertableDiscriminator = insertableDiscriminator;
 	}
 
 	public void bindEntity() {
 		persistentClass.setAbstract( annotatedClass.isAbstract() );
 		persistentClass.setClassName( annotatedClass.getName() );
 		persistentClass.setNodeName( name );
 		persistentClass.setJpaEntityName(name);
 		//persistentClass.setDynamic(false); //no longer needed with the Entity name refactoring?
 		persistentClass.setEntityName( annotatedClass.getName() );
 		bindDiscriminatorValue();
 
 		persistentClass.setLazy( lazy );
 		if ( proxyClass != null ) {
 			persistentClass.setProxyInterfaceName( proxyClass.getName() );
 		}
 		persistentClass.setDynamicInsert( dynamicInsert );
 		persistentClass.setDynamicUpdate( dynamicUpdate );
 
 		if ( persistentClass instanceof RootClass ) {
 			RootClass rootClass = (RootClass) persistentClass;
 			boolean mutable = true;
 			//priority on @Immutable, then @Entity.mutable()
 			if ( annotatedClass.isAnnotationPresent( Immutable.class ) ) {
 				mutable = false;
 			}
 			else {
 				org.hibernate.annotations.Entity entityAnn =
 						annotatedClass.getAnnotation( org.hibernate.annotations.Entity.class );
 				if ( entityAnn != null ) {
 					mutable = entityAnn.mutable();
 				}
 			}
 			rootClass.setMutable( mutable );
 			rootClass.setExplicitPolymorphism( isExplicitPolymorphism( polymorphismType ) );
 			if ( StringHelper.isNotEmpty( where ) ) rootClass.setWhere( where );
 			if ( cacheConcurrentStrategy != null ) {
 				rootClass.setCacheConcurrencyStrategy( cacheConcurrentStrategy );
 				rootClass.setCacheRegionName( cacheRegion );
 				rootClass.setLazyPropertiesCacheable( cacheLazyProperty );
 			}
 			if(forceDiscriminator != null) {
 				rootClass.setForceDiscriminator( forceDiscriminator );
 			}
 			if( insertableDiscriminator != null) {
 				rootClass.setDiscriminatorInsertable( insertableDiscriminator );
 			}
 		}
 		else {
 			if ( explicitHibernateEntityAnnotation ) {
 				log.warn( "@org.hibernate.annotations.Entity used on a non root entity: ignored for {}",
 						annotatedClass.getName() );
 			}
 			if ( annotatedClass.isAnnotationPresent( Immutable.class ) ) {
 				log.warn( "@Immutable used on a non root entity: ignored for {}",
 						annotatedClass.getName() );
 			}
 		}
 		persistentClass.setOptimisticLockMode( getVersioning( optimisticLockType ) );
 		persistentClass.setSelectBeforeUpdate( selectBeforeUpdate );
 
 		//set persister if needed
-		//@Persister has precedence over @Entity.persister
-		//in both fail we look for the PersisterClassProvider
-		//if all fail, the persister is left null and the Hibernate defaults kick in
 		Persister persisterAnn = annotatedClass.getAnnotation( Persister.class );
 		Class persister = null;
 		if ( persisterAnn != null ) {
 			persister = persisterAnn.impl();
 		}
 		else {
 			org.hibernate.annotations.Entity entityAnn = annotatedClass.getAnnotation( org.hibernate.annotations.Entity.class );
 			if ( entityAnn != null && !BinderHelper.isEmptyAnnotationValue( entityAnn.persister() ) ) {
 				try {
 					persister = ReflectHelper.classForName( entityAnn.persister() );
 				}
 				catch (ClassNotFoundException cnfe) {
 					throw new AnnotationException( "Could not find persister class: " + persister );
 				}
 			}
-			else {
-				final PersisterClassProvider persisterClassProvider = mappings.getPersisterClassProvider();
-				if ( persisterClassProvider != null ) {
-					persister = persisterClassProvider.getEntityPersisterClass( persistentClass.getEntityName() );
-				}
-			}
 		}
 		if ( persister != null ) {
 			persistentClass.setEntityPersisterClass( persister );
 		}
 
 		persistentClass.setBatchSize( batchSize );
 
 		//SQL overriding
 		SQLInsert sqlInsert = annotatedClass.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = annotatedClass.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = annotatedClass.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = annotatedClass.getAnnotation( SQLDeleteAll.class );
 		Loader loader = annotatedClass.getAnnotation( Loader.class );
 		
 		if ( sqlInsert != null ) {
 			persistentClass.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.parse( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			persistentClass.setLoaderName( loader.namedQuery() );
 		}
 
 		if ( annotatedClass.isAnnotationPresent( Synchronize.class )) {
 			Synchronize synchronizedWith = annotatedClass.getAnnotation(Synchronize.class);
 		
 			String [] tables = synchronizedWith.value();
 			for (String table : tables) {
 				persistentClass.addSynchronizedTable(table);
 			}
 		}
 				
 		if ( annotatedClass.isAnnotationPresent(Subselect.class )) {
 			Subselect subselect = annotatedClass.getAnnotation(Subselect.class);
 			this.subselect = subselect.value();
 		}	
 				
 		//tuplizers
 		if ( annotatedClass.isAnnotationPresent( Tuplizers.class ) ) {
 			for (Tuplizer tuplizer : annotatedClass.getAnnotation( Tuplizers.class ).value()) {
 				EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 				persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( annotatedClass.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = annotatedClass.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 
 		if ( !inheritanceState.hasParents() ) {
 			for ( Map.Entry<String, String> filter : filters.entrySet() ) {
 				String filterName = filter.getKey();
 				String cond = filter.getValue();
 				if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 					FilterDefinition definition = mappings.getFilterDefinition( filterName );
 					cond = definition == null ? null : definition.getDefaultFilterCondition();
 					if ( StringHelper.isEmpty( cond ) ) {
 						throw new AnnotationException(
 								"no filter condition found for filter " + filterName + " in " + this.name
 						);
 					}
 				}
 				persistentClass.addFilter( filterName, cond );
 			}
 		}
 		else {
 			if ( filters.size() > 0 ) {
 				log.warn( "@Filter not allowed on subclasses (ignored): {}", persistentClass.getEntityName() );
 			}
 		}
 		log.debug( "Import with entity name {}", name );
 		try {
 			mappings.addImport( persistentClass.getEntityName(), name );
 			String entityName = persistentClass.getEntityName();
 			if ( !entityName.equals( name ) ) {
 				mappings.addImport( entityName, entityName );
 			}
 		}
 		catch (MappingException me) {
 			throw new AnnotationException( "Use of the same entity name twice: " + name, me );
 		}
 	}
 
 	public void bindDiscriminatorValue() {
 		if ( StringHelper.isEmpty( discriminatorValue ) ) {
 			Value discriminator = persistentClass.getDiscriminator();
 			if ( discriminator == null ) {
 				persistentClass.setDiscriminatorValue( name );
 			}
 			else if ( "character".equals( discriminator.getType().getName() ) ) {
 				throw new AnnotationException(
 						"Using default @DiscriminatorValue for a discriminator of type CHAR is not safe"
 				);
 			}
 			else if ( "integer".equals( discriminator.getType().getName() ) ) {
 				persistentClass.setDiscriminatorValue( String.valueOf( name.hashCode() ) );
 			}
 			else {
 				persistentClass.setDiscriminatorValue( name ); //Spec compliant
 			}
 		}
 		else {
 			//persistentClass.getDiscriminator()
 			persistentClass.setDiscriminatorValue( discriminatorValue );
 		}
 	}
 
 	int getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
 				return Versioning.OPTIMISTIC_LOCK_VERSION;
 			case NONE:
 				return Versioning.OPTIMISTIC_LOCK_NONE;
 			case DIRTY:
 				return Versioning.OPTIMISTIC_LOCK_DIRTY;
 			case ALL:
 				return Versioning.OPTIMISTIC_LOCK_ALL;
 			default:
 				throw new AssertionFailure( "optimistic locking not supported: " + type );
 		}
 	}
 
 	private boolean isExplicitPolymorphism(PolymorphismType type) {
 		switch ( type ) {
 			case IMPLICIT:
 				return false;
 			case EXPLICIT:
 				return true;
 			default:
 				throw new AssertionFailure( "Unknown polymorphism type: " + type );
 		}
 	}
 
 	public void setBatchSize(BatchSize sizeAnn) {
 		if ( sizeAnn != null ) {
 			batchSize = sizeAnn.size();
 		}
 		else {
 			batchSize = -1;
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void setProxy(Proxy proxy) {
 		if ( proxy != null ) {
 			lazy = proxy.lazy();
 			if ( !lazy ) {
 				proxyClass = null;
 			}
 			else {
 				if ( AnnotationBinder.isDefault(
 						mappings.getReflectionManager().toXClass( proxy.proxyClass() ), mappings
 				) ) {
 					proxyClass = annotatedClass;
 				}
 				else {
 					proxyClass = mappings.getReflectionManager().toXClass( proxy.proxyClass() );
 				}
 			}
 		}
 		else {
 			lazy = true; //needed to allow association lazy loading.
 			proxyClass = annotatedClass;
 		}
 	}
 
 	public void setWhere(Where whereAnn) {
 		if ( whereAnn != null ) {
 			where = whereAnn.clause();
 		}
 	}
 
 	public void setWrapIdsInEmbeddedComponents(boolean wrapIdsInEmbeddedComponents) {
 		this.wrapIdsInEmbeddedComponents = wrapIdsInEmbeddedComponents;
 	}
 
 
 	private static class EntityTableObjectNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private EntityTableObjectNameSource(String explicitName, String entityName) {
 			this.explicitName = explicitName;
 			this.logicalName = StringHelper.isNotEmpty( explicitName )
 					? explicitName
 					: StringHelper.unqualify( entityName );
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	private static class EntityTableNamingStrategyHelper implements ObjectNameNormalizer.NamingStrategyHelper {
 		private final String entityName;
 
 		private EntityTableNamingStrategyHelper(String entityName) {
 			this.entityName = entityName;
 		}
 
 		public String determineImplicitName(NamingStrategy strategy) {
 			return strategy.classToTableName( entityName );
 		}
 
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperclassTable) {
 		EntityTableObjectNameSource tableNameContext = new EntityTableObjectNameSource( tableName, name );
 		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper( name );
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				tableNameContext,
 				namingStrategyHelper,
 				persistentClass.isAbstract(),
 				uniqueConstraints,
 				constraints,
 				denormalizedSuperclassTable,
 				mappings,
 				this.subselect
 		);
 
 		if ( persistentClass instanceof TableOwner ) {
 			log.info( "Bind entity {} on table {}", persistentClass.getEntityName(), table.getName() );
 			( (TableOwner) persistentClass ).setTable( table );
 		}
 		else {
 			throw new AssertionFailure( "binding a table for a subclass" );
 		}
 	}
 
 	public void finalSecondaryTableBinding(PropertyHolder propertyHolder) {
 		/*
 		 * Those operations has to be done after the id definition of the persistence class.
 		 * ie after the properties parsing
 		 */
 		Iterator joins = secondaryTables.values().iterator();
 		Iterator joinColumns = secondaryTableJoins.values().iterator();
 
 		while ( joins.hasNext() ) {
 			Object uncastedColumn = joinColumns.next();
 			Join join = (Join) joins.next();
 			createPrimaryColumnsToSecondaryTable( uncastedColumn, propertyHolder, join );
 		}
 		mappings.addJoins( persistentClass, secondaryTables );
 	}
 
 	private void createPrimaryColumnsToSecondaryTable(Object uncastedColumn, PropertyHolder propertyHolder, Join join) {
 		Ejb3JoinColumn[] ejb3JoinColumns;
 		PrimaryKeyJoinColumn[] pkColumnsAnn = null;
 		JoinColumn[] joinColumnsAnn = null;
 		if ( uncastedColumn instanceof PrimaryKeyJoinColumn[] ) {
 			pkColumnsAnn = (PrimaryKeyJoinColumn[]) uncastedColumn;
 		}
 		if ( uncastedColumn instanceof JoinColumn[] ) {
 			joinColumnsAnn = (JoinColumn[]) uncastedColumn;
 		}
 		if ( pkColumnsAnn == null && joinColumnsAnn == null ) {
 			ejb3JoinColumns = new Ejb3JoinColumn[1];
 			ejb3JoinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 					null,
 					null,
 					persistentClass.getIdentifier(),
 					secondaryTables,
 					propertyHolder, mappings
 			);
 		}
 		else {
 			int nbrOfJoinColumns = pkColumnsAnn != null ?
 					pkColumnsAnn.length :
 					joinColumnsAnn.length;
 			if ( nbrOfJoinColumns == 0 ) {
 				ejb3JoinColumns = new Ejb3JoinColumn[1];
 				ejb3JoinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						null,
 						null,
 						persistentClass.getIdentifier(),
 						secondaryTables,
 						propertyHolder, mappings
 				);
 			}
 			else {
 				ejb3JoinColumns = new Ejb3JoinColumn[nbrOfJoinColumns];
 				if ( pkColumnsAnn != null ) {
 					for (int colIndex = 0; colIndex < nbrOfJoinColumns; colIndex++) {
 						ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 								pkColumnsAnn[colIndex],
 								null,
 								persistentClass.getIdentifier(),
 								secondaryTables,
 								propertyHolder, mappings
 						);
 					}
 				}
 				else {
 					for (int colIndex = 0; colIndex < nbrOfJoinColumns; colIndex++) {
 						ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 								null,
 								joinColumnsAnn[colIndex],
 								persistentClass.getIdentifier(),
 								secondaryTables,
 								propertyHolder, mappings
 						);
 					}
 				}
 			}
 		}
 
 		for (Ejb3JoinColumn joinColumn : ejb3JoinColumns) {
 			joinColumn.forceNotNull();
 		}
 		bindJoinToPersistentClass( join, ejb3JoinColumns, mappings );
 	}
 
 	private void bindJoinToPersistentClass(Join join, Ejb3JoinColumn[] ejb3JoinColumns, Mappings mappings) {
 		SimpleValue key = new DependantValue( mappings, join.getTable(), persistentClass.getIdentifier() );
 		join.setKey( key );
 		setFKNameIfDefined( join );
 		key.setCascadeDeleteEnabled( false );
 		TableBinder.bindFk( persistentClass, null, ejb3JoinColumns, key, false, mappings );
 		join.createPrimaryKey();
 		join.createForeignKey();
 		persistentClass.addJoin( join );
 	}
 
 	private void setFKNameIfDefined(Join join) {
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null && !BinderHelper.isEmptyAnnotationValue( matchingTable.foreignKey().name() ) ) {
 			( (SimpleValue) join.getKey() ).setForeignKeyName( matchingTable.foreignKey().name() );
 		}
 	}
 
 	private org.hibernate.annotations.Table findMatchingComplimentTableAnnotation(Join join) {
 		String tableName = join.getTable().getQuotedName();
 		org.hibernate.annotations.Table table = annotatedClass.getAnnotation( org.hibernate.annotations.Table.class );
 		org.hibernate.annotations.Table matchingTable = null;
 		if ( table != null && tableName.equals( table.appliesTo() ) ) {
 			matchingTable = table;
 		}
 		else {
 			Tables tables = annotatedClass.getAnnotation( Tables.class );
 			if ( tables != null ) {
 				for (org.hibernate.annotations.Table current : tables.value()) {
 					if ( tableName.equals( current.appliesTo() ) ) {
 						matchingTable = current;
 						break;
 					}
 				}
 			}
 		}
 		return matchingTable;
 	}
 
 	public void firstLevelSecondaryTablesBinding(
 			SecondaryTable secTable, SecondaryTables secTables
 	) {
 		if ( secTables != null ) {
 			//loop through it
 			for (SecondaryTable tab : secTables.value()) {
 				addJoin( tab, null, null, false );
 			}
 		}
 		else {
 			if ( secTable != null ) addJoin( secTable, null, null, false );
 		}
 	}
 
 	//Used for @*ToMany @JoinTable
 	public Join addJoin(JoinTable joinTable, PropertyHolder holder, boolean noDelayInPkColumnCreation) {
 		return addJoin( null, joinTable, holder, noDelayInPkColumnCreation );
 	}
 
 	private static class SecondaryTableNameSource implements ObjectNameSource {
 		// always has an explicit name
 		private final String explicitName;
 
 		private SecondaryTableNameSource(String explicitName) {
 			this.explicitName = explicitName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return explicitName;
 		}
 	}
 
 	private static class SecondaryTableNamingStrategyHelper implements ObjectNameNormalizer.NamingStrategyHelper {
 		public String determineImplicitName(NamingStrategy strategy) {
 			// todo : throw an error?
 			return null;
 		}
 
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
 	}
 
 	private static SecondaryTableNamingStrategyHelper SEC_TBL_NS_HELPER = new SecondaryTableNamingStrategyHelper();
 
 	private Join addJoin(
 			SecondaryTable secondaryTable,
 			JoinTable joinTable,
 			PropertyHolder propertyHolder,
 			boolean noDelayInPkColumnCreation) {
 		// A non null propertyHolder means than we process the Pk creation without delay
 		Join join = new Join();
 		join.setPersistentClass( persistentClass );
 
 		final String schema;
 		final String catalog;
 		final SecondaryTableNameSource secondaryTableNameContext;
 		final Object joinColumns;
 		final List<UniqueConstraintHolder> uniqueConstraintHolders;
 
 		if ( secondaryTable != null ) {
 			schema = secondaryTable.schema();
 			catalog = secondaryTable.catalog();
 			secondaryTableNameContext = new SecondaryTableNameSource( secondaryTable.name() );
 			joinColumns = secondaryTable.pkJoinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( secondaryTable.uniqueConstraints() );
 		}
 		else if ( joinTable != null ) {
 			schema = joinTable.schema();
 			catalog = joinTable.catalog();
 			secondaryTableNameContext = new SecondaryTableNameSource( joinTable.name() );
 			joinColumns = joinTable.joinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( joinTable.uniqueConstraints() );
 		}
 		else {
 			throw new AssertionFailure( "Both JoinTable and SecondaryTable are null" );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				secondaryTableNameContext,
 				SEC_TBL_NS_HELPER,
 				false,
 				uniqueConstraintHolders,
 				null,
 				null,
 				mappings,
 				null
 		);
 
 		//no check constraints available on joins
 		join.setTable( table );
 
 		//somehow keep joins() for later.
 		//Has to do the work later because it needs persistentClass id!
 		log.info(
 				"Adding secondary table to entity {} -> {}", persistentClass.getEntityName(), join.getTable().getName()
 		);
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null ) {
 			join.setSequentialSelect( FetchMode.JOIN != matchingTable.fetch() );
 			join.setInverse( matchingTable.inverse() );
 			join.setOptional( matchingTable.optional() );
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlInsert().sql() ) ) {
 				join.setCustomSQLInsert( matchingTable.sqlInsert().sql().trim(),
 						matchingTable.sqlInsert().callable(),
 						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlInsert().check().toString().toLowerCase() )
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlUpdate().check().toString().toLowerCase() )
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.parse( matchingTable.sqlDelete().check().toString().toLowerCase() )
 				);
 			}
 		}
 		else {
 			//default
 			join.setSequentialSelect( false );
 			join.setInverse( false );
 			join.setOptional( true ); //perhaps not quite per-spec, but a Good Thing anyway
 		}
 
 		if ( noDelayInPkColumnCreation ) {
 			createPrimaryColumnsToSecondaryTable( joinColumns, propertyHolder, join );
 		}
 		else {
 			secondaryTables.put( table.getQuotedName(), join );
 			secondaryTableJoins.put( table.getQuotedName(), joinColumns );
 		}
 		return join;
 	}
 
 	public java.util.Map<String, Join> getSecondaryTables() {
 		return secondaryTables;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegion = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ?
 					null :
 					cacheAnn.region();
 			cacheConcurrentStrategy = getCacheConcurrencyStrategy( cacheAnn.usage() );
 			if ( "all".equalsIgnoreCase( cacheAnn.include() ) ) {
 				cacheLazyProperty = true;
 			}
 			else if ( "non-lazy".equalsIgnoreCase( cacheAnn.include() ) ) {
 				cacheLazyProperty = false;
 			}
 			else {
 				throw new AnnotationException( "Unknown lazy property annotations: " + cacheAnn.include() );
 			}
 		}
 		else {
 			cacheConcurrentStrategy = null;
 			cacheRegion = null;
 			cacheLazyProperty = true;
 		}
 	}
 
 	public static String getCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
 		org.hibernate.cache.access.AccessType accessType = strategy.toAccessType();
 		return accessType == null ? null : accessType.getName();
 	}
 
 	public void addFilter(String name, String condition) {
 		filters.put( name, condition );
 	}
 
 	public void setInheritanceState(InheritanceState inheritanceState) {
 		this.inheritanceState = inheritanceState;
 	}
 
 	public boolean isIgnoreIdAnnotations() {
 		return ignoreIdAnnotations;
 	}
 
 	public void setIgnoreIdAnnotations(boolean ignoreIdAnnotations) {
 		this.ignoreIdAnnotations = ignoreIdAnnotations;
 	}
 
 	public void processComplementaryTableDefinitions(org.hibernate.annotations.Table table) {
 		//comment and index are processed here
 		if ( table == null ) return;
 		String appliedTable = table.appliesTo();
 		Iterator tables = persistentClass.getTableClosureIterator();
 		Table hibTable = null;
 		while ( tables.hasNext() ) {
 			Table pcTable = (Table) tables.next();
 			if ( pcTable.getQuotedName().equals( appliedTable ) ) {
 				//we are in the correct table to find columns
 				hibTable = pcTable;
 				break;
 			}
 			hibTable = null;
 		}
 		if ( hibTable == null ) {
 			//maybe a join/secondary table
 			for ( Join join : secondaryTables.values() ) {
 				if ( join.getTable().getQuotedName().equals( appliedTable ) ) {
 					hibTable = join.getTable();
 					break;
 				}
 			}
 		}
 		if ( hibTable == null ) {
 			throw new AnnotationException(
 					"@org.hibernate.annotations.Table references an unknown table: " + appliedTable
 			);
 		}
 		if ( !BinderHelper.isEmptyAnnotationValue( table.comment() ) ) hibTable.setComment( table.comment() );
 		TableBinder.addIndexes( hibTable, table.indexes(), mappings );
 	}
 
 	public void processComplementaryTableDefinitions(Tables tables) {
 		if ( tables == null ) return;
 		for (org.hibernate.annotations.Table table : tables.value()) {
 			processComplementaryTableDefinitions( table );
 		}
 	}
 
 	public AccessType getPropertyAccessType() {
 		return propertyAccessType;
 	}
 
 	public void setPropertyAccessType(AccessType propertyAccessor) {
 		this.propertyAccessType = getExplicitAccessType( annotatedClass );
 		// only set the access type if there is no explicit access type for this class
 		if( this.propertyAccessType == null ) {
 			this.propertyAccessType = propertyAccessor;
 		}
 	}
 
 	public AccessType getPropertyAccessor(XAnnotatedElement element) {
 		AccessType accessType = getExplicitAccessType( element );
 		if ( accessType == null ) {
 		   accessType = propertyAccessType;
 		}
 		return accessType;
 	}
 
 	public AccessType getExplicitAccessType(XAnnotatedElement element) {
 		AccessType accessType = null;
 
 		AccessType hibernateAccessType = null;
 		AccessType jpaAccessType = null;
 
 		org.hibernate.annotations.AccessType accessTypeAnnotation = element.getAnnotation( org.hibernate.annotations.AccessType.class );
 		if ( accessTypeAnnotation != null ) {
 			hibernateAccessType = AccessType.getAccessStrategy( accessTypeAnnotation.value() );
 		}
 
 		Access access = element.getAnnotation( Access.class );
 		if ( access != null ) {
 			jpaAccessType = AccessType.getAccessStrategy( access.value() );
 		}
 
 		if ( hibernateAccessType != null && jpaAccessType != null && hibernateAccessType != jpaAccessType ) {
 			throw new MappingException(
 					"Found @Access and @AccessType with conflicting values on a property in class " + annotatedClass.toString()
 			);
 		}
 
 		if ( hibernateAccessType != null ) {
 			accessType = hibernateAccessType;
 		}
 		else if ( jpaAccessType != null ) {
 			accessType = jpaAccessType;
 		}
 
 		return accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 3a7dedf62d..323bfcf835 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1313 +1,1323 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
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
 import org.hibernate.engine.jdbc.spi.SQLExceptionHelper;
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
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
-import org.hibernate.persister.PersisterFactory;
+import org.hibernate.persister.internal.PersisterFactoryImpl;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.stat.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.EmptyIterator;
 import org.hibernate.util.ReflectHelper;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
-import javax.transaction.TransactionManager;
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
  * @see org.hibernate.classic.Session
  * @see org.hibernate.hql.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactory, SessionFactoryImplementor {
 
 	private static final Logger log = LoggerFactory.getLogger(SessionFactoryImpl.class);
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
 	private final transient ServiceRegistry serviceRegistry;
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
 	private final transient SessionFactoryObserver observer;
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
 		log.info("building session factory");
 
 		this.statistics = new ConcurrentStatisticsImpl( this );
 		getStatistics().setStatisticsEnabled( settings.isStatisticsEnabled() );
 		log.debug( "Statistics initialized [enabled={}]}", settings.isStatisticsEnabled() );
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 		this.interceptor = cfg.getInterceptor();
 		this.serviceRegistry = serviceRegistry;
 		this.settings = settings;
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
         this.eventListeners = listeners;
 		this.observer = observer != null ? observer : new SessionFactoryObserver() {
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 			public void sessionFactoryClosed(SessionFactory factory) {
 			}
 		};
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		if ( log.isDebugEnabled() ) {
 			log.debug("Session factory constructed with filter configurations : " + filters);
 		}
 
 		if ( log.isDebugEnabled() ) {
 			log.debug(
 					"instantiating session factory with properties: " + properties
 			);
 		}
 
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
 					log.trace( "Building cache for entity data [" + model.getEntityName() + "]" );
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
-			EntityPersister cp = PersisterFactory.createClassPersister( model, accessStrategy, this, mapping );
+			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
+					model,
+					accessStrategy,
+					this,
+					mapping
+			);
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
 				log.trace( "Building cache for collection data [" + model.getRole() + "]" );
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
-			CollectionPersister persister = PersisterFactory.createCollectionPersister( cfg, model, accessStrategy, this) ;
+			CollectionPersister persister = serviceRegistry.getService( PersisterFactory.class ).createCollectionPersister(
+					cfg,
+					model,
+					accessStrategy,
+					this
+			) ;
 			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = ( Set ) tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = ( Set ) tmpEntityToCollectionRoleMap.get( entityName );
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
 
 		log.debug("instantiated session factory");
 
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
 					if ( iterator.hasNext() ) {
 						failingQueries.append( ", " );
 					}
 					log.error( "Error in named query: " + queryName, e );
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
 		log.debug("Checking " + namedQueries.size() + " named HQL queries");
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				log.debug("Checking named query: " + queryName);
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
 
 		log.debug("Checking " + namedSqlQueries.size() + " named SQL queries");
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				log.debug("Checking named SQL query: " + queryName);
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
 
 	public org.hibernate.classic.Session openSession(Connection connection, Interceptor sessionLocalInterceptor) {
 		return openSession(connection, false, Long.MIN_VALUE, sessionLocalInterceptor);
 	}
 
 	public org.hibernate.classic.Session openSession(Interceptor sessionLocalInterceptor)
 	throws HibernateException {
 		// note that this timestamp is not correct if the connection provider
 		// returns an older JDBC connection that was associated with a
 		// transaction that was already begun before openSession() was called
 		// (don't know any possible solution to this!)
 		long timestamp = settings.getRegionFactory().nextTimestamp();
 		return openSession( null, true, timestamp, sessionLocalInterceptor );
 	}
 
 	public org.hibernate.classic.Session openSession(Connection connection) {
 		return openSession(connection, interceptor); //prevents this session from adding things to cache
 	}
 
 	public org.hibernate.classic.Session openSession() throws HibernateException {
 		return openSession(interceptor);
 	}
 
 	public org.hibernate.classic.Session openTemporarySession() throws HibernateException {
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
 
 	public org.hibernate.classic.Session openSession(
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
 
 	public org.hibernate.classic.Session getCurrentSession() throws HibernateException {
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
 
 	public SQLExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	// from javax.naming.Referenceable
 	public Reference getReference() throws NamingException {
 		log.debug("Returning a Reference to the SessionFactory");
 		return new Reference(
 			SessionFactoryImpl.class.getName(),
 		    new StringRefAddr("uuid", uuid),
 		    SessionFactoryObjectFactory.class.getName(),
 		    null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
 		log.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryObjectFactory.getInstance(uuid);
 		if (result==null) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryObjectFactory.getNamedInstance(name);
 			if (result==null) {
 				throw new InvalidObjectException("Could not find a SessionFactory named: " + name);
 			}
 			else {
 				log.debug("resolved SessionFactory by name");
 			}
 		}
 		else {
 			log.debug("resolved SessionFactory by uid");
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
 		log.trace("deserializing");
 		in.defaultReadObject();
 		log.debug("deserialized: " + uuid);
 	}
 
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		log.debug("serializing: " + uuid);
 		out.defaultWriteObject();
 		log.trace("serialized");
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
 		return (ClassMetadata) classMetadata.get(entityName);
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
 				ReflectHelper.classForName(className);
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
 	 * Note: Be aware that the sessionfactory instance still can
 	 * be a "heavy" object memory wise after close() has been called.  Thus
 	 * it is important to not keep referencing the instance to let the garbage
 	 * collector release the memory.
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			log.trace( "already closed" );
 			return;
 		}
 
 		log.info("closing");
 
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
 				if ( log.isDebugEnabled() ) {
 					log.debug( 
 							"evicting second-level cache: " +
 									MessageHelper.infoString( p, identifier, SessionFactoryImpl.this )
 					);
 				}
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
 				if ( log.isDebugEnabled() ) {
 					log.debug( "evicting second-level cache: " + p.getEntityName() );
 				}
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
 				if ( log.isDebugEnabled() ) {
 					log.debug(
 							"evicting second-level cache: " +
 									MessageHelper.collectionInfoString(p, ownerIdentifier, SessionFactoryImpl.this)
 					);
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
 					EntityMode.POJO,
 					SessionFactoryImpl.this
 			);
 		}
 
 		public void evictCollectionRegion(String role) {
 			CollectionPersister p = getCollectionPersister( role );
 			if ( p.hasCache() ) {
 				if ( log.isDebugEnabled() ) {
 					log.debug( "evicting second-level cache: " + p.getRole() );
 				}
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
 			if ( regionName == null ) {
 				throw new NullPointerException(
 						"Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)"
 				);
 			}
 			else {
 				if ( settings.isQueryCacheEnabled() ) {
 					QueryCache namedQueryCache = queryCaches.get( regionName );
 					if ( namedQueryCache != null ) {
 						namedQueryCache.clear();
 						// TODO : cleanup entries in queryCaches + allCacheRegions ?
 					}
 				}
 			}
 		}
 
 		public void evictQueryRegions() {
 			for ( QueryCache queryCache : queryCaches.values() ) {
 				queryCache.clear();
 				// TODO : cleanup entries in queryCaches + allCacheRegions ?
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
 				log.warn( "JTASessionContext being used with JdbcTransactionFactory; auto-flush will not operate correctly with getCurrentSession()" );
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
 				log.error( "Unable to construct current session context [" + impl + "]", t );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/PersisterClassProvider.java b/hibernate-core/src/main/java/org/hibernate/persister/PersisterClassProvider.java
deleted file mode 100644
index 029c4d0370..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/persister/PersisterClassProvider.java
+++ /dev/null
@@ -1,47 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * JBoss, Home of Professional Open Source
- * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
- * as indicated by the @authors tag. All rights reserved.
- * See the copyright.txt in the distribution for a
- * full listing of individual contributors.
- *
- * This copyrighted material is made available to anyone wishing to use,
- * modify, copy, or redistribute it subject to the terms and conditions
- * of the GNU Lesser General Public License, v. 2.1.
- * This program is distributed in the hope that it will be useful, but WITHOUT A
- * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
- * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
- * You should have received a copy of the GNU Lesser General Public License,
- * v.2.1 along with this distribution; if not, write to the Free Software
- * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
- * MA  02110-1301, USA.
- */
-package org.hibernate.persister;
-
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.entity.EntityPersister;
-
-/**
- * Provides persister classes based on the entity or collection role.
- * The persister class is chosen according to the following rules in decreasing priority:
- *  - the persister class defined explicitly via annotation or XML
- *  - the persister class returned by the PersisterClassProvider implementation (if not null)
- *  - the default provider as chosen by Hibernate Core (best choice most of the time)
- *
- * @author Emmanuel Bernard <emmanuel@hibernate.org>
- */
-public interface PersisterClassProvider {
-	/**
-	 * Returns the entity persister class for a given entityName or null
-	 * if the entity persister class should be the default.
-	 */
-	Class<? extends EntityPersister> getEntityPersisterClass(String entityName);
-
-	/**
-	 * Returns the collection persister class for a given collection role or null
-	 * if the collection persister class should be the default.
-	 */
-	Class<? extends CollectionPersister> getCollectionPersisterClass(String collectionPersister);
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
deleted file mode 100644
index e62c62f940..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
+++ /dev/null
@@ -1,177 +0,0 @@
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
-package org.hibernate.persister;
-
-import java.lang.reflect.Constructor;
-import java.lang.reflect.InvocationTargetException;
-
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.cache.access.EntityRegionAccessStrategy;
-import org.hibernate.cache.access.CollectionRegionAccessStrategy;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.engine.Mapping;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.mapping.Collection;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.persister.collection.BasicCollectionPersister;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.collection.OneToManyPersister;
-import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
-import org.hibernate.persister.entity.SingleTableEntityPersister;
-import org.hibernate.persister.entity.UnionSubclassEntityPersister;
-
-/**
- * Factory for <tt>EntityPersister</tt> and <tt>CollectionPersister</tt> instances
- *
- * @author Gavin King
- */
-public final class PersisterFactory {
-
-	//TODO: make EntityPersister *not* depend on SessionFactoryImplementor
-	//interface, if possible
-
-	// TODO : still need to make CollectionPersisters EntityMode-aware
-
-	private PersisterFactory() {}
-
-	private static final Class[] PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
-		PersistentClass.class, EntityRegionAccessStrategy.class, SessionFactoryImplementor.class, Mapping.class
-	};
-
-	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ? Should it not be enough with associated class ?
-	// or why does EntityPersister's not get access to configuration ?
-	//
-	// The only reason I could see that Configuration gets passed to collection persisters
-	// is so that they can look up the dom4j node name of the entity element in case
-	// no explicit node name was applied at the collection element level.  Are you kidding me?
-	// Trivial to fix then.  Just store and expose the node name on the entity persister
-	// (which the collection persister looks up anyway via other means...).
-	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
-			Collection.class, CollectionRegionAccessStrategy.class, Configuration.class, SessionFactoryImplementor.class
-		};
-
-	public static EntityPersister createClassPersister(
-			PersistentClass model,
-			EntityRegionAccessStrategy cacheAccessStrategy,
-			SessionFactoryImplementor factory,
-			Mapping cfg) throws HibernateException {
-		Class persisterClass = model.getEntityPersisterClass();
-		if ( persisterClass == null || persisterClass == SingleTableEntityPersister.class ) {
-			return new SingleTableEntityPersister( model, cacheAccessStrategy, factory, cfg );
-		}
-		else if ( persisterClass == JoinedSubclassEntityPersister.class ) {
-			return new JoinedSubclassEntityPersister( model, cacheAccessStrategy, factory, cfg );
-		}
-		else if ( persisterClass == UnionSubclassEntityPersister.class ) {
-			return new UnionSubclassEntityPersister( model, cacheAccessStrategy, factory, cfg );
-		}
-		else {
-			return create( persisterClass, model, cacheAccessStrategy, factory, cfg );
-		}
-	}
-
-	public static CollectionPersister createCollectionPersister(
-			Configuration cfg,
-			Collection model,
-			CollectionRegionAccessStrategy cacheAccessStrategy,
-			SessionFactoryImplementor factory) throws HibernateException {
-		Class persisterClass = model.getCollectionPersisterClass();
-		if ( persisterClass == null ) {
-			return model.isOneToMany()
-					? ( CollectionPersister ) new OneToManyPersister( model, cacheAccessStrategy, cfg, factory )
-					: ( CollectionPersister ) new BasicCollectionPersister( model, cacheAccessStrategy, cfg, factory );
-		}
-		else {
-			return create( persisterClass, cfg, model, cacheAccessStrategy, factory );
-		}
-
-	}
-
-	private static EntityPersister create(
-			Class persisterClass, 
-			PersistentClass model, 
-			EntityRegionAccessStrategy cacheAccessStrategy,
-			SessionFactoryImplementor factory,
-			Mapping cfg) throws HibernateException {
-		Constructor pc;
-		try {
-			pc = persisterClass.getConstructor( PERSISTER_CONSTRUCTOR_ARGS );
-		}
-		catch ( Exception e ) {
-			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
-		}
-
-		try {
-			return (EntityPersister) pc.newInstance( new Object[] { model, cacheAccessStrategy, factory, cfg } );
-		}
-		catch (InvocationTargetException ite) {
-			Throwable e = ite.getTargetException();
-			if (e instanceof HibernateException) {
-				throw (HibernateException) e;
-			}
-			else {
-				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
-			}
-		}
-		catch (Exception e) {
-			throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
-		}
-	}
-
-	private static CollectionPersister create(
-			Class persisterClass,
-			Configuration cfg,
-			Collection model,
-			CollectionRegionAccessStrategy cacheAccessStrategy,
-			SessionFactoryImplementor factory) throws HibernateException {
-		Constructor pc;
-		try {
-			pc = persisterClass.getConstructor( COLLECTION_PERSISTER_CONSTRUCTOR_ARGS );
-		}
-		catch (Exception e) {
-			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
-		}
-
-		try {
-			return (CollectionPersister) pc.newInstance( new Object[] { model, cacheAccessStrategy, cfg, factory } );
-		}
-		catch (InvocationTargetException ite) {
-			Throwable e = ite.getTargetException();
-			if (e instanceof HibernateException) {
-				throw (HibernateException) e;
-			}
-			else {
-				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
-			}
-		}
-		catch (Exception e) {
-			throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
-		}
-	}
-
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java
new file mode 100644
index 0000000000..f61b48fd6a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java
@@ -0,0 +1,69 @@
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
+package org.hibernate.persister.internal;
+
+import org.hibernate.persister.spi.PersisterClassResolver;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.spi.ServiceException;
+import org.hibernate.service.spi.ServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistry;
+
+import java.util.Map;
+
+/**
+ * @author Steve Ebersole
+ */
+public class PersisterClassResolverInitiator implements ServiceInitiator<PersisterClassResolver> {
+	public static final PersisterClassResolverInitiator INSTANCE = new PersisterClassResolverInitiator();
+	public static final String IMPL_NAME = "hibernate.persister.resolver";
+
+	@Override
+	public Class<PersisterClassResolver> getServiceInitiated() {
+		return PersisterClassResolver.class;
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public PersisterClassResolver initiateService(Map configurationValues, ServiceRegistry registry) {
+		final Object customImpl = configurationValues.get( IMPL_NAME );
+		if ( customImpl == null ) {
+			return new StandardPersisterClassResolver();
+		}
+
+		if ( PersisterClassResolver.class.isInstance( customImpl ) ) {
+			return (PersisterClassResolver) customImpl;
+		}
+
+		final Class<? extends PersisterClassResolver> customImplClass = Class.class.isInstance( customImpl )
+				? (Class<? extends PersisterClassResolver>) customImpl
+				: registry.getService( ClassLoaderService.class ).classForName( customImpl.toString() );
+
+		try {
+			return customImplClass.newInstance();
+		}
+		catch (Exception e) {
+			throw new ServiceException( "Could not initialize custom PersisterClassResolver impl [" + customImplClass.getName() + "]", e );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
new file mode 100644
index 0000000000..c56523a9b9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -0,0 +1,183 @@
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
+package org.hibernate.persister.internal;
+
+import org.hibernate.HibernateException;
+import org.hibernate.MappingException;
+import org.hibernate.cache.access.CollectionRegionAccessStrategy;
+import org.hibernate.cache.access.EntityRegionAccessStrategy;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.Mapping;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.mapping.Collection;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.spi.PersisterClassResolver;
+import org.hibernate.persister.spi.PersisterFactory;
+import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.ServiceRegistryAwareService;
+
+import java.lang.reflect.Constructor;
+import java.lang.reflect.InvocationTargetException;
+
+/**
+ * The standard Hibernate {@link PersisterFactory} implementation
+ *
+ * @author Gavin King
+ * @author Steve Ebersole
+ */
+public final class PersisterFactoryImpl implements PersisterFactory, ServiceRegistryAwareService {
+
+	/**
+	 * The constructor signature for {@link EntityPersister} implementations
+	 *
+	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
+	 */
+	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
+			PersistentClass.class,
+			EntityRegionAccessStrategy.class,
+			SessionFactoryImplementor.class,
+			Mapping.class
+	};
+
+	/**
+	 * The constructor signature for {@link CollectionPersister} implementations
+	 *
+	 * @todo still need to make collection persisters EntityMode-aware
+	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
+	 */
+	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
+			Collection.class,
+			CollectionRegionAccessStrategy.class,
+			Configuration.class,
+			SessionFactoryImplementor.class
+	};
+
+	private ServiceRegistry serviceRegistry;
+
+	@Override
+	public void injectServices(ServiceRegistry serviceRegistry) {
+		this.serviceRegistry = serviceRegistry;
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public EntityPersister createEntityPersister(
+			PersistentClass metadata,
+			EntityRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory,
+			Mapping cfg) {
+		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
+		if ( persisterClass == null ) {
+			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
+		}
+		return create( persisterClass, metadata, cacheAccessStrategy, factory, cfg );
+	}
+
+	private static EntityPersister create(
+			Class<? extends EntityPersister> persisterClass,
+			PersistentClass metadata,
+			EntityRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory,
+			Mapping cfg) throws HibernateException {
+		try {
+			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( ENTITY_PERSISTER_CONSTRUCTOR_ARGS );
+			try {
+				return constructor.newInstance( metadata, cacheAccessStrategy, factory, cfg );
+			}
+			catch (MappingException e) {
+				throw e;
+			}
+			catch (InvocationTargetException e) {
+				Throwable target = e.getTargetException();
+				if ( target instanceof HibernateException ) {
+					throw (HibernateException) target;
+				}
+				else {
+					throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), target );
+				}
+			}
+			catch (Exception e) {
+				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
+			}
+		}
+		catch (MappingException e) {
+			throw e;
+		}
+		catch (Exception e) {
+			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
+		}
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public CollectionPersister createCollectionPersister(
+			Configuration cfg,
+			Collection metadata,
+			CollectionRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory) throws HibernateException {
+		Class<? extends CollectionPersister> persisterClass = metadata.getCollectionPersisterClass();
+		if ( persisterClass == null ) {
+			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( metadata );
+		}
+
+		return create( persisterClass, cfg, metadata, cacheAccessStrategy, factory );
+	}
+
+	private static CollectionPersister create(
+			Class<? extends CollectionPersister> persisterClass,
+			Configuration cfg,
+			Collection metadata,
+			CollectionRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory) throws HibernateException {
+		try {
+			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( COLLECTION_PERSISTER_CONSTRUCTOR_ARGS );
+			try {
+				return constructor.newInstance( metadata, cacheAccessStrategy, cfg, factory );
+			}
+			catch (MappingException e) {
+				throw e;
+			}
+			catch (InvocationTargetException e) {
+				Throwable target = e.getTargetException();
+				if ( target instanceof HibernateException ) {
+					throw (HibernateException) target;
+				}
+				else {
+					throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), target );
+				}
+			}
+			catch (Exception e) {
+				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
+			}
+		}
+		catch (MappingException e) {
+			throw e;
+		}
+		catch (Exception e) {
+			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java
new file mode 100644
index 0000000000..73df183a37
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java
@@ -0,0 +1,69 @@
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
+package org.hibernate.persister.internal;
+
+import org.hibernate.persister.spi.PersisterFactory;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.spi.ServiceException;
+import org.hibernate.service.spi.ServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistry;
+
+import java.util.Map;
+
+/**
+ * @author Steve Ebersole
+ */
+public class PersisterFactoryInitiator implements ServiceInitiator<PersisterFactory> {
+	public static final PersisterFactoryInitiator INSTANCE = new PersisterFactoryInitiator();
+
+	public static final String IMPL_NAME = "hibernate.persister.factory";
+
+	@Override
+	public Class<PersisterFactory> getServiceInitiated() {
+		return PersisterFactory.class;
+	}
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public PersisterFactory initiateService(Map configurationValues, ServiceRegistry registry) {
+		final Object customImpl = configurationValues.get( IMPL_NAME );
+		if ( customImpl == null ) {
+			return new PersisterFactoryImpl();
+		}
+
+		if ( PersisterFactory.class.isInstance( customImpl ) ) {
+			return (PersisterFactory) customImpl;
+		}
+
+		final Class<? extends PersisterFactory> customImplClass = Class.class.isInstance( customImpl )
+				? ( Class<? extends PersisterFactory> ) customImpl
+				: registry.getService( ClassLoaderService.class ).classForName( customImpl.toString() );
+		try {
+			return customImplClass.newInstance();
+		}
+		catch (Exception e) {
+			throw new ServiceException( "Could not initialize custom PersisterFactory impl [" + customImplClass.getName() + "]", e );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
new file mode 100644
index 0000000000..6261b85578
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
@@ -0,0 +1,88 @@
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
+package org.hibernate.persister.internal;
+
+import org.hibernate.mapping.Collection;
+import org.hibernate.mapping.JoinedSubclass;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.mapping.RootClass;
+import org.hibernate.mapping.UnionSubclass;
+import org.hibernate.persister.collection.BasicCollectionPersister;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.collection.OneToManyPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
+import org.hibernate.persister.entity.SingleTableEntityPersister;
+import org.hibernate.persister.entity.UnionSubclassEntityPersister;
+import org.hibernate.persister.spi.PersisterClassResolver;
+import org.hibernate.persister.spi.UnknownPersisterException;
+
+/**
+ * @author Steve Ebersole
+ */
+public class StandardPersisterClassResolver implements PersisterClassResolver {
+	@Override
+	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
+		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
+		if ( RootClass.class.isInstance( metadata ) ) {
+			return singleTableEntityPersister();
+		}
+		else if ( JoinedSubclass.class.isInstance( metadata ) ) {
+			return joinedSubclassEntityPersister();
+		}
+		else if ( UnionSubclass.class.isInstance( metadata ) ) {
+			return unionSubclassEntityPersister();
+		}
+		else {
+			throw new UnknownPersisterException(
+					"Could not determine persister implementation for entity [" + metadata.getEntityName() + "]"
+			);
+		}
+	}
+
+	public Class<? extends EntityPersister> singleTableEntityPersister() {
+		return SingleTableEntityPersister.class;
+	}
+
+	public Class<? extends EntityPersister> joinedSubclassEntityPersister() {
+		return JoinedSubclassEntityPersister.class;
+	}
+
+	public Class<? extends EntityPersister> unionSubclassEntityPersister() {
+		return UnionSubclassEntityPersister.class;
+	}
+
+	@Override
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
+		return metadata.isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
+	}
+
+	private Class<OneToManyPersister> oneToManyPersister() {
+		return OneToManyPersister.class;
+	}
+
+	private Class<BasicCollectionPersister> basicCollectionPersister() {
+		return BasicCollectionPersister.class;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
new file mode 100644
index 0000000000..1137d12333
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
@@ -0,0 +1,54 @@
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
+package org.hibernate.persister.spi;
+
+import org.hibernate.mapping.Collection;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.spi.Service;
+
+/**
+ * Provides persister classes based on the entity or collection role.
+ * The persister class is chosen according to the following rules in decreasing priority:
+ *  - the persister class defined explicitly via annotation or XML
+ *  - the persister class returned by the PersisterClassResolver implementation (if not null)
+ *  - the default provider as chosen by Hibernate Core (best choice most of the time)
+ *
+ * @author Emmanuel Bernard <emmanuel@hibernate.org>
+ * @author Steve Ebersole
+ */
+public interface PersisterClassResolver extends Service {
+	/**
+	 * Returns the entity persister class for a given entityName or null
+	 * if the entity persister class should be the default.
+	 */
+	Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata);
+
+	/**
+	 * Returns the collection persister class for a given collection role or null
+	 * if the collection persister class should be the default.
+	 */
+	Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
new file mode 100644
index 0000000000..1108fa1752
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
@@ -0,0 +1,89 @@
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
+package org.hibernate.persister.spi;
+
+import org.hibernate.HibernateException;
+import org.hibernate.cache.access.CollectionRegionAccessStrategy;
+import org.hibernate.cache.access.EntityRegionAccessStrategy;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.Mapping;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.mapping.Collection;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.spi.Service;
+
+/**
+ * Contract for creating persister instances (both {@link EntityPersister} and {@link } varieties).
+ *
+ * @author Steve Ebersole
+ */
+public interface PersisterFactory extends Service {
+
+	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
+	// Should it not be enough with associated class ? or why does EntityPersister's not get access to configuration ?
+	//
+	// The only reason I could see that Configuration gets passed to collection persisters
+	// is so that they can look up the dom4j node name of the entity element in case
+	// no explicit node name was applied at the collection element level.  Are you kidding me?
+	// Trivial to fix then.  Just store and expose the node name on the entity persister
+	// (which the collection persister looks up anyway via other means...).
+
+	/**
+	 * Create an entity persister instance.
+	 *
+	 * @param model The O/R mapping metamodel definition for the entity
+	 * @param cacheAccessStrategy The caching strategy for this entity
+	 * @param factory The session factory
+	 * @param cfg The overall mapping
+	 *
+	 * @return An appropriate entity persister instance.
+	 *
+	 * @throws HibernateException Indicates a problem building the persister.
+	 */
+	public EntityPersister createEntityPersister(
+			PersistentClass model,
+			EntityRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory,
+			Mapping cfg) throws HibernateException;
+
+	/**
+	 * Create a collection persister instance.
+	 *
+	 * @param cfg The configuration
+	 * @param model The O/R mapping metamodel definition for the collection
+	 * @param cacheAccessStrategy The caching strategy for this collection
+	 * @param factory The session factory
+	 *
+	 * @return An appropriate collection persister instance.
+	 *
+	 * @throws HibernateException Indicates a problem building the persister.
+	 */
+	public CollectionPersister createCollectionPersister(
+			Configuration cfg,
+			Collection model,
+			CollectionRegionAccessStrategy cacheAccessStrategy,
+			SessionFactoryImplementor factory) throws HibernateException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/UnknownPersisterException.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/UnknownPersisterException.java
new file mode 100644
index 0000000000..f5b67d6e84
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/UnknownPersisterException.java
@@ -0,0 +1,41 @@
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
+package org.hibernate.persister.spi;
+
+import org.hibernate.HibernateException;
+
+/**
+ * Indicates that the persister to use is not known and couyld not be determined.
+ * 
+ * @author Steve Ebersole
+ */
+public class UnknownPersisterException extends HibernateException {
+	public UnknownPersisterException(String s) {
+		super( s );
+	}
+
+	public UnknownPersisterException(String string, Throwable root) {
+		super( string, root );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
index a6b8a545e7..f3d2eba700 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
@@ -1,64 +1,69 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
+import org.hibernate.persister.internal.PersisterClassResolverInitiator;
+import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 import java.util.ArrayList;
 import java.util.List;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardServiceInitiators {
 	public static List<ServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<ServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<ServiceInitiator> serviceInitiators = new ArrayList<ServiceInitiator>();
 
 		serviceInitiators.add( ClassLoaderServiceInitiator.INSTANCE );
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
+		serviceInitiators.add( PersisterClassResolverInitiator.INSTANCE );
+		serviceInitiators.add( PersisterFactoryInitiator.INSTANCE );
+
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 2cdcb817c2..9c09506736 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,697 +1,700 @@
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
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
+import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
-import org.hibernate.persister.PersisterClassProvider;
+import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
-public class GoofyPersisterClassProvider implements PersisterClassProvider {
-	public Class<? extends EntityPersister> getEntityPersisterClass(String entityName) {
+public class GoofyPersisterClassProvider implements PersisterClassResolver {
+	@Override
+	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
-	public Class<? extends CollectionPersister> getCollectionPersisterClass(String collectionPersister) {
+	@Override
+	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.access.EntityRegionAccessStrategy strategy,
 								   org.hibernate.engine.SessionFactoryImplementor sf,
 								   org.hibernate.engine.Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRootEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMetamodel getEntityMetamodel() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSubclassEntityName(String entityName) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasProxy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasMutableProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasSubselectLoadableCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCascades() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInherited() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isIdentifierAssignedByInsert() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIdentifierProperty() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean canExtractIdOutOfEntity() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Comparator getVersionComparator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public VersionType getVersionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getVersionProperty() {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasNaturalIdentifier() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasLazyProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type[] getPropertyTypes() {
 			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getPropertyNames() {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierPropertyName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCacheInvalidationRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazyPropertiesCacheable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ClassMetadata getClassMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isBatchLoadable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMode guessEntityMode(Object object) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstrumented(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasInsertGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUpdateGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersionPropertyGenerated() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterReassociate(Object entity, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 				throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getMappedClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsLifecycle(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsValidatable(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getConcreteProxyClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstance(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   org.hibernate.engine.SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionType getCollectionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
index d94a8952ba..46211c597c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
@@ -1,82 +1,81 @@
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
+import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit.UnitTestCase;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends UnitTestCase {
 	public PersisterClassProviderTest(String string) {
 		super( string );
 	}
 
 	public void testPersisterClassProvider() throws Exception {
 
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		ServiceRegistry serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		//no exception as the GoofyPersisterClassProvider is not set
 		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		sessionFactory.close();
 
-
+		serviceRegistry.registerService( PersisterClassResolver.class, new GoofyPersisterClassProvider() );
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
-		cfg.setPersisterClassProvider( new GoofyPersisterClassProvider() );
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
-		cfg.setPersisterClassProvider( new GoofyPersisterClassProvider() );
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AvailableSettings.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AvailableSettings.java
index 92c64df0ad..e53a801690 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AvailableSettings.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AvailableSettings.java
@@ -1,314 +1,314 @@
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
 package org.hibernate.ejb;
 
 /**
  * Defines the available HEM settings, both JPA-defined as well as Hibernate-specific
  * <p/>
  * NOTE : Does *not* include {@link org.hibernate.cfg.Environment} values.
  *
  * @author Steve Ebersole
  */
 public class AvailableSettings {
 
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
 	 * Naming strategy class name, the class has to have a no-arg constructor
 	 */
 	public static final String NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
 
 	/**
-	 * PersisterClassProvider class name, the class has to gave a no-arg constructor
+	 * PersisterClassResolver class name, the class has to gave a no-arg constructor
 	 */
 	public static final String PERSISTER_CLASS_PROVIDER = "hibernate.ejb.persister_class_provider";
 
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
 	 * List of classes names
 	 * Internal use only
 	 */
 	public static final String XML_FILE_NAMES = "hibernate.ejb.xml_files";
 	public static final String HBXML_FILES = "hibernate.hbmxml.files";
 	public static final String LOADED_CLASSES = "hibernate.ejb.loaded.classes";
 	public static final String JACC_CONTEXT_ID = "hibernate.jacc.ctx.id";
 	public static final String JACC_PREFIX = "hibernate.jacc";
 	public static final String JACC_ENABLED = "hibernate.jacc.enabled";
 	public static final String PERSISTENCE_UNIT_NAME = "hibernate.ejb.persistenceUnitName";
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index 07eaa01b90..56a882f1d7 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1659 +1,1641 @@
 // $Id$
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
 
 import org.dom4j.Element;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.AnnotationConfiguration;
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
 import org.hibernate.ejb.transaction.JoinableCMTTransactionFactory;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LogHelper;
 import org.hibernate.ejb.util.NamingHelper;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.event.EventListeners;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.PersistentClass;
-import org.hibernate.persister.PersisterClassProvider;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.JACCConfiguration;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 import org.hibernate.util.xml.MappingReader;
 import org.hibernate.util.xml.OriginImpl;
 import org.hibernate.util.xml.XmlDocument;
 
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
 	private final Logger log = LoggerFactory.getLogger( Ejb3Configuration.class );
 	private static final String IMPLEMENTATION_NAME = HibernatePersistence.class.getName();
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 	private static final String PARSED_MAPPING_DOMS = "hibernate.internal.mapping_doms";
 
 	private static EntityNotFoundDelegate ejb3EntityNotFoundDelegate = new Ejb3EntityNotFoundDelegate();
 	private static Configuration DEFAULT_CONFIGURATION = new AnnotationConfiguration();
 
 	private static class Ejb3EntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);
 		}
 	}
 
 	static {
 		Version.touch();
 	}
 
 	private String persistenceUnitName;
 	private String cfgXmlResource;
 
 	private AnnotationConfiguration cfg;
 	private final Map connectionProviderInjectionData;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient EventListenerConfigurator listenerConfigurator;
 	private PersistenceUnitTransactionType transactionType;
 	private boolean discardOnClose;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient ClassLoader overridenClassLoader;
 	private boolean isConfigurationProcessed = false;
 
 
 	public Ejb3Configuration() {
 		connectionProviderInjectionData = new HashMap();
 		cfg = new AnnotationConfiguration();
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
 			Map cpInjection = new HashMap();
 			cpInjection.put( "dataSource", ds );
 			connectionProviderInjectionData.put( ConnectionProviderInitiator.INJECTION_DATA, cpInjection );
 			this.setProperty( Environment.CONNECTION_PROVIDER, InjectedDataSourceConnectionProvider.class.getName() );
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
 		log.debug( "Creating Factory: {}", metadata.getName() );
 
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
 	 * the peristence.xml file.
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
 			log.debug( "Look up for persistence unit: {}", persistenceUnitName );
 			integration = integration == null ?
 					CollectionHelper.EMPTY_MAP :
 					Collections.unmodifiableMap( integration );
 			Enumeration<URL> xmls = Thread.currentThread()
 					.getContextClassLoader()
 					.getResources( "META-INF/persistence.xml" );
 			if ( ! xmls.hasMoreElements() ) {
 				log.info( "Could not find any META-INF/persistence.xml file in the classpath");
 			}
 			while ( xmls.hasMoreElements() ) {
 				URL url = xmls.nextElement();
 				log.trace( "Analysing persistence.xml: {}", url );
 				List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader.deploy(
 						url,
 						integration,
 						cfg.getEntityResolver(),
 						PersistenceUnitTransactionType.RESOURCE_LOCAL );
 				for ( PersistenceMetadata metadata : metadataFiles ) {
 					log.trace( "{}", metadata );
 
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
 	 * @return The configured EJB3Configurartion object
 	 *
 	 * @see HibernatePersistence#createContainerEntityManagerFactory
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceUnitInfo info, Map integration) {
 		if ( log.isDebugEnabled() ) {
 			log.debug( "Processing {}", LogHelper.logPersistenceUnitInfo( info ) );
 		}
 		else {
 			log.info( "Processing PersistenceUnitInfo [\n\tname: {}\n\t...]", info.getPersistenceUnitName() );
 		}
 
 		// Spec says the passed map may be null, so handle that to make further processing easier...
 		integration = integration != null ? Collections.unmodifiableMap( integration ) : CollectionHelper.EMPTY_MAP;
 
 		// See if we (Hibernate) are the persistence provider
 		String provider = (String) integration.get( AvailableSettings.PROVIDER );
 		if ( provider == null ) {
 			provider = info.getPersistenceProviderClassName();
 		}
 		if ( provider != null && ! provider.trim().startsWith( IMPLEMENTATION_NAME ) ) {
 			log.info( "Required a different provider: {}", provider );
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
 			log.warn(
 					"Persistence provider caller does not implement the EJB3 spec correctly." +
 							"PersistenceUnitInfo.getNewTempClassLoader() is null."
 			);
 			return;
 		}
 		for ( final String xmlFile : xmlFiles ) {
 			final InputStream fileInputStream = classLoaderToUse.getResourceAsStream( xmlFile );
 			if ( fileInputStream == null ) {
 				log.info( "Unable to resolve mapping file [{}]", xmlFile );
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
 					log.warn( "Could not close input stream", ioe );
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
 		log.debug( "Detect class: {}; detect hbm: {}", detectClasses, detectHbm );
 		context.detectClasses( detectClasses ).detectHbmFiles( detectHbm );
 	}
 
 	private void scanForClasses(ScanningContext scanningContext, List<String> packages, List<String> entities, List<NamedInputStream> hbmFiles) {
 		if (scanningContext.url == null) {
 			log.error( "Container is providing a null PersistenceUnitRootUrl: discovery impossible");
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
 	public EntityManagerFactory createEntityManagerFactory(Map workingVars) {
 		Properties props = new Properties();
 		if ( workingVars != null ) {
 			props.putAll( workingVars );
 			//remove huge non String elements for a clean props
 			props.remove( AvailableSettings.CLASS_NAMES );
 			props.remove( AvailableSettings.PACKAGE_NAMES );
 			props.remove( AvailableSettings.HBXML_FILES );
 			props.remove( AvailableSettings.LOADED_CLASSES );
 		}
 		configure( props, workingVars );
 		return buildEntityManagerFactory();
 	}
 
 	/**
 	 * Process configuration and build an EntityManagerFactory <b>when</b> the configuration is ready
 	 * @deprecated
 	 */
 	public EntityManagerFactory createEntityManagerFactory() {
 		configure( cfg.getProperties(), new HashMap() );
 		return buildEntityManagerFactory();
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory() {
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
 					connectionProviderInjectionData
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
 				Class interceptorClass = ReflectHelper.classForName( sessionInterceptorClassname, Ejb3Configuration.class );
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
 		else {
 			return null;
 		}
 	}
 
 	public Reference getReference() throws NamingException {
 		log.debug("Returning a Reference to the Ejb3Configuration");
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
 
-		final PersisterClassProvider persisterClassProvider = instantiateCustomClassFromConfiguration(
-				preparedProperties,
-				null,
-				cfg.getPersisterClassProvider(),
-				AvailableSettings.PERSISTER_CLASS_PROVIDER,
-				"persister class provider",
-				PersisterClassProvider.class
-		);
-		if ( persisterClassProvider != null ) {
-			cfg.setPersisterClassProvider( persisterClassProvider );
-		}
-
 		if ( jaccKeys.size() > 0 ) {
 			addSecurity( jaccKeys, preparedProperties, workingVars );
 		}
 
 		//initialize listeners
 		listenerConfigurator.setProperties( preparedProperties );
 		listenerConfigurator.configure();
 
 		//some spec compliance checking
 		//TODO centralize that?
 		if ( ! "true".equalsIgnoreCase( cfg.getProperty( Environment.AUTOCOMMIT ) ) ) {
 			log.warn( "{} = false break the EJB3 specification", Environment.AUTOCOMMIT );
 		}
 		discardOnClose = preparedProperties.getProperty( AvailableSettings.DISCARD_PC_ON_CLOSE )
 				.equals( "true" );
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
 				Class<T> clazz = (Class<T>) classForName( className );
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
 			}
 			catch (ClassCastException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + classDescription + " class does not implement " + objectClass + " interface: "
 								+ className, e
 				);
 			}
 		}
 		return null;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void addClassesToSessionFactory(Map workingVars) {
 		if ( workingVars.containsKey( AvailableSettings.CLASS_NAMES ) ) {
 			Collection<String> classNames = (Collection<String>) workingVars.get(
 					AvailableSettings.CLASS_NAMES
 			);
 			addNamedAnnotatedClasses( this, classNames, workingVars );
 		}
 
 		if ( workingVars.containsKey( PARSED_MAPPING_DOMS ) ) {
 			Collection<XmlDocument> xmlDocuments = (Collection<XmlDocument>) workingVars.get( PARSED_MAPPING_DOMS );
 			for ( XmlDocument xmlDocument : xmlDocuments ) {
 				cfg.add( xmlDocument );
 			}
 		}
 
 		//TODO apparently only used for Tests, get rid of it?
 		if ( workingVars.containsKey( AvailableSettings.LOADED_CLASSES ) ) {
 			Collection<Class> classes = (Collection<Class>) workingVars.get( AvailableSettings.LOADED_CLASSES );
 			for ( Class clazz : classes ) {
 				cfg.addAnnotatedClass( clazz );
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.PACKAGE_NAMES ) ) {
 			Collection<String> packages = (Collection<String>) workingVars.get(
 					AvailableSettings.PACKAGE_NAMES
 			);
 			for ( String pkg : packages ) {
 				cfg.addPackage( pkg );
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.XML_FILE_NAMES ) ) {
 			Collection<String> xmlFiles = (Collection<String>) workingVars.get(
 					AvailableSettings.XML_FILE_NAMES
 			);
 			for ( String xmlFile : xmlFiles ) {
 				Boolean useMetaInf = null;
 				try {
 					if ( xmlFile.endsWith( META_INF_ORM_XML ) ) useMetaInf = true;
 					cfg.addResource( xmlFile );
 				}
 				catch( MappingNotFoundException e ) {
 					if ( ! xmlFile.endsWith( META_INF_ORM_XML ) ) {
 						throw new PersistenceException( getExceptionHeader()
 								+ "Unable to find XML mapping file in classpath: " + xmlFile);
 					}
 					else {
 						useMetaInf = false;
 						//swallow it, the META-INF/orm.xml is optional
 					}
 				}
 				catch( MappingException me ) {
 					throw new PersistenceException( getExceptionHeader()
 								+ "Error while reading JPA XML file: " + xmlFile, me);
 				}
 				if ( log.isInfoEnabled() ) {
 					if ( Boolean.TRUE.equals( useMetaInf ) ) {
 						log.info( "{} {} found", getExceptionHeader(), META_INF_ORM_XML);
 					}
 					else if (Boolean.FALSE.equals( useMetaInf ) ) {
 						log.info( "{} No {} found", getExceptionHeader(), META_INF_ORM_XML);
 					}
 				}
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.HBXML_FILES ) ) {
 			Collection<NamedInputStream> hbmXmlFiles = (Collection<NamedInputStream>) workingVars.get(
 					AvailableSettings.HBXML_FILES
 			);
 			for ( NamedInputStream is : hbmXmlFiles ) {
 				try {
 					//addInputStream has the responsibility to close the stream
 					cfg.addInputStream( new BufferedInputStream( is.getStream() ) );
 				}
 				catch (MappingException me) {
 					//try our best to give the file name
 					if ( StringHelper.isEmpty( is.getName() ) ) {
 						throw me;
 					}
 					else {
 						throw new MappingException("Error while parsing file: " + is.getName(), me );
 					}
 				}
 			}
 		}
 	}
 
 	private String getExceptionHeader() {
 		if ( StringHelper.isNotEmpty( persistenceUnitName ) ) {
 			return "[PersistenceUnit: " + persistenceUnitName + "] ";
 		}
 		else {
 			return "";
 		}
 	}
 
 	private Properties prepareProperties(Properties properties, Map workingVars) {
 		Properties preparedProperties = new Properties();
 
 		//defaults different from Hibernate
 		preparedProperties.setProperty( Environment.RELEASE_CONNECTIONS, "auto" );
 		preparedProperties.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		//settings that always apply to a compliant EJB3
 		preparedProperties.setProperty( Environment.AUTOCOMMIT, "true" );
 		preparedProperties.setProperty( Environment.USE_IDENTIFIER_ROLLBACK, "false" );
 		preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 		preparedProperties.setProperty( AvailableSettings.DISCARD_PC_ON_CLOSE, "false" );
 		if (cfgXmlResource != null) {
 			preparedProperties.setProperty( AvailableSettings.CFG_FILE, cfgXmlResource );
 			cfgXmlResource = null;
 		}
 
 		//override the new defaults with the user defined ones
 		//copy programmatically defined properties
 		if ( cfg.getProperties() != null ) preparedProperties.putAll( cfg.getProperties() );
 		//copy them coping from configuration
 		if ( properties != null ) preparedProperties.putAll( properties );
 		//note we don't copy cfg.xml properties, since they have to be overriden
 
 		if (transactionType == null) {
 			//if it has not been set, the user use a programmatic way
 			transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		defineTransactionType(
 				preparedProperties.getProperty( AvailableSettings.TRANSACTION_TYPE ),
 				workingVars
 		);
 		boolean hasTxStrategy = StringHelper.isNotEmpty(
 				preparedProperties.getProperty( Environment.TRANSACTION_STRATEGY )
 		);
 		if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.JTA ) {
 			preparedProperties.setProperty(
 					Environment.TRANSACTION_STRATEGY, JoinableCMTTransactionFactory.class.getName()
 			);
 		}
 		else if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName() );
 		}
 		if ( hasTxStrategy ) {
 			log.warn(
 					"Overriding {} is dangerous, this might break the EJB3 specification implementation",
 					Environment.TRANSACTION_STRATEGY
 			);
 		}
 		if ( preparedProperties.getProperty( Environment.FLUSH_BEFORE_COMPLETION ).equals( "true" ) ) {
 			preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 			log.warn( "Defining {}=true ignored in HEM", Environment.FLUSH_BEFORE_COMPLETION );
 		}
 		return preparedProperties;
 	}
 
 	private Class classForName(String className) throws ClassNotFoundException {
 		return ReflectHelper.classForName( className, this.getClass() );
 	}
 
 	private void setCacheStrategy(String propertyKey, Map properties, boolean isClass, Map workingVars) {
 		String role = propertyKey.substring(
 				( isClass ? AvailableSettings.CLASS_CACHE_PREFIX
 						.length() : AvailableSettings.COLLECTION_CACHE_PREFIX.length() )
 						+ 1
 		);
 		//dot size added
 		String value = (String) properties.get( propertyKey );
 		StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			error.append(
 					isClass ? AvailableSettings.CLASS_CACHE_PREFIX : AvailableSettings.COLLECTION_CACHE_PREFIX
 			);
 			error.append( ": " ).append( propertyKey ).append( " " ).append( value );
 			throw new PersistenceException( getExceptionHeader() + error.toString() );
 		}
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		if ( isClass ) {
 			boolean lazyProperty = true;
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 			cfg.setCacheConcurrencyStrategy( role, usage, region, lazyProperty );
 		}
 		else {
 			cfg.setCollectionCacheConcurrencyStrategy( role, usage, region );
 		}
 	}
 
 	private void addSecurity(List<String> keys, Map properties, Map workingVars) {
 		log.debug( "Adding security" );
 		if ( !properties.containsKey( AvailableSettings.JACC_CONTEXT_ID ) ) {
 			throw new PersistenceException( getExceptionHeader() +
 					"Entities have been configured for JACC, but "
 							+ AvailableSettings.JACC_CONTEXT_ID
 							+ " has not been set"
 			);
 		}
 		String contextId = (String) properties.get( AvailableSettings.JACC_CONTEXT_ID );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 
 		int roleStart = AvailableSettings.JACC_PREFIX.length() + 1;
 
 		for ( String key : keys ) {
 			JACCConfiguration jaccCfg = new JACCConfiguration( contextId );
 			try {
 				String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
 				int classStart = roleStart + role.length() + 1;
 				String clazz = key.substring( classStart, key.length() );
 				String actions = (String) properties.get( key );
 				jaccCfg.addPermission( role, clazz, actions );
 			}
 			catch (IndexOutOfBoundsException e) {
 				throw new PersistenceException( getExceptionHeader() +
 						"Illegal usage of " + AvailableSettings.JACC_PREFIX + ": " + key );
 			}
 		}
 	}
 
 	private void addNamedAnnotatedClasses(
 			Ejb3Configuration cfg, Collection<String> classNames, Map workingVars
 	) {
 		for ( String name : classNames ) {
 			try {
 				Class clazz = classForName( name );
 				cfg.addAnnotatedClass( clazz );
 			}
 			catch (ClassNotFoundException cnfe) {
 				Package pkg;
 				try {
 					pkg = classForName( name + ".package-info" ).getPackage();
 				}
 				catch (ClassNotFoundException e) {
 					pkg = null;
 				}
 				if ( pkg == null ) {
 					throw new PersistenceException( getExceptionHeader() +  "class or package not found", cnfe );
 				}
 				else {
 					cfg.addPackage( name );
 				}
 			}
 		}
 	}
 
 	/*
 		TODO: not needed any more?
 	public Settings buildSettings(ConnectionProvider connectionProvider) throws HibernateException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			return settingsFactory.buildSettings( cfg.getProperties(), connectionProvider );
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 	*/
 
 	public Ejb3Configuration addProperties(Properties props) {
 		cfg.addProperties( props );
 		return this;
 	}
 
 	public Ejb3Configuration addAnnotatedClass(Class persistentClass) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addAnnotatedClass( persistentClass );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration configure(String resource) throws HibernateException {
 		//delay the call to configure to allow proper addition of all annotated classes (EJB-330)
 		if (cfgXmlResource != null)
 			throw new PersistenceException("configure(String) method already called for " + cfgXmlResource);
 		this.cfgXmlResource = resource;
 		return this;
 	}
 
 	public Ejb3Configuration addPackage(String packageName) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addPackage( packageName );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addFile(String xmlFile) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addFile( xmlFile );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addClass(Class persistentClass) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addClass( persistentClass );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addFile(File xmlFile) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addFile( xmlFile );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public void buildMappings() {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.buildMappings();
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Iterator getClassMappings() {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			return cfg.getClassMappings();
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public EventListeners getEventListeners() {
 		return cfg.getEventListeners();
 	}
 
 	SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		return cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	public Iterator getTableMappings() {
 		return cfg.getTableMappings();
 	}
 
 	public PersistentClass getClassMapping(String persistentClass) {
 		return cfg.getClassMapping( persistentClass );
 	}
 
 	public org.hibernate.mapping.Collection getCollectionMapping(String role) {
 		return cfg.getCollectionMapping( role );
 	}
 
 	public void setEntityResolver(EntityResolver entityResolver) {
 		cfg.setEntityResolver( entityResolver );
 	}
 
 	public Map getNamedQueries() {
 		return cfg.getNamedQueries();
 	}
 
 	public Interceptor getInterceptor() {
 		return cfg.getInterceptor();
 	}
 
 	public Properties getProperties() {
 		return cfg.getProperties();
 	}
 
 	public Ejb3Configuration setInterceptor(Interceptor interceptor) {
 		cfg.setInterceptor( interceptor );
 		return this;
 	}
 
 	public Ejb3Configuration setProperties(Properties properties) {
 		cfg.setProperties( properties );
 		return this;
 	}
 
 	public Map getFilterDefinitions() {
 		return cfg.getFilterDefinitions();
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		cfg.addFilterDefinition( definition );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		cfg.addAuxiliaryDatabaseObject( object );
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return cfg.getNamingStrategy();
 	}
 
 	public Ejb3Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		cfg.setNamingStrategy( namingStrategy );
 		return this;
 	}
 
-	public Ejb3Configuration setPersisterClassProvider(PersisterClassProvider persisterClassProvider) {
-		cfg.setPersisterClassProvider( persisterClassProvider );
-		return this;
-	}
-
 	public void setListeners(String type, String[] listenerClasses) {
 		cfg.setListeners( type, listenerClasses );
 	}
 
 	public void setListeners(String type, Object[] listeners) {
 		cfg.setListeners( type, listeners );
 	}
 
 	/**
 	 * This API is intended to give a read-only configuration.
 	 * It is sueful when working with SchemaExport or any Configuration based
 	 * tool.
 	 * DO NOT update configuration through it.
 	 */
 	public AnnotationConfiguration getHibernateConfiguration() {
 		//TODO make it really read only (maybe through proxying)
 		return cfg;
 	}
 
 	public Ejb3Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addInputStream( xmlInputStream );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addResource(String path) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addResource( path );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addResource(String path, ClassLoader classLoader) throws MappingException {
 		cfg.addResource( path, classLoader );
 		return this;
 	}
 
 	private enum XML_SEARCH {
 		HBM,
 		ORM_XML,
 		BOTH,
 		NONE;
 
 		public static XML_SEARCH getType(boolean searchHbm, boolean searchOrm) {
 			return searchHbm ?
 					searchOrm ? XML_SEARCH.BOTH : XML_SEARCH.HBM :
 					searchOrm ? XML_SEARCH.ORM_XML : XML_SEARCH.NONE;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
index a3935788a1..16d07eaba7 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,487 +1,491 @@
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
 package org.hibernate.ejb.test.ejb3configuration;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Map;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
+import org.hibernate.mapping.Collection;
+import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
-import org.hibernate.persister.PersisterClassProvider;
+import org.hibernate.persister.internal.PersisterClassResolverInitiator;
+import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends junit.framework.TestCase {
 	public void testPersisterClassProvider() {
 		Ejb3Configuration conf = new Ejb3Configuration();
-		conf.setPersisterClassProvider( new GoofyPersisterClassProvider() );
+		conf.getProperties().put( PersisterClassResolverInitiator.IMPL_NAME, GoofyPersisterClassProvider.class );
 		conf.addAnnotatedClass( Bell.class );
 		try {
 			final EntityManagerFactory entityManagerFactory = conf.buildEntityManagerFactory();
 			entityManagerFactory.close();
 		}
 		catch ( PersistenceException e ) {
 			assertNotNull( e.getCause() );
 			assertNotNull( e.getCause().getCause() );
 			assertEquals( GoofyException.class, e.getCause().getCause().getClass() );
 
 		}
 	}
 
-	public static class GoofyPersisterClassProvider implements PersisterClassProvider {
-
-		public Class<? extends EntityPersister> getEntityPersisterClass(String entityName) {
+	public static class GoofyPersisterClassProvider implements PersisterClassResolver {
+		@Override
+		public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 			return GoofyProvider.class;
 		}
 
-		public Class<? extends CollectionPersister> getCollectionPersisterClass(String collectionPersister) {
+		@Override
+		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		public GoofyProvider(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.access.EntityRegionAccessStrategy strategy,
 								   org.hibernate.engine.SessionFactoryImplementor sf,
 								   org.hibernate.engine.Mapping mapping) {
 			throw new GoofyException();
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRootEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMetamodel getEntityMetamodel() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSubclassEntityName(String entityName) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasProxy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasMutableProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasSubselectLoadableCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCascades() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInherited() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isIdentifierAssignedByInsert() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIdentifierProperty() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean canExtractIdOutOfEntity() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Comparator getVersionComparator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public VersionType getVersionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getVersionProperty() {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasNaturalIdentifier() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasLazyProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type[] getPropertyTypes() {
 			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getPropertyNames() {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierPropertyName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCacheInvalidationRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazyPropertiesCacheable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ClassMetadata getClassMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isBatchLoadable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMode guessEntityMode(Object object) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstrumented(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasInsertGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUpdateGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersionPropertyGenerated() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterReassociate(Object entity, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 				throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getMappedClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsLifecycle(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsValidatable(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getConcreteProxyClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstance(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
