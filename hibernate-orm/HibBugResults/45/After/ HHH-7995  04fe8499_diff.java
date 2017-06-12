diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index e92dc075b7..5f770cb4f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,3512 +1,3556 @@
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
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.jdbc.spi.JdbcServices;
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
 import org.hibernate.internal.util.ClassLoaderHelper;
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
+import org.hibernate.metamodel.spi.TypeContributions;
+import org.hibernate.metamodel.spi.TypeContributor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
+import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
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
+	
 	private TypeResolver typeResolver = new TypeResolver();
+	private List<TypeContributor> typeContributorRegistrations = new ArrayList<TypeContributor>();
 
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
 	private Map<Table, List<JPAIndexHolder>> jpaIndexHoldersByTable;
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
 		jpaIndexHoldersByTable = new HashMap<Table,List<JPAIndexHolder>>(  );
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
 		ClassLoader contextClassLoader = ClassLoaderHelper.getContextClassLoader();
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
 	public Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
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
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 * 
 	 * @deprecated Use {@link #generateSchemaUpdateScriptList(Dialect, DatabaseMetadata)} instead
 	 */
 	@SuppressWarnings({ "unchecked" })
 	@Deprecated
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		List<SchemaUpdateScript> scripts = generateSchemaUpdateScriptList( dialect, databaseMetadata );
 		return SchemaUpdateScript.toStringArray( scripts );
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
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public List<SchemaUpdateScript> generateSchemaUpdateScriptList(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		UniqueConstraintSchemaUpdateStrategy constraintMethod = UniqueConstraintSchemaUpdateStrategy.interpret( properties
 				.get( Environment.UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY ) );
 
 		List<SchemaUpdateScript> scripts = new ArrayList<SchemaUpdateScript>();
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema();
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), tableSchema,
 						tableCatalog, table.isQuoted() );
 				if ( tableInfo == null ) {
 					scripts.add( new SchemaUpdateScript( table.sqlCreateString( dialect, mapping, tableCatalog,
 							tableSchema ), false ) );
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings( dialect, mapping, tableInfo, tableCatalog,
 							tableSchema );
 					while ( subiter.hasNext() ) {
 						scripts.add( new SchemaUpdateScript( subiter.next(), false ) );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					scripts.add( new SchemaUpdateScript( comments.next(), false ) );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema();
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), tableSchema,
 						tableCatalog, table.isQuoted() );
 
 				if (! constraintMethod.equals( UniqueConstraintSchemaUpdateStrategy.SKIP )) {
 					Iterator uniqueIter = table.getUniqueKeyIterator();
 					while ( uniqueIter.hasNext() ) {
 						final UniqueKey uniqueKey = (UniqueKey) uniqueIter.next();
 						// Skip if index already exists. Most of the time, this
 						// won't work since most Dialects use Constraints. However,
 						// keep it for the few that do use Indexes.
 						if ( tableInfo != null && StringHelper.isNotEmpty( uniqueKey.getName() ) ) {
 							final IndexMetadata meta = tableInfo.getIndexMetadata( uniqueKey.getName() );
 							if ( meta != null ) {
 								continue;
 							}
 						}
 						String constraintString = uniqueKey.sqlCreateString( dialect, mapping, tableCatalog, tableSchema );
 						if ( constraintString != null && !constraintString.isEmpty() )
 							if ( constraintMethod.equals( UniqueConstraintSchemaUpdateStrategy.DROP_RECREATE_QUIETLY ) ) {
 								String constraintDropString = uniqueKey.sqlDropString( dialect, tableCatalog, tableCatalog );
 								scripts.add( new SchemaUpdateScript( constraintDropString, true) );
 							}
 							scripts.add( new SchemaUpdateScript( constraintString, true) );
 					}
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || ( tableInfo.getForeignKeyMetadata( fk ) == null && (
 							// Icky workaround for MySQL bug:
 									!( dialect instanceof MySQLDialect ) || tableInfo.getIndexMetadata( fk.getName() ) == null ) );
 							if ( create ) {
 								scripts.add( new SchemaUpdateScript( fk.sqlCreateString( dialect, mapping,
 										tableCatalog, tableSchema ), false ) );
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
 					scripts.add( new SchemaUpdateScript( index.sqlCreateString( dialect, mapping, tableCatalog,
 							tableSchema ), false ) );
 				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				scripts.addAll( SchemaUpdateScript.fromStringArray( lines, false ) );
 			}
 		}
 
 		return scripts;
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
 		
 		// TEMPORARY
 		// Ensure the correct ClassLoader is used in commons-annotations.
 		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 		Thread.currentThread().setContextClassLoader( ClassLoaderHelper.getContextClassLoader() );
 
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
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? StringHelper.randomFixedLengthHex("UK_")
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 		
 		for(Table table : jpaIndexHoldersByTable.keySet()){
 			final List<JPAIndexHolder> jpaIndexHolders = jpaIndexHoldersByTable.get( table );
 			int uniqueIndexPerTable = 0;
 			for ( JPAIndexHolder holder : jpaIndexHolders ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "idx_"+table.getName()+"_" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns(), holder.getOrdering(), holder.isUnique() );
 			}
 		}
 		
 		Thread.currentThread().setContextClassLoader( tccl );
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
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames){
 		buildUniqueKeyFromColumnNames( table, keyName, columnNames, null, true );
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames, String[] orderings, boolean unique) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			String column = columnNames[index];
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( column, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( column ) );
 			}
 		}
 		if ( unique ) {
 			UniqueKey uk = table.getOrCreateUniqueKey( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					uk.addColumn( column, order );
 					unbound.remove( column );
 				}
 			}
 		}
 		else {
 			Index index = table.getOrCreateIndex( keyName );
 			for ( int i = 0; i < columns.length; i++ ) {
 				Column column = columns[i];
 				String order = orderings != null ? orderings[i] : null;
 				if ( table.containsColumn( column ) ) {
 					index.addColumn( column, order );
 					unbound.remove( column );
 				}
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
 				sb.append("'").append( column.getName() ).append( "', " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append("'").append( column.getName() ).append( "', " );
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
-
+		
+		buildTypeRegistrations( serviceRegistry );
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
+	
+	private void buildTypeRegistrations(ServiceRegistry serviceRegistry) {
+		final TypeContributions typeContributions = new TypeContributions() {
+			@Override
+			public void contributeType(BasicType type) {
+				typeResolver.registerTypeOverride( type );
+			}
+
+			@Override
+			public void contributeType(UserType type, String[] keys) {
+				typeResolver.registerTypeOverride( type, keys );
+			}
+
+			@Override
+			public void contributeType(CompositeUserType type, String[] keys) {
+				typeResolver.registerTypeOverride( type, keys );
+			}
+		};
+
+		// add Dialect contributed types
+		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
+		dialect.contributeTypes( typeContributions, serviceRegistry );
+
+		// add TypeContributor contributed types.
+		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
+		for ( TypeContributor contributor : classLoaderService.loadJavaServices( TypeContributor.class ) ) {
+			contributor.contribute( typeContributions, serviceRegistry );
+		}
+		// from app registrations
+		for ( TypeContributor contributor : typeContributorRegistrations ) {
+			contributor.contribute( typeContributions, serviceRegistry );
+		}
+	}
 
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
 			LOG.debugf( "%s=%s", name, value );
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
 		LOG.configuringFromResource( resource );
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
 		LOG.configurationResource( resource );
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
 		LOG.configuringFromUrl( url );
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
 		LOG.configuringFromFile( configFile.getName() );
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
 			ErrorLogger errorLogger = new ErrorLogger( resourceName );
 			Document document = xmlHelper.createSAXReader( errorLogger,  entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errorLogger.hasErrors() ) {
 				throw new MappingException( "invalid configuration", errorLogger.getErrors().get( 0 ) );
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
 				LOG.unableToCloseInputStreamForResource( resourceName, ioe );
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
 
 		LOG.configuredSessionFactory( name );
 		LOG.debugf( "Properties: %s", properties );
 
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
 			LOG.debugf( "Session-factory config [%s] named resource [%s] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named file [%s] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named package [%s] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named class [%s] for mapping", name, className );
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
 
 	private JaccPermissionDeclarations jaccPermissionDeclarations;
 
 	private void parseSecurity(Element secNode) {
 		final String nodeContextId = secNode.attributeValue( "context" );
 
 		final String explicitContextId = getProperty( AvailableSettings.JACC_CONTEXT_ID );
 		if ( explicitContextId == null ) {
 			setProperty( AvailableSettings.JACC_CONTEXT_ID, nodeContextId );
 			LOG.jaccContextId( nodeContextId );
 		}
 		else {
 			// if they dont match, throw an error
 			if ( ! nodeContextId.equals( explicitContextId ) ) {
 				throw new HibernateException( "Non-matching JACC context ids" );
 			}
 		}
 		jaccPermissionDeclarations = new JaccPermissionDeclarations( nodeContextId );
 
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			final Element grantElement = (Element) grantElements.next();
 			final String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jaccPermissionDeclarations.addPermissionDeclaration(
 						new GrantedPermission(
 								grantElement.attributeValue( "role" ),
 								grantElement.attributeValue( "entity-name" ),
 								grantElement.attributeValue( "actions" )
 						)
 				);
 			}
 		}
 	}
 
 	public JaccPermissionDeclarations getJaccPermissionDeclarations() {
 		return jaccPermissionDeclarations;
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
 	 * @param serviceRegistry The registry of services to be used in building these settings.
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
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
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
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( functionName.toLowerCase(), function );
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
 
+	public void registerTypeContributor(TypeContributor typeContributor) {
+		typeContributorRegistrations.add( typeContributor );
+	}
+
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 	public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
 		this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
 		final AttributeConverter attributeConverter;
 		try {
 			attributeConverter = attributeConverterClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]"
 			);
 		}
 		addAttributeConverter( attributeConverter, autoApply );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 * @param autoApply Should the AttributeConverter be auto applied to property types as specified
 	 * by its "entity attribute" parameterized type?
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
 		if ( attributeConverterDefinitionsByClass == null ) {
 			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
 		}
 
 		final Object old = attributeConverterDefinitionsByClass.put(
 				attributeConverter.getClass(),
 				new AttributeConverterDefinition( attributeConverter, autoApply )
 		);
 
 		if ( old != null ) {
 			throw new AssertionFailure(
 					"AttributeConverter class [" + attributeConverter.getClass() + "] registered multiple times"
 			);
 		}
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
 	@SuppressWarnings( {"deprecation", "unchecked"})
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
 			LOG.debugf( "Added %s with class %s", typeName, typeClass );
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
 			return getLogicalTableName( table.getQuotedSchema(), table.getQuotedCatalog(), table.getQuotedName() );
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
 			StringBuilder keyBuilder = new StringBuilder();
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
 						currentTable.getQuotedSchema(), currentTable.getQuotedCatalog(), currentTable.getQuotedName()
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
 						currentTable.getQuotedSchema(), currentTable.getQuotedCatalog(), currentTable.getQuotedName()
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
 
 		@Override
 		public AttributeConverterDefinition locateAttributeConverter(Class converterClass) {
 			if ( attributeConverterDefinitionsByClass == null ) {
 				return null;
 			}
 			return attributeConverterDefinitionsByClass.get( converterClass );
 		}
 
 		@Override
 		public java.util.Collection<AttributeConverterDefinition> getAttributeConverters() {
 			if ( attributeConverterDefinitionsByClass == null ) {
 				return Collections.emptyList();
 			}
 			return attributeConverterDefinitionsByClass.values();
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
 
 		public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
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
 
 		private Boolean useNewGeneratorMappings;
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
 		}
 
 		private Boolean useNationalizedCharacterData;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean useNationalizedCharacterData() {
 			if ( useNationalizedCharacterData == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NATIONALIZED_CHARACTER_DATA );
 				useNationalizedCharacterData = Boolean.valueOf( booleanName );
 			}
 			return useNationalizedCharacterData.booleanValue();
 		}
 
 		private Boolean forceDiscriminatorInSelectsByDefault;
 
 		@Override
 		@SuppressWarnings( {"UnnecessaryUnboxing"})
 		public boolean forceDiscriminatorInSelectsByDefault() {
 			if ( forceDiscriminatorInSelectsByDefault == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT );
 				forceDiscriminatorInSelectsByDefault = Boolean.valueOf( booleanName );
 			}
 			return forceDiscriminatorInSelectsByDefault.booleanValue();
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
 					LOG.duplicateGeneratorName( old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				LOG.duplicateGeneratorTable( name );
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
 				LOG.duplicateJoins( persistentClass.getEntityName() );
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
 
 		public void addJpaIndexHolders(Table table, List<JPAIndexHolder> holders) {
 			List<JPAIndexHolder> holderList = jpaIndexHoldersByTable.get( table );
 			if ( holderList == null ) {
 				holderList = new ArrayList<JPAIndexHolder>();
 				jpaIndexHoldersByTable.put( table, holderList );
 			}
 			holderList.addAll( holders );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/spi/TypeContributor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/spi/TypeContributor.java
index 004caef5e6..4a04173faa 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/spi/TypeContributor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/spi/TypeContributor.java
@@ -1,41 +1,44 @@
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
 package org.hibernate.metamodel.spi;
 
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Contract for contributing types.
  *
  * @author Steve Ebersole
+ * 
+ * NOTE: Cherry-pick of HHH-7998 from metamodel.  For merging simplicity, just
+ * keep it in the o.h.metamodel.spi package.
  */
 public interface TypeContributor {
 	/**
 	 * Contribute types
 	 *
 	 * @param typeContributions The callback for adding contributed types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry);
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index 433aa11465..eb3fe79eba 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,1277 +1,1294 @@
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
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
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
 import org.hibernate.jpa.boot.scan.internal.StandardScanOptions;
 import org.hibernate.jpa.boot.scan.internal.StandardScanner;
 import org.hibernate.jpa.boot.scan.spi.ScanOptions;
 import org.hibernate.jpa.boot.scan.spi.ScanResult;
 import org.hibernate.jpa.boot.scan.spi.Scanner;
 import org.hibernate.jpa.boot.spi.ClassDescriptor;
 import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
 import org.hibernate.jpa.boot.spi.InputStreamAccess;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.MappingFileDescriptor;
 import org.hibernate.jpa.boot.spi.NamedInputStream;
 import org.hibernate.jpa.boot.spi.PackageDescriptor;
 import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
 import org.hibernate.jpa.boot.spi.StrategyRegistrationProviderList;
+import org.hibernate.jpa.boot.spi.TypeContributorList;
 import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
+import org.hibernate.metamodel.spi.TypeContributor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.ConfigLoader;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.ClassInfo;
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
 	 * Names a {@link StrategyRegistrationProviderList}
 	 */
 	public static final String STRATEGY_REGISTRATION_PROVIDERS = "hibernate.strategy_registration_provider";
+	
+	/**
+	 * Names a {@link TypeContributorList}
+	 */
+	public static final String TYPE_CONTRIBUTORS = "hibernate.type_contributors";
 
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
 
 	private final List<GrantedPermission> grantedJaccPermissions = new ArrayList<GrantedPermission>();
 	private final List<CacheRegionDefinition> cacheRegionDefinitions = new ArrayList<CacheRegionDefinition>();
 	// todo : would much prefer this as a local variable...
 	private final List<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping> cfgXmlNamedMappings = new ArrayList<JaxbHibernateConfiguration.JaxbSessionFactory.JaxbMapping>();
 	private Interceptor sessionFactoryInterceptor;
 	private NamingStrategy namingStrategy;
 	private SessionFactoryObserver suppliedSessionFactoryObserver;
 
 	private MetadataSources metadataSources;
 	private Configuration hibernateConfiguration;
 
 	private static EntityNotFoundDelegate jpaEntityNotFoundDelegate = new JpaEntityNotFoundDelegate();
 	
 	private ClassLoader providedClassLoader;
 
 	private static class JpaEntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException( "Unable to find " + entityName  + " with id " + id );
 		}
 	}
 
 	public EntityManagerFactoryBuilderImpl(PersistenceUnitDescriptor persistenceUnit, Map integrationSettings) {
 		this( persistenceUnit, integrationSettings, null );
 	}
 
 	public EntityManagerFactoryBuilderImpl(
 			PersistenceUnitDescriptor persistenceUnit,
 			Map integrationSettings,
 			ClassLoader providedClassLoader ) {
 		
 		LogHelper.logPersistenceUnitInformation( persistenceUnit );
 
 		this.persistenceUnit = persistenceUnit;
 
 		if ( integrationSettings == null ) {
 			integrationSettings = Collections.emptyMap();
 		}
 		
 		this.providedClassLoader = providedClassLoader;
 
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
 		final ScanResult scanResult = scan( bootstrapServiceRegistry );
 		final DeploymentResources deploymentResources = buildDeploymentResources( scanResult, bootstrapServiceRegistry );
 		//		2) building a Jandex index
 		final IndexView jandexIndex = locateOrBuildJandexIndex( deploymentResources );
 		//		3) building "metadata sources" to keep for later to use in building the SessionFactory
 		metadataSources = prepareMetadataSources( jandexIndex, deploymentResources, bootstrapServiceRegistry );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		withValidatorFactory( configurationValues.get( AvailableSettings.VALIDATION_FACTORY ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// push back class transformation to the environment; for the time being this only has any effect in EE
 		// container situations, calling back into PersistenceUnitInfo#addClassTransformer
 		final boolean useClassTransformer = "true".equals( configurationValues.remove( AvailableSettings.USE_CLASS_ENHANCER ) );
 		if ( useClassTransformer ) {
 			persistenceUnit.pushClassTransformer( metadataSources.collectMappingClassNames() );
 		}
 	}
 
 	private static interface DeploymentResources {
 		public Iterable<ClassDescriptor> getClassDescriptors();
 		public Iterable<PackageDescriptor> getPackageDescriptors();
 		public Iterable<MappingFileDescriptor> getMappingFileDescriptors();
 	}
 
 	private DeploymentResources buildDeploymentResources(
 			ScanResult scanResult,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 
 		// mapping files ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final ArrayList<MappingFileDescriptor> mappingFileDescriptors = new ArrayList<MappingFileDescriptor>();
 
 		final Set<String> nonLocatedMappingFileNames = new HashSet<String>();
 		final List<String> explicitMappingFileNames = persistenceUnit.getMappingFileNames();
 		if ( explicitMappingFileNames != null ) {
 			nonLocatedMappingFileNames.addAll( explicitMappingFileNames );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : scanResult.getLocatedMappingFiles() ) {
 			mappingFileDescriptors.add( mappingFileDescriptor );
 			nonLocatedMappingFileNames.remove( mappingFileDescriptor.getName() );
 		}
 
 		for ( String name : nonLocatedMappingFileNames ) {
 			MappingFileDescriptor descriptor = buildMappingFileDescriptor( name, bootstrapServiceRegistry );
 			mappingFileDescriptors.add( descriptor );
 		}
 
 
 		// classes and packages ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		final HashMap<String, ClassDescriptor> classDescriptorMap = new HashMap<String, ClassDescriptor>();
 		final HashMap<String, PackageDescriptor> packageDescriptorMap = new HashMap<String, PackageDescriptor>();
 
 		for ( ClassDescriptor classDescriptor : scanResult.getLocatedClasses() ) {
 			classDescriptorMap.put( classDescriptor.getName(), classDescriptor );
 		}
 
 		for ( PackageDescriptor packageDescriptor : scanResult.getLocatedPackages() ) {
 			packageDescriptorMap.put( packageDescriptor.getName(), packageDescriptor );
 		}
 
 		final List<String> explicitClassNames = persistenceUnit.getManagedClassNames();
 		if ( explicitClassNames != null ) {
 			for ( String explicitClassName : explicitClassNames ) {
 				// IMPL NOTE : explicitClassNames can contain class or package names!!!
 				if ( classDescriptorMap.containsKey( explicitClassName ) ) {
 					continue;
 				}
 				if ( packageDescriptorMap.containsKey( explicitClassName ) ) {
 					continue;
 				}
 
 				// try it as a class name first...
 				final String classFileName = explicitClassName.replace( '.', '/' ) + ".class";
 				final URL classFileUrl = bootstrapServiceRegistry.getService( ClassLoaderService.class )
 						.locateResource( classFileName );
 				if ( classFileUrl != null ) {
 					classDescriptorMap.put(
 							explicitClassName,
 							new ClassDescriptorImpl( explicitClassName, new UrlInputStreamAccess( classFileUrl ) )
 					);
 					continue;
 				}
 
 				// otherwise, try it as a package name
 				final String packageInfoFileName = explicitClassName.replace( '.', '/' ) + "/package-info.class";
 				final URL packageInfoFileUrl = bootstrapServiceRegistry.getService( ClassLoaderService.class )
 						.locateResource( packageInfoFileName );
 				if ( packageInfoFileUrl != null ) {
 					packageDescriptorMap.put(
 							explicitClassName,
 							new PackageDescriptorImpl( explicitClassName, new UrlInputStreamAccess( packageInfoFileUrl ) )
 					);
 					continue;
 				}
 
 				LOG.debugf(
 						"Unable to resolve class [%s] named in persistence unit [%s]",
 						explicitClassName,
 						persistenceUnit.getName()
 				);
 			}
 		}
 
 		return new DeploymentResources() {
 			@Override
 			public Iterable<ClassDescriptor> getClassDescriptors() {
 				return classDescriptorMap.values();
 			}
 
 			@Override
 			public Iterable<PackageDescriptor> getPackageDescriptors() {
 				return packageDescriptorMap.values();
 			}
 
 			@Override
 			public Iterable<MappingFileDescriptor> getMappingFileDescriptors() {
 				return mappingFileDescriptors;
 			}
 		};
 	}
 
 	private MappingFileDescriptor buildMappingFileDescriptor(
 			String name,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final URL url = bootstrapServiceRegistry.getService( ClassLoaderService.class ).locateResource( name );
 		if ( url == null ) {
 			throw persistenceException( "Unable to resolve named mapping-file [" + name + "]" );
 		}
 
 		return new MappingFileDescriptorImpl( name, new UrlInputStreamAccess( url ) );
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
 			DeploymentResources deploymentResources,
 			BootstrapServiceRegistry bootstrapServiceRegistry) {
 		// todo : this needs to tie into the metamodel branch...
 		MetadataSources metadataSources = new MetadataSources();
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			final String className = classDescriptor.getName();
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
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			metadataSources.packageNames.add( packageDescriptor.getName() );
 		}
 
 		for ( MappingFileDescriptor mappingFileDescriptor : deploymentResources.getMappingFileDescriptors() ) {
 			metadataSources.namedMappingFileInputStreams.add( mappingFileDescriptor.getStreamAccess().asNamedInputStream() );
 		}
 
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
 
 	private IndexView locateOrBuildJandexIndex(DeploymentResources deploymentResources) {
 		// for now create a whole new Index to work with, eventually we need to:
 		//		1) accept an Index as an incoming config value
 		//		2) pass that Index along to the metamodel code...
 		IndexView jandexIndex = (IndexView) configurationValues.get( JANDEX_INDEX );
 		if ( jandexIndex == null ) {
 			jandexIndex = buildJandexIndex( deploymentResources );
 		}
 		return jandexIndex;
 	}
 
 	private IndexView buildJandexIndex(DeploymentResources deploymentResources) {
 		Indexer indexer = new Indexer();
 
 		for ( ClassDescriptor classDescriptor : deploymentResources.getClassDescriptors() ) {
 			indexStream( indexer, classDescriptor.getStreamAccess() );
 		}
 
 		for ( PackageDescriptor packageDescriptor : deploymentResources.getPackageDescriptors() ) {
 			indexStream( indexer, packageDescriptor.getStreamAccess() );
 		}
 
 		// for now we just skip entities defined in (1) orm.xml files and (2) hbm.xml files.  this part really needs
 		// metamodel branch...
 
 		// for now, we also need to wrap this in a CompositeIndex until Jandex is updated to use a common interface
 		// between the 2...
 		return indexer.complete();
 	}
 
 	private void indexStream(Indexer indexer, InputStreamAccess streamAccess) {
 		try {
 			InputStream stream = streamAccess.accessInputStream();
 			try {
 				indexer.index( stream );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw persistenceException( "Unable to index from stream " + streamAccess.getStreamName(), e );
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
 			for ( Integrator integrator : integratorProvider.getIntegrators() ) {
 				bootstrapServiceRegistryBuilder.with( integrator );
 			}
 		}
 		
 		final StrategyRegistrationProviderList strategyRegistrationProviderList
 				= (StrategyRegistrationProviderList) integrationSettings.get( STRATEGY_REGISTRATION_PROVIDERS );
 		if ( strategyRegistrationProviderList != null ) {
 			for ( StrategyRegistrationProvider strategyRegistrationProvider : strategyRegistrationProviderList
 					.getStrategyRegistrationProviders() ) {
 				bootstrapServiceRegistryBuilder.withStrategySelectors( strategyRegistrationProvider );
 			}
 		}
 
 		// TODO: If providedClassLoader is present (OSGi, etc.) *and*
 		// an APP_CLASSLOADER is provided, should throw an exception or
 		// warn?
 		ClassLoader classLoader;
 		ClassLoader appClassLoader = (ClassLoader) integrationSettings.get( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		if ( providedClassLoader != null ) {
 			classLoader = providedClassLoader;
 		}
 		else if ( appClassLoader != null ) {
 			classLoader = appClassLoader;
 			integrationSettings.remove( org.hibernate.cfg.AvailableSettings.APP_CLASSLOADER );
 		}
 		else {
 			classLoader = persistenceUnit.getClassLoader();
 		}
 		bootstrapServiceRegistryBuilder.with( classLoader );
 
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
 			for ( JaxbHibernateConfiguration.JaxbSecurity.JaxbGrant grant : configurationElement.getSecurity().getGrant() ) {
 				grantedJaccPermissions.add(
 						new GrantedPermission(
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
 
 			grantedJaccPermissions.add( new GrantedPermission( role, clazz, (String) value ) );
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
 		final Scanner scanner = locateOrBuildScanner( bootstrapServiceRegistry );
 		final ScanOptions scanOptions = determineScanOptions();
 
 		return scanner.scan( persistenceUnit, scanOptions );
 	}
 
 	private ScanOptions determineScanOptions() {
 		return new StandardScanOptions(
 				(String) configurationValues.get( AvailableSettings.AUTODETECTION ),
 				persistenceUnit.isExcludeUnlistedClasses()
 		);
 	}
 
 	@SuppressWarnings("unchecked")
 	private Scanner locateOrBuildScanner(BootstrapServiceRegistry bootstrapServiceRegistry) {
 		final Object value = configurationValues.remove( AvailableSettings.SCANNER );
 		if ( value == null ) {
 			return new StandardScanner();
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
 
 	@Override
 	public void generateSchema() {
 		processProperties();
 
 		final ServiceRegistry serviceRegistry = buildServiceRegistry();
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// IMPL NOTE : TCCL handling here is temporary.
 		//		It is needed because this code still uses Hibernate Configuration and Hibernate commons-annotations
 		// 		in turn which relies on TCCL being set.
 
 		( (ClassLoaderServiceImpl) classLoaderService ).withTccl(
 				new ClassLoaderServiceImpl.Work() {
 					@Override
 					public Object perform() {
 						final Configuration hibernateConfiguration = buildHibernateConfiguration( serviceRegistry );
 						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
 						return null;
 					}
 				}
 		);
 
 		// release this builder
 		cancel();
 	}
 
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
 						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
 
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
 						return new EntityManagerFactoryImpl(
 								persistenceUnit.getName(),
 								sessionFactory,
 								settings,
 								configurationValues,
 								hibernateConfiguration
 						);
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
 			serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, dataSource );
 		}
 		else if ( persistenceUnit.getJtaDataSource() != null ) {
 			if ( ! serviceRegistryBuilder.getSettings().containsKey( org.hibernate.cfg.AvailableSettings.DATASOURCE ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, persistenceUnit.getJtaDataSource() );
 				// HHH-8121 : make the PU-defined value available to EMF.getProperties()
 				configurationValues.put( AvailableSettings.JTA_DATASOURCE, persistenceUnit.getJtaDataSource() );
 			}
 		}
 		else if ( persistenceUnit.getNonJtaDataSource() != null ) {
 			if ( ! serviceRegistryBuilder.getSettings().containsKey( org.hibernate.cfg.AvailableSettings.DATASOURCE ) ) {
 				serviceRegistryBuilder.applySetting( org.hibernate.cfg.AvailableSettings.DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 				// HHH-8121 : make the PU-defined value available to EMF.getProperties()
 				configurationValues.put( AvailableSettings.NON_JTA_DATASOURCE, persistenceUnit.getNonJtaDataSource() );
 			}
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
 
 		if ( grantedJaccPermissions != null ) {
 			final JaccService jaccService = serviceRegistry.getService( JaccService.class );
 			for ( GrantedPermission grantedPermission : grantedJaccPermissions ) {
 				jaccService.addPermission( grantedPermission );
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
 			catch ( InvalidMappingException e ) {
 				// try our best to give the file name
 				if ( StringHelper.isNotEmpty( namedInputStream.getName() ) ) {
 					throw new InvalidMappingException(
 							"Error while parsing file: " + namedInputStream.getName(),
 							e.getType(),
 							e.getPath(),
 							e
 					);
 				}
 				else {
 					throw e;
 				}
 			}
 			catch (MappingException me) {
 				// try our best to give the file name
 				if ( StringHelper.isNotEmpty( namedInputStream.getName() ) ) {
 					throw new MappingException("Error while parsing file: " + namedInputStream.getName(), me );
 				}
 				else {
 					throw me;
 				}
 			}
 		}
 		for ( String packageName : metadataSources.packageNames ) {
 			cfg.addPackage( packageName );
 		}
+		
+		final TypeContributorList typeContributorList
+				= (TypeContributorList) configurationValues.get( TYPE_CONTRIBUTORS );
+		if ( typeContributorList != null ) {
+			configurationValues.remove( TYPE_CONTRIBUTORS );
+			for ( TypeContributor typeContributor : typeContributorList.getTypeContributors() ) {
+				cfg.registerTypeContributor( typeContributor );
+			}
+		}
+		
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
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/TypeContributorImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/TypeContributorImpl.java
new file mode 100644
index 0000000000..0a49200a26
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/internal/entities/TypeContributorImpl.java
@@ -0,0 +1,38 @@
+/* 
+ * Hibernate, Relational Persistence for Idiomatic Java
+ * 
+ * JBoss, Home of Professional Open Source
+ * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
+ * as indicated by the @authors tag. All rights reserved.
+ * See the copyright.txt in the distribution for a
+ * full listing of individual contributors.
+ *
+ * This copyrighted material is made available to anyone wishing to use,
+ * modify, copy, or redistribute it subject to the terms and conditions
+ * of the GNU Lesser General Public License, v. 2.1.
+ * This program is distributed in the hope that it will be useful, but WITHOUT A
+ * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
+ * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
+ * You should have received a copy of the GNU Lesser General Public License,
+ * v.2.1 along with this distribution; if not, write to the Free Software
+ * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
+ * MA  02110-1301, USA.
+ */
+package org.hibernate.envers.internal.entities;
+
+import org.hibernate.metamodel.spi.TypeContributions;
+import org.hibernate.metamodel.spi.TypeContributor;
+import org.hibernate.service.ServiceRegistry;
+
+/**
+ * @author Brett Meyer
+ */
+public class TypeContributorImpl implements TypeContributor {
+
+	@Override
+	public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
+		typeContributions.contributeType( new RevisionTypeType(),
+				new String[] { RevisionTypeType.class.getName() } );
+	}
+
+}
diff --git a/hibernate-envers/src/main/resources/META-INF/services/org.hibernate.metamodel.spi.TypeContributor b/hibernate-envers/src/main/resources/META-INF/services/org.hibernate.metamodel.spi.TypeContributor
new file mode 100644
index 0000000000..5eeaedd30c
--- /dev/null
+++ b/hibernate-envers/src/main/resources/META-INF/services/org.hibernate.metamodel.spi.TypeContributor
@@ -0,0 +1 @@
+org.hibernate.envers.internal.entities.TypeContributorImpl
\ No newline at end of file
diff --git a/hibernate-envers/src/main/resources/OSGI-INF/blueprint/blueprint.xml b/hibernate-envers/src/main/resources/OSGI-INF/blueprint/blueprint.xml
index 07c01bfc9f..9fc677c57e 100644
--- a/hibernate-envers/src/main/resources/OSGI-INF/blueprint/blueprint.xml
+++ b/hibernate-envers/src/main/resources/OSGI-INF/blueprint/blueprint.xml
@@ -1,10 +1,13 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <blueprint  default-activation="eager" 
             xmlns="http://www.osgi.org/xmlns/blueprint/v1.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
 
   <bean id="integrator" class="org.hibernate.envers.event.spi.EnversIntegrator"/>
   <service ref="integrator" interface="org.hibernate.integrator.spi.Integrator"/>
+
+  <bean id="typeContributor" class="org.hibernate.envers.internal.entities.TypeContributorImpl"/>
+  <service ref="typeContributor" interface="org.hibernate.metamodel.spi.TypeContributor"/>
   
 </blueprint>
diff --git a/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiPersistenceProvider.java b/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiPersistenceProvider.java
index 23cc77d8b7..c99715b8e5 100644
--- a/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiPersistenceProvider.java
+++ b/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiPersistenceProvider.java
@@ -1,141 +1,151 @@
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
 package org.hibernate.osgi;
 
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.spi.PersistenceUnitInfo;
 
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.jpa.HibernatePersistenceProvider;
 import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
 import org.hibernate.jpa.boot.spi.IntegratorProvider;
 import org.hibernate.jpa.boot.spi.StrategyRegistrationProviderList;
-
+import org.hibernate.jpa.boot.spi.TypeContributorList;
+import org.hibernate.metamodel.spi.TypeContributor;
 import org.osgi.framework.Bundle;
 import org.osgi.framework.BundleContext;
 import org.osgi.framework.BundleReference;
 
 /**
  * Acts as the PersistenceProvider service in OSGi environments
  *
  * @author Brett Meyer
  * @author Tim Ward
  */
 public class OsgiPersistenceProvider extends HibernatePersistenceProvider {
 	private OsgiClassLoader osgiClassLoader;
 	private OsgiJtaPlatform osgiJtaPlatform;
 	private Bundle requestingBundle;
 	private BundleContext context;
 
 	/**
 	 * Constructs a OsgiPersistenceProvider
 	 *
 	 * @param osgiClassLoader The ClassLoader we built from OSGi Bundles
 	 * @param osgiJtaPlatform The OSGi-specific JtaPlatform impl we built
 	 * @param requestingBundle The OSGi Bundle requesting the PersistenceProvider
 	 * @param context The OSGi context
 	 */
 	public OsgiPersistenceProvider(
 			OsgiClassLoader osgiClassLoader,
 			OsgiJtaPlatform osgiJtaPlatform,
 			Bundle requestingBundle,
 			BundleContext context) {
 		this.osgiClassLoader = osgiClassLoader;
 		this.osgiJtaPlatform = osgiJtaPlatform;
 		this.requestingBundle = requestingBundle;
 		this.context = context;
 	}
 
 	// TODO: Does "hibernate.classloaders" and osgiClassLoader need added to the
 	// EMFBuilder somehow?
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
 		final Map settings = generateSettings( properties );
 
 		// TODO: This needs tested.
 		settings.put( org.hibernate.jpa.AvailableSettings.SCANNER, new OsgiScanner( requestingBundle ) );
 		// TODO: This is temporary -- for PersistenceXmlParser's use of
 		// ClassLoaderServiceImpl#fromConfigSettings
 		settings.put( AvailableSettings.ENVIRONMENT_CLASSLOADER, osgiClassLoader );
 
 		osgiClassLoader.addBundle( requestingBundle );
 
 		return super.createEntityManagerFactory( persistenceUnitName, properties );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
 		final Map settings = generateSettings( properties );
 
 		// OSGi ClassLoaders must implement BundleReference
 		settings.put(
 				org.hibernate.jpa.AvailableSettings.SCANNER,
 				new OsgiScanner( ( (BundleReference) info.getClassLoader() ).getBundle() )
 		);
 
 		osgiClassLoader.addClassLoader( info.getClassLoader() );
 
 		return super.createContainerEntityManagerFactory( info, properties );
 	}
 
 	@SuppressWarnings("unchecked")
 	private Map generateSettings(Map properties) {
 		final Map settings = new HashMap();
 		if ( properties != null ) {
 			settings.putAll( properties );
 		}
 
 		settings.put( AvailableSettings.JTA_PLATFORM, osgiJtaPlatform );
 
 		final List<Integrator> integrators = OsgiServiceUtil.getServiceImpls( Integrator.class, context );
 		final IntegratorProvider integratorProvider = new IntegratorProvider() {
 			@Override
 			public List<Integrator> getIntegrators() {
 				return integrators;
 			}
 		};
 		settings.put( EntityManagerFactoryBuilderImpl.INTEGRATOR_PROVIDER, integratorProvider );
 
 		final List<StrategyRegistrationProvider> strategyRegistrationProviders = OsgiServiceUtil.getServiceImpls(
 				StrategyRegistrationProvider.class, context );
 		final StrategyRegistrationProviderList strategyRegistrationProviderList = new StrategyRegistrationProviderList() {
 			@Override
 			public List<StrategyRegistrationProvider> getStrategyRegistrationProviders() {
 				return strategyRegistrationProviders;
 			}
 		};
 		settings.put( EntityManagerFactoryBuilderImpl.STRATEGY_REGISTRATION_PROVIDERS, strategyRegistrationProviderList );
+
+		final List<TypeContributor> typeContributors = OsgiServiceUtil.getServiceImpls( TypeContributor.class, context );
+		TypeContributorList typeContributorList = new TypeContributorList() {
+			@Override
+			public List<TypeContributor> getTypeContributors() {
+				return typeContributors;
+			}
+		};
+		settings.put( EntityManagerFactoryBuilderImpl.TYPE_CONTRIBUTORS, typeContributorList );
 		
 		return settings;
 	}
 }
diff --git a/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java b/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java
index af6c57be4d..71b45387c9 100644
--- a/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java
+++ b/hibernate-osgi/src/main/java/org/hibernate/osgi/OsgiSessionFactoryService.java
@@ -1,110 +1,115 @@
 /* 
  * Hibernate, Relational Persistence for Idiomatic Java
  * 
  * JBoss, Home of Professional Open Source
  * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
 package org.hibernate.osgi;
 
 import java.util.List;
 
-import org.osgi.framework.Bundle;
-import org.osgi.framework.BundleContext;
-import org.osgi.framework.ServiceFactory;
-import org.osgi.framework.ServiceRegistration;
-
 import org.hibernate.SessionFactory;
 import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.registry.selector.StrategyRegistrationProvider;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.metamodel.spi.TypeContributor;
 import org.hibernate.service.ServiceRegistry;
+import org.osgi.framework.Bundle;
+import org.osgi.framework.BundleContext;
+import org.osgi.framework.ServiceFactory;
+import org.osgi.framework.ServiceRegistration;
 
 /**
  * Hibernate 4.2 and 4.3 still heavily rely on TCCL for ClassLoading.  Although
  * our ClassLoaderService removed some of the reliance, the service is
  * unfortunately not available during Configuration.  An OSGi
  * bundle manually creating a SessionFactory would require numerous ClassLoader
  * tricks (or may be impossible altogether).
  * <p/>
  * In order to fully control the TCCL issues and shield users from the
  * knowledge, we're requiring that bundles use this OSGi ServiceFactory.  It
  * configures and provides a SessionFactory as an OSGi service.
  * <p/>
  * Note that an OSGi ServiceFactory differs from a Service.  The ServiceFactory
  * allows individual instances of Services to be created and provided to
  * multiple client Bundles.
  *
  * @author Brett Meyer
  * @author Tim Ward
  */
 public class OsgiSessionFactoryService implements ServiceFactory {
 	private OsgiClassLoader osgiClassLoader;
 	private OsgiJtaPlatform osgiJtaPlatform;
 	private BundleContext context;
 
 	/**
 	 * Constructs a OsgiSessionFactoryService
 	 *
 	 * @param osgiClassLoader The OSGi-specific ClassLoader created in HibernateBundleActivator
 	 * @param osgiJtaPlatform The OSGi-specific JtaPlatform created in HibernateBundleActivator
 	 * @param context The OSGi context
 	 */
 	public OsgiSessionFactoryService(
 			OsgiClassLoader osgiClassLoader,
 			OsgiJtaPlatform osgiJtaPlatform,
 			BundleContext context) {
 		this.osgiClassLoader = osgiClassLoader;
 		this.osgiJtaPlatform = osgiJtaPlatform;
 		this.context = context;
 	}
 
 	@Override
 	public Object getService(Bundle requestingBundle, ServiceRegistration registration) {
 		osgiClassLoader.addBundle( requestingBundle );
 
 		final Configuration configuration = new Configuration();
 		configuration.getProperties().put( AvailableSettings.JTA_PLATFORM, osgiJtaPlatform );
 		configuration.configure();
 
 		final BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
 		builder.with( osgiClassLoader );
 
 		final List<Integrator> integrators = OsgiServiceUtil.getServiceImpls( Integrator.class, context );
 		for ( Integrator integrator : integrators ) {
 			builder.with( integrator );
 		}
 
 		final List<StrategyRegistrationProvider> strategyRegistrationProviders
 				= OsgiServiceUtil.getServiceImpls( StrategyRegistrationProvider.class, context );
 		for ( StrategyRegistrationProvider strategyRegistrationProvider : strategyRegistrationProviders ) {
 			builder.withStrategySelectors( strategyRegistrationProvider );
 		}
+        
+        final List<TypeContributor> typeContributors = OsgiServiceUtil.getServiceImpls( TypeContributor.class, context );
+        for (TypeContributor typeContributor : typeContributors) {
+        	configuration.registerTypeContributor( typeContributor );
+        }
 
 		final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder( builder.build() )
 				.applySettings( configuration.getProperties() ).build();
 		return configuration.buildSessionFactory( serviceRegistry );
 	}
 
 	@Override
 	public void ungetService(Bundle requestingBundle, ServiceRegistration registration, Object service) {
 		((SessionFactory) service).close();
 	}
 
 }
