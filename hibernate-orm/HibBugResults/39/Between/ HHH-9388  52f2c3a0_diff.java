diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index f8a13e93da..1989d92e9f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1355 +1,1354 @@
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
 import javax.persistence.Converter;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
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
-import org.hibernate.cfg.naming.DefaultNamingStrategyDelegator;
 import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
 import org.hibernate.cfg.naming.NamingStrategyDelegator;
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
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
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
 	private NamingStrategyDelegator namingStrategyDelegator;
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
-		namingStrategyDelegator = DefaultNamingStrategyDelegator.INSTANCE;
+		namingStrategyDelegator = LegacyNamingStrategyDelegator.DEFAULT_INSTANCE;
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
 	 * Get a copy of all known MappedSuperclasses
 	 * <p/>
 	 * EXPERIMENTAL Consider this API as PRIVATE
 	 *
 	 * @return Set of all known MappedSuperclasses
 	 */
 	public java.util.Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
 		return new HashSet<MappedSuperclass>( mappedSuperClasses.values() );
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
 			if (key instanceof String) {
 				key = normalizer.normalizeIdentifierQuoting( (String) key );
 			}
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
index e0a86fce95..0cfc01922f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
@@ -1,703 +1,704 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.ColumnDefault;
 import org.hibernate.annotations.ColumnTransformer;
 import org.hibernate.annotations.ColumnTransformers;
 import org.hibernate.annotations.Index;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.cfg.naming.NamingStrategyDelegate;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 
 import org.jboss.logging.Logger;
 
 /**
  * Wrap state of an EJB3 @Column annotation
  * and build the Hibernate column mapping element
  *
  * @author Emmanuel Bernard
  */
 public class Ejb3Column {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Ejb3Column.class.getName());
 
 	private Column mappingColumn;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private String explicitTableName;
 	protected Map<String, Join> joins;
 	protected PropertyHolder propertyHolder;
 	private Mappings mappings;
 	private boolean isImplicit;
 	public static final int DEFAULT_COLUMN_LENGTH = 255;
 	public String sqlType;
 	private int length = DEFAULT_COLUMN_LENGTH;
 	private int precision;
 	private int scale;
 	private String logicalColumnName;
 	private String propertyName;
 	private boolean unique;
 	private boolean nullable = true;
 	private String formulaString;
 	private Formula formula;
 	private Table table;
 	private String readExpression;
 	private String writeExpression;
 
 	private String defaultValue;
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public String getLogicalColumnName() {
 		return logicalColumnName;
 	}
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public int getLength() {
 		return length;
 	}
 
 	public int getPrecision() {
 		return precision;
 	}
 
 	public int getScale() {
 		return scale;
 	}
 
 	public boolean isUnique() {
 		return unique;
 	}
 
 	public boolean isFormula() {
 		return StringHelper.isNotEmpty( formulaString );
 	}
 
 	public String getFormulaString() {
 		return formulaString;
 	}
 
 	/**
 	 * Deprecated as this is badly named for its use.
 	 *
 	 * @deprecated Use {@link #getExplicitTableName} instead
 	 */
 	@Deprecated
 	public String getSecondaryTableName() {
 		return explicitTableName;
 	}
 
 	public String getExplicitTableName() {
 		return explicitTableName;
 	}
 
 	/**
 	 * Deprecated as this is badly named for its use.
 	 *
 	 * @deprecated Use {@link #setExplicitTableName} instead
 	 */
 	@Deprecated
 	public void setSecondaryTableName(String explicitTableName) {
 		setExplicitTableName( explicitTableName );
 	}
 
 	public void setExplicitTableName(String explicitTableName) {
 		if ( "``".equals( explicitTableName ) ) {
 			this.explicitTableName = "";
 		}
 		else {
 			this.explicitTableName = explicitTableName;
 		}
 	}
 
 	public void setFormula(String formula) {
 		this.formulaString = formula;
 	}
 
 	public boolean isImplicit() {
 		return isImplicit;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	protected Mappings getMappings() {
 		return mappings;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public void setImplicit(boolean implicit) {
 		isImplicit = implicit;
 	}
 
 	public void setSqlType(String sqlType) {
 		this.sqlType = sqlType;
 	}
 
 	public void setLength(int length) {
 		this.length = length;
 	}
 
 	public void setPrecision(int precision) {
 		this.precision = precision;
 	}
 
 	public void setScale(int scale) {
 		this.scale = scale;
 	}
 
 	public void setLogicalColumnName(String logicalColumnName) {
 		this.logicalColumnName = logicalColumnName;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	public void setUnique(boolean unique) {
 		this.unique = unique;
 	}
 
 	public boolean isNullable() {
 		return mappingColumn.isNullable();
 	}
 
 	public String getDefaultValue() {
 		return defaultValue;
 	}
 
 	public void setDefaultValue(String defaultValue) {
 		this.defaultValue = defaultValue;
 	}
 
 	public Ejb3Column() {
 	}
 
 	public void bind() {
 		if ( StringHelper.isNotEmpty( formulaString ) ) {
 			LOG.debugf( "Binding formula %s", formulaString );
 			formula = new Formula();
 			formula.setFormula( formulaString );
 		}
 		else {
 			initMappingColumn(
 					logicalColumnName, propertyName, length, precision, scale, nullable, sqlType, unique, true
 			);
 			if ( defaultValue != null ) {
 				mappingColumn.setDefaultValue( defaultValue );
 			}
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Binding column: %s", toString() );
 			}
 		}
 	}
 
 	protected void initMappingColumn(
 			String columnName,
 			String propertyName,
 			int length,
 			int precision,
 			int scale,
 			boolean nullable,
 			String sqlType,
 			boolean unique,
 			boolean applyNamingStrategy) {
 		if ( StringHelper.isNotEmpty( formulaString ) ) {
 			this.formula = new Formula();
 			this.formula.setFormula( formulaString );
 		}
 		else {
 			this.mappingColumn = new Column();
 			redefineColumnName( columnName, propertyName, applyNamingStrategy );
 			this.mappingColumn.setLength( length );
 			if ( precision > 0 ) {  //revelent precision
 				this.mappingColumn.setPrecision( precision );
 				this.mappingColumn.setScale( scale );
 			}
 			this.mappingColumn.setNullable( nullable );
 			this.mappingColumn.setSqlType( sqlType );
 			this.mappingColumn.setUnique( unique );
 
 			if(writeExpression != null && !writeExpression.matches("[^?]*\\?[^?]*")) {
 				throw new AnnotationException(
 						"@WriteExpression must contain exactly one value placeholder ('?') character: property ["
 								+ propertyName + "] and column [" + logicalColumnName + "]"
 				);
 			}
 			if ( readExpression != null) {
 				this.mappingColumn.setCustomRead( readExpression );
 			}
 			if ( writeExpression != null) {
 				this.mappingColumn.setCustomWrite( writeExpression );
 			}
 		}
 	}
 
 	public boolean isNameDeferred() {
 		return mappingColumn == null || StringHelper.isEmpty( mappingColumn.getName() );
 	}
 
 	public void redefineColumnName(String columnName, String propertyName, boolean applyNamingStrategy) {
 		if ( applyNamingStrategy ) {
 			if ( StringHelper.isEmpty( columnName ) ) {
 				if ( propertyName != null ) {
 					mappingColumn.setName(
 							mappings.getObjectNameNormalizer().normalizeIdentifierQuoting(
-									getNamingStrategyDelegate().determineAttributeColumnName( propertyName )
+									getNamingStrategyDelegate().determineImplicitPropertyColumnName( propertyName )
 							)
 					);
 				}
 				//Do nothing otherwise
 			}
 			else {
 				columnName = mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( columnName );
 				columnName = getNamingStrategyDelegate().toPhysicalColumnName( columnName );
 				columnName = mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( columnName );
 				mappingColumn.setName( columnName );
 			}
 		}
 		else {
 			if ( StringHelper.isNotEmpty( columnName ) ) {
 				mappingColumn.setName( mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( columnName ) );
 			}
 		}
 	}
 
 	public String getName() {
 		return mappingColumn.getName();
 	}
 
 	public Column getMappingColumn() {
 		return mappingColumn;
 	}
 
 	public boolean isInsertable() {
 		return insertable;
 	}
 
 	public boolean isUpdatable() {
 		return updatable;
 	}
 
 	public void setNullable(boolean nullable) {
 		if ( mappingColumn != null ) {
 			mappingColumn.setNullable( nullable );
 		}
 		else {
 			this.nullable = nullable;
 		}
 	}
 
 	public void setJoins(Map<String, Join> joins) {
 		this.joins = joins;
 	}
 
 	public PropertyHolder getPropertyHolder() {
 		return propertyHolder;
 	}
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	protected void setMappingColumn(Column mappingColumn) {
 		this.mappingColumn = mappingColumn;
 	}
 
 	public void linkWithValue(SimpleValue value) {
 		if ( formula != null ) {
 			value.addFormula( formula );
 		}
 		else {
 			getMappingColumn().setValue( value );
 			value.addColumn( getMappingColumn() );
 			value.getTable().addColumn( getMappingColumn() );
 			addColumnBinding( value );
 			table = value.getTable();
 		}
 	}
 
 	protected void addColumnBinding(SimpleValue value) {
-		String logicalColumnName = getNamingStrategyDelegate().logicalColumnName( this.logicalColumnName, propertyName );
+		String logicalColumnName =
+				getNamingStrategyDelegate().determineLogicalColumnName( this.logicalColumnName, propertyName );
 		mappings.addColumnBinding( logicalColumnName, getMappingColumn(), value.getTable() );
 	}
 
 	protected NamingStrategyDelegate getNamingStrategyDelegate() {
 		return getNamingStrategyDelegate( mappings );
 	}
 
 	protected static NamingStrategyDelegate getNamingStrategyDelegate(Mappings mappings) {
 		return mappings.getNamingStrategyDelegator().getNamingStrategyDelegate( false );
 	}
 
 	/**
 	 * Find appropriate table of the column.
 	 * It can come from a secondary table or from the main table of the persistent class
 	 *
 	 * @return appropriate table
 	 * @throws AnnotationException missing secondary table
 	 */
 	public Table getTable() {
 		if ( table != null ){
 			return table;
 		}
 
 		if ( isSecondary() ) {
 			return getJoin().getTable();
 		}
 		else {
 			return propertyHolder.getTable();
 		}
 	}
 
 	public boolean isSecondary() {
 		if ( propertyHolder == null ) {
 			throw new AssertionFailure( "Should not call getTable() on column w/o persistent class defined" );
 		}
 
 		return StringHelper.isNotEmpty( explicitTableName )
 				&& !propertyHolder.getTable().getName().equals( explicitTableName );
 	}
 
 	public Join getJoin() {
 		Join join = joins.get( explicitTableName );
 		if ( join == null ) {
 			throw new AnnotationException(
 					"Cannot find the expected secondary table: no "
 							+ explicitTableName + " available for " + propertyHolder.getClassName()
 			);
 		}
 		else {
 			return join;
 		}
 	}
 
 	public void forceNotNull() {
 		mappingColumn.setNullable( false );
 	}
 
 	public static Ejb3Column[] buildColumnFromAnnotation(
 			javax.persistence.Column[] anns,
 			org.hibernate.annotations.Formula formulaAnn,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			Map<String, Join> secondaryTables,
 			Mappings mappings){
 		return buildColumnFromAnnotation(
 				anns, formulaAnn, nullability, propertyHolder, inferredData, null, secondaryTables, mappings
 		);
 	}
 	public static Ejb3Column[] buildColumnFromAnnotation(
 			javax.persistence.Column[] anns,
 			org.hibernate.annotations.Formula formulaAnn,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String suffixForDefaultColumnName,
 			Map<String, Join> secondaryTables,
 			Mappings mappings) {
 		Ejb3Column[] columns;
 		if ( formulaAnn != null ) {
 			Ejb3Column formulaColumn = new Ejb3Column();
 			formulaColumn.setFormula( formulaAnn.value() );
 			formulaColumn.setImplicit( false );
 			formulaColumn.setMappings( mappings );
 			formulaColumn.setPropertyHolder( propertyHolder );
 			formulaColumn.bind();
 			columns = new Ejb3Column[] { formulaColumn };
 		}
 		else {
 			javax.persistence.Column[] actualCols = anns;
 			javax.persistence.Column[] overriddenCols = propertyHolder.getOverriddenColumn(
 					StringHelper.qualify( propertyHolder.getPath(), inferredData.getPropertyName() )
 			);
 			if ( overriddenCols != null ) {
 				//check for overridden first
 				if ( anns != null && overriddenCols.length != anns.length ) {
 					throw new AnnotationException( "AttributeOverride.column() should override all columns for now" );
 				}
 				actualCols = overriddenCols.length == 0 ? null : overriddenCols;
 				LOG.debugf( "Column(s) overridden for property %s", inferredData.getPropertyName() );
 			}
 			if ( actualCols == null ) {
 				columns = buildImplicitColumn(
 						inferredData,
 						suffixForDefaultColumnName,
 						secondaryTables,
 						propertyHolder,
 						nullability,
 						mappings
 				);
 			}
 			else {
 				final int length = actualCols.length;
 				columns = new Ejb3Column[length];
 				for (int index = 0; index < length; index++) {
 					final ObjectNameNormalizer nameNormalizer = mappings.getObjectNameNormalizer();
 					javax.persistence.Column col = actualCols[index];
 					final String sqlType = col.columnDefinition().equals( "" )
 							? null
 							: nameNormalizer.normalizeIdentifierQuoting( col.columnDefinition() );
 					final String tableName =
 							! StringHelper.isEmpty(col.table())
 									? nameNormalizer.normalizeIdentifierQuoting( getNamingStrategyDelegate( mappings ).toPhysicalTableName(
 											col.table()
 									) )
 									: "";
 					final String columnName = nameNormalizer.normalizeIdentifierQuoting( col.name() );
 					Ejb3Column column = new Ejb3Column();
 
 					if ( length == 1 ) {
 						applyColumnDefault( column, inferredData );
 					}
 
 					column.setImplicit( false );
 					column.setSqlType( sqlType );
 					column.setLength( col.length() );
 					column.setPrecision( col.precision() );
 					column.setScale( col.scale() );
 					if ( StringHelper.isEmpty( columnName ) && ! StringHelper.isEmpty( suffixForDefaultColumnName ) ) {
 						column.setLogicalColumnName( inferredData.getPropertyName() + suffixForDefaultColumnName );
 					}
 					else {
 						column.setLogicalColumnName( columnName );
 					}
 
 					column.setPropertyName(
 							BinderHelper.getRelativePath( propertyHolder, inferredData.getPropertyName() )
 					);
 			 		column.setNullable(
 						col.nullable()
 					); //TODO force to not null if available? This is a (bad) user choice.
 					column.setUnique( col.unique() );
 					column.setInsertable( col.insertable() );
 					column.setUpdatable( col.updatable() );
 					column.setExplicitTableName( tableName );
 					column.setPropertyHolder( propertyHolder );
 					column.setJoins( secondaryTables );
 					column.setMappings( mappings );
 					column.extractDataFromPropertyData(inferredData);
 					column.bind();
 					columns[index] = column;
 				}
 			}
 		}
 		return columns;
 	}
 
 	private static void applyColumnDefault(Ejb3Column column, PropertyData inferredData) {
 		final XProperty xProperty = inferredData.getProperty();
 		if ( xProperty != null ) {
 			ColumnDefault columnDefaultAnn = xProperty.getAnnotation( ColumnDefault.class );
 			if ( columnDefaultAnn != null ) {
 				column.setDefaultValue( columnDefaultAnn.value() );
 			}
 		}
 		else {
 			LOG.trace(
 					"Could not perform @ColumnDefault lookup as 'PropertyData' did not give access to XProperty"
 			);
 		}
 	}
 
 	//must only be called after all setters are defined and before bind
 	private void extractDataFromPropertyData(PropertyData inferredData) {
 		if ( inferredData != null ) {
 			XProperty property = inferredData.getProperty();
 			if ( property != null ) {
 				processExpression( property.getAnnotation( ColumnTransformer.class ) );
 				ColumnTransformers annotations = property.getAnnotation( ColumnTransformers.class );
 				if (annotations != null) {
 					for ( ColumnTransformer annotation : annotations.value() ) {
 						processExpression( annotation );
 					}
 				}
 			}
 		}
 	}
 
 	private void processExpression(ColumnTransformer annotation) {
 		String nonNullLogicalColumnName = logicalColumnName != null ? logicalColumnName : ""; //use the default for annotations
 		if ( annotation != null &&
 				( StringHelper.isEmpty( annotation.forColumn() )
 						|| annotation.forColumn().equals( nonNullLogicalColumnName ) ) ) {
 			readExpression = annotation.read();
 			if ( StringHelper.isEmpty( readExpression ) ) {
 				readExpression = null;
 			}
 			writeExpression = annotation.write();
 			if ( StringHelper.isEmpty( writeExpression ) ) {
 				writeExpression = null;
 			}
 		}
 	}
 
 	private static Ejb3Column[] buildImplicitColumn(
 			PropertyData inferredData,
 			String suffixForDefaultColumnName,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			Nullability nullability,
 			Mappings mappings) {
 		Ejb3Column column = new Ejb3Column();
 		Ejb3Column[] columns = new Ejb3Column[1];
 		columns[0] = column;
 
 		//not following the spec but more clean
 		if ( nullability != Nullability.FORCED_NULL
 				&& inferredData.getClassOrElement().isPrimitive()
 				&& !inferredData.getProperty().isArray() ) {
 			column.setNullable( false );
 		}
 		column.setLength( DEFAULT_COLUMN_LENGTH );
 		final String propertyName = inferredData.getPropertyName();
 		column.setPropertyName(
 				BinderHelper.getRelativePath( propertyHolder, propertyName )
 		);
 		column.setPropertyHolder( propertyHolder );
 		column.setJoins( secondaryTables );
 		column.setMappings( mappings );
 
 		// property name + suffix is an "explicit" column name
 		if ( !StringHelper.isEmpty( suffixForDefaultColumnName ) ) {
 			column.setLogicalColumnName( propertyName + suffixForDefaultColumnName );
 			column.setImplicit( false );
 		}
 		else {
 			column.setImplicit( true );
 		}
 		applyColumnDefault( column, inferredData );
 		column.extractDataFromPropertyData( inferredData );
 		column.bind();
 		return columns;
 	}
 
 	public static void checkPropertyConsistency(Ejb3Column[] columns, String propertyName) {
 		int nbrOfColumns = columns.length;
 
 		if ( nbrOfColumns > 1 ) {
 			for (int currentIndex = 1; currentIndex < nbrOfColumns; currentIndex++) {
 
 				if (columns[currentIndex].isFormula() || columns[currentIndex - 1].isFormula()) {
 					continue;
 				}
 
 				if ( columns[currentIndex].isInsertable() != columns[currentIndex - 1].isInsertable() ) {
 					throw new AnnotationException(
 							"Mixing insertable and non insertable columns in a property is not allowed: " + propertyName
 					);
 				}
 				if ( columns[currentIndex].isNullable() != columns[currentIndex - 1].isNullable() ) {
 					throw new AnnotationException(
 							"Mixing nullable and non nullable columns in a property is not allowed: " + propertyName
 					);
 				}
 				if ( columns[currentIndex].isUpdatable() != columns[currentIndex - 1].isUpdatable() ) {
 					throw new AnnotationException(
 							"Mixing updatable and non updatable columns in a property is not allowed: " + propertyName
 					);
 				}
 				if ( !columns[currentIndex].getTable().equals( columns[currentIndex - 1].getTable() ) ) {
 					throw new AnnotationException(
 							"Mixing different tables in a property is not allowed: " + propertyName
 					);
 				}
 			}
 		}
 
 	}
 
 	public void addIndex(Index index, boolean inSecondPass) {
 		if ( index == null ) return;
 		String indexName = index.name();
 		addIndex( indexName, inSecondPass );
 	}
 
 	void addIndex(String indexName, boolean inSecondPass) {
 		IndexOrUniqueKeySecondPass secondPass = new IndexOrUniqueKeySecondPass( indexName, this, mappings, false );
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( mappings.getClasses() );
 		}
 		else {
 			mappings.addSecondPass(
 					secondPass
 			);
 		}
 	}
 
 	void addUniqueKey(String uniqueKeyName, boolean inSecondPass) {
 		IndexOrUniqueKeySecondPass secondPass = new IndexOrUniqueKeySecondPass( uniqueKeyName, this, mappings, true );
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( mappings.getClasses() );
 		}
 		else {
 			mappings.addSecondPass(
 					secondPass
 			);
 		}
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "Ejb3Column" );
 		sb.append( "{table=" ).append( getTable() );
 		sb.append( ", mappingColumn=" ).append( mappingColumn.getName() );
 		sb.append( ", insertable=" ).append( insertable );
 		sb.append( ", updatable=" ).append( updatable );
 		sb.append( ", unique=" ).append( unique );
 		sb.append( '}' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
index e595a1c45d..19587ba0f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
@@ -1,732 +1,732 @@
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
 
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.JoinColumn;
 import javax.persistence.PrimaryKeyJoinColumn;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.JoinColumnOrFormula;
 import org.hibernate.annotations.JoinColumnsOrFormulas;
 import org.hibernate.annotations.JoinFormula;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
 
 /**
  * Wrap state of an EJB3 @JoinColumn annotation
  * and build the Hibernate column mapping element
  *
  * @author Emmanuel Bernard
  */
 @SuppressWarnings("unchecked")
 public class Ejb3JoinColumn extends Ejb3Column {
 	/**
 	 * property name repated to this column
 	 */
 	private String referencedColumn;
 	private String mappedBy;
 	//property name on the mapped by side if any
 	private String mappedByPropertyName;
 	//table name on the mapped by side if any
 	private String mappedByTableName;
 	private String mappedByEntityName;
 	private String mappedByJpaEntityName;
 	private boolean JPA2ElementCollection;
 
 	public void setJPA2ElementCollection(boolean JPA2ElementCollection) {
 		this.JPA2ElementCollection = JPA2ElementCollection;
 	}
 
 	// TODO hacky solution to get the information at property ref resolution
 	public String getManyToManyOwnerSideEntityName() {
 		return manyToManyOwnerSideEntityName;
 	}
 
 	public void setManyToManyOwnerSideEntityName(String manyToManyOwnerSideEntityName) {
 		this.manyToManyOwnerSideEntityName = manyToManyOwnerSideEntityName;
 	}
 
 	private String manyToManyOwnerSideEntityName;
 
 	public void setReferencedColumn(String referencedColumn) {
 		this.referencedColumn = referencedColumn;
 	}
 
 	public String getMappedBy() {
 		return mappedBy;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	//Due to @AnnotationOverride overriding rules, I don't want the constructor to be public
 	private Ejb3JoinColumn() {
 		setMappedBy( BinderHelper.ANNOTATION_STRING_DEFAULT );
 	}
 
 	//Due to @AnnotationOverride overriding rules, I don't want the constructor to be public
 	//TODO get rid of it and use setters
 	private Ejb3JoinColumn(
 			String sqlType,
 			String name,
 			boolean nullable,
 			boolean unique,
 			boolean insertable,
 			boolean updatable,
 			String referencedColumn,
 			String secondaryTable,
 			Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			String mappedBy,
 			boolean isImplicit,
 			Mappings mappings) {
 		super();
 		setImplicit( isImplicit );
 		setSqlType( sqlType );
 		setLogicalColumnName( name );
 		setNullable( nullable );
 		setUnique( unique );
 		setInsertable( insertable );
 		setUpdatable( updatable );
 		setExplicitTableName( secondaryTable );
 		setPropertyHolder( propertyHolder );
 		setJoins( joins );
 		setMappings( mappings );
 		setPropertyName( BinderHelper.getRelativePath( propertyHolder, propertyName ) );
 		bind();
 		this.referencedColumn = referencedColumn;
 		this.mappedBy = mappedBy;
 	}
 
 	public String getReferencedColumn() {
 		return referencedColumn;
 	}
 
 	public static Ejb3JoinColumn[] buildJoinColumnsOrFormulas(
 			JoinColumnsOrFormulas anns,
 			String mappedBy,
 			Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			Mappings mappings) {
 		JoinColumnOrFormula [] ann = anns.value();
 		Ejb3JoinColumn [] joinColumns = new Ejb3JoinColumn[ann.length];
 		for (int i = 0; i < ann.length; i++) {
 			JoinColumnOrFormula join = ann[i];
 			JoinFormula formula = join.formula();
 			if (formula.value() != null && !formula.value().equals("")) {
 				joinColumns[i] = buildJoinFormula(
 						formula, mappedBy, joins, propertyHolder, propertyName, mappings
 				);
 			}
 			else {
 				joinColumns[i] = buildJoinColumns(
 						new JoinColumn[] { join.column() }, mappedBy, joins, propertyHolder, propertyName, mappings
 				)[0];
 			}
 		}
 
 		return joinColumns;
 	}
 
 	/**
 	 * build join formula
 	 */
 	public static Ejb3JoinColumn buildJoinFormula(
 			JoinFormula ann,
 			String mappedBy,
 			Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			Mappings mappings) {
 		Ejb3JoinColumn formulaColumn = new Ejb3JoinColumn();
 		formulaColumn.setFormula( ann.value() );
 		formulaColumn.setReferencedColumn(ann.referencedColumnName());
 		formulaColumn.setMappings( mappings );
 		formulaColumn.setPropertyHolder( propertyHolder );
 		formulaColumn.setJoins( joins );
 		formulaColumn.setPropertyName( BinderHelper.getRelativePath( propertyHolder, propertyName ) );
 		formulaColumn.bind();
 		return formulaColumn;
 	}
 
 	public static Ejb3JoinColumn[] buildJoinColumns(
 			JoinColumn[] anns,
 			String mappedBy,
 			Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			Mappings mappings) {
 		return buildJoinColumnsWithDefaultColumnSuffix(
 				anns, mappedBy, joins, propertyHolder, propertyName, "", mappings
 		);
 	}
 
 	public static Ejb3JoinColumn[] buildJoinColumnsWithDefaultColumnSuffix(
 			JoinColumn[] anns,
 			String mappedBy,
 			Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			String suffixForDefaultColumnName,
 			Mappings mappings) {
 		JoinColumn[] actualColumns = propertyHolder.getOverriddenJoinColumn(
 				StringHelper.qualify( propertyHolder.getPath(), propertyName )
 		);
 		if ( actualColumns == null ) actualColumns = anns;
 		if ( actualColumns == null || actualColumns.length == 0 ) {
 			return new Ejb3JoinColumn[] {
 					buildJoinColumn(
 							null,
 							mappedBy,
 							joins,
 							propertyHolder,
 							propertyName,
 							suffixForDefaultColumnName,
 							mappings )
 			};
 		}
 		else {
 			int size = actualColumns.length;
 			Ejb3JoinColumn[] result = new Ejb3JoinColumn[size];
 			for (int index = 0; index < size; index++) {
 				result[index] = buildJoinColumn(
 						actualColumns[index],
 						mappedBy,
 						joins,
 						propertyHolder,
 						propertyName,
 						suffixForDefaultColumnName,
 						mappings
 				);
 			}
 			return result;
 		}
 	}
 
 	/**
 	 * build join column for SecondaryTables
 	 */
 	private static Ejb3JoinColumn buildJoinColumn(
 			JoinColumn ann,
 			String mappedBy, Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			String suffixForDefaultColumnName,
 			Mappings mappings) {
 		if ( ann != null ) {
 			if ( BinderHelper.isEmptyAnnotationValue( mappedBy ) ) {
 				throw new AnnotationException(
 						"Illegal attempt to define a @JoinColumn with a mappedBy association: "
 								+ BinderHelper.getRelativePath( propertyHolder, propertyName )
 				);
 			}
 			Ejb3JoinColumn joinColumn = new Ejb3JoinColumn();
 			joinColumn.setMappings( mappings );
 			joinColumn.setJoinAnnotation( ann, null );
 			if ( StringHelper.isEmpty( joinColumn.getLogicalColumnName() )
 				&& ! StringHelper.isEmpty( suffixForDefaultColumnName ) ) {
 				joinColumn.setLogicalColumnName( propertyName + suffixForDefaultColumnName );
 			}
 			joinColumn.setJoins( joins );
 			joinColumn.setPropertyHolder( propertyHolder );
 			joinColumn.setPropertyName( BinderHelper.getRelativePath( propertyHolder, propertyName ) );
 			joinColumn.setImplicit( false );
 			joinColumn.bind();
 			return joinColumn;
 		}
 		else {
 			Ejb3JoinColumn joinColumn = new Ejb3JoinColumn();
 			joinColumn.setMappedBy( mappedBy );
 			joinColumn.setJoins( joins );
 			joinColumn.setPropertyHolder( propertyHolder );
 			joinColumn.setPropertyName(
 					BinderHelper.getRelativePath( propertyHolder, propertyName )
 			);
 			// property name + suffix is an "explicit" column name
 			if ( !StringHelper.isEmpty( suffixForDefaultColumnName ) ) {
 				joinColumn.setLogicalColumnName( propertyName + suffixForDefaultColumnName );
 				joinColumn.setImplicit( false );
 			}
 			else {
 				joinColumn.setImplicit( true );
 			}
 			joinColumn.setMappings( mappings );
 			joinColumn.bind();
 			return joinColumn;
 		}
 	}
 
 
 	// TODO default name still useful in association table
 	public void setJoinAnnotation(JoinColumn annJoin, String defaultName) {
 		if ( annJoin == null ) {
 			setImplicit( true );
 		}
 		else {
 			setImplicit( false );
 			final ObjectNameNormalizer nameNormalizer = getMappings().getObjectNameNormalizer();
 			if ( !BinderHelper.isEmptyAnnotationValue( annJoin.columnDefinition() ) ) setSqlType( annJoin.columnDefinition() );
 			if ( !BinderHelper.isEmptyAnnotationValue( annJoin.name() ) ) setLogicalColumnName( annJoin.name() );
 			setNullable( annJoin.nullable() );
 			setUnique( annJoin.unique() );
 			setInsertable( annJoin.insertable() );
 			setUpdatable( annJoin.updatable() );
 			setReferencedColumn( annJoin.referencedColumnName() );
 
 			final String tableName = !BinderHelper.isEmptyAnnotationValue( annJoin.table() )
 					? nameNormalizer.normalizeIdentifierQuoting( getNamingStrategyDelegate().toPhysicalTableName(
 							annJoin.table()
 					) )
 					: "";
 			setExplicitTableName( tableName );
 		}
 	}
 
 	/**
 	 * Build JoinColumn for a JOINED hierarchy
 	 */
 	public static Ejb3JoinColumn buildJoinColumn(
 			PrimaryKeyJoinColumn pkJoinAnn,
 			JoinColumn joinAnn,
 			Value identifier,
 			Map<String, Join> joins,
 			PropertyHolder propertyHolder,
 			Mappings mappings) {
 		Column col = (Column) identifier.getColumnIterator().next();
 		String defaultName = mappings.getLogicalColumnName( col.getQuotedName(), identifier.getTable() );
 		if ( pkJoinAnn != null || joinAnn != null ) {
 			String colName;
 			String columnDefinition;
 			String referencedColumnName;
 			if ( pkJoinAnn != null ) {
 				colName = pkJoinAnn.name();
 				columnDefinition = pkJoinAnn.columnDefinition();
 				referencedColumnName = pkJoinAnn.referencedColumnName();
 			}
 			else {
 				colName = joinAnn.name();
 				columnDefinition = joinAnn.columnDefinition();
 				referencedColumnName = joinAnn.referencedColumnName();
 			}
 
 			String sqlType = "".equals( columnDefinition )
 					? null
 					: mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( columnDefinition );
 			String name = "".equals( colName )
 					? defaultName
 					: colName;
 			name = mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			return new Ejb3JoinColumn(
 					sqlType,
 					name, false, false,
 					true, true,
 					referencedColumnName,
 					null, joins,
 					propertyHolder, null, null, false, mappings
 			);
 		}
 		else {
 			defaultName = mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( defaultName );
 			return new Ejb3JoinColumn(
 					null, defaultName,
 					false, false, true, true, null, null,
 					joins, propertyHolder, null, null, true, mappings
 			);
 		}
 	}
 
 	/**
 	 * Override persistent class on oneToMany Cases for late settings
 	 * Must only be used on second level pass binding
 	 */
 	public void setPersistentClass(
 			PersistentClass persistentClass,
 			Map<String, Join> joins,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		// TODO shouldn't we deduce the classname from the persistentclasS?
 		this.propertyHolder = PropertyHolderBuilder.buildPropertyHolder( persistentClass, joins, getMappings(), inheritanceStatePerClass );
 	}
 
 	public static void checkIfJoinColumn(Object columns, PropertyHolder holder, PropertyData property) {
 		if ( !( columns instanceof Ejb3JoinColumn[] ) ) {
 			throw new AnnotationException(
 					"@Column cannot be used on an association property: "
 							+ holder.getEntityName()
 							+ "."
 							+ property.getPropertyName()
 			);
 		}
 	}
 
 
 
 	public void copyReferencedStructureAndCreateDefaultJoinColumns(
 			PersistentClass referencedEntity,
 			Iterator columnIterator,
 			SimpleValue value) {
 		if ( !isNameDeferred() ) {
 			throw new AssertionFailure( "Building implicit column but the column is not implicit" );
 		}
 		while ( columnIterator.hasNext() ) {
 			Column synthCol = (Column) columnIterator.next();
 			this.linkValueUsingDefaultColumnNaming( synthCol, referencedEntity, value );
 		}
 		//reset for the future
 		setMappingColumn( null );
 	}
 
 	public void linkValueUsingDefaultColumnNaming(
 			Column referencedColumn,
 			PersistentClass referencedEntity,
 			SimpleValue value) {
 		String columnName;
 		String logicalReferencedColumn = getMappings().getLogicalColumnName(
 				referencedColumn.getQuotedName(), referencedEntity.getTable()
 		);
 		columnName = buildDefaultColumnName( referencedEntity, logicalReferencedColumn );
 		//yuk side effect on an implicit column
 		setLogicalColumnName( columnName );
 		setReferencedColumn( logicalReferencedColumn );
 		initMappingColumn(
 				columnName,
 				null, referencedColumn.getLength(),
 				referencedColumn.getPrecision(),
 				referencedColumn.getScale(),
 				getMappingColumn() != null ? getMappingColumn().isNullable() : false,
 				referencedColumn.getSqlType(),
 				getMappingColumn() != null ? getMappingColumn().isUnique() : false,
 			    false
 		);
 		linkWithValue( value );
 	}
 
 	public void addDefaultJoinColumnName(PersistentClass referencedEntity, String logicalReferencedColumn) {
 		final String columnName = buildDefaultColumnName( referencedEntity, logicalReferencedColumn );
 		getMappingColumn().setName( columnName );
 		setLogicalColumnName( columnName );
 	}
 
 	private String buildDefaultColumnName(PersistentClass referencedEntity, String logicalReferencedColumn) {
 		String columnName;
 		boolean mappedBySide = mappedByTableName != null || mappedByPropertyName != null;
 		boolean ownerSide = getPropertyName() != null;
 
 		Boolean isRefColumnQuoted = StringHelper.isQuoted( logicalReferencedColumn );
 		String unquotedLogicalReferenceColumn = isRefColumnQuoted ?
 				StringHelper.unquote( logicalReferencedColumn ) :
 				logicalReferencedColumn;
 
 		if ( mappedBySide ) {
 			String unquotedMappedbyTable = StringHelper.unquote( mappedByTableName );
 			if ( JPA2ElementCollection ) {
-				columnName = getNamingStrategyDelegate().determineElementCollectionForeignKeyColumnName(
-						mappedByPropertyName,
+				columnName = getNamingStrategyDelegate().determineImplicitElementCollectionJoinColumnName(
 						mappedByEntityName,
 						mappedByJpaEntityName,
 						unquotedMappedbyTable,
-						unquotedLogicalReferenceColumn
+						unquotedLogicalReferenceColumn,
+						mappedByPropertyName
 				);
 			}
 			else {
-				columnName = getNamingStrategyDelegate().determineEntityAssociationForeignKeyColumnName(
-						mappedByPropertyName,
+				columnName = getNamingStrategyDelegate().determineImplicitEntityAssociationJoinColumnName(
 						mappedByEntityName,
 						mappedByJpaEntityName,
 						unquotedMappedbyTable,
-						unquotedLogicalReferenceColumn
+						unquotedLogicalReferenceColumn,
+						mappedByPropertyName
 				);
 			}
 			//one element was quoted so we quote
 			if ( isRefColumnQuoted || StringHelper.isQuoted( mappedByTableName ) ) {
 				columnName = StringHelper.quote( columnName );
 			}
 		}
 		else if ( ownerSide ) {
 			String logicalTableName = getMappings().getLogicalTableName( referencedEntity.getTable() );
 			String unquotedLogicalTableName = StringHelper.unquote( logicalTableName );
-			columnName = getNamingStrategyDelegate().determineEntityAssociationForeignKeyColumnName(
-					getPropertyName(),
+			columnName = getNamingStrategyDelegate().determineImplicitEntityAssociationJoinColumnName(
 					referencedEntity.getEntityName(),
 					referencedEntity.getJpaEntityName(),
 					unquotedLogicalTableName,
-					unquotedLogicalReferenceColumn
+					unquotedLogicalReferenceColumn,
+					getPropertyName()
 			);
 			//one element was quoted so we quote
 			if ( isRefColumnQuoted || StringHelper.isQuoted( logicalTableName ) ) {
 				columnName = StringHelper.quote( columnName );
 			}
 		}
 		else {
 			//is an intra-entity hierarchy table join so copy the name by default
 			String logicalTableName = getMappings().getLogicalTableName( referencedEntity.getTable() );
 			String unquotedLogicalTableName = StringHelper.unquote( logicalTableName );
-			columnName = getNamingStrategyDelegate().determineJoinKeyColumnName(
+			columnName = getNamingStrategyDelegate().toPhysicalJoinKeyColumnName(
 					unquotedLogicalReferenceColumn,
 					unquotedLogicalTableName
 			);
 			//one element was quoted so we quote
 			if ( isRefColumnQuoted || StringHelper.isQuoted( logicalTableName ) ) {
 				columnName = StringHelper.quote( columnName );
 			}
 		}
 		return columnName;
 	}
 
 	/**
 	 * used for mappedBy cases
 	 */
 	public void linkValueUsingAColumnCopy(Column column, SimpleValue value) {
 		initMappingColumn(
 				//column.getName(),
 				column.getQuotedName(),
 				null, column.getLength(),
 				column.getPrecision(),
 				column.getScale(),
 				getMappingColumn().isNullable(),
 				column.getSqlType(),
 				getMappingColumn().isUnique(),
 				false //We do copy no strategy here
 		);
 		linkWithValue( value );
 	}
 
 	@Override
     protected void addColumnBinding(SimpleValue value) {
 		if ( StringHelper.isEmpty( mappedBy ) ) {
 			// was the column explicitly quoted in the mapping/annotation
 			// TODO: in metamodel, we need to better split global quoting and explicit quoting w/ respect to logical names
 			boolean isLogicalColumnQuoted = StringHelper.isQuoted( getLogicalColumnName() );
 			
 			final ObjectNameNormalizer nameNormalizer = getMappings().getObjectNameNormalizer();
 			final String logicalColumnName = nameNormalizer.normalizeIdentifierQuoting( getLogicalColumnName() );
 			final String referencedColumn = nameNormalizer.normalizeIdentifierQuoting( getReferencedColumn() );
 			final String unquotedLogColName = StringHelper.unquote( logicalColumnName );
 			final String unquotedRefColumn = StringHelper.unquote( referencedColumn );
-			String logicalCollectionColumnName = getNamingStrategyDelegate().logicalCollectionColumnName(
+			String logicalCollectionColumnName = getNamingStrategyDelegate().determineLogicalCollectionColumnName(
 					unquotedLogColName,
 					getPropertyName(),
 					unquotedRefColumn
 			);
 			
 			if ( isLogicalColumnQuoted ) {
 				logicalCollectionColumnName = StringHelper.quote( logicalCollectionColumnName );
 			}
 			getMappings().addColumnBinding( logicalCollectionColumnName, getMappingColumn(), value.getTable() );
 		}
 	}
 
 	//keep it JDK 1.4 compliant
 	//implicit way
 	public static final int NO_REFERENCE = 0;
 	//reference to the pk in an explicit order
 	public static final int PK_REFERENCE = 1;
 	//reference to non pk columns
 	public static final int NON_PK_REFERENCE = 2;
 
 	public static int checkReferencedColumnsType(
 			Ejb3JoinColumn[] columns,
 			PersistentClass referencedEntity,
 			Mappings mappings) {
 		//convenient container to find whether a column is an id one or not
 		Set<Column> idColumns = new HashSet<Column>();
 		Iterator idColumnsIt = referencedEntity.getKey().getColumnIterator();
 		while ( idColumnsIt.hasNext() ) {
 			idColumns.add( (Column) idColumnsIt.next() );
 		}
 
 		boolean isFkReferencedColumnName = false;
 		boolean noReferencedColumn = true;
 		//build the list of potential tables
 		if ( columns.length == 0 ) return NO_REFERENCE; //shortcut
 		Object columnOwner = BinderHelper.findColumnOwner(
 				referencedEntity, columns[0].getReferencedColumn(), mappings
 		);
 		if ( columnOwner == null ) {
 			try {
 				throw new MappingException(
 						"Unable to find column with logical name: "
 								+ columns[0].getReferencedColumn() + " in " + referencedEntity.getTable() + " and its related "
 								+ "supertables and secondary tables"
 				);
 			}
 			catch (MappingException e) {
 				throw new RecoverableException( e.getMessage(), e );
 			}
 		}
 		Table matchingTable = columnOwner instanceof PersistentClass ?
 				( (PersistentClass) columnOwner ).getTable() :
 				( (Join) columnOwner ).getTable();
 		//check each referenced column
 		for (Ejb3JoinColumn ejb3Column : columns) {
 			String logicalReferencedColumnName = ejb3Column.getReferencedColumn();
 			if ( StringHelper.isNotEmpty( logicalReferencedColumnName ) ) {
 				String referencedColumnName;
 				try {
 					referencedColumnName = mappings.getPhysicalColumnName( logicalReferencedColumnName, matchingTable );
 				}
 				catch (MappingException me) {
 					//rewrite the exception
 					throw new MappingException(
 							"Unable to find column with logical name: "
 									+ logicalReferencedColumnName + " in " + matchingTable.getName()
 					);
 				}
 				noReferencedColumn = false;
 				Column refCol = new Column( referencedColumnName );
 				boolean contains = idColumns.contains( refCol );
 				if ( !contains ) {
 					isFkReferencedColumnName = true;
 					break; //we know the state
 				}
 			}
 		}
 		if ( isFkReferencedColumnName ) {
 			return NON_PK_REFERENCE;
 		}
 		else if ( noReferencedColumn ) {
 			return NO_REFERENCE;
 		}
 		else if ( idColumns.size() != columns.length ) {
 			//reference use PK but is a subset or a superset
 			return NON_PK_REFERENCE;
 		}
 		else {
 			return PK_REFERENCE;
 		}
 	}
 
 	/**
 	 * Called to apply column definitions from the referenced FK column to this column.
 	 *
 	 * @param column the referenced column.
 	 */
 	public void overrideFromReferencedColumnIfNecessary(org.hibernate.mapping.Column column) {
 		if (getMappingColumn() != null) {
 			// columnDefinition can also be specified using @JoinColumn, hence we have to check
 			// whether it is set or not
 			if ( StringHelper.isEmpty( sqlType ) ) {
 				sqlType = column.getSqlType();
 				getMappingColumn().setSqlType( sqlType );
 			}
 
 			// these properties can only be applied on the referenced column - we can just take them over
 			getMappingColumn().setLength(column.getLength());
 			getMappingColumn().setPrecision(column.getPrecision());
 			getMappingColumn().setScale(column.getScale());
 		}
 	}
 
 	@Override
 	public void redefineColumnName(String columnName, String propertyName, boolean applyNamingStrategy) {
 		if ( StringHelper.isNotEmpty( columnName ) ) {
 			getMappingColumn().setName(
 					applyNamingStrategy ?
 							getNamingStrategyDelegate().toPhysicalColumnName( columnName ) :
 							columnName
 			);
 		}
 	}
 
 	public static Ejb3JoinColumn[] buildJoinTableJoinColumns(
 			JoinColumn[] annJoins,
 			Map<String, Join> secondaryTables,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			String mappedBy,
 			Mappings mappings) {
 		Ejb3JoinColumn[] joinColumns;
 		if ( annJoins == null ) {
 			Ejb3JoinColumn currentJoinColumn = new Ejb3JoinColumn();
 			currentJoinColumn.setImplicit( true );
 			currentJoinColumn.setNullable( false ); //I break the spec, but it's for good
 			currentJoinColumn.setPropertyHolder( propertyHolder );
 			currentJoinColumn.setJoins( secondaryTables );
 			currentJoinColumn.setMappings( mappings );
 			currentJoinColumn.setPropertyName(
 					BinderHelper.getRelativePath( propertyHolder, propertyName )
 			);
 			currentJoinColumn.setMappedBy( mappedBy );
 			currentJoinColumn.bind();
 
 			joinColumns = new Ejb3JoinColumn[] {
 					currentJoinColumn
 
 			};
 		}
 		else {
 			joinColumns = new Ejb3JoinColumn[annJoins.length];
 			JoinColumn annJoin;
 			int length = annJoins.length;
 			for (int index = 0; index < length; index++) {
 				annJoin = annJoins[index];
 				Ejb3JoinColumn currentJoinColumn = new Ejb3JoinColumn();
 				currentJoinColumn.setImplicit( true );
 				currentJoinColumn.setPropertyHolder( propertyHolder );
 				currentJoinColumn.setJoins( secondaryTables );
 				currentJoinColumn.setMappings( mappings );
 				currentJoinColumn.setPropertyName( BinderHelper.getRelativePath( propertyHolder, propertyName ) );
 				currentJoinColumn.setMappedBy( mappedBy );
 				currentJoinColumn.setJoinAnnotation( annJoin, propertyName );
 				currentJoinColumn.setNullable( false ); //I break the spec, but it's for good
 				//done after the annotation to override it
 				currentJoinColumn.bind();
 				joinColumns[index] = currentJoinColumn;
 			}
 		}
 		return joinColumns;
 	}
 
 	public void setMappedBy(String entityName, String jpaEntityName, String logicalTableName, String mappedByProperty) {
 		this.mappedByEntityName = entityName;
 		this.mappedByJpaEntityName = jpaEntityName;
 		this.mappedByTableName = logicalTableName;
 		this.mappedByPropertyName = mappedByProperty;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "Ejb3JoinColumn" );
 		sb.append( "{logicalColumnName='" ).append( getLogicalColumnName() ).append( '\'' );
 		sb.append( ", referencedColumn='" ).append( referencedColumn ).append( '\'' );
 		sb.append( ", mappedBy='" ).append( mappedBy ).append( '\'' );
 		sb.append( '}' );
 		return sb.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index d5b2120bee..089aef0d8d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1,2511 +1,2511 @@
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
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.cfg.naming.NamingStrategyDelegate;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Array;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.Constraint;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.FetchProfile;
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
 import org.hibernate.tuple.GeneratedValueGeneration;
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.Element;
 
 /**
  * Walks an XML mapping document and produces the Hibernate configuration-time metamodel (the
  * classes in the <tt>mapping</tt> package)
  *
  * @author Gavin King
  */
 public final class HbmBinder {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HbmBinder.class.getName());
 
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
 		LOG.debugf( "Import: %s -> %s", rename, className );
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
 	 * Responsible for performing the bind operation related to an &lt;class/&gt; mapping element.
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
 		        entity.isAbstract() != null && entity.isAbstract()
 			);
 		entity.setTable( table );
 		bindComment(table, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class: %s -> %s", entity.getEntityName(), entity.getTable().getName() );
 		}
 
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
 			entity.setDeclaredIdentifierProperty( prop );
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
 			entity.setDeclaredIdentifierProperty( prop );
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
 		if ( prop.getValueGenerationStrategy() != null ) {
 			if ( prop.getValueGenerationStrategy().getGenerationTiming() == GenerationTiming.INSERT ) {
 				throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 			}
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
 		final String explicitForceValue = subnode.attributeValue( "force" );
 		boolean forceDiscriminatorInSelects = explicitForceValue == null
 				? mappings.forceDiscriminatorInSelectsByDefault()
 				: "true".equals( explicitForceValue );
 		entity.setForceDiscriminator( forceDiscriminatorInSelects );
 		if ( "false".equals( subnode.attributeValue( "insert" ) ) ) {
 			entity.setDiscriminatorInsertable( false );
 		}
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
 		persistentClass.setJpaEntityName( StringHelper.unqualify( entityName ) );
 
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
 
 //		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
 //		if ( tuplizer != null ) {
 //			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
 //		}
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
 		entity.setOptimisticLockStyle( getOptimisticLockStyle( olNode ) );
 
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
 		return ExecuteUpdateResultCheckStyle.fromExternalName( attr.getValue() );
 	}
 
 	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, unionSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
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
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping union-subclass: %s -> %s", unionSubclass.getEntityName(), unionSubclass.getTable().getName() );
 		}
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping subclass: %s -> %s", subclass.getEntityName(), subclass.getTable().getName() );
 		}
 
 		// properties
 		createClassProperties( node, subclass, mappings, inheritedMetas );
 	}
 
 	private static String getClassTableName(
 			PersistentClass model,
 			Element node,
 			String schema,
 			String catalog,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		Attribute tableNameNode = node.attribute( "table" );
 		String logicalTableName;
 		String physicalTableName;
 		if ( tableNameNode == null ) {
 			logicalTableName = StringHelper.unqualify( model.getEntityName() );
-			physicalTableName = getNamingStrategyDelegate( mappings ).determinePrimaryTableLogicalName(
+			physicalTableName = getNamingStrategyDelegate( mappings ).determineImplicitPrimaryTableName(
 					model.getEntityName(),
 					model.getJpaEntityName()
 			);
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
 			physicalTableName = getNamingStrategyDelegate( mappings ).toPhysicalTableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	private static NamingStrategyDelegate getNamingStrategyDelegate(Mappings mappings) {
 		return  mappings.getNamingStrategyDelegator().getNamingStrategyDelegate( true );
 	}
 
 	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, joinedSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from
 																	// <joined-subclass>
 
 		// joined subclasses
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
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping joined-subclass: %s -> %s", joinedSubclass.getEntityName(), joinedSubclass.getTable().getName() );
 		}
 
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
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class join: %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		}
 
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
 					String columnName = columnElement.attributeValue( "name" );
-					String logicalColumnName = getNamingStrategyDelegate( mappings ).logicalColumnName(
+					String logicalColumnName = getNamingStrategyDelegate( mappings ).determineLogicalColumnName(
 							columnName, propertyPath
 					);
 					columnName = getNamingStrategyDelegate( mappings ).toPhysicalColumnName( columnName );
 					columnName = quoteIdentifier( columnName, mappings );
 					column.setName( columnName );
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
 			String columnName = columnAttribute.getValue();
-			String logicalColumnName = getNamingStrategyDelegate( mappings ).logicalColumnName(
+			String logicalColumnName = getNamingStrategyDelegate( mappings ).determineLogicalColumnName(
 					columnName, propertyPath
 			);
 			columnName = getNamingStrategyDelegate( mappings ).toPhysicalColumnName( columnName );
 			columnName = quoteIdentifier( columnName, mappings );
 			column.setName( columnName );
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
-			String columnName = getNamingStrategyDelegate( mappings ).determineAttributeColumnName( propertyPath );
+			String columnName = getNamingStrategyDelegate( mappings ).determineImplicitPropertyColumnName( propertyPath );
 			columnName = quoteIdentifier( columnName, mappings );
 			column.setName( columnName );
-			String logicalName = getNamingStrategyDelegate( mappings ).logicalColumnName( null, propertyPath );
+			String logicalName = getNamingStrategyDelegate( mappings ).determineLogicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
 			 * Right now HbmMetadataSourceProcessorImpl does not support the
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
         if ( typeNode == null ) {
             typeNode = node.attribute( "id-type" ); // for an any
         }
         else {
             typeName = typeNode.getValue();
         }
 
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
 
 		resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);
 	}
 
 	private static void resolveAndBindTypeDef(SimpleValue simpleValue,
 			Mappings mappings, String typeName, Properties parameters) {
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
 		}else if (typeName!=null && !mappings.isInSecondPass()){
 			BasicType basicType=mappings.getTypeResolver().basic(typeName);
 			if (basicType==null) {
 				/*
 				 * If the referenced typeName isn't a basic-type, it's probably a typedef defined 
 				 * in a mapping file not read yet.
 				 * It should be solved by deferring the resolution and binding of this type until 
 				 * all mapping files are read - the second passes.
 				 * Fixes issue HHH-7300
 				 */
 				SecondPass resolveUserTypeMappingSecondPass=new ResolveUserTypeMappingSecondPass(simpleValue,typeName,mappings,parameters);
 				mappings.addSecondPass(resolveUserTypeMappingSecondPass);
 			}
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
 
 		// Handle generated properties.
 		GenerationTiming generationTiming = GenerationTiming.parseFromName( generationName );
 		if ( generationTiming == GenerationTiming.ALWAYS || generationTiming == GenerationTiming.INSERT ) {
 			// we had generation specified...
 			//   	HBM only supports "database generated values"
 			property.setValueGenerationStrategy( new GeneratedValueGeneration( generationTiming ) );
 
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
 							"cannot specify both insert=\"true\" and generated=\"" + generationTiming.name().toLowerCase() +
 									"\" for property: " +
 									propName
 					);
 				}
 			}
 
 			// properties generated on update can never be updateable...
 			if ( property.isUpdateable() && generationTiming == GenerationTiming.ALWAYS ) {
 				if ( updateNode == null ) {
 					// updateable only because the user did not specify
 					// anything; just override it
 					property.setUpdateable( false );
 				}
 				else {
 					// the user specifically supplied update="true",
 					// which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generationTiming.name().toLowerCase() +
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
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
 			LOG.debug( msg );
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuilder columns = new StringBuilder();
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
 			collection.setOrderBy( orderNode.getValue() );
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
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
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
 				tableName = getNamingStrategyDelegate( mappings ).toPhysicalTableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				if ( node.element( "element" ) != null || node.element( "composite-element" ) != null ) {
-					tableName = getNamingStrategyDelegate( mappings ).determineElementCollectionTableLogicalName(
+					tableName = getNamingStrategyDelegate( mappings ).determineImplicitElementCollectionTableName(
 							collection.getOwner().getClassName(),
-							collection.getOwner().getEntityName(),
+							collection.getOwner().getJpaEntityName(),
 							logicalOwnerTableName,
 							path
 					);
 				}
 				else {
-					tableName = getNamingStrategyDelegate( mappings ).determineEntityAssociationJoinTableLogicalName(
-							collection.getOwner().getEntityName(),
+					tableName = getNamingStrategyDelegate( mappings ).determineImplicitEntityAssociationJoinTableName(
+							collection.getOwner().getClassName(),
 							collection.getOwner().getJpaEntityName(),
 							logicalOwnerTableName,
 							null,
 							null,
 							null,
 							path
 					);
 				}
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
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
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
 			fetchable.setLazy( true );
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness( node, fetchable, mappings, "proxy", defaultLazy );
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
 		manyToOne.setReferenceToPrimaryKey( manyToOne.getReferencedPropertyName() == null );
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
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
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToOne.setEmbedded( "true".equals( embed ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 		oneToOne.setReferenceToPrimaryKey( oneToOne.getReferencedPropertyName() == null );
 
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
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
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
 			property.setName( PropertyPath.IDENTIFIER_MAPPER_PROPERTY );
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
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE <many-to-many outer-join="..." is deprecated.:
 					// Default to join and non-lazy for the "second join"
 					// of the many-to-many
 					LOG.deprecatedManyToManyOuterJoin();
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
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
 		}
 		else {
 			if ( "many-to-many".equals( node.getName() ) ) {
 				//NOTE <many-to-many fetch="..." is deprecated.:
 				// Default to join and non-lazy for the "second join"
 				// of the many-to-many
 				LOG.deprecatedManyToManyFetch();
 				lazy = false;
 				fetchStyle = FetchMode.JOIN;
 			}
 			else {
 				boolean join = "join".equals( fetchNode.getValue() );
 				//lazy = !join;
 				fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 			}
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
 				uk.setName( Constraint.generateName( uk.generatedConstraintNamePrefix(),
 						table, uk.getColumns() ) );
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
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
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
 			toOne.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue( "on-delete" ) ) );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
index 9c1d4a53b3..d0cc7b7221 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
@@ -1,126 +1,127 @@
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
 
 
 /**
  * A set of rules for determining the physical column
  * and table names given the information in the mapping
  * document. May be used to implement project-scoped
  * naming standards for database objects.
  *
  * #propertyToTableName(String, String) should be replaced by
  * {@link #collectionTableName(String,String,String,String,String)}
  *
  * @see DefaultNamingStrategy
  * @see ImprovedNamingStrategy
  * @author Gavin King
  * @author Emmanuel Bernard
  *
- * @deprecated
+ * @deprecated A {@link org.hibernate.cfg.naming.NamingStrategyDelegator} should be used instead.
  */
+@Deprecated
 public interface NamingStrategy {
 	/**
 	 * Return a table name for an entity class
 	 * @param className the fully-qualified class name
 	 * @return a table name
 	 */
 	public String classToTableName(String className);
 	/**
 	 * Return a column name for a property path expression
 	 * @param propertyName a property path
 	 * @return a column name
 	 */
 	public String propertyToColumnName(String propertyName);
 	/**
 	 * Alter the table name given in the mapping document
 	 * @param tableName a table name
 	 * @return a table name
 	 */
 	public String tableName(String tableName);
 	/**
 	 * Alter the column name given in the mapping document
 	 * @param columnName a column name
 	 * @return a column name
 	 */
 	public String columnName(String columnName);
 	/**
 	 * Return a collection table name ie an association having a join table
 	 *
 	 * @param ownerEntity
 	 * @param ownerEntityTable owner side table name
 	 * @param associatedEntity
 	 * @param associatedEntityTable reverse side table name if any
 	 * @param propertyName collection role
 	 */
 	public String collectionTableName(
 			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
 			String propertyName
 	);
 	/**
 	 * Return the join key column name ie a FK column used in a JOINED strategy or for a secondary table
 	 *
 	 * @param joinedColumn joined column name (logical one) used to join with
 	 * @param joinedTable joined table name (ie the referenced table) used to join with
 	 */
 	public String joinKeyColumnName(String joinedColumn, String joinedTable);
 	/**
 	 * Return the foreign key column name for the given parameters
 	 * @param propertyName the property name involved
 	 * @param propertyEntityName
 	 * @param propertyTableName the property table name involved (logical one)
 	 * @param referencedColumnName the referenced column name involved (logical one)
 	 */
 	public String foreignKeyColumnName(
 			String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName
 	);
 	/**
 	 * Return the logical column name used to refer to a column in the metadata
 	 * (like index, unique constraints etc)
 	 * A full bijection is required between logicalNames and physical ones
 	 * logicalName have to be case insersitively unique for a given table
 	 *
 	 * @param columnName given column name if any
 	 * @param propertyName property name of this column
 	 */
 	public String logicalColumnName(String columnName, String propertyName);
 	/**
 	 * Returns the logical collection table name used to refer to a table in the mapping metadata
 	 *
 	 * @param tableName the metadata explicit name
 	 * @param ownerEntityTable owner table entity table name (logical one)
 	 * @param associatedEntityTable reverse side table name if any (logical one)
 	 * @param propertyName collection role
 	 */
 	public String logicalCollectionTableName(String tableName, String ownerEntityTable, String associatedEntityTable, String propertyName);
 
 	/**
 	 * Returns the logical foreign key column name used to refer to this column in the mapping metadata
 	 *
 	 * @param columnName given column name in the metadata if any
 	 * @param propertyName property name
 	 * @param referencedColumn referenced column name (logical one) in the join
 	 */
 	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index af52045c58..0090734084 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,1066 +1,1066 @@
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
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import javax.persistence.Access;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.NamedEntityGraph;
 import javax.persistence.NamedEntityGraphs;
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
 import org.hibernate.annotations.DynamicInsert;
 import org.hibernate.annotations.DynamicUpdate;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.NaturalIdCache;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.OptimisticLocking;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.Polymorphism;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.RowId;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.SelectBeforeUpdate;
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
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.cfg.naming.NamingStrategyDelegate;
 import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TableOwner;
 import org.hibernate.mapping.Value;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.BinderHelper.toAliasEntityMap;
 import static org.hibernate.cfg.BinderHelper.toAliasTableMap;
 
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName());
     private static final String NATURAL_ID_CACHE_SUFFIX = "##NaturalId";
 	
 	private String name;
 	private XClass annotatedClass;
 	private PersistentClass persistentClass;
 	private Mappings mappings;
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
 	private String naturalIdCacheRegion;
 	private List<Filter> filters = new ArrayList<Filter>();
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
 
 
 	@SuppressWarnings("SimplifiableConditionalExpression")
 	private void bindHibernateAnnotation(org.hibernate.annotations.Entity hibAnn) {
 		{
 			final DynamicInsert dynamicInsertAnn = annotatedClass.getAnnotation( DynamicInsert.class );
 			this.dynamicInsert = dynamicInsertAnn == null
 					? ( hibAnn == null ? false : hibAnn.dynamicInsert() )
 					: dynamicInsertAnn.value();
 		}
 
 		{
 			final DynamicUpdate dynamicUpdateAnn = annotatedClass.getAnnotation( DynamicUpdate.class );
 			this.dynamicUpdate = dynamicUpdateAnn == null
 					? ( hibAnn == null ? false : hibAnn.dynamicUpdate() )
 					: dynamicUpdateAnn.value();
 		}
 
 		{
 			final SelectBeforeUpdate selectBeforeUpdateAnn = annotatedClass.getAnnotation( SelectBeforeUpdate.class );
 			this.selectBeforeUpdate = selectBeforeUpdateAnn == null
 					? ( hibAnn == null ? false : hibAnn.selectBeforeUpdate() )
 					: selectBeforeUpdateAnn.value();
 		}
 
 		{
 			final OptimisticLocking optimisticLockingAnn = annotatedClass.getAnnotation( OptimisticLocking.class );
 			this.optimisticLockType = optimisticLockingAnn == null
 					? ( hibAnn == null ? OptimisticLockType.VERSION : hibAnn.optimisticLock() )
 					: optimisticLockingAnn.type();
 		}
 
 		{
 			final Polymorphism polymorphismAnn = annotatedClass.getAnnotation( Polymorphism.class );
 			this.polymorphismType = polymorphismAnn == null
 					? ( hibAnn == null ? PolymorphismType.IMPLICIT : hibAnn.polymorphism() )
 					: polymorphismAnn.type();
 		}
 
 		if ( hibAnn != null ) {
 			// used later in bind for logging
 			explicitHibernateEntityAnnotation = true;
 			//persister handled in bind
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
 
 	public boolean isRootEntity() {
 		// This is the best option I can think of here since PersistentClass is most likely not yet fully populated
 		return persistentClass instanceof RootClass;
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
 			rootClass.setNaturalIdCacheRegionName( naturalIdCacheRegion );
 			boolean forceDiscriminatorInSelects = forceDiscriminator == null
 					? mappings.forceDiscriminatorInSelectsByDefault()
 					: forceDiscriminator;
 			rootClass.setForceDiscriminator( forceDiscriminatorInSelects );
 			if( insertableDiscriminator != null) {
 				rootClass.setDiscriminatorInsertable( insertableDiscriminator );
 			}
 		}
 		else {
             if (explicitHibernateEntityAnnotation) {
 				LOG.entityAnnotationOnNonRoot(annotatedClass.getName());
 			}
             if (annotatedClass.isAnnotationPresent(Immutable.class)) {
 				LOG.immutableAnnotationOnNonRoot(annotatedClass.getName());
 			}
 		}
 		persistentClass.setOptimisticLockStyle( getVersioning( optimisticLockType ) );
 		persistentClass.setSelectBeforeUpdate( selectBeforeUpdate );
 
 		//set persister if needed
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
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
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
 				//todo tuplizer.entityModeType
 				persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( annotatedClass.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = annotatedClass.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			//todo tuplizer.entityModeType
 			persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 
 		for ( Filter filter : filters ) {
 			String filterName = filter.name();
 			String cond = filter.condition();
 			if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 				FilterDefinition definition = mappings.getFilterDefinition( filterName );
 				cond = definition == null ? null : definition.getDefaultFilterCondition();
 				if ( StringHelper.isEmpty( cond ) ) {
 					throw new AnnotationException(
 							"no filter condition found for filter " + filterName + " in " + this.name
 					);
 				}
 			}
 			persistentClass.addFilter(filterName, cond, filter.deduceAliasInjectionPoints(), 
 					toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 		}
 		LOG.debugf( "Import with entity name %s", name );
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
 
 		processNamedEntityGraphs();
 	}
 
 	private void processNamedEntityGraphs() {
 		processNamedEntityGraph( annotatedClass.getAnnotation( NamedEntityGraph.class ) );
 		final NamedEntityGraphs graphs = annotatedClass.getAnnotation( NamedEntityGraphs.class );
 		if ( graphs != null ) {
 			for ( NamedEntityGraph graph : graphs.value() ) {
 				processNamedEntityGraph( graph );
 			}
 		}
 	}
 
 	private void processNamedEntityGraph(NamedEntityGraph annotation) {
 		if ( annotation == null ) {
 			return;
 		}
 		mappings.addNamedEntityGraphDefintion( new NamedEntityGraphDefinition( annotation, name, persistentClass.getEntityName() ) );
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
 
 	OptimisticLockStyle getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
 				return OptimisticLockStyle.VERSION;
 			case NONE:
 				return OptimisticLockStyle.NONE;
 			case DIRTY:
 				return OptimisticLockStyle.DIRTY;
 			case ALL:
 				return OptimisticLockStyle.ALL;
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
 		private final String jpaEntityName;
 
 		private EntityTableNamingStrategyHelper(String entityName, String jpaEntityName) {
 			this.entityName = entityName;
 			this.jpaEntityName = jpaEntityName;
 		}
 
 		@Override
 		public String determineImplicitName(NamingStrategy strategy) {
 			return strategy.classToTableName( entityName );
 		}
 
 		@Override
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
 
 		@Override
 		public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
-			return getNamingStrategyDelegate( strategyDelegator ).determinePrimaryTableLogicalName(
+			return getNamingStrategyDelegate( strategyDelegator ).determineImplicitPrimaryTableName(
 					entityName,
 					jpaEntityName
 			);
 		}
 
 		@Override
 		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
 			return getNamingStrategyDelegate( strategyDelegator ).toPhysicalTableName( name );
 		}
 	}
 
 	private static NamingStrategyDelegate getNamingStrategyDelegate(NamingStrategyDelegator strategyDelegator) {
 		return strategyDelegator.getNamingStrategyDelegate( false );
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperclassTable) {
 		EntityTableObjectNameSource tableNameContext = new EntityTableObjectNameSource( tableName, name );
 		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper(
 				persistentClass.getEntityName(),
 				name
 		);
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
 		final RowId rowId = annotatedClass.getAnnotation( RowId.class );
 		if ( rowId != null ) {
 			table.setRowId( rowId.value() );
 		}
 
 		if ( persistentClass instanceof TableOwner ) {
 			LOG.debugf( "Bind entity %s on table %s", persistentClass.getEntityName(), table.getName() );
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
 
 		@Override
 		public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
 			// todo : throw an error?
 			return null;
 		}
 
 		@Override
 		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
 			return getNamingStrategyDelegate( strategyDelegator ).toPhysicalTableName( name );
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
 
 		if ( secondaryTable != null ) {
 			TableBinder.addIndexes( table, secondaryTable.indexes(), mappings );
 		}
 
 			//no check constraints available on joins
 		join.setTable( table );
 
 		//somehow keep joins() for later.
 		//Has to do the work later because it needs persistentClass id!
 		LOG.debugf( "Adding secondary table to entity %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null ) {
 			join.setSequentialSelect( FetchMode.JOIN != matchingTable.fetch() );
 			join.setInverse( matchingTable.inverse() );
 			join.setOptional( matchingTable.optional() );
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlInsert().sql() ) ) {
 				join.setCustomSQLInsert( matchingTable.sqlInsert().sql().trim(),
 						matchingTable.sqlInsert().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlInsert().check().toString().toLowerCase()
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlUpdate().check().toString().toLowerCase()
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlDelete().check().toString().toLowerCase()
 						)
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
 	
 	public void setNaturalIdCache(XClass clazzToProcess, NaturalIdCache naturalIdCacheAnn) {
 		if ( naturalIdCacheAnn != null ) {
 			if ( BinderHelper.isEmptyAnnotationValue( naturalIdCacheAnn.region() ) ) {
 				if (cacheRegion != null) {
 					naturalIdCacheRegion = cacheRegion + NATURAL_ID_CACHE_SUFFIX;
 				}
 				else {
 					naturalIdCacheRegion = clazzToProcess.getName() + NATURAL_ID_CACHE_SUFFIX;
 				}
 			}
 			else {
 				naturalIdCacheRegion = naturalIdCacheAnn.region();
 			}
 		}
 		else {
 			naturalIdCacheRegion = null;
 		}
 	}
 
 	public static String getCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
 		org.hibernate.cache.spi.access.AccessType accessType = strategy.toAccessType();
 		return accessType == null ? null : accessType.getExternalName();
 	}
 
 	public void addFilter(Filter filter) {
 		filters.add(filter);
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
 	public void processComplementaryTableDefinitions(javax.persistence.Table table) {
 		if ( table == null ) return;
 		TableBinder.addIndexes( persistentClass.getTable(), table.indexes(), mappings );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
index 1810726875..68d0b9d42d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
@@ -1,670 +1,670 @@
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
 import java.util.Iterator;
 import java.util.List;
 import javax.persistence.UniqueConstraint;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.Index;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexOrUniqueKeySecondPass;
 import org.hibernate.cfg.JPAIndexHolder;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.cfg.naming.NamingStrategyDelegate;
 import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 
 import org.jboss.logging.Logger;
 
 /**
  * Table related operations
  *
  * @author Emmanuel Bernard
  */
 @SuppressWarnings("unchecked")
 public class TableBinder {
 	//TODO move it to a getter/setter strategy
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TableBinder.class.getName());
 
 	private String schema;
 	private String catalog;
 	private String name;
 	private boolean isAbstract;
 	private List<UniqueConstraintHolder> uniqueConstraints;
 //	private List<String[]> uniqueConstraints;
 	String constraints;
 	Table denormalizedSuperTable;
 	Mappings mappings;
 	private String ownerEntityTable;
 	private String associatedEntityTable;
 	private String propertyName;
 	private String ownerEntity;
 	private String ownerJpaEntity;
 	private String associatedEntity;
 	private String associatedJpaEntity;
 	private boolean isJPA2ElementCollection;
 	private List<JPAIndexHolder> jpaIndexHolders;
 
 	public void setSchema(String schema) {
 		this.schema = schema;
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = catalog;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setAbstract(boolean anAbstract) {
 		isAbstract = anAbstract;
 	}
 
 	public void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
 		this.uniqueConstraints = TableBinder.buildUniqueConstraintHolders( uniqueConstraints );
 	}
 
 	public void setJpaIndex(javax.persistence.Index[] jpaIndex){
 		this.jpaIndexHolders = buildJpaIndexHolder( jpaIndex );
 	}
 
 	public void setConstraints(String constraints) {
 		this.constraints = constraints;
 	}
 
 	public void setDenormalizedSuperTable(Table denormalizedSuperTable) {
 		this.denormalizedSuperTable = denormalizedSuperTable;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public void setJPA2ElementCollection(boolean isJPA2ElementCollection) {
 		this.isJPA2ElementCollection = isJPA2ElementCollection;
 	}
 
 	private static class AssociationTableNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private AssociationTableNameSource(String explicitName, String logicalName) {
 			this.explicitName = explicitName;
 			this.logicalName = logicalName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	// only bind association table currently
 	public Table bind() {
 		//logicalName only accurate for assoc table...
 		final String unquotedOwnerTable = StringHelper.unquote( ownerEntityTable );
 		final String unquotedAssocTable = StringHelper.unquote( associatedEntityTable );
 
 		//@ElementCollection use ownerEntity_property instead of the cleaner ownerTableName_property
 		// ownerEntity can be null when the table name is explicitly set; <== gb: doesn't seem to be true...
 		final String ownerObjectName = isJPA2ElementCollection && ownerEntity != null ?
 				StringHelper.unqualify( ownerEntity ) : unquotedOwnerTable;
 		final ObjectNameSource nameSource = buildNameContext(
 				ownerObjectName,
 				unquotedAssocTable
 		);
 
 		final boolean ownerEntityTableQuoted = StringHelper.isQuoted( ownerEntityTable );
 		final boolean associatedEntityTableQuoted = StringHelper.isQuoted( associatedEntityTable );
 		final ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper = new ObjectNameNormalizer.NamingStrategyHelper() {
 			public String determineImplicitName(NamingStrategy strategy) {
 				throw new AssertionFailure( "method call should have been replaced by #determineImplicitName(NamingStrategyDelegate strategyDelegate)" );
 			}
 
 			public String handleExplicitName(NamingStrategy strategy, String name) {
 				return strategy.tableName( name );
 			}
 
 			@Override
 			public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
 				final NamingStrategyDelegate strategyDelegate = getNamingStrategyDelegate( strategyDelegator );
 				final String strategyResult;
 				if ( isJPA2ElementCollection ) {
-					strategyResult = strategyDelegate.determineElementCollectionTableLogicalName(
+					strategyResult = strategyDelegate.determineImplicitElementCollectionTableName(
 							ownerEntity,
 							ownerJpaEntity,
 							unquotedOwnerTable,
 							propertyName
 					);
 				}
 				else {
-					strategyResult =  strategyDelegate.determineEntityAssociationJoinTableLogicalName(
+					strategyResult =  strategyDelegate.determineImplicitEntityAssociationJoinTableName(
 							ownerEntity,
 							ownerJpaEntity,
 							unquotedOwnerTable,
 							associatedEntity,
 							associatedJpaEntity,
 							unquotedAssocTable,
 							propertyName
 					);
 				}
 				return ownerEntityTableQuoted || associatedEntityTableQuoted
 						? StringHelper.quote( strategyResult )
 						: strategyResult;
 			}
 
 			@Override
 			public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
 				return getNamingStrategyDelegate( strategyDelegator ).toPhysicalTableName( name );
 			}
 		};
 
 		return buildAndFillTable(
 				schema,
 				catalog,
 				nameSource,
 				namingStrategyHelper,
 				isAbstract,
 				uniqueConstraints,
 				jpaIndexHolders,
 				constraints,
 				denormalizedSuperTable,
 				mappings,
 				null
 		);
 	}
 
 	private ObjectNameSource buildNameContext(
 			String unquotedOwnerTable,
 			String unquotedAssocTable) {
 		final NamingStrategyDelegate strategyDelegate = getNamingStrategyDelegate(
 				mappings.getNamingStrategyDelegator()
 		);
 		String logicalName;
 		if ( isJPA2ElementCollection ) {
-			logicalName = strategyDelegate.logicalElementCollectionTableName(
+			logicalName = strategyDelegate.determineLogicalElementCollectionTableName(
 					name,
 					ownerEntity,
 					ownerJpaEntity,
 					unquotedOwnerTable,
 					propertyName
 			);
 		}
 		else {
-			logicalName =  strategyDelegate.logicalEntityAssociationJoinTableName(
+			logicalName =  strategyDelegate.determineLogicalEntityAssociationJoinTableName(
 					name,
 					ownerEntity,
 					ownerJpaEntity,
 					unquotedOwnerTable,
 					associatedEntity,
 					associatedJpaEntity,
 					unquotedAssocTable,
 					propertyName
 			);
 		}
 		if ( StringHelper.isQuoted( ownerEntityTable ) || StringHelper.isQuoted( associatedEntityTable ) ) {
 			logicalName = StringHelper.quote( logicalName );
 		}
 
 		return new AssociationTableNameSource( name, logicalName );
 	}
 
 	private NamingStrategyDelegate getNamingStrategyDelegate(
 			NamingStrategyDelegator strategyDelegator) {
 		return strategyDelegator.getNamingStrategyDelegate( false );
 	}
 
 	public static Table buildAndFillTable(
 			String schema,
 			String catalog,
 			ObjectNameSource nameSource,
 			ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper,
 			boolean isAbstract,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			List<JPAIndexHolder> jpaIndexHolders,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings,
 			String subselect){
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 
 		String realTableName = mappings.getObjectNameNormalizer().normalizeDatabaseIdentifier(
 				nameSource.getExplicitName(),
 				namingStrategyHelper
 		);
 
 		final Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					subselect,
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					subselect,
 					isAbstract
 			);
 		}
 
 		if ( CollectionHelper.isNotEmpty( uniqueConstraints ) ) {
 			mappings.addUniqueConstraintHolders( table, uniqueConstraints );
 		}
 
 		if ( CollectionHelper.isNotEmpty( jpaIndexHolders ) ) {
 			mappings.addJpaIndexHolders( table, jpaIndexHolders );
 		}
 
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 
 		// logicalName is null if we are in the second pass
 		final String logicalName = nameSource.getLogicalName();
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 
 
 	public static Table buildAndFillTable(
 			String schema,
 			String catalog,
 			ObjectNameSource nameSource,
 			ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper,
 			boolean isAbstract,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings,
 			String subselect) {
 		return buildAndFillTable( schema, catalog, nameSource, namingStrategyHelper, isAbstract, uniqueConstraints, null, constraints
 		, denormalizedSuperTable, mappings, subselect);
 	}
 
 	/**
 	 * @deprecated Use {@link #buildAndFillTable} instead.
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static Table fillTable(
 			String schema,
 			String catalog,
 			String realTableName,
 			String logicalName,
 			boolean isAbstract,
 			List uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 		Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					null, //subselect
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					null, //subselect
 					isAbstract
 			);
 		}
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraints( table, uniqueConstraints );
 		}
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 		//logicalName is null if we are in the second pass
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	public static void bindFk(
 			PersistentClass referencedEntity,
 			PersistentClass destinationEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		PersistentClass associatedClass;
 		if ( destinationEntity != null ) {
 			//overridden destination
 			associatedClass = destinationEntity;
 		}
 		else {
 			associatedClass = columns[0].getPropertyHolder() == null
 					? null
 					: columns[0].getPropertyHolder().getPersistentClass();
 		}
 		final String mappedByProperty = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedByProperty ) ) {
 			/**
 			 * Get the columns of the mapped-by property
 			 * copy them and link the copy to the actual value
 			 */
 			LOG.debugf( "Retrieving property %s.%s", associatedClass.getEntityName(), mappedByProperty );
 
 			final Property property = associatedClass.getRecursiveProperty( columns[0].getMappedBy() );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				Collection collection = ( (Collection) property.getValue() );
 				Value element = collection.getElement();
 				if ( element == null ) {
 					throw new AnnotationException(
 							"Illegal use of mappedBy on both sides of the relationship: "
 									+ associatedClass.getEntityName() + "." + mappedByProperty
 					);
 				}
 				mappedByColumns = element.getColumnIterator();
 			}
 			else {
 				mappedByColumns = property.getValue().getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 		}
 		else if ( columns[0].isImplicit() ) {
 			/**
 			 * if columns are implicit, then create the columns based on the
 			 * referenced entity id columns
 			 */
 			Iterator idColumns;
 			if ( referencedEntity instanceof JoinedSubclass ) {
 				idColumns = referencedEntity.getKey().getColumnIterator();
 			}
 			else {
 				idColumns = referencedEntity.getIdentifier().getColumnIterator();
 			}
 			while ( idColumns.hasNext() ) {
 				Column column = (Column) idColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingDefaultColumnNaming( column, referencedEntity, value );
 			}
 		}
 		else {
 			int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, referencedEntity, mappings );
 
 			if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 				String referencedPropertyName;
 				if ( value instanceof ToOne ) {
 					referencedPropertyName = ( (ToOne) value ).getReferencedPropertyName();
 				}
 				else if ( value instanceof DependantValue ) {
 					String propertyName = columns[0].getPropertyName();
 					if ( propertyName != null ) {
 						Collection collection = (Collection) referencedEntity.getRecursiveProperty( propertyName )
 								.getValue();
 						referencedPropertyName = collection.getReferencedPropertyName();
 					}
 					else {
 						throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 					}
 
 				}
 				else {
 					throw new AssertionFailure(
 							"Do a property ref on an unexpected Value type: "
 									+ value.getClass().getName()
 					);
 				}
 				if ( referencedPropertyName == null ) {
 					throw new AssertionFailure(
 							"No property ref found while expected"
 					);
 				}
 				Property synthProp = referencedEntity.getReferencedProperty( referencedPropertyName );
 				if ( synthProp == null ) {
 					throw new AssertionFailure(
 							"Cannot find synthProp: " + referencedEntity.getEntityName() + "." + referencedPropertyName
 					);
 				}
 				linkJoinColumnWithValueOverridingNameIfImplicit(
 						referencedEntity, synthProp.getColumnIterator(), columns, value
 				);
 
 			}
 			else {
 				if ( Ejb3JoinColumn.NO_REFERENCE == fkEnum ) {
 					//implicit case, we hope PK and FK columns are in the same order
 					if ( columns.length != referencedEntity.getIdentifier().getColumnSpan() ) {
 						throw new AnnotationException(
 								"A Foreign key refering " + referencedEntity.getEntityName()
 										+ " from " + associatedClass.getEntityName()
 										+ " has the wrong number of column. should be " + referencedEntity.getIdentifier()
 										.getColumnSpan()
 						);
 					}
 					linkJoinColumnWithValueOverridingNameIfImplicit(
 							referencedEntity,
 							referencedEntity.getIdentifier().getColumnIterator(),
 							columns,
 							value
 					);
 				}
 				else {
 					//explicit referencedColumnName
 					Iterator idColItr = referencedEntity.getKey().getColumnIterator();
 					org.hibernate.mapping.Column col;
 					Table table = referencedEntity.getTable(); //works cause the pk has to be on the primary table
 					if ( !idColItr.hasNext() ) {
 						LOG.debug( "No column in the identifier!" );
 					}
 					while ( idColItr.hasNext() ) {
 						boolean match = false;
 						//for each PK column, find the associated FK column.
 						col = (org.hibernate.mapping.Column) idColItr.next();
 						for (Ejb3JoinColumn joinCol : columns) {
 							String referencedColumn = joinCol.getReferencedColumn();
 							referencedColumn = mappings.getPhysicalColumnName( referencedColumn, table );
 							//In JPA 2 referencedColumnName is case insensitive
 							if ( referencedColumn.equalsIgnoreCase( col.getQuotedName() ) ) {
 								//proper join column
 								if ( joinCol.isNameDeferred() ) {
 									joinCol.linkValueUsingDefaultColumnNaming(
 											col, referencedEntity, value
 									);
 								}
 								else {
 									joinCol.linkWithValue( value );
 								}
 								joinCol.overrideFromReferencedColumnIfNecessary( col );
 								match = true;
 								break;
 							}
 						}
 						if ( !match ) {
 							throw new AnnotationException(
 									"Column name " + col.getName() + " of "
 											+ referencedEntity.getEntityName() + " not found in JoinColumns.referencedColumnName"
 							);
 						}
 					}
 				}
 			}
 		}
 		value.createForeignKey();
 		if ( unique ) {
 			createUniqueConstraint( value );
 		}
 	}
 
 	public static void linkJoinColumnWithValueOverridingNameIfImplicit(
 			PersistentClass referencedEntity,
 			Iterator columnIterator,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value) {
 		for (Ejb3JoinColumn joinCol : columns) {
 			Column synthCol = (Column) columnIterator.next();
 			if ( joinCol.isNameDeferred() ) {
 				//this has to be the default value
 				joinCol.linkValueUsingDefaultColumnNaming( synthCol, referencedEntity, value );
 			}
 			else {
 				joinCol.linkWithValue( value );
 				joinCol.overrideFromReferencedColumnIfNecessary( synthCol );
 			}
 		}
 	}
 
 	public static void createUniqueConstraint(Value value) {
 		Iterator iter = value.getColumnIterator();
 		ArrayList cols = new ArrayList();
 		while ( iter.hasNext() ) {
 			cols.add( iter.next() );
 		}
 		value.getTable().createUniqueKey( cols );
 	}
 
 	public static void addIndexes(Table hibTable, Index[] indexes, Mappings mappings) {
 		for (Index index : indexes) {
 			//no need to handle inSecondPass here since it is only called from EntityBinder
 			mappings.addSecondPass(
 					new IndexOrUniqueKeySecondPass( hibTable, index.name(), index.columnNames(), mappings )
 			);
 		}
 	}
 
 	public static void addIndexes(Table hibTable, javax.persistence.Index[] indexes, Mappings mappings) {
 		mappings.addJpaIndexHolders( hibTable, buildJpaIndexHolder( indexes ) );
 	}
 
 	public static List<JPAIndexHolder> buildJpaIndexHolder(javax.persistence.Index[] indexes){
 		List<JPAIndexHolder> holders = new ArrayList<JPAIndexHolder>( indexes.length );
 		for(javax.persistence.Index index : indexes){
 			holders.add( new JPAIndexHolder( index ) );
 		}
 		return holders;
 	}
 
 	/**
 	 * @deprecated Use {@link #buildUniqueConstraintHolders} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public static List<String[]> buildUniqueConstraints(UniqueConstraint[] constraintsArray) {
 		List<String[]> result = new ArrayList<String[]>();
 		if ( constraintsArray.length != 0 ) {
 			for (UniqueConstraint uc : constraintsArray) {
 				result.add( uc.columnNames() );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Build a list of {@link org.hibernate.cfg.UniqueConstraintHolder} instances given a list of
 	 * {@link UniqueConstraint} annotations.
 	 *
 	 * @param annotations The {@link UniqueConstraint} annotations.
 	 *
 	 * @return The built {@link org.hibernate.cfg.UniqueConstraintHolder} instances.
 	 */
 	public static List<UniqueConstraintHolder> buildUniqueConstraintHolders(UniqueConstraint[] annotations) {
 		List<UniqueConstraintHolder> result;
 		if ( annotations == null || annotations.length == 0 ) {
 			result = java.util.Collections.emptyList();
 		}
 		else {
 			result = new ArrayList<UniqueConstraintHolder>( CollectionHelper.determineProperSizing( annotations.length ) );
 			for ( UniqueConstraint uc : annotations ) {
 				result.add(
 						new UniqueConstraintHolder()
 								.setName( uc.name() )
 								.setColumns( uc.columnNames() )
 				);
 			}
 		}
 		return result;
 	}
 
 	public void setDefaultName(
 			String ownerEntity,
 			String ownerJpaEntity,
 			String ownerEntityTable,
 			String associatedEntity,
 			String associatedJpaEntity,
 			String associatedEntityTable,
 			String propertyName
 	) {
 		this.ownerEntity = ownerEntity;
 		this.ownerJpaEntity = ownerJpaEntity;
 		this.ownerEntityTable = ownerEntityTable;
 		this.associatedEntity = associatedEntity;
 		this.associatedJpaEntity = associatedJpaEntity;
 		this.associatedEntityTable = associatedEntityTable;
 		this.propertyName = propertyName;
 		this.name = null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java
index b8f6e466cf..df7feb04b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java
@@ -1,123 +1,119 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Gail Badner
  */
-public class HbmNamingStrategyDelegate extends AbstractNamingStrategyDelegate {
+public class HbmNamingStrategyDelegate extends NamingStrategyDelegateAdapter {
 
 	@Override
-	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName) {
+	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
 		return StringHelper.unqualify( entityName );
 	}
 
 	@Override
-	public String determineElementCollectionTableLogicalName(
+	public String determineImplicitElementCollectionTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		return ownerEntityTable
 				+ '_'
-				+ StringHelper.unqualify( propertyNamePath );
+				+ StringHelper.unqualify( propertyPath );
 	}
 
 	@Override
-	public String determineElementCollectionForeignKeyColumnName(String propertyName, String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName) {
+	public String determineImplicitElementCollectionJoinColumnName(String ownerEntityName, String ownerJpaEntityName, String ownerEntityTable, String referencedColumnName, String propertyPath) {
 		throw new UnsupportedOperationException( "Method not supported for Hibernate-specific mappings" );
 	}
 
 	@Override
-	public String determineEntityAssociationJoinTableLogicalName(
+	public String determineImplicitEntityAssociationJoinTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		return ownerEntityTable
 				+ '_'
-				+ StringHelper.unqualify( propertyNamePath );
+				+ StringHelper.unqualify( propertyPath );
 	}
 
 	@Override
-	public String determineEntityAssociationForeignKeyColumnName(
-			String propertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName) {
+	public String determineImplicitEntityAssociationJoinColumnName(
+			String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName, String propertyPath) {
 		throw new UnsupportedOperationException( "Method not supported for Hibernate-specific mappings" );
 	}
 
 	@Override
-	public String logicalElementCollectionTableName(
+	public String determineLogicalElementCollectionTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String propertyName) {
 		if ( tableName != null ) {
 			return tableName;
 		}
 		else {
-			return determineElementCollectionTableLogicalName(
+			return determineImplicitElementCollectionTableName(
 					ownerEntityName,
 					ownerJpaEntityName,
 					ownerEntityTable,
 					propertyName
 			);
 		}
 	}
 
 	@Override
-	public String logicalEntityAssociationJoinTableName(
+	public String determineLogicalEntityAssociationJoinTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
 			String propertyName) {
 		if ( tableName != null ) {
 			return tableName;
 		}
 		else {
-			return determineEntityAssociationJoinTableLogicalName(
+			return determineImplicitEntityAssociationJoinTableName(
 					ownerEntityName,
 					ownerJpaEntityName,
 					ownerEntityTable,
 					associatedEntityName,
 					associatedJpaEntityName,
 					associatedEntityTable,
 					propertyName
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/DefaultNamingStrategyDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/ImprovedNamingStrategyDelegator.java
similarity index 70%
rename from hibernate-core/src/main/java/org/hibernate/cfg/naming/DefaultNamingStrategyDelegator.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/naming/ImprovedNamingStrategyDelegator.java
index 38e46c0cc4..9f5adb4009 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/DefaultNamingStrategyDelegator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/ImprovedNamingStrategyDelegator.java
@@ -1,48 +1,57 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import java.io.Serializable;
 
 /**
  * @author Gail Badner
  */
-public class DefaultNamingStrategyDelegator implements NamingStrategyDelegator, Serializable {
-	public static final DefaultNamingStrategyDelegator INSTANCE = new DefaultNamingStrategyDelegator();
+public class ImprovedNamingStrategyDelegator implements NamingStrategyDelegator, Serializable {
+	public static final NamingStrategyDelegator DEFAULT_INSTANCE = new ImprovedNamingStrategyDelegator();
 
 	private final NamingStrategyDelegate hbmNamingStrategyDelegate;
 	private final NamingStrategyDelegate jpaNamingStrategyDelegate;
 
-	private DefaultNamingStrategyDelegator() {
-		this.hbmNamingStrategyDelegate = new HbmNamingStrategyDelegate();
-		this.jpaNamingStrategyDelegate = new JpaNamingStrategyDelegate();
+	private ImprovedNamingStrategyDelegator() {
+		this(
+				new HbmNamingStrategyDelegate(),
+				new JpaNamingStrategyDelegate()
+		);
+	}
+
+	protected ImprovedNamingStrategyDelegator(
+			NamingStrategyDelegate hbmNamingStrategyDelegate,
+			NamingStrategyDelegate jpaNamingStrategyDelegate) {
+		this.hbmNamingStrategyDelegate = hbmNamingStrategyDelegate;
+		this.jpaNamingStrategyDelegate = jpaNamingStrategyDelegate;
 	}
 
 	@Override
 	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
 		return isHbm ?
 				hbmNamingStrategyDelegate :
 				jpaNamingStrategyDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java
index 653de66ca6..6c0e68a766 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java
@@ -1,183 +1,175 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Gail Badner
  */
-public class JpaNamingStrategyDelegate extends AbstractNamingStrategyDelegate {
+public class JpaNamingStrategyDelegate extends NamingStrategyDelegateAdapter {
 
 	@Override
-	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName) {
+	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
 		return StringHelper.unqualify( determineEntityNameToUse( entityName, jpaEntityName ) );
 	}
 
 	@Override
-	public String determineElementCollectionTableLogicalName(
+	public String determineImplicitElementCollectionTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		// JPA states we should use the following as default:
 		//      "The concatenation of the name of the containing entity and the name of the
 		//       collection attribute, separated by an underscore.
 		// aka:
 		//     if owning entity has a JPA entity name: {OWNER JPA ENTITY NAME}_{COLLECTION ATTRIBUTE NAME}
 		//     otherwise: {OWNER ENTITY NAME}_{COLLECTION ATTRIBUTE NAME}
 		return determineEntityNameToUse( ownerEntityName, ownerJpaEntityName )
 				+ '_'
-				+ StringHelper.unqualify( propertyNamePath );
+				+ StringHelper.unqualify( propertyPath );
 	}
 
 	@Override
-	public String determineElementCollectionForeignKeyColumnName(
-			String propertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName) {
+	public String determineImplicitElementCollectionJoinColumnName(
+			String ownerEntityName, String ownerJpaEntityName, String ownerEntityTable, String referencedColumnName, String propertyPath) {
 		// JPA states we should use the following as default:
 		//     "The concatenation of the following: the name of the entity; "_"; the name of the
 		//      referenced primary key column"
-		return determineEntityNameToUse( propertyEntityName, propertyJpaEntityName )
+		return determineEntityNameToUse( ownerEntityName, ownerJpaEntityName )
 				+ '_'
 				+ referencedColumnName;
 	}
 
 	@Override
-	public String determineEntityAssociationJoinTableLogicalName(
+	public String determineImplicitEntityAssociationJoinTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		// JPA states we should use the following as default:
 		//		"The concatenated names of the two associated primary entity tables (owning side
 		//		first), separated by an underscore."
 		// aka:
 		// 		{OWNING SIDE PRIMARY TABLE NAME}_{NON-OWNING SIDE PRIMARY TABLE NAME}
 
 		return  ownerEntityTable
 				+ '_'
 				+ associatedEntityTable;
 	}
 
 	@Override
-	public String determineEntityAssociationForeignKeyColumnName(
-			String referencingPropertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName) {
+	public String determineImplicitEntityAssociationJoinColumnName(
+			String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName, String referencingPropertyName) {
 		// JPA states we should use the following as default:
 		//      "The concatenation of the following: the name of the referencing relationship
 		//      property or field of the referencing entity or embeddable class; "_"; the name
 		//      of the referenced primary key column. If there is no such referencing relationship
 		//      property or field in the entity, or if the join is for an element collection, the
 		//      join column name is formed as the concatenation of the following: the name of the
 		//      entity; "_"; the name of the referenced primary key column
 		// The part referring to an entity collection can be disregarded here since, determination of
 		// an element collection foreign key column name is covered  by #entityAssociationJoinTableName().
 		//
 		// For a unidirectional association:
 		//      {PROPERTY_ENTITY_NAME}_{REFERENCED_COLUMN_NAME}
 		// For a bidirectional association:
 		//      {REFERENCING_PROPERTY_NAME}_{REFERENCED_COLUMN_NAME}
 		final String header;
 		if ( referencingPropertyName == null ) {
 			// This is a unidirectional association.
 			header = determineEntityNameToUse( propertyEntityName, propertyJpaEntityName );
 		}
 		else {
 			// This is a bidirectional association.
 			header = StringHelper.unqualify( referencingPropertyName );
 		}
 		if ( header == null ) {
 			throw new AssertionFailure( "propertyJpaEntityName and referencingPropertyName cannot both be empty." );
 		}
 		return toPhysicalColumnName( header + "_" + referencedColumnName );
 	}
 
 	@Override
-	public String logicalElementCollectionTableName(
+	public String determineLogicalElementCollectionTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String propertyName) {
 		if ( tableName != null ) {
 			return tableName;
 		}
 		else {
-			return determineElementCollectionTableLogicalName(
+			return determineImplicitElementCollectionTableName(
 					ownerEntityName,
 					ownerJpaEntityName,
 					ownerEntityTable,
 					propertyName
 			);
 		}
 	}
 
 	@Override
-	public String logicalEntityAssociationJoinTableName(
+	public String determineLogicalEntityAssociationJoinTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
 			String propertyName) {
 		if ( tableName != null ) {
 			return tableName;
 		}
 		else {
-			return determineEntityAssociationJoinTableLogicalName(
+			return determineImplicitEntityAssociationJoinTableName(
 					ownerEntityName,
 					ownerJpaEntityName,
 					ownerEntityTable,
 					associatedEntityName,
 					associatedJpaEntityName,
 					associatedEntityTable,
 					propertyName
 			);
 		}
 	}
 
 	private String determineEntityNameToUse(String entityName, String jpaEntityName) {
 		if ( StringHelper.isNotEmpty( jpaEntityName ) ) {
 			// prefer the JPA entity name, if specified...
 			return jpaEntityName;
 		}
 		else {
 			// otherwise, use the Hibernate entity name
 			return StringHelper.unqualifyEntityName( entityName );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java
index 2a75f793e8..f9dc61c0c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java
@@ -1,125 +1,128 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 /**
  * @author Gail Badner
  */
-public class LegacyHbmNamingStrategyDelegate extends AbstractLegacyNamingStrategyDelegate {
+@Deprecated
+public class LegacyHbmNamingStrategyDelegate extends LegacyNamingStrategyDelegateAdapter {
 
 	public LegacyHbmNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
 		super( context );
 	}
 
 	@Override
-	public String determineElementCollectionTableLogicalName(
+	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
+		return getNamingStrategy().classToTableName( entityName );
+	}
+
+	@Override
+	public String determineImplicitElementCollectionTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		return getNamingStrategy().collectionTableName(
 				ownerEntityName,
 				ownerEntityTable,
 				null,
 				null,
-				propertyNamePath
+				propertyPath
 		);
 	}
 
 	@Override
-	public String determineElementCollectionForeignKeyColumnName(String propertyName, String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName) {
+	public String determineImplicitElementCollectionJoinColumnName(
+			String ownerEntityName, String ownerJpaEntityName, String ownerEntityTable, String referencedColumnName, String propertyPath) {
 		return getNamingStrategy().foreignKeyColumnName(
-				propertyName,
-				propertyEntityName,
-				propertyTableName,
+				propertyPath,
+				ownerEntityName,
+				ownerEntityTable,
 				referencedColumnName
 		);
 	}
 
 	@Override
-	public String determineEntityAssociationJoinTableLogicalName(
+	public String determineImplicitEntityAssociationJoinTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		return getNamingStrategy().collectionTableName(
 				ownerEntityName,
 				ownerEntityTable,
 				associatedEntityName,
 				associatedEntityTable,
-				propertyNamePath
+				propertyPath
 		);
 	}
 
 	@Override
-	public String determineEntityAssociationForeignKeyColumnName(
-			String propertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName) {
+	public String determineImplicitEntityAssociationJoinColumnName(
+			String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName, String propertyPath) {
 		return getNamingStrategy().foreignKeyColumnName(
-				propertyName,
+				propertyPath,
 				propertyEntityName,
 				propertyTableName,
 				referencedColumnName
 		);
 	}
 
 	@Override
-	public String logicalElementCollectionTableName(
+	public String determineLogicalElementCollectionTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String propertyName) {
 		return getNamingStrategy().logicalCollectionTableName(
 				tableName,
 				ownerEntityTable,
 				null,
 				propertyName
 		);
 	}
 
 	@Override
-	public String logicalEntityAssociationJoinTableName(
+	public String determineLogicalEntityAssociationJoinTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
 			String propertyName) {
 		return getNamingStrategy().logicalCollectionTableName(
 				tableName,
 				ownerEntityTable,
 				associatedEntityTable,
 				propertyName
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyStandardNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyJpaNamingStrategyDelegate.java
similarity index 67%
rename from hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyStandardNamingStrategyDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyJpaNamingStrategyDelegate.java
index 57e5e080a6..45aff669b2 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyStandardNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyJpaNamingStrategyDelegate.java
@@ -1,132 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Gail Badner
  */
-public class LegacyStandardNamingStrategyDelegate extends AbstractLegacyNamingStrategyDelegate {
+@Deprecated
+public class LegacyJpaNamingStrategyDelegate extends LegacyNamingStrategyDelegateAdapter {
 
-	LegacyStandardNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
+	LegacyJpaNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
 		super( context );
 	}
 
 	@Override
-	public String determineElementCollectionTableLogicalName(
+	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName) {
+		// jpaEntityname is being passed here in order to not cause a regression. See HHH-4312.
+		return getNamingStrategy().classToTableName( jpaEntityName );
+	}
+
+	@Override
+	public String determineImplicitElementCollectionTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		return getNamingStrategy().collectionTableName(
 				ownerEntityName,
 				StringHelper.unqualifyEntityName( ownerEntityName ),
 				null,
 				null,
-				propertyNamePath
+				propertyPath
 		);
 	}
 
 	@Override
-	public String determineElementCollectionForeignKeyColumnName(
-			String propertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName) {
+	public String determineImplicitElementCollectionJoinColumnName(
+			String ownerEntityName, String ownerJpaEntityName, String ownerEntityTable, String referencedColumnName, String propertyPath) {
 		return getNamingStrategy().foreignKeyColumnName(
-				propertyName,
-				propertyEntityName,
-				StringHelper.unqualifyEntityName( propertyEntityName ),
+				propertyPath,
+				ownerEntityName,
+				StringHelper.unqualifyEntityName( ownerEntityName ),
 				referencedColumnName
 		);
 	}
 
 	@Override
-	public String determineEntityAssociationJoinTableLogicalName(
+	public String determineImplicitEntityAssociationJoinTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
-			String propertyNamePath) {
+			String propertyPath) {
 		return getNamingStrategy().collectionTableName(
 				ownerEntityName,
 				ownerEntityTable,
 				associatedEntityName,
 				associatedEntityTable,
-				propertyNamePath
+				propertyPath
 		);
 	}
 
 	@Override
-	public String determineEntityAssociationForeignKeyColumnName(
-			String propertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName) {
+	public String determineImplicitEntityAssociationJoinColumnName(
+			String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName, String propertyPath) {
 		return getNamingStrategy().foreignKeyColumnName(
-				propertyName,
+				propertyPath,
 				propertyEntityName,
 				propertyTableName,
 				referencedColumnName
 		);
 	}
 
 	@Override
-	public String logicalElementCollectionTableName(
+	public String determineLogicalElementCollectionTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String propertyName) {
 		return getNamingStrategy().logicalCollectionTableName(
 				tableName,
 				ownerEntityName == null ? null : StringHelper.unqualifyEntityName( ownerEntityName ),
 				null,
 				propertyName
 		);
 	}
 
 	@Override
-	public String logicalEntityAssociationJoinTableName(
+	public String determineLogicalEntityAssociationJoinTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
 			String propertyName) {
 		return getNamingStrategy().logicalCollectionTableName(
 				tableName,
 				ownerEntityTable,
 				associatedEntityTable,
 				propertyName
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java
index 1a33e95ccb..5d33748784 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java
@@ -1,35 +1,36 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import org.hibernate.cfg.NamingStrategy;
 
 /**
  * @author Gail Badner
  */
+@Deprecated
 public interface LegacyNamingStrategyDelegate extends NamingStrategyDelegate {
 	public static interface LegacyNamingStrategyDelegateContext {
 		public NamingStrategy getNamingStrategy();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractLegacyNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegateAdapter.java
similarity index 68%
rename from hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractLegacyNamingStrategyDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegateAdapter.java
index 6f5355dd8d..da2fec0e2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractLegacyNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegateAdapter.java
@@ -1,79 +1,74 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import java.io.Serializable;
 
 import org.hibernate.cfg.NamingStrategy;
 
 /**
  * @author Gail Badner
  */
-public abstract class AbstractLegacyNamingStrategyDelegate implements NamingStrategyDelegate, Serializable {
+@Deprecated
+public abstract class LegacyNamingStrategyDelegateAdapter implements NamingStrategyDelegate, Serializable {
 	private final LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context;
 
-	public AbstractLegacyNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
+	public LegacyNamingStrategyDelegateAdapter(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
 		this.context = context;
 	}
 
 	protected NamingStrategy getNamingStrategy() {
 		return context.getNamingStrategy();
 	}
 
 	@Override
-	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName) {
-		// jpaEntity name is being passed here in order to not cause a regression. See HHH-4312.
-		return getNamingStrategy().classToTableName( jpaEntityName );
-	}
-
-	@Override
 	public String toPhysicalTableName(String tableName) {
 		return getNamingStrategy().tableName( tableName );
 	}
 
 	@Override
 	public String toPhysicalColumnName(String columnName) {
 		return getNamingStrategy().columnName( columnName );
 	}
 
 	@Override
-	public String determineAttributeColumnName(String propertyName) {
-		return getNamingStrategy().propertyToColumnName( propertyName );
+	public String determineImplicitPropertyColumnName(String propertyPath) {
+		return getNamingStrategy().propertyToColumnName( propertyPath );
 	}
 
 	@Override
-	public String determineJoinKeyColumnName(String joinedColumn, String joinedTable) {
+	public String toPhysicalJoinKeyColumnName(String joinedColumn, String joinedTable) {
 		return getNamingStrategy().joinKeyColumnName( joinedColumn, joinedTable );
 	}
 
 	@Override
-	public String logicalColumnName(String columnName, String propertyName) {
+	public String determineLogicalColumnName(String columnName, String propertyName) {
 		return getNamingStrategy().logicalColumnName( columnName, propertyName );
 	}
 
 	@Override
-	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
+	public String determineLogicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
 		return getNamingStrategy().logicalCollectionColumnName( columnName, propertyName, referencedColumn );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java
index 35a32ea726..af6db597c3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java
@@ -1,67 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import java.io.Serializable;
 
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 
 import static org.hibernate.cfg.naming.LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext;
 
 /**
- *
  * @deprecated Needed as a transitory implementation until the deprecated NamingStrategy contract
  * can be removed.
  *
  * @author Gail Badner
  */
 @Deprecated
 public class LegacyNamingStrategyDelegator
 		implements NamingStrategyDelegator, LegacyNamingStrategyDelegateContext, Serializable {
+	public static final NamingStrategyDelegator DEFAULT_INSTANCE = new LegacyNamingStrategyDelegator();
+
 	private final NamingStrategy namingStrategy;
 	private final NamingStrategyDelegate hbmNamingStrategyDelegate;
 	private final NamingStrategyDelegate jpaNamingStrategyDelegate;
 
-	public LegacyNamingStrategyDelegator() {
+	private LegacyNamingStrategyDelegator() {
 		this( EJB3NamingStrategy.INSTANCE );
 	}
 
 	public LegacyNamingStrategyDelegator(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		this.hbmNamingStrategyDelegate = new LegacyHbmNamingStrategyDelegate( this );
-		this.jpaNamingStrategyDelegate = new LegacyStandardNamingStrategyDelegate( this );
+		this.jpaNamingStrategyDelegate = new LegacyJpaNamingStrategyDelegate( this );
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	@Override
 	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
 		return isHbm ?
 				hbmNamingStrategyDelegate :
 				jpaNamingStrategyDelegate;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java
index de204ff000..bc402e203f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java
@@ -1,102 +1,173 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 /**
+ * A
  * @author Gail Badner
  */
 public interface NamingStrategyDelegate {
 
-	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName);
-
-	public String determineAttributeColumnName(String propertyName);
-
 	/**
-	 * Alter the table name given in the mapping document
-	 * @param tableName a table name
-	 * @return a table name
+	 * Determine the name of a entity's primary table when a name is not explicitly configured.
+	 *
+	 * @param entityName The fully qualified entity name
+	 * @param jpaEntityName The entity name provided by the {@link javax.persistence.Entity}
+	 *                      {@code name} attribute; or, if not mapped in this way, then the
+	 *                      unqualified entity name.
+	 *
+	 * @return The implicit table name.
 	 */
-	public String toPhysicalTableName(String tableName);
+	public String determineImplicitPrimaryTableName(String entityName, String jpaEntityName);
 
 	/**
-	 * Alter the column name given in the mapping document
-	 * @param columnName a column name
-	 * @return a column name
+	 * Determine the name of a property's column when a name is not explicitly configured.
+	 *
+	 * @param propertyPath The property path (not qualified by the entity name)
+	 * @return The implicit column name.
 	 */
-	public String toPhysicalColumnName(String columnName);
-
+	public String determineImplicitPropertyColumnName(String propertyPath);
 
-	public String determineElementCollectionTableLogicalName(
+	/**
+	 * Determine the name of a collection table for a collection of (non-entity) values
+	 * when a name is not explicitly configured.
+	 *
+	 * @param ownerEntityName The fully qualified entity name for the entity that owns the collection.
+	 * @param ownerJpaEntityName The entity name provided by the {@link javax.persistence.Entity}
+	 *                      {@code name} attribute for the entity that owns the collection;
+	 *                      or, if not mapped in this way, then the unqualified owner entity name.
+	 * @param ownerEntityTable The owner entity's physical primary table name;
+	 * @param propertyPath The property path (not qualified by the entity name),
+	 * @return The implicit table name.
+	 */
+	public String determineImplicitElementCollectionTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
-			String propertyName);
-
-	public String determineElementCollectionForeignKeyColumnName(
-			String propertyName,
-			String propertyEntityName,
-			String propertyJpaEntityName,
-			String propertyTableName,
-			String referencedColumnName);
+			String propertyPath);
 
+	/**
+	 * Determine the name of the join column in a collection table for
+	 * a collection of (non-entity) values when a name is not explicitly configured.
+	 *
+	 * @param ownerEntityName The fully qualified name of the entity that owns the collection.
+	 * @param ownerJpaEntityName The entity name provided by the {@link javax.persistence.Entity}
+	 *                      {@code name} attribute for the entity that owns the collection;
+	 *                      or, if not mapped in this way, then the unqualified entity name.
+	 * @param ownerEntityTable The owner entity's physical primary table name;
+	 * @param referencedColumnName The physical name of the column that the join column references.
+	 * @param propertyPath The property path (not qualified by the entity name),
+	 * @return The implicit column name.
+	 */
+	public String determineImplicitElementCollectionJoinColumnName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String referencedColumnName,
+			String propertyPath);
 
-	public String determineEntityAssociationJoinTableLogicalName(
+	/**
+	 * Determine the name of the join table for an entity (singular or plural) association when
+	 * a name is not explicitly configured.
+	 *
+	 * @param ownerEntityName The fully qualified name of the entity that owns the association;.
+	 * @param ownerJpaEntityName The entity name provided by the {@link javax.persistence.Entity}
+	 *                      {@code name} attribute for the entity that owns the association;
+	 *                      or, if not mapped in this way, then the unqualified owner entity name.
+	 * @param ownerEntityTable The owner entity's physical primary table name;
+	 * @param associatedEntityName The fully qualified name of the associated entity.
+	 * @param associatedJpaEntityName The entity name provided by the {@link javax.persistence.Entity}
+	 *                      {@code name} attribute for the associated entity;
+	 *                      or, if not mapped in this way, then the unqualified associated entity name.
+	 * @param associatedEntityTable The associated entity's physical primary table name;
+	 * @param propertyPath The property path (not qualified by the entity name),
+	 * @return The implicit table name.
+	 */
+	public String determineImplicitEntityAssociationJoinTableName(
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
-			String propertyName);
+			String propertyPath);
 
-	public String determineEntityAssociationForeignKeyColumnName(
-			String propertyName,
+	/**
+	 * Determine the name of join column for an entity (singular or plural) association when
+	 * a name is not explicitly configured.
+	 *
+	 * @param propertyEntityName The fully qualified name of the entity that contains the association;
+	 * @param propertyJpaEntityName The entity name provided by the {@link javax.persistence.Entity}
+	 *                      {@code name} attribute for the entity that contains the association;
+	 *                      or, if not mapped in this way, then the unqualified entity name.
+	 * @param propertyTableName The physical primary table name for the entity that contains the association.
+	 * @param referencedColumnName  The physical name of the column that the join column references.
+	 * @param propertyPath The property path (not qualified by the entity name),
+	 * @return The implicit table name.
+	 */
+	public String determineImplicitEntityAssociationJoinColumnName(
 			String propertyEntityName,
 			String propertyJpaEntityName,
 			String propertyTableName,
-			String referencedColumnName);
+			String referencedColumnName,
+			String propertyPath);
 
-	public String determineJoinKeyColumnName(String joinedColumn, String joinedTable);
+	public String toPhysicalJoinKeyColumnName(String joinedColumn, String joinedTable);
 
-	public String logicalColumnName(String columnName, String propertyName);
+	public String determineLogicalColumnName(String columnName, String propertyName);
 
-	public String logicalElementCollectionTableName(
+	public String determineLogicalElementCollectionTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String propertyName);
 
-	public String logicalEntityAssociationJoinTableName(
+	public String determineLogicalEntityAssociationJoinTableName(
 			String tableName,
 			String ownerEntityName,
 			String ownerJpaEntityName,
 			String ownerEntityTable,
 			String associatedEntityName,
 			String associatedJpaEntityName,
 			String associatedEntityTable,
 			String propertyName);
 
-	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn);
+	public String determineLogicalCollectionColumnName(String columnName, String propertyName, String referencedColumn);
+
+	/**
+	 * Alter the table name given in the mapping document
+	 * @param tableName a table name
+	 * @return a table name
+	 */
+	public String toPhysicalTableName(String tableName);
+
+	/**
+	 * Alter the column name given in the mapping document
+	 * @param columnName a column name
+	 * @return a column name
+	 */
+	public String toPhysicalColumnName(String columnName);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegateAdapter.java
similarity index 74%
rename from hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractNamingStrategyDelegate.java
rename to hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegateAdapter.java
index 871527e79e..e06bd2d428 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractNamingStrategyDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegateAdapter.java
@@ -1,66 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
 import java.io.Serializable;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
+ * An "adapter" for {@link NamingStrategyDelegate} implementations to extend.
+ *
  * @author Gail Badner
  */
-public abstract class AbstractNamingStrategyDelegate implements NamingStrategyDelegate, Serializable {
+public abstract class NamingStrategyDelegateAdapter implements NamingStrategyDelegate, Serializable {
+
+	@Override
+	public String determineImplicitPropertyColumnName(String propertyPath) {
+		return StringHelper.unqualify( propertyPath );
+	}
 
 	@Override
 	public String toPhysicalTableName(String tableName) {
 		return tableName;
 	}
 
 	@Override
 	public String toPhysicalColumnName(String columnName) {
 		return columnName;
 	}
 
 	@Override
-	public String determineAttributeColumnName(String propertyName) {
-		return StringHelper.unqualify( propertyName );
-	}
-
-	@Override
-	public String determineJoinKeyColumnName(String joinedColumn, String joinedTable) {
+	public String toPhysicalJoinKeyColumnName(String joinedColumn, String joinedTable) {
 		return toPhysicalColumnName( joinedColumn );
 	}
 
 	@Override
-	public String logicalColumnName(String columnName, String propertyName) {
+	public String determineLogicalColumnName(String columnName, String propertyName) {
 		return StringHelper.isNotEmpty( columnName ) ? columnName : StringHelper.unqualify( propertyName );
 	}
 
 	@Override
-	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
+	public String determineLogicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
 		return StringHelper.isNotEmpty( columnName ) ?
 				columnName :
 				StringHelper.unqualify( propertyName ) + "_" + referencedColumn;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java
index d13764acb2..bb4682b2af 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java
@@ -1,44 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.cfg.naming;
 
-import org.hibernate.cfg.NamingStrategy;
-
 /**
- * Provides access to the appropriate {@link NamingStrategyDelegate}.
+ * Provides access to the appropriate {@link NamingStrategyDelegate}, depending on whether a
+ * mapping is Hibernate-specific (i.e., hbm.xml).
  *
  * @author Gail Badner
+ *
+ * @see org.hibernate.cfg.naming.NamingStrategyDelegate
  */
 public interface NamingStrategyDelegator {
 
 	/**
 	 * Returns the appropriate {@link NamingStrategyDelegate}.
 	 *
 	 * @param isHbm - true, if {@link NamingStrategyDelegate} is to be used for a
 	 * hibernate-specific (hbm.xml) mapping; false, otherwise.
 	 *
 	 * @return the appropriate {@link NamingStrategyDelegate}
 	 */
 	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm);
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java
similarity index 57%
rename from hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomNamingCollectionElementTest.java
rename to hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java
index bad57f1f03..984dd131c5 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomNamingCollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomImprovedNamingCollectionElementTest.java
@@ -1,244 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.collectionelement;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.EJB3NamingStrategy;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.cfg.naming.AbstractLegacyNamingStrategyDelegate;
-import org.hibernate.cfg.naming.LegacyHbmNamingStrategyDelegate;
-import org.hibernate.cfg.naming.LegacyNamingStrategyDelegate;
-import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
-import org.hibernate.cfg.naming.NamingStrategyDelegate;
+import org.hibernate.cfg.naming.HbmNamingStrategyDelegate;
+import org.hibernate.cfg.naming.ImprovedNamingStrategyDelegator;
+import org.hibernate.cfg.naming.JpaNamingStrategyDelegate;
 import org.hibernate.testing.TestForIssue;
 
 /**
  * @author Gail Badner
  */
-public class CustomNamingCollectionElementTest extends CollectionElementTest {
+public class CustomImprovedNamingCollectionElementTest extends ImprovedNamingCollectionElementTest {
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
-		cfg.setNamingStrategyDelegator( new MyLegacyNamingStrategyDelegator() );
+		cfg.setNamingStrategyDelegator( new MyImprovedNamingStrategyDelegator() );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		// MyNamingStrategyDelegator will use the owner primary table name (instead of JPA entity name) in generated collection table.
 		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		// MyNamingStrategyDelegator will use owner primary table name (instead of JPA entity name) in generated collection table.
 		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_TABLE_elements" );
 	}
 
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		// MyNamingStrategyDelegator will use owner primary table name, which will default to the JPA entity name
 		// in generated join column.
 		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
 		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_TABLE_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
 		checkDefaultJoinColumnName( Boy.class, "hatedNames", "tbl_Boys_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
 		checkDefaultCollectionTableName( Boy.class, "hatedNames", "tbl_Boys_hatedNames" );
 	}
 
-	static class MyLegacyNamingStrategyDelegator extends LegacyNamingStrategyDelegator {
-		private final NamingStrategyDelegate hbmNamingStrategyDelegate = new LegacyHbmNamingStrategyDelegate( this );
-		private final NamingStrategyDelegate nonHbmNamingStrategyDelegate = new MyNonHbmNamingStrategyDelegator( this );
-
-		@Override
-		public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
-			return isHbm ? hbmNamingStrategyDelegate : nonHbmNamingStrategyDelegate;
-		}
-
-		@Override
-		public NamingStrategy getNamingStrategy() {
-			return EJB3NamingStrategy.INSTANCE;
+	static class MyImprovedNamingStrategyDelegator extends ImprovedNamingStrategyDelegator {
+		public MyImprovedNamingStrategyDelegator() {
+			super( new HbmNamingStrategyDelegate(), new MyNonHbmNamingStrategyDelegate() );
 		}
 
-		private class MyNonHbmNamingStrategyDelegator extends AbstractLegacyNamingStrategyDelegate {
-			MyNonHbmNamingStrategyDelegator(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context)  {
-				super( context );
-			}
-
-			@Override
-			public String toPhysicalTableName(String tableName) {
-				return getNamingStrategy().tableName( tableName );
-			}
-
-			@Override
-			public String toPhysicalColumnName(String columnName) {
-				return getNamingStrategy().columnName( columnName );
-			}
-
-			@Override
-			public String determineElementCollectionTableLogicalName(
-					String ownerEntityName,
-					String ownerJpaEntityName,
-					String ownerEntityTable,
-					String propertyNamePath) {
-				return getNamingStrategy().collectionTableName(
-						ownerEntityName,
-						ownerEntityTable,
-						null,
-						null,
-						propertyNamePath
-				);
-			}
-
-			@Override
-			public String determineElementCollectionForeignKeyColumnName(
-					String propertyName,
-					String propertyEntityName,
-					String propertyJpaEntityName,
-					String propertyTableName,
-					String referencedColumnName) {
-				return getNamingStrategy().foreignKeyColumnName(
-						propertyName,
-						propertyEntityName,
-						propertyTableName,
-						referencedColumnName
-				);
-			}
-
+		private static class MyNonHbmNamingStrategyDelegate extends JpaNamingStrategyDelegate {
 			@Override
-			public String determineEntityAssociationJoinTableLogicalName(
+			public String determineImplicitElementCollectionTableName(
 					String ownerEntityName,
 					String ownerJpaEntityName,
 					String ownerEntityTable,
-					String associatedEntityName,
-					String associatedJpaEntityName,
-					String associatedEntityTable,
-					String propertyNamePath) {
-				return getNamingStrategy().collectionTableName(
-						ownerEntityName,
-						ownerEntityTable,
-						associatedEntityName,
-						associatedEntityTable,
-						propertyNamePath
-				);
-			}
-
-			@Override
-			public String determineEntityAssociationForeignKeyColumnName(
-					String propertyName,
-					String propertyEntityName,
-					String propertyJpaEntityName,
-					String propertyTableName,
-					String referencedColumnName) {
-				return getNamingStrategy().foreignKeyColumnName(
-						propertyName,
-						propertyEntityName,
-						propertyTableName,
-						referencedColumnName
-				);
+					String propertyPath) {
+				// This impl uses the owner entity table name instead of the JPA entity name when
+				// generating the implicit name.
+				int loc = propertyPath.lastIndexOf(".");
+				final String unqualifiedPropertyName = loc < 0 ? propertyPath : propertyPath.substring( loc + 1 );
+				return ownerEntityTable
+						+ '_'
+						+ unqualifiedPropertyName;
 			}
 
 			@Override
-			public String logicalElementCollectionTableName(
-					String tableName,
+			public String determineImplicitElementCollectionJoinColumnName(
 					String ownerEntityName,
 					String ownerJpaEntityName,
 					String ownerEntityTable,
-					String propertyName) {
-				return getNamingStrategy().logicalCollectionTableName(
-						tableName,
-						ownerEntityTable,
-						null,
-						propertyName
-				);
-			}
-
-			@Override
-			public String logicalEntityAssociationJoinTableName(
-					String tableName,
-					String ownerEntityName,
-					String ownerJpaEntityName,
-					String ownerEntityTable,
-					String associatedEntityName,
-					String associatedJpaEntityName,
-					String associatedEntityTable,
-					String propertyName) {
-				return getNamingStrategy().logicalCollectionTableName(
-						tableName,
-						ownerEntityTable,
-						associatedEntityTable,
-						propertyName
-				);
+					String referencedColumnName,
+					String propertyPath) {
+				return ownerEntityTable
+						+ '_'
+						+ referencedColumnName;
 			}
 		}
 	}
-
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java
similarity index 93%
rename from hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java
rename to hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java
index 79525aa978..1cff724362 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/DefaultNamingCollectionElementTest.java
@@ -1,408 +1,414 @@
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
 package org.hibernate.test.annotations.collectionelement;
 
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 
 import org.junit.Test;
 
 import org.hibernate.Filter;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.test.annotations.Country;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
+ * Tests @ElementCollection using the default "legacy" NamingStrategyDelegator which does not
+ * comply with JPA spec in some cases. See HHH-9387 and HHH-9389 for more information..
+ *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
+ * @author Gail Badner
  */
 @SuppressWarnings("unchecked")
-public class CollectionElementTest extends BaseCoreFunctionalTestCase {
+public class DefaultNamingCollectionElementTest extends BaseCoreFunctionalTestCase {
+
 	@Test
 	public void testSimpleElement() throws Exception {
 		assertEquals(
 				"BoyFavoriteNumbers",
 				configuration().getCollectionMapping( Boy.class.getName() + '.' + "favoriteNumbers" )
 						.getCollectionTable().getName()
 		);
 		Session s = openSession();
 		s.getTransaction().begin();
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		boy.getNickNames().add( "Johnny" );
 		boy.getNickNames().add( "Thing" );
 		boy.getScorePerNickName().put( "Johnny", 3 );
 		boy.getScorePerNickName().put( "Thing", 5 );
 		int[] favNbrs = new int[4];
 		for (int index = 0; index < favNbrs.length - 1; index++) {
 			favNbrs[index] = index * 3;
 		}
 		boy.setFavoriteNumbers( favNbrs );
 		boy.getCharacters().add( Character.GENTLE );
 		boy.getCharacters().add( Character.CRAFTY );
 
 		HashMap<String,FavoriteFood> foods = new HashMap<String,FavoriteFood>();
 		foods.put( "breakfast", FavoriteFood.PIZZA);
 		foods.put( "lunch", FavoriteFood.KUNGPAOCHICKEN);
 		foods.put( "dinner", FavoriteFood.SUSHI);
 		boy.setFavoriteFood(foods);
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertNotNull( boy.getNickNames() );
 		assertTrue( boy.getNickNames().contains( "Thing" ) );
 		assertNotNull( boy.getScorePerNickName() );
 		assertTrue( boy.getScorePerNickName().containsKey( "Thing" ) );
 		assertEquals( Integer.valueOf( 5 ), boy.getScorePerNickName().get( "Thing" ) );
 		assertNotNull( boy.getFavoriteNumbers() );
 		assertEquals( 3, boy.getFavoriteNumbers()[1] );
 		assertTrue( boy.getCharacters().contains( Character.CRAFTY ) );
 		assertTrue( boy.getFavoriteFood().get("dinner").equals(FavoriteFood.SUSHI));
 		assertTrue( boy.getFavoriteFood().get("lunch").equals(FavoriteFood.KUNGPAOCHICKEN));
 		assertTrue( boy.getFavoriteFood().get("breakfast").equals(FavoriteFood.PIZZA));
 		List result = s.createQuery( "select boy from Boy boy join boy.nickNames names where names = :name" )
 				.setParameter( "name", "Thing" ).list();
 		assertEquals( 1, result.size() );
 		s.delete( boy );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCompositeElement() throws Exception {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		Toy toy = new Toy();
 		toy.setName( "Balloon" );
 		toy.setSerial( "serial001" );
 		toy.setBrand( new Brand() );
 		toy.getBrand().setName( "Bandai" );
 		boy.getFavoriteToys().add( toy );
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertNotNull( boy );
 		assertNotNull( boy.getFavoriteToys() );
 		assertTrue( boy.getFavoriteToys().contains( toy ) );
 		assertEquals( "@Parent is failing", boy, boy.getFavoriteToys().iterator().next().getOwner() );
 		s.delete( boy );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testAttributedJoin() throws Exception {
 		Session s = openSession();
 		s.getTransaction().begin();
 		Country country = new Country();
 		country.setName( "Australia" );
 		s.persist( country );
 
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		CountryAttitude attitude = new CountryAttitude();
 		// TODO: doesn't work
 		attitude.setBoy( boy );
 		attitude.setCountry( country );
 		attitude.setLikes( true );
 		boy.getCountryAttitudes().add( attitude );
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertTrue( boy.getCountryAttitudes().contains( attitude ) );
 		s.delete( boy );
 		s.delete( s.get( Country.class, country.getId() ) );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testLazyCollectionofElements() throws Exception {
 		assertEquals(
 				"BoyFavoriteNumbers",
 				configuration().getCollectionMapping( Boy.class.getName() + '.' + "favoriteNumbers" )
 						.getCollectionTable().getName()
 		);
 		Session s = openSession();
 		s.getTransaction().begin();
 		Boy boy = new Boy();
 		boy.setFirstName( "John" );
 		boy.setLastName( "Doe" );
 		boy.getNickNames().add( "Johnny" );
 		boy.getNickNames().add( "Thing" );
 		boy.getScorePerNickName().put( "Johnny", 3 );
 		boy.getScorePerNickName().put( "Thing", 5 );
 		int[] favNbrs = new int[4];
 		for (int index = 0; index < favNbrs.length - 1; index++) {
 			favNbrs[index] = index * 3;
 		}
 		boy.setFavoriteNumbers( favNbrs );
 		boy.getCharacters().add( Character.GENTLE );
 		boy.getCharacters().add( Character.CRAFTY );
 		s.persist( boy );
 		s.getTransaction().commit();
 		s.clear();
 		Transaction tx = s.beginTransaction();
 		boy = (Boy) s.get( Boy.class, boy.getId() );
 		assertNotNull( boy.getNickNames() );
 		assertTrue( boy.getNickNames().contains( "Thing" ) );
 		assertNotNull( boy.getScorePerNickName() );
 		assertTrue( boy.getScorePerNickName().containsKey( "Thing" ) );
 		assertEquals( new Integer( 5 ), boy.getScorePerNickName().get( "Thing" ) );
 		assertNotNull( boy.getFavoriteNumbers() );
 		assertEquals( 3, boy.getFavoriteNumbers()[1] );
 		assertTrue( boy.getCharacters().contains( Character.CRAFTY ) );
 		List result = s.createQuery( "select boy from Boy boy join boy.nickNames names where names = :name" )
 				.setParameter( "name", "Thing" ).list();
 		assertEquals( 1, result.size() );
 		s.delete( boy );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchEagerAndFilter() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 
 		TestCourse test = new TestCourse();
 
 		LocalizedString title = new LocalizedString( "title in english" );
 		title.getVariations().put( Locale.FRENCH.getLanguage(), "title en francais" );
 		test.setTitle( title );
 		s.save( test );
 
 		s.flush();
 		s.clear();
 
 		Filter filter = s.enableFilter( "selectedLocale" );
 		filter.setParameter( "param", "fr" );
 
 		Query q = s.createQuery( "from TestCourse t" );
 		List l = q.list();
 		assertEquals( 1, l.size() );
 
 		TestCourse t = (TestCourse) s.get( TestCourse.class, test.getTestCourseId() );
 		assertEquals( 1, t.getTitle().getVariations().size() );
 
 		tx.rollback();
 
 		s.close();
 	}
 
 	@Test
 	public void testMapKeyType() throws Exception {
 		Matrix m = new Matrix();
 		m.getMvalues().put( 1, 1.1f );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( m );
 		s.flush();
 		s.clear();
 		m = (Matrix) s.get( Matrix.class, m.getId() );
 		assertEquals( 1.1f, m.getMvalues().get( 1 ), 0.01f );
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testDefaultValueColumnForBasic() throws Exception {
 		isDefaultValueCollectionColumnPresent( Boy.class.getName(), "hatedNames" );
 		isDefaultValueCollectionColumnPresent( Boy.class.getName(), "preferredNames" );
 		isCollectionColumnPresent( Boy.class.getName(), "nickNames", "nickNames" );
 		isDefaultValueCollectionColumnPresent( Boy.class.getName(), "scorePerPreferredName");
 	}
 
 	private void isDefaultValueCollectionColumnPresent(String collectionOwner, String propertyName) {
 		isCollectionColumnPresent( collectionOwner, propertyName, propertyName );
 	}
 
 	private void isCollectionColumnPresent(String collectionOwner, String propertyName, String columnName) {
 		final Collection collection = configuration().getCollectionMapping( collectionOwner + "." + propertyName );
 		final Iterator columnIterator = collection.getCollectionTable().getColumnIterator();
 		boolean hasDefault = false;
 		while ( columnIterator.hasNext() ) {
 			Column column = (Column) columnIterator.next();
 			if ( columnName.equals( column.getName() ) ) hasDefault = true;
 		}
 		assertTrue( "Could not find " + columnName, hasDefault );
 	}
 
-
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameNoOverrides() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Products has @Entity (no @Table)
 		checkDefaultCollectionTableName( BugSystem.class, "bugs", "BugSystem_bugs" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		checkDefaultCollectionTableName( Boy.class, "hatedNames", "Boy_hatedNames" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
-		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
+		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Matrix_mvalues" );
 	}
 
 	@Test
-	@TestForIssue( jiraKey = "HHH-9387")
-	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
+	@TestForIssue( jiraKey = "HHH-9389")
+	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
-
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
-		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_elements" );
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
+		checkDefaultJoinColumnName( Owner.class, "elements", "Owner_id" );
 	}
 
 	protected void checkDefaultCollectionTableName(
 			Class<?> ownerEntityClass,
 			String ownerCollectionPropertyName,
 			String expectedCollectionTableName) {
 		final org.hibernate.mapping.Collection collection = configuration().getCollectionMapping(
 				ownerEntityClass.getName() + '.' + ownerCollectionPropertyName
 		);
 		final org.hibernate.mapping.Table table = collection.getCollectionTable();
 		assertEquals( expectedCollectionTableName, table.getName() );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnNoOverrides() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Products has @Entity (no @Table)
 		checkDefaultJoinColumnName( BugSystem.class, "bugs", "BugSystem_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Boy has @Entity @Table(name="tbl_Boys")
 		checkDefaultJoinColumnName( Boy.class, "hatedNames", "Boy_id" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
-		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
+		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Matrix_mId" );
 	}
 
 	@Test
-	@TestForIssue( jiraKey = "HHH-9389")
-	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
+	@TestForIssue( jiraKey = "HHH-9387")
+	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
-
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
-		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_id" );
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
+		checkDefaultCollectionTableName( Owner.class, "elements", "Owner_elements" );
 	}
 
 	protected void checkDefaultJoinColumnName(
 			Class<?> ownerEntityClass,
 			String ownerCollectionPropertyName,
 			String ownerForeignKeyNameExpected) {
 		final org.hibernate.mapping.Collection ownerCollection = configuration().getCollectionMapping(
 				ownerEntityClass.getName() + '.' + ownerCollectionPropertyName
 		);
 		// The default owner join column can only be computed if it has a PK with 1 column.
 		assertEquals ( 1, ownerCollection.getOwner().getKey().getColumnSpan() );
 		assertEquals( ownerForeignKeyNameExpected, ownerCollection.getKey().getColumnIterator().next().getText() );
 
 		boolean hasOwnerFK = false;
 		for ( Iterator it=ownerCollection.getCollectionTable().getForeignKeyIterator(); it.hasNext(); ) {
 			final ForeignKey fk = (ForeignKey) it.next();
 			assertSame( ownerCollection.getCollectionTable(), fk.getTable() );
 			if ( fk.getColumnSpan() > 1 ) {
 				continue;
 			}
 			if ( fk.getColumn( 0 ).getText().equals( ownerForeignKeyNameExpected ) ) {
 				assertSame( ownerCollection.getOwner().getTable(), fk.getReferencedTable() );
 				hasOwnerFK = true;
 			}
 		}
 		assertTrue( hasOwnerFK );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Boy.class,
 				Country.class,
 				TestCourse.class,
 				Matrix.class,
 				Owner.class,
 				BugSystem.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/LegacyNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java
similarity index 75%
rename from hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/LegacyNamingCollectionElementTest.java
rename to hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java
index e9b8ddd4d2..3099b6940e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/LegacyNamingCollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/ImprovedNamingCollectionElementTest.java
@@ -1,88 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.collectionelement;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.EJB3NamingStrategy;
-import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.cfg.naming.ImprovedNamingStrategyDelegator;
 import org.hibernate.testing.TestForIssue;
 
 /**
+ * Tests @ElementCollection using the "improved" NamingStrategyDelegator which complies
+ * with JPA spec.
+ *
  * @author Gail Badner
  */
-public class LegacyNamingCollectionElementTest extends CollectionElementTest {
+public class ImprovedNamingCollectionElementTest extends DefaultNamingCollectionElementTest {
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
-		cfg.setNamingStrategyDelegator( new LegacyNamingStrategyDelegator( EJB3NamingStrategy.INSTANCE ) );
+		cfg.setNamingStrategyDelegator( ImprovedNamingStrategyDelegator.DEFAULT_INSTANCE );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
-		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
-		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Matrix_mvalues" );
+		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
-		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
-		checkDefaultCollectionTableName( Owner.class, "elements", "Owner_elements" );
+		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_elements" );
 	}
 
-
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
-		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
-		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Matrix_mId" );
+		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
-		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
-		checkDefaultJoinColumnName( Owner.class, "elements", "Owner_id" );
+		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_id" );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java
index 174d56d790..03bec71d67 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java
@@ -1,52 +1,51 @@
 //$Id$
 package org.hibernate.test.annotations.manytomany;
 import java.util.Collection;
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.OrderBy;
 import javax.persistence.Table;
 
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 @Table(name = "tbl_group")
 @FilterDef(name="Groupfilter")
 public class Group {
 	private Integer id;
 	private Collection<Permission> permissions;
 
 	@Id
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	@ManyToMany(cascade = CascadeType.PERSIST)
-	@JoinTable(joinColumns = {@JoinColumn( name="groupId")})
 	@OrderBy("expirationDate")
 	@Where(clause = "1=1")
 	@WhereJoinTable(clause = "2=2")
 	@Filter(name="Groupfilter", condition = "3=3")
 	@FilterJoinTable(name="Groupfilter", condition = "4=4")
 	public Collection<Permission> getPermissions() {
 		return permissions;
 	}
 
 	public void setPermissions(Collection<Permission> permissions) {
 		this.permissions = permissions;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/GroupWithSet.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/GroupWithSet.java
index b6551506fc..e524946650 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/GroupWithSet.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/GroupWithSet.java
@@ -1,52 +1,51 @@
 //$Id$
 package org.hibernate.test.annotations.manytomany;
 import java.util.Set;
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.OrderBy;
 import javax.persistence.Table;
 
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 
 /**
  * @author Emmanuel Bernard
  */
 @Entity
 @Table(name = "tbl_group")
 @FilterDef(name="Groupfilter")
 public class GroupWithSet {
 	private Integer id;
 	private Set<Permission> permissions;
 
 	@Id
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	@ManyToMany(cascade = CascadeType.PERSIST)
-	@JoinTable(joinColumns = {@JoinColumn( name="groupId")})
 	@OrderBy("expirationDate")
 	@Where(clause = "1=1")
 	@WhereJoinTable(clause = "2=2")
 	@Filter(name="Groupfilter", condition = "3=3")
 	@FilterJoinTable(name="Groupfilter", condition = "4=4")
 	public Set<Permission> getPermissions() {
 		return permissions;
 	}
 
 	public void setPermissions(Set<Permission> permissions) {
 		this.permissions = permissions;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/DefaultNamingManyToManyTest.java
similarity index 91%
rename from hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java
rename to hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/DefaultNamingManyToManyTest.java
index d059f9110a..4e310563f6 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/DefaultNamingManyToManyTest.java
@@ -1,263 +1,247 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.manytomany.defaults;
 
 import java.util.Iterator;
 
 import org.junit.Test;
 
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.type.EntityType;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
- * Tests default names for @JoinTable and @JoinColumn for unidirectional and bidirectional
- * many-to-many associations.
+ * Tests names generated for @JoinTable and @JoinColumn for unidirectional and bidirectional
+ * many-to-many associations when the "default" {@link org.hibernate.cfg.naming.NamingStrategyDelegator}
+ * is used. The current default does not comply with the JPA spec in some cases. See HHH-9390
+ * for more information.
  *
  * NOTE: expected primary table names and join columns are explicit here to ensure that
  * entity names/tables and PK columns are not changed (which would invalidate these test cases).
  *
  * @author Gail Badner
  */
-public class ManyToManyDefaultsTest  extends BaseCoreFunctionalTestCase {
+public class DefaultNamingManyToManyTest extends BaseCoreFunctionalTestCase {
 
 	@Test
 	public void testBidirNoOverrides() {
 		// Employee.contactInfo.phoneNumbers: associated entity: PhoneNumber
 		// both have @Entity with no name configured and default primary table names;
 		// Primary table names default to unqualified entity classes.
 		// PK column for Employee.id: id (default)
 		// PK column for PhoneNumber.phNumber: phNumber (default)
 		// bidirectional association
 		checkDefaultJoinTablAndJoinColumnNames(
 				Employee.class,
 				"contactInfo.phoneNumbers",
 				"employees",
 				"Employee_PhoneNumber",
 				"employees_id",
 				"phoneNumbers_phNumber"
 		);
 	}
 
 	@Test
 	public void testBidirOwnerPKOverride() {
 		// Store.customers; associated entity: KnownClient
 		// both have @Entity with no name configured and default primary table names
 		// Primary table names default to unqualified entity classes.
 		// PK column for Store.id: sId
 		// PK column for KnownClient.id: id (default)
 		// bidirectional association
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"customers",
 				"stores",
 				"Store_KnownClient",
 				"stores_sId",
 				"customers_id"
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerPKAssocEntityNamePKOverride() {
 		// Store.items; associated entity: Item
 		// Store has @Entity with no name configured and no @Table
 		// Item has @Entity(name="ITEM") and no @Table
 		// PK column for Store.id: sId
 		// PK column for Item: iId
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"items",
 				null,
 				"Store_ITEM",
 				"Store_sId",
 				"items_iId"
 
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerPKAssocPrimaryTableNameOverride() {
 		// Store.implantedIn; associated entity: City
 		// Store has @Entity with no name configured and no @Table
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// PK column for Store.id: sId
 		// PK column for City.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"implantedIn",
 				null,
 				"Store_tbl_city",
 				"Store_sId",
 				"implantedIn_id"
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerPKAssocEntityNamePrimaryTableOverride() {
 		// Store.categories; associated entity: Category
 		// Store has @Entity with no name configured and no @Table
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// PK column for Store.id: sId
 		// PK column for Category.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Store.class,
 				"categories",
 				null,
 				"Store_CATEGORY_TAB",
 				"Store_sId",
 				"categories_id"
 		);
 	}
 
 	@Test
 	public void testUnidirOwnerEntityNamePKAssocPrimaryTableOverride() {
 		// Item.producedInCities: associated entity: City
 		// Item has @Entity(name="ITEM") and no @Table
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// PK column for Item: iId
 		// PK column for City.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Item.class,
 				"producedInCities",
 				null,
 				"ITEM_tbl_city",
 				"ITEM_iId",
 				"producedInCities_id"
 		);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
-	public void testUnidirOwnerPrimaryTableAssocEntityNamePKOverride() {
-		// City.stolenItems; associated entity: Item
-		// City has @Entity with no name configured and @Table(name = "tbl_city")
-		// Item has @Entity(name="ITEM") and no @Table
-		// PK column for City.id: id (default)
-		// PK column for Item: iId
-		// unidirectional
-		checkDefaultJoinTablAndJoinColumnNames(
-				City.class,
-				"stolenItems",
-				null,
-				"tbl_city_ITEM",
-				"City_id",
-				"stolenItems_iId"
-		);
-	}
-
-	@Test
-	@TestForIssue( jiraKey = "HHH-9390")
 	public void testUnidirOwnerEntityNamePrimaryTableOverride() {
 		// Category.clients: associated entity: KnownClient
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// KnownClient has @Entity with no name configured and no @Table
 		// PK column for Category.id: id (default)
 		// PK column for KnownClient.id: id (default)
 		// unidirectional
+		// legacy behavior would use the table name in the generated join column.
 		checkDefaultJoinTablAndJoinColumnNames(
 				Category.class,
 				"clients",
 				null,
 				"CATEGORY_TAB_KnownClient",
-				"CATEGORY_id",
+				"CATEGORY_TAB_id",
 				"clients_id"
 
 		);
 	}
 
 	protected void checkDefaultJoinTablAndJoinColumnNames(
 			Class<?> ownerEntityClass,
 			String ownerCollectionPropertyName,
 			String inverseCollectionPropertyName,
 			String expectedCollectionTableName,
 			String ownerForeignKeyNameExpected,
 			String inverseForeignKeyNameExpected) {
 		final org.hibernate.mapping.Collection collection = configuration().getCollectionMapping( ownerEntityClass.getName() + '.' + ownerCollectionPropertyName );
 		final org.hibernate.mapping.Table table = collection.getCollectionTable();
 		assertEquals( expectedCollectionTableName, table.getName() );
 
 		final org.hibernate.mapping.Collection ownerCollection = configuration().getCollectionMapping(
 				ownerEntityClass.getName() + '.' + ownerCollectionPropertyName
 		);
 		// The default owner and inverse join columns can only be computed if they have PK with 1 column.
 		assertEquals ( 1, ownerCollection.getOwner().getKey().getColumnSpan() );
 		assertEquals( ownerForeignKeyNameExpected, ownerCollection.getKey().getColumnIterator().next().getText() );
 
 		final EntityType associatedEntityType =  (EntityType) ownerCollection.getElement().getType();
 		final PersistentClass associatedPersistentClass =
 				configuration().getClassMapping( associatedEntityType.getAssociatedEntityName() );
 		assertEquals( 1, associatedPersistentClass.getKey().getColumnSpan() );
 		if ( inverseCollectionPropertyName != null ) {
 			final org.hibernate.mapping.Collection inverseCollection = configuration().getCollectionMapping(
 					associatedPersistentClass.getEntityName() + '.' + inverseCollectionPropertyName
 			);
 			assertEquals(
 					inverseForeignKeyNameExpected,
 					inverseCollection.getKey().getColumnIterator().next().getText()
 			);
 		}
 		boolean hasOwnerFK = false;
 		boolean hasInverseFK = false;
 		for ( Iterator it=ownerCollection.getCollectionTable().getForeignKeyIterator(); it.hasNext(); ) {
 			final ForeignKey fk = (ForeignKey) it.next();
 			assertSame( ownerCollection.getCollectionTable(), fk.getTable() );
 			if ( fk.getColumnSpan() > 1 ) {
 				continue;
 			}
 			if ( fk.getColumn( 0 ).getText().equals( ownerForeignKeyNameExpected ) ) {
 				assertSame( ownerCollection.getOwner().getTable(), fk.getReferencedTable() );
 				hasOwnerFK = true;
 			}
 			else  if ( fk.getColumn( 0 ).getText().equals( inverseForeignKeyNameExpected ) ) {
 				assertSame( associatedPersistentClass.getTable(), fk.getReferencedTable() );
 				hasInverseFK = true;
 			}
 		}
 		assertTrue( hasOwnerFK );
 		assertTrue( hasInverseFK );
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				Category.class,
 				City.class,
 				Employee.class,
 				Item.class,
 				KnownClient.class,
 				PhoneNumber.class,
 				Store.class,
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/LegacyManyToManyDefaultsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ImprovedManyToManyDefaultsTest.java
similarity index 80%
rename from hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/LegacyManyToManyDefaultsTest.java
rename to hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ImprovedManyToManyDefaultsTest.java
index 24b8b81932..7ba440294c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/LegacyManyToManyDefaultsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ImprovedManyToManyDefaultsTest.java
@@ -1,84 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.annotations.manytomany.defaults;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.EJB3NamingStrategy;
-import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
-import org.hibernate.testing.FailureExpected;
+import org.hibernate.cfg.naming.ImprovedNamingStrategyDelegator;
 import org.hibernate.testing.TestForIssue;
 
 /**
+ * Tests names generated for @JoinTable and @JoinColumn for unidirectional and bidirectional
+ * many-to-many associations when the "improved" {@link org.hibernate.cfg.naming.NamingStrategyDelegator}
+ * is used. The "improved" {@link org.hibernate.cfg.naming.NamingStrategyDelegator} complies with the JPA
+ * spec.
+ *
  * @author Gail Badner
  */
-public class LegacyManyToManyDefaultsTest extends ManyToManyDefaultsTest {
+public class ImprovedManyToManyDefaultsTest extends DefaultNamingManyToManyTest {
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
-		cfg.setNamingStrategyDelegator( new LegacyNamingStrategyDelegator( EJB3NamingStrategy.INSTANCE ) );
+		cfg.setNamingStrategyDelegator( ImprovedNamingStrategyDelegator.DEFAULT_INSTANCE );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
 	public void testUnidirOwnerPrimaryTableAssocEntityNamePKOverride() {
 		// City.stolenItems; associated entity: Item
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// Item has @Entity(name="ITEM") and no @Table
 		// PK column for City.id: id (default)
 		// PK column for Item: iId
 		// unidirectional
-		// legacy behavior would use the table name in the generated join column.
 		checkDefaultJoinTablAndJoinColumnNames(
 				City.class,
 				"stolenItems",
 				null,
 				"tbl_city_ITEM",
-				"tbl_city_id",
+				"City_id",
 				"stolenItems_iId"
 		);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
 	public void testUnidirOwnerEntityNamePrimaryTableOverride() {
 		// Category.clients: associated entity: KnownClient
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// KnownClient has @Entity with no name configured and no @Table
 		// PK column for Category.id: id (default)
 		// PK column for KnownClient.id: id (default)
 		// unidirectional
-		// legacy behavior would use the table name in the generated join column.
 		checkDefaultJoinTablAndJoinColumnNames(
 				Category.class,
 				"clients",
 				null,
 				"CATEGORY_TAB_KnownClient",
-				"CATEGORY_TAB_id",
+				"CATEGORY_id",
 				"clients_id"
 
 		);
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java
index 27a2eea330..cbe077610a 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java
@@ -1,50 +1,46 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.jpa.test;
 
 import org.hibernate.cfg.naming.HbmNamingStrategyDelegate;
+import org.hibernate.cfg.naming.ImprovedNamingStrategyDelegator;
 import org.hibernate.cfg.naming.JpaNamingStrategyDelegate;
 import org.hibernate.cfg.naming.NamingStrategyDelegate;
 import org.hibernate.cfg.naming.NamingStrategyDelegator;
 
 /**
  * @author Gail Badner
  */
-public class MyNamingStrategyDelegator implements NamingStrategyDelegator {
-	private final NamingStrategyDelegate hbmNamingStrategyDelegate = new HbmNamingStrategyDelegate();
-	private final NamingStrategyDelegate nonHbmNamingStrategyDelegate = new MyNonHbmNamingStrategyDelegate();
-
-	@Override
-	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
-		return isHbm ? hbmNamingStrategyDelegate :nonHbmNamingStrategyDelegate;
+public class MyNamingStrategyDelegator extends ImprovedNamingStrategyDelegator {
+	public MyNamingStrategyDelegator() {
+		super( new HbmNamingStrategyDelegate(), new MyNonHbmNamingStrategyDelegate() );
 	}
 
-	private class MyNonHbmNamingStrategyDelegate extends JpaNamingStrategyDelegate {
-
+	private static class MyNonHbmNamingStrategyDelegate extends JpaNamingStrategyDelegate {
 		@Override
 		public String toPhysicalColumnName(String columnName) {
 			return super.toPhysicalColumnName( "c_" + columnName );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java
index 8abfe53831..bfc3287628 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java
@@ -1,119 +1,119 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat, Inc.
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
 package org.hibernate.jpa.test.ejb3configuration;
 
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 import javax.persistence.PersistenceException;
 
 import org.junit.Test;
 
 import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
 import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.ejb.AvailableSettings;
+import org.hibernate.jpa.test.MyNamingStrategyDelegator;
 import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
 import org.hibernate.jpa.boot.spi.Bootstrap;
 import org.hibernate.jpa.test.MyNamingStrategy;
-import org.hibernate.jpa.test.MyNamingStrategyDelegator;
 import org.hibernate.jpa.test.PersistenceUnitInfoAdapter;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 
 /**
  * @author Gail Badner
  */
 public class NamingStrategyDelegatorConfigurationTest extends BaseUnitTestCase {
 
 	@Test
 	public void testNamingStrategyDelegatorFromProperty() {
 
 		// configure NamingStrategy
 		{
 			PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
 			EntityManagerFactoryBuilderImpl builder = (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(
 					adapter,
 					Collections.singletonMap( AvailableSettings.NAMING_STRATEGY, MyNamingStrategy.class.getName() )
 			);
 			assertEquals(
 					MyNamingStrategy.class.getName(),
 					builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY )
 			);
 			assertEquals( null, builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY_DELEGATOR ) );
 			builder.build();
 			final NamingStrategyDelegator namingStrategyDelegator =
 					builder.getHibernateConfiguration().getNamingStrategyDelegator();
 			assertTrue( LegacyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) );
 			assertTrue(
 					MyNamingStrategy.class.isInstance(
 							( (LegacyNamingStrategyDelegator)namingStrategyDelegator ).getNamingStrategy()
 					)
 			);
 		}
 
 		// configure NamingStrategyDelegator
 		{
 			PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
 			EntityManagerFactoryBuilderImpl builder = (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(
 					adapter,
 					Collections.singletonMap(
 							AvailableSettings.NAMING_STRATEGY_DELEGATOR,
 							MyNamingStrategyDelegator.class.getName()
 					)
 			);
 			assertEquals( null, builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY ) );
 			assertEquals(
 					MyNamingStrategyDelegator.class.getName(),
 					builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY_DELEGATOR )
 			);
 			builder.build();
 			final NamingStrategyDelegator namingStrategyDelegator =
 					builder.getHibernateConfiguration().getNamingStrategyDelegator();
 			assertTrue( MyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) );
 		}
 
 		// configure NamingStrategy and NamingStrategyDelegator
 		{
 			PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
 			final Map<String,String> integrationArgs = new HashMap<String,String>();
 			integrationArgs.put( AvailableSettings.NAMING_STRATEGY, MyNamingStrategy.class.getName() );
 			integrationArgs.put( AvailableSettings.NAMING_STRATEGY_DELEGATOR, MyNamingStrategyDelegator.class.getName() );
 			try {
 				EntityManagerFactoryBuilderImpl builder =  (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(
 						adapter,
 						integrationArgs
 				);
 				builder.build();
 				fail( "Should have thrown a PersistenceException because setting both properties is not allowed." );
 			}
 			catch (PersistenceException ex) {
 				// expected
 			}
 		}
 	}
 }
