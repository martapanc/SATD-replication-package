diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index b4eeb54a9d..eebd180d77 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1407 +1,1418 @@
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
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
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
 import org.hibernate.mapping.Constraint;
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
 import org.hibernate.metamodel.spi.TypeContributions;
 import org.hibernate.metamodel.spi.TypeContributor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
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
 	protected Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
 	protected Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	protected Map<String, NamedEntityGraphDefinition> namedEntityGraphMap;
 
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
 	private List<TypeContributor> typeContributorRegistrations = new ArrayList<TypeContributor>();
 
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
 	private Set<String> defaultNamedProcedure;
 
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
 		namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
 		namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>(  );
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
 		defaultNamedProcedure =  new HashSet<String>(  );
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
+	 * Get a copy of all known MappedSuperclasses
+	 * <p/>
+	 * EXPERIMENTAL Consider this API as PRIVATE
+	 *
+	 * @return Set of all known MappedSuperclasses
+	 */
+	public java.util.Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
+		return new HashSet<MappedSuperclass>( mappedSuperClasses.values() );
+	}
+
+	/**
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
 			jpaMetadataProvider.getXMLContext().applyDiscoveredAttributeConverters( this );
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
 		if ( files != null ) {
 			for ( File file : files ) {
 				if ( file.isDirectory() ) {
 					addDirectory( file );
 				}
 				else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 					addFile( file );
 				}
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
 			}
 		}
 
 		// Foreign keys must be created *after* unique keys for numerous DBs.  See HH-8390.
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
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
 								String constraintDropString = uniqueKey.sqlDropString( dialect, tableCatalog, tableSchema );
 								scripts.add( new SchemaUpdateScript( constraintDropString, true) );
 							}
 							scripts.add( new SchemaUpdateScript( constraintString, true) );
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
 
 		// Foreign keys must be created *after* unique keys for numerous DBs.  See HH-8390.
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			String tableSchema = ( table.getSchema() == null ) ? defaultSchema : table.getSchema();
 			String tableCatalog = ( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), tableSchema,
 						tableCatalog, table.isQuoted() );
 
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
 
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
index 018b292824..90a7237c7f 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerFactoryImpl.java
@@ -1,636 +1,636 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.internal;
 
 import javax.persistence.Cache;
 import javax.persistence.EntityGraph;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.NamedAttributeNode;
 import javax.persistence.NamedEntityGraph;
 import javax.persistence.NamedSubgraph;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PersistenceUnitUtil;
 import javax.persistence.Query;
 import javax.persistence.SynchronizationType;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.EntityType;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.LoadState;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.Hibernate;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.ejb.HibernateEntityManagerFactory;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.HibernateQuery;
 import org.hibernate.jpa.boot.internal.SettingsImpl;
 import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
 import org.hibernate.jpa.graph.internal.AbstractGraphNode;
 import org.hibernate.jpa.graph.internal.EntityGraphImpl;
 import org.hibernate.jpa.graph.internal.SubgraphImpl;
 import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
 import org.hibernate.jpa.internal.metamodel.MetamodelImpl;
 import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Actual Hibernate implementation of {@link javax.persistence.EntityManagerFactory}.
  *
  * @author Gavin King
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryImpl implements HibernateEntityManagerFactory {
 	private static final long serialVersionUID = 5423543L;
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private static final Logger log = Logger.getLogger( EntityManagerFactoryImpl.class );
 
 	private final transient SessionFactoryImpl sessionFactory;
 	private final transient PersistenceUnitTransactionType transactionType;
 	private final transient boolean discardOnClose;
 	private final transient Class sessionInterceptorClass;
 	private final transient CriteriaBuilderImpl criteriaBuilder;
 	private final transient MetamodelImpl metamodel;
 	private final transient HibernatePersistenceUnitUtil util;
 	private final transient Map<String,Object> properties;
 	private final String entityManagerFactoryName;
 
 	private final transient PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
 	private final transient Map<String,EntityGraphImpl> entityGraphs = new ConcurrentHashMap<String, EntityGraphImpl>();
 
 	@SuppressWarnings( "unchecked" )
 	public EntityManagerFactoryImpl(
 			PersistenceUnitTransactionType transactionType,
 			boolean discardOnClose,
 			Class sessionInterceptorClass,
 			Configuration cfg,
 			ServiceRegistry serviceRegistry,
 			String persistenceUnitName) {
 		this(
 				persistenceUnitName,
 				(SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry ),
 				new SettingsImpl().setReleaseResourcesOnCloseEnabled( discardOnClose ).setSessionInterceptorClass( sessionInterceptorClass ).setTransactionType( transactionType ),
 				cfg.getProperties(),
 				cfg
 		);
 	}
 
 	public EntityManagerFactoryImpl(
 			String persistenceUnitName,
 			SessionFactoryImplementor sessionFactory,
 			SettingsImpl settings,
 			Map<?, ?> configurationValues,
 			Configuration cfg) {
 		this.sessionFactory = (SessionFactoryImpl) sessionFactory;
 		this.transactionType = settings.getTransactionType();
 		this.discardOnClose = settings.isReleaseResourcesOnCloseEnabled();
 		this.sessionInterceptorClass = settings.getSessionInterceptorClass();
 
-		final Iterator<PersistentClass> classes = cfg.getClassMappings();
 		final JpaMetaModelPopulationSetting jpaMetaModelPopulationSetting = determineJpaMetaModelPopulationSetting( cfg );
 		if ( JpaMetaModelPopulationSetting.DISABLED == jpaMetaModelPopulationSetting ) {
 			this.metamodel = null;
 		}
 		else {
 			this.metamodel = MetamodelImpl.buildMetamodel(
-					classes,
+					cfg.getClassMappings(),
+					cfg.getMappedSuperclassMappingsCopy(),
 					sessionFactory,
 					JpaMetaModelPopulationSetting.IGNORE_UNSUPPORTED == jpaMetaModelPopulationSetting
 			);
 		}
 		this.criteriaBuilder = new CriteriaBuilderImpl( this );
 		this.util = new HibernatePersistenceUnitUtil( this );
 
 		HashMap<String,Object> props = new HashMap<String, Object>();
 		addAll( props, sessionFactory.getProperties() );
 		addAll( props, cfg.getProperties() );
 		addAll( props, configurationValues );
 		maskOutSensitiveInformation( props );
 		this.properties = Collections.unmodifiableMap( props );
 		String entityManagerFactoryName = (String)this.properties.get( AvailableSettings.ENTITY_MANAGER_FACTORY_NAME);
 		if (entityManagerFactoryName == null) {
 			entityManagerFactoryName = persistenceUnitName;
 		}
 		if (entityManagerFactoryName == null) {
 			entityManagerFactoryName = (String) UUID_GENERATOR.generate(null, null);
 		}
 		this.entityManagerFactoryName = entityManagerFactoryName;
 
 		applyNamedEntityGraphs( cfg.getNamedEntityGraphs() );
 
 		EntityManagerFactoryRegistry.INSTANCE.addEntityManagerFactory( entityManagerFactoryName, this );
 	}
 
 	private enum JpaMetaModelPopulationSetting {
 		ENABLED,
 		DISABLED,
 		IGNORE_UNSUPPORTED;
 		
 		private static JpaMetaModelPopulationSetting parse(String setting) {
 			if ( "enabled".equalsIgnoreCase( setting ) ) {
 				return ENABLED;
 			}
 			else if ( "disabled".equalsIgnoreCase( setting ) ) {
 				return DISABLED;
 			}
 			else {
 				return IGNORE_UNSUPPORTED;
 			}
 		}
 	}
 	
 	protected JpaMetaModelPopulationSetting determineJpaMetaModelPopulationSetting(Configuration cfg) {
 		String setting = ConfigurationHelper.getString(
 				AvailableSettings.JPA_METAMODEL_POPULATION,
 				cfg.getProperties(),
 				null
 		);
 		if ( setting == null ) {
 			setting = ConfigurationHelper.getString( AvailableSettings.JPA_METAMODEL_GENERATION, cfg.getProperties(), null );
 			if ( setting != null ) {
 				log.infof( 
 						"Encountered deprecated setting [%s], use [%s] instead",
 						AvailableSettings.JPA_METAMODEL_GENERATION,
 						AvailableSettings.JPA_METAMODEL_POPULATION
 				);
 			}
 		}
 		return JpaMetaModelPopulationSetting.parse( setting );
 	}
 
 	private static void addAll(HashMap<String, Object> destination, Map<?,?> source) {
 		for ( Map.Entry entry : source.entrySet() ) {
 			if ( String.class.isInstance( entry.getKey() ) ) {
 				destination.put( (String) entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	private void maskOutSensitiveInformation(HashMap<String, Object> props) {
 		maskOutIfSet( props, AvailableSettings.JDBC_PASSWORD );
 		maskOutIfSet( props, org.hibernate.cfg.AvailableSettings.PASS );
 	}
 
 	private void maskOutIfSet(HashMap<String, Object> props, String setting) {
 		if ( props.containsKey( setting ) ) {
 			props.put( setting, "****" );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	private void applyNamedEntityGraphs(Collection<NamedEntityGraphDefinition> namedEntityGraphs) {
 		for ( NamedEntityGraphDefinition definition : namedEntityGraphs ) {
 			final EntityType entityType = metamodel.getEntityTypeByName( definition.getJpaEntityName() );
 			final EntityGraphImpl entityGraph = new EntityGraphImpl(
 					definition.getRegisteredName(),
 					entityType,
 					this
 			);
 
 			final NamedEntityGraph namedEntityGraph = definition.getAnnotation();
 
 			if ( namedEntityGraph.includeAllAttributes() ) {
 				for ( Object attributeObject : entityType.getAttributes() ) {
 					entityGraph.addAttributeNodes( (Attribute) attributeObject );
 				}
 			}
 
 			if ( namedEntityGraph.attributeNodes() != null ) {
 				applyNamedAttributeNodes( namedEntityGraph.attributeNodes(), namedEntityGraph, entityGraph );
 			}
 
 			entityGraphs.put( definition.getRegisteredName(), entityGraph );
 		}
 	}
 
 	private void applyNamedAttributeNodes(
 			NamedAttributeNode[] namedAttributeNodes,
 			NamedEntityGraph namedEntityGraph,
 			AbstractGraphNode graphNode) {
 		for ( NamedAttributeNode namedAttributeNode : namedAttributeNodes ) {
 			if ( StringHelper.isNotEmpty( namedAttributeNode.subgraph() ) ) {
 				final SubgraphImpl subgraph = graphNode.addSubgraph( namedAttributeNode.value() );
 				applyNamedSubgraphs(
 						namedEntityGraph,
 						namedAttributeNode.subgraph(),
 						subgraph
 				);
 			}
 			if ( StringHelper.isNotEmpty( namedAttributeNode.keySubgraph() ) ) {
 				final SubgraphImpl subgraph = graphNode.addKeySubgraph( namedAttributeNode.value() );
 				applyNamedSubgraphs(
 						namedEntityGraph,
 						namedAttributeNode.keySubgraph(),
 						subgraph
 				);
 			}
 		}
 	}
 
 	private void applyNamedSubgraphs(NamedEntityGraph namedEntityGraph, String subgraphName, SubgraphImpl subgraph) {
 		for ( NamedSubgraph namedSubgraph : namedEntityGraph.subgraphs() ) {
 			if ( subgraphName.equals( namedSubgraph.name() ) ) {
 				applyNamedAttributeNodes(
 						namedSubgraph.attributeNodes(),
 						namedEntityGraph,
 						subgraph
 				);
 			}
 		}
 	}
 
 	public EntityManager createEntityManager() {
 		return createEntityManager( Collections.EMPTY_MAP );
 	}
 
 	@Override
 	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
 		return createEntityManager( synchronizationType, Collections.EMPTY_MAP );
 	}
 
 	public EntityManager createEntityManager(Map map) {
 		return createEntityManager( SynchronizationType.SYNCHRONIZED, map );
 	}
 
 	@Override
 	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
 		validateNotClosed();
 
 		//TODO support discardOnClose, persistencecontexttype?, interceptor,
 		return new EntityManagerImpl(
 				this,
 				PersistenceContextType.EXTENDED,
 				synchronizationType,
 				transactionType,
 				discardOnClose,
 				sessionInterceptorClass,
 				map
 		);
 	}
 
 	public CriteriaBuilder getCriteriaBuilder() {
 		validateNotClosed();
 		return criteriaBuilder;
 	}
 
 	public Metamodel getMetamodel() {
 		validateNotClosed();
 		return metamodel;
 	}
 
 	public void close() {
 		// The spec says so, that's why :(
 		validateNotClosed();
 
 		sessionFactory.close();
 		EntityManagerFactoryRegistry.INSTANCE.removeEntityManagerFactory(entityManagerFactoryName, this);
 	}
 
 	public Map<String, Object> getProperties() {
 		validateNotClosed();
 		return properties;
 	}
 
 	public Cache getCache() {
 		validateNotClosed();
 
 		// TODO : cache the cache reference?
 		return new JPACache( sessionFactory );
 	}
 
 	protected void validateNotClosed() {
 		if ( ! isOpen() ) {
 			throw new IllegalStateException( "EntityManagerFactory is closed" );
 		}
 	}
 
 	public PersistenceUnitUtil getPersistenceUnitUtil() {
 		validateNotClosed();
 		return util;
 	}
 
 	@Override
 	public void addNamedQuery(String name, Query query) {
 		validateNotClosed();
 
 		// NOTE : we use Query#unwrap here (rather than direct type checking) to account for possibly wrapped
 		// query implementations
 
 		// first, handle StoredProcedureQuery
 		try {
 			final StoredProcedureQueryImpl unwrapped = query.unwrap( StoredProcedureQueryImpl.class );
 			if ( unwrapped != null ) {
 				addNamedStoredProcedureQuery( name, unwrapped );
 				return;
 			}
 		}
 		catch ( PersistenceException ignore ) {
 			// this means 'query' is not a StoredProcedureQueryImpl
 		}
 
 		// then try as a native-SQL or JPQL query
 		try {
 			final HibernateQuery unwrapped = query.unwrap( HibernateQuery.class );
 			if ( unwrapped != null ) {
 				// create and register the proper NamedQueryDefinition...
 				final org.hibernate.Query hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
 				if ( org.hibernate.SQLQuery.class.isInstance( hibernateQuery ) ) {
 					sessionFactory.registerNamedSQLQueryDefinition(
 							name,
 							extractSqlQueryDefinition( (org.hibernate.SQLQuery) hibernateQuery, name )
 					);
 				}
 				else {
 					sessionFactory.registerNamedQueryDefinition( name, extractHqlQueryDefinition( hibernateQuery, name ) );
 				}
 				return;
 			}
 		}
 		catch ( PersistenceException ignore ) {
 			// this means 'query' is not a native-SQL or JPQL query
 		}
 
 
 		// if we get here, we are unsure how to properly unwrap the incoming query to extract the needed information
 		throw new PersistenceException(
 				String.format(
 						"Unsure how to how to properly unwrap given Query [%s] as basis for named query",
 						query
 				)
 		);
 	}
 
 	private void addNamedStoredProcedureQuery(String name, StoredProcedureQueryImpl query) {
 		final ProcedureCall procedureCall = query.getHibernateProcedureCall();
 		sessionFactory.getNamedQueryRepository().registerNamedProcedureCallMemento(
 				name,
 				procedureCall.extractMemento( query.getHints() )
 		);
 	}
 
 	private NamedSQLQueryDefinition extractSqlQueryDefinition(org.hibernate.SQLQuery nativeSqlQuery, String name) {
 		final NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder( name );
 		fillInNamedQueryBuilder( builder, nativeSqlQuery );
 		builder.setCallable( nativeSqlQuery.isCallable() )
 				.setQuerySpaces( nativeSqlQuery.getSynchronizedQuerySpaces() )
 				.setQueryReturns( nativeSqlQuery.getQueryReturns() );
 		return builder.createNamedQueryDefinition();
 	}
 
 	private NamedQueryDefinition extractHqlQueryDefinition(org.hibernate.Query hqlQuery, String name) {
 		final NamedQueryDefinitionBuilder builder = new NamedQueryDefinitionBuilder( name );
 		fillInNamedQueryBuilder( builder, hqlQuery );
 		// LockOptions only valid for HQL/JPQL queries...
 		builder.setLockOptions( hqlQuery.getLockOptions().makeCopy() );
 		return builder.createNamedQueryDefinition();
 	}
 
 	private void fillInNamedQueryBuilder(NamedQueryDefinitionBuilder builder, org.hibernate.Query query) {
 		builder.setQuery( query.getQueryString() )
 				.setComment( query.getComment() )
 				.setCacheable( query.isCacheable() )
 				.setCacheRegion( query.getCacheRegion() )
 				.setCacheMode( query.getCacheMode() )
 				.setTimeout( query.getTimeout() )
 				.setFetchSize( query.getFetchSize() )
 				.setFirstResult( query.getFirstResult() )
 				.setMaxResults( query.getMaxResults() )
 				.setReadOnly( query.isReadOnly() )
 				.setFlushMode( query.getFlushMode() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T unwrap(Class<T> cls) {
 		if ( SessionFactory.class.isAssignableFrom( cls ) ) {
 			return ( T ) sessionFactory;
 		}
 		if ( SessionFactoryImplementor.class.isAssignableFrom( cls ) ) {
 			return ( T ) sessionFactory;
 		}
 		if ( EntityManager.class.isAssignableFrom( cls ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap EntityManagerFactory as " + cls.getName() );
 	}
 
 	@Override
 	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
 		if ( ! EntityGraphImpl.class.isInstance( entityGraph ) ) {
 			throw new IllegalArgumentException(
 					"Unknown type of EntityGraph for making named : " + entityGraph.getClass().getName()
 			);
 		}
 		final EntityGraphImpl<T> copy = ( (EntityGraphImpl<T>) entityGraph ).makeImmutableCopy( graphName );
 		final EntityGraphImpl old = entityGraphs.put( graphName, copy );
 		if ( old != null ) {
 			log.debugf( "EntityGraph being replaced on EntityManagerFactory for name %s", graphName );
 		}
 	}
 
 	public EntityGraphImpl findEntityGraphByName(String name) {
 		return entityGraphs.get( name );
 	}
 
 	@SuppressWarnings("unchecked")
 	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
 		final EntityType<T> entityType = getMetamodel().entity( entityClass );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "Given class is not an entity : " + entityClass.getName() );
 		}
 
 		final List<EntityGraph<? super T>> results = new ArrayList<EntityGraph<? super T>>();
 		for ( EntityGraphImpl entityGraph : this.entityGraphs.values() ) {
 			if ( entityGraph.appliesTo( entityType ) ) {
 				results.add( entityGraph );
 			}
 		}
 		return results;
 	}
 
 	public boolean isOpen() {
 		return ! sessionFactory.isClosed();
 	}
 
 	public SessionFactoryImpl getSessionFactory() {
 		return sessionFactory;
 	}
 
 	@Override
 	public EntityTypeImpl getEntityTypeByName(String entityName) {
 		final EntityTypeImpl entityType = metamodel.getEntityTypeByName( entityName );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "[" + entityName + "] did not refer to EntityType" );
 		}
 		return entityType;
 	}
 
 	public String getEntityManagerFactoryName() {
 		return entityManagerFactoryName;
 	}
 
 	private static class JPACache implements Cache {
 		private SessionFactoryImplementor sessionFactory;
 
 		private JPACache(SessionFactoryImplementor sessionFactory) {
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
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <T> T unwrap(Class<T> cls) {
 			if ( RegionFactory.class.isAssignableFrom( cls ) ) {
 				return (T) sessionFactory.getSettings().getRegionFactory();
 			}
 			if ( org.hibernate.Cache.class.isAssignableFrom( cls ) ) {
 				return (T) sessionFactory.getCache();
 			}
 			throw new PersistenceException( "Hibernate cannot unwrap Cache as " + cls.getName() );
 		}
 	}
 
 	private static EntityManagerFactory getNamedEntityManagerFactory(String entityManagerFactoryName) throws InvalidObjectException {
 		Object result = EntityManagerFactoryRegistry.INSTANCE.getNamedEntityManagerFactory(entityManagerFactoryName);
 
 		if ( result == null ) {
 			throw new InvalidObjectException( "could not resolve entity manager factory during entity manager deserialization [name=" + entityManagerFactoryName + "]" );
 		}
 
 		return (EntityManagerFactory)result;
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if (entityManagerFactoryName == null) {
 			throw new InvalidObjectException( "could not serialize entity manager factory with null entityManagerFactoryName" );
 		}
 		oos.defaultWriteObject();
 	}
 
 	/**
 	 * After deserialization of an EntityManagerFactory, this is invoked to return the EntityManagerFactory instance
 	 * that is already in use rather than a cloned copy of the object.
 	 *
 	 * @return
 	 * @throws InvalidObjectException
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		return getNamedEntityManagerFactory(entityManagerFactoryName);
 	}
 
 
 
 	private static class HibernatePersistenceUnitUtil implements PersistenceUnitUtil, Serializable {
 		private final HibernateEntityManagerFactory emf;
 		private transient PersistenceUtilHelper.MetadataCache cache;
 
 		private HibernatePersistenceUnitUtil(EntityManagerFactoryImpl emf) {
 			this.emf = emf;
 			this.cache = emf.cache;
 		}
 
 		public boolean isLoaded(Object entity, String attributeName) {
 			// added log message to help with HHH-7454, if state == LoadState,NOT_LOADED, returning true or false is not accurate.
 			log.debug("PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
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
 			// added log message to help with HHH-7454, if state == LoadState,NOT_LOADED, returning true or false is not accurate.
 			log.debug("PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
 			return PersistenceUtilHelper.isLoaded( entity ) != LoadState.NOT_LOADED;
 		}
 
 		public Object getIdentifier(Object entity) {
 			final Class entityClass = Hibernate.getClass( entity );
 			final ClassMetadata classMetadata = emf.getSessionFactory().getClassMetadata( entityClass );
 			if (classMetadata == null) {
 				throw new IllegalArgumentException( entityClass + " is not an entity" );
 			}
 			//TODO does that work for @IdClass?
 			return classMetadata.getIdentifier( entity );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerMessageLogger.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerMessageLogger.java
index 1adb338e3e..5c983c27de 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerMessageLogger.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/EntityManagerMessageLogger.java
@@ -1,120 +1,127 @@
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
 package org.hibernate.jpa.internal;
 
 import java.net.URISyntaxException;
 import java.net.URL;
 
 import org.jboss.logging.annotations.Cause;
 import org.jboss.logging.annotations.LogMessage;
 import org.jboss.logging.annotations.Message;
 import org.jboss.logging.annotations.MessageLogger;
 
 import org.hibernate.internal.CoreMessageLogger;
 
 import static org.jboss.logging.Logger.Level.DEBUG;
 import static org.jboss.logging.Logger.Level.ERROR;
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * The jboss-logging {@link MessageLogger} for the hibernate-entitymanager module.  It reserves message ids ranging from
  * 15001 to 20000 inclusively.
  * <p/>
  * New messages must be added after the last message defined to ensure message codes are unique.
  */
 @MessageLogger( projectCode = "HHH" )
 public interface EntityManagerMessageLogger extends CoreMessageLogger {
 
     @LogMessage( level = INFO )
     @Message( value = "Bound Ejb3Configuration to JNDI name: %s", id = 15001 )
     void boundEjb3ConfigurationToJndiName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "Ejb3Configuration name: %s", id = 15002 )
     void ejb3ConfigurationName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "An Ejb3Configuration was renamed from name: %s", id = 15003 )
     void ejb3ConfigurationRenamedFromName( String name );
 
     @LogMessage( level = INFO )
     @Message( value = "An Ejb3Configuration was unbound from name: %s", id = 15004 )
     void ejb3ConfigurationUnboundFromName( String name );
 
     @LogMessage( level = WARN )
     @Message( value = "Exploded jar file does not exist (ignored): %s", id = 15005 )
     void explodedJarDoesNotExist( URL jarUrl );
 
     @LogMessage( level = WARN )
     @Message( value = "Exploded jar file not a directory (ignored): %s", id = 15006 )
     void explodedJarNotDirectory( URL jarUrl );
 
     @LogMessage( level = ERROR )
     @Message( value = "Illegal argument on static metamodel field injection : %s#%s; expected type :  %s; encountered type : %s", id = 15007 )
     void illegalArgumentOnStaticMetamodelFieldInjection( String name,
                                                          String name2,
                                                          String name3,
                                                          String name4 );
 
     @LogMessage( level = ERROR )
     @Message( value = "Malformed URL: %s", id = 15008 )
     void malformedUrl( URL jarUrl,
                        @Cause URISyntaxException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Malformed URL: %s", id = 15009 )
     void malformedUrlWarning( URL jarUrl,
                               @Cause URISyntaxException e );
 
     @LogMessage( level = WARN )
     @Message( value = "Unable to find file (ignored): %s", id = 15010 )
     void unableToFindFile( URL jarUrl,
                            @Cause Exception e );
 
     @LogMessage( level = ERROR )
     @Message( value = "Unable to locate static metamodel field : %s#%s", id = 15011 )
     void unableToLocateStaticMetamodelField( String name,
                                              String name2 );
 
     @LogMessage( level = INFO )
     @Message( value = "Using provided datasource", id = 15012 )
     void usingProvidedDataSource();
 
 
     @LogMessage( level = DEBUG )
     @Message( value = "Returning null (as required by JPA spec) rather than throwing EntityNotFoundException, " +
             "as the entity (type=%s, id=%s) does not exist", id = 15013 )
     void ignoringEntityNotFound( String entityName, String identifier);
 
 	@LogMessage( level = WARN )
 	@Message(
 			value = "DEPRECATION - attempt to refer to JPA positional parameter [?%1$s] using String name [\"%1$s\"] " +
 					"rather than int position [%1$s] (generally in Query#setParameter, Query#getParameter or " +
 					"Query#getParameterValue calls).  Hibernate previously allowed such usage, but it is considered " +
 					"deprecated.",
 			id = 15014
 	)
 	void deprecatedJpaPositionalParameterAccess(Integer jpaPositionalParameter);
+
+	@LogMessage( level = INFO )
+	@Message(
+			id = 15015,
+			value = "Encountered a MappedSuperclass [%s] not used in any entity hierarchy"
+	)
+	void unusedMappedSuperclass(String name);
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
index b568802132..b131996e52 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetadataContext.java
@@ -1,493 +1,506 @@
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
 package org.hibernate.jpa.internal.metamodel;
 
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.IdentifiableType;
 import javax.persistence.metamodel.MappedSuperclassType;
 import javax.persistence.metamodel.SingularAttribute;
 import java.lang.reflect.Field;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 
 /**
  * Defines a context for storing information during the building of the {@link MetamodelImpl}.
  * <p/>
  * This contextual information includes data needing to be processed in a second pass as well as
  * cross-references into the built metamodel classes.
  * <p/>
  * At the end of the day, clients are interested in the {@link #getEntityTypeMap} and {@link #getEmbeddableTypeMap}
  * results, which represent all the registered {@linkplain #registerEntityType entities} and
  *  {@linkplain #registerEmbeddedableType embeddables} respectively.
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 class MetadataContext {
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            MetadataContext.class.getName());
 
 	private final SessionFactoryImplementor sessionFactory;
-    private final boolean ignoreUnsupported;
+	private Set<MappedSuperclass> knownMappedSuperclasses;
+	private final boolean ignoreUnsupported;
 	private final AttributeFactory attributeFactory = new AttributeFactory( this );
 
 	private Map<Class<?>,EntityTypeImpl<?>> entityTypes
 			= new HashMap<Class<?>, EntityTypeImpl<?>>();
 	private Map<String,EntityTypeImpl<?>> entityTypesByEntityName
 			= new HashMap<String, EntityTypeImpl<?>>();
 	private Map<PersistentClass,EntityTypeImpl<?>> entityTypesByPersistentClass
 			= new HashMap<PersistentClass,EntityTypeImpl<?>>();
 	private Map<Class<?>, EmbeddableTypeImpl<?>> embeddables
 			= new HashMap<Class<?>, EmbeddableTypeImpl<?>>();
 	private Map<MappedSuperclass, MappedSuperclassTypeImpl<?>> mappedSuperclassByMappedSuperclassMapping
 			= new HashMap<MappedSuperclass,MappedSuperclassTypeImpl<?>>();
 	//this list contains MappedSuperclass and EntityTypes ordered by superclass first
 	private List<Object> orderedMappings = new ArrayList<Object>();
 	/**
 	 * Stack of PersistentClass being process. Last in the list is the highest in the stack.
 	 *
 	 */
 	private List<PersistentClass> stackOfPersistentClassesBeingProcessed
 			= new ArrayList<PersistentClass>();
 	private Map<MappedSuperclassTypeImpl<?>, PersistentClass> mappedSuperClassTypeToPersistentClass
 			= new HashMap<MappedSuperclassTypeImpl<?>, PersistentClass>();
 
-	public MetadataContext(SessionFactoryImplementor sessionFactory, boolean ignoreUnsupported) {
+	public MetadataContext(
+			SessionFactoryImplementor sessionFactory,
+			Set<MappedSuperclass> mappedSuperclasses,
+			boolean ignoreUnsupported) {
 		this.sessionFactory = sessionFactory;
-        this.ignoreUnsupported = ignoreUnsupported;
+		this.knownMappedSuperclasses = mappedSuperclasses;
+		this.ignoreUnsupported = ignoreUnsupported;
 	}
 
 	/*package*/ SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
     /*package*/ boolean isIgnoreUnsupported() {
         return ignoreUnsupported;
     }
 
     /**
 	 * Retrieves the {@linkplain Class java type} to {@link EntityTypeImpl} map.
 	 *
 	 * @return The {@linkplain Class java type} to {@link EntityTypeImpl} map.
 	 */
 	public Map<Class<?>, EntityTypeImpl<?>> getEntityTypeMap() {
 		return Collections.unmodifiableMap( entityTypes );
 	}
 
 	public Map<Class<?>, EmbeddableTypeImpl<?>> getEmbeddableTypeMap() {
 		return Collections.unmodifiableMap( embeddables );
 	}
 
 	public Map<Class<?>,MappedSuperclassType<?>> getMappedSuperclassTypeMap() {
 		// we need to actually build this map...
 		final Map<Class<?>,MappedSuperclassType<?>> mappedSuperClassTypeMap = CollectionHelper.mapOfSize(
 				mappedSuperclassByMappedSuperclassMapping.size()
 		);
 
 		for ( MappedSuperclassTypeImpl mappedSuperclassType : mappedSuperclassByMappedSuperclassMapping.values() ) {
 			mappedSuperClassTypeMap.put(
 					mappedSuperclassType.getJavaType(),
 					mappedSuperclassType
 			);
 		}
 
 		return mappedSuperClassTypeMap;
 	}
 
 	/*package*/ void registerEntityType(PersistentClass persistentClass, EntityTypeImpl<?> entityType) {
 		entityTypes.put( entityType.getBindableJavaType(), entityType );
 		entityTypesByEntityName.put( persistentClass.getEntityName(), entityType );
 		entityTypesByPersistentClass.put( persistentClass, entityType );
 		orderedMappings.add( persistentClass );
 	}
 
 	/*package*/ void registerEmbeddedableType(EmbeddableTypeImpl<?> embeddableType) {
 		embeddables.put( embeddableType.getJavaType(), embeddableType );
 	}
 
-	/*package*/ void registerMappedSuperclassType(MappedSuperclass mappedSuperclass,
-												  MappedSuperclassTypeImpl<?> mappedSuperclassType) {
+	/*package*/ void registerMappedSuperclassType(
+			MappedSuperclass mappedSuperclass,
+			MappedSuperclassTypeImpl<?> mappedSuperclassType) {
 		mappedSuperclassByMappedSuperclassMapping.put( mappedSuperclass, mappedSuperclassType );
 		orderedMappings.add( mappedSuperclass );
 		mappedSuperClassTypeToPersistentClass.put( mappedSuperclassType, getEntityWorkedOn() );
+
+		knownMappedSuperclasses.remove( mappedSuperclass );
 	}
 
 	/**
 	 * Given a Hibernate {@link PersistentClass}, locate the corresponding JPA {@link org.hibernate.type.EntityType}
 	 * implementation.  May retur null if the given {@link PersistentClass} has not yet been processed.
 	 *
 	 * @param persistentClass The Hibernate (config time) metamodel instance representing an entity.
 	 * @return Tne corresponding JPA {@link org.hibernate.type.EntityType}, or null if not yet processed.
 	 */
 	public EntityTypeImpl<?> locateEntityType(PersistentClass persistentClass) {
 		return entityTypesByPersistentClass.get( persistentClass );
 	}
 
 	/**
 	 * Given a Java {@link Class}, locate the corresponding JPA {@link org.hibernate.type.EntityType}.  May
 	 * return null which could means that no such mapping exists at least at this time.
 	 *
 	 * @param javaType The java class.
 	 * @return The corresponding JPA {@link org.hibernate.type.EntityType}, or null.
 	 */
 	public EntityTypeImpl<?> locateEntityType(Class<?> javaType) {
 		return entityTypes.get( javaType );
 	}
 
 	/**
 	 * Given an entity-name, locate the corresponding JPA {@link org.hibernate.type.EntityType}.  May
 	 * return null which could means that no such mapping exists at least at this time.
 	 *
 	 * @param entityName The entity-name.
 	 * @return The corresponding JPA {@link org.hibernate.type.EntityType}, or null.
 	 */
 	public EntityTypeImpl<?> locateEntityType(String entityName) {
 		return entityTypesByEntityName.get( entityName );
 	}
 
     public Map<String, EntityTypeImpl<?>> getEntityTypesByEntityName() {
         return Collections.unmodifiableMap( entityTypesByEntityName );
     }
 
     @SuppressWarnings({ "unchecked" })
 	public void wrapUp() {
         LOG.trace("Wrapping up metadata context...");
+
 		//we need to process types from superclasses to subclasses
 		for (Object mapping : orderedMappings) {
 			if ( PersistentClass.class.isAssignableFrom( mapping.getClass() ) ) {
 				@SuppressWarnings( "unchecked" )
 				final PersistentClass safeMapping = (PersistentClass) mapping;
                 LOG.trace("Starting entity [" + safeMapping.getEntityName() + "]");
 				try {
 					final EntityTypeImpl<?> jpa2Mapping = entityTypesByPersistentClass.get( safeMapping );
 					applyIdMetadata( safeMapping, jpa2Mapping );
 					applyVersionAttribute( safeMapping, jpa2Mapping );
 					Iterator<Property> properties = safeMapping.getDeclaredPropertyIterator();
 					while ( properties.hasNext() ) {
 						final Property property = properties.next();
 						if ( property.getValue() == safeMapping.getIdentifierMapper() ) {
 							// property represents special handling for id-class mappings but we have already
 							// accounted for the embedded property mappings in #applyIdMetadata &&
 							// #buildIdClassAttributes
 							continue;
 						}
 						if ( safeMapping.isVersioned() && property == safeMapping.getVersion() ) {
 							// skip the version property, it was already handled previously.
 							continue;
 						}
 						final Attribute attribute = attributeFactory.buildAttribute( jpa2Mapping, property );
 						if ( attribute != null ) {
 							jpa2Mapping.getBuilder().addAttribute( attribute );
 						}
 					}
 					jpa2Mapping.lock();
 					populateStaticMetamodel( jpa2Mapping );
 				}
 				finally {
                     LOG.trace("Completed entity [" + safeMapping.getEntityName() + "]");
 				}
 			}
 			else if ( MappedSuperclass.class.isAssignableFrom( mapping.getClass() ) ) {
 				@SuppressWarnings( "unchecked" )
 				final MappedSuperclass safeMapping = (MappedSuperclass) mapping;
                 LOG.trace("Starting mapped superclass [" + safeMapping.getMappedClass().getName() + "]");
 				try {
 					final MappedSuperclassTypeImpl<?> jpa2Mapping = mappedSuperclassByMappedSuperclassMapping.get(
 							safeMapping
 					);
 					applyIdMetadata( safeMapping, jpa2Mapping );
 					applyVersionAttribute( safeMapping, jpa2Mapping );
 					Iterator<Property> properties = safeMapping.getDeclaredPropertyIterator();
 					while ( properties.hasNext() ) {
 						final Property property = properties.next();
 						if ( safeMapping.isVersioned() && property == safeMapping.getVersion() ) {
 							// skip the version property, it was already handled previously.
 							continue;
 						}
 						final Attribute attribute = attributeFactory.buildAttribute( jpa2Mapping, property );
 						if ( attribute != null ) {
 							jpa2Mapping.getBuilder().addAttribute( attribute );
 						}
 					}
 					jpa2Mapping.lock();
 					populateStaticMetamodel( jpa2Mapping );
 				}
 				finally {
                     LOG.trace("Completed mapped superclass [" + safeMapping.getMappedClass().getName() + "]");
 				}
 			}
 			else {
 				throw new AssertionFailure( "Unexpected mapping type: " + mapping.getClass() );
 			}
 		}
 
 		for ( EmbeddableTypeImpl embeddable : embeddables.values() ) {
 			populateStaticMetamodel( embeddable );
 		}
 	}
 
 
 	private <X> void applyIdMetadata(PersistentClass persistentClass, EntityTypeImpl<X> jpaEntityType) {
 		if ( persistentClass.hasIdentifierProperty() ) {
 			final Property declaredIdentifierProperty = persistentClass.getDeclaredIdentifierProperty();
 			if (declaredIdentifierProperty != null) {
 				jpaEntityType.getBuilder().applyIdAttribute(
 						attributeFactory.buildIdAttribute( jpaEntityType, declaredIdentifierProperty )
 				);
 			}
 		}
 		else if ( persistentClass.hasIdentifierMapper() ) {
 			@SuppressWarnings( "unchecked")
 			Iterator<Property> propertyIterator = persistentClass.getIdentifierMapper().getPropertyIterator();
 			Set<SingularAttribute<? super X, ?>> attributes = buildIdClassAttributes( jpaEntityType, propertyIterator );
 			jpaEntityType.getBuilder().applyIdClassAttributes( attributes );
 		}
 		else {
 			final KeyValue value = persistentClass.getIdentifier();
 			if (value instanceof Component ) {
 				final Component component = ( Component ) value;
 				if ( component.getPropertySpan() > 1 ) {
 					//FIXME we are an Hibernate embedded id (ie not type)
 				}
 				else {
 					//FIXME take care of declared vs non declared property
 					jpaEntityType.getBuilder().applyIdAttribute(
 						attributeFactory.buildIdAttribute(
 								jpaEntityType,
 								(Property) component.getPropertyIterator().next() )
 					);
 				}
 			}
 		}
 	}
 
 	private <X> void applyIdMetadata(MappedSuperclass mappingType, MappedSuperclassTypeImpl<X> jpaMappingType) {
 		if ( mappingType.hasIdentifierProperty() ) {
 			final Property declaredIdentifierProperty = mappingType.getDeclaredIdentifierProperty();
 			if (declaredIdentifierProperty != null) {
 				jpaMappingType.getBuilder().applyIdAttribute(
 						attributeFactory.buildIdAttribute( jpaMappingType, declaredIdentifierProperty )
 				);
 			}
 		}
 		//an MappedSuperclass can have no identifier if the id is set below in the hierarchy
 		else if ( mappingType.getIdentifierMapper() != null ){
 			@SuppressWarnings( "unchecked")
 			Iterator<Property> propertyIterator = mappingType.getIdentifierMapper().getPropertyIterator();
 			Set<SingularAttribute<? super X, ?>> attributes = buildIdClassAttributes( jpaMappingType, propertyIterator );
 			jpaMappingType.getBuilder().applyIdClassAttributes( attributes );
 		}
 	}
 
 	private <X> void applyVersionAttribute(PersistentClass persistentClass, EntityTypeImpl<X> jpaEntityType) {
 		final Property declaredVersion = persistentClass.getDeclaredVersion();
 		if (declaredVersion != null) {
 			jpaEntityType.getBuilder().applyVersionAttribute(
 					attributeFactory.buildVersionAttribute( jpaEntityType, declaredVersion )
 			);
 		}
 	}
 
 	private <X> void applyVersionAttribute(MappedSuperclass mappingType, MappedSuperclassTypeImpl<X> jpaMappingType) {
 		final Property declaredVersion = mappingType.getDeclaredVersion();
 		if ( declaredVersion != null ) {
 			jpaMappingType.getBuilder().applyVersionAttribute(
 					attributeFactory.buildVersionAttribute( jpaMappingType, declaredVersion )
 			);
 		}
 	}
 
 	private <X> Set<SingularAttribute<? super X, ?>> buildIdClassAttributes(
 			AbstractIdentifiableType<X> ownerType,
 			Iterator<Property> propertyIterator) {
 		LOG.trace("Building old-school composite identifier [" + ownerType.getJavaType().getName() + "]");
 		Set<SingularAttribute<? super X, ?>> attributes	= new HashSet<SingularAttribute<? super X, ?>>();
 		while ( propertyIterator.hasNext() ) {
 			attributes.add( attributeFactory.buildIdAttribute( ownerType, propertyIterator.next() ) );
 		}
 		return attributes;
 	}
 
 	private <X> void populateStaticMetamodel(AbstractManagedType<X> managedType) {
 		final Class<X> managedTypeClass = managedType.getJavaType();
 		if ( managedTypeClass == null ) {
 			// should indicate MAP entity mode, skip...
 			return;
 		}
 		final String metamodelClassName = managedTypeClass.getName() + "_";
 		try {
 			final Class metamodelClass = Class.forName( metamodelClassName, true, managedTypeClass.getClassLoader() );
 			// we found the class; so populate it...
 			registerAttributes( metamodelClass, managedType );
 		}
 		catch ( ClassNotFoundException ignore ) {
 			// nothing to do...
 		}
 
 		// todo : this does not account for @MappeSuperclass, mainly because this is not being tracked in our
 		// internal metamodel as populated from the annotatios properly
 		AbstractManagedType<? super X> superType = managedType.getSupertype();
 		if ( superType != null ) {
 			populateStaticMetamodel( superType );
 		}
 	}
 
 	private final Set<Class> processedMetamodelClasses = new HashSet<Class>();
 
 	private <X> void registerAttributes(Class metamodelClass, AbstractManagedType<X> managedType) {
 		if ( ! processedMetamodelClasses.add( metamodelClass ) ) {
 			return;
 		}
 
 		// push the attributes on to the metamodel class...
 		for ( Attribute<X, ?> attribute : managedType.getDeclaredAttributes() ) {
 			registerAttribute( metamodelClass, attribute );
 		}
 
 		if ( IdentifiableType.class.isInstance( managedType ) ) {
 			final AbstractIdentifiableType<X> entityType = ( AbstractIdentifiableType<X> ) managedType;
 
 			// handle version
 			if ( entityType.hasDeclaredVersionAttribute() ) {
 				registerAttribute( metamodelClass, entityType.getDeclaredVersion() );
 			}
 
 			// handle id-class mappings specially
 			if ( entityType.hasIdClass() ) {
 				final Set<SingularAttribute<? super X, ?>> attributes = entityType.getIdClassAttributesSafely();
 				if ( attributes != null ) {
 					for ( SingularAttribute<? super X, ?> attribute : attributes ) {
 						registerAttribute( metamodelClass, attribute );
 					}
 				}
 			}
 		}
 	}
 
 	private <X> void registerAttribute(Class metamodelClass, Attribute<X, ?> attribute) {
 		final String name = attribute.getName();
 		try {
 			// there is a shortcoming in the existing Hibernate code in terms of the way MappedSuperclass
 			// support was bolted on which comes to bear right here when the attribute is an embeddable type
 			// defined on a MappedSuperclass.  We do not have the correct information to determine the
 			// appropriate attribute declarer in such cases and so the incoming metamodelClass most likely
 			// does not represent the declarer in such cases.
 			//
 			// As a result, in the case of embeddable classes we simply use getField rather than get
 			// getDeclaredField
 			final Field field = attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED
 					? metamodelClass.getField( name )
 					: metamodelClass.getDeclaredField( name );
 			try {
 				if ( ! field.isAccessible() ) {
 					// should be public anyway, but to be sure...
 					field.setAccessible( true );
 				}
 				field.set( null, attribute );
 			}
 			catch ( IllegalAccessException e ) {
 				// todo : exception type?
 				throw new AssertionFailure(
 						"Unable to inject static metamodel attribute : " + metamodelClass.getName() + '#' + name,
 						e
 				);
 			}
 			catch ( IllegalArgumentException e ) {
 				// most likely a mismatch in the type we are injecting and the defined field; this represents a
 				// mismatch in how the annotation processor interpretted the attribute and how our metamodel
 				// and/or annotation binder did.
 
 //              This is particularly the case as arrays are nto handled propery by the StaticMetamodel generator
 
 //				throw new AssertionFailure(
 //						"Illegal argument on static metamodel field injection : " + metamodelClass.getName() + '#' + name
 //								+ "; expected type :  " + attribute.getClass().getName()
 //								+ "; encountered type : " + field.getType().getName()
 //				);
                 LOG.illegalArgumentOnStaticMetamodelFieldInjection(metamodelClass.getName(),
                                                                    name,
                                                                    attribute.getClass().getName(),
                                                                    field.getType().getName());
 			}
 		}
 		catch ( NoSuchFieldException e ) {
             LOG.unableToLocateStaticMetamodelField(metamodelClass.getName(), name);
 //			throw new AssertionFailure(
 //					"Unable to locate static metamodel field : " + metamodelClass.getName() + '#' + name
 //			);
 		}
 	}
 
 	public MappedSuperclassTypeImpl<?> locateMappedSuperclassType(MappedSuperclass mappedSuperclass) {
 		return mappedSuperclassByMappedSuperclassMapping.get(mappedSuperclass);
 	}
 
 	public void pushEntityWorkedOn(PersistentClass persistentClass) {
 		stackOfPersistentClassesBeingProcessed.add(persistentClass);
 	}
 
 	public void popEntityWorkedOn(PersistentClass persistentClass) {
 		final PersistentClass stackTop = stackOfPersistentClassesBeingProcessed.remove(
 				stackOfPersistentClassesBeingProcessed.size() - 1
 		);
 		if (stackTop != persistentClass) {
 			throw new AssertionFailure( "Inconsistent popping: "
 				+ persistentClass.getEntityName() + " instead of " + stackTop.getEntityName() );
 		}
 	}
 
 	private PersistentClass getEntityWorkedOn() {
 		return stackOfPersistentClassesBeingProcessed.get(
 					stackOfPersistentClassesBeingProcessed.size() - 1
 			);
 	}
 
 	public PersistentClass getPersistentClassHostingProperties(MappedSuperclassTypeImpl<?> mappedSuperclassType) {
 		final PersistentClass persistentClass = mappedSuperClassTypeToPersistentClass.get( mappedSuperclassType );
 		if (persistentClass == null) {
 			throw new AssertionFailure( "Could not find PersistentClass for MappedSuperclassType: "
 					+ mappedSuperclassType.getJavaType() );
 		}
 		return persistentClass;
 	}
+
+	public Set<MappedSuperclass> getUnusedMappedSuperclasses() {
+		return new HashSet<MappedSuperclass>( knownMappedSuperclasses );
+	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
index 6a7a5516a3..57e54433e7 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/internal/metamodel/MetamodelImpl.java
@@ -1,236 +1,261 @@
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
 package org.hibernate.jpa.internal.metamodel;
 
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.mapping.MappedSuperclass;
-import org.hibernate.mapping.PersistentClass;
-
+import javax.persistence.metamodel.EmbeddableType;
+import javax.persistence.metamodel.EntityType;
+import javax.persistence.metamodel.ManagedType;
+import javax.persistence.metamodel.MappedSuperclassType;
+import javax.persistence.metamodel.Metamodel;
 import java.io.Serializable;
+import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
-import javax.persistence.metamodel.*;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.collections.CollectionHelper;
+import org.hibernate.jpa.internal.EntityManagerMessageLogger;
+import org.hibernate.jpa.internal.HEMLogging;
+import org.hibernate.mapping.MappedSuperclass;
+import org.hibernate.mapping.PersistentClass;
 
 /**
  * Hibernate implementation of the JPA {@link Metamodel} contract.
  *
  * @author Steve Ebersole
  * @author Emmanuel Bernard
  */
 public class MetamodelImpl implements Metamodel, Serializable {
+	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( MetamodelImpl.class );
+
 	private final Map<Class<?>,EntityTypeImpl<?>> entities;
 	private final Map<Class<?>, EmbeddableTypeImpl<?>> embeddables;
 	private final Map<Class<?>, MappedSuperclassType<?>> mappedSuperclassTypeMap;
     private final Map<String, EntityTypeImpl<?>> entityTypesByEntityName;
 
     /**
    	 * Build the metamodel using the information from the collection of Hibernate
    	 * {@link PersistentClass} models as well as the Hibernate {@link org.hibernate.SessionFactory}.
    	 *
    	 * @param persistentClasses Iterator over the Hibernate (config-time) metamodel
    	 * @param sessionFactory The Hibernate session factory.
    	 * @return The built metamodel
 	 * 
-	 * @deprecated use {@link #buildMetamodel(java.util.Iterator,org.hibernate.engine.spi.SessionFactoryImplementor,boolean)} instead
+	 * @deprecated use {@link #buildMetamodel(Iterator,Set,SessionFactoryImplementor,boolean)} instead
    	 */
 	@Deprecated
    	public static MetamodelImpl buildMetamodel(
    			Iterator<PersistentClass> persistentClasses,
    			SessionFactoryImplementor sessionFactory) {
-        return buildMetamodel(persistentClasses, sessionFactory, false);
+        return buildMetamodel( persistentClasses, Collections.<MappedSuperclass>emptySet(), sessionFactory, false );
    	}
 
 	/**
 	 * Build the metamodel using the information from the collection of Hibernate
 	 * {@link PersistentClass} models as well as the Hibernate {@link org.hibernate.SessionFactory}.
 	 *
 	 * @param persistentClasses Iterator over the Hibernate (config-time) metamodel
+	 * @param mappedSuperclasses All known MappedSuperclasses
 	 * @param sessionFactory The Hibernate session factory.
      * @param ignoreUnsupported ignore unsupported/unknown annotations (like @Any)
+	 *
 	 * @return The built metamodel
 	 */
 	public static MetamodelImpl buildMetamodel(
 			Iterator<PersistentClass> persistentClasses,
+			Set<MappedSuperclass> mappedSuperclasses,
 			SessionFactoryImplementor sessionFactory,
             boolean ignoreUnsupported) {
-		MetadataContext context = new MetadataContext( sessionFactory, ignoreUnsupported );
+		MetadataContext context = new MetadataContext( sessionFactory, mappedSuperclasses, ignoreUnsupported );
 		while ( persistentClasses.hasNext() ) {
 			PersistentClass pc = persistentClasses.next();
 			locateOrBuildEntityType( pc, context );
 		}
+		handleUnusedMappedSuperclasses( context );
 		context.wrapUp();
 		return new MetamodelImpl( context.getEntityTypeMap(), context.getEmbeddableTypeMap(), context.getMappedSuperclassTypeMap(), context.getEntityTypesByEntityName() );
 	}
 
+	private static void handleUnusedMappedSuperclasses(MetadataContext context) {
+		final Set<MappedSuperclass> unusedMappedSuperclasses = context.getUnusedMappedSuperclasses();
+		if ( !unusedMappedSuperclasses.isEmpty() ) {
+			for ( MappedSuperclass mappedSuperclass : unusedMappedSuperclasses ) {
+				log.unusedMappedSuperclass( mappedSuperclass.getMappedClass().getName() );
+				locateOrBuildMappedsuperclassType( mappedSuperclass, context );
+			}
+		}
+	}
+
 	private static EntityTypeImpl<?> locateOrBuildEntityType(PersistentClass persistentClass, MetadataContext context) {
 		EntityTypeImpl<?> entityType = context.locateEntityType( persistentClass );
 		if ( entityType == null ) {
 			entityType = buildEntityType( persistentClass, context );
 		}
 		return entityType;
 	}
 
 	//TODO remove / reduce @SW scope
 	@SuppressWarnings( "unchecked" )
 	private static EntityTypeImpl<?> buildEntityType(PersistentClass persistentClass, MetadataContext context) {
 		final Class javaType = persistentClass.getMappedClass();
 		context.pushEntityWorkedOn(persistentClass);
 		final MappedSuperclass superMappedSuperclass = persistentClass.getSuperMappedSuperclass();
 		AbstractIdentifiableType<?> superType = superMappedSuperclass == null
 				? null
 				: locateOrBuildMappedsuperclassType( superMappedSuperclass, context );
 		//no mappedSuperclass, check for a super entity
 		if (superType == null) {
 			final PersistentClass superPersistentClass = persistentClass.getSuperclass();
 			superType = superPersistentClass == null
 					? null
 					: locateOrBuildEntityType( superPersistentClass, context );
 		}
 		EntityTypeImpl entityType = new EntityTypeImpl(
 				javaType,
 				superType,
 				persistentClass
 		);
 
         context.registerEntityType( persistentClass, entityType );
 		context.popEntityWorkedOn(persistentClass);
 		return entityType;
 	}
 	
 	private static MappedSuperclassTypeImpl<?> locateOrBuildMappedsuperclassType(
 			MappedSuperclass mappedSuperclass, MetadataContext context) {
 		MappedSuperclassTypeImpl<?> mappedSuperclassType = context.locateMappedSuperclassType( mappedSuperclass );
 		if ( mappedSuperclassType == null ) {
 			mappedSuperclassType = buildMappedSuperclassType(mappedSuperclass, context);
 		}
 		return mappedSuperclassType;
 	}
 
 	//TODO remove / reduce @SW scope
 	@SuppressWarnings( "unchecked" )
 	private static MappedSuperclassTypeImpl<?> buildMappedSuperclassType(
 			MappedSuperclass mappedSuperclass,
 			MetadataContext context) {
 		final MappedSuperclass superMappedSuperclass = mappedSuperclass.getSuperMappedSuperclass();
 		AbstractIdentifiableType<?> superType = superMappedSuperclass == null
 				? null
 				: locateOrBuildMappedsuperclassType( superMappedSuperclass, context );
 		//no mappedSuperclass, check for a super entity
 		if (superType == null) {
 			final PersistentClass superPersistentClass = mappedSuperclass.getSuperPersistentClass();
 			superType = superPersistentClass == null
 					? null
 					: locateOrBuildEntityType( superPersistentClass, context );
 		}
 		final Class javaType = mappedSuperclass.getMappedClass();
 		MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(
 				javaType,
 				mappedSuperclass,
 				superType
 		);
 		context.registerMappedSuperclassType( mappedSuperclass, mappedSuperclassType );
 		return mappedSuperclassType;
 	}
 
 	/**
 	 * Instantiate the metamodel.
 	 *
 	 * @param entities The entity mappings.
 	 * @param embeddables The embeddable (component) mappings.
 	 * @param mappedSuperclassTypeMap The {@link javax.persistence.MappedSuperclass} mappings
 	 */
 	private MetamodelImpl(
 			Map<Class<?>, EntityTypeImpl<?>> entities,
 			Map<Class<?>, EmbeddableTypeImpl<?>> embeddables,
             Map<Class<?>, MappedSuperclassType<?>> mappedSuperclassTypeMap,
             Map<String, EntityTypeImpl<?>> entityTypesByEntityName) {
 		this.entities = entities;
 		this.embeddables = embeddables;
 		this.mappedSuperclassTypeMap = mappedSuperclassTypeMap;
         this.entityTypesByEntityName = entityTypesByEntityName;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> EntityType<X> entity(Class<X> cls) {
 		final EntityType<?> entityType = entities.get( cls );
 		if ( entityType == null ) {
 			throw new IllegalArgumentException( "Not an entity: " + cls );
 		}
 		return (EntityType<X>) entityType;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> ManagedType<X> managedType(Class<X> cls) {
 		ManagedType<?> type = entities.get( cls );
 		if ( type == null ) {
 			type = mappedSuperclassTypeMap.get( cls );
 		}
 		if ( type == null ) {
 			type = embeddables.get( cls );
 		}
 		if ( type == null ) {
 			throw new IllegalArgumentException( "Not an managed type: " + cls );
 		}
 		return (ManagedType<X>) type;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X> EmbeddableType<X> embeddable(Class<X> cls) {
 		final EmbeddableType<?> embeddableType = embeddables.get( cls );
 		if ( embeddableType == null ) {
 			throw new IllegalArgumentException( "Not an embeddable: " + cls );
 		}
 		return (EmbeddableType<X>) embeddableType;
 	}
 
 	@Override
 	public Set<ManagedType<?>> getManagedTypes() {
 		final int setSize = CollectionHelper.determineProperSizing(
 				entities.size() + mappedSuperclassTypeMap.size() + embeddables.size()
 		);
 		final Set<ManagedType<?>> managedTypes = new HashSet<ManagedType<?>>( setSize );
 		managedTypes.addAll( entities.values() );
 		managedTypes.addAll( mappedSuperclassTypeMap.values() );
 		managedTypes.addAll( embeddables.values() );
 		return managedTypes;
 	}
 
 	@Override
 	public Set<EntityType<?>> getEntities() {
 		return new HashSet<EntityType<?>>( entityTypesByEntityName.values() );
 	}
 
 	@Override
 	public Set<EmbeddableType<?>> getEmbeddables() {
 		return new HashSet<EmbeddableType<?>>( embeddables.values() );
 	}
 
 	public EntityTypeImpl getEntityTypeByName(String entityName) {
 		return entityTypesByEntityName.get( entityName );
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metadata/MetadataTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metadata/MetadataTest.java
index a337dbf6f6..f700de9e36 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metadata/MetadataTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metadata/MetadataTest.java
@@ -1,456 +1,458 @@
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
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.jpa.test.metadata;
 
+import java.util.Collections;
 import java.util.Set;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.Bindable;
 import javax.persistence.metamodel.EmbeddableType;
 import javax.persistence.metamodel.EntityType;
 import javax.persistence.metamodel.IdentifiableType;
 import javax.persistence.metamodel.ListAttribute;
 import javax.persistence.metamodel.ManagedType;
 import javax.persistence.metamodel.MapAttribute;
 import javax.persistence.metamodel.MappedSuperclassType;
 import javax.persistence.metamodel.PluralAttribute;
 import javax.persistence.metamodel.SetAttribute;
 import javax.persistence.metamodel.SingularAttribute;
 import javax.persistence.metamodel.Type;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.jpa.internal.metamodel.MetamodelImpl;
 import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.mapping.MappedSuperclass;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Emmanuel Bernard
  */
 public class MetadataTest extends BaseEntityManagerFunctionalTestCase {
 	@Test
 	public void testBaseOfService() throws Exception {
 		EntityManagerFactory emf = entityManagerFactory();
 		assertNotNull( emf.getMetamodel() );
 		final EntityType<Fridge> entityType = emf.getMetamodel().entity( Fridge.class );
 		assertNotNull( entityType );
 	}
 
 	@Test
 	public void testInvalidAttributeCausesIllegalArgumentException() {
 		// should not matter the exact subclass of ManagedType since this is implemented on the base class but
 		// check each anyway..
 
 		// entity
 		checkNonExistentAttributeAccess( entityManagerFactory().getMetamodel().entity( Fridge.class ) );
 
 		// embeddable
 		checkNonExistentAttributeAccess( entityManagerFactory().getMetamodel().embeddable( Address.class ) );
 	}
 
 	private void checkNonExistentAttributeAccess(ManagedType managedType) {
 		final String NAME = "NO_SUCH_ATTRIBUTE";
 		try {
 			managedType.getAttribute( NAME );
 			fail( "Lookup of non-existent attribute (getAttribute) should have caused IAE : " + managedType );
 		}
 		catch (IllegalArgumentException expected) {
 		}
 		try {
 			managedType.getSingularAttribute( NAME );
 			fail( "Lookup of non-existent attribute (getSingularAttribute) should have caused IAE : " + managedType );
 		}
 		catch (IllegalArgumentException expected) {
 		}
 		try {
 			managedType.getCollection( NAME );
 			fail( "Lookup of non-existent attribute (getCollection) should have caused IAE : " + managedType );
 		}
 		catch (IllegalArgumentException expected) {
 		}
 	}
 
 	@Test
 	@SuppressWarnings({ "unchecked" })
 	public void testBuildingMetamodelWithParameterizedCollection() {
 		Configuration cfg = new Configuration( );
 //		configure( cfg );
 		cfg.addAnnotatedClass( WithGenericCollection.class );
 		cfg.buildMappings();
 		SessionFactoryImplementor sfi = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry() );
-		MetamodelImpl.buildMetamodel( cfg.getClassMappings(), sfi, true );
+		MetamodelImpl.buildMetamodel( cfg.getClassMappings(), Collections.<MappedSuperclass>emptySet(), sfi, true );
 		sfi.close();
 	}
 
 	@Test
 	public void testLogicalManyToOne() throws Exception {
 		final EntityType<JoinedManyToOneOwner> entityType = entityManagerFactory().getMetamodel().entity( JoinedManyToOneOwner.class );
 		final SingularAttribute attr = entityType.getDeclaredSingularAttribute( "house" );
 		assertEquals( Attribute.PersistentAttributeType.MANY_TO_ONE, attr.getPersistentAttributeType() );
 		assertEquals( House.class, attr.getBindableJavaType() );
 		final EntityType<House> houseType = entityManagerFactory().getMetamodel().entity( House.class );
 		assertEquals( houseType.getBindableJavaType(), attr.getBindableJavaType() );
 	}
 
 	@Test
 	public void testEntity() throws Exception {
 		final EntityType<Fridge> fridgeType = entityManagerFactory().getMetamodel().entity( Fridge.class );
 		assertEquals( Fridge.class, fridgeType.getBindableJavaType() );
 		assertEquals( Bindable.BindableType.ENTITY_TYPE, fridgeType.getBindableType() );
 		SingularAttribute<Fridge,Integer> wrapped = fridgeType.getDeclaredSingularAttribute( "temperature", Integer.class );
 		assertNotNull( wrapped );
 		SingularAttribute<Fridge,Integer> primitive = fridgeType.getDeclaredSingularAttribute( "temperature", int.class );
 		assertNotNull( primitive );
 		assertNotNull( fridgeType.getDeclaredSingularAttribute( "temperature" ) );
 		assertNotNull( fridgeType.getDeclaredAttribute( "temperature" ) );
 		final SingularAttribute<Fridge, Long> id = fridgeType.getDeclaredId( Long.class );
 		assertNotNull( id );
 		assertTrue( id.isId() );
 		try {
 			fridgeType.getDeclaredId( java.util.Date.class );
 			fail( "expecting failure" );
 		}
 		catch ( IllegalArgumentException ignore ) {
 			// expected result
 		}
 		final SingularAttribute<? super Fridge, Long> id2 = fridgeType.getId( Long.class );
 		assertNotNull( id2 );
 
 		assertEquals( "Fridge", fridgeType.getName() );
 		assertEquals( Long.class, fridgeType.getIdType().getJavaType() );
 		assertTrue( fridgeType.hasSingleIdAttribute() );
 		assertFalse( fridgeType.hasVersionAttribute() );
 		assertEquals( Type.PersistenceType.ENTITY, fridgeType.getPersistenceType() );
 
 		assertEquals( 3, fridgeType.getDeclaredAttributes().size() );
 
 		final EntityType<House> houseType = entityManagerFactory().getMetamodel().entity( House.class );
 		assertEquals( "House", houseType.getName() );
 		assertTrue( houseType.hasSingleIdAttribute() );
 		final SingularAttribute<House, House.Key> houseId = houseType.getDeclaredId( House.Key.class );
 		assertNotNull( houseId );
 		assertTrue( houseId.isId() );
 		assertEquals( Attribute.PersistentAttributeType.EMBEDDED, houseId.getPersistentAttributeType() );
 		
 		final EntityType<Person> personType = entityManagerFactory().getMetamodel().entity( Person.class );
 		assertEquals( "Homo", personType.getName() );
 		assertFalse( personType.hasSingleIdAttribute() );
 		final Set<SingularAttribute<? super Person,?>> ids = personType.getIdClassAttributes();
 		assertNotNull( ids );
 		assertEquals( 2, ids.size() );
 		for (SingularAttribute<? super Person,?> localId : ids) {
 			assertTrue( localId.isId() );
 			assertSame( personType, localId.getDeclaringType() );
 			assertSame( localId, personType.getDeclaredAttribute( localId.getName() ) );
 			assertSame( localId, personType.getDeclaredSingularAttribute( localId.getName() ) );
 			assertSame( localId, personType.getAttribute( localId.getName() ) );
 			assertSame( localId, personType.getSingularAttribute( localId.getName() ) );
 			assertTrue( personType.getAttributes().contains( localId ) );
 		}
 
 		final EntityType<Giant> giantType = entityManagerFactory().getMetamodel().entity( Giant.class );
 		assertEquals( "HomoGigantus", giantType.getName() );
 		assertFalse( giantType.hasSingleIdAttribute() );
 		final Set<SingularAttribute<? super Giant,?>> giantIds = giantType.getIdClassAttributes();
 		assertNotNull( giantIds );
 		assertEquals( 2, giantIds.size() );
 		assertEquals( personType.getIdClassAttributes(), giantIds );
 		for (SingularAttribute<? super Giant,?> localGiantId : giantIds) {
 			assertTrue( localGiantId.isId() );
 			try {
 				giantType.getDeclaredAttribute( localGiantId.getName() );
 				fail( localGiantId.getName() + " is a declared attribute, but shouldn't be");
 			}
 			catch ( IllegalArgumentException ex) {
 				// expected
 			}
 			try {
 				giantType.getDeclaredSingularAttribute( localGiantId.getName() );
 				fail( localGiantId.getName() + " is a declared singular attribute, but shouldn't be");
 			}
 			catch ( IllegalArgumentException ex) {
 				// expected
 			}
 			assertSame( localGiantId, giantType.getAttribute( localGiantId.getName() ) );
 			assertTrue( giantType.getAttributes().contains( localGiantId ) );
 		}
 
 		final EntityType<FoodItem> foodType = entityManagerFactory().getMetamodel().entity( FoodItem.class );
 		assertTrue( foodType.hasVersionAttribute() );
 		final SingularAttribute<? super FoodItem, Long> version = foodType.getVersion( Long.class );
 		assertNotNull( version );
 		assertTrue( version.isVersion() );
 		assertEquals( 3, foodType.getDeclaredAttributes().size() );
 
 	}
 
 	@Test
 	public void testBasic() throws Exception {
 		final EntityType<Fridge> entityType = entityManagerFactory().getMetamodel().entity( Fridge.class );
 		final SingularAttribute<? super Fridge,Integer> singularAttribute = entityType.getDeclaredSingularAttribute(
 				"temperature",
 				Integer.class
 		);
 //		assertEquals( Integer.class, singularAttribute.getBindableJavaType() );
 //		assertEquals( Integer.class, singularAttribute.getType().getJavaType() );
 		assertEquals( int.class, singularAttribute.getBindableJavaType() );
 		assertEquals( int.class, singularAttribute.getType().getJavaType() );
 		assertEquals( Bindable.BindableType.SINGULAR_ATTRIBUTE, singularAttribute.getBindableType() );
 		assertFalse( singularAttribute.isId() );
 		assertFalse( singularAttribute.isOptional() );
 		assertFalse( entityType.getDeclaredSingularAttribute( "brand", String.class ).isOptional() );
 		assertEquals( Type.PersistenceType.BASIC, singularAttribute.getType().getPersistenceType() );
 		final Attribute<? super Fridge, ?> attribute = entityType.getDeclaredAttribute( "temperature" );
 		assertNotNull( attribute );
 		assertEquals( "temperature", attribute.getName() );
 		assertEquals( Fridge.class, attribute.getDeclaringType().getJavaType() );
 		assertEquals( Attribute.PersistentAttributeType.BASIC, attribute.getPersistentAttributeType() );
 //		assertEquals( Integer.class, attribute.getJavaType() );
 		assertEquals( int.class, attribute.getJavaType() );
 		assertFalse( attribute.isAssociation() );
 		assertFalse( attribute.isCollection() );
 
 		boolean found = false;
 		for (Attribute<Fridge, ?> attr : entityType.getDeclaredAttributes() ) {
 			if ("temperature".equals( attr.getName() ) ) {
 				found = true;
 				break;
 			}
 		}
 		assertTrue( found );
 	}
 
 	@Test
 	public void testEmbeddable() throws Exception {
 		final EntityType<House> entityType = entityManagerFactory().getMetamodel().entity( House.class );
 		final SingularAttribute<? super House,Address> address = entityType.getDeclaredSingularAttribute(
 				"address",
 				Address.class
 		);
 		assertNotNull( address );
 		assertEquals( Attribute.PersistentAttributeType.EMBEDDED, address.getPersistentAttributeType() );
 		assertFalse( address.isCollection() );
 		assertFalse( address.isAssociation() );
 		final EmbeddableType<Address> addressType = (EmbeddableType<Address>) address.getType();
 		assertEquals( Type.PersistenceType.EMBEDDABLE, addressType.getPersistenceType() );
 		assertEquals( 3, addressType.getDeclaredAttributes().size() );
 		assertTrue( addressType.getDeclaredSingularAttribute( "address1" ).isOptional() );
 		assertFalse( addressType.getDeclaredSingularAttribute( "address2" ).isOptional() );
 
 		final EmbeddableType<Address> directType = entityManagerFactory().getMetamodel().embeddable( Address.class );
 		assertNotNull( directType );
 		assertEquals( Type.PersistenceType.EMBEDDABLE, directType.getPersistenceType() );
 	}
 
 	@Test
 	public void testCollection() throws Exception {
 		final EntityType<Garden> entiytype = entityManagerFactory().getMetamodel().entity( Garden.class );
 		final Set<PluralAttribute<? super Garden, ?, ?>> attributes = entiytype.getPluralAttributes();
 		assertEquals( 1, attributes.size() );
 		PluralAttribute<? super Garden, ?, ?> flowers = attributes.iterator().next();
 		assertTrue( flowers instanceof ListAttribute );
 	}
 
 	@Test
 	public void testElementCollection() throws Exception {
 		final EntityType<House> entityType = entityManagerFactory().getMetamodel().entity( House.class );
 		final SetAttribute<House,Room> rooms = entityType.getDeclaredSet( "rooms", Room.class );
 		assertNotNull( rooms );
 		assertTrue( rooms.isAssociation() );
 		assertTrue( rooms.isCollection() );
 		assertEquals( Attribute.PersistentAttributeType.ELEMENT_COLLECTION, rooms.getPersistentAttributeType() );
 		assertEquals( Room.class, rooms.getBindableJavaType() );
 		assertEquals( Bindable.BindableType.PLURAL_ATTRIBUTE, rooms.getBindableType() );
 		assertEquals( Set.class, rooms.getJavaType() );
 		assertEquals( PluralAttribute.CollectionType.SET, rooms.getCollectionType() );
 		assertEquals( 3, entityType.getDeclaredPluralAttributes().size() );
 		assertEquals( Type.PersistenceType.EMBEDDABLE, rooms.getElementType().getPersistenceType() );
 
 		final MapAttribute<House,String,Room> roomsByName = entityType.getDeclaredMap(
 				"roomsByName", String.class, Room.class
 		);
 		assertNotNull( roomsByName );
 		assertEquals( String.class, roomsByName.getKeyJavaType() );
 		assertEquals( Type.PersistenceType.BASIC, roomsByName.getKeyType().getPersistenceType() );
 		assertEquals( PluralAttribute.CollectionType.MAP, roomsByName.getCollectionType() );
 
 		final ListAttribute<House,Room> roomsBySize = entityType.getDeclaredList( "roomsBySize", Room.class );
 		assertNotNull( roomsBySize );
 		assertEquals( Type.PersistenceType.EMBEDDABLE, roomsBySize.getElementType().getPersistenceType() );
 		assertEquals( PluralAttribute.CollectionType.LIST, roomsBySize.getCollectionType() );
 	}
 
 	@Test
 	public void testHierarchy() {
 		final EntityType<Cat> cat = entityManagerFactory().getMetamodel().entity( Cat.class );
 		assertNotNull( cat );
 		assertEquals( 7, cat.getAttributes().size() );
 		assertEquals( 1, cat.getDeclaredAttributes().size() );
 		ensureProperMember(cat.getDeclaredAttributes());
 
 		assertTrue( cat.hasVersionAttribute() );
 		assertEquals( "version", cat.getVersion(Long.class).getName() );
 		verifyDeclaredVersionNotPresent( cat );
 		verifyDeclaredIdNotPresentAndIdPresent(cat);
 
 		assertEquals( Type.PersistenceType.MAPPED_SUPERCLASS, cat.getSupertype().getPersistenceType() );
 		MappedSuperclassType<Cattish> cattish = (MappedSuperclassType<Cattish>) cat.getSupertype();
 		assertEquals( 6, cattish.getAttributes().size() );
 		assertEquals( 1, cattish.getDeclaredAttributes().size() );
 		ensureProperMember(cattish.getDeclaredAttributes());
 
 		assertTrue( cattish.hasVersionAttribute() );
 		assertEquals( "version", cattish.getVersion(Long.class).getName() );
 		verifyDeclaredVersionNotPresent( cattish );
 		verifyDeclaredIdNotPresentAndIdPresent(cattish);
 
 		assertEquals( Type.PersistenceType.ENTITY, cattish.getSupertype().getPersistenceType() );
 		EntityType<Feline> feline = (EntityType<Feline>) cattish.getSupertype();
 		assertEquals( 5, feline.getAttributes().size() );
 		assertEquals( 1, feline.getDeclaredAttributes().size() );
 		ensureProperMember(feline.getDeclaredAttributes());
 
 		assertTrue( feline.hasVersionAttribute() );
 		assertEquals( "version", feline.getVersion(Long.class).getName() );
 		verifyDeclaredVersionNotPresent( feline );
 		verifyDeclaredIdNotPresentAndIdPresent(feline);
 
 		assertEquals( Type.PersistenceType.MAPPED_SUPERCLASS, feline.getSupertype().getPersistenceType() );
 		MappedSuperclassType<Animal> animal = (MappedSuperclassType<Animal>) feline.getSupertype();
 		assertEquals( 4, animal.getAttributes().size() );
 		assertEquals( 2, animal.getDeclaredAttributes().size() );
 		ensureProperMember(animal.getDeclaredAttributes());
 
 		assertTrue( animal.hasVersionAttribute() );
 		assertEquals( "version", animal.getVersion(Long.class).getName() );
 		verifyDeclaredVersionNotPresent( animal );
 		assertEquals( "id", animal.getId(Long.class).getName() );
 		final SingularAttribute<Animal, Long> id = animal.getDeclaredId( Long.class );
 		assertEquals( "id", id.getName() );
 		assertNotNull( id.getJavaMember() );
 
 		assertEquals( Type.PersistenceType.MAPPED_SUPERCLASS, animal.getSupertype().getPersistenceType() );
 		MappedSuperclassType<Thing> thing = (MappedSuperclassType<Thing>) animal.getSupertype();
 		assertEquals( 2, thing.getAttributes().size() );
 		assertEquals( 2, thing.getDeclaredAttributes().size() );
 		ensureProperMember(thing.getDeclaredAttributes());
 		final SingularAttribute<Thing, Double> weight = thing.getDeclaredSingularAttribute( "weight", Double.class );
 		assertEquals( Double.class, weight.getJavaType() );
 
 		assertEquals( "version", thing.getVersion(Long.class).getName() );
 		final SingularAttribute<Thing, Long> version = thing.getDeclaredVersion( Long.class );
 		assertEquals( "version", version.getName() );
 		assertNotNull( version.getJavaMember() );
 		assertNull( thing.getId( Long.class ) );
 
 		assertNull( thing.getSupertype() );
 	}
 
 	@Test
 	public void testBackrefAndGenerics() throws Exception {
 		final EntityType<Parent> parent = entityManagerFactory().getMetamodel().entity( Parent.class );
 		assertNotNull( parent );
 		final SetAttribute<? super Parent, ?> children = parent.getSet( "children" );
 		assertNotNull( children );
 		assertEquals( 1, parent.getPluralAttributes().size() );
 		assertEquals( 4, parent.getAttributes().size() );
 		final EntityType<Child> child = entityManagerFactory().getMetamodel().entity( Child.class );
 		assertNotNull( child );
 		assertEquals( 2, child.getAttributes().size() );
 		final SingularAttribute<? super Parent, Parent.Relatives> attribute = parent.getSingularAttribute(
 				"siblings", Parent.Relatives.class
 		);
 		final EmbeddableType<Parent.Relatives> siblings = (EmbeddableType<Parent.Relatives>) attribute.getType();
 		assertNotNull(siblings);
 		final SetAttribute<? super Parent.Relatives, ?> siblingsCollection = siblings.getSet( "siblings" );
 		assertNotNull( siblingsCollection );
 		final Type<?> collectionElement = siblingsCollection.getElementType();
 		assertNotNull( collectionElement );
 		assertEquals( collectionElement, child );
 	}
 
 	private void ensureProperMember(Set<?> attributes) {
 		//we do not update the set so we are safe
 		@SuppressWarnings( "unchecked" )
 		final Set<Attribute<?, ?>> safeAttributes = ( Set<Attribute<?, ?>> ) attributes;
 		for (Attribute<?,?> attribute : safeAttributes ) {
 			final String name = attribute.getJavaMember().getName();
 			assertNotNull( attribute.getJavaMember() );
 			assertTrue( name.toLowerCase().endsWith( attribute.getName().toLowerCase() ) );
 		}
 	}
 
 	private void verifyDeclaredIdNotPresentAndIdPresent(IdentifiableType<?> type) {
 		assertEquals( "id", type.getId(Long.class).getName() );
 		try {
 			type.getDeclaredId(Long.class);
 			fail("Should not have a declared id");
 		}
 		catch (IllegalArgumentException e) {
 			//success
 		}
 	}
 
 	private void verifyDeclaredVersionNotPresent(IdentifiableType<?> type) {
 		try {
 			type.getDeclaredVersion(Long.class);
 			fail("Should not have a declared version");
 		}
 		catch (IllegalArgumentException e) {
 			//success
 		}
 	}
 
 	//todo test plural
 
 	@Override
 	public Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Fridge.class,
 				FoodItem.class,
 				Person.class,
 				Giant.class,
 				House.class,
 				Dog.class,
 				Cat.class,
 				Cattish.class,
 				Feline.class,
 				Garden.class,
 				Flower.class,
 				JoinedManyToOneOwner.class,
 				Parent.class,
 				Child.class
 		};
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/MappedSuperclassType2Test.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/MappedSuperclassType2Test.java
new file mode 100644
index 0000000000..c4e62eed5a
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/metamodel/MappedSuperclassType2Test.java
@@ -0,0 +1,78 @@
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
+package org.hibernate.jpa.test.metamodel;
+
+import javax.persistence.EntityManagerFactory;
+import javax.persistence.metamodel.ManagedType;
+import java.util.Arrays;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.test.PersistenceUnitDescriptorAdapter;
+
+import org.junit.Test;
+
+import org.hibernate.testing.FailureExpected;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertNotNull;
+
+/**
+ * Specifically see if we can access a MappedSuperclass via Metamodel that is not part of a entity hierarchy
+ *
+ * @author Steve Ebersole
+ */
+public class MappedSuperclassType2Test extends BaseUnitTestCase {
+	@Test
+	@TestForIssue( jiraKey = "HHH-8534" )
+	@FailureExpected( jiraKey = "HHH-8534" )
+	public void testMappedSuperclassAccessNoEntity() {
+		// stupid? yes.  tck does it? yes.
+
+		final PersistenceUnitDescriptorAdapter pu = new PersistenceUnitDescriptorAdapter() {
+			@Override
+			public List<String> getManagedClassNames() {
+				// pass in a MappedSuperclass that is not used in any entity hierarchy
+				return Arrays.asList( SomeMappedSuperclass.class.getName() );
+			}
+		};
+
+		final Map settings = new HashMap();
+		settings.put( AvailableSettings.HBM2DDL_AUTO, "create-drop" );
+
+		EntityManagerFactory emf = Bootstrap.getEntityManagerFactoryBuilder( pu, settings ).build();
+		try {
+			ManagedType<SomeMappedSuperclass> type = emf.getMetamodel().managedType( SomeMappedSuperclass.class );
+			// the issue was in regards to throwing an exception, but also check for nullness
+			assertNotNull( type );
+		}
+		finally {
+			emf.close();
+		}
+	}
+}
