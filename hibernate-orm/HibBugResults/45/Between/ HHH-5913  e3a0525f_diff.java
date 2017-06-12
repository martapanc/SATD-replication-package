diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index f9c8fbec42..e6f6bf5739 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,2954 +1,2762 @@
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
 
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
-import java.lang.reflect.Constructor;
-import java.lang.reflect.Method;
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
-import java.util.ResourceBundle;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
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
-import org.hibernate.cfg.beanvalidation.BeanValidationActivator;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
-import org.hibernate.event.EventListenerRegistration;
-import org.hibernate.event.EventType;
-import org.hibernate.event.PreInsertEventListener;
-import org.hibernate.event.PreUpdateEventListener;
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
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.service.StandardServiceInitiators;
-import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
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
-
-		applyConstraintsToDDL();
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
 
-	private void applyConstraintsToDDL() {
-		boolean applyOnDdl = getProperties().getProperty(
-				"hibernate.validator.apply_to_ddl",
-				"true"
-		)
-				.equalsIgnoreCase( "true" );
-
-		if ( !applyOnDdl ) {
-			return; // nothing to do in this case
-		}
-		applyHibernateValidatorLegacyConstraintsOnDDL();
-		applyBeanValidationConstraintsOnDDL();
-	}
-
-	@SuppressWarnings({ "unchecked" })
-	private void applyHibernateValidatorLegacyConstraintsOnDDL() {
-		//TODO search for the method only once and cache it?
-		Constructor validatorCtr = null;
-		Method applyMethod = null;
-		try {
-			Class classValidator = ReflectHelper.classForName(
-					"org.hibernate.validator.ClassValidator", this.getClass()
-			);
-			Class messageInterpolator = ReflectHelper.classForName(
-					"org.hibernate.validator.MessageInterpolator", this.getClass()
-			);
-			validatorCtr = classValidator.getDeclaredConstructor(
-					Class.class, ResourceBundle.class, messageInterpolator, Map.class, ReflectionManager.class
-			);
-			applyMethod = classValidator.getMethod( "apply", PersistentClass.class );
-		}
-		catch ( ClassNotFoundException e ) {
-            if (!isValidatorNotPresentLogged) LOG.validatorNotFound();
-			isValidatorNotPresentLogged = true;
-		}
-		catch ( NoSuchMethodException e ) {
-			throw new AnnotationException( e );
-		}
-		if ( applyMethod != null ) {
-			for ( PersistentClass persistentClazz : classes.values() ) {
-				//integrate the validate framework
-				String className = persistentClazz.getClassName();
-				if ( StringHelper.isNotEmpty( className ) ) {
-					try {
-						Object validator = validatorCtr.newInstance(
-								ReflectHelper.classForName( className ), null, null, null, reflectionManager
-						);
-						applyMethod.invoke( validator, persistentClazz );
-					}
-					catch ( Exception e ) {
-                        LOG.unableToApplyConstraints(className, e);
-					}
-				}
-			}
-		}
-	}
-
-	@SuppressWarnings({ "unchecked" })
-	private void applyBeanValidationConstraintsOnDDL() {
-		BeanValidationActivator.applyDDL( classes.values(), getProperties() );
-	}
-
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
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
         LOG.debugf("Preparing to build session factory with filters : %s", filterDefinitions);
 
 		secondPassCompile();
         if (!metadataSourceQueue.isEmpty()) LOG.incompleteMappingMetadataCacheProcessing();
 
-// todo : processing listeners for validator and search (and envers) requires HHH-5562
-		enableLegacyHibernateValidator( serviceRegistry );
-		enableBeanValidation( serviceRegistry );
-//		enableHibernateSearch();
-
 		validate();
+
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
 				sessionFactoryObserver
 			);
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		final ServiceRegistry serviceRegistry =  new BasicServiceRegistryImpl( properties );
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
-	private static final String LEGACY_VALIDATOR_EVENT_LISTENER = "org.hibernate.validator.event.ValidateEventListener";
-
-	private void enableLegacyHibernateValidator(ServiceRegistry serviceRegistry) {
-		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class )
-				.attachEventListenerRegistration( new LegacyHibernateValidatorEventListenerRegistration( properties ) );
-	}
-
-	public static class LegacyHibernateValidatorEventListenerRegistration implements EventListenerRegistration {
-		private final Properties configurationProperties;
-
-		public LegacyHibernateValidatorEventListenerRegistration(Properties configurationProperties) {
-			this.configurationProperties = configurationProperties;
-		}
-
-		@Override
-		public void apply(
-				EventListenerRegistry eventListenerRegistry,
-				Configuration configuration, Map<?, ?> configValues, ServiceRegistryImplementor serviceRegistry
-		) {
-			boolean loadLegacyValidator = ConfigurationHelper.getBoolean( "hibernate.validator.autoregister_listeners", configurationProperties, false );
-
-			Class validateEventListenerClass = null;
-			try {
-				validateEventListenerClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( LEGACY_VALIDATOR_EVENT_LISTENER );
-			}
-			catch ( Exception ignored) {
-			}
-
-			if ( ! loadLegacyValidator || validateEventListenerClass == null) {
-				LOG.debugf( "Skipping legacy validator loading" );
-				return;
-			}
-
-			final Object validateEventListener;
-			try {
-				validateEventListener = validateEventListenerClass.newInstance();
-			}
-			catch ( Exception e ) {
-				throw new AnnotationException( "Unable to load Validator event listener", e );
-			}
-
-			EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
-			// todo : duplication strategy
-
-			listenerRegistry.appendListeners( EventType.PRE_INSERT, (PreInsertEventListener) validateEventListener );
-			listenerRegistry.appendListeners( EventType.PRE_UPDATE, (PreUpdateEventListener) validateEventListener );
-		}
-	}
-
-	private void enableBeanValidation(ServiceRegistry serviceRegistry) {
-		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
-				new EventListenerRegistration() {
-					@Override
-					public void apply(
-							EventListenerRegistry eventListenerRegistry,
-							Configuration configuration,
-							Map<?, ?> configValues,
-							ServiceRegistryImplementor serviceRegistry) {
-						BeanValidationActivator.activateBeanValidation( eventListenerRegistry, getProperties() );
-					}
-				}
-		);
-	}
-
-	private static final String SEARCH_EVENT_LISTENER_REGISTERER_CLASS = "org.hibernate.cfg.search.HibernateSearchEventListenerRegister";
-
-	/**
-	 * Tries to automatically register Hibernate Search event listeners by locating the
-	 * appropriate bootstrap class and calling the <code>enableHibernateSearch</code> method.
-	 */
-//	private void enableHibernateSearch() {
-//		// load the bootstrap class
-//		Class searchStartupClass;
-//		try {
-//			searchStartupClass = ReflectHelper.classForName( SEARCH_STARTUP_CLASS, getClass() );
-//		}
-//		catch ( ClassNotFoundException e ) {
-//			// TODO remove this together with SearchConfiguration after 3.1.0 release of Search
-//			// try loading deprecated HibernateSearchEventListenerRegister
-//			try {
-//				searchStartupClass = ReflectHelper.classForName( SEARCH_EVENT_LISTENER_REGISTERER_CLASS, getClass() );
-//			}
-//			catch ( ClassNotFoundException cnfe ) {
-//                LOG.debugf("Search not present in classpath, ignoring event listener registration.");
-//				return;
-//			}
-//		}
-//
-//		// call the method for registering the listeners
-//		try {
-//			Object searchStartupInstance = searchStartupClass.newInstance();
-//			Method enableSearchMethod = searchStartupClass.getDeclaredMethod(
-//					SEARCH_STARTUP_METHOD,
-//					EventListeners.class,
-//					Properties.class
-//			);
-//			enableSearchMethod.invoke( searchStartupInstance, getEventListeners(), getProperties() );
-//		}
-//		catch ( InstantiationException e ) {
-//            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
-//		}
-//		catch ( IllegalAccessException e ) {
-//            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
-//		}
-//		catch ( NoSuchMethodException e ) {
-//            LOG.debugf("Method %s() not found in %s", SEARCH_STARTUP_METHOD, SEARCH_STARTUP_CLASS);
-//		}
-//		catch ( InvocationTargetException e ) {
-//            LOG.debugf("Unable to execute %s, ignoring event listener registration.", SEARCH_STARTUP_METHOD);
-//		}
-//	}
-
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
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
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
             LOG.debugf("Added %s with class %s", typeName, typeClass);
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
deleted file mode 100644
index 7c1def1353..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
+++ /dev/null
@@ -1,174 +0,0 @@
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
-package org.hibernate.cfg.beanvalidation;
-
-import java.lang.reflect.InvocationTargetException;
-import java.lang.reflect.Method;
-import java.util.Collection;
-import java.util.HashSet;
-import java.util.Properties;
-import java.util.Set;
-
-import org.hibernate.HibernateException;
-import org.hibernate.cfg.Environment;
-import org.hibernate.internal.util.ReflectHelper;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.service.event.spi.EventListenerRegistry;
-
-/**
- * This class has no hard dependency on Bean Validation APIs
- * It must use reflection every time BV is required.
- * @author Emmanuel Bernard
- */
-public class BeanValidationActivator {
-	private static final String BV_DISCOVERY_CLASS = "javax.validation.Validation";
-	private static final String TYPE_SAFE_ACTIVATOR_CLASS = "org.hibernate.cfg.beanvalidation.TypeSafeActivator";
-	private static final String TYPE_SAFE_DDL_METHOD = "applyDDL";
-	private static final String TYPE_SAFE_ACTIVATOR_METHOD = "activateBeanValidation";
-	private static final String MODE_PROPERTY = "javax.persistence.validation.mode";
-
-	public static void activateBeanValidation(EventListenerRegistry listenerRegistry, Properties properties) {
-		Set<ValidationMode> modes = ValidationMode.getModes( properties.get( MODE_PROPERTY ) );
-
-		try {
-			//load Validation
-			ReflectHelper.classForName( BV_DISCOVERY_CLASS, BeanValidationActivator.class );
-		}
-		catch ( ClassNotFoundException e ) {
-			if ( modes.contains( ValidationMode.CALLBACK ) ) {
-				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
-			}
-			else if (modes.contains( ValidationMode.AUTO ) ) {
-				//nothing to activate
-				return;
-			}
-		}
-
-		//de-activate not-null tracking at the core level when Bean Validation
-		// is present unless the user really asks for it
-		//Note that if BV is not present, the behavior is backward compatible
-		if ( properties.getProperty( Environment.CHECK_NULLABILITY ) == null ) {
-			properties.setProperty( Environment.CHECK_NULLABILITY, "false" );
-		}
-
-		if ( ! ( modes.contains( ValidationMode.CALLBACK ) || modes.contains( ValidationMode.AUTO ) ) ) return;
-
-		try {
-			Class<?> activator = ReflectHelper.classForName( TYPE_SAFE_ACTIVATOR_CLASS, BeanValidationActivator.class );
-			Method activateBeanValidation =
-					activator.getMethod( TYPE_SAFE_ACTIVATOR_METHOD, EventListenerRegistry.class, Properties.class );
-			activateBeanValidation.invoke( null, listenerRegistry, properties );
-		}
-		catch ( NoSuchMethodException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-		catch ( IllegalAccessException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-		catch ( InvocationTargetException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-		catch ( ClassNotFoundException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-	}
-
-	public static void applyDDL(Collection<PersistentClass> persistentClasses, Properties properties) {
-		Set<ValidationMode> modes = ValidationMode.getModes( properties.get( MODE_PROPERTY ) );
-		if ( ! ( modes.contains( ValidationMode.DDL ) || modes.contains( ValidationMode.AUTO ) ) ) return;
-		try {
-			//load Validation
-			ReflectHelper.classForName( BV_DISCOVERY_CLASS, BeanValidationActivator.class );
-		}
-		catch ( ClassNotFoundException e ) {
-			if ( modes.contains( ValidationMode.DDL ) ) {
-				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
-			}
-			else if (modes.contains( ValidationMode.AUTO ) ) {
-				//nothing to activate
-				return;
-			}
-		}
-		try {
-			Class<?> activator = ReflectHelper.classForName( TYPE_SAFE_ACTIVATOR_CLASS, BeanValidationActivator.class );
-			Method applyDDL =
-					activator.getMethod( TYPE_SAFE_DDL_METHOD, Collection.class, Properties.class );
-			applyDDL.invoke( null, persistentClasses, properties );
-		}
-		catch ( NoSuchMethodException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-		catch ( IllegalAccessException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-		catch ( InvocationTargetException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-		catch ( ClassNotFoundException e ) {
-			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
-		}
-	}
-
-	private static enum ValidationMode {
-		AUTO,
-		CALLBACK,
-		NONE,
-		DDL;
-
-		public static Set<ValidationMode> getModes(Object modeProperty) {
-			Set<ValidationMode> modes = new HashSet<ValidationMode>(3);
-			if (modeProperty == null) {
-				modes.add(ValidationMode.AUTO);
-			}
-			else {
-				final String[] modesInString = modeProperty.toString().split( "," );
-				for ( String modeInString : modesInString ) {
-					modes.add( getMode(modeInString) );
-				}
-			}
-			if ( modes.size() > 1 && ( modes.contains( ValidationMode.AUTO ) || modes.contains( ValidationMode.NONE ) ) ) {
-				StringBuilder message = new StringBuilder( "Incompatible validation modes mixed: " );
-				for (ValidationMode mode : modes) {
-					message.append( mode ).append( ", " );
-				}
-				throw new HibernateException( message.substring( 0, message.length() - 2 ) );
-			}
-			return modes;
-		}
-
-		private static ValidationMode getMode(String modeProperty) {
-			if (modeProperty == null || modeProperty.length() == 0) {
-				return AUTO;
-			}
-			else {
-				try {
-					return valueOf( modeProperty.trim().toUpperCase() );
-				}
-				catch ( IllegalArgumentException e ) {
-					throw new HibernateException( "Unknown validation mode in " + MODE_PROPERTY + ": " + modeProperty.toString() );
-				}
-			}
-		}
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
new file mode 100644
index 0000000000..e60b66f211
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
@@ -0,0 +1,234 @@
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
+package org.hibernate.cfg.beanvalidation;
+
+import java.lang.reflect.Method;
+import java.util.Collection;
+import java.util.HashSet;
+import java.util.Properties;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.impl.Integrator;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BeanValidationIntegrator implements Integrator {
+	private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BeanValidationIntegrator.class.getName());
+
+	public static final String MODE_PROPERTY = "javax.persistence.validation.mode";
+
+	private static final String ACTIVATOR_CLASS = "org.hibernate.cfg.beanvalidation.TypeSafeActivator";
+	private static final String DDL_METHOD = "applyDDL";
+	private static final String ACTIVATE_METHOD = "activateBeanValidation";
+
+	@Override
+	public void integrate(
+			Configuration configuration,
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		// determine requested validation modes.
+		final Set<ValidationMode> modes = ValidationMode.getModes( configuration.getProperties().get( MODE_PROPERTY ) );
+
+		// locate the type safe activator class
+		final Class typeSafeActivatorClass = loadTypeSafeActivatorClass( serviceRegistry );
+
+		// todo : if this works out, probably better to simply alter TypeSafeActivator into a single method...
+
+		applyRelationalConstraints(
+				modes,
+				typeSafeActivatorClass,
+				configuration
+		);
+		applyHibernateListeners(
+				modes,
+				typeSafeActivatorClass,
+				configuration,
+				sessionFactory,
+				serviceRegistry
+		);
+	}
+
+	private Class loadTypeSafeActivatorClass(SessionFactoryServiceRegistry serviceRegistry) {
+		try {
+			return serviceRegistry.getService( ClassLoaderService.class ).classForName( ACTIVATOR_CLASS );
+		}
+		catch (Exception e) {
+			// might be ok for the class loading to fail depending on what validation modes were requested...
+			return null;
+		}
+	}
+
+	private void applyRelationalConstraints(
+			Set<ValidationMode> modes,
+			Class typeSafeActivatorClass,
+			Configuration configuration) {
+		if ( ! ConfigurationHelper.getBoolean( LegacyHibernateValidationIntegrator.APPLY_CONSTRAINTS, configuration.getProperties(), true ) ){
+			LOG.debug( "Skipping application of relational constraints from legacy Hibernate Validator" );
+			return;
+		}
+
+		if ( ! ( modes.contains( ValidationMode.DDL ) || modes.contains( ValidationMode.AUTO ) ) ) {
+			return;
+		}
+		if ( typeSafeActivatorClass == null ) {
+			if ( modes.contains( ValidationMode.DDL ) ) {
+				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
+			}
+			else if (modes.contains( ValidationMode.AUTO ) ) {
+				//nothing to activate
+				return;
+			}
+		}
+
+		try {
+			Method applyDDLMethod = typeSafeActivatorClass.getMethod( DDL_METHOD, Collection.class, Properties.class );
+			try {
+				applyDDLMethod.invoke(
+						null,
+						configuration.createMappings().getClasses().values(),
+						configuration.getProperties()
+				);
+			}
+			catch (HibernateException e) {
+				throw e;
+			}
+			catch (Exception e) {
+				throw new HibernateException( "Error applying BeanValidation relational constraints", e );
+			}
+		}
+		catch (HibernateException e) {
+			throw e;
+		}
+		catch (Exception e) {
+			throw new HibernateException( "Unable to locate TypeSafeActivator#applyDDL method", e );
+		}
+	}
+
+	private void applyHibernateListeners(
+			Set<ValidationMode> modes,
+			Class typeSafeActivatorClass,
+			Configuration configuration,
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		// de-activate not-null tracking at the core level when Bean Validation is present unless the user explicitly
+		// asks for it
+		if ( configuration.getProperty( Environment.CHECK_NULLABILITY ) == null ) {
+			sessionFactory.getSettings().setCheckNullability( false );
+		}
+
+		if ( ! ( modes.contains( ValidationMode.CALLBACK ) || modes.contains( ValidationMode.AUTO ) ) ) {
+			return;
+		}
+
+		if ( typeSafeActivatorClass == null ) {
+			if ( modes.contains( ValidationMode.CALLBACK ) ) {
+				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
+			}
+			else if (modes.contains( ValidationMode.AUTO ) ) {
+				//nothing to activate
+				return;
+			}
+		}
+
+		try {
+			Method activateMethod = typeSafeActivatorClass.getMethod( ACTIVATE_METHOD, EventListenerRegistry.class, Properties.class );
+			try {
+				activateMethod.invoke(
+						null,
+						serviceRegistry.getService( EventListenerRegistry.class ),
+						configuration.getProperties()
+				);
+			}
+			catch (HibernateException e) {
+				throw e;
+			}
+			catch (Exception e) {
+				throw new HibernateException( "Error applying BeanValidation relational constraints", e );
+			}
+		}
+		catch (HibernateException e) {
+			throw e;
+		}
+		catch (Exception e) {
+			throw new HibernateException( "Unable to locate TypeSafeActivator#applyDDL method", e );
+		}
+	}
+
+
+	// Because the javax validation classes might not be on the runtime classpath
+	private static enum ValidationMode {
+		AUTO,
+		CALLBACK,
+		NONE,
+		DDL;
+
+		public static Set<ValidationMode> getModes(Object modeProperty) {
+			Set<ValidationMode> modes = new HashSet<ValidationMode>(3);
+			if (modeProperty == null) {
+				modes.add(ValidationMode.AUTO);
+			}
+			else {
+				final String[] modesInString = modeProperty.toString().split( "," );
+				for ( String modeInString : modesInString ) {
+					modes.add( getMode(modeInString) );
+				}
+			}
+			if ( modes.size() > 1 && ( modes.contains( ValidationMode.AUTO ) || modes.contains( ValidationMode.NONE ) ) ) {
+				StringBuilder message = new StringBuilder( "Incompatible validation modes mixed: " );
+				for (ValidationMode mode : modes) {
+					message.append( mode ).append( ", " );
+				}
+				throw new HibernateException( message.substring( 0, message.length() - 2 ) );
+			}
+			return modes;
+		}
+
+		private static ValidationMode getMode(String modeProperty) {
+			if (modeProperty == null || modeProperty.length() == 0) {
+				return AUTO;
+			}
+			else {
+				try {
+					return valueOf( modeProperty.trim().toUpperCase() );
+				}
+				catch ( IllegalArgumentException e ) {
+					throw new HibernateException( "Unknown validation mode in " + MODE_PROPERTY + ": " + modeProperty );
+				}
+			}
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/LegacyHibernateValidationIntegrator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/LegacyHibernateValidationIntegrator.java
new file mode 100644
index 0000000000..b1f1c12972
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/LegacyHibernateValidationIntegrator.java
@@ -0,0 +1,155 @@
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
+package org.hibernate.cfg.beanvalidation;
+
+import java.lang.reflect.Constructor;
+import java.lang.reflect.Method;
+import java.util.Map;
+import java.util.ResourceBundle;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.AnnotationException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.annotations.common.reflection.ReflectionManager;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.event.EventType;
+import org.hibernate.event.PreInsertEventListener;
+import org.hibernate.event.PreUpdateEventListener;
+import org.hibernate.impl.Integrator;
+import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
+/**
+ * @author Steve Ebersole
+ */
+public class LegacyHibernateValidationIntegrator implements Integrator {
+    private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, LegacyHibernateValidationIntegrator.class.getName() );
+
+	public static final String APPLY_CONSTRAINTS = "hibernate.validator.apply_to_ddl";
+	public static final String CLASS_VALIDATOR_CLASS = "org.hibernate.validator.ClassValidator";
+	public static final String MSG_INTERPOLATOR_CLASS = "org.hibernate.validator.MessageInterpolator";
+
+	public static final String AUTO_REGISTER = "hibernate.validator.autoregister_listeners";
+	public static final String LISTENER_CLASS_NAME = "org.hibernate.validator.event.ValidateEventListener";
+
+	// this code mostly all copied and pasted from various spots...
+
+	@Override
+	public void integrate(
+			Configuration configuration,
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		applyRelationalConstraints( configuration, serviceRegistry );
+		applyListeners( configuration, serviceRegistry );
+	}
+
+	@SuppressWarnings( {"unchecked"})
+	private void applyRelationalConstraints(Configuration configuration, SessionFactoryServiceRegistry serviceRegistry) {
+		if ( ! ConfigurationHelper.getBoolean( APPLY_CONSTRAINTS, configuration.getProperties(), true ) ){
+			LOG.debug( "Skipping application of relational constraints from legacy Hibernate Validator" );
+			return;
+		}
+
+		Constructor validatorCtr = null;
+		Method applyMethod = null;
+		try {
+			final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
+			final Class classValidator = classLoaderService.classForName( CLASS_VALIDATOR_CLASS );
+			final Class messageInterpolator = classLoaderService.classForName( MSG_INTERPOLATOR_CLASS );
+			validatorCtr = classValidator.getDeclaredConstructor(
+					Class.class, ResourceBundle.class, messageInterpolator, Map.class, ReflectionManager.class
+			);
+			applyMethod = classValidator.getMethod( "apply", PersistentClass.class );
+		}
+		catch ( NoSuchMethodException e ) {
+			throw new AnnotationException( e );
+		}
+		catch ( Exception e ) {
+			LOG.debug( "Legacy Hibernate Validator classes not found, ignoring" );
+		}
+
+		if ( applyMethod != null ) {
+			Iterable<PersistentClass> persistentClasses = ( (Map<String,PersistentClass>) configuration.createMappings().getClasses() ).values();
+			for ( PersistentClass persistentClass : persistentClasses ) {
+				// integrate the validate framework
+				String className = persistentClass.getClassName();
+				if ( StringHelper.isNotEmpty( className ) ) {
+					try {
+						Object validator = validatorCtr.newInstance(
+								ReflectHelper.classForName( className ), null, null, null, configuration.getReflectionManager()
+						);
+						applyMethod.invoke( validator, persistentClass );
+					}
+					catch ( Exception e ) {
+						LOG.unableToApplyConstraints(className, e);
+					}
+				}
+			}
+		}
+	}
+
+	private void applyListeners(Configuration configuration, SessionFactoryServiceRegistry serviceRegistry) {
+		final boolean registerListeners = ConfigurationHelper.getBoolean( AUTO_REGISTER, configuration.getProperties(), false );
+		if ( !registerListeners ) {
+			LOG.debug( "Skipping legacy validator auto registration" );
+			return;
+		}
+
+		final Class listenerClass = loadListenerClass( serviceRegistry );
+		if ( listenerClass == null ) {
+			LOG.debug( "Skipping legacy validator auto registration - could not locate listener" );
+			return;
+		}
+
+		final Object validateEventListener;
+		try {
+			validateEventListener = listenerClass.newInstance();
+		}
+		catch ( Exception e ) {
+			throw new AnnotationException( "Unable to instantiate Validator event listener", e );
+		}
+
+		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		// todo : duplication strategy
+
+		listenerRegistry.appendListeners( EventType.PRE_INSERT, (PreInsertEventListener) validateEventListener );
+		listenerRegistry.appendListeners( EventType.PRE_UPDATE, (PreUpdateEventListener) validateEventListener );
+	}
+
+	private Class loadListenerClass(SessionFactoryServiceRegistry serviceRegistry) {
+		try {
+			return serviceRegistry.getService( ClassLoaderService.class ).classForName( LISTENER_CLASS_NAME );
+		}
+		catch (Exception e) {
+			return null;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
deleted file mode 100644
index 85f5d8bf72..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchEventListenerRegister.java
+++ /dev/null
@@ -1,245 +0,0 @@
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
-package org.hibernate.cfg.search;
-
-import java.util.Properties;
-import org.hibernate.AnnotationException;
-import org.hibernate.HibernateLogger;
-import org.hibernate.event.EventListeners;
-import org.hibernate.event.PostCollectionRecreateEventListener;
-import org.hibernate.event.PostCollectionRemoveEventListener;
-import org.hibernate.event.PostCollectionUpdateEventListener;
-import org.hibernate.event.PostDeleteEventListener;
-import org.hibernate.event.PostInsertEventListener;
-import org.hibernate.event.PostUpdateEventListener;
-import org.hibernate.internal.util.ReflectHelper;
-import org.jboss.logging.Logger;
-
-/**
- * Helper methods initializing Hibernate Search event listeners.
- *
- * @deprecated as of release 3.4.0.CR2, replaced by Hibernate Search's {@link org.hibernate.search.cfg.EventListenerRegister}
- * @author Emmanuel Bernard
- * @author Hardy Ferentschik
- */
-@Deprecated
-public class HibernateSearchEventListenerRegister {
-
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       HibernateSearchEventListenerRegister.class.getName());
-
-	/**
-	 * Class name of the class needed to enable Search.
-	 */
-	private static final String FULL_TEXT_INDEX_EVENT_LISTENER_CLASS = "org.hibernate.search.event.FullTextIndexEventListener";
-
-	/**
-	 * @deprecated as of release 3.4.0.CR2, replaced by Hibernate Search's {@link org.hibernate.search.cfg.EventListenerRegister#enableHibernateSearch(EventListeners, Properties)}
-	 */
-	@SuppressWarnings("unchecked")
-	@Deprecated
-	public static void enableHibernateSearch(EventListeners eventListeners, Properties properties) {
-		// check whether search is explicitly enabled - if so there is nothing
-		// to do
-		String enableSearchListeners = properties.getProperty( "hibernate.search.autoregister_listeners" );
-		if("false".equalsIgnoreCase(enableSearchListeners )) {
-            LOG.willNotRegisterListeners();
-			return;
-		}
-
-		// add search events if the jar is available and class can be loaded
-		Class searchEventListenerClass = attemptToLoadSearchEventListener();
-		if ( searchEventListenerClass == null ) {
-            LOG.unableToFindListenerClass(FULL_TEXT_INDEX_EVENT_LISTENER_CLASS);
-			return;
-		}
-
-		Object searchEventListener = instantiateEventListener(searchEventListenerClass);
-
-        // TODO Generalize this. Pretty much the same code all the time. Reflection?
-		{
-			boolean present = false;
-			PostInsertEventListener[] listeners = eventListeners
-					.getPostInsertEventListeners();
-			if (listeners != null) {
-				for (Object eventListener : listeners) {
-					// not isAssignableFrom since the user could subclass
-					present = present
-							|| searchEventListenerClass == eventListener
-									.getClass();
-				}
-				if (!present) {
-					int length = listeners.length + 1;
-					PostInsertEventListener[] newListeners = new PostInsertEventListener[length];
-					System.arraycopy(listeners, 0, newListeners, 0, length - 1);
-					newListeners[length - 1] = (PostInsertEventListener) searchEventListener;
-					eventListeners.setPostInsertEventListeners(newListeners);
-				}
-			} else {
-				eventListeners
-						.setPostInsertEventListeners(new PostInsertEventListener[] { (PostInsertEventListener) searchEventListener });
-			}
-		}
-		{
-			boolean present = false;
-			PostUpdateEventListener[] listeners = eventListeners
-					.getPostUpdateEventListeners();
-			if (listeners != null) {
-				for (Object eventListener : listeners) {
-					// not isAssignableFrom since the user could subclass
-					present = present
-							|| searchEventListenerClass == eventListener
-									.getClass();
-				}
-				if (!present) {
-					int length = listeners.length + 1;
-					PostUpdateEventListener[] newListeners = new PostUpdateEventListener[length];
-					System.arraycopy(listeners, 0, newListeners, 0, length - 1);
-					newListeners[length - 1] = (PostUpdateEventListener) searchEventListener;
-					eventListeners.setPostUpdateEventListeners(newListeners);
-				}
-			} else {
-				eventListeners
-						.setPostUpdateEventListeners(new PostUpdateEventListener[] { (PostUpdateEventListener) searchEventListener });
-			}
-		}
-		{
-			boolean present = false;
-			PostDeleteEventListener[] listeners = eventListeners
-					.getPostDeleteEventListeners();
-			if (listeners != null) {
-				for (Object eventListener : listeners) {
-					// not isAssignableFrom since the user could subclass
-					present = present
-							|| searchEventListenerClass == eventListener
-									.getClass();
-				}
-				if (!present) {
-					int length = listeners.length + 1;
-					PostDeleteEventListener[] newListeners = new PostDeleteEventListener[length];
-					System.arraycopy(listeners, 0, newListeners, 0, length - 1);
-					newListeners[length - 1] = (PostDeleteEventListener) searchEventListener;
-					eventListeners.setPostDeleteEventListeners(newListeners);
-				}
-			} else {
-				eventListeners
-						.setPostDeleteEventListeners(new PostDeleteEventListener[] { (PostDeleteEventListener) searchEventListener });
-			}
-		}
-		{
-			boolean present = false;
-			PostCollectionRecreateEventListener[] listeners = eventListeners.getPostCollectionRecreateEventListeners();
-			if ( listeners != null ) {
-				for (Object eventListener : listeners) {
-					//not isAssignableFrom since the user could subclass
-					present = present || searchEventListenerClass == eventListener.getClass();
-				}
-				if ( !present ) {
-					int length = listeners.length + 1;
-					PostCollectionRecreateEventListener[] newListeners = new PostCollectionRecreateEventListener[length];
-					System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-					newListeners[length - 1] = (PostCollectionRecreateEventListener) searchEventListener;
-					eventListeners.setPostCollectionRecreateEventListeners( newListeners );
-				}
-			}
-			else {
-				eventListeners.setPostCollectionRecreateEventListeners(
-						new PostCollectionRecreateEventListener[] { (PostCollectionRecreateEventListener) searchEventListener }
-				);
-			}
-		}
-		{
-			boolean present = false;
-			PostCollectionRemoveEventListener[] listeners = eventListeners.getPostCollectionRemoveEventListeners();
-			if ( listeners != null ) {
-				for (Object eventListener : listeners) {
-					//not isAssignableFrom since the user could subclass
-					present = present || searchEventListenerClass == eventListener.getClass();
-				}
-				if ( !present ) {
-					int length = listeners.length + 1;
-					PostCollectionRemoveEventListener[] newListeners = new PostCollectionRemoveEventListener[length];
-					System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-					newListeners[length - 1] = (PostCollectionRemoveEventListener) searchEventListener;
-					eventListeners.setPostCollectionRemoveEventListeners( newListeners );
-				}
-			}
-			else {
-				eventListeners.setPostCollectionRemoveEventListeners(
-						new PostCollectionRemoveEventListener[] { (PostCollectionRemoveEventListener) searchEventListener }
-				);
-			}
-		}
-		{
-			boolean present = false;
-			PostCollectionUpdateEventListener[] listeners = eventListeners.getPostCollectionUpdateEventListeners();
-			if ( listeners != null ) {
-				for (Object eventListener : listeners) {
-					//not isAssignableFrom since the user could subclass
-					present = present || searchEventListenerClass == eventListener.getClass();
-				}
-				if ( !present ) {
-					int length = listeners.length + 1;
-					PostCollectionUpdateEventListener[] newListeners = new PostCollectionUpdateEventListener[length];
-					System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-					newListeners[length - 1] = (PostCollectionUpdateEventListener) searchEventListener;
-					eventListeners.setPostCollectionUpdateEventListeners( newListeners );
-				}
-			}
-			else {
-				eventListeners.setPostCollectionUpdateEventListeners(
-						new PostCollectionUpdateEventListener[] { (PostCollectionUpdateEventListener) searchEventListener }
-				);
-			}
-		}
-	}
-
-	/**
-	 * Tries to load Hibernate Search event listener.
-	 *
-	 * @return An event listener instance in case the jar was available.
-	 */
-	private static Class<?> attemptToLoadSearchEventListener() {
-		Class searchEventListenerClass = null;
-		try {
-			searchEventListenerClass = ReflectHelper.classForName(
-					FULL_TEXT_INDEX_EVENT_LISTENER_CLASS,
-					HibernateSearchEventListenerRegister.class);
-		} catch (ClassNotFoundException e) {
-            LOG.debugf("Search not present in classpath, ignoring event listener registration.");
-		}
-		return searchEventListenerClass;
-	}
-
-	private static Object instantiateEventListener(Class<?> clazz) {
-		Object searchEventListener;
-		try {
-			searchEventListener = clazz.newInstance();
-		} catch (Exception e) {
-			throw new AnnotationException(
-					"Unable to load Search event listener", e);
-		}
-		return searchEventListener;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
new file mode 100644
index 0000000000..766652e5cf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
@@ -0,0 +1,129 @@
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
+package org.hibernate.cfg.search;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.AnnotationException;
+import org.hibernate.HibernateLogger;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.SessionFactoryImplementor;
+import org.hibernate.event.EventType;
+import org.hibernate.event.PostCollectionRecreateEventListener;
+import org.hibernate.event.PostCollectionRemoveEventListener;
+import org.hibernate.event.PostCollectionUpdateEventListener;
+import org.hibernate.event.PostDeleteEventListener;
+import org.hibernate.event.PostInsertEventListener;
+import org.hibernate.event.PostUpdateEventListener;
+import org.hibernate.impl.Integrator;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.event.spi.DuplicationStrategy;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+
+/**
+ * Integrates Hibernate Search into Hibernate Core by registering its needed listeners
+ * <p/>
+ * The note on the original (now removed) org.hibernate.cfg.search.HibernateSearchEventListenerRegister class indicated
+ * that Search now uses a new means for this.  However that signature is relying on removed classes...
+ *
+ * @author Emmanuel Bernard
+ * @author Hardy Ferentschik
+ * @author Steve Ebersole
+ */
+public class HibernateSearchIntegrator implements Integrator {
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, HibernateSearchIntegrator.class.getName() );
+
+	public static final String AUTO_REGISTER = "hibernate.search.autoregister_listeners";
+	public static final String LISTENER_CLASS = "org.hibernate.search.event.FullTextIndexEventListener";
+
+	@Override
+	public void integrate(
+			Configuration configuration,
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		final boolean registerListeners = ConfigurationHelper.getBoolean( AUTO_REGISTER, configuration.getProperties(), false );
+		if ( !registerListeners ) {
+			LOG.debug( "Skipping search event listener auto registration" );
+			return;
+		}
+
+		final Class listenerClass = loadSearchEventListener( serviceRegistry );
+		if ( listenerClass == null ) {
+			LOG.debug( "Skipping search event listener auto registration - could not fid listener class" );
+			return;
+		}
+
+		final Object listener = instantiateListener( listenerClass );
+
+		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+
+		listenerRegistry.addDuplicationStrategy( new DuplicationStrategyImpl( listenerClass ) );
+
+		listenerRegistry.getEventListenerGroup( EventType.POST_INSERT ).appendListener( (PostInsertEventListener) listener );
+		listenerRegistry.getEventListenerGroup( EventType.POST_UPDATE ).appendListener( (PostUpdateEventListener) listener );
+		listenerRegistry.getEventListenerGroup( EventType.POST_DELETE ).appendListener( (PostDeleteEventListener) listener );
+		listenerRegistry.getEventListenerGroup( EventType.POST_COLLECTION_RECREATE ).appendListener( (PostCollectionRecreateEventListener) listener );
+		listenerRegistry.getEventListenerGroup( EventType.POST_COLLECTION_REMOVE ).appendListener( (PostCollectionRemoveEventListener) listener );
+		listenerRegistry.getEventListenerGroup( EventType.POST_COLLECTION_UPDATE ).appendListener( (PostCollectionUpdateEventListener) listener );
+	}
+
+	private Class loadSearchEventListener(SessionFactoryServiceRegistry serviceRegistry) {
+		try {
+			return serviceRegistry.getService( ClassLoaderService.class ).classForName( LISTENER_CLASS );
+		}
+		catch (Exception e) {
+			return null;
+		}
+	}
+
+	private Object instantiateListener(Class listenerClass) {
+		try {
+			return listenerClass.newInstance();
+		}
+		catch (Exception e) {
+			throw new AnnotationException( "Unable to instantiate Search event listener", e );
+		}
+	}
+
+	public static class DuplicationStrategyImpl implements DuplicationStrategy {
+		private final Class checkClass;
+
+		public DuplicationStrategyImpl(Class checkClass) {
+			this.checkClass = checkClass;
+		}
+
+		@Override
+		public boolean areMatch(Object listener, Object original) {
+			// not isAssignableFrom since the user could subclass
+			return checkClass == original.getClass() && checkClass == listener.getClass();
+		}
+
+		@Override
+		public Action getAction() {
+			return Action.KEEP_ORIGINAL;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index abd77f22c3..e6f081ddd5 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1373 +1,1379 @@
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
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.net.URL;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.jboss.logging.Logger;
 
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
+import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
+import org.hibernate.cfg.beanvalidation.LegacyHibernateValidationIntegrator;
+import org.hibernate.cfg.search.HibernateSearchIntegrator;
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
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
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
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient Statistics statistics;
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
         LOG.buildingSessionFactory();
 
 		// todo : move stats building to SF service reg building
 		this.statistics = buildStatistics( settings, serviceRegistry );
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 		this.interceptor = cfg.getInterceptor();
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
 		this.settings = settings;
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
 
 		for ( Integrator integrator : locateIntegrators( this.serviceRegistry ) ) {
 			integrator.integrate( cfg, this, (SessionFactoryServiceRegistry) this.serviceRegistry );
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
 
 	private Iterable<Integrator> locateIntegrators(ServiceRegistryImplementor serviceRegistry) {
 		List<Integrator> integrators = new ArrayList<Integrator>();
 
-		// todo : add "known" integrators -> BV, hibernate validation, search, envers
+		// todo : Envers needs to bbe handled by discovery to be because it is in a separate project
+		integrators.add( new LegacyHibernateValidationIntegrator() );
+		integrators.add( new BeanValidationIntegrator() );
+		integrators.add( new HibernateSearchIntegrator() );
 
 		final Properties properties = new Properties();
 
 		ClassLoaderService classLoader = serviceRegistry.getService( ClassLoaderService.class );
 		List<URL> urls = classLoader.locateResources( "META-INF/hibernate/org.hibernate.impl.Integrator" );
 		for ( URL url : urls ) {
 			try {
 				final InputStream propertyStream = url.openStream();
 				try {
 					properties.clear();
 					properties.load( propertyStream );
 					// for now we only understand 'implClass' as key
 					final String implClass = properties.getProperty( "implClass" );
 					Class integratorClass = classLoader.classForName( implClass );
 					try {
 						integrators.add( (Integrator) integratorClass.newInstance() );
 					}
 					catch (Exception e) {
 						throw new HibernateException( "Unable to instantiate specified Integrator class [" + implClass + "]", e );
 					}
 				}
 				finally {
 					try {
 						propertyStream.close();
 					}
 					catch (IOException ignore) {
 					}
 				}
 			}
 			catch ( IOException ioe ) {
 				LOG.debugf( ioe, "Unable to process Integrator service file [%s], skipping" , url.toExternalForm() );
 			}
 		}
 		return integrators;
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
         LOG.debugf( "Returning a Reference to the SessionFactory" );
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
 
 		SessionFactoryObjectFactory.removeInstance(uuid, name, properties);
 
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
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
index c8f9fc7440..0bf0195e02 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceImpl.java
@@ -1,191 +1,192 @@
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
+
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
+
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * Standard implementation of the service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceImpl implements ClassLoaderService {
 	public static final String APP_CL = "hibernate.classLoader.application";
 	public static final String RESOURCES_CL = "hibernate.classLoader.resources";
 	public static final String HIB_CL = "hibernate.classLoader.hibernate";
 	public static final String ENV_CL = "hibernate.classLoader.environment";
 
 	private final LinkedHashSet<ClassLoader> classLoadingClassLoaders;
 	private final ClassLoader resourcesClassLoader;
 
 	public ClassLoaderServiceImpl(Map configVales) {
 		this( determineClassLoaders( configVales ) );
 	}
 
-	private ClassLoaderServiceImpl(ClassLoader[] classLoaders) {
+	private ClassLoaderServiceImpl(ClassLoader... classLoaders) {
 		this( classLoaders[0], classLoaders[1], classLoaders[2], classLoaders[3] );
 	}
 
 	private static ClassLoader[] determineClassLoaders(Map configVales) {
 		ClassLoader applicationClassLoader = (ClassLoader) configVales.get( APP_CL );
 		ClassLoader resourcesClassLoader = (ClassLoader) configVales.get( RESOURCES_CL );
 		ClassLoader hibernateClassLoader = (ClassLoader) configVales.get( HIB_CL );
 		ClassLoader environmentClassLoader = (ClassLoader) configVales.get( ENV_CL );
 
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
-			catch ( Exception e ) {
+			catch ( Exception ignore) {
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
-		catch ( Exception e ) {
+		catch ( Exception ignore ) {
 		}
 
 		try {
 			return resourcesClassLoader.getResource( name );
 		}
-		catch ( Exception e ) {
+		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public InputStream locateResourceStream(String name) {
 		// first we try name as a URL
 		try {
 			return new URL( name ).openStream();
 		}
-		catch ( Exception e ) {
+		catch ( Exception ignore ) {
 		}
 
 		try {
 			return resourcesClassLoader.getResourceAsStream( name );
 		}
-		catch ( Exception e ) {
+		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public List<URL> locateResources(String name) {
+		ArrayList<URL> urls = new ArrayList<URL>();
 		try {
 			Enumeration<URL> urlEnumeration = resourcesClassLoader.getResources( name );
 			if ( urlEnumeration != null && urlEnumeration.hasMoreElements() ) {
-				ArrayList<URL> urls = new ArrayList<URL>();
 				while ( urlEnumeration.hasMoreElements() ) {
 					urls.add( urlEnumeration.nextElement() );
 				}
-				return urls;
 			}
 		}
-		catch ( Exception e ) {
+		catch ( Exception ignore ) {
 		}
 
-		return null;
+		return urls;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
index 34a79ebf52..454515193b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
@@ -1,128 +1,128 @@
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
 package org.hibernate.test.events;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.Destructible;
 import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.Initializable;
 import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 
 /**
  * CallbackTest implementation
  *
  * @author Steve Ebersole
  */
 public class CallbackTest extends BaseCoreFunctionalTestCase {
 	private TestingObserver observer = new TestingObserver();
 	private TestingListener listener = new TestingListener();
 
 	public String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setSessionFactoryObserver( observer );
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
 		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
 				new EventListenerRegistration() {
 					@Override
 					public void apply(
 							EventListenerRegistry eventListenerRegistry,
 							Configuration configuration,
-							Map<?, ?> configValues, ServiceRegistryImplementor serviceRegistry
-					) {
-						serviceRegistry.getService( EventListenerRegistry.class ).setListeners( EventType.DELETE, listener );
+							Map<?, ?> configValues,
+							ServiceRegistryImplementor serviceRegistry) {
+						eventListenerRegistry.setListeners( EventType.DELETE, listener );
 					}
 				}
 		);
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-5913", message = "Need to figure out how to initialize/destroy event listeners now")
 	public void testCallbacks() {
 		assertEquals( "observer not notified of creation", 1, observer.creationCount );
 		assertEquals( "listener not notified of creation", 1, listener.initCount );
 
 		sessionFactory().close();
 
 		assertEquals( "observer not notified of close", 1, observer.closedCount );
 		assertEquals( "listener not notified of close", 1, listener.destoryCount );
 	}
 
 	private static class TestingObserver implements SessionFactoryObserver {
 		private int creationCount = 0;
 		private int closedCount = 0;
 
 		public void sessionFactoryCreated(SessionFactory factory) {
 			creationCount++;
 		}
 
 		public void sessionFactoryClosed(SessionFactory factory) {
 			closedCount++;
 		}
 	}
 
 	private static class TestingListener implements DeleteEventListener, Initializable, Destructible {
 		private int initCount = 0;
 		private int destoryCount = 0;
 
 		public void initialize(Configuration cfg) {
 			initCount++;
 		}
 
 		public void cleanup() {
 			destoryCount++;
 		}
 
 		public void onDelete(DeleteEvent event) throws HibernateException {
 		}
 
 		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 		}
 	}
 }
