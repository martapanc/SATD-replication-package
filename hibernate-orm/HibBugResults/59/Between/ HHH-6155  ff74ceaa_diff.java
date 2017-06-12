diff --git a/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java b/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
index 324b6b84b7..6280b249a9 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/FilterKey.java
@@ -1,91 +1,91 @@
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
 package org.hibernate.cache;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.EntityMode;
 import org.hibernate.engine.TypedValue;
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
 import org.hibernate.type.Type;
 
 /**
  * Allows cached queries to be keyed by enabled filters.
  * 
  * @author Gavin King
  */
 public final class FilterKey implements Serializable {
 	private String filterName;
 	private Map filterParameters = new HashMap();
 	
 	public FilterKey(String name, Map params, Map types, EntityMode entityMode) {
 		filterName = name;
 		Iterator iter = params.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			Type type = (Type) types.get( me.getKey() );
 			filterParameters.put( me.getKey(), new TypedValue( type, me.getValue(), entityMode ) );
 		}
 	}
 	
 	public int hashCode() {
 		int result = 13;
 		result = 37 * result + filterName.hashCode();
 		result = 37 * result + filterParameters.hashCode();
 		return result;
 	}
 	
 	public boolean equals(Object other) {
 		if ( !(other instanceof FilterKey) ) return false;
 		FilterKey that = (FilterKey) other;
 		if ( !that.filterName.equals(filterName) ) return false;
 		if ( !that.filterParameters.equals(filterParameters) ) return false;
 		return true;
 	}
 	
 	public String toString() {
 		return "FilterKey[" + filterName + filterParameters + ']';
 	}
 	
 	public static Set createFilterKeys(Map enabledFilters, EntityMode entityMode) {
 		if ( enabledFilters.size()==0 ) return null;
 		Set result = new HashSet();
 		Iterator iter = enabledFilters.values().iterator();
 		while ( iter.hasNext() ) {
 			FilterImpl filter = (FilterImpl) iter.next();
 			FilterKey key = new FilterKey(
 					filter.getName(), 
 					filter.getParameters(), 
 					filter.getFilterDefinition().getParameterTypes(), 
 					entityMode
 				);
 			result.add(key);
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
index 440f80ad58..8e4abde6b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/RegionFactory.java
@@ -1,137 +1,137 @@
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
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Middleware LLC.
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
+ *
+ */
 package org.hibernate.cache;
-import java.util.Properties;
-import org.hibernate.cache.access.AccessType;
-import org.hibernate.cfg.Settings;
-
-/**
- * Contract for building second level cache regions.
- * <p/>
- * Implementors should define a constructor in one of two forms:<ul>
- * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
- * <li>MyRegionFactoryImpl()</li>
- * </ul>
- * Use the first when we need to read config properties prior to
- * {@link #start} being called.  For an example, have a look at
- * {@link org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge}
- * where we need the properties in order to determine which legacy 
- * {@link CacheProvider} to use so that we can answer the
- * {@link #isMinimalPutsEnabledByDefault()} question for the
- * {@link org.hibernate.cfg.SettingsFactory}.
- *
- * @author Steve Ebersole
- */
-public interface RegionFactory {
-
-	/**
-	 * Lifecycle callback to perform any necessary initialization of the
-	 * underlying cache implementation(s).  Called exactly once during the
-	 * construction of a {@link org.hibernate.impl.SessionFactoryImpl}.
-	 *
-	 * @param settings The settings in effect.
-	 * @param properties The defined cfg properties
-	 * @throws CacheException Indicates problems starting the L2 cache impl;
-	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
-	 * building.
-	 */
-	public void start(Settings settings, Properties properties) throws CacheException;
-
-	/**
-	 * Lifecycle callback to perform any necessary cleanup of the underlying
-	 * cache implementation(s).  Called exactly once during
-	 * {@link org.hibernate.SessionFactory#close}.
-	 */
-	public void stop();
-
-	/**
-	 * By default should we perform "minimal puts" when using this second
-	 * level cache implementation?
-	 *
-	 * @return True if "minimal puts" should be performed by default; false
-	 * otherwise.
-	 */
-	public boolean isMinimalPutsEnabledByDefault();
-
-	/**
-	 * Get the default access type for {@link EntityRegion entity} and
-	 * {@link CollectionRegion collection} regions.
-	 *
-	 * @return This factory's default access type.
-	 */
-	public AccessType getDefaultAccessType();
-
-	/**
-	 * Generate a timestamp.
-	 * <p/>
-	 * This is generally used for cache content locking/unlocking purposes
-	 * depending upon the access-strategy being used.
-	 *
-	 * @return The generated timestamp.
-	 */
-	public long nextTimestamp();
-
-	/**
-	 * Build a cache region specialized for storing entity data.
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @param metadata Information regarding the type of data to be cached
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
-
-	/**
-	 * Build a cache region specialized for storing collection data.
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @param metadata Information regarding the type of data to be cached
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
-
-	/**
-	 * Build a cache region specialized for storing query results
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
-
-	/**
-	 * Build a cache region specialized for storing update-timestamps data.
-	 *
-	 * @param regionName The name of the region.
-	 * @param properties Configuration properties.
-	 * @return The built region
-	 * @throws CacheException Indicates problems building the region.
-	 */
-	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
-}
+import java.util.Properties;
+import org.hibernate.cache.access.AccessType;
+import org.hibernate.cfg.Settings;
+
+/**
+ * Contract for building second level cache regions.
+ * <p/>
+ * Implementors should define a constructor in one of two forms:<ul>
+ * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
+ * <li>MyRegionFactoryImpl()</li>
+ * </ul>
+ * Use the first when we need to read config properties prior to
+ * {@link #start} being called.  For an example, have a look at
+ * {@link org.hibernate.cache.impl.bridge.RegionFactoryCacheProviderBridge}
+ * where we need the properties in order to determine which legacy 
+ * {@link CacheProvider} to use so that we can answer the
+ * {@link #isMinimalPutsEnabledByDefault()} question for the
+ * {@link org.hibernate.cfg.SettingsFactory}.
+ *
+ * @author Steve Ebersole
+ */
+public interface RegionFactory {
+
+	/**
+	 * Lifecycle callback to perform any necessary initialization of the
+	 * underlying cache implementation(s).  Called exactly once during the
+	 * construction of a {@link org.hibernate.internal.SessionFactoryImpl}.
+	 *
+	 * @param settings The settings in effect.
+	 * @param properties The defined cfg properties
+	 * @throws CacheException Indicates problems starting the L2 cache impl;
+	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
+	 * building.
+	 */
+	public void start(Settings settings, Properties properties) throws CacheException;
+
+	/**
+	 * Lifecycle callback to perform any necessary cleanup of the underlying
+	 * cache implementation(s).  Called exactly once during
+	 * {@link org.hibernate.SessionFactory#close}.
+	 */
+	public void stop();
+
+	/**
+	 * By default should we perform "minimal puts" when using this second
+	 * level cache implementation?
+	 *
+	 * @return True if "minimal puts" should be performed by default; false
+	 * otherwise.
+	 */
+	public boolean isMinimalPutsEnabledByDefault();
+
+	/**
+	 * Get the default access type for {@link EntityRegion entity} and
+	 * {@link CollectionRegion collection} regions.
+	 *
+	 * @return This factory's default access type.
+	 */
+	public AccessType getDefaultAccessType();
+
+	/**
+	 * Generate a timestamp.
+	 * <p/>
+	 * This is generally used for cache content locking/unlocking purposes
+	 * depending upon the access-strategy being used.
+	 *
+	 * @return The generated timestamp.
+	 */
+	public long nextTimestamp();
+
+	/**
+	 * Build a cache region specialized for storing entity data.
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @param metadata Information regarding the type of data to be cached
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
+
+	/**
+	 * Build a cache region specialized for storing collection data.
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @param metadata Information regarding the type of data to be cached
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
+
+	/**
+	 * Build a cache region specialized for storing query results
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
+
+	/**
+	 * Build a cache region specialized for storing update-timestamps data.
+	 *
+	 * @param regionName The name of the region.
+	 * @param properties Configuration properties.
+	 * @return The built region
+	 * @throws CacheException Indicates problems building the region.
+	 */
+	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index fdadb187a2..aa8bb8b466 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,1096 +1,1096 @@
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
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
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
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
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
         LOG.debugf( "Mapping Package %s", packageName );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
index 03a9bc9c80..288dbfb5bd 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/DetachedCriteria.java
@@ -1,183 +1,183 @@
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
 package org.hibernate.criterion;
 import java.io.Serializable;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * Some applications need to create criteria queries in "detached
  * mode", where the Hibernate session is not available. This class
  * may be instantiated anywhere, and then a <literal>Criteria</literal>
  * may be obtained by passing a session to 
  * <literal>getExecutableCriteria()</literal>. All methods have the
  * same semantics and behavior as the corresponding methods of the
  * <literal>Criteria</literal> interface.
  * 
  * @see org.hibernate.Criteria
  * @author Gavin King
  */
 public class DetachedCriteria implements CriteriaSpecification, Serializable {
 	
 	private final CriteriaImpl impl;
 	private final Criteria criteria;
 	
 	protected DetachedCriteria(String entityName) {
 		impl = new CriteriaImpl(entityName, null);
 		criteria = impl;
 	}
 	
 	protected DetachedCriteria(String entityName, String alias) {
 		impl = new CriteriaImpl(entityName, alias, null);
 		criteria = impl;
 	}
 	
 	protected DetachedCriteria(CriteriaImpl impl, Criteria criteria) {
 		this.impl = impl;
 		this.criteria = criteria;
 	}
 	
 	/**
 	 * Get an executable instance of <literal>Criteria</literal>,
 	 * to actually run the query.
 	 */
 	public Criteria getExecutableCriteria(Session session) {
 		impl.setSession( ( SessionImplementor ) session );
 		return impl;
 	}
 	
 	public static DetachedCriteria forEntityName(String entityName) {
 		return new DetachedCriteria(entityName);
 	}
 	
 	public static DetachedCriteria forEntityName(String entityName, String alias) {
 		return new DetachedCriteria(entityName, alias);
 	}
 	
 	public static DetachedCriteria forClass(Class clazz) {
 		return new DetachedCriteria( clazz.getName() );
 	}
 	
 	public static DetachedCriteria forClass(Class clazz, String alias) {
 		return new DetachedCriteria( clazz.getName() , alias );
 	}
 	
 	public DetachedCriteria add(Criterion criterion) {
 		criteria.add(criterion);
 		return this;
 	}
 
 	public DetachedCriteria addOrder(Order order) {
 		criteria.addOrder(order);
 		return this;
 	}
 
 	public DetachedCriteria createAlias(String associationPath, String alias)
 	throws HibernateException {
 		criteria.createAlias(associationPath, alias);
 		return this;
 	}
 
 	public DetachedCriteria createCriteria(String associationPath, String alias)
 	throws HibernateException {
 		return new DetachedCriteria( impl, criteria.createCriteria(associationPath, alias) );
 	}
 
 	public DetachedCriteria createCriteria(String associationPath)
 	throws HibernateException {
 		return new DetachedCriteria( impl, criteria.createCriteria(associationPath) );
 	}
 
 	public String getAlias() {
 		return criteria.getAlias();
 	}
 
 	public DetachedCriteria setFetchMode(String associationPath, FetchMode mode)
 	throws HibernateException {
 		criteria.setFetchMode(associationPath, mode);
 		return this;
 	}
 
 	public DetachedCriteria setProjection(Projection projection) {
 		criteria.setProjection(projection);
 		return this;
 	}
 
 	public DetachedCriteria setResultTransformer(ResultTransformer resultTransformer) {
 		criteria.setResultTransformer(resultTransformer);
 		return this;
 	}
 	
 	public String toString() {
 		return "DetachableCriteria(" + criteria.toString() + ')';
 	}
 	
 	CriteriaImpl getCriteriaImpl() {
 		return impl;
 	}
 
     public DetachedCriteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
         criteria.createAlias(associationPath, alias, joinType);
         return this;
     }
 	
 	public DetachedCriteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
 		criteria.createAlias(associationPath, alias, joinType, withClause);
 		return this;
 	}
 	
 	public DetachedCriteria createCriteria(String associationPath, int joinType) throws HibernateException {
         return new DetachedCriteria(impl, criteria.createCriteria(associationPath, joinType));
     }
 
     public DetachedCriteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
         return new DetachedCriteria(impl, criteria.createCriteria(associationPath, alias, joinType));
     }
 	
 	public DetachedCriteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
 		return new DetachedCriteria(impl, criteria.createCriteria(associationPath, alias, joinType, withClause));
 	}
 	
 	public DetachedCriteria setComment(String comment) {
         criteria.setComment(comment);
         return this;
     }
 
     public DetachedCriteria setLockMode(LockMode lockMode) {
         criteria.setLockMode(lockMode);
         return this;
     }
 
     public DetachedCriteria setLockMode(String alias, LockMode lockMode) {
         criteria.setLockMode(alias, lockMode);
         return this;
     }
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
index a32f5779ec..422f46f0ef 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/SubqueryExpression.java
@@ -1,155 +1,155 @@
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
 package org.hibernate.criterion;
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TypedValue;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.loader.criteria.CriteriaJoinWalker;
 import org.hibernate.loader.criteria.CriteriaQueryTranslator;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public abstract class SubqueryExpression implements Criterion {
 	
 	private CriteriaImpl criteriaImpl;
 	private String quantifier;
 	private String op;
 	private QueryParameters params;
 	private Type[] types;
 	private CriteriaQueryTranslator innerQuery;
 
 	protected Type[] getTypes() {
 		return types;
 	}
 	
 	protected SubqueryExpression(String op, String quantifier, DetachedCriteria dc) {
 		this.criteriaImpl = dc.getCriteriaImpl();
 		this.quantifier = quantifier;
 		this.op = op;
 	}
 	
 	protected abstract String toLeftSqlString(Criteria criteria, CriteriaQuery outerQuery);
 
 	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
 		final SessionFactoryImplementor factory = criteriaQuery.getFactory();
 		final OuterJoinLoadable persister =
 				( OuterJoinLoadable ) factory.getEntityPersister( criteriaImpl.getEntityOrClassName() );
 
 		createAndSetInnerQuery( criteriaQuery, factory );
 		criteriaImpl.setSession( deriveRootSession( criteria ) );
 
 		CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister,
 				innerQuery,
 				factory,
 				criteriaImpl,
 				criteriaImpl.getEntityOrClassName(),
 				criteriaImpl.getSession().getLoadQueryInfluencers(),
 				innerQuery.getRootSQLALias()
 		);
 
 		String sql = walker.getSQLString();
 
 		final StringBuffer buf = new StringBuffer( toLeftSqlString(criteria, criteriaQuery) );
 		if ( op != null ) {
 			buf.append( ' ' ).append( op ).append( ' ' );
 		}
 		if ( quantifier != null ) {
 			buf.append( quantifier ).append( ' ' );
 		}
 		return buf.append( '(' ).append( sql ).append( ')' )
 				.toString();
 	}
 
 	private SessionImplementor deriveRootSession(Criteria criteria) {
 		if ( criteria instanceof CriteriaImpl ) {
 			return ( ( CriteriaImpl ) criteria ).getSession();
 		}
 		else if ( criteria instanceof CriteriaImpl.Subcriteria ) {
 			return deriveRootSession( ( ( CriteriaImpl.Subcriteria ) criteria ).getParent() );
 		}
 		else {
 			// could happen for custom Criteria impls.  Not likely, but...
 			// 		for long term solution, see HHH-3514
 			return null;
 		}
 	}
 
 	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) 
 	throws HibernateException {
 		//the following two lines were added to ensure that this.params is not null, which
 		//can happen with two-deep nested subqueries
 		SessionFactoryImplementor factory = criteriaQuery.getFactory();
 		createAndSetInnerQuery(criteriaQuery, factory);
 		
 		Type[] ppTypes = params.getPositionalParameterTypes();
 		Object[] ppValues = params.getPositionalParameterValues();
 		TypedValue[] tv = new TypedValue[ppTypes.length];
 		for ( int i=0; i<ppTypes.length; i++ ) {
 			tv[i] = new TypedValue( ppTypes[i], ppValues[i], EntityMode.POJO );
 		}
 		return tv;
 	}
 
 	/**
 	 * Creates the inner query used to extract some useful information about types, since it is needed in both methods.
 	 *
 	 * @param criteriaQuery The criteria query
 	 * @param factory The session factory.
 	 */
 	private void createAndSetInnerQuery(CriteriaQuery criteriaQuery, SessionFactoryImplementor factory) {
 		if ( innerQuery == null ) {
 			//with two-deep subqueries, the same alias would get generated for
 			//both using the old method (criteriaQuery.generateSQLAlias()), so
 			//that is now used as a fallback if the main criteria alias isn't set
 			String alias;
 			if ( this.criteriaImpl.getAlias() == null ) {
 				alias = criteriaQuery.generateSQLAlias();
 			}
 			else {
 				alias = this.criteriaImpl.getAlias() + "_";
 			}
 
 			innerQuery = new CriteriaQueryTranslator(
 					factory,
 					criteriaImpl,
 					criteriaImpl.getEntityOrClassName(), //implicit polymorphism not supported (would need a union)
 					alias,
 					criteriaQuery
 				);
 
 			params = innerQuery.getQueryParameters();
 			types = innerQuery.getProjectedTypes();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java b/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
index cd487952ee..09ae097f3b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/LoadQueryInfluencers.java
@@ -1,199 +1,199 @@
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
 package org.hibernate.engine;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.Filter;
 import org.hibernate.UnknownProfileException;
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
 import org.hibernate.type.Type;
 
 /**
  * Centralize all options which can influence the SQL query needed to load an
  * entity.  Currently such influencers are defined as:<ul>
  * <li>filters</li>
  * <li>fetch profiles</li>
  * <li>internal fetch profile (merge profile, etc)</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class LoadQueryInfluencers implements Serializable {
 	/**
 	 * Static reference useful for cases where we are creating load SQL
 	 * outside the context of any influencers.  One such example is
 	 * anything created by the session factory.
 	 */
 	public static LoadQueryInfluencers NONE = new LoadQueryInfluencers();
 
 	private final SessionFactoryImplementor sessionFactory;
 	private String internalFetchProfile;
 	private Map enabledFilters;
 	private Set enabledFetchProfileNames;
 
 	public LoadQueryInfluencers() {
 		this( null, java.util.Collections.EMPTY_MAP, java.util.Collections.EMPTY_SET );
 	}
 
 	public LoadQueryInfluencers(SessionFactoryImplementor sessionFactory) {
 		this( sessionFactory, new HashMap(), new HashSet() );
 	}
 
 	private LoadQueryInfluencers(SessionFactoryImplementor sessionFactory, Map enabledFilters, Set enabledFetchProfileNames) {
 		this.sessionFactory = sessionFactory;
 		this.enabledFilters = enabledFilters;
 		this.enabledFetchProfileNames = enabledFetchProfileNames;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getInternalFetchProfile() {
 		return internalFetchProfile;
 	}
 
 	public void setInternalFetchProfile(String internalFetchProfile) {
 		if ( sessionFactory == null ) {
 			// thats the signal that this is the immutable, context-less
 			// variety
 			throw new IllegalStateException( "Cannot modify context-less LoadQueryInfluencers" );
 		}
 		this.internalFetchProfile = internalFetchProfile;
 	}
 
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasEnabledFilters() {
 		return enabledFilters != null && !enabledFilters.isEmpty();
 	}
 
 	public Map getEnabledFilters() {
 		// First, validate all the enabled filters...
 		//TODO: this implementation has bad performance
 		Iterator itr = enabledFilters.values().iterator();
 		while ( itr.hasNext() ) {
 			final Filter filter = ( Filter ) itr.next();
 			filter.validate();
 		}
 		return enabledFilters;
 	}
 
 	/**
 	 * Returns an unmodifiable Set of enabled filter names.
 	 * @return an unmodifiable Set of enabled filter names.
 	 */
 	public Set getEnabledFilterNames() {
 		return java.util.Collections.unmodifiableSet( enabledFilters.keySet() );
 	}
 
 	public Filter getEnabledFilter(String filterName) {
 		return ( Filter ) enabledFilters.get( filterName );
 	}
 
 	public Filter enableFilter(String filterName) {
 		FilterImpl filter = new FilterImpl( sessionFactory.getFilterDefinition( filterName ) );
 		enabledFilters.put( filterName, filter );
 		return filter;
 	}
 
 	public void disableFilter(String filterName) {
 		enabledFilters.remove( filterName );
 	}
 
 	public Object getFilterParameterValue(String filterParameterName) {
 		String[] parsed = parseFilterParameterName( filterParameterName );
 		FilterImpl filter = ( FilterImpl ) enabledFilters.get( parsed[0] );
 		if ( filter == null ) {
 			throw new IllegalArgumentException( "Filter [" + parsed[0] + "] currently not enabled" );
 		}
 		return filter.getParameter( parsed[1] );
 	}
 
 	public Type getFilterParameterType(String filterParameterName) {
 		String[] parsed = parseFilterParameterName( filterParameterName );
 		FilterDefinition filterDef = sessionFactory.getFilterDefinition( parsed[0] );
 		if ( filterDef == null ) {
 			throw new IllegalArgumentException( "Filter [" + parsed[0] + "] not defined" );
 		}
 		Type type = filterDef.getParameterType( parsed[1] );
 		if ( type == null ) {
 			// this is an internal error of some sort...
 			throw new InternalError( "Unable to locate type for filter parameter" );
 		}
 		return type;
 	}
 
 	public static String[] parseFilterParameterName(String filterParameterName) {
 		int dot = filterParameterName.indexOf( '.' );
 		if ( dot <= 0 ) {
 			throw new IllegalArgumentException( "Invalid filter-parameter name format" );
 		}
 		String filterName = filterParameterName.substring( 0, dot );
 		String parameterName = filterParameterName.substring( dot + 1 );
 		return new String[] { filterName, parameterName };
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasEnabledFetchProfiles() {
 		return enabledFetchProfileNames != null && !enabledFetchProfileNames.isEmpty();
 	}
 
 	public Set getEnabledFetchProfileNames() {
 		return enabledFetchProfileNames;
 	}
 
 	private void checkFetchProfileName(String name) {
 		if ( !sessionFactory.containsFetchProfileDefinition( name ) ) {
 			throw new UnknownProfileException( name );
 		}
 	}
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		return enabledFetchProfileNames.contains( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		enabledFetchProfileNames.add( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		enabledFetchProfileNames.remove( name );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java b/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
index 407e646353..1ac9565318 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Mapping.java
@@ -1,50 +1,50 @@
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
 package org.hibernate.engine;
 import org.hibernate.MappingException;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.type.Type;
 
 /**
  * Defines operations common to "compiled" mappings (ie. <tt>SessionFactory</tt>)
  * and "uncompiled" mappings (ie. <tt>Configuration</tt>) that are used by
  * implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.type.Type
- * @see org.hibernate.impl.SessionFactoryImpl
+ * @see org.hibernate.internal.SessionFactoryImpl
  * @see org.hibernate.cfg.Configuration
  * @author Gavin King
  */
 public interface Mapping {
 	/**
 	 * Allow access to the id generator factory, though this is only needed/allowed from configuration.
 	 * @return
 	 * @deprecated temporary solution 
 	 */
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory();
 	public Type getIdentifierType(String className) throws MappingException;
 	public String getIdentifierPropertyName(String className) throws MappingException;
 	public Type getReferencedPropertyType(String className, String propertyName) throws MappingException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java b/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
index b0f371e7fb..17100bb7d3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/QueryParameters.java
@@ -1,562 +1,562 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
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
  *
  */
 package org.hibernate.engine;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.hql.classic.ParserHelper;
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.pretty.Printer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * @author Gavin King
  */
 public final class QueryParameters {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryParameters.class.getName());
 
 	private Type[] positionalParameterTypes;
 	private Object[] positionalParameterValues;
 	private Map namedParameters;
 	private LockOptions lockOptions;
 	private RowSelection rowSelection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 	private ScrollMode scrollMode;
 	private Serializable[] collectionKeys;
 	private Object optionalObject;
 	private String optionalEntityName;
 	private Serializable optionalId;
 	private boolean isReadOnlyInitialized;
 	private boolean readOnly;
 	private boolean callable = false;
 	private boolean autodiscovertypes = false;
 	private boolean isNaturalKeyLookup;
 
 	private final ResultTransformer resultTransformer; // why is all others non final ?
 
 	private String processedSQL;
 	private Type[] processedPositionalParameterTypes;
 	private Object[] processedPositionalParameterValues;
 
 	public QueryParameters() {
 		this( ArrayHelper.EMPTY_TYPE_ARRAY, ArrayHelper.EMPTY_OBJECT_ARRAY );
 	}
 
 	public QueryParameters(Type type, Object value) {
 		this( new Type[] { type }, new Object[] { value } );
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] postionalParameterValues,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalObjectId) {
 		this( positionalParameterTypes, postionalParameterValues );
 		this.optionalObject = optionalObject;
 		this.optionalId = optionalObjectId;
 		this.optionalEntityName = optionalEntityName;
 
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] postionalParameterValues) {
 		this( positionalParameterTypes, postionalParameterValues, null, null, false, false, false, null, null, false, null );
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] postionalParameterValues,
 			final Serializable[] collectionKeys) {
 		this( positionalParameterTypes, postionalParameterValues, null, collectionKeys );
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] postionalParameterValues,
 			final Map namedParameters,
 			final Serializable[] collectionKeys) {
 		this(
 				positionalParameterTypes,
 				postionalParameterValues,
 				namedParameters,
 				null,
 				null,
 				false,
 				false,
 				false,
 				null,
 				null,
 				collectionKeys,
 				null
 		);
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final LockOptions lockOptions,
 			final RowSelection rowSelection,
 			final boolean isReadOnlyInitialized,
 			final boolean readOnly,
 			final boolean cacheable,
 			final String cacheRegion,
 			//final boolean forceCacheRefresh,
 			final String comment,
 			final boolean isLookupByNaturalKey,
 			final ResultTransformer transformer) {
 		this(
 				positionalParameterTypes,
 				positionalParameterValues,
 				null,
 				lockOptions,
 				rowSelection,
 				isReadOnlyInitialized,
 				readOnly,
 				cacheable,
 				cacheRegion,
 				comment,
 				null,
 				transformer
 		);
 		isNaturalKeyLookup = isLookupByNaturalKey;
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Map namedParameters,
 			final LockOptions lockOptions,
 			final RowSelection rowSelection,
 			final boolean isReadOnlyInitialized,
 			final boolean readOnly,
 			final boolean cacheable,
 			final String cacheRegion,
 			//final boolean forceCacheRefresh,
 			final String comment,
 			final Serializable[] collectionKeys,
 			ResultTransformer transformer) {
 		this.positionalParameterTypes = positionalParameterTypes;
 		this.positionalParameterValues = positionalParameterValues;
 		this.namedParameters = namedParameters;
 		this.lockOptions = lockOptions;
 		this.rowSelection = rowSelection;
 		this.cacheable = cacheable;
 		this.cacheRegion = cacheRegion;
 		//this.forceCacheRefresh = forceCacheRefresh;
 		this.comment = comment;
 		this.collectionKeys = collectionKeys;
 		this.isReadOnlyInitialized = isReadOnlyInitialized;
 		this.readOnly = readOnly;
 		this.resultTransformer = transformer;
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Map namedParameters,
 			final LockOptions lockOptions,
 			final RowSelection rowSelection,
 			final boolean isReadOnlyInitialized,
 			final boolean readOnly,
 			final boolean cacheable,
 			final String cacheRegion,
 			//final boolean forceCacheRefresh,
 			final String comment,
 			final Serializable[] collectionKeys,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final ResultTransformer transformer) {
 		this(
 				positionalParameterTypes,
 				positionalParameterValues,
 				namedParameters,
 				lockOptions,
 				rowSelection,
 				isReadOnlyInitialized,
 				readOnly,
 				cacheable,
 				cacheRegion,
 				comment,
 				collectionKeys,
 				transformer
 		);
 		this.optionalEntityName = optionalEntityName;
 		this.optionalId = optionalId;
 		this.optionalObject = optionalObject;
 	}
 
 	public boolean hasRowSelection() {
 		return rowSelection != null;
 	}
 
 	public Map getNamedParameters() {
 		return namedParameters;
 	}
 
 	public Type[] getPositionalParameterTypes() {
 		return positionalParameterTypes;
 	}
 
 	public Object[] getPositionalParameterValues() {
 		return positionalParameterValues;
 	}
 
 	public RowSelection getRowSelection() {
 		return rowSelection;
 	}
 
 	public ResultTransformer getResultTransformer() {
 		return resultTransformer;
 	}
 
 	public void setNamedParameters(Map map) {
 		namedParameters = map;
 	}
 
 	public void setPositionalParameterTypes(Type[] types) {
 		positionalParameterTypes = types;
 	}
 
 	public void setPositionalParameterValues(Object[] objects) {
 		positionalParameterValues = objects;
 	}
 
 	public void setRowSelection(RowSelection selection) {
 		rowSelection = selection;
 	}
 
 	public LockOptions getLockOptions() {
 		return lockOptions;
 	}
 
 	public void setLockOptions(LockOptions lockOptions) {
 		this.lockOptions = lockOptions;
 	}
 
 	public void traceParameters(SessionFactoryImplementor factory) throws HibernateException {
 		Printer print = new Printer( factory );
         if (positionalParameterValues.length != 0) LOG.trace("Parameters: "
                                                              + print.toString(positionalParameterTypes, positionalParameterValues));
         if (namedParameters != null) LOG.trace("Named parameters: " + print.toString(namedParameters));
 	}
 
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	public void setCacheable(boolean b) {
 		cacheable = b;
 	}
 
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	public void setCacheRegion(String cacheRegion) {
 		this.cacheRegion = cacheRegion;
 	}
 
 	public void validateParameters() throws QueryException {
 		int types = positionalParameterTypes == null ? 0 : positionalParameterTypes.length;
 		int values = positionalParameterValues == null ? 0 : positionalParameterValues.length;
 		if ( types != values ) {
 			throw new QueryException(
 					"Number of positional parameter types:" + types +
 							" does not match number of positional parameters: " + values
 			);
 		}
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public ScrollMode getScrollMode() {
 		return scrollMode;
 	}
 
 	public void setScrollMode(ScrollMode scrollMode) {
 		this.scrollMode = scrollMode;
 	}
 
 	public Serializable[] getCollectionKeys() {
 		return collectionKeys;
 	}
 
 	public void setCollectionKeys(Serializable[] collectionKeys) {
 		this.collectionKeys = collectionKeys;
 	}
 
 	public String getOptionalEntityName() {
 		return optionalEntityName;
 	}
 
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	public Serializable getOptionalId() {
 		return optionalId;
 	}
 
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	public Object getOptionalObject() {
 		return optionalObject;
 	}
 
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	/**
 	 * Has the read-only/modifiable mode been explicitly set?
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see QueryParameters#isReadOnly(SessionImplementor)
 	 *
 	 * @return true, the read-only/modifiable mode was explicitly set
 	 *         false, the read-only/modifiable mode was not explicitly set
 	 */
 	public boolean isReadOnlyInitialized() {
 		return isReadOnlyInitialized;
 	}
 
 	/**
 	 * Should entities and proxies loaded by the Query be put in read-only mode? The
 	 * read-only/modifiable setting must be initialized via QueryParameters#setReadOnly(boolean)
 	 * before calling this method.
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(SessionImplementor)
 	 * @see QueryParameters#setReadOnly(boolean)
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @return true, entities and proxies loaded by the Query will be put in read-only mode
 	 *         false, entities and proxies loaded by the Query will be put in modifiable mode
 	 * @throws IllegalStateException if the read-only/modifiable setting has not been
 	 * initialized (i.e., isReadOnlyInitialized() == false).
 	 */
 	public boolean isReadOnly() {
 		if ( ! isReadOnlyInitialized() ) {
 			throw new IllegalStateException( "cannot call isReadOnly() when isReadOnlyInitialized() returns false" );
 		}
 		return readOnly;
 	}
 
 	/**
 	 * Should entities and proxies loaded by the Query be put in read-only mode? If the
 	 * read-only/modifiable setting was not initialized
 	 * (i.e., QueryParameters#isReadOnlyInitialized() == false), then the default
 	 * read-only/modifiable setting for the persistence context is returned instead.
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @return true, entities and proxies loaded by the query will be put in read-only mode
 	 *         false, entities and proxies loaded by the query will be put in modifiable mode
 	 */
 	public boolean isReadOnly(SessionImplementor session) {
 		return ( isReadOnlyInitialized ?
 				isReadOnly() :
 				session.getPersistenceContext().isDefaultReadOnly()
 		);
 	}
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies loaded by the query.
 	 * 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(SessionImplementor)
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see org.hibernate.engine.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @return true, entities and proxies loaded by the query will be put in read-only mode
 	 *         false, entities and proxies loaded by the query will be put in modifiable mode
 	 */
 	public void setReadOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		this.isReadOnlyInitialized = true;
 	}
 
 	public void setCallable(boolean callable) {
 		this.callable = callable;
 	}
 
 	public boolean isCallable() {
 		return callable;
 	}
 
 	public boolean hasAutoDiscoverScalarTypes() {
 		return autodiscovertypes;
 	}
 
 	public void processFilters(String sql, SessionImplementor session) {
 		processFilters( sql, session.getLoadQueryInfluencers().getEnabledFilters(), session.getFactory() );
 	}
 
 	public void processFilters(String sql, Map filters, SessionFactoryImplementor factory) {
 		if ( filters.size() == 0 || sql.indexOf( ParserHelper.HQL_VARIABLE_PREFIX ) < 0 ) {
 			// HELLA IMPORTANT OPTIMIZATION!!!
 			processedPositionalParameterValues = getPositionalParameterValues();
 			processedPositionalParameterTypes = getPositionalParameterTypes();
 			processedSQL = sql;
 		}
 		else {
 			final Dialect dialect = factory.getDialect();
 			String symbols = new StringBuffer().append( ParserHelper.HQL_SEPARATORS )
 					.append( dialect.openQuote() )
 					.append( dialect.closeQuote() )
 					.toString();
 			StringTokenizer tokens = new StringTokenizer( sql, symbols, true );
 			StringBuffer result = new StringBuffer();
 
 			List parameters = new ArrayList();
 			List parameterTypes = new ArrayList();
 
 			int positionalIndex = 0;
 			while ( tokens.hasMoreTokens() ) {
 				final String token = tokens.nextToken();
 				if ( token.startsWith( ParserHelper.HQL_VARIABLE_PREFIX ) ) {
 					final String filterParameterName = token.substring( 1 );
 					final String[] parts = LoadQueryInfluencers.parseFilterParameterName( filterParameterName );
 					final FilterImpl filter = ( FilterImpl ) filters.get( parts[0] );
 					final Object value = filter.getParameter( parts[1] );
 					final Type type = filter.getFilterDefinition().getParameterType( parts[1] );
 					if ( value != null && Collection.class.isAssignableFrom( value.getClass() ) ) {
 						Iterator itr = ( ( Collection ) value ).iterator();
 						while ( itr.hasNext() ) {
 							Object elementValue = itr.next();
 							result.append( '?' );
 							parameters.add( elementValue );
 							parameterTypes.add( type );
 							if ( itr.hasNext() ) {
 								result.append( ", " );
 							}
 						}
 					}
 					else {
 						result.append( '?' );
 						parameters.add( value );
 						parameterTypes.add( type );
 					}
 				}
 				else {
 					if ( "?".equals( token ) && positionalIndex < getPositionalParameterValues().length ) {
 						parameters.add( getPositionalParameterValues()[positionalIndex] );
 						parameterTypes.add( getPositionalParameterTypes()[positionalIndex] );
 						positionalIndex++;
 					}
 					result.append( token );
 				}
 			}
 			processedPositionalParameterValues = parameters.toArray();
 			processedPositionalParameterTypes = ( Type[] ) parameterTypes.toArray( new Type[parameterTypes.size()] );
 			processedSQL = result.toString();
 		}
 	}
 
 	public String getFilteredSQL() {
 		return processedSQL;
 	}
 
 	public Object[] getFilteredPositionalParameterValues() {
 		return processedPositionalParameterValues;
 	}
 
 	public Type[] getFilteredPositionalParameterTypes() {
 		return processedPositionalParameterTypes;
 	}
 
 	public boolean isNaturalKeyLookup() {
 		return isNaturalKeyLookup;
 	}
 
 	public void setNaturalKeyLookup(boolean isNaturalKeyLookup) {
 		this.isNaturalKeyLookup = isNaturalKeyLookup;
 	}
 
 	public void setAutoDiscoverScalarTypes(boolean autodiscovertypes) {
 		this.autodiscovertypes = autodiscovertypes;
 	}
 
 	public QueryParameters createCopyUsing(RowSelection selection) {
 		QueryParameters copy = new QueryParameters(
 				this.positionalParameterTypes,
 				this.positionalParameterValues,
 				this.namedParameters,
 				this.lockOptions,
 				selection,
 				this.isReadOnlyInitialized,
 				this.readOnly,
 				this.cacheable,
 				this.cacheRegion,
 				this.comment,
 				this.collectionKeys,
 				this.optionalObject,
 				this.optionalEntityName,
 				this.optionalId,
 				this.resultTransformer
 		);
 		copy.processedSQL = this.processedSQL;
 		copy.processedPositionalParameterTypes = this.processedPositionalParameterTypes;
 		copy.processedPositionalParameterValues = this.processedPositionalParameterValues;
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
index 0dcd9fe9e8..950d67e654 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionFactoryImplementor.java
@@ -1,244 +1,241 @@
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
 package org.hibernate.engine;
 
-import java.sql.Connection;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
-import org.hibernate.ConnectionReleaseMode;
+
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
-import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
- * @see org.hibernate.impl.SessionFactoryImpl
+ * @see org.hibernate.internal.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	public Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	public EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@link #getJdbcServices().getDialect()}.{@link JdbcServices#getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	public Interceptor getInterceptor();
 
 	public QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	public Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	public String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the connection provider
 	 */
 	public ConnectionProvider getConnectionProvider();
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	public String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	public String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	public QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	public QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	public UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 
 	public NamedQueryDefinition getNamedQuery(String queryName);
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getSecondLevelCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	public Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionConverter getSQLExceptionConverter();
 	   // TODO: deprecate???
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 */
     public SqlExceptionHelper getSQLExceptionHelper();
 
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	public FetchProfile getFetchProfile(String name);
 
 	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index 843237ef8c..4574015f4f 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,393 +1,392 @@
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
 package org.hibernate.engine;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
-import org.hibernate.event.EventListeners;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Defines the internal contract between {@link org.hibernate.Session} / {@link org.hibernate.StatelessSession} and
  * other parts of Hibernate such as {@link Type}, {@link EntityPersister} and
  * {@link org.hibernate.persister.collection.CollectionPersister} implementors
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SessionImplementor extends Serializable, LobCreationContext {
 	/**
 	 * Match te method on {@link org.hibernate.Session} and {@link org.hibernate.StatelessSession}
 	 *
 	 * @return The tenant identifier of this session
 	 */
 	public String getTenantIdentifier();
 
 	/**
 	 * Provides access to JDBC connections
 	 *
 	 * @return The contract for accessing JDBC connections.
 	 */
 	public JdbcConnectionAccess getJdbcConnectionAccess();
 
 	/**
 	 * Hide the changing requirements of entity key creation
 	 *
 	 * @param id The entity id
 	 * @param persister The entity persister
 	 *
 	 * @return The entity key
 	 */
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister);
 
 	/**
 	 * Hide the changing requirements of cache key creation.
 	 *
 	 * @param id The entity identifier or collection key.
 	 * @param type The type
 	 * @param entityOrRoleName The entity name or collection role.
 	 *
 	 * @return The cache key
 	 */
 	public CacheKey generateCacheKey(Serializable id, final Type type, final String entityOrRoleName);
 
 	/**
 	 * Retrieves the interceptor currently in use by this event source.
 	 *
 	 * @return The interceptor.
 	 */
 	public Interceptor getInterceptor();
 
 	/**
 	 * Enable/disable automatic cache clearing from after transaction
 	 * completion (for EJB3)
 	 */
 	public void setAutoClear(boolean enabled);
 
 	/**
 	 * Disable automatic transaction joining.  The really only has any effect for CMT transactions.  The default
 	 * Hibernate behavior is to auto join any active JTA transaction (register {@link javax.transaction.Synchronization}).
 	 * JPA however defines an explicit join transaction operation.
 	 *
 	 * See javax.persistence.EntityManager#joinTransaction
 	 */
 	public void disableTransactionAutoJoin();
 
 	/**
 	 * Does this <tt>Session</tt> have an active Hibernate transaction
 	 * or is there a JTA transaction in progress?
 	 */
 	public boolean isTransactionInProgress();
 
 	/**
 	 * Initialize the collection (if not already initialized)
 	 */
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException;
 
 	/**
 	 * Load an instance without checking if it was deleted.
 	 *
 	 * When <tt>nullable</tt> is disabled this method may create a new proxy or
 	 * return an existing proxy; if it does not exist, throw an exception.
 	 *
 	 * When <tt>nullable</tt> is enabled, the method does not create new proxies
 	 * (but might return an existing proxy); if it does not exist, return
 	 * <tt>null</tt>.
 	 *
 	 * When <tt>eager</tt> is enabled, the object is eagerly fetched
 	 */
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable)
 	throws HibernateException;
 
 	/**
 	 * Load an instance immediately. This method is only called when lazily initializing a proxy.
 	 * Do not return the proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * System time before the start of the transaction
 	 */
 	public long getTimestamp();
 	/**
 	 * Get the creating <tt>SessionFactoryImplementor</tt>
 	 */
 	public SessionFactoryImplementor getFactory();
 
 	/**
 	 * Execute a <tt>find()</tt> query
 	 */
 	public List list(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute an <tt>iterate()</tt> query
 	 */
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a <tt>scroll()</tt> query
 	 */
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Execute a criteria query
 	 */
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode);
 	/**
 	 * Execute a criteria query
 	 */
 	public List list(CriteriaImpl criteria);
 
 	/**
 	 * Execute a filter
 	 */
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 	/**
 	 * Iterate a filter
 	 */
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Get the <tt>EntityPersister</tt> for any instance
 	 * @param entityName optional entity name
 	 * @param object the entity instance
 	 */
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Get the entity instance associated with the given <tt>Key</tt>,
 	 * calling the Interceptor if necessary
 	 */
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException;
 
 	/**
 	 * Return the identifier of the persistent object, or null if
 	 * not associated with the session
 	 */
 	public Serializable getContextEntityIdentifier(Object object);
 
 	/**
 	 * The best guess entity name for an entity not in an association
 	 */
 	public String bestGuessEntityName(Object object);
 
 	/**
 	 * The guessed entity name for an entity not in an association
 	 */
 	public String guessEntityName(Object entity) throws HibernateException;
 
 	/**
 	 * Instantiate the entity class, initializing with the given identifier
 	 */
 	public Object instantiate(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a fully built list.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The result list.
 	 * @throws HibernateException
 	 */
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a scrollable result.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 * @return The resulting scrollable result.
 	 * @throws HibernateException
 	 */
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 	throws HibernateException;
 
 	/**
 	 * Retreive the currently set value for a filter parameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter parameter value.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Object getFilterParameterValue(String filterParameterName);
 
 	/**
 	 * Retreive the type for a given filter parrameter.
 	 *
 	 * @param filterParameterName The filter parameter name in the format
 	 * {FILTER_NAME.PARAMETER_NAME}.
 	 * @return The filter param type
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Type getFilterParameterType(String filterParameterName);
 
 	/**
 	 * Return the currently enabled filters.  The filter map is keyed by filter
-	 * name, with values corresponding to the {@link org.hibernate.impl.FilterImpl}
+	 * name, with values corresponding to the {@link org.hibernate.internal.FilterImpl}
 	 * instance.
 	 * @return The currently enabled filters.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Map getEnabledFilters();
 
 	public int getDontFlushFromFind();
 
 	//TODO: temporary
 
 	/**
 	 * Get the persistence context for this session
 	 */
 	public PersistenceContext getPersistenceContext();
 
 	/**
 	 * Execute a HQL update or delete query
 	 */
 	int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a native SQL update or delete query
 	 */
 	int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException;
 
 
 	/**
 	 * Return changes to this session that have not been flushed yet.
 	 *
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException;
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException;
 
 	// copied from Session:
 
 	public EntityMode getEntityMode();
 	public CacheMode getCacheMode();
 	public void setCacheMode(CacheMode cm);
 	public boolean isOpen();
 	public boolean isConnected();
 	public FlushMode getFlushMode();
 	public void setFlushMode(FlushMode fm);
 	public Connection connection();
 	public void flush();
 
 	/**
 	 * Get a Query instance for a named query or named native SQL query
 	 */
 	public Query getNamedQuery(String name);
 	/**
 	 * Get a Query instance for a named native SQL query
 	 */
 	public Query getNamedSQLQuery(String name);
 
 	public boolean isEventSource();
 
 	public void afterScrollOperation();
 
 	/**
 	 * Get the <i>internal</i> fetch profile currently associated with this session.
 	 *
 	 * @return The current internal fetch profile, or null if none currently associated.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public String getFetchProfile();
 
 	/**
 	 * Set the current <i>internal</i> fetch profile for this session.
 	 *
 	 * @param name The internal fetch profile name to use
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public void setFetchProfile(String name);
 
 	/**
 	 * Retrieve access to the session's transaction coordinator.
 	 *
 	 * @return The transaction coordinator.
 	 */
 	public TransactionCoordinator getTransactionCoordinator();
 
 	/**
 	 * Determine whether the session is closed.  Provided separately from
 	 * {@link #isOpen()} as this method does not attempt any JTA synchronization
 	 * registration, where as {@link #isOpen()} does; which makes this one
 	 * nicer to use for most internal purposes.
 	 *
 	 * @return True if the session is closed; false otherwise.
 	 */
 	public boolean isClosed();
 
 	/**
 	 * Get the load query influencers associated with this session.
 	 *
 	 * @return the load query influencers associated with this session;
 	 * should never be null.
 	 */
 	public LoadQueryInfluencers getLoadQueryInfluencers();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java b/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
index f9ac14b64f..ffa1f79ca0 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/QueryPlanCache.java
@@ -1,347 +1,347 @@
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
 package org.hibernate.engine.query;
 
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.SimpleMRUCache;
 import org.hibernate.internal.util.collections.SoftLimitMRUCache;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Acts as a cache for compiled query plans, as well as query-parameter metadata.
  *
  * @see Environment#QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES
  * @see Environment#QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES
  *
  * @author Steve Ebersole
  */
 public class QueryPlanCache implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryPlanCache.class.getName());
 
 	private SessionFactoryImplementor factory;
 
 	public QueryPlanCache(SessionFactoryImplementor factory) {
 		int maxStrongReferenceCount = ConfigurationHelper.getInt(
 				Environment.QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES,
 				factory.getProperties(),
 				SoftLimitMRUCache.DEFAULT_STRONG_REF_COUNT
 		);
 		int maxSoftReferenceCount = ConfigurationHelper.getInt(
 				Environment.QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES,
 				factory.getProperties(),
 				SoftLimitMRUCache.DEFAULT_SOFT_REF_COUNT
 		);
 
 		this.factory = factory;
 		this.sqlParamMetadataCache = new SimpleMRUCache( maxStrongReferenceCount );
 		this.planCache = new SoftLimitMRUCache( maxStrongReferenceCount, maxSoftReferenceCount );
 	}
 
 	/**
 	 * simple cache of param metadata based on query string.  Ideally, the original "user-supplied query"
 	 * string should be used to obtain this metadata (i.e., not the para-list-expanded query string) to avoid
 	 * unnecessary cache entries.
 	 * <p>
 	 * Used solely for caching param metadata for native-sql queries, see {@link #getSQLParameterMetadata} for a
 	 * discussion as to why...
 	 */
 	private final SimpleMRUCache sqlParamMetadataCache;
 
 	/**
 	 * the cache of the actual plans...
 	 */
 	private final SoftLimitMRUCache planCache;
 
 
 	/**
 	 * Obtain the parameter metadata for given native-sql query.
 	 * <p/>
 	 * for native-sql queries, the param metadata is determined outside any relation to a query plan, because
 	 * query plan creation and/or retrieval for a native-sql query depends on all of the return types having been
 	 * set, which might not be the case up-front when param metadata would be most useful
 	 *
 	 * @param query The query
 	 * @return The parameter metadata
 	 */
 	public ParameterMetadata getSQLParameterMetadata(String query) {
 		ParameterMetadata metadata = ( ParameterMetadata ) sqlParamMetadataCache.get( query );
 		if ( metadata == null ) {
 			metadata = buildNativeSQLParameterMetadata( query );
 			sqlParamMetadataCache.put( query, metadata );
 		}
 		return metadata;
 	}
 
 	public HQLQueryPlan getHQLQueryPlan(String queryString, boolean shallow, Map enabledFilters)
 			throws QueryException, MappingException {
 		HQLQueryPlanKey key = new HQLQueryPlanKey( queryString, shallow, enabledFilters );
 		HQLQueryPlan plan = ( HQLQueryPlan ) planCache.get ( key );
 
 		if ( plan == null ) {
             LOG.trace("Unable to locate HQL query plan in cache; generating (" + queryString + ")");
 			plan = new HQLQueryPlan(queryString, shallow, enabledFilters, factory );
         } else LOG.trace("Located HQL query plan in cache (" + queryString + ")");
 
 		planCache.put( key, plan );
 
 		return plan;
 	}
 
 	public FilterQueryPlan getFilterQueryPlan(String filterString, String collectionRole, boolean shallow, Map enabledFilters)
 			throws QueryException, MappingException {
 		FilterQueryPlanKey key = new FilterQueryPlanKey( filterString, collectionRole, shallow, enabledFilters );
 		FilterQueryPlan plan = ( FilterQueryPlan ) planCache.get ( key );
 
 		if ( plan == null ) {
             LOG.trace("Unable to locate collection-filter query plan in cache; generating (" + collectionRole + " : "
                       + filterString + ")");
 			plan = new FilterQueryPlan( filterString, collectionRole, shallow, enabledFilters, factory );
         } else LOG.trace("Located collection-filter query plan in cache (" + collectionRole + " : " + filterString + ")");
 
 		planCache.put( key, plan );
 
 		return plan;
 	}
 
 	public NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) {
 		NativeSQLQueryPlan plan = ( NativeSQLQueryPlan ) planCache.get( spec );
 
 		if ( plan == null ) {
             LOG.trace("Unable to locate native-sql query plan in cache; generating (" + spec.getQueryString() + ")");
 			plan = new NativeSQLQueryPlan( spec, factory );
         } else LOG.trace("Located native-sql query plan in cache (" + spec.getQueryString() + ")");
 
 		planCache.put( spec, plan );
 		return plan;
 	}
 
 	@SuppressWarnings({ "UnnecessaryUnboxing" })
 	private ParameterMetadata buildNativeSQLParameterMetadata(String sqlString) {
 		ParamLocationRecognizer recognizer = ParamLocationRecognizer.parseLocations( sqlString );
 
 		OrdinalParameterDescriptor[] ordinalDescriptors =
 				new OrdinalParameterDescriptor[ recognizer.getOrdinalParameterLocationList().size() ];
 		for ( int i = 0; i < recognizer.getOrdinalParameterLocationList().size(); i++ ) {
 			final Integer position = ( Integer ) recognizer.getOrdinalParameterLocationList().get( i );
 			ordinalDescriptors[i] = new OrdinalParameterDescriptor( i, null, position.intValue() );
 		}
 
 		Iterator itr = recognizer.getNamedParameterDescriptionMap().entrySet().iterator();
 		Map<String,NamedParameterDescriptor> namedParamDescriptorMap = new HashMap<String,NamedParameterDescriptor>();
 		while( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String name = ( String ) entry.getKey();
 			final ParamLocationRecognizer.NamedParameterDescription description =
 					( ParamLocationRecognizer.NamedParameterDescription ) entry.getValue();
 			namedParamDescriptorMap.put(
 					name ,
 			        new NamedParameterDescriptor( name, null, description.buildPositionsArray(), description.isJpaStyle() )
 			);
 		}
 
 		return new ParameterMetadata( ordinalDescriptors, namedParamDescriptorMap );
 	}
 
 	private static class HQLQueryPlanKey implements Serializable {
 		private final String query;
 		private final boolean shallow;
 		private final Set<DynamicFilterKey> filterKeys;
 		private final int hashCode;
 
 		public HQLQueryPlanKey(String query, boolean shallow, Map enabledFilters) {
 			this.query = query;
 			this.shallow = shallow;
 
 			if ( enabledFilters == null || enabledFilters.isEmpty() ) {
 				filterKeys = Collections.emptySet();
 			}
 			else {
 				Set<DynamicFilterKey> tmp = new HashSet<DynamicFilterKey>(
 						CollectionHelper.determineProperSizing( enabledFilters ),
 						CollectionHelper.LOAD_FACTOR
 				);
 				for ( Object o : enabledFilters.values() ) {
 					tmp.add( new DynamicFilterKey( (FilterImpl) o ) );
 				}
 				this.filterKeys = Collections.unmodifiableSet( tmp );
 			}
 
 			int hash = query.hashCode();
 			hash = 29 * hash + ( shallow ? 1 : 0 );
 			hash = 29 * hash + filterKeys.hashCode();
 			this.hashCode = hash;
 		}
 
 		@Override
         public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			final HQLQueryPlanKey that = ( HQLQueryPlanKey ) o;
 
 			return shallow == that.shallow
 					&& filterKeys.equals( that.filterKeys )
 					&& query.equals( that.query );
 
 		}
 
 		@Override
         public int hashCode() {
 			return hashCode;
 		}
 	}
 
 	private static class DynamicFilterKey implements Serializable {
 		private final String filterName;
 		private final Map<String,Integer> parameterMetadata;
 		private final int hashCode;
 
 		@SuppressWarnings({ "UnnecessaryBoxing" })
 		private DynamicFilterKey(FilterImpl filter) {
 			this.filterName = filter.getName();
 			if ( filter.getParameters().isEmpty() ) {
 				parameterMetadata = Collections.emptyMap();
 			}
 			else {
 				parameterMetadata = new HashMap<String,Integer>(
 						CollectionHelper.determineProperSizing( filter.getParameters() ),
 						CollectionHelper.LOAD_FACTOR
 				);
 				for ( Object o : filter.getParameters().entrySet() ) {
 					final Map.Entry entry = (Map.Entry) o;
 					final String key = (String) entry.getKey();
 					final Integer valueCount;
 					if ( Collection.class.isInstance( entry.getValue() ) ) {
 						valueCount = new Integer( ( (Collection) entry.getValue() ).size() );
 					}
 					else {
 						valueCount = new Integer( 1 );
 					}
 					parameterMetadata.put( key, valueCount );
 				}
 			}
 
 			int hash = filterName.hashCode();
 			hash = 31 * hash + parameterMetadata.hashCode();
 			this.hashCode = hash;
 		}
 
 		@Override
         public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			DynamicFilterKey that = ( DynamicFilterKey ) o;
 
 			return filterName.equals( that.filterName )
 					&& parameterMetadata.equals( that.parameterMetadata );
 
 		}
 
 		@Override
         public int hashCode() {
 			return hashCode;
 		}
 	}
 
 	private static class FilterQueryPlanKey implements Serializable {
 		private final String query;
 		private final String collectionRole;
 		private final boolean shallow;
 		private final Set<String> filterNames;
 		private final int hashCode;
 
 		@SuppressWarnings({ "unchecked" })
 		public FilterQueryPlanKey(String query, String collectionRole, boolean shallow, Map enabledFilters) {
 			this.query = query;
 			this.collectionRole = collectionRole;
 			this.shallow = shallow;
 
 			if ( enabledFilters == null || enabledFilters.isEmpty() ) {
 				filterNames = Collections.emptySet();
 			}
 			else {
 				Set<String> tmp = new HashSet<String>();
 				tmp.addAll( enabledFilters.keySet() );
 				this.filterNames = Collections.unmodifiableSet( tmp );
 			}
 
 			int hash = query.hashCode();
 			hash = 29 * hash + collectionRole.hashCode();
 			hash = 29 * hash + ( shallow ? 1 : 0 );
 			hash = 29 * hash + filterNames.hashCode();
 			this.hashCode = hash;
 		}
 
 		@Override
         public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			final FilterQueryPlanKey that = ( FilterQueryPlanKey ) o;
 
 			return shallow == that.shallow
 					&& filterNames.equals( that.filterNames )
 					&& query.equals( that.query )
 					&& collectionRole.equals( that.collectionRole );
 
 		}
 
 		@Override
         public int hashCode() {
 			return hashCode;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
index c45a049d36..0016f72ebf 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/ast/util/JoinProcessor.java
@@ -1,254 +1,254 @@
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
 package org.hibernate.hql.ast.util;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.StringTokenizer;
 import org.hibernate.AssertionFailure;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.JoinSequence;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.hql.antlr.SqlTokenTypes;
 import org.hibernate.hql.ast.HqlSqlWalker;
 import org.hibernate.hql.ast.tree.DotNode;
 import org.hibernate.hql.ast.tree.FromClause;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.hql.ast.tree.ParameterContainer;
 import org.hibernate.hql.ast.tree.QueryNode;
 import org.hibernate.hql.classic.ParserHelper;
-import org.hibernate.impl.FilterImpl;
+import org.hibernate.internal.FilterImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.param.DynamicFilterParameterSpecification;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Performs the post-processing of the join information gathered during semantic analysis.
  * The join generating classes are complex, this encapsulates some of the JoinSequence-related
  * code.
  *
  * @author Joshua Davis
  */
 public class JoinProcessor implements SqlTokenTypes {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, JoinProcessor.class.getName());
 
 	private final HqlSqlWalker walker;
 	private final SyntheticAndFactory syntheticAndFactory;
 
 	/**
 	 * Constructs a new JoinProcessor.
 	 *
 	 * @param walker The walker to which we are bound, giving us access to needed resources.
 	 */
 	public JoinProcessor(HqlSqlWalker walker) {
 		this.walker = walker;
 		this.syntheticAndFactory = new SyntheticAndFactory( walker );
 	}
 
 	/**
 	 * Translates an AST join type (i.e., the token type) into a JoinFragment.XXX join type.
 	 *
 	 * @param astJoinType The AST join type (from HqlSqlTokenTypes or SqlTokenTypes)
 	 * @return a JoinFragment.XXX join type.
 	 * @see JoinFragment
 	 * @see SqlTokenTypes
 	 */
 	public static int toHibernateJoinType(int astJoinType) {
 		switch ( astJoinType ) {
 			case LEFT_OUTER:
 				return JoinFragment.LEFT_OUTER_JOIN;
 			case INNER:
 				return JoinFragment.INNER_JOIN;
 			case RIGHT_OUTER:
 				return JoinFragment.RIGHT_OUTER_JOIN;
 			default:
 				throw new AssertionFailure( "undefined join type " + astJoinType );
 		}
 	}
 
 	public void processJoins(QueryNode query) {
 		final FromClause fromClause = query.getFromClause();
 
 		final List fromElements;
 		if ( DotNode.useThetaStyleImplicitJoins ) {
 			// for regression testing against output from the old parser...
 			// found it easiest to simply reorder the FromElements here into ascending order
 			// in terms of injecting them into the resulting sql ast in orders relative to those
 			// expected by the old parser; this is definitely another of those "only needed
 			// for regression purposes".  The SyntheticAndFactory, then, simply injects them as it
 			// encounters them.
 			fromElements = new ArrayList();
 			ListIterator liter = fromClause.getFromElements().listIterator( fromClause.getFromElements().size() );
 			while ( liter.hasPrevious() ) {
 				fromElements.add( liter.previous() );
 			}
 		}
 		else {
 			fromElements = fromClause.getFromElements();
 		}
 
 		// Iterate through the alias,JoinSequence pairs and generate SQL token nodes.
 		Iterator iter = fromElements.iterator();
 		while ( iter.hasNext() ) {
 			final FromElement fromElement = ( FromElement ) iter.next();
 			JoinSequence join = fromElement.getJoinSequence();
             join.setSelector(new JoinSequence.Selector() {
                 public boolean includeSubclasses( String alias ) {
                     // The uber-rule here is that we need to include subclass joins if
                     // the FromElement is in any way dereferenced by a property from
                     // the subclass table; otherwise we end up with column references
                     // qualified by a non-existent table reference in the resulting SQL...
                     boolean containsTableAlias = fromClause.containsTableAlias(alias);
                     if (fromElement.isDereferencedBySubclassProperty()) {
                         // TODO : or should we return 'containsTableAlias'??
                         LOG.trace("Forcing inclusion of extra joins [alias=" + alias + ", containsTableAlias=" + containsTableAlias
                                   + "]");
                         return true;
                     }
                     boolean shallowQuery = walker.isShallowQuery();
                     boolean includeSubclasses = fromElement.isIncludeSubclasses();
                     boolean subQuery = fromClause.isSubQuery();
                     return includeSubclasses && containsTableAlias && !subQuery && !shallowQuery;
 					}
             }
 			);
 			addJoinNodes( query, join, fromElement );
 		}
 
 	}
 
 	private void addJoinNodes(QueryNode query, JoinSequence join, FromElement fromElement) {
 		JoinFragment joinFragment = join.toJoinFragment(
 				walker.getEnabledFilters(),
 				fromElement.useFromFragment() || fromElement.isDereferencedBySuperclassOrSubclassProperty(),
 				fromElement.getWithClauseFragment(),
 				fromElement.getWithClauseJoinAlias()
 		);
 
 		String frag = joinFragment.toFromFragmentString();
 		String whereFrag = joinFragment.toWhereFragmentString();
 
 		// If the from element represents a JOIN_FRAGMENT and it is
 		// a theta-style join, convert its type from JOIN_FRAGMENT
 		// to FROM_FRAGMENT
 		if ( fromElement.getType() == JOIN_FRAGMENT &&
 				( join.isThetaStyle() || StringHelper.isNotEmpty( whereFrag ) ) ) {
 			fromElement.setType( FROM_FRAGMENT );
 			fromElement.getJoinSequence().setUseThetaStyle( true ); // this is used during SqlGenerator processing
 		}
 
 		// If there is a FROM fragment and the FROM element is an explicit, then add the from part.
 		if ( fromElement.useFromFragment() /*&& StringHelper.isNotEmpty( frag )*/ ) {
 			String fromFragment = processFromFragment( frag, join ).trim();
             LOG.debugf("Using FROM fragment [%s]", fromFragment);
 			processDynamicFilterParameters(
 					fromFragment,
 					fromElement,
 					walker
 			);
 		}
 
 		syntheticAndFactory.addWhereFragment(
 				joinFragment,
 				whereFrag,
 				query,
 				fromElement,
 				walker
 		);
 	}
 
 	private String processFromFragment(String frag, JoinSequence join) {
 		String fromFragment = frag.trim();
 		// The FROM fragment will probably begin with ', '.  Remove this if it is present.
 		if ( fromFragment.startsWith( ", " ) ) {
 			fromFragment = fromFragment.substring( 2 );
 		}
 		return fromFragment;
 	}
 
 	public static void processDynamicFilterParameters(
 			final String sqlFragment,
 			final ParameterContainer container,
 			final HqlSqlWalker walker) {
 		if ( walker.getEnabledFilters().isEmpty()
 				&& ( ! hasDynamicFilterParam( sqlFragment ) )
 				&& ( ! ( hasCollectionFilterParam( sqlFragment ) ) ) ) {
 			return;
 		}
 
 		Dialect dialect = walker.getSessionFactoryHelper().getFactory().getDialect();
 		String symbols = new StringBuffer().append( ParserHelper.HQL_SEPARATORS )
 				.append( dialect.openQuote() )
 				.append( dialect.closeQuote() )
 				.toString();
 		StringTokenizer tokens = new StringTokenizer( sqlFragment, symbols, true );
 		StringBuffer result = new StringBuffer();
 
 		while ( tokens.hasMoreTokens() ) {
 			final String token = tokens.nextToken();
 			if ( token.startsWith( ParserHelper.HQL_VARIABLE_PREFIX ) ) {
 				final String filterParameterName = token.substring( 1 );
 				final String[] parts = LoadQueryInfluencers.parseFilterParameterName( filterParameterName );
 				final FilterImpl filter = ( FilterImpl ) walker.getEnabledFilters().get( parts[0] );
 				final Object value = filter.getParameter( parts[1] );
 				final Type type = filter.getFilterDefinition().getParameterType( parts[1] );
 				final String typeBindFragment = StringHelper.join(
 						",",
 						ArrayHelper.fillArray(
 								"?", type.getColumnSpan(
 								walker.getSessionFactoryHelper().getFactory()
 						)
 						)
 				);
 				final String bindFragment = ( value != null && Collection.class.isInstance( value ) )
 						? StringHelper.join( ",", ArrayHelper.fillArray( typeBindFragment, ( ( Collection ) value ).size() ) )
 						: typeBindFragment;
 				result.append( bindFragment );
 				container.addEmbeddedParameter( new DynamicFilterParameterSpecification( parts[0], parts[1], type ) );
 			}
 			else {
 				result.append( token );
 			}
 		}
 
 		container.setText( result.toString() );
 	}
 
 	private static boolean hasDynamicFilterParam(String sqlFragment) {
 		return sqlFragment.indexOf( ParserHelper.HQL_VARIABLE_PREFIX ) < 0;
 	}
 
 	private static boolean hasCollectionFilterParam(String sqlFragment) {
 		return sqlFragment.indexOf( "?" ) < 0;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
index ed68612245..c6e9edadeb 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
@@ -1,1058 +1,1058 @@
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
 package org.hibernate.hql.classic;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.JoinSequence;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.FilterTranslator;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.hql.NameGenerator;
 import org.hibernate.hql.ParameterTranslations;
-import org.hibernate.impl.IteratorImpl;
+import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.QuerySelect;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * An instance of <tt>QueryTranslator</tt> translates a Hibernate
  * query string to SQL.
  */
 public class QueryTranslatorImpl extends BasicLoader implements FilterTranslator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryTranslatorImpl.class.getName());
 
 	private static final String[] NO_RETURN_ALIASES = new String[] {};
 
 	private final String queryIdentifier;
 	private final String queryString;
 
 	private final Map typeMap = new LinkedHashMap();
 	private final Map collections = new LinkedHashMap();
 	private List returnedTypes = new ArrayList();
 	private final List fromTypes = new ArrayList();
 	private final List scalarTypes = new ArrayList();
 	private final Map namedParameters = new HashMap();
 	private final Map aliasNames = new HashMap();
 	private final Map oneToOneOwnerNames = new HashMap();
 	private final Map uniqueKeyOwnerReferences = new HashMap();
 	private final Map decoratedPropertyMappings = new HashMap();
 
 	private final List scalarSelectTokens = new ArrayList();
 	private final List whereTokens = new ArrayList();
 	private final List havingTokens = new ArrayList();
 	private final Map joins = new LinkedHashMap();
 	private final List orderByTokens = new ArrayList();
 	private final List groupByTokens = new ArrayList();
 	private final Set querySpaces = new HashSet();
 	private final Set entitiesToFetch = new HashSet();
 
 	private final Map pathAliases = new HashMap();
 	private final Map pathJoins = new HashMap();
 
 	private Queryable[] persisters;
 	private int[] owners;
 	private EntityType[] ownerAssociationTypes;
 	private String[] names;
 	private boolean[] includeInSelect;
 	private int selectLength;
 	private Type[] returnTypes;
 	private Type[] actualReturnTypes;
 	private String[][] scalarColumnNames;
 	private Map tokenReplacements;
 	private int nameCount = 0;
 	private int parameterCount = 0;
 	private boolean distinct = false;
 	private boolean compiled;
 	private String sqlString;
 	private Class holderClass;
 	private Constructor holderConstructor;
 	private boolean hasScalars;
 	private boolean shallowQuery;
 	private QueryTranslatorImpl superQuery;
 
 	private QueryableCollection collectionPersister;
 	private int collectionOwnerColumn = -1;
 	private String collectionOwnerName;
 	private String fetchName;
 
 	private String[] suffixes;
 
 	private Map enabledFilters;
 
 	/**
 	 * Construct a query translator
 	 *
 	 * @param queryIdentifier A unique identifier for the query of which this
 	 * translation is part; typically this is the original, user-supplied query string.
 	 * @param queryString The "preprocessed" query string; at the very least
 	 * already processed by {@link org.hibernate.hql.QuerySplitter}.
 	 * @param enabledFilters Any enabled filters.
 	 * @param factory The session factory.
 	 */
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 	        String queryString,
 	        Map enabledFilters,
 	        SessionFactoryImplementor factory) {
 		super( factory );
 		this.queryIdentifier = queryIdentifier;
 		this.queryString = queryString;
 		this.enabledFilters = enabledFilters;
 	}
 
 	/**
 	 * Construct a query translator; this form used internally.
 	 *
 	 * @param queryString The query string to process.
 	 * @param enabledFilters Any enabled filters.
 	 * @param factory The session factory.
 	 */
 	public QueryTranslatorImpl(
 	        String queryString,
 	        Map enabledFilters,
 	        SessionFactoryImplementor factory) {
 		this( queryString, queryString, enabledFilters, factory );
 	}
 
 	/**
 	 * Compile a subquery.
 	 *
 	 * @param superquery The containing query of the query to be compiled.
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems resolving
 	 * things referenced in the query.
 	 * @throws org.hibernate.QueryException Generally some form of syntatic
 	 * failure.
 	 */
 	void compile(QueryTranslatorImpl superquery) throws QueryException, MappingException {
 		this.tokenReplacements = superquery.tokenReplacements;
 		this.superQuery = superquery;
 		this.shallowQuery = true;
 		this.enabledFilters = superquery.getEnabledFilters();
 		compile();
 	}
 
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 */
 	public synchronized void compile(
 			Map replacements,
 			boolean scalar) throws QueryException, MappingException {
 		if ( !compiled ) {
 			this.tokenReplacements = replacements;
 			this.shallowQuery = scalar;
 			compile();
 		}
 	}
 
 	/**
 	 * Compile a filter. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 */
 	public synchronized void compile(
 			String collectionRole,
 			Map replacements,
 			boolean scalar) throws QueryException, MappingException {
 
 		if ( !isCompiled() ) {
 			addFromAssociation( "this", collectionRole );
 			compile( replacements, scalar );
 		}
 	}
 
 	/**
 	 * Compile the query (generate the SQL).
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems resolving
 	 * things referenced in the query.
 	 * @throws org.hibernate.QueryException Generally some form of syntatic
 	 * failure.
 	 */
 	private void compile() throws QueryException, MappingException {
 
         LOG.trace("Compiling query");
 		try {
 			ParserHelper.parse( new PreprocessingParser( tokenReplacements ),
 					queryString,
 					ParserHelper.HQL_SEPARATORS,
 					this );
 			renderSQL();
 		}
 		catch ( QueryException qe ) {
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 		catch ( MappingException me ) {
 			throw me;
 		}
 		catch ( Exception e ) {
             LOG.debug("Unexpected query compilation problem", e);
 			e.printStackTrace();
 			QueryException qe = new QueryException( "Incorrect query syntax", e );
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 
 		postInstantiate();
 
 		compiled = true;
 
 	}
 
 	@Override
     public String getSQLString() {
 		return sqlString;
 	}
 
 	public List collectSqlStrings() {
 		return ArrayHelper.toList( new String[] { sqlString } );
 	}
 
 	public String getQueryString() {
 		return queryString;
 	}
 
 	/**
 	 * Persisters for the return values of a <tt>find()</tt> style query.
 	 *
 	 * @return an array of <tt>EntityPersister</tt>s.
 	 */
 	@Override
     protected Loadable[] getEntityPersisters() {
 		return persisters;
 	}
 
 	/**
 	 * Types of the return values of an <tt>iterate()</tt> style query.
 	 *
 	 * @return an array of <tt>Type</tt>s.
 	 */
 	public Type[] getReturnTypes() {
 		return actualReturnTypes;
 	}
 
 	public String[] getReturnAliases() {
 		// return aliases not supported in classic translator!
 		return NO_RETURN_ALIASES;
 	}
 
 	public String[][] getColumnNames() {
 		return scalarColumnNames;
 	}
 
 	private static void logQuery(String hql, String sql) {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("HQL: %s", hql);
             LOG.debugf("SQL: %s", sql);
 		}
 	}
 
 	void setAliasName(String alias, String name) {
 		aliasNames.put( alias, name );
 	}
 
 	public String getAliasName(String alias) {
 		String name = ( String ) aliasNames.get( alias );
 		if ( name == null ) {
 			if ( superQuery != null ) {
 				name = superQuery.getAliasName( alias );
 			}
 			else {
 				name = alias;
 			}
 		}
 		return name;
 	}
 
 	String unalias(String path) {
 		String alias = StringHelper.root( path );
 		String name = getAliasName( alias );
         if (name != null) return name + path.substring(alias.length());
         return path;
 	}
 
 	void addEntityToFetch(String name, String oneToOneOwnerName, AssociationType ownerAssociationType) {
 		addEntityToFetch( name );
 		if ( oneToOneOwnerName != null ) oneToOneOwnerNames.put( name, oneToOneOwnerName );
 		if ( ownerAssociationType != null ) uniqueKeyOwnerReferences.put( name, ownerAssociationType );
 	}
 
 	private void addEntityToFetch(String name) {
 		entitiesToFetch.add( name );
 	}
 
 	private int nextCount() {
 		return ( superQuery == null ) ? nameCount++ : superQuery.nameCount++;
 	}
 
 	String createNameFor(String type) {
 		return StringHelper.generateAlias( type, nextCount() );
 	}
 
 	String createNameForCollection(String role) {
 		return StringHelper.generateAlias( role, nextCount() );
 	}
 
 	private String getType(String name) {
 		String type = ( String ) typeMap.get( name );
 		if ( type == null && superQuery != null ) {
 			type = superQuery.getType( name );
 		}
 		return type;
 	}
 
 	private String getRole(String name) {
 		String role = ( String ) collections.get( name );
 		if ( role == null && superQuery != null ) {
 			role = superQuery.getRole( name );
 		}
 		return role;
 	}
 
 	boolean isName(String name) {
 		return aliasNames.containsKey( name ) ||
 				typeMap.containsKey( name ) ||
 				collections.containsKey( name ) || (
 				superQuery != null && superQuery.isName( name )
 				);
 	}
 
 	PropertyMapping getPropertyMapping(String name) throws QueryException {
 		PropertyMapping decorator = getDecoratedPropertyMapping( name );
 		if ( decorator != null ) return decorator;
 
 		String type = getType( name );
 		if ( type == null ) {
 			String role = getRole( name );
 			if ( role == null ) {
 				throw new QueryException( "alias not found: " + name );
 			}
 			return getCollectionPersister( role ); //.getElementPropertyMapping();
 		}
 		else {
 			Queryable persister = getEntityPersister( type );
 			if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
 			return persister;
 		}
 	}
 
 	private PropertyMapping getDecoratedPropertyMapping(String name) {
 		return ( PropertyMapping ) decoratedPropertyMappings.get( name );
 	}
 
 	void decoratePropertyMapping(String name, PropertyMapping mapping) {
 		decoratedPropertyMappings.put( name, mapping );
 	}
 
 	private Queryable getEntityPersisterForName(String name) throws QueryException {
 		String type = getType( name );
 		Queryable persister = getEntityPersister( type );
 		if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
 		return persister;
 	}
 
 	Queryable getEntityPersisterUsingImports(String className) {
 		final String importedClassName = getFactory().getImportedClassName( className );
 		if ( importedClassName == null ) {
 			return null;
 		}
 		try {
 			return ( Queryable ) getFactory().getEntityPersister( importedClassName );
 		}
 		catch ( MappingException me ) {
 			return null;
 		}
 	}
 
 	Queryable getEntityPersister(String entityName) throws QueryException {
 		try {
 			return ( Queryable ) getFactory().getEntityPersister( entityName );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "persistent class not found: " + entityName );
 		}
 	}
 
 	QueryableCollection getCollectionPersister(String role) throws QueryException {
 		try {
 			return ( QueryableCollection ) getFactory().getCollectionPersister( role );
 		}
 		catch ( ClassCastException cce ) {
 			throw new QueryException( "collection role is not queryable: " + role );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "collection role not found: " + role );
 		}
 	}
 
 	void addType(String name, String type) {
 		typeMap.put( name, type );
 	}
 
 	void addCollection(String name, String role) {
 		collections.put( name, role );
 	}
 
 	void addFrom(String name, String type, JoinSequence joinSequence)
 			throws QueryException {
 		addType( name, type );
 		addFrom( name, joinSequence );
 	}
 
 	void addFromCollection(String name, String collectionRole, JoinSequence joinSequence)
 			throws QueryException {
 		//register collection role
 		addCollection( name, collectionRole );
 		addJoin( name, joinSequence );
 	}
 
 	void addFrom(String name, JoinSequence joinSequence)
 			throws QueryException {
 		fromTypes.add( name );
 		addJoin( name, joinSequence );
 	}
 
 	void addFromClass(String name, Queryable classPersister)
 			throws QueryException {
 		JoinSequence joinSequence = new JoinSequence( getFactory() )
 				.setRoot( classPersister, name );
 		//crossJoins.add(name);
 		addFrom( name, classPersister.getEntityName(), joinSequence );
 	}
 
 	void addSelectClass(String name) {
 		returnedTypes.add( name );
 	}
 
 	void addSelectScalar(Type type) {
 		scalarTypes.add( type );
 	}
 
 	void appendWhereToken(String token) {
 		whereTokens.add( token );
 	}
 
 	void appendHavingToken(String token) {
 		havingTokens.add( token );
 	}
 
 	void appendOrderByToken(String token) {
 		orderByTokens.add( token );
 	}
 
 	void appendGroupByToken(String token) {
 		groupByTokens.add( token );
 	}
 
 	void appendScalarSelectToken(String token) {
 		scalarSelectTokens.add( token );
 	}
 
 	void appendScalarSelectTokens(String[] tokens) {
 		scalarSelectTokens.add( tokens );
 	}
 
 	void addFromJoinOnly(String name, JoinSequence joinSequence) throws QueryException {
 		addJoin( name, joinSequence.getFromPart() );
 	}
 
 	void addJoin(String name, JoinSequence joinSequence) throws QueryException {
 		if ( !joins.containsKey( name ) ) joins.put( name, joinSequence );
 	}
 
 	void addNamedParameter(String name) {
 		if ( superQuery != null ) superQuery.addNamedParameter( name );
 		Integer loc = new Integer( parameterCount++ );
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			namedParameters.put( name, loc );
 		}
 		else if ( o instanceof Integer ) {
 			ArrayList list = new ArrayList( 4 );
 			list.add( o );
 			list.add( loc );
 			namedParameters.put( name, list );
 		}
 		else {
 			( ( ArrayList ) o ).add( loc );
 		}
 	}
 
 	@Override
     public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			QueryException qe = new QueryException( ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name );
 			qe.setQueryString( queryString );
 			throw qe;
 		}
 		if ( o instanceof Integer ) {
 			return new int[]{ ( ( Integer ) o ).intValue() };
 		}
 		else {
 			return ArrayHelper.toIntArray( ( ArrayList ) o );
 		}
 	}
 
 	private void renderSQL() throws QueryException, MappingException {
 
 		final int rtsize;
 		if ( returnedTypes.size() == 0 && scalarTypes.size() == 0 ) {
 			//ie no select clause in HQL
 			returnedTypes = fromTypes;
 			rtsize = returnedTypes.size();
 		}
 		else {
 			rtsize = returnedTypes.size();
 			Iterator iter = entitiesToFetch.iterator();
 			while ( iter.hasNext() ) {
 				returnedTypes.add( iter.next() );
 			}
 		}
 		int size = returnedTypes.size();
 		persisters = new Queryable[size];
 		names = new String[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 		suffixes = new String[size];
 		includeInSelect = new boolean[size];
 		for ( int i = 0; i < size; i++ ) {
 			String name = ( String ) returnedTypes.get( i );
 			//if ( !isName(name) ) throw new QueryException("unknown type: " + name);
 			persisters[i] = getEntityPersisterForName( name );
 			// TODO: cannot use generateSuffixes() - it handles the initial suffix differently.
 			suffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + '_';
 			names[i] = name;
 			includeInSelect[i] = !entitiesToFetch.contains( name );
 			if ( includeInSelect[i] ) selectLength++;
 			if ( name.equals( collectionOwnerName ) ) collectionOwnerColumn = i;
 			String oneToOneOwner = ( String ) oneToOneOwnerNames.get( name );
 			owners[i] = ( oneToOneOwner == null ) ? -1 : returnedTypes.indexOf( oneToOneOwner );
 			ownerAssociationTypes[i] = (EntityType) uniqueKeyOwnerReferences.get( name );
 		}
 
 		if ( ArrayHelper.isAllNegative( owners ) ) owners = null;
 
 		String scalarSelect = renderScalarSelect(); //Must be done here because of side-effect! yuck...
 
 		int scalarSize = scalarTypes.size();
 		hasScalars = scalarTypes.size() != rtsize;
 
 		returnTypes = new Type[scalarSize];
 		for ( int i = 0; i < scalarSize; i++ ) {
 			returnTypes[i] = ( Type ) scalarTypes.get( i );
 		}
 
 		QuerySelect sql = new QuerySelect( getFactory().getDialect() );
 		sql.setDistinct( distinct );
 
 		if ( !shallowQuery ) {
 			renderIdentifierSelect( sql );
 			renderPropertiesSelect( sql );
 		}
 
 		if ( collectionPersister != null ) {
 			sql.addSelectFragmentString( collectionPersister.selectFragment( fetchName, "__" ) );
 		}
 
 		if ( hasScalars || shallowQuery ) sql.addSelectFragmentString( scalarSelect );
 
 		//TODO: for some dialects it would be appropriate to add the renderOrderByPropertiesSelect() to other select strings
 		mergeJoins( sql.getJoinFragment() );
 
 		sql.setWhereTokens( whereTokens.iterator() );
 
 		sql.setGroupByTokens( groupByTokens.iterator() );
 		sql.setHavingTokens( havingTokens.iterator() );
 		sql.setOrderByTokens( orderByTokens.iterator() );
 
 		if ( collectionPersister != null && collectionPersister.hasOrdering() ) {
 			sql.addOrderBy( collectionPersister.getSQLOrderByString( fetchName ) );
 		}
 
 		scalarColumnNames = NameGenerator.generateColumnNames( returnTypes, getFactory() );
 
 		// initialize the Set of queried identifier spaces (ie. tables)
 		Iterator iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = getCollectionPersister( ( String ) iter.next() );
 			addQuerySpaces( p.getCollectionSpaces() );
 		}
 		iter = typeMap.keySet().iterator();
 		while ( iter.hasNext() ) {
 			Queryable p = getEntityPersisterForName( ( String ) iter.next() );
 			addQuerySpaces( p.getQuerySpaces() );
 		}
 
 		sqlString = sql.toQueryString();
 
 		if ( holderClass != null ) holderConstructor = ReflectHelper.getConstructor( holderClass, returnTypes );
 
 		if ( hasScalars ) {
 			actualReturnTypes = returnTypes;
 		}
 		else {
 			actualReturnTypes = new Type[selectLength];
 			int j = 0;
 			for ( int i = 0; i < persisters.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					actualReturnTypes[j++] = getFactory().getTypeResolver()
 							.getTypeFactory()
 							.manyToOne( persisters[i].getEntityName(), shallowQuery );
 				}
 			}
 		}
 
 	}
 
 	private void renderIdentifierSelect(QuerySelect sql) {
 		int size = returnedTypes.size();
 
 		for ( int k = 0; k < size; k++ ) {
 			String name = ( String ) returnedTypes.get( k );
 			String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 			sql.addSelectFragmentString( persisters[k].identifierSelectFragment( name, suffix ) );
 		}
 
 	}
 
 	/*private String renderOrderByPropertiesSelect() {
 		StringBuffer buf = new StringBuffer(10);
 
 		//add the columns we are ordering by to the select ID select clause
 		Iterator iter = orderByTokens.iterator();
 		while ( iter.hasNext() ) {
 			String token = (String) iter.next();
 			if ( token.lastIndexOf(".") > 0 ) {
 				//ie. it is of form "foo.bar", not of form "asc" or "desc"
 				buf.append(StringHelper.COMMA_SPACE).append(token);
 			}
 		}
 
 		return buf.toString();
 	}*/
 
 	private void renderPropertiesSelect(QuerySelect sql) {
 		int size = returnedTypes.size();
 		for ( int k = 0; k < size; k++ ) {
 			String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 			String name = ( String ) returnedTypes.get( k );
 			sql.addSelectFragmentString( persisters[k].propertySelectFragment( name, suffix, false ) );
 		}
 	}
 
 	/**
 	 * WARNING: side-effecty
 	 */
 	private String renderScalarSelect() {
 
 		boolean isSubselect = superQuery != null;
 
 		StringBuffer buf = new StringBuffer( 20 );
 
 		if ( scalarTypes.size() == 0 ) {
 			//ie. no select clause
 			int size = returnedTypes.size();
 			for ( int k = 0; k < size; k++ ) {
 
 				scalarTypes.add(
 						getFactory().getTypeResolver().getTypeFactory().manyToOne( persisters[k].getEntityName(), shallowQuery )
 				);
 
 				String[] idColumnNames = persisters[k].getIdentifierColumnNames();
 				for ( int i = 0; i < idColumnNames.length; i++ ) {
 					buf.append( returnedTypes.get( k ) ).append( '.' ).append( idColumnNames[i] );
 					if ( !isSubselect ) buf.append( " as " ).append( NameGenerator.scalarName( k, i ) );
 					if ( i != idColumnNames.length - 1 || k != size - 1 ) buf.append( ", " );
 				}
 
 			}
 
 		}
 		else {
 			//there _was_ a select clause
 			Iterator iter = scalarSelectTokens.iterator();
 			int c = 0;
 			boolean nolast = false; //real hacky...
 			int parenCount = 0; // used to count the nesting of parentheses
 			while ( iter.hasNext() ) {
 				Object next = iter.next();
 				if ( next instanceof String ) {
 					String token = ( String ) next;
 
 					if ( "(".equals( token ) ) {
 						parenCount++;
 					}
 					else if ( ")".equals( token ) ) {
 						parenCount--;
 					}
 
 					String lc = token.toLowerCase();
 					if ( lc.equals( ", " ) ) {
 						if ( nolast ) {
 							nolast = false;
 						}
 						else {
 							if ( !isSubselect && parenCount == 0 ) {
 								int x = c++;
 								buf.append( " as " )
 										.append( NameGenerator.scalarName( x, 0 ) );
 							}
 						}
 					}
 					buf.append( token );
 					if ( lc.equals( "distinct" ) || lc.equals( "all" ) ) {
 						buf.append( ' ' );
 					}
 				}
 				else {
 					nolast = true;
 					String[] tokens = ( String[] ) next;
 					for ( int i = 0; i < tokens.length; i++ ) {
 						buf.append( tokens[i] );
 						if ( !isSubselect ) {
 							buf.append( " as " )
 									.append( NameGenerator.scalarName( c, i ) );
 						}
 						if ( i != tokens.length - 1 ) buf.append( ", " );
 					}
 					c++;
 				}
 			}
 			if ( !isSubselect && !nolast ) {
 				int x = c++;
 				buf.append( " as " )
 						.append( NameGenerator.scalarName( x, 0 ) );
 			}
 
 		}
 
 		return buf.toString();
 	}
 
 	private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {
 
 		Iterator iter = joins.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			String name = ( String ) me.getKey();
 			JoinSequence join = ( JoinSequence ) me.getValue();
 			join.setSelector( new JoinSequence.Selector() {
 				public boolean includeSubclasses(String alias) {
 					boolean include = returnedTypes.contains( alias ) && !isShallowQuery();
 					return include;
 				}
 			} );
 
 			if ( typeMap.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else if ( collections.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else {
 				//name from a super query (a bit inelegant that it shows up here)
 			}
 
 		}
 
 	}
 
 	public final Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	/**
 	 * Is this query called by scroll() or iterate()?
 	 *
 	 * @return true if it is, false if it is called by find() or list()
 	 */
 	boolean isShallowQuery() {
 		return shallowQuery;
 	}
 
 	void addQuerySpaces(Serializable[] spaces) {
 		for ( int i = 0; i < spaces.length; i++ ) {
 			querySpaces.add( spaces[i] );
 		}
 		if ( superQuery != null ) superQuery.addQuerySpaces( spaces );
 	}
 
 	void setDistinct(boolean distinct) {
 		this.distinct = distinct;
 	}
 
 	boolean isSubquery() {
 		return superQuery != null;
 	}
 
 	/**
 	 * Overrides method from Loader
 	 */
 	@Override
     public CollectionPersister[] getCollectionPersisters() {
 		return collectionPersister == null ? null : new CollectionPersister[] { collectionPersister };
 	}
 
 	@Override
     protected String[] getCollectionSuffixes() {
 		return collectionPersister == null ? null : new String[] { "__" };
 	}
 
 	void setCollectionToFetch(String role, String name, String ownerName, String entityName)
 			throws QueryException {
 		fetchName = name;
 		collectionPersister = getCollectionPersister( role );
 		collectionOwnerName = ownerName;
 		if ( collectionPersister.getElementType().isEntityType() ) {
 			addEntityToFetch( entityName );
 		}
 	}
 
 	@Override
     protected String[] getSuffixes() {
 		return suffixes;
 	}
 
 	@Override
     protected String[] getAliases() {
 		return names;
 	}
 
 	/**
 	 * Used for collection filters
 	 */
 	private void addFromAssociation(final String elementName, final String collectionRole)
 			throws QueryException {
 		//q.addCollection(collectionName, collectionRole);
 		QueryableCollection persister = getCollectionPersister( collectionRole );
 		Type collectionElementType = persister.getElementType();
 		if ( !collectionElementType.isEntityType() ) {
 			throw new QueryException( "collection of values in filter: " + elementName );
 		}
 
 		String[] keyColumnNames = persister.getKeyColumnNames();
 		//if (keyColumnNames.length!=1) throw new QueryException("composite-key collection in filter: " + collectionRole);
 
 		String collectionName;
 		JoinSequence join = new JoinSequence( getFactory() );
 		collectionName = persister.isOneToMany() ?
 				elementName :
 				createNameForCollection( collectionRole );
 		join.setRoot( persister, collectionName );
 		if ( !persister.isOneToMany() ) {
 			//many-to-many
 			addCollection( collectionName, collectionRole );
 			try {
 				join.addJoin( ( AssociationType ) persister.getElementType(),
 						elementName,
 						JoinFragment.INNER_JOIN,
 						persister.getElementColumnNames(collectionName) );
 			}
 			catch ( MappingException me ) {
 				throw new QueryException( me );
 			}
 		}
 		join.addCondition( collectionName, keyColumnNames, " = ?" );
 		//if ( persister.hasWhere() ) join.addCondition( persister.getSQLWhereString(collectionName) );
 		EntityType elemType = ( EntityType ) collectionElementType;
 		addFrom( elementName, elemType.getAssociatedEntityName(), join );
 
 	}
 
 	String getPathAlias(String path) {
 		return ( String ) pathAliases.get( path );
 	}
 
 	JoinSequence getPathJoin(String path) {
 		return ( JoinSequence ) pathJoins.get( path );
 	}
 
 	void addPathAliasAndJoin(String path, String alias, JoinSequence joinSequence) {
 		pathAliases.put( path, alias );
 		pathJoins.put( path, joinSequence );
 	}
 
 	public List list(SessionImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
 		return list( session, queryParameters, getQuerySpaces(), actualReturnTypes );
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 
 		boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 
 			PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 			ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), false, queryParameters.getRowSelection(), session );
 			HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(holderConstructor, queryParameters.getResultTransformer());
 			Iterator result = new IteratorImpl( rs, st, session, queryParameters.isReadOnly( session ), returnTypes, getColumnNames(), hi );
 
 			if ( stats ) {
 				session.getFactory().getStatisticsImplementor().queryExecuted(
 						"HQL: " + queryString,
 						0,
 						System.currentTimeMillis() - startTime
 					);
 			}
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using iterate",
 					getSQLString()
 				);
 		}
 
 	}
 
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {
 		throw new UnsupportedOperationException( "Not supported!  Use the AST translator...");
 	}
 
 	@Override
     protected boolean[] includeInResultRow() {
 		boolean[] isResultReturned = includeInSelect;
 		if ( hasScalars ) {
 			isResultReturned = new boolean[ returnedTypes.size() ];
 			Arrays.fill( isResultReturned, true );
 		}
 		return isResultReturned;
 	}
 
 
 	@Override
     protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveClassicResultTransformer(
 				holderConstructor,
 				resultTransformer
 		);
 	}
 
 	@Override
     protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow = getResultRow( row, rs, session );
 		return ( holderClass == null && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
 	@Override
     protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = getColumnNames();
 			int queryCols = returnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
 	@Override
     protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		if ( holderClass != null ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				try {
 					results.set( i, holderConstructor.newInstance( row ) );
 				}
 				catch ( Exception e ) {
 					throw new QueryException( "could not instantiate: " + holderClass, e );
 				}
 			}
 		}
 		return results;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) result[j++] = row[i];
 			}
 			return result;
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
index 9ad39516a7..6f9392bfe6 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
@@ -1,958 +1,958 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.query.ParameterMetadata;
 import org.hibernate.hql.classic.ParserHelper;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.property.Getter;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Abstract implementation of the Query interface.
  *
  * @author Gavin King
  * @author Max Andersen
  */
 public abstract class AbstractQueryImpl implements Query {
 
 	private static final Object UNSET_PARAMETER = new MarkerObject("<unset parameter>");
 	private static final Object UNSET_TYPE = new MarkerObject("<unset type>");
 
 	private final String queryString;
 	protected final SessionImplementor session;
 	protected final ParameterMetadata parameterMetadata;
 
 	// parameter bind values...
 	private List values = new ArrayList(4);
 	private List types = new ArrayList(4);
 	private Map namedParameters = new HashMap(4);
 	private Map namedParameterLists = new HashMap(4);
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	private RowSelection selection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 	private FlushMode flushMode;
 	private CacheMode cacheMode;
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 	private Serializable collectionKey;
 	private Boolean readOnly;
 	private ResultTransformer resultTransformer;
 
 	public AbstractQueryImpl(
 			String queryString,
 	        FlushMode flushMode,
 	        SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		this.session = session;
 		this.queryString = queryString;
 		this.selection = new RowSelection();
 		this.flushMode = flushMode;
 		this.cacheMode = null;
 		this.parameterMetadata = parameterMetadata;
 	}
 
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + queryString + ')';
 	}
 
 	public final String getQueryString() {
 		return queryString;
 	}
 
 	//TODO: maybe call it getRowSelection() ?
 	public RowSelection getSelection() {
 		return selection;
 	}
 	
 	public Query setFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 	
 	public Query setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 		return this;
 	}
 
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	public Query setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	public Query setCacheRegion(String cacheRegion) {
 		if (cacheRegion != null)
 			this.cacheRegion = cacheRegion.trim();
 		return this;
 	}
 
 	public Query setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	public Query setFirstResult(int firstResult) {
 		selection.setFirstRow( new Integer(firstResult) );
 		return this;
 	}
 
 	public Query setMaxResults(int maxResults) {
 		if ( maxResults < 0 ) {
 			// treat negatives specically as meaning no limit...
 			selection.setMaxRows( null );
 		}
 		else {
 			selection.setMaxRows( new Integer(maxResults) );
 		}
 		return this;
 	}
 
 	public Query setTimeout(int timeout) {
 		selection.setTimeout( new Integer(timeout) );
 		return this;
 	}
 	public Query setFetchSize(int fetchSize) {
 		selection.setFetchSize( new Integer(fetchSize) );
 		return this;
 	}
 
 	public Type[] getReturnTypes() throws HibernateException {
 		return session.getFactory().getReturnTypes( queryString );
 	}
 
 	public String[] getReturnAliases() throws HibernateException {
 		return session.getFactory().getReturnAliases( queryString );
 	}
 
 	public Query setCollectionKey(Serializable collectionKey) {
 		this.collectionKey = collectionKey;
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				getSession().getPersistenceContext().isDefaultReadOnly() :
 				readOnly.booleanValue() 
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Query setReadOnly(boolean readOnly) {
 		this.readOnly = Boolean.valueOf( readOnly );
 		return this;
 	}
 
 	public Query setResultTransformer(ResultTransformer transformer) {
 		this.resultTransformer = transformer;
 		return this;
 	}
 	
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	SessionImplementor getSession() {
 		return session;
 	}
 
 	public abstract LockOptions getLockOptions();
 
 
 	// Parameter handling code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns a shallow copy of the named parameter value map.
 	 *
 	 * @return Shallow copy of the named parameter value map
 	 */
 	protected Map getNamedParams() {
 		return new HashMap( namedParameters );
 	}
 
 	/**
 	 * Returns an array representing all named parameter names encountered
 	 * during (intial) parsing of the query.
 	 * <p/>
 	 * Note <i>initial</i> here means different things depending on whether
 	 * this is a native-sql query or an HQL/filter query.  For native-sql, a
 	 * precursory inspection of the query string is performed specifically to
 	 * locate defined parameters.  For HQL/filter queries, this is the
 	 * information returned from the query-translator.  This distinction
 	 * holds true for all parameter metadata exposed here.
 	 *
 	 * @return Array of named parameter names.
 	 * @throws HibernateException
 	 */
 	public String[] getNamedParameters() throws HibernateException {
 		return ArrayHelper.toStringArray( parameterMetadata.getNamedParameterNames() );
 	}
 
 	/**
 	 * Does this query contain named parameters?
 	 *
 	 * @return True if the query was found to contain named parameters; false
 	 * otherwise;
 	 */
 	public boolean hasNamedParameters() {
 		return parameterMetadata.getNamedParameterNames().size() > 0;
 	}
 
 	/**
 	 * Retreive the value map for any named parameter lists (i.e., for
 	 * auto-expansion) bound to this query.
 	 *
 	 * @return The parameter list value map.
 	 */
 	protected Map getNamedParameterLists() {
 		return namedParameterLists;
 	}
 
 	/**
 	 * Retreives the list of parameter values bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter values.
 	 */
 	protected List getValues() {
 		return values;
 	}
 
 	/**
 	 * Retreives the list of parameter {@link Type type}s bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter types.
 	 */
 	protected List getTypes() {
 		return types;
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @throws QueryException
 	 */
 	protected void verifyParameters() throws QueryException {
 		verifyParameters(false);
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @param reserveFirstParameter if true, the first ? will not be verified since
 	 * its needed for e.g. callable statements returning a out parameter
 	 * @throws HibernateException
 	 */
 	protected void verifyParameters(boolean reserveFirstParameter) throws HibernateException {
 		if ( parameterMetadata.getNamedParameterNames().size() != namedParameters.size() + namedParameterLists.size() ) {
 			Set missingParams = new HashSet( parameterMetadata.getNamedParameterNames() );
 			missingParams.removeAll( namedParameterLists.keySet() );
 			missingParams.removeAll( namedParameters.keySet() );
 			throw new QueryException( "Not all named parameters have been set: " + missingParams, getQueryString() );
 		}
 
 		int positionalValueSpan = 0;
 		for ( int i = 0; i < values.size(); i++ ) {
 			Object object = types.get( i );
 			if( values.get( i ) == UNSET_PARAMETER || object == UNSET_TYPE ) {
 				if ( reserveFirstParameter && i==0 ) {
 					continue;
 				}
 				else {
 					throw new QueryException( "Unset positional parameter at position: " + i, getQueryString() );
 				}
 			}
 			positionalValueSpan += ( (Type) object ).getColumnSpan( session.getFactory() );
 		}
 
 		if ( parameterMetadata.getOrdinalParameterCount() != positionalValueSpan ) {
 			if ( reserveFirstParameter && parameterMetadata.getOrdinalParameterCount() - 1 != positionalValueSpan ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		(parameterMetadata.getOrdinalParameterCount()-1) +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 			else if ( !reserveFirstParameter ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		parameterMetadata.getOrdinalParameterCount() +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 		}
 	}
 
 	public Query setParameter(int position, Object val, Type type) {
 		if ( parameterMetadata.getOrdinalParameterCount() == 0 ) {
 			throw new IllegalArgumentException("No positional parameters in query: " + getQueryString() );
 		}
 		if ( position < 0 || position > parameterMetadata.getOrdinalParameterCount() - 1 ) {
 			throw new IllegalArgumentException("Positional parameter does not exist: " + position + " in query: " + getQueryString() );
 		}
 		int size = values.size();
 		if ( position < size ) {
 			values.set( position, val );
 			types.set( position, type );
 		}
 		else {
 			// prepend value and type list with null for any positions before the wanted position.
 			for ( int i = 0; i < position - size; i++ ) {
 				values.add( UNSET_PARAMETER );
 				types.add( UNSET_TYPE );
 			}
 			values.add( val );
 			types.add( type );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val, Type type) {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		else {
 			 namedParameters.put( name, new TypedValue( type, val, session.getEntityMode() ) );
 			 return this;
 		}
 	}
 
 	public Query setParameter(int position, Object val) throws HibernateException {
 		if (val == null) {
 			setParameter( position, val, StandardBasicTypes.SERIALIZABLE );
 		}
 		else {
 			setParameter( position, val, determineType( position, val ) );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val) throws HibernateException {
 		if (val == null) {
 			Type type = parameterMetadata.getNamedParameterExpectedType( name );
 			if ( type == null ) {
 				type = StandardBasicTypes.SERIALIZABLE;
 			}
 			setParameter( name, val, type );
 		}
 		else {
 			setParameter( name, val, determineType( name, val ) );
 		}
 		return this;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Class clazz) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( clazz );
 		}
 		return type;
 	}
 
 	private Type guessType(Object param) throws HibernateException {
 		Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy( param );
 		return guessType( clazz );
 	}
 
 	private Type guessType(Class clazz) throws HibernateException {
 		String typename = clazz.getName();
 		Type type = session.getFactory().getTypeResolver().heuristicType(typename);
 		boolean serializable = type!=null && type instanceof SerializableType;
 		if (type==null || serializable) {
 			try {
 				session.getFactory().getEntityPersister( clazz.getName() );
 			}
 			catch (MappingException me) {
 				if (serializable) {
 					return type;
 				}
 				else {
 					throw new HibernateException("Could not determine a type for class: " + typename);
 				}
 			}
 			return ( (Session) session ).getTypeHelper().entity( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	public Query setString(int position, String val) {
 		setParameter(position, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setCharacter(int position, char val) {
 		setParameter(position, new Character(val), StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setBoolean(int position, boolean val) {
 		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
 		Type typeToUse = determineType( position, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( position, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(int position, byte val) {
 		setParameter(position, new Byte(val), StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setShort(int position, short val) {
 		setParameter(position, new Short(val), StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setInteger(int position, int val) {
 		setParameter(position, new Integer(val), StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLong(int position, long val) {
 		setParameter(position, new Long(val), StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setFloat(int position, float val) {
 		setParameter(position, new Float(val), StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setDouble(int position, double val) {
 		setParameter(position, new Double(val), StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setBinary(int position, byte[] val) {
 		setParameter(position, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(int position, String val) {
 		setParameter(position, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setSerializable(int position, Serializable val) {
 		setParameter(position, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setDate(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setTime(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setEntity(int position, Object val) {
 		setParameter( position, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	private String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return session.bestGuessEntityName( val );
 	}
 
 	public Query setLocale(int position, Locale locale) {
 		setParameter(position, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(int position, Calendar calendar) {
 		setParameter(position, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(int position, Calendar calendar) {
 		setParameter(position, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setBinary(String name, byte[] val) {
 		setParameter(name, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(String name, String val) {
 		setParameter(name, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setBoolean(String name, boolean val) {
 		Boolean valueToUse = val ? Boolean.TRUE : Boolean.FALSE;
 		Type typeToUse = determineType( name, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( name, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(String name, byte val) {
 		setParameter(name, new Byte(val), StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setCharacter(String name, char val) {
 		setParameter(name, new Character(val), StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setDate(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setDouble(String name, double val) {
 		setParameter(name, new Double(val), StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setEntity(String name, Object val) {
 		setParameter( name, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	public Query setFloat(String name, float val) {
 		setParameter(name, new Float(val), StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setInteger(String name, int val) {
 		setParameter(name, new Integer(val), StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLocale(String name, Locale locale) {
 		setParameter(name, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(String name, Calendar calendar) {
 		setParameter(name, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(String name, Calendar calendar) {
 		setParameter(name, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setLong(String name, long val) {
 		setParameter(name, new Long(val), StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setSerializable(String name, Serializable val) {
 		setParameter(name, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setShort(String name, short val) {
 		setParameter(name, new Short(val), StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setString(String name, String val) {
 		setParameter(name, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setTime(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setBigDecimal(int position, BigDecimal number) {
 		setParameter(position, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigDecimal(String name, BigDecimal number) {
 		setParameter(name, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigInteger(int position, BigInteger number) {
 		setParameter(position, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setBigInteger(String name, BigInteger number) {
 		setParameter(name, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		namedParameterLists.put( name, new TypedValue( type, vals, session.getEntityMode() ) );
 		return this;
 	}
 	
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	protected String expandParameterLists(Map namedParamsCopy) {
 		String query = this.queryString;
 		Iterator iter = namedParameterLists.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			query = expandParameterList( query, (String) me.getKey(), (TypedValue) me.getValue(), namedParamsCopy );
 		}
 		return query;
 	}
 
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	private String expandParameterList(String query, String name, TypedValue typedList, Map namedParamsCopy) {
 		Collection vals = (Collection) typedList.getValue();
 		Type type = typedList.getType();
 
 		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
 		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
 		String placeholder =
 				new StringBuffer( paramPrefix.length() + name.length() )
 						.append( paramPrefix ).append(  name )
 						.toString();
 
 		if ( query == null ) {
 			return query;
 		}
 		int loc = query.indexOf( placeholder );
 
 		if ( loc < 0 ) {
 			return query;
 		}
 
 		String beforePlaceholder = query.substring( 0, loc );
 		String afterPlaceholder =  query.substring( loc + placeholder.length() );
 
 		// check if placeholder is already immediately enclosed in parentheses
 		// (ignoring whitespace)
 		boolean isEnclosedInParens =
 				StringHelper.getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' &&
 				StringHelper.getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')';
 
 		if ( vals.size() == 1  && isEnclosedInParens ) {
 			// short-circuit for performance when only 1 value and the
 			// placeholder is already enclosed in parentheses...
 			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next(), session.getEntityMode() ) );
 			return query;
 		}
 
 		StringBuffer list = new StringBuffer( 16 );
 		Iterator iter = vals.iterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			String alias = ( isJpaPositionalParam ? 'x' + name : name ) + i++ + '_';
 			namedParamsCopy.put( alias, new TypedValue( type, iter.next(), session.getEntityMode() ) );
 			list.append( ParserHelper.HQL_VARIABLE_PREFIX ).append( alias );
 			if ( iter.hasNext() ) {
 				list.append( ", " );
 			}
 		}
 		return StringHelper.replace(
 				beforePlaceholder,
 				afterPlaceholder,
 				placeholder.toString(),
 				list.toString(),
 				true,
 				true
 		);
 	}
 
 	public Query setParameterList(String name, Collection vals) throws HibernateException {
 		if ( vals == null ) {
 			throw new QueryException( "Collection must be not null!" );
 		}
 
 		if( vals.size() == 0 ) {
 			setParameterList( name, vals, null );
 		}
 		else {
 			setParameterList(name, vals, determineType( name, vals.iterator().next() ) );
 		}
 
 		return this;
 	}
 
 	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals), type );
 	}
 
 	public Query setParameterList(String name, Object[] vals) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals) );
 	}
 
 	public Query setProperties(Map map) throws HibernateException {
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 				final Object object = map.get(namedParam);
 				if(object==null) {
 					continue;
 				}
 				Class retType = object.getClass();
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 
 			
 		}
 		return this;				
 	}
 	
 	public Query setProperties(Object bean) throws HibernateException {
 		Class clazz = bean.getClass();
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 			try {
 				Getter getter = ReflectHelper.getGetter( clazz, namedParam );
 				Class retType = getter.getReturnType();
 				final Object object = getter.get( bean );
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 				 	setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 			}
 			catch (PropertyNotFoundException pnfe) {
 				// ignore
 			}
 		}
 		return this;
 	}
 
 	public Query setParameters(Object[] values, Type[] types) {
 		this.values = Arrays.asList(values);
 		this.types = Arrays.asList(types);
 		return this;
 	}
 
 
 	// Execution methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object uniqueResult() throws HibernateException {
 		return uniqueElement( list() );
 	}
 
 	static Object uniqueElement(List list) throws NonUniqueResultException {
 		int size = list.size();
 		if (size==0) return null;
 		Object first = list.get(0);
 		for ( int i=1; i<size; i++ ) {
 			if ( list.get(i)!=first ) {
 				throw new NonUniqueResultException( list.size() );
 			}
 		}
 		return first;
 	}
 
 	protected RowSelection getRowSelection() {
 		return selection;
 	}
 
 	public Type[] typeArray() {
 		return ArrayHelper.toTypeArray( getTypes() );
 	}
 	
 	public Object[] valueArray() {
 		return getValues().toArray();
 	}
 
 	public QueryParameters getQueryParameters(Map namedParams) {
 		return new QueryParameters(
 				typeArray(),
 				valueArray(),
 				namedParams,
 				getLockOptions(),
 				getSelection(),
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
 				collectionKey == null ? null : new Serializable[] { collectionKey },
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 	}
 	
 	protected void before() {
 		if ( flushMode!=null ) {
 			sessionFlushMode = getSession().getFlushMode();
 			getSession().setFlushMode(flushMode);
 		}
 		if ( cacheMode!=null ) {
 			sessionCacheMode = getSession().getCacheMode();
 			getSession().setCacheMode(cacheMode);
 		}
 	}
 	
 	protected void after() {
 		if (sessionFlushMode!=null) {
 			getSession().setFlushMode(sessionFlushMode);
 			sessionFlushMode = null;
 		}
 		if (sessionCacheMode!=null) {
 			getSession().setCacheMode(sessionCacheMode);
 			sessionCacheMode = null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
rename to hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
index 80cf6b4668..ddd84b2495 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
@@ -1,290 +1,289 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.hql.HolderInstantiator;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.Loader;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the <tt>ScrollableResults</tt> interface
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractScrollableResults implements ScrollableResults {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractScrollableResults.class.getName());
 
 	private final ResultSet resultSet;
 	private final PreparedStatement ps;
 	private final SessionImplementor session;
 	private final Loader loader;
 	private final QueryParameters queryParameters;
 	private final Type[] types;
 	private HolderInstantiator holderInstantiator;
 
 	public AbstractScrollableResults(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        SessionImplementor sess,
 			Loader loader,
 			QueryParameters queryParameters,
 	        Type[] types,
 	        HolderInstantiator holderInstantiator) throws MappingException {
 		this.resultSet=rs;
 		this.ps=ps;
 		this.session = sess;
 		this.loader = loader;
 		this.queryParameters = queryParameters;
 		this.types = types;
 		this.holderInstantiator = holderInstantiator!=null && holderInstantiator.isRequired()
 		        ? holderInstantiator
 		        : null;
 	}
 
 	protected abstract Object[] getCurrentRow();
 
 	protected ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	protected PreparedStatement getPs() {
 		return ps;
 	}
 
 	protected SessionImplementor getSession() {
 		return session;
 	}
 
 	protected Loader getLoader() {
 		return loader;
 	}
 
 	protected QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	protected Type[] getTypes() {
 		return types;
 	}
 
 	protected HolderInstantiator getHolderInstantiator() {
 		return holderInstantiator;
 	}
 
 	public final void close() throws HibernateException {
 		try {
 			// not absolutely necessary, but does help with aggressive release
 			//session.getJDBCContext().getConnectionManager().closeQueryStatement( ps, resultSet );
 			ps.close();
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not close results"
 				);
 		}
 		finally {
 			try {
 				session.getPersistenceContext().getLoadContexts().cleanup( resultSet );
 			}
 			catch( Throwable ignore ) {
 				// ignore this error for now
                 LOG.trace("Exception trying to cleanup load context : " + ignore.getMessage());
 			}
 		}
 	}
 
 	public final Object[] get() throws HibernateException {
 		return getCurrentRow();
 	}
 
 	public final Object get(int col) throws HibernateException {
 		return getCurrentRow()[col];
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are identical.
 	 *
 	 * @param col the column
 	 * @param returnType a "final" type
 	 */
 	protected final Object getFinal(int col, Type returnType) throws HibernateException {
 		if ( holderInstantiator!=null ) {
 			throw new HibernateException("query specifies a holder class");
 		}
 
 		if ( returnType.getReturnedClass()==types[col].getReturnedClass() ) {
 			return get(col);
 		}
 		else {
 			return throwInvalidColumnTypeException(col, types[col], returnType);
 		}
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are "assignable".
 	 *
 	 * @param col the column
 	 * @param returnType any type
 	 */
 	protected final Object getNonFinal(int col, Type returnType) throws HibernateException {
 		if ( holderInstantiator!=null ) {
 			throw new HibernateException("query specifies a holder class");
 		}
 
 		if ( returnType.getReturnedClass().isAssignableFrom( types[col].getReturnedClass() ) ) {
 			return get(col);
 		}
 		else {
 			return throwInvalidColumnTypeException(col, types[col], returnType);
 		}
 	}
 
 	public final BigDecimal getBigDecimal(int col) throws HibernateException {
 		return (BigDecimal) getFinal(col, StandardBasicTypes.BIG_DECIMAL);
 	}
 
 	public final BigInteger getBigInteger(int col) throws HibernateException {
 		return (BigInteger) getFinal(col, StandardBasicTypes.BIG_INTEGER);
 	}
 
 	public final byte[] getBinary(int col) throws HibernateException {
 		return (byte[]) getFinal(col, StandardBasicTypes.BINARY);
 	}
 
 	public final String getText(int col) throws HibernateException {
 		return (String) getFinal(col, StandardBasicTypes.TEXT);
 	}
 
 	public final Blob getBlob(int col) throws HibernateException {
 		return (Blob) getNonFinal(col, StandardBasicTypes.BLOB);
 	}
 
 	public final Clob getClob(int col) throws HibernateException {
 		return (Clob) getNonFinal(col, StandardBasicTypes.CLOB);
 	}
 
 	public final Boolean getBoolean(int col) throws HibernateException {
 		return (Boolean) getFinal(col, StandardBasicTypes.BOOLEAN);
 	}
 
 	public final Byte getByte(int col) throws HibernateException {
 		return (Byte) getFinal(col, StandardBasicTypes.BYTE);
 	}
 
 	public final Character getCharacter(int col) throws HibernateException {
 		return (Character) getFinal(col, StandardBasicTypes.CHARACTER);
 	}
 
 	public final Date getDate(int col) throws HibernateException {
 		return (Date) getNonFinal(col, StandardBasicTypes.TIMESTAMP);
 	}
 
 	public final Calendar getCalendar(int col) throws HibernateException {
 		return (Calendar) getNonFinal(col, StandardBasicTypes.CALENDAR);
 	}
 
 	public final Double getDouble(int col) throws HibernateException {
 		return (Double) getFinal(col, StandardBasicTypes.DOUBLE);
 	}
 
 	public final Float getFloat(int col) throws HibernateException {
 		return (Float) getFinal(col, StandardBasicTypes.FLOAT);
 	}
 
 	public final Integer getInteger(int col) throws HibernateException {
 		return (Integer) getFinal(col, StandardBasicTypes.INTEGER);
 	}
 
 	public final Long getLong(int col) throws HibernateException {
 		return (Long) getFinal(col, StandardBasicTypes.LONG);
 	}
 
 	public final Short getShort(int col) throws HibernateException {
 		return (Short) getFinal(col, StandardBasicTypes.SHORT);
 	}
 
 	public final String getString(int col) throws HibernateException {
 		return (String) getFinal(col, StandardBasicTypes.STRING);
 	}
 
 	public final Locale getLocale(int col) throws HibernateException {
 		return (Locale) getFinal(col, StandardBasicTypes.LOCALE);
 	}
 
 	/*public final Currency getCurrency(int col) throws HibernateException {
 		return (Currency) get(col);
 	}*/
 
 	public final TimeZone getTimeZone(int col) throws HibernateException {
 		return (TimeZone) getNonFinal(col, StandardBasicTypes.TIMEZONE);
 	}
 
 	public final Type getType(int i) {
 		return types[i];
 	}
 
 	private Object throwInvalidColumnTypeException(
 	        int i,
 	        Type type,
 	        Type returnType) throws HibernateException {
 		throw new HibernateException(
 				"incompatible column types: " +
 				type.getName() +
 				", " +
 				returnType.getName()
 		);
 	}
 
 	protected void afterScrollOperation() {
 		session.afterScrollOperation();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index fb8b9d2e1c..1cb31ff28f 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,307 +1,307 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract, SessionImplementor, TransactionContext {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private boolean closed = false;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory, String tenantIdentifier) {
 		this.factory = factory;
 		this.tenantIdentifier = tenantIdentifier;
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			if ( tenantIdentifier != null ) {
 				throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 			}
 		}
 		else {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "SessionFactory configured for multi-tenancy, but no tenant identifier specified" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return factory.getTransactionEnvironment();
 	}
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getTransactionCoordinator().getJdbcCoordinator().coordinateWork(
 				new WorkExecutorVisitable<T>() {
 					@Override
 					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 						try {
 							return callback.executeOnConnection( connection );
 						}
 						catch (SQLException e) {
 							throw getFactory().getSQLExceptionHelper().convert(
 									e,
 									"Error creating contextual LOB : " + e.getMessage()
 							);
 						}
 					}
 				}
 		);
 	}
 
 	@Override
 	public boolean isClosed() {
 		return closed;
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( closed ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
 	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			String queryString = nqd.getQueryString();
 			query = new QueryImpl(
 					queryString,
 			        nqd.getFlushMode(),
 			        this,
 			        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 			);
 			query.setComment( "named HQL query " + queryName );
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 			        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 			);
 			query.setComment( "named native SQL query " + queryName );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 		if ( nsqlqd==null ) {
 			throw new MappingException( "Named SQL query not known: " + queryName );
 		}
 		Query query = new SQLQueryImpl(
 				nsqlqd,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 		);
 		query.setComment( "named native SQL query " + queryName );
 		initQuery( query, nsqlqd );
 		return query;
 	}
 
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		if ( nqd.getTimeout()!=null ) query.setTimeout( nqd.getTimeout().intValue() );
 		if ( nqd.getFetchSize()!=null ) query.setFetchSize( nqd.getFetchSize().intValue() );
 		if ( nqd.getCacheMode() != null ) query.setCacheMode( nqd.getCacheMode() );
 		query.setReadOnly( nqd.isReadOnly() );
 		if ( nqd.getComment() != null ) query.setComment( nqd.getComment() );
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		QueryImpl query = new QueryImpl(
 				queryString,
 		        this,
 		        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister, getEntityMode(), getTenantIdentifier() );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return new CacheKey( id, type, entityOrRoleName, getEntityMode(), getTenantIdentifier(), getFactory() );
 	}
 
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			return connectionProvider.getConnection( tenantIdentifier );
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			connectionProvider.releaseConnection( tenantIdentifier, connection );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/CollectionFilterImpl.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/CollectionFilterImpl.java
index 70479167c1..c452cf7414 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/CollectionFilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CollectionFilterImpl.java
@@ -1,102 +1,102 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.HibernateException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.query.ParameterMetadata;
 import org.hibernate.type.Type;
 
 /**
  * implementation of the <tt>Query</tt> interface for collection filters
  * @author Gavin King
  */
 public class CollectionFilterImpl extends QueryImpl {
 
 	private Object collection;
 
 	public CollectionFilterImpl(
 			String queryString,
 	        Object collection,
 	        SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		super( queryString, session, parameterMetadata );
 		this.collection = collection;
 	}
 
 
 	/**
 	 * @see org.hibernate.Query#iterate()
 	 */
 	public Iterator iterate() throws HibernateException {
 		verifyParameters();
 		Map namedParams = getNamedParams();
 		return getSession().iterateFilter( 
 				collection, 
 				expandParameterLists(namedParams),
 				getQueryParameters(namedParams) 
 		);
 	}
 
 	/**
 	 * @see org.hibernate.Query#list()
 	 */
 	public List list() throws HibernateException {
 		verifyParameters();
 		Map namedParams = getNamedParams();
 		return getSession().listFilter( 
 				collection, 
 				expandParameterLists(namedParams),
 				getQueryParameters(namedParams) 
 		);
 	}
 
 	/**
 	 * @see org.hibernate.Query#scroll()
 	 */
 	public ScrollableResults scroll() throws HibernateException {
 		throw new UnsupportedOperationException("Can't scroll filters");
 	}
 
 	public Type[] typeArray() {
 		List typeList = getTypes();
 		int size = typeList.size();
 		Type[] result = new Type[size+1];
 		for (int i=0; i<size; i++) result[i+1] = (Type) typeList.get(i);
 		return result;
 	}
 
 	public Object[] valueArray() {
 		List valueList = getValues();
 		int size = valueList.size();
 		Object[] result = new Object[size+1];
 		for (int i=0; i<size; i++) result[i+1] = valueList.get(i);
 		return result;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/ConnectionObserverStatsBridge.java b/hibernate-core/src/main/java/org/hibernate/internal/ConnectionObserverStatsBridge.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/ConnectionObserverStatsBridge.java
rename to hibernate-core/src/main/java/org/hibernate/internal/ConnectionObserverStatsBridge.java
index cbfb318576..c8c54f0884 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/ConnectionObserverStatsBridge.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/ConnectionObserverStatsBridge.java
@@ -1,59 +1,59 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.ConnectionObserver;
 
 import java.io.Serializable;
 import java.sql.Connection;
 
 /**
  * @author Steve Ebersole
  */
 public class ConnectionObserverStatsBridge implements ConnectionObserver, Serializable {
 	private final SessionFactoryImplementor sessionFactory;
 
 	public ConnectionObserverStatsBridge(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	@Override
 	public void physicalConnectionObtained(Connection connection) {
 		sessionFactory.getStatisticsImplementor().connect();
 	}
 
 	@Override
 	public void physicalConnectionReleased() {
 	}
 
 	@Override
 	public void logicalConnectionClosed() {
 	}
 
 	@Override
 	public void statementPrepared() {
 		sessionFactory.getStatisticsImplementor().prepareStatement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
index bcb292408e..ae70bbf700 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/CriteriaImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
@@ -1,667 +1,667 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.NaturalIdentifier;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Implementation of the <tt>Criteria</tt> interface
  * @author Gavin King
  */
 public class CriteriaImpl implements Criteria, Serializable {
 
 	private final String entityOrClassName;
 	private transient SessionImplementor session;
 	private final String rootAlias;
 
 	private List criterionEntries = new ArrayList();
 	private List orderEntries = new ArrayList();
 	private Projection projection;
 	private Criteria projectionCriteria;
 
 	private List subcriteriaList = new ArrayList();
 
 	private Map fetchModes = new HashMap();
 	private Map lockModes = new HashMap();
 
 	private Integer maxResults;
 	private Integer firstResult;
 	private Integer timeout;
 	private Integer fetchSize;
 
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 
 	private FlushMode flushMode;
 	private CacheMode cacheMode;
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 
 	private Boolean readOnly;
 
 	private ResultTransformer resultTransformer = Criteria.ROOT_ENTITY;
 
 
 	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public CriteriaImpl(String entityOrClassName, SessionImplementor session) {
 		this(entityOrClassName, ROOT_ALIAS, session);
 	}
 
 	public CriteriaImpl(String entityOrClassName, String alias, SessionImplementor session) {
 		this.session = session;
 		this.entityOrClassName = entityOrClassName;
 		this.cacheable = false;
 		this.rootAlias = alias;
 	}
 
 	public String toString() {
 		return "CriteriaImpl(" +
 			entityOrClassName + ":" +
 			(rootAlias==null ? "" : rootAlias) +
 			subcriteriaList.toString() +
 			criterionEntries.toString() +
 			( projection==null ? "" : projection.toString() ) +
 			')';
 	}
 
 
 	// State ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	public void setSession(SessionImplementor session) {
 		this.session = session;
 	}
 
 	public String getEntityOrClassName() {
 		return entityOrClassName;
 	}
 
 	public Map getLockModes() {
 		return lockModes;
 	}
 
 	public Criteria getProjectionCriteria() {
 		return projectionCriteria;
 	}
 
 	public Iterator iterateSubcriteria() {
 		return subcriteriaList.iterator();
 	}
 
 	public Iterator iterateExpressionEntries() {
 		return criterionEntries.iterator();
 	}
 
 	public Iterator iterateOrderings() {
 		return orderEntries.iterator();
 	}
 
 	public Criteria add(Criteria criteriaInst, Criterion expression) {
 		criterionEntries.add( new CriterionEntry(expression, criteriaInst) );
 		return this;
 	}
 
 
 	// Criteria impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getAlias() {
 		return rootAlias;
 	}
 
 	public Projection getProjection() {
 		return projection;
 	}
 
 	public Criteria setProjection(Projection projection) {
 		this.projection = projection;
 		this.projectionCriteria = this;
 		setResultTransformer( PROJECTION );
 		return this;
 	}
 
 	public Criteria add(Criterion expression) {
 		add( this, expression );
 		return this;
 	}
 
 	public Criteria addOrder(Order ordering) {
 		orderEntries.add( new OrderEntry( ordering, this ) );
 		return this;
 	}
 
 	public FetchMode getFetchMode(String path) {
 		return (FetchMode) fetchModes.get(path);
 	}
 
 	public Criteria setFetchMode(String associationPath, FetchMode mode) {
 		fetchModes.put( associationPath, mode );
 		return this;
 	}
 
 	public Criteria setLockMode(LockMode lockMode) {
 		return setLockMode( getAlias(), lockMode );
 	}
 
 	public Criteria setLockMode(String alias, LockMode lockMode) {
 		lockModes.put( alias, lockMode );
 		return this;
 	}
 
 	public Criteria createAlias(String associationPath, String alias) {
 		return createAlias( associationPath, alias, INNER_JOIN );
 	}
 
 	public Criteria createAlias(String associationPath, String alias, int joinType) {
 		new Subcriteria( this, associationPath, alias, joinType );
 		return this;
 	}
 
 	public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) {
 		new Subcriteria( this, associationPath, alias, joinType, withClause );
 		return this;
 	}
 
 	public Criteria createCriteria(String associationPath) {
 		return createCriteria( associationPath, INNER_JOIN );
 	}
 
 	public Criteria createCriteria(String associationPath, int joinType) {
 		return new Subcriteria( this, associationPath, joinType );
 	}
 
 	public Criteria createCriteria(String associationPath, String alias) {
 		return createCriteria( associationPath, alias, INNER_JOIN );
 	}
 
 	public Criteria createCriteria(String associationPath, String alias, int joinType) {
 		return new Subcriteria( this, associationPath, alias, joinType );
 	}
 
 	public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) {
 		return new Subcriteria( this, associationPath, alias, joinType, withClause );
 	}
 
 	public ResultTransformer getResultTransformer() {
 		return resultTransformer;
 	}
 
 	public Criteria setResultTransformer(ResultTransformer tupleMapper) {
 		this.resultTransformer = tupleMapper;
 		return this;
 	}
 
 	public Integer getMaxResults() {
 		return maxResults;
 	}
 
 	public Criteria setMaxResults(int maxResults) {
 		this.maxResults = new Integer(maxResults);
 		return this;
 	}
 
 	public Integer getFirstResult() {
 		return firstResult;
 	}
 
 	public Criteria setFirstResult(int firstResult) {
 		this.firstResult = new Integer(firstResult);
 		return this;
 	}
 
 	public Integer getFetchSize() {
 		return fetchSize;
 	}
 
 	public Criteria setFetchSize(int fetchSize) {
 		this.fetchSize = new Integer(fetchSize);
 		return this;
 	}
 
 	public Integer getTimeout() {
 		return timeout;
 	}
 
 	public Criteria setTimeout(int timeout) {
 		this.timeout = new Integer(timeout);
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isReadOnlyInitialized() {
 		return readOnly != null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isReadOnly() {
 		if ( ! isReadOnlyInitialized() && getSession() == null ) {
 			throw new IllegalStateException(
 					"cannot determine readOnly/modifiable setting when it is not initialized and is not initialized and getSession() == null"
 			);
 		}
 		return ( isReadOnlyInitialized() ?
 				readOnly.booleanValue() :
 				getSession().getPersistenceContext().isDefaultReadOnly()
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Criteria setReadOnly(boolean readOnly) {
 		this.readOnly = Boolean.valueOf( readOnly );
 		return this;
 	}
 
 	public boolean getCacheable() {
 		return this.cacheable;
 	}
 
 	public Criteria setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	public String getCacheRegion() {
 		return this.cacheRegion;
 	}
 
 	public Criteria setCacheRegion(String cacheRegion) {
 		this.cacheRegion = cacheRegion.trim();
 		return this;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public Criteria setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	public Criteria setFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 
 	public Criteria setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 		return this;
 	}
 
 	public List list() throws HibernateException {
 		before();
 		try {
 			return session.list( this );
 		}
 		finally {
 			after();
 		}
 	}
 
 	public ScrollableResults scroll() {
 		return scroll( ScrollMode.SCROLL_INSENSITIVE );
 	}
 
 	public ScrollableResults scroll(ScrollMode scrollMode) {
 		before();
 		try {
 			return session.scroll(this, scrollMode);
 		}
 		finally {
 			after();
 		}
 	}
 
 	public Object uniqueResult() throws HibernateException {
 		return AbstractQueryImpl.uniqueElement( list() );
 	}
 
 	protected void before() {
 		if ( flushMode != null ) {
 			sessionFlushMode = getSession().getFlushMode();
 			getSession().setFlushMode( flushMode );
 		}
 		if ( cacheMode != null ) {
 			sessionCacheMode = getSession().getCacheMode();
 			getSession().setCacheMode( cacheMode );
 		}
 	}
 
 	protected void after() {
 		if ( sessionFlushMode != null ) {
 			getSession().setFlushMode( sessionFlushMode );
 			sessionFlushMode = null;
 		}
 		if ( sessionCacheMode != null ) {
 			getSession().setCacheMode( sessionCacheMode );
 			sessionCacheMode = null;
 		}
 	}
 
 	public boolean isLookupByNaturalKey() {
 		if ( projection != null ) {
 			return false;
 		}
 		if ( subcriteriaList.size() > 0 ) {
 			return false;
 		}
 		if ( criterionEntries.size() != 1 ) {
 			return false;
 		}
 		CriterionEntry ce = (CriterionEntry) criterionEntries.get(0);
 		return ce.getCriterion() instanceof NaturalIdentifier;
 	}
 
 
 	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final class Subcriteria implements Criteria, Serializable {
 
 		private String alias;
 		private String path;
 		private Criteria parent;
 		private LockMode lockMode;
 		private int joinType;
 		private Criterion withClause;
 
 		// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		private Subcriteria(Criteria parent, String path, String alias, int joinType, Criterion withClause) {
 			this.alias = alias;
 			this.path = path;
 			this.parent = parent;
 			this.joinType = joinType;
 			this.withClause = withClause;
 			CriteriaImpl.this.subcriteriaList.add( this );
 		}
 
 		private Subcriteria(Criteria parent, String path, String alias, int joinType) {
 			this( parent, path, alias, joinType, null );
 		}
 
 		private Subcriteria(Criteria parent, String path, int joinType) {
 			this( parent, path, null, joinType );
 		}
 
 		public String toString() {
 			return "Subcriteria("
 					+ path + ":"
 					+ (alias==null ? "" : alias)
 					+ ')';
 		}
 
 
 		// State ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		public String getAlias() {
 			return alias;
 		}
 
 		public void setAlias(String alias) {
 			this.alias = alias;
 		}
 
 		public String getPath() {
 			return path;
 		}
 
 		public Criteria getParent() {
 			return parent;
 		}
 
 		public LockMode getLockMode() {
 			return lockMode;
 		}
 
 		public Criteria setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public int getJoinType() {
 			return joinType;
 		}
 
 		public Criterion getWithClause() {
 			return this.withClause;
 		}
 
 
 		// Criteria impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		public Criteria add(Criterion expression) {
 			CriteriaImpl.this.add(this, expression);
 			return this;
 		}
 
 		public Criteria addOrder(Order order) {
 			CriteriaImpl.this.orderEntries.add( new OrderEntry(order, this) );
 			return this;
 		}
 
 		public Criteria createAlias(String associationPath, String alias) {
 			return createAlias( associationPath, alias, INNER_JOIN );
 		}
 
 		public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
 			new Subcriteria( this, associationPath, alias, joinType );
 			return this;
 		}
 
 		public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
 			new Subcriteria( this, associationPath, alias, joinType, withClause );
 			return this;
 		}
 
 		public Criteria createCriteria(String associationPath) {
 			return createCriteria( associationPath, INNER_JOIN );
 		}
 
 		public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
 			return new Subcriteria( Subcriteria.this, associationPath, joinType );
 		}
 
 		public Criteria createCriteria(String associationPath, String alias) {
 			return createCriteria( associationPath, alias, INNER_JOIN );
 		}
 
 		public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
 			return new Subcriteria( Subcriteria.this, associationPath, alias, joinType );
 		}
 
 		public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException {
 			return new Subcriteria( this, associationPath, alias, joinType, withClause );
 		}
 
 		public boolean isReadOnly() {
 			return CriteriaImpl.this.isReadOnly();
 		}
 
 		public boolean isReadOnlyInitialized() {
 			return CriteriaImpl.this.isReadOnlyInitialized();
 		}
 
 		public Criteria setReadOnly(boolean readOnly) {
 			CriteriaImpl.this.setReadOnly( readOnly );
 			return this;
 		}
 
 		public Criteria setCacheable(boolean cacheable) {
 			CriteriaImpl.this.setCacheable(cacheable);
 			return this;
 		}
 
 		public Criteria setCacheRegion(String cacheRegion) {
 			CriteriaImpl.this.setCacheRegion(cacheRegion);
 			return this;
 		}
 
 		public List list() throws HibernateException {
 			return CriteriaImpl.this.list();
 		}
 
 		public ScrollableResults scroll() throws HibernateException {
 			return CriteriaImpl.this.scroll();
 		}
 
 		public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
 			return CriteriaImpl.this.scroll(scrollMode);
 		}
 
 		public Object uniqueResult() throws HibernateException {
 			return CriteriaImpl.this.uniqueResult();
 		}
 
 		public Criteria setFetchMode(String associationPath, FetchMode mode) {
 			CriteriaImpl.this.setFetchMode( StringHelper.qualify(path, associationPath), mode);
 			return this;
 		}
 
 		public Criteria setFlushMode(FlushMode flushMode) {
 			CriteriaImpl.this.setFlushMode(flushMode);
 			return this;
 		}
 
 		public Criteria setCacheMode(CacheMode cacheMode) {
 			CriteriaImpl.this.setCacheMode(cacheMode);
 			return this;
 		}
 
 		public Criteria setFirstResult(int firstResult) {
 			CriteriaImpl.this.setFirstResult(firstResult);
 			return this;
 		}
 
 		public Criteria setMaxResults(int maxResults) {
 			CriteriaImpl.this.setMaxResults(maxResults);
 			return this;
 		}
 
 		public Criteria setTimeout(int timeout) {
 			CriteriaImpl.this.setTimeout(timeout);
 			return this;
 		}
 
 		public Criteria setFetchSize(int fetchSize) {
 			CriteriaImpl.this.setFetchSize(fetchSize);
 			return this;
 		}
 
 		public Criteria setLockMode(String alias, LockMode lockMode) {
 			CriteriaImpl.this.setLockMode(alias, lockMode);
 			return this;
 		}
 
 		public Criteria setResultTransformer(ResultTransformer resultProcessor) {
 			CriteriaImpl.this.setResultTransformer(resultProcessor);
 			return this;
 		}
 
 		public Criteria setComment(String comment) {
 			CriteriaImpl.this.setComment(comment);
 			return this;
 		}
 
 		public Criteria setProjection(Projection projection) {
 			CriteriaImpl.this.projection = projection;
 			CriteriaImpl.this.projectionCriteria = this;
 			setResultTransformer(PROJECTION);
 			return this;
 		}
 	}
 
 	public static final class CriterionEntry implements Serializable {
 		private final Criterion criterion;
 		private final Criteria criteria;
 
 		private CriterionEntry(Criterion criterion, Criteria criteria) {
 			this.criteria = criteria;
 			this.criterion = criterion;
 		}
 
 		public Criterion getCriterion() {
 			return criterion;
 		}
 
 		public Criteria getCriteria() {
 			return criteria;
 		}
 
 		public String toString() {
 			return criterion.toString();
 		}
 	}
 
 	public static final class OrderEntry implements Serializable {
 		private final Order order;
 		private final Criteria criteria;
 
 		private OrderEntry(Order order, Criteria criteria) {
 			this.criteria = criteria;
 			this.order = order;
 		}
 
 		public Order getOrder() {
 			return order;
 		}
 
 		public Criteria getCriteria() {
 			return criteria;
 		}
 
 		public String toString() {
 			return order.toString();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
index d1b5a4bcf7..de19fa6019 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/FetchingScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FetchingScrollableResultsImpl.java
@@ -1,332 +1,332 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.loader.Loader;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of ScrollableResults which can handle collection fetches.
  *
  * @author Steve Ebersole
  */
 public class FetchingScrollableResultsImpl extends AbstractScrollableResults {
 
 	public FetchingScrollableResultsImpl(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        SessionImplementor sess,
 	        Loader loader,
 	        QueryParameters queryParameters,
 	        Type[] types,
 	        HolderInstantiator holderInstantiator) throws MappingException {
 		super( rs, ps, sess, loader, queryParameters, types, holderInstantiator );
 	}
 
 	private Object[] currentRow = null;
 	private int currentPosition = 0;
 	private Integer maxPosition = null;
 
 	@Override
     protected Object[] getCurrentRow() {
 		return currentRow;
 	}
 
 	/**
 	 * Advance to the next result
 	 *
 	 * @return <tt>true</tt> if there is another result
 	 */
 	public boolean next() throws HibernateException {
 		if ( maxPosition != null && maxPosition.intValue() <= currentPosition ) {
 			currentRow = null;
 			currentPosition = maxPosition.intValue() + 1;
 			return false;
 		}
 
 		if ( isResultSetEmpty() ) {
 			currentRow = null;
 			currentPosition = 0;
 			return false;
 		}
 
 		Object row = getLoader().loadSequentialRowsForward(
 				getResultSet(),
 				getSession(),
 				getQueryParameters(),
 				false
 		);
 
 
 		boolean afterLast;
 		try {
 			afterLast = getResultSet().isAfterLast();
 		}
 		catch( SQLException e ) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "exception calling isAfterLast()"
 				);
 		}
 
 		currentPosition++;
 		currentRow = new Object[] { row };
 
 		if ( afterLast ) {
 			if ( maxPosition == null ) {
 				// we just hit the last position
 				maxPosition = new Integer( currentPosition );
 			}
 		}
 
 		afterScrollOperation();
 
 		return true;
 	}
 
 	/**
 	 * Retreat to the previous result
 	 *
 	 * @return <tt>true</tt> if there is a previous result
 	 */
 	public boolean previous() throws HibernateException {
 		if ( currentPosition <= 1 ) {
 			currentPosition = 0;
 			currentRow = null;
 			return false;
 		}
 
 		Object loadResult = getLoader().loadSequentialRowsReverse(
 				getResultSet(),
 				getSession(),
 				getQueryParameters(),
 				false,
 		        ( maxPosition != null && currentPosition > maxPosition.intValue() )
 		);
 
 		currentRow = new Object[] { loadResult };
 		currentPosition--;
 
 		afterScrollOperation();
 
 		return true;
 
 	}
 
 	/**
 	 * Scroll an arbitrary number of locations
 	 *
 	 * @param positions a positive (forward) or negative (backward) number of rows
 	 *
 	 * @return <tt>true</tt> if there is a result at the new location
 	 */
 	public boolean scroll(int positions) throws HibernateException {
 		boolean more = false;
 		if ( positions > 0 ) {
 			// scroll ahead
 			for ( int i = 0; i < positions; i++ ) {
 				more = next();
 				if ( !more ) {
 					break;
 				}
 			}
 		}
 		else if ( positions < 0 ) {
 			// scroll backward
 			for ( int i = 0; i < ( 0 - positions ); i++ ) {
 				more = previous();
 				if ( !more ) {
 					break;
 				}
 			}
 		}
 		else {
 			throw new HibernateException( "scroll(0) not valid" );
 		}
 
 		afterScrollOperation();
 
 		return more;
 	}
 
 	/**
 	 * Go to the last result
 	 *
 	 * @return <tt>true</tt> if there are any results
 	 */
 	public boolean last() throws HibernateException {
 		boolean more = false;
 		if ( maxPosition != null ) {
 			if ( currentPosition > maxPosition.intValue() ) {
 				more = previous();
 			}
 			for ( int i = currentPosition; i < maxPosition.intValue(); i++ ) {
 				more = next();
 			}
 		}
 		else {
 			try {
 				if ( isResultSetEmpty() || getResultSet().isAfterLast() ) {
 					// should not be able to reach last without maxPosition being set
 					// unless there are no results
 					return false;
 				}
 
 				while ( !getResultSet().isAfterLast() ) {
 					more = next();
 				}
 			}
 			catch( SQLException e ) {
 				throw getSession().getFactory().getSQLExceptionHelper().convert(
 						e,
 						"exception calling isAfterLast()"
 					);
 			}
 		}
 
 		afterScrollOperation();
 
 		return more;
 	}
 
 	/**
 	 * Go to the first result
 	 *
 	 * @return <tt>true</tt> if there are any results
 	 */
 	public boolean first() throws HibernateException {
 		beforeFirst();
 		boolean more = next();
 
 		afterScrollOperation();
 
 		return more;
 	}
 
 	/**
 	 * Go to a location just before first result (this is the initial location)
 	 */
 	public void beforeFirst() throws HibernateException {
 		try {
 			getResultSet().beforeFirst();
 		}
 		catch( SQLException e ) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "exception calling beforeFirst()"
 				);
 		}
 		currentRow = null;
 		currentPosition = 0;
 	}
 
 	/**
 	 * Go to a location just after the last result
 	 */
 	public void afterLast() throws HibernateException {
 		// TODO : not sure the best way to handle this.
 		// The non-performant way :
 		last();
 		next();
 		afterScrollOperation();
 	}
 
 	/**
 	 * Is this the first result?
 	 *
 	 * @return <tt>true</tt> if this is the first row of results
 	 *
 	 * @throws org.hibernate.HibernateException
 	 */
 	public boolean isFirst() throws HibernateException {
 		return currentPosition == 1;
 	}
 
 	/**
 	 * Is this the last result?
 	 *
 	 * @return <tt>true</tt> if this is the last row of results
 	 *
 	 * @throws org.hibernate.HibernateException
 	 */
 	public boolean isLast() throws HibernateException {
 		if ( maxPosition == null ) {
 			// we have not yet hit the last result...
 			return false;
 		}
 		else {
 			return currentPosition == maxPosition.intValue();
 		}
 	}
 
 	/**
 	 * Get the current location in the result set. The first row is number <tt>0</tt>, contrary to JDBC.
 	 *
 	 * @return the row number, numbered from <tt>0</tt>, or <tt>-1</tt> if there is no current row
 	 */
 	public int getRowNumber() throws HibernateException {
 		return currentPosition;
 	}
 
 	/**
 	 * Set the current location in the result set, numbered from either the first row (row number <tt>0</tt>), or the last
 	 * row (row number <tt>-1</tt>).
 	 *
 	 * @param rowNumber the row number, numbered from the last row, in the case of a negative row number
 	 *
 	 * @return true if there is a row at that row number
 	 */
 	public boolean setRowNumber(int rowNumber) throws HibernateException {
 		if ( rowNumber == 1 ) {
 			return first();
 		}
 		else if ( rowNumber == -1 ) {
 			return last();
 		}
 		else if ( maxPosition != null && rowNumber == maxPosition.intValue() ) {
 			return last();
 		}
 		return scroll( rowNumber - currentPosition );
 	}
 
 	private boolean isResultSetEmpty() {
 		try {
 			return currentPosition == 0 && ! getResultSet().isBeforeFirst() && ! getResultSet().isAfterLast();
 		}
 		catch( SQLException e ) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 			        e,
 			        "Could not determine if resultset is empty due to exception calling isBeforeFirst or isAfterLast()"
 			);
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/FilterHelper.java b/hibernate-core/src/main/java/org/hibernate/internal/FilterHelper.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/FilterHelper.java
rename to hibernate-core/src/main/java/org/hibernate/internal/FilterHelper.java
index eeee1f0646..17f856c18b 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/FilterHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FilterHelper.java
@@ -1,105 +1,105 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.Template;
 
 /**
  * Implementation of FilterHelper.
  *
  * @author Steve Ebersole
  */
 public class FilterHelper {
 
 	private final String[] filterNames;
 	private final String[] filterConditions;
 
 	/**
 	 * The map of defined filters.  This is expected to be in format
 	 * where the filter names are the map keys, and the defined
 	 * conditions are the values.
 	 *
 	 * @param filters The map of defined filters.
 	 * @param dialect The sql dialect
 	 * @param functionRegistry The SQL function registry
 	 */
 	public FilterHelper(Map filters, Dialect dialect, SQLFunctionRegistry functionRegistry) {
 		int filterCount = filters.size();
 		filterNames = new String[filterCount];
 		filterConditions = new String[filterCount];
 		Iterator iter = filters.entrySet().iterator();
 		filterCount = 0;
 		while ( iter.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) iter.next();
 			filterNames[filterCount] = (String) entry.getKey();
 			filterConditions[filterCount] = Template.renderWhereStringTemplate(
 					(String) entry.getValue(),
 					FilterImpl.MARKER,
 					dialect,
 					functionRegistry
 				);
 			filterConditions[filterCount] = StringHelper.replace(
 					filterConditions[filterCount],
 					":",
 					":" + filterNames[filterCount] + "."
 			);
 			filterCount++;
 		}
 	}
 
 	public boolean isAffectedBy(Map enabledFilters) {
 		for ( int i = 0, max = filterNames.length; i < max; i++ ) {
 			if ( enabledFilters.containsKey( filterNames[i] ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public String render(String alias, Map enabledFilters) {
 		StringBuffer buffer = new StringBuffer();
 		render( buffer, alias, enabledFilters );
 		return buffer.toString();
 	}
 
 	public void render(StringBuffer buffer, String alias, Map enabledFilters) {
 		if ( filterNames != null && filterNames.length > 0 ) {
 			for ( int i = 0, max = filterNames.length; i < max; i++ ) {
 				if ( enabledFilters.containsKey( filterNames[i] ) ) {
 					final String condition = filterConditions[i];
 					if ( StringHelper.isNotEmpty( condition ) ) {
 						buffer.append( " and " )
 								.append( StringHelper.replace( condition, FilterImpl.MARKER, alias ) );
 					}
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/FilterImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/FilterImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
index 9f04513fdf..f997863874 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/FilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/FilterImpl.java
@@ -1,173 +1,173 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.io.Serializable;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.Filter;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of FilterImpl.  FilterImpl implements the user's
  * view into enabled dynamic filters, allowing them to set filter parameter values.
  *
  * @author Steve Ebersole
  */
 public class FilterImpl implements Filter, Serializable {
 	public static final String MARKER = "$FILTER_PLACEHOLDER$";
 
 	private transient FilterDefinition definition;
 	private String filterName;
 	private Map parameters = new HashMap();
 	
 	void afterDeserialize(SessionFactoryImpl factory) {
 		definition = factory.getFilterDefinition(filterName);
 		validate();
 	}
 
 	/**
 	 * Constructs a new FilterImpl.
 	 *
 	 * @param configuration The filter's global configuration.
 	 */
 	public FilterImpl(FilterDefinition configuration) {
 		this.definition = configuration;
 		filterName = definition.getFilterName();
 	}
 
 	public FilterDefinition getFilterDefinition() {
 		return definition;
 	}
 
 	/**
 	 * Get the name of this filter.
 	 *
 	 * @return This filter's name.
 	 */
 	public String getName() {
 		return definition.getFilterName();
 	}
 	
 	public Map getParameters() {
 		return parameters;
 	}
 
 	/**
 	 * Set the named parameter's value for this filter.
 	 *
 	 * @param name The parameter's name.
 	 * @param value The value to be applied.
 	 * @return This FilterImpl instance (for method chaining).
 	 * @throws IllegalArgumentException Indicates that either the parameter was undefined or that the type
 	 * of the passed value did not match the configured type.
 	 */
 	public Filter setParameter(String name, Object value) throws IllegalArgumentException {
 		// Make sure this is a defined parameter and check the incoming value type
 		// TODO: what should be the actual exception type here?
 		Type type = definition.getParameterType( name );
 		if ( type == null ) {
 			throw new IllegalArgumentException( "Undefined filter parameter [" + name + "]" );
 		}
 		if ( value != null && !type.getReturnedClass().isAssignableFrom( value.getClass() ) ) {
 			throw new IllegalArgumentException( "Incorrect type for parameter [" + name + "]" );
 		}
 		parameters.put( name, value );
 		return this;
 	}
 
 	/**
 	 * Set the named parameter's value list for this filter.  Used
 	 * in conjunction with IN-style filter criteria.
 	 *
 	 * @param name   The parameter's name.
 	 * @param values The values to be expanded into an SQL IN list.
 	 * @return This FilterImpl instance (for method chaining).
 	 */
 	public Filter setParameterList(String name, Collection values) throws HibernateException  {
 		// Make sure this is a defined parameter and check the incoming value type
 		if ( values == null ) {
 			throw new IllegalArgumentException( "Collection must be not null!" );
 		}
 		Type type = definition.getParameterType( name );
 		if ( type == null ) {
 			throw new HibernateException( "Undefined filter parameter [" + name + "]" );
 		}
 		if ( values.size() > 0 ) {
 			Class elementClass = values.iterator().next().getClass();
 			if ( !type.getReturnedClass().isAssignableFrom( elementClass ) ) {
 				throw new HibernateException( "Incorrect type for parameter [" + name + "]" );
 			}
 		}
 		parameters.put( name, values );
 		return this;
 	}
 
 	/**
 	 * Set the named parameter's value list for this filter.  Used
 	 * in conjunction with IN-style filter criteria.
 	 *
 	 * @param name The parameter's name.
 	 * @param values The values to be expanded into an SQL IN list.
 	 * @return This FilterImpl instance (for method chaining).
 	 */
 	public Filter setParameterList(String name, Object[] values) throws IllegalArgumentException {
 		return setParameterList( name, Arrays.asList( values ) );
 	}
 
 	/**
 	 * Get the value of the named parameter for the current filter.
 	 *
 	 * @param name The name of the parameter for which to return the value.
 	 * @return The value of the named parameter.
 	 */
 	public Object getParameter(String name) {
 		return parameters.get( name );
 	}
 
 	/**
 	 * Perform validation of the filter state.  This is used to verify the
 	 * state of the filter after its enablement and before its use.
 	 *
 	 * @throws HibernateException If the state is not currently valid.
 	 */
 	public void validate() throws HibernateException {
 		// for each of the defined parameters, make sure its value
 		// has been set
 		Iterator itr = definition.getParameterNames().iterator();
 		while ( itr.hasNext() ) {
 			final String parameterName = (String) itr.next();
 			if ( parameters.get( parameterName ) == null ) {
 				throw new HibernateException(
 						"Filter [" + getName() + "] parameter [" + parameterName + "] value not set"
 				);
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
index b9fd62bf5c..9598be189f 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/IteratorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/IteratorImpl.java
@@ -1,182 +1,181 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.NoSuchElementException;
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * An implementation of <tt>java.util.Iterator</tt> that is
  * returned by <tt>iterate()</tt> query execution methods.
  * @author Gavin King
  */
 public final class IteratorImpl implements HibernateIterator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, IteratorImpl.class.getName());
 
 	private ResultSet rs;
 	private final EventSource session;
 	private boolean readOnly;
 	private final Type[] types;
 	private final boolean single;
 	private Object currentResult;
 	private boolean hasNext;
 	private final String[][] names;
 	private PreparedStatement ps;
 	private HolderInstantiator holderInstantiator;
 
 	public IteratorImpl(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        EventSource sess,
 	        boolean readOnly,
 	        Type[] types,
 	        String[][] columnNames,
 	        HolderInstantiator holderInstantiator)
 	throws HibernateException, SQLException {
 
 		this.rs=rs;
 		this.ps=ps;
 		this.session = sess;
 		this.readOnly = readOnly;
 		this.types = types;
 		this.names = columnNames;
 		this.holderInstantiator = holderInstantiator;
 
 		single = types.length==1;
 
 		postNext();
 	}
 
 	public void close() throws JDBCException {
 		if (ps!=null) {
 			try {
                 LOG.debugf("Closing iterator");
 				ps.close();
 				ps = null;
 				rs = null;
 				hasNext = false;
 			}
 			catch (SQLException e) {
                 LOG.unableToCloseIterator(e);
 				throw session.getFactory().getSQLExceptionHelper().convert(
 				        e,
 				        "Unable to close iterator"
 					);
 			}
 			finally {
 				try {
 					session.getPersistenceContext().getLoadContexts().cleanup( rs );
 				}
 				catch( Throwable ignore ) {
 					// ignore this error for now
                     LOG.debugf("Exception trying to cleanup load context : %s", ignore.getMessage());
 				}
 			}
 		}
 	}
 
 	private void postNext() throws SQLException {
         LOG.debugf("Attempting to retrieve next results");
 		this.hasNext = rs.next();
 		if (!hasNext) {
             LOG.debugf("Exhausted results");
 			close();
         } else LOG.debugf("Retrieved next results");
 	}
 
 	public boolean hasNext() {
 		return hasNext;
 	}
 
 	public Object next() throws HibernateException {
 		if ( !hasNext ) throw new NoSuchElementException("No more results");
 		boolean sessionDefaultReadOnlyOrig = session.isDefaultReadOnly();
 		session.setDefaultReadOnly( readOnly );
 		try {
 			boolean isHolder = holderInstantiator.isRequired();
 
             LOG.debugf("Assembling results");
 			if ( single && !isHolder ) {
 				currentResult = types[0].nullSafeGet( rs, names[0], session, null );
 			}
 			else {
 				Object[] currentResults = new Object[types.length];
 				for (int i=0; i<types.length; i++) {
 					currentResults[i] = types[i].nullSafeGet( rs, names[i], session, null );
 				}
 
 				if (isHolder) {
 					currentResult = holderInstantiator.instantiate(currentResults);
 				}
 				else {
 					currentResult = currentResults;
 				}
 			}
 
 			postNext();
             LOG.debugf("Returning current results");
 			return currentResult;
 		}
 		catch (SQLException sqle) {
 			throw session.getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not get next iterator result"
 				);
 		}
 		finally {
 			session.setDefaultReadOnly( sessionDefaultReadOnlyOrig );
 		}
 	}
 
 	public void remove() {
 		if (!single) {
 			throw new UnsupportedOperationException("Not a single column hibernate query result set");
 		}
 		if (currentResult==null) {
 			throw new IllegalStateException("Called Iterator.remove() before next()");
 		}
 		if ( !( types[0] instanceof EntityType ) ) {
 			throw new UnsupportedOperationException("Not an entity");
 		}
 
 		session.delete(
 				( (EntityType) types[0] ).getAssociatedEntityName(),
 				currentResult,
 				false,
 		        null
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
index 2c9ffbabb7..953820ca75 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/NonFlushedChangesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/NonFlushedChangesImpl.java
@@ -1,101 +1,100 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.engine.ActionQueue;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.StatefulPersistenceContext;
 import org.hibernate.event.EventSource;
 
 import org.jboss.logging.Logger;
 
 public final class NonFlushedChangesImpl implements NonFlushedChanges {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NonFlushedChangesImpl.class.getName());
 
 	private static class SessionNonFlushedChanges implements Serializable {
 		private transient EntityMode entityMode;
 		private transient ActionQueue actionQueue;
 		private transient StatefulPersistenceContext persistenceContext;
 
 		public SessionNonFlushedChanges(EventSource session) {
 			this.entityMode = session.getEntityMode();
 			this.actionQueue = session.getActionQueue();
 			this.persistenceContext = ( StatefulPersistenceContext ) session.getPersistenceContext();
 		}
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			ois.defaultReadObject();
 			entityMode = EntityMode.parse( ( String ) ois.readObject() );
 			persistenceContext = StatefulPersistenceContext.deserialize( ois, null );
 			actionQueue = ActionQueue.deserialize( ois, null );
 		}
 
 		private void writeObject(ObjectOutputStream oos) throws IOException {
             LOG.trace("Serializing SessionNonFlushedChanges");
 			oos.defaultWriteObject();
 			oos.writeObject( entityMode.toString() );
 			persistenceContext.serialize( oos );
 			actionQueue.serialize( oos );
 		}
 	}
 	private Map nonFlushedChangesByEntityMode = new HashMap();
 
 	public NonFlushedChangesImpl( EventSource session ) {
 		extractFromSession( session );
 	}
 
 	public void extractFromSession(EventSource session) {
 		if ( nonFlushedChangesByEntityMode.containsKey( session.getEntityMode() ) ) {
 			throw new AssertionFailure( "Already has non-flushed changes for entity mode: " + session.getEntityMode() );
 		}
 		nonFlushedChangesByEntityMode.put( session.getEntityMode(), new SessionNonFlushedChanges( session ) );
 	}
 
 	private SessionNonFlushedChanges getSessionNonFlushedChanges(EntityMode entityMode) {
 		return ( SessionNonFlushedChanges ) nonFlushedChangesByEntityMode.get( entityMode );
 	}
 
 	/* package-protected */
 	ActionQueue getActionQueue(EntityMode entityMode) {
 		return getSessionNonFlushedChanges( entityMode ).actionQueue;
 	}
 
 	/* package-protected */
 	StatefulPersistenceContext getPersistenceContext(EntityMode entityMode) {
 		return getSessionNonFlushedChanges( entityMode ).persistenceContext;
 	}
 
 	public void clear() {
 		nonFlushedChangesByEntityMode.clear();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/QueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/QueryImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/QueryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/QueryImpl.java
index fd21bd1dbb..8a2d724864 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/QueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/QueryImpl.java
@@ -1,147 +1,147 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.query.ParameterMetadata;
 
 /**
  * default implementation of the <tt>Query</tt> interface,
  * for "ordinary" HQL queries (not collection filters)
  * @see CollectionFilterImpl
  * @author Gavin King
  */
 public class QueryImpl extends AbstractQueryImpl {
 
 	private LockOptions lockOptions = new LockOptions();
 
 	public QueryImpl(
 			String queryString,
 	        FlushMode flushMode,
 	        SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		super( queryString, flushMode, session, parameterMetadata );
 	}
 
 	public QueryImpl(String queryString, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		this( queryString, null, session, parameterMetadata );
 	}
 
 	public Iterator iterate() throws HibernateException {
 		verifyParameters();
 		Map namedParams = getNamedParams();
 		before();
 		try {
 			return getSession().iterate(
 					expandParameterLists(namedParams),
 			        getQueryParameters(namedParams)
 				);
 		}
 		finally {
 			after();
 		}
 	}
 
 	public ScrollableResults scroll() throws HibernateException {
 		return scroll( ScrollMode.SCROLL_INSENSITIVE );
 	}
 
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
 		verifyParameters();
 		Map namedParams = getNamedParams();
 		before();
 		QueryParameters qp = getQueryParameters(namedParams);
 		qp.setScrollMode(scrollMode);
 		try {
 			return getSession().scroll( expandParameterLists(namedParams), qp );
 		}
 		finally {
 			after();
 		}
 	}
 
 	public List list() throws HibernateException {
 		verifyParameters();
 		Map namedParams = getNamedParams();
 		before();
 		try {
 			return getSession().list(
 					expandParameterLists(namedParams),
 			        getQueryParameters(namedParams)
 				);
 		}
 		finally {
 			after();
 		}
 	}
 
 	public int executeUpdate() throws HibernateException {
 		verifyParameters();
 		Map namedParams = getNamedParams();
 		before();
 		try {
             return getSession().executeUpdate(
                     expandParameterLists( namedParams ),
                     getQueryParameters( namedParams )
 	            );
 		}
 		finally {
 			after();
 		}
 	}
 
 	public Query setLockMode(String alias, LockMode lockMode) {
 		lockOptions.setAliasSpecificLockMode( alias, lockMode );
 		return this;
 	}
 	
 	public Query setLockOptions(LockOptions lockOption) {
 		this.lockOptions.setLockMode(lockOption.getLockMode());
 		this.lockOptions.setScope(lockOption.getScope());
 		this.lockOptions.setTimeOut(lockOption.getTimeOut());
 		return this;
 	}
 
 	public LockOptions getLockOptions() {
 		return lockOptions;
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
index eb173053f8..78951ca796 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
@@ -1,502 +1,502 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.query.ParameterMetadata;
 import org.hibernate.engine.query.sql.NativeSQLQueryJoinReturn;
 import org.hibernate.engine.query.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the {@link SQLQuery} contract.
  *
  * @author Max Andersen
  * @author Steve Ebersole
  */
 public class SQLQueryImpl extends AbstractQueryImpl implements SQLQuery {
 
 	private List<NativeSQLQueryReturn> queryReturns;
 	private List<ReturnBuilder> queryReturnBuilders;
 	private boolean autoDiscoverTypes;
 
 	private Collection<String> querySpaces;
 
 	private final boolean callable;
 
 	/**
 	 * Constructs a SQLQueryImpl given a sql query defined in the mappings.
 	 *
 	 * @param queryDef The representation of the defined <sql-query/>.
 	 * @param session The session to which this SQLQueryImpl belongs.
 	 * @param parameterMetadata Metadata about parameters found in the query.
 	 */
 	SQLQueryImpl(NamedSQLQueryDefinition queryDef, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( queryDef.getQueryString(), queryDef.getFlushMode(), session, parameterMetadata );
 		if ( queryDef.getResultSetRef() != null ) {
 			ResultSetMappingDefinition definition = session.getFactory()
 					.getResultSetMapping( queryDef.getResultSetRef() );
 			if (definition == null) {
 				throw new MappingException(
 						"Unable to find resultset-ref definition: " +
 						queryDef.getResultSetRef()
 					);
 			}
 			this.queryReturns = Arrays.asList( definition.getQueryReturns() );
 		}
 		else if ( queryDef.getQueryReturns() != null && queryDef.getQueryReturns().length > 0 ) {
 			this.queryReturns = Arrays.asList( queryDef.getQueryReturns() );
 		}
 		else {
 			this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
 		}
 
 		this.querySpaces = queryDef.getQuerySpaces();
 		this.callable = queryDef.isCallable();
 	}
 
 	SQLQueryImpl(
 			final String sql,
 	        final String returnAliases[],
 	        final Class returnClasses[],
 	        final LockMode[] lockModes,
 	        final SessionImplementor session,
 	        final Collection<String> querySpaces,
 	        final FlushMode flushMode,
 	        ParameterMetadata parameterMetadata) {
 		// TODO : this constructor form is *only* used from constructor directly below us; can it go away?
 		super( sql, flushMode, session, parameterMetadata );
 		queryReturns = new ArrayList<NativeSQLQueryReturn>( returnAliases.length );
 		for ( int i=0; i<returnAliases.length; i++ ) {
 			NativeSQLQueryRootReturn ret = new NativeSQLQueryRootReturn(
 					returnAliases[i],
 					returnClasses[i].getName(),
 					lockModes==null ? LockMode.NONE : lockModes[i]
 			);
 			queryReturns.add(ret);
 		}
 		this.querySpaces = querySpaces;
 		this.callable = false;
 	}
 
 	SQLQueryImpl(
 			final String sql,
 	        final String returnAliases[],
 	        final Class returnClasses[],
 	        final SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		this( sql, returnAliases, returnClasses, null, session, null, null, parameterMetadata );
 	}
 
 	SQLQueryImpl(String sql, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( sql, null, session, parameterMetadata );
 		queryReturns = new ArrayList<NativeSQLQueryReturn>();
 		querySpaces = null;
 		callable = false;
 	}
 
 	private NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] );
 	}
 
 	public List list() throws HibernateException {
 		verifyParameters();
 		before();
 
 		Map namedParams = getNamedParams();
 		NativeSQLQuerySpecification spec = generateQuerySpecification( namedParams );
 
 		try {
 			return getSession().list( spec, getQueryParameters( namedParams ) );
 		}
 		finally {
 			after();
 		}
 	}
 
 	private NativeSQLQuerySpecification generateQuerySpecification(Map namedParams) {
 		return new NativeSQLQuerySpecification(
 		        expandParameterLists(namedParams),
 		        getQueryReturns(),
 		        querySpaces
 		);
 	}
 
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
 		verifyParameters();
 		before();
 
 		Map namedParams = getNamedParams();
 		NativeSQLQuerySpecification spec = generateQuerySpecification( namedParams );
 
 		QueryParameters qp = getQueryParameters( namedParams );
 		qp.setScrollMode( scrollMode );
 
 		try {
 			return getSession().scroll( spec, qp );
 		}
 		finally {
 			after();
 		}
 	}
 
 	public ScrollableResults scroll() throws HibernateException {
 		return scroll(ScrollMode.SCROLL_INSENSITIVE);
 	}
 
 	public Iterator iterate() throws HibernateException {
 		throw new UnsupportedOperationException("SQL queries do not currently support iteration");
 	}
 
 	@Override
     public QueryParameters getQueryParameters(Map namedParams) {
 		QueryParameters qp = super.getQueryParameters(namedParams);
 		qp.setCallable(callable);
 		qp.setAutoDiscoverScalarTypes( autoDiscoverTypes );
 		return qp;
 	}
 
 	@Override
     protected void verifyParameters() {
 		// verifyParameters is called at the start of all execution type methods, so we use that here to perform
 		// some preparation work.
 		prepare();
 		verifyParameters( callable );
 		boolean noReturns = queryReturns==null || queryReturns.isEmpty();
 		if ( noReturns ) {
 			this.autoDiscoverTypes = noReturns;
 		}
 		else {
 			for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
 				if ( queryReturn instanceof NativeSQLQueryScalarReturn ) {
 					NativeSQLQueryScalarReturn scalar = (NativeSQLQueryScalarReturn) queryReturn;
 					if ( scalar.getType() == null ) {
 						autoDiscoverTypes = true;
 						break;
 					}
 				}
 			}
 		}
 	}
 
 	private void prepare() {
 		if ( queryReturnBuilders != null ) {
 			if ( ! queryReturnBuilders.isEmpty() ) {
 				if ( queryReturns != null ) {
 					queryReturns.clear();
 					queryReturns = null;
 				}
 				queryReturns = new ArrayList<NativeSQLQueryReturn>();
 				for ( ReturnBuilder builder : queryReturnBuilders ) {
 					queryReturns.add( builder.buildReturn() );
 				}
 				queryReturnBuilders.clear();
 			}
 			queryReturnBuilders = null;
 		}
 	}
 
 	@Override
     public String[] getReturnAliases() throws HibernateException {
 		throw new UnsupportedOperationException("SQL queries do not currently support returning aliases");
 	}
 
 	@Override
     public Type[] getReturnTypes() throws HibernateException {
 		throw new UnsupportedOperationException("not yet implemented for SQL queries");
 	}
 
 	public Query setLockMode(String alias, LockMode lockMode) {
 		throw new UnsupportedOperationException("cannot set the lock mode for a native SQL query");
 	}
 
 	public Query setLockOptions(LockOptions lockOptions) {
 		throw new UnsupportedOperationException("cannot set lock options for a native SQL query");
 	}
 
 	@Override
     public LockOptions getLockOptions() {
 		//we never need to apply locks to the SQL
 		return null;
 	}
 
 	public SQLQuery addScalar(final String columnAlias, final Type type) {
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add(
 				new ReturnBuilder() {
 					public NativeSQLQueryReturn buildReturn() {
 						return new NativeSQLQueryScalarReturn( columnAlias, type );
 					}
 				}
 		);
 		return this;
 	}
 
 	public SQLQuery addScalar(String columnAlias) {
 		return addScalar( columnAlias, null );
 	}
 
 	public RootReturn addRoot(String tableAlias, String entityName) {
 		RootReturnBuilder builder = new RootReturnBuilder( tableAlias, entityName );
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add( builder );
 		return builder;
 	}
 
 	public RootReturn addRoot(String tableAlias, Class entityType) {
 		return addRoot( tableAlias, entityType.getName() );
 	}
 
 	public SQLQuery addEntity(String entityName) {
 		return addEntity( StringHelper.unqualify( entityName ), entityName );
 	}
 
 	public SQLQuery addEntity(String alias, String entityName) {
 		addRoot( alias, entityName );
 		return this;
 	}
 
 	public SQLQuery addEntity(String alias, String entityName, LockMode lockMode) {
 		addRoot( alias, entityName ).setLockMode( lockMode );
 		return this;
 	}
 
 	public SQLQuery addEntity(Class entityType) {
 		return addEntity( entityType.getName() );
 	}
 
 	public SQLQuery addEntity(String alias, Class entityClass) {
 		return addEntity( alias, entityClass.getName() );
 	}
 
 	public SQLQuery addEntity(String alias, Class entityClass, LockMode lockMode) {
 		return addEntity( alias, entityClass.getName(), lockMode );
 	}
 
 	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		FetchReturnBuilder builder = new FetchReturnBuilder( tableAlias, ownerTableAlias, joinPropertyName );
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add( builder );
 		return builder;
 	}
 
 	public SQLQuery addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		addFetch( tableAlias, ownerTableAlias, joinPropertyName );
 		return this;
 	}
 
 	public SQLQuery addJoin(String alias, String path) {
 		createFetchJoin( alias, path );
 		return this;
 	}
 
 	private FetchReturn createFetchJoin(String tableAlias, String path) {
 		int loc = path.indexOf('.');
 		if ( loc < 0 ) {
 			throw new QueryException( "not a property path: " + path );
 		}
 		final String ownerTableAlias = path.substring( 0, loc );
 		final String joinedPropertyName = path.substring( loc+1 );
 		return addFetch( tableAlias, ownerTableAlias, joinedPropertyName );
 	}
 
 	public SQLQuery addJoin(String alias, String path, LockMode lockMode) {
 		createFetchJoin( alias, path ).setLockMode( lockMode );
 		return this;
 	}
 
 	public SQLQuery setResultSetMapping(String name) {
 		ResultSetMappingDefinition mapping = session.getFactory().getResultSetMapping( name );
 		if ( mapping == null ) {
 			throw new MappingException( "Unknown SqlResultSetMapping [" + name + "]" );
 		}
 		NativeSQLQueryReturn[] returns = mapping.getQueryReturns();
 		queryReturns.addAll( Arrays.asList( returns ) );
 		return this;
 	}
 
 	public SQLQuery addSynchronizedQuerySpace(String querySpace) {
 		if ( querySpaces == null ) {
 			querySpaces = new ArrayList<String>();
 		}
 		querySpaces.add( querySpace );
 		return this;
 	}
 
 	public SQLQuery addSynchronizedEntityName(String entityName) {
 		return addQuerySpaces( getSession().getFactory().getEntityPersister( entityName ).getQuerySpaces() );
 	}
 
 	public SQLQuery addSynchronizedEntityClass(Class entityClass) {
 		return addQuerySpaces( getSession().getFactory().getEntityPersister( entityClass.getName() ).getQuerySpaces() );
 	}
 
 	private SQLQuery addQuerySpaces(Serializable[] spaces) {
 		if ( spaces != null ) {
 			if ( querySpaces == null ) {
 				querySpaces = new ArrayList<String>();
 			}
 			querySpaces.addAll( Arrays.asList( (String[]) spaces ) );
 		}
 		return this;
 	}
 
 	public int executeUpdate() throws HibernateException {
 		Map namedParams = getNamedParams();
 		before();
 		try {
 			return getSession().executeNativeUpdate(
 					generateQuerySpecification( namedParams ),
 					getQueryParameters( namedParams )
 			);
 		}
 		finally {
 			after();
 		}
 	}
 
 	private class RootReturnBuilder implements RootReturn, ReturnBuilder {
 		private final String alias;
 		private final String entityName;
 		private LockMode lockMode = LockMode.READ;
 		private Map<String,List<String>> propertyMappings;
 
 		private RootReturnBuilder(String alias, String entityName) {
 			this.alias = alias;
 			this.entityName = entityName;
 		}
 
 		public RootReturn setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public RootReturn setDiscriminatorAlias(String alias) {
 			addProperty( "class", alias );
 			return this;
 		}
 
 		public RootReturn addProperty(String propertyName, String columnAlias) {
 			addProperty( propertyName ).addColumnAlias( columnAlias );
 			return this;
 		}
 
 		public ReturnProperty addProperty(final String propertyName) {
 			if ( propertyMappings == null ) {
 				propertyMappings = new HashMap<String,List<String>>();
 			}
 			return new ReturnProperty() {
 				public ReturnProperty addColumnAlias(String columnAlias) {
 					List<String> columnAliases = propertyMappings.get( propertyName );
 					if ( columnAliases == null ) {
 						columnAliases = new ArrayList<String>();
 					}
 					columnAliases.add( columnAlias );
 					return this;
 				}
 			};
 		}
 
 		public NativeSQLQueryReturn buildReturn() {
 			return new NativeSQLQueryRootReturn( alias, entityName, propertyMappings, lockMode );
 		}
 	}
 	private class FetchReturnBuilder implements FetchReturn, ReturnBuilder {
 		private final String alias;
 		private String ownerTableAlias;
 		private final String joinedPropertyName;
 		private LockMode lockMode = LockMode.READ;
 		private Map<String,List<String>> propertyMappings;
 
 		private FetchReturnBuilder(String alias, String ownerTableAlias, String joinedPropertyName) {
 			this.alias = alias;
 			this.ownerTableAlias = ownerTableAlias;
 			this.joinedPropertyName = joinedPropertyName;
 		}
 
 		public FetchReturn setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public FetchReturn addProperty(String propertyName, String columnAlias) {
 			addProperty( propertyName ).addColumnAlias( columnAlias );
 			return this;
 		}
 
 		public ReturnProperty addProperty(final String propertyName) {
 			if ( propertyMappings == null ) {
 				propertyMappings = new HashMap<String,List<String>>();
 			}
 			return new ReturnProperty() {
 				public ReturnProperty addColumnAlias(String columnAlias) {
 					List<String> columnAliases = propertyMappings.get( propertyName );
 					if ( columnAliases == null ) {
 						columnAliases = new ArrayList<String>();
 					}
 					columnAliases.add( columnAlias );
 					return this;
 				}
 			};
 		}
 
 		public NativeSQLQueryReturn buildReturn() {
 			return new NativeSQLQueryJoinReturn( alias, ownerTableAlias, joinedPropertyName, propertyMappings, lockMode );
 		}
 	}
 
 	private interface ReturnBuilder {
 		NativeSQLQueryReturn buildReturn();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/ScrollableResultsImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/ScrollableResultsImpl.java
index 83592a0aad..05b051f851 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/ScrollableResultsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/ScrollableResultsImpl.java
@@ -1,259 +1,259 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.loader.Loader;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the <tt>ScrollableResults</tt> interface
  * @author Gavin King
  */
 public class ScrollableResultsImpl extends AbstractScrollableResults implements ScrollableResults {
 
 	private Object[] currentRow;
 
 	public ScrollableResultsImpl(
 	        ResultSet rs,
 	        PreparedStatement ps,
 	        SessionImplementor sess,
 	        Loader loader,
 	        QueryParameters queryParameters,
 	        Type[] types, HolderInstantiator holderInstantiator) throws MappingException {
 		super( rs, ps, sess, loader, queryParameters, types, holderInstantiator );
 	}
 
 	protected Object[] getCurrentRow() {
 		return currentRow;
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#scroll(int)
 	 */
 	public boolean scroll(int i) throws HibernateException {
 		try {
 			boolean result = getResultSet().relative(i);
 			prepareCurrentRow(result);
 			return result;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not advance using scroll()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#first()
 	 */
 	public boolean first() throws HibernateException {
 		try {
 			boolean result = getResultSet().first();
 			prepareCurrentRow(result);
 			return result;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not advance using first()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#last()
 	 */
 	public boolean last() throws HibernateException {
 		try {
 			boolean result = getResultSet().last();
 			prepareCurrentRow(result);
 			return result;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not advance using last()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#next()
 	 */
 	public boolean next() throws HibernateException {
 		try {
 			boolean result = getResultSet().next();
 			prepareCurrentRow(result);
 			return result;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not advance using next()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#previous()
 	 */
 	public boolean previous() throws HibernateException {
 		try {
 			boolean result = getResultSet().previous();
 			prepareCurrentRow(result);
 			return result;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not advance using previous()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#afterLast()
 	 */
 	public void afterLast() throws HibernateException {
 		try {
 			getResultSet().afterLast();
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"exception calling afterLast()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#beforeFirst()
 	 */
 	public void beforeFirst() throws HibernateException {
 		try {
 			getResultSet().beforeFirst();
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"exception calling beforeFirst()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#isFirst()
 	 */
 	public boolean isFirst() throws HibernateException {
 		try {
 			return getResultSet().isFirst();
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"exception calling isFirst()"
 				);
 		}
 	}
 
 	/**
 	 * @see org.hibernate.ScrollableResults#isLast()
 	 */
 	public boolean isLast() throws HibernateException {
 		try {
 			return getResultSet().isLast();
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"exception calling isLast()"
 				);
 		}
 	}
 
 	public int getRowNumber() throws HibernateException {
 		try {
 			return getResultSet().getRow()-1;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"exception calling getRow()"
 				);
 		}
 	}
 
 	public boolean setRowNumber(int rowNumber) throws HibernateException {
 		if (rowNumber>=0) rowNumber++;
 		try {
 			boolean result = getResultSet().absolute(rowNumber);
 			prepareCurrentRow(result);
 			return result;
 		}
 		catch (SQLException sqle) {
 			throw getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not advance using absolute()"
 				);
 		}
 	}
 
 	private void prepareCurrentRow(boolean underlyingScrollSuccessful) 
 	throws HibernateException {
 		
 		if (!underlyingScrollSuccessful) {
 			currentRow = null;
 			return;
 		}
 
 		Object result = getLoader().loadSingleRow(
 				getResultSet(),
 				getSession(),
 				getQueryParameters(),
 				false
 		);
 		if ( result != null && result.getClass().isArray() ) {
 			currentRow = (Object[]) result;
 		}
 		else {
 			currentRow = new Object[] { result };
 		}
 
 		if ( getHolderInstantiator() != null ) {
 			currentRow = new Object[] { getHolderInstantiator().instantiate(currentRow) };
 		}
 
 		afterScrollOperation();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index fbd5bf376f..cd508bfa00 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1106 +1,1105 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
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
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.QueryPlanCache;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
-import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
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
         LOG.debug( "Building session factory" );
 
 		this.settings = settings;
 		this.interceptor = cfg.getInterceptor();
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
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
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
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
 			new SchemaExport( serviceRegistry, cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg );
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
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return new StatelessSessionImpl( null, null, this );
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return new StatelessSessionImpl( connection, null, this );
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObjectFactory.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
rename to hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObjectFactory.java
index f942f2b078..fb146d84e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObjectFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObjectFactory.java
@@ -1,169 +1,168 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.util.Hashtable;
 import java.util.Iterator;
 import java.util.Properties;
 import java.util.concurrent.ConcurrentHashMap;
 import javax.naming.Context;
 import javax.naming.InvalidNameException;
 import javax.naming.Name;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.event.EventContext;
 import javax.naming.event.NamespaceChangeListener;
 import javax.naming.event.NamingEvent;
 import javax.naming.event.NamingExceptionEvent;
 import javax.naming.event.NamingListener;
 import javax.naming.spi.ObjectFactory;
 
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.SessionFactory;
 import org.hibernate.internal.util.jndi.JndiHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Resolves {@link SessionFactory} instances during <tt>JNDI<tt> look-ups as well as during deserialization
  */
 public class SessionFactoryObjectFactory implements ObjectFactory {
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	private static final SessionFactoryObjectFactory INSTANCE; //to stop the class from being unloaded
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        SessionFactoryObjectFactory.class.getName());
 
 	static {
 		INSTANCE = new SessionFactoryObjectFactory();
         LOG.debugf("Initializing class SessionFactoryObjectFactory");
 	}
 
 	private static final ConcurrentHashMap<String, SessionFactory> INSTANCES = new ConcurrentHashMap<String, SessionFactory>();
 	private static final ConcurrentHashMap<String, SessionFactory> NAMED_INSTANCES = new ConcurrentHashMap<String, SessionFactory>();
 
 	private static final NamingListener LISTENER = new NamespaceChangeListener() {
 		public void objectAdded(NamingEvent evt) {
             LOG.debugf("A factory was successfully bound to name: %s", evt.getNewBinding().getName());
 		}
 		public void objectRemoved(NamingEvent evt) {
 			String name = evt.getOldBinding().getName();
             LOG.factoryUnboundFromName(name);
 			Object instance = NAMED_INSTANCES.remove(name);
 			Iterator iter = INSTANCES.values().iterator();
 			while ( iter.hasNext() ) {
 				if ( iter.next()==instance ) iter.remove();
 			}
 		}
 		public void objectRenamed(NamingEvent evt) {
 			String name = evt.getOldBinding().getName();
             LOG.factoryRenamedFromName(name);
 			NAMED_INSTANCES.put( evt.getNewBinding().getName(), NAMED_INSTANCES.remove(name) );
 		}
 		public void namingExceptionThrown(NamingExceptionEvent evt) {
 			//noinspection ThrowableResultOfMethodCallIgnored
             LOG.namingExceptionAccessingFactory(evt.getException());
 		}
 	};
 
 	public Object getObjectInstance(Object reference, Name name, Context ctx, Hashtable env) throws Exception {
         LOG.debugf("JNDI lookup: %s", name);
 		String uid = (String) ( (Reference) reference ).get(0).getContent();
 		return getInstance(uid);
 	}
 
 	public static void addInstance(String uid, String name, SessionFactory instance, Properties properties) {
         LOG.debugf("Registered: %s (%s)", uid, name == null ? "<unnamed>" : name);
 		INSTANCES.put(uid, instance);
 		if (name!=null) NAMED_INSTANCES.put(name, instance);
 
 		//must add to JNDI _after_ adding to HashMaps, because some JNDI servers use serialization
         if (name == null) LOG.notBindingFactoryToJndi();
 		else {
             LOG.factoryName(name);
 
 			try {
 				Context ctx = JndiHelper.getInitialContext(properties);
 				JndiHelper.bind(ctx, name, instance);
                 LOG.factoryBoundToJndiName(name);
 				( (EventContext) ctx ).addNamingListener(name, EventContext.OBJECT_SCOPE, LISTENER);
 			}
 			catch (InvalidNameException ine) {
                 LOG.invalidJndiName(name, ine);
 			}
 			catch (NamingException ne) {
                 LOG.unableToBindFactoryToJndi(ne);
 			}
 			catch(ClassCastException cce) {
                 LOG.initialContextDidNotImplementEventContext();
 			}
 		}
 	}
 
 	public static void removeInstance(String uid, String name, Properties properties) {
 		//TODO: theoretically non-threadsafe...
 
 		if (name!=null) {
             LOG.unbindingFactoryFromJndiName(name);
 
 			try {
 				Context ctx = JndiHelper.getInitialContext(properties);
 				ctx.unbind(name);
                 LOG.factoryUnboundFromJndiName(name);
 			}
 			catch (InvalidNameException ine) {
                 LOG.invalidJndiName(name, ine);
 			}
 			catch (NamingException ne) {
                 LOG.unableToUnbindFactoryFromJndi(ne);
 			}
 
 			NAMED_INSTANCES.remove(name);
 
 		}
 
 		INSTANCES.remove(uid);
 
 	}
 
 	public static Object getNamedInstance(String name) {
         LOG.debugf("Lookup: name=%s", name);
 		Object result = NAMED_INSTANCES.get(name);
 		if (result==null) {
             LOG.debugf("Not found: %s", name);
             LOG.debugf(NAMED_INSTANCES.toString());
 		}
 		return result;
 	}
 
 	public static Object getInstance(String uid) {
         LOG.debugf("Lookup: uid=%s", uid);
 		Object result = INSTANCES.get(uid);
 		if (result==null) {
             LOG.debugf("Not found: %s", uid);
             LOG.debugf(INSTANCES.toString());
 		}
 		return result;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObserverChain.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java
rename to hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObserverChain.java
index c7ecb0e48b..90d4e2de40 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryObserverChain.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryObserverChain.java
@@ -1,66 +1,66 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 
 /**
  * @author Steve Ebersole
  */
 public class SessionFactoryObserverChain implements SessionFactoryObserver {
 	private List<SessionFactoryObserver> observers;
 
 	public void addObserver(SessionFactoryObserver observer) {
 		if ( observers == null ) {
 			observers = new ArrayList<SessionFactoryObserver>();
 		}
 		observers.add( observer );
 	}
 
 	@Override
 	public void sessionFactoryCreated(SessionFactory factory) {
 		if ( observers == null ) {
 			return;
 		}
 
 		for ( SessionFactoryObserver observer : observers ) {
 			observer.sessionFactoryCreated( factory );
 		}
 	}
 
 	@Override
 	public void sessionFactoryClosed(SessionFactory factory) {
 		if ( observers == null ) {
 			return;
 		}
 
 		for ( SessionFactoryObserver observer : observers ) {
 			observer.sessionFactoryClosed( factory );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 343cf8f854..ae7519e2a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,1059 +1,1058 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2005-2011, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Reader;
 import java.io.Serializable;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.NClob;
 import java.sql.SQLException;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.SessionBuilder;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.ActionQueue;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.StatefulPersistenceContext;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.query.FilterQueryPlan;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.event.AutoFlushEvent;
 import org.hibernate.event.AutoFlushEventListener;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.DirtyCheckEvent;
 import org.hibernate.event.DirtyCheckEventListener;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.event.EvictEvent;
 import org.hibernate.event.EvictEventListener;
 import org.hibernate.event.FlushEvent;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.InitializeCollectionEvent;
 import org.hibernate.event.InitializeCollectionEventListener;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.LoadEventListener.LoadType;
 import org.hibernate.event.LockEvent;
 import org.hibernate.event.LockEventListener;
 import org.hibernate.event.MergeEvent;
 import org.hibernate.event.MergeEventListener;
 import org.hibernate.event.PersistEvent;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.RefreshEvent;
 import org.hibernate.event.RefreshEventListener;
 import org.hibernate.event.ReplicateEvent;
 import org.hibernate.event.ReplicateEventListener;
 import org.hibernate.event.SaveOrUpdateEvent;
 import org.hibernate.event.SaveOrUpdateEventListener;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 
 /**
  * Concrete implementation of a Session.
  *
  * Exposes two interfaces:<ul>
  *     <li>{@link Session} to the application</li>
  *     <li>{@link org.hibernate.engine.SessionImplementor} to other Hibernate components (SPI)</li>
  * </ul>
  *
  * This class is not thread-safe.
  *
  * @author Gavin King
  */
 public final class SessionImpl
 		extends AbstractSessionImpl
 		implements EventSource, org.hibernate.Session, TransactionContext, LobCreationContext {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around separate interceptor, factory, actionQueue, and persistentContext is not manageable...
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName());
 
 	private transient long timestamp;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 	private transient TransactionCoordinatorImpl transactionCoordinator;
 	private transient Interceptor interceptor;
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
 	private transient ConnectionReleaseMode connectionReleaseMode;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 	private transient EntityMode entityMode = EntityMode.POJO;
 
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
 	private transient int dontFlushFromFind = 0;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	private transient Session rootSession;
 	private transient Map childSessionsByEntityMode;
 
 	/**
 	 * Constructor used in building "child sessions".
 	 *
 	 * @param parent The parent session
 	 * @param entityMode
 	 */
 	private SessionImpl(SessionImpl parent, EntityMode entityMode) {
 		super( parent.factory, parent.getTenantIdentifier() );
 		this.rootSession = parent;
 		this.timestamp = parent.timestamp;
 		this.transactionCoordinator = parent.transactionCoordinator;
 		this.interceptor = parent.interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.entityMode = entityMode;
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = false;
 		this.autoCloseSessionEnabled = false;
 		this.connectionReleaseMode = null;
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
         if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
         LOG.debugf("Opened session [%s]", entityMode);
 	}
 
 	/**
 	 * Constructor used for openSession(...) processing, as well as construction
 	 * of sessions for getCurrentSession().
 	 *
 	 * @param connection The user-supplied connection to use for this session.
 	 * @param factory The factory from which this session was obtained
 	 * @param transactionCoordinator The transaction coordinator to use, may be null to indicate that a new transaction
 	 * coordinator should get created.
 	 * @param autoJoinTransactions Should the session automatically join JTA transactions?
 	 * @param timestamp The timestamp for this session
 	 * @param interceptor The interceptor to be applied to this session
 	 * @param entityMode The entity-mode for this session
 	 * @param flushBeforeCompletionEnabled Should we auto flush before completion of transaction
 	 * @param autoCloseSessionEnabled Should we auto close after completion of transaction
 	 * @param connectionReleaseMode The mode by which we should release JDBC connections.
 	 */
 	SessionImpl(
 			final Connection connection,
 			final SessionFactoryImpl factory,
 			final TransactionCoordinatorImpl transactionCoordinator,
 			final boolean autoJoinTransactions,
 			final long timestamp,
 			final Interceptor interceptor,
 			final EntityMode entityMode,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode,
 			final String tenantIdentifier) {
 		super( factory, tenantIdentifier );
 		this.rootSession = null;
 		this.timestamp = timestamp;
 		this.entityMode = entityMode;
 		this.interceptor = interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.connectionReleaseMode = connectionReleaseMode;
 		this.autoJoinTransactions = autoJoinTransactions;
 
 		if ( transactionCoordinator == null ) {
 			this.transactionCoordinator = new TransactionCoordinatorImpl( connection, this );
 			this.transactionCoordinator.getJdbcCoordinator().getLogicalConnection().addObserver(
 					new ConnectionObserverStatsBridge( factory )
 			);
 		}
 		else {
 			if ( connection != null ) {
 				throw new SessionException( "Cannot simultaneously share transaction context and specify connection" );
 			}
 			this.transactionCoordinator = transactionCoordinator;
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
         if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
         LOG.debugf("Opened session at timestamp: %s", timestamp);
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
 	public Session getSession(EntityMode entityMode) {
 		if ( this.entityMode == entityMode ) {
 			return this;
 		}
 
 		if ( rootSession != null ) {
 			return rootSession.getSession( entityMode );
 		}
 
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
 		SessionImpl rtn = null;
 		if ( childSessionsByEntityMode == null ) {
 			childSessionsByEntityMode = new HashMap();
 		}
 		else {
 			rtn = (SessionImpl) childSessionsByEntityMode.get( entityMode );
 		}
 
 		if ( rtn == null ) {
 			rtn = new SessionImpl( this, entityMode );
 			childSessionsByEntityMode.put( entityMode, rtn );
 		}
 
 		return rtn;
 	}
 
 	public void clear() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.clear();
 		actionQueue.clear();
 	}
 
 	public long getTimestamp() {
 		checkTransactionSynchStatus();
 		return timestamp;
 	}
 
 	public Connection close() throws HibernateException {
         LOG.trace("Closing session");
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed" );
 		}
 
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().closeSession();
 		}
 
 		try {
 			try {
 				if ( childSessionsByEntityMode != null ) {
 					Iterator childSessions = childSessionsByEntityMode.values().iterator();
 					while ( childSessions.hasNext() ) {
 						final SessionImpl child = ( SessionImpl ) childSessions.next();
 						child.close();
 					}
 				}
 			}
 			catch( Throwable t ) {
 				// just ignore
 			}
 
 			if ( rootSession == null ) {
 				return transactionCoordinator.close();
 			}
 			else {
 				return null;
 			}
 		}
 		finally {
 			setClosed();
 			cleanup();
 		}
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return connectionReleaseMode;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return autoJoinTransactions;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return autoCloseSessionEnabled;
 	}
 
 	public boolean isOpen() {
 		checkTransactionSynchStatus();
 		return !isClosed();
 	}
 
 	public boolean isFlushModeNever() {
 		return FlushMode.isManualFlushMode( getFlushMode() );
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return flushBeforeCompletionEnabled;
 	}
 
 	public void managedFlush() {
 		if ( isClosed() ) {
             LOG.trace("Skipping auto-flush due to session closed");
 			return;
 		}
         LOG.trace( "Automatically flushing session" );
 		flush();
 
 		if ( childSessionsByEntityMode != null ) {
 			Iterator iter = childSessionsByEntityMode.values().iterator();
 			while ( iter.hasNext() ) {
 				( (Session) iter.next() ).flush();
 			}
 		}
 	}
 
 	/**
 	 * Return changes to this session and its child sessions that have not been flushed yet.
 	 * <p/>
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		NonFlushedChanges nonFlushedChanges = new NonFlushedChangesImpl( this );
 		if ( childSessionsByEntityMode != null ) {
 			Iterator it = childSessionsByEntityMode.values().iterator();
 			while ( it.hasNext() ) {
 				nonFlushedChanges.extractFromSession( ( EventSource ) it.next() );
 			}
 		}
 		return nonFlushedChanges;
 	}
 
 	/**
 	 * Apply non-flushed changes from a different session to this session. It is assumed
 	 * that this SessionImpl is "clean" (e.g., has no non-flushed changes, no cached entities,
 	 * no cached collections, no queued actions). The specified NonFlushedChanges object cannot
 	 * be bound to any session.
 	 * <p/>
 	 * @param nonFlushedChanges the non-flushed changes
 	 */
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		replacePersistenceContext( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getPersistenceContext( entityMode) );
 		replaceActionQueue( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getActionQueue( entityMode ) );
 		if ( childSessionsByEntityMode != null ) {
 			for ( Iterator it = childSessionsByEntityMode.values().iterator(); it.hasNext(); ) {
 				( ( SessionImpl ) it.next() ).applyNonFlushedChanges( nonFlushedChanges );
 			}
 		}
 	}
 
 	private void replacePersistenceContext(StatefulPersistenceContext persistenceContextNew) {
 		if ( persistenceContextNew.getSession() != null ) {
 			throw new IllegalStateException( "new persistence context is already connected to a session " );
 		}
 		persistenceContext.clear();
 		ObjectInputStream ois = null;
 		try {
 			ois = new ObjectInputStream( new ByteArrayInputStream( serializePersistenceContext( persistenceContextNew ) ) );
 			this.persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not deserialize the persistence context",  ex );
 		}
 		catch (ClassNotFoundException ex) {
 			throw new SerializationException( "could not deserialize the persistence context", ex );
 		}
 		finally {
 			try {
 				if (ois != null) ois.close();
 			}
 			catch (IOException ex) {}
 		}
 	}
 
 	private static byte[] serializePersistenceContext(StatefulPersistenceContext pc) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream( 512 );
 		ObjectOutputStream oos = null;
 		try {
 			oos = new ObjectOutputStream( baos );
 			( pc ).serialize( oos );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not serialize persistence context", ex );
 		}
 		finally {
 			if ( oos != null ) {
 				try {
 					oos.close();
 				}
 				catch( IOException ex ) {
 					//ignore
 				}
 			}
 		}
 		return baos.toByteArray();
 	}
 
 	private void replaceActionQueue(ActionQueue actionQueueNew) {
 		if ( actionQueue.hasAnyQueuedActions() ) {
 			throw new IllegalStateException( "cannot replace an ActionQueue with queued actions " );
 		}
 		actionQueue.clear();
 		ObjectInputStream ois = null;
 		try {
 			ois = new ObjectInputStream( new ByteArrayInputStream( serializeActionQueue( actionQueueNew ) ) );
 			actionQueue = ActionQueue.deserialize( ois, this );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not deserialize the action queue",  ex );
 		}
 		catch (ClassNotFoundException ex) {
 			throw new SerializationException( "could not deserialize the action queue", ex );
 		}
 		finally {
 			try {
 				if (ois != null) ois.close();
 			}
 			catch (IOException ex) {}
 		}
 	}
 
 	private static byte[] serializeActionQueue(ActionQueue actionQueue) {
 		ByteArrayOutputStream baos = new ByteArrayOutputStream( 512 );
 		ObjectOutputStream oos = null;
 		try {
 			oos = new ObjectOutputStream( baos );
 			actionQueue.serialize( oos );
 		}
 		catch (IOException ex) {
 			throw new SerializationException( "could not serialize action queue", ex );
 		}
 		finally {
 			if ( oos != null ) {
 				try {
 					oos.close();
 				}
 				catch( IOException ex ) {
 					//ignore
 				}
 			}
 		}
 		return baos.toByteArray();
 	}
 
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 	public void managedClose() {
         LOG.trace( "Automatically closing session" );
 		close();
 	}
 
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getDistinctConnectionProxy();
 	}
 
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isOpen();
 	}
 
 	public boolean isTransactionInProgress() {
 		checkTransactionSynchStatus();
 		return !isClosed() && transactionCoordinator.isTransactionInProgress();
 	}
 
 	@Override
 	public Connection disconnect() throws HibernateException {
 		errorIfClosed();
         LOG.debugf("Disconnecting session");
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
         LOG.debugf("Reconnecting session");
 		checkTransactionSynchStatus();
 		transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualReconnect( conn );
 	}
 
 	public void setAutoClear(boolean enabled) {
 		errorIfClosed();
 		autoClear = enabled;
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		errorIfClosed();
 		autoJoinTransactions = false;
 	}
 
 	/**
 	 * Check if there is a Hibernate or JTA transaction in progress and,
 	 * if there is not, flush if necessary, make sure the connection has
 	 * been committed (if it is not in autocommit mode) and run the after
 	 * completion processing
 	 */
 	public void afterOperation(boolean success) {
 		if ( ! transactionCoordinator.isTransactionInProgress() ) {
 			transactionCoordinator.afterNonTransactionalQuery( success );
 		}
 	}
 
 	@Override
 	public void afterTransactionBegin(TransactionImplementor hibernateTransaction) {
 		errorIfClosed();
 		interceptor.afterTransactionBegin( hibernateTransaction );
 	}
 
 	@Override
 	public void beforeTransactionCompletion(TransactionImplementor hibernateTransaction) {
 		LOG.trace( "before transaction completion" );
 		actionQueue.beforeTransactionCompletion();
 		if ( rootSession == null ) {
 			try {
 				interceptor.beforeTransactionCompletion( hibernateTransaction );
 			}
 			catch (Throwable t) {
                 LOG.exceptionInBeforeTransactionCompletionInterceptor(t);
 			}
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful) {
 		LOG.trace( "after transaction completion" );
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion( successful );
 		if ( rootSession == null && hibernateTransaction != null ) {
 			try {
 				interceptor.afterTransactionCompletion( hibernateTransaction );
 			}
 			catch (Throwable t) {
                 LOG.exceptionInAfterTransactionCompletionInterceptor(t);
 			}
 		}
 		if ( autoClear ) {
 			clear();
 		}
 	}
 
 	/**
 	 * clear all the internal collections, just
 	 * to help the garbage collector, does not
 	 * clear anything that is needed during the
 	 * afterTransactionCompletion() phase
 	 */
 	private void cleanup() {
 		persistenceContext.clear();
 	}
 
 	public LockMode getCurrentLockMode(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object == null ) {
 			throw new NullPointerException( "null object passed to getCurrentLockMode()" );
 		}
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation(this);
 			if ( object == null ) {
 				return LockMode.NONE;
 			}
 		}
 		EntityEntry e = persistenceContext.getEntry(object);
 		if ( e == null ) {
 			throw new TransientObjectException( "Given object not associated with the session" );
 		}
 		if ( e.getStatus() != Status.MANAGED ) {
 			throw new ObjectDeletedException(
 					"The given object was deleted",
 					e.getId(),
 					e.getPersister().getEntityName()
 				);
 		}
 		return e.getLockMode();
 	}
 
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		// todo : should this get moved to PersistentContext?
 		// logically, is PersistentContext the "thing" to which an interceptor gets attached?
 		final Object result = persistenceContext.getEntity(key);
 		if ( result == null ) {
 			final Object newObject = interceptor.getEntity( key.getEntityName(), key.getIdentifier() );
 			if ( newObject != null ) {
 				lock( newObject, LockMode.NONE );
 			}
 			return newObject;
 		}
 		else {
 			return result;
 		}
 	}
 
 
 	// saveOrUpdate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void saveOrUpdate(Object object) throws HibernateException {
 		saveOrUpdate( null, object );
 	}
 
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent( entityName, obj, this ) );
 	}
 
 	private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 	}
 
 	private <T> Iterable<T> listeners(EventType<T> type) {
 		return eventListenerGroup( type ).listeners();
 	}
 
 	private <T> EventListenerGroup<T> eventListenerGroup(EventType<T> type) {
 		return factory.getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
 	}
 
 
 	// save() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private Serializable fireSave(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		return event.getResultId();
 	}
 
 
 	// update() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void update(Object obj) throws HibernateException {
 		update(null, obj);
 	}
 
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private void fireUpdate(SaveOrUpdateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent(entityName, object, lockMode, this) );
 	}
 
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return new LockRequestImpl(lockOptions);
 	}
 
 	public void lock(Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent(object, lockMode, this) );
 	}
 
 	private void fireLock(String entityName, Object object, LockOptions options) {
 		fireLock( new LockEvent( entityName, object, options, this) );
 	}
 
 	private void fireLock( Object object, LockOptions options) {
 		fireLock( new LockEvent( object, options, this ) );
 	}
 
 	private void fireLock(LockEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LockEventListener listener : listeners( EventType.LOCK ) ) {
 			listener.onLock( event );
 		}
 	}
 
 
 	// persist() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persist(String entityName, Object object) throws HibernateException {
 		firePersist( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persist(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	public void persist(String entityName, Object object, Map copiedAlready)
 	throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersist(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event );
 		}
 	}
 
 
 	// persistOnFlush() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void persistOnFlush(String entityName, Object object)
 			throws HibernateException {
 		firePersistOnFlush( new PersistEvent( entityName, object, this ) );
 	}
 
 	public void persistOnFlush(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		firePersistOnFlush( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersistOnFlush(Map copiedAlready, PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 	}
 
 
 	// merge() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object merge(String entityName, Object object) throws HibernateException {
 		return fireMerge( new MergeEvent( entityName, object, this ) );
 	}
 
 	public Object merge(Object object) throws HibernateException {
 		return merge( null, object );
 	}
 
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private Object fireMerge(MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event );
 		}
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event, copiedAlready );
 		}
 	}
 
 
 	// delete() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Delete a persistent object
 	 */
 	public void delete(Object object) throws HibernateException {
 		fireDelete( new DeleteEvent(object, this) );
 	}
 
 	/**
 	 * Delete a persistent object (by explicit entity name)
 	 */
 	public void delete(String entityName, Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, this ) );
 	}
 
 	/**
 	 * Delete a persistent object
 	 */
 	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, isCascadeDeleteEnabled, this ), transientEntities );
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
 		}
 	}
 
 
 	// load()/get() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void load(Object object, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, object, this);
 		fireLoad( event, LoadEventListener.RELOAD );
 	}
 
 	public Object load(Class entityClass, Serializable id) throws HibernateException {
 		return load( entityClass.getName(), id );
 	}
 
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, false, this);
 		boolean success = false;
 		try {
 			fireLoad( event, LoadEventListener.LOAD );
 			if ( event.getResult() == null ) {
 				getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
 			}
 			success = true;
 			return event.getResult();
 		}
 		finally {
 			afterOperation(success);
 		}
 	}
 
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return get( entityClass.getName(), id );
 	}
 
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, false, this);
 		boolean success = false;
 		try {
 			fireLoad(event, LoadEventListener.GET);
 			success = true;
 			return event.getResult();
 		}
 		finally {
 			afterOperation(success);
 		}
 	}
 
 	/**
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
         if (LOG.isDebugEnabled()) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
             LOG.debugf("Initializing proxy: %s", MessageHelper.infoString(persister, id, getFactory()));
 		}
 
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, LoadEventListener.IMMEDIATE_LOAD);
 		return event.getResult();
 	}
 
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		// todo : remove
 		LoadEventListener.LoadType type = nullable
 				? LoadEventListener.INTERNAL_LOAD_NULLABLE
 				: eager
 						? LoadEventListener.INTERNAL_LOAD_EAGER
 						: LoadEventListener.INTERNAL_LOAD_LAZY;
 		LoadEvent event = new LoadEvent(id, entityName, true, this);
 		fireLoad(event, type);
 		if ( !nullable ) {
 			UnresolvableObjectException.throwIfNull( event.getResult(), id, entityName );
 		}
 		return event.getResult();
 	}
 
 	public Object load(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return load( entityClass.getName(), id, lockMode );
 	}
 
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return load( entityClass.getName(), id, lockOptions );
 	}
 
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 		fireLoad( event, LoadEventListener.LOAD );
 		return event.getResult();
 	}
 
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 		fireLoad( event, LoadEventListener.LOAD );
 		return event.getResult();
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return get( entityClass.getName(), id, lockOptions );
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockMode, this);
 	   	fireLoad(event, LoadEventListener.GET);
 		return event.getResult();
 	}
 
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		LoadEvent event = new LoadEvent(id, entityName, lockOptions, this);
 	   	fireLoad( event, LoadEventListener.GET );
 		return event.getResult();
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void refresh(Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, this) );
 	}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index 94271b19a3..ed673342d5 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,708 +1,707 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.StatelessSession;
 import org.hibernate.Transaction;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.StatefulPersistenceContext;
 import org.hibernate.engine.Versioning;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public class StatelessSessionImpl extends AbstractSessionImpl implements StatelessSession {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, StatelessSessionImpl.class.getName());
 
 	private TransactionCoordinator transactionCoordinator;
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 
 	StatelessSessionImpl(Connection connection, String tenantIdentifier, SessionFactoryImpl factory) {
 		super( factory, tenantIdentifier );
 		this.transactionCoordinator = new TransactionCoordinatorImpl( connection, this );
 	}
 
 	// TransactionContext ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return factory.getTransactionEnvironment();
 	}
 
 	// inserts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Serializable insert(Object entity) {
 		errorIfClosed();
 		return insert(null, entity);
 	}
 
 	public Serializable insert(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifierGenerator().generate(this, entity);
 		Object[] state = persister.getPropertyValues(entity, EntityMode.POJO);
 		if ( persister.isVersioned() ) {
 			boolean substitute = Versioning.seedVersion(state, persister.getVersionProperty(), persister.getVersionType(), this);
 			if ( substitute ) {
 				persister.setPropertyValues( entity, state, EntityMode.POJO );
 			}
 		}
 		if ( id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			id = persister.insert(state, entity, this);
 		}
 		else {
 			persister.insert(id, state, entity, this);
 		}
 		persister.setIdentifier( entity, id, this );
 		return id;
 	}
 
 
 	// deletes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void delete(Object entity) {
 		errorIfClosed();
 		delete(null, entity);
 	}
 
 	public void delete(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion(entity, EntityMode.POJO);
 		persister.delete(id, version, entity, this);
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void update(Object entity) {
 		errorIfClosed();
 		update(null, entity);
 	}
 
 	public void update(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object[] state = persister.getPropertyValues(entity, EntityMode.POJO);
 		Object oldVersion;
 		if ( persister.isVersioned() ) {
 			oldVersion = persister.getVersion(entity, EntityMode.POJO);
 			Object newVersion = Versioning.increment( oldVersion, persister.getVersionType(), this );
 			Versioning.setVersion(state, newVersion, persister);
 			persister.setPropertyValues(entity, state, EntityMode.POJO);
 		}
 		else {
 			oldVersion = null;
 		}
 		persister.update(id, state, null, false, null, oldVersion, entity, null, this);
 	}
 
 
 	// loading ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object get(Class entityClass, Serializable id) {
 		return get( entityClass.getName(), id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	public Object get(String entityName, Serializable id) {
 		return get(entityName, id, LockMode.NONE);
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		errorIfClosed();
 		Object result = getFactory().getEntityPersister(entityName)
 				.load(id, null, lockMode, this);
 		if ( temporaryPersistenceContext.isLoadFinished() ) {
 			temporaryPersistenceContext.clear();
 		}
 		return result;
 	}
 
 	public void refresh(Object entity) {
 		refresh( bestGuessEntityName( entity ), entity, LockMode.NONE );
 	}
 
 	public void refresh(String entityName, Object entity) {
 		refresh( entityName, entity, LockMode.NONE );
 	}
 
 	public void refresh(Object entity, LockMode lockMode) {
 		refresh( bestGuessEntityName( entity ), entity, lockMode );
 	}
 
 	public void refresh(String entityName, Object entity, LockMode lockMode) {
 		final EntityPersister persister = this.getEntityPersister( entityName, entity );
 		final Serializable id = persister.getIdentifier( entity, this );
         if (LOG.isTraceEnabled()) LOG.trace("Refreshing transient " + MessageHelper.infoString(persister, id, this.getFactory()));
 		// TODO : can this ever happen???
 //		EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 //		if ( source.getPersistenceContext().getEntry( key ) != null ) {
 //			throw new PersistentObjectException(
 //					"attempted to refresh transient instance when persistent " +
 //					"instance was already associated with the Session: " +
 //					MessageHelper.infoString( persister, id, source.getFactory() )
 //			);
 //		}
 
 		if ( persister.hasCache() ) {
 			final CacheKey ck = generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 			persister.getCacheAccessStrategy().evict( ck );
 		}
 
 		String previousFetchProfile = this.getFetchProfile();
 		Object result = null;
 		try {
 			this.setFetchProfile( "refresh" );
 			result = persister.load( id, entity, lockMode, this );
 		}
 		finally {
 			this.setFetchProfile( previousFetchProfile );
 		}
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 	}
 
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException("proxies cannot be fetched by a stateless session");
 	}
 
 	public void initializeCollection(
 			PersistentCollection collection,
 	        boolean writing) throws HibernateException {
 		throw new SessionException("collections cannot be fetched by a stateless session");
 	}
 
 	public Object instantiate(
 			String entityName,
 	        Serializable id) throws HibernateException {
 		errorIfClosed();
 		return getFactory().getEntityPersister( entityName )
 				.instantiate( id, this );
 	}
 
 	public Object internalLoad(
 			String entityName,
 	        Serializable id,
 	        boolean eager,
 	        boolean nullable) throws HibernateException {
 		errorIfClosed();
 		EntityPersister persister = getFactory().getEntityPersister( entityName );
 		// first, try to load it from the temp PC associated to this SS
 		Object loaded = temporaryPersistenceContext.getEntity( generateEntityKey( id, persister ) );
 		if ( loaded != null ) {
 			// we found it in the temp PC.  Should indicate we are in the midst of processing a result set
 			// containing eager fetches via join fetch
 			return loaded;
 		}
 		if ( !eager && persister.hasProxy() ) {
 			// if the metadata allowed proxy creation and caller did not request forceful eager loading,
 			// generate a proxy
 			return persister.createProxy( id, this );
 		}
 		// otherwise immediately materialize it
 		return get( entityName, id );
 	}
 
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
 	public void close() {
 		managedClose();
 	}
 
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return factory.getSettings().getConnectionReleaseMode();
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
 	public boolean isAutoCloseSessionEnabled() {
 		return factory.getSettings().isAutoCloseSessionEnabled();
 	}
 
 	public boolean isFlushBeforeCompletionEnabled() {
 		return true;
 	}
 
 	public boolean isFlushModeNever() {
 		return false;
 	}
 
 	public void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		transactionCoordinator.close();
 		setClosed();
 	}
 
 	public void managedFlush() {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 	}
 
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 	@Override
 	public void afterTransactionBegin(TransactionImplementor hibernateTransaction) {
 		// nothing to do here
 	}
 
 	@Override
 	public void beforeTransactionCompletion(TransactionImplementor hibernateTransaction) {
 		// nothing to do here
 	}
 
 	@Override
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful) {
 		// nothing to do here
 	}
 
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName(object);
 	}
 
 	public Connection connection() {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getDistinctConnectionProxy();
 	}
 
 	public int executeUpdate(String query, QueryParameters queryParameters)
 			throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
 	public Map getEnabledFilters() {
 		return CollectionHelper.EMPTY_MAP;
 	}
 
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	public EntityPersister getEntityPersister(String entityName, Object object)
 			throws HibernateException {
 		errorIfClosed();
 		if ( entityName==null ) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			return factory.getEntityPersister( entityName )
 					.getSubclassEntityPersister( object, getFactory(), EntityMode.POJO );
 		}
 	}
 
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		return null;
 	}
 
 	public Type getFilterParameterType(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object getFilterParameterValue(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public FlushMode getFlushMode() {
 		return FlushMode.COMMIT;
 	}
 
 	public Interceptor getInterceptor() {
 		return EmptyInterceptor.INSTANCE;
 	}
 
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
 	public long getTimestamp() {
 		throw new UnsupportedOperationException();
 	}
 
 	public String guessEntityName(Object entity) throws HibernateException {
 		errorIfClosed();
 		return entity.getClass().getName();
 	}
 
 
 	public boolean isConnected() {
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isPhysicallyConnected();
 	}
 
 	public boolean isTransactionInProgress() {
 		return transactionCoordinator.isTransactionInProgress();
 	}
 
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		throw new UnsupportedOperationException();
 	}
 
 	public void setCacheMode(CacheMode cm) {
 		throw new UnsupportedOperationException();
 	}
 
 	public void setFlushMode(FlushMode fm) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	public boolean isEventSource() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
 		if ( readOnly == true ) {
 			throw new UnsupportedOperationException();
 		}
 	}
 
 /////////////////////////////////////////////////////////////////////////////////////////////////////
 
 	//TODO: COPY/PASTE FROM SessionImpl, pull up!
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		List results = CollectionHelper.EMPTY_LIST;
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public void afterOperation(boolean success) {
 		if ( ! transactionCoordinator.isTransactionInProgress() ) {
 			transactionCoordinator.afterNonTransactionalQuery( success );;
 		}
 	}
 
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
 		errorIfClosed();
 		String entityName = criteria.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 		        factory,
 		        criteria,
 		        entityName,
 		        getLoadQueryInfluencers()
 		);
 		return loader.scroll(this, scrollMode);
 	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
 		errorIfClosed();
 		String[] implementors = factory.getImplementors( criteria.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		for( int i=0; i <size; i++ ) {
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 			        factory,
 			        criteria,
 			        implementors[i],
 			        getLoadQueryInfluencers()
 			);
 		}
 
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 		try {
 			for( int i=0; i<size; i++ ) {
 				final List currentResults = loaders[i].list(this);
 				currentResults.addAll(results);
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		boolean success = false;
 		List results;
 		try {
 			results = loader.list(this, queryParameters);
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		return plan.performScroll( queryParameters, this );
 	}
 
 	public void afterScrollOperation() {
 		temporaryPersistenceContext.clear();
 	}
 
 	public void flush() {}
 
 	public NonFlushedChanges getNonFlushedChanges() {
 		throw new UnsupportedOperationException();
 	}
 
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) {
 		throw new UnsupportedOperationException();
 	}
 
 	public String getFetchProfile() {
 		return null;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return LoadQueryInfluencers.NONE;
 	}
 
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		errorIfClosed();
 		// nothing to do
 	}
 
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
 		errorIfClosed();
 		// not in any meaning we need to worry about here.
 		return false;
 	}
 
 	public void setFetchProfile(String name) {}
 
 	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
 		// no auto-flushing to support in stateless session
 		return false;
 	}
 
 	public int executeNativeUpdate(NativeSQLQuerySpecification nativeSQLQuerySpecification,
 			QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		NativeSQLQueryPlan plan = getNativeSQLQueryPlan(nativeSQLQuerySpecification);
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate(queryParameters, this);
 			success = true;
 		} finally {
 			afterOperation(success);
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/TransactionEnvironmentImpl.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/TransactionEnvironmentImpl.java
index 6e96517276..a5e2f044de 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/TransactionEnvironmentImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/TransactionEnvironmentImpl.java
@@ -1,72 +1,72 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class TransactionEnvironmentImpl implements TransactionEnvironment {
 	private final SessionFactoryImpl sessionFactory;
 
 	public TransactionEnvironmentImpl(SessionFactoryImpl sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	protected ServiceRegistry serviceRegistry() {
 		return sessionFactory.getServiceRegistry();
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return serviceRegistry().getService( JdbcServices.class );
 	}
 
 	@Override
 	public JtaPlatform getJtaPlatform() {
 		return serviceRegistry().getService( JtaPlatform.class );
 	}
 
 	@Override
 	public TransactionFactory getTransactionFactory() {
 		return serviceRegistry().getService( TransactionFactory.class );
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return sessionFactory.getStatisticsImplementor();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
index 27de6d4a32..3b6e2df986 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/TypeLocatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/TypeLocatorImpl.java
@@ -1,184 +1,184 @@
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
-package org.hibernate.impl;
+package org.hibernate.internal;
 import java.io.Serializable;
 import java.util.Properties;
 import org.hibernate.TypeHelper;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 
 /**
  * Implementation of {@link org.hibernate.TypeHelper}
  *
  * @todo Do we want to cache the results of {@link #entity}, {@link #custom} and {@link #any} ?
  *
  * @author Steve Ebersole
  */
 public class TypeLocatorImpl implements TypeHelper, Serializable {
 	private final TypeResolver typeResolver;
 
 	public TypeLocatorImpl(TypeResolver typeResolver) {
 		this.typeResolver = typeResolver;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public BasicType basic(String name) {
 		return typeResolver.basic( name );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public BasicType basic(Class javaType) {
 		BasicType type = typeResolver.basic( javaType.getName() );
 		if ( type == null ) {
 			final Class variant = resolvePrimitiveOrPrimitiveWrapperVariantJavaType( javaType );
 			if ( variant != null ) {
 				type = typeResolver.basic( variant.getName() );
 			}
 		}
 		return type;
 	}
 
 	private Class resolvePrimitiveOrPrimitiveWrapperVariantJavaType(Class javaType) {
 		// boolean
 		if ( Boolean.TYPE.equals( javaType ) ) {
 			return Boolean.class;
 		}
 		if ( Boolean.class.equals( javaType ) ) {
 			return Boolean.TYPE;
 		}
 
 		// char
 		if ( Character.TYPE.equals( javaType ) ) {
 			return Character.class;
 		}
 		if ( Character.class.equals( javaType ) ) {
 			return Character.TYPE;
 		}
 
 		// byte
 		if ( Byte.TYPE.equals( javaType ) ) {
 			return Byte.class;
 		}
 		if ( Byte.class.equals( javaType ) ) {
 			return Byte.TYPE;
 		}
 
 		// short
 		if ( Short.TYPE.equals( javaType ) ) {
 			return Short.class;
 		}
 		if ( Short.class.equals( javaType ) ) {
 			return Short.TYPE;
 		}
 
 		// int
 		if ( Integer.TYPE.equals( javaType ) ) {
 			return Integer.class;
 		}
 		if ( Integer.class.equals( javaType ) ) {
 			return Integer.TYPE;
 		}
 
 		// long
 		if ( Long.TYPE.equals( javaType ) ) {
 			return Long.class;
 		}
 		if ( Long.class.equals( javaType ) ) {
 			return Long.TYPE;
 		}
 
 		// float
 		if ( Float.TYPE.equals( javaType ) ) {
 			return Float.class;
 		}
 		if ( Float.class.equals( javaType ) ) {
 			return Float.TYPE;
 		}
 
 		// double
 		if ( Double.TYPE.equals( javaType ) ) {
 			return Double.class;
 		}
 		if ( Double.class.equals( javaType ) ) {
 			return Double.TYPE;
 		}
 
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type heuristicType(String name) {
 		return typeResolver.heuristicType( name );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type entity(Class entityClass) {
 		return entity( entityClass.getName() );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type entity(String entityName) {
 		return typeResolver.getTypeFactory().manyToOne( entityName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Type custom(Class userTypeClass) {
 		return custom( userTypeClass, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Type custom(Class userTypeClass, Properties parameters) {
 		if ( CompositeUserType.class.isAssignableFrom( userTypeClass ) ) {
 			return typeResolver.getTypeFactory().customComponent( userTypeClass, parameters );
 		}
 		else {
 			return typeResolver.getTypeFactory().custom( userTypeClass, parameters );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type any(Type metaType, Type identifierType) {
 		return typeResolver.getTypeFactory().any( metaType, identifierType );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/package.html b/hibernate-core/src/main/java/org/hibernate/internal/package.html
similarity index 80%
rename from hibernate-core/src/main/java/org/hibernate/impl/package.html
rename to hibernate-core/src/main/java/org/hibernate/internal/package.html
index 50ec07f8db..e12b49307c 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/internal/package.html
@@ -1,34 +1,32 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
 
 <html>
 <head></head>
 <body>
 <p>
-	This package contains implementations of the
-	central Hibernate APIs, especially the
-	Hibernate session.
+    An internal package containing mostly implementations of central Hibernate APIs of the
+    {@link org.hibernate} package.
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
index 397725521e..78c533e50d 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/SessionFactoryStub.java
@@ -1,223 +1,223 @@
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
 package org.hibernate.jmx;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.InvalidObjectException;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.StatelessSession;
 import org.hibernate.TypeHelper;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
-import org.hibernate.impl.SessionFactoryObjectFactory;
+import org.hibernate.internal.SessionFactoryObjectFactory;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.stat.Statistics;
 
 /**
  * A flyweight for <tt>SessionFactory</tt>. If the MBean itself does not
  * have classpath to the persistent classes, then a stub will be registered
  * with JNDI and the actual <tt>SessionFactoryImpl</tt> built upon first
  * access.
  * @author Gavin King
  */
 public class SessionFactoryStub implements SessionFactory {
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryStub.class.getName());
 
 	private transient SessionFactory impl;
 	private transient HibernateService service;
 	private String uuid;
 	private String name;
 
 	SessionFactoryStub(HibernateService service) {
 		this.service = service;
 		this.name = service.getJndiName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
 		SessionFactoryObjectFactory.addInstance( uuid, name, this, service.getProperties() );
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return getImpl().withOptions();
 	}
 
 	public Session openSession() throws HibernateException {
 		return getImpl().openSession();
 	}
 
 	public Session getCurrentSession() {
 		return getImpl().getCurrentSession();
 	}
 
 	private synchronized SessionFactory getImpl() {
 		if (impl==null) impl = service.buildSessionFactory();
 		return impl;
 	}
 
 	//readResolveObject
 	private Object readResolve() throws ObjectStreamException {
 		// look for the instance by uuid
 		Object result = SessionFactoryObjectFactory.getInstance(uuid);
 		if (result==null) {
             // in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryObjectFactory.getNamedInstance(name);
             if (result == null) throw new InvalidObjectException("Could not find a stub SessionFactory named: " + name);
             LOG.debugf("Resolved stub SessionFactory by name");
         } else LOG.debugf("Resolved stub SessionFactory by uid");
 		return result;
 	}
 
 	/**
 	 * @see javax.naming.Referenceable#getReference()
 	 */
 	public Reference getReference() throws NamingException {
 		return new Reference(
 			SessionFactoryStub.class.getName(),
 			new StringRefAddr("uuid", uuid),
 			SessionFactoryObjectFactory.class.getName(),
 			null
 		);
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getImpl().getClassMetadata(persistentClass);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName)
 	throws HibernateException {
 		return getImpl().getClassMetadata(entityName);
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return getImpl().getCollectionMetadata(roleName);
 	}
 
 	public Map<String,ClassMetadata> getAllClassMetadata() throws HibernateException {
 		return getImpl().getAllClassMetadata();
 	}
 
 	public Map getAllCollectionMetadata() throws HibernateException {
 		return getImpl().getAllCollectionMetadata();
 	}
 
 	public void close() throws HibernateException {
 	}
 
 	public boolean isClosed() {
 		return false;
 	}
 
 	public Cache getCache() {
 		return getImpl().getCache();
 	}
 
 	public void evict(Class persistentClass, Serializable id)
 		throws HibernateException {
 		getImpl().evict(persistentClass, id);
 	}
 
 	public void evict(Class persistentClass) throws HibernateException {
 		getImpl().evict(persistentClass);
 	}
 
 	public void evictEntity(String entityName, Serializable id)
 	throws HibernateException {
 		getImpl().evictEntity(entityName, id);
 	}
 
 	public void evictEntity(String entityName) throws HibernateException {
 		getImpl().evictEntity(entityName);
 	}
 
 	public void evictCollection(String roleName, Serializable id)
 		throws HibernateException {
 		getImpl().evictCollection(roleName, id);
 	}
 
 	public void evictCollection(String roleName) throws HibernateException {
 		getImpl().evictCollection(roleName);
 	}
 
 	public void evictQueries() throws HibernateException {
 		getImpl().evictQueries();
 	}
 
 	public void evictQueries(String cacheRegion) throws HibernateException {
 		getImpl().evictQueries(cacheRegion);
 	}
 
 	public Statistics getStatistics() {
 		return getImpl().getStatistics();
 	}
 
 	public StatelessSession openStatelessSession() {
 		return getImpl().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection conn) {
 		return getImpl().openStatelessSession(conn);
 	}
 
 	public Set getDefinedFilterNames() {
 		return getImpl().getDefinedFilterNames();
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		return getImpl().getFilterDefinition( filterName );
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return getImpl().containsFetchProfileDefinition( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return getImpl().getTypeHelper();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
index 7626d58cb0..b57fc2b286 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
@@ -1,317 +1,317 @@
 //$Id: StatisticsService.java 8262 2005-09-30 07:48:53Z oneovthafew $
 package org.hibernate.jmx;
 import javax.naming.InitialContext;
 import javax.naming.NameNotFoundException;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.SessionFactory;
-import org.hibernate.impl.SessionFactoryObjectFactory;
+import org.hibernate.internal.SessionFactoryObjectFactory;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.stat.EntityStatistics;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
 
 import org.jboss.logging.Logger;
 
 /**
  * JMX service for Hibernate statistics<br>
  * <br>
  * Register this MBean in your JMX server for a specific session factory
  * <pre>
  * //build the ObjectName you want
  * Hashtable tb = new Hashtable();
  * tb.put("type", "statistics");
  * tb.put("sessionFactory", "myFinancialApp");
  * ObjectName on = new ObjectName("hibernate", tb);
  * StatisticsService stats = new StatisticsService();
  * stats.setSessionFactory(sessionFactory);
  * server.registerMBean(stats, on);
  * </pre>
  * And call the MBean the way you want<br>
  * <br>
  * Register this MBean in your JMX server with no specific session factory
  * <pre>
  * //build the ObjectName you want
  * Hashtable tb = new Hashtable();
  * tb.put("type", "statistics");
  * tb.put("sessionFactory", "myFinancialApp");
  * ObjectName on = new ObjectName("hibernate", tb);
  * StatisticsService stats = new StatisticsService();
  * server.registerMBean(stats, on);
  * </pre>
  * And call the MBean by providing the <code>SessionFactoryJNDIName</code> first.
  * Then the session factory will be retrieved from JNDI and the statistics
  * loaded.
  *
  * @author Emmanuel Bernard
  */
 public class StatisticsService implements StatisticsServiceMBean {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, StatisticsService.class.getName());
 	//TODO: We probably should have a StatisticsNotPublishedException, to make it clean
 
 	SessionFactory sf;
 	String sfJNDIName;
 	Statistics stats = new ConcurrentStatisticsImpl();
 
 	/**
 	 * @see StatisticsServiceMBean#setSessionFactoryJNDIName(java.lang.String)
 	 */
 	public void setSessionFactoryJNDIName(String sfJNDIName) {
 		this.sfJNDIName = sfJNDIName;
 		try {
 			Object obj = new InitialContext().lookup(sfJNDIName);
 			if (obj instanceof Reference) {
 				Reference ref = (Reference) obj;
 				setSessionFactory( (SessionFactory) SessionFactoryObjectFactory.getInstance( (String) ref.get(0).getContent() ) );
 			}
 			else {
 				setSessionFactory( (SessionFactory) obj );
 			}
 		}
 		catch (NameNotFoundException e) {
             LOG.noSessionFactoryWithJndiName(sfJNDIName, e);
 			setSessionFactory(null);
 		}
 		catch (NamingException e) {
             LOG.unableToAccessSessionFactory(sfJNDIName, e);
 			setSessionFactory(null);
 		}
 		catch (ClassCastException e) {
             LOG.jndiNameDoesNotHandleSessionFactoryReference(sfJNDIName, e);
 			setSessionFactory(null);
 		}
 	}
 
 	/**
 	 * Useful to init this MBean wo a JNDI session factory name
 	 *
 	 * @param sf session factory to register
 	 */
 	public void setSessionFactory(SessionFactory sf) {
 		this.sf = sf;
 		if (sf == null) {
 			stats = new ConcurrentStatisticsImpl();
 		}
 		else {
 			stats = sf.getStatistics();
 		}
 
 	}
 	/**
 	 * @see StatisticsServiceMBean#clear()
 	 */
 	public void clear() {
 		stats.clear();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityStatistics(java.lang.String)
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		return stats.getEntityStatistics(entityName);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionStatistics(java.lang.String)
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		return stats.getCollectionStatistics(role);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheStatistics(java.lang.String)
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		return stats.getSecondLevelCacheStatistics(regionName);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getQueryStatistics(java.lang.String)
 	 */
 	public QueryStatistics getQueryStatistics(String hql) {
 		return stats.getQueryStatistics(hql);
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityDeleteCount()
 	 */
 	public long getEntityDeleteCount() {
 		return stats.getEntityDeleteCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityInsertCount()
 	 */
 	public long getEntityInsertCount() {
 		return stats.getEntityInsertCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityLoadCount()
 	 */
 	public long getEntityLoadCount() {
 		return stats.getEntityLoadCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityFetchCount()
 	 */
 	public long getEntityFetchCount() {
 		return stats.getEntityFetchCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getEntityUpdateCount()
 	 */
 	public long getEntityUpdateCount() {
 		return stats.getEntityUpdateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getQueryExecutionCount()
 	 */
 	public long getQueryExecutionCount() {
 		return stats.getQueryExecutionCount();
 	}
 	public long getQueryCacheHitCount() {
 		return stats.getQueryCacheHitCount();
 	}
 	public long getQueryExecutionMaxTime() {
 		return stats.getQueryExecutionMaxTime();
 	}
 	public long getQueryCacheMissCount() {
 		return stats.getQueryCacheMissCount();
 	}
 	public long getQueryCachePutCount() {
 		return stats.getQueryCachePutCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getFlushCount()
 	 */
 	public long getFlushCount() {
 		return stats.getFlushCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getConnectCount()
 	 */
 	public long getConnectCount() {
 		return stats.getConnectCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheHitCount()
 	 */
 	public long getSecondLevelCacheHitCount() {
 		return stats.getSecondLevelCacheHitCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCacheMissCount()
 	 */
 	public long getSecondLevelCacheMissCount() {
 		return stats.getSecondLevelCacheMissCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSecondLevelCachePutCount()
 	 */
 	public long getSecondLevelCachePutCount() {
 		return stats.getSecondLevelCachePutCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSessionCloseCount()
 	 */
 	public long getSessionCloseCount() {
 		return stats.getSessionCloseCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getSessionOpenCount()
 	 */
 	public long getSessionOpenCount() {
 		return stats.getSessionOpenCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionLoadCount()
 	 */
 	public long getCollectionLoadCount() {
 		return stats.getCollectionLoadCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionFetchCount()
 	 */
 	public long getCollectionFetchCount() {
 		return stats.getCollectionFetchCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionUpdateCount()
 	 */
 	public long getCollectionUpdateCount() {
 		return stats.getCollectionUpdateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionRemoveCount()
 	 */
 	public long getCollectionRemoveCount() {
 		return stats.getCollectionRemoveCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getCollectionRecreateCount()
 	 */
 	public long getCollectionRecreateCount() {
 		return stats.getCollectionRecreateCount();
 	}
 	/**
 	 * @see StatisticsServiceMBean#getStartTime()
 	 */
 	public long getStartTime() {
 		return stats.getStartTime();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#isStatisticsEnabled()
 	 */
 	public boolean isStatisticsEnabled() {
 		return stats.isStatisticsEnabled();
 	}
 
 	/**
 	 * @see StatisticsServiceMBean#setStatisticsEnabled(boolean)
 	 */
 	public void setStatisticsEnabled(boolean enable) {
 		stats.setStatisticsEnabled(enable);
 	}
 
 	public void logSummary() {
 		stats.logSummary();
 	}
 
 	public String[] getCollectionRoleNames() {
 		return stats.getCollectionRoleNames();
 	}
 
 	public String[] getEntityNames() {
 		return stats.getEntityNames();
 	}
 
 	public String[] getQueries() {
 		return stats.getQueries();
 	}
 
 	public String[] getSecondLevelCacheRegionNames() {
 		return stats.getSecondLevelCacheRegionNames();
 	}
 
 	public long getSuccessfulTransactionCount() {
 		return stats.getSuccessfulTransactionCount();
 	}
 	public long getTransactionCount() {
 		return stats.getTransactionCount();
 	}
 
 	public long getCloseStatementCount() {
 		return stats.getCloseStatementCount();
 	}
 	public long getPrepareStatementCount() {
 		return stats.getPrepareStatementCount();
 	}
 
 	public long getOptimisticFailureCount() {
 		return stats.getOptimisticFailureCount();
 	}
 
 	public String getQueryExecutionMaxTimeQueryString() {
 		return stats.getQueryExecutionMaxTimeQueryString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 06eb8de82e..4f3a876807 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1073 +1,1073 @@
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
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.FilterKey;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.QueryKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.EntityUniqueKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.hql.HolderInstantiator;
-import org.hibernate.impl.FetchingScrollableResultsImpl;
-import org.hibernate.impl.ScrollableResultsImpl;
+import org.hibernate.internal.FetchingScrollableResultsImpl;
+import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Abstract superclass of object loading (and querying) strategies. This class implements
  * useful common functionality that concrete loaders delegate to. It is not intended that this
  * functionality would be directly accessed by client code. (Hence, all methods of this class
  * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
  * <tt>Loadable</tt> interface, which is the contract between this class and
  * <tt>EntityPersister</tt>s that may be loaded by it.<br>
  * <br>
  * The present implementation is able to load any number of columns of entities and at most
  * one collection role per query.
  *
  * @author Gavin King
  * @see org.hibernate.persister.entity.Loadable
  */
 public abstract class Loader {
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	protected abstract String getSQLString();
 
 	/**
 	 * An array of persisters of entity classes contained in each row of results;
 	 * implemented by all subclasses
 	 *
 	 * @return The entity persisters.
 	 */
 	protected abstract Loadable[] getEntityPersisters();
 
 	/**
 	 * An array indicating whether the entities have eager property fetching
 	 * enabled.
 	 *
 	 * @return Eager property fetching indicators.
 	 */
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return null;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner").  The
 	 * indexes contained here are relative to the result of
 	 * {@link #getEntityPersisters}.
 	 *
 	 * @return The owner indicators (see discussion above).
 	 */
 	protected int[] getOwners() {
 		return null;
 	}
 
 	/**
 	 * An array of the owner types corresponding to the {@link #getOwners()}
 	 * returns.  Indices indicating no owner would be null here.
 	 *
 	 * @return The types for the owners.
 	 */
 	protected EntityType[] getOwnerAssociationTypes() {
 		return null;
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only
 	 * collection loaders return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return null;
 	}
 
 	/**
 	 * Get the index of the entity that owns the collection, or -1
 	 * if there is no owner in the query results (ie. in the case of a
 	 * collection initializer) or no collection.
 	 */
 	protected int[] getCollectionOwners() {
 		return null;
 	}
 
 	protected int[][] getCompositeKeyManyToOneTargetIndices() {
 		return null;
 	}
 
 	/**
 	 * What lock options does this load entities with?
 	 *
 	 * @param lockOptions a collection of lock options specified dynamically via the Query interface
 	 */
 	//protected abstract LockOptions[] getLockOptions(Map lockOptions);
 	protected abstract LockMode[] getLockModes(LockOptions lockOptions);
 
 	/**
 	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
 	 * empty superclass implementation merely returns its first
 	 * argument.
 	 */
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws HibernateException {
 		return sql;
 	}
 
 	/**
 	 * Does this query return objects that might be already cached
 	 * by the session, whose lock mode may need upgrading
 	 */
 	protected boolean upgradeLocks() {
 		return false;
 	}
 
 	/**
 	 * Return false is this loader is a batch entity loader
 	 */
 	protected boolean isSingleRowLoader() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL table aliases of entities whose
 	 * associations are subselect-loadable, returning
 	 * null if this loader does not support subselect
 	 * loading
 	 */
 	protected String[] getAliases() {
 		return null;
 	}
 
 	/**
 	 * Modify the SQL, adding lock hints and comments, if necessary
 	 */
 	protected String preprocessSQL(String sql, QueryParameters parameters, Dialect dialect)
 			throws HibernateException {
 
 		sql = applyLocks( sql, parameters.getLockOptions(), dialect );
 
 		return getFactory().getSettings().isCommentsEnabled() ?
 				prependComment( sql, parameters ) : sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuffer( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	private List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	private List doQueryAndInitializeNonLazyCollections(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException, SQLException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
 		if ( queryParameters.isReadOnlyInitialized() ) {
 			// The read-only/modifiable mode for the query was explicitly set.
 			// Temporarily set the default read-only/modifiable setting to the query's setting.
 			persistenceContext.setDefaultReadOnly( queryParameters.isReadOnly() );
 		}
 		else {
 			// The read-only/modifiable setting for the query was not initialized.
 			// Use the default read-only/modifiable from the persistence context instead.
 			queryParameters.setReadOnly( persistenceContext.isDefaultReadOnly() );
 		}
 		persistenceContext.beforeLoad();
 		List result;
 		try {
 			try {
 				result = doQuery( session, queryParameters, returnProxies, forcedResultTransformer );
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 		return result;
 	}
 
 	/**
 	 * Loads a single row from the result set.  This is the processing used from the
 	 * ScrollableResults where no collection fetches were encountered.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		final Object result;
 		try {
 			result = getRowFromResultSet(
 			        resultSet,
 					session,
 					queryParameters,
 					getLockModes( queryParameters.getLockOptions() ),
 					null,
 					hydratedObjects,
 					new EntityKey[entitySpan],
 					returnProxies
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not read next row of results",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 			);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	private Object sequentialLoad(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final EntityKey keyToRead) throws HibernateException {
 
 		final int entitySpan = getEntityPersisters().length;
 		final List hydratedObjects = entitySpan == 0 ?
 				null : new ArrayList( entitySpan );
 
 		Object result = null;
 		final EntityKey[] loadedKeys = new EntityKey[entitySpan];
 
 		try {
 			do {
 				Object loaded = getRowFromResultSet(
 						resultSet,
 						session,
 						queryParameters,
 						getLockModes( queryParameters.getLockOptions() ),
 						null,
 						hydratedObjects,
 						loadedKeys,
 						returnProxies
 					);
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( keyToRead.equals( loadedKeys[0] ) && resultSet.next() );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSet,
 				session,
 				queryParameters.isReadOnly( session )
 			);
 		session.getPersistenceContext().initializeNonLazyCollections();
 		return result;
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isAfterLast() ) {
 				// don't even bother trying to read further
 				return null;
 			}
 
 			if ( resultSet.isBeforeFirst() ) {
 				resultSet.next();
 			}
 
 			// We call getKeyFromResultSet() here so that we can know the
 			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
 			// it is also then called from getRowFromResultSet() which is certainly
 			// not the most efficient.  But the call here is needed, and there
 			// currently is no other way without refactoring of the doQuery()/getRowFromResultSet()
 			// methods
 			final EntityKey currentKey = getKeyFromResultSet(
 					0,
 					getEntityPersisters()[0],
 					null,
 					resultSet,
 					session
 				);
 
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, currentKey );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	/**
 	 * Loads a single logical row from the result set moving forward.  This is the
 	 * processing used from the ScrollableResults where there were collection fetches
 	 * encountered; thus a single logical row may have multiple rows in the underlying
 	 * result set.
 	 *
 	 * @param resultSet The result set from which to do the load.
 	 * @param session The session from which the request originated.
 	 * @param queryParameters The query parameters specified by the user.
 	 * @param returnProxies Should proxies be generated
 	 * @return The loaded "row".
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final boolean returnProxies,
 	        final boolean isLogicallyAfterLast) throws HibernateException {
 
 		// note that for sequential scrolling, we make the assumption that
 		// the first persister element is the "root entity"
 
 		try {
 			if ( resultSet.isFirst() ) {
 				// don't even bother trying to read any further
 				return null;
 			}
 
 			EntityKey keyToRead = null;
 			// This check is needed since processing leaves the cursor
 			// after the last physical row for the current logical row;
 			// thus if we are after the last physical row, this might be
 			// caused by either:
 			//      1) scrolling to the last logical row
 			//      2) scrolling past the last logical row
 			// In the latter scenario, the previous logical row
 			// really is the last logical row.
 			//
 			// In all other cases, we should process back two
 			// logical records (the current logic row, plus the
 			// previous logical row).
 			if ( resultSet.isAfterLast() && isLogicallyAfterLast ) {
 				// position cursor to the last row
 				resultSet.last();
 				keyToRead = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 			}
 			else {
 				// Since the result set cursor is always left at the first
 				// physical row after the "last processed", we need to jump
 				// back one position to get the key value we are interested
 				// in skipping
 				resultSet.previous();
 
 				// sequentially read the result set in reverse until we recognize
 				// a change in the key value.  At that point, we are pointed at
 				// the last physical sequential row for the logical row in which
 				// we are interested in processing
 				boolean firstPass = true;
 				final EntityKey lastKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 				while ( resultSet.previous() ) {
 					EntityKey checkKey = getKeyFromResultSet(
 							0,
 							getEntityPersisters()[0],
 							null,
 							resultSet,
 							session
 						);
 
 					if ( firstPass ) {
 						firstPass = false;
 						keyToRead = checkKey;
 					}
 
 					if ( !lastKey.equals( checkKey ) ) {
 						break;
 					}
 				}
 
 			}
 
 			// Read backwards until we read past the first physical sequential
 			// row with the key we are interested in loading
 			while ( resultSet.previous() ) {
 				EntityKey checkKey = getKeyFromResultSet(
 						0,
 						getEntityPersisters()[0],
 						null,
 						resultSet,
 						session
 					);
 
 				if ( !keyToRead.equals( checkKey ) ) {
 					break;
 				}
 			}
 
 			// Finally, read ahead one row to position result set cursor
 			// at the first physical row we are interested in loading
 			resultSet.next();
 
 			// and doAfterTransactionCompletion the load
 			return sequentialLoad( resultSet, session, queryParameters, returnProxies, keyToRead );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
 			        getSQLString()
 				);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SessionImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies) throws SQLException, HibernateException {
 		return getRowFromResultSet(
 				resultSet,
 				session,
 				queryParameters,
 				lockModesArray,
 				optionalObjectKey,
 				hydratedObjects,
 				keys,
 				returnProxies,
 				null
 		);
 	}
 
 	private Object getRowFromResultSet(
 	        final ResultSet resultSet,
 	        final SessionImplementor session,
 	        final QueryParameters queryParameters,
 	        final LockMode[] lockModesArray,
 	        final EntityKey optionalObjectKey,
 	        final List hydratedObjects,
 	        final EntityKey[] keys,
 	        boolean returnProxies,
 	        ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
 		registerNonExists( keys, persisters, session );
 
 		// this call is side-effecty
 		Object[] row = getRow(
 		        resultSet,
 				persisters,
 				keys,
 				queryParameters.getOptionalObject(),
 				optionalObjectKey,
 				lockModesArray,
 				hydratedObjects,
 				session
 		);
 
 		readCollectionElements( row, resultSet, session );
 
 		if ( returnProxies ) {
 			// now get an existing proxy for each row element (if there is one)
 			for ( int i = 0; i < entitySpan; i++ ) {
 				Object entity = row[i];
 				Object proxy = session.getPersistenceContext().proxyFor( persisters[i], keys[i], entity );
 				if ( entity != proxy ) {
 					// force the proxy to resolve itself
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null ?
 				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
 				forcedResultTransformer.transformTuple(
 						getResultRow( row, resultSet, session ),
 						getResultRowAliases()
 				)
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
 		}
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			if ( idType.isComponentType() && getCompositeKeyManyToOneTargetIndices() != null ) {
 				// we may need to force resolve any key-many-to-one(s)
 				int[] keyManyToOneTargetIndices = getCompositeKeyManyToOneTargetIndices()[i];
 				// todo : better solution is to order the index processing based on target indices
 				//		that would account for multiple levels whereas this scheme does not
 				if ( keyManyToOneTargetIndices != null ) {
 					for ( int targetIndex : keyManyToOneTargetIndices ) {
 						if ( targetIndex < numberOfPersistersToProcess ) {
 							final Type targetIdType = persisters[targetIndex].getIdentifierType();
 							final Serializable targetId = (Serializable) targetIdType.resolve(
 									hydratedKeyState[targetIndex],
 									session,
 									null
 							);
 							// todo : need a way to signal that this key is resolved and its data resolved
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
 						}
 
 						// this part copied from #getRow, this section could be refactored out
 						Object object = session.getEntityUsingInterceptor( keys[targetIndex] );
 						if ( object != null ) {
 							//its already loaded so don't need to hydrate it
 							instanceAlreadyLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									keys[targetIndex],
 									object,
 									lockModes[targetIndex],
 									session
 							);
 						}
 						else {
 							instanceNotYetLoaded(
 									resultSet,
 									targetIndex,
 									persisters[targetIndex],
 									getEntityAliases()[targetIndex].getRowIdAlias(),
 									keys[targetIndex],
 									lockModes[targetIndex],
 									getOptionalObjectKey( queryParameters, session ),
 									queryParameters.getOptionalObject(),
 									hydratedObjects,
 									session
 							);
 						}
 					}
 				}
 			}
 			final Serializable resolvedId = (Serializable) idType.resolve( hydratedKeyState[i], session, null );
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	private Serializable determineResultId(SessionImplementor session, Serializable optionalId, Type idType, Serializable resolvedId) {
 		final boolean idIsResultId = optionalId != null
 				&& resolvedId != null
 				&& idType.isEqual( optionalId, resolvedId, session.getEntityMode(), factory );
 		final Serializable resultId = idIsResultId ? optionalId : resolvedId;
 		return resultId;
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
 						null; //if null, owner will be retrieved from session
 
 				final CollectionPersister collectionPersister = collectionPersisters[i];
 				final Serializable key;
 				if ( owner == null ) {
 					key = null;
 				}
 				else {
 					key = collectionPersister.getCollectionType().getKeyOfOwner( owner, session );
 					//TODO: old version did not require hashmap lookup:
 					//keys[collectionOwner].getIdentifier()
 				}
 
 				readCollectionElement(
 						owner,
 						key,
 						collectionPersister,
 						descriptors[i],
 						resultSet,
 						session
 					);
 
 			}
 
 		}
 	}
 
 	private List doQuery(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = hasMaxRows( selection ) ?
 				selection.getMaxRows().intValue() :
 				Integer.MAX_VALUE;
 
 		final int entitySpan = getEntityPersisters().length;
 
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 		final ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), selection, session );
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final List results = new ArrayList();
 
 		try {
 
 			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 
 			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 
             LOG.trace("Processing result set");
 
 			int count;
 			for ( count = 0; count < maxRows && rs.next(); count++ ) {
 
                 LOG.debugf("Result set row: %s", count);
 
 				Object result = getRowFromResultSet(
 						rs,
 						session,
 						queryParameters,
 						lockModesArray,
 						optionalObjectKey,
 						hydratedObjects,
 						keys,
 						returnProxies,
 						forcedResultTransformer
 				);
 				results.add( result );
 
 				if ( createSubselects ) {
 					subselectResultKeys.add(keys);
 					keys = new EntityKey[entitySpan]; //can't reuse in this case
 				}
 
 			}
 
             LOG.trace("Done processing result set (" + count + " rows)");
 
 		}
 		finally {
 			st.close();
 		}
 
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 
 		return results; //getResultList(results);
 
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
 								aliases[i],
 								loadables[i],
 								queryParameters,
 								keySets[i],
 								namedParameterLocMap
 							);
 
 						session.getPersistenceContext()
 								.getBatchFetchQueue()
 								.addSubselect( rowKeys[i], subselectFetch );
 					}
 
 				}
 
 			}
 		}
 	}
 
 	private Map buildNamedParameterLocMap(QueryParameters queryParameters) {
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
 					);
 			}
 			return namedParameterLocMap;
 		}
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly)
 	throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 		//important: reuse the same event instances for performance!
 		final PreLoadEvent pre;
 		final PostLoadEvent post;
 		if ( session.isEventSource() ) {
 			pre = new PreLoadEvent( (EventSource) session );
 			post = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			pre = null;
 			post = null;
 		}
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
             LOG.trace("Total objects hydrated: " + hydratedObjectsSize);
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
 				}
 			}
 		}
 
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SessionImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		 return null;
 	}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
index 3d2ca1cdb8..fae77a7467 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaJoinWalker.java
@@ -1,282 +1,282 @@
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
 package org.hibernate.loader.criteria;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Set;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.loader.AbstractEntityJoinWalker;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.internal.util.collections.ArrayHelper;
 
 /**
  * A <tt>JoinWalker</tt> for <tt>Criteria</tt> queries.
  *
  * @see CriteriaLoader
  * @author Gavin King
  */
 public class CriteriaJoinWalker extends AbstractEntityJoinWalker {
 
 	//TODO: add a CriteriaImplementor interface
 	//      this class depends directly upon CriteriaImpl in the impl package...
 
 	private final CriteriaQueryTranslator translator;
 	private final Set querySpaces;
 	private final Type[] resultTypes;
 	private final boolean[] includeInResultRow;
 
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final List userAliasList = new ArrayList();
 	private final List resultTypeList = new ArrayList();
 	private final List includeInResultRowList = new ArrayList();
 
 	public Type[] getResultTypes() {
 		return resultTypes;
 	}
 
 	public String[] getUserAliases() {
 		return userAliases;
 	}
 
 	public boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	public CriteriaJoinWalker(
 			final OuterJoinLoadable persister, 
 			final CriteriaQueryTranslator translator,
 			final SessionFactoryImplementor factory, 
 			final CriteriaImpl criteria, 
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) {
 		this( persister, translator, factory, criteria, rootEntityName, loadQueryInfluencers, null );
 	}
 
 	public CriteriaJoinWalker(
 			final OuterJoinLoadable persister,
 			final CriteriaQueryTranslator translator,
 			final SessionFactoryImplementor factory,
 			final CriteriaImpl criteria,
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers,
 			final String alias) {
 		super( persister, factory, loadQueryInfluencers, alias );
 
 		this.translator = translator;
 
 		querySpaces = translator.getQuerySpaces();
 
 		if ( translator.hasProjection() ) {			
 			initProjection(
 					translator.getSelect(), 
 					translator.getWhereCondition(), 
 					translator.getOrderBy(),
 					translator.getGroupBy(),
 					LockOptions.NONE  
 				);
 			resultTypes = translator.getProjectedTypes();
 			userAliases = translator.getProjectedAliases();
 			includeInResultRow = new boolean[ resultTypes.length ];
 			Arrays.fill( includeInResultRow, true );
 		}
 		else {
 			initAll( translator.getWhereCondition(), translator.getOrderBy(), LockOptions.NONE );
 			// root entity comes last
 			userAliasList.add( criteria.getAlias() ); //root entity comes *last*
 			resultTypeList.add( translator.getResultType( criteria ) );
 			includeInResultRowList.add( true );
 			userAliases = ArrayHelper.toStringArray( userAliasList );
 			resultTypes = ArrayHelper.toTypeArray( resultTypeList );
 			includeInResultRow = ArrayHelper.toBooleanArray( includeInResultRowList );
 		}
 	}
 
 	protected int getJoinType(
 			OuterJoinLoadable persister,
 			final PropertyPath path,
 			int propertyNumber,
 			AssociationType associationType,
 			FetchMode metadataFetchMode,
 			CascadeStyle metadataCascadeStyle,
 			String lhsTable,
 			String[] lhsColumns,
 			final boolean nullable,
 			final int currentDepth) throws MappingException {
 		if ( translator.isJoin( path.getFullPath() ) ) {
 			return translator.getJoinType( path.getFullPath() );
 		}
 		else {
 			if ( translator.hasProjection() ) {
 				return -1;
 			}
 			else {
 				FetchMode fetchMode = translator.getRootCriteria().getFetchMode( path.getFullPath() );
 				if ( isDefaultFetchMode( fetchMode ) ) {
 					if ( isJoinFetchEnabledByProfile( persister, path, propertyNumber ) ) {
 						return getJoinType( nullable, currentDepth );
 					}
 					else {
 						return super.getJoinType(
 								persister,
 								path,
 								propertyNumber,
 								associationType,
 								metadataFetchMode,
 								metadataCascadeStyle,
 								lhsTable,
 								lhsColumns,
 								nullable,
 								currentDepth
 						);
 					}
 				}
 				else {
 					if ( fetchMode == FetchMode.JOIN ) {
 						isDuplicateAssociation( lhsTable, lhsColumns, associationType ); //deliberately ignore return value!
 						return getJoinType( nullable, currentDepth );
 					}
 					else {
 						return -1;
 					}
 				}
 			}
 		}
 	}
 
 	protected int getJoinType(
 			AssociationType associationType,
 			FetchMode config,
 			PropertyPath path,
 			String lhsTable,
 			String[] lhsColumns,
 			boolean nullable,
 			int currentDepth,
 			CascadeStyle cascadeStyle) throws MappingException {
 		return ( translator.isJoin( path.getFullPath() ) ?
 				translator.getJoinType( path.getFullPath() ) :
 				super.getJoinType(
 						associationType,
 						config,
 						path,
 						lhsTable,
 						lhsColumns,
 						nullable,
 						currentDepth,
 						cascadeStyle
 				)
 		);
 	}
 
 	private static boolean isDefaultFetchMode(FetchMode fetchMode) {
 		return fetchMode==null || fetchMode==FetchMode.DEFAULT;
 	}
 
 	/**
 	 * Use the discriminator, to narrow the select to instances
 	 * of the queried subclass, also applying any filters.
 	 */
 	protected String getWhereFragment() throws MappingException {
 		return super.getWhereFragment() +
 			( (Queryable) getPersister() ).filterFragment( getAlias(), getLoadQueryInfluencers().getEnabledFilters() );
 	}
 	
 	protected String generateTableAlias(int n, PropertyPath path, Joinable joinable) {
 		// TODO: deal with side-effects (changes to includeInResultRowList, userAliasList, resultTypeList)!!!
 
 		// for collection-of-entity, we are called twice for given "path"
 		// once for the collection Joinable, once for the entity Joinable.
 		// the second call will/must "consume" the alias + perform side effects according to consumesEntityAlias()
 		// for collection-of-other, however, there is only one call 
 		// it must "consume" the alias + perform side effects, despite what consumeEntityAlias() return says
 		// 
 		// note: the logic for adding to the userAliasList is still strictly based on consumesEntityAlias return value
 		boolean checkForSqlAlias = joinable.consumesEntityAlias();
 
 		if ( !checkForSqlAlias && joinable.isCollection() ) {
 			// is it a collection-of-other (component or value) ?
 			CollectionPersister collectionPersister = (CollectionPersister)joinable;
 			Type elementType = collectionPersister.getElementType();
 			if ( elementType.isComponentType() || !elementType.isEntityType() ) {
 				checkForSqlAlias = true;
  			}
 		}
 
 		String sqlAlias = null;
 
 		if ( checkForSqlAlias ) {
 			final Criteria subcriteria = translator.getCriteria( path.getFullPath() );
 			sqlAlias = subcriteria==null ? null : translator.getSQLAlias(subcriteria);
 			
 			if (joinable.consumesEntityAlias() && ! translator.hasProjection()) {
 				includeInResultRowList.add( subcriteria != null && subcriteria.getAlias() != null );
 				if (sqlAlias!=null) {
 					if ( subcriteria.getAlias() != null ) {
 						userAliasList.add( subcriteria.getAlias() );
 						resultTypeList.add( translator.getResultType( subcriteria ) );
 					}
 				}
 			}
 		}
 
 		if (sqlAlias == null) {
 			sqlAlias = super.generateTableAlias( n + translator.getSQLAliasCount(), path, joinable );
 		}
 
 		return sqlAlias;
 	}
 
 	protected String generateRootAlias(String tableName) {
 		return CriteriaQueryTranslator.ROOT_SQL_ALIAS;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 	
 	public String getComment() {
 		return "criteria query";
 	}
 
 	protected String getWithClause(PropertyPath path) {
 		return translator.getWithClause( path.getFullPath() );
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index 1c0f3acf5a..90218999a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,240 +1,240 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
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
  *
  */
 package org.hibernate.loader.criteria;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A <tt>Loader</tt> for <tt>Criteria</tt> queries. Note that criteria queries are
  * more like multi-object <tt>load()</tt>s than like HQL queries.
  *
  * @author Gavin King
  */
 public class CriteriaLoader extends OuterJoinLoader {
 
 	//TODO: this class depends directly upon CriteriaImpl, 
 	//      in the impl package ... add a CriteriaImplementor 
 	//      interface
 
 	//NOTE: unlike all other Loaders, this one is NOT
 	//      multithreaded, or cacheable!!
 
 	private final CriteriaQueryTranslator translator;
 	private final Set querySpaces;
 	private final Type[] resultTypes;
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final boolean[] includeInResultRow;
 	private final int resultRowLength;
 
 	public CriteriaLoader(
 			final OuterJoinLoadable persister, 
 			final SessionFactoryImplementor factory, 
 			final CriteriaImpl criteria, 
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) throws HibernateException {
 		super( factory, loadQueryInfluencers );
 
 		translator = new CriteriaQueryTranslator(
 				factory, 
 				criteria, 
 				rootEntityName, 
 				CriteriaQueryTranslator.ROOT_SQL_ALIAS
 			);
 
 		querySpaces = translator.getQuerySpaces();
 		
 		CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister, 
 				translator,
 				factory, 
 				criteria, 
 				rootEntityName, 
 				loadQueryInfluencers
 			);
 
 		initFromWalker(walker);
 		
 		userAliases = walker.getUserAliases();
 		resultTypes = walker.getResultTypes();
 		includeInResultRow = walker.includeInResultRow();
 		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
 		postInstantiate();
 
 	}
 	
 	public ScrollableResults scroll(SessionImplementor session, ScrollMode scrollMode) 
 	throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode(scrollMode);
 		return scroll(qp, resultTypes, null, session);
 	}
 
 	public List list(SessionImplementor session) 
 	throws HibernateException {
 		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 	throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session),
 				getResultRowAliases()
 		);
 	}
 			
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		final Object[] result;
 		if ( translator.hasProjection() ) {
 			Type[] types = translator.getProjectedTypes();
 			result = new Object[types.length];
 			String[] columnAliases = translator.getProjectedColumnAliases();
 			for ( int i=0, pos=0; i<result.length; i++ ) {
 				int numColumns = types[i].getColumnSpan( session.getFactory() );
 				if ( numColumns > 1 ) {
 			    	String[] typeColumnAliases = ArrayHelper.slice( columnAliases, pos, numColumns );
 					result[i] = types[i].nullSafeGet(rs, typeColumnAliases, session, null);
 				}
 				else {
 					result[i] = types[i].nullSafeGet(rs, columnAliases[pos], session, null);
 				}
 				pos += numColumns;
 			}
 		}
 		else {
 			result = toResultRow( row );
 		}
 		return result;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( resultRowLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[ resultRowLength ];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInResultRow[i] ) result[j++] = row[i];
 			}
 			return result;
 		}
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	protected String applyLocks(String sqlSelectString, LockOptions lockOptions, Dialect dialect) throws QueryException {
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sqlSelectString;
 		}
 
 		final LockOptions locks = new LockOptions(lockOptions.getLockMode());
 		locks.setScope( lockOptions.getScope());
 		locks.setTimeOut( lockOptions.getTimeOut());
 
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 		final String[] drivingSqlAliases = getAliases();
 		for ( int i = 0; i < drivingSqlAliases.length; i++ ) {
 			final LockMode lockMode = lockOptions.getAliasSpecificLockMode( drivingSqlAliases[i] );
 			if ( lockMode != null ) {
 				final Lockable drivingPersister = ( Lockable ) getEntityPersisters()[i];
 				final String rootSqlAlias = drivingPersister.getRootTableAlias( drivingSqlAliases[i] );
 				locks.setAliasSpecificLockMode( rootSqlAlias, lockMode );
 				if ( keyColumnNames != null ) {
 					keyColumnNames.put( rootSqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 				}
 			}
 		}
 		return dialect.applyLocksToSql( sqlSelectString, locks, keyColumnNames );
 	}
 
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		final String[] entityAliases = getAliases();
 		if ( entityAliases == null ) {
 			return null;
 		}
 		final int size = entityAliases.length;
 		LockMode[] lockModesArray = new LockMode[size];
 		for ( int i=0; i<size; i++ ) {
 			LockMode lockMode = lockOptions.getAliasSpecificLockMode( entityAliases[i] );
 			lockModesArray[i] = lockMode==null ? lockOptions.getLockMode() : lockMode;
 		}
 		return lockModesArray;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
index abae77f85b..5e29ea4144 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
@@ -1,679 +1,678 @@
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
 package org.hibernate.loader.criteria;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.criterion.CriteriaQuery;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.EnhancedProjection;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.hql.ast.util.SessionFactoryHelper;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
-import org.hibernate.type.ComponentType;
 import org.hibernate.type.StringRepresentableType;
 import org.hibernate.type.Type;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Gavin King
  */
 public class CriteriaQueryTranslator implements CriteriaQuery {
 
 	public static final String ROOT_SQL_ALIAS = Criteria.ROOT_ALIAS + '_';
 
 	private CriteriaQuery outerQueryTranslator;
 
 	private final CriteriaImpl rootCriteria;
 	private final String rootEntityName;
 	private final String rootSQLAlias;
 	private int aliasCount = 0;
 
 	private final Map /* <Criteria, CriteriaInfoProvider> */ criteriaInfoMap = new LinkedHashMap();
 	private final Map /* <String, CriteriaInfoProvider> */ nameCriteriaInfoMap = new LinkedHashMap();
 	private final Map criteriaSQLAliasMap = new HashMap();
 	private final Map aliasCriteriaMap = new HashMap();
 	private final Map associationPathCriteriaMap = new LinkedHashMap();
 	private final Map associationPathJoinTypesMap = new LinkedHashMap();
 	private final Map withClauseMap = new HashMap();
 	
 	private final SessionFactoryImplementor sessionFactory;
 	private final SessionFactoryHelper helper;
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias,
 	        CriteriaQuery outerQuery) throws HibernateException {
 		this( factory, criteria, rootEntityName, rootSQLAlias );
 		outerQueryTranslator = outerQuery;
 	}
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias) throws HibernateException {
 		this.rootCriteria = criteria;
 		this.rootEntityName = rootEntityName;
 		this.sessionFactory = factory;
 		this.rootSQLAlias = rootSQLAlias;
 		this.helper = new SessionFactoryHelper(factory);
 		createAliasCriteriaMap();
 		createAssociationPathCriteriaMap();
 		createCriteriaEntityNameMap();
 		createCriteriaSQLAliasMap();
 	}
 
 	public String generateSQLAlias() {
 		return StringHelper.generateAlias( Criteria.ROOT_ALIAS, aliasCount ) + '_';
 	}
 
 	public String getRootSQLALias() {
 		return rootSQLAlias;
 	}
 
 	private Criteria getAliasedCriteria(String alias) {
 		return ( Criteria ) aliasCriteriaMap.get( alias );
 	}
 
 	public boolean isJoin(String path) {
 		return associationPathCriteriaMap.containsKey( path );
 	}
 
 	public int getJoinType(String path) {
 		Integer result = ( Integer ) associationPathJoinTypesMap.get( path );
 		return ( result == null ? Criteria.INNER_JOIN : result.intValue() );
 	}
 
 	public Criteria getCriteria(String path) {
 		return ( Criteria ) associationPathCriteriaMap.get( path );
 	}
 
 	public Set getQuerySpaces() {
 		Set result = new HashSet();
 		Iterator iter = criteriaInfoMap.values().iterator();
 		while ( iter.hasNext() ) {
 			CriteriaInfoProvider info = ( CriteriaInfoProvider )iter.next();
 			result.addAll( Arrays.asList( info.getSpaces() ) );
 		}
 		return result;
 	}
 
 	private void createAliasCriteriaMap() {
 		aliasCriteriaMap.put( rootCriteria.getAlias(), rootCriteria );
 		Iterator iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			Criteria subcriteria = ( Criteria ) iter.next();
 			if ( subcriteria.getAlias() != null ) {
 				Object old = aliasCriteriaMap.put( subcriteria.getAlias(), subcriteria );
 				if ( old != null ) {
 					throw new QueryException( "duplicate alias: " + subcriteria.getAlias() );
 				}
 			}
 		}
 	}
 
 	private void createAssociationPathCriteriaMap() {
 		Iterator iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria crit = ( CriteriaImpl.Subcriteria ) iter.next();
 			String wholeAssociationPath = getWholeAssociationPath( crit );
 			Object old = associationPathCriteriaMap.put( wholeAssociationPath, crit );
 			if ( old != null ) {
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			int joinType = crit.getJoinType();
 			old = associationPathJoinTypesMap.put( wholeAssociationPath, new Integer( joinType ) );
 			if ( old != null ) {
 				// TODO : not so sure this is needed...
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			if ( crit.getWithClause() != null )
 			{
 				this.withClauseMap.put(wholeAssociationPath, crit.getWithClause());
 			}
 		}
 	}
 
 	private String getWholeAssociationPath(CriteriaImpl.Subcriteria subcriteria) {
 		String path = subcriteria.getPath();
 
 		// some messy, complex stuff here, since createCriteria() can take an
 		// aliased path, or a path rooted at the creating criteria instance
 		Criteria parent = null;
 		if ( path.indexOf( '.' ) > 0 ) {
 			// if it is a compound path
 			String testAlias = StringHelper.root( path );
 			if ( !testAlias.equals( subcriteria.getAlias() ) ) {
 				// and the qualifier is not the alias of this criteria
 				//      -> check to see if we belong to some criteria other
 				//          than the one that created us
 				parent = ( Criteria ) aliasCriteriaMap.get( testAlias );
 			}
 		}
 		if ( parent == null ) {
 			// otherwise assume the parent is the the criteria that created us
 			parent = subcriteria.getParent();
 		}
 		else {
 			path = StringHelper.unroot( path );
 		}
 
 		if ( parent.equals( rootCriteria ) ) {
 			// if its the root criteria, we are done
 			return path;
 		}
 		else {
 			// otherwise, recurse
 			return getWholeAssociationPath( ( CriteriaImpl.Subcriteria ) parent ) + '.' + path;
 		}
 	}
 
 	private void createCriteriaEntityNameMap() {
 		// initialize the rootProvider first
 		CriteriaInfoProvider rootProvider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister( rootEntityName ) );
 		criteriaInfoMap.put( rootCriteria, rootProvider);
 		nameCriteriaInfoMap.put ( rootProvider.getName(), rootProvider );
 
 		Iterator iter = associationPathCriteriaMap.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			CriteriaInfoProvider info = getPathInfo((String)me.getKey());
 
 			criteriaInfoMap.put(
 					me.getValue(), //the criteria instance
 					info
 			);
 
 			nameCriteriaInfoMap.put( info.getName(), info );
 		}
 	}
 
 
 	private CriteriaInfoProvider getPathInfo(String path) {
 		StringTokenizer tokens = new StringTokenizer( path, "." );
 		String componentPath = "";
 
 		// start with the 'rootProvider'
 		CriteriaInfoProvider provider = ( CriteriaInfoProvider )nameCriteriaInfoMap.get( rootEntityName );
 
 		while ( tokens.hasMoreTokens() ) {
 			componentPath += tokens.nextToken();
 			Type type = provider.getType( componentPath );
 			if ( type.isAssociationType() ) {
 				// CollectionTypes are always also AssociationTypes - but there's not always an associated entity...
 				AssociationType atype = ( AssociationType ) type;
 				CollectionType ctype = type.isCollectionType() ? (CollectionType)type : null;
 				Type elementType = (ctype != null) ? ctype.getElementType( sessionFactory ) : null;
 				// is the association a collection of components or value-types? (i.e a colloction of valued types?)
 				if ( ctype != null  && elementType.isComponentType() ) {
 					provider = new ComponentCollectionCriteriaInfoProvider( helper.getCollectionPersister(ctype.getRole()) );
 				}
 				else if ( ctype != null && !elementType.isEntityType() ) {
 					provider = new ScalarCollectionCriteriaInfoProvider( helper, ctype.getRole() );
 				}
 				else {
 					provider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister(
 											  atype.getAssociatedEntityName( sessionFactory )
 											  ));
 				}
 				
 				componentPath = "";
 			}
 			else if ( type.isComponentType() ) {
 				if (!tokens.hasMoreTokens()) {
 					throw new QueryException("Criteria objects cannot be created directly on components.  Create a criteria on owning entity and use a dotted property to access component property: "+path);
 				} else {
 					componentPath += '.';
 				}
 			}
 			else {
 				throw new QueryException( "not an association: " + componentPath );
 			}
 		}
 		
 		return provider;
 	}
 
 	public int getSQLAliasCount() {
 		return criteriaSQLAliasMap.size();
 	}
 
 	private void createCriteriaSQLAliasMap() {
 		int i = 0;
 		Iterator criteriaIterator = criteriaInfoMap.entrySet().iterator();
 		while ( criteriaIterator.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) criteriaIterator.next();
 			Criteria crit = ( Criteria ) me.getKey();
 			String alias = crit.getAlias();
 			if ( alias == null ) {
 				alias = (( CriteriaInfoProvider ) me.getValue()).getName(); // the entity name
 			}
 			criteriaSQLAliasMap.put( crit, StringHelper.generateAlias( alias, i++ ) );
 		}
 		criteriaSQLAliasMap.put( rootCriteria, rootSQLAlias );
 	}
 
 	public CriteriaImpl getRootCriteria() {
 		return rootCriteria;
 	}
 
 	public QueryParameters getQueryParameters() {
 		LockOptions lockOptions = new LockOptions();
 		RowSelection selection = new RowSelection();
 		selection.setFirstRow( rootCriteria.getFirstResult() );
 		selection.setMaxRows( rootCriteria.getMaxResults() );
 		selection.setTimeout( rootCriteria.getTimeout() );
 		selection.setFetchSize( rootCriteria.getFetchSize() );
 
 		Iterator iter = rootCriteria.getLockModes().entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			final Criteria subcriteria = getAliasedCriteria( ( String ) me.getKey() );
 			lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), (LockMode)me.getValue() );
 		}
 		List values = new ArrayList();
 		List types = new ArrayList();
 		iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria subcriteria = ( CriteriaImpl.Subcriteria ) iter.next();
 			LockMode lm = subcriteria.getLockMode();
 			if ( lm != null ) {
 				lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lm );
 			}
 			if ( subcriteria.getWithClause() != null )
 			{
 				TypedValue[] tv = subcriteria.getWithClause().getTypedValues( subcriteria, this );
 				for ( int i = 0; i < tv.length; i++ ) {
 					values.add( tv[i].getValue() );
 					types.add( tv[i].getType() );
 				}
 			}
 		}
 
 		// Type and value gathering for the WHERE clause needs to come AFTER lock mode gathering,
 		// because the lock mode gathering loop now contains join clauses which can contain
 		// parameter bindings (as in the HQL WITH clause).
 		iter = rootCriteria.iterateExpressionEntries();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.CriterionEntry ce = ( CriteriaImpl.CriterionEntry ) iter.next();
 			TypedValue[] tv = ce.getCriterion().getTypedValues( ce.getCriteria(), this );
 			for ( int i = 0; i < tv.length; i++ ) {
 				values.add( tv[i].getValue() );
 				types.add( tv[i].getType() );
 			}
 		}
 
 		Object[] valueArray = values.toArray();
 		Type[] typeArray = ArrayHelper.toTypeArray( types );
 		return new QueryParameters(
 				typeArray,
 		        valueArray,
 		        lockOptions,
 		        selection,
 		        rootCriteria.isReadOnlyInitialized(),
 		        ( rootCriteria.isReadOnlyInitialized() ? rootCriteria.isReadOnly() : false ),
 		        rootCriteria.getCacheable(),
 		        rootCriteria.getCacheRegion(),
 		        rootCriteria.getComment(),
 		        rootCriteria.isLookupByNaturalKey(),
 		        rootCriteria.getResultTransformer()
 		);
 	}
 
 	public boolean hasProjection() {
 		return rootCriteria.getProjection() != null;
 	}
 
 	public String getGroupBy() {
 		if ( rootCriteria.getProjection().isGrouped() ) {
 			return rootCriteria.getProjection()
 					.toGroupSqlString( rootCriteria.getProjectionCriteria(), this );
 		}
 		else {
 			return "";
 		}
 	}
 
 	public String getSelect() {
 		return rootCriteria.getProjection().toSqlString(
 				rootCriteria.getProjectionCriteria(),
 		        0,
 		        this
 		);
 	}
 
 	/* package-protected */
 	Type getResultType(Criteria criteria) {
 		return getFactory().getTypeResolver().getTypeFactory().manyToOne( getEntityName( criteria ) );
 	}
 
 	public Type[] getProjectedTypes() {
 		return rootCriteria.getProjection().getTypes( rootCriteria, this );
 	}
 
 	public String[] getProjectedColumnAliases() {
 		return rootCriteria.getProjection() instanceof EnhancedProjection ?
 				( ( EnhancedProjection ) rootCriteria.getProjection() ).getColumnAliases( 0, rootCriteria, this ) :
 				rootCriteria.getProjection().getColumnAliases( 0 );
 	}
 
 	public String[] getProjectedAliases() {
 		return rootCriteria.getProjection().getAliases();
 	}
 
 	public String getWhereCondition() {
 		StringBuffer condition = new StringBuffer( 30 );
 		Iterator criterionIterator = rootCriteria.iterateExpressionEntries();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.CriterionEntry entry = ( CriteriaImpl.CriterionEntry ) criterionIterator.next();
 			String sqlString = entry.getCriterion().toSqlString( entry.getCriteria(), this );
 			condition.append( sqlString );
 			if ( criterionIterator.hasNext() ) {
 				condition.append( " and " );
 			}
 		}
 		return condition.toString();
 	}
 
 	public String getOrderBy() {
 		StringBuffer orderBy = new StringBuffer( 30 );
 		Iterator criterionIterator = rootCriteria.iterateOrderings();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.OrderEntry oe = ( CriteriaImpl.OrderEntry ) criterionIterator.next();
 			orderBy.append( oe.getOrder().toSqlString( oe.getCriteria(), this ) );
 			if ( criterionIterator.hasNext() ) {
 				orderBy.append( ", " );
 			}
 		}
 		return orderBy.toString();
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return sessionFactory;
 	}
 
 	public String getSQLAlias(Criteria criteria) {
 		return ( String ) criteriaSQLAliasMap.get( criteria );
 	}
 
 	public String getEntityName(Criteria criteria) {
 		return (( CriteriaInfoProvider ) criteriaInfoMap.get( criteria )).getName();
 	}
 
 	public String getColumn(Criteria criteria, String propertyName) {
 		String[] cols = getColumns( propertyName, criteria );
 		if ( cols.length != 1 ) {
 			throw new QueryException( "property does not map to a single column: " + propertyName );
 		}
 		return cols[0];
 	}
 
 	/**
 	 * Get the names of the columns constrained
 	 * by this criterion.
 	 */
 	public String[] getColumnsUsingProjection(
 			Criteria subcriteria,
 	        String propertyName) throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		String[] projectionColumns = null;
 		if ( projection != null ) {
 			projectionColumns = ( projection instanceof EnhancedProjection ?
 					( ( EnhancedProjection ) projection ).getColumnAliases( propertyName, 0, rootCriteria, this ) :
 					projection.getColumnAliases( propertyName, 0 )
 			);
 		}
 		if ( projectionColumns == null ) {
 			//it does not refer to an alias of a projection,
 			//look for a property
 			try {
 				return getColumns( propertyName, subcriteria );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getColumnsUsingProjection( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			//it refers to an alias of a projection
 			return projectionColumns;
 		}
 	}
 
 	public String[] getIdentifierColumns(Criteria subcriteria) {
 		String[] idcols =
 				( ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) ) ).getIdentifierColumnNames();
 		return StringHelper.qualify( getSQLAlias( subcriteria ), idcols );
 	}
 
 	public Type getIdentifierType(Criteria subcriteria) {
 		return ( ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) ) ).getIdentifierType();
 	}
 
 	public TypedValue getTypedIdentifierValue(Criteria subcriteria, Object value) {
 		final Loadable loadable = ( Loadable ) getPropertyMapping( getEntityName( subcriteria ) );
 		return new TypedValue(
 				loadable.getIdentifierType(),
 		        value,
 		        EntityMode.POJO
 		);
 	}
 
 	public String[] getColumns(
 			String propertyName,
 	        Criteria subcriteria) throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toColumns(
 						getSQLAlias( subcriteria, propertyName ),
 				        getPropertyName( propertyName )
 				);
 	}
 
 	/**
 	 * Get the names of the columns mapped by a property path; if the
 	 * property path is not found in subcriteria, try the "outer" query.
 	 * Projection aliases are ignored.
 	 */
 	public String[] findColumns(String propertyName, Criteria subcriteria )
 	throws HibernateException {
 		try {
 			return getColumns( propertyName, subcriteria );
 		}
 		catch ( HibernateException he ) {
 			//not found in inner query, try the outer query
 			if ( outerQueryTranslator != null ) {
 				return outerQueryTranslator.findColumns( propertyName, subcriteria );
 			}
 			else {
 				throw he;
 			}
 		}
 	}
 
 	public Type getTypeUsingProjection(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		Type[] projectionTypes = projection == null ?
 		                         null :
 		                         projection.getTypes( propertyName, subcriteria, this );
 
 		if ( projectionTypes == null ) {
 			try {
 				//it does not refer to an alias of a projection,
 				//look for a property
 				return getType( subcriteria, propertyName );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getType( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			if ( projectionTypes.length != 1 ) {
 				//should never happen, i think
 				throw new QueryException( "not a single-length projection: " + propertyName );
 			}
 			return projectionTypes[0];
 		}
 	}
 
 	public Type getType(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toType( getPropertyName( propertyName ) );
 	}
 
 	/**
 	 * Get the a typed value for the given property value.
 	 */
 	public TypedValue getTypedValue(Criteria subcriteria, String propertyName, Object value)
 			throws HibernateException {
 		// Detect discriminator values...
 		if ( value instanceof Class ) {
 			Class entityClass = ( Class ) value;
 			Queryable q = SessionFactoryHelper.findQueryableUsingImports( sessionFactory, entityClass.getName() );
 			if ( q != null ) {
 				Type type = q.getDiscriminatorType();
 				String stringValue = q.getDiscriminatorSQLValue();
 				if (stringValue != null && stringValue.length() > 2
 						&& stringValue.startsWith("'")
 						&& stringValue.endsWith("'")) { // remove the single
 														// quotes
 					stringValue = stringValue.substring(1,
 							stringValue.length() - 1);
 				}
 				
 				// Convert the string value into the proper type.
 				if ( type instanceof StringRepresentableType ) {
 					StringRepresentableType nullableType = (StringRepresentableType) type;
 					value = nullableType.fromStringValue( stringValue );
 				}
 				else {
 					throw new QueryException( "Unsupported discriminator type " + type );
 				}
 				return new TypedValue(
 						type,
 				        value,
 				        EntityMode.POJO
 				);
 			}
 		}
 		// Otherwise, this is an ordinary value.
 		return new TypedValue(
 				getTypeUsingProjection( subcriteria, propertyName ),
 		        value,
 		        EntityMode.POJO
 		);
 	}
 
 	private PropertyMapping getPropertyMapping(String entityName)
 			throws MappingException {
 		CriteriaInfoProvider info = ( CriteriaInfoProvider )nameCriteriaInfoMap.get(entityName);
 		return info.getPropertyMapping();
 	}
 
 	//TODO: use these in methods above
 
 	public String getEntityName(Criteria subcriteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return getEntityName( crit );
 			}
 		}
 		return getEntityName( subcriteria );
 	}
 
 	public String getSQLAlias(Criteria criteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria subcriteria = getAliasedCriteria( root );
 			if ( subcriteria != null ) {
 				return getSQLAlias( subcriteria );
 			}
 		}
 		return getSQLAlias( criteria );
 	}
 
 	public String getPropertyName(String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return propertyName.substring( root.length() + 1 );
 			}
 		}
 		return propertyName;
 	}
 
 	public String getWithClause(String path)
 	{
 		final Criterion crit = (Criterion)this.withClauseMap.get(path);
 		return crit == null ? null : crit.toSqlString(getCriteria(path), this);
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index e8294c16b5..0a60c648f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,617 +1,617 @@
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
 package org.hibernate.loader.hql;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.hql.ast.QueryTranslatorImpl;
 import org.hibernate.hql.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.ast.tree.FromElement;
 import org.hibernate.hql.ast.tree.QueryNode;
 import org.hibernate.hql.ast.tree.SelectClause;
-import org.hibernate.impl.IteratorImpl;
+import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A delegate that implements the Loader part of QueryTranslator.
  *
  * @author josh
  */
 public class QueryLoader extends BasicLoader {
 
 	/**
 	 * The query translator that is delegating to this object.
 	 */
 	private QueryTranslatorImpl queryTranslator;
 
 	private Queryable[] entityPersisters;
 	private String[] entityAliases;
 	private String[] sqlAliases;
 	private String[] sqlAliasSuffixes;
 	private boolean[] includeInSelect;
 
 	private String[] collectionSuffixes;
 
 	private boolean hasScalars;
 	private String[][] scalarColumnNames;
 	//private Type[] sqlResultTypes;
 	private Type[] queryReturnTypes;
 
 	private final Map sqlAliasByEntityAlias = new HashMap(8);
 
 	private EntityType[] ownerAssociationTypes;
 	private int[] owners;
 	private boolean[] entityEagerPropertyFetches;
 
 	private int[] collectionOwners;
 	private QueryableCollection[] collectionPersisters;
 
 	private int selectLength;
 
 	private AggregatedSelectExpression aggregatedSelectExpression;
 	private String[] queryReturnAliases;
 
 	private LockMode[] defaultLockModes;
 
 
 	/**
 	 * Creates a new Loader implementation.
 	 *
 	 * @param queryTranslator The query translator that is the delegator.
 	 * @param factory The factory from which this loader is being created.
 	 * @param selectClause The AST representing the select clause for loading.
 	 */
 	public QueryLoader(
 			final QueryTranslatorImpl queryTranslator,
 	        final SessionFactoryImplementor factory,
 	        final SelectClause selectClause) {
 		super( factory );
 		this.queryTranslator = queryTranslator;
 		initialize( selectClause );
 		postInstantiate();
 	}
 
 	private void initialize(SelectClause selectClause) {
 
 		List fromElementList = selectClause.getFromElementsForLoad();
 
 		hasScalars = selectClause.isScalarSelect();
 		scalarColumnNames = selectClause.getColumnNames();
 		//sqlResultTypes = selectClause.getSqlResultTypes();
 		queryReturnTypes = selectClause.getQueryReturnTypes();
 
 		aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
 		queryReturnAliases = selectClause.getQueryReturnAliases();
 
 		List collectionFromElements = selectClause.getCollectionFromElements();
 		if ( collectionFromElements != null && collectionFromElements.size()!=0 ) {
 			int length = collectionFromElements.size();
 			collectionPersisters = new QueryableCollection[length];
 			collectionOwners = new int[length];
 			collectionSuffixes = new String[length];
 			for ( int i=0; i<length; i++ ) {
 				FromElement collectionFromElement = (FromElement) collectionFromElements.get(i);
 				collectionPersisters[i] = collectionFromElement.getQueryableCollection();
 				collectionOwners[i] = fromElementList.indexOf( collectionFromElement.getOrigin() );
 //				collectionSuffixes[i] = collectionFromElement.getColumnAliasSuffix();
 //				collectionSuffixes[i] = Integer.toString( i ) + "_";
 				collectionSuffixes[i] = collectionFromElement.getCollectionSuffix();
 			}
 		}
 
 		int size = fromElementList.size();
 		entityPersisters = new Queryable[size];
 		entityEagerPropertyFetches = new boolean[size];
 		entityAliases = new String[size];
 		sqlAliases = new String[size];
 		sqlAliasSuffixes = new String[size];
 		includeInSelect = new boolean[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 
 		for ( int i = 0; i < size; i++ ) {
 			final FromElement element = ( FromElement ) fromElementList.get( i );
 			entityPersisters[i] = ( Queryable ) element.getEntityPersister();
 
 			if ( entityPersisters[i] == null ) {
 				throw new IllegalStateException( "No entity persister for " + element.toString() );
 			}
 
 			entityEagerPropertyFetches[i] = element.isAllPropertyFetch();
 			sqlAliases[i] = element.getTableAlias();
 			entityAliases[i] = element.getClassAlias();
 			sqlAliasByEntityAlias.put( entityAliases[i], sqlAliases[i] );
 			// TODO should we just collect these like with the collections above?
 			sqlAliasSuffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + "_";
 //			sqlAliasSuffixes[i] = element.getColumnAliasSuffix();
 			includeInSelect[i] = !element.isFetch();
 			if ( includeInSelect[i] ) {
 				selectLength++;
 			}
 
 			owners[i] = -1; //by default
 			if ( element.isFetch() ) {
 				if ( element.isCollectionJoin() || element.getQueryableCollection() != null ) {
 					// This is now handled earlier in this method.
 				}
 				else if ( element.getDataType().isEntityType() ) {
 					EntityType entityType = ( EntityType ) element.getDataType();
 					if ( entityType.isOneToOne() ) {
 						owners[i] = fromElementList.indexOf( element.getOrigin() );
 					}
 					ownerAssociationTypes[i] = entityType;
 				}
 			}
 		}
 
 		//NONE, because its the requested lock mode, not the actual! 
 		defaultLockModes = ArrayHelper.fillArray( LockMode.NONE, size );
 	}
 
 	public AggregatedSelectExpression getAggregatedSelectExpression() {
 		return aggregatedSelectExpression;
 	}
 
 
 	// -- Loader implementation --
 
 	public final void validateScrollability() throws HibernateException {
 		queryTranslator.validateScrollability();
 	}
 
 	protected boolean needsFetchingScroll() {
 		return queryTranslator.containsCollectionFetches();
 	}
 
 	public Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	public String[] getAliases() {
 		return sqlAliases;
 	}
 
 	public String[] getSqlAliasSuffixes() {
 		return sqlAliasSuffixes;
 	}
 
 	public String[] getSuffixes() {
 		return getSqlAliasSuffixes();
 	}
 
 	public String[] getCollectionSuffixes() {
 		return collectionSuffixes;
 	}
 
 	protected String getQueryIdentifier() {
 		return queryTranslator.getQueryIdentifier();
 	}
 
 	/**
 	 * The SQL query string to be called.
 	 */
 	protected String getSQLString() {
 		return queryTranslator.getSQLString();
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only collection loaders
 	 * return a non-null value
 	 */
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return entityEagerPropertyFetches;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner")
 	 */
 	protected int[] getOwners() {
 		return owners;
 	}
 
 	protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	// -- Loader overrides --
 
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	/**
 	 * @param lockOptions a collection of lock modes specified dynamically via the Query interface
 	 */
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		if ( lockOptions == null ) {
 			return defaultLockModes;
 		}
 
 		if ( lockOptions.getAliasLockCount() == 0
 				&& ( lockOptions.getLockMode() == null || LockMode.NONE.equals( lockOptions.getLockMode() ) ) ) {
 			return defaultLockModes;
 		}
 
 		// unfortunately this stuff can't be cached because
 		// it is per-invocation, not constant for the
 		// QueryTranslator instance
 
 		LockMode[] lockModesArray = new LockMode[entityAliases.length];
 		for ( int i = 0; i < entityAliases.length; i++ ) {
 			LockMode lockMode = lockOptions.getEffectiveLockMode( entityAliases[i] );
 			if ( lockMode == null ) {
 				//NONE, because its the requested lock mode, not the actual!
 				lockMode = LockMode.NONE;
 			}
 			lockModesArray[i] = lockMode;
 		}
 
 		return lockModesArray;
 	}
 
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		// we are given a map of user-alias -> lock mode
 		// create a new map of sql-alias -> lock mode
 
 		if ( lockOptions == null ||
 			( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		// we need both the set of locks and the columns to reference in locks
 		// as the ultimate output of this section...
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
 		final Iterator itr = sqlAliasByEntityAlias.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = (Map.Entry) itr.next();
 			final String userAlias = (String) entry.getKey();
 			final String drivingSqlAlias = (String) entry.getValue();
 			if ( drivingSqlAlias == null ) {
 				throw new IllegalArgumentException( "could not locate alias to apply lock mode : " + userAlias );
 			}
 			// at this point we have (drivingSqlAlias) the SQL alias of the driving table
 			// corresponding to the given user alias.  However, the driving table is not
 			// (necessarily) the table against which we want to apply locks.  Mainly,
 			// the exception case here is joined-subclass hierarchies where we instead
 			// want to apply the lock against the root table (for all other strategies,
 			// it just happens that driving and root are the same).
 			final QueryNode select = ( QueryNode ) queryTranslator.getSqlAST();
 			final Lockable drivingPersister = ( Lockable ) select.getFromClause()
 					.findFromElementByUserOrSqlAlias( userAlias, drivingSqlAlias )
 					.getQueryable();
 			final String sqlAlias = drivingPersister.getRootTableAlias( drivingSqlAlias );
 
 			final LockMode effectiveLockMode = lockOptions.getEffectiveLockMode( userAlias );
 			locks.setAliasSpecificLockMode( sqlAlias, effectiveLockMode );
 
 			if ( keyColumnNames != null ) {
 				keyColumnNames.put( sqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 			}
 		}
 
 		// apply the collected locks and columns
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 		// todo : scalars???
 //		if ( row.length != lockModesArray.length ) {
 //			return;
 //		}
 //
 //		for ( int i = 0; i < lockModesArray.length; i++ ) {
 //			if ( LockMode.OPTIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //				final EntityEntry pcEntry =
 //			}
 //			else if ( LockMode.PESSIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //
 //			}
 //		}
 	}
 
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	private boolean hasSelectNew() {
 		return aggregatedSelectExpression != null &&  aggregatedSelectExpression.getResultTransformer() != null;
 	}
 
 	protected String[] getResultRowAliases() {
 		return queryReturnAliases;
 	}
 	
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.resolveResultTransformer( implicitResultTransformer, resultTransformer );
 	}
 
 	protected boolean[] includeInResultRow() {
 		boolean[] includeInResultTuple = includeInSelect;
 		if ( hasScalars ) {
 			includeInResultTuple = new boolean[ queryReturnTypes.length ];
 			Arrays.fill( includeInResultTuple, true );
 		}
 		return includeInResultTuple;
 	}
 
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		Object[] resultRow = getResultRow( row, rs, session );
 		boolean hasTransform = hasSelectNew() || transformer!=null;
 		return ( ! hasTransform && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = scalarColumnNames;
 			int queryCols = queryReturnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = queryReturnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...
 		HolderInstantiator holderInstantiator = buildHolderInstantiator( resultTransformer );
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = ( Object[] ) results.get( i );
 				Object result = holderInstantiator.instantiate(row);
 				results.set( i, result );
 			}
 
 			if ( !hasSelectNew() && resultTransformer != null ) {
 				return resultTransformer.transformList(results);
 			}
 			else {
 				return results;
 			}
 		}
 		else {
 			return results;
 		}
 	}
 
 	private HolderInstantiator buildHolderInstantiator(ResultTransformer queryLocalResultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.getHolderInstantiator(
 				implicitResultTransformer,
 				queryLocalResultTransformer,
 				queryReturnAliases
 		);
 	}
 	// --- Query translator methods ---
 
 	public List list(
 			SessionImplementor session,
 			QueryParameters queryParameters) throws HibernateException {
 		checkQuery( queryParameters );
 		return list( session, queryParameters, queryTranslator.getQuerySpaces(), queryReturnTypes );
 	}
 
 	private void checkQuery(QueryParameters queryParameters) {
 		if ( hasSelectNew() && queryParameters.getResultTransformer() != null ) {
 			throw new QueryException( "ResultTransformer is not allowed for 'select new' queries." );
 		}
 	}
 
 	public Iterator iterate(
 			QueryParameters queryParameters,
 			EventSource session) throws HibernateException {
 		checkQuery( queryParameters );
 		final boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.currentTimeMillis();
 		}
 
 		try {
 			final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 			if ( queryParameters.isCallable() ) {
 				throw new QueryException("iterate() not supported for callable statements");
 			}
 			final ResultSet rs = getResultSet(st, queryParameters.hasAutoDiscoverScalarTypes(), false, queryParameters.getRowSelection(), session);
 			final Iterator result = new IteratorImpl(
 					rs,
 			        st,
 			        session,
 			        queryParameters.isReadOnly( session ),
 			        queryReturnTypes,
 			        queryTranslator.getColumnNames(),
 			        buildHolderInstantiator( queryParameters.getResultTransformer() )
 			);
 
 			if ( stats ) {
 				session.getFactory().getStatisticsImplementor().queryExecuted(
 //						"HQL: " + queryTranslator.getQueryString(),
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
 				);
 			}
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query using iterate",
 			        getSQLString()
 				);
 		}
 
 	}
 
 	public ScrollableResults scroll(
 			final QueryParameters queryParameters,
 	        final SessionImplementor session) throws HibernateException {
 		checkQuery( queryParameters );
 		return scroll( 
 				queryParameters,
 				queryReturnTypes,
 				buildHolderInstantiator( queryParameters.getResultTransformer() ),
 				session
 		);
 	}
 
 	// -- Implementation private methods --
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		return queryTranslator.getParameterTranslations().getNamedParameterSqlLocations( name );
 	}
 
 	/**
 	 * We specifically override this method here, because in general we know much more
 	 * about the parameters and their appropriate bind positions here then we do in
 	 * our super because we track them explciitly here through the ParameterSpecification
 	 * interface.
 	 *
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException {
 //		int position = bindFilterParameterValues( statement, queryParameters, startIndex, session );
 		int position = startIndex;
 //		List parameterSpecs = queryTranslator.getSqlAST().getWalker().getParameters();
 		List parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
 		Iterator itr = parameterSpecs.iterator();
 		while ( itr.hasNext() ) {
 			ParameterSpecification spec = ( ParameterSpecification ) itr.next();
 			position += spec.bind( statement, queryParameters, session, position );
 		}
 		return position - startIndex;
 	}
 
 	private int bindFilterParameterValues(
 			PreparedStatement st,
 			QueryParameters queryParameters,
 			int position,
 			SessionImplementor session) throws SQLException {
 		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
 		// see the discussion there in DynamicFilterParameterSpecification's javadocs as to why
 		// it is currently not done that way.
 		int filteredParamCount = queryParameters.getFilteredPositionalParameterTypes() == null
 				? 0
 				: queryParameters.getFilteredPositionalParameterTypes().length;
 		int nonfilteredParamCount = queryParameters.getPositionalParameterTypes() == null
 				? 0
 				: queryParameters.getPositionalParameterTypes().length;
 		int filterParamCount = filteredParamCount - nonfilteredParamCount;
 		for ( int i = 0; i < filterParamCount; i++ ) {
 			Type type = queryParameters.getFilteredPositionalParameterTypes()[i];
 			Object value = queryParameters.getFilteredPositionalParameterValues()[i];
 			type.nullSafeSet( st, value, position, session );
 			position += type.getColumnSpan( getFactory() );
 		}
 
 		return position;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index d402349119..4e9096db94 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1064 +1,1064 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
-import org.hibernate.impl.FilterHelper;
+import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractCollectionPersister.class.getName());
 
     // TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	//SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final String sqlWhereString;
 	private final String sqlOrderByStringTemplate;
 	private final String sqlWhereStringTemplate;
 	private final boolean hasOrder;
 	protected final boolean hasWhere;
 	private final int baseIndex;
 
 	private final String nodeName;
 	private final String elementNodeName;
 	private final String indexNodeName;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 
 	//types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	//columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	//private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	private final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	//extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	private final boolean hasManyToManyOrder;
 	private final String manyToManyOrderByTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	public AbstractCollectionPersister(
 			final Collection collection,
 			final CollectionRegionAccessStrategy cacheAccessStrategy,
 			final Configuration cfg,
 			final SessionFactoryImplementor factory) throws MappingException, CacheException {
 
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collection.isMap() ?
 					( CacheEntryStructure ) new StructuredMapCacheEntry() :
 					( CacheEntryStructure ) new StructuredCollectionCacheEntry();
 		}
 		else {
 			cacheEntryStructure = new UnstructuredCacheEntry();
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister(entityName);
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		//isSet = collection.isSet();
 		//isSorted = collection.isSorted();
 		isPrimitiveArray = collection.isPrimitiveArray();
 		isArray = collection.isArray();
 		subselectLoadable = collection.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 			);
 
 		int spacesSize = 1 + collection.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collection.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collection.getWhere() ) ? "( " + collection.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate(sqlWhereString, dialect, factory.getSqlFunctionRegistry()) :
 				null;
 
 		hasOrphanDelete = collection.hasOrphanDelete();
 
 		int batch = collection.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collection.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collection.getKey().getType();
 		iter = collection.getKey().getColumnIterator();
 		int keySpan = collection.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName(dialect);
 			keyColumnAliases[k] = col.getAlias(dialect,collection.getOwner().getRootTable());
 			k++;
 		}
 
 		//unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		//ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister(entityName);
 			if ( elemNode==null ) {
 				elemNode = cfg.getClassMapping(entityName).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 		elementNodeName = elemNode;
 
 		int elementSpan = collection.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collection.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias(dialect);
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName(dialect);
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr(dialect);
 				elementColumnReaderTemplates[j] = col.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 
 		//workaround, for backward compatibility of sets with no
 		//not-null columns, assume all columns are used in the
 		//row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if (hasIndex) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collection;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias(dialect);
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate(dialect, factory.getSqlFunctionRegistry());
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName(dialect);
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 
 			indexNodeName = indexedCollection.getIndexNodeName();
 
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 			indexNodeName = null;
 		}
 
 		hasIdentifier = collection.isIdentified();
 		if (hasIdentifier) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = ( Column ) iter.next();
 			identifierColumnName = col.getQuotedName(dialect);
 			identifierColumnAlias = col.getAlias(dialect);
 			//unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					cfg.getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 			);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			//unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		//GENERATE THE SQL:
 
 		//sqlSelectString = sqlSelectString();
 		//sqlSelectRowString = sqlSelectRowString();
 
 		if ( collection.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collection.getCustomSQLInsert();
 			insertCallable = collection.isCustomInsertCallable();
 			insertCheckStyle = collection.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLInsert(), insertCallable )
 		            : collection.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collection.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collection.getCustomSQLUpdate();
 			updateCallable = collection.isCustomUpdateCallable();
 			updateCheckStyle = collection.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLUpdate(), insertCallable )
 		            : collection.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collection.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collection.getCustomSQLDelete();
 			deleteCallable = collection.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collection.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collection.getCustomSQLDeleteAll();
 			deleteAllCallable = collection.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString(  collection.isIndexed() && !collection.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collection.isLazy();
 		isExtraLazy = collection.isExtraLazy();
 
 		isInverse = collection.isInverse();
 
 		if ( collection.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collection ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; //elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping(
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory
 				);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping(
 					elementColumnNames,
 					elementType
 				);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { //not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping(
 						elementColumnNames,
 						elementType
 					);
 			}
 		}
 
 		hasOrder = collection.getOrderBy() != null;
 		if ( hasOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
 					collection.getOrderBy(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			sqlOrderByStringTemplate = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
 					collection.getManyToManyOrdering(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTemplate = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("Static SQL for collection: %s", getRole());
             if (getSQLInsertRowString() != null) LOG.debugf(" Row insert: %s", getSQLInsertRowString());
             if (getSQLUpdateRowString() != null) LOG.debugf(" Row update: %s", getSQLUpdateRowString());
             if (getSQLDeleteRowString() != null) LOG.debugf(" Row delete: %s", getSQLDeleteRowString());
             if (getSQLDeleteString() != null) LOG.debugf(" One-shot delete: %s", getSQLDeleteString());
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			//if there is a user-specified loader, return that
 			//TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
 		if (subselect == null) {
 			return null;
 		}
 		else {
 
 			// Take care of any entities that might have
 			// been evicted!
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}
 
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass() { //needed by arrays
 		return elementClass;
 	}
 
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() - baseIndex );
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 	throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role );  //an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsSettable, session);
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue(indexColumnIsSettable);
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() + baseIndex );
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (elementIsPureFormula) {
 			throw new AssertionFailure("cannot use a formula-based element in the where condition");
 		}
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsInPrimaryKey, session);
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (indexContainsFormula) {
 			throw new AssertionFailure("cannot use a formula-based index in the where condition");
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		} else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 			"max(" + getIndexColumnNames()[0] + ") + 1": //lists, arrays
 			"count(" + getElementColumnNames()[0] + ")"; //sets, maps, bags
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn(selectValue)
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i=0; i<elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i=0; i<indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify(alias, indexColumnNames, indexFormulaTemplates);
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify(alias, elementColumnNames, elementFormulaTemplates);
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for (int i=0; i<span; i++) {
 			if ( columnNames[i]==null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; //TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
index 34bd80df12..f6663b9f1c 100755
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
@@ -1,76 +1,76 @@
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
 package org.hibernate.persister.collection;
 import java.io.Serializable;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
 import org.hibernate.loader.collection.CollectionInitializer;
 
 import org.jboss.logging.Logger;
 
 /**
  * A wrapper around a named query.
  * @author Gavin King
  */
 public final class NamedQueryCollectionInitializer implements CollectionInitializer {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        NamedQueryCollectionInitializer.class.getName());
 
     private final String queryName;
 	private final CollectionPersister persister;
 
 	public NamedQueryCollectionInitializer(String queryName, CollectionPersister persister) {
 		super();
 		this.queryName = queryName;
 		this.persister = persister;
 	}
 
 	public void initialize(Serializable key, SessionImplementor session)
 	throws HibernateException {
 
         LOG.debugf("Initializing collection: %s using named query: %s", persister.getRole(), queryName);
 
 		//TODO: is there a more elegant way than downcasting?
 		AbstractQueryImpl query = (AbstractQueryImpl) session.getNamedSQLQuery(queryName);
 		if ( query.getNamedParameters().length>0 ) {
 			query.setParameter(
 					query.getNamedParameters()[0],
 					key,
 					persister.getKeyType()
 				);
 		}
 		else {
 			query.setParameter( 0, key, persister.getKeyType() );
 		}
 		query.setCollectionKey( key )
 				.setFlushMode( FlushMode.MANUAL )
 				.list();
 
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 3ec487dbfa..efd8bc8d80 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1079 +1,1079 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.engine.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
-import org.hibernate.impl.FilterHelper;
+import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoader;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.Tuplizer;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 				   SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryStructure cacheEntryStructure;
 	private final EntityMetamodel entityMetamodel;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	private final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 *
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
 	}
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
 				(CacheEntryStructure) new StructuredCacheEntry(this) :
 				(CacheEntryStructure) new UnstructuredCacheEntry();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( new Integer( i ) );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column)thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
 		}
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilterMap(), factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( new Integer( tableNumber ) );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( new Integer( colNumbers[j] ) );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( new Integer( formNumbers[j] ) );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
         if ( LOG.isTraceEnabled() ) {
 			LOG.trace(
 					"Initializing lazy properties of: " +
 							MessageHelper.infoString( this, id, getFactory() ) +
 							", field access: " + fieldName
 			);
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
         if (!hasLazyProperties()) throw new AssertionFailure("no lazy properties");
 
         LOG.trace("Initializing lazy properties from datastore");
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = ps.executeQuery();
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					ps.close();
 				}
 			}
 
             LOG.trace("Done initializing lazy properties");
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 				);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
         LOG.trace("Initializing lazy properties from second-level cache");
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
         LOG.trace("Done initializing lazy properties");
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue, session.getEntityMode() );
 		if (snapshot != null) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, session.getEntityMode(), factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_NONE ||
 			( !isVersioned() && optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) ||
 			getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}
 
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
index 0850bf3c6a..41badce7b4 100755
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
@@ -1,86 +1,86 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.FlushMode;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 
 /**
  * Not really a <tt>Loader</tt>, just a wrapper around a
  * named query.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class NamedQueryLoader implements UniqueEntityLoader {
 	private final String queryName;
 	private final EntityPersister persister;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, NamedQueryLoader.class.getName());
 
 	public NamedQueryLoader(String queryName, EntityPersister persister) {
 		super();
 		this.queryName = queryName;
 		this.persister = persister;
 	}
 
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
         if (lockOptions != null) LOG.debugf("Ignoring lock-options passed to named query loader");
 		return load( id, optionalObject, session );
 	}
 
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
         LOG.debugf("Loading entity: %s using named query: %s", persister.getEntityName(), queryName);
 
 		AbstractQueryImpl query = (AbstractQueryImpl) session.getNamedQuery(queryName);
 		if ( query.hasNamedParameters() ) {
 			query.setParameter(
 					query.getNamedParameters()[0],
 					id,
 					persister.getIdentifierType()
 				);
 		}
 		else {
 			query.setParameter( 0, id, persister.getIdentifierType() );
 		}
 		query.setOptionalId(id);
 		query.setOptionalEntityName( persister.getEntityName() );
 		query.setOptionalObject(optionalObject);
 		query.setFlushMode( FlushMode.MANUAL );
 		query.list();
 
 		// now look up the object we are really interested in!
 		// (this lets us correctly handle proxies and multi-row or multi-column queries)
 		return session.getPersistenceContext().getEntity( session.generateEntityKey( id, persister ) );
 
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
index 5153f17e03..9e71436976 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorNoIncrementTest.java
@@ -1,174 +1,174 @@
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
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
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
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
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
 		// 		so the first generated id value is maxlo + 1, here be 4
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		// which should also perform the first read on the sequence which should set it to its "start with" value (1)
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 3L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 4L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 5L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
index b6d7077dbf..199bbdc4f0 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/SequenceHiLoGeneratorTest.java
@@ -1,176 +1,176 @@
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
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
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
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
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
 		// 		so the first generated id value is maxlo + 1, here be 4
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
 		// unlike the newer strategies, the db value will not get update here.  It gets updated on the next invocation
 		// 	after a clock over
 		assertEquals( 1L, extractSequenceValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 8L, generatedValue.longValue() );
 		// this should force an increment in the sequence value
 		assertEquals( 2L, extractSequenceValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractSequenceValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select currval('" + TEST_SEQUENCE + "');" );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
index be46e0d4b5..53629165ba 100644
--- a/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/id/TableHiLoGeneratorTest.java
@@ -1,180 +1,180 @@
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
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.Session;
 import org.hibernate.TestingDatabaseInfo;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * I went back to 3.3 source and grabbed the code/logic as it existed back then and crafted this
  * unit test so that we can make sure the value keep being generated in the expected manner
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "deprecation" })
 public class TableHiLoGeneratorTest extends BaseUnitTestCase {
 	private static final String GEN_TABLE = "generator_table";
 	private static final String GEN_COLUMN = TableHiLoGenerator.DEFAULT_COLUMN_NAME;
 
 	private Configuration cfg;
 	private ServiceRegistry serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 	private TableHiLoGenerator generator;
 
 	@Before
 	public void setUp() throws Exception {
 		Properties properties = new Properties();
 		properties.setProperty( TableGenerator.TABLE, GEN_TABLE );
 		properties.setProperty( TableGenerator.COLUMN, GEN_COLUMN );
 		properties.setProperty( TableHiLoGenerator.MAX_LO, "3" );
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
 				}
 		);
 
 		Dialect dialect = new H2Dialect();
 
 		generator = new TableHiLoGenerator();
 		generator.configure( StandardBasicTypes.LONG, properties, dialect );
 
 		cfg = TestingDatabaseInfo.buildBaseConfiguration()
 				.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[0],
 						generator.sqlDropStrings( dialect )[0]
 				)
 		);
 
 		cfg.addAuxiliaryDatabaseObject(
 				new SimpleAuxiliaryDatabaseObject(
 						generator.sqlCreateStrings( dialect )[1],
 						null
 				)
 		);
 
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
 		assertEquals( 0L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		Long generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 1L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 2L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 3L, generatedValue.longValue() );
 		assertEquals( 1L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 4L, generatedValue.longValue() );
 		assertEquals( 2L, extractInDatabaseValue( session ) );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		generatedValue = (Long) generator.generate( session, null );
 		assertEquals( 5L, generatedValue.longValue() );
 		assertEquals( 2L, extractInDatabaseValue( session ) );
 
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private long extractInDatabaseValue(Session session) {
 		class WorkImpl implements Work {
 			private long value;
 			public void execute(Connection connection) throws SQLException {
 				PreparedStatement query = connection.prepareStatement( "select " + GEN_COLUMN + " from " + GEN_TABLE );
 				ResultSet resultSet = query.executeQuery();
 				resultSet.next();
 				value = resultSet.getLong( 1 );
 			}
 		}
 		WorkImpl work = new WorkImpl();
 		session.doWork( work );
 		return work.value;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java b/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
index c9247dc295..4ca8f3a584 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/connections/AggressiveReleaseTest.java
@@ -1,249 +1,248 @@
 // $Id: AggressiveReleaseTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 package org.hibernate.test.connections;
 
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Hibernate;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
-import org.hibernate.impl.SessionImpl;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 import org.junit.Test;
 
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Implementation of AggressiveReleaseTest.
  *
  * @author Steve Ebersole
  */
 public class AggressiveReleaseTest extends ConnectionManagementTestCase {
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		TestingJtaBootstrap.prepare( cfg.getProperties() );
 		cfg.setProperty( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 		cfg.setProperty( Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.AFTER_STATEMENT.toString() );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" );
 	}
 
 	@Override
 	protected Session getSessionUnderTest() throws Throwable {
 		return openSession();
 	}
 
 	@Override
 	protected void reconnect(Session session) {
 	}
 
 	@Override
 	protected void prepare() throws Throwable {
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().begin();
 	}
 
 	@Override
 	protected void done() throws Throwable {
 		TestingJtaBootstrap.INSTANCE.getTransactionManager().commit();
 	}
 
 	// Some additional tests specifically for the aggressive-release functionality...
 
 	@Test
 	public void testSerializationOnAfterStatementAggressiveRelease() throws Throwable {
 		prepare();
 		try {
 			Session s = getSessionUnderTest();
 			Silly silly = new Silly( "silly" );
 			s.save( silly );
 
 			// this should cause the CM to obtain a connection, and then release it
 			s.flush();
 
 			// We should be able to serialize the session at this point...
 			SerializationHelper.serialize( s );
 
 			s.delete( silly );
 			s.flush();
 
 			release( s );
 		}
 		finally {
 			done();
 		}
 	}
 
 	@Test
 	public void testSerializationFailsOnAfterStatementAggressiveReleaseWithOpenResources() throws Throwable {
 		prepare();
 		Session s = getSessionUnderTest();
 
 		Silly silly = new Silly( "silly" );
 		s.save( silly );
 
 		// this should cause the CM to obtain a connection, and then release it
 		s.flush();
 
 		// both scroll() and iterate() cause batching to hold on
 		// to resources, which should make aggressive-release not release
 		// the connection (and thus cause serialization to fail)
 		ScrollableResults sr = s.createQuery( "from Silly" ).scroll();
 
 		try {
 			SerializationHelper.serialize( s );
 			fail( "Serialization allowed on connected session; or aggressive release released connection with open resources" );
 		}
 		catch( IllegalStateException e ) {
 			// expected behavior
 		}
 
 		// getting the first row only because SybaseASE15Dialect throws NullPointerException
 		// if data is not read before closing the ResultSet
 		sr.next();
 
 		// Closing the ScrollableResults does currently force batching to
 		// aggressively release the connection
 		sr.close();
 		SerializationHelper.serialize( s );
 
 		s.delete( silly );
 		s.flush();
 
 		release( s );
 		done();
 	}
 
 	@Test
 	public void testQueryIteration() throws Throwable {
 		prepare();
 		Session s = getSessionUnderTest();
 		Silly silly = new Silly( "silly" );
 		s.save( silly );
 		s.flush();
 
 		Iterator itr = s.createQuery( "from Silly" ).iterate();
 		assertTrue( itr.hasNext() );
 		Silly silly2 = ( Silly ) itr.next();
 		assertEquals( silly, silly2 );
 		Hibernate.close( itr );
 
 		itr = s.createQuery( "from Silly" ).iterate();
 		Iterator itr2 = s.createQuery( "from Silly where name = 'silly'" ).iterate();
 
 		assertTrue( itr.hasNext() );
 		assertEquals( silly, itr.next() );
 		assertTrue( itr2.hasNext() );
 		assertEquals( silly, itr2.next() );
 
 		Hibernate.close( itr );
 		Hibernate.close( itr2 );
 
 		s.delete( silly );
 		s.flush();
 
 		release( s );
 		done();
 	}
 
 	@Test
 	public void testQueryScrolling() throws Throwable {
 		prepare();
 		Session s = getSessionUnderTest();
 		Silly silly = new Silly( "silly" );
 		s.save( silly );
 		s.flush();
 
 		ScrollableResults sr = s.createQuery( "from Silly" ).scroll();
 		assertTrue( sr.next() );
 		Silly silly2 = ( Silly ) sr.get( 0 );
 		assertEquals( silly, silly2 );
 		sr.close();
 
 		sr = s.createQuery( "from Silly" ).scroll();
 		ScrollableResults sr2 = s.createQuery( "from Silly where name = 'silly'" ).scroll();
 
 		assertTrue( sr.next() );
 		assertEquals( silly, sr.get( 0 ) );
 		assertTrue( sr2.next() );
 		assertEquals( silly, sr2.get( 0 ) );
 
 		sr.close();
 		sr2.close();
 
 		s.delete( silly );
 		s.flush();
 
 		release( s );
 		done();
 	}
 
 	@Test
 	public void testSuppliedConnection() throws Throwable {
 		prepare();
 
 		Connection originalConnection = sessionFactory().getServiceRegistry().getService( ConnectionProvider.class ).getConnection();
 		Session session = sessionFactory().withOptions().connection( originalConnection ).openSession();
 
 		Silly silly = new Silly( "silly" );
 		session.save( silly );
 
 		// this will cause the connection manager to cycle through the aggressive release logic;
 		// it should not release the connection since we explicitly suplied it ourselves.
 		session.flush();
 		assertTrue( session.isConnected() );
 
 		session.delete( silly );
 		session.flush();
 
 		release( session );
 		done();
 
 		sessionFactory().getServiceRegistry().getService( ConnectionProvider.class ).closeConnection( originalConnection );
 	}
 
 	@Test
 	public void testConnectionMaintanenceDuringFlush() throws Throwable {
 		prepare();
 		Session s = getSessionUnderTest();
 		s.beginTransaction();
 
 		List entities = new ArrayList();
 		for ( int i = 0; i < 10; i++ ) {
 			Other other = new Other( "other-" + i );
 			Silly silly = new Silly( "silly-" + i, other );
 			entities.add( silly );
 			s.save( silly );
 		}
 		s.flush();
 
 		Iterator itr = entities.iterator();
 		while ( itr.hasNext() ) {
 			Silly silly = ( Silly ) itr.next();
 			silly.setName( "new-" + silly.getName() );
 			silly.getOther().setName( "new-" + silly.getOther().getName() );
 		}
 		long initialCount = sessionFactory().getStatistics().getConnectCount();
 		s.flush();
 		assertEquals( "connection not maintained through flush", initialCount + 1, sessionFactory().getStatistics().getConnectCount() );
 
 		s.createQuery( "delete from Silly" ).executeUpdate();
 		s.createQuery( "delete from Other" ).executeUpdate();
 		s.getTransaction().commit();
 		release( s );
 		done();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/immutable/entitywithmutablecollection/AbstractEntityWithOneToManyTest.java b/hibernate-core/src/test/java/org/hibernate/test/immutable/entitywithmutablecollection/AbstractEntityWithOneToManyTest.java
index 2f98adb274..ec77159c03 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/immutable/entitywithmutablecollection/AbstractEntityWithOneToManyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/immutable/entitywithmutablecollection/AbstractEntityWithOneToManyTest.java
@@ -1,1033 +1,1033 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.immutable.entitywithmutablecollection;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Projections;
 import org.hibernate.criterion.Restrictions;
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gail Badner
  */
 @SuppressWarnings( {"UnusedDeclaration"})
 public abstract class AbstractEntityWithOneToManyTest extends BaseCoreFunctionalTestCase {
 	private boolean isContractPartiesInverse;
 	private boolean isContractPartiesBidirectional;
 	private boolean isContractVariationsBidirectional;
 	private boolean isContractVersioned;
 	public void configure(Configuration cfg) {
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true");
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" );
 	}
 
 	protected boolean checkUpdateCountsAfterAddingExistingElement() {
 		return true;
 	}
 
 	protected boolean checkUpdateCountsAfterRemovingElementWithoutDelete() {
 		return true;
 	}
 
 	protected void prepareTest() throws Exception {
 		super.prepareTest();
 		isContractPartiesInverse = ( ( SessionFactoryImpl ) sessionFactory() ).getCollectionPersister( Contract.class.getName() + ".parties" ).isInverse();
 		try {
 			 ( ( SessionFactoryImpl ) sessionFactory() ).getEntityPersister( Party.class.getName() ).getPropertyType( "contract" );
 			isContractPartiesBidirectional = true;
 		}
 		catch ( QueryException ex) {
 			isContractPartiesBidirectional = false;
 		}
 		try {
 			 ( ( SessionFactoryImpl ) sessionFactory() ).getEntityPersister( ContractVariation.class.getName() ).getPropertyType( "contract" );
 			isContractVariationsBidirectional = true;
 		}
 		catch ( QueryException ex) {
 			isContractVariationsBidirectional = false;
 		}
 
 		isContractVersioned = ( ( SessionFactoryImpl ) sessionFactory() ).getEntityPersister( Contract.class.getName() ).isVersioned();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testUpdateProperty() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		c.addParty( new Party( "party" ) );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist(c);
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		c.setCustomerName( "yogi" );
 		assertEquals( 1, c.getParties().size() );
 		Party party = ( Party ) c.getParties().iterator().next();
 		party.setName( "new party" );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 1, c.getParties().size() );
 		party = ( Party ) c.getParties().iterator().next();
 		assertEquals( "party", party.getName() );
 		if ( isContractPartiesBidirectional ) {
 			assertSame( c, party.getContract() );
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testCreateWithNonEmptyOneToManyCollectionOfNew() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		c.addParty( new Party( "party" ) );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist(c);
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 1, c.getParties().size() );
 		Party party = ( Party ) c.getParties().iterator().next();
 		assertEquals( "party", party.getName() );
 		if ( isContractPartiesBidirectional ) {
 			assertSame( c, party.getContract() );
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testCreateWithNonEmptyOneToManyCollectionOfExisting() {
 		clearCounts();
 
 		Party party = new Party( "party" );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( party );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		c.addParty( party );
 		s = openSession();
 		t = s.beginTransaction();
 		s.save( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		// BUG, should be assertUpdateCount( ! isContractPartiesInverse && isPartyVersioned ? 1 : 0 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 0 , c.getParties().size() );
 			party = ( Party ) s.createCriteria( Party.class ).uniqueResult();
 			assertNull( party.getContract() );
 			s.delete( party );
 		}
 		else {
 			assertEquals( 1 , c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, party.getContract() );
 			}
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testAddNewOneToManyElementToPersistentEntity() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone" );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.get( Contract.class, c.getId() );
 		assertEquals( 0, c.getParties().size() );
 		c.addParty( new Party( "party" ) );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 1, c.getParties().size() );
 		Party party = ( Party ) c.getParties().iterator().next();
 		assertEquals( "party", party.getName() );
 		if ( isContractPartiesBidirectional ) {
 			assertSame( c, party.getContract() );
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testAddExistingOneToManyElementToPersistentEntity() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone" );
 		Party party = new Party( "party" );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		s.persist( party );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.get( Contract.class, c.getId() );
 		assertEquals( 0, c.getParties().size() );
 		party = ( Party ) s.get( Party.class, party.getId() );
 		if ( isContractPartiesBidirectional ) {
 			assertNull( party.getContract() );
 		}
 		c.addParty( party );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		if ( checkUpdateCountsAfterAddingExistingElement() ) {
 			assertUpdateCount( isContractVersioned && ! isContractPartiesInverse ? 1 : 0 );
 		}
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 0, c.getParties().size() );
 			s.delete( party );
 		}
 		else {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, party.getContract() );
 			}
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testCreateWithEmptyOneToManyCollectionUpdateWithExistingElement() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		s.persist( party );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.addParty( party );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		if ( checkUpdateCountsAfterAddingExistingElement() ) {
 			assertUpdateCount( isContractVersioned && ! isContractPartiesInverse ? 1 : 0 );
 		}
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria(Contract.class).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 0, c.getParties().size() );
 			s.delete( party );
 		}
 		else {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, party.getContract() );
 			}
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testCreateWithNonEmptyOneToManyCollectionUpdateWithNewElement() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 		c.addParty( party );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist(c);
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		Party newParty = new Party( "new party" );
 		c.addParty( newParty );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria(Contract.class).uniqueResult();
 		assertEquals( 2, c.getParties().size() );
 		for ( Object o : c.getParties() ) {
 			Party aParty = (Party) o;
 			if ( aParty.getId() == party.getId() ) {
 				assertEquals( "party", aParty.getName() );
 			}
 			else if ( aParty.getId() == newParty.getId() ) {
 				assertEquals( "new party", aParty.getName() );
 			}
 			else {
 				fail( "unknown party" );
 			}
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, aParty.getContract() );
 			}
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 3 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testCreateWithEmptyOneToManyCollectionMergeWithExistingElement() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		s.persist( party );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.addParty( party );
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.merge( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		if ( checkUpdateCountsAfterAddingExistingElement() ) {
 			assertUpdateCount( isContractVersioned && ! isContractPartiesInverse ? 1 : 0 );
 		}
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 0, c.getParties().size() );
 			s.delete( party );
 		}
 		else {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, party.getContract() );
 			}
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testCreateWithNonEmptyOneToManyCollectionMergeWithNewElement() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 		c.addParty( party );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist(c);
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		Party newParty = new Party( "new party" );
 		c.addParty( newParty );
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.merge( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria(Contract.class).uniqueResult();
 		assertEquals( 2, c.getParties().size() );
 		for ( Object o : c.getParties() ) {
 			Party aParty = (Party) o;
 			if ( aParty.getId() == party.getId() ) {
 				assertEquals( "party", aParty.getName() );
 			}
 			else if ( !aParty.getName().equals( newParty.getName() ) ) {
 				fail( "unknown party:" + aParty.getName() );
 			}
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, aParty.getContract() );
 			}
 		}
 		s.delete(c);
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 3 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testMoveOneToManyElementToNewEntityCollection() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		c.addParty( new Party( "party" ) );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist(c);
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 1, c.getParties().size() );
 		Party party = ( Party ) c.getParties().iterator().next();
 		assertEquals( "party", party.getName() );
 		if ( isContractPartiesBidirectional ) {
 			assertSame( c, party.getContract() );
 		}
 		c.removeParty( party );
 		Contract c2 = new Contract(null, "david", "phone" );
 		c2.addParty( party );
 		s.save( c2 );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 1 );
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).add( Restrictions.idEq( Long.valueOf( c.getId() ) )).uniqueResult();
 		c2 = (Contract) s.createCriteria( Contract.class ).add( Restrictions.idEq( Long.valueOf( c2.getId() ) )).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, party.getContract() );
 			}
 			assertEquals( 0, c2.getParties().size() );
 		}
 		else {
 			assertEquals( 0, c.getParties().size() );
 			assertEquals( 1, c2.getParties().size() );
 			party = ( Party ) c2.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c2, party.getContract() );
 			}
 		}
 		s.delete(c);
 		s.delete( c2 );
 		assertEquals( new Long( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( new Long( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 3 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testMoveOneToManyElementToExistingEntityCollection() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		c.addParty( new Party( "party" ) );
 		Contract c2 = new Contract(null, "david", "phone" );
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		s.persist( c2 );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 3 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).add( Restrictions.idEq( Long.valueOf( c.getId() ) )).uniqueResult();
 		assertEquals( 1, c.getParties().size() );
 		Party party = ( Party ) c.getParties().iterator().next();
 		assertEquals( "party", party.getName() );
 		if ( isContractPartiesBidirectional ) {
 			assertSame( c, party.getContract() );
 		}
 		c.removeParty( party );
 		c2 = (Contract) s.createCriteria( Contract.class ).add( Restrictions.idEq( Long.valueOf( c2.getId() ) )).uniqueResult();
 		c2.addParty( party );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 0 );
 		assertUpdateCount( isContractVersioned ? 2 : 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Contract) s.createCriteria( Contract.class ).add( Restrictions.idEq( Long.valueOf( c.getId() ) )).uniqueResult();
 		c2 = (Contract) s.createCriteria( Contract.class ).add( Restrictions.idEq( Long.valueOf( c2.getId() ) )).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c, party.getContract() );
 			}
 			assertEquals( 0, c2.getParties().size() );
 		}
 		else {
 			assertEquals( 0, c.getParties().size() );
 			assertEquals( 1, c2.getParties().size() );
 			party = ( Party ) c2.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			if ( isContractPartiesBidirectional ) {
 				assertSame( c2, party.getContract() );
 			}
 		}
 		s.delete(c);
 		s.delete( c2 );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Contract.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria( Party.class ).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 3 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testRemoveOneToManyElementUsingUpdate() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 		c.addParty( party );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.removeParty( party );
 		assertEquals( 0, c.getParties().size() );
 		if ( isContractPartiesBidirectional ) {
 			assertNull( party.getContract() );
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( c );
 		s.update( party );
 		t.commit();
 		s.close();
 
 		if ( checkUpdateCountsAfterRemovingElementWithoutDelete() ) {
 			assertUpdateCount( isContractVersioned && ! isContractPartiesInverse ? 1 : 0 );
 		}
 		assertDeleteCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.createCriteria( Contract.class ).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			assertSame( c, party.getContract() );
 		}
 		else {
 			assertEquals( 0, c.getParties().size() );
 			party = ( Party ) s.createCriteria( Party.class ).uniqueResult();
 			if ( isContractPartiesBidirectional ) {
 				assertNull( party.getContract() );
 			}
 			s.delete( party );
 		}
 		s.delete( c );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testRemoveOneToManyElementUsingMerge() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 		c.addParty( party );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.removeParty( party );
 		assertEquals( 0, c.getParties().size() );
 		if ( isContractPartiesBidirectional ) {
 			assertNull( party.getContract() );
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.merge( c );
 		party = ( Party ) s.merge( party );
 		t.commit();
 		s.close();
 
 		if ( checkUpdateCountsAfterRemovingElementWithoutDelete() ) {
 			assertUpdateCount( isContractVersioned && ! isContractPartiesInverse ? 1 : 0 );
 		}
 		assertDeleteCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.createCriteria( Contract.class ).uniqueResult();
 		if ( isContractPartiesInverse ) {
 			assertEquals( 1, c.getParties().size() );
 			party = ( Party ) c.getParties().iterator().next();
 			assertEquals( "party", party.getName() );
 			assertSame( c, party.getContract() );
 		}
 		else {
 			assertEquals( 0, c.getParties().size() );
 			party = ( Party ) s.createCriteria( Party.class ).uniqueResult();
 			if ( isContractPartiesBidirectional ) {
 				assertNull( party.getContract() );
 			}
 			s.delete( party );
 		}
 		s.delete( c );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 2 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testDeleteOneToManyElement() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 		c.addParty( party );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( c );
 		c.removeParty( party );
 		s.delete( party );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		assertDeleteCount( 1 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 0, c.getParties().size() );
 		party = ( Party ) s.createCriteria( Party.class ).uniqueResult();
 		assertNull( party );
 		s.delete( c );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 1 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testRemoveOneToManyElementByDelete() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		Party party = new Party( "party" );
 		c.addParty( party );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.removeParty( party );
 		assertEquals( 0, c.getParties().size() );
 		if ( isContractPartiesBidirectional ) {
 			assertNull( party.getContract() );
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( c );
 		s.delete( party );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		assertDeleteCount( 1 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 0, c.getParties().size() );
 		s.delete( c );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Party.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 1 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testRemoveOneToManyOrphanUsingUpdate() {
 		clearCounts();
 
 		Contract c = new Contract( null, "gail", "phone");
 		ContractVariation cv = new ContractVariation( 1, c );
 		cv.setText( "cv1" );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.getVariations().remove( cv );
 		cv.setContract( null );
 		assertEquals( 0, c.getVariations().size() );
 		if ( isContractVariationsBidirectional ) {
 			assertNull( cv.getContract() );
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.update( c );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		assertDeleteCount( 1 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 0, c.getVariations().size() );
 		cv = ( ContractVariation ) s.createCriteria( ContractVariation.class ).uniqueResult();
 		assertNull( cv );
 		s.delete( c );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(ContractVariation.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( 0 );
 		assertDeleteCount( 1 );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment", "UnnecessaryBoxing"})
 	public void testRemoveOneToManyOrphanUsingMerge() {
 		Contract c = new Contract( null, "gail", "phone");
 		ContractVariation cv = new ContractVariation( 1, c );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( c );
 		t.commit();
 		s.close();
 
 		assertInsertCount( 2 );
 		assertUpdateCount( 0 );
 		clearCounts();
 
 		c.getVariations().remove( cv );
 		cv.setContract( null );
 		assertEquals( 0, c.getVariations().size() );
 		if ( isContractVariationsBidirectional ) {
 			assertNull( cv.getContract() );
 		}
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.merge( c );
 		cv = ( ContractVariation ) s.merge( cv );
 		t.commit();
 		s.close();
 
 		assertUpdateCount( isContractVersioned ? 1 : 0 );
 		assertDeleteCount( 1 );
 		clearCounts();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = ( Contract ) s.createCriteria( Contract.class ).uniqueResult();
 		assertEquals( 0, c.getVariations().size() );
 		cv = ( ContractVariation ) s.createCriteria( ContractVariation.class ).uniqueResult();
 		assertNull( cv );
 		s.delete( c );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(Contract.class).setProjection( Projections.rowCount() ).uniqueResult() );
 		assertEquals( Long.valueOf( 0 ), s.createCriteria(ContractVariation.class).setProjection( Projections.rowCount() ).uniqueResult() );
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
index 78108fff61..cffcae4045 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/ParentChildTest.java
@@ -1,1052 +1,1052 @@
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
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.dialect.DB2Dialect;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.engine.EntityEntry;
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
 import org.hibernate.jdbc.AbstractWork;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.StandardBasicTypes;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 
 @SuppressWarnings( {"UnnecessaryBoxing"})
 public class ParentChildTest extends LegacyTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] {
 			"legacy/ParentChild.hbm.xml",
 			"legacy/FooBar.hbm.xml",
 		 	"legacy/Baz.hbm.xml",
 		 	"legacy/Qux.hbm.xml",
 		 	"legacy/Glarch.hbm.xml",
 		 	"legacy/Fum.hbm.xml",
 		 	"legacy/Fumm.hbm.xml",
 		 	"legacy/Fo.hbm.xml",
 		 	"legacy/One.hbm.xml",
 		 	"legacy/Many.hbm.xml",
 		 	"legacy/Immutable.hbm.xml",
 		 	"legacy/Fee.hbm.xml",
 		 	"legacy/Vetoer.hbm.xml",
 		 	"legacy/Holder.hbm.xml",
 		 	"legacy/Simple.hbm.xml",
 		 	"legacy/Container.hbm.xml",
 		 	"legacy/Circular.hbm.xml",
 		 	"legacy/Stuff.hbm.xml"
 		};
 	}
 
 	@Test
 	public void testReplicate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Container baz = new Container();
 		Contained f = new Contained();
 		List list = new ArrayList();
 		list.add(baz);
 		f.setBag(list);
 		List list2 = new ArrayList();
 		list2.add(f);
 		baz.setBag(list2);
 		s.save(f);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.OVERWRITE);
 		// HHH-2378
 		SessionImpl x = (SessionImpl)s;
 		EntityEntry entry = x.getPersistenceContext().getEntry( baz );
 		assertNull(entry.getVersion());
 		// ~~~~~~~
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.replicate(baz, ReplicationMode.IGNORE);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete(baz);
 		s.delete(f);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryOneToOne() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Serializable id = s.save( new Parent() );
 		assertTrue( s.createQuery( "from Parent p left join fetch p.child" ).list().size()==1 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Parent p = (Parent) s.createQuery("from Parent p left join fetch p.child").uniqueResult();
 		assertTrue( p.getChild()==null );
 		s.createQuery( "from Parent p join p.child c where c.x > 0" ).list();
 		s.createQuery( "from Child c join c.parent p where p.x > 0" ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( s.get(Parent.class, id) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testProxyReuse() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		FooProxy foo = new Foo();
 		FooProxy foo2 = new Foo();
 		Serializable id = s.save(foo);
 		Serializable id2 = s.save(foo2);
 		foo2.setInt(1234567);
 		foo.setInt(1234);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		foo2 = (FooProxy) s.load(Foo.class, id2);
 		assertFalse( Hibernate.isInitialized(foo) );
 		Hibernate.initialize(foo2);
 		Hibernate.initialize(foo);
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		assertTrue( foo2.getComponent().getImportantDates().length==4 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo.setKey( "xyzid" );
 		foo.setFloat( new Float( 1.2f ) );
 		foo2.setKey( (String) id ); //intentionally id, not id2!
 		foo2.setFloat( new Float(1.3f) );
 		foo2.getDependent().setKey( null );
 		foo2.getComponent().getSubcomponent().getFee().setKey(null);
 		assertFalse( foo2.getKey().equals( id ) );
 		s.save( foo );
 		s.update( foo2 );
 		assertEquals( foo2.getKey(), id );
 		assertTrue( foo2.getInt()==1234567 );
 		assertEquals( foo.getKey(), "xyzid" );
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (FooProxy) s.load(Foo.class, id);
 		assertTrue( foo.getInt()==1234567 );
 		assertTrue( foo.getComponent().getImportantDates().length==4 );
 		String feekey = foo.getDependent().getKey();
 		String fookey = foo.getKey();
 		s.delete(foo);
 		s.delete( s.get(Foo.class, id2) );
 		s.delete( s.get(Foo.class, "xyzid") );
 // here is the issue (HHH-4092).  After the deletes above there are 2 Fees and a Glarch unexpectedly hanging around
 		assertEquals( 2, doDelete( s, "from java.lang.Object" ) );
 		t.commit();
 		s.close();
 		
 		//to account for new id rollback shit
 		foo.setKey(fookey);
 		foo.getDependent().setKey(feekey);
 		foo.getComponent().setGlarch(null);
 		foo.getComponent().setSubcomponent(null);
 		
 		s = openSession();
 		t = s.beginTransaction();
 		//foo.getComponent().setGlarch(null); //no id property!
 		s.replicate(foo, ReplicationMode.OVERWRITE);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Foo refoo = (Foo) s.get(Foo.class, id);
 		assertEquals( feekey, refoo.getDependent().getKey() );
 		s.delete(refoo);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testComplexCriteria() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		baz.setDefaults();
 		Map topGlarchez = new HashMap();
 		baz.setTopGlarchez(topGlarchez);
 		Glarch g1 = new Glarch();
 		g1.setName("g1");
 		s.save(g1);
 		Glarch g2 = new Glarch();
 		g2.setName("g2");
 		s.save(g2);
 		g1.setProxyArray( new GlarchProxy[] {g2} );
 		topGlarchez.put( new Character('1'),g1 );
 		topGlarchez.put( new Character('2'), g2);
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		s.save(foo1);
 		s.save(foo2);
 		baz.getFooSet().add(foo1);
 		baz.getFooSet().add(foo2);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		LockMode lockMode = (getDialect() instanceof DB2Dialect) ? LockMode.READ : LockMode.UPGRADE;
 
 		Criteria crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") )
 			.createCriteria("proxyArray")
 				.add( Restrictions.eqProperty("name", "name") )
 				.add( Restrictions.eq("name", "g2") )
 				.add( Restrictions.gt("x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") )
 			.add( Restrictions.eq("string", "a string") )
 			.add( Restrictions.lt("integer", new Integer(-665) ) );
 		crit.createCriteria("fooArray")
 				// this is the bit causing the problems; creating the criteria on fooArray does not add it to FROM,
 				// and so restriction below leads to an invalid reference.
 			.add( Restrictions.eq("string", "a string") )
 			.setLockMode(lockMode);
 
 		List list = crit.list();
 		assertTrue( list.size()==2 );
 		
 		s.createCriteria(Glarch.class).setLockMode(LockMode.UPGRADE).list();
 		s.createCriteria(Glarch.class).setLockMode(Criteria.ROOT_ALIAS, LockMode.UPGRADE).list();
 		
 		g2.setName(null);
 		t.commit();
 		s.close();
 		
 		s = openSession();
 		t = s.beginTransaction();
 		
 		list = s.createCriteria(Baz.class).add( Restrictions.isEmpty("fooSet") ).list();
 		assertEquals( list.size(), 0 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.isNotEmpty("fooSet") ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 
 		list = s.createCriteria(Baz.class).add( Restrictions.sizeEq("fooSet", 2) ).list();
 		assertEquals( new HashSet(list).size(), 1 );
 		
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class)
 			.setLockMode(lockMode);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.gt( "x", new Integer(-666) ) );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 		list = crit.list();
 
 		assertTrue( list.size()==4 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 
 		list = s.createCriteria(Baz.class)
 			.createCriteria("fooSet")
 				.createCriteria("foo")
 					.createCriteria("component.glarch")
 						.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		list = s.createCriteria(Baz.class)
 			.createAlias("fooSet", "foo")
 			.createAlias("foo.foo", "foo2")
 			.setLockMode("foo2", lockMode)
 			.add( Restrictions.isNull("foo2.component.glarch") )
 			.createCriteria("foo2.component.glarch")
 				.add( Restrictions.eq("name", "xxx") )
 			.list();
 		assertTrue( list.size()==0 );
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		crit = s.createCriteria(Baz.class);
 		crit.createCriteria("topGlarchez")
 			.add( Restrictions.isNotNull("name") );
 		crit.createCriteria("fooSet")
 			.add( Restrictions.isNull("null") );
 
 		list = crit.list();
 		assertTrue( list.size()==2 );
 		baz = (Baz) crit.uniqueResult();
 		assertTrue( Hibernate.isInitialized(baz.getTopGlarchez()) ); //cos it is nonlazy
 		assertTrue( !Hibernate.isInitialized(baz.getFooSet()) );
 		
 		s.createCriteria(Child.class).setFetchMode("parent", FetchMode.JOIN).list();
 
 		doDelete( s, "from Glarch g" );
 		s.delete( s.get(Foo.class, foo1.getKey() ) );
 		s.delete( s.get(Foo.class, foo2.getKey() ) );
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testArrayHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b left join fetch b.fooArray").uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testArrayCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFooArray( new FooProxy[] { foo1 } );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria(Baz.class).createCriteria( "fooArray" ).uniqueResult();
 		assertEquals( 1, baz.getFooArray().length );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneHQL() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createQuery("from Baz b").uniqueResult();
 		assertFalse( Hibernate.isInitialized( baz.getFoo() ) );
 		assertTrue( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneCriteria() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.createCriteria( Baz.class ).uniqueResult();
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testLazyManyToOneGet() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo1 = new Foo();
 		s.save(foo1);
 		baz.setFoo( foo1 );
 
 		s.flush();
 		s.clear();
 
 		baz = ( Baz ) s.get( Baz.class, baz.getCode() );
 		assertTrue( Hibernate.isInitialized( baz.getFoo() ) );
 		assertFalse( baz.getFoo() instanceof HibernateProxy );
 
 		t.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhere() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getParts().add(p1);
 		baz.getParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.parts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testClassWhereManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setMoreParts( new ArrayList() );
 		Part p1 = new Part();
 		p1.setDescription("xyz");
 		Part p2 = new Part();
 		p2.setDescription("abc");
 		baz.getMoreParts().add(p1);
 		baz.getMoreParts().add(p2);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		assertTrue( s.createCriteria(Part.class).list().size()==1 ); //there is a where condition on Part mapping
 		assertTrue( s.createCriteria(Part.class).add( Restrictions.eq( "id", p1.getId() ) ).list().size()==1 );
 		assertTrue( s.createQuery("from Part").list().size()==1 );
 		assertTrue( s.createQuery("from Baz baz join baz.moreParts").list().size()==2 );
 		baz = (Baz) s.createCriteria(Baz.class).uniqueResult();
 		assertTrue( s.createFilter( baz.getMoreParts(), "" ).list().size()==2 );
 		//assertTrue( baz.getParts().size()==1 );
 		s.delete( s.get( Part.class, p1.getId() ));
 		s.delete( s.get( Part.class, p2.getId() ));
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionQuery() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Simple s1 = new Simple( Long.valueOf(1) );
 		s1.setName("s");
 		s1.setCount(0);
 		Simple s2 = new Simple( Long.valueOf(2) );
 		s2.setCount(2);
 		Simple s3 = new Simple( Long.valueOf(3) );
 		s3.setCount(3);
 		s.save( s1 );
 		s.save( s2 );
 		s.save( s3 );
 		Container c = new Container();
 		Contained cd = new Contained();
 		List bag = new ArrayList();
 		bag.add(cd);
 		c.setBag(bag);
 		List l = new ArrayList();
 		l.add(s1);
 		l.add(s3);
 		l.add(s2);
 		c.setOneToMany(l);
 		l = new ArrayList();
 		l.add(s1);
 		l.add(null);
 		l.add(s2);
 		c.setManyToMany(l);
 		s.save(c);
 		Container cx = new Container();
 		s.save(cx);
 		Simple sx = new Simple( Long.valueOf(5) );
 		sx.setCount(5);
 		sx.setName("s");
 		s.save( sx );
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.oneToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where c.manyToMany[2] = s" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.oneToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c, Simple s where s = c.manyToMany[2]" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[0].name = 's'" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.oneToMany[2 - 2].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where 's' = c.manyToMany[(3+1)/4-1].name" ).list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		assertTrue(
 				s.createQuery( "select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'" )
 						.list()
 						.size() == 1
 		);
 		if ( ! ( getDialect() instanceof MySQLDialect ) && !(getDialect() instanceof org.hibernate.dialect.TimesTenDialect) ) {
 			assertTrue(
 					s.createQuery( "select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2" )
 							.list()
 							.size() == 1
 			);
 		}
 		assertTrue( s.contains(cd) );
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) )  {
 			s.createFilter( c.getBag(), "where 0 in elements(this.bag)" ).list();
 			s.createFilter( c.getBag(), "where 0 in elements(this.lazyBag)" ).list();
 		}
 		s.createQuery( "select count(comp.name) from ContainerX c join c.components comp" ).list();
 		s.delete(cd);
 		s.delete(c);
 		s.delete(s1);
 		s.delete(s2);
 		s.delete(s3);
 		s.delete(cx);
 		s.delete(sx);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		Child c = new Child();
 		c.setParent(p);
 		p.setChild(c);
 		s.save(p);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 parent", p!=null );
 		c.setCount(32);
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Child) s.load( Child.class, new Long( c.getId() ) );
 		p = c.getParent();
 		assertTrue( "1-1 update", p.getCount()==66 );
 		assertTrue( "1-1 update", c.getCount()==32 );
 		assertTrue(
 			"1-1 query",
 				s.createQuery( "from Child c where c.parent.count=66" ).list().size()==1
 		);
 		assertTrue(
 			"1-1 query",
 			( (Object[]) s.createQuery( "from Parent p join p.child c where p.count=66" ).list().get(0) ).length==2
 		);
 		s.createQuery( "select c, c.parent from Child c order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent from Child c where c.parent.count=66 order by c.parent.count" ).list();
 		s.createQuery( "select c, c.parent, c.parent.count from Child c order by c.parent.count" ).iterate();
 		List result = s.createQuery( "FROM Parent AS p WHERE p.count = ?" )
 				.setParameter( 0, new Integer(66), StandardBasicTypes.INTEGER )
 				.list();
 		assertEquals( "1-1 query", 1, result.size() );
 		s.delete(c); s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testParentNullChild() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Parent p = new Parent();
 		s.save(p);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( p.getChild()==null );
 		p.setCount(66);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		p = (Parent) s.load( Parent.class, new Long( p.getId() ) );
 		assertTrue( "null 1-1 update", p.getCount()==66 );
 		assertTrue( p.getChild()==null );
 		s.delete(p);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		c.setManyToMany( new ArrayList() );
 		c.setBag( new ArrayList() );
 		Simple s1 = new Simple( Long.valueOf(12) );
 		Simple s2 = new Simple( Long.valueOf(-1) );
 		s1.setCount(123); s2.setCount(654);
 		Contained c1 = new Contained();
 		c1.setBag( new ArrayList() );
 		c1.getBag().add(c);
 		c.getBag().add(c1);
 		c.getManyToMany().add(s1);
 		c.getManyToMany().add(s2);
 		Serializable cid = s.save(c);
 		s.save( s1 );
 		s.save( s2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==1 );
 		assertTrue( c.getManyToMany().size()==2 );
 		c1 = (Contained) c.getBag().iterator().next();
 		assertTrue( c.getBag().size()==1 );
 		c.getBag().remove(c1);
 		c1.getBag().remove(c);
 		assertTrue( c.getManyToMany().remove(0)!=null );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load(Container.class, cid);
 		assertTrue( c.getBag().size()==0 );
 		assertTrue( c.getManyToMany().size()==1 );
 		c1 = (Contained) s.load( Contained.class, new Long(c1.getId()) );
 		assertTrue( c1.getBag().size()==0 );
 		assertEquals( 1, doDelete( s, "from ContainerX c" ) );
 		assertEquals( 1, doDelete( s, "from Contained" ) );
 		assertEquals( 2, doDelete( s, "from Simple" ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testContainer() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Simple x = new Simple( Long.valueOf(1) );
 		x.setCount(123);
 		Simple y = new Simple( Long.valueOf(0) );
 		y.setCount(456);
 		s.save( x );
 		s.save( y );
 		List o2m = new ArrayList();
 		o2m.add(x); o2m.add(null); o2m.add(y);
 		List m2m = new ArrayList();
 		m2m.add(x); m2m.add(null); m2m.add(y);
 		c.setOneToMany(o2m); c.setManyToMany(m2m);
 		List comps = new ArrayList();
 		Container.ContainerInnerClass ccic = new Container.ContainerInnerClass();
 		ccic.setName("foo");
 		ccic.setSimple(x);
 		comps.add(ccic);
 		comps.add(null);
 		ccic = new Container.ContainerInnerClass();
 		ccic.setName("bar");
 		ccic.setSimple(y);
 		comps.add(ccic);
 		HashSet compos = new HashSet();
 		compos.add(ccic);
 		c.setComposites(compos);
 		c.setComponents(comps);
 		One one = new One();
 		Many many = new Many();
 		HashSet manies = new HashSet();
 		manies.add(many);
 		one.setManies(manies);
 		many.setOne(one);
 		ccic.setMany(many);
 		ccic.setOne(one);
 		s.save(one);
 		s.save(many);
 		s.save(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Long count = (Long) s.createQuery("select count(*) from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'").uniqueResult();
 		assertTrue( count.intValue()==1 );
 		List res = s.createQuery(
 				"select c, s from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'"
 		).list();
 		assertTrue(res.size()==1);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		ccic = (Container.ContainerInnerClass) c.getComponents().get(2);
 		assertTrue( ccic.getMany().getOne()==ccic.getOne() );
 		assertTrue( c.getComponents().size()==3 );
 		assertTrue( c.getComposites().size()==1 );
 		assertTrue( c.getOneToMany().size()==3 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(2)!=null );
 		for ( int i=0; i<3; i++ ) {
 			assertTrue( c.getManyToMany().get(i) == c.getOneToMany().get(i) );
 		}
 		Object o1 = c.getOneToMany().get(0);
 		Object o2 = c.getOneToMany().remove(2);
 		c.getOneToMany().set(0, o2);
 		c.getOneToMany().set(1, o1);
 		o1 = c.getComponents().remove(2);
 		c.getComponents().set(0, o1);
 		c.getManyToMany().set( 0, c.getManyToMany().get(2) );
 		Container.ContainerInnerClass ccic2 = new Container.ContainerInnerClass();
 		ccic2.setName("foo");
 		ccic2.setOne(one);
 		ccic2.setMany(many);
 		ccic2.setSimple( (Simple) s.load(Simple.class, new Long(0) ) );
 		c.getComposites().add(ccic2);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( c.getOneToMany().size()==2 );
 		assertTrue( c.getManyToMany().size()==3 );
 		assertTrue( c.getOneToMany().get(0)!=null );
 		assertTrue( c.getOneToMany().get(1)!=null );
 		( (Container.ContainerInnerClass) c.getComponents().get(0) ).setName("a different name");
 		( (Container.ContainerInnerClass) c.getComposites().iterator().next() ).setName("once again");
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		System.out.println( c.getOneToMany() );
 		System.out.println( c.getManyToMany() );
 		System.out.println( c.getComponents() );
 		System.out.println( c.getComposites() );
 		assertTrue( c.getComponents().size()==1 ); //WAS: 2
 		assertTrue( c.getComposites().size()==2 );
 		assertTrue( ( (Container.ContainerInnerClass) c.getComponents().get(0) ).getName().equals("a different name") );
 		Iterator iter = c.getComposites().iterator();
 		boolean found = false;
 		while ( iter.hasNext() ) {
 			if ( ( (Container.ContainerInnerClass) iter.next() ).getName().equals("once again") ) found = true;
 		}
 		assertTrue(found);
 		c.getOneToMany().clear();
 		c.getManyToMany().clear();
 		c.getComposites().clear();
 		c.getComponents().clear();
 		doDelete( s, "from Simple" );
 		doDelete( s, "from Many" );
 		doDelete( s, "from One" );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.load( Container.class, new Long( c.getId() ) );
 		assertTrue( c.getComponents().size()==0 );
 		assertTrue( c.getComposites().size()==0 );
 		assertTrue( c.getOneToMany().size()==0 );
 		assertTrue( c.getManyToMany().size()==0 );
 		s.delete(c);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeCompositeElements() throws Exception {
 		Container c = new Container();
 		List list = new ArrayList();
 		c.setCascades(list);
 		Container.ContainerInnerClass cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		Session s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 
 		c = new Container();
 		s = openSession();
 		s.beginTransaction();
 		s.save(c);
 		list = new ArrayList();
 		c.setCascades(list);
 		cic = new Container.ContainerInnerClass();
 		cic.setMany( new Many() );
 		cic.setOne( new One() );
 		list.add(cic);
 		s.getTransaction().commit();
 		s.close();
 		
 		s=openSession();
 		s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).iterate().next();
 		cic = (Container.ContainerInnerClass) c.getCascades().iterator().next();
 		assertTrue( cic.getMany()!=null && cic.getOne()!=null );
 		assertTrue( c.getCascades().size()==1 );
 		s.delete(c);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBag() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container c = new Container();
 		Contained c1 = new Contained();
 		Contained c2 = new Contained();
 		c.setBag( new ArrayList() );
 		c.getBag().add(c1);
 		c.getBag().add(c2);
 		c1.getBag().add(c);
 		c2.getBag().add(c);
 		s.save(c);
 		c.getBag().add(c2);
 		c2.getBag().add(c);
 		c.getLazyBag().add(c1);
 		c1.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		c.getLazyBag().size();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c3 = new Contained();
 		//c.getBag().add(c3);
 		//c3.getBag().add(c);
 		c.getLazyBag().add(c3);
 		c3.getLazyBag().add(c);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Contained c4 = new Contained();
 		c.getLazyBag().add(c4);
 		c4.getLazyBag().add(c);
 		assertTrue( c.getLazyBag().size()==3 ); //forces initialization
 		//s.save(c4);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		c = (Container) s.createQuery( "from ContainerX c" ).list().get(0);
 		Iterator i = c.getBag().iterator();
 		int j=0;
 		while ( i.hasNext() ) {
 			assertTrue( i.next()!=null );
 			j++;
 		}
 		assertTrue(j==3);
 		assertTrue( c.getLazyBag().size()==3 );
 		s.delete(c);
 		c.getBag().remove(c2);
 		Iterator iter = c.getBag().iterator();
 		j=0;
 		while ( iter.hasNext() ) {
 			j++;
 			s.delete( iter.next() );
 		}
 		assertTrue(j==2);
 		s.delete( s.load(Contained.class, new Long( c4.getId() ) ) );
 		s.delete( s.load(Contained.class, new Long( c3.getId() ) ) );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCircularCascade() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Circular c = new Circular();
 		c.setClazz(Circular.class);
 		c.setOther( new Circular() );
 		c.getOther().setOther( new Circular() );
 		c.getOther().getOther().setOther(c);
 		c.setAnyEntity( c.getOther() );
 		String id = (String) s.save(c);
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
diff --git a/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java b/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
index 4341ed0f6b..ef7041e14c 100755
--- a/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/proxy/ProxyTest.java
@@ -1,434 +1,434 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2006-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.proxy;
 
 import java.math.BigDecimal;
 import java.util.List;
 
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.LockMode;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.proxy.HibernateProxy;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gavin King
  */
 public class ProxyTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "proxy/DataPoint.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" ); // problem on HSQLDB (go figure)
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	@Test
 	public void testFinalizeFiltered() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load(DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 
 		try {
 			dp.getClass().getDeclaredMethod( "finalize", (Class[]) null );
 			fail();
 
 		}
 		catch (NoSuchMethodException e) {}
 
 		s.delete(dp);
 		t.commit();
 		s.close();
 
 	}
 
 	@Test
 	public void testProxyException() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load(DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 
 		try {
 			dp.exception();
 			fail();
 		}
 		catch (Exception e) {
 			assertTrue( e.getClass()==Exception.class );
 		}
 		s.delete(dp);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxySerializationAfterSessionClosed() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		s.close();
 		SerializationHelper.clone( dp );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testInitializedProxySerializationAfterSessionClosed() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		Hibernate.initialize( dp );
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.close();
 		SerializationHelper.clone( dp );
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxySerialization() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp.getId();
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp.getDescription();
 		assertTrue( Hibernate.isInitialized(dp) );
 		Object none = s.load( DataPoint.class, new Long(666));
 		assertFalse( Hibernate.isInitialized(none) );
 
 		t.commit();
 		s.disconnect();
 
 		Object[] holder = new Object[] { s, dp, none };
 
 		holder = (Object[]) SerializationHelper.clone(holder);
 		Session sclone = (Session) holder[0];
 		dp = (DataPoint) holder[1];
 		none = holder[2];
 
 		//close the original:
 		s.close();
 
 		t = sclone.beginTransaction();
 
 		DataPoint sdp = (DataPoint) sclone.load( DataPoint.class, new Long( dp.getId() ) );
 		assertSame(dp, sdp);
 		assertFalse(sdp instanceof HibernateProxy);
 		Object snone = sclone.load( DataPoint.class, new Long(666) );
 		assertSame(none, snone);
 		assertTrue(snone instanceof HibernateProxy);
 
 		sclone.delete(dp);
 
 		t.commit();
 		sclone.close();
 
 	}
 
 	@Test
 	public void testProxy() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		DataPoint dp = new DataPoint();
 		dp.setDescription("a data point");
 		dp.setX( new BigDecimal(1.0) );
 		dp.setY( new BigDecimal(2.0) );
 		s.persist(dp);
 		s.flush();
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long(dp.getId() ));
 		assertFalse( Hibernate.isInitialized(dp) );
 		DataPoint dp2 = (DataPoint) s.get( DataPoint.class, new Long(dp.getId()) );
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ), LockMode.NONE );
 		assertSame(dp, dp2);
 		assertFalse( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.load( DataPoint.class, new Long( dp.getId() ), LockMode.READ );
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long (dp.getId() ));
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.get( DataPoint.class, new Long ( dp.getId() ) , LockMode.READ );
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.clear();
 
 		dp = (DataPoint) s.load( DataPoint.class, new Long  ( dp.getId() ) );
 		assertFalse( Hibernate.isInitialized(dp) );
 		dp2 = (DataPoint) s.createQuery("from DataPoint").uniqueResult();
 		assertSame(dp, dp2);
 		assertTrue( Hibernate.isInitialized(dp) );
 		s.delete( dp );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testSubsequentNonExistentProxyAccess() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		DataPoint proxy = ( DataPoint ) s.load( DataPoint.class, new Long(-1) );
 		assertFalse( Hibernate.isInitialized( proxy ) );
 		try {
 			proxy.getDescription();
 			fail( "proxy access did not fail on non-existent proxy" );
 		}
 		catch( ObjectNotFoundException onfe ) {
 			// expected
 		}
 		catch( Throwable e ) {
 			fail( "unexpected exception type on non-existent proxy access : " + e );
 		}
 		// try it a second (subsequent) time...
 		try {
 			proxy.getDescription();
 			fail( "proxy access did not fail on non-existent proxy" );
 		}
 		catch( ObjectNotFoundException onfe ) {
 			// expected
 		}
 		catch( Throwable e ) {
 			fail( "unexpected exception type on non-existent proxy access : " + e );
 		}
 
 		t.commit();
 		s.close();
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	@Test
 	public void testProxyEviction() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Container container = new Container( "container" );
 		container.setOwner( new Owner( "owner" ) );
 		container.setInfo( new Info( "blah blah blah" ) );
 		container.getDataPoints().add( new DataPoint( new BigDecimal( 1 ), new BigDecimal( 1 ), "first data point" ) );
 		container.getDataPoints().add( new DataPoint( new BigDecimal( 2 ), new BigDecimal( 2 ), "second data point" ) );
 		s.save( container );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Container c = ( Container ) s.load( Container.class, container.getId() );
 		assertFalse( Hibernate.isInitialized( c ) );
 		s.evict( c );
 		try {
 			c.getName();
 			fail( "expecting LazyInitializationException" );
 		}
 		catch( LazyInitializationException e ) {
 			// expected result
 		}
 
 		c = ( Container ) s.load( Container.class, container.getId() );
 		assertFalse( Hibernate.isInitialized( c ) );
 		Info i = c.getInfo();
 		assertTrue( Hibernate.isInitialized( c ) );
 		assertFalse( Hibernate.isInitialized( i ) );
 		s.evict( c );
 		try {
 			i.getDetails();
 			fail( "expecting LazyInitializationException" );
 		}
 		catch( LazyInitializationException e ) {
 			// expected result
 		}
 
 		s.delete( c );
 
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testFullyLoadedPCSerialization() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Long lastContainerId = null;
 		int containerCount = 10;
 		int nestedDataPointCount = 5;
 		for ( int c_indx = 0; c_indx < containerCount; c_indx++ ) {
 			Owner owner = new Owner( "Owner #" + c_indx );
 			Container container = new Container( "Container #" + c_indx );
 			container.setOwner( owner );
 			for ( int dp_indx = 0; dp_indx < nestedDataPointCount; dp_indx++ ) {
 				DataPoint dp = new DataPoint();
 				dp.setDescription( "data-point [" + c_indx + ", " + dp_indx + "]" );
 // more HSQLDB fun...
 //				dp.setX( new BigDecimal( c_indx ) );
 				dp.setX( new BigDecimal( c_indx + dp_indx ) );
 				dp.setY( new BigDecimal( dp_indx ) );
 				container.getDataPoints().add( dp );
 			}
 			s.save( container );
 			lastContainerId = container.getId();
 		}
 		t.commit();
 		s.close();
 
 		s = openSession();
 		s.setFlushMode( FlushMode.MANUAL );
 		t = s.beginTransaction();
 		// load the last container as a proxy
 		Container proxy = ( Container ) s.load( Container.class, lastContainerId );
 		assertFalse( Hibernate.isInitialized( proxy ) );
 		// load the rest back into the PC
 		List all = s.createQuery( "from Container as c inner join fetch c.owner inner join fetch c.dataPoints where c.id <> :last" )
 				.setLong( "last", lastContainerId.longValue() )
 				.list();
 		Container container = ( Container ) all.get( 0 );
 		s.delete( container );
 		// force a snapshot retrieval of the proxied container
 		SessionImpl sImpl = ( SessionImpl ) s;
 		sImpl.getPersistenceContext().getDatabaseSnapshot(
 				lastContainerId,
 		        sImpl.getFactory().getEntityPersister( Container.class.getName() )
 		);
 		assertFalse( Hibernate.isInitialized( proxy ) );
 		t.commit();
 
 //		int iterations = 50;
 //		long cumulativeTime = 0;
 //		long cumulativeSize = 0;
 //		for ( int i = 0; i < iterations; i++ ) {
 //			final long start = System.currentTimeMillis();
 //			byte[] bytes = SerializationHelper.serialize( s );
 //			SerializationHelper.deserialize( bytes );
 //			final long end = System.currentTimeMillis();
 //			cumulativeTime += ( end - start );
 //			int size = bytes.length;
 //			cumulativeSize += size;
 ////			System.out.println( "Iteration #" + i + " took " + ( end - start ) + " ms : size = " + size + " bytes" );
 //		}
 //		System.out.println( "Average time : " + ( cumulativeTime / iterations ) + " ms" );
 //		System.out.println( "Average size : " + ( cumulativeSize / iterations ) + " bytes" );
 
 		byte[] bytes = SerializationHelper.serialize( s );
 		SerializationHelper.deserialize( bytes );
 
 		t = s.beginTransaction();
 		int count = s.createQuery( "delete DataPoint" ).executeUpdate();
 		assertEquals( "unexpected DP delete count", ( containerCount * nestedDataPointCount ), count );
 		count = s.createQuery( "delete Container" ).executeUpdate();
 		assertEquals( "unexpected container delete count", containerCount, count );
 		count = s.createQuery( "delete Owner" ).executeUpdate();
 		assertEquals( "unexpected owner delete count", containerCount, count );
 		t.commit();
 		s.close();
 	}
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
index 9c792c2df3..870c920f4c 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/querycache/AbstractQueryCacheResultTransformerTest.java
@@ -1,1046 +1,1046 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.querycache;
 
 import java.lang.reflect.Constructor;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.CriteriaSpecification;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projections;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.transform.Transformers;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractQueryCacheResultTransformerTest extends BaseCoreFunctionalTestCase {
 	private Student yogiExpected;
 	private Student shermanExpected;
 	private CourseMeeting courseMeetingExpected1;
 	private CourseMeeting courseMeetingExpected2;
 	private Course courseExpected;
 	private Enrolment yogiEnrolmentExpected;
 	private Enrolment shermanEnrolmentExpected;
 
 	@Override
 	public String[] getMappings() {
 		return new String[] { "querycache/Enrolment.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.CACHE_REGION_PREFIX, "foo" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	protected abstract class CriteriaExecutor extends QueryExecutor {
 		protected abstract Criteria getCriteria(Session s) throws Exception;
 		@Override
         protected Object getResults(Session s, boolean isSingleResult) throws Exception {
 			Criteria criteria = getCriteria( s ).setCacheable( getQueryCacheMode() != CacheMode.IGNORE ).setCacheMode( getQueryCacheMode() );
 			return ( isSingleResult ? criteria.uniqueResult() : criteria.list() );
 		}
 	}
 
 	protected abstract class HqlExecutor extends QueryExecutor {
 		protected abstract Query getQuery(Session s);
 		@Override
         protected Object getResults(Session s, boolean isSingleResult) {
 			Query query = getQuery( s ).setCacheable( getQueryCacheMode() != CacheMode.IGNORE ).setCacheMode( getQueryCacheMode() );
 			return ( isSingleResult ? query.uniqueResult() : query.list() );
 		}
 	}
 
 	protected abstract class QueryExecutor {
 		public Object execute(boolean isSingleResult) throws Exception{
 			Session s = openSession();
 			Transaction t = s.beginTransaction();
 			Object result = null;
 			try {
 				result = getResults( s, isSingleResult );
 				t.commit();
 			}
 			catch ( Exception ex ) {
 				t.rollback();
 				throw ex;
 			}
 			finally {
 				s.close();
 			}
 			return result;
 		}
 		protected abstract Object getResults(Session s, boolean isSingleResult) throws Exception;
 	}
 
 	protected interface ResultChecker {
 		void check(Object results);
 	}
 
 	protected abstract CacheMode getQueryCacheMode();
 
 	protected boolean areDynamicNonLazyAssociationsChecked() {
 		return true;
 	}
 
 	protected void createData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		courseExpected = new Course();
 		courseExpected.setCourseCode( "HIB" );
 		courseExpected.setDescription( "Hibernate Training" );
 		courseMeetingExpected1 = new CourseMeeting( courseExpected, "Monday", 1, "1313 Mockingbird Lane" );
 		courseMeetingExpected2 = new CourseMeeting( courseExpected, "Tuesday", 2, "1313 Mockingbird Lane" );
 		courseExpected.getCourseMeetings().add( courseMeetingExpected1 );
 		courseExpected.getCourseMeetings().add( courseMeetingExpected2 );
 		s.save( courseExpected );
 
 		yogiExpected = new Student();
 		yogiExpected.setName( new PersonName( "Yogi", "The", "Bear" ) );
 		yogiExpected.setStudentNumber( 111 );
 		yogiExpected.setPreferredCourse( courseExpected );
 		List yogiSecretCodes = new ArrayList();
 		yogiSecretCodes.add( Integer.valueOf( 0 ) );
 		yogiExpected.setSecretCodes( yogiSecretCodes );
 		s.save( yogiExpected );
 
 		Address address1 = new Address( yogiExpected, "home", "1 Main Street", "Podunk", "WA", "98000", "USA" );
 		Address address2 = new Address( yogiExpected, "work", "2 Main Street", "NotPodunk", "WA", "98001", "USA" );
 		yogiExpected.getAddresses().put( address1.getAddressType(), address1 );
 		yogiExpected.getAddresses().put( address2.getAddressType(), address2  );
 		s.save( address1 );
 		s.save( address2 );
 
 		shermanExpected = new Student();
 		shermanExpected.setName( new PersonName( "Sherman", null, "Grote" ) );
 		shermanExpected.setStudentNumber( 999 );
 		List shermanSecretCodes = new ArrayList();
 		shermanSecretCodes.add( Integer.valueOf( 1 ) );
 		shermanSecretCodes.add( Integer.valueOf( 2 ) );
 		shermanExpected.setSecretCodes( shermanSecretCodes );
 		s.save( shermanExpected );
 
 		shermanEnrolmentExpected = new Enrolment();
 		shermanEnrolmentExpected.setCourse( courseExpected );
 		shermanEnrolmentExpected.setCourseCode( courseExpected.getCourseCode() );
 		shermanEnrolmentExpected.setSemester( ( short ) 1 );
 		shermanEnrolmentExpected.setYear( ( short ) 1999 );
 		shermanEnrolmentExpected.setStudent( shermanExpected );
 		shermanEnrolmentExpected.setStudentNumber( shermanExpected.getStudentNumber() );
 		shermanExpected.getEnrolments().add( shermanEnrolmentExpected );
 		s.save( shermanEnrolmentExpected );
 
 		yogiEnrolmentExpected = new Enrolment();
 		yogiEnrolmentExpected.setCourse( courseExpected );
 		yogiEnrolmentExpected.setCourseCode( courseExpected.getCourseCode() );
 		yogiEnrolmentExpected.setSemester( ( short ) 3 );
 		yogiEnrolmentExpected.setYear( ( short ) 1998 );
 		yogiEnrolmentExpected.setStudent( yogiExpected );
 		yogiEnrolmentExpected.setStudentNumber( yogiExpected.getStudentNumber() );
 		yogiExpected.getEnrolments().add( yogiEnrolmentExpected );
 		s.save( yogiEnrolmentExpected );
 
 		t.commit();
 		s.close();
 	}
 
 	protected void deleteData() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.delete( yogiExpected );
 		s.delete( shermanExpected );
 		s.delete( yogiEnrolmentExpected );
 		s.delete( shermanEnrolmentExpected );
 		s.delete( courseMeetingExpected1 );
 		s.delete( courseMeetingExpected2 );
 		s.delete( courseExpected );
 		t.commit();
 		s.close();
 	}
 
 
 	public void testAliasToEntityMapNoProjectionList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			@Override
             protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.enrolments", "e", CriteriaSpecification.LEFT_JOIN )
 						.createAlias( "e.course", "c", CriteriaSpecification.LEFT_JOIN )
 								.setResultTransformer( CriteriaSpecification.ALIAS_TO_ENTITY_MAP )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			@Override
             public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.enrolments e left join e.course c order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap = ( Map ) resultList.get( 0 );
 				assertEquals( 3, yogiMap.size() );
 				Map shermanMap = ( Map ) resultList.get( 1 );
 				assertEquals( 3, shermanMap.size() );
 				assertEquals( yogiExpected, yogiMap.get( "s" ) );
 				assertEquals( yogiEnrolmentExpected, yogiMap.get( "e" ) );
 				assertEquals( courseExpected, yogiMap.get( "c" ) );
 				assertEquals( shermanExpected, shermanMap.get( "s" ) );
 				assertEquals( shermanEnrolmentExpected, shermanMap.get( "e" ) );
 				assertEquals( courseExpected, shermanMap.get( "c" ) );
 				assertSame( ( ( Map ) resultList.get( 0 ) ).get( "c" ), shermanMap.get( "c" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapNoProjectionMultiAndNullList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			@Override
             protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "p", CriteriaSpecification.LEFT_JOIN )
 						.createAlias( "s.addresses", "a", CriteriaSpecification.LEFT_JOIN )
 								.setResultTransformer( CriteriaSpecification.ALIAS_TO_ENTITY_MAP )
 						.addOrder( Order.asc( "s.studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			@Override
             public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.preferredCourse p left join s.addresses a order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				Map yogiMap1 = ( Map ) resultList.get( 0 );
 				assertEquals( 3, yogiMap1.size() );
 				Map yogiMap2 = ( Map ) resultList.get( 1 );
 				assertEquals( 3, yogiMap2.size() );
 				Map shermanMap = ( Map ) resultList.get( 2 );
 				assertEquals( 3, shermanMap.size() );
 				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
 				assertEquals( courseExpected, yogiMap1.get( "p" ) );
 				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
 						yogiMap1.get( "a" ));
 				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
 				assertEquals( courseExpected, yogiMap2.get( "p" ) );
 				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
 						yogiMap2.get( "a" ));
 				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
 				assertSame( yogiMap1.get( "p" ), yogiMap2.get( "p" ) );
 				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
 				assertEquals( shermanExpected, shermanMap.get( "s" ) );
 				assertEquals( shermanExpected.getPreferredCourse(), shermanMap.get( "p" ) );
 				assertNull( shermanMap.get( "a" ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testAliasToEntityMapNoProjectionNullAndNonNullAliasList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			@Override
             protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", CriteriaSpecification.LEFT_JOIN )
 								.setResultTransformer( CriteriaSpecification.ALIAS_TO_ENTITY_MAP )
 						.createCriteria( "s.preferredCourse", CriteriaSpecification.INNER_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			@Override
             public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join s.addresses a left join s.preferredCourse order by s.studentNumber" )
 						.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Map yogiMap1 = ( Map ) resultList.get( 0 );
 				assertEquals( 2, yogiMap1.size() );
 				Map yogiMap2 = ( Map ) resultList.get( 1 );
 				assertEquals( 2, yogiMap2.size() );
 				assertEquals( yogiExpected, yogiMap1.get( "s" ) );
 				Address yogiAddress1 = ( Address ) yogiMap1.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress1.getAddressType() ),
 						yogiMap1.get( "a" ));
 				assertEquals( yogiExpected, yogiMap2.get( "s" ) );
 				Address yogiAddress2 = ( Address ) yogiMap2.get( "a" );
 				assertEquals( yogiExpected.getAddresses().get( yogiAddress2.getAddressType() ),
 						yogiMap2.get( "a" ));
 				assertSame( yogiMap1.get( "s" ), yogiMap2.get( "s" ) );
 				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithNonLazyOneToManyUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Course.class );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Course" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Course );
 				assertEquals( courseExpected, results );
 				assertTrue( Hibernate.isInitialized( ((Course) courseExpected).getCourseMeetings() ) );
 				assertEquals( courseExpected.getCourseMeetings(), ((Course) courseExpected).getCourseMeetings() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testEntityWithNonLazyManyToOneList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( CourseMeeting.class )
 						.addOrder( Order.asc( "id.day" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			protected Query getQuery(Session s) {
 				return s.createQuery( "from CourseMeeting order by id.day" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( courseMeetingExpected1, resultList.get( 0 ) );
 				assertEquals( courseMeetingExpected2, resultList.get( 1 ) );
 				assertTrue( Hibernate.isInitialized( ((CourseMeeting) resultList.get( 0 )).getCourse() ) );
 				assertTrue( Hibernate.isInitialized( ((CourseMeeting) resultList.get( 1 )).getCourse() ) );
 				assertEquals( courseExpected, ((CourseMeeting) resultList.get( 0 )).getCourse() );
 				assertEquals( courseExpected, ((CourseMeeting) resultList.get( 1 )).getCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithLazyAssnUnique() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.add( Restrictions.eq( "studentNumber", shermanExpected.getStudentNumber() ) );
 				}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s where s.studentNumber = :studentNumber" )
 						.setParameter( "studentNumber", shermanExpected.getStudentNumber() );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				assertTrue( results instanceof Student );
 				assertEquals( shermanExpected, results );
 				assertNotNull( ((Student) results).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ((Student) results).getEnrolments() ) );
 				assertNull( ((Student) results).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, true );
 	}
 
 	@Test
 	public void testEntityWithLazyAssnList() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class )
 						.addOrder( Order.asc( "studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student order by studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ((Student) resultList.get( 0 )).getEnrolments() );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() ) );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithUnaliasedJoinFetchedLazyOneToManySingleElementList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber" ) );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.enrolments order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				}
 			}
 		};
 
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false);
 	}
 
 	@Test
 	public void testJoinWithFetchJoinListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pc", Criteria.LEFT_JOIN  )
 						.setFetchMode( "enrolments", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				// The following fails for criteria due to HHH-3524
 				//assertEquals( yogiExpected.getPreferredCourse(), ( ( Student ) resultList.get( 0 ) ).getPreferredCourse() );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( null, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber"
 				);
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[1] );
 				assertNull( ((Student) shermanObjects[0]).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithOwnerAndPropProjectedList() throws Exception {
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s, s.name from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals( yogiExpected.getName(), yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[0] );
 				assertEquals( shermanExpected.getName(), shermanObjects[1] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithPropAndOwnerProjectedList() throws Exception {
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s.name, s from Student s left join fetch s.enrolments left join s.preferredCourse order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected.getName(), yogiObjects[ 0 ] );
 				assertEquals( yogiExpected, yogiObjects[ 1 ] );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( shermanExpected.getName(), shermanObjects[ 0 ] );
 				assertEquals( shermanExpected, shermanObjects[1] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student )  yogiObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithOwnerAndAliasedJoinedProjectedListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "select s, pc from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber" );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 0 ] );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Course ) yogiObjects[ 1 ] ).getCourseCode()
 				);
 				Object[] shermanObjects = ( Object[]  ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[ 0 ] );
 				assertNull( shermanObjects[1] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 1 ] );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 0 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 0 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testJoinWithFetchJoinWithAliasedJoinedAndOwnerProjectedListHql() throws Exception {
 		HqlExecutor hqlSelectNewMapExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select pc, s from Student s left join fetch s.enrolments left join s.preferredCourse pc order by s.studentNumber"
 				);
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				assertEquals( yogiExpected, yogiObjects[ 1 ] );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						((Course) yogiObjects[0]).getCourseCode()
 				);
 				Object[] shermanObjects = ( Object[]  ) resultList.get( 1 );
 				assertEquals( shermanExpected, shermanObjects[1] );
 				assertNull( shermanObjects[0] );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertEquals( yogiExpected.getPreferredCourse(), yogiObjects[ 0 ] );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) yogiObjects[ 1 ] ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) shermanObjects[ 1 ] ).getEnrolments() ) );
 				}
 			}
 		};
 		runTest( hqlSelectNewMapExecutor, null, checker, false );
 	}
 
 	@Test
 	public void testEntityWithAliasedJoinFetchedLazyOneToManySingleElementListHql() throws Exception {
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.enrolments e order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals(
 						yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode()
 				);
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNull( ( ( Student ) resultList.get( 1 ) ).getPreferredCourse() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 				}
 			}
 		};
 
 		runTest( hqlExecutor, null, checker, false);
 	}
 
 	@Test
 	public void testEntityWithSelectFetchedLazyOneToManySingleElementListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "enrolments", FetchMode.SELECT )
 						.addOrder( Order.asc( "s.studentNumber" ) );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ((Student) resultList.get( 0 )).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ((Student) resultList.get( 0 )).getEnrolments() ) );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				assertFalse( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 			}
 		};
 
 		runTest( null, criteriaExecutorUnaliased, checker, false);
 	}
 
 	@Test
 	public void testEntityWithJoinFetchedLazyOneToManyMultiAndNullElementList() throws Exception {
 		//unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.addresses order by s.studentNumber" );
 			}
 		};
 
 		//aliased
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "a", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "addresses", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.setFetchMode( "a", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.addresses a order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertSame( resultList.get( 0 ), resultList.get( 1 ) );
 				assertEquals( shermanExpected, resultList.get( 2 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 2 ) ).getAddresses() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getAddresses() ) );
 					assertEquals( yogiExpected.getAddresses(), ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 					assertTrue( ( ( Student ) resultList.get( 2 ) ).getAddresses().isEmpty() );
 				}
 			}
 		};
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false );
 		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 		runTest( null, criteriaExecutorAliased3, checker, false );
 		runTest( null, criteriaExecutorAliased4, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinFetchedLazyManyToOneList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorUnaliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.preferredCourse order by s.studentNumber" );
 			}
 		};
 
 		// aliased
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "pCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased3 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "preferredCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased4 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.preferredCourse", "pCourse", Criteria.LEFT_JOIN )
 						.setFetchMode( "pCourse", FetchMode.JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutorAliased = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery( "from Student s left join fetch s.preferredCourse pCourse order by s.studentNumber" );
 			}
 		};
 
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertEquals( yogiExpected.getPreferredCourse().getCourseCode(),
 						( ( Student ) resultList.get( 0 ) ).getPreferredCourse().getCourseCode() );
 				assertNull( ((Student) resultList.get( 1 )).getPreferredCourse() );
 			}
 		};
 		runTest( hqlExecutorUnaliased, criteriaExecutorUnaliased, checker, false );
 		runTest( hqlExecutorAliased, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 		runTest( null, criteriaExecutorAliased3, checker, false );
 		runTest( null, criteriaExecutorAliased4, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinFetchedLazyManyToOneUsingProjectionList() throws Exception {
 		// unaliased
 		CriteriaExecutor criteriaExecutor = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Enrolment.class, "e" )
 						.createAlias( "e.student", "s", Criteria.LEFT_JOIN )
 						.setFetchMode( "student", FetchMode.JOIN )
 						.setFetchMode( "student.preferredCourse", FetchMode.JOIN )
 						.setProjection(
 								Projections.projectionList()
 										.add( Projections.property( "s.name" ) )
 										.add( Projections.property( "e.student" ) )
 						)
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		HqlExecutor hqlExecutor = new HqlExecutor() {
 			public Query getQuery(Session s) {
 				return s.createQuery(
 						"select s.name, s from Enrolment e left join e.student s left join fetch s.preferredCourse order by s.studentNumber"
 				);
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				Object[] yogiObjects = ( Object[] ) resultList.get( 0 );
 				Object[] shermanObjects = ( Object[] ) resultList.get( 1 );
 				assertEquals( yogiExpected.getName(), yogiObjects[ 0 ] );
 				assertEquals( shermanExpected.getName(), shermanObjects[ 0 ] );
 				// The following fails for criteria due to HHH-1425
 				// assertEquals( yogiExpected, yogiObjects[ 1 ] );
 				// assertEquals( shermanExpected, shermanObjects[ 1 ] );
 				assertEquals( yogiExpected.getStudentNumber(), ( ( Student ) yogiObjects[ 1 ] ).getStudentNumber() );
 				assertEquals( shermanExpected.getStudentNumber(), ( ( Student ) shermanObjects[ 1 ] ).getStudentNumber() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					// The following fails for criteria due to HHH-1425
 					//assertTrue( Hibernate.isInitialized( ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() ) );
 					//assertEquals( yogiExpected.getPreferredCourse(),  ( ( Student ) yogiObjects[ 1 ] ).getPreferredCourse() );
 					//assertTrue( Hibernate.isInitialized( ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() ) );
 					//assertEquals( shermanExpected.getPreferredCourse(),  ( ( Student ) shermanObjects[ 1 ] ).getPreferredCourse() );
 				}
 			}
 		};
 		runTest( hqlExecutor, criteriaExecutor, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyOneToManySingleElementListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.enrolments", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.enrolments", "e", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.enrolments", "e", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 2, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertEquals( shermanExpected, resultList.get( 1 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getEnrolments() ) );
 					assertEquals( yogiExpected.getEnrolments(), ( ( Student ) resultList.get( 0 ) ).getEnrolments() );
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 1 ) ).getEnrolments() ) );
 					assertEquals( shermanExpected.getEnrolments(), ( ( Student ) resultList.get( 1 ) ).getEnrolments() );
 				}
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 
 	@Test
 	public void testEntityWithJoinedLazyOneToManyMultiAndNullListCriteria() throws Exception {
 		CriteriaExecutor criteriaExecutorUnaliased = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased1 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createCriteria( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		CriteriaExecutor criteriaExecutorAliased2 = new CriteriaExecutor() {
 			protected Criteria getCriteria(Session s) {
 				// should use RootEntityTransformer by default
 				return s.createCriteria( Student.class, "s" )
 						.createAlias( "s.addresses", "a", Criteria.LEFT_JOIN )
 						.addOrder( Order.asc( "s.studentNumber") );
 			}
 		};
 		ResultChecker checker = new ResultChecker() {
 			public void check(Object results) {
 				List resultList = ( List ) results;
 				assertEquals( 3, resultList.size() );
 				assertEquals( yogiExpected, resultList.get( 0 ) );
 				assertSame( resultList.get( 0 ), resultList.get( 1 ) );
 				assertEquals( shermanExpected, resultList.get( 2 ) );
 				assertNotNull( ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 2 ) ).getAddresses() );
 				assertNotNull( ( ( Student ) resultList.get( 1 ) ).getAddresses() );
 				if ( areDynamicNonLazyAssociationsChecked() ) {
 					assertTrue( Hibernate.isInitialized( ( ( Student ) resultList.get( 0 ) ).getAddresses() ) );
 					assertEquals( yogiExpected.getAddresses(), ( ( Student ) resultList.get( 0 ) ).getAddresses() );
 					assertTrue( ( ( Student ) resultList.get( 2 ) ).getAddresses().isEmpty() );
 				}
 			}
 		};
 		runTest( null, criteriaExecutorUnaliased, checker, false );
 		runTest( null, criteriaExecutorAliased1, checker, false );
 		runTest( null, criteriaExecutorAliased2, checker, false );
 	}
 
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
index 1221f054c1..e5db2854df 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/QueryImpl.java
@@ -1,638 +1,638 @@
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
 package org.hibernate.ejb;
 import static javax.persistence.TemporalType.DATE;
 import static javax.persistence.TemporalType.TIME;
 import static javax.persistence.TemporalType.TIMESTAMP;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.NoResultException;
 import javax.persistence.NonUniqueResultException;
 import javax.persistence.Parameter;
 import javax.persistence.PersistenceException;
 import javax.persistence.Query;
 import javax.persistence.TemporalType;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.TypedQuery;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.QueryParameterException;
 import org.hibernate.SQLQuery;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.ejb.internal.EntityManagerMessageLogger;
 import org.hibernate.ejb.util.LockModeTypeHelper;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.query.NamedParameterDescriptor;
 import org.hibernate.engine.query.OrdinalParameterDescriptor;
 import org.hibernate.hql.QueryExecutionRequestException;
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
 import org.jboss.logging.Logger;
 
 /**
  * Hibernate implementation of both the {@link Query} and {@link TypedQuery} contracts.
  *
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class QueryImpl<X> extends org.hibernate.ejb.AbstractQueryImpl<X> implements TypedQuery<X>, HibernateQuery {
 
     public static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class, QueryImpl.class.getName());
 
 	private org.hibernate.Query query;
 	private Set<Integer> jpaPositionalIndices;
 	private Set<Parameter<?>> parameters;
 
 	public QueryImpl(org.hibernate.Query query, AbstractEntityManagerImpl em) {
 		this( query, em, Collections.<String, Class>emptyMap() );
 	}
 
 	public QueryImpl(
 			org.hibernate.Query query,
 			AbstractEntityManagerImpl em,
 			Map<String,Class> namedParameterTypeRedefinitions) {
 		super( em );
 		this.query = query;
 		extractParameterInfo( namedParameterTypeRedefinitions );
 	}
 
 	@SuppressWarnings({ "unchecked", "RedundantCast" })
 	private void extractParameterInfo(Map<String,Class> namedParameterTypeRedefinition) {
 		if ( ! AbstractQueryImpl.class.isInstance( query ) ) {
 			throw new IllegalStateException( "Unknown query type for parameter extraction" );
 		}
 
 		HashSet<Parameter<?>> parameters = new HashSet<Parameter<?>>();
 		AbstractQueryImpl queryImpl = AbstractQueryImpl.class.cast( query );
 
 		// extract named params
 		for ( String name : (Set<String>) queryImpl.getParameterMetadata().getNamedParameterNames() ) {
 			final NamedParameterDescriptor descriptor =
 					queryImpl.getParameterMetadata().getNamedParameterDescriptor( name );
 			Class javaType = namedParameterTypeRedefinition.get( name );
 			if ( javaType != null && mightNeedRedefinition( javaType ) ) {
 				descriptor.resetExpectedType(
 						sfi().getTypeResolver().heuristicType( javaType.getName() )
 				);
 			}
 			else if ( descriptor.getExpectedType() != null ) {
 				javaType = descriptor.getExpectedType().getReturnedClass();
 			}
 			final ParameterImpl parameter = new ParameterImpl( name, javaType );
 			parameters.add( parameter );
 			if ( descriptor.isJpaStyle() ) {
 				if ( jpaPositionalIndices == null ) {
 					jpaPositionalIndices = new HashSet<Integer>();
 				}
 				jpaPositionalIndices.add( Integer.valueOf( name ) );
 			}
 		}
 
 		// extract positional parameters
 		for ( int i = 0, max = queryImpl.getParameterMetadata().getOrdinalParameterCount(); i < max; i++ ) {
 			final OrdinalParameterDescriptor descriptor =
 					queryImpl.getParameterMetadata().getOrdinalParameterDescriptor( i+1 );
 			ParameterImpl parameter = new ParameterImpl(
 					i + 1,
 					descriptor.getExpectedType() == null
 							? null
 							: descriptor.getExpectedType().getReturnedClass()
 			);
 			parameters.add( parameter );
 			Integer position = descriptor.getOrdinalPosition();
             if (jpaPositionalIndices != null && jpaPositionalIndices.contains(position)) LOG.parameterPositionOccurredAsBothJpaAndHibernatePositionalParameter(position);
 		}
 
 		this.parameters = java.util.Collections.unmodifiableSet( parameters );
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return (SessionFactoryImplementor) getEntityManager().getFactory().getSessionFactory();
 	}
 
 	private boolean mightNeedRedefinition(Class javaType) {
 		// for now, only really no for dates/times/timestamps
 		return java.util.Date.class.isAssignableFrom( javaType );
 	}
 
 	private static class ParameterImpl implements Parameter {
 		private final String name;
 		private final Integer position;
 		private final Class javaType;
 
 		private ParameterImpl(String name, Class javaType) {
 			this.name = name;
 			this.javaType = javaType;
 			this.position = null;
 		}
 
 		private ParameterImpl(Integer position, Class javaType) {
 			this.position = position;
 			this.javaType = javaType;
 			this.name = null;
 		}
 
 		public String getName() {
 			return name;
 		}
 
 		public Integer getPosition() {
 			return position;
 		}
 
 		public Class getParameterType() {
 			return javaType;
 		}
 	}
 
 	public org.hibernate.Query getHibernateQuery() {
 		return query;
 	}
 
 	@Override
     protected int internalExecuteUpdate() {
 		return query.executeUpdate();
 	}
 
 	@Override
     protected void applyMaxResults(int maxResults) {
 		query.setMaxResults( maxResults );
 	}
 
 	@Override
     protected void applyFirstResult(int firstResult) {
 		query.setFirstResult( firstResult );
 	}
 
 	@Override
     protected void applyTimeout(int timeout) {
 		query.setTimeout( timeout );
 	}
 
 	@Override
     protected void applyComment(String comment) {
 		query.setComment( comment );
 	}
 
 	@Override
     protected void applyFetchSize(int fetchSize) {
 		query.setFetchSize( fetchSize );
 	}
 
 	@Override
     protected void applyCacheable(boolean isCacheable) {
 		query.setCacheable( isCacheable );
 	}
 
 	@Override
     protected void applyCacheRegion(String regionName) {
 		query.setCacheRegion( regionName );
 	}
 
 	@Override
     protected void applyReadOnly(boolean isReadOnly) {
 		query.setReadOnly( isReadOnly );
 	}
 
 	@Override
     protected void applyCacheMode(CacheMode cacheMode) {
 		query.setCacheMode( cacheMode );
 	}
 
 	@Override
     protected void applyFlushMode(FlushMode flushMode) {
 		query.setFlushMode( flushMode );
 	}
 
 	@Override
     protected boolean canApplyLockModes() {
-		return org.hibernate.impl.QueryImpl.class.isInstance( query );
+		return org.hibernate.internal.QueryImpl.class.isInstance( query );
 	}
 
 	@Override
 	protected void applyAliasSpecificLockMode(String alias, LockMode lockMode) {
-		( (org.hibernate.impl.QueryImpl) query ).getLockOptions().setAliasSpecificLockMode( alias, lockMode );
+		( (org.hibernate.internal.QueryImpl) query ).getLockOptions().setAliasSpecificLockMode( alias, lockMode );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked", "RedundantCast" })
 	public List<X> getResultList() {
 		try {
 			return query.list();
 		}
 		catch (QueryExecutionRequestException he) {
 			throw new IllegalStateException(he);
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException(e);
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked", "RedundantCast" })
 	public X getSingleResult() {
 		try {
 			boolean mucked = false;
 			// IMPL NOTE : the mucking with max results here is attempting to help the user from shooting themselves
 			//		in the foot in the case where they have a large query by limiting the query results to 2 max
 			//    SQLQuery cannot be safely paginated, leaving the user's choice here.
 			if ( getSpecifiedMaxResults() != 1 &&
 					! ( SQLQuery.class.isAssignableFrom( query.getClass() ) ) ) {
 				mucked = true;
 				query.setMaxResults( 2 ); //avoid OOME if the list is huge
 			}
 			List<X> result = query.list();
 			if ( mucked ) {
 				query.setMaxResults( getSpecifiedMaxResults() );
 			}
 
 			if ( result.size() == 0 ) {
 				NoResultException nre = new NoResultException( "No entity found for query" );
 				getEntityManager().handlePersistenceException( nre );
 				throw nre;
 			}
 			else if ( result.size() > 1 ) {
 				Set<X> uniqueResult = new HashSet<X>(result);
 				if ( uniqueResult.size() > 1 ) {
 					NonUniqueResultException nure = new NonUniqueResultException( "result returns more than one elements" );
 					getEntityManager().handlePersistenceException( nure );
 					throw nure;
 				}
 				else {
 					return uniqueResult.iterator().next();
 				}
 
 			}
 			else {
 				return result.get( 0 );
 			}
 		}
 		catch (QueryExecutionRequestException he) {
 			throw new IllegalStateException(he);
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException(e);
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
 		if ( ! parameters.contains( param ) ) {
 			throw new IllegalArgumentException( "Specified parameter was not found in query" );
 		}
 		if ( param.getName() != null ) {
 			// a named param, for not delegate out.  Eventually delegate *into* this method...
 			setParameter( param.getName(), value );
 		}
 		else {
 			setParameter( param.getPosition(), value );
 		}
 		return this;
 	}
 
 	public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
 		if ( ! parameters.contains( param ) ) {
 			throw new IllegalArgumentException( "Specified parameter was not found in query" );
 		}
 		if ( param.getName() != null ) {
 			// a named param, for not delegate out.  Eventually delegate *into* this method...
 			setParameter( param.getName(), value, temporalType );
 		}
 		else {
 			setParameter( param.getPosition(), value, temporalType );
 		}
 		return this;
 	}
 
 	public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
 		if ( ! parameters.contains( param ) ) {
 			throw new IllegalArgumentException( "Specified parameter was not found in query" );
 		}
 		if ( param.getName() != null ) {
 			// a named param, for not delegate out.  Eventually delegate *into* this method...
 			setParameter( param.getName(), value, temporalType );
 		}
 		else {
 			setParameter( param.getPosition(), value, temporalType );
 		}
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setParameter(String name, Object value) {
 		try {
 			if ( value instanceof Collection ) {
 				query.setParameterList( name, (Collection) value );
 			}
 			else {
 				query.setParameter( name, value );
 			}
 			registerParameterBinding( getParameter( name ), value );
 			return this;
 		}
 		catch (QueryParameterException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
 		try {
 			if ( temporalType == DATE ) {
 				query.setDate( name, value );
 			}
 			else if ( temporalType == TIME ) {
 				query.setTime( name, value );
 			}
 			else if ( temporalType == TIMESTAMP ) {
 				query.setTimestamp( name, value );
 			}
 			registerParameterBinding( getParameter( name ), value );
 			return this;
 		}
 		catch (QueryParameterException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
 		try {
 			if ( temporalType == DATE ) {
 				query.setCalendarDate( name, value );
 			}
 			else if ( temporalType == TIME ) {
 				throw new IllegalArgumentException( "not yet implemented" );
 			}
 			else if ( temporalType == TIMESTAMP ) {
 				query.setCalendar( name, value );
 			}
 			registerParameterBinding( getParameter(name), value );
 			return this;
 		}
 		catch (QueryParameterException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setParameter(int position, Object value) {
 		try {
 			if ( isJpaPositionalParameter( position ) ) {
 				this.setParameter( Integer.toString( position ), value );
 			}
 			else {
 				query.setParameter( position - 1, value );
 				registerParameterBinding( getParameter( position ), value );
 			}
 			return this;
 		}
 		catch (QueryParameterException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	private boolean isJpaPositionalParameter(int position) {
 		return jpaPositionalIndices != null && jpaPositionalIndices.contains( position );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
 		try {
 			if ( isJpaPositionalParameter( position ) ) {
 				String name = Integer.toString( position );
 				this.setParameter( name, value, temporalType );
 			}
 			else {
 				if ( temporalType == DATE ) {
 					query.setDate( position - 1, value );
 				}
 				else if ( temporalType == TIME ) {
 					query.setTime( position - 1, value );
 				}
 				else if ( temporalType == TIMESTAMP ) {
 					query.setTimestamp( position - 1, value );
 				}
 				registerParameterBinding( getParameter( position ), value );
 			}
 			return this;
 		}
 		catch (QueryParameterException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
 		try {
 			if ( isJpaPositionalParameter( position ) ) {
 				String name = Integer.toString( position );
 				this.setParameter( name, value, temporalType );
 			}
 			else {
 				if ( temporalType == DATE ) {
 					query.setCalendarDate( position - 1, value );
 				}
 				else if ( temporalType == TIME ) {
 					throw new IllegalArgumentException( "not yet implemented" );
 				}
 				else if ( temporalType == TIMESTAMP ) {
 					query.setCalendar( position - 1, value );
 				}
 				registerParameterBinding( getParameter( position ), value );
 			}
 			return this;
 		}
 		catch (QueryParameterException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getEntityManager().convert( he );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Set<Parameter<?>> getParameters() {
 		return parameters;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Parameter<?> getParameter(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "Name of parameter to locate cannot be null" );
 		}
 		for ( Parameter parameter : parameters ) {
 			if ( name.equals( parameter.getName() ) ) {
 				return parameter;
 			}
 		}
 		throw new IllegalArgumentException( "Unable to locate parameter named [" + name + "]" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Parameter<?> getParameter(int position) {
 		if ( isJpaPositionalParameter( position ) ) {
 			return getParameter( Integer.toString( position ) );
 		}
 		else {
 			for ( Parameter parameter : parameters ) {
 				if ( parameter.getPosition() != null && position == parameter.getPosition() ) {
 					return parameter;
 				}
 			}
 			throw new IllegalArgumentException( "Unable to locate parameter with position [" + position + "]" );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <T> Parameter<T> getParameter(String name, Class<T> type) {
 		Parameter param = getParameter( name );
 		if ( param.getParameterType() != null ) {
 			// we were able to determine the expected type during analysis, so validate it here
 			throw new IllegalArgumentException(
 					"Parameter type [" + param.getParameterType().getName() +
 							"] is not assignment compatible with requested type [" +
 							type.getName() + "]"
 			);
 		}
 		return param;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <T> Parameter<T> getParameter(int position, Class<T> type) {
 		Parameter param = getParameter( position );
 		if ( param.getParameterType() != null ) {
 			// we were able to determine the expected type during analysis, so validate it here
 			throw new IllegalArgumentException(
 					"Parameter type [" + param.getParameterType().getName() +
 							"] is not assignment compatible with requested type [" +
 							type.getName() + "]"
 			);
 		}
 		return param;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public <T> T unwrap(Class<T> tClass) {
 		if ( org.hibernate.Query.class.isAssignableFrom( tClass ) ) {
 			return (T) query;
 		}
 		else {
 			try {
 				return (T) this;
 			}
 			catch ( ClassCastException cce ) {
 				PersistenceException pe = new PersistenceException(
 						"Unsupported unwrap target type [" + tClass.getName() + "]"
 				);
 				//It's probably against the spec to not mark the tx for rollback but it will be easier for people
 				//getEntityManager().handlePersistenceException( pe );
 				throw pe;
 			}
 		}
 	}
 
 	private javax.persistence.LockModeType jpaLockMode = javax.persistence.LockModeType.NONE;
 
 	@Override
     @SuppressWarnings({ "unchecked" })
 	public TypedQuery<X> setLockMode(javax.persistence.LockModeType lockModeType) {
 		if (! getEntityManager().isTransactionInProgress()) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 		if ( ! canApplyLockModes() ) {
 			throw new IllegalStateException( "Not a JPAQL/Criteria query" );
 		}
 		this.jpaLockMode = lockModeType;
-		( (org.hibernate.impl.QueryImpl) query ).getLockOptions().setLockMode(
+		( (org.hibernate.internal.QueryImpl) query ).getLockOptions().setLockMode(
 				LockModeTypeHelper.getLockMode( lockModeType )
 		);
 		return this;
 	}
 
 	@Override
     public javax.persistence.LockModeType getLockMode() {
 		return jpaLockMode;
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/criteria/basic/ExpressionsTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/criteria/basic/ExpressionsTest.java
index 39ef1283ae..925d90cbda 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/criteria/basic/ExpressionsTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/criteria/basic/ExpressionsTest.java
@@ -1,331 +1,331 @@
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
 package org.hibernate.ejb.criteria.basic;
 
 import javax.persistence.EntityManager;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.ParameterExpression;
 import javax.persistence.criteria.Predicate;
 import javax.persistence.criteria.Root;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.Query;
 import org.hibernate.ejb.metamodel.AbstractMetamodelSpecificTest;
 import org.hibernate.ejb.metamodel.Phone;
 import org.hibernate.ejb.metamodel.Product;
 import org.hibernate.ejb.metamodel.Product_;
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * Tests that various expressions operate as expected
  *
  * @author Steve Ebersole
  */
 public class ExpressionsTest extends AbstractMetamodelSpecificTest {
 	private CriteriaBuilder builder;
 
 	@Before
 	public void prepareTestData() {
 		builder = entityManagerFactory().getCriteriaBuilder();
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Product product = new Product();
 		product.setId( "product1" );
 		product.setPrice( 1.23d );
 		product.setQuantity( 2 );
 		product.setPartNumber( ((long)Integer.MAX_VALUE) + 1 );
 		product.setRating( 1.999f );
 		product.setSomeBigInteger( BigInteger.valueOf( 987654321 ) );
 		product.setSomeBigDecimal( BigDecimal.valueOf( 987654.321 ) );
 		em.persist( product );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@After
 	public void cleanupTestData() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.find( Product.class, "product1" ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testEmptyConjunction() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		criteria.from( Product.class );
 		criteria.where( builder.and() );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 1, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testEmptyConjunctionIsTrue() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		criteria.from( Product.class );
 		criteria.where( builder.isTrue( builder.and() ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 1, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testEmptyConjunctionIsFalse() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		criteria.from( Product.class );
 		criteria.where( builder.isFalse( builder.and() ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 0, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testEmptyDisjunction() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		criteria.from( Product.class );
 		criteria.where( builder.disjunction() );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 0, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testEmptyDisjunctionIsTrue() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		criteria.from( Product.class );
 		criteria.where( builder.isTrue( builder.disjunction() ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 0, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testEmptyDisjunctionIsFalse() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		criteria.from( Product.class );
 		criteria.where( builder.isFalse( builder.disjunction() ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 1, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testDiff() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Integer> criteria = builder.createQuery( Integer.class );
 		criteria.from( Product.class );
 		criteria.select( builder.diff( builder.literal( 5 ), builder.literal( 2 ) ) );
 		Integer result = em.createQuery( criteria ).getSingleResult();
 		assertEquals( Integer.valueOf( 3 ), result );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testDiffWithQuotient() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Number> criteria = builder.createQuery( Number.class );
 		criteria.from( Product.class );
 		criteria.select(
 				builder.quot(
 						builder.diff(
 								builder.literal( BigDecimal.valueOf( 2.0 ) ),
 								builder.literal( BigDecimal.valueOf( 1.0 ) )
 						),
 						BigDecimal.valueOf( 2.0 )
 				)
 		);
 		Number result = em.createQuery( criteria ).getSingleResult();
 		assertEquals(0.5d, result.doubleValue(), 0.1d);
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testSumWithQuotient() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Number> criteria = builder.createQuery( Number.class );
 		criteria.from( Product.class );
 		criteria.select(
 				builder.quot(
 						builder.sum(
 								builder.literal( BigDecimal.valueOf( 0.0 ) ),
 								builder.literal( BigDecimal.valueOf( 1.0 ) )
 						),
 						BigDecimal.valueOf( 2.0 )
 				)
 		);
 		Number result = em.createQuery( criteria ).getSingleResult();
 		assertEquals(0.5d, result.doubleValue(), 0.1d);
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testQuotientAndMultiply() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Number> criteria = builder.createQuery( Number.class );
 		criteria.from( Product.class );
 		criteria.select(
 				builder.quot(
 						builder.prod(
 								builder.literal( BigDecimal.valueOf( 10.0 ) ),
 								builder.literal( BigDecimal.valueOf( 5.0 ) )
 						),
 						BigDecimal.valueOf( 2.0 )
 				)
 		);
 		Number result = em.createQuery( criteria ).getSingleResult();
 		assertEquals(25.0d, result.doubleValue(), 0.1d);
 
 		criteria.select(
 				builder.prod(
 						builder.quot(
 								builder.literal( BigDecimal.valueOf( 10.0 ) ),
 								builder.literal( BigDecimal.valueOf( 5.0 ) )
 						),
 						BigDecimal.valueOf( 2.0 )
 				)
 		);
 		result = em.createQuery( criteria ).getSingleResult();
 		assertEquals(4.0d, result.doubleValue(), 0.1d);
 
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testParameterReuse() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = em.getCriteriaBuilder().createQuery( Product.class );
 		Root<Product> from = criteria.from( Product.class );
 		ParameterExpression<String> param = em.getCriteriaBuilder().parameter( String.class );
 		Predicate predicate = em.getCriteriaBuilder().equal( from.get( Product_.id ), param );
 		Predicate predicate2 = em.getCriteriaBuilder().equal( from.get( Product_.name ), param );
 		criteria.where( em.getCriteriaBuilder().or( predicate, predicate2 ) );
 		assertEquals( 1, criteria.getParameters().size() );
 		TypedQuery<Product> query = em.createQuery( criteria );
 		int hqlParamCount = countGeneratedParameters( query.unwrap( Query.class ) );
 		assertEquals( 1, hqlParamCount );
 		query.setParameter( param, "abc" ).getResultList();
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	private int countGeneratedParameters(Query query) {
 		AbstractQueryImpl hqlQueryImpl = (AbstractQueryImpl) query;
 		return hqlQueryImpl.getParameterMetadata().getNamedParameterNames().size();
 	}
 
 	@Test
 	public void testInExplicitTupleList() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		Root<Product> from = criteria.from( Product.class );
 		criteria.where( from.get( Product_.partNumber ).in( Collections.singletonList( ((long)Integer.MAX_VALUE) + 1 ) ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 1, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testInExplicitTupleListVarargs() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		Root<Product> from = criteria.from( Product.class );
 		criteria.where( from.get( Product_.partNumber ).in( ((long)Integer.MAX_VALUE) + 1 ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 1, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testInExpressionVarargs() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Product> criteria = builder.createQuery( Product.class );
 		Root<Product> from = criteria.from( Product.class );
 		criteria.where( from.get( Product_.partNumber ).in( from.get( Product_.partNumber ) ) );
 		List<Product> result = em.createQuery( criteria ).getResultList();
 		assertEquals( 1, result.size() );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testJoinedElementCollectionValuesInTupleList() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		CriteriaQuery<Phone> criteria = builder.createQuery( Phone.class );
 		Root<Phone> from = criteria.from( Phone.class );
 		criteria.where(
 				from.join( "types" )
 						.in( Collections.singletonList( Phone.Type.WORK ) )
 		);
 		em.createQuery( criteria ).getResultList();
 		em.getTransaction().commit();
 		em.close();
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/cacheable/cachemodes/SharedCacheModesTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/cacheable/cachemodes/SharedCacheModesTest.java
index a16a9a3fbb..fa25aee6d5 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/cacheable/cachemodes/SharedCacheModesTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/cacheable/cachemodes/SharedCacheModesTest.java
@@ -1,122 +1,122 @@
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
 package org.hibernate.ejb.test.cacheable.cachemodes;
 
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.EntityManager;
 import javax.persistence.Query;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Session;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.HibernateEntityManager;
 import org.hibernate.ejb.HibernateQuery;
 import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;
-import org.hibernate.impl.AbstractQueryImpl;
+import org.hibernate.internal.AbstractQueryImpl;
 
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Steve Ebersole
  */
 public class SharedCacheModesTest extends BaseEntityManagerFunctionalTestCase {
 	@Override
 	public Class[] getAnnotatedClasses() {
 		return new Class[] { SimpleEntity.class };
 	}
 
 	@Test
 	public void testEntityManagerCacheModes() {
 
 		EntityManager em;
 		Session session;
 
 		em = getOrCreateEntityManager();
 		session = ( (HibernateEntityManager) em ).getSession();
 
 		// defaults...
 		assertEquals( CacheStoreMode.USE, em.getProperties().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheRetrieveMode.USE, em.getProperties().get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) );
 		assertEquals( CacheMode.NORMAL, session.getCacheMode() );
 
 		// overrides...
 		em.setProperty( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH );
 		assertEquals( CacheStoreMode.REFRESH, em.getProperties().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.REFRESH, session.getCacheMode() );
 
 		em.setProperty( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.BYPASS );
 		assertEquals( CacheStoreMode.BYPASS, em.getProperties().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.GET, session.getCacheMode() );
 
 		em.setProperty( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS );
 		assertEquals( CacheRetrieveMode.BYPASS, em.getProperties().get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) );
 		assertEquals( CacheMode.IGNORE, session.getCacheMode() );
 
 		em.setProperty( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.USE );
 		assertEquals( CacheStoreMode.USE, em.getProperties().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.PUT, session.getCacheMode() );
 
 		em.setProperty( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH );
 		assertEquals( CacheStoreMode.REFRESH, em.getProperties().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.REFRESH, session.getCacheMode() );
 	}
 
 	@Test
 	public void testQueryCacheModes() {
 		EntityManager em = getOrCreateEntityManager();
 		Query jpaQuery = em.createQuery( "from SimpleEntity" );
 		AbstractQueryImpl hibQuery = (AbstractQueryImpl) ( (HibernateQuery) jpaQuery ).getHibernateQuery();
 
 		jpaQuery.setHint( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.USE );
 		assertEquals( CacheStoreMode.USE, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.NORMAL, hibQuery.getCacheMode() );
 
 		jpaQuery.setHint( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.BYPASS );
 		assertEquals( CacheStoreMode.BYPASS, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.GET, hibQuery.getCacheMode() );
 
 		jpaQuery.setHint( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.REFRESH );
 		assertEquals( CacheStoreMode.REFRESH, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.REFRESH, hibQuery.getCacheMode() );
 
 		jpaQuery.setHint( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheRetrieveMode.BYPASS );
 		assertEquals( CacheRetrieveMode.BYPASS, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) );
 		assertEquals( CacheStoreMode.REFRESH, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.REFRESH, hibQuery.getCacheMode() );
 
 		jpaQuery.setHint( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.BYPASS );
 		assertEquals( CacheRetrieveMode.BYPASS, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) );
 		assertEquals( CacheStoreMode.BYPASS, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.IGNORE, hibQuery.getCacheMode() );
 
 		jpaQuery.setHint( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheStoreMode.USE );
 		assertEquals( CacheRetrieveMode.BYPASS, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) );
 		assertEquals( CacheStoreMode.USE, jpaQuery.getHints().get( AvailableSettings.SHARED_CACHE_STORE_MODE ) );
 		assertEquals( CacheMode.PUT, hibQuery.getCacheMode() );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
index 31ee8063e7..6ea48f9185 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/lock/QueryLockingTest.java
@@ -1,248 +1,248 @@
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
 package org.hibernate.ejb.test.lock;
 
 import javax.persistence.EntityManager;
 import javax.persistence.LockModeType;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.QueryImpl;
 import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;
-import org.hibernate.impl.SessionImpl;
+import org.hibernate.internal.SessionImpl;
 
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class QueryLockingTest extends BaseEntityManagerFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Lockable.class };
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	protected void addConfigOptions(Map options) {
 		options.put( org.hibernate.cfg.AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 	}
 
 	@Test
 	public void testOverallLockMode() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		QueryImpl jpaQuery = em.createQuery( "from Lockable l" ).unwrap( QueryImpl.class );
 
-		org.hibernate.impl.QueryImpl hqlQuery = (org.hibernate.impl.QueryImpl) jpaQuery.getHibernateQuery();
+		org.hibernate.internal.QueryImpl hqlQuery = (org.hibernate.internal.QueryImpl) jpaQuery.getHibernateQuery();
 		assertEquals( LockMode.NONE, hqlQuery.getLockOptions().getLockMode() );
 		assertNull( hqlQuery.getLockOptions().getAliasSpecificLockMode( "l" ) );
 		assertEquals( LockMode.NONE, hqlQuery.getLockOptions().getEffectiveLockMode( "l" ) );
 
 		// NOTE : LockModeType.READ should map to LockMode.OPTIMISTIC
 		jpaQuery.setLockMode( LockModeType.READ );
 		assertEquals( LockMode.OPTIMISTIC, hqlQuery.getLockOptions().getLockMode() );
 		assertNull( hqlQuery.getLockOptions().getAliasSpecificLockMode( "l" ) );
 		assertEquals( LockMode.OPTIMISTIC, hqlQuery.getLockOptions().getEffectiveLockMode( "l" ) );
 
 		jpaQuery.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.PESSIMISTIC_WRITE );
 		assertEquals( LockMode.OPTIMISTIC, hqlQuery.getLockOptions().getLockMode() );
 		assertEquals( LockMode.PESSIMISTIC_WRITE, hqlQuery.getLockOptions().getAliasSpecificLockMode( "l" ) );
 		assertEquals( LockMode.PESSIMISTIC_WRITE, hqlQuery.getLockOptions().getEffectiveLockMode( "l" ) );
 
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testPessimisticForcedIncrementOverall() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable", Lockable.class ).setLockMode( LockModeType.PESSIMISTIC_FORCE_INCREMENT ).getSingleResult();
 		assertFalse( reread.getVersion().equals( initial ) );
 		em.getTransaction().commit();
 		em.close();
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testPessimisticForcedIncrementSpecific() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable l", Lockable.class )
 				.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.PESSIMISTIC_FORCE_INCREMENT )
 				.getSingleResult();
 		assertFalse( reread.getVersion().equals( initial ) );
 		em.getTransaction().commit();
 		em.close();
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticForcedIncrementOverall() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable", Lockable.class ).setLockMode( LockModeType.OPTIMISTIC_FORCE_INCREMENT ).getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		em.getTransaction().commit();
 		em.close();
 		assertFalse( reread.getVersion().equals( initial ) );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticForcedIncrementSpecific() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable l", Lockable.class )
 				.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.OPTIMISTIC_FORCE_INCREMENT )
 				.getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		em.getTransaction().commit();
 		em.close();
 		assertFalse( reread.getVersion().equals( initial ) );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticOverall() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable", Lockable.class )
 				.setLockMode( LockModeType.OPTIMISTIC )
 				.getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		assertTrue( em.unwrap( SessionImpl.class ).getActionQueue().hasBeforeTransactionActions() );
 		em.getTransaction().commit();
 		em.close();
 		assertEquals( initial, reread.getVersion() );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testOptimisticSpecific() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable lock = new Lockable( "name" );
 		em.persist( lock );
 		em.getTransaction().commit();
 		em.close();
 		Integer initial = lock.getVersion();
 		assertNotNull( initial );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Lockable reread = em.createQuery( "from Lockable l", Lockable.class )
 				.setHint( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE+".l", LockModeType.OPTIMISTIC )
 				.getSingleResult();
 		assertEquals( initial, reread.getVersion() );
 		assertTrue( em.unwrap( SessionImpl.class ).getActionQueue().hasBeforeTransactionActions() );
 		em.getTransaction().commit();
 		em.close();
 		assertEquals( initial, reread.getVersion() );
 
 		em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.remove( em.getReference( Lockable.class, reread.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
index 68e4ecf7f3..2febfbf341 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
@@ -1,311 +1,310 @@
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
 package org.hibernate.envers.entities.mapper.relation.lazy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
-import org.hibernate.event.EventListeners;
-import org.hibernate.impl.CriteriaImpl;
+import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractDelegateSessionImplementor implements SessionImplementor {
     protected SessionImplementor delegate;
 
     public AbstractDelegateSessionImplementor(SessionImplementor delegate) {
         this.delegate = delegate;
     }
 
     public abstract Object doImmediateLoad(String entityName);
 
     public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
         return doImmediateLoad(entityName);
     }
 
     // Delegate methods
 
 
 	@Override
 	public String getTenantIdentifier() {
 		return delegate.getTenantIdentifier();
 	}
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		return delegate.getJdbcConnectionAccess();
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return delegate.generateEntityKey( id, persister );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return delegate.generateCacheKey( id, type, entityOrRoleName );
 	}
 
 	@Override
 	public <T> T execute(Callback<T> callback) {
 		return delegate.execute( callback );
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return delegate.getLoadQueryInfluencers();
 	}
 
 	public Interceptor getInterceptor() {
         return delegate.getInterceptor();
     }
 
     public void setAutoClear(boolean enabled) {
         delegate.setAutoClear(enabled);
     }
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		delegate.disableTransactionAutoJoin();
 	}
 
 	public boolean isTransactionInProgress() {
         return delegate.isTransactionInProgress();
     }
 
     public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
         delegate.initializeCollection(collection, writing);
     }
 
     public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
         return delegate.internalLoad(entityName, id, eager, nullable);
     }
 
     public long getTimestamp() {
         return delegate.getTimestamp();
     }
 
     public SessionFactoryImplementor getFactory() {
         return delegate.getFactory();
     }
 
     public List list(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.list(query, queryParameters);
     }
 
     public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.iterate(query, queryParameters);
     }
 
     public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.scroll(query, queryParameters);
     }
 
     public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
         return delegate.scroll(criteria, scrollMode);
     }
 
     public List list(CriteriaImpl criteria) {
         return delegate.list(criteria);
     }
 
     public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
         return delegate.listFilter(collection, filter, queryParameters);
     }
 
     public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
         return delegate.iterateFilter(collection, filter, queryParameters);
     }
 
     public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
         return delegate.getEntityPersister(entityName, object);
     }
 
     public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
         return delegate.getEntityUsingInterceptor(key);
     }
 
     public Serializable getContextEntityIdentifier(Object object) {
         return delegate.getContextEntityIdentifier(object);
     }
 
     public String bestGuessEntityName(Object object) {
         return delegate.bestGuessEntityName(object);
     }
 
     public String guessEntityName(Object entity) throws HibernateException {
         return delegate.guessEntityName(entity);
     }
 
     public Object instantiate(String entityName, Serializable id) throws HibernateException {
         return delegate.instantiate(entityName, id);
     }
 
     public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
         return delegate.listCustomQuery(customQuery, queryParameters);
     }
 
     public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
         return delegate.scrollCustomQuery(customQuery, queryParameters);
     }
 
     public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
         return delegate.list(spec, queryParameters);
     }
 
     public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
         return delegate.scroll(spec, queryParameters);
     }
 
     public Object getFilterParameterValue(String filterParameterName) {
         return delegate.getFilterParameterValue(filterParameterName);
     }
 
     public Type getFilterParameterType(String filterParameterName) {
         return delegate.getFilterParameterType(filterParameterName);
     }
 
     public Map getEnabledFilters() {
         return delegate.getEnabledFilters();
     }
 
     public int getDontFlushFromFind() {
         return delegate.getDontFlushFromFind();
     }
 
     public PersistenceContext getPersistenceContext() {
         return delegate.getPersistenceContext();
     }
 
     public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
         return delegate.executeUpdate(query, queryParameters);
     }
 
     public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
         return delegate.executeNativeUpdate(specification, queryParameters);
     }
 
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		return delegate.getNonFlushedChanges();
 	}
 
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		delegate.applyNonFlushedChanges( nonFlushedChanges );
 	}
 
     public EntityMode getEntityMode() {
         return delegate.getEntityMode();
     }
 
     public CacheMode getCacheMode() {
         return delegate.getCacheMode();
     }
 
     public void setCacheMode(CacheMode cm) {
         delegate.setCacheMode(cm);
     }
 
     public boolean isOpen() {
         return delegate.isOpen();
     }
 
     public boolean isConnected() {
         return delegate.isConnected();
     }
 
     public FlushMode getFlushMode() {
         return delegate.getFlushMode();
     }
 
     public void setFlushMode(FlushMode fm) {
         delegate.setFlushMode(fm);
     }
 
     public Connection connection() {
         return delegate.connection();
     }
 
     public void flush() {
         delegate.flush();
     }
 
     public Query getNamedQuery(String name) {
         return delegate.getNamedQuery(name);
     }
 
     public Query getNamedSQLQuery(String name) {
         return delegate.getNamedSQLQuery(name);
     }
 
     public boolean isEventSource() {
         return delegate.isEventSource();
     }
 
     public void afterScrollOperation() {
         delegate.afterScrollOperation();
     }
 
     public void setFetchProfile(String name) {
         delegate.setFetchProfile(name);
     }
 
     public String getFetchProfile() {
         return delegate.getFetchProfile();
     }
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return delegate.getTransactionCoordinator();
 	}
 
 	public boolean isClosed() {
         return delegate.isClosed();
     }
 }
