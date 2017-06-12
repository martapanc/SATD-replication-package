diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index 66ec2aa3eb..9f1b1b2afa 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,2860 +1,2860 @@
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
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
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
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
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
-		final ServiceRegistry serviceRegistry =  new ServiceRegistryImpl( properties );
+		final ServiceRegistry serviceRegistry =  new BasicServiceRegistryImpl( properties );
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
-						( (ServiceRegistryImpl ) serviceRegistry ).destroy();
+						( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java
index 26b5c4f3bd..65dbc8676c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/batch/internal/BatchBuilderInitiator.java
@@ -1,69 +1,71 @@
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
 
+import java.util.Map;
+
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceException;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
-
-import java.util.Map;
 
 /**
+ * Initiator for the {@link BatchBuilder} service
+ *
  * @author Steve Ebersole
  */
-public class BatchBuilderInitiator implements ServiceInitiator<BatchBuilder> {
+public class BatchBuilderInitiator implements BasicServiceInitiator<BatchBuilder> {
 	public static final BatchBuilderInitiator INSTANCE = new BatchBuilderInitiator();
 	public static final String BUILDER = "hibernate.jdbc.batch.builder";
 
 	@Override
 	public Class<BatchBuilder> getServiceInitiated() {
 		return BatchBuilder.class;
 	}
 
 	@Override
-	public BatchBuilder initiateService(Map configurationValues, ServiceRegistry registry) {
+	public BatchBuilder initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesInitiator.java
index 60a4c14d51..65aa06a6b5 100644
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
+
 import java.util.Map;
+
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JdbcServices} service
  *
+ * @todo : should this maybe be a SessionFactory service?
+ *
  * @author Steve Ebersole
  */
-public class JdbcServicesInitiator implements ServiceInitiator<JdbcServices> {
+public class JdbcServicesInitiator implements BasicServiceInitiator<JdbcServices> {
 	public static final JdbcServicesInitiator INSTANCE = new JdbcServicesInitiator();
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<JdbcServices> getServiceInitiated() {
 		return JdbcServices.class;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
-	public JdbcServices initiateService(Map configValues, ServiceRegistry registry) {
+	@Override
+	public JdbcServices initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new JdbcServicesImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
index 8a51b0be09..ba6f0b1de3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcServices.java
@@ -1,95 +1,93 @@
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
+
 import java.sql.Connection;
 import java.sql.ResultSet;
 
 import org.hibernate.dialect.Dialect;
-import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.engine.jdbc.ResultSetWrapperProxy;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.Service;
 
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
-	 * Obtain infomration about supported behavior reported by the JDBC driver.
+	 * Obtain information about supported behavior reported by the JDBC driver.
 	 * <p/>
-	 * Yuck, yuck, yuck!  Much prefer this to be part of a "basic settings" type object.  See discussion
-	 * on {@link org.hibernate.cfg.internal.JdbcServicesBuilder}
+	 * Yuck, yuck, yuck!  Much prefer this to be part of a "basic settings" type object.
 	 * 
-	 * @return
+	 * @return The extracted database metadata, oddly enough :)
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
index 9ad5c8cff2..f1a32c06cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/TransactionFactoryInitiator.java
@@ -1,96 +1,100 @@
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
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
+import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
-import org.jboss.logging.Logger;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard instantiator for the standard {@link TransactionFactory} service.
  *
  * @author Steve Ebersole
  */
-public class TransactionFactoryInitiator implements ServiceInitiator<TransactionFactory> {
-
+public class TransactionFactoryInitiator<T extends TransactionImplementor> implements BasicServiceInitiator<TransactionFactory> {
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        TransactionFactoryInitiator.class.getName());
 
 	public static final TransactionFactoryInitiator INSTANCE = new TransactionFactoryInitiator();
 
 	@Override
+	@SuppressWarnings( {"unchecked"})
 	public Class<TransactionFactory> getServiceInitiated() {
 		return TransactionFactory.class;
 	}
 
 	@Override
-	public TransactionFactory initiateService(Map configVales, ServiceRegistry registry) {
-		final Object strategy = configVales.get( Environment.TRANSACTION_STRATEGY );
+	@SuppressWarnings( {"unchecked"})
+	public TransactionFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		final Object strategy = configurationValues.get( Environment.TRANSACTION_STRATEGY );
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcTransactionFactory.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcTransactionFactory.java
index 5dfe7ecb8f..7e787d104a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcTransactionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/internal/jdbc/JdbcTransactionFactory.java
@@ -1,62 +1,61 @@
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
 package org.hibernate.engine.transaction.internal.jdbc;
 
 import org.hibernate.ConnectionReleaseMode;
-import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 
 /**
  * Factory for {@link org.hibernate.engine.transaction.internal.jdbc.JdbcTransaction} instances.
  *
  * @author Anton van Straaten
  * @author Steve Ebersole
  */
 public final class JdbcTransactionFactory implements TransactionFactory<JdbcTransaction> {
 	@Override
 	public JdbcTransaction createTransaction(TransactionCoordinator transactionCoordinator) {
 		return new JdbcTransaction( transactionCoordinator );
 	}
 
 	@Override
 	public boolean canBeDriver() {
 		return true;
 	}
 
 	@Override
 	public ConnectionReleaseMode getDefaultReleaseMode() {
 		return ConnectionReleaseMode.ON_CLOSE;
 	}
 
 	@Override
 	public boolean compatibleWithJtaSynchronization() {
 		return false;
 	}
 
 	@Override
 	public boolean isJoinableJtaTransaction(TransactionCoordinator transactionCoordinator, JdbcTransaction transaction) {
 		return false;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
index 36f864420b..5e787168aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
@@ -1,194 +1,194 @@
 //$Id: HibernateService.java 6100 2005-03-17 10:48:03Z turin42 $
 package org.hibernate.jmx;
 
 import java.util.Map;
 import java.util.Properties;
 import javax.naming.InitialContext;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ExternalSessionFactoryConfig;
 import org.hibernate.internal.util.jndi.JndiHelper;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.jboss.logging.Logger;
 
 
 /**
  * Implementation of <tt>HibernateServiceMBean</tt>. Creates a
  * <tt>SessionFactory</tt> and binds it to the specified JNDI name.<br>
  * <br>
  * All mapping documents are loaded as resources by the MBean.
  * @see HibernateServiceMBean
  * @see org.hibernate.SessionFactory
  * @author John Urberg, Gavin King
  */
 public class HibernateService extends ExternalSessionFactoryConfig implements HibernateServiceMBean {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, HibernateService.class.getName());
 
 	private String boundName;
 	private Properties properties = new Properties();
 
 	@Override
 	public void start() throws HibernateException {
 		boundName = getJndiName();
 		try {
 			buildSessionFactory();
 		}
 		catch (HibernateException he) {
             LOG.unableToBuildSessionFactoryUsingMBeanClasspath(he.getMessage());
             LOG.debug("Error was", he);
 			new SessionFactoryStub(this);
 		}
 	}
 
 	@Override
 	public void stop() {
         LOG.stoppingService();
 		try {
 			InitialContext context = JndiHelper.getInitialContext( buildProperties() );
 			( (SessionFactory) context.lookup(boundName) ).close();
 			//context.unbind(boundName);
 		}
 		catch (Exception e) {
             LOG.unableToStopHibernateService(e);
 		}
 	}
 
 	SessionFactory buildSessionFactory() throws HibernateException {
         LOG.startingServiceAtJndiName(boundName);
         LOG.serviceProperties(properties);
-        return buildConfiguration().buildSessionFactory(new ServiceRegistryImpl(properties));
+        return buildConfiguration().buildSessionFactory(new BasicServiceRegistryImpl(properties));
 	}
 
 	@Override
 	protected Map getExtraProperties() {
 		return properties;
 	}
 
 	@Override
 	public String getTransactionStrategy() {
 		return getProperty(Environment.TRANSACTION_STRATEGY);
 	}
 
 	@Override
 	public void setTransactionStrategy(String txnStrategy) {
 		setProperty(Environment.TRANSACTION_STRATEGY, txnStrategy);
 	}
 
 	@Override
 	public String getUserTransactionName() {
 		return getProperty(Environment.USER_TRANSACTION);
 	}
 
 	@Override
 	public void setUserTransactionName(String utName) {
 		setProperty(Environment.USER_TRANSACTION, utName);
 	}
 
 	@Override
 	public String getJtaPlatformName() {
 		return getProperty( JtaPlatformInitiator.JTA_PLATFORM );
 	}
 
 	@Override
 	public void setJtaPlatformName(String name) {
 		setProperty( JtaPlatformInitiator.JTA_PLATFORM, name );
 	}
 
 	@Override
 	public String getPropertyList() {
 		return buildProperties().toString();
 	}
 
 	@Override
 	public String getProperty(String property) {
 		return properties.getProperty(property);
 	}
 
 	@Override
 	public void setProperty(String property, String value) {
 		properties.setProperty(property, value);
 	}
 
 	@Override
 	public void dropSchema() {
 		new SchemaExport( buildConfiguration() ).drop(false, true);
 	}
 
 	@Override
 	public void createSchema() {
 		new SchemaExport( buildConfiguration() ).create(false, true);
 	}
 
 	public String getName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public String getDatasource() {
 		return getProperty(Environment.DATASOURCE);
 	}
 
 	@Override
 	public void setDatasource(String datasource) {
 		setProperty(Environment.DATASOURCE, datasource);
 	}
 
 	@Override
 	public String getJndiName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public void setJndiName(String jndiName) {
 		setProperty(Environment.SESSION_FACTORY_NAME, jndiName);
 	}
 
 	@Override
 	public String getUserName() {
 		return getProperty(Environment.USER);
 	}
 
 	@Override
 	public void setUserName(String userName) {
 		setProperty(Environment.USER, userName);
 	}
 
 	@Override
 	public String getPassword() {
 		return getProperty(Environment.PASS);
 	}
 
 	@Override
 	public void setPassword(String password) {
 		setProperty(Environment.PASS, password);
 	}
 
 	@Override
 	public void setFlushBeforeCompletionEnabled(String enabled) {
 		setProperty(Environment.FLUSH_BEFORE_COMPLETION, enabled);
 	}
 
 	@Override
 	public String getFlushBeforeCompletionEnabled() {
 		return getProperty(Environment.FLUSH_BEFORE_COMPLETION);
 	}
 
 	@Override
 	public void setAutoCloseSessionEnabled(String enabled) {
 		setProperty(Environment.AUTO_CLOSE_SESSION, enabled);
 	}
 
 	@Override
 	public String getAutoCloseSessionEnabled() {
 		return getProperty(Environment.AUTO_CLOSE_SESSION);
 	}
 
 	public Properties getProperties() {
 		return buildProperties();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterClassResolverInitiator.java
index f61b48fd6a..dd710d3bcd 100644
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
 
+import java.util.Map;
+
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceException;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
-
-import java.util.Map;
 
 /**
  * @author Steve Ebersole
  */
-public class PersisterClassResolverInitiator implements ServiceInitiator<PersisterClassResolver> {
+public class PersisterClassResolverInitiator implements BasicServiceInitiator<PersisterClassResolver> {
 	public static final PersisterClassResolverInitiator INSTANCE = new PersisterClassResolverInitiator();
 	public static final String IMPL_NAME = "hibernate.persister.resolver";
 
 	@Override
 	public Class<PersisterClassResolver> getServiceInitiated() {
 		return PersisterClassResolver.class;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
-	public PersisterClassResolver initiateService(Map configurationValues, ServiceRegistry registry) {
+	public PersisterClassResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryInitiator.java
index 73df183a37..68cc54643f 100644
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
 
+import java.util.Map;
+
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceException;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
-
-import java.util.Map;
 
 /**
  * @author Steve Ebersole
  */
-public class PersisterFactoryInitiator implements ServiceInitiator<PersisterFactory> {
+public class PersisterFactoryInitiator implements BasicServiceInitiator<PersisterFactory> {
 	public static final PersisterFactoryInitiator INSTANCE = new PersisterFactoryInitiator();
 
 	public static final String IMPL_NAME = "hibernate.persister.factory";
 
 	@Override
 	public Class<PersisterFactory> getServiceInitiated() {
 		return PersisterFactory.class;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
-	public PersisterFactory initiateService(Map configurationValues, ServiceRegistry registry) {
+	public PersisterFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
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
index 1137d12333..7bfd7e7aa9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterClassResolver.java
@@ -1,54 +1,62 @@
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
 import org.hibernate.service.spi.Service;
 
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
+	 *
+	 * @param metadata The entity metadata
+	 *
+	 * @return The entity persister class to use
 	 */
 	Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata);
 
 	/**
 	 * Returns the collection persister class for a given collection role or null
 	 * if the collection persister class should be the default.
+	 *
+	 * @param metadata The collection metadata
+	 *
+	 * @return The collection persister class to use
 	 */
 	Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index 1108fa1752..e71e41d349 100644
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
 import org.hibernate.service.spi.Service;
 
 /**
- * Contract for creating persister instances (both {@link EntityPersister} and {@link } varieties).
+ * Contract for creating persister instances (both {@link EntityPersister} and {@link CollectionPersister} varieties).
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
index 9fcbb4fd28..ebdb52b4aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/internal/ClassLoaderServiceInitiator.java
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
 package org.hibernate.service.classloading.internal;
+
 import java.util.Map;
-import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
 
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
- * Standard initiator for the stanndard {@link ClassLoaderService} service.
+ * Standard initiator for the standard {@link ClassLoaderService} service.
  *
  * @author Steve Ebersole
  */
-public class ClassLoaderServiceInitiator implements ServiceInitiator<ClassLoaderService> {
+public class ClassLoaderServiceInitiator implements BasicServiceInitiator<ClassLoaderService> {
 	public static final ClassLoaderServiceInitiator INSTANCE = new ClassLoaderServiceInitiator();
 
 	@Override
 	public Class<ClassLoaderService> getServiceInitiated() {
 		return ClassLoaderService.class;
 	}
 
 	@Override
-	public ClassLoaderService initiateService(Map configurationValues, ServiceRegistry registry) {
+	public ClassLoaderService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new ClassLoaderServiceImpl( configurationValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
index ebf939c79a..b10175e9a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/classloading/spi/ClassLoaderService.java
@@ -1,73 +1,75 @@
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
+
 import java.io.InputStream;
 import java.net.URL;
 import java.util.List;
+
 import org.hibernate.service.spi.Service;
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
similarity index 55%
rename from hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
index 494de1654c..a2c92818ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/AbstractServiceRegistryImpl.java
@@ -1,169 +1,160 @@
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
 
 import java.util.ArrayList;
 import java.util.List;
 import java.util.ListIterator;
-import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.internal.util.collections.CollectionHelper;
-import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.internal.proxy.javassist.ServiceProxyFactoryFactoryImpl;
 import org.hibernate.service.spi.Service;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.StandardServiceInitiators;
+import org.hibernate.service.spi.ServiceRegistry;
 import org.hibernate.service.spi.Stoppable;
 import org.hibernate.service.spi.UnknownServiceException;
 import org.hibernate.service.spi.proxy.ServiceProxyFactory;
-import org.hibernate.service.spi.proxy.ServiceProxyTargetSource;
-import org.jboss.logging.Logger;
 
 /**
- * Standard Hibernate implementation of the service registry.
- *
  * @author Steve Ebersole
  */
-public class ServiceRegistryImpl implements ServiceProxyTargetSource {
+public abstract class AbstractServiceRegistryImpl implements ServiceRegistryImplementor {
+	private static final HibernateLogger LOG = Logger.getMessageLogger( HibernateLogger.class, AbstractServiceRegistryImpl.class.getName() );
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ServiceRegistryImpl.class.getName());
+	private final ServiceRegistryImplementor parent;
 
-	private final ServiceInitializer initializer;
-	// for now just hardcode the javassist factory
+	// for now just hard-code the javassist factory
 	private ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactoryFactoryImpl().makeServiceProxyFactory( this );
 
 	private ConcurrentHashMap<Class,ServiceBinding> serviceBindingMap;
-	// IMPL NOTE : the list used for ordered destruction.  Cannot used ordered map above because we need to
+	// IMPL NOTE : the list used for ordered destruction.  Cannot used map above because we need to
 	// iterate it in reverse order which is only available through ListIterator
 	private List<Service> serviceList = new ArrayList<Service>();
 
-	public ServiceRegistryImpl(Map configurationValues) {
-		this( StandardServiceInitiators.LIST, configurationValues );
+	protected AbstractServiceRegistryImpl() {
+		this( null );
 	}
 
-	public ServiceRegistryImpl(List<ServiceInitiator> serviceInitiators, Map configurationValues) {
-		this.initializer = new ServiceInitializer( this, serviceInitiators, ConfigurationHelper.clone( configurationValues ) );
-		final int anticipatedSize = serviceInitiators.size() + 5; // allow some growth
-		serviceBindingMap = CollectionHelper.concurrentMap( anticipatedSize );
-		serviceList = CollectionHelper.arrayList( anticipatedSize );
+	protected AbstractServiceRegistryImpl(ServiceRegistryImplementor parent) {
+		this.parent = parent;
+		// assume 20 services for initial sizing
+		this.serviceBindingMap = CollectionHelper.concurrentMap( 20 );
+		this.serviceList = CollectionHelper.arrayList( 20 );
 	}
 
-	public void destroy() {
-		ListIterator<Service> serviceIterator = serviceList.listIterator( serviceList.size() );
-		while ( serviceIterator.hasPrevious() ) {
-			final Service service = serviceIterator.previous();
-			if ( Stoppable.class.isInstance( service ) ) {
-				try {
-					( (Stoppable) service ).stop();
-				}
-				catch ( Exception e ) {
-                    LOG.unableToStopService(service.getClass(), e.toString());
-				}
-			}
-		}
-		serviceList.clear();
-		serviceList = null;
-		serviceBindingMap.clear();
-		serviceBindingMap = null;
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public ServiceRegistry getParentServiceRegistry() {
+		return parent;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
-	public <T extends Service> T getService(Class<T> serviceRole) {
-		return locateOrCreateServiceBinding( serviceRole ).getProxy();
+	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
+		return locateServiceBinding( serviceRole, true );
 	}
 
 	@SuppressWarnings({ "unchecked" })
-	private <T extends Service> ServiceBinding<T> locateOrCreateServiceBinding(Class<T> serviceRole) {
-		ServiceBinding<T> serviceBinding = serviceBindingMap.get( serviceRole );
-		if ( serviceBinding == null ) {
-			T proxy = serviceProxyFactory.makeProxy( serviceRole );
-			serviceBinding = new ServiceBinding<T>( proxy );
-			serviceBindingMap.put( serviceRole, serviceBinding );
+	protected <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole, boolean checkParent) {
+		ServiceBinding<R> serviceBinding = serviceBindingMap.get( serviceRole );
+		if ( serviceBinding == null && checkParent && parent != null ) {
+			// look in parent
+			serviceBinding = parent.locateServiceBinding( serviceRole );
 		}
 		return serviceBinding;
 	}
 
 	@Override
-	@SuppressWarnings( {"unchecked"})
-	public <T extends Service> T getServiceInternal(Class<T> serviceRole) {
-		ServiceBinding<T> serviceBinding = serviceBindingMap.get( serviceRole );
+	public <R extends Service> R getService(Class<R> serviceRole) {
+		return locateOrCreateServiceBinding( serviceRole, true ).getProxy();
+	}
+
+	@SuppressWarnings({ "unchecked" })
+	protected <R extends Service> ServiceBinding<R> locateOrCreateServiceBinding(Class<R> serviceRole, boolean checkParent) {
+		ServiceBinding<R> serviceBinding = locateServiceBinding( serviceRole, checkParent );
 		if ( serviceBinding == null ) {
-			throw new HibernateException( "Only proxies should invoke #getServiceInternal" );
-		}
-		T service = serviceBinding.getTarget();
-		if ( service == null ) {
-			service = initializer.initializeService( serviceRole );
-			serviceBinding.setTarget( service );
-		}
-		if ( service == null ) {
-			throw new UnknownServiceException( serviceRole );
+			R proxy = serviceProxyFactory.makeProxy( serviceRole );
+			serviceBinding = new ServiceBinding<R>( proxy );
+			serviceBindingMap.put( serviceRole, serviceBinding );
 		}
-		return service;
+		return serviceBinding;
 	}
 
 	@Override
-	public <T extends Service> void registerService(Class<T> serviceRole, T service) {
-		ServiceBinding<T> serviceBinding = locateOrCreateServiceBinding( serviceRole );
-		T priorServiceInstance = serviceBinding.getTarget();
+	public <R extends Service> void registerService(Class<R> serviceRole, R service) {
+		ServiceBinding<R> serviceBinding = locateOrCreateServiceBinding( serviceRole, false );
+		R priorServiceInstance = serviceBinding.getTarget();
 		serviceBinding.setTarget( service );
 		if ( priorServiceInstance != null ) {
 			serviceList.remove( priorServiceInstance );
 		}
 		serviceList.add( service );
 	}
 
+	protected abstract <R extends Service> R initializeService(Class<R> serviceRole);
+
 	@Override
 	@SuppressWarnings( {"unchecked"})
-	public void registerServiceInitiator(ServiceInitiator initiator) {
-		ServiceBinding serviceBinding = serviceBindingMap.get( initiator.getServiceInitiated() );
-		if ( serviceBinding != null ) {
-			serviceBinding.setTarget( null );
-		}
-		initializer.registerServiceInitiator( initiator );
-	}
-
-	private static final class ServiceBinding<T> {
-		private final T proxy;
-		private T target;
-
-		private ServiceBinding(T proxy) {
-			this.proxy = proxy;
+	public <R extends Service> R getServiceInternal(Class<R> serviceRole) {
+		// this call comes from the binding proxy, we most definitely do not want to look up into the parent
+		// in this case!
+		ServiceBinding<R> serviceBinding = locateServiceBinding( serviceRole, false );
+		if ( serviceBinding == null ) {
+			throw new HibernateException( "Only proxies should invoke #getServiceInternal" );
 		}
-
-		public T getProxy() {
-			return proxy;
+		R service = serviceBinding.getTarget();
+		if ( service == null ) {
+			service = initializeService( serviceRole );
+			serviceBinding.setTarget( service );
 		}
-
-		public T getTarget() {
-			return target;
+		if ( service == null ) {
+			throw new UnknownServiceException( serviceRole );
 		}
+		return service;
+	}
 
-		public void setTarget(T target) {
-			this.target = target;
+	public void destroy() {
+		ListIterator<Service> serviceIterator = serviceList.listIterator( serviceList.size() );
+		while ( serviceIterator.hasPrevious() ) {
+			final Service service = serviceIterator.previous();
+			if ( Stoppable.class.isInstance( service ) ) {
+				try {
+					( (Stoppable) service ).stop();
+				}
+				catch ( Exception e ) {
+                    LOG.unableToStopService(service.getClass(), e.toString());
+				}
+			}
 		}
+		serviceList.clear();
+		serviceList = null;
+		serviceBindingMap.clear();
+		serviceBindingMap = null;
 	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceInitializer.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
similarity index 73%
rename from hibernate-core/src/main/java/org/hibernate/service/internal/ServiceInitializer.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
index a3d0f4700c..cb4661a578 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
@@ -1,208 +1,208 @@
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
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateLogger;
 import org.hibernate.service.jmx.spi.JmxService;
+import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.InjectService;
 import org.hibernate.service.spi.Manageable;
 import org.hibernate.service.spi.Service;
 import org.hibernate.service.spi.ServiceException;
-import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
+import org.hibernate.service.spi.StandardServiceInitiators;
 import org.hibernate.service.spi.Startable;
 import org.hibernate.service.spi.UnknownServiceException;
-import org.jboss.logging.Logger;
 
 /**
- * Delegate responsible for initializing services
+ * Standard Hibernate implementation of the service registry.
  *
  * @author Steve Ebersole
  */
-public class ServiceInitializer {
+public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl {
+    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, BasicServiceRegistryImpl.class.getName());
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, ServiceInitializer.class.getName());
-
-	private final ServiceRegistryImpl servicesRegistry;
-	private final Map<Class,ServiceInitiator> serviceInitiatorMap;
+	private final Map<Class,BasicServiceInitiator> serviceInitiatorMap;
 	private final Map configurationValues;
 
-	public ServiceInitializer(
-			ServiceRegistryImpl servicesRegistry,
-			List<ServiceInitiator> serviceInitiators,
-			Map configurationValues) {
-		this.servicesRegistry = servicesRegistry;
+	public BasicServiceRegistryImpl(Map configurationValues) {
+		this( StandardServiceInitiators.LIST, configurationValues );
+	}
+
+	public BasicServiceRegistryImpl(List<BasicServiceInitiator> serviceInitiators, Map configurationValues) {
+		super();
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
-	private static Map<Class, ServiceInitiator> toMap(List<ServiceInitiator> serviceInitiators) {
-		final Map<Class, ServiceInitiator> result = new HashMap<Class, ServiceInitiator>();
-		for ( ServiceInitiator initiator : serviceInitiators ) {
+	private static Map<Class, BasicServiceInitiator> toMap(List<BasicServiceInitiator> serviceInitiators) {
+		final Map<Class, BasicServiceInitiator> result = new HashMap<Class, BasicServiceInitiator>();
+		for ( BasicServiceInitiator initiator : serviceInitiators ) {
 			result.put( initiator.getServiceInitiated(), initiator );
 		}
 		return result;
 	}
 
-	void registerServiceInitiator(ServiceInitiator serviceInitiator) {
-		final Object previous = serviceInitiatorMap.put( serviceInitiator.getServiceInitiated(), serviceInitiator );
-		final boolean overwritten = previous != null;
-        if (overwritten) LOG.debugf("Over-wrote existing service initiator [role=%s]",
-                                    serviceInitiator.getServiceInitiated().getName());
+	@SuppressWarnings( {"unchecked"})
+	public void registerServiceInitiator(BasicServiceInitiator initiator) {
+		ServiceBinding serviceBinding = locateServiceBinding( initiator.getServiceInitiated(), false );
+		if ( serviceBinding != null ) {
+			serviceBinding.setTarget( null );
+		}
+		final Object previous = serviceInitiatorMap.put( initiator.getServiceInitiated(), initiator );
+		if ( previous != null ) {
+			LOG.debugf( "Over-wrote existing service initiator [role=%s]", initiator.getServiceInitiated().getName() );
+		}
 	}
 
-	/**
-	 * The main function of this delegate.  Used to initialize the service of a given role.
-	 *
-	 * @param serviceRole The service role
-	 * @param <T> The type of service role
-	 *
-	 * @return The intiialized instance of the service
-	 */
-	public <T extends Service> T initializeService(Class<T> serviceRole) {
+	@Override
+	protected <R extends Service> R initializeService(Class<R> serviceRole) {
         LOG.trace("Initializing service [role=" + serviceRole.getName() + "]");
 
 		// PHASE 1 : create service
-		T service = createService( serviceRole );
+		R service = createService( serviceRole );
 		if ( service == null ) {
 			return null;
 		}
 
 		// PHASE 2 : configure service (***potentially recursive***)
 		configureService( service );
 
 		// PHASE 3 : Start service
 		startService( service, serviceRole );
 
 		return service;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private <T extends Service> T createService(Class<T> serviceRole) {
-		ServiceInitiator<T> initiator = serviceInitiatorMap.get( serviceRole );
+		BasicServiceInitiator<T> initiator = serviceInitiatorMap.get( serviceRole );
 		if ( initiator == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 		try {
-			T service = initiator.initiateService( configurationValues, servicesRegistry );
+			T service = initiator.initiateService( configurationValues, this );
 			// IMPL NOTE : the register call here is important to avoid potential stack overflow issues
 			//		from recursive calls through #configureService
-			servicesRegistry.registerService( serviceRole, service );
+			registerService( serviceRole, service );
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
 
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
-			( (ServiceRegistryAwareService) service ).injectServices( servicesRegistry );
+			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
 	}
 
 	private <T extends Service> void applyInjections(T service) {
 		try {
 			for ( Method method : service.getClass().getMethods() ) {
 				InjectService injectService = method.getAnnotation( InjectService.class );
 				if ( injectService == null ) {
 					continue;
 				}
 
 				applyInjection( service, method, injectService );
 			}
 		}
 		catch (NullPointerException e) {
             LOG.error("NPE injecting service deps : " + service.getClass().getName());
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
 
 		// todo : because of the use of proxies, this is no longer returning null here...
 
-		final Service dependantService = servicesRegistry.getService( dependentServiceRole );
+		final Service dependantService = getService( dependentServiceRole );
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
-			servicesRegistry.getService( JmxService.class ).registerService( (Manageable) service, serviceRole );
+			getService( JmxService.class ).registerService( (Manageable) service, serviceRole );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceBinding.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceBinding.java
new file mode 100644
index 0000000000..0289c4fec2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceBinding.java
@@ -0,0 +1,48 @@
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
+/**
+ * @author Steve Ebersole
+ */
+final class ServiceBinding<R> {
+	private final R proxy;
+	private R target;
+
+	ServiceBinding(R proxy) {
+		this.proxy = proxy;
+	}
+
+	public R getProxy() {
+		return proxy;
+	}
+
+	public R getTarget() {
+		return target;
+	}
+
+	public void setTarget(R target) {
+		this.target = target;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImplementor.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImplementor.java
new file mode 100644
index 0000000000..27b5cbdf8b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ServiceRegistryImplementor.java
@@ -0,0 +1,35 @@
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
+import org.hibernate.service.spi.Service;
+import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.proxy.ServiceProxyTargetSource;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ServiceRegistryImplementor extends ServiceRegistry, ServiceProxyTargetSource {
+	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
index 17bc42a44b..0fd0a267d3 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/internal/ConnectionProviderInitiator.java
@@ -1,277 +1,280 @@
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
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
-import org.jboss.logging.Logger;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Instantiates and configures an appropriate {@link ConnectionProvider}.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
-public class ConnectionProviderInitiator implements ServiceInitiator<ConnectionProvider> {
+public class ConnectionProviderInitiator implements BasicServiceInitiator<ConnectionProvider> {
 	public static final ConnectionProviderInitiator INSTANCE = new ConnectionProviderInitiator();
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        ConnectionProviderInitiator.class.getName());
-
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<ConnectionProvider> getServiceInitiated() {
 		return ConnectionProvider.class;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
-	public ConnectionProvider initiateService(Map configurationValues, ServiceRegistry registry) {
+	@Override
+	public ConnectionProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
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
-	 * Transform JDBC connection properties.
+	 * Build the connection properties capable of being passed to the {@link java.sql.DriverManager#getConnection}
+	 * forms taking {@link Properties} argument.  We seek out all keys in the passed map which start with
+	 * {@code hibernate.connection.}, using them to create a new {@link Properties} instance.  The keys in this
+	 * new {@link Properties} have the {@code hibernate.connection.} prefix trimmed.
+	 *
+	 * @param properties The map from which to build the connection specific properties.
 	 *
-	 * Passed in the form <tt>hibernate.connection.*</tt> to the
-	 * format accepted by <tt>DriverManager</tt> by trimming the leading "<tt>hibernate.connection</tt>".
+	 * @return The connection properties.
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
-					final String passThruKey = key.substring( Environment.CONNECTION_PREFIX.length() + 1 );
-					result.setProperty( passThruKey, value );
+					result.setProperty(
+							key.substring( Environment.CONNECTION_PREFIX.length() + 1 ),
+							value
+					);
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java
index 788a81ca33..8a67c92d2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/connections/spi/ConnectionProvider.java
@@ -1,77 +1,79 @@
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
 import org.hibernate.service.spi.Service;
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
-	 * @throws HibernateException Inidcates a problem otherwise obtaining a connnection.
+	 * @throws HibernateException Indicates a problem otherwise obtaining a connection.
 	 */
 	public Connection getConnection() throws SQLException;
 
 	/**
 	 * Release a connection from Hibernate use.
 	 *
 	 * @param conn The JDBC connection to release
 	 *
 	 * @throws SQLException Indicates a problem closing the connection
-	 * @throws HibernateException Inidcates a problem otherwise releasing a connnection.
+	 * @throws HibernateException Indicates a problem otherwise releasing a connection.
 	 */
 	public void closeConnection(Connection conn) throws SQLException;
 
 	/**
 	 * Does this connection provider support aggressive release of JDBC
-	 * connections and re-acquistion of those connections (if need be) later?
+	 * connections and re-acquisition of those connections (if need be) later?
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
+	 *
+	 * @return {@code true} if aggressive releasing is supported; {@code false} otherwise.
 	 */
 	public boolean supportsAggressiveRelease();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
index 83f5aaa630..d6f77a0103 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectFactoryInitiator.java
@@ -1,51 +1,49 @@
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
+
 import java.util.Map;
+
+import org.hibernate.service.internal.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.dialect.spi.DialectFactory;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link DialectFactory} service
  *
  * @author Steve Ebersole
  */
-public class DialectFactoryInitiator implements ServiceInitiator<DialectFactory> {
+public class DialectFactoryInitiator implements BasicServiceInitiator<DialectFactory> {
 	public static final DialectFactoryInitiator INSTANCE = new DialectFactoryInitiator();
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<DialectFactory> getServiceInitiated() {
 		return DialectFactory.class;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
-	public DialectFactory initiateService(Map configVales, ServiceRegistry registry) {
+	@Override
+	public DialectFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new DialectFactoryImpl();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
index a1d8868729..21ee769af0 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/internal/DialectResolverInitiator.java
@@ -1,51 +1,49 @@
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
+
 import java.util.Map;
+
+import org.hibernate.service.internal.ServiceRegistryImplementor;
 import org.hibernate.service.jdbc.dialect.spi.DialectResolver;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link DialectResolver} service
  *
  * @author Steve Ebersole
  */
-public class DialectResolverInitiator implements ServiceInitiator<DialectResolver>  {
+public class DialectResolverInitiator implements BasicServiceInitiator<DialectResolver> {
 	public static final DialectResolverInitiator INSTANCE = new DialectResolverInitiator();
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Class<DialectResolver> getServiceInitiated() {
 		return DialectResolver.class;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
-	public DialectResolver initiateService(Map configVales, ServiceRegistry registry) {
+	@Override
+	public DialectResolver initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new StandardDialectResolver();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java
index 7fc3d0867c..f6e048c5d5 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectFactory.java
@@ -1,54 +1,56 @@
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
+
 import java.sql.Connection;
 import java.util.Map;
+
 import org.hibernate.HibernateException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.service.spi.Service;
 
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
index efd355b2b1..32bb10ea08 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jdbc/dialect/spi/DialectResolver.java
@@ -1,51 +1,52 @@
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
+
 import java.sql.DatabaseMetaData;
+
 import org.hibernate.dialect.Dialect;
 import org.hibernate.exception.JDBCConnectionException;
 import org.hibernate.service.spi.Service;
 
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
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
index b4472c8317..237761663d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/internal/JmxServiceInitiator.java
@@ -1,51 +1,53 @@
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
+
 import java.util.Map;
+
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
 import org.hibernate.service.jmx.spi.JmxService;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JmxService} service
  *
  * @author Steve Ebersole
  */
-public class JmxServiceInitiator implements ServiceInitiator<JmxService> {
+public class JmxServiceInitiator implements BasicServiceInitiator<JmxService> {
 	public static final String JMX_ENABLED = "hibernate.jmx.enabled";
 	public static final JmxServiceInitiator INSTANCE = new JmxServiceInitiator();
 
 	@Override
 	public Class<JmxService> getServiceInitiated() {
 		return JmxService.class;
 	}
 
 	@Override
-	public JmxService initiateService(Map configValues, ServiceRegistry registry) {
-		return ConfigurationHelper.getBoolean( JMX_ENABLED, configValues, false )
-				? new JmxServiceImpl( configValues )
+	public JmxService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		return ConfigurationHelper.getBoolean( JMX_ENABLED, configurationValues, false )
+				? new JmxServiceImpl( configurationValues )
 				: DisabledJmxServiceImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java b/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
index 16c6e5122e..6fcc1386b2 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jmx/spi/JmxService.java
@@ -1,50 +1,52 @@
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
+
 import javax.management.ObjectName;
+
 import org.hibernate.service.spi.Manageable;
 import org.hibernate.service.spi.Service;
 
 /**
  * Service providing simplified access to JMX related features needed by Hibernate.
  *
  * @author Steve Ebersole
  */
 public interface JmxService extends Service {
 	/**
-	 * Handles regsitration of a manageable service.
+	 * Handles registration of a manageable service.
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
index 732a1845eb..ece7f2e05b 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jndi/internal/JndiServiceInitiator.java
@@ -1,47 +1,49 @@
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
+
 import java.util.Map;
+
+import org.hibernate.service.internal.ServiceRegistryImplementor;
 import org.hibernate.service.jndi.spi.JndiService;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.BasicServiceInitiator;
 
 /**
  * Standard initiator for the standard {@link JndiService} service
  *
  * @author Steve Ebersole
  */
-public class JndiServiceInitiator implements ServiceInitiator<JndiService> {
+public class JndiServiceInitiator implements BasicServiceInitiator<JndiService> {
 	public static final JndiServiceInitiator INSTANCE = new JndiServiceInitiator();
 
 	@Override
 	public Class<JndiService> getServiceInitiated() {
 		return JndiService.class;
 	}
 
 	@Override
-	public JndiService initiateService(Map configurationValues, ServiceRegistry registry) {
+	public JndiService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
 		return new JndiServiceImpl( configurationValues );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java b/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
index d08f2fbff7..7d16f1d4ff 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jndi/spi/JndiService.java
@@ -1,49 +1,50 @@
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
+
 import org.hibernate.service.spi.Service;
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/internal/JtaPlatformInitiator.java
index ed33e35445..a6320d272c 100644
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
+
 import java.util.Map;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.jndi.JndiHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.internal.ServiceRegistryImplementor;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.jta.platform.spi.JtaPlatformException;
-import org.hibernate.service.spi.ServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistry;
+import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.transaction.TransactionManagerLookup;
-import org.jboss.logging.Logger;
 
 /**
  * Standard initiator for the standard {@link org.hibernate.service.jta.platform.spi.JtaPlatform}
  *
  * @author Steve Ebersole
  */
-public class JtaPlatformInitiator implements ServiceInitiator<JtaPlatform> {
+public class JtaPlatformInitiator implements BasicServiceInitiator<JtaPlatform> {
 	public static final JtaPlatformInitiator INSTANCE = new JtaPlatformInitiator();
 	public static final String JTA_PLATFORM = "hibernate.jta.platform";
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, JtaPlatformInitiator.class.getName());
 
 	@Override
 	public Class<JtaPlatform> getServiceInitiated() {
 		return JtaPlatform.class;
 	}
 
-	@SuppressWarnings( {"unchecked"})
 	@Override
-	public JtaPlatform initiateService(Map configVales, ServiceRegistry registry) {
-		final Object platform = getConfiguredPlatform( configVales, registry );
+	@SuppressWarnings( {"unchecked"})
+	public JtaPlatform initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		final Object platform = getConfiguredPlatform( configurationValues, registry );
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
 
-	private Object getConfiguredPlatform(Map configVales, ServiceRegistry registry) {
+	private Object getConfiguredPlatform(Map configVales, ServiceRegistryImplementor registry) {
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
 
-	private JtaPlatform mapLegacyClasses(
-			String transactionManagerLookupImplName,
-			Map configVales,
-			ServiceRegistry registry) {
-		if ( transactionManagerLookupImplName == null ) {
+	private JtaPlatform mapLegacyClasses(String tmlImplName, Map configVales, ServiceRegistryImplementor registry) {
+		if ( tmlImplName == null ) {
 			return null;
 		}
 
         LOG.legacyTransactionManagerStrategy(JtaPlatform.class.getName(), JTA_PLATFORM);
 
-		if ( "org.hibernate.transaction.BESTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.BESTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new BorlandEnterpriseServerJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.BTMTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.BTMTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new BitronixJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.JBossTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.JBossTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JBossAppServerPlatform();
 		}
 
-		if ( "org.hibernate.transaction.JBossTSStandaloneTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.JBossTSStandaloneTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JBossStandAloneJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.JOnASTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.JOnASTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JOnASJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.JOTMTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.JOTMTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JOTMJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.JRun4TransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.JRun4TransactionManagerLookup".equals( tmlImplName ) ) {
 			return new JRun4JtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.OC4JTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.OC4JTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new OC4JJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.OrionTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.OrionTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new OrionJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.ResinTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.ResinTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new ResinJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.SunONETransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.SunONETransactionManagerLookup".equals( tmlImplName ) ) {
 			return new SunOneJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.WeblogicTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.WeblogicTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new WeblogicJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.WebSphereTransactionManagerLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.WebSphereTransactionManagerLookup".equals( tmlImplName ) ) {
 			return new WebSphereJtaPlatform();
 		}
 
-		if ( "org.hibernate.transaction.WebSphereExtendedJTATransactionLookup".equals( transactionManagerLookupImplName ) ) {
+		if ( "org.hibernate.transaction.WebSphereExtendedJTATransactionLookup".equals( tmlImplName ) ) {
 			return new WebSphereExtendedJtaPlatform();
 		}
 
 		try {
 			TransactionManagerLookup lookup = (TransactionManagerLookup) registry.getService( ClassLoaderService.class )
-					.classForName( transactionManagerLookupImplName )
+					.classForName( tmlImplName )
 					.newInstance();
 			return new TransactionManagerLookupBridge( lookup, JndiHelper.extractJndiProperties( configVales ) );
 		}
 		catch ( Exception e ) {
 			throw new JtaPlatformException(
 					"Unable to build " + TransactionManagerLookupBridge.class.getName() + " from specified " +
 							TransactionManagerLookup.class.getName() + " implementation: " +
-							transactionManagerLookupImplName
+							tmlImplName
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
index 17654aa393..4f81875f18 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/jta/platform/spi/JtaPlatform.java
@@ -1,97 +1,99 @@
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
+
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 import javax.transaction.UserTransaction;
+
 import org.hibernate.service.spi.Service;
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
index 78797c675e..706bb0e0b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/BasicServiceInitiator.java
@@ -1,49 +1,52 @@
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
+
 import java.util.Map;
 
+import org.hibernate.service.internal.ServiceRegistryImplementor;
+
 /**
  * Responsible for initiating services.
  *
  * @author Steve Ebersole
  */
-public interface ServiceInitiator<T extends Service> {
+public interface BasicServiceInitiator<R extends Service> {
 	/**
-	 * Obtains the service role initiated by this initiator.  Should be uniqie withion a registry
+	 * Obtains the service role initiated by this initiator.  Should be unique within a registry
 	 *
 	 * @return The service role.
 	 */
-	public Class<T> getServiceInitiated();
+	public Class<R> getServiceInitiated();
 
 	/**
 	 * Initiates the managed service.
 	 *
 	 * @param configurationValues The configuration values in effect
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
-	public T initiateService(Map configurationValues, ServiceRegistry registry);
+	public R initiateService(Map configurationValues, ServiceRegistryImplementor registry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
index dabd842fbe..6672ebf719 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistry.java
@@ -1,59 +1,62 @@
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
- * The registry of services used by Hibernate
+ * The registry of {@link Service services}.
  *
  * @author Steve Ebersole
  */
 public interface ServiceRegistry {
 	/**
-	 * Retrieve a service by role.  If service is not found, but a {@link ServiceInitiator} is registered for
+	 * Retrieve this registry's parent registry.
+	 * 
+	 * @return The parent registry.  May be null.
+	 */
+	public ServiceRegistry getParentServiceRegistry();
+
+	/**
+	 * Retrieve a service by role.  If service is not found, but a {@link BasicServiceInitiator} is registered for
 	 * this service role, the service will be initialized and returned.
-	 *
+	 * <p/>
+	 * NOTE: We cannot return {@code <R extends Service<T>>} here because the service might come from the parent...
+	 * 
 	 * @param serviceRole The service role
-	 * @param <T> The type of the service
+	 * @param <R> The service role type
 	 *
 	 * @return The requested service.
 	 *
 	 * @throws UnknownServiceException Indicates the service was not known.
 	 */
-	public <T extends Service> T getService(Class<T> serviceRole);
+	public <R extends Service> R getService(Class<R> serviceRole);
 
 	/**
 	 * Register a service into the registry.
 	 *
 	 * @param serviceRole The service role.
 	 * @param service The service to register
+	 * @param <R> The service role type
 	 */
-	public <T extends Service> void registerService(Class<T> serviceRole, T service);
-
-	/**
-	 * Register a service initiator.
-	 *
-	 * @param initiator The initiator of a service
-	 */
-	public void registerServiceInitiator(ServiceInitiator initiator);
+	public <R extends Service> void registerService(Class<R> serviceRole, R service);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
index 1c69099540..67763b52a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/ServiceRegistryAwareService.java
@@ -1,39 +1,38 @@
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
 
-
 /**
  * Allows services to be injected with the {@link ServiceRegistry} during configuration phase.
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
index f3d2eba700..512279b38d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/StandardServiceInitiators.java
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
 package org.hibernate.service.spi;
 
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
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
-	public static List<ServiceInitiator> LIST = buildStandardServiceInitiatorList();
+	public static List<BasicServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
-	private static List<ServiceInitiator> buildStandardServiceInitiatorList() {
-		final List<ServiceInitiator> serviceInitiators = new ArrayList<ServiceInitiator>();
+	private static List<BasicServiceInitiator> buildStandardServiceInitiatorList() {
+		final List<BasicServiceInitiator> serviceInitiators = new ArrayList<BasicServiceInitiator>();
 
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
 
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyTargetSource.java b/hibernate-core/src/main/java/org/hibernate/service/spi/proxy/ServiceProxyTargetSource.java
index 75fe90d880..e7b50c0b7f 100644
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
 
 import org.hibernate.service.spi.Service;
 import org.hibernate.service.spi.ServiceRegistry;
 
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
-	 * @param <T> The type of the service
+	 * @param <R> The service role type
 	 *
 	 * @return The requested service.
 	 *
 	 * @throws org.hibernate.service.spi.UnknownServiceException Indicates the service was not known.
 	 */
-	public <T extends Service> T getServiceInternal(Class<T> serviceRole);
+	public <R extends Service> R getServiceInternal(Class<R> serviceRole);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 9436db7ac8..eba32ea470 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,547 +1,547 @@
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
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
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
 
-	private static ServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new ServiceRegistryImpl( properties );
+		return new BasicServiceRegistryImpl( properties );
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
 
-			ServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
+			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
index af368ceac7..2b1c6e1878 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,285 +1,285 @@
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
 
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
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
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.jboss.logging.Logger;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaUpdate {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SchemaUpdate.class.getName());
 	private ConnectionHelper connectionHelper;
 	private Configuration configuration;
 	private Dialect dialect;
 	private List exceptions;
 	private boolean haltOnError = false;
 	private boolean format = true;
 	private String outputFile = null;
 	private String delimiter;
 	private Formatter formatter;
 	private SqlStatementLogger sqlStatementLogger;
 
 	public SchemaUpdate(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaUpdate(Configuration cfg, Properties connectionProperties) throws HibernateException {
 		this.configuration = cfg;
 		dialect = Dialect.getDialect( connectionProperties );
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( connectionProperties );
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 		exceptions = new ArrayList();
 		formatter = ( ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, props ) ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public SchemaUpdate(JdbcServices jdbcServices, Configuration cfg) throws HibernateException {
 		this.configuration = cfg;
 		dialect = jdbcServices.getDialect();
 		connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				jdbcServices.getConnectionProvider()
 		);
 		exceptions = new ArrayList();
 		sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
-	private static ServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new ServiceRegistryImpl( properties );
+		return new BasicServiceRegistryImpl( properties );
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			// If true then execute db updates, otherwise just generate and display updates
 			boolean doUpdate = true;
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--text" ) ) {
 						doUpdate = false;
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
-			ServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
+			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaUpdate( serviceRegistry.getService( JdbcServices.class ), cfg ).execute( script, doUpdate );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Execute the schema updates
 	 *
 	 * @param script print all DDL to the console
 	 */
 	public void execute(boolean script, boolean doUpdate) {
 
         LOG.runningHbm2ddlSchemaUpdate();
 
 		Connection connection = null;
 		Statement stmt = null;
 		Writer outputFileWriter = null;
 
 		exceptions.clear();
 
 		try {
 
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect );
 				stmt = connection.createStatement();
 			}
 			catch ( SQLException sqle ) {
 				exceptions.add( sqle );
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
             LOG.updatingSchema();
 
 
 			if ( outputFile != null ) {
                 LOG.writingGeneratedSchemaToFile(outputFile);
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			String[] createSQL = configuration.generateSchemaUpdateScript( dialect, meta );
 			for ( int j = 0; j < createSQL.length; j++ ) {
 
 				final String sql = createSQL[j];
 				String formatted = formatter.format( sql );
 				try {
 					if ( delimiter != null ) {
 						formatted += delimiter;
 					}
 					if ( script ) {
 						System.out.println( formatted );
 					}
 					if ( outputFile != null ) {
 						outputFileWriter.write( formatted + "\n" );
 					}
 					if ( doUpdate ) {
                         LOG.debugf(sql);
 						stmt.executeUpdate( formatted );
 					}
 				}
 				catch ( SQLException e ) {
 					if ( haltOnError ) {
 						throw new JDBCException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
                     LOG.unsuccessful(sql);
                     LOG.error(e.getMessage());
 				}
 			}
 
             LOG.schemaUpdateComplete();
 
 		}
 		catch ( Exception e ) {
 			exceptions.add( e );
             LOG.unableToCompleteSchemaUpdate(e);
 		}
 		finally {
 
 			try {
 				if ( stmt != null ) {
 					stmt.close();
 				}
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
                 LOG.unableToCloseConnection(e);
 			}
 			try {
 				if( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch(Exception e) {
 				exceptions.add(e);
                 LOG.unableToCloseConnection(e);
 			}
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
 
 	public void setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	public void setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public void setOutputFile(String outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
index 50e85caee5..ea74e62b50 100755
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
@@ -1,172 +1,172 @@
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
 
 import java.io.FileInputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.jboss.logging.Logger;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaValidator {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SchemaValidator.class.getName());
 
 	private ConnectionHelper connectionHelper;
 	private Configuration configuration;
 	private Dialect dialect;
 
 	public SchemaValidator(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaValidator(Configuration cfg, Properties connectionProperties) throws HibernateException {
 		this.configuration = cfg;
 		dialect = Dialect.getDialect( connectionProperties );
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( connectionProperties );
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 	}
 
 	public SchemaValidator(JdbcServices jdbcServices, Configuration cfg ) throws HibernateException {
 		this.configuration = cfg;
 		dialect = jdbcServices.getDialect();
 		connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				jdbcServices.getConnectionProvider()
 		);
 	}
 
-	private static ServiceRegistryImpl createServiceRegistry(Properties properties) {
+	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new ServiceRegistryImpl( properties );
+		return new BasicServiceRegistryImpl( properties );
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
-			ServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
+			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaValidator( serviceRegistry.getService( JdbcServices.class ), cfg ).validate();
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Perform the validations.
 	 */
 	public void validate() {
 
         LOG.runningSchemaValidator();
 
 		Connection connection = null;
 
 		try {
 
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( false );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect, false );
 			}
 			catch ( SQLException sqle ) {
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
 			configuration.validateSchema( dialect, meta );
 
 		}
 		catch ( SQLException e ) {
             LOG.unableToCompleteSchemaValidation(e);
 		}
 		finally {
 
 			try {
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
                 LOG.unableToCloseConnection(e);
 			}
 
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/extendshbm/ExtendsTest.java b/hibernate-core/src/test/java/org/hibernate/test/extendshbm/ExtendsTest.java
index b6b6c4b230..536df42047 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/extendshbm/ExtendsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/extendshbm/ExtendsTest.java
@@ -1,211 +1,211 @@
 //$Id: ExtendsTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 package org.hibernate.test.extendshbm;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class ExtendsTest extends BaseUnitTestCase {
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() {
 		ServiceRegistryBuilder.destroy( serviceRegistry );
 	}
 
 	private String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	@Test
 	public void testAllInOne() {
 		Configuration cfg = new Configuration();
 
 		cfg.addResource( getBaseForMappings() + "extendshbm/allinone.hbm.xml" );
 		cfg.buildMappings();
 		assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 		assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 		assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 	}
 
 	@Test
 	public void testOutOfOrder() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/Customer.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Person.hbm.xml" );
 			cfg.addResource( getBaseForMappings() + "extendshbm/Employee.hbm.xml" );
 
 			cfg.buildSessionFactory( serviceRegistry );
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 
 		}
 		catch ( HibernateException e ) {
 			fail( "should not fail with exception! " + e );
 		}
 
 	}
 
 	@Test
 	public void testNwaitingForSuper() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/Customer.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Employee.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Person.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 
 	}
 
 	@Test
 	public void testMissingSuper() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/Customer.hbm.xml" );
 			assertNull(
 					"cannot be in the configuration yet!",
 					cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" )
 			);
 			cfg.addResource( getBaseForMappings() + "extendshbm/Employee.hbm.xml" );
 
 			cfg.buildSessionFactory( serviceRegistry );
 
 			fail( "Should not be able to build sessionFactory without a Person" );
 		}
 		catch ( HibernateException e ) {
 
 		}
 
 	}
 
 	@Test
 	public void testAllSeparateInOne() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/allseparateinone.hbm.xml" );
 
 			cfg.buildSessionFactory( serviceRegistry );
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Employee" ) );
 
 		}
 		catch ( HibernateException e ) {
 			fail( "should not fail with exception! " + e );
 		}
 
 	}
 
 	@Test
 	public void testJoinedSubclassAndEntityNamesOnly() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/entitynames.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "EntityHasName" ) );
 			assertNotNull( cfg.getClassMapping( "EntityCompany" ) );
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 	}
 
 	@Test
 	public void testEntityNamesWithPackage() {
 		Configuration cfg = new Configuration();
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/packageentitynames.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "EntityHasName" ) );
 			assertNotNull( cfg.getClassMapping( "EntityCompany" ) );
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 	}
 
 	@Test
 	public void testUnionSubclass() {
 		Configuration cfg = new Configuration();
 
 		try {
 			cfg.addResource( getBaseForMappings() + "extendshbm/unionsubclass.hbm.xml" );
 
 			cfg.buildMappings();
 
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Person" ) );
 			assertNotNull( cfg.getClassMapping( "org.hibernate.test.extendshbm.Customer" ) );
 
 		}
 		catch ( HibernateException e ) {
 			e.printStackTrace();
 			fail( "should not fail with exception! " + e );
 
 		}
 	}
 
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
index 44017628ae..78fd6f5585 100644
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
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.StandardServiceInitiators;
 
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
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = new ServiceRegistryImpl(
+		serviceRegistry = new BasicServiceRegistryImpl(
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
index 7feacb640f..7c596512f3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
@@ -1,91 +1,91 @@
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
 package org.hibernate.test.service;
 
 import java.util.Properties;
 
 import org.junit.Test;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceBootstrappingTest extends BaseUnitTestCase {
 	@Test
 	public void testBasicBuild() {
-		ServiceRegistryImpl serviceRegistry = new ServiceRegistryImpl( ConnectionProviderBuilder.getConnectionProviderProperties() );
+		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertFalse( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithLogging() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.put( Environment.SHOW_SQL, "true" );
 
-		ServiceRegistryImpl serviceRegistry = new ServiceRegistryImpl( props );
+		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( props );
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertTrue( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithServiceOverride() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 
-		ServiceRegistryImpl serviceRegistry = new ServiceRegistryImpl( props );
+		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( props );
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 
 		serviceRegistry.registerService( ConnectionProvider.class, new UserSuppliedConnectionProviderImpl() );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( UserSuppliedConnectionProviderImpl.class ) );
 
 		serviceRegistry.destroy();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
index 3943269b41..f4b1adc387 100644
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
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.StandardServiceInitiators;
 
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
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = new ServiceRegistryImpl(
+		serviceRegistry = new BasicServiceRegistryImpl(
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
index 6be132c23e..b1c5d57766 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
@@ -1,164 +1,164 @@
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
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.service.internal.ServiceProxy;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
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
  * Testing transaction handling when the JTA transaction facade is the driver.
  *
  * @author Steve Ebersole
  */
 public class BasicDrivingTest extends BaseUnitTestCase {
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName() );
 		TestingJtaBootstrap.prepare( configValues );
-		serviceRegistry = new ServiceRegistryImpl( configValues );
+		serviceRegistry = new BasicServiceRegistryImpl( configValues );
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
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
 			assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		assertTrue( txn.isInitiator() );
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
 			try {
 				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 				instance.retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 				instance.retrieveTransactionManager().rollback();
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
index b5cf608517..90694dc042 100644
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
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.internal.ServiceProxy;
-import org.hibernate.service.internal.ServiceRegistryImpl;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.StandardServiceInitiators;
 
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
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		TestingJtaBootstrap.prepare( configValues );
 //		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 
-		serviceRegistry = new ServiceRegistryImpl( StandardServiceInitiators.LIST, configValues );
+		serviceRegistry = new BasicServiceRegistryImpl( StandardServiceInitiators.LIST, configValues );
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
index 02b54aba83..0a14e81301 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1636 +1,1636 @@
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
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
 import org.hibernate.service.spi.ServiceRegistry;
 
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
-		return buildEntityManagerFactory( new ServiceRegistryImpl( cfg.getProperties() ) );
+		return buildEntityManagerFactory( new BasicServiceRegistryImpl( cfg.getProperties() ) );
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
 			Collection<String> xmlFiles = (Collection<String>) workingVars.get( AvailableSettings.XML_FILE_NAMES );
 			for ( String xmlFile : xmlFiles ) {
 				Boolean useMetaInf = null;
 				try {
 					if ( xmlFile.endsWith( META_INF_ORM_XML ) ) {
 						useMetaInf = true;
 					}
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
                 if (Boolean.TRUE.equals(useMetaInf)) {
 					LOG.exceptionHeaderFound(getExceptionHeader(), META_INF_ORM_XML);
 				}
                 else if (Boolean.FALSE.equals(useMetaInf)) {
 					LOG.exceptionHeaderNotFound(getExceptionHeader(), META_INF_ORM_XML);
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
 					Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName()
 			);
 		}
 		else if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName() );
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
 
 	public Ejb3Configuration setSessionFactoryObserver(SessionFactoryObserver observer) {
 		cfg.setSessionFactoryObserver( observer );
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
 	public Configuration getHibernateConfiguration() {
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
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
index 758209b67b..8a88b5a2d6 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
@@ -1,284 +1,284 @@
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
 package org.hibernate.ejb.test;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.Persistence;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.hibernate.testing.TestLogger.LOG;
 
 /**
  * A base class for all ejb tests.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public abstract class BaseEntityManagerFunctionalTestCase extends BaseUnitTestCase {
 	// IMPL NOTE : Here we use @Before and @After (instead of @BeforeClassOnce and @AfterClassOnce like we do in
 	// BaseCoreFunctionalTestCase) because the old HEM test methodology was to create an EMF for each test method.
 
 	private static final Dialect dialect = Dialect.getDialect();
 
 	private Ejb3Configuration ejb3Configuration;
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 	private EntityManagerFactory entityManagerFactory;
 
 	private EntityManager em;
 	private ArrayList<EntityManager> isolatedEms = new ArrayList<EntityManager>();
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	protected EntityManagerFactory entityManagerFactory() {
 		return entityManagerFactory;
 	}
 
-	protected ServiceRegistryImpl serviceRegistry() {
+	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Before
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void buildSessionFactory() throws Exception {
 		LOG.trace( "Building session factory" );
 		ejb3Configuration = buildConfiguration();
 		ejb3Configuration.configure( getConfig() );
 		afterConfigurationBuilt( ejb3Configuration );
 		serviceRegistry = buildServiceRegistry( ejb3Configuration.getHibernateConfiguration() );
 		applyServices( serviceRegistry );
 		entityManagerFactory = ejb3Configuration.buildEntityManagerFactory( serviceRegistry );
 		afterEntityManagerFactoryBuilt();
 	}
 
 	protected Ejb3Configuration buildConfiguration() {
 		Ejb3Configuration ejb3Cfg = constructConfiguration();
 		addMappings( ejb3Cfg.getHibernateConfiguration() );
 		return ejb3Cfg;
 	}
 
 	protected Ejb3Configuration constructConfiguration() {
 		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
 		if ( createSchema() ) {
 			ejb3Configuration.getHibernateConfiguration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return ejb3Configuration;
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource( mapping, getClass().getClassLoader() );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected Map getConfig() {
 		Map<Object, Object> config = loadProperties();
 		ArrayList<Class> classes = new ArrayList<Class>();
 
 		classes.addAll( Arrays.asList( getAnnotatedClasses() ) );
 		config.put( AvailableSettings.LOADED_CLASSES, classes );
 		for ( Map.Entry<Class, String> entry : getCachedClasses().entrySet() ) {
 			config.put( AvailableSettings.CLASS_CACHE_PREFIX + "." + entry.getKey().getName(), entry.getValue() );
 		}
 		for ( Map.Entry<String, String> entry : getCachedCollections().entrySet() ) {
 			config.put( AvailableSettings.COLLECTION_CACHE_PREFIX + "." + entry.getKey(), entry.getValue() );
 		}
 		if ( getEjb3DD().length > 0 ) {
 			ArrayList<String> dds = new ArrayList<String>();
 			dds.addAll( Arrays.asList( getEjb3DD() ) );
 			config.put( AvailableSettings.XML_FILE_NAMES, dds );
 		}
 
 		addConfigOptions( config );
 		return config;
 	}
 
 	protected void addConfigOptions(Map options) {
 	}
 
 	private Properties loadProperties() {
 		Properties props = new Properties();
 		InputStream stream = Persistence.class.getResourceAsStream( "/hibernate.properties" );
 		if ( stream != null ) {
 			try {
 				props.load( stream );
 			}
 			catch ( Exception e ) {
 				throw new RuntimeException( "could not load hibernate.properties" );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch ( IOException ignored ) {
 				}
 			}
 		}
 		return props;
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	public Map<Class, String> getCachedClasses() {
 		return new HashMap<Class, String>();
 	}
 
 	public Map<String, String> getCachedCollections() {
 		return new HashMap<String, String>();
 	}
 
 	public String[] getEjb3DD() {
 		return new String[] { };
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void afterConfigurationBuilt(Ejb3Configuration ejb3Configuration) {
 	}
 
-	protected ServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
+	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new ServiceRegistryImpl( properties );
+		return new BasicServiceRegistryImpl( properties );
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
-	protected void applyServices(ServiceRegistryImpl serviceRegistry) {
+	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterEntityManagerFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 
 	@After
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void releaseResources() {
 		releaseUnclosedEntityManagers();
 
 		if ( entityManagerFactory != null ) {
 			entityManagerFactory.close();
 		}
 
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 
 	private void releaseUnclosedEntityManagers() {
 		releaseUnclosedEntityManager( this.em );
 
 		for ( EntityManager isolatedEm : isolatedEms ) {
 			releaseUnclosedEntityManager( isolatedEm );
 		}
 	}
 
 	private void releaseUnclosedEntityManager(EntityManager em) {
 		if ( em == null ) {
 			return;
 		}
 		if ( em.getTransaction().isActive() ) {
 			em.getTransaction().rollback();
             LOG.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
 		}
 		if ( em.isOpen() ) {
 			// as we open an EM before the test runs, it will still be open if the test uses a custom EM.
 			// or, the person may have forgotten to close. So, do not raise a "fail", but log the fact.
 			em.close();
             LOG.warn("The EntityManager is not closed. Closing it.");
 		}
 	}
 
 	protected EntityManager getOrCreateEntityManager() {
 		if ( em == null || !em.isOpen() ) {
 			em = entityManagerFactory.createEntityManager();
 		}
 		return em;
 	}
 
 	protected EntityManager createIsolatedEntityManager() {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager();
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createIsolatedEntityManager(Map props) {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager(props);
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createEntityManager(Map properties) {
 		// always reopen a new EM and close the existing one
 		if ( em != null && em.isOpen() ) {
 			em.close();
 		}
 		em = entityManagerFactory.createEntityManager( properties );
 		return em;
 	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
index 44efbacc6a..6eb3cfb672 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
@@ -1,153 +1,152 @@
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
 package org.hibernate.envers.test;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import java.io.IOException;
 
 import org.testng.annotations.AfterClass;
 import org.testng.annotations.BeforeClass;
 import org.testng.annotations.BeforeMethod;
 import org.testng.annotations.Optional;
 import org.testng.annotations.Parameters;
 
 import org.hibernate.cfg.Environment;
-import org.hibernate.dialect.H2Dialect;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.envers.AuditReader;
 import org.hibernate.envers.AuditReaderFactory;
 import org.hibernate.envers.event.AuditEventListener;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PostDeleteEventListener;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PostUpdateEventListener;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionUpdateEventListener;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractEntityTest {
     private EntityManagerFactory emf;
     private EntityManager entityManager;
     private AuditReader auditReader;
     private Ejb3Configuration cfg;
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
     private boolean audited;
 
     public abstract void configure(Ejb3Configuration cfg);
 
     private void initListeners() {
         AuditEventListener listener = new AuditEventListener();
         cfg.getEventListeners().setPostInsertEventListeners(new PostInsertEventListener[] { listener });
         cfg.getEventListeners().setPostUpdateEventListeners(new PostUpdateEventListener[] { listener });
         cfg.getEventListeners().setPostDeleteEventListeners(new PostDeleteEventListener[] { listener });
         cfg.getEventListeners().setPreCollectionUpdateEventListeners(new PreCollectionUpdateEventListener[] { listener });
         cfg.getEventListeners().setPreCollectionRemoveEventListeners(new PreCollectionRemoveEventListener[] { listener });
         cfg.getEventListeners().setPostCollectionRecreateEventListeners(new PostCollectionRecreateEventListener[] { listener });
     }
 
     private void closeEntityManager() {
         if (entityManager != null) {
             entityManager.close();
             entityManager = null;
         }
     }
 
     @BeforeMethod
     public void newEntityManager() {
         closeEntityManager();
         
         entityManager = emf.createEntityManager();
 
         if (audited) {
             auditReader = AuditReaderFactory.get(entityManager);
         }
     }
 
     @BeforeClass
     @Parameters("auditStrategy")    
     public void init(@Optional String auditStrategy) throws IOException {
         init(true, auditStrategy);
     }
 
     protected void init(boolean audited, String auditStrategy) throws IOException {
         this.audited = audited;
 
         cfg = new Ejb3Configuration();
         if (audited) {
             initListeners();
         }
 
         cfg.configure( "hibernate.test.cfg.xml" );
 
         if (auditStrategy != null && !"".equals(auditStrategy)) {
             cfg.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
         }
 
         // Separate database for each test class
         cfg.setProperty( Environment.URL, "jdbc:h2:mem:" + this.getClass().getName() );
 
         configure( cfg );
 
 		cfg.configure( cfg.getHibernateConfiguration().getProperties() );
 
-		serviceRegistry = new ServiceRegistryImpl( cfg.getProperties() );
+		serviceRegistry = new BasicServiceRegistryImpl( cfg.getProperties() );
 
         emf = cfg.buildEntityManagerFactory( serviceRegistry );
 
         newEntityManager();
     }
 
     @AfterClass
     public void close() {
         closeEntityManager();
         emf.close();
 		serviceRegistry.destroy();
     }
 
     public EntityManager getEntityManager() {
         return entityManager;
     }
 
     public AuditReader getAuditReader() {
         return auditReader;
     }
 
     public Ejb3Configuration getCfg() {
         return cfg;
     }
 
     protected void addJTAConfig(Ejb3Configuration cfg) {
 		TestingJtaBootstrap.prepare( cfg.getProperties() );
 		cfg.getProperties().remove( Environment.USER );
 		cfg.getProperties().remove( Environment.PASS );
 		cfg.setProperty( AvailableSettings.TRANSACTION_TYPE, "JTA" );
     }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index 94f6c83ea8..8c0ad52fb6 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,224 +1,224 @@
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Set;
 
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 import org.hibernate.cache.GeneralDataRegion;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.Test;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 /**
  * Base class for tests of QueryResultsRegion and TimestampsRegion.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractGeneralDataRegionTestCase extends AbstractRegionImplTestCase {
 	protected static final String KEY = "Key";
 
 	protected static final String VALUE1 = "value1";
 	protected static final String VALUE2 = "value2";
 
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, false, true );
 	}
 
 	@Override
 	protected void putInRegion(Region region, Object key, Object value) {
 		((GeneralDataRegion) region).put( key, value );
 	}
 
 	@Override
 	protected void removeFromRegion(Region region, Object key) {
 		((GeneralDataRegion) region).evict( key );
 	}
 
 	@Test
 	public void testEvict() throws Exception {
 		evictOrRemoveTest();
 	}
 
 	private void evictOrRemoveTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryImpl( cfg.getProperties() ),
+				new BasicServiceRegistryImpl( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter localCache = getInfinispanCache( regionFactory );
 		boolean invalidation = localCache.isClusteredInvalidation();
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ), cfg.getProperties(), null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryImpl( cfg.getProperties() ),
+				new BasicServiceRegistryImpl( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 		localRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// allow async propagation
 		sleep( 250 );
 		Object expected = invalidation ? null : VALUE1;
 		assertEquals( expected, remoteRegion.get( KEY ) );
 
 		localRegion.evict( KEY );
 
 		// allow async propagation
 		sleep( 250 );
 		assertEquals( null, localRegion.get( KEY ) );
 		assertEquals( null, remoteRegion.get( KEY ) );
 	}
 
 	protected abstract String getStandardRegionName(String regionPrefix);
 
 	/**
 	 * Test method for {@link QueryResultsRegion#evictAll()}.
 	 * <p/>
 	 * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
 	 * CollectionRegionAccessStrategy API.
 	 */
 	public void testEvictAll() throws Exception {
 		evictOrRemoveAllTest( "entity" );
 	}
 
 	private void evictOrRemoveAllTest(String configName) throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryImpl( cfg.getProperties() ),
+				new BasicServiceRegistryImpl( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter localCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryImpl( cfg.getProperties() ),
+				new BasicServiceRegistryImpl( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter remoteCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		Set keys = localCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		keys = remoteCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 		localRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
 		remoteRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, remoteRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
 		localRegion.evictAll();
 
 		// allow async propagation
 		sleep( 250 );
 		// This should re-establish the region root node in the optimistic case
 		assertNull( localRegion.get( KEY ) );
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( localCache.keySet() ) );
 
 		// Re-establishing the region root on the local node doesn't
 		// propagate it to other nodes. Do a get on the remote node to re-establish
 		// This only adds a node in the case of optimistic locking
 		assertEquals( null, remoteRegion.get( KEY ) );
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( remoteCache.keySet() ) );
 
 		assertEquals( "local is clean", null, localRegion.get( KEY ) );
 		assertEquals( "remote is clean", null, remoteRegion.get( KEY ) );
 	}
 
 	protected void rollback() {
 		try {
 			BatchModeTransactionManager.getInstance().rollback();
 		}
 		catch (Exception e) {
 			LOG.error( e.getMessage(), e );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
index 8fe223e0bb..cf8e57de0b 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
@@ -1,138 +1,138 @@
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Defines the environment for a node.
  *
  * @author Steve Ebersole
  */
 public class NodeEnvironment {
 	private final Configuration configuration;
 
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 	private InfinispanRegionFactory regionFactory;
 
 	private Map<String,EntityRegionImpl> entityRegionMap;
 	private Map<String,CollectionRegionImpl> collectionRegionMap;
 
 	public NodeEnvironment(Configuration configuration) {
 		this.configuration = configuration;
 	}
 
 	public Configuration getConfiguration() {
 		return configuration;
 	}
 
-	public ServiceRegistryImpl getServiceRegistry() {
+	public BasicServiceRegistryImpl getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public EntityRegionImpl getEntityRegion(String name, CacheDataDescription cacheDataDescription) {
 		if ( entityRegionMap == null ) {
 			entityRegionMap = new HashMap<String, EntityRegionImpl>();
 			return buildAndStoreEntityRegion( name, cacheDataDescription );
 		}
 		EntityRegionImpl region = entityRegionMap.get( name );
 		if ( region == null ) {
 			region = buildAndStoreEntityRegion( name, cacheDataDescription );
 		}
 		return region;
 	}
 
 	private EntityRegionImpl buildAndStoreEntityRegion(String name, CacheDataDescription cacheDataDescription) {
 		EntityRegionImpl region = (EntityRegionImpl) regionFactory.buildEntityRegion(
 				name,
 				configuration.getProperties(),
 				cacheDataDescription
 		);
 		entityRegionMap.put( name, region );
 		return region;
 	}
 
 	public CollectionRegionImpl getCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
 		if ( collectionRegionMap == null ) {
 			collectionRegionMap = new HashMap<String, CollectionRegionImpl>();
 			return buildAndStoreCollectionRegion( name, cacheDataDescription );
 		}
 		CollectionRegionImpl region = collectionRegionMap.get( name );
 		if ( region == null ) {
 			region = buildAndStoreCollectionRegion( name, cacheDataDescription );
 			collectionRegionMap.put( name, region );
 		}
 		return region;
 	}
 
 	private CollectionRegionImpl buildAndStoreCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
 		CollectionRegionImpl region;
 		region = (CollectionRegionImpl) regionFactory.buildCollectionRegion(
 				name,
 				configuration.getProperties(),
 				cacheDataDescription
 		);
 		return region;
 	}
 
 	public void prepare() throws Exception {
-		serviceRegistry = new ServiceRegistryImpl( configuration.getProperties() );
+		serviceRegistry = new BasicServiceRegistryImpl( configuration.getProperties() );
 		regionFactory = CacheTestUtil.startRegionFactory( serviceRegistry, configuration );
 	}
 
 	public void release() throws Exception {
 		if ( entityRegionMap != null ) {
 			for ( EntityRegionImpl region : entityRegionMap.values() ) {
 				region.getCacheAdapter().withFlags( FlagAdapter.CACHE_MODE_LOCAL ).clear();
 				region.getCacheAdapter().stop();
 			}
 			entityRegionMap.clear();
 		}
 		if ( collectionRegionMap != null ) {
 			for ( CollectionRegionImpl collectionRegion : collectionRegionMap.values() ) {
 				collectionRegion.getCacheAdapter().withFlags( FlagAdapter.CACHE_MODE_LOCAL ).clear();
 				collectionRegion.getCacheAdapter().stop();
 			}
 			collectionRegionMap.clear();
 		}
 		if ( regionFactory != null ) {
 // Currently the RegionFactory is shutdown by its registration with the CacheTestSetup from CacheTestUtil when built
 			regionFactory.stop();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
index 7b2d9816e5..ad0b535feb 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
@@ -1,191 +1,191 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class DualNodeTestCase extends BaseCoreFunctionalTestCase {
 	private static final Log log = LogFactory.getLog( DualNodeTestCase.class );
 
 	public static final String NODE_ID_PROP = "hibernate.test.cluster.node.id";
 	public static final String NODE_ID_FIELD = "nodeId";
 	public static final String LOCAL = "local";
 	public static final String REMOTE = "remote";
 
 	private SecondNodeEnvironment secondNodeEnvironment;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 				"cache/infinispan/functional/Contact.hbm.xml", "cache/infinispan/functional/Customer.hbm.xml"
 		};
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return "transactional";
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		standardConfigure( cfg );
 		cfg.setProperty( NODE_ID_PROP, LOCAL );
 		cfg.setProperty( NODE_ID_FIELD, LOCAL );
 	}
 
 	@Override
 	protected void cleanupTest() throws Exception {
 		cleanupTransactionManagement();
 	}
 
 	protected void cleanupTransactionManagement() {
 		DualNodeJtaTransactionManagerImpl.cleanupTransactions();
 		DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
 	}
 
 	@Before
 	public void prepare() throws Exception {
 		secondNodeEnvironment = new SecondNodeEnvironment();
 	}
 
 	@After
 	public void unPrepare() {
 		if ( secondNodeEnvironment != null ) {
 			secondNodeEnvironment.shutDown();
 		}
 	}
 
 	protected SecondNodeEnvironment secondNodeEnvironment() {
 		return secondNodeEnvironment;
 	}
 
 	protected Class getCacheRegionFactory() {
 		return ClusterAwareRegionFactory.class;
 	}
 
 	protected Class getConnectionProviderClass() {
 		return DualNodeConnectionProviderImpl.class;
 	}
 
 	protected Class getJtaPlatformClass() {
 		return DualNodeJtaPlatformImpl.class;
 	}
 
 	protected Class getTransactionFactoryClass() {
 		return CMTTransactionFactory.class;
 	}
 
 	protected void sleep(long ms) {
 		try {
 			Thread.sleep( ms );
 		}
 		catch (InterruptedException e) {
 			log.warn( "Interrupted during sleep", e );
 		}
 	}
 
 	protected boolean getUseQueryCache() {
 		return true;
 	}
 
 	protected void configureSecondNode(Configuration cfg) {
 
 	}
 
 	protected void standardConfigure(Configuration cfg) {
 		super.configure( cfg );
 
 		cfg.setProperty( Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName() );
 		cfg.setProperty( JtaPlatformInitiator.JTA_PLATFORM, getJtaPlatformClass().getName() );
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName() );
 		cfg.setProperty( Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName() );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, String.valueOf( getUseQueryCache() ) );
 	}
 
 	public class SecondNodeEnvironment {
 		private Configuration configuration;
-		private ServiceRegistryImpl serviceRegistry;
+		private BasicServiceRegistryImpl serviceRegistry;
 		private SessionFactoryImplementor sessionFactory;
 
 		public SecondNodeEnvironment() {
 			configuration = constructConfiguration();
 			standardConfigure( configuration );
 			configuration.setProperty( NODE_ID_PROP, REMOTE );
 			configuration.setProperty( NODE_ID_FIELD, REMOTE );
 			configureSecondNode( configuration );
 			addMappings(configuration);
 			configuration.buildMappings();
 			applyCacheSettings( configuration );
 			afterConfigurationBuilt( configuration );
 			serviceRegistry = buildServiceRegistry( configuration );
 			sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		}
 
 		public Configuration getConfiguration() {
 			return configuration;
 		}
 
-		public ServiceRegistryImpl getServiceRegistry() {
+		public BasicServiceRegistryImpl getServiceRegistry() {
 			return serviceRegistry;
 		}
 
 		public SessionFactoryImplementor getSessionFactory() {
 			return sessionFactory;
 		}
 
 		public void shutDown() {
 			if ( sessionFactory != null ) {
 				try {
 					sessionFactory.close();
 				}
 				catch (Exception ignore) {
 				}
 			}
 			if ( serviceRegistry != null ) {
 				try {
 					serviceRegistry.destroy();
 				}
 				catch (Exception ignore) {
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index d6dcadb6e7..618558847e 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,333 +1,333 @@
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
 package org.hibernate.test.cache.infinispan.query;
 
 import java.util.Properties;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.infinispan.util.concurrent.IsolationLevel;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.StandardQueryCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import junit.framework.AssertionFailedError;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.hibernate.testing.TestLogger.LOG;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests of QueryResultRegionImpl.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
 	@Override
 	protected Region createRegion(
 			InfinispanRegionFactory regionFactory,
 			String regionName,
 			Properties properties,
 			CacheDataDescription cdd) {
 		return regionFactory.buildQueryResultsRegion( regionName, properties );
 	}
 
 	@Override
 	protected String getStandardRegionName(String regionPrefix) {
 		return regionPrefix + "/" + StandardQueryCache.class.getName();
 	}
 
 	@Override
 	protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
 		return CacheAdapterImpl.newInstance( regionFactory.getCacheManager().getCache( "local-query" ) );
 	}
 
 	@Override
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildCustomQueryCacheConfiguration( "test", "replicated-query" );
 	}
 
 	private void putDoesNotBlockGetTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryImpl( cfg.getProperties() ),
+				new BasicServiceRegistryImpl( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		final CountDownLatch readerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread reader = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					LOG.debug( "Transaction began, get value for key" );
 					assertTrue( VALUE2.equals( region.get( KEY ) ) == false );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (AssertionFailedError e) {
 					holder.a1 = e;
 					rollback();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					readerLatch.countDown();
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					LOG.debug( "Put value2" );
 					region.put( KEY, VALUE2 );
 					LOG.debug( "Put finished for value2, await writer latch" );
 					writerLatch.await();
 					LOG.debug( "Writer latch finished" );
 					BatchModeTransactionManager.getInstance().commit();
 					LOG.debug( "Transaction committed" );
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		reader.setDaemon( true );
 		writer.setDaemon( true );
 
 		writer.start();
 		assertFalse( "Writer is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		// Start the reader
 		reader.start();
 		assertTrue( "Reader finished promptly", readerLatch.await( 1000000000, TimeUnit.MILLISECONDS ) );
 
 		writerLatch.countDown();
 		assertTrue( "Reader finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		assertEquals( VALUE2, region.get( KEY ) );
 
 		if ( holder.a1 != null ) {
 			throw holder.a1;
 		}
 		else if ( holder.a2 != null ) {
 			throw holder.a2;
 		}
 
 		assertEquals( "writer saw no exceptions", null, holder.e1 );
 		assertEquals( "reader saw no exceptions", null, holder.e2 );
 	}
 
 	public void testGetDoesNotBlockPut() throws Exception {
 		getDoesNotBlockPutTest();
 	}
 
 	private void getDoesNotBlockPutTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new ServiceRegistryImpl( cfg.getProperties() ),
+				new BasicServiceRegistryImpl( cfg.getProperties() ),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		// final Fqn rootFqn = getRegionFqn(getStandardRegionName(REGION_PREFIX), REGION_PREFIX);
 		final CacheAdapter jbc = getInfinispanCache( regionFactory );
 
 		final CountDownLatch blockerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread blocker = new Thread() {
 
 			@Override
 			public void run() {
 				// Fqn toBlock = new Fqn(rootFqn, KEY);
 				GetBlocker blocker = new GetBlocker( blockerLatch, KEY );
 				try {
 					jbc.addListener( blocker );
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.get( KEY );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					jbc.removeListener( blocker );
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 
 			@Override
 			public void run() {
 				try {
 					writerLatch.await();
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.put( KEY, VALUE2 );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		blocker.setDaemon( true );
 		writer.setDaemon( true );
 
 		boolean unblocked = false;
 		try {
 			blocker.start();
 			writer.start();
 
 			assertFalse( "Blocker is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 			// Start the writer
 			writerLatch.countDown();
 			assertTrue( "Writer finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 			blockerLatch.countDown();
 			unblocked = true;
 
 			if ( IsolationLevel.REPEATABLE_READ.equals( jbc.getConfiguration().getIsolationLevel() ) ) {
 				assertEquals( VALUE1, region.get( KEY ) );
 			}
 			else {
 				assertEquals( VALUE2, region.get( KEY ) );
 			}
 
 			if ( holder.a1 != null ) {
 				throw holder.a1;
 			}
 			else if ( holder.a2 != null ) {
 				throw holder.a2;
 			}
 
 			assertEquals( "blocker saw no exceptions", null, holder.e1 );
 			assertEquals( "writer saw no exceptions", null, holder.e2 );
 		}
 		finally {
 			if ( !unblocked ) {
 				blockerLatch.countDown();
 			}
 		}
 	}
 
 	@Listener
 	public class GetBlocker {
 
 		private CountDownLatch latch;
 		// private Fqn fqn;
 		private Object key;
 
 		GetBlocker(
 				CountDownLatch latch,
 				Object key
 		) {
 			this.latch = latch;
 			this.key = key;
 		}
 
 		@CacheEntryVisited
 		public void nodeVisisted(CacheEntryVisitedEvent event) {
 			if ( event.isPre() && event.getKey().equals( key ) ) {
 				try {
 					latch.await();
 				}
 				catch (InterruptedException e) {
 					LOG.error( "Interrupted waiting for latch", e );
 				}
 			}
 		}
 	}
 
 	private class ExceptionHolder {
 		Exception e1;
 		Exception e2;
 		AssertionFailedError a1;
 		AssertionFailedError a2;
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
index d62c35320b..729925d7d4 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
@@ -1,212 +1,212 @@
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
 package org.hibernate.test.cache.infinispan.timestamp;
 
 import java.util.Properties;
 
 import org.infinispan.AdvancedCache;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryActivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryEvicted;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryPassivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.Event;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.ClassLoaderAwareCache;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.functional.classloader.Account;
 import org.hibernate.test.cache.infinispan.functional.classloader.AccountHolder;
 import org.hibernate.test.cache.infinispan.functional.classloader.SelectedClassnameClassLoader;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Tests of TimestampsRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TimestampsRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
     @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + UpdateTimestampsCache.class.getName();
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildTimestampsRegion(regionName, properties);
    }
 
    @Override
    protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("timestamps"));
    }
 
    public void testClearTimestampsRegionInIsolated() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  new ServiceRegistryImpl( cfg.getProperties() ),
+			  new BasicServiceRegistryImpl( cfg.getProperties() ),
 			  cfg,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       Configuration cfg2 = createConfiguration();
       InfinispanRegionFactory regionFactory2 = CacheTestUtil.startRegionFactory(
-			  new ServiceRegistryImpl( cfg.getProperties() ),
+			  new BasicServiceRegistryImpl( cfg.getProperties() ),
 			  cfg2,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       TimestampsRegionImpl region = (TimestampsRegionImpl) regionFactory.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg.getProperties());
       TimestampsRegionImpl region2 = (TimestampsRegionImpl) regionFactory2.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 //      QueryResultsRegion region2 = regionFactory2.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 
 //      ClassLoader cl = Thread.currentThread().getContextClassLoader();
 //      Thread.currentThread().setContextClassLoader(cl.getParent());
 //      log.info("TCCL is " + cl.getParent());
 
       Account acct = new Account();
       acct.setAccountHolder(new AccountHolder());
       region.getCacheAdapter().withFlags(FlagAdapter.FORCE_SYNCHRONOUS).put(acct, "boo");
 
 //      region.put(acct, "boo");
 //
 //      region.evictAll();
 
 //      Account acct = new Account();
 //      acct.setAccountHolder(new AccountHolder());
 
 
 
    }
 
    @Override
    protected Configuration createConfiguration() {
       return CacheTestUtil.buildConfiguration("test", MockInfinispanRegionFactory.class, false, true);
    }
 
    public static class MockInfinispanRegionFactory extends InfinispanRegionFactory {
 
       public MockInfinispanRegionFactory() {
       }
 
       public MockInfinispanRegionFactory(Properties props) {
          super(props);
       }
 
 //      @Override
 //      protected TimestampsRegionImpl createTimestampsRegion(CacheAdapter cacheAdapter, String regionName) {
 //         return new MockTimestampsRegionImpl(cacheAdapter, regionName, getTransactionManager(), this);
 //      }
 
       @Override
       protected ClassLoaderAwareCache createCacheWrapper(AdvancedCache cache) {
          return new ClassLoaderAwareCache(cache, Thread.currentThread().getContextClassLoader()) {
             @Override
             public void addListener(Object listener) {
                super.addListener(new MockClassLoaderAwareListener(listener, this));
             }
          };
       }
 
       //      @Override
 //      protected EmbeddedCacheManager createCacheManager(Properties properties) throws CacheException {
 //         try {
 //            EmbeddedCacheManager manager = new DefaultCacheManager(InfinispanRegionFactory.DEF_INFINISPAN_CONFIG_RESOURCE);
 //            org.infinispan.config.Configuration ispnCfg = new org.infinispan.config.Configuration();
 //            ispnCfg.setCacheMode(org.infinispan.config.Configuration.CacheMode.REPL_SYNC);
 //            manager.defineConfiguration("timestamps", ispnCfg);
 //            return manager;
 //         } catch (IOException e) {
 //            throw new CacheException("Unable to create default cache manager", e);
 //         }
 //      }
 
       @Listener      
       public static class MockClassLoaderAwareListener extends ClassLoaderAwareCache.ClassLoaderAwareListener {
          MockClassLoaderAwareListener(Object listener, ClassLoaderAwareCache cache) {
             super(listener, cache);
          }
 
          @CacheEntryActivated
          @CacheEntryCreated
          @CacheEntryEvicted
          @CacheEntryInvalidated
          @CacheEntryLoaded
          @CacheEntryModified
          @CacheEntryPassivated
          @CacheEntryRemoved
          @CacheEntryVisited
          public void event(Event event) throws Throwable {
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
             String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
             SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
             Thread.currentThread().setContextClassLoader(visible);
             super.event(event);
             Thread.currentThread().setContextClassLoader(cl);            
          }
       }
    }
 
 //   @Listener
 //   public static class MockTimestampsRegionImpl extends TimestampsRegionImpl {
 //
 //      public MockTimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager, RegionFactory factory) {
 //         super(cacheAdapter, name, transactionManager, factory);
 //      }
 //
 //      @CacheEntryModified
 //      public void nodeModified(CacheEntryModifiedEvent event) {
 ////         ClassLoader cl = Thread.currentThread().getContextClassLoader();
 ////         String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
 ////         String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
 ////         SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
 ////         Thread.currentThread().setContextClassLoader(visible);
 //         super.nodeModified(event);
 ////         Thread.currentThread().setContextClassLoader(cl);
 //      }
 //   }
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
index b773ebd7c3..cb96628a0a 100644
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
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.spi.ServiceRegistry;
 
 import java.util.Map;
 import java.util.Properties;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
-	public static ServiceRegistryImpl buildServiceRegistry() {
+	public static BasicServiceRegistryImpl buildServiceRegistry() {
 		return buildServiceRegistry( Environment.getProperties() );
 	}
 
-	public static ServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
+	public static BasicServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
 		Properties properties = new Properties();
 		properties.putAll( serviceRegistryConfig );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new ServiceRegistryImpl( properties );
+		return new BasicServiceRegistryImpl( properties );
 	}
 
 	public static void destroy(ServiceRegistry serviceRegistry) {
-		( (ServiceRegistryImpl) serviceRegistry ).destroy();
+		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index a3e41afe09..30a4eb659d 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,401 +1,401 @@
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
 package org.hibernate.testing.junit4;
 
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
 import org.hibernate.cache.HashtableCacheProvider;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
-import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private Configuration configuration;
-	private ServiceRegistryImpl serviceRegistry;
+	private BasicServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
-	protected ServiceRegistryImpl serviceRegistry() {
+	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected Session openSession() throws HibernateException {
 		session = sessionFactory().openSession();
 		return session;
 	}
 
 	protected Session openSession(Interceptor interceptor) throws HibernateException {
 		session = sessionFactory().openSession(interceptor);
 		return session;
 	}
 
 
 	// before/after test class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@BeforeClassOnce
 	private void buildSessionFactory() {
 		configuration = buildConfiguration();
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		addMappings( cfg );
 		cfg.buildMappings();
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 		return cfg;
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration()
 				.setProperty( Environment.CACHE_PROVIDER, HashtableCacheProvider.class.getName() );
 		configuration.setProperty( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return configuration;
 	}
 
 	protected void configure(Configuration configuration) {
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource(
 						getBaseForMappings() + mapping,
 						getClass().getClassLoader()
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				configuration.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				configuration.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				configuration.addInputStream( is );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		// todo : rename to getOrmXmlFiles()
 		return NO_MAPPINGS;
 	}
 
 	protected void applyCacheSettings(Configuration configuration) {
 		if ( getCacheConcurrencyStrategy() != null ) {
 			Iterator itr = configuration.getClassMappings();
 			while ( itr.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) itr.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
 				}
 			}
 			itr = configuration.getCollectionMappings();
 			while ( itr.hasNext() ) {
 				Collection coll = (Collection) itr.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 		afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 	}
 
 	protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 	}
 
-	protected ServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
+	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		ServiceRegistryImpl serviceRegistry = new ServiceRegistryImpl( properties );
+		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( properties );
 		applyServices( serviceRegistry );
 		return serviceRegistry;
 	}
 
-	protected void applyServices(ServiceRegistryImpl serviceRegistry) {
+	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	private void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
 	protected void rebuildSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		serviceRegistry.destroy();
 
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 
 	// before/after each test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 	}
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			if ( session.isConnected() ) {
 				session.doWork( new RollbackWork() );
 			}
 			session.close();
 		}
 		session = null;
 	}
 
 	public class RollbackWork implements Work {
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	protected void cleanupTest() throws Exception {
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing"})
 	protected void assertAllDataRemoved() {
 		if ( !createSchema() ) {
 			return; // no tables were created...
 		}
 		if ( !Boolean.getBoolean( VALIDATE_DATA_CLEANUP ) ) {
 			return;
 		}
 
 		Session tmpSession = sessionFactory.openSession();
 		try {
 			List list = tmpSession.createQuery( "select o from java.lang.Object o" ).list();
 
 			Map<String,Integer> items = new HashMap<String,Integer>();
 			if ( !list.isEmpty() ) {
 				for ( Object element : list ) {
 					Integer l = (Integer) items.get( tmpSession.getEntityName( element ) );
 					if ( l == null ) {
 						l = Integer.valueOf( 0 );
 					}
 					l = Integer.valueOf( l.intValue() + 1 ) ;
 					items.put( tmpSession.getEntityName( element ), l );
 					System.out.println( "Data left: " + element );
 				}
 				fail( "Data is left in the database: " + items.toString() );
 			}
 		}
 		finally {
 			try {
 				tmpSession.close();
 			}
 			catch( Throwable t ) {
 				// intentionally empty
 			}
 		}
 	}
 
 	protected boolean readCommittedIsolationMaintained(String scenario) {
 		int isolation = java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
 		Session testSession = null;
 		try {
 			testSession = openSession();
 			isolation = testSession.connection().getTransactionIsolation();
 		}
 		catch( Throwable ignore ) {
 		}
 		finally {
 			if ( testSession != null ) {
 				try {
 					testSession.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		if ( isolation < java.sql.Connection.TRANSACTION_READ_COMMITTED ) {
 			SkipLog.reportSkip( "environment does not support at least read committed isolation", scenario );
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 
 }
