diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 1c3b32d733..62b2cd3d72 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,2809 +1,2809 @@
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
 import org.hibernate.engine.jdbc.spi.JdbcServices;
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
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.JACCConfiguration;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
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
         LOG.trace("Starting secondPassCompile() processing");
 
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
         LOG.debugf("Processing fk mappings (*ToOne and JoinedSubclass)");
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
             if (!isValidatorNotPresentLogged) LOG.validatorNotFound();
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
                         LOG.unableToApplyConstraints(className, e);
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
         LOG.debugf("Processing extends queue");
 		processExtendsQueue();
 
         LOG.debugf("Processing collection mappings");
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
         LOG.debugf("Processing native query and ResultSetMapping mappings");
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
         LOG.debugf("Processing association property references");
 
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
 
         LOG.debugf("Processing foreign key constraints");
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
         LOG.debugf("Processing extends queue");
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
                 LOG.debugf("Resolving reference to class: %s", referencedEntityName);
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
-	public SessionFactory buildSessionFactory(ServicesRegistry serviceRegistry) throws HibernateException {
+	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
         LOG.debugf("Preparing to build session factory with filters : %s", filterDefinitions);
 
 		secondPassCompile();
         if (!metadataSourceQueue.isEmpty()) LOG.incompleteMappingMetadataCacheProcessing();
 
 		enableLegacyHibernateValidator();
 		enableBeanValidation();
 		enableHibernateSearch();
 
 		validate();
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry.getService( JdbcServices.class ) );
 
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
             LOG.debugf("Legacy Validator not present in classpath, ignoring event listener registration");
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
                 LOG.debugf("Search not present in classpath, ignoring event listener registration.");
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
             LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
 		}
 		catch ( IllegalAccessException e ) {
             LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
 		}
 		catch ( NoSuchMethodException e ) {
             LOG.debugf("Method %s() not found in %s", SEARCH_STARTUP_METHOD, SEARCH_STARTUP_CLASS);
 		}
 		catch ( InvocationTargetException e ) {
             LOG.debugf("Unable to execute %s, ignoring event listener registration.", SEARCH_STARTUP_METHOD);
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
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory() built}
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
             LOG.debugf("%s=%s", name, value);
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
         LOG.configuringFromResource(resource);
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
         LOG.configurationResource(resource);
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
         LOG.configuringFromUrl(url);
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
         LOG.configuringFromFile(configFile.getName());
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
                 LOG.unableToCloseInputStreamForResource(resourceName, ioe);
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
 
         LOG.configuredSessionFactory(name);
         LOG.debugf("Properties: %s", properties);
 
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
             LOG.debugf("Session-factory config [%s] named resource [%s] for mapping", name, resourceName);
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named file [%s] for mapping", name, fileName);
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName);
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named package [%s] for mapping", name, packageName);
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named class [%s] for mapping", name, className);
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
         LOG.jaccContextId(contextId);
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
         LOG.debugf("Event listeners: %s=%s", type, StringHelper.toString(listenerClasses));
 		setListeners( type, listenerClasses );
 	}
 
 	private void parseListener(Element element) {
 		String type = element.attributeValue( "type" );
 		if ( type == null ) {
 			throw new MappingException( "No type specified for listener" );
 		}
 		String impl = element.attributeValue( "class" );
         LOG.debugf("Event listener: %s=%s", type, impl);
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
 	public Settings buildSettings(JdbcServices jdbcServices) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, jdbcServices );
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/internal/ServicesRegistryBootstrap.java b/hibernate-core/src/main/java/org/hibernate/cfg/internal/ServicesRegistryBootstrap.java
index a003c1895d..f9fd2410be 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/internal/ServicesRegistryBootstrap.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/internal/ServicesRegistryBootstrap.java
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
 package org.hibernate.cfg.internal;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
-import org.hibernate.service.internal.ServicesRegistryImpl;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.ServiceInitiator;
 
 /**
  * The standard bootstrap process for Hibernate services
  *
  * @author Steve Ebersole
  */
 public class ServicesRegistryBootstrap {
 	private List<ServiceInitiator> serviceInitiators = new ArrayList<ServiceInitiator>();
 
 	public ServicesRegistryBootstrap() {
 		serviceInitiators.add( ClassLoaderServiceInitiator.INSTANCE );
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		//serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 	}
 
-	public ServicesRegistryImpl initiateServicesRegistry(Map configurationValues) {
-		final ServicesRegistryImpl servicesRegistry = new ServicesRegistryImpl( serviceInitiators );
+	public ServiceRegistryImpl initiateServicesRegistry(Map configurationValues) {
+		final ServiceRegistryImpl servicesRegistry = new ServiceRegistryImpl( serviceInitiators );
 		servicesRegistry.initialize( configurationValues );
 		return servicesRegistry;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java
index a369981e8c..60a4c14d51 100644
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
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Standard initiator for the standard {@link JdbcServices} service
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesInitiator implements ServiceInitiator<JdbcServices> {
 	public static final JdbcServicesInitiator INSTANCE = new JdbcServicesInitiator();
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class<JdbcServices> getServiceInitiated() {
 		return JdbcServices.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public JdbcServices initiateService(Map configValues, ServicesRegistry registry) {
+	public JdbcServices initiateService(Map configValues, ServiceRegistry registry) {
 		return new JdbcServicesImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index aa638212f4..45299c6a61 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1199 +1,1199 @@
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
 import javax.transaction.TransactionManager;
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
 import org.hibernate.persister.PersisterFactory;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.stat.ConcurrentStatisticsImpl;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.EmptyIterator;
 import org.hibernate.util.ReflectHelper;
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
  * @see org.hibernate.classic.Session
  * @see org.hibernate.hql.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl implements SessionFactory, SessionFactoryImplementor {
 
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
-	private final transient ServicesRegistry serviceRegistry;
+	private final transient ServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient TransactionManager transactionManager;
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
 
 	public SessionFactoryImpl(
 			Configuration cfg,
 	        Mapping mapping,
-			ServicesRegistry serviceRegistry,
+			ServiceRegistry serviceRegistry,
 	        Settings settings,
 	        EventListeners listeners,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.buildingSessionFactory();
 
 		this.statistics = new ConcurrentStatisticsImpl( this );
 		getStatistics().setStatisticsEnabled( settings.isStatisticsEnabled() );
         LOG.debugf("Statistics initialized [enabled=%s]", settings.isStatisticsEnabled());
 
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
 			EntityPersister cp = PersisterFactory.createClassPersister( model, accessStrategy, this, mapping );
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
 			CollectionPersister persister = PersisterFactory.createCollectionPersister( cfg, model, accessStrategy, this) ;
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
 
 		if ( settings.getTransactionManagerLookup()!=null ) {
             LOG.debugf("Obtaining JTA TransactionManager");
 			transactionManager = settings.getTransactionManagerLookup().getTransactionManager(properties);
 		}
 		else {
 			if ( settings.getTransactionFactory().isTransactionManagerRequired() ) {
 				throw new HibernateException("The chosen transaction strategy requires access to the JTA TransactionManager");
 			}
 			transactionManager = null;
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
 
 		this.observer.sessionFactoryCreated( this );
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
 
 	public TransactionFactory getTransactionFactory() {
 		return settings.getTransactionFactory();
 	}
 
 	public TransactionManager getTransactionManager() {
 		return transactionManager;
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
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatability
 		if ( impl == null && transactionManager != null ) {
 			impl = "jta";
 		}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
index 0f4dab8d68..9fcbb4fd28 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
@@ -1,48 +1,48 @@
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
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 
 /**
  * Standard initiator for the stanndard {@link ClassLoaderService} service.
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceInitiator implements ServiceInitiator<ClassLoaderService> {
 	public static final ClassLoaderServiceInitiator INSTANCE = new ClassLoaderServiceInitiator();
 
 	@Override
 	public Class<ClassLoaderService> getServiceInitiated() {
 		return ClassLoaderService.class;
 	}
 
 	@Override
-	public ClassLoaderService initiateService(Map configurationValues, ServicesRegistry registry) {
+	public ClassLoaderService initiateService(Map configurationValues, ServiceRegistry registry) {
 		return new ClassLoaderServiceImpl( configurationValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
index c0ca43301a..4ee37d4666 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
@@ -1,111 +1,111 @@
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
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import org.hibernate.HibernateLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.spi.Service;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.service.spi.Stoppable;
 import org.hibernate.service.spi.UnknownServiceException;
 import org.jboss.logging.Logger;
 
 /**
  * Basic Hibernate implementation of the service registry.
  *
  * @author Steve Ebersole
  */
-public class ServicesRegistryImpl implements ServicesRegistry {
+public class ServiceRegistryImpl implements ServiceRegistry {
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ServicesRegistryImpl.class.getName());
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ServiceRegistryImpl.class.getName());
 
 	private final List<ServiceInitiator> serviceInitiators;
 	private ServicesInitializer initializer;
 
 	private HashMap<Class,Service> serviceMap = new HashMap<Class, Service>();
 	// IMPL NOTE : the list used for ordered destruction.  Cannot used ordered map above because we need to
 	// iterate it in reverse order which is only available through ListIterator
 	private List<Service> serviceList = new ArrayList<Service>();
 
-	public ServicesRegistryImpl(List<ServiceInitiator> serviceInitiators) {
+	public ServiceRegistryImpl(List<ServiceInitiator> serviceInitiators) {
 		this.serviceInitiators = Collections.unmodifiableList( serviceInitiators );
 	}
 
 	public void initialize(Map configurationValues) {
 		this.initializer = new ServicesInitializer( this, serviceInitiators, ConfigurationHelper.clone( configurationValues ) );
 	}
 
 	public void destroy() {
 		ListIterator<Service> serviceIterator = serviceList.listIterator();
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
 		serviceMap.clear();
 		serviceMap = null;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <T extends Service> T getService(Class<T> serviceRole) {
 		T service = internalGetService( serviceRole );
 		if ( service == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 		return service;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private <T extends Service> T locateService(Class<T> serviceRole) {
 		return (T) serviceMap.get( serviceRole );
 	}
 
 	<T extends Service> T internalGetService(Class<T> serviceRole) {
 		T service = locateService( serviceRole );
 		if ( service == null ) {
 			service = initializer.initializeService( serviceRole );
 		}
 		return service;
 	}
 
 	<T extends Service> void registerService(Class<T> serviceRole, T service) {
 		serviceList.add( service );
 		serviceMap.put( serviceRole, service );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java
index db4dfd2937..75120bcd20 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ServicesInitializer.java
@@ -1,189 +1,189 @@
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
 import org.hibernate.HibernateLogger;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.InjectService;
 import org.hibernate.service.spi.Manageable;
 import org.hibernate.service.spi.Service;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistryAwareService;
+import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.Startable;
 import org.hibernate.service.spi.UnknownServiceException;
 import org.jboss.logging.Logger;
 
 /**
  * Delegate responsible for initializing services
  *
  * @author Steve Ebersole
  */
 public class ServicesInitializer {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ServicesInitializer.class.getName());
 
-	private final ServicesRegistryImpl servicesRegistry;
+	private final ServiceRegistryImpl servicesRegistry;
 	private final Map<Class,ServiceInitiator> serviceInitiatorMap;
 	private final Map configurationValues;
 
 	public ServicesInitializer(
-			ServicesRegistryImpl servicesRegistry,
+			ServiceRegistryImpl servicesRegistry,
 			List<ServiceInitiator> serviceInitiators,
 			Map configurationValues) {
 		this.servicesRegistry = servicesRegistry;
 		this.serviceInitiatorMap = toMap( serviceInitiators );
 		this.configurationValues = configurationValues;
 	}
 
 	/**
 	 * We convert the incoming list of initiators to a map for 2 reasons:<ul>
 	 * <li>to make it easier to look up the initiator we need for a given service role</li>
 	 * <li>to make sure there is only one initator for a given service role (last wins)</li>
 	 * </ul>
 	 *
 	 * @param serviceInitiators
 	 * @return
 	 */
 	private static Map<Class, ServiceInitiator> toMap(List<ServiceInitiator> serviceInitiators) {
 		final Map<Class, ServiceInitiator> result = new HashMap<Class, ServiceInitiator>();
 		for ( ServiceInitiator initiator : serviceInitiators ) {
 			result.put( initiator.getServiceInitiated(), initiator );
 		}
 		return result;
 	}
 
 	/**
 	 * The main function of this delegate.  Used to initialize the service of a given role.
 	 *
 	 * @param serviceRole The service role
 	 * @param <T> The type of service role
 	 *
 	 * @return The intiialized instance of the service
 	 */
 	public <T extends Service> T initializeService(Class<T> serviceRole) {
         LOG.trace("Initializing service [role=" + serviceRole.getName() + "]");
 
 		// PHASE 1 : create service
 		T service = createService( serviceRole );
 
 		// PHASE 2 : configure service (***potentially recursive***)
 		configureService( service );
 
 		// PHASE 3 : Start service
 		startService( service, serviceRole );
 
 		return service;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private <T extends Service> T createService(Class<T> serviceRole) {
 		ServiceInitiator<T> initiator = serviceInitiatorMap.get( serviceRole );
 		if ( initiator == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 		try {
 			T service = initiator.initiateService( configurationValues, servicesRegistry );
 			// IMPL NOTE : the register call here is important to avoid potential stack overflow issues
 			//		from recursive calls through #configureService
 			servicesRegistry.registerService( serviceRole, service );
 			return service;
 		}
 		catch ( ServiceException e ) {
 			throw e;
 		}
 		catch ( Exception e ) {
 			throw new ServiceException( "Unable to create requested service [" + serviceRole.getName() + "]", e );
 		}
 	}
 
 	private <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
 		if ( Configurable.class.isInstance( service ) ) {
 			( (Configurable) service ).configure( configurationValues );
 		}
 
-		if ( ServicesRegistryAwareService.class.isInstance( service ) ) {
-			( (ServicesRegistryAwareService) service ).injectServices( servicesRegistry );
+		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
+			( (ServiceRegistryAwareService) service ).injectServices( servicesRegistry );
 		}
 	}
 
 	private <T extends Service> void applyInjections(T service) {
 		for ( Method method : service.getClass().getMethods() ) {
 			InjectService injectService = method.getAnnotation( InjectService.class );
 			if ( injectService == null ) {
 				continue;
 			}
 
 			applyInjection( service, method, injectService );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private <T extends Service> void applyInjection(T service, Method injectionMethod, InjectService injectService) {
 		if ( injectionMethod.getParameterTypes() == null || injectionMethod.getParameterTypes().length != 1 ) {
 			throw new ServiceDependencyException(
 					"Encountered @InjectService on method with unexpected number of parameters"
 			);
 		}
 
 		Class dependentServiceRole = injectService.serviceRole();
 		if ( dependentServiceRole == null || dependentServiceRole.equals( Void.class ) ) {
 			dependentServiceRole = injectionMethod.getParameterTypes()[0];
 		}
 
 		final Service dependantService = servicesRegistry.internalGetService( dependentServiceRole );
 		if ( dependantService == null ) {
 			if ( injectService.required() ) {
 				throw new ServiceDependencyException(
 						"Dependency [" + dependentServiceRole + "] declared by service [" + service + "] not found"
 				);
 			}
 		}
 		else {
 			try {
 				injectionMethod.invoke( service, dependantService );
 			}
 			catch ( Exception e ) {
 				throw new ServiceDependencyException( "Cannot inject dependency service", e );
 			}
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private <T extends Service> void startService(T service, Class serviceRole) {
 		if ( Startable.class.isInstance( service ) ) {
 			( (Startable) service ).start();
 		}
 
 		if ( Manageable.class.isInstance( service ) ) {
 			servicesRegistry.getService( JmxService.class ).registerService( (Manageable) service, serviceRole );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
index 625c462b2c..abd97a7501 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
@@ -1,276 +1,276 @@
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
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.jboss.logging.Logger;
 
 /**
  * Instantiates and configures an appropriate {@link ConnectionProvider}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class ConnectionProviderInitiator implements ServiceInitiator<ConnectionProvider> {
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
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class<ConnectionProvider> getServiceInitiated() {
 		return ConnectionProvider.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public ConnectionProvider initiateService(Map configurationValues, ServicesRegistry registry) {
+	public ConnectionProvider initiateService(Map configurationValues, ServiceRegistry registry) {
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
 	 * Transform JDBC connection properties.
 	 *
 	 * Passed in the form <tt>hibernate.connection.*</tt> to the
 	 * format accepted by <tt>DriverManager</tt> by trimming the leading "<tt>hibernate.connection</tt>".
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
 					final String passThruKey = key.substring( Environment.CONNECTION_PREFIX.length() + 1 );
 					result.setProperty( passThruKey, value );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
index e21daed91e..83f5aaa630 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
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
 package org.hibernate.service.jdbc.dialect.internal;
 import java.util.Map;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Standard initiator for the standard {@link DialectFactory} service
  *
  * @author Steve Ebersole
  */
 public class DialectFactoryInitiator implements ServiceInitiator<DialectFactory> {
 	public static final DialectFactoryInitiator INSTANCE = new DialectFactoryInitiator();
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class<DialectFactory> getServiceInitiated() {
 		return DialectFactory.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public DialectFactory initiateService(Map configVales, ServicesRegistry registry) {
+	public DialectFactory initiateService(Map configVales, ServiceRegistry registry) {
 		return new DialectFactoryImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
index 788b5761ee..a1d8868729 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
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
 package org.hibernate.service.jdbc.dialect.internal;
 import java.util.Map;
 import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Standard initiator for the standard {@link DialectResolver} service
  *
  * @author Steve Ebersole
  */
 public class DialectResolverInitiator implements ServiceInitiator<DialectResolver>  {
 	public static final DialectResolverInitiator INSTANCE = new DialectResolverInitiator();
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class<DialectResolver> getServiceInitiated() {
 		return DialectResolver.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public DialectResolver initiateService(Map configVales, ServicesRegistry registry) {
+	public DialectResolver initiateService(Map configVales, ServiceRegistry registry) {
 		return new StandardDialectResolver();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
index 6ccf0ce919..b4472c8317 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
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
 package org.hibernate.service.jmx.internal;
 import java.util.Map;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.jmx.spi.JmxService;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Standard initiator for the standard {@link JmxService} service
  *
  * @author Steve Ebersole
  */
 public class JmxServiceInitiator implements ServiceInitiator<JmxService> {
 	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final JmxServiceInitiator INSTANCE = new JmxServiceInitiator();
 
 	@Override
 	public Class<JmxService> getServiceInitiated() {
 		return JmxService.class;
 	}
 
 	@Override
-	public JmxService initiateService(Map configValues, ServicesRegistry registry) {
+	public JmxService initiateService(Map configValues, ServiceRegistry registry) {
 		return ConfigurationHelper.getBoolean( JMX_ENABLED, configValues, false )
 				? new JmxServiceImpl( configValues )
 				: DisabledJmxServiceImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
index 0ad971efae..732a1845eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
@@ -1,47 +1,47 @@
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
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Standard initiator for the standard {@link JndiService} service
  *
  * @author Steve Ebersole
  */
 public class JndiServiceInitiator implements ServiceInitiator<JndiService> {
 	public static final JndiServiceInitiator INSTANCE = new JndiServiceInitiator();
 
 	@Override
 	public Class<JndiService> getServiceInitiated() {
 		return JndiService.class;
 	}
 
 	@Override
-	public JndiService initiateService(Map configurationValues, ServicesRegistry registry) {
+	public JndiService initiateService(Map configurationValues, ServiceRegistry registry) {
 		return new JndiServiceImpl( configurationValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
index 5a22b39102..e1b7a84a4f 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
@@ -1,72 +1,72 @@
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
 import org.hibernate.HibernateException;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * Standard initiator for the standard {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
  *
  * @author Steve Ebersole
  */
 public class JtaPlatformInitiator implements ServiceInitiator<JtaPlatform> {
 	public static final JtaPlatformInitiator INSTANCE = new JtaPlatformInitiator();
 
 	public static final String JTA_PLATFORM = "hibernate.jta.platform";
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Class<JtaPlatform> getServiceInitiated() {
 		return JtaPlatform.class;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
-	public JtaPlatform initiateService(Map configVales, ServicesRegistry registry) {
+	public JtaPlatform initiateService(Map configVales, ServiceRegistry registry) {
 		final Object platform = configVales.get( JTA_PLATFORM );
 		if ( JtaPlatform.class.isInstance( platform ) ) {
 			return (JtaPlatform) platform;
 		}
 
 		if ( platform == null ) {
 			return null;
 		}
 
 		final String platformImplName = platform.toString();
 
 		ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 		try {
 			return (JtaPlatform) classLoaderService.classForName( platformImplName ).newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Unable to create specified JtaPlatform class [" + platformImplName + "]", e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
index a9deece840..78797c675e 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
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
 package org.hibernate.service.spi;
 import java.util.Map;
 
 /**
  * Responsible for initiating services.
  *
  * @author Steve Ebersole
  */
 public interface ServiceInitiator<T extends Service> {
 	/**
 	 * Obtains the service role initiated by this initiator.  Should be uniqie withion a registry
 	 *
 	 * @return The service role.
 	 */
 	public Class<T> getServiceInitiated();
 
 	/**
 	 * Initiates the managed service.
 	 *
 	 * @param configurationValues The configuration values in effect
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
-	public T initiateService(Map configurationValues, ServicesRegistry registry);
+	public T initiateService(Map configurationValues, ServiceRegistry registry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
index 644b5a1736..a46d38c480 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
@@ -1,44 +1,44 @@
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
  * The registry of services used by Hibernate
  *
  * @author Steve Ebersole
  */
-public interface ServicesRegistry {
+public interface ServiceRegistry {
 	/**
 	 * Retrieve a service by role.
 	 *
 	 * @param type The service role
 	 * @param <T> The type of the service
 	 *
 	 * @return The requested service.
 	 *
 	 * @throws UnknownServiceException Indicates the service was not known.
 	 */
 	public <T extends Service> T getService(Class<T> type);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
similarity index 79%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
index 638e118935..1c69099540 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServicesRegistryAwareService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
@@ -1,39 +1,39 @@
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
- * Allows services to be injected with the {@link ServicesRegistry} during configuration phase.
+ * Allows services to be injected with the {@link ServiceRegistry} during configuration phase.
  *
  * @author Steve Ebersole
  */
-public interface ServicesRegistryAwareService {
+public interface ServiceRegistryAwareService {
 	/**
-	 * Callback to inject the regsitry.
+	 * Callback to inject the registry.
 	 *
-	 * @param servicesRegistry The registry
+	 * @param serviceRegistry The registry
 	 */
-	public void injectServices(ServicesRegistry servicesRegistry);
+	public void injectServices(ServiceRegistry serviceRegistry);
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/ServiceBootstrappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/ServiceBootstrappingTest.java
index abe5379abd..b551716e65 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/ServiceBootstrappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/ServiceBootstrappingTest.java
@@ -1,89 +1,91 @@
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
 package org.hibernate.test.cfg.internal;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
-import org.hibernate.service.internal.ServicesRegistryImpl;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.test.common.ConnectionProviderBuilder;
 import org.hibernate.testing.junit.UnitTestCase;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class ServiceBootstrappingTest extends UnitTestCase {
-	private ServicesRegistryImpl servicesRegistry;
+	private ServiceRegistryImpl servicesRegistry;
 
 	public ServiceBootstrappingTest(String string) {
 		super( string );
 	}
 
-	protected void setUp() {
+	@Override
+    protected void setUp() {
 		List<ServiceInitiator> serviceInitiators = new ArrayList<ServiceInitiator>();
 		serviceInitiators.add( ClassLoaderServiceInitiator.INSTANCE );
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
-		servicesRegistry = new ServicesRegistryImpl( serviceInitiators );
+		servicesRegistry = new ServiceRegistryImpl( serviceInitiators );
 	}
 
-	protected void tearDown() {
+	@Override
+    protected void tearDown() {
 		servicesRegistry.destroy();
 	}
 
 	public void testBasicBuild() {
 		servicesRegistry.initialize( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		JdbcServices jdbcServices = servicesRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider() instanceof DriverManagerConnectionProviderImpl );
 		assertFalse( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 	}
 
 	public void testBuildWithLogging() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.put( Environment.SHOW_SQL, "true" );
 
 		servicesRegistry.initialize( props );
 		JdbcServices jdbcServices = servicesRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider() instanceof DriverManagerConnectionProviderImpl );
 		assertTrue( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/TestServicesRegistryBootstrapping.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/TestServicesRegistryBootstrapping.java
index 70240d2850..ce3b3e24c1 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/TestServicesRegistryBootstrapping.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/internal/TestServicesRegistryBootstrapping.java
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
 package org.hibernate.test.cfg.internal;
 import org.hibernate.cfg.internal.ServicesRegistryBootstrap;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.service.internal.ServicesRegistryImpl;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 import org.hibernate.test.common.ConnectionProviderBuilder;
 import org.hibernate.testing.junit.UnitTestCase;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class TestServicesRegistryBootstrapping extends UnitTestCase {
 
 	public TestServicesRegistryBootstrapping(String string) {
 		super( string );
 	}
 
 	public void testBasicBootstrapping() {
-		ServicesRegistryImpl servicesRegistry = new ServicesRegistryBootstrap().initiateServicesRegistry(
+		ServiceRegistryImpl servicesRegistry = new ServicesRegistryBootstrap().initiateServicesRegistry(
 				ConnectionProviderBuilder.getConnectionProviderProperties()
 		);
 
 		assertNotNull( servicesRegistry.getService( JdbcServices.class ) );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/common/ServiceRegistryHolder.java b/hibernate-core/src/test/java/org/hibernate/test/common/ServiceRegistryHolder.java
index b1fb168b58..f13e0a3743 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/common/ServiceRegistryHolder.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/common/ServiceRegistryHolder.java
@@ -1,77 +1,78 @@
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
 package org.hibernate.test.common;
+
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.internal.ServicesRegistryBootstrap;
 import org.hibernate.engine.jdbc.internal.JdbcServicesImpl;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.internal.ServicesRegistryImpl;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.spi.ServiceRegistry;
 
 /**
  * @author Gail Badner
  */
 public class ServiceRegistryHolder {
-	private final ServicesRegistryImpl serviceRegistry;
+	private final ServiceRegistryImpl serviceRegistry;
 	private final Properties properties;
 
 	public ServiceRegistryHolder(Map props) {
 		properties = new Properties();
 		properties.putAll( props );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		serviceRegistry = new ServicesRegistryBootstrap().initiateServicesRegistry( properties );
 		properties.putAll( serviceRegistry.getService( JdbcServices.class ).getDialect().getDefaultProperties() );
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
-	public ServicesRegistry getServiceRegistry() {
+	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry.getService( JdbcServices.class );
 	}
 
 	public JdbcServicesImpl getJdbcServicesImpl() {
 		return ( JdbcServicesImpl ) getJdbcServices();
 	}
 
 	public ClassLoaderService getClassLoaderService() {
 		return serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	public void destroy() {
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/testing/junit/UnitTestCase.java b/hibernate-core/src/test/java/org/hibernate/testing/junit/UnitTestCase.java
index 5134999c1e..d52e5c3918 100644
--- a/hibernate-core/src/test/java/org/hibernate/testing/junit/UnitTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/testing/junit/UnitTestCase.java
@@ -1,188 +1,188 @@
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
 package org.hibernate.testing.junit;
 import static org.hibernate.TestLogger.LOG;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestCase;
 import junit.framework.TestSuite;
 import org.hibernate.TestLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.test.common.ServiceRegistryHolder;
 import org.jboss.logging.Logger;
 
 /**
  * A basic JUnit {@link junit.framework.TestCase} subclass for
  * adding some Hibernate specific behavior and functionality.
  *
  * @author Steve Ebersole
  */
 public abstract class UnitTestCase extends junit.framework.TestCase {
 
 	private ServiceRegistryHolder serviceRegistryHolder;
 
 	public UnitTestCase(String string) {
 		super( string );
 	}
 
 	/**
 	 * runBare overridden in order to apply FailureExpected validations
 	 * as well as start/complete logging
 	 *
 	 * @throws Throwable
 	 */
 	@Override
     public void runBare() throws Throwable {
 		final boolean doValidate = getName().endsWith( "FailureExpected" ) && Boolean.getBoolean( "hibernate.test.validatefailureexpected" );
 		try {
             Logger.getMessageLogger(TestLogger.class, TestLogger.class.getName()).info("Starting test [" + fullTestName() + "]");
 			super.runBare();
 			if ( doValidate ) {
 				throw new FailureExpectedTestPassedException();
 			}
 		}
 		catch ( FailureExpectedTestPassedException t ) {
 			throw t;
 		}
 		catch( Throwable t ) {
 			if ( doValidate ) {
 				skipExpectedFailure( t );
 			}
 			else {
 				throw t;
 			}
 		}
 		finally {
             LOG.info("Completed test [" + fullTestName() + "]");
 		}
 	}
 
 	@Override
 	protected void tearDown() throws Exception {
 		if ( serviceRegistryHolder != null ) {
 				serviceRegistryHolder.destroy();
 				serviceRegistryHolder = null;
 		}
 	}
 
-	protected ServicesRegistry getServiceRegistry() {
+	protected ServiceRegistry getServiceRegistry() {
 		if ( serviceRegistryHolder == null ) {
 			serviceRegistryHolder = new ServiceRegistryHolder( Environment.getProperties() );
 		}
 		return serviceRegistryHolder.getServiceRegistry();
  	}
 
 	protected JdbcServices getJdbcServices() {
 		return getServiceRegistry().getService( JdbcServices.class );
 	}
 
 	protected ConnectionProvider getConnectionProvider() {
 		return getJdbcServices().getConnectionProvider();
 	}
 
 	private static class FailureExpectedTestPassedException extends Exception {
 		public FailureExpectedTestPassedException() {
 			super( "Test marked as FailureExpected, but did not fail!" );
 		}
 	}
 
 	protected void skipExpectedFailure(Throwable error) {
 		reportSkip( "ignoring *FailuredExpected methods", "Failed with: " + error.toString() );
 	}
 
 	// additional assertions ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public static void assertElementTypeAssignability(java.util.Collection collection, Class clazz) throws AssertionFailedError {
 		Iterator itr = collection.iterator();
 		while ( itr.hasNext() ) {
 			assertClassAssignability( itr.next().getClass(), clazz );
 		}
 	}
 
 	public static void assertClassAssignability(Class source, Class target) throws AssertionFailedError {
 		if ( !target.isAssignableFrom( source ) ) {
 			throw new AssertionFailedError(
 			        "Classes were not assignment-compatible : source<" + source.getName() +
 			        "> target<" + target.getName() + ">"
 			);
 		}
 	}
 
 
 	// test skipping ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String fullTestName() {
 		return this.getClass().getName() + "#" + this.getName();
 	}
 
 	protected void reportSkip(String reason, String testDescription) {
         LOG.warn("*** skipping [" + fullTestName() + "] - " + testDescription + " : " + reason, new Exception());
 	}
 
 	// testsuite utitities ---------------------------------------------------
 
 	/**
 	 * Supports easy creation of TestSuites where a subclass' "FailureExpected"
 	 * version of a base test is included in the suite, while the base test
 	 * is excluded.  E.g. test class FooTestCase includes method testBar(), while test
 	 * class SubFooTestCase extends FooTestCase includes method testBarFailureExcluded().
 	 * Passing SubFooTestCase.class to this method will return a suite that
 	 * does not include testBar().
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
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/ExecutionEnvironment.java b/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/ExecutionEnvironment.java
index e4853093c0..ba7f1445e2 100644
--- a/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/ExecutionEnvironment.java
+++ b/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/ExecutionEnvironment.java
@@ -1,220 +1,220 @@
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
 package org.hibernate.testing.junit.functional;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.test.common.ServiceRegistryHolder;
 
 /**
  * {@inheritDoc}
  *
  * @author Steve Ebersole
  */
 public class ExecutionEnvironment {
 
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private final ExecutionEnvironment.Settings settings;
 
 	private Map conectionProviderInjectionProperties;
 	private ServiceRegistryHolder serviceRegistryHolder;
 	private Configuration configuration;
 	private SessionFactory sessionFactory;
 	private boolean allowRebuild;
 
 	public ExecutionEnvironment(ExecutionEnvironment.Settings settings) {
 		this.settings = settings;
 	}
 
 	public boolean isAllowRebuild() {
 		return allowRebuild;
 	}
 
 	public void setAllowRebuild(boolean allowRebuild) {
 		this.allowRebuild = allowRebuild;
 	}
 
 	public Dialect getDialect() {
 		return DIALECT;
 	}
 
 	public Configuration getConfiguration() {
 		return configuration;
 	}
 
-	public ServicesRegistry getServiceRegistry() {
+	public ServiceRegistry getServiceRegistry() {
 		return serviceRegistryHolder.getServiceRegistry();
 	}
 
 	public SessionFactory getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public void initialize(Map conectionProviderInjectionProperties) {
 		if ( sessionFactory != null ) {
 			throw new IllegalStateException( "attempt to initialize already initialized ExecutionEnvironment" );
 		}
 		if ( ! settings.appliesTo( getDialect() ) ) {
 			return;
 		}
 
 		this.conectionProviderInjectionProperties = conectionProviderInjectionProperties;
 		Configuration configuration = new Configuration();
 		configuration.setProperty( Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider" );
 
 		settings.configure( configuration );
 
 		if ( settings.createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 
 		// make sure we use the same dialect...
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 
 		applyMappings( configuration );
 		configuration.buildMappings();
 
 		applyCacheSettings( configuration );
 		settings.afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 
 		this.configuration = configuration;
 
 		serviceRegistryHolder = new ServiceRegistryHolder( getServiceRegistryProperties() );
 		sessionFactory = configuration.buildSessionFactory( serviceRegistryHolder.getServiceRegistry() );
 
 		settings.afterSessionFactoryBuilt( ( SessionFactoryImplementor ) sessionFactory );
 	}
 
 	private Map getServiceRegistryProperties() {
 		Map serviceRegistryProperties = configuration.getProperties();
 		if ( conectionProviderInjectionProperties != null && conectionProviderInjectionProperties.size() > 0 ) {
 			serviceRegistryProperties = new HashMap(
 					configuration.getProperties().size() + conectionProviderInjectionProperties.size()
 			);
 			serviceRegistryProperties.putAll( configuration.getProperties() );
 			serviceRegistryProperties.put(
 					ConnectionProviderInitiator.INJECTION_DATA, conectionProviderInjectionProperties
 			);
 		}
 		return serviceRegistryProperties;
 	}
 
 	private void applyMappings(Configuration configuration) {
 		String[] mappings = settings.getMappings();
 		for ( String mapping : mappings ) {
 			configuration.addResource(
 					settings.getBaseForMappings() + mapping,
 					ExecutionEnvironment.class.getClassLoader()
 			);
 		}
 	}
 
 	private void applyCacheSettings(Configuration configuration) {
 		if ( settings.getCacheConcurrencyStrategy() != null ) {
 			Iterator iter = configuration.getClassMappings();
 			while ( iter.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) iter.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( ( SimpleValue ) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && settings.overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), settings.getCacheConcurrencyStrategy() );
 				}
 			}
 			iter = configuration.getCollectionMappings();
 			while ( iter.hasNext() ) {
 				Collection coll = (Collection) iter.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), settings.getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	public void rebuild() {
 		if ( !allowRebuild ) {
 			return;
 		}
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 			sessionFactory = null;
 		}
 		if ( serviceRegistryHolder != null ) {
 			serviceRegistryHolder.destroy();
 			serviceRegistryHolder = null;
 		}
 		serviceRegistryHolder = new ServiceRegistryHolder( getServiceRegistryProperties() );
 		sessionFactory = configuration.buildSessionFactory( serviceRegistryHolder.getServiceRegistry() );
 		settings.afterSessionFactoryBuilt( ( SessionFactoryImplementor ) sessionFactory );
 	}
 
 	public void complete() {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 			sessionFactory = null;
 		}
 		if ( serviceRegistryHolder != null ) {
 			serviceRegistryHolder.destroy();
 			serviceRegistryHolder = null;
 		}
 		configuration = null;
 	}
 
 	public static interface Settings {
 		public String[] getMappings();
 		public String getBaseForMappings();
 		public boolean createSchema();
 		public boolean recreateSchemaAfterFailure();
 		public void configure(Configuration cfg);
 		public boolean overrideCacheStrategy();
 		public String getCacheConcurrencyStrategy();
 		public void afterSessionFactoryBuilt(SessionFactoryImplementor sfi);
 		public void afterConfigurationBuilt(Mappings mappings, Dialect dialect);
 		public boolean appliesTo(Dialect dialect);
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/annotations/HibernateTestCase.java b/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/annotations/HibernateTestCase.java
index 5e5d637dcc..a68e7a0020 100644
--- a/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/annotations/HibernateTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/testing/junit/functional/annotations/HibernateTestCase.java
@@ -1,348 +1,348 @@
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
 
 // $Id$
 
 package org.hibernate.testing.junit.functional.annotations;
 import static org.hibernate.TestLogger.LOG;
 import java.lang.annotation.Annotation;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 import java.sql.Connection;
 import java.sql.SQLException;
 import junit.framework.TestCase;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.jdbc.Work;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.test.common.ServiceRegistryHolder;
 import org.hibernate.testing.junit.DialectChecks;
 import org.hibernate.testing.junit.FailureExpected;
 import org.hibernate.testing.junit.RequiresDialect;
 import org.hibernate.testing.junit.RequiresDialectFeature;
 import org.hibernate.testing.junit.SkipForDialect;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.util.StringHelper;
 
 /**
  * A base class for all tests.
  *
  * @author Emmnauel Bernand
  * @author Hardy Ferentschik
  */
 public abstract class HibernateTestCase extends TestCase {
 
 	protected static Configuration cfg;
 	private static Class<?> lastTestClass;
 	private ServiceRegistryHolder serviceRegistryHolder;
 
 	public HibernateTestCase() {
 		super();
 	}
 
 	public HibernateTestCase(String x) {
 		super( x );
 	}
 
 	@Override
 	public void runBare() throws Throwable {
 		Method runMethod = findTestMethod();
 
 		final Skip skip = determineSkipByDialect( Dialect.getDialect(), runMethod );
 		if ( skip != null ) {
 			reportSkip( skip );
 			return;
 		}
 
 		setUp();
 		try {
 			runTest();
 		}
 		finally {
 			tearDown();
 		}
 	}
 
 	@Override
 	protected void runTest() throws Throwable {
 		Method runMethod = findTestMethod();
 		FailureExpected failureExpected = locateAnnotation( FailureExpected.class, runMethod );
 		try {
 			super.runTest();
 			if ( failureExpected != null ) {
 				throw new FailureExpectedTestPassedException();
 			}
 		}
 		catch ( FailureExpectedTestPassedException t ) {
 			closeResources();
 			throw t;
 		}
 		catch ( Throwable t ) {
 			if ( t instanceof InvocationTargetException ) {
 				t = ( ( InvocationTargetException ) t ).getTargetException();
 			}
 			if ( t instanceof IllegalAccessException ) {
 				t.fillInStackTrace();
 			}
 			closeResources();
 			if ( failureExpected != null ) {
 				StringBuilder builder = new StringBuilder();
 				if ( StringHelper.isNotEmpty( failureExpected.message() ) ) {
 					builder.append( failureExpected.message() );
 				}
 				else {
 					builder.append( "ignoring @FailureExpected test" );
 				}
 				builder.append( " (" )
 						.append( failureExpected.jiraKey() )
 						.append( ")" );
                 LOG.warn(builder.toString(), t);
 			}
 			else {
 				throw t;
 			}
 		}
 	}
 
 	@Override
 	protected void setUp() throws Exception {
 		if ( cfg == null || lastTestClass != getClass() ) {
 			buildConfiguration();
 			lastTestClass = getClass();
 		}
 		else {
 			runSchemaGeneration();
 		}
 	}
 
 	@Override
 	protected void tearDown() throws Exception {
 		runSchemaDrop();
 		handleUnclosedResources();
 	}
 
-	protected ServicesRegistry getServiceRegistry() {
+	protected ServiceRegistry getServiceRegistry() {
 		if ( serviceRegistryHolder == null ) {
 			serviceRegistryHolder = new ServiceRegistryHolder( Environment.getProperties() );
 		}
 		return serviceRegistryHolder.getServiceRegistry();
  	}
 
 	protected JdbcServices getJdbcServices() {
 		return getServiceRegistry().getService( JdbcServices.class );
 	}
 
 	protected static class Skip {
 		private final String reason;
 		private final String testDescription;
 
 		public Skip(String reason, String testDescription) {
 			this.reason = reason;
 			this.testDescription = testDescription;
 		}
 	}
 
 	protected final Skip determineSkipByDialect(Dialect dialect, Method runMethod) throws Exception {
 		// skips have precedence, so check them first
 		SkipForDialect skipForDialectAnn = locateAnnotation( SkipForDialect.class, runMethod );
 		if ( skipForDialectAnn != null ) {
 			for ( Class<? extends Dialect> dialectClass : skipForDialectAnn.value() ) {
 				if ( skipForDialectAnn.strictMatching() ) {
 					if ( dialectClass.equals( dialect.getClass() ) ) {
 						return buildSkip( dialect, skipForDialectAnn.comment(), skipForDialectAnn.jiraKey() );
 					}
 				}
 				else {
 					if ( dialectClass.isInstance( dialect ) ) {
 						return buildSkip( dialect, skipForDialectAnn.comment(), skipForDialectAnn.jiraKey() );
 					}
 				}
 			}
 		}
 
 		// then check against the requires
 		RequiresDialect requiresDialectAnn = locateAnnotation( RequiresDialect.class, runMethod );
 		if ( requiresDialectAnn != null ) {
 			for ( Class<? extends Dialect> dialectClass : requiresDialectAnn.value() ) {
 				if ( requiresDialectAnn.strictMatching() ) {
 					if ( !dialectClass.equals( dialect.getClass() ) ) {
 						return buildSkip( dialect, null, null );
 					}
 				}
 				else {
 					if ( !dialectClass.isInstance( dialect ) ) {
 						return buildSkip( dialect, requiresDialectAnn.comment(), requiresDialectAnn.jiraKey() );
 					}
 				}
 			}
 		}
 
 		// then check against a dialect feature
 		RequiresDialectFeature requiresDialectFeatureAnn = locateAnnotation( RequiresDialectFeature.class, runMethod );
 		if ( requiresDialectFeatureAnn != null ) {
 			Class<? extends DialectChecks> checkClass = requiresDialectFeatureAnn.value();
 			DialectChecks check = checkClass.newInstance();
 			boolean skip = !check.include( dialect );
 			if ( skip ) {
 				return buildSkip( dialect, requiresDialectFeatureAnn.comment(), requiresDialectFeatureAnn.jiraKey() );
 			}
 		}
 		return null;
 	}
 
 	protected <T extends Annotation> T locateAnnotation(Class<T> annotationClass, Method runMethod) {
 		T annotation = runMethod.getAnnotation( annotationClass );
 		if ( annotation == null ) {
 			annotation = getClass().getAnnotation( annotationClass );
 		}
 		if ( annotation == null ) {
 			annotation = runMethod.getDeclaringClass().getAnnotation( annotationClass );
 		}
 		return annotation;
 	}
 
 	protected Skip buildSkip(Dialect dialect, String comment, String jiraKey) {
 		StringBuilder buffer = new StringBuilder();
 		buffer.append( "skipping database-specific test [" );
 		buffer.append( fullTestName() );
 		buffer.append( "] for dialect [" );
 		buffer.append( dialect.getClass().getName() );
 		buffer.append( ']' );
 
 		if ( StringHelper.isNotEmpty( comment ) ) {
 			buffer.append( "; " ).append( comment );
 		}
 
 		if ( StringHelper.isNotEmpty( jiraKey ) ) {
 			buffer.append( " (" ).append( jiraKey ).append( ')' );
 		}
 
 		return new Skip( buffer.toString(), null );
 	}
 
 	public String fullTestName() {
 		return this.getClass().getName() + "#" + this.getName();
 	}
 
 	private Method findTestMethod() {
 		String fName = getName();
 		assertNotNull( fName );
 		Method runMethod = null;
 		try {
 			runMethod = getClass().getMethod( fName );
 		}
 		catch ( NoSuchMethodException e ) {
 			fail( "Method \"" + fName + "\" not found" );
 		}
 		if ( !Modifier.isPublic( runMethod.getModifiers() ) ) {
 			fail( "Method \"" + fName + "\" should be public" );
 		}
 		return runMethod;
 	}
 
 	protected abstract void buildConfiguration() throws Exception;
 
 	protected abstract Class<?>[] getAnnotatedClasses();
 
 	protected String[] getMappings() {
 		return new String[] { };
 	}
 
 	protected abstract void handleUnclosedResources();
 
 	protected void closeResources() {
 		if ( serviceRegistryHolder != null ) {
 			serviceRegistryHolder.destroy();
 			serviceRegistryHolder = null;
 		}
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return new String[] { };
 	}
 
 	protected String[] getXmlFiles() {
 		return new String[] { };
 	}
 
 	protected Dialect getDialect() {
 		return Dialect.getDialect();
 	}
 
 	protected static void setCfg(Configuration cfg) {
 		HibernateTestCase.cfg = cfg;
 	}
 
 	protected static Configuration getCfg() {
 		return cfg;
 	}
 
 	protected void configure(Configuration cfg) {
 	}
 
 	protected boolean recreateSchema() {
 		return true;
 	}
 
 	protected void runSchemaGeneration() {
 		SchemaExport export = new SchemaExport( getJdbcServices(), cfg );
 		export.create( true, true );
 	}
 
 	protected void runSchemaDrop() {
 		SchemaExport export = new SchemaExport( getJdbcServices(), cfg );
 		export.drop( true, true );
 	}
 
 	private void reportSkip(Skip skip) {
 		reportSkip( skip.reason, skip.testDescription );
 	}
 
 	protected void reportSkip(String reason, String testDescription) {
 		StringBuilder builder = new StringBuilder();
 		builder.append( "*** skipping test [" );
 		builder.append( fullTestName() );
 		builder.append( "] - " );
 		builder.append( testDescription );
 		builder.append( " : " );
 		builder.append( reason );
         LOG.warn(builder.toString());
 	}
 
 	public class RollbackWork implements Work {
 
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	private static class FailureExpectedTestPassedException extends Exception {
 		public FailureExpectedTestPassedException() {
 			super( "Test marked as @FailureExpected, but did not fail!" );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index 83c15e961f..7ae0496331 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1606 +1,1606 @@
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
 import org.hibernate.event.EventListeners;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.JACCConfiguration;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
-import org.hibernate.service.spi.ServicesRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.transaction.JDBCTransactionFactory;
 import org.hibernate.util.CollectionHelper;
 import org.hibernate.util.ReflectHelper;
 import org.hibernate.util.StringHelper;
 import org.hibernate.util.xml.MappingReader;
 import org.hibernate.util.xml.OriginImpl;
 import org.hibernate.util.xml.XmlDocument;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
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
 	 * @return The configured EJB3Configurartion object
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
 	@Deprecated
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
 		if ( preparedProperties.containsKey( AvailableSettings.INTERCEPTOR )
 				&& ( cfg.getInterceptor() == null
 				|| cfg.getInterceptor().equals( defaultInterceptor ) ) ) {
 			//cfg.setInterceptor has precedence over configuration file
 			String interceptorName = preparedProperties.getProperty( AvailableSettings.INTERCEPTOR );
 			try {
 				Class interceptor = classForName( interceptorName );
 				cfg.setInterceptor( (Interceptor) interceptor.newInstance() );
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to find interceptor class: " + interceptorName, e
 				);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to access interceptor class: " + interceptorName, e
 				);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to instanciate interceptor class: " + interceptorName, e
 				);
 			}
 			catch (ClassCastException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Interceptor class does not implement Interceptor interface: " + interceptorName, e
 				);
 			}
 		}
 		if ( preparedProperties.containsKey( AvailableSettings.NAMING_STRATEGY )
 				&& ( cfg.getNamingStrategy() == null
 				|| cfg.getNamingStrategy().equals( defaultNamingStrategy ) ) ) {
 			//cfg.setNamingStrategy has precedence over configuration file
 			String namingStrategyName = preparedProperties.getProperty( AvailableSettings.NAMING_STRATEGY );
 			try {
 				Class namingStragegy = classForName( namingStrategyName );
 				cfg.setNamingStrategy( (NamingStrategy) namingStragegy.newInstance() );
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to find naming strategy class: " + namingStrategyName, e
 				);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to access naming strategy class: " + namingStrategyName, e
 				);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to instanciate naming strategy class: " + namingStrategyName, e
 				);
 			}
 			catch (ClassCastException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Naming strategyy class does not implement NmaingStrategy interface: " + namingStrategyName,
 						e
 				);
 			}
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
 		discardOnClose = preparedProperties.getProperty( AvailableSettings.DISCARD_PC_ON_CLOSE )
 				.equals( "true" );
 		return this;
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
                 if (Boolean.TRUE.equals(useMetaInf)) LOG.exceptionHeaderFound(getExceptionHeader(), META_INF_ORM_XML);
                 else if (Boolean.FALSE.equals(useMetaInf)) LOG.exceptionHeaderNotFound(getExceptionHeader(), META_INF_ORM_XML);
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
         return (StringHelper.isNotEmpty(persistenceUnitName)) ? "[PersistenceUnit: " + persistenceUnitName + "] " : "";
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
 			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JDBCTransactionFactory.class.getName() );
 		}
         if (hasTxStrategy) LOG.overridingTransactionStrategyDangerous(Environment.TRANSACTION_STRATEGY);
 		if ( preparedProperties.getProperty( Environment.FLUSH_BEFORE_COMPLETION ).equals( "true" ) ) {
 			preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
             LOG.definingFlushBeforeCompletionIgnoredInHem(Environment.FLUSH_BEFORE_COMPLETION);
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
         LOG.debugf("Adding security");
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
                 if (pkg == null) throw new PersistenceException(getExceptionHeader() + "class or package not found", cnfe);
                 else cfg.addPackage(name);
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
 
-	SessionFactory buildSessionFactory(ServicesRegistry serviceRegistry) throws HibernateException {
+	SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
index a0333034ac..024392f4bf 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
@@ -1,230 +1,230 @@
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
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import javax.persistence.Cache;
 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceUnitUtil;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.LoadState;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.internal.ServicesRegistryBootstrap;
 import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
 import org.hibernate.ejb.metamodel.MetamodelImpl;
 import org.hibernate.ejb.util.PersistenceUtilHelper;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
-import org.hibernate.service.internal.ServicesRegistryImpl;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 
 /**
  * Actual Hiberate implementation of {@link javax.persistence.EntityManagerFactory}.
  *
  * @author Gavin King
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryImpl implements HibernateEntityManagerFactory {
-	private final transient ServicesRegistryImpl serviceRegistry;
+	private final transient ServiceRegistryImpl serviceRegistry;
 	private final SessionFactory sessionFactory;
 	private final PersistenceUnitTransactionType transactionType;
 	private final boolean discardOnClose;
 	private final Class sessionInterceptorClass;
 	private final CriteriaBuilderImpl criteriaBuilder;
 	private final Metamodel metamodel;
 	private final HibernatePersistenceUnitUtil util;
 	private final Map<String,Object> properties;
 	private final Map connectionProviderInjectionData;
 
 	private final PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
 
 	@SuppressWarnings( "unchecked" )
 	public EntityManagerFactoryImpl(
 			PersistenceUnitTransactionType transactionType,
 			boolean discardOnClose,
 			Class<?> sessionInterceptorClass,
 			Configuration cfg,
 			Map connectionProviderInjectionData) {
 		// FIXME: Get rid of this temporary way of creating the service registry for EM
 		Map serviceRegistryProperties = new HashMap(
 				cfg.getProperties().size() + connectionProviderInjectionData.size()
 		);
 		serviceRegistryProperties.putAll( cfg.getProperties() );
 		serviceRegistryProperties.putAll( connectionProviderInjectionData );
 		this.serviceRegistry = new ServicesRegistryBootstrap().initiateServicesRegistry( serviceRegistryProperties );
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
 		this.connectionProviderInjectionData = new HashMap();
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
 		serviceRegistry.destroy();
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
