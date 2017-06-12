diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
index 6ecee007eb..f38b0612eb 100644
--- a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
+++ b/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
@@ -1,146 +1,325 @@
 /*
  * Copyright (c) 2007, Red Hat Middleware, LLC. All rights reserved.
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
  * Red Hat Author(s): Steve Ebersole
  */
 package org.hibernate.cache.jbc2;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 
+import javax.transaction.SystemException;
+import javax.transaction.Transaction;
+import javax.transaction.TransactionManager;
+
 import org.jboss.cache.Cache;
 import org.jboss.cache.Fqn;
+import org.jboss.cache.Node;
+import org.jboss.cache.NodeSPI;
+import org.jboss.cache.config.Configuration;
 import org.jboss.cache.config.Option;
+import org.jboss.cache.config.Configuration.NodeLockingScheme;
+import org.jboss.cache.notifications.annotation.CacheListener;
+import org.jboss.cache.notifications.annotation.NodeCreated;
+import org.jboss.cache.notifications.event.NodeCreatedEvent;
+import org.jboss.cache.optimistic.DataVersion;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.Region;
+import org.hibernate.cache.jbc2.util.CacheHelper;
+import org.hibernate.cache.jbc2.util.NonLockingDataVersion;
 
 /**
- * General support for writing {@link Region} implementations for
- *
- *
+ * General support for writing {@link Region} implementations for JBoss Cache
+ * 2.x.
+ * 
+ * 
  * @author Steve Ebersole
  */
+@CacheListener
 public abstract class BasicRegionAdapter implements Region {
-	public static final String ITEM = "item";
-
-	protected final Cache jbcCache;
-	protected final String regionName;
-	protected final Fqn regionFqn;
-
-	public BasicRegionAdapter(Cache jbcCache, String regionName) {
-		this.jbcCache = jbcCache;
-		this.regionName = regionName;
-		this.regionFqn = Fqn.fromString( regionName.replace( '.', '/' ) );
-		activateLocalClusterNode();
-	}
-
-	private void activateLocalClusterNode() {
-		org.jboss.cache.Region jbcRegion = jbcCache.getRegion( regionFqn, true );
-		if ( jbcRegion.isActive() ) {
-			return;
-		}
-		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
-		if ( classLoader == null ) {
-			classLoader = getClass().getClassLoader();
-		}
-		jbcRegion.registerContextClassLoader( classLoader );
-		jbcRegion.activate();
-	}
-
-	public String getName() {
-		return regionName;
-	}
-
-	public Cache getCacheInstance() {
-		return jbcCache;
-	}
-
-	public Fqn getRegionFqn() {
-		return regionFqn;
-	}
-
-	public void destroy() throws CacheException {
-		try {
-			// NOTE : this is being used from the process of shutting down a
-			// SessionFactory.  Specific things to consider:
-			// 		(1) this clearing of the region should not propogate to
-			// 			other nodes on the cluster (if any); this is the
-			//			cache-mode-local option bit...
-			//		(2) really just trying a best effort to cleanup after
-			// 			ourselves; lock failures, etc are not critical here;
-			//			this is the fail-silently option bit...
-			Option option = new Option();
-			option.setCacheModeLocal( true );
-			option.setFailSilently( true );
-			jbcCache.getInvocationContext().setOptionOverrides( option );
-			jbcCache.removeNode( regionFqn );
-			deactivateLocalNode();
-		}
-		catch( Exception e ) {
-			throw new CacheException( e );
-		}
-	}
-
-	private void deactivateLocalNode() {
-		org.jboss.cache.Region jbcRegion = jbcCache.getRegion( regionFqn, false );
-		if ( jbcRegion != null && jbcRegion.isActive() ) {
-			jbcRegion.deactivate();
-			jbcRegion.unregisterContextClassLoader();
-		}
-	}
-
-	public long getSizeInMemory() {
-		// not supported
-		return -1;
-	}
-
-	public long getElementCountInMemory() {
-		try {
-			Set children = jbcCache.getRoot().getChild( regionFqn ).getChildrenNames();
-			return children == null ? 0 : children.size();
-		}
-		catch ( Exception e ) {
-			throw new CacheException( e );
-		}
-	}
-
-	public long getElementCountOnDisk() {
-		return -1;
-	}
-
-	public Map toMap() {
-		try {
-			Map result = new HashMap();
-			Set childrenNames = jbcCache.getRoot().getChild( regionFqn ).getChildrenNames();
-			if (childrenNames != null) {
-				for ( Object childName : childrenNames ) {
-					result.put( childName, jbcCache.get( new Fqn( regionFqn, childName ), ITEM ) );
-				}
-			}
-			return result;
-		}
-		catch (Exception e) {
-			throw new CacheException(e);
-		}
-	}
-
-	public long nextTimestamp() {
-		return System.currentTimeMillis() / 100;
-	}
-
-	public int getTimeout() {
-		return 600; //60 seconds
-	}
+    public static final String ITEM = CacheHelper.ITEM;
+
+    protected final Cache jbcCache;
+    protected final String regionName;
+    protected final Fqn regionFqn;
+    protected final boolean optimistic;
+    
+    protected final TransactionManager transactionManager;
+
+    protected SetResidentListener listener;
+    
+    public BasicRegionAdapter(Cache jbcCache, String regionName, String regionPrefix) {
+        this.jbcCache = jbcCache;
+        this.transactionManager = jbcCache.getConfiguration().getRuntimeConfig().getTransactionManager();
+        this.regionName = regionName;
+        this.regionFqn = createRegionFqn(regionName, regionPrefix);
+        optimistic = jbcCache.getConfiguration().getNodeLockingScheme() == NodeLockingScheme.OPTIMISTIC;
+        activateLocalClusterNode();
+    }
+
+    protected abstract Fqn<String> createRegionFqn(String regionName, String regionPrefix);
+
+    protected void activateLocalClusterNode() {
+        try {
+            Configuration cfg = jbcCache.getConfiguration();
+            if (cfg.isUseRegionBasedMarshalling()) {
+                org.jboss.cache.Region jbcRegion = jbcCache.getRegion(regionFqn, true);
+                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
+                if (classLoader == null) {
+                    classLoader = getClass().getClassLoader();
+                }
+                jbcRegion.registerContextClassLoader(classLoader);
+                if (jbcRegion.isActive() == false) {
+                    jbcRegion.activate();
+                }
+            }
+            
+            // If we are using replication, we may remove the root node
+            // and then need to re-add it. In that case, the fact
+            // that it is resident will not replicate, so use a listener
+            // to set it as resident
+            if (CacheHelper.isClusteredReplication(cfg.getCacheMode())) {
+                listener = new SetResidentListener();
+                jbcCache.addCacheListener(listener);
+            }
+            
+            // Make sure the root node for the region exists and 
+            // has a DataVersion that never complains
+            Node regionRoot = jbcCache.getRoot().getChild( regionFqn );
+            if (regionRoot == null) {                
+                // Establish the region root node with a non-locking data version
+                DataVersion version = optimistic ? NonLockingDataVersion.INSTANCE : null;
+                regionRoot = CacheHelper.addNode(jbcCache, regionFqn, true, true, version);    
+            }
+            else if (optimistic && regionRoot instanceof NodeSPI) {
+                // FIXME Hacky workaround to JBCACHE-1202
+                if ((((NodeSPI) regionRoot).getVersion() instanceof NonLockingDataVersion) == false) {
+                    ((NodeSPI) regionRoot).setVersion(NonLockingDataVersion.INSTANCE);
+                }
+            }
+            // Never evict this node
+            regionRoot.setResident(true);
+        }
+        catch (Exception e) {
+            throw new CacheException(e.getMessage(), e);
+        }
+    }
+
+    public String getName() {
+        return regionName;
+    }
+
+    public Cache getCacheInstance() {
+        return jbcCache;
+    }
+
+    public Fqn getRegionFqn() {
+        return regionFqn;
+    }
+
+    public void destroy() throws CacheException {
+        try {
+            // NOTE : this is being used from the process of shutting down a
+            // SessionFactory. Specific things to consider:
+            // (1) this clearing of the region should not propogate to
+            // other nodes on the cluster (if any); this is the
+            // cache-mode-local option bit...
+            // (2) really just trying a best effort to cleanup after
+            // ourselves; lock failures, etc are not critical here;
+            // this is the fail-silently option bit...
+            Option option = new Option();
+            option.setCacheModeLocal(true);
+            option.setFailSilently(true);
+            if (optimistic) {
+                option.setDataVersion(NonLockingDataVersion.INSTANCE);
+            }
+            jbcCache.getInvocationContext().setOptionOverrides(option);
+            jbcCache.removeNode(regionFqn);
+            deactivateLocalNode();            
+        } catch (Exception e) {
+            throw new CacheException(e);
+        }
+        finally {
+            if (listener != null)
+                jbcCache.removeCacheListener(listener);
+        }
+    }
+
+    protected void deactivateLocalNode() {
+        org.jboss.cache.Region jbcRegion = jbcCache.getRegion(regionFqn, false);
+        if (jbcRegion != null && jbcRegion.isActive()) {
+            jbcRegion.deactivate();
+            jbcRegion.unregisterContextClassLoader();
+        }
+    }
+
+    public long getSizeInMemory() {
+        // not supported
+        return -1;
+    }
+
+    public long getElementCountInMemory() {
+        try {
+            Set childrenNames = CacheHelper.getChildrenNames(jbcCache, regionFqn);
+            return childrenNames.size();
+        } catch (Exception e) {
+            throw new CacheException(e);
+        }
+    }
+
+    public long getElementCountOnDisk() {
+        return -1;
+    }
+
+    public Map toMap() {
+        try {
+            Map result = new HashMap();
+            Set childrenNames = CacheHelper.getChildrenNames(jbcCache, regionFqn);
+            for (Object childName : childrenNames) {
+                result.put(childName, jbcCache.get(new Fqn(regionFqn, childName), ITEM));
+            }
+            return result;
+        } catch (Exception e) {
+            throw new CacheException(e);
+        }
+    }
+
+    public long nextTimestamp() {
+        return System.currentTimeMillis() / 100;
+    }
+
+    public int getTimeout() {
+        return 600; // 60 seconds
+    }
+
+    /**
+     * Performs a JBoss Cache <code>get(Fqn, Object)</code> after first
+     * {@link #suspend suspending any ongoing transaction}. Wraps any exception
+     * in a {@link CacheException}. Ensures any ongoing transaction is resumed.
+     * 
+     * @param key
+     * @param opt any option to add to the get invocation. May be <code>null</code>
+     * @param suppressTimeout should any TimeoutException be suppressed?
+     * @return
+     */
+    protected Object suspendAndGet(Object key, Option opt, boolean suppressTimeout) throws CacheException {
+        Transaction tx = suspend();
+        try {
+            CacheHelper.setInvocationOption(getCacheInstance(), opt);
+            if (suppressTimeout)
+                return CacheHelper.getAllowingTimeout(getCacheInstance(), getRegionFqn(), key);
+            else
+                return CacheHelper.get(getCacheInstance(), getRegionFqn(), key);
+        } finally {
+            resume(tx);
+        }
+    }
+
+    /**
+     * Tell the TransactionManager to suspend any ongoing transaction.
+     * 
+     * @return the transaction that was suspended, or <code>null</code> if
+     *         there wasn't one
+     */
+    protected Transaction suspend() {
+        Transaction tx = null;
+        try {
+            if (transactionManager != null) {
+                tx = transactionManager.suspend();
+            }
+        } catch (SystemException se) {
+            throw new CacheException("Could not suspend transaction", se);
+        }
+        return tx;
+    }
+
+    /**
+     * Tell the TransactionManager to resume the given transaction
+     * 
+     * @param tx
+     *            the transaction to suspend. May be <code>null</code>.
+     */
+    protected void resume(Transaction tx) {
+        try {
+            if (tx != null)
+                transactionManager.resume(tx);
+        } catch (Exception e) {
+            throw new CacheException("Could not resume transaction", e);
+        }
+    }
+
+    /**
+     * Get an Option with a {@link Option#getDataVersion() data version}
+     * of {@link NonLockingDataVersion}.  The data version will not be 
+     * set if the cache is not configured for optimistic locking.
+     * 
+     * @param allowNullReturn If <code>true</code>, return <code>null</code>
+     *                        if the cache is not using optimistic locking.
+     *                        If <code>false</code>, return a default
+     *                        {@link Option}.
+     *                        
+     * @return the Option, or <code>null</code>.
+     */
+    protected Option getNonLockingDataVersionOption(boolean allowNullReturn) {
+        return optimistic ? NonLockingDataVersion.getInvocationOption() 
+                          : (allowNullReturn) ? null : new Option();
+    }
+
+    public static Fqn<String> getTypeFirstRegionFqn(String regionName, String regionPrefix, String regionType) {
+        Fqn<String> base = Fqn.fromString(regionType);
+        Fqn<String> added = Fqn.fromString(escapeRegionName(regionName, regionPrefix));
+        return new Fqn<String>(base, added);
+    }
+
+    public static Fqn<String> getTypeLastRegionFqn(String regionName, String regionPrefix, String regionType) {
+        Fqn<String> base = Fqn.fromString(escapeRegionName(regionName, regionPrefix));
+        return new Fqn<String>(base, regionType);
+    }
+
+    public static String escapeRegionName(String regionName, String regionPrefix) {
+        String escaped = null;
+        int idx = -1;
+        if (regionPrefix != null) {
+            idx = regionName.indexOf(regionPrefix);
+        }
+
+        if (idx > -1) {
+            int regionEnd = idx + regionPrefix.length();
+            String prefix = regionName.substring(0, regionEnd);
+            String suffix = regionName.substring(regionEnd);
+            suffix = suffix.replace('.', '/');
+            escaped = prefix + suffix;
+        } else {
+            escaped = regionName.replace('.', '/');
+            if (regionPrefix != null && regionPrefix.length() > 0) {
+                escaped = regionPrefix + "/" + escaped;
+            }
+        }
+        return escaped;
+    }
+    
+    @CacheListener
+    public class SetResidentListener {
+        
+        @NodeCreated
+        public void nodeCreated(NodeCreatedEvent event) {
+            if (!event.isPre() && event.getFqn().equals(getRegionFqn())) {
+                Node regionRoot = jbcCache.getRoot().getChild(getRegionFqn());
+                regionRoot.setResident(true);
+            }
+        }
+        
+    }
 }
