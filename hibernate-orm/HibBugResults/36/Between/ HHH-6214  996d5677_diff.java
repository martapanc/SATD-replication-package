diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCachingRegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCachingRegionFactory.java
index 0fdaf19e20..de22ac93df 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCachingRegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/NoCachingRegionFactory.java
@@ -1,85 +1,83 @@
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
 package org.hibernate.cache.internal;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.NoCachingEnabledException;
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.TimestampsRegion;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
 
 /**
  * Factory used if no caching enabled in config...
  *
  * @author Steve Ebersole
  */
 public class NoCachingRegionFactory implements RegionFactory {
-
-
-	public NoCachingRegionFactory(Properties properties) {
+	public NoCachingRegionFactory() {
 	}
 
 	public void start(Settings settings, Properties properties) throws CacheException {
 	}
 
 	public void stop() {
 	}
 
 	public boolean isMinimalPutsEnabledByDefault() {
 		return false;
 	}
 
 	public AccessType getDefaultAccessType() {
 		return null;
 	}
 
 	public long nextTimestamp() {
 		return System.currentTimeMillis() / 100;
 	}
 
 	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException {
 		throw new NoCachingEnabledException();
 	}
 
 	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
 			throws CacheException {
 		throw new NoCachingEnabledException();
 	}
 
 	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
 		throw new NoCachingEnabledException();
 	}
 
 	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
 		throw new NoCachingEnabledException();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java
new file mode 100644
index 0000000000..f14dec3b3e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/RegionFactoryInitiator.java
@@ -0,0 +1,81 @@
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
+package org.hibernate.cache.internal;
+
+import java.util.Map;
+
+import org.hibernate.cache.spi.RegionFactory;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.spi.BasicServiceInitiator;
+import org.hibernate.service.spi.ServiceException;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+
+/**
+ * Initiator for the {@link RegionFactory} service.
+ *
+ * @author Hardy Ferentschik
+ */
+public class RegionFactoryInitiator implements BasicServiceInitiator<RegionFactory> {
+	public static final RegionFactoryInitiator INSTANCE = new RegionFactoryInitiator();
+
+	/**
+	 * Property name to use to configure the full qualified class name for the {@code RegionFactory}
+	 */
+	public static final String IMPL_NAME = "hibernate.cache.region.factory_class";
+
+	@Override
+	public Class<RegionFactory> getServiceInitiated() {
+		return RegionFactory.class;
+	}
+
+	@Override
+	@SuppressWarnings( { "unchecked" })
+	public RegionFactory initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		final Object impl = configurationValues.get( IMPL_NAME );
+		if ( impl == null ) {
+			return new NoCachingRegionFactory();
+		}
+
+		if ( getServiceInitiated().isInstance( impl ) ) {
+			return (RegionFactory) impl;
+		}
+
+		Class<? extends RegionFactory> customImplClass = null;
+		if ( Class.class.isInstance( impl ) ) {
+			customImplClass = (Class<? extends RegionFactory>) impl;
+		}
+		else {
+			customImplClass = registry.getService( ClassLoaderService.class ).classForName( impl.toString() );
+		}
+
+		try {
+			return customImplClass.newInstance();
+		}
+		catch ( Exception e ) {
+			throw new ServiceException(
+					"Could not initialize custom RegionFactory impl [" + customImplClass.getName() + "]", e
+			);
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
index be9fae261f..15df7d6f39 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/RegionFactory.java
@@ -1,139 +1,151 @@
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
 package org.hibernate.cache.spi;
 
 import java.util.Properties;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.Settings;
+import org.hibernate.service.Service;
 
 /**
  * Contract for building second level cache regions.
  * <p/>
  * Implementors should define a constructor in one of two forms:<ul>
  * <li>MyRegionFactoryImpl({@link java.util.Properties})</li>
  * <li>MyRegionFactoryImpl()</li>
  * </ul>
  * Use the first when we need to read config properties prior to
- * {@link #start} being called.  For an example, have a look at
+ * {@link #start(Settings, Properties)} being called.  For an example, have a look at
  * {@link org.hibernate.cache.internal.bridge.RegionFactoryCacheProviderBridge}
- * where we need the properties in order to determine which legacy 
+ * where we need the properties in order to determine which legacy
  * {@link CacheProvider} to use so that we can answer the
  * {@link #isMinimalPutsEnabledByDefault()} question for the
  * {@link org.hibernate.cfg.SettingsFactory}.
  *
  * @author Steve Ebersole
  */
-public interface RegionFactory {
+public interface RegionFactory extends Service {
 
 	/**
 	 * Lifecycle callback to perform any necessary initialization of the
 	 * underlying cache implementation(s).  Called exactly once during the
 	 * construction of a {@link org.hibernate.internal.SessionFactoryImpl}.
 	 *
 	 * @param settings The settings in effect.
 	 * @param properties The defined cfg properties
+	 *
 	 * @throws org.hibernate.cache.CacheException Indicates problems starting the L2 cache impl;
 	 * considered as a sign to stop {@link org.hibernate.SessionFactory}
 	 * building.
 	 */
 	public void start(Settings settings, Properties properties) throws CacheException;
 
 	/**
 	 * Lifecycle callback to perform any necessary cleanup of the underlying
 	 * cache implementation(s).  Called exactly once during
 	 * {@link org.hibernate.SessionFactory#close}.
 	 */
 	public void stop();
 
 	/**
 	 * By default should we perform "minimal puts" when using this second
 	 * level cache implementation?
 	 *
 	 * @return True if "minimal puts" should be performed by default; false
-	 * otherwise.
+	 *         otherwise.
 	 */
 	public boolean isMinimalPutsEnabledByDefault();
 
 	/**
 	 * Get the default access type for {@link EntityRegion entity} and
 	 * {@link CollectionRegion collection} regions.
 	 *
 	 * @return This factory's default access type.
 	 */
 	public AccessType getDefaultAccessType();
 
 	/**
 	 * Generate a timestamp.
 	 * <p/>
 	 * This is generally used for cache content locking/unlocking purposes
 	 * depending upon the access-strategy being used.
 	 *
 	 * @return The generated timestamp.
 	 */
 	public long nextTimestamp();
 
 	/**
 	 * Build a cache region specialized for storing entity data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
+	 *
 	 * @return The built region
+	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
-	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
+	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
+			throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing collection data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
 	 * @param metadata Information regarding the type of data to be cached
+	 *
 	 * @return The built region
+	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
-	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException;
+	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
+			throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing query results
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
+	 *
 	 * @return The built region
+	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException;
 
 	/**
 	 * Build a cache region specialized for storing update-timestamps data.
 	 *
 	 * @param regionName The name of the region.
 	 * @param properties Configuration properties.
+	 *
 	 * @return The built region
+	 *
 	 * @throws CacheException Indicates problems building the region.
 	 */
 	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index 43c71945e5..01a043be59 100644
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
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.Service;
 
 /**
  * Contract for creating persister instances (both {@link EntityPersister} and {@link CollectionPersister} varieties).
  *
  * @author Steve Ebersole
  */
 public interface PersisterFactory extends Service {
 
-	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
+	// TODO: is it really necessary to provide Configuration to CollectionPersisters ?
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 24b3c21297..3aad4c47ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,78 +1,80 @@
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
 package org.hibernate.service;
 
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
+import org.hibernate.integrator.internal.IntegratorServiceInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
-import org.hibernate.integrator.internal.IntegratorServiceInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
-import java.util.ArrayList;
-import java.util.List;
-
 /**
  * @author Steve Ebersole
  */
 public class StandardServiceInitiators {
 	public static List<BasicServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<BasicServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<BasicServiceInitiator> serviceInitiators = new ArrayList<BasicServiceInitiator>();
 
 		serviceInitiators.add( ClassLoaderServiceInitiator.INSTANCE );
 		serviceInitiators.add( JndiServiceInitiator.INSTANCE );
 		serviceInitiators.add( JmxServiceInitiator.INSTANCE );
 
 		serviceInitiators.add( PersisterClassResolverInitiator.INSTANCE );
 		serviceInitiators.add( PersisterFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( ConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( MultiTenantConnectionProviderInitiator.INSTANCE );
 		serviceInitiators.add( DialectResolverInitiator.INSTANCE );
 		serviceInitiators.add( DialectFactoryInitiator.INSTANCE );
 		serviceInitiators.add( BatchBuilderInitiator.INSTANCE );
 		serviceInitiators.add( JdbcServicesInitiator.INSTANCE );
 
 		serviceInitiators.add( JtaPlatformInitiator.INSTANCE );
 		serviceInitiators.add( TransactionFactoryInitiator.INSTANCE );
 
 		serviceInitiators.add( SessionFactoryServiceRegistryFactoryInitiator.INSTANCE );
 		serviceInitiators.add( IntegratorServiceInitiator.INSTANCE );
 
+		serviceInitiators.add( RegionFactoryInitiator.INSTANCE );
+
 		return serviceInitiators;
 	}
-
 }
