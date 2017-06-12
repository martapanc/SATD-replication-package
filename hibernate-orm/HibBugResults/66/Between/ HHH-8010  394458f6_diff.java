diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
index dc2b5c301b..ccba37c693 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/classloading/internal/ClassLoaderServiceImpl.java
@@ -1,329 +1,336 @@
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
 package org.hibernate.boot.registry.classloading.internal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.ServiceLoader;
 
-import org.jboss.logging.Logger;
-
-import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.internal.util.ClassLoaderHelper;
+import org.jboss.logging.Logger;
 
 /**
  * Standard implementation of the service for interacting with class loaders
  *
  * @author Steve Ebersole
  */
 public class ClassLoaderServiceImpl implements ClassLoaderService {
 	private static final Logger log = Logger.getLogger( ClassLoaderServiceImpl.class );
 
 	private final ClassLoader aggregatedClassLoader;
 
 	public ClassLoaderServiceImpl() {
 		this( ClassLoaderServiceImpl.class.getClassLoader() );
 	}
 
 	public ClassLoaderServiceImpl(ClassLoader classLoader) {
 		this( Collections.singletonList( classLoader ) );
 	}
 
 	public ClassLoaderServiceImpl(List<ClassLoader> providedClassLoaders) {
 		final LinkedHashSet<ClassLoader> orderedClassLoaderSet = new LinkedHashSet<ClassLoader>();
 
 		// first add all provided class loaders, if any
 		if ( providedClassLoaders != null ) {
 			for ( ClassLoader classLoader : providedClassLoaders ) {
 				if ( classLoader != null ) {
 					orderedClassLoaderSet.add( classLoader );
 				}
 			}
 		}
 
 		// normalize adding known class-loaders...
-		// first the Hibernate class loader
+		// first, the "overridden" classloader provided by an environment (OSGi, etc.)
+		// TODO: This should probably be wired into BootstrapServiceRegistryBuilder
+		// instead, however that wasn't available in 4.2.  Once JPA 2.1 is testable
+		// in an OSGi container, move this and re-work.
+		if ( ClassLoaderHelper.overridenClassLoader != null ) {
+			orderedClassLoaderSet.add( ClassLoaderHelper.overridenClassLoader );
+		}
+		// then the Hibernate class loader
 		orderedClassLoaderSet.add( ClassLoaderServiceImpl.class.getClassLoader() );
 		// then the TCCL, if one...
 		final ClassLoader tccl = locateTCCL();
 		if ( tccl != null ) {
 			orderedClassLoaderSet.add( tccl );
 		}
 		// finally the system classloader
 		final ClassLoader sysClassLoader = locateSystemClassLoader();
 		if ( sysClassLoader != null ) {
 			orderedClassLoaderSet.add( sysClassLoader );
 		}
 
 		// now build the aggregated class loader...
 		this.aggregatedClassLoader = new AggregatedClassLoader( orderedClassLoaderSet );
 	}
 
 	@SuppressWarnings({"UnusedDeclaration", "unchecked", "deprecation"})
 	@Deprecated
 	public static ClassLoaderServiceImpl fromConfigSettings(Map configVales) {
 		final List<ClassLoader> providedClassLoaders = new ArrayList<ClassLoader>();
 
 		final Collection<ClassLoader> classLoaders = (Collection<ClassLoader>) configVales.get( AvailableSettings.CLASSLOADERS );
 		if ( classLoaders != null ) {
 			for ( ClassLoader classLoader : classLoaders ) {
 				providedClassLoaders.add( classLoader );
 			}
 		}
 
 		addIfSet( providedClassLoaders, AvailableSettings.APP_CLASSLOADER, configVales );
 		addIfSet( providedClassLoaders, AvailableSettings.RESOURCES_CLASSLOADER, configVales );
 		addIfSet( providedClassLoaders, AvailableSettings.HIBERNATE_CLASSLOADER, configVales );
 		addIfSet( providedClassLoaders, AvailableSettings.ENVIRONMENT_CLASSLOADER, configVales );
 
 		return new ClassLoaderServiceImpl( providedClassLoaders );
 	}
 
 	private static void addIfSet(List<ClassLoader> providedClassLoaders, String name, Map configVales) {
 		final ClassLoader providedClassLoader = (ClassLoader) configVales.get( name );
 		if ( providedClassLoader != null ) {
 			providedClassLoaders.add( providedClassLoader );
 		}
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
 
 	private static class AggregatedClassLoader extends ClassLoader {
 		private final ClassLoader[] individualClassLoaders;
 
 		private AggregatedClassLoader(final LinkedHashSet<ClassLoader> orderedClassLoaderSet) {
 			super( null );
 			individualClassLoaders = orderedClassLoaderSet.toArray( new ClassLoader[ orderedClassLoaderSet.size() ] );
 		}
 
 		@Override
 		public Enumeration<URL> getResources(String name) throws IOException {
 			final HashSet<URL> resourceUrls = new HashSet<URL>();
 
 			for ( ClassLoader classLoader : individualClassLoaders ) {
 				final Enumeration<URL> urls = classLoader.getResources( name );
 				while ( urls.hasMoreElements() ) {
 					resourceUrls.add( urls.nextElement() );
 				}
 			}
 
 			return new Enumeration<URL>() {
 				final Iterator<URL> resourceUrlIterator = resourceUrls.iterator();
 				@Override
 				public boolean hasMoreElements() {
 					return resourceUrlIterator.hasNext();
 				}
 
 				@Override
 				public URL nextElement() {
 					return resourceUrlIterator.next();
 				}
 			};
 		}
 
 		@Override
 		protected URL findResource(String name) {
 			for ( ClassLoader classLoader : individualClassLoaders ) {
 				final URL resource = classLoader.getResource( name );
 				if ( resource != null ) {
 					return resource;
 				}
 			}
 			return super.findResource( name );
 		}
 
 		@Override
 		protected Class<?> findClass(String name) throws ClassNotFoundException {
 			for ( ClassLoader classLoader : individualClassLoaders ) {
 				try {
 					return classLoader.loadClass( name );
 				}
 				catch (Exception ignore) {
 				}
 			}
 
 			throw new ClassNotFoundException( "Could not load requested class : " + name );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> classForName(String className) {
 		try {
 			return (Class<T>) Class.forName( className, true, aggregatedClassLoader );
 		}
 		catch (Exception e) {
 			throw new ClassLoadingException( "Unable to load class [" + className + "]", e );
 		}
 	}
 
 	@Override
 	public URL locateResource(String name) {
 		// first we try name as a URL
 		try {
 			return new URL( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			return aggregatedClassLoader.getResource( name );
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return null;
 	}
 
 	@Override
 	public InputStream locateResourceStream(String name) {
 		// first we try name as a URL
 		try {
 			log.tracef( "trying via [new URL(\"%s\")]", name );
 			return new URL( name ).openStream();
 		}
 		catch ( Exception ignore ) {
 		}
 
 		try {
 			log.tracef( "trying via [ClassLoader.getResourceAsStream(\"%s\")]", name );
 			InputStream stream =  aggregatedClassLoader.getResourceAsStream( name );
 			if ( stream != null ) {
 				return stream;
 			}
 		}
 		catch ( Exception ignore ) {
 		}
 
 		final String stripped = name.startsWith( "/" ) ? name.substring(1) : null;
 
 		if ( stripped != null ) {
 			try {
 				log.tracef( "trying via [new URL(\"%s\")]", stripped );
 				return new URL( stripped ).openStream();
 			}
 			catch ( Exception ignore ) {
 			}
 
 			try {
 				log.tracef( "trying via [ClassLoader.getResourceAsStream(\"%s\")]", stripped );
 				InputStream stream = aggregatedClassLoader.getResourceAsStream( stripped );
 				if ( stream != null ) {
 					return stream;
 				}
 			}
 			catch ( Exception ignore ) {
 			}
 		}
 
 		return null;
 	}
 
 	@Override
 	public List<URL> locateResources(String name) {
 		ArrayList<URL> urls = new ArrayList<URL>();
 		try {
 			Enumeration<URL> urlEnumeration = aggregatedClassLoader.getResources( name );
 			if ( urlEnumeration != null && urlEnumeration.hasMoreElements() ) {
 				while ( urlEnumeration.hasMoreElements() ) {
 					urls.add( urlEnumeration.nextElement() );
 				}
 			}
 		}
 		catch ( Exception ignore ) {
 		}
 
 		return urls;
 	}
 
 	@Override
 	public <S> LinkedHashSet<S> loadJavaServices(Class<S> serviceContract) {
 		final ServiceLoader<S> loader = ServiceLoader.load( serviceContract, aggregatedClassLoader );
 		final LinkedHashSet<S> services = new LinkedHashSet<S>();
 		for ( S service : loader ) {
 			services.add( service );
 		}
 
 		return services;
 	}
 
 	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 	// completely temporary !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 
 	public static interface Work<T> {
 		public T perform();
 	}
 
 	public <T> T withTccl(Work<T> work) {
 		final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 
 		boolean set = false;
 
 		try {
 			Thread.currentThread().setContextClassLoader( aggregatedClassLoader );
 			set = true;
 		}
 		catch (Exception ignore) {
 		}
 
 		try {
 			return work.perform();
 		}
 		finally {
 			if ( set ) {
 				Thread.currentThread().setContextClassLoader( tccl );
 			}
 		}
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index cb3b2879a1..dbd12237ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,2395 +1,2403 @@
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
-		ClassLoader contextClassLoader = ClassLoaderHelper.getClassLoader();
+		ClassLoader contextClassLoader = ClassLoaderHelper.getContextClassLoader();
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
+		
+		// TEMPORARY
+		// Ensure the correct ClassLoader is used in commons-annotations.
+		ClassLoader tccl = Thread.currentThread().getContextClassLoader();
+		Thread.currentThread().setContextClassLoader( ClassLoaderHelper.getContextClassLoader() );
 
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
 						? "UK_" + table.getName() + "_" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
+		
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
+		
+		Thread.currentThread().setContextClassLoader( tccl );
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
 			final String logicalColumnName = StringHelper.isNotEmpty( column ) ? normalizer.normalizeIdentifierQuoting( column ) : "";
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
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 		LOG.jaccContextId( contextId );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
index 54fd38430d..3475b2310c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/BlobProxy.java
@@ -1,245 +1,245 @@
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
 package org.hibernate.engine.jdbc;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.Blob;
 import java.sql.SQLException;
 
 import org.hibernate.engine.jdbc.internal.BinaryStreamImpl;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.type.descriptor.java.DataHelper;
 
 /**
  * Manages aspects of proxying {@link Blob} references for non-contextual creation, including proxy creation and
  * handling proxy invocations.  We use proxies here solely to avoid JDBC version incompatibilities.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class BlobProxy implements InvocationHandler {
 	private static final Class[] PROXY_INTERFACES = new Class[] { Blob.class, BlobImplementer.class };
 
 	private BinaryStream binaryStream;
 	private boolean needsReset = false;
 
 	/**
 	 * Constructor used to build {@link Blob} from byte array.
 	 *
 	 * @param bytes The byte array
 	 * @see #generateProxy(byte[])
 	 */
 	private BlobProxy(byte[] bytes) {
 		binaryStream = new BinaryStreamImpl( bytes );
 	}
 
 	/**
 	 * Constructor used to build {@link Blob} from a stream.
 	 *
 	 * @param stream The binary stream
 	 * @param length The length of the stream
 	 * @see #generateProxy(java.io.InputStream, long)
 	 */
 	private BlobProxy(InputStream stream, long length) {
 		this.binaryStream = new StreamBackedBinaryStream( stream, length );
 	}
 
 	private long getLength() {
 		return binaryStream.getLength();
 	}
 
 	private InputStream getStream() throws SQLException {
 		InputStream stream = binaryStream.getInputStream();
 		try {
 			if ( needsReset ) {
 				stream.reset();
 			}
 		}
 		catch ( IOException ioe) {
 			throw new SQLException("could not reset reader");
 		}
 		needsReset = true;
 		return stream;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @throws UnsupportedOperationException if any methods other than
 	 * {@link Blob#length}, {@link Blob#getUnderlyingStream},
 	 * {@link Blob#getBinaryStream}, {@link Blob#getBytes}, {@link Blob#free},
 	 * or toString/equals/hashCode are invoked.
 	 */
 	@Override
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		final String methodName = method.getName();
 		final int argCount = method.getParameterTypes().length;
 
 		if ( "length".equals( methodName ) && argCount == 0 ) {
 			return Long.valueOf( getLength() );
 		}
 		if ( "getUnderlyingStream".equals( methodName ) ) {
 			return binaryStream;
 		}
 		if ( "getBinaryStream".equals( methodName ) ) {
 			if ( argCount == 0 ) {
 				return getStream();
 			}
 			else if ( argCount == 2 ) {
 				long start = (Long) args[0];
 				if ( start < 1 ) {
 					throw new SQLException( "Start position 1-based; must be 1 or more." );
 				}
 				if ( start > getLength() ) {
 					throw new SQLException( "Start position [" + start + "] cannot exceed overall CLOB length [" + getLength() + "]" );
 				}
 				int length = (Integer) args[1];
 				if ( length < 0 ) {
 					// java docs specifically say for getBinaryStream(long,int) that the start+length must not exceed the
 					// total length, however that is at odds with the getBytes(long,int) behavior.
 					throw new SQLException( "Length must be great-than-or-equal to zero." );
 				}
 				return DataHelper.subStream( getStream(), start-1, length );
 			}
 		}
 		if ( "getBytes".equals( methodName ) ) {
 			if ( argCount == 2 ) {
 				long start = (Long) args[0];
 				if ( start < 1 ) {
 					throw new SQLException( "Start position 1-based; must be 1 or more." );
 				}
 				int length = (Integer) args[1];
 				if ( length < 0 ) {
 					throw new SQLException( "Length must be great-than-or-equal to zero." );
 				}
 				return DataHelper.extractBytes( getStream(), start-1, length );
 			}
 		}
 		if ( "free".equals( methodName ) && argCount == 0 ) {
 			binaryStream.release();
 			return null;
 		}
 		if ( "toString".equals( methodName ) && argCount == 0 ) {
 			return this.toString();
 		}
 		if ( "equals".equals( methodName ) && argCount == 1 ) {
 			return Boolean.valueOf( proxy == args[0] );
 		}
 		if ( "hashCode".equals( methodName ) && argCount == 0 ) {
 			return this.hashCode();
 		}
 
 		throw new UnsupportedOperationException( "Blob may not be manipulated from creating session" );
 	}
 
 	/**
 	 * Generates a BlobImpl proxy using byte data.
 	 *
 	 * @param bytes The data to be created as a Blob.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Blob generateProxy(byte[] bytes) {
 		return ( Blob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new BlobProxy( bytes )
 		);
 	}
 
 	/**
 	 * Generates a BlobImpl proxy using a given number of bytes from an InputStream.
 	 *
 	 * @param stream The input stream of bytes to be created as a Blob.
 	 * @param length The number of bytes from stream to be written to the Blob.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Blob generateProxy(InputStream stream, long length) {
 		return ( Blob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new BlobProxy( stream, length )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	private static ClassLoader getProxyClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = BlobImplementer.class.getClassLoader();
 		}
 		return cl;
 	}
 
 	private static class StreamBackedBinaryStream implements BinaryStream {
 		private final InputStream stream;
 		private final long length;
 
 		private byte[] bytes;
 
 		private StreamBackedBinaryStream(InputStream stream, long length) {
 			this.stream = stream;
 			this.length = length;
 		}
 
 		@Override
 		public InputStream getInputStream() {
 			return stream;
 		}
 
 		@Override
 		public byte[] getBytes() {
 			if ( bytes == null ) {
 				bytes = DataHelper.extractBytes( stream );
 			}
 			return bytes;
 		}
 
 		@Override
 		public long getLength() {
 			return (int) length;
 		}
 
 		@Override
 		public void release() {
 			try {
 				stream.close();
 			}
 			catch (IOException ignore) {
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
index 97fbe4cc26..f45489f2d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ClobProxy.java
@@ -1,225 +1,225 @@
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
 package org.hibernate.engine.jdbc;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.io.StringReader;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.Clob;
 import java.sql.SQLException;
 
 import org.hibernate.engine.jdbc.internal.CharacterStreamImpl;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.type.descriptor.java.DataHelper;
 
 /**
  * Manages aspects of proxying {@link Clob Clobs} for non-contextual creation, including proxy creation and
  * handling proxy invocations.  We use proxies here solely to avoid JDBC version incompatibilities.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class ClobProxy implements InvocationHandler {
 	private static final Class[] PROXY_INTERFACES = new Class[] { Clob.class, ClobImplementer.class };
 
 	private final CharacterStream characterStream;
 	private boolean needsReset = false;
 
 	/**
 	 * Constructor used to build {@link Clob} from string data.
 	 *
 	 * @param string The byte array
 	 * @see #generateProxy(String)
 	 */
 	protected ClobProxy(String string) {
 		this.characterStream = new CharacterStreamImpl( string );
 	}
 
 	/**
 	 * Constructor used to build {@link Clob} from a reader.
 	 *
 	 * @param reader The character reader.
 	 * @param length The length of the reader stream.
 	 * @see #generateProxy(java.io.Reader, long)
 	 */
 	protected ClobProxy(Reader reader, long length) {
 		this.characterStream = new CharacterStreamImpl( reader, length );
 	}
 
 	protected long getLength() {
 		return characterStream.getLength();
 	}
 
 	protected InputStream getAsciiStream() throws SQLException {
 		resetIfNeeded();
 		return new ReaderInputStream( characterStream.asReader() );
 	}
 
 	protected Reader getCharacterStream() throws SQLException {
 		resetIfNeeded();
 		return characterStream.asReader();
 	}
 
 	protected String getSubString(long start, int length) {
 		final String string = characterStream.asString();
 		// semi-naive implementation
 		int endIndex = Math.min( ((int)start)+length, string.length() );
 		return string.substring( (int)start, endIndex );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 *
 	 * @throws UnsupportedOperationException if any methods other than {@link Clob#length()},
 	 * {@link Clob#getAsciiStream()}, or {@link Clob#getCharacterStream()} are invoked.
 	 */
 	@Override
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		final String methodName = method.getName();
 		final int argCount = method.getParameterTypes().length;
 
 		if ( "length".equals( methodName ) && argCount == 0 ) {
 			return Long.valueOf( getLength() );
 		}
 		if ( "getUnderlyingStream".equals( methodName ) ) {
 			return characterStream;
 		}
 		if ( "getAsciiStream".equals( methodName ) && argCount == 0 ) {
 			return getAsciiStream();
 		}
 		if ( "getCharacterStream".equals( methodName ) ) {
 			if ( argCount == 0 ) {
 				return getCharacterStream();
 			}
 			else if ( argCount == 2 ) {
 				long start = (Long) args[0];
 				if ( start < 1 ) {
 					throw new SQLException( "Start position 1-based; must be 1 or more." );
 				}
 				if ( start > getLength() ) {
 					throw new SQLException( "Start position [" + start + "] cannot exceed overall CLOB length [" + getLength() + "]" );
 				}
 				int length = (Integer) args[1];
 				if ( length < 0 ) {
 					// java docs specifically say for getCharacterStream(long,int) that the start+length must not exceed the
 					// total length, however that is at odds with the getSubString(long,int) behavior.
 					throw new SQLException( "Length must be great-than-or-equal to zero." );
 				}
 				return DataHelper.subStream( getCharacterStream(), start-1, length );
 			}
 		}
 		if ( "getSubString".equals( methodName ) && argCount == 2 ) {
 			long start = (Long) args[0];
 			if ( start < 1 ) {
 				throw new SQLException( "Start position 1-based; must be 1 or more." );
 			}
 			if ( start > getLength() ) {
 				throw new SQLException( "Start position [" + start + "] cannot exceed overall CLOB length [" + getLength() + "]" );
 			}
 			int length = (Integer) args[1];
 			if ( length < 0 ) {
 				throw new SQLException( "Length must be great-than-or-equal to zero." );
 			}
 			return getSubString( start-1, length );
 		}
 		if ( "free".equals( methodName ) && argCount == 0 ) {
 			characterStream.release();
 			return null;
 		}
 		if ( "toString".equals( methodName ) && argCount == 0 ) {
 			return this.toString();
 		}
 		if ( "equals".equals( methodName ) && argCount == 1 ) {
 			return Boolean.valueOf( proxy == args[0] );
 		}
 		if ( "hashCode".equals( methodName ) && argCount == 0 ) {
 			return this.hashCode();
 		}
 
 		throw new UnsupportedOperationException( "Clob may not be manipulated from creating session" );
 	}
 
 	protected void resetIfNeeded() throws SQLException {
 		try {
 			if ( needsReset ) {
 				characterStream.asReader().reset();
 			}
 		}
 		catch ( IOException ioe ) {
 			throw new SQLException( "could not reset reader", ioe );
 		}
 		needsReset = true;
 	}
 
 	/**
 	 * Generates a {@link Clob} proxy using the string data.
 	 *
 	 * @param string The data to be wrapped as a {@link Clob}.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Clob generateProxy(String string) {
 		return ( Clob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ClobProxy( string )
 		);
 	}
 
 	/**
 	 * Generates a {@link Clob} proxy using a character reader of given length.
 	 *
 	 * @param reader The character reader
 	 * @param length The length of the character reader
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Clob generateProxy(Reader reader, long length) {
 		return ( Clob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ClobProxy( reader, length )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	protected static ClassLoader getProxyClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = ClobImplementer.class.getClassLoader();
 		}
 		return cl;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java
index 9afdd80207..4f9227891d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/NClobProxy.java
@@ -1,97 +1,97 @@
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
 package org.hibernate.engine.jdbc;
 
 import java.io.Reader;
 import java.lang.reflect.Proxy;
 import java.sql.Clob;
 import java.sql.NClob;
 
 import org.hibernate.internal.util.ClassLoaderHelper;
 
 /**
  * Manages aspects of proxying java.sql.NClobs for non-contextual creation, including proxy creation and
  * handling proxy invocations.  We use proxies here solely to avoid JDBC version incompatibilities.
  * <p/>
  * Generated proxies are typed as {@link java.sql.Clob} (java.sql.NClob extends {@link java.sql.Clob})
  * and in JDK 1.6+ environments, they are also typed to java.sql.NClob
  *
  * @author Steve Ebersole
  */
 public class NClobProxy extends ClobProxy {
 	public static final Class[] PROXY_INTERFACES = new Class[] { NClob.class, NClobImplementer.class };
 
 	protected NClobProxy(String string) {
 		super( string );
 	}
 
 	protected NClobProxy(Reader reader, long length) {
 		super( reader, length );
 	}
 
 	/**
 	 * Generates a {@link java.sql.Clob} proxy using the string data.
 	 *
 	 * @param string The data to be wrapped as a {@link java.sql.Clob}.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static NClob generateProxy(String string) {
 		return ( NClob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ClobProxy( string )
 		);
 	}
 
 	/**
 	 * Generates a {@link Clob} proxy using a character reader of given length.
 	 *
 	 * @param reader The character reader
 	 * @param length The length of the character reader
 	 *
 	 * @return The generated proxy.
 	 */
 	public static NClob generateProxy(Reader reader, long length) {
 		return ( NClob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ClobProxy( reader, length )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	protected static ClassLoader getProxyClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = NClobImplementer.class.getClassLoader();
 		}
 		return cl;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
index 8609ba53f8..4b0b21ea3f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/ResultSetWrapperProxy.java
@@ -1,189 +1,189 @@
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
 package org.hibernate.engine.jdbc;
 
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ClassLoaderHelper;
 
 /**
  * A proxy for a ResultSet delegate, responsible for locally caching the columnName-to-columnIndex resolution that
  * has been found to be inefficient in a few vendor's drivers (i.e., Oracle and Postgres).
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class ResultSetWrapperProxy implements InvocationHandler {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ResultSetWrapperProxy.class.getName());
 	private static final Class[] PROXY_INTERFACES = new Class[] { ResultSet.class };
 	private static final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
 
 	private final ResultSet rs;
 	private final ColumnNameCache columnNameCache;
 
 	private ResultSetWrapperProxy(ResultSet rs, ColumnNameCache columnNameCache) {
 		this.rs = rs;
 		this.columnNameCache = columnNameCache;
 	}
 
 	/**
 	 * Generates a proxy wrapping the ResultSet.
 	 *
 	 * @param resultSet The resultSet to wrap.
 	 * @param columnNameCache The cache storing data for converting column names to column indexes.
 	 * @return The generated proxy.
 	 */
 	public static ResultSet generateProxy(ResultSet resultSet, ColumnNameCache columnNameCache) {
 		return ( ResultSet ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new ResultSetWrapperProxy( resultSet, columnNameCache )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	public static ClassLoader getProxyClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = ResultSet.class.getClassLoader();
 		}
 		return cl;
 	}
 
 	@Override
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		if ( "findColumn".equals( method.getName() ) ) {
 			return Integer.valueOf( findColumn( ( String ) args[0] ) );
 		}
 
 		if ( isFirstArgColumnLabel( method, args ) ) {
 			try {
 				int columnIndex = findColumn( ( String ) args[0] );
 				return invokeMethod(
 						locateCorrespondingColumnIndexMethod( method ), buildColumnIndexMethodArgs( args, columnIndex )
 				);
 			}
 			catch ( SQLException ex ) {
 				StringBuilder buf = new StringBuilder()
 						.append( "Exception getting column index for column: [" )
 						.append( args[0] )
 						.append( "].\nReverting to using: [" )
 						.append( args[0] )
 						.append( "] as first argument for method: [" )
 						.append( method )
 						.append( "]" );
 				sqlExceptionHelper.logExceptions( ex, buf.toString() );
 			}
 			catch ( NoSuchMethodException ex ) {
 				LOG.unableToSwitchToMethodUsingColumnIndex( method );
 			}
 		}
 		return invokeMethod( method, args );
 	}
 
 	/**
 	 * Locate the column index corresponding to the given column name via the cache.
 	 *
 	 * @param columnName The column name to resolve into an index.
 	 * @return The column index corresponding to the given column name.
 	 * @throws SQLException if the ResultSet object does not contain columnName or a database access error occurs
 	 */
 	private int findColumn(String columnName) throws SQLException {
 		return columnNameCache.getIndexForColumnName( columnName, rs );
 	}
 
 	private boolean isFirstArgColumnLabel(Method method, Object args[]) {
 		// method name should start with either get or update
 		if ( ! ( method.getName().startsWith( "get" ) || method.getName().startsWith( "update" ) ) ) {
 			return false;
 		}
 
 		// method should have arguments, and have same number as incoming arguments
 		if ( ! ( method.getParameterTypes().length > 0 && args.length == method.getParameterTypes().length ) ) {
 			return false;
 		}
 
 		// The first argument should be a String (the column name)
 		//noinspection RedundantIfStatement
 		if ( ! ( String.class.isInstance( args[0] ) && method.getParameterTypes()[0].equals( String.class ) ) ) {
 			return false;
 		}
 
 		return true;
 	}
 
 	/**
 	 * For a given {@link ResultSet} method passed a column name, locate the corresponding method passed the same
 	 * parameters but the column index.
 	 *
 	 * @param columnNameMethod The method passed the column name
 	 * @return The corresponding method passed the column index.
 	 * @throws NoSuchMethodException Should never happen, but...
 	 */
 	private Method locateCorrespondingColumnIndexMethod(Method columnNameMethod) throws NoSuchMethodException {
 		Class actualParameterTypes[] = new Class[columnNameMethod.getParameterTypes().length];
 		actualParameterTypes[0] = int.class;
 		System.arraycopy(
 				columnNameMethod.getParameterTypes(),
 				1,
 				actualParameterTypes,
 				1,
 				columnNameMethod.getParameterTypes().length - 1
 		);
 		return columnNameMethod.getDeclaringClass().getMethod( columnNameMethod.getName(), actualParameterTypes );
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	private Object[] buildColumnIndexMethodArgs(Object[] incomingArgs, int columnIndex) {
 		Object actualArgs[] = new Object[incomingArgs.length];
 		actualArgs[0] = Integer.valueOf( columnIndex );
 		System.arraycopy( incomingArgs, 1, actualArgs, 1, incomingArgs.length - 1 );
 		return actualArgs;
 	}
 
 	private Object invokeMethod(Method method, Object args[]) throws Throwable {
 		try {
 			return method.invoke( rs, args );
 		}
 		catch ( InvocationTargetException e ) {
 			throw e.getTargetException();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java
index 160f49f76e..0d8209ef72 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableBlobProxy.java
@@ -1,111 +1,111 @@
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
 package org.hibernate.engine.jdbc;
 
 import java.io.Serializable;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.Blob;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.ClassLoaderHelper;
 
 /**
  * Manages aspects of proxying {@link Blob Blobs} to add serializability.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class SerializableBlobProxy implements InvocationHandler, Serializable {
 	private static final Class[] PROXY_INTERFACES = new Class[] { Blob.class, WrappedBlob.class, Serializable.class };
 
 	private transient final Blob blob;
 
 	/**
 	 * Builds a serializable {@link Blob} wrapper around the given {@link Blob}.
 	 *
 	 * @param blob The {@link Blob} to be wrapped.
 	 * @see
 	 */
 	private SerializableBlobProxy(Blob blob) {
 		this.blob = blob;
 	}
 
 	public Blob getWrappedBlob() {
 		if ( blob == null ) {
 			throw new IllegalStateException( "Blobs may not be accessed after serialization" );
 		}
 		else {
 			return blob;
 		}
 	}
 
 	@Override
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		if ( "getWrappedBlob".equals( method.getName() ) ) {
 			return getWrappedBlob();
 		}
 		try {
 			return method.invoke( getWrappedBlob(), args );
 		}
 		catch ( AbstractMethodError e ) {
 			throw new HibernateException( "The JDBC driver does not implement the method: " + method, e );
 		}
 		catch ( InvocationTargetException e ) {
 			throw e.getTargetException();
 		}
 	}
 
 	/**
 	 * Generates a SerializableBlob proxy wrapping the provided Blob object.
 	 *
 	 * @param blob The Blob to wrap.
 	 *
 	 * @return The generated proxy.
 	 */
 	public static Blob generateProxy(Blob blob) {
 		return ( Blob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new SerializableBlobProxy( blob )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	public static ClassLoader getProxyClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = WrappedBlob.class.getClassLoader();
 		}
 		return cl;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java
index ef991f4871..8b499e5c25 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/SerializableClobProxy.java
@@ -1,110 +1,110 @@
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
 package org.hibernate.engine.jdbc;
 
 import java.io.Serializable;
 import java.lang.reflect.InvocationHandler;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.lang.reflect.Proxy;
 import java.sql.Clob;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.util.ClassLoaderHelper;
 
 /**
  * Manages aspects of proxying {@link Clob Clobs} to add serializability.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class SerializableClobProxy implements InvocationHandler, Serializable {
 	private static final Class[] PROXY_INTERFACES = new Class[] { Clob.class, WrappedClob.class, Serializable.class };
 
 	private transient final Clob clob;
 
 	/**
 	 * Builds a serializable {@link java.sql.Clob} wrapper around the given {@link java.sql.Clob}.
 	 *
 	 * @param clob The {@link java.sql.Clob} to be wrapped.
 	 * @see #generateProxy(java.sql.Clob)
 	 */
 	protected SerializableClobProxy(Clob clob) {
 		this.clob = clob;
 	}
 
 	public Clob getWrappedClob() {
 		if ( clob == null ) {
 			throw new IllegalStateException( "Clobs may not be accessed after serialization" );
 		}
 		else {
 			return clob;
 		}
 	}
 
 	@Override
 	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
 		if ( "getWrappedClob".equals( method.getName() ) ) {
 			return getWrappedClob();
 		}
 		try {
 			return method.invoke( getWrappedClob(), args );
 		}
 		catch ( AbstractMethodError e ) {
 			throw new HibernateException( "The JDBC driver does not implement the method: " + method, e );
 		}
 		catch ( InvocationTargetException e ) {
 			throw e.getTargetException();
 		}
 	}
 
 	/**
 	 * Generates a SerializableClobProxy proxy wrapping the provided Clob object.
 	 *
 	 * @param clob The Clob to wrap.
 	 * @return The generated proxy.
 	 */
 	public static Clob generateProxy(Clob clob) {
 		return ( Clob ) Proxy.newProxyInstance(
 				getProxyClassLoader(),
 				PROXY_INTERFACES,
 				new SerializableClobProxy( clob )
 		);
 	}
 
 	/**
 	 * Determines the appropriate class loader to which the generated proxy
 	 * should be scoped.
 	 *
 	 * @return The class loader appropriate for proxy construction.
 	 */
 	public static ClassLoader getProxyClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = WrappedClob.class.getClassLoader();
 		}
 		return cl;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
index 6fd2f82c20..df83684145 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/connections/internal/DriverManagerConnectionProviderImpl.java
@@ -1,248 +1,263 @@
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
 package org.hibernate.engine.jdbc.connections.internal;
 
 import java.sql.Connection;
+import java.sql.Driver;
 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Map;
 import java.util.Properties;
 import java.util.concurrent.atomic.AtomicInteger;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.HibernateException;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.UnknownUnwrapTypeException;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
-import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.Stoppable;
+import org.jboss.logging.Logger;
 
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
 public class DriverManagerConnectionProviderImpl
 		implements ConnectionProvider, Configurable, Stoppable, ServiceRegistryAwareService {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DriverManagerConnectionProviderImpl.class.getName() );
 
 	private String url;
 	private Properties connectionProps;
 	private Integer isolation;
 	private int poolSize;
 	private boolean autocommit;
 
 	private final ArrayList<Connection> pool = new ArrayList<Connection>();
 	private final AtomicInteger checkedOut = new AtomicInteger();
 
 	private boolean stopped;
 
 	private transient ServiceRegistryImplementor serviceRegistry;
+	
+	private Driver driver;
 
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
 
 		String driverClassName = (String) configurationValues.get( AvailableSettings.DRIVER );
 		if ( driverClassName == null ) {
 			LOG.jdbcDriverNotSpecified( AvailableSettings.DRIVER );
 		}
 		else if ( serviceRegistry != null ) {
 			try {
-				serviceRegistry.getService( ClassLoaderService.class ).classForName( driverClassName );
+				driver = (Driver) serviceRegistry.getService(
+						ClassLoaderService.class ).classForName( driverClassName )
+						.newInstance();
 			}
-			catch ( ClassLoadingException e ) {
+			catch ( Exception e ) {
 				throw new ClassLoadingException(
-						"Specified JDBC Driver " + driverClassName + " class not found",
-						e
+						"Specified JDBC Driver " + driverClassName
+						+ " could not be loaded", e
 				);
 			}
 		}
 		// guard dog, mostly for making test pass
 		else {
 			try {
 				// trying via forName() first to be as close to DriverManager's semantics
-				Class.forName( driverClassName );
+				driver = (Driver) Class.forName( driverClassName ).newInstance();
 			}
-			catch ( ClassNotFoundException cnfe ) {
+			catch ( Exception e1 ) {
 				try{
-					ReflectHelper.classForName( driverClassName );
+					driver = (Driver) ReflectHelper.classForName( driverClassName ).newInstance();
 				}
-				catch ( ClassNotFoundException e ) {
-					throw new HibernateException( "Specified JDBC Driver " + driverClassName + " class not found", e );
+				catch ( Exception e2 ) {
+					throw new HibernateException( "Specified JDBC Driver " + driverClassName + " could not be loaded", e2 );
 				}
 			}
 		}
 
 		poolSize = ConfigurationHelper.getInt( AvailableSettings.POOL_SIZE, configurationValues, 20 ); // default pool size 20
 		LOG.hibernateConnectionPoolSize( poolSize );
 
 		autocommit = ConfigurationHelper.getBoolean( AvailableSettings.AUTOCOMMIT, configurationValues );
 		LOG.autoCommitMode( autocommit );
 
 		isolation = ConfigurationHelper.getInteger( AvailableSettings.ISOLATION, configurationValues );
 		if ( isolation != null )
 			LOG.jdbcIsolationLevel( Environment.isolationLevelToString( isolation.intValue() ) );
 
 		url = (String) configurationValues.get( AvailableSettings.URL );
 		if ( url == null ) {
 			String msg = LOG.jdbcUrlNotSpecified( AvailableSettings.URL );
 			LOG.error( msg );
 			throw new HibernateException( msg );
 		}
 
 		connectionProps = ConnectionProviderInitiator.getConnectionProperties( configurationValues );
 
 		LOG.usingDriver( driverClassName, url );
 		// if debug level is enabled, then log the password, otherwise mask it
 		if ( LOG.isDebugEnabled() )
 			LOG.connectionProperties( connectionProps );
 		else
 			LOG.connectionProperties( ConfigurationHelper.maskOut( connectionProps, "password" ) );
 	}
 
 	public void stop() {
 		LOG.cleaningUpConnectionPool( url );
 
 		for ( Connection connection : pool ) {
 			try {
 				connection.close();
 			}
 			catch (SQLException sqle) {
 				LOG.unableToClosePooledConnection( sqle );
 			}
 		}
 		pool.clear();
 		stopped = true;
 	}
 
 	public Connection getConnection() throws SQLException {
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) LOG.tracev( "Total checked-out connections: {0}", checkedOut.intValue() );
 
 		// essentially, if we have available connections in the pool, use one...
 		synchronized (pool) {
 			if ( !pool.isEmpty() ) {
 				int last = pool.size() - 1;
 				if ( traceEnabled ) LOG.tracev( "Using pooled JDBC connection, pool size: {0}", last );
 				Connection pooled = pool.remove( last );
 				if ( isolation != null ) {
 					pooled.setTransactionIsolation( isolation.intValue() );
 				}
 				if ( pooled.getAutoCommit() != autocommit ) {
 					pooled.setAutoCommit( autocommit );
 				}
 				checkedOut.incrementAndGet();
 				return pooled;
 			}
 		}
 
 		// otherwise we open a new connection...
-
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) LOG.debug( "Opening new JDBC connection" );
-
-		Connection conn = DriverManager.getConnection( url, connectionProps );
+		
+		Connection conn;
+		if ( driver != null ) {
+			// If a Driver is available, completely circumvent
+			// DriverManager#getConnection.  It attempts to double check
+			// ClassLoaders before using a Driver.  This does not work well in
+			// OSGi environments without wonky workarounds.
+			conn = driver.connect( url, connectionProps );
+		}
+		else {
+			// If no Driver, fall back on the original method.
+			conn = DriverManager.getConnection( url, connectionProps );
+		}
+		
 		if ( isolation != null ) {
 			conn.setTransactionIsolation( isolation.intValue() );
 		}
 		if ( conn.getAutoCommit() != autocommit ) {
 			conn.setAutoCommit(autocommit);
 		}
 
 		if ( debugEnabled ) {
 			LOG.debugf( "Created connection to: %s, Isolation Level: %s", url, conn.getTransactionIsolation() );
 		}
 
 		checkedOut.incrementAndGet();
 		return conn;
 	}
 
 	public void closeConnection(Connection conn) throws SQLException {
 		checkedOut.decrementAndGet();
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		// add to the pool if the max size is not yet reached.
 		synchronized ( pool ) {
 			int currentSize = pool.size();
 			if ( currentSize < poolSize ) {
 				if ( traceEnabled ) LOG.tracev( "Returning connection to pool, pool size: {0}", ( currentSize + 1 ) );
 				pool.add( conn );
 				return;
 			}
 		}
 
 		LOG.debug( "Closing JDBC connection" );
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
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/ClassLoaderHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/ClassLoaderHelper.java
index ea6b7fdb15..78e2f85826 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/ClassLoaderHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ClassLoaderHelper.java
@@ -1,38 +1,38 @@
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
 package org.hibernate.internal.util;
 
 /**
  * This exists purely to allow custom ClassLoaders to be injected and used
- * prior to ServiceRegistry and ClassLoadingService existance.  This should be
+ * prior to ServiceRegistry and ClassLoadingService existence.  This should be
  * replaced in Hibernate 5.
  * 
  * @author Brett Meyer
  */
 public class ClassLoaderHelper {
 	
 	public static ClassLoader overridenClassLoader = null;
 	
-	public static ClassLoader getClassLoader() {
+	public static ClassLoader getContextClassLoader() {
 		return overridenClassLoader != null ?
 			overridenClassLoader : Thread.currentThread().getContextClassLoader();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java
index dacda973c2..3cd79bdb99 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ConfigHelper.java
@@ -1,206 +1,206 @@
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
 package org.hibernate.internal.util;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.net.MalformedURLException;
 import java.net.URL;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * A simple class to centralize logic needed to locate config files on the system.
  *
  * @todo : Update usages to use {@link org.hibernate.boot.registry.classloading.spi.ClassLoaderService}
  *
  * @author Steve Ebersole
  */
 public final class ConfigHelper {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ConfigHelper.class.getName());
 
 	/** Try to locate a local URL representing the incoming path.  The first attempt
 	 * assumes that the incoming path is an actual URL string (file://, etc).  If this
 	 * does not work, then the next attempts try to locate this UURL as a java system
 	 * resource.
 	 *
 	 * @param path The path representing the config location.
 	 * @return An appropriate URL or null.
 	 */
 	public static URL locateConfig(final String path) {
 		try {
 			return new URL(path);
 		}
 		catch(MalformedURLException e) {
 			return findAsResource(path);
 		}
 	}
 
 	/**
 	 * Try to locate a local URL representing the incoming path.
 	 * This method <b>only</b> attempts to locate this URL as a
 	 * java system resource.
 	 *
 	 * @param path The path representing the config location.
 	 * @return An appropriate URL or null.
 	 */
 	public static URL findAsResource(final String path) {
 		URL url = null;
 
 		// First, try to locate this resource through the current
 		// context classloader.
-		ClassLoader contextClassLoader = ClassLoaderHelper.getClassLoader();
+		ClassLoader contextClassLoader = ClassLoaderHelper.getContextClassLoader();
 		if (contextClassLoader!=null) {
 			url = contextClassLoader.getResource(path);
 		}
 		if (url != null)
 			return url;
 
 		// Next, try to locate this resource through this class's classloader
 		url = ConfigHelper.class.getClassLoader().getResource(path);
 		if (url != null)
 			return url;
 
 		// Next, try to locate this resource through the system classloader
 		url = ClassLoader.getSystemClassLoader().getResource(path);
 
 		// Anywhere else we should look?
 		return url;
 	}
 
 	/** Open an InputStream to the URL represented by the incoming path.  First makes a call
 	 * to {@link #locateConfig(java.lang.String)} in order to find an appropriate URL.
 	 * {@link java.net.URL#openStream()} is then called to obtain the stream.
 	 *
 	 * @param path The path representing the config location.
 	 * @return An input stream to the requested config resource.
 	 * @throws HibernateException Unable to open stream to that resource.
 	 */
 	public static InputStream getConfigStream(final String path) throws HibernateException {
 		final URL url = ConfigHelper.locateConfig(path);
 
 		if (url == null) {
             String msg = LOG.unableToLocateConfigFile(path);
             LOG.error(msg);
 			throw new HibernateException(msg);
 		}
 
 		try {
 			return url.openStream();
         }
 		catch(IOException e) {
 	        throw new HibernateException("Unable to open config file: " + path, e);
         }
 	}
 
 	/** Open an Reader to the URL represented by the incoming path.  First makes a call
 	 * to {@link #locateConfig(java.lang.String)} in order to find an appropriate URL.
 	 * {@link java.net.URL#openStream()} is then called to obtain a stream, which is then
 	 * wrapped in a Reader.
 	 *
 	 * @param path The path representing the config location.
 	 * @return An input stream to the requested config resource.
 	 * @throws HibernateException Unable to open reader to that resource.
 	 */
 	public static Reader getConfigStreamReader(final String path) throws HibernateException {
 		return new InputStreamReader( getConfigStream(path) );
 	}
 
 	/** Loads a properties instance based on the data at the incoming config location.
 	 *
 	 * @param path The path representing the config location.
 	 * @return The loaded properties instance.
 	 * @throws HibernateException Unable to load properties from that resource.
 	 */
 	public static Properties getConfigProperties(String path) throws HibernateException {
 		try {
 			Properties properties = new Properties();
 			properties.load( getConfigStream(path) );
 			return properties;
 		}
 		catch(IOException e) {
 			throw new HibernateException("Unable to load properties from specified config file: " + path, e);
 		}
 	}
 
 	private ConfigHelper() {}
 
 	public static InputStream getResourceAsStream(String resource) {
 		String stripped = resource.startsWith("/") ?
 				resource.substring(1) : resource;
 
 		InputStream stream = null;
-		ClassLoader classLoader = ClassLoaderHelper.getClassLoader();
+		ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
 		if (classLoader!=null) {
 			stream = classLoader.getResourceAsStream( stripped );
 		}
 		if ( stream == null ) {
 			stream = Environment.class.getResourceAsStream( resource );
 		}
 		if ( stream == null ) {
 			stream = Environment.class.getClassLoader().getResourceAsStream( stripped );
 		}
 		if ( stream == null ) {
 			throw new HibernateException( resource + " not found" );
 		}
 		return stream;
 	}
 
 
 	public static InputStream getUserResourceAsStream(String resource) {
 		boolean hasLeadingSlash = resource.startsWith( "/" );
 		String stripped = hasLeadingSlash ? resource.substring(1) : resource;
 
 		InputStream stream = null;
 
-		ClassLoader classLoader = ClassLoaderHelper.getClassLoader();
+		ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
 		if ( classLoader != null ) {
 			stream = classLoader.getResourceAsStream( resource );
 			if ( stream == null && hasLeadingSlash ) {
 				stream = classLoader.getResourceAsStream( stripped );
 			}
 		}
 
 		if ( stream == null ) {
 			stream = Environment.class.getClassLoader().getResourceAsStream( resource );
 		}
 		if ( stream == null && hasLeadingSlash ) {
 			stream = Environment.class.getClassLoader().getResourceAsStream( stripped );
 		}
 
 		if ( stream == null ) {
 			throw new HibernateException( resource + " not found" );
 		}
 
 		return stream;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java
index 5d0ab0f774..ea13163a22 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java
@@ -1,384 +1,384 @@
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
 package org.hibernate.internal.util;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.lang.reflect.Modifier;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.property.BasicPropertyAccessor;
 import org.hibernate.property.DirectPropertyAccessor;
 import org.hibernate.property.Getter;
 import org.hibernate.property.PropertyAccessor;
 import org.hibernate.type.PrimitiveType;
 import org.hibernate.type.Type;
 
 /**
  * Utility class for various reflection operations.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class ReflectHelper {
 
 	//TODO: this dependency is kinda Bad
 	private static final PropertyAccessor BASIC_PROPERTY_ACCESSOR = new BasicPropertyAccessor();
 	private static final PropertyAccessor DIRECT_PROPERTY_ACCESSOR = new DirectPropertyAccessor();
 
 	public static final Class[] NO_PARAM_SIGNATURE = new Class[0];
 	public static final Object[] NO_PARAMS = new Object[0];
 
 	public static final Class[] SINGLE_OBJECT_PARAM_SIGNATURE = new Class[] { Object.class };
 
 	private static final Method OBJECT_EQUALS;
 	private static final Method OBJECT_HASHCODE;
 
 	static {
 		Method eq;
 		Method hash;
 		try {
 			eq = extractEqualsMethod( Object.class );
 			hash = extractHashCodeMethod( Object.class );
 		}
 		catch ( Exception e ) {
 			throw new AssertionFailure( "Could not find Object.equals() or Object.hashCode()", e );
 		}
 		OBJECT_EQUALS = eq;
 		OBJECT_HASHCODE = hash;
 	}
 
 	/**
 	 * Disallow instantiation of ReflectHelper.
 	 */
 	private ReflectHelper() {
 	}
 
 	/**
 	 * Encapsulation of getting hold of a class's {@link Object#equals equals}  method.
 	 *
 	 * @param clazz The class from which to extract the equals method.
 	 * @return The equals method reference
 	 * @throws NoSuchMethodException Should indicate an attempt to extract equals method from interface.
 	 */
 	public static Method extractEqualsMethod(Class clazz) throws NoSuchMethodException {
 		return clazz.getMethod( "equals", SINGLE_OBJECT_PARAM_SIGNATURE );
 	}
 
 	/**
 	 * Encapsulation of getting hold of a class's {@link Object#hashCode hashCode} method.
 	 *
 	 * @param clazz The class from which to extract the hashCode method.
 	 * @return The hashCode method reference
 	 * @throws NoSuchMethodException Should indicate an attempt to extract hashCode method from interface.
 	 */
 	public static Method extractHashCodeMethod(Class clazz) throws NoSuchMethodException {
 		return clazz.getMethod( "hashCode", NO_PARAM_SIGNATURE );
 	}
 
 	/**
 	 * Determine if the given class defines an {@link Object#equals} override.
 	 *
 	 * @param clazz The class to check
 	 * @return True if clazz defines an equals override.
 	 */
 	public static boolean overridesEquals(Class clazz) {
 		Method equals;
 		try {
 			equals = extractEqualsMethod( clazz );
 		}
 		catch ( NoSuchMethodException nsme ) {
 			return false; //its an interface so we can't really tell anything...
 		}
 		return !OBJECT_EQUALS.equals( equals );
 	}
 
 	/**
 	 * Determine if the given class defines a {@link Object#hashCode} override.
 	 *
 	 * @param clazz The class to check
 	 * @return True if clazz defines an hashCode override.
 	 */
 	public static boolean overridesHashCode(Class clazz) {
 		Method hashCode;
 		try {
 			hashCode = extractHashCodeMethod( clazz );
 		}
 		catch ( NoSuchMethodException nsme ) {
 			return false; //its an interface so we can't really tell anything...
 		}
 		return !OBJECT_HASHCODE.equals( hashCode );
 	}
 
 	/**
 	 * Determine if the given class implements the given interface.
 	 *
 	 * @param clazz The class to check
 	 * @param intf The interface to check it against.
 	 * @return True if the class does implement the interface, false otherwise.
 	 */
 	public static boolean implementsInterface(Class clazz, Class intf) {
 		assert intf.isInterface() : "Interface to check was not an interface";
 		return intf.isAssignableFrom( clazz );
 	}
 
 	/**
 	 * Perform resolution of a class name.
 	 * <p/>
 	 * Here we first check the context classloader, if one, before delegating to
 	 * {@link Class#forName(String, boolean, ClassLoader)} using the caller's classloader
 	 *
 	 * @param name The class name
 	 * @param caller The class from which this call originated (in order to access that class's loader).
 	 * @return The class reference.
 	 * @throws ClassNotFoundException From {@link Class#forName(String, boolean, ClassLoader)}.
 	 */
 	public static Class classForName(String name, Class caller) throws ClassNotFoundException {
 		try {
-			ClassLoader classLoader = ClassLoaderHelper.getClassLoader();
+			ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
 			if ( classLoader != null ) {
 				return classLoader.loadClass( name );
 			}
 		}
 		catch ( Throwable ignore ) {
 		}
 		return Class.forName( name, true, caller.getClassLoader() );
 	}
 
 	/**
 	 * Perform resolution of a class name.
 	 * <p/>
 	 * Same as {@link #classForName(String, Class)} except that here we delegate to
 	 * {@link Class#forName(String)} if the context classloader lookup is unsuccessful.
 	 *
 	 * @param name The class name
 	 * @return The class reference.
 	 * @throws ClassNotFoundException From {@link Class#forName(String)}.
 	 */
 	public static Class classForName(String name) throws ClassNotFoundException {
 		try {
-			ClassLoader classLoader = ClassLoaderHelper.getClassLoader();
+			ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
 			if ( classLoader != null ) {
 				return classLoader.loadClass(name);
 			}
 		}
 		catch ( Throwable ignore ) {
 		}
 		return Class.forName( name );
 	}
 
 	/**
 	 * Is this member publicly accessible.
 	 * <p/>
 	 * Short-hand for {@link #isPublic(Class, Member)} passing the member + {@link Member#getDeclaringClass()}
 	 *
 	 * @param member The member to check
 	 * @return True if the member is publicly accessible.
 	 */
 	public static boolean isPublic(Member member) {
 		return isPublic( member.getDeclaringClass(), member );
 	}
 
 	/**
 	 * Is this member publicly accessible.
 	 *
 	 * @param clazz The class which defines the member
 	 * @param member The memeber.
 	 * @return True if the member is publicly accessible, false otherwise.
 	 */
 	public static boolean isPublic(Class clazz, Member member) {
 		return Modifier.isPublic( member.getModifiers() ) && Modifier.isPublic( clazz.getModifiers() );
 	}
 
 	/**
 	 * Attempt to resolve the specified property type through reflection.
 	 *
 	 * @param className The name of the class owning the property.
 	 * @param name The name of the property.
 	 * @return The type of the property.
 	 * @throws MappingException Indicates we were unable to locate the property.
 	 */
 	public static Class reflectedPropertyClass(String className, String name) throws MappingException {
 		try {
 			Class clazz = ReflectHelper.classForName( className );
 			return getter( clazz, name ).getReturnType();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "class " + className + " not found while looking for property: " + name, cnfe );
 		}
 	}
 
 	/**
 	 * Attempt to resolve the specified property type through reflection.
 	 *
 	 * @param clazz The class owning the property.
 	 * @param name The name of the property.
 	 * @return The type of the property.
 	 * @throws MappingException Indicates we were unable to locate the property.
 	 */
 	public static Class reflectedPropertyClass(Class clazz, String name) throws MappingException {
 		return getter( clazz, name ).getReturnType();
 	}
 
 	private static Getter getter(Class clazz, String name) throws MappingException {
 		try {
 			return BASIC_PROPERTY_ACCESSOR.getGetter( clazz, name );
 		}
 		catch ( PropertyNotFoundException pnfe ) {
 			return DIRECT_PROPERTY_ACCESSOR.getGetter( clazz, name );
 		}
 	}
 
 	/**
 	 * Directly retrieve the {@link Getter} reference via the {@link BasicPropertyAccessor}.
 	 *
 	 * @param theClass The class owning the property
 	 * @param name The name of the property
 	 * @return The getter.
 	 * @throws MappingException Indicates we were unable to locate the property.
 	 */
 	public static Getter getGetter(Class theClass, String name) throws MappingException {
 		return BASIC_PROPERTY_ACCESSOR.getGetter( theClass, name );
 	}
 
 	/**
 	 * Resolve a constant to its actual value.
 	 *
 	 * @param name The name
 	 * @return The value
 	 */
 	public static Object getConstantValue(String name) {
 		Class clazz;
 		try {
 			clazz = classForName( StringHelper.qualifier( name ) );
 		}
 		catch ( Throwable t ) {
 			return null;
 		}
 		try {
 			return clazz.getField( StringHelper.unqualify( name ) ).get( null );
 		}
 		catch ( Throwable t ) {
 			return null;
 		}
 	}
 
 	/**
 	 * Retrieve the default (no arg) constructor from the given class.
 	 *
 	 * @param clazz The class for which to retrieve the default ctor.
 	 * @return The default constructor.
 	 * @throws PropertyNotFoundException Indicates there was not publicly accessible, no-arg constructor (todo : why PropertyNotFoundException???)
 	 */
 	public static Constructor getDefaultConstructor(Class clazz) throws PropertyNotFoundException {
 		if ( isAbstractClass( clazz ) ) {
 			return null;
 		}
 
 		try {
 			Constructor constructor = clazz.getDeclaredConstructor( NO_PARAM_SIGNATURE );
 			if ( !isPublic( clazz, constructor ) ) {
 				constructor.setAccessible( true );
 			}
 			return constructor;
 		}
 		catch ( NoSuchMethodException nme ) {
 			throw new PropertyNotFoundException(
 					"Object class [" + clazz.getName() + "] must declare a default (no-argument) constructor"
 			);
 		}
 	}
 
 	/**
 	 * Determine if the given class is declared abstract.
 	 *
 	 * @param clazz The class to check.
 	 * @return True if the class is abstract, false otherwise.
 	 */
 	public static boolean isAbstractClass(Class clazz) {
 		int modifier = clazz.getModifiers();
 		return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
 	}
 
 	/**
 	 * Determine is the given class is declared final.
 	 *
 	 * @param clazz The class to check.
 	 * @return True if the class is final, flase otherwise.
 	 */
 	public static boolean isFinalClass(Class clazz) {
 		return Modifier.isFinal( clazz.getModifiers() );
 	}
 
 	/**
 	 * Retrieve a constructor for the given class, with arguments matching the specified Hibernate mapping
 	 * {@link Type types}.
 	 *
 	 * @param clazz The class needing instantiation
 	 * @param types The types representing the required ctor param signature
 	 * @return The matching constructor.
 	 * @throws PropertyNotFoundException Indicates we could not locate an appropriate constructor (todo : again with PropertyNotFoundException???)
 	 */
 	public static Constructor getConstructor(Class clazz, Type[] types) throws PropertyNotFoundException {
 		final Constructor[] candidates = clazz.getConstructors();
 		for ( int i = 0; i < candidates.length; i++ ) {
 			final Constructor constructor = candidates[i];
 			final Class[] params = constructor.getParameterTypes();
 			if ( params.length == types.length ) {
 				boolean found = true;
 				for ( int j = 0; j < params.length; j++ ) {
 					final boolean ok = params[j].isAssignableFrom( types[j].getReturnedClass() ) || (
 							types[j] instanceof PrimitiveType &&
 									params[j] == ( ( PrimitiveType ) types[j] ).getPrimitiveClass()
 					);
 					if ( !ok ) {
 						found = false;
 						break;
 					}
 				}
 				if ( found ) {
 					if ( !isPublic( clazz, constructor ) ) {
 						constructor.setAccessible( true );
 					}
 					return constructor;
 				}
 			}
 		}
 		throw new PropertyNotFoundException( "no appropriate constructor in class: " + clazz.getName() );
 	}
 	
 	public static Method getMethod(Class clazz, Method method) {
 		try {
 			return clazz.getMethod( method.getName(), method.getParameterTypes() );
 		}
 		catch (Exception e) {
 			return null;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java
index 541a63e4e9..a8d89e9b08 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/SerializationHelper.java
@@ -1,377 +1,377 @@
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
 package org.hibernate.internal.util;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamClass;
 import java.io.OutputStream;
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.Hibernate;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.SerializationException;
 
 /**
  * <p>Assists with the serialization process and performs additional functionality based
  * on serialization.</p>
  * <p>
  * <ul>
  * <li>Deep clone using serialization
  * <li>Serialize managing finally and IOException
  * <li>Deserialize managing finally and IOException
  * </ul>
  *
  * <p>This class throws exceptions for invalid <code>null</code> inputs.
  * Each method documents its behaviour in more detail.</p>
  *
  * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
  * @author <a href="mailto:janekdb@yahoo.co.uk">Janek Bogucki</a>
  * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
  * @author Stephen Colebourne
  * @author Jeff Varszegi
  * @author Gary Gregory
  * @version $Id: SerializationHelper.java 9180 2006-01-30 23:51:27Z steveebersole $
  * @since 1.0
  */
 public final class SerializationHelper {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SerializationHelper.class.getName());
 
 	private SerializationHelper() {
 	}
 
 	// Clone
 	//-----------------------------------------------------------------------
 
 	/**
 	 * <p>Deep clone an <code>Object</code> using serialization.</p>
 	 *
 	 * <p>This is many times slower than writing clone methods by hand
 	 * on all objects in your object graph. However, for complex object
 	 * graphs, or for those that don't support deep cloning this can
 	 * be a simple alternative implementation. Of course all the objects
 	 * must be <code>Serializable</code>.</p>
 	 *
 	 * @param object the <code>Serializable</code> object to clone
 	 *
 	 * @return the cloned object
 	 *
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static Object clone(Serializable object) throws SerializationException {
 		LOG.trace( "Starting clone through serialization" );
 		if ( object == null ) {
 			return null;
 		}
 		return deserialize( serialize( object ), object.getClass().getClassLoader() );
 	}
 
 	// Serialize
 	//-----------------------------------------------------------------------
 
 	/**
 	 * <p>Serializes an <code>Object</code> to the specified stream.</p>
 	 *
 	 * <p>The stream will be closed once the object is written.
 	 * This avoids the need for a finally clause, and maybe also exception
 	 * handling, in the application code.</p>
 	 *
 	 * <p>The stream passed in is not buffered internally within this method.
 	 * This is the responsibility of your application if desired.</p>
 	 *
 	 * @param obj the object to serialize to bytes, may be null
 	 * @param outputStream the stream to write to, must not be null
 	 *
 	 * @throws IllegalArgumentException if <code>outputStream</code> is <code>null</code>
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static void serialize(Serializable obj, OutputStream outputStream) throws SerializationException {
 		if ( outputStream == null ) {
 			throw new IllegalArgumentException( "The OutputStream must not be null" );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			if ( Hibernate.isInitialized( obj ) ) {
 				LOG.tracev( "Starting serialization of object [{0}]", obj );
 			}
 			else {
 				LOG.trace( "Starting serialization of [uninitialized proxy]" );
 			}
 		}
 
 		ObjectOutputStream out = null;
 		try {
 			// stream closed in the finally
 			out = new ObjectOutputStream( outputStream );
 			out.writeObject( obj );
 
 		}
 		catch ( IOException ex ) {
 			throw new SerializationException( "could not serialize", ex );
 		}
 		finally {
 			try {
 				if ( out != null ) {
 					out.close();
 				}
 			}
 			catch ( IOException ignored ) {
 			}
 		}
 	}
 
 	/**
 	 * <p>Serializes an <code>Object</code> to a byte array for
 	 * storage/serialization.</p>
 	 *
 	 * @param obj the object to serialize to bytes
 	 *
 	 * @return a byte[] with the converted Serializable
 	 *
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static byte[] serialize(Serializable obj) throws SerializationException {
 		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream( 512 );
 		serialize( obj, byteArrayOutputStream );
 		return byteArrayOutputStream.toByteArray();
 	}
 
 	// Deserialize
 	//-----------------------------------------------------------------------
 
 	/**
 	 * Deserializes an object from the specified stream using the Thread Context
 	 * ClassLoader (TCCL).
 	 * <p/>
 	 * Delegates to {@link #doDeserialize}
 	 *
 	 * @param inputStream the serialized object input stream, must not be null
 	 *
 	 * @return the deserialized object
 	 *
 	 * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static Object deserialize(InputStream inputStream) throws SerializationException {
 		return doDeserialize( inputStream, defaultClassLoader(), hibernateClassLoader(), null );
 	}
 
 	/**
 	 * Returns the Thread Context ClassLoader (TCCL).
 	 *
 	 * @return The current TCCL
 	 */
 	public static ClassLoader defaultClassLoader() {
-		return ClassLoaderHelper.getClassLoader();
+		return ClassLoaderHelper.getContextClassLoader();
 	}
 
 	public static ClassLoader hibernateClassLoader() {
 		return SerializationHelper.class.getClassLoader();
 	}
 
 	/**
 	 * Deserializes an object from the specified stream using the Thread Context
 	 * ClassLoader (TCCL).  If there is no TCCL set, the classloader of the calling
 	 * class is used.
 	 * <p/>
 	 * The stream will be closed once the object is read. This avoids the need
 	 * for a finally clause, and maybe also exception handling, in the application
 	 * code.
 	 * <p/>
 	 * The stream passed in is not buffered internally within this method.  This is
 	 * the responsibility of the caller, if desired.
 	 *
 	 * @param inputStream the serialized object input stream, must not be null
 	 * @param loader The classloader to use
 	 *
 	 * @return the deserialized object
 	 *
 	 * @throws IllegalArgumentException if <code>inputStream</code> is <code>null</code>
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static Object deserialize(InputStream inputStream, ClassLoader loader) throws SerializationException {
 		return doDeserialize( inputStream, loader, defaultClassLoader(), hibernateClassLoader() );
 	}
 
 	public static Object doDeserialize(
 			InputStream inputStream,
 			ClassLoader loader,
 			ClassLoader fallbackLoader1,
 			ClassLoader fallbackLoader2) throws SerializationException {
 		if ( inputStream == null ) {
 			throw new IllegalArgumentException( "The InputStream must not be null" );
 		}
 
 		LOG.trace( "Starting deserialization of object" );
 
 		try {
 			CustomObjectInputStream in = new CustomObjectInputStream(
 					inputStream,
 					loader,
 					fallbackLoader1,
 					fallbackLoader2
 			);
 			try {
 				return in.readObject();
 			}
 			catch ( ClassNotFoundException e ) {
 				throw new SerializationException( "could not deserialize", e );
 			}
 			catch ( IOException e ) {
 				throw new SerializationException( "could not deserialize", e );
 			}
 			finally {
 				try {
 					in.close();
 				}
 				catch ( IOException ignore ) {
 					// ignore
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new SerializationException( "could not deserialize", e );
 		}
 	}
 
 	/**
 	 * Deserializes an object from an array of bytes using the Thread Context
 	 * ClassLoader (TCCL).  If there is no TCCL set, the classloader of the calling
 	 * class is used.
 	 * <p/>
 	 * Delegates to {@link #deserialize(byte[], ClassLoader)}
 	 *
 	 * @param objectData the serialized object, must not be null
 	 *
 	 * @return the deserialized object
 	 *
 	 * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static Object deserialize(byte[] objectData) throws SerializationException {
 		return doDeserialize( wrap( objectData ), defaultClassLoader(), hibernateClassLoader(), null );
 	}
 
 	private static InputStream wrap(byte[] objectData) {
 		if ( objectData == null ) {
 			throw new IllegalArgumentException( "The byte[] must not be null" );
 		}
 		return new ByteArrayInputStream( objectData );
 	}
 
 	/**
 	 * Deserializes an object from an array of bytes.
 	 * <p/>
 	 * Delegates to {@link #deserialize(java.io.InputStream, ClassLoader)} using a
 	 * {@link ByteArrayInputStream} to wrap the array.
 	 *
 	 * @param objectData the serialized object, must not be null
 	 * @param loader The classloader to use
 	 *
 	 * @return the deserialized object
 	 *
 	 * @throws IllegalArgumentException if <code>objectData</code> is <code>null</code>
 	 * @throws SerializationException (runtime) if the serialization fails
 	 */
 	public static Object deserialize(byte[] objectData, ClassLoader loader) throws SerializationException {
 		return doDeserialize( wrap( objectData ), loader, defaultClassLoader(), hibernateClassLoader() );
 	}
 
 
 	/**
 	 * By default, to resolve the classes being deserialized JDK serialization uses the
 	 * classes loader which loaded the class which initiated the deserialization call.  Here
 	 * that would be hibernate classes.  However, there are cases where that is not the correct
 	 * class loader to use; mainly here we are worried about deserializing user classes in
 	 * environments (app servers, etc) where Hibernate is on a parent classes loader.  To
 	 * facilitate for that we allow passing in the class loader we should use.
 	 */
 	private static final class CustomObjectInputStream extends ObjectInputStream {
 		private final ClassLoader loader1;
 		private final ClassLoader loader2;
 		private final ClassLoader loader3;
 
 		private CustomObjectInputStream(
 				InputStream in,
 				ClassLoader loader1,
 				ClassLoader loader2,
 				ClassLoader loader3) throws IOException {
 			super( in );
 			this.loader1 = loader1;
 			this.loader2 = loader2;
 			this.loader3 = loader3;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		@Override
 		protected Class resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
 			final String className = v.getName();
 			LOG.tracev( "Attempting to locate class [{0}]", className );
 
 			try {
 				return Class.forName( className, false, loader1 );
 			}
 			catch ( ClassNotFoundException e ) {
 				LOG.trace( "Unable to locate class using given classloader" );
 			}
 
 			if ( different( loader1, loader2 ) ) {
 				try {
 					return Class.forName( className, false, loader2 );
 				}
 				catch ( ClassNotFoundException e ) {
 					LOG.trace( "Unable to locate class using given classloader" );
 				}
 			}
 
 			if ( different( loader1, loader3 ) && different( loader2, loader3 ) ) {
 				try {
 					return Class.forName( className, false, loader3 );
 				}
 				catch ( ClassNotFoundException e ) {
 					LOG.trace( "Unable to locate class using given classloader" );
 				}
 			}
 
 			// By default delegate to normal JDK deserialization which will use the class loader
 			// of the class which is calling this deserialization.
 			return super.resolveClass( v );
 		}
 
 		private boolean different(ClassLoader one, ClassLoader other) {
             if (one == null) return other != null;
             return !one.equals(other);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java
index 3c35db7563..e44b9a30d6 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/xml/XMLHelper.java
@@ -1,112 +1,112 @@
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
 package org.hibernate.internal.util.xml;
 
 import org.dom4j.DocumentFactory;
 import org.dom4j.Element;
 import org.dom4j.io.DOMReader;
 import org.dom4j.io.OutputFormat;
 import org.dom4j.io.SAXReader;
 import org.dom4j.io.XMLWriter;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.ErrorHandler;
 
 /**
  * Small helper class that lazy loads DOM and SAX reader and keep them for fast use afterwards.
  */
 public final class XMLHelper {
 
 	public static final EntityResolver DEFAULT_DTD_RESOLVER = new DTDEntityResolver();
 
 	private DOMReader domReader;
 	private SAXReader saxReader;
 
 	/**
 	 * @param errorHandler the sax error handler
 	 * @param entityResolver an xml entity resolver
 	 *
 	 * @return Create and return a dom4j {@code SAXReader} which will append all validation errors
 	 *         to the passed error list
 	 */
 	public SAXReader createSAXReader(ErrorHandler errorHandler, EntityResolver entityResolver) {
 		SAXReader saxReader = resolveSAXReader();
 		saxReader.setEntityResolver( entityResolver );
 		saxReader.setErrorHandler( errorHandler );
 		return saxReader;
 	}
 
 	private SAXReader resolveSAXReader() {
 		if ( saxReader == null ) {
 			saxReader = new SAXReader();
 			saxReader.setMergeAdjacentText( true );
 			saxReader.setValidation( true );
 		}
 		return saxReader;
 	}
 
 	/**
 	 * @return create and return a dom4j DOMReader
 	 */
 	public DOMReader createDOMReader() {
 		if ( domReader == null ) {
 			domReader = new DOMReader();
 		}
 		return domReader;
 	}
 
 	public static Element generateDom4jElement(String elementName) {
 		return getDocumentFactory().createElement( elementName );
 	}
 
 	public static DocumentFactory getDocumentFactory() {
 
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		DocumentFactory factory;
 		try {
 			Thread.currentThread().setContextClassLoader( XMLHelper.class.getClassLoader() );
 			factory = DocumentFactory.getInstance();
 		}
 		finally {
 			Thread.currentThread().setContextClassLoader( cl );
 		}
 		return factory;
 	}
 
 	public static void dump(Element element) {
 		try {
 			// try to "pretty print" it
 			OutputFormat outFormat = OutputFormat.createPrettyPrint();
 			XMLWriter writer = new XMLWriter( System.out, outFormat );
 			writer.write( element );
 			writer.flush();
 			System.out.println( "" );
 		}
 		catch ( Throwable t ) {
 			// otherwise, just dump it
 			System.out.println( element.asXML() );
 		}
 
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/connection/DriverManagerRegistrationTest.java b/hibernate-core/src/test/java/org/hibernate/connection/DriverManagerRegistrationTest.java
index cb902559f3..9bb4798a72 100644
--- a/hibernate-core/src/test/java/org/hibernate/connection/DriverManagerRegistrationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/connection/DriverManagerRegistrationTest.java
@@ -1,194 +1,194 @@
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
 package org.hibernate.connection;
 
 import java.sql.Connection;
 import java.sql.Driver;
 import java.sql.DriverManager;
 import java.sql.DriverPropertyInfo;
 import java.sql.SQLException;
 import java.sql.SQLFeatureNotSupportedException;
 import java.util.Properties;
 import java.util.logging.Logger;
 
 import org.junit.AfterClass;
 import org.junit.Test;
 
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;
 
 /**
  * This test illustrates the problem with calling {@link ClassLoader#loadClass(String)} rather than
  * {@link Class#forName(String, boolean, ClassLoader)} in terms of invoking static ini
  *
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-7272" )
 public class DriverManagerRegistrationTest extends BaseUnitTestCase {
 
 	@Test
 	public void testDriverRegistrationUsingLoadClassFails() {
 		final String driverClassName = "org.hibernate.connection.DriverManagerRegistrationTest$TestDriver1";
 		final String url = "jdbc:hibernate:test";
 
 		try {
 			determineClassLoader().loadClass( driverClassName );
 		}
 		catch (ClassNotFoundException e) {
 			fail( "Error loading JDBC Driver class : " + e.getMessage() );
 		}
 
 		try {
 			DriverManager.getDriver( url );
 			fail( "This test should have failed to locate JDBC driver per HHH-7272" );
 		}
 		catch (SQLException expected) {
 			// actually this should fail due to the reasons discussed on HHH-7272
 		}
 	}
 
 	@Test
 	public void testDriverRegistrationUsingClassForNameSucceeds() {
 		final String driverClassName = "org.hibernate.connection.DriverManagerRegistrationTest$TestDriver2";
 		final String url = "jdbc:hibernate:test2";
 		try {
 			Class.forName( driverClassName, true, determineClassLoader() );
 		}
 		catch (ClassNotFoundException e) {
 			fail( "Error loading JDBC Driver class : " + e.getMessage() );
 		}
 
 		try {
 			assertNotNull( DriverManager.getDriver( url ) );
 		}
 		catch (SQLException expected) {
 			fail( "Unanticipated failure according to HHH-7272" );
 		}
 	}
 
 	private static ClassLoader determineClassLoader() {
-		ClassLoader cl = ClassLoaderHelper.getClassLoader();
+		ClassLoader cl = ClassLoaderHelper.getContextClassLoader();
 		if ( cl == null ) {
 			cl = DriverManagerRegistrationTest.class.getClassLoader();
 		}
 		return cl;
 	}
 
 	@AfterClass
 	public static void afterwards() {
 		try {
 			DriverManager.deregisterDriver( TestDriver1.INSTANCE );
 		}
 		catch (SQLException ignore) {
 		}
 		try {
 			DriverManager.deregisterDriver( TestDriver2.INSTANCE );
 		}
 		catch (SQLException ignore) {
 		}
 	}
 
 	public static abstract class AbstractTestJdbcDriver implements Driver {
 		public final String matchUrl;
 
 		protected AbstractTestJdbcDriver(String matchUrl) {
 			this.matchUrl = matchUrl;
 		}
 
 		@Override
 		public Connection connect(String url, Properties info) throws SQLException {
 			throw new RuntimeException( "Not real driver" );
 		}
 
 		@Override
 		public boolean acceptsURL(String url) throws SQLException {
 			return url.equals( matchUrl );
 		}
 
 		@Override
 		public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
 			return new DriverPropertyInfo[0];
 		}
 
 		@Override
 		public int getMajorVersion() {
 			return 1;
 		}
 
 		@Override
 		public int getMinorVersion() {
 			return 0;
 		}
 
 		@Override
 		public boolean jdbcCompliant() {
 			return false;
 		}
 
 		public Logger getParentLogger()
 				throws SQLFeatureNotSupportedException {
 			throw new SQLFeatureNotSupportedException();
 		}
 	}
 
 	public static class TestDriver1 extends AbstractTestJdbcDriver {
 		public static final TestDriver1 INSTANCE = new TestDriver1( "jdbc:hibernate:test" );
 
 		public TestDriver1(String matchUrl) {
 			super( matchUrl );
 		}
 
 		static {
 			try {
 				DriverManager.registerDriver( INSTANCE );
 			}
 			catch (SQLException e) {
 				System.err.println( "Unable to register driver : " + e.getMessage() );
 				e.printStackTrace();
 			}
 		}
 	}
 
 	public static class TestDriver2 extends AbstractTestJdbcDriver {
 		public static final TestDriver2 INSTANCE = new TestDriver2( "jdbc:hibernate:test2" );
 
 		public TestDriver2(String matchUrl) {
 			super( matchUrl );
 		}
 
 		static {
 			try {
 				DriverManager.registerDriver( INSTANCE );
 			}
 			catch (SQLException e) {
 				System.err.println( "Unable to register driver : " + e.getMessage() );
 				e.printStackTrace();
 			}
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
index 8faa3c9e2c..3a35625ec0 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
@@ -1,601 +1,601 @@
 package org.hibernate.cache.infinispan;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
 import org.hibernate.cache.infinispan.naturalid.NaturalIdRegionImpl;
 import org.hibernate.cache.infinispan.query.QueryResultsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.ClusteredTimestampsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.TimestampTypeOverrides;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.tm.HibernateTransactionManagerLookup;
 import org.hibernate.cache.infinispan.util.CacheCommandFactory;
 import org.hibernate.cache.infinispan.util.Caches;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.TimestampsRegion;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
 import org.hibernate.internal.util.ClassLoaderHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.infinispan.AdvancedCache;
 import org.infinispan.commands.module.ModuleCommandFactory;
 import org.infinispan.configuration.cache.CacheMode;
 import org.infinispan.configuration.cache.Configuration;
 import org.infinispan.configuration.cache.ConfigurationBuilder;
 import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
 import org.infinispan.configuration.parsing.ParserRegistry;
 import org.infinispan.factories.GlobalComponentRegistry;
 import org.infinispan.manager.DefaultCacheManager;
 import org.infinispan.manager.EmbeddedCacheManager;
 import org.infinispan.transaction.TransactionMode;
 import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
 import org.infinispan.util.FileLookupFactory;
 import org.infinispan.util.concurrent.IsolationLevel;
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
 /**
  * A {@link RegionFactory} for <a href="http://www.jboss.org/infinispan">Infinispan</a>-backed cache
  * regions.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class InfinispanRegionFactory implements RegionFactory {
 
    private static final Log log = LogFactory.getLog(InfinispanRegionFactory.class);
 
    private static final String PREFIX = "hibernate.cache.infinispan.";
 
    private static final String CONFIG_SUFFIX = ".cfg";
 
    private static final String STRATEGY_SUFFIX = ".eviction.strategy";
 
    private static final String WAKE_UP_INTERVAL_SUFFIX = ".eviction.wake_up_interval";
 
    private static final String MAX_ENTRIES_SUFFIX = ".eviction.max_entries";
 
    private static final String LIFESPAN_SUFFIX = ".expiration.lifespan";
 
    private static final String MAX_IDLE_SUFFIX = ".expiration.max_idle";
 
 //   private static final String STATISTICS_SUFFIX = ".statistics";
 
    /** 
     * Classpath or filesystem resource containing Infinispan configurations the factory should use.
     * 
     * @see #DEF_INFINISPAN_CONFIG_RESOURCE
     */
    public static final String INFINISPAN_CONFIG_RESOURCE_PROP = "hibernate.cache.infinispan.cfg";
 
    public static final String INFINISPAN_GLOBAL_STATISTICS_PROP = "hibernate.cache.infinispan.statistics";
 
    /**
     * Property that controls whether Infinispan should interact with the
     * transaction manager as a {@link javax.transaction.Synchronization} or as
     * an XA resource. If the property is set to true, it will be a
     * synchronization, otherwise an XA resource.
     *
     * @see #DEF_USE_SYNCHRONIZATION
     */
    public static final String INFINISPAN_USE_SYNCHRONIZATION_PROP = "hibernate.cache.infinispan.use_synchronization";
    
 	private static final String NATURAL_ID_KEY = "naturalid";
 
 	/**
 	 * Name of the configuration that should be used for natural id caches.
 	 *
 	 * @see #DEF_ENTITY_RESOURCE
 	 */
 	public static final String NATURAL_ID_CACHE_RESOURCE_PROP = PREFIX + NATURAL_ID_KEY + CONFIG_SUFFIX;
 
    private static final String ENTITY_KEY = "entity";
    
    /**
     * Name of the configuration that should be used for entity caches.
     * 
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String ENTITY_CACHE_RESOURCE_PROP = PREFIX + ENTITY_KEY + CONFIG_SUFFIX;
    
    private static final String COLLECTION_KEY = "collection";
    
    /**
     * Name of the configuration that should be used for collection caches.
     * No default value, as by default we try to use the same Infinispan cache
     * instance we use for entity caching.
     * 
     * @see #ENTITY_CACHE_RESOURCE_PROP
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String COLLECTION_CACHE_RESOURCE_PROP = PREFIX + COLLECTION_KEY + CONFIG_SUFFIX;
 
    private static final String TIMESTAMPS_KEY = "timestamps";
 
    /**
     * Name of the configuration that should be used for timestamp caches.
     * 
     * @see #DEF_TIMESTAMPS_RESOURCE
     */
    public static final String TIMESTAMPS_CACHE_RESOURCE_PROP = PREFIX + TIMESTAMPS_KEY + CONFIG_SUFFIX;
 
    private static final String QUERY_KEY = "query";
 
    /**
     * Name of the configuration that should be used for query caches.
     * 
     * @see #DEF_QUERY_RESOURCE
     */
    public static final String QUERY_CACHE_RESOURCE_PROP = PREFIX + QUERY_KEY + CONFIG_SUFFIX;
 
    /**
     * Default value for {@link #INFINISPAN_CONFIG_RESOURCE_PROP}. Specifies the "infinispan-configs.xml" file in this package.
     */
    public static final String DEF_INFINISPAN_CONFIG_RESOURCE = "org/hibernate/cache/infinispan/builder/infinispan-configs.xml";
 
    /**
     * Default value for {@link #ENTITY_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_ENTITY_RESOURCE = "entity";
 
    /**
     * Default value for {@link #TIMESTAMPS_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_TIMESTAMPS_RESOURCE = "timestamps";
 
    /**
     * Default value for {@link #QUERY_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_QUERY_RESOURCE = "local-query";
 
    /**
     * Default value for {@link #INFINISPAN_USE_SYNCHRONIZATION_PROP}.
     */
    public static final boolean DEF_USE_SYNCHRONIZATION = true;
 
    /**
     * Name of the pending puts cache.
     */
    public static final String PENDING_PUTS_CACHE_NAME = "pending-puts";
 
    private EmbeddedCacheManager manager;
 
    private final Map<String, TypeOverrides> typeOverrides = new HashMap<String, TypeOverrides>();
 
    private final Set<String> definedConfigurations = new HashSet<String>();
 
    private org.infinispan.transaction.lookup.TransactionManagerLookup transactionManagerlookup;
 
    private List<String> regionNames = new ArrayList<String>();
    
    /**
     * Create a new instance using the default configuration.
     */
    public InfinispanRegionFactory() {
    }
 
    /**
     * Create a new instance using conifguration properties in <code>props</code>.
     * 
     * @param props
     *           Environmental properties; currently unused.
     */
    public InfinispanRegionFactory(Properties props) {
    }
 
    /** {@inheritDoc} */
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building collection cache region [" + regionName + "]");
       AdvancedCache cache = getCache(regionName, COLLECTION_KEY, properties);
       CollectionRegionImpl region = new CollectionRegionImpl(
             cache, regionName, metadata, this);
       startRegion(region, regionName);
       return region;
    }
 
    /** {@inheritDoc} */
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building entity cache region [" + regionName + "]");
       AdvancedCache cache = getCache(regionName, ENTITY_KEY, properties);
       EntityRegionImpl region = new EntityRegionImpl(
             cache, regionName, metadata, this);
       startRegion(region, regionName);
       return region;
    }
 
 	@Override
 	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException {
 		if (log.isDebugEnabled()) {
 			log.debug("Building natural id cache region [" + regionName + "]");
 		}
 		AdvancedCache cache = getCache(regionName, NATURAL_ID_KEY, properties);
 		NaturalIdRegionImpl region = new NaturalIdRegionImpl(
 				cache, regionName, metadata, this);
 		startRegion(region, regionName);
 		return region;
 	}
 	
    /**
     * {@inheritDoc}
     */
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties)
             throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building query results cache region [" + regionName + "]");
       String cacheName = typeOverrides.get(QUERY_KEY).getCacheName();
       // If region name is not default one, lookup a cache for that region name
       if (!regionName.equals("org.hibernate.cache.internal.StandardQueryCache"))
          cacheName = regionName;
 
       AdvancedCache cache = getCache(cacheName, QUERY_KEY, properties);
       QueryResultsRegionImpl region = new QueryResultsRegionImpl(
             cache, regionName, this);
       startRegion(region, regionName);
       return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties)
             throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building timestamps cache region [" + regionName + "]");
       AdvancedCache cache = getCache(regionName, TIMESTAMPS_KEY, properties);
       TimestampsRegionImpl region = createTimestampsRegion(cache, regionName);
       startRegion(region, regionName);
       return region;
    }
 
    protected TimestampsRegionImpl createTimestampsRegion(
          AdvancedCache cache, String regionName) {
       if (Caches.isClustered(cache))
          return new ClusteredTimestampsRegionImpl(cache, regionName, this);
       else
          return new TimestampsRegionImpl(cache, regionName, this);
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean isMinimalPutsEnabledByDefault() {
       return true;
    }
 
    @Override
    public AccessType getDefaultAccessType() {
       return AccessType.TRANSACTIONAL;
    }
 
    /**
     * {@inheritDoc}
     */
    public long nextTimestamp() {
       return System.currentTimeMillis() / 100;
    }
    
    public void setCacheManager(EmbeddedCacheManager manager) {
       this.manager = manager;
    }
 
    public EmbeddedCacheManager getCacheManager() {
       return manager;
    }
 
    /**
     * {@inheritDoc}
     */
    public void start(Settings settings, Properties properties) throws CacheException {
       log.debug("Starting Infinispan region factory");
       try {
          transactionManagerlookup = createTransactionManagerLookup(settings, properties);
          manager = createCacheManager(properties);
          initGenericDataTypeOverrides();
          Enumeration keys = properties.propertyNames();
          while (keys.hasMoreElements()) {
             String key = (String) keys.nextElement();
             int prefixLoc;
             if ((prefixLoc = key.indexOf(PREFIX)) != -1) {
                dissectProperty(prefixLoc, key, properties);
             }
          }
          defineGenericDataTypeCacheConfigurations(properties);
          definePendingPutsCache();
       } catch (CacheException ce) {
          throw ce;
       } catch (Throwable t) {
           throw new CacheException("Unable to start region factory", t);
       }
    }
 
    private void definePendingPutsCache() {
       ConfigurationBuilder builder = new ConfigurationBuilder();
       // A local, lightweight cache for pending puts, which is
       // non-transactional and has aggressive expiration settings.
       // Locking is still required since the putFromLoad validator
       // code uses conditional operations (i.e. putIfAbsent).
       builder.clustering().cacheMode(CacheMode.LOCAL)
          .transaction().transactionMode(TransactionMode.NON_TRANSACTIONAL)
          .expiration().maxIdle(TimeUnit.SECONDS.toMillis(60))
          .storeAsBinary().enabled(false)
          .locking().isolationLevel(IsolationLevel.READ_COMMITTED)
          .jmxStatistics().disable();
 
       manager.defineConfiguration(PENDING_PUTS_CACHE_NAME, builder.build());
    }
 
    protected org.infinispan.transaction.lookup.TransactionManagerLookup createTransactionManagerLookup(
             Settings settings, Properties properties) {
       return new HibernateTransactionManagerLookup(settings, properties);
    }
 
    /**
     * {@inheritDoc}
     */
    public void stop() {
       log.debug("Stop region factory");
       stopCacheRegions();
       stopCacheManager();
    }
 
    protected void stopCacheRegions() {
       log.debug("Clear region references");
       getCacheCommandFactory(manager.getCache().getAdvancedCache())
             .clearRegions(regionNames);
       regionNames.clear();
    }
 
    protected void stopCacheManager() {
       log.debug("Stop cache manager");
       manager.stop();
    }
    
    /**
     * Returns an unmodifiable map containing configured entity/collection type configuration overrides.
     * This method should be used primarily for testing/checking purpouses.
     * 
     * @return an unmodifiable map.
     */
    public Map<String, TypeOverrides> getTypeOverrides() {
       return Collections.unmodifiableMap(typeOverrides);
    }
    
    public Set<String> getDefinedConfigurations() {
       return Collections.unmodifiableSet(definedConfigurations);
    }
 
    protected EmbeddedCacheManager createCacheManager(Properties properties) throws CacheException {
       try {
          String configLoc = ConfigurationHelper.getString(
                INFINISPAN_CONFIG_RESOURCE_PROP, properties, DEF_INFINISPAN_CONFIG_RESOURCE);
-         ClassLoader classLoader = ClassLoaderHelper.getClassLoader();
+         ClassLoader classLoader = ClassLoaderHelper.getContextClassLoader();
          InputStream is = FileLookupFactory.newInstance().lookupFileStrict(
                configLoc, classLoader);
          ParserRegistry parserRegistry = new ParserRegistry(classLoader);
          ConfigurationBuilderHolder holder = parserRegistry.parse(is);
 
          // Override global jmx statistics exposure
          String globalStats = extractProperty(
                INFINISPAN_GLOBAL_STATISTICS_PROP, properties);
          if (globalStats != null)
             holder.getGlobalConfigurationBuilder().globalJmxStatistics()
                   .enabled(Boolean.parseBoolean(globalStats));
 
          return createCacheManager(holder);
       } catch (IOException e) {
          throw new CacheException("Unable to create default cache manager", e);
       }
    }
 
    protected EmbeddedCacheManager createCacheManager(
          ConfigurationBuilderHolder holder) {
       return new DefaultCacheManager(holder, true);
    }
 
    private void startRegion(BaseRegion region, String regionName) {
       regionNames.add(regionName);
       getCacheCommandFactory(region.getCache()).addRegion(regionName, region);
    }
 
    private Map<String, TypeOverrides> initGenericDataTypeOverrides() {
       TypeOverrides entityOverrides = new TypeOverrides();
       entityOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(ENTITY_KEY, entityOverrides);
       TypeOverrides collectionOverrides = new TypeOverrides();
       collectionOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(COLLECTION_KEY, collectionOverrides);
       TypeOverrides naturalIdOverrides = new TypeOverrides();
       naturalIdOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(NATURAL_ID_KEY, naturalIdOverrides);
       TypeOverrides timestampOverrides = new TimestampTypeOverrides();
       timestampOverrides.setCacheName(DEF_TIMESTAMPS_RESOURCE);
       typeOverrides.put(TIMESTAMPS_KEY, timestampOverrides);
       TypeOverrides queryOverrides = new TypeOverrides();
       queryOverrides.setCacheName(DEF_QUERY_RESOURCE);
       typeOverrides.put(QUERY_KEY, queryOverrides);
       return typeOverrides;
    }
 
    private void dissectProperty(int prefixLoc, String key, Properties properties) {
       TypeOverrides cfgOverride;
       int suffixLoc;
       if (!key.equals(INFINISPAN_CONFIG_RESOURCE_PROP) && (suffixLoc = key.indexOf(CONFIG_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setCacheName(extractProperty(key, properties));
       } else if ((suffixLoc = key.indexOf(STRATEGY_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionStrategy(extractProperty(key, properties));
       } else if ((suffixLoc = key.indexOf(WAKE_UP_INTERVAL_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionWakeUpInterval(Long.parseLong(extractProperty(key, properties)));
       } else if ((suffixLoc = key.indexOf(MAX_ENTRIES_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionMaxEntries(Integer.parseInt(extractProperty(key, properties)));
       } else if ((suffixLoc = key.indexOf(LIFESPAN_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setExpirationLifespan(Long.parseLong(extractProperty(key, properties)));
       } else if ((suffixLoc = key.indexOf(MAX_IDLE_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setExpirationMaxIdle(Long.parseLong(extractProperty(key, properties)));
       }
    }
 
    private String extractProperty(String key, Properties properties) {
       String value = ConfigurationHelper.extractPropertyValue(key, properties);
       log.debugf("Configuration override via property %s: %s", key, value);
       return value;
    }
 
    private TypeOverrides getOrCreateConfig(int prefixLoc, String key, int suffixLoc) {
       String name = key.substring(prefixLoc + PREFIX.length(), suffixLoc);
       TypeOverrides cfgOverride = typeOverrides.get(name);
       if (cfgOverride == null) {
          cfgOverride = new TypeOverrides();
          typeOverrides.put(name, cfgOverride);
       }
       return cfgOverride;
    }
 
    private void defineGenericDataTypeCacheConfigurations(Properties properties) {
       String[] defaultGenericDataTypes = new String[]{ENTITY_KEY, COLLECTION_KEY, TIMESTAMPS_KEY, QUERY_KEY};
       for (String type : defaultGenericDataTypes) {
          TypeOverrides override = overrideStatisticsIfPresent(typeOverrides.get(type), properties);
          String cacheName = override.getCacheName();
          ConfigurationBuilder builder = new ConfigurationBuilder();
          // Read base configuration
          applyConfiguration(cacheName, builder);
 
          // Apply overrides
          override.applyTo(builder);
          // Configure transaction manager
          configureTransactionManager(builder, cacheName, properties);
          // Define configuration, validate and then apply
          Configuration cfg = builder.build();
          override.validateInfinispanConfiguration(cfg);
          manager.defineConfiguration(cacheName, cfg);
          definedConfigurations.add(cacheName);
       }
    }
 
    private AdvancedCache getCache(String regionName, String typeKey, Properties properties) {
       TypeOverrides regionOverride = typeOverrides.get(regionName);
       if (!definedConfigurations.contains(regionName)) {
          String templateCacheName;
          Configuration regionCacheCfg;
          ConfigurationBuilder builder = new ConfigurationBuilder();
          if (regionOverride != null) {
             if (log.isDebugEnabled()) log.debug("Cache region specific configuration exists: " + regionOverride);
             String cacheName = regionOverride.getCacheName();
             if (cacheName != null) // Region specific override with a given cache name
                templateCacheName = cacheName;
             else // Region specific override without cache name, so template cache name is generic for data type.
                templateCacheName = typeOverrides.get(typeKey).getCacheName();
 
             // Read template configuration
             applyConfiguration(templateCacheName, builder);
 
             regionOverride = overrideStatisticsIfPresent(regionOverride, properties);
             regionOverride.applyTo(builder);
 
          } else {
             // No region specific overrides, template cache name is generic for data type.
             templateCacheName = typeOverrides.get(typeKey).getCacheName();
             // Read template configuration
             builder.read(manager.getCacheConfiguration(templateCacheName));
             // Apply overrides
             typeOverrides.get(typeKey).applyTo(builder);
          }
          // Configure transaction manager
          configureTransactionManager(builder, templateCacheName, properties);
          // Define configuration
          manager.defineConfiguration(regionName, builder.build());
          definedConfigurations.add(regionName);
       }
       AdvancedCache cache = manager.getCache(regionName).getAdvancedCache();
       if (!cache.getStatus().allowInvocations()) {
          cache.start();
       }
       return createCacheWrapper(cache);
    }
 
    private void applyConfiguration(String cacheName, ConfigurationBuilder builder) {
       Configuration cfg = manager.getCacheConfiguration(cacheName);
       if (cfg != null)
          builder.read(cfg);
    }
 
    private CacheCommandFactory getCacheCommandFactory(AdvancedCache cache) {
       GlobalComponentRegistry globalCr = cache.getComponentRegistry()
             .getGlobalComponentRegistry();
 
       Map<Byte, ModuleCommandFactory> factories =
          (Map<Byte, ModuleCommandFactory>) globalCr
                .getComponent("org.infinispan.modules.command.factories");
 
       for (ModuleCommandFactory factory : factories.values()) {
          if (factory instanceof CacheCommandFactory)
             return (CacheCommandFactory) factory;
       }
 
       throw new CacheException("Infinispan custom cache command factory not " +
             "installed (possibly because the classloader where Infinispan " +
             "lives couldn't find the Hibernate Infinispan cache provider)");
    }
 
    protected AdvancedCache createCacheWrapper(AdvancedCache cache) {
       return cache;
    }
 
    private void configureTransactionManager(ConfigurationBuilder builder,
          String cacheName, Properties properties) {
       // Get existing configuration to verify whether a tm was configured or not.
       Configuration baseCfg = manager.getCacheConfiguration(cacheName);
       if (baseCfg != null && baseCfg.transaction().transactionMode().isTransactional()) {
          String ispnTmLookupClassName = baseCfg.transaction().transactionManagerLookup().getClass().getName();
          String hbTmLookupClassName = org.hibernate.cache.infinispan.tm.HibernateTransactionManagerLookup.class.getName();
          if (GenericTransactionManagerLookup.class.getName().equals(ispnTmLookupClassName)) {
             log.debug("Using default Infinispan transaction manager lookup " +
                   "instance (GenericTransactionManagerLookup), overriding it " +
                   "with Hibernate transaction manager lookup");
             builder.transaction().transactionManagerLookup(transactionManagerlookup);
          } else if (ispnTmLookupClassName != null && !ispnTmLookupClassName.equals(hbTmLookupClassName)) {
             log.debug("Infinispan is configured [" + ispnTmLookupClassName + "] with a different transaction manager lookup " +
                             "class than Hibernate [" + hbTmLookupClassName + "]");
          } else {
             // Infinispan TM lookup class null, so apply Hibernate one directly
             builder.transaction().transactionManagerLookup(transactionManagerlookup);
          }
 
          String useSyncProp = extractProperty(INFINISPAN_USE_SYNCHRONIZATION_PROP, properties);
          boolean useSync = useSyncProp == null ? DEF_USE_SYNCHRONIZATION : Boolean.parseBoolean(useSyncProp);
          builder.transaction().useSynchronization(useSync);
       }
    }
 
    private TypeOverrides overrideStatisticsIfPresent(TypeOverrides override, Properties properties) {
       String globalStats = extractProperty(INFINISPAN_GLOBAL_STATISTICS_PROP, properties);
       if (globalStats != null) {
          override.setExposeStatistics(Boolean.parseBoolean(globalStats));
       }
       return override;
    }
 }
