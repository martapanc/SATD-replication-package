diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
index f48d77acf8..2b4868fbdc 100755
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/access/TransactionalAccessDelegate.java
@@ -1,116 +1,112 @@
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
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.infinispan.Cache;
 
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
 
    protected final Cache cache;
 
    public TransactionalAccessDelegate(Cache cache) {
       this.cache = cache;
    }
 
    public Object get(Object key, long txTimestamp) throws CacheException {
       return cache.get(key);
    }
 
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
       cache.putForExternalRead(key, value);
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
       cache.put(key, value);
       return true;
    }
 
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
       return false;
    }
 
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
       cache.put(key, value);
       return true;
    }
 
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
             throws CacheException {
       return false;
    }
 
    public void remove(Object key) throws CacheException {
       cache.remove(key);
    }
 
    public void removeAll() throws CacheException {
       cache.clear();
    }
 
-//   public void evict(Object key) throws CacheException {
-//      cache.evict(key);
-//   }
-
    public void evictAll() throws CacheException {
       evictOrRemoveAll();
    }
 
    private void evictOrRemoveAll() throws CacheException {
       cache.clear();
    }
 }
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
index 2c8b7ff08f..41a00deaf5 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/CollectionRegionImpl.java
@@ -1,33 +1,33 @@
 package org.hibernate.cache.infinispan.collection;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
 import org.infinispan.Cache;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class CollectionRegionImpl extends BaseTransactionalDataRegion implements CollectionRegion {
 
-   public CollectionRegionImpl(Cache<Object, Object> cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
+   public CollectionRegionImpl(Cache cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
       super(cache, name, metadata, transactionManager);
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
index 89d578a49a..7fe5d1acf6 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
@@ -1,74 +1,73 @@
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
       this.delegate = new TransactionalAccessDelegate(region.getCache());
    }
 
    public void evict(Object key) throws CacheException {
-//      delegate.evict(key);
       delegate.remove(key);
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
index 187b575001..58d4dd5b68 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/EntityRegionImpl.java
@@ -1,33 +1,33 @@
 package org.hibernate.cache.infinispan.entity;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
 import org.infinispan.Cache;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class EntityRegionImpl extends BaseTransactionalDataRegion implements EntityRegion {
 
-   public EntityRegionImpl(Cache<Object, Object> cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
+   public EntityRegionImpl(Cache cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
       super(cache, name, metadata, transactionManager);
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
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
index 8ef8dd4042..9661a75f45 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseGeneralDataRegion.java
@@ -1,38 +1,38 @@
 package org.hibernate.cache.infinispan.impl;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.GeneralDataRegion;
 import org.infinispan.Cache;
 
 /**
  * Support for Infinispan {@link GeneralDataRegion} implementors.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class BaseGeneralDataRegion extends BaseRegion implements GeneralDataRegion {
 
-   public BaseGeneralDataRegion(Cache<Object, Object> cache, String name, TransactionManager transactionManager) {
+   public BaseGeneralDataRegion(Cache cache, String name, TransactionManager transactionManager) {
       super(cache, name, transactionManager);
    }
 
    public void evict(Object key) throws CacheException {
       getCache().evict(key);
    }
 
    public void evictAll() throws CacheException {
       getCache().clear();
    }
 
    public Object get(Object key) throws CacheException {
       return getCache().get(key);
    }
 
    public void put(Object key, Object value) throws CacheException {
       getCache().put(key, value);
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
index f1da7e29b6..5202d84847 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseRegion.java
@@ -1,180 +1,141 @@
 package org.hibernate.cache.infinispan.impl;
 
 import java.util.Map;
 
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.infinispan.Cache;
 import org.infinispan.context.Flag;
 
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
    private final Cache cache;
    private final String name;
    protected final TransactionManager transactionManager;
 
    public BaseRegion(Cache cache, String name, TransactionManager transactionManager) {
       this.cache = cache;
       this.name = name;
       this.transactionManager = transactionManager;
    }
 
    public Cache getCache() {
       return cache;
    }
 
    public String getName() {
       return name;
    }
 
    public long getElementCountInMemory() {
       return cache.size();
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
       return cache;
    }
 
    public void destroy() throws CacheException {
       cache.clear();
    }
    
    public boolean contains(Object key) {
       return CacheHelper.containsKey(cache, key, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);
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
    protected Object suspendAndGet(Object key, Flag opt, boolean suppressTimeout) throws CacheException {
        Transaction tx = suspend();
        try {
            if (suppressTimeout)
                return CacheHelper.getAllowingTimeout(cache, key);
            else
                return CacheHelper.get(cache, key);
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
    protected Transaction suspend() {
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
    protected void resume(Transaction tx) {
        try {
            if (tx != null)
                transactionManager.resume(tx);
        } catch (Exception e) {
            throw new CacheException("Could not resume transaction", e);
        }
    }
-   
-//   /**
-//    * HACKY WAY TO GET THE TRANSACTION MANAGER, TODO: resolve it!
-//    */
-//   private static TransactionManager getTransactionManager(Properties properties) {
-////      return cache == null ? null : extractComponent(cache, TransactionManager.class);
-//      return TransactionManagerLookupFactory.getTransactionManager(properties);
-//   }
-//   
-//   public static <T> T extractComponent(Cache cache, Class<T> componentType) {
-//      ComponentRegistry cr = extractComponentRegistry(cache);
-//      return cr.getComponent(componentType);
-//   }
-//   
-//   public static ComponentRegistry extractComponentRegistry(Cache cache) {
-//      return (ComponentRegistry) extractField(cache, "componentRegistry");
-//   }
-//   
-//   public static Object extractField(Object target, String fieldName) {
-//      return extractField(target.getClass(), target, fieldName);
-//   }
-//   
-//   public static Object extractField(Class type, Object target, String fieldName) {
-//      Field field;
-//      try {
-//         field = type.getDeclaredField(fieldName);
-//         field.setAccessible(true);
-//         return field.get(target);
-//      }
-//      catch (Exception e) {
-//         if (type.equals(Object.class)) {
-//            e.printStackTrace();
-//            return null;
-//         } else {
-//            // try with superclass!!
-//            return extractField(type.getSuperclass(), target, fieldName);
-//         }
-//      }
-//   }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
index 79fac91d95..f4949919be 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/impl/BaseTransactionalDataRegion.java
@@ -1,33 +1,33 @@
 package org.hibernate.cache.infinispan.impl;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.TransactionalDataRegion;
 import org.infinispan.Cache;
 
 /**
  * Support for Inifinispan {@link TransactionalDataRegion} implementors.
  * 
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class BaseTransactionalDataRegion extends BaseRegion implements TransactionalDataRegion {
 
    private final CacheDataDescription metadata;
 
-   public BaseTransactionalDataRegion(Cache<Object, Object> cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
+   public BaseTransactionalDataRegion(Cache cache, String name, CacheDataDescription metadata, TransactionManager transactionManager) {
       super(cache, name, transactionManager);
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
index 299a526f5d..fe2415cb4c 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/query/QueryResultsRegionImpl.java
@@ -1,75 +1,75 @@
 package org.hibernate.cache.infinispan.query;
 
 import java.util.Properties;
 
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.infinispan.impl.BaseTransactionalDataRegion;
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.infinispan.Cache;
 import org.infinispan.context.Flag;
 
 /**
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryResultsRegionImpl extends BaseTransactionalDataRegion implements QueryResultsRegion {
    private boolean localOnly;
 
-   public QueryResultsRegionImpl(Cache<Object, Object> cache, String name, Properties properties, TransactionManager transactionManager) {
+   public QueryResultsRegionImpl(Cache cache, String name, Properties properties, TransactionManager transactionManager) {
       super(cache, name, null, transactionManager);
       
       // If Infinispan is using INVALIDATION for query cache, we don't want to propagate changes.
       // We use the Timestamps cache to manage invalidation
       localOnly = CacheHelper.isClusteredInvalidation(cache);
    }
 
    public void evict(Object key) throws CacheException {
       if (localOnly)
          CacheHelper.removeKey(getCache(), key, Flag.CACHE_MODE_LOCAL);
       else 
          CacheHelper.removeKey(getCache(), key);
    }
 
    public void evictAll() throws CacheException {
       if (localOnly)
          CacheHelper.removeAll(getCache(), Flag.CACHE_MODE_LOCAL);
       else 
          CacheHelper.removeAll(getCache());
    }
 
    public Object get(Object key) throws CacheException {
       // Don't hold the JBC node lock throughout the tx, as that
       // prevents updates
       // Add a zero (or low) timeout option so we don't block
       // waiting for tx's that did a put to commit
       return suspendAndGet(key, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT, true);
    }
 
    public void put(Object key, Object value) throws CacheException {
       // Here we don't want to suspend the tx. If we do:
       // 1) We might be caching query results that reflect uncommitted
       // changes. No tx == no WL on cache node, so other threads
       // can prematurely see those query results
       // 2) No tx == immediate replication. More overhead, plus we
       // spread issue #1 above around the cluster
 
       // Add a zero (or quite low) timeout option so we don't block.
       // Ignore any TimeoutException. Basically we forego caching the
       // query result in order to avoid blocking.
       // Reads are done with suspended tx, so they should not hold the
       // lock for long.  Not caching the query result is OK, since
       // any subsequent read will just see the old result with its
       // out-of-date timestamp; that result will be discarded and the
       // db query performed again.
       if (localOnly)
          CacheHelper.putAllowingTimeout(getCache(), key, value, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT, Flag.CACHE_MODE_LOCAL);
       else 
          CacheHelper.putAllowingTimeout(getCache(), key, value, Flag.ZERO_LOCK_ACQUISITION_TIMEOUT);
       
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
index 47fc706805..2609ff6f85 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
@@ -1,121 +1,113 @@
 package org.hibernate.cache.infinispan.timestamp;
 
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.TimestampsRegion;
 import org.hibernate.cache.infinispan.impl.BaseGeneralDataRegion;
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.infinispan.Cache;
 import org.infinispan.context.Flag;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
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
 
-   public TimestampsRegionImpl(Cache<Object, Object> cache, String name, TransactionManager transactionManager) {
+   public TimestampsRegionImpl(Cache cache, String name, TransactionManager transactionManager) {
       super(cache, name, transactionManager);
       cache.addListener(this);
       populateLocalCache();
    }
 
    @Override
    public void evict(Object key) throws CacheException {
       // TODO Is this a valid operation on a timestamps cache?
       CacheHelper.removeKey(getCache(), key);
    }
 
    public void evictAll() throws CacheException {
       // TODO Is this a valid operation on a timestamps cache?
       CacheHelper.removeAll(getCache());
    }
 
    public Object get(Object key) throws CacheException {
       Object value = localCache.get(key);
       if (value == null) {
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
          CacheHelper.put(getCache(), key, value, Flag.FORCE_ASYNCHRONOUS);
       } catch (Exception e) {
          throw new CacheException(e);
       } finally {
          resume(tx);
       }
    }
 
    @Override
    public void destroy() throws CacheException {
       localCache.clear();
       getCache().removeListener(this);
       super.destroy();
    }
 
    /**
     * Monitors cache events and updates the local cache
     * 
     * @param event
     */
    @CacheEntryModified
    public void nodeModified(CacheEntryModifiedEvent event) {
       if (event.isPre()) return;
       localCache.put(event.getKey(), event.getValue());
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
-//      Fqn fqn = event.getFqn();
-//      Fqn regFqn = getRegionFqn();
-//      if (fqn.size() == regFqn.size() + 1 && fqn.isChildOf(regFqn)) {
-//         Object key = fqn.get(regFqn.size());
-//         localCache.remove(key);
-//      } else if (fqn.equals(regFqn)) {
-//         localCache.clear();
-//      }
    }
 
    /**
     * Brings all data from the distributed cache into our local cache.
     */
    private void populateLocalCache() {
       Set children = CacheHelper.getKeySet(getCache());
       for (Object key : children)
          get(key);
    }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
index cb243d60a5..d9fdda2455 100644
--- a/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
+++ b/cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
@@ -1,447 +1,372 @@
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
 
 import java.util.Set;
 
 import org.hibernate.cache.CacheException;
 import org.infinispan.Cache;
 import org.infinispan.config.Configuration;
 import org.infinispan.context.Flag;
 import org.infinispan.util.concurrent.TimeoutException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Helper for dealing with Infinisan cache instances.
  * 
- * @author Steve Ebersole
- * @author Brian Stansberry
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class CacheHelper {
 
-   /** Key under which items are cached */
-   public static final String ITEM = "item";
-   /** Key and value used in a hack to create region root nodes */
-   public static final String DUMMY = "dummy";
-
    private static final Logger log = LoggerFactory.getLogger(CacheHelper.class);
 
    /**
     * Disallow external instantiation of CacheHelper.
     */
    private CacheHelper() {
    }
 
    /**
     * Is this cache participating in a cluster with invalidation?
     * 
     * @param cache
     *           The cache to check.
     * @return True if the cache is configured for synchronous/asynchronous invalidation; false
     *         otherwise.
     */
    public static boolean isClusteredInvalidation(Cache cache) {
       return isClusteredInvalidation(cache.getConfiguration().getCacheMode());
    }
 
    /**
     * Does this cache mode indicate clustered invalidation?
     * 
     * @param cacheMode
     *           The cache to check
     * @return True if the cache mode is confiogured for synchronous/asynchronous invalidation; false
     *         otherwise.
     */
    public static boolean isClusteredInvalidation(Configuration.CacheMode cacheMode) {
       return cacheMode == Configuration.CacheMode.INVALIDATION_ASYNC
                || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC;
    }
 
    /**
     * Is this cache participating in a cluster with replication?
     * 
     * @param cache
     *           The cache to check.
     * @return True if the cache is configured for synchronous/asynchronous invalidation; false
     *         otherwise.
     */
    public static boolean isClusteredReplication(Cache cache) {
       return isClusteredReplication(cache.getConfiguration().getCacheMode());
    }
 
    /**
     * Does this cache mode indicate clustered replication?
     * 
     * @param cacheMode
     *           The cache to check
     * @return True if the cache mode is confiogured for synchronous/asynchronous invalidation; false
     *         otherwise.
     */
    public static boolean isClusteredReplication(Configuration.CacheMode cacheMode) {
       return cacheMode == Configuration.CacheMode.REPL_ASYNC || cacheMode == Configuration.CacheMode.REPL_SYNC;
    }
 
    public static boolean isSynchronous(Cache cache) {
       return isSynchronous(cache.getConfiguration().getCacheMode());
    }
 
    public static boolean isSynchronous(Configuration.CacheMode cacheMode) {
       return cacheMode == Configuration.CacheMode.REPL_SYNC || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC;
    }
 
    public static Set getKeySet(Cache cache) {
       return cache.keySet();
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>get(Fqn, Object)</code>, wrapping any exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     */
    public static Object get(Cache cache, Object key) throws CacheException {
       try {
          return cache.get(key);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>get(Fqn, Object)</code>, wrapping any exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     */
    public static Object getAllowingTimeout(Cache cache, Object key) throws CacheException {
       try {
          return cache.get(key);
       } catch (TimeoutException ignored) {
          // ignore it
          return null;
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>put(Object, Object)</code>, wrapping any exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     * @param value
     *           data to store in the cache node
     */
    public static void put(Cache cache, Object key, Object value) throws CacheException {
       put(cache, key, value, null);
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>put(Object, Object)</code>, wrapping any exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     * @param value
     *           data to store in the cache node
     * @param option
     *           invocation Option to set for this invocation. May be <code>null</code>.
     */
    public static void put(Cache cache, Object key, Object value, Flag option) throws CacheException {
       try {
          cache.getAdvancedCache().put(key, value, option);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>put(Object, Object)</code>, ignoring any {@link TimeoutException} and wrapping any other
     * exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     * @param value
     *           data to store in the cache node
     * @param option
     *           invocation Option to set for this invocation. May be <code>null</code>.
     */
    public static void putAllowingTimeout(Cache cache, Object key, Object value, Flag... option) throws CacheException {
       try {
          cache.getAdvancedCache().put(key, value, option);
       } catch (TimeoutException allowed) {
          // ignore it
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>putForExternalRead(Object, Object)</code>, wrapping any exception in a
     * {@link CacheException}. Ignores any JBoss Cache {@link TimeoutException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     * @param value
     *           data to store in the cache node
     */
    public static boolean putForExternalRead(Cache cache, Object key, Object value) throws CacheException {
       return putForExternalRead(cache, key, value, (Flag[])null);
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>putForExternalRead(Object, Object)</code>, wrapping any exception in a
     * {@link CacheException}. Ignores any JBoss Cache {@link TimeoutException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     * @param value
     *           data to store in the cache node
     * @param option
     *           invocation Option to set for this invocation. May be <code>null</code>.
     */
    public static boolean putForExternalRead(Cache cache, Object key, Object value, Flag... option) throws CacheException {
       try {
          cache.getAdvancedCache().putForExternalRead(key, value, option);
          return true;
       } catch (TimeoutException te) {
          // ignore!
          log.debug("ignoring write lock acquisition failure");
          return false;
       } catch (Throwable t) {
          throw new CacheException(t);
       }
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>removeNode(Fqn)</code>, wrapping any exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     */
    public static void remove(Cache cache, Object key) throws CacheException {
       remove(cache, key, null);
    }
 
    /**
     * Builds an {@link Fqn} from <code>region</code> and <code>key</code> and performs a JBoss Cache
     * <code>removeNode(Fqn)</code>, wrapping any exception in a {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param key
     *           specific key to append to the <code>region</code> to form the full Fqn
     * @param option
     *           invocation Option to set for this invocation. May be <code>null</code>.
     */
    public static void remove(Cache cache, Object key, Flag option) throws CacheException {
       try {
          cache.getAdvancedCache().remove(key, option);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any exception in a
     * {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     */
    public static void removeAll(Cache cache) throws CacheException {
       try {
          cache.clear();
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any exception in a
     * {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param option
     *           invocation Option to set for this invocation. May be <code>null</code>.
     */
    public static void removeAll(Cache cache, Flag option) throws CacheException {
       try {
          cache.getAdvancedCache().clear(option);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
    /**
     * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any exception in a
     * {@link CacheException}.
     * 
     * @param cache
     *           the cache to invoke on
     * @param region
     *           base Fqn for the cache region
     * @param option
     *           invocation Option to set for this invocation. May be <code>null</code>.
     */
    public static void removeKey(Cache cache, Object key, Flag option) throws CacheException {
       try {
          cache.getAdvancedCache().remove(key, option);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
    
    public static void removeKey(Cache cache, Object key) throws CacheException {
       try {
          cache.remove(key);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
    
-//   public static void evict(Cache cache, Object key) throws CacheException {
-//      try {
-//         cache.evict(key);
-//      } catch (Exception e) {
-//         throw new CacheException(e);
-//      }
-//   }
-   
    public static boolean containsKey(Cache cache, Object key, Flag... flags) {
       try {
          return cache.getAdvancedCache().containsKey(key, flags);
       } catch (Exception e) {
          throw new CacheException(e);
       }
    }
 
-//   public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident) throws CacheException {
-//      try {
-//         Option option = null;
-//         if (localOnly) {
-//            option = new Option();
-//            option.setCacheModeLocal(localOnly);
-//         }
-//
-//         Node root = cache.getRoot();
-//         setInvocationOption(cache, option);
-//         // FIXME hack to work around fact that calling
-//         // Node added = root.addChild( fqn ); doesn't
-//         // properly set the version on the node
-//         Node added = null;
-//         if (version == null) {
-//            added = root.addChild(fqn);
-//         } else {
-//            cache.put(fqn, DUMMY, DUMMY);
-//            added = root.getChild(fqn);
-//         }
-//         if (resident)
-//            added.setResident(true);
-//         return added;
-//      } catch (Exception e) {
-//         throw new CacheException(e);
-//      }
-//   }
-
-   /**
-//    * Assigns the given Option to the cache's {@link InvocationContext}. Does nothing if
-//    * <code>option</code> is <code>null</code>.
-//    * 
-//    * @param cache
-//    *           the cache. Cannot be <code>null</code>.
-//    * @param option
-//    *           the option. May be <code>null</code>.
-//    * 
-//    * @see {@link Cache#getInvocationContext()}
-//    * @see {@link InvocationContext#setOptionOverrides(Option)}
-//    */
-//   public static void setInvocationOption(Cache cache, Option option) {
-//      if (option != null) {
-//         cache.getInvocationContext().setOptionOverrides(option);
-//      }
-//   }
-
-//   /**
-//    * Creates an {@link Option} using the given {@link DataVersion} and passes it to
-//    * {@link #setInvocationOption(Cache, Option)}.
-//    * 
-//    * @param cache
-//    *           the cache to set the Option on. Cannot be <code>null</code>.
-//    * @param version
-//    *           the DataVersion to set. Cannot be <code>null</code>.
-//    */
-//   public static void setDataVersionOption(Cache cache, DataVersion version) {
-//      Option option = new Option();
-//      option.setDataVersion(version);
-//      setInvocationOption(cache, option);
-//   }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
index 5f8b7487bd..c196a1e9dc 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
@@ -1,128 +1,109 @@
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
 import org.hibernate.cache.RegionFactory;
 import org.hibernate.cache.TransactionalDataRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Base class for tests of EntityRegion and CollectionRegion implementations.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractEntityCollectionRegionTestCase extends AbstractRegionImplTestCase {
 
-    /**
-     * Create a new EntityCollectionRegionTestCaseBase.
-     * 
-     * @param name
-     */
-    public AbstractEntityCollectionRegionTestCase(String name) {
-        super(name);
-    }
-   
-    /** 
-     * Creates a Region backed by an PESSIMISTIC locking JBoss Cache, and then 
-     * ensures that it handles calls to buildAccessStrategy as expected when 
-     * all the various {@link AccessType}s are passed as arguments.
-     */
-    public void testSupportedAccessTypes() throws Exception {
-        supportedAccessTypeTest();
-    }
-    
-    private void supportedAccessTypeTest() throws Exception {
-        Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
-        String entityCfg = "entity";
-        cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, entityCfg);
-        InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-        supportedAccessTypeTest(regionFactory, cfg.getProperties());
-    }
-    
-    /** 
-     * Creates a Region using the given factory, and then ensure that it
-     * handles calls to buildAccessStrategy as expected when all the
-     * various {@link AccessType}s are passed as arguments.
-     */
-    protected abstract void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties);
-    
-    /**
-     * Test that the Region properly implements 
-     * {@link TransactionalDataRegion#isTransactionAware()}.
-     * 
-     * @throws Exception
-     */
-    public void testIsTransactionAware() throws Exception {
-        Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
-//        cfg.setProperty(SharedCacheInstanceManager.CACHE_RESOURCE_PROP, CacheTestUtil.LOCAL_PESSIMISTIC_CACHE);
+   /**
+    * Create a new EntityCollectionRegionTestCaseBase.
+    * 
+    * @param name
+    */
+   public AbstractEntityCollectionRegionTestCase(String name) {
+      super(name);
+   }
 
-        InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-        
-        TransactionalDataRegion region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
-        
-        assertTrue("Region is transaction-aware", region.isTransactionAware());
-        
-        CacheTestUtil.stopRegionFactory(regionFactory, getCacheTestSupport());
-        
-        cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
-//        cfg.setProperty(SharedCacheInstanceManager.CACHE_RESOURCE_PROP, CacheTestUtil.LOCAL_PESSIMISTIC_CACHE);
-        // Make it non-transactional
-        cfg.getProperties().remove(Environment.TRANSACTION_MANAGER_STRATEGY);
-        
-        regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-        
-        region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
-        
-        assertFalse("Region is not transaction-aware", region.isTransactionAware());
-        
-        CacheTestUtil.stopRegionFactory(regionFactory, getCacheTestSupport());
-    }
-    
-    public void testGetCacheDataDescription() throws Exception {
-        Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
-//        cfg.setProperty(SharedCacheInstanceManager.CACHE_RESOURCE_PROP, CacheTestUtil.LOCAL_PESSIMISTIC_CACHE);
-        
-        InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
-        
-        TransactionalDataRegion region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
-        
-        CacheDataDescription cdd = region.getCacheDataDescription();
-        
-        assertNotNull(cdd);
-        
-        CacheDataDescription expected = getCacheDataDescription();
-        assertEquals(expected.isMutable(), cdd.isMutable());
-        assertEquals(expected.isVersioned(), cdd.isVersioned());
-        assertEquals(expected.getVersionComparator(), cdd.getVersionComparator());
-        
-    }
+   /**
+    * Creates a Region backed by an PESSIMISTIC locking JBoss Cache, and then ensures that it
+    * handles calls to buildAccessStrategy as expected when all the various {@link AccessType}s are
+    * passed as arguments.
+    */
+   public void testSupportedAccessTypes() throws Exception {
+      supportedAccessTypeTest();
+   }
+
+   private void supportedAccessTypeTest() throws Exception {
+      Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
+      String entityCfg = "entity";
+      cfg.setProperty(InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, entityCfg);
+      InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
+      supportedAccessTypeTest(regionFactory, cfg.getProperties());
+   }
+
+   /**
+    * Creates a Region using the given factory, and then ensure that it handles calls to
+    * buildAccessStrategy as expected when all the various {@link AccessType}s are passed as
+    * arguments.
+    */
+   protected abstract void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties);
+
+   /**
+    * Test that the Region properly implements {@link TransactionalDataRegion#isTransactionAware()}.
+    * 
+    * @throws Exception
+    */
+   public void testIsTransactionAware() throws Exception {
+      Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
+      InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
+      TransactionalDataRegion region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
+      assertTrue("Region is transaction-aware", region.isTransactionAware());
+      CacheTestUtil.stopRegionFactory(regionFactory, getCacheTestSupport());
+      cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
+      // Make it non-transactional
+      cfg.getProperties().remove(Environment.TRANSACTION_MANAGER_STRATEGY);
+      regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
+      region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
+      assertFalse("Region is not transaction-aware", region.isTransactionAware());
+      CacheTestUtil.stopRegionFactory(regionFactory, getCacheTestSupport());
+   }
+
+   public void testGetCacheDataDescription() throws Exception {
+      Configuration cfg = CacheTestUtil.buildConfiguration("test", InfinispanRegionFactory.class, true, false);
+      InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
+      TransactionalDataRegion region = (TransactionalDataRegion) createRegion(regionFactory, "test/test", cfg.getProperties(), getCacheDataDescription());
+      CacheDataDescription cdd = region.getCacheDataDescription();
+      assertNotNull(cdd);
+      CacheDataDescription expected = getCacheDataDescription();
+      assertEquals(expected.isMutable(), cdd.isMutable());
+      assertEquals(expected.isVersioned(), cdd.isVersioned());
+      assertEquals(expected.getVersionComparator(), cdd.getVersionComparator());
+   }
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index 3e4b0811b9..2103b629b3 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,195 +1,193 @@
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
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.infinispan.Cache;
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
       Cache localCache = getInfinispanCache(regionFactory);
       boolean invalidation = CacheHelper.isClusteredInvalidation(localCache);
 
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
       Cache localCache = getInfinispanCache(regionFactory);
-//      boolean invalidation = CacheHelper.isClusteredInvalidation(localCache);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
 
       cfg = createConfiguration();
       regionFactory = CacheTestUtil.startRegionFactory(cfg, getCacheTestSupport());
       Cache remoteCache = getInfinispanCache(regionFactory);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(regionFactory,
                getStandardRegionName(REGION_PREFIX), cfg.getProperties(), null);
-//      String regionName = REGION_PREFIX;
 
       Set children = CacheHelper.getKeySet(localCache);
       assertEquals("No children in " + children, 0, children.size());
 
       children = CacheHelper.getKeySet(remoteCache);
       assertEquals("No children in " + children, 0, children.size());
 
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
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       // This only adds a node in the case of optimistic locking
       assertEquals(null, remoteRegion.get(KEY));
 
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
index 7a7fa18ba4..51557fa811 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
@@ -1,541 +1,512 @@
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
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.util.ComparableComparator;
 import org.infinispan.Cache;
 import org.infinispan.context.Flag;
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
    protected Cache localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
    protected Cache remoteCache;
 
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
       localCache = ((BaseRegion) localCollectionRegion).getCache();
       localAccessStrategy = localCollectionRegion.buildAccessStrategy(getAccessType());
       invalidation = CacheHelper.isClusteredInvalidation(localCache);
       synchronous = CacheHelper.isSynchronous(localCache);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteCollectionRegion = remoteRegionFactory.buildCollectionRegion(REGION_NAME, remoteCfg.getProperties(),
                getCacheDataDescription());
       remoteCache = ((BaseRegion) remoteCollectionRegion).getCache();
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
          localCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
          remoteCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
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
 
-      // Fqn regionFqn = getRegionFqn(REGION_NAME, REGION_PREFIX);
-      //
-      // Node regionRoot = localCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // assertTrue(regionRoot.isResident());
       assertEquals(0, localCache.keySet().size());
 
-      // regionRoot = remoteCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // assertTrue(regionRoot.isResident());
       assertEquals(0, remoteCache.keySet().size());
 
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
 
-      // regionRoot = localCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // assertTrue(regionRoot.isValid());
-      // assertTrue(regionRoot.isResident());
       assertEquals(0, localCache.keySet().size());
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      // regionRoot = remoteCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertTrue(regionRoot.isValid());
-      // assertTrue(regionRoot.isResident());
-      //        
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // Not invalidation, so we didn't insert a child above
       assertEquals(0, remoteCache.keySet().size());
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      // regionRoot = remoteCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertTrue(regionRoot.isValid());
-      // assertTrue(regionRoot.isResident());
-      // // Region root should have 1 child -- the one we added above
-      // assertEquals(1, getValidChildrenCount(regionRoot));
-      // Revalidate the region root
       assertEquals(1, remoteCache.keySet().size());
 
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
index 15e2ec8791..8cde37e781 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
@@ -1,743 +1,694 @@
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
 import org.hibernate.cache.infinispan.util.CacheHelper;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.util.ComparableComparator;
 import org.infinispan.Cache;
 import org.infinispan.context.Flag;
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
    protected Cache localCache;
    protected static Configuration remoteCfg;
    protected static InfinispanRegionFactory remoteRegionFactory;
    protected Cache remoteCache;
 
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
 
       localCache = ((BaseRegion) localEntityRegion).getCache();
 
       invalidation = CacheHelper.isClusteredInvalidation(localCache);
       synchronous = CacheHelper.isSynchronous(localCache);
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteEntityRegion = remoteRegionFactory.buildEntityRegion(REGION_NAME, remoteCfg
                .getProperties(), getCacheDataDescription());
       remoteAccessStrategy = remoteEntityRegion.buildAccessStrategy(getAccessType());
 
       remoteCache = ((BaseRegion) remoteEntityRegion).getCache();
 
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
          localCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
       } catch (Exception e) {
          log.error("Problem purging local cache", e);
       }
 
       try {
          remoteCache.getAdvancedCache().clear(Flag.CACHE_MODE_LOCAL);
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
 
-//      if (!isBlockingReads())
-         assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
-//      else {
-//         // Reader should be blocking for lock
-//         assertFalse("Threads completed", completionLatch.await(250, TimeUnit.MILLISECONDS));
-//         commitLatch.countDown();
-//         assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
-//      }
+      assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
 
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
 
-               // This will block w/ pessimistic locking and then
-               // read the new value; w/ optimistic it shouldn't
-               // block and will read the old value
-//               Object expected = !isBlockingReads() ? VALUE1 : VALUE2;
+               // This won't block w/ mvc and will read the old value
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
 
-//      if (!isBlockingReads())
-         // Should complete promptly
-         assertTrue(completionLatch.await(2, TimeUnit.SECONDS));
-//      else {
-//         // Reader thread should be blocking
-//         assertFalse(completionLatch.await(250, TimeUnit.MILLISECONDS));
-//         // Let the writer commit down
-//         commitLatch.countDown();
-//         assertTrue(completionLatch.await(1, TimeUnit.SECONDS));
-//      }
+      // Should complete promptly
+      assertTrue(completionLatch.await(2, TimeUnit.SECONDS));
 
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
 
-      // sleep(1000);
-
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
    }
 
    private void evictOrRemoveAllTest(boolean evict) {
 
       final String KEY = KEY_BASE + testCount++;
 
-//      Fqn regionFqn = getRegionFqn(REGION_NAME, REGION_PREFIX);
-//
-//      Node regionRoot = localCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
       assertEquals(0, localCache.keySet().size());
-//      assertTrue(regionRoot.isResident());
-
-//      regionRoot = remoteCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
       assertEquals(0, remoteCache.keySet().size());
-//      assertTrue(regionRoot.isResident());
 
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
 
       if (evict)
          localAccessStrategy.evictAll();
       else
          localAccessStrategy.removeAll();
 
       // This should re-establish the region root node in the optimistic case
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-//      regionRoot = localCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertEquals(0, getValidChildrenCount(regionRoot));
-//      assertTrue(regionRoot.isValid());
-//      assertTrue(regionRoot.isResident());
       assertEquals(0, localCache.keySet().size());
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-//      regionRoot = remoteCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertTrue(regionRoot.isValid());
-//      assertTrue(regionRoot.isResident());
-//      // Not invalidation, so we didn't insert a child above
-//      assertEquals(0, getValidChildrenCount(regionRoot));
       assertEquals(0, remoteCache.keySet().size());
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
-      // Revalidate the region root
-//      regionRoot = remoteCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertTrue(regionRoot.isValid());
-//      assertTrue(regionRoot.isResident());
-//      // Region root should have 1 child -- the one we added above
-//      assertEquals(1, getValidChildrenCount(regionRoot));
       assertEquals(1, remoteCache.keySet().size());
 
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
-//         localCache = localRegionFactory.getCacheManager().getCache("entity");
 
          remoteCfg = createConfiguration(configName);
          remoteRegionFactory = CacheTestUtil.startRegionFactory(remoteCfg);
-//         remoteCache = remoteRegionFactory.getCacheManager().getCache("entity");
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
index 9d87abff76..d3bee8471d 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/AbstractFunctionalTestCase.java
@@ -1,39 +1,38 @@
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
    
    public void testEmptySecondLevelCacheEntry() throws Exception {
-//      getSessions().evictEntity(Item.class.getName());
       getSessions().getCache().evictEntityRegion(Item.class.getName());
       Statistics stats = getSessions().getStatistics();
       stats.clear();
       SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName() + ".items");
       Map cacheEntries = statistics.getEntries();
       assertEquals(0, cacheEntries.size());
   }
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
index 061047313d..3efebe21cb 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/bulk/BulkOperationsTestCase.java
@@ -1,321 +1,300 @@
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
    
    private TransactionManager tm;
             
    public BulkOperationsTestCase(String string) {
       super(string);
    }
 
    public String[] getMappings() {
       return new String[] { "cache/infinispan/functional/Contact.hbm.xml", "cache/infinispan/functional/Customer.hbm.xml" };
    }
    
    @Override
    public String getCacheConcurrencyStrategy() {
       return "transactional";
    }
    
    protected Class getTransactionFactoryClass() {
        return CMTTransactionFactory.class;
    }
 
    protected Class getConnectionProviderClass() {
-//      return org.hibernate.test.tm.ConnectionProviderImpl.class;
       return org.hibernate.test.cache.infinispan.tm.XaConnectionProvider.class;
    }
   
    protected Class<? extends TransactionManagerLookup> getTransactionManagerLookupClass() {
       return org.hibernate.test.cache.infinispan.tm.XaTransactionManagerLookup.class;
-//      return org.hibernate.test.tm.TransactionManagerLookupImpl.class;
    }
 
    public void configure(Configuration cfg) {
       super.configure(cfg);
 
       cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
       cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
       cfg.setProperty(Environment.CONNECTION_PROVIDER, getConnectionProviderClass().getName());
       cfg.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, getTransactionManagerLookupClass().getName());
       
       Class transactionFactory = getTransactionFactoryClass();
       cfg.setProperty( Environment.TRANSACTION_STRATEGY, transactionFactory.getName());
    }
 
    public void testBulkOperations() throws Throwable {
       System.out.println("*** testBulkOperations()");
       boolean cleanedUp = false;
       try {
          tm = getTransactionManagerLookupClass().newInstance().getTransactionManager(null);
          
          createContacts();
 
          List<Integer> rhContacts = getContactsByCustomer("Red Hat");
          assertNotNull("Red Hat contacts exist", rhContacts);
          assertEquals("Created expected number of Red Hat contacts", 10, rhContacts.size());
 
          assertEquals("Deleted all Red Hat contacts", 10, deleteContacts());
 
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
          for (Integer id : jbContacts) {
             Contact contact = getContact(id);
             assertNotNull("JBoss contact " + id + " exists", contact);
             String expected = ("Kabir".equals(contact.getName())) ? "Updated" : "2222";
             assertEquals("JBoss contact " + id + " has correct TLF", expected, contact.getTlf());
          }
 
          List<Integer> updated = getContactsByTLF("Updated");
          assertNotNull("Got updated contacts", updated);
          assertEquals("Updated contacts", 5, updated.size());
       } catch(Throwable t) {
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
          List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter("cName", customerName)
                   .list();
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
          List results = session.createQuery(selectHQL).setFlushMode(FlushMode.AUTO).setParameter("cTLF", tlf).list();
          tm.commit();
          return results;
       } catch (Exception e) {
          tm.rollback();
          throw e;
       }
    }
 
    public int updateContacts(String name, String newTLF) throws Exception {
       String updateHQL = "update Contact set tlf = :cNewTLF where name = :cName";
 
       tm.begin();
       try {
 
          Session session = getSessions().getCurrentSession();
          int rowsAffected = session.createQuery(updateHQL).setFlushMode(FlushMode.AUTO).setParameter("cNewTLF", newTLF)
                   .setParameter("cName", name).executeUpdate();
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
 
-//   public void cleanup() throws Exception {
-//      String deleteContactHQL = "delete from Contact";
-//      String deleteCustomerHQL = "delete from Customer";
-//      tm.begin();
-//      try {
-//         Session session = getSessions().getCurrentSession();
-//         session.createQuery(deleteContactHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
-//         session.createQuery(deleteCustomerHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
-//         tm.commit();
-//      } catch (Exception e) {
-//         try {
-//            tm.rollback();
-//         } catch (Exception ee) {
-//            // ignored
-//         }
-//         throw e;
-//      }
-//   }
-   
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
          
          return customer;
       } finally {
          System.out.println("CREATE CUSTOMER " + id + " -  END");
       }
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
index 04712bbcf1..528dcd57b6 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/CacheAccessListener.java
@@ -1,125 +1,110 @@
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
    
-//   HashSet<Fqn<String>> modified = new HashSet<Fqn<String>>();
-//   HashSet<Fqn<String>> accessed = new HashSet<Fqn<String>>();
    HashSet modified = new HashSet();
    HashSet accessed = new HashSet();
 
    public void clear() {
       modified.clear();
       accessed.clear();
    }
 
    @CacheEntryModified
    public void nodeModified(CacheEntryModifiedEvent event) {
       if (!event.isPre()) {
          Object key = event.getKey();
          log.info("Modified node " + key);
          modified.add(key.toString());
       }
    }
    
    @CacheEntryCreated
    public void nodeCreated(CacheEntryCreatedEvent event) {
       if (!event.isPre()) {
          Object key = event.getKey();
          log.info("Created node " + key);
          modified.add(key.toString());
       }
    }
 
    @CacheEntryVisited
    public void nodeVisited(CacheEntryVisitedEvent event) {
       if (!event.isPre()) {
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
-//      boolean saw = false;
-//      for (Object key : sawEvents) {
-//         
-//      }
-//      Fqn<String> fqn = Fqn.fromString(regionName);
-//      for (Iterator<Fqn<String>> it = sawEvent.iterator(); it.hasNext();) {
-//         Fqn<String> modified = (Fqn<String>) it.next();
-//         if (modified.isChildOf(fqn)) {
-//            it.remove();
-//            saw = true;
-//         }
-//      }
-//      return saw;
-   }
+  }
 
 }
\ No newline at end of file
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java
index 667303bc74..125be92846 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/IsolatedCacheTestSetup.java
@@ -1,98 +1,67 @@
 /*
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates. All rights reserved.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, v. 2.1. This program is distributed in the
  * hope that it will be useful, but WITHOUT A WARRANTY; without even the implied
  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details. You should have received a
  * copy of the GNU Lesser General Public License, v.2.1 along with this
  * distribution; if not, write to the Free Software Foundation, Inc.,
  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
  *
  * Red Hat Author(s): Brian Stansberry
  */
 
 package org.hibernate.test.cache.infinispan.functional.classloader;
 
-import org.hibernate.cache.infinispan.InfinispanRegionFactory;
-import org.hibernate.test.cache.infinispan.functional.cluster.AbstractDualNodeTestCase;
 import org.hibernate.test.cache.infinispan.functional.cluster.ClusterAwareRegionFactory;
 import org.hibernate.test.cache.infinispan.functional.cluster.DualNodeJtaTransactionManagerImpl;
-import org.hibernate.util.PropertiesHelper;
-import org.infinispan.manager.CacheManager;
-import org.infinispan.manager.DefaultCacheManager;
 
 import junit.framework.Test;
 
 /**
  * A TestSetup that uses SelectedClassnameClassLoader to ensure that certain classes are not visible
- * to JBoss Cache or JGroups' classloader.
+ * to Infinispan or JGroups' classloader.
  * 
- * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
+ * @author Galder Zamarreño
  */
 public class IsolatedCacheTestSetup extends SelectedClassnameClassLoaderTestSetup {
 
    private String[] isolatedClasses;
    private String cacheConfig;
 
    /**
     * Create a new IsolatedCacheTestSetup.
     */
    public IsolatedCacheTestSetup(Test test, String[] isolatedClasses) {
       super(test, null, null, isolatedClasses);
       this.isolatedClasses = isolatedClasses;
-//      this.cacheConfig = cacheConfig;
    }
 
    @Override
    protected void setUp() throws Exception {
       super.setUp();
 
-//      // At this point the TCCL cannot see the isolatedClasses
-//      // We want the caches to use this CL as their default classloader
-
+      // At this point the TCCL cannot see the isolatedClasses
+      // We want the caches to use this CL as their default classloader
       ClassLoader tccl = Thread.currentThread().getContextClassLoader();
 
-//      org.jgroups.ChannelFactory cf = new org.jgroups.JChannelFactory();
-//      cf.setMultiplexerConfig(DEF_JGROUPS_RESOURCE);
-//
-//      // Use a CacheManager that will inject the desired defaultClassLoader into our caches
-//      CustomClassLoaderCacheManager cm = new CustomClassLoaderCacheManager(DEF_CACHE_FACTORY_RESOURCE, cf, tccl);
-//      cm.start();
-      
-//      CacheManager manager = new DefaultCacheManager("org/hibernate/test/cache/infinispan/functional/classloader/infinispan-configs.xml");
-//      ClusterAwareRegionFactory.addCacheManager(AbstractDualNodeTestCase.LOCAL, manager);
-//      ClusterAwareRegionFactory.addCacheManager(AbstractDualNodeTestCase.REMOTE, manager);
-
-//      cm.getCache(cacheConfig, true);
-//
-//      // Repeat for the "remote" cache
-//
-//      cf = new org.jgroups.JChannelFactory();
-//      cf.setMultiplexerConfig(DEF_JGROUPS_RESOURCE);
-//
-//      cm = new CustomClassLoaderCacheManager(DEF_CACHE_FACTORY_RESOURCE, cf, tccl);
-//      cm.start();
-//      TestCacheInstanceManager.addTestCacheManager(DualNodeTestUtil.REMOTE, cm);
-//
-//      cm.getCache(cacheConfig, true);
-
       // Now make the isolatedClasses visible to the test driver itself
       SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(isolatedClasses, null, null, tccl);
       Thread.currentThread().setContextClassLoader(visible);
    }
 
    @Override
    protected void tearDown() throws Exception {
       try {
          super.tearDown();
       } finally {
          ClusterAwareRegionFactory.clearCacheManagers();
          DualNodeJtaTransactionManagerImpl.cleanupTransactions();
          DualNodeJtaTransactionManagerImpl.cleanupTransactionManagers();
       }
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
index 0e23706673..33623ddb83 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoader.java
@@ -1,288 +1,232 @@
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
 package org.hibernate.test.cache.infinispan.functional.classloader;
 
 import java.io.ByteArrayOutputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.Map;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
- * A ClassLoader that loads classes whose classname begins with one of a
- * given set of strings, without attempting first to delegate to its
- * parent loader.
+ * A ClassLoader that loads classes whose classname begins with one of a given set of strings,
+ * without attempting first to delegate to its parent loader.
  * <p>
- * This class is intended to allow emulation of 2 different types of common J2EE
- * classloading situations.
+ * This class is intended to allow emulation of 2 different types of common J2EE classloading
+ * situations.
  * <ul>
- * <li>Servlet-style child-first classloading, where this class is the
- * child loader.</li>
- * <li>Parent-first classloading where the parent does not have access to
- * certain classes</li>
+ * <li>Servlet-style child-first classloading, where this class is the child loader.</li>
+ * <li>Parent-first classloading where the parent does not have access to certain classes</li>
  * </ul>
  * </p>
  * <p>
- * This class can also be configured to raise a ClassNotFoundException if
- * asked to load certain classes, thus allowing classes on the classpath
- * to be hidden from a test environment.
+ * This class can also be configured to raise a ClassNotFoundException if asked to load certain
+ * classes, thus allowing classes on the classpath to be hidden from a test environment.
  * </p>
- *
+ * 
  * @author Brian Stansberry
  */
-public class SelectedClassnameClassLoader extends ClassLoader
-{
+public class SelectedClassnameClassLoader extends ClassLoader {
    private Logger log = LoggerFactory.getLogger(SelectedClassnameClassLoader.class);
-   
+
    private String[] includedClasses = null;
    private String[] excludedClasses = null;
    private String[] notFoundClasses = null;
 
    private Map<String, Class> classes = new java.util.HashMap<String, Class>();
 
    /**
     * Creates a new classloader that loads the given classes.
-    *
-    * @param includedClasses array of class or package names that should be
-    *                        directly loaded by this loader.  Classes
-    *                        whose name starts with any of the strings
-    *                        in this array will be loaded by this class,
-    *                        unless their name appears in
-    *                        <code>excludedClasses</code>.
-    *                        Can be <code>null</code>
-    * @param excludedClasses array of class or package names that should NOT
-    *                        be directly loaded by this loader.  Loading of
-    *                        classes whose name starts with any of the
-    *                        strings in this array will be delegated to
-    *                        <code>parent</code>, even if the classes
-    *                        package or classname appears in
-    *                        <code>includedClasses</code>.  Typically this
-    *                        parameter is used to exclude loading one or
-    *                        more classes in a package whose other classes
-    *                        are loaded by this object.
-    * @param parent          ClassLoader to which loading of classes should
-    *                        be delegated if necessary
+    * 
+    * @param includedClasses
+    *           array of class or package names that should be directly loaded by this loader.
+    *           Classes whose name starts with any of the strings in this array will be loaded by
+    *           this class, unless their name appears in <code>excludedClasses</code>. Can be
+    *           <code>null</code>
+    * @param excludedClasses
+    *           array of class or package names that should NOT be directly loaded by this loader.
+    *           Loading of classes whose name starts with any of the strings in this array will be
+    *           delegated to <code>parent</code>, even if the classes package or classname appears
+    *           in <code>includedClasses</code>. Typically this parameter is used to exclude loading
+    *           one or more classes in a package whose other classes are loaded by this object.
+    * @param parent
+    *           ClassLoader to which loading of classes should be delegated if necessary
     */
-   public SelectedClassnameClassLoader(String[] includedClasses,
-                                       String[] excludedClasses,
-                                       ClassLoader parent)
-   {
+   public SelectedClassnameClassLoader(String[] includedClasses, String[] excludedClasses, ClassLoader parent) {
       this(includedClasses, excludedClasses, null, parent);
    }
 
    /**
     * Creates a new classloader that loads the given classes.
-    *
-    * @param includedClasses array of class or package names that should be
-    *                        directly loaded by this loader.  Classes
-    *                        whose name starts with any of the strings
-    *                        in this array will be loaded by this class,
-    *                        unless their name appears in
-    *                        <code>excludedClasses</code>.
-    *                        Can be <code>null</code>
-    * @param excludedClasses array of class or package names that should NOT
-    *                        be directly loaded by this loader.  Loading of
-    *                        classes whose name starts with any of the
-    *                        strings in this array will be delegated to
-    *                        <code>parent</code>, even if the classes
-    *                        package or classname appears in
-    *                        <code>includedClasses</code>.  Typically this
-    *                        parameter is used to exclude loading one or
-    *                        more classes in a package whose other classes
-    *                        are loaded by this object.
-    * @param notFoundClasses array of class or package names for which this
-    *                        should raise a ClassNotFoundException
-    * @param parent          ClassLoader to which loading of classes should
-    *                        be delegated if necessary
+    * 
+    * @param includedClasses
+    *           array of class or package names that should be directly loaded by this loader.
+    *           Classes whose name starts with any of the strings in this array will be loaded by
+    *           this class, unless their name appears in <code>excludedClasses</code>. Can be
+    *           <code>null</code>
+    * @param excludedClasses
+    *           array of class or package names that should NOT be directly loaded by this loader.
+    *           Loading of classes whose name starts with any of the strings in this array will be
+    *           delegated to <code>parent</code>, even if the classes package or classname appears
+    *           in <code>includedClasses</code>. Typically this parameter is used to exclude loading
+    *           one or more classes in a package whose other classes are loaded by this object.
+    * @param notFoundClasses
+    *           array of class or package names for which this should raise a ClassNotFoundException
+    * @param parent
+    *           ClassLoader to which loading of classes should be delegated if necessary
     */
-   public SelectedClassnameClassLoader(String[] includedClasses,
-                                       String[] excludedClasses,
-                                       String[] notFoundClasses,
-                                       ClassLoader parent)
-   {
+   public SelectedClassnameClassLoader(String[] includedClasses, String[] excludedClasses, String[] notFoundClasses,
+            ClassLoader parent) {
       super(parent);
       this.includedClasses = includedClasses;
       this.excludedClasses = excludedClasses;
       this.notFoundClasses = notFoundClasses;
-      
+
       log.debug("created " + this);
    }
 
-   protected synchronized Class<?> loadClass(String name, boolean resolve)
-         throws ClassNotFoundException
-   {
+   protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
       log.trace("loadClass(" + name + "," + resolve + ")");
-      if (isIncluded(name) && (isExcluded(name) == false))
-      {
+      if (isIncluded(name) && (isExcluded(name) == false)) {
          Class c = findClass(name);
 
-         if (resolve)
-         {
+         if (resolve) {
             resolveClass(c);
          }
          return c;
-      }
-      else if (isNotFound(name))
-      {
+      } else if (isNotFound(name)) {
          throw new ClassNotFoundException(name + " is discarded");
-      }
-      else
-      {
+      } else {
          return super.loadClass(name, resolve);
       }
    }
 
-   protected Class<?> findClass(String name) throws ClassNotFoundException
-   {
+   protected Class<?> findClass(String name) throws ClassNotFoundException {
       log.trace("findClass(" + name + ")");
       Class result = classes.get(name);
-      if (result != null)
-      {
+      if (result != null) {
          return result;
       }
 
-      if (isIncluded(name) && (isExcluded(name) == false))
-      {
+      if (isIncluded(name) && (isExcluded(name) == false)) {
          result = createClass(name);
-      }
-      else if (isNotFound(name))
-      {
+      } else if (isNotFound(name)) {
          throw new ClassNotFoundException(name + " is discarded");
-      }
-      else
-      {
+      } else {
          result = super.findClass(name);
       }
 
       classes.put(name, result);
 
       return result;
    }
 
-   protected Class createClass(String name) throws ClassFormatError, ClassNotFoundException
-   {
+   protected Class createClass(String name) throws ClassFormatError, ClassNotFoundException {
       log.info("createClass(" + name + ")");
-      try
-      {
+      try {
          InputStream is = getResourceAsStream(name.replace('.', '/').concat(".class"));
          byte[] bytes = new byte[1024];
          ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
          int read;
-         while ((read = is.read(bytes)) > -1)
-         {
+         while ((read = is.read(bytes)) > -1) {
             baos.write(bytes, 0, read);
          }
          bytes = baos.toByteArray();
          return this.defineClass(name, bytes, 0, bytes.length);
-      }
-      catch (FileNotFoundException e)
-      {
+      } catch (FileNotFoundException e) {
          throw new ClassNotFoundException("cannot find " + name, e);
-      }
-      catch (IOException e)
-      {
+      } catch (IOException e) {
          throw new ClassNotFoundException("cannot read " + name, e);
       }
    }
 
-   protected boolean isIncluded(String className)
-   {
+   protected boolean isIncluded(String className) {
 
-      if (includedClasses != null)
-      {
-         for (int i = 0; i < includedClasses.length; i++)
-         {
-            if (className.startsWith(includedClasses[i]))
-            {
+      if (includedClasses != null) {
+         for (int i = 0; i < includedClasses.length; i++) {
+            if (className.startsWith(includedClasses[i])) {
                return true;
             }
          }
       }
 
       return false;
    }
 
-   protected boolean isExcluded(String className)
-   {
+   protected boolean isExcluded(String className) {
 
-      if (excludedClasses != null)
-      {
-         for (int i = 0; i < excludedClasses.length; i++)
-         {
-            if (className.startsWith(excludedClasses[i]))
-            {
+      if (excludedClasses != null) {
+         for (int i = 0; i < excludedClasses.length; i++) {
+            if (className.startsWith(excludedClasses[i])) {
                return true;
             }
          }
       }
 
       return false;
    }
 
-   protected boolean isNotFound(String className)
-   {
+   protected boolean isNotFound(String className) {
 
-      if (notFoundClasses != null)
-      {
-         for (int i = 0; i < notFoundClasses.length; i++)
-         {
-            if (className.startsWith(notFoundClasses[i]))
-            {
+      if (notFoundClasses != null) {
+         for (int i = 0; i < notFoundClasses.length; i++) {
+            if (className.startsWith(notFoundClasses[i])) {
                return true;
             }
          }
       }
 
       return false;
    }
-   
-   public String toString()  {
+
+   public String toString() {
       String s = getClass().getName();
       s += "[includedClasses=";
       s += listClasses(includedClasses);
       s += ";excludedClasses=";
       s += listClasses(excludedClasses);
       s += ";notFoundClasses=";
       s += listClasses(notFoundClasses);
       s += ";parent=";
       s += getParent();
       s += "]";
       return s;
    }
-   
+
    private static String listClasses(String[] classes) {
-      if (classes == null) return null;
+      if (classes == null)
+         return null;
       String s = "";
       for (int i = 0; i < classes.length; i++) {
          if (i > 0)
             s += ",";
          s += classes[i];
       }
       return s;
    }
-   
+
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java
index 74c12ec4ca..9b011b2b73 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/classloader/SelectedClassnameClassLoaderTestSetup.java
@@ -1,73 +1,63 @@
 /*
  * Copyright (c) 2007, Red Hat, Inc. and/or it's affiliates. All rights reserved.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, v. 2.1. This program is distributed in the
  * hope that it will be useful, but WITHOUT A WARRANTY; without even the implied
  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details. You should have received a
  * copy of the GNU Lesser General Public License, v.2.1 along with this
  * distribution; if not, write to the Free Software Foundation, Inc.,
  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
  *
  * Red Hat Author(s): Brian Stansberry
  */
 
 package org.hibernate.test.cache.infinispan.functional.classloader;
 
 import junit.extensions.TestSetup;
 import junit.framework.Test;
 
 /**
- * A TestSetup that makes SelectedClassnameClassLoader the thread
- * context classloader for the duration of the test.
+ * A TestSetup that makes SelectedClassnameClassLoader the thread context classloader for the
+ * duration of the test.
  * 
  * @author <a href="brian.stansberry@jboss.com">Brian Stansberry</a>
  * @version $Revision: 1 $
  */
-public class SelectedClassnameClassLoaderTestSetup extends TestSetup
-{
+public class SelectedClassnameClassLoaderTestSetup extends TestSetup {
    private ClassLoader originalTCCL;
    private String[] includedClasses;
    private String[] excludedClasses;
    private String[] notFoundClasses;
-   
-   
+
    /**
     * Create a new SelectedClassnameClassLoaderTestSetup.
     * 
     * @param test
     */
-   public SelectedClassnameClassLoaderTestSetup(Test test,
-                                                String[] includedClasses,
-                                                String[] excludedClasses,
-                                                String[] notFoundClasses)
-   {
+   public SelectedClassnameClassLoaderTestSetup(Test test, String[] includedClasses, String[] excludedClasses, String[] notFoundClasses) {
       super(test);
       this.includedClasses = includedClasses;
       this.excludedClasses = excludedClasses;
       this.notFoundClasses = notFoundClasses;
    }
 
    @Override
-   protected void setUp() throws Exception
-   {      
+   protected void setUp() throws Exception {
       super.setUp();
-      
+
       originalTCCL = Thread.currentThread().getContextClassLoader();
       ClassLoader parent = originalTCCL == null ? getClass().getClassLoader() : originalTCCL;
       ClassLoader selectedTCCL = new SelectedClassnameClassLoader(includedClasses, excludedClasses, notFoundClasses, parent);
       Thread.currentThread().setContextClassLoader(selectedTCCL);
    }
 
    @Override
-   protected void tearDown() throws Exception
-   {
+   protected void tearDown() throws Exception {
       Thread.currentThread().setContextClassLoader(originalTCCL);
       super.tearDown();
    }
-   
-   
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
index 5522418939..edf1107669 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
@@ -1,387 +1,355 @@
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
 import org.hibernate.test.cache.infinispan.functional.Contact;
 import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheManager;
 import org.infinispan.marshall.MarshalledValue;
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
          assertTrue(localCollectionCache.isEmpty());
          assertTrue(localCustomerCache.isEmpty());
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
-         
-//         Set<Contact> contacts = customer.getContacts();
-//         for (Contact c : contacts) {
-//            if (c.getName().equals("Kabir")) {
-//               contacts.remove(c);
-//            } else {
-//               contactIds.add(c.getId());
-//            }
-//         }
-//         ids.contactIds = contactIds;
-//         customer.setContacts(contacts);
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
             MarshalledValue mv = (MarshalledValue) event.getKey();
             CacheKey cacheKey = (CacheKey) mv.get();
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
-      
-//      @CacheEntryModified
-//      public void nodeModified(CacheEntryModifiedEvent event) {
-//         log.debug(event.toString());
-//         if (!event.isPre()) {
-//            MarshalledValue mv = (MarshalledValue) event.getKey();
-//            CacheKey cacheKey = (CacheKey) mv.get();
-//            Integer primKey = (Integer) cacheKey.getKey();
-//            String key = (String) cacheKey.getEntityOrRoleName() + '#' + primKey;
-//            log.debug("MyListener[" + name +"] - Modified key " + key);
-//            // String name = fqn.toString();
-//            String token = ".functional.";
-//            int index = key.indexOf(token);
-//            if (index > -1) {
-//               index += token.length();
-//               key = key.substring(index);
-//               log.debug("MyListener[" + name +"] - recording modification of " + key);
-//               visited.add(key);
-//            }
-//         }
-//      }
    }
 
    private class IdContainer {
       Integer customerId;
       Set<Integer> contactIds;
    }
 
 }
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index 7ac9231c8b..103ade40db 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,303 +1,299 @@
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
 import org.hibernate.cfg.Configuration;
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.infinispan.Cache;
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
    protected Cache getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return regionFactory.getCacheManager().getCache("local-query");
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
 
-   // public void testGetDoesNotBlockPutPessimisticRepeatableRead() throws Exception {
-   // getDoesNotBlockPutTest();
-   // }
-
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
       final Cache jbc = getInfinispanCache(regionFactory);
 
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
diff --git a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
index c01a2b4ea1..b68e75de6c 100644
--- a/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
+++ b/cache-infinispan/src/test/java/org/hibernate/test/cache/infinispan/tm/XaTransactionImpl.java
@@ -1,247 +1,237 @@
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
 
-//         if (!runXaResourcePrepare()) {
-//            status = Status.STATUS_ROLLING_BACK;
-//         } else {
-//            status = Status.STATUS_PREPARED;
-//         }
-
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
 
-//         runXaResourceCommitTx();
-
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
-//      status = Status.STATUS_ROLLING_BACK;
-//      runXaResourceRollback();
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
