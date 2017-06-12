diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
index 0657c604da..161a099880 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
@@ -1,420 +1,571 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2;
 
+import java.util.Collections;
 import java.util.HashMap;
+import java.util.HashSet;
+import java.util.List;
 import java.util.Map;
 import java.util.Set;
+import java.util.concurrent.atomic.AtomicReference;
 
 import javax.transaction.SystemException;
 import javax.transaction.Transaction;
 import javax.transaction.TransactionManager;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.jbc2.util.CacheHelper;
 import org.hibernate.cache.jbc2.util.NonLockingDataVersion;
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 import org.jboss.cache.Node;
 import org.jboss.cache.NodeSPI;
 import org.jboss.cache.config.Configuration;
 import org.jboss.cache.config.Option;
 import org.jboss.cache.config.Configuration.NodeLockingScheme;
-import org.jboss.cache.notifications.annotation.CacheListener;
+import org.jboss.cache.notifications.annotation.NodeInvalidated;
+import org.jboss.cache.notifications.annotation.NodeModified;
+import org.jboss.cache.notifications.annotation.ViewChanged;
+import org.jboss.cache.notifications.event.NodeInvalidatedEvent;
+import org.jboss.cache.notifications.event.NodeModifiedEvent;
+import org.jboss.cache.notifications.event.ViewChangedEvent;
 import org.jboss.cache.optimistic.DataVersion;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * General support for writing {@link Region} implementations for JBoss Cache
  * 2.x.
  * 
  * @author Steve Ebersole
  */
-@CacheListener
 public abstract class BasicRegionAdapter implements Region {
    
+    private enum InvalidateState { INVALID, CLEARING, VALID };
     
     public static final String ITEM = CacheHelper.ITEM;
 
     protected final Cache jbcCache;
     protected final String regionName;
     protected final Fqn regionFqn;
+    protected final Fqn internalFqn;
     protected Node regionRoot;
     protected final boolean optimistic;
     protected final TransactionManager transactionManager;
     protected final Logger log;
     protected final Object regionRootMutex = new Object();
+    protected final Object memberId;
+    protected final boolean replication;
+    protected final Object invalidationMutex = new Object();
+    protected final AtomicReference<InvalidateState> invalidateState = 
+       new AtomicReference<InvalidateState>(InvalidateState.VALID);
+    protected final Set<Object> currentView = new HashSet<Object>();
 
 //    protected RegionRootListener listener;
     
     public BasicRegionAdapter(Cache jbcCache, String regionName, String regionPrefix) {
+
+        this.log = LoggerFactory.getLogger(getClass());
+        
         this.jbcCache = jbcCache;
         this.transactionManager = jbcCache.getConfiguration().getRuntimeConfig().getTransactionManager();
         this.regionName = regionName;
         this.regionFqn = createRegionFqn(regionName, regionPrefix);
-        optimistic = jbcCache.getConfiguration().getNodeLockingScheme() == NodeLockingScheme.OPTIMISTIC;
-        log = LoggerFactory.getLogger(getClass());
+        this.internalFqn = CacheHelper.getInternalFqn(regionFqn);
+        this.optimistic = jbcCache.getConfiguration().getNodeLockingScheme() == NodeLockingScheme.OPTIMISTIC;
+        this.memberId = jbcCache.getLocalAddress();
+        this.replication = CacheHelper.isClusteredReplication(jbcCache);
+        
+        this.jbcCache.addCacheListener(this);
+        
+        synchronized (currentView) {
+           List view = jbcCache.getMembers();
+           if (view != null) {
+              currentView.addAll(view);
+           }
+        }
+        
         activateLocalClusterNode();
         
         log.debug("Created Region for " + regionName + " -- regionPrefix is " + regionPrefix);
     }
 
     protected abstract Fqn<String> createRegionFqn(String regionName, String regionPrefix);
 
     protected void activateLocalClusterNode() {
        
         // Regions can get instantiated in the course of normal work (e.g.
         // a named query region will be created the first time the query is
         // executed), so suspend any ongoing tx
         Transaction tx = suspend();
         try {
             Configuration cfg = jbcCache.getConfiguration();
             if (cfg.isUseRegionBasedMarshalling()) {
                 org.jboss.cache.Region jbcRegion = jbcCache.getRegion(regionFqn, true);
                 ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                 if (classLoader == null) {
                     classLoader = getClass().getClassLoader();
                 }
                 jbcRegion.registerContextClassLoader(classLoader);
                 if ( !jbcRegion.isActive() ) {
                     jbcRegion.activate();
                 }
             }
             
 //            // If we are using replication, we may remove the root node
 //            // and then need to re-add it. In that case, the fact
 //            // that it is resident will not replicate, so use a listener
 //            // to set it as resident
 //            if (CacheHelper.isClusteredReplication(cfg.getCacheMode()) 
 //                  || CacheHelper.isClusteredInvalidation(cfg.getCacheMode())) {
 //                listener = new RegionRootListener();
 //                jbcCache.addCacheListener(listener);
 //            }
             
             regionRoot = jbcCache.getRoot().getChild( regionFqn );
             if (regionRoot == null || !regionRoot.isValid()) {
                // Establish the region root node with a non-locking data version
                DataVersion version = optimistic ? NonLockingDataVersion.INSTANCE : null;
                regionRoot = CacheHelper.addNode(jbcCache, regionFqn, true, true, version);
             }
             else if (optimistic && regionRoot instanceof NodeSPI) {
                 // FIXME Hacky workaround to JBCACHE-1202
                 if ( !( ( ( NodeSPI ) regionRoot ).getVersion() instanceof NonLockingDataVersion ) ) {
                     ((NodeSPI) regionRoot).setVersion(NonLockingDataVersion.INSTANCE);
                 }
             }
             if (!regionRoot.isResident()) {
                regionRoot.setResident(true);
             }
+            establishInternalNodes();
         }
         catch (Exception e) {
             throw new CacheException(e.getMessage(), e);
         }
         finally {
-            if (tx != null)
-               resume(tx);
+            resume(tx);
         }
         
     }
 
     private void establishRegionRootNode()
     {
         synchronized (regionRootMutex) {
             // If we've been blocking for the mutex, perhaps another
             // thread has already reestablished the root.
             // In case the node was reestablised via replication, confirm it's 
             // marked "resident" (a status which doesn't replicate)
             if (regionRoot != null && regionRoot.isValid()) {
                 return;
             }
             
             // For pessimistic locking, we just want to toss out our ref
             // to any old invalid root node and get the latest (may be null)            
             if (!optimistic) {
+               establishInternalNodes();
                regionRoot = jbcCache.getRoot().getChild( regionFqn );
                return;
             }
             
             // The rest only matters for optimistic locking, where we
             // need to establish the proper data version on the region root
             
             // Don't hold a transactional lock for this 
             Transaction tx = suspend();
             Node newRoot = null;
             try {
                  // Make sure the root node for the region exists and 
                  // has a DataVersion that never complains
                  newRoot = jbcCache.getRoot().getChild( regionFqn );
                  if (newRoot == null || !newRoot.isValid()) {                
                      // Establish the region root node with a non-locking data version
                      DataVersion version = optimistic ? NonLockingDataVersion.INSTANCE : null;
                      newRoot = CacheHelper.addNode(jbcCache, regionFqn, true, true, version);    
                  }
                  else if (newRoot instanceof NodeSPI) {
                      // FIXME Hacky workaround to JBCACHE-1202
                      if ( !( ( ( NodeSPI ) newRoot ).getVersion() instanceof NonLockingDataVersion ) ) {
                           ((NodeSPI) newRoot).setVersion(NonLockingDataVersion.INSTANCE);
                      }
                  }
                  // Never evict this node
                  newRoot.setResident(true);
+                 establishInternalNodes();
             }
             finally {
                 resume(tx);
                 regionRoot = newRoot;
             }
         }
     }
 
+    private void establishInternalNodes()
+    {
+       synchronized (currentView) {
+          Transaction tx = suspend();
+          try {
+             for (Object member : currentView) {
+                DataVersion version = optimistic ? NonLockingDataVersion.INSTANCE : null;
+                Fqn f = Fqn.fromRelativeElements(internalFqn, member);
+                CacheHelper.addNode(jbcCache, f, true, false, version);
+             }
+          }
+          finally {
+             resume(tx);
+          }
+       }
+       
+    }
+
     public String getName() {
         return regionName;
     }
 
     public Cache getCacheInstance() {
         return jbcCache;
     }
 
     public Fqn getRegionFqn() {
         return regionFqn;
     }
     
+    public Object getMemberId()
+    {
+       return this.memberId;
+    }
+    
     /**
      * Checks for the validity of the root cache node for this region,
      * creating a new one if it does not exist or is invalid, and also
      * ensuring that the root node is marked as resident.  Suspends any 
      * transaction while doing this to ensure no transactional locks are held 
      * on the region root.
      * 
      * TODO remove this once JBCACHE-1250 is resolved.
      */
     public void ensureRegionRootExists() {
        
        if (regionRoot == null || !regionRoot.isValid())
           establishRegionRootNode();
        
        // Fix up the resident flag
        if (regionRoot != null && regionRoot.isValid() && !regionRoot.isResident())
           regionRoot.setResident(true);
     }
+    
+    public boolean checkValid()
+    {
+       boolean valid = invalidateState.get() == InvalidateState.VALID;
+       
+       if (!valid) {
+          synchronized (invalidationMutex) {
+             if (invalidateState.compareAndSet(InvalidateState.INVALID, InvalidateState.CLEARING)) {
+                Transaction tx = suspend();
+                try {
+                   Option opt = new Option();
+                   opt.setLockAcquisitionTimeout(1);
+                   opt.setCacheModeLocal(true);
+                   CacheHelper.removeAll(jbcCache, regionFqn, opt);
+                   invalidateState.compareAndSet(InvalidateState.CLEARING, InvalidateState.VALID);
+                }
+                catch (Exception e) {
+                   if (log.isTraceEnabled()) {
+                      log.trace("Could not invalidate region: " + e.getLocalizedMessage());
+                   }
+                }
+                finally {
+                   resume(tx);
+                }
+             }
+          }
+          valid = invalidateState.get() == InvalidateState.VALID;
+       }
+       
+       return valid;   
+    }
 
     public void destroy() throws CacheException {
         try {
             // NOTE : this is being used from the process of shutting down a
             // SessionFactory. Specific things to consider:
             // (1) this clearing of the region should not propagate to
             // other nodes on the cluster (if any); this is the
             // cache-mode-local option bit...
             // (2) really just trying a best effort to cleanup after
             // ourselves; lock failures, etc are not critical here;
             // this is the fail-silently option bit...
             Option option = new Option();
             option.setCacheModeLocal(true);
             option.setFailSilently(true);
             if (optimistic) {
                 option.setDataVersion(NonLockingDataVersion.INSTANCE);
             }
             jbcCache.getInvocationContext().setOptionOverrides(option);
             jbcCache.removeNode(regionFqn);
             deactivateLocalNode();            
         } catch (Exception e) {
             throw new CacheException(e);
         }
-//        finally {
-//            if (listener != null)
-//                jbcCache.removeCacheListener(listener);
-//        }
+        finally {
+            jbcCache.removeCacheListener(this);
+        }
     }
 
     protected void deactivateLocalNode() {
         org.jboss.cache.Region jbcRegion = jbcCache.getRegion(regionFqn, false);
         if (jbcRegion != null && jbcRegion.isActive()) {
             jbcRegion.deactivate();
             jbcRegion.unregisterContextClassLoader();
         }
     }
 
     public long getSizeInMemory() {
         // not supported
         return -1;
     }
 
     public long getElementCountInMemory() {
-        try {
-            Set childrenNames = CacheHelper.getChildrenNames(jbcCache, regionFqn);
-            return childrenNames.size();
-        } catch (Exception e) {
-            throw new CacheException(e);
+        if (checkValid()) {
+           try {
+               Set childrenNames = CacheHelper.getChildrenNames(jbcCache, regionFqn);
+               int size = childrenNames.size();
+               if (childrenNames.contains(CacheHelper.Internal.NODE)) {
+                  size--;
+               }
+               return size;
+           } catch (Exception e) {
+               throw new CacheException(e);
+           }
         }
+        else {
+           return 0;
+        }           
     }
 
     public long getElementCountOnDisk() {
         return -1;
     }
 
     public Map toMap() {
-        try {
-            Map result = new HashMap();
-            Set childrenNames = CacheHelper.getChildrenNames(jbcCache, regionFqn);
-            for (Object childName : childrenNames) {
-                result.put(childName, CacheHelper.get(jbcCache,regionFqn, childName));
-            }
-            return result;
-        } catch (CacheException e) {
-            throw e;
-        } catch (Exception e) {
-            throw new CacheException(e);
+        if (checkValid()) {
+           try {
+               Map result = new HashMap();
+               Set childrenNames = CacheHelper.getChildrenNames(jbcCache, regionFqn);
+               for (Object childName : childrenNames) {
+                   if (CacheHelper.Internal.NODE != childName) {
+                      result.put(childName, CacheHelper.get(jbcCache,regionFqn, childName));
+                   }
+               }
+               return result;
+           } catch (CacheException e) {
+               throw e;
+           } catch (Exception e) {
+               throw new CacheException(e);
+           }
+        }
+        else {
+           return Collections.emptyMap();
         }
     }
 
     public long nextTimestamp() {
         return System.currentTimeMillis() / 100;
     }
 
     public int getTimeout() {
         return 600; // 60 seconds
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
     protected Object suspendAndGet(Object key, Option opt, boolean suppressTimeout) throws CacheException {
         Transaction tx = suspend();
         try {
             CacheHelper.setInvocationOption(getCacheInstance(), opt);
             if (suppressTimeout)
                 return CacheHelper.getAllowingTimeout(getCacheInstance(), getRegionFqn(), key);
             else
                 return CacheHelper.get(getCacheInstance(), getRegionFqn(), key);
         } finally {
             resume(tx);
         }
     }
+    
+    public Object getOwnerForPut()
+    {
+       Transaction tx = null;
+       try {
+           if (transactionManager != null) {
+               tx = transactionManager.getTransaction();
+           }
+       } catch (SystemException se) {
+           throw new CacheException("Could not obtain transaction", se);
+       }
+       return tx == null ? Thread.currentThread() : tx;
+       
+    }
 
     /**
      * Tell the TransactionManager to suspend any ongoing transaction.
      * 
      * @return the transaction that was suspended, or <code>null</code> if
      *         there wasn't one
      */
-    protected Transaction suspend() {
+    public Transaction suspend() {
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
-    protected void resume(Transaction tx) {
+    public void resume(Transaction tx) {
         try {
             if (tx != null)
                 transactionManager.resume(tx);
         } catch (Exception e) {
             throw new CacheException("Could not resume transaction", e);
         }
     }
 
     /**
      * Get an Option with a {@link Option#getDataVersion() data version}
      * of {@link NonLockingDataVersion}.  The data version will not be 
      * set if the cache is not configured for optimistic locking.
      * 
      * @param allowNullReturn If <code>true</code>, return <code>null</code>
      *                        if the cache is not using optimistic locking.
      *                        If <code>false</code>, return a default
      *                        {@link Option}.
      *                        
      * @return the Option, or <code>null</code>.
      */
     protected Option getNonLockingDataVersionOption(boolean allowNullReturn) {
         return optimistic ? NonLockingDataVersion.getInvocationOption() 
                           : (allowNullReturn) ? null : new Option();
     }
 
     public static Fqn<String> getTypeFirstRegionFqn(String regionName, String regionPrefix, String regionType) {
         Fqn<String> base = Fqn.fromString(regionType);
         Fqn<String> added = Fqn.fromString(escapeRegionName(regionName, regionPrefix));
         return new Fqn<String>(base, added);
     }
 
     public static Fqn<String> getTypeLastRegionFqn(String regionName, String regionPrefix, String regionType) {
         Fqn<String> base = Fqn.fromString(escapeRegionName(regionName, regionPrefix));
         return new Fqn<String>(base, regionType);
     }
 
     public static String escapeRegionName(String regionName, String regionPrefix) {
         String escaped = null;
         int idx = -1;
         if (regionPrefix != null) {
             idx = regionName.indexOf(regionPrefix);
         }
 
         if (idx > -1) {
             int regionEnd = idx + regionPrefix.length();
             String prefix = regionName.substring(0, regionEnd);
             String suffix = regionName.substring(regionEnd);
             suffix = suffix.replace('.', '/');
             escaped = prefix + suffix;
         } else {
             escaped = regionName.replace('.', '/');
             if (regionPrefix != null && regionPrefix.length() > 0) {
                 escaped = regionPrefix + "/" + escaped;
             }
         }
         return escaped;
     }
     
-//    @CacheListener
-//    public class RegionRootListener {
-//        
-//        @NodeCreated
-//        public void nodeCreated(NodeCreatedEvent event) {
-//            if (!event.isPre() && event.getFqn().equals(getRegionFqn())) {
-//                log.debug("Node created for " + getRegionFqn());
-//                Node regionRoot = jbcCache.getRoot().getChild(getRegionFqn());
-//                regionRoot.setResident(true);
-//            }
-//        }
-//        
-//    }
+    @NodeModified
+    public void nodeModified(NodeModifiedEvent event)
+    {
+       handleEvictAllModification(event);
+    }
+    
+    protected boolean handleEvictAllModification(NodeModifiedEvent event) {
+       
+       if (!event.isPre() && (replication || event.isOriginLocal()) && event.getData().containsKey(ITEM))
+       {
+          if (event.getFqn().isChildOf(internalFqn))
+          {
+             invalidateState.set(InvalidateState.INVALID);
+             return true;
+          }
+       }
+       return false;       
+    }
+    
+    @NodeInvalidated
+    public void nodeInvalidated(NodeInvalidatedEvent event)
+    {
+       handleEvictAllInvalidation(event);
+    }
+    
+    protected boolean handleEvictAllInvalidation(NodeInvalidatedEvent event)
+    {
+       if (!event.isPre() && event.getFqn().isChildOf(internalFqn))
+       {
+          invalidateState.set(InvalidateState.INVALID);
+          return true;
+       }      
+       return false;
+    }
+    
+    @ViewChanged
+    public void viewChanged(ViewChangedEvent event) {
+       
+       synchronized (currentView) {
+          List view = event.getNewView().getMembers();
+          if (view != null) {
+             currentView.addAll(view);
+             establishInternalNodes();
+          }
+       }
+       
+    }
+   
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java
index 3892850d72..2bb8b94fe7 100755
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java
@@ -1,175 +1,194 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2.access;
 
+import javax.transaction.Transaction;
+
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
-import org.hibernate.cache.jbc2.BasicRegionAdapter;
 import org.hibernate.cache.jbc2.TransactionalDataRegionAdapter;
 import org.hibernate.cache.jbc2.util.CacheHelper;
 import org.hibernate.cache.jbc2.util.DataVersionAdapter;
 import org.hibernate.cache.jbc2.util.NonLockingDataVersion;
 import org.jboss.cache.config.Option;
 import org.jboss.cache.optimistic.DataVersion;
 
 /**
  * Defines the strategy for transactional access to entity or collection data in
  * an optimistic-locking JBoss Cache using its 2.x APIs.
  * <p>
  * The intent of this class is to encapsulate common code and serve as a
  * delegate for {@link EntityRegionAccessStrategy} and
  * {@link CollectionRegionAccessStrategy} implementations.
  * </p>
  * 
  * @author Brian Stansberry
  * @version $Revision: 1 $
  */
 public class OptimisticTransactionalAccessDelegate extends TransactionalAccessDelegate {
 
     protected final CacheDataDescription dataDescription;
 
     public OptimisticTransactionalAccessDelegate(TransactionalDataRegionAdapter region) {
         super(region);
         this.dataDescription = region.getCacheDataDescription();
     }
 
     /**
      * Overrides the
      * {@link TransactionalAccessDelegate#evict(Object) superclass} by adding a
      * {@link NonLockingDataVersion} to the invocation.
      */
     @Override
     public void evict(Object key) throws CacheException {
-        
+        pendingPuts.remove(key);
         region.ensureRegionRootExists();
 
         Option opt = NonLockingDataVersion.getInvocationOption();
         CacheHelper.remove(cache, regionFqn, key, opt);
-    }
-
-    /**
-     * Overrides the {@link TransactionalAccessDelegate#evictAll() superclass}
-     * by adding a {@link NonLockingDataVersion} to the invocation.
-     */
-    @Override
-    public void evictAll() throws CacheException {
-
-        evictOrRemoveAll();
-    }    
+    } 
     
-    /**
-     * Overrides the {@link TransactionalAccessDelegate#get(Object, long) superclass}
-     * by {@link BasicRegionAdapter#ensureRegionRootExists() ensuring the root
-     * node for the region exists} before making the call.
-     */
+    
+
     @Override
-    public Object get(Object key, long txTimestamp) throws CacheException
+    public void evictAll() throws CacheException
     {
-        region.ensureRegionRootExists();
-        
-        return CacheHelper.get(cache, regionFqn, key);
+       pendingPuts.clear();
+       Transaction tx = region.suspend();
+       try {        
+          region.ensureRegionRootExists();
+          Option opt = NonLockingDataVersion.getInvocationOption();
+          CacheHelper.sendEvictAllNotification(cache, regionFqn, region.getMemberId(), opt);
+       }
+       finally {
+          region.resume(tx);
+       }
     }
 
-    /**
+   /**
      * Overrides the
      * {@link TransactionalAccessDelegate#insert(Object, Object, Object) superclass}
      * by adding a {@link DataVersion} to the invocation.
      */
     @Override
     public boolean insert(Object key, Object value, Object version) throws CacheException {
+       
+        pendingPuts.remove(key);
+        
+        if (!region.checkValid())
+            return false;
         
         region.ensureRegionRootExists();
 
         Option opt = getDataVersionOption(version, null);
         CacheHelper.put(cache, regionFqn, key, value, opt);
         return true;
     }
 
     @Override
     public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
             throws CacheException {
+       
+        if (!region.checkValid())
+            return false;
+        
+        if (!isPutValid(key))
+           return false;
         
         region.ensureRegionRootExists();
 
         // We ignore minimalPutOverride. JBossCache putForExternalRead is
         // already about as minimal as we can get; it will promptly return
         // if it discovers that the node we want to write to already exists
         Option opt = getDataVersionOption(version, version);
         return CacheHelper.putForExternalRead(cache, regionFqn, key, value, opt);
     }
 
     @Override
     public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
+       
+        if (!region.checkValid())
+            return false;
+        
+        if (!isPutValid(key))
+           return false;
         
         region.ensureRegionRootExists();
 
         Option opt = getDataVersionOption(version, version);
         return CacheHelper.putForExternalRead(cache, regionFqn, key, value, opt);
     }
 
     @Override
     public void remove(Object key) throws CacheException {
+       
+        pendingPuts.remove(key);
+        
+        // We remove whether or not the region is valid. Other nodes
+        // may have already restored the region so they need to
+        // be informed of the change.
         
         region.ensureRegionRootExists();
 
         Option opt = NonLockingDataVersion.getInvocationOption();
         CacheHelper.remove(cache, regionFqn, key, opt);
     }
 
     @Override
     public void removeAll() throws CacheException {
-
-        evictOrRemoveAll();  
+       pendingPuts.clear();
+       Option opt = NonLockingDataVersion.getInvocationOption();
+       CacheHelper.removeAll(cache, regionFqn, opt);
     }
 
     @Override
     public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
             throws CacheException {
+       
+        pendingPuts.remove(key);
+        
+        // We update whether or not the region is valid. Other nodes
+        // may have already restored the region so they need to
+        // be informed of the change.
         
         region.ensureRegionRootExists();
 
         Option opt = getDataVersionOption(currentVersion, previousVersion);
         CacheHelper.put(cache, regionFqn, key, value, opt);
         return true;
     }
 
     private Option getDataVersionOption(Object currentVersion, Object previousVersion) {
         
         DataVersion dv = (dataDescription != null && dataDescription.isVersioned()) ? new DataVersionAdapter(
                 currentVersion, previousVersion, dataDescription.getVersionComparator(), dataDescription.toString())
                 : NonLockingDataVersion.INSTANCE;
         Option opt = new Option();
         opt.setDataVersion(dv);
         return opt;
     }
 
-    private void evictOrRemoveAll() {
-       
-        Option opt = NonLockingDataVersion.getInvocationOption();
-        CacheHelper.removeAll(cache, regionFqn, opt);
-    }
-
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java
index 61e9e317ed..46e139000e 100755
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java
@@ -1,148 +1,236 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2.access;
 
+import java.util.HashSet;
+import java.util.Set;
+import java.util.concurrent.ConcurrentHashMap;
+import java.util.concurrent.ConcurrentMap;
+
+import javax.transaction.Transaction;
+
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.jbc2.BasicRegionAdapter;
 import org.hibernate.cache.jbc2.util.CacheHelper;
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 
 /**
  * Defines the strategy for transactional access to entity or collection data in
  * a pessimistic-locking JBoss Cache using its 2.x APIs.
  * <p>
  * The intent of this class is to encapsulate common code and serve as a
  * delegate for {@link EntityRegionAccessStrategy} and
  * {@link CollectionRegionAccessStrategy} implementations.
  * </p>
  * 
  * @author Brian Stansberry
  */
 public class TransactionalAccessDelegate {
-
+        
     protected final Cache cache;
     protected final Fqn regionFqn;
     protected final BasicRegionAdapter region;
+    protected final ConcurrentMap<Object, Set<Object>> pendingPuts = 
+       new ConcurrentHashMap<Object, Set<Object>>();
 
     public TransactionalAccessDelegate(BasicRegionAdapter adapter) {
         this.region = adapter;
         this.cache = adapter.getCacheInstance();
         this.regionFqn = adapter.getRegionFqn();
     }
 
     public Object get(Object key, long txTimestamp) throws CacheException {
-       
+        
+        if (!region.checkValid())
+           return null;
+        
         region.ensureRegionRootExists();
         
-        return CacheHelper.get(cache, regionFqn, key);
+        Object val = CacheHelper.get(cache, regionFqn, key);
+        
+        if (val == null) {
+           registerPendingPut(key);
+        }
+        
+        return val;
     }
 
-    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
+   public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
+       
+        if (!region.checkValid())
+            return false;
+        
+        if (!isPutValid(key))
+            return false;
        
         region.ensureRegionRootExists();
 
         return CacheHelper.putForExternalRead(cache, regionFqn, key, value);
     }
 
-    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+   public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
             throws CacheException {
        
+        if (!region.checkValid())
+            return false;
+        
+        if (!isPutValid(key))
+            return false;
+       
         region.ensureRegionRootExists();
 
         // We ignore minimalPutOverride. JBossCache putForExternalRead is
         // already about as minimal as we can get; it will promptly return
         // if it discovers that the node we want to write to already exists
         return CacheHelper.putForExternalRead(cache, regionFqn, key, value);
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
        
+        pendingPuts.remove(key);
+        
+        if (!region.checkValid())
+            return false;
+       
         region.ensureRegionRootExists();
 
         CacheHelper.put(cache, regionFqn, key, value);
         return true;
     }
 
     public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
         return false;
     }
 
     public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
             throws CacheException {
        
+        pendingPuts.remove(key);
+       
+        // We update whether or not the region is valid. Other nodes
+        // may have already restored the region so they need to
+        // be informed of the change.
+       
         region.ensureRegionRootExists();
 
         CacheHelper.put(cache, regionFqn, key, value);
         return true;
     }
 
     public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
             throws CacheException {
         return false;
     }
 
     public void remove(Object key) throws CacheException {
        
+        pendingPuts.remove(key);
+       
+        // We remove whether or not the region is valid. Other nodes
+        // may have already restored the region so they need to
+        // be informed of the change.
+       
         region.ensureRegionRootExists();
 
         CacheHelper.remove(cache, regionFqn, key);
     }
 
     public void removeAll() throws CacheException {
-        evictOrRemoveAll();
+       pendingPuts.clear();
+       CacheHelper.removeAll(cache, regionFqn); 
     }
 
     public void evict(Object key) throws CacheException {
        
+        pendingPuts.remove(key);
+       
         region.ensureRegionRootExists();
         
         CacheHelper.remove(cache, regionFqn, key);
     }
 
     public void evictAll() throws CacheException {
-        evictOrRemoveAll();
-    }
-    
-    private void evictOrRemoveAll() throws CacheException {
-        CacheHelper.removeAll(cache, regionFqn);        
+       pendingPuts.clear();
+       Transaction tx = region.suspend();
+       try {        
+          region.ensureRegionRootExists();
+          
+          CacheHelper.sendEvictAllNotification(cache, regionFqn, region.getMemberId(), null);
+       }
+       finally {
+          region.resume(tx);
+       }        
+    }
+
+    protected void registerPendingPut(Object key)
+    {
+      Set<Object> pending = pendingPuts.get(key);
+      if (pending == null) {
+         pending = new HashSet<Object>();
+      }
+      
+      synchronized (pending) {
+         Object owner = region.getOwnerForPut();
+         pending.add(owner);
+         Set<Object> existing = pendingPuts.putIfAbsent(key, pending);
+         if (existing != pending) {
+            // try again
+            registerPendingPut(key);
+         }
+      }
+    }
+
+    protected boolean isPutValid(Object key)
+    {
+       boolean valid = false;
+       Set<Object> pending = pendingPuts.get(key);
+       if (pending != null) {
+          synchronized (pending) {
+             valid = pending.remove(region.getOwnerForPut());
+             if (valid && pending.size() == 0) {
+                pendingPuts.remove(key);
+             }
+          }
+       }
+      return valid;
     }
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java
index b5377ea12e..1278aa318b 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java
@@ -1,69 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2.collection;
 
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 import org.jboss.cache.config.Configuration.NodeLockingScheme;
+import org.jboss.cache.notifications.annotation.CacheListener;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CollectionRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.jbc2.TransactionalDataRegionAdapter;
 
 /**
  * Defines the behavior of the collection cache regions for JBossCache 2.x.
  * 
  * @author Steve Ebersole
  */
+@CacheListener
 public class CollectionRegionImpl extends TransactionalDataRegionAdapter implements CollectionRegion {
 
     public static final String TYPE = "COLL";
     private boolean optimistic;
 
     public CollectionRegionImpl(Cache jbcCache, String regionName, String regionPrefix, CacheDataDescription metadata) {
         super(jbcCache, regionName, regionPrefix, metadata);
         optimistic = (jbcCache.getConfiguration().getNodeLockingScheme() == NodeLockingScheme.OPTIMISTIC);
     }
 
     public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
         if (AccessType.READ_ONLY.equals(accessType)) {
             return optimistic ? new OptimisticReadOnlyAccess(this) : new ReadOnlyAccess(this);
         }
         if (AccessType.TRANSACTIONAL.equals(accessType)) {
             return optimistic ? new OptimisticTransactionalAccess(this) : new TransactionalAccess(this);
         }
 
         // todo : add support for READ_WRITE ( + NONSTRICT_READ_WRITE ??? )
 
         throw new CacheException("unsupported access type [" + accessType.getName() + "]");
     }
 
     @Override
     protected Fqn<String> createRegionFqn(String regionName, String regionPrefix) {
         return getTypeLastRegionFqn(regionName, regionPrefix, TYPE);
     }
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java
index f9a603e772..89ba67ee14 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java
@@ -1,74 +1,76 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2.entity;
 
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 import org.jboss.cache.config.Configuration.NodeLockingScheme;
+import org.jboss.cache.notifications.annotation.CacheListener;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.EntityRegion;
 import org.hibernate.cache.access.AccessType;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.jbc2.TransactionalDataRegionAdapter;
 
 /**
  * Defines the behavior of the entity cache regions for JBossCache.
  * 
  * @author Steve Ebersole
  */
+@CacheListener
 public class EntityRegionImpl extends TransactionalDataRegionAdapter implements EntityRegion {
 
     public static final String TYPE = "ENTITY";
     
     private boolean optimistic;
 
     public EntityRegionImpl(Cache jbcCache, String regionName, String regionPrefix, CacheDataDescription metadata) {
         super(jbcCache, regionName, regionPrefix, metadata);
         optimistic = (jbcCache.getConfiguration().getNodeLockingScheme() == NodeLockingScheme.OPTIMISTIC);
     }
 
     /**
      * {@inheritDoc}
      */
     public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
         if (AccessType.READ_ONLY.equals(accessType)) {
             return optimistic ? new OptimisticReadOnlyAccess(this) : new ReadOnlyAccess(this);
         }
         if (AccessType.TRANSACTIONAL.equals(accessType)) {
             return optimistic ? new OptimisticTransactionalAccess(this) : new TransactionalAccess(this);
         }
 
         // todo : add support for READ_WRITE ( + NONSTRICT_READ_WRITE ??? )
 
         throw new CacheException("unsupported access type [" + accessType.getName() + "]");
     }
 
     @Override
     protected Fqn<String> createRegionFqn(String regionName, String regionPrefix) {
         return getTypeLastRegionFqn(regionName, regionPrefix, TYPE);
     }
 
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java
index e69e89f0af..d57709137c 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java
@@ -1,138 +1,152 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2.query;
 
 import java.util.Properties;
 
+import javax.transaction.Transaction;
+
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.jbc2.TransactionalDataRegionAdapter;
 import org.hibernate.cache.jbc2.util.CacheHelper;
 import org.hibernate.util.PropertiesHelper;
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 import org.jboss.cache.config.Option;
+import org.jboss.cache.notifications.annotation.CacheListener;
 
 /**
  * Defines the behavior of the query cache regions for JBossCache 2.x.
  * 
  * @author Brian Stansberry
  * @version $Revision$
  */
+@CacheListener
 public class QueryResultsRegionImpl extends TransactionalDataRegionAdapter implements QueryResultsRegion {
 
     public static final String QUERY_CACHE_LOCAL_ONLY_PROP = "hibernate.cache.region.jbc2.query.localonly";
     public static final String TYPE = "QUERY";
     
     /**
      * Whether we should set an option to disable propagation of changes around
      * cluster.
      */
     private boolean localOnly;
 
     /**
      * Create a new QueryResultsRegionImpl.
      * 
      * @param jbcCache The JBC cache instance to use to store the query results
      * @param regionName The name of the region (within the JBC cache)
      * @param regionPrefix Any region prefix to apply
 	 * @param properties The configuration properties.
      */
     public QueryResultsRegionImpl(Cache jbcCache, String regionName, String regionPrefix, Properties properties) {
         super(jbcCache, regionName, regionPrefix, null);
 
         // If JBC is using INVALIDATION, we don't want to propagate changes.
         // We use the Timestamps cache to manage invalidation
         localOnly = CacheHelper.isClusteredInvalidation(jbcCache);
         if (!localOnly) {
             // We don't want to waste effort setting an option if JBC is
             // already in LOCAL mode. If JBC is REPL_(A)SYNC then check
             // if they passed an config option to disable query replication
             localOnly = CacheHelper.isClusteredReplication(jbcCache)
                     && PropertiesHelper.getBoolean(QUERY_CACHE_LOCAL_ONLY_PROP, properties, false);
         }
     }
 
     public void evict(Object key) throws CacheException {
        
         ensureRegionRootExists();
         
         Option opt = getNonLockingDataVersionOption(false);
         if (localOnly)
             opt.setCacheModeLocal(true);
         CacheHelper.removeNode(getCacheInstance(), getRegionFqn(), key, opt);
     }
 
     public void evictAll() throws CacheException {
-        Option opt = getNonLockingDataVersionOption(false);
-        if (localOnly)
-            opt.setCacheModeLocal(true);
-        CacheHelper.removeAll(getCacheInstance(), getRegionFqn(), opt);
+          Transaction tx = suspend();
+          try {        
+             ensureRegionRootExists();
+             Option opt = getNonLockingDataVersionOption(true);
+             CacheHelper.sendEvictAllNotification(jbcCache, regionFqn, getMemberId(), opt);
+          }
+          finally {
+             resume(tx);
+          }        
     }
 
     public Object get(Object key) throws CacheException {
        
+        if (!checkValid())
+           return null;
+       
         ensureRegionRootExists();
 
         // Don't hold the JBC node lock throughout the tx, as that
         // prevents updates
         // Add a zero (or low) timeout option so we don't block
         // waiting for tx's that did a put to commit
         Option opt = new Option();
         opt.setLockAcquisitionTimeout(0);
         return suspendAndGet(key, opt, true);
     }
 
     public void put(Object key, Object value) throws CacheException {
        
-        ensureRegionRootExists();
-
-        // Here we don't want to suspend the tx. If we do:
-        // 1) We might be caching query results that reflect uncommitted
-        // changes. No tx == no WL on cache node, so other threads
-        // can prematurely see those query results
-        // 2) No tx == immediate replication. More overhead, plus we
-        // spread issue #1 above around the cluster
-
-        // Add a zero (or quite low) timeout option so we don't block.
-        // Ignore any TimeoutException. Basically we forego caching the
-        // query result in order to avoid blocking.
-        // Reads are done with suspended tx, so they should not hold the
-        // lock for long.  Not caching the query result is OK, since
-        // any subsequent read will just see the old result with its
-        // out-of-date timestamp; that result will be discarded and the
-        // db query performed again.
-        Option opt = getNonLockingDataVersionOption(false);
-        opt.setLockAcquisitionTimeout(2);
-        if (localOnly)
-            opt.setCacheModeLocal(true);
-        CacheHelper.putAllowingTimeout(getCacheInstance(), getRegionFqn(), key, value, opt);
+        if (checkValid()) {
+           ensureRegionRootExists();
+   
+           // Here we don't want to suspend the tx. If we do:
+           // 1) We might be caching query results that reflect uncommitted
+           // changes. No tx == no WL on cache node, so other threads
+           // can prematurely see those query results
+           // 2) No tx == immediate replication. More overhead, plus we
+           // spread issue #1 above around the cluster
+   
+           // Add a zero (or quite low) timeout option so we don't block.
+           // Ignore any TimeoutException. Basically we forego caching the
+           // query result in order to avoid blocking.
+           // Reads are done with suspended tx, so they should not hold the
+           // lock for long.  Not caching the query result is OK, since
+           // any subsequent read will just see the old result with its
+           // out-of-date timestamp; that result will be discarded and the
+           // db query performed again.
+           Option opt = getNonLockingDataVersionOption(false);
+           opt.setLockAcquisitionTimeout(2);
+           if (localOnly)
+               opt.setCacheModeLocal(true);
+           CacheHelper.putAllowingTimeout(getCacheInstance(), getRegionFqn(), key, value, opt);
+        }
     }
 
     @Override
     protected Fqn<String> createRegionFqn(String regionName, String regionPrefix) {
         return getTypeLastRegionFqn(regionName, regionPrefix, TYPE);
     }
 
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
index 30aa10638c..de900aae72 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
@@ -1,191 +1,222 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 
 package org.hibernate.cache.jbc2.timestamp;
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.transaction.Transaction;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.TimestampsRegion;
 import org.hibernate.cache.jbc2.TransactionalDataRegionAdapter;
 import org.hibernate.cache.jbc2.util.CacheHelper;
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 import org.jboss.cache.config.Option;
 import org.jboss.cache.notifications.annotation.CacheListener;
 import org.jboss.cache.notifications.annotation.NodeModified;
 import org.jboss.cache.notifications.annotation.NodeRemoved;
+import org.jboss.cache.notifications.event.NodeInvalidatedEvent;
 import org.jboss.cache.notifications.event.NodeModifiedEvent;
 import org.jboss.cache.notifications.event.NodeRemovedEvent;
 
 /**
  * Defines the behavior of the timestamps cache region for JBossCache 2.x.
  * 
  * TODO Need to define a way to ensure asynchronous replication events
  * do not result in timestamps moving backward, while dealing with the fact
  * that the normal sequence of UpdateTimestampsCache.preinvalidate() then
  * UpdateTimestampsCache.invalidate() will result in 2 calls to put() with
  * the latter call having an earlier timestamp.
  * 
  * @author Brian Stansberry
  * @version $Revision$
  */
 @CacheListener
 public class TimestampsRegionImpl extends TransactionalDataRegionAdapter implements TimestampsRegion {
 
     public static final String TYPE = "TS";
 
     private Map localCache = new ConcurrentHashMap();
     
     /**
      * Create a new TimestampsRegionImpl.
 	 *
      * @param jbcCache The JBC cache instance to use to store the timestamps data
      * @param regionName The name of the region (within the JBC cache)
      * @param regionPrefix Any region prefix to apply
 	 * @param properties The configuration properties.
      */
     public TimestampsRegionImpl(Cache jbcCache, String regionName, String regionPrefix, Properties properties) {
         super(jbcCache, regionName, regionPrefix, null);
 
         jbcCache.addCacheListener(this);
 
         populateLocalCache();
     }
 
     @Override
     protected Fqn<String> createRegionFqn(String regionName, String regionPrefix) {
         return getTypeFirstRegionFqn(regionName, regionPrefix, TYPE);
     }
 
     public void evict(Object key) throws CacheException {
        
         ensureRegionRootExists();
         
         // TODO Is this a valid operation on a timestamps cache?
         Option opt = getNonLockingDataVersionOption(true);
         CacheHelper.removeNode(getCacheInstance(), getRegionFqn(), key, opt);
     }
 
     public void evictAll() throws CacheException {
         // TODO Is this a valid operation on a timestamps cache?
-        Option opt = getNonLockingDataVersionOption(true);
-        CacheHelper.removeAll(getCacheInstance(), getRegionFqn(), opt);
+        Transaction tx = suspend();
+        try {        
+           ensureRegionRootExists();
+           Option opt = getNonLockingDataVersionOption(true);
+           CacheHelper.sendEvictAllNotification(jbcCache, regionFqn, getMemberId(), opt);
+        }
+        finally {
+           resume(tx);
+        }        
     }
 
     public Object get(Object key) throws CacheException {
 
         Object value = localCache.get(key);
-        if (value == null) {
+        if (value == null && checkValid()) {
            
             ensureRegionRootExists();
             
             value = suspendAndGet(key, null, false);
             if (value != null)
                 localCache.put(key, value);
         }
         return value;
     }
 
     public void put(Object key, Object value) throws CacheException {
        
         ensureRegionRootExists();
 
         // Don't hold the JBC node lock throughout the tx, as that
         // prevents reads and other updates
         Transaction tx = suspend();
         try {
             // TODO Why not use the timestamp in a DataVersion?
             Option opt = getNonLockingDataVersionOption(false);
             // We ensure ASYNC semantics (JBCACHE-1175)
             opt.setForceAsynchronous(true);
             CacheHelper.put(getCacheInstance(), getRegionFqn(), key, value, opt);
         } catch (Exception e) {
             throw new CacheException(e);
         } finally {
             resume(tx);
         }
     }
 
     @Override
     public void destroy() throws CacheException {
         localCache.clear();
         getCacheInstance().removeCacheListener(this);
         super.destroy();
     }
 
     /**
      * Monitors cache events and updates the local cache
      * 
      * @param event
      */
     @NodeModified
     public void nodeModified(NodeModifiedEvent event) {
-        if (event.isPre())
-            return;
-
-        Fqn fqn = event.getFqn();
-        Fqn regFqn = getRegionFqn();
-        if (fqn.size() == regFqn.size() + 1 && fqn.isChildOf(regFqn)) {
-            Object key = fqn.get(regFqn.size());
-            localCache.put(key, event.getData().get(ITEM));
+       
+        if (!handleEvictAllModification(event) && !event.isPre()) {
+   
+           Fqn fqn = event.getFqn();
+           Fqn regFqn = getRegionFqn();
+           if (fqn.size() == regFqn.size() + 1 && fqn.isChildOf(regFqn)) {
+               Object key = fqn.get(regFqn.size());
+               localCache.put(key, event.getData().get(ITEM));
+           }
         }
     }
 
     /**
      * Monitors cache events and updates the local cache
      * 
      * @param event
      */
     @NodeRemoved
     public void nodeRemoved(NodeRemovedEvent event) {
         if (event.isPre())
             return;
 
         Fqn fqn = event.getFqn();
         Fqn regFqn = getRegionFqn();
         if (fqn.size() == regFqn.size() + 1 && fqn.isChildOf(regFqn)) {
             Object key = fqn.get(regFqn.size());
             localCache.remove(key);
         }
         else if (fqn.equals(regFqn)) {
             localCache.clear();
         }
     }
+    
+    
 
-    /**
+    @Override
+   protected boolean handleEvictAllInvalidation(NodeInvalidatedEvent event)
+   {
+      boolean result = super.handleEvictAllInvalidation(event);
+      if (result) {
+         localCache.clear();
+      }
+      return result;
+   }
+
+   @Override
+   protected boolean handleEvictAllModification(NodeModifiedEvent event)
+   {
+      boolean result = super.handleEvictAllModification(event);
+      if (result) {
+         localCache.clear();
+      }
+      return result;
+   }
+
+   /**
      * Brings all data from the distributed cache into our local cache.
      */
     private void populateLocalCache() {
         Set children = CacheHelper.getChildrenNames(getCacheInstance(), getRegionFqn());
         for (Object key : children) {
             get(key);
         }
     }
 }
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
index fa8d79df52..b091eb0626 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
@@ -1,470 +1,491 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
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
 package org.hibernate.cache.jbc2.util;
 
 import java.util.Collections;
 import java.util.Set;
 
 import org.hibernate.cache.CacheException;
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
 import org.jboss.cache.InvocationContext;
 import org.jboss.cache.Node;
 import org.jboss.cache.config.Configuration;
 import org.jboss.cache.config.Option;
 import org.jboss.cache.lock.TimeoutException;
 import org.jboss.cache.optimistic.DataVersion;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 /**
  * Helper for dealing with JBossCache {@link Configuration.CacheMode}.
  * 
  * @author Steve Ebersole
  * @author Brian Stansberry
  */
 public class CacheHelper {
 
+    public static enum Internal { NODE, LOCAL };
+    
     /** Key under which items are cached */
     public static final String ITEM = "item";
     /** Key and value used in a hack to create region root nodes */
     public static final String DUMMY = "dummy";
     
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
      *            The cache to check.
      * @return True if the cache is configured for synchronous/asynchronous
      *         invalidation; false otherwise.
      */
     public static boolean isClusteredInvalidation(Cache cache) {
         return isClusteredInvalidation(cache.getConfiguration().getCacheMode());
     }
 
     /**
      * Does this cache mode indicate clustered invalidation?
      * 
      * @param cacheMode
      *            The cache to check
      * @return True if the cache mode is confiogured for
      *         synchronous/asynchronous invalidation; false otherwise.
      */
     public static boolean isClusteredInvalidation(Configuration.CacheMode cacheMode) {
         return cacheMode == Configuration.CacheMode.INVALIDATION_ASYNC
                 || cacheMode == Configuration.CacheMode.INVALIDATION_SYNC;
     }
 
     /**
      * Is this cache participating in a cluster with replication?
      * 
      * @param cache
      *            The cache to check.
      * @return True if the cache is configured for synchronous/asynchronous
      *         invalidation; false otherwise.
      */
     public static boolean isClusteredReplication(Cache cache) {
         return isClusteredReplication(cache.getConfiguration().getCacheMode());
     }
 
     /**
      * Does this cache mode indicate clustered replication?
      * 
      * @param cacheMode
      *            The cache to check
      * @return True if the cache mode is confiogured for
      *         synchronous/asynchronous invalidation; false otherwise.
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
     
     public static Set getChildrenNames(Cache cache, Fqn fqn) {
         Node node = cache.getRoot().getChild(fqn);
         return (node != null) ? node.getChildrenNames() : Collections.emptySet();
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>get(Fqn, Object)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      */
     public static Object get(Cache cache, Fqn region, Object key) throws CacheException {
         try {
             return cache.get(new Fqn(region, key), ITEM);
         } catch (Exception e) {
             throw new CacheException(e);
         }
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>get(Fqn, Object)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      */
     public static Object getAllowingTimeout(Cache cache, Fqn region, Object key) throws CacheException {
         try {
             return cache.get(new Fqn(region, key), ITEM);        
         } 
         catch (TimeoutException ignored) {
             // ignore it
             return null;
         }
         catch (Exception e) {
             throw new CacheException(e);
         }
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>put(Object, Object)</code>, wrapping
      * any exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      * @param value
      *            data to store in the cache node
      */
     public static void put(Cache cache, Fqn region, Object key, Object value) throws CacheException {
 
         put(cache, region, key, value, null);
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>put(Object, Object)</code>, wrapping
      * any exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      * @param value
      *            data to store in the cache node
      * @param option
      *            invocation Option to set for this invocation. May be
      *            <code>null</code>.
      */
     public static void put(Cache cache, Fqn region, Object key, Object value, Option option) throws CacheException {
         try {
             setInvocationOption(cache, option);
             cache.put(new Fqn(region, key), ITEM, value);
         } catch (Exception e) {
             throw new CacheException(e);
         }
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>put(Object, Object)</code>, ignoring any
      * {@link TimeoutException} and wrapping any other exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      * @param value
      *            data to store in the cache node
      * @param option
      *            invocation Option to set for this invocation. May be
      *            <code>null</code>.
      */
     public static void putAllowingTimeout(Cache cache, Fqn region, Object key, Object value, Option option) throws CacheException {
         try {
             setInvocationOption(cache, option);
             cache.put(new Fqn(region, key), ITEM, value);
         }
         catch (TimeoutException allowed) {
             // ignore it
         }
         catch (Exception e) {
             throw new CacheException(e);
         }
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache
      * <code>putForExternalRead(Object, Object)</code>, wrapping any
      * exception in a {@link CacheException}. Ignores any JBoss Cache
      * {@link TimeoutException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      * @param value
      *            data to store in the cache node
      */
     public static boolean putForExternalRead(Cache cache, Fqn region, Object key, Object value) throws CacheException {
 
         return putForExternalRead(cache, region, key, value, null);
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache
      * <code>putForExternalRead(Object, Object)</code>, wrapping any
      * exception in a {@link CacheException}. Ignores any JBoss Cache
      * {@link TimeoutException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      * @param value
      *            data to store in the cache node
      * @param option
      *            invocation Option to set for this invocation. May be
      *            <code>null</code>.
      */
     public static boolean putForExternalRead(Cache cache, Fqn region, Object key, Object value, Option option)
             throws CacheException {
         try {
             setInvocationOption(cache, option);
             cache.putForExternalRead(new Fqn(region, key), ITEM, value);
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
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      */
     public static void remove(Cache cache, Fqn region, Object key) throws CacheException {
 
         remove(cache, region, key, null);
     }
 
     /**
      * Builds an {@link Fqn} from <code>region</code> and <code>key</code>
      * and performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param key
      *            specific key to append to the <code>region</code> to form
      *            the full Fqn
      * @param option
      *            invocation Option to set for this invocation. May be
      *            <code>null</code>.
      */
     public static void remove(Cache cache, Fqn region, Object key, Option option) throws CacheException {
         try {
             setInvocationOption(cache, option);
             cache.removeNode(new Fqn(region, key));
         } catch (Exception e) {
             throw new CacheException(e);
         }
     }
 
     /**
      * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      */
     public static void removeAll(Cache cache, Fqn region) throws CacheException {
 
         removeAll(cache, region, null);
     }
 
     /**
      * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param option
      *            invocation Option to set for this invocation. May be
      *            <code>null</code>.
      */
     public static void removeAll(Cache cache, Fqn region, Option option) throws CacheException {
         try {
             setInvocationOption(cache, option);
             cache.removeNode(region);
         } catch (Exception e) {
             throw new CacheException(e);
         }
     }
 
     /**
      * Performs a JBoss Cache <code>removeNode(Fqn)</code>, wrapping any
      * exception in a {@link CacheException}.
      * 
      * @param cache
      *            the cache to invoke on
      * @param region
      *            base Fqn for the cache region
      * @param option
      *            invocation Option to set for this invocation. May be
      *            <code>null</code>.
      */
     public static void removeNode(Cache cache, Fqn region, Object key, Option option) throws CacheException {
         try {
             setInvocationOption(cache, option);
             cache.removeNode(new Fqn(region, key));
         } catch (Exception e) {
             throw new CacheException(e);
         }
     }
     
     public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident, DataVersion version)
             throws CacheException {
         try {
             Option option = null;
             if (localOnly || version != null) {
                 option = new Option();
                 option.setCacheModeLocal(localOnly);
                 option.setDataVersion(version);
             }
             
             Node root = cache.getRoot();
             setInvocationOption(cache, option);
             // FIXME hack to work around fact that calling
             // Node added = root.addChild( fqn ); doesn't 
             // properly set the version on the node
             Node added = null;
             if (version == null) {
                 added = root.addChild( fqn );
             }
             else {
                 cache.put(fqn, DUMMY, DUMMY);
                 added = root.getChild(fqn);
             }
             if (resident)
                 added.setResident(true);
             return added;
         }
         catch (Exception e) {
             throw new CacheException(e);
         }
     }
     
 
     /**
      * Assigns the given Option to the cache's {@link InvocationContext}. Does
      * nothing if <code>option</code> is <code>null</code>.
      * 
      * @param cache
      *            the cache. Cannot be <code>null</code>.
      * @param option
      *            the option. May be <code>null</code>.
      * 
      * @see {@link Cache#getInvocationContext()}
      * @see {@link InvocationContext#setOptionOverrides(Option)}
      */
     public static void setInvocationOption(Cache cache, Option option) {
         if (option != null) {
             cache.getInvocationContext().setOptionOverrides(option);
         }
     }
 
     /**
      * Creates an {@link Option} using the given {@link DataVersion} and passes
      * it to {@link #setInvocationOption(Cache, Option)}.
      * 
      * @param cache
      *            the cache to set the Option on. Cannot be <code>null</code>.
      * @param version
      *            the DataVersion to set. Cannot be <code>null</code>.
      */
     public static void setDataVersionOption(Cache cache, DataVersion version) {
         Option option = new Option();
         option.setDataVersion(version);
         setInvocationOption(cache, option);
     }
+    
+    public static Fqn getInternalFqn(Fqn region)
+    {
+       return Fqn.fromRelativeElements(region, Internal.NODE);
+    }
+    
+    public static void sendEvictNotification(Cache cache, Fqn region, Object member, Object key, Option option)
+    {
+       setInvocationOption(cache, option);
+       Fqn f = Fqn.fromRelativeElements(region, Internal.NODE, member  == null ? Internal.LOCAL : member, key);
+       cache.put(f, ITEM, DUMMY);
+    }
+    
+    public static void sendEvictAllNotification(Cache cache, Fqn region, Object member, Option option)
+    {
+       setInvocationOption(cache, option);
+       Fqn f = Fqn.fromRelativeElements(region, Internal.NODE, member  == null ? Internal.LOCAL : member);
+       cache.put(f, ITEM, DUMMY);
+    }
 }
