diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
index 80f4fd6d2d..3f1e498433 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationConfiguration.java
@@ -1,245 +1,252 @@
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
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 
 import org.dom4j.Document;
 
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
 
+	@Override
+	public AnnotationConfiguration setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator) {
+		super.setNamingStrategyDelegator( namingStrategyDelegator );
+		return this;
+	}
+
 	@Deprecated
     protected class ExtendedMappingsImpl extends MappingsImpl {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index f0d1139c00..f8a13e93da 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1352 +1,1355 @@
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
+import org.hibernate.cfg.naming.DefaultNamingStrategyDelegator;
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
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
-	protected NamingStrategy namingStrategy;
+	private NamingStrategyDelegator namingStrategyDelegator;
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
-		namingStrategy = EJB3NamingStrategy.INSTANCE;
+		namingStrategyDelegator = DefaultNamingStrategyDelegator.INSTANCE;
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
@@ -1457,2445 +1460,2500 @@ public class Configuration implements Serializable {
 				buildUniqueKeyFromColumnNames( table, holder.getName(), holder.getColumns(), holder.getOrdering(), holder.isUnique() );
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
 				String sb = "Foreign key circularity dependency involving the following tables: ";
 				throw new AnnotationException( sb );
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
 				// If at least 1 columnName does exist, 'columns' will contain a mix of Columns and nulls.  In order
 				// to exhaustively report all of the unbound columns at once, w/o an NPE in
 				// Constraint#generateName's array sorting, simply create a fake Column.
 				columns[index] = new Column( column );
 				unboundNoLogical.add( columns[index] );
 			}
 		}
 		
 		if ( StringHelper.isEmpty( keyName ) ) {
 			keyName = Constraint.generateName( "UK_", table, columns );
 		}
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 		
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
 
 	public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
 		return namedProcedureCallMap;
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
 		
 		buildTypeRegistrations( serviceRegistry );
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
 	
 	private void buildTypeRegistrations(ServiceRegistry serviceRegistry) {
 		final TypeContributions typeContributions = new TypeContributions() {
 			@Override
 			public void contributeType(BasicType type) {
 				typeResolver.registerTypeOverride( type );
 			}
 
 			@Override
 			public void contributeType(UserType type, String[] keys) {
 				typeResolver.registerTypeOverride( type, keys );
 			}
 
 			@Override
 			public void contributeType(CompositeUserType type, String[] keys) {
 				typeResolver.registerTypeOverride( type, keys );
 			}
 		};
 
 		// add Dialect contributed types
 		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
 		dialect.contributeTypes( typeContributions, serviceRegistry );
 
 		// add TypeContributor contributed types.
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		for ( TypeContributor contributor : classLoaderService.loadJavaServices( TypeContributor.class ) ) {
 			contributor.contribute( typeContributions, serviceRegistry );
 		}
 		// from app registrations
 		for ( TypeContributor contributor : typeContributorRegistrations ) {
 			contributor.contribute( typeContributions, serviceRegistry );
 		}
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
 			throw new MappingException( "You may only specify a cache for root <class> mappings.  Attempted on " + clazz );
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
-		return namingStrategy;
+		if ( LegacyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) ) {
+			return ( (LegacyNamingStrategyDelegator) namingStrategyDelegator ).getNamingStrategy();
+		}
+		return null;
+	}
+
+	public NamingStrategyDelegator getNamingStrategyDelegator() {
+		return namingStrategyDelegator;
 	}
 
 	/**
-	 * Set a custom naming strategy
+	 * Set the current naming strategy. An instance of {@link org.hibernate.cfg.naming.LegacyNamingStrategyDelegator}
+	 * will be constructed with the specified naming strategy.
 	 *
-	 * @param namingStrategy the NamingStrategy to set
+	 * @param namingStrategy the {@link NamingStrategy} to set; must be non-null.
 	 *
-	 * @return this for method chaining
+	 * @return this for method chaining.
+	 *
+	 * @see org.hibernate.cfg.naming.LegacyNamingStrategyDelegator#LegacyNamingStrategyDelegator(NamingStrategy)
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
-		this.namingStrategy = namingStrategy;
+		if ( namingStrategy == null ) {
+			throw new MappingException( "namingStrategy must be non-null" );
+		}
+		setNamingStrategyDelegator( new LegacyNamingStrategyDelegator( namingStrategy ) );
+		return this;
+	}
+
+	/**
+	 * Set a current naming strategy delegator.
+	 *
+	 * @param namingStrategyDelegator the {@link org.hibernate.cfg.naming.NamingStrategyDelegator} to set;
+	 *                                must be non-null; if {@code namingStrategyDelegator} is an instance
+	 *                                of {@link LegacyNamingStrategyDelegator}, then
+	 *                                {@link LegacyNamingStrategyDelegator#getNamingStrategy()} must be non-null.
+	 * @return this for method chaining.
+	 *
+	 * @throws org.hibernate.MappingException if {@code namingStrategyDelegator} is null.
+	 * @throws org.hibernate.MappingException if {@code namingStrategyDelegator} is an instance
+	 *         of {@link LegacyNamingStrategyDelegator} and   {@link LegacyNamingStrategyDelegator#getNamingStrategy()}
+	 *         is null.
+	 */
+	public Configuration setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator) {
+		if ( namingStrategyDelegator == null ) {
+			throw new MappingException( "namingStrategyDelegator and namingStrategyDelegator.getNamingStrategy() must be non-null" );
+		}
+		if ( LegacyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) &&
+				LegacyNamingStrategyDelegator.class.cast( namingStrategyDelegator ).getNamingStrategy() == null ) {
+			throw new MappingException( "namingStrategyDelegator.getNamingStrategy() must be non-null." );
+		}
+		this.namingStrategyDelegator = namingStrategyDelegator;
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
 
 	public void registerTypeContributor(TypeContributor typeContributor) {
 		typeContributorRegistrations.add( typeContributor );
 	}
 
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
 		addAttributeConverter(
 				instantiateAttributeConverter( attributeConverterClass ),
 				autoApply
 		);
 	}
 
 	private AttributeConverter instantiateAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
 		AttributeConverter attributeConverter;
 		try {
 			attributeConverter = attributeConverterClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException(
 					"Unable to instantiate AttributeConverter [" + attributeConverterClass.getName() + "]",
 					e
 			);
 		}
 		return attributeConverter;
 	}
 
 	/**
 	 * Adds the AttributeConverter Class to this Configuration.
 	 *
 	 * @param attributeConverterClass The AttributeConverter class.
 	 */
 	public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
 		addAttributeConverter( instantiateAttributeConverter( attributeConverterClass ) );
 	}
 
 	/**
 	 * Adds the AttributeConverter instance to this Configuration.  This form is mainly intended for developers
 	 * to programatically add their own AttributeConverter instance.  HEM, instead, uses the
 	 * {@link #addAttributeConverter(Class, boolean)} form
 	 *
 	 * @param attributeConverter The AttributeConverter instance.
 	 */
 	public void addAttributeConverter(AttributeConverter attributeConverter) {
 		boolean autoApply = false;
 		Converter converterAnnotation = attributeConverter.getClass().getAnnotation( Converter.class );
 		if ( converterAnnotation != null ) {
 			autoApply = converterAnnotation.autoApply();
 		}
 
 		addAttributeConverter( new AttributeConverterDefinition( attributeConverter, autoApply ) );
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
 		addAttributeConverter( new AttributeConverterDefinition( attributeConverter, autoApply ) );
 	}
 
 	public void addAttributeConverter(AttributeConverterDefinition definition) {
 		if ( attributeConverterDefinitionsByClass == null ) {
 			attributeConverterDefinitionsByClass = new ConcurrentHashMap<Class, AttributeConverterDefinition>();
 		}
 
 		final Object old = attributeConverterDefinitionsByClass.put( definition.getAttributeConverter().getClass(), definition );
 
 		if ( old != null ) {
 			throw new AssertionFailure(
 					String.format(
 							"AttributeConverter class [%s] registered multiple times",
 							definition.getAttributeConverter().getClass()
 					)
 			);
 		}
 	}
 
 	public java.util.Collection<NamedEntityGraphDefinition> getNamedEntityGraphs() {
 		return namedEntityGraphMap == null
 				? Collections.<NamedEntityGraphDefinition>emptyList()
 				: namedEntityGraphMap.values();
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
-			return namingStrategy;
+			return Configuration.this.getNamingStrategy();
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
-			Configuration.this.namingStrategy = namingStrategy;
+			Configuration.this.setNamingStrategy( namingStrategy );
+		}
+
+		public NamingStrategyDelegator getNamingStrategyDelegator() {
+			return Configuration.this.getNamingStrategyDelegator();
+		}
+
+		public void setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator) {
+			Configuration.this.setNamingStrategyDelegator( namingStrategyDelegator );
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
 			return tables.get( key );
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
 
 		@Override
 		public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition)
 				throws DuplicateMappingException {
 			final String name = definition.getRegisteredName();
 			if ( !defaultNamedProcedure.contains( name ) ) {
 				final NamedProcedureCallDefinition previous = namedProcedureCallMap.put( name, definition );
 				if ( previous != null ) {
 					throw new DuplicateMappingException( "named stored procedure query", name );
 				}
 			}
 		}
 		@Override
 		public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition)
 				throws DuplicateMappingException {
 			addNamedProcedureCallDefinition( definition );
 			defaultNamedProcedure.add( definition.getRegisteredName() );
 		}
 
 		@Override
 		public void addNamedEntityGraphDefintion(NamedEntityGraphDefinition definition)
 				throws DuplicateMappingException {
 			final String name = definition.getRegisteredName();
 
 			final NamedEntityGraphDefinition previous = namedEntityGraphMap.put( name, definition );
 			if ( previous != null ) {
 				throw new DuplicateMappingException( "NamedEntityGraph", name );
 			}
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
 							" Table [" + tableName + "] contains physical column name [" + physicalName
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
 		@Override
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
 			while ( logical == null && currentTable != null );
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
 
 		@Override
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings;
 		}
 
 
 		private Boolean implicitDiscriminatorColumnForJoinedInheritance;
 
 		@Override
 		public boolean useImplicitDiscriminatorColumnForJoinedInheritance() {
 			if ( implicitDiscriminatorColumnForJoinedInheritance == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS );
 				implicitDiscriminatorColumnForJoinedInheritance = Boolean.valueOf( booleanName );
 			}
 			return implicitDiscriminatorColumnForJoinedInheritance;
 		}
 
 
 		private Boolean ignoreExplicitDiscriminatorColumnForJoinedInheritance;
 
 		@Override
 		public boolean ignoreExplicitDiscriminatorColumnForJoinedInheritance() {
 			if ( ignoreExplicitDiscriminatorColumnForJoinedInheritance == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS );
 				ignoreExplicitDiscriminatorColumnForJoinedInheritance = Boolean.valueOf( booleanName );
 			}
 			return ignoreExplicitDiscriminatorColumnForJoinedInheritance;
 		}
 
 
 		private Boolean useNationalizedCharacterData;
 
 		@Override
 		public boolean useNationalizedCharacterData() {
 			if ( useNationalizedCharacterData == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.USE_NATIONALIZED_CHARACTER_DATA );
 				useNationalizedCharacterData = Boolean.valueOf( booleanName );
 			}
 			return useNationalizedCharacterData;
 		}
 
 		private Boolean forceDiscriminatorInSelectsByDefault;
 
 		@Override
 		public boolean forceDiscriminatorInSelectsByDefault() {
 			if ( forceDiscriminatorInSelectsByDefault == null ) {
 				final String booleanName = getConfigurationProperties()
 						.getProperty( AvailableSettings.FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT );
 				forceDiscriminatorInSelectsByDefault = Boolean.valueOf( booleanName );
 			}
 			return forceDiscriminatorInSelectsByDefault;
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
 			return setting != null && Boolean.valueOf( setting );
 		}
 
 		public NamingStrategy getNamingStrategy() {
-			return namingStrategy;
+			if ( LegacyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) ) {
+				( (LegacyNamingStrategyDelegator) namingStrategyDelegator ).getNamingStrategy();
+			}
+			return null;
+		}
+
+		@Override
+		protected NamingStrategyDelegator getNamingStrategyDelegator() {
+			return namingStrategyDelegator;
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
 			LOG.debug( "Processing hbm.xml files" );
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), Collections.EMPTY_MAP, entityNames );
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
 			LOG.debug( "Process annotated classes" );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
index a70e1ccaa1..e0a86fce95 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3Column.java
@@ -1,692 +1,703 @@
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
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
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
-									mappings.getNamingStrategy().propertyToColumnName( propertyName )
+									getNamingStrategyDelegate().determineAttributeColumnName( propertyName )
 							)
 					);
 				}
 				//Do nothing otherwise
 			}
 			else {
 				columnName = mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( columnName );
-				columnName = mappings.getNamingStrategy().columnName( columnName );
+				columnName = getNamingStrategyDelegate().toPhysicalColumnName( columnName );
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
-		String logicalColumnName = mappings.getNamingStrategy()
-				.logicalColumnName( this.logicalColumnName, propertyName );
+		String logicalColumnName = getNamingStrategyDelegate().logicalColumnName( this.logicalColumnName, propertyName );
 		mappings.addColumnBinding( logicalColumnName, getMappingColumn(), value.getTable() );
 	}
 
+	protected NamingStrategyDelegate getNamingStrategyDelegate() {
+		return getNamingStrategyDelegate( mappings );
+	}
+
+	protected static NamingStrategyDelegate getNamingStrategyDelegate(Mappings mappings) {
+		return mappings.getNamingStrategyDelegator().getNamingStrategyDelegate( false );
+	}
+
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
-					final String tableName = ! StringHelper.isEmpty(col.table())
-                                             ? nameNormalizer.normalizeIdentifierQuoting( mappings.getNamingStrategy().tableName( col.table() ) )
-                                             : "";
+					final String tableName =
+							! StringHelper.isEmpty(col.table())
+									? nameNormalizer.normalizeIdentifierQuoting( getNamingStrategyDelegate( mappings ).toPhysicalTableName(
+											col.table()
+									) )
+									: "";
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
index d7f786e9ff..e595a1c45d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Ejb3JoinColumn.java
@@ -1,713 +1,732 @@
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
+	private String mappedByJpaEntityName;
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
-					? nameNormalizer.normalizeIdentifierQuoting( getMappings().getNamingStrategy().tableName( annJoin.table() ) ) : "";
+					? nameNormalizer.normalizeIdentifierQuoting( getNamingStrategyDelegate().toPhysicalTableName(
+							annJoin.table()
+					) )
+					: "";
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
-			final String ownerObjectName = JPA2ElementCollection && mappedByEntityName != null ?
-				StringHelper.unqualify( mappedByEntityName ) : unquotedMappedbyTable;
-			columnName = getMappings().getNamingStrategy().foreignKeyColumnName(
-					mappedByPropertyName,
-					mappedByEntityName,
-					ownerObjectName,
-					unquotedLogicalReferenceColumn
-			);
+			if ( JPA2ElementCollection ) {
+				columnName = getNamingStrategyDelegate().determineElementCollectionForeignKeyColumnName(
+						mappedByPropertyName,
+						mappedByEntityName,
+						mappedByJpaEntityName,
+						unquotedMappedbyTable,
+						unquotedLogicalReferenceColumn
+				);
+			}
+			else {
+				columnName = getNamingStrategyDelegate().determineEntityAssociationForeignKeyColumnName(
+						mappedByPropertyName,
+						mappedByEntityName,
+						mappedByJpaEntityName,
+						unquotedMappedbyTable,
+						unquotedLogicalReferenceColumn
+				);
+			}
 			//one element was quoted so we quote
 			if ( isRefColumnQuoted || StringHelper.isQuoted( mappedByTableName ) ) {
 				columnName = StringHelper.quote( columnName );
 			}
 		}
 		else if ( ownerSide ) {
 			String logicalTableName = getMappings().getLogicalTableName( referencedEntity.getTable() );
 			String unquotedLogicalTableName = StringHelper.unquote( logicalTableName );
-			columnName = getMappings().getNamingStrategy().foreignKeyColumnName(
+			columnName = getNamingStrategyDelegate().determineEntityAssociationForeignKeyColumnName(
 					getPropertyName(),
 					referencedEntity.getEntityName(),
+					referencedEntity.getJpaEntityName(),
 					unquotedLogicalTableName,
 					unquotedLogicalReferenceColumn
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
-			columnName = getMappings().getNamingStrategy().joinKeyColumnName(
+			columnName = getNamingStrategyDelegate().determineJoinKeyColumnName(
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
-			String logicalCollectionColumnName = getMappings().getNamingStrategy()
-					.logicalCollectionColumnName( unquotedLogColName, getPropertyName(), unquotedRefColumn );
+			String logicalCollectionColumnName = getNamingStrategyDelegate().logicalCollectionColumnName(
+					unquotedLogColName,
+					getPropertyName(),
+					unquotedRefColumn
+			);
 			
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
-							getMappings().getNamingStrategy().columnName( columnName ) :
+							getNamingStrategyDelegate().toPhysicalColumnName( columnName ) :
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
 
-	public void setMappedBy(String entityName, String logicalTableName, String mappedByProperty) {
+	public void setMappedBy(String entityName, String jpaEntityName, String logicalTableName, String mappedByProperty) {
 		this.mappedByEntityName = entityName;
+		this.mappedByJpaEntityName = jpaEntityName;
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
index 949459ae03..d5b2120bee 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1,2499 +1,2519 @@
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
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
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
-			physicalTableName = mappings.getNamingStrategy().classToTableName( model.getEntityName() );
+			physicalTableName = getNamingStrategyDelegate( mappings ).determinePrimaryTableLogicalName(
+					model.getEntityName(),
+					model.getJpaEntityName()
+			);
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
-			physicalTableName = mappings.getNamingStrategy().tableName( logicalTableName );
+			physicalTableName = getNamingStrategyDelegate( mappings ).toPhysicalTableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
+	private static NamingStrategyDelegate getNamingStrategyDelegate(Mappings mappings) {
+		return  mappings.getNamingStrategyDelegator().getNamingStrategyDelegate( true );
+	}
+
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
-					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
+					String logicalColumnName = getNamingStrategyDelegate( mappings ).logicalColumnName(
 							columnName, propertyPath
 					);
-					columnName = mappings.getNamingStrategy().columnName( columnName );
+					columnName = getNamingStrategyDelegate( mappings ).toPhysicalColumnName( columnName );
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
-			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
+			String logicalColumnName = getNamingStrategyDelegate( mappings ).logicalColumnName(
 					columnName, propertyPath
 			);
-			columnName = mappings.getNamingStrategy().columnName( columnName );
+			columnName = getNamingStrategyDelegate( mappings ).toPhysicalColumnName( columnName );
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
-			String columnName = mappings.getNamingStrategy().propertyToColumnName( propertyPath );
+			String columnName = getNamingStrategyDelegate( mappings ).determineAttributeColumnName( propertyPath );
 			columnName = quoteIdentifier( columnName, mappings );
 			column.setName( columnName );
-			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
+			String logicalName = getNamingStrategyDelegate( mappings ).logicalColumnName( null, propertyPath );
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
-				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
+				tableName = getNamingStrategyDelegate( mappings ).toPhysicalTableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
-				tableName = mappings.getNamingStrategy().collectionTableName(
-						collection.getOwner().getEntityName(),
-						logicalOwnerTableName ,
-						null,
-						null,
-						path
-				);
+				if ( node.element( "element" ) != null || node.element( "composite-element" ) != null ) {
+					tableName = getNamingStrategyDelegate( mappings ).determineElementCollectionTableLogicalName(
+							collection.getOwner().getClassName(),
+							collection.getOwner().getEntityName(),
+							logicalOwnerTableName,
+							path
+					);
+				}
+				else {
+					tableName = getNamingStrategyDelegate( mappings ).determineEntityAssociationJoinTableLogicalName(
+							collection.getOwner().getEntityName(),
+							collection.getOwner().getJpaEntityName(),
+							logicalOwnerTableName,
+							null,
+							null,
+							null,
+							path
+					);
+				}
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
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
index a208247be5..24751a98f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Mappings.java
@@ -1,838 +1,866 @@
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
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
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
+	 *
+	 * @deprecated Use {@link #getNamingStrategyDelegator()} instead.
 	 */
+	@Deprecated
 	public NamingStrategy getNamingStrategy();
 
 	/**
-	 * Set the current naming strategy.
+	 * Set the current naming strategy. An instance of {@link org.hibernate.cfg.naming.LegacyNamingStrategyDelegator}
+	 * will be constructed with the specified naming strategy.
 	 *
-	 * @param namingStrategy The naming strategy to use.
+	 * @param namingStrategy the {@link NamingStrategy} to set; must be non-null.
+	 *
+	 * @deprecated Use {@link #setNamingStrategyDelegator(org.hibernate.cfg.naming.NamingStrategyDelegator)} instead.
+
+	 * @see org.hibernate.cfg.naming.LegacyNamingStrategyDelegator#LegacyNamingStrategyDelegator(NamingStrategy)
 	 */
+	@Deprecated
 	public void setNamingStrategy(NamingStrategy namingStrategy);
 
 	/**
+	 * Get the current naming strategy delegate.
+	 *
+	 * @return The current naming strategy delegate.
+	 */
+	public NamingStrategyDelegator getNamingStrategyDelegator();
+
+	/**
+	 * Set a current naming strategy delegator.
+	 *
+	 * @param namingStrategyDelegator the {@link org.hibernate.cfg.naming.NamingStrategyDelegator} to set;
+	 *                                must be non-null; if {@code namingStrategyDelegator} is an instance
+	 *                                of {@link org.hibernate.cfg.naming.LegacyNamingStrategyDelegator}, then
+	 *                                {@link org.hibernate.cfg.naming.LegacyNamingStrategyDelegator#getNamingStrategy()}
+	 *                                must be non-null.
+	 */
+	public void setNamingStrategyDelegator(NamingStrategyDelegator namingStrategyDelegator);
+
+	/**
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
 	 * Adds metadata for a named stored procedure call to this repository.
 	 *
 	 * @param definition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
 
 	/**
 	 * Adds metadata for a named stored procedure call to this repository.
 	 *
 	 * @param definition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If a query already exists with that name.
 	 */
 	public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) throws DuplicateMappingException;
 
 
 
 	/**
 	 * Adds metadata for a named entity graph to this repository
 	 *
 	 * @param namedEntityGraphDefinition The procedure call information
 	 *
 	 * @throws DuplicateMappingException If an entity graph already exists with that name.
 	 */
 	public void addNamedEntityGraphDefintion(NamedEntityGraphDefinition namedEntityGraphDefinition);
 
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
 	 * Locate the AttributeConverterDefinition corresponding to the given AttributeConverter Class.
 	 *
 	 * @param attributeConverterClass The AttributeConverter Class for which to get the definition
 	 *
 	 * @return The corresponding AttributeConverter definition; will return {@code null} if no corresponding
 	 * definition found.
 	 */
 	public AttributeConverterDefinition locateAttributeConverter(Class attributeConverterClass);
 
 	/**
 	 * All all AttributeConverter definitions
 	 *
 	 * @return The collection of all AttributeConverter definitions.
 	 */
 	public java.util.Collection<AttributeConverterDefinition> getAttributeConverters();
 
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
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory();
 
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
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public Map<Table, List<String[]>> getTableUniqueConstraints();
 
 	public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable();
 
 	/**
 	 * @deprecated Use {@link #addUniqueConstraintHolders} instead
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public void addUniqueConstraints(Table table, List uniqueConstraints);
 
 	public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders);
 
 	public void addJpaIndexHolders(Table table, List<JPAIndexHolder> jpaIndexHolders);
 
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
 	 * {@link AvailableSettings#USE_NEW_ID_GENERATOR_MAPPINGS} setting.
 	 *
 	 * @return True if the new generators should be used, false otherwise.
 	 */
 	public boolean useNewGeneratorMappings();
 
 	/**
 	 * Should we handle absent DiscriminatorColumn mappings for joined inheritance by implicitly mapping a
 	 * discriminator column?
 	 *
 	 * @return {@code true} indicates we should infer DiscriminatorColumn implicitly (aka, map to a discriminator
 	 * column even without a DiscriminatorColumn annotation); {@code false} (the default) indicates that we should not.
 	 *
 	 * @see AvailableSettings#IMPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	public boolean useImplicitDiscriminatorColumnForJoinedInheritance();
 
 	/**
 	 * Should we ignore explicit DiscriminatorColumn annotations when combined with joined inheritance?
 	 *
 	 * @return {@code true} indicates we should ignore explicit DiscriminatorColumn annotations; {@code false} (the
 	 * default) indicates we should not ignore them
 	 *
 	 * @see AvailableSettings#IGNORE_EXPLICIT_DISCRIMINATOR_COLUMNS_FOR_JOINED_SUBCLASS
 	 */
 	public boolean ignoreExplicitDiscriminatorColumnForJoinedInheritance();
 
 	/**
 	 * Should we use nationalized variants of character data by default?  This is controlled by the
 	 * {@link AvailableSettings#USE_NATIONALIZED_CHARACTER_DATA} setting.
 	 *
 	 * @return {@code true} if nationalized character data should be used by default; {@code false} otherwise.
 	 */
 	public boolean useNationalizedCharacterData();
 
 	/**
 	 * Return the property annotated with @ToOne and @Id if any.
 	 * Null otherwise
 	 */
 	public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName);
 
 	void addToOneAndIdProperty(XClass entity, PropertyData property);
 
 	public boolean forceDiscriminatorInSelectsByDefault();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
index c78e0f2f03..9c1d4a53b3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamingStrategy.java
@@ -1,124 +1,126 @@
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
+ *
+ * @deprecated
  */
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
index ef629ccecb..6e07e55009 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ObjectNameNormalizer.java
@@ -1,139 +1,175 @@
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
 
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Provides centralized normalization of how database object names are handled.
  *
  * @author Steve Ebersole
  */
 public abstract class ObjectNameNormalizer {
 
 	/**
 	 * Helper contract for dealing with {@link NamingStrategy} in different situations.
 	 */
 	public static interface NamingStrategyHelper {
 		/**
 		 * Called when the user supplied no explicit name/identifier for the given database object.
 		 *
 		 * @param strategy The naming strategy in effect
 		 *
 		 * @return The implicit name
+		 *
+		 * * @deprecated Replaced by {@link #determineImplicitName(org.hibernate.cfg.naming.NamingStrategyDelegator)}.
 		 */
+		@Deprecated
 		public String determineImplicitName(NamingStrategy strategy);
 
 		/**
 		 * Called when the user has supplied an explicit name for the database object.
 		 *
 		 * @param strategy The naming strategy in effect
 		 * @param name The {@link ObjectNameNormalizer#normalizeIdentifierQuoting normalized} explicit object name.
 		 *
 		 * @return The strategy-handled name.
+		 *
+		 * @deprecated Replaced by {@link #determineImplicitName(org.hibernate.cfg.naming.NamingStrategyDelegator)}.
 		 */
+		@Deprecated
 		public String handleExplicitName(NamingStrategy strategy, String name);
+
+		/**
+		 * Called when the user supplied no explicit name/identifier for the given database object.
+		 *
+		 * @param strategyDelegator The naming strategy delegator in effect
+		 *
+		 * @return The implicit name
+		 */
+		public String determineImplicitName(NamingStrategyDelegator strategyDelegator);
+
+		/**
+		 * Called when the user has supplied an explicit name for the database object.
+		 *
+		 * @param strategyDelegator The naming strategy delegator in effect
+		 * @param name The {@link ObjectNameNormalizer#normalizeIdentifierQuoting normalized} explicit object name.
+		 *
+		 * @return The strategy-handled name.
+		 */
+		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name);
 	}
 
 	/**
 	 * Performs the actual contract of normalizing a database name.
 	 *
 	 * @param explicitName The name the user explicitly gave for the database object.
 	 * @param helper The {@link NamingStrategy} helper.
 	 *
 	 * @return The normalized identifier.
 	 */
 	public String normalizeDatabaseIdentifier(final String explicitName, NamingStrategyHelper helper) {
 		String objectName = null;
 		// apply naming strategy
 		if ( StringHelper.isEmpty( explicitName ) ) {
 			// No explicit name given, so allow the naming strategy the chance
 			//    to determine it based on the corresponding mapped java name
-			objectName = helper.determineImplicitName( getNamingStrategy() );
+			objectName = helper.determineImplicitName( getNamingStrategyDelegator() );
 		}
 		else {
 			// An explicit name was given:
 			//    in some cases we allow the naming strategy to "fine tune" these, but first
 			//    handle any quoting for consistent handling in naming strategies
 			objectName = normalizeIdentifierQuoting( explicitName );
-			objectName = helper.handleExplicitName( getNamingStrategy(), objectName );
+			objectName = helper.handleExplicitName( getNamingStrategyDelegator(), objectName );
 			return normalizeIdentifierQuoting( objectName );
 		}
         // Conceivable that the naming strategy could return a quoted identifier, or
 			//    that user enabled <delimited-identifiers/>
 		return normalizeIdentifierQuoting( objectName );
 	}
 
 	/**
 	 * Allow normalizing of just the quoting aspect of identifiers.  This is useful for
 	 * schema and catalog in terms of initially making this public.
 	 * <p/>
 	 * This implements the rules set forth in JPA 2 (section "2.13 Naming of Database Objects") which
 	 * states that the double-quote (") is the character which should be used to denote a <tt>quoted
 	 * identifier</tt>.  Here, we handle recognizing that and converting it to the more elegant
 	 * bactick (`) approach used in Hibernate..  Additionally we account for applying what JPA2 terms
 	 *  
 	 *
 	 * @param identifier The identifier to be quoting-normalized.
 	 * @return The identifier accounting for any quoting that need be applied.
 	 */
 	public String normalizeIdentifierQuoting(String identifier) {
 		if ( StringHelper.isEmpty( identifier ) ) {
 			return null;
 		}
 
 		// Convert the JPA2 specific quoting character (double quote) to Hibernate's (back tick)
 		if ( identifier.startsWith( "\"" ) && identifier.endsWith( "\"" ) ) {
 			return '`' + identifier.substring( 1, identifier.length() - 1 ) + '`';
 		}
 
 		// Convert SQLServer style quoting
 		// TODO: This really should be tied to Dialect#openQuote/closeQuote
 		if ( identifier.startsWith( "[" ) && identifier.endsWith( "]" ) ) {
 			return '`' + identifier.substring( 1, identifier.length() - 1 ) + '`';
 		}
 
 		// If the user has requested "global" use of quoted identifiers, quote this identifier (using back ticks)
 		// if not already
 		if ( isUseQuotedIdentifiersGlobally() && ! ( identifier.startsWith( "`" ) && identifier.endsWith( "`" ) ) ) {
 			return '`' + identifier + '`';
 		}
 
 		return identifier;
 	}
 
 	/**
 	 * Retrieve whether the user requested that all database identifiers be quoted.
 	 *
 	 * @return True if the user requested that all database identifiers be quoted, false otherwise.
 	 */
 	protected abstract boolean isUseQuotedIdentifiersGlobally();
 
 	/**
 	 * Get the current {@link NamingStrategy}.
 	 *
 	 * @return The current {@link NamingStrategy}.
+	 *
+	 * @deprecated Replaced by {@link #getNamingStrategyDelegator()}
 	 */
+	@Deprecated
 	protected abstract NamingStrategy getNamingStrategy();
+
+	/**
+	 * Get the current {@link org.hibernate.cfg.naming.NamingStrategyDelegator}.
+	 *
+	 * @return The current {@link org.hibernate.cfg.naming.NamingStrategyDelegator}.
+	 */
+	protected abstract NamingStrategyDelegator getNamingStrategyDelegator();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index c45a6fd307..acf8cbe99a 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -208,1362 +208,1366 @@ public abstract class CollectionBinder {
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
 
 	public void setJpaOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		this.jpaOrderBy = jpaOrderBy;
 	}
 
 	public void setSqlOrderBy(OrderBy sqlOrderBy) {
 		this.sqlOrderBy = sqlOrderBy;
 	}
 
 	public void setSort(Sort deprecatedSort) {
 		this.deprecatedSort = deprecatedSort;
 	}
 
 	public void setNaturalSort(SortNatural naturalSort) {
 		this.naturalSort = naturalSort;
 	}
 
 	public void setComparatorSort(SortComparator comparatorSort) {
 		this.comparatorSort = comparatorSort;
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping,
 			Mappings mappings) {
 		final CollectionBinder result;
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
 				result = new SetBinder( false );
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
 				result = new MapBinder( false );
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
 
 		final CollectionType typeAnnotation = property.getAnnotation( CollectionType.class );
 		if ( typeAnnotation != null ) {
 			final String typeName = typeAnnotation.type();
 			// see if it names a type-def
 			final TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				result.explicitType = typeDef.getTypeClass();
 				result.explicitTypeParameters.putAll( typeDef.getParameters() );
 			}
 			else {
 				result.explicitType = typeName;
 				for ( Parameter param : typeAnnotation.parameters() ) {
 					result.explicitTypeParameters.setProperty( param.name(), param.value() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	protected CollectionBinder(boolean isSortedCollection) {
 		this.isSortedCollection = isSortedCollection;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	public void setTableBinder(TableBinder tableBinder) {
 		this.tableBinder = tableBinder;
 	}
 
 	public void setCollectionType(XClass collectionType) {
 		// NOTE: really really badly named.  This is actually NOT the collection-type, but rather the collection-element-type!
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
 		String role = StringHelper.qualify( propertyHolder.getPath(), propertyName );
 		LOG.debugf( "Collection role: %s", role );
 		collection.setRole( role );
 		collection.setNodeName( propertyName );
 		collection.setMappedByProperty( mappedBy );
 
 		if ( property.isAnnotationPresent( MapKeyColumn.class )
 			&& mapKeyPropertyName != null ) {
 			throw new AnnotationException(
 					"Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey "
 							+ "on the same collection: " + StringHelper.qualify(
 							propertyHolder.getPath(), propertyName
 					)
 			);
 		}
 
 		// set explicit type information
 		if ( explicitType != null ) {
 			final TypeDef typeDef = mappings.getTypeDef( explicitType );
 			if ( typeDef == null ) {
 				collection.setTypeName( explicitType );
 				collection.setTypeParameters( explicitTypeParameters );
 			}
 			else {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 
 		collection.setMutable( !property.isAnnotationPresent( Immutable.class ) );
 
 		//work on association
 		boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue( mappedBy );
 
 		final OptimisticLock lockAnn = property.getAnnotation( OptimisticLock.class );
 		final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 				? ! lockAnn.excluded()
 				: ! isMappedBy;
 		collection.setOptimisticLocked( includeInOptimisticLockChecks );
 
 		Persister persisterAnn = property.getAnnotation( Persister.class );
 		if ( persisterAnn != null ) {
 			collection.setCollectionPersisterClass( persisterAnn.impl() );
 		}
 
 		applySortingAndOrdering( collection );
 
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
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
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
 
 	private void applySortingAndOrdering(Collection collection) {
 		boolean isSorted = isSortedCollection;
 
 		boolean hadOrderBy = false;
 		boolean hadExplicitSort = false;
 
 		Class<? extends Comparator> comparatorClass = null;
 
 		if ( jpaOrderBy == null && sqlOrderBy == null ) {
 			if ( deprecatedSort != null ) {
 				LOG.debug( "Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead." );
 				if ( naturalSort != null || comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = deprecatedSort.type() != SortType.UNSORTED;
 				if ( deprecatedSort.type() == SortType.NATURAL ) {
 					isSorted = true;
 				}
 				else if ( deprecatedSort.type() == SortType.COMPARATOR ) {
 					isSorted = true;
 					comparatorClass = deprecatedSort.comparator();
 				}
 			}
 			else if ( naturalSort != null ) {
 				if ( comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = true;
 			}
 			else if ( comparatorSort != null ) {
 				hadExplicitSort = true;
 				comparatorClass = comparatorSort.value();
 			}
 		}
 		else {
 			if ( jpaOrderBy != null && sqlOrderBy != null ) {
 				throw new AnnotationException(
 						String.format(
 								"Illegal combination of @%s and @%s on %s",
 								javax.persistence.OrderBy.class.getName(),
 								OrderBy.class.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 
 			hadOrderBy = true;
 			hadExplicitSort = false;
 
 			// we can only apply the sql-based order by up front.  The jpa order by has to wait for second pass
 			if ( sqlOrderBy != null ) {
 				collection.setOrderBy( sqlOrderBy.clause() );
 			}
 		}
 
 		if ( isSortedCollection ) {
 			if ( ! hadExplicitSort && !hadOrderBy ) {
 				throw new AnnotationException(
 						"A sorted collection must define and ordering or sorting : " + safeCollectionRole()
 				);
 			}
 		}
 
 		collection.setSorted( isSortedCollection || hadExplicitSort );
 
 		if ( comparatorClass != null ) {
 			try {
 				collection.setComparator( comparatorClass.newInstance() );
 			}
 			catch (Exception e) {
 				throw new AnnotationException(
 						String.format(
 								"Could not instantiate comparator class [%s] for %s",
 								comparatorClass.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 		}
 	}
 
 	private AnnotationException buildIllegalSortCombination() {
 		return new AnnotationException(
 				String.format(
 						"Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used",
 						safeCollectionRole(),
 						Sort.class.getName(),
 						SortNatural.class.getName(),
 						SortComparator.class.getName()
 				)
 		);
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
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
 			@Override
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
 					ignoreNotFound,
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
 					associationTableBinder,
 					property,
 					propertyHolder,
 					mappings
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
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( mappings, collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		if ( jpaOrderBy != null ) {
 			final String orderByFragment = buildOrderByClauseFromHql(
 					jpaOrderBy.value(),
 					associatedClass,
 					collection.getRole()
 			);
 			if ( StringHelper.isNotEmpty( orderByFragment ) ) {
 				collection.setOrderBy( orderByFragment );
 			}
 		}
 
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
 		if ( debugEnabled ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref" );
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
 				collection.addManyToManyFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 			else {
 				collection.addFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					collection.addFilter(filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter(simpleFilterJoinTable.name(), simpleFilterJoinTable.condition(), 
 						simpleFilterJoinTable.deduceAliasInjectionPoints(), 
 						toAliasTableMap(simpleFilterJoinTable.aliases()), toAliasEntityMap(simpleFilterJoinTable.aliases()));
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
 					collection.addFilter(filter.name(), filter.condition(), 
 							filter.deduceAliasInjectionPoints(), 
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
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
 
 	private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
 		if ( orderByFragment != null ) {
 			if ( orderByFragment.length() == 0 ) {
 				//order by id
 				return "id asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "id desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
 		if ( orderByFragment != null ) {
 			// NOTE: "$element$" is a specially recognized collection property recognized by the collection persister
 			if ( orderByFragment.length() == 0 ) {
 				//order by element
 				return "$element$ asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "$element$ desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static SimpleValue buildCollectionKey(
 			Collection collValue,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			Mappings mappings) {
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
 					.getReferencedProperty( propRef )
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
 			Mappings mappings) throws MappingException {
 		if ( property == null ) {
 			throw new IllegalArgumentException( "null was passed for argument property" );
 		}
 
 		final PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		final String hqlOrderBy = extractHqlOrderBy( jpaOrderBy );
 
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
         if (LOG.isDebugEnabled()) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
             if (isCollectionOfEntities && unique) LOG.debugf("Binding a OneToMany: %s through an association table", path);
             else if (isCollectionOfEntities) LOG.debugf("Binding as ManyToMany: %s", path);
             else if (anyAnn != null) LOG.debugf("Binding a ManyToAny: %s", path);
             else LOG.debugf("Binding a collection of element: %s", path);
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
-						collValue.getOwner().getEntityName(), mappings.getLogicalTableName( ownerTable ),
+						collValue.getOwner().getEntityName(),
+						collValue.getOwner().getJpaEntityName(),
+						mappings.getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getEntityName(),
+						collValue.getOwner().getJpaEntityName(),
 						mappings.getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
+						collectionEntity != null ? collectionEntity.getJpaEntityName() : null,
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
 			// as per 11.1.38 of JPA 2.0 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			String fkName = fk != null ? fk.inverseName() : "";
 			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) {
 				element.setForeignKeyName( fkName );
 			}
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
 
 			CollectionPropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						null,
 						property,
 						parentPropertyHolder,
 						mappings
 				);
 			}
 			else {
 				elementClass = collType;
 				classType = mappings.getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property,
 						parentPropertyHolder,
 						mappings
 				);
 
 				// 'parentPropertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				parentPropertyHolder.startingProperty( property );
 
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				// todo : force in the case of Convert annotation(s) with embedded paths (beyond key/value prefixes)?
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
 				boolean isNullable = true;
 				Component component = AnnotationBinder.fillComponent(
 						holder,
 						inferredData,
 						isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD,
 						isNullable,
 						entityBinder,
 						false,
 						false,
 						true,
 						mappings,
 						inheritanceStatePerClass
 				);
 
 				collValue.setElement( component );
 
 				if ( StringHelper.isNotEmpty( hqlOrderBy ) ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 					if ( orderBy != null ) {
 						collValue.setOrderBy( orderBy );
 					}
 				}
 			}
 			else {
 				holder.prepare( property );
 
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
 				elementBinder.setType(
 						property,
 						elementClass,
 						collValue.getOwnerEntityName(),
 						holder.resolveElementAttributeConverterDefinition( elementClass )
 				);
 				elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 				elementBinder.setAccessType( accessType );
 				collValue.setElement( elementBinder.make() );
 				String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 				if ( orderBy != null ) {
 					collValue.setOrderBy( orderBy );
 				}
 			}
 		}
 
 		checkFilterConditions( collValue );
 
 		//FIXME: do optional = false
 		if ( isCollectionOfEntities ) {
 			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, mappings );
 		}
 
 	}
 
 	private String extractHqlOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		if ( jpaOrderBy != null ) {
 			return jpaOrderBy.value(); // Null not possible. In case of empty expression, apply default ordering.
 		}
 		return null; // @OrderBy not found.
 	}
 
 	private static void checkFilterConditions(Collection collValue) {
 		//for now it can't happen, but sometime soon...
 		if ( ( collValue.getFilters().size() != 0 || StringHelper.isNotEmpty( collValue.getWhere() ) ) &&
 				collValue.getFetchMode() == FetchMode.JOIN &&
 				!( collValue.getElement() instanceof SimpleValue ) && //SimpleValue (CollectionOfElements) are always SELECT but it does not matter
 				collValue.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 					"@ManyToMany or @CollectionOfElements defining filter or where without join fetching "
 							+ "not valid within collection using join fetching[" + collValue.getRole() + "]"
 			);
 		}
 	}
 
 	private static void bindCollectionSecondPass(
 			Collection collValue,
 			PersistentClass collectionEntity,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			Mappings mappings) {
 		try {
 			BinderHelper.createSyntheticPropertyReference(
 					joinColumns, collValue.getOwner(), collectionEntity, collValue, false, mappings
 			);
 		}
 		catch (AnnotationException ex) {
 			throw new AnnotationException( "Unable to map collection " + collectionEntity.getClassName() + "." + property.getName(), ex );
 		}
 		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( property.isAnnotationPresent( ElementCollection.class ) && joinColumns.length > 0 ) {
 			joinColumns[0].setJPA2ElementCollection( true );
 		}
 		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, mappings );
 	}
 
 	public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
 		this.cascadeDeleteEnabled = onDeleteCascade;
 	}
 
 	private String safeCollectionRole() {
 		if ( propertyHolder != null ) {
 			return propertyHolder.getEntityName() + "." + propertyName;
 		}
 		else {
 			return "";
 		}
 	}
 
 
 	/**
 	 * bind the inverse FK of a ManyToMany
 	 * If we are in a mappedBy case, read the columns from the associated
 	 * collection element
 	 * Otherwise delegates to the usual algorithm
 	 */
 	public static void bindManytoManyInverseFk(
 			PersistentClass referencedEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		final String mappedBy = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedBy ) ) {
 			final Property property = referencedEntity.getRecursiveProperty( mappedBy );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				mappedByColumns = ( (Collection) property.getValue() ).getKey().getColumnIterator();
 			}
 			else {
 				//find the appropriate reference key, can be in a join
 				Iterator joinsIt = referencedEntity.getJoinIterator();
 				KeyValue key = null;
 				while ( joinsIt.hasNext() ) {
 					Join join = (Join) joinsIt.next();
 					if ( join.containsProperty( property ) ) {
 						key = join.getKey();
 						break;
 					}
 				}
 				if ( key == null ) key = property.getPersistentClass().getIdentifier();
 				mappedByColumns = key.getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 			String referencedPropertyName =
 					mappings.getPropertyReferencedAssociation(
 							"inverse__" + referencedEntity.getEntityName(), mappedBy
 					);
 			if ( referencedPropertyName != null ) {
 				//TODO always a many to one?
 				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
 				mappings.addUniquePropertyReference( referencedEntity.getEntityName(), referencedPropertyName );
 			}
 			( (ManyToOne) value ).setReferenceToPrimaryKey( referencedPropertyName == null );
 			value.createForeignKey();
 		}
 		else {
 			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, mappings );
 			TableBinder.bindFk( referencedEntity, null, columns, value, unique, mappings );
 		}
 	}
 
 	public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
 		this.fkJoinColumns = ejb3JoinColumns;
 	}
 
 	public void setExplicitAssociationTable(boolean explicitAssocTable) {
 		this.isExplicitAssociationTable = explicitAssocTable;
 	}
 
 	public void setElementColumns(Ejb3Column[] elementColumns) {
 		this.elementColumns = elementColumns;
 	}
 
 	public void setEmbedded(boolean annotationPresent) {
 		this.isEmbedded = annotationPresent;
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void setMapKeyColumns(Ejb3Column[] mapKeyColumns) {
 		this.mapKeyColumns = mapKeyColumns;
 	}
 
 	public void setMapKeyManyToManyColumns(Ejb3JoinColumn[] mapJoinColumns) {
 		this.mapKeyManyToManyColumns = mapJoinColumns;
 	}
 
 	public void setLocalGenerators(HashMap<String, IdGenerator> localGenerators) {
 		this.localGenerators = localGenerators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index 92a69b2e62..af52045c58 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,1029 +1,1066 @@
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
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
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
+		private final String jpaEntityName;
 
-		private EntityTableNamingStrategyHelper(String entityName) {
+		private EntityTableNamingStrategyHelper(String entityName, String jpaEntityName) {
 			this.entityName = entityName;
+			this.jpaEntityName = jpaEntityName;
 		}
 
+		@Override
 		public String determineImplicitName(NamingStrategy strategy) {
 			return strategy.classToTableName( entityName );
 		}
 
+		@Override
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
+
+		@Override
+		public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
+			return getNamingStrategyDelegate( strategyDelegator ).determinePrimaryTableLogicalName(
+					entityName,
+					jpaEntityName
+			);
+		}
+
+		@Override
+		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
+			return getNamingStrategyDelegate( strategyDelegator ).toPhysicalTableName( name );
+		}
+	}
+
+	private static NamingStrategyDelegate getNamingStrategyDelegate(NamingStrategyDelegator strategyDelegator) {
+		return strategyDelegator.getNamingStrategyDelegate( false );
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperclassTable) {
 		EntityTableObjectNameSource tableNameContext = new EntityTableObjectNameSource( tableName, name );
-		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper( name );
+		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper(
+				persistentClass.getEntityName(),
+				name
+		);
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
+
+		@Override
+		public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
+			// todo : throw an error?
+			return null;
+		}
+
+		@Override
+		public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
+			return getNamingStrategyDelegate( strategyDelegator ).toPhysicalTableName( name );
+		}
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
index 83041f6af8..1810726875 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
@@ -1,611 +1,670 @@
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
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
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
+	private String ownerJpaEntity;
 	private String associatedEntity;
+	private String associatedJpaEntity;
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
-		// ownerEntity can be null when the table name is explicitly set
+		// ownerEntity can be null when the table name is explicitly set; <== gb: doesn't seem to be true...
 		final String ownerObjectName = isJPA2ElementCollection && ownerEntity != null ?
 				StringHelper.unqualify( ownerEntity ) : unquotedOwnerTable;
 		final ObjectNameSource nameSource = buildNameContext(
 				ownerObjectName,
-				unquotedAssocTable );
+				unquotedAssocTable
+		);
 
 		final boolean ownerEntityTableQuoted = StringHelper.isQuoted( ownerEntityTable );
 		final boolean associatedEntityTableQuoted = StringHelper.isQuoted( associatedEntityTable );
 		final ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper = new ObjectNameNormalizer.NamingStrategyHelper() {
 			public String determineImplicitName(NamingStrategy strategy) {
+				throw new AssertionFailure( "method call should have been replaced by #determineImplicitName(NamingStrategyDelegate strategyDelegate)" );
+			}
 
-				final String strategyResult = strategy.collectionTableName(
-						ownerEntity,
-						ownerObjectName,
-						associatedEntity,
-						unquotedAssocTable,
-						propertyName
+			public String handleExplicitName(NamingStrategy strategy, String name) {
+				return strategy.tableName( name );
+			}
 
-				);
+			@Override
+			public String determineImplicitName(NamingStrategyDelegator strategyDelegator) {
+				final NamingStrategyDelegate strategyDelegate = getNamingStrategyDelegate( strategyDelegator );
+				final String strategyResult;
+				if ( isJPA2ElementCollection ) {
+					strategyResult = strategyDelegate.determineElementCollectionTableLogicalName(
+							ownerEntity,
+							ownerJpaEntity,
+							unquotedOwnerTable,
+							propertyName
+					);
+				}
+				else {
+					strategyResult =  strategyDelegate.determineEntityAssociationJoinTableLogicalName(
+							ownerEntity,
+							ownerJpaEntity,
+							unquotedOwnerTable,
+							associatedEntity,
+							associatedJpaEntity,
+							unquotedAssocTable,
+							propertyName
+					);
+				}
 				return ownerEntityTableQuoted || associatedEntityTableQuoted
 						? StringHelper.quote( strategyResult )
 						: strategyResult;
 			}
 
-			public String handleExplicitName(NamingStrategy strategy, String name) {
-				return strategy.tableName( name );
+			@Override
+			public String handleExplicitName(NamingStrategyDelegator strategyDelegator, String name) {
+				return getNamingStrategyDelegate( strategyDelegator ).toPhysicalTableName( name );
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
 
-
-	private ObjectNameSource buildNameContext(String unquotedOwnerTable, String unquotedAssocTable) {
-		String logicalName = mappings.getNamingStrategy().logicalCollectionTableName(
-				name,
-				unquotedOwnerTable,
-				unquotedAssocTable,
-				propertyName
+	private ObjectNameSource buildNameContext(
+			String unquotedOwnerTable,
+			String unquotedAssocTable) {
+		final NamingStrategyDelegate strategyDelegate = getNamingStrategyDelegate(
+				mappings.getNamingStrategyDelegator()
 		);
+		String logicalName;
+		if ( isJPA2ElementCollection ) {
+			logicalName = strategyDelegate.logicalElementCollectionTableName(
+					name,
+					ownerEntity,
+					ownerJpaEntity,
+					unquotedOwnerTable,
+					propertyName
+			);
+		}
+		else {
+			logicalName =  strategyDelegate.logicalEntityAssociationJoinTableName(
+					name,
+					ownerEntity,
+					ownerJpaEntity,
+					unquotedOwnerTable,
+					associatedEntity,
+					associatedJpaEntity,
+					unquotedAssocTable,
+					propertyName
+			);
+		}
 		if ( StringHelper.isQuoted( ownerEntityTable ) || StringHelper.isQuoted( associatedEntityTable ) ) {
 			logicalName = StringHelper.quote( logicalName );
 		}
 
 		return new AssociationTableNameSource( name, logicalName );
 	}
 
+	private NamingStrategyDelegate getNamingStrategyDelegate(
+			NamingStrategyDelegator strategyDelegator) {
+		return strategyDelegator.getNamingStrategyDelegate( false );
+	}
+
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
-			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
+			String ownerEntity,
+			String ownerJpaEntity,
+			String ownerEntityTable,
+			String associatedEntity,
+			String associatedJpaEntity,
+			String associatedEntityTable,
 			String propertyName
 	) {
 		this.ownerEntity = ownerEntity;
+		this.ownerJpaEntity = ownerJpaEntity;
 		this.ownerEntityTable = ownerEntityTable;
 		this.associatedEntity = associatedEntity;
+		this.associatedJpaEntity = associatedJpaEntity;
 		this.associatedEntityTable = associatedEntityTable;
 		this.propertyName = propertyName;
 		this.name = null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractLegacyNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractLegacyNamingStrategyDelegate.java
new file mode 100644
index 0000000000..6f5355dd8d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractLegacyNamingStrategyDelegate.java
@@ -0,0 +1,79 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import java.io.Serializable;
+
+import org.hibernate.cfg.NamingStrategy;
+
+/**
+ * @author Gail Badner
+ */
+public abstract class AbstractLegacyNamingStrategyDelegate implements NamingStrategyDelegate, Serializable {
+	private final LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context;
+
+	public AbstractLegacyNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
+		this.context = context;
+	}
+
+	protected NamingStrategy getNamingStrategy() {
+		return context.getNamingStrategy();
+	}
+
+	@Override
+	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName) {
+		// jpaEntity name is being passed here in order to not cause a regression. See HHH-4312.
+		return getNamingStrategy().classToTableName( jpaEntityName );
+	}
+
+	@Override
+	public String toPhysicalTableName(String tableName) {
+		return getNamingStrategy().tableName( tableName );
+	}
+
+	@Override
+	public String toPhysicalColumnName(String columnName) {
+		return getNamingStrategy().columnName( columnName );
+	}
+
+	@Override
+	public String determineAttributeColumnName(String propertyName) {
+		return getNamingStrategy().propertyToColumnName( propertyName );
+	}
+
+	@Override
+	public String determineJoinKeyColumnName(String joinedColumn, String joinedTable) {
+		return getNamingStrategy().joinKeyColumnName( joinedColumn, joinedTable );
+	}
+
+	@Override
+	public String logicalColumnName(String columnName, String propertyName) {
+		return getNamingStrategy().logicalColumnName( columnName, propertyName );
+	}
+
+	@Override
+	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
+		return getNamingStrategy().logicalCollectionColumnName( columnName, propertyName, referencedColumn );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractNamingStrategyDelegate.java
new file mode 100644
index 0000000000..871527e79e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/AbstractNamingStrategyDelegate.java
@@ -0,0 +1,66 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import java.io.Serializable;
+
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * @author Gail Badner
+ */
+public abstract class AbstractNamingStrategyDelegate implements NamingStrategyDelegate, Serializable {
+
+	@Override
+	public String toPhysicalTableName(String tableName) {
+		return tableName;
+	}
+
+	@Override
+	public String toPhysicalColumnName(String columnName) {
+		return columnName;
+	}
+
+	@Override
+	public String determineAttributeColumnName(String propertyName) {
+		return StringHelper.unqualify( propertyName );
+	}
+
+	@Override
+	public String determineJoinKeyColumnName(String joinedColumn, String joinedTable) {
+		return toPhysicalColumnName( joinedColumn );
+	}
+
+	@Override
+	public String logicalColumnName(String columnName, String propertyName) {
+		return StringHelper.isNotEmpty( columnName ) ? columnName : StringHelper.unqualify( propertyName );
+	}
+
+	@Override
+	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
+		return StringHelper.isNotEmpty( columnName ) ?
+				columnName :
+				StringHelper.unqualify( propertyName ) + "_" + referencedColumn;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/DefaultNamingStrategyDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/DefaultNamingStrategyDelegator.java
new file mode 100644
index 0000000000..38e46c0cc4
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/DefaultNamingStrategyDelegator.java
@@ -0,0 +1,48 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import java.io.Serializable;
+
+/**
+ * @author Gail Badner
+ */
+public class DefaultNamingStrategyDelegator implements NamingStrategyDelegator, Serializable {
+	public static final DefaultNamingStrategyDelegator INSTANCE = new DefaultNamingStrategyDelegator();
+
+	private final NamingStrategyDelegate hbmNamingStrategyDelegate;
+	private final NamingStrategyDelegate jpaNamingStrategyDelegate;
+
+	private DefaultNamingStrategyDelegator() {
+		this.hbmNamingStrategyDelegate = new HbmNamingStrategyDelegate();
+		this.jpaNamingStrategyDelegate = new JpaNamingStrategyDelegate();
+	}
+
+	@Override
+	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
+		return isHbm ?
+				hbmNamingStrategyDelegate :
+				jpaNamingStrategyDelegate;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java
new file mode 100644
index 0000000000..b8f6e466cf
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/HbmNamingStrategyDelegate.java
@@ -0,0 +1,123 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * @author Gail Badner
+ */
+public class HbmNamingStrategyDelegate extends AbstractNamingStrategyDelegate {
+
+	@Override
+	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName) {
+		return StringHelper.unqualify( entityName );
+	}
+
+	@Override
+	public String determineElementCollectionTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyNamePath) {
+		return ownerEntityTable
+				+ '_'
+				+ StringHelper.unqualify( propertyNamePath );
+	}
+
+	@Override
+	public String determineElementCollectionForeignKeyColumnName(String propertyName, String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName) {
+		throw new UnsupportedOperationException( "Method not supported for Hibernate-specific mappings" );
+	}
+
+	@Override
+	public String determineEntityAssociationJoinTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyNamePath) {
+		return ownerEntityTable
+				+ '_'
+				+ StringHelper.unqualify( propertyNamePath );
+	}
+
+	@Override
+	public String determineEntityAssociationForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName) {
+		throw new UnsupportedOperationException( "Method not supported for Hibernate-specific mappings" );
+	}
+
+	@Override
+	public String logicalElementCollectionTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyName) {
+		if ( tableName != null ) {
+			return tableName;
+		}
+		else {
+			return determineElementCollectionTableLogicalName(
+					ownerEntityName,
+					ownerJpaEntityName,
+					ownerEntityTable,
+					propertyName
+			);
+		}
+	}
+
+	@Override
+	public String logicalEntityAssociationJoinTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyName) {
+		if ( tableName != null ) {
+			return tableName;
+		}
+		else {
+			return determineEntityAssociationJoinTableLogicalName(
+					ownerEntityName,
+					ownerJpaEntityName,
+					ownerEntityTable,
+					associatedEntityName,
+					associatedJpaEntityName,
+					associatedEntityTable,
+					propertyName
+			);
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java
new file mode 100644
index 0000000000..653de66ca6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/JpaNamingStrategyDelegate.java
@@ -0,0 +1,183 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * @author Gail Badner
+ */
+public class JpaNamingStrategyDelegate extends AbstractNamingStrategyDelegate {
+
+	@Override
+	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName) {
+		return StringHelper.unqualify( determineEntityNameToUse( entityName, jpaEntityName ) );
+	}
+
+	@Override
+	public String determineElementCollectionTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyNamePath) {
+		// JPA states we should use the following as default:
+		//      "The concatenation of the name of the containing entity and the name of the
+		//       collection attribute, separated by an underscore.
+		// aka:
+		//     if owning entity has a JPA entity name: {OWNER JPA ENTITY NAME}_{COLLECTION ATTRIBUTE NAME}
+		//     otherwise: {OWNER ENTITY NAME}_{COLLECTION ATTRIBUTE NAME}
+		return determineEntityNameToUse( ownerEntityName, ownerJpaEntityName )
+				+ '_'
+				+ StringHelper.unqualify( propertyNamePath );
+	}
+
+	@Override
+	public String determineElementCollectionForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName) {
+		// JPA states we should use the following as default:
+		//     "The concatenation of the following: the name of the entity; "_"; the name of the
+		//      referenced primary key column"
+		return determineEntityNameToUse( propertyEntityName, propertyJpaEntityName )
+				+ '_'
+				+ referencedColumnName;
+	}
+
+	@Override
+	public String determineEntityAssociationJoinTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyNamePath) {
+		// JPA states we should use the following as default:
+		//		"The concatenated names of the two associated primary entity tables (owning side
+		//		first), separated by an underscore."
+		// aka:
+		// 		{OWNING SIDE PRIMARY TABLE NAME}_{NON-OWNING SIDE PRIMARY TABLE NAME}
+
+		return  ownerEntityTable
+				+ '_'
+				+ associatedEntityTable;
+	}
+
+	@Override
+	public String determineEntityAssociationForeignKeyColumnName(
+			String referencingPropertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName) {
+		// JPA states we should use the following as default:
+		//      "The concatenation of the following: the name of the referencing relationship
+		//      property or field of the referencing entity or embeddable class; "_"; the name
+		//      of the referenced primary key column. If there is no such referencing relationship
+		//      property or field in the entity, or if the join is for an element collection, the
+		//      join column name is formed as the concatenation of the following: the name of the
+		//      entity; "_"; the name of the referenced primary key column
+		// The part referring to an entity collection can be disregarded here since, determination of
+		// an element collection foreign key column name is covered  by #entityAssociationJoinTableName().
+		//
+		// For a unidirectional association:
+		//      {PROPERTY_ENTITY_NAME}_{REFERENCED_COLUMN_NAME}
+		// For a bidirectional association:
+		//      {REFERENCING_PROPERTY_NAME}_{REFERENCED_COLUMN_NAME}
+		final String header;
+		if ( referencingPropertyName == null ) {
+			// This is a unidirectional association.
+			header = determineEntityNameToUse( propertyEntityName, propertyJpaEntityName );
+		}
+		else {
+			// This is a bidirectional association.
+			header = StringHelper.unqualify( referencingPropertyName );
+		}
+		if ( header == null ) {
+			throw new AssertionFailure( "propertyJpaEntityName and referencingPropertyName cannot both be empty." );
+		}
+		return toPhysicalColumnName( header + "_" + referencedColumnName );
+	}
+
+	@Override
+	public String logicalElementCollectionTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyName) {
+		if ( tableName != null ) {
+			return tableName;
+		}
+		else {
+			return determineElementCollectionTableLogicalName(
+					ownerEntityName,
+					ownerJpaEntityName,
+					ownerEntityTable,
+					propertyName
+			);
+		}
+	}
+
+	@Override
+	public String logicalEntityAssociationJoinTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyName) {
+		if ( tableName != null ) {
+			return tableName;
+		}
+		else {
+			return determineEntityAssociationJoinTableLogicalName(
+					ownerEntityName,
+					ownerJpaEntityName,
+					ownerEntityTable,
+					associatedEntityName,
+					associatedJpaEntityName,
+					associatedEntityTable,
+					propertyName
+			);
+		}
+	}
+
+	private String determineEntityNameToUse(String entityName, String jpaEntityName) {
+		if ( StringHelper.isNotEmpty( jpaEntityName ) ) {
+			// prefer the JPA entity name, if specified...
+			return jpaEntityName;
+		}
+		else {
+			// otherwise, use the Hibernate entity name
+			return StringHelper.unqualifyEntityName( entityName );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java
new file mode 100644
index 0000000000..2a75f793e8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyHbmNamingStrategyDelegate.java
@@ -0,0 +1,125 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+/**
+ * @author Gail Badner
+ */
+public class LegacyHbmNamingStrategyDelegate extends AbstractLegacyNamingStrategyDelegate {
+
+	public LegacyHbmNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
+		super( context );
+	}
+
+	@Override
+	public String determineElementCollectionTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyNamePath) {
+		return getNamingStrategy().collectionTableName(
+				ownerEntityName,
+				ownerEntityTable,
+				null,
+				null,
+				propertyNamePath
+		);
+	}
+
+	@Override
+	public String determineElementCollectionForeignKeyColumnName(String propertyName, String propertyEntityName, String propertyJpaEntityName, String propertyTableName, String referencedColumnName) {
+		return getNamingStrategy().foreignKeyColumnName(
+				propertyName,
+				propertyEntityName,
+				propertyTableName,
+				referencedColumnName
+		);
+	}
+
+	@Override
+	public String determineEntityAssociationJoinTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyNamePath) {
+		return getNamingStrategy().collectionTableName(
+				ownerEntityName,
+				ownerEntityTable,
+				associatedEntityName,
+				associatedEntityTable,
+				propertyNamePath
+		);
+	}
+
+	@Override
+	public String determineEntityAssociationForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName) {
+		return getNamingStrategy().foreignKeyColumnName(
+				propertyName,
+				propertyEntityName,
+				propertyTableName,
+				referencedColumnName
+		);
+	}
+
+	@Override
+	public String logicalElementCollectionTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyName) {
+		return getNamingStrategy().logicalCollectionTableName(
+				tableName,
+				ownerEntityTable,
+				null,
+				propertyName
+		);
+	}
+
+	@Override
+	public String logicalEntityAssociationJoinTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyName) {
+		return getNamingStrategy().logicalCollectionTableName(
+				tableName,
+				ownerEntityTable,
+				associatedEntityTable,
+				propertyName
+		);
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java
new file mode 100644
index 0000000000..1a33e95ccb
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegate.java
@@ -0,0 +1,35 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import org.hibernate.cfg.NamingStrategy;
+
+/**
+ * @author Gail Badner
+ */
+public interface LegacyNamingStrategyDelegate extends NamingStrategyDelegate {
+	public static interface LegacyNamingStrategyDelegateContext {
+		public NamingStrategy getNamingStrategy();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java
new file mode 100644
index 0000000000..35a32ea726
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyNamingStrategyDelegator.java
@@ -0,0 +1,67 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import java.io.Serializable;
+
+import org.hibernate.cfg.EJB3NamingStrategy;
+import org.hibernate.cfg.NamingStrategy;
+
+import static org.hibernate.cfg.naming.LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext;
+
+/**
+ *
+ * @deprecated Needed as a transitory implementation until the deprecated NamingStrategy contract
+ * can be removed.
+ *
+ * @author Gail Badner
+ */
+@Deprecated
+public class LegacyNamingStrategyDelegator
+		implements NamingStrategyDelegator, LegacyNamingStrategyDelegateContext, Serializable {
+	private final NamingStrategy namingStrategy;
+	private final NamingStrategyDelegate hbmNamingStrategyDelegate;
+	private final NamingStrategyDelegate jpaNamingStrategyDelegate;
+
+	public LegacyNamingStrategyDelegator() {
+		this( EJB3NamingStrategy.INSTANCE );
+	}
+
+	public LegacyNamingStrategyDelegator(NamingStrategy namingStrategy) {
+		this.namingStrategy = namingStrategy;
+		this.hbmNamingStrategyDelegate = new LegacyHbmNamingStrategyDelegate( this );
+		this.jpaNamingStrategyDelegate = new LegacyStandardNamingStrategyDelegate( this );
+	}
+
+	public NamingStrategy getNamingStrategy() {
+		return namingStrategy;
+	}
+
+	@Override
+	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
+		return isHbm ?
+				hbmNamingStrategyDelegate :
+				jpaNamingStrategyDelegate;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyStandardNamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyStandardNamingStrategyDelegate.java
new file mode 100644
index 0000000000..57e5e080a6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/LegacyStandardNamingStrategyDelegate.java
@@ -0,0 +1,132 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import org.hibernate.internal.util.StringHelper;
+
+/**
+ * @author Gail Badner
+ */
+public class LegacyStandardNamingStrategyDelegate extends AbstractLegacyNamingStrategyDelegate {
+
+	LegacyStandardNamingStrategyDelegate(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context) {
+		super( context );
+	}
+
+	@Override
+	public String determineElementCollectionTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyNamePath) {
+		return getNamingStrategy().collectionTableName(
+				ownerEntityName,
+				StringHelper.unqualifyEntityName( ownerEntityName ),
+				null,
+				null,
+				propertyNamePath
+		);
+	}
+
+	@Override
+	public String determineElementCollectionForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName) {
+		return getNamingStrategy().foreignKeyColumnName(
+				propertyName,
+				propertyEntityName,
+				StringHelper.unqualifyEntityName( propertyEntityName ),
+				referencedColumnName
+		);
+	}
+
+	@Override
+	public String determineEntityAssociationJoinTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyNamePath) {
+		return getNamingStrategy().collectionTableName(
+				ownerEntityName,
+				ownerEntityTable,
+				associatedEntityName,
+				associatedEntityTable,
+				propertyNamePath
+		);
+	}
+
+	@Override
+	public String determineEntityAssociationForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName) {
+		return getNamingStrategy().foreignKeyColumnName(
+				propertyName,
+				propertyEntityName,
+				propertyTableName,
+				referencedColumnName
+		);
+	}
+
+	@Override
+	public String logicalElementCollectionTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyName) {
+		return getNamingStrategy().logicalCollectionTableName(
+				tableName,
+				ownerEntityName == null ? null : StringHelper.unqualifyEntityName( ownerEntityName ),
+				null,
+				propertyName
+		);
+	}
+
+	@Override
+	public String logicalEntityAssociationJoinTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyName) {
+		return getNamingStrategy().logicalCollectionTableName(
+				tableName,
+				ownerEntityTable,
+				associatedEntityTable,
+				propertyName
+		);
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java
new file mode 100644
index 0000000000..de204ff000
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegate.java
@@ -0,0 +1,102 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+/**
+ * @author Gail Badner
+ */
+public interface NamingStrategyDelegate {
+
+	public String determinePrimaryTableLogicalName(String entityName, String jpaEntityName);
+
+	public String determineAttributeColumnName(String propertyName);
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
+
+	public String determineElementCollectionTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyName);
+
+	public String determineElementCollectionForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName);
+
+
+	public String determineEntityAssociationJoinTableLogicalName(
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyName);
+
+	public String determineEntityAssociationForeignKeyColumnName(
+			String propertyName,
+			String propertyEntityName,
+			String propertyJpaEntityName,
+			String propertyTableName,
+			String referencedColumnName);
+
+	public String determineJoinKeyColumnName(String joinedColumn, String joinedTable);
+
+	public String logicalColumnName(String columnName, String propertyName);
+
+	public String logicalElementCollectionTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String propertyName);
+
+	public String logicalEntityAssociationJoinTableName(
+			String tableName,
+			String ownerEntityName,
+			String ownerJpaEntityName,
+			String ownerEntityTable,
+			String associatedEntityName,
+			String associatedJpaEntityName,
+			String associatedEntityTable,
+			String propertyName);
+
+	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java
new file mode 100644
index 0000000000..d13764acb2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/naming/NamingStrategyDelegator.java
@@ -0,0 +1,44 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.cfg.naming;
+
+import org.hibernate.cfg.NamingStrategy;
+
+/**
+ * Provides access to the appropriate {@link NamingStrategyDelegate}.
+ *
+ * @author Gail Badner
+ */
+public interface NamingStrategyDelegator {
+
+	/**
+	 * Returns the appropriate {@link NamingStrategyDelegate}.
+	 *
+	 * @param isHbm - true, if {@link NamingStrategyDelegate} is to be used for a
+	 * hibernate-specific (hbm.xml) mapping; false, otherwise.
+	 *
+	 * @return the appropriate {@link NamingStrategyDelegate}
+	 */
+	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java
index 1b69367031..b690c79196 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/IdentifierGeneratorResolver.java
@@ -1,97 +1,103 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.Properties;
 
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.engine.config.spi.ConfigurationService;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 
 /**
  * @author Gail Badner
  */
 public class IdentifierGeneratorResolver {
 
 	private final MetadataImplementor metadata;
 
 	IdentifierGeneratorResolver(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	// IdentifierGeneratorResolver.resolve() must execute after AttributeTypeResolver.resolve()
 	// to ensure that identifier type is resolved.
 	@SuppressWarnings( {"unchecked"} )
 	void resolve() {
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				Properties properties = new Properties( );
 				properties.putAll(
 						metadata.getServiceRegistry()
 								.getService( ConfigurationService.class )
 								.getSettings()
 				);
 				//TODO: where should these be added???
 				if ( ! properties.contains( AvailableSettings.PREFER_POOLED_VALUES_LO ) ) {
 					properties.put( AvailableSettings.PREFER_POOLED_VALUES_LO, "false" );
 				}
 				if ( ! properties.contains( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER ) ) {
 					properties.put(
 							PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 							new ObjectNameNormalizerImpl( metadata )
 					);
 				}
 				entityBinding.getHierarchyDetails().getEntityIdentifier().createIdentifierGenerator(
 						metadata.getIdentifierGeneratorFactory(),
 						properties
 				);
 			}
 		}
 	}
 
 	private static class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
 		private final boolean useQuotedIdentifiersGlobally;
 		private final NamingStrategy namingStrategy;
 
 		private ObjectNameNormalizerImpl(MetadataImplementor metadata ) {
 			this.useQuotedIdentifiersGlobally = metadata.isGloballyQuotedIdentifiers();
 			this.namingStrategy = metadata.getNamingStrategy();
 		}
 
 		@Override
 		protected boolean isUseQuotedIdentifiersGlobally() {
 			return useQuotedIdentifiersGlobally;
 		}
 
 		@Override
 		protected NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
+
+		@Override
+		protected NamingStrategyDelegator getNamingStrategyDelegator() {
+			return null;
+		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
index 763d5fcbc3..0521617005 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
@@ -1,178 +1,184 @@
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
 
 import static org.junit.Assert.assertEquals;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Session;
 import org.hibernate.testing.env.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.StandardBasicTypes;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
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
     private SessionImplementor session;
 
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
+
+					@Override
+					protected NamingStrategyDelegator getNamingStrategyDelegator() {
+						return cfg.getNamingStrategyDelegator();
+					}
 				}
 		);
 
 		Dialect dialect = TestingDatabaseInfo.DIALECT;
 
 		generator = new SequenceHiLoGenerator();
 		generator.configure( StandardBasicTypes.LONG, properties, dialect );
 
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
         if(session != null && !session.isClosed()) {
             ((Session)session).close();
         }
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 
 	@Test
 	public void testHiLoAlgorithm() {
 		session = (SessionImpl) sessionFactory.openSession();
 		((Session)session).beginTransaction();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// initially sequence should be uninitialized
 		assertEquals( 0L, extractSequenceValue( (session) ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// historically the hilo generators skipped the initial block of values;
 		// 		so the first generated id value is maxlo + 1, here be 4
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		// which should also perform the first read on the sequence which should set it to its "start with" value (1)
 		assertEquals( 1L, extractSequenceValue( (session) ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 2L, extractSequenceValue( (session) ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 3L, extractSequenceValue( (session) ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 4L, extractSequenceValue( (session) ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 5L, extractSequenceValue( (session) ) );
 
 		((Session)session).getTransaction().commit();
 		((Session)session).close();
 	}
 
 	private long extractSequenceValue(final SessionImplementor session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				
 				PreparedStatement query = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( query );
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		( (Session) session ).doWork( work );
 		return work.value;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
index 7239297ff7..7346f01155 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
@@ -1,168 +1,175 @@
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
 
 import static org.junit.Assert.assertEquals;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Session;
 import org.hibernate.testing.env.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.StandardBasicTypes;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
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
 		properties.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, new ObjectNameNormalizer() {
 			@Override
 			protected boolean isUseQuotedIdentifiersGlobally() {
 				return false;
 			}
 
 			@Override
 			protected NamingStrategy getNamingStrategy() {
 				return cfg.getNamingStrategy();
 			}
+
+			@Override
+			protected NamingStrategyDelegator getNamingStrategyDelegator() {
+				return cfg.getNamingStrategyDelegator();
+			}
+
 		} );
 
 		Dialect dialect = TestingDatabaseInfo.DIALECT;
 
 		generator = new SequenceHiLoGenerator();
 		generator.configure( StandardBasicTypes.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject( new SimpleAuxiliaryDatabaseObject( generator.sqlCreateStrings( dialect )[0],
 				generator.sqlDropStrings( dialect )[0] ) );
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
 		// so the first generated id value is maxlo + 1, here be 4
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
 		// unlike the newer strategies, the db value will not get update here. It gets updated on the next invocation
 		// after a clock over
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 8L, generatedValue.longValue() );
 		// this should force an increment in the sequence value
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(final SessionImplementor session) {
 		class WorkImpl implements Work {
 			private long value;
 
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( query );
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		( (Session) session ).doWork( work );
 		return work.value;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java b/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
index 1c2196b4a0..eef654fbb7 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/enhanced/SequenceStyleConfigUnitTest.java
@@ -1,246 +1,252 @@
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
 package org.hibernate.id.enhanced;
 
 import java.util.Properties;
 
 import org.junit.Test;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.StandardBasicTypes;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;
 
 /**
  * Tests that SequenceStyleGenerator configures itself as expected in various scenarios
  *
  * @author Steve Ebersole
  */
 public class SequenceStyleConfigUnitTest extends BaseUnitTestCase {
 	private void assertClassAssignability(Class expected, Class actual) {
 		if ( ! expected.isAssignableFrom( actual ) ) {
 			fail( "Actual type [" + actual.getName() + "] is not assignable to expected type [" + expected.getName() + "]" );
 		}
 	}
 
 
 	/**
 	 * Test all params defaulted with a dialect supporting sequences
 	 */
 	@Test
 	public void testDefaultedSequenceBackedConfiguration() {
 		Dialect dialect = new SequenceDialect();
 		Properties props = buildGeneratorPropertiesBase();
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 
 		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( NoopOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
 	}
 
 	private Properties buildGeneratorPropertiesBase() {
 		Properties props = new Properties();
 		props.put(
 				PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER,
 				new ObjectNameNormalizer() {
 					protected boolean isUseQuotedIdentifiersGlobally() {
 						return false;
 					}
 
 					protected NamingStrategy getNamingStrategy() {
 						return null;
 					}
+
+					@Override
+					protected NamingStrategyDelegator getNamingStrategyDelegator() {
+						return null;
+					}
 				}
 		);
 		return props;
 	}
 
 	/**
 	 * Test all params defaulted with a dialect which does not support sequences
 	 */
 	@Test
 	public void testDefaultedTableBackedConfiguration() {
 		Dialect dialect = new TableDialect();
 		Properties props = buildGeneratorPropertiesBase();
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 
 		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( NoopOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
 	}
 
 	/**
 	 * Test default optimizer selection for sequence backed generators
 	 * based on the configured increment size; both in the case of the
 	 * dialect supporting pooled sequences (pooled) and not (hilo)
 	 */
 	@Test
 	public void testDefaultOptimizerBasedOnIncrementBackedBySequence() {
 		Properties props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
 
 		// for dialects which do not support pooled sequences, we default to pooled+table
 		Dialect dialect = new SequenceDialect();
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( PooledOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
 
 		// for dialects which do support pooled sequences, we default to pooled+sequence
 		dialect = new PooledSequenceDialect();
 		generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( PooledOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
 	}
 
 	/**
 	 * Test default optimizer selection for table backed generators
 	 * based on the configured increment size.  Here we always prefer
 	 * pooled.
 	 */
 	@Test
 	public void testDefaultOptimizerBasedOnIncrementBackedByTable() {
 		Properties props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "10" );
 		Dialect dialect = new TableDialect();
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( PooledOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
 	}
 
 	/**
 	 * Test forcing of table as backing strucuture with dialect supporting sequences
 	 */
 	@Test
 	public void testForceTableUse() {
 		Dialect dialect = new SequenceDialect();
 		Properties props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.FORCE_TBL_PARAM, "true" );
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( NoopOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( SequenceStyleGenerator.DEF_SEQUENCE_NAME, generator.getDatabaseStructure().getName() );
 	}
 
 	/**
 	 * Test explicitly specifying both optimizer and increment
 	 */
 	@Test
 	public void testExplicitOptimizerWithExplicitIncrementSize() {
 		// with sequence ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		final Dialect dialect = new SequenceDialect();
 
 		// optimizer=none w/ increment > 1 => should honor optimizer
 		Properties props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.OPT_PARAM, StandardOptimizerDescriptor.NONE.getExternalName() );
 		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( NoopOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( 1, generator.getOptimizer().getIncrementSize() );
 		assertEquals( 1, generator.getDatabaseStructure().getIncrementSize() );
 
 		// optimizer=hilo w/ increment > 1 => hilo
 		props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.OPT_PARAM, StandardOptimizerDescriptor.HILO.getExternalName() );
 		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
 		generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( HiLoOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
 		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
 
 		// optimizer=pooled w/ increment > 1 => hilo
 		props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.OPT_PARAM, StandardOptimizerDescriptor.POOLED.getExternalName() );
 		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
 		generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		// because the dialect reports to not support pooled seqyences, the expectation is that we will
 		// use a table for the backing structure...
 		assertClassAssignability( TableStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( PooledOptimizer.class, generator.getOptimizer().getClass() );
 		assertEquals( 20, generator.getOptimizer().getIncrementSize() );
 		assertEquals( 20, generator.getDatabaseStructure().getIncrementSize() );
 	}
 
 	@Test
 	public void testPreferPooledLoSettingHonored() {
 		final Dialect dialect = new PooledSequenceDialect();
 
 		Properties props = buildGeneratorPropertiesBase();
 		props.setProperty( SequenceStyleGenerator.INCREMENT_PARAM, "20" );
 		SequenceStyleGenerator generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( PooledOptimizer.class, generator.getOptimizer().getClass() );
 
 		props.setProperty( Environment.PREFER_POOLED_VALUES_LO, "true" );
 		generator = new SequenceStyleGenerator();
 		generator.configure( StandardBasicTypes.LONG, props, dialect );
 		assertClassAssignability( SequenceStructure.class, generator.getDatabaseStructure().getClass() );
 		assertClassAssignability( PooledLoOptimizer.class, generator.getOptimizer().getClass() );
 	}
 
 	private static class TableDialect extends Dialect {
 		public boolean supportsSequences() {
 			return false;
 		}
 	}
 
 	private static class SequenceDialect extends Dialect {
 		public boolean supportsSequences() {
 			return true;
 		}
 		public boolean supportsPooledSequences() {
 			return false;
 		}
 		public String getSequenceNextValString(String sequenceName) throws MappingException {
 			return "";
 		}
 	}
 
 	private static class PooledSequenceDialect extends SequenceDialect {
 		public boolean supportsPooledSequences() {
 			return true;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java
index 48f296892c..79525aa978 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CollectionElementTest.java
@@ -1,413 +1,408 @@
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
-import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class CollectionElementTest extends BaseCoreFunctionalTestCase {
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
-	@FailureExpected( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9387")
-	@FailureExpected( jiraKey = "HHH-9387")
 	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_elements" );
 	}
 
-	private void checkDefaultCollectionTableName(
+	protected void checkDefaultCollectionTableName(
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
-	@FailureExpected( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
 		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9389")
-	@FailureExpected( jiraKey = "HHH-9389")
 	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
 		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
 		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
 
 
 		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
 		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_id" );
 	}
 
-	private void checkDefaultJoinColumnName(
+	protected void checkDefaultJoinColumnName(
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomNamingCollectionElementTest.java
new file mode 100644
index 0000000000..bad57f1f03
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/CustomNamingCollectionElementTest.java
@@ -0,0 +1,244 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.collectionelement;
+
+import org.junit.Test;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.EJB3NamingStrategy;
+import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.cfg.naming.AbstractLegacyNamingStrategyDelegate;
+import org.hibernate.cfg.naming.LegacyHbmNamingStrategyDelegate;
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegate;
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
+import org.hibernate.testing.TestForIssue;
+
+/**
+ * @author Gail Badner
+ */
+public class CustomNamingCollectionElementTest extends CollectionElementTest {
+
+	@Override
+	public void configure(Configuration cfg) {
+		super.configure( cfg );
+		cfg.setNamingStrategyDelegator( new MyLegacyNamingStrategyDelegator() );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9387")
+	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
+		// MyNamingStrategyDelegator will use the owner primary table name (instead of JPA entity name) in generated collection table.
+		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Mtx_mvalues" );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9387")
+	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
+		// MyNamingStrategyDelegator will use owner primary table name (instead of JPA entity name) in generated collection table.
+		checkDefaultCollectionTableName( Owner.class, "elements", "OWNER_TABLE_elements" );
+	}
+
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9389")
+	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
+		// MyNamingStrategyDelegator will use owner primary table name, which will default to the JPA entity name
+		// in generated join column.
+		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Mtx_mId" );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9389")
+	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
+		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
+		checkDefaultJoinColumnName( Owner.class, "elements", "OWNER_TABLE_id" );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9389")
+	public void testDefaultJoinColumnOwnerPrimaryTableOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Boy has @Entity @Table(name="tbl_Boys")
+		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
+		checkDefaultJoinColumnName( Boy.class, "hatedNames", "tbl_Boys_id" );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9387")
+	public void testDefaultTableNameOwnerPrimaryTableOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Boy has @Entity @Table(name="tbl_Boys")
+		// MyNamingStrategyDelegator will use the table name (instead of JPA entity name) in generated join column.
+		checkDefaultCollectionTableName( Boy.class, "hatedNames", "tbl_Boys_hatedNames" );
+	}
+
+	static class MyLegacyNamingStrategyDelegator extends LegacyNamingStrategyDelegator {
+		private final NamingStrategyDelegate hbmNamingStrategyDelegate = new LegacyHbmNamingStrategyDelegate( this );
+		private final NamingStrategyDelegate nonHbmNamingStrategyDelegate = new MyNonHbmNamingStrategyDelegator( this );
+
+		@Override
+		public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
+			return isHbm ? hbmNamingStrategyDelegate : nonHbmNamingStrategyDelegate;
+		}
+
+		@Override
+		public NamingStrategy getNamingStrategy() {
+			return EJB3NamingStrategy.INSTANCE;
+		}
+
+		private class MyNonHbmNamingStrategyDelegator extends AbstractLegacyNamingStrategyDelegate {
+			MyNonHbmNamingStrategyDelegator(LegacyNamingStrategyDelegate.LegacyNamingStrategyDelegateContext context)  {
+				super( context );
+			}
+
+			@Override
+			public String toPhysicalTableName(String tableName) {
+				return getNamingStrategy().tableName( tableName );
+			}
+
+			@Override
+			public String toPhysicalColumnName(String columnName) {
+				return getNamingStrategy().columnName( columnName );
+			}
+
+			@Override
+			public String determineElementCollectionTableLogicalName(
+					String ownerEntityName,
+					String ownerJpaEntityName,
+					String ownerEntityTable,
+					String propertyNamePath) {
+				return getNamingStrategy().collectionTableName(
+						ownerEntityName,
+						ownerEntityTable,
+						null,
+						null,
+						propertyNamePath
+				);
+			}
+
+			@Override
+			public String determineElementCollectionForeignKeyColumnName(
+					String propertyName,
+					String propertyEntityName,
+					String propertyJpaEntityName,
+					String propertyTableName,
+					String referencedColumnName) {
+				return getNamingStrategy().foreignKeyColumnName(
+						propertyName,
+						propertyEntityName,
+						propertyTableName,
+						referencedColumnName
+				);
+			}
+
+			@Override
+			public String determineEntityAssociationJoinTableLogicalName(
+					String ownerEntityName,
+					String ownerJpaEntityName,
+					String ownerEntityTable,
+					String associatedEntityName,
+					String associatedJpaEntityName,
+					String associatedEntityTable,
+					String propertyNamePath) {
+				return getNamingStrategy().collectionTableName(
+						ownerEntityName,
+						ownerEntityTable,
+						associatedEntityName,
+						associatedEntityTable,
+						propertyNamePath
+				);
+			}
+
+			@Override
+			public String determineEntityAssociationForeignKeyColumnName(
+					String propertyName,
+					String propertyEntityName,
+					String propertyJpaEntityName,
+					String propertyTableName,
+					String referencedColumnName) {
+				return getNamingStrategy().foreignKeyColumnName(
+						propertyName,
+						propertyEntityName,
+						propertyTableName,
+						referencedColumnName
+				);
+			}
+
+			@Override
+			public String logicalElementCollectionTableName(
+					String tableName,
+					String ownerEntityName,
+					String ownerJpaEntityName,
+					String ownerEntityTable,
+					String propertyName) {
+				return getNamingStrategy().logicalCollectionTableName(
+						tableName,
+						ownerEntityTable,
+						null,
+						propertyName
+				);
+			}
+
+			@Override
+			public String logicalEntityAssociationJoinTableName(
+					String tableName,
+					String ownerEntityName,
+					String ownerJpaEntityName,
+					String ownerEntityTable,
+					String associatedEntityName,
+					String associatedJpaEntityName,
+					String associatedEntityTable,
+					String propertyName) {
+				return getNamingStrategy().logicalCollectionTableName(
+						tableName,
+						ownerEntityTable,
+						associatedEntityTable,
+						propertyName
+				);
+			}
+		}
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/LegacyNamingCollectionElementTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/LegacyNamingCollectionElementTest.java
new file mode 100644
index 0000000000..e9b8ddd4d2
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/collectionelement/LegacyNamingCollectionElementTest.java
@@ -0,0 +1,88 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.collectionelement;
+
+import org.junit.Test;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.EJB3NamingStrategy;
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.testing.TestForIssue;
+
+/**
+ * @author Gail Badner
+ */
+public class LegacyNamingCollectionElementTest extends CollectionElementTest {
+
+	@Override
+	public void configure(Configuration cfg) {
+		super.configure( cfg );
+		cfg.setNamingStrategyDelegator( new LegacyNamingStrategyDelegator( EJB3NamingStrategy.INSTANCE ) );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9387")
+	public void testDefaultTableNameOwnerEntityNameAndPKColumnOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
+		checkDefaultCollectionTableName( Matrix.class, "mvalues", "Matrix_mvalues" );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9387")
+	public void testDefaultTableNameOwnerPrimaryTableAndEntityNamesOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated collection table.
+		checkDefaultCollectionTableName( Owner.class, "elements", "Owner_elements" );
+	}
+
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9389")
+	public void testDefaultJoinColumnOwnerEntityNameAndPKColumnOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Matrix has @Entity(name="Mtx"); entity table name defaults to "Mtx"; owner PK column is configured as "mId"
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
+		checkDefaultJoinColumnName( Matrix.class, "mvalues", "Matrix_mId" );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9389")
+	public void testDefaultJoinColumnOwnerPrimaryTableAndEntityNamesOverride() {
+		// NOTE: expected JPA entity names are explicit here (rather than just getting them from the PersistentClass)
+		//       to ensure that entity names/tables are not changed (which would invalidate these test cases).
+
+		// Owner has @Entity( name="OWNER") @Table( name="OWNER_TABLE")
+		// Legacy behavior used unqualified entity name (instead of JPA entity name) in generated join column.
+		checkDefaultJoinColumnName( Owner.class, "elements", "Owner_id" );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java
index d7d7f4662a..174d56d790 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/Group.java
@@ -1,49 +1,52 @@
 //$Id$
 package org.hibernate.test.annotations.manytomany;
 import java.util.Collection;
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.JoinTable;
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
+	@JoinTable(joinColumns = {@JoinColumn( name="groupId")})
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
index 01bbe3b91a..b6551506fc 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/GroupWithSet.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/GroupWithSet.java
@@ -1,49 +1,52 @@
 //$Id$
 package org.hibernate.test.annotations.manytomany;
 import java.util.Set;
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.JoinTable;
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
+	@JoinTable(joinColumns = {@JoinColumn( name="groupId")})
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/LegacyManyToManyDefaultsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/LegacyManyToManyDefaultsTest.java
new file mode 100644
index 0000000000..24b8b81932
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/LegacyManyToManyDefaultsTest.java
@@ -0,0 +1,84 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.annotations.manytomany.defaults;
+
+import org.junit.Test;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.EJB3NamingStrategy;
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.testing.FailureExpected;
+import org.hibernate.testing.TestForIssue;
+
+/**
+ * @author Gail Badner
+ */
+public class LegacyManyToManyDefaultsTest extends ManyToManyDefaultsTest {
+	@Override
+	public void configure(Configuration cfg) {
+		super.configure( cfg );
+		cfg.setNamingStrategyDelegator( new LegacyNamingStrategyDelegator( EJB3NamingStrategy.INSTANCE ) );
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9390")
+	public void testUnidirOwnerPrimaryTableAssocEntityNamePKOverride() {
+		// City.stolenItems; associated entity: Item
+		// City has @Entity with no name configured and @Table(name = "tbl_city")
+		// Item has @Entity(name="ITEM") and no @Table
+		// PK column for City.id: id (default)
+		// PK column for Item: iId
+		// unidirectional
+		// legacy behavior would use the table name in the generated join column.
+		checkDefaultJoinTablAndJoinColumnNames(
+				City.class,
+				"stolenItems",
+				null,
+				"tbl_city_ITEM",
+				"tbl_city_id",
+				"stolenItems_iId"
+		);
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-9390")
+	public void testUnidirOwnerEntityNamePrimaryTableOverride() {
+		// Category.clients: associated entity: KnownClient
+		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
+		// KnownClient has @Entity with no name configured and no @Table
+		// PK column for Category.id: id (default)
+		// PK column for KnownClient.id: id (default)
+		// unidirectional
+		// legacy behavior would use the table name in the generated join column.
+		checkDefaultJoinTablAndJoinColumnNames(
+				Category.class,
+				"clients",
+				null,
+				"CATEGORY_TAB_KnownClient",
+				"CATEGORY_TAB_id",
+				"clients_id"
+
+		);
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java
index b1329c7dbc..d059f9110a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/manytomany/defaults/ManyToManyDefaultsTest.java
@@ -1,266 +1,263 @@
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
-import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.type.EntityType;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests default names for @JoinTable and @JoinColumn for unidirectional and bidirectional
  * many-to-many associations.
  *
  * NOTE: expected primary table names and join columns are explicit here to ensure that
  * entity names/tables and PK columns are not changed (which would invalidate these test cases).
  *
  * @author Gail Badner
  */
 public class ManyToManyDefaultsTest  extends BaseCoreFunctionalTestCase {
 
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
-	@FailureExpected( jiraKey = "HHH-9390")
 	public void testUnidirOwnerPrimaryTableAssocEntityNamePKOverride() {
 		// City.stolenItems; associated entity: Item
 		// City has @Entity with no name configured and @Table(name = "tbl_city")
 		// Item has @Entity(name="ITEM") and no @Table
 		// PK column for City.id: id (default)
 		// PK column for Item: iId
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				City.class,
 				"stolenItems",
 				null,
 				"tbl_city_ITEM",
 				"City_id",
 				"stolenItems_iId"
 		);
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9390")
-	@FailureExpected( jiraKey = "HHH-9390")
 	public void testUnidirOwnerEntityNamePrimaryTableOverride() {
 		// Category.clients: associated entity: KnownClient
 		// Category has @Entity(name="CATEGORY") @Table(name="CATEGORY_TAB")
 		// KnownClient has @Entity with no name configured and no @Table
 		// PK column for Category.id: id (default)
 		// PK column for KnownClient.id: id (default)
 		// unidirectional
 		checkDefaultJoinTablAndJoinColumnNames(
 				Category.class,
 				"clients",
 				null,
 				"CATEGORY_TAB_KnownClient",
 				"CATEGORY_id",
 				"clients_id"
 
 		);
 	}
 
-	private void checkDefaultJoinTablAndJoinColumnNames(
+	protected void checkDefaultJoinTablAndJoinColumnNames(
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
index aa7946e346..697c4a2f7e 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/AvailableSettings.java
@@ -1,549 +1,554 @@
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
 	String PROVIDER = "javax.persistence.provider";
 
 	/**
 	 * The type of transactions supported by the entity managers.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.2
 	 */
 	String TRANSACTION_TYPE = "javax.persistence.transactionType";
 
 	/**
 	 * The JNDI name of a JTA {@link javax.sql.DataSource}.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.5
 	 */
 	String JTA_DATASOURCE = "javax.persistence.jtaDataSource";
 
 	/**
 	 * The JNDI name of a non-JTA {@link javax.sql.DataSource}.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.5
 	 */
 	String NON_JTA_DATASOURCE = "javax.persistence.nonJtaDataSource";
 
 	/**
 	 * The name of a JDBC driver to use to connect to the database.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_URL}, {@link #JDBC_USER} and {@link #JDBC_PASSWORD}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See section 8.2.1.9
 	 */
 	String JDBC_DRIVER = "javax.persistence.jdbc.driver";
 
 	/**
 	 * The JDBC connection url to use to connect to the database.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_USER} and {@link #JDBC_PASSWORD}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See section 8.2.1.9
 	 */
 	String JDBC_URL = "javax.persistence.jdbc.url";
 
 	/**
 	 * The JDBC connection user name.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_URL} and {@link #JDBC_PASSWORD}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See section 8.2.1.9
 	 */
 	String JDBC_USER = "javax.persistence.jdbc.user";
 
 	/**
 	 * The JDBC connection password.
 	 * <p/>
 	 * Used in conjunction with {@link #JDBC_DRIVER}, {@link #JDBC_URL} and {@link #JDBC_USER}
 	 * to define how to make connections to the database in lieu of
 	 * a datasource (either {@link #JTA_DATASOURCE} or {@link #NON_JTA_DATASOURCE}).
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	String JDBC_PASSWORD = "javax.persistence.jdbc.password";
 
 	/**
 	 * Used to indicate whether second-level (what JPA terms shared cache) caching is
 	 * enabled as per the rules defined in JPA 2 section 3.1.7.
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.7
 	 * @see javax.persistence.SharedCacheMode
 	 */
 	String SHARED_CACHE_MODE = "javax.persistence.sharedCache.mode";
 
 	/**
 	 * NOTE : Not a valid EMF property...
 	 * <p/>
 	 * Used to indicate if the provider should attempt to retrieve requested data
 	 * in the shared cache.
 	 *
 	 * @see javax.persistence.CacheRetrieveMode
 	 */
 	String SHARED_CACHE_RETRIEVE_MODE ="javax.persistence.cache.retrieveMode";
 
 	/**
 	 * NOTE : Not a valid EMF property...
 	 * <p/>
 	 * Used to indicate if the provider should attempt to store data loaded from the database
 	 * in the shared cache.
 	 *
 	 * @see javax.persistence.CacheStoreMode
 	 */
 	String SHARED_CACHE_STORE_MODE ="javax.persistence.cache.storeMode";
 
 	/**
 	 * Used to indicate what form of automatic validation is in effect as per rules defined
 	 * in JPA 2 section 3.6.1.1
 	 * <p/>
 	 * See JPA 2 sections 9.4.3 and 8.2.1.8
 	 * @see javax.persistence.ValidationMode
 	 */
 	String VALIDATION_MODE = "javax.persistence.validation.mode";
 
 	/**
 	 * Used to pass along any discovered validator factory.
 	 */
 	String VALIDATION_FACTORY = "javax.persistence.validation.factory";
 
 	/**
 	 * Used to request (hint) a pessimistic lock scope.
 	 * <p/>
 	 * See JPA 2 sections 8.2.1.9 and 3.4.4.3
 	 */
 	String LOCK_SCOPE = "javax.persistence.lock.scope";
 
 	/**
 	 * Used to request (hint) a pessimistic lock timeout (in milliseconds).
 	 * <p/>
 	 * See JPA 2 sections 8.2.1.9 and 3.4.4.3
 	 */
 	String LOCK_TIMEOUT = "javax.persistence.lock.timeout";
 
 	/**
 	 * Used to coordinate with bean validators
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	String PERSIST_VALIDATION_GROUP = "javax.persistence.validation.group.pre-persist";
 
 	/**
 	 * Used to coordinate with bean validators
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	String UPDATE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-update";
 
 	/**
 	 * Used to coordinate with bean validators
 	 * <p/>
 	 * See JPA 2 section 8.2.1.9
 	 */
 	String REMOVE_VALIDATION_GROUP = "javax.persistence.validation.group.pre-remove";
 
 	/**
 	 * Used to pass along the CDI BeanManager, if any, to be used.
 	 */
 	String CDI_BEAN_MANAGER = "javax.persistence.bean.manager";
 
 	/**
 	 * Specifies whether schema generation commands for schema creation are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SchemaGenSource} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #SCHEMA_GEN_CREATE_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SchemaGenSource
 	 */
 	String SCHEMA_GEN_CREATE_SOURCE = "javax.persistence.schema-generation.create-source";
 
 	/**
 	 * Specifies whether schema generation commands for schema dropping are to be determine based on object/relational
 	 * mapping metadata, DDL scripts, or a combination of the two.  See {@link SchemaGenSource} for valid set of values.
 	 * If no value is specified, a default is assumed as follows:<ul>
 	 *     <li>
 	 *         if source scripts are specified (per {@value #SCHEMA_GEN_DROP_SCRIPT_SOURCE}),then "scripts" is assumed
 	 *     </li>
 	 *     <li>
 	 *         otherwise, "metadata" is assumed
 	 *     </li>
 	 * </ul>
 	 *
 	 * @see SchemaGenSource
 	 */
 	String SCHEMA_GEN_DROP_SOURCE = "javax.persistence.schema-generation.drop-source";
 
 	/**
 	 * Specifies the CREATE script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 *
 	 * @see #SCHEMA_GEN_CREATE_SOURCE
 	 * @see #SCHEMA_GEN_DROP_SCRIPT_SOURCE
 	 */
 	String SCHEMA_GEN_CREATE_SCRIPT_SOURCE = "javax.persistence.schema-generation.create-script-source";
 
 	/**
 	 * Specifies the DROP script file as either a {@link java.io.Reader} configured for reading of the DDL script
 	 * file or a string designating a file {@link java.net.URL} for the DDL script.
 	 *
 	 * @see #SCHEMA_GEN_DROP_SOURCE
 	 * @see #SCHEMA_GEN_CREATE_SCRIPT_SOURCE
 	 */
 	String SCHEMA_GEN_DROP_SCRIPT_SOURCE = "javax.persistence.schema-generation.drop-script-source";
 
 	/**
 	 * Specifies the type of schema generation action to be taken by the persistence provider in regards to sending
 	 * commands directly to the database via JDBC.  See {@link SchemaGenAction} for the set of possible values.
 	 * <p/>
 	 * If no value is specified, the default is "none".
 	 *
 	 * @see SchemaGenAction
 	 */
 	String SCHEMA_GEN_DATABASE_ACTION = "javax.persistence.schema-generation.database.action";
 
 	/**
 	 * Specifies the type of schema generation action to be taken by the persistence provider in regards to writing
 	 * commands to DDL script files.  See {@link SchemaGenAction} for the set of possible values.
 	 * <p/>
 	 * If no value is specified, the default is "none".
 	 *
 	 * @see SchemaGenAction
 	 */
 	String SCHEMA_GEN_SCRIPTS_ACTION = "javax.persistence.schema-generation.scripts.action";
 
 	/**
 	 * For cases where the {@value #SCHEMA_GEN_SCRIPTS_ACTION} value indicates that schema creation commands should
 	 * be written to DDL script file, {@value #SCHEMA_GEN_SCRIPTS_CREATE_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #SCHEMA_GEN_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String SCHEMA_GEN_SCRIPTS_CREATE_TARGET = "javax.persistence.schema-generation.scripts.create-target";
 
 	/**
 	 * For cases where the {@value #SCHEMA_GEN_SCRIPTS_ACTION} value indicates that schema drop commands should
 	 * be written to DDL script file, {@value #SCHEMA_GEN_SCRIPTS_DROP_TARGET} specifies either a
 	 * {@link java.io.Writer} configured for output of the DDL script or a string specifying the file URL for the DDL
 	 * script.
 	 *
 	 * @see #SCHEMA_GEN_SCRIPTS_ACTION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String SCHEMA_GEN_SCRIPTS_DROP_TARGET = "javax.persistence.schema-generation.scripts.drop-target";
 
 	/**
 	 * Specifies whether the persistence provider is to create the database schema(s) in addition to creating
 	 * database objects (tables, sequences, constraints, etc).  The value of this boolean property should be set
 	 * to {@code true} if the persistence provider is to create schemas in the database or to generate DDL that
 	 * contains “CREATE SCHEMA” commands.  If this property is not supplied (or is explicitly {@code false}), the
 	 * provider should not attempt to create database schemas.
 	 */
 	String SCHEMA_GEN_CREATE_SCHEMAS = "javax.persistence.create-database-schemas";
 
 	/**
 	 * Allows passing the specific {@link java.sql.Connection} instance to be used for performing schema generation
 	 * where the target is "database".
 	 * <p/>
 	 * May also be used to determine the values for {@value #SCHEMA_GEN_DB_NAME},
 	 * {@value #SCHEMA_GEN_DB_MAJOR_VERSION} and {@value #SCHEMA_GEN_DB_MINOR_VERSION}.
 	 */
 	String SCHEMA_GEN_CONNECTION = "javax.persistence.schema-generation-connection";
 
 	/**
 	 * Specifies the name of the database provider in cases where a Connection to the underlying database is
 	 * not available (aka, mainly in generating scripts).  In such cases, a value for
 	 * {@value #SCHEMA_GEN_DB_NAME} *must* be specified.
 	 * <p/>
 	 * The value of this setting is expected to match the value returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseProductName()} for the target database.
 	 * <p/>
 	 * Additionally specifying {@value #SCHEMA_GEN_DB_MAJOR_VERSION} and/or {@value #SCHEMA_GEN_DB_MINOR_VERSION}
 	 * may be required to understand exactly how to generate the required schema commands.
 	 *
 	 * @see #SCHEMA_GEN_DB_MAJOR_VERSION
 	 * @see #SCHEMA_GEN_DB_MINOR_VERSION
 	 */
 	@SuppressWarnings("JavaDoc")
 	String SCHEMA_GEN_DB_NAME = "javax.persistence.database-product-name";
 
 	/**
 	 * Specifies the major version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where {@value #SCHEMA_GEN_DB_NAME} does not provide enough distinction.
 
 	 * @see #SCHEMA_GEN_DB_NAME
 	 * @see #SCHEMA_GEN_DB_MINOR_VERSION
 	 */
 	String SCHEMA_GEN_DB_MAJOR_VERSION = "javax.persistence.database-major-version";
 
 	/**
 	 * Specifies the minor version of the underlying database, as would be returned by
 	 * {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion} for the target database.  This value is used to
 	 * help more precisely determine how to perform schema generation tasks for the underlying database in cases
 	 * where te combination of {@value #SCHEMA_GEN_DB_NAME} and {@value #SCHEMA_GEN_DB_MAJOR_VERSION} does not provide
 	 * enough distinction.
 	 *
 	 * @see #SCHEMA_GEN_DB_NAME
 	 * @see #SCHEMA_GEN_DB_MAJOR_VERSION
 	 */
 	String SCHEMA_GEN_DB_MINOR_VERSION = "javax.persistence.database-minor-version";
 
 	/**
 	 * Specifies a {@link java.io.Reader} configured for reading of the SQL load script or a string designating the
 	 * file {@link java.net.URL} for the SQL load script.
 	 * <p/>
 	 * A "SQL load script" is a script that performs some database initialization (INSERT, etc).
 	 */
 	String SCHEMA_GEN_LOAD_SCRIPT_SOURCE = "javax.persistence.sql-load-script-source";
 
 
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
 	String ALIAS_SPECIFIC_LOCK_MODE = "org.hibernate.lockMode";
 
 	/**
 	 * JAR autodetection artifacts class, hbm
 	 */
 	String AUTODETECTION = "hibernate.archive.autodetection";
 
 	/**
 	 * cfg.xml configuration file used
 	 */
 	String CFG_FILE = "hibernate.ejb.cfgfile";
 
 	/**
 	 * Caching configuration should follow the following pattern
 	 * hibernate.ejb.classcache.<fully.qualified.Classname> usage[, region]
 	 * where usage is the cache strategy used and region the cache region name
 	 */
 	String CLASS_CACHE_PREFIX = "hibernate.ejb.classcache";
 
 	/**
 	 * Caching configuration should follow the following pattern
 	 * hibernate.ejb.collectioncache.<fully.qualified.Classname>.<role> usage[, region]
 	 * where usage is the cache strategy used and region the cache region name
 	 */
 	String COLLECTION_CACHE_PREFIX = "hibernate.ejb.collectioncache";
 
 	/**
 	 * Interceptor class name, the class has to have a no-arg constructor
 	 * the interceptor instance is shared amongst all EntityManager of a given EntityManagerFactory
 	 */
 	String INTERCEPTOR = "hibernate.ejb.interceptor";
 
 	/**
 	 * Interceptor class name, the class has to have a no-arg constructor
 	 */
 	String SESSION_INTERCEPTOR = "hibernate.ejb.interceptor.session_scoped";
 
 	/**
 	 * SessionFactoryObserver class name, the class must have a no-arg constructor
 	 */
 	String SESSION_FACTORY_OBSERVER = "hibernate.ejb.session_factory_observer";
 
 	/**
 	 * Naming strategy class name, the class has to have a no-arg constructor
 	 */
 	String NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
 
 	/**
+	 * Naming strategy delegator class name, the class has to have a no-arg constructor that returns a non-null value for {@link }
+	 */
+	public static final String NAMING_STRATEGY_DELEGATOR = "hibernate.ejb.naming_strategy_delegator";
+
+	/**
 	 * IdentifierGeneratorStrategyProvider class name, the class must have a no-arg constructor
 	 * @deprecated if possible wait of Hibernate 4.1 and theService registry (MutableIdentifierGeneratorStrategy service)
 	 */
 	String IDENTIFIER_GENERATOR_STRATEGY_PROVIDER = "hibernate.ejb.identifier_generator_strategy_provider";
 
 	/**
 	 * Event configuration should follow the following pattern
 	 * hibernate.ejb.event.[eventType] f.q.c.n.EventListener1, f.q.c.n.EventListener12 ...
 	 */
 	String EVENT_LISTENER_PREFIX = "hibernate.ejb.event";
 
 	/**
 	 * Enable the class file enhancement
 	 */
 	String USE_CLASS_ENHANCER = "hibernate.ejb.use_class_enhancer";
 
 	/**
 	 * Whether or not discard persistent context on entityManager.close()
 	 * The EJB3 compliant and default choice is false
 	 */
 	String DISCARD_PC_ON_CLOSE = "hibernate.ejb.discard_pc_on_close";
 
 	/**
 	 * Consider this as experimental
 	 * It is not recommended to set up this property, the configuration is stored
 	 * in the JNDI in a serialized form
 	 *
 	 * @deprecated Configuration going away.
 	 */
 	@Deprecated
 	String CONFIGURATION_JNDI_NAME = "hibernate.ejb.configuration_jndi_name";
 
 	/**
 	 * Used to determine flush mode.
 	 */
 	//Use the org.hibernate prefix. instead of hibernate. as it is a query hint se QueryHints
 	String FLUSH_MODE = "org.hibernate.flushMode";
 
 	/**
 	 * Pass an implementation of {@link org.hibernate.ejb.packaging.Scanner}:
 	 *  - preferably an actual instance
 	 *  - or a class name with a no-arg constructor 
 	 */
 	String SCANNER = "hibernate.ejb.resource_scanner";
 
 	/**
 	 * List of classes names
 	 * Internal use only
 	 *
 	 * @deprecated Was never intended for external use
 	 */
 	@Deprecated
 	@SuppressWarnings("UnusedDeclaration")
 	String CLASS_NAMES = "hibernate.ejb.classes";
 
 	/**
 	 * List of annotated packages
 	 * Internal use only
 	 *
 	 * @deprecated Was never intended for external use
 	 */
 	@Deprecated
 	@SuppressWarnings("UnusedDeclaration")
 	String PACKAGE_NAMES = "hibernate.ejb.packages";
 
 	/**
 	 * EntityManagerFactory name
 	 */
 	String ENTITY_MANAGER_FACTORY_NAME = "hibernate.ejb.entitymanager_factory_name";
 
 	/**
 	 * @deprecated use {@link #JPA_METAMODEL_POPULATION} instead.
 	 */
 	@Deprecated
 	String JPA_METAMODEL_GENERATION = "hibernate.ejb.metamodel.generation";
 
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
 	String JPA_METAMODEL_POPULATION = "hibernate.ejb.metamodel.population";
 
 
 	/**
 	 * List of classes names
 	 * Internal use only
 	 */
 	String XML_FILE_NAMES = "hibernate.ejb.xml_files";
 	String HBXML_FILES = "hibernate.hbmxml.files";
 	String LOADED_CLASSES = "hibernate.ejb.loaded.classes";
 
 	/**
 	 * Deprecated
 	 *
 	 * @deprecated Use {@link org.hibernate.cfg.AvailableSettings#JACC_CONTEXT_ID} instead
 	 */
 	@Deprecated
 	String JACC_CONTEXT_ID = org.hibernate.cfg.AvailableSettings.JACC_CONTEXT_ID;
 
 	/**
 	 * Deprecated
 	 *
 	 * @deprecated Use {@link org.hibernate.cfg.AvailableSettings#JACC_PREFIX} instead
 	 */
 	@Deprecated
 	String JACC_PREFIX = org.hibernate.cfg.AvailableSettings.JACC_PREFIX;
 
 	/**
 	 * Deprecated
 	 *
 	 * @deprecated Use {@link org.hibernate.cfg.AvailableSettings#JACC_ENABLED} instead
 	 */
 	@Deprecated
 	String JACC_ENABLED = org.hibernate.cfg.AvailableSettings.JACC_ENABLED;
 
 	/**
 	 * Used to pass along the name of the persistence unit.
 	 */
 	String PERSISTENCE_UNIT_NAME = "hibernate.ejb.persistenceUnitName";
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
index fd30a8b062..86d4ee51ff 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/jpa/boot/internal/EntityManagerFactoryBuilderImpl.java
@@ -1,1310 +1,1324 @@
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
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
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
 import org.hibernate.jpa.boot.spi.TypeContributorList;
 import org.hibernate.jpa.event.spi.JpaIntegrator;
 import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
 import org.hibernate.jpa.internal.EntityManagerMessageLogger;
 import org.hibernate.jpa.internal.schemagen.JpaSchemaGenerator;
 import org.hibernate.jpa.internal.util.LogHelper;
 import org.hibernate.jpa.internal.util.PersistenceUnitTransactionTypeHelper;
 import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 import org.hibernate.metamodel.spi.TypeContributor;
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
 	
 	/**
 	 * Names a {@link TypeContributorList}
 	 */
 	public static final String TYPE_CONTRIBUTORS = "hibernate.type_contributors";
 
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
+	private NamingStrategyDelegator namingStrategyDelegator;
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
 								JandexHelper.getValue( converterAnnotation, "autoApply", boolean.class,
 										bootstrapServiceRegistry.getService( ClassLoaderService.class ) )
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
 
 		final String cfgXmlResourceName1 = (String) merged.remove( AvailableSettings.CFG_FILE );
 		if ( StringHelper.isNotEmpty( cfgXmlResourceName1 ) ) {
 			// it does, so load those properties
 			JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue()
 					.loadConfigXmlResource( cfgXmlResourceName1 );
 			processHibernateConfigurationElement( configurationElement, merged );
 		}
 
 		// see if integration settings named a Hibernate config file....
 		final String cfgXmlResourceName2 = (String) integrationSettings.get( AvailableSettings.CFG_FILE );
 		if ( StringHelper.isNotEmpty( cfgXmlResourceName2 ) ) {
 			integrationSettings.remove( AvailableSettings.CFG_FILE );
 			// it does, so load those properties
 			JaxbHibernateConfiguration configurationElement = configLoaderHolder.getValue().loadConfigXmlResource(
 					cfgXmlResourceName2
 			);
 			processHibernateConfigurationElement( configurationElement, merged );
 		}
 
 		// finally, apply integration-supplied settings (per JPA spec, integration settings should override other sources)
 		merged.putAll( integrationSettings );
 
 		if ( !merged.containsKey( AvailableSettings.VALIDATION_MODE ) ) {
 			if ( persistenceUnit.getValidationMode() != null ) {
 				merged.put( AvailableSettings.VALIDATION_MODE, persistenceUnit.getValidationMode() );
 			}
 		}
 
 		if ( !merged.containsKey( AvailableSettings.SHARED_CACHE_MODE ) ) {
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
 						
 						// This seems overkill, but building the SF is necessary to get the Integrators to kick in.
 						// Metamodel will clean this up...
 						try {
 							hibernateConfiguration.buildSessionFactory( serviceRegistry );
 						}
 						catch (MappingException e) {
 							throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 						}
 						
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
 
 						SessionFactoryImplementor sessionFactory;
 						try {
 							sessionFactory = (SessionFactoryImplementor) hibernateConfiguration.buildSessionFactory( serviceRegistry );
 						}
 						catch (MappingException e) {
 							throw persistenceException( "Unable to build Hibernate SessionFactory", e );
 						}
 						
 						// must do after buildSessionFactory to let the Integrators kick in
 						JpaSchemaGenerator.performGeneration( hibernateConfiguration, serviceRegistry );
 
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
 			configurationValues.put( AvailableSettings.VALIDATION_FACTORY, this.validatorFactory );
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
+				else if ( AvailableSettings.NAMING_STRATEGY_DELEGATOR.equals( keyString ) ) {
+					namingStrategyDelegator = strategySelector.resolveStrategy( NamingStrategyDelegator.class, entry.getValue() );
+				}
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
 		Configuration cfg = new Configuration();
 		cfg.getProperties().putAll( props );
 
 		cfg.setEntityNotFoundDelegate( jpaEntityNotFoundDelegate );
 
+		if ( namingStrategy != null && namingStrategyDelegator != null ) {
+			throw persistenceException(
+					AvailableSettings.NAMING_STRATEGY + " and " + AvailableSettings.NAMING_STRATEGY_DELEGATOR +
+							" properties cannot be used together. To be valid, only one of these properties can be set."
+			);
+		}
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
+		else if ( namingStrategyDelegator != null ) {
+			cfg.setNamingStrategyDelegator( namingStrategyDelegator );
+		}
 
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
 				if ( AttributeConverter.class.isAssignableFrom( cls ) ) {
 					cfg.addAttributeConverter( (Class<? extends AttributeConverter>) cls );
 				}
 				else {
 					cfg.addAnnotatedClass( cls );
 				}
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
 		
 		final TypeContributorList typeContributorList
 				= (TypeContributorList) configurationValues.get( TYPE_CONTRIBUTORS );
 		if ( typeContributorList != null ) {
 			configurationValues.remove( TYPE_CONTRIBUTORS );
 			for ( TypeContributor typeContributor : typeContributorList.getTypeContributors() ) {
 				cfg.registerTypeContributor( typeContributor );
 			}
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
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyLegacyNamingStrategyDelegator.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyLegacyNamingStrategyDelegator.java
new file mode 100644
index 0000000000..eee6eb4652
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyLegacyNamingStrategyDelegator.java
@@ -0,0 +1,54 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test;
+
+import org.hibernate.cfg.naming.HbmNamingStrategyDelegate;
+import org.hibernate.cfg.naming.JpaNamingStrategyDelegate;
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
+
+/**
+ * @author Gail Badner
+ */
+public class MyLegacyNamingStrategyDelegator extends LegacyNamingStrategyDelegator {
+	private final NamingStrategyDelegate hbmNamingStrategyDelegate = new HbmNamingStrategyDelegate();
+	private final NamingStrategyDelegate nonHbmNamingStrategyDelegate = new MyNonHbmNamingStrategyDelegate();
+
+	public MyLegacyNamingStrategyDelegator() {
+		super( new MyNamingStrategy() );
+	}
+
+	@Override
+	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
+		return isHbm ? hbmNamingStrategyDelegate :nonHbmNamingStrategyDelegate;
+	}
+
+	private class MyNonHbmNamingStrategyDelegate extends JpaNamingStrategyDelegate {
+
+		@Override
+		public String toPhysicalColumnName(String columnName) {
+			return super.toPhysicalColumnName( "c_" + columnName );
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java
new file mode 100644
index 0000000000..27a2eea330
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/MyNamingStrategyDelegator.java
@@ -0,0 +1,50 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.jpa.test;
+
+import org.hibernate.cfg.naming.HbmNamingStrategyDelegate;
+import org.hibernate.cfg.naming.JpaNamingStrategyDelegate;
+import org.hibernate.cfg.naming.NamingStrategyDelegate;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
+
+/**
+ * @author Gail Badner
+ */
+public class MyNamingStrategyDelegator implements NamingStrategyDelegator {
+	private final NamingStrategyDelegate hbmNamingStrategyDelegate = new HbmNamingStrategyDelegate();
+	private final NamingStrategyDelegate nonHbmNamingStrategyDelegate = new MyNonHbmNamingStrategyDelegate();
+
+	@Override
+	public NamingStrategyDelegate getNamingStrategyDelegate(boolean isHbm) {
+		return isHbm ? hbmNamingStrategyDelegate :nonHbmNamingStrategyDelegate;
+	}
+
+	private class MyNonHbmNamingStrategyDelegate extends JpaNamingStrategyDelegate {
+
+		@Override
+		public String toPhysicalColumnName(String columnName) {
+			return super.toPhysicalColumnName( "c_" + columnName );
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java
new file mode 100644
index 0000000000..8abfe53831
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/ejb3configuration/NamingStrategyDelegatorConfigurationTest.java
@@ -0,0 +1,119 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2014, Red Hat, Inc. and/or its affiliates or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat, Inc.
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
+package org.hibernate.jpa.test.ejb3configuration;
+
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.Map;
+import javax.persistence.PersistenceException;
+
+import org.junit.Test;
+
+import org.hibernate.cfg.naming.LegacyNamingStrategyDelegator;
+import org.hibernate.cfg.naming.NamingStrategyDelegator;
+import org.hibernate.ejb.AvailableSettings;
+import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
+import org.hibernate.jpa.boot.spi.Bootstrap;
+import org.hibernate.jpa.test.MyNamingStrategy;
+import org.hibernate.jpa.test.MyNamingStrategyDelegator;
+import org.hibernate.jpa.test.PersistenceUnitInfoAdapter;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
+
+
+/**
+ * @author Gail Badner
+ */
+public class NamingStrategyDelegatorConfigurationTest extends BaseUnitTestCase {
+
+	@Test
+	public void testNamingStrategyDelegatorFromProperty() {
+
+		// configure NamingStrategy
+		{
+			PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
+			EntityManagerFactoryBuilderImpl builder = (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(
+					adapter,
+					Collections.singletonMap( AvailableSettings.NAMING_STRATEGY, MyNamingStrategy.class.getName() )
+			);
+			assertEquals(
+					MyNamingStrategy.class.getName(),
+					builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY )
+			);
+			assertEquals( null, builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY_DELEGATOR ) );
+			builder.build();
+			final NamingStrategyDelegator namingStrategyDelegator =
+					builder.getHibernateConfiguration().getNamingStrategyDelegator();
+			assertTrue( LegacyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) );
+			assertTrue(
+					MyNamingStrategy.class.isInstance(
+							( (LegacyNamingStrategyDelegator)namingStrategyDelegator ).getNamingStrategy()
+					)
+			);
+		}
+
+		// configure NamingStrategyDelegator
+		{
+			PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
+			EntityManagerFactoryBuilderImpl builder = (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(
+					adapter,
+					Collections.singletonMap(
+							AvailableSettings.NAMING_STRATEGY_DELEGATOR,
+							MyNamingStrategyDelegator.class.getName()
+					)
+			);
+			assertEquals( null, builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY ) );
+			assertEquals(
+					MyNamingStrategyDelegator.class.getName(),
+					builder.getConfigurationValues().get( AvailableSettings.NAMING_STRATEGY_DELEGATOR )
+			);
+			builder.build();
+			final NamingStrategyDelegator namingStrategyDelegator =
+					builder.getHibernateConfiguration().getNamingStrategyDelegator();
+			assertTrue( MyNamingStrategyDelegator.class.isInstance( namingStrategyDelegator ) );
+		}
+
+		// configure NamingStrategy and NamingStrategyDelegator
+		{
+			PersistenceUnitInfoAdapter adapter = new PersistenceUnitInfoAdapter();
+			final Map<String,String> integrationArgs = new HashMap<String,String>();
+			integrationArgs.put( AvailableSettings.NAMING_STRATEGY, MyNamingStrategy.class.getName() );
+			integrationArgs.put( AvailableSettings.NAMING_STRATEGY_DELEGATOR, MyNamingStrategyDelegator.class.getName() );
+			try {
+				EntityManagerFactoryBuilderImpl builder =  (EntityManagerFactoryBuilderImpl) Bootstrap.getEntityManagerFactoryBuilder(
+						adapter,
+						integrationArgs
+				);
+				builder.build();
+				fail( "Should have thrown a PersistenceException because setting both properties is not allowed." );
+			}
+			catch (PersistenceException ex) {
+				// expected
+			}
+		}
+	}
+}
