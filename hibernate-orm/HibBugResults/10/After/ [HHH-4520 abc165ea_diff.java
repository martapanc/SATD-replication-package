diff --git a/cache-infinispan/pom.xml b/cache-infinispan/pom.xml
index 1ed78f52e7..2440d6c931 100644
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
-      <version.infinispan>4.0.0-SNAPSHOT</version.infinispan>
+      <version.infinispan>4.0.0.CR2</version.infinispan>
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
index 0d84aa8e80..f2aa21bf4d 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/InfinispanRegionFactory.java
@@ -1,392 +1,392 @@
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
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Settings;
 import org.hibernate.util.PropertiesHelper;
 import org.infinispan.Cache;
 import org.infinispan.config.Configuration;
 import org.infinispan.manager.CacheManager;
 import org.infinispan.manager.DefaultCacheManager;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * A {@link RegionFactory} for <a href="http://www.jboss.org/infinispan">Infinispan</a>-backed cache
  * regions.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class InfinispanRegionFactory implements RegionFactory {
-   
-   private static final Logger log = LoggerFactory.getLogger(InfinispanRegionFactory.class);
-   
+
+   private static final Log log = LogFactory.getLog(InfinispanRegionFactory.class);
+
    private static final String PREFIX = "hibernate.cache.infinispan.";
-   
+
    private static final String CONFIG_SUFFIX = ".cfg";
-   
+
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
       CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(cache);
       CollectionRegionImpl region = new CollectionRegionImpl(cacheAdapter, regionName, metadata, transactionManager);
       region.start();
       return region;
    }
 
    /** {@inheritDoc} */
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
       if (log.isDebugEnabled()) log.debug("Building entity cache region [" + regionName + "]");
       Cache cache = getCache(regionName, ENTITY_KEY, properties);
       CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(cache);
       EntityRegionImpl region = new EntityRegionImpl(cacheAdapter, regionName, metadata, transactionManager);
       region.start();
       return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties)
             throws CacheException {
       log.debug("Building query results cache region [" + regionName + "]");
       String cacheName = typeOverrides.get(QUERY_KEY).getCacheName();
       CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(manager.getCache(cacheName));
       QueryResultsRegionImpl region = new QueryResultsRegionImpl(cacheAdapter, regionName, properties, transactionManager);
       region.start();
       return region;
    }
 
    /**
     * {@inheritDoc}
     */
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties)
             throws CacheException {
       log.debug("Building timestamps cache region [" + regionName + "]");
       String cacheName = typeOverrides.get(TIMESTAMPS_KEY).getCacheName();
       CacheAdapter cacheAdapter = CacheAdapterImpl.newInstance(manager.getCache(cacheName));
       TimestampsRegionImpl region = new TimestampsRegionImpl(cacheAdapter, regionName, transactionManager);
       region.start();
       return region;
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
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java
index 0986635128..2b5142836a 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/JndiInfinispanRegionFactory.java
@@ -1,83 +1,83 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
 package org.hibernate.cache.infinispan;
 
 import java.util.Properties;
 
 import javax.naming.Context;
 import javax.naming.InitialContext;
 import javax.naming.NamingException;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.util.NamingHelper;
 import org.hibernate.util.PropertiesHelper;
 import org.infinispan.manager.CacheManager;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * A {@link RegionFactory} for <a href="http://www.jboss.org/infinispan">Infinispan</a>-backed cache
  * regions that finds its cache manager in JNDI rather than creating one itself.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class JndiInfinispanRegionFactory extends InfinispanRegionFactory {
 
-   private static final Logger log = LoggerFactory.getLogger(JndiInfinispanRegionFactory.class);
+   private static final Log log = LogFactory.getLog(JndiInfinispanRegionFactory.class);
 
    /**
     * Specifies the JNDI name under which the {@link CacheManager} to use is bound.
     * There is no default value -- the user must specify the property.
     */
    public static final String CACHE_MANAGER_RESOURCE_PROP = "hibernate.cache.infinispan.cachemanager";
    
    @Override
    protected CacheManager createCacheManager(Properties properties) throws CacheException {
       String name = PropertiesHelper.getString(CACHE_MANAGER_RESOURCE_PROP, properties, null);
       if (name == null)
          throw new CacheException("Configuration property " + CACHE_MANAGER_RESOURCE_PROP + " not set");
       return locateCacheManager(name, NamingHelper.getJndiProperties(properties));
    }
 
    private CacheManager locateCacheManager(String jndiNamespace, Properties jndiProperties) {
       Context ctx = null;
       try {
           ctx = new InitialContext(jndiProperties);
           return (CacheManager) ctx.lookup(jndiNamespace);
       } catch (NamingException ne) {
           String msg = "Unable to retrieve CacheManager from JNDI [" + jndiNamespace + "]";
           log.info(msg, ne);
           throw new CacheException( msg );
       } finally {
           if (ctx != null) {
               try {
                   ctx.close();
               } catch( NamingException ne ) {
                   log.info("Unable to release initial context", ne);
               }
           }
       }
   }
 
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java
new file mode 100644
index 0000000000..5ba5ee6fbf
--- /dev/null
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/PutFromLoadValidator.java
@@ -0,0 +1,478 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009, Red Hat, Inc or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
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
+ */
+package org.hibernate.cache.infinispan.access;
+
+import java.lang.ref.WeakReference;
+import java.util.HashMap;
+import java.util.LinkedList;
+import java.util.List;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
+import java.util.concurrent.ConcurrentMap;
+import java.util.concurrent.locks.Lock;
+import java.util.concurrent.locks.ReentrantLock;
+
+import javax.transaction.SystemException;
+import javax.transaction.Transaction;
+import javax.transaction.TransactionManager;
+
+import org.hibernate.cache.CacheException;
+
+/**
+ * Encapsulates logic to allow a {@link TransactionalAccessDelegate} to determine
+ * whether a {@link TransactionalAccessDelegate#putFromLoad(Object, Object, long, Object, boolean)
+ * call should be allowed to update the cache. A <code>putFromLoad</code> has
+ * the potential to store stale data, since the data may have been removed from the
+ * database and the cache between the time when the data was read from the database 
+ * and the actual call to <code>putFromLoad</code>.
+ *
+ * @author Brian Stansberry
+ * 
+ * @version $Revision: $
+ */
+public class PutFromLoadValidator {
+   /**
+    * Period in ms after a removal during which a call to {@link #isPutValid(Object)} that hasn't
+    * been {@link #registerPendingPut(Object) pre-registered} (aka a "naked put") will return false.
+    */
+   public static final long NAKED_PUT_INVALIDATION_PERIOD = 10 * 1000;
+
+   /** Period after which a pending put is placed in the over-age queue */
+   private static final long PENDING_PUT_OVERAGE_PERIOD = 5 * 1000;
+
+   /** Period before which we stop trying to clean out pending puts */
+   private static final long PENDING_PUT_RECENT_PERIOD = 2 * 1000;
+
+   /**
+    * Period after which a pending put is never expected to come in and should be cleaned
+    */
+   private static final long MAX_PENDING_PUT_DELAY = 2 * 60 * 1000;
+
+   /**
+    * Used to determine whether the owner of a pending put is a thread or a transaction
+    */
+   private final TransactionManager transactionManager;
+
+   private final long nakedPutInvalidationPeriod;
+   private final long pendingPutOveragePeriod;
+   private final long pendingPutRecentPeriod;
+   private final long maxPendingPutDelay;
+
+   /**
+    * Registry of expected, future, isPutValid calls. If a key+owner is registered in this map, it
+    * is not a "naked put" and is allowed to proceed.
+    */
+   private final ConcurrentMap<Object, PendingPutMap> pendingPuts = new ConcurrentHashMap<Object, PendingPutMap>();
+   /**
+    * List of pending puts. Used to ensure we don't leak memory via the pendingPuts map
+    */
+   private final List<WeakReference<PendingPut>> pendingQueue = new LinkedList<WeakReference<PendingPut>>();
+   /**
+    * Separate list of pending puts that haven't been resolved within PENDING_PUT_OVERAGE_PERIOD.
+    * Used to ensure we don't leak memory via the pendingPuts map. Tracked separately from more
+    * recent pending puts for efficiency reasons.
+    */
+   private final List<WeakReference<PendingPut>> overagePendingQueue = new LinkedList<WeakReference<PendingPut>>();
+   /** Lock controlling access to pending put queues */
+   private final Lock pendingLock = new ReentrantLock();
+   private final ConcurrentMap<Object, Long> recentRemovals = new ConcurrentHashMap<Object, Long>();
+   /**
+    * List of recent removals. Used to ensure we don't leak memory via the recentRemovals map
+    */
+   private final List<RecentRemoval> removalsQueue = new LinkedList<RecentRemoval>();
+   /**
+    * The time when the first element in removalsQueue will expire. No reason to do housekeeping on
+    * the queue before this time.
+    */
+   private volatile long earliestRemovalTimestamp;
+   /** Lock controlling access to removalsQueue */
+   private final Lock removalsLock = new ReentrantLock();
+
+   /**
+    * The time of the last call to regionRemoved(), plus NAKED_PUT_INVALIDATION_PERIOD. All naked
+    * puts will be rejected until the current time is greater than this value.
+    */
+   private volatile long invalidationTimestamp;
+
+   /**
+    * Creates a new PutFromLoadValidator.
+    * 
+    * @param transactionManager
+    *           transaction manager to use to associated changes with a transaction; may be
+    *           <code>null</code>
+    */
+   public PutFromLoadValidator(TransactionManager transactionManager) {
+      this(transactionManager, NAKED_PUT_INVALIDATION_PERIOD, PENDING_PUT_OVERAGE_PERIOD,
+               PENDING_PUT_RECENT_PERIOD, MAX_PENDING_PUT_DELAY);
+   }
+
+   /**
+    * Constructor variant for use by unit tests; allows control of various timeouts by the test.
+    */
+   protected PutFromLoadValidator(TransactionManager transactionManager,
+            long nakedPutInvalidationPeriod, long pendingPutOveragePeriod,
+            long pendingPutRecentPeriod, long maxPendingPutDelay) {
+      this.transactionManager = transactionManager;
+      this.nakedPutInvalidationPeriod = nakedPutInvalidationPeriod;
+      this.pendingPutOveragePeriod = pendingPutOveragePeriod;
+      this.pendingPutRecentPeriod = pendingPutRecentPeriod;
+      this.maxPendingPutDelay = maxPendingPutDelay;
+   }
+
+   // ----------------------------------------------------------------- Public
+
+   public boolean isPutValid(Object key) {
+      boolean valid = false;
+      long now = System.currentTimeMillis();
+
+      PendingPutMap pending = pendingPuts.get(key);
+      if (pending != null) {
+         synchronized (pending) {
+            PendingPut toCancel = pending.remove(getOwnerForPut());
+            valid = toCancel != null;
+            if (valid) {
+               toCancel.completed = true;
+               if (pending.size() == 0) {
+                  pendingPuts.remove(key);
+               }
+            }
+         }
+      }
+
+      if (!valid) {
+         if (now > invalidationTimestamp) {
+            Long removedTime = recentRemovals.get(key);
+            if (removedTime == null || now > removedTime.longValue()) {
+               valid = true;
+            }
+         }
+      }
+
+      cleanOutdatedPendingPuts(now, true);
+
+      return valid;
+   }
+
+   public void keyRemoved(Object key) {
+      // Invalidate any pending puts
+      pendingPuts.remove(key);
+
+      // Record when this occurred to invalidate later naked puts
+      RecentRemoval removal = new RecentRemoval(key, this.nakedPutInvalidationPeriod);
+      recentRemovals.put(key, removal.timestamp);
+
+      // Don't let recentRemovals map become a memory leak
+      RecentRemoval toClean = null;
+      boolean attemptClean = removal.timestamp.longValue() > earliestRemovalTimestamp;
+      removalsLock.lock();
+      try {
+         removalsQueue.add(removal);
+
+         if (attemptClean) {
+            if (removalsQueue.size() > 1) { // we have at least one as we
+               // just added it
+               toClean = removalsQueue.remove(0);
+            }
+            earliestRemovalTimestamp = removalsQueue.get(0).timestamp.longValue();
+         }
+      } finally {
+         removalsLock.unlock();
+      }
+
+      if (toClean != null) {
+         Long cleaned = recentRemovals.get(toClean.key);
+         if (cleaned != null && cleaned.equals(toClean.timestamp)) {
+            cleaned = recentRemovals.remove(toClean.key);
+            if (cleaned != null && cleaned.equals(toClean.timestamp) == false) {
+               // Oops; removed the wrong timestamp; restore it
+               recentRemovals.putIfAbsent(toClean.key, cleaned);
+            }
+         }
+      }
+   }
+
+   public void cleared() {
+      invalidationTimestamp = System.currentTimeMillis() + this.nakedPutInvalidationPeriod;
+      pendingLock.lock();
+      try {
+         removalsLock.lock();
+         try {
+            pendingPuts.clear();
+            pendingQueue.clear();
+            overagePendingQueue.clear();
+            recentRemovals.clear();
+            removalsQueue.clear();
+            earliestRemovalTimestamp = invalidationTimestamp;
+
+         } finally {
+            removalsLock.unlock();
+         }
+      } finally {
+         pendingLock.unlock();
+      }
+   }
+
+   /**
+    * Notifies this validator that it is expected that a database read followed by a subsequent
+    * {@link #isPutValid(Object)} call will occur. The intent is this method would be called
+    * following a cache miss wherein it is expected that a database read plus cache put will occur.
+    * Calling this method allows the validator to treat the subsequent <code>isPutValid</code> as if
+    * the database read occurred when this method was invoked. This allows the validator to compare
+    * the timestamp of this call against the timestamp of subsequent removal notifications. A put
+    * that occurs without this call preceding it is "naked"; i.e the validator must assume the put
+    * is not valid if any relevant removal has occurred within
+    * {@link #NAKED_PUT_INVALIDATION_PERIOD} milliseconds.
+    * 
+    * @param key
+    *           key that will be used for subsequent put
+    */
+   public void registerPendingPut(Object key) {
+      PendingPut pendingPut = new PendingPut(key, getOwnerForPut());
+      PendingPutMap pendingForKey = new PendingPutMap();
+      synchronized (pendingForKey) {
+         for (;;) {
+            PendingPutMap existing = pendingPuts.putIfAbsent(key, pendingForKey);
+            if (existing != null && existing != pendingForKey) {
+               synchronized (existing) {
+                  existing.put(pendingPut);
+                  PendingPutMap doublecheck = pendingPuts.putIfAbsent(key, existing);
+                  if (doublecheck == null || doublecheck == existing) {
+                     break;
+                  }
+                  // else we hit a race and need to loop to try again
+               }
+            } else {
+               pendingForKey.put(pendingPut);
+               break;
+            }
+         }
+      }
+
+      // Guard against memory leaks
+      preventOutdatedPendingPuts(pendingPut);
+   }
+
+   // -------------------------------------------------------------- Protected
+
+   /** Only for use by unit tests; may be removed at any time */
+   protected int getPendingPutQueueLength() {
+      pendingLock.lock();
+      try {
+         return pendingQueue.size();
+      } finally {
+         pendingLock.unlock();
+      }
+   }
+
+   /** Only for use by unit tests; may be removed at any time */
+   protected int getOveragePendingPutQueueLength() {
+      pendingLock.lock();
+      try {
+         return overagePendingQueue.size();
+      } finally {
+         pendingLock.unlock();
+      }
+   }
+
+   /** Only for use by unit tests; may be removed at any time */
+   protected int getRemovalQueueLength() {
+      removalsLock.lock();
+      try {
+         return removalsQueue.size();
+      } finally {
+         removalsLock.unlock();
+      }
+   }
+
+   // ---------------------------------------------------------------- Private
+
+   private Object getOwnerForPut() {
+      Transaction tx = null;
+      try {
+         if (transactionManager != null) {
+            tx = transactionManager.getTransaction();
+         }
+      } catch (SystemException se) {
+         throw new CacheException("Could not obtain transaction", se);
+      }
+      return tx == null ? Thread.currentThread() : tx;
+
+   }
+
+   private void preventOutdatedPendingPuts(PendingPut pendingPut) {
+      pendingLock.lock();
+      try {
+         pendingQueue.add(new WeakReference<PendingPut>(pendingPut));
+         cleanOutdatedPendingPuts(pendingPut.timestamp, false);
+      } finally {
+         pendingLock.unlock();
+      }
+   }
+
+   private void cleanOutdatedPendingPuts(long now, boolean lock) {
+
+      PendingPut toClean = null;
+      if (lock) {
+         pendingLock.lock();
+      }
+      try {
+
+         // Clean items out of the basic queue
+
+         long overaged = now - this.pendingPutOveragePeriod;
+         long recent = now - this.pendingPutRecentPeriod;
+
+         int pos = 0;
+         while (pendingQueue.size() > pos) {
+            WeakReference<PendingPut> ref = pendingQueue.get(pos);
+            PendingPut item = ref.get();
+            if (item == null || item.completed) {
+               pendingQueue.remove(pos);
+            } else if (item.timestamp < overaged) {
+               // Potential leak; move to the overaged queued
+               pendingQueue.remove(pos);
+               overagePendingQueue.add(ref);
+            } else if (item.timestamp >= recent) {
+               // Don't waste time on very recent items
+               break;
+            } else if (pos > 2) {
+               // Don't spend too much time getting nowhere
+               break;
+            } else {
+               // Move on to the next item
+               pos++;
+            }
+         }
+
+         // Process the overage queue until we find an item to clean
+         // or an incomplete item that hasn't aged out
+         long mustCleanTime = now - this.maxPendingPutDelay;
+
+         while (overagePendingQueue.size() > 0) {
+            WeakReference<PendingPut> ref = overagePendingQueue.get(0);
+            PendingPut item = ref.get();
+            if (item == null || item.completed) {
+               overagePendingQueue.remove(0);
+            } else {
+               if (item.timestamp < mustCleanTime) {
+                  overagePendingQueue.remove(0);
+                  toClean = item;
+               }
+               break;
+            }
+         }
+      } finally {
+         if (lock) {
+            pendingLock.unlock();
+         }
+      }
+
+      // We've found a pendingPut that never happened; clean it up
+      if (toClean != null) {
+         PendingPutMap map = pendingPuts.get(toClean.key);
+         if (map != null) {
+            synchronized (map) {
+               PendingPut cleaned = map.remove(toClean.owner);
+               if (toClean.equals(cleaned) == false) {
+                  // Oops. Restore it.
+                  map.put(cleaned);
+               } else if (map.size() == 0) {
+                  pendingPuts.remove(toClean.key);
+               }
+            }
+         }
+      }
+
+   }
+
+   /**
+    * Lazy-initialization map for PendingPut. Optimized for the expected usual case where only a
+    * single put is pending for a given key.
+    * 
+    * This class is NOT THREAD SAFE. All operations on it must be performed with the object monitor
+    * held.
+    */
+   private static class PendingPutMap {
+      private PendingPut singlePendingPut;
+      private Map<Object, PendingPut> fullMap;
+
+      public void put(PendingPut pendingPut) {
+         if (singlePendingPut == null) {
+            if (fullMap == null) {
+               // initial put
+               singlePendingPut = pendingPut;
+            } else {
+               fullMap.put(pendingPut.owner, pendingPut);
+            }
+         } else {
+            // 2nd put; need a map
+            fullMap = new HashMap<Object, PendingPut>(4);
+            fullMap.put(singlePendingPut.owner, singlePendingPut);
+            singlePendingPut = null;
+            fullMap.put(pendingPut.owner, pendingPut);
+         }
+      }
+
+      public PendingPut remove(Object ownerForPut) {
+         PendingPut removed = null;
+         if (fullMap == null) {
+            if (singlePendingPut != null && singlePendingPut.owner.equals(ownerForPut)) {
+               removed = singlePendingPut;
+               singlePendingPut = null;
+            }
+         } else {
+            removed = fullMap.remove(ownerForPut);
+         }
+         return removed;
+      }
+
+      public int size() {
+         return fullMap == null ? (singlePendingPut == null ? 0 : 1) : fullMap.size();
+      }
+   }
+
+   private static class PendingPut {
+      private final Object key;
+      private final Object owner;
+      private final long timestamp = System.currentTimeMillis();
+      private volatile boolean completed;
+
+      private PendingPut(Object key, Object owner) {
+         this.key = key;
+         this.owner = owner;
+      }
+
+   }
+
+   private static class RecentRemoval {
+      private final Object key;
+      private final Long timestamp;
+
+      private RecentRemoval(Object key, long nakedPutInvalidationPeriod) {
+         this.key = key;
+         timestamp = Long.valueOf(System.currentTimeMillis() + nakedPutInvalidationPeriod);
+      }
+   }
+
+}
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
index bad6ade4d7..2fb0ba159e 100755
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
@@ -1,135 +1,160 @@
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
 
 import javax.transaction.Transaction;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.infinispan.impl.BaseRegion;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheHelper;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
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
-
+   private static final Log log = LogFactory.getLog(TransactionalAccessDelegate.class);
    protected final CacheAdapter cacheAdapter;
    protected final BaseRegion region;
+   protected final PutFromLoadValidator putValidator;
 
-   public TransactionalAccessDelegate(BaseRegion region) {
+   public TransactionalAccessDelegate(BaseRegion region, PutFromLoadValidator validator) {
       this.region = region;
       this.cacheAdapter = region.getCacheAdapter();
+      this.putValidator = validator;
    }
 
    public Object get(Object key, long txTimestamp) throws CacheException {
       if (!region.checkValid()) 
          return null;
-      return cacheAdapter.get(key);
+      Object val = cacheAdapter.get(key);
+      if (val == null)
+         putValidator.registerPendingPut(key);
+      return val;
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
-      if (!region.checkValid())
+      if (!region.checkValid()) {
+         return false;
+      }
+      if (!putValidator.isPutValid(key)) {
          return false;
+      }
       cacheAdapter.putForExternalRead(key, value);
       return true;
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
             throws CacheException {
-      return putFromLoad(key, value, txTimestamp, version);
+      boolean trace = log.isTraceEnabled();
+      if (!region.checkValid()) {
+         if (trace) log.trace("Region not valid");
+         return false;
+      }
+      if (!putValidator.isPutValid(key)) {
+         if (trace) log.trace("Put {0} not valid", key);
+         return false;
+      }
+      cacheAdapter.putForExternalRead(key, value);
+      return true;
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
       if (!region.checkValid())
          return false;
       cacheAdapter.put(key, value);
       return true;
    }
 
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
       return false;
    }
 
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
       // We update whether or not the region is valid. Other nodes
       // may have already restored the region so they need to
       // be informed of the change.
       cacheAdapter.put(key, value);
       return true;
    }
 
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
             throws CacheException {
       return false;
    }
 
    public void remove(Object key) throws CacheException {
       // We update whether or not the region is valid. Other nodes
       // may have already restored the region so they need to
       // be informed of the change.
+      putValidator.keyRemoved(key);
       cacheAdapter.remove(key);
    }
 
    public void removeAll() throws CacheException {
+      putValidator.cleared();
       cacheAdapter.clear();
    }
 
    public void evict(Object key) throws CacheException {
+      putValidator.keyRemoved(key);
       cacheAdapter.remove(key);
    }
 
    public void evictAll() throws CacheException {
+      putValidator.cleared();
       Transaction tx = region.suspend();
       try {
          CacheHelper.sendEvictAllNotification(cacheAdapter, region.getAddress());
       } finally {
          region.resume(tx);
       }
    }
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
index 35a68c9f39..8ca4eb94b0 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
@@ -1,35 +1,39 @@
 package org.hibernate.cache.infinispan.collection;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
+import org.hibernate.cache.infinispan.access.PutFromLoadValidator;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.infinispan.notifications.Listener;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 @Listener
 public class CollectionRegionImpl extends BaseTransactionalDataRegion implements CollectionRegion {
 
    public CollectionRegionImpl(CacheAdapter cacheAdapter, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
       super(cacheAdapter, name, metadata, transactionManager);
    }
 
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
       if (AccessType.READ_ONLY.equals(accessType)) {
          return new ReadOnlyAccess(this);
       } else if (AccessType.TRANSACTIONAL.equals(accessType)) {
          return new TransactionalAccess(this);
       }
       throw new CacheException("Unsupported access type [" + accessType.getName() + "]");
    }
 
+   public PutFromLoadValidator getPutFromLoadValidator() {
+      return new PutFromLoadValidator(transactionManager);
+   }
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java
index b8656b0e98..0ab37fea4a 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/ReadOnlyAccess.java
@@ -1,41 +1,41 @@
 package org.hibernate.cache.infinispan.collection;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.SoftLock;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * This defines the strategy for transactional access to collection data in a
  * Infinispan instance.
  * <p/>
  * The read-only access to a Infinispan really is still transactional, just with 
  * the extra semantic or guarantee that we will not update data.
  *
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 class ReadOnlyAccess extends TransactionalAccess {
-   private static final Logger log = LoggerFactory.getLogger(ReadOnlyAccess.class);
+   private static final Log log = LogFactory.getLog(ReadOnlyAccess.class);
 
    ReadOnlyAccess(CollectionRegionImpl region) {
       super(region);
    }
    public SoftLock lockItem(Object key, Object version) throws CacheException {
       throw new UnsupportedOperationException("Illegal attempt to edit read only item");
    }
 
    public SoftLock lockRegion() throws CacheException {
       throw new UnsupportedOperationException("Illegal attempt to edit read only region");
    }
 
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
       log.error("Illegal attempt to edit read only item");
    }
 
    public void unlockRegion(SoftLock lock) throws CacheException {
       log.error("Illegal attempt to edit read only item");
    }
 
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
index 82b89b4527..56d54b3da0 100644
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
-      this.delegate = new TransactionalAccessDelegate(region);
+      this.delegate = new TransactionalAccessDelegate(region, region.getPutFromLoadValidator());
    }
 
    public void evict(Object key) throws CacheException {
       delegate.evict(key);
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
index d0d4322cda..dc75b061a7 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
@@ -1,35 +1,39 @@
 package org.hibernate.cache.infinispan.entity;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
+import org.hibernate.cache.infinispan.access.PutFromLoadValidator;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.infinispan.notifications.Listener;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 @Listener
 public class EntityRegionImpl extends BaseTransactionalDataRegion implements EntityRegion {
 
    public EntityRegionImpl(CacheAdapter cacheAdapter, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
       super(cacheAdapter, name, metadata, transactionManager);
    }
 
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
       if (AccessType.READ_ONLY.equals(accessType)) {
          return new ReadOnlyAccess(this);
       } else if (AccessType.TRANSACTIONAL.equals(accessType)) {
          return new TransactionalAccess(this);
       }
       throw new CacheException("Unsupported access type [" + accessType.getName() + "]");
    }
 
+   public PutFromLoadValidator getPutFromLoadValidator() {
+      return new PutFromLoadValidator(transactionManager);
+   }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
index 7cbc144cfd..6f8c58770c 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
@@ -1,49 +1,49 @@
 package org.hibernate.cache.infinispan.entity;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.SoftLock;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * A specialization of {@link TransactionalAccess} that ensures we never update data. Infinispan
  * access is always transactional.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 class ReadOnlyAccess extends TransactionalAccess {
-   private static final Logger log = LoggerFactory.getLogger(ReadOnlyAccess.class);
+   private static final Log log = LogFactory.getLog(ReadOnlyAccess.class);
 
    ReadOnlyAccess(EntityRegionImpl region) {
       super(region);
    }
 
    public SoftLock lockItem(Object key, Object version) throws CacheException {
       throw new UnsupportedOperationException("Illegal attempt to edit read only item");
    }
 
    public SoftLock lockRegion() throws CacheException {
       throw new UnsupportedOperationException("Illegal attempt to edit read only item");
    }
 
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
       log.error("Illegal attempt to edit read only item");
    }
 
    public void unlockRegion(SoftLock lock) throws CacheException {
       log.error("Illegal attempt to edit read only item");
    }
 
    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
       throw new UnsupportedOperationException("Illegal attempt to edit read only item");
    }
 
    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
             throws CacheException {
       throw new UnsupportedOperationException("Illegal attempt to edit read only item");
    }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
index acb6251262..989219eb14 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
@@ -1,88 +1,88 @@
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
-      this.delegate = new TransactionalAccessDelegate(region);
+      this.delegate = new TransactionalAccessDelegate(region, region.getPutFromLoadValidator());
    }
 
    public void evict(Object key) throws CacheException {
       delegate.evict(key);
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
       return delegate.insert(key, value, version);
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
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
index af44d507b8..50b3475367 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
@@ -1,286 +1,301 @@
 package org.hibernate.cache.infinispan.impl;
 
 import java.util.Collections;
+import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.atomic.AtomicReference;
 
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.infinispan.util.AddressAdapter;
 import org.hibernate.cache.infinispan.util.AddressAdapterImpl;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.event.CacheEntryInvalidatedEvent;
 import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
 import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
 import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 
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
    private enum InvalidateState { INVALID, CLEARING, VALID };
    private static final Log log = LogFactory.getLog(BaseRegion.class);
    private final String name;
    protected final CacheAdapter cacheAdapter;
    protected final AddressAdapter address;
    protected final Set<AddressAdapter> currentView = new HashSet<AddressAdapter>();
    protected final TransactionManager transactionManager;
    protected final boolean replication;
    protected final Object invalidationMutex = new Object();
    protected final AtomicReference<InvalidateState> invalidateState = new AtomicReference<InvalidateState>(InvalidateState.VALID);
 
    public BaseRegion(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager) {
       this.cacheAdapter = cacheAdapter;
       this.name = name;
       this.transactionManager = transactionManager;
       this.replication = cacheAdapter.isClusteredReplication();
       this.address = this.cacheAdapter.getAddress();
       this.cacheAdapter.addListener(this);
    }
 
    public void start() {
       if (address != null) {
          synchronized (currentView) {
             List<AddressAdapter> view = cacheAdapter.getMembers();
             if (view != null) {
                currentView.addAll(view);
                establishInternalNodes();
             }
          }
       }
    }
 
    /**
     * Calls to this method must be done from synchronized (currentView) blocks only!!
     */
    private void establishInternalNodes() {
       Transaction tx = suspend();
       try {
          for (AddressAdapter member : currentView) {
             CacheHelper.initInternalEvict(cacheAdapter, member);
          }
       } finally {
          resume(tx);
       }
    }
 
    public String getName() {
       return name;
    }
 
    public CacheAdapter getCacheAdapter() {
       return cacheAdapter;
    }
 
    public long getElementCountInMemory() {
       if (checkValid()) {
          Set keySet = cacheAdapter.keySet();
          int size = cacheAdapter.size();
          if (CacheHelper.containsEvictAllNotification(keySet, address))
             size--;
          return size;
       }
       return 0;
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
       if (checkValid()) {
-         Map map = cacheAdapter.toMap();
-         Set keys = map.keySet();
-         for (Object key : keys) {
-            if (CacheHelper.isEvictAllNotification(key)) {
-               map.remove(key);
+         // If copying causes issues, provide a lazily loaded Map
+         Map map = new HashMap();
+         Set<Map.Entry> entries = cacheAdapter.toMap().entrySet();
+         for (Map.Entry entry : entries) {
+            Object key = entry.getKey();
+            if (!CacheHelper.isEvictAllNotification(key)) {
+               map.put(key, entry.getValue());
             }
          }
          return map;
       }
       return Collections.EMPTY_MAP;
    }
 
    public void destroy() throws CacheException {
       try {
          cacheAdapter.clear();
       } finally {
          cacheAdapter.removeListener(this);
       }
    }
-   
+
    public boolean contains(Object key) {
       if (!checkValid())
          return false;
       // Reads are non-blocking in Infinispan, so not sure of the necessity of passing ZERO_LOCK_ACQUISITION_TIMEOUT
       return cacheAdapter.withFlags(FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT).containsKey(key);
    }
 
    public AddressAdapter getAddress() {
       return address;
    }
 
    public boolean checkValid() {
       boolean valid = invalidateState.get() == InvalidateState.VALID;
       if (!valid) {
          synchronized (invalidationMutex) {
             if (invalidateState.compareAndSet(InvalidateState.INVALID, InvalidateState.CLEARING)) {
                Transaction tx = suspend();
                try {
                   cacheAdapter.withFlags(FlagAdapter.CACHE_MODE_LOCAL, FlagAdapter.ZERO_LOCK_ACQUISITION_TIMEOUT).clear();
                   invalidateState.compareAndSet(InvalidateState.CLEARING, InvalidateState.VALID);
                }
                catch (Exception e) {
                   if (log.isTraceEnabled()) {
                      log.trace("Could not invalidate region: " + e.getLocalizedMessage());
                   }
                }
                finally {
                   resume(tx);
                }
             }
          }
          valid = invalidateState.get() == InvalidateState.VALID;
       }
       
       return valid;
    }
 
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
    protected Object suspendAndGet(Object key, FlagAdapter opt, boolean suppressTimeout) throws CacheException {
        Transaction tx = suspend();
        try {
            if (suppressTimeout)
                return cacheAdapter.getAllowingTimeout(key);
            else
                return cacheAdapter.get(key);
        } finally {
            resume(tx);
        }
    }
-   
+
+   public Object getOwnerForPut() {
+      Transaction tx = null;
+      try {
+          if (transactionManager != null) {
+              tx = transactionManager.getTransaction();
+          }
+      } catch (SystemException se) {
+          throw new CacheException("Could not obtain transaction", se);
+      }
+      return tx == null ? Thread.currentThread() : tx;
+   }
+
    /**
     * Tell the TransactionManager to suspend any ongoing transaction.
     * 
     * @return the transaction that was suspended, or <code>null</code> if
     *         there wasn't one
     */
    public Transaction suspend() {
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
 
    /**
     * Tell the TransactionManager to resume the given transaction
     * 
     * @param tx
     *            the transaction to suspend. May be <code>null</code>.
     */
    public void resume(Transaction tx) {
        try {
            if (tx != null)
                transactionManager.resume(tx);
        } catch (Exception e) {
            throw new CacheException("Could not resume transaction", e);
        }
    }
 
    @CacheEntryModified
    public void entryModified(CacheEntryModifiedEvent event) {
       handleEvictAllModification(event);
    }
 
    protected boolean handleEvictAllModification(CacheEntryModifiedEvent event) {
       if (!event.isPre() && (replication || event.isOriginLocal()) && CacheHelper.isEvictAllNotification(event.getKey(), event.getValue())) {
          if (log.isTraceEnabled()) log.trace("Set invalid state because marker cache entry was put: {0}", event);
          invalidateState.set(InvalidateState.INVALID);
          return true;
       }
       return false;
    }
 
    @CacheEntryInvalidated
    public void entryInvalidated(CacheEntryInvalidatedEvent event) {
       if (log.isTraceEnabled()) log.trace("Cache entry invalidated: {0}", event);
       handleEvictAllInvalidation(event);
    }
 
    protected boolean handleEvictAllInvalidation(CacheEntryInvalidatedEvent event) {
       if (!event.isPre() && CacheHelper.isEvictAllNotification(event.getKey())) {
          if (log.isTraceEnabled()) log.trace("Set invalid state because marker cache entry was invalidated: {0}", event);
          invalidateState.set(InvalidateState.INVALID);
          return true;
       }
       return false;
    }
 
    @ViewChanged
    public void viewChanged(ViewChangedEvent event) {
       synchronized (currentView) {
          List<AddressAdapter> view = AddressAdapterImpl.toAddressAdapter(event.getNewMembers());
          if (view != null) {
             currentView.addAll(view);
             establishInternalNodes();
          }
       }
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
index 96783c5a4b..984f6c63cc 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
@@ -1,115 +1,115 @@
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
 
 import java.io.Externalizable;
 import java.io.IOException;
 import java.io.ObjectInput;
 import java.io.ObjectOutput;
 import java.util.Set;
 
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * Helper for dealing with Infinisan cache instances.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class CacheHelper {
 
-   private static final Logger log = LoggerFactory.getLogger(CacheHelper.class);
+   private static final Log log = LogFactory.getLog(CacheHelper.class);
 
    /**
     * Disallow external instantiation of CacheHelper.
     */
    private CacheHelper() {
    }
 
    public static void initInternalEvict(CacheAdapter cacheAdapter, AddressAdapter member) {
       EvictAll eKey = new EvictAll(member == null ? NoAddress.INSTANCE : member);
       cacheAdapter.withFlags(FlagAdapter.CACHE_MODE_LOCAL).put(eKey, Internal.INIT);
    }
 
    public static void sendEvictAllNotification(CacheAdapter cacheAdapter, AddressAdapter member) {
       EvictAll eKey = new EvictAll(member == null ? NoAddress.INSTANCE : member);
       cacheAdapter.put(eKey, Internal.EVICT);
    }
 
    public static boolean isEvictAllNotification(Object key) {
       return key instanceof EvictAll;
    }
 
    public static boolean containsEvictAllNotification(Set keySet, AddressAdapter member) {
       EvictAll eKey = new EvictAll(member == null ? NoAddress.INSTANCE : member);
       return keySet.contains(eKey);
    }
 
    public static boolean isEvictAllNotification(Object key, Object value) {
       return key instanceof EvictAll && value == Internal.EVICT;
    }
 
    private static class EvictAll implements Externalizable {
       AddressAdapter member;
 
       EvictAll(AddressAdapter member) {
          this.member = member;
       }
 
       @Override
       public boolean equals(Object obj) {
          if (obj == this)
             return true;
          if (!(obj instanceof EvictAll))
             return false;
          EvictAll ek = (EvictAll) obj;
          return ek.member.equals(member);
       }
 
       @Override
       public int hashCode() {
          int result = 17;
          result = 31 * result + member.hashCode();
          return result;
       }
 
       public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
          member = (AddressAdapter) in.readObject();
       }
 
       public void writeExternal(ObjectOutput out) throws IOException {
          out.writeObject(member);
       }
    }
 
    private enum NoAddress implements AddressAdapter {
       INSTANCE;
    }
 
    private enum Internal { 
       INIT, EVICT;
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java
new file mode 100644
index 0000000000..69f99cb50e
--- /dev/null
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/access/PutFromLoadValidatorUnitTestCase.java
@@ -0,0 +1,414 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009, Red Hat, Inc or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
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
+ */
+package org.hibernate.test.cache.infinispan.access;
+
+import java.util.concurrent.CountDownLatch;
+import java.util.concurrent.ExecutorService;
+import java.util.concurrent.Executors;
+import java.util.concurrent.TimeUnit;
+import java.util.concurrent.atomic.AtomicInteger;
+
+import javax.transaction.Transaction;
+import javax.transaction.TransactionManager;
+
+import org.hibernate.cache.infinispan.access.PutFromLoadValidator;
+import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaTransactionManagerImpl;
+
+import junit.framework.TestCase;
+
+/**
+ * Tests of {@link PutFromLoadValidator}.
+ * 
+ * @author Brian Stansberry
+ * @author Galder Zamarreño
+ * 
+ * @version $Revision: $
+ */
+public class PutFromLoadValidatorUnitTestCase extends TestCase {
+   private Object KEY1 = "KEY1";
+
+   private TransactionManager tm;
+
+   public PutFromLoadValidatorUnitTestCase(String name) {
+      super(name);
+   }
+
+   @Override
+   protected void setUp() throws Exception {
+      super.setUp();
+      tm = DualNodeJtaTransactionManagerImpl.getInstance("test");
+   }
+
+   @Override
+   protected void tearDown() throws Exception {
+      try {
+         super.tearDown();
+      } finally {
+         tm = null;
+         try {
+            DualNodeJtaTransactionManagerImpl.cleanupTransactions();
+         } finally {
+            DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
+         }
+      }
+   }
+
+   public void testNakedPut() throws Exception {
+      nakedPutTest(false);
+   }
+
+   public void testNakedPutTransactional() throws Exception {
+      nakedPutTest(true);
+   }
+
+   private void nakedPutTest(boolean transactional) throws Exception {
+      PutFromLoadValidator testee = new PutFromLoadValidator(transactional ? tm : null);
+      if (transactional) {
+         tm.begin();
+      }
+      assertTrue(testee.isPutValid(KEY1));
+   }
+
+   public void testRegisteredPut() throws Exception {
+      registeredPutTest(false);
+   }
+
+   public void testRegisteredPutTransactional() throws Exception {
+      registeredPutTest(true);
+   }
+
+   private void registeredPutTest(boolean transactional) throws Exception {
+      PutFromLoadValidator testee = new PutFromLoadValidator(transactional ? tm : null);
+      if (transactional) {
+         tm.begin();
+      }
+      testee.registerPendingPut(KEY1);
+      assertTrue(testee.isPutValid(KEY1));
+   }
+
+   public void testNakedPutAfterKeyRemoval() throws Exception {
+      nakedPutAfterRemovalTest(false, false);
+   }
+
+   public void testNakedPutAfterKeyRemovalTransactional() throws Exception {
+      nakedPutAfterRemovalTest(true, false);
+   }
+
+   public void testNakedPutAfterRegionRemoval() throws Exception {
+      nakedPutAfterRemovalTest(false, true);
+   }
+
+   public void testNakedPutAfterRegionRemovalTransactional() throws Exception {
+      nakedPutAfterRemovalTest(true, true);
+   }
+
+   private void nakedPutAfterRemovalTest(boolean transactional, boolean removeRegion)
+            throws Exception {
+      PutFromLoadValidator testee = new PutFromLoadValidator(transactional ? tm : null);
+      if (removeRegion) {
+         testee.cleared();
+      } else {
+         testee.keyRemoved(KEY1);
+      }
+      if (transactional) {
+         tm.begin();
+      }
+      assertFalse(testee.isPutValid(KEY1));
+
+   }
+
+   public void testRegisteredPutAfterKeyRemoval() throws Exception {
+      registeredPutAfterRemovalTest(false, false);
+   }
+
+   public void testRegisteredPutAfterKeyRemovalTransactional() throws Exception {
+      registeredPutAfterRemovalTest(true, false);
+   }
+
+   public void testRegisteredPutAfterRegionRemoval() throws Exception {
+      registeredPutAfterRemovalTest(false, true);
+   }
+
+   public void testRegisteredPutAfterRegionRemovalTransactional() throws Exception {
+      registeredPutAfterRemovalTest(true, true);
+   }
+
+   private void registeredPutAfterRemovalTest(boolean transactional, boolean removeRegion)
+            throws Exception {
+      PutFromLoadValidator testee = new PutFromLoadValidator(transactional ? tm : null);
+      if (removeRegion) {
+         testee.cleared();
+      } else {
+         testee.keyRemoved(KEY1);
+      }
+      if (transactional) {
+         tm.begin();
+      }
+      testee.registerPendingPut(KEY1);
+      assertTrue(testee.isPutValid(KEY1));
+   }
+
+   public void testRegisteredPutWithInterveningKeyRemoval() throws Exception {
+      registeredPutWithInterveningRemovalTest(false, false);
+   }
+
+   public void testRegisteredPutWithInterveningKeyRemovalTransactional() throws Exception {
+      registeredPutWithInterveningRemovalTest(true, false);
+   }
+
+   public void testRegisteredPutWithInterveningRegionRemoval() throws Exception {
+      registeredPutWithInterveningRemovalTest(false, true);
+   }
+
+   public void testRegisteredPutWithInterveningRegionRemovalTransactional() throws Exception {
+      registeredPutWithInterveningRemovalTest(true, true);
+   }
+
+   private void registeredPutWithInterveningRemovalTest(boolean transactional, boolean removeRegion)
+            throws Exception {
+      PutFromLoadValidator testee = new PutFromLoadValidator(transactional ? tm : null);
+      if (transactional) {
+         tm.begin();
+      }
+      testee.registerPendingPut(KEY1);
+      if (removeRegion) {
+         testee.cleared();
+      } else {
+         testee.keyRemoved(KEY1);
+      }
+      assertFalse(testee.isPutValid(KEY1));
+   }
+
+   public void testDelayedNakedPutAfterKeyRemoval() throws Exception {
+      delayedNakedPutAfterRemovalTest(false, false);
+   }
+
+   public void testDelayedNakedPutAfterKeyRemovalTransactional() throws Exception {
+      delayedNakedPutAfterRemovalTest(true, false);
+   }
+
+   public void testDelayedNakedPutAfterRegionRemoval() throws Exception {
+      delayedNakedPutAfterRemovalTest(false, true);
+   }
+
+   public void testDelayedNakedPutAfterRegionRemovalTransactional() throws Exception {
+      delayedNakedPutAfterRemovalTest(true, true);
+   }
+
+   private void delayedNakedPutAfterRemovalTest(boolean transactional, boolean removeRegion)
+            throws Exception {
+      PutFromLoadValidator testee = new TestValidator(transactional ? tm : null, 100, 1000, 500,
+               10000);
+      if (removeRegion) {
+         testee.cleared();
+      } else {
+         testee.keyRemoved(KEY1);
+      }
+      if (transactional) {
+         tm.begin();
+      }
+      Thread.sleep(110);
+      assertTrue(testee.isPutValid(KEY1));
+
+   }
+
+   public void testMultipleRegistrations() throws Exception {
+      multipleRegistrationtest(false);
+   }
+
+   public void testMultipleRegistrationsTransactional() throws Exception {
+      multipleRegistrationtest(true);
+   }
+
+   private void multipleRegistrationtest(final boolean transactional) throws Exception {
+      final PutFromLoadValidator testee = new PutFromLoadValidator(transactional ? tm : null);
+
+      final CountDownLatch registeredLatch = new CountDownLatch(3);
+      final CountDownLatch finishedLatch = new CountDownLatch(3);
+      final AtomicInteger success = new AtomicInteger();
+
+      Runnable r = new Runnable() {
+         public void run() {
+            try {
+               if (transactional) {
+                  tm.begin();
+               }
+               testee.registerPendingPut(KEY1);
+               registeredLatch.countDown();
+               registeredLatch.await(5, TimeUnit.SECONDS);
+               if (testee.isPutValid(KEY1)) {
+                  success.incrementAndGet();
+               }
+               finishedLatch.countDown();
+            } catch (Exception e) {
+               e.printStackTrace();
+            }
+         }
+      };
+
+      ExecutorService executor = Executors.newFixedThreadPool(3);
+
+      // Start with a removal so the "isPutValid" calls will fail if
+      // any of the concurrent activity isn't handled properly
+
+      testee.cleared();
+
+      // Do the registration + isPutValid calls
+      executor.execute(r);
+      executor.execute(r);
+      executor.execute(r);
+
+      finishedLatch.await(5, TimeUnit.SECONDS);
+
+      assertEquals("All threads succeeded", 3, success.get());
+   }
+
+   /**
+    * White box test for ensuring key removals get cleaned up.
+    * 
+    * @throws Exception
+    */
+   public void testRemovalCleanup() throws Exception {
+      TestValidator testee = new TestValidator(null, 200, 1000, 500, 10000);
+      testee.keyRemoved("KEY1");
+      testee.keyRemoved("KEY2");
+      Thread.sleep(210);
+      assertEquals(2, testee.getRemovalQueueLength());
+      testee.keyRemoved("KEY1");
+      assertEquals(2, testee.getRemovalQueueLength());
+      testee.keyRemoved("KEY2");
+      assertEquals(2, testee.getRemovalQueueLength());
+   }
+
+   /**
+    * Very much a white box test of the logic for ensuring pending put registrations get cleaned up.
+    * 
+    * @throws Exception
+    */
+   public void testPendingPutCleanup() throws Exception {
+      TestValidator testee = new TestValidator(tm, 5000, 600, 300, 900);
+
+      // Start with a regionRemoval so we can confirm at the end that all
+      // registrations have been cleaned out
+      testee.cleared();
+
+      testee.registerPendingPut("1");
+      testee.registerPendingPut("2");
+      testee.registerPendingPut("3");
+      testee.registerPendingPut("4");
+      testee.registerPendingPut("5");
+      testee.registerPendingPut("6");
+      testee.isPutValid("6");
+      testee.isPutValid("2");
+      // ppq = [1,2(c),3,4,5,6(c)]
+      assertEquals(6, testee.getPendingPutQueueLength());
+      assertEquals(0, testee.getOveragePendingPutQueueLength());
+
+      // Sleep past "pendingPutRecentPeriod"
+      Thread.sleep(310);
+      testee.registerPendingPut("7");
+      // White box -- should have cleaned out 2 (completed) but
+      // not gotten to 6 (also removed)
+      // ppq = [1,3,4,5,6(c),7]
+      assertEquals(0, testee.getOveragePendingPutQueueLength());
+      assertEquals(6, testee.getPendingPutQueueLength());
+
+      // Sleep past "pendingPutOveragePeriod"
+      Thread.sleep(310);
+      testee.registerPendingPut("8");
+      // White box -- should have cleaned out 6 (completed) and
+      // moved 1, 3, 4 and 5 to overage queue
+      // oppq = [1,3,4,5] ppq = [7,8]
+      assertEquals(4, testee.getOveragePendingPutQueueLength());
+      assertEquals(2, testee.getPendingPutQueueLength());
+
+      // Sleep past "maxPendingPutDelay"
+      Thread.sleep(310);
+      testee.isPutValid("3");
+      // White box -- should have cleaned out 1 (overage) and
+      // moved 7 to overage queue
+      // oppq = [3(c),4,5,7] ppq=[8]
+      assertEquals(4, testee.getOveragePendingPutQueueLength());
+      assertEquals(1, testee.getPendingPutQueueLength());
+
+      // Sleep past "maxPendingPutDelay"
+      Thread.sleep(310);
+      tm.begin();
+      testee.registerPendingPut("7");
+      Transaction tx = tm.suspend();
+
+      // White box -- should have cleaned out 3 (completed)
+      // and 4 (overage) and moved 8 to overage queue
+      // We now have 5,7,8 in overage and 7tx in pending
+      // oppq = [5,7,8] ppq=[7tx]
+      assertEquals(3, testee.getOveragePendingPutQueueLength());
+      assertEquals(1, testee.getPendingPutQueueLength());
+
+      // Validate that only expected items can do puts, thus indirectly
+      // proving the others have been cleaned out of pendingPuts map
+      assertFalse(testee.isPutValid("1"));
+      // 5 was overage, so should have been cleaned
+      assertEquals(2, testee.getOveragePendingPutQueueLength());
+      assertFalse(testee.isPutValid("2"));
+      // 7 was overage, so should have been cleaned
+      assertEquals(1, testee.getOveragePendingPutQueueLength());
+      assertFalse(testee.isPutValid("3"));
+      assertFalse(testee.isPutValid("4"));
+      assertFalse(testee.isPutValid("5"));
+      assertFalse(testee.isPutValid("6"));
+      assertFalse(testee.isPutValid("7"));
+      assertTrue(testee.isPutValid("8"));
+      tm.resume(tx);
+      assertTrue(testee.isPutValid("7"));
+   }
+
+   private static class TestValidator extends PutFromLoadValidator {
+
+      protected TestValidator(TransactionManager transactionManager,
+               long nakedPutInvalidationPeriod, long pendingPutOveragePeriod,
+               long pendingPutRecentPeriod, long maxPendingPutDelay) {
+         super(transactionManager, nakedPutInvalidationPeriod, pendingPutOveragePeriod,
+                  pendingPutRecentPeriod, maxPendingPutDelay);
+      }
+
+      @Override
+      public int getOveragePendingPutQueueLength() {
+         // TODO Auto-generated method stub
+         return super.getOveragePendingPutQueueLength();
+      }
+
+      @Override
+      public int getPendingPutQueueLength() {
+         // TODO Auto-generated method stub
+         return super.getPendingPutQueueLength();
+      }
+
+      @Override
+      public int getRemovalQueueLength() {
+         // TODO Auto-generated method stub
+         return super.getRemovalQueueLength();
+      }
+
+   }
+}
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
new file mode 100644
index 0000000000..248093ae30
--- /dev/null
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
@@ -0,0 +1,96 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
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
+ */
+package org.hibernate.test.cache.infinispan.collection;
+
+import java.util.Properties;
+
+import org.hibernate.cache.CacheDataDescription;
+import org.hibernate.cache.CacheException;
+import org.hibernate.cache.CollectionRegion;
+import org.hibernate.cache.Region;
+import org.hibernate.cache.RegionFactory;
+import org.hibernate.cache.access.AccessType;
+import org.hibernate.cache.access.CollectionRegionAccessStrategy;
+import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cache.infinispan.util.CacheAdapter;
+import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
+import org.hibernate.test.cache.infinispan.AbstractEntityCollectionRegionTestCase;
+
+/**
+ * Tests of CollectionRegionImpl.
+ * 
+ * @author Galder Zamarreño
+ */
+public class CollectionRegionImplTestCase extends AbstractEntityCollectionRegionTestCase {
+
+   public CollectionRegionImplTestCase(String name) {
+      super(name);
+   }
+
+   @Override
+   protected void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties) {
+      CollectionRegion region = regionFactory.buildCollectionRegion("test", properties, null);
+      assertNull("Got TRANSACTIONAL", region.buildAccessStrategy(AccessType.TRANSACTIONAL)
+               .lockRegion());
+      try {
+         region.buildAccessStrategy(AccessType.READ_ONLY).lockRegion();
+         fail("Did not get READ_ONLY");
+      } catch (UnsupportedOperationException good) {
+      }
+
+      try {
+         region.buildAccessStrategy(AccessType.NONSTRICT_READ_WRITE);
+         fail("Incorrectly got NONSTRICT_READ_WRITE");
+      } catch (CacheException good) {
+      }
+
+      try {
+         region.buildAccessStrategy(AccessType.READ_WRITE);
+         fail("Incorrectly got READ_WRITE");
+      } catch (CacheException good) {
+      }
+   }
+
+   @Override
+   protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
+      return regionFactory.buildCollectionRegion(regionName, properties, cdd);
+   }
+
+   @Override
+   protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
+      return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache(InfinispanRegionFactory.DEF_ENTITY_RESOURCE));
+   }
+
+   @Override
+   protected void putInRegion(Region region, Object key, Object value) {
+      CollectionRegionAccessStrategy strategy = ((CollectionRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL);
+      strategy.putFromLoad(key, value, System.currentTimeMillis(), new Integer(1));
+   }
+
+   @Override
+   protected void removeFromRegion(Region region, Object key) {
+      ((CollectionRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).remove(key);
+   }
+
+}
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
index d3071caf0c..ab39901fe7 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
@@ -1,688 +1,686 @@
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
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.util.ComparableComparator;
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
    protected CacheAdapter localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
    protected CacheAdapter remoteCache;
 
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
 
       localCache = ((BaseRegion) localEntityRegion).getCacheAdapter();
 
       invalidation = localCache.isClusteredInvalidation();
       synchronous = localCache.isSynchronous();
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteEntityRegion = remoteRegionFactory.buildEntityRegion(REGION_NAME, remoteCfg
                .getProperties(), getCacheDataDescription());
       remoteAccessStrategy = remoteEntityRegion.buildAccessStrategy(getAccessType());
 
       remoteCache = ((BaseRegion) remoteEntityRegion).getCacheAdapter();
 
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
          localCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
          remoteCache.withFlags(FlagAdapter.CACHE_MODE_LOCAL).clear();
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
-      localAccessStrategy.get(KEY, System.currentTimeMillis());
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
-      remoteAccessStrategy.get(KEY, System.currentTimeMillis());
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
       final String KEY = KEY_BASE + testCount++;
       assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       if (evict)
          localAccessStrategy.evict(KEY);
       else
          localAccessStrategy.remove(KEY);
 
       assertEquals(null, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
    }
 
    private void evictOrRemoveAllTest(boolean evict) {
       final String KEY = KEY_BASE + testCount++;
       assertEquals(0, getValidKeyCount(localCache.keySet()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
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
 
       if (evict) {
          log.debug("Call evict all locally");
          localAccessStrategy.evictAll();
       } else {
          localAccessStrategy.removeAll();
       }
 
       // This should re-establish the region root node in the optimistic case
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(localCache.keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(remoteCache.keySet()));
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(1, getValidKeyCount(remoteCache.keySet()));
 
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
index 62d019f4ff..7f93396961 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
@@ -1,132 +1,131 @@
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
 
 import junit.framework.AssertionFailedError;
 
 import org.hibernate.cache.access.AccessType;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 
 /**
  * Base class for tests of TRANSACTIONAL access.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractTransactionalAccessTestCase extends AbstractEntityRegionAccessStrategyTestCase {
 
    public AbstractTransactionalAccessTestCase(String name) {
       super(name);
    }
 
    @Override
    protected AccessType getAccessType() {
       return AccessType.TRANSACTIONAL;
    }
 
    public void testContestedPutFromLoad() throws Exception {
 
       final String KEY = KEY_BASE + testCount++;
 
-      localAccessStrategy.get(KEY, System.currentTimeMillis());
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
 
       final CountDownLatch pferLatch = new CountDownLatch(1);
       final CountDownLatch pferCompletionLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(1);
 
       Thread blocker = new Thread("Blocker") {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                assertEquals("Correct initial value", VALUE1, localAccessStrategy.get(KEY,
                         txTimestamp));
 
                localAccessStrategy.update(KEY, VALUE2, new Integer(2), new Integer(1));
 
                pferLatch.countDown();
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
 
       Thread putter = new Thread("Putter") {
 
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, new Integer(1));
 
                BatchModeTransactionManager.getInstance().commit();
             } catch (Exception e) {
                log.error("node1 caught exception", e);
                node1Exception = e;
                rollback();
             } catch (AssertionFailedError e) {
                node1Failure = e;
                rollback();
             } finally {
                pferCompletionLatch.countDown();
             }
          }
       };
 
       blocker.start();
       assertTrue("Active tx has done an update", pferLatch.await(1, TimeUnit.SECONDS));
       putter.start();
       assertTrue("putFromLoadreturns promtly", pferCompletionLatch.await(10, TimeUnit.MILLISECONDS));
 
       commitLatch.countDown();
 
       assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
 
       assertThreadsRanCleanly();
 
       long txTimestamp = System.currentTimeMillis();
       assertEquals("Correct node1 value", VALUE2, localAccessStrategy.get(KEY, txTimestamp));
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
index 60849d61df..f0ee1ab2ea 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
@@ -1,102 +1,95 @@
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
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.test.cache.infinispan.AbstractEntityCollectionRegionTestCase;
 
 /**
  * Tests of EntityRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class EntityRegionImplTestCase extends AbstractEntityCollectionRegionTestCase {
-    
-    public EntityRegionImplTestCase(String name) {
-        super(name);
-    } 
-    
-    @Override
-    protected void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties) {
-        
-        EntityRegion region = regionFactory.buildEntityRegion("test", properties, null);
-        
-        assertNull("Got TRANSACTIONAL", region.buildAccessStrategy(AccessType.TRANSACTIONAL).lockRegion());
-        
-        try
-        {
-            region.buildAccessStrategy(AccessType.READ_ONLY).lockRegion();
-            fail("Did not get READ_ONLY");
-        }
-        catch (UnsupportedOperationException good) {}
-        
-        try
-        {
-            region.buildAccessStrategy(AccessType.NONSTRICT_READ_WRITE);
-            fail("Incorrectly got NONSTRICT_READ_WRITE");
-        }
-        catch (CacheException good) {}
-        
-        try
-        {
-            region.buildAccessStrategy(AccessType.READ_WRITE);
-            fail("Incorrectly got READ_WRITE");
-        }
-        catch (CacheException good) {}      
-        
-    }
 
-    @Override
-    protected void putInRegion(Region region, Object key, Object value) {
-        ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).insert(key, value, new Integer(1));
-    }
+   public EntityRegionImplTestCase(String name) {
+      super(name);
+   }
+
+   @Override
+   protected void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties) {
+      EntityRegion region = regionFactory.buildEntityRegion("test", properties, null);
+      assertNull("Got TRANSACTIONAL", region.buildAccessStrategy(AccessType.TRANSACTIONAL)
+               .lockRegion());
+      try {
+         region.buildAccessStrategy(AccessType.READ_ONLY).lockRegion();
+         fail("Did not get READ_ONLY");
+      } catch (UnsupportedOperationException good) {
+      }
+
+      try {
+         region.buildAccessStrategy(AccessType.NONSTRICT_READ_WRITE);
+         fail("Incorrectly got NONSTRICT_READ_WRITE");
+      } catch (CacheException good) {
+      }
+
+      try {
+         region.buildAccessStrategy(AccessType.READ_WRITE);
+         fail("Incorrectly got READ_WRITE");
+      } catch (CacheException good) {
+      }
+   }
 
-    @Override
-    protected void removeFromRegion(Region region, Object key) {
-        ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).remove(key);
-    }
+   @Override
+   protected void putInRegion(Region region, Object key, Object value) {
+      ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).insert(key, value, new Integer(1));
+   }
+
+   @Override
+   protected void removeFromRegion(Region region, Object key) {
+      ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).remove(key);
+   }
 
    @Override
-   protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName,
-            Properties properties, CacheDataDescription cdd) {
+   protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildEntityRegion(regionName, properties, cdd);
    }
 
    @Override
    protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
-      return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("entity"));
+      return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache(InfinispanRegionFactory.DEF_ENTITY_RESOURCE));
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
deleted file mode 100644
index 4f4687f506..0000000000
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
+++ /dev/null
@@ -1,38 +0,0 @@
-package org.hibernate.test.cache.infinispan.functional;
-
-import java.util.Map;
-
-import org.hibernate.junit.functional.FunctionalTestCase;
-import org.hibernate.stat.SecondLevelCacheStatistics;
-import org.hibernate.stat.Statistics;
-
-/**
- * @author Galder Zamarreño
- * @since 3.5
- */
-public abstract class AbstractFunctionalTestCase extends FunctionalTestCase {
-   private final String cacheConcurrencyStrategy;
-
-   public AbstractFunctionalTestCase(String string, String cacheConcurrencyStrategy) {
-      super(string);
-      this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
-   }
-
-   public String[] getMappings() {
-      return new String[] { "cache/infinispan/functional/Item.hbm.xml" };
-   }
-
-   @Override
-   public String getCacheConcurrencyStrategy() {
-      return cacheConcurrencyStrategy;
-   }
-
-   public void testEmptySecondLevelCacheEntry() throws Exception {
-      getSessions().getCache().evictEntityRegion(Item.class.getName());
-      Statistics stats = getSessions().getStatistics();
-      stats.clear();
-      SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
-      Map cacheEntries = statistics.getEntries();
-      assertEquals(0, cacheEntries.size());
-  }
-}
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java
index 1656db7504..50c6fdfa6f 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicReadOnlyTestCase.java
@@ -1,13 +1,18 @@
 package org.hibernate.test.cache.infinispan.functional;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
-public class BasicReadOnlyTestCase extends AbstractFunctionalTestCase {
+public class BasicReadOnlyTestCase extends SingleNodeTestCase {
 
    public BasicReadOnlyTestCase(String string) {
-      super(string, "read-only");
+      super(string);
+   }
+
+   @Override
+   public String getCacheConcurrencyStrategy() {
+      return "read-only";
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
index 2ed3d95ae6..da0a96614b 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/BasicTransactionalTestCase.java
@@ -1,191 +1,290 @@
 package org.hibernate.test.cache.infinispan.functional;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cache.entry.CacheEntry;
+import org.hibernate.cfg.Configuration;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.Statistics;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * @author Galder Zamarreño
  * @since 3.5
  */
-public class BasicTransactionalTestCase extends AbstractFunctionalTestCase {
+public class BasicTransactionalTestCase extends SingleNodeTestCase {
+   private static final Log log = LogFactory.getLog(BasicTransactionalTestCase.class);
 
    public BasicTransactionalTestCase(String string) {
-      super(string, "transactional");
+      super(string);
    }
 
-   public void testEntityCache() {
+   @Override
+   public void configure(Configuration cfg) {
+      super.configure(cfg);
+   }
+
+   public void testEntityCache() throws Exception {
       Item item = new Item("chris", "Chris's Item");
+      beginTx();
+      try {
+         Session s = openSession();
+         s.getTransaction().begin();
+         s.persist(item);
+         s.getTransaction().commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
 
-      Session s = openSession();
-      Statistics stats = s.getSessionFactory().getStatistics();
-      s.getTransaction().begin();
-      s.persist(item);
-      s.getTransaction().commit();
-      s.close();
-
-      s = openSession();
-      Item found = (Item) s.load(Item.class, item.getId());
-      System.out.println(stats);
-      assertEquals(item.getDescription(), found.getDescription());
-      assertEquals(0, stats.getSecondLevelCacheMissCount());
-      assertEquals(1, stats.getSecondLevelCacheHitCount());
-      s.delete(found);
-      s.close();
+      beginTx();
+      try {
+         Session s = openSession();
+         Item found = (Item) s.load(Item.class, item.getId());
+         Statistics stats = s.getSessionFactory().getStatistics();
+         log.info(stats.toString());
+         assertEquals(item.getDescription(), found.getDescription());
+         assertEquals(0, stats.getSecondLevelCacheMissCount());
+         assertEquals(1, stats.getSecondLevelCacheHitCount());
+         s.delete(found);
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
    }
-   
-   public void testCollectionCache() {
+
+   public void testCollectionCache() throws Exception {
       Item item = new Item("chris", "Chris's Item");
       Item another = new Item("another", "Owned Item");
       item.addItem(another);
 
-      Session s = openSession();
-      s.getTransaction().begin();
-      s.persist(item);
-      s.persist(another);
-      s.getTransaction().commit();
-      s.close();
-
-      s = openSession();
-      Statistics stats = s.getSessionFactory().getStatistics();
-      Item loaded = (Item) s.load(Item.class, item.getId());
-      assertEquals(1, loaded.getItems().size());
-      s.close();
-
-      s = openSession();
-      SecondLevelCacheStatistics cStats = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
-      Item loadedWithCachedCollection = (Item) s.load(Item.class, item.getId());
-      stats.logSummary();
-      assertEquals(item.getName(), loadedWithCachedCollection.getName());
-      assertEquals(item.getItems().size(), loadedWithCachedCollection.getItems().size());
-      assertEquals(1, cStats.getHitCount());
-      Map cacheEntries = cStats.getEntries();
-      assertEquals(1, cacheEntries.size());
-      s.close();
+      beginTx();
+      try {
+         Session s = openSession();
+         s.getTransaction().begin();
+         s.persist(item);
+         s.persist(another);
+         s.getTransaction().commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+
+      beginTx();
+      try {
+         Session s = openSession();
+         Item loaded = (Item) s.load(Item.class, item.getId());
+         assertEquals(1, loaded.getItems().size());
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+
+      beginTx();
+      try {
+         Session s = openSession();
+         Statistics stats = s.getSessionFactory().getStatistics();
+         SecondLevelCacheStatistics cStats = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
+         Item loadedWithCachedCollection = (Item) s.load(Item.class, item.getId());
+         stats.logSummary();
+         assertEquals(item.getName(), loadedWithCachedCollection.getName());
+         assertEquals(item.getItems().size(), loadedWithCachedCollection.getItems().size());
+         assertEquals(1, cStats.getHitCount());
+         Map cacheEntries = cStats.getEntries();
+         assertEquals(1, cacheEntries.size());
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
    }
 
-   public void testStaleWritesLeaveCacheConsistent() {
-      Session s = openSession();
-      Transaction txn = s.beginTransaction();
-      VersionedItem item = new VersionedItem();
-      item.setName("steve");
-      item.setDescription("steve's item");
-      s.save(item);
-      txn.commit();
-      s.close();
+   public void testStaleWritesLeaveCacheConsistent() throws Exception {
+      VersionedItem item = null;
+      Transaction txn = null;
+      Session s = null;
+      beginTx();
+      try {
+         s = openSession();
+         txn = s.beginTransaction();
+         item = new VersionedItem();
+         item.setName("steve");
+         item.setDescription("steve's item");
+         s.save(item);
+         txn.commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
 
       Long initialVersion = item.getVersion();
 
       // manually revert the version property
       item.setVersion(new Long(item.getVersion().longValue() - 1));
 
+      beginTx();
       try {
-          s = openSession();
-          txn = s.beginTransaction();
-          s.update(item);
-          txn.commit();
-          s.close();
-          fail("expected stale write to fail");
-      } catch (Throwable expected) {
-          // expected behavior here
-          if (txn != null) {
-              try {
-                  txn.rollback();
-              } catch (Throwable ignore) {
-              }
-          }
+         s = openSession();
+         txn = s.beginTransaction();
+         s.update(item);
+         txn.commit();
+         fail("expected stale write to fail");
+      } catch (Exception e) {
+         setRollbackOnlyTxExpected(e);
       } finally {
-          if (s != null && s.isOpen()) {
-              try {
-                  s.close();
-              } catch (Throwable ignore) {
-              }
-          }
+         commitOrRollbackTx();
+         if (s != null && s.isOpen()) {
+            try {
+               s.close();
+            } catch (Throwable ignore) {
+            }
+         }
       }
 
       // check the version value in the cache...
       SecondLevelCacheStatistics slcs = sfi().getStatistics().getSecondLevelCacheStatistics(VersionedItem.class.getName());
 
       Object entry = slcs.getEntries().get(item.getId());
       Long cachedVersionValue;
       cachedVersionValue = (Long) ((CacheEntry) entry).getVersion();
       assertEquals(initialVersion.longValue(), cachedVersionValue.longValue());
 
-      // cleanup
-      s = openSession();
-      txn = s.beginTransaction();
-      item = (VersionedItem) s.load(VersionedItem.class, item.getId());
-      s.delete(item);
-      txn.commit();
-      s.close();
+      beginTx();
+      try {
+         // cleanup
+         s = openSession();
+         txn = s.beginTransaction();
+         item = (VersionedItem) s.load(VersionedItem.class, item.getId());
+         s.delete(item);
+         txn.commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
   }
 
-   public void testQueryCacheInvalidation() {
-      Session s = openSession();
-      Transaction t = s.beginTransaction();
-      Item i = new Item();
-      i.setName("widget");
-      i.setDescription("A really top-quality, full-featured widget.");
-      s.persist(i);
-      t.commit();
-      s.close();
+   public void testQueryCacheInvalidation() throws Exception {
+      Session s = null;
+      Transaction t = null;
+      Item i = null;
+      
+      beginTx();
+      try {
+         s = openSession();
+         t = s.beginTransaction();
+         i = new Item();
+         i.setName("widget");
+         i.setDescription("A really top-quality, full-featured widget.");
+         s.persist(i);
+         t.commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
 
       SecondLevelCacheStatistics slcs = s.getSessionFactory().getStatistics().getSecondLevelCacheStatistics(Item.class.getName());
 
       assertEquals(slcs.getPutCount(), 1);
       assertEquals(slcs.getElementCountInMemory(), 1);
       assertEquals(slcs.getEntries().size(), 1);
 
-      s = openSession();
-      t = s.beginTransaction();
-      i = (Item) s.get(Item.class, i.getId());
-
-      assertEquals(slcs.getHitCount(), 1);
-      assertEquals(slcs.getMissCount(), 0);
-
-      i.setDescription("A bog standard item");
-
-      t.commit();
-      s.close();
+      beginTx();
+      try {
+         s = openSession();
+         t = s.beginTransaction();
+         i = (Item) s.get(Item.class, i.getId());
+         assertEquals(slcs.getHitCount(), 1);
+         assertEquals(slcs.getMissCount(), 0);
+         i.setDescription("A bog standard item");
+         t.commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
 
       assertEquals(slcs.getPutCount(), 2);
 
       CacheEntry entry = (CacheEntry) slcs.getEntries().get(i.getId());
       Serializable[] ser = entry.getDisassembledState();
       assertTrue(ser[0].equals("widget"));
       assertTrue(ser[1].equals("A bog standard item"));
       
-      // cleanup
-      s = openSession();
-      t = s.beginTransaction();
-      s.delete(i);
-      t.commit();
-      s.close();
+      beginTx();
+      try {
+         // cleanup
+         s = openSession();
+         t = s.beginTransaction();
+         s.delete(i);
+         t.commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
    }
    
-   public void testQueryCache() {
+   public void testQueryCache() throws Exception {
+      Session s = null;
       Item item = new Item("chris", "Chris's Item");
+      
+      beginTx();
+      try {
+         s = openSession();
+         s.getTransaction().begin();
+         s.persist(item);
+         s.getTransaction().commit();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+
+      beginTx();
+      try {
+         s = openSession();
+         s.createQuery("from Item").setCacheable(true).list();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
 
-      Session s = openSession();
-      s.getTransaction().begin();
-      s.persist(item);
-      s.getTransaction().commit();
-      s.close();
-
-      s = openSession();
-      s.createQuery("from Item").setCacheable(true).list();
-      s.close();
-
-      s = openSession();
-      Statistics stats = s.getSessionFactory().getStatistics();
-      s.createQuery("from Item").setCacheable(true).list();
-      assertEquals(1, stats.getQueryCacheHitCount());
-      s.createQuery("delete from Item").executeUpdate();
-      s.close();
+      beginTx();
+      try {
+         s = openSession();
+         Statistics stats = s.getSessionFactory().getStatistics();
+         s.createQuery("from Item").setCacheable(true).list();
+         assertEquals(1, stats.getQueryCacheHitCount());
+         s.createQuery("delete from Item").executeUpdate();
+         s.close();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
new file mode 100644
index 0000000000..4e11450ce8
--- /dev/null
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/ConcurrentWriteTest.java
@@ -0,0 +1,513 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
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
+ */
+package org.hibernate.test.cache.infinispan.functional;
+
+import java.io.PrintWriter;
+import java.io.StringWriter;
+import java.util.ArrayList;
+import java.util.HashSet;
+import java.util.List;
+import java.util.Random;
+import java.util.Set;
+import java.util.concurrent.Callable;
+import java.util.concurrent.CyclicBarrier;
+import java.util.concurrent.ExecutorService;
+import java.util.concurrent.Executors;
+import java.util.concurrent.Future;
+import java.util.concurrent.TimeUnit;
+
+import javax.transaction.TransactionManager;
+
+import org.hibernate.FlushMode;
+import org.hibernate.Session;
+import org.hibernate.cache.RegionFactory;
+import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.connection.ConnectionProvider;
+import org.hibernate.stat.SecondLevelCacheStatistics;
+import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeTestCase;
+import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeConnectionProviderImpl;
+import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaTransactionManagerImpl;
+import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeTransactionManagerLookup;
+import org.hibernate.transaction.TransactionManagerLookup;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
+
+/**
+ * 
+ * @author nikita_tovstoles@mba.berkeley.edu
+ * @author Galder Zamarreño
+ */
+public class ConcurrentWriteTest extends SingleNodeTestCase {
+   private static final Log log = LogFactory.getLog(ConcurrentWriteTest.class);
+
+   /**
+    * when USER_COUNT==1, tests pass, when >4 tests fail
+    */
+   private static final int USER_COUNT = 5;
+   private static final int ITERATION_COUNT = 150;
+   private static final int THINK_TIME_MILLIS = 10;
+   private static final long LAUNCH_INTERVAL_MILLIS = 10;
+   private static final Random random = new Random();
+
+   /**
+    * kill switch used to stop all users when one fails
+    */
+   private static volatile boolean TERMINATE_ALL_USERS = false;
+
+   /**
+    * collection of IDs of all customers participating in this test
+    */
+   private Set<Integer> customerIDs = new HashSet<Integer>();
+
+   private TransactionManager tm;
+
+   public ConcurrentWriteTest(String x) {
+      super(x);
+   }
+
+   @Override
+   protected TransactionManager getTransactionManager() {
+      return DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.LOCAL);
+   }
+
+   @Override
+   protected Class<? extends RegionFactory> getCacheRegionFactory() {
+      return InfinispanRegionFactory.class;
+   }
+
+   @Override
+   protected Class<? extends ConnectionProvider> getConnectionProviderClass() {
+       return DualNodeConnectionProviderImpl.class;
+   }
+
+   @Override
+   protected Class<? extends TransactionManagerLookup> getTransactionManagerLookupClass() {
+       return DualNodeTransactionManagerLookup.class;
+   }
+
+   /**
+    * test that DB can be queried
+    * 
+    * @throws java.lang.Exception
+    */
+   public void testPingDb() throws Exception {
+      try {
+         beginTx();
+         getEnvironment().getSessionFactory().getCurrentSession().createQuery("from " + Customer.class.getName()).list();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+//         setRollbackOnly();
+//         fail("failed to query DB; exception=" + e);
+      } finally {
+         commitOrRollbackTx();
+      }
+   }
+
+   @Override
+   protected void prepareTest() throws Exception {
+      super.prepareTest();
+      TERMINATE_ALL_USERS = false;
+   }
+
+   @Override
+   protected void cleanupTest() throws Exception {
+      try {
+         super.cleanupTest();
+      } finally {
+         cleanup();
+         // DualNodeJtaTransactionManagerImpl.cleanupTransactions();
+         // DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
+      }
+   }
+
+   @Override
+   public void configure(Configuration cfg) {
+      super.configure(cfg);
+      cfg.setProperty(DualNodeTestCase.NODE_ID_PROP, DualNodeTestCase.LOCAL);
+   }
+
+   @Override
+   protected boolean getUseQueryCache() {
+      return true;
+   }
+
+   public void testSingleUser() throws Exception {
+      // setup
+      Customer customer = createCustomer(0);
+      final Integer customerId = customer.getId();
+      getCustomerIDs().add(customerId);
+
+      assertNull("contact exists despite not being added", getFirstContact(customerId));
+
+      // check that cache was hit
+      SecondLevelCacheStatistics customerSlcs = getEnvironment().getSessionFactory()
+               .getStatistics().getSecondLevelCacheStatistics(Customer.class.getName());
+      assertEquals(customerSlcs.getPutCount(), 1);
+      assertEquals(customerSlcs.getElementCountInMemory(), 1);
+      assertEquals(customerSlcs.getEntries().size(), 1);
+
+      SecondLevelCacheStatistics contactsCollectionSlcs = getEnvironment().getSessionFactory()
+               .getStatistics().getSecondLevelCacheStatistics(Customer.class.getName() + ".contacts");
+      assertEquals(1, contactsCollectionSlcs.getPutCount());
+      assertEquals(1, contactsCollectionSlcs.getElementCountInMemory());
+      assertEquals(1, contactsCollectionSlcs.getEntries().size());
+
+      final Contact contact = addContact(customerId);
+      assertNotNull("contact returned by addContact is null", contact);
+      assertEquals("Customer.contacts cache was not invalidated after addContact", 0,
+               contactsCollectionSlcs.getElementCountInMemory());
+
+      assertNotNull("Contact missing after successful add call", getFirstContact(customerId));
+
+      // read everyone's contacts
+      readEveryonesFirstContact();
+
+      removeContact(customerId);
+      assertNull("contact still exists after successful remove call", getFirstContact(customerId));
+
+   }
+
+//   /**
+//    * TODO: This will fail until ISPN-??? has been fixed.
+//    * 
+//    * @throws Exception
+//    */
+//   public void testManyUsers() throws Throwable {
+//      try {
+//         // setup - create users
+//         for (int i = 0; i < USER_COUNT; i++) {
+//            Customer customer = createCustomer(0);
+//            getCustomerIDs().add(customer.getId());
+//         }
+//         assertEquals("failed to create enough Customers", USER_COUNT, getCustomerIDs().size());
+//
+//         final ExecutorService executor = Executors.newFixedThreadPool(USER_COUNT);
+//
+//         CyclicBarrier barrier = new CyclicBarrier(USER_COUNT + 1);
+//         List<Future<Void>> futures = new ArrayList<Future<Void>>(USER_COUNT);
+//         for (Integer customerId : getCustomerIDs()) {
+//            Future<Void> future = executor.submit(new UserRunner(customerId, barrier));
+//            futures.add(future);
+//            Thread.sleep(LAUNCH_INTERVAL_MILLIS); // rampup
+//         }
+////         barrier.await(); // wait for all threads to be ready
+//         barrier.await(45, TimeUnit.SECONDS); // wait for all threads to finish
+//         log.info("All threads finished, let's shutdown the executor and check whether any exceptions were reported");
+//         for (Future<Void> future : futures) future.get();
+//         log.info("All future gets checked");
+//      } catch (Throwable t) {
+//         log.error("Error running test", t);
+//         throw t;
+//      }
+//   }
+
+   public void cleanup() throws Exception {
+      getCustomerIDs().clear();
+      String deleteContactHQL = "delete from Contact";
+      String deleteCustomerHQL = "delete from Customer";
+      beginTx();
+      try {
+         Session session = getEnvironment().getSessionFactory().getCurrentSession();
+         session.createQuery(deleteContactHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
+         session.createQuery(deleteCustomerHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+   }
+
+   private Customer createCustomer(int nameSuffix) throws Exception {
+      Customer customer = null;
+      beginTx();
+      try {
+         customer = new Customer();
+         customer.setName("customer_" + nameSuffix);
+         customer.setContacts(new HashSet<Contact>());
+         getEnvironment().getSessionFactory().getCurrentSession().persist(customer);
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+      return customer;
+   }
+
+   /**
+    * read first contact of every Customer participating in this test. this forces concurrent cache
+    * writes of Customer.contacts Collection cache node
+    * 
+    * @return who cares
+    * @throws java.lang.Exception
+    */
+   private void readEveryonesFirstContact() throws Exception {
+      beginTx();
+      try {
+         for (Integer customerId : getCustomerIDs()) {
+            if (TERMINATE_ALL_USERS) {
+               setRollbackOnlyTx();
+               return;
+            }
+            Customer customer = (Customer) getEnvironment().getSessionFactory().getCurrentSession().load(Customer.class, customerId);
+            Set<Contact> contacts = customer.getContacts();
+            if (!contacts.isEmpty()) {
+               contacts.iterator().next();
+            }
+         }
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+   }
+
+   /**
+    * -load existing Customer -get customer's contacts; return 1st one
+    * 
+    * @param customerId
+    * @return first Contact or null if customer has none
+    */
+   private Contact getFirstContact(Integer customerId) throws Exception {
+      assert customerId != null;
+      Contact firstContact = null;
+      beginTx();
+      try {
+         final Customer customer = (Customer) getEnvironment().getSessionFactory()
+                  .getCurrentSession().load(Customer.class, customerId);
+         Set<Contact> contacts = customer.getContacts();
+         firstContact = contacts.isEmpty() ? null : contacts.iterator().next();
+         if (TERMINATE_ALL_USERS)
+            setRollbackOnlyTx();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+      return firstContact;
+   }
+
+   /**
+    * -load existing Customer -create a new Contact and add to customer's contacts
+    * 
+    * @param customerId
+    * @return added Contact
+    */
+   private Contact addContact(Integer customerId) throws Exception {
+      assert customerId != null;
+      Contact contact = null;
+      beginTx();
+      try {
+         final Customer customer = (Customer) getEnvironment().getSessionFactory()
+                  .getCurrentSession().load(Customer.class, customerId);
+         contact = new Contact();
+         contact.setName("contact name");
+         contact.setTlf("wtf is tlf?");
+         contact.setCustomer(customer);
+         customer.getContacts().add(contact);
+         // assuming contact is persisted via cascade from customer
+         if (TERMINATE_ALL_USERS)
+            setRollbackOnlyTx();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+      return contact;
+   }
+
+   /**
+    * remove existing 'contact' from customer's list of contacts
+    * 
+    * @param contact
+    *           contact to remove from customer's contacts
+    * @param customerId
+    * @throws IllegalStateException
+    *            if customer does not own a contact
+    */
+   private void removeContact(Integer customerId) throws Exception {
+      assert customerId != null;
+
+      beginTx();
+      try {
+         Customer customer = (Customer) getEnvironment().getSessionFactory().getCurrentSession()
+                  .load(Customer.class, customerId);
+         Set<Contact> contacts = customer.getContacts();
+         if (contacts.size() != 1) {
+            throw new IllegalStateException("can't remove contact: customer id=" + customerId
+                     + " expected exactly 1 contact, " + "actual count=" + contacts.size());
+         }
+
+         Contact contact = contacts.iterator().next();
+         contacts.remove(contact);
+         contact.setCustomer(null);
+
+         // explicitly delete Contact because hbm has no 'DELETE_ORPHAN' cascade?
+         // getEnvironment().getSessionFactory().getCurrentSession().delete(contact); //appears to
+         // not be needed
+
+         // assuming contact is persisted via cascade from customer
+
+         if (TERMINATE_ALL_USERS)
+            setRollbackOnlyTx();
+      } catch (Exception e) {
+         setRollbackOnlyTx(e);
+      } finally {
+         commitOrRollbackTx();
+      }
+   }
+
+   /**
+    * @return the customerIDs
+    */
+   public Set<Integer> getCustomerIDs() {
+      return customerIDs;
+   }
+
+   private String statusOfRunnersToString(Set<UserRunner> runners) {
+      assert runners != null;
+
+      StringBuilder sb = new StringBuilder("TEST CONFIG [userCount=" + USER_COUNT
+               + ", iterationsPerUser=" + ITERATION_COUNT + ", thinkTimeMillis="
+               + THINK_TIME_MILLIS + "] " + " STATE of UserRunners: ");
+
+      for (UserRunner r : runners) {
+         sb.append(r.toString() + System.getProperty("line.separator"));
+      }
+      return sb.toString();
+   }
+
+   class UserRunner implements Callable<Void> {
+      private final CyclicBarrier barrier;
+      final private Integer customerId;
+      private int completedIterations = 0;
+      private Throwable causeOfFailure;
+
+      public UserRunner(Integer cId, CyclicBarrier barrier) {
+         assert cId != null;
+         this.customerId = cId;
+         this.barrier = barrier;
+      }
+
+      private boolean contactExists() throws Exception {
+         return getFirstContact(customerId) != null;
+      }
+
+      public Void call() throws Exception {
+         // name this thread for easier log tracing
+         Thread.currentThread().setName("UserRunnerThread-" + getCustomerId());
+         log.info("Wait for all executions paths to be ready to perform calls");
+         try {
+//            barrier.await();
+            for (int i = 0; i < ITERATION_COUNT && !TERMINATE_ALL_USERS; i++) {
+               if (contactExists())
+                  throw new IllegalStateException("contact already exists before add, customerId=" + customerId);
+               addContact(customerId);
+               thinkRandomTime();
+               if (!contactExists())
+                  throw new IllegalStateException("contact missing after successful add, customerId=" + customerId);
+               thinkRandomTime();
+               // read everyone's contacts
+               readEveryonesFirstContact();
+               thinkRandomTime();
+               removeContact(customerId);
+               if (contactExists())
+                  throw new IllegalStateException("contact still exists after successful remove call, customerId=" + customerId);
+               thinkRandomTime();
+               ++completedIterations;
+               if (log.isTraceEnabled()) log.trace("Iteration completed {0}", completedIterations);
+            }
+         } catch (Throwable t) {
+            TERMINATE_ALL_USERS = true;
+            log.error("Error", t);
+            throw new Exception(t);
+            // rollback current transaction if any
+            // really should not happen since above methods all follow begin-commit-rollback pattern
+            // try {
+            // if
+            // (DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestUtil.LOCAL).getTransaction()
+            // != null) {
+            // DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestUtil.LOCAL).rollback();
+            // }
+            // } catch (SystemException ex) {
+            // throw new RuntimeException("failed to rollback tx", ex);
+            // }
+         } finally {
+            log.info("Wait for all execution paths to finish");
+            barrier.await();
+         }
+         return null;
+      }
+
+      public boolean isSuccess() {
+         return ITERATION_COUNT == getCompletedIterations();
+      }
+
+      public int getCompletedIterations() {
+         return completedIterations;
+      }
+
+      public Throwable getCauseOfFailure() {
+         return causeOfFailure;
+      }
+
+      public Integer getCustomerId() {
+         return customerId;
+      }
+
+      @Override
+      public String toString() {
+         return super.toString() + "[customerId=" + getCustomerId() + " iterationsCompleted="
+                  + getCompletedIterations() + " completedAll=" + isSuccess() + " causeOfFailure="
+                  + (this.causeOfFailure != null ? getStackTrace(causeOfFailure) : "") + "] ";
+      }
+   }
+
+   public static String getStackTrace(Throwable throwable) {
+      StringWriter sw = new StringWriter();
+      PrintWriter pw = new PrintWriter(sw, true);
+      throwable.printStackTrace(pw);
+      return sw.getBuffer().toString();
+   }
+
+   /**
+    * sleep between 0 and THINK_TIME_MILLIS.
+    * 
+    * @throws RuntimeException
+    *            if sleep is interrupted or TERMINATE_ALL_USERS flag was set to true i n the
+    *            meantime
+    */
+   private void thinkRandomTime() {
+      try {
+         Thread.sleep(random.nextInt(THINK_TIME_MILLIS));
+      } catch (InterruptedException ex) {
+         throw new RuntimeException("sleep interrupted", ex);
+      }
+
+      if (TERMINATE_ALL_USERS) {
+         throw new RuntimeException("told to terminate (because a UserRunner had failed)");
+      }
+   }
+
+}
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
new file mode 100644
index 0000000000..df89b91a57
--- /dev/null
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/SingleNodeTestCase.java
@@ -0,0 +1,121 @@
+package org.hibernate.test.cache.infinispan.functional;
+
+import java.util.Map;
+
+import javax.transaction.Status;
+import javax.transaction.TransactionManager;
+
+import org.hibernate.cache.RegionFactory;
+import org.hibernate.cache.infinispan.InfinispanRegionFactory;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.connection.ConnectionProvider;
+import org.hibernate.junit.functional.FunctionalTestCase;
+import org.hibernate.stat.SecondLevelCacheStatistics;
+import org.hibernate.stat.Statistics;
+import org.hibernate.transaction.CMTTransactionFactory;
+import org.hibernate.transaction.TransactionFactory;
+import org.hibernate.transaction.TransactionManagerLookup;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
+
+/**
+ * @author Galder Zamarreño
+ * @since 3.5
+ */
+public abstract class SingleNodeTestCase extends FunctionalTestCase {
+   private static final Log log = LogFactory.getLog(SingleNodeTestCase.class);
+   private final TransactionManager tm;
+
+   public SingleNodeTestCase(String string) {
+      super(string);
+      tm = getTransactionManager();
+   }
+
+   protected TransactionManager getTransactionManager() {
+      try {
+         return getTransactionManagerLookupClass().newInstance().getTransactionManager(null);
+      } catch (Exception e) {
+         log.error("Error", e);
+         throw new RuntimeException(e);
+      }
+   }
+
+   
+   public String[] getMappings() {
+      return new String[] { 
+               "cache/infinispan/functional/Item.hbm.xml", 
+               "cache/infinispan/functional/Customer.hbm.xml", 
+               "cache/infinispan/functional/Contact.hbm.xml"};
+   }
+
+   @Override
+   public String getCacheConcurrencyStrategy() {
+      return "transactional";
+   }
+
+   protected Class<? extends RegionFactory> getCacheRegionFactory() {
+      return InfinispanRegionFactory.class;
+   }
+
+   protected Class<? extends TransactionFactory> getTransactionFactoryClass() {
+      return CMTTransactionFactory.class;
+   }
+
+   protected Class<? extends ConnectionProvider> getConnectionProviderClass() {
+      return org.hibernate.test.cache.infinispan.tm.XaConnectionProvider.class;
+   }
+
+   protected Class<? extends TransactionManagerLookup> getTransactionManagerLookupClass() {
+      return org.hibernate.test.cache.infinispan.tm.XaTransactionManagerLookup.class;
+   }
+
+   protected boolean getUseQueryCache() {
+      return true;
+   }
+
+   public void configure(Configuration cfg) {
+      super.configure(cfg);
+      cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
+      cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
+      cfg.setProperty(Environment.USE_QUERY_CACHE, String.valueOf(getUseQueryCache()));
+      cfg.setProperty(Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName());
+      cfg.setProperty(Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName());
+      cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass().getName());
+      cfg.setProperty(Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName());
+   }
+
+   public void testEmptySecondLevelCacheEntry() throws Exception {
+      getSessions().getCache().evictEntityRegion(Item.class.getName());
+      Statistics stats = getSessions().getStatistics();
+      stats.clear();
+      SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
+      Map cacheEntries = statistics.getEntries();
+      assertEquals(0, cacheEntries.size());
+   }
+
+   protected void beginTx() throws Exception {
+      tm.begin();
+   }
+
+   protected void setRollbackOnlyTx() throws Exception {
+      tm.setRollbackOnly();
+   }
+
+   protected void setRollbackOnlyTx(Exception e) throws Exception {
+      log.error("Error", e);
+      tm.setRollbackOnly();
+      throw e;
+   }
+
+   protected void setRollbackOnlyTxExpected(Exception e) throws Exception {
+      log.debug("Expected behaivour", e);
+      tm.setRollbackOnly();
+   }
+
+   protected void commitOrRollbackTx() throws Exception {
+      if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+      else tm.rollback();
+   }
+   
+}
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
index 587f81a6ec..e3e83f41f1 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
@@ -1,341 +1,372 @@
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
 
+import javax.transaction.Status;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.FlushMode;
+import org.hibernate.cache.RegionFactory;
+import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.classic.Session;
+import org.hibernate.connection.ConnectionProvider;
 import org.hibernate.junit.functional.FunctionalTestCase;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.test.cache.infinispan.functional.Contact;
 import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.hibernate.transaction.CMTTransactionFactory;
+import org.hibernate.transaction.TransactionFactory;
 import org.hibernate.transaction.TransactionManagerLookup;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * BulkOperationsTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class BulkOperationsTestCase extends FunctionalTestCase {
-
-   private static final Logger log = LoggerFactory.getLogger(BulkOperationsTestCase.class);
+   private static final Log log = LogFactory.getLog(BulkOperationsTestCase.class);
 
    private TransactionManager tm;
 
    public BulkOperationsTestCase(String string) {
       super(string);
    }
 
    public String[] getMappings() {
       return new String[] { "cache/infinispan/functional/Contact.hbm.xml",
                "cache/infinispan/functional/Customer.hbm.xml" };
    }
 
    @Override
    public String getCacheConcurrencyStrategy() {
       return "transactional";
    }
 
-   protected Class getTransactionFactoryClass() {
+   protected Class<? extends RegionFactory> getCacheRegionFactory() {
+      return InfinispanRegionFactory.class;
+   }
+
+   protected Class<? extends TransactionFactory> getTransactionFactoryClass() {
       return CMTTransactionFactory.class;
    }
 
-   protected Class getConnectionProviderClass() {
+   protected Class<? extends ConnectionProvider> getConnectionProviderClass() {
       return org.hibernate.test.cache.infinispan.tm.XaConnectionProvider.class;
    }
 
    protected Class<? extends TransactionManagerLookup> getTransactionManagerLookupClass() {
       return org.hibernate.test.cache.infinispan.tm.XaTransactionManagerLookup.class;
    }
 
    public void configure(Configuration cfg) {
       super.configure(cfg);
-
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
       cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
       cfg.setProperty(Environment.USE_QUERY_CACHE, "false");
+      cfg.setProperty(Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName());
       cfg.setProperty(Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName());
-      cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass()
-               .getName());
-
-      Class transactionFactory = getTransactionFactoryClass();
-      cfg.setProperty(Environment.TRANSACTION_STRATEGY, transactionFactory.getName());
+      cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass().getName());
+      cfg.setProperty(Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName());
    }
 
    public void testBulkOperations() throws Throwable {
-      System.out.println("*** testBulkOperations()");
+      log.info("*** testBulkOperations()");
       boolean cleanedUp = false;
       try {
          tm = getTransactionManagerLookupClass().newInstance().getTransactionManager(null);
 
          createContacts();
 
          List<Integer> rhContacts = getContactsByCustomer("Red Hat");
          assertNotNull("Red Hat contacts exist", rhContacts);
          assertEquals("Created expected number of Red Hat contacts", 10, rhContacts.size());
 
          SecondLevelCacheStatistics contactSlcs = getEnvironment().getSessionFactory()
                   .getStatistics().getSecondLevelCacheStatistics(Contact.class.getName());
          assertEquals(20, contactSlcs.getElementCountInMemory());
 
          assertEquals("Deleted all Red Hat contacts", 10, deleteContacts());
          assertEquals(0, contactSlcs.getElementCountInMemory());
 
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
          assertEquals(0, contactSlcs.getElementCountInMemory());
          for (Integer id : jbContacts) {
             Contact contact = getContact(id);
             assertNotNull("JBoss contact " + id + " exists", contact);
             String expected = ("Kabir".equals(contact.getName())) ? "Updated" : "2222";
             assertEquals("JBoss contact " + id + " has correct TLF", expected, contact.getTlf());
          }
 
          List<Integer> updated = getContactsByTLF("Updated");
          assertNotNull("Got updated contacts", updated);
          assertEquals("Updated contacts", 5, updated.size());
 
          updateContactsWithOneManual("Kabir", "UpdatedAgain");
          assertEquals(contactSlcs.getElementCountInMemory(), 0);
          for (Integer id : jbContacts) {
             Contact contact = getContact(id);
             assertNotNull("JBoss contact " + id + " exists", contact);
             String expected = ("Kabir".equals(contact.getName())) ? "UpdatedAgain" : "2222";
             assertEquals("JBoss contact " + id + " has correct TLF", expected, contact.getTlf());
          }
 
          updated = getContactsByTLF("UpdatedAgain");
          assertNotNull("Got updated contacts", updated);
          assertEquals("Updated contacts", 5, updated.size());
       } catch (Throwable t) {
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
-         for (int i = 0; i < 10; i++)
-            createCustomer(i);
-         tm.commit();
+         for (int i = 0; i < 10; i++) createCustomer(i);
       } catch (Exception e) {
          log.error("Unable to create customer", e);
-         tm.rollback();
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+         else tm.rollback();
       }
    }
 
    public int deleteContacts() throws Exception {
       String deleteHQL = "delete Contact where customer in ";
       deleteHQL += " (select customer FROM Customer as customer ";
       deleteHQL += " where customer.name = :cName)";
 
       tm.begin();
       try {
-
          Session session = getSessions().getCurrentSession();
          int rowsAffected = session.createQuery(deleteHQL).setFlushMode(FlushMode.AUTO)
                   .setParameter("cName", "Red Hat").executeUpdate();
          tm.commit();
          return rowsAffected;
       } catch (Exception e) {
-         try {
-            tm.rollback();
-         } catch (Exception ee) {
-            // ignored
-         }
+         log.error("Unable to delete contac", e);
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) {
+            tm.commit();
+         } else {
+            try {
+               tm.rollback();
+            } catch (Exception ee) {
+               // ignored
+            }
+         }
       }
    }
 
    public List<Integer> getContactsByCustomer(String customerName) throws Exception {
       String selectHQL = "select contact.id from Contact contact";
       selectHQL += " where contact.customer.name = :cName";
 
       log.debug("Get contacts for customer " + customerName);
       tm.begin();
       try {
 
          Session session = getSessions().getCurrentSession();
          List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter(
                   "cName", customerName).list();
-         tm.commit();
          return results;
       } catch (Exception e) {
-         tm.rollback();
+         log.error("Unable to get contacts by customer", e);
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+         else tm.rollback();
       }
    }
 
    public List<Integer> getContactsByTLF(String tlf) throws Exception {
       String selectHQL = "select contact.id from Contact contact";
       selectHQL += " where contact.tlf = :cTLF";
 
       tm.begin();
       try {
-
          Session session = getSessions().getCurrentSession();
          List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter(
                   "cTLF", tlf).list();
-         tm.commit();
          return results;
       } catch (Exception e) {
-         tm.rollback();
+         log.error("Unable to get contacts", e);
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+         else tm.rollback();
       }
    }
 
    public int updateContacts(String name, String newTLF) throws Exception {
       String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
       tm.begin();
       try {
          Session session = getSessions().getCurrentSession();
          int rowsAffected = session.createQuery(updateHQL).setFlushMode(FlushMode.AUTO)
                   .setParameter("cNewTLF", newTLF).setParameter("cName", name).executeUpdate();
-         tm.commit();
          return rowsAffected;
       } catch (Exception e) {
-         tm.rollback();
+         log.error("Unable to update contacts", e);
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+         else tm.rollback();
       }
    }
 
    public int updateContactsWithOneManual(String name, String newTLF) throws Exception {
       String queryHQL = "from Contact c where c.name = :cName";
       String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
       tm.begin();
       try {
          Session session = getSessions().getCurrentSession();
          @SuppressWarnings("unchecked")
          List<Contact> list = session.createQuery(queryHQL).setParameter("cName", name).list();
          list.get(0).setTlf(newTLF);
          int rowsAffected = session.createQuery(updateHQL).setFlushMode(FlushMode.AUTO)
                   .setParameter("cNewTLF", newTLF).setParameter("cName", name).executeUpdate();
-         tm.commit();
          return rowsAffected;
       } catch (Exception e) {
-         tm.rollback();
+         log.error("Unable to update contacts with one manual", e);
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+         else tm.rollback();
       }
    }
 
    public Contact getContact(Integer id) throws Exception {
       tm.begin();
       try {
-
          Session session = getSessions().getCurrentSession();
          Contact contact = (Contact) session.get(Contact.class, id);
-         tm.commit();
          return contact;
       } catch (Exception e) {
-         tm.rollback();
+         log.error("Unable to get contact", e);
+         tm.setRollbackOnly();
          throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) tm.commit();
+         else tm.rollback();
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
-         tm.commit();
       } catch (Exception e) {
-         if (!ignore) {
-            try {
-               tm.rollback();
-            } catch (Exception ee) {
-               // ignored
+         log.error("Unable to get contact", e);
+         tm.setRollbackOnly();
+         throw e;
+      } finally {
+         if (tm.getStatus() == Status.STATUS_ACTIVE) {
+            tm.commit();
+         } else {
+            if (!ignore) {
+               try {
+                  tm.rollback();
+               } catch (Exception ee) {
+                  // ignored
+               }
             }
-            throw e;
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
 
          return customer;
       } finally {
          System.out.println("CREATE CUSTOMER " + id + " -  END");
       }
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java
index 392fc0c7c8..9cce572637 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/ClassLoaderTestDAO.java
@@ -1,295 +1,295 @@
 /*
  * JBoss, Home of Professional Open Source.
  * Copyright 2006, Red Hat, Inc. and/or it's affiliates, and individual contributors
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
 
 import java.lang.reflect.Method;
 import java.util.Iterator;
 import java.util.List;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.Query;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * Comment
  * 
  * @author Brian Stansberry
  */
 public class ClassLoaderTestDAO {
-   private static final Logger log = LoggerFactory.getLogger(ClassLoaderTestDAO.class);
+   private static final Log log = LogFactory.getLog(ClassLoaderTestDAO.class);
 
    private SessionFactory sessionFactory;
    private TransactionManager tm;
 
    private Class acctClass;
    private Class holderClass;
    private Method setId;
    private Method setBalance;
    private Method setBranch;
    private Method setHolder;
    private Object smith;
    private Object jones;
    private Object barney;
    private Method setName;
    private Method setSsn;
 
    public ClassLoaderTestDAO(SessionFactory factory, TransactionManager tm) throws Exception {
       this.sessionFactory = factory;
       this.tm = tm;
 
       acctClass = Thread.currentThread().getContextClassLoader().loadClass(
                getClass().getPackage().getName() + ".Account");
       holderClass = Thread.currentThread().getContextClassLoader().loadClass(
                getClass().getPackage().getName() + ".AccountHolder");
       setId = acctClass.getMethod("setId", Integer.class);
       setBalance = acctClass.getMethod("setBalance", Integer.class);
       setBranch = acctClass.getMethod("setBranch", String.class);
       setHolder = acctClass.getMethod("setAccountHolder", holderClass);
 
       setName = holderClass.getMethod("setLastName", String.class);
       setSsn = holderClass.getMethod("setSsn", String.class);
 
       smith = holderClass.newInstance();
       setName.invoke(smith, "Smith");
       setSsn.invoke(smith, "1000");
 
       jones = holderClass.newInstance();
       setName.invoke(jones, "Jones");
       setSsn.invoke(jones, "2000");
 
       barney = holderClass.newInstance();
       setName.invoke(barney, "Barney");
       setSsn.invoke(barney, "3000");
    }
 
    public Object getSmith() {
       return smith;
    }
 
    public Object getJones() {
       return jones;
    }
 
    public Object getBarney() {
       return barney;
    }
 
    public void updateAccountBranch(Integer id, String branch) throws Exception {
       log.debug("Updating account " + id + " to branch " + branch);
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          Object account = session.get(acctClass, id);
          log.debug("Set branch " + branch);
          setBranch.invoke(account, branch);
          session.update(account);
          tm.commit();
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
       log.debug("Updated account " + id + " to branch " + branch);
    }
 
    public int getCountForBranch(String branch, boolean useRegion) throws Exception {
       tm.begin();
       try {
          Query query = sessionFactory.getCurrentSession().createQuery(
                   "select account from Account as account where account.branch = :branch");
          query.setString("branch", branch);
          if (useRegion) {
             query.setCacheRegion("AccountRegion");
          }
          query.setCacheable(true);
          int result = query.list().size();
          tm.commit();
          return result;
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
 
    }
 
    public void createAccount(Object holder, Integer id, Integer openingBalance, String branch) throws Exception {
       log.debug("Creating account " + id);
       tm.begin();
       try {
          Object account = acctClass.newInstance();
          setId.invoke(account, id);
          setHolder.invoke(account, holder);
          setBalance.invoke(account, openingBalance);
          log.debug("Set branch " + branch);
          setBranch.invoke(account, branch);
          sessionFactory.getCurrentSession().persist(account);
          tm.commit();
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
 
       log.debug("Created account " + id);
    }
 
    public Account getAccount(Integer id) throws Exception {
       log.debug("Getting account " + id);
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          Account acct = (Account) session.get(acctClass, id);
          tm.commit();
          return acct;
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
    }
 
    public Account getAccountWithRefresh(Integer id) throws Exception {
       log.debug("Getting account " + id + " with refresh");
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          Account acct = (Account) session.get(acctClass, id);
          session.refresh(acct);
          acct = (Account) session.get(acctClass, id);
          tm.commit();
          return acct;
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
    }
 
    public void updateAccountBalance(Integer id, Integer newBalance) throws Exception {
       log.debug("Updating account " + id + " to balance " + newBalance);
       tm.begin();
       try {
          Session session = sessionFactory.getCurrentSession();
          Object account = session.get(acctClass, id);
          setBalance.invoke(account, newBalance);
          session.update(account);
          tm.commit();
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
       log.debug("Updated account " + id + " to balance " + newBalance);
    }
 
    public String getBranch(Object holder, boolean useRegion) throws Exception {
       tm.begin();
       try {
          Query query = sessionFactory.getCurrentSession().createQuery(
                   "select account.branch from Account as account where account.accountHolder = ?");
          query.setParameter(0, holder);
          if (useRegion) {
             query.setCacheRegion("AccountRegion");
          }
          query.setCacheable(true);
          String result = (String) query.list().get(0);
          tm.commit();
          return result;
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
    }
 
    public int getTotalBalance(Object holder, boolean useRegion) throws Exception {
       List results = null;
       tm.begin();
       try {
          Query query = sessionFactory.getCurrentSession().createQuery(
                   "select account.balance from Account as account where account.accountHolder = ?");
          query.setParameter(0, holder);
          if (useRegion) {
             query.setCacheRegion("AccountRegion");
          }
          query.setCacheable(true);
          results = query.list();
          tm.commit();
       } catch (Exception e) {
          log.error("rolling back", e);
          tm.rollback();
          throw e;
       }
 
       int total = 0;
       if (results != null) {
          for (Iterator it = results.iterator(); it.hasNext();) {
             total += ((Integer) it.next()).intValue();
             System.out.println("Total = " + total);
          }
       }
       return total;
    }
 
    public void cleanup() throws Exception {
       internalCleanup();
    }
 
    private void internalCleanup() throws Exception {
       if (sessionFactory != null) {
          tm.begin();
          try {
 
             Session session = sessionFactory.getCurrentSession();
             Query query = session.createQuery("select account from Account as account");
             List accts = query.list();
             if (accts != null) {
                for (Iterator it = accts.iterator(); it.hasNext();) {
                   try {
                      Object acct = it.next();
                      log.info("Removing " + acct);
                      session.delete(acct);
                   } catch (Exception ignored) {
                   }
                }
             }
             tm.commit();
          } catch (Exception e) {
             tm.rollback();
             throw e;
          }
       }
    }
 
    public void remove() {
       try {
          internalCleanup();
       } catch (Exception e) {
          log.error("Caught exception in remove", e);
       }
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedClassLoaderTest.java
index 310f4ebec4..2ed9356486 100644
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
-import org.hibernate.test.cache.infinispan.functional.cluster.AbstractDualNodeTestCase;
+import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeTestCase;
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
-public class IsolatedClassLoaderTest extends AbstractDualNodeTestCase {
+public class IsolatedClassLoaderTest extends DualNodeTestCase {
 
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
-      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
+      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.LOCAL);
       Cache localReplicatedCache = localManager.getCache("replicated-entity");
 
       // Bind a listener to the "remote" cache
-      CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.REMOTE);
+      CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.REMOTE);
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
-      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
+      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.LOCAL);
       localQueryCache = localManager.getCache("replicated-query");
       localQueryListener = new CacheAccessListener();
       localQueryCache.addListener(localQueryListener);
 
-      TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.LOCAL);
+      TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.LOCAL);
 
       // Bind a listener to the "remote" cache
-      CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.REMOTE);
+      CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.REMOTE);
       remoteQueryCache = remoteManager.getCache("replicated-query");
       remoteQueryListener = new CacheAccessListener();
       remoteQueryCache.addListener(remoteQueryListener);
 
-      TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.REMOTE);
+      TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.REMOTE);
 
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
 
       assertEquals("Query cache used", 1, remoteQueryListener.getSawRegionModificationCount());
       remoteQueryListener.clearSawRegionModification();
 
       // Do query again from node 1
       log.info("Repeat first query (get count for branch + " + branch + " ) on remote node");
       assertEquals("63088 has correct # of accounts", 6, dao1.getCountForBranch(branch, useNamedRegion));
       assertEquals("Query cache used", 1, remoteQueryListener.getSawRegionModificationCount());
       remoteQueryListener.clearSawRegionModification();
 
       sleep(SLEEP_TIME);
 
       assertEquals("Query cache used", 1, localQueryListener.getSawRegionModificationCount());
       localQueryListener.clearSawRegionModification();
 
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java
index e42e10a2c8..6bf014ed56 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/ClusterAwareRegionFactory.java
@@ -1,123 +1,123 @@
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
 
 import java.util.Hashtable;
 import java.util.Properties;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.TimestampsRegion;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Settings;
 import org.infinispan.manager.CacheManager;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * ClusterAwareRegionFactory.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class ClusterAwareRegionFactory implements RegionFactory {
    
-   private static final Logger log = LoggerFactory.getLogger(ClusterAwareRegionFactory.class);
+   private static final Log log = LogFactory.getLog(ClusterAwareRegionFactory.class);
    private static final Hashtable<String, CacheManager> cacheManagers = new Hashtable<String, CacheManager>();
 
    private final InfinispanRegionFactory delegate = new InfinispanRegionFactory();
    private String cacheManagerName;
    private boolean locallyAdded;
    
    public ClusterAwareRegionFactory(Properties props) {
    }
    
    public static CacheManager getCacheManager(String name) {
       return cacheManagers.get(name);
    }
    
    public static void addCacheManager(String name, CacheManager manager) {
       cacheManagers.put(name, manager);
    }
    
    public static void clearCacheManagers() {
       for (CacheManager manager : cacheManagers.values()) {
          try {
             manager.stop();
          } catch (Exception e) {
             log.error("Exception cleaning up CacheManager " + manager, e);
          }
       }
       cacheManagers.clear();      
    }
 
    public void start(Settings settings, Properties properties) throws CacheException {
-      cacheManagerName = properties.getProperty(AbstractDualNodeTestCase.NODE_ID_PROP);
+      cacheManagerName = properties.getProperty(DualNodeTestCase.NODE_ID_PROP);
       
       CacheManager existing = getCacheManager(cacheManagerName);
       locallyAdded = (existing == null);
       
       if (locallyAdded) {
          delegate.start(settings, properties);
          cacheManagers.put(cacheManagerName, delegate.getCacheManager());
       } else {
          delegate.setCacheManager(existing);
       }      
    }
 
    public void stop() {
       if (locallyAdded) cacheManagers.remove(cacheManagerName);     
       delegate.stop();
    }
 
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties,
             CacheDataDescription metadata) throws CacheException {
       return delegate.buildCollectionRegion(regionName, properties, metadata);
    }
 
    public EntityRegion buildEntityRegion(String regionName, Properties properties,
             CacheDataDescription metadata) throws CacheException {
       return delegate.buildEntityRegion(regionName, properties, metadata);
    }
 
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties)
             throws CacheException {
       return delegate.buildQueryResultsRegion(regionName, properties);
    }
 
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties)
             throws CacheException {
       return delegate.buildTimestampsRegion(regionName, properties);
    }
 
    public boolean isMinimalPutsEnabledByDefault() {
       return delegate.isMinimalPutsEnabledByDefault();
    }
 
    public long nextTimestamp() {
       return delegate.nextTimestamp();
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
index 7472e42f4b..e6c735d793 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeConnectionProviderImpl.java
@@ -1,85 +1,85 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.connection.ConnectionProvider;
 import org.hibernate.connection.ConnectionProviderFactory;
 
 /**
  * A {@link ConnectionProvider} implementation adding JTA-style transactionality around the returned
  * connections using the {@link DualNodeJtaTransactionManagerImpl}.
  * 
  * @author Brian Stansberry
  */
 public class DualNodeConnectionProviderImpl implements ConnectionProvider {
    private static ConnectionProvider actualConnectionProvider = ConnectionProviderFactory.newConnectionProvider();
    private String nodeId;
    private boolean isTransactional;
 
    public static ConnectionProvider getActualConnectionProvider() {
       return actualConnectionProvider;
    }
 
    public void configure(Properties props) throws HibernateException {
-      nodeId = props.getProperty(AbstractDualNodeTestCase.NODE_ID_PROP);
+      nodeId = props.getProperty(DualNodeTestCase.NODE_ID_PROP);
       if (nodeId == null)
-         throw new HibernateException(AbstractDualNodeTestCase.NODE_ID_PROP + " not configured");
+         throw new HibernateException(DualNodeTestCase.NODE_ID_PROP + " not configured");
    }
 
    public Connection getConnection() throws SQLException {
       DualNodeJtaTransactionImpl currentTransaction = DualNodeJtaTransactionManagerImpl
                .getInstance(nodeId).getCurrentTransaction();
       if (currentTransaction == null) {
          isTransactional = false;
          return actualConnectionProvider.getConnection();
       } else {
          isTransactional = true;
          Connection connection = currentTransaction.getEnlistedConnection();
          if (connection == null) {
             connection = actualConnectionProvider.getConnection();
             currentTransaction.enlistConnection(connection);
          }
          return connection;
       }
    }
 
    public void closeConnection(Connection conn) throws SQLException {
       if (!isTransactional) {
          conn.close();
       }
    }
 
    public void close() throws HibernateException {
       actualConnectionProvider.close();
    }
 
    public boolean supportsAggressiveRelease() {
       return true;
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java
index 09cfe0b08c..953df8c6b4 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionImpl.java
@@ -1,253 +1,253 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
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
 
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * SimpleJtaTransactionImpl variant that works with DualNodeTransactionManagerImpl.
  * 
  * @author Brian Stansberry
  */
 public class DualNodeJtaTransactionImpl implements Transaction {
-   private static final Logger log = LoggerFactory.getLogger(DualNodeJtaTransactionImpl.class);
+   private static final Log log = LogFactory.getLog(DualNodeJtaTransactionImpl.class);
 
    private int status;
    private LinkedList synchronizations;
    private Connection connection; // the only resource we care about is jdbc connection
    private final DualNodeJtaTransactionManagerImpl jtaTransactionManager;
    private List<XAResource> enlistedResources = new ArrayList<XAResource>();
    private Xid xid = new DualNodeJtaTransactionXid();
 
    public DualNodeJtaTransactionImpl(DualNodeJtaTransactionManagerImpl jtaTransactionManager) {
       this.jtaTransactionManager = jtaTransactionManager;
       this.status = Status.STATUS_ACTIVE;
    }
 
    public int getStatus() {
       return status;
    }
 
    public void commit() throws RollbackException, HeuristicMixedException,
             HeuristicRollbackException, IllegalStateException, SystemException {
 
       if (status == Status.STATUS_MARKED_ROLLBACK) {
          log.trace("on commit, status was marked for rollback-only");
          rollback();
       } else {
          status = Status.STATUS_PREPARING;
          
          for (int i = 0; i < synchronizations.size(); i++) {
             Synchronization s = (Synchronization) synchronizations.get(i);
             s.beforeCompletion();
          }
          
          if (!runXaResourcePrepare()) {
             status = Status.STATUS_ROLLING_BACK;
          } else {
             status = Status.STATUS_PREPARED;
          }
 
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
          
 
          runXaResourceCommitTx();
 
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
       status = Status.STATUS_ROLLING_BACK;
       runXaResourceRollback();
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
 
       if (synchronizations != null) {
          for (int i = 0; i < synchronizations.size(); i++) {
             Synchronization s = (Synchronization) synchronizations.get(i);
             s.afterCompletion(status);
          }
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
 
    public boolean enlistResource(XAResource xaResource) throws RollbackException,
             IllegalStateException, SystemException {
       enlistedResources.add(xaResource);
       try {
          xaResource.start(xid, 0);
       } catch (XAException e) {
          log.error("Got an exception", e);
          throw new SystemException(e.getMessage());
       }
       return true;
    }
 
    public boolean delistResource(XAResource xaResource, int i) throws IllegalStateException,
             SystemException {
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
             throw new SystemException(e.getMessage());
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
    
    private static class DualNodeJtaTransactionXid implements Xid {
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java
index 46c9722ac7..7b89c622eb 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeJtaTransactionManagerImpl.java
@@ -1,158 +1,158 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import java.util.Hashtable;
 
 import javax.transaction.HeuristicMixedException;
 import javax.transaction.HeuristicRollbackException;
 import javax.transaction.InvalidTransactionException;
 import javax.transaction.NotSupportedException;
 import javax.transaction.RollbackException;
 import javax.transaction.Status;
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * Variant of SimpleJtaTransactionManagerImpl that doesn't use a VM-singleton, but rather a set of
  * impls keyed by a node id.
  * 
  * @author Brian Stansberry
  */
 public class DualNodeJtaTransactionManagerImpl implements TransactionManager {
 
-   private static final Logger log = LoggerFactory.getLogger(DualNodeJtaTransactionManagerImpl.class);
+   private static final Log log = LogFactory.getLog(DualNodeJtaTransactionManagerImpl.class);
 
    private static final Hashtable INSTANCES = new Hashtable();
 
    private ThreadLocal currentTransaction = new ThreadLocal();
    private String nodeId;
 
    public synchronized static DualNodeJtaTransactionManagerImpl getInstance(String nodeId) {
       DualNodeJtaTransactionManagerImpl tm = (DualNodeJtaTransactionManagerImpl) INSTANCES
                .get(nodeId);
       if (tm == null) {
          tm = new DualNodeJtaTransactionManagerImpl(nodeId);
          INSTANCES.put(nodeId, tm);
       }
       return tm;
    }
 
    public synchronized static void cleanupTransactions() {
       for (java.util.Iterator it = INSTANCES.values().iterator(); it.hasNext();) {
          TransactionManager tm = (TransactionManager) it.next();
          try {
             tm.suspend();
          } catch (Exception e) {
             log.error("Exception cleaning up TransactionManager " + tm);
          }
       }
    }
 
    public synchronized static void cleanupTransactionManagers() {
       INSTANCES.clear();
    }
 
    private DualNodeJtaTransactionManagerImpl(String nodeId) {
       this.nodeId = nodeId;
    }
 
    public int getStatus() throws SystemException {
       Transaction tx = getCurrentTransaction();
       return tx == null ? Status.STATUS_NO_TRANSACTION : tx.getStatus();
    }
 
    public Transaction getTransaction() throws SystemException {
       return (Transaction) currentTransaction.get();
    }
 
    public DualNodeJtaTransactionImpl getCurrentTransaction() {
       return (DualNodeJtaTransactionImpl) currentTransaction.get();
    }
 
    public void begin() throws NotSupportedException, SystemException {
       currentTransaction.set(new DualNodeJtaTransactionImpl(this));
    }
 
    public Transaction suspend() throws SystemException {
       DualNodeJtaTransactionImpl suspended = getCurrentTransaction();
       log.trace(nodeId + ": Suspending " + suspended + " for thread "
                + Thread.currentThread().getName());
       currentTransaction.set(null);
       return suspended;
    }
 
    public void resume(Transaction transaction) throws InvalidTransactionException,
             IllegalStateException, SystemException {
       currentTransaction.set((DualNodeJtaTransactionImpl) transaction);
       log.trace(nodeId + ": Resumed " + transaction + " for thread "
                + Thread.currentThread().getName());
    }
 
    public void commit() throws RollbackException, HeuristicMixedException,
             HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
       Transaction tx = getCurrentTransaction();
       if (tx == null) {
          throw new IllegalStateException("no current transaction to commit");
       }
       tx.commit();
    }
 
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
       Transaction tx = getCurrentTransaction();
       if (tx == null) {
          throw new IllegalStateException("no current transaction");
       }
       tx.rollback();
    }
 
    public void setRollbackOnly() throws IllegalStateException, SystemException {
       Transaction tx = getCurrentTransaction();
       if (tx == null) {
          throw new IllegalStateException("no current transaction");
       }
       tx.setRollbackOnly();
    }
 
    public void setTransactionTimeout(int i) throws SystemException {
    }
 
    void endCurrent(DualNodeJtaTransactionImpl transaction) {
       if (transaction == currentTransaction.get()) {
          currentTransaction.set(null);
       }
    }
 
    public String toString() {
       StringBuffer sb = new StringBuffer(getClass().getName());
       sb.append("[nodeId=");
       sb.append(nodeId);
       sb.append("]");
       return sb.toString();
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
similarity index 93%
rename from cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java
rename to cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
index 9a73105e11..38a46be8bc 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/AbstractDualNodeTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTestCase.java
@@ -1,241 +1,243 @@
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
 
-import java.util.Set;
-
 import org.hibernate.Session;
-import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.junit.functional.ExecutionEnvironment;
 import org.hibernate.junit.functional.FunctionalTestCase;
 import org.hibernate.transaction.CMTTransactionFactory;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * AbstractDualNodeTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
-public abstract class AbstractDualNodeTestCase extends FunctionalTestCase {
+public abstract class DualNodeTestCase extends FunctionalTestCase {
    
-   private static final Logger log = LoggerFactory.getLogger(AbstractDualNodeTestCase.class);
+   private static final Log log = LogFactory.getLog(DualNodeTestCase.class);
    public static final String NODE_ID_PROP = "hibernate.test.cluster.node.id";
    public static final String LOCAL = "local";
    public static final String REMOTE = "remote";
    private ExecutionEnvironment secondNodeEnvironment;
    private Session secondNodeSession;
 
-   public AbstractDualNodeTestCase(String string) {
+   public DualNodeTestCase(String string) {
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
 
+   protected boolean getUseQueryCache() {
+      return true;
+   }
+
    protected void standardConfigure(Configuration cfg) {
       super.configure(cfg);
 
       cfg.setProperty(Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName());
       cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass().getName());
       cfg.setProperty(Environment.TRANSACTION_STRATEGY, getTransactionFactoryClass().getName());
       cfg.setProperty(Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName());
+      cfg.setProperty(Environment.USE_QUERY_CACHE, String.valueOf(getUseQueryCache()));
    }
 
    /**
     * Settings impl that delegates most calls to the DualNodeTestCase itself, but overrides the
     * configure method to allow separate cache settings for the second node.
     */
    public class SecondNodeSettings implements ExecutionEnvironment.Settings {
-      private final AbstractDualNodeTestCase delegate;
+      private final DualNodeTestCase delegate;
 
       public SecondNodeSettings() {
-         this.delegate = AbstractDualNodeTestCase.this;
+         this.delegate = DualNodeTestCase.this;
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java
index 37757c348e..9abdffdf54 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/DualNodeTransactionManagerLookup.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
 import java.util.Properties;
 import javax.transaction.TransactionManager;
 import javax.transaction.Transaction;
 
 import org.hibernate.transaction.TransactionManagerLookup;
 import org.hibernate.HibernateException;
 
 /**
  * SimpleJtaTransactionManagerLookupImpl subclass that finds a different DualNodeTransactionManager
  * based on the value of property {@link DualNodeTestUtil#NODE_ID_PROP}.
  * 
  * @author Brian Stansberry
  */
 public class DualNodeTransactionManagerLookup implements TransactionManagerLookup {
 
    public TransactionManager getTransactionManager(Properties props) throws HibernateException {
-      String nodeId = props.getProperty(AbstractDualNodeTestCase.NODE_ID_PROP);
+      String nodeId = props.getProperty(DualNodeTestCase.NODE_ID_PROP);
       if (nodeId == null)
-         throw new HibernateException(AbstractDualNodeTestCase.NODE_ID_PROP + " not configured");
+         throw new HibernateException(DualNodeTestCase.NODE_ID_PROP + " not configured");
       return DualNodeJtaTransactionManagerImpl.getInstance(nodeId);
    }
 
    public String getUserTransactionName() {
       throw new UnsupportedOperationException("jndi currently not implemented for these tests");
    }
 
    public Object getTransactionIdentifier(Transaction transaction) {
       return transaction;
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
index 721513ed5c..b2d84d5af0 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
@@ -1,364 +1,364 @@
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
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.test.cache.infinispan.functional.Contact;
 import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheManager;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.jboss.util.collection.ConcurrentSet;
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * EntityCollectionInvalidationTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
-public class EntityCollectionInvalidationTestCase extends AbstractDualNodeTestCase {
-   private static final Logger log = LoggerFactory.getLogger(EntityCollectionInvalidationTestCase.class);
+public class EntityCollectionInvalidationTestCase extends DualNodeTestCase {
+   private static final Log log = LogFactory.getLog(EntityCollectionInvalidationTestCase.class);
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
-      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
+      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.LOCAL);
       // Cache localCache = localManager.getCache("entity");
       Cache localCustomerCache = localManager.getCache(Customer.class.getName());
       Cache localContactCache = localManager.getCache(Contact.class.getName());
       Cache localCollectionCache = localManager.getCache(Customer.class.getName() + ".contacts");
       MyListener localListener = new MyListener("local");
       localCustomerCache.addListener(localListener);
       localContactCache.addListener(localListener);
       localCollectionCache.addListener(localListener);
-      TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.LOCAL);
+      TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.LOCAL);
 
       // Bind a listener to the "remote" cache
-      CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.REMOTE);
+      CacheManager remoteManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.REMOTE);
       Cache remoteCustomerCache = remoteManager.getCache(Customer.class.getName());
       Cache remoteContactCache = remoteManager.getCache(Contact.class.getName());
       Cache remoteCollectionCache = remoteManager.getCache(Customer.class.getName() + ".contacts");
       MyListener remoteListener = new MyListener("remote");
       remoteCustomerCache.addListener(remoteListener);
       remoteContactCache.addListener(remoteListener);
       remoteCollectionCache.addListener(remoteListener);
-      TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.REMOTE);
+      TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.REMOTE);
 
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
          assertEquals(0, getValidKeyCount(localCollectionCache.keySet()));
          assertEquals(0, getValidKeyCount(localCustomerCache.keySet()));
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
 
    protected int getValidKeyCount(Set keys) {
       int result = 0;
       for (Object key : keys) {
          if (!(CacheHelper.isEvictAllNotification(key))) {
             result++;
          }
       }
       return result;
   }
 
    @Listener
    public static class MyListener {
-      private static final Logger log = LoggerFactory.getLogger(MyListener.class);
+      private static final Log log = LogFactory.getLog(MyListener.class);
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
             CacheKey cacheKey = (CacheKey) event.getKey();
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
index e137946d6d..de4536f383 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/SessionRefreshTestCase.java
@@ -1,138 +1,138 @@
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
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.test.cache.infinispan.functional.classloader.Account;
 import org.hibernate.test.cache.infinispan.functional.classloader.ClassLoaderTestDAO;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheManager;
 import org.infinispan.test.TestingUtil;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * SessionRefreshTestCase.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
-public class SessionRefreshTestCase extends AbstractDualNodeTestCase {
+public class SessionRefreshTestCase extends DualNodeTestCase {
 
    public static final String OUR_PACKAGE = SessionRefreshTestCase.class.getPackage().getName();
    
    protected final Logger log = LoggerFactory.getLogger(getClass());
 
    static int test = 0;
    
    private Cache localCache;
 
    public SessionRefreshTestCase(String string) {
       super(string);
    }
 
    protected String getEntityCacheConfigName() {
       return "entity";
    }
    
    /**
     * Disables use of the second level cache for this session factory.
     * 
     * {@inheritDoc} 
     */
    @Override
    protected void configureSecondNode(Configuration cfg) {
       super.configureSecondNode(cfg);
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "false");
    }
    
    @Override
    protected void standardConfigure(Configuration cfg) {
       super.standardConfigure(cfg);
       cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, getEntityCacheConfigName()); 
    }
 
    @Override
    public String[] getMappings() {
       return new String[] { "cache/infinispan/functional/classloader/Account.hbm.xml" };
    }
 
    @Override
    protected void cleanupTransactionManagement() {
       // Don't clean up the managers, just the transactions
       // Managers are still needed by the long-lived caches
       DualNodeJtaTransactionManagerImpl.cleanupTransactions();
    }
 
    public void testRefreshAfterExternalChange() throws Exception {
       // First session factory uses a cache
-      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(AbstractDualNodeTestCase.LOCAL);
+      CacheManager localManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.LOCAL);
       localCache = localManager.getCache(Account.class.getName());
-      TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.LOCAL);
+      TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.LOCAL);
       SessionFactory localFactory = getEnvironment().getSessionFactory();
 
       // Second session factory doesn't; just needs a transaction manager
-      TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(AbstractDualNodeTestCase.REMOTE);
+      TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.REMOTE);
       SessionFactory remoteFactory = getSecondNodeEnvironment().getSessionFactory();
 
       ClassLoaderTestDAO dao0 = new ClassLoaderTestDAO(localFactory, localTM);
       ClassLoaderTestDAO dao1 = new ClassLoaderTestDAO(remoteFactory, remoteTM);
 
       Integer id = new Integer(1);
-      dao0.createAccount(dao0.getSmith(), id, new Integer(5), AbstractDualNodeTestCase.LOCAL);
+      dao0.createAccount(dao0.getSmith(), id, new Integer(5), DualNodeTestCase.LOCAL);
 
       // Basic sanity check
       Account acct1 = dao1.getAccount(id);
       assertNotNull(acct1);
-      assertEquals(AbstractDualNodeTestCase.LOCAL, acct1.getBranch());
+      assertEquals(DualNodeTestCase.LOCAL, acct1.getBranch());
 
       // This dao's session factory isn't caching, so cache won't see this change
-      dao1.updateAccountBranch(id, AbstractDualNodeTestCase.REMOTE);
+      dao1.updateAccountBranch(id, DualNodeTestCase.REMOTE);
 
       // dao1's session doesn't touch the cache,
       // so reading from dao0 should show a stale value from the cache
       // (we check to confirm the cache is used)
       Account acct0 = dao0.getAccount(id);
       assertNotNull(acct0);
-      assertEquals(AbstractDualNodeTestCase.LOCAL, acct0.getBranch());
+      assertEquals(DualNodeTestCase.LOCAL, acct0.getBranch());
       log.debug("Contents when re-reading from local: " + TestingUtil.printCache(localCache));
 
       // Now call session.refresh and confirm we get the correct value
       acct0 = dao0.getAccountWithRefresh(id);
       assertNotNull(acct0);
-      assertEquals(AbstractDualNodeTestCase.REMOTE, acct0.getBranch());
+      assertEquals(DualNodeTestCase.REMOTE, acct0.getBranch());
       log.debug("Contents after refreshing in remote: " + TestingUtil.printCache(localCache));
 
       // Double check with a brand new session, in case the other session
       // for some reason bypassed the 2nd level cache
       ClassLoaderTestDAO dao0A = new ClassLoaderTestDAO(localFactory, localTM);
       Account acct0A = dao0A.getAccount(id);
       assertNotNull(acct0A);
-      assertEquals(AbstractDualNodeTestCase.REMOTE, acct0A.getBranch());
+      assertEquals(DualNodeTestCase.REMOTE, acct0A.getBranch());
       log.debug("Contents after creating a new session: " + TestingUtil.printCache(localCache));
    }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
index 4143d424f1..00ea5091d5 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
@@ -1,243 +1,243 @@
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
 
-import org.slf4j.Logger;
-import org.slf4j.LoggerFactory;
+import org.infinispan.util.logging.Log;
+import org.infinispan.util.logging.LogFactory;
 
 /**
  * XaResourceCapableTransactionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class XaTransactionImpl implements Transaction {
-   private static final Logger log = LoggerFactory.getLogger(XaTransactionImpl.class);
+   private static final Log log = LogFactory.getLog(XaTransactionImpl.class);
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
          
          runXaResourcePrepare();
 
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
          
          runXaResourceCommitTx();
 
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
       
       runXaResourceRollback();
 
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
diff --git a/cache-infinispan/src/test/resources/hibernate.properties b/cache-infinispan/src/test/resources/hibernate.properties
index 14104bb0bc..f3509e0000 100755
--- a/cache-infinispan/src/test/resources/hibernate.properties
+++ b/cache-infinispan/src/test/resources/hibernate.properties
@@ -1,40 +1,36 @@
 ################################################################################
 # Hibernate, Relational Persistence for Idiomatic Java                         #
 #                                                                              #
 # Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates or third-party contributors as    #
 # indicated by the @author tags or express copyright attribution               #
 # statements applied by the authors.  All third-party contributions are        #
 # distributed under license by Red Hat, Inc. and/or it's affiliates.                         #
 #                                                                              #
 # This copyrighted material is made available to anyone wishing to use, modify,#
 # copy, or redistribute it subject to the terms and conditions of the GNU      #
 # Lesser General Public License, as published by the Free Software Foundation. #
 #                                                                              #
 # This program is distributed in the hope that it will be useful,              #
 # but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 # or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 # for more details.                                                            #
 #                                                                              #
 # You should have received a copy of the GNU Lesser General Public License     #
 # along with this distribution; if not, write to:                              #
 # Free Software Foundation, Inc.                                               #
 # 51 Franklin Street, Fifth Floor                                              #
 # Boston, MA  02110-1301  USA                                                  #
 ################################################################################
 hibernate.dialect org.hibernate.dialect.HSQLDialect
 hibernate.connection.driver_class org.hsqldb.jdbcDriver
 hibernate.connection.url jdbc:hsqldb:mem:/test
 hibernate.connection.username sa
 hibernate.connection.password
 
 hibernate.connection.pool_size 5
 
 hibernate.format_sql true
 
 hibernate.max_fetch_depth 5
 
 hibernate.generate_statistics true
-
-hibernate.cache.use_second_level_cache true
-hibernate.cache.use_query_cache true
-hibernate.cache.region.factory_class org.hibernate.cache.infinispan.InfinispanRegionFactory
\ No newline at end of file
