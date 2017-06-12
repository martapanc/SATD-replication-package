diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
index 54f29470d1..4e2bdd9f6a 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CacheDataDescriptionImpl.java
@@ -1,95 +1,107 @@
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
 package org.hibernate.cache.internal;
 
 import java.util.Comparator;
 
 import org.hibernate.cache.spi.CacheDataDescription;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.type.VersionType;
 
 /**
  * {@inheritDoc}
  *
  * @author Steve Ebersole
  */
 public class CacheDataDescriptionImpl implements CacheDataDescription {
 	private final boolean mutable;
 	private final boolean versioned;
 	private final Comparator versionComparator;
 
 	public CacheDataDescriptionImpl(boolean mutable, boolean versioned, Comparator versionComparator) {
 		this.mutable = mutable;
 		this.versioned = versioned;
 		this.versionComparator = versionComparator;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public Comparator getVersionComparator() {
 		return versionComparator;
 	}
 
 	public static CacheDataDescriptionImpl decode(PersistentClass model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.isVersioned(),
 				model.isVersioned() ? ( ( VersionType ) model.getVersion().getType() ).getComparator() : null
 		);
 	}
 
 	public static CacheDataDescriptionImpl decode(EntityBinding model) {
-		Comparator versionComparator = null;
-		if ( model.isVersioned() ) {
-			versionComparator = (
-					( VersionType ) model
-								.getVersioningValueBinding()
-								.getHibernateTypeDescriptor()
-								.getExplicitType()
-			).getComparator();
-		}
-
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.isVersioned(),
-				versionComparator
+				getVersionComparator( model )
 		);
 	}
 
 	public static CacheDataDescriptionImpl decode(Collection model) {
 		return new CacheDataDescriptionImpl(
 				model.isMutable(),
 				model.getOwner().isVersioned(),
 				model.getOwner().isVersioned() ? ( ( VersionType ) model.getOwner().getVersion().getType() ).getComparator() : null
 		);
 	}
+
+	public static CacheDataDescriptionImpl decode(PluralAttributeBinding model) {
+		return new CacheDataDescriptionImpl(
+				model.isMutable(),
+				model.getEntityBinding().isVersioned(),
+				getVersionComparator( model.getEntityBinding() )
+		);
+	}
+
+	private static Comparator getVersionComparator(EntityBinding model ) {
+		Comparator versionComparator = null;
+		if ( model.isVersioned() ) {
+			versionComparator = (
+					( VersionType ) model
+								.getVersioningValueBinding()
+								.getHibernateTypeDescriptor()
+								.getExplicitType()
+			).getComparator();
+		}
+		return versionComparator;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java
index 6f88fd1c6a..7187fec426 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java
@@ -1,172 +1,50 @@
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
 package org.hibernate.cache.spi.access;
 
-import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.CollectionRegion;
 
 /**
  * Contract for managing transactional and concurrent access to cached collection
  * data.  For cached collection data, all modification actions actually just
  * invalidate the entry(s).  The call sequence here is:
  * {@link #lockItem} -> {@link #remove} -> {@link #unlockItem}
  * <p/>
  * There is another usage pattern that is used to invalidate entries
  * after performing "bulk" HQL/SQL operations:
  * {@link #lockRegion} -> {@link #removeAll} -> {@link #unlockRegion}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
-public interface CollectionRegionAccessStrategy {
+public interface CollectionRegionAccessStrategy extends RegionAccessStrategy {
 
 	/**
 	 * Get the wrapped collection cache region
 	 *
 	 * @return The underlying region
 	 */
 	public CollectionRegion getRegion();
 
-	/**
-	 * Attempt to retrieve an object from the cache. Mainly used in attempting
-	 * to resolve entities/collections from the second level cache.
-	 *
-	 * @param key The key of the item to be retrieved.
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @return the cached object or <tt>null</tt>
-	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public Object get(Object key, long txTimestamp) throws CacheException;
-
-	/**
-	 * Attempt to cache an object, after loading from the database.
-	 *
-	 * @param key The item key
-	 * @param value The item
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @param version the item version number
-	 * @return <tt>true</tt> if the object was successfully cached
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public boolean putFromLoad(
-			Object key,
-			Object value,
-			long txTimestamp,
-			Object version) throws CacheException;
-
-	/**
-	 * Attempt to cache an object, after loading from the database, explicitly
-	 * specifying the minimalPut behavior.
-	 *
-	 * @param key The item key
-	 * @param value The item
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @param version the item version number
-	 * @param minimalPutOverride Explicit minimalPut flag
-	 * @return <tt>true</tt> if the object was successfully cached
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public boolean putFromLoad(
-			Object key,
-			Object value,
-			long txTimestamp,
-			Object version,
-			boolean minimalPutOverride) throws CacheException;
-
-	/**
-	 * We are going to attempt to update/delete the keyed object. This
-	 * method is used by "asynchronous" concurrency strategies.
-	 * <p/>
-	 * The returned object must be passed back to release(), to release the
-	 * lock. Concurrency strategies which do not support client-visible
-	 * locks may silently return null.
-	 *
-	 * @param key The key of the item to lock
-	 * @param version The item's current version value
-	 * @return A representation of our lock on the item; or null.
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public SoftLock lockItem(Object key, Object version) throws CacheException;
-
-	/**
-	 * Lock the entire region
-	 *
-	 * @return A representation of our lock on the item; or null.
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public SoftLock lockRegion() throws CacheException;
-
-	/**
-	 * Called when we have finished the attempted update/delete (which may or
-	 * may not have been successful), after transaction completion.  This method
-	 * is used by "asynchronous" concurrency strategies.
-	 *
-	 * @param key The item key
-	 * @param lock The lock previously obtained from {@link #lockItem}
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void unlockItem(Object key, SoftLock lock) throws CacheException;
-
-	/**
-	 * Called after we have finished the attempted invalidation of the entire
-	 * region
-	 *
-	 * @param lock The lock previously obtained from {@link #lockRegion}
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void unlockRegion(SoftLock lock) throws CacheException;
-
-	/**
-	 * Called after an item has become stale (before the transaction completes).
-	 * This method is used by "synchronous" concurrency strategies.
-	 *
-	 * @param key The key of the item to remove
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void remove(Object key) throws CacheException;
-
-	/**
-	 * Called to evict data from the entire region
-	 *
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void removeAll() throws CacheException;
-
-	/**
-	 * Forcibly evict an item from the cache immediately without regard for transaction
-	 * isolation.
-	 *
-	 * @param key The key of the item to remove
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void evict(Object key) throws CacheException;
-
-	/**
-	 * Forcibly evict all items from the cache immediately without regard for transaction
-	 * isolation.
-	 *
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void evictAll() throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
index 7b110fe46c..faf27cc846 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
@@ -1,229 +1,140 @@
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
 package org.hibernate.cache.spi.access;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.EntityRegion;
 
 /**
  * Contract for managing transactional and concurrent access to cached entity
  * data.  The expected call sequences related to various operations are:<ul>
  * <li><b>INSERTS</b> : {@link #insert} -> {@link #afterInsert}</li>
  * <li><b>UPDATES</b> : {@link #lockItem} -> {@link #update} -> {@link #afterUpdate}</li>
  * <li><b>DELETES</b> : {@link #lockItem} -> {@link #remove} -> {@link #unlockItem}</li>
  * </ul>
  * <p/>
  * There is another usage pattern that is used to invalidate entries
  * after performing "bulk" HQL/SQL operations:
  * {@link #lockRegion} -> {@link #removeAll} -> {@link #unlockRegion}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
-public interface EntityRegionAccessStrategy {
+public interface EntityRegionAccessStrategy extends RegionAccessStrategy{
 
 	/**
 	 * Get the wrapped entity cache region
 	 *
 	 * @return The underlying region
 	 */
 	public EntityRegion getRegion();
 
 	/**
-	 * Attempt to retrieve an object from the cache. Mainly used in attempting
-	 * to resolve entities/collections from the second level cache.
-	 *
-	 * @param key The key of the item to be retrieved.
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @return the cached object or <tt>null</tt>
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public Object get(Object key, long txTimestamp) throws CacheException;
-
-	/**
-	 * Attempt to cache an object, after loading from the database.
-	 *
-	 * @param key The item key
-	 * @param value The item
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @param version the item version number
-	 * @return <tt>true</tt> if the object was successfully cached
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public boolean putFromLoad(
-			Object key,
-			Object value,
-			long txTimestamp,
-			Object version) throws CacheException;
-
-	/**
-	 * Attempt to cache an object, after loading from the database, explicitly
-	 * specifying the minimalPut behavior.
-	 *
-	 * @param key The item key
-	 * @param value The item
-	 * @param txTimestamp a timestamp prior to the transaction start time
-	 * @param version the item version number
-	 * @param minimalPutOverride Explicit minimalPut flag
-	 * @return <tt>true</tt> if the object was successfully cached
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public boolean putFromLoad(
-			Object key,
-			Object value,
-			long txTimestamp,
-			Object version,
-			boolean minimalPutOverride) throws CacheException;
-
-	/**
-	 * We are going to attempt to update/delete the keyed object. This
-	 * method is used by "asynchronous" concurrency strategies.
-	 * <p/>
-	 * The returned object must be passed back to release(), to release the
-	 * lock. Concurrency strategies which do not support client-visible
-	 * locks may silently return null.
-	 *
-	 * @param key The key of the item to lock
-	 * @param version The item's current version value
-	 * @return A representation of our lock on the item; or null.
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public SoftLock lockItem(Object key, Object version) throws CacheException;
-
-	/**
-	 * Lock the entire region
-	 *
-	 * @return A representation of our lock on the item; or null.
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public SoftLock lockRegion() throws CacheException;
-
-	/**
-	 * Called when we have finished the attempted update/delete (which may or
-	 * may not have been successful), after transaction completion.  This method
-	 * is used by "asynchronous" concurrency strategies.
-	 *
-	 * @param key The item key
-	 * @param lock The lock previously obtained from {@link #lockItem}
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void unlockItem(Object key, SoftLock lock) throws CacheException;
-
-	/**
-	 * Called after we have finished the attempted invalidation of the entire
-	 * region
-	 *
-	 * @param lock The lock previously obtained from {@link #lockRegion}
-	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
-	 */
-	public void unlockRegion(SoftLock lock) throws CacheException;
-
-	/**
 	 * Called after an item has been inserted (before the transaction completes),
 	 * instead of calling evict().
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param version The item's version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean insert(Object key, Object value, Object version) throws CacheException;
 
 	/**
 	 * Called after an item has been inserted (after the transaction completes),
 	 * instead of calling release().
 	 * This method is used by "asynchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param version The item's version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean afterInsert(Object key, Object value, Object version) throws CacheException;
 
 	/**
 	 * Called after an item has been updated (before the transaction completes),
 	 * instead of calling evict(). This method is used by "synchronous" concurrency
 	 * strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param currentVersion The item's current version value
 	 * @param previousVersion The item's previous version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException;
 
 	/**
 	 * Called after an item has been updated (after the transaction completes),
 	 * instead of calling release().  This method is used by "asynchronous"
 	 * concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param currentVersion The item's current version value
 	 * @param previousVersion The item's previous version value
 	 * @param lock The lock previously obtained from {@link #lockItem}
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException;
 
 	/**
 	 * Called after an item has become stale (before the transaction completes).
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The key of the item to remove
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public void remove(Object key) throws CacheException;
 
 	/**
 	 * Called to evict data from the entire region
 	 *
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public void removeAll() throws CacheException;
 
 	/**
 	 * Forcibly evict an item from the cache immediately without regard for transaction
 	 * isolation.
 	 *
 	 * @param key The key of the item to remove
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public void evict(Object key) throws CacheException;
 
 	/**
 	 * Forcibly evict all items from the cache immediately without regard for transaction
 	 * isolation.
 	 *
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	public void evictAll() throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
new file mode 100644
index 0000000000..c60c4f25e0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
@@ -0,0 +1,153 @@
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
+package org.hibernate.cache.spi.access;
+
+import org.hibernate.cache.CacheException;
+
+/**
+ * @author Gail Badner
+ */
+public interface RegionAccessStrategy {
+	/**
+	 * Attempt to retrieve an object from the cache. Mainly used in attempting
+	 * to resolve entities/collections from the second level cache.
+	 *
+	 * @param key The key of the item to be retrieved.
+	 * @param txTimestamp a timestamp prior to the transaction start time
+	 * @return the cached object or <tt>null</tt>
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	Object get(Object key, long txTimestamp) throws CacheException;
+
+	/**
+	 * Attempt to cache an object, after loading from the database.
+	 *
+	 * @param key The item key
+	 * @param value The item
+	 * @param txTimestamp a timestamp prior to the transaction start time
+	 * @param version the item version number
+	 * @return <tt>true</tt> if the object was successfully cached
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	boolean putFromLoad(
+			Object key,
+			Object value,
+			long txTimestamp,
+			Object version) throws CacheException;
+
+	/**
+	 * Attempt to cache an object, after loading from the database, explicitly
+	 * specifying the minimalPut behavior.
+	 *
+	 * @param key The item key
+	 * @param value The item
+	 * @param txTimestamp a timestamp prior to the transaction start time
+	 * @param version the item version number
+	 * @param minimalPutOverride Explicit minimalPut flag
+	 * @return <tt>true</tt> if the object was successfully cached
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	boolean putFromLoad(
+			Object key,
+			Object value,
+			long txTimestamp,
+			Object version,
+			boolean minimalPutOverride) throws CacheException;
+
+	/**
+	 * We are going to attempt to update/delete the keyed object. This
+	 * method is used by "asynchronous" concurrency strategies.
+	 * <p/>
+	 * The returned object must be passed back to release(), to release the
+	 * lock. Concurrency strategies which do not support client-visible
+	 * locks may silently return null.
+	 *
+	 * @param key The key of the item to lock
+	 * @param version The item's current version value
+	 * @return A representation of our lock on the item; or null.
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	SoftLock lockItem(Object key, Object version) throws CacheException;
+
+	/**
+	 * Lock the entire region
+	 *
+	 * @return A representation of our lock on the item; or null.
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	SoftLock lockRegion() throws CacheException;
+
+	/**
+	 * Called when we have finished the attempted update/delete (which may or
+	 * may not have been successful), after transaction completion.  This method
+	 * is used by "asynchronous" concurrency strategies.
+	 *
+	 * @param key The item key
+	 * @param lock The lock previously obtained from {@link #lockItem}
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	void unlockItem(Object key, SoftLock lock) throws CacheException;
+
+	/**
+	 * Called after we have finished the attempted invalidation of the entire
+	 * region
+	 *
+	 * @param lock The lock previously obtained from {@link #lockRegion}
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	void unlockRegion(SoftLock lock) throws CacheException;
+
+	/**
+	 * Called after an item has become stale (before the transaction completes).
+	 * This method is used by "synchronous" concurrency strategies.
+	 *
+	 * @param key The key of the item to remove
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	void remove(Object key) throws CacheException;
+
+	/**
+	 * Called to evict data from the entire region
+	 *
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	void removeAll() throws CacheException;
+
+	/**
+	 * Forcibly evict an item from the cache immediately without regard for transaction
+	 * isolation.
+	 *
+	 * @param key The key of the item to remove
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	void evict(Object key) throws CacheException;
+
+	/**
+	 * Forcibly evict all items from the cache immediately without regard for transaction
+	 * isolation.
+	 *
+	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
+	 */
+	void evictAll() throws CacheException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 5a925154b8..b45043307a 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1664,1493 +1664,1494 @@ public final class HbmBinder {
 
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
 
 		oneToOne.setEmbedded( "true".equals( node.attributeValue( "embed-xml" ) ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
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
 			property.setName("_identifierMapper");
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
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
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
 				uk.setName("_UniqueKey");
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
 				Property property = createProperty( value, propertyName, persistentClass
 					.getClassName(), subnode, mappings, inheritedMetas );
 				if ( !mutable ) property.setUpdateable(false);
 				if ( naturalId ) property.setNaturalIdentifier(true);
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
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
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
             LOG.debugf(
 					"Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName()
 			);
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
             LOG.debugf("Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole());
 			collection.addManyToManyFilter( name, condition );
 		}
 	}
 
 	public static final FlushMode getFlushMode(String flushMode) {
 		if ( flushMode == null ) {
 			return null;
 		}
 		else if ( "auto".equals( flushMode ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( "commit".equals( flushMode ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( "never".equals( flushMode ) ) {
 			return FlushMode.NEVER;
 		}
 		else if ( "manual".equals( flushMode ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( "always".equals( flushMode ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else {
 			throw new MappingException( "unknown flushmode" );
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
         LOG.debugf("Named query: %s -> %s", queryName, query);
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : new Integer( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : new Integer( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinition(
+				queryName,
 				query,
 				cacheable,
 				region,
 				timeout,
 				fetchSize,
 				getFlushMode( queryElem.attributeValue( "flush-mode" ) ) ,
 				getCacheMode( cacheMode ),
 				readOnly,
 				comment,
 				getParameterTypes(queryElem)
 			);
 
-		mappings.addQuery( queryName, namedQuery );
+		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static CacheMode getCacheMode(String cacheMode) {
 		if (cacheMode == null) return null;
 		if ( "get".equals( cacheMode ) ) return CacheMode.GET;
 		if ( "ignore".equals( cacheMode ) ) return CacheMode.IGNORE;
 		if ( "normal".equals( cacheMode ) ) return CacheMode.NORMAL;
 		if ( "put".equals( cacheMode ) ) return CacheMode.PUT;
 		if ( "refresh".equals( cacheMode ) ) return CacheMode.REFRESH;
 		throw new MappingException("Unknown Cache Mode: " + cacheMode);
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
 
 		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
 		String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable & !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
         LOG.debugf("Parsing filter-def [%s]", name);
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
             LOG.debugf("Adding filter parameter : %s -> %s", paramName, paramType);
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
             LOG.debugf("Parameter heuristic type : %s", heuristicType);
 			paramMappings.put( paramName, heuristicType );
 		}
         LOG.debugf("Parsed filter-def [%s]", name);
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.buildSessionFactory
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
         LOG.debugf("Applying filter [%s] as [%s]", name, condition);
 		filterable.addFilter( name, condition );
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
index 636b94df4a..478912e0f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
@@ -1,125 +1,127 @@
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
 import java.util.Iterator;
 import java.util.Map;
 import org.dom4j.Attribute;
 import org.dom4j.Element;
 
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.internal.util.StringHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class NamedSQLQuerySecondPass extends ResultSetMappingBinder implements QuerySecondPass {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        NamedSQLQuerySecondPass.class.getName());
 
 	private Element queryElem;
 	private String path;
 	private Mappings mappings;
 
 	public NamedSQLQuerySecondPass(Element queryElem, String path, Mappings mappings) {
 		this.queryElem = queryElem;
 		this.path = path;
 		this.mappings = mappings;
 	}
 
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		String queryName = queryElem.attribute( "name" ).getValue();
 		if (path!=null) queryName = path + '.' + queryName;
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : new Integer( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : new Integer( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		java.util.List<String> synchronizedTables = new ArrayList<String>();
 		Iterator tables = queryElem.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			synchronizedTables.add( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 		boolean callable = "true".equals( queryElem.attributeValue( "callable" ) );
 
 		NamedSQLQueryDefinition namedQuery;
 		Attribute ref = queryElem.attribute( "resultset-ref" );
 		String resultSetRef = ref == null ? null : ref.getValue();
 		if ( StringHelper.isNotEmpty( resultSetRef ) ) {
 			namedQuery = new NamedSQLQueryDefinition(
+					queryName,
 					queryElem.getText(),
 					resultSetRef,
 					synchronizedTables,
 					cacheable,
 					region,
 					timeout,
 					fetchSize,
 					HbmBinder.getFlushMode( queryElem.attributeValue( "flush-mode" ) ),
 					HbmBinder.getCacheMode( cacheMode ),
 					readOnly,
 					comment,
 					HbmBinder.getParameterTypes( queryElem ),
 					callable
 			);
 			//TODO check there is no actual definition elemnents when a ref is defined
 		}
 		else {
 			ResultSetMappingDefinition definition = buildResultSetMappingDefinition( queryElem, path, mappings );
 			namedQuery = new NamedSQLQueryDefinition(
+					queryName,
 					queryElem.getText(),
 					definition.getQueryReturns(),
 					synchronizedTables,
 					cacheable,
 					region,
 					timeout,
 					fetchSize,
 					HbmBinder.getFlushMode( queryElem.attributeValue( "flush-mode" ) ),
 					HbmBinder.getCacheMode( cacheMode ),
 					readOnly,
 					comment,
 					HbmBinder.getParameterTypes( queryElem ),
 					callable
 			);
 		}
 
-        LOG.debugf("Named SQL query: %s -> %s", queryName, namedQuery.getQueryString());
+        LOG.debugf("Named SQL query: %s -> %s", namedQuery.getName(), namedQuery.getQueryString());
 		mappings.addSQLQuery( queryName, namedQuery );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
index 03648535a0..cb1ad0e7d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
@@ -1,418 +1,424 @@
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
 import java.util.HashMap;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import javax.persistence.QueryHint;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.annotations.CacheModeType;
 import org.hibernate.annotations.FlushModeType;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 
 import org.jboss.logging.Logger;
 
 /**
  * Query binder
  *
  * @author Emmanuel Bernard
  */
 public abstract class QueryBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryBinder.class.getName());
 
 	public static void bindQuery(NamedQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
         if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) throw new AnnotationException(
                                                                                                 "A named query must have a name when used in class or package level");
 		//EJBQL Query
 		QueryHint[] hints = queryAnn.hints();
 		String queryName = queryAnn.query();
 		NamedQueryDefinition query = new NamedQueryDefinition(
+				queryAnn.name(),
 				queryName,
 				getBoolean( queryName, "org.hibernate.cacheable", hints ),
 				getString( queryName, "org.hibernate.cacheRegion", hints ),
 				getTimeout( queryName, hints ),
 				getInteger( queryName, "org.hibernate.fetchSize", hints ),
 				getFlushMode( queryName, hints ),
 				getCacheMode( queryName, hints ),
 				getBoolean( queryName, "org.hibernate.readOnly", hints ),
 				getString( queryName, "org.hibernate.comment", hints ),
 				null
 		);
 		if ( isDefault ) {
-			mappings.addDefaultQuery( queryAnn.name(), query );
+			mappings.addDefaultQuery( query.getName(), query );
 		}
 		else {
-			mappings.addQuery( queryAnn.name(), query );
+			mappings.addQuery( query.getName(), query );
 		}
-        LOG.debugf( "Binding named query: %s => %s", queryAnn.name(), queryAnn.query() );
+        LOG.debugf( "Binding named query: %s => %s", query.getName(), query.getQueryString() );
 	}
 
 
 	public static void bindNativeQuery(NamedNativeQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
         if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) throw new AnnotationException(
                                                                                                 "A named query must have a name when used in class or package level");
 		NamedSQLQueryDefinition query;
 		String resultSetMapping = queryAnn.resultSetMapping();
 		QueryHint[] hints = queryAnn.hints();
 		String queryName = queryAnn.query();
 		if ( !BinderHelper.isEmptyAnnotationValue( resultSetMapping ) ) {
 			//sql result set usage
 			query = new NamedSQLQueryDefinition(
+					queryAnn.name(),
 					queryName,
 					resultSetMapping,
 					null,
 					getBoolean( queryName, "org.hibernate.cacheable", hints ),
 					getString( queryName, "org.hibernate.cacheRegion", hints ),
 					getTimeout( queryName, hints ),
 					getInteger( queryName, "org.hibernate.fetchSize", hints ),
 					getFlushMode( queryName, hints ),
 					getCacheMode( queryName, hints ),
 					getBoolean( queryName, "org.hibernate.readOnly", hints ),
 					getString( queryName, "org.hibernate.comment", hints ),
 					null,
 					getBoolean( queryName, "org.hibernate.callable", hints )
 			);
 		}
 		else if ( !void.class.equals( queryAnn.resultClass() ) ) {
 			//class mapping usage
 			//FIXME should be done in a second pass due to entity name?
 			final NativeSQLQueryRootReturn entityQueryReturn =
 					new NativeSQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
 			query = new NamedSQLQueryDefinition(
+					queryAnn.name(),
 					queryName,
 					new NativeSQLQueryReturn[] { entityQueryReturn },
 					null,
 					getBoolean( queryName, "org.hibernate.cacheable", hints ),
 					getString( queryName, "org.hibernate.cacheRegion", hints ),
 					getTimeout( queryName, hints ),
 					getInteger( queryName, "org.hibernate.fetchSize", hints ),
 					getFlushMode( queryName, hints ),
 					getCacheMode( queryName, hints ),
 					getBoolean( queryName, "org.hibernate.readOnly", hints ),
 					getString( queryName, "org.hibernate.comment", hints ),
 					null,
 					getBoolean( queryName, "org.hibernate.callable", hints )
 			);
 		}
 		else {
 			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 		}
 		if ( isDefault ) {
-			mappings.addDefaultSQLQuery( queryAnn.name(), query );
+			mappings.addDefaultSQLQuery( query.getName(), query );
 		}
 		else {
-			mappings.addSQLQuery( queryAnn.name(), query );
+			mappings.addSQLQuery( query.getName(), query );
 		}
         LOG.debugf( "Binding named native query: %s => %s", queryAnn.name(), queryAnn.query() );
 	}
 
 	public static void bindNativeQuery(org.hibernate.annotations.NamedNativeQuery queryAnn, Mappings mappings) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
         if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) throw new AnnotationException(
                                                                                                 "A named query must have a name when used in class or package level");
         NamedSQLQueryDefinition query;
 		String resultSetMapping = queryAnn.resultSetMapping();
 		if ( !BinderHelper.isEmptyAnnotationValue( resultSetMapping ) ) {
 			//sql result set usage
 			query = new NamedSQLQueryDefinition(
+					queryAnn.name(),
 					queryAnn.query(),
 					resultSetMapping,
 					null,
 					queryAnn.cacheable(),
 					BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
 					queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
 					queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
 					getFlushMode( queryAnn.flushMode() ),
 					getCacheMode( queryAnn.cacheMode() ),
 					queryAnn.readOnly(),
 					BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment(),
 					null,
 					queryAnn.callable()
 			);
 		}
 		else if ( !void.class.equals( queryAnn.resultClass() ) ) {
 			//class mapping usage
 			//FIXME should be done in a second pass due to entity name?
 			final NativeSQLQueryRootReturn entityQueryReturn =
 					new NativeSQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
 			query = new NamedSQLQueryDefinition(
+					queryAnn.name(),
 					queryAnn.query(),
 					new NativeSQLQueryReturn[] { entityQueryReturn },
 					null,
 					queryAnn.cacheable(),
 					BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
 					queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
 					queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
 					getFlushMode( queryAnn.flushMode() ),
 					getCacheMode( queryAnn.cacheMode() ),
 					queryAnn.readOnly(),
 					BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment(),
 					null,
 					queryAnn.callable()
 			);
 		}
 		else {
 			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 		}
-		mappings.addSQLQuery( queryAnn.name(), query );
-        LOG.debugf( "Binding named native query: %s => %s", queryAnn.name(), queryAnn.query() );
+		mappings.addSQLQuery( query.getName(), query );
+        LOG.debugf( "Binding named native query: %s => %s", query.getName(), queryAnn.query() );
 	}
 
 	public static void bindQueries(NamedQueries queriesAnn, Mappings mappings, boolean isDefault) {
 		if ( queriesAnn == null ) return;
 		for (NamedQuery q : queriesAnn.value()) {
 			bindQuery( q, mappings, isDefault );
 		}
 	}
 
 	public static void bindNativeQueries(NamedNativeQueries queriesAnn, Mappings mappings, boolean isDefault) {
 		if ( queriesAnn == null ) return;
 		for (NamedNativeQuery q : queriesAnn.value()) {
 			bindNativeQuery( q, mappings, isDefault );
 		}
 	}
 
 	public static void bindNativeQueries(
 			org.hibernate.annotations.NamedNativeQueries queriesAnn, Mappings mappings
 	) {
 		if ( queriesAnn == null ) return;
 		for (org.hibernate.annotations.NamedNativeQuery q : queriesAnn.value()) {
 			bindNativeQuery( q, mappings );
 		}
 	}
 
 	public static void bindQuery(org.hibernate.annotations.NamedQuery queryAnn, Mappings mappings) {
 		if ( queryAnn == null ) return;
         if (BinderHelper.isEmptyAnnotationValue(queryAnn.name())) throw new AnnotationException(
                                                                                                 "A named query must have a name when used in class or package level");
 		FlushMode flushMode;
 		flushMode = getFlushMode( queryAnn.flushMode() );
 
 		NamedQueryDefinition query = new NamedQueryDefinition(
+				queryAnn.name(),
 				queryAnn.query(),
 				queryAnn.cacheable(),
 				BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
 				queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
 				queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
 				flushMode,
 				getCacheMode( queryAnn.cacheMode() ),
 				queryAnn.readOnly(),
 				BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment(),
 				null
 		);
 
-		mappings.addQuery( queryAnn.name(), query );
-        LOG.debugf( "Binding named query: %s => %s", queryAnn.name(), queryAnn.query() );
+		mappings.addQuery( query.getName(), query );
+        LOG.debugf( "Binding named query: %s => %s", query.getName(), query.getQueryString() );
 	}
 
 	private static FlushMode getFlushMode(FlushModeType flushModeType) {
 		FlushMode flushMode;
 		switch ( flushModeType ) {
 			case ALWAYS:
 				flushMode = FlushMode.ALWAYS;
 				break;
 			case AUTO:
 				flushMode = FlushMode.AUTO;
 				break;
 			case COMMIT:
 				flushMode = FlushMode.COMMIT;
 				break;
 			case NEVER:
 				flushMode = FlushMode.MANUAL;
 				break;
 			case MANUAL:
 				flushMode = FlushMode.MANUAL;
 				break;
 			case PERSISTENCE_CONTEXT:
 				flushMode = null;
 				break;
 			default:
 				throw new AssertionFailure( "Unknown flushModeType: " + flushModeType );
 		}
 		return flushMode;
 	}
 
 	private static CacheMode getCacheMode(CacheModeType cacheModeType) {
 		switch ( cacheModeType ) {
 			case GET:
 				return CacheMode.GET;
 			case IGNORE:
 				return CacheMode.IGNORE;
 			case NORMAL:
 				return CacheMode.NORMAL;
 			case PUT:
 				return CacheMode.PUT;
 			case REFRESH:
 				return CacheMode.REFRESH;
 			default:
 				throw new AssertionFailure( "Unknown cacheModeType: " + cacheModeType );
 		}
 	}
 
 
 	public static void bindQueries(org.hibernate.annotations.NamedQueries queriesAnn, Mappings mappings) {
 		if ( queriesAnn == null ) return;
 		for (org.hibernate.annotations.NamedQuery q : queriesAnn.value()) {
 			bindQuery( q, mappings );
 		}
 	}
 
 	public static void bindSqlResultsetMappings(SqlResultSetMappings ann, Mappings mappings, boolean isDefault) {
 		if ( ann == null ) return;
 		for (SqlResultSetMapping rs : ann.value()) {
 			//no need to handle inSecondPass
 			mappings.addSecondPass( new ResultsetMappingSecondPass( rs, mappings, true ) );
 		}
 	}
 
 	public static void bindSqlResultsetMapping(SqlResultSetMapping ann, Mappings mappings, boolean isDefault) {
 		//no need to handle inSecondPass
 		mappings.addSecondPass( new ResultsetMappingSecondPass( ann, mappings, isDefault ) );
 	}
 
 	private static CacheMode getCacheMode(String query, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( "org.hibernate.cacheMode".equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 					return CacheMode.GET;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 					return CacheMode.IGNORE;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 					return CacheMode.NORMAL;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 					return CacheMode.PUT;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 					return CacheMode.REFRESH;
 				}
 				else {
 					throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static FlushMode getFlushMode(String query, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( "org.hibernate.flushMode".equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 					return FlushMode.ALWAYS;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 					return FlushMode.AUTO;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 					return FlushMode.COMMIT;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 					return FlushMode.MANUAL;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 					return FlushMode.MANUAL;
 				}
 				else {
 					throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static boolean getBoolean(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( "true" ) ) {
 					return true;
 				}
 				else if ( hint.value().equalsIgnoreCase( "false" ) ) {
 					return false;
 				}
 				else {
 					throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return false;
 	}
 
 	private static String getString(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				return hint.value();
 			}
 		}
 		return null;
 	}
 
 	private static Integer getInteger(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				try {
 					return Integer.decode( hint.value() );
 				}
 				catch (NumberFormatException nfe) {
 					throw new AnnotationException( "Not an integer in hint: " + query + ":" + hint.name(), nfe );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(String queryName, QueryHint[] hints) {
 		Integer timeout = getInteger( queryName, "javax.persistence.query.timeout", hints );
 
 		if ( timeout != null ) {
 			// convert milliseconds to seconds
 			timeout = new Integer ((int)Math.round(timeout.doubleValue() / 1000.0 ) );
 		}
 		else {
 			// timeout is already in seconds
 			timeout = getInteger( queryName, "org.hibernate.timeout", hints );
 		}
 		return timeout;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
index 47b97dfbfd..d021ef32a4 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
@@ -1,142 +1,151 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 
 /**
  * Definition of a named query, defined in the mapping metadata.
  *
  * @author Gavin King
  */
 public class NamedQueryDefinition implements Serializable {
+	private final String name;
 	private final String query;
 	private final boolean cacheable;
 	private final String cacheRegion;
 	private final Integer timeout;
 	private final Integer fetchSize;
 	private final FlushMode flushMode;
 	private final Map parameterTypes;
 	private CacheMode cacheMode;
 	private boolean readOnly;
 	private String comment;
 
 	// kept for backward compatibility until after the 3.1beta5 release of HA
+	// TODO: is this still needed?
 	public NamedQueryDefinition(
 			String query,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			Map parameterTypes) {
 		this(
+				null,
 				query,
 				cacheable,
 				cacheRegion,
 				timeout,
 				fetchSize,
 				flushMode,
 				null,
 				false,
 				null,
 				parameterTypes
 		);
 	}
 
 	public NamedQueryDefinition(
+			String name,
 			String query,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
 			Map parameterTypes) {
+		this.name = name;
 		this.query = query;
 		this.cacheable = cacheable;
 		this.cacheRegion = cacheRegion;
 		this.timeout = timeout;
 		this.fetchSize = fetchSize;
 		this.flushMode = flushMode;
 		this.parameterTypes = parameterTypes;
 		this.cacheMode = cacheMode;
 		this.readOnly = readOnly;
 		this.comment = comment;
 	}
 
+	public String getName() {
+		return name;
+	}
+
 	public String getQueryString() {
 		return query;
 	}
 
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	public Integer getFetchSize() {
 		return fetchSize;
 	}
 
 	public Integer getTimeout() {
 		return timeout;
 	}
 
 	public FlushMode getFlushMode() {
 		return flushMode;
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + query + ')';
 	}
 
 	public Map getParameterTypes() {
 		return parameterTypes;
 	}
 
 	public String getQuery() {
 		return query;
 	}
 
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	public boolean isReadOnly() {
 		return readOnly;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
index 106140e47d..b3f675be17 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
@@ -1,206 +1,211 @@
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
 package org.hibernate.engine.spi;
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 
 /**
  * Definition of a named native SQL query, defined in the mapping metadata.
  * 
  * @author Max Andersen
  */
 public class NamedSQLQueryDefinition extends NamedQueryDefinition {
 
 	private NativeSQLQueryReturn[] queryReturns;
 	private final List<String> querySpaces;
 	private final boolean callable;
 	private String resultSetRef;
 
 	/**
 	 * This form used to construct a NamedSQLQueryDefinition from the binder
 	 * code when a the result-set mapping information is explicitly
 	 * provided in the query definition (i.e., no resultset-mapping used)
 	 *
 	 * @param query The sql query string
 	 * @param queryReturns The in-lined query return definitions
 	 * @param querySpaces Any specified query spaces (used for auto-flushing)
 	 * @param cacheable Whether the query results are cacheable
 	 * @param cacheRegion If cacheable, the region into which to store the results
 	 * @param timeout A JDBC-level timeout to be applied
 	 * @param fetchSize A JDBC-level fetch-size to be applied
 	 * @param flushMode The flush mode to use for this query
 	 * @param cacheMode The cache mode to use during execution and subsequent result loading
 	 * @param readOnly Whether returned entities should be marked as read-only in the session
 	 * @param comment Any sql comment to be applied to the query
 	 * @param parameterTypes parameter type map
 	 * @param callable Does the query string represent a callable object (i.e., proc)
 	 */
 	public NamedSQLQueryDefinition(
+			String name,
 			String query,
 			NativeSQLQueryReturn[] queryReturns,
 			List<String> querySpaces,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
 			Map parameterTypes,
 			boolean callable) {
 		super(
+				name,
 				query.trim(), /* trim done to workaround stupid oracle bug that cant handle whitespaces before a { in a sp */
 				cacheable,
 				cacheRegion,
 				timeout,
 				fetchSize,
 				flushMode,
 				cacheMode,
 				readOnly,
 				comment,
 				parameterTypes
 		);
 		this.queryReturns = queryReturns;
 		this.querySpaces = querySpaces;
 		this.callable = callable;
 	}
 
 	/**
 	 * This form used to construct a NamedSQLQueryDefinition from the binder
 	 * code when a resultset-mapping reference is used.
 	 *
 	 * @param query The sql query string
 	 * @param resultSetRef The resultset-mapping name
 	 * @param querySpaces Any specified query spaces (used for auto-flushing)
 	 * @param cacheable Whether the query results are cacheable
 	 * @param cacheRegion If cacheable, the region into which to store the results
 	 * @param timeout A JDBC-level timeout to be applied
 	 * @param fetchSize A JDBC-level fetch-size to be applied
 	 * @param flushMode The flush mode to use for this query
 	 * @param cacheMode The cache mode to use during execution and subsequent result loading
 	 * @param readOnly Whether returned entities should be marked as read-only in the session
 	 * @param comment Any sql comment to be applied to the query
 	 * @param parameterTypes parameter type map
 	 * @param callable Does the query string represent a callable object (i.e., proc)
 	 */
 	public NamedSQLQueryDefinition(
+			String name,
 			String query,
 			String resultSetRef,
 			List<String> querySpaces,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
 			Map parameterTypes,
 			boolean callable) {
 		super(
+				name,
 				query.trim(), /* trim done to workaround stupid oracle bug that cant handle whitespaces before a { in a sp */
 				cacheable,
 				cacheRegion,
 				timeout,
 				fetchSize,
 				flushMode,
 				cacheMode,
 				readOnly,
 				comment,
 				parameterTypes
 		);
 		this.resultSetRef = resultSetRef;
 		this.querySpaces = querySpaces;
 		this.callable = callable;
 	}
 
 	/**
 	 * This form used from annotations (?).  Essentially the same as the above using a
 	 * resultset-mapping reference, but without cacheMode, readOnly, and comment.
 	 *
 	 * FIXME: annotations do not use it, so it can be remove from my POV
 	 * @deprecated
 	 *
 	 *
 	 * @param query The sql query string
 	 * @param resultSetRef The result-set-mapping name
 	 * @param querySpaces Any specified query spaces (used for auto-flushing)
 	 * @param cacheable Whether the query results are cacheable
 	 * @param cacheRegion If cacheable, the region into which to store the results
 	 * @param timeout A JDBC-level timeout to be applied
 	 * @param fetchSize A JDBC-level fetch-size to be applied
 	 * @param flushMode The flush mode to use for this query
 	 * @param parameterTypes parameter type map
 	 * @param callable Does the query string represent a callable object (i.e., proc)
 	 */
 	public NamedSQLQueryDefinition(
 			String query,
 			String resultSetRef,
 			List<String> querySpaces,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			Map parameterTypes,
 			boolean callable) {
 		this(
+				null,
 				query,
 				resultSetRef,
 				querySpaces,
 				cacheable,
 				cacheRegion,
 				timeout,
 				fetchSize,
 				flushMode,
 				null,
 				false,
 				null,
 				parameterTypes,
 				callable
 		);
 	}
 
 	public NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns;
 	}
 
 	public List<String> getQuerySpaces() {
 		return querySpaces;
 	}
 
 	public boolean isCallable() {
 		return callable;
 	}
 
 	public String getResultSetRef() {
 		return resultSetRef;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index a26521ee92..57f16a315a 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1608 +1,1699 @@
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
 package org.hibernate.internal;
 
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
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jndi.spi.JndiService;
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
  * @see org.hibernate.hql.spi.QueryTranslator
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
-	private final transient Map namedQueries;
-	private final transient Map namedSqlQueries;
-	private final transient Map sqlResultSetMappings;
+	private final transient Map<String, NamedQueryDefinition> namedQueries;
+	private final transient Map<String, NamedSQLQueryDefinition> namedSqlQueries;
+	private final transient Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map fetchProfiles;
-	private final transient Map imports;
+	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient HashMap entityNameResolvers = new HashMap();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	@SuppressWarnings( {"unchecked"} )
 	public SessionFactoryImpl(
 			final Configuration cfg,
 	        Mapping mapping,
 			ServiceRegistry serviceRegistry,
 	        Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
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
 
 		this.filters = new HashMap<String, FilterDefinition>();
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
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
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
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
                 LOG.trace("Building cache for collection data [" + model.getRole() + "]");
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
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
-		namedQueries = new HashMap( cfg.getNamedQueries() );
-		namedSqlQueries = new HashMap( cfg.getNamedSQLQueries() );
-		sqlResultSetMappings = new HashMap( cfg.getSqlResultSetMappings() );
-		imports = new HashMap( cfg.getImports() );
+		namedQueries = new HashMap<String, NamedQueryDefinition>( cfg.getNamedQueries() );
+		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>( cfg.getNamedSQLQueries() );
+		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>( cfg.getSqlResultSetMappings() );
+		imports = new HashMap<String,String>( cfg.getImports() );
 
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
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
 
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
 
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		// TODO: remove initialization of final variables; just setting to null to make compiler happy
 		this.name = null;
 		this.uuid = null;
-		this.collectionPersisters = null;
-		this.collectionMetadata = null;
-		this.collectionRolesByEntityParticipant = null;
-		this.namedQueries = null;
-		this.namedSqlQueries = null;
-		this.sqlResultSetMappings = null;
 		this.fetchProfiles = null;
-		this.imports = null;
 		this.queryCache = null;
 		this.updateTimestampsCache = null;
 		this.queryCaches = null;
 		this.currentSessionContext = null;
 		this.sqlFunctionRegistry = null;
 		this.transactionEnvironment = null;
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				metadata.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
         LOG.debugf("Session factory constructed with filter configurations : %s", filters);
         LOG.debugf("Instantiating session factory with properties: %s", properties );
 
 		// TODO: get RegionFactory from service registry
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
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
 			// TODO: add Integrator.integrate(MetadataImplementor, ...)
 			// integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				// TODO: create the IdentifierGenerator while the metadata is being build, then simply
 				// use EntityBinding.getIdentifierGenerator() (also remove getIdentifierGeneratorFactory from Mappings)
-				IdentifierGenerator generator = entityBinding.getEntityIdentifier().createIdentifierGenerator(
-						metadata.getIdentifierGeneratorFactory()
-				);
-				identifierGenerators.put( entityBinding.getEntity().getName(), generator );
+				// TODO: this is broken; throws NullPointerException
+				//IdentifierGenerator generator = entityBinding.getEntityIdentifier().createIdentifierGenerator(
+				//		metadata.getIdentifierGeneratorFactory()
+				//);
+				//identifierGenerators.put( entityBinding.getEntity().getName(), generator );
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap();
-		Map<String, EntityRegionAccessStrategy> entityAccessStrategies = new HashMap<String, EntityRegionAccessStrategy>();
+		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getCaching() != null &&
 					model.getCaching() != null &&
 					model.getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getCaching().getRegion();
-				accessStrategy = entityAccessStrategies.get( cacheRegionName );
+				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getCaching().getAccessType();
 					LOG.trace("Building cache for entity data [" + model.getEntity().getName() + "]");
 					EntityRegion entityRegion =
 							settings.getRegionFactory().buildEntityRegion(
 									cacheRegionName,
 									properties,
 									CacheDataDescriptionImpl.decode( model )
 							);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
+		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
+		collectionPersisters = new HashMap();
+		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
+			if ( model.getAttribute() == null ) {
+				throw new IllegalStateException( "No attribute defined for a PluralAttributeBinding: " +  model );
+			}
+			if ( model.getAttribute().isSingular() ) {
+				throw new IllegalStateException(
+						"PluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
+				);
+			}
+			// TODO: Add PluralAttributeBinding.getCaching()
+			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
+			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
+			CollectionRegionAccessStrategy accessStrategy = null;
+			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
+				// TODO: is model.getAttribute().getName() the collection's role??? For now, assuming it is
+                LOG.trace("Building cache for collection data [" + model.getAttribute().getName() + "]");
+				CollectionRegion collectionRegion =
+						settings.getRegionFactory()
+								.buildCollectionRegion(
+										cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
+								);
+				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
+				entityAccessStrategies.put( cacheRegionName, accessStrategy );
+				allCacheRegions.put( cacheRegionName, collectionRegion );
+			}
+			CollectionPersister persister =
+					serviceRegistry
+							.getService( PersisterFactory.class )
+							.createCollectionPersister( metadata, model, accessStrategy, this );
+			// TODO: is model.getAttribute().getName() the collection's role??? For now, assuming it is
+			collectionPersisters.put( model.getAttribute().getName(), persister.getCollectionMetadata() );
+			Type indexType = persister.getIndexType();
+			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
+				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
+				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
+				if ( roles == null ) {
+					roles = new HashSet();
+					tmpEntityToCollectionRoleMap.put( entityName, roles );
+				}
+				roles.add( persister.getRole() );
+			}
+			Type elementType = persister.getElementType();
+			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
+				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
+				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
+				if ( roles == null ) {
+					roles = new HashSet();
+					tmpEntityToCollectionRoleMap.put( entityName, roles );
+				}
+				roles.add( persister.getRole() );
+			}
+		}
+		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
+		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
+		while ( itr.hasNext() ) {
+			final Map.Entry entry = ( Map.Entry ) itr.next();
+			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
+		}
+		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
+
+		//Named Queries:
+		namedQueries = new HashMap<String,NamedQueryDefinition>();
+		for ( NamedQueryDefinition namedQueryDefinition :  metadata.getNamedQueryDefinitions() ) {
+			namedQueries.put( namedQueryDefinition.getName(), namedQueryDefinition );
+		}
+		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
+		for ( NamedSQLQueryDefinition namedNativeQueryDefinition: metadata.getNamedNativeQueryDefinitions() ) {
+			namedSqlQueries.put( namedNativeQueryDefinition.getName(), namedNativeQueryDefinition );
+		}
+		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
+		for( ResultSetMappingDefinition resultSetMappingDefinition : metadata.getResultSetMappingDefinitions() ) {
+			sqlResultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
+		}
+		imports = new HashMap<String,String>();
+		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
+			imports.put( importEntry.getKey(), importEntry.getValue() );
+		}
+
+		// after *all* persisters and named queries are registered
+		Iterator iter = entityPersisters.values().iterator();
+		while ( iter.hasNext() ) {
+			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
+			// TODO: broken
+			//persister.postInstantiate();
+			registerEntityNameResolvers( persister );
+
+		}
+		iter = collectionPersisters.values().iterator();
+		while ( iter.hasNext() ) {
+			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
+			persister.postInstantiate();
+		}
+
 		// TODO: implement
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
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
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
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
 		if(LOG.isDebugEnabled())
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
 		if(LOG.isDebugEnabled())
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
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
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
 		return sessionFactoryOptions.getInterceptor();
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
 
 	@Override
 	public Reference getReference() throws NamingException {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
         LOG.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
             if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
             LOG.debugf("Resolved SessionFactory by name");
         }
 		else {
 			LOG.debugf("Resolved SessionFactory by UUID");
 		}
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
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid, name, serviceRegistry.getService( JndiService.class )
 		);
 
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
 	}
 
 	public QueryCache getQueryCache() {
 		return queryCache;
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			return getQueryCache();
 		}
 
 		if ( !settings.isQueryCacheEnabled() ) {
 			return null;
 		}
 
 		QueryCache currentQueryCache = queryCaches.get( regionName );
 		if ( currentQueryCache == null ) {
 			currentQueryCache = settings.getQueryCacheFactory().getQueryCache( regionName, updateTimestampsCache, settings, properties );
 			queryCaches.put( regionName, currentQueryCache );
 			allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 		}
 
 		return currentQueryCache;
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	public Map getAllSecondLevelCacheRegions() {
 		return new HashMap( allCacheRegions );
 	}
 
 	public boolean isClosed() {
 		return isClosed;
 	}
 
 	public Statistics getStatistics() {
 		return getStatisticsImplementor();
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return serviceRegistry.getService( StatisticsImplementor.class );
 	}
 
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		FilterDefinition def = ( FilterDefinition ) filters.get( filterName );
 		if ( def == null ) {
 			throw new HibernateException( "No such filter configured [" + filterName + "]" );
 		}
 		return def;
 	}
 
 	public boolean containsFetchProfileDefinition(String name) {
 		return fetchProfiles.containsKey( name );
 	}
 
 	public Set getDefinedFilterNames() {
 		return filters.keySet();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return (IdentifierGenerator) identifierGenerators.get(rootEntityName);
 	}
 
 	private org.hibernate.engine.transaction.spi.TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( org.hibernate.engine.transaction.spi.TransactionFactory.class );
 	}
 
 	private boolean canAccessTransactionManager() {
 		try {
 			return serviceRegistry.getService( JtaPlatform.class ).retrieveTransactionManager() != null;
 		}
 		catch (Exception e) {
 			return false;
 		}
 	}
 
 	private CurrentSessionContext buildCurrentSessionContext() {
 		String impl = properties.getProperty( Environment.CURRENT_SESSION_CONTEXT_CLASS );
 		// for backward-compatibility
 		if ( impl == null ) {
 			if ( canAccessTransactionManager() ) {
 				impl = "jta";
 			}
 			else {
 				return null;
 			}
 		}
 
 		if ( "jta".equals( impl ) ) {
 			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
                 LOG.autoFlushWillNotWork();
 			}
 			return new JTASessionContext( this );
 		}
 		else if ( "thread".equals( impl ) ) {
 			return new ThreadLocalSessionContext( this );
 		}
 		else if ( "managed".equals( impl ) ) {
 			return new ManagedSessionContext( this );
 		}
 		else {
 			try {
 				Class implClass = ReflectHelper.classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
                 LOG.unableToConstructCurrentSessionContext(impl, t);
 				return null;
 			}
 		}
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return ( FetchProfile ) fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	/**
 	 * Custom serialization hook used during Session serialization.
 	 *
 	 * @param oos The stream to which to write the factory
 	 * @throws IOException Indicates problems writing out the serial data stream
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeUTF( uuid );
 		oos.writeBoolean( name != null );
 		if ( name != null ) {
 			oos.writeUTF( name );
 		}
 	}
 
 	/**
 	 * Custom deserialization hook used during Session deserialization.
 	 *
 	 * @param ois The stream from which to "read" the factory
 	 * @return The deserialized factory
 	 * @throws IOException indicates problems reading back serial data stream
 	 * @throws ClassNotFoundException indicates problems reading back serial data stream
 	 */
 	static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
             LOG.trace("Could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name");
 			if ( isNamed ) {
 				result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private EntityMode entityMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.entityMode = settings.getDefaultEntityMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
 					connection,
 					sessionFactory,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					entityMode,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 		}
 
 		@Override
 		public SessionBuilder interceptor(Interceptor interceptor) {
 			this.interceptor = interceptor;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder noInterceptor() {
 			this.interceptor = EmptyInterceptor.INSTANCE;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			this.connectionReleaseMode = connectionReleaseMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			this.autoJoinTransactions = autoJoinTransactions;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder autoClose(boolean autoClose) {
 			this.autoClose = autoClose;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			this.flushBeforeCompletion = flushBeforeCompletion;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder entityMode(EntityMode entityMode) {
 			this.entityMode = entityMode;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index a63d14aa3a..8f3f861b4f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
@@ -1,76 +1,91 @@
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
 
 package org.hibernate.metamodel;
 
+import java.util.Map;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.IdGenerator;
+import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 
 /**
  * @author Steve Ebersole
  */
 public interface Metadata {
 	/**
 	 * Exposes the options used to produce a {@link Metadata} instance.
 	 */
 	public static interface Options {
 		public SourceProcessingOrder getSourceProcessingOrder();
 		public NamingStrategy getNamingStrategy();
 		public SharedCacheMode getSharedCacheMode();
 		public AccessType getDefaultAccessType();
 		public boolean useNewIdentifierGenerators();
 		public String getDefaultSchemaName();
 		public String getDefaultCatalogName();
 	}
 
 	public Options getOptions();
 
 	public SessionFactoryBuilder getSessionFactoryBuilder();
 
 	public SessionFactory buildSessionFactory();
 
 	public Iterable<EntityBinding> getEntityBindings();
 
 	public EntityBinding getEntityBinding(String entityName);
 
 	/**
 	 * Get the "root" entity binding
 	 * @param entityName
 	 * @return the "root entity binding; simply returns entityBinding if it is the root entity binding
 	 */
 	public EntityBinding getRootEntityBinding(String entityName);
 
+	public Iterable<PluralAttributeBinding> getCollectionBindings();
+
 	public Iterable<TypeDef> getTypeDefinitions();
 
 	public Iterable<FilterDefinition> getFilterDefinitions();
 
+	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions();
+
+	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();
+
+	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions();
+
+	public Iterable<Map.Entry<String, String>> getImports();
+
 	public IdGenerator getIdGenerator(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
index 2a86402719..660843ff7a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
@@ -1,301 +1,303 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import java.util.HashMap;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.annotations.QueryHints;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 public class QueryBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryBinder.class.getName()
 	);
 
 	/**
 	 * Binds all {@link NamedQuery}, {@link NamedQueries}, {@link NamedNativeQuery}, {{@link NamedNativeQueries},
 	 * {@link org.hibernate.annotations.NamedQuery} , {@link org.hibernate.annotations.NamedQueries},
 	 * {@link org.hibernate.annotations.NamedNativeQuery}, and {@link org.hibernate.annotations.NamedNativeQueries} annotations to
 	 * the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex index
 	 */
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance query : jandex.getAnnotations( JPADotNames.NAMED_QUERY ) ) {
 			bindNamedQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( JPADotNames.NAMED_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValueAsArray( queries, "value" ) ) {
 				bindNamedQuery( metadata, query );
 			}
 		}
 		for ( AnnotationInstance query : jandex.getAnnotations( JPADotNames.NAMED_NATIVE_QUERY ) ) {
 			bindNamedNativeQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( JPADotNames.NAMED_NATIVE_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValueAsArray( queries, "value" ) ) {
 				bindNamedNativeQuery( metadata, query );
 			}
 		}
 		for ( AnnotationInstance query : jandex.getAnnotations( HibernateDotNames.NAMED_QUERY ) ) {
 			bindNamedQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( HibernateDotNames.NAMED_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValueAsArray( queries, "value" ) ) {
 				bindNamedQuery( metadata, query );
 			}
 		}
 		for ( AnnotationInstance query : jandex.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY ) ) {
 			bindNamedNativeQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValueAsArray( queries, "value" ) ) {
 				bindNamedNativeQuery( metadata, query );
 			}
 		}
 	}
 
 	private static void bindNamedQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValueAsString( annotation, "name" );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		String query = JandexHelper.getValueAsString( annotation, "query" );
 		AnnotationInstance[] hints = JandexHelper.getValueAsArray( annotation, "hints" );
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 		metadata.addNamedQuery(
-				name,
 				new NamedQueryDefinition(
+						name,
 						query, getBoolean( hints, QueryHints.CACHEABLE, name ), cacheRegion,
 						timeout, fetchSize, getFlushMode( hints, QueryHints.FLUSH_MODE, name ),
 						getCacheMode( hints, QueryHints.CACHE_MODE, name ),
 						getBoolean( hints, QueryHints.READ_ONLY, name ), comment, null
 				)
 		);
 		LOG.debugf( "Binding named query: %s => %s", name, query );
 	}
 
 	private static void bindNamedNativeQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValueAsString( annotation, "name" );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named native query must have a name when used in class or package level" );
 		}
 		String query = JandexHelper.getValueAsString( annotation, "query" );
 		String resultSetMapping = JandexHelper.getValueAsString( annotation, "resultSetMapping" );
 		AnnotationInstance[] hints = JandexHelper.getValueAsArray( annotation, "hints" );
 		boolean cacheable = getBoolean( hints, "org.hibernate.cacheable", name );
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 		FlushMode flushMode = getFlushMode( hints, QueryHints.FLUSH_MODE, name );
 		CacheMode cacheMode = getCacheMode( hints, QueryHints.CACHE_MODE, name );
 		boolean readOnly = getBoolean( hints, QueryHints.READ_ONLY, name );
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 		boolean callable = getBoolean( hints, QueryHints.CALLABLE, name );
 		NamedSQLQueryDefinition def;
 		if ( StringHelper.isNotEmpty( resultSetMapping ) ) {
 			def = new NamedSQLQueryDefinition(
+					name,
 					query, resultSetMapping, null, cacheable,
 					cacheRegion, timeout, fetchSize,
 					flushMode, cacheMode, readOnly, comment,
 					null, callable
 			);
 		}
 		else {
 			String resultClass = JandexHelper.getValueAsString( annotation, "resultClass" );
 			if ( void.class.equals( resultClass ) ) {
 				throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 			}
 			def = new NamedSQLQueryDefinition(
+					name,
 					query, new NativeSQLQueryRootReturn[] {
 					new NativeSQLQueryRootReturn(
 							"alias1",
 							resultClass,
 							new HashMap(),
 							LockMode.READ
 					)
 			},
 					null, cacheable, cacheRegion, timeout, fetchSize, flushMode, cacheMode, readOnly,
 					comment, null, callable
 			);
 
 		}
-		metadata.addNamedNativeQuery( name, def );
+		metadata.addNamedNativeQuery( def );
 		LOG.debugf( "Binding named native query: %s => %s", name, query );
 	}
 
 	private static boolean getBoolean(AnnotationInstance[] hints,
 									  String element,
 									  String query) {
 		String val = getString( hints, element );
 		if ( val == null || val.equalsIgnoreCase( "false" ) ) {
 			return false;
 		}
 		if ( val.equalsIgnoreCase( "true" ) ) {
 			return true;
 		}
 		throw new AnnotationException( "Not a boolean in hint: " + query + ":" + element );
 	}
 
 	private static CacheMode getCacheMode(AnnotationInstance[] hints,
 										  String element,
 										  String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 			return CacheMode.GET;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 			return CacheMode.IGNORE;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 			return CacheMode.NORMAL;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 			return CacheMode.PUT;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 			return CacheMode.REFRESH;
 		}
 		throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + element );
 	}
 
 	private static FlushMode getFlushMode(AnnotationInstance[] hints,
 										  String element,
 										  String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else {
 			throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + element );
 		}
 
 	}
 
 	private static Integer getInteger(AnnotationInstance[] hints,
 									  String element,
 									  String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		try {
 			return Integer.decode( val );
 		}
 		catch ( NumberFormatException nfe ) {
 			throw new AnnotationException( "Not an integer in hint: " + query + ":" + element, nfe );
 		}
 	}
 
 	private static String getString(AnnotationInstance[] hints,
 									String element) {
 		for ( AnnotationInstance hint : hints ) {
 			if ( element.equals( JandexHelper.getValue( hint, "name" ) ) ) {
 				return JandexHelper.getValueAsString( hint, "value" );
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(AnnotationInstance[] hints,
 									  String query) {
 		Integer timeout = getInteger( hints, QueryHints.TIMEOUT_JPA, query );
 		if ( timeout == null ) {
 			return getInteger( hints, QueryHints.TIMEOUT_HIBERNATE, query ); // timeout is already in seconds
 		}
 		return new Integer( (int) Math.round( timeout.doubleValue() / 1000.0 ) ); // convert milliseconds to seconds
 	}
 
 	private QueryBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 4ed83c11f3..29cf6993bf 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,475 +1,526 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.MetaAttribute;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.hbm.HbmBinder;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 	private ClassLoaderService classLoaderService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database = new Database();
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
-	private Map<String, String> imports;
+	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
+	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
 	// todo : keep as part of Database?
 	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final Binder[] binders;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			binders = new Binder[] {
 					new HbmBinder( this ),
 					new AnnotationBinder( this )
 			};
 		}
 		else {
 			binders = new Binder[] {
 					new AnnotationBinder( this ),
 					new HbmBinder( this )
 			};
 		}
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( binders, metadataSources );
 		bindIndependentMetadata( binders, metadataSources );
 		bindTypeDependentMetadata( binders, metadataSources );
 		bindMappingMetadata( binders, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( binders, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 	}
 
 	private void prepare(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
+		if ( profile == null || profile.getName() == null ) {
+			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
+		}
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
+		if ( def == null || def.getFilterName() == null ) {
+			throw new IllegalArgumentException( "Filter definition object or name is null: "  + def );
+		}
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
+		if ( generator == null || generator.getName() == null ) {
+			throw new IllegalArgumentException( "ID generator object or name is null." );
+		}
 		idGenerators.put( generator.getName(), generator );
 	}
 
 	@Override
 	public IdGenerator getIdGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGenerators.get( name );
 	}
 	@Override
 	public void registerIdentifierGenerator(String name, String generatorClassName) {
 		 identifierGeneratorFactory.register( name, classLoaderService().classForName( generatorClassName ) );
 	}
 
 	@Override
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
+		if ( auxiliaryDatabaseObject == null ) {
+			throw new IllegalArgumentException( "Auxiliary database object is null." );
+		}
 		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 	}
 
 	@Override
-	public void addNamedNativeQuery(String name, NamedSQLQueryDefinition def) {
-		namedNativeQueryDefs.put( name, def );
+	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
+		if ( def == null || def.getName() == null ) {
+			throw new IllegalArgumentException( "Named native query definition object or name is null: " + def.getQueryString() );
+		}
+		namedNativeQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
-	public void addNamedQuery(String name, NamedQueryDefinition def) {
-		namedQueryDefs.put( name, def );
+	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
+		return namedNativeQueryDefs.values();
+	}
+
+	@Override
+	public void addNamedQuery(NamedQueryDefinition def) {
+		if ( def == null || def.getName() == null ) {
+			throw new IllegalArgumentException( "Named query definition object or name is null: " + def.getQueryString() );
+		}
+		namedQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
+	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
+		return namedQueryDefs.values();
+	}
+
+	@Override
+	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
+		if ( resultSetMappingDefinition == null || resultSetMappingDefinition.getName() == null ) {
+			throw new IllegalArgumentException( "Resultset mappping object or name is null: " + resultSetMappingDefinition );
+		}
+		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
+	}
+
+	@Override
+	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
+		return resultSetMappings.values();
+	}
+
+	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
+		if ( typeDef == null || typeDef.getName() == null ) {
+			throw new IllegalArgumentException( "Type definition object or name is null: " + typeDef.getTypeClass() );
+		}
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService(){
 		if(classLoaderService==null){
 			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
 		if ( rootEntityBinding == null ) {
 			EntityBinding entityBinding = entityBindingMap.get( entityName );
 			if ( entityBinding == null ) {
 				throw new IllegalStateException( "Unknown entity binding: " + entityName );
 			}
 			if ( entityBinding.isRoot() ) {
 				rootEntityBinding = entityBinding;
 			}
 			else {
 				if ( entityBinding.getEntity().getSuperType() == null ) {
 					throw new IllegalStateException( "Entity binding has no root: " + entityName );
 				}
 				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
 			}
 			rootEntityBindingMap.put( entityName, rootEntityBinding );
 		}
 		return rootEntityBinding;
 	}
 
 	public Iterable<EntityBinding> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	public void addEntity(EntityBinding entityBinding) {
 		final String entityName = entityBinding.getEntity().getName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, entityBinding );
 	}
 
 	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
-	public Iterable<PluralAttributeBinding> getCollections() {
+	@Override
+	public Iterable<PluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getEntityBinding().getEntity().getName();
 		final String attributeName = pluralAttributeBinding.getAttribute().getName();
 		final String collectionRole = owningEntityName + '.' + attributeName;
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, pluralAttributeBinding );
 	}
 
 	public void addImport(String importName, String entityName) {
-		if ( imports == null ) {
-			imports = new HashMap<String, String>();
+		if ( importName == null || entityName == null ) {
+			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
+	public Iterable<Map.Entry<String, String>> getImports() {
+		return imports.entrySet();
+	}
+
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		return sessionFactoryBuilder;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
 	@Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return globalMetaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return this;
 	}
 
 	private static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	private static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	private static final String DEFAULT_CASCADE = "none";
 	private static final String DEFAULT_PROPERTY_ACCESS = "property";
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getExplicitType();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		AttributeBinding idBinding = entityBinding.getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
 	}
 
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getDefaultSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getDefaultCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getDefaultIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDefaultDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDefaultCascade() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getDefaultAccess() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean isDefaultLazy() {
 			return true;
 		}
 
 		@Override
 		public Map<String, MetaAttribute> getMappingMetas() {
 			return Collections.emptyMap();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
index 884a2400a9..0ab0f2619e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
@@ -1,73 +1,76 @@
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
 package org.hibernate.metamodel.source.spi;
 
+import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
 	public BasicServiceRegistry getServiceRegistry();
 
 	public Database getDatabase();
 
 	public TypeResolver getTypeResolver();
 
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
 	public void addCollection(PluralAttributeBinding collectionBinding);
 
 	public void addFetchProfile(FetchProfile profile);
 
 	public void addTypeDefinition(TypeDef typeDef);
 
 	public void addFilterDefinition(FilterDefinition filterDefinition);
 
 	public void addIdGenerator(IdGenerator generator);
 
 	public void registerIdentifierGenerator(String name, String clazz);
 
-	public void addNamedNativeQuery(String name, NamedSQLQueryDefinition def);
+	public void addNamedNativeQuery(NamedSQLQueryDefinition def);
 
-	public void addNamedQuery(String name, NamedQueryDefinition def);
+	public void addNamedQuery(NamedQueryDefinition def);
+
+	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition);
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index a02bada59e..82f6c457f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1756 +1,1826 @@
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
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
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
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.FilterHelper;
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
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
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
 				lazyNumbers.add( i );
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
 
+
+	public AbstractEntityPersister(
+			final EntityBinding entityBinding,
+			final EntityRegionAccessStrategy cacheAccessStrategy,
+			final SessionFactoryImplementor factory) throws HibernateException {
+		// TODO: Implement! Initializing final fields to make compiler happy
+		this.factory = factory;
+		this.cacheAccessStrategy = cacheAccessStrategy;
+		isLazyPropertiesCacheable = false;
+		cacheEntryStructure = null;
+		entityMetamodel = null;
+		rootTableKeyColumnNames = null;
+		rootTableKeyColumnReaders = null;
+		rootTableKeyColumnReaderTemplates = null;
+		identifierAliases = null;
+		identifierColumnSpan = -1;
+		versionColumnName = null;
+		hasFormulaProperties = false;
+		batchSize = -1;
+		hasSubselectLoadableCollections = false;
+		rowIdName = null;
+		lazyProperties = null;
+		sqlWhereString = null;
+		sqlWhereStringTemplate = null;
+		propertyColumnSpans = null;
+		propertySubclassNames = null;
+		propertyColumnAliases = null;
+		propertyColumnNames = null;
+		propertyColumnFormulaTemplates = null;
+		propertyColumnReaderTemplates = null;
+		propertyColumnWriters = null;
+		propertyColumnUpdateable = null;
+		propertyColumnInsertable = null;
+		propertyUniqueness = null;
+		propertySelectable = null;
+		lazyPropertyNames = null;
+		lazyPropertyNumbers = null;
+		lazyPropertyTypes = null;
+		lazyPropertyColumnAliases = null;
+		subclassPropertyNameClosure = null;
+		subclassPropertySubclassNameClosure = null;
+		subclassPropertyTypeClosure = null;
+		subclassPropertyFormulaTemplateClosure = null;
+		subclassPropertyColumnNameClosure = null;
+		subclassPropertyColumnReaderClosure = null;
+		subclassPropertyColumnReaderTemplateClosure = null;
+		subclassPropertyFetchModeClosure = null;
+		subclassPropertyNullabilityClosure = null;
+		propertyDefinedOnSubclass = null;
+		subclassPropertyColumnNumberClosure = null;
+		subclassPropertyFormulaNumberClosure = null;
+		subclassPropertyCascadeStyleClosure = null;
+		subclassColumnClosure = null;
+		subclassColumnLazyClosure = null;
+		subclassColumnAliasClosure = null;
+		subclassColumnSelectableClosure = null;
+		subclassColumnReaderTemplateClosure = null;
+		subclassFormulaClosure = null;
+		subclassFormulaTemplateClosure = null;
+		subclassFormulaAliasClosure = null;
+		subclassFormulaLazyClosure = null;
+		filterHelper = null;
+		loaderName = null;
+		queryLoader = null;
+		temporaryIdTableName = null;
+		temporaryIdTableDDL = null;
+		propertyMapping = null;
+	}
+
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
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
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
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting current persistent state for: "
                                             + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
 	}
 
 	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
 					// ValueInclusion.PARTIAL would indicate parts of a component need to
 					// be included in the select; currently we then just render the entire
 					// component into the select clause in that case.
 					public boolean includeProperty(int propertyNumber) {
 						return inclusions[propertyNumber] != ValueInclusion.NONE;
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = st.executeUpdate();
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting version: " + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	protected String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuffer buf = new StringBuffer().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	protected String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 
 	//This is really ugly, but necessary:
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index 17793e33bc..9b367a5cf4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,781 +1,819 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
  * mapping strategy
  *
  * @author Gavin King
  */
 public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int tableSpan;
 	private final String[] tableNames;
 	private final String[] naturalOrderTableNames;
 	private final String[][] tableKeyColumns;
 	private final String[][] tableKeyColumnReaders;
 	private final String[][] tableKeyColumnReaderTemplates;
 	private final String[][] naturalOrderTableKeyColumns;
 	private final String[][] naturalOrderTableKeyColumnReaders;
 	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
 	private final boolean[] naturalOrderCascadeDeleteEnabled;
 
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] naturalOrderPropertyTableNumbers;
 	private final int[] propertyTableNumbers;
 
 	// the closure of all properties in the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	private final boolean[] subclassTableSequentialSelect;
 	private final boolean[] subclassTableIsLazyClosure;
 	
 	// subclass discrimination works by assigning particular
 	// values to certain combinations of null primary key
 	// values in the outer join using an SQL CASE
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final String[] discriminatorValues;
 	private final String[] notNullColumnNames;
 	private final int[] notNullColumnTableNumbers;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	private final String discriminatorSQLString;
 
 	//INITIALIZATION:
 
 	public JoinedSubclassEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// DISCRIMINATOR
 
 		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			try {
 				discriminatorValue = new Integer( persistentClass.getSubclassId() );
 				discriminatorSQLString = discriminatorValue.toString();
 			}
 			catch (Exception e) {
 				throw new MappingException("Could not format discriminator value to SQL string", e );
 			}
 		}
 		else {
 			discriminatorValue = null;
 			discriminatorSQLString = null;
 		}
 
 		if ( optimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
 		}
 
 		//MULTITABLES
 
 		final int idColumnSpan = getIdentifierColumnSpan();
 
 		ArrayList tables = new ArrayList();
 		ArrayList keyColumns = new ArrayList();
 		ArrayList keyColumnReaders = new ArrayList();
 		ArrayList keyColumnReaderTemplates = new ArrayList();
 		ArrayList cascadeDeletes = new ArrayList();
 		Iterator titer = persistentClass.getTableClosureIterator();
 		Iterator kiter = persistentClass.getKeyClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			KeyValue key = (KeyValue) kiter.next();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add(tabname);
 			String[] keyCols = new String[idColumnSpan];
 			String[] keyColReaders = new String[idColumnSpan];
 			String[] keyColReaderTemplates = new String[idColumnSpan];
 			Iterator citer = key.getColumnIterator();
 			for ( int k=0; k<idColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add(keyCols);
 			keyColumnReaders.add(keyColReaders);
 			keyColumnReaderTemplates.add(keyColReaderTemplates);
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 		
 		//Span of the tables directly mapped by this entity and super-classes, if any
 		int coreTableSpan = tables.size();
 		
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			
 			Table tab = join.getTable();
 			 
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add(tabname);
 			
 			KeyValue key = join.getKey();
 			int joinIdColumnSpan = 	key.getColumnSpan();		
 			
 			String[] keyCols = new String[joinIdColumnSpan];
 			String[] keyColReaders = new String[joinIdColumnSpan];
 			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
 						
 			Iterator citer = key.getColumnIterator();
 			
 			for ( int k=0; k<joinIdColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add(keyCols);
 			keyColumnReaders.add(keyColReaders);
 			keyColumnReaderTemplates.add(keyColReaderTemplates);
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 		
 		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
 		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray(keyColumns);
 		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray(keyColumnReaders);
 		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray(keyColumnReaderTemplates);
 		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray(cascadeDeletes);
 
 		ArrayList subtables = new ArrayList();
 		ArrayList isConcretes = new ArrayList();
 		ArrayList isDeferreds = new ArrayList();
 		ArrayList isLazies = new ArrayList();
 		
 		keyColumns = new ArrayList();
 		titer = persistentClass.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassTable(tab) );
 			isDeferreds.add(Boolean.FALSE);
 			isLazies.add(Boolean.FALSE);
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add(tabname);
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k=0; k<idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add(key);
 		}
 		
 		//Add joins
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			
 			Table tab = join.getTable();
 			 
 			isConcretes.add( persistentClass.isClassOrSuperclassTable(tab) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isLazies.add(join.isLazy());
 			
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add(tabname);
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k=0; k<idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add(key);
 						
 		}
 				
 		String [] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray(subtables);
 		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray(keyColumns);
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		
 		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
 		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
 		int currentPosition = 0;
 		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0 ; i--, currentPosition++ ) {
 			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
 			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
 		} 
 
 		/**
 		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
 		 * For the Client entity:
 		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
 		 * added to the meta-data when the annotated entities are processed.
 		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
 		 * the first table as it will the driving table.
 		 * tableNames -> CLIENT, PERSON
 		 */
 				
 		tableSpan = naturalOrderTableNames.length;
  		tableNames = reverse(naturalOrderTableNames, coreTableSpan);
 		tableKeyColumns = reverse(naturalOrderTableKeyColumns, coreTableSpan);
 		tableKeyColumnReaders = reverse(naturalOrderTableKeyColumnReaders, coreTableSpan);
 		tableKeyColumnReaderTemplates = reverse(naturalOrderTableKeyColumnReaderTemplates, coreTableSpan);
 		subclassTableNameClosure = reverse(naturalOrderSubclassTableNameClosure, coreTableSpan);
 		subclassTableKeyColumnClosure = reverse(naturalOrderSubclassTableKeyColumnClosure, coreTableSpan);
  
 		spaces = ArrayHelper.join(
 				tableNames,
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 
 		// Custom sql
 		customSQLInsert = new String[tableSpan];
 		customSQLUpdate = new String[tableSpan];
 		customSQLDelete = new String[tableSpan];
 		insertCallable = new boolean[tableSpan];
 		updateCallable = new boolean[tableSpan];
 		deleteCallable = new boolean[tableSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 
 		PersistentClass pc = persistentClass;
 		int jk = coreTableSpan-1;
 		while (pc!=null) {
 			customSQLInsert[jk] = pc.getCustomSQLInsert();
 			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
 			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault(
 					customSQLInsert[jk], insertCallable[jk]
 			)
 		                                  : pc.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
 			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
 			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
 		                                  : pc.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[jk] = pc.getCustomSQLDelete();
 			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
 			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
 		                                  : pc.getCustomSQLDeleteCheckStyle();
 			jk--;
 			pc = pc.getSuperclass();
 		}
 		
 		if ( jk != -1 ) {
 			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
 		}
  
 		joinIter = persistentClass.getJoinClosureIterator();
 		int j = coreTableSpan;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			
 			customSQLInsert[j] = join.getCustomSQLInsert();
 			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
 			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
 		                                  : join.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[j] = join.getCustomSQLUpdate();
 			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
 			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
 		                                  : join.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[j] = join.getCustomSQLDelete();
 			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
 			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
 		                                  : join.getCustomSQLDeleteCheckStyle();
 			j++;
 		}
 		
 		// PROPERTIES
 		int hydrateSpan = getPropertySpan();
 		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
 		propertyTableNumbers = new int[hydrateSpan];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i=0;
 		while( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			String tabname = prop.getValue().getTable().getQualifiedName(
 				factory.getDialect(),
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 			);
 			propertyTableNumbers[i] = getTableId(tabname, tableNames);
 			naturalOrderPropertyTableNumbers[i] = getTableId(tabname, naturalOrderTableNames);
 			i++;
 		}
 
 		// subclass closure properties
 
 		//TODO: code duplication with SingleTableEntityPersister
 
 		ArrayList columnTableNumbers = new ArrayList();
 		ArrayList formulaTableNumbers = new ArrayList();
 		ArrayList propTableNumbers = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Table tab = prop.getValue().getTable();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			Integer tabnum = new Integer( getTableId(tabname, subclassTableNameClosure) );
   			propTableNumbers.add(tabnum);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaTableNumbers.add(tabnum);
 				}
 				else {
 					columnTableNumbers.add(tabnum);
 				}
 			}
 
 		}
 
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnTableNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTableNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaTableNumbers);
 
 		// SUBCLASSES
  
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[subclassSpan-1] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 			discriminatorValues = new String[subclassSpan];
 			discriminatorValues[subclassSpan-1] = discriminatorSQLString;
 			notNullColumnTableNumbers = new int[subclassSpan];
 			final int id = getTableId(
 				persistentClass.getTable().getQualifiedName(
 						factory.getDialect(),
 						factory.getSettings().getDefaultCatalogName(),
 						factory.getSettings().getDefaultSchemaName()
 				),
 				subclassTableNameClosure
 			);
 			notNullColumnTableNumbers[subclassSpan-1] = id;
 			notNullColumnNames = new String[subclassSpan];
 			notNullColumnNames[subclassSpan-1] =  subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 		}
 		else {
 			discriminatorValues = null;
 			notNullColumnTableNumbers = null;
 			notNullColumnNames = null;
 		}
 
 		iter = persistentClass.getSubclassIterator();
 		int k=0;
 		while ( iter.hasNext() ) {
 			Subclass sc = (Subclass) iter.next();
 			subclassClosure[k] = sc.getEntityName();
 			try {
 				if ( persistentClass.isPolymorphic() ) {
 					// we now use subclass ids that are consistent across all
 					// persisters for a class hierarchy, so that the use of
 					// "foo.class = Bar" works in HQL
 					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
 					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
 					discriminatorValues[k] = subclassId.toString();
 					int id = getTableId(
 						sc.getTable().getQualifiedName(
 								factory.getDialect(),
 								factory.getSettings().getDefaultCatalogName(),
 								factory.getSettings().getDefaultSchemaName()
 						),
 						subclassTableNameClosure
 					);
 					notNullColumnTableNumbers[k] = id;
 					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException("Error parsing discriminator value", e );
 			}
 			k++;
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 
 		postConstruct(mapping);
 
 	}
 
+	public JoinedSubclassEntityPersister(
+			final EntityBinding entityBinding,
+			final EntityRegionAccessStrategy cacheAccessStrategy,
+			final SessionFactoryImplementor factory,
+			final Mapping mapping) throws HibernateException {
+		super( entityBinding, cacheAccessStrategy, factory );
+		// TODO: implement!!! initializing final fields to null to make compiler happy
+		tableSpan = -1;
+		tableNames = null;
+		naturalOrderTableNames = null;
+		tableKeyColumns = null;
+		tableKeyColumnReaders = null;
+		tableKeyColumnReaderTemplates = null;
+		naturalOrderTableKeyColumns = null;
+		naturalOrderTableKeyColumnReaders = null;
+		naturalOrderTableKeyColumnReaderTemplates = null;
+		naturalOrderCascadeDeleteEnabled = null;
+		spaces = null;
+		subclassClosure = null;
+		subclassTableNameClosure = null;
+		subclassTableKeyColumnClosure= null;
+		isClassOrSuperclassTable = null;
+		naturalOrderPropertyTableNumbers = null;
+		propertyTableNumbers = null;
+		subclassPropertyTableNumberClosure = null;
+		subclassColumnTableNumberClosure = null;
+		subclassFormulaTableNumberClosure = null;
+		subclassTableSequentialSelect = null;
+		subclassTableIsLazyClosure = null;
+		discriminatorValues = null;
+		notNullColumnNames = null;
+		notNullColumnTableNumbers = null;
+		constraintOrderedTableNames = null;
+		constraintOrderedKeyColumnNames = null;
+		discriminatorSQLString = null;
+	}
+
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 	
 	
 	/*public void postInstantiate() throws MappingException {
 		super.postInstantiate();
 		//TODO: other lock modes?
 		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
 	}*/
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLString;
 	}
 
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassesByDiscriminatorValue.get(value);
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces; // don't need subclass tables, because they can't appear in conditions
 	}
 
 
 	protected String getTableName(int j) {
 		return naturalOrderTableNames[j];
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return naturalOrderTableKeyColumns[j];
 	}
 
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return naturalOrderCascadeDeleteEnabled[j];
 	}
 
 	protected boolean isPropertyOfTable(int property, int j) {
 		return naturalOrderPropertyTableNumbers[property]==j;
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException {
 
 		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
 
 		final UniqueEntityLoader loader = hasQueryLoader() ?
 				getQueryLoader() :
 				this.loader;
 		try {
 
 			final Object result = loader.load(id, optionalObject, session);
 
 			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
 		}
 	}*/
 
 	private static final void reverse(Object[] objects, int len) {
 		Object[] temp = new Object[len];
 		for (int i=0; i<len; i++) {
 			temp[i] = objects[len-i-1];
 		}
 		for (int i=0; i<len; i++) {
 			objects[i] = temp[i];
 		}
 	}
 
 	
 	/**
 	 * Reverse the first n elements of the incoming array
 	 * @param objects
 	 * @param n
 	 * @return New array with the first n elements in reversed order 
 	 */
 	private static final String[] reverse(String [] objects, int n) {
 		
 		int size = objects.length;
 		String[] temp = new String[size];
 		
 		for (int i=0; i<n; i++) {
 			temp[i] = objects[n-i-1];
 		}
 		
 		for (int i=n; i < size; i++) {
 			temp[i] =  objects[i];
 		}
 		
 		return temp;
 	}
 		
 	/**
 	 * Reverse the first n elements of the incoming array
 	 * @param objects
 	 * @param n
 	 * @return New array with the first n elements in reversed order 
 	 */
 	private static final String[][] reverse(String[][] objects, int n) {
 		int size = objects.length;
 		String[][] temp = new String[size][];
 		for (int i=0; i<n; i++) {
 			temp[i] = objects[n-i-1];
 		}
 		
 		for (int i=n; i<size; i++) {
 			temp[i] = objects[i];
 		}
 		
 		return temp;
 	}
 	
 	
 	
 	public String fromTableFragment(String alias) {
 		return getTableName() + ' ' + alias;
 	}
 
 	public String getTableName() {
 		return tableNames[0];
 	}
 
 	private static int getTableId(String tableName, String[] tables) {
 		for ( int j=0; j<tables.length; j++ ) {
 			if ( tableName.equals( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure("Table " + tableName + " not found");
 	}
 
 	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( hasSubclasses() ) {
 			select.setExtraSelectList( discriminatorFragment(name), getDiscriminatorAlias() );
 		}
 	}
 
 	private CaseFragment discriminatorFragment(String alias) {
 		CaseFragment cases = getFactory().getDialect().createCaseFragment();
 
 		for ( int i=0; i<discriminatorValues.length; i++ ) {
 			cases.addWhenColumnNotNull(
 				generateTableAlias( alias, notNullColumnTableNumbers[i] ),
 				notNullColumnNames[i],
 				discriminatorValues[i]
 			);
 		}
 
 		return cases;
 	}
 
 	public String filterFragment(String alias) {
 		return hasWhere() ?
 			" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
 			"";
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return generateTableAlias( rootAlias, tableSpan-1 );
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return tableKeyColumns[0];
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return tableKeyColumnReaderTemplates[0];
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return tableKeyColumnReaders[0];
 	}		
 	
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 
 		if ( ENTITY_CLASS.equals(propertyName) ) {
 			// This doesn't actually seem to work but it *might*
 			// work on some dbs. Also it doesn't work if there
 			// are multiple columns of results because it
 			// is not accounting for the suffix:
 			// return new String[] { getDiscriminatorColumnName() };
 
 			return new String[] { discriminatorFragment(alias).toFragmentString() };
 		}
 		else {
 			return super.toColumns(alias, propertyName);
 		}
 
 	}
 
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return tableSpan;
 	}
 
 	public boolean isMultiTable() {
 		return true;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return naturalOrderPropertyTableNumbers;
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 	
 	
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			return null;
 		}
 		return tableNames[ propertyTableNumbers[ index.intValue() ] ];
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	public String getRootTableName() {
 		return naturalOrderTableNames[0];
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		if ( "class".equals( propertyPath ) ) {
 			// special case where we need to force incloude all subclass joins
 			return Declarer.SUBCLASS;
 		}
 		return super.getSubclassPropertyDeclarer( propertyPath );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
index fc6501a6b7..8585b5d718 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
@@ -1,750 +1,794 @@
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
 package org.hibernate.persister.entity;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.Value;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.InFragment;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.Type;
 
 /**
  * The default implementation of the <tt>EntityPersister</tt> interface.
  * Implements the "table-per-class-hierarchy" or "roll-up" mapping strategy
  * for an entity class and its inheritence hierarchy.  This is implemented
  * as a single table holding all classes in the hierarchy with a discrimator
  * column used to determine which concrete class is referenced.
  *
  * @author Gavin King
  */
 public class SingleTableEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int joinSpan;
 	private final String[] qualifiedTableNames;
 	private final boolean[] isInverseTable;
 	private final boolean[] isNullableTable;
 	private final String[][] keyColumnNames;
 	private final boolean[] cascadeDeleteEnabled;
 	private final boolean hasSequentialSelects;
 	
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final boolean[] subclassTableIsLazyClosure;
 	private final boolean[] isInverseSubclassTable;
 	private final boolean[] isNullableSubclassTable;
 	private final boolean[] subclassTableSequentialSelect;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] propertyTableNumbers;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	// discriminator column
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final boolean forceDiscriminator;
 	private final String discriminatorColumnName;
 	private final String discriminatorColumnReaders;
 	private final String discriminatorColumnReaderTemplate;
 	private final String discriminatorFormula;
 	private final String discriminatorFormulaTemplate;
 	private final String discriminatorAlias;
 	private final Type discriminatorType;
 	private final String discriminatorSQLValue;
 	private final boolean discriminatorInsertable;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//private final Map propertyTableNumbersByName = new HashMap();
 	private final Map propertyTableNumbersByNameAndSubclass = new HashMap();
 	
 	private final Map sequentialSelectStringsByEntityName = new HashMap();
 
 	private static final Object NULL_DISCRIMINATOR = new MarkerObject("<null discriminator>");
 	private static final Object NOT_NULL_DISCRIMINATOR = new MarkerObject("<not null discriminator>");
 
 	//INITIALIZATION:
 
 	public SingleTableEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// CLASS + TABLE
 
 		joinSpan = persistentClass.getJoinClosureSpan()+1;
 		qualifiedTableNames = new String[joinSpan];
 		isInverseTable = new boolean[joinSpan];
 		isNullableTable = new boolean[joinSpan];
 		keyColumnNames = new String[joinSpan][];
 		final Table table = persistentClass.getRootTable();
 		qualifiedTableNames[0] = table.getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		isInverseTable[0] = false;
 		isNullableTable[0] = false;
 		keyColumnNames[0] = getIdentifierColumnNames();
 		cascadeDeleteEnabled = new boolean[joinSpan];
 
 		// Custom sql
 		customSQLInsert = new String[joinSpan];
 		customSQLUpdate = new String[joinSpan];
 		customSQLDelete = new String[joinSpan];
 		insertCallable = new boolean[joinSpan];
 		updateCallable = new boolean[joinSpan];
 		deleteCallable = new boolean[joinSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
 
 		customSQLInsert[0] = persistentClass.getCustomSQLInsert();
 		insertCallable[0] = customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
 		insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[0], insertCallable[0] )
 									  : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
 		updateCallable[0] = customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
 		updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[0], updateCallable[0] )
 									  : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLDelete[0] = persistentClass.getCustomSQLDelete();
 		deleteCallable[0] = customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
 		deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null
 									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[0], deleteCallable[0] )
 									  : persistentClass.getCustomSQLDeleteCheckStyle();
 
 		// JOINS
 
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		int j = 1;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			qualifiedTableNames[j] = join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			);
 			isInverseTable[j] = join.isInverse();
 			isNullableTable[j] = join.isOptional();
 			cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() && 
 				factory.getDialect().supportsCascadeDelete();
 
 			customSQLInsert[j] = join.getCustomSQLInsert();
 			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
 			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
 		                                  : join.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[j] = join.getCustomSQLUpdate();
 			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
 			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
 		                                  : join.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[j] = join.getCustomSQLDelete();
 			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
 			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
 			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
 		                                  : join.getCustomSQLDeleteCheckStyle();
 
 			Iterator iter = join.getKey().getColumnIterator();
 			keyColumnNames[j] = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyColumnNames[j][i++] = col.getQuotedName( factory.getDialect() );
 			}
 
 			j++;
 		}
 
 		constraintOrderedTableNames = new String[qualifiedTableNames.length];
 		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
 		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
 			constraintOrderedTableNames[position] = qualifiedTableNames[i];
 			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
 		}
 
 		spaces = ArrayHelper.join(
 				qualifiedTableNames, 
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 		
 		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
 
 		boolean hasDeferred = false;
 		ArrayList subclassTables = new ArrayList();
 		ArrayList joinKeyColumns = new ArrayList();
 		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
 		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
 		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
 		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
 		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
 		subclassTables.add( qualifiedTableNames[0] );
 		joinKeyColumns.add( getIdentifierColumnNames() );
 		isConcretes.add(Boolean.TRUE);
 		isDeferreds.add(Boolean.FALSE);
 		isInverses.add(Boolean.FALSE);
 		isNullables.add(Boolean.FALSE);
 		isLazies.add(Boolean.FALSE);
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassJoin(join) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isInverses.add( join.isInverse() );
 			isNullables.add( join.isOptional() );
 			isLazies.add( lazyAvailable && join.isLazy() );
 			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join) ) hasDeferred = true;
 			subclassTables.add( join.getTable().getQualifiedName( 
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 			Iterator iter = join.getKey().getColumnIterator();
 			String[] keyCols = new String[ join.getKey().getColumnSpan() ];
 			int i = 0;
 			while ( iter.hasNext() ) {
 				Column col = (Column) iter.next();
 				keyCols[i++] = col.getQuotedName( factory.getDialect() );
 			}
 			joinKeyColumns.add(keyCols);
 		}
 		
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
 		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
 		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
 		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
 		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
 		hasSequentialSelects = hasDeferred;
 
 		// DISCRIMINATOR
 
 		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			Value discrimValue = persistentClass.getDiscriminator();
 			if (discrimValue==null) {
 				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
 			}
 			forceDiscriminator = persistentClass.isForceDiscriminator();
 			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
 			if ( discrimValue.hasFormula() ) {
 				Formula formula = (Formula) selectable;
 				discriminatorFormula = formula.getFormula();
 				discriminatorFormulaTemplate = formula.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorColumnName = null;
 				discriminatorColumnReaders = null;
 				discriminatorColumnReaderTemplate = null;
 				discriminatorAlias = "clazz_";
 			}
 			else {
 				Column column = (Column) selectable;
 				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
 				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
 				discriminatorColumnReaderTemplate = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 				discriminatorFormula = null;
 				discriminatorFormulaTemplate = null;
 			}
 			discriminatorType = persistentClass.getDiscriminator().getType();
 			if ( persistentClass.isDiscriminatorValueNull() ) {
 				discriminatorValue = NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NULL;
 				discriminatorInsertable = false;
 			}
 			else if ( persistentClass.isDiscriminatorValueNotNull() ) {
 				discriminatorValue = NOT_NULL_DISCRIMINATOR;
 				discriminatorSQLValue = InFragment.NOT_NULL;
 				discriminatorInsertable = false;
 			}
 			else {
 				discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
 				try {
 					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 					discriminatorValue = dtype.stringToObject( persistentClass.getDiscriminatorValue() );
 					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException("Could not format discriminator value to SQL string", e);
 				}
 			}
 		}
 		else {
 			forceDiscriminator = false;
 			discriminatorInsertable = false;
 			discriminatorColumnName = null;
 			discriminatorColumnReaders = null;
 			discriminatorColumnReaderTemplate = null;
 			discriminatorAlias = null;
 			discriminatorType = null;
 			discriminatorValue = null;
 			discriminatorSQLValue = null;
 			discriminatorFormula = null;
 			discriminatorFormulaTemplate = null;
 		}
 
 		// PROPERTIES
 
 		propertyTableNumbers = new int[ getPropertySpan() ];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i=0;
 		while( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			propertyTableNumbers[i++] = persistentClass.getJoinNumber(prop);
 
 		}
 
 		//TODO: code duplication with JoinedSubclassEntityPersister
 		
 		ArrayList columnJoinNumbers = new ArrayList();
 		ArrayList formulaJoinedNumbers = new ArrayList();
 		ArrayList propertyJoinNumbers = new ArrayList();
 		
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Integer join = persistentClass.getJoinNumber(prop);
 			propertyJoinNumbers.add(join);
 
 			//propertyTableNumbersByName.put( prop.getName(), join );
 			propertyTableNumbersByNameAndSubclass.put( 
 					prop.getPersistentClass().getEntityName() + '.' + prop.getName(), 
 					join 
 			);
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaJoinedNumbers.add(join);
 				}
 				else {
 					columnJoinNumbers.add(join);
 				}
 			}
 		}
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 		}
 
 		// SUBCLASSES
 		if ( persistentClass.isPolymorphic() ) {
 			iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				if ( sc.isDiscriminatorValueNull() ) {
 					subclassesByDiscriminatorValue.put( NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else if ( sc.isDiscriminatorValueNotNull() ) {
 					subclassesByDiscriminatorValue.put( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
 				}
 				else {
 					try {
 						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
 						subclassesByDiscriminatorValue.put(
 							dtype.stringToObject( sc.getDiscriminatorValue() ),
 							sc.getEntityName()
 						);
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException("Error parsing discriminator value", e);
 					}
 				}
 			}
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
+	public SingleTableEntityPersister(
+			final EntityBinding entityBinding,
+			final EntityRegionAccessStrategy cacheAccessStrategy,
+			final SessionFactoryImplementor factory,
+			final Mapping mapping) throws HibernateException {
+
+		super( entityBinding, cacheAccessStrategy, factory );
+
+		//TODO: implement!!!! initializing final fields to make compiler happy...
+		joinSpan = -1;
+		qualifiedTableNames = null;
+		isInverseTable = null;
+		isNullableTable = null;
+		keyColumnNames = null;
+		cascadeDeleteEnabled = null;
+		hasSequentialSelects = false;
+		spaces = null;
+		subclassClosure = null;
+		subclassTableNameClosure = null;
+		subclassTableIsLazyClosure = null;
+		isInverseSubclassTable = null;
+		isNullableSubclassTable = null;
+		subclassTableSequentialSelect = null;
+		subclassTableKeyColumnClosure = null;
+		isClassOrSuperclassTable = null;
+		propertyTableNumbers = null;
+		subclassPropertyTableNumberClosure = null;
+		subclassColumnTableNumberClosure = null;
+		subclassFormulaTableNumberClosure = null;
+		forceDiscriminator = false;
+		discriminatorColumnName = null;
+		discriminatorColumnReaders = null;
+		discriminatorColumnReaderTemplate = null;
+		discriminatorFormula = null;
+		discriminatorFormulaTemplate = null;
+		discriminatorAlias = null;
+		discriminatorType = null;
+		discriminatorSQLValue = null;
+		discriminatorInsertable = false;
+		constraintOrderedTableNames = null;
+		constraintOrderedKeyColumnNames = null;
+	}
+
 	protected boolean isInverseTable(int j) {
 		return isInverseTable[j];
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return isInverseSubclassTable[j];
 	}
 
 	public String getDiscriminatorColumnName() {
 		return discriminatorColumnName;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return discriminatorColumnReaders;
 	}			
 	
 	public String getDiscriminatorColumnReaderTemplate() {
 		return discriminatorColumnReaderTemplate;
 	}	
 	
 	protected String getDiscriminatorAlias() {
 		return discriminatorAlias;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return discriminatorFormulaTemplate;
 	}
 
 	public String getTableName() {
 		return qualifiedTableNames[0];
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		if (value==null) {
 			return (String) subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
 		}
 		else {
 			String result = (String) subclassesByDiscriminatorValue.get(value);
 			if (result==null) result = (String) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
 			return result;
 		}
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	//Access cached SQL
 
 	protected boolean isDiscriminatorFormula() {
 		return discriminatorColumnName==null;
 	}
 
 	protected String getDiscriminatorFormula() {
 		return discriminatorFormula;
 	}
 
 	protected String getTableName(int j) {
 		return qualifiedTableNames[j];
 	}
 	
 	protected String[] getKeyColumns(int j) {
 		return keyColumnNames[j];
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return cascadeDeleteEnabled[j];
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return propertyTableNumbers[property]==j;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 	
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' ' + name;
 	}
 
 	public String filterFragment(String alias) throws MappingException {
 		String result = discriminatorFilterFragment(alias);
 		if ( hasWhere() ) result += " and " + getSQLWhereString(alias);
 		return result;
 	}
 	
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return forceDiscriminator ?
 			discriminatorFilterFragment(alias) :
 			"";
 	}
 
 	private String discriminatorFilterFragment(String alias) throws MappingException {
 		if ( needsDiscriminator() ) {
 			InFragment frag = new InFragment();
 
 			if ( isDiscriminatorFormula() ) {
 				frag.setFormula( alias, getDiscriminatorFormulaTemplate() );
 			}
 			else {
 				frag.setColumn( alias, getDiscriminatorColumnName() );
 			}
 
 			String[] subclasses = getSubclassClosure();
 			for ( int i=0; i<subclasses.length; i++ ) {
 				final Queryable queryable = (Queryable) getFactory().getEntityPersister( subclasses[i] );
 				if ( !queryable.isAbstract() ) frag.addValue( queryable.getDiscriminatorSQLValue() );
 			}
 
 			StringBuffer buf = new StringBuffer(50)
 				.append(" and ")
 				.append( frag.toFragmentString() );
 
 			return buf.toString();
 		}
 		else {
 			return "";
 		}
 	}
 
 	private boolean needsDiscriminator() {
 		return forceDiscriminator || isInherited();
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( isDiscriminatorFormula() ) {
 			select.addFormula( name, getDiscriminatorFormulaTemplate(), getDiscriminatorAlias() );
 		}
 		else {
 			select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 		}
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return joinSpan;
 	}
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 
 		if (discriminatorInsertable) {
 			insert.addColumn( getDiscriminatorColumnName(), discriminatorSQLValue );
 		}
 
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return propertyTableNumbers;
 	}
 		
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return hasSequentialSelects && 
 			isSubclassTableSequentialSelect( getSubclassPropertyTableNumber(propertyName, entityName) );
 	}
 	
 	public boolean hasSequentialSelect() {
 		return hasSequentialSelects;
 	}
 	
 	private int getSubclassPropertyTableNumber(String propertyName, String entityName) {
 		Type type = propertyMapping.toType(propertyName);
 		if ( type.isAssociationType() && ( (AssociationType) type ).useLHSPrimaryKey() ) return 0;
 		final Integer tabnum = (Integer) propertyTableNumbersByNameAndSubclass.get(entityName + '.' + propertyName);
 		return tabnum==null ? 0 : tabnum.intValue();
 	}
 	
 	protected String getSequentialSelect(String entityName) {
 		return (String) sequentialSelectStringsByEntityName.get(entityName);
 	}
 
 	private String generateSequentialSelect(Loadable persister) {
 		//if ( this==persister || !hasSequentialSelects ) return null;
 
 		//note that this method could easily be moved up to BasicEntityPersister,
 		//if we ever needed to reuse it from other subclasses
 		
 		//figure out which tables need to be fetched
 		AbstractEntityPersister subclassPersister = (AbstractEntityPersister) persister;
 		HashSet tableNumbers = new HashSet();
 		String[] props = subclassPersister.getPropertyNames();
 		String[] classes = subclassPersister.getPropertySubclassNames();
 		for ( int i=0; i<props.length; i++ ) {
 			int propTableNumber = getSubclassPropertyTableNumber( props[i], classes[i] );
 			if ( isSubclassTableSequentialSelect(propTableNumber) && !isSubclassTableLazy(propTableNumber) ) {
 				tableNumbers.add( propTableNumber);
 			}
 		}
 		if ( tableNumbers.isEmpty() ) return null;
 		
 		//figure out which columns are needed
 		ArrayList columnNumbers = new ArrayList();
 		final int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassColumnClosure().length; i++ ) {
 			if ( tableNumbers.contains( columnTableNumbers[i] ) ) {
 				columnNumbers.add( i );
 			}
 		}
 		
 		//figure out which formulas are needed
 		ArrayList formulaNumbers = new ArrayList();
 		final int[] formulaTableNumbers = getSubclassColumnTableNumberClosure();
 		for ( int i=0; i<getSubclassFormulaTemplateClosure().length; i++ ) {
 			if ( tableNumbers.contains( formulaTableNumbers[i] ) ) {
 				formulaNumbers.add( i );
 			}
 		}
 		
 		//render the SQL
 		return renderSelect( 
 			ArrayHelper.toIntArray(tableNumbers),
 			ArrayHelper.toIntArray(columnNumbers),
 			ArrayHelper.toIntArray(formulaNumbers)
 		);
 	}
 		
 		
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 	
 	protected boolean isNullableTable(int j) {
 		return isNullableTable[j];
 	}
 	
 	protected boolean isNullableSubclassTable(int j) {
 		return isNullableSubclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull(propertyName);
 		if (index==null) return null;
 		return qualifiedTableNames[ propertyTableNumbers[ index.intValue() ] ];
 	}
 	
 	public void postInstantiate() {
 		super.postInstantiate();
 		if (hasSequentialSelects) {
 			String[] entityNames = getSubclassClosure();
 			for ( int i=1; i<entityNames.length; i++ ) {
 				Loadable loadable = (Loadable) getFactory().getEntityPersister( entityNames[i] );
 				if ( !loadable.isAbstract() ) { //perhaps not really necessary...
 					String sequentialSelect = generateSequentialSelect(loadable);
 					sequentialSelectStringsByEntityName.put( entityNames[i], sequentialSelect );
 				}
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return getTableSpan() > 1;
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
index b041286f0a..d9347ddcf4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/UnionSubclassEntityPersister.java
@@ -1,482 +1,500 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the "table-per-concrete-class" or "roll-down" mapping 
  * strategy for an entity and its inheritence hierarchy.
  *
  * @author Gavin King
  */
 public class UnionSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final String subquery;
 	private final String tableName;
 	//private final String rootTableName;
 	private final String[] subclassClosure;
 	private final String[] spaces;
 	private final String[] subclassSpaces;
 	private final String discriminatorSQLValue;
 	private final Map subclassByDiscriminatorValue = new HashMap();
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	//INITIALIZATION:
 
 	public UnionSubclassEntityPersister(
 			final PersistentClass persistentClass, 
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 		
 		if ( getIdentifierGenerator() instanceof IdentityGenerator ) {
 			throw new MappingException(
 					"Cannot use identity column key generation with <union-subclass> mapping for: " + 
 					getEntityName() 
 			);
 		}
 
 		// TABLE
 
 		tableName = persistentClass.getTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getSettings().getDefaultCatalogName(), 
 				factory.getSettings().getDefaultSchemaName() 
 		);
 		/*rootTableName = persistentClass.getRootTable().getQualifiedName( 
 				factory.getDialect(), 
 				factory.getDefaultCatalog(), 
 				factory.getDefaultSchema() 
 		);*/
 
 		//Custom SQL
 
 		String sql;
 		boolean callable = false;
 		ExecuteUpdateResultCheckStyle checkStyle = null;
 		sql = persistentClass.getCustomSQLInsert();
 		callable = sql != null && persistentClass.isCustomInsertCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLInsertCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLInsertCheckStyle();
 		customSQLInsert = new String[] { sql };
 		insertCallable = new boolean[] { callable };
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLUpdate();
 		callable = sql != null && persistentClass.isCustomUpdateCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLUpdateCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLUpdateCheckStyle();
 		customSQLUpdate = new String[] { sql };
 		updateCallable = new boolean[] { callable };
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		sql = persistentClass.getCustomSQLDelete();
 		callable = sql != null && persistentClass.isCustomDeleteCallable();
 		checkStyle = sql == null
 				? ExecuteUpdateResultCheckStyle.COUNT
 	            : persistentClass.getCustomSQLDeleteCheckStyle() == null
 						? ExecuteUpdateResultCheckStyle.determineDefault( sql, callable )
 	                    : persistentClass.getCustomSQLDeleteCheckStyle();
 		customSQLDelete = new String[] { sql };
 		deleteCallable = new boolean[] { callable };
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[] { checkStyle };
 
 		discriminatorSQLValue = String.valueOf( persistentClass.getSubclassId() );
 
 		// PROPERTIES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[0] = getEntityName();
 
 		// SUBCLASSES
 		subclassByDiscriminatorValue.put( 
 				new Integer( persistentClass.getSubclassId() ), 
 				persistentClass.getEntityName() 
 		);
 		if ( persistentClass.isPolymorphic() ) {
 			Iterator iter = persistentClass.getSubclassIterator();
 			int k=1;
 			while ( iter.hasNext() ) {
 				Subclass sc = (Subclass) iter.next();
 				subclassClosure[k++] = sc.getEntityName();
 				subclassByDiscriminatorValue.put( new Integer( sc.getSubclassId() ), sc.getEntityName() );
 			}
 		}
 		
 		//SPACES
 		//TODO: i'm not sure, but perhaps we should exclude
 		//      abstract denormalized tables?
 		
 		int spacesSize = 1 + persistentClass.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = tableName;
 		Iterator iter = persistentClass.getSynchronizedTables().iterator();
 		for ( int i=1; i<spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 		
 		HashSet subclassTables = new HashSet();
 		iter = persistentClass.getSubclassTableClosureIterator();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			subclassTables.add( table.getQualifiedName(
 					factory.getDialect(), 
 					factory.getSettings().getDefaultCatalogName(), 
 					factory.getSettings().getDefaultSchemaName() 
 			) );
 		}
 		subclassSpaces = ArrayHelper.toStringArray(subclassTables);
 
 		subquery = generateSubquery(persistentClass, mapping);
 
 		if ( isMultiTable() ) {
 			int idColumnSpan = getIdentifierColumnSpan();
 			ArrayList tableNames = new ArrayList();
 			ArrayList keyColumns = new ArrayList();
 			if ( !isAbstract() ) {
 				tableNames.add( tableName );
 				keyColumns.add( getIdentifierColumnNames() );
 			}
 			iter = persistentClass.getSubclassTableClosureIterator();
 			while ( iter.hasNext() ) {
 				Table tab = ( Table ) iter.next();
 				if ( !tab.isAbstractUnionTable() ) {
 					String tableName = tab.getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					);
 					tableNames.add( tableName );
 					String[] key = new String[idColumnSpan];
 					Iterator citer = tab.getPrimaryKey().getColumnIterator();
 					for ( int k=0; k<idColumnSpan; k++ ) {
 						key[k] = ( ( Column ) citer.next() ).getQuotedName( factory.getDialect() );
 					}
 					keyColumns.add( key );
 				}
 			}
 
 			constraintOrderedTableNames = ArrayHelper.toStringArray( tableNames );
 			constraintOrderedKeyColumnNames = ArrayHelper.to2DStringArray( keyColumns );
 		}
 		else {
 			constraintOrderedTableNames = new String[] { tableName };
 			constraintOrderedKeyColumnNames = new String[][] { getIdentifierColumnNames() };
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap(persistentClass);
 		
 		postConstruct(mapping);
 
 	}
 
+	public UnionSubclassEntityPersister(
+			final EntityBinding entityBinding,
+			final EntityRegionAccessStrategy cacheAccessStrategy,
+			final SessionFactoryImplementor factory,
+			final Mapping mapping) throws HibernateException {
+		super(entityBinding, cacheAccessStrategy, factory );
+		// TODO: implement!!! initializing final fields to null to make compiler happy.
+		subquery = null;
+		tableName = null;
+		subclassClosure = null;
+		spaces = null;
+		subclassSpaces = null;
+		discriminatorSQLValue = null;
+		constraintOrderedTableNames = null;
+		constraintOrderedKeyColumnNames = null;
+	}
+
 	public Serializable[] getQuerySpaces() {
 		return subclassSpaces;
 	}
 	
 	public String getTableName() {
 		return subquery;
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLValue;
 	}
 
 	public String[] getSubclassClosure() {
 		return subclassClosure;
 	}
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassByDiscriminatorValue.get(value);
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces;
 	}
 
 	protected boolean isDiscriminatorFormula() {
 		return false;
 	}
 
 	/**
 	 * Generate the SQL that selects a row by id
 	 */
 	protected String generateSelectString(LockMode lockMode) {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 			.setLockMode(lockMode)
 			.setTableName( getTableName() )
 			.addColumns( getIdentifierColumnNames() )
 			.addColumns( 
 					getSubclassColumnClosure(), 
 					getSubclassColumnAliasClosure(),
 					getSubclassColumnLazyiness()
 			)
 			.addColumns( 
 					getSubclassFormulaClosure(), 
 					getSubclassFormulaAliasClosure(),
 					getSubclassFormulaLazyiness()
 			);
 		//TODO: include the rowids!!!!
 		if ( hasSubclasses() ) {
 			if ( isDiscriminatorFormula() ) {
 				select.addColumn( getDiscriminatorFormula(), getDiscriminatorAlias() );
 			}
 			else {
 				select.addColumn( getDiscriminatorColumnName(), getDiscriminatorAlias() );
 			}
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "load " + getEntityName() );
 		}
 		return select.addCondition( getIdentifierColumnNames(), "=?" ).toStatementString();
 	}
 
 	protected String getDiscriminatorFormula() {
 		return null;
 	}
 
 	protected String getTableName(int j) {
 		return tableName;
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return getIdentifierColumnNames();
 	}
 	
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return false;
 	}
 	
 	protected boolean isPropertyOfTable(int property, int j) {
 		return true;
 	}
 
 	// Execute the SQL:
 
 	public String fromTableFragment(String name) {
 		return getTableName() + ' '  + name;
 	}
 
 	public String filterFragment(String name) {
 		return hasWhere() ?
 			" and " + getSQLWhereString(name) :
 			"";
 	}
 
 	public String getSubclassPropertyTableName(int i) {
 		return getTableName();//ie. the subquery! yuck!
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		select.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
 	}
 	
 	protected int[] getPropertyTableNumbersInSelect() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return 0;
 	}
 
 	public int getSubclassPropertyTableNumber(String propertyName) {
 		return 0;
 	}
 
 	public boolean isMultiTable() {
 		// This could also just be true all the time...
 		return isAbstract() || hasSubclasses();
 	}
 
 	public int getTableSpan() {
 		return 1;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return new int[ getSubclassColumnClosure().length ];
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return new int[ getSubclassFormulaClosure().length ];
 	}
 
 	protected boolean[] getTableHasColumns() {
 		return new boolean[] { true };
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return new int[ getPropertySpan() ];
 	}
 
 	protected String generateSubquery(PersistentClass model, Mapping mapping) {
 
 		Dialect dialect = getFactory().getDialect();
 		Settings settings = getFactory().getSettings();
 		
 		if ( !model.hasSubclasses() ) {
 			return model.getTable().getQualifiedName(
 					dialect,
 					settings.getDefaultCatalogName(),
 					settings.getDefaultSchemaName()
 				);
 		}
 
 		HashSet columns = new LinkedHashSet();
 		Iterator titer = model.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table table = (Table) titer.next();
 			if ( !table.isAbstractUnionTable() ) {
 				Iterator citer = table.getColumnIterator();
 				while ( citer.hasNext() ) columns.add( citer.next() );
 			}
 		}
 
 		StringBuffer buf = new StringBuffer()
 			.append("( ");
 
 		Iterator siter = new JoinedIterator(
 			new SingletonIterator(model),
 			model.getSubclassIterator()
 		);
 
 		while ( siter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) siter.next();
 			Table table = clazz.getTable();
 			if ( !table.isAbstractUnionTable() ) {
 				//TODO: move to .sql package!!
 				buf.append("select ");
 				Iterator citer = columns.iterator();
 				while ( citer.hasNext() ) {
 					Column col = (Column) citer.next();
 					if ( !table.containsColumn(col) ) {
 						int sqlType = col.getSqlTypeCode(mapping);
 						buf.append( dialect.getSelectClauseNullString(sqlType) )
 							.append(" as ");
 					}
 					buf.append( col.getName() );
 					buf.append(", ");
 				}
 				buf.append( clazz.getSubclassId() )
 					.append(" as clazz_");
 				buf.append(" from ")
 					.append( table.getQualifiedName(
 							dialect,
 							settings.getDefaultCatalogName(),
 							settings.getDefaultSchemaName()
 					) );
 				buf.append(" union ");
 				if ( dialect.supportsUnionAll() ) {
 					buf.append("all ");
 				}
 			}
 		}
 		
 		if ( buf.length() > 2 ) {
 			//chop the last union (all)
 			buf.setLength( buf.length() - ( dialect.supportsUnionAll() ? 11 : 7 ) );
 		}
 
 		return buf.append(" )").toString();
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return getIdentifierColumnNames();
 	}
 
 	public String getSubclassTableName(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return tableName;
 	}
 
 	public int getSubclassTableSpan() {
 		return 1;
 	}
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		if (j!=0) throw new AssertionFailure("only one table");
 		return true;
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		//TODO: check this....
 		return getTableName();
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 			return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index 271c78856c..fd81d4962b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,248 +1,249 @@
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
 package org.hibernate.persister.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 /**
  * The standard Hibernate {@link PersisterFactory} implementation
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class PersisterFactoryImpl implements PersisterFactory, ServiceRegistryAwareService {
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			PersistentClass.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations using
 	 * an {@link EntityBinding}.
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW to ENTITY_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			EntityBinding.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			Collection.class,
 			CollectionRegionAccessStrategy.class,
 			Configuration.class,
 			SessionFactoryImplementor.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations using
 	 * a {@link PluralAttributeBinding}
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW to COLLECTION_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			PluralAttributeBinding.class,
 			CollectionRegionAccessStrategy.class,
-			Configuration.class,
+			MetadataImplementor.class,
 			SessionFactoryImplementor.class
 	};
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(
 			PersistentClass metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(EntityBinding metadata,
 												 EntityRegionAccessStrategy cacheAccessStrategy,
 												 SessionFactoryImplementor factory,
 												 Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	// TODO: change metadata arg type to EntityBinding when new metadata is integrated
 	private static EntityPersister create(
 			Class<? extends EntityPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException {
 		try {
 			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, factory, cfg );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
-			Collection metadata,
+			Collection collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
-		Class<? extends CollectionPersister> persisterClass = metadata.getCollectionPersisterClass();
+		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
-			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( metadata );
+			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
-		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS, cfg, metadata, cacheAccessStrategy, factory );
+		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS, cfg, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
-
-	public CollectionPersister createCollectionPersister(Configuration cfg,
-														 PluralAttributeBinding metadata,
+	public CollectionPersister createCollectionPersister(MetadataImplementor metadata,
+														 PluralAttributeBinding collectionMetadata,
 														 CollectionRegionAccessStrategy cacheAccessStrategy,
 														 SessionFactoryImplementor factory) throws HibernateException {
-		Class<? extends CollectionPersister> persisterClass = metadata.getCollectionPersisterClass();
+		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
-			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( metadata );
+			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
-		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, cfg, metadata, cacheAccessStrategy, factory );
+		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
-	// TODO: change metadata arg type to PluralAttributeBinding when new metadata is integrated
+	// TODO: change collectionMetadata arg type to PluralAttributeBinding when new metadata is integrated
+	// TODO: change metadata arg type to MetadataImplementor when new metadata is integrated
 	private static CollectionPersister create(
 			Class<? extends CollectionPersister> persisterClass,
 			Class[] persisterConstructorArgs,
-			Configuration cfg,
-			Object metadata,
+			Object cfg,
+			Object collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		try {
 			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
-				return constructor.newInstance( metadata, cacheAccessStrategy, cfg, factory );
+				return constructor.newInstance( collectionMetadata, cacheAccessStrategy, cfg, factory );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
index 9c6ab9b647..e67fa86a1f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/StandardPersisterClassResolver.java
@@ -1,117 +1,121 @@
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
 
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.UnionSubclass;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.BasicCollectionPersister;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.OneToManyPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
 import org.hibernate.persister.entity.SingleTableEntityPersister;
 import org.hibernate.persister.entity.UnionSubclassEntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.UnknownPersisterException;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardPersisterClassResolver implements PersisterClassResolver {
 
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
+
+		if ( metadata.isRoot() ) {
+			return singleTableEntityPersister(); // EARLY RETURN!
+		}
 		switch ( metadata.getInheritanceType() ) {
 			case JOINED: {
-				joinedSubclassEntityPersister();
+				return joinedSubclassEntityPersister();
 			}
 			case SINGLE_TABLE: {
 				return singleTableEntityPersister();
 			}
 			case TABLE_PER_CLASS: {
 				return unionSubclassEntityPersister();
 			}
 			default: {
 				throw new UnknownPersisterException(
 						"Could not determine persister implementation for entity [" + metadata.getEntity().getName() + "]"
 				);
 			}
 
 		}
 	}
 
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		// todo : make sure this is based on an attribute kept on the metamodel in the new code, not the concrete PersistentClass impl found!
 		if ( RootClass.class.isInstance( metadata ) ) {
 			return singleTableEntityPersister();
 		}
 		else if ( JoinedSubclass.class.isInstance( metadata ) ) {
 			return joinedSubclassEntityPersister();
 		}
 		else if ( UnionSubclass.class.isInstance( metadata ) ) {
 			return unionSubclassEntityPersister();
 		}
 		else {
 			throw new UnknownPersisterException(
 					"Could not determine persister implementation for entity [" + metadata.getEntityName() + "]"
 			);
 		}
 	}
 
 	public Class<? extends EntityPersister> singleTableEntityPersister() {
 		return SingleTableEntityPersister.class;
 	}
 
 	public Class<? extends EntityPersister> joinedSubclassEntityPersister() {
 		return JoinedSubclassEntityPersister.class;
 	}
 
 	public Class<? extends EntityPersister> unionSubclassEntityPersister() {
 		return UnionSubclassEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return metadata.isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return metadata.getCollectionElement().isOneToMany() ? oneToManyPersister() : basicCollectionPersister();
 	}
 
 	private Class<OneToManyPersister> oneToManyPersister() {
 		return OneToManyPersister.class;
 	}
 
 	private Class<BasicCollectionPersister> basicCollectionPersister() {
 		return BasicCollectionPersister.class;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index b8cf709380..7cc75f8c32 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
@@ -1,128 +1,129 @@
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
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
+import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.Service;
 
 /**
  * Contract for creating persister instances (both {@link EntityPersister} and {@link CollectionPersister} varieties).
  *
  * @author Steve Ebersole
  */
 public interface PersisterFactory extends Service {
 
 	// TODO: is it really necessary to provide Configuration to CollectionPersisters ?
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
 			EntityBinding model,
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
 
 	/**
 	 * Create a collection persister instance.
 	 *
-	 * @param cfg The configuration
+	 * @param metadata The metadata
 	 * @param model The O/R mapping metamodel definition for the collection
 	 * @param cacheAccessStrategy The caching strategy for this collection
 	 * @param factory The session factory
 	 *
 	 * @return An appropriate collection persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public CollectionPersister createCollectionPersister(
-			Configuration cfg,
+			MetadataImplementor metadata,
 			PluralAttributeBinding model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
index 8135c53c6c..75c5ec0ef1 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
@@ -1,194 +1,195 @@
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
 import java.util.Iterator;
 
 import org.junit.Test;
 
 import org.hibernate.CallbackException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.Interceptor;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.Type;
 
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class SessionFactoryBuilderImplTest extends BaseUnitTestCase {
 
 	@Test
 	public void testGettingSessionFactoryBuilder() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		assertNotNull( sessionFactoryBuilder );
 		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
 	}
 
 	@Test
 	public void testBuildSessionFactoryWithDefaultOptions() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
 		assertSame( EmptyInterceptor.INSTANCE, sessionFactory.getSessionFactoryOptions().getInterceptor() );
 		assertTrue( EntityNotFoundDelegate.class.isInstance(
 				sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate()
 		) );
 	}
 
 	@Test
 	public void testBuildSessionFactoryWithUpdatedOptions() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		Interceptor interceptor = new AnInterceptor();
 		EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
 			@Override
 			public void handleEntityNotFound(String entityName, Serializable id) {
 				throw new ObjectNotFoundException( id, entityName );
 			}
 		};
 		sessionFactoryBuilder.with( interceptor );
 		sessionFactoryBuilder.with( entityNotFoundDelegate );
 		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
 		assertSame( interceptor, sessionFactory.getSessionFactoryOptions().getInterceptor() );
 		assertSame( entityNotFoundDelegate, sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate() );
 	}
 
 	private SessionFactoryBuilder getSessionFactoryBuilder() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
+		sources.addAnnotatedClass( SimpleEntity.class );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		return  metadata.getSessionFactoryBuilder();
 	}
 
 	private static class AnInterceptor implements Interceptor {
 		private static final Interceptor INSTANCE = EmptyInterceptor.INSTANCE;
 
 		@Override
 		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onLoad( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onFlushDirty( entity, id, currentState, previousState, propertyNames, types );
 		}
 
 		@Override
 		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onSave( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			INSTANCE.onDelete( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionRecreate( collection, key );
 		}
 
 		@Override
 		public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionRemove( collection, key );
 		}
 
 		@Override
 		public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionUpdate( collection, key );
 		}
 
 		@Override
 		public void preFlush(Iterator entities) throws CallbackException {
 			INSTANCE.preFlush( entities );
 		}
 
 		@Override
 		public void postFlush(Iterator entities) throws CallbackException {
 			INSTANCE.postFlush( entities );
 		}
 
 		@Override
 		public Boolean isTransient(Object entity) {
 			return INSTANCE.isTransient( entity );
 		}
 
 		@Override
 		public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
 			return INSTANCE.findDirty( entity, id, currentState, previousState, propertyNames, types );
 		}
 
 		@Override
 		public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
 				throws CallbackException {
 			return INSTANCE.instantiate( entityName, entityMode, id );
 		}
 
 		@Override
 		public String getEntityName(Object object) throws CallbackException {
 			return INSTANCE.getEntityName( object );
 		}
 
 		@Override
 		public Object getEntity(String entityName, Serializable id) throws CallbackException {
 			return INSTANCE.getEntity( entityName, id );
 		}
 
 		@Override
 		public void afterTransactionBegin(Transaction tx) {
 			INSTANCE.afterTransactionBegin( tx );
 		}
 
 		@Override
 		public void beforeTransactionCompletion(Transaction tx) {
 			INSTANCE.beforeTransactionCompletion( tx );
 		}
 
 		@Override
 		public void afterTransactionCompletion(Transaction tx) {
 			INSTANCE.afterTransactionCompletion( tx );
 		}
 
 		@Override
 		public String onPrepareStatement(String sql) {
 			return INSTANCE.onPrepareStatement( sql );
 		}
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SimpleEntity.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SimpleEntity.java
new file mode 100644
index 0000000000..c6f1c5c763
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SimpleEntity.java
@@ -0,0 +1,60 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.metamodel.source.internal;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+
+/**
+ * @author Steve Ebersole
+ */
+@Entity
+public class SimpleEntity {
+	@Id
+	private Long id;
+	private String name;
+
+	public SimpleEntity() {
+	}
+
+	public SimpleEntity(String name) {
+		this.name = name;
+	}
+
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
