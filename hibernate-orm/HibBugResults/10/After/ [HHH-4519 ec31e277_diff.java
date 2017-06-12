diff --git a/cache-infinispan/pom.xml b/cache-infinispan/pom.xml
index b0c633bb67..1ed78f52e7 100644
--- a/cache-infinispan/pom.xml
+++ b/cache-infinispan/pom.xml
@@ -1,165 +1,165 @@
 <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
 
     <modelVersion>4.0.0</modelVersion>
 
     <parent>
         <groupId>org.hibernate</groupId>
         <artifactId>hibernate-parent</artifactId>
         <version>3.5.0-SNAPSHOT</version>
         <relativePath>../parent/pom.xml</relativePath>
     </parent>
     
     <groupId>org.hibernate</groupId>
     <artifactId>hibernate-infinispan</artifactId>
     <packaging>jar</packaging>
 
     <name>Hibernate Infinispan Integration</name>
     <description>Integration of Hibernate with Infinispan</description>
 
     <properties>
-      <version.infinispan>4.0.0.BETA2</version.infinispan>
+      <version.infinispan>4.0.0-SNAPSHOT</version.infinispan>
       <version.hsqldb>1.8.0.2</version.hsqldb>
       <version.cglib>2.2</version.cglib>
       <version.javassist>3.4.GA</version.javassist>
       <skipUnitTests>true</skipUnitTests>
       <!-- 
          Following is the default jgroups mcast address.  If you find the testsuite runs very slowly, there
          may be problems with multicast on the interface JGroups uses by default on your machine. You can
          try to resolve setting 'jgroups.bind_addr' as a system-property to the jvm launching maven and
          setting the value to an interface where you know multicast works
       -->
       <jgroups.bind_addr>127.0.0.1</jgroups.bind_addr>
     </properties>
 
     <dependencies>
         <dependency>
             <groupId>${groupId}</groupId>
             <artifactId>hibernate-core</artifactId>
             <version>${version}</version>
         </dependency>
         <dependency>
             <groupId>org.infinispan</groupId>
             <artifactId>infinispan-core</artifactId>
             <version>${version.infinispan}</version> 
         </dependency>
         
         <!-- test dependencies -->
         <dependency>
             <groupId>${groupId}</groupId>
             <artifactId>hibernate-testing</artifactId>
             <version>${version}</version>
             <!-- <scope>test</scope> TODO fix this -->
         </dependency>
         <dependency>
             <groupId>org.infinispan</groupId>
             <artifactId>infinispan-core</artifactId>
             <version>${version.infinispan}</version>
             <type>test-jar</type>
             <scope>test</scope>
         </dependency>
         <dependency>
             <groupId>hsqldb</groupId>
             <artifactId>hsqldb</artifactId>
             <version>${version.hsqldb}</version>
             <scope>test</scope>
         </dependency>
         <!-- this is optional on core :( and needed for testing -->
         <dependency>
             <groupId>cglib</groupId>
             <artifactId>cglib</artifactId>
             <version>${version.cglib}</version>
             <scope>test</scope>
         </dependency>
         <dependency>
             <groupId>javassist</groupId>
             <artifactId>javassist</artifactId>
             <version>${version.javassist}</version>
             <scope>test</scope>
         </dependency>
     </dependencies>
 
     <build>
         <plugins>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-compiler-plugin</artifactId>
                 <configuration>
                     <source>1.6</source>
                     <target>1.6</target>
                     <compilerVersion>1.6</compilerVersion>
                     <executable>${jdk16_home}/bin/javac</executable>
                     <fork>true</fork>
                     <verbose>true</verbose>
                 </configuration>
             </plugin>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-surefire-plugin</artifactId>
                 <configuration>
                     <jvm>${jdk16_home}/bin/java</jvm>
                     <excludes>
                         <!-- Skip a long-running test of a prototype class -->
                         <exclude>**/ClusteredConcurrentTimestampRegionTestCase.java</exclude>
                     </excludes>
                     <systemProperties>
                         <property>
                             <name>hibernate.test.validatefailureexpected</name>
                             <value>true</value>
                         </property>
                         <property>
                             <name>jgroups.bind_addr</name>
                             <value>${jgroups.bind_addr}</value>
                         </property>
                         <!-- There are problems with multicast and IPv6 on some
                              OS/JDK combos, so we tell Java to use IPv4. If you
                              have problems with multicast when running the tests
                              you can try setting this to 'false', although typically
                              that won't be helpful.
                         -->
                         <property>
                             <name>java.net.preferIPv4Stack</name>
                             <value>true</value>
                         </property>
                         <!-- Tell JGroups to only wait a short time for PING 
                              responses before determining coordinator. Speeds cluster
                              formation during integration tests. (This is too
                              low a value for a real system; only use for tests.)
                         -->
                         <property>
                             <name>jgroups.ping.timeout</name>
                             <value>500</value>
                         </property>
                         <!-- Tell JGroups to only require one PING response
                              before determining coordinator. Speeds cluster
                              formation during integration tests. (This is too
                              low a value for a real system; only use for tests.)
                         -->
                         <property>
                             <name>jgroups.ping.num_initial_members</name>
                             <value>1</value>
                         </property>
                         <!-- Disable the JGroups message bundling feature
                              to speed tests and avoid FLUSH issue -->
                         <property>
                             <name>jgroups.udp.enable_bundling</name>
                             <value>false</value>
                         </property>
                     </systemProperties>
                     <skipExec>${skipUnitTests}</skipExec>
                 </configuration>
             </plugin>
         </plugins>
     </build>
 
     <profiles>
         <profile>
             <id>test</id>
             <activation>
                 <activeByDefault>false</activeByDefault>
             </activation>
             <properties>
                 <skipUnitTests>false</skipUnitTests>
             </properties>
         </profile>
      </profiles>
 </project>
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
index 8153196ed6..0d84aa8e80 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
@@ -1,378 +1,392 @@
 package org.hibernate.cache.infinispan;
 
 import java.io.IOException;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.TimestampsRegion;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.query.QueryResultsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.TimestampTypeOverrides;
 import org.hibernate.cache.infinispan.tm.HibernateTransactionManagerLookup;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Settings;
 import org.hibernate.util.PropertiesHelper;
 import org.infinispan.Cache;
 import org.infinispan.config.Configuration;
 import org.infinispan.manager.CacheManager;
 import org.infinispan.manager.DefaultCacheManager;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * A {@link RegionFactory} for <a href="http://www.jboss.org/infinispan">Infinispan</a>-backed cache
  * regions.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class InfinispanRegionFactory implements RegionFactory {
    
    private static final Logger log = LoggerFactory.getLogger(InfinispanRegionFactory.class);
    
    private static final String PREFIX = "hibernate.cache.infinispan.";
    
    private static final String CONFIG_SUFFIX = ".cfg";
    
    private static final String STRATEGY_SUFFIX = ".eviction.strategy";
 
    private static final String WAKE_UP_INTERVAL_SUFFIX = ".eviction.wake_up_interval";
    
    private static final String MAX_ENTRIES_SUFFIX = ".eviction.max_entries";
    
    private static final String LIFESPAN_SUFFIX = ".expiration.lifespan";
    
    private static final String MAX_IDLE_SUFFIX = ".expiration.max_idle";
    
    /** 
     * Classpath or filesystem resource containing Infinispan configurations the factory should use.
     * 
     * @see #DEF_INFINISPAN_CONFIG_RESOURCE
     */
    public static final String INFINISPAN_CONFIG_RESOURCE_PROP = "hibernate.cache.infinispan.cfg";
 
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
     * @see #DEF_TS_RESOURCE
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
     * Default value for {@link #INFINISPAN_RESOURCE_PROP}. Specifies the "infinispan-configs.xml" file in this package.
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
    
    private CacheManager manager;
    
    private final Map<String, TypeOverrides> typeOverrides = new HashMap<String, TypeOverrides>();
    
    private final Set<String> definedConfigurations = new HashSet<String>();
    
    private org.infinispan.transaction.lookup.TransactionManagerLookup transactionManagerlookup;
    
    private TransactionManager transactionManager;
 
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
       log.debug("Building collection cache region [" + regionName + "]");
       Cache cache = getCache(regionName, COLLECTION_KEY, properties);
-      return new CollectionRegionImpl(cache, regionName, metadata, transactionManager);
+      CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(cache);
+      CollectionRegionImpl region = new CollectionRegionImpl(cacheAdapter, regionName, metadata, transactionManager);
+      region.start();
+      return region;
    }
 
    /** {@inheritDoc} */
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building entity cache region [" + regionName + "]");
       Cache cache = getCache(regionName, ENTITY_KEY, properties);
-      return new EntityRegionImpl(cache, regionName, metadata, transactionManager);
+      CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(cache);
+      EntityRegionImpl region = new EntityRegionImpl(cacheAdapter, regionName, metadata, transactionManager);
+      region.start();
+      return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties)
             throws CacheException {
       log.debug("Building query results cache region [" + regionName + "]");
       String cacheName = typeOverrides.get(QUERY_KEY).getCacheName();
-      return new QueryResultsRegionImpl(manager.getCache(cacheName), regionName, properties, transactionManager);
+      CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(manager.getCache(cacheName));
+      QueryResultsRegionImpl region = new QueryResultsRegionImpl(cacheAdapter, regionName, properties, transactionManager);
+      region.start();
+      return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties)
             throws CacheException {
       log.debug("Building timestamps cache region [" + regionName + "]");
       String cacheName = typeOverrides.get(TIMESTAMPS_KEY).getCacheName();
-      return new TimestampsRegionImpl(manager.getCache(cacheName), regionName, transactionManager);
+      CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(manager.getCache(cacheName));
+      TimestampsRegionImpl region = new TimestampsRegionImpl(cacheAdapter, regionName, transactionManager);
+      region.start();
+      return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public boolean isMinimalPutsEnabledByDefault() {
       return true;
    }
 
    /**
     * {@inheritDoc}
     */
    public long nextTimestamp() {
       return System.currentTimeMillis() / 100;
    }
    
    public void setCacheManager(CacheManager manager) {
       this.manager = manager;
    }
 
    public CacheManager getCacheManager() {
       return manager;
    }
 
    /**
     * {@inheritDoc}
     */
    public void start(Settings settings, Properties properties) throws CacheException {
       log.debug("Starting Infinispan region factory");
       try {
          transactionManagerlookup = new HibernateTransactionManagerLookup(settings, properties);
          transactionManager = transactionManagerlookup.getTransactionManager();
          
          manager = createCacheManager(properties);
          initGenericDataTypeOverrides();
          Enumeration keys = properties.propertyNames();
          while (keys.hasMoreElements()) {
             String key = (String) keys.nextElement();
             int prefixLoc = -1;
             if ((prefixLoc = key.indexOf(PREFIX)) != -1) {
                dissectProperty(prefixLoc, key, properties);
             }
          }
          defineGenericDataTypeCacheConfigurations(settings, properties);
       } catch (CacheException ce) {
          throw ce;
       } catch (Throwable t) {
           throw new CacheException("Unable to start region factory", t);
       }
    }
 
    /**
     * {@inheritDoc}
     */
    public void stop() {
       log.debug("Stopping Infinispan CacheManager");
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
 
    protected CacheManager createCacheManager(Properties properties) throws CacheException {
       try {
          String configLoc = PropertiesHelper.getString(INFINISPAN_CONFIG_RESOURCE_PROP, properties, DEF_INFINISPAN_CONFIG_RESOURCE);
          return new DefaultCacheManager(configLoc);
       } catch (IOException e) {
          throw new CacheException("Unable to create default cache manager", e);
       }
    }
 
    private Map<String, TypeOverrides> initGenericDataTypeOverrides() {
       TypeOverrides entityOverrides = new TypeOverrides();
       entityOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(ENTITY_KEY, entityOverrides);
       TypeOverrides collectionOverrides = new TypeOverrides();
       collectionOverrides.setCacheName(DEF_ENTITY_RESOURCE);
       typeOverrides.put(COLLECTION_KEY, collectionOverrides);
       TypeOverrides timestampOverrides = new TimestampTypeOverrides();
       timestampOverrides.setCacheName(DEF_TIMESTAMPS_RESOURCE);
       typeOverrides.put(TIMESTAMPS_KEY, timestampOverrides);
       TypeOverrides queryOverrides = new TypeOverrides();
       queryOverrides.setCacheName(DEF_QUERY_RESOURCE);
       typeOverrides.put(QUERY_KEY, queryOverrides);
       return typeOverrides;
    }
    
 //   private boolean isGenericDataTypeProperty(String property) {
 //      return property.startsWith(PREFIX + ENTITY_KEY) || property.startsWith(PREFIX + COLLECTION_KEY) 
 //            || property.startsWith(PREFIX + QUERY_KEY) || property.startsWith(PREFIX + TIMESTAMP_KEY);
 //   }
    
    private void dissectProperty(int prefixLoc, String key, Properties properties) {
       TypeOverrides cfgOverride = null;
       int suffixLoc = -1;
       if (!key.equals(INFINISPAN_CONFIG_RESOURCE_PROP) && (suffixLoc = key.indexOf(CONFIG_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setCacheName(PropertiesHelper.extractPropertyValue(key, properties));
       } else if ((suffixLoc = key.indexOf(STRATEGY_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionStrategy(PropertiesHelper.extractPropertyValue(key, properties));
       } else if ((suffixLoc = key.indexOf(WAKE_UP_INTERVAL_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionWakeUpInterval(Long.parseLong(PropertiesHelper.extractPropertyValue(key, properties)));
       } else if ((suffixLoc = key.indexOf(MAX_ENTRIES_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setEvictionMaxEntries(PropertiesHelper.getInt(key, properties, -1));
       } else if ((suffixLoc = key.indexOf(LIFESPAN_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setExpirationLifespan(Long.parseLong(PropertiesHelper.extractPropertyValue(key, properties)));
       } else if ((suffixLoc = key.indexOf(MAX_IDLE_SUFFIX)) != -1) {
          cfgOverride = getOrCreateConfig(prefixLoc, key, suffixLoc);
          cfgOverride.setExpirationMaxIdle(Long.parseLong(PropertiesHelper.extractPropertyValue(key, properties)));
       }
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
    
    private void defineGenericDataTypeCacheConfigurations(Settings settings, Properties properties) throws CacheException {
       String[] defaultGenericDataTypes = new String[]{ENTITY_KEY, COLLECTION_KEY, TIMESTAMPS_KEY, QUERY_KEY};
       for (String type : defaultGenericDataTypes) {
          TypeOverrides override = typeOverrides.get(type);
          String cacheName = override.getCacheName();
          Configuration newCacheCfg = override.createInfinispanConfiguration();
          // Apply overrides
          Configuration cacheConfig = manager.defineConfiguration(cacheName, cacheName, newCacheCfg);
          // Configure transaction manager
          cacheConfig = configureTransactionManager(cacheConfig, cacheName, properties);
          manager.defineConfiguration(cacheName, cacheName, cacheConfig);
          definedConfigurations.add(cacheName);
          override.validateInfinispanConfiguration(cacheConfig);
       }
    }
    
    private Cache getCache(String regionName, String typeKey, Properties properties) {
       TypeOverrides regionOverride = typeOverrides.get(regionName);
       if (!definedConfigurations.contains(regionName)) {
          String templateCacheName = null;
          Configuration regionCacheCfg = null;
          if (regionOverride != null) {
             if (log.isDebugEnabled()) log.debug("Entity cache region specific configuration exists: " + regionOverride);
             regionCacheCfg = regionOverride.createInfinispanConfiguration();
             String cacheName = regionOverride.getCacheName();
             if (cacheName != null) // Region specific override with a given cache name
                templateCacheName = cacheName; 
             else // Region specific override without cache name, so template cache name is generic for data type.
                templateCacheName = typeOverrides.get(typeKey).getCacheName(); 
          } else {
             // No region specific overrides, template cache name is generic for data type.
             templateCacheName = typeOverrides.get(typeKey).getCacheName();
             regionCacheCfg = typeOverrides.get(typeKey).createInfinispanConfiguration();
          }
          // Configure transaction manager
          regionCacheCfg = configureTransactionManager(regionCacheCfg, templateCacheName, properties);
          // Apply overrides
          manager.defineConfiguration(regionName, templateCacheName, regionCacheCfg);
          definedConfigurations.add(regionName);
       }
       return manager.getCache(regionName);
    }
    
    private Configuration configureTransactionManager(Configuration regionOverrides, String templateCacheName, Properties properties) {
       // Get existing configuration to verify whether a tm was configured or not.
       Configuration templateConfig = manager.defineConfiguration(templateCacheName, new Configuration());
       String ispnTmLookupClassName = templateConfig.getTransactionManagerLookupClass();
       String hbTmLookupClassName = org.hibernate.cache.infinispan.tm.HibernateTransactionManagerLookup.class.getName();
       if (ispnTmLookupClassName != null && !ispnTmLookupClassName.equals(hbTmLookupClassName)) {
          log.debug("Infinispan is configured [" + ispnTmLookupClassName + "] with a different transaction manager lookup " +
                "class than Hibernate [" + hbTmLookupClassName + "]");
       } else {
          regionOverrides.setTransactionManagerLookup(transactionManagerlookup);
       }
       return regionOverrides;
    }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
index 2b4868fbdc..bad6ade4d7 100755
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
@@ -1,112 +1,135 @@
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
 package org.hibernate.cache.infinispan.access;
 
+import javax.transaction.Transaction;
+
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
-import org.infinispan.Cache;
+import org.hibernate.cache.infinispan.impl.BaseRegion;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.CacheHelper;
 
 /**
  * Defines the strategy for transactional access to entity or collection data in a Infinispan instance.
  * <p>
  * The intent of this class is to encapsulate common code and serve as a delegate for
  * {@link EntityRegionAccessStrategy} and {@link CollectionRegionAccessStrategy} implementations.
  * 
  * @author Brian Stansberry
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TransactionalAccessDelegate {
 
-   protected final Cache cache;
+   protected final CacheAdapter cacheAdapter;
+   protected final BaseRegion region;
 
-   public TransactionalAccessDelegate(Cache cache) {
-      this.cache = cache;
+   public TransactionalAccessDelegate(BaseRegion region) {
+      this.region = region;
+      this.cacheAdapter = region.getCacheAdapter();
    }
 
    public Object get(Object key, long txTimestamp) throws CacheException {
-      return cache.get(key);
+      if (!region.checkValid()) 
+         return null;
+      return cacheAdapter.get(key);
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
-      cache.putForExternalRead(key, value);
+      if (!region.checkValid())
+         return false;
+      cacheAdapter.putForExternalRead(key, value);
       return true;
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
             throws CacheException {
       return putFromLoad(key, value, txTimestamp, version);
    }
 
    public SoftLock lockItem(Object key, Object version) throws CacheException {
       return null;
    }
 
    public SoftLock lockRegion() throws CacheException {
       return null;
    }
 
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }
 
    public void unlockRegion(SoftLock lock) throws CacheException {
    }
 
    public boolean insert(Object key, Object value, Object version) throws CacheException {
-      cache.put(key, value);
+      if (!region.checkValid())
+         return false;
+      cacheAdapter.put(key, value);
       return true;
    }
 
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
       return false;
    }
 
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
-      cache.put(key, value);
+      // We update whether or not the region is valid. Other nodes
+      // may have already restored the region so they need to
+      // be informed of the change.
+      cacheAdapter.put(key, value);
       return true;
    }
 
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
             throws CacheException {
       return false;
    }
 
    public void remove(Object key) throws CacheException {
-      cache.remove(key);
+      // We update whether or not the region is valid. Other nodes
+      // may have already restored the region so they need to
+      // be informed of the change.
+      cacheAdapter.remove(key);
    }
 
    public void removeAll() throws CacheException {
-      cache.clear();
+      cacheAdapter.clear();
    }
 
-   public void evictAll() throws CacheException {
-      evictOrRemoveAll();
+   public void evict(Object key) throws CacheException {
+      cacheAdapter.remove(key);
    }
 
-   private void evictOrRemoveAll() throws CacheException {
-      cache.clear();
+   public void evictAll() throws CacheException {
+      Transaction tx = region.suspend();
+      try {
+         CacheHelper.sendEvictAllNotification(cacheAdapter, region.getAddress());
+      } finally {
+         region.resume(tx);
+      }
    }
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
index 41a00deaf5..35a68c9f39 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
@@ -1,33 +1,35 @@
 package org.hibernate.cache.infinispan.collection;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
-import org.infinispan.Cache;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.infinispan.notifications.Listener;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
+@Listener
 public class CollectionRegionImpl extends BaseTransactionalDataRegion implements CollectionRegion {
 
-   public CollectionRegionImpl(Cache cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
-      super(cache, name, metadata, transactionManager);
+   public CollectionRegionImpl(CacheAdapter cacheAdapter, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
+      super(cacheAdapter, name, metadata, transactionManager);
    }
 
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
       if (AccessType.READ_ONLY.equals(accessType)) {
          return new ReadOnlyAccess(this);
       } else if (AccessType.TRANSACTIONAL.equals(accessType)) {
          return new TransactionalAccess(this);
       }
       throw new CacheException("Unsupported access type [" + accessType.getName() + "]");
    }
 
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
index 7fe5d1acf6..82b89b4527 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
@@ -1,73 +1,73 @@
 package org.hibernate.cache.infinispan.collection;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 
 /**
  * Transactional collection region access for Infinispan.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 class TransactionalAccess implements CollectionRegionAccessStrategy {
 
    private final CollectionRegionImpl region;
    
    private final TransactionalAccessDelegate delegate;
 
    TransactionalAccess(CollectionRegionImpl region) {
       this.region = region;
-      this.delegate = new TransactionalAccessDelegate(region.getCache());
+      this.delegate = new TransactionalAccessDelegate(region);
    }
 
    public void evict(Object key) throws CacheException {
-      delegate.remove(key);
+      delegate.evict(key);
    }
 
    public void evictAll() throws CacheException {
       delegate.evictAll();
    }
 
    public Object get(Object key, long txTimestamp) throws CacheException {
       return delegate.get(key, txTimestamp);
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
       return delegate.putFromLoad(key, value, txTimestamp, version);
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
       return delegate.putFromLoad(key, value, txTimestamp, version, minimalPutOverride);
    }
 
    public void remove(Object key) throws CacheException {
       delegate.remove(key);
    }
 
    public void removeAll() throws CacheException {
       delegate.removeAll();
    }
 
    public CollectionRegion getRegion() {
       return region;
    }
 
    public SoftLock lockItem(Object key, Object version) throws CacheException {
       return null;
    }
 
    public SoftLock lockRegion() throws CacheException {
       return null;
    }
 
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }
 
    public void unlockRegion(SoftLock lock) throws CacheException {
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
index 58d4dd5b68..d0d4322cda 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
@@ -1,33 +1,35 @@
 package org.hibernate.cache.infinispan.entity;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
-import org.infinispan.Cache;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.infinispan.notifications.Listener;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
+@Listener
 public class EntityRegionImpl extends BaseTransactionalDataRegion implements EntityRegion {
 
-   public EntityRegionImpl(Cache cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
-      super(cache, name, metadata, transactionManager);
+   public EntityRegionImpl(CacheAdapter cacheAdapter, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
+      super(cacheAdapter, name, metadata, transactionManager);
    }
 
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
       if (AccessType.READ_ONLY.equals(accessType)) {
          return new ReadOnlyAccess(this);
       } else if (AccessType.TRANSACTIONAL.equals(accessType)) {
          return new TransactionalAccess(this);
       }
       throw new CacheException("Unsupported access type [" + accessType.getName() + "]");
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
index c11b018dfa..acb6251262 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
@@ -1,89 +1,88 @@
 package org.hibernate.cache.infinispan.entity;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 
 /**
  * Transactional entity region access for Infinispan.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 class TransactionalAccess implements EntityRegionAccessStrategy {
  
    private final EntityRegionImpl region;
    
    private final TransactionalAccessDelegate delegate;
 
    TransactionalAccess(EntityRegionImpl region) {
       this.region = region;
-      this.delegate = new TransactionalAccessDelegate(region.getCache());
+      this.delegate = new TransactionalAccessDelegate(region);
    }
 
    public void evict(Object key) throws CacheException {
-      delegate.remove(key);
+      delegate.evict(key);
    }
 
    public void evictAll() throws CacheException {
       delegate.evictAll();
    }
 
    public Object get(Object key, long txTimestamp) throws CacheException {
       return delegate.get(key, txTimestamp);
    }
 
    public EntityRegion getRegion() {
       return this.region;
    }
 
    public boolean insert(Object key, Object value, Object version) throws CacheException {
-      region.getCache().put(key, value);
-      return true; // TODO this is suspect
+      return delegate.insert(key, value, version);
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
       return delegate.putFromLoad(key, value, txTimestamp, version);
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
       return delegate.putFromLoad(key, value, txTimestamp, version, minimalPutOverride);
    }
 
    public void remove(Object key) throws CacheException {
       delegate.remove(key);
    }
 
    public void removeAll() throws CacheException {
       delegate.removeAll();
    }
 
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
       return delegate.update(key, value, currentVersion, previousVersion);
    }
 
    public SoftLock lockItem(Object key, Object version) throws CacheException {
       return null;
    }
 
    public SoftLock lockRegion() throws CacheException {
       return null;
    }
 
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }
 
    public void unlockRegion(SoftLock lock) throws CacheException {
    }
 
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
       return false;
    }
 
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
       return false;
    }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
index 9661a75f45..297012f041 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
@@ -1,38 +1,38 @@
 package org.hibernate.cache.infinispan.impl;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.GeneralDataRegion;
-import org.infinispan.Cache;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 
 /**
  * Support for Infinispan {@link GeneralDataRegion} implementors.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class BaseGeneralDataRegion extends BaseRegion implements GeneralDataRegion {
 
-   public BaseGeneralDataRegion(Cache cache, String name, TransactionManager transactionManager) {
-      super(cache, name, transactionManager);
+   public BaseGeneralDataRegion(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager) {
+      super(cacheAdapter, name, transactionManager);
    }
 
    public void evict(Object key) throws CacheException {
-      getCache().evict(key);
+      cacheAdapter.evict(key);
    }
 
    public void evictAll() throws CacheException {
-      getCache().clear();
+      cacheAdapter.clear();
    }
 
    public Object get(Object key) throws CacheException {
-      return getCache().get(key);
+      return cacheAdapter.get(key);
    }
 
    public void put(Object key, Object value) throws CacheException {
-      getCache().put(key, value);
+      cacheAdapter.put(key, value);
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
index 5202d84847..af44d507b8 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
@@ -1,141 +1,286 @@
 package org.hibernate.cache.infinispan.impl;
 
+import java.util.Collections;
+import java.util.HashSet;
+import java.util.List;
 import java.util.Map;
+import java.util.Set;
+import java.util.concurrent.atomic.AtomicReference;
 
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.Region;
+import org.hibernate.cache.infinispan.util.AddressAdapter;
+import org.hibernate.cache.infinispan.util.AddressAdapterImpl;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheHelper;
-import org.infinispan.Cache;
-import org.infinispan.context.Flag;
+import org.hibernate.cache.infinispan.util.FlagAdapter;
+import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
+import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
+import org.infinispan.notifications.cachelistener.event.CacheEntryInvalidatedEvent;
+import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
+import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
+import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * Support for Infinispan {@link Region}s. Handles common "utility" methods for an underlying named
  * Cache. In other words, this implementation doesn't actually read or write data. Subclasses are
  * expected to provide core cache interaction appropriate to the semantics needed.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class BaseRegion implements Region {
-   private final Cache cache;
+   private enum InvalidateState { INVALID, CLEARING, VALID };
+   private static final Log log = LogFactory.getLog(BaseRegion.class);
    private final String name;
+   protected final CacheAdapter cacheAdapter;
+   protected final AddressAdapter address;
+   protected final Set<AddressAdapter> currentView = new HashSet<AddressAdapter>();
    protected final TransactionManager transactionManager;
+   protected final boolean replication;
+   protected final Object invalidationMutex = new Object();
+   protected final AtomicReference<InvalidateState> invalidateState = new AtomicReference<InvalidateState>(InvalidateState.VALID);
 
-   public BaseRegion(Cache cache, String name, TransactionManager transactionManager) {
-      this.cache = cache;
+   public BaseRegion(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager) {
+      this.cacheAdapter = cacheAdapter;
       this.name = name;
       this.transactionManager = transactionManager;
+      this.replication = cacheAdapter.isClusteredReplication();
+      this.address = this.cacheAdapter.getAddress();
+      this.cacheAdapter.addListener(this);
    }
 
-   public Cache getCache() {
-      return cache;
+   public void start() {
+      if (address != null) {
+         synchronized (currentView) {
+            List<AddressAdapter> view = cacheAdapter.getMembers();
+            if (view != null) {
+               currentView.addAll(view);
+               establishInternalNodes();
+            }
+         }
+      }
+   }
+
+   /**
+    * Calls to this method must be done from synchronized (currentView) blocks only!!
+    */
+   private void establishInternalNodes() {
+      Transaction tx = suspend();
+      try {
+         for (AddressAdapter member : currentView) {
+            CacheHelper.initInternalEvict(cacheAdapter, member);
+         }
+      } finally {
+         resume(tx);
+      }
    }
 
    public String getName() {
       return name;
    }
 
+   public CacheAdapter getCacheAdapter() {
+      return cacheAdapter;
+   }
+
    public long getElementCountInMemory() {
-      return cache.size();
+      if (checkValid()) {
+         Set keySet = cacheAdapter.keySet();
+         int size = cacheAdapter.size();
+         if (CacheHelper.containsEvictAllNotification(keySet, address))
+            size--;
+         return size;
+      }
+      return 0;
    }
 
    /**
     * Not supported.
     * 
     * @return -1
     */
    public long getElementCountOnDisk() {
       return -1;
    }
 
    /**
     * Not supported.
     * 
     * @return -1
     */
    public long getSizeInMemory() {
       return -1;
    }
 
    public int getTimeout() {
       return 600; // 60 seconds
    }
 
    public long nextTimestamp() {
       return System.currentTimeMillis() / 100;
    }
 
    public Map toMap() {
-      return cache;
+      if (checkValid()) {
+         Map map = cacheAdapter.toMap();
+         Set keys = map.keySet();
+         for (Object key : keys) {
+            if (CacheHelper.isEvictAllNotification(key)) {
+               map.remove(key);
+            }
+         }
+         return map;
+      }
+      return Collections.EMPTY_MAP;
    }
 
    public void destroy() throws CacheException {
-      cache.clear();
+      try {
+         cacheAdapter.clear();
+      } finally {
+         cacheAdapter.removeListener(this);
+      }
    }
    
    public boolean contains(Object key) {
-      return CacheHelper.containsKey(cache, key, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);
+      if (!checkValid())
+         return false;
+      // Reads are non-blocking in Infinispan, so not sure of the necessity of passing ZERO_LOCK_ACQUISITION_TIMEOUT
+      return cacheAdapter.withFlags(FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT).containsKey(key);
    }
-   
+
+   public AddressAdapter getAddress() {
+      return address;
+   }
+
+   public boolean checkValid() {
+      boolean valid = invalidateState.get() == InvalidateState.VALID;
+      if (!valid) {
+         synchronized (invalidationMutex) {
+            if (invalidateState.compareAndSet(InvalidateState.INVALID, InvalidateState.CLEARING)) {
+               Transaction tx = suspend();
+               try {
+                  cacheAdapter.withFlags(FlagAdapter.CACHE_MODE_LOCAL, FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT).clear();
+                  invalidateState.compareAndSet(InvalidateState.CLEARING, InvalidateState.VALID);
+               }
+               catch (Exception e) {
+                  if (log.isTraceEnabled()) {
+                     log.trace("Could not invalidate region: " + e.getLocalizedMessage());
+                  }
+               }
+               finally {
+                  resume(tx);
+               }
+            }
+         }
+         valid = invalidateState.get() == InvalidateState.VALID;
+      }
+      
+      return valid;
+   }
+
    /**
     * Performs a JBoss Cache <code>get(Fqn, Object)</code> after first
     * {@link #suspend suspending any ongoing transaction}. Wraps any exception
     * in a {@link CacheException}. Ensures any ongoing transaction is resumed.
     * 
     * @param key The key of the item to get
     * @param opt any option to add to the get invocation. May be <code>null</code>
     * @param suppressTimeout should any TimeoutException be suppressed?
     * @return The retrieved object
       * @throws CacheException issue managing transaction or talking to cache
     */
-   protected Object suspendAndGet(Object key, Flag opt, boolean suppressTimeout) throws CacheException {
+   protected Object suspendAndGet(Object key, FlagAdapter opt, boolean suppressTimeout) throws CacheException {
        Transaction tx = suspend();
        try {
            if (suppressTimeout)
-               return CacheHelper.getAllowingTimeout(cache, key);
+               return cacheAdapter.getAllowingTimeout(key);
            else
-               return CacheHelper.get(cache, key);
+               return cacheAdapter.get(key);
        } finally {
            resume(tx);
        }
    }
    
    /**
     * Tell the TransactionManager to suspend any ongoing transaction.
     * 
     * @return the transaction that was suspended, or <code>null</code> if
     *         there wasn't one
     */
-   protected Transaction suspend() {
+   public Transaction suspend() {
        Transaction tx = null;
        try {
            if (transactionManager != null) {
                tx = transactionManager.suspend();
            }
        } catch (SystemException se) {
            throw new CacheException("Could not suspend transaction", se);
        }
        return tx;
    }
-   
+
    /**
     * Tell the TransactionManager to resume the given transaction
     * 
     * @param tx
     *            the transaction to suspend. May be <code>null</code>.
     */
-   protected void resume(Transaction tx) {
+   public void resume(Transaction tx) {
        try {
            if (tx != null)
                transactionManager.resume(tx);
        } catch (Exception e) {
            throw new CacheException("Could not resume transaction", e);
        }
    }
 
+   @CacheEntryModified
+   public void entryModified(CacheEntryModifiedEvent event) {
+      handleEvictAllModification(event);
+   }
+
+   protected boolean handleEvictAllModification(CacheEntryModifiedEvent event) {
+      if (!event.isPre() && (replication || event.isOriginLocal()) && CacheHelper.isEvictAllNotification(event.getKey(), event.getValue())) {
+         if (log.isTraceEnabled()) log.trace("Set invalid state because marker cache entry was put: {0}", event);
+         invalidateState.set(InvalidateState.INVALID);
+         return true;
+      }
+      return false;
+   }
+
+   @CacheEntryInvalidated
+   public void entryInvalidated(CacheEntryInvalidatedEvent event) {
+      if (log.isTraceEnabled()) log.trace("Cache entry invalidated: {0}", event);
+      handleEvictAllInvalidation(event);
+   }
+
+   protected boolean handleEvictAllInvalidation(CacheEntryInvalidatedEvent event) {
+      if (!event.isPre() && CacheHelper.isEvictAllNotification(event.getKey())) {
+         if (log.isTraceEnabled()) log.trace("Set invalid state because marker cache entry was invalidated: {0}", event);
+         invalidateState.set(InvalidateState.INVALID);
+         return true;
+      }
+      return false;
+   }
+
+   @ViewChanged
+   public void viewChanged(ViewChangedEvent event) {
+      synchronized (currentView) {
+         List<AddressAdapter> view = AddressAdapterImpl.toAddressAdapter(event.getNewMembers());
+         if (view != null) {
+            currentView.addAll(view);
+            establishInternalNodes();
+         }
+      }
+   }
+
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
index f4949919be..a9acd7b625 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
@@ -1,33 +1,33 @@
 package org.hibernate.cache.infinispan.impl;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.TransactionalDataRegion;
-import org.infinispan.Cache;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 
 /**
  * Support for Inifinispan {@link TransactionalDataRegion} implementors.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class BaseTransactionalDataRegion extends BaseRegion implements TransactionalDataRegion {
 
    private final CacheDataDescription metadata;
 
-   public BaseTransactionalDataRegion(Cache cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
-      super(cache, name, transactionManager);
+   public BaseTransactionalDataRegion(CacheAdapter cacheAdapter, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
+      super(cacheAdapter, name, transactionManager);
       this.metadata = metadata;
    }
 
    public CacheDataDescription getCacheDataDescription() {
       return metadata;
    }
 
    public boolean isTransactionAware() {
       return transactionManager != null;
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
index fe2415cb4c..09f782962d 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
@@ -1,75 +1,86 @@
 package org.hibernate.cache.infinispan.query;
 
 import java.util.Properties;
 
+import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheHelper;
-import org.infinispan.Cache;
-import org.infinispan.context.Flag;
+import org.hibernate.cache.infinispan.util.FlagAdapter;
+import org.infinispan.notifications.Listener;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
+@Listener
 public class QueryResultsRegionImpl extends BaseTransactionalDataRegion implements QueryResultsRegion {
    private boolean localOnly;
 
-   public QueryResultsRegionImpl(Cache cache, String name, Properties properties, TransactionManager transactionManager) {
-      super(cache, name, null, transactionManager);
+   public QueryResultsRegionImpl(CacheAdapter cacheAdapter, String name, Properties properties, TransactionManager transactionManager) {
+      super(cacheAdapter, name, null, transactionManager);
       
       // If Infinispan is using INVALIDATION for query cache, we don't want to propagate changes.
       // We use the Timestamps cache to manage invalidation
-      localOnly = CacheHelper.isClusteredInvalidation(cache);
+      localOnly = cacheAdapter.isClusteredInvalidation();
    }
 
    public void evict(Object key) throws CacheException {
       if (localOnly)
-         CacheHelper.removeKey(getCache(), key, Flag.CACHE_MODE_LOCAL);
+         cacheAdapter.withFlags(FlagAdapter.CACHE_MODE_LOCAL).remove(key);
       else 
-         CacheHelper.removeKey(getCache(), key);
+         cacheAdapter.remove(key);
    }
 
    public void evictAll() throws CacheException {
-      if (localOnly)
-         CacheHelper.removeAll(getCache(), Flag.CACHE_MODE_LOCAL);
-      else 
-         CacheHelper.removeAll(getCache());
+      Transaction tx = suspend();
+      try {
+         CacheHelper.sendEvictAllNotification(cacheAdapter, getAddress());
+      } finally {
+         resume(tx);
+      }
    }
 
    public Object get(Object key) throws CacheException {
+      if (!checkValid())
+         return null;
+
       // Don't hold the JBC node lock throughout the tx, as that
       // prevents updates
       // Add a zero (or low) timeout option so we don't block
       // waiting for tx's that did a put to commit
-      return suspendAndGet(key, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT, true);
+      return suspendAndGet(key, FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT, true);
    }
 
    public void put(Object key, Object value) throws CacheException {
-      // Here we don't want to suspend the tx. If we do:
-      // 1) We might be caching query results that reflect uncommitted
-      // changes. No tx == no WL on cache node, so other threads
-      // can prematurely see those query results
-      // 2) No tx == immediate replication. More overhead, plus we
-      // spread issue #1 above around the cluster
+      if (checkValid()) {
+         // Here we don't want to suspend the tx. If we do:
+         // 1) We might be caching query results that reflect uncommitted
+         // changes. No tx == no WL on cache node, so other threads
+         // can prematurely see those query results
+         // 2) No tx == immediate replication. More overhead, plus we
+         // spread issue #1 above around the cluster
 
-      // Add a zero (or quite low) timeout option so we don't block.
-      // Ignore any TimeoutException. Basically we forego caching the
-      // query result in order to avoid blocking.
-      // Reads are done with suspended tx, so they should not hold the
-      // lock for long.  Not caching the query result is OK, since
-      // any subsequent read will just see the old result with its
-      // out-of-date timestamp; that result will be discarded and the
-      // db query performed again.
-      if (localOnly)
-         CacheHelper.putAllowingTimeout(getCache(), key, value, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT, Flag.CACHE_MODE_LOCAL);
-      else 
-         CacheHelper.putAllowingTimeout(getCache(), key, value, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);
-      
+         // Add a zero (or quite low) timeout option so we don't block.
+         // Ignore any TimeoutException. Basically we forego caching the
+         // query result in order to avoid blocking.
+         // Reads are done with suspended tx, so they should not hold the
+         // lock for long.  Not caching the query result is OK, since
+         // any subsequent read will just see the old result with its
+         // out-of-date timestamp; that result will be discarded and the
+         // db query performed again.
+         if (localOnly)
+            cacheAdapter.withFlags(FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT, FlagAdapter.CACHE_MODE_LOCAL)
+               .putAllowingTimeout(key, value);
+         else 
+            cacheAdapter.withFlags(FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT)
+               .putAllowingTimeout(key, value);
+      }
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
index 2609ff6f85..5056db968e 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
@@ -1,113 +1,138 @@
 package org.hibernate.cache.infinispan.timestamp;
 
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.TimestampsRegion;
 import org.hibernate.cache.infinispan.impl.BaseGeneralDataRegion;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheHelper;
-import org.infinispan.Cache;
-import org.infinispan.context.Flag;
+import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
+import org.infinispan.notifications.cachelistener.event.CacheEntryInvalidatedEvent;
 import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
 import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
 
 /**
  * Defines the behavior of the timestamps cache region for Infinispan.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 @Listener
 public class TimestampsRegionImpl extends BaseGeneralDataRegion implements TimestampsRegion {
 
    private Map localCache = new ConcurrentHashMap();
 
-   public TimestampsRegionImpl(Cache cache, String name, TransactionManager transactionManager) {
-      super(cache, name, transactionManager);
-      cache.addListener(this);
+   public TimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager) {
+      super(cacheAdapter, name, transactionManager);
+      cacheAdapter.addListener(this);
       populateLocalCache();
    }
 
    @Override
    public void evict(Object key) throws CacheException {
       // TODO Is this a valid operation on a timestamps cache?
-      CacheHelper.removeKey(getCache(), key);
+      cacheAdapter.remove(key);
    }
 
    public void evictAll() throws CacheException {
       // TODO Is this a valid operation on a timestamps cache?
-      CacheHelper.removeAll(getCache());
+      Transaction tx = suspend();
+      try {        
+         CacheHelper.sendEvictAllNotification(cacheAdapter, getAddress());
+      } finally {
+         resume(tx);
+      }
    }
 
    public Object get(Object key) throws CacheException {
       Object value = localCache.get(key);
-      if (value == null) {
+      if (value == null && checkValid()) {
          value = suspendAndGet(key, null, false);
          if (value != null)
             localCache.put(key, value);
       }
       return value;
    }
 
    public void put(Object key, Object value) throws CacheException {
       // Don't hold the JBC node lock throughout the tx, as that
       // prevents reads and other updates
       Transaction tx = suspend();
       try {
          // We ensure ASYNC semantics (JBCACHE-1175)
-         CacheHelper.put(getCache(), key, value, Flag.FORCE_ASYNCHRONOUS);
+         cacheAdapter.withFlags(FlagAdapter.FORCE_ASYNCHRONOUS).put(key, value);
       } catch (Exception e) {
          throw new CacheException(e);
       } finally {
          resume(tx);
       }
    }
 
    @Override
    public void destroy() throws CacheException {
       localCache.clear();
-      getCache().removeListener(this);
+      cacheAdapter.removeListener(this);
       super.destroy();
    }
 
    /**
     * Monitors cache events and updates the local cache
     * 
     * @param event
     */
    @CacheEntryModified
    public void nodeModified(CacheEntryModifiedEvent event) {
-      if (event.isPre()) return;
-      localCache.put(event.getKey(), event.getValue());
+      if (!handleEvictAllModification(event) && !event.isPre()) {
+         localCache.put(event.getKey(), event.getValue());
+      }
    }
 
    /**
     * Monitors cache events and updates the local cache
     * 
     * @param event
     */
    @CacheEntryRemoved
    public void nodeRemoved(CacheEntryRemovedEvent event) {
       if (event.isPre()) return;
       localCache.remove(event.getKey());
    }
 
+   @Override
+   protected boolean handleEvictAllModification(CacheEntryModifiedEvent event) {
+      boolean result = super.handleEvictAllModification(event);
+      if (result) {
+         localCache.clear();
+      }
+      return result;
+   }
+
+   @Override
+   protected boolean handleEvictAllInvalidation(CacheEntryInvalidatedEvent event) {
+      boolean result = super.handleEvictAllInvalidation(event);
+      if (result) {
+         localCache.clear();
+      }
+      return result;
+   }
+
    /**
     * Brings all data from the distributed cache into our local cache.
     */
    private void populateLocalCache() {
-      Set children = CacheHelper.getKeySet(getCache());
+      Set children = cacheAdapter.keySet();
       for (Object key : children)
          get(key);
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java
new file mode 100644
index 0000000000..c9c736d300
--- /dev/null
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapter.java
@@ -0,0 +1,32 @@
+/*
+ * JBoss, Home of Professional Open Source.
+ * Copyright 2009, Red Hat, Inc. and/or its affiliates, and
+ * individual contributors as indicated by the @author tags. See the
+ * copyright.txt file in the distribution for a full listing of
+ * individual contributors.
+ *
+ * This is free software; you can redistribute it and/or modify it
+ * under the terms of the GNU Lesser General Public License as
+ * published by the Free Software Foundation; either version 2.1 of
+ * the License, or (at your option) any later version.
+ *
+ * This software is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of
+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
+ * Lesser General Public License for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public
+ * License along with this software; if not, write to the Free
+ * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
+ * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
+ */
+package org.hibernate.cache.infinispan.util;
+
+/**
+ * AddressAdapter.
+ * 
+ * @author Galder Zamarreño
+ * @since 3.5
+ */
+public interface AddressAdapter {
+}
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java
new file mode 100644
index 0000000000..93345ddfdf
--- /dev/null
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/AddressAdapterImpl.java
@@ -0,0 +1,84 @@
+/*
+ * JBoss, Home of Professional Open Source.
+ * Copyright 2009, Red Hat, Inc. and/or its affiliates, and
+ * individual contributors as indicated by the @author tags. See the
+ * copyright.txt file in the distribution for a full listing of
+ * individual contributors.
+ *
+ * This is free software; you can redistribute it and/or modify it
+ * under the terms of the GNU Lesser General Public License as
+ * published by the Free Software Foundation; either version 2.1 of
+ * the License, or (at your option) any later version.
+ *
+ * This software is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of
+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
+ * Lesser General Public License for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public
+ * License along with this software; if not, write to the Free
+ * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
+ * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
+ */
+package org.hibernate.cache.infinispan.util;
+
+import java.io.Externalizable;
+import java.io.IOException;
+import java.io.ObjectInput;
+import java.io.ObjectOutput;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.infinispan.remoting.transport.Address;
+
+/**
+ * AddressAdapterImpl.
+ * 
+ * @author Galder Zamarreño
+ * @since 3.5
+ */
+public class AddressAdapterImpl implements AddressAdapter, Externalizable {
+
+   private Address address;
+
+   private AddressAdapterImpl(Address address) {
+      this.address = address;
+   }
+
+   static AddressAdapter newInstance(Address address) {
+      return new AddressAdapterImpl(address);
+   }
+
+   public static List<AddressAdapter> toAddressAdapter(List<Address> ispnAddresses) {
+      List<AddressAdapter> addresses = new ArrayList<AddressAdapter>(ispnAddresses.size());
+      for (Address address : ispnAddresses) {
+         addresses.add(AddressAdapterImpl.newInstance(address));
+      }
+      return addresses;
+   }
+
+   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
+      address = (Address) in.readObject();
+   }
+
+   public void writeExternal(ObjectOutput out) throws IOException {
+      out.writeObject(address);
+   }
+
+   @Override
+   public boolean equals(Object obj) {
+      if (obj == this)
+         return true;
+      if (!(obj instanceof AddressAdapterImpl))
+         return false;
+      AddressAdapterImpl other = (AddressAdapterImpl) obj;
+      return other.address.equals(address);
+   }
+
+   @Override
+   public int hashCode() {
+      int result = 17;
+      result = 31 * result + address.hashCode();
+      return result;
+   }
+}
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java
new file mode 100644
index 0000000000..f092182253
--- /dev/null
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapter.java
@@ -0,0 +1,207 @@
+/*
+ * JBoss, Home of Professional Open Source.
+ * Copyright 2009, Red Hat, Inc. and/or its affiliates, and
+ * individual contributors as indicated by the @author tags. See the
+ * copyright.txt file in the distribution for a full listing of
+ * individual contributors.
+ *
+ * This is free software; you can redistribute it and/or modify it
+ * under the terms of the GNU Lesser General Public License as
+ * published by the Free Software Foundation; either version 2.1 of
+ * the License, or (at your option) any later version.
+ *
+ * This software is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of
+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
+ * Lesser General Public License for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public
+ * License along with this software; if not, write to the Free
+ * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
+ * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
+ */
+package org.hibernate.cache.infinispan.util;
+
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.hibernate.cache.CacheException;
+import org.infinispan.Cache;
+import org.infinispan.config.Configuration;
+import org.infinispan.util.concurrent.TimeoutException;
+
+/**
+ * Infinispan cache abstraction.
+ * 
+ * @author Galder Zamarreño
+ * @since 3.5
+ */
+public interface CacheAdapter {
+
+   /**
+    * Is this cache participating in a cluster with invalidation?
+    * 
+    * @return true if the cache is configured for synchronous/asynchronous invalidation; false otherwise.
+    */
+   boolean isClusteredInvalidation();
+
+   /**
+    * Is this cache participating in a cluster with replication?
+    * 
+    * @return true if the cache is configured for synchronous/asynchronous invalidation; false otherwise.
+    */
+   boolean isClusteredReplication();
+
+   /**
+    * Is this cache configured for synchronous communication?
+    * 
+    * @return true if the cache is configured for synchronous communication; false otherwise.
+    */
+   boolean isSynchronous();
+
+   /**
+    * Set of keys of this cache.
+    * 
+    * @return Set containing keys stored in this cache.
+    */
+   Set keySet();
+
+   /** 
+    * A builder-style method that adds flags to any cache API call.
+    * 
+    * @param flagAdapters a set of flags to apply.  See the {@link FlagAdapter} documentation.
+    * @return a cache on which a real operation is to be invoked.
+    */
+   CacheAdapter withFlags(FlagAdapter... flagAdapters);
+
+   /**
+    * Method to check whether a certain key exists in this cache.
+    * 
+    * @param key key to look up.
+    * @return true if key is present, false otherwise.
+    */
+   boolean containsKey(Object key);
+
+   /**
+    * Performs an <code>get(Object)</code> on the cache, wrapping any exception in a {@link CacheException}.
+    * 
+    * @param key key to retrieve
+    * @throws CacheException
+    */
+   Object get(Object key) throws CacheException;
+
+   /**
+    * Performs an <code>get(Object)</code> on the cache ignoring any {@link TimeoutException} 
+    * and wrapping any other exception in a {@link CacheException}.
+    * 
+    * @param key key to retrieve
+    * @throws CacheException
+    */
+   Object getAllowingTimeout(Object key) throws CacheException;
+
+   /**
+    * Performs a <code>put(Object, Object)</code> on the cache, wrapping any exception in a {@link CacheException}.
+    * 
+    * @param key key whose value will be modified
+    * @param value data to store in the cache entry
+    * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> 
+    *         if there was no mapping for <tt>key</tt>.
+    * @throws CacheException
+    */
+   Object put(Object key, Object value) throws CacheException;
+
+   /**
+    * Performs a <code>put(Object, Object)</code> on the cache ignoring any {@link TimeoutException} 
+    * and wrapping any exception in a {@link CacheException}.
+    * 
+    * @param key key whose value will be modified
+    * @param value data to store in the cache entry
+    * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> 
+    *         if there was no mapping for <tt>key</tt>.
+    * @throws CacheException
+    */
+   Object putAllowingTimeout(Object key, Object value) throws CacheException;
+
+   /**
+    * See {@link Cache#putForExternalRead(Object, Object)} for detailed documentation.
+    * 
+    * @param key key with which the specified value is to be associated.
+    * @param value value to be associated with the specified key.
+    * @throws CacheException
+    */
+   void putForExternalRead(Object key, Object value) throws CacheException;
+
+   /**
+    * Performs a <code>remove(Object)</code>, wrapping any exception in a {@link CacheException}.
+    * 
+    * @param key key to be removed
+    * @return the previous value associated with <tt>key</tt>, or 
+    *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
+    * @throws CacheException
+    */
+   Object remove(Object key) throws CacheException;
+
+   /**
+    * Evict the given key from memory.
+    * 
+    * @param key to evict.
+    */
+   void evict(Object key) throws CacheException;
+
+   /**
+    * Clear the cache.
+    * 
+    * @throws CacheException
+    */
+   void clear() throws CacheException;
+
+   /**
+    * Add listener to this cache.
+    * 
+    * @param listener to be added to cache.
+    */
+   void addListener(Object listener);
+
+   /**
+    * Get local cluster address.
+    * 
+    * @return Address representing local address.
+    */
+   AddressAdapter getAddress();
+
+   /**
+    * Get cluster members.
+    * 
+    * @return List of cluster member Address instances
+    */
+   List<AddressAdapter> getMembers();
+
+   /**
+    * Size of cache.
+    * 
+    * @return number of cache entries.
+    */
+   int size();
+
+   /**
+    * This method returns a Map view of the cache.
+    * 
+    * @return Map view of cache.
+    */
+   Map toMap();
+
+   /**
+    * Remove listener from cache instance.
+    * 
+    * @param listener to be removed.
+    */
+   void removeListener(Object listener);
+
+   /**
+    * Get cache configuration.
+    * 
+    * @return Configuration instance associated with this cache.
+    */
+   Configuration getConfiguration();
+}
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java
new file mode 100644
index 0000000000..6ef1de5839
--- /dev/null
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheAdapterImpl.java
@@ -0,0 +1,205 @@
+/*
+ * JBoss, Home of Professional Open Source.
+ * Copyright 2009, Red Hat, Inc. and/or its affiliates, and
+ * individual contributors as indicated by the @author tags. See the
+ * copyright.txt file in the distribution for a full listing of
+ * individual contributors.
+ *
+ * This is free software; you can redistribute it and/or modify it
+ * under the terms of the GNU Lesser General Public License as
+ * published by the Free Software Foundation; either version 2.1 of
+ * the License, or (at your option) any later version.
+ *
+ * This software is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of
+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
+ * Lesser General Public License for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public
+ * License along with this software; if not, write to the Free
+ * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
+ * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
+ */
+package org.hibernate.cache.infinispan.util;
+
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.hibernate.cache.CacheException;
+import org.infinispan.Cache;
+import org.infinispan.config.Configuration;
+import org.infinispan.context.Flag;
+import org.infinispan.remoting.rpc.RpcManager;
+import org.infinispan.util.concurrent.TimeoutException;
+
+/**
+ * CacheAdapterImpl.
+ * 
+ * @author Galder Zamarreño
+ * @since 3.5
+ */
+public class CacheAdapterImpl implements CacheAdapter {
+
+   private final Cache cache;
+
+   private CacheAdapterImpl(Cache cache) {
+      this.cache = cache;
+   }
+
+   public static CacheAdapter newInstance(Cache cache) {
+      return new CacheAdapterImpl(cache);
+   }
+
+   public boolean isClusteredInvalidation() {
+      return isClusteredInvalidation(cache.getConfiguration().getCacheMode());
+   }
+
+   public boolean isClusteredReplication() {
+      return isClusteredReplication(cache.getConfiguration().getCacheMode());
+   }
+
+   public boolean isSynchronous() {
+      return isSynchronous(cache.getConfiguration().getCacheMode());
+   }
+
+   public Set keySet() {
+      return cache.keySet();
+   }
+
+   public CacheAdapter withFlags(FlagAdapter... flagAdapters) {
+      Flag[] flags = FlagAdapter.toFlags(flagAdapters);
+      return newInstance(cache.getAdvancedCache().withFlags(flags));
+   }
+
+   public Object get(Object key) throws CacheException {
+      try {
+         return cache.get(key);
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public Object getAllowingTimeout(Object key) throws CacheException {
+      try {
+         return cache.get(key);
+      } catch (TimeoutException ignored) {
+         // ignore it
+         return null;
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public Object put(Object key, Object value) throws CacheException {
+      try {
+         return cache.put(key, value);
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public Object putAllowingTimeout(Object key, Object value) throws CacheException {
+      try {
+         return cache.put(key, value);
+      } catch (TimeoutException allowed) {
+         // ignore it
+         return null;
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public void putForExternalRead(Object key, Object value) throws CacheException {
+      try {
+         cache.putForExternalRead(key, value);
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public Object remove(Object key) throws CacheException {
+      try {
+         return cache.remove(key);
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public void evict(Object key) throws CacheException {
+      try {
+         cache.evict(key);
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   public void clear() throws CacheException {
+      try {
+         cache.clear();
+      } catch (Exception e) {
+         throw new CacheException(e);
+      }
+   }
+
+   private static boolean isClusteredInvalidation(Configuration.CacheMode cacheMode) {
+      return cacheMode == Configuration.CacheMode.INVALIDATION_ASYNC
+               || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC;
+   }
+
+   private static boolean isClusteredReplication(Configuration.CacheMode cacheMode) {
+      return cacheMode == Configuration.CacheMode.REPL_ASYNC
+               || cacheMode == Configuration.CacheMode.REPL_SYNC;
+   }
+
+   private static boolean isSynchronous(Configuration.CacheMode cacheMode) {
+      return cacheMode == Configuration.CacheMode.REPL_SYNC
+               || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC
+               || cacheMode == Configuration.CacheMode.DIST_SYNC;
+   }
+
+   public void addListener(Object listener) {
+      cache.addListener(listener);
+   }
+
+   public AddressAdapter getAddress() {
+      RpcManager rpc = cache.getAdvancedCache().getRpcManager();
+      if (rpc != null) {
+         return AddressAdapterImpl.newInstance(rpc.getTransport().getAddress());
+      }
+      return null;
+   }
+
+   public List<AddressAdapter> getMembers() {
+      RpcManager rpc = cache.getAdvancedCache().getRpcManager();
+      if (rpc != null) {
+         return AddressAdapterImpl.toAddressAdapter(rpc.getTransport().getMembers());
+      }
+      return null;
+   }
+
+   public RpcManager getRpcManager() {
+      return cache.getAdvancedCache().getRpcManager();
+   }
+
+   public int size() {
+      return cache.size();
+   }
+
+   public Map toMap() {
+      return cache;
+   }
+
+   public void removeListener(Object listener) {
+      cache.removeListener(listener);
+   }
+
+   public boolean containsKey(Object key) {
+      return cache.containsKey(key);
+   }
+
+   public Configuration getConfiguration() {
+      return cache.getConfiguration();
+   }
+
+}
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
index d9fdda2455..96783c5a4b 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
@@ -1,372 +1,115 @@
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
 package org.hibernate.cache.infinispan.util;
 
+import java.io.Externalizable;
+import java.io.IOException;
+import java.io.ObjectInput;
+import java.io.ObjectOutput;
 import java.util.Set;
 
-import org.hibernate.cache.CacheException;
-import org.infinispan.Cache;
-import org.infinispan.config.Configuration;
-import org.infinispan.context.Flag;
-import org.infinispan.util.concurrent.TimeoutException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Helper for dealing with Infinisan cache instances.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class CacheHelper {
 
    private static final Logger log = LoggerFactory.getLogger(CacheHelper.class);
 
    /**
     * Disallow external instantiation of CacheHelper.
     */
    private CacheHelper() {
    }
 
-   /**
-    * Is this cache participating in a cluster with invalidation?
-    * 
-    * @param cache
-    *           The cache to check.
-    * @return True if the cache is configured for synchronous/asynchronous invalidation; false
-    *         otherwise.
-    */
-   public static boolean isClusteredInvalidation(Cache cache) {
-      return isClusteredInvalidation(cache.getConfiguration().getCacheMode());
+   public static void initInternalEvict(CacheAdapter cacheAdapter, AddressAdapter member) {
+      EvictAll eKey = new EvictAll(member == null ? NoAddress.INSTANCE : member);
+      cacheAdapter.withFlags(FlagAdapter.CACHE_MODE_LOCAL).put(eKey, Internal.INIT);
    }
 
-   /**
-    * Does this cache mode indicate clustered invalidation?
-    * 
-    * @param cacheMode
-    *           The cache to check
-    * @return True if the cache mode is confiogured for synchronous/asynchronous invalidation; false
-    *         otherwise.
-    */
-   public static boolean isClusteredInvalidation(Configuration.CacheMode cacheMode) {
-      return cacheMode == Configuration.CacheMode.INVALIDATION_ASYNC
-               || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC;
+   public static void sendEvictAllNotification(CacheAdapter cacheAdapter, AddressAdapter member) {
+      EvictAll eKey = new EvictAll(member == null ? NoAddress.INSTANCE : member);
+      cacheAdapter.put(eKey, Internal.EVICT);
    }
 
-   /**
-    * Is this cache participating in a cluster with replication?
-    * 
-    * @param cache
-    *           The cache to check.
-    * @return True if the cache is configured for synchronous/asynchronous invalidation; false
-    *         otherwise.
-    */
-   public static boolean isClusteredReplication(Cache cache) {
-      return isClusteredReplication(cache.getConfiguration().getCacheMode());
+   public static boolean isEvictAllNotification(Object key) {
+      return key instanceof EvictAll;
    }
 
-   /**
-    * Does this cache mode indicate clustered replication?
-    * 
-    * @param cacheMode
-    *           The cache to check
-    * @return True if the cache mode is confiogured for synchronous/asynchronous invalidation; false
-    *         otherwise.
-    */
-   public static boolean isClusteredReplication(Configuration.CacheMode cacheMode) {
-      return cacheMode == Configuration.CacheMode.REPL_ASYNC || cacheMode == Configuration.CacheMode.REPL_SYNC;
+   public static boolean containsEvictAllNotification(Set keySet, AddressAdapter member) {
+      EvictAll eKey = new EvictAll(member == null ? NoAddress.INSTANCE : member);
+      return keySet.contains(eKey);
    }
 
-   public static boolean isSynchronous(Cache cache) {
-      return isSynchronous(cache.getConfiguration().getCacheMode());
+   public static boolean isEvictAllNotification(Object key, Object value) {
+      return key instanceof EvictAll && value == Internal.EVICT;
    }
 
-   public static boolean isSynchronous(Configuration.CacheMode cacheMode) {
-      return cacheMode == Configuration.CacheMode.REPL_SYNC || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC;
-   }
-
-   public static Set getKeySet(Cache cache) {
-      return cache.keySet();
-   }
+   private static class EvictAll implements Externalizable {
+      AddressAdapter member;
 
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>get(Fqn, Object)</code>, wrapping any exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    */
-   public static Object get(Cache cache, Object key) throws CacheException {
-      try {
-         return cache.get(key);
-      } catch (Exception e) {
-         throw new CacheException(e);
+      EvictAll(AddressAdapter member) {
+         this.member = member;
       }
-   }
 
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>get(Fqn, Object)</code>, wrapping any exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    */
-   public static Object getAllowingTimeout(Cache cache, Object key) throws CacheException {
-      try {
-         return cache.get(key);
-      } catch (TimeoutException ignored) {
-         // ignore it
-         return null;
-      } catch (Exception e) {
-         throw new CacheException(e);
+      @Override
+      public boolean equals(Object obj) {
+         if (obj == this)
+            return true;
+         if (!(obj instanceof EvictAll))
+            return false;
+         EvictAll ek = (EvictAll) obj;
+         return ek.member.equals(member);
       }
-   }
 
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>put(Object, Object)</code>, wrapping any exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    * @param value
-    *           data to store in the cache node
-    */
-   public static void put(Cache cache, Object key, Object value) throws CacheException {
-      put(cache, key, value, null);
-   }
-
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>put(Object, Object)</code>, wrapping any exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    * @param value
-    *           data to store in the cache node
-    * @param option
-    *           invocation Option to set for this invocation. May be <code>null</code>.
-    */
-   public static void put(Cache cache, Object key, Object value, Flag option) throws CacheException {
-      try {
-         cache.getAdvancedCache().put(key, value, option);
-      } catch (Exception e) {
-         throw new CacheException(e);
+      @Override
+      public int hashCode() {
+         int result = 17;
+         result = 31 * result + member.hashCode();
+         return result;
       }
-   }
 
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>put(Object, Object)</code>, ignoring any {@link TimeoutException} and wrapping any other
-    * exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    * @param value
-    *           data to store in the cache node
-    * @param option
-    *           invocation Option to set for this invocation. May be <code>null</code>.
-    */
-   public static void putAllowingTimeout(Cache cache, Object key, Object value, Flag... option) throws CacheException {
-      try {
-         cache.getAdvancedCache().put(key, value, option);
-      } catch (TimeoutException allowed) {
-         // ignore it
-      } catch (Exception e) {
-         throw new CacheException(e);
+      public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
+         member = (AddressAdapter) in.readObject();
       }
-   }
 
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>putForExternalRead(Object, Object)</code>, wrapping any exception in a
-    * {@link CacheException}. Ignores any JBoss Cache {@link TimeoutException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    * @param value
-    *           data to store in the cache node
-    */
-   public static boolean putForExternalRead(Cache cache, Object key, Object value) throws CacheException {
-      return putForExternalRead(cache, key, value, (Flag[])null);
-   }
-
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>putForExternalRead(Object, Object)</code>, wrapping any exception in a
-    * {@link CacheException}. Ignores any JBoss Cache {@link TimeoutException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    * @param value
-    *           data to store in the cache node
-    * @param option
-    *           invocation Option to set for this invocation. May be <code>null</code>.
-    */
-   public static boolean putForExternalRead(Cache cache, Object key, Object value, Flag... option) throws CacheException {
-      try {
-         cache.getAdvancedCache().putForExternalRead(key, value, option);
-         return true;
-      } catch (TimeoutException te) {
-         // ignore!
-         log.debug("ignoring write lock acquisition failure");
-         return false;
-      } catch (Throwable t) {
-         throw new CacheException(t);
-      }
-   }
-
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>removeNode(Fqn)</code>, wrapping any exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    */
-   public static void remove(Cache cache, Object key) throws CacheException {
-      remove(cache, key, null);
-   }
-
-   /**
-    * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
-    * <code>removeNode(Fqn)</code>, wrapping any exception in a {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param key
-    *           specific key to append to the <code>region</code> to form the full Fqn
-    * @param option
-    *           invocation Option to set for this invocation. May be <code>null</code>.
-    */
-   public static void remove(Cache cache, Object key, Flag option) throws CacheException {
-      try {
-         cache.getAdvancedCache().remove(key, option);
-      } catch (Exception e) {
-         throw new CacheException(e);
-      }
-   }
-
-   /**
-    * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any exception in a
-    * {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    */
-   public static void removeAll(Cache cache) throws CacheException {
-      try {
-         cache.clear();
-      } catch (Exception e) {
-         throw new CacheException(e);
+      public void writeExternal(ObjectOutput out) throws IOException {
+         out.writeObject(member);
       }
    }
 
-   /**
-    * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any exception in a
-    * {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param option
-    *           invocation Option to set for this invocation. May be <code>null</code>.
-    */
-   public static void removeAll(Cache cache, Flag option) throws CacheException {
-      try {
-         cache.getAdvancedCache().clear(option);
-      } catch (Exception e) {
-         throw new CacheException(e);
-      }
+   private enum NoAddress implements AddressAdapter {
+      INSTANCE;
    }
 
-   /**
-    * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any exception in a
-    * {@link CacheException}.
-    * 
-    * @param cache
-    *           the cache to invoke on
-    * @param region
-    *           base Fqn for the cache region
-    * @param option
-    *           invocation Option to set for this invocation. May be <code>null</code>.
-    */
-   public static void removeKey(Cache cache, Object key, Flag option) throws CacheException {
-      try {
-         cache.getAdvancedCache().remove(key, option);
-      } catch (Exception e) {
-         throw new CacheException(e);
-      }
-   }
-   
-   public static void removeKey(Cache cache, Object key) throws CacheException {
-      try {
-         cache.remove(key);
-      } catch (Exception e) {
-         throw new CacheException(e);
-      }
-   }
-   
-   public static boolean containsKey(Cache cache, Object key, Flag... flags) {
-      try {
-         return cache.getAdvancedCache().containsKey(key, flags);
-      } catch (Exception e) {
-         throw new CacheException(e);
-      }
+   private enum Internal { 
+      INIT, EVICT;
    }
 
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java
new file mode 100644
index 0000000000..5765a299ec
--- /dev/null
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/FlagAdapter.java
@@ -0,0 +1,59 @@
+/*
+ * JBoss, Home of Professional Open Source.
+ * Copyright 2009, Red Hat, Inc. and/or its affiliates, and
+ * individual contributors as indicated by the @author tags. See the
+ * copyright.txt file in the distribution for a full listing of
+ * individual contributors.
+ *
+ * This is free software; you can redistribute it and/or modify it
+ * under the terms of the GNU Lesser General Public License as
+ * published by the Free Software Foundation; either version 2.1 of
+ * the License, or (at your option) any later version.
+ *
+ * This software is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of
+ * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
+ * Lesser General Public License for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public
+ * License along with this software; if not, write to the Free
+ * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
+ * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
+ */
+package org.hibernate.cache.infinispan.util;
+
+import org.hibernate.cache.CacheException;
+import org.infinispan.context.Flag;
+
+/**
+ * FlagAdapter.
+ * 
+ * @author Galder Zamarreño
+ * @since 3.5
+ */
+public enum FlagAdapter {
+   ZERO_LOCK_ACQUISITION_TIMEOUT,
+   CACHE_MODE_LOCAL,
+   FORCE_ASYNCHRONOUS;
+   
+   Flag toFlag() {
+      switch(this) {
+         case ZERO_LOCK_ACQUISITION_TIMEOUT:
+            return Flag.ZERO_LOCK_ACQUISITION_TIMEOUT;
+         case CACHE_MODE_LOCAL:
+            return Flag.CACHE_MODE_LOCAL;
+         case FORCE_ASYNCHRONOUS:
+            return Flag.FORCE_ASYNCHRONOUS;
+         default:
+            throw new CacheException("Unmatched Infinispan flag " + this);
+      }
+   }
+   
+   static Flag[] toFlags(FlagAdapter[] adapters) {
+      Flag[] flags = new Flag[adapters.length];
+      for (int i = 0; i < adapters.length; i++) {
+         flags[i] = adapters[i].toFlag();
+      }
+      return flags;
+   }
+}
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index 2103b629b3..4d69d2887c 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,193 +1,194 @@
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
 
 import org.hibernate.cache.GeneralDataRegion;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
-import org.hibernate.cache.infinispan.util.CacheHelper;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
-import org.infinispan.Cache;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
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
 
    public AbstractGeneralDataRegionTestCase(String name) {
       super(name);
    }
 
    @Override
    protected void putInRegion(Region region, Object key, Object value) {
       ((GeneralDataRegion) region).put(key, value);
    }
 
    @Override
    protected void removeFromRegion(Region region, Object key) {
       ((GeneralDataRegion) region).evict(key);
    }
 
    /**
     * Test method for {@link QueryResultsRegion#evict(java.lang.Object)}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvict() throws Exception {
       evictOrRemoveTest();
    }
 
    private void evictOrRemoveTest() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-      Cache localCache = getInfinispanCache(regionFactory);
-      boolean invalidation = CacheHelper.isClusteredInvalidation(localCache);
+      CacheAdapter localCache = getInfinispanCache(regionFactory);
+      boolean invalidation = localCache.isClusteredInvalidation();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       cfg = createConfiguration();
       regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
 
       GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       assertNull("local is clean", localRegion.get(KEY));
       assertNull("remote is clean", remoteRegion.get(KEY));
 
       localRegion.put(KEY, VALUE1);
       assertEquals(VALUE1, localRegion.get(KEY));
 
       // allow async propagation
       sleep(250);
       Object expected = invalidation ? null : VALUE1;
       assertEquals(expected, remoteRegion.get(KEY));
 
       localRegion.evict(KEY);
 
       // allow async propagation
       sleep(250);
       assertEquals(null, localRegion.get(KEY));
       assertEquals(null, remoteRegion.get(KEY));
    }
 
    protected abstract String getStandardRegionName(String regionPrefix);
 
    /**
     * Test method for {@link QueryResultsRegion#evictAll()}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvictAll() throws Exception {
       evictOrRemoveAllTest("entity");
    }
 
    private void evictOrRemoveAllTest(String configName) throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-      Cache localCache = getInfinispanCache(regionFactory);
+      CacheAdapter localCache = getInfinispanCache(regionFactory);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       cfg = createConfiguration();
       regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-      Cache remoteCache = getInfinispanCache(regionFactory);
+      CacheAdapter remoteCache = getInfinispanCache(regionFactory);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
-      Set children = CacheHelper.getKeySet(localCache);
-      assertEquals("No children in " + children, 0, children.size());
+      Set keys = localCache.keySet();
+      assertEquals("No valid children in " + keys, 0, getValidKeyCount(keys));
 
-      children = CacheHelper.getKeySet(remoteCache);
-      assertEquals("No children in " + children, 0, children.size());
+      keys = remoteCache.keySet();
+      assertEquals("No valid children in " + keys, 0, getValidKeyCount(keys));
 
       assertNull("local is clean", localRegion.get(KEY));
       assertNull("remote is clean", remoteRegion.get(KEY));
 
       localRegion.put(KEY, VALUE1);
       assertEquals(VALUE1, localRegion.get(KEY));
 
       // Allow async propagation
       sleep(250);
 
       remoteRegion.put(KEY, VALUE1);
       assertEquals(VALUE1, remoteRegion.get(KEY));
 
       // Allow async propagation
       sleep(250);
 
       localRegion.evictAll();
 
       // allow async propagation
       sleep(250);
       // This should re-establish the region root node in the optimistic case
       assertNull(localRegion.get(KEY));
+      assertEquals("No valid children in " + keys, 0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       // This only adds a node in the case of optimistic locking
       assertEquals(null, remoteRegion.get(KEY));
+      assertEquals("No valid children in " + keys, 0, getValidKeyCount(remoteCache.keySet()));
 
       assertEquals("local is clean", null, localRegion.get(KEY));
       assertEquals("remote is clean", null, remoteRegion.get(KEY));
    }
 
    protected Configuration createConfiguration() {
       Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, false, true);
       return cfg;
    }
 
    protected void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
    }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
index 66a9166ddb..e559dcb59c 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractNonFunctionalTestCase.java
@@ -1,97 +1,111 @@
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
 
+import java.util.Set;
+
 import org.hibernate.cache.RegionFactory;
+import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.junit.UnitTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestSupport;
 import org.infinispan.Cache;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Base class for all non-functional tests of Infinispan integration.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractNonFunctionalTestCase extends UnitTestCase {
 
     public static final String REGION_PREFIX = "test";
     
     private CacheTestSupport testSupport;
     protected final Logger log = LoggerFactory.getLogger(getClass());
     
     public AbstractNonFunctionalTestCase(String name) {
         super(name);
         testSupport = new CacheTestSupport(log);
     }
 
     @Override
     protected void setUp() throws Exception {
         super.setUp();
         
         testSupport.setUp();
     }
 
     @Override
     protected void tearDown() throws Exception {
         super.tearDown();
         
         testSupport.tearDown();
     }
 
     protected void registerCache(Cache cache) {
         testSupport.registerCache(cache);
     }
 
     protected void unregisterCache(Cache cache) {
         testSupport.unregisterCache(cache);
     }
 
     protected void registerFactory(RegionFactory factory) {
         testSupport.registerFactory(factory);
     }
 
     protected void unregisterFactory(RegionFactory factory) {
         testSupport.unregisterFactory(factory);
     }
 
     protected CacheTestSupport getCacheTestSupport() {
         return testSupport;
     }
-    
+
     protected void sleep(long ms) {
         try {
             Thread.sleep(ms);
         }
         catch (InterruptedException e) {
             log.warn("Interrupted during sleep", e);
         }
     }
     
     protected void avoidConcurrentFlush() {
         testSupport.avoidConcurrentFlush();
     }
+
+    protected int getValidKeyCount(Set keys) {
+       int result = 0;
+       for (Object key : keys) {
+          if (!(CacheHelper.isEvictAllNotification(key))) {
+             result++;
+          }
+       }
+       return result;
+   }
+
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
index 1b78a7faa2..8e2bbe619f 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
@@ -1,59 +1,59 @@
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
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.util.ComparableComparator;
-import org.infinispan.Cache;
 
 /**
  * Base class for tests of Region implementations.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractRegionImplTestCase extends AbstractNonFunctionalTestCase {
 
    public AbstractRegionImplTestCase(String name) {
       super(name);
    }
 
-   protected abstract Cache getInfinispanCache(InfinispanRegionFactory regionFactory);
+   protected abstract CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory);
 
    protected abstract Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd);
 
    protected abstract void putInRegion(Region region, Object key, Object value);
 
    protected abstract void removeFromRegion(Region region, Object key);
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java
index 86695db63b..af66813bee 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/InfinispanRegionFactoryTestCase.java
@@ -1,411 +1,411 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.query.QueryResultsRegionImpl;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
-import org.infinispan.Cache;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.infinispan.config.Configuration;
 import org.infinispan.config.Configuration.CacheMode;
 import org.infinispan.eviction.EvictionStrategy;
 import org.infinispan.manager.CacheManager;
 import org.infinispan.manager.DefaultCacheManager;
 
 import junit.framework.TestCase;
 
 /**
  * InfinispanRegionFactoryTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class InfinispanRegionFactoryTestCase extends TestCase {
 
    public void testConfigurationProcessing() {
       final String person = "com.acme.Person";
       final String addresses = "com.acme.Person.addresses";
       Properties p = new Properties();
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.cfg", "person-cache");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.strategy", "LRU");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.wake_up_interval", "2000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.max_entries", "5000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.expiration.lifespan", "60000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.expiration.max_idle", "30000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.cfg", "person-addresses-cache");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.expiration.lifespan", "120000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.expiration.max_idle", "60000");
       p.setProperty("hibernate.cache.infinispan.query.cfg", "my-query-cache");
       p.setProperty("hibernate.cache.infinispan.query.eviction.strategy", "FIFO");
       p.setProperty("hibernate.cache.infinispan.query.eviction.wake_up_interval", "3000");
       p.setProperty("hibernate.cache.infinispan.query.eviction.max_entries", "10000");
 
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
 
       assertEquals("entity", factory.getTypeOverrides().get("entity").getCacheName());
       assertEquals("entity", factory.getTypeOverrides().get("collection").getCacheName());
       assertEquals("timestamps", factory.getTypeOverrides().get("timestamps").getCacheName());
 
       assertEquals("person-cache", factory.getTypeOverrides().get(person).getCacheName());
       assertEquals(EvictionStrategy.LRU, factory.getTypeOverrides().get(person).getEvictionStrategy());
       assertEquals(2000, factory.getTypeOverrides().get(person).getEvictionWakeUpInterval());
       assertEquals(5000, factory.getTypeOverrides().get(person).getEvictionMaxEntries());
       assertEquals(60000, factory.getTypeOverrides().get(person).getExpirationLifespan());
       assertEquals(30000, factory.getTypeOverrides().get(person).getExpirationMaxIdle());
 
       assertEquals("person-addresses-cache", factory.getTypeOverrides().get(addresses).getCacheName());
       assertEquals(120000, factory.getTypeOverrides().get(addresses).getExpirationLifespan());
       assertEquals(60000, factory.getTypeOverrides().get(addresses).getExpirationMaxIdle());
 
       assertEquals("my-query-cache", factory.getTypeOverrides().get("query").getCacheName());
       assertEquals(EvictionStrategy.FIFO, factory.getTypeOverrides().get("query").getEvictionStrategy());
       assertEquals(3000, factory.getTypeOverrides().get("query").getEvictionWakeUpInterval());
       assertEquals(10000, factory.getTypeOverrides().get("query").getEvictionMaxEntries());
    }
    
    public void testBuildEntityCollectionRegionsPersonPlusEntityCollectionOverrides() {
       final String person = "com.acme.Person";
       final String address = "com.acme.Address";
       final String car = "com.acme.Car";
       final String addresses = "com.acme.Person.addresses";
       final String parts = "com.acme.Car.parts";
       Properties p = new Properties();
       // First option, cache defined for entity and overrides for generic entity data type and entity itself.
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.cfg", "person-cache");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.strategy", "LRU");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.wake_up_interval", "2000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.max_entries", "5000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.expiration.lifespan", "60000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.expiration.max_idle", "30000");
       p.setProperty("hibernate.cache.infinispan.entity.cfg", "myentity-cache");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.strategy", "FIFO");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.wake_up_interval", "3000");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.max_entries", "20000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.cfg", "addresses-cache");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.eviction.strategy", "FIFO");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.eviction.wake_up_interval", "2500");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.eviction.max_entries", "5500");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.expiration.lifespan", "65000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.addresses.expiration.max_idle", "35000");
       p.setProperty("hibernate.cache.infinispan.collection.cfg", "mycollection-cache");
       p.setProperty("hibernate.cache.infinispan.collection.eviction.strategy", "LRU");
       p.setProperty("hibernate.cache.infinispan.collection.eviction.wake_up_interval", "3500");
       p.setProperty("hibernate.cache.infinispan.collection.eviction.max_entries", "25000");
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          assertNotNull(factory.getTypeOverrides().get(person));
          assertFalse(factory.getDefinedConfigurations().contains(person));
          assertNotNull(factory.getTypeOverrides().get(addresses));
          assertFalse(factory.getDefinedConfigurations().contains(addresses));
-         Cache cache = null;
+         CacheAdapter cache = null;
 
          EntityRegionImpl region = (EntityRegionImpl) factory.buildEntityRegion(person, p, null);
          assertNotNull(factory.getTypeOverrides().get(person));
          assertTrue(factory.getDefinedConfigurations().contains(person));
          assertNull(factory.getTypeOverrides().get(address));
-         cache = region.getCache();
+         cache = region.getCacheAdapter();
          Configuration cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.LRU, cacheCfg.getEvictionStrategy());
          assertEquals(2000, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(5000, cacheCfg.getEvictionMaxEntries());
          assertEquals(60000, cacheCfg.getExpirationLifespan());
          assertEquals(30000, cacheCfg.getExpirationMaxIdle());
 
          region = (EntityRegionImpl) factory.buildEntityRegion(address, p, null);
          assertNotNull(factory.getTypeOverrides().get(person));
          assertTrue(factory.getDefinedConfigurations().contains(person));
          assertNull(factory.getTypeOverrides().get(address));
-         cache = region.getCache();
+         cache = region.getCacheAdapter();
          cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.FIFO, cacheCfg.getEvictionStrategy());
          assertEquals(3000, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(20000, cacheCfg.getEvictionMaxEntries());
 
          region = (EntityRegionImpl) factory.buildEntityRegion(car, p, null);
          assertNotNull(factory.getTypeOverrides().get(person));
          assertTrue(factory.getDefinedConfigurations().contains(person));
          assertNull(factory.getTypeOverrides().get(address));
-         cache = region.getCache();
+         cache = region.getCacheAdapter();
          cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.FIFO, cacheCfg.getEvictionStrategy());
          assertEquals(3000, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(20000, cacheCfg.getEvictionMaxEntries());
 
          CollectionRegionImpl collectionRegion = (CollectionRegionImpl) factory.buildCollectionRegion(addresses, p, null);
          assertNotNull(factory.getTypeOverrides().get(addresses));
          assertTrue(factory.getDefinedConfigurations().contains(person));
          assertNull(factory.getTypeOverrides().get(parts));
-         cache = collectionRegion .getCache();
+         cache = collectionRegion .getCacheAdapter();
          cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.FIFO, cacheCfg.getEvictionStrategy());
          assertEquals(2500, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(5500, cacheCfg.getEvictionMaxEntries());
          assertEquals(65000, cacheCfg.getExpirationLifespan());
          assertEquals(35000, cacheCfg.getExpirationMaxIdle());
 
          collectionRegion = (CollectionRegionImpl) factory.buildCollectionRegion(parts, p, null);
          assertNotNull(factory.getTypeOverrides().get(addresses));
          assertTrue(factory.getDefinedConfigurations().contains(addresses));
          assertNull(factory.getTypeOverrides().get(parts));
-         cache = collectionRegion.getCache();
+         cache = collectionRegion.getCacheAdapter();
          cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.LRU, cacheCfg.getEvictionStrategy());
          assertEquals(3500, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(25000, cacheCfg.getEvictionMaxEntries());
 
          collectionRegion = (CollectionRegionImpl) factory.buildCollectionRegion(parts, p, null);
          assertNotNull(factory.getTypeOverrides().get(addresses));
          assertTrue(factory.getDefinedConfigurations().contains(addresses));
          assertNull(factory.getTypeOverrides().get(parts));
-         cache = collectionRegion.getCache();
+         cache = collectionRegion.getCacheAdapter();
          cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.LRU, cacheCfg.getEvictionStrategy());
          assertEquals(3500, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(25000, cacheCfg.getEvictionMaxEntries());
       } finally {
          factory.stop();
       }
    }
 
    public void testBuildEntityCollectionRegionOverridesOnly() {
-      Cache cache = null;
+      CacheAdapter cache = null;
       Properties p = new Properties();
       p.setProperty("hibernate.cache.infinispan.entity.eviction.strategy", "FIFO");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.wake_up_interval", "3000");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.max_entries", "30000");
       p.setProperty("hibernate.cache.infinispan.collection.eviction.strategy", "LRU");
       p.setProperty("hibernate.cache.infinispan.collection.eviction.wake_up_interval", "3500");
       p.setProperty("hibernate.cache.infinispan.collection.eviction.max_entries", "35000");
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          EntityRegionImpl region = (EntityRegionImpl) factory.buildEntityRegion("com.acme.Address", p, null);
          assertNull(factory.getTypeOverrides().get("com.acme.Address"));
-         cache = region.getCache();
+         cache = region.getCacheAdapter();
          Configuration cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.FIFO, cacheCfg.getEvictionStrategy());
          assertEquals(3000, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(30000, cacheCfg.getEvictionMaxEntries());
          assertEquals(100000, cacheCfg.getExpirationMaxIdle());
 
          CollectionRegionImpl collectionRegion = (CollectionRegionImpl) factory.buildCollectionRegion("com.acme.Person.addresses", p, null);
          assertNull(factory.getTypeOverrides().get("com.acme.Person.addresses"));
-         cache = collectionRegion.getCache();
+         cache = collectionRegion.getCacheAdapter();
          cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.LRU, cacheCfg.getEvictionStrategy());
          assertEquals(3500, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(35000, cacheCfg.getEvictionMaxEntries());
          assertEquals(100000, cacheCfg.getExpirationMaxIdle());
       } finally {
          factory.stop();
       }
    }
 
    public void testBuildEntityRegionPersonPlusEntityOverridesWithoutCfg() {
       final String person = "com.acme.Person";
       Properties p = new Properties();
       // Third option, no cache defined for entity and overrides for generic entity data type and entity itself.
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.eviction.strategy", "LRU");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.expiration.lifespan", "60000");
       p.setProperty("hibernate.cache.infinispan.com.acme.Person.expiration.max_idle", "30000");
       p.setProperty("hibernate.cache.infinispan.entity.cfg", "myentity-cache");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.strategy", "FIFO");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.wake_up_interval", "3000");
       p.setProperty("hibernate.cache.infinispan.entity.eviction.max_entries", "10000");
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          assertNotNull(factory.getTypeOverrides().get(person));
          assertFalse(factory.getDefinedConfigurations().contains(person));
          EntityRegionImpl region = (EntityRegionImpl) factory.buildEntityRegion(person, p, null);
          assertNotNull(factory.getTypeOverrides().get(person));
          assertTrue(factory.getDefinedConfigurations().contains(person));
-         Cache cache = region.getCache();
+         CacheAdapter cache = region.getCacheAdapter();
          Configuration cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.LRU, cacheCfg.getEvictionStrategy());
          assertEquals(3000, cacheCfg.getEvictionWakeUpInterval());
          assertEquals(10000, cacheCfg.getEvictionMaxEntries());
          assertEquals(60000, cacheCfg.getExpirationLifespan());
          assertEquals(30000, cacheCfg.getExpirationMaxIdle());
       } finally {
          factory.stop();
       }
    }
 
    public void testTimestampValidation() {
       Properties p = new Properties();
       final DefaultCacheManager manager = new DefaultCacheManager();
       InfinispanRegionFactory factory = new InfinispanRegionFactory() {
          @Override
          protected CacheManager createCacheManager(Properties properties) throws CacheException {
             return manager;
          }
       };
       Configuration config = new Configuration();
       config.setCacheMode(CacheMode.INVALIDATION_SYNC);
       manager.defineConfiguration("timestamps", config);
       try {
          factory.start(null, p);
          fail("Should have failed saying that invalidation is not allowed for timestamp caches.");
       } catch(CacheException ce) {
       }
    }
    
    public void testBuildDefaultTimestampsRegion() {
       final String timestamps = "org.hibernate.cache.UpdateTimestampsCache";
       Properties p = new Properties();
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          assertTrue(factory.getDefinedConfigurations().contains("timestamps"));
          assertTrue(factory.getTypeOverrides().get("timestamps").getCacheName().equals("timestamps"));
          Configuration config = new Configuration();
          config.setFetchInMemoryState(false);
          manager.defineConfiguration("timestamps", config);
          TimestampsRegionImpl region = (TimestampsRegionImpl) factory.buildTimestampsRegion(timestamps, p);
-         Cache cache = region.getCache();
+         CacheAdapter cache = region.getCacheAdapter();
          Configuration cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.NONE, cacheCfg.getEvictionStrategy());
          assertEquals(CacheMode.REPL_ASYNC, cacheCfg.getCacheMode());
          assertTrue(cacheCfg.isUseLazyDeserialization());
       } finally {
          factory.stop();
       }
    }
    
    public void testBuildDiffCacheNameTimestampsRegion() {
       final String timestamps = "org.hibernate.cache.UpdateTimestampsCache";
       Properties p = new Properties();
       p.setProperty("hibernate.cache.infinispan.timestamps.cfg", "unrecommended-timestamps");
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          assertFalse(factory.getDefinedConfigurations().contains("timestamp"));
          assertTrue(factory.getDefinedConfigurations().contains("unrecommended-timestamps"));
          assertTrue(factory.getTypeOverrides().get("timestamps").getCacheName().equals("unrecommended-timestamps"));
          Configuration config = new Configuration();
          config.setFetchInMemoryState(false);
          config.setCacheMode(CacheMode.REPL_SYNC);
          manager.defineConfiguration("unrecommended-timestamps", config);
          TimestampsRegionImpl region = (TimestampsRegionImpl) factory.buildTimestampsRegion(timestamps, p);
-         Cache cache = region.getCache();
+         CacheAdapter cache = region.getCacheAdapter();
          Configuration cacheCfg = cache.getConfiguration();
          assertEquals(EvictionStrategy.NONE, cacheCfg.getEvictionStrategy());
          assertEquals(CacheMode.REPL_SYNC, cacheCfg.getCacheMode());
          assertFalse(cacheCfg.isUseLazyDeserialization());
       } finally {
          factory.stop();
       }
    }
    
    public void testBuildTimestamRegionWithCacheNameOverride() {
       final String timestamps = "org.hibernate.cache.UpdateTimestampsCache";
       Properties p = new Properties();
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       p.setProperty("hibernate.cache.infinispan.timestamps.cfg", "mytimestamps-cache");
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          factory.buildTimestampsRegion(timestamps, p);
          assertTrue(factory.getDefinedConfigurations().contains("mytimestamps-cache"));
       } finally {
          factory.stop();
       }
    }
    
    public void testBuildTimestamRegionWithFifoEvictionOverride() {
       final String timestamps = "org.hibernate.cache.UpdateTimestampsCache";
       Properties p = new Properties();
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       p.setProperty("hibernate.cache.infinispan.timestamps.cfg", "mytimestamps-cache");
       p.setProperty("hibernate.cache.infinispan.timestamps.eviction.strategy", "FIFO");
       p.setProperty("hibernate.cache.infinispan.timestamps.eviction.wake_up_interval", "3000");
       p.setProperty("hibernate.cache.infinispan.timestamps.eviction.max_entries", "10000");
       try {
          factory.start(null, p);
          CacheManager manager = factory.getCacheManager();
          manager.getGlobalConfiguration().setTransportClass(null);
          factory.buildTimestampsRegion(timestamps, p);
          assertTrue(factory.getDefinedConfigurations().contains("mytimestamps-cache"));
          fail("Should fail cos no eviction configurations are allowed for timestamp caches");
       } catch(CacheException ce) {
       } finally {
          factory.stop();
       }
    }
    
    public void testBuildTimestamRegionWithNoneEvictionOverride() {
       final String timestamps = "org.hibernate.cache.UpdateTimestampsCache";
       Properties p = new Properties();
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       p.setProperty("hibernate.cache.infinispan.timestamps.cfg", "timestamps-none-eviction");
       p.setProperty("hibernate.cache.infinispan.timestamps.eviction.strategy", "NONE");
       p.setProperty("hibernate.cache.infinispan.timestamps.eviction.wake_up_interval", "3000");
       p.setProperty("hibernate.cache.infinispan.timestamps.eviction.max_entries", "10000");
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          factory.buildTimestampsRegion(timestamps, p);
          assertTrue(factory.getDefinedConfigurations().contains("timestamps-none-eviction"));
       } finally {
          factory.stop();
       }
    }
    
    public void testBuildQueryRegion() {
       final String query = "org.hibernate.cache.StandardQueryCache";
       Properties p = new Properties();
       InfinispanRegionFactory factory = new InfinispanRegionFactory();
       factory.start(null, p);
       CacheManager manager = factory.getCacheManager();
       manager.getGlobalConfiguration().setTransportClass(null);
       try {
          assertTrue(factory.getDefinedConfigurations().contains("local-query"));
          QueryResultsRegionImpl region = (QueryResultsRegionImpl) factory.buildQueryResultsRegion(query, p);
-         Cache cache = region.getCache();
+         CacheAdapter cache = region.getCacheAdapter();
          Configuration cacheCfg = cache.getConfiguration();
          assertEquals(CacheMode.LOCAL, cacheCfg.getCacheMode());
       } finally {
          factory.stop();
       }
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
index 51557fa811..444746d737 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
@@ -1,512 +1,511 @@
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
 package org.hibernate.test.cache.infinispan.collection;
 
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import junit.extensions.TestSetup;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestSuite;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
-import org.hibernate.cache.infinispan.util.CacheHelper;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.util.ComparableComparator;
-import org.infinispan.Cache;
-import org.infinispan.context.Flag;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 /**
  * Base class for tests of CollectionRegionAccessStrategy impls.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractCollectionRegionAccessStrategyTestCase extends AbstractNonFunctionalTestCase {
 
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY_BASE = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
 
    protected static int testCount;
 
    protected static Configuration localCfg;
    protected static InfinispanRegionFactory localRegionFactory;
-   protected Cache localCache;
+   protected CacheAdapter localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
-   protected Cache remoteCache;
+   protected CacheAdapter remoteCache;
 
    protected CollectionRegion localCollectionRegion;
    protected CollectionRegionAccessStrategy localAccessStrategy;
 
    protected CollectionRegion remoteCollectionRegion;
    protected CollectionRegionAccessStrategy remoteAccessStrategy;
 
    protected boolean invalidation;
    protected boolean synchronous;
 
    protected Exception node1Exception;
    protected Exception node2Exception;
 
    protected AssertionFailedError node1Failure;
    protected AssertionFailedError node2Failure;
 
    public static Test getTestSetup(Class testClass, String configName) {
       TestSuite suite = new TestSuite(testClass);
       return new AccessStrategyTestSetup(suite, configName);
    }
 
    public static Test getTestSetup(Test test, String configName) {
       return new AccessStrategyTestSetup(test, configName);
    }
 
    /**
     * Create a new TransactionalAccessTestCase.
     * 
     * @param name
     */
    public AbstractCollectionRegionAccessStrategyTestCase(String name) {
       super(name);
    }
 
    protected abstract AccessType getAccessType();
 
    protected void setUp() throws Exception {
       super.setUp();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       localCollectionRegion = localRegionFactory.buildCollectionRegion(REGION_NAME, localCfg.getProperties(),
                getCacheDataDescription());
-      localCache = ((BaseRegion) localCollectionRegion).getCache();
+      localCache = ((BaseRegion) localCollectionRegion).getCacheAdapter();
       localAccessStrategy = localCollectionRegion.buildAccessStrategy(getAccessType());
-      invalidation = CacheHelper.isClusteredInvalidation(localCache);
-      synchronous = CacheHelper.isSynchronous(localCache);
+      invalidation = localCache.isClusteredInvalidation();
+      synchronous = localCache.isSynchronous();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteCollectionRegion = remoteRegionFactory.buildCollectionRegion(REGION_NAME, remoteCfg.getProperties(),
                getCacheDataDescription());
-      remoteCache = ((BaseRegion) remoteCollectionRegion).getCache();
+      remoteCache = ((BaseRegion) remoteCollectionRegion).getCacheAdapter();
       remoteAccessStrategy = remoteCollectionRegion.buildAccessStrategy(getAccessType());
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected void tearDown() throws Exception {
 
       super.tearDown();
 
       if (localCollectionRegion != null)
          localCollectionRegion.destroy();
       if (remoteCollectionRegion != null)
          remoteCollectionRegion.destroy();
 
       try {
-         localCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
+         localCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
-         remoteCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
+         remoteCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging remote cache", e);
       }
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected static Configuration createConfiguration(String configName, String configResource) {
       Configuration cfg = CacheTestUtil.buildConfiguration(REGION_PREFIX, InfinispanRegionFactory.class, true, false);
       cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, configName);
       return cfg;
    }
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
    protected boolean isUsingInvalidation() {
       return invalidation;
    }
 
    protected boolean isSynchronous() {
       return synchronous;
    }
 
    /**
     * This is just a setup test where we assert that the cache config is as we expected.
     */
    public abstract void testCacheConfiguration();
 
    /**
     * Test method for {@link TransactionalAccess#getRegion()}.
     */
    public void testGetRegion() {
       assertEquals("Correct region", localCollectionRegion, localAccessStrategy.getRegion());
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)}
     * .
     */
    public void testPutFromLoad() throws Exception {
       putFromLoadTest(false);
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)}
     * .
     */
    public void testPutFromLoadMinimal() throws Exception {
       putFromLoadTest(true);
    }
 
    /**
     * Simulate 2 nodes, both start, tx do a get, experience a cache miss, then 'read from db.' First
     * does a putFromLoad, then an evict (to represent a change). Second tries to do a putFromLoad
     * with stale data (i.e. it took longer to read from the db). Both commit their tx. Then both
     * start a new tx and get. First should see the updated data; second should either see the
     * updated data (isInvalidation()( == false) or null (isInvalidation() == true).
     * 
     * @param useMinimalAPI
     * @throws Exception
     */
    private void putFromLoadTest(final boolean useMinimalAPI) throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       final CountDownLatch writeLatch1 = new CountDownLatch(1);
       final CountDownLatch writeLatch2 = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread node1 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertEquals("node1 starts clean", null, localAccessStrategy.get(KEY, txTimestamp));
 
                writeLatch1.await();
 
                if (useMinimalAPI) {
                   localAccessStrategy.putFromLoad(KEY, VALUE2, txTimestamp, new Integer(2), true);
                } else {
                   localAccessStrategy.putFromLoad(KEY, VALUE2, txTimestamp, new Integer(2));
                }
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                // Let node2 write
                writeLatch2.countDown();
                completionLatch.countDown();
             }
          }
       };
 
       Thread node2 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("node2 starts clean", remoteAccessStrategy.get(KEY, txTimestamp));
 
                // Let node1 write
                writeLatch1.countDown();
                // Wait for node1 to finish
                writeLatch2.await();
 
                // Let the first PFER propagate
                sleep(200);
 
                if (useMinimalAPI) {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1), true);
                } else {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
                }
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node2 caught exception", e);
                node2Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node2Failure = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       node1.setDaemon(true);
       node2.setDaemon(true);
 
       node1.start();
       node2.start();
 
       assertTrue("Threads completed", completionLatch.await(2, TimeUnit.SECONDS));
 
       if (node1Failure != null)
          throw node1Failure;
       if (node2Failure != null)
          throw node2Failure;
 
       assertEquals("node1 saw no exceptions", null, node1Exception);
       assertEquals("node2 saw no exceptions", null, node2Exception);
 
       // let the final PFER propagate
       sleep(100);
 
       long txTimestamp = System.currentTimeMillis();
       String msg1 = "Correct node1 value";
       String msg2 = "Correct node2 value";
       Object expected1 = null;
       Object expected2 = null;
       if (isUsingInvalidation()) {
          // PFER does not generate any invalidation, so each node should
          // succeed. We count on database locking and Hibernate removing
          // the collection on any update to prevent the situation we have
          // here where the caches have inconsistent data
          expected1 = VALUE2;
          expected2 = VALUE1;
       } else {
          // the initial VALUE2 should prevent the node2 put
          expected1 = VALUE2;
          expected2 = VALUE2;
       }
 
       assertEquals(msg1, expected1, localAccessStrategy.get(KEY, txTimestamp));
       assertEquals(msg2, expected2, remoteAccessStrategy.get(KEY, txTimestamp));
    }
 
    /**
     * Test method for {@link TransactionalAccess#remove(java.lang.Object)}.
     */
    public void testRemove() {
       evictOrRemoveTest(false);
    }
 
    /**
     * Test method for {@link TransactionalAccess#removeAll()}.
     */
    public void testRemoveAll() {
       evictOrRemoveAllTest(false);
    }
 
    /**
     * Test method for {@link TransactionalAccess#evict(java.lang.Object)}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvict() {
       evictOrRemoveTest(true);
    }
 
    /**
     * Test method for {@link TransactionalAccess#evictAll()}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * CollectionRegionAccessStrategy API.
     */
    public void testEvictAll() {
       evictOrRemoveAllTest(true);
    }
 
    private void evictOrRemoveTest(boolean evict) {
 
       final String KEY = KEY_BASE + testCount++;
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       if (evict)
          localAccessStrategy.evict(KEY);
       else
          localAccessStrategy.remove(KEY);
 
       assertEquals(null, localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
    }
 
    private void evictOrRemoveAllTest(boolean evict) {
 
       final String KEY = KEY_BASE + testCount++;
 
-      assertEquals(0, localCache.keySet().size());
+      assertEquals(0, getValidKeyCount(localCache.keySet()));
 
-      assertEquals(0, remoteCache.keySet().size());
+      assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       if (evict)
          localAccessStrategy.evictAll();
       else
          localAccessStrategy.removeAll();
 
       // This should re-establish the region root node
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      assertEquals(0, localCache.keySet().size());
+      assertEquals(0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      assertEquals(0, remoteCache.keySet().size());
+      assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      assertEquals(1, remoteCache.keySet().size());
+      assertEquals(1, getValidKeyCount(remoteCache.keySet()));
 
       // Wait for async propagation of the putFromLoad
       sleep(250);
 
       assertEquals("local is correct", (isUsingInvalidation() ? null : VALUE1), localAccessStrategy.get(KEY, System
                .currentTimeMillis()));
       assertEquals("remote is correct", VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
    }
 
    private void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
 
    }
 
    private static class AccessStrategyTestSetup extends TestSetup {
 
       private static final String PREFER_IPV4STACK = "java.net.preferIPv4Stack";
 
       private final String configResource;
       private final String configName;
       private String preferIPv4Stack;
 
       public AccessStrategyTestSetup(Test test, String configName) {
          this(test, configName, null);
       }
 
       public AccessStrategyTestSetup(Test test, String configName, String configResource) {
          super(test);
          this.configName = configName;
          this.configResource = configResource;
       }
 
       @Override
       protected void setUp() throws Exception {
          super.setUp();
 
          // Try to ensure we use IPv4; otherwise cluster formation is very slow
          preferIPv4Stack = System.getProperty(PREFER_IPV4STACK);
          System.setProperty(PREFER_IPV4STACK, "true");
 
          localCfg = createConfiguration(configName, configResource);
          localRegionFactory = CacheTestUtil.startRegionFactory(localCfg);
 
          remoteCfg = createConfiguration(configName, configResource);
          remoteRegionFactory = CacheTestUtil.startRegionFactory(remoteCfg);
       }
 
       @Override
       protected void tearDown() throws Exception {
          try {
             super.tearDown();
          } finally {
             if (preferIPv4Stack == null)
                System.clearProperty(PREFER_IPV4STACK);
             else
                System.setProperty(PREFER_IPV4STACK, preferIPv4Stack);
          }
 
          if (localRegionFactory != null)
             localRegionFactory.stop();
 
          if (remoteRegionFactory != null)
             remoteRegionFactory.stop();
       }
 
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
index 8cde37e781..d3071caf0c 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
@@ -1,694 +1,688 @@
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
 package org.hibernate.test.cache.infinispan.entity;
 
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import junit.extensions.TestSetup;
 import junit.framework.AssertionFailedError;
 import junit.framework.Test;
 import junit.framework.TestSuite;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.impl.CacheDataDescriptionImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
-import org.hibernate.cache.infinispan.util.CacheHelper;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.util.ComparableComparator;
-import org.infinispan.Cache;
-import org.infinispan.context.Flag;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 /**
  * Base class for tests of EntityRegionAccessStrategy impls.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractEntityRegionAccessStrategyTestCase extends AbstractNonFunctionalTestCase {
 
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY_BASE = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
 
    protected static int testCount;
 
    protected static Configuration localCfg;
    protected static InfinispanRegionFactory localRegionFactory;
-   protected Cache localCache;
+   protected CacheAdapter localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
-   protected Cache remoteCache;
+   protected CacheAdapter remoteCache;
 
    protected boolean invalidation;
    protected boolean synchronous;
 
    protected EntityRegion localEntityRegion;
    protected EntityRegionAccessStrategy localAccessStrategy;
 
    protected EntityRegion remoteEntityRegion;
    protected EntityRegionAccessStrategy remoteAccessStrategy;
 
    protected Exception node1Exception;
    protected Exception node2Exception;
 
    protected AssertionFailedError node1Failure;
    protected AssertionFailedError node2Failure;
 
    public static Test getTestSetup(Class testClass, String configName) {
       TestSuite suite = new TestSuite(testClass);
       return new AccessStrategyTestSetup(suite, configName);
    }
 
    public static Test getTestSetup(Test test, String configName) {
       return new AccessStrategyTestSetup(test, configName);
    }
 
    /**
     * Create a new TransactionalAccessTestCase.
     * 
     * @param name
     */
    public AbstractEntityRegionAccessStrategyTestCase(String name) {
       super(name);
    }
 
    protected abstract AccessType getAccessType();
 
    protected void setUp() throws Exception {
       super.setUp();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       localEntityRegion = localRegionFactory.buildEntityRegion(REGION_NAME, localCfg
                .getProperties(), getCacheDataDescription());
       localAccessStrategy = localEntityRegion.buildAccessStrategy(getAccessType());
 
-      localCache = ((BaseRegion) localEntityRegion).getCache();
+      localCache = ((BaseRegion) localEntityRegion).getCacheAdapter();
 
-      invalidation = CacheHelper.isClusteredInvalidation(localCache);
-      synchronous = CacheHelper.isSynchronous(localCache);
+      invalidation = localCache.isClusteredInvalidation();
+      synchronous = localCache.isSynchronous();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteEntityRegion = remoteRegionFactory.buildEntityRegion(REGION_NAME, remoteCfg
                .getProperties(), getCacheDataDescription());
       remoteAccessStrategy = remoteEntityRegion.buildAccessStrategy(getAccessType());
 
-      remoteCache = ((BaseRegion) remoteEntityRegion).getCache();
+      remoteCache = ((BaseRegion) remoteEntityRegion).getCacheAdapter();
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected void tearDown() throws Exception {
 
       super.tearDown();
 
       if (localEntityRegion != null)
          localEntityRegion.destroy();
       if (remoteEntityRegion != null)
          remoteEntityRegion.destroy();
 
       try {
-         localCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
+         localCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
-         remoteCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
+         remoteCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging remote cache", e);
       }
 
       node1Exception = null;
       node2Exception = null;
 
       node1Failure = null;
       node2Failure = null;
    }
 
    protected static Configuration createConfiguration(String configName) {
       Configuration cfg = CacheTestUtil.buildConfiguration(REGION_PREFIX, InfinispanRegionFactory.class, true, false);
       cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, configName);
       return cfg;
    }
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
    protected boolean isUsingInvalidation() {
       return invalidation;
    }
 
    protected boolean isSynchronous() {
       return synchronous;
    }
 
    protected void assertThreadsRanCleanly() {
       if (node1Failure != null)
          throw node1Failure;
       if (node2Failure != null)
          throw node2Failure;
 
       if (node1Exception != null) {
          log.error("node1 saw an exception", node1Exception);
          assertEquals("node1 saw no exceptions", null, node1Exception);
       }
 
       if (node2Exception != null) {
          log.error("node2 saw an exception", node2Exception);
          assertEquals("node2 saw no exceptions", null, node2Exception);
       }
    }
 
    /**
     * This is just a setup test where we assert that the cache config is as we expected.
     */
    public abstract void testCacheConfiguration();
 
    /**
     * Test method for {@link TransactionalAccess#getRegion()}.
     */
    public void testGetRegion() {
       assertEquals("Correct region", localEntityRegion, localAccessStrategy.getRegion());
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)}
     * .
     */
    public void testPutFromLoad() throws Exception {
       putFromLoadTest(false);
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)}
     * .
     */
    public void testPutFromLoadMinimal() throws Exception {
       putFromLoadTest(true);
    }
 
    /**
     * Simulate 2 nodes, both start, tx do a get, experience a cache miss, then 'read from db.' First
     * does a putFromLoad, then an update. Second tries to do a putFromLoad with stale data (i.e. it
     * took longer to read from the db). Both commit their tx. Then both start a new tx and get.
     * First should see the updated data; second should either see the updated data (isInvalidation()
     * == false) or null (isInvalidation() == true).
     * 
     * @param useMinimalAPI
     * @throws Exception
     */
    private void putFromLoadTest(final boolean useMinimalAPI) throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       final CountDownLatch writeLatch1 = new CountDownLatch(1);
       final CountDownLatch writeLatch2 = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread node1 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("node1 starts clean", localAccessStrategy.get(KEY, txTimestamp));
 
                writeLatch1.await();
 
                if (useMinimalAPI) {
                   localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1), true);
                } else {
                   localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
                }
 
                localAccessStrategy.update(KEY, VALUE2, new Integer(2), new Integer(1));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                // Let node2 write
                writeLatch2.countDown();
                completionLatch.countDown();
             }
          }
       };
 
       Thread node2 = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("node1 starts clean", remoteAccessStrategy.get(KEY, txTimestamp));
 
                // Let node1 write
                writeLatch1.countDown();
                // Wait for node1 to finish
                writeLatch2.await();
 
                if (useMinimalAPI) {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1), true);
                } else {
                   remoteAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
                }
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node2 caught exception", e);
                node2Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node2Failure = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       node1.setDaemon(true);
       node2.setDaemon(true);
 
       node1.start();
       node2.start();
 
       assertTrue("Threads completed", completionLatch.await(2, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE2, localAccessStrategy.get(KEY, txTimestamp));
 
       if (isUsingInvalidation()) {
          // no data version to prevent the PFER; we count on db locks preventing this
          assertEquals("Expected node2 value", VALUE1, remoteAccessStrategy.get(KEY, txTimestamp));
       } else {
          // The node1 update is replicated, preventing the node2 PFER
          assertEquals("Correct node2 value", VALUE2, remoteAccessStrategy.get(KEY, txTimestamp));
       }
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#insert(java.lang.Object, java.lang.Object, java.lang.Object)}.
     */
    public void testInsert() throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       final CountDownLatch readLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread inserter = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertNull("Correct initial value", localAccessStrategy.get(KEY, txTimestamp));
 
                localAccessStrategy.insert(KEY, VALUE1, new Integer(1));
 
                readLatch.countDown();
                commitLatch.await();
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       Thread reader = new Thread() {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                readLatch.await();
 //               Object expected = !isBlockingReads() ? null : VALUE1;
                Object expected = null;
 
                assertEquals("Correct initial value", expected, localAccessStrategy.get(KEY,
                         txTimestamp));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                commitLatch.countDown();
                completionLatch.countDown();
             }
          }
       };
 
       inserter.setDaemon(true);
       reader.setDaemon(true);
       inserter.start();
       reader.start();
 
       assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE1, localAccessStrategy.get(KEY, txTimestamp));
       Object expected = isUsingInvalidation() ? null : VALUE1;
       assertEquals("Correct node2 value", expected, remoteAccessStrategy.get(KEY, txTimestamp));
    }
 
    /**
     * Test method for
     * {@link TransactionalAccess#update(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)}
     * .
     */
    public void testUpdate() throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
       // Set up initial state
       localAccessStrategy.get(KEY, System.currentTimeMillis());
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       remoteAccessStrategy.get(KEY, System.currentTimeMillis());
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
 
       // Let the async put propagate
       sleep(250);
 
       final CountDownLatch readLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread updater = new Thread("testUpdate-updater") {
 
          public void run() {
             boolean readerUnlocked = false;
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Transaction began, get initial value");
                assertEquals("Correct initial value", VALUE1, localAccessStrategy.get(KEY, txTimestamp));
                log.debug("Now update value");
                localAccessStrategy.update(KEY, VALUE2, new Integer(2), new Integer(1));
                log.debug("Notify the read latch");
                readLatch.countDown();
                readerUnlocked = true;
                log.debug("Await commit");
                commitLatch.await();
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                if (!readerUnlocked) readLatch.countDown();
                log.debug("Completion latch countdown");
                completionLatch.countDown();
             }
          }
       };
 
       Thread reader = new Thread("testUpdate-reader") {
 
          public void run() {
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Transaction began, await read latch");
                readLatch.await();
                log.debug("Read latch acquired, verify local access strategy");
 
                // This won't block w/ mvc and will read the old value
                Object expected = VALUE1;
                assertEquals("Correct value", expected, localAccessStrategy.get(KEY, txTimestamp));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                commitLatch.countDown();
                log.debug("Completion latch countdown");
                completionLatch.countDown();
             }
          }
       };
 
       updater.setDaemon(true);
       reader.setDaemon(true);
       updater.start();
       reader.start();
 
       // Should complete promptly
       assertTrue(completionLatch.await(2, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE2, localAccessStrategy.get(KEY, txTimestamp));
       Object expected = isUsingInvalidation() ? null : VALUE2;
       assertEquals("Correct node2 value", expected, remoteAccessStrategy.get(KEY, txTimestamp));
    }
 
    /**
     * Test method for {@link TransactionalAccess#remove(java.lang.Object)}.
     */
    public void testRemove() {
       evictOrRemoveTest(false);
    }
 
    /**
     * Test method for {@link TransactionalAccess#removeAll()}.
     */
    public void testRemoveAll() {
       evictOrRemoveAllTest(false);
    }
 
    /**
     * Test method for {@link TransactionalAccess#evict(java.lang.Object)}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * EntityRegionAccessStrategy API.
     */
    public void testEvict() {
       evictOrRemoveTest(true);
    }
 
    /**
     * Test method for {@link TransactionalAccess#evictAll()}.
     * 
     * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
     * EntityRegionAccessStrategy API.
     */
    public void testEvictAll() {
       evictOrRemoveAllTest(true);
    }
 
    private void evictOrRemoveTest(boolean evict) {
-
       final String KEY = KEY_BASE + testCount++;
+      assertEquals(0, getValidKeyCount(localCache.keySet()));
+      assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      // Wait for async propagation
-      sleep(250);
-
       if (evict)
          localAccessStrategy.evict(KEY);
       else
          localAccessStrategy.remove(KEY);
 
       assertEquals(null, localAccessStrategy.get(KEY, System.currentTimeMillis()));
-
+      assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
+      assertEquals(0, getValidKeyCount(remoteCache.keySet()));
    }
 
    private void evictOrRemoveAllTest(boolean evict) {
-
       final String KEY = KEY_BASE + testCount++;
-
-      assertEquals(0, localCache.keySet().size());
-      assertEquals(0, remoteCache.keySet().size());
-
+      assertEquals(0, getValidKeyCount(localCache.keySet()));
+      assertEquals(0, getValidKeyCount(remoteCache.keySet()));
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       // Wait for async propagation
       sleep(250);
 
-      if (evict)
+      if (evict) {
+         log.debug("Call evict all locally");
          localAccessStrategy.evictAll();
-      else
+      } else {
          localAccessStrategy.removeAll();
+      }
 
       // This should re-establish the region root node in the optimistic case
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
-
-      assertEquals(0, localCache.keySet().size());
+      assertEquals(0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
-
-      assertEquals(0, remoteCache.keySet().size());
+      assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
-
-      assertEquals(1, remoteCache.keySet().size());
+      assertEquals(1, getValidKeyCount(remoteCache.keySet()));
 
       // Wait for async propagation
       sleep(250);
 
       assertEquals("local is correct", (isUsingInvalidation() ? null : VALUE1), localAccessStrategy
                .get(KEY, System.currentTimeMillis()));
       assertEquals("remote is correct", VALUE1, remoteAccessStrategy.get(KEY, System
                .currentTimeMillis()));
    }
 
    protected void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
    }
 
    private static class AccessStrategyTestSetup extends TestSetup {
 
       private static final String PREFER_IPV4STACK = "java.net.preferIPv4Stack";
       private final String configName;
       private String preferIPv4Stack;
 
       public AccessStrategyTestSetup(Test test, String configName) {
          super(test);
          this.configName = configName;
       }
 
       @Override
       protected void setUp() throws Exception {
          try {
             super.tearDown();
          } finally {
             if (preferIPv4Stack == null)
                System.clearProperty(PREFER_IPV4STACK);
             else
                System.setProperty(PREFER_IPV4STACK, preferIPv4Stack);
          }
 
          // Try to ensure we use IPv4; otherwise cluster formation is very slow
          preferIPv4Stack = System.getProperty(PREFER_IPV4STACK);
          System.setProperty(PREFER_IPV4STACK, "true");
 
          localCfg = createConfiguration(configName);
          localRegionFactory = CacheTestUtil.startRegionFactory(localCfg);
 
          remoteCfg = createConfiguration(configName);
          remoteRegionFactory = CacheTestUtil.startRegionFactory(remoteCfg);
       }
 
       @Override
       protected void tearDown() throws Exception {
          super.tearDown();
 
          if (localRegionFactory != null)
             localRegionFactory.stop();
 
          if (remoteRegionFactory != null)
             remoteRegionFactory.stop();
       }
 
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
index e91e5963ba..60849d61df 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
@@ -1,101 +1,102 @@
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
 package org.hibernate.test.cache.infinispan.entity;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.test.cache.infinispan.AbstractEntityCollectionRegionTestCase;
-import org.infinispan.Cache;
 
 /**
  * Tests of EntityRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class EntityRegionImplTestCase extends AbstractEntityCollectionRegionTestCase {
     
     public EntityRegionImplTestCase(String name) {
         super(name);
     } 
     
     @Override
     protected void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties) {
         
         EntityRegion region = regionFactory.buildEntityRegion("test", properties, null);
         
         assertNull("Got TRANSACTIONAL", region.buildAccessStrategy(AccessType.TRANSACTIONAL).lockRegion());
         
         try
         {
             region.buildAccessStrategy(AccessType.READ_ONLY).lockRegion();
             fail("Did not get READ_ONLY");
         }
         catch (UnsupportedOperationException good) {}
         
         try
         {
             region.buildAccessStrategy(AccessType.NONSTRICT_READ_WRITE);
             fail("Incorrectly got NONSTRICT_READ_WRITE");
         }
         catch (CacheException good) {}
         
         try
         {
             region.buildAccessStrategy(AccessType.READ_WRITE);
             fail("Incorrectly got READ_WRITE");
         }
         catch (CacheException good) {}      
         
     }
 
     @Override
     protected void putInRegion(Region region, Object key, Object value) {
         ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).insert(key, value, new Integer(1));
     }
 
     @Override
     protected void removeFromRegion(Region region, Object key) {
         ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).remove(key);
     }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName,
             Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildEntityRegion(regionName, properties, cdd);
    }
 
    @Override
-   protected Cache getInfinispanCache(InfinispanRegionFactory regionFactory) {
-      return regionFactory.getCacheManager().getCache("entity");
+   protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
+      return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("entity"));
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
index d3bee8471d..4f4687f506 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
@@ -1,38 +1,38 @@
 package org.hibernate.test.cache.infinispan.functional;
 
 import java.util.Map;
 
 import org.hibernate.junit.functional.FunctionalTestCase;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractFunctionalTestCase extends FunctionalTestCase {
    private final String cacheConcurrencyStrategy;
 
    public AbstractFunctionalTestCase(String string, String cacheConcurrencyStrategy) {
       super(string);
       this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
    }
 
    public String[] getMappings() {
       return new String[] { "cache/infinispan/functional/Item.hbm.xml" };
    }
 
    @Override
    public String getCacheConcurrencyStrategy() {
       return cacheConcurrencyStrategy;
    }
-   
+
    public void testEmptySecondLevelCacheEntry() throws Exception {
       getSessions().getCache().evictEntityRegion(Item.class.getName());
       Statistics stats = getSessions().getStatistics();
       stats.clear();
       SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
       Map cacheEntries = statistics.getEntries();
       assertEquals(0, cacheEntries.size());
   }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
index 6cdb0ef0d9..2ed3d95ae6 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
@@ -1,188 +1,191 @@
 package org.hibernate.test.cache.infinispan.functional;
 
 import java.io.Serializable;
+import java.util.Map;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class BasicTransactionalTestCase extends AbstractFunctionalTestCase {
 
    public BasicTransactionalTestCase(String string) {
       super(string, "transactional");
    }
 
    public void testEntityCache() {
       Item item = new Item("chris", "Chris's Item");
 
       Session s = openSession();
       Statistics stats = s.getSessionFactory().getStatistics();
       s.getTransaction().begin();
       s.persist(item);
       s.getTransaction().commit();
       s.close();
 
       s = openSession();
       Item found = (Item) s.load(Item.class, item.getId());
       System.out.println(stats);
       assertEquals(item.getDescription(), found.getDescription());
       assertEquals(0, stats.getSecondLevelCacheMissCount());
       assertEquals(1, stats.getSecondLevelCacheHitCount());
       s.delete(found);
       s.close();
    }
    
    public void testCollectionCache() {
       Item item = new Item("chris", "Chris's Item");
       Item another = new Item("another", "Owned Item");
       item.addItem(another);
 
       Session s = openSession();
       s.getTransaction().begin();
       s.persist(item);
       s.persist(another);
       s.getTransaction().commit();
       s.close();
 
       s = openSession();
       Statistics stats = s.getSessionFactory().getStatistics();
       Item loaded = (Item) s.load(Item.class, item.getId());
       assertEquals(1, loaded.getItems().size());
       s.close();
 
       s = openSession();
       SecondLevelCacheStatistics cStats = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
       Item loadedWithCachedCollection = (Item) s.load(Item.class, item.getId());
       stats.logSummary();
       assertEquals(item.getName(), loadedWithCachedCollection.getName());
       assertEquals(item.getItems().size(), loadedWithCachedCollection.getItems().size());
       assertEquals(1, cStats.getHitCount());
+      Map cacheEntries = cStats.getEntries();
+      assertEquals(1, cacheEntries.size());
       s.close();
    }
 
    public void testStaleWritesLeaveCacheConsistent() {
       Session s = openSession();
       Transaction txn = s.beginTransaction();
       VersionedItem item = new VersionedItem();
       item.setName("steve");
       item.setDescription("steve's item");
       s.save(item);
       txn.commit();
       s.close();
 
       Long initialVersion = item.getVersion();
 
       // manually revert the version property
       item.setVersion(new Long(item.getVersion().longValue() - 1));
 
       try {
           s = openSession();
           txn = s.beginTransaction();
           s.update(item);
           txn.commit();
           s.close();
           fail("expected stale write to fail");
       } catch (Throwable expected) {
           // expected behavior here
           if (txn != null) {
               try {
                   txn.rollback();
               } catch (Throwable ignore) {
               }
           }
       } finally {
           if (s != null && s.isOpen()) {
               try {
                   s.close();
               } catch (Throwable ignore) {
               }
           }
       }
 
       // check the version value in the cache...
       SecondLevelCacheStatistics slcs = sfi().getStatistics().getSecondLevelCacheStatistics(VersionedItem.class.getName());
 
       Object entry = slcs.getEntries().get(item.getId());
       Long cachedVersionValue;
       cachedVersionValue = (Long) ((CacheEntry) entry).getVersion();
       assertEquals(initialVersion.longValue(), cachedVersionValue.longValue());
 
       // cleanup
       s = openSession();
       txn = s.beginTransaction();
       item = (VersionedItem) s.load(VersionedItem.class, item.getId());
       s.delete(item);
       txn.commit();
       s.close();
   }
 
    public void testQueryCacheInvalidation() {
       Session s = openSession();
       Transaction t = s.beginTransaction();
       Item i = new Item();
       i.setName("widget");
       i.setDescription("A really top-quality, full-featured widget.");
       s.persist(i);
       t.commit();
       s.close();
 
       SecondLevelCacheStatistics slcs = s.getSessionFactory().getStatistics().getSecondLevelCacheStatistics(Item.class.getName());
 
       assertEquals(slcs.getPutCount(), 1);
       assertEquals(slcs.getElementCountInMemory(), 1);
       assertEquals(slcs.getEntries().size(), 1);
 
       s = openSession();
       t = s.beginTransaction();
       i = (Item) s.get(Item.class, i.getId());
 
       assertEquals(slcs.getHitCount(), 1);
       assertEquals(slcs.getMissCount(), 0);
 
       i.setDescription("A bog standard item");
 
       t.commit();
       s.close();
 
       assertEquals(slcs.getPutCount(), 2);
 
       CacheEntry entry = (CacheEntry) slcs.getEntries().get(i.getId());
       Serializable[] ser = entry.getDisassembledState();
       assertTrue(ser[0].equals("widget"));
       assertTrue(ser[1].equals("A bog standard item"));
       
       // cleanup
       s = openSession();
       t = s.beginTransaction();
       s.delete(i);
       t.commit();
       s.close();
    }
    
    public void testQueryCache() {
       Item item = new Item("chris", "Chris's Item");
 
       Session s = openSession();
       s.getTransaction().begin();
       s.persist(item);
       s.getTransaction().commit();
       s.close();
 
       s = openSession();
       s.createQuery("from Item").setCacheable(true).list();
       s.close();
 
       s = openSession();
       Statistics stats = s.getSessionFactory().getStatistics();
       s.createQuery("from Item").setCacheable(true).list();
       assertEquals(1, stats.getQueryCacheHitCount());
       s.createQuery("delete from Item").executeUpdate();
       s.close();
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
index 3efebe21cb..587f81a6ec 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
@@ -1,300 +1,341 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.functional.bulk;
 
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.FlushMode;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Session;
 import org.hibernate.junit.functional.FunctionalTestCase;
+import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.test.cache.infinispan.functional.Contact;
 import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.hibernate.transaction.CMTTransactionFactory;
 import org.hibernate.transaction.TransactionManagerLookup;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * BulkOperationsTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class BulkOperationsTestCase extends FunctionalTestCase {
 
    private static final Logger log = LoggerFactory.getLogger(BulkOperationsTestCase.class);
-   
+
    private TransactionManager tm;
-            
+
    public BulkOperationsTestCase(String string) {
       super(string);
    }
 
    public String[] getMappings() {
-      return new String[] { "cache/infinispan/functional/Contact.hbm.xml", "cache/infinispan/functional/Customer.hbm.xml" };
+      return new String[] { "cache/infinispan/functional/Contact.hbm.xml",
+               "cache/infinispan/functional/Customer.hbm.xml" };
    }
-   
+
    @Override
    public String getCacheConcurrencyStrategy() {
       return "transactional";
    }
-   
+
    protected Class getTransactionFactoryClass() {
-       return CMTTransactionFactory.class;
+      return CMTTransactionFactory.class;
    }
 
    protected Class getConnectionProviderClass() {
       return org.hibernate.test.cache.infinispan.tm.XaConnectionProvider.class;
    }
-  
+
    protected Class<? extends TransactionManagerLookup> getTransactionManagerLookupClass() {
       return org.hibernate.test.cache.infinispan.tm.XaTransactionManagerLookup.class;
    }
 
    public void configure(Configuration cfg) {
       super.configure(cfg);
 
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
       cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
+      cfg.setProperty(Environment.USE_QUERY_CACHE, "false");
       cfg.setProperty(Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName());
-      cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass().getName());
-      
+      cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass()
+               .getName());
+
       Class transactionFactory = getTransactionFactoryClass();
-      cfg.setProperty( Environment.TRANSACTION_STRATEGY, transactionFactory.getName());
+      cfg.setProperty(Environment.TRANSACTION_STRATEGY, transactionFactory.getName());
    }
 
    public void testBulkOperations() throws Throwable {
       System.out.println("*** testBulkOperations()");
       boolean cleanedUp = false;
       try {
          tm = getTransactionManagerLookupClass().newInstance().getTransactionManager(null);
-         
+
          createContacts();
 
          List<Integer> rhContacts = getContactsByCustomer("Red Hat");
          assertNotNull("Red Hat contacts exist", rhContacts);
          assertEquals("Created expected number of Red Hat contacts", 10, rhContacts.size());
 
+         SecondLevelCacheStatistics contactSlcs = getEnvironment().getSessionFactory()
+                  .getStatistics().getSecondLevelCacheStatistics(Contact.class.getName());
+         assertEquals(20, contactSlcs.getElementCountInMemory());
+
          assertEquals("Deleted all Red Hat contacts", 10, deleteContacts());
+         assertEquals(0, contactSlcs.getElementCountInMemory());
 
          List<Integer> jbContacts = getContactsByCustomer("JBoss");
          assertNotNull("JBoss contacts exist", jbContacts);
          assertEquals("JBoss contacts remain", 10, jbContacts.size());
 
          for (Integer id : rhContacts) {
             assertNull("Red Hat contact " + id + " cannot be retrieved", getContact(id));
          }
          rhContacts = getContactsByCustomer("Red Hat");
          if (rhContacts != null) {
             assertEquals("No Red Hat contacts remain", 0, rhContacts.size());
          }
 
          updateContacts("Kabir", "Updated");
+         assertEquals(0, contactSlcs.getElementCountInMemory());
          for (Integer id : jbContacts) {
             Contact contact = getContact(id);
             assertNotNull("JBoss contact " + id + " exists", contact);
             String expected = ("Kabir".equals(contact.getName())) ? "Updated" : "2222";
             assertEquals("JBoss contact " + id + " has correct TLF", expected, contact.getTlf());
          }
 
          List<Integer> updated = getContactsByTLF("Updated");
          assertNotNull("Got updated contacts", updated);
          assertEquals("Updated contacts", 5, updated.size());
-      } catch(Throwable t) {
+
+         updateContactsWithOneManual("Kabir", "UpdatedAgain");
+         assertEquals(contactSlcs.getElementCountInMemory(), 0);
+         for (Integer id : jbContacts) {
+            Contact contact = getContact(id);
+            assertNotNull("JBoss contact " + id + " exists", contact);
+            String expected = ("Kabir".equals(contact.getName())) ? "UpdatedAgain" : "2222";
+            assertEquals("JBoss contact " + id + " has correct TLF", expected, contact.getTlf());
+         }
+
+         updated = getContactsByTLF("UpdatedAgain");
+         assertNotNull("Got updated contacts", updated);
+         assertEquals("Updated contacts", 5, updated.size());
+      } catch (Throwable t) {
          cleanedUp = true;
          log.debug("Exceptional cleanup");
          cleanup(true);
          throw t;
       } finally {
          // cleanup the db so we can run this test multiple times w/o restarting the cluster
          if (!cleanedUp) {
             log.debug("Non exceptional cleanup");
             cleanup(false);
          }
       }
    }
 
    public void createContacts() throws Exception {
       log.debug("Create 10 contacts");
       tm.begin();
       try {
          for (int i = 0; i < 10; i++)
             createCustomer(i);
          tm.commit();
       } catch (Exception e) {
          log.error("Unable to create customer", e);
          tm.rollback();
          throw e;
       }
    }
 
    public int deleteContacts() throws Exception {
       String deleteHQL = "delete Contact where customer in ";
       deleteHQL += " (select customer FROM Customer as customer ";
       deleteHQL += " where customer.name = :cName)";
 
       tm.begin();
       try {
 
          Session session = getSessions().getCurrentSession();
          int rowsAffected = session.createQuery(deleteHQL).setFlushMode(FlushMode.AUTO)
                   .setParameter("cName", "Red Hat").executeUpdate();
          tm.commit();
          return rowsAffected;
       } catch (Exception e) {
          try {
             tm.rollback();
          } catch (Exception ee) {
             // ignored
          }
          throw e;
       }
    }
 
    public List<Integer> getContactsByCustomer(String customerName) throws Exception {
       String selectHQL = "select contact.id from Contact contact";
       selectHQL += " where contact.customer.name = :cName";
 
       log.debug("Get contacts for customer " + customerName);
       tm.begin();
       try {
 
          Session session = getSessions().getCurrentSession();
-         List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter("cName", customerName)
-                  .list();
+         List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter(
+                  "cName", customerName).list();
          tm.commit();
          return results;
       } catch (Exception e) {
          tm.rollback();
          throw e;
       }
    }
 
    public List<Integer> getContactsByTLF(String tlf) throws Exception {
       String selectHQL = "select contact.id from Contact contact";
       selectHQL += " where contact.tlf = :cTLF";
 
       tm.begin();
       try {
 
          Session session = getSessions().getCurrentSession();
-         List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter("cTLF", tlf).list();
+         List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter(
+                  "cTLF", tlf).list();
          tm.commit();
          return results;
       } catch (Exception e) {
          tm.rollback();
          throw e;
       }
    }
 
    public int updateContacts(String name, String newTLF) throws Exception {
       String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
-
       tm.begin();
       try {
+         Session session = getSessions().getCurrentSession();
+         int rowsAffected = session.createQuery(updateHQL).setFlushMode(FlushMode.AUTO)
+                  .setParameter("cNewTLF", newTLF).setParameter("cName", name).executeUpdate();
+         tm.commit();
+         return rowsAffected;
+      } catch (Exception e) {
+         tm.rollback();
+         throw e;
+      }
+   }
 
+   public int updateContactsWithOneManual(String name, String newTLF) throws Exception {
+      String queryHQL = "from Contact c where c.name = :cName";
+      String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
+      tm.begin();
+      try {
          Session session = getSessions().getCurrentSession();
-         int rowsAffected = session.createQuery(updateHQL).setFlushMode(FlushMode.AUTO).setParameter("cNewTLF", newTLF)
-                  .setParameter("cName", name).executeUpdate();
+         @SuppressWarnings("unchecked")
+         List<Contact> list = session.createQuery(queryHQL).setParameter("cName", name).list();
+         list.get(0).setTlf(newTLF);
+         int rowsAffected = session.createQuery(updateHQL).setFlushMode(FlushMode.AUTO)
+                  .setParameter("cNewTLF", newTLF).setParameter("cName", name).executeUpdate();
          tm.commit();
          return rowsAffected;
       } catch (Exception e) {
          tm.rollback();
          throw e;
       }
    }
 
    public Contact getContact(Integer id) throws Exception {
       tm.begin();
       try {
 
          Session session = getSessions().getCurrentSession();
          Contact contact = (Contact) session.get(Contact.class, id);
          tm.commit();
          return contact;
       } catch (Exception e) {
          tm.rollback();
          throw e;
       }
    }
 
    public void cleanup(boolean ignore) throws Exception {
       String deleteContactHQL = "delete from Contact";
       String deleteCustomerHQL = "delete from Customer";
       tm.begin();
       try {
          Session session = getSessions().getCurrentSession();
          session.createQuery(deleteContactHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
          session.createQuery(deleteCustomerHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
          tm.commit();
       } catch (Exception e) {
          if (!ignore) {
             try {
                tm.rollback();
             } catch (Exception ee) {
                // ignored
             }
             throw e;
          }
       }
    }
 
    private Customer createCustomer(int id) throws Exception {
       System.out.println("CREATE CUSTOMER " + id);
       try {
          Customer customer = new Customer();
          customer.setName((id % 2 == 0) ? "JBoss" : "Red Hat");
          Set<Contact> contacts = new HashSet<Contact>();
 
          Contact kabir = new Contact();
          kabir.setCustomer(customer);
          kabir.setName("Kabir");
          kabir.setTlf("1111");
          contacts.add(kabir);
 
          Contact bill = new Contact();
          bill.setCustomer(customer);
          bill.setName("Bill");
          bill.setTlf("2222");
          contacts.add(bill);
 
          customer.setContacts(contacts);
 
          Session s = openSession();
          s.getTransaction().begin();
          s.persist(customer);
          s.getTransaction().commit();
          s.close();
-         
+
          return customer;
       } finally {
          System.out.println("CREATE CUSTOMER " + id + " -  END");
       }
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
index 528dcd57b6..aa7c4edad2 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
@@ -1,110 +1,111 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat, Inc. and/or it's affiliates or third-party contributors as
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
 package org.hibernate.test.cache.infinispan.functional.classloader;
 
 import java.util.HashSet;
 import java.util.Set;
 
+import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
 import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 @Listener
 public class CacheAccessListener {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    HashSet modified = new HashSet();
    HashSet accessed = new HashSet();
 
    public void clear() {
       modified.clear();
       accessed.clear();
    }
 
    @CacheEntryModified
    public void nodeModified(CacheEntryModifiedEvent event) {
-      if (!event.isPre()) {
+      if (!event.isPre() && !CacheHelper.isEvictAllNotification(event.getKey())) {
          Object key = event.getKey();
          log.info("Modified node " + key);
          modified.add(key.toString());
       }
    }
    
    @CacheEntryCreated
    public void nodeCreated(CacheEntryCreatedEvent event) {
-      if (!event.isPre()) {
+      if (!event.isPre() && !CacheHelper.isEvictAllNotification(event.getKey())) {
          Object key = event.getKey();
          log.info("Created node " + key);
          modified.add(key.toString());
       }
    }
 
    @CacheEntryVisited
    public void nodeVisited(CacheEntryVisitedEvent event) {
-      if (!event.isPre()) {
+      if (!event.isPre() && !CacheHelper.isEvictAllNotification(event.getKey())) {
          Object key = event.getKey();
          log.info("Visited node " + key);
          accessed.add(key.toString());
       }
    }
 
    public boolean getSawRegionModification(Object key) {
       return getSawRegion(key, modified);
    }
    
    public int getSawRegionModificationCount() {
       return modified.size();
    }
    
    public void clearSawRegionModification() {
       modified.clear();
    }
 
    public boolean getSawRegionAccess(Object key) {
       return getSawRegion(key, accessed);
    }
 
    public int getSawRegionAccessCount() {
       return accessed.size();
    }
    
    public void clearSawRegionAccess() {
       accessed.clear();
    }
 
    private boolean getSawRegion(Object key, Set sawEvents) {
       if (sawEvents.contains(key)) {
          sawEvents.remove(key);
          return true;
       }
       return false;
   }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
index 0c305919b0..310f4ebec4 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
@@ -1,315 +1,315 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.functional.classloader;
 
 import javax.transaction.TransactionManager;
 
 import junit.framework.Test;
 import junit.framework.TestSuite;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.StandardQueryCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.functional.cluster.AbstractDualNodeTestCase;
 import org.hibernate.test.cache.infinispan.functional.cluster.ClusterAwareRegionFactory;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaTransactionManagerImpl;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheManager;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Tests entity and query caching when class of objects being cached are not visible to Infinispan's
  * classloader. Also serves as a general integration test.
  * <p/>
  * This test stores an object (AccountHolder) that isn't visible to the Infinispan classloader in
  * the cache in two places:
  * 
  * 1) As part of the value tuple in an Account entity 2) As part of the FQN in a query cache entry
  * (see query in ClassLoaderTestDAO.getBranch())
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class IsolatedClassLoaderTest extends AbstractDualNodeTestCase {
 
    public static final String OUR_PACKAGE = IsolatedClassLoaderTest.class.getPackage().getName();
 
    private static final String CACHE_CONFIG = "classloader";
 
    protected static final long SLEEP_TIME = 300L;
 
    protected final Logger log = LoggerFactory.getLogger(getClass());
 
    static int test = 0;
 
    private Cache localQueryCache ;
    private CacheAccessListener localQueryListener;
 
    private Cache remoteQueryCache;
    private CacheAccessListener remoteQueryListener;
 
    public IsolatedClassLoaderTest(String string) {
       super(string);
    }
 
    public static Test suite() throws Exception {
       TestSuite suite = new TestSuite(IsolatedClassLoaderTest.class);
       String[] acctClasses = { OUR_PACKAGE + ".Account", OUR_PACKAGE + ".AccountHolder" };
       return new IsolatedCacheTestSetup(suite, acctClasses);
    }
 
    @Override
    public String[] getMappings() {
       return new String[] { "cache/infinispan/functional/classloader/Account.hbm.xml" };
    }
    
    @Override
    protected void standardConfigure(Configuration cfg) {
       super.standardConfigure(cfg);
       cfg.setProperty(InfinispanRegionFactory.QUERY_CACHE_RESOURCE_PROP, "replicated-query");
    }
 
 
    @Override
    protected void cleanupTransactionManagement() {
       // Don't clean up the managers, just the transactions
       // Managers are still needed by the long-lived caches
       DualNodeJtaTransactionManagerImpl.cleanupTransactions();
    }
 
    @Override
    protected void cleanupTest() throws Exception {
       try {
          if (localQueryCache != null && localQueryListener != null)
             localQueryCache.removeListener(localQueryListener);
          if (remoteQueryCache != null && remoteQueryListener != null)
             remoteQueryCache.removeListener(remoteQueryListener);
       } finally {
          super.cleanupTest();
       }
    }
 
    /**
     * Simply confirms that the test fixture's classloader isolation setup is functioning as
     * expected.
     * 
     * @throws Exception
     */
    public void testIsolatedSetup() throws Exception {
       // Bind a listener to the "local" cache
       // Our region factory makes its CacheManager available to us
       CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
       Cache localReplicatedCache = localManager.getCache("replicated-entity");
 
       // Bind a listener to the "remote" cache
       CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.REMOTE);
       Cache remoteReplicatedCache = remoteManager.getCache("replicated-entity");
 
       ClassLoader cl = Thread.currentThread().getContextClassLoader();
       Thread.currentThread().setContextClassLoader(cl.getParent());
       log.info("TCCL is " + cl.getParent());
 
       Account acct = new Account();
       acct.setAccountHolder(new AccountHolder());
 
       try {
          localReplicatedCache.put("isolated1", acct);
          // With lazy deserialization, retrieval in remote forces class resolution
          remoteReplicatedCache.get("isolated1");
          fail("Should not have succeeded in putting acct -- classloader not isolated");
       } catch (Exception e) {
          if (e.getCause() instanceof ClassNotFoundException) {
             log.info("Caught exception as desired", e);
          } else {
             throw new IllegalStateException("Unexpected exception", e);
          }
       }
 
       Thread.currentThread().setContextClassLoader(cl);
       log.info("TCCL is " + cl);
       localReplicatedCache.put("isolated2", acct);
       assertEquals(acct.getClass().getName(), remoteReplicatedCache.get("isolated2").getClass().getName());
    }
 
    public void testClassLoaderHandlingNamedQueryRegion() throws Exception {
       queryTest(true);
    }
 
    public void testClassLoaderHandlingStandardQueryCache() throws Exception {
       queryTest(false);
    }
 
    protected void queryTest(boolean useNamedRegion) throws Exception {
       // Bind a listener to the "local" cache
       // Our region factory makes its CacheManager available to us
       CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
       localQueryCache = localManager.getCache("replicated-query");
       localQueryListener = new CacheAccessListener();
       localQueryCache.addListener(localQueryListener);
 
       TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.LOCAL);
 
       // Bind a listener to the "remote" cache
       CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.REMOTE);
       remoteQueryCache = remoteManager.getCache("replicated-query");
       remoteQueryListener = new CacheAccessListener();
       remoteQueryCache.addListener(remoteQueryListener);
 
       TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.REMOTE);
 
       SessionFactory localFactory = getEnvironment().getSessionFactory();
       SessionFactory remoteFactory = getSecondNodeEnvironment().getSessionFactory();
 
       ClassLoaderTestDAO dao0 = new ClassLoaderTestDAO(localFactory, localTM);
       ClassLoaderTestDAO dao1 = new ClassLoaderTestDAO(remoteFactory, remoteTM);
 
       // Initial ops on node 0
       setupEntities(dao0);
 
       String branch = "63088";
       // Query on post code count
       assertEquals(branch + " has correct # of accounts", 6, dao0.getCountForBranch(branch, useNamedRegion));
       
       assertEquals("Query cache used", 1, localQueryListener.getSawRegionModificationCount());
       localQueryListener.clearSawRegionModification();
       
 //      log.info("First query (get count for branch + " + branch + " ) on node0 done, contents of local query cache are: " + TestingUtil.printCache(localQueryCache));
 
       // Sleep a bit to allow async repl to happen
       sleep(SLEEP_TIME);
-      
+
       assertEquals("Query cache used", 1, remoteQueryListener.getSawRegionModificationCount());
       remoteQueryListener.clearSawRegionModification();
 
       // Do query again from node 1
       log.info("Repeat first query (get count for branch + " + branch + " ) on remote node");
       assertEquals("63088 has correct # of accounts", 6, dao1.getCountForBranch(branch, useNamedRegion));
       assertEquals("Query cache used", 1, remoteQueryListener.getSawRegionModificationCount());
       remoteQueryListener.clearSawRegionModification();
-      
+
       sleep(SLEEP_TIME);
-      
+
       assertEquals("Query cache used", 1, localQueryListener.getSawRegionModificationCount());
       localQueryListener.clearSawRegionModification();
-      
+
       log.info("First query on node 1 done");
 
       // Sleep a bit to allow async repl to happen
       sleep(SLEEP_TIME);
 
       // Do some more queries on node 0
       log.info("Do query Smith's branch");
       assertEquals("Correct branch for Smith", "94536", dao0.getBranch(dao0.getSmith(), useNamedRegion));
       log.info("Do query Jone's balance");
       assertEquals("Correct high balances for Jones", 40, dao0.getTotalBalance(dao0.getJones(), useNamedRegion));
 
       assertEquals("Query cache used", 2, localQueryListener.getSawRegionModificationCount());
       localQueryListener.clearSawRegionModification();
 //      // Clear the access state
 //      localQueryListener.getSawRegionAccess("???");
 
       log.info("Second set of queries on node0 done");
 
       // Sleep a bit to allow async repl to happen
       sleep(SLEEP_TIME);
 
       // Check if the previous queries replicated
       assertEquals("Query cache remotely modified", 2, remoteQueryListener.getSawRegionModificationCount());
       remoteQueryListener.clearSawRegionModification();
 
       log.info("Repeat second set of queries on node1");
 
       // Do queries again from node 1
       log.info("Again query Smith's branch");
       assertEquals("Correct branch for Smith", "94536", dao1.getBranch(dao1.getSmith(), useNamedRegion));
       log.info("Again query Jone's balance");
       assertEquals("Correct high balances for Jones", 40, dao1.getTotalBalance(dao1.getJones(), useNamedRegion));
 
       // Should be no change; query was already there
       assertEquals("Query cache modified", 0, remoteQueryListener.getSawRegionModificationCount());
       assertEquals("Query cache accessed", 2, remoteQueryListener.getSawRegionAccessCount());
       remoteQueryListener.clearSawRegionAccess();
 
       log.info("Second set of queries on node1 done");
 
       // allow async to propagate
       sleep(SLEEP_TIME);
 
       // Modify underlying data on node 1
       modifyEntities(dao1);
 
       // allow async timestamp change to propagate
       sleep(SLEEP_TIME);
 
       // Confirm query results are correct on node 0
       assertEquals("63088 has correct # of accounts", 7, dao0.getCountForBranch("63088", useNamedRegion));
       assertEquals("Correct branch for Smith", "63088", dao0.getBranch(dao0.getSmith(), useNamedRegion));
       assertEquals("Correct high balances for Jones", 50, dao0.getTotalBalance(dao0.getJones(), useNamedRegion));
       log.info("Third set of queries on node0 done");
    }
 
    protected void setupEntities(ClassLoaderTestDAO dao) throws Exception {
       dao.cleanup();
 
       dao.createAccount(dao.getSmith(), new Integer(1001), new Integer(5), "94536");
       dao.createAccount(dao.getSmith(), new Integer(1002), new Integer(15), "94536");
       dao.createAccount(dao.getSmith(), new Integer(1003), new Integer(20), "94536");
 
       dao.createAccount(dao.getJones(), new Integer(2001), new Integer(5), "63088");
       dao.createAccount(dao.getJones(), new Integer(2002), new Integer(15), "63088");
       dao.createAccount(dao.getJones(), new Integer(2003), new Integer(20), "63088");
 
       dao.createAccount(dao.getBarney(), new Integer(3001), new Integer(5), "63088");
       dao.createAccount(dao.getBarney(), new Integer(3002), new Integer(15), "63088");
       dao.createAccount(dao.getBarney(), new Integer(3003), new Integer(20), "63088");
 
       log.info("Standard entities created");
    }
 
    protected void resetRegionUsageState(CacheAccessListener localListener, CacheAccessListener remoteListener) {
       String stdName = StandardQueryCache.class.getName();
       String acctName = Account.class.getName();
 
       localListener.getSawRegionModification(stdName);
       localListener.getSawRegionModification(acctName);
 
       localListener.getSawRegionAccess(stdName);
       localListener.getSawRegionAccess(acctName);
 
       remoteListener.getSawRegionModification(stdName);
       remoteListener.getSawRegionModification(acctName);
 
       remoteListener.getSawRegionAccess(stdName);
       remoteListener.getSawRegionAccess(acctName);
 
       log.info("Region usage state cleared");
    }
 
    protected void modifyEntities(ClassLoaderTestDAO dao) throws Exception {
       dao.updateAccountBranch(1001, "63088");
       dao.updateAccountBalance(2001, 15);
 
       log.info("Entities modified");
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java
index 8a2c7d9df5..9a73105e11 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java
@@ -1,238 +1,241 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
+import java.util.Set;
+
 import org.hibernate.Session;
+import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.junit.functional.ExecutionEnvironment;
 import org.hibernate.junit.functional.FunctionalTestCase;
 import org.hibernate.transaction.CMTTransactionFactory;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * AbstractDualNodeTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractDualNodeTestCase extends FunctionalTestCase {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractDualNodeTestCase.class);
    public static final String NODE_ID_PROP = "hibernate.test.cluster.node.id";
    public static final String LOCAL = "local";
    public static final String REMOTE = "remote";
    private ExecutionEnvironment secondNodeEnvironment;
    private Session secondNodeSession;
 
    public AbstractDualNodeTestCase(String string) {
       super(string);
    }
    
    public String[] getMappings() {
       return new String[] { "cache/infinispan/functional/Contact.hbm.xml", "cache/infinispan/functional/Customer.hbm.xml" };
    }
    
    @Override
    public String getCacheConcurrencyStrategy() {
       return "transactional";
    }
    
    protected Class getCacheRegionFactory() {
       return ClusterAwareRegionFactory.class;
    }
 
    @Override
    public void configure(Configuration cfg) {
       standardConfigure(cfg);
       configureFirstNode(cfg);
    }
 
    @Override
    protected void prepareTest() throws Exception {
       log.info("Building second node locally managed execution env");
       secondNodeEnvironment = new ExecutionEnvironment(new SecondNodeSettings());
       secondNodeEnvironment.initialize();
       super.prepareTest();
    }
    
    @Override
    protected void runTest() throws Throwable {
       try {
           super.runTest();
       } finally {
          if ( secondNodeSession != null && secondNodeSession.isOpen() ) {
              if ( secondNodeSession.isConnected() ) {
                 secondNodeSession.connection().rollback();
              }
              secondNodeSession.close();
              secondNodeSession = null;
              fail( "unclosed session" );
          } else {
             secondNodeSession = null;
          }
          
       }
    }
 
    @Override
    protected void cleanupTest() throws Exception {
       try {
           super.cleanupTest();
       
           log.info( "Destroying second node locally managed execution env" );
           secondNodeEnvironment.complete();
           secondNodeEnvironment = null;
       } finally {
          cleanupTransactionManagement();
       }
    }
    
    protected void cleanupTransactionManagement() {
       DualNodeJtaTransactionManagerImpl.cleanupTransactions();
       DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
    }
 
    public ExecutionEnvironment getSecondNodeEnvironment() {
       return secondNodeEnvironment;
    }
 
    protected Class getConnectionProviderClass() {
       return DualNodeConnectionProviderImpl.class;
    }
 
    protected Class getTransactionManagerLookupClass() {
       return DualNodeTransactionManagerLookup.class;
    }
 
    protected Class getTransactionFactoryClass() {
       return CMTTransactionFactory.class;
    }
 
    /**
     * Apply any node-specific configurations to our first node.
     * 
     * @param the
     *           Configuration to update.
     */
    protected void configureFirstNode(Configuration cfg) {
       cfg.setProperty(NODE_ID_PROP, LOCAL);
    }
 
    /**
     * Apply any node-specific configurations to our second node.
     * 
     * @param the
     *           Configuration to update.
     */
    protected void configureSecondNode(Configuration cfg) {
       cfg.setProperty(NODE_ID_PROP, REMOTE);
    }
    
    protected void sleep(long ms) {
       try {
           Thread.sleep(ms);
       }
       catch (InterruptedException e) {
           log.warn("Interrupted during sleep", e);
       }
   }
 
    protected void standardConfigure(Configuration cfg) {
       super.configure(cfg);
 
       cfg.setProperty(Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName());
       cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass().getName());
       cfg.setProperty(Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName());
       cfg.setProperty(Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName());
    }
 
    /**
     * Settings impl that delegates most calls to the DualNodeTestCase itself, but overrides the
     * configure method to allow separate cache settings for the second node.
     */
    public class SecondNodeSettings implements ExecutionEnvironment.Settings {
       private final AbstractDualNodeTestCase delegate;
 
       public SecondNodeSettings() {
          this.delegate = AbstractDualNodeTestCase.this;
       }
 
       /**
        * This is the important one -- we extend the delegate's work by adding second-node specific
        * settings
        */
       public void configure(Configuration arg0) {
          delegate.standardConfigure(arg0);
          configureSecondNode(arg0);
       }
 
       /**
        * Disable creating of schemas; we let the primary session factory do that to our shared
        * database.
        */
       public boolean createSchema() {
          return false;
       }
 
       /**
        * Disable creating of schemas; we let the primary session factory do that to our shared
        * database.
        */
       public boolean recreateSchemaAfterFailure() {
          return false;
       }
 
       public void afterConfigurationBuilt(Mappings arg0, Dialect arg1) {
          delegate.afterConfigurationBuilt(arg0, arg1);
       }
 
       public void afterSessionFactoryBuilt(SessionFactoryImplementor arg0) {
          delegate.afterSessionFactoryBuilt(arg0);
       }
 
       public boolean appliesTo(Dialect arg0) {
          return delegate.appliesTo(arg0);
       }
 
       public String getBaseForMappings() {
          return delegate.getBaseForMappings();
       }
 
       public String getCacheConcurrencyStrategy() {
          return delegate.getCacheConcurrencyStrategy();
       }
 
       public String[] getMappings() {
          return delegate.getMappings();
       }
 
       public boolean overrideCacheStrategy() {
          return delegate.overrideCacheStrategy();
       }
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
index edf1107669..721513ed5c 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
@@ -1,355 +1,364 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.CacheKey;
+import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.test.cache.infinispan.functional.Contact;
 import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheManager;
-import org.infinispan.marshall.MarshalledValue;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.jboss.util.collection.ConcurrentSet;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * EntityCollectionInvalidationTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class EntityCollectionInvalidationTestCase extends AbstractDualNodeTestCase {
    private static final Logger log = LoggerFactory.getLogger(EntityCollectionInvalidationTestCase.class);
    private static final long SLEEP_TIME = 50l;
    private static final Integer CUSTOMER_ID = new Integer(1);
    static int test = 0;
 
    public EntityCollectionInvalidationTestCase(String string) {
       super(string);
    }
 
    protected String getEntityCacheConfigName() {
       return "entity";
    }
 
    public void testAll() throws Exception {
       log.info("*** testAll()");
 
       // Bind a listener to the "local" cache
       // Our region factory makes its CacheManager available to us
       CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
       // Cache localCache = localManager.getCache("entity");
       Cache localCustomerCache = localManager.getCache(Customer.class.getName());
       Cache localContactCache = localManager.getCache(Contact.class.getName());
       Cache localCollectionCache = localManager.getCache(Customer.class.getName() + ".contacts");
       MyListener localListener = new MyListener("local");
       localCustomerCache.addListener(localListener);
       localContactCache.addListener(localListener);
       localCollectionCache.addListener(localListener);
       TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.LOCAL);
 
       // Bind a listener to the "remote" cache
       CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.REMOTE);
       Cache remoteCustomerCache = remoteManager.getCache(Customer.class.getName());
       Cache remoteContactCache = remoteManager.getCache(Contact.class.getName());
       Cache remoteCollectionCache = remoteManager.getCache(Customer.class.getName() + ".contacts");
       MyListener remoteListener = new MyListener("remote");
       remoteCustomerCache.addListener(remoteListener);
       remoteContactCache.addListener(remoteListener);
       remoteCollectionCache.addListener(remoteListener);
       TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.REMOTE);
 
       SessionFactory localFactory = getEnvironment().getSessionFactory();
       SessionFactory remoteFactory = getSecondNodeEnvironment().getSessionFactory();
 
       try {
          assertTrue(remoteListener.isEmpty());
          assertTrue(localListener.isEmpty());
          
          log.debug("Create node 0");
          IdContainer ids = createCustomer(localFactory, localTM);
 
          assertTrue(remoteListener.isEmpty());
          assertTrue(localListener.isEmpty());
          
          // Sleep a bit to let async commit propagate. Really just to
          // help keep the logs organized for debugging any issues
          sleep(SLEEP_TIME);
 
          log.debug("Find node 0");
          // This actually brings the collection into the cache
          getCustomer(ids.customerId, localFactory, localTM);
 
          sleep(SLEEP_TIME);
 
          // Now the collection is in the cache so, the 2nd "get"
          // should read everything from the cache
          log.debug("Find(2) node 0");
          localListener.clear();
          getCustomer(ids.customerId, localFactory, localTM);
 
          // Check the read came from the cache
          log.debug("Check cache 0");
          assertLoadedFromCache(localListener, ids.customerId, ids.contactIds);
 
          log.debug("Find node 1");
          // This actually brings the collection into the cache since invalidation is in use
          getCustomer(ids.customerId, remoteFactory, remoteTM);
          
          // Now the collection is in the cache so, the 2nd "get"
          // should read everything from the cache
          log.debug("Find(2) node 1");
          remoteListener.clear();
          getCustomer(ids.customerId, remoteFactory, remoteTM);
 
          // Check the read came from the cache
          log.debug("Check cache 1");
          assertLoadedFromCache(remoteListener, ids.customerId, ids.contactIds);
 
          // Modify customer in remote
          remoteListener.clear();
          ids = modifyCustomer(ids.customerId, remoteFactory, remoteTM);
          sleep(250);
          assertLoadedFromCache(remoteListener, ids.customerId, ids.contactIds);
 
          // After modification, local cache should have been invalidated and hence should be empty
-         assertTrue(localCollectionCache.isEmpty());
-         assertTrue(localCustomerCache.isEmpty());
+         assertEquals(0, getValidKeyCount(localCollectionCache.keySet()));
+         assertEquals(0, getValidKeyCount(localCustomerCache.keySet()));
       } catch (Exception e) {
          log.error("Error", e);
          throw e;
       } finally {
          // cleanup the db
          log.debug("Cleaning up");
          cleanup(localFactory, localTM);
       }
    }
 
    private IdContainer createCustomer(SessionFactory sessionFactory, TransactionManager tm)
             throws Exception {
       log.debug("CREATE CUSTOMER");
 
       tm.begin();
 
       try {
          Session session = sessionFactory.getCurrentSession();
          Customer customer = new Customer();
          customer.setName("JBoss");
          Set<Contact> contacts = new HashSet<Contact>();
 
          Contact kabir = new Contact();
          kabir.setCustomer(customer);
          kabir.setName("Kabir");
          kabir.setTlf("1111");
          contacts.add(kabir);
 
          Contact bill = new Contact();
          bill.setCustomer(customer);
          bill.setName("Bill");
          bill.setTlf("2222");
          contacts.add(bill);
 
          customer.setContacts(contacts);
 
          session.save(customer);
          tm.commit();
 
          IdContainer ids = new IdContainer();
          ids.customerId = customer.getId();
          Set contactIds = new HashSet();
          contactIds.add(kabir.getId());
          contactIds.add(bill.getId());
          ids.contactIds = contactIds;
 
          return ids;
       } catch (Exception e) {
          log.error("Caught exception creating customer", e);
          try {
             tm.rollback();
          } catch (Exception e1) {
             log.error("Exception rolling back txn", e1);
          }
          throw e;
       } finally {
          log.debug("CREATE CUSTOMER -  END");
       }
    }
 
    private Customer getCustomer(Integer id, SessionFactory sessionFactory, TransactionManager tm) throws Exception {
       log.debug("Find customer with id=" + id);
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          Customer customer = doGetCustomer(id, session, tm);
          tm.commit();
          return customer;
       } catch (Exception e) {
          try {
             tm.rollback();
          } catch (Exception e1) {
             log.error("Exception rolling back txn", e1);
          }
          throw e;
       } finally {
          log.debug("Find customer ended.");
       }
    }
    
    private Customer doGetCustomer(Integer id, Session session, TransactionManager tm) throws Exception {
       Customer customer = (Customer) session.get(Customer.class, id);
       // Access all the contacts
       for (Iterator it = customer.getContacts().iterator(); it.hasNext();) {
          ((Contact) it.next()).getName();
       }
       return customer;
    }
    
    private IdContainer modifyCustomer(Integer id, SessionFactory sessionFactory, TransactionManager tm) throws Exception {
       log.debug("Modify customer with id=" + id);
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          IdContainer ids = new IdContainer();
          Set contactIds = new HashSet();
          Customer customer = doGetCustomer(id, session, tm);
          customer.setName("NewJBoss");
          ids.customerId = customer.getId();
          Set<Contact> contacts = customer.getContacts();
          for (Contact c : contacts) {
             contactIds.add(c.getId());
          }
          Contact contact = contacts.iterator().next();
          contacts.remove(contact);
          contactIds.remove(contact.getId());
          ids.contactIds = contactIds;
          contact.setCustomer(null);
          
          session.save(customer);
          tm.commit();
          return ids;
       } catch (Exception e) {
          try {
             tm.rollback();
          } catch (Exception e1) {
             log.error("Exception rolling back txn", e1);
          }
          throw e;
       } finally {
          log.debug("Find customer ended.");
       }
    }
 
    private void cleanup(SessionFactory sessionFactory, TransactionManager tm) throws Exception {
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          Customer c = (Customer) session.get(Customer.class, CUSTOMER_ID);
          if (c != null) {
             Set contacts = c.getContacts();
             for (Iterator it = contacts.iterator(); it.hasNext();)
                session.delete(it.next());
             c.setContacts(null);
             session.delete(c);
          }
 
          tm.commit();
       } catch (Exception e) {
          try {
             tm.rollback();
          } catch (Exception e1) {
             log.error("Exception rolling back txn", e1);
          }
          log.error("Caught exception in cleanup", e);
       }
    }
 
    private void assertLoadedFromCache(MyListener listener, Integer custId, Set contactIds) {
       assertTrue("Customer#" + custId + " was in cache", listener.visited.contains("Customer#"
                + custId));
       for (Iterator it = contactIds.iterator(); it.hasNext();) {
          Integer contactId = (Integer) it.next();
          assertTrue("Contact#" + contactId + " was in cache", listener.visited.contains("Contact#"
                   + contactId));
          assertTrue("Contact#" + contactId + " was in cache", listener.visited.contains("Contact#"
                   + contactId));
       }
       assertTrue("Customer.contacts" + custId + " was in cache", listener.visited
                .contains("Customer.contacts#" + custId));
    }
 
+   protected int getValidKeyCount(Set keys) {
+      int result = 0;
+      for (Object key : keys) {
+         if (!(CacheHelper.isEvictAllNotification(key))) {
+            result++;
+         }
+      }
+      return result;
+  }
+
    @Listener
    public static class MyListener {
       private static final Logger log = LoggerFactory.getLogger(MyListener.class);
       private Set<String> visited = new ConcurrentSet<String>();
       private final String name;
       
       public MyListener(String name) {
          this.name = name;
       }      
 
       public void clear() {
          visited.clear();
       }
       
       public boolean isEmpty() {
          return visited.isEmpty();
       }
 
       @CacheEntryVisited
       public void nodeVisited(CacheEntryVisitedEvent event) {
          log.debug(event.toString());
          if (!event.isPre()) {
-            MarshalledValue mv = (MarshalledValue) event.getKey();
-            CacheKey cacheKey = (CacheKey) mv.get();
+            CacheKey cacheKey = (CacheKey) event.getKey();
             Integer primKey = (Integer) cacheKey.getKey();
             String key = (String) cacheKey.getEntityOrRoleName() + '#' + primKey;
             log.debug("MyListener[" + name +"] - Visiting key " + key);
             // String name = fqn.toString();
             String token = ".functional.";
             int index = key.indexOf(token);
             if (index > -1) {
                index += token.length();
                key = key.substring(index);
                log.debug("MyListener[" + name +"] - recording visit to " + key);
                visited.add(key);
             }
          }
       }
    }
 
    private class IdContainer {
       Integer customerId;
       Set<Integer> contactIds;
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index 103ade40db..e0565d4041 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,299 +1,300 @@
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
 
 import junit.framework.AssertionFailedError;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.StandardQueryCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
-import org.infinispan.Cache;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.infinispan.util.concurrent.IsolationLevel;
 
 /**
  * Tests of QueryResultRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
    // protected static final String REGION_NAME = "test/" + StandardQueryCache.class.getName();
 
    /**
     * Create a new EntityRegionImplTestCase.
     * 
     * @param name
     */
    public QueryRegionImplTestCase(String name) {
       super(name);
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildQueryResultsRegion(regionName, properties);
    }
 
    @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + StandardQueryCache.class.getName();
    }
 
    @Override
-   protected Cache getInfinispanCache(InfinispanRegionFactory regionFactory) {
-      return regionFactory.getCacheManager().getCache("local-query");
+   protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
+      return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("local-query"));
    }
    
    @Override
    protected Configuration createConfiguration() {
       return CacheTestUtil.buildCustomQueryCacheConfiguration("test", "replicated-query");
    }
 
    public void testPutDoesNotBlockGet() throws Exception {
       putDoesNotBlockGetTest();
    }
 
    private void putDoesNotBlockGetTest() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg
                .getProperties());
 
       region.put(KEY, VALUE1);
       assertEquals(VALUE1, region.get(KEY));
 
       final CountDownLatch readerLatch = new CountDownLatch(1);
       final CountDownLatch writerLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(1);
       final ExceptionHolder holder = new ExceptionHolder();
 
       Thread reader = new Thread() {
          public void run() {
             try {
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Transaction began, get value for key");
                assertTrue(VALUE2.equals(region.get(KEY)) == false);
                BatchModeTransactionManager.getInstance().commit();
             } catch (AssertionFailedError e) {
                holder.a1 = e;
                rollback();
             } catch (Exception e) {
                holder.e1 = e;
                rollback();
             } finally {
                readerLatch.countDown();
             }
          }
       };
 
       Thread writer = new Thread() {
          public void run() {
             try {
                BatchModeTransactionManager.getInstance().begin();
                log.debug("Put value2");
                region.put(KEY, VALUE2);
                log.debug("Put finished for value2, await writer latch");
                writerLatch.await();
                log.debug("Writer latch finished");
                BatchModeTransactionManager.getInstance().commit();
                log.debug("Transaction committed");
             } catch (Exception e) {
                holder.e2 = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       reader.setDaemon(true);
       writer.setDaemon(true);
 
       writer.start();
       assertFalse("Writer is blocking", completionLatch.await(100, TimeUnit.MILLISECONDS));
 
       // Start the reader
       reader.start();
       assertTrue("Reader finished promptly", readerLatch.await(1000000000, TimeUnit.MILLISECONDS));
 
       writerLatch.countDown();
       assertTrue("Reader finished promptly", completionLatch.await(100, TimeUnit.MILLISECONDS));
 
       assertEquals(VALUE2, region.get(KEY));
 
       if (holder.a1 != null)
          throw holder.a1;
       else if (holder.a2 != null)
          throw holder.a2;
 
       assertEquals("writer saw no exceptions", null, holder.e1);
       assertEquals("reader saw no exceptions", null, holder.e2);
    }
 
    public void testGetDoesNotBlockPut() throws Exception {
       getDoesNotBlockPutTest();
    }
 
    private void getDoesNotBlockPutTest() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg
                .getProperties());
 
       region.put(KEY, VALUE1);
       assertEquals(VALUE1, region.get(KEY));
 
       // final Fqn rootFqn = getRegionFqn(getStandardRegionName(REGION_PREFIX), REGION_PREFIX);
-      final Cache jbc = getInfinispanCache(regionFactory);
+      final CacheAdapter jbc = getInfinispanCache(regionFactory);
 
       final CountDownLatch blockerLatch = new CountDownLatch(1);
       final CountDownLatch writerLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(1);
       final ExceptionHolder holder = new ExceptionHolder();
 
       Thread blocker = new Thread() {
 
          public void run() {
             // Fqn toBlock = new Fqn(rootFqn, KEY);
             GetBlocker blocker = new GetBlocker(blockerLatch, KEY);
             try {
                jbc.addListener(blocker);
 
                BatchModeTransactionManager.getInstance().begin();
                region.get(KEY);
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                holder.e1 = e;
                rollback();
             } finally {
                jbc.removeListener(blocker);
             }
          }
       };
 
       Thread writer = new Thread() {
 
          public void run() {
             try {
                writerLatch.await();
 
                BatchModeTransactionManager.getInstance().begin();
                region.put(KEY, VALUE2);
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                holder.e2 = e;
                rollback();
             } finally {
                completionLatch.countDown();
             }
          }
       };
 
       blocker.setDaemon(true);
       writer.setDaemon(true);
 
       boolean unblocked = false;
       try {
          blocker.start();
          writer.start();
 
          assertFalse("Blocker is blocking", completionLatch.await(100, TimeUnit.MILLISECONDS));
          // Start the writer
          writerLatch.countDown();
          assertTrue("Writer finished promptly", completionLatch.await(100, TimeUnit.MILLISECONDS));
 
          blockerLatch.countDown();
          unblocked = true;
 
          if (IsolationLevel.REPEATABLE_READ.equals(jbc.getConfiguration().getIsolationLevel())) {
             assertEquals(VALUE1, region.get(KEY));
          } else {
             assertEquals(VALUE2, region.get(KEY));
          }
 
          if (holder.a1 != null)
             throw holder.a1;
          else if (holder.a2 != null)
             throw holder.a2;
 
          assertEquals("blocker saw no exceptions", null, holder.e1);
          assertEquals("writer saw no exceptions", null, holder.e2);
       } finally {
          if (!unblocked)
             blockerLatch.countDown();
       }
    }
 
    @Listener
    public class GetBlocker {
 
       private CountDownLatch latch;
       // private Fqn fqn;
       private Object key;
 
       GetBlocker(CountDownLatch latch, Object key) {
          this.latch = latch;
          this.key = key;
       }
 
       @CacheEntryVisited
       public void nodeVisisted(CacheEntryVisitedEvent event) {
          if (event.isPre() && event.getKey().equals(key)) {
             try {
                latch.await();
             } catch (InterruptedException e) {
                log.error("Interrupted waiting for latch", e);
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
index 47d28ae567..8b6ce47a42 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
@@ -1,62 +1,63 @@
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
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
-import org.infinispan.Cache;
 
 /**
  * Tests of TimestampsRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TimestampsRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
     
     public TimestampsRegionImplTestCase(String name) {
         super(name);
     }
 
     @Override
     protected String getStandardRegionName(String regionPrefix) {
         return regionPrefix + "/" + UpdateTimestampsCache.class.getName();
     }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildTimestampsRegion(regionName, properties);
    }
 
    @Override
-   protected Cache getInfinispanCache(InfinispanRegionFactory regionFactory) {
-      return regionFactory.getCacheManager().getCache("timestamps");
+   protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
+      return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("timestamps"));
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
index b68e75de6c..4143d424f1 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
@@ -1,237 +1,243 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat, Inc. and/or it's affiliates, and individual contributors
  * as indicated by the @author tags. See the copyright.txt file in the
  * distribution for a full listing of individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */
 package org.hibernate.test.cache.infinispan.tm;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.concurrent.atomic.AtomicInteger;
 
 import javax.transaction.HeuristicMixedException;
 import javax.transaction.HeuristicRollbackException;
 import javax.transaction.RollbackException;
 import javax.transaction.Status;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.xa.XAException;
 import javax.transaction.xa.XAResource;
 import javax.transaction.xa.Xid;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * XaResourceCapableTransactionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class XaTransactionImpl implements Transaction {
    private static final Logger log = LoggerFactory.getLogger(XaTransactionImpl.class);
    private int status;
    private LinkedList synchronizations;
    private Connection connection; // the only resource we care about is jdbc connection
    private final XaTransactionManagerImpl jtaTransactionManager;
    private List<XAResource> enlistedResources = new ArrayList<XAResource>();
    private Xid xid = new XaResourceCapableTransactionXid();
 
    public XaTransactionImpl(XaTransactionManagerImpl jtaTransactionManager) {
       this.jtaTransactionManager = jtaTransactionManager;
       this.status = Status.STATUS_ACTIVE;
    }
 
    public int getStatus() {
       return status;
    }
 
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
             IllegalStateException, SystemException {
 
       if (status == Status.STATUS_MARKED_ROLLBACK) {
          log.trace("on commit, status was marked for rollback-only");
          rollback();
       } else {
          status = Status.STATUS_PREPARING;
 
          for (int i = 0; i < synchronizations.size(); i++) {
             Synchronization s = (Synchronization) synchronizations.get(i);
             s.beforeCompletion();
          }
+         
+         runXaResourcePrepare();
 
          status = Status.STATUS_COMMITTING;
 
          if (connection != null) {
             try {
                connection.commit();
                connection.close();
             } catch (SQLException sqle) {
                status = Status.STATUS_UNKNOWN;
                throw new SystemException();
             }
          }
+         
+         runXaResourceCommitTx();
 
          status = Status.STATUS_COMMITTED;
 
          for (int i = 0; i < synchronizations.size(); i++) {
             Synchronization s = (Synchronization) synchronizations.get(i);
             s.afterCompletion(status);
          }
 
          // status = Status.STATUS_NO_TRANSACTION;
          jtaTransactionManager.endCurrent(this);
       }
    }
 
    public void rollback() throws IllegalStateException, SystemException {
       status = Status.STATUS_ROLLEDBACK;
 
       if (connection != null) {
          try {
             connection.rollback();
             connection.close();
          } catch (SQLException sqle) {
             status = Status.STATUS_UNKNOWN;
             throw new SystemException();
          }
       }
+      
+      runXaResourceRollback();
 
       for (int i = 0; i < synchronizations.size(); i++) {
          Synchronization s = (Synchronization) synchronizations.get(i);
          s.afterCompletion(status);
       }
 
       // status = Status.STATUS_NO_TRANSACTION;
       jtaTransactionManager.endCurrent(this);
    }
 
    public void setRollbackOnly() throws IllegalStateException, SystemException {
       status = Status.STATUS_MARKED_ROLLBACK;
    }
 
    public void registerSynchronization(Synchronization synchronization) throws RollbackException,
             IllegalStateException, SystemException {
       // todo : find the spec-allowable statuses during which synch can be registered...
       if (synchronizations == null) {
          synchronizations = new LinkedList();
       }
       synchronizations.add(synchronization);
    }
 
    public void enlistConnection(Connection connection) {
       if (this.connection != null) {
          throw new IllegalStateException("Connection already registered");
       }
       this.connection = connection;
    }
 
    public Connection getEnlistedConnection() {
       return connection;
    }
 
    public boolean enlistResource(XAResource xaResource) throws RollbackException, IllegalStateException,
             SystemException {
       enlistedResources.add(xaResource);
       try {
          xaResource.start(xid, 0);
       } catch (XAException e) {
          log.error("Got an exception", e);
          throw new SystemException(e.getMessage());
       }
       return true;
    }
 
    public boolean delistResource(XAResource xaResource, int i) throws IllegalStateException, SystemException {
       throw new SystemException("not supported");
    }
    
    public Collection<XAResource> getEnlistedResources() {
       return enlistedResources;
    }
    
    private boolean runXaResourcePrepare() throws SystemException {
       Collection<XAResource> resources = getEnlistedResources();
       for (XAResource res : resources) {
          try {
             res.prepare(xid);
          } catch (XAException e) {
             log.trace("The resource wants to rollback!", e);
             return false;
          } catch (Throwable th) {
             log.error("Unexpected error from resource manager!", th);
             throw new SystemException(th.getMessage());
          }
       }
       return true;
    }
    
    private void runXaResourceRollback() {
       Collection<XAResource> resources = getEnlistedResources();
       for (XAResource res : resources) {
          try {
             res.rollback(xid);
          } catch (XAException e) {
             log.warn("Error while rolling back",e);
          }
       }
    }
 
    private boolean runXaResourceCommitTx() throws HeuristicMixedException {
       Collection<XAResource> resources = getEnlistedResources();
       for (XAResource res : resources) {
          try {
             res.commit(xid, false);//todo we only support one phase commit for now, change this!!!
          } catch (XAException e) {
             log.warn("exception while committing",e);
             throw new HeuristicMixedException(e.getMessage());
          }
       }
       return true;
    }
    
    private static class XaResourceCapableTransactionXid implements Xid {
       private static AtomicInteger txIdCounter = new AtomicInteger(0);
       private int id = txIdCounter.incrementAndGet();
 
       public int getFormatId() {
          return id;
       }
 
       public byte[] getGlobalTransactionId() {
          throw new IllegalStateException("TODO - please implement me!!!"); //todo implement!!!
       }
 
       public byte[] getBranchQualifier() {
          throw new IllegalStateException("TODO - please implement me!!!"); //todo implement!!!
       }
 
       @Override
       public String toString() {
          return getClass().getSimpleName() + "{" +
                "id=" + id +
                '}';
       }
    }
 }
