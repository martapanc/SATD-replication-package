diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
new file mode 100644
index 0000000000..69da3afb98
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
@@ -0,0 +1,325 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.internal;
+
+import java.io.Serializable;
+import java.util.Arrays;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.action.spi.AfterTransactionCompletionProcess;
+import org.hibernate.cache.spi.NaturalIdCacheKey;
+import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.event.spi.EventSource;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Maintains a {@link org.hibernate.engine.spi.PersistenceContext}-level 2-way cross-reference (xref) between the 
+ * identifiers and natural ids of entities associated with the PersistenceContext.  Additionally coordinates
+ * actions related to the shared caching of the entity's natural id. 
+ * 
+ * @author Steve Ebersole
+ */
+public class NaturalIdXrefDelegate {
+	private static final Logger LOG = Logger.getLogger( NaturalIdXrefDelegate.class );
+
+	private final StatefulPersistenceContext persistenceContext;
+	private final Map<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
+
+	public NaturalIdXrefDelegate(StatefulPersistenceContext persistenceContext) {
+		this.persistenceContext = persistenceContext;
+	}
+
+	protected SessionImplementor session() {
+		return persistenceContext.getSession();
+	}
+
+	public void cacheNaturalIdResolution(
+			EntityPersister persister, 
+			final Serializable pk,
+			Object[] naturalIdValues,
+			CachedNaturalIdValueSource valueSource) {
+		validateNaturalId( persister, naturalIdValues );
+
+		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+		if ( entityNaturalIdResolutionCache == null ) {
+			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
+			naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
+		}
+
+		final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
+		entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, cachedNaturalId );
+		entityNaturalIdResolutionCache.naturalIdToPkMap.put( cachedNaturalId, pk );
+
+		//If second-level caching is enabled cache the resolution there as well
+		if ( persister.hasNaturalIdCache() ) {
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
+
+			final SessionFactoryImplementor factory = session().getFactory();
+
+			switch ( valueSource ) {
+				case LOAD: {
+					final boolean put = naturalIdCacheAccessStrategy.putFromLoad(
+							naturalIdCacheKey,
+							pk,
+							session().getTimestamp(),
+							null
+					);
+
+					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+						factory.getStatisticsImplementor()
+								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
+					}
+
+					break;
+				}
+				case INSERT: {
+					naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, pk );
+
+					( (EventSource) session() ).getActionQueue().registerProcess(
+							new AfterTransactionCompletionProcess() {
+								@Override
+								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+									final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, pk );
+
+									if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+										factory.getStatisticsImplementor().naturalIdCachePut(
+												naturalIdCacheAccessStrategy.getRegion().getName() );
+									}
+								}
+							}
+					);
+
+					break;
+				}
+				case UPDATE: {
+					final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
+					naturalIdCacheAccessStrategy.update( naturalIdCacheKey, pk );
+
+					( (EventSource) session() ).getActionQueue().registerProcess(
+							new AfterTransactionCompletionProcess() {
+								@Override
+								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+									final boolean put = naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, pk, lock );
+
+									if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+										factory.getStatisticsImplementor().naturalIdCachePut(
+												naturalIdCacheAccessStrategy.getRegion().getName() );
+									}
+								}
+							}
+					);
+
+					break;
+				}
+			}
+		}
+	}
+
+	protected void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
+		if ( !persister.hasNaturalIdentifier() ) {
+			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
+		}
+		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
+			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
+		}
+	}
+
+	public void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] deletedNaturalIdValues) {
+		validateNaturalId( persister, deletedNaturalIdValues );
+
+		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+		Object[] sessionCachedNaturalIdValues = null;
+		if ( entityNaturalIdResolutionCache != null ) {
+			final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap
+					.remove( pk );
+			if ( cachedNaturalId != null ) {
+				entityNaturalIdResolutionCache.naturalIdToPkMap.remove( cachedNaturalId );
+				sessionCachedNaturalIdValues = cachedNaturalId.getValues();
+			}
+		}
+
+		if ( persister.hasNaturalIdCache() ) {
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister
+					.getNaturalIdCacheAccessStrategy();
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( deletedNaturalIdValues, persister, session() );
+			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
+
+			if ( sessionCachedNaturalIdValues != null
+					&& !Arrays.equals( sessionCachedNaturalIdValues, deletedNaturalIdValues ) ) {
+				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues, persister, session() );
+				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
+			}
+		}
+	}
+
+	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
+		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+		if ( entityNaturalIdResolutionCache == null ) {
+			return null;
+		}
+
+		final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
+		if ( cachedNaturalId == null ) {
+			return null;
+		}
+
+		return cachedNaturalId.getValues();
+	}
+
+	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
+		validateNaturalId( persister, naturalIdValues );
+
+		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+
+		Serializable pk;
+		final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
+		if ( entityNaturalIdResolutionCache != null ) {
+			pk = entityNaturalIdResolutionCache.naturalIdToPkMap.get( cachedNaturalId );
+
+			// Found in session cache
+			if ( pk != null ) {
+				if ( LOG.isTraceEnabled() ) {
+					LOG.trace(
+							"Resolved natural key -> primary key resolution in session cache: " +
+									persister.getRootEntityName() + "#[" +
+									Arrays.toString( naturalIdValues ) + "]"
+					);
+				}
+
+				return pk;
+			}
+		}
+
+		// Session cache miss, see if second-level caching is enabled
+		if ( !persister.hasNaturalIdCache() ) {
+			return null;
+		}
+
+		// Try resolution from second-level cache
+		final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
+
+		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+		pk = (Serializable) naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session().getTimestamp() );
+
+		// Found in second-level cache, store in session cache
+		final SessionFactoryImplementor factory = session().getFactory();
+		if ( pk != null ) {
+			if ( factory.getStatistics().isStatisticsEnabled() ) {
+				factory.getStatisticsImplementor().naturalIdCacheHit(
+						naturalIdCacheAccessStrategy.getRegion().getName()
+				);
+			}
+
+			if ( LOG.isTraceEnabled() ) {
+				// protected to avoid Arrays.toString call unless needed
+				LOG.tracef(
+						"Found natural key [%s] -> primary key [%s] xref in second-level cache for %s",
+						Arrays.toString( naturalIdValues ),
+						pk,
+						persister.getRootEntityName()
+				);
+			}
+
+			if ( entityNaturalIdResolutionCache == null ) {
+				entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
+				naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
+			}
+
+			entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, cachedNaturalId );
+			entityNaturalIdResolutionCache.naturalIdToPkMap.put( cachedNaturalId, pk );
+		}
+		else if ( factory.getStatistics().isStatisticsEnabled() ) {
+			factory.getStatisticsImplementor().naturalIdCacheMiss( naturalIdCacheAccessStrategy.getRegion().getName() );
+		}
+
+		return pk;
+	}
+
+
+	private static class CachedNaturalId {
+		private final EntityPersister persister;
+		private final Object[] values;
+		private int hashCode;
+
+		public CachedNaturalId(EntityPersister persister, Object[] values) {
+			this.persister = persister;
+			this.values = values;
+
+			final int prime = 31;
+			int result = 1;
+			result = prime * result + ( ( persister == null ) ? 0 : persister.hashCode() );
+			result = prime * result + Arrays.hashCode( values );
+			this.hashCode = result;
+		}
+
+		public Object[] getValues() {
+			return values;
+		}
+
+		@Override
+		public int hashCode() {
+			return this.hashCode;
+		}
+
+		@Override
+		public boolean equals(Object obj) {
+			if ( this == obj ) {
+				return true;
+			}
+			if ( obj == null ) {
+				return false;
+			}
+			if ( getClass() != obj.getClass() ) {
+				return false;
+			}
+
+			final CachedNaturalId other = (CachedNaturalId) obj;
+			return persister.equals( other.persister )
+					&& Arrays.equals( values, other.values );
+		}
+	}
+
+	private static class NaturalIdResolutionCache implements Serializable {
+		private final EntityPersister persister;
+
+		private NaturalIdResolutionCache(EntityPersister persister) {
+			this.persister = persister;
+		}
+
+		public EntityPersister getPersister() {
+			return persister;
+		}
+
+		private Map<Serializable, CachedNaturalId> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, CachedNaturalId>();
+		private Map<CachedNaturalId, Serializable> naturalIdToPkMap = new ConcurrentHashMap<CachedNaturalId, Serializable>();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index b871dd6c87..eb7254de9e 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,2026 +1,1785 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
-import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
-import java.util.concurrent.ConcurrentHashMap;
 
 import org.apache.commons.collections.map.AbstractReferenceMap;
 import org.apache.commons.collections.map.ReferenceMap;
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
-import org.hibernate.action.spi.AfterTransactionCompletionProcess;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
-import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
-import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
-import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
-import org.jboss.logging.Logger;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
  * </p>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatefulPersistenceContext.class.getName() );
 
 	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
 
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	// Identity map of EntityEntry instances, by the entity instance
 	private Map<Object,EntityEntry> entityEntries;
 
 	// Entity proxies, by EntityKey
 	private Map<EntityKey, Object> proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map<EntityKey, Object> entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map<Object, PersistentCollection> arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private IdentityMap<PersistentCollection, CollectionEntry> collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map<CollectionKey, PersistentCollection> collectionsByKey;
 
 	// Set of EntityKeys of deleted objects
 	private HashSet<EntityKey> nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet<AssociationKey> nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List<PersistentCollection> nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map<CollectionKey,PersistentCollection> unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map<Object,Object> parentsByChild;
 
 	private int cascading = 0;
 	private int loadCounter = 0;
 	private boolean flushing = false;
 
 	private boolean defaultReadOnly = false;
 	private boolean hasNonReadOnlyEntities = false;
 
 	private LoadContexts loadContexts;
 	private BatchFetchQueue batchFetchQueue;
 
 
 
 	/**
 	 * Constructs a PersistentContext, bound to the given session.
 	 *
 	 * @param session The session "owning" this context.
 	 */
 	public StatefulPersistenceContext(SessionImplementor session) {
 		this.session = session;
 
 		entitiesByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap<EntityUniqueKey, Object>( INIT_COLL_SIZE );
 		//noinspection unchecked
 		proxiesByKey = (Map<EntityKey, Object>) new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 
 		collectionsByKey = new HashMap<CollectionKey, PersistentCollection>( INIT_COLL_SIZE );
 		arrayHolders = new IdentityHashMap<Object, PersistentCollection>( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet<EntityKey>();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet<AssociationKey>( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList<PersistentCollection>( INIT_COLL_SIZE );
 	}
 
 	@Override
 	public boolean isStateless() {
 		return false;
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	@Override
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(8);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		if ( unownedCollections == null ) {
 			return null;
 		}
 		else {
 			return unownedCollections.remove(key);
 		}
 	}
 
 	@Override
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	@Override
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			final LazyInitializer li = ((HibernateProxy) o).getHibernateLazyInitializer();
 			li.unsetSession();
 		}
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
 		}
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntries.clear();
 		parentsByChild.clear();
 		entitySnapshotsByKey.clear();
 		collectionsByKey.clear();
 		collectionEntries.clear();
 		if ( unownedCollections != null ) {
 			unownedCollections.clear();
 		}
 		proxiesByKey.clear();
 		nullifiableEntityKeys.clear();
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.clear();
 		}
 		// defaultReadOnly is unaffected by clear()
 		hasNonReadOnlyEntities = false;
 		if ( loadContexts != null ) {
 			loadContexts.cleanup();
 		}
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	@Override
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	@Override
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus(status);
 		setHasNonReadOnlyEnties(status);
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		// Downgrade locks
 		for ( EntityEntry o : entityEntries.values() ) {
 			o.setLockMode( LockMode.NONE );
 		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 */
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
 		Object cached = entitySnapshotsByKey.get(key);
 		if (cached!=null) {
 			return cached==NO_ROW ? null : (Object[]) cached;
 		}
 		else {
 			Object[] snapshot = persister.getDatabaseSnapshot( id, session );
 			entitySnapshotsByKey.put( key, snapshot==null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	@Override
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// let's first see if it is part of the natural id cache...
 		final Object[] cachedValue = findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
 			cacheNaturalIdResolution( persister, id, dbValue, CachedNaturalIdValueSource.LOAD );
 			return dbValue;
 		}
 		else {
 			// for a mutable natural there is a likelihood that the the information will already be
 			// snapshot-cached.
 			final int[] props = persister.getNaturalIdentifierProperties();
 			final Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW ) {
 				return null;
 			}
 
 			final Object[] naturalIdSnapshotSubSet = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshotSubSet[i] = entitySnapshot[ props[i] ];
 			}
 			cacheNaturalIdResolution( persister, id, naturalIdSnapshotSubSet, CachedNaturalIdValueSource.LOAD );
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
 	/**
 	 * Retrieve the cached database snapshot for the requested entity key.
 	 * <p/>
 	 * This differs from {@link #getDatabaseSnapshot} is two important respects:<ol>
 	 * <li>no snapshot is obtained from the database if not already cached</li>
 	 * <li>an entry of {@link #NO_ROW} here is interpretet as an exception</li>
 	 * </ol>
 	 * @param key The entity key for which to retrieve the cached snapshot
 	 * @return The cached snapshot
 	 * @throws IllegalStateException if the cached snapshot was == {@link #NO_ROW}.
 	 */
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
 		}
 		return ( Object[] ) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put(key, entity);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 	}
 
 	/**
 	 * Get the entity instance associated with the given
 	 * <tt>EntityKey</tt>
 	 */
 	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get(key);
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
 	@Override
 	public Object removeEntity(EntityKey key) {
 		Object entity = entitiesByKey.remove(key);
 		Iterator iter = entitiesByUniqueKey.values().iterator();
 		while ( iter.hasNext() ) {
 			if ( iter.next()==entity ) iter.remove();
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
 		entitySnapshotsByKey.remove(key);
 		nullifiableEntityKeys.remove(key);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 		getBatchFetchQueue().removeSubselect(key);
 		return entity;
 	}
 
 	/**
 	 * Get an entity cached by unique key
 	 */
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get(euk);
 	}
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put(euk, entity);
 	}
 
 	/**
 	 * Retrieve the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntries.get(entity);
 	}
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntries.remove(entity);
 	}
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntries.containsKey(entity);
 	}
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get(coll);
 	}
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
 	@Override
 	public EntityEntry addEntity(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final EntityKey entityKey,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 		addEntity( entityKey, entity );
 		return addEntry(
 				entity,
 				status,
 				loadedState,
 				null,
 				entityKey.getIdentifier(),
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 		);
 	}
 
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it
 	 * to the event source's internal caches.
 	 */
 	@Override
 	public EntityEntry addEntry(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched) {
 
 		EntityEntry e = new EntityEntry(
 				status,
 				loadedState,
 				rowId,
 				id,
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				persister.getEntityMode(),
 				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched,
 				this
 		);
 		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties(status);
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey(collection);
 	}
 
 	@Override
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
 	/**
 	 * Takes the given object and, if it represents a proxy, reassociates it with this event source.
 	 *
 	 * @param value The possible proxy to be reassociated.
 	 * @return Whether the passed value represented an actual proxy which got initialized.
 	 * @throws MappingException
 	 */
 	@Override
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( !Hibernate.isInitialized(value) ) {
 			HibernateProxy proxy = (HibernateProxy) value;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy(li, proxy);
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * If a deleted entity instance is re-saved, and it has a proxy, we need to
 	 * reset the identifier of the proxy
 	 */
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
 			HibernateProxy proxy = (HibernateProxy) value;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			li.setIdentifier(id);
 			reassociateProxy(li, proxy);
 		}
 	}
 
 	/**
 	 * Associate a proxy that was instantiated by another session with this session
 	 *
 	 * @param li The proxy initializer.
 	 * @param proxy The proxy to reassociate.
 	 */
 	private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) {
 		if ( li.getSession() != this.getSession() ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
 			final EntityKey key = session.generateEntityKey( li.getIdentifier(), persister );
 		  	// any earlier proxy takes precedence
 			if ( !proxiesByKey.containsKey( key ) ) {
 				proxiesByKey.put( key, proxy );
 			}
 			proxy.getHibernateLazyInitializer().setSession( session );
 		}
 	}
 
 	/**
 	 * Get the entity instance underlying the given proxy, throwing
 	 * an exception if the proxy is uninitialized. If the given object
 	 * is not a proxy, simply return the argument.
 	 */
 	@Override
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
 						"object was an uninitialized proxy for " +
 						li.getEntityName()
 				);
 			}
 			return li.getImplementation(); //unwrap the object
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	/**
 	 * Possibly unproxy the given reference and reassociate it with the current session.
 	 *
 	 * @param maybeProxy The reference to be unproxied if it currently represents a proxy.
 	 * @return The unproxied instance.
 	 * @throws HibernateException
 	 */
 	@Override
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy(li, proxy);
 			return li.getImplementation(); //initialize + unwrap the object
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	/**
 	 * Attempts to check whether the given key represents an entity already loaded within the
 	 * current session.
 	 * @param object The entity reference against which to perform the uniqueness check.
 	 * @throws HibernateException
 	 */
 	@Override
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
 		Object entity = getEntity(key);
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
 	/**
 	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
 	 * and overwrite the registration of the old one. This breaks == and occurs only for
 	 * "class" proxies rather than "interface" proxies. Also init the proxy to point to
 	 * the given target implementation if necessary.
 	 *
 	 * @param proxy The proxy instance to be narrowed.
 	 * @param persister The persister for the proxied entity.
 	 * @param key The internal cache key for the proxied entity.
 	 * @param object (optional) the actual proxied entity instance.
 	 * @return An appropriately narrowed instance.
 	 * @throws HibernateException
 	 */
 	@Override
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			if ( object != null ) {
 				proxiesByKey.remove(key);
 				return object; //return the proxied object
 			}
 			else {
 				proxy = persister.createProxy( key.getIdentifier(), session );
 				Object proxyOrig = proxiesByKey.put(key, proxy); //overwrite old proxy
 				if ( proxyOrig != null ) {
 					if ( ! ( proxyOrig instanceof HibernateProxy ) ) {
 						throw new AssertionFailure(
 								"proxy not of type HibernateProxy; it is " + proxyOrig.getClass()
 						);
 					}
 					// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
 					boolean readOnlyOrig = ( ( HibernateProxy ) proxyOrig ).getHibernateLazyInitializer().isReadOnly();
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 				}
 				return proxy;
 			}
 		}
 		else {
 
 			if ( object != null ) {
 				LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 				li.setImplementation(object);
 			}
 
 			return proxy;
 
 		}
 
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * third argument (the entity associated with the key) if no proxy exists. Init
 	 * the proxy to the target implementation, if necessary.
 	 */
 	@Override
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
 	throws HibernateException {
 		if ( !persister.hasProxy() ) return impl;
 		Object proxy = proxiesByKey.get(key);
 		if ( proxy != null ) {
 			return narrowProxy(proxy, persister, key, impl);
 		}
 		else {
 			return impl;
 		}
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * argument (the entity associated with the key) if no proxy exists.
 	 * (slower than the form above)
 	 */
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		EntityEntry e = getEntry(impl);
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
 	@Override
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
 	/**
 	 * Get the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner, if its entity ID is available from the collection's loaded key
 	 * and the owner entity is in the persistence context; otherwise, returns null
 	 */
 	@Override
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
 		CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
 			return null; // early exit...
 		}
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
 		Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	@Override
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection) {
 		return getLoadedCollectionOwnerIdOrNull( getCollectionEntry( collection ) );
 	}
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param ce The collection entry
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	private Serializable getLoadedCollectionOwnerIdOrNull(CollectionEntry ce) {
 		if ( ce == null || ce.getLoadedKey() == null || ce.getLoadedPersister() == null ) {
 			return null;
 		}
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// get the ID from collection.getOwner()
 		return ce.getLoadedPersister().getCollectionType().getIdOfOwnerOrNull( ce.getLoadedKey(), session );
 	}
 
 	/**
 	 * add a collection we just loaded up (still needs initializing)
 	 */
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		addCollection(collection, ce, id);
 	}
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 	}
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
 	@Override
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 	throws HibernateException {
 		addCollection(collection, persister);
 	}
 
 	/**
 	 * Add an collection to the cache, with a given collection entry.
 	 *
 	 * @param coll The collection for which we are adding an entry.
 	 * @param entry The entry representing the collection.
 	 * @param key The key of the collection's entry.
 	 */
 	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
 		collectionEntries.put( coll, entry );
 		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		PersistentCollection old = collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
 				throw new AssertionFailure("bug adding collection twice");
 			}
 			// or should it actually throw an exception?
 			old.unsetSession( session );
 			collectionEntries.remove( old );
 			// watch out for a case where old is still referenced
 			// somewhere in the object graph! (which is a user error)
 		}
 	}
 
 	/**
 	 * Add a collection to the cache, creating a new collection entry for it
 	 *
 	 * @param collection The collection for which we are adding an entry.
 	 * @param persister The collection persister
 	 */
 	private void addCollection(PersistentCollection collection, CollectionPersister persister) {
 		CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
 	/**
 	 * add an (initialized) collection that was created by another session and passed
 	 * into update() (ie. one with a snapshot and existing state on the database)
 	 */
 	@Override
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
 	throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
 			CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
 	/**
 	 * add a collection we just pulled out of the cache (does not need initializing)
 	 */
 	@Override
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
 	throws HibernateException {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		ce.postInitialize(collection);
 		addCollection(collection, ce, id);
 		return ce;
 	}
 
 	/**
 	 * Get the collection instance associated with the <tt>CollectionKey</tt>
 	 */
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add(collection);
 	}
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
 			LOG.debug( "Initializing non-lazy collections" );
 			//do this work only at the very highest level of the load
 			loadCounter++; //don't let this method be called recursively
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					nonlazyCollections.remove( size - 1 ).forceInitialization();
 				}
 			}
 			finally {
 				loadCounter--;
 				clearNullProperties();
 			}
 		}
 	}
 
 
 	/**
 	 * Get the <tt>PersistentCollection</tt> object for an array
 	 */
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get(array);
 	}
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading
 	 * array, since the array instance is not created until endLoad().
 	 */
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove(array);
 	}
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry(coll).getSnapshot();
 	}
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
 	@Override
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
 			coll = getCollectionHolder(collection);
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
 				Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
 					PersistentCollection pc = wrappers.next();
 					if ( pc.isWrapper(collection) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
 		return (coll == null) ? null : getCollectionEntry(coll);
 	}
 
 	/**
 	 * Get an existing proxy by key
 	 */
 	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get(key);
 	}
 
 	/**
 	 * Add a proxy to the session cache
 	 */
 	@Override
 	public void addProxy(EntityKey key, Object proxy) {
 		proxiesByKey.put(key, proxy);
 	}
 
 	/**
 	 * Remove a proxy from the session cache.
 	 * <p/>
 	 * Additionally, ensure that any load optimization references
 	 * such as batch or subselect loading get cleaned up as well.
 	 *
 	 * @param key The key of the entity proxy to be removed
 	 * @return The proxy reference.
 	 */
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	/**
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	@Override
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	@Override
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return entityEntries;
 	}
 
 	@Override
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	@Override
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	@Override
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	@Override
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	@Override
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	@Override
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	@Override
 	public void setFlushing(boolean flushing) {
 		this.flushing = flushing;
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
 	@Override
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	@Override
 	public void afterLoad() {
 		loadCounter--;
 	}
 
 	@Override
 	public boolean isLoadFinished() {
 		return loadCounter == 0;
 	}
 
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	@Override
     public String toString() {
 		return new StringBuilder()
 				.append("PersistenceContext[entityKeys=")
 				.append(entitiesByKey.keySet())
 				.append(",collectionKeys=")
 				.append(collectionsByKey.keySet())
 				.append("]")
 				.toString();
 	}
 
 	/**
 	 * Search <tt>this</tt> persistence context for an associated entity instance which is considered the "owner" of
 	 * the given <tt>childEntity</tt>, and return that owner's id value.  This is performed in the scenario of a
 	 * uni-directional, non-inverse one-to-many collection (which means that the collection elements do not maintain
 	 * a direct reference to the owner).
 	 * <p/>
 	 * As such, the processing here is basically to loop over every entity currently associated with this persistence
 	 * context and for those of the correct entity (sub) type to extract its collection role property value and see
 	 * if the child is contained within that collection.  If so, we have found the owner; if not, we go on.
 	 * <p/>
 	 * Also need to account for <tt>mergeMap</tt> which acts as a local copy cache managed for the duration of a merge
 	 * operation.  It represents a map of the detached entity instances pointing to the corresponding managed instance.
 	 *
 	 * @param entityName The entity name for the entity type which would own the child
 	 * @param propertyName The name of the property on the owning entity type which would name this child association.
 	 * @param childEntity The child entity instance for which to locate the owner instance id.
 	 * @param mergeMap A map of non-persistent instances from an on-going merge operation (possibly null).
 	 *
 	 * @return The id of the entityName instance which is said to own the child; null if an appropriate owner not
 	 * located.
 	 */
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntries.get( parent );
 			//there maybe more than one parent, filter by type
 			if ( 	persister.isSubclassEntityName(entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
 				parentsByChild.remove( childEntity ); // remove wrong entry
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		for ( Entry<Object,EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
 			final EntityEntry entityEntry = me.getValue();
 			// does this entity entry pertain to the entity persister in which we are interested (owner)?
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				final Object entityEntryInstance = me.getKey();
 
 				//check if the managed object is the parent
 				boolean found = isFoundInParent(
 						propertyName,
 						childEntity,
 						persister,
 						collectionPersister,
 						entityEntryInstance
 				);
 
 				if ( !found && mergeMap != null ) {
 					//check if the detached object being merged is the parent
 					Object unmergedInstance = mergeMap.get( entityEntryInstance );
 					Object unmergedChild = mergeMap.get( childEntity );
 					if ( unmergedInstance != null && unmergedChild != null ) {
 						found = isFoundInParent(
 								propertyName,
 								unmergedChild,
 								persister,
 								collectionPersister,
 								unmergedInstance
 						);
 					}
 				}
 
 				if ( found ) {
 					return entityEntry.getId();
 				}
 
 			}
 		}
 
 		// if we get here, it is possible that we have a proxy 'in the way' of the merge map resolution...
 		// 		NOTE: decided to put this here rather than in the above loop as I was nervous about the performance
 		//		of the loop-in-loop especially considering this is far more likely the 'edge case'
 		if ( mergeMap != null ) {
 			for ( Object o : mergeMap.entrySet() ) {
 				final Entry mergeMapEntry = (Entry) o;
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = (HibernateProxy) mergeMapEntry.getKey();
 					if ( persister.isSubclassEntityName( proxy.getHibernateLazyInitializer().getEntityName() ) ) {
 						boolean found = isFoundInParent(
 								propertyName,
 								childEntity,
 								persister,
 								collectionPersister,
 								mergeMap.get( proxy )
 						);
 						if ( !found ) {
 							found = isFoundInParent(
 									propertyName,
 									mergeMap.get( childEntity ),
 									persister,
 									collectionPersister,
 									mergeMap.get( proxy )
 							);
 						}
 						if ( found ) {
 							return proxy.getHibernateLazyInitializer().getIdentifier();
 						}
 					}
 				}
 			}
 		}
 
 		return null;
 	}
 
 	private boolean isFoundInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent) {
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 
 		EntityPersister persister = session.getFactory()
 				.getEntityPersister(entity);
 		CollectionPersister cp = session.getFactory()
 				.getCollectionPersister(entity + '.' + property);
 
 	    // try cache lookup first
 	    Object parent = parentsByChild.get(childEntity);
 		if (parent != null) {
 			final EntityEntry entityEntry = entityEntries.get(parent);
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				Object index = getIndexInParent(property, childEntity, persister, cp, parent);
 
 				if (index==null && mergeMap!=null) {
 					Object unmergedInstance = mergeMap.get(parent);
 					Object unmergedChild = mergeMap.get(childEntity);
 					if ( unmergedInstance!=null && unmergedChild!=null ) {
 						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
 					}
 				}
 				if (index!=null) {
 					return index;
 				}
 			}
 			else {
 				parentsByChild.remove(childEntity); // remove wrong entry
 			}
 		}
 
 		//Not found in cache, proceed
 		for ( Entry<Object, EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
 			EntityEntry ee = me.getValue();
 			if ( persister.isSubclassEntityName( ee.getEntityName() ) ) {
 				Object instance = me.getKey();
 
 				Object index = getIndexInParent(property, childEntity, persister, cp, instance);
 
 				if (index==null && mergeMap!=null) {
 					Object unmergedInstance = mergeMap.get(instance);
 					Object unmergedChild = mergeMap.get(childEntity);
 					if ( unmergedInstance!=null && unmergedChild!=null ) {
 						index = getIndexInParent(property, unmergedChild, persister, cp, unmergedInstance);
 					}
 				}
 
 				if (index!=null) return index;
 			}
 		}
 		return null;
 	}
 
 	private Object getIndexInParent(
 			String property,
 			Object childEntity,
 			EntityPersister persister,
 			CollectionPersister collectionPersister,
 			Object potentialParent){
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		if ( collection!=null && Hibernate.isInitialized(collection) ) {
 			return collectionPersister.getCollectionType().indexOf(collection, childEntity);
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Record the fact that the association belonging to the keyed
 	 * entity is null.
 	 */
 	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		if ( entityOrProxy == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		boolean isReadOnly;
 		if ( entityOrProxy instanceof HibernateProxy ) {
 			isReadOnly = ( ( HibernateProxy ) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
 		}
 		else {
 			EntityEntry ee =  getEntry( entityOrProxy );
 			if ( ee == null ) {
 				throw new TransientObjectException("Instance was not associated with this persistence context" );
 			}
 			isReadOnly = ee.isReadOnly();
 		}
 		return isReadOnly;
 	}
 
 	@Override
 	public void setReadOnly(Object object, boolean readOnly) {
 		if ( object == null ) {
 			throw new AssertionFailure( "object must be non-null." );
 		}
 		if ( isReadOnly( object ) == readOnly ) {
 			return;
 		}
 		if ( object instanceof HibernateProxy ) {
 			HibernateProxy proxy = ( HibernateProxy ) object;
 			setProxyReadOnly( proxy, readOnly );
 			if ( Hibernate.isInitialized( proxy ) ) {
 				setEntityReadOnly(
 						proxy.getHibernateLazyInitializer().getImplementation(),
 						readOnly
 				);
 			}
 		}
 		else {
 			setEntityReadOnly( object, readOnly );
 			// PersistenceContext.proxyFor( entity ) returns entity if there is no proxy for that entity
 			// so need to check the return value to be sure it is really a proxy
 			Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
 			if ( maybeProxy instanceof HibernateProxy ) {
 				setProxyReadOnly( ( HibernateProxy ) maybeProxy, readOnly );
 			}
 		}
 	}
 
 	private void setProxyReadOnly(HibernateProxy proxy, boolean readOnly) {
 		if ( proxy.getHibernateLazyInitializer().getSession() != getSession() ) {
 			throw new AssertionFailure(
 					"Attempt to set a proxy to read-only that is associated with a different session" );
 		}
 		proxy.getHibernateLazyInitializer().setReadOnly( readOnly );
 	}
 
 	private void setEntityReadOnly(Object entity, boolean readOnly) {
 		EntityEntry entry = getEntry(entity);
 		if (entry == null) {
 			throw new TransientObjectException("Instance was not associated with this persistence context" );
 		}
 		entry.setReadOnly(readOnly, entity );
 		hasNonReadOnlyEntities = hasNonReadOnlyEntities || ! readOnly;
 	}
 
 	@Override
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		Object entity = entitiesByKey.remove( oldKey );
 		EntityEntry oldEntry = entityEntries.remove( entity );
 		parentsByChild.clear();
 
 		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
 		addEntity( newKey, entity );
 		addEntry(
 				entity,
 		        oldEntry.getStatus(),
 		        oldEntry.getLoadedState(),
 		        oldEntry.getRowId(),
 		        generatedId,
 		        oldEntry.getVersion(),
 		        oldEntry.getLockMode(),
 		        oldEntry.isExistsInDatabase(),
 		        oldEntry.getPersister(),
 		        oldEntry.isBeingReplicated(),
 		        oldEntry.isLoadedWithLazyPropertiesUnfetched()
 		);
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * persistence context.
 	 *
 	 * @param oos The stream to which the persistence context should get written
 	 * @throws IOException serialization errors.
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace( "Serializing persistent-context" );
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries");
 		Iterator itr = entitiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries");
 		itr = entitiesByUniqueKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityUniqueKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries");
 		itr = proxiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( (EntityKey) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries");
 		itr = entitySnapshotsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entityEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entityEntries.size() + "] entityEntries entries");
 		itr = entityEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( EntityEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( collectionsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries");
 		itr = collectionsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( CollectionKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries");
 		itr = collectionEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( CollectionEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries");
 		itr = arrayHolders.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries");
 		for ( EntityKey entry : nullifiableEntityKeys ) {
 			entry.serialize( oos );
 		}
 	}
 
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace("Serializing persistent-context");
 		StatefulPersistenceContext rtn = new StatefulPersistenceContext( session );
 
 		// during deserialization, we need to reconnect all proxies and
 		// collections to this session, as well as the EntityEntry and
 		// CollectionEntry instances; these associations are transient
 		// because serialization is used for different things.
 
 		try {
 			rtn.defaultReadOnly = ois.readBoolean();
 			// todo : we can actually just determine this from the incoming EntityEntry-s
 			rtn.hasNonReadOnlyEntities = ois.readBoolean();
 
 			int count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
 			rtn.entitiesByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
 			rtn.entitiesByUniqueKey = new HashMap<EntityUniqueKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
 			//noinspection unchecked
 			rtn.proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK, count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count, .75f );
 			for ( int i = 0; i < count; i++ ) {
 				EntityKey ek = EntityKey.deserialize( ois, session );
 				Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
 				} else {
 					if ( tracing ) LOG.trace("Encountered prunded proxy");
 				}
 				// otherwise, the proxy was pruned during the serialization process
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
 			rtn.entitySnapshotsByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
 			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				Object entity = ois.readObject();
 				EntityEntry entry = EntityEntry.deserialize( ois, rtn );
 				rtn.entityEntries.put( entity, entry );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
 			rtn.collectionsByKey = new HashMap<CollectionKey,PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				final PersistentCollection pc = ( PersistentCollection ) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
 			rtn.arrayHolders = new IdentityHashMap<Object, PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
 			rtn.nullifiableEntityKeys = new HashSet<EntityKey>();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, session ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
 	@Override
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put(child, parent);
 	}
 
 	@Override
 	public void removeChildParent(Object child) {
 	   parentsByChild.remove(child);
 	}
 
 
 	// INSERTED KEYS HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private HashMap<String,List<Serializable>> insertedKeysMap;
 
 	@Override
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		// we only are worried about registering these if the persister defines caching
 		if ( persister.hasCache() ) {
 			if ( insertedKeysMap == null ) {
 				insertedKeysMap = new HashMap<String, List<Serializable>>();
 			}
 			final String rootEntityName = persister.getRootEntityName();
 			List<Serializable> insertedEntityIds = insertedKeysMap.get( rootEntityName );
 			if ( insertedEntityIds == null ) {
 				insertedEntityIds = new ArrayList<Serializable>();
 				insertedKeysMap.put( rootEntityName, insertedEntityIds );
 			}
 			insertedEntityIds.add( id );
 		}
 	}
 
 	@Override
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
 		// again, we only really care if the entity is cached
 		if ( persister.hasCache() ) {
 			if ( insertedKeysMap != null ) {
 				List<Serializable> insertedEntityIds = insertedKeysMap.get( persister.getRootEntityName() );
 				if ( insertedEntityIds != null ) {
 					return insertedEntityIds.contains( id );
 				}
 			}
 		}
 		return false;
 	}
 
 	private void cleanUpInsertedKeysAfterTransaction() {
 		if ( insertedKeysMap != null ) {
 			insertedKeysMap.clear();
 		}
 	}
 
 
 
 	// NATURAL ID RESOLUTION HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	private NaturalIdXrefDelegate naturalIdXrefDelegate = new NaturalIdXrefDelegate( this );
+
 	@Override
-	public void loadedStateInsertedNotification(EntityEntry entityEntry, Object[] state) {
+	public void entityStateInsertedNotification(EntityEntry entityEntry, Object[] state) {
 		final EntityPersister persister = entityEntry.getPersister();
 		if ( !persister.hasNaturalIdentifier() ) {
 			// nothing to do
 			return;
 		}
 
 		final Object[] naturalIdValues = getNaturalIdValues( state, persister );
 
 		// cache
-		cacheNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues, CachedNaturalIdValueSource.INSERT );
+		naturalIdXrefDelegate.cacheNaturalIdResolution(
+				persister,
+				entityEntry.getId(),
+				naturalIdValues,
+				CachedNaturalIdValueSource.INSERT
+		);
 	}
 
 	@Override
-	public void loadedStateUpdatedNotification(EntityEntry entityEntry, Object[] state) {
+	public void entityStateUpdatedNotification(EntityEntry entityEntry, Object[] state) {
 		final EntityPersister persister = entityEntry.getPersister();
 		if ( !persister.hasNaturalIdentifier() ) {
 			// nothing to do
 			return;
 		}
 
 		final Object[] naturalIdValues = getNaturalIdValues( state, persister );
 
 		// re-cache
-		cacheNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues, CachedNaturalIdValueSource.UPDATE );
+		naturalIdXrefDelegate.cacheNaturalIdResolution(
+				persister,
+				entityEntry.getId(),
+				naturalIdValues,
+				CachedNaturalIdValueSource.UPDATE
+		);
 	}
 
 	@Override
-	public void loadedStateDeletedNotification(EntityEntry entityEntry, Object[] deletedState) {
+	public void entityStateDeletedNotification(EntityEntry entityEntry, Object[] deletedState) {
 		final EntityPersister persister = entityEntry.getPersister();
 		if ( !persister.hasNaturalIdentifier() ) {
 			// nothing to do
 			return;
 		}
 
 		final Object[] naturalIdValues = getNaturalIdValues( deletedState, persister );
 
 		// evict from cache
-		evictNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues );
+		naturalIdXrefDelegate.evictNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues );
 	}
 
 	private Object[] getNaturalIdValues(Object[] state, EntityPersister persister) {
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 
 		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 			naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
 		}
 
 		return naturalIdValues;
 	}
 
-	private static class LocalNaturalIdCacheKey {
-		private final EntityPersister persister;
-		private final Object[] values;
-		private int hashCode;
-
-		public LocalNaturalIdCacheKey(EntityPersister persister, Object[] values) {
-			this.persister = persister;
-			this.values = values;
-
-			final int prime = 31;
-			int result = 1;
-			result = prime * result + ( ( persister == null ) ? 0 : persister.hashCode() );
-			result = prime * result + Arrays.hashCode( values );
-			this.hashCode = result;
-		}
-
-		public Object[] getValues() {
-			return values;
-		}
-
-		@Override
-		public int hashCode() {
-			return this.hashCode;
-		}
-
-		@Override
-		public boolean equals(Object obj) {
-			if ( this == obj ) {
-				return true;
-			}
-			if ( obj == null ) {
-				return false;
-			}
-			if ( getClass() != obj.getClass() ) {
-				return false;
-			}
-
-			final LocalNaturalIdCacheKey other = (LocalNaturalIdCacheKey) obj;
-			return persister.equals( other.persister )
-					&& Arrays.equals( values, other.values );
-		}
-	}
-
-	private static class NaturalIdResolutionCache implements Serializable {
-		private final EntityPersister persister;
-
-		private NaturalIdResolutionCache(EntityPersister persister) {
-			this.persister = persister;
-		}
-
-		public EntityPersister getPersister() {
-			return persister;
-		}
-
-		private Map<Serializable, LocalNaturalIdCacheKey> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, LocalNaturalIdCacheKey>();
-		private Map<LocalNaturalIdCacheKey, Serializable> naturalIdToPkMap = new ConcurrentHashMap<LocalNaturalIdCacheKey, Serializable>();
-	}
-
-	private void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
-		if ( !persister.hasNaturalIdentifier() ) {
-			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
-		}
-		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
-			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
-		}
-	}
-
-	private final Map<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
 
 	@Override
 	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
-		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
-		if ( entityNaturalIdResolutionCache == null ) {
-			return null;
-		}
-
-		final LocalNaturalIdCacheKey localNaturalIdCacheKey = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
-		if ( localNaturalIdCacheKey == null ) {
-			return null;
-		}
-
-		return localNaturalIdCacheKey.getValues();
+		return naturalIdXrefDelegate.findCachedNaturalId( persister, pk );
 	}
 
 	@Override
 	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
-		validateNaturalId( persister, naturalIdValues );
-
-		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
-
-		Serializable pk;
-		final LocalNaturalIdCacheKey localNaturalIdCacheKey = new LocalNaturalIdCacheKey( persister, naturalIdValues );
-		if ( entityNaturalIdResolutionCache != null ) {
-			pk = entityNaturalIdResolutionCache.naturalIdToPkMap.get( localNaturalIdCacheKey );
-
-			// Found in session cache
-			if ( pk != null ) {
-				if ( LOG.isTraceEnabled() ) {
-					LOG.trace(
-							"Resolved natural key -> primary key resolution in session cache: " +
-									persister.getRootEntityName() + "#[" +
-									Arrays.toString( naturalIdValues ) + "]"
-					);
-				}
-
-				return pk;
-			}
-		}
-
-		// Session cache miss, see if second-level caching is enabled
-		if ( !persister.hasNaturalIdCache() ) {
-			return null;
-		}
-		
-		// Try resolution from second-level cache
-		final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
-		
-		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-		pk = (Serializable) naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session.getTimestamp() );
-
-		// Found in second-level cache, store in session cache
-		final SessionFactoryImplementor factory = getSession().getFactory();
-		if ( pk != null ) {
-			if ( factory.getStatistics().isStatisticsEnabled() ) {
-				factory.getStatisticsImplementor().naturalIdCacheHit(
-						naturalIdCacheAccessStrategy.getRegion().getName()
-				);
-			}
-
-			if ( LOG.isTraceEnabled() )
-				LOG.trace(
-						"Resolved natural key -> primary key resolution in second-level cache: " +
-								persister.getRootEntityName() + "#[" +
-								Arrays.toString( naturalIdValues ) + "]"
-				);
-
-			if ( entityNaturalIdResolutionCache == null ) {
-				entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
-				naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
-			}
-
-			entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, localNaturalIdCacheKey );
-			entityNaturalIdResolutionCache.naturalIdToPkMap.put( localNaturalIdCacheKey, pk );
-		}
-		else if ( factory.getStatistics().isStatisticsEnabled() ) {
-			factory.getStatisticsImplementor().naturalIdCacheMiss( naturalIdCacheAccessStrategy.getRegion().getName() );
-		}
-
-		return pk;
+		return naturalIdXrefDelegate.findCachedNaturalIdResolution( persister, naturalIdValues );
 	}
 
 	@Override
-	public void cacheNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] naturalIdValues,
+	public void cacheNaturalIdResolution(
+			EntityPersister persister,
+			final Serializable pk,
+			Object[] naturalIdValues,
 			CachedNaturalIdValueSource valueSource) {
-		validateNaturalId( persister, naturalIdValues );
-		
-		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
-		if ( entityNaturalIdResolutionCache == null ) {
-			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
-			naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
-		}
-
-		final LocalNaturalIdCacheKey localNaturalIdCacheKey = new LocalNaturalIdCacheKey( persister, naturalIdValues );
-		entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, localNaturalIdCacheKey );
-		entityNaturalIdResolutionCache.naturalIdToPkMap.put( localNaturalIdCacheKey, pk );
-
-		//If second-level caching is enabled cache the resolution there as well
-		if ( persister.hasNaturalIdCache() ) {
-			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
-
-			final SessionFactoryImplementor factory = getSession().getFactory();
-			
-			switch ( valueSource ) {
-				case LOAD: {
-					final boolean put = naturalIdCacheAccessStrategy.putFromLoad(
-							naturalIdCacheKey,
-							pk,
-							session.getTimestamp(),
-							null
-					);
-
-					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
-						factory.getStatisticsImplementor()
-								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
-					}
-
-					break;
-				}
-				case INSERT: {
-					naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, pk );
-
-					( (EventSource) this.session ).getActionQueue().registerProcess(
-							new AfterTransactionCompletionProcess() {
-								@Override
-								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-									final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, pk );
-
-									if ( put && factory.getStatistics().isStatisticsEnabled() ) {
-										factory.getStatisticsImplementor().naturalIdCachePut(
-												naturalIdCacheAccessStrategy.getRegion().getName() );
-									}
-								}
-							}
-					);
-
-					break;
-				}
-				case UPDATE: {
-					final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
-					naturalIdCacheAccessStrategy.update( naturalIdCacheKey, pk );
-
-					( (EventSource) this.session ).getActionQueue().registerProcess(
-							new AfterTransactionCompletionProcess() {
-								@Override
-								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-									final boolean put = naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, pk, lock );
-
-									if ( put && factory.getStatistics().isStatisticsEnabled() ) {
-										factory.getStatisticsImplementor().naturalIdCachePut(
-												naturalIdCacheAccessStrategy.getRegion().getName() );
-									}
-								}
-							}
-					);
-
-					break;
-				}
-			}
-		}
-	}
-
-	private void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] deletedNaturalIdValues) {
-		if ( !persister.hasNaturalIdentifier() ) {
-			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
-		}
-		if ( persister.getNaturalIdentifierProperties().length != deletedNaturalIdValues.length ) {
-			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
-		}
-
-		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
-		Object[] sessionCachedNaturalIdValues = null;
-		if ( entityNaturalIdResolutionCache != null ) {
-			final LocalNaturalIdCacheKey localNaturalIdCacheKey = entityNaturalIdResolutionCache.pkToNaturalIdMap
-					.remove( pk );
-			if ( localNaturalIdCacheKey != null ) {
-				entityNaturalIdResolutionCache.naturalIdToPkMap.remove( localNaturalIdCacheKey );
-				sessionCachedNaturalIdValues = localNaturalIdCacheKey.getValues();
-			}
-		}
-
-		if ( persister.hasNaturalIdCache() ) {
-			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister
-					.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( deletedNaturalIdValues, persister,
-					session );
-			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
-
-			if ( sessionCachedNaturalIdValues != null
-					&& !Arrays.equals( sessionCachedNaturalIdValues, deletedNaturalIdValues ) ) {
-				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues,
-						persister, session );
-				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
-			}
-		}
+		naturalIdXrefDelegate.cacheNaturalIdResolution( persister, pk, naturalIdValues, valueSource );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/CachedNaturalIdValueSource.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/CachedNaturalIdValueSource.java
new file mode 100644
index 0000000000..500c7ceed8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/CachedNaturalIdValueSource.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.engine.spi;
+
+/**
+ * The type of action from which the cache call is originating.
+ */
+public enum CachedNaturalIdValueSource {
+	LOAD,
+	INSERT,
+	UPDATE
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
index 7fc7d89625..ccdd884910 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
@@ -1,465 +1,465 @@
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
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * We need an entry to tell us all about the current state of an object with respect to its persistent state
  * 
  * @author Gavin King
  */
 public final class EntityEntry implements Serializable {
 	private LockMode lockMode;
 	private Status status;
 	private Status previousStatus;
 	private final Serializable id;
 	private Object[] loadedState;
 	private Object[] deletedState;
 	private boolean existsInDatabase;
 	private Object version;
 	private transient EntityPersister persister; // for convenience to save some lookups
 	private final EntityMode entityMode;
 	private final String tenantId;
 	private final String entityName;
 	private transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
 	private boolean isBeingReplicated;
 	private boolean loadedWithLazyPropertiesUnfetched; //NOTE: this is not updated when properties are fetched lazily!
 	private final transient Object rowId;
 	private final transient PersistenceContext persistenceContext;
 
 	public EntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
 			final String tenantId,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.status = status;
 		this.previousStatus = null;
 		// only retain loaded state if the status is not Status.READ_ONLY
 		if ( status != Status.READ_ONLY ) {
 			this.loadedState = loadedState;
 		}
 		this.id=id;
 		this.rowId=rowId;
 		this.existsInDatabase=existsInDatabase;
 		this.version=version;
 		this.lockMode=lockMode;
 		this.isBeingReplicated=disableVersionIncrement;
 		this.loadedWithLazyPropertiesUnfetched = lazyPropertiesAreUnfetched;
 		this.persister=persister;
 		this.entityMode = entityMode;
 		this.tenantId = tenantId;
 		this.entityName = persister == null ? null : persister.getEntityName();
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * This for is used during custom deserialization handling
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	private EntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final EntityMode entityMode,
 			final String tenantId,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 	        final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.entityName = entityName;
 		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
 		this.id = id;
 		this.entityMode = entityMode;
 		this.tenantId = tenantId;
 		this.status = status;
 		this.previousStatus = previousStatus;
 		this.loadedState = loadedState;
 		this.deletedState = deletedState;
 		this.version = version;
 		this.lockMode = lockMode;
 		this.existsInDatabase = existsInDatabase;
 		this.isBeingReplicated = isBeingReplicated;
 		this.loadedWithLazyPropertiesUnfetched = loadedWithLazyPropertiesUnfetched;
 		this.rowId = null; // this is equivalent to the old behavior...
 		this.persistenceContext = persistenceContext;
 	}
 
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	public void setLockMode(LockMode lockMode) {
 		this.lockMode = lockMode;
 	}
 
 	public Status getStatus() {
 		return status;
 	}
 
 	public void setStatus(Status status) {
 		if (status==Status.READ_ONLY) {
 			loadedState = null; //memory optimization
 		}
 		if ( this.status != status ) {
 			this.previousStatus = this.status;
 			this.status = status;
 		}
 	}
 
 	public Serializable getId() {
 		return id;
 	}
 
 	public Object[] getLoadedState() {
 		return loadedState;
 	}
 
 	public Object[] getDeletedState() {
 		return deletedState;
 	}
 
 	public void setDeletedState(Object[] deletedState) {
 		this.deletedState = deletedState;
 	}
 
 	public boolean isExistsInDatabase() {
 		return existsInDatabase;
 	}
 
 	public Object getVersion() {
 		return version;
 	}
 
 	public EntityPersister getPersister() {
 		return persister;
 	}
 
 	/**
 	 * Get the EntityKey based on this EntityEntry.
 	 * @return the EntityKey
 	 * @throws  IllegalStateException if getId() is null
 	 */
 	public EntityKey getEntityKey() {
 		if ( cachedEntityKey == null ) {
 			if ( getId() == null ) {
 				throw new IllegalStateException( "cannot generate an EntityKey when id is null.");
 			}
 			cachedEntityKey = new EntityKey( getId(), getPersister(), tenantId );
 		}
 		return cachedEntityKey;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public boolean isBeingReplicated() {
 		return isBeingReplicated;
 	}
 	
 	public Object getRowId() {
 		return rowId;
 	}
 	
 	/**
 	 * Handle updating the internal state of the entry after actually performing
 	 * the database update.  Specifically we update the snapshot information and
 	 * escalate the lock mode
 	 *
 	 * @param entity The entity instance
 	 * @param updatedState The state calculated after the update (becomes the
 	 * new {@link #getLoadedState() loaded state}.
 	 * @param nextVersion The new version.
 	 */
 	public void postUpdate(Object entity, Object[] updatedState, Object nextVersion) {
 		this.loadedState = updatedState;
 		setLockMode( LockMode.WRITE );
 
 		if ( getPersister().isVersioned() ) {
 			this.version = nextVersion;
 			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 		}
 
 		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
 			final FieldInterceptor interceptor = getPersister().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.clearDirty();
 			}
 		}
 		persistenceContext.getSession()
 				.getFactory()
 				.getCustomEntityDirtinessStrategy()
 				.resetDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
 
 		notifyLoadedStateUpdated( updatedState );
 	}
 
 	/**
 	 * After actually deleting a row, record the fact that the instance no longer
 	 * exists in the database
 	 */
 	public void postDelete() {
 		previousStatus = status;
 		status = Status.GONE;
 		existsInDatabase = false;
 		
 		notifyLoadedStateDeleted( deletedState );
 	}
 	
 	/**
 	 * After actually inserting a row, record the fact that the instance exists on the 
 	 * database (needed for identity-column key generation)
 	 */
 	public void postInsert(Object[] insertedState) {
 		existsInDatabase = true;
 		
 		notifyLoadedStateInserted( insertedState );
 	}
 	
 	public boolean isNullifiable(boolean earlyInsert, SessionImplementor session) {
 		return getStatus() == Status.SAVING || (
 				earlyInsert ?
 						!isExistsInDatabase() :
 						session.getPersistenceContext().getNullifiableEntityKeys()
 							.contains( getEntityKey() )
 				);
 	}
 	
 	public Object getLoadedValue(String propertyName) {
 		int propertyIndex = ( (UniqueKeyLoadable) persister ).getPropertyIndex(propertyName);
 		return loadedState[propertyIndex];
 	}
 
 	/**
 	 * Not sure this is the best method name, but the general idea here is to return {@code true} if the entity can
 	 * possibly be dirty.  This can only be the case if it is in a modifiable state (not read-only/deleted) and it
 	 * either has mutable properties or field-interception is not telling us it is dirty.  Clear as mud? :/
 	 *
 	 * A name like canPossiblyBeDirty might be better
 	 *
 	 * @param entity The entity to test
 	 *
 	 * @return {@code true} indicates that the entity could possibly be dirty and that dirty check
 	 * should happen; {@code false} indicates there is no way the entity can be dirty
 	 */
 	public boolean requiresDirtyCheck(Object entity) {
 		return isModifiableEntity()
 				&& ( getPersister().hasMutableProperties() || ! isUnequivocallyNonDirty( entity ) );
 	}
 
 	@SuppressWarnings( {"SimplifiableIfStatement"})
 	private boolean isUnequivocallyNonDirty(Object entity) {
 		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
 			// the entity must be instrumented (otherwise we cant check dirty flag) and the dirty flag is false
 			return ! getPersister().getInstrumentationMetadata().extractInterceptor( entity ).isDirty();
 		}
 
 		final CustomEntityDirtinessStrategy customEntityDirtinessStrategy =
 				persistenceContext.getSession().getFactory().getCustomEntityDirtinessStrategy();
 		if ( customEntityDirtinessStrategy.canDirtyCheck( entity, getPersister(), (Session) persistenceContext.getSession() ) ) {
 			return ! customEntityDirtinessStrategy.isDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
 		}
 
 		return false;
 	}
 
 	/**
 	 * Can the entity be modified?
 	 *
 	 * The entity is modifiable if all of the following are true:
 	 * <ul>
 	 * <li>the entity class is mutable</li>
 	 * <li>the entity is not read-only</li>
 	 * <li>if the current status is Status.DELETED, then the entity was not read-only when it was deleted</li>
 	 * </ul>
 	 * @return true, if the entity is modifiable; false, otherwise,
 	 */
 	public boolean isModifiableEntity() {
 		return getPersister().isMutable()
 				&& status != Status.READ_ONLY
 				&& ! ( status == Status.DELETED && previousStatus == Status.READ_ONLY );
 	}
 
 	public void forceLocked(Object entity, Object nextVersion) {
 		version = nextVersion;
 		loadedState[ persister.getVersionProperty() ] = version;
 		//noinspection deprecation
 		setLockMode( LockMode.FORCE );  // TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
 		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 	}
 
 	public boolean isReadOnly() {
 		if (status != Status.MANAGED && status != Status.READ_ONLY) {
 			throw new HibernateException("instance was not in a valid state");
 		}
 		return status == Status.READ_ONLY;
 	}
 
 	public void setReadOnly(boolean readOnly, Object entity) {
 		if ( readOnly == isReadOnly() ) {
 			// simply return since the status is not being changed
 			return;
 		}
 		if ( readOnly ) {
 			setStatus( Status.READ_ONLY );
 			loadedState = null;
 		}
 		else {
 			if ( ! persister.isMutable() ) {
 				throw new IllegalStateException( "Cannot make an immutable entity modifiable." );
 			}
 			setStatus( Status.MANAGED );
 			loadedState = getPersister().getPropertyValues( entity );
 			notifyLoadedStateUpdated( loadedState );
 		}
 	}
 	
 	public String toString() {
 		return "EntityEntry" + 
 				MessageHelper.infoString(entityName, id) + 
 				'(' + status + ')';
 	}
 
 	public boolean isLoadedWithLazyPropertiesUnfetched() {
 		return loadedWithLazyPropertiesUnfetched;
 	}
 
 	private void notifyLoadedStateUpdated(Object[] state) {
 		if ( persistenceContext == null ) {
 			throw new HibernateException( "PersistenceContext was null on attempt to update loaded state" );
 		}
 
-		persistenceContext.loadedStateUpdatedNotification( this, state );
+		persistenceContext.entityStateUpdatedNotification( this, state );
 	}
 	
 	private void notifyLoadedStateInserted(Object[] state) {
 		if ( persistenceContext == null ) {
 			throw new HibernateException( "PersistenceContext was null on attempt to insert loaded state" );
 		}
 
-		persistenceContext.loadedStateInsertedNotification( this, state );
+		persistenceContext.entityStateInsertedNotification( this, state );
 	}
 
 	private void notifyLoadedStateDeleted(Object[] deletedState) {
 		if ( persistenceContext == null ) {
 			throw new HibernateException( "PersistenceContext was null on attempt to delete loaded state" );
 		}
 
-		persistenceContext.loadedStateDeletedNotification( this, deletedState );
+		persistenceContext.entityStateDeletedNotification( this, deletedState );
 	}
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 *
 	 * @throws IOException If a stream error occurs
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( entityName );
 		oos.writeObject( id );
 		oos.writeObject( entityMode.toString() );
 		oos.writeObject( tenantId );
 		oos.writeObject( status.name() );
 		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
 		// todo : potentially look at optimizing these two arrays
 		oos.writeObject( loadedState );
 		oos.writeObject( deletedState );
 		oos.writeObject( version );
 		oos.writeObject( lockMode.toString() );
 		oos.writeBoolean( existsInDatabase );
 		oos.writeBoolean( isBeingReplicated );
 		oos.writeBoolean( loadedWithLazyPropertiesUnfetched );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param persistenceContext The context being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws IOException If a stream error occurs
 	 * @throws ClassNotFoundException If any of the classes declared in the stream
 	 * cannot be found
 	 */
 	public static EntityEntry deserialize(
 			ObjectInputStream ois,
 	        PersistenceContext persistenceContext) throws IOException, ClassNotFoundException {
 		String previousStatusString;
 		return new EntityEntry(
 				// this complexity comes from non-flushed changes, should really look at how that reattaches entries
 				( persistenceContext.getSession() == null ? null : persistenceContext.getSession().getFactory() ),
 		        (String) ois.readObject(),
 				( Serializable ) ois.readObject(),
 	            EntityMode.parse( (String) ois.readObject() ),
 				(String) ois.readObject(),
 				Status.valueOf( (String) ois.readObject() ),
 				( ( previousStatusString = ( String ) ois.readObject() ).length() == 0 ?
 							null :
 							Status.valueOf( previousStatusString )
 				),
 	            ( Object[] ) ois.readObject(),
 	            ( Object[] ) ois.readObject(),
 	            ois.readObject(),
 	            LockMode.valueOf( (String) ois.readObject() ),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 				persistenceContext
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
index 5f6697e904..f59a4540a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
@@ -1,707 +1,716 @@
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
 import java.util.HashSet;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents the state of "stuff" Hibernate is tracking, including (not exhaustive):
  * <ul>
  *     <li>entities</li>
  *     <li>collections</li>
  *     <li>snapshots</li>
  *     <li>proxies</li>
  * </ul>
  * <p/>
  * Often referred to as the "first level cache".
  * 
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"JavaDoc"})
 public interface PersistenceContext {
 	
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean isStateless();
 
 	/**
 	 * Get the session to which this persistence context is bound.
 	 *
 	 * @return The session.
 	 */
 	public SessionImplementor getSession();
 
 	/**
 	 * Retrieve this persistence context's managed load context.
 	 *
 	 * @return The load context
 	 */
 	public LoadContexts getLoadContexts();
 
 	/**
 	 * Add a collection which has no owner loaded
 	 *
 	 * @param key The collection key under which to add the collection
 	 * @param collection The collection to add
 	 */
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection);
 
 	/**
 	 * Take ownership of a previously unowned collection, if one.  This method returns {@code null} if no such
 	 * collection was previous added () or was previously removed.
 	 * <p/>
 	 * This should indicate the owner is being loaded and we are ready to "link" them.
 	 *
 	 * @param key The collection key for which to locate a collection collection
 	 *
 	 * @return The unowned collection, or {@code null}
 	 */
 	public PersistentCollection useUnownedCollection(CollectionKey key);
 
 	/**
 	 * Get the {@link BatchFetchQueue}, instantiating one if necessary.
 	 *
 	 * @return The batch fetch queue in effect for this persistence context
 	 */
 	public BatchFetchQueue getBatchFetchQueue();
 	
 	/**
 	 * Clear the state of the persistence context
 	 */
 	public void clear();
 
 	/**
 	 * @return false if we know for certain that all the entities are read-only
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean hasNonReadOnlyEntities();
 
 	/**
 	 * Set the status of an entry
 	 *
 	 * @param entry The entry for which to set the status
 	 * @param status The new status
 	 */
 	public void setEntryStatus(EntityEntry entry, Status status);
 
 	/**
 	 * Called after transactions end
 	 */
 	public void afterTransactionCompletion();
 
 	/**
 	 * Get the current state of the entity as known to the underlying database, or null if there is no
 	 * corresponding row
 	 *
 	 * @param id The identifier of the entity for which to grab a snapshot
 	 * @param persister The persister of the entity.
 	 *
 	 * @return The entity's (non-cached) snapshot
 	 *
 	 * @see #getCachedDatabaseSnapshot
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister);
 
 	/**
 	 * Get the current database state of the entity, using the cached state snapshot if one is available.
 	 *
 	 * @param key The entity key
 	 *
 	 * @return The entity's (non-cached) snapshot
 	 */
 	public Object[] getCachedDatabaseSnapshot(EntityKey key);
 
 	/**
 	 * Get the values of the natural id fields as known to the underlying database, or null if the entity has no
 	 * natural id or there is no corresponding row.
 	 *
 	 * @param id The identifier of the entity for which to grab a snapshot
 	 * @param persister The persister of the entity.
 	 *
 	 * @return The current (non-cached) snapshot of the entity's natural id state.
 	 */
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister);
 
 	/**
 	 * Add a canonical mapping from entity key to entity instance
 	 *
 	 * @param key The key under which to add an entity
 	 * @param entity The entity instance to add
 	 */
 	public void addEntity(EntityKey key, Object entity);
 
 	/**
 	 * Get the entity instance associated with the given key
 	 *
 	 * @param key The key under which to look for an entity
 	 *
 	 * @return The matching entity, or {@code null}
 	 */
 	public Object getEntity(EntityKey key);
 
 	/**
 	 * Is there an entity with the given key in the persistence context
 	 *
 	 * @param key The key under which to look for an entity
 	 *
 	 * @return {@code true} indicates an entity was found; otherwise {@code false}
 	 */
 	public boolean containsEntity(EntityKey key);
 
 	/**
 	 * Remove an entity.  Also clears up all other state associated with the entity aside from the {@link EntityEntry}
 	 *
 	 * @param key The key whose matching entity should be removed
 	 *
 	 * @return The matching entity
 	 */
 	public Object removeEntity(EntityKey key);
 
 	/**
 	 * Add an entity to the cache by unique key
 	 *
 	 * @param euk The unique (non-primary) key under which to add an entity
 	 * @param entity The entity instance
 	 */
 	public void addEntity(EntityUniqueKey euk, Object entity);
 
 	/**
 	 * Get an entity cached by unique key
 	 *
 	 * @param euk The unique (non-primary) key under which to look for an entity
 	 *
 	 * @return The located entity
 	 */
 	public Object getEntity(EntityUniqueKey euk);
 
 	/**
 	 * Retrieve the {@link EntityEntry} representation of the given entity.
 	 *
 	 * @param entity The entity instance for which to locate the corresponding entry
 	 * @return The entry
 	 */
 	public EntityEntry getEntry(Object entity);
 
 	/**
 	 * Remove an entity entry from the session cache
 	 *
 	 * @param entity The entity instance for which to remove the corresponding entry
 	 * @return The matching entry
 	 */
 	public EntityEntry removeEntry(Object entity);
 
 	/**
 	 * Is there an {@link EntityEntry} registration for this entity instance?
 	 *
 	 * @param entity The entity instance for which to check for an entry
 	 *
 	 * @return {@code true} indicates a matching entry was found.
 	 */
 	public boolean isEntryFor(Object entity);
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 *
 	 * @param coll The persistent collection instance for which to locate the collection entry
 	 *
 	 * @return The matching collection entry
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll);
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
 	public EntityEntry addEntity(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final EntityKey entityKey,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched);
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it 
 	 * to the event source's internal caches.
 	 */
 	public EntityEntry addEntry(
 			final Object entity,
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final boolean disableVersionIncrement,
 			boolean lazyPropertiesAreUnfetched);
 
 	/**
 	 * Is the given collection associated with this persistence context?
 	 */
 	public boolean containsCollection(PersistentCollection collection);
 	
 	/**
 	 * Is the given proxy associated with this persistence context?
 	 */
 	public boolean containsProxy(Object proxy);
 
 	/**
 	 * Takes the given object and, if it represents a proxy, reassociates it with this event source.
 	 *
 	 * @param value The possible proxy to be reassociated.
 	 * @return Whether the passed value represented an actual proxy which got initialized.
 	 * @throws MappingException
 	 */
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException;
 
 	/**
 	 * If a deleted entity instance is re-saved, and it has a proxy, we need to
 	 * reset the identifier of the proxy 
 	 */
 	public void reassociateProxy(Object value, Serializable id) throws MappingException;
 
 	/**
 	 * Get the entity instance underlying the given proxy, throwing
 	 * an exception if the proxy is uninitialized. If the given object
 	 * is not a proxy, simply return the argument.
 	 */
 	public Object unproxy(Object maybeProxy) throws HibernateException;
 
 	/**
 	 * Possibly unproxy the given reference and reassociate it with the current session.
 	 *
 	 * @param maybeProxy The reference to be unproxied if it currently represents a proxy.
 	 * @return The unproxied instance.
 	 * @throws HibernateException
 	 */
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException;
 
 	/**
 	 * Attempts to check whether the given key represents an entity already loaded within the
 	 * current session.
 	 * @param object The entity reference against which to perform the uniqueness check.
 	 * @throws HibernateException
 	 */
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException;
 
 	/**
 	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
 	 * and overwrite the registration of the old one. This breaks == and occurs only for
 	 * "class" proxies rather than "interface" proxies. Also init the proxy to point to
 	 * the given target implementation if necessary.
 	 *
 	 * @param proxy The proxy instance to be narrowed.
 	 * @param persister The persister for the proxied entity.
 	 * @param key The internal cache key for the proxied entity.
 	 * @param object (optional) the actual proxied entity instance.
 	 * @return An appropriately narrowed instance.
 	 * @throws HibernateException
 	 */
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException;
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * third argument (the entity associated with the key) if no proxy exists. Init
 	 * the proxy to the target implementation, if necessary.
 	 */
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
 			throws HibernateException;
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * argument (the entity associated with the key) if no proxy exists.
 	 * (slower than the form above)
 	 */
 	public Object proxyFor(Object impl) throws HibernateException;
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister)
 			throws MappingException;
 
 	/**
 	 * Get the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner if its entity ID is available from the collection's loaded key
 	 * and the owner entity is in the persistence context; otherwise, returns null
 	 */
 	Object getLoadedCollectionOwnerOrNull(PersistentCollection collection);
 
 	/**
 	 * Get the ID for the entity that owned this persistent collection when it was loaded
 	 *
 	 * @param collection The persistent collection
 	 * @return the owner ID if available from the collection's loaded key; otherwise, returns null
 	 */
 	public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection);
 
 	/**
 	 * add a collection we just loaded up (still needs initializing)
 	 */
 	public void addUninitializedCollection(CollectionPersister persister,
 			PersistentCollection collection, Serializable id);
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	public void addUninitializedDetachedCollection(CollectionPersister persister,
 			PersistentCollection collection);
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 			throws HibernateException;
 
 	/**
 	 * add an (initialized) collection that was created by another session and passed
 	 * into update() (ie. one with a snapshot and existing state on the database)
 	 */
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister,
 			PersistentCollection collection) throws HibernateException;
 
 	/**
 	 * add a collection we just pulled out of the cache (does not need initializing)
 	 */
 	public CollectionEntry addInitializedCollection(CollectionPersister persister,
 			PersistentCollection collection, Serializable id) throws HibernateException;
 
 	/**
 	 * Get the collection instance associated with the <tt>CollectionKey</tt>
 	 */
 	public PersistentCollection getCollection(CollectionKey collectionKey);
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	public void addNonLazyCollection(PersistentCollection collection);
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	public void initializeNonLazyCollections() throws HibernateException;
 
 	/**
 	 * Get the <tt>PersistentCollection</tt> object for an array
 	 */
 	public PersistentCollection getCollectionHolder(Object array);
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading 
 	 * array, since the array instance is not created until endLoad().
 	 */
 	public void addCollectionHolder(PersistentCollection holder);
 	
 	/**
 	 * Remove the mapping of collection to holder during eviction
 	 * of the owning entity
 	 */
 	public PersistentCollection removeCollectionHolder(Object array);
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	public Serializable getSnapshot(PersistentCollection coll);
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
 	public CollectionEntry getCollectionEntryOrNull(Object collection);
 
 	/**
 	 * Get an existing proxy by key
 	 */
 	public Object getProxy(EntityKey key);
 
 	/**
 	 * Add a proxy to the session cache
 	 */
 	public void addProxy(EntityKey key, Object proxy);
 
 	/**
 	 * Remove a proxy from the session cache
 	 */
 	public Object removeProxy(EntityKey key);
 
 	/** 
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	public HashSet getNullifiableEntityKeys();
 
 	/**
 	 * Get the mapping from key value to entity instance
 	 */
 	public Map getEntitiesByKey();
 	
 	/**
 	 * Get the mapping from entity instance to entity entry
 	 */
 	public Map getEntityEntries();
 
 	/**
 	 * Get the mapping from collection instance to collection entry
 	 */
 	public Map getCollectionEntries();
 
 	/**
 	 * Get the mapping from collection key to collection instance
 	 */
 	public Map getCollectionsByKey();
 
 	/**
 	 * How deep are we cascaded?
 	 */
 	public int getCascadeLevel();
 	
 	/**
 	 * Called before cascading
 	 */
 	public int incrementCascadeLevel();
 
 	/**
 	 * Called after cascading
 	 */
 	public int decrementCascadeLevel();
 
 	/**
 	 * Is a flush cycle currently in process?
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean isFlushing();
 	
 	/**
 	 * Called before and after the flushcycle
 	 */
 	public void setFlushing(boolean flushing);
 
 	/**
 	 * Call this before begining a two-phase load
 	 */
 	public void beforeLoad();
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	public void afterLoad();
 	
 	/**
 	 * Is in a two-phase load? 
 	 */
 	public boolean isLoadFinished();
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	public String toString();
 
 	/**
 	 * Search the persistence context for an owner for the child object,
 	 * given a collection role
 	 */
 	public Serializable getOwnerId(String entity, String property, Object childObject, Map mergeMap);
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	public Object getIndexInOwner(String entity, String property, Object childObject, Map mergeMap);
 
 	/**
 	 * Record the fact that the association belonging to the keyed
 	 * entity is null.
 	 */
 	public void addNullProperty(EntityKey ownerKey, String propertyName);
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName);
 
 	/**
 	 * Will entities and proxies that are loaded into this persistence
 	 * context be made read-only by default?
 	 *
 	 * To determine the read-only/modifiable setting for a particular entity
 	 * or proxy:
 	 * @see PersistenceContext#isReadOnly(Object)
 	 * @see org.hibernate.Session#isReadOnly(Object) 
 	 *
 	 * @return true, loaded entities/proxies will be made read-only by default;
 	 *         false, loaded entities/proxies will be made modifiable by default.
 	 *
 	 * @see org.hibernate.Session#isDefaultReadOnly() 
 	 */
 	public boolean isDefaultReadOnly();
 
 	/**
 	 * Change the default for entities and proxies loaded into this persistence
 	 * context from modifiable to read-only mode, or from modifiable to read-only
 	 * mode.
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the persistence context's current setting.
 	 *
 	 * To change the read-only/modifiable setting for a particular entity
 	 * or proxy that is already in this session:
 +	 * @see PersistenceContext#setReadOnly(Object,boolean)
 	 * @see org.hibernate.Session#setReadOnly(Object, boolean)
 	 *
 	 * To override this session's read-only/modifiable setting for entities
 	 * and proxies loaded by a Query:
 	 * @see org.hibernate.Query#setReadOnly(boolean)
 	 *
 	 * @param readOnly true, the default for loaded entities/proxies is read-only;
 	 *                 false, the default for loaded entities/proxies is modifiable
 	 *
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 */
 	public void setDefaultReadOnly(boolean readOnly);
 
 	/**
 	 * Is the entity or proxy read-only?
 	 * <p/>
 	 * To determine the default read-only/modifiable setting used for entities and proxies that are loaded into the
 	 * session use {@link org.hibernate.Session#isDefaultReadOnly}
 	 *
 	 * @param entityOrProxy an entity or proxy
 	 *
 	 * @return {@code true} if the object is read-only; otherwise {@code false} to indicate that the object is
 	 * modifiable.
 	 */
 	public boolean isReadOnly(Object entityOrProxy);
 
 	/**
 	 * Set an unmodified persistent object to read-only mode, or a read-only
 	 * object to modifiable mode.
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the session's current setting.
 	 *
 	 * If the entity or proxy already has the specified read-only/modifiable
 	 * setting, then this method does nothing.
 	 *
 	 * @param entityOrProxy an entity or proxy
 	 * @param readOnly if {@code true}, the entity or proxy is made read-only; otherwise, the entity or proxy is made
 	 * modifiable.
 	 *
 	 * @see org.hibernate.Session#setDefaultReadOnly
 	 * @see org.hibernate.Session#setReadOnly
 	 * @see org.hibernate.Query#setReadOnly
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId);
 
 	/**
 	 * Add a child/parent relation to cache for cascading op
 	 *
 	 * @param child The child of the relationship
 	 * @param parent The parent of the relationship
 	 */
 	public void addChildParent(Object child, Object parent);
 
 	/**
 	 * Remove child/parent relation from cache
 	 *
 	 * @param child The child to be removed.
 	 */
 	public void removeChildParent(Object child);
 
 	/**
 	 * Register keys inserted during the current transaction
 	 *
 	 * @param persister The entity persister
 	 * @param id The id
 	 */
 	public void registerInsertedKey(EntityPersister persister, Serializable id);
 
 	/**
 	 * Allows callers to check to see if the identified entity was inserted during the current transaction.
 	 *
 	 * @param persister The entity persister
 	 * @param id The id
 	 *
 	 * @return True if inserted during this transaction, false otherwise.
 	 */
 	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id);
 
-	public void loadedStateUpdatedNotification(EntityEntry entityEntry, Object[] state);
-	
-	public void loadedStateInsertedNotification(EntityEntry entityEntry, Object[] state);
-	
-	public void loadedStateDeletedNotification(EntityEntry entityEntry, Object[] deletedState);
+	/**
+	 * Callback used to signal that loaded entity state has changed.
+	 *
+	 * @param entityEntry The entry of the entity that has changed.
+	 * @param state The new state.
+	 */
+	public void entityStateUpdatedNotification(EntityEntry entityEntry, Object[] state);
+
+	/**
+	 * Callback used to signal that entity state has been inserted.
+	 *
+	 * @param entityEntry The entry of the inserted entity
+	 * @param state The new state
+	 */
+	public void entityStateInsertedNotification(EntityEntry entityEntry, Object[] state);
+
+	/**
+	 * Callback used to signal that entity state has been deleted.
+	 *
+	 * @param entityEntry The entry of the inserted entity
+	 * @param deletedState The state of the entity at the time of deletion
+	 */
+	public void entityStateDeletedNotification(EntityEntry entityEntry, Object[] deletedState);
 
 	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk);
 
 	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalId);
 
-	/**
-	 * The type of action the cache call is originating from
-	 */
-	public enum CachedNaturalIdValueSource {
-		LOAD,
-		INSERT,
-		UPDATE
-	}
-	
 	public void cacheNaturalIdResolution(EntityPersister persister, Serializable pk, Object[] naturalId, CachedNaturalIdValueSource valueSource);
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
index 91e11fc854..8d95951acb 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
@@ -1,154 +1,154 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
-import org.hibernate.engine.spi.PersistenceContext.CachedNaturalIdValueSource;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 public class DefaultResolveNaturalIdEventListener
 		extends AbstractLockUpgradeEventListener
 		implements ResolveNaturalIdEventListener {
 
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			DefaultResolveNaturalIdEventListener.class.getName()
 	);
 
 	@Override
 	public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
 		final Serializable entityId = resolveNaturalId( event );
 		event.setEntityId( entityId );
 	}
 
 	/**
 	 * Coordinates the efforts to load a given entity. First, an attempt is
 	 * made to load the entity from the session-level cache. If not found there,
 	 * an attempt is made to locate it in second-level cache. Lastly, an
 	 * attempt is made to load it directly from the datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The loaded entity, or null.
 	 */
 	protected Serializable resolveNaturalId(final ResolveNaturalIdEvent event) {
 		final EntityPersister persister = event.getEntityPersister();
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled )
 			LOG.tracev( "Attempting to resolve: {0}",
 					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		Serializable entityId = resolveFromCache( event );
 		if ( entityId != null ) {
 			if ( traceEnabled )
 				LOG.tracev( "Resolved object in cache: {0}",
 						MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 			return entityId;
 		}
 
 		if ( traceEnabled )
 			LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		return loadFromDatasource( event );
 	}
 
 	/**
 	 * Attempts to resolve the entity id corresponding to the event's natural id values from the session
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The entity from the cache, or null.
 	 */
 	protected Serializable resolveFromCache(final ResolveNaturalIdEvent event) {
 		return event.getSession().getPersistenceContext().findCachedNaturalIdResolution(
 				event.getEntityPersister(),
 				event.getOrderedNaturalIdValues()
 		);
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The object loaded from the datasource, or null if not found.
 	 */
 	protected Serializable loadFromDatasource(final ResolveNaturalIdEvent event) {
 		final SessionFactoryImplementor factory = event.getSession().getFactory();
 		final boolean stats = factory.getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.currentTimeMillis();
 		}
 		
 		final Serializable pk = event.getEntityPersister().loadEntityIdByNaturalId(
 				event.getOrderedNaturalIdValues(),
 				event.getLockOptions(),
 				event.getSession()
 		);
 		
 		if ( stats ) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = event.getEntityPersister().getNaturalIdCacheAccessStrategy();
 			final String regionName = naturalIdCacheAccessStrategy == null ? null : naturalIdCacheAccessStrategy.getRegion().getName();
 			
 			factory.getStatisticsImplementor().naturalIdQueryExecuted(
 					regionName,
 					System.currentTimeMillis() - startTime );
 		}
 		
 		//PK can be null if the entity doesn't exist
 		if (pk != null) {
 			event.getSession().getPersistenceContext().cacheNaturalIdResolution(
 					event.getEntityPersister(),
 					pk,
 					event.getOrderedNaturalIdValues(),
 					CachedNaturalIdValueSource.LOAD
 			);
 		}
 		
 		return pk;
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Building.java b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/Building.java
similarity index 66%
rename from hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Building.java
rename to hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/Building.java
index c10e2b692f..e5cc8880a4 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/Building.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/Building.java
@@ -1,120 +1,142 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+
 //$Id$
-package org.hibernate.test.annotations.naturalid;
+package org.hibernate.test.naturalid.immutableentity;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
-import javax.persistence.ManyToOne;
 
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.NaturalId;
 import org.hibernate.annotations.NaturalIdCache;
 
 /**
  * @author Eric Dalquist
- * @see https://hibernate.onjira.com/browse/HHH-7085
  */
 @Entity
 @Immutable
 @NaturalIdCache
 public class Building {
 	@Id
 	@GeneratedValue
 	private Integer id;
 	
 	private String name;
 	
 	@NaturalId
 	private String address;
 	@NaturalId
 	private String city;
 	@NaturalId
 	private String state;
 
 
 	public Integer getId() {
 		return id;
 	}
 
 	public void setId(Integer id) {
 		this.id = id;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public String getCity() {
 		return city;
 	}
 
 	public void setCity(String city) {
 		this.city = city;
 	}
 
 	public String getState() {
 		return state;
 	}
 
 	public void setState(String state) {
 		this.state = state;
 	}
 
 	public String getAddress() {
 		return address;
 	}
 
 	public void setAddress(String address) {
 		this.address = address;
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ( ( address == null ) ? 0 : address.hashCode() );
 		result = prime * result + ( ( city == null ) ? 0 : city.hashCode() );
 		result = prime * result + ( ( state == null ) ? 0 : state.hashCode() );
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object obj) {
 		if ( this == obj )
 			return true;
 		if ( obj == null )
 			return false;
 		if ( getClass() != obj.getClass() )
 			return false;
 		Building other = (Building) obj;
 		if ( address == null ) {
 			if ( other.address != null )
 				return false;
 		}
 		else if ( !address.equals( other.address ) )
 			return false;
 		if ( city == null ) {
 			if ( other.city != null )
 				return false;
 		}
 		else if ( !city.equals( other.city ) )
 			return false;
 		if ( state == null ) {
 			if ( other.state != null )
 				return false;
 		}
 		else if ( !state.equals( other.state ) )
 			return false;
 		return true;
 	}
 
 	@Override
 	public String toString() {
 		return "Building [id=" + id + ", name=" + name + ", address=" + address + ", city=" + city + ", state=" + state
 				+ "]";
 	}
 	
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableEntityNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java
similarity index 97%
rename from hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableEntityNaturalIdTest.java
rename to hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java
index ea0cd572b1..5ba5128840 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/ImmutableEntityNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java
@@ -1,177 +1,179 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.test.annotations.naturalid;
+package org.hibernate.test.naturalid.immutableentity;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
+
+import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 /**
  * Test case for NaturalId annotation on an {@link Immutable} entity
  *
  * @author Eric Dalquist
- * @see https://hibernate.onjira.com/browse/HHH-7085
  */
 @SuppressWarnings("unchecked")
+@TestForIssue( jiraKey = "HHH-7085" )
 public class ImmutableEntityNaturalIdTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testMappingProperties() {
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				Building.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertEquals( "Wrong number of elements", 3, propertiesIndex.length );
 	}
 
 	@Test
 	public void testImmutableNaturalIdLifecycle() {
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 
 		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be empty", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be empty", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be empty", 0, stats.getNaturalIdQueryExecutionCount() );
 		
 		Building b1 = new Building();
 		b1.setName( "Computer Science" );
 		b1.setAddress( "1210 W. Dayton St." );
 		b1.setCity( "Madison" );
 		b1.setState( "WI" );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( b1 );
 		tx.commit();
 		s.close();
 
 		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be empty", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after insert", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be empty", 0, stats.getNaturalIdQueryExecutionCount() );
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		
 		//Clear caches and reset cache stats
 		s.getSessionFactory().getCache().evictNaturalIdRegions();
 		stats.clear();
 		
 		NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Building.class );
 		naturalIdLoader.using( "address", "1210 W. Dayton St." ).using( "city", "Madison" ).using( "state", "WI" );
 
 		// first query
 		Building building = (Building) naturalIdLoader.load();
 		assertNotNull( building );
 		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after first query", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after first query", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after first query", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 		
 		//Try two, should be a cache hit
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		naturalIdLoader = s.byNaturalId( Building.class );
 		naturalIdLoader.using( "address", "1210 W. Dayton St." ).using( "city", "Madison" ).using( "state", "WI" );
 
 		// second query
 		building = (Building) naturalIdLoader.load();
 		assertNotNull( building );
 		assertEquals( "Cache hits should be one after second query", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after second query", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after second query", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after second query", 1, stats.getNaturalIdQueryExecutionCount() );
 		
 		// Try Deleting
 		s.delete( building );
 		
 		// third query
 		building = (Building) naturalIdLoader.load();
 		assertNull( building );
 		assertEquals( "Cache hits should be one after second query", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after second query", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after second query", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after second query", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.commit();
 		s.close();
 		
 		//Try three, should be db lookup and miss
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		naturalIdLoader = s.byNaturalId( Building.class );
 		naturalIdLoader.using( "address", "1210 W. Dayton St." ).using( "city", "Madison" ).using( "state", "WI" );
 
 		// second query
 		building = (Building) naturalIdLoader.load();
 		assertNull( building );
 		assertEquals( "Cache hits should be one after third query", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after third query", 2, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after third query", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after third query", 2, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 		
 		
 		
 		
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Building.class
 		};
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
