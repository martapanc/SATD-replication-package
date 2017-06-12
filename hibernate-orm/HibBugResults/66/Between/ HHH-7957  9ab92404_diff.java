diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index fdc0c029a4..6dfe1ace55 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1879 +1,1879 @@
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
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
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
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.internal.util.xml.ErrorLogger;
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
 import org.hibernate.secure.internal.JACCConfiguration;
 import org.hibernate.service.ServiceRegistry;
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
  * <p/>
  * NOTE : This will be replaced by use of {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder} and
  * {@link org.hibernate.metamodel.MetadataSources} instead after the 4.0 release at which point this class will become
  * deprecated and scheduled for removal in 5.0.  See
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6183">HHH-6183</a>,
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-2578">HHH-2578</a> and
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6586">HHH-6586</a> for details
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public class Configuration implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
 
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
 
 	private MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
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
 	private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 	private boolean specjProprietarySyntaxEnabled;
 
 	private ConcurrentHashMap<Class,AttributeConverterDefinition> attributeConverterDefinitionsByClass;
 
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
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
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
 			LOG.unableToDeserializeCache( cachedFile.getPath(), e );
 		}
 		catch ( FileNotFoundException e ) {
 			LOG.cachedFileNotFound( cachedFile.getPath(), e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
 			LOG.debugf( "Writing cache file for: %s to: %s", xmlFile, cachedFile );
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
 		}
 		catch ( Exception e ) {
 			LOG.unableToWriteCachedFile( cachedFile.getPath(), e.getMessage() );
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
 
 		LOG.readingCachedMappings( cachedFile );
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
 		LOG.debugf( "Mapping XML:\n%s", xml );
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
 
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
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
 				LOG.trace( "Was unable to close input stream");
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
 		LOG.debugf( "Mapping Document:\n%s", doc );
 
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
 		LOG.readingMappingsFromResource( resourceName );
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
 		LOG.readingMappingsFromResource( resourceName );
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
 		LOG.readingMappingsFromResource( mappingResourceName );
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
 		LOG.debugf( "Mapping Package %s", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
 			LOG.unableToParseMetadata( packageName );
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
 		LOG.searchingForMappingDocuments( jar.getName() );
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
 					LOG.foundMappingDocument( ze.getName() );
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
 				LOG.unableToCloseJar( ioe.getMessage() );
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
-	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
+	public Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
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
 
 				Iterator subIter = table.getUniqueKeyIterator();
 				while ( subIter.hasNext() ) {
 					UniqueKey uk = (UniqueKey) subIter.next();
 					String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 					if (constraintString != null) script.add( constraintString );
 				}
 
 
 				subIter = table.getIndexIterator();
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
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
 						table.isQuoted()
 				);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									tableCatalog,
 									tableSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							tableCatalog,
 							tableSchema
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
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema() ;
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						tableSchema,
 						tableCatalog,
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
 												tableCatalog,
 												tableSchema
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
 									tableCatalog,
 									tableSchema
 							)
 					);
 				}
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
 		LOG.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				Map defaults = reflectionManager.getDefaults();
 				final Object isDelimited = defaults.get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 				// Set default schema name if orm.xml declares it.
 				final String schema = (String) defaults.get( "schema" );
 				if ( StringHelper.isNotEmpty( schema ) ) {
 					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
 				}
 				// Set default catalog name if orm.xml declares it.
 				final String catalog = (String) defaults.get( "catalog" );
 				if ( StringHelper.isNotEmpty( catalog ) ) {
 					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
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
 		LOG.debug("Processing fk mappings (*ToOne and JoinedSubclass)");
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
 				String dependentTable = quotedTableName(classMapping.getTable());
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
 			String dependentTable = quotedTableName(sp.getValue().getTable());
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
 
 	private String quotedTableName(Table table) {
 		return Table.qualify( table.getCatalog(), table.getQuotedSchema(), table.getQuotedName() );
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
 			for ( FkSecondPass pass : endOfQueueFkSecondPasses ) {
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch (RecoverableException e) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = (RuntimeException) e.getCause();
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
 		UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uk.addColumn( column );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
 		LOG.debug( "Processing extends queue" );
 		processExtendsQueue();
 
 		LOG.debug( "Processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
 		LOG.debug( "Processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
 		LOG.debug( "Processing association property references" );
 
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
 
 		LOG.debug( "Creating tables' unique integer identifiers" );
 		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
 		int uniqueInteger = 0;
 		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		while ( itr.hasNext() ) {
 			Table table = (Table) itr.next();
 			table.setUniqueInteger( uniqueInteger++ );
 			secondPassCompileForeignKeys( table, done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
 		LOG.debug( "Processing extends queue" );
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuilder buf = new StringBuilder( "Following super classes referenced in extends not found: " );
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
 
 	protected void secondPassCompileForeignKeys(Table table, Set<ForeignKey> done) throws MappingException {
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
 				LOG.debugf( "Resolving reference to class: %s", referencedEntityName );
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
 	 * @param serviceRegistry The registry of services to be used in creating this session factory.
 	 *
 	 * @return The built {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		LOG.debugf( "Preparing to build session factory with filters : %s", filterDefinitions );
 
 		secondPassCompile();
 		if ( !metadataSourceQueue.isEmpty() ) {
 			LOG.incompleteMappingMetadataCacheProcessing();
 		}
 
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
 		final ServiceRegistry serviceRegistry =  new StandardServiceRegistryBuilder()
 				.applySettings( properties )
 				.build();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	/**
 	 * Retrieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory built}
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
 	 * @return The value currently associated with that property name; may be null.
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/DatabaseInfoDialectResolver.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/DatabaseInfoDialectResolver.java
index de2efd779c..9eccc26347 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/DatabaseInfoDialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/DatabaseInfoDialectResolver.java
@@ -1,78 +1,78 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.engine.jdbc.dialect.spi;
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.service.Service;
 
 /**
  * A contract for resolving database name, major version and minor version to Dialect
  *
  * @author Steve Ebersole
  */
 public interface DatabaseInfoDialectResolver extends Service {
-	public static final int NO_VERSION = -9999;
-
 	/**
 	 * Determine the {@link Dialect} to use based on the given information.  Implementations are
 	 * expected to return the {@link Dialect} instance to use, or {@code null} if the they did not locate a match.
 	 *
 	 * @param databaseInfo Access to the needed database information
 	 *
 	 * @return The dialect to use, or null.
 	 */
 	public Dialect resolve(DatabaseInfo databaseInfo);
 
 	public static interface DatabaseInfo {
+		public static final int NO_VERSION = -9999;
+
 		/**
 		 * Obtain access to the database name, as returned from {@link java.sql.DatabaseMetaData#getDatabaseProductName()}
 		 * for the target database
 		 *
 		 * @return The database name
 		 */
 		public String getDatabaseName();
 
 		/**
 		 * Obtain access to the database major version, as returned from
 		 * {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion()} for the target database; {@value #NO_VERSION}
 		 * indicates no version information was supplied
 		 *
 		 * @return The major version
 		 *
 		 * @see #NO_VERSION
 		 */
 		public int getDatabaseMajorVersion();
 
 		/**
 		 * Obtain access to the database minor version, as returned from
 		 * {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion()} for the target database; {@value #NO_VERSION}
 		 * indicates no version information was supplied
 		 *
 		 * @return The minor version
 		 *
 		 * @see #NO_VERSION
 		 */
 		public int getDatabaseMinorVersion();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
index 96dc25a88d..51548171b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/PersistentIdentifierGenerator.java
@@ -1,105 +1,108 @@
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
 package org.hibernate.id;
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 
 /**
  * An <tt>IdentifierGenerator</tt> that requires creation of database objects.
  * <br><br>
  * All <tt>PersistentIdentifierGenerator</tt>s that also implement
  * <tt>Configurable</tt> have access to a special mapping parameter: schema
  *
  * @see IdentifierGenerator
  * @see Configurable
  * @author Gavin King
  */
 public interface PersistentIdentifierGenerator extends IdentifierGenerator {
 
 	/**
 	 * The configuration parameter holding the schema name
 	 */
 	public static final String SCHEMA = "schema";
 
 	/**
 	 * The configuration parameter holding the table name for the
 	 * generated id
 	 */
 	public static final String TABLE = "target_table";
 
 	/**
 	 * The configuration parameter holding the table names for all
 	 * tables for which the id must be unique
 	 */
 	public static final String TABLES = "identity_tables";
 
 	/**
 	 * The configuration parameter holding the primary key column
 	 * name of the generated id
 	 */
 	public static final String PK = "target_column";
 
     /**
      * The configuration parameter holding the catalog name
      */
     public static final String CATALOG = "catalog";
 
 	/**
 	 * The key under whcih to find the {@link org.hibernate.cfg.ObjectNameNormalizer} in the config param map.
 	 */
 	public static final String IDENTIFIER_NORMALIZER = "identifier_normalizer";
 
 	/**
 	 * The SQL required to create the underlying database objects.
 	 *
 	 * @param dialect The dialect against which to generate the create command(s)
 	 * @return The create command(s)
 	 * @throws HibernateException problem creating the create command(s)
 	 */
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException;
 
 	/**
 	 * The SQL required to remove the underlying database objects.
 	 *
 	 * @param dialect The dialect against which to generate the drop command(s)
 	 * @return The drop command(s)
 	 * @throws HibernateException problem creating the drop command(s)
 	 */
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException;
 
 	/**
 	 * Return a key unique to the underlying database objects. Prevents us from
 	 * trying to create/remove them multiple times.
 	 * 
 	 * @return Object an identifying key for this generator
 	 */
 	public Object generatorKey();
 
+	public String getSchema();
+
+	public String getCatalog();
 }
 
 
 
 
 
 
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
index 35ea9ba54f..4c68a1f5eb 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
@@ -1,357 +1,524 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa;
 
 
+import org.hibernate.internal.util.StringHelper;
+
 /**
  * Defines the available HEM settings, both JPA-defined as well as Hibernate-specific
  * <p/>
  * NOTE : Does *not* include {@link org.hibernate.cfg.Environment} values.
  *
  * @author Steve Ebersole
  */
 public interface AvailableSettings {
 
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
 
 	/**
 	 * Used to pass along the CDI BeanManager, if any, to be used.
 	 */
 	public static final String CDI_BEAN_MANAGER = "javax.persistence.bean.manager";
 
+	/**
+	 * Specifies the action to be taken by the persistence provider.  The set of possible values are:<ul>
+	 *     <li>none</li>
+	 *     <li>create</li>
+	 *     <li>drop</li>
+	 *     <li>drop-and-create</li>
+	 * </ul>
+	 *
+	 * If no value is specified, the default is "none".
+	 *
+	 * @see SchemaGenAction
+	 */
+	public static final String SCHEMA_GEN_ACTION = "javax.persistence.schema-generation-action";
+
+	/**
+	 * Specifies whether the schema is to be created in the database, whether scripts are to be generated, or both.
+	 * The values for this property are:<ul>
+	 *     <li>database</li>
+	 *     <li>scripts</li>
+	 *     <li>database-and-scripts</li>
+	 * </ul>
+	 * If no value is specified, a default is assumed as follows:<ul>
+	 *     <li>
+	 *         if script targets are specified (per {@value #SCHEMA_GEN_CREATE_SCRIPT_TARGET} and
+	 *         {@value #SCHEMA_GEN_DROP_SCRIPT_TARGET}), then the default is assumed to be "scripts"
+	 *     </li>
+	 *     <li>
+	 *         Otherwise, "database" is assumed
+	 *     </li>
+	 * </ul>
+	 *
+	 * @see SchemaGenTarget
+	 */
+	public static final String SCHEMA_GEN_TARGET = "javax.persistence.schema-generation-target";
+
+	/**
+	 * If schema creations scripts are to be generated, the target/location for these scripts must be specified.  This
+	 * target may take the form of either a {@link java.io.Writer} or a string designating a
+	 * {@link java.net.URL}.
+	 * <p/>
+	 * Create and drop scripts are written separately (though the same Writer/URL could be passed).
+	 * {@value #SCHEMA_GEN_CREATE_SCRIPT_TARGET} specifies the target for the create script.
+	 *
+	 * @see #SCHEMA_GEN_DROP_SCRIPT_TARGET
+	 */
+	@SuppressWarnings("JavaDoc")
+	public static final String SCHEMA_GEN_CREATE_SCRIPT_TARGET = "javax.persistence.ddl-create-script-target";
+
+	/**
+	 * If schema creations scripts are to be generated, the target/location for these scripts must be specified.  This
+	 * target may take the form of either a {@link java.io.Writer} or a string designating a
+	 * {@link java.net.URL}.
+	 * <p/>
+	 * Create and drop scripts are written separately (though the same Writer/URL could be passed).
+	 * {@value #SCHEMA_GEN_DROP_SCRIPT_TARGET} specifies the target for the create script.
+	 *
+	 * @see #SCHEMA_GEN_CREATE_SCRIPT_TARGET
+	 */
+	@SuppressWarnings("JavaDoc")
+	public static final String SCHEMA_GEN_DROP_SCRIPT_TARGET = "javax.persistence.ddl-drop-script-target";
+
+	/**
+	 * Specifies whether schema generation is to occur on the basis of the object/relational mapping metadata, DDL
+	 * scripts, or a combination of the two.  The valid values for this property are: <ul>
+	 *     <li>metadata</li>
+	 *     <li>scripts</li>
+	 *     <li>metadata-then-scripts</li>
+	 *     <li>scripts-then-metadata</li>
+	 * </ul>
+	 * If no value is specified, a default is assumed as follows:<ul>
+	 *     <li>
+	 *         if source scripts are specified (per {@value #SCHEMA_GEN_CREATE_SCRIPT_SOURCE} and
+	 *         {@value #SCHEMA_GEN_DROP_SCRIPT_SOURCE}),then "scripts" is assumed
+	 *     </li>
+	 *     <li>
+	 *         otherwise, "metadata" is assumed
+	 *     </li>
+	 * </ul>
+	 *
+	 * @see SchemaGenSource
+	 */
+	public static final String SCHEMA_GEN_SOURCE = "javax.persistence.schema-generation-source";
+
+	/**
+	 * Specifies the CREATE script file as either a {@link java.io.Reader} configured for reading of the DDL script
+	 * file or a string designating a file {@link java.net.URL} for the DDL script.
+	 *
+	 * @see #SCHEMA_GEN_DROP_SCRIPT_SOURCE
+	 */
+	public static final String SCHEMA_GEN_CREATE_SCRIPT_SOURCE = "javax.persistence.ddl-create-script-source";
+
+	/**
+	 * Specifies the DROP script file as either a {@link java.io.Reader} configured for reading of the DDL script
+	 * file or a string designating a file {@link java.net.URL} for the DDL script.
+	 *
+	 * @see #SCHEMA_GEN_CREATE_SCRIPT_SOURCE
+	 */
+	public static final String SCHEMA_GEN_DROP_SCRIPT_SOURCE = "javax.persistence.ddl-drop-script-source";
+
+	/**
+	 * Specifies whether the persistence provider is to create the database schema(s) in addition to creating
+	 * database objects (tables, sequences, constraints, etc).  The value of this boolean property should be set
+	 * to {@code true} if the persistence provider is to create schemas in the database or to generate DDL that
+	 * contains “CREATE SCHEMA” commands.  If this property is not supplied (or is explicitly {@code false}), the
+	 * provider should not attempt to create database schemas.
+	 */
+	public static final String SCHEMA_GEN_CREATE_SCHEMAS = "javax.persistence.create-database-schemas";
+
+	/**
+	 * Allows passing the specific {@link java.sql.Connection} instance to be used for performing schema generation
+	 * where the target is "database".
+	 * <p/>
+	 * May also be used to determine the values for {@value #SCHEMA_GEN_DB_NAME},
+	 * {@value #SCHEMA_GEN_DB_MAJOR_VERSION} and {@value #SCHEMA_GEN_DB_MINOR_VERSION}.
+	 */
+	public static final String SCHEMA_GEN_CONNECTION = "javax.persistence.schema-generation-connection";
+
+	/**
+	 * Specifies the name of the database provider in cases where a Connection to the underlying database is
+	 * not available (aka, mainly in generating scripts).  In such cases, a value for
+	 * {@value #SCHEMA_GEN_DB_NAME} *must* be specified.
+	 * <p/>
+	 * The value of this setting is expected to match the value returned by
+	 * {@link java.sql.DatabaseMetaData#getDatabaseProductName()} for the target database.
+	 * <p/>
+	 * Additionally specifying {@value #SCHEMA_GEN_DB_MAJOR_VERSION} and/or {@value #SCHEMA_GEN_DB_MINOR_VERSION}
+	 * may be required to understand exactly how to generate the required schema commands.
+	 *
+	 * @see #SCHEMA_GEN_DB_MAJOR_VERSION
+	 * @see #SCHEMA_GEN_DB_MINOR_VERSION
+	 */
+	@SuppressWarnings("JavaDoc")
+	public static final String SCHEMA_GEN_DB_NAME = "javax.persistence.database-product-name";
+
+	/**
+	 * Specifies the major version of the underlying database, as would be returned by
+	 * {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion} for the target database.  This value is used to
+	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
+	 * where {@value #SCHEMA_GEN_DB_NAME} does not provide enough distinction.
+
+	 * @see #SCHEMA_GEN_DB_NAME
+	 * @see #SCHEMA_GEN_DB_MINOR_VERSION
+	 */
+	public static final String SCHEMA_GEN_DB_MAJOR_VERSION = "javax.persistence.database-major-version";
+
+	/**
+	 * Specifies the minor version of the underlying database, as would be returned by
+	 * {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion} for the target database.  This value is used to
+	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
+	 * where te combination of {@value #SCHEMA_GEN_DB_NAME} and {@value #SCHEMA_GEN_DB_MAJOR_VERSION} does not provide
+	 * enough distinction.
+	 *
+	 * @see #SCHEMA_GEN_DB_NAME
+	 * @see #SCHEMA_GEN_DB_MAJOR_VERSION
+	 */
+	public static final String SCHEMA_GEN_DB_MINOR_VERSION = "javax.persistence.database-minor-version";
+
+	/**
+	 * Specifies a {@link java.io.Reader} configured for reading of the SQL load script or a string designating the
+	 * file {@link java.net.URL} for the SQL load script.
+	 * <p/>
+	 * A "SQL load script" is a script that performs some database initialization (INSERT, etc).
+	 */
+	public static final String SCHEMA_GEN_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
+
 
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
 	 * SessionFactoryObserver class name, the class must have a no-arg constructor
 	 */
 	public static final String SESSION_FACTORY_OBSERVER = "hibernate.ejb.session_factory_observer";
 
 	/**
 	 * Naming strategy class name, the class has to have a no-arg constructor
 	 */
 	public static final String NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
 
 	/**
 	 * IdentifierGeneratorStrategyProvider class name, the class must have a no-arg constructor
 	 * @deprecated if possible wait of Hibernate 4.1 and theService registry (MutableIdentifierGeneratorStrategy service)
 	 */
 	public static final String IDENTIFIER_GENERATOR_STRATEGY_PROVIDER = "hibernate.ejb.identifier_generator_strategy_provider";
 
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
 	 * EntityManagerFactory name
 	 */
 	public static final String ENTITY_MANAGER_FACTORY_NAME = "hibernate.ejb.entitymanager_factory_name";
 
 	/**
 	 * @deprecated use {@link #JPA_METAMODEL_POPULATION} instead.
 	 */
 	@Deprecated
 	public static final String JPA_METAMODEL_GENERATION = "hibernate.ejb.metamodel.generation";
 
 	/**
 	 * Setting that controls whether we seek out JPA "static metamodel" classes and populate them.  Accepts
 	 * 3 values:<ul>
 	 *     <li>
 	 *         <b>enabled</b> - Do the population
 	 *     </li>
 	 *     <li>
 	 *         <b>disabled</b> - Do not do the population
 	 *     </li>
 	 *     <li>
 	 *         <b>ignoreUnsupported</b> - Do the population, but ignore any non-JPA features that would otherwise
 	 *         result in the population failing.
 	 *     </li>
 	 * </ul>
 	 *
 	 */
 	public static final String JPA_METAMODEL_POPULATION = "hibernate.ejb.metamodel.population";
 
 
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/HibernatePersistenceProvider.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/HibernatePersistenceProvider.java
index 961a126ac4..1e54878404 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/HibernatePersistenceProvider.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/HibernatePersistenceProvider.java
@@ -1,130 +1,141 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
  * Boston, MA  02110-1301  USA\
  */
 package org.hibernate.jpa;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.LoadState;
 import javax.persistence.spi.PersistenceProvider;
 import javax.persistence.spi.PersistenceUnitInfo;
 import javax.persistence.spi.ProviderUtil;
 import java.util.Collections;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
 import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
 import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.ProviderChecker;
 import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
 
 /**
  * The Hibernate {@link PersistenceProvider} implementation
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class HibernatePersistenceProvider implements PersistenceProvider {
 
 	private final PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Note: per-spec, the values passed as {@code properties} override values found in {@code persistence.xml}
 	 */
 	@Override
 	public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
+		final EntityManagerFactoryBuilder builder = getEntityManagerFactoryBuilderOrNull( persistenceUnitName, properties );
+		return builder == null ? null : builder.build();
+	}
+
+	private EntityManagerFactoryBuilder getEntityManagerFactoryBuilderOrNull(String persistenceUnitName, Map properties) {
 		final Map integration = wrap( properties );
 		final List<ParsedPersistenceXmlDescriptor> units = PersistenceXmlParser.locatePersistenceUnits( integration );
 
 		if ( persistenceUnitName == null && units.size() > 1 ) {
 			// no persistence-unit name to look for was given and we found multiple persistence-units
 			throw new PersistenceException( "No name provided and multiple persistence units found" );
 		}
 
 		for ( ParsedPersistenceXmlDescriptor persistenceUnit : units ) {
 			boolean matches = persistenceUnitName == null || persistenceUnit.getName().equals( persistenceUnitName );
 			if ( !matches ) {
 				continue;
 			}
 
 			// See if we (Hibernate) are the persistence provider
 			if ( ! ProviderChecker.isProvider( persistenceUnit, properties ) ) {
 				continue;
 			}
 
-			return Bootstrap.getEntityManagerFactoryBuilder( persistenceUnit, integration ).build();
+			return Bootstrap.getEntityManagerFactoryBuilder( persistenceUnit, integration );
 		}
 
 		return null;
 	}
 
 	@SuppressWarnings("unchecked")
 	private static Map wrap(Map properties) {
 		return properties == null ? Collections.emptyMap() : Collections.unmodifiableMap( properties );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Note: per-spec, the values passed as {@code properties} override values found in {@link PersistenceUnitInfo}
 	 */
 	@Override
 	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map integration) {
 		return Bootstrap.getEntityManagerFactoryBuilder( info, integration ).build();
 	}
 
 	@Override
 	public void generateSchema(PersistenceUnitInfo info, Map map) {
-		// todo : implement
+		EntityManagerFactoryBuilder builder = Bootstrap.getEntityManagerFactoryBuilder( info, map );
+		builder.generateSchema();
 	}
 
 	@Override
 	public boolean generateSchema(String persistenceUnitName, Map map) {
-		// todo : implement
-		return false;
+		final EntityManagerFactoryBuilder builder = getEntityManagerFactoryBuilderOrNull( persistenceUnitName, map );
+		if ( builder == null ) {
+			return false;
+		}
+		builder.generateSchema();
+		return true;
 	}
 
 	private final ProviderUtil providerUtil = new ProviderUtil() {
 		@Override
 		public LoadState isLoadedWithoutReference(Object proxy, String property) {
 			return PersistenceUtilHelper.isLoadedWithoutReference( proxy, property, cache );
 		}
 		@Override
 		public LoadState isLoadedWithReference(Object proxy, String property) {
 			return PersistenceUtilHelper.isLoadedWithReference( proxy, property, cache );
 		}
 		@Override
 		public LoadState isLoaded(Object o) {
 			return PersistenceUtilHelper.isLoaded(o);
 		}
 	};
 
 	@Override
 	public ProviderUtil getProviderUtil() {
 		return providerUtil;
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenAction.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenAction.java
new file mode 100644
index 0000000000..6c7a7ad481
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenAction.java
@@ -0,0 +1,101 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa;
+
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * Describes the allowable values of the {@value AvailableSettings#SCHEMA_GEN_ACTION} setting.
+ *
+ * @see AvailableSettings#SCHEMA_GEN_ACTION
+ *
+ * @author Steve Ebersole
+ */
+public enum SchemaGenAction {
+	/**
+	 * "none" - no actions will be performed (aka, generation is disabled).
+	 */
+	NONE( "none" ),
+	/**
+	 * "create" - database creation will be generated
+	 */
+	CREATE( "create" ),
+	/**
+	 * "drop" - database dropping will be generated
+	 */
+	DROP( "drop" ),
+	/**
+	 * "drop-and-create" - both database creation and database dropping will be generated.
+	 */
+	BOTH( "drop-and-create" );
+
+	private final String externalName;
+
+	private SchemaGenAction(String externalName) {
+		this.externalName = externalName;
+	}
+
+	/**
+	 * Used when processing JPA configuration to interpret the {@value AvailableSettings#SCHEMA_GEN_ACTION} setting.
+	 *
+	 * @param value The encountered value of {@value AvailableSettings#SCHEMA_GEN_ACTION}
+	 *
+	 * @return The matching enum value.  An empty value will return {@link #NONE}.
+	 *
+	 * @throws IllegalArgumentException If the incoming value is unrecognized
+	 */
+	public static SchemaGenAction interpret(String value) {
+		if ( StringHelper.isEmpty( value ) ) {
+			// default is NONE
+			return NONE;
+		}
+
+		if ( CREATE.externalName.equals( value ) ) {
+			return CREATE;
+		}
+		else if ( DROP.externalName.equals( value ) ) {
+			return DROP;
+		}
+		else if ( BOTH.externalName.equals( value ) ) {
+			return BOTH;
+		}
+
+		throw new IllegalArgumentException(
+				String.format( "Unrecognized '%s' value : %s", AvailableSettings.SCHEMA_GEN_ACTION, value )
+		);
+	}
+
+	public boolean includesCreate() {
+		return this == CREATE || this == BOTH;
+	}
+
+	public boolean includesDrop() {
+		return this == DROP || this == BOTH;
+	}
+
+	@Override
+	public String toString() {
+		return getClass().getSimpleName() + "(" + externalName + ")";
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenSource.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenSource.java
new file mode 100644
index 0000000000..3634f11e87
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenSource.java
@@ -0,0 +1,103 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa;
+
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * Describes the allowable values of the {@value AvailableSettings#SCHEMA_GEN_SOURCE} setting.
+ *
+ * @see AvailableSettings#SCHEMA_GEN_SOURCE
+ *
+ * @author Steve Ebersole
+ */
+public enum SchemaGenSource {
+	/**
+	 * "metadata" - The O/RM metadata is used as the exclusive source for generation
+	 */
+	METADATA( "metadata" ),
+	/**
+	 * "scripts" - External DDL script(s) are used as the exclusive source for generation.  The scripts for schema
+	 * creation and dropping come from different sources.  The creation DDL script is identified by the
+	 * {@value AvailableSettings#SCHEMA_GEN_CREATE_SCRIPT_SOURCE} setting; the drop DDL script is identified by the
+	 * {@value AvailableSettings#SCHEMA_GEN_DROP_SCRIPT_SOURCE} setting.
+	 *
+	 * @see AvailableSettings#SCHEMA_GEN_CREATE_SCRIPT_SOURCE
+	 * @see AvailableSettings#SCHEMA_GEN_DROP_SCRIPT_SOURCE
+	 */
+	SCRIPTS( "scripts" ),
+	/**
+	 * "metadata-then-scripts" - Both the O/RM metadata and external DDL scripts are used as sources for generation,
+	 * with the O/RM metadata being applied first.
+	 *
+	 * @see #METADATA
+	 * @see #SCRIPTS
+	 */
+	METADATA_THEN_SCRIPTS( "metadata-then-scripts" ),
+	/**
+	 * "scripts-then-metadata" - Both the O/RM metadata and external DDL scripts are used as sources for generation,
+	 * with the commands from the external DDL script(s) being applied first
+	 *
+	 * @see #SCRIPTS
+	 * @see #METADATA
+	 */
+	SCRIPTS_THEN_METADATA( "scripts-then-metadata" );
+
+	private final String externalName;
+
+	private SchemaGenSource(String externalName) {
+		this.externalName = externalName;
+	}
+
+	/**
+	 * Used when processing JPA configuration to interpret the {@value AvailableSettings#SCHEMA_GEN_SOURCE} setting.
+	 *
+	 * @param value The encountered value of {@value AvailableSettings#SCHEMA_GEN_SOURCE}
+	 *
+	 * @return The matching enum value.  An empty value will return {@code null}.
+	 *
+	 * @throws IllegalArgumentException If the incoming value is unrecognized
+	 */
+	public static SchemaGenSource interpret(String value) {
+		if ( StringHelper.isEmpty( value ) ) {
+			// empty is in fact valid as means to interpret default value based on other settings
+			return null;
+		}
+
+		if ( METADATA.externalName.equals( value ) ) {
+			return METADATA;
+		}
+		else if ( SCRIPTS.externalName.equals( value ) ) {
+			return SCRIPTS;
+		}
+		else if ( METADATA_THEN_SCRIPTS.externalName.equals( value ) ) {
+			return METADATA_THEN_SCRIPTS;
+		}
+		else if ( SCRIPTS_THEN_METADATA.externalName.equals( value ) ) {
+			return SCRIPTS_THEN_METADATA;
+		}
+
+		throw new IllegalArgumentException( "Unrecognized schema generation source value : " + value );
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenTarget.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenTarget.java
new file mode 100644
index 0000000000..fcb0d4c302
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/SchemaGenTarget.java
@@ -0,0 +1,92 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa;
+
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * Describes the allowable values of the {@value AvailableSettings#SCHEMA_GEN_TARGET} setting.
+ *
+ * @see AvailableSettings#SCHEMA_GEN_TARGET
+ *
+ * @author Steve Ebersole
+ */
+public enum SchemaGenTarget {
+	/**
+	 * "database" - Generation commands will be executed directly against the database (via JDBC Statements).
+	 */
+	DATABASE( "database" ),
+	/**
+	 * "scripts" - Generation commands will be written to script (text) "targets" as indicated by the
+	 * {@value AvailableSettings#SCHEMA_GEN_CREATE_SCRIPT_TARGET} and
+	 * {@value AvailableSettings#SCHEMA_GEN_DROP_SCRIPT_TARGET} settings.
+	 */
+	SCRIPTS( "scripts" ),
+	/**
+	 * "database-and-scripts" - Generation commands will be sent to both.
+	 *
+	 * @see #DATABASE
+	 * @see #SCRIPTS
+	 */
+	BOTH( "database-and-scripts" );
+
+	private final String externalName;
+
+	private SchemaGenTarget(String externalName) {
+		this.externalName = externalName;
+	}
+
+	/**
+	 * Used when processing JPA configuration to interpret the {@value AvailableSettings#SCHEMA_GEN_TARGET} setting.
+	 *
+	 * @param value The encountered value of {@value AvailableSettings#SCHEMA_GEN_TARGET}
+	 *
+	 * @return The matching enum value.  An empty value will return {@code null}.
+	 *
+	 * @throws IllegalArgumentException If the incoming value is unrecognized
+	 */
+	public static SchemaGenTarget interpret(String value) {
+		if ( StringHelper.isEmpty( value ) ) {
+			// empty is in fact valid as means to interpret default value based on other settings
+			return null;
+		}
+
+		if ( DATABASE.externalName.equals( value ) ) {
+			return DATABASE;
+		}
+		else if ( SCRIPTS.externalName.equals( value ) ) {
+			return SCRIPTS;
+		}
+		else if ( BOTH.externalName.equals( value ) ) {
+			return BOTH;
+		}
+
+		throw new IllegalArgumentException( "Unknown schema generation target value : " + value );
+	}
+
+	@Override
+	public String toString() {
+		return getClass().getSimpleName() + "(" + externalName + ")";
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index 5f9fe063b2..46a3167632 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,1227 +1,1254 @@
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
 package org.hibernate.jpa.boot.internal;
 
 import java.io.BufferedInputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import javax.persistence.AttributeConverter;
 import javax.persistence.Converter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.jaxb.cfg.JaxbHibernateConfiguration;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
 import org.hibernate.jpa.event.spi.JpaIntegrator;
+import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.packaging.internal.NativeScanner;
 import org.hibernate.jpa.packaging.spi.NamedInputStream;
 import org.hibernate.jpa.packaging.spi.Scanner;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.internal.JACCConfiguration;
 import org.hibernate.service.ConfigLoader;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.CompositeIndex;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.jboss.jandex.IndexView;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryBuilderImpl implements EntityManagerFactoryBuilder {
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
 			EntityManagerMessageLogger.class,
 			EntityManagerFactoryBuilderImpl.class.getName()
 	);
 
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// New settings
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	/**
 	 * Names a {@link IntegratorProvider}
 	 */
 	public static final String INTEGRATOR_PROVIDER = "hibernate.integrator_provider";
 
 	/**
 	 * Names a Jandex {@link Index} instance to use.
 	 */
 	public static final String JANDEX_INDEX = "hibernate.jandex_index";
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Explicit "injectables"
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private Object validatorFactory;
 	private DataSource dataSource;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final PersistenceUnitDescriptor persistenceUnit;
 	private final SettingsImpl settings = new SettingsImpl();
 	private final StandardServiceRegistryBuilder serviceRegistryBuilder;
 	private final Map configurationValues;
 
 	private final List<JaccDefinition> jaccDefinitions = new ArrayList<JaccDefinition>();
 	private final List<CacheRegionDefinition> cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 	// todo : would much prefer this as a local variable...
 	private final List<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping> cfgXmlNamedMappings = new ArrayList<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping>();
 	private Interceptor sessionFactoryInterceptor;
 	private NamingStrategy namingStrategy;
 	private SessionFactoryObserver suppliedSessionFactoryObserver;
 
 	private MetadataSources metadataSources;
 	private Configuration hibernateConfiguration;
 
 	private static EntityNotFoundDelegate jpaEntityNotFoundDelegate = new JpaEntityNotFoundDelegate();
 
 	private static class JpaEntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException( "Unable to find " + entityName  + " with id " + id );
 		}
 	}
 
 	public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
 		LogHelper.logPersistenceUnitInformation( persistenceUnit );
 
 		this.persistenceUnit = persistenceUnit;
 
 		if ( integrationSettings == null ) {
 			integrationSettings = Collections.emptyMap();
 		}
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// First we build the boot-strap service registry, which mainly handles class loader interactions
 		final BootstrapServiceRegistry bootstrapServiceRegistry = buildBootstrapServiceRegistry( integrationSettings );
 		// And the main service registry.  This is needed to start adding configuration values, etc
 		this.serviceRegistryBuilder = new StandardServiceRegistryBuilder( bootstrapServiceRegistry );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Next we build a merged map of all the configuration values
 		this.configurationValues = mergePropertySources( persistenceUnit, integrationSettings, bootstrapServiceRegistry );
 		// add all merged configuration values into the service registry builder
 		this.serviceRegistryBuilder.applySettings( configurationValues );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Next we do a preliminary pass at metadata processing, which involves:
 		//		1) scanning
 		ScanResult scanResult = scan( bootstrapServiceRegistry );
 		//		2) building a Jandex index
 		Set<String> collectedManagedClassNames = collectManagedClassNames( scanResult );
 		IndexView jandexIndex = locateOrBuildJandexIndex( collectedManagedClassNames, scanResult.getPackageNames(), bootstrapServiceRegistry );
 		//		3) building "metadata sources" to keep for later to use in building the SessionFactory
 		metadataSources = prepareMetadataSources( jandexIndex, collectedManagedClassNames, scanResult, bootstrapServiceRegistry );
 
 		withValidatorFactory( configurationValues.get( AvailableSettings.VALIDATION_FACTORY ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// push back class transformation to the environment; for the time being this only has any effect in EE
 		// container situations, calling back into PersistenceUnitInfo#addClassTransformer
 		final boolean useClassTransformer = "true".equals( configurationValues.remove( AvailableSettings.USE_CLASS_ENHANCER ) );
 		if ( useClassTransformer ) {
 			persistenceUnit.pushClassTransformer( metadataSources.collectMappingClassNames() );
 		}
 	}
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// temporary!
 	@SuppressWarnings("unchecked")
 	public Map getConfigurationValues() {
 		return Collections.unmodifiableMap( configurationValues );
 	}
 
 	public Configuration getHibernateConfiguration() {
 		return hibernateConfiguration;
 	}
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	@SuppressWarnings("unchecked")
 	private MetadataSources prepareMetadataSources(
 			IndexView jandexIndex,
 			Set<String> collectedManagedClassNames,
 			ScanResult scanResult,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// todo : this needs to tie into the metamodel branch...
 		MetadataSources metadataSources = new MetadataSources();
 
 		for ( String className : collectedManagedClassNames ) {
 			final ClassInfo classInfo = jandexIndex.getClassByName( DotName.createSimple( className ) );
 			if ( classInfo == null ) {
 				// Not really sure what this means.  Most likely it is explicitly listed in the persistence unit,
 				// but mapped via mapping file.  Anyway assume its a mapping class...
 				metadataSources.annotatedMappingClassNames.add( className );
 				continue;
 			}
 
 			// logic here assumes an entity is not also a converter...
 			AnnotationInstance converterAnnotation = JandexHelper.getSingleAnnotation(
 					classInfo.annotations(),
 					JPADotNames.CONVERTER
 			);
 			if ( converterAnnotation != null ) {
 				metadataSources.converterDescriptors.add(
 						new MetadataSources.ConverterDescriptor(
 								className,
 								JandexHelper.getValue( converterAnnotation, "autoApply", boolean.class )
 						)
 				);
 			}
 			else {
 				metadataSources.annotatedMappingClassNames.add( className );
 			}
 		}
 
 		metadataSources.packageNames.addAll( scanResult.getPackageNames() );
 
 		metadataSources.namedMappingFileInputStreams.addAll( scanResult.getHbmFiles() );
 
 		metadataSources.mappingFileResources.addAll( scanResult.getMappingFiles() );
 		final String explicitHbmXmls = (String) configurationValues.remove( AvailableSettings.HBXML_FILES );
 		if ( explicitHbmXmls != null ) {
 			metadataSources.mappingFileResources.addAll( Arrays.asList( StringHelper.split( ", ", explicitHbmXmls ) ) );
 		}
 		final List<String> explicitOrmXml = (List<String>) configurationValues.remove( AvailableSettings.XML_FILE_NAMES );
 		if ( explicitOrmXml != null ) {
 			metadataSources.mappingFileResources.addAll( explicitOrmXml );
 		}
 
 		return metadataSources;
 	}
 
 	private Set<String> collectManagedClassNames(ScanResult scanResult) {
 		Set<String> collectedNames = new HashSet<String>();
 		if ( persistenceUnit.getManagedClassNames() != null ) {
 			collectedNames.addAll( persistenceUnit.getManagedClassNames() );
 		}
 		collectedNames.addAll( scanResult.getManagedClassNames() );
 		return collectedNames;
 	}
 
 	private IndexView locateOrBuildJandexIndex(
 			Set<String> collectedManagedClassNames,
 			List<String> packageNames,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// for now create a whole new Index to work with, eventually we need to:
 		//		1) accept an Index as an incoming config value
 		//		2) pass that Index along to the metamodel code...
 		//
 		// (1) is mocked up here, but JBoss AS does not currently pass in any Index to use...
 		IndexView jandexIndex = (IndexView) configurationValues.get( JANDEX_INDEX );
 		if ( jandexIndex == null ) {
 			jandexIndex = buildJandexIndex( collectedManagedClassNames, packageNames, bootstrapServiceRegistry );
 		}
 		return jandexIndex;
 	}
 
 	private IndexView buildJandexIndex(Set<String> classNamesSource, List<String> packageNames, BootstrapServiceRegistry bootstrapServiceRegistry) {
 		Indexer indexer = new Indexer();
 
 		for ( String className : classNamesSource ) {
 			indexResource( className.replace( '.', '/' ) + ".class", indexer, bootstrapServiceRegistry );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : packageNames ) {
 			indexResource( packageName.replace( '.', '/' ) + "/package-info.class", indexer, bootstrapServiceRegistry );
 		}
 
 		// for now we just skip entities defined in (1) orm.xml files and (2) hbm.xml files.  this part really needs
 		// metamodel branch...
 
 		// for now, we also need to wrap this in a CompositeIndex until Jandex is updated to use a common interface
 		// between the 2...
 		return CompositeIndex.create( indexer.complete() );
 	}
 
 	private void indexResource(String resourceName, Indexer indexer, BootstrapServiceRegistry bootstrapServiceRegistry) {
 		InputStream stream = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResourceStream( resourceName );
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw persistenceException( "Unable to open input stream for resource " + resourceName, e );
 		}
 	}
 
 	/**
 	 * Builds the {@link BootstrapServiceRegistry} used to eventually build the {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}; mainly
 	 * used here during instantiation to define class-loading behavior.
 	 *
 	 * @param integrationSettings Any integration settings passed by the EE container or SE application
 	 *
 	 * @return The built BootstrapServiceRegistry
 	 */
 	private BootstrapServiceRegistry buildBootstrapServiceRegistry(Map integrationSettings) {
 		final BootstrapServiceRegistryBuilder bootstrapServiceRegistryBuilder = new BootstrapServiceRegistryBuilder();
 		bootstrapServiceRegistryBuilder.with( new JpaIntegrator() );
 
 		final IntegratorProvider integratorProvider = (IntegratorProvider) integrationSettings.get( INTEGRATOR_PROVIDER );
 		if ( integratorProvider != null ) {
 			integrationSettings.remove( INTEGRATOR_PROVIDER );
 			for ( Integrator integrator : integratorProvider.getIntegrators() ) {
 				bootstrapServiceRegistryBuilder.with( integrator );
 			}
 		}
 
 		ClassLoader classLoader = (ClassLoader) integrationSettings.get( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		if ( classLoader != null ) {
 			integrationSettings.remove( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		}
 		else {
 			classLoader = persistenceUnit.getClassLoader();
 		}
 		bootstrapServiceRegistryBuilder.withApplicationClassLoader( classLoader );
 
 		return bootstrapServiceRegistryBuilder.build();
 	}
 
 	@SuppressWarnings("unchecked")
 	private Map mergePropertySources(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			final BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Map merged = new HashMap();
 		// first, apply persistence.xml-defined settings
 		if ( persistenceUnit.getProperties() != null ) {
 			merged.putAll( persistenceUnit.getProperties() );
 		}
 
 		merged.put( AvailableSettings.PERSISTENCE_UNIT_NAME, persistenceUnit.getName() );
 
 		// see if the persistence.xml settings named a Hibernate config file....
 		final ValueHolder<ConfigLoader> configLoaderHolder = new ValueHolder<ConfigLoader>(
 				new ValueHolder.DeferredInitializer<ConfigLoader>() {
 					@Override
 					public ConfigLoader initialize() {
 						return new ConfigLoader( bootstrapServiceRegistry );
 					}
 				}
 		);
 
 		{
 			final String cfgXmlResourceName = (String) merged.remove( AvailableSettings.CFG_FILE );
 			if ( StringHelper.isNotEmpty( cfgXmlResourceName ) ) {
 				// it does, so load those properties
 				JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue()
 						.loadConfigXmlResource( cfgXmlResourceName );
 				processHibernateConfigurationElement( configurationElement, merged );
 			}
 		}
 
 		// see if integration settings named a Hibernate config file....
 		{
 			final String cfgXmlResourceName = (String) integrationSettings.get( AvailableSettings.CFG_FILE );
 			if ( StringHelper.isNotEmpty( cfgXmlResourceName ) ) {
 				integrationSettings.remove( AvailableSettings.CFG_FILE );
 				// it does, so load those properties
 				JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue().loadConfigXmlResource(
 						cfgXmlResourceName
 				);
 				processHibernateConfigurationElement( configurationElement, merged );
 			}
 		}
 
 		// finally, apply integration-supplied settings (per JPA spec, integration settings should override other sources)
 		merged.putAll( integrationSettings );
 
 		if ( ! merged.containsKey( AvailableSettings.VALIDATION_MODE ) ) {
 			if ( persistenceUnit.getValidationMode() != null ) {
 				merged.put( AvailableSettings.VALIDATION_MODE, persistenceUnit.getValidationMode() );
 			}
 		}
 
 		if ( ! merged.containsKey( AvailableSettings.SHARED_CACHE_MODE ) ) {
 			if ( persistenceUnit.getSharedCacheMode() != null ) {
 				merged.put( AvailableSettings.SHARED_CACHE_MODE, persistenceUnit.getSharedCacheMode() );
 			}
 		}
 
 		// was getting NPE exceptions from the underlying map when just using #putAll, so going this safer route...
 		Iterator itr = merged.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			if ( entry.getValue() == null ) {
 				itr.remove();
 			}
 		}
 
 		return merged;
 	}
 
 	@SuppressWarnings("unchecked")
 	private void processHibernateConfigurationElement(
 			JaxbHibernateConfiguration configurationElement,
 			Map mergeMap) {
 		if ( ! mergeMap.containsKey( org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME ) ) {
 			String cfgName = configurationElement.getSessionFactory().getName();
 			if ( cfgName != null ) {
 				mergeMap.put( org.hibernate.cfg.AvailableSettings.SESSION_FACTORY_NAME, cfgName );
 			}
 		}
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbProperty jaxbProperty : configurationElement.getSessionFactory().getProperty() ) {
 			mergeMap.put( jaxbProperty.getName(), jaxbProperty.getValue() );
 		}
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping jaxbMapping : configurationElement.getSessionFactory().getMapping() ) {
 			cfgXmlNamedMappings.add( jaxbMapping );
 		}
 
 		for ( Object cacheDeclaration : configurationElement.getSessionFactory().getClassCacheOrCollectionCache() ) {
 			if ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache.class.isInstance( cacheDeclaration ) ) {
 				final JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache jaxbClassCache
 						= (JaxbHibernateConfiguration.JaxbSessionFactory.JaxbClassCache) cacheDeclaration;
 				cacheRegionDefinitions.add(
 						new CacheRegionDefinition(
 								CacheRegionDefinition.CacheType.ENTITY,
 								jaxbClassCache.getClazz(),
 								jaxbClassCache.getUsage().value(),
 								jaxbClassCache.getRegion(),
 								"all".equals( jaxbClassCache.getInclude() )
 						)
 				);
 			}
 			else {
 				final JaxbHibernateConfiguration.JaxbSessionFactory.JaxbCollectionCache jaxbCollectionCache
 						= (JaxbHibernateConfiguration.JaxbSessionFactory.JaxbCollectionCache) cacheDeclaration;
 				cacheRegionDefinitions.add(
 						new CacheRegionDefinition(
 								CacheRegionDefinition.CacheType.COLLECTION,
 								jaxbCollectionCache.getCollection(),
 								jaxbCollectionCache.getUsage().value(),
 								jaxbCollectionCache.getRegion(),
 								false
 						)
 				);
 			}
 		}
 
 		if ( configurationElement.getSecurity() != null ) {
 			final String contextId = configurationElement.getSecurity().getContext();
 			for ( JaxbHibernateConfiguration.JaxbSecurity.JaxbGrant grant : configurationElement.getSecurity().getGrant() ) {
 				jaccDefinitions.add(
 						new JaccDefinition(
 								contextId,
 								grant.getRole(),
 								grant.getEntityName(),
 								grant.getActions()
 						)
 				);
 			}
 		}
 	}
 
 	private String jaccContextId;
 
 	private void addJaccDefinition(String key, Object value) {
 		if ( jaccContextId == null ) {
 			jaccContextId = (String) configurationValues.get( AvailableSettings.JACC_CONTEXT_ID );
 			if ( jaccContextId == null ) {
 				throw persistenceException(
 						"Entities have been configured for JACC, but "
 								+ AvailableSettings.JACC_CONTEXT_ID + " has not been set"
 				);
 			}
 		}
 
 		try {
 			final int roleStart = AvailableSettings.JACC_PREFIX.length() + 1;
 			final String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
 			final int classStart = roleStart + role.length() + 1;
 			final String clazz = key.substring( classStart, key.length() );
 
 			final JaccDefinition def = new JaccDefinition( jaccContextId, role, clazz, (String) value );
 
 			jaccDefinitions.add( def );
 
 		}
 		catch ( IndexOutOfBoundsException e ) {
 			throw persistenceException( "Illegal usage of " + AvailableSettings.JACC_PREFIX + ": " + key );
 		}
 	}
 
 	private void addCacheRegionDefinition(String role, String value, CacheRegionDefinition.CacheType cacheType) {
 		final StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			if ( cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 				error.append( AvailableSettings.CLASS_CACHE_PREFIX )
 						.append( ": " )
 						.append( AvailableSettings.CLASS_CACHE_PREFIX );
 			}
 			else {
 				error.append( AvailableSettings.COLLECTION_CACHE_PREFIX )
 						.append( ": " )
 						.append( AvailableSettings.COLLECTION_CACHE_PREFIX );
 			}
 			error.append( '.' )
 					.append( role )
 					.append( ' ' )
 					.append( value )
 					.append( ".  Was expecting configuration, but found none" );
 			throw persistenceException( error.toString() );
 		}
 
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		boolean lazyProperty = true;
 		if ( cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 		}
 		else {
 			lazyProperty = false;
 		}
 
 		final CacheRegionDefinition def = new CacheRegionDefinition( cacheType, role, usage, region, lazyProperty );
 		cacheRegionDefinitions.add( def );
 	}
 
 	@SuppressWarnings("unchecked")
 	private ScanResult scan(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		Scanner scanner = locateOrBuildScanner( bootstrapServiceRegistry );
 		ScanningContext scanningContext = new ScanningContext();
 
 		final ScanResult scanResult = new ScanResult();
 		if ( persistenceUnit.getMappingFileNames() != null ) {
 			scanResult.getMappingFiles().addAll( persistenceUnit.getMappingFileNames() );
 		}
 
 		// dunno, but the old code did it...
 		scanningContext.setSearchOrm( ! scanResult.getMappingFiles().contains( META_INF_ORM_XML ) );
 
 		if ( persistenceUnit.getJarFileUrls() != null ) {
 			prepareAutoDetectionSettings( scanningContext, false );
 			for ( URL jar : persistenceUnit.getJarFileUrls() ) {
 				scanningContext.setUrl( jar );
 				scanInContext( scanner, scanningContext, scanResult );
 			}
 		}
 
 		prepareAutoDetectionSettings( scanningContext, persistenceUnit.isExcludeUnlistedClasses() );
 		scanningContext.setUrl( persistenceUnit.getPersistenceUnitRootUrl() );
 		scanInContext( scanner, scanningContext, scanResult );
 
 		return scanResult;
 	}
 
 	@SuppressWarnings("unchecked")
 	private Scanner locateOrBuildScanner(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Object value = configurationValues.remove( AvailableSettings.SCANNER );
 		if ( value == null ) {
 			return new NativeScanner();
 		}
 
 		if ( Scanner.class.isInstance( value ) ) {
 			return (Scanner) value;
 		}
 
 		Class<? extends Scanner> scannerClass;
 		if ( Class.class.isInstance( value ) ) {
 			try {
 				scannerClass = (Class<? extends Scanner>) value;
 			}
 			catch ( ClassCastException e ) {
 				throw persistenceException( "Expecting Scanner implementation, but found " + ((Class) value).getName() );
 			}
 		}
 		else {
 			final String scannerClassName = value.toString();
 			try {
 				scannerClass = bootstrapServiceRegistry.getService( ClassLoaderService.class ).classForName( scannerClassName );
 			}
 			catch ( ClassCastException e ) {
 				throw persistenceException( "Expecting Scanner implementation, but found " + scannerClassName );
 			}
 		}
 
 		try {
 			return scannerClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw persistenceException( "Unable to instantiate Scanner class: " + scannerClass, e );
 		}
 	}
 
 	private void prepareAutoDetectionSettings(ScanningContext context, boolean excludeUnlistedClasses) {
 		final String detectionSetting = (String) configurationValues.get( AvailableSettings.AUTODETECTION );
 
 		if ( detectionSetting == null ) {
 			if ( excludeUnlistedClasses ) {
 				context.setDetectClasses( false );
 				context.setDetectHbmFiles( false );
 			}
 			else {
 				context.setDetectClasses( true );
 				context.setDetectHbmFiles( true );
 			}
 		}
 		else {
 			for ( String token : StringHelper.split( ", ", detectionSetting ) ) {
 				if ( "class".equalsIgnoreCase( token ) ) {
 					context.setDetectClasses( true );
 				}
 				if ( "hbm".equalsIgnoreCase( token ) ) {
 					context.setDetectClasses( true );
 				}
 			}
 		}
 	}
 
 	private void scanInContext(
 			Scanner scanner,
 			ScanningContext scanningContext,
 			ScanResult scanResult) {
 		if ( scanningContext.getUrl() == null ) {
 			// not sure i like just ignoring this being null, but this is exactly what the old code does...
 			LOG.containerProvidingNullPersistenceUnitRootUrl();
 			return;
 		}
 
 		try {
 			if ( scanningContext.isDetectClasses() ) {
 				Set<Package> matchingPackages = scanner.getPackagesInJar( scanningContext.url, new HashSet<Class<? extends Annotation>>(0) );
 				for ( Package pkg : matchingPackages ) {
 					scanResult.getPackageNames().add( pkg.getName() );
 				}
 
 				Set<Class<? extends Annotation>> annotationsToLookFor = new HashSet<Class<? extends Annotation>>();
 				annotationsToLookFor.add( Entity.class );
 				annotationsToLookFor.add( MappedSuperclass.class );
 				annotationsToLookFor.add( Embeddable.class );
 				annotationsToLookFor.add( Converter.class );
 				Set<Class<?>> matchingClasses = scanner.getClassesInJar( scanningContext.url, annotationsToLookFor );
 				for ( Class<?> clazz : matchingClasses ) {
 					scanResult.getManagedClassNames().add( clazz.getName() );
 				}
 			}
 
 			Set<String> patterns = new HashSet<String>();
 			if ( scanningContext.isSearchOrm() ) {
 				patterns.add( META_INF_ORM_XML );
 			}
 			if ( scanningContext.isDetectHbmFiles() ) {
 				patterns.add( "**/*.hbm.xml" );
 			}
 			if ( ! scanResult.getMappingFiles().isEmpty() ) {
 				patterns.addAll( scanResult.getMappingFiles() );
 			}
 			if ( patterns.size() != 0 ) {
 				Set<NamedInputStream> files = scanner.getFilesInJar( scanningContext.getUrl(), patterns );
 				for ( NamedInputStream file : files ) {
 					scanResult.getHbmFiles().add( file );
 					scanResult.getMappingFiles().remove( file.getName() );
 				}
 			}
 		}
 		catch (PersistenceException e ) {
 			throw e;
 		}
 		catch ( RuntimeException e ) {
 			throw persistenceException( "error trying to scan url: " + scanningContext.getUrl().toString(), e );
 		}
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory) {
 		this.validatorFactory = validatorFactory;
 
 		if ( validatorFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validatorFactory );
 		}
 		return this;
 	}
 
 	@Override
 	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource) {
 		this.dataSource = dataSource;
 
 		return this;
 	}
 
 	@Override
 	public void cancel() {
 		// todo : close the bootstrap registry (not critical, but nice to do)
 
 	}
 
+	@Override
+	public void generateSchema() {
+		processProperties();
+
+		final ServiceRegistry serviceRegistry = buildServiceRegistry();
+		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
+
+		// IMPL NOTE : TCCL handling here is temporary.
+		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
+		// 		in turn which relies on TCCL being set.
+
+		( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
+				new ClassLoaderServiceImpl.Work() {
+					@Override
+					public Object perform() {
+						final Configuration hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
+						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
+						return null;
+					}
+				}
+		);
+
+		// release this builder
+		cancel();
+	}
+
 	@SuppressWarnings("unchecked")
 	public EntityManagerFactory build() {
 		processProperties();
 
 		final ServiceRegistry serviceRegistry = buildServiceRegistry();
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// IMPL NOTE : TCCL handling here is temporary.
 		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
 		// 		in turn which relies on TCCL being set.
 
 		return ( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work<EntityManagerFactoryImpl>() {
 					@Override
 					public EntityManagerFactoryImpl perform() {
 						hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
 
 						SessionFactoryImplementor sessionFactory;
 						try {
 							sessionFactory = (SessionFactoryImplementor) hibernateConfiguration.buildSessionFactory( serviceRegistry );
 						}
 						catch (MappingException e) {
 							throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 						}
 
 						if ( suppliedSessionFactoryObserver != null ) {
 							sessionFactory.addObserver( suppliedSessionFactoryObserver );
 						}
 						sessionFactory.addObserver( new ServiceRegistryCloser() );
 
 						// NOTE : passing cfg is temporary until
 						return new EntityManagerFactoryImpl( persistenceUnit.getName(), sessionFactory, settings, configurationValues, hibernateConfiguration );
 					}
 				}
 		);
 	}
 
 	private void processProperties() {
 		applyJdbcConnectionProperties();
 		applyTransactionProperties();
 
 		Object validationFactory = this.validatorFactory;
 		if ( validationFactory == null ) {
 			validationFactory = configurationValues.get( AvailableSettings.VALIDATION_FACTORY );
 		}
 		if ( validationFactory != null ) {
 			BeanValidationIntegrator.validateFactory( validationFactory );
 			serviceRegistryBuilder.applySetting( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 		}
 
 		// flush before completion validation
 		if ( "true".equals( configurationValues.get( Environment.FLUSH_BEFORE_COMPLETION ) ) ) {
 			serviceRegistryBuilder.applySetting( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 			LOG.definingFlushBeforeCompletionIgnoredInHem( Environment.FLUSH_BEFORE_COMPLETION );
 		}
 
 		final StrategySelector strategySelector = serviceRegistryBuilder.getBootstrapServiceRegistry().getService( StrategySelector.class );
 
 		for ( Object oEntry : configurationValues.entrySet() ) {
 			Map.Entry entry = (Map.Entry) oEntry;
 			if ( entry.getKey() instanceof String ) {
 				final String keyString = (String) entry.getKey();
 
 				if ( AvailableSettings.INTERCEPTOR.equals( keyString ) ) {
 					sessionFactoryInterceptor = strategySelector.resolveStrategy( Interceptor.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.SESSION_INTERCEPTOR.equals( keyString ) ) {
 					settings.setSessionInterceptorClass(
 							loadSessionInterceptorClass( entry.getValue(), strategySelector )
 					);
 				}
 				else if ( AvailableSettings.NAMING_STRATEGY.equals( keyString ) ) {
 					namingStrategy = strategySelector.resolveStrategy( NamingStrategy.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.SESSION_FACTORY_OBSERVER.equals( keyString ) ) {
 					suppliedSessionFactoryObserver = strategySelector.resolveStrategy( SessionFactoryObserver.class, entry.getValue() );
 				}
 				else if ( AvailableSettings.DISCARD_PC_ON_CLOSE.equals( keyString ) ) {
 					settings.setReleaseResourcesOnCloseEnabled( "true".equals( entry.getValue() ) );
 				}
 				else if ( keyString.startsWith( AvailableSettings.CLASS_CACHE_PREFIX ) ) {
 					addCacheRegionDefinition(
 							keyString.substring( AvailableSettings.CLASS_CACHE_PREFIX.length() + 1 ),
 							(String) entry.getValue(),
 							CacheRegionDefinition.CacheType.ENTITY
 					);
 				}
 				else if ( keyString.startsWith( AvailableSettings.COLLECTION_CACHE_PREFIX ) ) {
 					addCacheRegionDefinition(
 							keyString.substring( AvailableSettings.COLLECTION_CACHE_PREFIX.length() + 1 ),
 							(String) entry.getValue(),
 							CacheRegionDefinition.CacheType.COLLECTION
 					);
 				}
 				else if ( keyString.startsWith( AvailableSettings.JACC_PREFIX )
 						&& ! ( keyString.equals( AvailableSettings.JACC_CONTEXT_ID )
 						|| keyString.equals( AvailableSettings.JACC_ENABLED ) ) ) {
 					addJaccDefinition( (String) entry.getKey(), entry.getValue() );
 				}
 			}
 		}
 	}
 
 	private void applyJdbcConnectionProperties() {
 		if ( dataSource != null ) {
 			serviceRegistryBuilder.applySetting( Environment.DATASOURCE, dataSource );
 		}
 		else if ( persistenceUnit.getJtaDataSource() != null ) {
 			serviceRegistryBuilder.applySetting( Environment.DATASOURCE, persistenceUnit.getJtaDataSource() );
 		}
 		else if ( persistenceUnit.getNonJtaDataSource() != null ) {
 			serviceRegistryBuilder.applySetting( Environment.DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 		}
 		else {
 			final String driver = (String) configurationValues.get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DRIVER, driver );
 			}
 			final String url = (String) configurationValues.get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.URL, url );
 			}
 			final String user = (String) configurationValues.get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.USER, user );
 			}
 			final String pass = (String) configurationValues.get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.PASS, pass );
 			}
 		}
 	}
 
 	private void applyTransactionProperties() {
 		PersistenceUnitTransactionType txnType = PersistenceUnitTransactionTypeHelper.interpretTransactionType(
 				configurationValues.get( AvailableSettings.TRANSACTION_TYPE )
 		);
 		if ( txnType == null ) {
 			txnType = persistenceUnit.getTransactionType();
 		}
 		if ( txnType == null ) {
 			// is it more appropriate to have this be based on bootstrap entry point (EE vs SE)?
 			txnType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		settings.setTransactionType( txnType );
 		boolean hasTxStrategy = configurationValues.containsKey( Environment.TRANSACTION_STRATEGY );
 		if ( hasTxStrategy ) {
 			LOG.overridingTransactionStrategyDangerous( Environment.TRANSACTION_STRATEGY );
 		}
 		else {
 			if ( txnType == PersistenceUnitTransactionType.JTA ) {
 				serviceRegistryBuilder.applySetting( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class );
 			}
 			else if ( txnType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 				serviceRegistryBuilder.applySetting( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class );
 			}
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	private Class<? extends Interceptor> loadSessionInterceptorClass(Object value, StrategySelector strategySelector) {
 		if ( value == null ) {
 			return null;
 		}
 
 		return Class.class.isInstance( value )
 				? (Class<? extends Interceptor>) value
 				: strategySelector.selectStrategyImplementor( Interceptor.class, value.toString() );
 	}
 
 	public ServiceRegistry buildServiceRegistry() {
 		return serviceRegistryBuilder.build();
 	}
 
 	public Configuration buildHibernateConfiguration(ServiceRegistry serviceRegistry) {
 		Properties props = new Properties();
 		props.putAll( configurationValues );
 		Configuration cfg = new Configuration().setProperties( props );
 
 		cfg.setEntityNotFoundDelegate( jpaEntityNotFoundDelegate );
 
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
 
 		if ( sessionFactoryInterceptor != null ) {
 			cfg.setInterceptor( sessionFactoryInterceptor );
 		}
 
 		final Object strategyProviderValue = props.get( AvailableSettings.IDENTIFIER_GENERATOR_STRATEGY_PROVIDER );
 		final IdentifierGeneratorStrategyProvider strategyProvider = strategyProviderValue == null
 				? null
 				: serviceRegistry.getService( StrategySelector.class )
 						.resolveStrategy( IdentifierGeneratorStrategyProvider.class, strategyProviderValue );
 
 		if ( strategyProvider != null ) {
 			final MutableIdentifierGeneratorFactory identifierGeneratorFactory = cfg.getIdentifierGeneratorFactory();
 			for ( Map.Entry<String,Class<?>> entry : strategyProvider.getStrategies().entrySet() ) {
 				identifierGeneratorFactory.register( entry.getKey(), entry.getValue() );
 			}
 		}
 
 		if ( jaccDefinitions != null ) {
 			for ( JaccDefinition jaccDefinition : jaccDefinitions ) {
 				JACCConfiguration jaccCfg = new JACCConfiguration( jaccDefinition.contextId );
 				jaccCfg.addPermission( jaccDefinition.role, jaccDefinition.clazz, jaccDefinition.actions );
 			}
 		}
 
 		if ( cacheRegionDefinitions != null ) {
 			for ( CacheRegionDefinition cacheRegionDefinition : cacheRegionDefinitions ) {
 				if ( cacheRegionDefinition.cacheType == CacheRegionDefinition.CacheType.ENTITY ) {
 					cfg.setCacheConcurrencyStrategy(
 							cacheRegionDefinition.role,
 							cacheRegionDefinition.usage,
 							cacheRegionDefinition.region,
 							cacheRegionDefinition.cacheLazy
 					);
 				}
 				else {
 					cfg.setCollectionCacheConcurrencyStrategy(
 							cacheRegionDefinition.role,
 							cacheRegionDefinition.usage,
 							cacheRegionDefinition.region
 					);
 				}
 			}
 		}
 
 
 		// todo : need to have this use the metamodel codebase eventually...
 
 		for ( JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping jaxbMapping : cfgXmlNamedMappings ) {
 			if ( jaxbMapping.getClazz() != null ) {
 				cfg.addAnnotatedClass(
 						serviceRegistry.getService( ClassLoaderService.class ).classForName( jaxbMapping.getClazz() )
 				);
 			}
 			else if ( jaxbMapping.getResource() != null ) {
 				cfg.addResource( jaxbMapping.getResource() );
 			}
 			else if ( jaxbMapping.getJar() != null ) {
 				cfg.addJar( new File( jaxbMapping.getJar() ) );
 			}
 			else if ( jaxbMapping.getPackage() != null ) {
 				cfg.addPackage( jaxbMapping.getPackage() );
 			}
 		}
 
 		List<Class> loadedAnnotatedClasses = (List<Class>) configurationValues.remove( AvailableSettings.LOADED_CLASSES );
 		if ( loadedAnnotatedClasses != null ) {
 			for ( Class cls : loadedAnnotatedClasses ) {
 				cfg.addAnnotatedClass( cls );
 			}
 		}
 
 		for ( String className : metadataSources.getAnnotatedMappingClassNames() ) {
 			cfg.addAnnotatedClass( serviceRegistry.getService( ClassLoaderService.class ).classForName( className ) );
 		}
 
 		for ( MetadataSources.ConverterDescriptor converterDescriptor : metadataSources.getConverterDescriptors() ) {
 			final Class<? extends AttributeConverter> converterClass;
 			try {
 				Class theClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( converterDescriptor.converterClassName );
 				converterClass = (Class<? extends AttributeConverter>) theClass;
 			}
 			catch (ClassCastException e) {
 				throw persistenceException(
 						String.format(
 								"AttributeConverter implementation [%s] does not implement AttributeConverter interface",
 								converterDescriptor.converterClassName
 						)
 				);
 			}
 			cfg.addAttributeConverter( converterClass, converterDescriptor.autoApply );
 		}
 
 		for ( String resourceName : metadataSources.mappingFileResources ) {
 			Boolean useMetaInf = null;
 			try {
 				if ( resourceName.endsWith( META_INF_ORM_XML ) ) {
 					useMetaInf = true;
 				}
 				cfg.addResource( resourceName );
 			}
 			catch( MappingNotFoundException e ) {
 				if ( ! resourceName.endsWith( META_INF_ORM_XML ) ) {
 					throw persistenceException( "Unable to find XML mapping file in classpath: " + resourceName );
 				}
 				else {
 					useMetaInf = false;
 					//swallow it, the META-INF/orm.xml is optional
 				}
 			}
 			catch( MappingException me ) {
 				throw persistenceException( "Error while reading JPA XML file: " + resourceName, me );
 			}
 
 			if ( Boolean.TRUE.equals( useMetaInf ) ) {
 				LOG.exceptionHeaderFound( getExceptionHeader(), META_INF_ORM_XML );
 			}
 			else if (Boolean.FALSE.equals(useMetaInf)) {
 				LOG.exceptionHeaderNotFound( getExceptionHeader(), META_INF_ORM_XML );
 			}
 		}
 		for ( NamedInputStream namedInputStream : metadataSources.namedMappingFileInputStreams ) {
 			try {
 				//addInputStream has the responsibility to close the stream
 				cfg.addInputStream( new BufferedInputStream( namedInputStream.getStream() ) );
 			}
 			catch (MappingException me) {
 				//try our best to give the file name
 				if ( StringHelper.isEmpty( namedInputStream.getName() ) ) {
 					throw me;
 				}
 				else {
 					throw new MappingException("Error while parsing file: " + namedInputStream.getName(), me );
 				}
 			}
 		}
 		for ( String packageName : metadataSources.packageNames ) {
 			cfg.addPackage( packageName );
 		}
 		return cfg;
 	}
 
 	public static class ServiceRegistryCloser implements SessionFactoryObserver {
 		@Override
 		public void sessionFactoryCreated(SessionFactory sessionFactory) {
 			// nothing to do
 		}
 
 		@Override
 		public void sessionFactoryClosed(SessionFactory sessionFactory) {
 			SessionFactoryImplementor sfi = ( (SessionFactoryImplementor) sessionFactory );
 			sfi.getServiceRegistry().destroy();
 			ServiceRegistry basicRegistry = sfi.getServiceRegistry().getParentServiceRegistry();
 			( (ServiceRegistryImplementor) basicRegistry ).destroy();
 		}
 	}
 
 	private PersistenceException persistenceException(String message) {
 		return persistenceException( message, null );
 	}
 
 	private PersistenceException persistenceException(String message, Exception cause) {
 		return new PersistenceException(
 				getExceptionHeader() + message,
 				cause
 		);
 	}
 
 	private String getExceptionHeader() {
 		return "[PersistenceUnit: " + persistenceUnit.getName() + "] ";
 	}
 
 	public static class CacheRegionDefinition {
 		public static enum CacheType { ENTITY, COLLECTION }
 
 		public final CacheType cacheType;
 		public final String role;
 		public final String usage;
 		public final String region;
 		public final boolean cacheLazy;
 
 		public CacheRegionDefinition(
 				CacheType cacheType,
 				String role,
 				String usage,
 				String region, boolean cacheLazy) {
 			this.cacheType = cacheType;
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.cacheLazy = cacheLazy;
 		}
 	}
 
 	public static class JaccDefinition {
 		public final String contextId;
 		public final String role;
 		public final String clazz;
 		public final String actions;
 
 		public JaccDefinition(String contextId, String role, String clazz, String actions) {
 			this.contextId = contextId;
 			this.role = role;
 			this.clazz = clazz;
 			this.actions = actions;
 		}
 	}
 
 	public static class ScanningContext {
 		private URL url;
 		private boolean detectClasses;
 		private boolean detectHbmFiles;
 		private boolean searchOrm;
 
 		public URL getUrl() {
 			return url;
 		}
 
 		public void setUrl(URL url) {
 			this.url = url;
 		}
 
 		public boolean isDetectClasses() {
 			return detectClasses;
 		}
 
 		public void setDetectClasses(boolean detectClasses) {
 			this.detectClasses = detectClasses;
 		}
 
 		public boolean isDetectHbmFiles() {
 			return detectHbmFiles;
 		}
 
 		public void setDetectHbmFiles(boolean detectHbmFiles) {
 			this.detectHbmFiles = detectHbmFiles;
 		}
 
 		public boolean isSearchOrm() {
 			return searchOrm;
 		}
 
 		public void setSearchOrm(boolean searchOrm) {
 			this.searchOrm = searchOrm;
 		}
 	}
 
 	private static class ScanResult {
 		private final List<String> managedClassNames = new ArrayList<String>();
 		private final List<String> packageNames = new ArrayList<String>();
 		private final List<NamedInputStream> hbmFiles = new ArrayList<NamedInputStream>();
 		private final List<String> mappingFiles = new ArrayList<String>();
 
 		public List<String> getManagedClassNames() {
 			return managedClassNames;
 		}
 
 		public List<String> getPackageNames() {
 			return packageNames;
 		}
 
 		public List<NamedInputStream> getHbmFiles() {
 			return hbmFiles;
 		}
 
 		public List<String> getMappingFiles() {
 			return mappingFiles;
 		}
 	}
 
 	public static class MetadataSources {
 		private final List<String> annotatedMappingClassNames = new ArrayList<String>();
 		private final List<ConverterDescriptor> converterDescriptors = new ArrayList<ConverterDescriptor>();
 		private final List<NamedInputStream> namedMappingFileInputStreams = new ArrayList<NamedInputStream>();
 		private final List<String> mappingFileResources = new ArrayList<String>();
 		private final List<String> packageNames = new ArrayList<String>();
 
 		public List<String> getAnnotatedMappingClassNames() {
 			return annotatedMappingClassNames;
 		}
 
 		public List<ConverterDescriptor> getConverterDescriptors() {
 			return converterDescriptors;
 		}
 
 		public List<NamedInputStream> getNamedMappingFileInputStreams() {
 			return namedMappingFileInputStreams;
 		}
 
 		public List<String> getPackageNames() {
 			return packageNames;
 		}
 
 		public List<String> collectMappingClassNames() {
 			// todo : the complete answer to this involves looking through the mapping files as well.
 			// 		Really need the metamodel branch code to do that properly
 			return annotatedMappingClassNames;
 		}
 
 		public static class ConverterDescriptor {
 			private final String converterClassName;
 			private final boolean autoApply;
 
 			public ConverterDescriptor(String converterClassName, boolean autoApply) {
 				this.converterClassName = converterClassName;
 				this.autoApply = autoApply;
 			}
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/spi/EntityManagerFactoryBuilder.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/spi/EntityManagerFactoryBuilder.java
index abd50f6a94..dc560c8a82 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/spi/EntityManagerFactoryBuilder.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/spi/EntityManagerFactoryBuilder.java
@@ -1,76 +1,81 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.boot.spi;
 
 import javax.persistence.EntityManagerFactory;
 import javax.sql.DataSource;
 
 /**
  * Represents a 2-phase JPA bootstrap process for building a Hibernate EntityManagerFactory.
  *
  * The first phase is the process of instantiating this builder.  During the first phase, loading of Class references
  * is highly discouraged.
  *
  * The second phase is building the EntityManagerFactory instance via {@link #build}.
  *
  * If anything goes wrong during either phase and the bootstrap process needs to be aborted, {@link #cancel()} should
  * be called.
  *
  * @author Steve Ebersole
  * @author Scott Marlow
  */
 public interface EntityManagerFactoryBuilder {
 	/**
 	 * Allows passing in a Java EE ValidatorFactory (delayed from constructing the builder, AKA phase 2) to be used
 	 * in building the EntityManagerFactory
 	 *
 	 * @param validatorFactory The ValidatorFactory
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public EntityManagerFactoryBuilder withValidatorFactory(Object validatorFactory);
 
 	/**
 	 * Allows passing in a DataSource (delayed from constructing the builder, AKA phase 2) to be used
 	 * in building the EntityManagerFactory
 	 *
 	 * @param dataSource The DataSource to use
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public EntityManagerFactoryBuilder withDataSource(DataSource dataSource);
 
 	/**
 	 * Build {@link EntityManagerFactory} instance
 	 *
 	 * @return The built {@link EntityManagerFactory}
 	 */
 	public EntityManagerFactory build();
 
 	/**
 	 * Cancel the building processing.  This is used to signal the builder to release any resources in the case of
 	 * something having gone wrong during the bootstrap process
 	 */
 	public void cancel();
+
+	/**
+	 * Perform an explicit schema generation (rather than an "auto" one) based on the
+	 */
+	public void generateSchema();
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/DatabaseTarget.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/DatabaseTarget.java
new file mode 100644
index 0000000000..457bc8c99c
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/DatabaseTarget.java
@@ -0,0 +1,102 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import javax.persistence.PersistenceException;
+import java.sql.SQLException;
+import java.sql.Statement;
+
+import org.jboss.logging.Logger;
+
+/**
+ * GenerationTarget implementation for handling generation directly to the database
+ *
+ * @see org.hibernate.jpa.SchemaGenTarget#DATABASE
+ * @see org.hibernate.jpa.SchemaGenTarget#BOTH
+ *
+ * @author Steve Ebersole
+ */
+class DatabaseTarget implements GenerationTarget {
+	private static final Logger log = Logger.getLogger( DatabaseTarget.class );
+
+	private final JdbcConnectionContext jdbcConnectionContext;
+
+	private Statement jdbcStatement;
+
+	DatabaseTarget(JdbcConnectionContext jdbcConnectionContext) {
+		this.jdbcConnectionContext = jdbcConnectionContext;
+	}
+
+	@Override
+	public void acceptCreateCommands(Iterable<String> commands) {
+		for ( String command : commands ) {
+			try {
+				jdbcStatement().execute( command );
+			}
+			catch (SQLException e) {
+				throw new PersistenceException(
+						"Unable to execute JPA schema generation create command [" + command + "]"
+				);
+			}
+		}
+	}
+
+	private Statement jdbcStatement() {
+		if ( jdbcStatement == null ) {
+			try {
+				jdbcStatement = jdbcConnectionContext.getJdbcConnection().createStatement();
+			}
+			catch (SQLException e) {
+				throw new PersistenceException( "Unable to generate JDBC Statement object for schema generation" );
+			}
+		}
+		return jdbcStatement;
+	}
+
+	@Override
+	public void acceptDropCommands(Iterable<String> commands) {
+		for ( String command : commands ) {
+			try {
+				jdbcStatement().execute( command );
+			}
+			catch (SQLException e) {
+				throw new PersistenceException(
+						"Unable to execute JPA schema generation drop command [" + command + "]"
+				);
+			}
+		}
+	}
+
+	@Override
+	public void release() {
+		if ( jdbcStatement != null ) {
+			try {
+				jdbcStatement.close();
+			}
+			catch (SQLException e) {
+				log.debug( "Unable to close JDBC statement after JPA schema generation : " + e.toString() );
+			}
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/FileScriptSource.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/FileScriptSource.java
new file mode 100644
index 0000000000..297c90fb8e
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/FileScriptSource.java
@@ -0,0 +1,75 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import javax.persistence.PersistenceException;
+import java.io.File;
+import java.io.FileReader;
+import java.io.IOException;
+import java.io.Reader;
+
+import org.jboss.logging.Logger;
+
+/**
+ * SqlScriptReader implementation for File references.  A reader is opened here and then explicitly closed on
+ * {@link #reader}.
+ *
+ * @author Steve Ebersole
+ */
+class FileScriptSource extends ReaderScriptSource implements SqlScriptReader {
+	private static final Logger log = Logger.getLogger( FileScriptSource.class );
+
+	public FileScriptSource(String fileUrl) {
+		super( toFileReader( fileUrl ) );
+	}
+
+	@Override
+	public void release() {
+		try {
+			reader().close();
+		}
+		catch (IOException e) {
+			log.warn( "Unable to close file reader for generation script source" );
+		}
+	}
+
+	@SuppressWarnings("ResultOfMethodCallIgnored")
+	private static Reader toFileReader(String fileUrl) {
+		final File file = new File( fileUrl );
+		try {
+			// best effort, since this is very well not allowed in EE environments
+			file.createNewFile();
+		}
+		catch (Exception e) {
+			log.debug( "Exception calling File#createNewFile : " + e.toString() );
+		}
+		try {
+			return new FileReader( file );
+		}
+		catch (IOException e) {
+			throw new PersistenceException( "Unable to open specified script target file for writing : " + fileUrl );
+		}
+	}
+
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/GenerationSource.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/GenerationSource.java
new file mode 100644
index 0000000000..549661919e
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/GenerationSource.java
@@ -0,0 +1,53 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+/**
+ * Contract describing a generation source for create commands
+ *
+ * @see org.hibernate.jpa.SchemaGenSource
+ * @see org.hibernate.jpa.AvailableSettings#SCHEMA_GEN_SOURCE
+ *
+ * @author Steve Ebersole
+ */
+interface GenerationSource {
+	/**
+	 * Retrieve the create generation commands from this source.
+	 *
+	 * @return The generation commands
+	 */
+	public Iterable<String> getCreateCommands();
+
+	/**
+	 * Retrieve the drop generation commands from this source
+	 *
+	 * @return The generation commands
+	 */
+	public Iterable<String> getDropCommands();
+
+	/**
+	 * Release this source.  Give it a change to release its resources, if any.
+	 */
+	public void release();
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/GenerationTarget.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/GenerationTarget.java
new file mode 100644
index 0000000000..6e674455fc
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/GenerationTarget.java
@@ -0,0 +1,53 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+/**
+ * Describes a schema generation target
+ *
+ * @see org.hibernate.jpa.SchemaGenTarget
+ * @see org.hibernate.jpa.AvailableSettings#SCHEMA_GEN_TARGET
+ *
+ * @author Steve Ebersole
+ */
+interface GenerationTarget {
+	/**
+	 * Accept a group of create generation commands
+	 *
+	 * @param commands The commands
+	 */
+	public void acceptCreateCommands(Iterable<String> commands);
+
+	/**
+	 * Accept a group of drop generation commands.
+	 *
+	 * @param commands The commands
+	 */
+	public void acceptDropCommands(Iterable<String> commands);
+
+	/**
+	 * Release this target, giving it a change to release its resources.
+	 */
+	public void release();
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/JdbcConnectionContext.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/JdbcConnectionContext.java
new file mode 100644
index 0000000000..cc6d90c534
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/JdbcConnectionContext.java
@@ -0,0 +1,67 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import javax.persistence.PersistenceException;
+import java.sql.Connection;
+import java.sql.SQLException;
+
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
+
+/**
+ * Defines access to a JDBC Connection for use in Schema generation
+ *
+ * @author Steve Ebersole
+ */
+class JdbcConnectionContext {
+	private final JdbcConnectionAccess jdbcConnectionAccess;
+	private Connection jdbcConnection;
+
+	JdbcConnectionContext(JdbcConnectionAccess jdbcConnectionAccess) {
+		this.jdbcConnectionAccess = jdbcConnectionAccess;
+	}
+
+	public Connection getJdbcConnection() {
+		if ( jdbcConnection == null ) {
+			try {
+				this.jdbcConnection = jdbcConnectionAccess.obtainConnection();
+			}
+			catch (SQLException e) {
+				throw new PersistenceException( "Unable to obtain JDBC Connection", e );
+			}
+		}
+		return jdbcConnection;
+	}
+
+	public void release() {
+		if ( jdbcConnection != null ) {
+			try {
+				jdbcConnectionAccess.releaseConnection( jdbcConnection );
+			}
+			catch (SQLException e) {
+				throw new PersistenceException( "Unable to release JDBC Connection", e );
+			}
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/JpaSchemaGenerator.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/JpaSchemaGenerator.java
new file mode 100644
index 0000000000..011f6bc4dc
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/JpaSchemaGenerator.java
@@ -0,0 +1,428 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import javax.persistence.PersistenceException;
+import java.io.Reader;
+import java.sql.Connection;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.engine.jdbc.dialect.spi.DatabaseInfoDialectResolver;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.id.IdentifierGenerator;
+import org.hibernate.id.PersistentIdentifierGenerator;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.jpa.AvailableSettings;
+import org.hibernate.jpa.SchemaGenAction;
+import org.hibernate.jpa.SchemaGenSource;
+import org.hibernate.jpa.SchemaGenTarget;
+import org.hibernate.mapping.Table;
+import org.hibernate.service.ServiceRegistry;
+import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
+
+/**
+ * Class responsible for the JPA-defined schema generation behavior.
+ *
+ * @author Steve Ebersole
+ */
+public class JpaSchemaGenerator {
+	private static final Logger log = Logger.getLogger( JpaSchemaGenerator.class );
+
+	public static void performGeneration(Configuration hibernateConfiguration, ServiceRegistry serviceRegistry) {
+
+		// First, determine the actions (if any) to be performed ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+		final SchemaGenAction action = SchemaGenAction.interpret(
+				hibernateConfiguration.getProperty( AvailableSettings.SCHEMA_GEN_ACTION )
+		);
+		if ( action == SchemaGenAction.NONE ) {
+			// no generation requested
+			return;
+		}
+
+
+		// Figure out the JDBC Connection context, if any ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+		final JdbcConnectionContext jdbcConnectionContext = determineAppropriateJdbcConnectionContext(
+				hibernateConfiguration,
+				serviceRegistry
+		);
+
+		final Dialect dialect = determineDialect( jdbcConnectionContext, hibernateConfiguration, serviceRegistry );
+
+		final ImportSqlCommandExtractor scriptCommandExtractor = serviceRegistry.getService( ImportSqlCommandExtractor.class );
+
+
+		// Next, determine the targets ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		final List<GenerationTarget> generationTargetList = new ArrayList<GenerationTarget>();
+
+		SchemaGenTarget target = SchemaGenTarget.interpret(
+				hibernateConfiguration.getProperty( AvailableSettings.SCHEMA_GEN_TARGET )
+		);
+
+		// the default is dependent upon whether script targets were also specified...
+		final Object createScriptTargetSetting = hibernateConfiguration.getProperties().get(
+				AvailableSettings.SCHEMA_GEN_CREATE_SCRIPT_TARGET
+		);
+		final Object dropScriptTargetSetting = hibernateConfiguration.getProperties().get(
+				AvailableSettings.SCHEMA_GEN_CREATE_SCRIPT_TARGET
+		);
+
+		if ( target == null ) {
+			if ( createScriptTargetSetting != null && dropScriptTargetSetting != null ) {
+				target = SchemaGenTarget.SCRIPTS;
+			}
+			else {
+				target = SchemaGenTarget.DATABASE;
+			}
+		}
+
+		if ( target == SchemaGenTarget.DATABASE || target == SchemaGenTarget.BOTH ) {
+			generationTargetList.add( new DatabaseTarget( jdbcConnectionContext ) );
+		}
+		if ( target == SchemaGenTarget.SCRIPTS || target == SchemaGenTarget.BOTH ) {
+			// both create and drop scripts are expected per JPA spec
+			if ( createScriptTargetSetting == null ) {
+				throw new IllegalArgumentException( "For schema generation creation script target missing" );
+			}
+			if ( dropScriptTargetSetting == null ) {
+				throw new IllegalArgumentException( "For schema generation drop script target missing" );
+			}
+			generationTargetList.add( new ScriptsTarget( createScriptTargetSetting, dropScriptTargetSetting ) );
+		}
+
+
+		// determine sources ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		final List<GenerationSource> generationSourceList = new ArrayList<GenerationSource>();
+
+		SchemaGenSource source = SchemaGenSource.interpret(
+				hibernateConfiguration.getProperty( AvailableSettings.SCHEMA_GEN_SOURCE )
+		);
+
+		// the default for sources is dependent upon whether script sources were specified...
+		final Object createScriptSourceSetting = hibernateConfiguration.getProperties().get(
+				AvailableSettings.SCHEMA_GEN_CREATE_SCRIPT_SOURCE
+		);
+		final Object dropScriptSourceSetting = hibernateConfiguration.getProperties().get(
+				AvailableSettings.SCHEMA_GEN_DROP_SCRIPT_SOURCE
+		);
+
+		if ( source == null ) {
+			if ( createScriptSourceSetting != null && dropScriptSourceSetting != null ) {
+				source = SchemaGenSource.SCRIPTS;
+			}
+			else {
+				source = SchemaGenSource.METADATA;
+			}
+		}
+
+		final boolean createSchemas = ConfigurationHelper.getBoolean(
+				AvailableSettings.SCHEMA_GEN_CREATE_SCHEMAS,
+				hibernateConfiguration.getProperties(),
+				false
+		);
+		if ( createSchemas ) {
+			// todo : does it make sense to generate schema(s) defined in metadata if only script sources are to be used?
+			generationSourceList.add( new CreateSchemaCommandSource( hibernateConfiguration, dialect ) );
+		}
+
+		if ( source == SchemaGenSource.METADATA ) {
+			generationSourceList.add( new MetadataSource( hibernateConfiguration, dialect ) );
+		}
+		else if ( source == SchemaGenSource.SCRIPTS ) {
+			generationSourceList.add( new ScriptSource( createScriptSourceSetting, dropScriptSourceSetting, scriptCommandExtractor ) );
+		}
+		else if ( source == SchemaGenSource.METADATA_THEN_SCRIPTS ) {
+			generationSourceList.add( new MetadataSource( hibernateConfiguration, dialect ) );
+			generationSourceList.add( new ScriptSource( createScriptSourceSetting, dropScriptSourceSetting, scriptCommandExtractor ) );
+		}
+		else if ( source == SchemaGenSource.SCRIPTS_THEN_METADATA ) {
+			generationSourceList.add( new ScriptSource( createScriptSourceSetting, dropScriptSourceSetting, scriptCommandExtractor ) );
+			generationSourceList.add( new MetadataSource( hibernateConfiguration, dialect ) );
+		}
+
+		final Object importScriptSetting = hibernateConfiguration.getProperties().get(
+				AvailableSettings.SCHEMA_GEN_LOAD_SCRIPT_SOURCE
+		);
+		if ( importScriptSetting != null ) {
+			generationSourceList.add( new ImportScriptSource( importScriptSetting, scriptCommandExtractor ) );
+		}
+
+
+		// do the generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+		try {
+			doGeneration( action, generationSourceList, generationTargetList );
+		}
+		finally {
+			releaseResources( generationSourceList, generationTargetList, jdbcConnectionContext );
+		}
+	}
+
+
+	private static JdbcConnectionContext determineAppropriateJdbcConnectionContext(
+			Configuration hibernateConfiguration,
+			ServiceRegistry serviceRegistry) {
+		// see if a specific connection has been provided:
+		final Connection providedConnection = (Connection) hibernateConfiguration.getProperties().get(
+				AvailableSettings.SCHEMA_GEN_CONNECTION
+		);
+
+		if ( providedConnection != null ) {
+			return new JdbcConnectionContext(
+					new JdbcConnectionAccess() {
+						@Override
+						public Connection obtainConnection() throws SQLException {
+							return providedConnection;
+						}
+
+						@Override
+						public void releaseConnection(Connection connection) throws SQLException {
+							// do nothing
+						}
+
+						@Override
+						public boolean supportsAggressiveRelease() {
+							return false;
+						}
+					}
+			);
+		}
+
+		final ConnectionProvider connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
+		if ( connectionProvider != null ) {
+			return new JdbcConnectionContext(
+					new JdbcConnectionAccess() {
+						@Override
+						public Connection obtainConnection() throws SQLException {
+							return connectionProvider.getConnection();
+						}
+
+						@Override
+						public void releaseConnection(Connection connection) throws SQLException {
+							connectionProvider.closeConnection( connection );
+						}
+
+						@Override
+						public boolean supportsAggressiveRelease() {
+							return connectionProvider.supportsAggressiveRelease();
+						}
+					}
+			);
+		}
+
+		// otherwise, return a no-op impl
+		return new JdbcConnectionContext( null ) {
+			@Override
+			public Connection getJdbcConnection() {
+				throw new PersistenceException( "No connection information supplied" );
+			}
+		};
+	}
+
+	private static Dialect determineDialect(
+			JdbcConnectionContext jdbcConnectionContext,
+			Configuration hibernateConfiguration,
+			ServiceRegistry serviceRegistry) {
+		final String explicitDbName = hibernateConfiguration.getProperty( AvailableSettings.SCHEMA_GEN_DB_NAME );
+		final String explicitDbMajor = hibernateConfiguration.getProperty( AvailableSettings.SCHEMA_GEN_DB_MAJOR_VERSION );
+		final String explicitDbMinor = hibernateConfiguration.getProperty( AvailableSettings.SCHEMA_GEN_DB_MINOR_VERSION );
+
+		if ( StringHelper.isNotEmpty( explicitDbName ) ) {
+			serviceRegistry.getService( DatabaseInfoDialectResolver.class ).resolve(
+					new DatabaseInfoDialectResolver.DatabaseInfo() {
+						@Override
+						public String getDatabaseName() {
+							return explicitDbName;
+						}
+
+						@Override
+						public int getDatabaseMajorVersion() {
+							return StringHelper.isEmpty( explicitDbMajor )
+									? NO_VERSION
+									: Integer.parseInt( explicitDbMajor );
+						}
+
+						@Override
+						public int getDatabaseMinorVersion() {
+							return StringHelper.isEmpty( explicitDbMinor )
+									? NO_VERSION
+									: Integer.parseInt( explicitDbMinor );
+						}
+					}
+			);
+		}
+
+		return serviceRegistry.getService( JdbcServices.class ).getDialect();
+	}
+
+	private static void doGeneration(
+			SchemaGenAction action,
+			List<GenerationSource> generationSourceList,
+			List<GenerationTarget> generationTargetList) {
+
+		for ( GenerationSource source : generationSourceList ) {
+			if ( action.includesCreate() ) {
+				final Iterable<String> createCommands = source.getCreateCommands();
+				for ( GenerationTarget target : generationTargetList ) {
+					target.acceptCreateCommands( createCommands );
+				}
+			}
+
+			if ( action.includesDrop() ) {
+				final Iterable<String> dropCommands = source.getDropCommands();
+				for ( GenerationTarget target : generationTargetList ) {
+					target.acceptDropCommands( dropCommands );
+				}
+			}
+		}
+	}
+
+	private static void releaseResources(
+			List<GenerationSource> generationSourceList,
+			List<GenerationTarget> generationTargetList,
+			JdbcConnectionContext jdbcConnectionContext) {
+		for ( GenerationTarget target : generationTargetList ) {
+			try {
+				target.release();
+			}
+			catch (Exception e) {
+				log.debug( "Problem releasing generation target : " + e.toString() );
+			}
+		}
+
+		for ( GenerationSource source : generationSourceList ) {
+			try {
+				source.release();
+			}
+			catch (Exception e) {
+				log.debug( "Problem releasing generation source : " + e.toString() );
+			}
+		}
+
+		try {
+			jdbcConnectionContext.release();
+		}
+		catch (Exception e) {
+			log.debug( "Unable to release JDBC connection after generation" );
+		}
+	}
+
+
+	private static class CreateSchemaCommandSource implements GenerationSource {
+		private final List<String> commands;
+
+		private CreateSchemaCommandSource(Configuration hibernateConfiguration, Dialect dialect) {
+			final HashSet<String> schemas = new HashSet<String>();
+//			final HashSet<String> catalogs = new HashSet<String>();
+
+			final Iterator<Table> tables = hibernateConfiguration.getTableMappings();
+			while ( tables.hasNext() ) {
+				final Table table = tables.next();
+//				catalogs.add( table.getCatalog() );
+				schemas.add( table.getSchema() );
+			}
+
+			final Iterator<IdentifierGenerator> generators = hibernateConfiguration.iterateGenerators( dialect );
+			while ( generators.hasNext() ) {
+				final IdentifierGenerator generator = generators.next();
+				if ( PersistentIdentifierGenerator.class.isInstance( generator ) ) {
+//					catalogs.add( ( (PersistentIdentifierGenerator) generator ).getCatalog() );
+					schemas.add( ( (PersistentIdentifierGenerator) generator ).getCatalog() );
+				}
+			}
+
+//			if ( schemas.isEmpty() && catalogs.isEmpty() ) {
+			if ( schemas.isEmpty() ) {
+				commands = Collections.emptyList();
+				return;
+			}
+
+			commands = new ArrayList<String>();
+
+			for ( String schema : schemas ) {
+				commands.add( dialect.getCreateSchemaCommand( schema ) );
+			}
+
+			// generate "create catalog" commands
+		}
+
+		@Override
+		public Iterable<String> getCreateCommands() {
+			return commands;
+		}
+
+		@Override
+		public Iterable<String> getDropCommands() {
+			return Collections.emptyList();
+		}
+
+		@Override
+		public void release() {
+			// nothing to do
+		}
+	}
+
+	private static class ImportScriptSource implements GenerationSource {
+		private final SqlScriptReader sourceReader;
+		private final ImportSqlCommandExtractor scriptCommandExtractor;
+
+		public ImportScriptSource(Object scriptSourceSetting, ImportSqlCommandExtractor scriptCommandExtractor) {
+			this.scriptCommandExtractor = scriptCommandExtractor;
+
+			if ( Reader.class.isInstance( scriptSourceSetting ) ) {
+				sourceReader = new ReaderScriptSource( (Reader) scriptSourceSetting );
+			}
+			else {
+				sourceReader = new FileScriptSource( scriptSourceSetting.toString() );
+			}
+		}
+
+		@Override
+		public Iterable<String> getCreateCommands() {
+			return sourceReader.read( scriptCommandExtractor );
+		}
+
+		@Override
+		public Iterable<String> getDropCommands() {
+			return Collections.emptyList();
+		}
+
+		@Override
+		public void release() {
+			sourceReader.release();
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/MetadataSource.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/MetadataSource.java
new file mode 100644
index 0000000000..aaea15f222
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/MetadataSource.java
@@ -0,0 +1,57 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import java.util.Arrays;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.dialect.Dialect;
+
+/**
+ * @author Steve Ebersole
+ */
+public class MetadataSource implements GenerationSource {
+	private final Configuration hibernateConfiguration;
+	private final Dialect dialect;
+
+	public MetadataSource(Configuration hibernateConfiguration, Dialect dialect) {
+		this.hibernateConfiguration = hibernateConfiguration;
+		this.dialect = dialect;
+	}
+
+	@Override
+	public Iterable<String> getCreateCommands() {
+		return Arrays.asList( hibernateConfiguration.generateSchemaCreationScript( dialect ) );
+	}
+
+	@Override
+	public Iterable<String> getDropCommands() {
+		return Arrays.asList( hibernateConfiguration.generateDropSchemaScript( dialect ) );
+	}
+
+	@Override
+	public void release() {
+		// nothing to do
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ReaderScriptSource.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ReaderScriptSource.java
new file mode 100644
index 0000000000..8127a7abe0
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ReaderScriptSource.java
@@ -0,0 +1,63 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import java.io.Reader;
+import java.util.Arrays;
+import java.util.Collections;
+
+import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
+
+/**
+ * SqlScriptReader implementation for explicitly given Readers.  The readers are not released by this class.
+ *
+ * @author Steve Ebersole
+ */
+class ReaderScriptSource implements SqlScriptReader {
+	private final Reader reader;
+
+	public ReaderScriptSource(Reader reader) {
+		this.reader = reader;
+	}
+
+	@Override
+	public Iterable<String> read(ImportSqlCommandExtractor commandExtractor) {
+		final String[] commands = commandExtractor.extractCommands( reader );
+		if ( commands == null ) {
+			return Collections.emptyList();
+		}
+		else {
+			return Arrays.asList( commands );
+		}
+	}
+
+	@Override
+	public void release() {
+		// nothing to do here
+	}
+
+	protected Reader reader() {
+		return reader;
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ScriptSource.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ScriptSource.java
new file mode 100644
index 0000000000..c81c9fcaf4
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ScriptSource.java
@@ -0,0 +1,84 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import javax.persistence.PersistenceException;
+import java.io.File;
+import java.io.FileReader;
+import java.io.IOException;
+import java.io.Reader;
+import java.io.Writer;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ScriptSource implements GenerationSource {
+	private static final Logger log = Logger.getLogger( ScriptSource.class );
+
+	private final SqlScriptReader createSource;
+	private final SqlScriptReader dropSource;
+	private final ImportSqlCommandExtractor scriptCommandExtractor;
+
+	public ScriptSource(
+			Object createScriptSourceSetting,
+			Object dropScriptSourceSetting,
+			ImportSqlCommandExtractor scriptCommandExtractor) {
+		this.scriptCommandExtractor = scriptCommandExtractor;
+
+		if ( Reader.class.isInstance( createScriptSourceSetting ) ) {
+			createSource = new ReaderScriptSource( (Reader) createScriptSourceSetting );
+		}
+		else {
+			createSource = new FileScriptSource( createScriptSourceSetting.toString() );
+		}
+
+		if ( Writer.class.isInstance( dropScriptSourceSetting ) ) {
+			dropSource = new ReaderScriptSource( (Reader) dropScriptSourceSetting );
+		}
+		else {
+			dropSource = new FileScriptSource( dropScriptSourceSetting.toString() );
+		}
+	}
+
+	@Override
+	public Iterable<String> getCreateCommands() {
+		return createSource.read( scriptCommandExtractor );
+	}
+
+	@Override
+	public Iterable<String> getDropCommands() {
+		return dropSource.read( scriptCommandExtractor );
+	}
+
+	@Override
+	public void release() {
+		createSource.release();
+		dropSource.release();
+	}
+
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ScriptsTarget.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ScriptsTarget.java
new file mode 100644
index 0000000000..1802f1d9d4
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/ScriptsTarget.java
@@ -0,0 +1,153 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import javax.persistence.PersistenceException;
+import java.io.File;
+import java.io.FileWriter;
+import java.io.IOException;
+import java.io.Writer;
+
+import org.jboss.logging.Logger;
+
+/**
+ * GenerationTarget implementation for handling generation to scripts
+ *
+ * @see org.hibernate.jpa.SchemaGenTarget#SCRIPTS
+ * @see org.hibernate.jpa.SchemaGenTarget#BOTH
+ *
+ * @author Steve Ebersole
+ */
+class ScriptsTarget implements GenerationTarget {
+	private static final Logger log = Logger.getLogger( ScriptsTarget.class );
+
+	private final ScriptTargetTarget createScriptTarget;
+	private final ScriptTargetTarget dropScriptTarget;
+
+	public ScriptsTarget(Object createScriptTargetSetting, Object dropScriptTargetSetting) {
+		if ( Writer.class.isInstance( createScriptTargetSetting ) ) {
+			createScriptTarget = new WriterScriptTarget( (Writer) createScriptTargetSetting );
+		}
+		else {
+			createScriptTarget = new FileScriptTarget( createScriptTargetSetting.toString() );
+		}
+
+		if ( Writer.class.isInstance( dropScriptTargetSetting ) ) {
+			dropScriptTarget = new WriterScriptTarget( (Writer) dropScriptTargetSetting );
+		}
+		else {
+			dropScriptTarget = new FileScriptTarget( dropScriptTargetSetting.toString() );
+		}
+	}
+
+	@Override
+	public void acceptCreateCommands(Iterable<String> commands) {
+		for ( String command : commands ) {
+			createScriptTarget.accept( command );
+		}
+	}
+
+	@Override
+	public void acceptDropCommands(Iterable<String> commands) {
+		for ( String command : commands ) {
+			dropScriptTarget.accept( command );
+		}
+	}
+
+	@Override
+	public void release() {
+		createScriptTarget.release();
+		dropScriptTarget.release();
+	}
+
+	/**
+	 * Internal contract for handling Writer/File differences
+	 */
+	private static interface ScriptTargetTarget {
+		public void accept(String command);
+		public void release();
+	}
+
+	private static class WriterScriptTarget implements ScriptTargetTarget {
+		private final Writer writer;
+
+		public WriterScriptTarget(Writer writer) {
+			this.writer = writer;
+		}
+
+		@Override
+		public void accept(String command) {
+			try {
+				writer.write( command );
+				writer.flush();
+			}
+			catch (IOException e) {
+				throw new PersistenceException( "Could not write to target script file", e );
+			}
+		}
+
+		@Override
+		public void release() {
+			// nothing to do for a supplied writer
+		}
+
+		protected Writer writer() {
+			return writer;
+		}
+	}
+
+	private static class FileScriptTarget extends WriterScriptTarget implements ScriptTargetTarget {
+		public FileScriptTarget(String fileUrl) {
+			super( toFileWriter( fileUrl ) );
+		}
+
+		@Override
+		public void release() {
+			try {
+				writer().close();
+			}
+			catch (IOException e) {
+				throw new PersistenceException( "Unable to close file writer : " + e.toString() );
+			}
+		}
+	}
+
+	@SuppressWarnings("ResultOfMethodCallIgnored")
+	private static Writer toFileWriter(String fileUrl) {
+		final File file = new File( fileUrl );
+		try {
+			// best effort, since this is very well not allowed in EE environments
+			file.createNewFile();
+		}
+		catch (Exception e) {
+			log.debug( "Exception calling File#createNewFile : " + e.toString() );
+		}
+		try {
+			return new FileWriter( file );
+		}
+		catch (IOException e) {
+			throw new PersistenceException( "Unable to open specified script target file for writing : " + fileUrl );
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/SqlScriptReader.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/SqlScriptReader.java
new file mode 100644
index 0000000000..d36cb2d93b
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/schemagen/SqlScriptReader.java
@@ -0,0 +1,36 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.internal.schemagen;
+
+import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
+
+/**
+ * Contract for handling Reader/File differences
+ *
+ * @author Steve Ebersole
+ */
+public interface SqlScriptReader {
+	public Iterable<String> read(ImportSqlCommandExtractor commandExtractor);
+	public void release();
+}
